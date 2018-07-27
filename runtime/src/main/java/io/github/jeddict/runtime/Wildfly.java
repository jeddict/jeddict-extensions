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
package io.github.jeddict.runtime;

import io.github.jeddict.jcode.ApplicationConfigData;
import io.github.jeddict.jcode.DatabaseType;
import static io.github.jeddict.jcode.JPAConstants.JAVA_DATASOURCE_PREFIX;
import io.github.jeddict.jcode.RuntimeProvider;
import io.github.jeddict.jcode.annotation.ConfigData;
import io.github.jeddict.jcode.jpa.PersistenceProviderType;
import io.github.jeddict.jcode.util.BuildManager;
import io.github.jeddict.jpa.spec.EntityMappings;
import org.netbeans.api.project.Project;

public abstract class Wildfly implements RuntimeProvider {

    @ConfigData
    protected Project project;

    @ConfigData
    protected EntityMappings entityMapping;

    @ConfigData
    protected ApplicationConfigData appConfigData;
    
    protected static final String TEMPLATE = "io/github/jeddict/runtime/template/";

    @Override
    public String getJNDIPrefix() {
        return JAVA_DATASOURCE_PREFIX + "jdbc/";
    }

    @Override
    public void updatePersistenceProvider(DatabaseType databaseType) {
        if (entityMapping.getPersistenceProviderType() == PersistenceProviderType.ECLIPSELINK) {
            BuildManager.getInstance(project)
                    .copy(TEMPLATE + "persistence/provider/pom/WILDFLY_ECLIPSELINK.xml")
                    .commit();
        }
    }
    
}
