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
import static org.jcode.docker.generator.ServerFamily.WILDFLY_FAMILY;
import static org.jcode.docker.generator.ServerType.NONE;
import static org.jcode.docker.generator.ServerType.PAYARA;
import static org.jcode.docker.generator.ServerType.PAYARA_MICRO;
import static org.jcode.docker.generator.ServerType.WILDFLY;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.jcode.console.Console;
import static org.netbeans.jcode.console.Console.BOLD;
import static org.netbeans.jcode.console.Console.FG_RED;
import static org.netbeans.jcode.console.Console.UNDERLINE;
import static org.netbeans.jcode.core.util.FileUtil.expandTemplate;
import org.netbeans.jcode.core.util.POMManager;
import org.netbeans.jcode.core.util.PersistenceUtil;
import static org.netbeans.jcode.core.util.PersistenceUtil.removeProperty;
import static org.netbeans.jcode.core.util.ProjectHelper.getDockerDirectory;
import static org.netbeans.jcode.jpa.JPAConstants.JAVA_DATASOURCE_PREFIX;
import static org.netbeans.jcode.jpa.JPAConstants.JAVA_GLOBAL_DATASOURCE_PREFIX;
import static org.netbeans.jcode.jpa.JPAConstants.JDBC_DRIVER;
import static org.netbeans.jcode.jpa.JPAConstants.JDBC_PASSWORD;
import static org.netbeans.jcode.jpa.JPAConstants.JDBC_URL;
import static org.netbeans.jcode.jpa.JPAConstants.JDBC_USER;
import org.netbeans.jcode.layer.ConfigData;
import org.netbeans.jcode.layer.Generator;
import org.netbeans.jcode.layer.Technology;
import org.netbeans.jcode.stack.config.data.ApplicationConfigData;
import org.netbeans.jcode.task.progress.ProgressHandler;
import org.netbeans.jcode.web.dd.util.WebDDUtil;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.openide.filesystems.FileObject;
import org.openide.util.lookup.ServiceProvider;
import org.netbeans.modules.j2ee.persistence.dd.common.PersistenceUnit;
import org.openide.util.NbBundle;

/**
 * Generates Docker image.
 *
 * @author Gaurav Gupta
 */
@ServiceProvider(service = Generator.class)
@Technology(label = "Infra", panel = DockerConfigPanel.class, index = 1)
public final class DockerGenerator implements Generator {

    private static final String TEMPLATE = "org/jcode/docker/template/";
    private static final String DOCKER_MACHINE_PROPERTY = "docker.machine";
    private static final String BINARY = "binary";
    private static final String DB_NAME = "db.name";
    private static final String DB_USER = "db.user";
    private static final String DB_PASSWORD = "db.password";
    private static final String DB_HOST = "db.host";
    private static final String DB_PORT = "db.port";
    private static final String DOCKER_PROFILE = "docker";

    @ConfigData
    private DockerConfigData dockerConfig;

    @ConfigData
    private Project project;

    @ConfigData
    private SourceGroup source;

    @ConfigData
    private ProgressHandler handler;

    @ConfigData
    private EntityMappings entityMapping;

    @ConfigData
    private ApplicationConfigData appConfigData;

    private static final String DOCKER_FILE = "DockerFile";
    private static final String DOCKER_COMPOSE = "docker-compose.yml";

    @Override
    public void execute() throws IOException {
        if (!appConfigData.isCompleteApplication()) {
            return;
        }
        if (dockerConfig.isDockerEnable()) {
            handler.progress(Console.wrap(DockerGenerator.class, "MSG_Progress_Now_Generating", FG_RED, BOLD, UNDERLINE));

            FileObject targetFolder = getDockerDirectory(source);
            Map<String, Object> params = new HashMap<>();
            params.put("docker", dockerConfig);
            
            String dockerFile = dockerConfig.getServerType().name();
            if (dockerConfig.getServerType() == ServerType.PAYARA_MICRO
                    || dockerConfig.getServerType() == ServerType.WILDFLY_SWARM) {
                dockerFile = "JAVA";
            }
            expandTemplate(TEMPLATE + "DockerFile_" + dockerFile + ".ftl", targetFolder, DOCKER_FILE, params);
            handler.progress(DOCKER_FILE);
            expandTemplate(TEMPLATE + "docker-compose.yml.ftl", targetFolder, DOCKER_COMPOSE, params);
            handler.progress(DOCKER_COMPOSE);
            addMavenDependencies();
        }

        boolean setupDataSourceLocally = !(dockerConfig.isDockerEnable() && (dockerConfig.getServerType() == PAYARA || dockerConfig.getServerType() == WILDFLY));
        
        if (dockerConfig.isDbInfoExist()) {
            addDatabaseProperties();
            if (setupDataSourceLocally) {
                generateDataSourceDD();
                if (POMManager.isMavenProject(project)) {
                    POMManager pomManager = new POMManager(TEMPLATE + "web/xml/filter/pom/_pom.xml", project);
                    pomManager.commit();
                }
                if (dockerConfig.getServerType() == ServerType.PAYARA_MICRO
                        || dockerConfig.getServerType() == ServerType.WILDFLY_SWARM) {
                    addDatabaseDriverDependency(dockerConfig.getDatabaseType());
                }
            }
        }
        updatePersistenceXml();
        
        if (POMManager.isMavenProject(project)) {
            if (dockerConfig.getServerType() == ServerType.PAYARA_MICRO) {
                POMManager pomManager = new POMManager(TEMPLATE + "payara/micro/pom/_pom.xml", project);
                pomManager.commit();
            } else if (dockerConfig.getServerType() == ServerType.WILDFLY_SWARM) {
                POMManager pomManager = new POMManager(TEMPLATE + "wildfly/swarm/pom/_pom.xml", project);
                pomManager.commit();
            }
        }
        
    }

    private void generateDataSourceDD() throws IOException {
        handler.progress("web.xml <data-source>");
        Map<String, Object> params = new HashMap<>();
        params.put("jndi", getJNDI(dockerConfig.getServerType(), dockerConfig.getDataSource()));
        params.put("driverClass", dockerConfig.getDatabaseType().getDriver().getClassName());
        WebDDUtil.createDD(project, "/org/jcode/docker/template/web/xml/datasource/_web.xml.ftl", params);
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
            punit.setJtaDataSource(dockerConfig.isDbInfoExist()? getJNDI(dockerConfig.getServerType(), dockerConfig.getDataSource()) : null);
            punit.setProvider(getProvider(dockerConfig.getServerType(), punit.getProvider()));
            PersistenceUtil.updatePersistenceUnit(project, punit);
        }
    }

    public static String getJNDI(ServerType server, String dataSource) {
        if (server.getFamily() == WILDFLY_FAMILY ) {
            return JAVA_DATASOURCE_PREFIX + "jdbc/" + dataSource;
        } else if (server == PAYARA_MICRO) {
            return JAVA_GLOBAL_DATASOURCE_PREFIX + "jdbc/" + dataSource;
        } else {
            return "jdbc/" + dataSource;
        }
    }

    public static String getProvider(ServerType server, String existingProvider) {
        if (server != NONE || server != null) {
            return server.getPersistenceProvider();
        } else {
            return existingProvider;
        }
    }

    private void addMavenDependencies() {
        if (POMManager.isMavenProject(project)) {
            POMManager pomManager = new POMManager(TEMPLATE + "fabric8io/pom/_pom.xml", project);
            pomManager.fixDistributionProperties();
            pomManager.commit();
            handler.info("Profile", "Use \"docker\" profile to create and run Docker image");

            Properties properties = new Properties();
            properties.put(DOCKER_MACHINE_PROPERTY, dockerConfig.getDockerMachine());
            properties.put(BINARY, dockerConfig.getServerType().getBinary());
            pomManager.addProperties(DOCKER_PROFILE, properties);

        } else {
            handler.warning(NbBundle.getMessage(DockerGenerator.class, "TITLE_Maven_Project_Not_Found"),
                    NbBundle.getMessage(DockerGenerator.class, "MSG_Maven_Project_Not_Found"));
        }

    }

    private void addDatabaseDriverDependency(DatabaseType databaseType) {
        if (POMManager.isMavenProject(project) && databaseType.getDriver() != null) {
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
    
    private void addDatabaseProperties() {
        if(POMManager.isMavenProject(project)) {
            POMManager pomManager = new POMManager(project);
            Properties properties = new Properties();
            properties.put(DB_NAME, dockerConfig.getDbName());
            properties.put(DB_USER, dockerConfig.getDbUserName());
            properties.put(DB_PASSWORD, dockerConfig.getDbPassword());
            properties.put(DB_HOST, "db");
            properties.put(DB_PORT, dockerConfig.getDatabaseType().getDefaultPort());
            pomManager.addProperties(properties);
            pomManager.commit();
        }
    }
}
