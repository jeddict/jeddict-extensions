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
package org.netbeans.jcode.ng.main.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import static org.netbeans.jcode.ng.main.domain.NGApplicationConfig.ELASTIC_SEARCH_ENGINE;
import static org.netbeans.jcode.ng.main.domain.NGApplicationConfig.GATEWAY_APPLICATION_TYPE;
import static org.netbeans.jcode.ng.main.domain.NGApplicationConfig.JWT_AUTHENTICATION_TYPE;
import static org.netbeans.jcode.ng.main.domain.NGApplicationConfig.OAUTH2_AUTHENTICATION_TYPE;
import static org.netbeans.jcode.ng.main.domain.NGApplicationConfig.SESSION_AUTHENTICATION_TYPE;
import static org.netbeans.jcode.ng.main.domain.NGApplicationConfig.SPRING_WEBSOCKET;
import static org.netbeans.jcode.ng.main.domain.NGApplicationConfig.UAA_AUTHENTICATION_TYPE;

public class NG2SourceFilter extends ApplicationSourceFilter {

    private Map<String, Supplier<Boolean>> dataFilter;

    public NG2SourceFilter(NGApplicationConfig config) {
        super(config);
    }

    protected Map<String, Supplier<Boolean>> getGeneratorFilter() {
        if (dataFilter == null) {
            dataFilter = new HashMap<>();

            //AuthenticationType
            dataFilter.put("_auth-oauth2.service.ts", () -> OAUTH2_AUTHENTICATION_TYPE.equals(config.getAuthenticationType()));
            dataFilter.put("_base64.service.ts", () -> OAUTH2_AUTHENTICATION_TYPE.equals(config.getAuthenticationType()));
            dataFilter.put("_auth-jwt.service.ts", () -> JWT_AUTHENTICATION_TYPE.equals(config.getAuthenticationType()) || UAA_AUTHENTICATION_TYPE.equals(config.getAuthenticationType()));
            dataFilter.put("_auth.interceptor.ts", () -> OAUTH2_AUTHENTICATION_TYPE.equals(config.getAuthenticationType()) || JWT_AUTHENTICATION_TYPE.equals(config.getAuthenticationType()) || UAA_AUTHENTICATION_TYPE.equals(config.getAuthenticationType()));
            dataFilter.put("_auth-session.service.ts", () -> SESSION_AUTHENTICATION_TYPE.equals(config.getAuthenticationType()));

            dataFilter.put("_session.model.ts", () -> SESSION_AUTHENTICATION_TYPE.equals(config.getAuthenticationType()));
            dataFilter.put("_sessions.service.ts", () -> SESSION_AUTHENTICATION_TYPE.equals(config.getAuthenticationType()));
            dataFilter.put("_sessions.state.ts", () -> SESSION_AUTHENTICATION_TYPE.equals(config.getAuthenticationType()));
            dataFilter.put("_sessions.component.html", () -> SESSION_AUTHENTICATION_TYPE.equals(config.getAuthenticationType()));
            dataFilter.put("_sessions.component.ts", () -> SESSION_AUTHENTICATION_TYPE.equals(config.getAuthenticationType()));

            //ApplicationType
            dataFilter.put("_gateway.component.html", () -> GATEWAY_APPLICATION_TYPE.equals(config.getAuthenticationType()));
            dataFilter.put("_gateway.component.ts", () -> GATEWAY_APPLICATION_TYPE.equals(config.getAuthenticationType()));
            dataFilter.put("_gateway.state.ts", () -> GATEWAY_APPLICATION_TYPE.equals(config.getAuthenticationType()));
            dataFilter.put("_gateway-route.model.ts", () -> GATEWAY_APPLICATION_TYPE.equals(config.getAuthenticationType()));
            dataFilter.put("_gateway-routes.service.ts", () -> GATEWAY_APPLICATION_TYPE.equals(config.getAuthenticationType()));

            //Social Login
            dataFilter.put("_social-register.component.html", () -> config.isEnableSocialSignIn());
            dataFilter.put("_social-register.component.ts", () -> config.isEnableSocialSignIn());
            dataFilter.put("_social-auth.component.ts", () -> config.isEnableSocialSignIn() && JWT_AUTHENTICATION_TYPE.equals(config.getAuthenticationType()));
            dataFilter.put("_social.state.ts", () -> config.isEnableSocialSignIn());
            dataFilter.put("_social.service.ts", () -> config.isEnableSocialSignIn());
            dataFilter.put("_social.component.ts", () -> config.isEnableSocialSignIn());
            dataFilter.put("_social.component.html", () -> config.isEnableSocialSignIn());

            //WebSocket
            dataFilter.put("_tracker.component.html", () -> SPRING_WEBSOCKET.equals(config.getWebsocket()));//admin
            dataFilter.put("_tracker.component.ts", () -> SPRING_WEBSOCKET.equals(config.getWebsocket()));//admin
            dataFilter.put("_tracker.state.ts", () -> SPRING_WEBSOCKET.equals(config.getWebsocket()));//admin
            dataFilter.put("_tracker.service.ts", () -> SPRING_WEBSOCKET.equals(config.getWebsocket()));//shared

            //Language
            dataFilter.put("_jhi-translate.directive.ts", () -> config.isEnableTranslation());//shared/language
            dataFilter.put("_translate-partial-loader.provider.ts", () -> config.isEnableTranslation());//shared/language
            dataFilter.put("_language.pipe.ts", () -> config.isEnableTranslation());//shared/language
            dataFilter.put("_language.constants.ts", () -> config.isEnableTranslation());//shared/language
            dataFilter.put("_language.service.ts", () -> config.isEnableTranslation());//shared/language
            dataFilter.put("_jhi-missing-translation.config.ts", () -> config.isEnableTranslation());//shared/language

            dataFilter.put("_translation.config.ts", () -> config.isEnableTranslation());
            dataFilter.put("_translation-storage.provider.ts", () -> config.isEnableTranslation());
            dataFilter.put("_active-menu.directive.ts", () -> config.isEnableTranslation());//layouts/navbar

            //Metrics
            dataFilter.put("_metrics.component.ts", () -> config.isEnableMetrics());
            dataFilter.put("_metrics-modal.component.ts", () -> config.isEnableMetrics());
            dataFilter.put("_metrics.service.ts", () -> config.isEnableMetrics());
            dataFilter.put("_metrics.component.html", () -> config.isEnableMetrics());
            dataFilter.put("_metrics-modal.component.html", () -> config.isEnableMetrics());
            dataFilter.put("_metrics.state.ts", () -> config.isEnableMetrics());

            //Logs
            dataFilter.put("_logs.component.ts", () -> config.isEnableLogs());
            dataFilter.put("_log.model.ts", () -> config.isEnableLogs());
            dataFilter.put("_logs.service.ts", () -> config.isEnableLogs());
            dataFilter.put("_logs.component.html", () -> config.isEnableLogs());
            dataFilter.put("_logs.state.ts", () -> config.isEnableLogs());

            //Health
            dataFilter.put("_health.component.ts", () -> config.isEnableHealth());//admin/health
            dataFilter.put("_health-modal.component.ts", () -> config.isEnableHealth());//admin/health
            dataFilter.put("_health.service.ts", () -> config.isEnableHealth());//admin/health
            dataFilter.put("_health.component.html", () -> config.isEnableHealth());//admin/health
            dataFilter.put("_health-modal.component.html", () -> config.isEnableHealth());//admin/health
            dataFilter.put("_health.state.ts", () -> config.isEnableHealth());//admin/health

            //Configuration
            dataFilter.put("_configuration.component.ts", () -> config.isEnableConfiguration());
            dataFilter.put("_configuration.component.html", () -> config.isEnableConfiguration());
            dataFilter.put("_configuration.state.ts", () -> config.isEnableConfiguration());
            dataFilter.put("_configuration.service.ts", () -> config.isEnableConfiguration());

            //Audit
            dataFilter.put("_audit-data.model.ts", () -> config.isEnableAudits());//admin/audits/
            dataFilter.put("_audit.model.ts", () -> config.isEnableAudits());//admin/audits/
            dataFilter.put("_audits.component.ts", () -> config.isEnableAudits());//admin/audits/
            dataFilter.put("_audits.component.html", () -> config.isEnableAudits());//admin/audits/
            dataFilter.put("_audits.state.ts", () -> config.isEnableAudits());//admin/audits/
            dataFilter.put("_audits.service.ts", () -> config.isEnableAudits());//admin/audits/

            //Docs
            dataFilter.put("_docs.component.ts", () -> config.isEnableDocs());
            dataFilter.put("_docs.component.html", () -> config.isEnableDocs());
            dataFilter.put("_docs.state.ts", () -> config.isEnableDocs());
            dataFilter.put("swagger-ui/_index.html", () -> config.isEnableDocs());
            dataFilter.put("swagger-ui/images/throbber.gif", () -> config.isEnableDocs());
//            dataFilter.put("swagger-ui/config/resource.tson", () -> config.isEnableDocs());
//            dataFilter.put("swagger-ui/config/ui.tson", () -> config.isEnableDocs());

            //SCSS
            dataFilter.put("scss/_main.scss", () -> config.isUseSass());
            dataFilter.put("scss/_vendor.scss", () -> config.isUseSass());

            //Profile
            dataFilter.put("_profile.service.ts", () -> config.isEnableProfile());//layouts/profiles
            dataFilter.put("_profile-info.model.ts", () -> config.isEnableProfile());//layouts/profiles
            dataFilter.put("_page-ribbon.component.ts", () -> config.isEnableProfile());//layouts/profiles
            
            //Search Engine
//            dataFilter.put("_entity-search.service.ts", () -> ELASTIC_SEARCH_ENGINE.equals(config.getSearchEngine()));

            //Skip UserManagement
            dataFilter.put("_user-management.component.ts", () -> !config.isSkipUserManagement());
            dataFilter.put("_user-management-detail.component.ts", () -> !config.isSkipUserManagement());
            dataFilter.put("_user-management-dialog.component.ts", () -> !config.isSkipUserManagement());
            dataFilter.put("_user-management-delete-dialog.component.ts", () -> !config.isSkipUserManagement());
            dataFilter.put("_user.model.ts", () -> !config.isSkipUserManagement());
            dataFilter.put("_user.service.ts", () -> !config.isSkipUserManagement());
            dataFilter.put("_user-management.state.ts", () -> !config.isSkipUserManagement());
            dataFilter.put("_user-management.component.html", () -> !config.isSkipUserManagement());
            dataFilter.put("_user-management-detail.component.html", () -> !config.isSkipUserManagement());
            dataFilter.put("_user-management-dialog.component.html", () -> !config.isSkipUserManagement());
            dataFilter.put("_user-management-delete-dialog.component.html", () -> !config.isSkipUserManagement());
        }
        return dataFilter;
    }

}
