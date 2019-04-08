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
package io.github.jeddict.cdi.logger;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.VariableTree;
import static io.github.jeddict.jcode.CDIConstants.INJECTION_POINT;
import static io.github.jeddict.jcode.CDIConstants.PRODUCES;
import io.github.jeddict.jcode.task.progress.ProgressHandler;
import static io.github.jeddict.jcode.util.Constants.JAVA_EXT;
import static io.github.jeddict.jcode.util.Constants.LOGGER;
import io.github.jeddict.jcode.util.JavaSourceHelper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.modules.j2ee.core.api.support.java.GenerationUtils;
import org.netbeans.modules.j2ee.core.api.support.java.SourceUtils;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Gaurav Gupta
 */
public class LoggerProducerGenerator {

    public static final String LOGGER_PRODUCER_CLASS = "LoggerProducer";
    public static final String METHOD_NAME = "createLogger";
    public static final String INJECTION_POINT_VAR = "injectionPoint";
    private static final String METHID_BODY = "return Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getCanonicalName());";

    public static FileObject generate(FileObject toDir, ProgressHandler handler) throws IOException {

        FileObject fileObject = toDir.getFileObject(LOGGER_PRODUCER_CLASS, JAVA_EXT);
        if (fileObject != null) {
            fileObject.delete();
        }

        FileObject appClass = GenerationUtils.createClass(toDir, LOGGER_PRODUCER_CLASS, null);
        JavaSource javaSource = JavaSource.forFileObject(appClass);
        if (javaSource == null) {
            return null;
        }
        handler.progress(LOGGER_PRODUCER_CLASS);
        javaSource.runModificationTask((WorkingCopy workingCopy) -> {
            workingCopy.toPhase(JavaSource.Phase.RESOLVED);
            TypeElement classElement = SourceUtils.getPublicTopLevelElement(workingCopy);
            ClassTree classTree = JavaSourceHelper.getTopLevelClassTree(workingCopy);
            TreeMaker maker = workingCopy.getTreeMaker();
            GenerationUtils genUtils = GenerationUtils.newInstance(workingCopy);
            List<Tree> members = new ArrayList<>(classTree.getMembers());
            
            ModifiersTree methodModifiersTree = maker.Modifiers(EnumSet.of(Modifier.PUBLIC));
            methodModifiersTree = maker.addModifiersAnnotation(methodModifiersTree, genUtils.createAnnotation(PRODUCES));
            Tree returnType = genUtils.createType(LOGGER, classElement);
            List<VariableTree> params = new ArrayList<>();
            ModifiersTree paramModifier = maker.Modifiers(Collections.<Modifier>emptySet());
            VariableTree injectionPoint = maker.Variable(paramModifier,
                    INJECTION_POINT_VAR, genUtils.createType(INJECTION_POINT, classElement), null); //NOI18N
            params.add(injectionPoint);
            MethodTree methodTree = maker.Method(methodModifiersTree,
                    METHOD_NAME, returnType,
                    Collections.<TypeParameterTree>emptyList(),
                    params,
                    Collections.<ExpressionTree>emptyList(),
                    "{" + METHID_BODY + "}", null);
            members.add(methodTree);
            
            ClassTree newClassTree = maker.Class(
                    classTree.getModifiers(),
                    classTree.getSimpleName(),
                    classTree.getTypeParameters(),
                    classTree.getExtendsClause(),
                    classTree.getImplementsClause(),
                    members);
            
            workingCopy.rewrite(classTree, newClassTree);
        }).commit();
        return appClass;
    }

}
