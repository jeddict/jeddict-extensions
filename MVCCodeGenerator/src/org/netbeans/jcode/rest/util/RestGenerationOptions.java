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
package org.netbeans.jcode.rest.util;

/**
 * This class represents code generation options.
 *
 */
public final class RestGenerationOptions {

    private RestMethod method;
    private String returnType, body;
    private String[] parameterAnnoation, parameterTypes, parameterNames, parameterAnnoationValues, consumes, produces;

    public String[] getConsumes() {
        return consumes;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setConsumes(String[] consumes) {
        this.consumes = consumes;
    }

    public String[] getParameterNames() {
        return parameterNames;
    }

    public void setParameterNames(String[] parameterNames) {
        this.parameterNames = parameterNames;
    }

    public String[] getParameterAnnoationValues() {
        return parameterAnnoationValues;
    }

    public void setParameterAnnoationValues(String[] parameterAnnoationValues) {
        this.parameterAnnoationValues = parameterAnnoationValues;
    }

    public String[] getProduces() {
        return produces;
    }

    public void setProduces(String[] produces) {
        this.produces = produces;
    }

    public String[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(String[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public RestMethod getRestMethod() {
        return method;
    }

    public void setRestMethod(RestMethod operation) {
        method = operation;
    }

    /**
     * @return the parameterAnnoation
     */
    public String[] getParameterAnnoations() {
        return parameterAnnoation;
    }

    /**
     * @param parameterAnnoation the parameterAnnoation to set
     */
    public void setParameterAnnoations(String[] parameterAnnoation) {
        this.parameterAnnoation = parameterAnnoation;
    }

}
