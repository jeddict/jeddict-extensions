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
package org.netbeans.jcode.rest.controller;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import static java.util.Collections.EMPTY_LIST;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.lang.model.element.Modifier;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import static javax.lang.model.type.TypeKind.VOID;

import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.jcode.cdi.logger.LoggerProducerGenerator;
import org.netbeans.jcode.console.Console;
import static org.netbeans.jcode.console.Console.BOLD;
import static org.netbeans.jcode.console.Console.FG_RED;
import static org.netbeans.jcode.console.Console.UNDERLINE;
import static org.netbeans.jcode.console.Console.wrap;
import org.netbeans.jcode.core.util.Constants;
import static org.netbeans.jcode.core.util.Constants.JAVA_EXT;
import static org.netbeans.jcode.core.util.Constants.LIST_TYPE;
import org.netbeans.jcode.core.util.Constants.MimeType;
import static org.netbeans.jcode.core.util.Constants.MimeType.JSON;
import static org.netbeans.jcode.core.util.Constants.MimeType.TEXT;
import static org.netbeans.jcode.core.util.Constants.MimeType.XML;
import static org.netbeans.jcode.core.util.Constants.MimeType.find;
import static org.netbeans.jcode.core.util.Constants.VOID;
import org.netbeans.jcode.core.util.Inflector;
import static org.netbeans.jcode.core.util.Inflector.getInstance;
import org.netbeans.jcode.core.util.StringHelper;
import org.netbeans.jcode.ejb.facade.SessionBeanData;
import org.netbeans.modules.j2ee.core.api.support.java.GenerationUtils;
import org.netbeans.modules.j2ee.core.api.support.java.JavaIdentifiers;
import org.netbeans.modules.j2ee.core.api.support.java.SourceUtils;
import org.netbeans.jcode.rest.util.RestGenerationOptions;
import org.netbeans.jcode.entity.info.EntityClassInfo;
import org.netbeans.jcode.entity.info.EntityClassInfo.FieldInfo;
import org.netbeans.jcode.entity.info.EntityResourceBeanModel;
import org.netbeans.modules.websvc.rest.model.api.RestConstants;
import org.netbeans.jcode.rest.util.RestUtils;
import org.netbeans.jcode.core.util.SourceGroupSupport;
import static org.netbeans.jcode.core.util.SourceGroupSupport.getFolderForPackage;
import static org.netbeans.jcode.core.util.SourceGroupSupport.getFolderForPackage;
import static org.netbeans.jcode.core.util.SourceGroupSupport.getFolderForPackage;
import static org.netbeans.jcode.core.util.SourceGroupSupport.getFolderForPackage;
import static org.netbeans.jcode.core.util.StringHelper.firstLower;
import org.netbeans.jcode.ejb.facade.EjbFacadeGenerator;
import org.netbeans.jcode.generator.internal.util.Util;
import org.netbeans.jcode.layer.ConfigData;
import org.netbeans.jcode.layer.Generator;
import org.netbeans.jcode.layer.Technology;
import static org.netbeans.jcode.layer.Technology.Type.CONTROLLER;
import static org.netbeans.jcode.rest.controller.Operation.COUNT;
import static org.netbeans.jcode.rest.controller.Operation.CREATE;
import static org.netbeans.jcode.rest.controller.Operation.EDIT;
import static org.netbeans.jcode.rest.controller.Operation.FIND;
import static org.netbeans.jcode.rest.controller.Operation.FIND_ALL;
import static org.netbeans.jcode.rest.controller.Operation.FIND_RANGE;
import static org.netbeans.jcode.rest.controller.Operation.REMOVE;
import org.netbeans.jcode.rest.filter.RESTFilterGenerator;
import org.netbeans.jcode.rest.returntype.ControllerReturnType;
import static org.netbeans.jcode.rest.returntype.ControllerReturnType.JAXRS_RESPONSE;
import static org.netbeans.jcode.rest.util.RestUtils.createApplicationConfigClass;
import static org.netbeans.jcode.rest.util.RestUtils.disableRestServicesChangeListner;
import static org.netbeans.jcode.rest.util.RestUtils.enableRestServicesChangeListner;
import org.netbeans.jcode.task.progress.ProgressHandler;
import static org.netbeans.modules.j2ee.core.api.support.java.GenerationUtils.createClass;
import static org.netbeans.modules.j2ee.core.api.support.java.GenerationUtils.newInstance;
import static org.netbeans.modules.j2ee.core.api.support.java.JavaIdentifiers.unqualify;
import static org.netbeans.modules.j2ee.core.api.support.java.SourceUtils.getPublicTopLevelElement;
import static org.netbeans.modules.websvc.rest.model.api.RestConstants.EJB;
import static org.netbeans.modules.websvc.rest.model.api.RestConstants.HTTP_RESPONSE;
import static org.netbeans.modules.websvc.rest.model.api.RestConstants.PATH;
import static org.netbeans.modules.websvc.rest.model.api.RestConstants.PATH_PARAM;
import static org.netbeans.modules.websvc.rest.model.api.RestConstants.PATH_PARAM_ANNOTATION;
import org.netbeans.modules.websvc.rest.spi.RestSupport;
import static org.netbeans.modules.websvc.rest.spi.RestSupport.RestConfig.IDE;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import static org.openide.util.Exceptions.printStackTrace;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Gaurav Gupta
 */
@ServiceProvider(service = Generator.class)
@Technology(type = CONTROLLER, label = "REST", panel = RESTPanel.class, parents = {EjbFacadeGenerator.class})

public class RESTGenerator implements Generator {

    private final static String ENTITY_NAME_EXP = "<entity>";
    private final static String ENTITIES_NAME_EXP = "<entities>";
    
    public final static String UTIL_PACKAGE = "util";
    private final static String SESSION_BEAN_VAR_DECLARATION = "facade";
    private EntityResourceBeanModel model;

    @ConfigData
    private SessionBeanData beanData;

    @ConfigData
    private RESTData restData;

    @Override
    public void execute(Project project, SourceGroup source, EntityResourceBeanModel model, ProgressHandler handler) throws IOException {
        handler.progress(wrap(RESTGenerator.class, "MSG_Progress_Now_Generating", FG_RED, BOLD, UNDERLINE));
        generateUtil(project, source, handler);
        for (EntityClassInfo classInfo : model.getEntityInfos()) {
            generate(project, source, classInfo.getType(), classInfo.getPrimaryKeyType(), false, false, true, handler);
        }
    }

    public Set<FileObject> generate(final Project project, final SourceGroup sourceGroup,
            final String entityFQN, final String idClass,
            final boolean hasRemote, final boolean hasLocal,
            boolean overrideExisting, ProgressHandler handler) throws IOException {

        final Set<FileObject> createdFiles = new HashSet<>();
        final String entitySimpleName = unqualify(entityFQN);
        final String variableName = firstLower(entitySimpleName);
        final String listVariableName = getInstance().pluralize(variableName);

        String facadeFileName = beanData.getPrefixName() + entitySimpleName + beanData.getSuffixName();
        String fqFacadeFileName = beanData.getPackage().isEmpty() ? facadeFileName : beanData.getPackage() + '.' + facadeFileName;

        String controllerFileName = restData.getPrefixName() + entitySimpleName + restData.getSuffixName();
        handler.progress(controllerFileName);

        FileObject targetFolder = getFolderForPackage(sourceGroup, restData.getPackage(), true);

        FileObject controllerFO = targetFolder.getFileObject(controllerFileName, JAVA_EXT);//skips here

        if (controllerFO != null) {
            if (overrideExisting) {
                controllerFO.delete();
            } else {
                throw new IOException("File already exists exception: " + controllerFO.getPath());
            }
        }

        final FileObject facade = createClass(targetFolder, controllerFileName, null);
        createdFiles.add(facade);

        if (model != null) {
            Util.generatePrimaryKeyMethod(facade, entityFQN, model);
        }

        // add implements and extends clauses to the facade
        final Task<WorkingCopy> modificationTask = new Task<WorkingCopy>() {
            @Override
            public void run(WorkingCopy wc) throws Exception {
                wc.toPhase(Phase.RESOLVED);
                TypeElement classElement = getPublicTopLevelElement(wc);
                ClassTree classTree = wc.getTrees().getTree(classElement);
                assert classTree != null;
                GenerationUtils genUtils = newInstance(wc);
                TreeMaker maker = wc.getTreeMaker();

                List<Tree> members = new ArrayList<>(classTree.getMembers());

                members.add(maker.Variable(maker.addModifiersAnnotation(genUtils.createModifiers(PRIVATE), genUtils.createAnnotation(EJB)),
                        SESSION_BEAN_VAR_DECLARATION, genUtils.createType(fqFacadeFileName, classElement), null));

                List<RestGenerationOptions> restGenerationOptions
                        = getRestFacadeMethodOptions(entityFQN, idClass);

                ModifiersTree publicModifiers = genUtils.createModifiers(PUBLIC);
                ModifiersTree paramModifier = maker.Modifiers(Collections.<Modifier>emptySet());
                for (RestGenerationOptions option : restGenerationOptions) {

                    ModifiersTree modifiersTree
                            = maker.addModifiersAnnotation(publicModifiers,
                                    genUtils.createAnnotation(
                                            option.getRestMethod().getMethod()));

                    // add @Path annotation
                    String uriPath = option.getRestMethod().getUriPath();
                    if (uriPath != null) {
                        ExpressionTree annArgument = maker.Literal(uriPath);
                        modifiersTree
                                = maker.addModifiersAnnotation(modifiersTree,
                                        genUtils.createAnnotation(PATH,
                                                Collections.<ExpressionTree>singletonList(
                                                        annArgument)));

                    }
//                      // add @Produces annotation
//        modifiersTree = addMimeHandlerAnnotation(genUtils, maker,
//                modifiersTree, RestConstants.PRODUCE_MIME, option.getProduces());
//        
//        // add @Consumes annotation
//        modifiersTree = addMimeHandlerAnnotation(genUtils, maker,
//                modifiersTree, RestConstants.CONSUME_MIME, option.getConsumes());

                    // create arguments list
                    List<VariableTree> vars = new ArrayList<>();
                    String[] paramNames = option.getParameterNames();
                    int paramLength = paramNames == null ? 0
                            : option.getParameterNames().length;

                    if (paramLength > 0) {
                        String[] paramTypes = option.getParameterTypes();
                        String[] annotations = option.getParameterAnnoations();
                        String[] annotationValues = option.getParameterAnnoationValues();

                        for (int i = 0; i < paramLength; i++) {
                            ModifiersTree pathParamTree = paramModifier;
                            if (annotations != null && annotations[i] != null) {

                                AnnotationTree annotationTree;
                                if (annotationValues[i] != null) {
                                    annotationTree = genUtils.createAnnotation(annotations[i],
                                            Collections.<ExpressionTree>singletonList(
                                                    maker.Literal(annotationValues[i])));
                                } else {
                                    annotationTree = genUtils.createAnnotation(annotations[i]);

                                }
                                pathParamTree = maker.addModifiersAnnotation(pathParamTree, annotationTree);

                            }
                            Tree paramTree = genUtils.createType(paramTypes[i],
                                    classElement);
                            VariableTree var = maker.Variable(pathParamTree,
                                    paramNames[i].replaceAll(ENTITY_NAME_EXP, variableName)
                                            .replaceAll(ENTITIES_NAME_EXP, listVariableName), paramTree, null); 
                            vars.add(var);

                        }
                    }
                    
            Tree returnType = null;
            if(restData.getReturnType() == JAXRS_RESPONSE){
                returnType = genUtils.createType(HTTP_RESPONSE, classElement);
            } else { // GENERIC_ENTITY
                 returnType = (option.getReturnType() == null || option.getReturnType().equals(VOID))? 
                                maker.PrimitiveType(VOID): genUtils.createType(option.getReturnType(), classElement);
            }
            
            
            String bodyContent = option.getBody().toString()
                    .replaceAll(ENTITY_NAME_EXP, variableName).replaceAll(ENTITIES_NAME_EXP, listVariableName);
                    members.add(maker.Method(modifiersTree,
                            option.getRestMethod().getMethodName() + entitySimpleName,
                            returnType, EMPTY_LIST,
                            vars,
                            (List<ExpressionTree>) EMPTY_LIST,
                            "{" + bodyContent + "}", 
                            null)
                    );

                }

                ModifiersTree modifiersTree = classTree.getModifiers();

                modifiersTree = maker.addModifiersAnnotation(modifiersTree,
                        genUtils.createAnnotation(PATH,
                                Collections.<ExpressionTree>singletonList(maker.Literal(variableName))
                        ));

                ClassTree newClassTree = maker.Class(
                        modifiersTree,
                        classTree.getSimpleName(),
                        classTree.getTypeParameters(),
                        classTree.getExtendsClause(),
                        classTree.getImplementsClause(),
                        members);

                wc.rewrite(classTree, newClassTree);
            }
        };

        try {
            JavaSource.forFileObject(facade).runWhenScanFinished((CompilationController controller) -> {
                controller.toPhase(Phase.ELEMENTS_RESOLVED);
                JavaSource.forFileObject(facade).runModificationTask(modificationTask).commit();
            }, true).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return createdFiles;
    }

    private ModifiersTree addMimeHandlerAnnotation(GenerationUtils genUtils,
            TreeMaker maker, ModifiersTree modifiersTree, String handlerAnnotation, String[] mimes) {
        if (mimes == null) {
            return modifiersTree;
        }
        ExpressionTree annArguments;
        if (mimes.length == 1) {
            annArguments = mimeTypeTree(maker, mimes[0]);
        } else {
            List<ExpressionTree> mimeTypes = new ArrayList<>();
            for (String mime : mimes) {
                mimeTypes.add(mimeTypeTree(maker, mime));
            }
            annArguments = maker.NewArray(null,
                    Collections.<ExpressionTree>emptyList(),
                    mimeTypes);
        }
        return maker.addModifiersAnnotation(modifiersTree,
                genUtils.createAnnotation(
                        handlerAnnotation,
                        Collections.<ExpressionTree>singletonList(
                                annArguments)));
    }

    private ExpressionTree mimeTypeTree(TreeMaker maker, String mimeType) {
        Constants.MimeType type = find(mimeType);
        ExpressionTree result;
        if (type == null) {
            result = maker.Literal(mimeType);
        } else {
            result = type.expressionTree(maker);
        }
        return result;
    }
    

    public void generateUtil(final Project project, final SourceGroup sourceGroup, ProgressHandler handler) throws IOException {
        FileObject targetFolder = getFolderForPackage(sourceGroup, restData.getPackage(), true);

        generateApplicationConfig(project, sourceGroup, handler);
        if (!restData.getFilterTypes().isEmpty()) {
            FileObject utilFolder = getFolderForPackage(targetFolder, UTIL_PACKAGE, true);
            LoggerProducerGenerator.generate(utilFolder, handler);
            RESTFilterGenerator.generate(project, sourceGroup, utilFolder, restData.getFilterTypes(), handler);
        }

    }

    public void generateApplicationConfig(final Project project, final SourceGroup sourceGroup, ProgressHandler handler) throws IOException {

        if (restData.getRestConfigData() == null) {
            return;
        }
        final RestSupport restSupport = project.getLookup().lookup(RestSupport.class);
        IDE.setAppClassName(restData.getRestConfigData().getPackage() + "." + restData.getRestConfigData().getApplicationClass()); 
        if (restSupport != null) {
            try {
                restSupport.ensureRestDevelopmentReady(IDE);
            } catch (IOException ex) {
                printStackTrace(ex);
            }
        }

        FileObject restAppPack = null;
        try {
            restAppPack = getFolderForPackage(sourceGroup, restData.getRestConfigData().getPackage(), true);
        } catch (IOException ex) {
            printStackTrace(ex);
        }

        final String appClassName = restData.getRestConfigData().getApplicationClass();
        try {
            if (restAppPack != null && appClassName != null) {
                createApplicationConfigClass(restSupport, restAppPack, appClassName, restData.getRestConfigData().getApplicationPath(), handler);
            }
            disableRestServicesChangeListner(project);
            restSupport.configure("JPA Modeler - REST support");
        } catch (Exception iox) {
            printStackTrace(iox);
        } finally {
            enableRestServicesChangeListner(project);
        }
    }

    private List<RestGenerationOptions> getRestFacadeMethodOptions(
            String entityFQN, String idClass) {
        String paramArg = "java.lang.Character".equals(idClass)
                ? "id.charAt(0)" : "id"; 
        String idType = "id".equals(paramArg) ? idClass : "java.lang.String"; 

        boolean needPathSegment = false;
        if (model != null) {
            EntityClassInfo entityInfo = model.getEntityInfo(entityFQN);
            if (entityInfo != null) {
                FieldInfo idFieldInfo = entityInfo.getIdFieldInfo();
                needPathSegment = idFieldInfo != null && idFieldInfo.isEmbeddedId()
                        && idFieldInfo.getType() != null;
            }
        }        
        
        RestGenerationOptions createOptions = new RestGenerationOptions();
        createOptions.setRestMethod(CREATE);
        createOptions.setReturnType(VOID); 
        createOptions.setParameterNames(new String[]{ENTITY_NAME_EXP}); 
        createOptions.setParameterTypes(new String[]{entityFQN});
        createOptions.setConsumes(new String[]{XML.toString(), JSON.toString()}); 
        createOptions.setBody("facade.create(").append(ENTITY_NAME_EXP).append(");"); 
        if(restData.getReturnType() == JAXRS_RESPONSE){
            createOptions.appendBody('\n').append("return Response.status(Response.Status.CREATED).entity(").append(ENTITY_NAME_EXP).append(").build();");
        }

        RestGenerationOptions editOptions = new RestGenerationOptions();
        editOptions.setRestMethod(EDIT);
        editOptions.setReturnType(VOID);
        editOptions.setParameterNames(new String[]{"id", ENTITY_NAME_EXP}); 
        editOptions.setParameterAnnoations(new String[]{PATH_PARAM, null});
        editOptions.setParameterAnnoationValues(new String[]{"id", null}); 
        editOptions.setParameterTypes(new String[]{idType, entityFQN});
        editOptions.setConsumes(new String[]{XML.toString(), JSON.toString()}); 
        editOptions.setBody("facade.edit(").append(ENTITY_NAME_EXP).append(");"); 
        if(restData.getReturnType() == JAXRS_RESPONSE){
            editOptions.appendBody('\n').append("return Response.ok().build();");
        }

        RestGenerationOptions destroyOptions = new RestGenerationOptions();
        destroyOptions.setRestMethod(REMOVE);
        destroyOptions.setReturnType(VOID);
        destroyOptions.setParameterNames(new String[]{"id"}); 
        destroyOptions.setParameterAnnoations(new String[]{PATH_PARAM_ANNOTATION});
        destroyOptions.setParameterAnnoationValues(new String[]{"id"}); 
        StringBuilder builder = new StringBuilder();
        if (needPathSegment) {
            destroyOptions.setParameterTypes(new String[]{"javax.ws.rs.core.PathSegment"}); // NOI18N
            builder.append(idType);
            builder.append(" key=getPrimaryKey(id);\n");
            paramArg = "key";
        } else {
            destroyOptions.setParameterTypes(new String[]{idType});
        }
        StringBuilder removeBody = new StringBuilder(builder);
        removeBody.append("facade.remove(facade.find(").append(paramArg).append("));");                                  
        destroyOptions.setBody(removeBody);
        if(restData.getReturnType() == JAXRS_RESPONSE){
            destroyOptions.appendBody('\n').append("return Response.ok().build();");
        } else {
            
        }

        RestGenerationOptions findOptions = new RestGenerationOptions();
        findOptions.setRestMethod(FIND);
        findOptions.setReturnType(entityFQN);
        findOptions.setProduces(new String[]{XML.toString(), JSON.toString()}); 
        findOptions.setParameterAnnoationValues(new String[]{"id"}); 
        findOptions.setParameterAnnoations(new String[]{PATH_PARAM_ANNOTATION});
        findOptions.setParameterNames(new String[]{"id"}); 
        if (needPathSegment) {
            findOptions.setParameterTypes(new String[]{"javax.ws.rs.core.PathSegment"}); // NOI18N
        } else {
            findOptions.setParameterTypes(new String[]{idType});
        }
        StringBuilder findBody = new StringBuilder(builder);
        if (restData.getReturnType() == JAXRS_RESPONSE) {
            findBody.append(entityFQN).append(" ").append(ENTITY_NAME_EXP).append(" = facade.find(").append(paramArg).append(");");
            findBody.append('\n').append("return Response.ok(").append(ENTITY_NAME_EXP).append(").build();");
        } else {
            findBody.append("return facade.find(").append(paramArg).append(");");
        }
        findOptions.setBody(findBody);
                                

        String entityListType = LIST_TYPE + '<' + entityFQN + '>';
        RestGenerationOptions findAllOptions = new RestGenerationOptions();
        findAllOptions.setRestMethod(FIND_ALL);
        findAllOptions.setReturnType(entityListType);
        findAllOptions.setProduces(new String[]{XML.toString(), JSON.toString()});
        StringBuilder findAllBody = new StringBuilder();
        if (restData.getReturnType() == JAXRS_RESPONSE) {
            findAllBody.append(entityListType).append(" ").append(ENTITIES_NAME_EXP).append(" = facade.findAll();");
            findAllBody.append('\n').append("return Response.ok(").append(ENTITIES_NAME_EXP).append(").build();");
        } else {
            findAllBody.append("return facade.findAll();");
        }
        findAllOptions.setBody(findAllBody);

        
        RestGenerationOptions findRangeOptions = new RestGenerationOptions();
        findRangeOptions.setRestMethod(FIND_RANGE);
        findRangeOptions.setReturnType(entityListType);
        findRangeOptions.setProduces(new String[]{XML.toString(), JSON.toString()}); 
        findRangeOptions.setParameterAnnoationValues(new String[]{"from", "to"}); 
        findRangeOptions.setParameterAnnoations(new String[]{PATH_PARAM_ANNOTATION, PATH_PARAM_ANNOTATION});
        findRangeOptions.setParameterNames(new String[]{"from", "to"}); 
        findRangeOptions.setParameterTypes(new String[]{Integer.class.getCanonicalName(), Integer.class.getCanonicalName()});
        StringBuilder findRangeBody = new StringBuilder(builder);
        if (restData.getReturnType() == JAXRS_RESPONSE) {
            findRangeBody.append(entityListType).append(" ").append(ENTITIES_NAME_EXP).append(" = facade.findRange(new int[]{from, to}));");
            findRangeBody.append('\n').append("return Response.ok(").append(ENTITIES_NAME_EXP).append(").build();");
        } else {
            findRangeBody.append("return facade.findRange(new int[]{from, to}));");
        }
        findRangeOptions.setBody(findRangeBody);
        
        

        RestGenerationOptions countOptions = new RestGenerationOptions();
        countOptions.setRestMethod(COUNT);
        countOptions.setReturnType(String.class.getCanonicalName());
        countOptions.setProduces(new String[]{TEXT.toString()});
        if (restData.getReturnType() == JAXRS_RESPONSE) {
            countOptions.setBody("return Response.ok(facade.count()).build();");
        } else {
            countOptions.setBody("return String.valueOf(facade.count());");
        }
        
        return Arrays.<RestGenerationOptions>asList(
                createOptions,
                editOptions,
                destroyOptions,
                findOptions,
                findAllOptions,
                findRangeOptions,
                countOptions
        );
    }

}
