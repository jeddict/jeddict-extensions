
#MAIL
service.mail.enable = false
service.mail.host = smtp.gmail.com
service.mail.port = 587
service.mail.auth.username = sample_user@example.com
service.mail.auth.password = sample_password
service.mail.from = from@example.com
service.mail.baseurl = http://127.0.0.1:8080/myContextPath

#SECURITY
security.authentication.jwt.secret: my-secret-sample-token
security.authentication.jwt.tokenValidityInSeconds: 86400
security.authentication.jwt.tokenValidityInSecondsForRememberMe: 1314000

<#if metrics>#METRICS
metrics.jmx.enable: true
metrics.logs.enable: false</#if>