<?xml version='1.0' encoding='UTF-8' ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:ui="http://java.sun.com/jsf/facelets">

    <h:head>
        <title>Facelet Title</title>
        <h:outputStylesheet name="css/jsfcrud.css"/>
    </h:head>
    <h:body >
        <#list Entities as Entity>
            <h:link outcome="app/entities/${Entity?uncap_first}/list${Entity}" value="Show ${Entity} Items Latest"/>
            <br/>
        </#list> 
    </h:body>
</html>

