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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityResourceBeanModel {

    private final EntityResourceModelBuilder builder;
    private final Map<String, EntityClassInfo> entityInfos;
    private boolean valid;

    /**
     * Creates a new instance of ResourceBeanModel
     * @param builder
     */
    public EntityResourceBeanModel(EntityResourceModelBuilder builder) {
        this.builder = builder;
        entityInfos = new HashMap<>();
    }

    public EntityClassInfo getEntityInfo(String fqn) {
        return entityInfos.get(fqn);
    }

    public List<EntityClassInfo> getEntityInfos() {
        return new ArrayList<>(entityInfos.values());
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean v) {
        valid = v;
    }

    public EntityResourceModelBuilder getBuilder() {
        return builder;
    }

    void addEntityInfo(String fqn, EntityClassInfo info) {
        if (info == null) {
            return;
        }
        entityInfos.put(fqn, info);
    }
}
