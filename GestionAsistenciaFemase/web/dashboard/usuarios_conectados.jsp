<%@page import="cl.femase.gestionweb.vo.UsuariosByPerfilVO"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.concurrent.ConcurrentHashMap"%>
<%@page import="cl.femase.gestionweb.vo.UsuarioVO"%>
<%
ConcurrentHashMap<String, UsuarioVO> usuariosConectados = 
    (ConcurrentHashMap<String, UsuarioVO>) request.getAttribute("usuarios_conectados");

List<UsuariosByPerfilVO> summary = 
    (List<UsuariosByPerfilVO>)request.getAttribute("summary_perfiles_usuarios_conectados");
%>


<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Usuarios Conectados - Dashboard</title>
  <!-- Bootstrap CSS -->
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
  <!-- Bootstrap Icons -->
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
  <!-- DataTables CSS -->
  <link rel="stylesheet" href="https://cdn.datatables.net/1.13.7/css/dataTables.bootstrap5.min.css">
  <!-- DataTables Buttons CSS -->
  <link rel="stylesheet" href="https://cdn.datatables.net/buttons/2.4.1/css/buttons.bootstrap5.min.css">
  <style>
    body {
      background: linear-gradient(135deg, #f8fafc 0%, #e3edfa 100%);
      font-family: 'Segoe UI', Arial, sans-serif;
      min-height: 100vh;
    }
    .dashboard-header {
      background: #2673b8;
      color: #fff;
      padding: 1px 0 28px 0;
      text-align: center;
      border-bottom-left-radius: 22px;
      border-bottom-right-radius: 22px;
      margin-bottom: 26px;
      box-shadow: 0 4px 16px #b3c6e4;
    }
    .dashboard-header h1 {
      font-size: large;
      letter-spacing: 1px;
    }
    .summary-cards {
      margin: -56px auto 30px auto;
      display: flex;
      gap: 32px;
      justify-content: center;
      max-width: 65vw;
      position: relative;
      z-index: 10;
    }
    .summary-card {
      background: #fff;
      border-radius: 18px;
      box-shadow: 0 2px 14px #c6d1e6d4;
      padding: 10px 25px;
      text-align: center;
      min-width: 170px;
      transition: box-shadow 0.26s;
    }
    .summary-card:hover {
      box-shadow: 0 4px 22px #8facff55;
      transform: translateY(-3px) scale(1.03);
    }
    .summary-card h2 {
      margin: 0 0 10px 0;
      font-size: 2.4em;
      color: #2673b8;
      font-weight: 900;
    }
    .summary-card p {
      color: #667199;
      margin: 0;
      font-size: 1.15em;
      font-weight: 600;
    }
    .container-table {
      max-width: 1100px;
      margin: -18px auto 40px auto;
      background: #fff;
      border-radius: 20px;
      box-shadow: 0 2px 20px #b3c6e433;
      padding: 38px 34px 28px 34px;
    }
    .container-table-header {
      display: flex;
      align-items: center;
      gap: 20px;
      margin-bottom: 8px;
      justify-content: space-between;
    }
    .container-table-header .btn-refresh {
      padding: 6px 16px;
      font-size: 1.15em;
      border-radius: 6px;
      font-weight: 600;
      background-color: #2673b8;
      color: #fff;
      border: none;
      transition: background 0.14s;
    }
    .container-table-header .btn-refresh:hover {
      background-color: #174872;
      color: #fff;
    }
    /* Icon-only style for DataTables buttons */
    .dt-buttons .btn-icon {
      padding: 7px 11px !important;
      color: #595959 !important;
      background: #f8fafc !important;
      border: 1px solid #dee2e6 !important;
      border-radius: 6px !important;
      margin-right: 8px !important;
      transition: background 0.18s, color 0.18s;
    }
    .dt-buttons .btn-icon:hover {
      background: #2673b8 !important;
      color: #fff !important;
    }
    .dt-buttons .btn-icon:focus {
      box-shadow: 0 0 0 0.15rem #2673b87d !important;
    }
    .dataTables_wrapper .dataTables_filter input {
      border-radius: 5px;
    }
    table.dataTable thead th {
      background-color: #2673b8 !important;
      color: #fff !important;
      font-weight: 700;
      letter-spacing: 0.4px;
    }
    table.dataTable tbody tr {
      background: #f7faff;
      transition: background 0.18s;
    }
    table.dataTable tbody tr:hover {
      background: #ddeaff !important;
    }
    @media (max-width: 900px) {
      .summary-cards { flex-direction: column; align-items: center; gap: 18px; }
      .container-table { padding: 15px 4px; }
      .container-table-header { flex-direction: column; align-items: flex-start; gap: 8px; }
    }
    @media (max-width: 600px) {
      .dashboard-header { padding: 28px 0 17px 0; font-size: 1em;}
      .summary-card { padding: 20px 10px; min-width: 130px;}
    }
  </style>
</head>
<body>
  <div class="dashboard-header">
    <h1>Usuarios Conectados</h1>
    <p class="mb-0" style="font-size:1.12em;font-weight:400;">Monitorea en tiempo real la actividad de los usuarios en el sistema</p>
  </div>

  <div class="summary-cards">
    
      <%
        for (UsuariosByPerfilVO item : summary) {
            %>
            <div class="summary-card">
                <h2><%=item.getCountUsuarios()%></h2>
                <p><%=item.getNombrePerfil()%></p>
            </div>    
        <%}%>
    
  </div>

  <div class="container-table">
    <div class="container-table-header">
      <h4 class="mb-0">Usuarios conectados actualmente</h4>
      <button class="btn-refresh" onclick="refrescarUsuarios()">
        <i class="bi bi-arrow-clockwise"></i>
      </button>
    </div>
    <table id="tablaUsuarios" class="table table-striped table-bordered" style="width:100%">
      <thead>
        <tr>
          <th>Nombre</th>
          <th>Usuario</th>
          <th>Perfil</th>
          <th>IP</th>
          <th>Navegador</th>
          <th>Sistema Operativo</th>
          <th>Conectado desde</th>
        </tr>
      </thead>
      <tbody>
        <%
            for (Map.Entry<String, UsuarioVO> entry : usuariosConectados.entrySet()) {
                String sessionId = entry.getKey();
                UsuarioVO usuario       = entry.getValue();
                String nombreCompleto   = usuario.getNombreCompletoEncode();
                String username         = usuario.getUsername();
                String perfilUsuario    = usuario.getNomPerfil();
                String clientIP         = usuario.getClientIP();
                String fechaHoraUltimaConexion = usuario.getFechaHoraUltimaConexion();
                String browserName      = usuario.getBrowserName();
                String operatingSystem  = usuario.getOperatingSystem();
                
                System.out.println("[DashBoardServlet]"
                    + "SessionId: " + sessionId 
                    + ", Username: " + usuario.getUsername()
                    + ", fecha hora ultima conexion: " + usuario.getFechaHoraUltimaConexion()
                    + ", clientIP: " + usuario.getClientIP());
            
        %>  
                    <tr>
                      <td><%=nombreCompleto%></td>
                      <td><%=username%></td>
                      <td><%=perfilUsuario%></td>
                      <td><%=clientIP%></td>
                      <td><%=browserName%></td>
                      <td><%=operatingSystem%></td>
                      <td><%=fechaHoraUltimaConexion%></td>
                    </tr>
        <%}%>
      </tbody>
    </table>
  </div>

  <!-- JQuery -->
  <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
  <!-- Bootstrap JS -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
  <!-- DataTables JS -->
  <script src="https://cdn.datatables.net/1.13.7/js/jquery.dataTables.min.js"></script>
  <script src="https://cdn.datatables.net/1.13.7/js/dataTables.bootstrap5.min.js"></script>
  <!-- DataTables Buttons (Exportar) -->
  <script src="https://cdn.datatables.net/buttons/2.4.1/js/dataTables.buttons.min.js"></script>
  <script src="https://cdn.datatables.net/buttons/2.4.1/js/buttons.bootstrap5.min.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.10.1/jszip.min.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.2.7/pdfmake.min.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.2.7/vfs_fonts.js"></script>
  <script src="https://cdn.datatables.net/buttons/2.4.1/js/buttons.html5.min.js"></script>
  <script src="https://cdn.datatables.net/buttons/2.4.1/js/buttons.print.min.js"></script>
  <script>
    let dtUsuarios;
    $(document).ready(function() {
      dtUsuarios = $('#tablaUsuarios').DataTable({
        dom: '<"row mb-3"<"col-sm-12 col-md-9"B><"col-sm-12 col-md-3"f>>rtip',
        buttons: 
		[
			{ 
			  extend: 'csv', 
			  className: 'btn btn-icon', 
			  text: '<i class="bi bi-file-earmark fs-3 text-warning" ></i>', // fs-2: icono grande
			  titleAttr: 'Exportar CSV' 
			},
			{ 
			  extend: 'excel', 
			  className: 'btn btn-icon', 
			  text: '<i class="bi bi-file-earmark-excel fs-3 text-success"></i>',
			  titleAttr: 'Exportar Excel'
			},
			{ 
			  extend: 'pdf', 
			  className: 'btn btn-icon', 
			  text: '<i class="bi bi-file-earmark-pdf fs-3 text-danger"></i>',
			  titleAttr: 'Exportar PDF'
			}
		],
        language: {
          url: "//cdn.datatables.net/plug-ins/1.13.7/i18n/es-ES.json"
        }
      });
    });
    
        function refrescarUsuarios() {
            location.reload();
        }

  </script>
</body>
</html>
