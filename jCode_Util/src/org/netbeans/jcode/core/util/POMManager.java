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
package org.netbeans.jcode.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import static java.util.stream.Collectors.toSet;
import javax.xml.namespace.QName;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.ModelReader;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.netbeans.api.project.Project;
import org.netbeans.modules.maven.NbMavenProjectImpl;
import org.netbeans.modules.maven.api.ModelUtils;
import org.netbeans.modules.maven.api.NbMavenProject;
import org.netbeans.modules.maven.embedder.EmbedderFactory;
import org.netbeans.modules.maven.model.ModelOperation;
import org.netbeans.modules.maven.model.Utilities;
import org.netbeans.modules.maven.model.pom.Activation;
import org.netbeans.modules.maven.model.pom.ActivationCustom;
import org.netbeans.modules.maven.model.pom.Build;
import org.netbeans.modules.maven.model.pom.Configuration;
import org.netbeans.modules.maven.model.pom.DependencyManagement;
import org.netbeans.modules.maven.model.pom.POMModel;
import org.netbeans.modules.maven.model.pom.POMModelFactory;
import org.netbeans.modules.maven.model.pom.Plugin;
import org.netbeans.modules.maven.model.pom.PluginExecution;
import org.netbeans.modules.maven.model.pom.Profile;
import org.netbeans.modules.maven.model.pom.Repository;
import org.netbeans.modules.maven.model.pom.RepositoryPolicy;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;

/**
 *
 * @author jGauravGupta
 */
public class POMManager {

    private Model sourceModel;
    private Project project;
    private FileObject pomFileObject;
    private List<ModelOperation<POMModel>> operations;
    private POMModel pomModel;

    public POMManager(String inputResource, Project project) {
        try {
            this.project = project;
            operations = new ArrayList<>();
            InputStream pomStream = FileUtil.loadResource(inputResource);
            ModelReader reader = EmbedderFactory.getProjectEmbedder().lookupComponent(ModelReader.class);
            sourceModel = reader.read(pomStream, Collections.singletonMap(ModelReader.IS_STRICT, false));
            pomFileObject = org.openide.filesystems.FileUtil.toFileObject(project.getLookup().lookup(NbMavenProjectImpl.class).getPOMFile());
            org.netbeans.modules.xml.xam.ModelSource source = Utilities.createModelSource(pomFileObject);
            pomModel = POMModelFactory.getDefault().createFreshModel(source);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public static boolean isMavenProject(Project project) {
        return project.getLookup().lookup(NbMavenProjectImpl.class) != null;
    }

    private void execute() {
        registerDependency();
        registerRepository();
        registerPlugin();
        registerProfile();
    }

    private void registerPlugin() {
        if (sourceModel.getBuild() != null && !sourceModel.getBuild().getPlugins().isEmpty()) {
            try {
                pomModel.startTransaction();
                org.netbeans.modules.maven.model.pom.Project pomProject = pomModel.getProject();
                Build bld = pomProject.getBuild();
                if (bld == null) {
                    bld = pomModel.getFactory().createBuild();
                    pomProject.setBuild(bld);
                }

                for (org.apache.maven.model.Plugin plugin : sourceModel.getBuild().getPlugins()) {
                    Plugin plg = bld.findPluginById(plugin.getGroupId(), plugin.getArtifactId());
                    if (plg == null) {
                        plg = pomModel.getFactory().createPlugin();
                        plg.setGroupId(plugin.getGroupId());
                        plg.setArtifactId(plugin.getArtifactId());
                        plg.setExtensions(Boolean.TRUE);
                        bld.addPlugin(plg);

                        if (plugin.getConfiguration() != null) {
                            Xpp3Dom parentDOM = (Xpp3Dom) plugin.getConfiguration();

                            Configuration cnf = plg.getConfiguration();
                            if (cnf == null) {
                                cnf = pomModel.getFactory().createConfiguration();
                                plg.setConfiguration(cnf);
                            }
                            for (Xpp3Dom childDOM : parentDOM.getChildren()) {
                                childDOM.getName();
                                childDOM.getValue();
                                cnf.setSimpleParameter(childDOM.getName(), childDOM.getValue());
                            }
                        }

                        if (plugin.getExecutions() != null && !plugin.getExecutions().isEmpty()) {
                            for (org.apache.maven.model.PluginExecution execution : plugin.getExecutions()) {
                                PluginExecution pluginExecution = pomModel.getFactory().createExecution();
                                pluginExecution.setId(execution.getId());
                                pluginExecution.setPhase(execution.getPhase());
                                execution.getGoals().forEach(pluginExecution::addGoal);
                                plg.addExecution(pluginExecution);
                            }
                        }
                    }
                    plg.setVersion(plugin.getVersion());
                }
            } finally {
                pomModel.endTransaction();
            }
        }
    }
    
    private void registerProfile() {
        if (!sourceModel.getProfiles().isEmpty()) {
            try {
                pomModel.startTransaction();
                org.netbeans.modules.maven.model.pom.Project pomProject = pomModel.getProject();
                for (org.apache.maven.model.Profile sourceProfile : sourceModel.getProfiles()) {
                    Profile targetProfile = pomProject.findProfileById(sourceProfile.getId());
                    if (targetProfile == null) {
                        targetProfile = pomModel.getFactory().createProfile();
                        pomProject.addProfile(targetProfile);
                        targetProfile.setId(sourceProfile.getId());
                        if (sourceProfile.getActivation() != null) {
                            Activation activation = pomModel.getFactory().createActivation();
                            targetProfile.setActivation(activation);
                            activation.setChildElementText("activeByDefault", Boolean.toString(sourceProfile.getActivation().isActiveByDefault()), new QName("activeByDefault"));
                        }
                    }
                    targetProfile.setDependencyManagement(registerDependencyManagement(sourceProfile.getDependencyManagement(), targetProfile.getDependencyManagement()));
                    for (org.apache.maven.model.Dependency sourceDependency : sourceProfile.getDependencies()) {
                        org.netbeans.modules.maven.model.pom.Dependency targetDependency = targetProfile.findDependencyById(sourceDependency.getGroupId(), sourceDependency.getArtifactId(), null);
                        if (targetDependency == null) {
                            targetDependency = createDependency(sourceDependency);
                            targetProfile.addDependency(targetDependency);
                        } else {
                            targetDependency.setVersion(sourceDependency.getVersion());
                        }
                    }
                }
            } finally {
                pomModel.endTransaction();
            }
        }
    }

    private DependencyManagement registerDependencyManagement(org.apache.maven.model.DependencyManagement source, DependencyManagement target) {
        if (source != null) {
            if (target == null) {
                target = pomModel.getFactory().createDependencyManagement();
            }
            for (org.apache.maven.model.Dependency sourceDependency : source.getDependencies()) {
                org.netbeans.modules.maven.model.pom.Dependency targetDependency = target.findDependencyById(sourceDependency.getGroupId(), sourceDependency.getArtifactId(), null);
                if (targetDependency == null) {
                    targetDependency = createDependency(sourceDependency);
                    target.addDependency(targetDependency);
                } else {
                    targetDependency.setVersion(sourceDependency.getVersion());
                }
            }
        }
        return target;
    }
    private void registerDependency() {
        try {
            pomModel.startTransaction();
            pomModel.getProject().setDependencyManagement(registerDependencyManagement(sourceModel.getDependencyManagement(), pomModel.getProject().getDependencyManagement()));
            sourceModel.getDependencies().forEach((sourceDependency) -> {
                org.netbeans.modules.maven.model.pom.Dependency targetDependency = pomModel.getProject().findDependencyById(sourceDependency.getGroupId(), sourceDependency.getArtifactId(), null);
                if (targetDependency == null) {
                    targetDependency = createDependency(sourceDependency);
                    pomModel.getProject().addDependency(targetDependency);
                } else {
                    targetDependency.setVersion(sourceDependency.getVersion());
                }
            });
        } finally {
            pomModel.endTransaction();
        }

    }

    private org.netbeans.modules.maven.model.pom.Dependency createDependency(org.apache.maven.model.Dependency source) {
        org.netbeans.modules.maven.model.pom.Dependency target = pomModel.getFactory().createDependency();
        target.setGroupId(source.getGroupId());
        target.setArtifactId(source.getArtifactId());
        target.setVersion(source.getVersion());
        target.setClassifier(source.getClassifier());
      if(!"jar".equals(source.getType())) {
          target.setType(source.getType()); 
      }
        target.setScope(source.getScope());

        return target;
    }

    private void registerRepository() {
        if (sourceModel.getRepositories().size() > 0) {
            operations.add((POMModel pomModel) -> {
                Set<String> existingRepositories = pomModel.getProject().getRepositories() != null ? pomModel.getProject().getRepositories().stream().map(r -> r.getId()).collect(toSet()) : Collections.EMPTY_SET;
                for (org.apache.maven.model.Repository repository : sourceModel.getRepositories()) {
                    if (!existingRepositories.contains(repository.getId())) {
                        Repository repo = pomModel.getFactory().createRepository();
                        repo.setId(repository.getId());//isSnapshot ? MavenNbModuleImpl.NETBEANS_SNAPSHOT_REPO_ID : MavenNbModuleImpl.NETBEANS_REPO_ID);
                        repo.setName(repository.getName());
                        repo.setLayout(repository.getLayout());
                        repo.setUrl(repository.getUrl());
                        if (repository.getSnapshots() != null) {
                            RepositoryPolicy policy = pomModel.getFactory().createReleaseRepositoryPolicy();
                            policy.setEnabled(Boolean.valueOf(repository.getSnapshots().getEnabled()));
                            repo.setReleases(policy);
                        }
                        pomModel.getProject().addRepository(repo);
                    }
                }
            });
        }
    }

    public void setSourceVersion(final String version) {
        operations.add((POMModel pomModel) -> ModelUtils.setSourceLevel(pomModel, version));
    }

    private void downloadDependency() {
        RequestProcessor.getDefault().post(project.getLookup().lookup(NbMavenProject.class)::triggerDependencyDownload);
    }

    public void commit() {
        execute();
        if (operations.size() > 0) {
            Utilities.performPOMModelOperations(pomFileObject, operations);
        }
        downloadDependency();
    }
}
