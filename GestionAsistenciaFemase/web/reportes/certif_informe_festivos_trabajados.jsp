<%@page import="cl.femase.gestionweb.vo.UsuarioCentroCostoVO"%>
<%@ include file="/include/check_session.jsp" %>

<%@page import="cl.femase.gestionweb.vo.UsuarioVO"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>

<%
    UsuarioVO theUser	= (UsuarioVO)session.getAttribute("usuarioObj");
    List<UsuarioCentroCostoVO> cencos 
        = (List<UsuarioCentroCostoVO>)session.getAttribute("cencos_empleado");
%>

<!DOCTYPE html>

<html>
<head>
    <meta charset="utf-8" />

    <meta name="keywords" content="femase,gestion,web">
    <meta name="description" content="Sistema de Gestion FEMASE-RRHH-Control Asistencia">
    <meta name="author" content="Adiazr">

    <title>Reporte domingos y festivos</title>
    
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
             
            //evento para combo centro costo
            $('#cencoId').change(function(event) {
                 //var tokenCenco = $('select#cencoId').val().split("|");
                 var empresaSelected  = null;
                 var deptoSelected    = null;
                 var cencoSelected    = $('select#cencoId').val();
                 
                 var sourceSelected = 'informe_festivos_trabajados';
                 $.get('<%=request.getContextPath()%>/JsonListServlet', {
                         empresaID : empresaSelected,deptoID : deptoSelected,cencoID : cencoSelected,source: sourceSelected
                 }, function(response) {
                        var select = $('#rutEmpleado');
                        select.find('option').remove();
                        var newoption = "";
                        newoption += "<option value='-1' selected>Seleccione Empleado</option>";
                        for (i=0; i<response.length; i++) {
                           var auxNombre = '['+response[i].rut+'] '+response[i].nombres + 
                                ' ' + response[i].apePaterno + ' '+response[i].apeMaterno;
                            newoption += "<option value='" + response[i].rut + "'>" + auxNombre + "</option>";
                        }
                        $('#rutEmpleado').html(newoption);
                 });
                 /*
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
                            var labelTurno = '[' + idTurno+'] '+response2[i2].nombre;
                            newoption += "<option value='" + idTurno + "'>" + labelTurno + "</option>";
                        }
                        $('#turno').html(newoption);
                });*/
            });
             
            $('#dialog1').hide();
            $('#dialog2').hide();
            $('#dialog3').hide();
			
        });
       
        function validaForm() {
            var isOk=true;
            var cencoId = $('#cencoId').val();
            var rutEmpleado = $('#rutEmpleado').val();
            var startDate = $('#fechaInicioAsStr').val();
            var endDate = $('#fechaFinAsStr').val();
           
            if (cencoId === '-1'){
                //alert('Seleccione centro de costo');
                $("#dialog1").dialog({
                    show: {effect: 'fade', speed: 1000},
                    hide: {effect: 'fade', speed: 1000},
                    width: 250,
                    height: 150,
                    open: function( event, ui ) {
                      $("#dialog1").closest("div[role='dialog']").css({top:120,left:200});              
                    }
                });
                $('#cencoId').focus();
                isOk=false;
            }
            //alert('rutEmpleado: '+rutEmpleado);
            if (isOk===true && rutEmpleado === '-1'){
                //alert('Seleccione empleado');
                $("#dialog3").dialog({
                    show: {effect: 'fade', speed: 1000},
                    hide: {effect: 'fade', speed: 1000},
                    width: 250,
                    height: 150,
                    open: function( event, ui ) {
                      $("#dialog3").closest("div[role='dialog']").css({top:120,left:200});              
                    }
                });
                $('#rutEmpleado').focus();
                isOk=false;
            }
		   
            if (isOk){
                if (startDate === '' || endDate === ''){
                    $("#dialog2").dialog({
                        show: {effect: 'fade', speed: 1000},
                        hide: {effect: 'fade', speed: 1000},
                        width: 250,
                        height: 150,
                        open: function( event, ui ) {
                            $("#dialog2").closest("div[role='dialog']").css({top:120,left:200});              
                        }
                    });
                    $('#fechaInicioAsStr').focus();
                    isOk=false;
                }
            }
			
            if (isOk){
                return true;	
            }else return false;
			
        }
		
		/**
		*
		*/
		function selectAll(selectBox,selectAll) { 
			// have we been passed an ID 
			if (typeof selectBox == "string") { 
				selectBox = document.getElementById(selectBox);
			} 
			// is the select box a multiple select box? 
			if (selectBox.type == "select-multiple") { 
				for (var i = 0; i < selectBox.options.length; i++) { 
					 selectBox.options[i].selected = selectAll; 
				} 
    		}
    	}
        
    </script>
    
    <script type="text/javascript">
        var config = {
          '.chosen-select'           : {},
          '.chosen-select-deselect'  : {allow_single_deselect:true},
          '.chosen-select-no-single' : {disable_search_threshold:10},
          '.chosen-select-no-results': {no_results_text:'Oops, nothing found!'},
          '.chosen-select-width'     : {width:"95%"}
        };
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
                    font-size: 21px;
                    margin-top: 20px;
                    margin-left: 20px;
		}
    </style>
</head>
<body>
    
        <div style="position:relative">
            <h4>Reporte domingos y festivos
            </h4><br>
            
        </div>
	<div class="content-container">
            
            <div class="padded-content-container">
                
    <!-- Filtros de busqueda INICIO-->                
    <div class="filtering">
        <form id="demo-form" 
              action="<%=request.getContextPath()%>/ReporteDiasFestivosTrabajados" 
              method="POST" 
              onsubmit="return validaForm();">
            <div id="content2" align="left">
               <div id="col1"></div>
            </div>
            <div id="content3" align="left">
            </div>
            <div id="content4" align="left">    
                <div id="col1"><label>Centro Costo</label></div>
                <div id="col2">    
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
                    }%>
                    </select>
            	</div>
            </div>
            <!-- fin div cenco -->
            <!-- inicio div empleado -->
            <div id="content4" align="left">    
                <div id="col1">
                    <label>Empleado</label>
                </div>
                <div id="col1">
                    &nbsp;
                    <!--
                    <label><input type="button" name="Button" value="Seleccionar todos" 
                        onclick="selectAll(document.getElementById('rutEmpleado'),true)" />
                    </label>
                    -->
                </div>
                <div id="col2">    
                	<select name="rutEmpleado" size="10" id="rutEmpleado" multiple>
                        <option value='-1' selected>Seleccione Empleado</option>
					</select>
            	</div>
            </div>
            <!-- fin div rut empleado -->
            <!-- inicio div turno -->
            <div id="content5" align="left">
            	<div id="col1">&nbsp;<!--<label>Turno (normal)</label>--></div>
                <div id="col2">            
                    <input type="hidden" id="turno" name="turno" value="-1">
                    <!--
                    <select id="turno" name="turno" style="width:350px;" required>
                        <option value="-1" selected>Cualquiera</option>
                    </select>
                    -->
                </div>
            </div> 
            <!-- fin div turno -->

            <div id="content6" align="left">            
                <div id="col1"><label>Fecha desde:</label></div>
                <div id="col2"><input name="fechaInicioAsStr" type="text" id="fechaInicioAsStr"></div>   
            </div>
            <div id="content7" align="left">            
                <div id="col1"><label>Fecha hasta:</label></div>
                <div id="col2"><input name="fechaFinAsStr" type="text" id="fechaFinAsStr"></div>   
            </div>    
            <!-- inicio div formato -->
            <div id="content6" align="left">
            	<div id="col1"><label>Formato archivo</label></div>
                <div id="col2">            
                    <select id="formato" name="formato" style="width:90px;" required>
                        <option value="csv" selected>CSV</option>
                        <option value="xls">Excel(xls)</option>
                        <option value="xml">XML</option>
                        <option value="pdf">PDF</option>
                    </select>
                </div>
            </div> 
            <!-- fin div formato -->    
              
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
          <p>Seleccione centro de costo</p>
        </div>
        <div id="dialog2" title="Validacion">
          <p>Seleccione rango de fechas</p>
        </div>
        <div id="dialog3" title="Validacion">
          <p>Seleccione empleado</p>
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
            $('#fechaInicioAsStr').datepick({dateFormat: 'yyyy-mm-dd'});
            $('#fechaFinAsStr').datepick({dateFormat: 'yyyy-mm-dd'});

        });

    </script>
  
</body>
</html>

