<!DOCTYPE html>
    <html>
    <head>
        <title>Google Chart (Ausencias del mes)</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script type="text/javascript" src="jquery.min_1_10_2.js"></script>
        <script type="text/javascript" src="loader.js"></script>
        <script type="text/javascript">
            var queryObject="";
            var queryObjectLen="";
            $.ajax({
                type : 'POST',
                url : '<%=request.getContextPath()%>/servlet/LoadGraficoAusencias?action=torta',
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
                data.addColumn('string', 'Ausencia');
                data.addColumn('number', 'Num ausencias');
                
                for(var i=0;i<queryObjectLen;i++)
                {
                    var ausencia = queryObject.empdetails[i].ausencia;
                    var total_ausencias = queryObject.empdetails[i].total_ausencias;
                    data.addRows([
                        [ausencia,parseFloat(total_ausencias)]
                    ]);
                }
                var formatter = new google.visualization.DateFormat({pattern: 'HH:mm'});
                formatter.format(data, 1);
                var options = {
                    title: 'Ausencias del mes en curso',
                    is3D: true
                    //format: 'HH:mm',
                    // legend: { position: 'bottom' },
                    // backgroundColor: '#65B1B1',
                    // colors: ['#003366']
                };
                var chart = new google.visualization.PieChart(document.getElementById('chart_div'));
                
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
