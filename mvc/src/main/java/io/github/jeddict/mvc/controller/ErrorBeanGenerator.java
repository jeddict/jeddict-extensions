/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.mvc.controller;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ModifiersTree;
import java.io.IOException;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.WorkingCopy;
import static io.github.jeddict.jcode.util.Constants.JAVA_EXT;
import static io.github.jeddict.jcode.util.Constants.NAMED;
import static io.github.jeddict.jcode.util.Constants.REQUEST_SCOPE;
import io.github.jeddict.mvc.JavaSourceHelper;
import io.github.jeddict.jcode.task.progress.ProgressHandler;
import io.github.jeddict.mvc.GenerationUtils;
import io.github.jeddict.mvc.SourceUtils;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Gaurav Gupta
 */
public class ErrorBeanGenerator {

    public static final String ERROR_BEAN_CLASS = "ErrorBean";

    public static FileObject generate(FileObject packageFolder, ProgressHandler handler) throws IOException {

        FileObject configFO = packageFolder.getFileObject(ERROR_BEAN_CLASS, JAVA_EXT);
        if (configFO != null) {
            return configFO;
        }

        handler.progress(ERROR_BEAN_CLASS);
        FileObject appClass = GenerationUtils.createClass(packageFolder, ERROR_BEAN_CLASS, null);
        JavaSource javaSource = JavaSource.forFileObject(appClass);
        if (javaSource == null) {
            return null;
        }
        javaSource.runModificationTask((WorkingCopy workingCopy) -> {
            workingCopy.toPhase(JavaSource.Phase.RESOLVED);
            GenerationUtils genUtils = GenerationUtils.newInstance(workingCopy);
            ClassTree classTree = io.github.jeddict.jcode.util.JavaSourceHelper.getTopLevelClassTree(workingCopy);
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
        }).commit();
        return appClass;
    }

}
