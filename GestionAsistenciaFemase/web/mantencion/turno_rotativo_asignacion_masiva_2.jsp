<%@ include file="/include/check_session.jsp" %>
<%@page import="java.time.LocalDateTime"%>
<%@page import="java.time.ZoneId"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.Calendar"%>
<%@page import="cl.femase.gestionweb.vo.TurnoRotativoVO"%>
<%@page import="java.util.Iterator"%>
<%@page import="cl.femase.gestionweb.vo.EmpresaVO"%>
<%@page import="cl.femase.gestionweb.vo.DuracionVO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="cl.femase.gestionweb.vo.AsignacionTurnoRotativoVO"%>
<%@page import="java.util.List"%>
<%@page import="cl.femase.gestionweb.vo.EmpleadoVO"%>
<%
    ArrayList<DuracionVO> duraciones =   (ArrayList<DuracionVO>)request.getAttribute("duraciones");
    List<TurnoRotativoVO> turnos    =   (List<TurnoRotativoVO>)request.getAttribute("turnos");
    ArrayList<EmpleadoVO> empleados =   (ArrayList<EmpleadoVO>)request.getAttribute("empleados");
    String labelAction = "Seleccion de turno y duracion";
    String strAction="modificar";
    String fechaDesde = "";
    String fechaHasta = "";
    Calendar mycal          = Calendar.getInstance(new Locale("es","CL"));
    Date currentDate        = mycal.getTime();
    SimpleDateFormat sdf    = new SimpleDateFormat("yyyy-MM-dd");
    String fechaActual      = sdf.format(currentDate);
    LocalDateTime localDateTime = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    localDateTime = localDateTime.plusDays(7);
    
    // convert LocalDateTime to date
    Date endDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    fechaDesde  = fechaActual;
    fechaHasta  = sdf.format(endDate);
%>
<!DOCTYPE html>
<html>
<head>
	<script src="<%=request.getContextPath()%>/Jquery-JTable/Scripts/jquery-1.9.1.min.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/Jquery-JTable/Scripts/jquery-ui-1.10.0.min.js" type="text/javascript"></script>
    
 <!-- javascript y estilo para calendario datepicker  -->
<script src="<%=request.getContextPath()%>/jquery-plugins/datepicker/js/jquery.datepick.js"></script>
<script src="<%=request.getContextPath()%>/jquery-plugins/datepicker/js/jquery.plugin.min.js"></script>
<script src="<%=request.getContextPath()%>/jquery-plugins/datepicker/js/jquery.datepick.js"></script>
<link href="<%=request.getContextPath()%>/jquery-plugins/datepicker/css/jquery.datepick.css"rel="stylesheet">
    
    <script type="text/javascript">
        
        $(document).ready(function() {
         
        });


        function setFechas(keyDuracion) {
            var tt = document.getElementById('startDate').value;
            var res = keyDuracion.split("|");
            var numDias = parseInt(res[1])+1;
			var date = new Date(tt);
			var newdate = new Date(date);
		
			newdate.setDate(newdate.getDate() + numDias);
			
			var dd = parseInt(newdate.getDate());
			var mm = parseInt(newdate.getMonth()) + 1;
			var y = parseInt(newdate.getFullYear());
			var auxdd = '' + dd;
			var auxmm = '' + mm;
			if (dd < 10) auxdd = '0' + auxdd;
			if (mm < 10) auxmm = '0' + auxmm;

    		var someFormattedDate = y + '-' + auxmm + '-' + auxdd;
			document.getElementById('endDate').value = someFormattedDate;
		}

        function guardarAsignacion(){
            var duracion = document.getElementById("duracion").value;
            var turno    = document.getElementById("turno").value;
            var desde    = document.getElementById("startDate").value;
            var hasta    = document.getElementById("endDate").value;
            if (duracion === '-1'){
                alert('Seleccione duracion');
                document.getElementById("duracion").focus();
                return false;
            }else if (turno === '-1'){
                alert('Seleccione turno');
                document.getElementById("turno").focus();
                return false;
            }else if (desde === ''){
                alert('Seleccione fecha de inicio');
                document.getElementById("startDate").focus();
                return false;
            }else if (hasta === ''){
                alert('Seleccione fecha de termino');
                document.getElementById("endDate").focus();
                return false;
            }
            var objDate = document.getElementById("startDate").value;
            var arrDate    = objDate.split('-');
            var intYear   = parseInt(arrDate[0]);
            var intMonth  = parseInt(arrDate[1]);
            var intDay    = parseInt(arrDate[2]);
            var startDate = new Date(intYear, intMonth, intDay);
            var objDate = document.getElementById("endDate").value;
            var arrDate    = objDate.split('-');
            var intYear   = parseInt(arrDate[0]);
            var intMonth  = parseInt(arrDate[1]);
            var intDay    = parseInt(arrDate[2]);
            var endDate = new Date(intYear, intMonth, intDay);

            if (endDate < startDate) {
                alert('La fecha de inicio debe ser menor que la fecha de termino');
                document.getElementById("startDate").focus();
                return false;
                // myDate is between startDate and endDate
            }
            document.nturnosForm.submit();
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
	width: 100%;
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
<title>Seleccionar duracion y turno</title>
</head>
<body>
<form 
name="nturnosForm"
id="nturnosForm"
method="post" 
action="<%=request.getContextPath()%>/TurnosRotativosAsignacionController"
target="_parent"
>
<p class="labelBold"><%=labelAction%></p>
<table width="100%" border="0">
  <tr>
    <td bgcolor="#dddddd">
    <div class="divTableContainer">  
    <div class="divTable">
        <div class="divTableBody">
            <div class="divTableRow">
                
                
                
                <div class="divTableHeading">&nbsp;Duraci&oacute;n</div>
                <div class="divTableCell">&nbsp;:
                    <select name="duracion" id="duracion" onchange="setFechas(this.value)">
                      <option value="-1" selected>Seleccione duracion</option>
                    <%for (int i = 0; i < duraciones.size(); i++) {
                         DuracionVO itDuracion = duraciones.get(i);
                         String strKey=itDuracion.getId()+"|"+itDuracion.getNumDias();
                        %>
                        <option value="<%=strKey%>"><%=itDuracion.getNombre()%></option>
			
		    <%}%>  
                    
                    
                  </select>
                  <input name="action" type="hidden" id="action" value="guardarAsignacionMasiva">
                </div>
                <div class="divTableHeading">&nbsp;Fecha de inicio</div>
                <div class="divTableCell">&nbsp;:
                    <input name="startDate" type="text" id="startDate" value="<%=fechaDesde%>" size="10" maxlength="11">
                </div>
                <div class="divTableHeading">&nbsp;Fecha de t&eacute;rmino</div>
                <div class="divTableCell">:
                  <input name="endDate" type="text" id="endDate" value="<%=fechaHasta%>" size="10" maxlength="11">
                </div>
            </div>
            <div class="divTableRow">
                <div class="divTableHeading">&nbsp;Turno</div>
                <div class="divTableCell">:
                    <select name="turno" id="turno">
                        <option value="-1" selected>Seleccione turno</option>
                        <%
                            Iterator<TurnoRotativoVO> iteraturnos = turnos.iterator();
                            while(iteraturnos.hasNext() ) {
                                TurnoRotativoVO auxturno = iteraturnos.next();
                                String strLabel = auxturno.getNombre() 
                                    + "[" 
                                    + auxturno.getHoraEntrada() 
                                    + " - " 
                                    + auxturno.getHoraSalida() + "]";
                                %>
                                <option value="<%=auxturno.getId()%>"><%=strLabel%></option>
                                <%
                            }
                        %>
                    </select>
                </div>
                <div class="divTableHeading">&nbsp;</div>
                <div class="divTableCell">&nbsp;:</div>
                <!--<div class="divTableHeading">&nbsp;</div>
                <div class="divTableCell">&nbsp;</div>-->
            </div>
        </div>
	</div>
   </div>
    
    </td>
  </tr>
</table>
        
        <!-- Mostrar lista de empleados seleccionados-->
        <p class="labelBold">Empleados seleccionados.</p>
        <table>
          <tr>
            <th>Num Ficha</th>
            <th>Rut</th>
            <th>Nombre</th>
            <th>Empresa</th>
            <th>Departamento</th>
            <th>Centro de costo</th>
            <th>Turno</th>
          </tr>
          <%
            for (int i = 0; i < empleados.size(); i++) {
                EmpleadoVO empleado = empleados.get(i);
                String empleadoKey = empleado.getEmpresa().getId()
                    + "|"
                    + empleado.getRut();
                %>
                <tr>
                  <td>
                    <%=empleado.getCodInterno()%>
                      </td>
                    <td><%=empleado.getRut()%>
                        <input type="hidden" 
                            name="rutSelected" 
                            id="hiddenField" 
                            value="<%=empleadoKey%>"/></td>
                    <td><%=empleado.getNombres()%></td>
                    <td><%=empleado.getEmpresaNombre()%></td>
                    <td><%=empleado.getDeptoNombre()%></td>
                    <td><%=empleado.getCencoNombre()%></td>
                    <td><%=empleado.getNombreTurno()%></td>
                </tr>
            <%}
          %>

        </table>
        <!-- fin tabla de empleados -->
                   
<p class="labelBold">&nbsp;</p>
<p>
  <button type="button" id="LoadRecordsButton" onClick="guardarAsignacion()">Guardar</button>
   
</form>

<script type="text/javascript">
        $.datepicker.regional['es'] = {
            closeText: 'Cerrar',
            prevText: '< Ant',
            nextText: 'Sig >',
            currentText: 'Hoy',
            monthNames: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'],
            monthNamesShort: ['Ene','Feb','Mar','Abr', 'May','Jun','Jul','Ago','Sep', 'Oct','Nov','Dic'],
            dayNames: ['Domingo', 'Lunes', 'Martes', 'Mi�rcoles', 'Jueves', 'Viernes', 'S�bado'],
            dayNamesShort: ['Dom','Lun','Mar','Mi�','Juv','Vie','S�b'],
            dayNamesMin: ['Do','Lu','Ma','Mi','Ju','Vi','S�'],
            weekHeader: 'Sm',
            dateFormat: 'dd/mm/yy',
            firstDay: 1,
            isRTL: false,
            showMonthAfterYear: false,
            yearSuffix: ''
        };
        $.datepicker.setDefaults($.datepicker.regional['es']);
    
        $(function() {
            $('#startDate').datepick({dateFormat: 'yyyy-mm-dd'});
            $('#endDate').datepick({dateFormat: 'yyyy-mm-dd'});

        });

    </script>                                         
</body>
</html>