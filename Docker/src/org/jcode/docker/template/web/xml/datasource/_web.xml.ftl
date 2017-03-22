<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         version="3.1">
    <data-source>
        <!--  environment-specific properties applied using maven-war-plugin Webapp resource filtering -->
        <name>${jndi}</name>
        <class-name>${driverClass}</class-name>
        <server-name>${r"${db.host}"}</server-name>
        <port-number>${r"${db.port}"}</port-number>
        <database-name>${r"${db.name"}}</database-name>
        <user>${r"${db.user}"}</user>
        <password>${r"${db.password}"}</password>
    </data-source>
</web-app>
