<!DOCTYPE html>
<%
    String dataPoints = (String)request.getAttribute("dataPoints");
    System.out.println("[google_chart_linea.jsp]dataPoints: "+dataPoints);
%>
 
<!DOCTYPE HTML>
  <html>
  <head>
    <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
    <script type="text/javascript">
      google.charts.load('current', {'packages':['corechart']});
      google.charts.setOnLoadCallback(drawChart);

      function drawChart() {
        /*
		var data = google.visualization.arrayToDataTable([
          ['Hora', 'Entradas', 'Salidas'],
          ['08:00:00',  1000,      400],
          ['09:00:00',  1170,      460],
          ['10:00:00',  660,       1120],
          ['11:00:00',  1030,      540]
        ]);
		*/
		var data = google.visualization.arrayToDataTable(<%=dataPoints%>);
		
	//var data = new google.visualization.DataTable(<%=dataPoints%>);
		
        var options = {
          title: 'Company Performance',
          curveType: 'function',
          legend: { position: 'bottom' }
        };

        var chart = new google.visualization.LineChart(document.getElementById('curve_chart'));

        chart.draw(data, options);
      }
    </script>
  </head>
  <body>
    <div id="curve_chart" style="width: 900px; height: 500px"></div>
  </body>
</html>