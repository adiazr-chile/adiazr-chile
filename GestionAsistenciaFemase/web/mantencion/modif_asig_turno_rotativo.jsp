<%@ include file="/include/check_session.jsp" %>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.StringTokenizer"%>
<%@page import="java.util.ArrayList"%>
<%@page import="cl.femase.gestionweb.vo.TurnoRotativoDetalleVO"%>
<%@page import="cl.femase.gestionweb.vo.EmpleadoVO"%>
<%@page import="cl.femase.gestionweb.common.Utilidades"%>
<%@page import="org.joda.time.LocalDate"%>
<%@page import="java.util.Iterator"%>
<%@page import="cl.femase.gestionweb.vo.EmpresaVO"%>
<%@page import="java.util.List"%>
<%
    List<EmpresaVO> empresas            = (List<EmpresaVO>)session.getAttribute("empresas");
    //System.out.println("[asignacion_turno_rotativo.jsp]empresas.size= " + empresas.size());
    String accion = "insertUpdate";
    EmpleadoVO objEmpleado = (EmpleadoVO)request.getAttribute("empleadoSelected");
    String anioMesSelected = (String)request.getAttribute("anioMesSelected");
    ArrayList<String> diasLaborales = (ArrayList<String>)request.getAttribute("diasLaborales");
    ArrayList<String> diasLibres = (ArrayList<String>)request.getAttribute("diasLibres");
    String empresaTurno = (String)request.getAttribute("empresaTurnoSelected");
    String turnoSelected = (String)request.getAttribute("turnoSelected");
    String strDiasLaborales = (String)request.getAttribute("strDiasLaborales");
    String strDiasLibres = (String)request.getAttribute("strDiasLibres");
                        
    System.out.println("[modif_asig_turno_rotativoJSP]anioMesSelected: "+anioMesSelected);
    if (anioMesSelected == null) anioMesSelected="";
    String labelRut = "";
    String labelNombre = "";
    String labelDepto = "";
    String labelCenco = "";
    if (objEmpleado!=null){
        labelRut = objEmpleado.getRut();
        labelNombre = objEmpleado.getNombres()+" "+objEmpleado.getApeMaterno();
        labelDepto = objEmpleado.getDepartamento().getNombre();
        labelCenco = objEmpleado.getCentroCosto().getNombre();
    }
    int startday = 0;
    int endday = 0;
    int auxmes = -1;
    int auxanio= -1;
    if (anioMesSelected.compareTo("") != 0){    
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        StringTokenizer tokenaniomes=new StringTokenizer(anioMesSelected, "-");
        auxmes = Integer.parseInt(tokenaniomes.nextToken());
        auxanio = Integer.parseInt(tokenaniomes.nextToken());
        
        System.out.println("Date inicial: "+auxanio + "-" + auxmes + "-1");
        Date date = sdf.parse(auxanio + "-" + auxmes + "-1");
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(date);  
        System.out.println("First Day Of Month : " + calendar.getActualMinimum(Calendar.DAY_OF_MONTH));  
        System.out.println("Last Day of Month  : " + calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        startday = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
        endday = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        auxmes--;
        if (auxmes<0) auxanio--;
        System.out.println("[turno_rotativo.jsp]"
            + "startDay "+startday + ", endDay: "+endday);
    }
	
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
  
  <title>Turno Rotativo - Asignacion</title>

<script type="text/javascript">
        
        $(document).ready(function() {
             $('#empresaId').change(function(event) {
                 var empresaSelected = $("select#empresaId").val();
                 $.get('<%=request.getContextPath()%>/JsonListServlet', {
                         empresaID : empresaSelected
                 }, function(response) {
                        var select = $('#deptoId');
                        select.find('option').remove();
                        var newoption = "";
                        newoption += "<option value='-1'>Seleccione Departamento</option>";
                        for (i=0; i<response.length; i++) {
                            newoption += "<option value='"+response[i].id+"'>"+response[i].nombre+"</option>";
                        }
                        $('#deptoId').html(newoption);
                 });
             });
             //evento para combo depto
             $('#deptoId').change(function(event) {
                 var empresaSelected = $("select#empresaId").val();
                 var deptoSelected = $("select#deptoId").val();
                 $.get('<%=request.getContextPath()%>/JsonListServlet', {
                         empresaID : empresaSelected,deptoID : deptoSelected
                 }, function(response) {
                        var select = $('#cencoId');
                        select.find('option').remove();
                        var newoption = "";
                        newoption += "<option value='-1'>Seleccione Centro de costo</option>";
                        for (i=0; i<response.length; i++) {
                            newoption += "<option value='"+response[i].id+"'>"+response[i].nombre+"</option>";
                        }
                        $('#cencoId').html(newoption);
                 });
             });
             //evento para combo centro costo
             $('#cencoId').change(function(event) {
                 var empresaSelected = $("select#empresaId").val();
                 var deptoSelected = $("select#deptoId").val();
                 var cencoSelected = $("select#cencoId").val();
                 var sourceSelected = 'asignacion_turno_rotativo';
                 $.get('<%=request.getContextPath()%>/JsonListServlet', {
                         empresaID : empresaSelected,deptoID : deptoSelected,cencoID : cencoSelected,source: sourceSelected
                 }, function(response) {
                        var select = $('#rut');
                        select.find('option').remove();
                        var newoption = "";
                        newoption += "<option value='-1'>--------------</option>";
                        for (i=0; i<response.length; i++) {
                            var auxNombre = '['+response[i].rut+'] '+response[i].nombres + 
                                ' ' + response[i].apePaterno + ' '+response[i].apeMaterno;
                            newoption += "<option value='" + response[i].rut + "'>" + auxNombre + "</option>";
                        }
                        $('#rut').html(newoption);
                 });
             });
             
             //Evento al seleccionar combo de empresa: cargar turnos rotativos sin asignacion de turnos definidos
             $('#empresaTurno').change(function(event) {
                 var auxempresaSelected = $("select#empresaTurno").val();
                 //alert('empresaSelected='+auxempresaSelected);
                 $.get('<%=request.getContextPath()%>/JsonListServlet', {
                        empresaTurnoID : auxempresaSelected,source: 'modificarAsignacionTurnoRotativo'
                 }, function(response) {
                        var select = $('#idTurno');
                        select.find('option').remove();
                        var newoption = "";
                        var labeloption="";
                        newoption += "<option value='-1'>Seleccione Turno</option>";
                        for (i=0; i<response.length; i++) {
                            labeloption=response[i].nombre+" ["+response[i].horaEntrada+" a "+response[i].horaSalida+"]";
                            newoption += "<option value='"+response[i].id+"' id='"+response[i].id+"'>"+labeloption+"</option>";
                        }
                        $('#idTurno').html(newoption);
                        
                 });
             });
			 
                // Hide the icon and open the menu when you
               // click on the text field.
               $('#anioMes').MonthPicker(
                    { 
                        Button: false,
                        MonthFormat: 'yy-mm' // Short month name, Full year. 
                    }
                );
			 
         });

       
        
    </script>

	<script type='text/javascript'>//<![CDATA[
	
	/*
		var fechasSeleccionadas = '';
        var selecteddates=[new Date(2017, 10, 15), new Date(2017, 10, 16), new Date(2017, 10, 20)];
		
		para setear fechas existentes ya seteadas en BD
		dateFormat: "yy-mm-dd", 
        addDates: selecteddates,
	*/
            var fechasLaboralesSeleccionadas = '<%=strDiasLaborales%>';
            var fechasLibresSeleccionadas = '<%=strDiasLibres%>';
            window.onload=function(){
                var selecteddatesLab='';
                var selecteddatesLib='';
                <%
                    String jsDatesArray="";
                    if (diasLaborales!=null && diasLaborales.size() > 0){
                        Iterator<String> it = diasLaborales.iterator();
                        while(it.hasNext()){
                            StringTokenizer tokenfecha=new StringTokenizer(it.next(), "-");
                            while (tokenfecha.hasMoreElements()){
                                int year    = Integer.parseInt(tokenfecha.nextToken());
                                int month   = Integer.parseInt(tokenfecha.nextToken());
                                int day     = Integer.parseInt(tokenfecha.nextToken());
                                month--;
                                if (month<0) year--;
                                jsDatesArray+="new Date("+year+","+ month+","+ day+"),";
                            }
                        }
                        jsDatesArray = jsDatesArray.substring(0, jsDatesArray.length() - 1);%>
                        selecteddatesLab=[<%=jsDatesArray%>];
                    <%}//fin if (diasLaborales != null && diasLaborales.size() > 0){

                      if (diasLibres != null && diasLibres.size() > 0){
                        String jsDatesArray2="";
                        Iterator<String> it2 = diasLibres.iterator();
                        while(it2.hasNext()){
                            StringTokenizer tokenfecha2=new StringTokenizer(it2.next(), "-");
                            while (tokenfecha2.hasMoreElements()){
                                int year    = Integer.parseInt(tokenfecha2.nextToken());
                                int month   = Integer.parseInt(tokenfecha2.nextToken());
                                int day     = Integer.parseInt(tokenfecha2.nextToken());
                                month--;
                                if (month<0) year--;
                                jsDatesArray2+="new Date("+year+","+ month+","+ day+"),";
                            }
                        }
                        jsDatesArray2 = jsDatesArray2.substring(0, jsDatesArray2.length() - 1);
                    %>
                        selecteddatesLib=[<%=jsDatesArray2%>];
                    <%}//fin if (diasLibres != null && diasLibres.size() > 0){%>
                  
                $('#calendario_dias_laborales').multiDatesPicker({
                    dateFormat: "yy-mm-dd",
                    minDate: new Date(<%=auxanio%>, <%=auxmes%> , <%=startday%>),
                    maxDate: new Date(<%=auxanio%>, <%=auxmes%> , <%=endday%>),
                    <%if (diasLaborales!=null && diasLaborales.size() > 0){%>
                        addDates: selecteddatesLab,
                    <%}%>
                    onSelect: function (dateText, inst) {
                            var datesLaborales = $(this).val();
                            //alert('dia laboral selected: '+dateText+', inst: '+inst);
                            fechasLaboralesSeleccionadas = datesLaborales;
                            $('#input_dias_laborales').val(datesLaborales);
                    },
                    onChangeMonthYear: function(year, month, inst) {
                        //alert('dias laborales.Cambio de mes. mes-anio: '+year + "/" + month);
                        //$(this).val(month + "/" + year);
                    }
                });
			
                $('#calendario_dias_libres').multiDatesPicker({
                    dateFormat: "yy-mm-dd", 
                    minDate: new Date(<%=auxanio%>, <%=auxmes%> , <%=startday%>),
                    maxDate: new Date(<%=auxanio%>, <%=auxmes%> , <%=endday%>),
                    <%if (diasLibres!=null && diasLibres.size() > 0){%>
                        addDates: selecteddatesLib,
                    <%}%>
                    onSelect: function (dateText, inst) {
                        var datesLibres = $(this).val();
                        fechasLibresSeleccionadas = datesLibres;
                        $('#input_dias_libres').val(datesLibres);
                    },
                    onChangeMonthYear: function(year, month, inst) {
                        //alert('dias libres. Cambio de mes. mes-anio: '+year + "/" + month);
                        //$(this).val(month + "/" + year);
                    }	
                });
			
            };//]]> 
    
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
	
		function validaEmpleados()
		{
			var seleccionOk = true;
			
			var listaEmpleados = document.getElementById("rut");
			var sinseleccion = listaExisteValor(listaEmpleados,'-1');
			var empleados_count = listaEmpleados.options.length;
			if (empleados_count == 1 && sinseleccion){
				alert("Seleccione uno o mas empleados");
				listaEmpleados.focus();	
				seleccionOk = false;
			}else{
				if(listaEmpleados.selectedIndex < 0){
					alert("Seleccione uno o mas empleados");
					seleccionOk = false;
				}	
			}
						
			return seleccionOk;
		}
		
		function validaTurno()
		{
			var seleccionOk = true;
			
			var listaTurnos = document.getElementById("idTurno");
			//var sinseleccion = listaExisteValor(listaTurnos,'-1');
			//var turnos_count = listaTurnos.options.length;
			//alert("turnos_count= "+turnos_count);
			if (listaTurnos.value === '-1'){
				alert("Seleccione un turno rotativo");	
                                document.getElementById("empresaTurno").focus();
				seleccionOk = false;
			}
						
			return seleccionOk;
		}
	
		function verDetalle(){
			if (validaTurno()){
				if (validaEmpleados()){
					var aniomes = document.getElementById("anioMes");
					if (anioMes.value !== ''){	
						document.form1.action.value = 'load_asignacion';
						document.form1.submit();
					}else{
						alert("Seleccione mes");
						aniomes.focus();	
					}
				}
			}
		}
	
		function enviarDatos(){
                    //alert('validando formulario');
                    var fechaSelected='';
                    var auxanio ='';
                    var auxmes  = '';
                    var auxFechasLaborales = '';
                    var auxFechasLibres = '';
                    var listaTurnos = document.getElementById("idTurno");
                    if (fechasLaboralesSeleccionadas !== ''){	
                        //iterar fechas laborales eleccionadas para setear anio-mes en cada una	
                        var input =fechasLaboralesSeleccionadas;
                        var output = input.split(",");
                        for (var i = 0; i < output.length; i++) {
                            // System.out.println("anio= " +strfecha.substring(0,4) +", mes= " +strfecha.substring(5,7));
                            fechaSelected = output[i];
                            var arrfecha = fechaSelected.split("-");
                            auxanio = arrfecha[0];
                            auxmes  = arrfecha[1];
                            //alert('fecha laboral seleccionada= ' + fechaSelected+', anio= '+auxanio+', mes= '+auxmes);	
                            auxFechasLaborales = auxFechasLaborales + auxanio+'-'+auxmes+'|'+fechaSelected+',';
                        }
                        auxFechasLaborales = auxFechasLaborales.substring(0,auxFechasLaborales.length-1);
                    }
						
                    if (fechasLibresSeleccionadas !== ''){	
                        //iterar fechas libres eleccionadas para setear anio-mes en cada una	
                        var input =fechasLibresSeleccionadas;
                        var output = input.split(",");
                        for (var i = 0; i < output.length; i++) {
                            // System.out.println("anio= " +strfecha.substring(0,4) +", mes= " +strfecha.substring(5,7));
                            fechaSelected = output[i];
                            var arrfecha2 = fechaSelected.split("-");
                            auxanio = arrfecha2[0];
                            auxmes  = arrfecha2[1];
                            //alert('fecha laboral seleccionada= ' + fechaSelected+', anio= '+auxanio+', mes= '+auxmes);	
                            auxFechasLibres = auxFechasLibres + auxanio+'-'+auxmes+'|'+fechaSelected+',';
                        }
                        auxFechasLibres = auxFechasLibres.substring(0,auxFechasLibres.length-1);
                    }
                    document.form1.input_dias_laborales.value = auxFechasLaborales;
                    document.form1.input_dias_libres.value = auxFechasLibres;	
                       	
                    document.form1.submit();
                }
                
    </script>
</head>

<body>
  
    <form name="form1" method="post" action="<%=request.getContextPath()%>/TurnosRotativosController">
        <p></p>
        <span class="tituloForm">Turno Rotativo - Modificar Asignaci&oacute;n</span>
        <p></p>
        <div id="contenidos" >
            <div id="columna1">
                <label>Empresa
                <select name="empresaTurno" id="empresaTurno">
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
              <label for="idTurno">Turno</label>
              <select id="idTurno" name="idTurno" style="width:350px;" tabindex="2">
                <option value="-1" selected>----------</option>
              </select>
            </div>
        </div>
        
        <div id="contenidos" >
            <div id="columna1">
                &nbsp;
              <span class="labelSeccion">
                <label for="labelDLAB2">Selecci&oacute;n de empleado</label>
              </span></div>
            <div id="columna2">
             &nbsp;
            </div>
	  	</div>
        <!-- Seleccion de turno rotativo por empresa -->
        <div id="contenidos" >
            <div id="columna1">
                <label>Empresa
                <select name="empresaId" id="empresaId">
                <option value="-1" selected>----------</option>
                <%
                    Iterator<EmpresaVO> iteraempresas2 = empresas.iterator();
                    while(iteraempresas2.hasNext() ) {
                        EmpresaVO auxempresa = iteraempresas2.next();
                        %>
                        <option value="<%=auxempresa.getId()%>"><%=auxempresa.getNombre()%></option>
                        <%
                    }
                %>
                </select>
            </label>
            </div>
            <div id="columna2">
             	<label for="deptoId">Departamento</label>
                <select id="deptoId" name="deptoId" style="width:350px;" required>
                    <option value="-1">--------</option>
                </select>
            </div>
	  	</div>
        
        <div id="contenidos" >
            <div id="columna1">
                <label>Centro Costo
                <select name="cencoId" id="cencoId">
                    <option value="-1" selected>----------</option>
                </select>
            </label>
            </div>
            <div id="columna2">
             <label>Empleado
                <select name="rut" id="rut">
                    <option value="-1" selected>----------</option>
                </select>
            </label>
             
            </div>
	  	</div>
         <div id="contenidos" >
            <div id="columna1">
               <label>A&ntilde;o/Mes
                <input name="anioMes" type="text" id="anioMes" style="width:80px;" value="<%=anioMesSelected%>" readonly/>
                &nbsp;<img alt="Month/Year Picker" onclick="showCalendarControl('anioMes');" 
				src="<%=request.getContextPath()%>/images/datepicker.gif" />
            	</label>
                
            </div>
            <div id="columna2">
              
                <a class="button button-blue" href="javascript:;" onclick="verDetalle();">Ver detalle turno</a>
            </div>
	  	</div>    
         
        <%if (objEmpleado != null){%>
            <!-- mostrar datos del empleado seleccionado -->
            <div id="contenidos" >
                <div id="columna1">
                  <span class="labelSeccion"><label for="labelDLAB">Empleado seleccionado</label></span>
                </div>
                <div id="columna2">
                  &nbsp;
                </div>
            </div>      
            
            <div id="contenidos" >
                <div id="columna1">
                    <label><%=labelRut%> - <%=labelNombre%>
                </label>
                </div>
                <div id="columna2">
                 <label><%=labelDepto%> - <%=labelCenco%>
                </label>
                </div>
            </div>      
          
            <div id="contenidos">
                <div id="columna1">
                    <span class="labelSeccion"><label for="labelDLAB">&nbsp;&nbsp;D&iacute;as laborales</label></span>
                </div>
                <div id="columna2" >
                    <span class="labelSeccion"><label for="labelDLIB">&nbsp;&nbsp;D&iacute;as libres</label></span>
                </div>
            </div>
            
            <div id="contenidos">
                <div id="columna1">
                    <div id="calendario_dias_laborales" ></div>
                </div>
                <div id="columna2" >
                    <div id="calendario_dias_libres" ></div>
                </div>
            </div>
          
            
            <input name="input_dias_laborales" type="hidden" value="">
            <input name="input_dias_libres" type="hidden" value="">
            <input name="rutSelected" id="rutSelected" type="hidden" value="<%=labelRut%>">
            <input name="empresaSelected" id="empresaSelected" type="hidden" value="<%=empresaTurno%>">
            <input name="turnoSelected" id="turnoSelected" type="hidden" value="<%=turnoSelected%>">
                 
            <div id="contenidos" >
                <div id="columna1">
                    &nbsp;
                </div>
                <div id="columna2">
                 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a class="button button-blue" href="javascript:;" onclick="enviarDatos();">Guardar</a>
                </div>
            </div>
       <%}%> 
       <input name="action" type="hidden" id="action" value="modif_asignacion">    
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
        
         <%if (empresaTurno != null && empresaTurno.compareTo("") != 0){%>
            setTurnoSelected();
        <%}%>

        function setTurnoSelected(){
            //alert('set combo empresa y turno...');
            //document.getElementById("empresaTurno").value = '<%=empresaTurno%>';
            
            //empresa - turno
            //$('#empresaTurno option[value="<%=empresaTurno%>"]'); 
            $("#empresaTurno").val('<%=empresaTurno%>');
            $("#empresaTurno").change();
           
            //$("#empresaTurno").val('<%=empresaTurno%>').trigger("change");
            ////$("#empresaTurno").val(<%=empresaTurno%>);
           
            //var turnoSelected='<%=turnoSelected%>';
            //$("#idTurno").val(turnoSelected).trigger("change"); 
            
            /*
            var comboEmpresa = document.getElementById("empresaTurno");
            if (comboEmpresa != null){
                selectItemByValue(comboEmpresa, '<%=empresaTurno%>');
                $("#empresaTurno").change();
                //$("#empresaTurno").val('<%=empresaTurno%>').trigger("change");
            }else{
                alert('combo empresa no existe...!!');
            }
            */
        }
        
        function selectItemByValue(lista, value){

            for(var i=0; i < lista.options.length; i++)
            {
                //alert('valor en combo= '+lista.options[i].value+', valor a seleccionar: '+value);  
                if(lista.options[i].value == value){
                    lista.selectedIndex = i;
                    //alert('item encontrado. selected index= '+i);
                }
            }
        }
    </script>
  
</body>

</html>

