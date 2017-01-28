//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2014.01.21 at 01:52:19 PM IST
//
package org.netbeans.jpa.modeler.spec;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import org.eclipse.persistence.internal.jpa.metadata.columns.OrderColumnMetadata;
import static org.netbeans.jcode.jpa.JPAConstants.ORDER_COLUMN_FQN;
import org.netbeans.jpa.source.JavaSourceParserUtil;

/**
 *
 *
 * @Target({METHOD, FIELD}) @Retention(RUNTIME) public @interface OrderColumn {
 * String name() default ""; boolean nullable() default true; boolean
 * insertable() default true; boolean updatable() default true; String
 * columnDefinition() default ""; }
 *
 *
 *
 * <p>
 * Java class for order-column complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType name="order-column">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="nullable" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="insertable" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="updatable" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="column-definition" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "order-column")
public class OrderColumn {

    @XmlAttribute(name = "n")
    protected String name;
    @XmlAttribute(name = "nu")
    protected Boolean nullable = true;
    @XmlAttribute(name = "in")
    protected Boolean insertable = true;
    @XmlAttribute(name = "up")
    protected Boolean updatable = true;
    @XmlAttribute(name = "cd")
    protected String columnDefinition;

    public static OrderColumn load(Element element, VariableElement variableElement) {
        AnnotationMirror annotationMirror = JavaSourceParserUtil.findAnnotation(element, ORDER_COLUMN_FQN);
        OrderColumn orderColumn = null;
        if (annotationMirror != null) {
            orderColumn = new OrderColumn();
            orderColumn.name = (String) JavaSourceParserUtil.findAnnotationValue(annotationMirror, "name");
            orderColumn.nullable = (Boolean) JavaSourceParserUtil.findAnnotationValue(annotationMirror, "nullable");
            orderColumn.insertable = (Boolean) JavaSourceParserUtil.findAnnotationValue(annotationMirror, "insertable");
            orderColumn.updatable = (Boolean) JavaSourceParserUtil.findAnnotationValue(annotationMirror, "updatable");
            orderColumn.columnDefinition = (String) JavaSourceParserUtil.findAnnotationValue(annotationMirror, "columnDefinition");
        }
        return orderColumn;
    }

    /**
     * Gets the value of the name property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the nullable property.
     *
     * @return possible object is {@link Boolean }
     *
     */
    public Boolean isNullable() {
        return nullable;
    }

    /**
     * Sets the value of the nullable property.
     *
     * @param value allowed object is {@link Boolean }
     *
     */
    public void setNullable(Boolean value) {
        this.nullable = value;
    }

    /**
     * Gets the value of the insertable property.
     *
     * @return possible object is {@link Boolean }
     *
     */
    public Boolean isInsertable() {
        return insertable;
    }

    /**
     * Sets the value of the insertable property.
     *
     * @param value allowed object is {@link Boolean }
     *
     */
    public void setInsertable(Boolean value) {
        this.insertable = value;
    }

    /**
     * Gets the value of the updatable property.
     *
     * @return possible object is {@link Boolean }
     *
     */
    public Boolean isUpdatable() {
        return updatable;
    }

    /**
     * Sets the value of the updatable property.
     *
     * @param value allowed object is {@link Boolean }
     *
     */
    public void setUpdatable(Boolean value) {
        this.updatable = value;
    }

    /**
     * Gets the value of the columnDefinition property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getColumnDefinition() {
        return columnDefinition;
    }

    /**
     * Sets the value of the columnDefinition property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setColumnDefinition(String value) {
        this.columnDefinition = value;
    }

    public OrderColumnMetadata getAccessor() {
//        OrderColumnMetadata accessor = new OrderColumnMetadata();
//        accessor.setName(name);
//        accessor.setColumnDefinition(columnDefinition);
//        accessor.setNullable(nullable);
//        accessor.setInsertable(insertable);
//        accessor.setUpdatable(updatable);
//        return accessor;
            return null;
    }

}
