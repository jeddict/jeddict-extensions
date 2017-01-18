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
package org.netbeans.jcode.ng.main.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.netbeans.jcode.core.util.StringHelper.firstUpper;
import static org.netbeans.jcode.core.util.StringHelper.snakeCase;
import static org.netbeans.jcode.core.util.StringHelper.startCase;
import org.netbeans.jpa.modeler.spec.extend.BaseAttribute;
import org.netbeans.jpa.modeler.spec.validation.constraints.Constraint;
import org.netbeans.jpa.modeler.spec.validation.constraints.Max;
import org.netbeans.jpa.modeler.spec.validation.constraints.Min;
import org.netbeans.jpa.modeler.spec.validation.constraints.NotNull;
import org.netbeans.jpa.modeler.spec.validation.constraints.Pattern;
import org.netbeans.jpa.modeler.spec.validation.constraints.Size;

public class NGField {

    public String fieldName;
    public String fieldType;
    public String fieldTypeBlobContent;//any , image, text
    public boolean fieldIsEnum;
    public String fieldNameCapitalized;
    public String fieldNameUnderscored;
    public String fieldNameHumanized;
    public String fieldInJavaBeanMethod;
    public String fieldValues;

    public List<String> fieldValidateRules;
    public boolean fieldValidate;
    public String fieldValidateRulesMax;
    public String fieldValidateRulesMin;
    public String fieldValidateRulesMaxlength;
    public String fieldValidateRulesMinlength;
    public String fieldValidateRulesMaxbytes;
    public String fieldValidateRulesMinbytes;
    public String fieldValidateRulesPattern;


    public NGField(String fieldName) {
        this.fieldName = fieldName;
    }

    public NGField(BaseAttribute attribute) {
        this.fieldName = attribute.getName();
        loadValidation(attribute.getConstraintsMap());
    }

    //['required', 'max', 'min', 'maxlength', 'minlength', 'maxbytes', 'minbytes', 'pattern'];
    private void loadValidation(Map<String, Constraint> constraints) {
        List<String> validationRules = new ArrayList<>();
        
        NotNull notNull = (NotNull) constraints.get(NotNull.class.getSimpleName());
        if (notNull != null && notNull.getSelected()) {
            validationRules.add("required");
        }
        
        Pattern pattern = (Pattern) constraints.get(Pattern.class.getSimpleName());
        if (pattern != null && pattern.getSelected()) {
            validationRules.add("pattern");
            fieldValidateRulesMin = pattern.getRegexp();
        }
        
        Min min = (Min) constraints.get(Min.class.getSimpleName());
        if (min != null && min.getSelected()) {
            validationRules.add("min");
            fieldValidateRulesMin = String.valueOf(min.getValue());
        }
        
        Max max = (Max) constraints.get(Max.class.getSimpleName());
        if (max != null && max.getSelected()) {
            validationRules.add("max");
            fieldValidateRulesMax = String.valueOf(max.getValue());
        }
        
        if (constraints.get(Size.class.getSimpleName()) != null) {
            Size size = (Size) constraints.get(Size.class.getSimpleName());
            if (size.getMax() != null) {
                fieldValidateRulesMaxlength = String.valueOf(size.getMax());
                validationRules.add("max");
            }
            if (size.getMin() != null) {
                fieldValidateRulesMinlength = String.valueOf(size.getMin());
                validationRules.add("min");
            }
        }
        setFieldValidate(validationRules);

    }

    /**
     * @return the fieldName
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * @param fieldName the fieldName to set
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * @return the fieldType
     */
    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        setFieldType(fieldType, "sql");
    }

    /**
     * @param fieldType the fieldType to set
     */
    public void setFieldType(String fieldType, String databaseType) {
        if ("DateTime".equals(fieldType)) {
            fieldType = "ZonedDateTime";
        }
        if ("Date".equals(fieldType)) {
            fieldType = "ZonedDateTime";
        }
        this.fieldType = fieldType;
    }

    /**
     * @return the fieldTypeBlobContent
     */
    public String getFieldTypeBlobContent() {
        return fieldTypeBlobContent;
    }

    /**
     * @param fieldTypeBlobContent the fieldTypeBlobContent to set
     */
    public void setFieldTypeBlobContent(String fieldTypeBlobContent) {
        this.fieldTypeBlobContent = fieldTypeBlobContent;
    }

    /**
     * @return the fieldNameHumanized
     */
    public String getFieldNameHumanized() {
        if (fieldNameHumanized == null) {
            fieldNameHumanized = startCase(fieldName);
        }
        return fieldNameHumanized;
    }

    /**
     * @param fieldNameHumanized the fieldNameHumanized to set
     */
    public void setFieldNameHumanized(String fieldNameHumanized) {
        this.fieldNameHumanized = fieldNameHumanized;
    }

    /**
     * @return the fieldIsEnum
     */
    public boolean getFieldIsEnum() {
        return isFieldIsEnum();
    }

    /**
     * @param fieldIsEnum the fieldIsEnum to set
     */
    public void setFieldIsEnum(boolean fieldIsEnum) {
        this.fieldIsEnum = fieldIsEnum;
    }

    /**
     * @return the fieldIsEnum
     */
    public boolean isFieldIsEnum() {
        return fieldIsEnum;
    }

    /**
     * @return the fieldNameCapitalized
     */
    public String getFieldNameCapitalized() {
        if (fieldNameCapitalized == null) {
            fieldNameCapitalized = firstUpper(fieldName);
        }
        return fieldNameCapitalized;
    }

    /**
     * @param fieldNameCapitalized the fieldNameCapitalized to set
     */
    public void setFieldNameCapitalized(String fieldNameCapitalized) {
        this.fieldNameCapitalized = fieldNameCapitalized;
    }

    /**
     * @return the fieldNameUnderscored
     */
    public String getFieldNameUnderscored() {
        if (fieldNameUnderscored == null) {
            fieldNameUnderscored = snakeCase(fieldName);
        }
        return fieldNameUnderscored;
    }

    /**
     * @param fieldNameUnderscored the fieldNameUnderscored to set
     */
    public void setFieldNameUnderscored(String fieldNameUnderscored) {
        this.fieldNameUnderscored = fieldNameUnderscored;
    }

    /**
     * @return the fieldInJavaBeanMethod
     */
    public String getFieldInJavaBeanMethod() {
//        if (fieldInJavaBeanMethod == null) {
//                    if (fieldName.length() > 1) {
//                        Character firstLetter = fieldName.charAt(0);
//                        Character secondLetter = fieldName.charAt(1);
//                        if (firstLetter == firstLetter.toLowerCase() && secondLetter == secondLetter.toUpperCase()) {
//                            field.fieldInJavaBeanMethod = firstLetter.toLowerCase() + field.fieldName.slice(1);
//                        } else {
//                            field.fieldInJavaBeanMethod = _.firstUpper(field.fieldName);
//                        }
//                    } else {
//                        field.fieldInJavaBeanMethod = _.firstUpper(field.fieldName);
//                    }
//                }
        return fieldInJavaBeanMethod;
    }

    /**
     * @param fieldInJavaBeanMethod the fieldInJavaBeanMethod to set
     */
    public void setFieldInJavaBeanMethod(String fieldInJavaBeanMethod) {
        this.fieldInJavaBeanMethod = fieldInJavaBeanMethod;
    }

    /**
     * @return the fieldValidate
     */
    public boolean isFieldValidate() {
        return fieldValidate;
    }

    public void setFieldValidate(List<String> fieldValidateRules) {

        if (fieldValidateRules != null && fieldValidateRules.size() >= 1) {
            fieldValidate = true;
        } else {
            fieldValidate = false;
        }
        this.fieldValidateRules = fieldValidateRules;
    }

    /**
     * @return the fieldValues
     */
    public String getFieldValues() {
        if (fieldValues == null) {
            return EMPTY;
        }
        return fieldValues;
    }

    /**
     * @param fieldValues the fieldValues to set
     */
    public void setFieldValues(String fieldValues) {
        this.fieldValues = fieldValues;
    }

}
