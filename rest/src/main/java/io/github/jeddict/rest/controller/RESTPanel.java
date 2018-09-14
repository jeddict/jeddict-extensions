/**
 * Copyright 2013-2018 the original author or authors from the Jeddict project (https://jeddict.github.io/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.asecurityTypeCombopache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.jeddict.rest.controller;

import io.github.jeddict.jcode.LayerConfigPanel;
import static io.github.jeddict.jcode.util.JavaSourceHelper.isValidPackageName;
import io.github.jeddict.rest.applicationconfig.RestConfigData;
import io.github.jeddict.rest.applicationconfig.RestConfigDialog;
import static javax.lang.model.SourceVersion.isName;
import static javax.swing.JOptionPane.OK_OPTION;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.modeler.properties.spec.ComboBoxValue;
import static org.openide.util.NbBundle.getMessage;

/**
 *
 * @author Gaurav Gupta
 */
public class RESTPanel extends LayerConfigPanel<RESTData> {

    private static final String DEFAULT_PACKAGE = "controller";
    private RestConfigDialog configDialog;

    public RESTPanel() {
        initComponents();
        securityTypeLabel.setVisible(false);
        securityTypeCombo.setVisible(false);
        metricsCheckbox.setVisible(false);
        openAPICheckBox.setVisible(false);
        loggerCheckBox.setVisible(false);
    }

    @Override
    public boolean hasError() {
        warningLabel.setText("");
        if (!isValidPackageName(getPackage())) {
            warningLabel.setText(getMessage(RESTPanel.class, "RESTPanel.invalidRestPackage.message"));
            return true;
        }
        String prefix = getPrefix();
        String suffix = getSuffix();

        if (isNotBlank(prefix) && !isName(prefix)) {
            warningLabel.setText(getMessage(RESTPanel.class, "RESTPanel.invalidPrefix.message"));
            return true;
        }
        if (isNotBlank(suffix) && !isName(prefix + '_' + suffix)) {
            warningLabel.setText(getMessage(RESTPanel.class, "RESTPanel.invalidSuffix.message"));
            return true;
        }
        if ("Service".equals(suffix)) {
            warningLabel.setText(getMessage(RESTPanel.class, "RESTPanel.reservedSuffix.message"));
            return true;
        }
        return false;
    }

    @Override
    public void read() {
        RESTData data = this.getConfigData();
        if(isNotBlank(data.getPackage())){
            setPackage(data.getPackage());
        }
        
        if(isNotBlank(data.getPrefixName())){
            setPrefix(data.getPrefixName());
        }
        
        if(isNotBlank(data.getSuffixName())){
            setSuffix(data.getSuffixName());
        }
        
       if (data.getSecurityType()!=null) {
            setSecurityType(data.getSecurityType());
        }
        setLogger(data.isLogger());
        setMetrics(data.isMetrics());
        setOpenAPI(data.isOpenAPI());
        setTestCase(data.isTestCase());
    }

    @Override
    public void store() {
        RESTData data = this.getConfigData();
        data.setPrefixName(getPrefix());
        data.setSuffixName(getSuffix());
        data.setPackage(getPackage());
        if (data.getRestConfigData() == null ) {
            RestConfigData restConfigData = new RestConfigData();
            data.setRestConfigData(restConfigData);
        }
        data.setMetrics(isMetrics());
        data.setLogger(isLogger());
        data.setOpenAPI(isOpenAPI());
        data.setTestCase(isTestCase());
        data.setSecurityType(SecurityType.SECURITY_JWT);
    }

    private Project project;
    private SourceGroup sourceGroup;
    
    @Override
    public void init(String _package, Project project, SourceGroup sourceGroup) {
        this.project = project;
        this.sourceGroup = sourceGroup;
        setPackage(DEFAULT_PACKAGE);
        addChangeListener(prefixField);
        addChangeListener(suffixField);
        
        securityTypeCombo.removeAllItems();
        for (SecurityType securityType : SecurityType.values()) {
            securityTypeCombo.addItem(new ComboBoxValue(securityType, securityType.toString()));
        }
    }
    
    private void setSecurityType(SecurityType securityType) {
        if (securityType == null) {
            securityTypeCombo.setSelectedIndex(0);
        } else {
            for (int i = 0; i < securityTypeCombo.getItemCount(); i++) {
                if (((ComboBoxValue<SecurityType>) securityTypeCombo.getItemAt(i)).getValue() == securityType) {
                    securityTypeCombo.setSelectedIndex(i);
                }
            }
        }
    }
    
    private SecurityType getSecurityType(){
        return ((ComboBoxValue<SecurityType>) securityTypeCombo.getSelectedItem()).getValue();
    }
    
    public String getPackage() {
        return packageTextField.getText().trim();
    }

    private void setPackage(String _package) {
        packageTextField.setText(_package);
    }
        
    public String getSuffix() {
        return suffixField.getText().trim();
    }

    public String getPrefix() {
        return prefixField.getText().trim();
    }
    
    private void setPrefix(String prefix) {
        prefixField.setText(prefix);
    }
    
    private void setSuffix(String suffix) {
        suffixField.setText(suffix);
    }
    
    private boolean isMetrics() {
        return metricsCheckbox.isSelected();
    }

    private void setMetrics(boolean metrics) {
        metricsCheckbox.setSelected(metrics);
    }
    
   private boolean isTestCase() {
        return testcaseCheckBox.isSelected();
    }

    private void setTestCase(boolean testCase) {
        testcaseCheckBox.setSelected(testCase);
    }
    
    private boolean isOpenAPI() {
        return openAPICheckBox.isSelected();
    }

    private void setOpenAPI(boolean openAPI) {
        openAPICheckBox.setSelected(openAPI);
    }
   
    private boolean isLogger() {
        return loggerCheckBox.isSelected();
    }

    private void setLogger(boolean loggerEnable) {
        loggerCheckBox.setSelected(loggerEnable);
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
        suffixPanel = new javax.swing.JPanel();
        namePane = new javax.swing.JLayeredPane();
        prefixField = new javax.swing.JTextField();
        entityLabel = new javax.swing.JLabel();
        suffixField = new javax.swing.JTextField();
        nameLabel = new javax.swing.JLabel();
        packagePanel = new javax.swing.JPanel();
        packageLabel = new javax.swing.JLabel();
        packageWrapper = new javax.swing.JLayeredPane();
        packagePrefixLabel = new javax.swing.JLabel();
        packageTextField = new javax.swing.JTextField();
        appPanel = new javax.swing.JPanel();
        securityTypeLabel = new javax.swing.JLabel();
        securityTypeCombo = new javax.swing.JComboBox();
        applicationConfigButton = new javax.swing.JButton();
        miscPanel = new javax.swing.JPanel();
        testcaseCheckBox = new javax.swing.JCheckBox();
        metricsCheckbox = new javax.swing.JCheckBox();
        openAPICheckBox = new javax.swing.JCheckBox();
        loggerCheckBox = new javax.swing.JCheckBox();

        warningPanel.setLayout(new java.awt.BorderLayout(10, 0));

        warningLabel.setForeground(new java.awt.Color(200, 0, 0));
        warningLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(warningLabel, org.openide.util.NbBundle.getMessage(RESTPanel.class, "RESTPanel.warningLabel.text")); // NOI18N
        warningPanel.add(warningLabel, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.GridLayout(5, 0, 0, 10));

        suffixPanel.setPreferredSize(new java.awt.Dimension(200, 27));
        suffixPanel.setLayout(new java.awt.BorderLayout(10, 0));

        namePane.setPreferredSize(new java.awt.Dimension(150, 27));
        namePane.setLayout(new javax.swing.BoxLayout(namePane, javax.swing.BoxLayout.LINE_AXIS));

        prefixField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        prefixField.setToolTipText(org.openide.util.NbBundle.getMessage(RESTPanel.class, "RESTPanel.prefixField.toolTipText")); // NOI18N
        prefixField.setPreferredSize(new java.awt.Dimension(45, 27));
        prefixField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                prefixFieldPropertyChange(evt);
            }
        });
        namePane.add(prefixField);

        entityLabel.setForeground(javax.swing.UIManager.getDefaults().getColor("Button.shadow"));
        entityLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(entityLabel, org.openide.util.NbBundle.getMessage(RESTPanel.class, "RESTPanel.entityLabel.text")); // NOI18N
        entityLabel.setPreferredSize(new java.awt.Dimension(70, 27));
        entityLabel.setRequestFocusEnabled(false);
        namePane.add(entityLabel);

        suffixField.setText(org.openide.util.NbBundle.getMessage(RESTPanel.class, "RESTPanel.suffixField.text")); // NOI18N
        suffixField.setPreferredSize(new java.awt.Dimension(50, 27));
        suffixField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                suffixFieldPropertyChange(evt);
            }
        });
        namePane.add(suffixField);

        suffixPanel.add(namePane, java.awt.BorderLayout.CENTER);

        org.openide.awt.Mnemonics.setLocalizedText(nameLabel, org.openide.util.NbBundle.getMessage(RESTPanel.class, "RESTPanel.nameLabel.text")); // NOI18N
        nameLabel.setPreferredSize(new java.awt.Dimension(100, 17));
        suffixPanel.add(nameLabel, java.awt.BorderLayout.WEST);

        jPanel1.add(suffixPanel);

        packagePanel.setLayout(new java.awt.BorderLayout(10, 0));

        org.openide.awt.Mnemonics.setLocalizedText(packageLabel, org.openide.util.NbBundle.getMessage(RESTPanel.class, "RESTPanel.packageLabel.text")); // NOI18N
        packageLabel.setPreferredSize(new java.awt.Dimension(115, 17));
        packagePanel.add(packageLabel, java.awt.BorderLayout.LINE_START);

        packageWrapper.setLayout(new java.awt.BorderLayout());

        packagePrefixLabel.setForeground(new java.awt.Color(153, 153, 153));
        org.openide.awt.Mnemonics.setLocalizedText(packagePrefixLabel, org.openide.util.NbBundle.getMessage(RESTPanel.class, "RESTPanel.packagePrefixLabel.text")); // NOI18N
        packagePrefixLabel.setPreferredSize(new java.awt.Dimension(185, 14));
        packageWrapper.add(packagePrefixLabel, java.awt.BorderLayout.WEST);

        packageTextField.setText(org.openide.util.NbBundle.getMessage(RESTPanel.class, "RESTPanel.packageTextField.text")); // NOI18N
        packageWrapper.add(packageTextField, java.awt.BorderLayout.CENTER);

        packagePanel.add(packageWrapper, java.awt.BorderLayout.CENTER);

        jPanel1.add(packagePanel);

        org.openide.awt.Mnemonics.setLocalizedText(securityTypeLabel, org.openide.util.NbBundle.getMessage(RESTPanel.class, "RESTPanel.securityTypeLabel.text")); // NOI18N
        securityTypeLabel.setPreferredSize(new java.awt.Dimension(60, 17));

        securityTypeCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " " }));
        securityTypeCombo.setPreferredSize(new java.awt.Dimension(60, 27));

        org.openide.awt.Mnemonics.setLocalizedText(applicationConfigButton, org.openide.util.NbBundle.getMessage(RESTPanel.class, "RESTPanel.applicationConfigButton.text")); // NOI18N
        applicationConfigButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applicationConfigButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout appPanelLayout = new javax.swing.GroupLayout(appPanel);
        appPanel.setLayout(appPanelLayout);
        appPanelLayout.setHorizontalGroup(
            appPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(appPanelLayout.createSequentialGroup()
                .addComponent(securityTypeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(securityTypeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 270, Short.MAX_VALUE)
                .addComponent(applicationConfigButton))
        );
        appPanelLayout.setVerticalGroup(
            appPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(securityTypeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(securityTypeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(applicationConfigButton, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel1.add(appPanel);

        miscPanel.setLayout(new java.awt.GridLayout(1, 0));

        testcaseCheckBox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(testcaseCheckBox, org.openide.util.NbBundle.getMessage(RESTPanel.class, "RESTPanel.testcaseCheckBox.text")); // NOI18N
        testcaseCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        miscPanel.add(testcaseCheckBox);

        metricsCheckbox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(metricsCheckbox, org.openide.util.NbBundle.getMessage(RESTPanel.class, "RESTPanel.metricsCheckbox.text")); // NOI18N
        miscPanel.add(metricsCheckbox);

        openAPICheckBox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(openAPICheckBox, org.openide.util.NbBundle.getMessage(RESTPanel.class, "RESTPanel.openAPICheckBox.text")); // NOI18N
        miscPanel.add(openAPICheckBox);

        loggerCheckBox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(loggerCheckBox, org.openide.util.NbBundle.getMessage(RESTPanel.class, "RESTPanel.loggerCheckBox.text")); // NOI18N
        miscPanel.add(loggerCheckBox);

        jPanel1.add(miscPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(warningPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 27, Short.MAX_VALUE)
                .addComponent(warningPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void prefixFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_prefixFieldPropertyChange
        fire();
    }//GEN-LAST:event_prefixFieldPropertyChange

    private void suffixFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_suffixFieldPropertyChange
        fire();
    }//GEN-LAST:event_suffixFieldPropertyChange

    private void applicationConfigButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applicationConfigButtonActionPerformed
        if (configDialog == null) {
            configDialog = new RestConfigDialog();
            configDialog.init(getPackage(), project, sourceGroup);
        }
        configDialog.setVisible(true);
        if (configDialog.getDialogResult() == OK_OPTION) {
            this.getConfigData().setRestConfigData(configDialog.getRestConfigData());
        }
    }//GEN-LAST:event_applicationConfigButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel appPanel;
    private javax.swing.JButton applicationConfigButton;
    private javax.swing.JLabel entityLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JCheckBox loggerCheckBox;
    private javax.swing.JCheckBox metricsCheckbox;
    private javax.swing.JPanel miscPanel;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JLayeredPane namePane;
    private javax.swing.JCheckBox openAPICheckBox;
    private javax.swing.JLabel packageLabel;
    private javax.swing.JPanel packagePanel;
    private javax.swing.JLabel packagePrefixLabel;
    private javax.swing.JTextField packageTextField;
    private javax.swing.JLayeredPane packageWrapper;
    private javax.swing.JTextField prefixField;
    private javax.swing.JComboBox securityTypeCombo;
    private javax.swing.JLabel securityTypeLabel;
    private javax.swing.JTextField suffixField;
    private javax.swing.JPanel suffixPanel;
    private javax.swing.JCheckBox testcaseCheckBox;
    private javax.swing.JLabel warningLabel;
    private javax.swing.JPanel warningPanel;
    // End of variables declaration//GEN-END:variables
}
