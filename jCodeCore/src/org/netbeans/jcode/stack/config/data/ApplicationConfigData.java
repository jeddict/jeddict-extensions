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
package org.netbeans.jcode.stack.config.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.jcode.layer.Generator;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Gaurav Gupta
 */
public class ApplicationConfigData implements Serializable {

    private Project project;
    private SourceGroup sourceGroup;
    private Map<String, FileObject> entities;

    private LayerConfigData bussinesLayerConfig;
    private LayerConfigData controllerLayerConfig;
    private LayerConfigData viewerLayerConfig;
    private Generator bussinesLayerGenerator;
    private Generator controllerLayerGenerator;
    private Generator viewerLayerGenerator;

    /**
     * @return the bussinesLayerConfig
     */
    public LayerConfigData getBussinesLayerConfig() {
        return bussinesLayerConfig;
    }

    /**
     * @param bussinesLayerConfig the bussinesLayerConfig to set
     */
    public void setBussinesLayerConfig(LayerConfigData bussinesLayerConfig) {
        this.bussinesLayerConfig = bussinesLayerConfig;
    }

    /**
     * @return the controllerLayerConfig
     */
    public LayerConfigData getControllerLayerConfig() {
        return controllerLayerConfig;
    }

    /**
     * @param controllerLayerConfig the controllerLayerConfig to set
     */
    public void setControllerLayerConfig(LayerConfigData controllerLayerConfig) {
        this.controllerLayerConfig = controllerLayerConfig;
    }

    /**
     * @return the viewerLayerConfig
     */
    public LayerConfigData getViewerLayerConfig() {
        return viewerLayerConfig;
    }

    /**
     * @param viewerLayerConfig the viewerLayerConfig to set
     */
    public void setViewerLayerConfig(LayerConfigData viewerLayerConfig) {
        this.viewerLayerConfig = viewerLayerConfig;
    }

    /**
     * @return the project
     */
    public Project getProject() {
        return project;
    }

    /**
     * @param project the project to set
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * @return the sourceGroup
     */
    public SourceGroup getSourceGroup() {
        return sourceGroup;
    }

    /**
     * @param sourceGroup the sourceGroup to set
     */
    public void setSourceGroup(SourceGroup sourceGroup) {
        this.sourceGroup = sourceGroup;
    }

    /**
     * @return the entities
     */
    public Map<String, FileObject> getEntities() {
        if (entities == null) {
            entities = new HashMap<>();
        }
        return entities;
    }

    /**
     * @param entities the entities to set
     */
    public void setEntities(Map<String, FileObject> entities) {
        this.entities = entities;
    }

    public FileObject getEntity(String key) {
        return getEntities().get(key);
    }

    public FileObject putEntity(String key, FileObject value) {
        return getEntities().put(key, value);
    }

    /**
     * @return the bussinesLayerGenerator
     */
    public Generator getBussinesLayerGenerator() {
        return bussinesLayerGenerator;
    }

    /**
     * @param bussinesLayerGenerator the bussinesLayerGenerator to set
     */
    public void setBussinesLayerGenerator(Generator bussinesLayerGenerator) {
        this.bussinesLayerGenerator = bussinesLayerGenerator;
    }

    /**
     * @return the controllerLayerGenerator
     */
    public Generator getControllerLayerGenerator() {
        return controllerLayerGenerator;
    }

    /**
     * @param controllerLayerGenerator the controllerLayerGenerator to set
     */
    public void setControllerLayerGenerator(Generator controllerLayerGenerator) {
        this.controllerLayerGenerator = controllerLayerGenerator;
    }

    /**
     * @return the viewerLayerGenerator
     */
    public Generator getViewerLayerGenerator() {
        return viewerLayerGenerator;
    }

    /**
     * @param viewerLayerGenerator the viewerLayerGenerator to set
     */
    public void setViewerLayerGenerator(Generator viewerLayerGenerator) {
        this.viewerLayerGenerator = viewerLayerGenerator;
    }

}
