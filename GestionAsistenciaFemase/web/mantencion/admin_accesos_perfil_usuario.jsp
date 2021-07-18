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
    ArrayList<PerfilUsuarioVO> perfiles   = (ArrayList<PerfilUsuarioVO>)session.getAttribute("perfiles_usuario");
    ArrayList<ModuloSistemaVO> modulos   = (ArrayList<ModuloSistemaVO>)session.getAttribute("modulos_sistema");
    LinkedHashMap<String, AccesoVO> accesosDisponibles   = (LinkedHashMap<String, AccesoVO>)request.getAttribute("accesos_disponibles");
    LinkedHashMap<String, ModuloAccesoPerfilVO> accesosPerfilModulo   = (LinkedHashMap<String, ModuloAccesoPerfilVO>)request.getAttribute("accesos_perfilmodulo");
    String perfilSelected = (String)session.getAttribute("perfilSelected");
    String moduloSelected = (String)session.getAttribute("moduloSelected");

    if (perfilSelected == null) perfilSelected = "-1";
    if (moduloSelected == null) moduloSelected = "-1";
	                  
%>
<!doctype html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Admin Accesos Perfil de usuario</title>
  <meta name="description" content="Admin Accesos Perfil de usuario">
    <link href="<%=request.getContextPath()%>/css-varios/jquerysctipttop.css" rel="stylesheet" type="text/css">
  <script src="<%=request.getContextPath()%>/js-varios/jquery-1.12.4.min.js"></script>
  <script src="<%=request.getContextPath()%>/js-varios/bootstrap.min.js"></script>
  <script src="js/jquery.selectlistactions.js"></script>
  
  <link rel="stylesheet" href="jquery-plugins/chosen_v1.6.2/chosen.css">
  
  <script type="text/javascript">
    
	function validateform(){
		var items = document.getElementById("accesos_selected").options.length;
		if (items > 0){
			$("#accesos_selected option").prop("selected",true);
			document.getElementById("modo").value='save';
			document.myForm.submit();
		}else {
                    //alert('No hay accesos seleccionados...');
                    if (confirm('¿Esta seguro que desea eliminar todos los accesos definidos?')){
                        document.getElementById("modo").value='save';
			document.myForm.submit();
                    }
                    //document.getElementById("accesos_selected").focus();
		}
	}
	  
	function validateModuloPerfil(){
		var selectidx = document.getElementById("perfil_id").selectedIndex;
                var selectidx2 = document.getElementById("modulo_id").selectedIndex;
		//alert('idx perfil seleccionado= ' + selectidx+', idx modulo seleccionado= ' + selectidx2);
		//selec=nombreform.nombreselect.selectedIndex 
		var success=true;
		if (document.getElementById("perfil_id").options[selectidx].value==="-1"){ 
			alert ('Seleccione un perfil...');
			document.getElementById("perfil_id").focus();
			success=false;
		} 
		
		if (document.getElementById("modulo_id").options[selectidx2].value==="-1"){ 
			alert ('Seleccione un modulo...');
			document.getElementById("modulo_id").focus();
			success=false;
		}
				
		if (success){
			
			document.getElementById("modo").value='load';
			//document.getElementById("myForm").submit();
			document.myForm.submit();
		}
			
	}
      
  </script>
  
  <link rel="stylesheet" href="<%=request.getContextPath()%>/css-varios/bootstrap.min.css">
  <link rel="stylesheet" href="css/site.css">

  <!-- estilo para botones -->
  <link rel="stylesheet"
  href="jquery-plugins/chosen_v1.6.2/docsupport/style.css">
  
  <style type="text/css" media="all">
    /* fix rtl for demo */
    .chosen-rtl .chosen-drop { left: -9000px; }
   body { background-color:#fafafa; font-family: times, serif; font-size:12pt; font-style:normal;}
   .container { margin:0px auto; max-width:660px;}
  </style>
  
  
  <!--[if lt IE 9]>
  <script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script>
  <![endif]-->
</head>

<body>
    <form name="myForm" action="<%=request.getContextPath()%>/AdmAccesosModuloPerfil" method="POST">
	<div class="container">
	
  <h2>Mantenedor de accesos por Perfil de usuario </h2>
             
        <div>
  <em>Perfil de usuario</em> 
  
      <select id="perfil_id" name="perfil_id" class="chosen-select" style="width:350px;" tabindex="2">
        <option value="-1"></option>
        
        <%  PerfilUsuarioVO obj1=null;
            String straux="";
            Iterator<PerfilUsuarioVO> iteraperfiles = perfiles.iterator();
            while(iteraperfiles.hasNext()){
                obj1 = iteraperfiles.next();
				straux="";
				if (Integer.parseInt(perfilSelected) == obj1.getId()){
					straux="selected";
				}%>
                <option value="<%=obj1.getId()%>" <%=straux%>><%=obj1.getNombre()%></option>
            <%}%>
        
      </select>
   <input name="modo" type="hidden" id="modo" value="start">
      </div>

  <div>
  <em>M&oacute;dulo Sistema</em> 
  <select id="modulo_id" name="modulo_id" class="chosen-select" style="width:350px;" tabindex="2">
        <option value="-1"></option>
    
        <%  
            ModuloSistemaVO obj2=null;
            Iterator<ModuloSistemaVO> iteramodulos = modulos.iterator();
            while(iteramodulos.hasNext()){
                obj2 = iteramodulos.next();
                straux="";
                if (Integer.parseInt(moduloSelected) == obj2.getModulo_id()){
                        straux="selected";
                }
        %>
            <option value="<%=obj2.getModulo_id()%>" <%=straux%>><%=obj2.getModulo_nombre()%></option>
        <%}%>
    
  </select>
   <span class="clearfix">
       <a class="button button-blue" href="javascript:;" onclick="validateModuloPerfil();">Ver accesos
       </a></span></div>
	
        <p>&nbsp;</p>
        <div class="row style-select">
                <div class="col-md-12">
				<div class="subject-info-box-1">
					<label>Accesos disponibles</label>
					<select name="accesos_base" multiple class="form-control" id="accesos_base">
                    <%   Collection c3 = accesosDisponibles.values();
						AccesoVO accesoaux1=null;
						Iterator<AccesoVO> iteraaccesos1 = c3.iterator();
						while(iteraaccesos1.hasNext()){
							accesoaux1 = iteraaccesos1.next();
						%>
							<option value="<%=accesoaux1.getId()%>"><%=accesoaux1.getLabel()%></option>
						<%}%>
        
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
						<%   Collection c4 = accesosPerfilModulo.values();
						ModuloAccesoPerfilVO accesoaux2=null;
						Iterator<ModuloAccesoPerfilVO> iteraaccesos2 = c4.iterator();
						while(iteraaccesos2.hasNext()){
							accesoaux2 = iteraaccesos2.next();
						%>
							<option value="<%=accesoaux2.getAccesoId()%>"><%=accesoaux2.getAccesoLabel()%></option>
						<%}%>
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
  src="jquery-plugins/chosen_v1.6.2/chosen.jquery.js">
  </script>
  <script type="text/javascript"
  src="jquery-plugins/chosen_v1.6.2/docsupport/prism.js" charset="utf-8">
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