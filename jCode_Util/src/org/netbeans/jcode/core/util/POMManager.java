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
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.ModelReader;
import org.netbeans.api.project.Project;
import org.netbeans.modules.maven.NbMavenProjectImpl;
import org.netbeans.modules.maven.api.ModelUtils;
import org.netbeans.modules.maven.api.NbMavenProject;
import org.netbeans.modules.maven.embedder.EmbedderFactory;
import org.netbeans.modules.maven.model.ModelOperation;
import org.netbeans.modules.maven.model.Utilities;
import org.netbeans.modules.maven.model.pom.POMModel;
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

    private Model model;
    private Project project;
    private FileObject pomFileObject;
    private List<ModelOperation<POMModel>> operations;

    public POMManager(String inputResource, Project project) {
        try {
            this.project = project;
            operations = new ArrayList<>();
            InputStream pomStream = FileUtil.loadResource(inputResource);
            ModelReader reader = EmbedderFactory.getProjectEmbedder().lookupComponent(ModelReader.class);
            model = reader.read(pomStream, Collections.singletonMap(ModelReader.IS_STRICT, false));
            pomFileObject = org.openide.filesystems.FileUtil.toFileObject(project.getLookup().lookup(NbMavenProjectImpl.class).getPOMFile());
//            org.netbeans.modules.xml.xam.ModelSource source = Utilities.createModelSource(pomFileObject);
//            pomModel = POMModelFactory.getDefault().createFreshModel(source);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    public static boolean isMavenProject(Project project){
        return project.getLookup().lookup(NbMavenProjectImpl.class)!=null;
    }
    public void execute() {
        registerDependency();
        registerRepository();
    }

    private void registerDependency() {
        for (Dependency dependency : model.getDependencies()) {
            ModelUtils.addDependency(project.getProjectDirectory().getFileObject("pom.xml") /*NOI18N*/,
                    dependency.getGroupId(), dependency.getArtifactId(),
                    dependency.getVersion(), null,
                    dependency.getScope(), dependency.getClassifier(), false);
        }
    }

    private void registerRepository() {
        if (model.getRepositories().size() > 0) {

            operations.add((POMModel pomModel) -> {
                Set<String> existingRepositories = pomModel.getProject().getRepositories()!=null ? pomModel.getProject().getRepositories().stream().map(r -> r.getId()).collect(toSet()) : Collections.EMPTY_SET;
                for (org.apache.maven.model.Repository repository : model.getRepositories()) {
                    if (!existingRepositories.contains(repository.getId())) {
                        Repository repo = pomModel.getFactory().createRepository();
                        repo.setId(repository.getId());//isSnapshot ? MavenNbModuleImpl.NETBEANS_SNAPSHOT_REPO_ID : MavenNbModuleImpl.NETBEANS_REPO_ID);
                        repo.setName(repository.getName());
                        repo.setUrl(repository.getUrl());
                        RepositoryPolicy policy = pomModel.getFactory().createReleaseRepositoryPolicy();
                        policy.setEnabled(Boolean.valueOf(repository.getSnapshots().getEnabled()));
                        repo.setReleases(policy);
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
        if (operations.size() > 0) {
            Utilities.performPOMModelOperations(pomFileObject, operations);
        }
        downloadDependency();
    }
}
