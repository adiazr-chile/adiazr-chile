
<%@page import="cl.femase.gestionweb.vo.CargoVO"%>
<%@page import="cl.femase.gestionweb.vo.CentroCostoVO"%>
<%@page import="cl.femase.gestionweb.vo.DepartamentoVO"%>
<%@page import="cl.femase.gestionweb.vo.EmpresaVO"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>

<%
    List<EmpresaVO> empresas = (List<EmpresaVO>)session.getAttribute("empresas");
    List<DepartamentoVO> departamentos   = (List<DepartamentoVO>)session.getAttribute("departamentos");
    List<CentroCostoVO> cencos   = (List<CentroCostoVO>)session.getAttribute("cencos");
   
%>

<!DOCTYPE html>

<html>
<head>
    <meta charset="utf-8" />

    <meta name="keywords" content="femase,gestion,web">
    <meta name="description" content="Sistema de Gestion FEMASE-RRHH-Control Asistencia">
    <meta name="author" content="Adiazr">

    <title>Reporte Organizacion</title>
	    
    <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.4/jquery.min.js"></script>
    <script type="text/javascript" src="../jquery-plugins/chosen_v1.6.2/chosen.jquery.js"></script>
   
    <!-- estilo para botones -->
    <link rel="stylesheet" href="../jquery-plugins/chosen_v1.6.2/docsupport/style.css">
    <link rel="stylesheet" href="../jquery-plugins/chosen_v1.6.2/docsupport/prism.css">
    <link rel="stylesheet" href="../jquery-plugins/chosen_v1.6.2/chosen.css">
    
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
         });
       
        function validaForm() {
            var isOk=true;
            var empresaId = $('#empresa').val();
            var deptoId = $('#depto').val();
            var cencoId = $('#cenco').val();
            var formato = $('#formato').val();
            
            if (empresaId === '-1' 
                && deptoId === '-1' 
                && cencoId === '-1'){
                    alert('Seleccione algun criterio de busqueda');
                    isOk=false;
                    return false;
            }
            if (formato === 'csv'){
                document.getElementById('demo-form').method = 'get'; //Will set it
                return true;
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
            color: #06C
        }
    </style>
</head>
<body>
    <div class="site-container">
        <div class="main-header" style="position: relative">
            <h1>Reporte Organizacion</h1>
            
        </div>
	<div class="content-container">
            
            <div class="padded-content-container">
                
    <!-- Filtros de busqueda INICIO-->                
    <div class="filtering">
        <form id="demo-form" action="<%=request.getContextPath()%>/ReporteOrganizacion" method="POST" onsubmit="return validaForm();">
             <label>Empresa
                <select name="empresaId" id="empresaId">
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
                
             <label for="deptoId">Departamento</label>
            <select id="deptoId" name="deptoId" style="width:350px;" required>
                <option value="-1">--------</option>
            </select>
                
            <label>Centro Costo
                <select name="cencoId" id="cencoId">
                    <option value="-1" selected>----------</option>
                </select>
            </label>    
            
            <label>Formato
                <select name="formato" id="formato" class="chosen-select">
                <option value="-1" selected>----------</option>
                    <option value="pdf" selected>PDF</option>
                    <option value="csv">CSV</option>
                </select>
            </label>

            <!-- Boton Buscar-->  
            <button type="submit" 
                id="LoadRecordsButton"
                name="LoadRecordsButton"
                class="button button-blue">Generar Reporte</button>
            
        </form>
    </div>
    
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
</body>
</html>

