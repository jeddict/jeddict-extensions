/**
 * Copyright [2016] Gaurav Gupta
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jcode.docker.generator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;
import org.jcode.infra.DatabaseDriver;
import org.jcode.infra.DatabaseType;
import org.jcode.infra.ServerFamily;
import static org.jcode.infra.ServerFamily.PAYARA_FAMILY;
import static org.jcode.infra.ServerFamily.WILDFLY_FAMILY;
import org.jcode.infra.ServerType;
import static org.jcode.infra.ServerType.NONE;
import static org.jcode.infra.ServerType.PAYARA;
import static org.jcode.infra.ServerType.WILDFLY;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.jcode.console.Console;
import static org.netbeans.jcode.console.Console.BOLD;
import static org.netbeans.jcode.console.Console.FG_RED;
import static org.netbeans.jcode.console.Console.UNDERLINE;
import static org.netbeans.jcode.core.util.FileUtil.expandTemplate;
import org.netbeans.jcode.core.util.POMManager;
import org.netbeans.jcode.core.util.PersistenceUtil;
import static org.netbeans.jcode.core.util.PersistenceUtil.addProperty;
import static org.netbeans.jcode.core.util.PersistenceUtil.removeProperty;
import static org.netbeans.jcode.core.util.ProjectHelper.getDockerDirectory;
import static org.netbeans.jcode.jpa.JPAConstants.JAVA_DATASOURCE_PREFIX;
import static org.netbeans.jcode.jpa.JPAConstants.JAVA_GLOBAL_DATASOURCE_PREFIX;
import static org.netbeans.jcode.jpa.JPAConstants.JDBC_DRIVER;
import static org.netbeans.jcode.jpa.JPAConstants.JDBC_PASSWORD;
import static org.netbeans.jcode.jpa.JPAConstants.JDBC_URL;
import static org.netbeans.jcode.jpa.JPAConstants.JDBC_USER;
import org.netbeans.jcode.jpa.PersistenceProviderType;
import org.netbeans.jcode.layer.ConfigData;
import org.netbeans.jcode.layer.Generator;
import org.netbeans.jcode.layer.Technology;
import org.netbeans.jcode.stack.config.data.ApplicationConfigData;
import org.netbeans.jcode.task.progress.ProgressHandler;
import org.netbeans.jcode.web.dd.util.WebDDUtil;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.modules.j2ee.persistence.dd.common.PersistenceUnit;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * Generates Docker image.
 *
 * @author Gaurav Gupta
 */
@ServiceProvider(service = Generator.class)
@Technology(label = "Infra", panel = DockerConfigPanel.class, entityGenerator = false, tabIndex = 1)
public class DockerGenerator implements Generator {

    private static final String TEMPLATE = "org/jcode/docker/template/";
    private static final String DOCKER_MACHINE_PROPERTY = "docker.machine";
    private static final String BINARY = "binary";
    private static final String DOCKER_IMAGE = "docker.image";
    private static final String DB_NAME = "db.name";
    private static final String DB_USER = "db.user";
    private static final String DB_PASSWORD = "db.password";
    private static final String DB_SVC = "db.svc";
//    private static final String DB_PORT = "db.port";
    private static final String DOCKER_PROFILE = "docker";
    private static final String DEVELOPMENT_PROFILE = "dev";
    private static final String DOCKER_FILE = "DockerFile";
    private static final String DOCKER_COMPOSE = "docker-compose.yml";

    @ConfigData
    protected DockerConfigData config;

    @ConfigData
    protected Project project;

    @ConfigData
    protected SourceGroup source;

    @ConfigData
    protected ProgressHandler handler;

    @ConfigData
    protected EntityMappings entityMapping;

    @ConfigData
    protected ApplicationConfigData appConfigData;

//    private boolean setupDataSourceLocally;
    protected PersistenceProviderType persistenceProviderType;
    protected String applicationName;

    @Override
    public void execute() throws IOException {
        if (!appConfigData.isCompleteApplication()) {
            return;
        }
//        setupDataSourceLocally = !(config.isDockerEnable() && (config.getServerType() == PAYARA || config.getServerType() == WILDFLY));
        manageServerSettingGeneration();
        manageDBSettingGeneration();
        manageDockerGeneration();
    }

    private void manageDockerGeneration() throws IOException {
        if (config.isDockerEnable()) {
            handler.progress(Console.wrap(DockerGenerator.class, "MSG_Progress_Now_Generating", FG_RED, BOLD, UNDERLINE));

            FileObject targetFolder = getDockerDirectory(source);
            Map<String, Object> params = getParams();
            handler.progress(expandTemplate(TEMPLATE + config.getServerType().getTemplate(), targetFolder, DOCKER_FILE, params));
            handler.progress(expandTemplate(TEMPLATE + DOCKER_COMPOSE + ".ftl", targetFolder, DOCKER_COMPOSE, params));
            if (POMManager.isMavenProject(project)) {
                POMManager pomManager = new POMManager(TEMPLATE + "fabric8io/pom/_pom.xml", project);
                pomManager.commit();
                appConfigData.addProfile(DOCKER_PROFILE);
                handler.info("Docker", "Use \"docker\" profile to create and run Docker image");

                Properties properties = new Properties();
//                    handler.warning(NbBundle.getMessage(DockerGenerator.class, "TITLE_Docker_Machine_Not_Found"),
//                            NbBundle.getMessage(DockerGenerator.class, "MSG_Docker_Machine_Not_Found"));
//                    properties.put(DOCKER_MACHINE_PROPERTY, "local");
                if (!StringUtils.isBlank(config.getDockerMachine())) {
                    pomManager = new POMManager(TEMPLATE + "fabric8io/pom/docker_machine_pom.xml", project);
                    pomManager.commit();
                    properties.put(DOCKER_MACHINE_PROPERTY, config.getDockerMachine());
                } 
                properties.put(BINARY, config.getServerType().getBinary());
                properties.put(DOCKER_IMAGE, config.getDockerNamespace()
                        + "/" + config.getDockerRepository() + ":${project.version}");
                pomManager.addProperties(DOCKER_PROFILE, properties);
            }
        }
    }

    private void manageDBSettingGeneration() throws IOException {
        updatePersistenceXml();
        hibernateUpdate();
        addDatabaseDriverDependency();
        if (config.isDbInfoExist()) {
            addDatabaseProperties();
//            if (setupDataSourceLocally) {
                generateDataSourceDD();
                if (POMManager.isMavenProject(project)) {
                    POMManager pomManager = new POMManager(TEMPLATE + "web/xml/filter/pom/_pom.xml", project);
                    pomManager.commit();
                }
//            }
        }
    }

    private void updatePersistenceXml() {
        String puName = entityMapping.getPersistenceUnitName();
        Optional<PersistenceUnit> punitOptional = PersistenceUtil.getPersistenceUnit(project, puName);
        if (punitOptional.isPresent()) {
            PersistenceUnit punit = punitOptional.get();
            punit.setTransactionType("JTA");
            removeProperty(punit, JDBC_URL);
            removeProperty(punit, JDBC_PASSWORD);
            removeProperty(punit, JDBC_DRIVER);
            removeProperty(punit, JDBC_USER);
            punit.setJtaDataSource(config.isDbInfoExist() ? getJNDI(config.getServerType(), config.getDataSource()) : null);
            punit.setProvider(getPersistenceProvider(config.getServerType(), entityMapping, punit.getProvider()));
            PersistenceUtil.updatePersistenceUnit(project, punit);
        }
    }

    private void hibernateUpdate() {
        if (config.getServerType().getFamily() == ServerFamily.PAYARA_FAMILY
                && entityMapping.getPersistenceProviderType() == PersistenceProviderType.HIBERNATE) {
            String puName = entityMapping.getPersistenceUnitName();
            Optional<PersistenceUnit> punitOptional = PersistenceUtil.getPersistenceUnit(project, puName);
            if (punitOptional.isPresent()) {
                PersistenceUnit punit = punitOptional.get();
                addProperty(punit, "hibernate.dialect", getHibernateDialect());
//                addProperty(punit, "hibernate.hbm2ddl.auto", "create");
                addProperty(punit, "hibernate.show_sql", "true");
                addProperty(punit, "hibernate.transaction.jta.platform", "org.hibernate.service.jta.platform.internal.SunOneJtaPlatform");
                PersistenceUtil.updatePersistenceUnit(project, punit);
            }
            if (POMManager.isMavenProject(project)) {
//                POMManager pomManager = new POMManager(TEMPLATE + "persistence/provider/pom/" + config.getServerType().name() + "_"+persistenceProviderType.name() + ".xml", project);
                POMManager pomManager = new POMManager(TEMPLATE + "persistence/provider/pom/PAYARA_HIBERNATE.xml", project);
                pomManager.commit();
            }
        } else if (config.getServerType().getFamily() == ServerFamily.WILDFLY_FAMILY
                && entityMapping.getPersistenceProviderType() == PersistenceProviderType.ECLIPSELINK) {
            if (POMManager.isMavenProject(project)) {
                POMManager pomManager = new POMManager(TEMPLATE + "persistence/provider/pom/WILDFLY_ECLIPSELINK.xml", project);
                pomManager.commit();
            }
        }
    }

    private String getHibernateDialect() {
        if (config.getDatabaseType() != null) {
            switch (config.getDatabaseType()) {
                case MYSQL:
                    return "org.hibernate.dialect.MySQL5Dialect";
                case POSTGRESQL:
                    return "org.hibernate.dialect.PostgreSQLDialect";
                case MARIADB:
                    return "org.hibernate.dialect.MariaDBDialect";
                case DERBY:
                    return "org.hibernate.dialect.DB2Dialect";
                case H2:
                    return "org.hibernate.dialect.H2Dialect";
                default:
                    break;
            }
        }
        throw new IllegalStateException("DB type not supported");
    }

    private String getPersistenceProvider(ServerType server, EntityMappings entityMappings, String existingProvider) {
        if (persistenceProviderType == null) {
            if (entityMappings.getPersistenceProviderType() != null) {
                persistenceProviderType = entityMappings.getPersistenceProviderType();
            } else if (server != NONE || server != null) {
                persistenceProviderType = server.getPersistenceProviderType();
            } else {
                return existingProvider;
            }
        }
        return persistenceProviderType.getProviderClass();
    }

    private String getJNDI(ServerType server, String dataSource) {
        if (server.getFamily() == WILDFLY_FAMILY) {
            return JAVA_DATASOURCE_PREFIX + "jdbc/" + dataSource;
//        } else if (server.getFamily() == PAYARA_FAMILY) {
//            if (setupDataSourceLocally) {
//                return JAVA_GLOBAL_DATASOURCE_PREFIX + "jdbc/" + dataSource;
//            } else {
//                return "jdbc/" + dataSource;
//            }
        } else {
            return JAVA_GLOBAL_DATASOURCE_PREFIX + "jdbc/" + dataSource;
        }
    }

    private void manageServerSettingGeneration() {
        if (POMManager.isMavenProject(project)) {
            POMManager pomManager = new POMManager(TEMPLATE + "profile/dev/pom/_pom.xml", project);
            pomManager.fixDistributionProperties();
            pomManager.commit();

            if (config.getServerType() == ServerType.PAYARA_MICRO) {
                pomManager = new POMManager(TEMPLATE + "payara/micro/pom/_pom.xml", project);
                pomManager.commit();
            } else if (config.getServerType() == ServerType.WILDFLY_SWARM) {
                pomManager = new POMManager(TEMPLATE + "wildfly/swarm/pom/_pom.xml", project);
                pomManager.commit();
            }

        } else {
            handler.warning(NbBundle.getMessage(DockerGenerator.class, "TITLE_Maven_Project_Not_Found"),
                    NbBundle.getMessage(DockerGenerator.class, "MSG_Maven_Project_Not_Found"));
        }

    }

    private void addDatabaseDriverDependency() {
        if (POMManager.isMavenProject(project)) {
            ServerType serverType = config.getServerType();
            DatabaseType databaseType = config.getDatabaseType();
            boolean addDependency = (config.isDbInfoExist() && serverType.getEmbeddedDB() != databaseType)
                    || (serverType.getEmbeddedDB() == databaseType && serverType.isEmbeddedDBDriverRequired());
            if (databaseType.getDriver() != null && addDependency) {
                POMManager pomManager = new POMManager(project);
                DatabaseDriver driver = databaseType.getDriver();
                String versionRef = "version." + driver.getGroupId();
                pomManager.registerDependency(driver.getGroupId(), driver.getArtifactId(), "${" + versionRef + '}', null, null, null);
                Properties properties = new Properties();
                properties.put(versionRef, driver.getVersion());
                pomManager.addProperties(properties);
                pomManager.commit();
            }
        }
    }

    private void addDatabaseProperties() {
        if (POMManager.isMavenProject(project)) {
            POMManager pomManager = new POMManager(project);
            Properties properties = new Properties();
            properties.put(DB_USER, config.getDbUserName());
            properties.put(DB_PASSWORD, config.getDbPassword());
            properties.put(DB_NAME, config.getDbName());
            properties.put(DB_SVC, getDBService());
//            properties.put(DB_PORT, config.getDbPort());
            pomManager.addProperties(DEVELOPMENT_PROFILE, properties);
            pomManager.commit();
            appConfigData.addProfile(DEVELOPMENT_PROFILE);
        }
    }

    private void generateDataSourceDD() throws IOException {
        handler.progress("web.xml <data-source>");
        Map<String, Object> params = new HashMap<>(getParams());
        params.put("JNDI", getJNDI(config.getServerType(), config.getDataSource()));
        params.put("DRIVER_CLASS", config.getDatabaseType().getDriver().getClassName());
        WebDDUtil.createDD(project, "/org/jcode/docker/template/web/xml/datasource/_web.xml.ftl", params);
    }

    protected Map<String, Object> getParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("DB_USER", config.getDbUserName());
        params.put("DB_PASSWORD", config.getDbPassword());
        params.put("DB_NAME", config.getDbName());
        params.put("DB_SVC", getDBService());
        params.put("DB_PORT", config.getDbPort());
        params.put("DB_VERSION", config.getDatabaseVersion());
        params.put("DB_TYPE", config.getDatabaseType().getDockerImage());
        params.put("DATASOURCE", config.getDataSource());
        params.put("SERVER_TYPE", config.getServerType().name());
        return params;
    }

    protected String getDBService() {
        if (config.getDbHost() != null) {
            return config.getDbHost();
        } else {
            return getApplicationName() + "-" + config.getDatabaseType().name().toLowerCase();
        }
    }

    protected String getApplicationName() {
        if (applicationName == null) {
            applicationName = config.getDockerRepository()
                    .replace("${project.artifactId}", getPOMManager().getArtifactId());
        }
        return applicationName;
    }

    private POMManager projectPOMManager;

    protected POMManager getPOMManager() {
        if (projectPOMManager == null) {
            projectPOMManager = new POMManager(project, true);
        }
        return projectPOMManager;
    }

}
