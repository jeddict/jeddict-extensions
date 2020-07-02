<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:ui="http://xmlns.jcp.org/jsf/facelets"
      xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:f="http://xmlns.jcp.org/jsf/core"
      xmlns:p="http://primefaces.org/ui">

    
    <ui:composition template="/template.xhtml">

        <ui:define name="title">
            <h:outputText value="Edit ${Entity}"></h:outputText>
        </ui:define>

        <ui:define name="header" >
        </ui:define>

        <ui:define name="body" >
            <div>
                <h1 align="center">Edit ${Entity} </h1>
            </div>
            <h:form id="edit" style="margin-left: 20%;margin-right: 20%">
                <h:panelGrid columns="3" cellpadding="5" style="margin: 0 auto;">
                 <#list attributes as attribute>
                        <#if (attribute.attributeType == "String")|| 
                    (attribute.attributeType == "long") || (attribute.attributeType == "Long") ||
                    (attribute.attributeType == "int") || (attribute.attributeType == "Integer")||
                    (attribute.attributeType == "char") || (attribute.attributeType == "Character")||
                    (attribute.attributeType == "byte") || (attribute.attributeType == "Byte")||
                    (attribute.attributeType == "float") || (attribute.attributeType == "Float")||
                    (attribute.attributeType == "double") || (attribute.attributeType == "Double")||
                    (attribute.attributeType == "short") || (attribute.attributeType == "Short")||
                    (attribute.attributeType == "java.math.BigInteger") || (attribute.attributeType == "java.math.BigDecimal")>
                    <p:outputLabel for="${attribute.name}" value="${attribute.name}" />
                    <p:inputText id="${attribute.name}" value="${hash}{${EntityController}.selected.${attribute.name}}">
                    </p:inputText>  
                    <p:messages for="${attribute.name}">                        
                        <p:autoUpdate />                    
                    </p:messages>

                        <#elseif (attribute.attributeType == "boolean") || (attribute.attributeType == "Boolean")  >
                    <p:outputLabel for="${attribute.name}" value="${attribute.name}" />
                    <p:selectBooleanCheckbox value="${hash}{${EntityController}.selected.${attribute.name}}" id="${attribute.name}"/>
                    </#if>
                </#list>
                    <p:commandButton  value="Update" action="${hash}{${EntityController}.update}">
                    </p:commandButton>
                    <p:commandButton value="Cancel" action="${hash}{${EntityController}.prepareList}" immediate="true">
                    </p:commandButton>
                </h:panelGrid>
            </h:form>  
        </ui:define>
    </ui:composition>
</html>
