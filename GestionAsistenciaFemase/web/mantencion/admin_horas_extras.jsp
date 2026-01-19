<%@page import="cl.femase.gestionweb.common.Constantes"%>
<%@page import="cl.femase.gestionweb.vo.UsuarioVO"%>
<%@page import="java.time.LocalDate"%>
<%
    UsuarioVO theUser = (UsuarioVO)session.getAttribute("usuarioObj");
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
    <title>Autorizaci&oacute;n de Horas extras</title>
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
        
        /* Aplica a todos los inputs readonly del modal */
        #editModal input[readonly],
        #editModal select[readonly] {
            background-color: #f0f4f8;   /* gris azulado suave */
            color: #555;
            cursor: default;
        }

        /* Opcional: quitar borde fuerte y simular texto plano */
        #editModal input[readonly] {
            border-color: #d0d7de;
        }
        
    </style>
</head>
<body>
<div class="container-fluid my-4">
    <h1 class="mb-4">
        <span class="material-icons" style="font-size: 1.2em; vertical-align: middle;">assignment_turned_in</span>
        Autorizaci&oacute;n de Horas extras
    </h1>

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
                <div class="col-md-2 d-flex align-items-end">
                    <button id="buscarBtn" type="submit" class="btn btn-primary btn-sm w-100">
                        <span class="material-icons" style="font-size:1em;">search</span>
                    </button>
                </div>
            </div>
        </div>
    </form>

    <!-- Botones de exportación (sin nuevo registro ni mostrar detalles) -->
    <div class="d-flex justify-content-end mb-2">
        <button id="csvBtn" class="btn btn-success btn-sm btn-export">CSV</button>
        <button id="xlsxBtn" class="btn btn-success btn-sm btn-export">XLSX</button>
    </div>

    <div class="card">
        <div class="card-body">
            <div class="table-responsive">
                <table id="horasExtrasTable" class="table table-striped table-hover" style="width:100%">
                    <thead>
                        <tr>
                            <th>Acciones</th>
                            <th>RUT Empleado</th>
                            <th>Nombre Empleado</th>
                            <th>Centro Costo</th>
                            <th>Cargo</th>
                            <th>Fecha c&aacute;lculo</th>
                            <th>Fecha marca entrada</th>
                            <th>Hora entrada</th>
                            <th>Hora salida</th>
                            <th>Entrada te&oacute;rica</th>
                            <th>Salida te&oacute;rica</th>
                            <th>Duracion turno</th>
                            <th>Hrs presenciales</th>
                            <th>Observaci&oacute;n</th>
                            <th>HE autorizadas</th>
                            <th>HE Autorizadas? (SI/NO)</th>
                            
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

<!-- Modal edición horas extras -->
<div class="modal fade" id="editModal" tabindex="-1" aria-labelledby="editModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg">
    <form id="editForm" class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="editModalLabel">Editar Horas extras</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        <div class="row">

          <div class="mb-2 col-md-4">
            <label class="form-label">RUT Empleado</label>
            <input type="text" class="form-control" id="editRutEmpleado" name="rutEmpleado" readonly>
          </div>
          <div class="mb-2 col-md-4">
            <label class="form-label">Nombre Empleado</label>
            <input type="text" class="form-control" id="editNombreEmpleado" name="nombreEmpleado" readonly>
          </div>
          <div class="mb-2 col-md-4">
            <label class="form-label">Centro de costo</label>
            <input type="text" class="form-control" id="editCentroCosto" name="centroCosto" readonly>
          </div>

          <div class="mb-2 col-md-3">
            <label class="form-label">Fecha entrada</label>
            <input type="date" class="form-control" id="editFechaEntrada" name="fechaEntrada" readonly>
          </div>
          <div class="mb-2 col-md-3">
            <label class="form-label">Hora entrada</label>
            <input type="time" step="1" class="form-control" id="editHoraEntrada" name="horaEntrada" readonly>
          </div>
          <div class="mb-2 col-md-3">
            <label class="form-label">Fecha salida</label>
            <input type="date" class="form-control" id="editFechaSalida" name="fechaSalida" readonly>
          </div>
          <div class="mb-2 col-md-3">
            <label class="form-label">Hora salida</label>
            <input type="time" step="1" class="form-control" id="editHoraSalida" name="horaSalida" readonly>
          </div>

          <div class="mb-2 col-md-3">
            <label class="form-label">Entrada turno</label>
            <input type="time" step="1" class="form-control" id="editHoraEntradaTeorica" name="horaEntradaTeorica" readonly>
          </div>
          
            <div class="mb-2 col-md-3">
                <label class="form-label">Salida turno</label>
                <input type="time" step="1" class="form-control" id="editHoraSalidaTeorica" name="horaSalidaTeorica" readonly>
            </div>
            
            <div class="mb-2 col-md-3">
                <label class="form-label">Horas turno</label>
                <input type="time" step="1" class="form-control" id="editDuracionTeorica" name="duracionTeorica" readonly>
            </div>
         
          <div class="mb-2 col-md-3">
            <label class="form-label">Horas presenciales</label>
            <input type="text" class="form-control" id="editHorasPresenciales" name="horasPresenciales" readonly>
          </div>
          <!--  
            <div class="mb-2 col-md-3">
                <label class="form-label">Diferencia Horas</label>
                <input type="text" class="form-control" id="editDiferenciaHoras" name="diferenciaHoras" readonly>
            </div>
          -->
          <div class="mb-2 col-md-3">
            <label class="form-label">Horas extras (En base a las horas presenciales)</label>
            <input type="text" class="form-control" id="editHorasExtras" name="horasExtras" readonly>
          </div>

          <div class="mb-2 col-md-3">
            <label class="form-label">Horas extras autorizadas (hh:mm)</label>
            <!-- step="60" hace que el picker avance de minuto en minuto -->
            <input type="time" class="form-control" id="editHorasExtrasAutorizadas" name="horasExtrasAutorizadas" step="60">
          </div>
          <div class="mb-2 col-md-3">
            <label class="form-label">Hrs extras autorizadas (SI/NO)</label>
            <select class="form-select" id="editHorasExtrasAutorizadasSN" name="horasExtrasAutorizadasSN">
              <option value="S">SI</option>
              <option value="N">NO</option>
            </select>
          </div>

        </div>
      </div>
      <div class="modal-footer">
        <% if (!readOnly) { %>
          <button type="submit" id="btnGuardarCambios" class="btn btn-primary">Guardar cambios</button>
        <% } %>
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
      </div>
    </form>
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

        function hhmmToMinutes(hhmm) {
            if (!hhmm) return 0;
            let parts = hhmm.split(':');
            let h = parseInt(parts[0] || "0", 10);
            let m = parseInt(parts[1] || "0", 10);
            return h * 60 + m;
        }

        function minutesToHHMM(mins) {
            mins = Math.max(0, mins);
            let h = Math.floor(mins / 60);
            let m = mins % 60;
            return String(h).padStart(2, '0') + ':' + String(m).padStart(2, '0');
        }


        var tabla = null;

        $(document).ready(function(){

            $('#editHorasExtrasAutorizadas').on('change', function(){
                let val = $(this).val() || "00:00";
                if (hhmmToMinutes(val) > 0) {
                    $('#editHorasExtrasAutorizadasSN').val("S");
                } else {
                    $('#editHorasExtrasAutorizadasSN').val("N");
                }
            });

            $('#editHorasExtrasAutorizadasSN').on('change', function(){
                let v = $(this).val();
                if (v === "N") {
                    // Si es NO, horas en 00:00 y no requerido
                    $('#editHorasExtrasAutorizadas').val("00:00")
                                                   .removeAttr('required');
                } else if (v === "S") {
                    // Si es SI, marcar como requerido
                    $('#editHorasExtrasAutorizadas').attr('required', 'required');
                }
            });    

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

            // DataTable para horas extras: columnas alineadas con el JSON y el <thead>
            tabla = $('#horasExtrasTable').DataTable({
                scrollX: true,
                columns: [
                    {
                        data: null,
                        orderable: false,
                        render: function(data, type, row, meta) {
                            return `<button class="btn btn-sm btn-primary actions-btn editRow" title="Editar registro">
                                        <span class="material-icons">edit</span>
                                    </button>`;
                        }
                    },
                    { data: "rutEmpleado" },             // RUT Empleado
                    { data: "nombreEmpleado" },         // Nombre Empleado
                    { data: "cencoNombre" },            // Centro Costo
                    { data: "cargoNombre" },            // Cargo
                    { data: "fechaHoraCalculo" },       // Fecha cálculo
                    { data: "fechaEntradaMarca" },      // Fecha marca entrada
                    { data: "horaEntrada" },            // Hora entrada
                    { data: "horaSalida" },             // Hora salida
                    { data: "horaEntradaTeorica" },     // Hora entrada teórica
                    { data: "horaSalidaTeorica" },      // Hora salida teórica
                    { data: "duracionTeorica" },      // Duracion turno en hh:mm
                    { data: "hrsPresenciales" },        // Horas presenciales
                    { data: "observacion" },            // Observación
                    { data: "horasMinsExtrasAutorizadas" }, // Horas extras autorizadas (hh:mm)
                    { data: "autorizaHrsExtras" }      // Hrs extras autorizadas (SI/NO)
                    
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
                    fechaFin: $('#fechaFin').val()
                };
                $.ajax({
                    url: '<%=request.getContextPath()%>/api/autoriza_horas_extras',
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify(obj),
                    dataType: 'json',
                    success: function(data) {
                        tabla.clear().rows.add(data).draw();
                    },
                    error: function(xhr, status, err){
                        console.error("Error listar:", status, err);
                        console.error("Response:", xhr.responseText);
                        showToast('Error al invocar backend (listar): ' + status, 'error');
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
                cargarRegistrosAjax();
            });

            let registroActual = null;

            // Editar fila: rellenar modal usando los nombres reales del JSON
            $('#horasExtrasTable tbody').on('click', '.editRow', function() {
                let row = tabla.row($(this).closest('tr'));
                let data = row.data();
                registroActual = data;

                $('#editRutEmpleado').val(data.rutEmpleado);
                $('#editNombreEmpleado').val(data.nombreEmpleado);
                $('#editCentroCosto').val(data.cencoNombre);

                // En tu JSON no hay fechaEntrada/fechaSalida separadas,
                // sólo fechaEntradaMarca y horaEntrada/horaSalida.
                $('#editFechaEntrada').val(data.fechaEntradaMarca);
                $('#editHoraEntrada').val(data.horaEntrada);
                $('#editFechaSalida').val(data.fechaEntradaMarca); // misma fecha
                $('#editHoraSalida').val(data.horaSalida);

                $('#editHoraEntradaTeorica').val(data.horaEntradaTeorica);
                $('#editHoraSalidaTeorica').val(data.horaSalidaTeorica);
                $('#editHorasPresenciales').val(data.hrsPresenciales);
                $('#editHorasExtras').val(data.diferenciaHoras);
                
                $('#editDuracionTeorica').val(data.duracionTeorica);
                ////$('#editDiferenciaHoras').val(data.diferenciaHoras);
                
                // NUEVO: si horas presenciales = 0 -> toast + deshabilitar guardar
                let hrsPres = data.hrsPresenciales || "00:00";
                let presMins = hhmmToMinutes(hrsPres);
                if (presMins === 0) {
                    showToast('Se requiere ejecutar un cálculo de asistencia', 'error');
                    $('#btnGuardarCambios').prop('disabled', true);
                } else {
                    $('#btnGuardarCambios').prop('disabled', false);
                }

                // Máximo permitido para horas extras autorizadas = horas extras (sobretiempo)
                // (ya NO usamos hrsPresenciales como límite)
                let maxMins = hhmmToMinutes(data.diferenciaHoras || "00:00"); // ejemplo "03:10"
                let maxHHMM = minutesToHHMM(maxMins);                        // "HH:MM"
                $('#editHorasExtrasAutorizadas').attr('max', maxHHMM);

                // Setear horas autorizadas y combo SI/NO según valor
                let autorizadas = data.horasMinsExtrasAutorizadas || "00:00";
                if (autorizadas.length === 4) { // por si viene "0:30" -> "00:30"
                    autorizadas = "0" + autorizadas;
                }
                $('#editHorasExtrasAutorizadas').val(autorizadas);

                // Regla: si autorizadas > 0 -> combo S y required
                if (hhmmToMinutes(autorizadas) > 0) {
                    $('#editHorasExtrasAutorizadasSN').val("S");
                    $('#editHorasExtrasAutorizadas').attr('required','required');
                } else {
                    $('#editHorasExtrasAutorizadasSN').val(data.autorizaHrsExtras || "N");
                    if ($('#editHorasExtrasAutorizadasSN').val() === "S") {
                        $('#editHorasExtrasAutorizadas').attr('required','required');
                    } else {
                        $('#editHorasExtrasAutorizadas').removeAttr('required');
                    }
                }

                let modal = new bootstrap.Modal(document.getElementById('editModal'));
                modal.show();
            });


            $('#editForm').on('submit', function(e){
                e.preventDefault();
            
                let sn  = $('#editHorasExtrasAutorizadasSN').val();
                let hrsAut = $('#editHorasExtrasAutorizadas').val() || "00:00";
                let hrsSobretiempo = $('#editHorasExtras').val() || "00:00"; // Horas extras (sobretiempo)

                //alert('hrsSobretiempo: ' + hrsSobretiempo);

                // Si es SI, obligar a que no sea 00:00
                if (sn === "S" && hhmmToMinutes(hrsAut) === 0) {
                    showToast('Debe ingresar un valor mayor a 00:00 en "Horas extras autorizadas (hh:mm)" cuando selecciona SI.', 'error');
                    return;
                }

                // Regla nueva: autorizadas <= sobretiempo
                let maxMins = hhmmToMinutes(hrsSobretiempo);
                let selMins = hhmmToMinutes(hrsAut);

                if (selMins > maxMins) {
                    showToast('Las horas extras autorizadas no pueden superar las horas extras (sobretiempo) [' + hrsSobretiempo + '].', 'error');
                    return;
                }
            
                let obj = {
                    accion: 'modificar',
                    rutEmpleado: $('#editRutEmpleado').val(),
                    nombreEmpleado: $('#editNombreEmpleado').val(),
                    centroCosto: $('#editCentroCosto').val(),
                    fechaEntrada: $('#editFechaEntrada').val(),
                    horaEntrada: $('#editHoraEntrada').val(),
                    fechaSalida: $('#editFechaSalida').val(),
                    horaSalida: $('#editHoraSalida').val(),
                    horaEntradaTeorica: $('#editHoraEntradaTeorica').val(),
                    horaSalidaTeorica: $('#editHoraSalidaTeorica').val(),
                    horasTeoricas: $('#editHorasTeoricas').val(),
                    horasTrabajadas: $('#editHorasTrabajadas').val(),
                    horasPresenciales: $('#editHorasPresenciales').val(),
                    horasExtras: $('#editHorasExtras').val(),
                    horasExtrasAutorizadas: $('#editHorasExtrasAutorizadas').val(),
                    horasExtrasAutorizadasSN: $('#editHorasExtrasAutorizadasSN').val()
                };

                $.ajax({
                    url: '<%=request.getContextPath()%>/api/autoriza_horas_extras',
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify(obj),
                    dataType: 'json',
                    success: function(response){
                        cargarRegistrosAjax();
                        if (response.exito) {
                            showToast(response.mensaje, 'success');
                        } else {
                            showToast(response.mensaje, 'error');
                        }
                    },
                    error: function(xhr, status, err){
                        console.error("Error modificar:", status, err);
                        console.error("Response:", xhr.responseText);
                        showToast('Error al invocar backend (modificar): ' + status, 'error');
                    }
                });
                bootstrap.Modal.getInstance(document.getElementById('editModal')).hide();
            });

            function exportTable(type){
                let headers = [
                    "RUT Empleado","Nombre Empleado","Centro Costo","Cargo",
                    "Fecha cálculo","Fecha marca entrada","Hora entrada","Hora salida",
                    "Hora entrada teórica","Hora salida teórica",
                    "Horas presenciales",
                    "Observación","Horas extras","Hrs extras autorizadas (SI/NO)",
                    "Horas extras autorizadas"
                ];
                let datos = tabla.rows({ search: 'applied' }).data().toArray();
                let rows = datos.map(row => [
                    row.rutEmpleado,
                    row.nombreEmpleado,
                    row.cencoNombre,
                    row.cargoNombre,
                    row.fechaHoraCalculo,
                    row.fechaEntradaMarca,
                    row.horaEntrada,
                    row.horaSalida,
                    row.horaEntradaTeorica,
                    row.horaSalidaTeorica,
                    row.hrsPresenciales,
                    row.observacion || "",
                    row.horaMinsExtras,
                    row.autorizaHrsExtras,
                    row.horasMinsExtrasAutorizadas
                ]);

                if(type==="csv"){
                    let csv = headers.join(",")+"\n";
                    rows.forEach(r => {csv += r.join(",")+"\n";});
                    let blob = new Blob([csv], {type:"text/csv"});
                    saveAs(blob, "horas_extras.csv");
                }
                if(type==="xlsx"){
                    let wb = XLSX.utils.book_new();
                    let ws = XLSX.utils.aoa_to_sheet([headers,...rows]);
                    XLSX.utils.book_append_sheet(wb, ws, "HorasExtras");
                    XLSX.writeFile(wb, "horas_extras.xlsx");
                }
            }

            $('#csvBtn').click(()=>exportTable('csv'));
            $('#xlsxBtn').click(()=>exportTable('xlsx'));

        });
    </script>

</body>
</html>
