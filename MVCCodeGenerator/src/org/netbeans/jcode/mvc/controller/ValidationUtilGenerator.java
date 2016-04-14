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
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.VariableTree;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.jcode.core.util.JavaSourceHelper;
import static org.netbeans.jcode.mvc.controller.ErrorBeanGenerator.ERROR_BEAN_CLASS;
import org.netbeans.jcode.mvc.controller.api.returntype.ControllerReturnType;
import org.netbeans.jcode.mvc.util.MVCConstants;
import static org.netbeans.jcode.mvc.util.MVCConstants.BINDING_RESULT;
import static org.netbeans.jcode.mvc.util.MVCConstants.VIEWABLE_UNQF;
import static org.netbeans.jcode.rest.util.RestConstant.RESPONSE;
import org.netbeans.modules.j2ee.core.api.support.java.GenerationUtils;
import org.netbeans.modules.j2ee.core.api.support.java.SourceUtils;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Gaurav Gupta
 */
public class ValidationUtilGenerator {

    public static final String VALIDATION_UTIL_CLASS = "ValidationUtil";
    public static final String METHOD_NAME = "getResponse";
    public static final String BINDING_RESULT_VAR = "validationResult";
    public static final String ERROR_BEAN_VAR = "error";
    private static final String METHID_BODY =  "            final java.util.Set<javax.validation.ConstraintViolation<?>> set = validationResult.getAllViolations();\n"
            + "            final javax.validation.ConstraintViolation<?> cv = set.iterator().next();\n"
            + "            final String property = cv.getPropertyPath().toString();\n\n"
            + "            error.setProperty(property.substring(property.lastIndexOf('.') + 1));\n"
            + "            error.setValue(cv.getInvalidValue());\n"
            + "            error.setMessage(cv.getMessage());\n\n"
            + "            return ";
            

    public static FileObject generate(MVCData mvcData, FileObject packageFolder, String webPath) throws IOException {

        FileObject configFO = packageFolder.getFileObject(VALIDATION_UTIL_CLASS, "java");
        if (configFO != null) {
            configFO.delete();
        }

        FileObject appClass = GenerationUtils.createClass(packageFolder, VALIDATION_UTIL_CLASS, null);
        JavaSource javaSource = JavaSource.forFileObject(appClass);
        if (javaSource == null) {
            return null;
        }
        javaSource.runModificationTask(new Task<WorkingCopy>() {

            @Override
            public void run(WorkingCopy workingCopy) throws Exception {
                workingCopy.toPhase(JavaSource.Phase.RESOLVED);
                TypeElement classElement = SourceUtils.getPublicTopLevelElement(workingCopy);
                ClassTree classTree = JavaSourceHelper.getTopLevelClassTree(workingCopy);
                TreeMaker maker = workingCopy.getTreeMaker();
                GenerationUtils genUtils = GenerationUtils.newInstance(workingCopy);
                ModifiersTree modifiersTree = maker.Modifiers(EnumSet.of(Modifier.PUBLIC, Modifier.STATIC));
                List<Tree> members = new ArrayList<>(classTree.getMembers());

                ExpressionTree packageTree = workingCopy.getCompilationUnit().getPackageName();
                String packageName = packageTree.toString();
                
                String errorFile = webPath + "/error.jsp";
                StringBuilder body = new StringBuilder();
                body.append("{").append(METHID_BODY);//.replaceAll(FOLDER_NAME_EXP, webPath);
                Tree returnType = null;
                if (mvcData.getReturnType() == ControllerReturnType.STRING) {
                    returnType = genUtils.createType(String.class.getName(), classElement);
                    body.append("\"").append(errorFile).append("\";");
                } else if (mvcData.getReturnType() == ControllerReturnType.VIEWABLE) {
                    returnType = genUtils.createType(MVCConstants.VIEWABLE, classElement);
                    body.append("new ").append(VIEWABLE_UNQF).append("(\"").append(errorFile).append("\");");
                } else if (mvcData.getReturnType() == ControllerReturnType.VIEW_ANNOTATION || mvcData.getReturnType() == ControllerReturnType.JAXRS_RESPONSE) {
                    returnType = genUtils.createType(RESPONSE, classElement);
                    body.append("Response.status(Response.Status.BAD_REQUEST).entity(\"").append(errorFile).append("\").build()");
                }
                body.append("}");
                
                List<VariableTree> vars = new ArrayList<>();
                ModifiersTree paramModifier = maker.Modifiers(Collections.<Modifier>emptySet());
                VariableTree validationResult = maker.Variable(paramModifier,
                       BINDING_RESULT_VAR , genUtils.createType(BINDING_RESULT, classElement), null); //NOI18N
                VariableTree errorBean = maker.Variable(paramModifier,
                        ERROR_BEAN_VAR , genUtils.createType(packageName + "." + ERROR_BEAN_CLASS, classElement), null); //NOI18N
                vars.add(validationResult);
                vars.add(errorBean);

                MethodTree methodTree = maker.Method(modifiersTree,
                        METHOD_NAME, returnType,
                        Collections.<TypeParameterTree>emptyList(),
                        vars,
                        Collections.<ExpressionTree>emptyList(),
                       body.toString(), null);
                members.add(methodTree);

                ClassTree newClassTree = maker.Class(
                        classTree.getModifiers(),
                        classTree.getSimpleName(),
                        classTree.getTypeParameters(),
                        classTree.getExtendsClause(),
                        classTree.getImplementsClause(),
                        members);

                workingCopy.rewrite(classTree, newClassTree);
            }

        }).commit();
        return appClass;
    }

}