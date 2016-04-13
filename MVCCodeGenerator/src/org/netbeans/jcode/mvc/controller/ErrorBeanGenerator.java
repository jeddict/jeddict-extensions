/**
 * Copyright [2016] Gaurav Gupta
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a
 * workingCopy of the License at
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
import com.sun.source.tree.ModifiersTree;
import java.io.IOException;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.WorkingCopy;
import static org.netbeans.jcode.core.util.Constants.NAMED;
import static org.netbeans.jcode.core.util.Constants.REQUEST_SCOPE;
import org.netbeans.jcode.core.util.JavaSourceHelper;
import org.netbeans.modules.j2ee.core.api.support.java.GenerationUtils;
import org.netbeans.modules.j2ee.core.api.support.java.SourceUtils;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Gaurav Gupta
 */
public class ErrorBeanGenerator {

    public static final String ERROR_BEAN_CLASS = "ErrorBean";

    public static FileObject generate(FileObject packageFolder) throws IOException {

        FileObject configFO = packageFolder.getFileObject(ERROR_BEAN_CLASS, "java");
        if (configFO != null) {
            return configFO;
        }

        FileObject appClass = GenerationUtils.createClass(packageFolder, ERROR_BEAN_CLASS, null);
        JavaSource javaSource = JavaSource.forFileObject(appClass);
        if (javaSource == null) {
            return null;
        }
        javaSource.runModificationTask(new Task<WorkingCopy>() {

            @Override
            public void run(WorkingCopy workingCopy) throws Exception {
                workingCopy.toPhase(JavaSource.Phase.RESOLVED);
                GenerationUtils genUtils = GenerationUtils.newInstance(workingCopy);
                ClassTree classTree = JavaSourceHelper.getTopLevelClassTree(workingCopy);
                TypeElement classElement = SourceUtils.getPublicTopLevelElement(workingCopy);
                TreeMaker maker = workingCopy.getTreeMaker();

                ModifiersTree modifiersTree
                        = maker.addModifiersAnnotation(classTree.getModifiers(),
                                genUtils.createAnnotation(NAMED));
                modifiersTree
                        = maker.addModifiersAnnotation(modifiersTree,
                                genUtils.createAnnotation(REQUEST_SCOPE));

                ClassTree newClassTree;

                newClassTree = JavaSourceHelper.addBeanProperty(genUtils, maker, classElement, classTree, "property", String.class.getName());
                newClassTree = JavaSourceHelper.addBeanProperty(genUtils, maker, classElement, newClassTree, "value", Object.class.getName());
                newClassTree = JavaSourceHelper.addBeanProperty(genUtils, maker, classElement, newClassTree, "message", String.class.getName());

                newClassTree = maker.Class(
                        modifiersTree,
                        newClassTree.getSimpleName(),
                        newClassTree.getTypeParameters(),
                        newClassTree.getExtendsClause(),
                        newClassTree.getImplementsClause(),
                        newClassTree.getMembers());

                workingCopy.rewrite(classTree, newClassTree);
            }

        }).commit();
        return appClass;
    }

}
