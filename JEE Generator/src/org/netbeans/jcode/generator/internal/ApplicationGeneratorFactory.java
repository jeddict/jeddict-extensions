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
package org.netbeans.jcode.generator.internal;

import org.netbeans.api.project.Project;
import org.netbeans.jcode.core.util.ProjectHelper;
import org.netbeans.jcode.core.util.ProjectType;

/**
 *
 * @author Gaurav Gupta
 */
public class ApplicationGeneratorFactory {

    public static BaseApplicationGenerator newInstance(Project project) {

        if(ProjectHelper.getProjectType(project) != ProjectType.JAR ) {
//        if (MiscUtilities.isJavaEE6AndHigher(project)) {//removed to provide support for Gradle project
            return new ApplicationGenerator();
        } else {
            throw new IllegalStateException("JEE6+ supported");
        }
    }
}
