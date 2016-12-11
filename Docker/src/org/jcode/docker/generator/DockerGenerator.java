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
import static org.jcode.docker.generator.ServerType.NONE;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.jcode.console.Console;
import static org.netbeans.jcode.console.Console.BOLD;
import static org.netbeans.jcode.console.Console.FG_RED;
import static org.netbeans.jcode.console.Console.UNDERLINE;
import static org.netbeans.jcode.core.util.FileUtil.expandTemplate;
import org.netbeans.jcode.core.util.POMManager;
import static org.netbeans.jcode.core.util.ProjectHelper.getDockerDirectory;
import org.netbeans.jcode.layer.ConfigData;
import org.netbeans.jcode.layer.Generator;
import org.netbeans.jcode.layer.Technology;
import org.netbeans.jcode.task.progress.ProgressHandler;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * Generates EJB facades for entity classes.
 *
 * @author Gaurav Gupta
 */
@ServiceProvider(service = Generator.class)
@Technology(label = "Docker", panel = DockerConfigPanel.class, index = 1)
public final class DockerGenerator implements Generator {

    private static final String TEMPLATE = "org/jcode/docker/template/";

    @ConfigData
    private DockerConfigData dockerConfig;

    @ConfigData
    private Project project;

    @ConfigData
    private SourceGroup source;

    @ConfigData
    private ProgressHandler handler;

    @Override
    public void execute() throws IOException {
        if (dockerConfig.getServerType() != NONE) {
            handler.progress(Console.wrap(DockerGenerator.class, "MSG_Progress_Now_Generating", FG_RED, BOLD, UNDERLINE));
            System.out.println("");
            FileObject targetFolder = getDockerDirectory(source);
            Map<String, Object> params = new HashMap<>();
            params.put("docker", dockerConfig);
            params.put("binary", "build.war");
            expandTemplate(TEMPLATE + "DockerFile_" + dockerConfig.getServerType().name() + ".ftl", targetFolder, "DockerFile", params);
            expandTemplate(TEMPLATE + "docker-compose_" + dockerConfig.getServerType().name() + ".yml.ftl", targetFolder, "docker-compose.yml", params);
            addMavenDependencies("fabric8io/pom/_pom.xml");
        }
    }

    private void addMavenDependencies(String pom) {
        if (POMManager.isMavenProject(project)) {
            POMManager pomManager = new POMManager(TEMPLATE + pom, project);
            pomManager.commit();
        } else {
            handler.warning(NbBundle.getMessage(DockerGenerator.class, "TITLE_Maven_Project_Not_Found"),
                    NbBundle.getMessage(DockerGenerator.class, "MSG_Maven_Project_Not_Found"));
        }
    }
}
