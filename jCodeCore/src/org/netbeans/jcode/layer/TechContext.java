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
package org.netbeans.jcode.layer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.jcode.stack.config.panel.DefaultConfigPanel;
import org.netbeans.jcode.stack.config.panel.LayerConfigPanel;
import org.openide.util.Exceptions;

/**
 *
 * @author jGauravGupta <gaurav.gupta.jc@gmail.com>
 */
public class TechContext {

    private Generator generator;
    private Technology technology;
    private LayerConfigPanel panel;
    private List<TechContext> siblingTechContext;

    public TechContext(Generator generator, Technology technology) {
        this.generator = generator;
        this.technology = technology;
        this.siblingTechContext = Generator.getSiblingTechContexts(generator);
    }

    public TechContext(Generator generator) {
        this(generator, generator.getClass().getAnnotation(Technology.class));
    }

    public void createPanel(Project project, SourceGroup sourceGroup, String _package) {
        if (panel == null) {
            if (isValid()) {
                try {
                    panel = getTechnology().panel().newInstance();
                } catch (InstantiationException | IllegalAccessException ex) {
                    Exceptions.printStackTrace(ex);
                }
            } else {
                panel = new DefaultConfigPanel();
            }

            panel.init(_package, project, sourceGroup);
            panel.read();
            
            getSiblingTechContext().forEach(context -> context.createPanel(project, sourceGroup, _package));
        }
    }
    
      public LayerConfigPanel getPanel() {
        return panel;
    }

    /**
     * @return the generator
     */
    public Generator getGenerator() {
        return generator;
    }

    /**
     * @param generator the generator to set
     */
    public void setGenerator(Generator generator) {
        this.generator = generator;
        this.technology = generator.getClass().getAnnotation(Technology.class);
    }

    /**
     * @return the technology
     */
    public Technology getTechnology() {
        return technology;
    }

    /**
     * @param technology the technology to set
     */
    public void setTechnology(Technology technology) {
        this.technology = technology;
    }

    @Override
    public String toString() {
        return technology.label();
    }

    public boolean isValid() {
        return getTechnology().panel() != null && getTechnology().panel() != LayerConfigPanel.class;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this.technology.label());
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
        final TechContext other = (TechContext) obj;
        if (this.generator.getClass() != other.generator.getClass()) {
            return false;
        }
        return true;
    }

    public List<TechContext> getSiblingTechContext() {
        if (siblingTechContext == null) {
            siblingTechContext = new ArrayList<>();
        }
        return siblingTechContext;
    }

    public void setSiblingTechContext(List<TechContext> siblingTechContext) {
        this.siblingTechContext = siblingTechContext;
    }

    public boolean addSiblingTechContext(TechContext e) {
        return getSiblingTechContext().add(e);
    }

    public boolean removeSiblingTechContext(TechContext o) {
        return getSiblingTechContext().remove(o);
    }

}
