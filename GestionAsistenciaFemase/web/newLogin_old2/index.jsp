<%@page import="cl.femase.gestionweb.vo.PropertiesVO"%>
<%
    PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
    String version      = appProperties.getVersion();
    String startYear    = appProperties.getStartYear();
    String currentYear  = appProperties.getCurrentYear();
    String labelAnios = startYear + "-" + currentYear;
    String mensaje = (String) session.getAttribute("mensaje");
    System.out.println("[GestionFemaseWeb]login/index.jsp]mensaje: " + mensaje);
	//if (mensaje==null) mensaje="&nbsp;";
    session.removeAttribute("mensaje");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Sistema de Gestion Asistencia-FEMASE</title>
	<meta charset="UTF-8">
	<link rel="icon" href="../images/femase_fav_icon_16x16.gif" type="image/gif" sizes="16x16">
	
	<!--<script src="vendor/jquery/jquery-3.2.1.min.js"></script>-->

        <!-- Importar Materialize CSS -->
        <link href="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/css/materialize.min.css" rel="stylesheet">
        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/js/materialize.min.js"></script>

<style>
        
	@import url(https://fonts.googleapis.com/css?family=Open+Sans);
	.btn {
	display: inline-block;
	*display: inline;
	*zoom: 1;
	padding: 4px 10px 4px;
	margin-bottom: 0;
	font-size: 13px;
	line-height: 18px;
	color: #333333;
	text-align: center;
	text-shadow: 0 1px 1px rgba(255, 255, 255, 0.75);
	vertical-align: middle;
	background-color: #f5f5f5;
	background-image: -moz-linear-gradient(top, #ffffff, #e6e6e6);
	background-image: -ms-linear-gradient(top, #ffffff, #e6e6e6);
	background-image: -webkit-gradient(linear, 0 0, 0 100%, from(#ffffff), to(#e6e6e6));
	background-image: -webkit-linear-gradient(top, #ffffff, #e6e6e6);
	background-image: -o-linear-gradient(top, #ffffff, #e6e6e6);
	background-image: linear-gradient(top, #ffffff, #e6e6e6);
	background-repeat: repeat-x;
	filter: progid:dximagetransform.microsoft.gradient(startColorstr=#ffffff, endColorstr=#e6e6e6, GradientType=0);
	border-color: #e6e6e6 #e6e6e6 #e6e6e6;
	border-color: rgba(0, 0, 0, 0.1) rgba(0, 0, 0, 0.1) rgba(0, 0, 0, 0.25);
	border: 1px solid #e6e6e6;
	-webkit-border-radius: 4px;
	-moz-border-radius: 4px;
	border-radius: 4px;
	-webkit-box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.2), 0 1px 2px rgba(0, 0, 0, 0.05);
	-moz-box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.2), 0 1px 2px rgba(0, 0, 0, 0.05);
	box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.2), 0 1px 2px rgba(0, 0, 0, 0.05);
	cursor: pointer;
	*margin-left: .3em;
}

.btn:hover,
.btn:active,
.btn.active,
.btn.disabled,
.btn[disabled] {
	background-color: #e6e6e6;
}

.btn-large {
	padding: 9px 14px;
	font-size: 15px;
	line-height: normal;
	-webkit-border-radius: 5px;
	-moz-border-radius: 5px;
	border-radius: 5px;
}

.btn:hover {
	color: #333333;
	text-decoration: none;
	background-color: #e6e6e6;
	background-position: 0 -15px;
	-webkit-transition: background-position 0.1s linear;
	-moz-transition: background-position 0.1s linear;
	-ms-transition: background-position 0.1s linear;
	-o-transition: background-position 0.1s linear;
	transition: background-position 0.1s linear;
}

.btn-primary,
.btn-primary:hover {
	text-shadow: 0 -1px 0 rgba(0, 0, 0, 0.25);
	color: #ffffff;
}

.btn-primary.active {
	color: rgba(255, 255, 255, 0.75);
}

.btn-primary {
	background-color: #4a77d4;
	background-image: -moz-linear-gradient(top, #6eb6de, #4a77d4);
	background-image: -ms-linear-gradient(top, #6eb6de, #4a77d4);
	background-image: -webkit-gradient(linear, 0 0, 0 100%, from(#6eb6de), to(#4a77d4));
	background-image: -webkit-linear-gradient(top, #6eb6de, #4a77d4);
	background-image: -o-linear-gradient(top, #6eb6de, #4a77d4);
	background-image: linear-gradient(top, #6eb6de, #4a77d4);
	background-repeat: repeat-x;
	filter: progid:dximagetransform.microsoft.gradient(startColorstr=#6eb6de, endColorstr=#4a77d4, GradientType=0);
	border: 1px solid #3762bc;
	text-shadow: 1px 1px 1px rgba(0, 0, 0, 0.4);
	box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.2), 0 1px 2px rgba(0, 0, 0, 0.5);
}

.btn-primary:hover,
.btn-primary:active,
.btn-primary.active,
.btn-primary.disabled,
.btn-primary[disabled] {
	filter: none;
	background-color: #4a77d4;
}

.btn-block {
	width: 100%;
	display: block;
}

* {
	-webkit-box-sizing: border-box;
	-moz-box-sizing: border-box;
	-ms-box-sizing: border-box;
	-o-box-sizing: border-box;
	box-sizing: border-box;
}

html {
	width: 100%;
	height: 100%;
	overflow: hidden;
}

body {
	width: 100%;
	height: 100%;
	font-family: 'Open Sans', sans-serif;
	background: #092756;
	background: -moz-radial-gradient(0% 100%, ellipse cover, rgba(104, 128, 138, .4) 10%, rgba(138, 114, 76, 0) 40%), -moz-linear-gradient(top, rgba(57, 173, 219, .25) 0%, rgba(42, 60, 87, .4) 100%), -moz-linear-gradient(-45deg, #ffffff 0%, #99CCCC 100%);
	background: -webkit-radial-gradient(0% 100%, ellipse cover, rgba(104, 128, 138, .4) 10%, rgba(138, 114, 76, 0) 40%), -webkit-linear-gradient(top, rgba(57, 173, 219, .25) 0%, rgba(42, 60, 87, .4) 100%), -webkit-linear-gradient(-45deg, #ffffff 0%, #99CCCC 100%);
	background: -o-radial-gradient(0% 100%, ellipse cover, rgba(104, 128, 138, .4) 10%, rgba(138, 114, 76, 0) 40%), -o-linear-gradient(top, rgba(57, 173, 219, .25) 0%, rgba(42, 60, 87, .4) 100%), -o-linear-gradient(-45deg, #ffffff 0%, #99CCCC 100%);
	background: -ms-radial-gradient(0% 100%, ellipse cover, rgba(104, 128, 138, .4) 10%, rgba(138, 114, 76, 0) 40%), -ms-linear-gradient(top, rgba(57, 173, 219, .25) 0%, rgba(42, 60, 87, .4) 100%), -ms-linear-gradient(-45deg, #ffffff 0%, #99CCCC 100%);
	background: -webkit-radial-gradient(0% 100%, ellipse cover, rgba(104, 128, 138, .4) 10%, rgba(138, 114, 76, 0) 40%), linear-gradient(to bottom, rgba(57, 173, 219, .25) 0%, rgba(42, 60, 87, .4) 100%), linear-gradient(135deg, #ffffff 0%, #99CCCC 100%);
	filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#3E1D6D', endColorstr='#092756', GradientType=1);
}

.login {
	position: absolute;
	top: 30%;
	left: 50%;
	margin: -150px 0 0 -150px;
	width: 300px;
	height: 300px;
}

.login h1 {
	color: #fff;
	text-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
	letter-spacing: 1px;
	text-align: center;
}

input {
	width: 100%;
	margin-bottom: 10px;
	/*background: rgba(0, 0, 0, 0.3);*/
	border: none;
	outline: none;
	padding: 10px;
	font-size: 13px;
	color: #333333;
	text-shadow: 1px 1px 1px rgba(0, 0, 0, 0.3);
	border: 1px solid rgba(0, 0, 0, 0.3);
	border-radius: 4px;
	box-shadow: inset 0 -5px 45px rgba(100, 100, 100, 0.2), 0 1px 1px rgba(255, 255, 255, 0.2);
	-webkit-transition: box-shadow .5s ease;
	-moz-transition: box-shadow .5s ease;
	-o-transition: box-shadow .5s ease;
	-ms-transition: box-shadow .5s ease;
	transition: box-shadow .5s ease;
}

input:focus {
	box-shadow: inset 0 -5px 45px rgba(100, 100, 100, 0.4), 0 1px 1px rgba(255, 255, 255, 0.2);
}
.alertError {
	  background-color: #f44336;
	  color: white;
	}
        
    /* Estilo personalizado para hacer el modal m�s peque�o */
    #emailModal {
      width: 300px; /* Ancho del modal */
      height: auto; /* Ajusta la altura autom�ticamente */
    }
    .modal-content {
      padding: 20px; /* Espaciado interno */
    }
    .modal-footer {
      text-align: center; /* Centrar el bot�n de cierre */
    }
</style>

</head>

<body>
<div class="login">
  <h1>Login<img src="images/logo_femase_01.png" width="335" height="121" /></h1>
    <form method="post" action="<%=request.getContextPath()%>/UserAuth">
      	<input type="text" id="username" name="username" placeholder="Username" required="required" />
        <input type="password" id="password" name="password" placeholder="Password" required="required" />
        <button type="submit" class="btn btn-primary btn-block btn-large">Iniciar sesi&oacute;n</button>
        <a class="waves-effect waves-light btn modal-trigger" href="#emailModal">Solicitar Clave</a>
        <div id="errorDiv" class="alertError">
			<span onclick="this.parentElement.style.display='none';">&times;</span> 
			<%=mensaje%>.
		</div>
	</form>
                        <h6>Versi&oacute;n <%=version%> Dise&ntilde;ado por <a href="http://www.femase.cl" target="_blank">FEMASE</a>&copy; <%=labelAnios%></h6>
</div>

<!-- Modal Structure -->
<div id="emailModal" class="modal">
  <div class="modal-content">
    <h5>Ingrese su correo</h5>
    <form id="emailForm">
      <div class="input-field">
        <input id="emailInput" type="text" placeholder="usuario" required>
        <label for="emailInput">Correo Electronico</label>
      </div>
      <p>@dt.gob.cl</p>
      <button type="submit" class="btn waves-effect waves-light">Enviar</button>
    </form>
  </div>
  <div class="modal-footer">
    <a href="#!" class="modal-close waves-effect waves-green btn-flat">Cerrar</a>
  </div>
</div>

<script>
		
            $(document).ready(function(){
                var x = document.getElementById("errorDiv");
                x.style.display = "none";
                <%if (mensaje != null) {%>
                    x.style.display = "block";
                <%}%>
				document.getElementById("username").focus();
            });
            
            // Inicializar el modal
            document.addEventListener('DOMContentLoaded', function() {
              var elems = document.querySelectorAll('.modal');
              M.Modal.init(elems);
            });

            // Manejar el env�o del formulario
            document.getElementById('emailForm').addEventListener('submit', function(event) {
              event.preventDefault();
              const userInput = document.getElementById('emailInput').value.trim();

              // Validar que el usuario haya ingresado algo antes de enviar
              if (userInput) {
                const email = `${userInput}@dt.gob.cl`;
                console.log('Correo enviado al backend:', email);
                alert(`Correo enviado: ${email}`);
              } else {
                alert('Por favor, ingrese un usuario v�lido.');
              }
            });
            
	</script>
</body>
</html>
