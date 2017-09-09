    <security-constraint>
<#list entityConstraints as entityConstraint>
        <web-resource-collection>
            <web-resource-name>${entityConstraint.name}</web-resource-name>
            <url-pattern>/resources/api/${entityConstraint.urlPath}</url-pattern>
        </web-resource-collection>
</#list>
        <auth-constraint>
            <role-name>ROLE_USER</role-name>
        </auth-constraint>
    </security-constraint>