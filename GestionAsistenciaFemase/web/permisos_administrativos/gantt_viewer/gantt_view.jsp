<%
    String jsonData     = (String)request.getAttribute("jsonData");
    String empresaName  = (String)request.getAttribute("empresaName");
    String deptoName    = (String)request.getAttribute("deptoName");
    String cencoName    = (String)request.getAttribute("cencoName");
    String anioMes	= (String)request.getAttribute("anioMes");
    String labelAnioMes	= (String)request.getAttribute("labelAnioMes");

    String labelFiltro = "&nbsp;";
    if (empresaName != null){
        labelFiltro = empresaName + "/" + deptoName + "/" + cencoName + "  ("+labelAnioMes+")";
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<title>Planificacion Permisos Administrativos</title>
	<style>
		body {
                    font-family: sans-serif;
                    background: #f5f5f5;
		}
		.container {
                    width: 80%;
                    margin: 0 auto;
                    overflow-y: scroll;
                    height: 500px;
		}
		/* custom class */
		.gantt .bar-milestone .bar {
                    fill: tomato;
		}
	</style>
	<link rel="stylesheet" href="<%=request.getContextPath()%>/permisos_administrativos/gantt_viewer/frappe_gantt_lib/frappe-gantt.css" />
	<script src="<%=request.getContextPath()%>/permisos_administrativos/gantt_viewer/frappe_gantt_lib/frappe-gantt.js"></script>
</head>
<body>
	<div class="container">
		<h2><%=labelFiltro%></h2>
		<div class="gantt-target"></div>
	</div>
	<script>
		var tasks = <%=jsonData%>;
		var gantt_chart = new Gantt(".gantt-target", tasks, {
			on_click: function (task) {
				alert('id: ' + task.id + ', name: '+task.name);
				//console.log(task);
			},
			on_date_change: function(task, start, end) {
				console.log(task, start, end);
			},
			on_progress_change: function(task, progress) {
				console.log(task, progress);
			},
			on_view_change: function(mode) {
				console.log(mode);
			},
			language: 'es',
			step: 24,
			header_height: 50,
			column_width: 30,
			view_modes: ['Quarter Day', 'Half Day', 'Day', 'Week', 'Month'],
			bar_height: 20,
			bar_corner_radius: 3,
			arrow_curve: 5,
			padding: 5,
			view_mode: 'Day',   
			date_format: 'YYYY-MM-DD',
			custom_popup_html: null
		});
		
		//console.log(gantt_chart);
	</script>
</body>
</html>