<%@page import="java.util.Iterator"%>
<%@page import="cl.femase.gestionweb.common.Constantes"%>
<%@page import="cl.femase.gestionweb.vo.AusenciaVO"%>
<%@page import="java.util.List"%>
<%@page import="cl.femase.gestionweb.vo.UsuarioVO"%>
<%@page import="java.time.LocalDate"%>
<%
    UsuarioVO theUser = (UsuarioVO)session.getAttribute("usuarioObj");
    List<AusenciaVO> ausencias = (List<AusenciaVO>)session.getAttribute("ausencias");
    boolean readOnly = false;
    if (theUser.getIdPerfil() == Constantes.ID_PERFIL_FISCALIZADOR || theUser.getIdPerfil() == Constantes.ID_PERFIL_EMPLEADO){
        readOnly = true;
    }
    LocalDate hoy = LocalDate.now();
    LocalDate primerDia = hoy.withDayOfMonth(1);
    LocalDate ultimoDia = hoy.withDayOfMonth(hoy.lengthOfMonth());
    String primerDiaStr = primerDia.toString();
    String ultimoDiaStr = ultimoDia.toString();
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Justificaciones de Inasistencias</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.datatables.net/1.12.1/css/dataTables.bootstrap5.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.13/css/select2.min.css">
    <style>
        body { background: #f4f8fc; }
        .card { box-shadow: 0 4px 12px rgba(0,0,0,0.08); border: none; }
        h1 { color: #3163ad; font-size: 1.5rem; margin-bottom: 1rem; }
        .btn-primary { background: #3163ad; border: none; }
        .btn-success { background: #2ecc71; border: none; }
        .btn-export { margin-left: 8px; }
        table.dataTable td, table.dataTable th { 
            font-size: 0.80em; 
            padding-top: 0.30rem !important; 
            padding-bottom: 0.20rem !important; 
        }
        .actions-btn { margin-right: 6px; }
        .select2-container .select2-selection--single { height: 32px; font-size: 0.9em; }
        .form-label { font-size: 0.92em; margin-bottom: 0.2em; }
        .form-select, .form-control {
            padding-top: 0.25rem;
            padding-bottom: 0.22rem;
            font-size: 0.95em;
            height: 32px !important;
            min-height: 32px !important;
            margin-bottom: 0.3em;
        }
        .card-body.row.align-items-end { row-gap: 0.25rem; }
        .table .actions-btn .material-icons, 
        table.dataTable .actions-btn .material-icons {
          font-size: 1.05em !important;
          vertical-align: middle;
          line-height: 1;
        }
        .actions-btn {
          padding: 1px 3px !important;
        }

        /* Toast base */
        .toast-visible {
          color: #f5f5f5 !important;
          font-size: 1em !important;
          box-shadow: 0 4px 10px rgba(38,50,56,0.14);
          border-radius: 8px !important;
          font-weight: 500;
          padding: 10px 18px !important;
          z-index: 2000;
          max-width: 300px;
          min-width: 200px;
          background: #263238 !important; /* Default elegant (info) */
        }

        /* Toast éxito */
        .toast-visible.success {
          background: #198754 !important; /* Verde Bootstrap */
          color: #fff !important;
        }

        /* Toast error */
        .toast-visible.error {
          background: #b71c1c !important; /* Rojo elegante profundo */
          color: #fff !important;
        }

          #toastError .toast-body {
            font-size: 0.95em !important;
            padding: 0;
            margin: 0;
          }

          .btn-close {
            filter: invert(100%);
          }

          @media (max-width: 600px){
            #toastError {
              left: 50%;
              right: auto;
              top: 10px;
              transform: translateX(-50%);
              width: 90vw;
              font-size: 0.95em;
              max-width: none;
            }
          }

        
    </style>
</head>
<body>
<div class="container-fluid my-4">
    <h1 class="mb-4"><span class="material-icons" style="font-size: 1.2em; vertical-align: middle;">assignment_turned_in</span> Justificaciones de Inasistencias</h1>
    <form id="formBusqueda" method="post">
        <div class="card mb-3">
            <div class="card-body row align-items-end">
                <div class="col-md-3">
                    <label for="empleado" class="form-label">Empleado</label>
                    <select id="empleado" name="empleado" class="form-select"></select>
                </div>
                <div class="col-md-2">
                    <label for="fechaInicio" class="form-label">Fecha inicio</label>
                    <input type="date" id="fechaInicio" name="fechaInicio" class="form-control" value="<%=primerDiaStr%>">
                </div>
                <div class="col-md-2">
                    <label for="fechaFin" class="form-label">Fecha fin</label>
                    <input type="date" id="fechaFin" name="fechaFin" class="form-control" value="<%=ultimoDiaStr%>">
                </div>
                <div class="col-md-3">
                    <label for="tipoAusencia" class="form-label">Tipo Ausencia</label>
                    <select id="tipoAusencia" name="tipoAusencia" class="form-select">
                        <option value="-1">Todas</option>
                        <%
                        if (ausencias != null) {
                        Iterator<AusenciaVO> itera3 = ausencias.iterator();
                        while(itera3.hasNext() ) {
                            AusenciaVO auxausencia = itera3.next();
                            if (auxausencia.getId() != 1){
                        %>
                            <option value="<%=auxausencia.getId()%>"><%=auxausencia.getNombre()%></option>
                        <%
                            }
                        }
                        }
                        %>
                    </select>
                </div>
                <div class="col-md-2 d-flex align-items-end">
                    <button id="buscarBtn" type="submit" class="btn btn-primary btn-sm w-100">
                        <span class="material-icons" style="font-size:1em;">search</span>
                    </button>
                </div>
            </div>
        </div>
    </form>
                    
    <!-- botones de exportacion y nuevo registro -->                    
    <div class="d-flex justify-content-end mb-2">
        <% if (!readOnly) { %>
            <button id="nuevoRegistroBtn" type="button"
                    class="btn btn-primary btn-sm btn-export me-2"
                    onclick="crearAusencia();">
                <span class="material-icons">add</span> Nuevo registro
            </button>
        <% } %>

        <button id="csvBtn" class="btn btn-success btn-sm btn-export">CSV</button>
        <button id="xlsxBtn" class="btn btn-success btn-sm btn-export">XLSX</button>
        <button id="toggleColumnsBtn" class="btn btn-secondary btn-sm btn-export ms-2">
            <span class="material-icons">unfold_more</span> Mostrar detalles
        </button>
    </div>


    <div class="card">
        <div class="card-body">
            <div class="table-responsive">
                <table id="ausenciasTable" class="table table-striped table-hover" style="width:100%">
                    <thead>
                        <tr>
                            <th>Correlativo</th>
                            <th>RUT Empleado</th>
                            <th>Nombre Empleado</th>
                            <th>Cargo Empleado</th>
                            <th>Fecha inicio</th>
                            <th>Fecha fin</th>
                            <th>ID Ausencia</th>
                            <th>Tipo Ausencia</th>
                            <th>Permite Hora</th>
                            <th>Hora inicio</th>
                            <th>Hora fin</th>
                            <th>Autorizada</th>
                            <th>RUT Autorizador</th>
                            <th>Nombre Autorizador</th>
                            <th>Cargo Autorizador</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <!-- Registros vía JS -->
                    </tbody>
                </table>
            </div>    
        </div>
    </div>
</div>

<div id="toastError" class="toast toast-visible align-items-center position-fixed top-0 end-0 m-3" role="alert" aria-live="assertive" aria-atomic="true">
  <div class="d-flex">
    <div class="toast-body" id="toastMsg"></div>
    <button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast"></button>
  </div>
</div>

<!-- Modal edición -->
<div class="modal fade" id="editModal" tabindex="-1" aria-labelledby="editModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <form id="editForm" class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="editModalLabel">Editar Ausencia</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body row">

        <div class="mb-2 col-md-6">
          <label class="form-label">RUT Empleado</label>
          <input type="text" class="form-control" id="editRutEmpleado" name="rutEmpleado" readonly>
        </div>
        <div class="mb-2 col-md-6">
          <label class="form-label">Nombre Empleado</label>
          <input type="text" class="form-control" id="editNombreEmpleado" name="nombreEmpleado" readonly>
        </div>
        <div class="mb-2 col-md-6">
          <label class="form-label">Cargo Empleado</label>
          <input type="text" class="form-control" id="editCargoEmpleado" name="nombreCargoEmpleado" readonly>
        </div>
        <div class="mb-2 col-md-6">
          <label class="form-label">Correlativo</label>
          <input type="text" class="form-control" id="editCorrelativo" name="correlativo" readonly>
        </div>
        <div class="mb-2 col-md-6">
          <label class="form-label">ID Ausencia</label>
          <select class="form-select" id="editIdAusencia" name="idAusencia">
            <%
                        if (ausencias != null) {
                        Iterator<AusenciaVO> itera4 = ausencias.iterator();
                        while(itera4.hasNext() ) {
                            AusenciaVO auxausencia = itera4.next();
                            if (auxausencia.getId() != 1){
                        %>
                            <option value="<%=auxausencia.getId()%>"><%=auxausencia.getNombre()%></option>
                        <%
                            }
                        }
                        }
                        %>
            <!-- ...agrega más valores de maqueta según tu sistema -->
          </select>
        </div>
        <div class="mb-2 col-md-6">
          <label class="form-label">Permite Hora</label>
          <select class="form-select" id="editPermiteHora" name="permiteHora">
            <option value="S">Si</option>
            <option value="N">No</option>
          </select>
        </div>
        <div class="mb-2 col-md-6">
          <label class="form-label">Hora inicio</label>
          <input type="time" step="1" class="form-control" id="editHoraInicio" name="horaInicioFullAsStr">
        </div>
        <div class="mb-2 col-md-6">
          <label class="form-label">Hora fin</label>
          <input type="time" step="1" class="form-control" id="editHoraFin" name="horaFinFullAsStr">
        </div>
        <div class="mb-2 col-md-6">
          <label class="form-label">Autorizador</label>
          <input type="text" class="form-control" id="editRutAutorizador" name="editRutAutorizador" readonly>
          <!--
            <select class="form-select" id="editRutAutorizador" name="rutAutorizador">
              <option value="12345678-9">Juan Autoriza [12345678-9]</option>
              <option value="98765432-1">Ana Apueva [98765432-1]</option>
            </select>
          -->
        </div>
        <div class="mb-2 col-md-6">
          <label class="form-label">Fecha inicio</label>
          <input type="date" class="form-control" id="editInicio" name="fechaInicioAsStr" required>
        </div>
        <div class="mb-2 col-md-6">
          <label class="form-label">Fecha fin</label>
          <input type="date" class="form-control" id="editFin" name="fechaFinAsStr" required>
        </div>
        <div class="mb-2 col-md-6">
          <label class="form-label">Autorizada</label>
          <select class="form-select" id="editAutorizada" name="ausenciaAutorizada">
            <option value="S">Si</option>
            <option value="N">No</option>
          </select>
        </div>
        
      </div>
      <div class="modal-footer">
        <button type="submit" class="btn btn-primary">Guardar cambios</button>
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
      </div>
    </form>
  </div>
</div>


<!-- Modal eliminación -->
<div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="deleteModalLabel">Eliminar Registro</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        ¿Estás seguro que quieres eliminar la ausencia correlativo <span id="deleteCorrelativo" class="fw-bold"></span>?
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-danger" id="confirmDeleteBtn">Eliminar</button>
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
      </div>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="https://cdn.datatables.net/1.12.1/js/jquery.dataTables.min.js"></script>
<script src="https://cdn.datatables.net/1.12.1/js/dataTables.bootstrap5.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/xlsx/0.18.5/xlsx.full.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/FileSaver.js/2.0.5/FileSaver.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.2.7/pdfmake.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.2.7/vfs_fonts.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.13/js/select2.min.js"></script>
<script>
    
    /**
    * 
    * */
    function crearAusencia(){
        document.location.href='<%=request.getContextPath()%>/jqueryform-detalle-ausencias/form_crear_detalle_ausencia.jsp';
    }
    
    function showToast(msg, type="info"){
        $('#toastMsg').text(msg);
        $('#toastError').removeClass('error success info');
        if (type === "error") {
            $('#toastError').addClass('error');
        } else if (type === "success") {
            $('#toastError').addClass('success');
        } else {
            $('#toastError').addClass('info');
        }
        let toastEl = document.getElementById('toastError');
        if (toastEl) {
            new bootstrap.Toast(toastEl, {delay:6000}).show();
        } else {
            alert(msg);
        }
    }

var tabla = null;
$(document).ready(function(){

    $('#fechaInicio').val('<%=primerDiaStr%>');
    $('#fechaFin').val('<%=ultimoDiaStr%>');

    $('#empleado').select2({
        theme: 'bootstrap-5',
        minimumInputLength: 2,
        placeholder: 'Buscar empleado por nombre o rut...',
        ajax: {
            url: '<%=request.getContextPath()%>/api/empleados',
            dataType: 'json',
            delay: 200,
            data: function (params) {
                return { term: params.term };
            },
            processResults: function (data) {
                return {
                    results: data.map(function(emp){
                        return {
                            id: emp.rut,
                            text: emp.nombreYpaterno + ' (' + emp.cencoNombre + ')'
                        };
                    })
                };
            }
        }
    });

    // DATATABLE: oculta columnas extras al inicio
    tabla = $('#ausenciasTable').DataTable({
        scrollX: true,
        columns: [
            { data: "correlativo"},
            { data: "rutEmpleado" },
            { data: "nombreEmpleado" },
            { data: "nombreCargoEmpleado" },
            { data: "fechaInicioAsStr" },
            { data: "fechaFinAsStr" },
            { data: "idAusencia", visible: false },     // ocultas al inicio
            { data: "nombreAusencia"},
            { data: "permiteHora"},
            { data: "horaInicioFullAsStr", visible: false },
            { data: "horaFinFullAsStr", visible: false },
            { data: "ausenciaAutorizada"},
            { data: "rutAutorizador", visible: false },
            { data: "nombreAutorizador", visible: false },
            { data: "nombreCargoAutorizador", visible: false },
            {
                data: null,
                orderable: false,
                render: function(data, type, row, meta) {
                    return `<button class="btn btn-sm btn-primary actions-btn editRow"><span class="material-icons">edit</span></button>
                            <button class="btn btn-sm btn-danger actions-btn deleteRow"><span class="material-icons">delete</span></button>`;
                }
                
               
            }
        ],
        paging: true,
        searching: true,
        ordering: true,
        pageLength: 10
    });

    function cargarRegistrosAjax() {
        let obj = {
            accion: 'listar',
            empleado: $('#empleado').val(),
            fechaInicio: $('#fechaInicio').val(),
            fechaFin: $('#fechaFin').val(),
            tipoAusencia: $('#tipoAusencia').val()
        };
        $.ajax({
            url: '<%=request.getContextPath()%>/api/detalle_ausencias',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(obj),
            dataType: 'json',
            success: function(data) {
                tabla.clear().rows.add(data).draw();
            }
        });
    }


    $('#formBusqueda').on('submit', function(e){
        e.preventDefault();
        let empleado = $('#empleado').val();
        let f1 = $('#fechaInicio').val();
        let f2 = $('#fechaFin').val();
        if (!empleado) { showToast('Debe seleccionar un empleado.', 'error'); return; }
        if (!f1) { showToast('Debe ingresar la fecha de inicio.', 'error'); return; }
        if (!f2) { showToast('Debe ingresar la fecha de fin.', 'error'); return; }
        if (f1 > f2) { 
            showToast('El rango de fechas no es válido: Fecha inicio debe ser menor o igual a fecha fin.', 'error'); 
            return; 
        }
        //if (!window.confirm('¿Desea enviar los criterios de búsqueda al backend?')) return;
        cargarRegistrosAjax();
    });

    let correlativoAEliminar = null;
    let tipoAusenciaAEliminar = null;
    $('#ausenciasTable tbody').on('click', '.deleteRow', function() {
        let row = tabla.row($(this).closest('tr'));
        let data = row.data();
        correlativoAEliminar = data.correlativo;
        tipoAusenciaAEliminar= data.nombreAusencia;
        $('#deleteCorrelativo').text(correlativoAEliminar+'[' + tipoAusenciaAEliminar + ']');
        let modal = new bootstrap.Modal(document.getElementById('deleteModal'));
        modal.show();
    });
    
    $('#confirmDeleteBtn').on('click', function(){
        $.ajax({
            url: '<%=request.getContextPath()%>/api/detalle_ausencias',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                accion: 'eliminar',
                id: correlativoAEliminar
            }),
            dataType: 'json',
            success: function(response){
                tabla.rows().every(function(){
                    var rowData = this.data();
                    if(String(rowData.correlativo) === String(correlativoAEliminar)) {
                        this.remove();
                    }
                });
                tabla.draw();
                if (response.exito) {
                    showToast(response.mensaje, 'success');
                } else {
                    showToast(response.mensaje, 'error');
                }
            }
        });
        bootstrap.Modal.getInstance(document.getElementById('deleteModal')).hide();
    });


    let correlativoAEditar = null;
    $('#ausenciasTable tbody').on('click', '.editRow', function() {
        let row = tabla.row($(this).closest('tr'));
        let data = row.data();
        correlativoAEditar = data.correlativo;
        // Campos readonly
        $('#editRutEmpleado').val(data.rutEmpleado);
        $('#editNombreEmpleado').val(data.nombreEmpleado);
        $('#editCargoEmpleado').val(data.nombreCargoEmpleado);
        $('#editCorrelativo').val(data.correlativo);
        // Select: ID Ausencia
        $('#editIdAusencia').val(data.idAusencia);
        // Select: Permite Hora
        $('#editPermiteHora').val(data.permiteHora);
        // Hora inicio/fin
        $('#editHoraInicio').val(data.horaInicioFullAsStr);
        $('#editHoraFin').val(data.horaFinFullAsStr);
        // Campo Autorizador readonly
        $('#editRutAutorizador').val(data.nombreAutorizador + '[' + data.rutAutorizador + ',' + data.nombreCargoAutorizador +']');
        // Fechas
        $('#editInicio').val(data.fechaInicioAsStr);
        $('#editFin').val(data.fechaFinAsStr);
        // Select: Autorizada
        $('#editAutorizada').val(data.ausenciaAutorizada);

        let modal = new bootstrap.Modal(document.getElementById('editModal'));
        modal.show();
    });

    $('#editForm').on('submit', function(e){
        e.preventDefault();
        // Recolectar todos los valores del formulario
        let obj = {
            accion: 'modificar',
            correlativo: $('#editCorrelativo').val(),
            rutEmpleado: $('#editRutEmpleado').val(),
            nombreEmpleado: $('#editNombreEmpleado').val(),
            nombreCargoEmpleado: $('#editCargoEmpleado').val(),
            idAusencia: $('#editIdAusencia').val(),
            permiteHora: $('#editPermiteHora').val(),
            horaInicioFullAsStr: $('#editHoraInicio').val(),
            horaFinFullAsStr: $('#editHoraFin').val(),
            rutAutorizador: $('#editRutAutorizador').val(),
            fechaInicioAsStr: $('#editInicio').val(),
            fechaFinAsStr: $('#editFin').val(),
            ausenciaAutorizada: $('#editAutorizada').val()
        };
        console.log(JSON.stringify(obj, null, 2));
        $.ajax({
            url: '<%=request.getContextPath()%>/api/detalle_ausencias',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(obj),
            dataType: 'json', // <- Esto fuerza jQuery a parsear la respuesta como JSON
            success: function(response){
                cargarRegistrosAjax();
                if (response.exito) {
                    showToast(response.mensaje, 'success');
                } else {
                    showToast(response.mensaje, 'error');
                }
            }
        });
        bootstrap.Modal.getInstance(document.getElementById('editModal')).hide();
    });


    function exportTable(type){
        let headers = ["RUT Empleado","Nombre Empleado","Cargo Empleado","Correlativo","Fecha inicio","Fecha fin","ID Ausencia","Tipo Ausencia","Permite Hora","Hora inicio","Hora fin","Autorizada","RUT Autorizador","Nombre Autorizador","Cargo Autorizador"];
        let datos = tabla.rows({ search: 'applied' }).data().toArray();
        let rows = datos.map(row => [
            row.rutEmpleado, row.nombreEmpleado, row.nombreCargoEmpleado, row.correlativo,
            row.fechaInicioAsStr, row.fechaFinAsStr, row.idAusencia, row.nombreAusencia, row.permiteHora,
            row.horaInicioFullAsStr, row.horaFinFullAsStr, row.ausenciaAutorizada, row.rutAutorizador,
            row.nombreAutorizador, row.nombreCargoAutorizador
        ]);
        if(type==="csv"){
            let csv = headers.join(",")+"\n";
            rows.forEach(r => {csv += r.join(",")+"\n";});
            let blob = new Blob([csv], {type:"text/csv"});
            saveAs(blob, "ausencias.csv");
        }
        if(type==="xlsx"){
            let wb = XLSX.utils.book_new();
            let ws = XLSX.utils.aoa_to_sheet([headers,...rows]);
            XLSX.utils.book_append_sheet(wb, ws, "Ausencias");
            XLSX.writeFile(wb, "ausencias.xlsx");
        }
        if(type==="pdf"){
            pdfMake.createPdf({
                content: [
                    {text: 'Registros de Ausencias', style:'header'},
                    {table:{headerRows:1, widths:Array(headers.length).fill('*'), body:[headers,...rows]}}
                ],
                styles:{header:{fontSize:16, bold:true, alignment:'center',margin:[0,0,0,10]}}
            }).download("ausencias.pdf");
        }
    }
    $('#csvBtn').click(()=>exportTable('csv'));
    $('#xlsxBtn').click(()=>exportTable('xlsx'));
    $('#pdfBtn').click(()=>exportTable('pdf'));

    // Botón para mostrar/ocultar columnas extra
    $('#toggleColumnsBtn').on('click', function(){
        let mostrando = tabla.column(6).visible();
        [6,7,8,9,10,11,12,13,14].forEach(function(idx) {
            var col = tabla.column(idx);
            col.visible(!mostrando);
        });
        if (!mostrando) {
            $(this).html('<span class="material-icons">unfold_less</span> Ocultar detalles');
        } else {
            $(this).html('<span class="material-icons">unfold_more</span> Mostrar detalles');
        }
    });

});
</script>
</body>
</html>
