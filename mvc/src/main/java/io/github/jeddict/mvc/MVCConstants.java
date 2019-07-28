/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a
 * workingCopy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.jeddict.mvc;

import io.github.jeddict.jcode.JAXRSConstants;

/**
 *
 * @author Gaurav Gupta
 */
public class MVCConstants {

    public static final String CONTROLLER = "javax.mvc.annotation.Controller";
    public static final String CSRF = "javax.mvc.security.Csrf";
    public static final String VIEWABLE_UNQF = "Viewable";
    public static final String VIEWABLE = "javax.mvc.Viewable";
    public static final String VIEW_ENGINE = "javax.mvc.engine.ViewEngine";
    public static final String VIEW = "javax.mvc.annotation.View";
    public static final String MODELS = "javax.mvc.Models"; 
    public static final String REDIRECT  = "redirect:";
    public static final String BINDING_RESULT  = "javax.mvc.binding.BindingResult";
    public static final String CSRF_VALID  = "javax.mvc.annotation.CsrfValid";
    
    
    public enum MimeType {

        XML("application/xml", "Xml", "APPLICATION_XML"),
        JSON("application/json", "Json", "APPLICATION_JSON"),
        TEXT("text/plain", "Text", "TEXT_PLAIN"),
        HTML("text/html", "Html", "TEXT_HTML"),
        IMAGE("image/png", "Image", null);

        private final String value;
        private final String suffix;
        private final String mediaTypeField;

        MimeType(String value, String suffix, String mediaTypeField) {
            this.value = value;
            this.suffix = suffix;
            this.mediaTypeField = mediaTypeField;
        }

        public String value() {
            return value;
        }

        public String suffix() {
            return suffix;
        }

        public static MimeType find(String value) {
            for (MimeType m : values()) {
                if (m.value().equals(value)) {
                    return m;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public enum HttpMethodType {

        GET("get", JAXRSConstants.GET),
        PUT("put", JAXRSConstants.PUT),
        POST("post", JAXRSConstants.POST),
        DELETE("delete", JAXRSConstants.DELETE);

        private final String prefix;
        private final String annotationType;

        HttpMethodType(String prefix, String annotationType) {
            this.prefix = prefix;
            this.annotationType = annotationType;
        }

        public String value() {
            return name();
        }

        public String prefix() {
            return prefix;
        }

        public String getAnnotationType() {
            return annotationType;
        }
    }

    public static final String REST_STUBS_DIR = "rest";

    public static final String PASSWORD = "password";
}
