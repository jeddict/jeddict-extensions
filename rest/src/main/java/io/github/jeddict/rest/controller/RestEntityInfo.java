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

/**
 *
 * @author Gaurav Gupta
 */
public class RestEntityInfo{
    
    private final String name;
    private final String _package;
    private final String urlPath;

    public RestEntityInfo(String _package, String name, String urlPath) {
        this._package = _package;
        this.name = name;
        this.urlPath = urlPath;
    }

    public String getName() {
        return name;
    }

    public String getPackage() {
        return _package;
    }

    public String getUrlPath() {
        return urlPath;
    }
    
    
}