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

import org.jcode.infra.ServerType;
import org.jcode.infra.DatabaseType;
import java.awt.CardLayout;
import java.awt.Component;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import org.netbeans.modules.docker.api.DockerInstance;
import org.apache.commons.lang.StringUtils;
import static org.jcode.infra.ServerType.NONE;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.jcode.stack.config.panel.LayerConfigPanel;
import org.netbeans.modules.docker.api.DockerSupport;
import static org.openide.util.NbBundle.getMessage;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.netbeans.api.db.explorer.support.DatabaseExplorerUIs;
import org.netbeans.jcode.core.util.JdbcUrl;

/**
 *
 * @author Gaurav Gupta
 */
public class DockerConfigPanel extends LayerConfigPanel<DockerConfigData> {

    public DockerConfigPanel() {
        initComponents();
        DatabaseExplorerUIs.connect(dbConnectionComboBox, ConnectionManager.getDefault());
    }

    @Override
    public boolean hasError() {
        warningLabel.setText("");
        if (getServerType() == null || getServerType() == NONE) {
            warningLabel.setText(getMessage(DockerConfigPanel.class, "DockerConfigPanel.serverRequired.message"));
            return true;
        }
        if (dockerMachineCheckBox.isSelected()) {
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
            if (StringUtils.isBlank(namespaceTextField.getText())) {
                warningLabel.setText(getMessage(DockerConfigPanel.class, "DockerConfigPanel.invalidNamespace.message"));
                return true;
            }
            if (StringUtils.isBlank(repositoryTextField.getText())) {
                warningLabel.setText(getMessage(DockerConfigPanel.class, "DockerConfigPanel.invalidRepository.message"));
                return true;
            }
        } else {
            Optional<DatabaseConnection> databaseConnection = getDatabaseConnection();
            if (databaseConnection.isPresent()) {
                if (!getDatabaseType().isMatchingDatabase(databaseConnection.get())) {
                    warningLabel.setText(getMessage(DockerConfigPanel.class, "CON_DIF_TYPE"));
                    return true;
                }
            }
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

    private Optional<DatabaseConnection> getDatabaseConnection() {
        if (dbConnectionComboBox.getSelectedItem() instanceof org.netbeans.api.db.explorer.DatabaseConnection) {
            return Optional.of((DatabaseConnection) dbConnectionComboBox.getSelectedItem());
        }
        return Optional.empty();
    }

    private boolean validateDB() {
        if (dockerMachineCheckBox.isSelected() && getDatabaseType() == DatabaseType.MYSQL) {
            if (StringUtils.equals(dbUserTextField.getText(), "root")) {
                warningLabel.setText(getMessage(DockerConfigPanel.class, "MYSQL_ROOT_USER_EXIST"));
                return false;
            }
        }
        return true;
    }

    @Override
    public void read() {
        
        DockerConfigData data = this.getConfigData();
        if (data.getServerType() != null) {
            serverComboBox.setSelectedItem(data.getServerType());
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
        
        if (StringUtils.isNotBlank(data.getDockerNamespace())) {
            namespaceTextField.setText(data.getDockerNamespace());
        }
        if (StringUtils.isNotBlank(data.getDockerRepository())) {
            repositoryTextField.setText(data.getDockerRepository());
        }
        dockerMachineCheckBox.setSelected(data.isDockerActivated());
        if (data.isDockerActivated() && StringUtils.isNotEmpty(data.getDockerMachine())) {
            setDockerMachine(data.getDockerMachine());
        }
        dockerMachineCheckBoxActionPerformed(null);//automates serverComboBoxActionPerformed(null);
        
        if (data.getDatabaseType() != null) {
            dbComboBox.setSelectedItem(data.getDatabaseType());
        }
        selectDBConnection();
    }
    
    private void selectDBConnection(){
        DockerConfigData data = this.getConfigData();
        for (int i = 0; i < dbConnectionComboBox.getItemCount(); i++) {
            Object dbObj = dbConnectionComboBox.getItemAt(i);
            if (dbObj instanceof org.netbeans.api.db.explorer.DatabaseConnection) {
                org.netbeans.api.db.explorer.DatabaseConnection databaseConnection = (DatabaseConnection) dbObj;
                String name = JdbcUrl.getDatabaseName(databaseConnection.getDatabaseURL());
                String user = databaseConnection.getUser();
                String password = databaseConnection.getPassword();
                String host = JdbcUrl.getHostName(databaseConnection.getDatabaseURL());
                String port = String.valueOf(JdbcUrl.getPort(databaseConnection.getDatabaseURL()));
                if(StringUtils.equals(name, data.getDbName()) 
                        && StringUtils.equals(user, data.getDbUserName())
                        && StringUtils.equals(password, data.getDbPassword())
                        && StringUtils.equals(host, data.getDbHost())
                        && StringUtils.equals(port, data.getDbPort())){
                    dbConnectionComboBox.setSelectedItem(databaseConnection);
                }
            }
        }
    }

    @Override
    public void store() {
        DockerConfigData data = this.getConfigData();
        data.setServerType(getServerType());
        data.setDatabaseType(getDatabaseType());
        data.setDatabaseVersion((String) dbVersionComboBox.getSelectedItem());
        data.setDbName(null);
        if (dockerMachineCheckBox.isSelected()) {
            data.setDockerNamespace(namespaceTextField.getText().trim());
            data.setDockerRepository(repositoryTextField.getText().trim());
            DockerInstance dockerInstance = buildInstanceVisual.getInstance();
            if (dockerInstance != null && dockerInstance.getKeyFile() != null) {
                data.setDockerMachine(dockerInstance.getKeyFile().getParentFile().getName());
            }
            data.setDbName(dbNameTextField.getText());
            data.setDbUserName(dbUserTextField.getText());
            data.setDbPassword(dbPasswordTextField.getText());
            data.setDbHost(null);
            data.setDbPort(getDatabaseType().getDefaultPort());
        } else {
            Optional<DatabaseConnection> databaseConnection = getDatabaseConnection();
            if (databaseConnection.isPresent()) {
                data.setDbName(JdbcUrl.getDatabaseName(databaseConnection.get().getDatabaseURL()));
                data.setDbUserName(databaseConnection.get().getUser());
                data.setDbPassword(databaseConnection.get().getPassword());
                data.setDbHost(JdbcUrl.getHostName(databaseConnection.get().getDatabaseURL()));
                data.setDbPort(String.valueOf(JdbcUrl.getPort(databaseConnection.get().getDatabaseURL())));
            }
        }
        data.setDataSource(dsTextField.getText());
        data.setDockerActivated(isDockerActivated());
        data.setDockerEnable(checkDockerStatus());
    }
    
    public boolean isDockerActivated(){
        return dockerMachineCheckBox.isSelected();
    }
    
    public void activateDocker(){
        dockerMachineCheckBox.setSelected(true);
        dockerMachineCheckBoxActionPerformed(null);
    }

    @Override
    public void init(String _package, Project project, SourceGroup sourceGroup) {
        
    }

    private ServerType getServerType() {
        return (ServerType) serverComboBox.getSelectedItem();
    }

    private DatabaseType getDatabaseType() {
        return (DatabaseType) dbComboBox.getSelectedItem();
    }

    private void setDockerMachine(String machineName) {
        DockerSupport integration = DockerSupport.getDefault();
        integration.getInstances()
                .stream()
                .filter(inst -> inst.getKeyFile() != null && StringUtils.equals(machineName, inst.getKeyFile().getParentFile().getName()))
                .findAny()
                .ifPresent(buildInstanceVisual::setInstance);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLayeredPane8 = new javax.swing.JLayeredPane();
        dovckubeLayeredPane = new javax.swing.JLayeredPane();
        dockerMachineCheckBox = new javax.swing.JCheckBox();
        buildInstanceVisual = new org.netbeans.modules.docker.ui.build2.BuildInstanceVisual();
        dockerImageLayeredPane = new javax.swing.JLayeredPane();
        dockerImageLabel = new javax.swing.JLabel();
        dockerImageSubLayeredPane = new javax.swing.JLayeredPane();
        namespaceTextField = new javax.swing.JTextField();
        groupArtifactSpeperatorLabel = new javax.swing.JLabel();
        repositoryTextField = new javax.swing.JTextField();
        jLayeredPane4 = new javax.swing.JLayeredPane();
        serverWrapperPanel = new javax.swing.JPanel();
        serverComboBox = new javax.swing.JComboBox<>();
        serverLabel = new javax.swing.JLabel();
        paddingLayeredPane = new javax.swing.JLayeredPane();
        dbWrapperPanel = new javax.swing.JPanel();
        dbLabel = new javax.swing.JLabel();
        dbComboBox = new javax.swing.JComboBox<>();
        dbVersionComboBox = new javax.swing.JComboBox<>();
        dbDataPanel = new javax.swing.JPanel();
        dbCredentialPanel = new javax.swing.JPanel();
        dbUserPanel = new javax.swing.JPanel();
        dbUserLabel = new javax.swing.JLabel();
        dbUserTextField = new javax.swing.JTextField();
        dbPasswordPanel = new javax.swing.JPanel();
        dbPasswordLabel = new javax.swing.JLabel();
        dbPasswordTextField = new javax.swing.JTextField();
        dbConnectionWrapperPanel = new javax.swing.JPanel();
        dbConnectionComboBox = new javax.swing.JComboBox();
        dbConnectionLabel = new javax.swing.JLabel();
        jLayeredPane2 = new javax.swing.JLayeredPane();
        reloadConnectionButton = new javax.swing.JButton();
        dsWrpperPanel = new javax.swing.JPanel();
        dsPanel = new javax.swing.JPanel();
        dsLabel = new javax.swing.JLabel();
        dsTextField = new javax.swing.JTextField();
        dbNamePanel = new javax.swing.JPanel();
        dbNameLabel = new javax.swing.JLabel();
        dbNameTextField = new javax.swing.JTextField();
        infoPanel = new javax.swing.JPanel();
        infoLabel = new javax.swing.JLabel();
        warningPanel = new javax.swing.JPanel();
        warningLabel = new javax.swing.JLabel();

        jLayeredPane8.setPreferredSize(new java.awt.Dimension(400, 140));
        jLayeredPane8.setLayout(new java.awt.GridLayout(7, 1, 0, 12));

        dovckubeLayeredPane.setPreferredSize(new java.awt.Dimension(706, 28));
        dovckubeLayeredPane.setLayout(new java.awt.BorderLayout());

        addChangeListener(dockerMachineCheckBox);
        dockerMachineCheckBox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(dockerMachineCheckBox, org.openide.util.NbBundle.getMessage(DockerConfigPanel.class, "DockerConfigPanel.dockerMachineCheckBox.text")); // NOI18N
        dockerMachineCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dockerMachineCheckBoxActionPerformed(evt);
            }
        });
        dovckubeLayeredPane.add(dockerMachineCheckBox, java.awt.BorderLayout.EAST);

        Component buildInstanceLabel = buildInstanceVisual.getComponent(0);
        if(buildInstanceLabel instanceof JLabel){
            //((JLabel) buildInstanceLabel).setText("Docker host :       ");
            ((JLabel) buildInstanceLabel).setText("Docker machine : ");
        }
        Component buildInstanceCombo = buildInstanceVisual.getComponent(1);
        if(buildInstanceCombo instanceof JComboBox){
            ((JComboBox)buildInstanceCombo).addActionListener(e -> checkDockerStatus());
            //((JComboBox)buildInstanceCombo).setEditable(true);
        }
        dovckubeLayeredPane.add(buildInstanceVisual, java.awt.BorderLayout.CENTER);

        jLayeredPane8.add(dovckubeLayeredPane);

        dockerImageLayeredPane.setMinimumSize(new java.awt.Dimension(73, 20));
        dockerImageLayeredPane.setPreferredSize(new java.awt.Dimension(608, 20));
        dockerImageLayeredPane.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(dockerImageLabel, org.openide.util.NbBundle.getMessage(DockerConfigPanel.class, "DockerConfigPanel.dockerImageLabel.text")); // NOI18N
        dockerImageLabel.setMaximumSize(new java.awt.Dimension(98, 17));
        dockerImageLabel.setMinimumSize(new java.awt.Dimension(48, 17));
        dockerImageLabel.setPreferredSize(new java.awt.Dimension(88, 17));
        dockerImageLayeredPane.add(dockerImageLabel, java.awt.BorderLayout.WEST);

        dockerImageSubLayeredPane.setMinimumSize(new java.awt.Dimension(6, 20));
        dockerImageSubLayeredPane.setPreferredSize(new java.awt.Dimension(54, 20));
        dockerImageSubLayeredPane.setLayout(new javax.swing.BoxLayout(dockerImageSubLayeredPane, javax.swing.BoxLayout.LINE_AXIS));

        namespaceTextField.setText(org.openide.util.NbBundle.getMessage(DockerConfigPanel.class, "DockerConfigPanel.namespaceTextField.text")); // NOI18N
        namespaceTextField.setToolTipText(org.openide.util.NbBundle.getMessage(DockerConfigPanel.class, "DockerConfigPanel.namespaceTextField.toolTipText")); // NOI18N
        namespaceTextField.setPreferredSize(new java.awt.Dimension(10, 20));
        namespaceTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                namespaceTextFieldActionPerformed(evt);
            }
        });
        dockerImageSubLayeredPane.add(namespaceTextField);

        groupArtifactSpeperatorLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(groupArtifactSpeperatorLabel, org.openide.util.NbBundle.getMessage(DockerConfigPanel.class, "DockerConfigPanel.groupArtifactSpeperatorLabel.text")); // NOI18N
        dockerImageSubLayeredPane.add(groupArtifactSpeperatorLabel);

        repositoryTextField.setText(org.openide.util.NbBundle.getMessage(DockerConfigPanel.class, "DockerConfigPanel.repositoryTextField.text")); // NOI18N
        repositoryTextField.setToolTipText(org.openide.util.NbBundle.getMessage(DockerConfigPanel.class, "DockerConfigPanel.repositoryTextField.toolTipText")); // NOI18N
        repositoryTextField.setPreferredSize(new java.awt.Dimension(10, 20));
        dockerImageSubLayeredPane.add(repositoryTextField);

        dockerImageLayeredPane.add(dockerImageSubLayeredPane, java.awt.BorderLayout.CENTER);

        jLayeredPane8.add(dockerImageLayeredPane);

        javax.swing.GroupLayout jLayeredPane4Layout = new javax.swing.GroupLayout(jLayeredPane4);
        jLayeredPane4.setLayout(jLayeredPane4Layout);
        jLayeredPane4Layout.setHorizontalGroup(
            jLayeredPane4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 565, Short.MAX_VALUE)
        );
        jLayeredPane4Layout.setVerticalGroup(
            jLayeredPane4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 22, Short.MAX_VALUE)
        );

        jLayeredPane8.add(jLayeredPane4);

        serverWrapperPanel.setLayout(new java.awt.BorderLayout(10, 0));

        addChangeListener(serverComboBox);
        loadServerTypeModel();
        serverComboBox.setPreferredSize(new java.awt.Dimension(115, 35));
        serverComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                serverComboBoxActionPerformed(evt);
            }
        });
        serverWrapperPanel.add(serverComboBox, java.awt.BorderLayout.CENTER);

        org.openide.awt.Mnemonics.setLocalizedText(serverLabel, org.openide.util.NbBundle.getMessage(DockerConfigPanel.class, "DockerConfigPanel.serverLabel.text")); // NOI18N
        serverLabel.setPreferredSize(new java.awt.Dimension(78, 17));
        serverWrapperPanel.add(serverLabel, java.awt.BorderLayout.WEST);

        paddingLayeredPane.setPreferredSize(new java.awt.Dimension(86, 10));

        javax.swing.GroupLayout paddingLayeredPaneLayout = new javax.swing.GroupLayout(paddingLayeredPane);
        paddingLayeredPane.setLayout(paddingLayeredPaneLayout);
        paddingLayeredPaneLayout.setHorizontalGroup(
            paddingLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 86, Short.MAX_VALUE)
        );
        paddingLayeredPaneLayout.setVerticalGroup(
            paddingLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        serverWrapperPanel.add(paddingLayeredPane, java.awt.BorderLayout.EAST);

        jLayeredPane8.add(serverWrapperPanel);

        dbWrapperPanel.setMinimumSize(new java.awt.Dimension(59, 34));
        dbWrapperPanel.setPreferredSize(new java.awt.Dimension(184, 52));
        dbWrapperPanel.setLayout(new java.awt.BorderLayout(10, 0));

        org.openide.awt.Mnemonics.setLocalizedText(dbLabel, org.openide.util.NbBundle.getMessage(DockerConfigPanel.class, "DockerConfigPanel.dbLabel.text")); // NOI18N
        dbLabel.setMaximumSize(new java.awt.Dimension(39, 14));
        dbLabel.setMinimumSize(new java.awt.Dimension(39, 14));
        dbLabel.setPreferredSize(new java.awt.Dimension(78, 17));
        dbWrapperPanel.add(dbLabel, java.awt.BorderLayout.WEST);

        dbComboBox.setPreferredSize(new java.awt.Dimension(115, 35));
        dbComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dbComboBoxActionPerformed(evt);
            }
        });
        dbWrapperPanel.add(dbComboBox, java.awt.BorderLayout.CENTER);

        dbVersionComboBox.setMinimumSize(new java.awt.Dimension(8, 20));
        dbVersionComboBox.setName(""); // NOI18N
        dbVersionComboBox.setPreferredSize(new java.awt.Dimension(86, 26));
        dbWrapperPanel.add(dbVersionComboBox, java.awt.BorderLayout.LINE_END);

        jLayeredPane8.add(dbWrapperPanel);

        dbDataPanel.setPreferredSize(new java.awt.Dimension(603, 27));
        dbDataPanel.setLayout(new java.awt.CardLayout());

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

        dbDataPanel.add(dbCredentialPanel, "DB_MANUAL");

        dbConnectionWrapperPanel.setLayout(new java.awt.BorderLayout(10, 0));

        addChangeListener(serverComboBox);
        dbConnectionComboBox.setPreferredSize(new java.awt.Dimension(115, 35));
        dbConnectionWrapperPanel.add(dbConnectionComboBox, java.awt.BorderLayout.CENTER);

        org.openide.awt.Mnemonics.setLocalizedText(dbConnectionLabel, org.openide.util.NbBundle.getMessage(DockerConfigPanel.class, "DockerConfigPanel.dbConnectionLabel.text")); // NOI18N
        dbConnectionLabel.setPreferredSize(new java.awt.Dimension(78, 17));
        dbConnectionWrapperPanel.add(dbConnectionLabel, java.awt.BorderLayout.WEST);

        jLayeredPane2.setPreferredSize(new java.awt.Dimension(86, 10));

        reloadConnectionButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jcode/docker/resources/refresh.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(reloadConnectionButton, org.openide.util.NbBundle.getMessage(DockerConfigPanel.class, "DockerConfigPanel.reloadConnectionButton.text")); // NOI18N
        reloadConnectionButton.setBorder(null);
        reloadConnectionButton.setContentAreaFilled(false);
        reloadConnectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reloadConnectionButtonActionPerformed(evt);
            }
        });

        jLayeredPane2.setLayer(reloadConnectionButton, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane2Layout = new javax.swing.GroupLayout(jLayeredPane2);
        jLayeredPane2.setLayout(jLayeredPane2Layout);
        jLayeredPane2Layout.setHorizontalGroup(
            jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane2Layout.createSequentialGroup()
                .addComponent(reloadConnectionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 60, Short.MAX_VALUE))
        );
        jLayeredPane2Layout.setVerticalGroup(
            jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(reloadConnectionButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        dbConnectionWrapperPanel.add(jLayeredPane2, java.awt.BorderLayout.EAST);

        dbDataPanel.add(dbConnectionWrapperPanel, "DB_AUTO");

        jLayeredPane8.add(dbDataPanel);

        dsWrpperPanel.setLayout(new java.awt.GridLayout(1, 2, 25, 0));

        dsPanel.setLayout(new java.awt.BorderLayout());

        dsLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        org.openide.awt.Mnemonics.setLocalizedText(dsLabel, org.openide.util.NbBundle.getMessage(DockerConfigPanel.class, "DockerConfigPanel.dsLabel.text")); // NOI18N
        dsLabel.setPreferredSize(new java.awt.Dimension(87, 17));
        dsPanel.add(dsLabel, java.awt.BorderLayout.WEST);

        dsTextField.setText(org.openide.util.NbBundle.getMessage(DockerConfigPanel.class, "DockerConfigPanel.dsTextField.text")); // NOI18N
        dsTextField.setPreferredSize(new java.awt.Dimension(14, 27));
        dsPanel.add(dsTextField, java.awt.BorderLayout.CENTER);

        dsWrpperPanel.add(dsPanel);

        dbNamePanel.setLayout(new java.awt.BorderLayout());

        dbNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(dbNameLabel, org.openide.util.NbBundle.getMessage(DockerConfigPanel.class, "DockerConfigPanel.dbNameLabel.text")); // NOI18N
        dbNameLabel.setPreferredSize(new java.awt.Dimension(90, 17));
        dbNamePanel.add(dbNameLabel, java.awt.BorderLayout.WEST);

        dbNameTextField.setText(org.openide.util.NbBundle.getMessage(DockerConfigPanel.class, "DockerConfigPanel.dbNameTextField.text")); // NOI18N
        dbNameTextField.setPreferredSize(new java.awt.Dimension(14, 27));
        dbNamePanel.add(dbNameTextField, java.awt.BorderLayout.CENTER);

        dsWrpperPanel.add(dbNamePanel);

        jLayeredPane8.add(dsWrpperPanel);

        infoPanel.setLayout(new java.awt.GridLayout(1, 0));

        infoLabel.setForeground(new java.awt.Color(102, 0, 255));
        infoLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(infoLabel, org.openide.util.NbBundle.getMessage(DockerConfigPanel.class, "DockerConfigPanel.infoLabel.text")); // NOI18N
        infoPanel.add(infoLabel);

        warningPanel.setLayout(new java.awt.GridLayout(1, 0));

        warningLabel.setForeground(new java.awt.Color(200, 0, 0));
        warningLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(warningLabel, org.openide.util.NbBundle.getMessage(DockerConfigPanel.class, "DockerConfigPanel.warningLabel.text")); // NOI18N
        warningPanel.add(warningLabel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(infoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(warningPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLayeredPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 565, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLayeredPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addComponent(infoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(warningPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void serverComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_serverComboBoxActionPerformed
        setVisibility(dockerMachineCheckBox.isSelected());//getServerType() != ServerType.NONE);
        checkDockerStatus();
        loadDatabaseTypeModel();
    }//GEN-LAST:event_serverComboBoxActionPerformed

    private void dbComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dbComboBoxActionPerformed
        loadDatabaseVersionModel();
        boolean embedded = getDatabaseType().isEmbeddedDB();
        dbDataPanel.setVisible(!embedded);
        dsWrpperPanel.setVisible(!embedded);
    }//GEN-LAST:event_dbComboBoxActionPerformed

    private void dockerMachineCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dockerMachineCheckBoxActionPerformed
        setVisibility(dockerMachineCheckBox.isSelected());
        checkDockerStatus();
        loadDatabaseTypeModel();
    }//GEN-LAST:event_dockerMachineCheckBoxActionPerformed
    
    private void reloadConnectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadConnectionButtonActionPerformed
        dbConnectionComboBox.setSelectedItem(null);
        dbConnectionComboBox.updateUI();
    }//GEN-LAST:event_reloadConnectionButtonActionPerformed

    private void namespaceTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_namespaceTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_namespaceTextFieldActionPerformed

    private void setVisibility(boolean status) {
        if (dbNamePanel.isVisible() != status) {
            dbVersionComboBox.setEnabled(status);
            dbNamePanel.setVisible(status);
            CardLayout dbDataPanelLayout = (CardLayout) dbDataPanel.getLayout();
            dbDataPanelLayout.show(dbDataPanel, status ? "DB_MANUAL" : "DB_AUTO");//uncomment for db connection - non-docker
        }
        
        namespaceTextField.setEnabled(dockerMachineCheckBox.isSelected());
        repositoryTextField.setEnabled(dockerMachineCheckBox.isSelected());
        groupArtifactSpeperatorLabel.setEnabled(dockerMachineCheckBox.isSelected());
        dockerImageLabel.setEnabled(dockerMachineCheckBox.isSelected());
        Arrays.stream(buildInstanceVisual.getComponents())
                .forEach(c -> c.setEnabled(dockerMachineCheckBox.isSelected()));//Docker Machine
    }

    private boolean checkDockerStatus() {
        if (!dockerMachineCheckBox.isSelected()) {
            infoLabel.setText(getMessage(DockerConfigPanel.class, "DOCKER_DISABLED_MESSAGE"));
//        } else if (buildInstanceVisual.getInstance() == null) {
//            infoLabel.setText(getMessage(DockerConfigPanel.class, "DOCKER_DISABLED", getMessage(DockerConfigPanel.class, "DOKER_MACHINE_REQUIRED")));
        } else if (getServerType() == null || getServerType() == NONE) {
            infoLabel.setText(getMessage(DockerConfigPanel.class, "DOCKER_DISABLED", getMessage(DockerConfigPanel.class, "SERVER_REQUIRED")));
        } else {
            infoLabel.setText("");
            return true;
        }
        return false;
    }

    private void loadServerTypeModel() {
        serverComboBox.setModel(new DefaultComboBoxModel(
                Stream.of(ServerType.values())
                        .filter(ServerType::isVisible)
                        .toArray(ServerType[]::new))
        );
    }

    private void loadDatabaseTypeModel() {
        dbComboBox.setModel(
                new DefaultComboBoxModel(Stream.of(DatabaseType.values())
                        .filter(type -> type.isDockerSupport() || !dockerMachineCheckBox.isSelected())
                        .filter(type -> !type.isEmbeddedDB() || (type.isEmbeddedDB() && getServerType().getEmbeddedDB() == type))
                        //                        .sorted((t1,t2)-> getServerType().getEmbeddedDB() == t1?-1:(getServerType().getEmbeddedDB() == t2?1:0))
                        .toArray(DatabaseType[]::new))
        );
        dbComboBoxActionPerformed(null);
    }

    private void loadDatabaseVersionModel() {
        dbVersionComboBox.removeAllItems();
        dbVersionComboBox.setModel(new DefaultComboBoxModel(getDatabaseType().getVersion().stream().toArray(String[]::new)));
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.netbeans.modules.docker.ui.build2.BuildInstanceVisual buildInstanceVisual;
    private javax.swing.JComboBox<DatabaseType> dbComboBox;
    private javax.swing.JComboBox dbConnectionComboBox;
    private javax.swing.JLabel dbConnectionLabel;
    private javax.swing.JPanel dbConnectionWrapperPanel;
    private javax.swing.JPanel dbCredentialPanel;
    private javax.swing.JPanel dbDataPanel;
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
    private javax.swing.JPanel dbWrapperPanel;
    private javax.swing.JLabel dockerImageLabel;
    private javax.swing.JLayeredPane dockerImageLayeredPane;
    private javax.swing.JLayeredPane dockerImageSubLayeredPane;
    private javax.swing.JCheckBox dockerMachineCheckBox;
    private javax.swing.JLayeredPane dovckubeLayeredPane;
    private javax.swing.JLabel dsLabel;
    private javax.swing.JPanel dsPanel;
    private javax.swing.JTextField dsTextField;
    private javax.swing.JPanel dsWrpperPanel;
    private javax.swing.JLabel groupArtifactSpeperatorLabel;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JPanel infoPanel;
    private javax.swing.JLayeredPane jLayeredPane2;
    private javax.swing.JLayeredPane jLayeredPane4;
    private javax.swing.JLayeredPane jLayeredPane8;
    private javax.swing.JTextField namespaceTextField;
    private javax.swing.JLayeredPane paddingLayeredPane;
    private javax.swing.JButton reloadConnectionButton;
    private javax.swing.JTextField repositoryTextField;
    private javax.swing.JComboBox<ServerType> serverComboBox;
    private javax.swing.JLabel serverLabel;
    private javax.swing.JPanel serverWrapperPanel;
    private javax.swing.JLabel warningLabel;
    private javax.swing.JPanel warningPanel;
    // End of variables declaration//GEN-END:variables

}
