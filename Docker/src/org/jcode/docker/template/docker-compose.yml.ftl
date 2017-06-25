version: '2'
services:
    web:
        build:
            context: .
            dockerfile: DockerFile
            args:
                BINARY: ${r"${docker.binary}"}
                <#if SERVER_TYPE != "PAYARA_MICRO" && SERVER_TYPE != "WILDFLY_SWARM">
                DB_DATASOURCE: '${DATASOURCE}'
                DB_NAME: '${r"${db.name}"}'
                DB_USER: '${r"${db.user}"}'
                DB_PASS: '${r"${db.password}"}'
                DB_SVC: '${r"${db.svc}"}'
                DB_PORT: '${r"${db.port}"}'
                </#if>
        ports:
            - "8080:8080" 
            - "8081:8081"
        links:
            - '${r"${db.svc}"}' 
    '${r"${db.svc}"}':
        image: ${DB_TYPE}:${DB_VERSION}
        ports:
            - "${r"${db.port}"}:${DB_PORT}"   
        environment:
<#if DB_TYPE == "mysql" || DB_TYPE == "mariadb">
            MYSQL_ROOT_PASSWORD: '${r"${db.password}"}'
            MYSQL_USER: '${r"${db.user}"}'
            MYSQL_PASSWORD: '${r"${db.password}"}'
            MYSQL_DATABASE: '${r"${db.name}"}'
#        volumes:
#            - data-mysql:/var/lib/mysql
#volumes:  
#    data-mysql:
#        driver: local       
<#elseif DB_TYPE == "postgres">
            POSTGRES_USER: '${r"${db.user}"}'
            POSTGRES_PASSWORD: '${r"${db.password}"}'
            POSTGRES_DB: '${r"${db.name}"}'
#        volumes:
#           - data-postgres:/var/lib/postgresql/data
#volumes:  
#    data-postgres:
#      driver: local
</#if>