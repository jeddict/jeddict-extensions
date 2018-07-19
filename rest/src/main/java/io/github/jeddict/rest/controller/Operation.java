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
package io.github.jeddict.rest.controller;

import io.github.jeddict.rest.util.RestMethod;
import static org.netbeans.modules.websvc.rest.model.api.RestConstants.DELETE;
import static org.netbeans.modules.websvc.rest.model.api.RestConstants.GET;
import static org.netbeans.modules.websvc.rest.model.api.RestConstants.POST;
import static org.netbeans.modules.websvc.rest.model.api.RestConstants.PUT;

public enum Operation implements RestMethod {

    //CRUD => Create, List, Update, Remove
    CREATE(POST, "create", null, null),
    EDIT(PUT, "edit", "{id}", null),
    REMOVE(DELETE, "remove", "{id}", null),
    FIND(GET, "find", "{id}", null),
    FIND_ALL(GET, "findAll", null, null),
    FIND_RANGE(GET, "findRange", "{from}/{to}", null),
    COUNT(GET, "count", "count", null);

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
