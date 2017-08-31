/**
 * Copyright [2016] Gaurav Gupta
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "class", propOrder = {
    "attributes"
})
public class PlainClass extends JavaClass<PlainAttributes> {

    @XmlElement(name = "attrs")
    private PlainAttributes attributes;

    public PlainClass() {
    }

    public PlainClass(String clazz) {
        this.clazz = clazz;
    }

    @Override
    public PlainAttributes getAttributes() {
        if (attributes == null) {
            attributes = new PlainAttributes();
            attributes.setJavaClass(this);
        }
        return attributes;
    }

    @Override
    public void setAttributes(PlainAttributes attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getName() {
        return clazz;
    }

    @Override
    public void setName(String name) {
        this.clazz = clazz;
    }

}
