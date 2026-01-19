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
    <title>Reporte de asistencia Semanal</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.13/css/select2.min.css">
    <style>
        body { background: #f4f8fc; }
        .card { box-shadow: 0 4px 12px rgba(0,0,0,0.08); border: none; }
        h1 { color: #3163ad; font-size: 1.5rem; margin-bottom: 1rem; }
        .btn-primary { background: #3163ad; border: none; }
        .btn-success { background: #2ecc71; border: none; }
        .btn-export { margin-left: 8px; }
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
        .row-filtros {
            row-gap: 0.15rem;
        }

        /* Listbox empleados (puede ser algo alto, ahora tiene más ancho) */
        #empleados {
            height: 110px !important;      /* aprox. la mitad de 220px */
            min-height: 110px !important;
            font-size: 0.75rem;            /* letra más pequeña */
            line-height: 1.1;              /* filas más compactas */
        }
        
        #empleados option {
            font-size: 0.75rem;
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
    </style>
</head>
<body>
<div class="container-fluid my-4">
    <h1 class="mb-4">
        <span class="material-icons" style="font-size: 1.2em; vertical-align: middle;">assignment_turned_in</span>
        Reporte de asistencia Semanal
    </h1>

    <form id="formReporte" method="post">
        <div class="card mb-3">
            <div class="card-body">

                <!-- Fila 1: Centro de costo + Empleados (más ancho) -->
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

                <!-- Fila 2: Fechas + Formato -->
                <div class="row row-filtros align-items-start">
                    <div class="col-md-3">
                        <label for="fechaInicio" class="form-label">Fecha inicio</label>
                        <input type="date" id="fechaInicio" name="fechaInicio" class="form-control" value="<%=primerDiaStr%>">
                    </div>
                    <div class="col-md-3">
                        <label for="fechaFin" class="form-label">Fecha fin</label>
                        <input type="date" id="fechaFin" name="fechaFin" class="form-control" value="<%=ultimoDiaStr%>">
                    </div>
                    <div class="col-md-2">
                        <label for="formato" class="form-label">Formato</label>
                        <select id="formato" name="formato" class="form-select">
                            <option value="pdf">PDF</option>
                            <option value="xlsx">XLSX</option>
                        </select>
                    </div>
                    <div class="col-md-4 d-flex align-items-end justify-content-end">
                        <button id="generarBtn" type="submit" class="btn btn-primary btn-sm">
                            <span id="spinner" class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true" style="display:none;"></span>
                            <span id="btnText">
                                <span class="material-icons" style="font-size:1em;">download</span>
                                Generar
                            </span>
                        </button>
                    </div>
                </div>

            </div>
        </div>
    </form>
</div>

<div id="toastError" class="toast toast-visible align-items-center position-fixed top-0 end-0 m-3" role="alert" aria-live="assertive" aria-atomic="true">
  <div class="d-flex">
    <div class="toast-body" id="toastMsg"></div>
    <button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast"></button>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
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

    function actualizarResumenEmpleados() {
        let total = $('#empleados option').length;
        let sel   = ($('#empleados').val() || []).length;
        $('#resumenEmpleados').text(sel + ' seleccionados de ' + total);
    }

    $(document).ready(function(){

        // 1) Búsqueda de centros de costo con input text (Select2 + backend)
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

        // 2) Cuando se selecciona un centro de costo, cargar empleados de ese centro
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

        // 3) Actualizar contador al cambiar selección
        $('#empleados').on('change', actualizarResumenEmpleados);

        $('#fechaInicio').val('<%=primerDiaStr%>');
        $('#fechaFin').val('<%=ultimoDiaStr%>');
        actualizarResumenEmpleados(); // inicial

        // 4) Envío del formulario
        $('#formReporte').on('submit', function(e){
            e.preventDefault();

            let cenco = $('#centroCosto').val();
            let empleadosSel = $('#empleados').val() || [];
            let f1 = $('#fechaInicio').val();
            let f2 = $('#fechaFin').val();
            let formato = $('#formato').val();

            if (!cenco) { showToast('Debe seleccionar un centro de costo.', 'error'); return; }
            if (empleadosSel.length === 0) { showToast('Debe seleccionar al menos un empleado.', 'error'); return; }
            if (!f1) { showToast('Debe ingresar la fecha de inicio.', 'error'); return; }
            if (!f2) { showToast('Debe ingresar la fecha de fin.', 'error'); return; }
            if (f1 > f2) {
                showToast('El rango de fechas no es válido: Fecha inicio debe ser menor o igual a fecha fin.', 'error');
                return;
            }

            let obj = {
                accion: 'reporte_asistencia_semanal',
                centroCosto: cenco,
                empleados: empleadosSel,
                fechaInicio: f1,
                fechaFin: f2,
                formato: formato
            };

            // Mostrar spinner y deshabilitar botón
            $('#generarBtn').prop('disabled', true);
            $('#spinner').show();
            $('#btnText').text('Generando...');

            $.ajax({
                url: '<%=request.getContextPath()%>/api/reporte_asistencia_semanal',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(obj),
                xhrFields: { responseType: 'blob' },
                success: function(blob){
                    let extension = (empleadosSel.length > 1) ? 'zip' : formato;
                    let nombreArchivo = 'reporte_asistencia_semanal_' + f1 + '_a_' + f2 + '.' + extension;

                    let link = document.createElement('a');
                    let url = window.URL.createObjectURL(blob);
                    link.href = url;
                    link.download = nombreArchivo;
                    document.body.appendChild(link);
                    link.click();
                    link.remove();
                    window.URL.revokeObjectURL(url);
                    showToast('Reporte generado correctamente.', 'success');
                },
                error: function(xhr, status, err){
                    console.error("Error reporte:", status, err);
                    console.error("Response:", xhr.responseText);
                    showToast('Error al generar reporte: ' + status, 'error');
                },
                complete: function(){
                    // Ocultar spinner y reactivar botón
                    $('#spinner').hide();
                    $('#btnText').html('<span class="material-icons" style="font-size:1em;">download</span> Generar');
                    $('#generarBtn').prop('disabled', false);
                }
            });

        });


    });
</script>

</body>
</html>
