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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import static java.util.stream.Collectors.toSet;
import org.netbeans.jcode.jpa.util.PersistenceHelper;
import org.netbeans.jcode.layer.ConfigData;
import org.netbeans.jcode.layer.Generator;

import org.netbeans.jcode.rest.util.RestUtils;
import org.netbeans.jcode.stack.config.data.ApplicationConfigData;
import org.netbeans.jcode.stack.config.data.LayerConfigData;
import org.netbeans.jcode.task.progress.ProgressHandler;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.openide.filesystems.FileObject;

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
        if(applicationConfigData.getBussinesLayerConfig()!=null){
            count = count + count*unit;
        }
        if(applicationConfigData.getControllerLayerConfig()!=null){
            count = count + count*unit;
        }
        if(applicationConfigData.getViewerLayerConfig()!=null){
            count = count + count*webUnit;
        }
        return (int)count;
    }

    private void generateCRUD(ApplicationConfigData applicationConfigData, ProgressHandler handler) throws IOException {

        LayerConfigData bussinesLayerConfig = applicationConfigData.getBussinesLayerConfig();
        LayerConfigData controllerLayerConfig = applicationConfigData.getControllerLayerConfig();
        if (controllerLayerConfig != null) {
            controllerLayerConfig.setParentLayerConfigData(bussinesLayerConfig);
        }
        LayerConfigData viewerLayerConfig = applicationConfigData.getViewerLayerConfig();
        if (viewerLayerConfig != null) {
            viewerLayerConfig.setParentLayerConfigData(controllerLayerConfig);
        }

        if (bussinesLayerConfig == null) {
            return;
        }
        inject(applicationConfigData.getBussinesLayerGenerator(), applicationConfigData, handler);
        applicationConfigData.getBussinesLayerGenerator().execute();

        if (controllerLayerConfig == null) {
            return;
        }
        inject(applicationConfigData.getControllerLayerGenerator(), applicationConfigData, handler);
        applicationConfigData.getControllerLayerGenerator().execute();

        if (viewerLayerConfig == null) {
            return;
        }
        inject(applicationConfigData.getViewerLayerGenerator(), applicationConfigData, handler);
        applicationConfigData.getViewerLayerGenerator().execute();

    }

    private List<Field> getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            fields = getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }

    private void inject(Generator instance, ApplicationConfigData applicationConfigData, ProgressHandler handler) {
        LayerConfigData bussinesLayerConfig = applicationConfigData.getBussinesLayerConfig();
        LayerConfigData controllerLayerConfig = applicationConfigData.getControllerLayerConfig();
        LayerConfigData viewerLayerConfig = applicationConfigData.getViewerLayerConfig();
        List<Field> fields = getAllFields(new LinkedList<>(), instance.getClass());
        for (Field field : fields) {
            if (field.isAnnotationPresent(ConfigData.class)) {
                field.setAccessible(true);
                try {
                    if (field.getGenericType() == ApplicationConfigData.class) {
                        field.set(instance, applicationConfigData);
                    } else if (field.getGenericType() == EntityMappings.class) {
                        field.set(instance, applicationConfigData.getEntityMappings());
                    } else if (bussinesLayerConfig != null && field.getGenericType() == bussinesLayerConfig.getClass()) {
                        field.set(instance, bussinesLayerConfig);
                    } else if (controllerLayerConfig != null && field.getGenericType() == controllerLayerConfig.getClass()) {
                        field.set(instance, controllerLayerConfig);
                    } else if (viewerLayerConfig != null && field.getGenericType() == viewerLayerConfig.getClass()) {
                        field.set(instance, viewerLayerConfig);
                    } else if (field.getType().isAssignableFrom(handler.getClass())) {
                        field.set(instance, handler);
                    } else if (field.getType().isAssignableFrom(applicationConfigData.getProject().getClass())) {
                        field.set(instance, applicationConfigData.getProject());
                    } else if (field.getType().isAssignableFrom(applicationConfigData.getSourceGroup().getClass())) {
                        field.set(instance, applicationConfigData.getSourceGroup());
                    }
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
