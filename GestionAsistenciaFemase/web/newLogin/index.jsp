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
<!DOCTYPE html>
<html lang="en">
<head>
  <title>Sistema de Gestion Asistencia-FEMASE</title>
  <link rel="icon" href="../images/femase_fav_icon_16x16.gif" type="image/gif" sizes="16x16"><!-- comment -->
  
    <!-- Importar Materialize CSS -->
  <link href="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/css/materialize.min.css" rel="stylesheet">
  <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/js/materialize.min.js"></script>
  <style>
    /* Estilo personalizado para hacer el modal más pequeño */
    #emailModal {
      width: 300px; /* Ancho del modal */
      height: auto; /* Ajusta la altura automáticamente */
    }
    .modal-content {
      padding: 20px; /* Espaciado interno */
    }
    .modal-footer {
      text-align: center; /* Centrar el botón de cierre */
    }
    
    /* Estilo para el formulario de inicio de sesión */
    .login-form {
      background-color: #ffffff;
      padding: 20px;
      border-radius: 10px;
      box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
    }
    
    /* Alinear botones */
    .button-row {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
	
	.btn,.btn-large,.btn-small {
		text-decoration: none;
		color: #fff;
		background-color: #3c7dff;
		text-align: center;
		letter-spacing: .5px;
		-webkit-transition: background-color .2s ease-out;
		transition: background-color .2s ease-out;
		cursor: pointer
	}
  </style>
</head>
<body>

<!-- Formulario de inicio de sesión -->
<!-- Formulario de inicio de sesi�n -->
<div class="container">
  <div class="row">
    <div class="col s12 m6 offset-m3">
      <div class="login-form">
        <img src="images/logo_femase_01.png" alt="Logo Institucional" style="width: 100%; max-width: 200px; margin: 20px auto;">
        
        <h4>Iniciar Sesi�n</h4>
        <form id="loginForm" method="post" action="<%=request.getContextPath()%>/UserAuth">
          <div class="input-field">
            <input id="username" name="username" type="text" required>
            <label for="username">Nombre de Usuario</label>
          </div>
          <div class="input-field">
            <input id="password" name="password" type="password" required>
            <label for="password">Contrase�a</label>
          </div>
          <div class="button-row">
            <button type="submit" class="btn waves-effect waves-light">Iniciar Sesi�n</button>
            <a class="waves-effect waves-light btn modal-trigger" href="#emailModal">Solicitar Clave</a>
          </div>
          
        </form>
      </div>
        <div class="version-info" style="text-align:center; font-size: 0.8em; color: #aaa; margin-top: 15px;">
        Versi&oacute;n <%=version%> Dise&ntilde;ado por <a href="http://www.femase.cl" target="_blank">FEMASE</a>&copy; <%=labelAnios%>
        </div>
        <div id="errorDiv" class="alertError">
            <span onclick="this.parentElement.style.display='none';">&times;</span> 
            <%=mensaje%>.
        </div>
    </div>
  </div>
</div>

<!-- Modal Structure -->
<div id="emailModal" class="modal">
  <div class="modal-content">
    <h5>Ingrese su correo</h5>
    <form id="emailForm" method="post" action="<%=request.getContextPath()%>/RequestAccessCodeServlet">
      <div class="input-field">
        <input id="emailInput" name="emailInput" type="text" placeholder="usuario" required>
        <label for="emailInput">Correo Electr&oacute;nico</label>
      </div>
      <p>@dt.gob.cl</p>
      <button type="submit" class="btn waves-effect waves-light">Solicitar clave</button>
    </form>
  </div>
  <div class="modal-footer">
    <a href="#!" class="modal-close waves-effect waves-green btn-flat">Cerrar</a>
  </div>
</div>

<script>
  // Inicializar el modal
  document.addEventListener('DOMContentLoaded', function() {
    var elems = document.querySelectorAll('.modal');
    M.Modal.init(elems);
  });

    // Manejar el env�o del formulario de inicio de sesi�n
    document.getElementById('loginForm').addEventListener('submit', function(event) {
      const username = document.getElementById('username').value.trim();
      const password = document.getElementById('password').value.trim();

      // Validar que el usuario haya ingresado algo antes de enviar
      if (!username || !password) {
        event.preventDefault(); // Solo si la validaci�n falla, se previene el env�o
        alert('Por favor, ingrese un usuario y clave correctos.');
      } else {
        console.log('Inicio de sesi�n:', username, password);
        //alert('Inicio de sesi�n exitoso');
        // El formulario se enviar� autom�ticamente si la validaci�n es correcta
      }
    });

  // Manejar el envío del formulario de correo electrónico
  document.getElementById('emailForm').addEventListener('submit', function(event) {
    //event.preventDefault();
    const userInput = document.getElementById('emailInput').value.trim();
    
    // Validar que el usuario haya ingresado algo antes de enviar
    if (userInput) {
      //const email = `${userInput}@dt.gob.cl`;
      console.log('Correo enviado al backend:', email);
      //alert(`Correo enviado: ${email}`);
    } else {
        event.preventDefault(); // Solo si la validaci�n falla, se previene el env�o
        alert('Por favor, ingrese un correo v&aacute;lido.');
    }
  });
</script>

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
