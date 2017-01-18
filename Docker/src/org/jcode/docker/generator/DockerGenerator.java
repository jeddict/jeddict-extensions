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
import static org.jcode.docker.generator.ServerType.NONE;
import static org.jcode.docker.generator.ServerType.PAYARA;
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
import static org.netbeans.jcode.jpa.JPAConstants.JBOSS_DATASOURCE_PREFIX;
import static org.netbeans.jcode.jpa.JPAConstants.JDBC_DRIVER;
import static org.netbeans.jcode.jpa.JPAConstants.JDBC_PASSWORD;
import static org.netbeans.jcode.jpa.JPAConstants.JDBC_URL;
import static org.netbeans.jcode.jpa.JPAConstants.JDBC_USER;
import org.netbeans.jcode.layer.ConfigData;
import org.netbeans.jcode.layer.Generator;
import org.netbeans.jcode.layer.Technology;
import org.netbeans.jcode.task.progress.ProgressHandler;
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
@Technology(label = "Docker", panel = DockerConfigPanel.class, index = 1)
public final class DockerGenerator implements Generator {

    private static final String TEMPLATE = "org/jcode/docker/template/";
    private static final String DOCKER_MACHINE_PROPERTY = "docker.machine";
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
    
    private static final String DOCKER_FILE = "DockerFile";
    private static final String DOCKER_COMPOSE = "docker-compose.yml";

    @Override
    public void execute() throws IOException {
        if (dockerConfig.isDockerEnable() && dockerConfig.getServerType() != NONE) {
            handler.progress(Console.wrap(DockerGenerator.class, "MSG_Progress_Now_Generating", FG_RED, BOLD, UNDERLINE));
            
//             handler.help(NbBundle.getMessage(DockerGenerator.class, "DOCKER_MACHINE_GUIDE_TITLE"),
//                    NbBundle.getMessage(DockerGenerator.class, "DOCKER_MACHINE_GUIDE"));
//             
//             handler.help(NbBundle.getMessage(DockerGenerator.class, "DOCKER_GUIDE_TITLE"),
//                    NbBundle.getMessage(DockerGenerator.class, "DOCKER_GUIDE"));
           
            FileObject targetFolder = getDockerDirectory(source);
            Map<String, Object> params = new HashMap<>();
            params.put("docker", dockerConfig);
            expandTemplate(TEMPLATE + "DockerFile_" + dockerConfig.getServerType().name() + ".ftl", targetFolder, DOCKER_FILE, params);
            handler.progress(DOCKER_FILE);
            expandTemplate(TEMPLATE + "docker-compose.yml.ftl", targetFolder, DOCKER_COMPOSE, params);
            handler.progress(DOCKER_COMPOSE);
            addMavenDependencies("fabric8io/pom/_pom.xml");
            
            updatePersistenceXml();
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
                punit.setJtaDataSource(getJNDI(dockerConfig.getServerType(), dockerConfig.getDataSource()));
                punit.setProvider(getProvider(dockerConfig.getServerType(), punit.getProvider()));
                PersistenceUtil.updatePersistenceUnit(project, punit);
            }
    }
    
    public static String getJNDI(ServerType server, String dataSource) {
        if (server == WILDFLY) {
            return JBOSS_DATASOURCE_PREFIX + "jdbc/" + dataSource;
        } else {
            return "jdbc/" + dataSource;
        }
    }
    
    public static String getProvider(ServerType server, String existingProvider) {
        if (server == WILDFLY) {
            return "org.hibernate.ejb.HibernatePersistence";
        } else if (server == PAYARA) {
            return "org.eclipse.persistence.jpa.PersistenceProvider";
        } else {
            return existingProvider;
        }
    }

    private void addMavenDependencies(String pom) {
        if (POMManager.isMavenProject(project)) {
            POMManager pomManager = new POMManager(TEMPLATE + pom, project);
            pomManager.fixDistributionProperties();
            pomManager.commit();
            Properties properties = new Properties();
            properties.put(DOCKER_MACHINE_PROPERTY, dockerConfig.getDockerMachine());
            pomManager.addProperties(DOCKER_PROFILE, properties);
        } else {
            handler.warning(NbBundle.getMessage(DockerGenerator.class, "TITLE_Maven_Project_Not_Found"),
                    NbBundle.getMessage(DockerGenerator.class, "MSG_Maven_Project_Not_Found"));
        }
    }
}
