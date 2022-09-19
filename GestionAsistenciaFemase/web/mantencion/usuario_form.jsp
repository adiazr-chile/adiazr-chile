<%@page import="cl.femase.gestionweb.vo.ProveedorCorreoVO"%>
<%@ include file="/include/check_session.jsp" %>
<%@page import="cl.femase.gestionweb.vo.EmpresaVO"%>
<%@page import="cl.femase.gestionweb.vo.UsuarioCentroCostoVO"%>
<%@page import="java.util.List"%>
<%@page import="cl.femase.gestionweb.vo.UsuarioVO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="cl.femase.gestionweb.vo.PerfilUsuarioVO"%>
<%@page import="cl.femase.gestionweb.vo.ModuloSistemaVO"%>
<%@page import="java.util.LinkedHashMap"%>
<%@ page import="java.util.Collection"%>
<%@ page import="java.util.Iterator"%>

<%
    List<EmpresaVO> empresas            = (List<EmpresaVO>)session.getAttribute("empresas");
    List<ProveedorCorreoVO> proveedoresCorreo   = (List<ProveedorCorreoVO>)session.getAttribute("proveedores_correo");
    //seteo cencos usuario 
    ArrayList<UsuarioCentroCostoVO> lstCencosUsuario = (ArrayList<UsuarioCentroCostoVO>)request.getAttribute("cencos_usuario");
    UsuarioVO auxUser = (UsuarioVO)request.getAttribute("info_usuario");
    ArrayList<PerfilUsuarioVO> perfiles   = (ArrayList<PerfilUsuarioVO>)session.getAttribute("perfiles");
    String msgError = (String)request.getAttribute("mensaje");
    System.out.println("[GestionFemaseWeb]usuario_form]msgError: " + msgError);
    
    String accion	= "create";
	String run		= "";
    String nombres	= "";
    String paterno	= "";
    String materno	= "";
	String empresaId= "";
    String email	= "";
    int estado		= 1;
    String username	= "";
    String password	= "";
    int perfilSelected = -1;
	
    if (auxUser != null){
        accion="update";
		
		run			= auxUser.getRunEmpleado();
        nombres		= auxUser.getNombres();
        paterno		= auxUser.getApPaterno();
        materno		= auxUser.getApMaterno();
		empresaId	= auxUser.getEmpresaId();
        email		= auxUser.getEmail();
        estado		= auxUser.getEstado();
        username	= auxUser.getUsername();
        password	= auxUser.getPassword();
	
        perfilSelected = auxUser.getIdPerfil();
    }
    
%>
<!doctype html>
<html>
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Admin Accesos Perfil de usuario</title>
  <meta name="description" content="Admin Usuario">
  <meta http-equiv=?Content-Type? content=?text/html; charset=UTF-8? />
  <script src="<%=request.getContextPath()%>/js/jquery-1.12.4.min.js"></script>
  <!--
  	<script type="text/javascript" src="<%=request.getContextPath()%>/jquery-plugins/chosen_v1.6.2/chosen.jquery.js">  </script>
  	<script type="text/javascript" src="<%=request.getContextPath()%>/jquery-plugins/chosen_v1.6.2/docsupport/prism.js" charset="utf-8"> </script>   
  	<link rel="stylesheet" href="<%=request.getContextPath()%>/jquery-plugins/chosen_v1.6.2/chosen.css">
  -->
  
  <!-- estilo para botones -->
  <link rel="stylesheet" href="<%=request.getContextPath()%>/jquery-plugins/chosen_v1.6.2/docsupport/style.css">
  <!-- Bootstrap core CSS -->
  <link rel="stylesheet" href="<%=request.getContextPath()%>/css/bootstrap.css" >
  
  <!-- para mostrar dialogo -->
  <link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/smoothness/jquery-ui.css">
  <script src="//code.jquery.com/jquery-1.12.4.js"></script>
  <script src="//code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
  <!-- fin -->
  
  <script type="text/javascript">
   
    function agregarCenco(){
        var cencoId = document.getElementById("cencoAdd").value;
        //alert('cencoKey: ' + cencoId);
        var cencoText = document.getElementById("cencoAdd").options[document.getElementById("cencoAdd").selectedIndex ].text;
        //alert('cencoId: ' + cencoId + ', cencoText: ' + cencoText);
        if ( $("#cencos_selected option[value='"+cencoId+"']").length === 0 ){
            $("#cencos_selected").append('<option value="'+cencoId+'">'+cencoText+'</option>');
        }

    }
	
    function quitarCenco(){
        $("#cencos_selected option:selected").remove();
    }
		
    function loadParams(){
        document.getElementById("estado").value='<%=estado%>';
		document.getElementById("empresaId").value='<%=empresaId%>';
        <%
        if (lstCencosUsuario !=null && lstCencosUsuario.size()>0){
            for(int x=0;x<lstCencosUsuario.size();x++) {
                UsuarioCentroCostoVO cenco = lstCencosUsuario.get(x);
                String cencoKey = cenco.getEmpresaId()+"|"+cenco.getDeptoId()+"|"+cenco.getCcostoId();
                %>
                $("#cencos_selected").append('<option value="<%=cencoKey%>"><%=cenco.getCcostoNombre()%></option>');  
            <%}
        }%>
    }
    
	    $(document).ready(function() {
				
             //agregar cenco adicional
             $('#empresaAdd').change(function(event) {
                 var empresaSelected = $("select#empresaAdd").val();
                 $.get('<%=request.getContextPath()%>/JsonListServlet', {
                         empresaID : empresaSelected
                 }, function(response) {
                        var select = $('#deptoAdd'); 
                        select.find('option').remove();
                        var newoption = "";
                        newoption += "<option value='-1'>Seleccione Departamento</option>";
                        for (i=0; i<response.length; i++) {
                            newoption += "<option value='"+response[i].id+"'>"+response[i].nombre+"</option>";
                        }
                        $('#deptoAdd').html(newoption);
                 });
             });
             //evento para combo depto
             $('#deptoAdd').change(function(event) {
                 var empresaSelected = $("select#empresaAdd").val();
                 var deptoSelected = $("select#deptoAdd").val();
                 $.get('<%=request.getContextPath()%>/JsonListServlet', {
                         empresaID : empresaSelected,deptoID : deptoSelected
                 }, function(response) {
                        var select = $('#cencoAdd');
                        select.find('option').remove();
                        var newoption = "";
                        newoption += "<option value='-1'>Seleccione Centro de costo</option>";
                        for (i=0; i<response.length; i++) {
                            newoption += "<option value='"+empresaSelected+"|"+deptoSelected+"|"+response[i].id+"'>"+response[i].nombre+"</option>";
                        }
                        $('#cencoAdd').html(newoption);
                 });
             });
             
            $( "#dialog" ).dialog({ autoOpen: false });
            
            <%if (msgError != null && msgError.compareTo("")!=0){%>
                $( "#dialog" ).dialog( "open" );
                $( "#dialog" ).dialog({
                    width: 500
                });
            <%}%>

         });

	var Fn = {
		// Valida el rut con su cadena completa "XXXXXXXX-X"
		validaRut : function (rutCompleto) {
			rutCompleto = rutCompleto.replace("‐","-");
			if (!/^[0-9]+[-|‐]{1}[0-9kK]{1}$/.test( rutCompleto ))
				return false;
			var tmp 	= rutCompleto.split('-');
			var digv	= tmp[1]; 
			var rut 	= tmp[0];
			if ( digv == 'K' ) digv = 'k' ;
			
			return (Fn.dv(rut) == digv );
		},
		dv : function(T){
			var M=0,S=1;
			for(;T;T=Math.floor(T/10))
				S=(S+T%10*(9-M++%6))%11;
			return S?S-1:'k';
		}
	}

	function validaRut(){
		if (Fn.validaRut( $("#run").val() )){
			$("#msgerror").hide();
		} else {
			$("#msgerror").show();
			$("#msgerror").html("El RUN no es v\u00E1lido");
		}	
	}

    function validateform(){
        
        var run     = document.getElementById("run").value;
		var nombres     = document.getElementById("nombres").value;
        var username    = document.getElementById("username").value;
        var auxpass     = document.getElementById("password").value;
        var auxrepass   = document.getElementById("repassword").value;
        var items = document.getElementById("cencos_selected").options.length;
        var runOk = true;
		//var rutValido = Fn.validaRut( $("#run").val() ));
		if (run !== ''){		
			if (Fn.validaRut( $("#run").val() )){
				$("#msgerror").hide();
			} else {
				$("#msgerror").show();
				$("#msgerror").html("El RUN no es v\u00E1lido");
				document.getElementById("run").focus();
				runOk = false;
			}
		}
		
		if (runOk && username !== ''){
            if (nombres !== ''){
                if (auxpass === auxrepass){
                    if (items >= 0){
                        $("#cencos_selected option").prop("selected",true);
                        document.myForm.submit();
                    }
                }else{
                    alert('Las claves no coinciden.');
                    document.getElementById("password").focus();
                }
            }else{
                alert('Ingrese nombres.');
                document.getElementById("nombres").focus();
            }
        }else{
            alert('Ingrese nombre de usuario.');
            document.getElementById("username").focus();
        }
    }
	
  </script>
  
  <style type="text/css" media="all">
    /* fix rtl for demo */
    .chosen-rtl .chosen-drop { left: -9000px; }
   	body { background-color:#fafafa; font-family: times, serif; font-size:12pt; font-style:normal;}
   .container { margin:0px auto; max-width:900px;}
   	
	#msgerror {
		font-size:1.4em;
	}
	
	.text-info {
		color: #31708f
	}

	a.text-info:focus,
	a.text-info:hover {
		color: #245269
	}
	
	.text-warning {
		color: #8a6d3b
	}

	a.text-warning:focus,
	a.text-warning:hover {
		color: #66512c
	}


  </style>
    
  <!--[if lt IE 9]>
  <script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script>
  <![endif]-->
</head>

<body onLoad="loadParams();">
    <form name="myForm" action="<%=request.getContextPath()%>/AdmUsuarioServlet" method="POST">
	<div class="container">
	
  <h2>Mantenci&oacute;n usuario </h2>
        
  <div>
  <em>RUN</em><input name="run" id="run" type="text" class="form-control" value="<%=run%>" size="20" maxlength="35" onBlur="validaRut();">
  Formato: 99999999-K<br>
  <p class="text-info" id="msgerror"></p>
  <em>Nombres</em><input name="nombres" id="nombres" type="text" class="form-control" value="<%=nombres%>" size="20" maxlength="35">
  <em>Ap. Paterno</em><input name="paterno" id="paterno" type="text" class="form-control" value="<%=paterno%>" size="30" maxlength="30">
  </div>
  <div>
  <em>Ap. Materno</em><input name="materno" id="materno" type="text" class="form-control" value="<%=materno%>" size="30" maxlength="30">
  <em>Email</em><input name="email" id="email" type="text" class="form-control" value="<%=email%>" size="35" maxlength="35">
  <em>Empresa</em>
  		<select id="empresaId" name="empresaId" style="width:150px;" required>
        	<option value="-1">Sin empresa</option>
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
    <div>
      <em>Perfil de usuario</em> 
           <select id="perfil_id" name="perfil_id" class="chosen-select" style="width:150px;" tabindex="2">
            <option value="-1"></option>
            
            <%  PerfilUsuarioVO obj1=null;
                String straux="";
                Iterator<PerfilUsuarioVO> iteraperfiles = perfiles.iterator();
                while(iteraperfiles.hasNext()){
                    obj1 = iteraperfiles.next();
                    straux="";
                    if (perfilSelected == obj1.getId()){
                        straux="selected";
                    }%>
                    <option value="<%=obj1.getId()%>" <%=straux%>><%=obj1.getNombre()%></option>
                <%}%>
            
          </select>
          <em>Estado</em> 
           <select id="estado" name="estado" class="chosen-select" style="width:100px;" tabindex="2">
            <option value="-1"></option>
            	<option value="1" selected>Vigente</option>
                <option value="2">No Vigente</option>
          	</select>
          <input name="action" type="hidden" id="action" value="<%=accion%>">
    </div>

	<!-- empresa, depto y centro costo por defecto del usuario-->
	<div></div>
<div>
  <em>Nombre de usuario</em><input name="username" id="username" type="text" class="form-control" value="<%=username%>" size="35" maxlength="35">
 </div>
 <div>
  <em>Clave</em><input name="password" id="password" type="password" class="form-control" value="<%=password%>" size="35" maxlength="35">
 </div>
 <div>
  <em>Re-ingrese clave</em><input name="repassword" id="repassword" type="password" class="form-control" value="<%=password%>" size="35" maxlength="35">
 </div>

         <p>&nbsp;</p>
         <div>
  <em><b>Restricci&oacute;n de acceso a informaci&oacute;n</b></em>
 </div>
 <div>
  <em>(Definici&oacute;n de los centros de costo a los que el usuario tendr&aacute; acceso)</em>
 </div>
         
         <!-- empresa, depto y centro costo adicionales para el usuario-->
	<div>
  		<label>Empresa
                <select name="empresaAdd" id="empresaAdd">
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
      <label for="deptoAdd">Departamento</label>
      <select id="deptoAdd" name="deptoAdd" style="width:200px;" required>
          <option value="-1">--------</option>
        </select>
      <label>Centro Costo
	      <select name="cencoAdd" id="cencoAdd">
	        <option value="-1" selected>----------</option>
        </select>
      </label>
      <a class="button button-blue" href="javascript:;" onClick="agregarCenco();">Agregar</a> </div>
     <div></div>
     
      <div></div>
      <div>
         <label>Centros de costo seleccionados</label><br>
		<select name="cencos_selected" multiple id="cencos_selected" style="width:250px">
			
	    </select>
        <a class="button button-blue" href="javascript:;" onclick="quitarCenco();">Quitar</a>
         </div>
         <div>
         <p></p>
          <!-- botones -->
          <a class="button button-blue" href="javascript:;" onclick="validateform();">Guardar</a>
          <a class="button button-blue" href="<%=request.getContextPath()%>/mantencion/usuarios.jsp">Volver</a>
          </div>
         </div>
     </div>
         
				<div class="clearfix">
               
			</div>
		
	
	
        </form>
          <div id="dialog" title="Alerta de Sistema"><%=msgError%></div>
          
        <script type="text/javascript">
			/*
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
			*/
	
  </script>
  
</body>
</html>