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

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import org.netbeans.jcode.core.util.SourceUtil;
import org.netbeans.jcode.mvc.util.MVCConstants;
import static org.netbeans.jcode.mvc.util.MVCConstants.INJECT;
import static org.netbeans.jcode.mvc.util.MVCConstants.MODELS;
import static org.netbeans.jcode.rest.util.Constants.BEAN_PARAM;
import org.netbeans.modules.j2ee.core.api.support.java.GenerationUtils;
import org.netbeans.modules.j2ee.core.api.support.java.JavaIdentifiers;
import org.netbeans.modules.j2ee.core.api.support.java.SourceUtils;
import org.netbeans.modules.j2ee.persistence.wizard.fromdb.FacadeGenerator;
import org.netbeans.jcode.rest.util.RestGenerationOptions;
import org.netbeans.jpa.modeler.rest.codegen.model.EntityClassInfo;
import org.netbeans.jpa.modeler.rest.codegen.model.EntityClassInfo.FieldInfo;
import org.netbeans.jpa.modeler.rest.codegen.model.EntityResourceBeanModel;
import org.netbeans.modules.websvc.rest.model.api.RestConstants;
import org.netbeans.jpa.modeler.rest.wizard.Util;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Gaurav Gupta
 */
public class MVCControllerGenerator implements FacadeGenerator {
    
    private static final String SUFFIX = "Controller"; //NOI18N
    
    private EntityResourceBeanModel model;
    
    public MVCControllerGenerator(){
    }
    
    public MVCControllerGenerator(EntityResourceBeanModel model){
        this.model = model;
    }

    /**
     * Generates the facade and the loca/remote interface(s) for thhe given
     * entity class.
     * <i>Package private visibility for tests</i>.
     * @param targetFolder the folder where the facade and interfaces are generated.
     * @param entityClass the FQN of the entity class for which the facade is generated.
     * @param pkg the package prefix for the generated facede.
     * @param hasRemote specifies whether a remote interface is generated.
     * @param hasLocal specifies whether a local interface is generated.
     * @param strategyClass the entity manager lookup strategy.
     *
     * @return a set containing the generated files.
     */
    @Override
    public Set<FileObject> generate(final Project project,
            final Map<String, String> entityNames,
            final FileObject targetFolder,
            final String entityFQN,
            final String idClass,
            final String pkg, 
            final boolean hasRemote,
            final boolean hasLocal,
            boolean overrideExisting) throws IOException {

        final Set<FileObject> createdFiles = new HashSet<>();
        final String entitySimpleName = JavaIdentifiers.unqualify(entityFQN);
        final String variableName = SourceUtil.lowerFirstChar(entitySimpleName);

        final FileObject facade = GenerationUtils.createClass(targetFolder, entitySimpleName + SUFFIX, null);
        createdFiles.add(facade);
        
        if ( model != null ) {
            Util.generatePrimaryKeyMethod(facade, entityFQN, model);
        }
       
        // add implements and extends clauses to the facade
        final Task<WorkingCopy> modificationTask = new Task<WorkingCopy>(){
            @Override
            public void run(WorkingCopy wc) throws Exception {
                wc.toPhase(Phase.RESOLVED);
                TypeElement classElement = SourceUtils.getPublicTopLevelElement(wc);
                ClassTree classTree = wc.getTrees().getTree(classElement);
                assert classTree != null;
                GenerationUtils genUtils = GenerationUtils.newInstance(wc);
                TreeMaker maker = wc.getTreeMaker();

               
                List<Tree> members = new ArrayList<>(classTree.getMembers());

                members.add(maker.Variable(maker.addModifiersAnnotation(genUtils.createModifiers(Modifier.PUBLIC),genUtils.createAnnotation(INJECT)), 
                        "model", genUtils.createType(MODELS, classElement), null));
                
                members.add(maker.Variable(maker.addModifiersAnnotation(genUtils.createModifiers(Modifier.PUBLIC),genUtils.createAnnotation(INJECT)), 
                        "facade", genUtils.createType("models.ses." + entitySimpleName + "Facade", classElement), null));

                List<RestGenerationOptions> restGenerationOptions = 
                    getRestFacadeMethodOptions(entityFQN, idClass);

                ModifiersTree publicModifiers = genUtils.createModifiers(
                        Modifier.PUBLIC);
                ModifiersTree paramModifier = maker.Modifiers(Collections.<Modifier>emptySet());
                for(RestGenerationOptions option: restGenerationOptions) {

                    ModifiersTree modifiersTree =
                            maker.addModifiersAnnotation(publicModifiers, 
                                    genUtils.createAnnotation(
                                            option.getRestMethod().getMethod()));

                     // add @Path annotation
                    String uriPath = option.getRestMethod().getUriPath();
                    if (uriPath != null) {
                        ExpressionTree annArgument = maker.Literal(uriPath);
                        modifiersTree =
                                maker.addModifiersAnnotation(modifiersTree,
                                genUtils.createAnnotation(RestConstants.PATH, 
                                        Collections.<ExpressionTree>singletonList(
                                                annArgument)));

                    }
                    
                    // add @Controller annotation
                        modifiersTree =
                                maker.addModifiersAnnotation(modifiersTree,
                                genUtils.createAnnotation(MVCConstants.CONTROLLER));
 

                    // add @View annotation
                    String view = option.getRestMethod().getView();
                    if (view != null) {
                        view =  view.replaceAll("<entity>", variableName);
                        ExpressionTree annArgument = maker.Literal(view);
                        modifiersTree =
                                maker.addModifiersAnnotation(modifiersTree,
                                genUtils.createAnnotation(MVCConstants.VIEW, 
                                        Collections.<ExpressionTree>singletonList(
                                                annArgument)));

                    }
                    
//                    // add @Produces annotation
//                    String[] produces = option.getProduces();
//                    if (produces != null) {
//                        ExpressionTree annArguments;
//                        if (produces.length == 1) {
//                            annArguments = mimeTypeTree(maker, produces[0]);
//                        } else {
//                            List<ExpressionTree> mimeTypes = new ArrayList<ExpressionTree>();
//                            for (int i=0; i< produces.length; i++) {
//                                mimeTypes.add(mimeTypeTree(maker, produces[i]));
//                            }
//                            annArguments = maker.NewArray(null,
//                                    Collections.<ExpressionTree>emptyList(), 
//                                    mimeTypes);
//                        }
//                        modifiersTree =
//                                maker.addModifiersAnnotation(modifiersTree,
//                                        genUtils.createAnnotation(
//                                                RestConstants.PRODUCE_MIME, 
//                                                Collections.<ExpressionTree>singletonList(annArguments)));
//                    }
//                    // add @Consumes annotation
//                    String[] consumes = option.getConsumes();
//                    if (consumes != null) {
//                        ExpressionTree annArguments;
//                        if (consumes.length == 1) {
//                            annArguments = mimeTypeTree(maker, consumes[0]);
//                        } else {
//                            List<ExpressionTree> mimeTypes = new ArrayList<ExpressionTree>();
//                            for (int i=0; i< consumes.length; i++) {
//                                mimeTypes.add(mimeTypeTree(maker, consumes[i]));
//                            }
//                            annArguments = maker.NewArray(null, 
//                                    Collections.<ExpressionTree>emptyList(), mimeTypes);
//                        }
//                        modifiersTree =
//                                maker.addModifiersAnnotation(modifiersTree,
//                                        genUtils.createAnnotation(
//                                                RestConstants.CONSUME_MIME, 
//                                                Collections.<ExpressionTree>singletonList(annArguments)));
//                    }

                    // create arguments list
                    List<VariableTree> vars = new ArrayList<>();
                    String[] paramNames = option.getParameterNames();
                    int paramLength = paramNames == null ? 0 : 
                        option.getParameterNames().length ;

                    if (paramLength > 0) {
                        String[] paramTypes = option.getParameterTypes();
                        String[] annotations = option.getParameterAnnoations();
                        String[] annotationValues = option.getParameterAnnoationValues();
                        
                        for (int i = 0; i<paramLength; i++) {
                            ModifiersTree pathParamTree = paramModifier;
                            if (annotations != null && annotations[i] != null) {
                                List<ExpressionTree> annArguments = null;
                                if(annotationValues[i]!=null){
                                annArguments = Collections.<ExpressionTree>singletonList(
                                            maker.Literal(annotationValues[i]));
                                 pathParamTree =
                                    maker.addModifiersAnnotation(paramModifier, 
                                            genUtils.createAnnotation(annotations[i], 
                                                    annArguments));
                                } else {
                                     pathParamTree =
                                    maker.addModifiersAnnotation(paramModifier, 
                                            genUtils.createAnnotation(annotations[i]));
                                }
                               
                            }
                            Tree paramTree = genUtils.createType(paramTypes[i], 
                                    classElement);
                            VariableTree var = maker.Variable(pathParamTree, 
                                    paramNames[i], paramTree, null); //NOI18N
                            vars.add(var);

                        }
                    }

                    Tree returnType = (option.getReturnType() == null || 
                            option.getReturnType().equals("void"))?  //NOI18N
                                            maker.PrimitiveType(TypeKind.VOID):
                                            genUtils.createType(option.getReturnType(), 
                                                    classElement);

                    members.add(maker.Method(
                                modifiersTree,
                                option.getRestMethod().getMethodName(),
                                returnType,
                                Collections.EMPTY_LIST,
                                vars,
                                (List<ExpressionTree>)Collections.EMPTY_LIST,
                                "{"+option.getBody().replaceAll("<entity>", variableName)+"}", //NOI18N
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
        JavaSource.forFileObject(facade).runWhenScanFinished( new Task<CompilationController>(){

            @Override
            public void run(CompilationController controller) throws Exception {
                controller.toPhase(Phase.ELEMENTS_RESOLVED);
                JavaSource.forFileObject(facade).runModificationTask(modificationTask).commit();
            }
            
            }, true).get();
        }
        catch( InterruptedException | ExecutionException e ){
            Logger.getLogger(MVCControllerGenerator.class.getCanonicalName()).
                log(Level.INFO, null ,e );
        }
        

        return createdFiles;
    }


    private List<RestGenerationOptions> getRestFacadeMethodOptions(
            String entityFQN, String idClass)
    {
        String paramArg = "java.lang.Character".equals(idClass) ? 
                "id.charAt(0)" : "id"; //NOI18N
        String idType = "id".equals(paramArg) ? idClass : "java.lang.String"; //NOI18N
        
        boolean needPathSegment = false;
        if ( model!= null ){
            EntityClassInfo entityInfo = model.getEntityInfo(entityFQN);
            if ( entityInfo!= null ){
                FieldInfo idFieldInfo = entityInfo.getIdFieldInfo();
                needPathSegment = idFieldInfo!=null && idFieldInfo.isEmbeddedId() 
                        && idFieldInfo.getType()!= null;
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
          if ( needPathSegment ){
            redirectUpdateOptions.setParameterTypes(new String[]{"javax.ws.rs.core.PathSegment"}); // NOI18N
        }  else {
            redirectUpdateOptions.setParameterTypes(new String[]{idType});     
        }
        StringBuilder updateBody = new StringBuilder();
        updateBody.append("model.put(\"<entity>\",facade.find(");                  //NOI18N
        updateBody.append(paramArg);
        updateBody.append("));");                                  //NOI18N
        redirectUpdateOptions.setBody( updateBody.toString());  

        
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
        if ( needPathSegment ){
            destroyOptions.setParameterTypes(new String[]{"javax.ws.rs.core.PathSegment"}); // NOI18N
            builder.append(idType);
            builder.append(" key=getPrimaryKey(id);\n");
            paramArg = "key";
        }
        else {
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
        if ( needPathSegment ){
            findOptions.setParameterTypes(new String[]{"javax.ws.rs.core.PathSegment"}); // NOI18N
        }
        else {
            findOptions.setParameterTypes(new String[]{idType});     
        }
        StringBuilder findBody = new StringBuilder(builder);
        findBody.append("model.put(\"<entity>\",facade.find(");                  //NOI18N
        findBody.append(paramArg);
        findBody.append("));");                                  //NOI18N
        findOptions.setBody( findBody.toString()); 

        RestGenerationOptions findAllOptions = new RestGenerationOptions();
        findAllOptions.setRestMethod(Operation.FIND_ALL);
        findAllOptions.setReturnType("void");//NOI18N
        findAllOptions.setProduces(new String[]{"application/xml", "application/json"});
        findAllOptions.setBody("model.put(\"<entity>List\",facade.findAll());");

//        RestGenerationOptions findSubOptions = new RestGenerationOptions();
//        findSubOptions.setRestMethod(Operation.FIND_RANGE);
//        findSubOptions.setReturnType("java.util.List<" + entityFQN + ">");//NOI18N
//        findSubOptions.setProduces(new String[]{"application/xml", 
//                "application/json"}); //NOI18N
//        findSubOptions.setParameterNames(new String[]{"from", "to"}); //NOI18N
//        findSubOptions.setParameterTypes(new String[]{"java.lang.Integer", 
//                "java.lang.Integer"}); //NOI18N
//        findSubOptions.setParameterAnnoations(new String[]{RestConstants.PATH_PARAM_ANNOTATION, RestConstants.PATH_PARAM_ANNOTATION});
//        findSubOptions.setParameterAnnoationValues(new String[]{"from", "to"}); //NOI18N
//        findSubOptions.setBody("return super.findRange(new int[] {from, to});"); //NOI18N

        RestGenerationOptions countOptions = new RestGenerationOptions();
        countOptions.setRestMethod(Operation.COUNT);
        countOptions.setReturnType("java.lang.String");//NOI18N
        countOptions.setProduces(new String[]{"text/plain"}); //NOI18N
        countOptions.setBody("return String.valueOf(facade.count());"); //NOI18N

        return Arrays.<RestGenerationOptions>asList(
                redirectCreateOptions,
                createOptions,
                redirectUpdateOptions,
                updateOptions,
                destroyOptions,
                findOptions,
                findAllOptions,
//                findSubOptions,
                countOptions);
    }


}
