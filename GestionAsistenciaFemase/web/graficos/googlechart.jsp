<!DOCTYPE html>
    <html>
    <head>
        <title>Google Chart with jsp Java Json</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
        <script type="text/javascript" src="https://www.google.com/jsapi"></script>
        <script type="text/javascript">
            var queryObject="";
            var queryObjectLen="";
            $.ajax({
                type : 'POST',
                url : '<%=request.getContextPath()%>/servlet/LoadGrafico',
                dataType:'json',
                success : function(data) {
                    queryObject = eval('(' + JSON.stringify(data) + ')');
                    queryObjectLen = queryObject.empdetails.length;
                    alert('jsonObj: ' + queryObjectLen);
                },
                error : function(xhr, type) {
                    alert('server error occoured')
                }
            });
            google.load("visualization", "1", {packages:["corechart"]});
            google.setOnLoadCallback(drawChart);
            function drawChart() {
                var data = new google.visualization.DataTable();
                data.addColumn('string', 'Hora');
                data.addColumn('number', 'Num Marcas');
                
                for(var i=0;i<queryObjectLen;i++)
                {
                    var fechahora = queryObject.empdetails[i].fechahora;
                    var num_marcas = queryObject.empdetails[i].num_marcas;
                    alert('fechahora: ' + fechahora+', num_marcas: '+num_marcas);
                    data.addRows([
                        [fechahora,parseInt(num_marcas)]
                    ]);
                }
                var formatter = new google.visualization.DateFormat({pattern: 'HH:mm'});
                formatter.format(data, 1);
                var options = {
                    title: 'Info Empl',
                    'is3D':true,
                    format: 'HH:mm',
                    'width':798,
                     'height':398,
                     legend: { position: 'bottom' },
                     backgroundColor: '#7ED3AB'
                };
                var chart = new google.visualization.LineChart(document.getElementById('chart_div'));
                
                chart.draw(data,options);
            }
        </script>
        </head>
        <body>
              <div id="chart_div" style="width:400; height:300"></div>
         </body>
        </html>
