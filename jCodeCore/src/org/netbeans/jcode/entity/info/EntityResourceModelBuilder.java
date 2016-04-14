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
package org.netbeans.jcode.entity.info;

import static com.sun.javafx.scene.CameraHelper.project;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.jcode.entity.info.EntityClassInfo.FieldInfo;
import org.netbeans.jcode.core.util.SourceGroupSupport;
import org.openide.util.Exceptions;

public class EntityResourceModelBuilder {

    private Map<String, EntityClassInfo> entitiesInRelationMap;
    private Map<String, EntityClassInfo> allEntitiesClassInfoMap;
    private EntityResourceBeanModel model;

    /**
     * Creates a new instance of ModelBuilder
     */
  public EntityResourceModelBuilder(Project project, SourceGroup sourceGroup, Collection<String> entities) {
        entitiesInRelationMap = new HashMap<>();
        allEntitiesClassInfoMap = new HashMap<>();
        entities.stream().forEach((entity) -> {
        EntityClassInfo info = new EntityClassInfo(project,sourceGroup,entity, this);
            allEntitiesClassInfoMap.put(entity, info);
            if (!info.getFieldInfos().isEmpty()) {
                entitiesInRelationMap.put(entity, info);
            }
        });
    }

    public Set<EntityClassInfo> getEntityInfos() {
        return new HashSet<>(allEntitiesClassInfoMap.values());
    }

    public Set<String> getAllEntityNames() {
        return allEntitiesClassInfoMap.keySet();
    }

    public EntityClassInfo getEntityClassInfo(String type) {
        return allEntitiesClassInfoMap.get(type);
    }

    public EntityResourceBeanModel build() {
        model = new EntityResourceBeanModel(this);
        try {
            for (Entry<String, EntityClassInfo> entry : entitiesInRelationMap.entrySet()) {
                String fqn = entry.getKey();
                EntityClassInfo info = entry.getValue();
                model.addEntityInfo(fqn, info);
                computeRelationships(info);
            }

            model.setValid(true);

        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            model.setValid(false);
        }

        entitiesInRelationMap.clear();
        return model;
    }

    private void computeRelationships(EntityClassInfo info) {
        for (FieldInfo fieldInfo : info.getFieldInfos()) {
            if (fieldInfo.isRelationship()) {
                if (fieldInfo.isOneToMany() || fieldInfo.isManyToMany()) {
                    String typeArg = fieldInfo.getTypeArg();
                    EntityClassInfo classInfo = allEntitiesClassInfoMap.get(typeArg);
                    model.addEntityInfo(typeArg, classInfo);
                } else {
                    String type = fieldInfo.getType();
                    EntityClassInfo classInfo = allEntitiesClassInfoMap.get(type);
                    model.addEntityInfo(type, classInfo);
                }
            }
        }
    }
}
