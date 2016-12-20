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
import org.netbeans.jcode.core.util.POMManager;
import org.netbeans.jcode.core.util.PersistenceUtil;
import org.netbeans.jcode.jpa.util.PersistenceHelper;
import org.netbeans.jcode.layer.ConfigData;
import org.netbeans.jcode.layer.Generator;
import org.netbeans.jcode.layer.TechContext;

import org.netbeans.jcode.rest.util.RestUtils;
import org.netbeans.jcode.stack.config.data.ApplicationConfigData;
import org.netbeans.jcode.stack.config.data.LayerConfigData;
import org.netbeans.jcode.task.progress.ProgressHandler;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.modules.j2ee.persistence.provider.InvalidPersistenceXmlException;
import org.netbeans.modules.j2ee.persistence.provider.ProviderUtil;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author Gaurav Gupta
 */
public class ApplicationGenerator extends BaseApplicationGenerator {

    private ApplicationConfigData applicationConfigData;
    @Override
    public Set<FileObject> generate(ApplicationConfigData applicationConfigData, ProgressHandler handle) throws IOException {
        this.applicationConfigData=applicationConfigData;
        
        if (handle != null) {
            initProgressReporting(handle);
        }

        EntityMappings entityMappings = applicationConfigData.getEntityMappings();
        Set<String> entities = entityMappings.getFQEntity().collect(toSet());
        //Make necessary changes to the persistence.xml
        new PersistenceHelper(getProject()).configure(entities,!RestUtils.hasJTASupport(getProject()));
        configurePersistence();

        generateCRUD(applicationConfigData, handle);
        finishProgressReporting();
        return new HashSet<>();
    }

    @Override
    protected int getTotalWorkUnits() {
        float unit = 1.5f;
        float webUnit = 5f;
        float count = applicationConfigData.getEntityMappings().getConcreteEntity().count();
        if(applicationConfigData.getBussinesTechContext()!=null){
            count = count + count*unit;
        }
        if(applicationConfigData.getControllerTechContext()!=null){
            count = count + count*unit;
        }
        if(applicationConfigData.getViewerTechContext()!=null){
            count = count + count*webUnit;
        }
        return (int)count;
    }

    private void generateCRUD(ApplicationConfigData applicationConfigData, ProgressHandler handler) throws IOException {
        Map<Class<? extends LayerConfigData>,LayerConfigData> layerConfigData = new HashMap<>();
        TechContext bussinesLayerConfig = applicationConfigData.getBussinesTechContext();
        if (bussinesLayerConfig == null) {
            return;
        }
        layerConfigData.put(applicationConfigData.getBussinesTechContext().getPanel().getConfigData().getClass(),applicationConfigData.getBussinesTechContext().getPanel().getConfigData());
        layerConfigData.putAll(applicationConfigData.getBussinesTechContext().getSiblingTechContext()
                .stream().map(context -> context.getPanel().getConfigData()).collect(toMap(data -> data.getClass(), identity(), (d1,d2)->d1)));

        TechContext controllerLayerConfig = applicationConfigData.getControllerTechContext();
        TechContext viewerLayerConfig = null;
        if (controllerLayerConfig != null) {
            controllerLayerConfig.getPanel().getConfigData().setParentLayerConfigData(bussinesLayerConfig.getPanel().getConfigData());
            layerConfigData.put(applicationConfigData.getControllerTechContext().getPanel().getConfigData().getClass(),applicationConfigData.getControllerTechContext().getPanel().getConfigData());
            layerConfigData.putAll(applicationConfigData.getControllerTechContext().getSiblingTechContext()
                    .stream().map(context -> context.getPanel().getConfigData()).collect(toMap(data -> data.getClass(), identity(), (d1,d2)->d1)));

            viewerLayerConfig = applicationConfigData.getViewerTechContext();
            if (viewerLayerConfig != null) {
                viewerLayerConfig.getPanel().getConfigData().setParentLayerConfigData(controllerLayerConfig.getPanel().getConfigData());
                layerConfigData.put(applicationConfigData.getViewerTechContext().getPanel().getConfigData().getClass(),applicationConfigData.getViewerTechContext().getPanel().getConfigData());
                layerConfigData.putAll(applicationConfigData.getViewerTechContext().getSiblingTechContext()
                        .stream().map(context -> context.getPanel().getConfigData()).collect(toMap(data -> data.getClass(), identity(), (d1,d2)->d1)));
            }
        }
        
        inject(applicationConfigData.getBussinesTechContext().getGenerator(), applicationConfigData, layerConfigData, handler);
        applicationConfigData.getBussinesTechContext().getGenerator().execute();
        for (TechContext context : applicationConfigData.getBussinesTechContext().getSiblingTechContext()) {
            inject(context.getGenerator(), applicationConfigData, layerConfigData, handler);
            context.getGenerator().execute();
        }

        if (controllerLayerConfig != null) {
            inject(applicationConfigData.getControllerTechContext().getGenerator(), applicationConfigData, layerConfigData, handler);
            applicationConfigData.getControllerTechContext().getGenerator().execute();
            for (TechContext context : applicationConfigData.getControllerTechContext().getSiblingTechContext()) {
                inject(context.getGenerator(), applicationConfigData, layerConfigData, handler);
                context.getGenerator().execute();
            }
        }

        if (viewerLayerConfig != null) {
            inject(applicationConfigData.getViewerTechContext().getGenerator(), applicationConfigData, layerConfigData, handler);
            applicationConfigData.getViewerTechContext().getGenerator().execute();
            for (TechContext context : applicationConfigData.getViewerTechContext().getSiblingTechContext()) {
                inject(context.getGenerator(), applicationConfigData, layerConfigData, handler);
                context.getGenerator().execute();
            }
        }
        
        if(POMManager.isMavenProject(getProject())){
            POMManager.reload(getProject());
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

    private void inject(Generator instance, ApplicationConfigData applicationConfigData, Map<Class<? extends LayerConfigData>,LayerConfigData> layerConfigData, ProgressHandler handler) {
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
                    } else if(LayerConfigData.class.isAssignableFrom(field.getType())){
                        field.set(instance, layerConfigData.get(field.getType()));
                    }
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
