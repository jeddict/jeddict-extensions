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
package org.netbeans.jcode.angular1;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.netbeans.jcode.ng.main.domain.ApplicationSourceFilter;
import org.netbeans.jcode.ng.main.domain.NGApplicationConfig;
import static org.netbeans.jcode.ng.main.domain.NGApplicationConfig.ELASTIC_SEARCH_ENGINE;
import static org.netbeans.jcode.ng.main.domain.NGApplicationConfig.GATEWAY_APPLICATION_TYPE;
import static org.netbeans.jcode.ng.main.domain.NGApplicationConfig.JWT_AUTHENTICATION_TYPE;
import static org.netbeans.jcode.ng.main.domain.NGApplicationConfig.OAUTH2_AUTHENTICATION_TYPE;
import static org.netbeans.jcode.ng.main.domain.NGApplicationConfig.SESSION_AUTHENTICATION_TYPE;
import static org.netbeans.jcode.ng.main.domain.NGApplicationConfig.SPRING_WEBSOCKET;
import static org.netbeans.jcode.ng.main.domain.NGApplicationConfig.UAA_AUTHENTICATION_TYPE;

public class NG1SourceFilter extends ApplicationSourceFilter {

    private Map<String, Supplier<Boolean>> dataFilter;

  
    public NG1SourceFilter(NGApplicationConfig config) {
        super(config);
    }
  

    protected Map<String, Supplier<Boolean>> getGeneratorFilter() {
        if (dataFilter == null) {
            dataFilter = new HashMap<>();

            //AuthenticationType
            dataFilter.put("_auth.oauth2.service.js", () -> OAUTH2_AUTHENTICATION_TYPE.equals(config.getAuthenticationType()));
            dataFilter.put("_auth.jwt.service.js", () -> JWT_AUTHENTICATION_TYPE.equals(config.getAuthenticationType()) || UAA_AUTHENTICATION_TYPE.equals(config.getAuthenticationType()));
            dataFilter.put("_auth.interceptor.js", () -> OAUTH2_AUTHENTICATION_TYPE.equals(config.getAuthenticationType()) || JWT_AUTHENTICATION_TYPE.equals(config.getAuthenticationType()) || UAA_AUTHENTICATION_TYPE.equals(config.getAuthenticationType()));
            dataFilter.put("_auth.session.service.js", () -> SESSION_AUTHENTICATION_TYPE.equals(config.getAuthenticationType()));
            dataFilter.put("_sessions.service.js", () -> SESSION_AUTHENTICATION_TYPE.equals(config.getAuthenticationType()));
            dataFilter.put("sessions.html", () -> SESSION_AUTHENTICATION_TYPE.equals(config.getAuthenticationType()));
            dataFilter.put("_sessions.state.js", () -> SESSION_AUTHENTICATION_TYPE.equals(config.getAuthenticationType()));
            dataFilter.put("_sessions.controller.js", () -> SESSION_AUTHENTICATION_TYPE.equals(config.getAuthenticationType()));

            //Language
            dataFilter.put("_language.filter.js", () -> config.isEnableTranslation());
            dataFilter.put("_language.constants.js", () -> config.isEnableTranslation());
            dataFilter.put("_language.controller.js", () -> config.isEnableTranslation());
            dataFilter.put("_language.service.js", () -> config.isEnableTranslation());

            //ApplicationType
            dataFilter.put("gateway.html", () -> GATEWAY_APPLICATION_TYPE.equals(config.getAuthenticationType()));
            dataFilter.put("_gateway.state.js", () -> GATEWAY_APPLICATION_TYPE.equals(config.getAuthenticationType()));
            dataFilter.put("_gateway.controller.js", () -> GATEWAY_APPLICATION_TYPE.equals(config.getAuthenticationType()));
            dataFilter.put("_gateway.routes.service.js", () -> GATEWAY_APPLICATION_TYPE.equals(config.getAuthenticationType()));

            //Social Login
            dataFilter.put("_social.html", () -> config.isEnableSocialSignIn());
            dataFilter.put("_social.directive.js", () -> config.isEnableSocialSignIn());
            dataFilter.put("_social-register.html", () -> config.isEnableSocialSignIn());
            dataFilter.put("_social-register.controller.js", () -> config.isEnableSocialSignIn());
            dataFilter.put("_social.service.js", () -> config.isEnableSocialSignIn());
            dataFilter.put("_social.state.js", () -> config.isEnableSocialSignIn());
            dataFilter.put("_social-auth.controller.js", () -> config.isEnableSocialSignIn() && JWT_AUTHENTICATION_TYPE.equals(config.getAuthenticationType()));

            //WebSocket
            dataFilter.put("tracker.html", () -> SPRING_WEBSOCKET.equals(config.getWebsocket()));
            dataFilter.put("_tracker.state.js", () -> SPRING_WEBSOCKET.equals(config.getWebsocket()));
            dataFilter.put("_tracker.controller.js", () -> SPRING_WEBSOCKET.equals(config.getWebsocket()));
            dataFilter.put("_tracker.service.js", () -> SPRING_WEBSOCKET.equals(config.getWebsocket()));

            //Metrics
            dataFilter.put("_metrics.controller.js", () -> config.isEnableMetrics());
            dataFilter.put("_metrics.modal.controller.js", () -> config.isEnableMetrics());
            dataFilter.put("_metrics.html", () -> config.isEnableMetrics());
            dataFilter.put("_metrics.modal.html", () -> config.isEnableMetrics());
            dataFilter.put("_metrics.service.js", () -> config.isEnableMetrics());
            dataFilter.put("_metrics.state.js", () -> config.isEnableMetrics());

            //Logs
            dataFilter.put("_logs.controller.js", () -> config.isEnableLogs());
            dataFilter.put("logs.html", () -> config.isEnableLogs());
            dataFilter.put("_logs.service.js", () -> config.isEnableLogs());
            dataFilter.put("_logs.state.js", () -> config.isEnableLogs());

                        
            //Health
            dataFilter.put("_health.controller.js", () -> config.isEnableHealth());
            dataFilter.put("_health.modal.controller.js", () -> config.isEnableHealth());
            dataFilter.put("health.html", () -> config.isEnableHealth());
            dataFilter.put("_health.modal.html", () -> config.isEnableHealth());
            dataFilter.put("_health.service.js", () -> config.isEnableHealth());
            dataFilter.put("_health.state.js", () -> config.isEnableHealth());

            //Configuration
            dataFilter.put("_configuration.controller.js", () -> config.isEnableConfiguration());
            dataFilter.put("configuration.html", () -> config.isEnableConfiguration());
            dataFilter.put("_configuration.service.js", () -> config.isEnableConfiguration());
            dataFilter.put("_configuration.state.js", () -> config.isEnableConfiguration());

            //Audit
            dataFilter.put("_audits.controller.js", () -> config.isEnableAudits());
            dataFilter.put("audits.html", () -> config.isEnableAudits());
            dataFilter.put("_audits.service.js", () -> config.isEnableAudits());
            dataFilter.put("_audits.state.js", () -> config.isEnableAudits());

            //Docs
            dataFilter.put("docs.html", () -> config.isEnableDocs());
            dataFilter.put("_docs.state.js", () -> config.isEnableDocs());
            dataFilter.put("swagger-ui/_index.html", () -> config.isEnableDocs());
            dataFilter.put("swagger-ui/images/throbber.gif", () -> config.isEnableDocs());
            dataFilter.put("swagger-ui/config/resource.json", () -> config.isEnableDocs());
            dataFilter.put("swagger-ui/config/ui.json", () -> config.isEnableDocs());

            //SCSS
            dataFilter.put("scss/main.scss", () -> config.isUseSass());
            dataFilter.put("scss/vendor.scss", () -> config.isUseSass());
            
            //Profile
            dataFilter.put("_page-ribbon.directive.js", () -> config.isEnableProfile());
            dataFilter.put("_profile.service.js", () -> config.isEnableProfile());
        
            //Profile
            dataFilter.put("_entity-search.service.js", () -> ELASTIC_SEARCH_ENGINE.equals(config.getSearchEngine()));
        
        }
        return dataFilter;
    }

    
}
