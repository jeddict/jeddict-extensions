import './vendor.ts';
<%_ if (authenticationType === 'uaa') { _%>
import { AuthInterceptor } from './blocks/interceptor/auth.interceptor';
<%_ } %>
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { BrowserModule } from '@angular/platform-browser';
import { Ng2Webstorage } from 'ng2-webstorage';

import { <%=angular2AppName%>SharedModule, UserRouteAccessService } from './shared';
import { <%=angular2AppName%>HomeModule } from './home/home.module';
import { <%=angular2AppName%>AdminModule } from './admin/admin.module';
import { <%=angular2AppName%>AccountModule } from './account/account.module';
import { <%=angular2AppName%>EntityModule } from './entities/entity.module';

import { LayoutRoutingModule } from './layouts';
import { customHttpProvider } from './blocks/interceptor/http.provider';
import { PaginationConfig } from './blocks/config/uib-pagination.config';

import {
    <%=jhiPrefixCapitalized%>MainComponent,
    NavbarComponent,
    FooterComponent,
    <%_ if(enableProfile) { _%>
    ProfileService,
    PageRibbonComponent,
    <%_ } _%>
    <%_ if (enableTranslation) { _%>
    ActiveMenuDirective,
    <%_ } _%>
    ErrorComponent
} from './layouts';


@NgModule({
    imports: [
        BrowserModule,
        LayoutRoutingModule,
        Ng2Webstorage.forRoot({ prefix: 'jhi', separator: '-'}),
        <%=angular2AppName%>SharedModule,
        <%=angular2AppName%>HomeModule,
        <%=angular2AppName%>AdminModule,
        <%=angular2AppName%>AccountModule,
        <%=angular2AppName%>EntityModule
    ],
    declarations: [
        <%=jhiPrefixCapitalized%>MainComponent,
        NavbarComponent,
        ErrorComponent,
        <%_ if (enableProfile) { _%>
        PageRibbonComponent,
        <%_ } _%>
        <%_ if (enableTranslation) { _%>
        ActiveMenuDirective,
        <%_ } _%>
        FooterComponent
    ],
    providers: [
        <%_ if(enableProfile) { _%>ProfileService,<%_ } _%>
        { provide: Window, useValue: window },
        { provide: Document, useValue: document },
        customHttpProvider(),
        PaginationConfig,
        UserRouteAccessService
    ],
    bootstrap: [ <%=jhiPrefixCapitalized%>MainComponent ]
})
export class <%=angular2AppName%>AppModule {}
