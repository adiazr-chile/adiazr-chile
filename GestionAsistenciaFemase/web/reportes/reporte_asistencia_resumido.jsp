<%@ include file="/include/check_session.jsp" %>

<%@page import="cl.femase.gestionweb.vo.TurnoVO"%>
<%@page import="cl.femase.gestionweb.vo.UsuarioVO"%>
<%@page import="cl.femase.gestionweb.vo.CargoVO"%>
<%@page import="cl.femase.gestionweb.vo.CentroCostoVO"%>
<%@page import="cl.femase.gestionweb.vo.DepartamentoVO"%>
<%@page import="cl.femase.gestionweb.vo.EmpresaVO"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>

<%
    UsuarioVO theUser	= (UsuarioVO)session.getAttribute("usuarioObj");
    List<EmpresaVO> empresas = (List<EmpresaVO>)session.getAttribute("empresas");
    List<CargoVO> cargos = (List<CargoVO>)session.getAttribute("cargos");
    //List<TurnoVO> turnos   = (List<TurnoVO>)session.getAttribute("turnos_cencos");
%>

<!DOCTYPE html>

<html>
<head>
    <meta charset="utf-8" />

    <meta name="keywords" content="femase,gestion,web">
    <meta name="description" content="Sistema de Gestion FEMASE-RRHH-Control Asistencia">
    <meta name="author" content="Adiazr">

    <title>Reporte de Asistencia Resumido (CSV)</title>
    
    <!--
    <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.4/jquery.min.js"></script>
    -->
    <script src="../Jquery-JTable/Scripts/jquery-1.9.1.min.js" type="text/javascript"></script>
    <script src="../Jquery-JTable/Scripts/jquery-ui-1.10.0.min.js" type="text/javascript"></script>
    <script type="text/javascript" src="../jquery-plugins/chosen_v1.6.2/chosen.jquery.js"></script>
   
    <!-- estilo para botones -->
    <link rel="stylesheet" href="../jquery-plugins/chosen_v1.6.2/docsupport/style.css">
    <link rel="stylesheet" href="../jquery-plugins/chosen_v1.6.2/docsupport/prism.css">
    <link rel="stylesheet" href="../jquery-plugins/chosen_v1.6.2/chosen.css">
    
    <link rel="stylesheet" href="../Jquery-JTable/Content/themes/metroblue/jquery-ui.css">
    
    <!-- javascript y estilo para calendario datepicker  -->
    <script src="../jquery-plugins/datepicker/js/jquery.datepick.js"></script>
    <script src="../jquery-plugins/datepicker/js/jquery.plugin.min.js"></script>
    <script src="../jquery-plugins/datepicker/js/jquery.datepick.js"></script>
    <link href="../jquery-plugins/datepicker/css/jquery.datepick.css"rel="stylesheet">
    
    <script type="text/javascript">

        $(document).ready(function() {
             $('#empresa').change(function(event) {
                 var empresaSelected = $("select#empresa").val();
                 $.get('<%=request.getContextPath()%>/JsonListServlet', {
                         empresaID : empresaSelected
                 }, function(response) {
                        var select = $('#depto');
                        select.find('option').remove();
                        var newoption = "";
                        newoption += "<option value='-1'>Seleccione Departamento</option>";
                        for (i=0; i<response.length; i++) {
                            newoption += "<option value='"+response[i].id+"'>"+response[i].nombre+"</option>";
                        }
                        $('#depto').html(newoption);
                 });
             });
             //evento para combo depto
             $('#depto').change(function(event) {
                 var empresaSelected = $("select#empresa").val();
                 var deptoSelected = $("select#depto").val();
                 $.get('<%=request.getContextPath()%>/JsonListServlet', {
                         empresaID : empresaSelected,deptoID : deptoSelected
                 }, function(response) {
                        var select = $('#cenco');
                        select.find('option').remove();
                        var newoption = "";
                        newoption += "<option value='-1'>Seleccione Centro de costo</option>";
                        for (i=0; i<response.length; i++) {
                            newoption += "<option value='"+response[i].id+"'>"+response[i].nombre+"</option>";
                        }
                        $('#cenco').html(newoption);
                 });
             });
             
             //evento para combo centro costo
             $('#cenco').change(function(event) {
                var empresaSelected = $("select#empresa").val();
                var deptoSelected = $("select#depto").val();
                var cencoSelected = $("select#cenco").val();
                var sourceSelected = 'reporte_asistencia';
                /* 
                $.get('<%=request.getContextPath()%>/JsonListServlet', {
                    empresaID : empresaSelected,deptoID : deptoSelected,cencoID : cencoSelected,source: sourceSelected
                }, function(response) {
                    var select = $('#rut');
                    select.find('option').remove();
                    var newoption = "";
                    newoption += "<option value='todos'>Todos</option>";
                    for (i=0; i<response.length; i++) {
                        var auxNombre = '['+response[i].rut+'] '+response[i].nombres + 
                            ' ' + response[i].apePaterno + ' '+response[i].apeMaterno;
                        newoption += "<option value='" + response[i].rut + "'>" + auxNombre + "</option>";
                    }
                    $('#rut').html(newoption);
                 });
                 */
                sourceSelected = 'cargar_turnos_by_cenco';
                $.get('<%=request.getContextPath()%>/JsonListServlet', {
                    empresaID : empresaSelected,deptoID : deptoSelected,cencoID : cencoSelected,source: sourceSelected
                }, function(response2) {
                        var select = $('#turno');
                        select.find('option').remove();
                        var newoption = "";
                        newoption += "<option value='-1'>Cualquiera</option>";
                        for (i2 = 0; i2 <response2.length; i2++) {
                            var idTurno = response2[i2].id;
                            var labelTurno = response2[i2].nombre;
                            newoption += "<option value='" + idTurno + "'>" + labelTurno + "</option>";
                        }
                        $('#turno').html(newoption);
                });
            });
             
			 $('#dialog1').hide()
			 $('#dialog2').hide()
			 $('#dialog3').hide()
        });
       
        function validaForm() {
            var isOk=true;
            var empresaId = $('#empresa').val();
            var deptoId = $('#depto').val();
            var cencoId = $('#cenco').val();
            var startDate = $('#startDate').val();
            var endDate = $('#endDate').val();
           
		   	if (empresaId === '-1'){
				$("#dialog1").dialog({
                                    show: {effect: 'fade', speed: 1000},
                                    hide: {effect: 'fade', speed: 1000},
                                    width: 250,
                                    height: 150,
                                    open: function( event, ui ) {
                                      $("#dialog1").closest("div[role='dialog']").css({top:120,left:200});              
                                     }
                                });
				$('#empresa').focus();
				isOk=false;
				//return false;
			}else if (deptoId !== '-1' ){
					if (cencoId === '-1'){
						//alert('Seleccione centro de costo');
						$("#dialog2").dialog({
                                                    show: {effect: 'fade', speed: 1000},
                                                    hide: {effect: 'fade', speed: 1000},
                                                    width: 250,
                                                    height: 150,
                                                    open: function( event, ui ) {
                                                      $("#dialog2").closest("div[role='dialog']").css({top:120,left:200});              
                                                     }
                                                });
						$('#cenco').focus();
						isOk=false;
					}
			}
			if (isOk){
				if (startDate === '' || endDate === ''){
					$("#dialog3").dialog({
                                            show: {effect: 'fade', speed: 1000},
                                            hide: {effect: 'fade', speed: 1000},
                                            width: 250,
                                            height: 150,
                                            open: function( event, ui ) {
                                              $("#dialog3").closest("div[role='dialog']").css({top:120,left:200});              
                                             }
                                        });
					$('#startDate').focus();
					isOk=false;
				}
			}
			
			if (isOk){
				//alert('submit form');
				//$('#demo-form').submit();
				return true;	
			}else return false;
			
        }
        
    </script>
    
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
    <style>
        div.filtering
        {
            border: 1px solid #999;
            margin-bottom: 5px;
            padding: 10px;
            background-color: #EEE;
        }
        
        body {
            background-color: #fafafa;
            font-size: 10pt;
            font-style: normal;
			font-family: monospace;
            color: #06C
        }
		
		h4 {
		  font-family: 'Segoe UI Semilight', 'Open Sans', Verdana, Arial, Helvetica, sans-serif;
		  font-weight: 300;
		  font-size: 16px;
		}
    </style>
</head>
<body>
    
        <div style="position:relative">
            <h4>Reporte de Asistencia Resumido (CSV)
            </h4><br>
            
        </div>
	<div class="content-container">
            
            <div class="padded-content-container">
                
    <!-- Filtros de busqueda INICIO-->                
    <div class="filtering">
        <form id="demo-form" 
              action="<%=request.getContextPath()%>/AsistenciaReport" 
              method="POST" 
              onsubmit="return validaForm();">
            <div id="content2" align="left">
               <div id="col1"> <label for="empresa">Empresa</label></div>
               <div id="col2">     
                    <select name="empresa" id="empresa">
                    <option value="-1" selected>----------</option>
                    <%
                        Iterator<EmpresaVO> iteraempresas = empresas.iterator();
                        while(iteraempresas.hasNext() ) {
                            EmpresaVO auxempresa = iteraempresas.next();
                            %>
                            <option value="<%=auxempresa.getId()%>"><%=auxempresa.getNombre()%></option>
                            <%
                        }
                    %>
                    </select>
                </div>
            </div>
            <div id="content3" align="left">    
               <div id="col1"> 
               		<label for="depto">Departamento</label>
                </div>
               <div id="col2"> 
               		<select id="depto" name="depto" style="width:350px;" required>
                    	<option value="-1">--------</option>
                	</select>
               </div>
            </div>
            <div id="content4" align="left">    
                <div id="col1"><label>Centro Costo</label></div>
                <div id="col2">    
                	<select name="cenco" id="cenco">
                        <option value="-1" selected>----------</option>
                    </select>
            	</div>
            </div>
            <!-- fin div cenco -->
            <!-- inicio div empleado -->
            <!--<div id="content5" align="left">
            	<div id="col1"><label>Empleado</label></div>
				<div id="col2">            
                    <select name="rut" id="rut">
                        <option value="-1" selected>----------</option>
                    </select>
                </div>
            </div>--> 
            <!-- fin div rut empleado -->
            <!-- inicio div turno -->
            <div id="content5" align="left">
            	<div id="col1"><label>Turno</label></div>
                <div id="col2">            
                    <select id="turno" name="turno" style="width:350px;" required>
                        <option value="-1" selected>Cualquiera</option>
                    </select>
                </div>
            </div> 
            <!-- fin div turno -->

            <div id="content6" align="left">            
                <div id="col1"><label>Fecha desde:</label></div>
                <div id="col2"><input name="startDate" type="text" id="startDate"></div>   
            </div>
            <div id="content7" align="left">            
                <div id="col1"><label>Fecha hasta:</label></div>
                <div id="col2"><input name="endDate" type="text" id="endDate"></div>   
            </div>    
                
            <div id="content9" align="left">       
                <div id="col1"></div>
                <div id="col2">
                    <input type="hidden" id="tipo" name="tipo" value="3">
                    <!--
                	<select name="tipo" id="tipo" >
                            <option value="1" selected>Reporte Asistencia</option>
                            <option value="2">Detalle marcas</option>
                            <% if (theUser.getIdPerfil() == 1){%>
                                <option value="3">Asistencia Resumido (CSV)</option>
                            <%}%>
                        </select>
                    -->
                </div>    
             </div>   
			<br>
            <div id="content9" align="left">             
            	<div id="col1">
                    <!-- Boton Buscar-->  
                    <button type="submit" 
                        id="LoadRecordsButton"
                        name="LoadRecordsButton"
                        class="button button-blue">Generar Informe</button>
                  </div>
            </div>
        <div id="dialog1" title="Validacion">
          <p>Seleccione empresa</p>
        </div>
        <div id="dialog2" title="Validacion">
          <p>Seleccione centro de costo</p>
        </div>
        <div id="dialog3" title="Validacion">
          <p>Seleccione rango de fechas</p>
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

