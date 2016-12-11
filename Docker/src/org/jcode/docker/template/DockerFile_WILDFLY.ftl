FROM jboss/wildfly:latest
#VOLUME ["/maven"]
# User root user to install software
USER root
# Install packages necessary to run EAP
RUN yum update -y && yum -y install curl && yum clean all

# Specify the user which should be used to execute all commands below
USER jboss
WORKDIR $JBOSS_HOME
ARG BINARY

ARG DB_NAME
ARG DB_USER
ARG DB_PASS
ARG DB_DATASOURCE
ARG DB_JNDI=java:/jdbc/datasources/$DB_DATASOURCE

<#if docker.databaseType == "MySQL">
ENV MYSQL_VERSION 5.1.38
ARG DB_HOST=$DB_PORT_3306_TCP_ADDR
ARG DB_PORT=$DB_PORT_3306_TCP_PORT

# Install mysql drivers and datasource
RUN /bin/sh -c '$JBOSS_HOME/bin/standalone.sh &' && \
  sleep 10 && \
  cd /tmp && \
  curl -L -o mysql-$MYSQL_VERSION.jar http://central.maven.org/maven2/mysql/mysql-connector-java/$MYSQL_VERSION/mysql-connector-java-$MYSQL_VERSION.jar && \
  $JBOSS_HOME/bin/jboss-cli.sh --connect --command="deploy /tmp/mysql-$MYSQL_VERSION.jar"  && \
  $JBOSS_HOME/bin/jboss-cli.sh --connect --command="module add --name=com.mysql --resources=/tmp/mysql-$MYSQL_VERSION.jar --dependencies=javax.api,javax.transaction.api" && \
  $JBOSS_HOME/bin/jboss-cli.sh --connect --command="/subsystem=datasources/jdbc-driver=mysql:add(driver-name=mysql,driver-module-name=com.mysql,driver-xa-datasource-class-name=com.mysql.jdbc.jdbc2.optional.MysqlXADataSource)" && \
  $JBOSS_HOME/bin/jboss-cli.sh --connect --command="data-source add --name=$DB_DATASOURCE --driver-name=mysql --jndi-name=$DB_JNDI --connection-url=jdbc:mysql://$DB_HOST:$DB_PORT/$DB_NAME --user-name=$DB_USER --password=$DB_PASS --use-ccm=false --max-pool-size=25 --blocking-timeout-wait-millis=5000 --enabled=true" && \
  $JBOSS_HOME/bin/jboss-cli.sh --connect --command=:shutdown && \
  # Fix for WFLYCTL0056
  rm -rf $JBOSS_HOME/standalone/configuration/standalone_xml_history/ $JBOSS_HOME/standalone/log/* && \ 
  rm -rf /tmp/mysql-*.jar
<#elseif docker.databaseType == "PostgreSQL">
ENV POSTGRESQL_VERSION 9.1-901.jdbc4
ARG DB_HOST=$POSTGRES_PORT_5432_TCP_ADDR
ARG DB_PORT=$POSTGRES_PORT_5432_TCP_PORT

# Install postgres drivers and datasource
RUN /bin/sh -c '$JBOSS_HOME/bin/standalone.sh &' && \
  sleep 10 && \
  cd /tmp && \
  curl -L -o postgresql-$POSTGRESQL_VERSION.jar https://repo1.maven.org/maven2/postgresql/postgresql/$POSTGRESQL_VERSION/postgresql-$POSTGRESQL_VERSION.jar && \
  $JBOSS_HOME/bin/jboss-cli.sh --connect --command="deploy /tmp/postgresql-$POSTGRESQL_VERSION.jar" && \
  $JBOSS_HOME/bin/jboss-cli.sh --connect --command="module add --name=com.postgresql --resources=/tmp/postgresql-$POSTGRESQL_VERSION.jar --dependencies=javax.api,javax.transaction.api" && \
  $JBOSS_HOME/bin/jboss-cli.sh --connect --command="/subsystem=datasources/jdbc-driver=postgresql:add(driver-name=postgresql,driver-module-name=com.postgresql,driver-xa-datasource-class-name=org.postgresql.xa.PGXADataSource)" && \
  $JBOSS_HOME/bin/jboss-cli.sh --connect --command="data-source add --name=$DB_DATASOURCE --driver-name=postgresql --jndi-name=$DB_JNDI --connection-url=jdbc:postgresql://$DB_HOST:$DB_PORT/$DB_NAME --user-name=$DB_USER --password=$DB_PASS --use-ccm=false --max-pool-size=25 --blocking-timeout-wait-millis=5000 --enabled=true" && \
  $JBOSS_HOME/bin/jboss-cli.sh --connect --command=:shutdown && \
  # Fix for WFLYCTL0056
  rm -rf $JBOSS_HOME/standalone/configuration/standalone_xml_history/ $JBOSS_HOME/standalone/log/* && \ 
  rm -rf /tmp/postgresql-*.jar
</#if>

# Deploy the binary
ADD $BINARY $JBOSS_HOME/standalone/deployments/

# Expose the ports we're interested in
EXPOSE 8080

# Set the default command to run on boot
# This will boot WildFly in the standalone mode and bind to all interface
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0"]
