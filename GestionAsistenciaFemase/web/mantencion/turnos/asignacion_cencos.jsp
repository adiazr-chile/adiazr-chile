<%@ include file="/include/check_session.jsp" %>
<%@page import="java.util.Set"%>
<%@page import="cl.femase.gestionweb.vo.UsuarioCentroCostoVO"%>
<%@page import="cl.femase.gestionweb.vo.TurnoVO"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.util.Locale"%>
<%@page import="java.time.format.TextStyle"%>
<%@page import="java.time.LocalDate"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Iterator"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    List<UsuarioCentroCostoVO> cencos 
        = (List<UsuarioCentroCostoVO>)session.getAttribute("cencos_empleado"); 
    
    LinkedHashMap<Integer, TurnoVO> turnosDisponibles 
        = (LinkedHashMap<Integer, TurnoVO>)session.getAttribute("turnos_noasignados_cenco"); 
    
    LinkedHashMap<Integer, TurnoVO> turnosAsignados 
        = (LinkedHashMap<Integer, TurnoVO>)session.getAttribute("turnos_cenco"); 
    
    String cencoId = (String)session.getAttribute("cencoId"); 
    String strReload  = (String)request.getParameter("reload");
    String strLabelCenco  = (String)session.getAttribute("labelCenco");
    if (strLabelCenco == null) strLabelCenco = "";
   
    if (strReload != null && strReload.compareTo("true") == 0){
        strLabelCenco 	= "";
        turnosAsignados = null;
        turnosDisponibles= null;
        cencoId = null;
    } 
	
    if (turnosAsignados == null) turnosAsignados = new LinkedHashMap<>();
    if (turnosDisponibles == null) turnosDisponibles = new LinkedHashMap<>();
	 
    System.out.println("[asignacion_cencos.jsp]-1-"
        + " strReload= " + strReload);
    
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <title>(FEMASE)Asignacion turnos a centros de costo</title>
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
            
        <%}%>
    });
	
	function loadTurnos(){
            var comboCenco = document.getElementById("cencoId");
            var cencoSelected = comboCenco.value;
            if (cencoSelected !== '-1'){
                    var labelCenco = comboCenco.options[comboCenco.selectedIndex].text;
                    document.getElementById("labelCenco").value = labelCenco;
                    document.asignacionccForm.submit();
            }else {
                    alert('Seleccione un centro de costo...');
                    return false;
            }
	}
	
	function guardarAsignacion(){
            document.getElementById("action").value = "guardar_asignacion";
            selectAllTurnos();
            document.asignacionccForm.submit();
	}

	function selectAllTurnos() { 
            var selectBox = document.getElementById("list2");
            for (var i = 0; i < selectBox.options.length; i++) { 
                selectBox.options[i].selected = true; 
            } 
	}        
</script>
</head>

<body>
 <form name="asignacionccForm" action="<%=request.getContextPath()%>/servlet/AsignacionTurnosCencosServlet">
<div class="container">
  <p class="header h1">Asignaci&oacute;n de turnos a centros de costo</p>
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
                        valueCenco = auxCenco.getEmpresaId() + "--" + auxCenco.getDeptoId() + "--" + auxCenco.getCcostoId();
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
              <input type="button" name="ver" id="ver" value="Ver turnos" class="button button-blue" onClick="loadTurnos()">
            </span>
              <input name="action" type="hidden" id="action" value="list_turnos">
              <input name="labelCenco" type="hidden" id="labelCenco" value=""></td>
          </tr>
        </table>
      </div>
    </div>
  
    <div class="panel panel-default">
      <div class="panel-heading">Selecci&oacute;n de turnos &nbsp;<%=strLabelCenco%></div>
      <div class="panel-body">
        <table width="90%" border="0">
          <tr>
            <td width="61%" class="h4">Turnos existentes</td>
            <td width="3%">&nbsp;</td>
            <td width="36%" class="h4">Turnos seleccionados</td>
          </tr>
          <tr>
            <td rowspan="3" valign="top">
              <select name="list1" size="8" 
                      multiple="multiple" class="dropdown-header" id="list1" style="width:400px" rows=2>
                <%
                    int intValue = -1;
                    String strLabel = "";
                    Iterator<TurnoVO> it1 = turnosDisponibles.values().iterator();
                    while(it1.hasNext() ) {
                        TurnoVO auxTurno = it1.next();
                        intValue = auxTurno.getId();
                        strLabel = auxTurno.getNombre();
                        strLabel = "["+auxTurno.getId()+"] "+strLabel.toUpperCase();
                    %>
                        <option value="<%=intValue%>"><%=strLabel%></option>
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
                    int intValue2 = -1;
                    String strLabel2 = "";    
                    Iterator<TurnoVO> it2 = turnosAsignados.values().iterator();
                    while(it2.hasNext() ) {
                        TurnoVO auxTurno2 = it2.next();
                        intValue2 = auxTurno2.getId();
                        strLabel2 = auxTurno2.getNombre();
                        strLabel2 = "[" + auxTurno2.getId() + "] "+ strLabel2.toUpperCase();
                        
                    %>
                        <option value="<%=intValue2%>"><%=strLabel2%></option>
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
          <tr>
            <td valign="top">&nbsp;</td>
            <td height="21" valign="top">&nbsp;</td>
            <td valign="top"><span class="col-md-4">
              <input type="button" name="btn_preview2" id="btn_preview2" value="Guardar" 
              class="button button-blue" onClick="guardarAsignacion()">
            </span></td>
          </tr>
        </table>
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
