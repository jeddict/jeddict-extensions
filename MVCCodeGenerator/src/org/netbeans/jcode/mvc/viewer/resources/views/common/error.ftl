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
<%@ include file="header.jspf"%>

<div id="page-wrapper">
    <div class="row">
        <div class="col-md-8 col-md-offset-2">
            <div class="login-panel panel panel-red">
                <div class="panel-heading">
                    <h3 class="panel-title">Error :</h3>
                </div>
                <div class="panel-body">
                    <div class="list-group">
                        <a href="#" class="list-group-item">
                            <div class="row">
                                <div class="col-lg-4"><span class="small">Property :</span></div>
                                <div class="col-lg-8">${errorBean.property}</div>
                            </div>
                        </a>  

                        <a href="#" class="list-group-item">
                            <div class="row">
                                <div class="col-lg-4"><span class="small">Value :</span></div>
                                <div class="col-lg-8">${errorBean.value}</div>
                            </div>
                        </a>  

                        <a href="#" class="list-group-item">
                            <div class="row">
                                <div class="col-lg-4"><span class="small">Message :</span></div>
                                <div class="col-lg-8">${errorBean.message}</div>
                            </div>
                        </a>  
                    </div>
                </div>
                <div class="panel-footer">
                    <a href="index.html" class="btn btn-lg btn-defult btn-block">Back</a>
                </div>
            </div>
        </div>
    </div>
</div>


<%@ include file="footer.jspf"%>
