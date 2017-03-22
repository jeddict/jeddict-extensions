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

public interface NGRelationship {
        
    public static final String ONE_TO_ONE = "one-to-one";
    public static final String ONE_TO_MANY = "one-to-many";
    public static final String MANY_TO_ONE = "many-to-one";
    public static final String MANY_TO_MANY = "many-to-many";
    
   
    /**
     * @return the name
     */
    String getName();

    /**
     * @return the otherEntityField
     */
    String getOtherEntityField();

    /**
     * @return the otherEntityFieldCapitalized
     */
    String getOtherEntityFieldCapitalized();

    /**
     * @return the otherEntityName
     */
    String getOtherEntityName();

    /**
     * @return the otherEntityNameCapitalized
     */
    String getOtherEntityNameCapitalized();

    /**
     * @return the otherEntityNameCapitalizedPlural
     */
    String getOtherEntityNameCapitalizedPlural();

    /**
     * @return the otherEntityNamePlural
     */
    String getOtherEntityNamePlural();

    /**
     * @return the otherEntityRelationshipName
     */
    String getOtherEntityRelationshipName();

    /**
     * @return the otherEntityRelationshipNamePlural
     */
    String getOtherEntityRelationshipNamePlural();

    /**
     * @return the otherEntityStateName
     */
    String getOtherEntityStateName();

    /**
     * @return the relationshipFieldName
     */
    String getRelationshipFieldName();

    /**
     * @return the relationshipFieldNamePlural
     */
    String getRelationshipFieldNamePlural();

    /**
     * @return the relationshipName
     */
    String getRelationshipName();

    /**
     * @return the relationshipNameCapitalized
     */
    String getRelationshipNameCapitalized();

    /**
     * @return the relationshipNameCapitalizedPlural
     */
    String getRelationshipNameCapitalizedPlural();

    /**
     * @return the relationshipNameHumanized
     */
    String getRelationshipNameHumanized();

    /**
     * @return the relationshipNamePlural
     */
    String getRelationshipNamePlural();

    /**
     * @return the relationshipType
     */
    String getRelationshipType();

    /**
     * @return the relationshipValidateRules
     */
    List<String> getRelationshipValidateRules();

    /**
     * @return the ownerSide
     */
    boolean isOwnerSide();

    /**
     * @return the relationshipRequired
     */
    boolean isRelationshipRequired();

    /**
     * @return the relationshipValidate
     */
    boolean isRelationshipValidate();

    /**
     * @param name the name to set
     */
    void setName(String name);

    /**
     * @param otherEntityField the otherEntityField to set
     */
    void setOtherEntityField(String otherEntityField);

    /**
     * @param otherEntityRelationshipName the otherEntityRelationshipName to set
     */
    void setOtherEntityRelationshipName(String otherEntityRelationshipName);

    /**
     * @param ownerSide the ownerSide to set
     */
    void setOwnerSide(boolean ownerSide);

    /**
     * @param relationshipName the relationshipName to set
     */
    void setRelationshipName(String relationshipName);

    /**
     * @param relationshipRequired the relationshipRequired to set
     */
    void setRelationshipRequired(boolean relationshipRequired);

    /**
     * @param relationshipType the relationshipType to set
     */
    void setRelationshipType(String relationshipType);

    /**
     * @param relationshipValidate the relationshipValidate to set
     */
    void setRelationshipValidate(boolean relationshipValidate);

    /**
     * @param relationshipValidateRules the relationshipValidateRules to set
     */
    void setRelationshipValidateRules(List<String> relationshipValidateRules);
    
}
