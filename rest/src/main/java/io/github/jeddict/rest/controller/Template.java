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

/**
 *
 * @author jGauravGupta
 */
public class Template {
    private final String path;
    private final String fileName;
    private String packageSuffix;

    public Template(String path, String fileName) {
        this.path = path;
        this.fileName = fileName;
    }

    public Template(String path, String fileName, String packageSuffix) {
        this.path = path;
        this.fileName = fileName;
        this.packageSuffix = packageSuffix;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @return the packageSuffix
     */
    public String getPackageSuffix() {
        return packageSuffix;
    }
    
}
