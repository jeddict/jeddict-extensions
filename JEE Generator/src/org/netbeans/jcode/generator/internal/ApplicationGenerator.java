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
package org.netbeans.jcode.generator.internal;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import org.netbeans.api.project.Project;
import org.netbeans.jcode.core.util.POMManager;
import org.netbeans.jcode.generator.AbstractGenerator;
import org.netbeans.jcode.jpa.util.PersistenceHelper;
import org.netbeans.jcode.layer.ConfigData;
import org.netbeans.jcode.layer.Generator;
import org.netbeans.jcode.layer.TechContext;

import org.netbeans.jcode.rest.util.RestUtils;
import org.netbeans.jcode.stack.config.data.ApplicationConfigData;
import org.netbeans.jcode.stack.config.data.LayerConfigData;
import org.netbeans.jcode.task.progress.ProgressHandler;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.openide.util.Exceptions;

/**
 *
 * @author Gaurav Gupta
 */
public class ApplicationGenerator extends AbstractGenerator {

    private Project project;
    private ApplicationConfigData applicationConfigData;
    private ProgressHandler handler;
    private Map<Class<? extends LayerConfigData>, LayerConfigData> layerConfigData;

    @Override
    public void initialize(ApplicationConfigData applicationConfigData, ProgressHandler progressHandler) {
        this.applicationConfigData = applicationConfigData;
        this.handler = progressHandler;
        this.project = applicationConfigData.getProject();
        injectData();
//        this.persistenceUnit = new PersistenceHelper(project).getPersistenceUnit();
    }


    @Override
    public void generate() {
        try {
            if (handler != null) {
                initProgressReporting(handler);
            }
            EntityMappings entityMappings = applicationConfigData.getEntityMappings();
            Set<String> entities = entityMappings.getFQEntity().collect(toSet());
            //Make necessary changes to the persistence.xml
            new PersistenceHelper(project).configure(entities, !RestUtils.hasJTASupport(project));
            generateCRUD();
            finishProgressReporting();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

        @Override
    public void preGeneration() {
        TechContext bussinesLayerConfig = applicationConfigData.getBussinesTechContext();
        if (bussinesLayerConfig == null) {
            return;
        }
        bussinesLayerConfig.getGenerator().preExecute();
        for (TechContext context : bussinesLayerConfig.getSiblingTechContext()) {
            context.getGenerator().preExecute();
        }

        TechContext controllerLayerConfig = applicationConfigData.getControllerTechContext();
        if (controllerLayerConfig == null) {
            return;
        }
        controllerLayerConfig.getGenerator().preExecute();
        for (TechContext context : controllerLayerConfig.getSiblingTechContext()) {
            context.getGenerator().preExecute();
        }

        TechContext viewerLayerConfig = applicationConfigData.getViewerTechContext();
        if (viewerLayerConfig == null) {
            return;
        }
        viewerLayerConfig.getGenerator().preExecute();
        for (TechContext context : viewerLayerConfig.getSiblingTechContext()) {
            context.getGenerator().preExecute();
        }
    }

    @Override
    public void postGeneration() {
        
        TechContext bussinesLayerConfig = applicationConfigData.getBussinesTechContext();
        if (bussinesLayerConfig == null) {
            return;
        }
        bussinesLayerConfig.getGenerator().postExecute();
        for (TechContext context : bussinesLayerConfig.getSiblingTechContext()) {
            context.getGenerator().postExecute();
        }

        TechContext controllerLayerConfig = applicationConfigData.getControllerTechContext();
        if (controllerLayerConfig == null) {
            return;
        }
        controllerLayerConfig.getGenerator().postExecute();
        for (TechContext context : controllerLayerConfig.getSiblingTechContext()) {
            context.getGenerator().postExecute();
        }

        TechContext viewerLayerConfig = applicationConfigData.getViewerTechContext();
        if (viewerLayerConfig == null) {
            return;
        }
        viewerLayerConfig.getGenerator().postExecute();
        for (TechContext context : viewerLayerConfig.getSiblingTechContext()) {
            context.getGenerator().postExecute();
        }
    }

    private void injectData() {
        layerConfigData = new HashMap<>();
        TechContext bussinesLayerConfig = applicationConfigData.getBussinesTechContext();
        if (bussinesLayerConfig == null) {
            return;
        }
        storeLayerConfigData(bussinesLayerConfig);
        
        TechContext controllerLayerConfig = applicationConfigData.getControllerTechContext();
        TechContext viewerLayerConfig = null;
        if (controllerLayerConfig != null) {
            controllerLayerConfig.getPanel().getConfigData().setParentLayerConfigData(bussinesLayerConfig.getPanel().getConfigData());
            storeLayerConfigData(controllerLayerConfig);

            viewerLayerConfig = applicationConfigData.getViewerTechContext();
            if (viewerLayerConfig != null) {
                viewerLayerConfig.getPanel().getConfigData().setParentLayerConfigData(controllerLayerConfig.getPanel().getConfigData());
                storeLayerConfigData(viewerLayerConfig);
            }
        }

        inject(bussinesLayerConfig);
        if (controllerLayerConfig != null) {
            inject(controllerLayerConfig);
        }
        if (viewerLayerConfig != null) {
            inject(viewerLayerConfig);
        }
    }
    
    private void storeLayerConfigData(TechContext rootTechContext) {
        layerConfigData.put(rootTechContext.getPanel().getConfigData().getClass(), rootTechContext.getPanel().getConfigData());
        for (TechContext context : rootTechContext.getSiblingTechContext()) {
            storeLayerConfigData(context);
        }
    }
    
    private void inject(TechContext rootTechContext) {
        inject(rootTechContext.getGenerator(), applicationConfigData, layerConfigData, handler);
        for (TechContext context : rootTechContext.getSiblingTechContext()) {
            inject(context);
        }
    }

    private void execute(TechContext rootTechContext) throws IOException{
        rootTechContext.getGenerator().execute();
        for (TechContext siblingTechContext : rootTechContext.getSiblingTechContext()) {
            execute(siblingTechContext);
        }
    }
    
    private void generateCRUD() throws IOException {
        TechContext bussinesLayerConfig = applicationConfigData.getBussinesTechContext();
        if (bussinesLayerConfig == null) {
            return;
        }
        execute(bussinesLayerConfig);

        TechContext controllerLayerConfig = applicationConfigData.getControllerTechContext();
        if (controllerLayerConfig == null) {
            return;
        }
        execute(controllerLayerConfig);

        TechContext viewerLayerConfig = applicationConfigData.getViewerTechContext();
        if (viewerLayerConfig == null) {
            return;
        }
        execute(viewerLayerConfig);

        if (POMManager.isMavenProject(project)) {
            POMManager.reload(project);
        }

//        PersistenceUtil.getPersistenceUnit(getProject(), applicationConfigData.getEntityMappings().getPersistenceUnitName()).ifPresent(pud -> {
//            try {
//                ProviderUtil.getPUDataObject(getProject()).save();
//            } catch (InvalidPersistenceXmlException ex) {
//                Exceptions.printStackTrace(ex);
//            }
//        });
    }

    private List<Field> getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));
        if (type.getSuperclass()!= Object.class) {
            fields = getAllFields(fields, type.getSuperclass());
        }
        return fields;
    }

    private void inject(Generator instance, ApplicationConfigData applicationConfigData, Map<Class<? extends LayerConfigData>, LayerConfigData> layerConfigData, ProgressHandler handler) {
        List<Field> fields = getAllFields(new LinkedList<>(), instance.getClass());
        for (Field field : fields) {
            if (field.isAnnotationPresent(ConfigData.class)) {
                field.setAccessible(true);
                try {
                    if (field.getGenericType() == ApplicationConfigData.class) {
                        field.set(instance, applicationConfigData);
                    } else if (field.getGenericType() == EntityMappings.class) {
                        field.set(instance, applicationConfigData.getEntityMappings());
                    } else if (field.getType().isAssignableFrom(handler.getClass())) {
                        field.set(instance, handler);
                    } else if (field.getType().isAssignableFrom(applicationConfigData.getProject().getClass())) {
                        field.set(instance, applicationConfigData.getProject());
                    } else if (field.getType().isAssignableFrom(applicationConfigData.getSourceGroup().getClass())) {
                        field.set(instance, applicationConfigData.getSourceGroup());
                    } else if (LayerConfigData.class.isAssignableFrom(field.getType())) {
                        field.set(instance, layerConfigData.get(field.getType()));
                    }
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected int getTotalWorkUnits() {
        float unit = 1.5f;
        float webUnit = 5f;
        float count = applicationConfigData.getEntityMappings().getGeneratedEntity().count();
        if (applicationConfigData.getBussinesTechContext() != null) {
            count = count + count * unit;
        }
        if (applicationConfigData.getControllerTechContext() != null) {
            count = count + count * unit;
        }
        if (applicationConfigData.getViewerTechContext() != null) {
            count = count + count * webUnit;
        }
        return (int) count;
    }

}
