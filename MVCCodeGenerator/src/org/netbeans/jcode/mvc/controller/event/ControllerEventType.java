/**
 * Copyright [2016] Gaurav Gupta
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a
 * workingCopy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.netbeans.jcode.mvc.controller.event;

import org.netbeans.jcode.core.util.Constants;
import org.netbeans.modules.j2ee.core.api.support.java.JavaIdentifiers;

/**
 *
 * @author Gaurav Gupta
 */
public enum ControllerEventType {

    
    POST_CONSTRUCT(Constants.POST_CONSTRUCT, "init",
            "logger.config(() -> this.getClass().getSimpleName() + \" created\");"),
    BEFORE_CONTROLLER_EVENT("javax.mvc.event.BeforeControllerEvent", "onBeforeController",
            "logger.info(() -> \"Controller matched for \" + event.getUriInfo().getRequestUri());"),
    AFTER_CONTROLLER_EVENT("javax.mvc.event.AfterControllerEvent", "onAfterController",
            "logger.info(() -> \"Controller: \" + e.getResourceInfo().getResourceMethod());"),
    BEFORE_PROCESSVIEW_EVENT("javax.mvc.event.BeforeProcessViewEvent", "onBeforeProcessView",
            "logger.info(() -> \"View engine: \" + event.getView());"),
    AFTER_PROCESSVIEW_EVENT("javax.mvc.event.AfterProcessViewEvent", "onAfterProcessView",
            "logger.info(() -> \"View engine: \" + event.getEngine());"),
    CONTROLLER_REDIRECT_EVENT("javax.mvc.event.ControllerRedirectEvent", "onControllerRedirect",
            "logger.info(() -> \"Location: \" + e.getLocation());"),;

    private String className;
    private String methodName;
    private String body;

    private ControllerEventType(String className, String methodName, String body) {
        this.className = className;
        this.methodName = methodName;
        this.body = body;
    }

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param className the className to set
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * @return the methodName
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * @param methodName the methodName to set
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * @return the body
     */
    public String getBody() {
        return body;
    }

    /**
     * @param body the body to set
     */
    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return JavaIdentifiers.unqualify(className);
    }
    
    

}
