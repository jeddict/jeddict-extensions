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
package org.netbeans.jcode.angular2.domain;

import org.netbeans.jcode.ng.main.domain.*;
import java.util.ArrayList;
import java.util.List;
import static org.netbeans.jcode.core.util.StringHelper.camelCase;
import static org.netbeans.jcode.core.util.StringHelper.firstLower;
import static org.netbeans.jcode.core.util.StringHelper.firstUpper;
import static org.netbeans.jcode.core.util.StringHelper.kebabCase;
import static org.netbeans.jcode.core.util.StringHelper.pluralize;
import static org.netbeans.jcode.core.util.StringHelper.startCase;
import static org.netbeans.jcode.ng.main.domain.NGRelationship.MANY_TO_MANY;
import static org.netbeans.jcode.ng.main.domain.NGRelationship.MANY_TO_ONE;
import static org.netbeans.jcode.ng.main.domain.NGRelationship.ONE_TO_MANY;
import static org.netbeans.jcode.ng.main.domain.NGRelationship.ONE_TO_ONE;

/**
 *
 * @author jGauravGupta
 */
public class NG2Entity implements NGEntity{

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
    public String entityAngularName;
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
    public List<String> differentTypes = new ArrayList<>();
    public final List<NGField> fields = new ArrayList<>();
    public final List<NGRelationship> relationships = new ArrayList<>();
    private final List<NGRelationship> differentRelationships = new ArrayList<>();
    private String pkType;

    public NG2Entity(String name, String entityAngularJSSuffix) {
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
        this.entityFileName = kebabCase(this.entityNameCapitalized + firstUpper(entityAngularJSSuffix));
        this.entityPluralFileName = entityNamePluralizedAndSpinalCased + entityAngularJSSuffix;
        this.entityServiceFileName = this.entityFileName;
        this.entityAngularName = this.entityClass + firstUpper(camelCase(entityAngularJSSuffix));
        this.entityAngularJSName = this.entityClass + firstUpper(camelCase(entityAngularJSSuffix));
        this.entityStateName = kebabCase(entityAngularName);
        this.entityUrl = this.entityStateName;
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

    @Override
    public void addRelationship(NGRelationship relationship) {
        getRelationships().add(relationship);
        // Load in-memory data for root
        if (MANY_TO_MANY.equals(relationship.getRelationshipType()) && relationship.isOwnerSide()) {
            setFieldsContainOwnerManyToMany(true);
        } else if (ONE_TO_ONE.equals(relationship.getRelationshipType()) && !relationship.isOwnerSide()) {
            setFieldsContainNoOwnerOneToOne(true);
        } else if (ONE_TO_ONE.equals(relationship.getRelationshipType()) && relationship.isOwnerSide()) {
            setFieldsContainOwnerOneToOne(true);
        } else if (ONE_TO_MANY.equals(relationship.getRelationshipType())) {
            setFieldsContainOneToMany(true);
        } else if (MANY_TO_ONE.equals(relationship.getRelationshipType())) {
            setFieldsContainManyToOne(true);
        }

        if (relationship.getRelationshipValidateRules() != null && relationship.getRelationshipValidateRules().contains("required")) {
            relationship.setRelationshipValidate(true);
            relationship.setRelationshipRequired(true);
            setValidation(true);
        }

        String entityType = relationship.getOtherEntityNameCapitalized();
        if (!differentTypes.contains(entityType)) {
            getDifferentTypes().add(entityType);
            getDifferentRelationships().add(relationship);
        }
    }

    @Override
    public void removeRelationship(NGRelationship relationship) {
        getRelationships().remove(relationship);
    }

    @Override
    public void addField(NGField field) {
        getFields().add(field);
        if (null != field.getFieldType()) {
            switch (field.getFieldType()) {
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

    @Override
    public void removeField(NGField field) {
        getFields().remove(field);
    }

    /**
     * @return the fields
     */
    @Override
    public List<NGField> getFields() {
        return fields;
    }

    /**
     * @return the relationships
     */
    @Override
    public List<NGRelationship> getRelationships() {
        return relationships;
    }

    /**
     * @return the name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the entityNameCapitalized
     */
    @Override
    public String getEntityNameCapitalized() {
        return entityNameCapitalized;
    }

    /**
     * @param entityNameCapitalized the entityNameCapitalized to set
     */
    @Override
    public void setEntityNameCapitalized(String entityNameCapitalized) {
        this.entityNameCapitalized = entityNameCapitalized;
    }

    /**
     * @return the entityClass
     */
    @Override
    public String getEntityClass() {
        return entityClass;
    }

    /**
     * @param entityClass the entityClass to set
     */
    @Override
    public void setEntityClass(String entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * @return the entityClassHumanized
     */
    @Override
    public String getEntityClassHumanized() {
        return entityClassHumanized;
    }

    /**
     * @param entityClassHumanized the entityClassHumanized to set
     */
    @Override
    public void setEntityClassHumanized(String entityClassHumanized) {
        this.entityClassHumanized = entityClassHumanized;
    }

    /**
     * @return the entityClassPlural
     */
    @Override
    public String getEntityClassPlural() {
        return entityClassPlural;
    }

    /**
     * @param entityClassPlural the entityClassPlural to set
     */
    @Override
    public void setEntityClassPlural(String entityClassPlural) {
        this.entityClassPlural = entityClassPlural;
    }

    /**
     * @return the entityClassPluralHumanized
     */
    @Override
    public String getEntityClassPluralHumanized() {
        return entityClassPluralHumanized;
    }

    /**
     * @param entityClassPluralHumanized the entityClassPluralHumanized to set
     */
    @Override
    public void setEntityClassPluralHumanized(String entityClassPluralHumanized) {
        this.entityClassPluralHumanized = entityClassPluralHumanized;
    }

    /**
     * @return the entityInstance
     */
    @Override
    public String getEntityInstance() {
        return entityInstance;
    }

    /**
     * @param entityInstance the entityInstance to set
     */
    @Override
    public void setEntityInstance(String entityInstance) {
        this.entityInstance = entityInstance;
    }

    /**
     * @return the entityInstancePlural
     */
    @Override
    public String getEntityInstancePlural() {
        return entityInstancePlural;
    }

    /**
     * @param entityInstancePlural the entityInstancePlural to set
     */
    @Override
    public void setEntityInstancePlural(String entityInstancePlural) {
        this.entityInstancePlural = entityInstancePlural;
    }

    /**
     * @return the entityApiUrl
     */
    @Override
    public String getEntityApiUrl() {
        return entityApiUrl;
    }

    /**
     * @param entityApiUrl the entityApiUrl to set
     */
    @Override
    public void setEntityApiUrl(String entityApiUrl) {
        this.entityApiUrl = entityApiUrl;
    }

    /**
     * @return the entityFolderName
     */
    @Override
    public String getEntityFolderName() {
        return entityFolderName;
    }

    /**
     * @param entityFolderName the entityFolderName to set
     */
    @Override
    public void setEntityFolderName(String entityFolderName) {
        this.entityFolderName = entityFolderName;
    }

    /**
     * @return the entityFileName
     */
    @Override
    public String getEntityFileName() {
        return entityFileName;
    }

    /**
     * @param entityFileName the entityFileName to set
     */
    @Override
    public void setEntityFileName(String entityFileName) {
        this.entityFileName = entityFileName;
    }

    /**
     * @return the entityPluralFileName
     */
    @Override
    public String getEntityPluralFileName() {
        return entityPluralFileName;
    }

    /**
     * @param entityPluralFileName the entityPluralFileName to set
     */
    @Override
    public void setEntityPluralFileName(String entityPluralFileName) {
        this.entityPluralFileName = entityPluralFileName;
    }

    /**
     * @return the entityServiceFileName
     */
    @Override
    public String getEntityServiceFileName() {
        return entityServiceFileName;
    }

    /**
     * @param entityServiceFileName the entityServiceFileName to set
     */
    @Override
    public void setEntityServiceFileName(String entityServiceFileName) {
        this.entityServiceFileName = entityServiceFileName;
    }

    /**
     * @return the entityAngularJSName
     */
    @Override
    public String getEntityAngularJSName() {
        return entityAngularJSName;
    }

    /**
     * @param entityAngularJSName the entityAngularJSName to set
     */
    @Override
    public void setEntityAngularJSName(String entityAngularJSName) {
        this.entityAngularJSName = entityAngularJSName;
    }

    /**
     * @return the entityStateName
     */
    @Override
    public String getEntityStateName() {
        return entityStateName;
    }

    /**
     * @param entityStateName the entityStateName to set
     */
    @Override
    public void setEntityStateName(String entityStateName) {
        this.entityStateName = entityStateName;
    }

    /**
     * @return the entityUrl
     */
    @Override
    public String getEntityUrl() {
        return entityUrl;
    }

    /**
     * @param entityUrl the entityUrl to set
     */
    @Override
    public void setEntityUrl(String entityUrl) {
        this.entityUrl = entityUrl;
    }

    /**
     * @return the entityTranslationKey
     */
    @Override
    public String getEntityTranslationKey() {
        return entityTranslationKey;
    }

    /**
     * @param entityTranslationKey the entityTranslationKey to set
     */
    @Override
    public void setEntityTranslationKey(String entityTranslationKey) {
        this.entityTranslationKey = entityTranslationKey;
    }

    /**
     * @return the entityTranslationKeyMenu
     */
    @Override
    public String getEntityTranslationKeyMenu() {
        return entityTranslationKeyMenu;
    }

    /**
     * @param entityTranslationKeyMenu the entityTranslationKeyMenu to set
     */
    @Override
    public void setEntityTranslationKeyMenu(String entityTranslationKeyMenu) {
        this.entityTranslationKeyMenu = entityTranslationKeyMenu;
    }

    /**
     * @return the fieldsContainZonedDateTime
     */
    @Override
    public boolean isFieldsContainZonedDateTime() {
        return fieldsContainZonedDateTime;
    }

    /**
     * @param fieldsContainZonedDateTime the fieldsContainZonedDateTime to set
     */
    @Override
    public void setFieldsContainZonedDateTime(boolean fieldsContainZonedDateTime) {
        this.fieldsContainZonedDateTime = fieldsContainZonedDateTime;
    }

    /**
     * @return the fieldsContainLocalDate
     */
    @Override
    public boolean isFieldsContainLocalDate() {
        return fieldsContainLocalDate;
    }

    /**
     * @param fieldsContainLocalDate the fieldsContainLocalDate to set
     */
    @Override
    public void setFieldsContainLocalDate(boolean fieldsContainLocalDate) {
        this.fieldsContainLocalDate = fieldsContainLocalDate;
    }

    /**
     * @return the fieldsContainBigDecimal
     */
    @Override
    public boolean isFieldsContainBigDecimal() {
        return fieldsContainBigDecimal;
    }

    /**
     * @param fieldsContainBigDecimal the fieldsContainBigDecimal to set
     */
    @Override
    public void setFieldsContainBigDecimal(boolean fieldsContainBigDecimal) {
        this.fieldsContainBigDecimal = fieldsContainBigDecimal;
    }

    /**
     * @return the fieldsContainBlob
     */
    @Override
    public boolean isFieldsContainBlob() {
        return fieldsContainBlob;
    }

    /**
     * @param fieldsContainBlob the fieldsContainBlob to set
     */
    @Override
    public void setFieldsContainBlob(boolean fieldsContainBlob) {
        this.fieldsContainBlob = fieldsContainBlob;
    }

    /**
     * @return the validation
     */
    @Override
    public boolean isValidation() {
        return validation;
    }

    /**
     * @param validation the validation to set
     */
    @Override
    public void setValidation(boolean validation) {
        this.validation = validation;
    }

    /**
     * @return the fieldsContainOwnerManyToMany
     */
    @Override
    public boolean isFieldsContainOwnerManyToMany() {
        return fieldsContainOwnerManyToMany;
    }

    /**
     * @param fieldsContainOwnerManyToMany the fieldsContainOwnerManyToMany to
     * set
     */
    @Override
    public void setFieldsContainOwnerManyToMany(boolean fieldsContainOwnerManyToMany) {
        this.fieldsContainOwnerManyToMany = fieldsContainOwnerManyToMany;
    }

    /**
     * @return the fieldsContainNoOwnerOneToOne
     */
    @Override
    public boolean isFieldsContainNoOwnerOneToOne() {
        return fieldsContainNoOwnerOneToOne;
    }

    /**
     * @param fieldsContainNoOwnerOneToOne the fieldsContainNoOwnerOneToOne to
     * set
     */
    @Override
    public void setFieldsContainNoOwnerOneToOne(boolean fieldsContainNoOwnerOneToOne) {
        this.fieldsContainNoOwnerOneToOne = fieldsContainNoOwnerOneToOne;
    }

    /**
     * @return the fieldsContainOwnerOneToOne
     */
    @Override
    public boolean isFieldsContainOwnerOneToOne() {
        return fieldsContainOwnerOneToOne;
    }

    /**
     * @param fieldsContainOwnerOneToOne the fieldsContainOwnerOneToOne to set
     */
    @Override
    public void setFieldsContainOwnerOneToOne(boolean fieldsContainOwnerOneToOne) {
        this.fieldsContainOwnerOneToOne = fieldsContainOwnerOneToOne;
    }

    /**
     * @return the fieldsContainOneToMany
     */
    @Override
    public boolean isFieldsContainOneToMany() {
        return fieldsContainOneToMany;
    }

    /**
     * @param fieldsContainOneToMany the fieldsContainOneToMany to set
     */
    @Override
    public void setFieldsContainOneToMany(boolean fieldsContainOneToMany) {
        this.fieldsContainOneToMany = fieldsContainOneToMany;
    }

    /**
     * @return the fieldsContainManyToOne
     */
    @Override
    public boolean isFieldsContainManyToOne() {
        return fieldsContainManyToOne;
    }

    /**
     * @param fieldsContainManyToOne the fieldsContainManyToOne to set
     */
    @Override
    public void setFieldsContainManyToOne(boolean fieldsContainManyToOne) {
        this.fieldsContainManyToOne = fieldsContainManyToOne;
    }

    /**
     * @return the differentTypes
     */
    @Override
    public List<String> getDifferentTypes() {
        return differentTypes;
    }

    /**
     * @param differentTypes the differentTypes to set
     */
    @Override
    public void setDifferentTypes(List<String> differentTypes) {
        this.differentTypes = differentTypes;
    }

    /**
     * @return the differentRelationships
     */
    @Override
    public List<NGRelationship> getDifferentRelationships() {
        return differentRelationships;
    }


    /**
     * @return the pkType
     */
    @Override
    public String getPkType() {
        return pkType;
    }

    /**
     * @param pkType the pkType to set
     */
    @Override
    public void setPkType(String pkType) {
        this.pkType = pkType;
    }
}
