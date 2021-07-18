<%@page import="cl.femase.gestionweb.vo.CargoVO"%>
<%@page import="cl.femase.gestionweb.vo.ModuloAccesoPerfilVO"%>
<%@page import="cl.femase.gestionweb.vo.EmpresaVO"%>
<%@page import="cl.femase.gestionweb.vo.DepartamentoVO"%>
<%@page import="cl.femase.gestionweb.vo.CentroCostoVO"%>
<%@page import="cl.femase.gestionweb.vo.ComunaVO"%>
<%@page import="cl.femase.gestionweb.vo.TurnoVO"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@ page import="java.util.Collection"%>
<%@ page import="java.util.Iterator"%>


<%
    List<EmpresaVO> empresas = (List<EmpresaVO>)session.getAttribute("empresas");
    List<DepartamentoVO> departamentos   = (List<DepartamentoVO>)session.getAttribute("departamentos");
    List<CentroCostoVO> cencos   = (List<CentroCostoVO>)session.getAttribute("cencos");  
    List<ComunaVO> comunas   = (List<ComunaVO>)session.getAttribute("comunas");  
    List<TurnoVO> turnos   = (List<TurnoVO>)session.getAttribute("turnos");
    List<CargoVO> cargos   = (List<CargoVO>)session.getAttribute("cargos");
%>
<!doctype html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Crear Empleado</title>
  <meta name="description" content="Crear Empleado">
  
        <script src="../js/jquery-1.12.4.min.js"></script>
	<script src="../js/formularios/parsley.min.js"></script>
	<script src="<%=request.getContextPath()%>/js-varios/jquery.min.js"></script>
	<script src="../jquery-plugins/datepicker/js/jquery.plugin.min.js"></script>
	<script src="../jquery-plugins/datepicker/js/jquery.datepick.js"></script>
	<script src="../js/jquery-ui.js"></script>
	
        <script type="text/javascript" src="../jquery-plugins/chosen_v1.6.2/chosen.jquery.js">  </script>
        <script type="text/javascript" src="../jquery-plugins/chosen_v1.6.2/docsupport/prism.js" charset="utf-8"> </script>
        
        <script src="https://cdn.jsdelivr.net/jquery.validation/1.15.0/jquery.validate.min.js"></script>
        <script src="https://cdn.jsdelivr.net/jquery.validation/1.15.0/additional-methods.min.js"></script>
       
        
	<link rel="stylesheet" href="../jquery-plugins/datepicker/css/jquery.datepick.css" >
	<link rel="stylesheet" href="../css/style.css">
	<link rel="stylesheet" href="../css/parsley.css" >
	<!-- Bootstrap core CSS -->
	<link rel="stylesheet" href="../css/bootstrap.css" >
         <!-- estilo para botones -->
        <link rel="stylesheet" href="../jquery-plugins/chosen_v1.6.2/docsupport/style.css">
    <!-- -->

    <link rel="stylesheet" href="../jquery-plugins/chosen_v1.6.2/chosen.css">
    <link rel="stylesheet" href="https://jqueryvalidation.org/files/demo/site-demos.css">
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
   
        $(function() {
             $('#fechaNacimientoAsStr').datepick({dateFormat: 'dd-mm-yyyy'});
             $('#fechaInicioContratoAsStr').datepick({dateFormat: 'dd-mm-yyyy'});
         });
    	
	function volver(){
		window.history.back();
	}
        
        function loadValues(){
        //cargar items seleccionados combos...
            document.getElementById("genderM").checked = true;
        }  
  </script>
  
  
  <style type="text/css" media="all">
    /* fix rtl for demo */
    .chosen-rtl .chosen-drop { left: -9000px; }
   body {
	background-color: #fafafa;
	font-family:font-family, times, serif; 	
	font-size: 12pt;
	font-style: normal;
	color: #06C
}
   .container { margin:0px auto; max-width:1000px;}
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
    vertical-align: middle;
    padding: 10px;
}
</style>
  
  
  <!--[if lt IE 9]>
  <script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script>
  <![endif]-->
</head>

<body onLoad="loadValues()">
   <div id="container" class="container">
	<div class="bs-callout bs-callout-warning hidden">
	  <h4>Oh snap!</h4>
	  <p>This form seems to be invalid :(</p>
	</div>

	<div class="bs-callout bs-callout-info hidden">
	  <h4>Yay!</h4>
	  <p>Everything seems to be ok :)</p>
	</div>

       <form id="demo-form" action="<%=request.getContextPath()%>/EmpleadosController" 
              data-parsley-validate="" method="post" enctype="multipart/form-data">
       
          <h1><span class="h1">Crear Empleado</span>
            <input type="hidden" name="action" id="action" value="create">
            <!-- MAX_FILE_SIZE must precede the file input field -->
            <input type="hidden" name="MAX_FILE_SIZE" value="30000" />          
          </h1>
      
	  
	  <div id="contenedor">
		<div id="contenidos">
			<div id="columna1"><label for="rut">Rut * :</label>
                        <input type="text" class="form-control" name="rut" required maxlength="12"></div>
			<div id="columna2">&nbsp;</div>
		</div>
              
              <div id="contenidos">
			<div id="columna1"><label for="nombres">Nombres * :</label>
	  <input type="text" class="form-control" name="nombres" required maxlength="70"></div>
			<div id="columna2"><label for="apePaterno">Ap. Paterno * :</label>
	  <input type="text" class="form-control" name="apePaterno" required maxlength="50" id="apePaterno"></div>
			
		</div>
              
              
		<div id="contenidos">
			<div id="columna1"><label for="apeMaterno">Ap. Materno * :</label>
                            <input type="text" class="form-control" name="apeMaterno" required maxlength="50" id="apeMaterno">
                        </div>
                        <div id="columna2" ><label for="fec_nac">Fecha de nacimiento *:</label>
                            <input name="fechaNacimientoAsStr" type="text" required id="fechaNacimientoAsStr">
                        </div>
			
                </div>
                <div id="contenidos">
			<div id="columna1"> <label for="direccion">Direcci&oacute;n * :</label>
                            <input type="text" class="form-control" name="direccion" required maxlength="70">
                        </div>
			<div id="columna2" ><label for="email">Email * :</label>
                            <input type="email" class="form-control" name="email" data-parsley-trigger="change" required maxlength="70">
                        </div>
		</div>
              
              <div id="contenidos">
			<div id="columna1"><label for="foto">Foto :(S&oacute;lo se permite formato .png y .jpg): </label>
      <input type="file" name="foto"></div>
			<div id="columna2"><label for="sexo">Sexo *:</label>
	  <p>
		M: <input type="radio" name="sexo" id="genderM" value="M" required>
		F: <input type="radio" name="sexo" id="genderF" value="F">
	  </p> </div>
			
		</div>
              
              <div id="contenidos">
			<div id="columna1"><label for="fono_fijo">Tel&eacute;fono fijo * :</label>
	  <input type="text" class="form-control" name="fono_fijo" required maxlength="20"></div>
			<div id="columna2"><label for="fono_movil">Tel&eacute;fono m&oacute;vil * :</label>
	  <input type="text" class="form-control" name="fono_movil" required maxlength="20"></div>
			
		</div>
              
            <div id="contenidos">
                    <div id="columna1"><label for="comunaId">Comuna</label>
                        <select id="comunaId" name="comunaId" class="chosen-select" style="width:350px;" tabindex="2" required>
                          <option value="-1"></option>
                          <%
                              Iterator<ComunaVO> iteracomunas = comunas.iterator();
                              while(iteracomunas.hasNext() ) {
                                  ComunaVO auxcomuna = iteracomunas.next();
                                  %>
                                  <option value="<%=auxcomuna.getId()%>"><%=auxcomuna.getNombre()%></option>
                                  <%
                              }
                          %>
                        </select>
                    </div>
                    <div id="columna2"><label for="estado">Estado</label>
                      <select id="estado" name="estado" class="chosen-select" style="width:350px;" tabindex="2" required>
                        <option value="-1"></option>
                        <option value="1">Vigente</option>
                        <option value="2">No Vigente</option>
                      </select>
                    </div>
                   
            </div>
                 <div id="contenidos">   
                    <div id="columna1"><label for="fec_contrato">Fecha inicio contrato *:</label>
                      <input name="fechaInicioContratoAsStr" type="text" required id="fechaInicioContratoAsStr" required>
                    </div>
                    <div id="columna2">&nbsp;</div>
                 </div>   
                    
             <!-- empresa depto y cenco-->        
            <div id="contenidos">
                <div id="columna1">
                    <label for="empresaId">Empresa</label>
                    <select id="empresaId" name="empresaId" style="width:350px;" required>
                    <option value="-1"></option>
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
                <div id="columna2">&nbsp;</div>
            </div>
             <div id="contenidos">       
                <div id="columna1"><label for="deptoId">Departamento</label>
                    <select id="deptoId" name="deptoId" style="width:350px;" required>
                        <option value="-1">--------</option>
                  </select>
                </div>
                    <div id="columna2">
                        <label for="cencoId">Centro de Costo</label>
                        <select id="cencoId" name="cencoId" style="width:350px;" tabindex="2" required>
                          <option value="-1">--------</option>
                        </select>
                    </div>
            </div>        
            <div id="contenidos">
                <div id="columna1">
                    <label for="idTurno">Turno</label>
                    <select id="idTurno" name="idTurno" style="width:350px;" required>
                    <option value="-1"></option>
                        <%
                            Iterator<TurnoVO> iteraturnos = turnos.iterator();
                            while(iteraturnos.hasNext() ) {
                                TurnoVO auxturno = iteraturnos.next();
                                %>
                                <option value="<%=auxturno.getId()%>"><%=auxturno.getNombre()%></option>
                                <%
                            }
                        %>
                    </select>
                </div>
                <div id="columna2">
                    <label for="sexo">Autoriza Ausencias *:</label>
                    <p>
                    Si: <input type="radio" name="autorizaAusencia" id="autorizaS" value="S" required>
                    No: <input type="radio" name="autorizaAusencia" id="autorizaN" value="N">
                    </p> 
                </div>
            </div>
            
            <div id="contenidos">
                <div id="columna1">
                    <label for="idCargo">Cargo</label>
                    <select id="idCargo" name="idCargo" style="width:350px;" required>
                    <option value="-1"></option>
                        <%
                            Iterator<CargoVO> iteracargos = cargos.iterator();
                            while(iteracargos.hasNext() ) {
                                CargoVO auxcargo = iteracargos.next();
                                %>
                                <option value="<%=auxcargo.getId()%>"><%=auxcargo.getNombre()%></option>
                                <%
                            }
                        %>
                    </select>
                </div>
                <div id="columna2">&nbsp;</div>
            </div>        
                    
               <div id="contenidos">
			<div id="columna1"><small>* requerido</small></div>
			<div id="columna2"><input type="submit" class="button button-blue" value="Guardar"></div>
			<div id="columna3"><input name="botonvolver" type="button" value="Volver" class="button button-blue" onclick="volver();"></div>
		</div>
                    
	</div><!-- Fin contenedor de divs con los campos de formulario -->
	
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
  
  <script>
// just for the demos, avoids form submit
/*jQuery.validator.setDefaults({
  debug: true,
  success: "valid"
});*/
$( "#demo-form" ).validate({
  rules: {
    foto: {
      required: true,
      extension: "jpg|png"
    }
  }
});

$(function () {
  $('#demo-form').parsley().on('field:validated', function() {
    var ok = $('.parsley-error').length === 0;
    $('.bs-callout-info').toggleClass('hidden', !ok);
    $('.bs-callout-warning').toggleClass('hidden', ok);
  })
  
});
</script>
</body>
</html>