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
package org.netbeans.jpa.modeler.spec.workspace;

import java.awt.Point;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.netbeans.jpa.modeler.spec.extend.JavaClass;

/**
 *
 * @author jGauravGupta
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlJavaTypeAdapter(value = WorkSpaceItemValidator.class)
public class WorkSpaceItem {

    @XmlAttribute(name = "ref")
    @XmlIDREF
    private JavaClass javaClass;

    @XmlAttribute(name = "x")
    private Integer x;
    @XmlAttribute(name = "y")
    private Integer y;

    public WorkSpaceItem() {
    }

    public WorkSpaceItem(JavaClass javaClass) {
        this.javaClass = javaClass;
    }
    
    public WorkSpaceItem(JavaClass javaClass, Integer x, Integer y) {
        this.javaClass = javaClass;
        this.x = x;
        this.y = y;
    }

    /**
     * @return the javaClass
     */
    public JavaClass getJavaClass() {
        return javaClass;
    }

    /**
     * @param javaClass the javaClass to set
     */
    public void setJavaClass(JavaClass javaClass) {
        this.javaClass = javaClass;
    }

    /**
     * @return the x
     */
    public Integer getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(Integer x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public Integer getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(Integer y) {
        this.y = y;
    }

    public Point getLocation() {
        if (x == null || y == null) {
            return null;
        }
        return new Point(x, y);
    }
    
    public void setLocation(Point point) {
        if (point == null) {
            x = null;
            y = null;
        } else {
            x = point.x;
            y = point.y;
        }
    }

    @Override
    public int hashCode() {
        Integer hash = 7;
        if(this.javaClass!=null){
            hash = 37 * hash + Objects.hashCode(this.javaClass.getId());
        }
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
        final WorkSpaceItem other = (WorkSpaceItem) obj;
        if(this.javaClass == null || other.javaClass == null){
            return false;
        }
        if (!Objects.equals(this.javaClass.getId(), other.javaClass.getId())) {
            return false;
        }
        return true;
    }

    
}
