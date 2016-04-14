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
package org.netbeans.jcode.mvc.controller;

import java.awt.Component;
import java.util.List;
import javax.lang.model.SourceVersion;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JList;
import javax.swing.event.ChangeEvent;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.text.JTextComponent;
import org.apache.commons.lang.StringUtils;
import org.netbeans.api.java.source.ui.ScanDialog;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.jcode.mvc.controller.api.returntype.ControllerReturnType;
import org.netbeans.jcode.rest.RestConfigData;
import org.netbeans.jcode.rest.RestConfigDialog;
import org.netbeans.jcode.stack.config.panel.*;
import org.netbeans.modules.j2ee.core.api.support.java.JavaIdentifiers;
import org.netbeans.modules.websvc.rest.model.api.RestApplication;
import org.netbeans.modules.websvc.rest.spi.RestSupport;
import org.netbeans.spi.java.project.support.ui.PackageView;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;
import static org.openide.util.NbBundle.getMessage;

/**
 *
 * @author Gaurav Gupta
 */
public class MVCPanel extends LayerConfigPanel<MVCData> {

    private static final String DEFAULT_PACKAGE = "controller";
    private RestConfigData restConfigData;
    private boolean useJersey;
    private boolean configuredREST;
    private List<RestApplication> restApplications;
    private RestConfigDialog configDialog;

    public MVCPanel() {
        initComponents();

    }

    @Override
    public boolean hasError() {
        warningLabel.setText("");
        if (!JavaIdentifiers.isValidPackageName(getPackage())) {
            warningLabel.setText(NbBundle.getMessage(MVCPanel.class, "MVCPanel.invalidPackage.message"));
            return true;
        }
        String prefix = getPrefix();
        String suffix = getSuffix();

        if (StringUtils.isNotBlank(prefix) && !SourceVersion.isName(prefix)) {
            warningLabel.setText(NbBundle.getMessage(MVCPanel.class, "MVCPanel.invalidPrefix.message"));
            return true;
        }
        if (StringUtils.isNotBlank(suffix) && !SourceVersion.isName(prefix + '_' + suffix)) {
            warningLabel.setText(NbBundle.getMessage(MVCPanel.class, "MVCPanel.invalidSuffix.message"));
            return true;
        }
        return false;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        hasError();
    }

    @Override
    public void read() {

    }

    @Override
    public void store() {
        this.getConfigData().setPrefixName(getPrefix());
        this.getConfigData().setSuffixName(getSuffix());
        this.getConfigData().setPackage(getPackage());
        if (restConfigData == null && !useJersey) {// && !configuredREST){
            restConfigData = new RestConfigData();
            restConfigData.setPackage(getPackage());
        }
        this.getConfigData().setRestConfigData(restConfigData);
        this.getConfigData().setBeanValidation(getBeanValidation());
        this.getConfigData().setReturnType(getReturnType());
    }

    private String _package;
    private Project project;
    private SourceGroup sourceGroup;

    @Override
    public void init(String _package, Project project, SourceGroup sourceGroup) {
        this._package = _package;
        this.project = project;
        this.sourceGroup = sourceGroup;

        this.setConfigData(new MVCData());
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
        addChangeListener(prefixField);
        addChangeListener(suffixField);

        final RestSupport restSupport = project.getLookup().lookup(RestSupport.class);
        if (restSupport != null) {
            if (restSupport.isEE5() && restSupport.hasJersey1(true)
                    || restSupport.hasSpringSupport() && !restSupport.hasJersey2(true)) {
                useJersey = true;
            }
        }
        ScanDialog.runWhenScanFinished(() -> {
            boolean configured;//restSupport.isRestSupportOn();
            configured = restSupport.hasJerseyServlet();
            restApplications = restSupport.getRestApplications();
            if (!configured) {
                configured = restApplications != null && !restApplications.isEmpty();
            }
            if(configDialog!=null){
                configDialog.setRestApplicationClasses(restApplications);
            }
            configurREST(configured);
        },getMessage(MVCPanel.class, "MVCPanel.scanningExistingApp.text") );

    }

    private void configurREST(boolean configured) {
        configuredREST = configured;
    }

    public String getPackage() {
        return ((JTextComponent) packageCombo.getEditor().getEditorComponent()).getText().trim();
    }
    
    public boolean getBeanValidation() {
        return beanValidation.isSelected();
    }
    
    public ControllerReturnType getReturnType() {
        return (ControllerReturnType) viewCombo.getSelectedItem();
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

    public String getSuffix() {
        return suffixField.getText().trim();
    }

    public String getPrefix() {
        return prefixField.getText().trim();
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
        warningPanel = new javax.swing.JPanel();
        warningLabel = new javax.swing.JLabel();
        suffixPanel = new javax.swing.JPanel();
        namePane = new javax.swing.JLayeredPane();
        prefixField = new javax.swing.JTextField();
        entityLabel = new javax.swing.JLabel();
        suffixField = new javax.swing.JTextField();
        nameLabel = new javax.swing.JLabel();
        applicationConfigButton = new javax.swing.JButton();
        viewPanel = new javax.swing.JPanel();
        viewLabel = new javax.swing.JLabel();
        viewCombo = new javax.swing.JComboBox();
        beanValidation = new javax.swing.JCheckBox();

        packagePanel.setLayout(new java.awt.BorderLayout(10, 0));

        packageLabel.setLabelFor(packageCombo);
        org.openide.awt.Mnemonics.setLocalizedText(packageLabel, org.openide.util.NbBundle.getMessage(MVCPanel.class, "MVCPanel.packageLabel.text")); // NOI18N
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

        warningPanel.setLayout(new java.awt.BorderLayout(10, 0));

        warningLabel.setForeground(new java.awt.Color(200, 0, 0));
        warningLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(warningLabel, org.openide.util.NbBundle.getMessage(MVCPanel.class, "MVCPanel.warningLabel.text")); // NOI18N
        warningPanel.add(warningLabel, java.awt.BorderLayout.CENTER);

        suffixPanel.setLayout(new java.awt.BorderLayout(10, 0));

        namePane.setLayout(new javax.swing.BoxLayout(namePane, javax.swing.BoxLayout.LINE_AXIS));

        prefixField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        prefixField.setText(org.openide.util.NbBundle.getMessage(MVCPanel.class, "MVCPanel.prefixField.text")); // NOI18N
        prefixField.setToolTipText(org.openide.util.NbBundle.getMessage(MVCPanel.class, "MVCPanel.prefixField.toolTipText")); // NOI18N
        prefixField.setPreferredSize(new java.awt.Dimension(100, 27));
        prefixField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                prefixFieldPropertyChange(evt);
            }
        });
        namePane.add(prefixField);

        entityLabel.setForeground(javax.swing.UIManager.getDefaults().getColor("Button.shadow"));
        org.openide.awt.Mnemonics.setLocalizedText(entityLabel, org.openide.util.NbBundle.getMessage(MVCPanel.class, "MVCPanel.entityLabel.text")); // NOI18N
        entityLabel.setPreferredSize(new java.awt.Dimension(58, 27));
        entityLabel.setRequestFocusEnabled(false);
        namePane.add(entityLabel);

        suffixField.setText(org.openide.util.NbBundle.getMessage(MVCPanel.class, "MVCPanel.suffixField.text")); // NOI18N
        suffixField.setPreferredSize(new java.awt.Dimension(100, 27));
        suffixField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                suffixFieldPropertyChange(evt);
            }
        });
        namePane.add(suffixField);

        suffixPanel.add(namePane, java.awt.BorderLayout.CENTER);

        org.openide.awt.Mnemonics.setLocalizedText(nameLabel, org.openide.util.NbBundle.getMessage(MVCPanel.class, "MVCPanel.nameLabel.text")); // NOI18N
        nameLabel.setPreferredSize(new java.awt.Dimension(100, 17));
        suffixPanel.add(nameLabel, java.awt.BorderLayout.WEST);

        org.openide.awt.Mnemonics.setLocalizedText(applicationConfigButton, org.openide.util.NbBundle.getMessage(MVCPanel.class, "MVCPanel.applicationConfigButton.text")); // NOI18N
        applicationConfigButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applicationConfigButtonActionPerformed(evt);
            }
        });

        viewPanel.setLayout(new java.awt.BorderLayout(10, 0));

        viewLabel.setLabelFor(viewCombo);
        org.openide.awt.Mnemonics.setLocalizedText(viewLabel, org.openide.util.NbBundle.getMessage(MVCPanel.class, "MVCPanel.viewLabel.text")); // NOI18N
        viewLabel.setPreferredSize(new java.awt.Dimension(100, 17));
        viewPanel.add(viewLabel, java.awt.BorderLayout.LINE_START);

        viewCombo.setModel(new DefaultComboBoxModel(ControllerReturnType.values()));
        viewCombo.setRenderer(new BasicComboBoxRenderer() {
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (isSelected) {
                    setBackground(list.getSelectionBackground());
                    setForeground(list.getSelectionForeground());
                    if (index > -1) {
                        ControllerReturnType returnType = (ControllerReturnType)value;
                        list.setToolTipText(returnType.getDescription());
                    }
                }
                else {
                    setBackground(list.getBackground());
                    setForeground(list.getForeground());
                }
                setFont(list.getFont());
                setText((value == null) ? "" : value.toString());

                return this;
            }
        });
        viewCombo.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                viewComboPropertyChange(evt);
            }
        });
        viewPanel.add(viewCombo, java.awt.BorderLayout.CENTER);

        beanValidation.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(beanValidation, org.openide.util.NbBundle.getMessage(MVCPanel.class, "MVCPanel.beanValidation.text")); // NOI18N
        beanValidation.setToolTipText(org.openide.util.NbBundle.getMessage(MVCPanel.class, "MVCPanel.beanValidation.toolTipText")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(packagePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE)
                    .addComponent(suffixPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(109, 109, 109)
                        .addComponent(beanValidation)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(applicationConfigButton))
                    .addComponent(viewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE))
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
                .addComponent(suffixPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(packagePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(viewPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(applicationConfigButton)
                    .addComponent(beanValidation))
                .addGap(42, 42, 42))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(160, Short.MAX_VALUE)
                    .addComponent(warningPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
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
        if (useJersey) {
            NotifyDescriptor d = new NotifyDescriptor.Message(getMessage(MVCPanel.class, "MVCPanel.notSupported.text"), NotifyDescriptor.INFORMATION_MESSAGE);
            d.setTitle(getMessage(MVCPanel.class, "MVCPanel.notSupported.title"));
            DialogDisplayer.getDefault().notify(d);
//        } else if (configuredREST) { //Don't delete it
//            final RestSupport restSupport = project.getLookup().lookup(RestSupport.class);
//            List<RestApplication> restApplications = restSupport.getRestApplications();
//            List<String> restApplicationClasses = restApplications.stream().map(a -> a.getApplicationClass()).collect(Collectors.toList());
//            int reply = javax.swing.JOptionPane.showConfirmDialog(WindowManager.getDefault().getMainWindow(),
//                    getMessage(MVCPanel.class, "MVCPanel.pathExist.text", restApplicationClasses),
//                    getMessage(MVCPanel.class, "MVCPanel.pathExist.title"), JOptionPane.YES_NO_OPTION);
//            if (reply == JOptionPane.YES_OPTION) {
//                openApplicationConfig();
//            }
        } else {
            openApplicationConfig();
        }
    }//GEN-LAST:event_applicationConfigButtonActionPerformed

    private void viewComboPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_viewComboPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_viewComboPropertyChange
    private void openApplicationConfig() {
        if (configDialog == null) {
            configDialog = new RestConfigDialog();
            if (restApplications != null) {
                configDialog.setRestApplicationClasses(restApplications);
            }
            configDialog.init(_package, project, sourceGroup);
        }
        configDialog.setVisible(true);
        if (configDialog.getDialogResult() == javax.swing.JOptionPane.OK_OPTION) {
            restConfigData = configDialog.getRestConfigData();
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton applicationConfigButton;
    private javax.swing.JCheckBox beanValidation;
    private javax.swing.JLabel entityLabel;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JLayeredPane namePane;
    private javax.swing.JComboBox packageCombo;
    private javax.swing.JLabel packageLabel;
    private javax.swing.JPanel packagePanel;
    private javax.swing.JTextField prefixField;
    private javax.swing.JTextField suffixField;
    private javax.swing.JPanel suffixPanel;
    private javax.swing.JComboBox viewCombo;
    private javax.swing.JLabel viewLabel;
    private javax.swing.JPanel viewPanel;
    private javax.swing.JLabel warningLabel;
    private javax.swing.JPanel warningPanel;
    // End of variables declaration//GEN-END:variables
}