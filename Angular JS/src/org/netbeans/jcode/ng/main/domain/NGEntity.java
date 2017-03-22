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

/**
 *
 * @author jGauravGupta
 */
public interface NGEntity {
    
    void addField(NGField field);

    void addRelationship(NGRelationship relationship);

    /**
     * @return the differentRelationships
     */
    List<NGRelationship> getDifferentRelationships();

    /**
     * @return the differentTypes
     */
    List<String> getDifferentTypes();

    /**
     * @return the entityAngularJSName
     */
    String getEntityAngularJSName();

    /**
     * @return the entityApiUrl
     */
    String getEntityApiUrl();

    /**
     * @return the entityClass
     */
    String getEntityClass();

    /**
     * @return the entityClassHumanized
     */
    String getEntityClassHumanized();

    /**
     * @return the entityClassPlural
     */
    String getEntityClassPlural();

    /**
     * @return the entityClassPluralHumanized
     */
    String getEntityClassPluralHumanized();

    /**
     * @return the entityFileName
     */
    String getEntityFileName();

    /**
     * @return the entityFolderName
     */
    String getEntityFolderName();

    /**
     * @return the entityInstance
     */
    String getEntityInstance();

    /**
     * @return the entityInstancePlural
     */
    String getEntityInstancePlural();

    /**
     * @return the entityNameCapitalized
     */
    String getEntityNameCapitalized();

    /**
     * @return the entityPluralFileName
     */
    String getEntityPluralFileName();

    /**
     * @return the entityServiceFileName
     */
    String getEntityServiceFileName();

    /**
     * @return the entityStateName
     */
    String getEntityStateName();

    /**
     * @return the entityTranslationKey
     */
    String getEntityTranslationKey();

    /**
     * @return the entityTranslationKeyMenu
     */
    String getEntityTranslationKeyMenu();

    /**
     * @return the entityUrl
     */
    String getEntityUrl();

    /**
     * @return the fields
     */
    List<NGField> getFields();

    /**
     * @return the name
     */
    String getName();

    /**
     * @return the pkType
     */
    String getPkType();

    /**
     * @return the relationships
     */
    List<NGRelationship> getRelationships();

    /**
     * @return the fieldsContainBigDecimal
     */
    boolean isFieldsContainBigDecimal();

    /**
     * @return the fieldsContainBlob
     */
    boolean isFieldsContainBlob();

    /**
     * @return the fieldsContainLocalDate
     */
    boolean isFieldsContainLocalDate();

    /**
     * @return the fieldsContainManyToOne
     */
    boolean isFieldsContainManyToOne();

    /**
     * @return the fieldsContainNoOwnerOneToOne
     */
    boolean isFieldsContainNoOwnerOneToOne();

    /**
     * @return the fieldsContainOneToMany
     */
    boolean isFieldsContainOneToMany();

    /**
     * @return the fieldsContainOwnerManyToMany
     */
    boolean isFieldsContainOwnerManyToMany();

    /**
     * @return the fieldsContainOwnerOneToOne
     */
    boolean isFieldsContainOwnerOneToOne();

    /**
     * @return the fieldsContainZonedDateTime
     */
    boolean isFieldsContainZonedDateTime();

    /**
     * @return the validation
     */
    boolean isValidation();

    void removeField(NGField field);

    void removeRelationship(NGRelationship relationship);

    /**
     * @param differentTypes the differentTypes to set
     */
    void setDifferentTypes(List<String> differentTypes);

    /**
     * @param entityAngularJSName the entityAngularJSName to set
     */
    void setEntityAngularJSName(String entityAngularJSName);

    /**
     * @param entityApiUrl the entityApiUrl to set
     */
    void setEntityApiUrl(String entityApiUrl);

    /**
     * @param entityClass the entityClass to set
     */
    void setEntityClass(String entityClass);

    /**
     * @param entityClassHumanized the entityClassHumanized to set
     */
    void setEntityClassHumanized(String entityClassHumanized);

    /**
     * @param entityClassPlural the entityClassPlural to set
     */
    void setEntityClassPlural(String entityClassPlural);

    /**
     * @param entityClassPluralHumanized the entityClassPluralHumanized to set
     */
    void setEntityClassPluralHumanized(String entityClassPluralHumanized);

    /**
     * @param entityFileName the entityFileName to set
     */
    void setEntityFileName(String entityFileName);

    /**
     * @param entityFolderName the entityFolderName to set
     */
    void setEntityFolderName(String entityFolderName);

    /**
     * @param entityInstance the entityInstance to set
     */
    void setEntityInstance(String entityInstance);

    /**
     * @param entityInstancePlural the entityInstancePlural to set
     */
    void setEntityInstancePlural(String entityInstancePlural);

    /**
     * @param entityNameCapitalized the entityNameCapitalized to set
     */
    void setEntityNameCapitalized(String entityNameCapitalized);

    /**
     * @param entityPluralFileName the entityPluralFileName to set
     */
    void setEntityPluralFileName(String entityPluralFileName);

    /**
     * @param entityServiceFileName the entityServiceFileName to set
     */
    void setEntityServiceFileName(String entityServiceFileName);

    /**
     * @param entityStateName the entityStateName to set
     */
    void setEntityStateName(String entityStateName);

    /**
     * @param entityTranslationKey the entityTranslationKey to set
     */
    void setEntityTranslationKey(String entityTranslationKey);

    /**
     * @param entityTranslationKeyMenu the entityTranslationKeyMenu to set
     */
    void setEntityTranslationKeyMenu(String entityTranslationKeyMenu);

    /**
     * @param entityUrl the entityUrl to set
     */
    void setEntityUrl(String entityUrl);

    /**
     * @param fieldsContainBigDecimal the fieldsContainBigDecimal to set
     */
    void setFieldsContainBigDecimal(boolean fieldsContainBigDecimal);

    /**
     * @param fieldsContainBlob the fieldsContainBlob to set
     */
    void setFieldsContainBlob(boolean fieldsContainBlob);

    /**
     * @param fieldsContainLocalDate the fieldsContainLocalDate to set
     */
    void setFieldsContainLocalDate(boolean fieldsContainLocalDate);

    /**
     * @param fieldsContainManyToOne the fieldsContainManyToOne to set
     */
    void setFieldsContainManyToOne(boolean fieldsContainManyToOne);

    /**
     * @param fieldsContainNoOwnerOneToOne the fieldsContainNoOwnerOneToOne to
     * set
     */
    void setFieldsContainNoOwnerOneToOne(boolean fieldsContainNoOwnerOneToOne);

    /**
     * @param fieldsContainOneToMany the fieldsContainOneToMany to set
     */
    void setFieldsContainOneToMany(boolean fieldsContainOneToMany);

    /**
     * @param fieldsContainOwnerManyToMany the fieldsContainOwnerManyToMany to
     * set
     */
    void setFieldsContainOwnerManyToMany(boolean fieldsContainOwnerManyToMany);

    /**
     * @param fieldsContainOwnerOneToOne the fieldsContainOwnerOneToOne to set
     */
    void setFieldsContainOwnerOneToOne(boolean fieldsContainOwnerOneToOne);

    /**
     * @param fieldsContainZonedDateTime the fieldsContainZonedDateTime to set
     */
    void setFieldsContainZonedDateTime(boolean fieldsContainZonedDateTime);

    /**
     * @param name the name to set
     */
    void setName(String name);

    /**
     * @param pkType the pkType to set
     */
    void setPkType(String pkType);

    /**
     * @param validation the validation to set
     */
    void setValidation(boolean validation);
    
}