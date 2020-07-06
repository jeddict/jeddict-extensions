<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:ui="http://xmlns.jcp.org/jsf/facelets"
      xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:f="http://xmlns.jcp.org/jsf/core"
      xmlns:p="http://primefaces.org/ui">

    <ui:composition template="/template.xhtml">

        <ui:define name="title">
            <h:outputText value="Create ${Entity}"></h:outputText>
        </ui:define>

        <ui:define name="header" >
        </ui:define>

        <ui:define name="body" >
            <div>
                <h1 align="center">Create ${Entity} </h1>
            </div>
            <h:form id="add" style="margin-left: 20%;margin-right: 20%">
                <h:panelGrid columns="3" cellpadding="5" style="margin: 0 auto;">
                    <#list attributes as attribute>
                    <#if (attribute.getClass().getSimpleName()) == "Basic">
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
                    
                    <#elseif (attribute.attributeType == "java.time.LocalDateTime")>
                    <p:outputLabel for="${attribute.name}" value="${attribute.name}" />
                    <p:datePicker id="${attribute.name}" value="${hash}{${EntityController}.selected.${attribute.name}}" showTime="true" showSeconds="true" pattern="MM/dd/yyyy HH:mm:ss"/>
                    <p:messages for="${attribute.name}">
                        <p:autoUpdate />
                    </p:messages>
                    
                    <#elseif (attribute.attributeType == "java.time.LocalDate")  >
                    <p:outputLabel for="${attribute.name}" value="${attribute.name}" />
                    <p:datePicker id="${attribute.name}" value="${hash}{${EntityController}.selected.${attribute.name}}"/>
                    <p:messages for="${attribute.name}">
                        <p:autoUpdate />
                    </p:messages>
                    
                    <#elseif (attribute.attributeType == "java.time.LocalTime")  >
                    <p:outputLabel for="${attribute.name}" value="${attribute.name}" />
                    <p:datePicker id="${attribute.name}" value="${hash}{${EntityController}.selected.${attribute.name}}" timeOnly="true" showSeconds="true" pattern="HH:mm:ss"/>
                    <p:messages for="${attribute.name}">
                        <p:autoUpdate />
                    </p:messages>  
                    
                    <#elseif (attribute.attributeType == "java.util.Date")>
                    <p:outputLabel for="${attribute.name}" value="${attribute.name}" />
                        <#if (attribute.temporal == "DATE")>
                    <p:datePicker id="${attribute.name}" value="${hash}{${EntityController}.selected.${attribute.name}}" pattern="MM/dd/yyyy"/>
                        <#elseif (attribute.temporal == "TIME")>
                    <p:datePicker id="${attribute.name}" value="${hash}{${EntityController}.selected.${attribute.name}}" timeOnly="true" showSeconds="true" pattern="HH:mm:ss"/>
                        <#elseif (attribute.temporal == "TIMESTAMP")>
                    <p:datePicker id="${attribute.name}" value="${hash}{${EntityController}.selected.${attribute.name}}" showTime="true" showSeconds="true" pattern="MM/dd/yyyy HH:mm:ss"/>
                        </#if>
                    <p:messages for="${attribute.name}">
                        <p:autoUpdate />
                    </p:messages>
                </#if>
                </#if>
                </#list>
                    <p:commandButton  value="Create" action="${hash}{${EntityController}.create}">
                    </p:commandButton>
                    <p:commandButton value="Cancel" action="${hash}{${EntityController}.prepareList}" immediate="true">
                    </p:commandButton>
                </h:panelGrid>
            </h:form>  
        </ui:define>
    </ui:composition>
</html>
