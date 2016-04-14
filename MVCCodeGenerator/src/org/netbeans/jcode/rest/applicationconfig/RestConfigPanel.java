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
package org.netbeans.jcode.rest.applicationconfig;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.lang.model.SourceVersion;
import javax.swing.ComboBoxModel;
import javax.swing.event.ChangeEvent;
import javax.swing.text.JTextComponent;
import org.apache.commons.lang.StringUtils;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.jcode.stack.config.panel.LayerConfigPanel;
import org.netbeans.modules.j2ee.core.api.support.java.JavaIdentifiers;
import org.netbeans.modules.websvc.rest.model.api.RestApplication;
import org.netbeans.spi.java.project.support.ui.PackageView;
import org.openide.util.NbBundle;

/**
 *
 * @author Gaurav Gupta
 */
public class RestConfigPanel extends LayerConfigPanel<RestConfigData> {

    private static final String DEFAULT_PACKAGE = "controller";
    private Map<String,RestApplication> restApplications = Collections.EMPTY_MAP;
    public RestConfigPanel() {
        initComponents();
    }
    
    @Override
    public boolean hasError() {
        warningLabel.setText("");
        
        String _package = getPackage();
        
        System.out.println("Called : " + _package);

        if (!JavaIdentifiers.isValidPackageName(_package)) {
            warningLabel.setText(NbBundle.getMessage(RestConfigPanel.class, "RestConfigDialog.invalidPackage.message"));
            return true;
        }
        String restClass = getRestClass();
       
        if (!SourceVersion.isName(restClass)) {
            warningLabel.setText(NbBundle.getMessage(RestConfigPanel.class, "RestConfigDialog.invalidClassName.message"));
            return true;
        }
        
        String restPath = getRestPath();
       
        if (StringUtils.isBlank(restPath)) {
            warningLabel.setText(NbBundle.getMessage(RestConfigPanel.class, "RestConfigDialog.invalidPath.message"));
            return true;
        }
        
        
        
        RestApplication restApplication = restApplications.get(restPath);
         if (restApplication !=null && !restApplication.getApplicationClass().equals(_package + "." +restClass)) {
            warningLabel.setText(NbBundle.getMessage(RestConfigPanel.class, "RestConfigDialog.alreadyExist.message", restApplication.getApplicationClass()));
            return true;
        }
        return false;
    }
    
    
    @Override
    public void stateChanged(ChangeEvent e) {
        hasError();
    }
    
    @Override
    public void read(){
    
    }
    @Override
    public void store(){
        this.getConfigData().setApplicationClass(getRestClass());
        this.getConfigData().setApplicationPath(getRestPath());
        this.getConfigData().setPackage(getPackage());
    }
    
    @Override
    public void init(String _package,Project project, SourceGroup sourceGroup) {
        this.setConfigData(new RestConfigData());
        if (sourceGroup != null) {
            packageCombo.setRenderer(PackageView.listRenderer());
            ComboBoxModel model = PackageView.createListView(sourceGroup);
            if (model.getSize() > 0) {
                model.setSelectedItem(model.getElementAt(0));
            }
            packageCombo.setModel(model);
            addChangeListener(packageCombo);
            if (StringUtils.isBlank(_package)) {
                setPackage(DEFAULT_PACKAGE);
            } else {
                setPackage(_package + '.' + DEFAULT_PACKAGE);
            }
        }
        addChangeListener(restConfigClassField);
        addChangeListener(restPathField);
        
    }


    public String getPackage() {
        return ((JTextComponent) packageCombo.getEditor().getEditorComponent()).getText().trim();
    }

    private void setPackage(String _package) {
        ComboBoxModel model = packageCombo.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            if (model.getElementAt(i).toString().equals(_package)) {
                model.setSelectedItem(model.getElementAt(i));
                return;
            }
        }
        ((JTextComponent) packageCombo.getEditor().getEditorComponent()).setText(_package);
    }

    
   
    
     public String getRestClass() {
        return restConfigClassField.getText().trim();
    }
     
       public String getRestPath() {
        return restPathField.getText().trim();
    }

    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        packagePanel = new javax.swing.JPanel();
        packageLabel = new javax.swing.JLabel();
        packageCombo = new javax.swing.JComboBox();
        restPathPanel = new javax.swing.JPanel();
        restPathLabel = new javax.swing.JLabel();
        restPathField = new javax.swing.JTextField();
        restConfigClassPanel = new javax.swing.JPanel();
        restConfigClassLabel = new javax.swing.JLabel();
        restConfigClassField = new javax.swing.JTextField();
        warningPanel = new javax.swing.JPanel();
        warningLabel = new javax.swing.JLabel();

        packagePanel.setLayout(new java.awt.BorderLayout(10, 0));

        packageLabel.setLabelFor(packageCombo);
        org.openide.awt.Mnemonics.setLocalizedText(packageLabel, org.openide.util.NbBundle.getMessage(RestConfigPanel.class, "RestConfigPanel.packageLabel.text")); // NOI18N
        packageLabel.setPreferredSize(new java.awt.Dimension(100, 17));
        packagePanel.add(packageLabel, java.awt.BorderLayout.LINE_START);

        packageCombo.setEditable(true);
        packageCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " " }));
        packageCombo.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                packageComboPropertyChange(evt);
            }
        });
        packagePanel.add(packageCombo, java.awt.BorderLayout.CENTER);

        restPathPanel.setLayout(new java.awt.BorderLayout(10, 0));

        org.openide.awt.Mnemonics.setLocalizedText(restPathLabel, org.openide.util.NbBundle.getMessage(RestConfigPanel.class, "RestConfigPanel.restPathLabel.text")); // NOI18N
        restPathLabel.setPreferredSize(new java.awt.Dimension(100, 17));
        restPathPanel.add(restPathLabel, java.awt.BorderLayout.WEST);

        restPathField.setText(org.openide.util.NbBundle.getMessage(RestConfigPanel.class, "RestConfigPanel.restPathField.text")); // NOI18N
        restPathPanel.add(restPathField, java.awt.BorderLayout.CENTER);

        restConfigClassPanel.setLayout(new java.awt.BorderLayout(10, 0));

        org.openide.awt.Mnemonics.setLocalizedText(restConfigClassLabel, org.openide.util.NbBundle.getMessage(RestConfigPanel.class, "RestConfigPanel.restConfigClassLabel.text")); // NOI18N
        restConfigClassLabel.setPreferredSize(new java.awt.Dimension(100, 17));
        restConfigClassPanel.add(restConfigClassLabel, java.awt.BorderLayout.WEST);

        restConfigClassField.setText(org.openide.util.NbBundle.getMessage(RestConfigPanel.class, "RestConfigPanel.restConfigClassField.text")); // NOI18N
        restConfigClassPanel.add(restConfigClassField, java.awt.BorderLayout.CENTER);

        warningPanel.setLayout(new java.awt.BorderLayout(10, 0));

        warningLabel.setForeground(new java.awt.Color(200, 0, 0));
        warningLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(warningLabel, org.openide.util.NbBundle.getMessage(RestConfigPanel.class, "RestConfigPanel.warningLabel.text")); // NOI18N
        warningPanel.add(warningLabel, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(restPathPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE)
                    .addComponent(packagePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE)
                    .addComponent(restConfigClassPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(warningPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(restPathPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(restConfigClassPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(packagePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(53, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(137, Short.MAX_VALUE)
                    .addComponent(warningPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void packageComboPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_packageComboPropertyChange
        fire();
    }//GEN-LAST:event_packageComboPropertyChange
      
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox packageCombo;
    private javax.swing.JLabel packageLabel;
    private javax.swing.JPanel packagePanel;
    private javax.swing.JTextField restConfigClassField;
    private javax.swing.JLabel restConfigClassLabel;
    private javax.swing.JPanel restConfigClassPanel;
    private javax.swing.JTextField restPathField;
    private javax.swing.JLabel restPathLabel;
    private javax.swing.JPanel restPathPanel;
    private javax.swing.JLabel warningLabel;
    private javax.swing.JPanel warningPanel;
    // End of variables declaration//GEN-END:variables

    /**
     * @param restApplicationClasses the restApplicationClasses to set
     */
    public void setRestApplicationClasses(List<RestApplication> restApplicationList) {
        restApplications = new HashMap<>();
        restApplicationList.stream().forEach((restApplication) -> {
            restApplications.put(restApplication.getApplicationPath(), restApplication);
        });
    }
}
