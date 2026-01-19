<%@page import="cl.femase.gestionweb.vo.AfpVO"%>
<%@page import="java.util.HashMap"%>
<%@page import="cl.femase.gestionweb.common.Constantes"%>
<%@page import="cl.femase.gestionweb.vo.UsuarioVO"%>
<%
    UsuarioVO theUser = (UsuarioVO)session.getAttribute("usuarioObj");
    boolean readOnly = false;
    if (theUser.getIdPerfil() == Constantes.ID_PERFIL_FISCALIZADOR || theUser.getIdPerfil() == Constantes.ID_PERFIL_EMPLEADO){
        readOnly = true;
    } 
    HashMap<String, AfpVO> listaAfps = (HashMap<String, AfpVO>)session.getAttribute("afps");
    
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Calcular vacaciones</title>

    <!-- CSS: Bootstrap + iconos + Select2 + DataTables + Buttons + FixedColumns -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.13/css/select2.min.css">
    <link rel="stylesheet" href="https://cdn.datatables.net/1.13.8/css/dataTables.bootstrap5.min.css">
    <link rel="stylesheet" href="https://cdn.datatables.net/buttons/2.4.2/css/buttons.bootstrap5.min.css">
    <link rel="stylesheet" href="https://cdn.datatables.net/fixedcolumns/4.3.0/css/fixedColumns.bootstrap5.min.css">

    <style>
        body { background: #f4f8fc; }
        .card { box-shadow: 0 4px 12px rgba(0,0,0,0.08); border: none; }
        h1 { color: #3163ad; font-size: 1.5rem; margin-bottom: 1rem; }
        .btn-primary { background: #3163ad; border: none; }
        .btn-success { background: #2ecc71; border: none; }
        .form-label { font-size: 0.92em; margin-bottom: 0.2em; }
        .form-select, .form-control {
            padding-top: 0.25rem;
            padding-bottom: 0.22rem;
            font-size: 0.95em;
            height: 32px !important;
            min-height: 32px !important;
            margin-bottom: 0.3em;
        }
        .select2-container .select2-selection--single {
            height: 32px;
            font-size: 0.9em;
        }
        .select2-container--default .select2-selection--single .select2-selection__rendered {
            line-height: 30px;
        }
        .select2-container--default .select2-selection--single .select2-selection__arrow {
            height: 30px;
        }
        .card-body {
            padding-top: 0.5rem;
            padding-bottom: 0.5rem;
        }
        .row-filtros { row-gap: 0.15rem; }

        #empleados {
            height: 110px !important;
            min-height: 110px !important;
            font-size: 0.75rem;
            line-height: 1.1;
        }
        #empleados option { font-size: 0.75rem; }

        /* Tabla resultados */
        .table-sm th, .table-sm td {
            padding: .2rem .4rem;
            font-size: 0.78rem;
            vertical-align: middle;
        }
        .dataTables_wrapper .dataTables_filter input {
            height: 28px;
            font-size: 0.8rem;
        }
        .dataTables_wrapper .dataTables_paginate .page-link {
            padding: .15rem .45rem;
            font-size: 0.78rem;
        }

        /* Toast */
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
        <span class="material-icons" style="font-size: 1.2em; vertical-align: middle;">event_available</span>
        Calcular vacaciones
    </h1>

    <!-- Filtros: centro de costo + empleados -->
    <form id="formCalculo" method="post">
        <div class="card mb-3">
            <div class="card-body">
                <div class="row row-filtros align-items-start mb-2">
                    <div class="col-md-3">
                        <label for="centroCosto" class="form-label">Centro de costo</label>
                        <select id="centroCosto" name="centroCosto" class="form-select"></select>
                    </div>

                    <div class="col-md-9">
                        <label for="empleados" class="form-label">Empleado(s)</label>
                        <select id="empleados" name="empleados" class="form-select" multiple>
                            <!-- se llena cuando se elige centro de costo -->
                        </select>
                        <div class="d-flex justify-content-between">
                            <small class="text-muted" style="font-size:0.8em;">
                                Use Ctrl+click o Shift+click para seleccionar varios.
                            </small>
                            <small id="resumenEmpleados" class="text-muted" style="font-size:0.8em;">
                                0 seleccionados de 0
                            </small>
                        </div>
                    </div>
                </div>

                <div class="row mt-2">
                    <div class="col-md-6 d-flex align-items-end gap-2">
                        <button id="btnVerDatos" type="button" class="btn btn-secondary btn-sm">
                            <span class="material-icons" style="font-size:1em;">visibility</span> Ver datos
                        </button>
                        <button id="btnCalcular" type="button" class="btn btn-primary btn-sm">
                            <span id="spinnerCalcular" class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true" style="display:none;"></span>
                            <span id="btnCalcularText">
                                <span class="material-icons" style="font-size:1em;">calculate</span>
                                Calcular vacaciones
                            </span>
                        </button>
                    </div>
                </div>

            </div>
        </div>
    </form>

    <!-- Tabla de resultados -->
    <div class="card">
        <div class="card-body">
            <h6 class="mb-2">Resultados</h6>
            <div class="table-responsive">
                <table id="tablaVacaciones" class="table table-sm table-striped table-hover" style="width:100%;">
                    <thead>
                    <tr>
                        <th>Run trabajador</th>
                        <th>Nombre</th>
                        <th>Fecha &uacute;ltimo c&aacute;lculo</th>
                        <th>Fecha inicio contrato</th>
                        <th>AFP Certificado</th>
                        <th>Fecha emisi&oacute;n certificado</th>
                        <th>Num cotizaciones certificado</th>
                        <th>Mensaje vacaciones progresivas</th>
                        <th>D&iacute;­as progresivos</th>
                        <th>D&iacute;as adicionales</th>
                        <th>D&iacute;as especiales</th>
                        <th>Acumulados (+)</th>
                        <th>Asignados (-)</th>
                        <th>Saldo</th>
                        <th>Comentario</th>
                        <th>Fec Inicio &uacute;ltima vacaci&oacute;n</th>
                        <th>Fec fin &uacute;ltima vacaci&oacute;n</th>
                        <th>Saldo VBA</th>
                        <th>Saldo VP</th>
                        <th>Acciones</th>
                    </tr>
                    </thead>
                    <tbody></tbody>
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

<!-- Modal edición registro -->
<div class="modal fade" id="modalEditar" tabindex="-1" aria-labelledby="modalEditarLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg modal-dialog-centered">
    <div class="modal-content">
      <form id="formEditar">
        <div class="modal-header">
          <h5 class="modal-title" id="modalEditarLabel">Editar datos de vacaciones</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
        </div>
        <div class="modal-body">
          <input type="hidden" id="editRun" name="runEmpleado">
          <input type="hidden" id="editEmpresa" name="empresaId">

          <div class="row g-2">
            <div class="col-md-4">
              <label class="form-label">AFP Certificado</label>
                <select id="editAfpCert" name="afpCertificado">
                    <option value="-1">-- Seleccione AFP --</option>
                    <%
                        for (AfpVO afp : listaAfps.values()) {
                        %>
                            <option value="<%=afp.getCode()%>">
                                <%=afp.getNombre()%>
                            </option>
                        <%
                        }
                    %>
                </select>
            </div>
            <div class="col-md-4">
              <label class="form-label">Fecha emisi&oacute;n certificado</label>
              <input type="date" id="editFecCert" name="fecEmisionCert" class="form-control form-control-sm">
            </div>
            <div class="col-md-4">
              <label class="form-label">Num cotizaciones certificado</label>
              <input type="number" id="editNumCotiz" name="numCotizCert" class="form-control form-control-sm" min="0" step="1">
            </div>
          </div>

          <div class="row g-2 mt-2">
            <div class="col-md-4">
              <label class="form-label">Dias especiales</label>
              <select id="editDiasEspeciales" name="diasEspeciales" class="form-select form-select-sm">
                <option value="N">No</option>
                <option value="S">Si­</option>
              </select>
            </div>
            <div class="col-md-4">
              <label class="form-label">Dias adicionales</label>
              <input type="number" id="editDiasAdicionales" name="diasAdicionales" class="form-control form-control-sm" min="0" step="1">
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">Cancelar</button>
          <button type="submit" class="btn btn-primary btn-sm">Guardar cambios</button>
        </div>
      </form>
    </div>
  </div>
</div>

        <!-- JS: Bootstrap, jQuery, Select2, DataTables + Buttons + FixedColumns + Export -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.13/js/select2.min.js"></script>

    <!-- DataTables núcleo + Bootstrap -->
    <script src="https://cdn.datatables.net/1.13.8/js/jquery.dataTables.min.js"></script>
    <script src="https://cdn.datatables.net/1.13.8/js/dataTables.bootstrap5.min.js"></script>

    <!-- Buttons base + Bootstrap + ColVis -->
    <script src="https://cdn.datatables.net/buttons/2.4.2/js/dataTables.buttons.min.js"></script>
    <script src="https://cdn.datatables.net/buttons/2.4.2/js/buttons.bootstrap5.min.js"></script>
    <script src="https://cdn.datatables.net/buttons/2.4.2/js/buttons.colVis.min.js"></script>

    <!-- FixedColumns -->
    <script src="https://cdn.datatables.net/fixedcolumns/4.3.0/js/dataTables.fixedColumns.min.js"></script>

    <!-- Exportación HTML5 (DEBE ir después de buttons y jszip) -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.10.1/jszip.min.js"></script>
    <script src="https://cdn.datatables.net/buttons/2.4.2/js/buttons.html5.min.js"></script>
<script>
    let tabla;

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

    function actualizarResumenEmpleados() {
        let total = $('#empleados option').length;
        let sel   = ($('#empleados').val() || []).length;
        $('#resumenEmpleados').text(sel + ' seleccionados de ' + total);
    }

    $(document).ready(function(){

        // DataTable
        tabla = $('#tablaVacaciones').DataTable({
            scrollX: true,
            paging: true,
            pageLength: 15,
            lengthChange: false,
            searching: true,
            ordering: true,
            info: true,
            autoWidth: false,
            dom: 'Bfrtip',
            buttons: [
                {
                    extend: 'colvis',
                    text: 'Columnas disponibles',
                    className: 'btn btn-sm btn-outline-secondary',
                    columns: '.col-opcional'
                },
                {
                    extend: 'excelHtml5',
                    text: 'Exportar XLSX',
                    className: 'btn btn-sm btn-outline-success',
                    exportOptions: {
                        columns: ':visible:not(:last-child)' // todas las visibles menos Acciones
                    }
                },
                {
                    extend: 'csvHtml5',
                    text: 'Exportar CSV',
                    className: 'btn btn-sm btn-outline-primary',
                    fieldSeparator: ';',
                    exportOptions: {
                        columns: ':visible:not(:last-child)'
                    }
                }
            ],

            // Columnas opcionales ocultas por defecto
            columnDefs: [
                {
                    targets: [5, 6, 7, 9, 10, 14, 15, 16],
                    visible: false,
                    className: 'col-opcional'
                }
            ],
            columns: [
                { data: 'rutEmpleado' },                     // 0 Run trabajador (FIJA)
                { data: 'nombreEmpleado' },                  // 1 Nombre (FIJA)
                { data: 'fechaCalculo' },                    // 2 Fecha último cálculo (FIJA)
                { data: 'fechaInicioContrato' },             // 3 Fecha inicio contrato (FIJA)
                { data: 'afpName' },                         // 4 AFP Certificado (FIJA)
                { data: 'fechaCertifVacacionesProgresivas' },// 5 Fecha emisión certificado (OPCIONAL)
                { data: 'numCotizaciones' },                 // 6 Num cotizaciones certificado (OPCIONAL)
                { data: 'mensajeVp' },                       // 7 Mensaje vacaciones progresivas (OPCIONAL)
                { data: 'diasProgresivos' },                 // 8 Días progresivos (FIJA)
                { data: 'diasAdicionales' },                 // 9 Días adicionales (OPCIONAL)
                { data: 'diasEspeciales' },                  // 10 Días especiales (OPCIONAL)
                { data: 'diasAcumulados' },                  // 11 Acumulados (+) (FIJA)
                { data: 'diasEfectivos' },                   // 12 Asignados (-) (FIJA)
                { data: 'saldoDias' },                       // 13 Saldo (FIJA)
                { data: 'comentario' },                      // 14 Comentario (OPCIONAL)
                { data: 'fechaInicioUltimasVacaciones' },    // 15 Fec Inicio última vacación (OPCIONAL)
                { data: 'fechaFinUltimasVacaciones' },       // 16 Fec fin última vacación (OPCIONAL)
                { data: 'saldoDiasVBA' },                    // 17 Saldo VBA (FIJA)
                { data: 'saldoDiasVP' },                     // 18 Saldo VP (FIJA)
                {
                    data: null,
                    orderable: false,
                    searchable: false,
                    defaultContent:
                        '<button type="button" class="btn btn-outline-secondary btn-xs btn-sm btn-editar">' +
                        '<span class="material-icons" style="font-size:1em;">edit</span>' +
                        '</button>'
                }                                            // 19 Acciones (FIJA)
            ],
            fixedColumns: {
                leftColumns: 5,   // Run, Nombre, Fecha ult calc, Fecha inicio contrato, AFP
                rightColumns: 1   // Acciones
            },

            language: {
                url: 'https://cdn.datatables.net/plug-ins/1.13.8/i18n/es-ES.json'
            }
        });

        // Sobrescribir clase por defecto (opcional, si sigue viéndose mal)
        $.fn.dataTable.Buttons.defaults.dom.button.className = 'btn btn-sm btn-outline-secondary';

        // Centro de costo (Select2 + backend)
        $('#centroCosto').select2({
            theme: 'bootstrap-5',
            minimumInputLength: 1,
            placeholder: 'Buscar centro de costo...',
            ajax: {
                url: '<%=request.getContextPath()%>/api/centros_costo',
                dataType: 'json',
                delay: 200,
                data: function (params) {
                    return { term: params.term };
                },
                processResults: function (data) {
                    return {
                        results: data.map(function(cc){
                            return {
                                id: cc.id,
                                text: '[' + cc.deptoNombre + '] ' + cc.nombre
                            };
                        })
                    };
                }
            }
        });

        // Cambio centro de costo -> cargar empleados
        $('#centroCosto').on('change', function(){
            let cencoId = $(this).val();
            let $empleados = $('#empleados');
            $empleados.empty();

            if(!cencoId){
                actualizarResumenEmpleados();
                return;
            }

            $.ajax({
                url: '<%=request.getContextPath()%>/api/empleados',
                type: 'GET',
                dataType: 'json',
                data: { centroCosto: cencoId },
                success: function(data){
                    data.forEach(function(emp){
                        $empleados.append(
                            '<option value="' + emp.rut + '">'
                                + emp.nombreYpaterno + ' (' + emp.rut + ') - ' + emp.nombreTurno +
                            '</option>'
                        );
                    });
                    actualizarResumenEmpleados();
                },
                error: function(xhr, status, err){
                    console.error("Error cargando empleados:", status, err);
                    showToast('Error al cargar empleados del centro de costo.', 'error');
                    actualizarResumenEmpleados();
                }
            });
        });

        $('#empleados').on('change', actualizarResumenEmpleados);
        actualizarResumenEmpleados();

        // Botón Ver datos
        $('#btnVerDatos').on('click', function(){
            let cenco = $('#centroCosto').val();
            let empleadosSel = $('#empleados').val() || [];

            if (!cenco) {
                showToast('Debe seleccionar un centro de costo.', 'error');
                return;
            }
            if (empleadosSel.length === 0) {
                showToast('Debe seleccionar un empleado.', 'error');
                return;
            }

            $.ajax({
                url: '<%=request.getContextPath()%>/servlet/VerInfoVacaciones',
                type: 'POST',
                traditional: true,
                dataType: 'json',
                data: {
                    empresaId: '<%=theUser.getEmpresaId()%>',
                    centroCosto: cenco,
                    empleados: empleadosSel   // aquí será un solo run o varios
                },
                success: function(data){
                    tabla.clear().rows.add(data || []).draw();
                },
                error: function(xhr, status, err){
                    console.error("Error VerInfoVacaciones:", status, err);
                    showToast('Error al obtener datos de vacaciones.', 'error');
                }
            });
        });

        // Botón Calcular vacaciones
        $('#btnCalcular').on('click', function(){
            let cenco = $('#centroCosto').val();
            let empleadosSel = $('#empleados').val() || [];

            if (!cenco) { showToast('Debe seleccionar un centro de costo.', 'error'); return; }
            if (empleadosSel.length === 0) { showToast('Debe seleccionar al menos un empleado.', 'error'); return; }

            $('#btnCalcular').prop('disabled', true);
            $('#spinnerCalcular').show();
            $('#btnCalcularText').text('Calculando...');

            $.ajax({
                url: '<%=request.getContextPath()%>/servlet/CalculoVacacionesNew',
                type: 'POST',
                traditional: true,
                dataType: 'json',
                data: {
                    empresaId: '<%=theUser.getEmpresaId()%>',
                    centroCosto: cenco,
                    empleados: empleadosSel
                },
                success: function(data){
                    tabla.clear().rows.add(data || []).draw();
                    showToast('Cálculo de vacaciones ejecutado correctamente.', 'success');
                },
                error: function(xhr, status, err){
                    console.error("Error CalculoVacacionesNew:", status, err);
                    showToast('Error al calcular vacaciones.', 'error');
                },
                complete: function(){
                    $('#spinnerCalcular').hide();
                    $('#btnCalcularText').html('<span class="material-icons" style="font-size:1em;">calculate</span> Calcular vacaciones');
                    $('#btnCalcular').prop('disabled', false);
                }
            });
        });

        // Abrir modal edición
        $('#tablaVacaciones tbody').on('click', '.btn-editar', function(){
            const data = tabla.row($(this).closest('tr')).data();
            if (!data) return;

            $('#editRun').val(data.rutEmpleado || '');
            $('#editEmpresa').val('<%=theUser.getEmpresaId()%>' || '');
            $('#editAfpCert').val(data.afpCode || '');
            $('#editFecCert').val(data.fechaCertifVacacionesProgresivas || '');
            $('#editNumCotiz').val(data.numCotizaciones || '');
            $('#editDiasEspeciales').val((data.diasEspeciales || 'N') === 'S' ? 'S' : 'N');
            $('#editDiasAdicionales').val(data.diasAdicionales || 0);

            const modal = new bootstrap.Modal(document.getElementById('modalEditar'));
            modal.show();
        });

        // Guardar cambios edición
        $('#formEditar').on('submit', function(e){
            e.preventDefault();

            const payload = {
                empresaId: $('#editEmpresa').val(),
                runEmpleado: $('#editRun').val(),
                afpCertificado: $('#editAfpCert').val(),
                fecEmisionCert: $('#editFecCert').val(),
                numCotizCert: $('#editNumCotiz').val(),
                diasEspeciales: $('#editDiasEspeciales').val(),
                diasAdicionales: $('#editDiasAdicionales').val()
            };

            $.ajax({
                url: '<%=request.getContextPath()%>/servlet/GuardarInfoVacaciones',
                type: 'POST',
                dataType: 'json',
                data: $.extend({accion:'guardarDatosVacaciones'}, payload),
                success: function(resp){
                    showToast('Datos de vacaciones actualizados correctamente.', 'success');
                    $('#modalEditar').modal('hide');

                    if (!resp || !resp.rutEmpleado) {
                        return;
                    }

                    const row = tabla.row(function(idx, data, node){
                        return data.rutEmpleado === resp.rutEmpleado;
                    });

                    if (row.any()) {
                        const dataRow = row.data();

                        dataRow.afpCode                          = resp.afpCode;
                        dataRow.afpName                          = resp.afpName;
                        dataRow.fechaCertifVacacionesProgresivas = resp.fechaCertifVacacionesProgresivas;
                        dataRow.numCotizaciones                  = resp.numCotizaciones;
                        dataRow.diasEspeciales                   = resp.diasEspeciales;
                        dataRow.diasAdicionales                  = resp.diasAdicionales;
                        dataRow.diasProgresivos                  = resp.diasProgresivos;
                        dataRow.diasAcumulados                   = resp.diasAcumulados;
                        dataRow.diasEfectivos                    = resp.diasEfectivos;
                        dataRow.saldoDias                        = resp.saldoDias;
                        dataRow.saldoDiasVBA                     = resp.saldoDiasVBA;
                        dataRow.saldoDiasVP                      = resp.saldoDiasVP;
                        dataRow.mensajeVp                        = resp.mensajeVp;

                        row.data(dataRow).draw(false);
                    }
                },
                error: function(xhr, status, err){
                    console.error("Error actualizando datos vacaciones:", status, err);
                    showToast('Error al actualizar datos de vacaciones.', 'error');
                }
            });
        });

    });
</script>

</body>
</html>
