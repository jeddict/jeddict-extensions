import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ParseLinks } from 'ng-jhipster';
<%_ if (websocket === 'spring-websocket') { _%>
import { <%=jhiPrefixCapitalized%>TrackerService } from './../shared/tracker/tracker.service';
<%_ } _%>

import { <%=angular2AppName%>SharedModule } from '../shared';

import {
    adminState,
    <%_ if (enableAudits) { _%>
    AuditsComponent,
    <%_ } _%>
    <%_ if (!skipUserManagement) { _%>
    UserMgmtComponent,
    UserDialogComponent,
    UserDeleteDialogComponent,
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
    <%_ } _%>
    <%_ if (websocket === 'spring-websocket') { _%>
    <%=jhiPrefixCapitalized%>TrackerComponent,
    <%_ } _%>
<%_ if (enableLogs) { _%>
    LogsService,
<%_ } _%>
    <%_ if (!skipUserManagement) { _%>
    UserResolvePagingParams,
    UserResolve,
    UserModalService
    <%_ } _%>
} from './';


@NgModule({
    imports: [
        <%=angular2AppName%>SharedModule,
        RouterModule.forRoot(adminState, { useHash: true })
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
        UserDialogComponent,
        UserDeleteDialogComponent,
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
        <%_ if (websocket === 'spring-websocket') { _%>
        <%=jhiPrefixCapitalized%>TrackerService,
        <%_ } _%>
        <%_ if (!skipUserManagement) { _%>
        UserResolvePagingParams,
        UserResolve,
        UserModalService
        <%_ } _%>
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class <%=angular2AppName%>AdminModule {}
