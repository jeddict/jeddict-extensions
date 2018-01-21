package org.netbeans.jpa.modeler.spec.bean;

import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import org.netbeans.jpa.modeler.spec.extend.CollectionTypeHandler;

public class BeanCollectionAttribute extends BeanAttribute implements CollectionTypeHandler {

    @XmlAttribute(name = "ct")
    private String collectionType;
    @XmlAttribute(name = "cit")
    private String collectionImplType;

    public BeanCollectionAttribute() {
    }

    /**
     * @return the collectionType
     */
    @Override
    public String getCollectionType() {
        if (collectionType == null) {
            collectionType = List.class.getName();
        }
        return collectionType;
    }

    /**
     * @param collectionType the collectionType to set
     */
    @Override
    public void setCollectionType(String collectionType) {
        this.collectionType = collectionType;
    }

    /**
     * @return the collectionImplType
     */
    @Override
    public String getCollectionImplType() {
        return collectionImplType;
    }

    /**
     * @param collectionImplType the collectionImplementationType to set
     */
    @Override
    public void setCollectionImplType(String collectionImplType) {
        this.collectionImplType = collectionImplType;
    }

}
