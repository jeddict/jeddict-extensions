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
package org.netbeans.jcode.angular2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import static java.util.stream.Collectors.toList;
import org.netbeans.jcode.angular2.domain.NG2ApplicationConfig;
import org.netbeans.jcode.angular2.domain.NG2Entity;
import org.netbeans.jcode.angular2.domain.NG2Relationship;
import org.netbeans.jcode.angular2.domain.NG2Field;
import org.netbeans.jcode.console.Console;
import static org.netbeans.jcode.console.Console.BOLD;
import static org.netbeans.jcode.console.Console.FG_RED;
import static org.netbeans.jcode.console.Console.UNDERLINE;
import static org.netbeans.jcode.core.util.FileUtil.getFileExt;
import org.netbeans.jcode.core.util.POMManager;
import static org.netbeans.jcode.core.util.ProjectHelper.getProjectWebRoot;
import org.netbeans.jcode.core.util.SourceGroupSupport;
import org.netbeans.jcode.layer.Technology;
import static org.netbeans.jcode.layer.Technology.Type.VIEWER;
import org.netbeans.jcode.rest.controller.RESTGenerator;
import org.openide.util.lookup.ServiceProvider;
import org.netbeans.jcode.layer.Generator;
import org.netbeans.jcode.ng.main.AngularGenerator;
import static org.netbeans.jcode.ng.main.AngularUtil.copyDynamicFile;
import static org.netbeans.jcode.ng.main.AngularUtil.copyDynamicResource;
import static org.netbeans.jcode.ng.main.AngularUtil.getResource;
import static org.netbeans.jcode.ng.main.AngularUtil.insertNeedle;
import org.netbeans.jcode.ng.main.domain.ApplicationSourceFilter;
import org.netbeans.jcode.ng.main.domain.EntityConfig;
import org.netbeans.jcode.ng.main.domain.NGApplicationConfig;
import org.netbeans.jcode.ng.main.domain.NGEntity;
import org.netbeans.jcode.ng.main.domain.NGField;
import org.netbeans.jcode.ng.main.domain.NGRelationship;
import org.netbeans.jcode.ng.main.domain.Needle;
import org.netbeans.jcode.ng.main.domain.NeedleFile;
import org.netbeans.jcode.parser.ejs.EJSParser;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.extend.BaseAttribute;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Gaurav Gupta
 */
@ServiceProvider(service = Generator.class)
@Technology(type = VIEWER, label = "Angular", panel = Angular2Panel.class, parents = {RESTGenerator.class})
public class Angular2Generator extends AngularGenerator {

    private static final String TEMPLATE = "org/netbeans/jcode/angular2/template/";
    private static final String CLIENT_FRAMEWORK = "angular2";
    private FileObject projectRoot;
    private FileObject webRoot;
    private ApplicationSourceFilter fileFilter;

    private final Function<String, String> PATH_RESOLVER = (templatePath) -> {
        String ext = getFileExt(templatePath);
        if (!fileFilter.isEnable(templatePath)) {
            return null;
        }
        if (templatePath.contains("/_")) {
            templatePath = templatePath.replaceAll("/_", "/");
        } else if (templatePath.charAt(0) == '_') { //_index.html
            templatePath = templatePath.substring(1);
        }
        return templatePath;
    };

    @Override
    protected void generateClientSideComponent() {
        try {
            NGApplicationConfig applicationConfig = getAppConfig();
            fileFilter = getApplicationSourceFilter(applicationConfig);
            projectRoot = project.getProjectDirectory();
            webRoot = getProjectWebRoot(project);
            handler.append(Console.wrap(AngularGenerator.class, "MSG_Copying_Entity_Files", FG_RED, BOLD, UNDERLINE));
            Map<String, String> templateLib = getResource(getTemplatePath() + "entity-include-resources.zip");
            List<NGEntity> ngEntities = new ArrayList<>();
            List<Entity> entities = entityMapping.getGeneratedEntity().collect(toList());
            for (Entity entity : entities) {
                NGEntity ngEntity = getEntity(applicationConfig.getAngularAppName(), entity);
                if (ngEntity != null) {
                    ngEntities.add(ngEntity);
                    generateNgEntity(applicationConfig, getEntityConfig(), ngEntity, templateLib);
                    generateNgEntityTest(applicationConfig, getEntityConfig(), ngEntity);
                    generateNgEntityi18nResource(applicationConfig, fileFilter, ngEntity);
                }
            }
            applicationConfig.setEntities(ngEntities);

            if (appConfigData.isCompleteApplication()) {
                EJSParser parser = new EJSParser();
                parser.addContext(applicationConfig);
                generateNgApplication(parser);
                generateNgTest(parser);
                generateNgApplicationi18nResource(applicationConfig, fileFilter);
//              generateNgLocaleResource(applicationConfig);
                generateNgHome(parser);

                addMavenDependencies("pom/_pom.xml");
            }

            updateNgEntityNeedle(applicationConfig, ngEntities);

//            installYarn(project.getProjectDirectory());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

//    public void installYarn(FileObject workingFolder){
//        handler.append(Console.wrap(Angular2Generator.class, "YARN_INSTALL", FG_RED, BOLD, UNDERLINE));
//        executeCommand(workingFolder,handler, "yarn", "install");
//    }
    protected void generateNgEntity(NGApplicationConfig applicationConfig,
            EntityConfig config, NGEntity entity, Map<String, String> templateLib) throws IOException {
        EJSParser parser = new EJSParser();
        parser.addContext(applicationConfig);
        parser.addContext(entity);
        parser.addContext(config);

        parser.setImportTemplate(templateLib);
        parser.eval("function toArrayString(array) { return '[\\'' + array.join('\\',\\'') + '\\']' }");//external util function
        copyDynamicResource(getParserManager(parser), getTemplatePath() + "entity-resources.zip", webRoot, getEntityPathResolver(entity), handler);
    }

    private Function<String, String> getEntityPathResolver(NGEntity entity) {
        Function<String, String> ENTITY_PATH_RESOLVER = (templatePath) -> {
            String ext = templatePath.substring(templatePath.lastIndexOf('.') + 1);
            if (!fileFilter.isEnable(templatePath)) {
                return null;
            }
            if (templatePath.contains("_entity-management")) {
                templatePath = templatePath.replace("_entity-management", entity.getEntityFolderName() + '/' + entity.getEntityFileName());
            } else if (templatePath.contains("_entity.service.ts")
                    || templatePath.contains("_entity.model.ts")
                    || templatePath.contains("_entity-popup.service.ts")) {
                templatePath = templatePath.replace("_entity", entity.getEntityFolderName() + '/' + entity.getEntityServiceFileName());
            } else if (templatePath.contains("_index.ts")) {
                templatePath = templatePath.replace("_index", entity.getEntityFolderName() + "/index");
            } else if (templatePath.contains("_entity.spec.ts")) {
                templatePath = templatePath.replace("_entity", entity.getEntityServiceFileName());
            }

            return templatePath;
        };
        return ENTITY_PATH_RESOLVER;
    }

    private void updateNgEntityNeedle(NGApplicationConfig applicationConfig, List<NGEntity> ngEntities) {
        for (NeedleFile needleFilefile : getNeedleFiles()) {
            for (String file : needleFilefile.getFile()) {
                needleFilefile.getNeedles().stream()
                        .forEach(needle -> insertNeedle(webRoot, file, needle.getInsertPointer(), needle.getTemplate(applicationConfig, ngEntities), handler));
            }
        }
    }

    private List<NeedleFile> getNeedleFiles() {
        NeedleFile ENTITY_MODULE_TS = new NeedleFile("app/entities/entity.module.ts");
        ENTITY_MODULE_TS.setNeedles(Arrays.asList(//                new Needle("needle-add-entity-to-module-import", "\t\t${entityClass}Service,\n"
                //                        + "\t\t${entityAngularJSName}Component,\n"
                //                        + "\t\t${entityAngularJSName}DetailComponent,\n"
                //                        + "\t\t${entityAngularJSName}DialogComponent,\n"
                //                        + "\t\t${entityAngularJSName}DeleteDialogComponent,\n"
                //                        + "\t\t${entityInstance}State,\n"
                //                        + "\t\t${entityInstance}DetailState,\n"
                //                        + "\t\t${entityInstance}NewState,\n"
                //                        + "\t\t${entityInstance}EditState,\n"
                //                        + "\t\t${entityInstance}DeleteState,"),
                //                new Needle("needle-add-entity-to-module-states", "\t\t${entityInstance}State,\n"
                //                        + "\t\t${entityInstance}NewState,\n"
                //                        + "\t\t${entityInstance}DetailState,\n"
                //                        + "\t\t${entityInstance}EditState,\n"
                //                        + "\t\t${entityInstance}DeleteState,"),
                //                new Needle("needle-add-entity-to-module-declarations", "\t\t\t\t${entityAngularJSName}Component,\n"
                //                        + "\t\t\t\t${entityAngularJSName}DetailComponent,\n"
                //                        + "\t\t\t\t${entityAngularJSName}DialogComponent,\n"
                //                        + "\t\t\t\t${entityAngularJSName}DeleteDialogComponent,"),
                //                new Needle("needle-add-entity-to-module-entryComponents", "\t\t\t\t${entityAngularJSName}DialogComponent,\n"
                //                        + "\t\t\t\t${entityAngularJSName}DeleteDialogComponent,"),
                //                new Needle("needle-add-entity-to-module-providers", "\t\t\t\t${entityClass}Service,"),
                //appName -> getAngular2AppName
                new Needle("needle-add-entity-module-import", "import { ${appName}${entityAngularJSName}Module } from './${entityFolderName}/${entityFileName}.module';"),
                new Needle("needle-add-entity-module", "\t${appName}${entityAngularJSName}Module,")
        ));

//        NeedleFile INDEX_TS = new NeedleFile("app/entities/index.ts");
//        INDEX_TS.setNeedles(Arrays.asList(
//                new Needle("needle-add-entity-to-index-model-export", "export * from './${entityFolderName}/${entityFileName}.model';"),
//                new Needle("needle-add-entity-to-index-service-export", "export * from './${entityFolderName}/${entityFileName}.service';"),
//                new Needle("needle-add-entity-to-index-export", "export * from './${entityFolderName}/${entityFileName}-dialog.component';\n"
//                        + "export * from './${entityFolderName}/${entityFileName}-delete-dialog.component';\n"
//                        + "export * from './${entityFolderName}/${entityFileName}-detail.component';\n"
//                        + "export * from './${entityFolderName}/${entityFileName}.component';\n"
//                        + "export * from './${entityFolderName}/${entityFileName}.state';")
//        ));
        NeedleFile NAVBAR_COMPONENT_HTML = new NeedleFile("app/layouts/navbar/navbar.component.html");
        NAVBAR_COMPONENT_HTML.setNeedles(Arrays.asList(new Needle("needle-add-entity-to-menu",
                "                    <li uiSrefActive=\"active\">\n"
                + "                        <a class=\"dropdown-item\" routerLink=\"${routerName}\" (click)=\"collapseNavbar()\">\n"
                + "                            <i class=\"fa fa-fw fa-asterisk\" aria-hidden=\"true\"></i>\n"
                + "                            <span <#if enableTranslation>${prefix}Translate=\"global.menu.entities.${camelCase_routerName}\"</#if>>${startCase_routerName}</span>\n"
                + "                        </a>\n"
                + "                    </li>")
        ));

        NeedleFile GLOBAL_JSON = new NeedleFile("i18n/en/global.json");
        GLOBAL_JSON.setNeedles(Arrays.asList(//                new Needle("needle-menu-add-entry", "\t\t\t\t\t\t\t\t\"${camelCase_routerName}\" : \"${startCase_routerName}\",")
                new Needle("needle-menu-add-entry", "\t\t\t\t\t\t\t\t\"${entityTranslationKeyMenu}\": \"${startCase_entityClass}\",")
        ));

//        NeedleFile LANGUAGE_CONSTANTS_TS = new NeedleFile("app/shared/language/language.constants.ts");
//        LANGUAGE_CONSTANTS_TS.setNeedles(Arrays.asList(
//                new Needle("needle-i18n-language-constant", "\"${camelCase_routerName}\" : \"${startCase_routerName}\",")
//        ));
        return Arrays.asList(ENTITY_MODULE_TS, NAVBAR_COMPONENT_HTML, GLOBAL_JSON);
    }

    private void addMavenDependencies(String pom) {
        if (POMManager.isMavenProject(project)) {
            POMManager pomManager = new POMManager(TEMPLATE + pom, project);
            pomManager.commit();
        } else {
            handler.warning(NbBundle.getMessage(Angular2Generator.class, "TITLE_Maven_Project_Not_Found"), NbBundle.getMessage(Angular2Generator.class, "MSG_Maven_Project_Not_Found"));
        }
    }

    private void generateNgHome(EJSParser parser) throws IOException {
        Function<String, String> pathResolver = templatePath -> templatePath.replace("_", "");// "_index.html" ->  "index.html";
        copyDynamicResource(getParserManager(parser), getTemplatePath() + "project-resources.zip", projectRoot, pathResolver, handler);

//        parser.setDelimiter('#');//nested template
//        copyDynamicFile(getParserManager(parser), getTemplatePath() + "_index.html", webRoot, "index.html", handler);
        FileObject nbactionsFile = projectRoot.getFileObject("nbactions.xml");
        if (nbactionsFile == null) {
            copyDynamicFile(getParserManager(parser), getTemplatePath() + "webpack_nbactions.xml", projectRoot, "nbactions.xml", handler);
        }
        handler.info(NbBundle.getMessage(Angular2Generator.class, "ACTIVATE_PROFILE"),
                NbBundle.getMessage(Angular2Generator.class, "ACTIVATE_WEBPACK_PROFILE"));

//        removeUnwantedFile();
    }

    private void removeUnwantedFile() throws IOException {
//        rename index.html
        FileObject indexFile = webRoot.getFileObject("index.html");
        if (indexFile != null) {
            FileLock lock = indexFile.lock();
            indexFile.rename(lock, "index_" + new Date().getTime(), "html");
            lock.releaseLock();
        }
    }

    protected void generateNgApplication(EJSParser parser) throws IOException {
        handler.append(Console.wrap(AngularGenerator.class, "MSG_Copying_Application_Files", FG_RED, BOLD, UNDERLINE));
        List<String> skipList = Arrays.asList("_language.pipe.ts");//charset issue
        copyDynamicResource(getParserManager(parser, skipList), getTemplatePath() + "web-resources.zip", webRoot, PATH_RESOLVER, handler);
    }

    protected void generateNgTest(EJSParser parser) throws IOException {
        FileObject testRoot = SourceGroupSupport.getTestSourceGroup(project).getRootFolder().getParent();//test/java => ../java
//        FileObject javascriptRoot = testRoot.createFolder("javascript");
        copyDynamicResource(getParserManager(parser), getTemplatePath() + "karma-test.zip", testRoot, PATH_RESOLVER, handler);
        if (ngData.isProtractorTest()) {
            copyDynamicResource(getParserManager(parser), getTemplatePath() + "protractor-test.zip", testRoot, PATH_RESOLVER, handler);
        }
    }

    protected void generateNgEntityTest(NGApplicationConfig applicationConfig, EntityConfig config, NGEntity entity) throws IOException {
        EJSParser parser = new EJSParser();
        parser.addContext(applicationConfig);
        parser.addContext(entity);
        parser.addContext(config);

        FileObject testRoot = SourceGroupSupport.getTestSourceGroup(project).getRootFolder().getParent();//test/java => ../java
        copyDynamicResource(getParserManager(parser), getTemplatePath() + "entity-karma-test.zip", testRoot, getEntityPathResolver(entity), handler);
        if (ngData.isProtractorTest()) {
            copyDynamicResource(getParserManager(parser), getTemplatePath() + "entity-protractor-test.zip", testRoot, getEntityPathResolver(entity), handler);
        }
    }

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
        return new NG2SourceFilter(applicationConfig);
    }

    @Override
    protected NGApplicationConfig getAppConfig() {
        NGApplicationConfig config = super.getAppConfig();
        config.setClientPackageManager("yarn");
        config.setProtractorTests(ngData.isProtractorTest());
        return config;
    }

    @Override
    public NGApplicationConfig getNGApplicationConfig() {
        return new NG2ApplicationConfig();
    }

    @Override
    public NGEntity getNGEntity(String name, String entityAngularJSSuffix) {
        return new NG2Entity(name, entityAngularJSSuffix);
    }

    @Override
    public NGRelationship getNGRelationship(String angularAppName, String entityAngularJSSuffix, Entity entity, RelationAttribute relation) {
        return new NG2Relationship(angularAppName, entityAngularJSSuffix, entity, relation);
    }

    @Override
    public NGField getNGField(BaseAttribute attribute) {
        return new NG2Field(attribute);
    }

}
