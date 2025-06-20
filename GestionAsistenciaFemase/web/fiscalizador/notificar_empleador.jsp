<%@page import="cl.femase.gestionweb.vo.EmpresaVO"%>
<%@page import="cl.femase.gestionweb.vo.UsuarioVO"%>
<%@page import="cl.femase.gestionweb.business.EmpresaBp"%>
<%
    UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
    
    EmpresaBp empresaBp     = new EmpresaBp(null);
    EmpresaVO empresa       = empresaBp.getEmpresaByKey(userConnected.getEmpresaId());
    String nombreEmpleador  = empresa.getNombre();
    String notificacionEmail= empresa.getNotificationEmail();
    
%>

<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <title>Notificar empleador</title>
  <!-- Materialize CSS -->
  <link href="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/css/materialize.min.css" rel="stylesheet" />
  <!-- Material Icons -->
  <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <style>
    .card {
      margin-top: 40px;
      max-width: 420px;
      margin-left: auto;
      margin-right: auto;
    }
    .input-field input[readonly] {
      background-color: #f5f5f5;
      color: #444;
      border-bottom: 1px solid #9e9e9e;
    }
    .btn-block {
      width: 100%;
    }
  </style>
</head>
<body class="grey lighten-4">
  <div class="container">
    <div class="card z-depth-2">
      <div class="card-content">
        <span class="card-title center-align">Notificar empleador</span>
        <form name="searchForm" 
              id="searchForm" 
              method="POST" 
              action="<%=request.getContextPath()%>/fiscaliza/NotificarEmpleadorServlet?action=init" autocomplete="off">
          <div class="input-field">
            <i class="material-icons prefix">business</i>
            <input id="nombreEmpleador" name="nombreEmpleador" type="text" value="<%=nombreEmpleador%>" readonly />
            <label for="empleador" class="active">Empleador</label>
          </div>
          <div class="input-field">
            <i class="material-icons prefix">mail_outline</i>
            <input id="emailNotificacion" name="emailNotificacion" type="text" value="<%=notificacionEmail%>" readonly />
            <label for="destinatario" class="active">Destinatario</label>
          </div>
          <div class="center-align" style="margin-top: 30px;">
            <button class="btn waves-effect waves-light btn-block" type="submit">
              Notificar
              <i class="material-icons right">send</i>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
  <!-- Materialize JS -->
  <script src="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/js/materialize.min.js"></script>
</body>
</html>
