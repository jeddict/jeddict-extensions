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
package org.netbeans.jcode.angular2;

import java.util.prefs.Preferences;
import org.apache.commons.lang.StringUtils;
import static org.apache.commons.lang.StringUtils.EMPTY;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.jcode.ng.main.AngularData;
import org.netbeans.jcode.ng.main.PaginationType;
import static org.netbeans.jcode.core.util.StringHelper.firstLower;
import static org.netbeans.jcode.core.util.StringHelper.kebabCase;
import static org.netbeans.jcode.core.util.StringHelper.startCase;
import org.netbeans.jcode.stack.config.panel.*;
import org.netbeans.jcode.util.PreferenceUtils;
import org.netbeans.modeler.properties.entity.custom.editor.combobox.client.entity.ComboBoxValue;
import org.openide.util.NbBundle;

/**
 *
 * @author Gaurav Gupta
 */
public class Angular2Panel extends LayerConfigPanel<AngularData> {

    private Preferences pref;

    public Angular2Panel() {
        initComponents();
    }

    @Override
    public boolean hasError() {
        warningLabel.setText("");
        setModule(getModule().replaceAll("[^a-zA-Z0-9]+", EMPTY));
        if (StringUtils.isBlank(getModule())) {
            warningLabel.setText(NbBundle.getMessage(Angular2Panel.class, "AngularPanel.invalidModule.message"));
            return true;
        }
        if (StringUtils.isBlank(getApplicationTitle())) {
            warningLabel.setText(NbBundle.getMessage(Angular2Panel.class, "AngularPanel.invalidTitle.message"));
            return true;
        }
        return false;
    }


    @Override
    public void read() {
        this.setConfigData(PreferenceUtils.get(pref, AngularData.class));
        AngularData data = this.getConfigData();
        if (StringUtils.isNotBlank(data.getModule())) {
            setModule(data.getModule());
        }
        if (StringUtils.isNotBlank(data.getApplicationTitle())) {
            setApplicationTitle(data.getApplicationTitle());
        }
        
        if (data.getPagination()!=null) {
            setPaginationType(data.getPagination());
        }
        setProtractorTest(data.isProtractorTest());
    }

    @Override
    public void store() {
        this.getConfigData().setModule(getModule());
        this.getConfigData().setApplicationTitle(getApplicationTitle());
        this.getConfigData().setPagination(getPaginationType());
        this.getConfigData().setProtractorTest(isProtractorTest());
        PreferenceUtils.set(pref, this.getConfigData());
    }

    private Project project;

    @Override
    public void init(String folder, Project project, SourceGroup sourceGroup) {
        pref = ProjectUtils.getPreferences(project, AngularData.class, true);
        this.project = project;
        
        setModule(kebabCase(firstLower(project.getProjectDirectory().getName())));
        setApplicationTitle(startCase(project.getProjectDirectory().getName()));
        
        paginationComboBox.removeAllItems();
        for (PaginationType pagination : PaginationType.values()) {
            paginationComboBox.addItem(new ComboBoxValue(pagination, pagination.getTitle()));
        }

    }
    
    private void setPaginationType(PaginationType paginationType) {
        if (paginationType == null) {
            paginationComboBox.setSelectedIndex(0);
        } else {
            for (int i = 0; i < paginationComboBox.getItemCount(); i++) {
                if (((ComboBoxValue<PaginationType>) paginationComboBox.getItemAt(i)).getValue() == paginationType) {
                    paginationComboBox.setSelectedIndex(i);
                }
            }
        }
    }
    
    private PaginationType getPaginationType(){
        return ((ComboBoxValue<PaginationType>) paginationComboBox.getSelectedItem()).getValue();
    }



    public String getModule() {
        return angularModuleTextField.getText().trim();
    }
    private void setModule(String module) {
        angularModuleTextField.setText(module);
    }
    public String getApplicationTitle() {
        return appTitleTextField.getText().trim();
    }
    private void setApplicationTitle(String module) {
        appTitleTextField.setText(module);
    }
    
    public boolean isProtractorTest() {
        return protractorTest_CheckBox.isSelected();
    }
    
    private void setProtractorTest(boolean test) {
        protractorTest_CheckBox.setSelected(test);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        warningPanel = new javax.swing.JPanel();
        warningLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        wrapperPanel2 = new javax.swing.JPanel();
        angularModuleLabel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        angularModuleTextField = new javax.swing.JTextField();
        wrapperPanel1 = new javax.swing.JPanel();
        appTitleLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        titleStartLabel = new javax.swing.JLabel();
        appTitleTextField = new javax.swing.JTextField();
        titleEndLabel = new javax.swing.JLabel();
        wrapperPanel3 = new javax.swing.JPanel();
        paginationLabel = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        paginationComboBox = new javax.swing.JComboBox();
        wrapperPanel4 = new javax.swing.JPanel();
        protractorTest_CheckBox = new javax.swing.JCheckBox();

        warningPanel.setLayout(new java.awt.BorderLayout(10, 0));

        warningLabel.setForeground(new java.awt.Color(200, 0, 0));
        warningLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(warningLabel, org.openide.util.NbBundle.getMessage(Angular2Panel.class, "Angular2Panel.warningLabel.text")); // NOI18N
        warningPanel.add(warningLabel, java.awt.BorderLayout.CENTER);

        jPanel1.setPreferredSize(new java.awt.Dimension(217, 120));
        jPanel1.setLayout(new java.awt.GridLayout(4, 0, 0, 15));

        wrapperPanel2.setLayout(new java.awt.BorderLayout(10, 0));

        org.openide.awt.Mnemonics.setLocalizedText(angularModuleLabel, org.openide.util.NbBundle.getMessage(Angular2Panel.class, "Angular2Panel.angularModuleLabel.text")); // NOI18N
        wrapperPanel2.add(angularModuleLabel, java.awt.BorderLayout.LINE_START);

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        angularModuleTextField.setText(org.openide.util.NbBundle.getMessage(Angular2Panel.class, "Angular2Panel.angularModuleTextField.text")); // NOI18N
        angularModuleTextField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                angularModuleTextFieldPropertyChange(evt);
            }
        });
        jPanel3.add(angularModuleTextField);

        wrapperPanel2.add(jPanel3, java.awt.BorderLayout.CENTER);

        jPanel1.add(wrapperPanel2);

        wrapperPanel1.setLayout(new java.awt.BorderLayout(10, 0));

        org.openide.awt.Mnemonics.setLocalizedText(appTitleLabel, org.openide.util.NbBundle.getMessage(Angular2Panel.class, "Angular2Panel.appTitleLabel.text")); // NOI18N
        wrapperPanel1.add(appTitleLabel, java.awt.BorderLayout.LINE_START);

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        titleStartLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(titleStartLabel, org.openide.util.NbBundle.getMessage(Angular2Panel.class, "Angular2Panel.titleStartLabel.text")); // NOI18N
        titleStartLabel.setPreferredSize(new java.awt.Dimension(45, 14));
        jPanel2.add(titleStartLabel);

        appTitleTextField.setText(org.openide.util.NbBundle.getMessage(Angular2Panel.class, "Angular2Panel.appTitleTextField.text")); // NOI18N
        appTitleTextField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                appTitleTextFieldPropertyChange(evt);
            }
        });
        jPanel2.add(appTitleTextField);

        titleEndLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(titleEndLabel, org.openide.util.NbBundle.getMessage(Angular2Panel.class, "Angular2Panel.titleEndLabel.text")); // NOI18N
        titleEndLabel.setPreferredSize(new java.awt.Dimension(45, 14));
        jPanel2.add(titleEndLabel);

        wrapperPanel1.add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel1.add(wrapperPanel1);

        wrapperPanel3.setLayout(new java.awt.BorderLayout(10, 0));

        org.openide.awt.Mnemonics.setLocalizedText(paginationLabel, org.openide.util.NbBundle.getMessage(Angular2Panel.class, "Angular2Panel.paginationLabel.text")); // NOI18N
        paginationLabel.setPreferredSize(new java.awt.Dimension(80, 14));
        wrapperPanel3.add(paginationLabel, java.awt.BorderLayout.LINE_START);

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.LINE_AXIS));

        jPanel4.add(paginationComboBox);

        wrapperPanel3.add(jPanel4, java.awt.BorderLayout.CENTER);

        jPanel1.add(wrapperPanel3);

        org.openide.awt.Mnemonics.setLocalizedText(protractorTest_CheckBox, org.openide.util.NbBundle.getMessage(Angular2Panel.class, "Angular2Panel.protractorTest_CheckBox.text")); // NOI18N

        javax.swing.GroupLayout wrapperPanel4Layout = new javax.swing.GroupLayout(wrapperPanel4);
        wrapperPanel4.setLayout(wrapperPanel4Layout);
        wrapperPanel4Layout.setHorizontalGroup(
            wrapperPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, wrapperPanel4Layout.createSequentialGroup()
                .addGap(86, 86, 86)
                .addComponent(protractorTest_CheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(294, 294, 294))
        );
        wrapperPanel4Layout.setVerticalGroup(
            wrapperPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(protractorTest_CheckBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel1.add(wrapperPanel4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(warningPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(183, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(289, Short.MAX_VALUE)
                    .addComponent(warningPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void appTitleTextFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_appTitleTextFieldPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_appTitleTextFieldPropertyChange

    private void angularModuleTextFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_angularModuleTextFieldPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_angularModuleTextFieldPropertyChange

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel angularModuleLabel;
    private javax.swing.JTextField angularModuleTextField;
    private javax.swing.JLabel appTitleLabel;
    private javax.swing.JTextField appTitleTextField;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JComboBox paginationComboBox;
    private javax.swing.JLabel paginationLabel;
    private javax.swing.JCheckBox protractorTest_CheckBox;
    private javax.swing.JLabel titleEndLabel;
    private javax.swing.JLabel titleStartLabel;
    private javax.swing.JLabel warningLabel;
    private javax.swing.JPanel warningPanel;
    private javax.swing.JPanel wrapperPanel1;
    private javax.swing.JPanel wrapperPanel2;
    private javax.swing.JPanel wrapperPanel3;
    private javax.swing.JPanel wrapperPanel4;
    // End of variables declaration//GEN-END:variables


}
