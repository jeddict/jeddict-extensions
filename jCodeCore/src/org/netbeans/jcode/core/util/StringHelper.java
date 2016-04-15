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

public final class StringHelper {

    public static String firstLower(String string) {
        boolean makeFirstLower = string.length() < 2 || (!Character.isUpperCase(string.charAt(1)));
        return makeFirstLower ? string.substring(0, 1).toLowerCase() + string.substring(1) : string;
    }

    public static String firstUpper(String string) {
        return string.length() > 1 ? string.substring(0, 1).toUpperCase() + string.substring(1) : string.toUpperCase();
    }

    public static String removeBeanMethodPrefix(String methodName) {
        if (methodName.startsWith("get")) {  //NOI18N
            methodName = methodName.replaceFirst("get", "");
        }
        if (methodName.startsWith("set")) {  //NOI18N
            methodName = methodName.replaceFirst("set", "");
        }
        if (methodName.startsWith("is")) {  //NOI18N
            methodName = methodName.replaceFirst("is", "");
        }
        return methodName;
    }

    public static boolean isGetterMethod(String methodName) {
        return methodName.startsWith("get") || methodName.startsWith("is");
    }

    public static String toNatural(String input) {
        String natural = "";
        Character lastChar = null;
        for (Character curChar : input.toCharArray()) {
            if (lastChar == null) {
                // First character
                lastChar = Character.toUpperCase(curChar);
                natural = natural + lastChar;

            } else {
                if (Character.isLowerCase(lastChar)
                        && (Character.isUpperCase(curChar)) || Character.isDigit(curChar)) {
                    natural = natural + " " + curChar;
                } else {
                    natural = natural + curChar;
                }
                lastChar = curChar;
            }

        }
        return natural;
    }
    
   public static String toConstant(String input) {
        String constant = "";
        Character lastChar = null;
        for (Character curChar : input.toCharArray()) {
            if (lastChar == null) {
                // First character
                lastChar = Character.toUpperCase(curChar);
                constant = constant + lastChar;

            } else {
                if (Character.isLowerCase(lastChar)
                        && (Character.isUpperCase(curChar) || Character.isDigit(curChar))) {
                    constant = constant + '_' + curChar;
                } else {
                    constant = constant + Character.toUpperCase(curChar);
                }
                lastChar = curChar;
            }

        }
        return constant;
    }

}
