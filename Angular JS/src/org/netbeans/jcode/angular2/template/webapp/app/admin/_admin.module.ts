import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { UIRouterModule } from 'ui-router-ng2';
import { ParseLinks } from 'ng-jhipster';

import { <%=angular2AppName%>SharedModule } from '../shared';

import {
    <%_ if (enableAudits) { _%>
    AuditsComponent,
    <%_ } _%>
    <%_ if (!skipUserManagement) { _%>
    UserMgmtComponent,
    UserMgmtDetailComponent,
    UserMgmtDialogComponent,
    UserMgmtDeleteDialogComponent,
    <%_ } _%>
<%_ if (enableLogs) { _%>
    LogsComponent,
<%_ } _%>
<%_ if (enableMetrics) { _%>
    <%=jhiPrefixCapitalized%>MetricsMonitoringModalComponent,
    <%=jhiPrefixCapitalized%>MetricsMonitoringComponent,
<%_ } _%>
<%_ if (enableHealth) { _%>
    <%=jhiPrefixCapitalized%>HealthModalComponent,
    <%=jhiPrefixCapitalized%>HealthCheckComponent,
<%_ } _%>
<%_ if (enableConfiguration) { _%>
    <%=jhiPrefixCapitalized%>ConfigurationComponent,
<%_ } _%>
<%_ if (enableDocs) { _%>
    <%=jhiPrefixCapitalized%>DocsComponent,
<%_ } _%>
<%_ if (enableAudits) { _%>
    AuditsService,
<%_ } _%>
    UserService,
<%_ if (enableConfiguration) { _%>
    <%=jhiPrefixCapitalized%>ConfigurationService,
<%_ } _%>
<%_ if (enableHealth) { _%>
    <%=jhiPrefixCapitalized%>HealthService,
<%_ } _%>
<%_ if (enableMetrics) { _%>
    <%=jhiPrefixCapitalized%>MetricsService,
<%_ } _%>
    <%_ if (applicationType === 'gateway') { _%>
    GatewayRoutesService,
    <%=jhiPrefixCapitalized%>GatewayComponent,
    gatewayState,
    <%_ } _%>
    <%_ if (websocket === 'spring-websocket') { _%>
    <%=jhiPrefixCapitalized%>TrackerComponent,
    trackerState,
    <%_ } _%>
<%_ if (enableLogs) { _%>
    LogsService,
<%_ } _%>
<%_ if (enableAudits) { _%>
    auditState,
<%_ } _%>
<%_ if (enableConfiguration) { _%>
    configState,
<%_ } _%>
<%_ if (enableDocs) { _%>
    docsState,
<%_ } _%>
<%_ if (enableHealth) { _%>
    healthState,
<%_ } _%>
<%_ if (enableLogs) { _%>
    logsState,
<%_ } _%>
    <%_ if (!skipUserManagement) { _%>
    userMgmtState,
    userMgmtDetailState,
    userMgmtNewState,
    userMgmtEditState,
    userMgmtDeleteState,
    <%_ } _%>
<%_ if (enableMetrics) { _%>
    metricsState,
<%_ } _%>
    adminState
} from './';

let ADMIN_STATES = [
    <%_ if (enableAudits) { _%>
    auditState,
    <%_ } _%>
    <%_ if (enableConfiguration) { _%>
    configState,
    <%_ } _%>
    <%_ if (enableDocs) { _%>
    docsState,
    <%_ } _%>
    <%_ if (enableHealth) { _%>
    healthState,
    <%_ } _%>
    <%_ if (enableLogs) { _%>
    logsState,
    <%_ } _%>
    <%_ if (applicationType === 'gateway') { _%>
    gatewayState,
    <%_ } _%>
    <%_ if (websocket === 'spring-websocket') { _%>
    trackerState,
    <%_ } _%>
    <%_ if (!skipUserManagement) { _%>
    userMgmtState,
    userMgmtDetailState,
    userMgmtNewState,
    userMgmtEditState,
    userMgmtDeleteState,
    <%_ } _%>
    <%_ if (enableMetrics) { _%>
    metricsState,
    <%_ } _%>
    adminState

];

@NgModule({
    imports: [
        <%=angular2AppName%>SharedModule,
        UIRouterModule.forChild({ states: ADMIN_STATES })
    ],
    declarations: [
<%_ if (enableAudits) { _%>
        AuditsComponent,
<%_ } _%>
<%_ if (enableLogs) { _%>
        LogsComponent,
<%_ } _%>
<%_ if (enableConfiguration) { _%>
        <%=jhiPrefixCapitalized%>ConfigurationComponent,
<%_ } _%>
<%_ if (enableHealth) { _%>
        <%=jhiPrefixCapitalized%>HealthCheckComponent,
        <%=jhiPrefixCapitalized%>HealthModalComponent,
<%_ } _%>
<%_ if (enableDocs) { _%>
        <%=jhiPrefixCapitalized%>DocsComponent,
<%_ } _%>
        <%_ if (applicationType === 'gateway') { _%>
        <%=jhiPrefixCapitalized%>GatewayComponent,
        <%_ } _%>
        <%_ if (websocket === 'spring-websocket') { _%>
        <%=jhiPrefixCapitalized%>TrackerComponent,
        <%_ } _%>
<%_ if (enableMetrics) { _%>
        <%=jhiPrefixCapitalized%>MetricsMonitoringComponent,
        <%=jhiPrefixCapitalized%>MetricsMonitoringModalComponent,
<%_ } _%>
        <%_ if (!skipUserManagement) { _%>
        UserMgmtComponent,
        UserMgmtDetailComponent,
        UserMgmtDialogComponent,
        UserMgmtDeleteDialogComponent
        <%_ } _%>
    ],
    entryComponents: [
<%_ if (enableHealth) { _%>
        <%=jhiPrefixCapitalized%>HealthModalComponent,
 <%_ } _%>
<%_ if (enableMetrics) { _%>
        <%=jhiPrefixCapitalized%>MetricsMonitoringModalComponent,
 <%_ } _%>
        <%_ if (!skipUserManagement) { _%>
        UserMgmtDialogComponent,
        UserMgmtDeleteDialogComponent
        <%_ } _%>
    ],
    providers: [
<%_ if (enableAudits) { _%>
        AuditsService,
<%_ } _%>
<%_ if (enableConfiguration) { _%>
        <%=jhiPrefixCapitalized%>ConfigurationService,
<%_ } _%>
<%_ if (enableHealth) { _%>
        <%=jhiPrefixCapitalized%>HealthService,
<%_ } _%>
<%_ if (enableMetrics) { _%>
        <%=jhiPrefixCapitalized%>MetricsService,
<%_ } _%>
        <%_ if (applicationType === 'gateway') { _%>
        GatewayRoutesService,
        <%_ } _%>
<%_ if (enableLogs) { _%>
        LogsService,
<%_ } _%>
        UserService
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class <%=angular2AppName%>AdminModule {}
