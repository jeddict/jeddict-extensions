import { Routes, CanActivate } from '@angular/router';

import {
    <%_ if (enableAudits) { _%>
    auditsRoute,
    <%_ } _%>
    <%_ if (enableConfiguration) { _%>
    configurationRoute,
    <%_ } _%>
    docsRoute,
    <%_ if (enableHealth) { _%>
    healthRoute,
    <%_ } _%>
    <%_ if (enableLogs) { _%>
    logsRoute,
    <%_ } _%>
    metricsRoute,
    <%_ if (applicationType === 'gateway') { _%>
    gatewayRoute,
    <%_ } _%>
    <%_ if (websocket === 'spring-websocket') { _%>
    trackerRoute,
    <%_ } _%>
    <%_ if (!skipUserManagement) { _%>
    userMgmtRoute,
    userDialogRoute
    <%_ } _%>
} from './';

import { UserRouteAccessService } from '../shared';

let ADMIN_ROUTES = [
    <%_ if (enableAudits) { _%>
    auditsRoute,
    <%_ } _%>
    <%_ if (enableConfiguration) { _%>
    configurationRoute,
    <%_ } _%>
    docsRoute,
    <%_ if (enableHealth) { _%>
    healthRoute,
    <%_ } _%>
    <%_ if (enableLogs) { _%>
    logsRoute,
    <%_ } _%>
    <%_ if (applicationType === 'gateway') { _%>
    gatewayRoute,
    <%_ } _%>
    <%_ if (websocket === 'spring-websocket') { _%>
    trackerRoute,
    <%_ } _%>
    <%_ if (!skipUserManagement) { _%>
    ...userMgmtRoute,
    <%_ } _%>
    metricsRoute
];


export const adminState: Routes = [{
    path: '',
    data: {
        authorities: ['ROLE_ADMIN']
    },
    canActivate: [UserRouteAccessService],
    children: ADMIN_ROUTES
},
    <%_ if (!skipUserManagement) { _%>
    ...userDialogRoute
    <%_ } _%>
];
