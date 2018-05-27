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

import static io.github.jeddict.jcode.DatabaseType.H2;
import io.github.jeddict.jcode.RuntimeProvider;
import static io.github.jeddict.jcode.jpa.PersistenceProviderType.HIBERNATE;
import org.openide.util.lookup.ServiceProvider;
import io.github.jeddict.jcode.annotation.Runtime;

//@ServiceProvider(service = RuntimeProvider.class)
//@Runtime(name = "WILDFLY_SERVER",
//        displayName = "Wildfly Server",
//        persistenceProvider = HIBERNATE,
//        embeddedDB = H2)
public final class WildflyServer extends Wildfly {

    @Override
    public String getDockerTemplate() {
        return TEMPLATE + "DockerFile_WILDFLY.ftl";
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
    }
}
