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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import javax.lang.model.element.TypeElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import org.netbeans.jpa.modeler.spec.EntityMappings;

@XmlAccessorType(XmlAccessType.FIELD)
public class PlainAttributes extends Attributes<PlainClass> {

    @XmlElement(name = "attr")
    private List<PlainAttribute> attributes;
        
    @Override
    public List<Attribute> getAllAttribute(boolean includeParentClassAttibute) {
        List<Attribute> attributes = super.getAllAttribute(includeParentClassAttibute);
        attributes.addAll(this.getAttributes());
        return attributes;
    }
    
    @Override
    public List<Attribute> findAllAttribute(String name, boolean includeParentClassAttibute) {
        List<Attribute> attributes = super.findAllAttribute(name, includeParentClassAttibute);

        for (PlainAttribute defaultAttribute : getAttributes()) {
            if (defaultAttribute.getName() != null && defaultAttribute.getName().equals(name)) {
                attributes.add(defaultAttribute);
            }
        }
        return attributes;
    }
    /**
     * @return the defaultAttributes
     */
    public List<PlainAttribute> getAttributes() {
        if (this.attributes == null) {
            this.attributes = new ArrayList<>();
        }
        return attributes;
    }

    /**
     * @param attributes the defaultAttributes to set
     */
    public void setAttributes(List<PlainAttribute> attributes) {
        this.attributes = attributes;
    }

    public void addAttribute(PlainAttribute attribute) {
        getAttributes().add(attribute);
        attribute.setAttributes(this);
    }

    public void removeAttribute(PlainAttribute attribute) {
        getAttributes().remove(attribute);
        attribute.setAttributes(null);
    }

    @Override
    public void load(EntityMappings entityMappings, TypeElement element, boolean fieldAccess) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.attributes);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PlainAttributes other = (PlainAttributes) obj;
        if (!Objects.equals(new HashSet(this.attributes), new HashSet(other.attributes))) {
            return false;
        }
        return true;
    }

    
    
}
