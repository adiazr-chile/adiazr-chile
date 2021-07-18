<%
    String version  = "4.0.0";
    String appAnios = "2017-2021";
    String mensaje = (String) session.getAttribute("mensaje");
    System.out.println("[login/index.jsp]mensaje: " + mensaje);
	//if (mensaje==null) mensaje="&nbsp;";
    session.removeAttribute("mensaje");
%>
<!DOCTYPE html>
<!--[if lt IE 7 ]> <html lang="en" class="ie6 ielt8"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="ie7 ielt8"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="ie8"> <![endif]-->
<!--[if (gte IE 9)|!(IE)]><!--> <html lang="en"> <!--<![endif]-->
<head>
<meta charset="utf-8">
<link rel="icon" href="../images/femase_fav_icon_16x16.gif" type="image/gif" sizes="16x16">
<title>Sistema de Gestion Asistencia-FEMASE</title>
<script src="jquery-3.2.1.min.js"></script>
<link rel="stylesheet" type="text/css" href="style.css" />
</head>
<body>
<div class="container">
    <section id="content">
        <form method="post" action="<%=request.getContextPath()%>/UserAuth">
            <h1><br>
            <img src="../images/logo_femase_01.png" width="302" height="109">Gesti&oacute;n de Asistencia</h1>
            <div>
                <input type="text" placeholder="Nombre de usuario" required autofocus id="username" name="username"/>
            </div>
            <div>
                <input type="password" placeholder="Clave" required id="password" name="password"/>
            </div>
            <div>
                <input type="submit" value="Entrar" />
                <!--<a href="#">Lost your password?</a>
                <a href="#">Register</a>-->
            </div>
            <div id="errorDiv" class="alertError">
            	<span onclick="this.parentElement.style.display='none';">&times;</span> 
            	<%=mensaje%>.
        	</div>
             
      </form><!-- form -->
        <div>
          <h6>Versi&oacute;n <%=version%> Designed by <a href="http://www.femase.cl">FEMASE</a>&copy; <%=appAnios%>
          </h6>
        </div>
  </section><!-- content -->
</div><!-- container -->
	<script>
		
        $(document).ready(function(){
          var x = document.getElementById("errorDiv");
          x.style.display = "none";
                <%if (mensaje != null) {%>
                        x.style.display = "block";
                <%}%>
        });

	</script>
</body>
</html>