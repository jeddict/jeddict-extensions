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
import java.util.Arrays;
import java.util.List;
import org.openide.util.Lookup;

/**
 *
 * @author Gaurav Gupta
 */
public interface Generator {

    void execute() throws IOException;

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
        return getTechContexts(null, Technology.Type.BUSINESS);
    }

    static List<TechContext> getController(TechContext parentCodeGenerator) {
         return getTechContexts(parentCodeGenerator, Technology.Type.CONTROLLER);
    }

    static List<TechContext> getViewer(TechContext parentCodeGenerator) {
        return getTechContexts(parentCodeGenerator, Technology.Type.VIEWER);
    }
    
    
    
    
    
    static List<TechContext> getSiblingTechContexts(Generator rootCodeGenerator) {
        List<TechContext> siblingCodeGenerators = new ArrayList<>();
        Lookup.getDefault().lookupAll(Generator.class).stream().forEach((Generator codeGenerator) -> {
            Technology technology = codeGenerator.getClass().getAnnotation(Technology.class);
            if (technology.type() == Technology.Type.NONE
                    && Arrays.stream(technology.sibling()).filter(sibling -> sibling == rootCodeGenerator.getClass()).findAny().isPresent()) {
                siblingCodeGenerators.add(new TechContext(codeGenerator));
            }
        });
        return siblingCodeGenerators;
    }
    
    static List<TechContext> getTechContexts(TechContext parentCodeGenerator, Technology.Type type) {
        List<TechContext> codeGenerators = new ArrayList<>();//default <none> type //LayerConfigPanel
        List<TechContext> customCodeGenerators = new ArrayList<>();
       
        Lookup.getDefault().lookupAll(Generator.class).stream().forEach((Generator codeGenerator) -> {
            Technology technology = codeGenerator.getClass().getAnnotation(Technology.class);
            if (technology.type() == type) {
                if (technology.panel() == org.netbeans.jcode.stack.config.panel.LayerConfigPanel.class) {
                    codeGenerators.add(new TechContext(codeGenerator));
                } else {
                    if (parentCodeGenerator != null) {
                        for (Class<? extends Generator> genClass : technology.parents()) {
                            if (genClass == parentCodeGenerator.getGenerator().getClass()) {
                                customCodeGenerators.add(new TechContext(codeGenerator));
                                break;
                            }
                        }
                        for (Class<? extends Generator> genClass : parentCodeGenerator.getTechnology().children()) {
                            if (genClass == codeGenerator.getClass()) {
                                customCodeGenerators.add(new TechContext(codeGenerator));
                                break;
                            }
                        }
                    } else {
                        customCodeGenerators.add(new TechContext(codeGenerator));
                    }
                }
            }
        });
        codeGenerators.addAll(customCodeGenerators);
        return codeGenerators;
    }
}
