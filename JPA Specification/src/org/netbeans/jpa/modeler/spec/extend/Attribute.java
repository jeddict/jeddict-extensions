/**
 * Copyright [2014] Gaurav Gupta
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
package org.netbeans.jpa.modeler.spec.extend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toSet;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlTransient;
import static org.netbeans.jcode.core.util.AttributeType.BIGDECIMAL;
import static org.netbeans.jcode.core.util.AttributeType.BIGINTEGER;
import static org.netbeans.jcode.core.util.AttributeType.BYTE;
import static org.netbeans.jcode.core.util.AttributeType.BYTE_WRAPPER;
import static org.netbeans.jcode.core.util.AttributeType.DOUBLE;
import static org.netbeans.jcode.core.util.AttributeType.DOUBLE_WRAPPER;
import static org.netbeans.jcode.core.util.AttributeType.FLOAT;
import static org.netbeans.jcode.core.util.AttributeType.FLOAT_WRAPPER;
import static org.netbeans.jcode.core.util.AttributeType.INT;
import static org.netbeans.jcode.core.util.AttributeType.INT_WRAPPER;
import static org.netbeans.jcode.core.util.AttributeType.LONG;
import static org.netbeans.jcode.core.util.AttributeType.LONG_WRAPPER;
import static org.netbeans.jcode.core.util.AttributeType.SHORT;
import static org.netbeans.jcode.core.util.AttributeType.SHORT_WRAPPER;
import static org.netbeans.jcode.core.util.AttributeType.STRING;
import static org.netbeans.jcode.core.util.AttributeType.STRING_FQN;
import org.netbeans.jpa.modeler.settings.code.CodePanel;
import org.netbeans.jpa.modeler.spec.extend.annotation.Annotation;
import org.netbeans.jpa.modeler.spec.jaxb.JaxbVariableType;
import org.netbeans.jpa.modeler.spec.jaxb.JaxbVariableTypeHandler;
import org.netbeans.jpa.modeler.spec.jaxb.JaxbXmlAttribute;
import org.netbeans.jpa.modeler.spec.jaxb.JaxbXmlElement;
import org.netbeans.jpa.modeler.spec.validation.constraints.AssertFalse;
import org.netbeans.jpa.modeler.spec.validation.constraints.AssertTrue;
import org.netbeans.jpa.modeler.spec.validation.constraints.Constraint;
import org.netbeans.jpa.modeler.spec.validation.constraints.DecimalMax;
import org.netbeans.jpa.modeler.spec.validation.constraints.DecimalMin;
import org.netbeans.jpa.modeler.spec.validation.constraints.Digits;
import org.netbeans.jpa.modeler.spec.validation.constraints.Future;
import org.netbeans.jpa.modeler.spec.validation.constraints.Max;
import org.netbeans.jpa.modeler.spec.validation.constraints.Min;
import org.netbeans.jpa.modeler.spec.validation.constraints.NotNull;
import org.netbeans.jpa.modeler.spec.validation.constraints.Null;
import org.netbeans.jpa.modeler.spec.validation.constraints.Past;
import org.netbeans.jpa.modeler.spec.validation.constraints.Pattern;
import org.netbeans.jpa.modeler.spec.validation.constraints.Size;
import org.netbeans.jpa.source.JavaSourceParserUtil;
import org.netbeans.modeler.core.NBModelerUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author Gaurav Gupta
 */
public abstract class Attribute extends FlowPin implements JaxbVariableTypeHandler {

    @XmlElement(name = "an")
    private List<Annotation> annotation;
    @XmlAttribute(name = "v")
    private boolean visibile = true;
    @XmlElement(name = "des")
    private String description;

    @XmlAttribute(name = "xvt", required = true)//(name = "jaxb-variable-type", required = true)
    private JaxbVariableType jaxbVariableType;
    @XmlElement(name = "xa")//(name = "jaxb-xml-attribute")
    private JaxbXmlAttribute jaxbXmlAttribute;
    @XmlElement(name = "xe")//(name = "jaxb-xml-element")
    private JaxbXmlElement jaxbXmlElement;
    @XmlElement(name = "xe")//(name = "jaxb-xml-element")
    @XmlElementWrapper(name = "xel")//(name = "jaxb-xml-element-list")
    private List<JaxbXmlElement> jaxbXmlElementList;
//    @XmlAttribute(name = "jaxb-xml-list")
//    private Boolean jaxbXmlList;

    @XmlAttribute(name = "name", required = true)
    protected String name;

    @XmlAttribute(name = "dv")
    protected String defaultValue;

    @XmlAttribute(name = "ui")
    private Boolean includeInUI;

    @XmlAttribute(name = "ft")
    private Boolean functionalType;

    @XmlElement(name = "snp")
    private List<AttributeSnippet> snippets;
    
    @XmlAttribute(name = "pc")
    private Boolean propertyChangeSupport;

    @XmlAttribute(name = "vc")
    private Boolean vetoableChangeSupport;

    @XmlElementWrapper(name = "bv")
    @XmlElements({
        @XmlElement(name = "nu", type = Null.class)
        ,
        @XmlElement(name = "nn", type = NotNull.class)
        ,
        @XmlElement(name = "af", type = AssertFalse.class)
        ,
        @XmlElement(name = "at", type = AssertTrue.class)
        ,
        @XmlElement(name = "pa", type = Past.class)
        ,
        @XmlElement(name = "fu", type = Future.class)
        ,
        @XmlElement(name = "si", type = Size.class)
        ,
        @XmlElement(name = "pt", type = Pattern.class)
        ,
        @XmlElement(name = "mi", type = Min.class)
        ,
        @XmlElement(name = "ma", type = Max.class)
        ,
        @XmlElement(name = "dmi", type = DecimalMin.class)
        ,
        @XmlElement(name = "dma", type = DecimalMax.class)
        ,
        @XmlElement(name = "di", type = Digits.class)
    })
    private Set<Constraint> constraints = CONSTRAINTS_SUPPLIER.get();

    public final static Map<Class<? extends Constraint>, Integer> ALL_CONSTRAINTS = getAllConstraintsClass(); //Applicable Constraint template for datatype
    public final static Supplier<Set<Constraint>> CONSTRAINTS_SUPPLIER = () -> new TreeSet<>((a, b) -> ALL_CONSTRAINTS.getOrDefault(a.getClass(), 0).compareTo(ALL_CONSTRAINTS.getOrDefault(b.getClass(), 0)));

    protected void loadAttribute(Element element, VariableElement variableElement, ExecutableElement getterElement) {
        this.setId(NBModelerUtil.getAutoGeneratedStringId());
        this.name = variableElement.getSimpleName().toString();
        this.setAnnotation(JavaSourceParserUtil.getNonEEAnnotation(element));
        this.setFunctionalType(getterElement.getReturnType().toString().startsWith(Optional.class.getCanonicalName()));
    }

    /**
     * Gets the value of the name property.
     *
     * @return possible object is {@link String }
     *
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is {@link String }
     *
     */
    @Override
    public void setName(String value) {
        this.name = value;
    }

    /**
     * @return the annotation
     */
    public List<Annotation> getAnnotation() {
        if (annotation == null) {
            annotation = new ArrayList<Annotation>();
        }
        return annotation;
    }

    /**
     * @param annotation the annotation to set
     */
    public void setAnnotation(List<Annotation> annotation) {
        this.annotation = annotation;
    }

    public void addAnnotation(Annotation annotation_In) {
        if (annotation == null) {
            annotation = new ArrayList<>();
        }
        this.annotation.add(annotation_In);
    }

    public void removeAnnotation(Annotation annotation_In) {
        if (annotation == null) {
            annotation = new ArrayList<>();
        }
        this.annotation.remove(annotation_In);
    }

    /**
     * @return the visibile
     */
    public boolean isVisibile() {
        return visibile;
    }

    /**
     * @param visibile the visibile to set
     */
    public void setVisibile(boolean visibile) {
        this.visibile = visibile;
    }

    /**
     * @return the jaxbVariableType
     */
    @Override
    public JaxbVariableType getJaxbVariableType() {
        return jaxbVariableType;
    }

    /**
     * @param jaxbVariableType the jaxbVariableType to set
     */
    @Override
    public void setJaxbVariableType(JaxbVariableType jaxbVariableType) {
        this.jaxbVariableType = jaxbVariableType;
    }

    /**
     * @return the jaxbXmlAttribute
     */
    @Override
    public JaxbXmlAttribute getJaxbXmlAttribute() {
        return jaxbXmlAttribute;
    }

    /**
     * @param jaxbXmlAttribute the jaxbXmlAttribute to set
     */
    @Override
    public void setJaxbXmlAttribute(JaxbXmlAttribute jaxbXmlAttribute) {
        this.jaxbXmlAttribute = jaxbXmlAttribute;
    }

    /**
     * @return the jaxbXmlElement
     */
    @Override
    public JaxbXmlElement getJaxbXmlElement() {
        return jaxbXmlElement;
    }

    /**
     * @param jaxbXmlElement the jaxbXmlElement to set
     */
    @Override
    public void setJaxbXmlElement(JaxbXmlElement jaxbXmlElement) {
        this.jaxbXmlElement = jaxbXmlElement;
    }

    /**
     * @return the jaxbXmlElementList
     */
    @Override
    public List<JaxbXmlElement> getJaxbXmlElementList() {
        return jaxbXmlElementList;
    }

    /**
     * @param jaxbXmlElementList the jaxbXmlElementList to set
     */
    @Override
    public void setJaxbXmlElementList(List<JaxbXmlElement> jaxbXmlElementList) {
        this.jaxbXmlElementList = jaxbXmlElementList;
    }

//    /**
//     * @return the jaxbXmlList
//     */
//    public Boolean getJaxbXmlList() {
//        return jaxbXmlList;
//    }
//
//    /**
//     * @param jaxbXmlList the jaxbXmlList to set
//     */
//    public void setJaxbXmlList(Boolean jaxbXmlList) {
//        this.jaxbXmlList = jaxbXmlList;
//    }
    @Override
    public List<JaxbVariableType> getJaxbVariableList() {
        return Arrays.asList(JaxbVariableType.values());
    }

    @XmlTransient
    private BaseAttributes attributes;

    public JavaClass getJavaClass() {
        return attributes.getJavaClass();
    }

    public void setAttributes(BaseAttributes attributes) {
        this.attributes = attributes;
    }

    void afterUnmarshal(Unmarshaller u, Object parent) {
        if (parent instanceof BaseAttributes) {
            setAttributes((BaseAttributes) parent);
        }
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Used to get data type title to display in ui component e.g Set<String>,
     * Integer, List<Entity> etc.
     *
     */
    public abstract String getDataTypeLabel();

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Attribute other = (Attribute) obj;
        return Objects.equals(this.id, other.id);
    }

    /**
     * @return the includeInUI
     */
    public Boolean getIncludeInUI() {
        if (includeInUI == null) {
            return true;
        }
        return includeInUI;
    }

    /**
     * @param includeInUI the includeInUI to set
     */
    public void setIncludeInUI(Boolean includeInUI) {
        this.includeInUI = includeInUI;
    }

    public boolean isTextAttributeType(String attributeType) {
        return STRING.equals(attributeType) || STRING_FQN.equals(attributeType);
    }

    public boolean isPrecisionAttributeType(String attributeType) {
        if (null != attributeType) {
            switch (attributeType) {
                case BYTE:
                case BYTE_WRAPPER:
                    return true;
                case SHORT:
                case SHORT_WRAPPER:
                    return true;
                case INT:
                case INT_WRAPPER:
                    return true;
                case LONG:
                case LONG_WRAPPER:
                    return true;
                case FLOAT:
                case FLOAT_WRAPPER:
                    return true;
                case DOUBLE:
                case DOUBLE_WRAPPER:
                    return true;
                case BIGINTEGER:
                case BIGDECIMAL:
                    return true;
                default:
                    break;
            }
        }

        return false;
    }

    public boolean isScaleAttributeType(String attributeType) {
        if (FLOAT.equals(attributeType) || FLOAT_WRAPPER.equals(attributeType)) {
            return true;
        } else if (DOUBLE.equals(attributeType) || DOUBLE_WRAPPER.equals(attributeType)) {
            return true;
        } else if (BIGDECIMAL.equals(attributeType)) {
            return true;
        }
        return false;
    }

    public static boolean isMapType(String collectionType) {
        boolean valid = false;
        try {
            if (collectionType != null && !collectionType.trim().isEmpty()) {
                if (java.util.Map.class.isAssignableFrom(Class.forName(collectionType.trim()))) {
                    valid = true;
                }
            }
        } catch (ClassNotFoundException ex) {
            //skip allow = false;
        }
        return valid;
    }

    /**
     * @return the functionalType
     */
    public Boolean getFunctionalType() {
        if (functionalType == null) {
            return CodePanel.isOptionalReturnType();
        }
        return functionalType;
    }

    public boolean isOptionalReturnType() {
        return  getFunctionalType();
    }

    /**
     * @param functionalType the functionalType to set
     */
    public void setFunctionalType(Boolean functionalType) {
        this.functionalType = functionalType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @XmlTransient
    private Map<String, Constraint> constraintsMap;
    @XmlTransient
    private String constraintsDataTypeBinding;

    public Map<String, Constraint> getConstraintsMap() {
        if (constraintsMap == null || !getDataTypeLabel().equals(constraintsDataTypeBinding)) {
            Map<String, Constraint> completeConstraintsMap = getConstraints().stream().collect(Collectors.toMap(c -> c.getClass().getSimpleName(), c -> c, (c1, c2) -> c1));
            Set<Class<? extends Constraint>> allConstraintsClass = getAllConstraintsClass().keySet();
            Set<Class<? extends Constraint>> allowedConstraintsClass = getConstraintsClass();
            constraintsMap = allowedConstraintsClass.stream().collect(Collectors.toMap(c -> c.getSimpleName(), c -> completeConstraintsMap.get(c.getSimpleName()), (c1, c2) -> c1));
            //after datatype change , clearConstraint/disable the non-applicable constraints
            if (constraintsDataTypeBinding != null) {
                allConstraintsClass.removeAll(annotation);
                Set<Class<? extends Constraint>> skipConstraintsClass = allConstraintsClass;
                skipConstraintsClass.stream().map(c -> completeConstraintsMap.get(c.getSimpleName())).forEach(Constraint::clear);
            }
            constraintsDataTypeBinding = getDataTypeLabel();
        }
        return constraintsMap;
    }

    /**
     * @return the complete list of Constraint (old datatype Constraint instance
     * and new created Constraint instance)
     */
    private void bootAllConstraints() {
        Set<Class<? extends Constraint>> existingConstraints = constraints.stream().map(c -> c.getClass()).collect(toSet());
        for (Class<? extends Constraint> constraintClass : ALL_CONSTRAINTS.keySet()) {
            if (!existingConstraints.contains(constraintClass)) {
                try {
                    constraints.add(constraintClass.newInstance());
                } catch (InstantiationException | IllegalAccessException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    /**
     * Complete list of constraint class based
     */
    private static Map<Class<? extends Constraint>, Integer> getAllConstraintsClass() {
        Map<Class<? extends Constraint>, Integer> classes = new HashMap<>();
        classes.put(Null.class, 1);
        classes.put(NotNull.class, 2);
        classes.put(AssertFalse.class, 3);
        classes.put(AssertTrue.class, 4);
        classes.put(Past.class, 5);
        classes.put(Future.class, 6);
        classes.put(Size.class, 7);
        classes.put(Pattern.class, 8);
        classes.put(Min.class, 9);
        classes.put(Max.class, 10);
        classes.put(DecimalMin.class, 11);
        classes.put(DecimalMax.class, 12);
        classes.put(Digits.class, 13);
        return classes;
    }

    /**
     * Filtered constraint class based on data type
     */
    public Set<Class<? extends Constraint>> getConstraintsClass() {
//      DecimalMin/DecimalMax,Digits > BigDecimal,BigInteger,String,byte,short,int,long | Min/Max > BigDecimal,BigInteger,byte,short,int,long
//      Size > String,Collection,Map,Array | Past/Future > java.util.Date,java.util.Calendar | attern > String
        Set<Class<? extends Constraint>> classes = new LinkedHashSet<>();
        classes.add(NotNull.class);
        classes.add(Null.class);
        return classes;
    }

    /**
     * @return the constraints
     */
    public Set<Constraint> getConstraints() {
        if (constraints == null) {
            constraints = CONSTRAINTS_SUPPLIER.get();
        }
        if (ALL_CONSTRAINTS.size() != constraints.size()) {
            bootAllConstraints();
        }
        return constraints;
    }

    /**
     * @param constraints the constraints to set
     */
    public void setConstraints(Set<Constraint> constraints) {
        this.constraintsMap = null;//reset
        this.constraints = constraints;
    }

    /**
     * @return the snippets
     */
    public List<AttributeSnippet> getSnippets() {
        if (snippets == null) {
            snippets = new ArrayList<>();
        }
        return snippets;
    }

    /**
     * @param snippets the snippets to set
     */
    public void setSnippets(List<AttributeSnippet> snippets) {
        this.snippets = snippets;
    }

    public boolean addSnippet(AttributeSnippet snippet) {
        return getSnippets().add(snippet);
    }

    public boolean removeSnippet(AttributeSnippet snippet) {
        return getSnippets().remove(snippet);
    }

        /**
     * @return the propertyChangeSupport
     */
    public Boolean getPropertyChangeSupport() {
        if(!CodePanel.isJavaSESupportEnable() && (propertyChangeSupport == null || propertyChangeSupport == false)){
            return null;
        }
//        if (propertyChangeSupport == null) {
//            return true;
//        }
        return propertyChangeSupport;
    }

    /**
     * @param propertyChangeSupport the propertyChangeSupport to set
     */
    public void setPropertyChangeSupport(Boolean propertyChangeSupport) {
        this.propertyChangeSupport = propertyChangeSupport;
    }

    /**
     * @return the vetoableChangeSupport
     */
    public Boolean getVetoableChangeSupport() {
        if(!CodePanel.isJavaSESupportEnable() && (vetoableChangeSupport == null || vetoableChangeSupport == false)){
            return null;
        }
        return vetoableChangeSupport;
    }

    /**
     * @param vetoableChangeSupport the vetoableChangeSupport to set
     */
    public void setVetoableChangeSupport(Boolean vetoableChangeSupport) {
        this.vetoableChangeSupport = vetoableChangeSupport;
    }

}
