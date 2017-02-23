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
package org.netbeans.jcode.angular1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import static java.util.stream.Collectors.toList;
import org.netbeans.jcode.console.Console;
import static org.netbeans.jcode.console.Console.BOLD;
import static org.netbeans.jcode.console.Console.FG_RED;
import static org.netbeans.jcode.console.Console.UNDERLINE;
import org.netbeans.jcode.core.util.FileUtil;
import static org.netbeans.jcode.core.util.FileUtil.getFileExt;
import static org.netbeans.jcode.core.util.FileUtil.getSimpleFileNameWithExt;
import static org.netbeans.jcode.core.util.ProjectHelper.getProjectWebRoot;
import org.netbeans.jcode.layer.Technology;
import static org.netbeans.jcode.layer.Technology.Type.VIEWER;
import org.netbeans.jcode.rest.controller.RESTGenerator;
import org.openide.util.lookup.ServiceProvider;
import org.netbeans.jcode.layer.Generator;
import org.netbeans.jcode.ng.main.AngularGenerator;
import org.netbeans.jcode.angular2.Angular2Panel;
import static org.netbeans.jcode.ng.main.AngularUtil.copyDynamicFile;
import static org.netbeans.jcode.ng.main.AngularUtil.copyDynamicResource;
import static org.netbeans.jcode.ng.main.AngularUtil.getResource;
import org.netbeans.jcode.ng.main.domain.ApplicationSourceFilter;
import org.netbeans.jcode.ng.main.domain.EntityConfig;
import org.netbeans.jcode.ng.main.domain.NGApplicationConfig;
import org.netbeans.jcode.ng.main.domain.NGEntity;
import org.netbeans.jcode.parser.ejs.EJSParser;
import org.netbeans.jpa.modeler.spec.Entity;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author Gaurav Gupta
 */
@ServiceProvider(service = Generator.class)
@Technology(type = VIEWER, label = "Angular JS 1", panel = Angular1Panel.class, parents = {RESTGenerator.class})
public class Angular1Generator extends AngularGenerator {

    private static final String TEMPLATE = "org/netbeans/jcode/angular1/template/";
    private static final String CLIENT_FRAMEWORK = "angular1";
    private ApplicationSourceFilter sourceFilter;

    @Override
    public String getTemplatePath() {
        return TEMPLATE;
    }

    @Override
    protected String getClientFramework() {
        return CLIENT_FRAMEWORK;
    }

    @Override
    protected ApplicationSourceFilter getApplicationSourceFilter(NGApplicationConfig applicationConfig) {
        if (sourceFilter == null) {
            sourceFilter = new NG1SourceFilter(applicationConfig);
        }
        return sourceFilter;
    }
    private final static String MODULE_JS = "app/app.module.js";

    @Override
    protected void generateClientSideComponent() {
        try {

            NGApplicationConfig applicationConfig = getAppConfig();
            ApplicationSourceFilter fileFilter = getApplicationSourceFilter(applicationConfig);

            handler.append(Console.wrap(AngularGenerator.class, "MSG_Copying_Entity_Files", FG_RED, BOLD, UNDERLINE));
            Map<String, String> templateLib = getResource(getTemplatePath() + "entity-include-resources.zip");
            List<NGEntity> entities = new ArrayList<>();
            for (Entity entity : entityMapping.getGeneratedEntity().collect(toList())) {
                NGEntity ngEntity = getEntity(entity);
                if (ngEntity != null) {
                    entities.add(ngEntity);
                    generateNgEntity(applicationConfig, fileFilter, getEntityConfig(), ngEntity, templateLib);
                    generateNgEntityi18nResource(applicationConfig, fileFilter, ngEntity);
                }
            }
            applicationConfig.setEntities(entities);

            if (appConfigData.isCompleteApplication()) {
                generateNgApplication(applicationConfig, fileFilter);
                generateNgApplicationi18nResource(applicationConfig, fileFilter);
                generateNgLocaleResource(applicationConfig, fileFilter);
                generateNgHome(applicationConfig, fileFilter);
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    protected void generateNgEntity(NGApplicationConfig applicationConfig, ApplicationSourceFilter fileFilter,
            EntityConfig config, NGEntity entity, Map<String, String> templateLib) throws IOException {
        FileObject webRoot = getProjectWebRoot(project);
        EJSParser parser = new EJSParser();
        parser.addContext(applicationConfig);
        parser.addContext(entity);
        parser.addContext(config);

        Function<String, String> pathResolver = (templatePath) -> {
            String simpleFileName = getSimpleFileNameWithExt(templatePath);
            String ext = templatePath.substring(templatePath.lastIndexOf('.') + 1);
            if (!fileFilter.isEnable(simpleFileName)) {
                return null;
            }
            if (templatePath.contains("_entity-management" + ".html")) {
                templatePath = templatePath.replace("_entity-management", entity.getEntityFolderName() + '/' + entity.getEntityPluralFileName());
            } else if (templatePath.contains("services/_entity.service.js")) {
                templatePath = templatePath.replace("services/_entity.service.js",
                        "entities/" + entity.getEntityFolderName() + '/' + entity.getEntityServiceFileName() + ".service.js");
            } else if (templatePath.contains("services/_entity-search.service.js")) {
                templatePath = templatePath.replace("services/_entity-search.service.js",
                        "entities/" + entity.getEntityFolderName() + '/' + entity.getEntityServiceFileName() + ".search.service.js");
            } else {
                templatePath = templatePath.replace("_entity-management", entity.getEntityFolderName() + '/' + entity.getEntityFileName());
            }

            if ("js".equals(ext)) {
                entityScriptFiles.add(templatePath);
            }
            return templatePath;
        };
        parser.setImportTemplate(templateLib);
        copyDynamicResource(getParserManager(parser), getTemplatePath() + "entity-resources.zip", webRoot, pathResolver, handler);
    }

    protected void generateNgLocaleResource(NGApplicationConfig applicationConfig, ApplicationSourceFilter fileFilter) throws IOException {
        FileObject webRoot = getProjectWebRoot(project);

        Map<String, Object> data = new HashMap();//todo remove

        EJSParser parser = new EJSParser();
        parser.addContext(applicationConfig);
        parser.addContext(data);

        Function<String, String> pathResolver = (templatePath) -> {
            String lang = templatePath.substring(templatePath.indexOf('_') + 1, templatePath.lastIndexOf('.')); //angular-locale_en.js 
            if (!applicationConfig.getLanguages().contains(lang)) { //if lang selected by dev 
                return null;
            }
            //path modification not required
            return templatePath;
        };
        copyDynamicResource(getParserManager(parser), getTemplatePath() + "angular-locale.zip", webRoot, pathResolver, handler);
    }

    private void generateNgHome(NGApplicationConfig applicationConfig, ApplicationSourceFilter fileFilter) throws IOException {
        FileObject webRoot = getProjectWebRoot(project);

        Map<String, Object> data = new HashMap();
        data.put("entityScriptFiles", entityScriptFiles);
        scriptFiles.remove(MODULE_JS);
        scriptFiles.add(0, MODULE_JS);
        data.put("scriptFiles", scriptFiles);

        EJSParser parser = new EJSParser();
        parser.addContext(applicationConfig);
        parser.addContext(data);

        copyDynamicFile(getParserManager(parser), getTemplatePath() + "_index.html", webRoot, "index.html", handler);
        copyDynamicFile(getParserManager(parser), getTemplatePath() + "_bower.json", project.getProjectDirectory(), "bower.json", handler);
        handler.append(Console.wrap(AngularGenerator.class, "MSG_Copying_Bower_Lib_Files", FG_RED, BOLD));
        FileUtil.copyStaticResource(getTemplatePath() + "bower_components.zip", webRoot, null, handler);
    }

    protected void generateNgApplication(NGApplicationConfig applicationConfig, ApplicationSourceFilter fileFilter) throws IOException {
        handler.append(Console.wrap(AngularGenerator.class, "MSG_Copying_Application_Files", FG_RED, BOLD, UNDERLINE));
        FileObject webRoot = getProjectWebRoot(project);

        EJSParser parser = new EJSParser();
        parser.addContext(applicationConfig);

        Function<String, String> pathResolver = (templatePath) -> {
            String simpleFileName = getSimpleFileNameWithExt(templatePath);
            String ext = getFileExt(templatePath);

            if (!templatePath.startsWith("app")) {
                if (!fileFilter.isEnable(templatePath)) {
                    return null;
                }
            } else if (!fileFilter.isEnable(simpleFileName)) {
                return null;
            }
            if (templatePath.contains("/_")) {
                templatePath = templatePath.replaceAll("/_", "/");
            }
            if ("js".equals(ext)) {
                scriptFiles.add(templatePath);
            }
            return templatePath;
        };
        copyDynamicResource(getParserManager(parser), getTemplatePath() + "web-resources.zip", webRoot, pathResolver, handler);
    }

}
