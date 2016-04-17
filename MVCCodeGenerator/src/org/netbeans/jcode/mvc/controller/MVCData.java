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
package org.netbeans.jcode.mvc.controller;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.netbeans.jcode.mvc.controller.api.returntype.ControllerReturnType;
import org.netbeans.jcode.mvc.controller.event.ControllerEventType;
import org.netbeans.jcode.rest.applicationconfig.RestConfigData;
import org.netbeans.jcode.stack.config.data.LayerConfigData;

/**
 *
 * @author Gaurav Gupta
 */
public class MVCData extends LayerConfigData {

    private String prefixName;
    private String suffixName;
    private String _package;
    private boolean beanValidation;
    private ControllerReturnType returnType;
    private RestConfigData restConfigData;
    private List<ControllerEventType> eventTypes;
    private boolean csrf;
    private boolean xss;

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

}
