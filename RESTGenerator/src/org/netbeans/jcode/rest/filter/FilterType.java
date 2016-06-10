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
package org.netbeans.jcode.rest.filter;

import org.netbeans.jcode.rest.RestConstants;
import org.openide.util.NbBundle;

/**
 *
 * @author Gaurav Gupta
 */
public enum FilterType {

    PRE_MATCHING(RestConstants.PRE_MATCHING,
            NbBundle.getMessage(FilterType.class, "REST.fiter.PRE_MATCHING.title"),
            NbBundle.getMessage(FilterType.class, "REST.fiter.PRE_MATCHING")),
    POST_MATCHING(RestConstants.CONTAINER_REQUEST,
            NbBundle.getMessage(FilterType.class, "REST.fiter.POST_MATCHING.title"),
            NbBundle.getMessage(FilterType.class, "REST.fiter.POST_MATCHING")),
    CONTAINER_RESPONSE(RestConstants.CONTAINER_RESPONSE,
            NbBundle.getMessage(FilterType.class, "REST.fiter.CONTAINER_RESPONSE.title"),
            NbBundle.getMessage(FilterType.class, "REST.fiter.CONTAINER_RESPONSE")),
    CLIENT_REQUEST(RestConstants.CLIENT_REQUEST,
            NbBundle.getMessage(FilterType.class, "REST.fiter.CLIENT_REQUEST.title"),
            NbBundle.getMessage(FilterType.class, "REST.fiter.CLIENT_REQUEST")),
    CLIENT_RESPONSE(RestConstants.CLIENT_RESPONSE,
            NbBundle.getMessage(FilterType.class, "REST.fiter.CLIENT_RESPONSE.title"),
            NbBundle.getMessage(FilterType.class, "REST.fiter.CLIENT_RESPONSE"));

    private final String className;
    private final String title;
    private final String description;

    private FilterType(String className, String title, String description) {
        this.className = className;
        this.title = title;
        this.description = description;
    }

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    @Override
    public String toString() {
        return title;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

}
