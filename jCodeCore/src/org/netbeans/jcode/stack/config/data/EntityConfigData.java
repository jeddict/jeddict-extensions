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

import org.openide.filesystems.FileObject;

/**
 *
 * @author jGauravGupta
 */
public class EntityConfigData {
    
    private FileObject entityFile;
    private String labelAttribute;

    public EntityConfigData(FileObject entityFile) {
        this.entityFile = entityFile;
    }
    
    

    /**
     * @return the entityFile
     */
    public FileObject getEntityFile() {
        return entityFile;
    }

    /**
     * @param entityFile the entityFile to set
     */
    public void setEntityFile(FileObject entityFile) {
        this.entityFile = entityFile;
    }

    /**
     * @return the labelAttribute
     */
    public String getLabelAttribute() {
        return labelAttribute;
    }

    /**
     * @param labelAttribute the labelAttribute to set
     */
    public void setLabelAttribute(String labelAttribute) {
        this.labelAttribute = labelAttribute;
    }

}
