package org.netbeans.jpa.modeler.spec.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import org.netbeans.jpa.modeler.spec.extend.BaseAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class BeanAttribute extends BaseAttribute {

    @XmlAttribute(name = "attribute-type")
    private String attributeType;

    public BeanAttribute() {
    }

    /**
     * @return the attributeType
     */
    public String getAttributeType() {
        return attributeType;
    }

    /**
     * @param attributeType the attributeType to set
     */
    public void setAttributeType(String attributeType) {
        this.attributeType = attributeType;
    }

    @Override
    public String getDataTypeLabel() {
        return getAttributeType();
    }
    
}
