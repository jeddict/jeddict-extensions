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

import java.util.Arrays;
import java.util.List;
import org.netbeans.jcode.ng.main.domain.EntityConfig;
import org.netbeans.jcode.rest.controller.RESTData;
import org.netbeans.jcode.stack.config.data.LayerConfigData;

/**
 *
 * @author Gaurav Gupta
 */
public class AngularData extends LayerConfigData<RESTData> {

    private String module;
    private String applicationTitle;
    private EntityConfig applicationConfig;
    private PaginationType pagination; 
    private boolean protractorTest;

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
    
    @Override
    protected void onLayerConnection(){
        getParentLayerConfigData().setPagination(pagination != null && pagination != PaginationType.NO);
    }

    /**
     * @return the pagination
     */
    public PaginationType getPagination() {
        if(pagination==null){
            return PaginationType.NO;
        }
        return pagination;
    }

    /**
     * @param pagination the pagination to set
     */
    public void setPagination(PaginationType pagination) {
        this.pagination = pagination;
    }

    /**
     * @return the protractorTest
     */
    public boolean isProtractorTest() {
        return protractorTest;
    }

    /**
     * @param protractorTest the protractorTest to set
     */
    public void setProtractorTest(boolean protractorTest) {
        this.protractorTest = protractorTest;
    }
    
    @Override
    public List<String> getUsageDetails() {
        return Arrays.asList("PaginationType-"+getPagination().getTitle(), 
                isProtractorTest() ? "ProtractorTest" : null);
    }
}
