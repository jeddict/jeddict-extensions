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
import org.apache.commons.lang.StringUtils;
import org.netbeans.jpa.source.JavaSourceParserUtil;

/**
 *
 * @author Gaurav Gupta
 */
@XmlRootElement(name="dmi")
public class DecimalMin extends Constraint {

    @XmlAttribute(name = "v")
    private String value;
    
    @XmlAttribute(name = "i")
    private boolean inclusive = true;

    @Override
    public void load(AnnotationMirror annotationMirror) {
        super.load(annotationMirror);
        this.value = JavaSourceParserUtil.findAnnotationValueAsString(annotationMirror, "value");
        this.inclusive = (Boolean)JavaSourceParserUtil.findAnnotationValue(annotationMirror, "inclusive");
    }

    @Override
    public boolean isEmpty() {
        return StringUtils.isBlank(value) && !inclusive;
    }
    
    @Override
    protected void clearConstraint(){
        value = null;
        inclusive = true;
    }

    /**
     * @return the inclusive
     */
    public boolean isInclusive() {
        return inclusive;
    }

    /**
     * @param inclusive the inclusive to set
     */
    public void setInclusive(boolean inclusive) {
        this.inclusive = inclusive;
    }
    
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
