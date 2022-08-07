<%@page import="cl.femase.gestionweb.vo.RegistroHistoricoVO"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<%
	//tabla seleccionada
	String tabla = (String)request.getAttribute("tabla");
        String startDate    = (String)request.getAttribute("startDate");
        String endDate      = (String)request.getAttribute("endDate");
	RegistroHistoricoVO ultimoRegistro = (RegistroHistoricoVO)request.getAttribute("ultimoRegistro");
	RegistroHistoricoVO registroVistaPrevia = (RegistroHistoricoVO)request.getAttribute("vista_previa");
        RegistroHistoricoVO resumenEjecucion = (RegistroHistoricoVO)request.getAttribute("resumen");
	if (startDate==null) startDate="";
        if (endDate==null) endDate="";
%>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Documento sin título</title>


 	<script type="text/javascript" src="<%=request.getContextPath()%>/jquery-multiple_dates-js/jquery-2.2.4.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/jquery-multiple_dates-js/ui/1.12.1/jquery-ui.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/jquery-multiple_dates-js/ui/1.12.1/jquery-ui.multidatespicker.js"></script>

    <link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
    <script src="https://code.jquery.com/jquery-1.12.4.js"></script>
    <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>

<script src="<%=request.getContextPath()%>/jquery-plugins/datepicker/js/jquery.plugin.min.js"></script>
<script src="<%=request.getContextPath()%>/jquery-plugins/datepicker/js/jquery.datepick.js"></script>
<link href="<%=request.getContextPath()%>/jquery-plugins/datepicker/css/jquery.datepick.css"rel="stylesheet">


<script>

	function enviar(accion){
		document.getElementById("accion").value=accion;
		document.getElementById("myForm").submit();
	}

</script>

	
<style>
	* {
	  box-sizing: border-box;
	}
	body {
	  font-family: Verdana, Geneva, sans-serif;
	  background: #EEE;
	  color: #ecf0f1;
	  line-height: 1.618em;
	}
	.wrapper {
	  max-width: 50rem;
	  width: 100%;
	  margin: 0 auto;
	}
	.tabs {
		position: relative;
		margin: 3rem 0;
		background: #195598;
		height: 14.75rem;
		width: 1000px;
	}
	.tabs::before,
	.tabs::after {
	  content: "";
	  display: table;
	}
	.tabs::after {
	  clear: both;
	}
	.tab {
	  float: left;
	}
	.tab-switch {
	  display: none;
	}
	.tab-label {
	  position: relative;
	  display: block;
	  line-height: 2.75em;
	  height: 3em;
	  padding: 0 1.618em;
	  background: #195598;
	  border-right: 0.125rem solid #c2dbd6;
	  color: C00;
	  cursor: pointer;
	  top: 0;
	  transition: all 0.25s;
	}
	.tab-label:hover {
	  top: -0.25rem;
	  transition: top 0.25s;
	}
	.tab-content {
		height: 346px;
		position: absolute;
		z-index: 1;
		top: 2.75em;
		left: 0;
		padding: 1.618rem;
		background: #fff;
		color: #2c3e50;
		border-bottom: 0.25rem solid #bdc3c7;
		opacity: 0;
		transition: all 0.35s;
		width: 1000px;
	}
	.tab-switch:checked + .tab-label {
	  background: #fff;
	  color: #2c3e50;
	  border-bottom: 0;
	  border-right: 0.125rem solid #fff;
	  transition: all 0.35s;
	  z-index: 1;
	  top: -0.0625rem;
	}
	.tab-switch:checked + label + .tab-content {
	  z-index: 2;
	  opacity: 1;
	  transition: all 0.35s;
	}
	.wrapper .tabs .tab #tab1 table tr td {
		font-family: Verdana, Geneva, sans-serif;
		color: #000;
	}
</style>

</head>

<body>
<form id="myForm" name="myForm" method="post" action="<%=request.getContextPath()%>/servlet/TransferToHistoricalTables">
    <div class="wrapper">
      <h1>Traspaso a historicos</h1>
      <div class="tabs">
        <div class="tab">
          <input type="radio" name="css-tabs" id="tab-1" checked class="tab-switch">
          <label for="tab-1" class="tab-label">&Uacute;ltimo registro</label>
          <div class="tab-content" id="tab1">
            <table width="100%" border="0" cellspacing="1" cellpadding="1">
              <tr>
                <td>
                    <input type="radio" name="tabla" value="marca" id="checkbox_marca"  checked="checked" />Marcas
                </td>
                <td> 
                  <input type="radio" name="tabla" value="marca_rechazo" id="checkbox_marca_rechazo" />Marcas Rechazadas</label>
                </td>
                <td>
                    <input type="radio" name="tabla" value="detalle_ausencia" id="checkbox_detalle_ausencia" />Ausencias (Justificaciones)
                </td>
                <td>
                    <input type="radio" name="tabla" value="detalle_asistencia" id="checkbox_detalle_asistencia" />C&aacute;lculos de Asistencia
                </td>
                <td>
                    <input type="radio" name="tabla" value="mantencion_evento" id="checkbox_mantencion_evento" />Log de auditoria
                </td>
              </tr>
              <tr>
                <td colspan="2"><input type="button" name="button" id="button" value="Ver ultimo registro" onclick="enviar('lastrow')"/></td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
              </tr>
            </table>
            <table width="100%" border="0" cellspacing="1" cellpadding="1">
              <tr>
                <td bgcolor="#3300CC"><table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
                  <tr>
                    <td align="center" bgcolor="#2cd5d9">&Uacute;ltimo registro hist&oacute;rico</td>
                    <td align="center" bgcolor="#FFFFFF">&nbsp;</td>
                    <td align="center" bgcolor="#FFFFFF">&nbsp;</td>
                  </tr>
                  <tr>
                    <td align="center" bgcolor="#2cd5d9">Tabla</td>
                    <td align="center" bgcolor="#2cd5d9">Fecha del registro</td>
                    <td align="center" bgcolor="#2cd5d9">Fecha-hora traspaso</td>
                  </tr>
					<%if (ultimoRegistro != null){%>                  
                          <tr>
                            <td bgcolor="#FFFFFF"><%=ultimoRegistro.getTabla()%></td>
                            <td align="center" bgcolor="#FFFFFF"><%=ultimoRegistro.getFechaRegistro()%></td>
                            <td align="center" bgcolor="#FFFFFF"><%=ultimoRegistro.getFechaHoraTraspaso()%></td>
                          </tr>
                    <%}%>
                </table></td>
              </tr>
            </table>
          </div>
        </div>
        <div class="tab">
          <input type="radio" name="css-tabs" id="tab-2" class="tab-switch">
          <label for="tab-2" class="tab-label">Vista previa</label>
          <div class="tab-content" id="tab2">
            <table width="100%" border="0" cellspacing="1" cellpadding="1">
              <tr>
                <td>Fecha desde</td>
                <td>
                  <label for="startDate"></label>
                  <input type="text" name="startDate" id="startDate" value="<%=startDate%>"/>
                </td>
                <td>Fecha hasta</td>
                <td><input type="text" name="endDate" id="endDate" value="<%=endDate%>"/></td>
                <td><input type="button" name="button2" id="button2" value="Vista previa" onclick="enviar('preview')"/></td>
              </tr>
            </table>
            <table width="60%" border="0" align="center" cellpadding="1" cellspacing="1">
              <tr>
                <td bgcolor="#3300CC"><table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
                  <tr>
                    <td colspan="2" align="left" bgcolor="#2cd5d9">Vista previa de registros a traspasar</td>
                  </tr>
                  <tr>
                    <td align="center" bgcolor="#2cd5d9">Tabla</td>
                    <td align="center" bgcolor="#2cd5d9">Registros por traspasar</td>
                  </tr>
                  <%if (registroVistaPrevia != null){%>
                      <tr>
                        <td bgcolor="#FFFFFF"><%=registroVistaPrevia.getTabla()%></td>
                        <td align="right" bgcolor="#FFFFFF"><%=registroVistaPrevia.getNumRegistrosAsStr()%>&nbsp;</td>
                      </tr>
                  <%}%>
                  <tr>
                    <td bgcolor="#FFFFFF">&nbsp;</td>
                    <td align="right" bgcolor="#FFFFFF">
                    <%if (registroVistaPrevia != null){%>
                    	<input type="button" name="button3" id="button3" value="Ejecutar Traspaso" onclick="enviar('execute')"/></td>
                    <%}%>
                  </tr>
                </table></td>
              </tr>
            </table>
          </div>
        </div>
        <div class="tab">
          <input type="radio" name="css-tabs" id="tab-3" class="tab-switch">
          <label for="tab-3" class="tab-label">Resultado</label>
          <div class="tab-content" id="tab3">
            <table width="60%" border="0" align="center" cellpadding="1" cellspacing="1">
              <tr>
                <td bgcolor="#3300CC"><table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
                  <tr>
                    <td colspan="2" align="left" bgcolor="#2cd5d9">Resumen de registros a hist&oacute;rico</td>
                  </tr>
                  <tr>
                    <td align="center" bgcolor="#2cd5d9">Tabla</td>
                    <td align="center" bgcolor="#2cd5d9">Registros insertados</td>
                  </tr>
                    <%if (resumenEjecucion != null){%>
                        <tr>
                          <td bgcolor="#FFFFFF"><%=resumenEjecucion.getTabla()%></td>
                          <td align="right" bgcolor="#FFFFFF"><%=resumenEjecucion.getNumRegistrosInsertadosAsStr()%>&nbsp;</td>
                        </tr>
                    <%}%>
                </table></td>
              </tr>
          </table>
            <p>&nbsp;</p>
          </div>
        </div>
        <input type="hidden" name="accion" id="accion" />
      </div>
      
    </div>
</form>

<script type="text/javascript">
        var config = {
          '.chosen-select'           : {},
          '.chosen-select-deselect'  : {allow_single_deselect:true},
          '.chosen-select-no-single' : {disable_search_threshold:10},
          '.chosen-select-no-results': {no_results_text:'Oops, nothing found!'},
          '.chosen-select-width'     : {width:"95%"}
        }
        for (var selector in config) {
          $(selector).chosen(config[selector]);
        }
		
		
    </script>
  
    <script type="text/javascript">
        
        $(document).ready(function() {
            <%if (tabla != null){%>
            	document.getElementById("checkbox_<%=tabla%>").checked = true;
			<%}%>
         });

         $.datepicker.regional['es'] = {
            closeText: 'Cerrar',
            prevText: '< Ant',
            nextText: 'Sig >',
            currentText: 'Hoy',
            monthNames: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'],
            monthNamesShort: ['Ene','Feb','Mar','Abr', 'May','Jun','Jul','Ago','Sep', 'Oct','Nov','Dic'],
            dayNames: ['Domingo', 'Lunes', 'Martes', 'Miercoles', 'Jueves', 'Viernes', 'Sabado'],
            dayNamesShort: ['Dom','Lun','Mar','Mie','Juv','Vie','Sab'],
            dayNamesMin: ['Do','Lu','Ma','Mi','Ju','Vi','Sa'],
            weekHeader: 'Sm',
            dateFormat: 'dd/mm/yy',
            firstDay: 1,
            isRTL: false,
            showMonthAfterYear: false,
            yearSuffix: ''
        };
        $.datepicker.setDefaults($.datepicker.regional['es']);
    
        $(function() {
            $('#startDate').datepick({dateFormat: 'yyyy-mm-dd',maxDate: 0});
			$('#endDate').datepick({dateFormat: 'yyyy-mm-dd',maxDate: 0});
        });
        
        
    </script>

</body>
</html>
