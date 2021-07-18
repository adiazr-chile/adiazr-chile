<%@ include file="/include/check_session.jsp" %>

<%@page import="cl.femase.gestionweb.vo.ModuloAccesoPerfilVO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="cl.femase.gestionweb.vo.AccesoVO"%>
<%@page import="cl.femase.gestionweb.vo.PerfilUsuarioVO"%>
<%@page import="cl.femase.gestionweb.vo.ModuloSistemaVO"%>
<%@page import="java.util.LinkedHashMap"%>
<%@ page import="java.util.Collection"%>
<%@ page import="java.util.Iterator"%>

<%
    	                  
%>
<!doctype html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Admin Empleados</title>
  <meta name="description" content="Admin Accesos Perfil de usuario">
  
        <script src="../js/jquery-1.12.4-jquery.min.js"></script>
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
   
   $(function() {
        $('#fecha_nac').datepick({dateFormat: 'dd-mm-yyyy'});
        $('#fec_contrato').datepick({dateFormat: 'dd-mm-yyyy'});
    });
    	
  </script>
  
  
  <style type="text/css" media="all">
    /* fix rtl for demo */
    .chosen-rtl .chosen-drop { left: -9000px; }
   body { background-color:#fafafa; font-family:font-family, times, serif; font-size:12pt; font-style:normal;}
   .container { margin:0px auto; max-width:660px;}
  </style>
  
  
  <!--[if lt IE 9]>
  <script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script>
  <![endif]-->
</head>

<body>
   <div id="container" class="container">
	<div class="bs-callout bs-callout-warning hidden">
	  <h4>Oh snap!</h4>
	  <p>This form seems to be invalid :(</p>
	</div>

	<div class="bs-callout bs-callout-info hidden">
	  <h4>Yay!</h4>
	  <p>Everything seems to be ok :)</p>
	</div>

	<form id="demo-form" data-parsley-validate="">
          <label for="rut">Rut * :</label>
          <input type="text" class="form-control" name="rut" required="" maxlength="12">
          
	  <label for="nombres">Nombres * :</label>
	  <input type="text" class="form-control" name="nombres" required="" maxlength="70">

          <label for="paterno">Ap. Paterno * :</label>
	  <input type="text" class="form-control" name="paterno" required="" maxlength="50">
          
          <label for="materno">Ap. Materno * :</label>
	  <input type="text" class="form-control" name="materno" required="" maxlength="50">
          
          <label for="fec_nac">Fecha de nacimiento *:</label>
	  <p><input type="text" id="fecha_nac" required=""></p>
          
          <label for="direccion">Dirección * :</label>
	  <input type="text" class="form-control" name="direccion" required="" maxlength="70">
          
	  <label for="email">Email * :</label>
	  <input type="email" class="form-control" name="email" data-parsley-trigger="change" required="" maxlength="70">
            <label for="foto">Foto :(Sólo se permite formato .png y .jpg): </label>
            <p><input type="file" id="foto" name="foto"></p>
	  <br>
	  
          <label for="sexo">Sexo *:</label>
	  <p>
		M: <input type="radio" name="sexo" id="genderM" value="M" required="">
		F: <input type="radio" name="sexo" id="genderF" value="F">
	  </p>
          <label for="fono_fijo">Teléfono fijo * :</label>
	  <input type="text" class="form-control" name="fono_fijo" required="" maxlength="20">
	  <label for="fono_movil">Teléfono móvil * :</label>
	  <input type="text" class="form-control" name="fono_movil" required="" maxlength="20">
	  
	  <label for="comuna">Comuna</label>
          <p><select id="comuna" name="comuna" class="chosen-select" style="width:350px;" tabindex="2">
            <option value="-1"></option>
            <option value="1">Comuna 1</option>
            <option value="2">Comuna 2</option>
            <option value="3">Comuna 3</option>
          </select><p>
          
          <label for="estado">Estado</label>
          <p><select id="estado" name="estado" class="chosen-select" style="width:350px;" tabindex="2">
            <option value="-1"></option>
            <option value="1">Vigente</option>
            <option value="2">No Vigente</option>
          </select><p>
              
        <label for="fec_contrato">Fecha inicio contrato *:</label>
	  <p><input type="text" id="fec_contrato" required=""></p>
	  <!--<a class="button button-blue" href="javascript:;" onclick="validateform();">Guardar</a>-->
        
            <p><small>* requerido</small></p>
            <input type="submit" class="button button-blue" value="Guardar">
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
jQuery.validator.setDefaults({
  debug: true,
  success: "valid"
});
$( "#demo-form" ).validate({
  rules: {
    foto: {
      required: true,
      extension: "jpg|png"
    }
  }
});
</script>
</body>
</html>