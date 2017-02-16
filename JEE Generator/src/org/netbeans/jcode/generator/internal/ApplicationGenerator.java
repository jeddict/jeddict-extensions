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
import java.util.HashSet;
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
import org.netbeans.jcode.jpa.util.PersistenceHelper.PersistenceUnit;
import org.netbeans.jcode.layer.ConfigData;
import org.netbeans.jcode.layer.Generator;
import org.netbeans.jcode.layer.TechContext;

import org.netbeans.jcode.rest.util.RestUtils;
import org.netbeans.jcode.stack.config.data.ApplicationConfigData;
import org.netbeans.jcode.stack.config.data.LayerConfigData;
import org.netbeans.jcode.task.progress.ProgressHandler;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.openide.filesystems.FileObject;
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
        layerConfigData.put(bussinesLayerConfig.getPanel().getConfigData().getClass(), bussinesLayerConfig.getPanel().getConfigData());
        layerConfigData.putAll(bussinesLayerConfig.getSiblingTechContext()
                .stream().map(context -> context.getPanel().getConfigData()).collect(toMap(data -> data.getClass(), identity(), (d1, d2) -> d1)));

        TechContext controllerLayerConfig = applicationConfigData.getControllerTechContext();
        TechContext viewerLayerConfig = null;
        if (controllerLayerConfig != null) {
            controllerLayerConfig.getPanel().getConfigData().setParentLayerConfigData(bussinesLayerConfig.getPanel().getConfigData());
            layerConfigData.put(controllerLayerConfig.getPanel().getConfigData().getClass(), controllerLayerConfig.getPanel().getConfigData());
            layerConfigData.putAll(controllerLayerConfig.getSiblingTechContext()
                    .stream().map(context -> context.getPanel().getConfigData()).collect(toMap(data -> data.getClass(), identity(), (d1, d2) -> d1)));

            viewerLayerConfig = applicationConfigData.getViewerTechContext();
            if (viewerLayerConfig != null) {
                viewerLayerConfig.getPanel().getConfigData().setParentLayerConfigData(controllerLayerConfig.getPanel().getConfigData());
                layerConfigData.put(viewerLayerConfig.getPanel().getConfigData().getClass(), viewerLayerConfig.getPanel().getConfigData());
                layerConfigData.putAll(viewerLayerConfig.getSiblingTechContext()
                        .stream().map(context -> context.getPanel().getConfigData()).collect(toMap(data -> data.getClass(), identity(), (d1, d2) -> d1)));
            }
        }

        inject(bussinesLayerConfig.getGenerator(), applicationConfigData, layerConfigData, handler);
        for (TechContext context : bussinesLayerConfig.getSiblingTechContext()) {
            inject(context.getGenerator(), applicationConfigData, layerConfigData, handler);
        }

        if (controllerLayerConfig != null) {
            inject(controllerLayerConfig.getGenerator(), applicationConfigData, layerConfigData, handler);
            for (TechContext context : controllerLayerConfig.getSiblingTechContext()) {
                inject(context.getGenerator(), applicationConfigData, layerConfigData, handler);
            }
        }

        if (viewerLayerConfig != null) {
            inject(viewerLayerConfig.getGenerator(), applicationConfigData, layerConfigData, handler);
            for (TechContext context : viewerLayerConfig.getSiblingTechContext()) {
                inject(context.getGenerator(), applicationConfigData, layerConfigData, handler);
            }
        }

    }

    private void generateCRUD() throws IOException {
        TechContext bussinesLayerConfig = applicationConfigData.getBussinesTechContext();
        if (bussinesLayerConfig == null) {
            return;
        }
        bussinesLayerConfig.getGenerator().execute();
        for (TechContext context : bussinesLayerConfig.getSiblingTechContext()) {
            context.getGenerator().execute();
        }

        TechContext controllerLayerConfig = applicationConfigData.getControllerTechContext();
        if (controllerLayerConfig == null) {
            return;
        }
        controllerLayerConfig.getGenerator().execute();
        for (TechContext context : controllerLayerConfig.getSiblingTechContext()) {
            context.getGenerator().execute();
        }

        TechContext viewerLayerConfig = applicationConfigData.getViewerTechContext();
        if (viewerLayerConfig == null) {
            return;
        }
        viewerLayerConfig.getGenerator().execute();
        for (TechContext context : viewerLayerConfig.getSiblingTechContext()) {
            context.getGenerator().execute();
        }

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
        if (type.getSuperclass() != null) {
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
