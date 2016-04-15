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
<!-- Bootstrap Core JavaScript -->
<script src="${r"${webPath}"}/static/script/bootstrap.min.js"></script>

<!-- Metis Menu Plugin JavaScript -->
<script src="${r"${webPath}"}/static/script/metisMenu.min.js"></script>

<!-- DataTables JavaScript -->
<script src="${r"${webPath}"}/static/script/jquery.dataTables.min.js"></script>
<script src="${r"${webPath}"}/static/script/dataTables.bootstrap.min.js"></script>

<!-- Custom Theme JavaScript -->
<script src="${r"${webPath}"}/static/script/theme.js"></script>

<!-- Page-Level Demo Scripts - Tables - Use for reference -->
<script>
    $(document).ready(function () {
        $('#dataTables-example').DataTable({
            responsive: true
        });
    });
</script>

</body>

</html>
