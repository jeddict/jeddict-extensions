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
package io.github.jeddict.runtime;

import static io.github.jeddict.jcode.DatabaseType.H2;
import io.github.jeddict.jcode.RuntimeProvider;
import io.github.jeddict.jcode.annotation.Runtime;
import static io.github.jeddict.jcode.jpa.PersistenceProviderType.ECLIPSELINK;
import io.github.jeddict.jcode.util.BuildManager;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = RuntimeProvider.class)
@Runtime(name = "PAYARA_SERVER",
        displayName = "Payara Server",
        version = "6.2022.1.Alpha4",
        persistenceProvider = ECLIPSELINK,
        embeddedDB = H2)
public final class PayaraServer extends Payara {

    @Override
    public String getDockerTemplate() {
        return TEMPLATE + "DockerFile_PAYARA.ftl";
    }

    @Override
    public String getBuildName() {
        return "${build.name}.war";
    }

    @Override
    public void addDependency(boolean docker) {
    }

    @Override
    public void addTestDependency(boolean docker) {
        BuildManager buildManager = BuildManager.getInstance(project)
                .copy(TEMPLATE + "payara/common/arquillian/pom.xml")
                .copy(TEMPLATE + "payara/server/arquillian/pom.xml");
        if (docker) {
            buildManager.copy(TEMPLATE + "payara/server/docker/arquillian/pom.xml");
        } else {
            buildManager.copy(TEMPLATE + "payara/server/no_docker/arquillian/pom.xml");
        }
        buildManager.commit();
    }
}
