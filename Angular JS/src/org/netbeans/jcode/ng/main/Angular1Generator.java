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
package org.netbeans.jcode.ng.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.script.ScriptException;
import org.apache.commons.io.IOUtils;
import org.netbeans.api.project.Project;
import org.netbeans.jcode.console.Console;
import static org.netbeans.jcode.console.Console.BOLD;
import static org.netbeans.jcode.console.Console.FG_RED;
import static org.netbeans.jcode.console.Console.UNDERLINE;
import org.netbeans.jcode.core.util.FileUtil;
import static org.netbeans.jcode.core.util.FileUtil.getFileExt;
import static org.netbeans.jcode.core.util.FileUtil.getSimpleFileName;
import static org.netbeans.jcode.core.util.FileUtil.getSimpleFileNameWithExt;
import static org.netbeans.jcode.core.util.JavaSourceHelper.getSimpleClassName;
import org.netbeans.jcode.core.util.JavaUtil;
import static org.netbeans.jcode.core.util.ProjectHelper.getProjectWebRoot;
import org.netbeans.jcode.entity.info.EntityClassInfo;
import org.netbeans.jcode.entity.info.EntityResourceBeanModel;
import org.netbeans.jcode.layer.ConfigData;
import org.netbeans.jcode.layer.Generator;
import org.netbeans.jcode.layer.Technology;
import static org.netbeans.jcode.layer.Technology.Type.VIEWER;
import static org.netbeans.jcode.ng.main.AngularUtil.copyDynamicFile;
import static org.netbeans.jcode.ng.main.AngularUtil.copyDynamicResource;
import static org.netbeans.jcode.ng.main.AngularUtil.getResource;
import org.netbeans.jcode.ng.main.domain.NGApplicationConfig;
import org.netbeans.jcode.ng.main.domain.ApplicationSourceFilter;
import org.netbeans.jcode.ng.main.domain.EntityConfig;
import org.netbeans.jcode.ng.main.domain.NGEntity;
import org.netbeans.jcode.ng.main.domain.NGField;
import org.netbeans.jcode.ng.main.domain.NGRelationship;
import org.netbeans.jcode.parser.ejs.EJSParser;
import org.netbeans.jcode.parser.ejs.FileTypeStream;
import org.netbeans.jcode.rest.controller.RESTData;
import org.netbeans.jcode.rest.controller.RESTGenerator;
import org.netbeans.jcode.stack.config.data.ApplicationConfigData;
import org.netbeans.jcode.task.progress.ProgressHandler;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.Embedded;
import org.netbeans.jpa.modeler.spec.Entity;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.Id;
import org.netbeans.jpa.modeler.spec.Transient;
import org.netbeans.jpa.modeler.spec.Version;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.BaseAttribute;
import org.netbeans.jpa.modeler.spec.extend.EnumTypeHandler;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;

/**
 *
 * @author Gaurav Gupta
 */
@ServiceProvider(service = Generator.class)
@Technology(type = VIEWER, label = "Angular JS 1", panel = AngularPanel.class, parents = {RESTGenerator.class})
public class Angular1Generator implements Generator {

    private static final String TEMPLATE = "org/netbeans/jcode/template/angular1/";
    
    @ConfigData
    private EntityMappings entityMapping;
    
    @ConfigData
    private ApplicationConfigData appConfig;
    
    @ConfigData
    private AngularData ngData;

    @ConfigData
    private RESTData restData;

    @ConfigData
    private Project project; 
    
    @ConfigData
    private EntityResourceBeanModel model;
    
    @ConfigData
    private ProgressHandler handler;

    private static final List<String> PARSER_FILE_TYPE = Arrays.asList("html", "js", "css", "scss", "json");
//    private static final List<String> BINARY_FILE_TYPE = Arrays.asList("gif", "ico", "png", "jpeg", "jpg");

    private Consumer<FileTypeStream> getParserManager(EJSParser parser, Map<String, String> extTemplate) {
        return (fileTypeStream) -> {
            try {
                if (PARSER_FILE_TYPE.contains(fileTypeStream.getFileType())) {
                    IOUtils.write(parser.parse(fileTypeStream.getInputStream(), extTemplate), fileTypeStream.getOutputStream());
                } else {
                    IOUtils.copy(fileTypeStream.getInputStream(), fileTypeStream.getOutputStream());
                }
            } catch (ScriptException | IOException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
//                IOUtils.closeQuietly(fileTypeStream.getInputStream());
//                IOUtils.closeQuietly(fileTypeStream.getOutputStream());
            }
        };
    }

    private List<String> entityScriptFiles;
    private List<String> scriptFiles;
    private final static String MODULE_JS = "app/app.module.js";
    
    @Override
    public void execute() throws IOException {
        entityScriptFiles = new ArrayList<>();
        scriptFiles = new ArrayList<>();
        generateClientSideComponent();
    }
    
    private void generateClientSideComponent() {
        try {
            
            NGApplicationConfig applicationConfig = getAppConfig();
            ApplicationSourceFilter fileFilter = new ApplicationSourceFilter(applicationConfig);
            
            handler.append(Console.wrap(Angular1Generator.class, "MSG_Copying_Entity_Files", FG_RED, BOLD, UNDERLINE));
            Map<String, String> templateLib = getResource(TEMPLATE + "entity-include-resources.zip");
            List<NGEntity> entities = new ArrayList<>();
            for (EntityClassInfo entityClassInfo : model.getEntityInfos()) {
                NGEntity entity = getEntity(entityClassInfo);
                if (entity != null) {
                    entities.add(entity);
                    generateNgEntity(applicationConfig, fileFilter, getEntityConfig(), entity, templateLib);
                    generateNgEntityi18nResource(applicationConfig, fileFilter, entity);
                }
            }
            applicationConfig.setEntities(entities);
            
            generateNgApplication(applicationConfig, fileFilter);
            generateNgApplicationi18nResource(applicationConfig, fileFilter);
            generateNgLocaleResource(applicationConfig, fileFilter);
            generateNgHome(applicationConfig, fileFilter);
            
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
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

        Function<String, String> pathResolver = (templatePath) -> {
            return templatePath.substring(templatePath.lastIndexOf("/_")+2);// "_index.html" ->  "index.html"
        };

        copyDynamicFile(getParserManager(parser, null),TEMPLATE +  "_index.html", webRoot, pathResolver, handler);
        copyDynamicFile(getParserManager(parser, null), TEMPLATE + "_bower.json", project.getProjectDirectory(), pathResolver, handler);
        handler.append(Console.wrap(Angular1Generator.class, "MSG_Copying_Bower_Lib_Files", FG_RED, BOLD));
        FileUtil.copyStaticResource(TEMPLATE + "bower_components.zip", webRoot, null, handler);

    }

    private void generateNgApplication(NGApplicationConfig applicationConfig, ApplicationSourceFilter fileFilter) throws IOException {
        handler.append(Console.wrap(Angular1Generator.class, "MSG_Copying_Application_Files", FG_RED, BOLD, UNDERLINE));
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
        copyDynamicResource(getParserManager(parser, null), TEMPLATE + "web-resources.zip", webRoot, pathResolver, handler);
    }

    private void generateNgEntity(NGApplicationConfig applicationConfig, ApplicationSourceFilter fileFilter,
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
        copyDynamicResource(getParserManager(parser, templateLib), TEMPLATE + "entity-resources.zip", webRoot, pathResolver, handler);
    }

    private void generateNgEntityi18nResource(NGApplicationConfig applicationConfig, ApplicationSourceFilter fileFilter, NGEntity entity) throws IOException {
        FileObject webRoot = getProjectWebRoot(project);

        EJSParser parser = new EJSParser();
        parser.addContext(applicationConfig);
        parser.addContext(entity);

        Function<String, String> pathResolver = (templatePath) -> {
            String simpleFileName = getSimpleFileName(templatePath);
            String[] pathSplitter = simpleFileName.split("_");
            String type = pathSplitter[1];
            String lang = pathSplitter[2];
            if (!"entity".equals(type) || !applicationConfig.getLanguages().contains(lang)) {
                return null;
            }

            templatePath = String.format("i18n/%s/%s.json", lang, entity.getEntityTranslationKey());
            return templatePath;
        };
        copyDynamicResource(getParserManager(parser, null), TEMPLATE + "entity-resource-i18n.zip", webRoot, pathResolver, handler);
    }

    private void generateNgApplicationi18nResource(NGApplicationConfig applicationConfig, ApplicationSourceFilter fileFilter) throws IOException {
        FileObject webRoot = getProjectWebRoot(project);

        Map<String, Object> data = new HashMap();//todo remove

        EJSParser parser = new EJSParser();
        parser.addContext(applicationConfig);
        parser.addContext(data);

        Function<String, String> pathResolver = (templatePath) -> {
            String lang = templatePath.split("/")[1]; //"i18n/en/password.json" 
            if (!applicationConfig.getLanguages().contains(lang)) { //if lang selected by dev 
                return null;
            }
            if (templatePath.contains("/_")) {
                templatePath = templatePath.replaceAll("/_", "/");
            }
            //path modification not required
            return templatePath;
        };
        copyDynamicResource(getParserManager(parser, null), TEMPLATE + "web-resources-i18n.zip", webRoot, pathResolver, handler);
    }
    
    private void generateNgLocaleResource(NGApplicationConfig applicationConfig, ApplicationSourceFilter fileFilter) throws IOException {
        FileObject webRoot = getProjectWebRoot(project);

        Map<String, Object> data = new HashMap();//todo remove

        EJSParser parser = new EJSParser();
        parser.addContext(applicationConfig);
        parser.addContext(data);

        Function<String, String> pathResolver = (templatePath) -> {
            String lang = templatePath.substring(templatePath.indexOf('_')+1, templatePath.lastIndexOf('.')); //angular-locale_en.js 
            if (!applicationConfig.getLanguages().contains(lang)) { //if lang selected by dev 
                return null;
            }
            //path modification not required
            return templatePath;
        };
        copyDynamicResource(getParserManager(parser, null), TEMPLATE + "angular-locale.zip", webRoot, pathResolver, handler);
    }

    private EntityConfig getEntityConfig() {
        EntityConfig entityConfig = new EntityConfig();
        return entityConfig;
    }

    private NGApplicationConfig getAppConfig() {
        NGApplicationConfig applicationConfig = new NGApplicationConfig();
        applicationConfig.setAngularAppName(ngData.getModule());
        applicationConfig.setEnableTranslation(true);
        applicationConfig.setJhiPrefix("jhi");
        applicationConfig.setBaseName(ngData.getApplicationTitle());
        applicationConfig.setApplicationPath(restData.getRestConfigData().getApplicationPath());
        applicationConfig.setEnableMetrics(restData.isMetrics());
        applicationConfig.setRestPackage(restData.getPackage());
        applicationConfig.setEnableDocs(restData.isDocsEnable());
        return applicationConfig;
    }

    private NGEntity getEntity(EntityClassInfo entityClassInfo) {
        Entity entitySpec = appConfig.getEntityMappings().findEntity(entityClassInfo.getName());
        if(!"id".equals(entityClassInfo.getIdFieldInfo().getName())){
             handler.error(NbBundle.getMessage(Angular1Generator.class, "TITLE_PK_Field_Named_Id_Missing"),
                NbBundle.getMessage(Angular1Generator.class, "MSG_PK_Field_Named_Id_Missing", entitySpec.getName()));
             return null;
        }
        NGEntity entity = new NGEntity(entitySpec.getClazz(), "");
                List<Attribute> attributes = entitySpec.getAttributes().getAllAttribute();
//Uncomment for inheritence support
//        if(entitySpec.getSubclassList().size() > 1){
//            return null;
//        }
//        attributes.addAll(entitySpec.getSuperclassAttributes());
        for(Attribute attribute : attributes){
            if(attribute instanceof Id && ((Id)attribute).isGeneratedValue()){
                continue;
            }
            if(!attribute.getIncludeInUI()){//system attribute
                continue;
            }
            
            if(attribute instanceof RelationAttribute){
                RelationAttribute relationAttribute = (RelationAttribute)attribute;
                NGRelationship relationship = new NGRelationship(entitySpec, relationAttribute);
                Entity mappedEntity = relationAttribute.getConnectedEntity();
                if(mappedEntity.getLabelAttribute()==null || mappedEntity.getLabelAttribute().getName().equals("id")){
                    handler.warning(NbBundle.getMessage(Angular1Generator.class, "TITLE_Entity_Label_Missing"),
                    NbBundle.getMessage(Angular1Generator.class, "MSG_Entity_Label_Missing", entityClassInfo.getName()));
                } else {
                    relationship.setOtherEntityField(mappedEntity.getLabelAttribute().getName());
                }
                if(entitySpec == mappedEntity){
                    handler.warning(NbBundle.getMessage(Angular1Generator.class, "TITLE_Self_Relation_Not_Supported"),
                    NbBundle.getMessage(Angular1Generator.class, "MSG_Self_Relation_Not_Supported", attribute.getName(), entityClassInfo.getName()));
                    continue;
                }
                entity.addRelationship(relationship);
            } else if(attribute instanceof BaseAttribute){
                if(attribute instanceof EnumTypeHandler && ((EnumTypeHandler)attribute).getEnumerated()!=null){
                    handler.warning(NbBundle.getMessage(Angular1Generator.class, "TITLE_Enum_Type_Not_Supported"),
                    NbBundle.getMessage(Angular1Generator.class, "MSG_Enum_Type_Not_Supported", attribute.getName(), entityClassInfo.getName()));
                    continue;
                }
                if(attribute instanceof Embedded){
                    handler.warning(NbBundle.getMessage(Angular1Generator.class, "TITLE_Embedded_Type_Not_Supported"),
                    NbBundle.getMessage(Angular1Generator.class, "MSG_Embedded_Type_Not_Supported", attribute.getName(), entityClassInfo.getName()));
                    continue;
                }
                if(attribute instanceof ElementCollection){
                    handler.warning(NbBundle.getMessage(Angular1Generator.class, "TITLE_ElementCollection_Type_Not_Supported"),
                    NbBundle.getMessage(Angular1Generator.class, "MSG_ElementCollection_Type_Not_Supported", attribute.getName(), entityClassInfo.getName()));
                    continue;
                }
                if(attribute instanceof Version || attribute instanceof Transient){
                    continue;
                }
                NGField field = new NGField((BaseAttribute)attribute);
                Class<?> primitiveType = JavaUtil.getPrimitiveType(attribute.getDataTypeLabel());
                if(primitiveType!=null){
                    field.setFieldType(primitiveType.getSimpleName());//todo short, byte, char not supported in ui template
                } else {
                    field.setFieldType(getSimpleClassName(attribute.getDataTypeLabel()));
                }
                
                entity.addField(field);
            }
        }
        
//        for (EntityClassInfo.FieldInfo fieldInfo : entityClassInfo.getFieldInfos()) {
//            if(fieldInfo.isGeneratedValue() || entityConfig.getSystemAttribute().contains(fieldInfo.getName())){
//                continue;
//            }
//            if (fieldInfo.isRelationship()) {
//                NGRelationship relationship = new NGRelationship(entityClassInfo, fieldInfo);
//                EntityConfigData mappedEntityConfig ;
//                if (fieldInfo.isManyToMany() || fieldInfo.isOneToMany()) {
//                    mappedEntityConfig = appConfig.getEntity(fieldInfo.getTypeArg());
//                } else {
//                    mappedEntityConfig = appConfig.getEntity(fieldInfo.getType());
//                }
//                if(mappedEntityConfig.getLabelAttribute()==null || mappedEntityConfig.getLabelAttribute().equals("id")){
//                     handler.warning(NbBundle.getMessage(Angular1Generator.class, "TITLE_Entity_Label_Missing"),
//                                   NbBundle.getMessage(Angular1Generator.class, "MSG_Entity_Label_Missing", entityClassInfo.getName()));
//                }
//                relationship.setOtherEntityField(mappedEntityConfig.getLabelAttribute());
//                entity.addRelationship(relationship);
//            } else {
//                if(fieldInfo.isEnumerated()){
//                    handler.warning(NbBundle.getMessage(Angular1Generator.class, "TITLE_Enum_Type_Not_Supported"),
//                    NbBundle.getMessage(Angular1Generator.class, "MSG_Enum_Type_Not_Supported", fieldInfo.getName(), entityClassInfo.getName()));
//                    continue;
//                }
//                if(fieldInfo.isEmbedded()){
//                    handler.warning(NbBundle.getMessage(Angular1Generator.class, "TITLE_Embedded_Type_Not_Supported"),
//                    NbBundle.getMessage(Angular1Generator.class, "MSG_Embedded_Type_Not_Supported", fieldInfo.getName(), entityClassInfo.getName()));
//                    continue;
//                }
//                NGField field = new NGField(fieldInfo);
//                field.setFieldType(getSimpleClassName(fieldInfo.getType()));
//                entity.addField(field);
//            }
//        }
        return entity;
    }

  
}
