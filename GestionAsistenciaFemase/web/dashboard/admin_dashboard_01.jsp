<%@page import="cl.femase.gestionweb.vo.AgrupadoAusenciaVO"%>
<%@page import="cl.femase.gestionweb.vo.DetalleAusenciaVO"%>
<%@page import="java.util.LinkedHashMap"%>
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

    LinkedHashMap<Integer, DetalleAusenciaVO> ausenciasEmpleados = 
        (LinkedHashMap<Integer, DetalleAusenciaVO>) request.getAttribute("empleados_ausencias");

    List<AgrupadoAusenciaVO> summaryAusencias = 
        (List<AgrupadoAusenciaVO>)request.getAttribute("summary_ausencias");
    
    int sumUsuarios = 0;    
    for (UsuariosByPerfilVO item : summary) {
        sumUsuarios += item.getCountUsuarios();
    }
    
    int sumAusencias = 0;   
    String labelsAusencias = "";
    String numsAusencias = "";
    for (AgrupadoAusenciaVO item : summaryAusencias) {
        sumAusencias += item.getCantidad();
        labelsAusencias += "'" + item.getNombreAusencia() + "',";
        numsAusencias += item.getCantidad() + ",";
    }
    if (!summaryAusencias.isEmpty()){
        labelsAusencias = labelsAusencias.substring(0, labelsAusencias.length()-1);
        numsAusencias = numsAusencias.substring(0, numsAusencias.length()-1);
    }
    
%>

<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Dashboard Asistencia</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.datatables.net/1.13.6/css/dataTables.bootstrap5.min.css" rel="stylesheet">
  <link href="https://cdn.datatables.net/buttons/2.4.2/css/buttons.bootstrap5.min.css" rel="stylesheet">
  <style>
    html { scroll-behavior: smooth; }
    body {
      background: #f5f7fa;
      font-family: 'Segoe UI', Arial, sans-serif;
    }
    .dashboard {
      max-width: 1200px;
      margin: 40px auto;
      padding: 32px 24px 24px 24px;
      background: #fff;
      border-radius: 14px;
      box-shadow: 0 2px 14px rgba(0,0,0,0.07);
    }
    .cards {
      display: flex;
      gap: 22px;
      margin-bottom: 26px;
      flex-wrap: wrap;
    }
    .card-dashboard {
      flex: 1 1 230px;
      min-width: 190px;
      background: linear-gradient(135deg, #2157C7 80%, #1a3166 100%);
      color: #fff;
      padding: 14px 0 12px 0;
      text-align: center;
      border-radius: 10px;
      font-size: 0.96rem;
      font-weight: 500;
      box-shadow: 0 3px 8px rgba(32,63,144,0.10);
      margin-bottom: 8px;
      transition: transform 0.18s, box-shadow 0.18s;
      position: relative;
      overflow: hidden;
      cursor: pointer;
      user-select: none;
    }
    .card-dashboard:active, .card-dashboard:focus, .card-dashboard:hover {
      transform: translateY(-2px) scale(1.022);
      box-shadow: 0 6px 10px rgba(32,63,144,0.19), 0 1.5px 7px rgba(0,0,0,0.08);
      z-index: 2;
    }
    .card-dashboard.ausencias { background: linear-gradient(135deg,#028a79 80%,#0b4035 100%);}
    .card-dashboard.atrasos { background: linear-gradient(135deg,#ff9800 80%,#a35a00 100%);}
    .card-dashboard.sinmarcar { background: linear-gradient(135deg,#e53935 80%,#741813 100%);}
    .card-dashboard .number {
      font-size: 1.6em;
      font-weight: bold;
      margin-bottom: 2px;
      margin-top: 4px;
      letter-spacing: 1px;
    }
    .main-section { display: flex; gap: 35px; flex-wrap: wrap; }
    .pie-chart-container {
      flex: 1.2 1 260px;
      min-width: 275px;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: start;
      padding-bottom: 10px;
    }
    .pie-legend { font-size: 1rem; margin-top: 13px;}
    .pie-legend span {
      display: inline-block;
      width: 16px;
      height: 16px;
      margin-right: 8px;
      border-radius: 2px;
      vertical-align: middle;
    }
    .tables-section {
      flex: 2 1 400px;
      min-width: 340px;
      display: flex;
      flex-direction: column;
      gap: 28px;
    }
    .table-title {
      font-size: 1.13rem;
      font-weight: 600;
      margin-bottom: 6px;
      color: #2157C7;
    }
    .dataTables_wrapper .dt-buttons .btn {
      margin-right: 6px;
      margin-bottom: 7px;
      font-size: 0.97em;
      border-radius: 22px !important;
      font-weight: 500;
      background: #2157C7 !important;
      border-color: #2157C7 !important;
    }
    .dataTables_length label, .dataTables_filter label {
      color: #2157C7;
      font-size: 1em;
    }
    th, td { text-align: left; vertical-align: middle!important; font-size: 1em;}
    @media (max-width: 1100px) {
      .main-section { flex-direction: column; gap: 24px;}
      .pie-chart-container { align-items: flex-start; min-width: 170px;}
    }
  </style>
  <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
  <script src="https://code.jquery.com/jquery-3.7.0.min.js"></script>
  <script src="https://cdn.datatables.net/1.13.6/js/jquery.dataTables.min.js"></script>
  <script src="https://cdn.datatables.net/1.13.6/js/dataTables.bootstrap5.min.js"></script>
  <script src="https://cdn.datatables.net/buttons/2.4.2/js/dataTables.buttons.min.js"></script>
  <script src="https://cdn.datatables.net/buttons/2.4.2/js/buttons.bootstrap5.min.js"></script>
  <script src="https://cdn.datatables.net/buttons/2.4.2/js/buttons.html5.min.js"></script>
  <script src="https://cdn.datatables.net/buttons/2.4.2/js/buttons.print.min.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.2.7/pdfmake.min.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.2.7/vfs_fonts.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.10.1/jszip.min.js"></script>
</head>
<body>
  <div class="dashboard">
      <div style="text-align:right; margin-bottom:10px;">
	  <button class="btn btn-outline-primary" onclick="location.reload();">
		Refrescar
		<i class="bi bi-arrow-clockwise"></i>
	  </button>
	</div>
    <!-- Tarjetas resumen con eventos -->
    <div class="cards">
      <div class="card-dashboard" tabindex="0" onclick="goToTable('tablaConexiones')">
        <div>Usuarios conectados hoy</div>
        <div class="number"><%=sumUsuarios%></div>
      </div>
      <div class="card-dashboard ausencias" tabindex="0" onclick="goToTable('tablaAusencias')">
        <div>Total de ausencias</div>
        <div class="number"><%=sumAusencias%></div>
      </div>
      <div class="card-dashboard atrasos" tabindex="0" onclick="goToTable('tablaAtrasos')">
        <div>Empleados con atrasos</div>
        <div class="number">3</div>
      </div>
      <div class="card-dashboard sinmarcar" tabindex="0" onclick="goToTable('tablaSinMarcar')">
        <div>Sin marcar asistencia</div>
        <div class="number">2</div>
      </div>
    </div>
    <div class="main-section">
      <div class="pie-chart-container">
        <canvas id="ausenciasPie" width="180" height="180"></canvas>
        <div class="pie-legend" id="pie-legend"></div>
      </div>
      <div class="tables-section">
        <div>
          <span id="tablaConexiones"></span>
          <div class="table-title">Conexiones recientes</div>
          <table class="table table-striped table-bordered display compact" style="width:100%">
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
        <div>
          <span id="tablaAtrasos"></span>
          <div class="table-title">Empleados con atrasos</div>
          <table class="table table-striped table-bordered display compact" style="width:100%">
            <thead>
              <tr>
                <th>Usuario</th>
                <th>Departamento</th>
                <th>Hora de llegada</th>
                <th>Minutos de atraso</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>ejemplo.nombre</td>
                <td>Departamento</td>
                <td>09:10</td>
                <td>20</td>
              </tr>
            </tbody>
          </table>
        </div>
        <div>
          <span id="tablaSinMarcar"></span>
          <div class="table-title">Sin marcar asistencia</div>
          <table class="table table-striped table-bordered display compact" style="width:100%">
            <thead>
              <tr>
                <th>Usuario</th>
                <th>Departamento</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>ejemplo.otracaso</td>
                <td>Operaciones</td>
              </tr>
            </tbody>
          </table>
        </div>
        <div>
          <span id="tablaAusencias"></span>
          <div class="table-title">Ausencias</div>
          <table class="table table-striped table-bordered display compact" style="width:100%">
            <thead>
              <tr>
                <th>Nombre</th>
                <th>RUN</th>
                <th>Cenco</th>
                <th>Ausencia</th>
                <th>Inicio</th>
                <th>Fin</th>
              </tr>
            </thead>
            <tbody>
                <%
                    for (Map.Entry<Integer, DetalleAusenciaVO> entry : ausenciasEmpleados.entrySet()) {
                        Integer clave = entry.getKey();
                        DetalleAusenciaVO ausencia = entry.getValue();
                        String nombre   = ausencia.getNombreEmpleado();
                        String run      = ausencia.getRutEmpleado();
                        String nombreAusencia = ausencia.getNombreAusencia();
                        String inicio = ausencia.getFechaInicioAsStr();
                        String fin = ausencia.getFechaFinAsStr();
                        String cencoNombre = ausencia.getCencoNombre();
                %>
                            <tr>
                                <td><%=nombre%></td>
                                <td><%=run%></td>
                                <td><%=cencoNombre%></td>
                                <td><%=nombreAusencia%></td>
                                <td><%=inicio%></td>
                                <td><%=fin%></td>
                            </tr>
                    <%}%>
                
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
  <script>
    // Foco y scroll suave a secciÃ³n correspondiente
    function goToTable(id) {
      const el = document.getElementById(id);
      if (el) {
        el.scrollIntoView({behavior: 'smooth', block: 'start'});
        el.focus();
      }
    }

    // Función para obtener colores automáticamente
    function getColorPalette(n) {
      // Paleta base, se puede ampliar o hacer más aleatoria
      const palette = ['#00c776','#ffb200','#2176ff','#e53935','#ff9800','#028a79','#6f42c1','#009688','#00bcd4','#ffb6b3','#f44336'];
      // Si hay más categorías que colores base, genera colores aleatorios
      while (palette.length < n) {
        //palette.push('#' + Math.floor(Math.random()*16777215).toString(16));
        palette.push('#' + Math.floor(Math.random() * 16777215).toString(16).padStart(6, '0'));
      }
      return palette.slice(0, n);
    }

    const colorArray = getColorPalette(<%=summaryAusencias.size()%>);
    //alert('legend pie backgroundColor: ' + backgroundColor);
    // Pie chart (ajusta datos desde backend si lo necesitas)
    const dataPie = {
      labels: [<%=labelsAusencias%>],
      datasets: [{
        data: [<%=numsAusencias%>],
        backgroundColor: colorArray
        //backgroundColor: ['#00c776','#ffb200','#2176ff','#e53935','#ff9800','#028a79']
      }]
    };
    
    const configPie = {
      type: 'pie',
      data: dataPie,
      options: {
        responsive: false,
        plugins: { legend: { display: false } }
      }
    };
    window.addEventListener('DOMContentLoaded', function() {
      // Chart.js
      
        new Chart(document.getElementById('ausenciasPie'), configPie);
      
        function renderPieLegend() {
            const legend = document.getElementById('pie-legend');
            legend.innerHTML = '';
            if (dataPie && dataPie.labels && dataPie.datasets && dataPie.datasets[0]) {
              dataPie.labels.forEach((label, i) => {
                const color = dataPie.datasets[0].backgroundColor[i];
                const value = dataPie.datasets[0].data[i];
                //alert('color: ' + color + ', label: ' + label + ', value: ' + value)
                legend.innerHTML += '<div style="margin-bottom:4px;"><span style="display:inline-block;width:16px;height:16px;margin-right:8px;border-radius:2px;vertical-align:middle;background:' + color + ';"></span>' + label + ' (' + value + ')</div>';
              });
            }
          }

      
      renderPieLegend();

      // DataTables espaÃ±ol, Bootstrap5 y botones export
      const dtOpts = {
        language: { url: 'https://cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json' },
        dom: "<'row'<'col-sm-6'l><'col-sm-6'f>>" +
             "<'row'<'col-sm-12'tr>>" +
             "<'row'<'col-sm-5'i><'col-sm-7 text-end'Bp>>",
        buttons: [
          {extend:'copyHtml5',text:'<i class="bi bi-clipboard"></i> Copiar',className:'btn btn-primary'},
          {extend:'excelHtml5',text:'<i class="bi bi-file-earmark-excel"></i> Excel',className:'btn btn-success'},
          {extend:'csvHtml5',text:'<i class="bi bi-filetype-csv"></i> CSV',className:'btn btn-warning'},
          {extend:'pdfHtml5',text:'<i class="bi bi-file-earmark-pdf"></i> PDF',className:'btn btn-danger'},
          {extend:'print',text:'<i class="bi bi-printer"></i> Imprimir',className:'btn btn-secondary'}
        ],
        paging: true,
        pageLength: 5,
        lengthMenu: [5,10,25,50,100],
        order: []
      };
      $('.table.display').DataTable(dtOpts);
    });
    
    function refrescar() {
        location.reload();
    }
  </script>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
</body>
</html>
