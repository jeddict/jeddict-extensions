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

import java.util.Arrays;
import java.util.List;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.netbeans.jcode.core.util.StringHelper.firstUpper;
import static org.netbeans.jcode.core.util.StringHelper.snakeCase;
import static org.netbeans.jcode.core.util.StringHelper.startCase;
import org.netbeans.jcode.entity.info.EntityClassInfo;

public class Field {

    public String fieldName;
    public String fieldType;
    public String fieldTypeBlobContent;//any , image, text
    public boolean fieldIsEnum;
    public String fieldNameCapitalized;
    public String fieldNameUnderscored;
    public String fieldNameHumanized;
    public String fieldInJavaBeanMethod;
    public boolean fieldValidate;
    private String fieldValues;
  
    
    public Field(EntityClassInfo.FieldInfo fieldInfo) {
        this.fieldName = fieldInfo.getName();
        this.fieldIsEnum= fieldInfo.isEnumerated();
//        this.fieldTypeBlobContent= fieldInfo.isLob();
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
    
    public void setFieldType(String fieldType){
        setFieldType(fieldType, "sql");
    }

    /**
     * @param fieldType the fieldType to set
     */
    public void setFieldType(String fieldType, String databaseType) {
        if ("DateTime".equals(fieldType) || "Date".equals(fieldType)) {
            fieldType = "ZonedDateTime";
        }
//        final String newDataType = fieldType;
//        boolean nonEnumType = Arrays.asList("String", "Integer", "Long", "Float", "Double", "BigDecimal", "LocalDate", "ZonedDateTime", "Boolean", "byte[]", "ByteBuffer")
//                .stream().filter(datatype -> datatype.equals(newDataType)).findAny().isPresent();
//
//        if (("sql".equals(databaseType) || "mongodb".equals(databaseType)) && !nonEnumType) {
//            fieldIsEnum = true;
//        } else {
//            fieldIsEnum = false;
//        }
      
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
        if (fieldValidateRules != null  && fieldValidateRules.size() >= 1) {
                    fieldValidate = true;
                } else {
                    fieldValidate = false;
                }
    }

    /**
     * @return the fieldValues
     */
    public String getFieldValues() {
        if(fieldValues==null){
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
