/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.mvc.util;

import static io.github.jeddict.jcode.util.JavaUtil.removeBeanMethodPrefix;
import io.github.jeddict.jcode.util.StringHelper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import org.netbeans.modules.j2ee.persistence.wizard.jpacontroller.JpaControllerUtil;

public class CustomJpaControllerUtil extends JpaControllerUtil {

    /**
     * Returns all methods in class and its super classes which are entity
     * classes or mapped superclasses in order of superclasses.
     */
    public static ExecutableElement[] getEntityMethodsBySuperClass(TypeElement entityTypeElement) {
        List<ExecutableElement> result = new LinkedList<>();
        TypeElement typeElement = entityTypeElement;
        List<TypeElement> typeElements = new LinkedList<>();

        // First we will traverse up the element chain to get to the super classes
        while (typeElement != null) {
            if (isAnnotatedWith(typeElement, "jakarta.persistence.Entity") || isAnnotatedWith(typeElement, "jakarta.persistence.MappedSuperclass")) { // NOI18N
                typeElements.add(typeElement);
            }
            typeElement = getSuperclassTypeElement(typeElement);
        }

        // Now we will reverse the element order to get to the top element first
        Collections.reverse(typeElements);

        // Finally, grab the methods for each element
        for (TypeElement reversedElement : typeElements) {
            result.addAll(ElementFilter.methodsIn(reversedElement.getEnclosedElements()));
        }
        return result.toArray(new ExecutableElement[result.size()]);
    }

    public static String getPropNameFromMethod(String name) {
        //getABcd should be converted to ABcd, getFooBar should become fooBar
        //getA1 is "a1", getA_ is a_, getAB is AB
        //in case method doesn't start with "get" return name with brackets
        if (!name.startsWith("get") && !name.startsWith("set") && !name.startsWith("is")) {  //NOI18N
            return name + "()";   //NOI18n
        }

        // First, remove getter prefix
        name = removeBeanMethodPrefix(name);

        // Now proceed with making first character lower according to logic
        // outline in the comment above.
        return StringHelper.firstLower(name);
    }

    public static boolean isReadOnly(ExecutableElement getterMethod) throws NotGetterMethodException {

        // Determine the setter name to search for
        String setterMethod = getterMethod.getSimpleName().toString();
        if (setterMethod.startsWith("get")) {
            setterMethod = setterMethod.replaceFirst("get", "set");
        } else if (setterMethod.startsWith("is")) {
            setterMethod = setterMethod.replaceFirst("is", "set");
        } else {
            throw new NotGetterMethodException();
        }

        // Try to find a matching setter method from a list of methods in the
        // enclosing type element
        TypeElement enclosingElementType = (TypeElement) getterMethod.getEnclosingElement();
        List<ExecutableElement> enclosedMethods = new LinkedList<>();
        enclosedMethods.addAll(ElementFilter.methodsIn(enclosingElementType.getEnclosedElements()));
        for (ExecutableElement method : enclosedMethods) {
            if (method.getSimpleName().toString().equals(setterMethod)) {
                return false;
            }
        }

        // No setter found? Then it's a Read-Only property.
        return true;
    }
    // ----------------------------------------------------------------------------------------- Nested Classes

    public static class CustomEmbeddedPkSupport extends EmbeddedPkSupport {

        private Map<TypeElement, EmbeddedPkSupportInfo> typeToInfo = new HashMap<>();

        @Override
        public Set<ExecutableElement> getPkAccessorMethods(TypeElement type) {
            EmbeddedPkSupportInfo info = getInfo(type);
            return info.getPkAccessorMethods();
        }

        @Override
        public boolean getPkSetterMethodExist(TypeElement type, ExecutableElement getter) {
            EmbeddedPkSupportInfo info = getInfo(type);
            String column = info.getReferencedColumnName(getter);
            return info.getSetterString(column) != null;
        }

        @Override
        public String getCodeToPopulatePkField(TypeElement type, ExecutableElement pkAccessorMethod) {
            EmbeddedPkSupportInfo info = getInfo(type);
            String code = info.getCodeToPopulatePkField(pkAccessorMethod);
            if (code != null) {
                return code;
            }

            code = "";
            ExecutableElement relationshipMethod = info.getRelationshipMethod(pkAccessorMethod);
            String referencedColumnName = info.getReferencedColumnName(pkAccessorMethod);
            if (relationshipMethod == null || referencedColumnName == null) {
                info.putCodeToPopulatePkField(pkAccessorMethod, code);
                return code;
            }

            TypeMirror relationshipTypeMirror = relationshipMethod.getReturnType();
            if (TypeKind.DECLARED != relationshipTypeMirror.getKind()) {
                info.putCodeToPopulatePkField(pkAccessorMethod, code);
                return code;
            }
            DeclaredType declaredType = (DeclaredType) relationshipTypeMirror;
            TypeElement relationshipType = (TypeElement) declaredType.asElement();

            EmbeddedPkSupportInfo relatedInfo = getInfo(relationshipType);
            String accessorString = relatedInfo.getAccessorString(referencedColumnName);
            if (accessorString == null) {
                info.putCodeToPopulatePkField(pkAccessorMethod, code);
                return code;
            }

            code = relationshipMethod.getSimpleName().toString() + "()." + accessorString;
            info.putCodeToPopulatePkField(pkAccessorMethod, code);
            return code;
        }

        @Override
        public boolean isRedundantWithRelationshipField(TypeElement type, ExecutableElement pkAccessorMethod) {
            return getCodeToPopulatePkField(type, pkAccessorMethod).length() > 0;
        }

        @Override
        public boolean isRedundantWithPkFields(TypeElement type, ExecutableElement relationshipMethod) {
            EmbeddedPkSupportInfo info = getInfo(type);
            return info.isRedundantWithPkFields(relationshipMethod);
        }

        private EmbeddedPkSupportInfo getInfo(TypeElement type) {
            EmbeddedPkSupportInfo info = typeToInfo.get(type);
            if (info == null) {
                info = new EmbeddedPkSupportInfo(type);
                typeToInfo.put(type, info);
            }
            return info;
        }
    }

    private static class EmbeddedPkSupportInfo {

        private Map<String, ExecutableElement> joinColumnNameToRelationshipMethod = new LinkedHashMap<>();
        private Map<ExecutableElement, List<String>> relationshipMethodToJoinColumnNames = new LinkedHashMap<>(); //used only in isRedundantWithPkFields
        private Map<String, String> joinColumnNameToReferencedColumnName = new LinkedHashMap<>();
        private Map<String, String> columnNameToAccessorString = new LinkedHashMap<>();
        private Map<String, String> columnNameToSetterString = new LinkedHashMap<>();
        private Map<ExecutableElement, String> pkAccessorMethodToColumnName = new LinkedHashMap<>();
        private Map<ExecutableElement, String> pkSetterMethodToColumnName = new LinkedHashMap<>();
        private Map<ExecutableElement, String> pkAccessorMethodToPopulationCode = new LinkedHashMap<>(); //derived
        private boolean isFieldAccess;

        public Set<ExecutableElement> getPkAccessorMethods() {
            return pkAccessorMethodToColumnName.keySet();
        }

        public ExecutableElement getRelationshipMethod(ExecutableElement pkAccessorMethod) {
            String columnName = pkAccessorMethodToColumnName.get(pkAccessorMethod);
            if (columnName == null) {
                return null;
            }
            return joinColumnNameToRelationshipMethod.get(columnName);
        }

        public String getReferencedColumnName(ExecutableElement pkAccessorMethod) {
            String columnName = pkAccessorMethodToColumnName.get(pkAccessorMethod);
            if (columnName == null) {
                return null;
            }
            return joinColumnNameToReferencedColumnName.get(columnName);
        }

        public String getAccessorString(String columnName) {
            return columnNameToAccessorString.get(columnName);
        }

        public String getSetterString(String columnName) {
            return columnNameToSetterString.get(columnName);
        }

        public String getCodeToPopulatePkField(ExecutableElement pkAccessorMethod) {
            return pkAccessorMethodToPopulationCode.get(pkAccessorMethod);
        }

        public void putCodeToPopulatePkField(ExecutableElement pkAccessorMethod, String code) {
            pkAccessorMethodToPopulationCode.put(pkAccessorMethod, code);
        }

        public boolean isRedundantWithPkFields(ExecutableElement relationshipMethod) {
            List<String> joinColumnNameList = relationshipMethodToJoinColumnNames.get(relationshipMethod);
            if (joinColumnNameList == null) {
                return false;
            }
            Collection<String> pkColumnNames = pkAccessorMethodToColumnName.values();
            for (String columnName : joinColumnNameList) {
                if (!pkColumnNames.contains(columnName)) {
                    return false;
                }
            }
            return true;
        }

        EmbeddedPkSupportInfo(TypeElement type) {
            isFieldAccess = isFieldAccess(type);
            for (ExecutableElement method : getEntityMethods(type)) {
                String methodName = method.getSimpleName().toString();
                if (methodName.startsWith("get")) {
                    Element f = isFieldAccess ? guessField(method) : method;
                    if (f != null) {
                        int a = -1;
                        AnnotationMirror columnAnnotation = null;
                        String[] columnAnnotationFqns = {"jakarta.persistence.EmbeddedId", "jakarta.persistence.JoinColumns", "jakarta.persistence.JoinColumn", "jakarta.persistence.Column"}; //NOI18N
                        for (int i = 0; i < columnAnnotationFqns.length; i++) {
                            String columnAnnotationFqn = columnAnnotationFqns[i];
                            AnnotationMirror columnAnnotationMirror = findAnnotation(f, columnAnnotationFqn);
                            if (columnAnnotationMirror != null) {
                                a = i;
                                columnAnnotation = columnAnnotationMirror;
                                break;
                            }
                        }
                        if (a == 0) {
                            //populate pkAccessorMethodToColumnName and columnNameToAccessorString
                            populateMapsForEmbedded(method);
                        } else if ((a == 1 || a == 2)
                                && (isAnnotatedWith(f, "jakarta.persistence.OneToOne")
                                || isAnnotatedWith(f, "jakarta.persistence.ManyToOne"))) {
                            //populate joinColumnNameToRelationshipMethod, relationshipMethodToJoinColumnNames, and joinColumnNameToReferencedColumnName
                            populateJoinColumnNameMaps(method, columnAnnotationFqns[a], columnAnnotation);
                        } else if (a == 3) {
                            //populate columnNameToAccessorString
                            String columnName = findAnnotationValueAsString(columnAnnotation, "name"); //NOI18N
                            if (columnName != null) {
                                columnNameToAccessorString.put(columnName, method.getSimpleName().toString() + "()");
                            }
                        }
                    }
                }
            }
        }

        private void populateMapsForEmbedded(ExecutableElement idGetterElement) {
            TypeMirror idType = idGetterElement.getReturnType();
            if (TypeKind.DECLARED != idType.getKind()) {
                return;
            }
            DeclaredType declaredType = (DeclaredType) idType;
            TypeElement idClass = (TypeElement) declaredType.asElement();

            for (ExecutableElement pkMethod : ElementFilter.methodsIn(idClass.getEnclosedElements())) {
                String pkMethodName = pkMethod.getSimpleName().toString();
                if (pkMethodName.startsWith("get")) {
                    String columnName = guessColumnName(pkMethod);
                    if (columnName != null && columnName.length() > 0) {
                        pkAccessorMethodToColumnName.put(pkMethod, columnName);
                        columnNameToAccessorString.put(columnName,
                                idGetterElement.getSimpleName().toString() + "()."
                                + pkMethod.getSimpleName() + "()");
                    }
                } else if (pkMethodName.startsWith("set")) {
                    Element pkFieldElement = isFieldAccess ? guessField(pkMethod) : guessGetter(pkMethod);
                    if (pkFieldElement != null) {//we do not need setters not associated with fields/properties
                        String columnName = guessColumnName(pkMethod);
                        if (columnName != null && columnName.length() > 0) {
                            pkSetterMethodToColumnName.put(pkMethod, columnName);
                            columnNameToSetterString.put(columnName,
                                    idGetterElement.getSimpleName().toString() + "()."
                                    + pkMethod.getSimpleName() + "()");
                        }
                    }
                }
            }
        }

        private String guessColumnName(ExecutableElement pkMethod) {
            Element pkFieldvariable = guessField(pkMethod);
            Element pkFieldElement = isFieldAccess ? pkFieldvariable : guessGetter(pkMethod);
            if (pkFieldElement == null) {
                return null;
            }//something is missed, may be getter name do not match variable name, see #190854
            String pkMethodName = pkMethod.getSimpleName().toString();
            String columnName = null;
            AnnotationMirror columnAnnotation = findAnnotation(pkFieldElement, "jakarta.persistence.Column"); //NOI18N
            if (columnAnnotation != null) {
                columnName = findAnnotationValueAsString(columnAnnotation, "name"); //NOI18N
            }
            if (columnName == null) {
                //it's not necessary to annotate with @Column and have name also, it;s optional in JPA1.0/2.0 and may be later
                if (pkFieldvariable.getModifiers().contains(Modifier.TRANSIENT)) {
                    return null;
                }//do not store transient fields
                if (isFieldAccess) {
                    columnName = pkFieldvariable.getSimpleName().toString().toUpperCase();
                } else {
                    columnName = pkMethodName.substring(3).toUpperCase();
                }
            }
            return columnName;
        }

        private void populateJoinColumnNameMaps(ExecutableElement m, String columnAnnotationFqn, AnnotationMirror columnAnnotation) {
            List<AnnotationMirror> joinColumnAnnotations;
            if ("jakarta.persistence.JoinColumn".equals(columnAnnotationFqn)) {
                joinColumnAnnotations = new ArrayList<>();
                joinColumnAnnotations.add(columnAnnotation);
            } else {  //columnAnnotation is a jakarta.persistence.JoinColumns
                joinColumnAnnotations = findNestedAnnotations(columnAnnotation, "jakarta.persistence.JoinColumn"); //NOI18N
            }
            for (AnnotationMirror joinColumnAnnotation : joinColumnAnnotations) {
                String columnName = findAnnotationValueAsString(joinColumnAnnotation, "name"); //NOI18N
                if (columnName != null) {
                    String referencedColumnName = findAnnotationValueAsString(joinColumnAnnotation, "referencedColumnName"); //NOI18N
                    joinColumnNameToRelationshipMethod.put(columnName, m);
                    joinColumnNameToReferencedColumnName.put(columnName, referencedColumnName);
                    List<String> joinColumnNameList = relationshipMethodToJoinColumnNames.get(m);
                    if (joinColumnNameList == null) {
                        joinColumnNameList = new ArrayList<>();
                        relationshipMethodToJoinColumnNames.put(m, joinColumnNameList);
                    }
                    joinColumnNameList.add(columnName);
                }
            }
        }
    }

}
