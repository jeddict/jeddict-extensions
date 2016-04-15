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
package org.netbeans.jcode.core.util;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.websvc.rest.spi.MiscUtilities;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Gaurav Gupta
 */
public class ProjectHelper {

    public static boolean isCDIEnabled(FileObject fileObject) {
        Project project = FileOwnerQuery.getOwner(fileObject);
        if (project != null) {
            return isCDIEnabled(project);
        }
        return false;
    }

    public static boolean isCDIEnabled(Project project) {
        WebModule wm = WebModule.getWebModule(project.getProjectDirectory());
        if (wm != null) {
            if (!MiscUtilities.isJavaEE6AndHigher(project)) {
                return false;
            }
            FileObject confRoot = wm.getWebInf();
            if (confRoot != null && confRoot.getFileObject("beans.xml") != null) {  //NOI18N
                return true;
            }
        }
        return false;
    }

    public static List<Project> getJavaProjects() {
        List<Project> list = new ArrayList<>();
        Project[] openProjects = OpenProjects.getDefault().getOpenProjects();

        if (openProjects != null) {
            for (Project prj : openProjects) {
                Sources sources = ProjectUtils.getSources(prj);
                if (sources == null) {
                    continue;
                }
                SourceGroup[] srcGrps = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
                if (srcGrps != null && srcGrps.length > 0) {
                    list.add(prj);
                }
            }
        }
        return list;
    }
    
     public static boolean isJavaEE6AndHigher(Project project) {
        return MiscUtilities.isJavaEE6AndHigher(project);
    }

}
