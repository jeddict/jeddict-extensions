<#if database == "MySQL">
    <#assign escape = "`" >
    <#assign bool = "1" >
<#elseif database == "PostgreSQL">
    <#assign escape = "" >
    <#assign bool = "true" >
<#elseif database == "Derby">
    <#assign escape = "\"" >
    <#assign bool = "1" >
<#else>
    <#assign escape = "\"" >
    <#assign bool = "1" >
</#if>
INSERT INTO account(id, login, email, first_name, last_name, lang_key, activated, created_date, password_hash) VALUES (1001,  'user', 'user@example.com', 'User_F', 'User_L', 'en', ${bool}, '2016-08-10 09:27:36.388', 'ee11cbb19052e40b07aac0ca060c23ee')
INSERT INTO account(id, login, email, first_name, last_name, lang_key, activated, created_date, password_hash) VALUES (1002, 'admin', 'admin@example.com', 'Admin_F', 'Admin_L', 'en', ${bool}, '2016-08-10 09:27:58.256', '21232f297a57a5a743894a0e4a801fc3')
INSERT INTO authority (${escape}name${escape}) VALUES ('ROLE_ADMIN')
INSERT INTO authority (${escape}name${escape}) VALUES ('ROLE_ANONYMOUS')
INSERT INTO authority (${escape}name${escape}) VALUES ('ROLE_USER')
INSERT INTO user_authority (user_id, authority_name) VALUES (1001, 'ROLE_USER')
INSERT INTO user_authority (user_id, authority_name) VALUES (1002, 'ROLE_USER')
INSERT INTO user_authority (user_id, authority_name) VALUES (1002, 'ROLE_ADMIN')