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
    EmpleadoVO infoempleado = (EmpleadoVO)request.getAttribute("infoempleado");
    AsignacionTurnoRotativoVO turnoAsignado = (AsignacionTurnoRotativoVO)request.getAttribute("turnoAsignado");
    AsignacionTurnoRotativoVO newTurnoAsignado = (AsignacionTurnoRotativoVO)request.getAttribute("newTurnoAsignado");
    
    ArrayList<DuracionVO> duraciones =   (ArrayList<DuracionVO>)request.getAttribute("duraciones");
    List<TurnoRotativoVO> turnos    =   (List<TurnoRotativoVO>)request.getAttribute("turnos");
    List<AsignacionTurnoRotativoVO> asignacionesConflicto  =   (List<AsignacionTurnoRotativoVO>)request.getAttribute("asignacionesConflicto");
    String labelAction = "Modificar/reemplazar asignacion turno";
    String strAction="modificar";
    String fechaDesde = "";
    String fechaHasta = "";
    Calendar mycal          = Calendar.getInstance(new Locale("es","CL"));
    Date currentDate        = mycal.getTime();
    SimpleDateFormat sdf    = new SimpleDateFormat("yyyy-MM-dd");
    String fechaActual      = sdf.format(currentDate);
    LocalDateTime localDateTime = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    localDateTime = localDateTime.plusDays(7);
    int idDuracion  = 0;
    int idTurno     = 0;
    
    // convert LocalDateTime to date
    Date endDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    if (turnoAsignado.getIdTurno() == 0){
        strAction   ="crear";
        labelAction = "Crear asignacion turno";
        fechaDesde  = fechaActual;
        fechaHasta  = sdf.format(endDate);
        
        String newIdTurno       = (String)request.getAttribute("newIdTurno");
        String newIdDuracion    = (String)request.getAttribute("newIdDuracion");
        String newFechaInicio   = (String)request.getAttribute("newFechaDesde");
        String newFechaTermino  = (String)request.getAttribute("newFechaHasta");
        if (newIdTurno != null){
            idTurno = Integer.parseInt(newIdTurno);
        }
        if (newIdDuracion != null){
            idDuracion = Integer.parseInt(newIdDuracion);
        }
        if (newFechaInicio != null){
            fechaDesde = newFechaInicio;
        }
        if (newFechaTermino != null){
            fechaHasta = newFechaTermino;
        }
        
    }else{
        if (newTurnoAsignado != null){
            System.out.println("jsp asignacion turnos, newTurnoAsignado!=null");
            idTurno = newTurnoAsignado.getIdTurno();
            idDuracion = newTurnoAsignado.getIdDuracion();
            fechaDesde = newTurnoAsignado.getFechaDesde();
            fechaHasta = newTurnoAsignado.getFechaHasta();
        }else{
            idTurno = turnoAsignado.getIdTurno();
            idDuracion = turnoAsignado.getIdDuracion();
            fechaDesde = turnoAsignado.getFechaDesde();
            fechaHasta = turnoAsignado.getFechaHasta();
        }
        
    }
    
    
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
        <%

        String keyDuracion = idDuracion + "|" + turnoAsignado.getDiasDuracion();
        if (newTurnoAsignado!=null && newTurnoAsignado.getIdTurno() > 0){
            keyDuracion = idDuracion + "|" + newTurnoAsignado.getDiasDuracion();
        }
        if (turnoAsignado.getIdTurno() > 0 || (newTurnoAsignado!=null && newTurnoAsignado.getIdTurno() > 0)){%> 
            document.getElementById("turno").value = '<%=idTurno%>';
            document.getElementById("duracion").value = '<%=keyDuracion%>';
        <%}%>
    });
		
    function setFechas(keyDuracion) {
        var tt = document.getElementById('startDate').value;
        var res = keyDuracion.split("|");
        var numDias = parseInt(res[1]);//+1;
        //alert('startDate: ' + tt + ',numDias: ' + numDias);
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

        function verAsignacion(){
            var url='<%=request.getContextPath()%>/TurnosRotativosAsignacionController?';
            url+= 'action=mostrarAsignacionEmpleado';
            url+= '&empresa=<%=infoempleado.getEmpresa().getId()%>';
            url+= '&rut=<%=infoempleado.getRut()%>';

            document.location.href=url;
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
<title>Turnos rotativos crear/modificar</title>
</head>
<body>
<form 
name="nturnosForm"
id="nturnosForm"
method="post" 
action="<%=request.getContextPath()%>/TurnosRotativosAsignacionController">
<p class="labelBold"><%=labelAction%></p>
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
                  <input type="hidden" name="action" id="action" value="<%=strAction%>">
                  
                  
                  <input type="hidden" name="currentIdTurno" id="currentIdTurno" value="<%=turnoAsignado.getIdTurno()%>">
                  <input type="hidden" name="currentIdDuracion" id="currentIdDuracion" value="<%=turnoAsignado.getIdDuracion()%>">
                  <input type="hidden" name="currentFechaDesde" id="currentFechaDesde" value="<%=turnoAsignado.getFechaDesde()%>">
                  <input type="hidden" name="currentFechaHasta" id="currentFechaHasta" value="<%=turnoAsignado.getFechaHasta()%>">
                  
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
                </div>
                <div class="divTableHeading">&nbsp;Fecha de inicio</div>
                <div class="divTableCell">&nbsp;:
                    <input name="startDate" type="text" id="startDate" value="<%=fechaDesde%>">
                </div>
                <div class="divTableHeading">&nbsp;Fecha de t&eacute;rmino</div>
                <div class="divTableCell">:
                  <input name="endDate" type="text" id="endDate" value="<%=fechaHasta%>">
                </div>
            </div>
            <div class="divTableRow">
                <div class="divTableHeading">&nbsp;Turno</div>
                <div class="divTableCell">:
                    <select name="turno" id="turno">
                        <option value="-1" selected>Seleccione turno</option>
                        <%
                            Iterator<TurnoRotativoVO> iteraturnos = turnos.iterator();
                            String strLabel = "";
                            while(iteraturnos.hasNext() ) {
                                TurnoRotativoVO auxturno = iteraturnos.next();
                                strLabel = "(" + auxturno.getId() +") "+ auxturno.getNombre() + "[" 
                                    + auxturno.getHoraEntrada() 
                                    + " - " + auxturno.getHoraSalida()+"]";
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
        
    <%if (asignacionesConflicto!=null && !asignacionesConflicto.isEmpty()){%>                
        <!-- Mostrar interseccion de turnos segun fechas-->
        <p class="labelBold">Asignaciones conflicto. Seleccione el turno que desea reemplazar.</p>
        <table>
          <tr>
            <th>Seleccionar</th>
            <th>Nombre Turno</th>
            <th>Desde</th>
            <th>Hasta</th>
          </tr>
          <%
            String strAsignacionKey = "";  
            for (int i = 0; i < asignacionesConflicto.size(); i++) {
                AsignacionTurnoRotativoVO asignacion = asignacionesConflicto.get(i);
                strAsignacionKey = asignacion.getIdTurno() 
                    + "|" + asignacion.getFechaDesde() 
                    + "|" + asignacion.getFechaHasta();
                %>
                <tr>
                  <td>
                    <input type="radio" name="asignacionConflictoKey" id="asignacionKey" 
                    value="<%=strAsignacionKey%>">
                      </td>
                    <td><%=asignacion.getNombreTurno()%></td>
                    <td><%=asignacion.getFechaDesde()%></td>
                    <td><%=asignacion.getFechaHasta()%></td>
                </tr>
            <%}
          %>

        </table>
        <!-- fin tabla de interseccion de turnos -->
        <%}%>            
<p class="labelBold">&nbsp;</p>
<p>
  <button type="button" id="LoadRecordsButton" onClick="guardarAsignacion()">Guardar</button>
   <button type="button" id="LoadRecordsButton" onClick="verAsignacion()">Cancelar</button></p>
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