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
package org.netbeans.jcode.mvc.auth.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.jcode.core.util.Constants;
import org.netbeans.jcode.core.util.FileUtil;
import static org.netbeans.jcode.core.util.SourceGroupSupport.getPackageForFolder;
import org.netbeans.jcode.task.progress.ProgressHandler;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Gaurav Gupta
 */
public class LoginControllerGenerator {

    private static final String LOGIN_CONTROLLER_CLASS = "LoginController";
    private static final String TEMPLATE = "/org/netbeans/jcode/mvc/auth/controller/LoginController.ftl";

    public static void generate(final Project project, final SourceGroup sourceGroup, FileObject packageFolder, ProgressHandler handler) throws IOException {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("package", getPackageForFolder(sourceGroup, packageFolder));
        handler.progress(LOGIN_CONTROLLER_CLASS);
        FileUtil.expandTemplate(TEMPLATE, packageFolder, LOGIN_CONTROLLER_CLASS + Constants.JAVA_EXT_SUFFIX, params);
        
    }

}
