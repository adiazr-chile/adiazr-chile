<%@ include file="/include/check_session.jsp" %>
<%@page import="java.util.StringTokenizer"%>
<%@page import="java.util.ArrayList"%>
<%@page import="cl.femase.gestionweb.common.Utilidades"%>
<%@page import="java.util.Iterator"%>
<%@page import="cl.femase.gestionweb.vo.EmpresaVO"%>
<%@page import="java.util.List"%>
<%
    List<EmpresaVO> empresas            = (List<EmpresaVO>)session.getAttribute("empresas");
    
    String empresaProcesoSelected = (String)request.getAttribute("empresaProcesoSelected");
    int intProcesoSelected = -1; //(Integer)request.getAttribute("procesoSelected");
    if (request.getAttribute("procesoSelected")!=null){
        intProcesoSelected = (Integer)request.getAttribute("procesoSelected");
    }
    String procesoSelected = String.valueOf(intProcesoSelected);
    ArrayList<String> listaHoras = (ArrayList<String>)request.getAttribute("listaHoras");
    Integer diaSelected = (Integer)request.getAttribute("diaSelected");
    String mensaje = (String)request.getAttribute("mensaje");
    String mensajeError = (String)request.getAttribute("mensajeError");
    if (request.getAttribute("diaSelected")==null) diaSelected=0;
    if (listaHoras == null) listaHoras=new ArrayList();
	
    System.out.println("[GestionFemaseWeb]programar_proceso.jsp]"
        + "empresaSelected= " + empresaProcesoSelected
        + ", procesoSelected= " + procesoSelected
		+ ", diaSelected= " + diaSelected);
	
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
  
  <title>Programacion de procesos</title>

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
	
        function enviarDatos(){
            if (validaProceso()){
                $("#horas option").prop("selected",true);
                document.form1.action.value = 'programar_proceso';
                document.form1.submit();
            }
        }
	
        function loadProgramacion(){
            if (validaProceso()){
                document.form1.action.value = 'load_programacion';
                document.form1.submit();
            }
        }
	
	function showSelectedValues(){
            var valueSelected = document.getElementById("horas").value;
            document.getElementById("horaSelected").value = valueSelected;
            document.getElementById("horaSelectedHidden").value = valueSelected;
	}
	
	function listaQuitarElemSel(lista) {
            listaQuitarElemento(lista, lista.selectedIndex);
	}
	
	function listaQuitarElemento(lista, indice){
		if (indice >=0)  {
			for(x=indice; x< lista.options.length-1; x++) {
				lista.options[x] = new Option(lista.options[x+1].text, lista.options[x+1].value);
				if(lista.options[x+1].selected)
				lista.options[x].selected=true;
			}
			lista.options.length = lista.options.length-1;
		}
	}
	
	function actualizaItem(){
            var objListaHoras = document.getElementById("horas");
            var oldValue=document.getElementById("horaSelectedHidden").value;
            var newValue=document.getElementById("horaSelected").value;
            var indiceSelected = -1;
            for(x=0; x < objListaHoras.options.length; x++) {
                if (objListaHoras.options[x].value == oldValue) {
                    indiceSelected = x;
                    break;
                }
            }
            //alert('indiceSelected= '+indiceSelected);
            //eliminar elemento de 
            listaQuitarElemento(document.getElementById("horas"), indiceSelected);
            listaAddElemento(document.getElementById("horas"),newValue,newValue);
	}
	
	function eliminaItems(){
            //var txtSelectedValuesObj = document.getElementById('txtSelectedValues');
            var selectedArray = new Array();
            var selectedIndexArray = new Array();
            var selObj = document.getElementById('horas');
            var i;
            var count = 0;
            for (i=0; i<selObj.options.length; i++) {
                if (selObj.options[i].selected) {
                  selectedArray[count] = selObj.options[i].value;
                  selectedIndexArray[count] = i;
                  count++;
                }
            }
            //
            //txtSelectedValuesObj.value = selectedArray;
            if (selectedIndexArray.length > 0 ){
                for (j = 0;j < selectedIndexArray.length; j++){j
                    listaQuitarElemento(selObj, selectedIndexArray[j]);
                }
            }
		
            //alert('horas seleccionadas= '+selectedArray+', indices= '+selectedIndexArray);
	}
	
	function listaAddElemento (lista, valor, texto) {
            var largo = lista.length;
            if (!listaExisteValor(lista, valor)){
                lista.options[largo] = new Option();
                lista.options[largo].value = valor;
                lista.options[largo].text = texto;
            }
	}
	
	function listaExisteValor(lista, valor) {
            var existe = false;
            for(x=0; x< lista.options.length; x++) {
                if (lista.options[x].value == valor) {
                    existe = true;
                    break;
                }
            }
            return existe;
	}
	
	function clearValues(){
		$("#codDia").val(0);
		var list = document.getElementById('horas');
		while (list.firstChild) {
			list.removeChild(list.firstChild);
		}	
	}
	
    </script>

    <script type='text/javascript'>//<![CDATA[
	
        //cargar datos 
        window.onload=function(){
        

        };//]]> 

                
    </script>
</head>

<body>
  
    <form name="form1" method="post" action="<%=request.getContextPath()%>/ProcesosController">
        <p></p>
        <span class="tituloForm">Programacion Procesos</span>
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
              <select id="idProceso" name="idProceso" style="width:350px;" tabindex="2">
                <option value="-1" selected>----------</option>
              </select>
            </div>
        </div>
        
        <div id="contenidos" >
            <div id="columna1">
                <label>3.- Dia
                  	<select name="codDia" id="codDia" onChange="loadProgramacion()">
                        <option value="0" selected>Seleccione dia de la semana</option>
                        <option value="-1">Todos</option>
                        <option value="1">Lunes</option>
                        <option value="2">Martes</option>
                        <option value="3">Miercoles</option>
                        <option value="4">Jueves</option>
                        <option value="5">Viernes</option>
                        <option value="6">Sabado</option>
                        <option value="7">Domingo</option>
                	</select>
            	</label>
            </div>
            <div id="columna2">
             &nbsp;
              <span class="labelSeccion">
                <label for="labelDLAB2">Horas programadas</label>
              </span>
            </div>
            
	  	</div>
        
        
        <!-- Seleccion de proceso por empresa -->
        <div id="contenidos" >
            <div id="columna1">
                <label>Horas
                  <select name="horas" size="5" multiple="multiple" id="horas" onchange="showSelectedValues()" style="width:100px">
                   <%
                    Iterator<String> itHoras = listaHoras.iterator();
                    while(itHoras.hasNext() ) {
                        String auxhora = itHoras.next();
                        %>
                        <option value="<%=auxhora%>"><%=auxhora%></option>
                        <%
                    }
                %>
                  </select>
              </label>
            </div>
            <div id="columna2">
             	<label for="deptoId">Hora Seleccionada&nbsp;</label>
             	<input type="text" name="horaSelected" id="horaSelected" />
              <input type="hidden" name="horaSelectedHidden" id="horaSelectedHidden" />
            <a class="button button-blue" href="javascript:;" onclick="actualizaItem();">Modificar/Agregar hora</a></div>
	  	</div>
        
        <div id="contenidos" >
            <div id="columna1"><a class="button button-blue" href="javascript:;" onclick="eliminaItems();">Eliminar hora(s)</a></div>
            <div id="columna2"><a class="button button-blue" href="javascript:;" onclick="enviarDatos();">Guardar</a></div>
	  	</div>
          <input name="empresaSelected" id="empresaSelected" type="hidden" value="<%=empresaProcesoSelected%>">
            <input name="procesoSelected" id="procesoSelected" type="hidden" value="<%=procesoSelected%>">
            <input name="horas_selected" id="horas_selected" type="hidden">
           
       <input name="action" type="hidden" id="action" value="programar_proceso">    
       
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
            //alert('[loadValues]Empresa: <%=empresaProcesoSelected%>'+', codDia: <%=diaSelected%>');
            //dia
            $("#codDia").val(<%=diaSelected%>);
            
            //empresa
            $("#empresaProceso").val('<%=empresaProcesoSelected%>');
            $("#empresaProceso").change();
           
        }
        
        function setProcesoSelected(){
            var procesoSelected='<%=procesoSelected%>';
            //alert('[setProcesoSelected]Empresa: <%=empresaProcesoSelected%>'+', procesoId: <%=procesoSelected%>');
            $("#idProceso").val(<%=procesoSelected%>);
            //$("#idProceso").val(procesoSelected).trigger("change");
            $("#idProceso").change();
        }
        
    </script>
</body>

</html>

