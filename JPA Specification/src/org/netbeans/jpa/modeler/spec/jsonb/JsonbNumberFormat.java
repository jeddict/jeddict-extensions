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
package org.netbeans.jpa.modeler.spec.jsonb;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import static org.netbeans.jcode.core.util.AttributeType.BIGDECIMAL;
import static org.netbeans.jcode.core.util.AttributeType.BIGINTEGER;
import static org.netbeans.jcode.core.util.AttributeType.DOUBLE;
import static org.netbeans.jcode.core.util.AttributeType.DOUBLE_WRAPPER;
import static org.netbeans.jcode.core.util.AttributeType.FLOAT;
import static org.netbeans.jcode.core.util.AttributeType.FLOAT_WRAPPER;
import static org.netbeans.jcode.core.util.AttributeType.INT;
import static org.netbeans.jcode.core.util.AttributeType.INT_WRAPPER;
import static org.netbeans.jcode.core.util.AttributeType.LONG;
import static org.netbeans.jcode.core.util.AttributeType.LONG_WRAPPER;
import static org.netbeans.jcode.core.util.AttributeType.SHORT;
import static org.netbeans.jcode.core.util.AttributeType.SHORT_WRAPPER;
import static org.netbeans.jcode.jsonb.JSONBConstants.JSONB_NUMBER_FORMAT_FQN;
import org.netbeans.jpa.source.JavaSourceParserUtil;

/**
 *
 * @author jGauravGupta
 */
public class JsonbNumberFormat extends JsonbFormat {

    private static final Set<String> SUPPORTED_TYPE = new HashSet<>(Arrays.asList(SHORT, INT, LONG, FLOAT, DOUBLE,
            SHORT_WRAPPER, INT_WRAPPER, LONG_WRAPPER, FLOAT_WRAPPER, DOUBLE_WRAPPER, BIGINTEGER, BIGDECIMAL));

    public boolean isSupportedFormat(String type) {
        return SUPPORTED_TYPE.contains(type);
    }

    public static JsonbNumberFormat load(Element element) {
        AnnotationMirror annotationMirror = JavaSourceParserUtil.findAnnotation(element, JSONB_NUMBER_FORMAT_FQN);
        JsonbNumberFormat jsonbDateFormat = null;
        if (annotationMirror != null) {
            jsonbDateFormat = new JsonbNumberFormat();
            String value = (String) JavaSourceParserUtil.findAnnotationValue(annotationMirror, "value");
            if (value != null) {
                jsonbDateFormat.setValue(value);
            }
            String locale = (String) JavaSourceParserUtil.findAnnotationValue(annotationMirror, "locale");
            if (locale != null) {
                jsonbDateFormat.setLocale(locale);
            }
        }
        return jsonbDateFormat;
    }

}
