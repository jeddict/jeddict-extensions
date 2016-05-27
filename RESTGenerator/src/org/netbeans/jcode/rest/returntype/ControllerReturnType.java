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
package org.netbeans.jcode.rest.returntype;

import org.openide.util.NbBundle;

/**
 *
 * @author Gaurav Gupta
 */
public enum ControllerReturnType {

    JAXRS_RESPONSE("JAX-RS Response ", NbBundle.getMessage(ControllerReturnType.class, "REST.returnType.Response")),
    GENERIC_ENTITY("Generic Entity", NbBundle.getMessage(ControllerReturnType.class, "REST.returnType.GenericEntity"));

    private final String title;
    private final String description;

    private ControllerReturnType(String title, String description) {
        this.title = title;
        this.description = description;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return title;
    }

}
