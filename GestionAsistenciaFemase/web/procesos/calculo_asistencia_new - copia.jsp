<%@page import="cl.femase.gestionweb.vo.DetalleAsistenciaVO"%>
<%@page import="java.util.ArrayList"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="cl.femase.gestionweb.common.Constantes"%>
<%@ include file="/include/check_session.jsp" %>

<%@page import="cl.femase.gestionweb.vo.UsuarioVO"%>
<%@page import="cl.femase.gestionweb.vo.CargoVO"%>
<%@page import="cl.femase.gestionweb.vo.CentroCostoVO"%>
<%@page import="cl.femase.gestionweb.vo.DepartamentoVO"%>
<%@page import="cl.femase.gestionweb.vo.EmpresaVO"%>

<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.LinkedHashMap"%>

<%
    UsuarioVO theUser	= (UsuarioVO)session.getAttribute("usuarioObj");
    List<EmpresaVO> empresas            = (List<EmpresaVO>)session.getAttribute("empresas");
    List<CargoVO> cargos                = (List<CargoVO>)session.getAttribute("cargos");    
    String startDate = (String)session.getAttribute("startDate");
    String endDate = (String)session.getAttribute("endDate");
    
    ArrayList<DetalleAsistenciaVO> detalleAsistencia = 
        (ArrayList<DetalleAsistenciaVO>)request.getAttribute("detalle_asistencia_resumen");
    if (detalleAsistencia == null){
        detalleAsistencia = new ArrayList<>();
    }
    
    LinkedHashMap<String, ArrayList<DetalleAsistenciaVO>> hashMapDetalle = 
        (LinkedHashMap<String, ArrayList<DetalleAsistenciaVO>>)request.getAttribute("hashmap_detalle_asistencia");
    
    //ID_PERFIL_DIRECTOR
    System.out.println("[GestionFemaseWeb]calculoHoras.jsp]"
        + "startDate= "+startDate
        + ",endDate= "+endDate);
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8" />
    <title>Calculo Asistencia Moderna</title>
    <!-- Bootstrap 5 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- DataTables Bootstrap 5 -->
    <link href="https://cdn.jsdelivr.net/npm/datatables.net-bs5@1.13.8/css/dataTables.bootstrap5.min.css" rel="stylesheet">
    <!-- Bootstrap Datepicker -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-datepicker@1.10.0/dist/css/bootstrap-datepicker.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container mt-5">
  <div class="card shadow-sm">
    <div class="card-header bg-primary text-white">
      <h5 class="mb-0">Calcular asistencia</h5>
    </div>
    
    <!-- Spinner -->  
    <div id="spinnerCarga" class="text-center my-3 d-none">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Cargando...</span>
        </div>
    </div>
  
      
    <div class="card-body">
        <form id="filtroForm" class="row row-cols-lg-auto g-2 align-items-center" method="POST" action="<%=request.getContextPath()%>/DetalleAsistenciaController?action=calcular">
            <div class="col">
              <label for="empresaId" class="form-label mb-0">Empresa</label>
              <select class="form-select form-select-sm" id="empresaId" name="empresaId" required>
                <option value="-1">Empresa</option>
                <%
                Iterator<EmpresaVO> iteraempresas = empresas.iterator();
                while(iteraempresas.hasNext() ) {
                  EmpresaVO auxempresa = iteraempresas.next();
                %>
                  <option value="<%=auxempresa.getId()%>"><%=auxempresa.getNombre()%></option>
                <%
                }
                %>
              </select>
            </div>
            <div class="col">
              <label for="deptoId" class="form-label mb-0">Depto</label>
              <select class="form-select form-select-sm" id="deptoId" name="deptoId" required>
                <option value="-1">Depto</option>
              </select>
            </div>
            <div class="col">
              <label for="cencoId" class="form-label mb-0">Centro costo</label>
              <select class="form-select form-select-sm" id="cencoId" name="cencoId" required>
                <option value="-1">Centro</option>
              </select>
            </div>
            <div class="col">
              <label for="rut" class="form-label mb-0">Empleado</label>
              <select class="form-select form-select-sm" id="rut" name="rut" multiple required>
                <option value="-1">----------</option>
              </select>
            </div>
            <div class="col">
              <label for="startDate" class="form-label mb-0">Desde</label>
              <input type="text" class="form-control form-control-sm datepicker" id="startDate" name="startDate" autocomplete="off" required />
            </div>
            <div class="col">
              <label for="endDate" class="form-label mb-0">Hasta</label>
              <input type="text" class="form-control form-control-sm datepicker" id="endDate" name="endDate" autocomplete="off" required />
            </div>
            <div class="col align-self-end">
              <button type="submit" class="btn btn-success btn-sm" id="buscarBtn">Calcular</button>
            </div>
        </form>

    </div>
  </div>

  <div class="card mt-4 shadow-sm">
    <div class="card-header bg-secondary text-white">
      <h6 class="mb-0">Resultados</h6>
    </div>
    <div class="card-body">
      <table id="tablaEmpleados" class="table table-striped table-hover" style="width:100%">
        <thead>
          <tr>
            <th>RUT</th>
            <th>Nombre</th>
            <th>Centro de Costo</th>
            <th>Fecha</th>
            <th>Entrada Teórica</th>
            <th>Salida Teórica</th>
            <th>Entrada real</th>
            <th>Salida real</th>
            <th>Hrs presenciales</th>
            <th>Hrs Extras autorizadas</th>
            <th>Observación</th>
          </tr>
        </thead>
        <tbody>
          <!-- Datos dummy -->
          <% 
                for (int i = 0; i < detalleAsistencia.size(); i++) {
                    DetalleAsistenciaVO detalle = detalleAsistencia.get(i);
            %>  
                    <tr>
                        <td><%=detalle.getRut()%></td>
                        <td><%=detalle.getNombreEmpleado()%></td>
                        <td><%=detalle.getCencoNombre()%></td>
                        <td><%=detalle.getFechaEntradaMarca()%></td>
                        <td><%=detalle.getHoraEntradaTeorica()%></td>
                        <td><%=detalle.getHoraSalidaTeorica()%></td>
                        <td><%=detalle.getHoraEntrada()%></td>
                        <td><%=detalle.getHoraSalida()%></td>
                        <td><%=detalle.getHrsPresenciales()%></td>
                        <td><%=detalle.getHorasMinsExtrasAutorizadas()%></td>
                        <td><%=detalle.getObservacion()%></td>
                    </tr>
                <%}%>
         
            
        </tbody>
      </table>
    </div>
  </div>
</div>

<!-- Scripts -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/datatables.net@1.13.8/js/jquery.dataTables.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/datatables.net-bs5@1.13.8/js/dataTables.bootstrap5.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap-datepicker@1.10.0/dist/js/bootstrap-datepicker.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap-datepicker@1.10.0/dist/locales/bootstrap-datepicker.es.min.js"></script>

<script>
$(function() {
    // Bootrstrap datepicker español
    $('.datepicker').datepicker({
      format: 'yyyy-mm-dd',
      language: 'es',
      autoclose: true,
      todayHighlight: true
    });

    // --- BLOQUE SOLICITADO: combos dependientes ---
    $('#empresaId').change(function(event) {
        var empresaSelected = $("select#empresaId").val();
        $.get('<%=request.getContextPath()%>/JsonListServlet', {
                empresaID : empresaSelected
        }, function(response) {
            var select = $('#deptoId');
            select.find('option').remove();
            var newoption = "";
            newoption += "<option value='-1'>Seleccione Departamento</option>";
            for (i=0; i<response.length; i++) {
                newoption += "<option value='"+response[i].id+"'>"+response[i].nombre+"</option>";
            }
            $('#deptoId').html(newoption);
        });
    });

    //evento para combo depto
    $('#deptoId').change(function(event) {
        var empresaSelected = $("select#empresaId").val();
        var deptoSelected = $("select#deptoId").val();
        $.get('<%=request.getContextPath()%>/JsonListServlet', {
                empresaID : empresaSelected, deptoID : deptoSelected
        }, function(response) {
            var select = $('#cencoId');
            select.find('option').remove();
            var newoption = "";
            newoption += "<option value='-1'>Seleccione Centro de costo</option>";
            for (i=0; i<response.length; i++) {
                newoption += "<option value='"+response[i].id+"'>"+response[i].nombre+"</option>";
            }
            $('#cencoId').html(newoption);
        });
    });

    //evento para combo centro costo
    $('#cencoId').change(function(event) {
        var empresaSelected = $("select#empresaId").val();
        var deptoSelected = $("select#deptoId").val();
        var cencoSelected = $("select#cencoId").val();
        //alert('empresa: '+empresaSelected+',depto: '+deptoSelected+',cenco: '+cencoSelected);
        var sourceSelected = 'calculo_asistencia';
        $.get('<%=request.getContextPath()%>/JsonListServlet', {
            empresaID : empresaSelected, deptoID : deptoSelected, cencoID : cencoSelected, source: sourceSelected
        }, function(response) {
            var select = $('#rut');
            select.find('option').remove();
            var newoption = "";
            newoption += "<option value='todos'>Todos</option>";
            for (i=0; i<response.length; i++) {
                var auxNombre = '['+response[i].rut+'] '+response[i].nombres +
                    ' ' + response[i].apePaterno + ' '+response[i].apeMaterno;
                newoption += "<option value='" + response[i].rut + "'>" + auxNombre + "</option>";
            }
            $('#rut').html(newoption);
        });
    });

    // Inicializa DataTable
    let tabla = $('#tablaEmpleados').DataTable({
      language: {
        url: "//cdn.datatables.net/plug-ins/1.13.8/i18n/es-ES.json"
      }
    });

    $('#filtroForm').submit(function(e) {
        // Muestra el spinner al hacer clic en Buscar
        $('#spinnerCarga').removeClass('d-none');
        // (opcional: oculta la tabla durante la carga)
        $('#tablaEmpleados').hide();
    });

    // Cuando termines de cargar los registros en la tabla (por ejemplo, al recibir datos desde AJAX):
    function actualizarTablaEmpleados(datos) {
        // Lógica para refrescar la tabla con los datos
        tabla.clear().rows.add(datos).draw();

        // Oculta el spinner y muestra la tabla
        $('#spinnerCarga').addClass('d-none');
        $('#tablaEmpleados').show();
    }



    // Busca (actualiza tabla demo, conecta con tu backend según necesidad)
    /*$('#filtroForm').submit(function(e) {
        e.preventDefault();
     
        this.submit();
    });
    */
});
</script>
</body>
</html>
