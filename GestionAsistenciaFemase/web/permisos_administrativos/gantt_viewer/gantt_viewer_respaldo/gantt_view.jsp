<%
	String jsonData     = (String)request.getAttribute("jsonData");
	String cencoId    = (String)request.getAttribute("cencoId");
	String paramAnio	= (String)request.getAttribute("paramAnio");
	String paramMes	= (String)request.getAttribute("paramMes");

	
%>

<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>Gantt</title>
        <meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
        <meta content="Marek Bielanczuk" name="Author" />
		<script src="gantt_chart/js/jquery-1.5.1.min.js" type="text/javascript"></script>
		<script src="gantt_chart/js/jquery.fn.gantt.min.js" type="text/javascript"></script>
        <link rel="stylesheet" href="gantt_chart/style.css" type="text/css" media="screen" />
		<script type="text/javascript">
        <!--
	        jQuery(function () {
	        	//var dataPath = location.href.substring(0, location.href.lastIndexOf('/')+1) + "js/data.js";
	        	$(".gantt").gantt({source: "<%=request.getContextPath()%>/servlet/GanttVacaciones?action=loadJsonData&cencoId=<%=cencoId%>&paramAnio=<%=paramAnio%>&paramMes=<%=paramMes%>"})
	        });
        --> 

        </script>
    </head>
    <body>
    	<div class="main-header" style="position: relative">
      	    <h2>Calendario vacaciones</h2>
		</div><!-- fin <div class="main-header"> -->
    	<div class="gantt" />More script and css style
: <a href="http://www.htmldrive.net/" title="HTML DRIVE - Free DHMTL Scripts,Jquery 

plugins,Javascript,CSS,CSS3,Html5 Library">www.htmldrive.net </a>
    </body>
</html>