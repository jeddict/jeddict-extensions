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

import io.github.jeddict.rest.util.RestMethod;
import static org.netbeans.modules.websvc.rest.model.api.RestConstants.GET;
import static org.netbeans.modules.websvc.rest.model.api.RestConstants.POST;

public enum Operation implements RestMethod {

    //CRUD => Create, List, Update, Remove
    REDIRECT_TO_CREATE(GET, "empty",
            "new", "<entity>/create.jsp"),
    CREATE(POST, "create",
            "new", "redirect:<entity>/list"),
    REDIRECT_TO_UPDATE(GET, "edit",
            "update/{id}", "<entity>/update.jsp"),
    UPDATE(POST, "update",
            "update", "redirect:<entity>/list"),
    REMOVE(GET, "remove",
            "remove/{id}", "redirect:<entity>/list"),
    FIND(GET, "find",
            "{id}", "<entity>/view.jsp"),
    FIND_ALL(GET, "findAll",
            "list", "<entity>/list.jsp");

    private final String method, methodName;
    private final String uriPath, view;

    private Operation(String method, String methodName, String uriPath, String view) {
        this.method = method;
        this.methodName = methodName;
        this.uriPath = uriPath;
        this.view = view;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public String getUriPath() {
        return uriPath;
    }

    @Override
    public String getView() {
        return view;
    }

}
