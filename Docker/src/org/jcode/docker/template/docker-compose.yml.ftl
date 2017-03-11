version: '2'
services:
    web:
        build:
            context: .
            dockerfile: DockerFile
            args:
                BINARY: ${r"${docker.binary}"}
                <#if docker.serverType.name() != "PAYARA_MICRO" && docker.serverType.name() != "WILDFLY_SWARM">
                DB_DATASOURCE: '${docker.dataSource}'
                DB_NAME: '${r"${db.name}"}'
                DB_USER: '${r"${db.user}"}'
                DB_PASS: '${r"${db.password}"}'
                DB_HOST: '${r"${db.host}"}'
                DB_PORT: '${r"${db.port}"}'
                </#if>
        ports:
            - "8080:8080" 
            - "8081:8081"
        links:
            - db 
    db:
<#if docker.databaseType == "MySQL" || docker.databaseType == "MariaDB">
        <#if docker.databaseType == "MySQL">
        image: mysql:${docker.databaseVersion}
        <#elseif docker.databaseType == "MariaDB">
        image: mariadb:${docker.databaseVersion}
        </#if>
        ports:
            - "${r"${db.port}"}:3306"  
        environment:
            MYSQL_ROOT_PASSWORD: '${r"${db.password}"}'
            MYSQL_USER: '${r"${db.user}"}'
            MYSQL_PASSWORD: '${r"${db.password}"}'
            MYSQL_DATABASE: '${r"${db.name}"}'
#        volumes:
#            - data-mysql:/var/lib/mysql
#volumes:  
#    data-mysql:
#        driver: local       
<#elseif docker.databaseType == "PostgreSQL">
        image: postgres:${docker.databaseVersion}
        ports:
            - "${r"${db.port}"}:5432"  
        environment:
            POSTGRES_USER: '${r"${db.user}"}'
            POSTGRES_PASSWORD: '${r"${db.password}"}'
            POSTGRES_DB: '${r"${db.name}"}'
#        volumes:
#           - data-postgres:/var/lib/postgresql/data
#volumes:  
#    data-postgres:
#      driver: local
</#if>