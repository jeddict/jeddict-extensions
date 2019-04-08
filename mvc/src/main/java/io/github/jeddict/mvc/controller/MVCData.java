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
package io.github.jeddict.mvc.controller;

import io.github.jeddict.jcode.LayerConfigData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import io.github.jeddict.util.StringUtils;
import io.github.jeddict.mvc.controller.event.ControllerEventType;
import io.github.jeddict.mvc.controller.returntype.ControllerReturnType;
import io.github.jeddict.repository.RepositoryData;
import io.github.jeddict.rest.applicationconfig.RestConfigData;
import javax.json.bind.annotation.JsonbProperty;

/**
 *
 * @author Gaurav Gupta
 */
public class MVCData extends LayerConfigData<RepositoryData> {

    private String prefixName;

    private String suffixName;

    @JsonbProperty("package")
    private String _package;

    private boolean beanValidation;

    private ControllerReturnType returnType;

    private RestConfigData restConfigData;

    private List<ControllerEventType> eventTypes;

    private boolean csrf;

    private boolean xss;

    private boolean authentication;

    private boolean hybridClass = true;


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
        if(StringUtils.isBlank(suffixName)){
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

    public boolean isBeanValidation() {
        return beanValidation;
    }

    public void setBeanValidation(boolean beanValidation) {
        this.beanValidation = beanValidation;
    }

    /**
     * @return the eventTypes
     */
    public List<ControllerEventType> getEventType() {
        if(eventTypes==null){
            eventTypes = new ArrayList<>();
        }
        return eventTypes;
    }

    /**
     * @param eventType the eventTypes to set
     */
    public void setEventType(List<ControllerEventType> eventType) {
        this.eventTypes = eventType;
    }

    public boolean addEventType(ControllerEventType eventType) {
        return getEventType().add(eventType);
    }

    public boolean removeEventType(ControllerEventType eventType) {
        return getEventType().remove(eventType);
    }

    /**
     * @return the csrf
     */
    public boolean isCSRF() {
        return csrf;
    }

    /**
     * @param csrf the csrf to set
     */
    public void setCSRF(boolean csrf) {
        this.csrf = csrf;
    }

    /**
     * @return the xss
     */
    public boolean isXSS() {
        return xss;
    }

    /**
     * @param xss the xss to set
     */
    public void setXSS(boolean xss) {
        this.xss = xss;
    }

    /**
     * @return the hybridClass
     */
    public boolean isHybridClass() {
        return hybridClass;
    }

    /**
     * @param hybridClass the hybridClass to set
     */
    public void setHybridClass(boolean hybridClass) {
        this.hybridClass = hybridClass;
    }

    /**
     * @return the authentication
     */
    public boolean isAuthentication() {
        return authentication;
    }

    /**
     * @param authentication the authentication to set
     */
    public void setAuthentication(boolean authentication) {
        this.authentication = authentication;
    }
    
    @Override
    protected void onLayerConnection(){
        getParentLayerConfigData().setNamed(true);
    }
    
    @Override
    public List<String> getUsageDetails() {
        return Collections.<String>emptyList();
    }

}
