/**
 * Copyright [2018] Gaurav Gupta
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
package org.netbeans.jpa.modeler.spec.bean;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlTransient;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.JavaClass;
import org.netbeans.jpa.modeler.spec.jaxb.JaxbVariableType;

/**
 *
 * @author Gaurav Gupta
 */
public abstract class AssociationAttribute extends Attribute {

    @XmlAttribute(name = "connected-class-id", required = true)
    @XmlIDREF
    private JavaClass connectedClass;

    @XmlAttribute(name = "connected-attribute-id", required = true)
    @XmlIDREF
    private AssociationAttribute connectedAttribute;

    @XmlTransient
    protected String targetClass;

    public String getTargetClass() {
        if (targetClass != null) {
            return targetClass;
        }
        if (connectedClass != null) {
            return connectedClass.getClazz();
        } else {
            return null;
        }
    }

    public void setTargetClass(String value) {
        this.targetClass = value;
    }

    /**
     * @return the connectedEntityId
     */
    public JavaClass getConnectedClass() {
        return connectedClass;
    }

    /**
     * @param connectedClass the connectedClass to set
     */
    public void setConnectedClass(JavaClass connectedClass) {
        this.connectedClass = connectedClass;
    }

    /**
     * @return the connectedAttribute
     */
    public AssociationAttribute getConnectedAttribute() {
        return connectedAttribute;
    }

    /**
     * @param connectedAttribute the connectedAttribute to set
     */
    public void setConnectedAttribute(AssociationAttribute connectedAttribute) {
        this.connectedAttribute = connectedAttribute;
    }

    public String getConnectedAttributeName() {
        return connectedAttribute != null ? connectedAttribute.getName() : null;
    }

    @Override
    public List<JaxbVariableType> getJaxbVariableList() {
        List<JaxbVariableType> jaxbVariableTypeList = new ArrayList<>();
        jaxbVariableTypeList.add(JaxbVariableType.XML_DEFAULT);
        jaxbVariableTypeList.add(JaxbVariableType.XML_ELEMENT);
        jaxbVariableTypeList.add(JaxbVariableType.XML_ELEMENT_WRAPPER);
        jaxbVariableTypeList.add(JaxbVariableType.XML_TRANSIENT);
        jaxbVariableTypeList.add(JaxbVariableType.XML_INVERSE_REFERENCE);//both side are applicable
        jaxbVariableTypeList.add(JaxbVariableType.XML_ELEMENT_REF);
        return jaxbVariableTypeList;
    }

    @Override
    public String getDataTypeLabel() {
        return getTargetClass();
    }

    /**
     * @return the owner
     */
    public abstract boolean isOwner();

    /**
     * @param owner the owner to set
     */
    public abstract void setOwner(boolean owner);

}
