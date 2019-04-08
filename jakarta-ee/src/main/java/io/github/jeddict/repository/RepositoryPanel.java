/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.repository;

import io.github.jeddict.jcode.LayerConfigPanel;
import javax.lang.model.SourceVersion;
import static io.github.jeddict.util.StringUtils.equalsIgnoreCase;
import static io.github.jeddict.util.StringUtils.isNotBlank;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import static org.netbeans.modules.j2ee.core.api.support.java.JavaIdentifiers.isValidPackageName;
import static org.openide.util.NbBundle.getMessage;

/**
 *
 * @author Gaurav Gupta
 */
public class RepositoryPanel extends LayerConfigPanel<RepositoryData> {

    private static final String DEFAULT_PACKAGE = "repository";

    public RepositoryPanel() {
        initComponents();
        
        servicePackagePanel.setVisible(false);
        servicePanel.setVisible(false);
        separatorPane.setVisible(false);
    }

    @Override
    public boolean hasError() {
        warningLabel.setText("");

        String repositoryPackage = getRepositoryPackage();
        String repositoryPrefix = getRepositoryPrefix();
        String repositorySuffix = getRepositorySuffix();

        if (!isValidPackageName(repositoryPackage)) {
            warningLabel.setText(getMessage(RepositoryPanel.class, "RepositoryPanel.invalidRepositoryPackage.message"));
            return true;
        }
        if (isNotBlank(repositoryPrefix) && !SourceVersion.isName(repositoryPrefix)) {
            warningLabel.setText(getMessage(RepositoryPanel.class, "RepositoryPanel.invalidRepositoryPrefix.message"));
            return true;
        }
        if (isNotBlank(repositorySuffix) && !SourceVersion.isName(repositoryPrefix + '_' + repositorySuffix)) {
            warningLabel.setText(getMessage(RepositoryPanel.class, "RepositoryPanel.invalidRepositorySuffix.message"));
            return true;
        }

        String servicePackage = getServicePackage();
        String servicePrefix = getServicePrefix();
        String serviceSuffix = getServiceSuffix();

        if (!isValidPackageName(servicePackage)) {
            warningLabel.setText(getMessage(RepositoryPanel.class, "RepositoryPanel.invalidServicePackage.message"));
            return true;
        }
        if (isNotBlank(servicePrefix) && !SourceVersion.isName(servicePrefix)) {
            warningLabel.setText(getMessage(RepositoryPanel.class, "RepositoryPanel.invalidServicePrefix.message"));
            return true;
        }
        if (isNotBlank(serviceSuffix) && !SourceVersion.isName(servicePrefix + '_' + serviceSuffix)) {
            warningLabel.setText(getMessage(RepositoryPanel.class, "RepositoryPanel.invalidServiceSuffix.message"));
            return true;
        }

        if (equalsIgnoreCase(repositorySuffix, serviceSuffix)) {
            warningLabel.setText(getMessage(RepositoryPanel.class, "RepositoryPanel.suffixValueSame.message"));
            return true;
        }
        return false;
    }

    @Override
    public void read() {
        RepositoryData data = this.getConfigData();
        if (isNotBlank(data.getRepositoryPackage())) {
            setRepositoryPackage(data.getRepositoryPackage());
        }
        if (isNotBlank(data.getRepositoryPrefixName())) {
            setRepositoryPrefix(data.getRepositoryPrefixName());
        }
        if (isNotBlank(data.getRepositorySuffixName())) {
            setRepositorySuffix(data.getRepositorySuffixName());
        }
        if (isNotBlank(data.getServicePackage())) {
            setServicePackage(data.getServicePackage());
        }
        if (isNotBlank(data.getServicePrefixName())) {
            setServicePrefix(data.getServicePrefixName());
        }
        if (isNotBlank(data.getServiceSuffixName())) {
            setServiceSuffix(data.getServiceSuffixName());
        }
    }

    @Override
    public void store() {
        RepositoryData data = this.getConfigData();
        data.setRepositoryPackage(getRepositoryPackage());
        data.setRepositoryPrefixName(getRepositoryPrefix());
        data.setRepositorySuffixName(getRepositorySuffix());
        data.setServicePackage(getServicePackage());
        data.setServicePrefixName(getServicePrefix());
        data.setServiceSuffixName(getServiceSuffix());
    }

    @Override
    public void init(String _package, Project project, SourceGroup sourceGroup) {
        setRepositoryPackage(DEFAULT_PACKAGE);
        addChangeListener(repositoryPrefixField);
        addChangeListener(repositorySuffixField);
        addChangeListener(servicePrefixField);
        addChangeListener(serviceSuffixField);
    }

    public String getRepositoryPackage() {
        return repositoryPackageTextField.getText().trim();
    }

    private void setRepositoryPackage(String repositoryPackage) {
        repositoryPackageTextField.setText(repositoryPackage);
    }

    public String getRepositorySuffix() {
        return repositorySuffixField.getText().trim();
    }

    public String getRepositoryPrefix() {
        return repositoryPrefixField.getText().trim();
    }

    private void setRepositoryPrefix(String prefix) {
        repositoryPrefixField.setText(prefix);
    }

    private void setRepositorySuffix(String suffix) {
        repositorySuffixField.setText(suffix);
    }

    public String getServicePackage() {
        return servicePackageTextField.getText().trim();
    }

    private void setServicePackage(String servicePackage) {
        servicePackageTextField.setText(servicePackage);
    }

    public String getServiceSuffix() {
        return serviceSuffixField.getText().trim();
    }

    public String getServicePrefix() {
        return servicePrefixField.getText().trim();
    }

    private void setServicePrefix(String prefix) {
        servicePrefixField.setText(prefix);
    }

    private void setServiceSuffix(String suffix) {
        serviceSuffixField.setText(suffix);
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
        repositoryPanel = new javax.swing.JPanel();
        repositoryInnerPanel = new javax.swing.JLayeredPane();
        repositoryPrefixField = new javax.swing.JTextField();
        repositoryEntityLabel = new javax.swing.JLabel();
        repositorySuffixField = new javax.swing.JTextField();
        repositoryPanelLabel = new javax.swing.JLabel();
        repositoryPackagePanel = new javax.swing.JPanel();
        repositoryPackageLabel = new javax.swing.JLabel();
        repositoryPackageWrapper = new javax.swing.JLayeredPane();
        repositoryPackagePrefixLabel = new javax.swing.JLabel();
        repositoryPackageTextField = new javax.swing.JTextField();
        separatorPane = new javax.swing.JLayeredPane();
        separator = new javax.swing.JSeparator();
        servicePanel = new javax.swing.JPanel();
        serviceInnerPanel = new javax.swing.JLayeredPane();
        servicePrefixField = new javax.swing.JTextField();
        serviceEntityLabel = new javax.swing.JLabel();
        serviceSuffixField = new javax.swing.JTextField();
        servicePanelLabel = new javax.swing.JLabel();
        servicePackagePanel = new javax.swing.JPanel();
        servicePackageLabel = new javax.swing.JLabel();
        servicePackageWrapper = new javax.swing.JLayeredPane();
        servicePackagePrefixLabel = new javax.swing.JLabel();
        servicePackageTextField = new javax.swing.JTextField();

        warningPanel.setLayout(new java.awt.BorderLayout(10, 0));

        warningLabel.setForeground(new java.awt.Color(200, 0, 0));
        warningLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(warningLabel, org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.warningLabel.text")); // NOI18N
        warningPanel.add(warningLabel, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.GridLayout(5, 0, 0, 15));

        repositoryPanel.setLayout(new java.awt.BorderLayout(10, 0));

        repositoryInnerPanel.setLayout(new javax.swing.BoxLayout(repositoryInnerPanel, javax.swing.BoxLayout.LINE_AXIS));

        repositoryPrefixField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        repositoryPrefixField.setText(org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.repositoryPrefixField.text")); // NOI18N
        repositoryPrefixField.setToolTipText(org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.repositoryPrefixField.toolTipText")); // NOI18N
        repositoryPrefixField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                repositoryPrefixFieldPropertyChange(evt);
            }
        });
        repositoryInnerPanel.add(repositoryPrefixField);

        repositoryEntityLabel.setForeground(javax.swing.UIManager.getDefaults().getColor("Button.shadow"));
        repositoryEntityLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(repositoryEntityLabel, org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.repositoryEntityLabel.text")); // NOI18N
        repositoryEntityLabel.setPreferredSize(new java.awt.Dimension(70, 27));
        repositoryEntityLabel.setRequestFocusEnabled(false);
        repositoryInnerPanel.add(repositoryEntityLabel);

        repositorySuffixField.setText(org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.repositorySuffixField.text")); // NOI18N
        repositorySuffixField.setToolTipText(org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.repositorySuffixField.toolTipText")); // NOI18N
        repositorySuffixField.setPreferredSize(new java.awt.Dimension(100, 27));
        repositorySuffixField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                repositorySuffixFieldPropertyChange(evt);
            }
        });
        repositoryInnerPanel.add(repositorySuffixField);

        repositoryPanel.add(repositoryInnerPanel, java.awt.BorderLayout.CENTER);

        repositoryPanelLabel.setLabelFor(repositorySuffixField);
        org.openide.awt.Mnemonics.setLocalizedText(repositoryPanelLabel, org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.repositoryPanelLabel.text")); // NOI18N
        repositoryPanelLabel.setPreferredSize(new java.awt.Dimension(120, 17));
        repositoryPanel.add(repositoryPanelLabel, java.awt.BorderLayout.WEST);

        jPanel1.add(repositoryPanel);

        repositoryPackagePanel.setLayout(new java.awt.BorderLayout(10, 0));

        org.openide.awt.Mnemonics.setLocalizedText(repositoryPackageLabel, org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.repositoryPackageLabel.text")); // NOI18N
        repositoryPackageLabel.setPreferredSize(new java.awt.Dimension(120, 17));
        repositoryPackagePanel.add(repositoryPackageLabel, java.awt.BorderLayout.LINE_START);

        repositoryPackageWrapper.setLayout(new java.awt.BorderLayout());

        repositoryPackagePrefixLabel.setForeground(new java.awt.Color(153, 153, 153));
        org.openide.awt.Mnemonics.setLocalizedText(repositoryPackagePrefixLabel, org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.repositoryPackagePrefixLabel.text")); // NOI18N
        repositoryPackagePrefixLabel.setPreferredSize(new java.awt.Dimension(185, 14));
        repositoryPackageWrapper.add(repositoryPackagePrefixLabel, java.awt.BorderLayout.WEST);

        repositoryPackageTextField.setText(org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.repositoryPackageTextField.text")); // NOI18N
        repositoryPackageWrapper.add(repositoryPackageTextField, java.awt.BorderLayout.CENTER);

        repositoryPackagePanel.add(repositoryPackageWrapper, java.awt.BorderLayout.CENTER);

        jPanel1.add(repositoryPackagePanel);

        separatorPane.setLayer(separator, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout separatorPaneLayout = new javax.swing.GroupLayout(separatorPane);
        separatorPane.setLayout(separatorPaneLayout);
        separatorPaneLayout.setHorizontalGroup(
            separatorPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(separator, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 647, Short.MAX_VALUE)
        );
        separatorPaneLayout.setVerticalGroup(
            separatorPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(separatorPaneLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(separator, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(separatorPane);

        servicePanel.setLayout(new java.awt.BorderLayout(10, 0));

        serviceInnerPanel.setLayout(new javax.swing.BoxLayout(serviceInnerPanel, javax.swing.BoxLayout.LINE_AXIS));

        servicePrefixField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        servicePrefixField.setText(org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.servicePrefixField.text")); // NOI18N
        servicePrefixField.setToolTipText(org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.servicePrefixField.toolTipText")); // NOI18N
        servicePrefixField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                servicePrefixFieldPropertyChange(evt);
            }
        });
        serviceInnerPanel.add(servicePrefixField);

        serviceEntityLabel.setForeground(javax.swing.UIManager.getDefaults().getColor("Button.shadow"));
        serviceEntityLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(serviceEntityLabel, org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.serviceEntityLabel.text")); // NOI18N
        serviceEntityLabel.setPreferredSize(new java.awt.Dimension(70, 27));
        serviceEntityLabel.setRequestFocusEnabled(false);
        serviceInnerPanel.add(serviceEntityLabel);

        serviceSuffixField.setText(org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.serviceSuffixField.text")); // NOI18N
        serviceSuffixField.setToolTipText(org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.serviceSuffixField.toolTipText")); // NOI18N
        serviceSuffixField.setPreferredSize(new java.awt.Dimension(100, 27));
        serviceSuffixField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                serviceSuffixFieldPropertyChange(evt);
            }
        });
        serviceInnerPanel.add(serviceSuffixField);

        servicePanel.add(serviceInnerPanel, java.awt.BorderLayout.CENTER);

        servicePanelLabel.setLabelFor(repositorySuffixField);
        org.openide.awt.Mnemonics.setLocalizedText(servicePanelLabel, org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.servicePanelLabel.text")); // NOI18N
        servicePanelLabel.setPreferredSize(new java.awt.Dimension(170, 17));
        servicePanel.add(servicePanelLabel, java.awt.BorderLayout.WEST);

        jPanel1.add(servicePanel);

        servicePackagePanel.setLayout(new java.awt.BorderLayout(10, 0));

        org.openide.awt.Mnemonics.setLocalizedText(servicePackageLabel, org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.servicePackageLabel.text")); // NOI18N
        servicePackageLabel.setPreferredSize(new java.awt.Dimension(160, 17));
        servicePackagePanel.add(servicePackageLabel, java.awt.BorderLayout.LINE_START);

        servicePackageWrapper.setLayout(new java.awt.BorderLayout());

        servicePackagePrefixLabel.setForeground(new java.awt.Color(153, 153, 153));
        org.openide.awt.Mnemonics.setLocalizedText(servicePackagePrefixLabel, org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.servicePackagePrefixLabel.text")); // NOI18N
        servicePackagePrefixLabel.setPreferredSize(new java.awt.Dimension(185, 14));
        servicePackageWrapper.add(servicePackagePrefixLabel, java.awt.BorderLayout.WEST);

        servicePackageTextField.setText(org.openide.util.NbBundle.getMessage(RepositoryPanel.class, "RepositoryPanel.servicePackageTextField.text")); // NOI18N
        servicePackageWrapper.add(servicePackageTextField, java.awt.BorderLayout.CENTER);

        servicePackagePanel.add(servicePackageWrapper, java.awt.BorderLayout.CENTER);

        jPanel1.add(servicePackagePanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 677, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(warningPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 647, Short.MAX_VALUE)
                    .addContainerGap()))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 260, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(224, Short.MAX_VALUE)
                    .addComponent(warningPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(84, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void repositorySuffixFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_repositorySuffixFieldPropertyChange
        fire();
    }//GEN-LAST:event_repositorySuffixFieldPropertyChange

    private void repositoryPrefixFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_repositoryPrefixFieldPropertyChange
        fire();
    }//GEN-LAST:event_repositoryPrefixFieldPropertyChange

    private void serviceSuffixFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_serviceSuffixFieldPropertyChange
        fire();
    }//GEN-LAST:event_serviceSuffixFieldPropertyChange

    private void servicePrefixFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_servicePrefixFieldPropertyChange
        fire();
    }//GEN-LAST:event_servicePrefixFieldPropertyChange

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel repositoryEntityLabel;
    private javax.swing.JLayeredPane repositoryInnerPanel;
    private javax.swing.JLabel repositoryPackageLabel;
    private javax.swing.JPanel repositoryPackagePanel;
    private javax.swing.JLabel repositoryPackagePrefixLabel;
    private javax.swing.JTextField repositoryPackageTextField;
    private javax.swing.JLayeredPane repositoryPackageWrapper;
    private javax.swing.JPanel repositoryPanel;
    private javax.swing.JLabel repositoryPanelLabel;
    private javax.swing.JTextField repositoryPrefixField;
    private javax.swing.JTextField repositorySuffixField;
    private javax.swing.JSeparator separator;
    private javax.swing.JLayeredPane separatorPane;
    private javax.swing.JLabel serviceEntityLabel;
    private javax.swing.JLayeredPane serviceInnerPanel;
    private javax.swing.JLabel servicePackageLabel;
    private javax.swing.JPanel servicePackagePanel;
    private javax.swing.JLabel servicePackagePrefixLabel;
    private javax.swing.JTextField servicePackageTextField;
    private javax.swing.JLayeredPane servicePackageWrapper;
    private javax.swing.JPanel servicePanel;
    private javax.swing.JLabel servicePanelLabel;
    private javax.swing.JTextField servicePrefixField;
    private javax.swing.JTextField serviceSuffixField;
    private javax.swing.JLabel warningLabel;
    private javax.swing.JPanel warningPanel;
    // End of variables declaration//GEN-END:variables

}
