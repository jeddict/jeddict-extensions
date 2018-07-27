/**
 * Copyright 2013-2018 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.rest.util;

/**
 * This class represents code generation options.
 *
 */
public final class RestGenerationOptions {

    private RestMethod method;
    private String returnType;
    private StringBuilder body;
    private String[] parameterAnnoation, parameterTypes, parameterNames, parameterAnnoationValues, consumes, produces;

    public String[] getConsumes() {
        return consumes;
    }

    public StringBuilder getBody() {
        if (this.body == null) {
            this.body = new StringBuilder();
        }
        return body;
    }

    public void setBody(StringBuilder body) {
        this.body = body;
    }

    public StringBuilder setBody(String body) {
        this.body = new StringBuilder(body);
        return this.body;
    }

    public StringBuilder appendBody(String bodyContent) {
        
        getBody().append(bodyContent);
        return this.body;
    }
    
    public StringBuilder appendBody(char bodyContent) {
        
        getBody().append(bodyContent);
        return this.body;
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
