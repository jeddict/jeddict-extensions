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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import static java.util.function.Function.identity;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.commons.lang.StringUtils;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.XMLAttributes;
import org.netbeans.jcode.core.util.AttributeType.Type;
import static org.netbeans.jcode.core.util.AttributeType.Type.OTHER;
import static org.netbeans.jcode.core.util.AttributeType.getArrayType;
import static org.netbeans.jcode.core.util.AttributeType.getType;
import static org.netbeans.jcode.core.util.AttributeType.isArray;
import org.netbeans.jcode.core.util.JavaIdentifiers;
import org.netbeans.jpa.modeler.db.accessor.BasicSpecAccessor;
import org.netbeans.jpa.modeler.db.accessor.ElementCollectionSpecAccessor;
import org.netbeans.jpa.modeler.db.accessor.EmbeddedSpecAccessor;
import org.netbeans.jpa.modeler.db.accessor.ManyToManySpecAccessor;
import org.netbeans.jpa.modeler.db.accessor.ManyToOneSpecAccessor;
import org.netbeans.jpa.modeler.db.accessor.OneToManySpecAccessor;
import org.netbeans.jpa.modeler.db.accessor.OneToOneSpecAccessor;
import org.netbeans.jpa.modeler.spec.Basic;
import org.netbeans.jpa.modeler.spec.Convert;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.Embedded;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.ManagedClass;
import org.netbeans.jpa.modeler.spec.ManyToMany;
import org.netbeans.jpa.modeler.spec.ManyToOne;
import org.netbeans.jpa.modeler.spec.OneToMany;
import org.netbeans.jpa.modeler.spec.OneToOne;
import org.netbeans.jpa.modeler.spec.Transient;
import org.netbeans.jpa.modeler.spec.workspace.WorkSpace;

/**
 *
 * @author Shiwani Gupta <jShiwaniGupta@gmail.com>
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class PersistenceAttributes<T extends ManagedClass> extends Attributes<T> implements IPersistenceAttributes {

    @XmlElement(name = "basic")
    private List<Basic> basic;
    @XmlElement(name = "many-to-one")
    private List<ManyToOne> manyToOne;
    @XmlElement(name = "one-to-many")
    private List<OneToMany> oneToMany;
    @XmlElement(name = "one-to-one")
    private List<OneToOne> oneToOne;
    @XmlElement(name = "many-to-many")
    private List<ManyToMany> manyToMany;
    @XmlElement(name = "element-collection")
    private List<ElementCollection> elementCollection;
    @XmlElement(name = "embedded")
    private List<Embedded> embedded;
    @XmlElement(name = "transient")
    private List<Transient> _transient;

    /**
     * Gets the value of the basic property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the basic property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    addBasic(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link Basic }
     *
     *
     */
    @Override
    public List<Basic> getBasic() {
        if (basic == null) {
            setBasic(new ArrayList<>());
        }
        return this.basic;
    }
    
    public List<Basic> getSuperBasic(){
        List<Basic> superVersion = new ArrayList();
        JavaClass currentClass = getJavaClass();
        do {
            if(currentClass instanceof ManagedClass){
               ManagedClass<IPersistenceAttributes> managedClass = (ManagedClass)currentClass;
               superVersion.addAll(managedClass.getAttributes().getBasic());
            }
            currentClass = currentClass.getSuperclass();
        } while(currentClass != null);
        return superVersion;
    }

    public Optional<Basic> getBasic(String id) {
        if (basic != null) {
            return basic.stream().filter(a -> a.getId().equals(id)).findFirst();
        }
        return null;
    }

    @Override
    public void addBasic(Basic basic) {
        this.getBasic().add(basic);
        notifyListeners(basic, "addAttribute", null, null);
        basic.setAttributes(this);
    }

    @Override
    public void removeBasic(Basic basic) {
        this.getBasic().remove(basic);
        notifyListeners(basic, "removeAttribute", null, null);
    }

    /**
     * Gets the value of the manyToOne property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the manyToOne property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    addManyToOne(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ManyToOne }
     *
     *
     */
    @Override
    public List<ManyToOne> getManyToOne() {
        if (manyToOne == null) {
            setManyToOne(new ArrayList<>());
        }
        return this.manyToOne;
    }

    public Optional<ManyToOne> getManyToOne(String id) {
        if (manyToOne != null) {
            return manyToOne.stream().filter(a -> a.getId().equals(id)).findFirst();
        }
        return null;
    }

    public void addManyToOne(ManyToOne manyToOne) {
        getManyToOne().add(manyToOne);
        manyToOne.setAttributes(this);
    }

    public void removeManyToOne(ManyToOne manyToOne) {
        getManyToOne().remove(manyToOne);
    }

    /**
     * Gets the value of the oneToMany property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the oneToMany property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOneToMany().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OneToMany }
     *
     *
     */
    @Override
    public List<OneToMany> getOneToMany() {
        if (oneToMany == null) {
            setOneToMany(new ArrayList<>());
        }
        return this.oneToMany;
    }

    public Optional<OneToMany> getOneToMany(String id) {
        if (oneToMany != null) {
            return oneToMany.stream().filter(a -> a.getId().equals(id)).findFirst();
        }
        return null;
    }

    public void addOneToMany(OneToMany oneToMany) {
        getOneToMany().add(oneToMany);
        oneToMany.setAttributes(this);
    }

    /**
     * Gets the value of the oneToOne property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the oneToOne property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOneToOne().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OneToOne }
     *
     *
     */
    @Override
    public List<OneToOne> getOneToOne() {
        if (oneToOne == null) {
            setOneToOne(new ArrayList<>());
        }
        return this.oneToOne;
    }

    public void addOneToOne(OneToOne oneToOne) {
        getOneToOne().add(oneToOne);
        oneToOne.setAttributes(this);
    }

    public Optional<OneToOne> getOneToOne(String id) {
        if (oneToOne != null) {
            return oneToOne.stream().filter(a -> a.getId().equals(id)).findFirst();
        }
        return null;
    }

    /**
     * Gets the value of the manyToMany property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the manyToMany property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getManyToMany().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ManyToMany }
     *
     *
     */
    @Override
    public List<ManyToMany> getManyToMany() {
        if (manyToMany == null) {
            setManyToMany(new ArrayList<>());
        }
        return this.manyToMany;
    }

    public Optional<ManyToMany> getManyToMany(String id) {
        if (manyToMany != null) {
            return manyToMany.stream().filter(a -> a.getId().equals(id)).findFirst();
        }
        return null;
    }

    public void addManyToMany(ManyToMany manyToMany) {
        getManyToMany().add(manyToMany);
        manyToMany.setAttributes(this);
    }

    /**
     * Gets the value of the elementCollection property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the elementCollection property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getElementCollection().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ElementCollection }
     *
     *
     */
    @Override
    public List<ElementCollection> getElementCollection() {
        if (elementCollection == null) {
            setElementCollection(new ArrayList<>());
        }
        return this.elementCollection;
    }

    public Optional<ElementCollection> getElementCollection(String id) {
        if (elementCollection != null) {
            return elementCollection.stream().filter(a -> a.getId().equals(id)).findFirst();
        }
        return null;
    }

    @Override
    public void addElementCollection(ElementCollection elementCollection) {
        this.getElementCollection().add(elementCollection);
        notifyListeners(elementCollection, "addAttribute", null, null);
        elementCollection.setAttributes(this);
    }

    @Override
    public void removeElementCollection(ElementCollection elementCollection) {
        this.getElementCollection().remove(elementCollection);
        notifyListeners(elementCollection, "removeAttribute", null, null);
    }

    /**
     * Gets the value of the embedded property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the embedded property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEmbedded().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Embedded }
     *
     *
     */
    @Override
    public List<Embedded> getEmbedded() {
        if (embedded == null) {
            setEmbedded(new ArrayList<>());
        }
        return this.embedded;
    }

    public Optional<Embedded> getEmbedded(String id) {
        if (embedded != null) {
            return embedded.stream().filter(a -> a.getId().equals(id)).findFirst();
        }
        return null;
    }

    @Override
    public void addEmbedded(Embedded embedded) {
        this.getEmbedded().add(embedded);
        notifyListeners(embedded, "addAttribute", null, null);
        embedded.setAttributes(this);
    }

    @Override
    public void removeEmbedded(Embedded embedded) {
        this.getEmbedded().remove(embedded);
        notifyListeners(embedded, "removeAttribute", null, null);
    }

    /**
     * Gets the value of the transient property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the transient property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransient().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Transient }
     *
     *
     */
    @Override
    public List<Transient> getTransient() {
        if (_transient == null) {
            setTransient(new ArrayList<>());
        }
        return this._transient;
    }

    public Optional<Transient> getTransient(String id) {
        if (_transient != null) {
            return _transient.stream().filter(a -> a.getId().equals(id)).findFirst();
        }
        return null;
    }

    @Override
    public void addTransient(Transient _transient) {
        this.getTransient().add(_transient);
        notifyListeners(_transient, "addAttribute", null, null);
        _transient.setAttributes(this);
    }

    @Override
    public void removeTransient(Transient _transient) {
        this.getTransient().remove(_transient);
        notifyListeners(_transient, "removeAttribute", null, null);
    }

    @Override
    public List<RelationAttribute> getRelationAttributes() { 
        List<RelationAttribute> relationAttributes = new ArrayList<>(this.getOneToOne());
        relationAttributes.addAll(this.getManyToOne());
        relationAttributes.addAll(this.getOneToMany());
        relationAttributes.addAll(this.getManyToMany());
        return relationAttributes;
    }
    
    public List<SingleRelationAttribute> getDerivedRelationAttributes() {
        List<SingleRelationAttribute> relationAttributeWidget = new ArrayList<>();
        relationAttributeWidget.addAll(
                getOneToOne().stream()
                        .filter(SingleRelationAttribute::isPrimaryKey)
                        .collect(toList())
        );
        relationAttributeWidget.addAll(
                getManyToOne().stream()
                        .filter(SingleRelationAttribute::isPrimaryKey)
                        .collect(toList())
        );
        return relationAttributeWidget;
    }
    
    @Override
    public Set<String> getConnectedClass(Set<String> javaClasses){
        javaClasses.add(getJavaClass().getFQN());
        if(getJavaClass().getSuperclass()!=null && getJavaClass().getSuperclass() instanceof ManagedClass){
            javaClasses.addAll(((ManagedClass)getJavaClass().getSuperclass()).getAttributes().getConnectedClass(javaClasses));
        }
        javaClasses.addAll(getBasicConnectedClass(javaClasses));        
        javaClasses.addAll(getRelationConnectedClass(javaClasses));
        javaClasses.addAll(getEmbeddedConnectedClass(javaClasses));
        javaClasses.addAll(getElementCollectionConnectedClass(javaClasses));
        return javaClasses;
    }
    
    public Set<String> getRelationConnectedClass(Set<String> javaClasses){
        Map<ManagedClass, String> releationClasses = getRelationAttributes().stream()
                .map(RelationAttribute::getConnectedEntity)
                .distinct()
                .filter(jc -> !javaClasses.contains(jc.getFQN()))
                .collect(toMap(identity(), JavaClass::getFQN));
        javaClasses.addAll(releationClasses.values());
        for (ManagedClass releationClass : releationClasses.keySet()) {
            javaClasses.addAll(releationClass.getAttributes().getConnectedClass(javaClasses));
        }
        return javaClasses;
    }
    
    @Override
    public Set<Entity> getRelationConnectedClassRef() {
        Set<Entity> javaClasses = getRelationAttributes().stream()
                .map(RelationAttribute::getConnectedEntity)
                .collect(toSet());
        javaClasses.addAll(getEmbedded().stream()
                .map(Embedded::getConnectedClass)
                .flatMap(c -> c.getAttributes().getRelationConnectedClassRef().stream())
                .collect(toSet()));
        return javaClasses;
    }
    
    
    public Set<String> getEmbeddedConnectedClass(Set<String> javaClasses){
        Map<ManagedClass, String> releationClasses = getEmbedded().stream()
                .map(Embedded::getConnectedClass)
                .distinct()
                .filter(jc -> !javaClasses.contains(jc.getFQN()))
                .collect(toMap(identity(), JavaClass::getFQN));
        javaClasses.addAll(releationClasses.values());
        for (ManagedClass releationClass : releationClasses.keySet()) {
            javaClasses.addAll(releationClass.getAttributes().getConnectedClass(javaClasses));
        }
        return javaClasses;
    }
    
    public Set<String> getElementCollectionConnectedClass(Set<String> javaClasses) {
        Map<ManagedClass, String> elementCollectionClasses = getElementCollection().stream()
                .filter(ec -> ec.getConnectedClass() != null)
                .map(ElementCollection::getConnectedClass)
                .distinct()
                .filter(jc -> !javaClasses.contains(jc.getFQN()))
                .collect(toMap(identity(), JavaClass::getFQN));
        javaClasses.addAll(elementCollectionClasses.values());
        for (ManagedClass elementCollectionClass : elementCollectionClasses.keySet()) {
            javaClasses.addAll(elementCollectionClass.getAttributes().getConnectedClass(javaClasses));
        }
        return javaClasses;
    }

    public Set<String> getBasicConnectedClass(Set<String> javaClasses) {
        List<String> basicClasses = getBasic().stream()
                .map(Basic::getDataTypeLabel)
                .filter(dataType -> {
                    if (StringUtils.isNotEmpty(dataType)) {
                        dataType = isArray(dataType) ? getArrayType(dataType) : dataType;
                        Type type = getType(dataType);
                        if (type == OTHER) {
                            return !JavaIdentifiers.getPackageName(dataType).startsWith("java");
                        }
                    }
                    return false;
                })
                .distinct()
                .collect(Collectors.toList());
        javaClasses.addAll(basicClasses);
        return javaClasses;
    }
    
    public Optional<RelationAttribute> getRelationAttribute(String id) {
        return getRelationAttributes().stream().filter(a -> a.getId().equals(id)).findFirst();
    }


    @Override
    public void removeRelationAttribute(RelationAttribute relationAttribute) {
        if (relationAttribute instanceof ManyToMany) {
            this.getManyToMany().remove((ManyToMany) relationAttribute);
            notifyListeners(relationAttribute, "removeAttribute", null, null);
        } else if (relationAttribute instanceof OneToMany) {
            this.getOneToMany().remove((OneToMany) relationAttribute);
            notifyListeners(relationAttribute, "removeAttribute", null, null);
        } else if (relationAttribute instanceof ManyToOne) {
            this.getManyToOne().remove((ManyToOne) relationAttribute);
            notifyListeners(relationAttribute, "removeAttribute", null, null);
        } else if (relationAttribute instanceof OneToOne) {
            this.getOneToOne().remove((OneToOne) relationAttribute);
            notifyListeners(relationAttribute, "removeAttribute", null, null);
        } else {
            throw new IllegalStateException("Invalid Type Relation Attribute");
        }
    }

    @Override
    public void addRelationAttribute(RelationAttribute relationAttribute) {
        if (relationAttribute instanceof ManyToMany) {
            this.addManyToMany((ManyToMany) relationAttribute);
            notifyListeners(relationAttribute, "addAttribute", null, null);
        } else if (relationAttribute instanceof OneToMany) {
            this.addOneToMany((OneToMany) relationAttribute);
            notifyListeners(relationAttribute, "addAttribute", null, null);
        } else if (relationAttribute instanceof ManyToOne) {
            this.addManyToOne((ManyToOne) relationAttribute);
            notifyListeners(relationAttribute, "addAttribute", null, null);
        } else if (relationAttribute instanceof OneToOne) {
            this.addOneToOne((OneToOne) relationAttribute);
            notifyListeners(relationAttribute, "addAttribute", null, null);
        } else {
            throw new IllegalStateException("Invalid Type Relation Attribute");
        }
    }

    @Override
    public List<Attribute> getAllAttribute(boolean includeParentClassAttibute) {
        List<Attribute> attributes = super.getAllAttribute(includeParentClassAttibute);
        attributes.addAll(this.getBasic());
        attributes.addAll(this.getElementCollection());
        attributes.addAll(this.getEmbedded());
        attributes.addAll(this.getRelationAttributes());
        attributes.addAll(this.getTransient());
        return attributes;
    }
    
    @Override
    public boolean isAttributeExist(String name) {
        if (super.isAttributeExist(name)) {
            return true;
        }
        
        if (basic != null) {
            for (Basic basic_TMP : basic) {
                if (basic_TMP.getName() != null && basic_TMP.getName().equals(name)) {
                    return true;
                }
            }
        }
        if (_transient != null) {
            for (Transient transient_TMP : _transient) {
                if (transient_TMP.getName() != null && transient_TMP.getName().equals(name)) {
                    return true;
                }
            }
        }
        if (elementCollection != null) {
            for (ElementCollection elementCollection_TMP : elementCollection) {
                if (elementCollection_TMP.getName() != null && elementCollection_TMP.getName().equals(name)) {
                    return true;
                }
            }
        }
        if (embedded != null) {
            for (Embedded embedded_TMP : embedded) {
                if (embedded_TMP.getName() != null && embedded_TMP.getName().equals(name)) {
                    return true;
                }
            }
        }

        if (oneToOne != null) {
            for (OneToOne oneToOne_TMP : oneToOne) {
                if (oneToOne_TMP.getName() != null && oneToOne_TMP.getName().equals(name)) {
                    return true;
                }
            }
        }
        if (oneToMany != null) {
            for (OneToMany oneToMany_TMP : oneToMany) {
                if (oneToMany_TMP.getName() != null && oneToMany_TMP.getName().equals(name)) {
                    return true;
                }
            }
        }
        if (manyToOne != null) {
            for (ManyToOne manyToOne_TMP : manyToOne) {
                if (manyToOne_TMP.getName() != null && manyToOne_TMP.getName().equals(name)) {
                    return true;
                }
            }
        }
        if (manyToMany != null) {
            for (ManyToMany manyToMany_TMP : manyToMany) {
                if (manyToMany_TMP.getName() != null && manyToMany_TMP.getName().equals(name)) {
                    return true;
                }
            }
        }

        return false;
    }
    
    
    @Override
    public List<Attribute> findAllAttribute(String name,boolean includeParentClassAttibute) {
        List<Attribute> attributes = super.findAllAttribute(name,includeParentClassAttibute);

        if (basic != null) {
            for (Basic basic_TMP : basic) {
                if (basic_TMP.getName() != null && basic_TMP.getName().equals(name)) {
                    attributes.add(basic_TMP);
                }
            }
        }
        if (elementCollection != null) {
            for (ElementCollection elementCollection_TMP : elementCollection) {
                if (elementCollection_TMP.getName() != null && elementCollection_TMP.getName().equals(name)) {
                    attributes.add(elementCollection_TMP);
                }
            }
        }

        if (_transient != null) {
            for (Transient transient_TMP : _transient) {
                if (transient_TMP.getName() != null && transient_TMP.getName().equals(name)) {
                    attributes.add(transient_TMP);
                }
            }
        }
        if (oneToOne != null) {
            for (OneToOne oneToOne_TMP : oneToOne) {
                if (oneToOne_TMP.getName() != null && oneToOne_TMP.getName().equals(name)) {
                    attributes.add(oneToOne_TMP);
                }
            }
        }
        if (oneToMany != null) {
            for (OneToMany oneToMany_TMP : oneToMany) {
                if (oneToMany_TMP.getName() != null && oneToMany_TMP.getName().equals(name)) {
                    attributes.add(oneToMany_TMP);
                }
            }
        }
        if (manyToOne != null) {
            for (ManyToOne manyToOne_TMP : manyToOne) {
                if (manyToOne_TMP.getName() != null && manyToOne_TMP.getName().equals(name)) {
                    attributes.add(manyToOne_TMP);
                }
            }
        }
        if (manyToMany != null) {
            for (ManyToMany manyToMany_TMP : manyToMany) {
                if (manyToMany_TMP.getName() != null && manyToMany_TMP.getName().equals(name)) {
                    attributes.add(manyToMany_TMP);
                }
            }
        }
        if (embedded != null) {
            for (Embedded embedded_TMP : embedded) {
                if (embedded_TMP.getName() != null && embedded_TMP.getName().equals(name)) {
                    attributes.add(embedded_TMP);
                }
            }
        }

        return attributes;
    }

    public XMLAttributes getAccessor(WorkSpace workSpace) {
        XMLAttributes attr = new XMLAttributes();
        attr.setBasicCollections(new ArrayList<>());
        attr.setBasicMaps(new ArrayList<>());
        attr.setTransformations(new ArrayList<>());
        attr.setVariableOneToOnes(new ArrayList<>());
        attr.setStructures(new ArrayList<>());
        attr.setArrays(new ArrayList<>());
        attr.setBasics(new ArrayList<>());
        attr.setElementCollections(new ArrayList<>());
        attr.setEmbeddeds(new ArrayList<>());
        attr.setTransients(new ArrayList<>());
        attr.setManyToManys(new ArrayList<>());
        attr.setManyToOnes(new ArrayList<>());
        attr.setOneToManys(new ArrayList<>());
        attr.setOneToOnes(new ArrayList<>());
        return attr;
    }

    @Override
    public XMLAttributes updateAccessor(WorkSpace workSpace, XMLAttributes attr, boolean inherit) {
        attr.getBasics().addAll(getBasic()
                .stream()
                .map(bsc -> BasicSpecAccessor.getInstance(bsc, inherit))
                .collect(toList()));
        attr.getElementCollections().addAll(getElementCollection()
                .stream()
                .filter(ec -> workSpace==null || ec.getConnectedClass()== null || workSpace.hasItem(ec.getConnectedClass()))
                .map(ec -> ElementCollectionSpecAccessor.getInstance(ec, inherit))
                .collect(toList()));
        attr.getEmbeddeds().addAll(getEmbedded()
                .stream()
                .filter(emb -> workSpace==null || workSpace.hasItem(emb.getConnectedClass()))
                .map(emb -> EmbeddedSpecAccessor.getInstance(emb, inherit))
                .collect(toList()));        
//      Skip Transient
        attr.getManyToManys().addAll(getManyToMany()
                .stream()
                .filter(mtm -> workSpace==null || workSpace.hasItem(mtm.getConnectedEntity()))
                .map(mtm -> ManyToManySpecAccessor.getInstance(mtm, inherit))
                .collect(toList()));
        attr.getManyToOnes().addAll(getManyToOne()
                .stream()
                .filter(mto -> workSpace==null || workSpace.hasItem(mto.getConnectedEntity()))
                .map(mto -> ManyToOneSpecAccessor.getInstance(mto, inherit))
                .collect(toList()));
        attr.getOneToManys().addAll(getOneToMany()
                .stream()
                .filter(otm -> workSpace==null || workSpace.hasItem(otm.getConnectedEntity()))
                .map(otm -> OneToManySpecAccessor.getInstance(otm, inherit))
                .collect(toList()));
        attr.getOneToOnes().addAll(getOneToOne()
                .stream()
                .filter(oto -> workSpace==null || workSpace.hasItem(oto.getConnectedEntity()))
                .map(oto -> OneToOneSpecAccessor.getInstance(oto, inherit))
                .collect(toList()));
        return attr; 
    }

    /**
     * @param basic the basic to set
     */
    public void setBasic(List<Basic> basic) {
        this.basic = basic;
    }

    /**
     * @param manyToOne the manyToOne to set
     */
    @Override
    public void setManyToOne(List<ManyToOne> manyToOne) {
        this.manyToOne = manyToOne;
    }

    /**
     * @param oneToMany the oneToMany to set
     */
    @Override
    public void setOneToMany(List<OneToMany> oneToMany) {
        this.oneToMany = oneToMany;
    }

    /**
     * @param oneToOne the oneToOne to set
     */
    @Override
    public void setOneToOne(List<OneToOne> oneToOne) {
        this.oneToOne = oneToOne;
    }

    /**
     * @param manyToMany the manyToMany to set
     */
    @Override
    public void setManyToMany(List<ManyToMany> manyToMany) {
        this.manyToMany = manyToMany;
    }

    /**
     * @param elementCollection the elementCollection to set
     */
    @Override
    public void setElementCollection(List<ElementCollection> elementCollection) {
        this.elementCollection = elementCollection;
    }

    /**
     * @param embedded the embedded to set
     */
    public void setEmbedded(List<Embedded> embedded) {
        this.embedded = embedded;
    }

    /**
     * @param _transient the _transient to set
     */
    public void setTransient(List<Transient> _transient) {
        this._transient = _transient;
    }

    public List<Attribute> getNonRelationAttributes() {
        List<Attribute> attributes = new ArrayList<>(this.getBasic());
        attributes.addAll(this.getElementCollection().stream().filter(ec -> ec.getConnectedClass() == null).collect(toList()));
        return attributes;
    }

    public Set<String> getAllConvert(){
        Set<String> converts = new HashSet();
        for (Basic bc : getBasic()) {
            Convert convert = bc.getConvert();
            if(StringUtils.isNotBlank(convert.getConverter())){
                converts.add(convert.getConverter());
            }
        }
        for (ElementCollection ec : getElementCollection()) {
            converts.addAll(ec.getConverts().stream().filter(con -> StringUtils.isNotBlank(con.getConverter())).map(Convert::getConverter).collect(toSet()));
            converts.addAll(ec.getMapKeyConverts().stream().filter(con -> StringUtils.isNotBlank(con.getConverter())).map(Convert::getConverter).collect(toSet()));
        }
        for (Embedded ec : getEmbedded()) {
            converts.addAll(ec.getConverts().stream().filter(con -> con.getConverter() != null).map(Convert::getConverter).collect(toSet()));
        }
        for (OneToMany otm : getOneToMany()) {
            converts.addAll(otm.getMapKeyConverts().stream().filter(con -> con.getConverter() != null).map(Convert::getConverter).collect(toSet()));
        }
        for (ManyToMany mtm : getManyToMany()) {
            converts.addAll(mtm.getMapKeyConverts().stream().filter(con -> con.getConverter() != null).map(Convert::getConverter).collect(toSet()));
        }
        return converts;
    }
}
