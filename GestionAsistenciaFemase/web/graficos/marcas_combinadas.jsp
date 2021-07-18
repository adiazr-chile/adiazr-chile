<!DOCTYPE html>
    <html>
    <head>
        <title>Google Chart (Marcas entrada/salida)</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
        <script type="text/javascript" src="https://www.google.com/jsapi"></script>
        <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script> 
        
        <script type="text/javascript">
            var queryObject="";
            var queryObjectLen="";
            $.ajax({
                type : 'POST',
                url : '<%=request.getContextPath()%>/servlet/LoadGraficoMarcasCombinadas',
                dataType:'json',
                success : function(data) {
                    queryObject = eval('(' + JSON.stringify(data) + ')');
                    queryObjectLen = queryObject.empdetails.length;
                },
                    error : function(xhr, type) {
                    alert('server error occoured');
                }
            });
            google.load("visualization", "1", {packages:["corechart"]});
            setTimeout(function () {
                google.setOnLoadCallback(drawChart());
            }, 5000);
            function drawChart() {
                //alert('[marcas combinadas]Dibujar grafico...');
                var data = new google.visualization.DataTable();
                data.addColumn('string', 'Hora');
                data.addColumn('number', 'Entradas');
                data.addColumn('number', 'Salidas');
                
                for(var i=0;i<queryObjectLen;i++)
                {
                    var fechahora = queryObject.empdetails[i].fechahora;
                    var entradas = queryObject.empdetails[i].entradas;
                    var salidas = queryObject.empdetails[i].salidas;
                    //alert('fechaHora: '+fechahora
                    //    +', num marcas: '+num_marcas);
                    data.addRows([
                        [fechahora,parseInt(entradas),parseInt(salidas)]
                    ]);
                }
                var formatter = new google.visualization.DateFormat({pattern: 'HH:mm'});
                formatter.format(data, 1);
                var options = {
                    title: 'Marcaciones de entrada/salida',
                    'is3D':true,
                    format: 'HH:mm',
                    hAxis: {title: 'Hora', titleTextStyle: {color: 'blue'}},
                    legend: { position: 'bottom' },
                    backgroundColor: '#65B1B1',
                    colors: ['#003366','#FF0000']
                };
                var chart = new google.visualization.LineChart(document.getElementById('chart_div'));
                
                chart.draw(data,options);
                //alert('[marcas combinadas]Terminando de Dibujar grafico...');
            }
            /*
            $(window).resize(function(){
                drawChart();
            });
            */
            //drawChart();
        </script>
        
        <style>
            .chart {
                width: 80%;
                min-height: 450px;
            }

            .row {
                margin: 0 !important;
            }
        </style>
        
        </head>
        <body>
              <div id="chart_div" class="chart"></div>
         </body>
        </html>
