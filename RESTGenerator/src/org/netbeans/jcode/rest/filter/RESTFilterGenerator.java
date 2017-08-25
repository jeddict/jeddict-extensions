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
package org.netbeans.jcode.rest.filter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import static org.netbeans.jcode.core.util.Constants.JAVA_EXT_SUFFIX;
import static org.netbeans.jcode.core.util.FileUtil.expandTemplate;
import static org.netbeans.jcode.core.util.SourceGroupSupport.getPackageForFolder;
import org.netbeans.jcode.task.progress.ProgressHandler;
import static org.netbeans.modules.j2ee.core.api.support.java.JavaIdentifiers.unqualify;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Gaurav Gupta
 */
@Deprecated
public class RESTFilterGenerator {

    private static final String TEMPLATE_PATH = "/org/netbeans/jcode/rest/filter/resources/";
    private static final String TEMPLATE_EXT = ".ftl";

    public static void generate(final Project project, final SourceGroup sourceGroup, FileObject packageFolder, List<FilterType> filterTypes, ProgressHandler handler) throws IOException {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("package", getPackageForFolder(sourceGroup, packageFolder));
        for (FilterType filterType : filterTypes) {
            String className = unqualify(filterType.getClassName());
            String fileName = className + "Impl";
            params.put("class", fileName);
            handler.progress(fileName);
            expandTemplate(TEMPLATE_PATH + className + TEMPLATE_EXT, packageFolder, fileName + JAVA_EXT_SUFFIX, params);
        }
    }

}
