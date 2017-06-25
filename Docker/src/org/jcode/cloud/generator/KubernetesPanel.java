/**
 * Copyright [2017] Gaurav Gupta
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

import org.netbeans.jcode.window.GenericDialog;

/**
 *
 * @author jGauravGupta
 */
public class KubernetesPanel extends GenericDialog {

    private final KubernetesConfigData kubernetesConfigData;
    
    /**
     * Creates new form KubernetesPanel
     * @param kubernetesConfigData
     */
    public KubernetesPanel(KubernetesConfigData kubernetesConfigData) {
        this.kubernetesConfigData = kubernetesConfigData;
        initComponents();
        getRootPane().setDefaultButton(saveButton);
        namespaceTextField.setText(kubernetesConfigData.getNamespace());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLayeredPane5 = new javax.swing.JLayeredPane();
        jLayeredPane4 = new javax.swing.JLayeredPane();
        saveButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        jLayeredPane2 = new javax.swing.JLayeredPane();
        namespace = new javax.swing.JLabel();
        namespaceTextField = new javax.swing.JTextField();
        jLayeredPane3 = new javax.swing.JLayeredPane();

        setTitle(org.openide.util.NbBundle.getMessage(KubernetesPanel.class, "KubernetesPanel.title")); // NOI18N
        setSize(new java.awt.Dimension(500, 200));

        jLayeredPane4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        org.openide.awt.Mnemonics.setLocalizedText(saveButton, org.openide.util.NbBundle.getMessage(KubernetesPanel.class, "KubernetesPanel.saveButton.text")); // NOI18N
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        jLayeredPane4.add(saveButton);

        org.openide.awt.Mnemonics.setLocalizedText(cancelButton, org.openide.util.NbBundle.getMessage(KubernetesPanel.class, "KubernetesPanel.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        jLayeredPane4.add(cancelButton);

        jLayeredPane1.setLayout(new java.awt.GridLayout(2, 1, 0, 12));

        jLayeredPane2.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(namespace, org.openide.util.NbBundle.getMessage(KubernetesPanel.class, "KubernetesPanel.namespace.text")); // NOI18N
        namespace.setPreferredSize(new java.awt.Dimension(88, 14));
        jLayeredPane2.add(namespace, java.awt.BorderLayout.WEST);

        namespaceTextField.setText(org.openide.util.NbBundle.getMessage(KubernetesPanel.class, "KubernetesPanel.namespaceTextField.text")); // NOI18N
        namespaceTextField.setToolTipText(org.openide.util.NbBundle.getMessage(KubernetesPanel.class, "KubernetesPanel.namespaceTextField.toolTipText")); // NOI18N
        jLayeredPane2.add(namespaceTextField, java.awt.BorderLayout.CENTER);

        jLayeredPane1.add(jLayeredPane2);

        javax.swing.GroupLayout jLayeredPane3Layout = new javax.swing.GroupLayout(jLayeredPane3);
        jLayeredPane3.setLayout(jLayeredPane3Layout);
        jLayeredPane3Layout.setHorizontalGroup(
            jLayeredPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 490, Short.MAX_VALUE)
        );
        jLayeredPane3Layout.setVerticalGroup(
            jLayeredPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLayeredPane1.add(jLayeredPane3);

        jLayeredPane5.setLayer(jLayeredPane4, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane5.setLayer(jLayeredPane1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane5Layout = new javax.swing.GroupLayout(jLayeredPane5);
        jLayeredPane5.setLayout(jLayeredPane5Layout);
        jLayeredPane5Layout.setHorizontalGroup(
            jLayeredPane5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane5Layout.createSequentialGroup()
                .addGroup(jLayeredPane5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLayeredPane4)
                    .addGroup(jLayeredPane5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLayeredPane1)))
                .addContainerGap())
        );
        jLayeredPane5Layout.setVerticalGroup(
            jLayeredPane5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPane5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                .addComponent(jLayeredPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane5)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane5)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        cancelActionPerformed(evt);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        setVisible(false);//if (!hasError()) {
        kubernetesConfigData.setNamespace(namespaceTextField.getText());
        this.setDialogResult(javax.swing.JOptionPane.OK_OPTION);
        dispose();
    }//GEN-LAST:event_saveButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLayeredPane jLayeredPane2;
    private javax.swing.JLayeredPane jLayeredPane3;
    private javax.swing.JLayeredPane jLayeredPane4;
    private javax.swing.JLayeredPane jLayeredPane5;
    private javax.swing.JLabel namespace;
    private javax.swing.JTextField namespaceTextField;
    private javax.swing.JButton saveButton;
    // End of variables declaration//GEN-END:variables
}
