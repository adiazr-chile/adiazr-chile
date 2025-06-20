<%
	String mensaje = (String) session.getAttribute("mensaje");
        System.out.println("[GestionFemaseWeb]index.jsp]mensaje: " + mensaje);
        session.removeAttribute("mensaje");
%>
<!DOCTYPE html>
<html lang="en">
<head>
	<title>Sistema de Gestion Asistencia-FEMASE</title>
        <meta charset="UTF-8">
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
    <link rel="icon" href="../images/femase_fav_icon_16x16.gif" type="image/gif" sizes="16x16">
    
    <style>
        .alertError {
          background-color: #f44336;
          color: white;
        }

        .closebtn {
          margin-left: 15px;
          color: white;
          font-weight: bold;
          float: right;
          font-size: 22px;
          line-height: 20px;
          cursor: pointer;
          transition: 0.3s;
        }

        .closebtn:hover {
          color: black;
        }
    </style>

</head>
<body style="background-color: #666666;">
    <div class="limiter">
        <div class="container-login100">
            <div class="wrap-login100">
                <form method="post" class="login100-form validate-form" action="<%=request.getContextPath()%>/UserAuth">
                    <span class="login100-form-title p-b-43">
                    <img src="../images/logo_femase_01.png" width="335" height="121"><br>
                    Gesti&oacute;n de Asistencia</span>
					
                    <div class="wrap-input100 validate-input">
                        <input class="input100" name="username" type="text" id="username" size="20" maxlength="15">
                        <span class="focus-input100"></span>
                        <span class="label-input100">Usuario</span>
                    </div>
					
                    <div class="wrap-input100 validate-input" data-validate="Clave es requerida">
                        <input class="input100" name="password" type="password" id="password" size="12" maxlength="12">
                        <span class="focus-input100"></span>
                        <span class="label-input100">Clave</span>
                    </div>

                    <div class="flex-sb-m w-full p-t-3 p-b-32">
                        <div class="contact100-form-checkbox">
                            <input class="input-checkbox100" id="ckb1" type="checkbox" name="remember-me">
                            <label class="label-checkbox100" for="ckb1">
                                    Recordarme
                            </label>
                        </div>
                        <div id="errorDiv" class="alertError">
                          <span class="closebtn" onclick="this.parentElement.style.display='none';">&times;</span> 
                          <%=mensaje%>.
                        </div>
                        
                        <!--<div>
                            <a href="#" class="txt1">
                                Forgot Password?
                            </a>
                        </div>-->
                    </div>
			

                    <div class="container-login100-form-btn">
                            <button class="login100-form-btn">
                                    Login
                            </button>
                    </div>

                    <div class="text-center p-t-46 p-b-20">
                            <span class="txt2">
                                Versi&oacute;n 2.6.3 Dise&ntilde;ado por <a href="http://www.femase.cl">FEMASE</a>&copy; 2017-2019
                            </span>
                    </div>

                    
                </form>

                <div class="login100-more" style="background-image: url('images/fondo_2.png');">
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