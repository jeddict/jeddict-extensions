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
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.netbeans.jcode.core.util.StringHelper.firstLower;
import static org.netbeans.jcode.core.util.StringHelper.firstUpper;
import static org.netbeans.jcode.core.util.StringHelper.kebabCase;
import static org.netbeans.jcode.core.util.StringHelper.startCase;
import static org.netbeans.jcode.core.util.StringHelper.trim;
import org.netbeans.jcode.entity.info.EntityClassInfo;
import org.netbeans.jcode.entity.info.EntityClassInfo.FieldInfo;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.ManyToMany;
import org.netbeans.jpa.modeler.spec.ManyToOne;
import org.netbeans.jpa.modeler.spec.OneToMany;
import org.netbeans.jpa.modeler.spec.OneToOne;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;

public class NGRelationship {
        
    //        Name->RelationshipName
    //        OtherEntityName->OtherEntityField
    public static final String ONE_TO_ONE = "one-to-one";
    public static final String ONE_TO_MANY = "one-to-many";
    public static final String MANY_TO_ONE = "many-to-one";
    public static final String MANY_TO_MANY = "many-to-many";
    private static final Logger LOG = Logger.getLogger(NGRelationship.class.getName());
    
    
    public String name;//self entityName , used for otherEntityRelationshipName

    public String relationshipType;//MANY_TO_ONE,ONE_TO_ONE ,MANY_TO_MANY
    public boolean ownerSide;

    public String relationshipName;
    public String relationshipNameHumanized;
    public String relationshipNamePlural;
    public String relationshipFieldName;
    public String relationshipFieldNamePlural;
    public String relationshipNameCapitalized;
    public String relationshipNameCapitalizedPlural;

    public String otherEntityName;
    public String otherEntityNameCapitalized;
    public String otherEntityNamePlural;
    public String otherEntityNameCapitalizedPlural;
    public String otherEntityStateName;
    public String otherEntityField;
    public String otherEntityFieldCapitalized;
    public String otherEntityRelationshipName;
    public String otherEntityRelationshipNamePlural;
    public boolean relationshipValidate;
    public boolean relationshipRequired;
    public String entityAngularJSSuffix;

    public List<String> relationshipValidateRules = new ArrayList<>();

    public NGRelationship(String relationshipType, boolean ownerSide, String relationshipName, String otherEntityName, String entityAngularJSSuffix) {
        this.relationshipType = relationshipType;
        this.ownerSide = ownerSide;
        this.relationshipName=relationshipName;
        this.otherEntityName = otherEntityName;
        this.entityAngularJSSuffix = entityAngularJSSuffix;
    }

    @Deprecated
    public NGRelationship(EntityClassInfo classInfo, FieldInfo fieldInfo) {
        this.relationshipName = fieldInfo.getName();
        this.ownerSide = fieldInfo.getMappedByField()==null;
        if(fieldInfo.isManyToMany()){
            this.otherEntityName = firstLower(fieldInfo.getSimpleTypeArgName());
            relationshipType = MANY_TO_MANY;
        } else if(fieldInfo.isOneToMany()){
            this.otherEntityName = firstLower(fieldInfo.getSimpleTypeArgName());
            relationshipType = ONE_TO_MANY;
        } else if(fieldInfo.isManyToOne()){
            this.otherEntityName = firstLower(fieldInfo.getSimpleTypeName());
            relationshipType = MANY_TO_ONE;
        } else if(fieldInfo.isOneToOne()){
            this.otherEntityName = firstLower(fieldInfo.getSimpleTypeName());
            relationshipType = ONE_TO_ONE;
        } 
        this.name = classInfo.getName();
    }
    
        public NGRelationship(Entity entity, RelationAttribute relation) {
        this.relationshipName = relation.getName();
        this.ownerSide = relation.isOwner();
        if(relation instanceof ManyToMany){
            this.otherEntityName = firstLower(relation.getConnectedEntity().getClazz());
            relationshipType = MANY_TO_MANY;
        } else if(relation instanceof OneToMany){
            this.otherEntityName = firstLower(relation.getConnectedEntity().getClazz());
            relationshipType = ONE_TO_MANY;
        } else if(relation instanceof ManyToOne){
            this.otherEntityName = firstLower(relation.getConnectedEntity().getClazz());
            relationshipType = MANY_TO_ONE;
        } else if(relation instanceof OneToOne){
            this.otherEntityName = firstLower(relation.getConnectedEntity().getClazz());
            relationshipType = ONE_TO_ONE;
        } 
        this.name = entity.getClazz();
    }
    
    

    /**
     * @return the relationshipType
     */
    public String getRelationshipType() {
        if (relationshipType == null) {
            throw new IllegalStateException("relationshipType is missing in " + this.getName() + " for relationship");
        }
        return relationshipType;
    }

    /**
     * @param relationshipType the relationshipType to set
     */
    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }

    /**
     * @return the ownerSide
     */
    public boolean isOwnerSide() {

        return ownerSide;
    }

    /**
     * @param ownerSide the ownerSide to set
     */
    public void setOwnerSide(boolean ownerSide) {
        this.ownerSide = ownerSide;
    }

    /**
     * @return the relationshipName
     */
    public String getRelationshipName() {
        if (relationshipName == null) {
            relationshipName = getOtherEntityName();
            LOG.log(Level.WARNING, "relationshipName is missing in {0} for relationship using {1} as fallback", new Object[]{getName(), otherEntityName});
        }
        return relationshipName;
    }

    /**
     * @param relationshipName the relationshipName to set
     */
    public void setRelationshipName(String relationshipName) {
        this.relationshipName = relationshipName;
    }

    /**
     * @return the relationshipFieldName
     */
    public String getRelationshipFieldName() {
        if (relationshipFieldName == null) {
            relationshipFieldName = firstLower(relationshipName);
        }
        return relationshipFieldName;
    }

    /**
     * @return the relationshipFieldNamePlural
     */
    public String getRelationshipFieldNamePlural() {
        if (relationshipFieldNamePlural == null) {
              relationshipFieldNamePlural = pluralize(firstLower(relationshipName));
           
        }
        return relationshipFieldNamePlural;
    }

    String pluralize(String data){
         if( relationshipType.equals(ONE_TO_MANY) ||  relationshipType.equals(MANY_TO_MANY)){
             return data;
         } else {
            return org.netbeans.jcode.generator.internal.util.Util.pluralize(data);
         }
    }
    /**
     * @return the relationshipNameHumanized
     */
    public String getRelationshipNameHumanized() {
        if (relationshipNameHumanized == null) {
            relationshipNameHumanized = startCase(relationshipName);
        }
        return relationshipNameHumanized;
    }

    /**
     * @return the otherEntityName
     */
    public String getOtherEntityName() {
        if (otherEntityName == null) {
            throw new IllegalStateException("otherEntityName is missing in " + getName() + " for relationship ");
        }
        return otherEntityName;
    }

    /**
     * @return the otherEntityStateName
     */
    public String getOtherEntityStateName() {
        if (otherEntityStateName == null) {
            otherEntityStateName = trim(kebabCase(otherEntityName), '-') + (this.entityAngularJSSuffix!=null?entityAngularJSSuffix:EMPTY); 
        }
        return otherEntityStateName;
    }

    /**
     * @return the otherEntityField
     */
    public String getOtherEntityField() {
        if (otherEntityField == null && (MANY_TO_ONE.equals(relationshipType)
                || (MANY_TO_MANY.equals(relationshipType) && ownerSide == true)
                || (ONE_TO_ONE.equals(relationshipType) && ownerSide == true))) {
            otherEntityField = "id";
            LOG.log(Level.WARNING, "otherEntityField is missing in {0} for relationship , using id as fallback", this.getName());
        }
        return otherEntityField;
    }

    /**
     * @param otherEntityField the otherEntityField to set
     */
    public void setOtherEntityField(String otherEntityField) {
        this.otherEntityField = otherEntityField;
    }

    /**
     * @return the otherEntityFieldCapitalized
     */
    public String getOtherEntityFieldCapitalized() {
        if (otherEntityFieldCapitalized == null) {
            String OtherEntityField = getOtherEntityField();
            otherEntityFieldCapitalized = OtherEntityField!=null?firstUpper(OtherEntityField):null;
        }
        return otherEntityFieldCapitalized;
    }

    /**
     * @return the otherEntityRelationshipName
     */
    public String getOtherEntityRelationshipName() {
        if (otherEntityRelationshipName == null
                && (ONE_TO_MANY.equals(relationshipType)
                || (MANY_TO_MANY.equals(relationshipType) && ownerSide == false)
                || (ONE_TO_ONE.equals(relationshipType)))) {
            otherEntityRelationshipName = firstLower(getName());//warning
            LOG.log(Level.WARNING, "otherEntityRelationshipName is missing in {0} for relationship , using {1} as fallback", new Object[]{this.getName(), firstLower(this.getName())});
        }
        return otherEntityRelationshipName;
    }

    /**
     * @param otherEntityRelationshipName the otherEntityRelationshipName to set
     */
    public void setOtherEntityRelationshipName(String otherEntityRelationshipName) {
        this.otherEntityRelationshipName = otherEntityRelationshipName;
    }

    /**
     * @return the relationshipValidateRules
     */
    public List<String> getRelationshipValidateRules() {
        return relationshipValidateRules;
    }

    /**
     * @param relationshipValidateRules the relationshipValidateRules to set
     */
    public void setRelationshipValidateRules(List<String> relationshipValidateRules) {
        this.relationshipValidateRules = relationshipValidateRules;
    }

    /**
     * @return the relationshipValidate
     */
    public boolean isRelationshipValidate() {
        return relationshipValidate;
    }

    /**
     * @param relationshipValidate the relationshipValidate to set
     */
    public void setRelationshipValidate(boolean relationshipValidate) {
        this.relationshipValidate = relationshipValidate;
    }

    /**
     * @return the relationshipRequired
     */
    public boolean isRelationshipRequired() {
        return relationshipRequired;
    }

    /**
     * @param relationshipRequired the relationshipRequired to set
     */
    public void setRelationshipRequired(boolean relationshipRequired) {
        this.relationshipRequired = relationshipRequired;
    }

    /**
     * @return the otherEntityNameCapitalized
     */
    public String getOtherEntityNameCapitalized() {
        if (otherEntityNameCapitalized == null) {
            otherEntityNameCapitalized = firstUpper(otherEntityName);
        }
        return otherEntityNameCapitalized;
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
     * @return the relationshipNameCapitalized
     */
    public String getRelationshipNameCapitalized() {
        if (relationshipNameCapitalized == null) {
            relationshipNameCapitalized = firstUpper(relationshipName);
        }
        return relationshipNameCapitalized;
    }

    /**
     * @return the relationshipNameCapitalizedPlural
     */
    public String getRelationshipNameCapitalizedPlural() {
        if (relationshipNameCapitalizedPlural == null) {
            relationshipNameCapitalizedPlural = pluralize(firstUpper(relationshipName));
        }
        return relationshipNameCapitalizedPlural;
    }

    /**
     * @return the relationshipNamePlural
     */
    public String getRelationshipNamePlural() {
        if (relationshipNamePlural == null) {
            relationshipNamePlural = pluralize(relationshipName);
        }
        return relationshipNamePlural;
    }

    /**
     * @return the otherEntityRelationshipNamePlural
     */
    public String getOtherEntityRelationshipNamePlural() {
        if (otherEntityRelationshipNamePlural != null
                && (ONE_TO_MANY.equals(relationshipType)
                || (MANY_TO_MANY.equals(relationshipType) && ownerSide == false)
                || (ONE_TO_ONE.equals(relationshipType)))) {
            otherEntityRelationshipNamePlural = pluralize(otherEntityRelationshipName);
        }
        return otherEntityRelationshipNamePlural;
    }

    /**
     * @return the otherEntityNamePlural
     */
    public String getOtherEntityNamePlural() {
        if (otherEntityNamePlural == null) {
            otherEntityNamePlural = pluralize(otherEntityName);
        }
        return otherEntityNamePlural;
    }

    /**
     * @return the otherEntityNameCapitalizedPlural
     */
    public String getOtherEntityNameCapitalizedPlural() {
        if (otherEntityNameCapitalizedPlural == null) {
            otherEntityNameCapitalizedPlural = pluralize(firstUpper(otherEntityName));
        }
        return otherEntityNameCapitalizedPlural;
    }
    
     
}
