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
package org.jcode.cloud.generator;

import org.jcode.docker.generator.DockerConfigPanel;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.jcode.layer.ConfigData;
import org.netbeans.jcode.stack.config.panel.LayerConfigPanel;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import static org.openide.util.NbBundle.getMessage;

/**
 *
 * @author Gaurav Gupta
 */
public class CloudConfigPanel extends LayerConfigPanel<CloudConfigData> {

    @ConfigData
    private DockerConfigPanel dockerConfigPanel;

    private boolean cloudEnable;

    public CloudConfigPanel() {
        initComponents();
    }

    @Override
    public boolean hasError() {
        warningLabel.setText("");
        return false;
    }

    @Override
    public void read() {
        CloudConfigData data = this.getConfigData();
        cloudEnable = dockerConfigPanel.isDockerActivated();
        kubernetesCheckBox.setSelected(cloudEnable && data.getKubernetesConfigData().isEnabled());
        kubernetesButton.setEnabled(cloudEnable && data.getKubernetesConfigData().isEnabled());
        if (!cloudEnable) {
            infoLabel.setText(getMessage(DockerConfigPanel.class, "DOCKER_DISABLED_MESSAGE"));
        } else {
            infoLabel.setText("");
        }
    }

    @Override
    public void store() {
        CloudConfigData data = this.getConfigData();
        data.getKubernetesConfigData().setEnabled(kubernetesCheckBox.isSelected());
    }

    @Override
    public void init(String _package, Project project, SourceGroup sourceGroup) {
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLayeredPane1 = new javax.swing.JLayeredPane();
        jLayeredPane2 = new javax.swing.JLayeredPane();
        jLabel2 = new javax.swing.JLabel();
        kubernetesCheckBox = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        kubernetesButton = new javax.swing.JButton();
        jLayeredPane3 = new javax.swing.JLayeredPane();
        jLayeredPane4 = new javax.swing.JLayeredPane();
        warningLabel = new javax.swing.JLabel();
        infoLabel = new javax.swing.JLabel();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jLayeredPane1.setPreferredSize(new java.awt.Dimension(648, 150));
        jLayeredPane1.setLayout(new java.awt.GridLayout(3, 0, 20, 0));

        jLayeredPane2.setLayout(new javax.swing.BoxLayout(jLayeredPane2, javax.swing.BoxLayout.X_AXIS));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(CloudConfigPanel.class, "CloudConfigPanel.jLabel2.text")); // NOI18N
        jLayeredPane2.add(jLabel2);

        org.openide.awt.Mnemonics.setLocalizedText(kubernetesCheckBox, org.openide.util.NbBundle.getMessage(CloudConfigPanel.class, "CloudConfigPanel.kubernetesCheckBox.text")); // NOI18N
        kubernetesCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kubernetesCheckBoxActionPerformed(evt);
            }
        });
        jLayeredPane2.add(kubernetesCheckBox);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(CloudConfigPanel.class, "CloudConfigPanel.jLabel1.text")); // NOI18N
        jLayeredPane2.add(jLabel1);

        kubernetesButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jcode/cloud/resources/settings.png"))); // NOI18N
        kubernetesButton.setToolTipText(org.openide.util.NbBundle.getMessage(CloudConfigPanel.class, "CloudConfigPanel.kubernetesButton.toolTipText")); // NOI18N
        kubernetesButton.setEnabled(false);
        kubernetesButton.setPreferredSize(new java.awt.Dimension(21, 21));
        kubernetesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kubernetesButtonActionPerformed(evt);
            }
        });
        jLayeredPane2.add(kubernetesButton);

        jLayeredPane1.add(jLayeredPane2);

        javax.swing.GroupLayout jLayeredPane3Layout = new javax.swing.GroupLayout(jLayeredPane3);
        jLayeredPane3.setLayout(jLayeredPane3Layout);
        jLayeredPane3Layout.setHorizontalGroup(
            jLayeredPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 421, Short.MAX_VALUE)
        );
        jLayeredPane3Layout.setVerticalGroup(
            jLayeredPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 48, Short.MAX_VALUE)
        );

        jLayeredPane1.add(jLayeredPane3);

        javax.swing.GroupLayout jLayeredPane4Layout = new javax.swing.GroupLayout(jLayeredPane4);
        jLayeredPane4.setLayout(jLayeredPane4Layout);
        jLayeredPane4Layout.setHorizontalGroup(
            jLayeredPane4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 421, Short.MAX_VALUE)
        );
        jLayeredPane4Layout.setVerticalGroup(
            jLayeredPane4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 48, Short.MAX_VALUE)
        );

        jLayeredPane1.add(jLayeredPane4);

        warningLabel.setForeground(new java.awt.Color(200, 0, 0));
        warningLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(warningLabel, org.openide.util.NbBundle.getMessage(CloudConfigPanel.class, "CloudConfigPanel.warningLabel.text")); // NOI18N

        infoLabel.setForeground(new java.awt.Color(102, 0, 255));
        infoLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(infoLabel, org.openide.util.NbBundle.getMessage(CloudConfigPanel.class, "CloudConfigPanel.infoLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(infoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(warningLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLayeredPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(infoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(warningLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void kubernetesCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kubernetesCheckBoxActionPerformed
        boolean openSetting = false;
        if (kubernetesCheckBox.isSelected()) {
            if (dockerConfigPanel.isDockerActivated()) {
                openSetting = true;
            } else {
                NotifyDescriptor.Confirmation msg = new NotifyDescriptor.Confirmation(
                        "Would you like to enable the Docker ?",
                        "Docker not activated", NotifyDescriptor.QUESTION_MESSAGE);
                if (NotifyDescriptor.YES_OPTION.equals(DialogDisplayer.getDefault().notify(msg))) {
                    dockerConfigPanel.activateDocker();
                    read();
                    openSetting = true;
                } else {
                    kubernetesCheckBox.setSelected(false);
                    kubernetesButton.setEnabled(false);
                }
            }
        } else {
            kubernetesButton.setEnabled(false);
        }
        if (openSetting) {
            KubernetesPanel kubernetesPanel = new KubernetesPanel(this.getConfigData().getKubernetesConfigData());
            kubernetesPanel.setVisible(true);
        }
    }//GEN-LAST:event_kubernetesCheckBoxActionPerformed

    private void kubernetesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kubernetesButtonActionPerformed
        kubernetesCheckBoxActionPerformed(null);
    }//GEN-LAST:event_kubernetesButtonActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        if (cloudEnable != dockerConfigPanel.isDockerActivated()) {
            read();
        }
    }//GEN-LAST:event_formComponentShown


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel infoLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLayeredPane jLayeredPane2;
    private javax.swing.JLayeredPane jLayeredPane3;
    private javax.swing.JLayeredPane jLayeredPane4;
    private javax.swing.JButton kubernetesButton;
    private javax.swing.JCheckBox kubernetesCheckBox;
    private javax.swing.JLabel warningLabel;
    // End of variables declaration//GEN-END:variables

}
