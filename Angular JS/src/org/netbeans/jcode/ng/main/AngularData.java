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
package org.netbeans.jcode.ng.main;

import org.netbeans.jcode.ng.main.domain.EntityConfig;
import org.netbeans.jcode.stack.config.data.LayerConfigData;

/**
 *
 * @author Gaurav Gupta
 */
public class AngularData extends LayerConfigData {

    private String module;
    private String applicationTitle;

    private EntityConfig applicationConfig;


    /**
     * @return the applicationConfig
     */
    public EntityConfig getApplicationConfig() {
        return applicationConfig;
    }

    /**
     * @param applicationConfig the applicationConfig to set
     */
    public void setApplicationConfig(EntityConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    /**
     * @return the module
     */
    public String getModule() {
        return module;
    }

    /**
     * @param module the module to set
     */
    public void setModule(String module) {
        this.module = module;
    }

    /**
     * @return the applicationTitle
     */
    public String getApplicationTitle() {
        return applicationTitle;
    }

    /**
     * @param applicationTitle the applicationTitle to set
     */
    public void setApplicationTitle(String applicationTitle) {
        this.applicationTitle = applicationTitle;
    }
}
