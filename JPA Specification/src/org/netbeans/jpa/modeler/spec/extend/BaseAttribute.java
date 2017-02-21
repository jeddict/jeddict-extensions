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
package org.netbeans.jpa.modeler.spec.extend;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import static org.netbeans.jcode.core.util.AttributeType.BIGDECIMAL;
import static org.netbeans.jcode.core.util.AttributeType.BIGINTEGER;
import static org.netbeans.jcode.core.util.AttributeType.BOOLEAN;
import static org.netbeans.jcode.core.util.AttributeType.BYTE;
import static org.netbeans.jcode.core.util.AttributeType.BYTE_WRAPPER;
import static org.netbeans.jcode.core.util.AttributeType.CALENDAR;
import static org.netbeans.jcode.core.util.AttributeType.DATE;
import static org.netbeans.jcode.core.util.AttributeType.INT;
import static org.netbeans.jcode.core.util.AttributeType.INT_WRAPPER;
import static org.netbeans.jcode.core.util.AttributeType.LONG;
import static org.netbeans.jcode.core.util.AttributeType.LONG_WRAPPER;
import static org.netbeans.jcode.core.util.AttributeType.SHORT;
import static org.netbeans.jcode.core.util.AttributeType.SHORT_WRAPPER;
import static org.netbeans.jcode.core.util.AttributeType.STRING;
import static org.netbeans.jcode.core.util.AttributeType.isArray;
import org.netbeans.jpa.modeler.spec.jaxb.JaxbVariableType;
import org.netbeans.bean.validation.constraints.AssertFalse;
import org.netbeans.bean.validation.constraints.AssertTrue;
import org.netbeans.bean.validation.constraints.Constraint;
import org.netbeans.bean.validation.constraints.DecimalMax;
import org.netbeans.bean.validation.constraints.DecimalMin;
import org.netbeans.bean.validation.constraints.Digits;
import org.netbeans.bean.validation.constraints.Future;
import org.netbeans.bean.validation.constraints.Max;
import org.netbeans.bean.validation.constraints.Min;
import org.netbeans.bean.validation.constraints.Past;
import org.netbeans.bean.validation.constraints.Pattern;
import org.netbeans.bean.validation.constraints.Size;

/**
 *
 * @author Gaurav Gupta
 */
public abstract class BaseAttribute extends Attribute {

    /**
     * @return the attributeType
     */
    public abstract String getAttributeType();

    @Override
    public List<JaxbVariableType> getJaxbVariableList() {
        List<JaxbVariableType> jaxbVariableTypeList = new ArrayList<>();
        jaxbVariableTypeList.add(JaxbVariableType.XML_ATTRIBUTE);
        jaxbVariableTypeList.add(JaxbVariableType.XML_ELEMENT);
        jaxbVariableTypeList.add(JaxbVariableType.XML_VALUE);
        jaxbVariableTypeList.add(JaxbVariableType.XML_TRANSIENT);
        return jaxbVariableTypeList;
    }
    
    @Override
    public String getDataTypeLabel() {
        return getAttributeType();
    }

    @Override
    public Set<Class<? extends Constraint>> getConstraintsClass() {
        Set<Class<? extends Constraint>> classes = super.getConstraintsClass();
        String attribute = getAttributeType();
        if (StringUtils.isNotBlank(attribute)) {
            switch (attribute) {
                case BOOLEAN:
                    classes.add(AssertTrue.class);
                    classes.add(AssertFalse.class);
                    break;
                case STRING:
                    classes.add(Size.class);//array, collection, map pending
                    classes.add(Pattern.class);
                    
                    classes.add(DecimalMin.class);
                    classes.add(DecimalMax.class);
                    classes.add(Digits.class);
                    break;
                case CALENDAR:
                case DATE:
                    classes.add(Past.class);
                    classes.add(Future.class);
                    break;
                case BIGDECIMAL:
                case BIGINTEGER:
                case BYTE:
                case SHORT:
                case INT:
                case LONG:
                case BYTE_WRAPPER:
                case SHORT_WRAPPER:
                case INT_WRAPPER:
                case LONG_WRAPPER:
                    classes.add(Min.class);
                    classes.add(Max.class);
                    classes.add(DecimalMin.class);
                    classes.add(DecimalMax.class);
                    classes.add(Digits.class);
                    break;
                    default:
                        if(isArray(attribute)){
                            classes.add(Size.class);
                        }
                        
            }
        }
        return classes;
    }

    public boolean isTextAttributeType() {
        return isTextAttributeType(getAttributeType());
    }
    public boolean isPrecisionAttributeType() {
        return isPrecisionAttributeType(getAttributeType());
    }
    public boolean isScaleAttributeType() {
        return isScaleAttributeType(getAttributeType());
    }
    

}
