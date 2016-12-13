FROM payara/server-full:latest
WORKDIR $PAYARA_PATH/bin
# User: admin & Password: admin
RUN echo "AS_ADMIN_PASSWORD=admin" > /tmp/pwdfile

ARG BINARY
ARG DB_HOST
ARG DB_PORT
ARG DB_NAME
ARG DB_USER
ARG DB_PASS
ARG DB_DATASOURCE
ARG DB_JNDI=jdbc/$DB_DATASOURCE

<#if docker.databaseType == "MySQL">
ENV MYSQL_VERSION 5.1.38
# Install mysql drivers and datasource
RUN curl -L -o $PAYARA_PATH/glassfish/lib/mysql-$MYSQL_VERSION.jar http://central.maven.org/maven2/mysql/mysql-connector-java/$MYSQL_VERSION/mysql-connector-java-$MYSQL_VERSION.jar && \
    ./asadmin --user=admin start-domain && \
    ./asadmin --user=admin --passwordfile=/tmp/pwdfile create-jdbc-connection-pool --datasourceclassname=com.mysql.jdbc.jdbc2.optional.MysqlXADataSource --restype javax.sql.XADataSource --property password=$DB_PASS:user=$DB_USER:DatabaseName=$DB_NAME:ServerName=$DB_HOST:port=$DB_PORT $DB_DATASOURCE && \
<#elseif docker.databaseType == "PostgreSQL">
ENV POSTGRESQL_VERSION 9.1-901.jdbc4
# Install Postgre drivers and datasource
RUN curl -L -o $PAYARA_PATH/glassfish/lib/postgresql-$POSTGRESQL_VERSION.jar https://repo1.maven.org/maven2/postgresql/postgresql/$POSTGRESQL_VERSION/postgresql-$POSTGRESQL_VERSION.jar && \
    ./asadmin --user=admin start-domain && \
    ./asadmin --user=admin --passwordfile=/tmp/pwdfile create-jdbc-connection-pool --datasourceclassname=org.postgresql.xa.PGXADataSource --restype javax.sql.XADataSource --property password=$DB_PASS:user=$DB_USER:DatabaseName=$DB_NAME:ServerName=$DB_HOST:port=$DB_PORT $DB_DATASOURCE && \
</#if>
    ./asadmin --user=admin --passwordfile=/tmp/pwdfile create-jdbc-resource --connectionpoolid $DB_DATASOURCE $DB_JNDI && \
#   ./asadmin --user=admin --passwordfile=/tmp/pwdfile list-jdbc-connection-pools && \
#   ./asadmin --user admin --passwordfile /tmp/pwdfile list-jdbc-resources && \
    ./asadmin --user=admin stop-domain && \
    rm /tmp/pwdfile

# Deploy the binary
ADD $BINARY $PAYARA_PATH/glassfish/domains/domain1/autodeploy

# Expose the ports we're interested in
EXPOSE 8080

CMD $PAYARA_PATH/bin/asadmin start-domain --verbose
