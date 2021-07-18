<%@page import="java.util.LinkedHashMap"%>
<%@page import="cl.femase.gestionweb.vo.EmpleadoVO"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%
    String tipoUpload = (String)request.getAttribute("tipo");
        
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" type="text/css" href="style/style.css" media="screen" />
<title>Pagina Mensaje</title>
<style type="text/css">
<!--
body {
	background-color: #FFF;
}
-->
</style></head>

<body>
<div id="wrap">
  <div id="content">
    <div class="center">
      <h2><a href="#">Sistema Gestion - FEMASE</a></h2>
      <p>&nbsp;</p>
     <p align="justify"><h2><%=(String)session.getAttribute("mensaje")%></h2></p><br />
      <br />
    </div>
    <div style="clear: both;"></div>
  </div>
  
</div>
</body>
</html>
