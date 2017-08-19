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
package org.netbeans.jcode.rest.util;

import org.netbeans.jcode.core.util.Constants;
import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WildcardTree;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.lang.StringUtils;
import org.netbeans.api.j2ee.core.Profile;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.ModificationResult;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import static org.netbeans.jcode.cdi.CDIConstants.INJECT;
import static org.netbeans.jcode.core.util.Constants.JAVA_EXT;
import org.netbeans.jcode.core.util.JavaSourceHelper;
import org.netbeans.jcode.core.util.SourceGroupSupport;
import static org.netbeans.jcode.jpa.JPAConstants.BASIC_FQN;
import static org.netbeans.jcode.jpa.JPAConstants.ID_FQN;
import static org.netbeans.jcode.rest.RestConstants.FORM_PARAM;
import static org.netbeans.jcode.rest.RestConstants.SINGLETON_METHOD;
import org.netbeans.jcode.rest.applicationconfig.RestConfigData;
import org.netbeans.jcode.task.progress.ProgressHandler;
import org.netbeans.modules.j2ee.core.api.support.java.GenerationUtils;
import org.netbeans.modules.j2ee.core.api.support.java.SourceUtils;
import org.netbeans.modules.j2ee.deployment.common.api.Datasource;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.spi.webmodule.WebModuleProvider;
import org.netbeans.modules.websvc.rest.MiscPrivateUtilities;
import org.netbeans.modules.websvc.rest.model.api.RestConstants;
import org.netbeans.modules.websvc.rest.model.api.RestServicesModel;
import org.netbeans.modules.websvc.rest.spi.MiscUtilities;
import org.netbeans.modules.websvc.rest.spi.RestSupport;
import static org.netbeans.modules.websvc.rest.spi.RestSupport.JAX_RS_APPLICATION_CLASS;
import org.openide.filesystems.FileObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * REST support utility
 *
 */
public class RestUtils {

    public static RestSupport getRestSupport(Project project) {
        return project.getLookup().lookup(RestSupport.class);
    }

    public static RestServicesModel getRestServicesMetadataModel(Project project) {
        RestSupport support = getRestSupport(project);
        if (support != null) {
            return support.getRestServicesModel();
        }
        return null;
    }

    public static void disableRestServicesChangeListner(Project project) {
        final RestServicesModel wsModel = RestUtils.getRestServicesMetadataModel(project);
        if (wsModel == null) {
            return;
        }
        wsModel.disablePropertyChangeListener();
    }

    public static void enableRestServicesChangeListner(Project project) {
        final RestServicesModel wsModel = RestUtils.getRestServicesMetadataModel(project);
        if (wsModel == null) {
            return;
        }
        wsModel.enablePropertyChangeListener();
    }

    public static boolean hasJTASupport(Project project) {
        RestSupport support = getRestSupport(project);

        if (support != null) {
            return support.hasJTASupport();
        }

        return false;
    }

    public static FileObject getDeploymentDescriptor(Project p) {
        WebModuleProvider wmp = p.getLookup().lookup(WebModuleProvider.class);
        if (wmp != null) {
            return wmp.findWebModule(p.getProjectDirectory()).getDeploymentDescriptor();
        }
        return null;
    }

    public static boolean hasSpringSupport(Project project) {
        RestSupport support = getRestSupport(project);

        if (support != null) {
            return support.hasSpringSupport();
        }

        return false;
    }

    public static boolean isServerTomcat(Project project) {
        RestSupport support = getRestSupport(project);

        if (support != null) {
            return support.isServerTomcat();
        }

        return false;
    }

    public static boolean isServerGFV3(Project project) {
        RestSupport support = getRestSupport(project);

        if (support != null) {
            return support.isServerGFV3();
        }

        return false;
    }

    public static boolean isServerGFV2(Project project) {
        RestSupport support = getRestSupport(project);

        if (support != null) {
            return support.isServerGFV2();
        }

        return false;
    }

    public static Datasource getDatasource(Project project, String jndiName) {
        return MiscUtilities.getDatasource(project, jndiName);
    }

    //
    // TODO: The following methods don't belong here. Some of them should go into
    // JavaSourceHelper and the XML/DOM related methods should go into
    // their own utility class.
    //        
    public static String getAttributeValue(Node n, String nodePath, String attrName) throws XPathExpressionException {
        String attrValue = null;
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression expr3 = xpath.compile(nodePath + "/@" + attrName);
        Object result3 = expr3.evaluate(n, XPathConstants.NODESET);
        NodeList nodes3 = (NodeList) result3;
        for (int i = 0; i < nodes3.getLength(); i++) {
            attrValue = nodes3.item(i).getNodeValue();
            break;
        }
        return attrValue;
    }

    public static String findUri(JavaSource rSrc) {
        String path = null;
        List<? extends AnnotationMirror> annotations = JavaSourceHelper.getClassAnnotations(rSrc);
        for (AnnotationMirror annotation : annotations) {
            String cAnonType = annotation.getAnnotationType().toString();
            if (RestConstants.PATH.equals(cAnonType)) {
                path = getValueFromAnnotation(annotation);
            }
        }
        return path;
    }

    public static boolean isStaticResource(JavaSource src) {
        List<? extends AnnotationMirror> annotations = JavaSourceHelper.getClassAnnotations(src);
        if (annotations != null && annotations.size() > 0) {
            for (AnnotationMirror annotation : annotations) {
                String classAnonType = annotation.getAnnotationType().toString();
                if (RestConstants.PATH.equals(classAnonType)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isDynamicResource(JavaSource src) {
        List<MethodTree> trees = JavaSourceHelper.getAllMethods(src);
        for (MethodTree tree : trees) {
            List<? extends AnnotationTree> mAnons = tree.getModifiers().getAnnotations();
            if (mAnons != null && mAnons.size() > 0) {
                for (AnnotationTree mAnon : mAnons) {
                    String mAnonType = mAnon.getAnnotationType().toString();
                    if (RestConstants.PATH_ANNOTATION.equals(mAnonType) || RestConstants.PATH.equals(mAnonType)) {
                        return true;
                    } else if (RestConstants.GET_ANNOTATION.equals(mAnonType) || RestConstants.GET.equals(mAnonType)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String findElementName(MethodTree tree) {
        String eName = "";
        List<? extends AnnotationTree> mAnons = tree.getModifiers().getAnnotations();
        if (mAnons != null && mAnons.size() > 0) {
            for (AnnotationTree mAnon : mAnons) {
                eName = mAnon.toString();
                if (eName.indexOf("\"") != -1) {
                    eName = getValueFromAnnotation(mAnon);
                } else {
                    eName = getNameFromMethod(tree);
                }
            }
        }
        return eName.substring(0, 1).toLowerCase() + eName.substring(1);
    }

    public static MethodTree findGetAsXmlMethod(JavaSource rSrc) {
        MethodTree method = null;
        List<MethodTree> rTrees = JavaSourceHelper.getAllMethods(rSrc);
        for (MethodTree tree : rTrees) {
            boolean isHttpGetMethod = false;
            boolean isXmlMime = false;
            List<? extends AnnotationTree> mAnons = tree.getModifiers().getAnnotations();
            if (mAnons != null && mAnons.size() > 0) {
                for (AnnotationTree mAnon : mAnons) {
                    String mAnonType = mAnon.getAnnotationType().toString();
                    if (RestConstants.GET_ANNOTATION.equals(mAnonType) || RestConstants.GET.equals(mAnonType)) {
                        isHttpGetMethod = true;
                    } else if (RestConstants.PRODUCE_MIME_ANNOTATION.equals(mAnonType)
                            || RestConstants.PRODUCE_MIME.equals(mAnonType)) {
                        List<String> mimes = getMimeAnnotationValue(mAnon);
                        if (mimes.contains(Constants.MimeType.JSON.value())
                                || mimes.contains(Constants.MimeType.XML.value())) {
                            isXmlMime = true;
                        }
                    }
                }
                if (isHttpGetMethod && isXmlMime) {
                    method = tree;
                    break;
                }
            }
        }
        return method;
    }

    public static String getNameFromMethod(MethodTree tree) {
        String attrName = tree.getName().toString();
        attrName = attrName.substring(attrName.indexOf("get") + 3);
        attrName = attrName.substring(0, 1).toLowerCase() + attrName.substring(1);
        return attrName;
    }

    public static String getValueFromAnnotation(AnnotationMirror annotation) {
        return getValueFromAnnotation(annotation.getElementValues().values().toString());
    }

    public static String getValueFromAnnotation(AnnotationTree mAnon) {
        return getValueFromAnnotation(mAnon.toString());
    }

    public static String getValueFromAnnotation(ExpressionTree eAnon) {
        return getValueFromAnnotation(eAnon.toString());
    }

    public static String getValueFromAnnotation(String value) {
        if (value.indexOf("\"") != -1) {
            value = value.substring(value.indexOf("\"") + 1, value.lastIndexOf("\""));
        }
        return value;
    }

    public static List<String> getMimeAnnotationValue(AnnotationTree ant) {
        List<? extends ExpressionTree> ets = ant.getArguments();
        if (ets.size() > 0) {
            String value = getValueFromAnnotation(ets.get(0));
            value = value.replace("\"", "");
            return Arrays.asList(value.split(","));
        }
        return Collections.emptyList();
    }

    public static String escapeJSReserved(String key) {
        if (key.equals("delete")) {
            return key + "_";
        } else {
            return key;
        }
    }

    public static J2eeModule getJ2eeModule(Project prj) {
        J2eeModuleProvider provider = prj.getLookup().lookup(J2eeModuleProvider.class);
        if (provider != null) {
            return provider.getJ2eeModule();
        }
        return null;
    }

    public static boolean isAnnotationConfigAvailable(Project project) throws IOException {
        RestSupport restSupport = project.getLookup().lookup(RestSupport.class);
        if (restSupport != null) {
            if (restSupport.hasSpringSupport()) {
                return false;
            }
        }

        if (MiscUtilities.isJavaEE6AndHigher(project)) {
            SourceGroup[] sourceGroups = SourceGroupSupport
                    .getJavaSourceGroups(project);
            if (sourceGroups.length > 0) {
                ClassPath cp = ClassPath.getClassPath(
                        sourceGroups[0].getRootFolder(), ClassPath.COMPILE);
                if (cp != null && cp.findResource(
                        "javax/ws/rs/ApplicationPath.class") != null // NOI18N
                        && cp.findResource(
                                "javax/ws/rs/core/Application.class") != null)// NOI18N
                {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isJSR_311OnClasspath(Project project) throws IOException {
        return hasClass(project, "javax/ws/rs/Path.class");   // NOI18N
    }

    public static boolean hasClass(Project project, String fqn) throws IOException {
        WebModule wm = WebModule.getWebModule(project.getProjectDirectory());
        if (wm != null) {
            SourceGroup[] sourceGroups = SourceGroupSupport.getJavaSourceGroups(project);
            if (sourceGroups.length > 0) {
                ClassPath cp = ClassPath.getClassPath(sourceGroups[0].getRootFolder(),
                        ClassPath.COMPILE);
                if (cp != null && cp.findResource(fqn) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public static FileObject createApplicationConfigClass(final RestSupport restSupport, RestConfigData configData, 
            FileObject packageFolder, String appPackage,
            List<RestApp> singletonClasses, List<String> providerClasses, ProgressHandler handler) throws IOException {
        
         final String className = configData.getApplicationClass();
       final String applicationPath = configData.getApplicationPath();
       
        FileObject configFO = packageFolder.getFileObject(className, JAVA_EXT);
        if (configFO != null) {
            configFO.delete();
        }

        FileObject appClass = GenerationUtils.createClass(packageFolder, className, null);
        JavaSource javaSource = JavaSource.forFileObject(appClass);
        if (javaSource == null) {
            return null;
        }
        handler.progress(className);

        javaSource.runModificationTask((WorkingCopy workingCopy) -> {
            workingCopy.toPhase(JavaSource.Phase.RESOLVED);
            JavaSourceHelper.addClassAnnotation(workingCopy,
                    new String[]{"javax.ws.rs.ApplicationPath"},
                    new String[]{applicationPath});         // NOI18N
            ClassTree tree = JavaSourceHelper.getTopLevelClassTree(workingCopy);//
            TreeMaker maker = workingCopy.getTreeMaker();
            ClassTree newTree = maker.setExtends(tree,
                    maker.QualIdent(JAX_RS_APPLICATION_CLASS)); // NOI18N
            
            
            //Singletons imports
            TypeElement classElement = SourceUtils.getPublicTopLevelElement(workingCopy);
            GenerationUtils genUtils = GenerationUtils.newInstance(workingCopy);
            if(singletonClasses!=null){
                for (RestApp app : singletonClasses) {
                    if (app == RestApp.METRICS) {
                        String metricPackage = StringUtils.isBlank(appPackage)? "metrics" : appPackage + ".metrics" ;
                        VariableTree variableTree = maker.Variable(maker.addModifiersAnnotation(genUtils.createModifiers(Modifier.PRIVATE), genUtils.createAnnotation(INJECT)),
                                "metricsConfigurer", genUtils.createType(metricPackage + ".MetricsConfigurer" , classElement), null);
                        newTree = maker.addClassMember(newTree, variableTree);
                    }
                }
            }
            
            newTree = createGetClasses(workingCopy, maker, newTree, restSupport, providerClasses);
            newTree = MiscUtilities.createAddResourceClasses(maker, newTree, workingCopy, "{}", true);
            newTree = createGetSingletons(workingCopy, maker, newTree, singletonClasses);
            
            
            workingCopy.rewrite(tree, newTree);
        }).commit();
        return appClass;
    }

    private static ClassTree createGetClasses(WorkingCopy workingCopy, TreeMaker maker, ClassTree newTree, RestSupport restSupport, List<String> providerClasses) {

        ModifiersTree modifiersTree = maker.Modifiers(
                EnumSet.of(Modifier.PUBLIC), Collections.singletonList(
                maker.Annotation(maker.QualIdent(
                        Override.class.getCanonicalName()),
                        Collections.<ExpressionTree>emptyList())));

        WildcardTree wildCard = maker.Wildcard(Tree.Kind.UNBOUNDED_WILDCARD,
                null);
        ParameterizedTypeTree wildClass = maker.ParameterizedType(
                maker.QualIdent(Class.class.getCanonicalName()),
                Collections.singletonList(wildCard));
        ParameterizedTypeTree wildSet = maker.ParameterizedType(
                maker.QualIdent(Set.class.getCanonicalName()),
                Collections.singletonList(wildClass));

        MethodTree methodTree = maker.Method(modifiersTree,
                RestConstants.GET_CLASSES, wildSet,
                Collections.<TypeParameterTree>emptyList(),
                Collections.<VariableTree>emptyList(),
                Collections.<ExpressionTree>emptyList(),
                createBodyForGetClassesMethod(restSupport, providerClasses), null);
        return maker.addClassMember(newTree, methodTree);
    }
    
    
    public static String createBodyForGetClassesMethod(RestSupport restSupport, List<String> provideClasses) {
        StringBuilder builder = new StringBuilder();
        builder.append('{');
        builder.append("Set<Class<?>> resources = new java.util.HashSet<>();");// NOI18N
        if (provideClasses != null) {
            for (String _class : provideClasses) {
                builder.append("resources.add(").append(_class).append(".class);");
            }
        }
        builder.append(RestConstants.GET_REST_RESOURCE_CLASSES2+"(resources);");
        builder.append("return resources;}");
        return builder.toString();
    }

    private static ClassTree createGetSingletons(WorkingCopy workingCopy, TreeMaker maker, ClassTree newTree, List<RestApp> singletonClasses) {
        if (singletonClasses == null || singletonClasses.isEmpty()) {
            return newTree;
        }

        //add import
        CompilationUnitTree cut = workingCopy.getCompilationUnit();
        CompilationUnitTree newCut = maker.addCompUnitImport(cut, maker.Import(maker.Identifier(HashSet.class.getCanonicalName()), false));
        newCut = maker.addCompUnitImport(newCut, maker.Import(maker.Identifier(Set.class.getCanonicalName()), false));

        ModifiersTree modifiersTree = maker.Modifiers(
                EnumSet.of(Modifier.PUBLIC), Collections.singletonList(
                maker.Annotation(maker.QualIdent(Override.class.getCanonicalName()),
                        Collections.<ExpressionTree>emptyList())));

        ParameterizedTypeTree wildSet = maker.ParameterizedType(
                maker.QualIdent(Set.class.getCanonicalName()),
                Collections.singletonList(maker.QualIdent(Object.class.getCanonicalName())));

        StringBuilder body = new StringBuilder();
        body.append("{final Set<Object> instances = new HashSet<>();\n");
        if(singletonClasses!=null){
        for (RestApp app : singletonClasses) {
            body.append("instances.add(").append(app.getBody()).append(");\n");
            for(String _import : app.getImports()){
                newCut = maker.addCompUnitImport(newCut, maker.Import(maker.Identifier(_import), false));
            }
        }
        }
        
        body.append("return instances;}");

        MethodTree methodTree = maker.Method(modifiersTree,
                SINGLETON_METHOD, wildSet,
                Collections.<TypeParameterTree>emptyList(),
                Collections.<VariableTree>emptyList(),
                Collections.<ExpressionTree>emptyList(),
                body.toString(), null);

        workingCopy.rewrite(cut, newCut);//update import
        return maker.addClassMember(newTree, methodTree);
    }

    public static boolean hasProfile(Project project, Profile... profiles) {
        WebModule webModule = WebModule.getWebModule(project.getProjectDirectory());
        if (webModule != null) {
            Profile projectProfile = webModule.getJ2eeProfile();
            for (Profile profile : profiles) {
                if (projectProfile == profile) {
                    return true;
                }
            }
        }
        return false;
    }

}
