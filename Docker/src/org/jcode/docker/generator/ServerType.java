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

import java.util.Arrays;
import static java.util.Collections.EMPTY_LIST;
import java.util.List;
import static org.jcode.docker.generator.ServerFamily.PAYARA_FAMILY;
import static org.jcode.docker.generator.ServerFamily.WILDFLY_FAMILY;

/**
 *
 * @author jGauravGupta
 */
public enum ServerType {
    
    NONE(null, "<No Server Selected>", null, EMPTY_LIST),
    PAYARA(PAYARA_FAMILY, "Payara", "org.eclipse.persistence.jpa.PersistenceProvider", Arrays.asList("latest", "161", "161.1")),
    PAYARA_MICRO(PAYARA_FAMILY, "Payara Micro", "org.eclipse.persistence.jpa.PersistenceProvider", Arrays.asList("latest", "161", "161.1")),
    WILDFLY(WILDFLY_FAMILY, "Wildfly", "org.hibernate.ejb.HibernatePersistence", Arrays.asList("latest", "8.1.0.Final", "8.2.1.Final", "8.2.0.Final", "9.0.0.Final", "10.1.0.Final", "9.0.1.Final", "9.0.2.Final", "10.0.0.Final"));
//    GLASSFISH("Glassfish", Arrays.asList("4.1.1","4.1.1-web")),

    private final ServerFamily family;
    private final String displayName;
    private final List<String> version;
    private final String persistenceProvider;

    private ServerType(ServerFamily family, String displayName, String persistenceProvider, List<String> version) {
        this.family = family;
        this.displayName = displayName;
        this.version = version;
        this.persistenceProvider = persistenceProvider;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getVersion() {
        return version;
    }

    /**
     * @return the persistenceProvider
     */
    public String getPersistenceProvider() {
        return persistenceProvider;
    }

    @Override
    public String toString() {
        return displayName;
    }

    /**
     * @return the family
     */
    public ServerFamily getFamily() {
        return family;
    }

}
