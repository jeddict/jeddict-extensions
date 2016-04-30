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
import java.util.Collection;
import java.util.List;
import org.netbeans.api.project.Project;
import org.netbeans.jcode.entity.info.EntityClassInfo;
import org.netbeans.jcode.entity.info.EntityClassInfo.FieldInfo;
import org.netbeans.jcode.entity.info.EntityResourceBeanModel;

import org.netbeans.jcode.generator.AbstractGenerator;
import org.netbeans.jcode.generator.internal.util.Util;
import org.netbeans.jcode.jpa.util.PersistenceHelper.PersistenceUnit;

/**
 *
 * @author Gaurav Gupta
 */
public abstract class BaseApplicationGenerator extends AbstractGenerator {

    private PersistenceUnit persistenceUnit;
    private EntityResourceBeanModel model;
    private Project project;

    /**
     * Creates a new instance of EntityRESTServicesCodeGenerator
     */
    public void initialize(EntityResourceBeanModel model, Project project,PersistenceUnit persistenceUnit) {
        this.model = model;
        this.project = project;
        this.persistenceUnit = persistenceUnit;
    }

    protected void preGenerate(List<String> fqnEntities) throws IOException {
    }

    protected void configurePersistence() {
    }

    protected EntityClassInfo getEntityClassInfo(String className) {
        return model.getBuilder().getEntityClassInfo(className);
    }

    protected Project getProject() {
        return project;
    }

    protected EntityResourceBeanModel getModel() {
        return model;
    }

    protected PersistenceUnit getPersistenceUnit() {
        return persistenceUnit;
    }

    protected int getEntitiesCount() {
        return getModel().getEntityInfos().size();
    }

    protected String getIdFieldToUriStmt(FieldInfo idField) {
        String getterName = Util.getGetterName(idField);

        if (idField.isEmbeddedId()) {
            Collection<FieldInfo> fields = idField.getFieldInfos();
            StringBuilder stmt = new StringBuilder();
            int index = 0;

            for (FieldInfo f : fields) {
                if (index++ > 0) {
                    stmt.append(" + \",\" + ");             // NOI18N
                }
                stmt.append("entity.");                   // NOI18N
                stmt.append(getterName);
                stmt.append("().");                       // NOI18N
                stmt.append(Util.getGetterName(f));
                stmt.append("()");                       // NOI18N
            }

            return stmt.toString();
        } else {
            return "entity." + getterName + "()";           // NOI18N
        }
    }

}
