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
<#if online>
<!-- Bootstrap Core JavaScript -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.6/js/bootstrap.min.js"></script>

<!-- Metis Menu Plugin JavaScript -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/metisMenu/1.1.3/metisMenu.min.js"></script>

<!-- DataTables JavaScript -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/datatables/1.10.10/js/jquery.dataTables.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/datatables/1.10.10/js/dataTables.bootstrap.min.js"></script>
<#else>
<!-- Bootstrap Core JavaScript -->
<script src="${r"${webPath}"}/static/script/bootstrap.min.js"></script>

<!-- Metis Menu Plugin JavaScript -->
<script src="${r"${webPath}"}/static/script/metisMenu.min.js"></script>

<!-- DataTables JavaScript -->
<script src="${r"${webPath}"}/static/script/jquery.dataTables.min.js"></script>
<script src="${r"${webPath}"}/static/script/dataTables.bootstrap.min.js"></script>
</#if>
<!-- Custom Theme JavaScript -->
<script src="${r"${webPath}"}/static/script/theme.js"></script>

</body>

</html>
