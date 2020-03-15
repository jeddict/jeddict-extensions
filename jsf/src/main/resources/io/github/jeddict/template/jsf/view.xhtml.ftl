<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:ui="http://xmlns.jcp.org/jsf/facelets"
      xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:f="http://xmlns.jcp.org/jsf/core"
      xmlns:p="http://primefaces.org/ui">

    <ui:composition template="/template.xhtml">

        <ui:define name="title">
            <h:outputText value="View ${Entity}"></h:outputText>
        </ui:define>

        <ui:define name="header" >
        </ui:define>

        <ui:define name="body" >
            <div>
                <h1 align="center">View ${Entity} </h1>
            </div>
            <h:form id="view">
                <h:panelGrid columns="2" style="margin-left: 25%; width: 50%" border = "1" cellpadding = "5" >
                   
                    <#list attributes as attribute>
                        <p:outputLabel for="${attribute.name}" value="${attribute.name}" />
                        <p:outputLabel id="${attribute.name}" value="${hash}{${EntityController}.selected.${attribute.name}}" />
                    </#list>
                </h:panelGrid>
                <p:commandButton style="margin-left: 50%" value="Back" action="${hash}{${EntityController}.prepareList}" immediate="true"></p:commandButton>

            </h:form>  
        </ui:define>
    </ui:composition>
</html>
