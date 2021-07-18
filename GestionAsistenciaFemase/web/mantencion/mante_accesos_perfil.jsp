<%@ include file="/include/check_session.jsp" %>
<!doctype html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Accesos por Perfil usuario</title>
  <meta name="description" content="Accesos por Perfil usuario">
    <link href="<%=request.getContextPath()%>/css-varios/jquerysctipttop.css" rel="stylesheet" type="text/css">
  <script src="<%=request.getContextPath()%>/js-varios/jquery-1.12.4.min.js"></script>
  <script src="<%=request.getContextPath()%>/js-varios/bootstrap.min.js"></script>
  <script src="<%=request.getContextPath()%>/js/jquery.selectlistactions.js"></script>
  
  <link rel="stylesheet" href="<%=request.getContextPath()%>/jquery-plugins/chosen_v1.6.2/chosen.css">
  
  <script type="text/javascript">
      
      function validateform(){
            var items = document.getElementById("accesos_selected").options.length;
            if (items > 0){
                $("#accesos_selected option").prop("selected",true);
                //document.getElementById("myForm").submit();
                document.myForm.submit();
            }else alert('No hay accesos seleccionados...');
      }
      
  </script>
  
  <link rel="stylesheet" href="<%=request.getContextPath()%>/css-varios/bootstrap.min.css">
  <link rel="stylesheet" href="<%=request.getContextPath()%>/style/site.css">

  <!-- estilo para botones -->
  <link rel="stylesheet"
  href="<%=request.getContextPath()%>/jquery-plugins/chosen_v1.6.2/docsupport/style.css">
  
  <style type="text/css" media="all">
    /* fix rtl for demo */
    .chosen-rtl .chosen-drop { left: -9000px; }
   body { background-color:#fafafa; font-family:'Open Sans';}
   .container { margin:50px auto; max-width:660px;}
  </style>
  
  
  <!--[if lt IE 9]>
  <script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script>
  <![endif]-->
</head>

<body>
    <form name="myForm" action="<%=request.getContextPath()%>/NewServlet" method="POST">
	<div class="container">
	<header>
  <h1>Mantenedor de accesos por Perfil de usuario </h1>
  </header>
            
        <div>
  <em>Perfil de usuario</em> 
  <select id="perfil_id" name="perfil_id" class="chosen-select"
  style="width:350px;" tabindex="2">
    <option value="-1" selected="selected"></option>
    <option value="1">Administrador</option>
    <option value="1">RR.HH</option>
  </select>
   </div>

  <div>
  <em>M&oacute;dulo Sistema</em> 
  <select id="modulo_id" name="modulo_id" class="chosen-select"
  style="width:350px;" tabindex="2">
    <option value="-1" selected="selected"></option>
    <option value="1">Configuracion Sistema</option>
    <option value="2">Mantenedores</option>
    <option value="3">Reportes</option>
  </select>
   </div>

<p>&#xa0;</p>		
		<p>&nbsp;</p>
		<div class="row style-select">
			<div class="col-md-12">
				<div class="subject-info-box-1">
					<label>Accesos disponibles</label>
					<select name="accesos_base" multiple class="form-control" id="accesos_base">
						<option value="1">Admin Módulos Sistema</option>
						<option value="2">Admin Accesos</option>
						<option value="3">Admin Perfiles</option>
						<option value="4">Admin Usuarios</option>
					</select>
				</div>

				<div class="subject-info-arrows text-center">
					<br /><br />
					<input type='button' id='btnAllRight' value='>>' class="btn btn-default" /><br />
					<input type='button' id='btnRight' value='>' class="btn btn-default" /><br />
					<input type='button' id='btnLeft' value='<' class="btn btn-default" /><br />
					<input type='button' id='btnAllLeft' value='<<' class="btn btn-default" />
				</div>

				<div class="subject-info-box-2">
					<label>Accesos seleccionados</label>
					<select name="accesos_selected" multiple class="form-control" id="accesos_selected">
						
					</select>
				</div>

				<div class="clearfix">
                                <!-- botones -->
                                <a class="button button-blue" href="javascript:;" onclick="validateform();">Guardar</a>
                                </div>
			</div>
		</div>
	</div>
	
	<script>
       	
        $('#btnRight').click(function (e) {
            $('select').moveToListAndDelete('#accesos_base', '#accesos_selected');
            e.preventDefault();
        });

        $('#btnAllRight').click(function (e) {
            $('select').moveAllToListAndDelete('#accesos_base', '#accesos_selected');
            e.preventDefault();
        });

        $('#btnLeft').click(function (e) {
            $('select').moveToListAndDelete('#accesos_selected', '#accesos_base');
            e.preventDefault();
        });

        $('#btnAllLeft').click(function (e) {
            $('select').moveAllToListAndDelete('#accesos_selected', '#accesos_base');
            e.preventDefault();
        });
    </script>
        </form>
        
        <script type="text/javascript"
  src="<%=request.getContextPath()%>/jquery-plugins/chosen_v1.6.2/chosen.jquery.js">
  </script>
  <script type="text/javascript"
  src="<%=request.getContextPath()%>/jquery-plugins/chosen_v1.6.2/docsupport/prism.js" charset="utf-8">
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
</body>
</html>