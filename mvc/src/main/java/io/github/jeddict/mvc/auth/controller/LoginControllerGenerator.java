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
package io.github.jeddict.mvc.auth.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import io.github.jeddict.jcode.util.Constants;
import io.github.jeddict.jcode.util.FileUtil;
import static io.github.jeddict.jcode.util.SourceGroupSupport.getPackageForFolder;
import io.github.jeddict.jcode.task.progress.ProgressHandler;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Gaurav Gupta
 */
public class LoginControllerGenerator {

    private static final String LOGIN_CONTROLLER_CLASS = "LoginController";
    private static final String TEMPLATE = "/io/github/jeddict/mvc/auth/controller/LoginController.ftl";

    public static void generate(final Project project, final SourceGroup sourceGroup, FileObject packageFolder, ProgressHandler handler) throws IOException {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("package", getPackageForFolder(sourceGroup, packageFolder));
        handler.progress(LOGIN_CONTROLLER_CLASS);
        FileUtil.expandTemplate(TEMPLATE, packageFolder, LOGIN_CONTROLLER_CLASS + Constants.JAVA_EXT_SUFFIX, params);
        
    }

}
