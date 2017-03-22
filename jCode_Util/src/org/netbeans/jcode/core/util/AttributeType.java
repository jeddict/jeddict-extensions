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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import static org.netbeans.jcode.core.util.AttributeType.Type.ARRAY;
import static org.netbeans.jcode.core.util.AttributeType.Type.OTHER;
import static org.netbeans.jcode.core.util.AttributeType.Type.PRIMITIVE;
import static org.netbeans.jcode.core.util.AttributeType.Type.PRIMITIVE_ARRAY;
import static org.netbeans.jcode.core.util.AttributeType.Type.WRAPPER;

/**
 *
 * @author Gaurav Gupta
 */
public class AttributeType {

    public static final String BYTE = "byte";
    public static final String SHORT = "short";
    public static final String INT = "int";
    public static final String LONG = "long";
    public static final String FLOAT = "float";
    public static final String DOUBLE = "double";
    public static final String BOOLEAN = "boolean";
    public static final String CHAR = "char";

    public static final String BYTE_WRAPPER = "Byte";
    public static final String SHORT_WRAPPER = "Short";
    public static final String INT_WRAPPER = "Integer";
    public static final String LONG_WRAPPER = "Long";
    public static final String FLOAT_WRAPPER = "Float";
    public static final String DOUBLE_WRAPPER = "Double";
    public static final String BOOLEAN_WRAPPER = "Boolean";
    public static final String CHAR_WRAPPER = "Character";

    public static final String BIGDECIMAL = "java.math.BigDecimal";
    public static final String BIGINTEGER = "java.math.BigInteger";
    public static final String STRING = "String";
    public static final String STRING_FQN = "java.lang.String";
    public static final String CALENDAR = "java.util.Calendar";
    public static final String DATE = "java.util.Date";
    public static final String SQL_DATE = "java.sql.Date";
    public static final String SQL_TIME = "java.sql.Time";
    public static final String SQL_TIMESTAMP = "java.sql.Timestamp";
    public static final String BYTE_ARRAY = "byte[]";
    public static final String BYTE_WRAPPER_ARRAY = "Byte[]";
    public static final String CHAR_ARRAY = "char[]";
    public static final String CHAR_WRAPPER_ARRAY = "Character[]";
    public static final String UUID = "java.util.UUID";
    
    public static final String INSTANT = "java.time.Instant";
    public static final String LOCAL_DATE = "java.time.LocalDate";
    public static final String LOCAL_DATE_TIME = "java.time.LocalDateTime";
    public static final String LOCAL_TIME = "java.time.LocalTime";
    public static final String MONTH_DAY = "java.time.MonthDay";
    public static final String OFFSET_DATE_TIME = "java.time.OffsetDateTime";
    public static final String OFFSET_TIME = "java.time.OffsetTime";
    public static final String YEAR = "java.time.Year";
    public static final String YEAR_MONTH = "java.time.YearMonth";
    public static final String ZONED_DATE_TIME = "java.time.ZonedDateTime";
    public static final String HIJRAH_DATE = "java.time.chrono.HijrahDate";
    public static final String JAPANESE_DATE = "java.time.chrono.JapaneseDate";
    public static final String MINGUO_DATE = "java.time.chrono.MinguoDate";
    public static final String THAI_BUDDHIST_DATE = "java.time.chrono.ThaiBuddhistDate";

    private static final Map<String, String> WRAPPER_DATA_TYPES = new HashMap<>();
        
    static {
        WRAPPER_DATA_TYPES.put(BYTE_WRAPPER, BYTE);
        WRAPPER_DATA_TYPES.put(SHORT_WRAPPER, SHORT);
        WRAPPER_DATA_TYPES.put(INT_WRAPPER, INT);
        WRAPPER_DATA_TYPES.put(LONG_WRAPPER, LONG);
        WRAPPER_DATA_TYPES.put(FLOAT_WRAPPER, FLOAT);
        WRAPPER_DATA_TYPES.put(DOUBLE_WRAPPER, DOUBLE);
        WRAPPER_DATA_TYPES.put(BOOLEAN_WRAPPER, BOOLEAN);
        WRAPPER_DATA_TYPES.put(CHAR_WRAPPER, CHAR);
    }
    
    public static String getPrimitiveType(String wrapperType) {
        boolean array = false;
        if (isArray(wrapperType)) {
            array = true;
            wrapperType = wrapperType.substring(0, wrapperType.length() - 2);
        }
        String primitiveType = WRAPPER_DATA_TYPES.get(wrapperType);
        if (primitiveType != null) {
            return primitiveType + (array?"[]":"");
        } else {
            return wrapperType + (array?"[]":"");
        }
    }

    private static final Map<String, String> PRIMITIVE_DATA_TYPES = WRAPPER_DATA_TYPES.entrySet().stream().collect(Collectors.toMap(Entry::getValue, Entry::getKey));

    public static String getWrapperType(String primitiveType) {
        boolean array = false;
        if (isArray(primitiveType)) {
            array = true;
            primitiveType = primitiveType.substring(0, primitiveType.length() - 2);
        }
        String wrapperType = PRIMITIVE_DATA_TYPES.get(primitiveType);
        if (wrapperType != null) {
            return wrapperType + (array?"[]":"");
        } else {
            return primitiveType + (array?"[]":"");
        }
    }

    public static enum Type {
        PRIMITIVE, WRAPPER, ARRAY, PRIMITIVE_ARRAY, STRING, OTHER;
    }

    public static Type getType(String type) {
        if (isWrapper(type)) {
            return WRAPPER;
        } else if (isPrimitive(type)) {
            return PRIMITIVE;
        } else if (isArray(type)) {
            if (isPrimitiveArray(type)) {
                return PRIMITIVE_ARRAY;
            } else {
                return ARRAY;
            }
        } else if (String.class.getSimpleName().equals(type) || String.class.getCanonicalName().equals(type)) {
            return Type.STRING;
        } else {
            return OTHER;
        }

    }

    public static boolean isBoolean(String type) {
        return BOOLEAN.equals(type) || BOOLEAN_WRAPPER.equals(type);
    }

    public static boolean isPrimitive(String type) {
        return PRIMITIVE_DATA_TYPES.containsKey(type);
    }

    public static boolean isWrapper(String type) {
        return WRAPPER_DATA_TYPES.containsKey(type);
    }

    public static boolean isPrimitiveArray(String type) {
        return PRIMITIVE_DATA_TYPES.containsKey(getArrayType(type)) && isArray(type);
    }

    public static boolean isWrapperArray(String type) {
        return WRAPPER_DATA_TYPES.containsKey(getArrayType(type)) && isArray(type);
    }

    public static String getArrayType(String type) {
        return type.substring(0, type.length() - 2).trim(); 
    }

    public static boolean isArray(String type) {
        return type.charAt(type.length() - 2) == '[' && type.charAt(type.length() - 1) == ']';
    }

    public static boolean isPrecision(String type) {
        return isDouble(type) || isFloat(type);
    }

    public static boolean isDouble(String type) {
        return DOUBLE_WRAPPER.equals(type) || DOUBLE.equals(type);
    }

    public static boolean isFloat(String type) {
        return FLOAT_WRAPPER.equals(type) || FLOAT.equals(type);
    }

}
