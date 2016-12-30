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
import java.util.List;

/**
 *
 * @author jGauravGupta
 */
public enum DatabaseType {
    MYSQL("MySQL", Arrays.asList("latest","5.5","5.6","5.7", "8.0"), true),
    POSTGRESQL("PostgreSQL", Arrays.asList("latest", "9.6", "9.5", "9.4", "9.3", "9.2"), true),
    DERBY("Derby", Arrays.asList("--"), false);
    
    String displayName;
    List<String> version;
    boolean dockerSupport;

    private DatabaseType(String displayName, List<String> version, boolean dockerSupport) {
        this.displayName = displayName;
        this.version = version;
        this.dockerSupport = dockerSupport;
    }
    
    public String getDisplayName() {
        return displayName;
    }

    public List<String> getVersion() {
        return version;
    }
    
    public boolean isDockerSupport() {
        return dockerSupport;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
