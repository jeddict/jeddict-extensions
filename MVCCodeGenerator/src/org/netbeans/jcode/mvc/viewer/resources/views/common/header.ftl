 <#--  Copyright [2016] Gaurav Gupta
 
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy of
  the License at
 
  http://www.apache.org/licenses/LICENSE-2.0
 
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  License for the specific language governing permissions and limitations under
  the License.
  -->
<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="en">

    <head>

        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta name="description" content="">
        <meta name="author" content="">

        <title></title>
        
        <c:set var="appPath" value="${r"${mvc.contextPath}"}/${applicationPath}"/>
        <c:set var="webPath" value="${r"${mvc.contextPath}"}/${webPath}"/>

        <#if online>
        <!-- Bootstrap Core CSS -->
        <link href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.6/css/bootstrap.min.css" rel="stylesheet">
        <!-- MetisMenu CSS -->
        <link href="https://cdnjs.cloudflare.com/ajax/libs/metisMenu/1.1.3/metisMenu.min.css" rel="stylesheet">
        <!-- DataTables CSS -->
        <link href="https://cdnjs.cloudflare.com/ajax/libs/datatables/1.10.10/css/dataTables.bootstrap.min.css" rel="stylesheet">
        <!-- DataTables Responsive CSS -->
        <!--<link href="../bower_components/datatables-responsive/css/dataTables.responsive.css" rel="stylesheet">-->
        <!-- Custom Fonts -->
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.2.0/css/font-awesome.min.css" rel="stylesheet" type="text/css">
        <#else>
        <!-- Bootstrap Core CSS -->
        <link href="${r"${webPath}"}/static/style/bootstrap.min.css" rel="stylesheet">
        <!-- MetisMenu CSS -->
        <link href="${r"${webPath}"}/static/style/metisMenu.min.css" rel="stylesheet">
        <!-- DataTables CSS -->
        <link href="${r"${webPath}"}/static/style/dataTables.bootstrap.css" rel="stylesheet">
        <!-- DataTables Responsive CSS -->
        <!--<link href="../bower_components/datatables-responsive/css/dataTables.responsive.css" rel="stylesheet">-->
        <!-- Custom Fonts -->
        <link href="${r"${webPath}"}/static/style/font-awesome.css" rel="stylesheet" type="text/css">
        </#if>
        <!-- Custom CSS -->
        <link href="${r"${webPath}"}/static/style/theme.css" rel="stylesheet">

        <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
        <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
        <!--[if lt IE 9]>
            <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
            <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
        <![endif]-->

        <#if online>
        <!-- jQuery -->
        <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
        <#else>
        <!-- jQuery -->
        <script src="${r"${webPath}"}/static/script/jquery.min.js"></script>
        </#if>

    </head>

    <body>
        <div id="wrapper">
            <%@ include file="navigationbar.jspf"%>
