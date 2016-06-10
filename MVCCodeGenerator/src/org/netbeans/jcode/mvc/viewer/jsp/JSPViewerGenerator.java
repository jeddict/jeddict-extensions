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
package org.netbeans.jcode.mvc.viewer.jsp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import static java.util.stream.Collectors.toSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.lang.StringUtils;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.jcode.console.Console;
import static org.netbeans.jcode.console.Console.BOLD;
import static org.netbeans.jcode.console.Console.FG_MAGENTA;
import static org.netbeans.jcode.console.Console.FG_RED;
import static org.netbeans.jcode.console.Console.UNDERLINE;
import org.netbeans.jcode.core.util.FileUtil;
import org.netbeans.jcode.core.util.StringHelper;
import org.netbeans.jcode.entity.info.EntityResourceBeanModel;
import org.netbeans.jcode.layer.ConfigData;
import org.netbeans.jcode.layer.Generator;
import org.netbeans.jcode.layer.Technology;
import static org.netbeans.jcode.layer.Technology.Type.VIEWER;
import org.netbeans.jcode.mvc.controller.MVCControllerGenerator;
import org.netbeans.modules.j2ee.core.api.support.java.JavaIdentifiers;
import org.netbeans.jcode.mvc.controller.MVCData;
import org.netbeans.jcode.mvc.viewer.dto.FromEntityBase;
import org.netbeans.jcode.mvc.controller.Operation;
import org.netbeans.jcode.mvc.controller.event.ControllerEventType;
import org.netbeans.jcode.servlet.util.ServletUtil;
import org.netbeans.jcode.task.progress.ProgressHandler;
import org.netbeans.modules.web.api.webmodule.WebProjectConstants;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Gaurav Gupta
 */
@ServiceProvider(service=Generator.class)
@Technology(type=VIEWER, label="JSP", panel=JSPPanel.class, parents={MVCControllerGenerator.class})
public class JSPViewerGenerator implements Generator{

    private static final String TEMPLATE_PATH = "org/netbeans/jcode/mvc/viewer/resources/"; //NOI18N
    private static final String CRUD_HOME_PATH = "views/"; //NOI18N
    private static final String COMMON_TEMPLATE_PATH = "views/common/";
    private static final String CRUD_PATH = "views/entity/"; //NOI18N

    private static final Map<Operation, String> GENERATED_CRUD_FILES = new HashMap<>();
    private static final Map<String, String> TEMPLATE_PATTERN_FILES = new HashMap<>();
    private static final Map<Operation, String> CRUD_FILES = new HashMap<>();

    private static final String TEMPALTE_EXT = ".ftl"; //NOI18N
    private static final String JSP_EXT = ".jsp"; //NOI18N
    private static final String JSPF_EXT = ".jspf"; //NOI18N

    private static final String DEFAULT_GENERATED_CRUD_PATH = "views/"; //NOI18N
    public static final String TARGET_COMMON_TEMPLATE_PATH = "common/";

    static {
         final String HEADER = "header"; //NOI18N
        final String NAVIGATIONBAR = "navigationbar"; //NOI18N
        final String ERROR = "error"; //NOI18N
        final String FOOTER = "footer"; //NOI18N

        TEMPLATE_PATTERN_FILES.put(HEADER + TEMPALTE_EXT, TARGET_COMMON_TEMPLATE_PATH + HEADER + JSPF_EXT);
        TEMPLATE_PATTERN_FILES.put(NAVIGATIONBAR + TEMPALTE_EXT, TARGET_COMMON_TEMPLATE_PATH + NAVIGATIONBAR + JSPF_EXT);
        TEMPLATE_PATTERN_FILES.put(ERROR + TEMPALTE_EXT, TARGET_COMMON_TEMPLATE_PATH + ERROR + JSP_EXT);
        TEMPLATE_PATTERN_FILES.put(FOOTER + TEMPALTE_EXT, TARGET_COMMON_TEMPLATE_PATH + FOOTER + JSPF_EXT);

        final String CREATE = "create.ftl"; //NOI18N
        final String UPDATE = "update.ftl"; //NOI18N
        final String FIND = "list.ftl"; //NOI18N
        final String VIEW = "view.ftl"; //NOI18N

        CRUD_FILES.put(Operation.CREATE, CREATE);
        CRUD_FILES.put(Operation.UPDATE, UPDATE);
        CRUD_FILES.put(Operation.FIND_ALL, FIND);
        CRUD_FILES.put(Operation.FIND, VIEW);
        GENERATED_CRUD_FILES.put(Operation.CREATE, "create");
        GENERATED_CRUD_FILES.put(Operation.UPDATE, "update");
        GENERATED_CRUD_FILES.put(Operation.FIND_ALL, "list");
        GENERATED_CRUD_FILES.put(Operation.FIND, "view");
    }

    @ConfigData
    private JSPData jspData;
    @ConfigData
    private MVCData mvcData;
        
    private static final String WEB_XML_DD = "/org/netbeans/jcode/mvc/dd/welcomefile/web.xml";

    @Override
    public void execute(Project project,SourceGroup source, EntityResourceBeanModel model, ProgressHandler handler) throws IOException {
       Set<String> entities = model.getEntityInfos().stream().map(ei -> ei.getType()).collect(toSet());
        generateCRUD(project, entities, true, handler);
        generateHome(project, entities, handler);
        generateWelcomeFileDD(project, handler);
        generateStaticResources(project, handler);
    }

    
    public void generateWelcomeFileDD(Project project, ProgressHandler handler) throws IOException {
        String welcomeFile = null;
        if (mvcData.isAuthentication()) {
            welcomeFile = "/index.jsp";
        } else {
            welcomeFile = '/' + jspData.getFolder() + "/home.jsp";
        }
        boolean sucess = ServletUtil.setWelcomeFiles(project, welcomeFile);
        if (!sucess) { // NetBeans API bug resolution
            handler.progress(Console.wrap(NbBundle.getMessage(ServletUtil.class, "MSG_Init_WelcomeFile", jspData.getFolder()), FG_MAGENTA, BOLD, UNDERLINE));
            Map<String, Object> params = new HashMap<>();
            params.put("WELCOME_FILE", welcomeFile);
            ServletUtil.createDD(project, WEB_XML_DD, params);
        }
        handler.progress(Console.wrap(NbBundle.getMessage(ServletUtil.class, "MSG_Progress_WelcomeFile", jspData.getFolder()), FG_MAGENTA, BOLD, UNDERLINE));
    }

    public void generateStaticResources(Project project, ProgressHandler handler) throws IOException {
        handler.append(Console.wrap(JSPViewerGenerator.class, "MSG_Copying_Static_Files", FG_RED, BOLD, UNDERLINE));
        Sources sources = ProjectUtils.getSources(project);
        SourceGroup sourceGroups[] = sources.getSourceGroups(WebProjectConstants.TYPE_DOC_ROOT);
        FileObject webRoot = sourceGroups[0].getRootFolder();
        if (!jspData.isOnlineTheme()) {
            copyStaticResource("lib-resources", webRoot, jspData.getResourceFolder(), handler);
        }
        copyStaticResource("theme-resources", webRoot,  jspData.getResourceFolder(), handler);

        Map<String, Object> params = new HashMap<>();
        params.put("webPath", jspData.getFolder());
        params.put("resourcePath", jspData.getResourceFolder());
        String applicationPath = mvcData.getRestConfigData() == null ? "" : mvcData.getRestConfigData().getApplicationPath();
        params.put("applicationPath", applicationPath);
        params.put("CSRFPrevention", mvcData.isCSRF());
        params.put("XSSPrevention", mvcData.isXSS());
        params.put("Authentication", mvcData.isAuthentication());
        params.put("online", jspData.isOnlineTheme());

        handler.append(Console.wrap(JSPViewerGenerator.class, "MSG_Generating_Static_Template", FG_RED, BOLD));
        for (Entry<String, String> entry : TEMPLATE_PATTERN_FILES.entrySet()) {
            String targetPath =  jspData.getResourceFolder() + '/' + entry.getValue();
//            if (webRoot.getFileObject(targetPath) == null) {
                expandSingleJSPTemplate(TEMPLATE_PATH + COMMON_TEMPLATE_PATH + entry.getKey(),
                        targetPath, webRoot, params, handler);
//            }
        }
    }

    private void copyStaticResource(String inputRes, FileObject webRoot, String folder, ProgressHandler handler) throws IOException {
        InputStream stream = JSPViewerGenerator.class.getClassLoader().getResourceAsStream(TEMPLATE_PATH + inputRes + ".zip");
        try (ZipInputStream inputStream = new ZipInputStream(stream)) {
            ZipEntry entry;
            while ((entry = inputStream.getNextEntry()) != null) {
                if (entry.getName().lastIndexOf('.') == -1) { //skip if not file
                    continue;
                }
                String targetPath = folder + '/' +entry.getName();
                handler.progress(targetPath);

                FileObject target = org.openide.filesystems.FileUtil.createData(webRoot, targetPath);
                FileLock lock = target.lock();
                try (OutputStream outputStream = target.getOutputStream(lock)) {
                    for (int c = inputStream.read(); c != -1; c = inputStream.read()) {
                        outputStream.write(c);
                    }
                    inputStream.closeEntry();
                } finally {
                    lock.releaseLock();
                }
            }
        }
    }

    public void generateCRUD(final Project project, Set<String> entities, boolean overrideExisting, ProgressHandler handler) throws IOException {
        
        handler.progress(Console.wrap(JSPViewerGenerator.class, "MSG_Generating_CRUD_Template", FG_RED, BOLD, UNDERLINE));
        for (String entityFQN : entities) {
        
        Sources sources = ProjectUtils.getSources(project);
        SourceGroup sourceGroups[] = sources.getSourceGroups(WebProjectConstants.TYPE_DOC_ROOT);
        FileObject webRoot = sourceGroups[0].getRootFolder();
        String entityClass = entityFQN;
        String crudPath = jspData.getFolder();
        String jspEntityIncludeFolder;
        if (StringUtils.isNotBlank(crudPath)) {
            jspEntityIncludeFolder = "/" + crudPath;
        } else {
            jspEntityIncludeFolder = "/" + DEFAULT_GENERATED_CRUD_PATH;
        }

        Map<String, Object> params = FromEntityBase.createFieldParameters(webRoot, entityClass, entityClass, null, false, true);
        params.put("CSRFPrevention", mvcData.isCSRF());
        params.put("XSSPrevention", mvcData.isXSS());
        params.put("webPath", jspData.getFolder());
        params.put("resourcePath", jspData.getResourceFolder());

        for (Entry<Operation, String> entry : CRUD_FILES.entrySet()) {
            expandSingleJSPTemplate(TEMPLATE_PATH + CRUD_PATH + entry.getValue(),
                    getJSPFileName(entityClass,jspEntityIncludeFolder, GENERATED_CRUD_FILES.get(entry.getKey())) + JSP_EXT,
                    webRoot, params, handler);
        }
        }
    }

    public void generateHome(final Project project,
            final Set<String> entities, ProgressHandler handler) throws IOException {
        Sources srcs = ProjectUtils.getSources(project);
        SourceGroup sgWeb[] = srcs.getSourceGroups(WebProjectConstants.TYPE_DOC_ROOT);
        FileObject webRoot = sgWeb[0].getRootFolder();
        String crudPath = jspData.getFolder();
        String jspEntityIncludeFolder;
        if (StringUtils.isNotBlank(crudPath)) {
            jspEntityIncludeFolder = "/" + crudPath;
        } else {
            jspEntityIncludeFolder = "/" + DEFAULT_GENERATED_CRUD_PATH;
        }

        Map<String, Object> params = new LinkedHashMap<>();
        Map<String, String> entityVarMapping = new LinkedHashMap<>();
        entities.stream().forEach((entity) -> {
            String entityName = JavaIdentifiers.unqualify(entity);
            entityVarMapping.put(StringHelper.firstLower(entityName), entityName);// "person", "Person"
        });
        params.put("entities", entityVarMapping);
        params.put("online", jspData.isOnlineTheme());
        params.put("webPath", jspData.getFolder());
        params.put("resourcePath", jspData.getResourceFolder());
        params.put("applicationPath", mvcData.getRestConfigData().getApplicationPath());

        expandSingleJSPTemplate(TEMPLATE_PATH + CRUD_HOME_PATH + "home.ftl",
                getJSPFileName(null, jspEntityIncludeFolder, "home") + JSP_EXT,
                webRoot, params, handler);
        
        expandSingleJSPTemplate(TEMPLATE_PATH + CRUD_HOME_PATH + "login.ftl",
                getJSPFileName(null, jspEntityIncludeFolder, "login") + JSP_EXT,
                webRoot, params, handler);
        
        if(mvcData.isAuthentication()){
        expandSingleJSPTemplate(TEMPLATE_PATH + CRUD_HOME_PATH + "index.ftl",
                "index" + JSP_EXT,
                webRoot, params, handler);
        }

    }

    private static void expandSingleJSPTemplate(String inputTemplatePath, String outputFilePath,
            FileObject webRoot, Map<String, Object> params, ProgressHandler handler) throws IOException {
         handler.progress(outputFilePath);

        FileUtil.expandTemplate(inputTemplatePath, webRoot, outputFilePath, params);
    }

    private static String getJSPFileName(String entityClass, String jspFolder, String name) {
        if (StringUtils.isNotBlank(entityClass)) {
            String simpleClassName = JavaIdentifiers.unqualify(entityClass);
            entityClass = simpleClassName.substring(0, 1).toLowerCase() + simpleClassName.substring(1);
        }
        if (jspFolder.endsWith("/")) {
            jspFolder = jspFolder.substring(0, jspFolder.length() - 1);
        }
        if (jspFolder.startsWith("/")) {
            jspFolder = jspFolder.substring(1);
        }
        if (StringUtils.isNotBlank(entityClass)) {
            if (jspFolder.length() > 0) {
                return jspFolder + "/" + entityClass + "/" + name;
            } else {
                return entityClass + "/" + name;
            }
        } else {
            if (jspFolder.length() > 0) {
                return jspFolder + "/" + name;
            } else {
                return name;
            }
        }
    }

}
