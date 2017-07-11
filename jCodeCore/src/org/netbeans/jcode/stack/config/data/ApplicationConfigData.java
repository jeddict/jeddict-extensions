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
import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.jcode.layer.TechContext;
import org.netbeans.jpa.modeler.spec.EntityMappings;

/**
 *
 * @author Gaurav Gupta
 */
public class ApplicationConfigData implements Serializable {

    private Project project;
    private SourceGroup sourceGroup;
    private boolean completeApplication;
    private EntityMappings entityMappings;
    
    private TechContext bussinesTechContext;
    private TechContext controllerTechContext;
    private TechContext viewerTechContext;
    
    private List<String> profiles = new ArrayList<>();
    
    
    public void addProfile(String profile){
        profiles.add(profile);
    }
    
    public void removeProfile(String profile){
        profiles.remove(profile);
    }
    
   public String getProfiles(){
        return String.join(",", profiles);
    }

    public TechContext getBussinesTechContext() {
        return bussinesTechContext;
    }

    public void setBussinesTechContext(TechContext bussinesTechContext) {
        this.bussinesTechContext = bussinesTechContext;
    }

    public TechContext getControllerTechContext() {
        return controllerTechContext;
    }

    public void setControllerTechContext(TechContext controllerTechContext) {
        this.controllerTechContext = controllerTechContext;
    }

    public TechContext getViewerTechContext() {
        return viewerTechContext;
    }

    public void setViewerTechContext(TechContext viewerLayerTechContext) {
        this.viewerTechContext = viewerLayerTechContext;
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
     * @return the entityMappings
     */
    public EntityMappings getEntityMappings() {
        return entityMappings;
    }

    /**
     * @param entityMappings the entityMappings to set
     */
    public void setEntityMappings(EntityMappings entityMappings) {
        this.entityMappings = entityMappings;
    }

    /**
     * @return the completeApplication
     */
    public boolean isCompleteApplication() {
        return completeApplication;
    }

    /**
     * @param completeApplication the completeApplication to set
     */
    public void setCompleteApplication(boolean completeApplication) {
        this.completeApplication = completeApplication;
    }

}
