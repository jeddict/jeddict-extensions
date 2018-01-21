package org.netbeans.jpa.modeler.spec.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "many-to-many")
@XmlRootElement
public class ManyToManyAssociation extends MultiAssociationAttribute {

}
