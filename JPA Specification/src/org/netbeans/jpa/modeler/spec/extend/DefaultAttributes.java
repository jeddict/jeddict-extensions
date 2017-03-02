/**
 * Copyright [2017] Gaurav Gupta
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
import java.util.List;
import javax.lang.model.element.TypeElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import org.netbeans.jpa.modeler.spec.DefaultAttribute;
import org.netbeans.jpa.modeler.spec.DefaultClass;
import org.netbeans.jpa.modeler.spec.EntityMappings;

/**
 *
 * @author Shiwani Gupta <jShiwaniGupta@gmail.com>
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class DefaultAttributes extends Attributes<DefaultClass> {

    @XmlElement(name = "attr")
    private List<DefaultAttribute> defaultAttributes;
        
    @Override
    public List<Attribute> getAllAttribute(boolean includeParentClassAttibute) {
        List<Attribute> attributes = super.getAllAttribute(includeParentClassAttibute);
        attributes.addAll(this.getDefaultAttributes());
        return attributes;
    }
    
    @Override
    public List<Attribute> findAllAttribute(String name, boolean includeParentClassAttibute) {
        List<Attribute> attributes = super.findAllAttribute(name, includeParentClassAttibute);

        for (DefaultAttribute defaultAttribute : getDefaultAttributes()) {
            if (defaultAttribute.getName() != null && defaultAttribute.getName().equals(name)) {
                attributes.add(defaultAttribute);
            }
        }
        return attributes;
    }
    /**
     * @return the defaultAttributes
     */
    public List<DefaultAttribute> getDefaultAttributes() {
        if (this.defaultAttributes == null) {
            this.defaultAttributes = new ArrayList<>();
        }
        return defaultAttributes;
    }

    /**
     * @param attributes the defaultAttributes to set
     */
    public void setDefaultAttributes(List<DefaultAttribute> attributes) {
        this.defaultAttributes = attributes;
    }

    public void addDefaultAttribute(DefaultAttribute attribute) {
        getDefaultAttributes().add(attribute);
//        attribute.setJavaClass(this.getJavaClass());
    }

    public void removeDefaultAttribute(DefaultAttribute attribute) {
        getDefaultAttributes().remove(attribute);
//        attribute.setJavaClass(null);
    }

    @Override
    public void load(EntityMappings entityMappings, TypeElement element, boolean fieldAccess) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
