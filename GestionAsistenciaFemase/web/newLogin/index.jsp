<%@page import="cl.femase.gestionweb.vo.PropertiesVO"%>
<%
    PropertiesVO appProperties = (PropertiesVO)application.getAttribute("appProperties");
    String version = appProperties.getVersion();
    String startYear = appProperties.getStartYear();
    String currentYear = appProperties.getCurrentYear();
    String labelAnios = startYear + "-" + currentYear;
    String mensaje = (String) session.getAttribute("mensaje");
    session.removeAttribute("mensaje");
%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Sistema de Gestión Asistencia-FEMASE</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="icon" href="../images/femase_fav_icon_16x16.gif" type="image/gif" sizes="16x16">
  <!-- Materialize CSS -->
  <link href="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/css/materialize.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons" />
  <style>
    body {
      min-height: 100vh;
      background: linear-gradient(120deg,#5db0e9 0%,#4386d6 100%);
      display: flex;
      align-items: center;
      justify-content: center;
    }
    .login-form {
      background: #fff;
      border-radius: 12px;
      max-width: 380px;
      margin: 0 auto;
      box-shadow: 0 8px 20px rgba(40,68,86,0.10);
      padding: 30px 25px 20px 25px;
    }
    .login-form img {
      max-width: 150px;
      display: block;
      margin: 0 auto 18px auto;
    }
    .login-footer {
      margin-top: 12px;
      color: #757575;
      text-align: center;
      font-size: 0.9em;
    }
    @media (max-width: 480px) {
      .login-form { padding: 18px 8px; max-width: 97vw;}
      .login-form h4{font-size:1.2em;}
      .login-footer{font-size:0.8em;}
    }
    .button-row {
      display: flex;
      gap: 10px;
    }
    .button-row .btn {
      flex: 1;
    }
    #errorDiv {
      display: <%=(mensaje != null && !mensaje.isEmpty()) ? "block" : "none"%>;
      margin: 14px 0 0 0;
      padding: 8px 10px;
      border-radius:4px;
      background: #ef5350;
      color: #fff;
      font-size: 1em;
      text-align: center;
    }
    #errorDiv .close {
      position: absolute;
      right: 12px;
      cursor: pointer;
      color: #fff;
      font-size: 1.2em;
      top: 3px;
      background: none;
      border: none;
    }
  </style>
</head>
<body>
  <div class="login-form z-depth-3">
    <img src="images/logo_femase_01.png" alt="Logo Institucional">
    <h4 class="center-align">Iniciar Sesión</h4>
    <form id="loginForm" method="post" action="<%=request.getContextPath()%>/UserAuth" autocomplete="off">
      <div class="input-field">
        <input id="username" name="username" type="text" required autocomplete="off">
        <label for="username">Usuario</label>
      </div>
      <div class="input-field">
        <input id="password" name="password" type="password" required autocomplete="off">
        <label for="password">Contraseña</label>
      </div>
      <div class="button-row">
        <button type="submit" class="btn waves-effect waves-light blue">Entrar</button>
        <a class="btn waves-effect waves-light modal-trigger" href="#emailModal">Solicitar clave</a>
      </div>
    </form>
    <div id="errorDiv">
      <button class="close" onclick="document.getElementById('errorDiv').style.display='none';">&times;</button>
      <i class="material-icons prefix" style="vertical-align:middle;">error_outline</i>
      <%=mensaje%>
    </div>
    <div class="login-footer">
      Versión <%=version%> - Diseñado por <a href="http://www.femase.cl" target="_blank">FEMASE</a> &copy; <%=labelAnios%>
    </div>
  </div>

  <!-- Modal para solicitar clave -->
  <div id="emailModal" class="modal">
    <div class="modal-content">
      <h6>Recuperar clave</h6>
      <form id="emailForm" method="post" action="<%=request.getContextPath()%>/RequestAccessCodeServlet">
        <div class="input-field">
          <input id="emailInput" name="emailInput" type="text" required autocomplete="off">
          <label for="emailInput">Usuario institucional</label>
        </div>
        <span>@dt.gob.cl</span>
        <div style="margin-top:18px;text-align:center;">
          <button type="submit" class="btn blue waves-effect" style="width:100%;">Solicitar clave</button>
        </div>
      </form>
    </div>
    <div class="modal-footer">
      <a href="#!" class="modal-close waves-effect waves-blue btn-flat">Cancelar</a>
    </div>
  </div>

  <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/js/materialize.min.js"></script>
  <script>
    $(document).ready(function() {
      M.AutoInit();
      $('#username').focus();

      // Validación básica en frontend
      $('#loginForm').on('submit', function(e){
        const username = $('#username').val().trim();
        const password = $('#password').val().trim();
        if(!username || !password){
          e.preventDefault();
          M.toast({html:"Ingrese usuario y contraseña.",classes:'rounded red'});
        }
      });

      $('#emailForm').on('submit', function(e){
        const usuario = $('#emailInput').val().trim();
        if(!usuario){
          e.preventDefault();
          M.toast({html:"Ingrese su usuario institucional.",classes:'rounded red'});
        }
      });
    });
  </script>
</body>
</html>
