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
import java.util.Collections;
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
import org.apache.commons.lang.StringUtils;
import static org.netbeans.jcode.core.util.AttributeType.DOUBLE;
import static org.netbeans.jcode.core.util.AttributeType.DOUBLE_WRAPPER;
import static org.netbeans.jcode.core.util.AttributeType.FLOAT;
import static org.netbeans.jcode.core.util.AttributeType.FLOAT_WRAPPER;
import static org.netbeans.jcode.core.util.AttributeType.STRING_FQN;
import org.netbeans.jpa.modeler.settings.code.CodePanel;
import org.netbeans.jpa.modeler.spec.jaxb.JaxbVariableType;
import org.netbeans.jpa.modeler.spec.jaxb.JaxbVariableTypeHandler;
import org.netbeans.jpa.modeler.spec.jaxb.JaxbXmlAttribute;
import org.netbeans.jpa.modeler.spec.jaxb.JaxbXmlElement;
import org.netbeans.bean.validation.constraints.AssertFalse;
import org.netbeans.bean.validation.constraints.AssertTrue;
import org.netbeans.bean.validation.constraints.Constraint;
import org.netbeans.bean.validation.constraints.DecimalMax;
import org.netbeans.bean.validation.constraints.DecimalMin;
import org.netbeans.bean.validation.constraints.Digits;
import org.netbeans.bean.validation.constraints.Future;
import org.netbeans.bean.validation.constraints.Max;
import org.netbeans.bean.validation.constraints.Min;
import org.netbeans.bean.validation.constraints.NotNull;
import org.netbeans.bean.validation.constraints.Null;
import org.netbeans.bean.validation.constraints.Past;
import org.netbeans.bean.validation.constraints.Pattern;
import org.netbeans.bean.validation.constraints.Size;
import static org.netbeans.jcode.core.util.AttributeType.BIGDECIMAL;
import static org.netbeans.jcode.core.util.AttributeType.BIGINTEGER;
import static org.netbeans.jcode.core.util.AttributeType.BOOLEAN;
import static org.netbeans.jcode.core.util.AttributeType.BOOLEAN_WRAPPER;
import static org.netbeans.jcode.core.util.AttributeType.BYTE;
import static org.netbeans.jcode.core.util.AttributeType.BYTE_WRAPPER;
import static org.netbeans.jcode.core.util.AttributeType.CALENDAR;
import static org.netbeans.jcode.core.util.AttributeType.DATE;
import static org.netbeans.jcode.core.util.AttributeType.HIJRAH_DATE;
import static org.netbeans.jcode.core.util.AttributeType.INSTANT;
import static org.netbeans.jcode.core.util.AttributeType.INT;
import static org.netbeans.jcode.core.util.AttributeType.INT_WRAPPER;
import static org.netbeans.jcode.core.util.AttributeType.JAPANESE_DATE;
import static org.netbeans.jcode.core.util.AttributeType.LOCAL_DATE;
import static org.netbeans.jcode.core.util.AttributeType.LOCAL_DATE_TIME;
import static org.netbeans.jcode.core.util.AttributeType.LOCAL_TIME;
import static org.netbeans.jcode.core.util.AttributeType.LONG;
import static org.netbeans.jcode.core.util.AttributeType.LONG_WRAPPER;
import static org.netbeans.jcode.core.util.AttributeType.MINGUO_DATE;
import static org.netbeans.jcode.core.util.AttributeType.MONTH_DAY;
import static org.netbeans.jcode.core.util.AttributeType.OFFSET_DATE_TIME;
import static org.netbeans.jcode.core.util.AttributeType.OFFSET_TIME;
import static org.netbeans.jcode.core.util.AttributeType.SHORT;
import static org.netbeans.jcode.core.util.AttributeType.SHORT_WRAPPER;
import static org.netbeans.jcode.core.util.AttributeType.STRING;
import static org.netbeans.jcode.core.util.AttributeType.THAI_BUDDHIST_DATE;
import static org.netbeans.jcode.core.util.AttributeType.YEAR;
import static org.netbeans.jcode.core.util.AttributeType.YEAR_MONTH;
import static org.netbeans.jcode.core.util.AttributeType.ZONED_DATE_TIME;
import static org.netbeans.jcode.core.util.AttributeType.isArray;
import org.netbeans.jpa.modeler.spec.jsonb.JsonbDateFormat;
import org.netbeans.jpa.modeler.spec.jsonb.JsonbNumberFormat;
import org.netbeans.jpa.modeler.spec.jsonb.JsonbTypeHandler;
import org.netbeans.jpa.source.JavaSourceParserUtil;
import org.netbeans.modeler.core.NBModelerUtil;
import org.netbeans.modeler.properties.type.Embedded;
import org.openide.util.Exceptions;

/**
 *
 * @author Gaurav Gupta
 */
public abstract class Attribute extends FlowPin implements JaxbVariableTypeHandler, 
        JsonbTypeHandler, Embedded {

    @XmlElement(name = "an")
    private List<AttributeAnnotation> annotation;
    @XmlTransient
    private List<AttributeAnnotation> runtimeAnnotation;
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

    @XmlTransient
    private List<AttributeSnippet> runtimeSnippets;

    @XmlAttribute(name = "pc")
    private Boolean propertyChangeSupport;

    @XmlAttribute(name = "vc")
    private Boolean vetoableChangeSupport;

    
    //Jsonb support start
    
    @XmlAttribute(name = "jbn")
    private Boolean jsonbNillable;

    @XmlAttribute(name = "jbt")
    private Boolean jsonbTransient ;

    @XmlAttribute(name = "jbp")
    private String jsonbProperty;

    @XmlElement(name = "jbta")
    private ReferenceClass jsonbTypeAdapter;
    
    @XmlElement(name = "jbdf")
    private JsonbDateFormat jsonbDateFormat;
        
    @XmlElement(name = "jbnf")
    private JsonbNumberFormat jsonbNumberFormat;   
    
    @XmlElement(name = "jbtd")
    private ReferenceClass jsonbTypeDeserializer;
        
    @XmlElement(name = "jbts")
    private ReferenceClass jsonbTypeSerializer ;
    
    //Jsonb support end
    
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
    private Set<Constraint> attributeConstraints = CONSTRAINTS_SUPPLIER.get();

    @XmlTransient
    private Map<String, Constraint> attributeConstraintsMap;
    @XmlTransient
    private String attributeConstraintsDataTypeBinding;

    @XmlElementWrapper(name = "kbv")
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
    private Set<Constraint> keyConstraints = CONSTRAINTS_SUPPLIER.get();

    @XmlTransient
    private Map<String, Constraint> keyConstraintsMap;
    @XmlTransient
    private String keyConstraintsDataTypeBinding;

    @XmlElementWrapper(name = "vbv")
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
    private Set<Constraint> valueConstraints = CONSTRAINTS_SUPPLIER.get();

    @XmlTransient
    private Map<String, Constraint> valueConstraintsMap;
    @XmlTransient
    private String valueConstraintsDataTypeBinding;

    public final static Map<Class<? extends Constraint>, Integer> ALL_CONSTRAINTS = getAllConstraintsClass(); //Applicable Constraint template for datatype
    public final static Supplier<Set<Constraint>> CONSTRAINTS_SUPPLIER = () -> new TreeSet<>((a, b) -> ALL_CONSTRAINTS.getOrDefault(a.getClass(), 0).compareTo(ALL_CONSTRAINTS.getOrDefault(b.getClass(), 0)));

    protected void loadAttribute(Element element, VariableElement variableElement, ExecutableElement getterElement) {
        this.setId(NBModelerUtil.getAutoGeneratedStringId());
        this.name = variableElement.getSimpleName().toString();
        this.setAnnotation(JavaSourceParserUtil.getNonEEAnnotation(element, AttributeAnnotation.class));
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
    public List<AttributeAnnotation> getAnnotation() {
        if (annotation == null) {
            annotation = new ArrayList<>();
        }
        return annotation;
    }

    /**
     * @param annotation the annotation to set
     */
    public void setAnnotation(List<AttributeAnnotation> annotation) {
        this.annotation = annotation;
    }

    public void addAnnotation(AttributeAnnotation annotation) {
        getAnnotation().add(annotation);
    }

    public void removeAnnotation(AttributeAnnotation annotation) {
        getAnnotation().remove(annotation);
    }

    /**
     * @return the runtimeAnnotation
     */
    public List<AttributeAnnotation> getRuntimeAnnotation() {
        if (runtimeAnnotation == null) {
            runtimeAnnotation = new ArrayList<>();
        }
        return runtimeAnnotation;
    }

    /**
     * @param runtimeAnnotation the runtimeAnnotation to set
     */
    public void setRuntimeAnnotation(List<AttributeAnnotation> runtimeAnnotation) {
        this.runtimeAnnotation = runtimeAnnotation;
    }

    public void addRuntimeAnnotation(AttributeAnnotation runtimeAnnotation) {
        getRuntimeAnnotation().add(runtimeAnnotation);
    }

    public void removeRuntimeAnnotation(AttributeAnnotation runtimeAnnotation) {
        getRuntimeAnnotation().remove(runtimeAnnotation);
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

    @Override
    public List<JaxbVariableType> getJaxbVariableList() {
        return Arrays.asList(JaxbVariableType.values());
    }

    @XmlTransient
    private IAttributes attributes;

    public JavaClass getJavaClass() {
        return attributes.getJavaClass();
    }

    public void setAttributes(IAttributes attributes) {
        this.attributes = attributes;
    }

    void afterUnmarshal(Unmarshaller u, Object parent) {
        if (parent instanceof IAttributes) {
            setAttributes((IAttributes) parent);//this
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
        return getFunctionalType();
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

    public Map<String, Constraint> getAttributeConstraintsMap() {
        if (attributeConstraintsMap == null || !Objects.equals(getDataTypeLabel(), attributeConstraintsDataTypeBinding)) {//Objects.equals used -> getDataTypeLabel() could be null incase of EmbeddedId
            Map<String, Constraint> completeConstraintsMap = getAttributeConstraints()
                    .stream()
                    .collect(Collectors.toMap(c -> c.getClass().getSimpleName(), c -> c, (c1, c2) -> c1));
            Set<Class<? extends Constraint>> allConstraintsClass = getAllConstraintsClass().keySet();
            Set<Class<? extends Constraint>> allowedConstraintsClass = getAttributeConstraintsClass();
            attributeConstraintsMap = allowedConstraintsClass
                    .stream()
                    .collect(Collectors.toMap(c -> c.getSimpleName(), c -> completeConstraintsMap.get(c.getSimpleName()), (c1, c2) -> c1));
            attributeConstraintsDataTypeBinding = cleanUnusedConstraint(attributeConstraintsDataTypeBinding, allConstraintsClass, completeConstraintsMap);
        }
        return attributeConstraintsMap;
    }

    public Map<String, Constraint> getKeyConstraintsMap() {
        if (keyConstraintsMap == null || !Objects.equals(getDataTypeLabel(), keyConstraintsDataTypeBinding)) {//Objects.equals used -> getDataTypeLabel() could be null incase of EmbeddedId
            Map<String, Constraint> completeConstraintsMap = getKeyConstraints()
                    .stream()
                    .collect(Collectors.toMap(c -> c.getClass().getSimpleName(), c -> c, (c1, c2) -> c1));
            Set<Class<? extends Constraint>> allConstraintsClass = getAllConstraintsClass().keySet();
            Set<Class<? extends Constraint>> allowedConstraintsClass = getKeyConstraintsClass();
            keyConstraintsMap = allowedConstraintsClass
                    .stream()
                    .collect(Collectors.toMap(c -> c.getSimpleName(), c -> completeConstraintsMap.get(c.getSimpleName()), (c1, c2) -> c1));
            keyConstraintsDataTypeBinding = cleanUnusedConstraint(keyConstraintsDataTypeBinding, allConstraintsClass, completeConstraintsMap);
        }
        return keyConstraintsMap;
    }

    public Map<String, Constraint> getValueConstraintsMap() {
        if (valueConstraintsMap == null || !Objects.equals(getDataTypeLabel(), valueConstraintsDataTypeBinding)) {//Objects.equals used -> getDataTypeLabel() could be null incase of EmbeddedId
            Map<String, Constraint> completeConstraintsMap = getValueConstraints()
                    .stream()
                    .collect(Collectors.toMap(c -> c.getClass().getSimpleName(), c -> c, (c1, c2) -> c1));
            Set<Class<? extends Constraint>> allConstraintsClass = getAllConstraintsClass().keySet();
            Set<Class<? extends Constraint>> allowedConstraintsClass = getValueConstraintsClass();
            valueConstraintsMap = allowedConstraintsClass
                    .stream()
                    .collect(Collectors.toMap(c -> c.getSimpleName(), c -> completeConstraintsMap.get(c.getSimpleName()), (c1, c2) -> c1));
            valueConstraintsDataTypeBinding = cleanUnusedConstraint(valueConstraintsDataTypeBinding, allConstraintsClass, completeConstraintsMap);
        }
        return valueConstraintsMap;
    }

    private String cleanUnusedConstraint(String constraintsDataTypeBinding, Set<Class<? extends Constraint>> allConstraintsClass, Map<String, Constraint> completeConstraintsMap) {
        //after datatype change , clearConstraint/disable the non-applicable constraints
        if (constraintsDataTypeBinding != null) {
            allConstraintsClass//todo only non-visible
                    .stream()
                    .map(c -> completeConstraintsMap.get(c.getSimpleName()))
                    .forEach(Constraint::clear);
        }
        constraintsDataTypeBinding = getDataTypeLabel();
        return constraintsDataTypeBinding;
    }

    /**
     * @return the complete list of Constraint (old datatype Constraint instance
     * and new created Constraint instance)
     */
    private void bootAllConstraints(Set<Constraint> constraints) {
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

    public Set<Class<? extends Constraint>> getAttributeConstraintsClass() {        
         return Collections.emptySet();
     }
     
     
     public Set<Class<? extends Constraint>> getKeyConstraintsClass() {
         return Collections.emptySet();
     }
     
     
     public Set<Class<? extends Constraint>> getValueConstraintsClass() {
         return Collections.emptySet();
     }
     
     
     
    /**
     * Filtered constraint class based on data type
     *
     * @return
     */
    protected Set<Class<? extends Constraint>> getConstraintsClass(String attribute) {
        Set<Class<? extends Constraint>> classes = new LinkedHashSet<>();
        classes.add(NotNull.class);
        classes.add(Null.class);
        if (StringUtils.isNotBlank(attribute)) {
            switch (attribute) {
                case STRING:
                case STRING_FQN:
                    classes.add(Size.class);//array, collection, map pending
                    classes.add(Pattern.class);
                    classes.add(DecimalMin.class);
                    classes.add(DecimalMax.class);
                    classes.add(Digits.class);
                    break;
                case BIGDECIMAL:
                case BIGINTEGER:
                case BYTE:
                case SHORT:
                case INT:
                case LONG:
                case BYTE_WRAPPER:
                case SHORT_WRAPPER:
                case INT_WRAPPER:
                case LONG_WRAPPER:
                    classes.add(Min.class);
                    classes.add(Max.class);
                    classes.add(DecimalMin.class);
                    classes.add(DecimalMax.class);
                    classes.add(Digits.class);
                    break;
                case BOOLEAN:
                case BOOLEAN_WRAPPER:
                    classes.add(AssertTrue.class);
                    classes.add(AssertFalse.class);
                    break;
                case CALENDAR:
                case DATE:
                case INSTANT:
                case LOCAL_DATE:
                case LOCAL_DATE_TIME:
                case LOCAL_TIME:
                case MONTH_DAY:
                case OFFSET_DATE_TIME:
                case OFFSET_TIME:
                case YEAR:
                case YEAR_MONTH:
                case ZONED_DATE_TIME:
                case HIJRAH_DATE:
                case JAPANESE_DATE:
                case MINGUO_DATE:
                case THAI_BUDDHIST_DATE:
                    classes.add(Past.class);
                    classes.add(Future.class);
                    break;
                default:
                    if(isArray(attribute)){
                        classes.add(Size.class);
                    } 
            }
        }
        return classes;
    }
    
    protected Set<Class<? extends Constraint>> getCollectionTypeConstraintsClass() {
        Set<Class<? extends Constraint>> classes = new LinkedHashSet<>();
        classes.add(NotNull.class);
        classes.add(Null.class);
        classes.add(Size.class);
        return classes;
    }
  
    /**
     * @param constraints
     * @return the constraints
     */
    public Set<Constraint> getAttributeConstraints() {
        if (attributeConstraints == null) {
            attributeConstraints = CONSTRAINTS_SUPPLIER.get();
        }
        if (ALL_CONSTRAINTS.size() != attributeConstraints.size()) {
            bootAllConstraints(attributeConstraints);
        }
        return attributeConstraints;
    }

    /**
     * @return the constraints
     */
    public Set<Constraint> getKeyConstraints() {
        if (keyConstraints == null) {
            keyConstraints = CONSTRAINTS_SUPPLIER.get();
        }
        if (ALL_CONSTRAINTS.size() != keyConstraints.size()) {
            bootAllConstraints(keyConstraints);
        }
        return keyConstraints;
    }

    /**
     * @return the constraints
     */
    public Set<Constraint> getValueConstraints() {
        if (valueConstraints == null) {
            valueConstraints = CONSTRAINTS_SUPPLIER.get();
        }
        if (ALL_CONSTRAINTS.size() != valueConstraints.size()) {
            bootAllConstraints(valueConstraints);
        }
        return valueConstraints;
    }

    /**
     * @param constraints the constraints to set
     */
    public void setAttributeConstraints(Set<Constraint> constraints) {
        this.attributeConstraintsMap = null;//reset
        this.attributeConstraints = constraints;
    }

    /**
     * @param keyConstraints the keyConstraints to set
     */
    public void setKeyConstraints(Set<Constraint> keyConstraints) {
        this.keyConstraintsMap = null;//reset
        this.keyConstraints = keyConstraints;
    }

    /**
     * @param valueConstraints the valueConstraints to set
     */
    public void setValueConstraints(Set<Constraint> valueConstraints) {
        this.valueConstraintsMap = null;//reset
        this.valueConstraints = valueConstraints;
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
     * @return the runtimeSnippets
     */
    public List<AttributeSnippet> getRuntimeSnippets() {
        if (runtimeSnippets == null) {
            runtimeSnippets = new ArrayList<>();
        }
        return runtimeSnippets;
    }

    /**
     * @param runtimeSnippets the runtimeSnippets to set
     */
    public void setRuntimeSnippets(List<AttributeSnippet> runtimeSnippets) {
        this.runtimeSnippets = runtimeSnippets;
    }

    public boolean addRuntimeSnippet(AttributeSnippet snippet) {
        return getRuntimeSnippets().add(snippet);
    }

    public boolean removeRuntimeSnippet(AttributeSnippet snippet) {
        return getRuntimeSnippets().remove(snippet);
    }

    /**
     * @return the propertyChangeSupport
     */
    public Boolean getPropertyChangeSupport() {
        if (!CodePanel.isJavaSESupportEnable() && (propertyChangeSupport == null || propertyChangeSupport == false)) {
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
        if (!CodePanel.isJavaSESupportEnable() && (vetoableChangeSupport == null || vetoableChangeSupport == false)) {
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

    /**
     * @return the jsonbNillable
     */
    public Boolean getJsonbNillable() {
        if (jsonbNillable == null) {
            return false;
        }
        return jsonbNillable;
    }

    /**
     * @param jsonbNillable the jsonbNillable to set
     */
    public void setJsonbNillable(Boolean jsonbNillable) {
        this.jsonbNillable = jsonbNillable;
    }

    /**
     * @return the jsonbTransient
     */
    public Boolean getJsonbTransient() {
        if (jsonbTransient == null) {
            return false;
        }
        return jsonbTransient;
    }

    /**
     * @param jsonbTransient the jsonbTransient to set
     */
    public void setJsonbTransient(Boolean jsonbTransient) {
        this.jsonbTransient = jsonbTransient;
    }

    /**
     * @return the jsonbProperty
     */
    public String getJsonbProperty() {
        return jsonbProperty;
    }

    /**
     * @param jsonbProperty the jsonbProperty to set
     */
    public void setJsonbProperty(String jsonbProperty) {
        this.jsonbProperty = jsonbProperty;
    }

    /**
     * @return the jsonbTypeAdapter
     */
    public ReferenceClass getJsonbTypeAdapter() {
        return jsonbTypeAdapter;
    }

    /**
     * @param jsonbTypeAdapter the jsonbTypeAdapter to set
     */
    public void setJsonbTypeAdapter(ReferenceClass jsonbTypeAdapter) {
        this.jsonbTypeAdapter = jsonbTypeAdapter;
    }

    /**
     * @return the jsonbDateFormat
     */
    public JsonbDateFormat getJsonbDateFormat() {
        if(jsonbDateFormat==null){
            jsonbDateFormat = new JsonbDateFormat();
        }
        return jsonbDateFormat;
    }

    /**
     * @param jsonbDateFormat the jsonbDateFormat to set
     */
    public void setJsonbDateFormat(JsonbDateFormat jsonbDateFormat) {
        this.jsonbDateFormat = jsonbDateFormat;
    }

    /**
     * @return the jsonbNumberFormat
     */
    public JsonbNumberFormat getJsonbNumberFormat() {
        if(jsonbNumberFormat==null){
            jsonbNumberFormat = new JsonbNumberFormat();
        }
        return jsonbNumberFormat;
    }

    /**
     * @param jsonbNumberFormat the jsonbNumberFormat to set
     */
    public void setJsonbNumberFormat(JsonbNumberFormat jsonbNumberFormat) {
        this.jsonbNumberFormat = jsonbNumberFormat;
    }

    /**
     * @return the jsonbTypeDeserializer
     */
    public ReferenceClass getJsonbTypeDeserializer() {
        return jsonbTypeDeserializer;
    }

    /**
     * @param jsonbTypeDeserializer the jsonbTypeDeserializer to set
     */
    public void setJsonbTypeDeserializer(ReferenceClass jsonbTypeDeserializer) {
        this.jsonbTypeDeserializer = jsonbTypeDeserializer;
    }

    /**
     * @return the JsonbTypeSerializer
     */
    public ReferenceClass getJsonbTypeSerializer() {
        return jsonbTypeSerializer;
    }

    /**
     * @param JsonbTypeSerializer the JsonbTypeSerializer to set
     */
    public void setJsonbTypeSerializer(ReferenceClass jsonbTypeSerializer) {
        this.jsonbTypeSerializer = jsonbTypeSerializer;
    }

}
