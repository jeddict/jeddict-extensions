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
package org.netbeans.jpa.modeler.db.accessor;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.MappedSuperclassAccessor;
import org.netbeans.db.modeler.exception.DBValidationException;
import org.netbeans.jpa.modeler.spec.MappedSuperclass;
import org.netbeans.jpa.modeler.spec.workspace.WorkSpace;

/**
 *
 * @author Shiwani Gupta
 */
public class MappedSuperclassSpecAccessor extends MappedSuperclassAccessor {

    private MappedSuperclass mappedSuperclass;

    private MappedSuperclassSpecAccessor(MappedSuperclass mappedSuperclass) {
        this.mappedSuperclass = mappedSuperclass;
    }

    public static MappedSuperclassSpecAccessor getInstance(WorkSpace workSpace, MappedSuperclass mappedSuperclass) {
        MappedSuperclassSpecAccessor accessor = new MappedSuperclassSpecAccessor(mappedSuperclass);
        accessor.setClassName(mappedSuperclass.getClazz());
        accessor.setAccess("VIRTUAL");
        accessor.setAttributes(mappedSuperclass.getAttributes().getAccessor(workSpace, true));
        if (mappedSuperclass.getSuperclass() != null) {
            accessor.setParentClassName(mappedSuperclass.getSuperclass().getClazz());
        }
        return accessor;
    }

    /**
     * @return the entity
     */
    public MappedSuperclass getMappedSuperclass() {
        return mappedSuperclass;
    }

    @Override
    public void process() {
        try {
            super.process();
        } catch (ValidationException ex) {
            DBValidationException exception = new DBValidationException(ex);
            exception.setJavaClass(mappedSuperclass);
            throw exception;
        }
    }

    @Override
    protected void processVirtualClass() {
        try {
            super.processVirtualClass();
        } catch (ValidationException ex) {
            DBValidationException exception = new DBValidationException(ex);
            exception.setJavaClass(mappedSuperclass);
            throw exception;
        }
    }

}
