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
package io.github.jeddict.mvc.viewer.jsp;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import static java.util.stream.Collectors.toSet;
import org.apache.commons.lang3.StringUtils;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import io.github.jeddict.jcode.console.Console;
import static io.github.jeddict.jcode.console.Console.BOLD;
import static io.github.jeddict.jcode.console.Console.FG_MAGENTA;
import static io.github.jeddict.jcode.console.Console.FG_DARK_RED;
import static io.github.jeddict.jcode.console.Console.UNDERLINE;
import io.github.jeddict.jcode.util.FileUtil;
import io.github.jeddict.jcode.util.StringHelper;
import io.github.jeddict.jcode.Generator;
import io.github.jeddict.mvc.controller.MVCControllerGenerator;
import io.github.jeddict.mvc.controller.MVCData;
import io.github.jeddict.mvc.controller.Operation;
import io.github.jeddict.mvc.viewer.dto.FromEntityBase;
import io.github.jeddict.jcode.ApplicationConfigData;
import io.github.jeddict.jcode.annotation.ConfigData;
import io.github.jeddict.jcode.annotation.Technology;
import static io.github.jeddict.jcode.annotation.Technology.Type.VIEWER;
import io.github.jeddict.jcode.task.progress.ProgressHandler;
import io.github.jeddict.jcode.util.WebDDUtil;
import static io.github.jeddict.jcode.util.WebDDUtil.DD_NAME;
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.EntityMappings;
import org.netbeans.modules.j2ee.core.api.support.java.JavaIdentifiers;
import org.netbeans.modules.web.api.webmodule.WebProjectConstants;
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

    private static final String TEMPLATE_PATH = "io/github/jeddict/mvc/viewer/template/"; //NOI18N
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
        
    @ConfigData
    private EntityMappings entityMappings;
    
    @ConfigData
    private ProgressHandler handler;
    
    @ConfigData
    private ApplicationConfigData appConfigData;
    
    private Project project; 
    
    private static final String WEB_XML_DD = "/io/github/jeddict/mvc/template/dd/welcomefile/_web.xml";

    @Override
    public void execute() throws IOException {
        project = appConfigData.getTargetProject();
        Set<String> entities = entityMappings.getGeneratedEntity()
                .map(Entity::getFQN)
                .collect(toSet());
        generateCRUD(entities, true);
        generateHome(entities);
        if (appConfigData.isCompleteApplication()) {
            generateWelcomeFileDD();
            generateStaticResources(project, handler);
        }
    }

    
    private void generateWelcomeFileDD() throws IOException {
        String welcomeFile;
        if (mvcData.isAuthentication()) {
            welcomeFile = "/index.jsp";
        } else {
//            welcomeFile = '/' + jspData.getFolder() + "/home.jsp";
            welcomeFile = "/home.jsp";
        }
        boolean success = WebDDUtil.setWelcomeFiles(project, welcomeFile);
        if (!success) { // NetBeans API bug resolution
            handler.progress(Console.wrap(NbBundle.getMessage(WebDDUtil.class, "MSG_Init_WelcomeFile", jspData.getFolder()), FG_MAGENTA, BOLD, UNDERLINE));
            Map<String, Object> params = new HashMap<>();
            params.put("WELCOME_FILE", welcomeFile);
            WebDDUtil.createDD(project, WEB_XML_DD, params, targetDir -> true);
        }
        handler.progress(Console.wrap(NbBundle.getMessage(WebDDUtil.class, "MSG_Progress_WelcomeFile", jspData.getFolder()), FG_MAGENTA, BOLD, UNDERLINE));
    }

    public void generateStaticResources(Project project, ProgressHandler handler) throws IOException {
        handler.append(Console.wrap(JSPViewerGenerator.class, "MSG_Copying_Static_Files", FG_DARK_RED, BOLD, UNDERLINE));
        Sources sources = ProjectUtils.getSources(project);
        SourceGroup sourceGroups[] = sources.getSourceGroups(WebProjectConstants.TYPE_DOC_ROOT);
        FileObject webRoot = sourceGroups[0].getRootFolder();
        if (!jspData.isOnlineTheme()) {
            FileUtil.copyStaticResource(TEMPLATE_PATH + "lib-resources.zip", webRoot, jspData.getResourceFolder(), handler);
        }
        FileUtil.copyStaticResource(TEMPLATE_PATH + "theme-resources.zip", webRoot,  jspData.getResourceFolder(), handler);

        Map<String, Object> params = new HashMap<>();
        params.put("webPath", jspData.getFolder());
        params.put("resourcePath", jspData.getResourceFolder());
        String applicationPath = mvcData.getRestConfigData() == null ? "" : mvcData.getRestConfigData().getApplicationPath();
        params.put("applicationPath", applicationPath);
        params.put("CSRFPrevention", mvcData.isCSRF());
        params.put("XSSPrevention", mvcData.isXSS());
        params.put("Authentication", mvcData.isAuthentication());
        params.put("online", jspData.isOnlineTheme());

        handler.append(Console.wrap(JSPViewerGenerator.class, "MSG_Generating_Static_Template", FG_DARK_RED, BOLD));
        for (Entry<String, String> entry : TEMPLATE_PATTERN_FILES.entrySet()) {
            String targetPath =  jspData.getResourceFolder() + '/' + entry.getValue();
//            if (webRoot.getFileObject(targetPath) == null) {
                expandSingleJSPTemplate(TEMPLATE_PATH + COMMON_TEMPLATE_PATH + entry.getKey(),
                        targetPath, webRoot, params, handler);
//            }
        }
    }

    public void generateCRUD(Set<String> entities, boolean overrideExisting) throws IOException {
        
        handler.progress(Console.wrap(JSPViewerGenerator.class, "MSG_Generating_CRUD_Template", FG_DARK_RED, BOLD, UNDERLINE));
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

    public void generateHome(final Set<String> entities) throws IOException {
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
        entities.forEach((entity) -> {
            String entityName = JavaIdentifiers.unqualify(entity);
            entityVarMapping.put(StringHelper.firstLower(entityName), entityName);// "person", "Person"
        });
        params.put("entities", entityVarMapping);
        params.put("online", jspData.isOnlineTheme());
        params.put("webPath", jspData.getFolder());
        params.put("resourcePath", jspData.getResourceFolder());
        params.put("applicationPath", mvcData.getRestConfigData().getApplicationPath());

        expandSingleJSPTemplate(TEMPLATE_PATH + CRUD_HOME_PATH + "home.ftl",
                "home" + JSP_EXT,
                webRoot, params, handler);
        
        if(mvcData.isAuthentication()){
            expandSingleJSPTemplate(TEMPLATE_PATH + CRUD_HOME_PATH + "login.ftl",
                    getJSPFileName(null, jspEntityIncludeFolder, "login") + JSP_EXT,
                    webRoot, params, handler);


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
