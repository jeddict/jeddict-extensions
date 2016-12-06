<?xml version="1.0" encoding="UTF-8"?>
<persistence version="2.0" xmlns="http://java.sun.com/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="         http://java.sun.com/xml/ns/persistence         http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd">
  <persistence-unit name="${PU_NAME}" transaction-type="JTA">
    <jta-data-source>jdbc/arquillian</jta-data-source>
    <exclude-unlisted-classes>false</exclude-unlisted-classes>
    <properties>
      <property name="javax.persistence.schema-generation.database.action" value="drop-and-create"/>
      <property name="javax.persistence.sql-load-script-source" value="META-INF/sql/insert.sql"/>
    </properties>
  </persistence-unit>
</persistence>