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

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.XMLAttributes;
import org.netbeans.jpa.modeler.spec.Basic;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.Embedded;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.ManyToMany;
import org.netbeans.jpa.modeler.spec.ManyToOne;
import org.netbeans.jpa.modeler.spec.OneToMany;
import org.netbeans.jpa.modeler.spec.OneToOne;
import org.netbeans.jpa.modeler.spec.Transient;

/**
 *
 * @author Gaurav_Gupta
 */
public interface IPersistenceAttributes extends IAttributes {

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
    List<Basic> getBasic();

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
    List<ElementCollection> getElementCollection();
    void setElementCollection(List<ElementCollection> elementCollection);

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
    List<Embedded> getEmbedded();
    void setEmbedded(List<Embedded> embedded);

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
    List<ManyToMany> getManyToMany();
    void setManyToMany(List<ManyToMany> manyToMany);

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
    List<ManyToOne> getManyToOne();
    void setManyToOne(List<ManyToOne> manyToOne);

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
    List<OneToMany> getOneToMany();
    void setOneToMany(List<OneToMany> oneToMany);

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
    List<OneToOne> getOneToOne();
    void setOneToOne(List<OneToOne> oneToOne);

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
    List<Transient> getTransient();

    void addBasic(Basic basic);

    void removeBasic(Basic basic);
    
    Optional<Basic> getBasic(String id);

    void addTransient(Transient _transient);

    void removeTransient(Transient _transient);
    
    Optional<Transient> getTransient(String id);
    
    void addEmbedded(Embedded embedded);

    void removeEmbedded(Embedded embedded);
    
    Optional<Embedded> getEmbedded(String id);

    void addElementCollection(ElementCollection elementCollection);

    void removeElementCollection(ElementCollection elementCollection);

    Optional<ElementCollection> getElementCollection(String id);
    
    void removeRelationAttribute(RelationAttribute relationAttribute);

    void addRelationAttribute(RelationAttribute relationAttribute);

    List<Attribute> getNonRelationAttributes();

    List<RelationAttribute> getRelationAttributes();
    
    Optional<RelationAttribute> getRelationAttribute(String id);
    
    Optional<ManyToMany> getManyToMany(String id);
    
    void addManyToMany(ManyToMany manyToMany);

    Optional<ManyToOne> getManyToOne(String id);
    
    void addManyToOne(ManyToOne manyToOne);

    Optional<OneToMany> getOneToMany(String id);
    
    void addOneToMany(OneToMany oneToMany);

    Optional<OneToOne> getOneToOne(String id);
    
    void addOneToOne(OneToOne oneToOne);
    
    Set<Entity> getRelationConnectedClassRef();
    
    Set<String> getAllConvert();
    
    List<Basic> getSuperBasic();
    
    XMLAttributes getAccessor();

    XMLAttributes getAccessor(boolean inherit);
    
    XMLAttributes updateAccessor(XMLAttributes attr, boolean inherit);

}
