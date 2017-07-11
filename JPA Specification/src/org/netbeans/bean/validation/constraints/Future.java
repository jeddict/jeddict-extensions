/**
 * Copyright [2014] Gaurav Gupta
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
package org.netbeans.bean.validation.constraints;

import javax.lang.model.element.AnnotationMirror;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import org.netbeans.jpa.source.JavaSourceParserUtil;

/**
 *
 * @author Gaurav Gupta
 */
@XmlRootElement(name = "fu")
public class Future extends Constraint {

//    @XmlAttribute(name = "op")
//    private Boolean orPresent;

    public Future() {
    }

//    @Override
//    public void load(AnnotationMirror annotationMirror) {
//        super.load(annotationMirror);
//        this.orPresent = (Boolean) JavaSourceParserUtil.findAnnotationValue(annotationMirror, "orPresent");
//    }
    
    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    protected void clearConstraint(){
//        orPresent = false;
    }
//
//    /**
//     * @return the orPresent
//     */
//    public Boolean getOrPresent() {
//        return orPresent;
//    }
//
//    /**
//     * @param orPresent the orPresent to set
//     */
//    public void setOrPresent(Boolean orPresent) {
//        this.orPresent = orPresent;
//    }
}
