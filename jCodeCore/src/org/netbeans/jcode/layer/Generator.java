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
package org.netbeans.jcode.layer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.jcode.entity.info.EntityResourceBeanModel;
import org.netbeans.jcode.task.progress.ProgressHandler;
import org.openide.util.Lookup;

/**
 *
 * @author Gaurav Gupta
 */
public interface Generator {

    void execute(Project project, SourceGroup source, EntityResourceBeanModel model, ProgressHandler handler) throws IOException;

    static TechContext get(String className) {
        TechContext context = null;
        for (Generator codeGenerator : Lookup.getDefault().lookupAll(Generator.class)) {
            if (codeGenerator.getClass().getSimpleName().equals(className)) {
                context = new TechContext(codeGenerator);
            }
        }
        return context;
    }

    static List<TechContext> getBusinessService() {
        List<TechContext> codeGenerators = new ArrayList<>();
        Lookup.getDefault().lookupAll(Generator.class).stream().forEach((Generator codeGenerator) -> {
            Technology technology = codeGenerator.getClass().getAnnotation(Technology.class);
            if (technology.type() == Technology.Type.BUSINESS) {
                codeGenerators.add(new TechContext(codeGenerator));
            }
        });
        return codeGenerators;
    }

    static List<TechContext> getController(TechContext parentCodeGenerator) {
        List<TechContext> codeGenerators = new ArrayList<>();

        Lookup.getDefault().lookupAll(Generator.class).stream().forEach((Generator codeGenerator) -> {
            Technology technology = codeGenerator.getClass().getAnnotation(Technology.class);
            if (technology.type() == Technology.Type.CONTROLLER) {
                if (codeGenerator.getClass() == DefaultControllerLayer.class) {
                    codeGenerators.add(new TechContext(codeGenerator));
                }
                for (Class<? extends Generator> genClass : technology.parents()) {
                    if (genClass == parentCodeGenerator.getGenerator().getClass()) {
                        codeGenerators.add(new TechContext(codeGenerator));
                        break;
                    }
                }
            }
        });
        return codeGenerators;
    }

    static List<TechContext> getViewer(TechContext parentCodeGenerator) {
        List<TechContext> codeGenerators = new ArrayList<>();
        Lookup.getDefault().lookupAll(Generator.class).stream().forEach((Generator codeGenerator) -> {
            Technology technology = codeGenerator.getClass().getAnnotation(Technology.class);
            if (codeGenerator.getClass() == DefaultViewerLayer.class) {
                codeGenerators.add(new TechContext(codeGenerator));
            }
            if (technology.type() == Technology.Type.VIEWER) {
                for (Class<? extends Generator> genClass : technology.parents()) {
                    if (genClass == parentCodeGenerator.getGenerator().getClass()) {
                        codeGenerators.add(new TechContext(codeGenerator));
                        break;
                    }
                }
            }
        });
        return codeGenerators;
    }
}
