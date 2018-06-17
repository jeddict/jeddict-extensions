/**
 * Copyright 2013-2018 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.infra;

import static io.github.jeddict.infra.Payara.TEMPLATE;
import static io.github.jeddict.jcode.DatabaseType.H2;
import io.github.jeddict.jcode.RuntimeProvider;
import io.github.jeddict.jcode.annotation.Runtime;
import static io.github.jeddict.jcode.jpa.PersistenceProviderType.ECLIPSELINK;
import io.github.jeddict.jcode.util.BuildManager;
import io.github.jeddict.jcode.util.POMManager;
import java.util.Properties;
import org.openide.util.lookup.ServiceProvider;
import org.netbeans.modules.maven.model.pom.POMExtensibilityElement;

@ServiceProvider(service = RuntimeProvider.class)
@Runtime(name = "PAYARA_MICRO",
        displayName = "Payara Micro",
        version = "5.182",
        persistenceProvider = ECLIPSELINK,
        embeddedDB = H2)
public final class PayaraMicro extends Payara {
    
    private static final String WEB_PORT = "web.port";
    private static final String DEVELOPMENT_PROFILE = "dev";
    
    @Override
    public String getDockerTemplate() {
        return TEMPLATE + "DockerFile_JAVA.ftl";
    }

    @Override
    public String getBuildName() {
        return "${build.name}-microbundle.jar";
    }

    @Override
    public void addDependency(boolean docker) {
        POMManager pomManager = new POMManager(project, TEMPLATE + "payara/micro/pom/_pom.xml");
        pomManager.setExtensionOverrideFilter((source, target) -> {
            if ("option".equalsIgnoreCase(source.getName())) {
                for (POMExtensibilityElement element : target.getExtensibilityElements()) {
                    if ("key".equals(element.getQName().getLocalPart())) {
                        return source.getChild("key").getValue().equals(element.getElementText());
                    }
                }
            }
            return true;
        });
        pomManager.commit();
        appConfigData.addGoalAndActivate(project, "payara-micro:" + (docker ? "bundle" : "start"));

        Properties properties = new Properties();
        properties.put(WEB_PORT, "8080");//for micro maven plugin
        BuildManager.getInstance(project)
                .addDefaultProperties(DEVELOPMENT_PROFILE, properties)
                .commit();
        
        if (docker) {
            BuildManager.getInstance(project)
                .copy(TEMPLATE + "payara/micro/pom/_pom_docker.xml")
                .commit();
        }
    }

    @Override
    public void addTestDependency(boolean docker) {
        BuildManager.getInstance(project)
                .copy(TEMPLATE + "arquillian/pom/payara_common_pom.xml")
                .copy(TEMPLATE + "arquillian/pom/payara_micro_pom.xml")
                .commit();
    }
}
