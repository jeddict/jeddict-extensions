<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:ui="http://xmlns.jcp.org/jsf/facelets"
      xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:f="http://xmlns.jcp.org/jsf/core"
      xmlns:p="http://primefaces.org/ui">
    <#macro test fieldAttribute parentAttribute ="">
            <#if fieldAttribute.attributeType??>
            	<#if (parentAttribute.getClass().getSimpleName()) == "Id">
                    <#assign attributeName = fieldAttribute.name>
                <#elseif (parentAttribute.getClass().getSimpleName()) == "Basic">
                    <#assign attributeName = fieldAttribute.name>
                <#elseif (parentAttribute.getClass().getSimpleName()) == "Embedded">
                    <#assign attributeName = parentAttribute.name + "." + fieldAttribute.name>
                </#if>
                <#if (fieldAttribute.attributeType == "String")|| 
                    (fieldAttribute.attributeType == "long") || (fieldAttribute.attributeType == "Long") ||
                    (fieldAttribute.attributeType == "int") || (fieldAttribute.attributeType == "Integer")||
                    (fieldAttribute.attributeType == "char") || (fieldAttribute.attributeType == "Character")||
                    (fieldAttribute.attributeType == "byte") || (fieldAttribute.attributeType == "Byte")||
                    (fieldAttribute.attributeType == "float") || (fieldAttribute.attributeType == "Float")||
                    (fieldAttribute.attributeType == "double") || (fieldAttribute.attributeType == "Double")||
                    (fieldAttribute.attributeType == "short") || (fieldAttribute.attributeType == "Short")||
                    (fieldAttribute.attributeType == "java.math.BigInteger") || (fieldAttribute.attributeType == "java.math.BigDecimal")>
                    <p:outputLabel for="${fieldAttribute.name}" value="${fieldAttribute.name}" />
                    <p:inputText id="${fieldAttribute.name}" value="${hash}{${EntityController}.selected.${attributeName}}">
                    </p:inputText>  
                    <p:messages for="${fieldAttribute.name}">                        
                        <p:autoUpdate />                    
                    </p:messages>

                    <#elseif (fieldAttribute.attributeType == "boolean") || (fieldAttribute.attributeType == "Boolean")  >
                    <p:outputLabel for="${fieldAttribute.name}" value="${fieldAttribute.name}" />
                    <p:selectBooleanCheckbox value="${hash}{${EntityController}.selected.${attributeName}}" id="${fieldAttribute.name}"/>
                    
                    <#elseif (fieldAttribute.attributeType == "java.time.LocalDateTime")>
                    <p:outputLabel for="${fieldAttribute.name}" value="${fieldAttribute.name}" />
                    <p:datePicker id="${fieldAttribute.name}" value="${hash}{${EntityController}.selected.${attributeName}}" showTime="true" showSeconds="true" pattern="MM/dd/yyyy HH:mm:ss"/>
                    <p:messages for="${fieldAttribute.name}">
                        <p:autoUpdate />
                    </p:messages>
                    
                    <#elseif (fieldAttribute.attributeType == "java.time.LocalDate")  >
                    <p:outputLabel for="${fieldAttribute.name}" value="${fieldAttribute.name}" />
                    <p:datePicker id="${fieldAttribute.name}" value="${hash}{${EntityController}.selected.${attributeName}}"/>
                    <p:messages for="${fieldAttribute.name}">
                        <p:autoUpdate />
                    </p:messages>
                    
                    <#elseif (fieldAttribute.attributeType == "java.time.LocalTime")  >
                    <p:outputLabel for="${fieldAttribute.name}" value="${fieldAttribute.name}" />
                    <p:datePicker id="${fieldAttribute.name}" value="${hash}{${EntityController}.selected.${attributeName}}" timeOnly="true" showSeconds="true" pattern="HH:mm:ss"/>
                    <p:messages for="${fieldAttribute.name}">
                        <p:autoUpdate />
                    </p:messages>  
                    
                    <#elseif (fieldAttribute.attributeType == "java.util.Date")>
                    <p:outputLabel for="${fieldAttribute.name}" value="${fieldAttribute.name}" />
                        <#if (fieldAttribute.temporal == "DATE")>
                    <p:datePicker id="${fieldAttribute.name}" value="${hash}{${EntityController}.selected.${attributeName}}" pattern="MM/dd/yyyy"/>
                        <#elseif (fieldAttribute.temporal == "TIME")>
                    <p:datePicker id="${fieldAttribute.name}" value="${hash}{${EntityController}.selected.${attributeName}}" timeOnly="true" showSeconds="true" pattern="HH:mm:ss"/>
                        <#elseif (fieldAttribute.temporal == "TIMESTAMP")>
                    <p:datePicker id="${fieldAttribute.name}" value="${hash}{${EntityController}.selected.${attributeName}}" showTime="true" showSeconds="true" pattern="MM/dd/yyyy HH:mm:ss"/>
                        </#if>
                    <p:messages for="${fieldAttribute.name}">
                        <p:autoUpdate />
                    </p:messages>
                    
                    <#elseif (fieldAttribute.enumerated == "STRING") || (fieldAttribute.enumerated == "ORDINAL") || (fieldAttribute.enumerated == "DEFAULT")>
                    <p:outputLabel for="${fieldAttribute.name}" value="${fieldAttribute.name}" />
                    <h:selectOneMenu id="${fieldAttribute.name}" value="${hash}{${EntityController}.selected.${attributeName}}" >
                        <f:selectItems value="${hash}{${EntityController}.${attributeName}}" />
                    </h:selectOneMenu>
                    <p:messages for="${fieldAttribute.name}">
                        <p:autoUpdate />
                    </p:messages>
                   
                </#if>
            </#if>
    </#macro>

    <ui:composition template="/template.xhtml">

        <ui:define name="title">
            <h:outputText value="${pageType} ${Entity}"></h:outputText>
        </ui:define>

        <ui:define name="header" >
        </ui:define>

        <ui:define name="body" >
            <div>
                <h1 align="center">${pageType} ${Entity} </h1>
            </div>
            <h:form id="add" style="margin-left: 20%;margin-right: 20%">
                <h:panelGrid columns="3" cellpadding="5" style="margin: 0 auto;">
                <#list attributes as attribute>
                    <#if (attribute.getClass().getSimpleName()) == "Id">
                    <#if pageType == "Create">
                        <#if (attribute.generatedValue == false)>
                        <@test fieldAttribute=attribute parentAttribute=attribute/>
                        </#if>
                    <#elseif pageType == "Update">
                    <h:inputHidden id="${attribute.name}" value="${hash}{${EntityController}.selected.${attribute.name}}" required="true" >
                    </h:inputHidden>
                    </#if>
                    </#if>
                  
                    <#if (attribute.getClass().getSimpleName()) == "Basic">
                      <@test fieldAttribute=attribute parentAttribute=attribute/>
                    </#if>
                  
                    <#if (attribute.getClass().getSimpleName()) == "Embedded">
                    <#list embeddables as embeddable>
                    <#if attribute.name?matches(embeddable.name, "i")>
                     <#list embeddable.getAttributes().getAllAttribute() as emAttribute>
                      <#if emAttribute.attributeType??>
                      <@test fieldAttribute=emAttribute parentAttribute=attribute/>
                      </#if>
                     </#list>
                    </#if>
                    </#list>
                    </#if>
                </#list>
                    <p:commandButton  value="${pageType}" action="${hash}{${EntityController}.${pageType?uncap_first}}">
                    </p:commandButton>
                    <p:commandButton value="Cancel" action="${hash}{${EntityController}.prepareList}" immediate="true">
                    </p:commandButton>
                </h:panelGrid>
            </h:form>  
        </ui:define>
    </ui:composition>
</html>
