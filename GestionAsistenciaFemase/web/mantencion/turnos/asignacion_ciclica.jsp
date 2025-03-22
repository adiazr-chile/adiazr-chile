<%@page import="cl.femase.gestionweb.vo.TurnoRotativoCicloVO"%>
<%@ include file="/include/check_session.jsp" %>
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
    List<UsuarioCentroCostoVO> cencos 
        = (List<UsuarioCentroCostoVO>)session.getAttribute("cencos_empleado"); 
    List<TurnoRotativoVO> turnos = 
        (List<TurnoRotativoVO>)session.getAttribute("turnos_rotativos"); 
    
    List<TurnoRotativoCicloVO> ciclos = (List<TurnoRotativoCicloVO>)session.getAttribute("ciclos");
    
    List<LocalDate> detalleCiclo = (List<LocalDate>)session.getAttribute("detalle_ciclo");
	
    List<EmpleadoVO> listaEmpleados = (List<EmpleadoVO>)session.getAttribute("empleados_cenco"); 
    List<EmpleadoVO> listaEmpleadosSeleccionados = (List<EmpleadoVO>)session.getAttribute("empleados_seleccionados"); 
    
    String cencoId = (String)session.getAttribute("cencoId"); 
    
    String strReload  = (String)request.getParameter("reload");
    String strLabelCenco  = (String)session.getAttribute("labelCenco");
    String fechaInicio  = (String)session.getAttribute("fecha_inicio");
    String numCiclos    = (String)session.getAttribute("num_ciclos");
    String duracion    = (String)session.getAttribute("duracion");
   		
    if (fechaInicio == null) fechaInicio = "";
    if (numCiclos == null) numCiclos = "7";
    if (duracion == null) duracion = "1";
    if (strLabelCenco == null) strLabelCenco = "";
   
    
    if (strReload != null && strReload.compareTo("true") == 0){
        fechaInicio 	= "";
        numCiclos		= "7";
        duracion 		= "1";
        strLabelCenco 	= "";
        listaEmpleados 	= null;
        detalleCiclo	= null;
    } 
	
    if (cencos == null) cencos = new ArrayList<>();
    if (listaEmpleados == null) listaEmpleados = new ArrayList<>();
    if (listaEmpleadosSeleccionados == null) listaEmpleadosSeleccionados = new ArrayList<>();
    if (turnos == null) turnos = new ArrayList<>();
    if (detalleCiclo == null) detalleCiclo = new ArrayList<>();
	 
    System.out.println("[GestionFemaseWeb]asignacion_ciclica.jsp]-1-"
        + ". FechaInicio= " + fechaInicio
        + ", numCiclos= " + numCiclos
        + ", duracion= " + duracion
        + ", strReload= " + strReload);
    
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
  <script src="<%=request.getContextPath()%>/mantencion/turnos/jquery-1.9.1.min.js" type="text/javascript"></script>
  <!--<script src="datepicker-es.js" type="text/javascript"></script>-->

	<!-- javascript y estilo para calendario datepicker  -->
    <link rel="stylesheet" href="<%=request.getContextPath()%>/mantencion/turnos/jquery-ui.css">
    <script src="<%=request.getContextPath()%>/mantencion/turnos/jquery-1.12.4.js"></script>
    <script src="<%=request.getContextPath()%>/mantencion/turnos/jquery-ui.js"></script>
    
	<script language="javascript">
    $(function(){
        
        $.datepicker.regional['es'] = {
            closeText: 'Cerrar',
            prevText: '< Ant',
            nextText: 'Sig >',
            currentText: 'Hoy',
            monthNames: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'],
            monthNamesShort: ['Ene','Feb','Mar','Abr', 'May','Jun','Jul','Ago','Sep', 'Oct','Nov','Dic'],
            dayNames: ['Domingo', 'Lunes', 'Martes', 'Miercoles', 'Jueves', 'Viernes', 'Sabado'],
            dayNamesShort: ['Dom','Lun','Mar','Mie','Juv','Vie','Sab'],
            dayNamesMin: ['Do','Lu','Ma','Mi','Ju','Vi','Sab'],
            weekHeader: 'Sm',
            dateFormat: 'dd/mm/yy',
            firstDay: 1,
            isRTL: false,
            showMonthAfterYear: false,
            yearSuffix: ''
        };
        $.datepicker.setDefaults($.datepicker.regional['es']);
            
        $( function() {
            $( "#fecha_inicio" ).datepicker();
            $( "#fecha_inicio" ).datepicker( "option", "dateFormat", "yy-mm-dd");
        });

        $("#button1").click(function(){
            $("#list1 > option:selected").each(function(){
                $(this).remove().appendTo("#list2");
            });
        });
        
        $("#button2").click(function(){
            $("#list2 > option:selected").each(function(){
                $(this).remove().appendTo("#list1");
            });
        });
		
        <%if (cencoId != null){%>
            document.getElementById("cencoId").value = "<%=cencoId%>";
            <%if (!listaEmpleados.isEmpty()){%>
                document.getElementById("num_ciclos").value = "<%=numCiclos%>";
                document.getElementById("duracion").value = "<%=duracion%>";
            <%}%>
            
            <% if (fechaInicio != null && fechaInicio.compareTo("") != 0){%>
                var fechaInicio = new Date('<%=fechaInicio%>'+'T24:00:00-00:00');
                $("#fecha_inicio").datepicker().datepicker("setDate", fechaInicio);
            <%}%>
            
        <%}else{%>
            document.getElementById("num_ciclos").value = "7";
        <%}%>
    });
	
	function loadEmpleados(){
		var comboCenco = document.getElementById("cencoId");
		var cencoSelected = comboCenco.value;
		if (cencoSelected !== '-1'){
			var labelCenco = comboCenco.options[comboCenco.selectedIndex].text;
			document.getElementById("labelCenco").value = labelCenco;
			document.asignacionForm.submit();
		}else {
			alert('Seleccione un centro de costo...');
			return false;
		}
	}
	
	function verCiclos(){
            var empleadosSelected = document.getElementById("list2").length;
            var fechaInicio = document.getElementById("fecha_inicio").value;
            if (empleadosSelected > 0){
                if (fechaInicio !== ""){
                    document.getElementById("action").value = "mostrar_ciclo";
                    document.asignacionForm.submit();
                }else {
                    alert("Debe ingresar una fecha de inicio.");
                    document.getElementById("fecha_inicio").focus();
                    return false;
                }
            }else {
                alert("Debe seleccionar uno o mas empleados.");
                document.getElementById("list2").focus();
                return false;
            }
            
            
            
	}
	
	function vistaPrevia(){
            document.getElementById("action").value = "preview_asignacion_ciclo";
			selectAllEmpleados();
            document.asignacionForm.submit();
	}


	function selectAllEmpleados() { 
		var selectBox = document.getElementById("list2");
		for (var i = 0; i < selectBox.options.length; i++) { 
			 selectBox.options[i].selected = true; 
		} 
	}
        
        
</script>
</head>

<body>
 <form name="asignacionForm" action="<%=request.getContextPath()%>/servlet/AsignacionCiclica">
<div class="container">
  <p class="header h1">Asignaci&oacute;n c&iacute;clica de turnos a empleados</p>
  <div class="panel-group">
   <div class="panel panel-primary">
      <div class="panel-heading"></div>
      <div class="panel-body">
        <table width="100%" border="0">
          <tr>
            <td width="31%" valign="top" class="h4">Centro de costo</td>
            <td width="43%" align="center" valign="top"><span class="col-md-4">
              <select name="cencoId" id="cencoId">
                    <option value="-1" selected>----------</option>
                    <%
                    String valueCenco="";
                    String labelCenco="";    
                    Iterator<UsuarioCentroCostoVO> iteracencos = cencos.iterator();
                    while(iteracencos.hasNext() ) {
                        UsuarioCentroCostoVO auxCenco = iteracencos.next();
                        valueCenco = auxCenco.getEmpresaId() + "|" + auxCenco.getDeptoId() + "|" + auxCenco.getCcostoId();
                        labelCenco = "[" + auxCenco.getEmpresaNombre()+ "]," 
                            + "[" + auxCenco.getDeptoNombre()+ "],"
                            + "[" + auxCenco.getCcostoNombre()+ "]";
                    %>
                        <option value="<%=valueCenco%>"><%=labelCenco%></option>
                        <%
                    }
                %>
              </select>
            </span></td>
            <td width="26%" align="right" valign="top"><span class="col-md-4">
              <input type="button" name="ver" id="ver" value="Ver empleados" class="button button-blue" onClick="loadEmpleados()">
            </span>
              <input name="action" type="hidden" id="action" value="list_empleados">
              <input name="labelCenco" type="hidden" id="labelCenco" value="list_empleados"></td>
          </tr>
        </table>
      </div>
    </div>
  
    <div class="panel panel-default">
      <div class="panel-heading">Selecci&oacute;n de empleados &nbsp;<%=strLabelCenco%></div>
      <div class="panel-body">
        <table width="90%" border="0">
          <tr>
            <td width="61%" class="h4">Empleados existentes</td>
            <td width="3%">&nbsp;</td>
            <td width="36%" class="h4">Empleados seleccionados</td>
          </tr>
          <tr>
            <td rowspan="3" valign="top">
              <select name="list1" size="8" 
                      multiple="multiple" class="dropdown-header" id="list1" style="width:400px" rows=2>
                <%
                    String strValue = "";
                    String strLabel = "";    
                    Iterator<EmpleadoVO> iteraEmpleados = listaEmpleados.iterator();
                    while(iteraEmpleados.hasNext() ) {
                        EmpleadoVO auxEmpleado = iteraEmpleados.next();
                        strValue = auxEmpleado.getRut();
                        strLabel = auxEmpleado.getNombreCompleto();
                        strLabel = "(" + strValue + ") " + strLabel.toUpperCase();
                    %>
                        <option value="<%=strValue%>"><%=strLabel%></option>
                        <%
                    }
                %>
              </select></td>
            <td height="14" valign="top">&nbsp;</td>
            <td rowspan="3" valign="top">
                <select name="list2" size="8" 
                        multiple="multiple" 
                        class="dropdown-header" 
                        id="list2" 
                        style="width:400px" rows=2>
              <%
                    String strValue2 = "";
                    String strLabel2 = "";    
                    Iterator<EmpleadoVO> itSeleccionados = listaEmpleadosSeleccionados.iterator();
                    while(itSeleccionados.hasNext() ) {
                        EmpleadoVO auxEmpleado2 = itSeleccionados.next();
                        strValue2 = auxEmpleado2.getRut();
                        //strLabel2 = auxEmpleado2.getNombreCompleto() + " " + auxEmpleado2.getApeMaterno();
                        
                        strLabel2 = auxEmpleado2.getNombreCompleto();
                        strLabel2 = "(" + strValue2 + ") " + strLabel2.toUpperCase();
                        
                    %>
                        <option value="<%=strValue2%>"><%=strLabel2%></option>
                        <%
                    }
                %>
            </select></td>
          </tr>
          <tr>
            <td height="62" valign="top"><input id="button1" type="button" value=">" class="button button-blue"/></td>
            </tr>
          <tr>
            <td height="51" valign="top"><input id="button2" type="button" value="<" class="button button-blue"/></td>
          </tr>
        </table>
      </div>
    </div>

 <%if (!listaEmpleados.isEmpty()){%>
    
 <%
     System.out.println("[GestionFemaseWeb]asignacion_ciclica.jsp]-2-"
        + ". FechaInicio= " + fechaInicio
        + ", numCiclos= " + numCiclos
        + ", duracion= " + duracion);
 
 %>
 
    <div class="panel panel-default">
        <div class="panel-heading">Inicializaci&oacute;n de ciclo(s)</div>
        <div>
            <table width="90%" border="0" cellpadding="1" cellspacing="1">
              <tr>
                <td width="12%" valign="middle" class="h4">&nbsp;Fecha inicio</td>
                <td width="15%" valign="middle"><label for="fecha_inicio"></label>
                    <input type="text" name="fecha_inicio" id="fecha_inicio">
                    
                <td width="16%" align="right" valign="middle" class="h4">N&uacute;mero de ciclos</td>
                <td width="20%" valign="middle"><span class="col-md-4">
                  <select id="num_ciclos" name="num_ciclos" >
                    <%
                     Iterator<TurnoRotativoCicloVO> iteraCiclos = ciclos.iterator();
                    while(iteraCiclos.hasNext() ) {
                        TurnoRotativoCicloVO auxCiclo = iteraCiclos.next();
                        strValue = "" + auxCiclo.getNumDias();
                        strLabel = auxCiclo.getEtiqueta();
                        //strLabel = "(" + strValue + ") " + strLabel.toUpperCase();
                    %>
                        <option value="<%=strValue%>"><%=strLabel%></option>
                        <%} %>
                  </select>
                </span></td>
                <td width="15%" align="right" valign="middle" class="h4">Duraci&oacute;n</td>
                <td width="11%" valign="middle"><span class="col-md-4">
                    <select id="duracion" name="duracion">
                        <option value="1M" selected>1 mes</option>
                        <option value="3M">3 meses</option>
                        <option value="6M">6 meses</option>
                        <option value="1">1 A&ntilde;o</option>
                        <option value="2">2 A&ntilde;os</option>
                        <option value="3">3 A&ntilde;os</option>
                        <option value="4">4 A&ntilde;os</option>
                        <option value="5">5 A&ntilde;os</option>
                    </select>
                </span></td>
                <td width="11%" valign="middle"><span class="col-md-4">
                  <input type="button" name="btn_guardar" id="btn_guardar" value="Definir Ciclo(s)" 
                  class="button button-blue" onClick="verCiclos()">
                </span></td>
              </tr>
            </table>
        </div>
    </div>

 <!-- Panel para definir los turnos para cada dia del ciclo-->  
<div class="panel panel-default">
      <div class="panel-heading">Asignaci&oacute;n de turnos para el/los ciclo(s)</div>
      <div>
        <table width="90%" border="0" cellpadding="1" cellspacing="1">
          <tr>
            <td align="right" valign="top" bgcolor="#CCFFFF" class="h4">D&iacute;a&nbsp;</td>
            <td align="left" valign="top" bgcolor="#CCFFFF" class="h4">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Turno</td>
            </tr>
            <%
                Iterator<LocalDate> iteraCiclo = detalleCiclo.iterator();
                while(iteraCiclo.hasNext() ) {
                    LocalDate date = iteraCiclo.next();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    String formattedString = date.format(formatter);
                    String labelDiaSemana=
                        date.getDayOfWeek().getDisplayName(TextStyle.FULL, localeCl_1)
                        + " " + formattedString;
                    
                    %>
                        <tr>
                            <td width="12%" align="right" valign="top" class="h5"><%=labelDiaSemana%>
                            <input name="date" type="hidden" id="date" value="<%=date.toString()%>">
                            &nbsp;</td>
                            <td width="15%" valign="top">
                                    <select id="turno_<%=date.toString()%>" 
                                            name="turno_<%=date.toString()%>" 
                                            class="dropdown-header">
                                           <option value="-1">Sin turno asignado</option> 
                                        <%
                                        Iterator<TurnoRotativoVO> iteraTurnos = turnos.iterator();
                                        while(iteraTurnos.hasNext() ) {
                                            TurnoRotativoVO turno = iteraTurnos.next();
                                            int turnoId = turno.getId();
                                            //String turnoName = turno.getNombre() 
                                            //    + " [" + turno.getHoraEntrada()
                                            //    + "-" + turno.getHoraSalida() + "]";
                                            
                                            String turnoName = "(" + turno.getId() +") "+ turno.getNombre() + "[" 
                                                + turno.getHoraEntrada() 
                                                + " - " + turno.getHoraSalida()+"]";    

                                        %>    
                                        <option value="<%=turnoId%>"><%=turnoName%></option>
                                        <%}%>    
                                    </select>

                            </td>
                        </tr>
                <%}%>
          <tr>
            <td valign="top">&nbsp;</td>
            <td valign="top"><span class="col-md-4">
              <input type="button" name="btn_preview" id="btn_preview" value="Vista previa asignacion" 
              class="button button-blue" onClick="vistaPrevia()">
            </span></td>
            </tr>
        </table>
      </div>
    </div>
<%}%>
  </div>
</div>
  

  </div>
</div>

</form>
         
    <script type="text/javascript">
       
    </script>		
</body>
</html>
