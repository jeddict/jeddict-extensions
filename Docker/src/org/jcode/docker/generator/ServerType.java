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

/**
 *
 * @author jGauravGupta
 */
public enum ServerType {
    WILDFLY("Wildfly", Arrays.asList("latest","8.1.0.Final","8.2.1.Final","8.2.0.Final","9.0.0.Final","10.1.0.Final","9.0.1.Final","9.0.2.Final","10.0.0.Final")),
    PAYARA("Payara", Arrays.asList("latest", "161", "161.1")),
//    GLASSFISH("Glassfish", Arrays.asList("4.1.1","4.1.1-web")),
    NONE("<No Server Selected>", EMPTY_LIST);
    
    private String displayName;
    private List<String> version;

    private ServerType(String displayName, List<String> version) {
        this.displayName = displayName;
        this.version = version;
    }
    
    public String getDisplayName() {
        return displayName;
    }

    public List<String> getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return displayName;
    }
    
    
}
