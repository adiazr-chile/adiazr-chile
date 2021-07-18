<%@ include file="/include/check_session.jsp" %>
<%@page import="cl.femase.gestionweb.vo.AsignacionTurnoRotativoVO"%>
<%@page import="java.util.List"%>
<%@page import="cl.femase.gestionweb.vo.EmpleadoVO"%>
<%
    EmpleadoVO infoempleado = (EmpleadoVO)request.getAttribute("infoempleado");
    List<AsignacionTurnoRotativoVO> listaTurnos = (List<AsignacionTurnoRotativoVO>)request.getAttribute("listaturnos");
    
%>
<!DOCTYPE html>
<html>
<head>
<script language="javascript1.5">
	
        function switchDeleteButton(){
            var checkedCheckboxes = document.querySelectorAll("[name='asignacionKey_del']:checked");
            if (checkedCheckboxes.length === 0) {
                document.getElementById("boton2").disabled = true;
            }else{
                document.getElementById("boton2").disabled = false;
            }
            
        }
        
        /*function switchDeleteButton(){
		//var isMyCheckboxChecked = $('#asignacionKey_del').is(':checked');
		//var ckbox = document.getElementById("asignacionKey_del");//$("input[name='asignacionKey_del']");
		var isChecked = document.getElementById("asignacionKey_del").checked;
                alert('[switchDeleteButton]isChecked?'+isChecked);
		
                var type[];
                $("input[name='asignacionKey_del']:checked").each(function (i) {
                    type[i] = $(this).val();
                    alert('type '+i+': '+type[i]);
                });
                //if (isMyCheckboxChecked) {
		if (isChecked) {
			document.getElementById("boton2").disabled = false;  
			//$('boton2').removeAttr("disabled");
		}else{
			document.getElementById("boton2").disabled = true;  
			//$('boton2').attr("disabled", "disabled");
		}
	}*/
	
	function eliminarAsignacion(){
		document.getElementById("action").value='deleteAsignacion';
		document.turnosForm.submit();	
	}
</script>
    
<style>
	body {
	  font-family: 'Segoe UI Semilight', 'Open Sans', Verdana, Arial, Helvetica, sans-serif;
	  font-weight: 400;
	  color: #000000;
	  font-size: 11px;
	}
	table {
		font-family: arial, sans-serif;
		border-collapse: collapse;
		width: 100%;
	}
	
	td, th {
		border: 1px solid #0b67cd;
		text-align: left;
		padding: 2px;
		font-size:11px;
	}
	
	tr:nth-child(even) {
		background-color: #b5cde8;
		font-size:11px;
	}
	
	.divTable{
		display: table;
		width: 70%;
		font-family: arial, sans-serif;
		font-size: 11px;	
	}
	.divTableContainer{
		display: table;
		width: 100%;
		font-family: arial, sans-serif;
		font-size: 11px;	
	}
	.divTableRow {
		display: table-row;
	}
	.divTableHeading {
		display: table-header-group;
		font-size: 11px;
	}
	.divTableCell, .divTableHead {
		display: table-cell;
		padding: 3px 10px;
		background-color: #FFFFFF;
		font-size: 11px;
	}
	.divTableHeading {
		background-color: #dddddd;
		display: table-header-group;
		font-weight: bold;
	}
	.divTableFoot {
		background-color: #EEE;
		display: table-footer-group;
		font-weight: bold;
	}
	.divTableBody {
		display: table-row-group;
	}
	.labelBold {
		font-weight: bold;
			color: #2d89ef;
	}
</style>
<title>Turnos rotativos asignados</title>
</head>
<body>
<form 
name="turnosForm"
id="turnosForm"
method="POST" 
action="<%=request.getContextPath()%>/TurnosRotativosAsignacionController">
<table width="100%" border="0">
  <tr>
    <td bgcolor="#dddddd">
    <div class="divTableContainer">  
    <div class="divTable">
        <div class="divTableBody">
            <div class="divTableRow">
                <div class="divTableHeading">&nbsp;Rut empleado</div>
                <div class="divTableCell">&nbsp;:<%=infoempleado.getRut()%>
                  <input type="hidden" name="rutEmpleado" id="rutEmpleado" value="<%=infoempleado.getRut()%>">
                  <input type="hidden" name="action" id="action" value="crearModificarAsignacion">
                  
                </div>
                <div class="divTableHeading">&nbsp;Nombre</div>
                <div class="divTableCell">&nbsp;:<%=infoempleado.getNombres()%></div>
                <div class="divTableHeading">&nbsp;Empresa</div>
                <div class="divTableCell">:<%=infoempleado.getEmpresa().getNombre()%>
                  <input type="hidden" name="empresaId" id="empresaId" value="<%=infoempleado.getEmpresa().getId()%>">
                </div>
            </div>
            <div class="divTableRow">
                <div class="divTableHeading">&nbsp;Departamento</div>
                <div class="divTableCell">&nbsp;:<%=infoempleado.getDepartamento().getNombre()%></div>
                <div class="divTableHeading">&nbsp;Centro de costo</div>
                <div class="divTableCell">&nbsp;:<%=infoempleado.getCentroCosto().getNombre()%></div>
                <!--<div class="divTableHeading">&nbsp;</div>
                <div class="divTableCell">&nbsp;</div>-->
            </div>
        </div>
	</div>
   </div>
    
    </td>
  </tr>
</table>
<p class="labelBold">Historial de turnos asignados</p>

<table>
  <tr>
    <th>Seleccionar</th>
    <th>Eliminar</th>
    <th>Nombre Turno</th>
    <th>Desde</th>
    <th>Hasta</th>
  </tr>
  <%
    String strAsignacionKey = "";
    String labelTurno = "";
    for (int i = 0; i < listaTurnos.size(); i++) {
        AsignacionTurnoRotativoVO asignacion = listaTurnos.get(i);
        strAsignacionKey = asignacion.getIdTurno() 
                + "|" + asignacion.getFechaDesde() 
                + "|" + asignacion.getFechaHasta();
        labelTurno = asignacion.getNombreTurno();
        %>
        <tr>
       	  <td>
       	    <input type="radio" name="asignacionKey" id="asignacionKey" 
            value="<%=strAsignacionKey%>">
   	      </td>
       	  <td><input type="checkbox" name="asignacionKey_del" id="asignacionKey_del" 
                     value="<%=strAsignacionKey%>" onclick="switchDeleteButton()"></td>
            <td><%=labelTurno%></td>
            <td><%=asignacion.getFechaDesde()%></td>
            <td><%=asignacion.getFechaHasta()%></td>
        </tr>
    <%}
  %>
  
</table>
<p><button type="submit" id="boton1">Asignar/reemplazar turno</button>
<button type="button" id="boton2" onClick="eliminarAsignacion()" disabled>Eliminar seleccionado(s)</button>
</p>
</form>
</body>
</html>