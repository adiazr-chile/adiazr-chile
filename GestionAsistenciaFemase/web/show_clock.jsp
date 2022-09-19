<%@page import="cl.femase.gestionweb.common.Utilidades"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.Calendar"%>
<%
    Calendar calHoy = Calendar.getInstance(new Locale("es","CL"));
    Date fechaActual = calHoy.getTime();
    System.out.println("[GestionFemaseWeb]ShowClock]"
        + "fechaHoraActual: " + fechaActual);
    long timeInServer = fechaActual.getTime();
    String fecha = Utilidades.getDatePartAsString(new Date(), "yyyy-MM-dd");
    System.out.println("[GestionFemaseWeb]ShowClock]"
        + "solo fecha Actual: " + fecha);
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>(FEMASE)Reloj </title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    	
    <script>
        var serverTime  = <%=timeInServer%>;
        var expected = serverTime;
        var date;
        var hours;
        var minutes;
        var seconds;
        var now = performance.now();
        var then = now;
        var dt = 0;
        var nextInterval = interval = 1000; // ms

        setTimeout(step, interval);
        function step() {
            then = now;
            now = performance.now();
            dt = now - then - nextInterval; // the drift

            nextInterval = interval - dt;
            serverTime += interval;
            date     = new Date(serverTime);
            hours    = date.getHours();
            minutes  = date.getMinutes();
            seconds  = date.getSeconds();
            document.getElementById('txt').innerHTML = '<%=fecha%>' + ' - ' + hours + ':' + minutes + ':' + seconds;
            //document.getElementById('txt').innerHTML =
            console.log(nextInterval, dt); //Click away to another tab and check the logs after a while
            now = performance.now();

            setTimeout(step, Math.max(0, nextInterval)); // take into account drift
        }
    </script>
</head>

<body>
 <span id="txt"></span>
</body>
</html>
