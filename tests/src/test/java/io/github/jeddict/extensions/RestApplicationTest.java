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
package io.github.jeddict.extensions;

import io.github.jeddict.docker.generator.DockerConfigData;
import io.github.jeddict.docker.generator.DockerGenerator;
import static io.github.jeddict.jcode.DatabaseType.H2;
import io.github.jeddict.jcode.TechContext;
import io.github.jeddict.rest.controller.RESTData;
import io.github.jeddict.rest.controller.RESTGenerator;
import io.github.jeddict.runtime.PayaraServer;
import static java.util.Collections.singletonList;
import org.junit.jupiter.api.*;
import org.netbeans.api.project.Project;

/**
 *
 * @author jGauravGupta
 */
public class RestApplicationTest extends RepositoryApplicationTest {

    @Test
    @Override
    protected void test() throws Exception {
        Project project = generateMonolith("jaxrs-sample-app", "default-monolith.jpa");
        fireMavenBuild(project, singletonList("install"), singletonList("payara-ci-managed"), null);
    }

    @Override
    protected TechContext getRepositoryContext() {
        DockerConfigData dockerConfigData = new DockerConfigData();
        dockerConfigData.setRuntimeProviderClass(PayaraServer.class);
        dockerConfigData.setDatabaseType(H2);
        dockerConfigData.setDbName(null);
        dockerConfigData.setDbHost(null);

        TechContext techContext = super.getRepositoryContext();
        techContext.findSiblingTechContext(DockerGenerator.class)
                .ifPresent(context -> context.setConfigData(dockerConfigData));
        return techContext;
    }

    @Override
    protected TechContext getControllerContext() {
        RESTData restData = new RESTData();
        restData.setPrefixName("");
        restData.setPackage("controller");
        restData.setTestCase(true);
        TechContext techContext = new TechContext(RESTGenerator.class);
        techContext.setConfigData(restData);
        return techContext;
    }

}
