<?xml version="1.0" encoding="UTF-8"?>
<persistence version="2.1" xmlns="http://xmlns.jcp.org/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd">
  <persistence-unit name="${PU_NAME}" transaction-type="JTA">
    <#--<jta-data-source>jdbc/arquillian</jta-data-source>-->
    <jta-data-source>java:comp/DefaultDataSource</jta-data-source>
    <exclude-unlisted-classes>false</exclude-unlisted-classes>
    <properties>
      <property name="javax.persistence.schema-generation.database.action" value="drop-and-create"/>
      <#if !microservices><property name="javax.persistence.sql-load-script-source" value="META-INF/sql/insert.sql"/></#if>
    </properties>
  </persistence-unit>
</persistence>
