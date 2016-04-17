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
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../common/header.jspf"%>
<div id="page-wrapper">
    <div class="row">
        <div class="col-lg-12">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h5><i class="fa fa-plus-square fa-fw"></i> <b> Create a new ${entityLabel} </b></h5>
                    </div>
                <div class="panel-body">
                    <div class="row">
                        <div class="col-lg-12">
                            <form role="form" action="${r"${appPath}"}/${entityName}/new" method="POST">
                                          <#list entityDescriptors as entityDescriptor>
                                            <#-- Skip this field if it is an identity field that has an auto-generated value       -->
                                            <#-- Skip this field if we are dealing with many:many and this entity is not the owner -->
                                            <#if !entityDescriptor.generatedValue &&
                                                 !entityDescriptor.versionField &&
                                                 !entityDescriptor.readOnly &&
                                                 entityDescriptor.returnType != "invalid" &&
                                                 !(entityDescriptor.relationshipMany && entityDescriptor.relationshipOne)> 
                                                <#if entityDescriptor.returnType == "checkbox" >
                                <div class="checkbox form-group">
                                    <label for="${entityDescriptor.propertyName}">
                                        <input type="${entityDescriptor.returnType}" name="${entityDescriptor.propertyName}" />
                                                    ${entityDescriptor.label}</label>
                                    </div>
                                                <#else>
                                <div class="form-group">
                                    <label for="${entityDescriptor.propertyName}">${entityDescriptor.label}</label>
                                    <input class="form-control" type="${entityDescriptor.returnType}" name="${entityDescriptor.propertyName}" path="${entityDescriptor.propertyName}" <#if entityDescriptor.primaryKey> required="required" autofocus="autofocus" </#if> />
                                    </div>
                                                 </#if>
                                            </#if>
                                        </#list>
                                <#if CSRFPrevention>
                                <input type="hidden" name="${r"${mvc.csrf.name}"}" value="${r"${mvc.csrf.token}"}"/>
                                </#if>
                                <button type="submit" class="btn btn-primary"><i class="fa fa-check fa-fw"></i>Submit</button>
                                <a href="${r"${appPath}"}/${entityName}/list" class="btn btn-default"><i class="fa fa-close fa-fw"></i>Cancel</a>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<%--end content--%>
<%@ include file="../common/footer.jspf"%>
