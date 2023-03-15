/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.docker.generator;

import io.github.jeddict.jcode.ApplicationConfigData;
import io.github.jeddict.jcode.DatabaseDriver;
import io.github.jeddict.jcode.DatabaseType;
import io.github.jeddict.jcode.Generator;
import static io.github.jeddict.jcode.JPAConstants.JDBC_DRIVER;
import static io.github.jeddict.jcode.JPAConstants.JDBC_PASSWORD;
import static io.github.jeddict.jcode.JPAConstants.JDBC_URL;
import static io.github.jeddict.jcode.JPAConstants.JDBC_USER;
import static io.github.jeddict.jcode.RegistryType.CONSUL;
import io.github.jeddict.jcode.annotation.ConfigData;
import io.github.jeddict.jcode.annotation.Runtime;
import io.github.jeddict.jcode.annotation.Technology;
import io.github.jeddict.jcode.console.Console;
import static io.github.jeddict.jcode.console.Console.*;
import io.github.jeddict.jcode.generator.ApplicationGenerator;
import io.github.jeddict.jcode.jpa.PersistenceProviderType;
import io.github.jeddict.jcode.task.progress.ProgressHandler;
import static io.github.jeddict.jcode.util.FileUtil.expandTemplate;
import io.github.jeddict.jcode.util.POMManager;
import io.github.jeddict.jcode.util.PersistenceUtil;
import static io.github.jeddict.jcode.util.PersistenceUtil.removeProperty;
import static io.github.jeddict.jcode.util.ProjectHelper.getDockerDirectory;
import io.github.jeddict.jpa.spec.EntityMappings;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import io.github.jeddict.util.StringUtils;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
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
@Technology(
        label = "Infra", 
        panel = DockerConfigPanel.class, 
        entityGenerator = false, 
        tabIndex = 1
)
public class DockerGenerator implements Generator {

    private static final String TEMPLATE = "io/github/jeddict/docker/template/";
    private static final String DOCKER_MACHINE_PROPERTY = "docker.machine";
    private static final String DOCKER_URL_PROPERTY = "docker.url";
    private static final String DOCKER_CERT_PATH = "docker.certPath";
    private static final String BINARY = "binary";
    private static final String DOCKER_IMAGE = "docker.image";
    private static final String DB_NAME = "db.name";
    private static final String DB_USER = "db.user";
    private static final String DB_PASSWORD = "db.password";
    private static final String DB_SVC = "db.svc";
    private static final String DB_PORT = "db.port";
    
    private static final String WEB_SVC = "web.svc";
    private static final String WEB_HOST = "web.host";
    private static final String WEB_PORT = "web.port";
    private static final String REGISTRY_URL = "registry.url";

    private static final String DOCKER_PROFILE = "docker";
    private static final String DEV_PROFILE = "dev";
    private static final String PROD_PROFILE = "prod";
    private static final String DOCKER_FILE = "DockerFile";
    private static final String DOCKER_COMPOSE = "docker-compose.yml";

    @ConfigData
    protected DockerConfigData config;

    @ConfigData
    protected ProgressHandler handler;

    @ConfigData
    protected EntityMappings entityMapping;

    @ConfigData
    protected ApplicationConfigData appConfigData;

    protected PersistenceProviderType persistenceProviderType;
    protected String applicationName;

    protected Project project;
    protected SourceGroup source;
    
    @Override
    public void preExecute() {
        project = appConfigData.isGateway() ? appConfigData.getGatewayProject() : appConfigData.getTargetProject();
        source = appConfigData.isGateway() ? appConfigData.getGatewaySourceGroup() : appConfigData.getTargetSourceGroup();
        ApplicationGenerator.inject(
                config.getRuntimeProvider(),
                appConfigData,
                Collections.EMPTY_MAP,
                handler,
                project,
                source
        );
        config.getRuntimeProvider().preExecute();
    }
    
    @Override
    public void execute() throws IOException {
        if (!appConfigData.isCompleteApplication()) {
            return;
        }
        manageServerSettingGeneration();
        manageDBSettingGeneration();
        manageDockerGeneration();
    }

    private void manageDockerGeneration() throws IOException {
        if (config.isDockerEnable()) {
            handler.progress(Console.wrap(DockerGenerator.class, "MSG_Progress_Now_Generating", FG_DARK_RED, BOLD));

            FileObject targetFolder = getDockerDirectory(source);
            Map<String, Object> params = getParams();
            handler.progress(expandTemplate(config.getRuntimeProvider().getDockerTemplate(), targetFolder, DOCKER_FILE, params));
            handler.progress(expandTemplate(TEMPLATE + DOCKER_COMPOSE + ".ftl", targetFolder, DOCKER_COMPOSE, params));
            if (POMManager.isMavenProject(project)) {
                POMManager pomManager;
                if ( config.getDockerCertPath() != null && !StringUtils.isBlank(config.getDockerCertPath())) {
                    pomManager = new POMManager(project, TEMPLATE + "fabric8io/pom/docker_machine_pom_cert.xml");
                } else {
                    pomManager = new POMManager(project, TEMPLATE + "fabric8io/pom/_pom.xml");
                }
                pomManager.commit();
                appConfigData.getEnvironment("dev")
                        .addProfile(DOCKER_PROFILE);
                appConfigData.getEnvironment("prod")
                        .addProfile(DOCKER_PROFILE);
                handler.info("Docker", "Use \"docker\" profile to create and run Docker image");

                Properties properties = new Properties();
//                    handler.warning(NbBundle.getMessage(DockerGenerator.class, "TITLE_Docker_Machine_Not_Found"),
//                            NbBundle.getMessage(DockerGenerator.class, "MSG_Docker_Machine_Not_Found"));
//                    properties.put(DOCKER_MACHINE_PROPERTY, "local");
                if (!StringUtils.isBlank(config.getDockerMachine())) {
                    pomManager = new POMManager(project, TEMPLATE + "fabric8io/pom/docker_machine_pom.xml");
                    pomManager.commit();
                    properties.put(DOCKER_MACHINE_PROPERTY, config.getDockerMachine());
                    properties.put(DOCKER_URL_PROPERTY, config.getDockerUrl());
                    if ( config.getDockerCertPath() != null && !StringUtils.isBlank(config.getDockerCertPath())) {
                        properties.put(DOCKER_CERT_PATH, config.getDockerCertPath());
                    }
                } 
                properties.put(BINARY, config.getRuntimeProvider().getBuildName());
                properties.put(DOCKER_IMAGE, config.getDockerNamespace()
                        + "/" + config.getDockerRepository() + ":${project.version}");
                pomManager.addProperties(DOCKER_PROFILE, properties);
            }
        }
    }

    private void manageDBSettingGeneration() throws IOException {
        updatePersistenceXml();
        config.getRuntimeProvider().updatePersistenceProvider(config.getDatabaseType());
        addDatabaseDriverDependency();
        if (config.isDbInfoExist()) {
            addDatabaseProperties();
//            if (setupDataSourceLocally) {
                generateDataSourceDD();
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
    
            punit.setJtaDataSource(config.isDbInfoExist() ? config.getRuntimeProvider().getJNDIPrefix() + config.getDataSource() : null);
            punit.setProvider(getPersistenceProvider(config.getRuntime(), entityMapping, punit.getProvider()));
            PersistenceUtil.updatePersistenceUnit(project, punit);
        }
    }

    private String getPersistenceProvider(Runtime runtime, EntityMappings entityMappings, String existingProvider) {
        if (persistenceProviderType == null) {
            if (entityMappings.getPersistenceProviderType() != null) {
                persistenceProviderType = entityMappings.getPersistenceProviderType();
            } else if (runtime != null && !runtime.name().isEmpty()) {
                persistenceProviderType = runtime.persistenceProvider();
            } else {
                return existingProvider;
            }
        }
        return persistenceProviderType.getProviderClass();
    }
    
    private void manageServerSettingGeneration() {
        if (POMManager.isMavenProject(project)) {
            POMManager pomManager = new POMManager(project, TEMPLATE + "profile/dev/pom/_pom.xml");
            pomManager.fixDistributionProperties();
            pomManager.commit();
            
            config.getRuntimeProvider().addDependency(config.isDockerEnable());
            addWebProperties();
        } else {
            handler.warning(NbBundle.getMessage(DockerGenerator.class, "TITLE_Maven_Project_Not_Found"),
                    NbBundle.getMessage(DockerGenerator.class, "MSG_Maven_Project_Not_Found"));
        }

    }

    private void addDatabaseDriverDependency() {
        if (POMManager.isMavenProject(project)) {
            Runtime runtime = config.getRuntime();
            DatabaseType databaseType = config.getDatabaseType();
            boolean addDependency = (config.isDbInfoExist() && runtime.embeddedDB() != databaseType)
                    || (runtime.embeddedDB() == databaseType && runtime.embeddedDBDriverRequired());
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
            properties.put(DB_PORT, config.getDbPort());
            pomManager.addProperties(DEV_PROFILE, properties);
            pomManager.commit();
        }
    }
    
    private void addWebProperties() {
        if (POMManager.isMavenProject(project)) {
            POMManager pomManager = new POMManager(project);
            Properties properties = new Properties();
            if (config.isDockerEnable()) {
                properties.put(WEB_SVC, getWebService());
            }
            
            String registryPort = appConfigData.getRegistryType() == CONSUL ? "8500" : "8091";
            
            appConfigData.getEnvironment("dev")
                    .addConfig("contextPath", appConfigData.getTargetContextPath());
            appConfigData.getEnvironment("prod")
                    .addConfig("contextPath", appConfigData.getTargetContextPath());
            if (appConfigData.isMicroservice()){
                properties.put(WEB_HOST, "http://localhost");
                properties.put(WEB_PORT, (8080 + new SecureRandom().nextInt(1000)) + "");
                appConfigData.getEnvironment("dev")
                        .addConfig("registryURL", "http://localhost:" + registryPort)
                        .addBuildProperty(WEB_HOST, "<container host>")
                        .addBuildProperty(WEB_PORT, "<container port>");
                appConfigData.getEnvironment("prod")
                        .addConfig("registryURL", "http://localhost:" + registryPort)
                        .addBuildProperty(WEB_HOST, "<container host>")
                        .addBuildProperty(WEB_PORT, "<container port>");
                handler.info("Service Registry",
                        Console.wrap(String.join(", ", WEB_HOST, WEB_PORT, REGISTRY_URL), FG_MAGENTA)
                        + " properties are required for Service Registry");
            } else if (appConfigData.isGateway()) {
                properties.put(WEB_PORT, "8080");//for docker
                appConfigData.getEnvironment("dev")
                        .addConfig("registryURL", "http://localhost:" + registryPort);
                appConfigData.getEnvironment("prod")
                        .addConfig("registryURL", "http://localhost:" + registryPort);
                handler.info("Service Discovery",
                        Console.wrap(REGISTRY_URL, FG_MAGENTA)
                        + " property is required for Service Discovery");
            }
            pomManager.addProperties(DEV_PROFILE, properties);
            pomManager.commit();
            appConfigData.getEnvironment("dev")
                    .addProfileAndActivate(DEV_PROFILE, project);
            appConfigData.getEnvironment("prod")
                    .addProfile(PROD_PROFILE);
        }
    }

    private void generateDataSourceDD() throws IOException {
        handler.progress("web.xml <data-source>");
        Map<String, Object> params = new HashMap<>(getParams());
        params.put("JNDI", config.getRuntimeProvider().getJNDIPrefix() + config.getDataSource());
        params.put("DRIVER_CLASS", config.getDatabaseType().getDriver().getClassName());
        appConfigData.addWebDescriptorContent(
                expandTemplate("/io/github/jeddict/docker/template/datasource/web/descriptor/_web.xml.ftl", params), 
                appConfigData.getTargetProject());
    }

    protected Map<String, Object> getParams() {
        Map<String, Object> params = new HashMap<>();
//        params.put("DB_USER", config.getDbUserName());
//        params.put("DB_PASSWORD", config.getDbPassword());
//        params.put("DB_NAME", config.getDbName());
        params.put("DB_SVC", getDBService());
        params.put("DB_PORT", config.getDbPort());
        params.put("DB_VERSION", config.getDatabaseVersion());
        params.put("DB_TYPE", config.getDatabaseType().getDockerImage());
//        params.put("DATASOURCE", config.getDataSource());
        params.put("SERVER_TYPE", config.getRuntime().name());
        return params;
    }

    protected String getDBService() {
        if (config.getDbHost() != null) {
            return config.getDbHost();
        } else {
            return getApplicationName() + "-" + config.getDatabaseType().name().toLowerCase();
        }
    }

    protected String getWebService() {
        return getApplicationName() + "-web";
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
    
    @Override
    public void postExecute() {
        config.getRuntimeProvider().postExecute();
    }
}






