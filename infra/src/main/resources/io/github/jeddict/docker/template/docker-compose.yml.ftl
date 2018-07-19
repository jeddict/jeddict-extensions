version: '2'
services:
    ${r"${web.svc}"}:
        build:
            context: .
            dockerfile: DockerFile
            args:
                BINARY: '${r"${docker.binary}"}'
                EXPOSE_PORT: '${r"${web.port}"}'
        ports:
            - "${r"${web.port}"}:8080" 
        links:
            - '${r"${db.svc}"}' 
    ${r"${db.svc}"}:
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