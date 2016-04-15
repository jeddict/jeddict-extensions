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
package org.netbeans.jcode.mvc.controller;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;

import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import static org.netbeans.jcode.beanvalidation.BeanVaildationConstants.EXECUTABLE_TYPE;
import static org.netbeans.jcode.beanvalidation.BeanVaildationConstants.VALID;
import static org.netbeans.jcode.beanvalidation.BeanVaildationConstants.VALIDATE_ON_EXECUTION;
import org.netbeans.jcode.cdi.logger.LoggerProducerGenerator;
import org.netbeans.jcode.core.util.StringHelper;
import org.netbeans.jcode.ejb.facade.SessionBeanData;
import org.netbeans.jcode.mvc.MVCConstants;
import static org.netbeans.jcode.mvc.MVCConstants.INJECT;
import static org.netbeans.jcode.mvc.MVCConstants.MODELS;
import org.netbeans.modules.j2ee.core.api.support.java.GenerationUtils;
import org.netbeans.modules.j2ee.core.api.support.java.JavaIdentifiers;
import org.netbeans.modules.j2ee.core.api.support.java.SourceUtils;
import org.netbeans.jcode.rest.util.RestGenerationOptions;
import org.netbeans.jcode.entity.info.EntityClassInfo;
import org.netbeans.jcode.entity.info.EntityClassInfo.FieldInfo;
import org.netbeans.jcode.entity.info.EntityResourceBeanModel;
import static org.netbeans.jcode.mvc.controller.ErrorBeanGenerator.ERROR_BEAN_CLASS;
import static org.netbeans.jcode.mvc.controller.ValidationUtilGenerator.BINDING_RESULT_VAR;
import static org.netbeans.jcode.mvc.controller.ValidationUtilGenerator.ERROR_BEAN_VAR;
import org.netbeans.jcode.mvc.controller.api.returntype.ControllerReturnType;
import static org.netbeans.jcode.mvc.MVCConstants.BINDING_RESULT;
import static org.netbeans.jcode.mvc.MVCConstants.REDIRECT;
import static org.netbeans.jcode.mvc.MVCConstants.VIEWABLE;
import org.netbeans.modules.websvc.rest.model.api.RestConstants;
import org.netbeans.jcode.mvc.util.Util;
import org.netbeans.jcode.mvc.viewer.jsp.JSPData;
import static org.netbeans.jcode.rest.RestConstant.BEAN_PARAM;
import static org.netbeans.jcode.rest.RestConstant.RESPONSE;
import static org.netbeans.jcode.rest.RestConstant.RESPONSE_UNQF;
import org.netbeans.jcode.rest.util.RestUtils;
import org.netbeans.jcode.core.util.SourceGroupSupport;
import static org.netbeans.jcode.mvc.controller.ValidationUtilGenerator.VALIDATION_UTIL_CLASS;
import org.netbeans.jcode.mvc.controller.event.ControllerEventGenerator;
import org.netbeans.jcode.task.progress.ProgressHandler;
import org.netbeans.modules.websvc.rest.spi.RestSupport;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author Gaurav Gupta
 */
public class MVCControllerGenerator {

    public final static String ENTITY_NAME_EXP = "<entity>";
    public final static String FOLDER_NAME_EXP = "<folder>";
    public final static String UTIL_PACKAGE = "util";

    private final static String MODEL_VAR_DECLARATION = "model";
    private final static String SESSION_BEAN_VAR_DECLARATION = "facade";
    private final static String VALIDATION_FILTER = "       if (validationResult.isFailed()) {\n"
            + "            return ValidationUtil.getResponse(validationResult, error);\n"
            + "        }\n\n";
    private EntityResourceBeanModel model;

    public MVCControllerGenerator() {
    }

    public MVCControllerGenerator(EntityResourceBeanModel model) {
        this.model = model;
    }

    public Set<FileObject> generate(final Project project, final SourceGroup sourceGroup,
            final String entityFQN, final String idClass,
            final SessionBeanData beanData, final MVCData mvcData, final JSPData viewerData,
            final boolean hasRemote, final boolean hasLocal,
            boolean overrideExisting, ProgressHandler handler) throws IOException {

        final Set<FileObject> createdFiles = new HashSet<>();
        final String entitySimpleName = JavaIdentifiers.unqualify(entityFQN);
        final String variableName = StringHelper.firstLower(entitySimpleName);
        final String constantName = StringHelper.toConstant(entitySimpleName);

        String facadeFileName = beanData.getPrefixName() + entitySimpleName + beanData.getSuffixName();
        String fqFacadeFileName = beanData.getPackage().isEmpty() ? facadeFileName : beanData.getPackage() + '.' + facadeFileName;

        String controllerFileName = mvcData.getPrefixName() + entitySimpleName + mvcData.getSuffixName();
        handler.progress(controllerFileName);

        FileObject targetFolder = SourceGroupSupport.getFolderForPackage(sourceGroup, mvcData.getPackage(), true);
        FileObject utilFolder = SourceGroupSupport.getFolderForPackage(targetFolder, UTIL_PACKAGE, true);
        String utilPackage = SourceGroupSupport.getPackageForFolder(sourceGroup, utilFolder);

        FileObject controllerFO = targetFolder.getFileObject(controllerFileName, "java");//skips here

        if (controllerFO != null) {
            if (overrideExisting) {
                controllerFO.delete();
            } else {
                throw new IOException("File already exists exception: " + controllerFO.getPath());
            }
        }

        final FileObject facade = GenerationUtils.createClass(targetFolder, controllerFileName, null);
        createdFiles.add(facade);

        String webPath;
        if (viewerData != null) {
            webPath = viewerData.getFolder();
        } else {
            webPath = JSPData.DEFAULT_FOLDER;
        }

        if (model != null) {
            Util.generatePrimaryKeyMethod(facade, entityFQN, model);
        }

        // add implements and extends clauses to the facade
        final Task<WorkingCopy> modificationTask = new Task<WorkingCopy>() {
            @Override
            public void run(WorkingCopy wc) throws Exception {
                wc.toPhase(Phase.RESOLVED);
                TypeElement classElement = SourceUtils.getPublicTopLevelElement(wc);
                ClassTree classTree = wc.getTrees().getTree(classElement);
                assert classTree != null;
                GenerationUtils genUtils = GenerationUtils.newInstance(wc);
                TreeMaker maker = wc.getTreeMaker();
                CompilationUnitTree cut = wc.getCompilationUnit();
                CompilationUnitTree newCut = maker.addCompUnitImport(cut, maker.Import(maker.Identifier(utilPackage + "." + ERROR_BEAN_CLASS), false));
                newCut = maker.addCompUnitImport(newCut, maker.Import(maker.Identifier(utilPackage + "." + VALIDATION_UTIL_CLASS), false));
                wc.rewrite(cut, newCut);
                            
//                ExpressionTree packageTree = wc.getCompilationUnit().getPackageName();
//                String packageName = packageTree.toString();

                List<Tree> members = new ArrayList<>(classTree.getMembers());

                members.add(maker.Variable(maker.addModifiersAnnotation(genUtils.createModifiers(Modifier.PRIVATE), genUtils.createAnnotation(INJECT)),
                        MODEL_VAR_DECLARATION, genUtils.createType(MODELS, classElement), null));

                members.add(maker.Variable(maker.addModifiersAnnotation(genUtils.createModifiers(Modifier.PRIVATE), genUtils.createAnnotation(INJECT)),
                        SESSION_BEAN_VAR_DECLARATION, genUtils.createType(fqFacadeFileName, classElement), null));

                if (mvcData.isBeanValidation()) {
                    members.add(maker.Variable(maker.addModifiersAnnotation(genUtils.createModifiers(Modifier.PRIVATE), genUtils.createAnnotation(INJECT)),
                            BINDING_RESULT_VAR, genUtils.createType(BINDING_RESULT, classElement), null));

                    members.add(maker.Variable(maker.addModifiersAnnotation(genUtils.createModifiers(Modifier.PRIVATE), genUtils.createAnnotation(INJECT)),
                            ERROR_BEAN_VAR, genUtils.createType(ERROR_BEAN_CLASS, classElement), null));
                }

                List<RestGenerationOptions> restGenerationOptions
                        = getRestFacadeMethodOptions(entityFQN, idClass);

                ModifiersTree publicModifiers = genUtils.createModifiers(
                        Modifier.PUBLIC);
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
                                        genUtils.createAnnotation(RestConstants.PATH,
                                                Collections.<ExpressionTree>singletonList(
                                                        annArgument)));

                    }

                    // add @Controller annotation
                    modifiersTree
                            = maker.addModifiersAnnotation(modifiersTree,
                                    genUtils.createAnnotation(MVCConstants.CONTROLLER));

                    // create arguments list
                    List<VariableTree> vars = new ArrayList<>();
                    String[] paramNames = option.getParameterNames();
                    int paramLength = paramNames == null ? 0
                            : option.getParameterNames().length;

                    boolean beanParamExist = false;
                    if (paramLength > 0) {
                        String[] paramTypes = option.getParameterTypes();
                        String[] annotations = option.getParameterAnnoations();
                        String[] annotationValues = option.getParameterAnnoationValues();

                        for (int i = 0; i < paramLength; i++) {
                            ModifiersTree pathParamTree = paramModifier;
                            if (annotations != null && annotations[i] != null) {

                                if (mvcData.isBeanValidation() && BEAN_PARAM.equals(annotations[i])) {
                                    pathParamTree = maker.addModifiersAnnotation(pathParamTree,
                                            genUtils.createAnnotation(VALID));
                                    beanParamExist = true;
                                }

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
                                    paramNames[i], paramTree, null); //NOI18N
                            vars.add(var);

                        }
                    }

                    StringBuilder body = new StringBuilder(option.getBody().replaceAll(ENTITY_NAME_EXP, constantName));
                    if (mvcData.isBeanValidation() && beanParamExist) {
                        body.insert(0, VALIDATION_FILTER);

                        // add @ValidateOnExecution annotation
                        modifiersTree
                                = maker.addModifiersAnnotation(modifiersTree,
                                        genUtils.createAnnotation(VALIDATE_ON_EXECUTION, 
                                                Collections.singletonList(genUtils.createAnnotationArgument("type", EXECUTABLE_TYPE, "NONE"))));
                    
                    }    
                        
                    Tree returnType = null;
                    String view = option.getRestMethod().getView();
                    view = view.replaceAll(ENTITY_NAME_EXP, variableName).replaceAll(FOLDER_NAME_EXP, webPath);

                    
                    
                    if (mvcData.getReturnType() == ControllerReturnType.VIEW_ANNOTATION && !(mvcData.isBeanValidation() && beanParamExist)) {
                         // add @View annotation
                            ExpressionTree annArgument = maker.Literal(view);
                            modifiersTree
                                    = maker.addModifiersAnnotation(modifiersTree,
                                            genUtils.createAnnotation(MVCConstants.VIEW,
                                                    Collections.<ExpressionTree>singletonList(
                                                            annArgument)));
                            returnType = maker.PrimitiveType(TypeKind.VOID);
                    } else if (mvcData.getReturnType() == ControllerReturnType.STRING ||
                            (mvcData.getReturnType() == ControllerReturnType.VIEW_ANNOTATION && mvcData.isBeanValidation() && beanParamExist)) {
                        body.append("\n").append("return \"").append(view).append("\";");
                        returnType = genUtils.createType(String.class.getName(), classElement);
                    } else if (mvcData.getReturnType() == ControllerReturnType.VIEWABLE) {
                        body.append("\n").append("return new ").append(MVCConstants.VIEWABLE_UNQF).append("(\"").append(view).append("\");");
                        returnType = genUtils.createType(VIEWABLE, classElement);
                    } else if (mvcData.getReturnType() == ControllerReturnType.JAXRS_RESPONSE) {
                        if (view.startsWith(REDIRECT)) {
                            body.append("\n").append("return ").append(RESPONSE_UNQF).append(".seeOther(").append(URI.class.getName()).append(".create(\"").append(view.substring(REDIRECT.length())).append("\")).build()");
                        } else {
                            body.append("\n").append("return ").append(RESPONSE_UNQF).append(".status(").append(RESPONSE_UNQF).append(".Status.OK).entity(\"").append(view).append("\").build()");
                        }
                        returnType = genUtils.createType(RESPONSE, classElement);
                    }

                    members.add(maker.Method(
                            modifiersTree,
                            option.getRestMethod().getMethodName()+entitySimpleName ,
                            returnType,
                            Collections.EMPTY_LIST,
                            vars,
                            (List<ExpressionTree>) Collections.EMPTY_LIST,
                            "{" + body + "}", //NOI18N
                            null)
                    );

                }

                ModifiersTree modifiersTree = maker.addModifiersAnnotation(classTree.getModifiers(),
                        genUtils.createAnnotation(RestConstants.PATH,
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
        
        if (mvcData.isBeanValidation()) {
            ValidationUtilGenerator.generate(mvcData, utilFolder, webPath);
            ErrorBeanGenerator.generate(utilFolder);
        }
        if(!mvcData.getEventType().isEmpty()){
            LoggerProducerGenerator.generate(utilFolder);
            ControllerEventGenerator.generate(mvcData.getEventType(), utilFolder);
        }
        return createdFiles;
    }

    public void generateApplicationConfig(final Project project, final SourceGroup sourceGroup,
            final MVCData mvcData, ProgressHandler handler) throws IOException {

        if (mvcData.getRestConfigData() == null) {
            return;
        }
        final RestSupport restSupport = project.getLookup().lookup(RestSupport.class);
        RestSupport.RestConfig.IDE.setAppClassName(mvcData.getRestConfigData().getPackage() + "." + mvcData.getRestConfigData().getApplicationClass()); //NOI18N
        if (restSupport != null) {
            try {
                restSupport.ensureRestDevelopmentReady(RestSupport.RestConfig.IDE);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        FileObject restAppPack = null;
        try {
            restAppPack = SourceGroupSupport.getFolderForPackage(sourceGroup, mvcData.getRestConfigData().getPackage(), true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        final String appClassName = mvcData.getRestConfigData().getApplicationClass();
        try {
            if (restAppPack != null && appClassName != null) {
                RestUtils.createApplicationConfigClass(restSupport, restAppPack, appClassName, mvcData.getRestConfigData().getApplicationPath());
            }
            RestUtils.disableRestServicesChangeListner(project);
            restSupport.configure("JPA Modeler - REST support");
        } catch (Exception iox) {
            Exceptions.printStackTrace(iox);
        } finally {
            RestUtils.enableRestServicesChangeListner(project);
        }
    }

    private List<RestGenerationOptions> getRestFacadeMethodOptions(
            String entityFQN, String idClass) {
        String paramArg = "java.lang.Character".equals(idClass)
                ? "id.charAt(0)" : "id"; //NOI18N
        String idType = "id".equals(paramArg) ? idClass : "java.lang.String"; //NOI18N

        boolean needPathSegment = false;
        if (model != null) {
            EntityClassInfo entityInfo = model.getEntityInfo(entityFQN);
            if (entityInfo != null) {
                FieldInfo idFieldInfo = entityInfo.getIdFieldInfo();
                needPathSegment = idFieldInfo != null && idFieldInfo.isEmbeddedId()
                        && idFieldInfo.getType() != null;
            }
        }

        RestGenerationOptions redirectCreateOptions = new RestGenerationOptions();
        redirectCreateOptions.setRestMethod(Operation.REDIRECT_TO_CREATE);
        redirectCreateOptions.setReturnType("void"); //NOI18N
        redirectCreateOptions.setBody(""); //NOI18N

        RestGenerationOptions createOptions = new RestGenerationOptions();
        createOptions.setRestMethod(Operation.CREATE);
        createOptions.setReturnType("void"); //NOI18N
        createOptions.setParameterNames(new String[]{"entity"}); //NOI18N
        createOptions.setParameterTypes(new String[]{entityFQN});
        createOptions.setParameterAnnoations(new String[]{BEAN_PARAM});
        createOptions.setParameterAnnoationValues(new String[]{null});
        createOptions.setConsumes(new String[]{"application/xml", "application/json"}); //NOI18N
        createOptions.setBody("facade.create(entity);"); //NOI18N

        RestGenerationOptions redirectUpdateOptions = new RestGenerationOptions();
        redirectUpdateOptions.setRestMethod(Operation.REDIRECT_TO_UPDATE);
        redirectUpdateOptions.setReturnType("void"); //NOI18N
        redirectUpdateOptions.setParameterNames(new String[]{"id"}); //NOI18N
        redirectUpdateOptions.setParameterAnnoations(new String[]{RestConstants.PATH_PARAM});
        redirectUpdateOptions.setParameterAnnoationValues(new String[]{"id"}); //NOI18N
        if (needPathSegment) {
            redirectUpdateOptions.setParameterTypes(new String[]{"javax.ws.rs.core.PathSegment"}); // NOI18N
        } else {
            redirectUpdateOptions.setParameterTypes(new String[]{idType});
        }
        StringBuilder updateBody = new StringBuilder();
        updateBody.append("model.put(\"<entity>\",facade.find(");                  //NOI18N
        updateBody.append(paramArg);
        updateBody.append("));");                                  //NOI18N
        redirectUpdateOptions.setBody(updateBody.toString());

        RestGenerationOptions updateOptions = new RestGenerationOptions();
        updateOptions.setRestMethod(Operation.UPDATE);
        updateOptions.setReturnType("void");//NOI18N
        updateOptions.setParameterNames(new String[]{"entity"}); //NOI18N
        updateOptions.setParameterAnnoations(new String[]{BEAN_PARAM});
        updateOptions.setParameterAnnoationValues(new String[]{null}); //NOI18N
        updateOptions.setParameterTypes(new String[]{entityFQN});
        updateOptions.setConsumes(new String[]{"application/xml", "application/json"}); //NOI18N
        updateOptions.setBody("facade.edit(entity);"); //NOI18N

        RestGenerationOptions destroyOptions = new RestGenerationOptions();
        destroyOptions.setRestMethod(Operation.REMOVE);
        destroyOptions.setReturnType("void");//NOI18N
        destroyOptions.setParameterNames(new String[]{"id"}); //NOI18N
        destroyOptions.setParameterAnnoations(new String[]{RestConstants.PATH_PARAM_ANNOTATION});
        destroyOptions.setParameterAnnoationValues(new String[]{"id"}); //NOI18N
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
        removeBody.append("facade.remove(facade.find(");             //NOI18N
        removeBody.append(paramArg);
        removeBody.append("));");                                  //NOI18N
        destroyOptions.setBody(removeBody.toString());

        RestGenerationOptions findOptions = new RestGenerationOptions();
        findOptions.setRestMethod(Operation.FIND);
        findOptions.setReturnType("void");//NOI18N
        findOptions.setProduces(new String[]{"application/xml", "application/json"}); //NOI18N
        findOptions.setParameterAnnoationValues(new String[]{"id"}); //NOI18N
        findOptions.setParameterAnnoations(new String[]{RestConstants.PATH_PARAM_ANNOTATION});
        findOptions.setParameterNames(new String[]{"id"}); //NOI18N
        if (needPathSegment) {
            findOptions.setParameterTypes(new String[]{"javax.ws.rs.core.PathSegment"}); // NOI18N
        } else {
            findOptions.setParameterTypes(new String[]{idType});
        }
        StringBuilder findBody = new StringBuilder(builder);
        findBody.append("model.put(\"<entity>\",facade.find(");                  //NOI18N
        findBody.append(paramArg);
        findBody.append("));");                                  //NOI18N
        findOptions.setBody(findBody.toString());

        RestGenerationOptions findAllOptions = new RestGenerationOptions();
        findAllOptions.setRestMethod(Operation.FIND_ALL);
        findAllOptions.setReturnType("void");//NOI18N
        findAllOptions.setProduces(new String[]{"application/xml", "application/json"});
        findAllOptions.setBody("model.put(\"<entity>_LIST\",facade.findAll());");

        return Arrays.<RestGenerationOptions>asList(
                redirectCreateOptions,
                createOptions,
                redirectUpdateOptions,
                updateOptions,
                destroyOptions,
                findOptions,
                findAllOptions
        );
    }

}
