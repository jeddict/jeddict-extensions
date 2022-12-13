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
package io.github.jeddict.mvc.viewer.dto;

import io.github.jeddict.jcode.util.JavaIdentifiers;
import io.github.jeddict.mvc.util.CustomJpaControllerUtil;
import io.github.jeddict.mvc.util.NotGetterMethodException;
import static io.github.jeddict.jcode.util.JavaUtil.isGetterMethod;
import io.github.jeddict.jcode.util.StringHelper;
import static io.github.jeddict.mvc.viewer.dto.HtmlType.CHECKBOX;
import static io.github.jeddict.mvc.viewer.dto.HtmlType.DATE;
import static io.github.jeddict.mvc.viewer.dto.HtmlType.INVALID;
import static io.github.jeddict.mvc.viewer.dto.HtmlType.NUMBER;
import static io.github.jeddict.mvc.viewer.dto.HtmlType.TEXT;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Types;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.Task;
import static io.github.jeddict.jcode.util.JavaIdentifiers.getPackageName;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

public abstract class FromEntityBase {

    private static final String RELATION_LABEL_ANNOTATION = "RelationLabel";

    private static final String ITEM_VAR = "item";
    private boolean readOnly = false;

    protected abstract boolean isCollectionComponent();

    protected abstract boolean showReadOnlyFormFlag();

    protected abstract String getDialogTitle();

    protected abstract String getTemplate();

    protected final boolean isReadOnlyForm() {
        return readOnly;
    }

    public static Map<String, Object> createFieldParameters(FileObject targetJspFO, final String entityClass,
            final String managedBean, final String managedBeanProperty, final boolean collectionComponent,
            final boolean initValueGetters) throws IOException {
        final Map<String, Object> params = new HashMap<>();
        JavaSource javaSource = JavaSource.create(EntityClass.createClasspathInfo(targetJspFO));
        javaSource.runUserActionTask((CompilationController controller) -> {
            controller.toPhase(JavaSource.Phase.ELEMENTS_RESOLVED);
            TypeElement typeElement = controller.getElements().getTypeElement(entityClass);
            enumerateEntityFields(params, controller, typeElement, managedBeanProperty, collectionComponent, initValueGetters);
        }, true);
        params.put("managedBean", managedBean); // NOI18N
        params.put("managedBeanProperty", managedBeanProperty); // NOI18N
        params.put("entityLabel", StringHelper.toNatural(JavaIdentifiers.unqualify(entityClass)));
        String entityName = StringHelper.firstLower(JavaIdentifiers.unqualify(entityClass));
        params.put("entityName", entityName); // NOI18N
        params.put("entityConstant", StringHelper.toConstant(entityName)); // NOI18N
        return params;
    }

    private static void enumerateEntityFields(Map<String, Object> params, CompilationController controller,
            TypeElement bean, String managedBeanProperty, boolean collectionComponent, boolean initValueGetters) {
        List<TemplateData> templateData = new ArrayList<>();
        List<FieldDesc> fields = new ArrayList<>();
        String idFieldName = "";
        if (bean != null) {
            ExecutableElement[] methods = CustomJpaControllerUtil.getEntityMethodsBySuperClass(bean);
            CustomJpaControllerUtil.CustomEmbeddedPkSupport embeddedPkSupport = null;
            for (ExecutableElement method : methods) {
                // filter out @Transient methods
                FieldDesc fd = new FieldDesc(controller, method, bean, initValueGetters);
                Element element = method;
                if(fd.isFieldAccess()){
                    element = fd.getFieldElement();
                }
                if (CustomJpaControllerUtil.findAnnotation(element, "jakarta.persistence.Transient") != null) { //NOI18N
                    continue;
                }
                if (fd.isValid()) {
                    int relationship = fd.getRelationship();

                    //Does field have an autogenerated value?
                    fd.setGeneratedValue(EntityClass.isAutoGenerated(method, fd.isFieldAccess()));

                    if (EntityClass.isId(method, fd.isFieldAccess())) {
                        fd.setPrimaryKey();
                        idFieldName = fd.getPropertyName();
                        TypeMirror rType = method.getReturnType();
                        if (TypeKind.DECLARED == rType.getKind()) {
                            DeclaredType rTypeDeclared = (DeclaredType) rType;
                            TypeElement rTypeElement = (TypeElement) rTypeDeclared.asElement();
                            if (CustomJpaControllerUtil.isEmbeddableClass(rTypeElement)) {
                                if (embeddedPkSupport == null) {
                                    embeddedPkSupport = new CustomJpaControllerUtil.CustomEmbeddedPkSupport();
                                }
                                String propName = fd.getPropertyName();
                                for (ExecutableElement pkMethod : embeddedPkSupport.getPkAccessorMethods(bean)) {
                                    String pkMethodName = pkMethod.getSimpleName().toString();
                                    fd = new FieldDesc(controller, pkMethod, bean);
                                    fd.setLabel(pkMethodName.substring(3));
                                    fd.setPropertyName(propName + "." + CustomJpaControllerUtil.getPropNameFromMethod(pkMethodName));
                                    fd.setEmbeddedKey();
                                    if (embeddedPkSupport.isRedundantWithRelationshipField(bean, pkMethod)) {
                                        fd.setKeyPartOfRelationshipFied();
                                    }
                                    fields.add(fd);
                                }
                            } else {
                                fields.add(fd);
                            }
                            continue;
                        } else {
                            //primitive types
                            fields.add(fd);
                        }
                    } else if (fd.getDateTimeFormat().length() > 0) {
                        fields.add(fd);
                    } else if (relationship == CustomJpaControllerUtil.REL_TO_ONE
                            || relationship == CustomJpaControllerUtil.REL_TO_MANY) {
                        if (embeddedPkSupport == null) {
                            embeddedPkSupport = new CustomJpaControllerUtil.CustomEmbeddedPkSupport();
                        }
                        if (embeddedPkSupport.isRedundantWithPkFields(bean, method)) {
                            fd.setEmbeddedKey();
                        }
                        fields.add(fd);
                    } else if (relationship == CustomJpaControllerUtil.REL_NONE) {
                        fields.add(fd);
                    }
                }
            }
        }

        processFields(params, templateData, controller, bean, fields, managedBeanProperty, collectionComponent);

        params.put("entityDescriptors", templateData); // NOI18N
        params.put("item", ITEM_VAR); // NOI18N
        params.put("comment", Boolean.FALSE); // NOI18N
        params.put("entityIdField", idFieldName); //pass along the ID field.
    }

    private static ExecutableElement findPrimaryKeyGetter(CompilationController controller, TypeElement bean) {
        ExecutableElement[] methods = CustomJpaControllerUtil.getEntityMethods(bean);
        for (ExecutableElement method : methods) {
            FieldDesc fd = new FieldDesc(controller, method, bean, false);
            if (fd.isValid()) {
                if (EntityClass.isId(method, fd.isFieldAccess())) {
                    return method;
                }
            }
        }
        return null;
    }

    private static void processFields(Map<String, Object> params, List<TemplateData> templateData,
            CompilationController controller, TypeElement bean, List<FieldDesc> fields, String managedBeanProperty,
            boolean collectionComponent) {
        for (FieldDesc fd : fields) {
            templateData.add(new TemplateData(fd, (collectionComponent ? ITEM_VAR : managedBeanProperty) + "."));
        }
    }

    public static void createParamsForConverterTemplate(final Map<String, Object> params, final FileObject targetJspFO,
            final String entityClass, final CustomJpaControllerUtil.CustomEmbeddedPkSupport embeddedPkSupport) throws IOException {
        JavaSource javaSource = JavaSource.create(EntityClass.createClasspathInfo(targetJspFO));
        javaSource.runUserActionTask(new Task<CompilationController>() {
            @Override
            public void run(CompilationController controller) throws IOException {
                controller.toPhase(JavaSource.Phase.ELEMENTS_RESOLVED);
                TypeElement typeElement = controller.getElements().getTypeElement(entityClass);
                createParamsForConverterTemplate(params, controller, typeElement, embeddedPkSupport);
            }
        }, true);
    }
    private static final String INDENT = "            "; // TODO: jsut reformat generated code

    private static void createParamsForConverterTemplate(Map<String, Object> params, CompilationController controller,
            TypeElement bean, CustomJpaControllerUtil.CustomEmbeddedPkSupport embeddedPkSupport) throws IOException {
        // primary key type:
        ExecutableElement primaryGetter = findPrimaryKeyGetter(controller, bean);
        StringBuffer key = new StringBuffer();
        StringBuffer stringKey = new StringBuffer();
        String keyType;
        String keyTypeFQN;
        //
        String keyBodyValue = null;
        String keyStringBodyValue = null;
        String keyGetterValue = "UNDEFINED_PK_GETTER";
        String keyTypeValue = "UNDEFINED_PK_TYPE";
        Boolean keyEmbeddedValue = Boolean.FALSE;        //
        Boolean keyDerivedValue = Boolean.FALSE;
        List<EmbeddedDesc> embeddedFields = new ArrayList<>();
        if (primaryGetter != null) {
            TypeMirror idType = primaryGetter.getReturnType();
            ExecutableElement primaryGetterDerived = null;
            if (TypeKind.DECLARED == idType.getKind()) {
                DeclaredType declaredType = (DeclaredType) idType;
                TypeElement idClass = (TypeElement) declaredType.asElement();
                boolean embeddable = idClass != null && CustomJpaControllerUtil.isEmbeddableClass(idClass);
                boolean isDirevideId = false;
                if (!embeddable && CustomJpaControllerUtil.haveId(idClass)) {//NOI18N
                    isDirevideId = CustomJpaControllerUtil.isRelationship(primaryGetter, CustomJpaControllerUtil.isFieldAccess(idClass)) != CustomJpaControllerUtil.REL_NONE;
                }
                if (isDirevideId) {
                    //it may be direved id, find id field in parent entity
                    primaryGetterDerived = findPrimaryKeyGetter(controller, idClass);
                    if (primaryGetterDerived != null) {
                        idType = primaryGetterDerived.getReturnType();
                        if (TypeKind.DECLARED == idType.getKind()) {
                            declaredType = (DeclaredType) idType;
                            idClass = (TypeElement) declaredType.asElement();
                            embeddable = idClass != null && CustomJpaControllerUtil.isEmbeddableClass(idClass);
                        }
                    } else {
                        idClass = null;//clean all, can't find getter in derived id
                    }
                }
                if (idClass != null) {
                    keyType = idClass.getSimpleName().toString();
                    keyTypeFQN = idClass.getQualifiedName().toString();
                    if (embeddable) {
                        keyEmbeddedValue = Boolean.TRUE;
                        int index = 0;

                        // embeddedPkSupport handling
                        Set<ExecutableElement> methods = embeddedPkSupport.getPkAccessorMethods(bean);
                        for (ExecutableElement pkMethod : methods) {
                            if (embeddedPkSupport.isRedundantWithRelationshipField(bean, pkMethod)) {
                                embeddedFields.add(new EmbeddedDesc(
                                        "s" + pkMethod.getSimpleName().toString().substring(1),
                                        embeddedPkSupport.getCodeToPopulatePkField(bean, pkMethod)));
                            }
                        }

                        for (ExecutableElement method : ElementFilter.methodsIn(idClass.getEnclosedElements())) {
                            if (method.getSimpleName().toString().startsWith("set")) {
                                addParam(key, stringKey, method.getSimpleName().toString(), index,
                                        keyType, keyTypeFQN, method.getParameters().get(0).asType());
                                index++;
                            }
                        }
                        if (index == 0) {
                            key.append(NbBundle.getMessage(FromEntityBase.class, "ERR_NO_SETTERS", new String[]{INDENT, keyTypeFQN, "Converter.getKey()"}));//NOI18N;
                            stringKey.append(NbBundle.getMessage(FromEntityBase.class, "ERR_NO_SETTERS", new String[]{INDENT, keyTypeFQN, "Converter.getKey()"}));//NOI18N;
                        }
                    } else {
                        addParam(key, stringKey, null, -1, keyType, keyTypeFQN, idType);
                    }
                } else {
                    keyTypeFQN = null;
                }
            } else {
                //keyType = getCorrespondingType(idType);
                keyTypeFQN = keyType = idType.toString();
                addParam(key, stringKey, null, -1, keyType, keyTypeFQN, idType);
            }
            if (keyTypeFQN != null) {
                keyTypeValue = keyTypeFQN;
                if (key.toString().endsWith("\n")) {
                    key.setLength(key.length() - 1);
                }
                keyBodyValue = key.toString();
                if (stringKey.toString().endsWith("\n")) {
                    stringKey.setLength(stringKey.length() - 1);
                }
                keyStringBodyValue = stringKey.toString();
                keyGetterValue = primaryGetter.getSimpleName().toString() + (primaryGetterDerived != null ? "()." + primaryGetterDerived.getSimpleName().toString() : "");
            }
        }
        //it's required to have getter for jsf creation
        params.put("keyBody", keyBodyValue != null ? keyBodyValue : NbBundle.getMessage(FromEntityBase.class, "ERR_NO_GETTERS", new String[]{INDENT, bean.getQualifiedName().toString(), "Converter.getKey()"}));
        params.put("keyStringBody", keyStringBodyValue != null ? keyStringBodyValue : NbBundle.getMessage(FromEntityBase.class, "ERR_NO_GETTERS", new String[]{INDENT, bean.getQualifiedName().toString(), "Converter.getKey()"}));
        params.put("keyGetter", keyGetterValue);//NOI18N
        params.put("keySetter", "s" + keyGetterValue.substring(1));//NOI18N
        params.put("keyType", keyTypeValue);//NOI18N
        params.put("keyEmbedded", keyEmbeddedValue);//NOI18N
        params.put("keyDerived", keyDerivedValue);//NOI18N
        if (embeddedFields.size() > 0) {
            params.put("embeddedIdFields", embeddedFields);
        }
    }

    private static void addParam(StringBuffer key, StringBuffer stringKey, String setter,
            int index, String keyType, String keyTypeFQN, TypeMirror idType) {
        if (index == 0) {
            key.append(INDENT + "String values[] = value.split(SEPARATOR_ESCAPED);\n");
            key.append(INDENT + "key = new " + keyTypeFQN + "();\n");
        }
        if (index > 0) {
            stringKey.append(INDENT + "sb.append(SEPARATOR);\n");
        }

        // do conversion
        String conversion = getConversionFromString(idType, index, keyType);

        if (setter != null) {
            key.append(INDENT + "key." + setter + "(" + conversion + ");\n");
            stringKey.append(INDENT + "sb.append(value.g" + setter.substring(1) + "());\n");
        } else {
            key.append(INDENT + "key = " + conversion + ";\n");
            stringKey.append(INDENT + "sb.append(value);\n");
        }
    }

    private static String getConversionFromString(TypeMirror idType, int index, String keyType) {
        String param = index == -1 ? "value" : "values[" + index + "]";
        if (TypeKind.BOOLEAN == idType.getKind()) {
            return "Boolean.parseBoolean(" + param + ")";
        } else if (TypeKind.BYTE == idType.getKind()) {
            return "Byte.parseByte(" + param + ")";
        } else if (TypeKind.CHAR == idType.getKind()) {
            return param + ".charAt(0)";
        } else if (TypeKind.DOUBLE == idType.getKind()) {
            return "Double.parseDouble(" + param + ")";
        } else if (TypeKind.FLOAT == idType.getKind()) {
            return "Float.parseFloat(" + param + ")";
        } else if (TypeKind.INT == idType.getKind()) {
            return "Integer.parseInt(" + param + ")";
        } else if (TypeKind.LONG == idType.getKind()) {
            return "Long.parseLong(" + param + ")";
        } else if (TypeKind.SHORT == idType.getKind()) {
            return "Short.parseShort(" + param + ")";
        } else if (TypeKind.DECLARED == idType.getKind()) {
            if ("Boolean".equals(idType.toString()) || "java.lang.Boolean".equals(idType.toString())) {
                return "Boolean.valueOf(" + param + ")";
            } else if ("Byte".equals(idType.toString()) || "java.lang.Byte".equals(idType.toString())) {
                return "Byte.valueOf(" + param + ")";
            } else if ("Character".equals(idType.toString()) || "java.lang.Character".equals(idType.toString())) {
                return "new Character(" + param + ".charAt(0))";
            } else if ("Double".equals(idType.toString()) || "java.lang.Double".equals(idType.toString())) {
                return "Double.valueOf(" + param + ")";
            } else if ("Float".equals(idType.toString()) || "java.lang.Float".equals(idType.toString())) {
                return "Float.valueOf(" + param + ")";
            } else if ("Integer".equals(idType.toString()) || "java.lang.Integer".equals(idType.toString())) {
                return "Integer.valueOf(" + param + ")";
            } else if ("Long".equals(idType.toString()) || "java.lang.Long".equals(idType.toString())) {
                return "Long.valueOf(" + param + ")";
            } else if ("Short".equals(idType.toString()) || "java.lang.Short".equals(idType.toString())) {
                return "Short.valueOf(" + param + ")";
            } else if ("BigDecimal".equals(idType.toString()) || "java.math.BigDecimal".equals(idType.toString())) {
                return "new java.math.BigDecimal(" + param + ")";
            } else if ("Date".equals(idType.toString()) || "java.util.Date".equals(idType.toString())) {
                return "java.sql.Date.valueOf(" + param + ")";
            }
        }
        return param;
    }

//    private static String getCorrespondingType(TypeMirror idType) {
//        if (TypeKind.BOOLEAN == idType.getKind()) {
//            return "boolean";
//        } else if (TypeKind.BYTE == idType.getKind()) {
//            return "byte";
//        } else if (TypeKind.CHAR == idType.getKind()) {
//            return "char";
//        } else if (TypeKind.DOUBLE == idType.getKind()) {
//            return "double";
//        } else if (TypeKind.FLOAT == idType.getKind()) {
//            return "float";
//        } else if (TypeKind.INT == idType.getKind()) {
//            return "int";
//        } else if (TypeKind.LONG == idType.getKind()) {
//            return "long";
//        } else if (TypeKind.SHORT == idType.getKind()) {
//            return "short";
//        } else {
//            return "UnknownType";
//        }
//    }
    public final static class FieldDesc {

        private ExecutableElement method;
        private Element fieldElement;
        private String methodName;
        private String propertyName;
        private String label;
        private Boolean fieldAccess = null;
        private Integer relationship = null;
        private TypeElement bean;
        private CompilationController controller;
        private String dateTimeFormat = null;
        private String valuesGetter = "fixme";
        private String valuesListGetter;
        private boolean primaryKey;
        private boolean generatedValue;
        private boolean embeddedKey;
        private boolean keyPartOfRelationshipFied;
        private boolean readOnly;
        private boolean versionField;

        public boolean isGeneratedValue() {
            return generatedValue;
        }

        public void setGeneratedValue(boolean generatedValue) {
            this.generatedValue = generatedValue;
        }

        private FieldDesc(CompilationController controller, ExecutableElement method, TypeElement bean, boolean enableValueGetters) {
            this(controller, method, bean);
            if (enableValueGetters) {
                valuesGetter = null;
            }
        }

        private FieldDesc(CompilationController controller, ExecutableElement method, TypeElement bean) {
            this.controller = controller;
            this.method = method;
            this.bean = bean;
            this.methodName = method.getSimpleName().toString();
            this.propertyName = CustomJpaControllerUtil.getPropNameFromMethod(getMethodName());
            this.label = StringHelper.firstUpper(this.propertyName);
            this.fieldElement = this.isFieldAccess() ? CustomJpaControllerUtil.guessField(method) : method;
            if (this.fieldElement == null) {
                this.fieldElement = method;
            }

        }

        public boolean isPrimaryKey() {
            return primaryKey;
        }

        public void setPrimaryKey() {
            this.primaryKey = true;
        }

        public boolean isEmbeddedKey() {
            return embeddedKey;
        }

        public void setEmbeddedKey() {
            this.embeddedKey = true;
        }

        public boolean isKeyPartOfRelationshipFied() {
            return keyPartOfRelationshipFied;
        }

        public void setKeyPartOfRelationshipFied() {
            this.keyPartOfRelationshipFied = true;
        }

        public String getMethodName() {
            return methodName;
        }

        public Integer getMaxSize() {
            if (getFieldElement() != null) {
                AnnotationMirror sizeAnnotation = CustomJpaControllerUtil.findAnnotation(getFieldElement(), "jakarta.validation.constraints.Size");
                if (sizeAnnotation != null) {
                    String stringMemberValue = CustomJpaControllerUtil.findAnnotationValueAsString(sizeAnnotation, "max");
                    if (stringMemberValue != null) {
                        int parseInt;
                        try {
                            parseInt = Integer.parseInt(stringMemberValue);
                            return parseInt;
                        } catch (NumberFormatException ex) {
                            return null;
                        }
                    }
                }
            }
            return null;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public void setPropertyName(String propertyName) {
            this.propertyName = propertyName;
        }

        public String getLabel() {
            return StringHelper.toNatural(label);
        }

        public void setLabel(String label) {
            this.label = label;
        }

        private boolean isFieldAccess() {
            if (fieldAccess == null) {
                fieldAccess = CustomJpaControllerUtil.isFieldAccess(bean);
            }
            return fieldAccess;
        }

        public boolean isReadOnly() {
            try {
                readOnly = CustomJpaControllerUtil.isReadOnly(method);
            } catch (NotGetterMethodException ex) {
                readOnly = false;
            }
            return readOnly;
        }

        public boolean isValid() {
            // Also take into account methods that start with "is" for boolean getters.
            return (getMethodName().startsWith("get") || getMethodName().startsWith("is")); // NOI18N
        }

        public boolean isVersionField() {
            versionField = false;
            if (getFieldElement() != null) {
                versionField = CustomJpaControllerUtil.isAnnotatedWith(getFieldElement(), "jakarta.persistence.Version");
            }
            return versionField;
        }

        public int getRelationship() {
            if (relationship == null) {
                relationship = CustomJpaControllerUtil.isRelationship(method, isFieldAccess());
            }
            return relationship;
        }

        public String getDateTimeFormat() {
            if (dateTimeFormat == null) {
                dateTimeFormat = "";
                TypeMirror dateTypeMirror = controller.getElements().getTypeElement("java.util.Date").asType(); // NOI18N
                if (controller.getTypes().isSameType(dateTypeMirror, method.getReturnType())) {
                    String temporal = EntityClass.getTemporal(method, isFieldAccess());
                    if (temporal != null) {
                        dateTimeFormat = EntityClass.getDateTimeFormat(temporal);
                    }
                }
            }
            return dateTimeFormat;
        }

        private boolean isBlob() {
            Element fieldElement = isFieldAccess() ? CustomJpaControllerUtil.guessField(method) : method;
            if (fieldElement == null) {
                fieldElement = method;
            }
            return CustomJpaControllerUtil.isAnnotatedWith(fieldElement, "jakarta.persistence.Lob"); // NOI18N
        }

        @Override
        public String toString() {
            return "Field[" + // NOI18N
                    "methodName=" + getMethodName() + // NOI18N
                    ",propertyName=" + getPropertyName() + // NOI18N
                    ",label=" + label + // NOI18N
                    ",valid=" + isValid() + // NOI18N
                    ",field=" + isFieldAccess() + // NOI18N
                    ",relationship=" + getRelationship() + // NOI18N
                    ",datetime=" + getDateTimeFormat() + // NOI18N
                    ",valuesListGetter=" + getValuesListGetter() + // NOI18N
                    "]"; // NOI18N
        }

        public String getRelationClassName() {
            TypeMirror passedReturnType = method.getReturnType();
            if (TypeKind.DECLARED != passedReturnType.getKind() || !(passedReturnType instanceof DeclaredType)) {
                return null;
            }
            Types types = controller.getTypes();
            TypeMirror passedReturnTypeStripped = CustomJpaControllerUtil.stripCollection((DeclaredType) passedReturnType, types);
            if (passedReturnTypeStripped == null) {
                return null;
            }
            TypeElement passedReturnTypeStrippedElement = (TypeElement) types.asElement(passedReturnTypeStripped);
            return passedReturnTypeStrippedElement.getSimpleName().toString();
        }

        public String getRelationQualifiedClassName() {
            TypeMirror passedReturnType = method.getReturnType();
            if (TypeKind.DECLARED != passedReturnType.getKind() || !(passedReturnType instanceof DeclaredType)) {
                return null;
            }
            Types types = controller.getTypes();
            TypeMirror passedReturnTypeStripped = CustomJpaControllerUtil.stripCollection((DeclaredType) passedReturnType, types);
            if (passedReturnTypeStripped == null) {
                return null;
            }
            TypeElement passedReturnTypeStrippedElement = (TypeElement) types.asElement(passedReturnTypeStripped);
            return passedReturnTypeStrippedElement.getQualifiedName().toString();
        }

        public String getValuesListGetter() {
            if (getRelationship() == CustomJpaControllerUtil.REL_NONE) {
                return null;
            }
            String name = getRelationClassName();
            if (name == null) {
                valuesListGetter = "";
            } else {
                name = name.substring(0, 1).toLowerCase() + name.substring(1);
                valuesListGetter = name + "Controller.items";
            }
            return valuesListGetter;
        }

        public String getValuesConverter() {
            if (getRelationship() == CustomJpaControllerUtil.REL_NONE) {
                return null;
            }
            String name = getRelationClassName();
            if (name == null) {
                valuesListGetter = "";
            } else {
                name = name.substring(0, 1).toLowerCase() + name.substring(1);
                valuesListGetter = name + "Converter";
            }
            return valuesListGetter;
        }

        public String getRelationsLabelName(String searchLabels) {
            if (getRelationship() == CustomJpaControllerUtil.REL_NONE) {
                return "";
            }
            /*Locate custom RelationLabel annotation class */

            String name = getRelationQualifiedClassName();
            if (name != null) {
                TypeElement relationEntity = controller.getElements().getTypeElement(name);
                String entityPackage = getPackageName(relationEntity.getQualifiedName().toString());
                ExecutableElement[] relationMethods = CustomJpaControllerUtil.getEntityMethods(relationEntity);
                String idField = "";
                for (ExecutableElement relationMethod : relationMethods) {
                    if (isGetterMethod(relationMethod.getSimpleName().toString())) {
                        FieldDesc relfd = new FieldDesc(controller, relationMethod, relationEntity);
                        /* check whether an entity is annotated with a custom @RelationLabel. This should
                         * be a user-created annotation-type to be placed inside the entity's
                         * package. This is currently the ONLY way to ensure a perfect hit
                         * for a relationships "Label". Unfortunately, the developer must
                         * annotate each entity by hand.
                         */
                        boolean isRelationLabel = false;
                        Element relationFieldElement = isFieldAccess() ? CustomJpaControllerUtil.guessField(relationMethod) : relationMethod;
                        if (relationFieldElement == null) {
                            relationFieldElement = relationMethod;
                        }
                        try {
                            isRelationLabel = CustomJpaControllerUtil.isAnnotatedWith(relationFieldElement, entityPackage + "." + RELATION_LABEL_ANNOTATION);
                        } catch (Exception ex) {
                            // Do nothing
                        }
                        boolean isPrimaryKey = EntityClass.isId(relationMethod, relfd.isFieldAccess());
                        String relPropertyName = relfd.getPropertyName();
                        if (isRelationLabel) {
                            return relPropertyName;
                        }
                        if (isPrimaryKey) {
                            idField = relPropertyName;
                        }
                        for (String searchLabel : searchLabels.split(",")) {
                            if (relPropertyName.toLowerCase().contains(searchLabel.toLowerCase())) {
                                return relPropertyName;
                            }
                        }
                    }
                }
                return idField; // Return primary key field name if no match was found
            }
            return "";
        }

        private boolean isRequired() {
            return !CustomJpaControllerUtil.isFieldOptionalAndNullable(method, isFieldAccess());
        }

        public String getReturnType() {
            if (method.getReturnType().getKind().isPrimitive()) {
                if (method.getReturnType().getKind() == TypeKind.BOOLEAN) {
                    return CHECKBOX.type();
                } else {
                    return NUMBER.type();
                }
            } else if (method.getReturnType().getKind() == TypeKind.DECLARED) {
                Class _class = null;
                try {
                    _class = Class.forName(method.getReturnType().toString());
                } catch (ClassNotFoundException ex) {
                    return INVALID.type();
                }
                if (Number.class.isAssignableFrom(_class)) {
                    return NUMBER.type();
                } else if (Boolean.class == _class) {
                    return CHECKBOX.type();
                } else if (String.class == _class) {
                    return TEXT.type();
                } else if (Date.class.isAssignableFrom(_class)) {
                    return DATE.type();
                } else if (Calendar.class == _class) {
                    return DATE.type();
                }
            }

            return INVALID.type();
        }
  
        public String getHtmlType() {
            return method.getReturnType().toString();
        }

        public boolean isRelationshipOwner() {
            Element fieldElement = isFieldAccess() ? CustomJpaControllerUtil.guessField(method) : method;
            if (fieldElement == null) {
                fieldElement = method;
            }
            return CustomJpaControllerUtil.isAnnotatedWith(fieldElement, "jakarta.persistence.JoinTable"); // NOI18N
        }

        /**
         * @return the fieldElement
         */
        public Element getFieldElement() {
            return fieldElement;
        }
    }

    public static final class TemplateData {

        private FieldDesc fd;
        private String prefix;

        private TemplateData(FieldDesc fd, String prefix) {
            this.fd = fd;
            this.prefix = prefix;
        }

        public String getLabel() {
            return fd.getLabel();
        }

        public String getName() {
            return prefix + fd.getPropertyName();
        }

        public String getPropertyName() {
            return fd.getPropertyName();
        }

        public String getDateTimeFormat() {
            return fd.getDateTimeFormat();
        }

        public String getReturnType() {
            return fd.getReturnType();
        }

        public boolean isBlob() {
            return fd.isBlob();
        }

        // @Id fields in template (ftl) files.
        public boolean isPrimaryKey() {
            return fd.isPrimaryKey();
        }

        public boolean isRelationshipOne() {
            return fd.getRelationship() == CustomJpaControllerUtil.REL_TO_ONE;
        }

        public boolean isRelationshipMany() {
            return fd.getRelationship() == CustomJpaControllerUtil.REL_TO_MANY;
        }

        public String getId() {
            return fd.getPropertyName();
        }

        public boolean isRequired() {
            return fd.isRequired();
        }

        public String getValuesListGetter() {
            return fd.getValuesListGetter();
        }

        public String getValuesConverter() {
            return fd.getValuesConverter();
        }

        public boolean isGeneratedValue() {
            return fd.isGeneratedValue();
        }

        public boolean isRelationshipOwner() {
            return fd.isRelationshipOwner();
        }

        public String getRelationClassName() {
            return fd.getRelationClassName();
        }

        public String getRelationQualifiedClassName() {
            return fd.getRelationQualifiedClassName();
        }

        public String getRelationsLabelName(String labelArtifacts) {
            return fd.getRelationsLabelName(labelArtifacts);
        }

        public boolean isEmbeddedKey() {
            return fd.embeddedKey;
        }

        public Integer getMaxSize() {
            return fd.getMaxSize();
        }

        public boolean isReadOnly() {
            return fd.isReadOnly();
        }

        public boolean isVersionField() {
            return fd.isVersionField();
        }

        @Override
        public String toString() {
            return "TemplateData[fd=" + fd + ",prefix=" + prefix + "]"; // NOI18N
        }
    }

    public static final class EmbeddedDesc {

        private final String embeddedSetter;
        private final String codeToPopulate;

        public EmbeddedDesc(String embeddedSetter, String codeToPopulate) {
            this.embeddedSetter = embeddedSetter;
            this.codeToPopulate = codeToPopulate;
        }

        public String getEmbeddedSetter() {
            return embeddedSetter;
        }

        public String getCodeToPopulate() {
            return codeToPopulate;
        }
    }
}
