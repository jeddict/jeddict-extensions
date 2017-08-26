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
package org.netbeans.jcode.rest.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import static javax.lang.model.SourceVersion.isName;
import javax.swing.ComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import static javax.swing.JOptionPane.OK_OPTION;
import javax.swing.text.JTextComponent;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.netbeans.api.java.source.ui.ScanDialog.runWhenScanFinished;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import static org.netbeans.jcode.core.util.JavaSourceHelper.isValidPackageName;
import org.netbeans.jcode.rest.applicationconfig.RestConfigData;
import org.netbeans.jcode.rest.applicationconfig.RestConfigDialog;
import org.netbeans.jcode.rest.filter.FilterType;
import org.netbeans.jcode.stack.config.panel.*;
import org.netbeans.modules.websvc.rest.spi.RestSupport;
import org.netbeans.spi.java.project.support.ui.PackageView;
import static org.openide.util.NbBundle.getMessage;

/**
 *
 * @author Gaurav Gupta
 */
public class RESTPanel extends LayerConfigPanel<RESTData> {

    private static final String DEFAULT_PACKAGE = "controller";
    private RestConfigDialog configDialog;
    private final Map<JCheckBox, FilterType> eventTypeBoxs = new HashMap<>();

    public RESTPanel() {
        initComponents();
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
       
        setLogger(data.isLogger());
        setMetrics(data.isMetrics());
        setDocsEnable(data.isDocsEnable());
        setTestCase(data.isTestCase());
        
        setSelectedEventType(data.getFilterTypes());
    }

    @Override
    public void store() {
        RESTData data = this.getConfigData();
        data.setPrefixName(getPrefix());
        data.setSuffixName(getSuffix());
        data.setPackage(getPackage());
        if (data.getRestConfigData() == null ) {//&& !useJersey// && !configuredREST){
            RestConfigData restConfigData = new RestConfigData();
            restConfigData.setPackage(getPackage());
            data.setRestConfigData(restConfigData);
        }
//        data.setReturnType(getReturnType());
        data.setFilterTypes(getSelectedEventType());
        data.setMetrics(isMetrics());
        data.setLogger(isLogger());
        data.setDocsEnable(isDocsEnable());
        data.setTestCase(isTestCase());
    }

    private Project project;
    private SourceGroup sourceGroup;

    private void setPackageType(JComboBox comboBox){
            comboBox.setRenderer(PackageView.listRenderer());
            ComboBoxModel model = PackageView.createListView(sourceGroup);
            if (model.getSize() > 0) {
                model.setSelectedItem(model.getElementAt(0));
            }
            comboBox.setModel(model);
            addChangeListener(comboBox); 
    }
    
    @Override
    public void init(String modelerPackage, Project project, SourceGroup sourceGroup) {
        this.project = project;
        this.sourceGroup = sourceGroup;

        if (sourceGroup != null) {
            setPackageType(packageCombo);

            String _package;
            if (isBlank(modelerPackage)) {
                _package = DEFAULT_PACKAGE;
            } else {
                _package = modelerPackage + '.' + DEFAULT_PACKAGE;
            }
            setPackage(_package);
        }
        addChangeListener(prefixField);
        addChangeListener(suffixField);
    }

    public String getPackage() {
        return ((JTextComponent) packageCombo.getEditor().getEditorComponent()).getText().trim();
    }

    private void setPackage(String _package) {
        ComboBoxModel model = packageCombo.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            if (model.getElementAt(i).toString().equals(_package)) {
                model.setSelectedItem(model.getElementAt(i));
                break;
            }
        }
        ((JTextComponent) packageCombo.getEditor().getEditorComponent()).setText(_package);
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
    
    private boolean isDocsEnable() {
        return docsCheckBox.isSelected();
    }

    private void setDocsEnable(boolean docsEnable) {
        docsCheckBox.setSelected(docsEnable);
    }
   
    private boolean isLogger() {
        return loggerCheckBox.isSelected();
    }

    private void setLogger(boolean loggerEnable) {
        loggerCheckBox.setSelected(loggerEnable);
    }
    
    

    public List<FilterType> getSelectedEventType() {
        List<FilterType> eventTypes = new ArrayList<>();
        for (Entry<JCheckBox, FilterType> eventTypeBoxEntry : eventTypeBoxs.entrySet()) {
            if (eventTypeBoxEntry.getKey().isSelected()) {
                eventTypes.add(eventTypeBoxEntry.getValue());
            }
        }
        return eventTypes;
    }
    
    public void setSelectedEventType(List<FilterType> controllerEventTypes) {
        for (Entry<JCheckBox, FilterType> eventTypeBoxEntry : eventTypeBoxs.entrySet()) {
            eventTypeBoxEntry.getKey().setSelected(controllerEventTypes.contains(eventTypeBoxEntry.getValue()));
        }
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
        packageCombo = new javax.swing.JComboBox();
        appPanel = new javax.swing.JPanel();
        testcaseCheckBox = new javax.swing.JCheckBox();
        applicationConfigButton = new javax.swing.JButton();
        miscPanel = new javax.swing.JPanel();
        wrapper = new javax.swing.JLayeredPane();
        metricsCheckbox = new javax.swing.JCheckBox();
        docsCheckBox = new javax.swing.JCheckBox();
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
        prefixField.setText(org.openide.util.NbBundle.getMessage(RESTPanel.class, "RESTPanel.prefixField.text")); // NOI18N
        prefixField.setToolTipText(org.openide.util.NbBundle.getMessage(RESTPanel.class, "RESTPanel.prefixField.toolTipText")); // NOI18N
        prefixField.setPreferredSize(new java.awt.Dimension(50, 27));
        prefixField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                prefixFieldPropertyChange(evt);
            }
        });
        namePane.add(prefixField);

        entityLabel.setForeground(javax.swing.UIManager.getDefaults().getColor("Button.shadow"));
        entityLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(entityLabel, org.openide.util.NbBundle.getMessage(RESTPanel.class, "RESTPanel.entityLabel.text")); // NOI18N
        entityLabel.setPreferredSize(new java.awt.Dimension(58, 27));
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

        packageLabel.setLabelFor(packageCombo);
        org.openide.awt.Mnemonics.setLocalizedText(packageLabel, org.openide.util.NbBundle.getMessage(RESTPanel.class, "RESTPanel.packageLabel.text")); // NOI18N
        packageLabel.setPreferredSize(new java.awt.Dimension(100, 17));
        packagePanel.add(packageLabel, java.awt.BorderLayout.LINE_START);

        packageCombo.setEditable(true);
        packageCombo.setEditable(true);
        packageCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " " }));
        packageCombo.setPreferredSize(new java.awt.Dimension(60, 27));
        packageCombo.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                packageComboPropertyChange(evt);
            }
        });
        packagePanel.add(packageCombo, java.awt.BorderLayout.CENTER);

        jPanel1.add(packagePanel);

        testcaseCheckBox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(testcaseCheckBox, org.openide.util.NbBundle.getMessage(RESTPanel.class, "RESTPanel.testcaseCheckBox.text")); // NOI18N

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
                .addGap(325, 325, 325)
                .addComponent(testcaseCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                .addComponent(applicationConfigButton)
                .addContainerGap())
        );
        appPanelLayout.setVerticalGroup(
            appPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(appPanelLayout.createSequentialGroup()
                .addGroup(appPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(testcaseCheckBox)
                    .addComponent(applicationConfigButton, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel1.add(appPanel);

        miscPanel.setLayout(new java.awt.BorderLayout());

        metricsCheckbox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(metricsCheckbox, org.openide.util.NbBundle.getMessage(RESTPanel.class, "RESTPanel.metricsCheckbox.text")); // NOI18N

        docsCheckBox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(docsCheckBox, org.openide.util.NbBundle.getMessage(RESTPanel.class, "RESTPanel.docsCheckBox.text")); // NOI18N
        docsCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                docsCheckBoxActionPerformed(evt);
            }
        });

        loggerCheckBox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(loggerCheckBox, org.openide.util.NbBundle.getMessage(RESTPanel.class, "RESTPanel.loggerCheckBox.text")); // NOI18N

        wrapper.setLayer(metricsCheckbox, javax.swing.JLayeredPane.DEFAULT_LAYER);
        wrapper.setLayer(docsCheckBox, javax.swing.JLayeredPane.DEFAULT_LAYER);
        wrapper.setLayer(loggerCheckBox, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout wrapperLayout = new javax.swing.GroupLayout(wrapper);
        wrapper.setLayout(wrapperLayout);
        wrapperLayout.setHorizontalGroup(
            wrapperLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(wrapperLayout.createSequentialGroup()
                .addGap(110, 110, 110)
                .addComponent(metricsCheckbox, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(69, 69, 69)
                .addComponent(loggerCheckBox)
                .addGap(80, 80, 80)
                .addComponent(docsCheckBox)
                .addContainerGap())
        );
        wrapperLayout.setVerticalGroup(
            wrapperLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, wrapperLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(wrapperLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(metricsCheckbox)
                    .addComponent(docsCheckBox)
                    .addComponent(loggerCheckBox)))
        );

        miscPanel.add(wrapper, java.awt.BorderLayout.CENTER);

        jPanel1.add(miscPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 629, Short.MAX_VALUE)
                    .addComponent(warningPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(warningPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void packageComboPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_packageComboPropertyChange
        fire();
    }//GEN-LAST:event_packageComboPropertyChange

    private void prefixFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_prefixFieldPropertyChange
        fire();
    }//GEN-LAST:event_prefixFieldPropertyChange

    private void suffixFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_suffixFieldPropertyChange
        fire();
    }//GEN-LAST:event_suffixFieldPropertyChange

    private void applicationConfigButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applicationConfigButtonActionPerformed
//        if (useJersey) {
//            NotifyDescriptor d = new NotifyDescriptor.Message(getMessage(RESTPanel.class, "RESTPanel.notSupported.text"), NotifyDescriptor.INFORMATION_MESSAGE);
//            d.setTitle(getMessage(RESTPanel.class, "RESTPanel.notSupported.title"));
//            DialogDisplayer.getDefault().notify(d);
//        } else if (configuredREST) { //Don't delete it
//            final RestSupport restSupport = project.getLookup().lookup(RestSupport.class);
//            List<RestApplication> restApplications = restSupport.getRestApplications();
//            List<String> restApplicationClasses = restApplications.stream().map(a -> a.getApplicationClass()).collect(Collectors.toList());
//            int reply = javax.swing.JOptionPane.showConfirmDialog(WindowManager.getDefault().getMainWindow(),
//                    getMessage(RESTPanel.class, "RESTPanel.pathExist.text", restApplicationClasses),
//                    getMessage(RESTPanel.class, "RESTPanel.pathExist.title"), JOptionPane.YES_NO_OPTION);
//            if (reply == JOptionPane.YES_OPTION) {
//                openApplicationConfig();
//            }
//        } else {
            openApplicationConfig();
//        }
    }//GEN-LAST:event_applicationConfigButtonActionPerformed

    private void docsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_docsCheckBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_docsCheckBoxActionPerformed
    private void openApplicationConfig() {
        if (configDialog == null) {
            configDialog = new RestConfigDialog();
            configDialog.init(getPackage(), project, sourceGroup);
            final RestSupport restSupport = project.getLookup().lookup(RestSupport.class);
            if (restSupport != null) {
                runWhenScanFinished(() -> {
                    configDialog.setRestApplicationClasses(restSupport.getRestApplications());
                    configDialog.setVisible(true);
                }, getMessage(RESTPanel.class, "RESTPanel.scanningExistingApp.text"));
            }
        } else {
            configDialog.setVisible(true);
        }
        if (configDialog.getDialogResult() == OK_OPTION) {
            this.getConfigData().setRestConfigData(configDialog.getRestConfigData());
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel appPanel;
    private javax.swing.JButton applicationConfigButton;
    private javax.swing.JCheckBox docsCheckBox;
    private javax.swing.JLabel entityLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JCheckBox loggerCheckBox;
    private javax.swing.JCheckBox metricsCheckbox;
    private javax.swing.JPanel miscPanel;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JLayeredPane namePane;
    private javax.swing.JComboBox packageCombo;
    private javax.swing.JLabel packageLabel;
    private javax.swing.JPanel packagePanel;
    private javax.swing.JTextField prefixField;
    private javax.swing.JTextField suffixField;
    private javax.swing.JPanel suffixPanel;
    private javax.swing.JCheckBox testcaseCheckBox;
    private javax.swing.JLabel warningLabel;
    private javax.swing.JPanel warningPanel;
    private javax.swing.JLayeredPane wrapper;
    // End of variables declaration//GEN-END:variables
}
