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
package org.netbeans.jcode.ejb.facade;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.VariableTree;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toList;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.JavaClassPathConstants;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.project.classpath.ProjectClassPathModifier;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.ModificationResult;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.api.project.libraries.LibraryManager;
import org.netbeans.jcode.console.Console;
import static org.netbeans.jcode.console.Console.BOLD;
import static org.netbeans.jcode.console.Console.FG_RED;
import static org.netbeans.jcode.console.Console.UNDERLINE;
import static org.netbeans.jcode.core.util.Constants.JAVA_EXT;
import static org.netbeans.jcode.core.util.Constants.JAVA_EXT_SUFFIX;
import static org.netbeans.jcode.core.util.Constants.NAMED;
import org.netbeans.jcode.core.util.StringHelper;
import org.netbeans.jcode.core.util.SourceGroupSupport;
import org.netbeans.jcode.entity.info.EntityResourceBeanModel;
import org.netbeans.jcode.layer.ConfigData;
import org.netbeans.jcode.layer.Generator;
import org.netbeans.jcode.layer.Technology;
import static org.netbeans.jcode.layer.Technology.Type.BUSINESS;
import org.netbeans.jcode.task.progress.ProgressHandler;
import org.netbeans.modules.j2ee.core.api.support.java.GenerationUtils;
import org.netbeans.modules.j2ee.core.api.support.java.JavaIdentifiers;
import org.netbeans.modules.j2ee.core.api.support.java.SourceUtils;
import org.netbeans.modules.j2ee.metadata.model.api.MetadataModel;
import org.netbeans.modules.j2ee.persistence.action.EntityManagerGenerator;
import org.netbeans.modules.j2ee.persistence.action.GenerationOptions;
import org.netbeans.modules.j2ee.persistence.api.EntityClassScope;
import org.netbeans.modules.j2ee.persistence.api.metadata.orm.Entity;
import org.netbeans.modules.j2ee.persistence.api.metadata.orm.EntityMappingsMetadata;
import org.netbeans.modules.j2ee.persistence.spi.entitymanagergenerator.ContainerManagedJTAInjectableInEJB;
import org.netbeans.modules.j2ee.persistence.spi.entitymanagergenerator.EntityManagerGenerationStrategy;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * Generates EJB facades for entity classes.
 *
 * @author Gaurav Gupta
 */

@ServiceProvider(service=Generator.class)
@Technology(type=BUSINESS, label="Session Bean Facade", panel=SessionBeanPanel.class)
public final class EjbFacadeGenerator implements Generator{

    private static final Logger LOGGER = Logger.getLogger(EjbFacadeGenerator.class.getName());

    private static final String FACADE_ABSTRACT = "Abstract"; //NOI18N
    private static final String FACADE_REMOTE_SUFFIX = "Remote"; //NOI18N
    private static final String FACADE_LOCAL_SUFFIX = "Local"; //NOI18N
    private static final String EJB_LOCAL = "javax.ejb.Local"; //NOI18N
    private static final String EJB_REMOTE = "javax.ejb.Remote"; //NOI18N
    protected static final String EJB_STATELESS = "javax.ejb.Stateless"; //NOI18N
    

    private Project project;
    /**
     * Contains the names of the entities. Key the FQN class name, value the
     * name of the entity.
     */
    private final Map<String, String> entityNames = new HashMap<>();

    @ConfigData
    private SessionBeanData beanData;
     
    @Override
    public void execute(Project project, SourceGroup source,EntityResourceBeanModel model, ProgressHandler handler) throws IOException {
        handler.progress(Console.wrap(EjbFacadeGenerator.class, "MSG_Progress_Now_Generating", FG_RED, BOLD, UNDERLINE));
        generate(project, source, model.getEntityInfos().stream().map(ei -> ei.getType()).collect(toList()), beanData, handler);
    }
    
    public Set<FileObject> generate(Project project, final SourceGroup targetSourceGroup, List<String> entities,
            SessionBeanData beanData, ProgressHandler handler) throws IOException {
        final Set<FileObject> createdFiles = new HashSet<>();

        FileObject targetFolder = SourceGroupSupport.getFolderForPackage(targetSourceGroup, beanData.getPackage(), true);
        initEntityNames();

        generateAbstract(project, targetFolder, beanData, true);
        for (String entity : entities) {
            handler.progress(beanData.getPrefixName() + JavaIdentifiers.unqualify(entity) + beanData.getSuffixName());
            createdFiles.addAll(generate(project, targetFolder, entity, beanData, false, false, project, project, true));
        }

        return createdFiles;
    }

    /**
     * Generates the facade and the loca/remote interface(s) for the given
     * entity class.
     *
     * @param targetFolder the folder where the facade and interfaces are
     * generated.
     * @param entityClass the FQN of the entity class for which the facade is
     * generated.
     * @param pkg the package prefix for the generated facede.
     * @param hasRemote specifies whether a remote interface is generated.
     * @param hasLocal specifies whether a local interface is generated.
     *
     * @return a set containing the generated files.
     */
    private Set<FileObject> generate(final Project project, final FileObject targetFolder, final String entityClass, SessionBeanData beanData, final boolean hasRemote, final boolean hasLocal, Project remoteProject, Project entityProject, boolean overrideExisting) throws IOException {
        return generate(project, targetFolder, entityClass, beanData, hasRemote, hasLocal, remoteProject, entityProject, ContainerManagedJTAInjectableInEJB.class, overrideExisting);
    }

    private FileObject generateAbstract(final Project project, final FileObject targetFolder,
            final SessionBeanData beanData, boolean overrideExisting) throws IOException {

        //create the abstract facade class
        String fileName = beanData.getPrefixName() + FACADE_ABSTRACT + beanData.getSuffixName();
        FileObject afFO = targetFolder.getFileObject(fileName, JAVA_EXT);//skips here
        
        if (afFO != null) {
            if (overrideExisting) {
                afFO.delete();
            } else {
                throw new IOException("File already exists exception: " + afFO.getPath());
            }
        }
        
        afFO = org.netbeans.jcode.core.util.FileUtil.expandTemplate("org/netbeans/jcode/ejb/facade/resource/AbstractFacade.java.ftl", targetFolder, fileName+'.'+JAVA_EXT, Collections.singletonMap("package", beanData.getPackage()));
       
        try {
            JavaSource.forFileObject(afFO).runWhenScanFinished((CompilationController cc) -> {
                cc.toPhase(Phase.ELEMENTS_RESOLVED);
            }, true).get();
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        return afFO;
    }

    /**
     * Generates the facade and the loca/remote interface(s) for thhe given
     * entity class.
     * <i>Package private visibility for tests</i>.
     *
     * @param targetFolder the folder where the facade and interfaces are
     * generated.
     * @param entityClass the FQN of the entity class for which the facade is
     * generated.
     * @param pkg the package prefix for the generated facede.
     * @param hasRemote specifies whether a remote interface is generated.
     * @param hasLocal specifies whether a local interface is generated.
     * @param strategyClass the entity manager lookup strategy.
     *
     * @return a set containing the generated files.
     */
    Set<FileObject> generate(final Project project, final FileObject targetFolder, final String entityFQN,
            final SessionBeanData beanData, final boolean hasRemote, final boolean hasLocal,
            final Project remoteProject,
            final Project entityProject,
            final Class<? extends EntityManagerGenerationStrategy> strategyClass,
            boolean overrideExisting) throws IOException {

        final Set<FileObject> createdFiles = new HashSet<FileObject>();
        final String entitySimpleName = JavaIdentifiers.unqualify(entityFQN);
        final String variableName = StringHelper.firstLower(entitySimpleName);

        //create the abstract facade class
        String fileName = beanData.getPrefixName() + FACADE_ABSTRACT + beanData.getSuffixName();
        final String afName = beanData.getPackage().isEmpty() ? fileName : beanData.getPackage() + "." + fileName; //NOI18N
    
       
        String facadeName = beanData.getPrefixName() + entitySimpleName + beanData.getSuffixName();
        // create the facade
        FileObject existingFO = targetFolder.getFileObject(facadeName, JAVA_EXT);
        if (existingFO != null) {
            if (overrideExisting) {
                existingFO.delete();
            } else {
                throw new IOException("File already exists exception: " + existingFO.getPath());
            }
        }
        final FileObject facade = GenerationUtils.createClass(targetFolder, facadeName, null);
        createdFiles.add(facade);

        // generate methods for the facade
        EntityManagerGenerator generator = new EntityManagerGenerator(facade, entityFQN);
        List<GenerationOptions> methodOptions = getMethodOptions(entityFQN, variableName);
        for (GenerationOptions each : methodOptions) {
            generator.generate(each, strategyClass);
        }

        // create the interfaces
        final String localInterfaceFQN = beanData.getPackage() + "." + getUniqueClassName(facadeName + FACADE_LOCAL_SUFFIX, targetFolder);
        final String remoteInterfaceFQN = beanData.getPackage() + "." + getUniqueClassName(facadeName + FACADE_REMOTE_SUFFIX, targetFolder);

        List<GenerationOptions> intfOptions = getAbstractFacadeMethodOptions(entityFQN, variableName);
        if (hasLocal) {
            final SourceGroup[] groups = ProjectUtils.getSources(project).getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
            String simpleName = JavaIdentifiers.unqualify(localInterfaceFQN);
            if (!interfaceExists(groups, beanData.getPackage(), simpleName)) {
                FileObject local = createInterface(simpleName, EJB_LOCAL, targetFolder);
                addMethodToInterface(intfOptions, local);
                createdFiles.add(local);
            }
        }
        if (hasRemote) {
            final SourceGroup[] groups = ProjectUtils.getSources(remoteProject).getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
            String simpleName = JavaIdentifiers.unqualify(remoteInterfaceFQN);
            if (!interfaceExists(groups, beanData.getPackage(), simpleName)) {
                FileObject remotePackage = createRemoteInterfacePackage(remoteProject, beanData.getPackage(), targetFolder);
                FileObject remote = createInterface(simpleName, EJB_REMOTE, remotePackage);
                addMethodToInterface(intfOptions, remote);
                createdFiles.add(remote);
                if (entityProject != null && !entityProject.getProjectDirectory().equals(remoteProject.getProjectDirectory())) {
                    if (groups != null && groups.length > 0) {
                        FileObject fo = groups[0].getRootFolder();
                        ClassPath cp = ClassPath.getClassPath(fo, ClassPath.COMPILE);
                        if (cp != null) {
                            try {
                                ProjectClassPathModifier.addProjects(new Project[]{entityProject}, fo, ClassPath.COMPILE);
                            } catch (IOException | UnsupportedOperationException e) {
                                NotifyDescriptor d = new NotifyDescriptor.Message(
                                        NbBundle.getMessage(EjbFacadeGenerator.class, "WARN_UpdateClassPath",
                                                ProjectUtils.getInformation(remoteProject).getDisplayName(),
                                                ProjectUtils.getInformation(entityProject).getDisplayName()),
                                        NotifyDescriptor.INFORMATION_MESSAGE);
                                DialogDisplayer.getDefault().notify(d);
                            }
                        }
                    }
                }
            }
        }



        JavaSource facadeJS = JavaSource.forFileObject(facade);
        if (facadeJS == null) {
            ClassPath classPath = ClassPath.getClassPath(facade, ClassPath.SOURCE);
            if (classPath == null) {
                LOGGER.log(Level.WARNING, "facade FO: valid={0}, sourceCP=null", facade.isValid());
            } else {
                LOGGER.log(
                        Level.WARNING,
                        "facade FO: valid={0}, onSourceCP={1}",
                        new Object[]{facade.isValid(), classPath.contains(facade)});
            }
        }
        
                // add the @stateless annotation
        // add implements and extends clauses to the facade
        Task<WorkingCopy> modificationTask = new Task<WorkingCopy>() {
            @Override
            public void run(WorkingCopy wc) throws Exception {
                wc.toPhase(Phase.RESOLVED);
                String fqn = beanData.getPackage().isEmpty() ? facadeName : beanData.getPackage() + "." + facadeName;
                TypeElement classElement = wc.getElements().getTypeElement(fqn);
                ClassTree classTree = wc.getTrees().getTree(classElement);
                if (classTree == null) {
                    StringBuilder message = new StringBuilder();
                    message.append("Facade fileObject: {0} [valid={1}]").append("\n") //NOI18N
                            .append("Facade source package: {2}, entityName: {3}").append("\n") //NOI18N
                            .append("ClassElement: {4}").append("\n");                          //NOI18N
                    String loggingMessage = MessageFormat.format(message.toString(),
                            new Object[]{facade, facade != null ? facade.isValid() : "null", beanData.getPackage(), entitySimpleName, classElement}); //NOI18N
                    LOGGER.severe(loggingMessage);
                }
                GenerationUtils genUtils = GenerationUtils.newInstance(wc);
                TreeMaker maker = wc.getTreeMaker();
       
                List<Tree> implementsClause = new ArrayList<>(classTree.getImplementsClause());
                if (hasLocal) {
                    implementsClause.add(genUtils.createType(localInterfaceFQN, classElement));
                }
                if (hasRemote) {
                    implementsClause.add(genUtils.createType(remoteInterfaceFQN, classElement));
                }

                List<Tree> members = new ArrayList<>(classTree.getMembers());
                MethodTree constructor = maker.Constructor(
                        genUtils.createModifiers(Modifier.PUBLIC),
                        Collections.<TypeParameterTree>emptyList(),
                        Collections.<VariableTree>emptyList(),
                        Collections.<ExpressionTree>emptyList(),
                        "{super(" + entitySimpleName + ".class);}");            //NOI18N
                members.add(constructor);

                TypeElement abstactFacadeElement = wc.getElements().getTypeElement(afName);
                TypeElement entityElement = wc.getElements().getTypeElement(entityFQN);

                DeclaredType declaredType = wc.getTypes().getDeclaredType(abstactFacadeElement, entityElement.asType());
                Tree extendsClause = maker.Type(declaredType);

                ModifiersTree modifiersTree
                        = maker.addModifiersAnnotation(classTree.getModifiers(),
                                genUtils.createAnnotation(EJB_STATELESS));

                modifiersTree
                        = maker.addModifiersAnnotation(modifiersTree,
                                genUtils.createAnnotation(NAMED,
                                        Collections.<ExpressionTree>singletonList(maker.Literal(variableName))));

                ClassTree newClassTree = maker.Class(
                        modifiersTree,
                        classTree.getSimpleName(),
                        classTree.getTypeParameters(),
                        extendsClause,
                        implementsClause,
                        members);

                wc.rewrite(classTree, newClassTree);
            }
        };

        facadeJS.runModificationTask(modificationTask).commit();

        return createdFiles;
    }

    public static FileObject createRemoteInterfacePackage(Project projectForRemoteInterface, String remoteInterfacePackageName, FileObject ejbSourcePackage) throws IOException {
        assert ProjectUtils.getSources(projectForRemoteInterface).getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA).length > 0;
        FileObject root = ProjectUtils.getSources(projectForRemoteInterface).getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA)[0].getRootFolder();
        FileObject remotePkg = FileUtil.createFolder(root, remoteInterfacePackageName.replace('.', '/'));
        // add project where remote interface is defined to classpath of project where EJB is going to be implemented:
        ProjectClassPathModifier.addProjects(new Project[]{projectForRemoteInterface}, ejbSourcePackage, ClassPath.COMPILE);
        // make sure project where remote interfrace is going to be defined has javax.ejb API available:
        assert LibraryManager.getDefault().getLibrary("javaee-api-6.0") != null;
        if (ClassPath.getClassPath(remotePkg, ClassPath.COMPILE).findResource("javax/ejb") == null) {
            try {
                // first try JavaClassPathConstants.COMPILE_ONLY - if remotePkg represents
                // Maven project then it will work; J2SE project on the other hand will fail
                // and simple ClassPath.COMPILE should be used instead:
                ProjectClassPathModifier.addLibraries(new Library[]{LibraryManager.getDefault().getLibrary("javaee-api-6.0")}, remotePkg, JavaClassPathConstants.COMPILE_ONLY);
            } catch (UnsupportedOperationException e) {
                ProjectClassPathModifier.addLibraries(new Library[]{LibraryManager.getDefault().getLibrary("javaee-api-6.0")}, remotePkg, ClassPath.COMPILE);
            }
        }
        return remotePkg;
    }

    /**
     * @return the options representing the methods for a facade, i.e.
     * create/edit/ find/remove/findAll.
     */
    private List<GenerationOptions> getMethodOptions(String entityFQN, String variableName) {

        GenerationOptions getEMOptions = new GenerationOptions();
        getEMOptions.setAnnotation("java.lang.Override"); //NOI18N
        getEMOptions.setMethodName("getEntityManager"); //NOI18N
        getEMOptions.setOperation(GenerationOptions.Operation.GET_EM);
        getEMOptions.setReturnType("javax.persistence.EntityManager");//NOI18N
        getEMOptions.setModifiers(EnumSet.of(Modifier.PROTECTED));

        return Arrays.<GenerationOptions>asList(getEMOptions);
    }

    private List<GenerationOptions> getAbstractFacadeMethodOptions(String entityFQN, String variableName) {
        //abstract methods

        GenerationOptions getEMOptions = new GenerationOptions();
        getEMOptions.setMethodName("getEntityManager"); //NOI18N
        getEMOptions.setReturnType("javax.persistence.EntityManager");//NOI18N
        getEMOptions.setModifiers(EnumSet.of(Modifier.PROTECTED, Modifier.ABSTRACT));

        //implemented methods
        GenerationOptions createOptions = new GenerationOptions();
        createOptions.setMethodName("create"); //NOI18N
        createOptions.setOperation(GenerationOptions.Operation.PERSIST);
        createOptions.setReturnType("void");//NOI18N
        createOptions.setParameterName(variableName);
        createOptions.setParameterType(entityFQN);

        GenerationOptions editOptions = new GenerationOptions();
        editOptions.setMethodName("edit");//NOI18N
        editOptions.setOperation(GenerationOptions.Operation.MERGE);
        editOptions.setReturnType("void");//NOI18N
        editOptions.setParameterName(variableName);
        editOptions.setParameterType(entityFQN);

        GenerationOptions destroyOptions = new GenerationOptions();
        destroyOptions.setMethodName("remove");//NOI18N
        destroyOptions.setOperation(GenerationOptions.Operation.REMOVE);
        destroyOptions.setReturnType("void");//NOI18N
        destroyOptions.setParameterName(variableName);
        destroyOptions.setParameterType(entityFQN);

        GenerationOptions findOptions = new GenerationOptions();
        findOptions.setMethodName("find");//NOI18N
        findOptions.setOperation(GenerationOptions.Operation.FIND);
        findOptions.setReturnType(entityFQN);//NOI18N
        findOptions.setParameterName("id");//NOI18N
        findOptions.setParameterType("Object");//NOI18N

        GenerationOptions findAllOptions = new GenerationOptions();
        findAllOptions.setMethodName("findAll");//NOI18N
        findAllOptions.setOperation(GenerationOptions.Operation.FIND_ALL);
        findAllOptions.setReturnType("java.util.List<" + entityFQN + ">");//NOI18N
        findAllOptions.setQueryAttribute(getEntityName(entityFQN));

        GenerationOptions findSubOptions = new GenerationOptions();
        findSubOptions.setMethodName("findRange");//NOI18N
        findSubOptions.setOperation(GenerationOptions.Operation.FIND_SUBSET);
        findSubOptions.setReturnType("java.util.List<" + entityFQN + ">");//NOI18N
        findSubOptions.setQueryAttribute(getEntityName(entityFQN));
        findSubOptions.setParameterName("range");//NOI18N
        findSubOptions.setParameterType("int[]");//NOI18N

        GenerationOptions countOptions = new GenerationOptions();
        countOptions.setMethodName("count");//NOI18N
        countOptions.setOperation(GenerationOptions.Operation.COUNT);
        countOptions.setReturnType("int");//NOI18N
        countOptions.setQueryAttribute(getEntityName(entityFQN));

        return Arrays.<GenerationOptions>asList(getEMOptions, createOptions, editOptions, destroyOptions, findOptions, findAllOptions, findSubOptions, countOptions);
    }

    /**
     * @return the name for the given <code>entityFQN</code>.
     */
    private String getEntityName(String entityFQN) {
        String result = entityNames.get(entityFQN);
        return result != null ? result : JavaIdentifiers.unqualify(entityFQN);
    }

    /**
     * Initializes the {@link #entityNames} map.
     */
    private void initEntityNames() throws IOException {
        if (project == null) {
            // just to facilitate testing, avoids the need to provide a project (together with the getEntityName method)
            return;
        }
        //XXX should probably be using MetadataModelReadHelper. needs a progress indicator as well (#113874).
        try {
            EntityClassScope entityClassScope = EntityClassScope.getEntityClassScope(project.getProjectDirectory());
            MetadataModel<EntityMappingsMetadata> entityMappingsModel = entityClassScope.getEntityMappingsModel(true);
            Future<Void> result = entityMappingsModel.runReadActionWhenReady((EntityMappingsMetadata metadata) -> {
                for (Entity entity : metadata.getRoot().getEntity()) {
                    entityNames.put(entity.getClass2(), entity.getName());
                }
                return null;
            });
            result.get();
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    String getUniqueClassName(String candidateName, FileObject targetFolder) {
        return FileUtil.findFreeFileName(targetFolder, candidateName, JAVA_EXT); //NOI18N
    }

    /**
     * Creates an interface with the given <code>name</code>, annotated with an
     * annotation of the given <code>annotationType</code>. <i>Package private
     * visibility just because of tests</i>.
     *
     * @param name the name for the interface
     * @param annotationType the FQN of the annotation
     * @param targetFolder the folder to which the interface is generated
     *
     * @return the generated interface.
     */
    FileObject createInterface(String name, final String annotationType, FileObject targetFolder) throws IOException {
        FileObject sourceFile = GenerationUtils.createInterface(targetFolder, name, null);
        JavaSource source = JavaSource.forFileObject(sourceFile);
        if (source == null) {
            LOGGER.log(Level.SEVERE, "JavaSource not created for FileObject: path={0}, valid={1}, mime-type={2}",
                    new Object[]{sourceFile.getPath(), sourceFile.isValid(), sourceFile.getMIMEType()});
        }
        ModificationResult result = source.runModificationTask(new Task<WorkingCopy>() {

            @Override
            public void run(WorkingCopy workingCopy) throws Exception {
                workingCopy.toPhase(Phase.RESOLVED);
                ClassTree clazz = SourceUtils.getPublicTopLevelTree(workingCopy);
                assert clazz != null;
                GenerationUtils genUtils = GenerationUtils.newInstance(workingCopy);
                TreeMaker make = workingCopy.getTreeMaker();
                AnnotationTree annotations = genUtils.createAnnotation(annotationType);
                ModifiersTree modifiers = make.Modifiers(clazz.getModifiers(), Collections.<AnnotationTree>singletonList(annotations));
                ClassTree modifiedClass = make.Class(modifiers, clazz.getSimpleName(), clazz.getTypeParameters(), clazz.getExtendsClause(), Collections.<ExpressionTree>emptyList(), Collections.<Tree>emptyList());
                workingCopy.rewrite(clazz, modifiedClass);
            }
        });
        result.commit();
        return source.getFileObjects().iterator().next();
    }

    /**
     * Adds a method to the given interface.
     *
     * @param name the name of the method.
     * @param returnType the return type of the method.
     * @param parameterName the name of the parameter for the method.
     * @param parameterType the FQN type of the parameter.
     * @param target the target interface.
     */
    void addMethodToInterface(final List<GenerationOptions> options, final FileObject target) throws IOException {

        JavaSource source = JavaSource.forFileObject(target);
        ModificationResult result = source.runModificationTask(new Task<WorkingCopy>() {

            @Override
            public void run(WorkingCopy copy) throws Exception {
                copy.toPhase(Phase.RESOLVED);
                GenerationUtils utils = GenerationUtils.newInstance(copy);
                TypeElement typeElement = SourceUtils.getPublicTopLevelElement(copy);
                assert typeElement != null;
                ClassTree original = copy.getTrees().getTree(typeElement);
                ClassTree modifiedClass = original;
                TreeMaker make = copy.getTreeMaker();
                for (GenerationOptions each : options) {
                    if (each.getModifiers().size() == 1 && each.getModifiers().contains(Modifier.PUBLIC)) {
                        MethodTree method = make.Method(make.Modifiers(Collections.<Modifier>emptySet()),
                                each.getMethodName(), utils.createType(each.getReturnType(), typeElement),
                                Collections.<TypeParameterTree>emptyList(), getParameterList(each, make, utils, typeElement),
                                Collections.<ExpressionTree>emptyList(), (BlockTree) null, null);
                        modifiedClass = make.addClassMember(modifiedClass, method);
                    }
                }
                copy.rewrite(original, modifiedClass);
            }
        });
        result.commit();
    }

    private List<VariableTree> getParameterList(GenerationOptions options, TreeMaker make, GenerationUtils utils, TypeElement scope) {
        if (options.getParameterName() == null) {
            return Collections.<VariableTree>emptyList();
        }
        VariableTree vt = make.Variable(make.Modifiers(Collections.<Modifier>emptySet()),
                options.getParameterName(), utils.createType(options.getParameterType(), scope), null);
        return Collections.<VariableTree>singletonList(vt);
    }

    private static boolean interfaceExists(SourceGroup[] groups, String pkg, String simpleName) {
        String path = pkg.replace(".", "/"); //NOIN18N
        for (SourceGroup sourceGroup : groups) {
            FileObject pkgFO = sourceGroup.getRootFolder().getFileObject(path);
            if (pkgFO != null) {
                if (pkgFO.getFileObject(simpleName + JAVA_EXT_SUFFIX) != null) { //NOI18N
                    return true;
                }
            }
        }
        return false;
    }
}
