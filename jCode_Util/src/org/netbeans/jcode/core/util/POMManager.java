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
import org.netbeans.modules.maven.model.pom.BuildBase;
import org.netbeans.modules.maven.model.pom.Configuration;
import org.netbeans.modules.maven.model.pom.Dependency;
import org.netbeans.modules.maven.model.pom.DependencyContainer;
import org.netbeans.modules.maven.model.pom.DependencyManagement;
import org.netbeans.modules.maven.model.pom.POMComponent;
import org.netbeans.modules.maven.model.pom.POMExtensibilityElement;
import org.netbeans.modules.maven.model.pom.POMModel;
import org.netbeans.modules.maven.model.pom.POMModelFactory;
import org.netbeans.modules.maven.model.pom.Plugin;
import org.netbeans.modules.maven.model.pom.PluginExecution;
import org.netbeans.modules.maven.model.pom.Profile;
import org.netbeans.modules.maven.model.pom.Properties;
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
        try {
            pomModel.startTransaction();
            org.netbeans.modules.maven.model.pom.Project pomProject = pomModel.getProject();
            if (pomProject.getProperties() == null) {
                pomProject.setProperties(pomModel.getFactory().createProperties());
            }
            registerProperties(sourceModel.getProperties(), pomProject.getProperties());
            pomProject.setDependencyManagement(registerDependencyManagement(sourceModel.getDependencyManagement(), pomProject.getDependencyManagement()));
            registerDependency(sourceModel.getDependencies(), pomProject);
            registerRepository();
            registerPlugin();
            registerProfile();
        } finally {
            pomModel.endTransaction();
        }
    }

    private Properties registerProperties(java.util.Properties source, Properties target) {
        if (source!= null && !source.isEmpty()) {
            if (target == null) {
                target = pomModel.getFactory().createProperties();
            }
            for (String sourceKey : source.stringPropertyNames()) {
                String sourceValue = source.getProperty(sourceKey);
                String targetValue = target.getProperty(sourceKey);
                if (targetValue == null) {
                    target.setProperty(sourceKey, sourceValue);
                }
            }
        }
        return target;
    }

    private void registerPlugin() {
        if (sourceModel.getBuild() != null && !sourceModel.getBuild().getPlugins().isEmpty()) {
            registerBuild(sourceModel.getBuild(), pomModel.getProject().getBuild());
        }
    }

    private BuildBase registerBuild(org.apache.maven.model.BuildBase sourceBuild, BuildBase targetBuild) {
        if(sourceBuild == null){
            return targetBuild;
        }
        if (targetBuild == null) {
            targetBuild = pomModel.getFactory().createBuild();
        }
        if(sourceBuild.getFinalName()!=null){
            targetBuild.setFinalName(sourceBuild.getFinalName());
        }
        if(sourceBuild.getPlugins() != null && !sourceBuild.getPlugins().isEmpty()){
        for (org.apache.maven.model.Plugin sourcePlugin : sourceBuild.getPlugins()) {
            Plugin targetPlugin = targetBuild.findPluginById(sourcePlugin.getGroupId(), sourcePlugin.getArtifactId());
            if (targetPlugin == null) {
                targetPlugin = pomModel.getFactory().createPlugin();
                targetPlugin.setGroupId(sourcePlugin.getGroupId());
                targetPlugin.setArtifactId(sourcePlugin.getArtifactId());
                if(sourcePlugin.getExtensions() != null){
                    targetPlugin.setExtensions(Boolean.TRUE);
                }
                targetBuild.addPlugin(targetPlugin);

                if (sourcePlugin.getConfiguration() != null) {
                    Xpp3Dom parentDOM = (Xpp3Dom) sourcePlugin.getConfiguration();

                    Configuration targetConfig = targetPlugin.getConfiguration();
                    if (targetConfig == null) {
                        targetConfig = pomModel.getFactory().createConfiguration();
                        targetPlugin.setConfiguration(targetConfig);
                    }
                    loadDom(parentDOM, targetConfig);
                }

                if (sourcePlugin.getExecutions() != null && !sourcePlugin.getExecutions().isEmpty()) {
                    for (org.apache.maven.model.PluginExecution execution : sourcePlugin.getExecutions()) {
                        PluginExecution pluginExecution = pomModel.getFactory().createExecution();
                        pluginExecution.setId(execution.getId());
                        pluginExecution.setPhase(execution.getPhase());
                        execution.getGoals().forEach(pluginExecution::addGoal);
                        targetPlugin.addExecution(pluginExecution);
                    }
                }
            }
            targetPlugin.setVersion(sourcePlugin.getVersion());
        }
        }
        return targetBuild;
    }

    private void loadDom(Xpp3Dom source, POMComponent target) {
        for (Xpp3Dom childDOM : source.getChildren()) {
            if (childDOM.getValue() != null) {
                if (target instanceof Configuration) {
                    ((Configuration) target).setSimpleParameter(childDOM.getName(), childDOM.getValue());
                } else {
                    target.setChildElementText("ree", childDOM.getValue(), new QName(childDOM.getName()));
                }
            } else {
                POMExtensibilityElement element = pomModel.getFactory().createPOMExtensibilityElement(new QName(childDOM.getName()));
                target.addExtensibilityElement(element);
                loadDom(childDOM, element);
            }
        }
    }

    private void registerProfile() {
        if (!sourceModel.getProfiles().isEmpty()) {
            org.netbeans.modules.maven.model.pom.Project pomProject = pomModel.getProject();
            for (org.apache.maven.model.Profile sourceProfile : sourceModel.getProfiles()) {
                Profile targetProfile = pomProject.findProfileById(sourceProfile.getId());
                if (targetProfile == null) {
                    targetProfile = pomModel.getFactory().createProfile();
                    pomProject.addProfile(targetProfile);
                    targetProfile.setId(sourceProfile.getId());
                    targetProfile.setProperties(registerProperties(sourceProfile.getProperties(), targetProfile.getProperties()));
                    if (sourceProfile.getActivation() != null) {
                        Activation activation = pomModel.getFactory().createActivation();
                        targetProfile.setActivation(activation);
                        activation.setChildElementText("activeByDefault", Boolean.toString(sourceProfile.getActivation().isActiveByDefault()), new QName("activeByDefault"));
                    }
                }
                targetProfile.setDependencyManagement(registerDependencyManagement(sourceProfile.getDependencyManagement(), targetProfile.getDependencyManagement()));
                registerDependency(sourceProfile.getDependencies(), targetProfile);
                targetProfile.setBuildBase(registerBuild(sourceProfile.getBuild(), targetProfile.getBuildBase()));
            }
        }
    }

    private DependencyManagement registerDependencyManagement(org.apache.maven.model.DependencyManagement source, DependencyManagement target) {
        if (source != null) {
            if (target == null) {
                target = pomModel.getFactory().createDependencyManagement();
            }
            registerDependency(source.getDependencies(), target);
        }
        return target;
    }

    private void registerDependency(List<org.apache.maven.model.Dependency> source, DependencyContainer target) {
        source.forEach((sourceDependency) -> {
            org.netbeans.modules.maven.model.pom.Dependency targetDependency = target.findDependencyById(sourceDependency.getGroupId(), sourceDependency.getArtifactId(), null);
            if (targetDependency == null) {
                targetDependency = createDependency(sourceDependency);
                target.addDependency(targetDependency);
            } else {
                targetDependency.setVersion(sourceDependency.getVersion());
            }
        });
    }

    private Dependency createDependency(org.apache.maven.model.Dependency source) {
        org.netbeans.modules.maven.model.pom.Dependency target = pomModel.getFactory().createDependency();
        target.setGroupId(source.getGroupId());
        target.setArtifactId(source.getArtifactId());
        target.setVersion(source.getVersion());
        target.setClassifier(source.getClassifier());
        if (!"jar".equals(source.getType())) {
            target.setType(source.getType());
        }
        target.setScope(source.getScope());

        return target;
    }

    private void registerRepository() {
        if (sourceModel.getRepositories().size() > 0) {
            operations.add(pomModel -> {
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
        operations.add(pomModel -> ModelUtils.setSourceLevel(pomModel, version));
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
