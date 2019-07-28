/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.mvc.controller;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WildcardTree;
import io.github.jeddict.jcode.util.CDIUtil;
import io.github.jeddict.cdi.logger.LoggerProducerGenerator;
import io.github.jeddict.jcode.ApplicationConfigData;
import static io.github.jeddict.jcode.BeanVaildationConstants.EXECUTABLE_TYPE;
import static io.github.jeddict.jcode.BeanVaildationConstants.VALIDATE_ON_EXECUTION;
import static io.github.jeddict.jcode.BeanVaildationConstants.VALID_FQN;
import static io.github.jeddict.jcode.CDIConstants.INJECT;
import io.github.jeddict.jcode.Generator;
import io.github.jeddict.jcode.JAXRSConstants;
import static io.github.jeddict.jcode.JAXRSConstants.BEAN_PARAM;
import static io.github.jeddict.jcode.JAXRSConstants.FORM_PARAM;
import static io.github.jeddict.jcode.JAXRSConstants.GET_PROPERTIES;
import static io.github.jeddict.jcode.JAXRSConstants.RESPONSE;
import static io.github.jeddict.jcode.JAXRSConstants.RESPONSE_UNQF;
import static io.github.jeddict.jcode.SecurityConstants.CALLER_NAME;
import static io.github.jeddict.jcode.SecurityConstants.CREDENTIALS;
import static io.github.jeddict.jcode.SecurityConstants.DEFAULT_CREDENTIALS;
import static io.github.jeddict.jcode.SecurityConstants.EMBEDDED_IDENTITY_STORE_DEFINITION;
import io.github.jeddict.jcode.annotation.ConfigData;
import io.github.jeddict.jcode.annotation.Technology;
import static io.github.jeddict.jcode.annotation.Technology.Type.CONTROLLER;
import io.github.jeddict.jcode.console.Console;
import static io.github.jeddict.jcode.console.Console.BOLD;
import static io.github.jeddict.jcode.console.Console.FG_DARK_RED;
import static io.github.jeddict.jcode.console.Console.UNDERLINE;
import io.github.jeddict.jcode.task.progress.ProgressHandler;
import io.github.jeddict.jcode.util.AttributeType.Type;
import static io.github.jeddict.jcode.util.AttributeType.Type.ARRAY;
import static io.github.jeddict.jcode.util.AttributeType.Type.PRIMITIVE;
import static io.github.jeddict.jcode.util.AttributeType.Type.PRIMITIVE_ARRAY;
import static io.github.jeddict.jcode.util.AttributeType.Type.STRING;
import static io.github.jeddict.jcode.util.AttributeType.Type.WRAPPER;
import static io.github.jeddict.jcode.util.AttributeType.getType;
import static io.github.jeddict.jcode.util.AttributeType.getWrapperType;
import io.github.jeddict.jcode.util.BuildManager;
import static io.github.jeddict.jcode.util.Constants.JAVA_EXT;
import static io.github.jeddict.jcode.util.Constants.VOID;
import io.github.jeddict.jcode.util.Inflector;
import io.github.jeddict.jcode.util.JavaIdentifiers;
import io.github.jeddict.mvc.JavaSourceHelper;
import static io.github.jeddict.jcode.util.ProjectHelper.getFolderForPackage;
import io.github.jeddict.jcode.util.StringHelper;
import io.github.jeddict.jpa.spec.DefaultAttribute;
import io.github.jeddict.jpa.spec.DefaultClass;
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.AttributeAnnotation;
import io.github.jeddict.mvc.MVCConstants;
import static io.github.jeddict.mvc.MVCConstants.BINDING_RESULT;
import static io.github.jeddict.mvc.MVCConstants.CSRF_VALID;
import static io.github.jeddict.mvc.MVCConstants.MODELS;
import io.github.jeddict.mvc.MVCConstants.MimeType;
import static io.github.jeddict.mvc.MVCConstants.PASSWORD;
import static io.github.jeddict.mvc.MVCConstants.REDIRECT;
import static io.github.jeddict.mvc.MVCConstants.VIEWABLE;
import io.github.jeddict.mvc.auth.controller.AuthMechanismGenerator;
import io.github.jeddict.mvc.auth.controller.LoginControllerGenerator;
import static io.github.jeddict.mvc.controller.ErrorBeanGenerator.ERROR_BEAN_CLASS;
import static io.github.jeddict.mvc.controller.ValidationUtilGenerator.BINDING_RESULT_VAR;
import static io.github.jeddict.mvc.controller.ValidationUtilGenerator.ERROR_BEAN_VAR;
import static io.github.jeddict.mvc.controller.ValidationUtilGenerator.VALIDATION_UTIL_CLASS;
import io.github.jeddict.mvc.controller.event.ControllerEventGenerator;
import io.github.jeddict.mvc.controller.returntype.ControllerReturnType;
import io.github.jeddict.mvc.viewer.jsp.JSPData;
import io.github.jeddict.repository.RepositoryData;
import io.github.jeddict.repository.RepositoryGenerator;
import io.github.jeddict.rest.applicationconfig.RestConfigData;
import static io.github.jeddict.rest.applicationconfig.RestConfigPanel.DEFAULT_RESOURCE_FOLDER;
import io.github.jeddict.rest.converter.ParamConvertorGenerator;
import io.github.jeddict.rest.util.RestGenerationOptions;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import static java.util.stream.Collectors.toList;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import io.github.jeddict.util.StringUtils;
import org.netbeans.api.java.source.Comment;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.TreeUtilities;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.modules.j2ee.core.api.support.java.GenerationUtils;
import org.netbeans.modules.j2ee.core.api.support.java.SourceUtils;
//import org.netbeans.modules.websvc.rest.model.api.RestServicesModel;
//import org.netbeans.modules.websvc.rest.spi.RestSupport;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Gaurav Gupta
 */
@ServiceProvider(service = Generator.class)
@Technology(
        type = CONTROLLER,
        label = "MVC 1.0",
        panel = MVCPanel.class,
        parents = {RepositoryGenerator.class}
)
public class MVCControllerGenerator implements Generator {

    private static final String TEMPLATE = "io/github/jeddict/mvc/template/";

    public final static String ENTITY_NAME_EXP = "<entity>";
    private final static String ENTITIES_NAME_EXP = "<entities>";
    public final static String FOLDER_NAME_EXP = "<folder>";
    public final static String UTIL_PACKAGE = "util";

    private final static String MODEL_VAR_DECLARATION = "model";
    private final static String REPOSITORY_REF_DECLARATION = "repository";
    private final static String VALIDATION_FILTER = "       if (validationResult.isFailed()) {\n"
            + "            return ValidationUtil.getResponse(validationResult, error);\n"
            + "        }\n\n";

    @ConfigData
    private RepositoryData repositoryData;

    @ConfigData
    private JSPData jspData;

    @ConfigData
    private MVCData mvcData;

    @ConfigData
    private EntityMappings entityMapping;

    @ConfigData
    private ProgressHandler handler;

    @ConfigData
    private ApplicationConfigData appConfigData;

    private Project project;

    private SourceGroup source;

    @Override
    public void preExecute() {
        addFormParam();
    }

    @Override
    public void execute() throws IOException {

        project = appConfigData.getTargetProject();
        source = appConfigData.getTargetSourceGroup();

        handler.progress(Console.wrap(MVCControllerGenerator.class, "MSG_Progress_Now_Generating", FG_DARK_RED, BOLD, UNDERLINE));
        if (appConfigData.isCompleteApplication()) {
            generateUtil();
            addMavenDependencies();
        }
        for (Entity entity : entityMapping.getGeneratedEntity().collect(toList())) {
            generate(entity, false, false, true);
        }
    }

    public Set<FileObject> generate(Entity entity, final boolean hasRemote, final boolean hasLocal, boolean overrideExisting) throws IOException {
        final String entityFQN = entity.getFQN();
        final String idClass = entity.getAttributes().getIdField().getDataTypeLabel();
        final Set<FileObject> createdFiles = new HashSet<>();
        final String entitySimpleName = entity.getClazz();
        final String variableName = StringHelper.firstLower(entitySimpleName);
        final String listVariableName = Inflector.getInstance().pluralize(variableName);
//        final String constantName = StringHelper.toConstant(entitySimpleName);

        String repositoryFileName = repositoryData.getRepositoryPrefixName() + entitySimpleName + repositoryData.getRepositorySuffixName();
        String fqRepositoryFileName = entity.getAbsolutePackage(appConfigData.getTargetPackage() + '.' + repositoryData.getRepositoryPackage()) + '.' + repositoryFileName;

        String controllerFileName = mvcData.getPrefixName() + entitySimpleName + mvcData.getSuffixName();
        handler.progress(controllerFileName);

        FileObject targetFolder = getFolderForPackage(source, entity.getAbsolutePackage(mvcData.getPackage()), true);

        FileObject controllerFO = targetFolder.getFileObject(controllerFileName, JAVA_EXT);//skips here

        if (controllerFO != null) {
            if (overrideExisting) {
                controllerFO.delete();
            } else {
                throw new IOException("File already exists exception: " + controllerFO.getPath());
            }
        }

        final FileObject controllerFile = GenerationUtils.createClass(targetFolder, controllerFileName, null);
        createdFiles.add(controllerFile);

        String webPath;
        if (jspData != null) {
            webPath = jspData.getFolder();
        } else {
            webPath = JSPData.DEFAULT_FOLDER;
        }

        // add implements and extends clauses to the repository
        final Task<WorkingCopy> modificationTask = (WorkingCopy wc) -> {
            wc.toPhase(Phase.RESOLVED);
            TypeElement classElement = SourceUtils.getPublicTopLevelElement(wc);
            ClassTree classTree = wc.getTrees().getTree(classElement);
            assert classTree != null;
            GenerationUtils genUtils = GenerationUtils.newInstance(wc);
            TreeMaker maker = wc.getTreeMaker();

            if (mvcData.isBeanValidation()) {
                String utilPackage = StringUtils.isBlank(mvcData.getPackage()) ? UTIL_PACKAGE : mvcData.getPackage() + "." + UTIL_PACKAGE;
                CompilationUnitTree cut = wc.getCompilationUnit();
                CompilationUnitTree newCut = maker.addCompUnitImport(cut, maker.Import(maker.Identifier(utilPackage + "." + ERROR_BEAN_CLASS), false));
                newCut = maker.addCompUnitImport(newCut, maker.Import(maker.Identifier(utilPackage + "." + VALIDATION_UTIL_CLASS), false));
                wc.rewrite(cut, newCut);
            }
//                ExpressionTree packageTree = wc.getCompilationUnit().getPackageName();
//                String packageName = packageTree.toString();

            List<Tree> members = new ArrayList<>(classTree.getMembers());

            members.add(maker.Variable(maker.addModifiersAnnotation(genUtils.createModifiers(Modifier.PRIVATE), genUtils.createAnnotation(INJECT)),
                    MODEL_VAR_DECLARATION, genUtils.createType(MODELS, classElement), null));

            members.add(maker.Variable(maker.addModifiersAnnotation(genUtils.createModifiers(Modifier.PRIVATE), genUtils.createAnnotation(INJECT)),
                    REPOSITORY_REF_DECLARATION, genUtils.createType(fqRepositoryFileName, classElement), null));

            if (mvcData.isBeanValidation()) {
                members.add(maker.Variable(maker.addModifiersAnnotation(genUtils.createModifiers(Modifier.PRIVATE), genUtils.createAnnotation(INJECT)),
                        BINDING_RESULT_VAR, genUtils.createType(BINDING_RESULT, classElement), null));

                members.add(maker.Variable(maker.addModifiersAnnotation(genUtils.createModifiers(Modifier.PRIVATE), genUtils.createAnnotation(INJECT)),
                        ERROR_BEAN_VAR, genUtils.createType(ERROR_BEAN_CLASS, classElement), null));
            }

            List<RestGenerationOptions> restGenerationOptions
                    = getRestRepositoryMethodOptions(entityFQN, idClass);

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
                                    genUtils.createAnnotation(JAXRSConstants.PATH,
                                            Collections.<ExpressionTree>singletonList(
                                                    annArgument)));

                }

                if (mvcData.isHybridClass()) {
                    // add @Controller annotation
                    modifiersTree
                            = maker.addModifiersAnnotation(modifiersTree,
                                    genUtils.createAnnotation(MVCConstants.CONTROLLER));
                }

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
                                        genUtils.createAnnotation(VALID_FQN));
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
                                paramNames[i].replaceAll(ENTITY_NAME_EXP, variableName)
                                        .replaceAll(ENTITIES_NAME_EXP, listVariableName), paramTree, null);
                        vars.add(var);

                    }
                }

                StringBuilder body = option.getBody();
                if (mvcData.isBeanValidation() && beanParamExist) {
                    body.insert(0, VALIDATION_FILTER);

                    // add @ValidateOnExecution annotation
                    modifiersTree
                            = maker.addModifiersAnnotation(modifiersTree,
                                    genUtils.createAnnotation(VALIDATE_ON_EXECUTION,
                                            Collections.singletonList(genUtils.createAnnotationArgument("type", EXECUTABLE_TYPE, "NONE"))));

                }

                if (mvcData.isCSRF() && beanParamExist) {
                    // add @CsrfValid annotation
                    modifiersTree = maker.addModifiersAnnotation(modifiersTree, genUtils.createAnnotation(CSRF_VALID));
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
                } else if (mvcData.getReturnType() == ControllerReturnType.STRING
                        || (mvcData.getReturnType() == ControllerReturnType.VIEW_ANNOTATION && mvcData.isBeanValidation() && beanParamExist)) {
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

                String bodyContent = body.toString()
                        .replaceAll(ENTITY_NAME_EXP, variableName).replaceAll(ENTITIES_NAME_EXP, listVariableName);

                members.add(maker.Method(
                        modifiersTree,
                        option.getRestMethod().getMethodName() + entitySimpleName,
                        returnType,
                        Collections.EMPTY_LIST,
                        vars,
                        Collections.<ExpressionTree>emptyList(),
                        "{" + bodyContent + "}",
                        null)
                );

            }

            ModifiersTree modifiersTree = classTree.getModifiers();

            if (!mvcData.isHybridClass()) {
                // add @Controller annotation
                modifiersTree
                        = maker.addModifiersAnnotation(modifiersTree,
                                genUtils.createAnnotation(MVCConstants.CONTROLLER));
            }

            modifiersTree = maker.addModifiersAnnotation(modifiersTree,
                    genUtils.createAnnotation(JAXRSConstants.PATH,
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
        };

        try {
            JavaSource.forFileObject(controllerFile).runWhenScanFinished(controller -> {
                controller.toPhase(Phase.ELEMENTS_RESOLVED);
                JavaSource.forFileObject(controllerFile).runModificationTask(modificationTask).commit();
            }, true).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        generatePrimaryKeyMethod(controllerFile, entity, entityMapping);

        return createdFiles;
    }

    public void generatePrimaryKeyMethod(final FileObject restResourceClass, Entity entity, EntityMappings entityMappings) throws IOException {
        if ((entity.isEmbeddedIdType() && entity.getAttributes().getEmbeddedId() != null) || entity.isIdClassType()) {
            final String idType = entity.getRootPackage() + '.' + entity.getCompositePrimaryKeyClass();
            JavaSource javaSource = JavaSource.forFileObject(restResourceClass);
            Task<WorkingCopy> task = new Task<WorkingCopy>() {

                @Override
                public void run(WorkingCopy workingCopy) throws Exception {
                    workingCopy.toPhase(Phase.RESOLVED);
                    CompilationUnitTree tree = workingCopy.getCompilationUnit();

                    TreeMaker maker = workingCopy.getTreeMaker();
                    Tree returnTypeTree = JavaSourceHelper.createTypeTree(workingCopy, idType);
                    ModifiersTree modifiersTree = JavaSourceHelper.createModifiersTree(workingCopy, new Modifier[]{Modifier.PRIVATE}, null, null);
                    List<VariableTree> vars = new ArrayList<>();

                    VariableTree var = maker.Variable(maker.Modifiers(
                            EnumSet.noneOf(Modifier.class)),
                            "pathSegment", JavaSourceHelper.createTypeTree(workingCopy,
                                    "javax.ws.rs.core.PathSegment"), null);     // NOI18N
                    vars.add(var);

                    MethodTree methodTree = maker.Method(modifiersTree,
                            "getPrimaryKey", returnTypeTree,
                            Collections.<TypeParameterTree>emptyList(),
                            vars,
                            Collections.<ExpressionTree>emptyList(),
                            getBody(entity.isEmbeddedIdType() ? entity.getAttributes().getEmbeddedId().getConnectedClass()
                                    : entityMapping.findDefaultClass(entity.getIdClass().getClazz()).orElse(null), workingCopy), null);

                    for (Tree typeDeclaration : tree.getTypeDecls()) {
                        if (TreeUtilities.CLASS_TREE_KINDS.contains(typeDeclaration.getKind())) {
                            ClassTree classTree = (ClassTree) typeDeclaration;
                            ClassTree newTree = maker.addClassMember(classTree, methodTree);
                            workingCopy.rewrite(classTree, newTree);
                        }
                    }
                }

                private String getBody(DefaultClass javaClass, WorkingCopy workingCopy) {
                    StringBuilder builder = new StringBuilder("{ ");              // NOI18N
                    builder.append(" /* \n");                                     // NOI18N
                    builder.append(" * pathSemgent represents a URI path segment ");// NOI18N
                    builder.append("and any associated matrix parameters.\n");    // NOI18N
                    builder.append(" * URI path part is supposed to be in ");     // NOI18N
                    builder.append("form of 'somePath");
                    List<DefaultAttribute> fieldInfos = javaClass.getAttributes().getDefaultAttributes();
                    for (Attribute fieldInfo : fieldInfos) {
                        String name = fieldInfo.getName();
                        builder.append(';');
                        builder.append(name);
                        builder.append('=');
                        builder.append(name);
                        builder.append("Value");
                    }
                    builder.append("'.\n");                                        // NOI18N
                    builder.append(" * Here 'somePath' is a result of getPath() ");// NOI18N
                    builder.append("method invocation and \n");                   // NOI18N
                    builder.append(" * it is ignored in the following code.\n");  // NOI18N
                    builder.append(" * Matrix parameters are used as field names");// NOI18N
                    builder.append(" to build a primary key instance.\n");         // NOI18N
                    builder.append(" */");
                    builder.append(javaClass.getName());
                    builder.append(" key=new ");                        // NOI18N
                    builder.append(javaClass.getName());
                    builder.append("();");                              // NOI18N

                    StringBuilder keyBuidler = new StringBuilder("javax.ws.rs.core.MultivaluedMap<String,String>");// NOI18N
                    keyBuidler.append(" map = pathSegment.getMatrixParameters();");// NOI18N
                    for (Attribute fieldInfo : fieldInfos) {
                        String name = fieldInfo.getName();
                        keyBuidler.append("java.util.List<String> ");    // NOI18N   
                        keyBuidler.append(name);
                        keyBuidler.append("=map.get(\"");               // NOI18N 
                        keyBuidler.append(name);
                        keyBuidler.append("\");");                     // NOI18N 
                        keyBuidler.append("if ( ");
                        keyBuidler.append(name);
                        keyBuidler.append("!=null && !");                // NOI18N
                        keyBuidler.append(name);
                        keyBuidler.append(".isEmpty()){");               // NOI18N 
                        String dataType = fieldInfo.getDataTypeLabel();
                        Type type = getType(dataType);
                        if (type == PRIMITIVE_ARRAY || type == ARRAY) {
                            keyBuidler.append(fieldInfo.getDataTypeLabel());
                            keyBuidler.append(" field=new ").append(fieldInfo.getDataTypeLabel());
                            keyBuidler.deleteCharAt(keyBuidler.length() - 1);
                            keyBuidler.append(name).append(".size()];");
                            keyBuidler.append("for( int i=0;i<").append(name).append(".size();i++){");
                            keyBuidler.append("field[i]= new ");
                            if (type == PRIMITIVE_ARRAY) {
                                keyBuidler.append(getWrapperType(dataType));//todo remove array
                            } else {
                                keyBuidler.append(dataType);
                            }
                            keyBuidler.append("(").append(name).append(".get(i));");
                            keyBuidler.append('}');
                            keyBuidler.append("key.").append(getSetterName(fieldInfo)).append("(field);");
                        } else {
                            keyBuidler.append("key.").append(getSetterName(fieldInfo)).append('(');
                            if (type == STRING) {
                                keyBuidler.append(name).append(".get(0)");
                            } else if (type == PRIMITIVE) {
                                keyBuidler.append(getWrapperType(dataType)).append(".valueOf(").append(name).append(".get(0))");             // NOI18N
                            } else if (type == WRAPPER) {
                                keyBuidler.append(dataType).append(".valueOf(").append(name).append(".get(0))");
                            } else {
                                keyBuidler.append("new ").append(dataType).append("(").append(name).append(".get(0))");
                            }
                            keyBuidler.append(");");
                        }
                        keyBuidler.append('}');
                    }
                    builder.append(keyBuidler);

                    builder.append("return key;");                             // NOI18N
                    builder.append(" }");                                          // NOI18N
                    return builder.toString();
                }
            };
            javaSource.runModificationTask(task).commit();
        }
    }

    public static String getGetterName(Attribute fieldInfo) {
        return "get" + StringHelper.firstUpper(fieldInfo.getName());      //NOI18N
    }

    public static String getSetterName(Attribute fieldInfo) {
        return "set" + StringHelper.firstUpper(fieldInfo.getName());      //NOI18N
    }

    public void generateUtil() throws IOException {
        FileObject targetFolder = getFolderForPackage(source, mvcData.getPackage(), true);
        FileObject utilFolder = getFolderForPackage(targetFolder, UTIL_PACKAGE, true);

        String resourcePath = DEFAULT_RESOURCE_FOLDER;
        if (jspData != null) {
            resourcePath = jspData.getResourceFolder();
        }

        if (mvcData.isBeanValidation()) {
            ValidationUtilGenerator.generate(mvcData, utilFolder, resourcePath, handler);
            ErrorBeanGenerator.generate(utilFolder, handler);
        }

        if (!mvcData.getEventType().isEmpty()) {
            LoggerProducerGenerator.generate(utilFolder, handler);
            ControllerEventGenerator.generate(mvcData.getEventType(), utilFolder, handler);
        }

        ParamConvertorGenerator.generate(project, source, utilFolder, handler);

        CDIUtil.createDD(project);

        generateApplicationConfig(project, source, handler);

        if (mvcData.isAuthentication()) {
            LoginControllerGenerator.generate(project, source, targetFolder, handler);
            AuthMechanismGenerator.generate(project, source, targetFolder, handler);
        }
    }

    private void generateApplicationConfig(final Project project, final SourceGroup sourceGroup, ProgressHandler handler) throws IOException {

        if (mvcData.getRestConfigData() == null) {
            return;
        }
//        final RestSupport restSupport = project.getLookup().lookup(RestSupport.class);
//        RestSupport.RestConfig.IDE.setAppClassName(mvcData.getPackage() + "." + mvcData.getRestConfigData().getApplicationClass());
//        if (restSupport != null) {
//            try {
//                restSupport.ensureRestDevelopmentReady(RestSupport.RestConfig.IDE);
//            } catch (IOException ex) {
//                Exceptions.printStackTrace(ex);
//            }
//        }

        FileObject restAppPack = getFolderForPackage(sourceGroup, mvcData.getPackage(), true);

        final String appClassName = mvcData.getRestConfigData().getApplicationClass();
        try {
            if (restAppPack != null && appClassName != null) {

                FileObject configFO = createApplicationConfigClass(mvcData.getRestConfigData(),
                        restAppPack, null, null, handler);
                JavaSource javaSource = JavaSource.forFileObject(configFO);//add some cutom properties/method specific to MVC
                if (javaSource != null) {

                    javaSource.runModificationTask((WorkingCopy workingCopy) -> {
                        workingCopy.toPhase(JavaSource.Phase.RESOLVED);
                        ClassTree tree = io.github.jeddict.jcode.util.JavaSourceHelper.getTopLevelClassTree(workingCopy);//
                        TreeMaker maker = workingCopy.getTreeMaker();
                        ClassTree newTree = tree; // NOI18N

                        newTree = addAuthAnnotation(workingCopy, maker, newTree);
                        newTree = createAppConfigGetProperties(workingCopy, maker, newTree);
                        workingCopy.rewrite(tree, newTree);
                    }).commit();
                }

            }
//            disableRestServicesChangeListner(project);
//            restSupport.configure("Jeddict - REST support");
        } catch (IOException | IllegalArgumentException iox) {
            Exceptions.printStackTrace(iox);
        } finally {
//            enableRestServicesChangeListner(project);
        }
    }

    public static FileObject createApplicationConfigClass(RestConfigData configData,
            FileObject packageFolder, String appPackage,
            List<String> providerClasses, ProgressHandler handler) throws IOException {

        final String className = configData.getApplicationClass();
        final String applicationPath = configData.getApplicationPath();

        FileObject configFO = packageFolder.getFileObject(className, JAVA_EXT);
        if (configFO != null) {
            configFO.delete();
        }

        FileObject appClass = GenerationUtils.createClass(packageFolder, className, null);
        JavaSource javaSource = JavaSource.forFileObject(appClass);
        if (javaSource == null) {
            return null;
        }

        javaSource.runModificationTask((WorkingCopy workingCopy) -> {
            workingCopy.toPhase(JavaSource.Phase.RESOLVED);
            JavaSourceHelper.addClassAnnotation(workingCopy,
                    new String[]{"javax.ws.rs.ApplicationPath"},
                    new String[]{applicationPath});         // NOI18N
            ClassTree tree = io.github.jeddict.jcode.util.JavaSourceHelper.getTopLevelClassTree(workingCopy);//
            TreeMaker maker = workingCopy.getTreeMaker();
            ClassTree newTree = maker.setExtends(tree,
                    maker.QualIdent(JAXRSConstants.JAX_RS_APPLICATION_CLASS)); // NOI18N

            //Singletons imports
            TypeElement classElement = SourceUtils.getPublicTopLevelElement(workingCopy);
            GenerationUtils genUtils = GenerationUtils.newInstance(workingCopy);

            newTree = createGetClasses(workingCopy, maker, newTree, providerClasses);
            newTree = createAddResourceClasses(maker, newTree, workingCopy, "{}", true);

            workingCopy.rewrite(tree, newTree);
        }).commit();
        return appClass;
    }

    private static ClassTree createGetClasses(WorkingCopy workingCopy, TreeMaker maker, ClassTree newTree, List<String> providerClasses) {

        ModifiersTree modifiersTree = maker.Modifiers(
                EnumSet.of(Modifier.PUBLIC), Collections.singletonList(
                maker.Annotation(maker.QualIdent(
                        Override.class.getCanonicalName()),
                        Collections.<ExpressionTree>emptyList())));

        WildcardTree wildCard = maker.Wildcard(Tree.Kind.UNBOUNDED_WILDCARD,
                null);
        ParameterizedTypeTree wildClass = maker.ParameterizedType(
                maker.QualIdent(Class.class.getCanonicalName()),
                Collections.singletonList(wildCard));
        ParameterizedTypeTree wildSet = maker.ParameterizedType(
                maker.QualIdent(Set.class.getCanonicalName()),
                Collections.singletonList(wildClass));

        MethodTree methodTree = maker.Method(modifiersTree,
                JAXRSConstants.GET_CLASSES, wildSet,
                Collections.<TypeParameterTree>emptyList(),
                Collections.<VariableTree>emptyList(),
                Collections.<ExpressionTree>emptyList(),
                createBodyForGetClassesMethod(providerClasses), null);
        return maker.addClassMember(newTree, methodTree);
    }

    private static String createBodyForGetClassesMethod(List<String> provideClasses) {
        StringBuilder builder = new StringBuilder();
        builder.append('{');
        builder.append("Set<Class<?>> resources = new java.util.HashSet<>();");
        if (provideClasses != null) {
            provideClasses.forEach(_class -> builder.append("resources.add(").append(_class).append(".class);"));
        }
        builder.append(JAXRSConstants.GET_REST_RESOURCE_CLASSES2 + "(resources);");
        builder.append("return resources;}");
        return builder.toString();
    }

    private ClassTree addAuthAnnotation(WorkingCopy workingCopy, TreeMaker maker, ClassTree newTree) {

        if (!mvcData.isAuthentication()) {
            return newTree;
        }
        CompilationUnitTree cut = workingCopy.getCompilationUnit();
        CompilationUnitTree newCut = cut;
        GenerationUtils genUtils = GenerationUtils.newInstance(workingCopy);

        ExpressionTree credential = genUtils.createAnnotation(CREDENTIALS, Arrays.asList(
                genUtils.createAnnotationArgument(CALLER_NAME, DEFAULT_CREDENTIALS),
                genUtils.createAnnotationArgument(PASSWORD, DEFAULT_CREDENTIALS)));

        ModifiersTree modifiersTree = maker.addModifiersAnnotation(newTree.getModifiers(),
                genUtils.createAnnotation(EMBEDDED_IDENTITY_STORE_DEFINITION,
                        Collections.<ExpressionTree>singletonList(credential)));

        workingCopy.rewrite(cut, newCut);

        ClassTree newClassTree = maker.Class(
                modifiersTree,
                newTree.getSimpleName(),
                newTree.getTypeParameters(),
                newTree.getExtendsClause(),
                newTree.getImplementsClause(),
                newTree.getMembers());

        return newClassTree;
    }

    private ClassTree createAppConfigGetProperties(WorkingCopy workingCopy, TreeMaker maker, ClassTree newTree) {

        if (jspData == null) {
            return newTree;
        }
        String viewEngineFolder = jspData.getFolder();
        boolean csrfProtection = mvcData.isCSRF();
        if (StringUtils.isBlank(viewEngineFolder) && !csrfProtection) {
            return newTree;
        }

        CompilationUnitTree cut = workingCopy.getCompilationUnit();
        CompilationUnitTree newCut = cut;

        ModifiersTree modifiersTree = maker.Modifiers(
                EnumSet.of(Modifier.PUBLIC), Collections.singletonList(
                maker.Annotation(maker.QualIdent(
                        Override.class.getCanonicalName()),
                        Collections.<ExpressionTree>emptyList())));
        List<Tree> typeArguments = new ArrayList<>();
        typeArguments.add(maker.QualIdent(String.class.getCanonicalName()));
        typeArguments.add(maker.QualIdent(Object.class.getCanonicalName()));

        ParameterizedTypeTree wildMap = maker.ParameterizedType(maker.QualIdent(Map.class.getCanonicalName()), typeArguments);

        StringBuilder builder = new StringBuilder();
        builder.append("{").append('\n');
        builder.append("Map<String, Object> props = new HashMap<>();").append('\n');
        if (StringUtils.isNotBlank(viewEngineFolder)) {
            newCut = maker.addCompUnitImport(cut, maker.Import(maker.Identifier(HashMap.class.getCanonicalName()), false));
            newCut = maker.addCompUnitImport(newCut, maker.Import(maker.Identifier(MVCConstants.VIEW_ENGINE), false));
            builder.append("props.put(ViewEngine.VIEW_FOLDER, \"/").append(viewEngineFolder).append("\");").append('\n');
        }
        if (csrfProtection) {
            newCut = maker.addCompUnitImport(newCut, maker.Import(maker.Identifier(MVCConstants.CSRF), false));
            builder.append("props.put(Csrf.CSRF_PROTECTION, Csrf.CsrfOptions.EXPLICIT);").append('\n');
        }
        builder.append("return props;").append('}');

        workingCopy.rewrite(cut, newCut);

        MethodTree methodTree = maker.Method(modifiersTree,
                GET_PROPERTIES, wildMap,
                Collections.<TypeParameterTree>emptyList(),
                Collections.<VariableTree>emptyList(),
                Collections.<ExpressionTree>emptyList(),
                builder.toString(), null);
        return maker.addClassMember(newTree, methodTree);
    }

    private List<RestGenerationOptions> getRestRepositoryMethodOptions(String entityFQN, String idClass) {
        String paramArg = "java.lang.Character".equals(idClass) ? "id.charAt(0)" : "id";
        String idType = "id".equals(paramArg) ? idClass : "java.lang.String";
        String entityClass = JavaIdentifiers.unqualify(entityFQN);
        boolean needPathSegment = false;
        Optional<Entity> entityOpt = entityMapping.findEntity(entityClass);
        if (entityOpt.isPresent()) {
            needPathSegment = entityOpt.get().isIdClassType() || (entityOpt.get().isEmbeddedIdType() && entityOpt.get().getAttributes().getEmbeddedId() != null);
        }
        String KEY_NAME = entityClass.toUpperCase();

        RestGenerationOptions redirectCreateOptions = new RestGenerationOptions();
        redirectCreateOptions.setRestMethod(Operation.REDIRECT_TO_CREATE);
        redirectCreateOptions.setReturnType(VOID);
        redirectCreateOptions.setBody("");

        RestGenerationOptions createOptions = new RestGenerationOptions();
        createOptions.setRestMethod(Operation.CREATE);
        createOptions.setReturnType(VOID);
        createOptions.setParameterNames(new String[]{ENTITY_NAME_EXP});
        createOptions.setParameterTypes(new String[]{entityFQN});
        createOptions.setParameterAnnoations(new String[]{BEAN_PARAM});
        createOptions.setParameterAnnoationValues(new String[]{null});
        createOptions.setConsumes(new String[]{MimeType.XML.toString(), MimeType.JSON.toString()});
        createOptions.setBody("repository.create(").append(ENTITY_NAME_EXP).append(");");

        RestGenerationOptions redirectUpdateOptions = new RestGenerationOptions();
        redirectUpdateOptions.setRestMethod(Operation.REDIRECT_TO_UPDATE);
        redirectUpdateOptions.setReturnType(VOID);
        redirectUpdateOptions.setParameterNames(new String[]{"id"});
        redirectUpdateOptions.setParameterAnnoations(new String[]{JAXRSConstants.PATH_PARAM});
        redirectUpdateOptions.setParameterAnnoationValues(new String[]{"id"});
        StringBuilder builder = new StringBuilder();
        if (needPathSegment) {
            redirectUpdateOptions.setParameterTypes(new String[]{"javax.ws.rs.core.PathSegment"}); // NOI18N
            builder.append(idType);
            builder.append(" key=getPrimaryKey(id);\n");
            paramArg = "key";
        } else {
            redirectUpdateOptions.setParameterTypes(new String[]{idType});
        }
        StringBuilder updateBody = new StringBuilder(builder);
        updateBody.append("model.put(\"").append(KEY_NAME).append("\" ,repository.find(");
        updateBody.append(paramArg);
        updateBody.append("));");
        redirectUpdateOptions.setBody(updateBody);

        RestGenerationOptions updateOptions = new RestGenerationOptions();
        updateOptions.setRestMethod(Operation.UPDATE);
        updateOptions.setReturnType(VOID);
        updateOptions.setParameterNames(new String[]{ENTITY_NAME_EXP});
        updateOptions.setParameterAnnoations(new String[]{BEAN_PARAM});
        updateOptions.setParameterAnnoationValues(new String[]{null});
        updateOptions.setParameterTypes(new String[]{entityFQN});
        updateOptions.setConsumes(new String[]{MimeType.XML.toString(), MimeType.JSON.toString()});
        updateOptions.setBody("repository.edit(").append(ENTITY_NAME_EXP).append(");");

        RestGenerationOptions destroyOptions = new RestGenerationOptions();
        destroyOptions.setRestMethod(Operation.REMOVE);
        destroyOptions.setReturnType(VOID);
        destroyOptions.setParameterNames(new String[]{"id"});
        destroyOptions.setParameterAnnoations(new String[]{JAXRSConstants.PATH_PARAM_ANNOTATION});
        destroyOptions.setParameterAnnoationValues(new String[]{"id"});
        if (needPathSegment) {
            destroyOptions.setParameterTypes(new String[]{"javax.ws.rs.core.PathSegment"}); // NOI18N
        } else {
            destroyOptions.setParameterTypes(new String[]{idType});
        }
        StringBuilder removeBody = new StringBuilder(builder);
        removeBody.append("repository.remove(repository.find(");
        removeBody.append(paramArg);
        removeBody.append("));");
        destroyOptions.setBody(removeBody);

        RestGenerationOptions findOptions = new RestGenerationOptions();
        findOptions.setRestMethod(Operation.FIND);
        findOptions.setReturnType(VOID);
        findOptions.setProduces(new String[]{MimeType.XML.toString(), MimeType.JSON.toString()});
        findOptions.setParameterAnnoationValues(new String[]{"id"});
        findOptions.setParameterAnnoations(new String[]{JAXRSConstants.PATH_PARAM_ANNOTATION});
        findOptions.setParameterNames(new String[]{"id"});
        if (needPathSegment) {
            findOptions.setParameterTypes(new String[]{"javax.ws.rs.core.PathSegment"}); // NOI18N
        } else {
            findOptions.setParameterTypes(new String[]{idType});
        }
        StringBuilder findBody = new StringBuilder(builder);
        findBody.append("model.put(\"").append(KEY_NAME).append("\" ,repository.find(");
        findBody.append(paramArg);
        findBody.append("));");
        findOptions.setBody(findBody);

        RestGenerationOptions findAllOptions = new RestGenerationOptions();
        findAllOptions.setRestMethod(Operation.FIND_ALL);
        findAllOptions.setReturnType(VOID);
        findAllOptions.setProduces(new String[]{MimeType.XML.toString(), MimeType.JSON.toString()});
        findAllOptions.setBody("model.put(\"").append(KEY_NAME).append("_LIST\",repository.findAll());");

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

    private void addMavenDependencies() {
        BuildManager.getInstance(project)
                .copy(TEMPLATE + "pom/_pom.xml")
                .setSourceVersion("1.8")
                .commit();
    }

    private void addFormParam() {
        for (Entity entity : entityMapping.getEntity()) {
            List<Attribute> attributes = new ArrayList<>(entity.getAttributes().getId());
            attributes.addAll(entity.getAttributes().getBasic());
            for (Attribute attribute : attributes) {
                attribute.addRuntimeAnnotation(new AttributeAnnotation(String.format("@%s(\"%s\")", FORM_PARAM, attribute.getName())));
            }
        }
    }

//    public static RestServicesModel getRestServicesMetadataModel(Project project) {
//        RestSupport support = project.getLookup().lookup(RestSupport.class);;
//        if (support != null) {
//            return support.getRestServicesModel();
//        }
//        return null;
//    }
//
//    public static void disableRestServicesChangeListner(Project project) {
//        final RestServicesModel wsModel = getRestServicesMetadataModel(project);
//        if (wsModel == null) {
//            return;
//        }
//        wsModel.disablePropertyChangeListener();
//    }
//
//    public static void enableRestServicesChangeListner(Project project) {
//        final RestServicesModel wsModel = getRestServicesMetadataModel(project);
//        if (wsModel == null) {
//            return;
//        }
//        wsModel.enablePropertyChangeListener();
//    }
    /**
     * creates addResourceClasses method
     *
     * @param maker tree maker
     * @param classTree class tree
     * @param controller compilation controller
     * @param methodBody method body
     * @param addComment add comment or not
     * @return modified class tree
     * @throws IOException 
     */
    public static ClassTree createAddResourceClasses(TreeMaker maker,
            ClassTree classTree, CompilationController controller,
            String methodBody, boolean addComment) throws IOException
    {
        WildcardTree wildCard = maker.Wildcard(Tree.Kind.UNBOUNDED_WILDCARD,
                null);
        ParameterizedTypeTree wildClass = maker.ParameterizedType(
                maker.QualIdent(Class.class.getCanonicalName()),
                Collections.singletonList(wildCard));
        ParameterizedTypeTree wildSet = maker.ParameterizedType(
                maker.QualIdent(Set.class.getCanonicalName()),
                Collections.singletonList(wildClass));
        ModifiersTree modifiersTree = maker.Modifiers(EnumSet
                .of(Modifier.PRIVATE));
        VariableTree newParam = maker.Variable(
                maker.Modifiers(Collections.<Modifier>emptySet()),
                "resources", wildSet, null);
        MethodTree methodTree = maker.Method(modifiersTree,
                JAXRSConstants.GET_REST_RESOURCE_CLASSES2, maker.Type("void"),
                Collections.<TypeParameterTree> emptyList(),
                Arrays.asList(newParam),
                Collections.<ExpressionTree> emptyList(), methodBody,
                null);
        if (addComment) {
            Comment comment = Comment.create(Comment.Style.JAVADOC,// -2, -2, -2,
                    "Do not modify "+JAXRSConstants.GET_REST_RESOURCE_CLASSES2+"() method.\n"
                    + "It is automatically populated with\n"
                    + "all resources defined in the project.\n"
                    + "If required, comment out calling this method in getClasses()."); // NOI18N
            maker.addComment(methodTree, comment, true);
        }
        return maker.addClassMember(classTree, methodTree);
    }

}
