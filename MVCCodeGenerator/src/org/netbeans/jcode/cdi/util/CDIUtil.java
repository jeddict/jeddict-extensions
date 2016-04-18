/**
 * Copyright [2016] Gaurav Gupta
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a
 * workingCopy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.netbeans.jcode.cdi.util;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import org.netbeans.api.j2ee.core.Profile;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import static org.netbeans.jcode.core.util.Constants.WEB_INF;
import org.netbeans.jcode.core.util.FileUtil;
import org.netbeans.modules.j2ee.common.J2eeProjectCapabilities;
import org.netbeans.modules.j2ee.common.dd.DDHelper;
import org.netbeans.modules.web.api.webmodule.WebProjectConstants;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;

/**
 *
 * @author Gaurav Gupta
 */
public class CDIUtil {

    private static final String RESOURCE_FOLDER = "/org/netbeans/jcode/cdi/resource/"; //NOI18N

    private static final String defaultName = "beans";

    public static Set<DataObject> createDD(Project project) throws IOException {
        Sources sources = ProjectUtils.getSources(project);
        SourceGroup sourceGroups[] = sources.getSourceGroups(WebProjectConstants.TYPE_DOC_ROOT);
        FileObject webRoot = sourceGroups[0].getRootFolder();
        FileObject targetDir = FileUtil.createFolder(webRoot, WEB_INF);
        boolean useCDI11 = true;
        if (project != null) {
            J2eeProjectCapabilities cap = J2eeProjectCapabilities.forProject(project);
            if (cap != null && !cap.isCdi11Supported()) {
                useCDI11 = false;
            }
        }
        FileObject fo = createBeansXml(useCDI11 ? Profile.JAVA_EE_7_FULL : Profile.JAVA_EE_6_FULL, targetDir, defaultName);
        if (fo != null) {
            return Collections.singleton(DataObject.find(fo));
        } else {
            return Collections.EMPTY_SET;
        }
    }
    
    /**
     * Creates beans.xml deployment descriptor. (override method for bean-discovery-mode="all" support )
     * @param j2eeProfile Java EE profile to specify which version of beans.xml should be created
     * @param dir Directory where beans.xml should be created
     * @param name name of configuration file to create; should be always "beans" for now
     * @return beans.xml file as FileObject
     * @throws java.io.IOException
     */
    public static FileObject createBeansXml(Profile j2eeProfile, FileObject dir, String name) throws IOException {
        String template = null;
        if (Profile.JAVA_EE_6_FULL == j2eeProfile || Profile.JAVA_EE_6_WEB == j2eeProfile) {
            return DDHelper.createBeansXml(j2eeProfile, dir, name);
        }
        if (Profile.JAVA_EE_7_FULL == j2eeProfile || Profile.JAVA_EE_7_WEB == j2eeProfile) {
            template = "beans.xml"; //NOI18N
        }

        if (template == null)
            return null;

       return FileUtil.copyFile(CDIUtil.class.getResourceAsStream(RESOURCE_FOLDER + template), dir, name+".xml");
    }

}
