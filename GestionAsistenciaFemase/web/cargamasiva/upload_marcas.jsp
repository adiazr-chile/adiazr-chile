<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>

<%
    
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="content-type" content="text/html; charset=UTF-8">
  <title>Carga de marcas al Sistema</title>
  <script src="../Jquery-JTable/Scripts/jquery-1.9.1.min.js" type="text/javascript"></script>
  <script src="../Jquery-JTable/Scripts/jquery-ui-1.10.0.min.js" type="text/javascript"></script>
  
  <script type="text/javascript">
    
   
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
</head>

<body>

<div>
<form id="upload-form" action="<%=request.getContextPath()%>/UploadMarcasServlet" 
    method="post" 
    enctype="multipart/form-data">    
        <h3>Sistema de Gestion - FEMASE- Carga de marcas</h3>
        
        <div>
            <label for="file">Seleccione archivo CSV:</label>
            <input type="file" name="file"> 
            <input type="submit" value="Cargar Archivo"> 
        </div>
</form>
</div>
</body>
</html>
