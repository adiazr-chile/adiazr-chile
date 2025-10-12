<%@page import="cl.femase.gestionweb.common.Utilidades"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.Calendar"%>
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
    
    Calendar mycal=Calendar.getInstance();
    int anioActual = mycal.get(Calendar.YEAR);
    LinkedHashMap<Integer,String> listaAnios = new LinkedHashMap<Integer,String>();
    listaAnios.put(anioActual-5, String.valueOf(anioActual-5));
    listaAnios.put(anioActual-4, String.valueOf(anioActual-4));
    listaAnios.put(anioActual-3, String.valueOf(anioActual-3));
    listaAnios.put(anioActual-2, String.valueOf(anioActual-2));
    listaAnios.put(anioActual-1, String.valueOf(anioActual-1));
    listaAnios.put(anioActual, String.valueOf(anioActual));
    Date currentDate = mycal.getTime();
    int semestreActual = Utilidades.getSemestre(currentDate);
    //listaAnios.put(anioActual+1, String.valueOf(anioActual+1));
%>

<!DOCTYPE html>

<html>
<head>
    <meta charset="utf-8" />

    <meta name="keywords" content="femase,gestion,web">
    <meta name="description" content="Sistema de Gestion FEMASE-RRHH-Control Asistencia">
    <meta name="author" content="Adiazr">

    <title>Reporte de Permisos Administrativos</title>
    
    <!-- Include para monthpicker -->
    
    <script type="text/javascript" src="js/jquery.min.js" ></script>
    <script type="text/javascript" src="js/jquery-ui.min.js"></script>
    <link rel="stylesheet" href="css/jquery-ui.css">
    
    <!-- estilo para botones -->
    <link rel="stylesheet" href="../jquery-plugins/chosen_v1.6.2/docsupport/style.css">
    <link rel="stylesheet" href="../jquery-plugins/chosen_v1.6.2/docsupport/prism.css">
    <link rel="stylesheet" href="../jquery-plugins/chosen_v1.6.2/chosen.css">
    
    <script type="text/javascript">

        $(document).ready(function() {
             $("select[name='paramSemestre']").val("<%=semestreActual%>");
             $("select[name='paramAnio']").val("<%=anioActual%>");
            
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
                            var labelZonaExtrema = '';
                            var labelCenco = '';
                            if (response[i].zonaExtrema === 'S') labelZonaExtrema = ' (Zona Extrema)';
                            else labelZonaExtrema = '';
                            
                            labelCenco = response[i].nombre + " " + labelZonaExtrema;
                            newoption += "<option value='"+response[i].id+"'>"+labelCenco+"</option>";
                        }
                        $('#cenco').html(newoption);
                 });
             });
             
             //evento para combo centro costo
             $('#cenco').change(function(event) {
                var empresaSelected = $("select#empresa").val();
                var deptoSelected = $("select#depto").val();
                var cencoSelected = $("select#cenco").val();
                var sourceSelected = 'reporte_vacaciones';
                 
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
        });
       
        function validaForm() {
            //var isOk=true;
            var empresaId = $('#empresa').val();
            var deptoId = $('#depto').val();
            var cencoId = $('#cenco').val();
           
            if (empresaId === '-1' 
                && deptoId === '-1' 
                && cencoId === '-1'){
                    alert('Seleccione algun criterio de busqueda');
                    //isOk=false;
                    return false;
            }
            
                return true;
        }
        
    </script>
   
    
    <style type="text/css">
	.ui-datepicker-calendar { display: none !important; }
        
    </style>

    <style>
        
        .ui-datepicker-calendar {
            display: none;
        }
    
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
            <h4><br>&nbsp;&nbsp;&nbsp;Informe de Permisos Administrativos</h4><br>
                <p>
                  <strong>Semestre 1:</strong> del 1 de enero al 30 de junio<br>
                  <strong>Semestre 2:</strong> del 1 de julio al 31 de diciembre
                </p>
        </div>
	<div class="content-container">
            
            <div class="padded-content-container">
                
    <!-- Filtros de busqueda INICIO-->                
    <div class="filtering">
        <form id="demo-form" 
              action="<%=request.getContextPath()%>/servlet/PermisosAdministrativosReport" 
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
            <div id="content5" align="left">
            	<div id="col1"><label>Empleado</label></div>
				<div id="col2">            
                    <select name="rut" id="rut">
                        <option value="-1" selected>----------</option>
                    </select>
                </div>
            </div>
            
            <label>A&ntilde;o:
                <select id="paramAnio" name="paramAnio" style="width:150px;" tabindex="2" required>
                    <option value="-1">Seleccione a&ntilde;o</option>
                    <%
                        for(Integer anioKey : listaAnios.keySet()) {
                            String anioLabel = listaAnios.get(anioKey);
                           %>
                           <option value="<%=anioKey%>"><%=anioLabel%></option>
                        <%}%>
                </select>
            </label>
                
                <label>Semestre:
                <select id="paramSemestre" name="paramSemestre" style="width:150px;" tabindex="2" required>
                    <option value="1">Primer Semestre</option>
                    <option value="2">Segundo Semestre</option>
                </select>
            </label>
                
            <!-- fin div rut empleado -->
            <!--
            <div id="content6" align="left">            
                <div id="col1"><label>Fecha desde:</label></div>
                <div id="col2"><input name="startDate" class="datepicker" type="text" id="startDate"></div>   
            </div>
            <div id="content7" align="left">            
                <div id="col1"><label>Fecha hasta:</label></div>
                <div id="col2"><input name="endDate" class="datepicker" type="text" id="endDate"></div>   
            </div>    
            <div id="content8" align="left">
                <input type="hidden" id="formato" name="formato" value="pdf">
            </div>    
            -->   
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
        </form>
             
</body>
</html>

