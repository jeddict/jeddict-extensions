export * from './error/error.component';
export * from './error/error.route';
export * from './main/main.component';
export * from './footer/footer.component';
export * from './navbar/navbar.component';
<%_ if (enableTranslation) { _%>
export * from './navbar/active-menu.directive';
<%_ } _%>
<%_ if (enableProfile) { _%>
export * from './profiles/page-ribbon.component';
export * from './profiles/profile.service';
export * from './profiles/profile-info.model';
<%_ } _%>
export * from './layout-routing.module';
