<%@page import="cl.femase.gestionweb.vo.PropertiesVO"%>
<%
    PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
    String version      = appProperties.getVersion();
    String startYear    = appProperties.getStartYear();
    String currentYear  = appProperties.getCurrentYear();
    String labelAnios = startYear + "-" + currentYear;
    String mensaje = (String) session.getAttribute("mensaje");
    System.out.println("[login/index.jsp]mensaje: " + mensaje);
	//if (mensaje==null) mensaje="&nbsp;";
    session.removeAttribute("mensaje");
%>
<!DOCTYPE html>
<html lang="en">
<head>
	<title>Sistema de Gestion Asistencia-FEMASE</title>
	<meta charset="UTF-8">
        <link rel="icon" href="../images/femase_fav_icon_16x16.gif" type="image/gif" sizes="16x16">
	<meta name="viewport" content="width=device-width, initial-scale=1">
<!--===============================================================================================-->	
	<!--<link rel="icon" type="image/png" href="images/icons/favicon.ico"/>-->
<!--===============================================================================================-->
	<link rel="stylesheet" type="text/css" href="vendor/bootstrap/css/bootstrap.min.css">
<!--===============================================================================================-->
	<link rel="stylesheet" type="text/css" href="fonts/font-awesome-4.7.0/css/font-awesome.min.css">
<!--===============================================================================================-->
	<link rel="stylesheet" type="text/css" href="fonts/Linearicons-Free-v1.0.0/icon-font.min.css">
<!--===============================================================================================-->
	<link rel="stylesheet" type="text/css" href="fonts/iconic/css/material-design-iconic-font.min.css">
<!--===============================================================================================-->
	<link rel="stylesheet" type="text/css" href="vendor/animate/animate.css">
<!--===============================================================================================-->	
	<link rel="stylesheet" type="text/css" href="vendor/css-hamburgers/hamburgers.min.css">
<!--===============================================================================================-->
	<link rel="stylesheet" type="text/css" href="vendor/animsition/css/animsition.min.css">
<!--===============================================================================================-->
	<link rel="stylesheet" type="text/css" href="vendor/select2/select2.min.css">
<!--===============================================================================================-->	
	<link rel="stylesheet" type="text/css" href="vendor/daterangepicker/daterangepicker.css">
<!--===============================================================================================-->
	<link rel="stylesheet" type="text/css" href="css/util.css">
	<link rel="stylesheet" type="text/css" href="css/main.css">
<!--===============================================================================================-->
<style>
    .alertError {
          background-color: #f44336;
          color: white;
        }
    
</style>

</head>
<body style="background-color: #999999;">
	
	<div class="limiter">
		<div class="container-login100">
			<div class="login100-more" style="background-image: url('images/bg-03.jpg');"></div>

			<div class="wrap-login100 p-l-50 p-r-50 p-t-72 p-b-50">
				<form method="post" action="<%=request.getContextPath()%>/UserAuth">
					<span class="login100-form-title p-b-59"><img src="images/logo_femase_01.png" width="335" height="121"></span>
                                        <div class="wrap-input100 validate-input" data-validate="Se requiere nombre de usuario">
						<span class="label-input100">Nombre de usuario</span>
						<input class="input100" type="text" id="username" name="username" placeholder="Nombre de usuario..." tabindex="0" required>
						<span class="focus-input100"></span>
					</div>

					<div class="wrap-input100 validate-input" data-validate = "Se requiere clave">
						<span class="label-input100">Clave</span>
						<input class="input100" type="password" id="password" name="password" placeholder="*************" tabindex="1" required>
						<span class="focus-input100"></span>
					</div>

					<div class="container-login100-form-btn">
						<div class="wrap-login100-form-btn">
							<div class="login100-form-bgbtn"></div>
							<button class="login100-form-btn">
								Ingresar
							</button>
						</div>

					</div>
                    <div id="errorDiv" class="alertError">
            	<span onclick="this.parentElement.style.display='none';">&times;</span> 
            	<%=mensaje%>.
        	</div>
				</form>
                <div>
          <h6>Versi&oacute;n <%=version%> Designed by <a href="http://www.femase.cl">FEMASE</a>&copy; <%=labelAnios%>
          </h6>
        </div>
			</div>
		</div>
	</div>
	
<!--===============================================================================================-->
	<script src="vendor/jquery/jquery-3.2.1.min.js"></script>
<!--===============================================================================================-->
	<script src="vendor/animsition/js/animsition.min.js"></script>
<!--===============================================================================================-->
	<script src="vendor/bootstrap/js/popper.js"></script>
	<script src="vendor/bootstrap/js/bootstrap.min.js"></script>
<!--===============================================================================================-->
	<script src="vendor/select2/select2.min.js"></script>
<!--===============================================================================================-->
	<script src="vendor/daterangepicker/moment.min.js"></script>
	<script src="vendor/daterangepicker/daterangepicker.js"></script>
<!--===============================================================================================-->
	<script src="vendor/countdowntime/countdowntime.js"></script>
<!--===============================================================================================-->
	<script src="js/main.js"></script>

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