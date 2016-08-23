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
import static org.netbeans.jcode.core.util.StringHelper.camelCase;
import static org.netbeans.jcode.core.util.StringHelper.firstLower;
import static org.netbeans.jcode.core.util.StringHelper.firstUpper;
import static org.netbeans.jcode.core.util.StringHelper.kebabCase;
import static org.netbeans.jcode.core.util.StringHelper.startCase;
import static org.netbeans.jcode.generator.internal.util.Util.pluralize;
import static org.netbeans.jcode.ng.main.domain.NGRelationship.MANY_TO_MANY;
import static org.netbeans.jcode.ng.main.domain.NGRelationship.MANY_TO_ONE;
import static org.netbeans.jcode.ng.main.domain.NGRelationship.ONE_TO_MANY;
import static org.netbeans.jcode.ng.main.domain.NGRelationship.ONE_TO_ONE;

/**
 *
 * @author jGauravGupta
 */
public class NGEntity {

    public String name;//BankAccount
    public String entityNameCapitalized;
    public String entityClass;
    public String entityClassHumanized;
    public String entityClassPlural;
    public String entityClassPluralHumanized;
    public String entityInstance;
    public String entityInstancePlural;
    public String entityApiUrl;
    public String entityFolderName;
    public String entityFileName;
    public String entityPluralFileName;
    public String entityServiceFileName;
    public String entityAngularJSName;
    public String entityStateName;
    public String entityUrl;
    public String entityTranslationKey;
    public String entityTranslationKeyMenu;

    public boolean fieldsContainZonedDateTime;
    public boolean fieldsContainLocalDate;
    public boolean fieldsContainBigDecimal;
    public boolean fieldsContainBlob;
    public boolean validation;
    public boolean fieldsContainOwnerManyToMany;
    public boolean fieldsContainNoOwnerOneToOne;
    public boolean fieldsContainOwnerOneToOne;
    public boolean fieldsContainOneToMany;
    public boolean fieldsContainManyToOne;
    public List<String> differentTypes = new ArrayList<>();//Arrays.asList("BankAccount", "User", "Operation")
    public final List<Field> fields = new ArrayList<>();
    public final List<NGRelationship> relationships = new ArrayList<>();

    public NGEntity(String name, String entityAngularJSSuffix) {
        String entityNameSpinalCased = kebabCase(firstLower(name));
        String entityNamePluralizedAndSpinalCased = kebabCase(firstLower(pluralize(name)));

        this.name = name;
        this.entityNameCapitalized = firstUpper(name);
        this.entityClass = this.entityNameCapitalized;
        this.entityClassHumanized = startCase(this.entityNameCapitalized);
        this.entityClassPlural = pluralize(this.entityClass);
        this.entityClassPluralHumanized = startCase(this.entityClassPlural);
        this.entityInstance = firstLower(name);
        this.entityInstancePlural = pluralize(this.entityInstance);
        this.entityApiUrl = entityNameSpinalCased; 
        this.entityFolderName = entityNameSpinalCased;
        this.entityFileName = entityNameSpinalCased + entityAngularJSSuffix;
        this.entityPluralFileName = entityNamePluralizedAndSpinalCased + entityAngularJSSuffix;
        this.entityServiceFileName = entityNameSpinalCased;
        this.entityAngularJSName = this.entityClass + firstUpper(camelCase(entityAngularJSSuffix));
        this.entityStateName = entityNameSpinalCased + entityAngularJSSuffix;
        this.entityUrl = entityNameSpinalCased + entityAngularJSSuffix;
        this.entityTranslationKey = this.entityInstance;
        this.entityTranslationKeyMenu = camelCase(this.entityStateName);

        this.fieldsContainZonedDateTime = false;
        this.fieldsContainLocalDate = false;
        this.fieldsContainBigDecimal = false;
        this.fieldsContainBlob = false;
        this.validation = false;
        this.fieldsContainOwnerManyToMany = false;
        this.fieldsContainNoOwnerOneToOne = false;
        this.fieldsContainOwnerOneToOne = false;
        this.fieldsContainOneToMany = false;
        this.fieldsContainManyToOne = false;
        this.differentTypes.add(this.entityClass);

    }

    public void addRelationship(NGRelationship relationship) {
        getRelationships().add(relationship);
        // Load in-memory data for root
        if (MANY_TO_MANY.equals(relationship.relationshipType) && relationship.ownerSide) {
            setFieldsContainOwnerManyToMany(true);
        } else if (ONE_TO_ONE.equals(relationship.relationshipType) && !relationship.ownerSide) {
            setFieldsContainNoOwnerOneToOne(true);
        } else if (ONE_TO_ONE.equals(relationship.relationshipType) && relationship.ownerSide) {
            setFieldsContainOwnerOneToOne(true);
        } else if (ONE_TO_MANY.equals(relationship.relationshipType)) {
            setFieldsContainOneToMany(true);
        } else if (MANY_TO_ONE.equals(relationship.relationshipType)) {
            setFieldsContainManyToOne(true);
        }

        if (relationship.getRelationshipValidateRules() != null && relationship.getRelationshipValidateRules().contains("required")) {
            relationship.relationshipValidate = true;
            relationship.relationshipRequired = true;
            setValidation(true);
        }

        String entityType = relationship.getOtherEntityNameCapitalized();
        if (!differentTypes.contains(entityType)) {
            getDifferentTypes().add(entityType);
        }
    }

    public void removeRelationship(NGRelationship relationship) {
        getRelationships().remove(relationship);
    }

    public void addField(Field field) {
        getFields().add(field);
        if (null != field.fieldType) {
            switch (field.fieldType) {
                case "ZonedDateTime":
                    setFieldsContainZonedDateTime(true);
                    break;
                case "LocalDate":
                    setFieldsContainLocalDate(true);
                    break;
                case "BigDecimal":
                    setFieldsContainBigDecimal(true);
                    break;
                case "byte[]":
                case "ByteBuffer":
                    setFieldsContainBlob(true);
                    break;
                default:
                    break;
            }
        }
    }

    public void removeField(Field field) {
        getFields().remove(field);
    }

    /**
     * @return the fields
     */
    public List<Field> getFields() {
        return fields;
    }

    /**
     * @return the relationships
     */
    public List<NGRelationship> getRelationships() {
        return relationships;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the entityNameCapitalized
     */
    public String getEntityNameCapitalized() {
        return entityNameCapitalized;
    }

    /**
     * @param entityNameCapitalized the entityNameCapitalized to set
     */
    public void setEntityNameCapitalized(String entityNameCapitalized) {
        this.entityNameCapitalized = entityNameCapitalized;
    }

    /**
     * @return the entityClass
     */
    public String getEntityClass() {
        return entityClass;
    }

    /**
     * @param entityClass the entityClass to set
     */
    public void setEntityClass(String entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * @return the entityClassHumanized
     */
    public String getEntityClassHumanized() {
        return entityClassHumanized;
    }

    /**
     * @param entityClassHumanized the entityClassHumanized to set
     */
    public void setEntityClassHumanized(String entityClassHumanized) {
        this.entityClassHumanized = entityClassHumanized;
    }

    /**
     * @return the entityClassPlural
     */
    public String getEntityClassPlural() {
        return entityClassPlural;
    }

    /**
     * @param entityClassPlural the entityClassPlural to set
     */
    public void setEntityClassPlural(String entityClassPlural) {
        this.entityClassPlural = entityClassPlural;
    }

    /**
     * @return the entityClassPluralHumanized
     */
    public String getEntityClassPluralHumanized() {
        return entityClassPluralHumanized;
    }

    /**
     * @param entityClassPluralHumanized the entityClassPluralHumanized to set
     */
    public void setEntityClassPluralHumanized(String entityClassPluralHumanized) {
        this.entityClassPluralHumanized = entityClassPluralHumanized;
    }

    /**
     * @return the entityInstance
     */
    public String getEntityInstance() {
        return entityInstance;
    }

    /**
     * @param entityInstance the entityInstance to set
     */
    public void setEntityInstance(String entityInstance) {
        this.entityInstance = entityInstance;
    }

    /**
     * @return the entityInstancePlural
     */
    public String getEntityInstancePlural() {
        return entityInstancePlural;
    }

    /**
     * @param entityInstancePlural the entityInstancePlural to set
     */
    public void setEntityInstancePlural(String entityInstancePlural) {
        this.entityInstancePlural = entityInstancePlural;
    }

    /**
     * @return the entityApiUrl
     */
    public String getEntityApiUrl() {
        return entityApiUrl;
    }

    /**
     * @param entityApiUrl the entityApiUrl to set
     */
    public void setEntityApiUrl(String entityApiUrl) {
        this.entityApiUrl = entityApiUrl;
    }

    /**
     * @return the entityFolderName
     */
    public String getEntityFolderName() {
        return entityFolderName;
    }

    /**
     * @param entityFolderName the entityFolderName to set
     */
    public void setEntityFolderName(String entityFolderName) {
        this.entityFolderName = entityFolderName;
    }

    /**
     * @return the entityFileName
     */
    public String getEntityFileName() {
        return entityFileName;
    }

    /**
     * @param entityFileName the entityFileName to set
     */
    public void setEntityFileName(String entityFileName) {
        this.entityFileName = entityFileName;
    }

    /**
     * @return the entityPluralFileName
     */
    public String getEntityPluralFileName() {
        return entityPluralFileName;
    }

    /**
     * @param entityPluralFileName the entityPluralFileName to set
     */
    public void setEntityPluralFileName(String entityPluralFileName) {
        this.entityPluralFileName = entityPluralFileName;
    }

    /**
     * @return the entityServiceFileName
     */
    public String getEntityServiceFileName() {
        return entityServiceFileName;
    }

    /**
     * @param entityServiceFileName the entityServiceFileName to set
     */
    public void setEntityServiceFileName(String entityServiceFileName) {
        this.entityServiceFileName = entityServiceFileName;
    }

    /**
     * @return the entityAngularJSName
     */
    public String getEntityAngularJSName() {
        return entityAngularJSName;
    }

    /**
     * @param entityAngularJSName the entityAngularJSName to set
     */
    public void setEntityAngularJSName(String entityAngularJSName) {
        this.entityAngularJSName = entityAngularJSName;
    }

    /**
     * @return the entityStateName
     */
    public String getEntityStateName() {
        return entityStateName;
    }

    /**
     * @param entityStateName the entityStateName to set
     */
    public void setEntityStateName(String entityStateName) {
        this.entityStateName = entityStateName;
    }

    /**
     * @return the entityUrl
     */
    public String getEntityUrl() {
        return entityUrl;
    }

    /**
     * @param entityUrl the entityUrl to set
     */
    public void setEntityUrl(String entityUrl) {
        this.entityUrl = entityUrl;
    }

    /**
     * @return the entityTranslationKey
     */
    public String getEntityTranslationKey() {
        return entityTranslationKey;
    }

    /**
     * @param entityTranslationKey the entityTranslationKey to set
     */
    public void setEntityTranslationKey(String entityTranslationKey) {
        this.entityTranslationKey = entityTranslationKey;
    }

    /**
     * @return the entityTranslationKeyMenu
     */
    public String getEntityTranslationKeyMenu() {
        return entityTranslationKeyMenu;
    }

    /**
     * @param entityTranslationKeyMenu the entityTranslationKeyMenu to set
     */
    public void setEntityTranslationKeyMenu(String entityTranslationKeyMenu) {
        this.entityTranslationKeyMenu = entityTranslationKeyMenu;
    }

    /**
     * @return the fieldsContainZonedDateTime
     */
    public boolean isFieldsContainZonedDateTime() {
        return fieldsContainZonedDateTime;
    }

    /**
     * @param fieldsContainZonedDateTime the fieldsContainZonedDateTime to set
     */
    public void setFieldsContainZonedDateTime(boolean fieldsContainZonedDateTime) {
        this.fieldsContainZonedDateTime = fieldsContainZonedDateTime;
    }

    /**
     * @return the fieldsContainLocalDate
     */
    public boolean isFieldsContainLocalDate() {
        return fieldsContainLocalDate;
    }

    /**
     * @param fieldsContainLocalDate the fieldsContainLocalDate to set
     */
    public void setFieldsContainLocalDate(boolean fieldsContainLocalDate) {
        this.fieldsContainLocalDate = fieldsContainLocalDate;
    }

    /**
     * @return the fieldsContainBigDecimal
     */
    public boolean isFieldsContainBigDecimal() {
        return fieldsContainBigDecimal;
    }

    /**
     * @param fieldsContainBigDecimal the fieldsContainBigDecimal to set
     */
    public void setFieldsContainBigDecimal(boolean fieldsContainBigDecimal) {
        this.fieldsContainBigDecimal = fieldsContainBigDecimal;
    }

    /**
     * @return the fieldsContainBlob
     */
    public boolean isFieldsContainBlob() {
        return fieldsContainBlob;
    }

    /**
     * @param fieldsContainBlob the fieldsContainBlob to set
     */
    public void setFieldsContainBlob(boolean fieldsContainBlob) {
        this.fieldsContainBlob = fieldsContainBlob;
    }

    /**
     * @return the validation
     */
    public boolean isValidation() {
        return validation;
    }

    /**
     * @param validation the validation to set
     */
    public void setValidation(boolean validation) {
        this.validation = validation;
    }

    /**
     * @return the fieldsContainOwnerManyToMany
     */
    public boolean isFieldsContainOwnerManyToMany() {
        return fieldsContainOwnerManyToMany;
    }

    /**
     * @param fieldsContainOwnerManyToMany the fieldsContainOwnerManyToMany to
     * set
     */
    public void setFieldsContainOwnerManyToMany(boolean fieldsContainOwnerManyToMany) {
        this.fieldsContainOwnerManyToMany = fieldsContainOwnerManyToMany;
    }

    /**
     * @return the fieldsContainNoOwnerOneToOne
     */
    public boolean isFieldsContainNoOwnerOneToOne() {
        return fieldsContainNoOwnerOneToOne;
    }

    /**
     * @param fieldsContainNoOwnerOneToOne the fieldsContainNoOwnerOneToOne to
     * set
     */
    public void setFieldsContainNoOwnerOneToOne(boolean fieldsContainNoOwnerOneToOne) {
        this.fieldsContainNoOwnerOneToOne = fieldsContainNoOwnerOneToOne;
    }

    /**
     * @return the fieldsContainOwnerOneToOne
     */
    public boolean isFieldsContainOwnerOneToOne() {
        return fieldsContainOwnerOneToOne;
    }

    /**
     * @param fieldsContainOwnerOneToOne the fieldsContainOwnerOneToOne to set
     */
    public void setFieldsContainOwnerOneToOne(boolean fieldsContainOwnerOneToOne) {
        this.fieldsContainOwnerOneToOne = fieldsContainOwnerOneToOne;
    }

    /**
     * @return the fieldsContainOneToMany
     */
    public boolean isFieldsContainOneToMany() {
        return fieldsContainOneToMany;
    }

    /**
     * @param fieldsContainOneToMany the fieldsContainOneToMany to set
     */
    public void setFieldsContainOneToMany(boolean fieldsContainOneToMany) {
        this.fieldsContainOneToMany = fieldsContainOneToMany;
    }

    /**
     * @return the fieldsContainManyToOne
     */
    public boolean isFieldsContainManyToOne() {
        return fieldsContainManyToOne;
    }

    /**
     * @param fieldsContainManyToOne the fieldsContainManyToOne to set
     */
    public void setFieldsContainManyToOne(boolean fieldsContainManyToOne) {
        this.fieldsContainManyToOne = fieldsContainManyToOne;
    }

    /**
     * @return the differentTypes
     */
    public List<String> getDifferentTypes() {
        return differentTypes;
    }

    /**
     * @param differentTypes the differentTypes to set
     */
    public void setDifferentTypes(List<String> differentTypes) {
        this.differentTypes = differentTypes;
    }

}
