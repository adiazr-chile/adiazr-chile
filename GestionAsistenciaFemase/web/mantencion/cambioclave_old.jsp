<%@ include file="/include/check_session.jsp" %>

<%@ page import="cl.femase.gestionweb.vo.UsuarioVO"%>

<%
	UsuarioVO theUser	= (UsuarioVO)session.getAttribute("usuarioObj");
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

<script type="text/javascript">
	function submitForm(){
		if (document.form1.currentpwd.value == ''){
			alert('Debe ingresar una clave.');
			document.form1.currentpwd.focus();
			return false;
		}
		
		if (document.form1.clave.value == ''){
			alert('Debe ingresar una clave.');
			document.form1.clave.focus();
			return false;
		}
		
		//validar re-ingreso de clave
		if (document.form1.clave.value!=document.form1.reclave.value){
			alert('Las claves no coinciden.');
			document.form1.clave.focus();
			return false;
		}
		
		document.form1.submit();
	}
	
</script>

<link href="<%=request.getContextPath()%>/style/formulario.css" rel="stylesheet" type="text/css" />
<title>Cambio de clave</title>
</head>

<body>
<div class="table-container fullWidth float-left">
<form id="form1" name="form1" method="post" action="<%=request.getContextPath()%>/UsuariosController" target="_self">
  <div class="widget-header"><span>Cambio de Contrase&ntilde;a
      <input name="action" type="hidden" id="accion" value="changepass" />
        <input name="tipo" type="hidden" id="tipo" value="CCL" />
  </span></div>
  <div class="table-container float-left mediumPercentWidth">
    <div class="property-row">
      <div class="property-name">
        <label>Username</label>
      </div>
      <div>
        <input name="username" type="text"
                class="mediumPercentWidth" id="username" tabindex="0" value="<%=theUser.getUsername()%>"
                maxlength="30" readonly="readonly" />
      </div>
    </div>
    <div class="property-row">
      <div class="property-name">
        <label>Contrase&ntilde;a actual</label>
      </div>
      <div>
        <input type="password" maxlength="15" name="currentpwd" id="currentpwd" class="mediumPercentWidth" />
      </div>
    </div>
    <div class="property-row">
      <div class="property-name">
        <label>Nueva contrase&ntilde;a</label>
      </div>
      <div>
        <input name="clave" maxlength="15" type="password" id="clave" class="mediumPercentWidth" />
      </div>
    </div>
    <div class="property-row">
      <div class="property-name">
        <label>Reingrese contrase&ntilde;a</label>
      </div>
      <div>
        <input maxlength="15" name="reclave" type="password" id="reclave" class="mediumPercentWidth" />
      </div>
      <div >
        <input type="button" name="updbtn" id="updbtn" value="Guardar" onClick="submitForm()"/>

    </div>
    </div>
    
  </div>
  </form>
</div>

</body>
</html>
