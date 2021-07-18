

<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.OutputStream"%>
<%@page import="java.io.File"%>
<%@page import="java.net.URL"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>(FEMASE)Mensaje de administracion</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/mantencion/turnos/bootstrap.min.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/mantencion/turnos/style.css">
    <script src="<%=request.getContextPath()%>/mantencion/turnos/jquery-1.9.1.min.js" type="text/javascript"></script>
   	
	<script language="javascript">
            
            // Set the date we're counting down to
			var ahora= new Date();
			ahora.setSeconds(ahora.getSeconds() + 10 );
			var countDownDate = ahora.getTime();

            // Update the count down every 1 second
            var x = setInterval(function() {
                // Get today's date and time
                var now = new Date().getTime();

                // Find the distance between now and the count down date
                var distance = countDownDate - now;

                // Time calculations for days, hours, minutes and seconds
                var seconds = Math.floor((distance % (1000 * 60)) / 1000);

                // Output the result in an element with id="demo"
                document.getElementById("demo").innerHTML = seconds + "s ";

                // If the count down is over, write some text 
                if (distance < 0) {
                    clearInterval(x);
                    //document.getElementById("demo").innerHTML = "EXPIRED";
					parent.document.location.href = "<%=request.getContextPath()%>/";
					//location.replace("<%=request.getContextPath()%>/")
                }
            }, 1000);

        </script>
</head>

<body>
 <form name="asignacionForm" action="<%=request.getContextPath()%>/servlet/ss">
<div class="container">
  <p class="header h1"></p>
  <div class="panel-group">
   <div class="panel panel-primary">
      <div class="panel-heading">Tu sesion ha expirado.</div>
      <div class="panel-body">
        <table width="70%" border="0" align="center">
          <tr>
            <td width="94%" valign="top" class="h4">
                <span class="header h1">
                    Ser&aacute;s redireccionado a la p&aacute;gina de login en <p id="demo"></p>
                </span>
            </td>
            </tr>
        </table>
      </div>
    </div>
   
  </div>
</div>
  

  </div>
</div>

</form>
</body>
</html>
