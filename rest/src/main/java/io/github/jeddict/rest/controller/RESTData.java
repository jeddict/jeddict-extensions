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
package io.github.jeddict.rest.controller;

import io.github.jeddict.jcode.LayerConfigData;
import io.github.jeddict.repository.RepositoryData;
import java.util.Arrays;
import java.util.List;
import static io.github.jeddict.util.StringUtils.isBlank;
import io.github.jeddict.rest.applicationconfig.RestConfigData;
import static io.github.jeddict.rest.controller.SecurityType.SECURITY_JWT;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTransient;

/**
 *
 * @author Gaurav Gupta
 */
public class RESTData extends LayerConfigData<RepositoryData> {

    private String prefixName;

    private String suffixName;

    @JsonbProperty("package")
    private String _package;

    private RestConfigData restConfigData;

    private boolean metrics = true;

    private boolean logger = true;

    private boolean openAPI = true;

    private boolean testCase = true;

    @JsonbTransient
    private String frontendAppName = "app";

    private SecurityType securityType;

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
        if (restConfigData == null) {
            return new RestConfigData();
        }
        return restConfigData;
    }

    /**
     * @param restConfigData the restConfigData to set
     */
    public void setRestConfigData(RestConfigData restConfigData) {
        this.restConfigData = restConfigData;
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
     * @return the openAPI
     */
    public boolean isOpenAPI() {
        return openAPI;
    }

    /**
     * @param openAPI the openAPI to set
     */
    public void setOpenAPI(boolean openAPI) {
        this.openAPI = openAPI;
    }

    /**
     * @return the frontendAppName
     */
    public String getFrontendAppName() {
        return frontendAppName;
    }

    /**
     * @param frontendAppName the frontendAppName to set
     */
    public void setFrontendAppName(String frontendAppName) {
        this.frontendAppName = frontendAppName;
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
    
    @Override
    protected void onLayerConnection(){
        getParentLayerConfigData().setNamed(false);
    }
    
    /**
     * @return the securityType
     */
    public SecurityType getSecurityType() {
        if (securityType == null) {
            securityType = SECURITY_JWT;
        }
        return securityType;
    }

    /**
     * @param securityType the securityType to set
     */
    public void setSecurityType(SecurityType securityType) {
        this.securityType = securityType;
    }

    @Override
    public List<String> getUsageDetails() {
        return Arrays.asList(
                metrics ? "Metrics" : null,
                logger ? "Log-Manager" : null,
                openAPI ? "OpenAPI" : null,
                testCase ? "TestCase" : null,
                securityType != null ? securityType.name() : null
        );
    }
}
