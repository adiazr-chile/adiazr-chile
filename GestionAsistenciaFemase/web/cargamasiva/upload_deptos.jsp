<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="content-type" content="text/html; charset=UTF-8">
  <title>Carga Masiva de datos al sistema</title>
  
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
<h3>Sistema de Gestion - FEMASE- Carga masiva de Departamentos</h3>
    
    <div>
        <form id="upload-form" 
            action="<%=request.getContextPath()%>/UploadDepartamentosServlet" 
            method="post" 
            enctype="multipart/form-data">
            <!--
            <p>
            <label for="tipocarga">Carga a realizar:</label>
                <select name="tipocarga" id="item">
                  <option value="-1" selected>Seleccione</option>
                  <option value="empleados">Empleados</option>
                  <option value="regiones">Regiones</option>
                </select>
            </p>-->
            <label for="file">Seleccione archivo CSV:</label>
            <input type="file" name="file"> 
            <input type="submit" value="Cargar Archivo"> 
        </form>
    </div>
</div>
</body>
</html>
