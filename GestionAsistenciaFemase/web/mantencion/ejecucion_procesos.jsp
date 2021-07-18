<%@ include file="/include/check_session.jsp" %>
<%@page import="cl.femase.gestionweb.vo.CentroCostoVO"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.StringTokenizer"%>
<%@page import="java.util.ArrayList"%>
<%@page import="cl.femase.gestionweb.common.Utilidades"%>
<%@page import="java.util.Iterator"%>
<%@page import="cl.femase.gestionweb.vo.EmpresaVO"%>
<%@page import="cl.femase.gestionweb.vo.ProcesoFiltroVO"%>

<%@page import="java.util.List"%>
<%
    List<EmpresaVO> empresas            = (List<EmpresaVO>)session.getAttribute("empresas");
    
    String empresaProcesoSelected = (String)request.getAttribute("empresaProcesoSelected");
    int intProcesoSelected = -1; //(Integer)request.getAttribute("procesoSelected");
    if (request.getAttribute("procesoSelected")!=null){
        intProcesoSelected = (Integer)request.getAttribute("procesoSelected");
    }
    LinkedHashMap<String,List<CentroCostoVO>> cencos = 
        (LinkedHashMap<String,List<CentroCostoVO>>)session.getAttribute("allCencos");
    LinkedHashMap<String, ProcesoFiltroVO> filtros = (LinkedHashMap<String, ProcesoFiltroVO>)request.getAttribute("filtros"); 
	
    String procesoSelected = String.valueOf(intProcesoSelected);
    String mensaje = (String)request.getAttribute("mensaje");
    String mensajeError = (String)request.getAttribute("mensajeError");
    
%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8">
  	
	<script type="text/javascript" src="<%=request.getContextPath()%>/jquery-multiple_dates-js/jquery-2.2.4.js"></script>
 	<script type="text/javascript" src="<%=request.getContextPath()%>/jquery-multiple_dates-js/ui/1.12.1/jquery-ui.min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/jquery-multiple_dates-js/ui/1.12.1/jquery-ui.multidatespicker.js"></script>

	<!-- bibliotecas js para monthpicker -->
	<script type="text/javascript" src="<%=request.getContextPath()%>/jquery-monthpicker-js/CalendarControl.js"></script>
    
	<!-- estilos css-->
  <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/jquery-multiple_dates-css/result-light.css">    
  <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/jquery-multiple_dates-css/jquery-ui.css">
  <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/jquery-multiple_dates-css/jquery-ui.multidatespicker.css">
  <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/jquery-plugins/chosen_v1.6.2/chosen.css">
  <!-- estilo para botones -->
  <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/jquery-plugins/chosen_v1.6.2/docsupport/style.css">
    
  <!-- Estilos css para monthpicker -->
  <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/jquery-monthpicker-css/StyleCalendar.css">
    
  <style type="text/css">
  	#contenidos #columna1 label {
		font-size: medium;
		vertical-align: top;
	}
	
	.labelSeccion {
		font-size: medium;
		font-weight: bold;
		font-variant: small-caps;
	}
  	.tituloForm {
		font-size: x-large;
		font-style: normal;
		color: #036;
		text-decoration: underline;
		font-variant: small-caps;
        font-size: x-large;
	}
	<!-- estilo para month picker-->
	.month-year-input {
	  width: 60px;
	  margin-right: 2px;
	}
  </style>
  
   <style type="text/css">
		#contenedor {
			display: table;
			border: 0px solid #ffffff;
			width: 900px;
			text-align: center;
			margin: 0 auto;
		}
		#contenidos {
			display: table-row;
		}
		#columna1, #columna2, #columna3 {
			display: table-cell;
			border: 1px solid #ffffff;
			vertical-align: top;
			padding: 10px;
			color: #036
		}
  </style>
  
  <title>Ejecucion de procesos</title>

	<script type="text/javascript">
 
        function validaProceso()
        {
            var seleccionOk = true;
            var listaProcesos = document.getElementById("idProceso");
            if (listaProcesos.value === '-1'){
                alert("Seleccione proceso");	
                document.getElementById("empresaProceso").focus();
                seleccionOk = false;
            }
            return seleccionOk;
        }
	
        function ejecutar(){
            if (validaProceso()){
                document.form1.action.value = 'ejecutar_proceso';
                if (document.getElementById("centro_costo"))
                if (document.getElementsByName("centro_costo").length > 0) {
                    // then field2 does not exist
                    var items = document.getElementById("centro_costo").options.length;
                    $("#centro_costo option").prop("selected",true);
                }
                
                
                document.form1.submit();
            }
        }
	
        function loadFiltros(){
            if (validaProceso()){
                document.form1.action.value = 'load_filtros';
                document.form1.submit();
            }
        }
	
    </script>

</head>

<body>
  
    <form name="form1" method="post" action="<%=request.getContextPath()%>/ProcesosController">
        <p></p>
        <span class="tituloForm">Ejecucion Procesos</span>
        <p></p>
        <div id="contenidos" >
            <div id="columna1">
                <label>1.- Empresa
                  <select name="empresaProceso" id="empresaProceso">
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
            </label>
            </div>
            <div id="columna2" >
              <label for="idProceso">2.- Proceso</label>
              <select id="idProceso" name="idProceso" style="width:350px;" tabindex="2" onChange="loadFiltros()">
                <option value="-1" selected>----------</option>
              </select>
            </div>
        </div>
        
        <div id="contenidos" >
            <div id="columna1">
              <span class="labelSeccion">
                <label for="labelDLAB2">Filtros para la ejecuci&Oacute;n</label>
              </span></div>
            <div id="columna2">
             &nbsp;
            </div>
            
	  	</div>
        
        <div id="contenidos" >
            <div id="columna1">Nombre filtro</div>
            <div id="columna2">Valor</div>
	  	</div>
        
        	<%if (filtros!=null && !filtros.isEmpty()){
                    int iteracion=0;
                    Set<String> keys = filtros.keySet();
                    for(String k:keys){
                        ProcesoFiltroVO filtro = filtros.get(k);
                        iteracion++;
			%>
                            
                        <%if (!filtro.isIsList()){ 
                            if (!filtro.isIsCheckbox()){ 
                            %>    
                        	<div id="contenidos" >
                                <div id="columna1"><%=filtro.getLabel()%></div>
                                <div id="columna2">
                                    <input type="text" 
                                        name="<%=filtro.getCode()%>"
                                        id="<%=filtro.getCode()%>"
                                        value="<%=filtro.getDefaultValue()%>"><%=filtro.getFormat()%></div>
                                </div>
                            <%}else{%>
                                <div id="contenidos" >
                                <div id="columna1"><%=filtro.getLabel()%></div>
                                <div id="columna2">
                                    <input type="checkbox" name="<%=filtro.getCode()%>" value="S">
                                </div>
                            <%}%>
                        <%}else if (filtro.getSourceTable().compareTo("empresa") == 0){%>
                                <div id="contenidos" >
                                <div id="columna1"><%=filtro.getLabel()%></div>
                                <div id="columna2">
                                    <select name="<%=filtro.getCode()%>" id="<%=filtro.getCode()%>">
                                    <option value="-1" selected>----------</option>
                                    <%
                                        iteraempresas = empresas.iterator();
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
                        <%}else if (filtro.getSourceTable().compareTo("centro_costo") == 0){%>
                                <div id="contenidos" >
                                    <div id="columna1"><%=filtro.getLabel()%></div>
                                    <div id="columna2">
                                        <select name="<%=filtro.getCode()%>" id="<%=filtro.getCode()%>" multiple style="width:500px" size="12">
                                            <option value="-1" selected>----------</option>
                                            <%
                                            Set<String> keyscencos = cencos.keySet();
                                            for(String cencokey:keyscencos){
                                                List<CentroCostoVO> cencosList = cencos.get(cencokey);
                                                Iterator<CentroCostoVO> iteracencos = cencosList.iterator();
                                                iteracencos = cencosList.iterator();
                                                while(iteracencos.hasNext() ) {
                                                    CentroCostoVO cencoit = iteracencos.next();
                                                    String label = "[" + cencoit.getEmpresaNombre() 
                                                        + "-" + cencoit.getDeptoNombre() 
                                                        + "-" + cencoit.getNombre() + "]"; 
                                                    %>
                                                    <option value="<%=cencoit.getId()%>"><%=label%></option>
                                                <%}//fin while
                                            }//fin for %>
                                        </select>
                                    </div>
                                </div>
                        <%}//fin if filtro centro costo%>
                <%}//fin for keys%> 
            <%}//fin if filtros != null%>    
        <div id="contenidos" >
            <div id="columna1"></div>
            <div id="columna2"><a class="button button-blue" href="javascript:;" onclick="ejecutar();">Ejecutar</a></div>
	  	</div>
          <input name="empresaSelected" id="empresaSelected" type="hidden" value="<%=empresaProcesoSelected%>">
            <input name="procesoSelected" id="procesoSelected" type="hidden" value="<%=procesoSelected%>">
       <input name="action" type="hidden" id="action" value="ejecutar_proceso">    
       
       <div id="dialog-message" title="Programacion proceso">
           <%if (mensaje != null && mensaje.compareTo("") != 0){%>
                <p>
                    <span class="ui-icon ui-icon-circle-check" style="float:left; margin:0 7px 50px 0;"></span>
                    <%=mensaje%>
                </p>
            <%}%>    
            <%if (mensajeError != null && mensajeError.compareTo("") != 0){%>
                <p>
                    <b><%=mensajeError%></b>.
                </p>
            <%}%>    
</div>
    </form>
    
    <script type="text/javascript" src="<%=request.getContextPath()%>/jquery-plugins/chosen_v1.6.2/chosen.jquery.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/jquery-plugins/chosen_v1.6.2/docsupport/prism.js" charset="utf-8"></script>
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
             //Evento al seleccionar combo de empresa: cargar procesos definidos
             $('#empresaProceso').change(function(event) {
                 var auxempresaSelected = $("select#empresaProceso").val();
                 //alert('empresa selected: '+auxempresaSelected);
                 $.get('<%=request.getContextPath()%>/JsonListServlet', {
                        empresaProcesoID : auxempresaSelected,source: 'programacionProceso'
                 }, function(response) {
                        var select = $('#idProceso');
                        select.find('option').remove();
                        var newoption = "";
                        //var keyoption="";
                        var labeloption="";
                        newoption += "<option value='-1'>Seleccione Proceso</option>";
                        for (i=0; i<response.length; i++) {
                            //keyoption = response[i].empresaId+'|'+response[i].id;
                            labeloption = response[i].nombre+' ['+response[i].jobName+']';
                            //newoption += "<option value='"+keyoption+"'>"+labeloption+"</option>";
                            if (response[i].id==='<%=procesoSelected%>'){
                                newoption += "<option value='"+response[i].id+"' id='"+response[i].id+"' selected>"+labeloption+"</option>";
                            }else{
                                newoption += "<option value='"+response[i].id+"' id='"+response[i].id+"'>"+labeloption+"</option>";
                            }
                        }
                        
                        $('#idProceso').html(newoption);
                        setProcesoSelected();
                });
            });
            loadValues();
	
            <%if ( (mensaje != null && mensaje.compareTo("") != 0) 
                || (mensajeError != null && mensajeError.compareTo("") != 0)){%>
                    $('#dialog-message').dialog({
                        modal: true,
                        buttons: {
                            Ok: function() {
                              $( this ).dialog( "close" );
                            }
                        }
                    });
            <%}%>
         });

        function loadValues(){
            //empresa
            $("#empresaProceso").val('<%=empresaProcesoSelected%>');
            $("#empresaProceso").change();
           
        }
        
        function setProcesoSelected(){
            var procesoSelected='<%=procesoSelected%>';
            //alert('[setProcesoSelected]Empresa: <%=empresaProcesoSelected%>'+', procesoId: <%=procesoSelected%>');
            $("#idProceso").val(<%=procesoSelected%>);
            //$("#idProceso").val(procesoSelected).trigger("change");
            //$("#idProceso").change();
        }
        
    </script>
</body>

</html>

