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

import io.github.jeddict.jcode.ApplicationConfigData;
import io.github.jeddict.jcode.DatabaseType;
import static io.github.jeddict.jcode.DatabaseType.DERBY;
import static io.github.jeddict.jcode.DatabaseType.MARIADB;
import static io.github.jeddict.jcode.DatabaseType.MYSQL;
import static io.github.jeddict.jcode.JPAConstants.JAVA_GLOBAL_DATASOURCE_PREFIX;
import io.github.jeddict.jcode.RuntimeProvider;
import io.github.jeddict.jcode.annotation.ConfigData;
import io.github.jeddict.jcode.jpa.PersistenceProviderType;
import io.github.jeddict.jcode.util.BuildManager;
import io.github.jeddict.jcode.util.PersistenceUtil;
import static io.github.jeddict.jcode.util.PersistenceUtil.addProperty;
import io.github.jeddict.jpa.spec.EntityMappings;
import java.util.Optional;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.persistence.dd.common.PersistenceUnit;

public abstract class Payara implements RuntimeProvider {

    @ConfigData
    protected Project project;

    @ConfigData
    protected EntityMappings entityMapping;

    @ConfigData
    protected ApplicationConfigData appConfigData;
    
    protected static final String TEMPLATE = "io/github/jeddict/runtime/template/";

    @Override
    public void updatePersistenceProvider(DatabaseType databaseType) {
        if (entityMapping.getPersistenceProviderType() == PersistenceProviderType.HIBERNATE) {
            String puName = entityMapping.getPersistenceUnitName();
            Optional<PersistenceUnit> punitOptional = PersistenceUtil.getPersistenceUnit(project, puName);
            if (punitOptional.isPresent()) {
                PersistenceUnit punit = punitOptional.get();
                addProperty(punit, "hibernate.dialect", getHibernateDialect(databaseType));
//                addProperty(punit, "hibernate.hbm2ddl.auto", "create");
                addProperty(punit, "hibernate.show_sql", "true");
                addProperty(punit, "hibernate.transaction.jta.platform", "org.hibernate.service.jta.platform.internal.SunOneJtaPlatform");
                PersistenceUtil.updatePersistenceUnit(project, punit);
            }
            BuildManager.getInstance(project)
                    .copy(TEMPLATE + "persistence/provider/pom/PAYARA_HIBERNATE.xml")
                    .commit();
        }
    }

    private String getHibernateDialect(DatabaseType databaseType) {
        if (databaseType != null) {
            switch (databaseType) {
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
    
    @Override
    public String getJNDIPrefix() {
        return JAVA_GLOBAL_DATASOURCE_PREFIX + "jdbc/";
    }
    
}
