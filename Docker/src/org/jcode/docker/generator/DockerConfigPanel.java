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
package org.jcode.docker.generator;

import java.awt.Component;
import java.util.Arrays;
import java.util.prefs.Preferences;
import java.util.stream.Stream;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import org.netbeans.modules.docker.api.DockerInstance;
import org.apache.commons.lang.StringUtils;
import static org.jcode.docker.generator.ServerType.NONE;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.jcode.stack.config.panel.LayerConfigPanel;
import org.netbeans.jcode.util.PreferenceUtils;
import org.netbeans.modules.docker.api.DockerSupport;
import static org.openide.util.NbBundle.getMessage;

/**
 *
 * @author Gaurav Gupta
 */
public class DockerConfigPanel extends LayerConfigPanel<DockerConfigData> {

    private Preferences pref;

    public DockerConfigPanel() {
        initComponents();
    }

    @Override
    public boolean hasError() {
        warningLabel.setText("");
        if (getServerType() == null || getServerType() == NONE) {
            warningLabel.setText(getMessage(DockerConfigPanel.class, "DockerConfigPanel.serverRequired.message"));
            return true;
        }
        if (StringUtils.isBlank(dbUserTextField.getText())) {
            warningLabel.setText(getMessage(DockerConfigPanel.class, "DockerConfigPanel.invalidDBUserName.message"));
            return true;
        }
        if (StringUtils.isBlank(dbPasswordTextField.getText())) {
            warningLabel.setText(getMessage(DockerConfigPanel.class, "DockerConfigPanel.invalidDBPassword.message"));
            return true;
        }
        if (StringUtils.isBlank(dbNameTextField.getText())) {
            warningLabel.setText(getMessage(DockerConfigPanel.class, "DockerConfigPanel.invalidDBName.message"));
            return true;
        }
        if (StringUtils.isBlank(dsTextField.getText())) {
            warningLabel.setText(getMessage(DockerConfigPanel.class, "DockerConfigPanel.invalidDataSource.message"));
            return true;
        }
        if (!validateDB()) {
            return true;
        }

        return false;
    }

    private boolean validateDB() {
        if (getDatabaseType() == DatabaseType.MYSQL) {
            if (StringUtils.equals(dbUserTextField.getText(), "root")) {
                warningLabel.setText(getMessage(DockerConfigPanel.class, "MYSQL_ROOT_USER_EXIST"));
                return false;
            }
        }
        return true;
    }

    @Override
    public void read() {
        this.setConfigData(PreferenceUtils.get(pref, DockerConfigData.class));
        DockerConfigData data = this.getConfigData();
        if (data.getServerType() != null) {
            serverComboBox.setSelectedItem(data.getServerType());
        }
        if (StringUtils.isNotBlank(data.getServerVersion())) {
            serverVersionComboBox.setSelectedItem(data.getServerVersion());
        }
        if (data.getDatabaseType() != null) {
            dbComboBox.setSelectedItem(data.getDatabaseType());
        }
        if (StringUtils.isNotBlank(data.getDatabaseVersion())) {
            dbVersionComboBox.setSelectedItem(data.getDatabaseVersion());
        }
        if (StringUtils.isNotBlank(data.getDbUserName())) {
            dbUserTextField.setText(data.getDbUserName());
        }
        if (StringUtils.isNotBlank(data.getDbPassword())) {
            dbPasswordTextField.setText(data.getDbPassword());
        }
        if (StringUtils.isNotBlank(data.getDbName())) {
            dbNameTextField.setText(data.getDbName());
        }
        if (StringUtils.isNotBlank(data.getDataSource())) {
            dsTextField.setText(data.getDataSource());
        }
        
        dockerMachineCheckBox.setSelected(data.isDockerActivated());
        if (data.isDockerActivated() && StringUtils.isNotEmpty(data.getDockerMachine())) {
            setDockerMachine(data.getDockerMachine());
        }
        
        serverComboBoxActionPerformed(null);
        dockerMachineCheckBoxActionPerformed(null);
    }

    @Override
    public void store() {
        DockerConfigData data = this.getConfigData();
        data.setServerType(getServerType());
        data.setServerVersion((String) serverVersionComboBox.getSelectedItem());
        data.setDatabaseType(getDatabaseType());
        data.setDatabaseVersion((String) dbVersionComboBox.getSelectedItem());
        data.setDbName(dbNameTextField.getText());
        data.setDataSource(dsTextField.getText());
        data.setDbUserName(dbUserTextField.getText());
        data.setDbPassword(dbPasswordTextField.getText());
        DockerInstance dockerInstance = buildInstanceVisual.getInstance();
        if (dockerInstance!=null && dockerInstance.getKeyFile() != null) {
            data.setDockerMachine(dockerInstance.getKeyFile().getParentFile().getName());
        }
        data.setDockerActivated(dockerMachineCheckBox.isSelected());
        data.setDockerEnable(checkDockerStatus());
        PreferenceUtils.set(pref, this.getConfigData());
    }

    @Override
    public void init(String _package, Project project, SourceGroup sourceGroup) {
        pref = ProjectUtils.getPreferences(project, DockerConfigData.class, true);
    }

    private ServerType getServerType() {
        return (ServerType) serverComboBox.getSelectedItem();
    }

    private DatabaseType getDatabaseType() {
        return (DatabaseType) dbComboBox.getSelectedItem();
    }

    private void setDockerMachine(String machineName) {
        DockerSupport integration = DockerSupport.getDefault();
        integration.getInstances().stream().filter(inst -> inst.getKeyFile() != null && StringUtils.equals(machineName, inst.getKeyFile().getParentFile().getName()))
                .findAny().ifPresent(buildInstanceVisual::setInstance);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        warningPanel = new javax.swing.JPanel();
        warningLabel = new javax.swing.JLabel();
        panel = new javax.swing.JPanel();
        serverWrapperPanel = new javax.swing.JPanel();
        serverConfigPanel = new javax.swing.JLayeredPane();
        serverComboBox = new javax.swing.JComboBox<>();
        serverVersionLabel = new javax.swing.JLabel();
        serverVersionComboBox = new javax.swing.JComboBox<>();
        serverLabel = new javax.swing.JLabel();
        dbWrapperPanel = new javax.swing.JPanel();
        dbConfigPanel = new javax.swing.JLayeredPane();
        dbComboBox = new javax.swing.JComboBox<>();
        dbVersionLabel = new javax.swing.JLabel();
        dbVersionComboBox = new javax.swing.JComboBox<>();
        dbLabel = new javax.swing.JLabel();
        dbCredentialPanel = new javax.swing.JPanel();
        dbUserPanel = new javax.swing.JPanel();
        dbUserLabel = new javax.swing.JLabel();
        dbUserTextField = new javax.swing.JTextField();
        dbPasswordPanel = new javax.swing.JPanel();
        dbPasswordLabel = new javax.swing.JLabel();
        dbPasswordTextField = new javax.swing.JTextField();
        dsWrpperPanel = new javax.swing.JPanel();
        dbNamePanel = new javax.swing.JPanel();
        dbNameLabel = new javax.swing.JLabel();
        dbNameTextField = new javax.swing.JTextField();
        dsPanel = new javax.swing.JPanel();
        dsLabel = new javax.swing.JLabel();
        dsTextField = new javax.swing.JTextField();
        infoPanel = new javax.swing.JPanel();
        infoLabel = new javax.swing.JLabel();
        buildInstanceVisual = new org.netbeans.modules.docker.ui.build2.BuildInstanceVisual();
        dockerMachineCheckBox = new javax.swing.JCheckBox();

        warningPanel.setLayout(new java.awt.BorderLayout(10, 0));

        warningLabel.setForeground(new java.awt.Color(200, 0, 0));
        warningLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(warningLabel, org.openide.util.NbBundle.getMessage(DockerConfigPanel.class, "DockerConfigPanel.warningLabel.text")); // NOI18N
        warningPanel.add(warningLabel, java.awt.BorderLayout.CENTER);

        panel.setPreferredSize(new java.awt.Dimension(560, 25));
        panel.setLayout(new java.awt.GridLayout(5, 0, 0, 10));

        serverWrapperPanel.setLayout(new java.awt.BorderLayout(10, 0));

        serverConfigPanel.setLayout(new javax.swing.BoxLayout(serverConfigPanel, javax.swing.BoxLayout.LINE_AXIS));

        addChangeListener(serverComboBox);
        loadServerTypeModel();
        serverComboBox.setPreferredSize(new java.awt.Dimension(115, 35));
        serverComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                serverComboBoxActionPerformed(evt);
            }
        });
        serverConfigPanel.add(serverComboBox);

        serverVersionLabel.setForeground(javax.swing.UIManager.getDefaults().getColor("Button.shadow"));
        serverVersionLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(serverVersionLabel, org.openide.util.NbBundle.getMessage(DockerConfigPanel.class, "DockerConfigPanel.serverVersionLabel.text")); // NOI18N
        serverVersionLabel.setPreferredSize(new java.awt.Dimension(80, 27));
        serverVersionLabel.setRequestFocusEnabled(false);
        serverConfigPanel.add(serverVersionLabel);

        serverVersionComboBox.setPreferredSize(new java.awt.Dimension(80, 26));
        serverConfigPanel.add(serverVersionComboBox);

        serverWrapperPanel.add(serverConfigPanel, java.awt.BorderLayout.CENTER);

        org.openide.awt.Mnemonics.setLocalizedText(serverLabel, org.openide.util.NbBundle.getMessage(DockerConfigPanel.class, "DockerConfigPanel.serverLabel.text")); // NOI18N
        serverLabel.setPreferredSize(new java.awt.Dimension(78, 17));
        serverWrapperPanel.add(serverLabel, java.awt.BorderLayout.WEST);

        panel.add(serverWrapperPanel);

        dbWrapperPanel.setLayout(new java.awt.BorderLayout(10, 0));

        dbConfigPanel.setLayout(new javax.swing.BoxLayout(dbConfigPanel, javax.swing.BoxLayout.LINE_AXIS));

        loadDatabaseTypeModel();
        dbComboBox.setPreferredSize(new java.awt.Dimension(115, 35));
        dbComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dbComboBoxActionPerformed(evt);
            }
        });
        dbConfigPanel.add(dbComboBox);

        dbVersionLabel.setForeground(javax.swing.UIManager.getDefaults().getColor("Button.shadow"));
        dbVersionLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(dbVersionLabel, org.openide.util.NbBundle.getMessage(DockerConfigPanel.class, "DockerConfigPanel.dbVersionLabel.text")); // NOI18N
        dbVersionLabel.setPreferredSize(new java.awt.Dimension(80, 27));
        dbVersionLabel.setRequestFocusEnabled(false);
        dbConfigPanel.add(dbVersionLabel);

        dbVersionComboBox.setPreferredSize(new java.awt.Dimension(80, 26));
        dbConfigPanel.add(dbVersionComboBox);

        dbWrapperPanel.add(dbConfigPanel, java.awt.BorderLayout.CENTER);

        org.openide.awt.Mnemonics.setLocalizedText(dbLabel, org.openide.util.NbBundle.getMessage(DockerConfigPanel.class, "DockerConfigPanel.dbLabel.text")); // NOI18N
        dbLabel.setPreferredSize(new java.awt.Dimension(78, 17));
        dbWrapperPanel.add(dbLabel, java.awt.BorderLayout.WEST);

        panel.add(dbWrapperPanel);

        dbCredentialPanel.setPreferredSize(new java.awt.Dimension(603, 27));
        dbCredentialPanel.setLayout(new java.awt.GridLayout(1, 2, 25, 0));

        dbUserPanel.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(dbUserLabel, org.openide.util.NbBundle.getMessage(DockerConfigPanel.class, "DockerConfigPanel.dbUserLabel.text")); // NOI18N
        dbUserLabel.setPreferredSize(new java.awt.Dimension(87, 17));
        dbUserPanel.add(dbUserLabel, java.awt.BorderLayout.WEST);

        dbUserTextField.setText(org.openide.util.NbBundle.getMessage(DockerConfigPanel.class, "DockerConfigPanel.dbUserTextField.text")); // NOI18N
        dbUserTextField.setPreferredSize(new java.awt.Dimension(14, 27));
        dbUserPanel.add(dbUserTextField, java.awt.BorderLayout.CENTER);

        dbCredentialPanel.add(dbUserPanel);

        dbPasswordPanel.setLayout(new java.awt.BorderLayout());

        dbPasswordLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(dbPasswordLabel, org.openide.util.NbBundle.getMessage(DockerConfigPanel.class, "DockerConfigPanel.dbPasswordLabel.text")); // NOI18N
        dbPasswordLabel.setPreferredSize(new java.awt.Dimension(90, 17));
        dbPasswordPanel.add(dbPasswordLabel, java.awt.BorderLayout.WEST);

        dbPasswordTextField.setText(org.openide.util.NbBundle.getMessage(DockerConfigPanel.class, "DockerConfigPanel.dbPasswordTextField.text")); // NOI18N
        dbPasswordTextField.setPreferredSize(new java.awt.Dimension(14, 27));
        dbPasswordPanel.add(dbPasswordTextField, java.awt.BorderLayout.CENTER);

        dbCredentialPanel.add(dbPasswordPanel);

        panel.add(dbCredentialPanel);

        dsWrpperPanel.setLayout(new java.awt.GridLayout(1, 2, 25, 0));

        dbNamePanel.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(dbNameLabel, org.openide.util.NbBundle.getMessage(DockerConfigPanel.class, "DockerConfigPanel.dbNameLabel.text")); // NOI18N
        dbNameLabel.setPreferredSize(new java.awt.Dimension(87, 17));
        dbNamePanel.add(dbNameLabel, java.awt.BorderLayout.WEST);

        dbNameTextField.setText(org.openide.util.NbBundle.getMessage(DockerConfigPanel.class, "DockerConfigPanel.dbNameTextField.text")); // NOI18N
        dbNameTextField.setPreferredSize(new java.awt.Dimension(14, 27));
        dbNamePanel.add(dbNameTextField, java.awt.BorderLayout.CENTER);

        dsWrpperPanel.add(dbNamePanel);

        dsPanel.setLayout(new java.awt.BorderLayout());

        dsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(dsLabel, org.openide.util.NbBundle.getMessage(DockerConfigPanel.class, "DockerConfigPanel.dsLabel.text")); // NOI18N
        dsLabel.setPreferredSize(new java.awt.Dimension(90, 17));
        dsPanel.add(dsLabel, java.awt.BorderLayout.WEST);

        dsTextField.setText(org.openide.util.NbBundle.getMessage(DockerConfigPanel.class, "DockerConfigPanel.dsTextField.text")); // NOI18N
        dsTextField.setPreferredSize(new java.awt.Dimension(14, 27));
        dsTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dsTextFieldActionPerformed(evt);
            }
        });
        dsPanel.add(dsTextField, java.awt.BorderLayout.CENTER);

        dsWrpperPanel.add(dsPanel);

        panel.add(dsWrpperPanel);

        infoPanel.setLayout(new java.awt.BorderLayout(10, 0));

        infoLabel.setForeground(new java.awt.Color(102, 0, 255));
        infoLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(infoLabel, org.openide.util.NbBundle.getMessage(DockerConfigPanel.class, "DockerConfigPanel.infoLabel.text")); // NOI18N
        infoPanel.add(infoLabel, java.awt.BorderLayout.CENTER);

        Component buildInstanceLabel = buildInstanceVisual.getComponent(0);
        if(buildInstanceLabel instanceof JLabel){
            ((JLabel) buildInstanceLabel).setText("Docker host :");
        }
        Component buildInstanceCombo = buildInstanceVisual.getComponent(1);
        if(buildInstanceCombo instanceof JComboBox){
            ((JComboBox)buildInstanceCombo).addActionListener(e -> checkDockerStatus());
        }

        addChangeListener(dockerMachineCheckBox);
        dockerMachineCheckBox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(dockerMachineCheckBox, org.openide.util.NbBundle.getMessage(DockerConfigPanel.class, "DockerConfigPanel.dockerMachineCheckBox.text")); // NOI18N
        dockerMachineCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dockerMachineCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, 594, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(buildInstanceVisual, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(dockerMachineCheckBox)))
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(warningPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 594, Short.MAX_VALUE)
                    .addContainerGap()))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(infoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 594, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(buildInstanceVisual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dockerMachineCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(146, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(310, Short.MAX_VALUE)
                    .addComponent(warningPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(283, Short.MAX_VALUE)
                    .addComponent(infoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(31, 31, 31)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void serverComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_serverComboBoxActionPerformed
        setVisibility(dockerMachineCheckBox.isSelected());//getServerType() != ServerType.NONE);
        checkDockerStatus();
        loadServerVersionModel();
        loadDatabaseTypeModel();
    }//GEN-LAST:event_serverComboBoxActionPerformed

    private void dbComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dbComboBoxActionPerformed
        loadDatabaseVersionModel();
    }//GEN-LAST:event_dbComboBoxActionPerformed

    private void dsTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dsTextFieldActionPerformed

    }//GEN-LAST:event_dsTextFieldActionPerformed

    private void dockerMachineCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dockerMachineCheckBoxActionPerformed
       checkDockerStatus();
       loadDatabaseTypeModel();
       setVisibility(dockerMachineCheckBox.isSelected());
       Arrays.stream(buildInstanceVisual.getComponents()).forEach(c-> c.setEnabled(dockerMachineCheckBox.isSelected()));//Docker Machine
    }//GEN-LAST:event_dockerMachineCheckBoxActionPerformed

    private void setVisibility(boolean status) {
        if (dsWrpperPanel.isVisible() != status) {
//            dbWrapperPanel.setVisible(status);
            dsWrpperPanel.setVisible(status);
            dbCredentialPanel.setVisible(status);
        }
    }
    
    private boolean checkDockerStatus(){
        if(!dockerMachineCheckBox.isSelected()){
             infoLabel.setText(getMessage(DockerConfigPanel.class, "DOCKER_DISABLED_MESSAGE"));
        } else if(buildInstanceVisual.getInstance()==null){
             infoLabel.setText(getMessage(DockerConfigPanel.class, "DOCKER_DISABLED", getMessage(DockerConfigPanel.class, "DOKER_MACHINE_REQUIRED")));
        } else if(getServerType()==null || getServerType()==NONE){
             infoLabel.setText(getMessage(DockerConfigPanel.class, "DOCKER_DISABLED", getMessage(DockerConfigPanel.class, "SERVER_REQUIRED")));
        } else {
            infoLabel.setText("");
            return true;
        }
        return false;
    }
    
    private void loadServerTypeModel(){
        serverComboBox.setModel(new DefaultComboBoxModel(Stream.of(ServerType.values()).toArray(ServerType[]::new)));
}
    
    private void loadDatabaseTypeModel(){
        dbComboBox.setModel(
                new DefaultComboBoxModel(Stream.of(DatabaseType.values())
                        .filter(type -> type.isDockerSupport() || !dockerMachineCheckBox.isSelected())
                        .filter(type -> type.isServerSupported(getServerType()))
                        .toArray(DatabaseType[]::new))
        );
        loadDatabaseVersionModel();
    }
    
    private void loadDatabaseVersionModel() {
        dbVersionComboBox.removeAllItems();
        dbVersionComboBox.setModel(new DefaultComboBoxModel(getDatabaseType().getVersion().stream().toArray(String[]::new)));
    }
    
    private void loadServerVersionModel() {
        serverVersionComboBox.removeAllItems();
        serverVersionComboBox.setModel(new DefaultComboBoxModel(getServerType().getVersion().stream().toArray(String[]::new)));
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.netbeans.modules.docker.ui.build2.BuildInstanceVisual buildInstanceVisual;
    private javax.swing.JComboBox<DatabaseType> dbComboBox;
    private javax.swing.JLayeredPane dbConfigPanel;
    private javax.swing.JPanel dbCredentialPanel;
    private javax.swing.JLabel dbLabel;
    private javax.swing.JLabel dbNameLabel;
    private javax.swing.JPanel dbNamePanel;
    private javax.swing.JTextField dbNameTextField;
    private javax.swing.JLabel dbPasswordLabel;
    private javax.swing.JPanel dbPasswordPanel;
    private javax.swing.JTextField dbPasswordTextField;
    private javax.swing.JLabel dbUserLabel;
    private javax.swing.JPanel dbUserPanel;
    private javax.swing.JTextField dbUserTextField;
    private javax.swing.JComboBox<String> dbVersionComboBox;
    private javax.swing.JLabel dbVersionLabel;
    private javax.swing.JPanel dbWrapperPanel;
    private javax.swing.JCheckBox dockerMachineCheckBox;
    private javax.swing.JLabel dsLabel;
    private javax.swing.JPanel dsPanel;
    private javax.swing.JTextField dsTextField;
    private javax.swing.JPanel dsWrpperPanel;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JPanel infoPanel;
    private javax.swing.JPanel panel;
    private javax.swing.JComboBox<ServerType> serverComboBox;
    private javax.swing.JLayeredPane serverConfigPanel;
    private javax.swing.JLabel serverLabel;
    private javax.swing.JComboBox<String> serverVersionComboBox;
    private javax.swing.JLabel serverVersionLabel;
    private javax.swing.JPanel serverWrapperPanel;
    private javax.swing.JLabel warningLabel;
    private javax.swing.JPanel warningPanel;
    // End of variables declaration//GEN-END:variables

}
