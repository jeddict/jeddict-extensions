<?xml version="1.0" encoding="UTF-8"?>
<arquillian xmlns="http://jboss.org/schema/arquillian"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://jboss.org/schema/arquillian http://jboss.org/schema/arquillian/arquillian_1_0.xsd">

    <defaultProtocol type="Servlet 5.0"/>
    <container qualifier="payara">
        <configuration>
            <property name="allowConnectingToRunningServer">true</property>
            <!--<property name="domain">production</property>-->
        </configuration>
    </container>
    
</arquillian>