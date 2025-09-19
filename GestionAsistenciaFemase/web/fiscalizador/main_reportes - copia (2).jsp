<%@page import="java.time.LocalDate"%>
<%@page import="cl.femase.gestionweb.vo.FiltroBusquedaEmpleadosVO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="cl.femase.gestionweb.vo.EmpleadoConsultaFiscalizadorVO"%>
<%@page import="cl.femase.gestionweb.vo.CargoVO"%>
<%@page import="cl.femase.gestionweb.vo.TurnoVO"%>
<%@page import="java.util.Iterator"%>
<%@page import="cl.femase.gestionweb.vo.UsuarioCentroCostoVO"%>
<%@page import="java.util.List"%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Reportes / Fiscalizacion / Seleccion empleados</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.10.0/css/bootstrap-datepicker.min.css" rel="stylesheet"/>
    <link href="https://cdn.datatables.net/1.13.6/css/dataTables.bootstrap5.min.css" rel="stylesheet">
    <link href="https://cdn.datatables.net/buttons/2.4.2/css/buttons.bootstrap5.min.css" rel="stylesheet">
    <style>
        body { padding: 10px; font-size: 0.93rem; }
        .row-filtros { margin-bottom: 5px; gap: 0.8rem;}
        .form-control-sm, .form-select-sm { font-size: 0.88rem; height:2.05rem; }
        .select2-container--bootstrap-5 .select2-selection--single { min-height:2.05rem; }
        .select2-selection__rendered { font-size: 0.88rem; line-height: 2.05rem; }
        .datepicker { background-color: #fff; }
        .input-radio-inline { display: inline-block; margin-right: 0.8rem; font-size:0.98em; vertical-align: middle;}
        .input-radio-inline input[type="radio"] { margin-right: 3px;}
        .card-body { padding: 10px; }
        .page-title { font-size: 1.18rem; font-weight: 600; margin-bottom: 1.1rem; }
        .grupo-filtros-grupal {background: #f7f8fa; border-radius: 7px; padding:6px 7px; margin-right: 1.0rem;}
        @media(max-width: 992px){
            .row-filtros {flex-wrap:wrap;}
            .grupo-filtros-grupal {margin-bottom:6px;}
        }
        .dt-buttons { float: right;}

		td {
		  word-wrap: break-word;
		  word-break: break-all;
		}

		
		.btn-enviar-seleccion {
		  background-color: #007bff;       /* Azul brillante */
		  color: #fff;                     /* Blanco para el texto */
		  font-size: 1.1rem;
		  font-weight: bold;
		  padding: 12px 28px;
		  border: none;
		  border-radius: 8px;
		  cursor: pointer;
		  box-shadow: 0 4px 12px rgba(0,0,0,0.18);
		  transition: background 0.2s;
		}

		.btn-enviar-seleccion:hover {
		  background-color: #0056b3;       /* Azul más oscuro al pasar el mouse */
		}
		
		
		.filtros-sticky {
			position: sticky;
			top: 0;
			background: #fff;
			z-index: 10;
			box-shadow: 0 2px 8px rgba(0,0,0,0.04);
			padding: 18px 20px;
			border-radius: 8px;
		}
		
		.oculto { display: none; }
		.visible { display: block; }
	
    </style>
	
	<!-- JS PARA LÓGICA DE MODO DE BÚSQUEDA Y LIMPIAR -->
  <script>
    function actualizaCamposModoBusqueda() {
      var modo = document.getElementById('modoBusqueda').value;
      var grupal = document.getElementById('filtrosGrupales');
      var individual = document.getElementById('filtrosIndividuales');
      if(modo === 'grupal') {
        grupal.className = 'visible';
        individual.className = 'oculto';
      } else {
        grupal.className = 'oculto';
        individual.className = 'visible';
      }
    }
    window.addEventListener('DOMContentLoaded', function() {
      actualizaCamposModoBusqueda();
      document.getElementById('frmBusqueda').addEventListener('reset', function() {
        setTimeout(function(){
          document.getElementById('filtrosGrupales').className = 'oculto';
          document.getElementById('filtrosIndividuales').className = 'visible';
          document.getElementById('modoBusqueda').selectedIndex = 0;
          actualizaCamposModoBusqueda();
        }, 10);
      });
    });
  </script>
	
</head>
<body>
<div class="container-fluid">
    <div class="page-title">Reportes / Fiscalización / Selección empleados</div>

    <div class="card mb-2">
        <div class="card-body filtros-sticky">
            <form id="frmBusqueda" autocomplete="off">
                <input type="hidden" id="action" name="action" value="list">
				<!-- FILA 1 -->
                <div class="row row-cols-auto align-items-center row-filtros">
                    <div class="col">
                        <select name="tipo_reporte" id="tipo_reporte" class="form-select form-select-sm" style="min-width:160px">
                            <option value="-1">Tipo Reporte</option>
                            <option value="asistencia">Asistencia</option>
                            <option value="jornad">Jornada Diaria</option>
                            <option value="domingos_festivos">Días domingos y/o festivos</option>
                            <option value="modificaciones_turnos"> dificaciones y/o alteraciones de turnos</option>
                            <option value="diario">Diario</option>
                            <option value="incidentes_tecnicos" >Incidentes técnicos</option>
                        </select>
                    </div>
                    <div class="col">
                        <select id="modoBusqueda" name="modoBusqueda" onchange="actualizaCamposModoBusqueda()">
						  <option value="individual" selected>Búsqueda Individual</option>
						  <option value="grupal">Búsqueda Grupal</option>
						</select>
                    </div>
                    
					<div id="filtrosGrupales" class="oculto">
						  <select name="centroCostoId" id="centroCostoId">
						  <option value="-1" selected>Seleccione Centro Costo</option>
						  <option>[Fundacion Mi Casa],[I- Tarapacá],[CENIM POZO ALMONTE]</option>
						  <option>[Fundacion Mi Casa],[I- Tarapacá],[RESIDENCIA IQUIQUE]</option>
						  <option>[Fundacion Mi Casa],[II- Antofagasta],[AFT-PF CALAMA]</option>
						</select>
						  <select name="turnoId" id="turnoId">
							<option value="-1" selected>Seleccione Turno</option>
							<option>LV 08:00-17:00</option>
							<option>LV 08:30-18:00</option>
							<option>LV 09:00-18:30</option>
						  </select>
						  <select name="cargoId" id="cargoId">
							<option value="-1" selected>Seleccione Cargo</option>
							<option>ABOGADO/A</option>
							<option>AUXILIAR</option>
							<option>AUXILIAR DE ASEO</option>
						  </select>
						</div>
						<div id="filtrosIndividuales" class="visible">
						  <input type="text" name="run" placeholder="RUN">
						  <input type="text" name="nombre" placeholder="Nombre">
						</div>
                </div>
                <!-- FILA 2 -->
                <div class="row row-cols-auto align-items-center row-filtros">
                    <div class="col input-radio-inline">
                        <input type="radio" name="tipoFecha" id="fechaPeriodo" value="periodo" checked>
                        <label for="fechaPeriodo" class="me-2">Período</label>
                        <input type="radio" name="tipoFecha" id="fechaRango" value="rango">
                        <label for="fechaRango">Rango</label>
                    </div>
                    <div class="col campo-periodo">
                        <select id="periodoPredefinido" class="form-select form-select-sm" style="min-width:120px">
                            <option value="semana">Semana</option>
                            <option value="quincena">Quincena</option>
                            <option value="mes">Mes Ant.</option>
                            <option value="anio">Año</option>
                        </select>
						
                    </div>
                    <div class="col campo-desde" style="display:none;">
                        <input type="text" class="form-control form-control-sm datepicker" name="fecha_inicio" id="fecha_inicio" placeholder="Desde">
                    </div>
                    <div class="col campo-hasta" style="display:none;">
                        <input type="text" class="form-control form-control-sm datepicker" name="fecha_fin" id="fecha_fin" placeholder="Hasta">
                    </div>
					<div class="col campo-formato-salida">
						<select id="formato_salida" class="form-select form-select-sm" style="min-width:120px">
                            <option value="PDF" selected>Reporte PDF</option>
							<option value="XLSX">Reporte EXCEL</option>
							<option value="DOCX">Reporte WORD</option>
                        </select>
                    </div>
                    <div class="col mx-2">
                        <!--
						<button type="submit" class="btn btn-sm btn-primary">Buscar</button>
                        <button type="reset" id="btnLimpiar" class="btn btn-sm btn-secondary">Limpiar</button>
						-->
						<button type="submit" class="btn btn-primary rounded-pill shadow-sm px-4">Buscar</button>
						<button type="reset" id="btnLimpiar" class="btn btn-outline-secondary rounded-pill shadow-sm px-4">Limpiar</button>
						
						<!--<button type="reset" class="btn btn-outline-secondary rounded-pill shadow-sm px-4">Limpiar</button>-->
					</div>
                </div>
            </form>
        </div>
    </div>

    <!-- TABLA RESULTADOS CON EXPORTACIÓN -->
    <div class="card">
        <div class="card-body">
			<form id="formSeleccion">
				<table id="tablaEmpleados" class="table table-sm table-striped table-bordered nowrap" style="width:100%">
					<thead>
					<tr>
						<th><input type="checkbox" id="selectAll"></th>
						<th>RUN</th>
						<th>Nombre</th>
						<th>Empresa</th>
						<th>Centro Costo</th>
						<th>Estado</th>
						<th>Turno</th>
						<th>Cargo</th>
					</tr>
					</thead>
					<tbody>
						<!-- Datos desde backend -->
						<tr>
							<td> <input type="checkbox" name="seleccion_registro[]" value="9184215-7B"></td>
							<td>9184215-7B</td>
							<td>ADRIANA VICENTINA VERGARA GUERRA</td>
							<td>Fundacion Mi Casa</td>
							<td>ADMINISTRACION CENTRAL</td>
							<td>Vigente</td>
							<td style="width: 200px; word-wrap: break-word; white-space: normal; overflow: hidden; text-overflow: ellipsis;">
								(414) Lunes a Jueves : 10:30 - 20:00 / 30 min Colacion -- Viernes : 10:30 - 19:00 / 30 min Colacion
							</td>
							<td style="width: 200px; word-wrap: break-word; white-space: normal; overflow: hidden; text-overflow: ellipsis;">
								APOYO PROFESIONAL DIRECCION GENERAL DE FINANZAS Y SUPERVISION INTEGRAL
							</td>
						</tr>
						<tr>
							<td> <input type="checkbox" name="seleccion_registro[]" value="13055851-8"></td>
							<td>13055851-8</td>
							<td>Beatriz Edith López Betancourt</td>
							<td>Fundacion Mi Casa</td>
							<td>ADMINISTRACION CENTRAL</td>
							<td>Vigente</td>
							<td style="width: 200px; word-wrap: break-word; white-space: normal; overflow: hidden; text-overflow: ellipsis;">
								(409) lunes, miercoles y viernes : 15:00 - 20:00 -- sabado : 09:00 - 16:00
							</td>
							<td style="width: 200px; word-wrap: break-word; white-space: normal; overflow: hidden; text-overflow: ellipsis;">
								AUXILIAR DE ASEO Y LAVANDERIA
							</td>
						</tr>
					</tbody>
					
				</table>
				<input type="hidden" id="tipoReporte" name="tipoReporte" value="">
				<input type="hidden" id="periodoPredefinido" name="periodoPredefinido" value="">
				<input type="hidden" id="fecha_inicio" name="fecha_inicio" value="">
				<input type="hidden" id="fecha_fin" name="fecha_fin" value="">
				<input type="hidden" id="formato_salida" name="formato_salida" value="">
				<input type="hidden" id="action" name="action" value="launchReport">
				<button type="submit" class="btn-enviar-seleccion">Generar Reporte</button>
			</form>
			
        </div>
    </div>
</div>

<!-- LIBRERÍAS -->
<script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.10.0/js/bootstrap-datepicker.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.10.0/locales/bootstrap-datepicker.es.min.js"></script>
<script src="https://cdn.datatables.net/1.13.6/js/jquery.dataTables.min.js"></script>
<script src="https://cdn.datatables.net/1.13.6/js/dataTables.bootstrap5.min.js"></script>
<script src="https://cdn.datatables.net/buttons/2.4.2/js/dataTables.buttons.min.js"></script>
<script src="https://cdn.datatables.net/buttons/2.4.2/js/buttons.bootstrap5.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.10.1/jszip.min.js"></script>
<script src="https://cdn.datatables.net/buttons/2.4.2/js/buttons.html5.min.js"></script>
<script>
$(function() {
    
	// Inicializar select2 en los selects grupales
    $('#centroCostoId, #turnoId, #cargoId').select2({ theme: "bootstrap-5", width:'resolve' });

    $('.datepicker').datepicker({
        format: "dd-mm-yyyy",
        language: "es",
        autoclose: true,
        todayHighlight: true,
        endDate: new Date()
    });

    
    // Periodo/Rango switches
    $('input[name="tipoFecha"]').change(function(){
        if($('#fechaPeriodo').is(':checked')){
            $('.campo-periodo').show();
            $('.campo-desde, .campo-hasta').hide();
        }else{
            $('.campo-periodo').hide();
            $('.campo-desde, .campo-hasta').show();
        }
    }).trigger('change');

    // DataTable con orden, paginación y exportación CSV/XLSX
    var tabla = $('#tablaEmpleados').DataTable({
		dom: 'Blfrtip',
		buttons: [
			{ extend: 'excelHtml5', text: 'Exportar Excel', className: 'btn btn-sm btn-success' },
			{ extend: 'csvHtml5', text: 'Exportar CSV', className: 'btn btn-sm btn-info' }
		],
		paging: true,
		pageLength: 10,
		lengthMenu: [ [5, 10, 25, 50, -1], [5, 10, 25, 50, "Todos"] ],
		language: {
			lengthMenu: "Mostrar _MENU_ registros por página",
			info: "Mostrando registros del _START_ al _END_ de un total de _TOTAL_ registros"
		},
		ordering: true,
		responsive: true
	});


    // Checkbox seleccionar todo
    $('#selectAll').on('click', function () {
        var rows = tabla.rows({ 'search': 'applied' }).nodes();
        $('input[type="checkbox"]', rows).prop('checked', this.checked);
    });

   
	$("#btnLimpiar").on("click", function() {
	  let form = document.getElementById("frmBusqueda");
	  form.reset();
	  setTimeout(function() {
		$("#centroCostoId").val("-1").trigger("change");
		$("#turnoId").val("-1").trigger("change");
		$("#cargoId").val("-1").trigger("change");
	  }, 10);
	});

		document.getElementById('formSeleccion').addEventListener('submit', function(e) {
		  e.preventDefault(); // Evitar el envío inmediato
		  const seleccionados = [];
		  document.querySelectorAll('input[name="seleccion_registro[]"]:checked').forEach(function(checkbox) {
			seleccionados.push(checkbox.value);
		  });
		  alert('Registros seleccionados:\n' + seleccionados.join('\n'));
		  // Puedes remover el siguiente comentario para enviar realmente el formulario después del alert.
		  // e.target.submit();
		});
	
		document.getElementById('frmBusqueda').addEventListener('submit', function(e) {
		  e.preventDefault();

		  // Obtén los valores de los filtros que te interesan
		  const tipoReporte = document.getElementById('tipoReporte').value;
		  const centroCosto = document.getElementById('centroCostoId').value;
		  const periodo = document.getElementById('periodoPredefinido').value;

		  // Crea el mensaje de resumen
		  let mensaje =
			'Campos de búsqueda seleccionados:\n' +
			'- Tipo de reporte: ' + tipoReporte + '\n' +
			'- Centro de costo: ' + centroCosto + '\n' +
			'- Período: ' + periodo;

		  alert(mensaje);

		  // Después del alert, envía realmente el formulario
		  e.target.submit();
		});
	
	
});
</script>
</body>
</html>
