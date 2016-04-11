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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="common/header.jspf"%>

<!-- Timeline CSS -->
<link href="${r"${webPath}"}/static/style/timeline.css" rel="stylesheet">
<!-- Morris Charts CSS -->
<link href="${r"${webPath}"}/static/style/morris.css" rel="stylesheet">


<div id="page-wrapper">
     <div class="row">
        <#assign keys = entities?keys>
        <#list keys as key> 
        <div class="col-lg-3 col-md-6">
            <div class="panel panel-primary">
                <div class="panel-heading">
                    <div class="row">
                        <div class="col-xs-3">
                            <div>${entities[key]}</div>
                            </div>
                        <div class="col-xs-9 text-right">
                            <div class="huge">${r"${"}${key}${r".count()}"}</div>
                            </div>
                        </div>
                    </div>
                <a href="${r"${appPath}"}/${key}/list">
                    <div class="panel-footer">
                        <span class="pull-left">View Details</span>
                        <span class="pull-right"><i class="fa fa-arrow-circle-right"></i></span>
                        <div class="clearfix"></div>
                        </div>
                    </a>
                </div>
            </div>                               
           </#list>
        </div>

    <div class="row">
        <div class="col-lg-12">
            <div class="panel panel-default">
                <div class="panel-body">
                    <div id="morris-donut-chart"></div>
                    <div style="display: none" id="morris-area-chart"></div>
                    </div>
                    <!-- /.panel-body -->
                </div>
            </div>
        </div>
    </div>

<!-- Morris Charts JavaScript -->
<script src="${r"${webPath}"}/static/script/raphael-min.js"></script>
<script src="${r"${webPath}"}/static/script/morris.min.js"></script>
<script src="${r"${webPath}"}/static/script/morris-data.js"></script>


<%@ include file="common/footer.jspf"%>
