<!DOCTYPE html>
    <html>
    <head>
        <title>Google Chart (Ausencias del mes)</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
        <script type="text/javascript" src="https://www.google.com/jsapi"></script>
        <script type="text/javascript">
            var queryObject="";
            var queryObjectLen="";
            var chart_data;
            var paction = "torta";
            var paniomes = "2019-04";
            var pcencoId = "66";
            google.load("visualization", "1", {packages:["corechart"]});
            google.setOnLoadCallback(load_page_data);

            function load_page_data(){
                $.ajax({
                    url: '<%=request.getContextPath()%>/servlet/LoadGraficoAusencias?action=torta',
                    data: {'action':paction,'aniomes':paniomes,'cencoId':pcencoId},
                    async: false,
                    success: function(data){
                        if(data){
                            chart_data = $.parseJSON(data);
                            drawChart(chart_data, "My Chart", "Data");
                        }
                    },
                });
            }

            function drawChart(chart_data, chart1_main_title, chart1_vaxis_title) {
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
