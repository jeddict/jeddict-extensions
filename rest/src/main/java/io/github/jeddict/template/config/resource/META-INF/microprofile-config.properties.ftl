#MAIL
service.mail.enable = false
service.mail.host = smtp.gmail.com
service.mail.port = 587
service.mail.auth.username = sample_user@example.com
service.mail.auth.password = sample_password
service.mail.from = from@example.com
service.mail.baseurl = http://127.0.0.1:8080/myContextPath

#SECURITY
security.jwt.issuer=com.mycompany
security.jwt.validityInSeconds: 86400
security.jwt.validityInSecondsForRememberMe: 1314000

context.path = ${r"${context.path}"}
<#if microservices>
web.host = ${r"${web.host}"}
web.port = ${r"${web.port}"}
</#if><#if microservices || gateway>
registry.url: ${r"${registry.url}"}</#if>