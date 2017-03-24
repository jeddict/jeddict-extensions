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
import java.util.Collections;
import java.util.LinkedHashSet;
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
import static org.netbeans.jcode.core.util.AttributeType.HIJRAH_DATE;
import static org.netbeans.jcode.core.util.AttributeType.INSTANT;
import static org.netbeans.jcode.core.util.AttributeType.JAPANESE_DATE;
import static org.netbeans.jcode.core.util.AttributeType.LOCAL_DATE;
import static org.netbeans.jcode.core.util.AttributeType.LOCAL_DATE_TIME;
import static org.netbeans.jcode.core.util.AttributeType.LOCAL_TIME;
import static org.netbeans.jcode.core.util.AttributeType.MINGUO_DATE;
import static org.netbeans.jcode.core.util.AttributeType.MONTH_DAY;
import static org.netbeans.jcode.core.util.AttributeType.OFFSET_DATE_TIME;
import static org.netbeans.jcode.core.util.AttributeType.OFFSET_TIME;
import static org.netbeans.jcode.core.util.AttributeType.THAI_BUDDHIST_DATE;
import static org.netbeans.jcode.core.util.AttributeType.YEAR;
import static org.netbeans.jcode.core.util.AttributeType.YEAR_MONTH;
import static org.netbeans.jcode.core.util.AttributeType.ZONED_DATE_TIME;

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
    public Set<Class<? extends Constraint>> getAttributeConstraintsClass() {
//        if(isOptionalReturnType()){
//            return Collections.EMPTY_SET;
//        }
        return getConstraintsClass(getAttributeType());
    }
    
//     @Override
//    public Set<Class<? extends Constraint>> getValueConstraintsClass() {
//        if(!isOptionalReturnType()){
//            return Collections.EMPTY_SET;
//        }
//        return getConstraintsClass(getAttributeType());
//    }
  
    

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
