/**
 * Copyright [2017] Gaurav Gupta
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
package org.netbeans.jcode.ng.main.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.netbeans.jcode.core.util.FileUtil.expandTemplate;
import static org.netbeans.jcode.core.util.StringHelper.camelCase;
import static org.netbeans.jcode.core.util.StringHelper.startCase;

/**
 *
 * @author jGauravGupta
 */
public class Needle {

    private String insertPointer;
    private String template;

    public Needle(String insertPointer, String template) {
        this.insertPointer = insertPointer;
        this.template = template;
    }

    /**
     * @return the insertPointer
     */
    public String getInsertPointer() {
        return insertPointer;
    }

    /**
     * @return the template
     */
    public String getTemplate() {
        return template;
    }
    
    public String getTemplate(NGApplicationConfig applicationConfig, List<NGEntity> ngEntities) {
        StringBuilder content = new StringBuilder();
        for (NGEntity entity : ngEntities) {
            Map<String, Object> param = new HashMap<>();
            param.put("entityFolderName", entity.getEntityFolderName());
            param.put("entityFileName", entity.getEntityFileName());
            param.put("entityClass", entity.getEntityClass());
            param.put("entityAngularJSName", entity.getEntityAngularJSName());
            param.put("entityInstance", entity.getEntityInstance());
            param.put("routerName", entity.getEntityStateName());
            param.put("enableTranslation", applicationConfig.isEnableTranslation());
            param.put("camelCase_routerName", camelCase(entity.getEntityStateName()));
            param.put("startCase_routerName", startCase(entity.getEntityStateName()));
            param.put("entityTranslationKeyMenu", entity.getEntityTranslationKeyMenu());
            param.put("startCase_entityClass", startCase(entity.getEntityClass()));
            param.put("appName", applicationConfig.getAngular2AppName());
            param.put("prefix", applicationConfig.getJhiPrefix());
            
            content.append(expandTemplate(template, param));
        }
        return content.toString();
    }

}
