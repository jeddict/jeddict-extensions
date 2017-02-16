/**
 * Copyright [2017] Gaurav Gupta
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
package org.netbeans.jcode.rest.controller;

import java.util.ArrayList;
import java.util.List;
import static org.apache.commons.lang.StringUtils.isBlank;
import org.netbeans.jcode.rest.applicationconfig.RestConfigData;
import org.netbeans.jcode.rest.filter.FilterType;
import org.netbeans.jcode.rest.returntype.ControllerReturnType;
import org.netbeans.jcode.stack.config.data.LayerConfigData;

/**
 *
 * @author Gaurav Gupta
 */
public class RESTData extends LayerConfigData {

    private String prefixName;
    private String suffixName;
    private String _package;
    private String appPackage;
    private RestConfigData restConfigData;
    private ControllerReturnType returnType;
    private List<FilterType> filterTypes;
    private boolean metrics = true;
    private boolean logger = true;
    private boolean docsEnable = true;
    private boolean completeApplication = true;
    private boolean testCase = true;
    private boolean pagination;
    
    /**
     * @return the _package
     */
    public String getPackage() {
        return _package;
    }

    /**
     * @param _package the _package to set
     */
    public void setPackage(String _package) {
        this._package = _package;
    }

    /**
     * @return the suffixName
     */
    public String getSuffixName() {
        if (isBlank(suffixName)) {
            suffixName = "Controller";
        }
        return suffixName;
    }

    /**
     * @param suffixName the suffixName to set
     */
    public void setSuffixName(String suffixName) {
        this.suffixName = suffixName;
    }

    /**
     * @return the prefixName
     */
    public String getPrefixName() {
        return prefixName;
    }

    /**
     * @param prefixName the prefixName to set
     */
    public void setPrefixName(String prefixName) {
        this.prefixName = prefixName;
    }

    /**
     * @return the restConfigData
     */
    public RestConfigData getRestConfigData() {
        return restConfigData;
    }

    /**
     * @param restConfigData the restConfigData to set
     */
    public void setRestConfigData(RestConfigData restConfigData) {
        this.restConfigData = restConfigData;
    }

    public ControllerReturnType getReturnType() {
        return returnType;
    }

    public void setReturnType(ControllerReturnType returnType) {
        this.returnType = returnType;
    }

    /**
     * @return the filterTypes
     */
    public List<FilterType> getFilterTypes() {
        if (filterTypes == null) {
            filterTypes = new ArrayList<>();
        }
        return filterTypes;
    }

    /**
     * @param filterTypes the filterTypes to set
     */
    public void setFilterTypes(List<FilterType> filterTypes) {
        this.filterTypes = filterTypes;
    }

    public boolean addFilterType(FilterType filterType) {
        return getFilterTypes().add(filterType);
    }

    public boolean removeFilterType(FilterType filterType) {
        return getFilterTypes().remove(filterType);
    }

    /**
     * @return the appPackage
     */
    public String getAppPackage() {
        return appPackage;
    }

    /**
     * @param appPackage the appPackage to set
     */
    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    /**
     * @return the metrics
     */
    public boolean isMetrics() {
        return metrics;
    }

    /**
     * @param metrics the metrics to set
     */
    public void setMetrics(boolean metrics) {
        this.metrics = metrics;
    }

    /**
     * @return the swagger
     */
    public boolean isDocsEnable() {
        return docsEnable;
    }

    /**
     * @param swagger the swagger to set
     */
    public void setDocsEnable(boolean swagger) {
        this.docsEnable = swagger;
    }
    
    /**
     * @return the pagination
     */
    public boolean isPagination() {
        return pagination;
    }

    /**
     * @param pagination the pagination to set
     */
    public void setPagination(boolean pagination) {
        this.pagination = pagination;
    }

    /**
     * @return the testCase
     */
    public boolean isTestCase() {
        return testCase;
    }

    /**
     * @param testCase the testCase to set
     */
    public void setTestCase(boolean testCase) {
        this.testCase = testCase;
    }

    /**
     * @return the logger
     */
    public boolean isLogger() {
        return logger;
    }

    /**
     * @param logger the logger to set
     */
    public void setLogger(boolean logger) {
        this.logger = logger;
    }

    /**
     * @return the completeApplication
     */
    public boolean isCompleteApplication() {
        return completeApplication;
    }

    /**
     * @param completeApplication the completeApplication to set
     */
    public void setCompleteApplication(boolean completeApplication) {
        this.completeApplication = completeApplication;
    }

}
