<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.time.LocalDate"%>
<%@page import="cl.femase.gestionweb.common.Constantes"%>
<%@page import="cl.femase.gestionweb.vo.UsuarioVO"%>
<%
    UsuarioVO theUser = (UsuarioVO)session.getAttribute("usuarioObj");
    boolean readOnly = false;
    if (theUser.getIdPerfil() == Constantes.ID_PERFIL_FISCALIZADOR || theUser.getIdPerfil() == Constantes.ID_PERFIL_EMPLEADO){
        readOnly = true;
    }
    
    HashMap<Integer, String> tiposDeMarcas = (HashMap<Integer, String>)session.getAttribute("tiposDeMarcas");
        
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
    <title>Administración de marcas</title>

    <!-- CSS: Bootstrap + iconos + DataTables + Select2 -->
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
          background: #263238 !important;
        }
        .toast-visible.success {
          background: #198754 !important;
          color: #fff !important;
        }
        .toast-visible.error {
          background: #b71c1c !important;
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
    <h1 class="mb-4">
        <span class="material-icons" style="font-size: 1.2em; vertical-align: middle;">schedule</span>
        Administración de marcas
    </h1>

    <!-- Filtros -->
    <form id="formBusqueda" method="post">
        <div class="card mb-3">
            <div class="card-body row align-items-end">
                <div class="col-md-3">
                    <label for="empleado" class="form-label">Empleado</label>
                    <select id="empleado" name="empleado" class="form-select"></select>
                </div>
                <div class="col-md-2">
                    <label for="fechaInicio" class="form-label">Fecha desde</label>
                    <input type="date" id="fechaInicio" name="fechaInicio" class="form-control" value="<%=primerDiaStr%>">
                </div>
                <div class="col-md-2">
                    <label for="fechaFin" class="form-label">Fecha hasta</label>
                    <input type="date" id="fechaFin" name="fechaFin" class="form-control" value="<%=ultimoDiaStr%>">
                </div>
                <div class="col-md-3">
                    <label for="hashcode" class="form-label">Código hash</label>
                    <input type="text" id="hashcode" name="hashcode" class="form-control"
                           maxlength="32" placeholder="32 caracteres">
                </div>
                <div class="col-md-2 d-flex align-items-end">
                    <button id="buscarBtn" type="submit" class="btn btn-primary btn-sm w-100">
                        <span class="material-icons" style="font-size:1em;">search</span>
                    </button>
                </div>
            </div>
        </div>
    </form>

    <!-- botones de exportación -->
    <div class="d-flex justify-content-end mb-2">
        <button id="csvBtn" class="btn btn-success btn-sm btn-export">CSV</button>
        <button id="xlsxBtn" class="btn btn-success btn-sm btn-export">XLSX</button>
    </div>

    <!-- Tabla de marcas -->
    <div class="card">
        <div class="card-body">
            <div class="table-responsive">
                <table id="marcasTable" class="table table-striped table-hover" style="width:100%">
                    <thead>
                        <tr>
                            <th>Run</th>
                            <th>Tipo</th>
                            <th>Fecha hora</th>
                            <th>Correlativo</th>
                            <th>Dispositivo</th>
                            <th>Hashcode</th>
                            <th>Turno</th>
                            <th>Más Info</th>
                            <th>Id</th>
                            <th>Tipo marca manual</th>
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

<!-- Toast -->
<div id="toastError" class="toast toast-visible align-items-center position-fixed top-0 end-0 m-3"
     role="alert" aria-live="assertive" aria-atomic="true">
  <div class="d-flex">
    <div class="toast-body" id="toastMsg"></div>
    <button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast"></button>
  </div>
</div>

<!-- Modal edición marca -->
<div class="modal fade" id="editModal" tabindex="-1" aria-labelledby="editModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg modal-dialog-centered">
    <form id="editForm" class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="editModalLabel">Editar marca</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body row g-2">

        <input type="hidden" id="editId" name="id">
            <input type="hidden" class="form-control" id="editNombreEmpleado" name="nombreEmpleado">
            <input type="hidden" class="form-control" id="editFechaHoraKey" name="editFechaHoraKey">
            <input type="hidden" class="form-control" id="editHashcode" name="editHashcode">
            <input type="hidden" class="form-control" id="editTipoMarcaOriginal" name="editTipoMarcaOriginal">
        <div class="col-md-4">
          <label class="form-label">Rut</label>
          <input type="text" class="form-control" id="editRutEmpleado" name="rutEmpleado" readonly>
        </div>
        
        <div class="col-md-8">
          
        </div>

        <div class="col-md-4">
          <label class="form-label">Fecha marcación</label>
          <input type="date" class="form-control" id="editFecha" name="fecha">
        </div>
        <div class="col-md-4">
          <label class="form-label">Hora marcación</label>
          <input type="time" class="form-control" id="editHora" name="hora" step="1">
        </div>
        <div class="col-md-4">
          <label class="form-label">Evento/Tipo</label>
          <select class="form-select" id="editEvento" name="evento">
            <!-- llena según tu catálogo de eventos -->
            <%
                for (Map.Entry<Integer, String> entry : tiposDeMarcas.entrySet()) {
                    Integer key = entry.getKey();
                    String value = entry.getValue();
                    //System.out.println("Key: " + key + ", Value: " + value);
                  %>
                  <option value="<%=key%>"><%=value%></option>
            <%}%>
            
          </select>
        </div>

        <div class="col-12">
          <label class="form-label">Comentario</label>
          <textarea class="form-control" id="editComentario" name="comentario" rows="2"></textarea>
        </div>

      </div>
      <div class="modal-footer">
        <button type="submit" class="btn btn-primary btn-sm">Guardar cambios</button>
        <button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">Cancelar</button>
      </div>
    </form>
  </div>
</div>

<!-- Modal eliminación -->
<div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="deleteModalLabel">Eliminar registro</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        <p class="mb-2">¿Estás seguro que quieres eliminar la siguiente marca?</p>

        <div class="mb-2">
          <label class="form-label mb-0">Correlativo</label>
          <input type="text" class="form-control" id="deleteCorrelativo" readonly>
        </div>
        <div class="mb-2">
          <label class="form-label mb-0">Fecha marcación</label>
          <input type="text" class="form-control" id="deleteFecha" readonly>
        </div>
        <div class="mb-2">
          <label class="form-label mb-0">Hora marcación</label>
          <input type="text" class="form-control" id="deleteHora" readonly>
        </div>

        <!-- ocultos para enviar al backend -->
        <input type="hidden" id="deleteId">
        <input type="hidden" id="deleteRutEmpleado">
        <input type="hidden" id="deleteNombreEmpleado">
        <input type="hidden" id="deleteFechaHoraKey">
        <input type="hidden" id="deleteHashcode">
        <input type="hidden" id="deleteEvento">
        <input type="hidden" id="deleteComentario">

      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-danger btn-sm" id="confirmDeleteBtn">Eliminar</button>
        <button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">Cancelar</button>
      </div>
    </div>
  </div>
</div>


<!-- JS: Bootstrap, jQuery, DataTables, export, Select2 -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>

<script src="https://cdn.datatables.net/1.12.1/js/jquery.dataTables.min.js"></script>
<script src="https://cdn.datatables.net/1.12.1/js/dataTables.bootstrap5.min.js"></script>

<script src="https://cdnjs.cloudflare.com/ajax/libs/xlsx/0.18.5/xlsx.full.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/FileSaver.js/2.0.5/FileSaver.min.js"></script>

<script src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.13/js/select2.min.js"></script>

<script>
    let tabla = null;
    let idAEliminar = null;

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

    // hash válido: 32 caracteres hex (0-9, a-f, A-F)
    function hashValido(value) {
        if (!value) return false;
        const trimmed = value.trim();
        return /^[0-9a-fA-F]{32}$/.test(trimmed);
    }

    $(document).ready(function(){

        $('#fechaInicio').val('<%=primerDiaStr%>');
        $('#fechaFin').val('<%=ultimoDiaStr%>');

        // Empleado con Select2
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

        // DataTable de marcas
        tabla = $('#marcasTable').DataTable({
            scrollX: true,
            columns: [
                { data: "rutEmpleado" },
                { data: "nombreTipoMarca" },
                { data: "fechaHoraStr" },
                { data: "correlativo" },
                { data: "codDispositivo" },
                { data: "hashcode" },
                { data: "labelTurno" },
                { data: "masInfo" },
                { data: "id" },
                { data: "codTipoMarcaManual" },
                {
                    data: null,
                    orderable: false,
                    render: function(data, type, row, meta) {
                        return `
                            <button class="btn btn-sm btn-primary actions-btn editRow">
                                <span class="material-icons">edit</span>
                            </button>
                            <button class="btn btn-sm btn-danger actions-btn deleteRow">
                                <span class="material-icons">delete</span>
                            </button>`;
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
                accion     : 'listar',
                empleado   : $('#empleado').val(),
                fechaInicio: $('#fechaInicio').val(),
                fechaFin   : $('#fechaFin').val(),
                hashcode   : $('#hashcode').val()
            };
            $.ajax({
                url: '<%=request.getContextPath()%>/api/marcaciones',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(obj),
                dataType: 'json',
                success: function(data) {
                    tabla.clear().rows.add(data || []).draw();
                },
                error: function(){
                    showToast('Error al consultar marcas.', 'error');
                }
            });
        }

        // Buscar
        $('#formBusqueda').on('submit', function(e){
            e.preventDefault();

            const hash = $('#hashcode').val();
            const esHashValido = hashValido(hash);

            // Si hay hash válido, no validamos empleado ni fechas
            if (!esHashValido) {
                let empleado = $('#empleado').val();
                let f1 = $('#fechaInicio').val();
                let f2 = $('#fechaFin').val();

                if (!empleado) { showToast('Debe seleccionar un empleado.', 'error'); return; }
                if (!f1) { showToast('Debe ingresar la fecha de inicio.', 'error'); return; }
                if (!f2) { showToast('Debe ingresar la fecha de fin.', 'error'); return; }
                if (f1 > f2) {
                    showToast('El rango de fechas no es válido: inicio debe ser menor o igual a fin.', 'error');
                    return;
                }
            }

            cargarRegistrosAjax();
        });

        // Editar
        $('#marcasTable tbody').on('click', '.editRow', function() {
            let row = tabla.row($(this).closest('tr'));
            let data = row.data();
            if (!data) return;

            $('#editId').val(data.correlativo);
            $('#editRutEmpleado').val(data.rutEmpleado);
            $('#editNombreEmpleado').val('pepe');
            $('#editFechaHoraKey').val(data.fechaHoraKey);
            $('#editHashcode').val(data.hashcode);
            if (data.fechaHora && data.fechaHora.length >= 16){
                $('#editFecha').val(data.fechaHora.substring(0,10));
                $('#editHora').val(data.fechaHora.substring(11,19));
            } else {
                $('#editFecha').val('');
                $('#editHora').val('');
            }

            $('#editEvento').val(data.tipoMarca || '');
            $('#editTipoMarcaOriginal').val(data.tipoMarca || '');
            
            $('#editComentario').val(data.comentario || '');

            let modal = new bootstrap.Modal(document.getElementById('editModal'));
            modal.show();
        });

        $('#editForm').on('submit', function(e){
            e.preventDefault();
            let obj = {
                accion        : 'modificar',
                id            : $('#editId').val(),
                rutEmpleado   : $('#editRutEmpleado').val(),
                fechaHoraKey  : $('#editFechaHoraKey').val(),
                nombreEmpleado: $('#editNombreEmpleado').val(),
                hashcode      : $('#editHashcode').val(),
                fecha         : $('#editFecha').val(),
                hora          : $('#editHora').val(),
                evento        : $('#editEvento').val(),
                tipoMarcaOriginal: $('#editTipoMarcaOriginal').val(),
                comentario    : $('#editComentario').val()
            };
            $.ajax({
                url: '<%=request.getContextPath()%>/api/marcaciones',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(obj),
                dataType: 'json',
                success: function(response){
                    cargarRegistrosAjax();
                    if (response && response.exito) {
                        showToast(response.mensaje || 'Marca actualizada correctamente.', 'success');
                    } else {
                        showToast(response && response.mensaje ? response.mensaje : 'Error al actualizar marca.', 'error');
                    }
                },
                error: function(){
                    showToast('Error al actualizar marca.', 'error');
                }
            });
            bootstrap.Modal.getInstance(document.getElementById('editModal')).hide();
        });

        // Eliminar
        $('#marcasTable tbody').on('click', '.deleteRow', function(){
            let row = tabla.row($(this).closest('tr'));
            let data = row.data();
            if (!data) return;

            // Guardas el row en el botón para usarlo luego si quieres borrar directo sin recargar
            $('#confirmDeleteBtn').data('row', row);

            // Llenas ocultos con lo que espera tu backend
            $('#deleteId').val(data.correlativo);            // mismo que editId
            $('#deleteRutEmpleado').val(data.rutEmpleado);
            $('#deleteNombreEmpleado').val('pepe');          // igual que en tu edición
            $('#deleteFechaHoraKey').val(data.fechaHoraKey);
            $('#deleteHashcode').val(data.hashcode);
            $('#deleteEvento').val(data.tipoMarca || '');
            $('#deleteComentario').val(data.comentario || '');

            // Mostrar en solo lectura
            $('#deleteCorrelativo').val(data.correlativo);

            // Si tienes fechaHora en formato "yyyy-MM-dd HH:mm"
            let fecha = '';
            let hora  = '';
            if (data.fechaHora && data.fechaHora.length >= 16){
                fecha = data.fechaHora.substring(0,10);
                hora  = data.fechaHora.substring(11,16);
            }
            $('#deleteFecha').val(fecha);
            $('#deleteHora').val(hora);

            let modal = new bootstrap.Modal(document.getElementById('deleteModal'));
            modal.show();
        });


        $('#confirmDeleteBtn').on('click', function(){
            // Si quisieras borrar solo en cliente con el row guardado:
            // let row = $(this).data('row');

            let obj = {
                accion        : 'eliminar',
                id            : $('#deleteId').val(),
                rutEmpleado   : $('#deleteRutEmpleado').val(),
                fechaHoraKey  : $('#deleteFechaHoraKey').val(),
                nombreEmpleado: $('#deleteNombreEmpleado').val(),
                hashcode      : $('#deleteHashcode').val(),
                fecha         : $('#deleteFecha').val(),
                hora          : $('#deleteHora').val(),
                evento        : $('#deleteEvento').val(),
                comentario    : $('#deleteComentario').val()
            };

            $.ajax({
                url: '<%=request.getContextPath()%>/api/marcaciones',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(obj),
                dataType: 'json',
                success: function(response){
                    // Si la eliminación en backend fue OK, sacas la fila de la tabla
                    if (response && response.exito) {
                        // opción 1: recargar de backend (consistente)
                        cargarRegistrosAjax();

                        // opción 2: borrar solo la fila en cliente:
                        // let row = $('#confirmDeleteBtn').data('row');
                        // if (row) { row.remove().draw(); }

                        showToast(response.mensaje || 'Registro eliminado.', 'success');
                    } else {
                        showToast(response && response.mensaje ? response.mensaje : 'No se pudo eliminar el registro.', 'error');
                    }
                },
                error: function(){
                    showToast('Error al eliminar registro.', 'error');
                }
            });

            bootstrap.Modal.getInstance(document.getElementById('deleteModal')).hide();
        });


        // Export CSV / XLSX
        // Export CSV / XLSX alineado con DataTable
        function exportTable(type){
            // Tomar encabezados visibles desde el thead, excluyendo la última columna (Acciones)
            let headerCells = $('#marcasTable thead th').not(':last');
            let headers = headerCells.map(function(){ return $(this).text().trim(); }).get();

            // Tomar datos de filas filtradas en DataTables
            let datos = tabla.rows({ search: 'applied' }).data().toArray();

            // Mapear cada fila de acuerdo al orden de columnas definido en DataTables,
            // excluyendo también la última columna (acciones)
            let rows = datos.map(row => [
                row.rutEmpleado,
                row.nombreTipoMarca,
                row.fechaHoraStr,
                row.correlativo,
                row.codDispositivo,
                row.hashcode,
                row.labelTurno,
                row.masInfo,
                row.id,
                row.codTipoMarcaManual
            ]);

            if (type === "csv") {
                // Escapar comas y dobles comillas de forma simple
                let csv = headers.join(",") + "\n";
                rows.forEach(r => {
                    let line = r.map(val => {
                        if (val == null) return "";
                        let s = String(val);
                        if (s.includes('"')) s = s.replace(/"/g, '""');
                        if (s.includes(',') || s.includes('"') || s.includes('\n')) {
                            s = `"${s}"`;
                        }
                        return s;
                    }).join(",");
                    csv += line + "\n";
                });
                let blob = new Blob([csv], {type:"text/csv;charset=utf-8;"});
                saveAs(blob, "marcas.csv");
            }

            if (type === "xlsx") {
                let wb = XLSX.utils.book_new();
                let ws = XLSX.utils.aoa_to_sheet([headers, ...rows]);
                XLSX.utils.book_append_sheet(wb, ws, "Marcas");
                XLSX.writeFile(wb, "marcas.xlsx");
            }
        }
        $('#csvBtn').click(()=>exportTable('csv'));
        $('#xlsxBtn').click(()=>exportTable('xlsx'));
    });
</script>
</body>
</html>
