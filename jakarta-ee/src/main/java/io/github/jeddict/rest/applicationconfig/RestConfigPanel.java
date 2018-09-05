/**
 * Copyright 2013-2018 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.rest.applicationconfig;

import io.github.jeddict.jcode.LayerConfigPanel;
import java.util.prefs.Preferences;
import javax.lang.model.SourceVersion;
import javax.swing.event.ChangeEvent;
import org.apache.commons.lang.StringUtils;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import io.github.jeddict.jcode.util.PreferenceUtils;
import org.openide.util.NbBundle;

/**
 *
 * @author Gaurav Gupta
 */
public class RestConfigPanel extends LayerConfigPanel<RestConfigData> {

    private Preferences pref;
    
    public static final String DEFAULT_RESOURCE_FOLDER = "webresources";

    public RestConfigPanel() {
        initComponents();
    }

    @Override
    public boolean hasError() {
        warningLabel.setText("");
//        String _package = getPackage();
//        if (!JavaIdentifiers.isValidPackageName(_package)) {
//            warningLabel.setText(NbBundle.getMessage(RestConfigPanel.class, "RestConfigDialog.invalidPackage.message"));
//            return true;
//        }

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
        if (StringUtils.equals(restPath, DEFAULT_RESOURCE_FOLDER)) {
            warningLabel.setText(NbBundle.getMessage(RestConfigPanel.class, "RestConfigDialog.reservedPath.message", DEFAULT_RESOURCE_FOLDER));
            return true;
        }

//        RestApplication restApplication = restApplications.get(restPath);
//        if (restApplication != null && !restApplication.getApplicationClass().equals(_package + "." + restClass)) {
//            warningLabel.setText(NbBundle.getMessage(RestConfigPanel.class, "RestConfigDialog.alreadyExist.message", restApplication.getApplicationClass()));
//            return true;
//        }
        return false;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        hasError();
    }

    @Override
    public void read() {
        this.setConfigData(PreferenceUtils.get(pref, RestConfigData.class));
        RestConfigData data = this.getConfigData();
//        if (StringUtils.isNotBlank(data.getPackage())) {
//            setPackage(data.getPackage());
//        }

        if (StringUtils.isNotBlank(data.getApplicationPath())) {
            setRestPath(data.getApplicationPath());
        }

        if (StringUtils.isNotBlank(data.getApplicationClass())) {
            setRestClass(data.getApplicationClass());
        }

    }

    @Override
    public void store() {
        this.getConfigData().setApplicationClass(getRestClass());
        this.getConfigData().setApplicationPath(getRestPath());
//        this.getConfigData().setPackage(getPackage());
        PreferenceUtils.set(pref, this.getConfigData());
    }

    @Override
    public void init(String _package, Project project, SourceGroup sourceGroup) {
        pref = ProjectUtils.getPreferences(project, RestConfigData.class, true);
        addChangeListener(restConfigClassField);
        addChangeListener(restPathField);
    }

    public String getRestClass() {
        return restConfigClassField.getText().trim();
    }

    public String getRestPath() {
        return restPathField.getText().trim();
    }

    public void setRestClass(String _class) {
        restConfigClassField.setText(_class);
    }

    public void setRestPath(String path) {
        restPathField.setText(path);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        restPathPanel = new javax.swing.JPanel();
        restPathLabel = new javax.swing.JLabel();
        restPathField = new javax.swing.JTextField();
        restConfigClassPanel = new javax.swing.JPanel();
        restConfigClassLabel = new javax.swing.JLabel();
        restConfigClassField = new javax.swing.JTextField();
        warningPanel = new javax.swing.JPanel();
        warningLabel = new javax.swing.JLabel();

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
                .addContainerGap(27, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(72, Short.MAX_VALUE)
                    .addComponent(warningPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField restConfigClassField;
    private javax.swing.JLabel restConfigClassLabel;
    private javax.swing.JPanel restConfigClassPanel;
    private javax.swing.JTextField restPathField;
    private javax.swing.JLabel restPathLabel;
    private javax.swing.JPanel restPathPanel;
    private javax.swing.JLabel warningLabel;
    private javax.swing.JPanel warningPanel;
    // End of variables declaration//GEN-END:variables

//    public void setRestApplicationClasses(List<RestApplication> restApplicationList) {
//        restApplications = restApplicationList.stream().collect(toMap(app -> app.getApplicationPath(), app -> app, (app1, app2) -> app1));
//    }
}