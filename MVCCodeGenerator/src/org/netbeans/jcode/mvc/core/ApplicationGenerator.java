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
package org.netbeans.jcode.mvc.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import static java.util.stream.Collectors.toSet;

import org.netbeans.jcode.rest.util.RestUtils;
import org.netbeans.jcode.entity.info.EntityClassInfo;
import org.netbeans.jcode.core.util.PersistenceHelper;
import org.netbeans.jcode.mvc.util.Util;
import org.netbeans.jcode.stack.config.data.ApplicationConfigData;
import org.netbeans.jcode.task.progress.ProgressHandler;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Gaurav Gupta
 */
public class ApplicationGenerator extends BaseApplicationGenerator {

    private ApplicationConfigData applicationConfigData;
    @Override
    public Set<FileObject> generate(ProgressHandler handle, ApplicationConfigData applicationConfigData) throws IOException {
        this.applicationConfigData=applicationConfigData;
        
        if (handle != null) {
            initProgressReporting(handle);
        }

        //Make necessary changes to the persistence.xml
        new PersistenceHelper(getProject()).configure(getModel().getBuilder().
                getAllEntityNames(), !RestUtils.hasJTASupport(getProject()));
        configurePersistence();

        Set<String> entities = getModel().getEntityInfos().stream().map(ei -> ei.getEntityFqn()).collect(toSet());
        
        preGenerate(new ArrayList<>(entities));
        Util.generateCRUD(entities, getModel(), handle, applicationConfigData);
        
        
        finishProgressReporting();

        return new HashSet<>();
    }

    @Override
    protected int getTotalWorkUnits() {
        float unit = 1.5f;
        float webUnit = 5f;
        float count = getEntitiesCount();
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

}
