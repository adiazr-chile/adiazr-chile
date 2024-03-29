<%@ include file="/include/check_session.jsp" %>

<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.OutputStream"%>
<%@page import="java.io.File"%>
<%@page import="java.net.URL"%>

<%
    String paramCenco = (String)session.getAttribute("cencoId");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>(FEMASE)Asignacion de turnos a centros de costo</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/mantencion/turnos/bootstrap.min.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/mantencion/turnos/style.css">
    <script src="<%=request.getContextPath()%>/mantencion/turnos/jquery-1.9.1.min.js" type="text/javascript"></script>
   	
	<script language="javascript">
            function volver(){
                var paramCencoId = '<%=paramCenco%>';
                document.location.href='<%=request.getContextPath()%>/servlet/AsignacionTurnosRotativosCencosServlet?action=list_turnos&cencoId='+paramCencoId; 
            }
        </script>
</head>

<body>
 <form name="asignacionForm" action="<%=request.getContextPath()%>/servlet/ss">
<div class="container">
  <p class="header h1">Asignaci&oacute;n de turnos rotativos a centros de costo</p>
  <div class="panel-group">
   <div class="panel panel-primary">
      <div class="panel-heading"></div>
      <div class="panel-body">
        <table width="70%" border="0">
          <tr>
            <td width="40%" valign="top" class="h4">Asignacion guardada exitosamente</td>
            <td width="23%" align="center" valign="top"><a href="javascript:volver()">Volver</a></td>
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
