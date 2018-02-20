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
package io.github.jeddict.rest.filter;

import static io.github.jeddict.jcode.util.Constants.JAVA_EXT_SUFFIX;
import static io.github.jeddict.jcode.util.FileUtil.expandTemplate;
import static io.github.jeddict.jcode.util.SourceGroupSupport.getPackageForFolder;
import io.github.jeddict.jcode.task.progress.ProgressHandler;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import static io.github.jeddict.jcode.util.Constants.JAVA_EXT_SUFFIX;
import static io.github.jeddict.jcode.util.SourceGroupSupport.getPackageForFolder;
import static org.netbeans.modules.j2ee.core.api.support.java.JavaIdentifiers.unqualify;
import org.openide.filesystems.FileObject;
import static io.github.jeddict.jcode.util.FileUtil.expandTemplate;

/**
 *
 * @author Gaurav Gupta
 */
@Deprecated
public class RESTFilterGenerator {

    private static final String TEMPLATE_PATH = "/io/github/jeddict/rest/filter/resources/";
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
