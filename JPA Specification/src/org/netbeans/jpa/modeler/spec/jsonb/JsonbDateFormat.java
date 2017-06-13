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
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import static org.netbeans.jcode.core.util.AttributeType.CALENDAR;
import static org.netbeans.jcode.core.util.AttributeType.DATE;
import static org.netbeans.jcode.core.util.AttributeType.DURATION;
import static org.netbeans.jcode.core.util.AttributeType.GREGORIAN_CALENDAR;
import static org.netbeans.jcode.core.util.AttributeType.INSTANT;
import static org.netbeans.jcode.core.util.AttributeType.LOCAL_DATE;
import static org.netbeans.jcode.core.util.AttributeType.LOCAL_DATE_TIME;
import static org.netbeans.jcode.core.util.AttributeType.LOCAL_TIME;
import static org.netbeans.jcode.core.util.AttributeType.OFFSET_DATE_TIME;
import static org.netbeans.jcode.core.util.AttributeType.OFFSET_TIME;
import static org.netbeans.jcode.core.util.AttributeType.PERIOD;
import static org.netbeans.jcode.core.util.AttributeType.SIMPLE_TIME_ZONE;
import static org.netbeans.jcode.core.util.AttributeType.TIME_ZONE;
import static org.netbeans.jcode.core.util.AttributeType.ZONED_DATE_TIME;
import static org.netbeans.jcode.core.util.AttributeType.ZONE_ID;
import static org.netbeans.jcode.core.util.AttributeType.ZONE_OFFSET;
import static org.netbeans.jcode.jsonb.JSONBConstants.JSONB_DATE_FORMAT_FQN;
import org.netbeans.jpa.modeler.spec.validator.JsonbDateFormatValidator;
import org.netbeans.jpa.source.JavaSourceParserUtil;

/**
 *
 * @author jGauravGupta
 */
@XmlJavaTypeAdapter(value = JsonbDateFormatValidator.class)
public class JsonbDateFormat extends JsonbFormat {
    
    private static final Set<String> SUPPORTED_TYPE = new HashSet<>(Arrays.asList(DATE,CALENDAR,GREGORIAN_CALENDAR,TIME_ZONE ,SIMPLE_TIME_ZONE,
            INSTANT,DURATION,PERIOD,LOCAL_DATE,LOCAL_TIME,LOCAL_DATE_TIME,
            ZONED_DATE_TIME,ZONE_ID,ZONE_OFFSET,OFFSET_DATE_TIME,OFFSET_TIME));
    
    public boolean isSupportedFormat(String type) {
        return SUPPORTED_TYPE.contains(type);
    }
    
    public static JsonbDateFormat load(Element element) {
        AnnotationMirror annotationMirror = JavaSourceParserUtil.findAnnotation(element, JSONB_DATE_FORMAT_FQN);
        JsonbDateFormat jsonbDateFormat = null;
        if (annotationMirror != null) {
            jsonbDateFormat = new JsonbDateFormat();
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
