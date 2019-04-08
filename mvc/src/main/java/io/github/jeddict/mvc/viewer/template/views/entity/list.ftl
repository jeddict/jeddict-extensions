 <#--  Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
 
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
<%@ include file="/${resourcePath}/common/header.jspf"%>

<div id="wrapper">
<%@ include file="/${resourcePath}/common/navigationbar.jspf"%>
<div id="page-wrapper">
    <div class="row">
        <div class="col-lg-12">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <div class="row">
                        <div class="col-lg-6"><h5><i class="fa fa-database fa-fw"></i> <b> ${entityLabel} List</b></h5></div>
                        <div class="col-lg-6">
                            <div align="right">
                                <a class="btn btn-primary btn-sm" href="new"><i class="fa fa-plus"></i> Add</a>
                                <c:if test="${r"${not empty requestScope."}${entityConstant}${r"_LIST}"}">
                                    <button class="btn btn-default btn-sm" onclick="javascript:window.print()">
                                        <i class="fa fa-print fa-fw"></i> Print ${entityLabel} list
                                        </button>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </div>
                    <!-- /.panel-heading -->
                <div class="panel-body">
                    <div class="dataTable_wrapper">
                        <table class="table table-striped table-bordered table-hover" id="${entityConstant}_TABLE">
                            <thead>
                                <tr>
                                             <#list entityDescriptors as entityDescriptor>
                                                <#if !entityDescriptor.generatedValue &&
                                                     !entityDescriptor.versionField &&
                                                     !entityDescriptor.readOnly &&
                                                     entityDescriptor.returnType != "invalid" &&
                                                     !(entityDescriptor.relationshipMany && entityDescriptor.relationshipOne)> 
                                    <th>${entityDescriptor.label}</th>
                                                </#if>
                                            </#list>
                                    <th></th>
                                    </tr>
                                </thead>
                            <tbody>
                                                <#-- ${personList} var="person"   -->
                            <c:forEach items="${r"${"}${entityConstant}${r"_LIST}"}" var="${entityConstant}">
                                <tr>
                                                        <#list entityDescriptors as entityDescriptor>
                                                            <#if !entityDescriptor.generatedValue &&
                                                                 !entityDescriptor.versionField &&
                                                                 !entityDescriptor.readOnly &&
                                                                 entityDescriptor.returnType != "invalid" &&
                                                                 !(entityDescriptor.relationshipMany && entityDescriptor.relationshipOne)> 
                                                                                    <#-- ${r"${"}${primaryKey}${r"}"}   -->
                                                                        <#if XSSPrevention>
                                                                        <td>${r"${mvc.encoders.html("}${entityConstant}.${entityDescriptor.propertyName}${r")}"}</td>
                                                                        <#else>
                                                                        <td>${r"${"}${entityConstant}.${entityDescriptor.propertyName}${r"}"}</td>
                                                                        </#if>
                                                                     
                                                            </#if>
                                                            <#if entityDescriptor.primaryKey>
                                                                 <#assign primaryKey = entityConstant + "." +entityDescriptor.propertyName >
                                                            </#if>
                                                        </#list>
                                    <td>
                                        <div class="pull-right">
                                            <div class="btn-group">
                                                <button type="button" class="btn btn-primary btn-xs dropdown-toggle" data-toggle="dropdown">
                                                    <i class="fa fa-gear"></i>  <span class="caret"></span>
                                                    </button>
                                                <ul class="dropdown-menu pull-right" role="menu">
                                                    <li><a href="${r"${"}${primaryKey}${r"}"}"><i class="fa fa-level-up fa-fw"></i>  View</a></li>
                                                    <li><a href="${r"${appPath}"}/${entityName}/update/${r"${"}${primaryKey}${r"}"}"><i class="fa fa-edit fa-fw"></i>  Edit</a></li>
                                                    <li class="divider"></li>
                                                    <li><a data-toggle="modal" data-target="#confirm_delete_${r"${"}${primaryKey}${r"}"}" href="#"  ><i class="fa fa-trash-o fa-fw"></i> Delete</a>
                                                        </li>
                                                    </ul>
                                                </div>
                                            </div>
                                            <!-- Modal -->
                                        <div class="modal fade" id="confirm_delete_${r"${"}${primaryKey}${r"}"}" tabindex="-1" role="dialog" aria-hidden="true">
                                            <div class="modal-dialog">
                                                <div class="modal-content">
                                                    <div class="modal-header">
                                                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                                                        <h4 class="modal-title">Confirmation</h4>
                                                        </div>
                                                    <div class="modal-body">
                                                        <p>Are you sure to delete ${entityLabel} ?</p>
                                                        </div>
                                                    <div class="modal-footer">
                                                        <form action="${r"${appPath}"}/${entityName}/remove/${r"${"}${primaryKey}${r"}"}" method="DELETE">
                                                            <a href="#" class="btn" data-dismiss="modal">Cancel</a> <button type="submit" class="btn btn-primary">Confirm</button>
                                                            </form>
                                                        </div>
                                                    </div>
                                                    <!-- /.modal-content -->
                                                </div>
                                                <!-- /.modal-dialog -->
                                            </div>
                                            <!-- /.modal -->
                                        </td>

                                    </tr>
                                </c:forEach>
                            </tbody>
                            </table>
                        </div>


                    <c:if test="${r"${empty requestScope."}${entityConstant}${r"_LIST}"}">
                        <div class="alert alert-info">
                            <div align="center">No ${entityLabel} found</div>
                            </div>
                        </c:if>



                    </div>
                    <!-- /.panel-body -->
                </div>
                <!-- /.panel -->
            </div>
            <!-- /.col-lg-12 -->
        </div>
       <!-- /.row -->
    </div>
</div>
<script>
    $(document).ready(function () {
        $('${entityConstant}_TABLE').DataTable({
            responsive: true
        });
    });
</script>

<%--end content--%>
<%@ include file="/${resourcePath}/common/footer.jspf"%>
