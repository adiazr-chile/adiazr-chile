<!DOCTYPE html>
    <html>
    <head>
        <title>Google Chart (Horas atraso por mes)</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
        <script type="text/javascript" src="https://www.google.com/jsapi"></script>
        <script type="text/javascript">
            var queryObject="";
            var queryObjectLen="";
            $.ajax({
                type : 'POST',
                url : '<%=request.getContextPath()%>/servlet/LoadGraficoAtrasosHorasExtras?action=mes',
                dataType:'json',
                success : function(data) {
                    queryObject = eval('(' + JSON.stringify(data) + ')');
                    queryObjectLen = queryObject.empdetails.length;
                },
                    error : function(xhr, type) {
                    alert('server error occoured')
                }
            });
            google.load("visualization", "1", {packages:["corechart"]});
            google.setOnLoadCallback(drawChart);
            function drawChart() {
                var data = new google.visualization.DataTable();
                data.addColumn('string', 'Mes');
                data.addColumn('number', 'Horas atraso');
                
                for(var i=0;i<queryObjectLen;i++)
                {
                    var aniomes = queryObject.empdetails[i].aniomes;
                    var horas_atraso = queryObject.empdetails[i].horas_atraso;
                    data.addRows([
                        [aniomes,parseFloat(horas_atraso)]
                    ]);
                }
                var formatter = new google.visualization.DateFormat({pattern: 'HH:mm'});
                formatter.format(data, 1);
                var options = {
                    title: 'Horas de atraso (ultimos 12 meses)',
                    hAxis: {title: 'Hora', titleTextStyle: {color: 'blue'}},
                    'is3D':true,
                    format: 'HH:mm',
                     legend: { position: 'bottom' },
                     backgroundColor: '#65B1B1',
                     colors: ['#003366']
                };
                var chart = new google.visualization.ColumnChart(document.getElementById('chart_div'));
                
                chart.draw(data,options);
            }
            
            $(window).resize(function(){
                drawChart();
            });
        </script>
        <style>
            .chart {
                width: 100%;
                min-height: 450px;
            }

            .row {
                margin: 0 !important;
            }
        </style>
        </head>
        <body onload="drawChart();">
              <div id="chart_div" class="chart"></div>
         </body>
        </html>
