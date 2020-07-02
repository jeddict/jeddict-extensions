<?xml version='1.0' encoding='UTF-8' ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:ui="http://xmlns.jcp.org/jsf/facelets"
      xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:p="http://primefaces.org/ui">

    <h:head>
        <title><ui:insert name="title">Default Title</ui:insert></title>
        <h:outputStylesheet library="css" name="jsfcrud.css"/>
        <h:outputScript library="js" name="jsfcrud.js"/>

    </h:head>

    <h:body>

        <p:growl id="growl" life="3000" />

        <p:layout fullPage="true"  >
            <p:layoutUnit position="north"  size="100"  resizable="true" closable="true" collapsible="true" >

            <p:commandButton id="home" icon="ui-icon-home" value="Home" style="float:right; margin-right: 2%"></p:commandButton>

                <ui:insert name="header" />
            </p:layoutUnit>

            <p:layoutUnit position="south" size="50"  resizable="true" closable="true" collapsible="true">
                <h:outputText value="copy right by jeddict" />
            </p:layoutUnit>

            <p:layoutUnit position="center">
                <ui:insert name="body"/>
            </p:layoutUnit>

        </p:layout>

    </h:body>

</html>
