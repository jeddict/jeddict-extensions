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

import java.util.List;

public interface NGField {
    
    /**
     * @return the fieldInJavaBeanMethod
     */
    String getFieldInJavaBeanMethod();

    /**
     * @return the fieldIsEnum
     */
    boolean getFieldIsEnum();

    /**
     * @return the fieldName
     */
    String getFieldName();

    /**
     * @return the fieldNameCapitalized
     */
    String getFieldNameCapitalized();

    /**
     * @return the fieldNameHumanized
     */
    String getFieldNameHumanized();

    /**
     * @return the fieldNameUnderscored
     */
    String getFieldNameUnderscored();

    /**
     * @return the fieldType
     */
    String getFieldType();

    /**
     * @return the fieldTypeBlobContent
     */
    String getFieldTypeBlobContent();

    /**
     * @return the fieldValues
     */
    String getFieldValues();

    /**
     * @return the fieldIsEnum
     */
    boolean isFieldIsEnum();

    /**
     * @return the fieldValidate
     */
    boolean isFieldValidate();

    /**
     * @param fieldInJavaBeanMethod the fieldInJavaBeanMethod to set
     */
    void setFieldInJavaBeanMethod(String fieldInJavaBeanMethod);

    /**
     * @param fieldIsEnum the fieldIsEnum to set
     */
    void setFieldIsEnum(boolean fieldIsEnum);

    /**
     * @param fieldName the fieldName to set
     */
    void setFieldName(String fieldName);

    /**
     * @param fieldNameCapitalized the fieldNameCapitalized to set
     */
    void setFieldNameCapitalized(String fieldNameCapitalized);

    /**
     * @param fieldNameHumanized the fieldNameHumanized to set
     */
    void setFieldNameHumanized(String fieldNameHumanized);

    /**
     * @param fieldNameUnderscored the fieldNameUnderscored to set
     */
    void setFieldNameUnderscored(String fieldNameUnderscored);

    void setFieldType(String fieldType);

    /**
     * @param fieldType the fieldType to set
     */
    void setFieldType(String fieldType, String databaseType);

    /**
     * @param fieldTypeBlobContent the fieldTypeBlobContent to set
     */
    void setFieldTypeBlobContent(String fieldTypeBlobContent);

    void setFieldValidate(List<String> fieldValidateRules);

    /**
     * @param fieldValues the fieldValues to set
     */
    void setFieldValues(String fieldValues);
    
}