<%@ include file="/include/check_session.jsp" %>
<%@page import="cl.femase.gestionweb.vo.UsuarioVO"%>
<%@page import="cl.femase.gestionweb.vo.AsignacionCiclicaTurnoVO"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.util.Locale"%>
<%@page import="java.time.format.TextStyle"%>
<%@page import="java.time.LocalDate"%>
<%@page import="cl.femase.gestionweb.vo.TurnoRotativoVO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="cl.femase.gestionweb.vo.EmpleadoVO"%>
<%@page import="java.util.List"%>

<%@page import="cl.femase.gestionweb.vo.UsuarioCentroCostoVO"%>
<%@page import="java.util.Iterator"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    Locale localeCl_1 = new Locale("es", "CL");
    UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
    List<AsignacionCiclicaTurnoVO> listaAsignacion 
        = (List<AsignacionCiclicaTurnoVO>)session.getAttribute("asignacion_turnos"+ "|" + userConnected.getUsername());
 
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <title>(FEMASE)Asignacion ciclica de turnos</title>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="<%=request.getContextPath()%>/mantencion/turnos/bootstrap.min.css">
  <link rel="stylesheet" href="<%=request.getContextPath()%>/mantencion/turnos/style.css">
  <!--script src="bootstrap.min.js"></script -->
  <!--<script src="<%=request.getContextPath()%>/mantencion/turnos/jquery-1.9.1.min.js" type="text/javascript"></script>-->
  <!--<script src="datepicker-es.js" type="text/javascript"></script>-->

	<!-- datatable y paginacion -->
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/jquery-datatable/jquery.dataTables.min.css">
    <script type="text/javascript" src="<%=request.getContextPath()%>/jquery-datatable/jquery-3.3.1.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/jquery-datatable/jquery.dataTables.min.js"></script>


	<!-- javascript y estilo para calendario datepicker  -->
    <link rel="stylesheet" href="<%=request.getContextPath()%>/mantencion/turnos/jquery-ui.css">
    <!-- <script src="<%=request.getContextPath()%>/mantencion/turnos/jquery-1.12.4.js"></script> -->
    <script src="<%=request.getContextPath()%>/mantencion/turnos/jquery-ui.js"></script>
    
	<script language="javascript">
    	$(document).ready(function() {
            $('#example').DataTable( {
                "order": [[ 0, "asc" ]]
            });
        } );
	
        function guardarTurnos(){
            document.asignacionForm.submit();
	}
        
        

</script>
</head>

<body>
 <form name="asignacionForm" action="<%=request.getContextPath()%>/servlet/AsignacionCiclica">
<div class="container">
  <!--<p class="header h1">Vista previa de asignaci&oacute;n c&iacute;clica de turnos a empleados</p>-->
  <div class="panel-group">
   <div class="panel panel-primary">
      <div class="panel-heading">Vista previa de asignaci&oacute;n c&iacute;clica de turnos a empleados</div>
      <div class="panel-body">
        <%if (!listaAsignacion.isEmpty()){%>
         <table id="example" class="display" style="width:100%">
              <thead>
				<tr>
				  <th>Fecha</th>
                  <th>Dia<input name="action" type="hidden" id="action" value="guardar_asignacion_ciclo"></th>
                  <th>Turno</th>
                  </tr>
              </thead>
              <tbody>
                
                <%
                    Iterator<AsignacionCiclicaTurnoVO> iteraAsignacion = listaAsignacion.iterator();
                    while(iteraAsignacion.hasNext() ) 
                    {
                        AsignacionCiclicaTurnoVO asignacion = iteraAsignacion.next();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                        String formattedString = asignacion.getFecha().format(formatter);
                        String fechaKey = asignacion.getFecha().toString();
                        String labelDiaSemana=
                            asignacion.getFecha().getDayOfWeek().getDisplayName(TextStyle.FULL, localeCl_1)
                                + " " + formattedString;
						
                        %>
                        <tr>
                          <td><%=fechaKey%></td>
                            <td><%=labelDiaSemana%>
                                <input name="date" type="hidden" id="date" value="<%=asignacion.getFecha().toString()%>">
                                &nbsp;
                            </td>
                            <td><%=asignacion.getNombreTurno()%></td>
                        </tr>
                  <%}%>
              </tbody>
              <tfoot>
                <tr>
                  <th>Fecha</th>
                  <th>Dia</th>
                  <th>Turno</th>
                  </tr>
                <tr>
                  <td valign="top">&nbsp;</td>
            <td valign="top">&nbsp;</td>
            <td valign="top"><span class="col-md-4">
              <input type="button" name="btn_guardar2" id="btn_guardar2" value="Asignar Turnos" 
              class="button button-blue" onClick="guardarTurnos()">
              <input type="button" name="btn_volver" id="btn_volver" value="Volver" 
              class="button button-blue" onClick="javascript:window.history.back();">
            </span></td>
          </tr>  
              </tfoot>
            </table>
          
          
        <%}%>
        
      </div>
    </div>
 
  </div>
</div>
  

  </div>
</div>

</form>
         
    <script type="text/javascript">
       
    </script>		
</body>
</html>
