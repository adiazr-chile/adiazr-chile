<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="content-type" content="text/html; charset=UTF-8">
  <title>Carga de vacaciones</title>
  
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
  .label-rojo {
	color: #F00;
}
  </style>
</head>

<body>

<div>
<h3>Sistema de Gestion - FEMASE- Carga de vacaciones</h3>
<p>Formato Cabecera: empresa_id,rut_empleado,fecha_inicio,fecha_fin</p>
<p>Linea de datos, ejemplos: <br> 
emp03,13055851-8,2020-09-01,2020-09-04<br>
emp03,13055851-8,2020-09-07,2020-09-11
  </p>
<p class="label-rojo">Despu&eacute;s de cargar el archivo, debe ejecutar un 'C&aacute;lculo de saldo de Vacaciones'.</p>
<div>
  <form id="upload-form" 
            action="<%=request.getContextPath()%>/UploadVacacionesServlet" 
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
