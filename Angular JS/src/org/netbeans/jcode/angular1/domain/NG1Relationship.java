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
package org.netbeans.jcode.angular1.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.apache.commons.lang.StringUtils.EMPTY;
import org.netbeans.jcode.core.util.StringHelper;
import static org.netbeans.jcode.core.util.StringHelper.firstLower;
import static org.netbeans.jcode.core.util.StringHelper.firstUpper;
import static org.netbeans.jcode.core.util.StringHelper.kebabCase;
import static org.netbeans.jcode.core.util.StringHelper.startCase;
import static org.netbeans.jcode.core.util.StringHelper.trim;
import org.netbeans.jcode.ng.main.domain.NGRelationship;
import static org.netbeans.jcode.ng.main.domain.NGRelationship.MANY_TO_MANY;
import static org.netbeans.jcode.ng.main.domain.NGRelationship.MANY_TO_ONE;
import static org.netbeans.jcode.ng.main.domain.NGRelationship.ONE_TO_MANY;
import static org.netbeans.jcode.ng.main.domain.NGRelationship.ONE_TO_ONE;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.ManyToMany;
import org.netbeans.jpa.modeler.spec.ManyToOne;
import org.netbeans.jpa.modeler.spec.OneToMany;
import org.netbeans.jpa.modeler.spec.OneToOne;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;

public class NG1Relationship implements NGRelationship{
        
    //        Name->RelationshipName
    //        OtherEntityName->OtherEntityField

    private static final Logger LOG = Logger.getLogger(NG1Relationship.class.getName());
    
    
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
    public String otherEntityModuleName;
    public String otherEntityModulePath;
    public boolean relationshipValidate;
    public boolean relationshipRequired;
    public String entityAngularJSSuffix;

    public List<String> relationshipValidateRules = new ArrayList<>();

//    public NG1Relationship(String angularAppName, String relationshipType, boolean ownerSide, String relationshipName, String otherEntityName, String entityAngularJSSuffix) {
//        this.relationshipType = relationshipType;
//        this.ownerSide = ownerSide;
//        this.relationshipName=relationshipName;
//        this.otherEntityName = otherEntityName;
//        this.entityAngularJSSuffix = entityAngularJSSuffix;
//        setOtherEntityModule(angularAppName);
//    }
  
    public NG1Relationship(String angularAppName, Entity entity, RelationAttribute relation) {
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
        setOtherEntityModule(angularAppName);
    }
    
    private void setOtherEntityModule(String angularAppName) {
        if (!"User".equals(this.getOtherEntityNameCapitalized())) {
            this.otherEntityModuleName = angularAppName + this.getOtherEntityNameCapitalized() + "Module";
             this.otherEntityModulePath = kebabCase(firstLower(this.otherEntityName));
        } else {
            this.otherEntityModuleName = angularAppName + "SharedModule";
            this.otherEntityModulePath = "../shared";
        }
    }

    /**
     * @return the relationshipType
     */
    @Override
    public String getRelationshipType() {
        if (relationshipType == null) {
            throw new IllegalStateException("relationshipType is missing in " + this.getName() + " for relationship");
        }
        return relationshipType;
    }

    /**
     * @param relationshipType the relationshipType to set
     */
    @Override
    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }

    /**
     * @return the ownerSide
     */
    @Override
    public boolean isOwnerSide() {

        return ownerSide;
    }

    /**
     * @param ownerSide the ownerSide to set
     */
    @Override
    public void setOwnerSide(boolean ownerSide) {
        this.ownerSide = ownerSide;
    }

    /**
     * @return the relationshipName
     */
    @Override
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
    @Override
    public void setRelationshipName(String relationshipName) {
        this.relationshipName = relationshipName;
    }

    /**
     * @return the relationshipFieldName
     */
    @Override
    public String getRelationshipFieldName() {
        if (relationshipFieldName == null) {
            relationshipFieldName = firstLower(relationshipName);
        }
        return relationshipFieldName;
    }

    /**
     * @return the relationshipFieldNamePlural
     */
    @Override
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
            return StringHelper.pluralize(data);
         }
    }
    /**
     * @return the relationshipNameHumanized
     */
    @Override
    public String getRelationshipNameHumanized() {
        if (relationshipNameHumanized == null) {
            relationshipNameHumanized = startCase(relationshipName);
        }
        return relationshipNameHumanized;
    }

    /**
     * @return the otherEntityName
     */
    @Override
    public String getOtherEntityName() {
        if (otherEntityName == null) {
            throw new IllegalStateException("otherEntityName is missing in " + getName() + " for relationship ");
        }
        return otherEntityName;
    }

    /**
     * @return the otherEntityStateName
     */
    @Override
    public String getOtherEntityStateName() {
        if (otherEntityStateName == null) {
            otherEntityStateName = trim(kebabCase(otherEntityName), '-') + (this.entityAngularJSSuffix!=null?entityAngularJSSuffix:EMPTY); 
        }
        return otherEntityStateName;
    }

    /**
     * @return the otherEntityField
     */
    @Override
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
    @Override
    public void setOtherEntityField(String otherEntityField) {
        this.otherEntityField = otherEntityField;
    }

    /**
     * @return the otherEntityFieldCapitalized
     */
    @Override
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
    @Override
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
    @Override
    public void setOtherEntityRelationshipName(String otherEntityRelationshipName) {
        this.otherEntityRelationshipName = otherEntityRelationshipName;
    }

    /**
     * @return the relationshipValidateRules
     */
    @Override
    public List<String> getRelationshipValidateRules() {
        return relationshipValidateRules;
    }

    /**
     * @param relationshipValidateRules the relationshipValidateRules to set
     */
    @Override
    public void setRelationshipValidateRules(List<String> relationshipValidateRules) {
        this.relationshipValidateRules = relationshipValidateRules;
    }

    /**
     * @return the relationshipValidate
     */
    @Override
    public boolean isRelationshipValidate() {
        return relationshipValidate;
    }

    /**
     * @param relationshipValidate the relationshipValidate to set
     */
    @Override
    public void setRelationshipValidate(boolean relationshipValidate) {
        this.relationshipValidate = relationshipValidate;
    }

    /**
     * @return the relationshipRequired
     */
    @Override
    public boolean isRelationshipRequired() {
        return relationshipRequired;
    }

    /**
     * @param relationshipRequired the relationshipRequired to set
     */
    @Override
    public void setRelationshipRequired(boolean relationshipRequired) {
        this.relationshipRequired = relationshipRequired;
    }

    /**
     * @return the otherEntityNameCapitalized
     */
    @Override
    public String getOtherEntityNameCapitalized() {
        if (otherEntityNameCapitalized == null) {
            otherEntityNameCapitalized = firstUpper(otherEntityName);
        }
        return otherEntityNameCapitalized;
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
     * @return the relationshipNameCapitalized
     */
    @Override
    public String getRelationshipNameCapitalized() {
        if (relationshipNameCapitalized == null) {
            relationshipNameCapitalized = firstUpper(relationshipName);
        }
        return relationshipNameCapitalized;
    }

    /**
     * @return the relationshipNameCapitalizedPlural
     */
    @Override
    public String getRelationshipNameCapitalizedPlural() {
        if (relationshipNameCapitalizedPlural == null) {
            relationshipNameCapitalizedPlural = pluralize(firstUpper(relationshipName));
        }
        return relationshipNameCapitalizedPlural;
    }

    /**
     * @return the relationshipNamePlural
     */
    @Override
    public String getRelationshipNamePlural() {
        if (relationshipNamePlural == null) {
            relationshipNamePlural = pluralize(relationshipName);
        }
        return relationshipNamePlural;
    }

    /**
     * @return the otherEntityRelationshipNamePlural
     */
    @Override
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
    @Override
    public String getOtherEntityNamePlural() {
        if (otherEntityNamePlural == null) {
            otherEntityNamePlural = pluralize(otherEntityName);
        }
        return otherEntityNamePlural;
    }

    /**
     * @return the otherEntityNameCapitalizedPlural
     */
    @Override
    public String getOtherEntityNameCapitalizedPlural() {
        if (otherEntityNameCapitalizedPlural == null) {
            otherEntityNameCapitalizedPlural = pluralize(firstUpper(otherEntityName));
        }
        return otherEntityNameCapitalizedPlural;
    }
    
     
}
