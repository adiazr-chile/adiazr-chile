<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="content-type" content="text/html; charset=UTF-8">
  <title>Carga de dias de vacaciones</title>
  
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
<h3>Sistema de Gestion - FEMASE- Carga Informaci&oacute;n de vacaciones</h3>
<p>Formato Cabecera: empresa_id,rut_empleado,dias_progresivos,afp,fecha_certif_afp,dias_especiales,dias_adicionales</p>
<p>Linea de datos, ejemplos: <br> 
emp03,8888-K,0,-1,-1,N,0
  <br>
  emp03,6666-6,1,HABITAT,2020-04-01,N,0  </p>
<div>
  <form id="upload-form" 
            action="<%=request.getContextPath()%>/UploadDiasVacacionesServlet" 
            method="post" 
            enctype="multipart/form-data">
      <label for="file">Seleccione archivo CSV:</label>
            <input type="file" name="file"> 
            <input type="submit" value="Cargar Archivo"> 
        </form>
    </div>
</div>
</body>
</html>
