package org.netbeans.jpa.modeler.spec.extend;

import org.netbeans.jpa.modeler.spec.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.ColumnHandler;

@XmlAccessorType(XmlAccessType.FIELD)
public class PlainAttribute extends Attribute {

    @XmlAttribute(name = "attribute-type")
    private String attributeType;
    
    @XmlTransient
    private Attribute connectedAttribute;
    
    @XmlAttribute(name = "derived")
    private boolean derived;
    

    public PlainAttribute() {
    }

    public PlainAttribute(Attribute connectedAttribute) {
        this.connectedAttribute = connectedAttribute;
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

    /**
     * @return the derived
     */
    public boolean isDerived() {
        return derived;
    }

    /**
     * @param derived the derived to set
     */
    public void setDerived(boolean derived) {
        this.derived = derived;
    }

    /**
     * @return the connectedAttribute
     */
    public Attribute getConnectedAttribute() {
        return connectedAttribute;
    }

    @Override
    public String getDataTypeLabel() {
        return getAttributeType();
    }
    
}
