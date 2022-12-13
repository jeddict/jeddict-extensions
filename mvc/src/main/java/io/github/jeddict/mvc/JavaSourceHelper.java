/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.jeddict.mvc;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.java.source.TreeMaker;
import io.github.jeddict.mvc.GenerationUtils;
import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import io.github.jeddict.jcode.task.AbstractTask;
import io.github.jeddict.jcode.util.Constants;
import static io.github.jeddict.jcode.util.JavaSourceHelper.*;
import static io.github.jeddict.mvc.JavaSourceHelper.createModifiersTree;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.project.Project;
import org.openide.util.Exceptions;

/**
 *
 * @author jGauravGupta
 */
public class JavaSourceHelper {

    public static ClassTree addBeanProperty(GenerationUtils genUtils, TreeMaker maker, TypeElement classElement, ClassTree classTree, String varName, String varType) {
        VariableTree field = genUtils.createField(classElement, genUtils.createModifiers(Modifier.PRIVATE), varName, varType, null);
        MethodTree getter = genUtils.createPropertyGetterMethod(classElement, genUtils.createModifiers(Modifier.PUBLIC), varName, varType);
        MethodTree setter = genUtils.createPropertySetterMethod(classElement, genUtils.createModifiers(Modifier.PUBLIC), varName, varType);
        ClassTree newClassTree = classTree;
        newClassTree = maker.insertClassMember(newClassTree, 0, field);
        newClassTree = maker.addClassMember(newClassTree, getter);
        newClassTree = maker.addClassMember(newClassTree, setter);
        return newClassTree;
    }

    public static void addClassAnnotation(WorkingCopy copy, String[] annotations, Object[] annotationAttrs) {
        TreeMaker maker = copy.getTreeMaker();
        ClassTree tree = getTopLevelClassTree(copy);
        if (tree == null) {
            return;
        }

        ModifiersTree modifiers = tree.getModifiers();

        for (int i = 0; i < annotations.length; i++) {
            List<ExpressionTree> attrTrees = null;
            Object attr = annotationAttrs[i];

            if (attr != null) {
                attrTrees = new ArrayList<>();

                if (attr instanceof ExpressionTree) {
                    attrTrees.add((ExpressionTree) attr);
                } else {
                    attrTrees.add(maker.Literal(attr));
                }
            } else {
                attrTrees = Collections.<ExpressionTree>emptyList();
            }

            AnnotationTree newAnnotation = maker.Annotation(maker.Identifier(annotations[i]), attrTrees);

            if (modifiers != null) {
                modifiers = maker.addModifiersAnnotation(modifiers, newAnnotation);
            }
        }

        copy.rewrite(tree.getModifiers(), modifiers);
    }

    public static ExpressionTree createTypeTree(WorkingCopy copy, Object type) {
        if (type instanceof String) {
            TypeElement element = copy.getElements().getTypeElement((String) type);
            if (element != null) {
                return copy.getTreeMaker().QualIdent(element);
            } else {
                return copy.getTreeMaker().Identifier((String) type);
            }
        } else {
            return (ExpressionTree) type;
        }
    }

    public static Collection<String> getImports(CompilationController controller) {
        Set<String> imports = new HashSet<>();
        CompilationUnitTree cu = controller.getCompilationUnit();

        if (cu != null) {
            List<? extends ImportTree> importTrees = cu.getImports();

            for (ImportTree importTree : importTrees) {
                imports.add(importTree.getQualifiedIdentifier().toString());
            }
        }

        return imports;
    }

    public static VariableTree getField(CompilationController controller, String fieldName) {
        TypeElement classElement = getTopLevelClassElement(controller);
        if (classElement == null) {
            return null;
        }
        List<VariableElement> fields = ElementFilter.fieldsIn(classElement.getEnclosedElements());

        for (VariableElement field : fields) {
            if (field.getSimpleName().toString().equals(fieldName)) {
                return (VariableTree) controller.getTrees().getTree(field);
            }
        }

        return null;
    }

    public static ClassTree addField(WorkingCopy copy, ClassTree tree, Modifier[] modifiers, String[] annotations, Object[] annotationAttrs, String name, Object type) {
        return addField(copy, tree, modifiers, annotations, annotationAttrs, name, type, null);
    }

    public static ClassTree addField(WorkingCopy copy, ClassTree tree, Modifier[] modifiers, String[] annotations, Object[] annotationAttrs, String name, Object type, Object initialValue) {

        TreeMaker maker = copy.getTreeMaker();
        ClassTree modifiedTree = tree;

        Tree typeTree = createTypeTree(copy, type);

        ModifiersTree modifiersTree = createModifiersTree(copy, modifiers, annotations, annotationAttrs);

        ExpressionTree init = initialValue == null ? null : maker.Literal(initialValue);

        VariableTree variableTree = maker.Variable(modifiersTree, name, typeTree, init);

        return maker.insertClassMember(modifiedTree, 0, variableTree);
    }

    public static void addFields(WorkingCopy copy, String[] names, Object[] types, Object[] initialValues, Modifier[] modifiers) {

        TreeMaker maker = copy.getTreeMaker();
        ClassTree classTree = getTopLevelClassTree(copy);
        ClassTree modifiedTree = classTree;
        String[] annotations = new String[0];
        Object[] annotationAttrs = new Object[0];

        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            Object type = types[i];
            Object initialValue = initialValues[i];
            // get around Retouche lack support for non-java.lang type Literal ???
            if (initialValue instanceof Enum) {
                continue;
            }

            Tree typeTree = createTypeTree(copy, type);

            ModifiersTree modifiersTree = createModifiersTree(copy, modifiers, annotations, annotationAttrs);
            ExpressionTree init = initialValue == null ? null : maker.Literal(initialValue);
            VariableTree variableTree = maker.Variable(modifiersTree, name, typeTree, init);
            modifiedTree = maker.insertClassMember(modifiedTree, 0, variableTree);
        }
        copy.rewrite(classTree, modifiedTree);
    }

    public static ModifiersTree createModifiersTree(WorkingCopy copy, Modifier[] modifiers, String[] annotations, Object[] annotationAttrs) {
        TreeMaker maker = copy.getTreeMaker();
        Set<Modifier> modifierSet = new HashSet<>();

        for (Modifier modifier : modifiers) {
            modifierSet.add(modifier);
        }

        List<AnnotationTree> annotationTrees = createAnnotationTrees(copy, annotations, annotationAttrs);

        return maker.Modifiers(modifierSet, annotationTrees);
    }

    private static List<AnnotationTree> createAnnotationTrees(WorkingCopy copy, String[] annotations, Object[] annotationAttrs) {
        TreeMaker maker = copy.getTreeMaker();
        List<AnnotationTree> annotationTrees = null;

        if (annotations != null) {
            annotationTrees = new ArrayList<>();

            for (int i = 0; i < annotations.length; i++) {
                String annotation = annotations[i];

                List<ExpressionTree> expressionTrees = Collections.<ExpressionTree>emptyList();

                if (annotationAttrs != null) {
                    Object attr = annotationAttrs[i];

                    if (attr != null) {
                        expressionTrees = new ArrayList<>();

                        if (attr instanceof ExpressionTree) {
                            expressionTrees.add((ExpressionTree) attr);
                        } else {
                            expressionTrees.add(maker.Literal(attr));
                        }
                    }
                }

                annotationTrees.add(maker.Annotation(maker.Identifier(annotation), expressionTrees));
            }
        } else {
            annotationTrees = Collections.<AnnotationTree>emptyList();
        }

        return annotationTrees;
    }

    public static TypeElement getTypeElement(CompilationController controller, ClassTree classTree) {
        TreePath classTreePath = controller.getTrees().getPath(controller.getCompilationUnit(), classTree);
        return (TypeElement) controller.getTrees().getElement(classTreePath);
    }

    public static TypeElement getTypeElement(CompilationController controller, TreePath treePath) {
        return (TypeElement) controller.getTrees().getElement(treePath);
    }

    public static void addFields(WorkingCopy copy, String[] names, Object[] types) {
        Object[] initValues = new Object[types.length];
        for (int i = 0; i < types.length; i++) {
            Object type = types[i];
            if (String.class.equals(type) || String.class.getName().equals(type)) {
                initValues[i] = "";
            } else if (type instanceof Class && Number.class.isAssignableFrom((Class) type)) {
                initValues[i] = 0;
            } else {
                initValues[i] = null;
            }
        }
        addFields(copy, names, types, initValues);
    }

    public static void addFields(WorkingCopy copy, String[] names, Object[] types, Object[] initialValues) {
        addFields(copy, names, types, initialValues, Constants.PRIVATE);
    }

    public static List<JavaSource> getEntityClasses(Project project) {
        List<JavaSource> sources = getJavaSources(project);
        List<JavaSource> entityClasses = new ArrayList<>();

        for (JavaSource source : sources) {
            if (isEntity(source)) {
                entityClasses.add(source);
            }
        }

        return entityClasses;
    }

    public static boolean isEntity(JavaSource source) {
        final boolean[] isBoolean = new boolean[1];

        try {
            source.runUserActionTask(new AbstractTask<CompilationController>() {

                public void run(CompilationController controller) throws IOException {
                    controller.toPhase(JavaSource.Phase.ELEMENTS_RESOLVED);

                    TypeElement classElement = getTopLevelClassElement(controller);
                    if (classElement == null) {
                        return;
                    }

                    List<? extends AnnotationMirror> annotations = controller.getElements().getAllAnnotationMirrors(classElement);

                    for (AnnotationMirror annotation : annotations) {
                        if (annotation.toString().equals("@jakarta.persistence.Entity")) {
                            //NOI18N
                            isBoolean[0] = true;

                            break;
                        }
                    }
                }
            }, true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        return isBoolean[0];
    }

}
