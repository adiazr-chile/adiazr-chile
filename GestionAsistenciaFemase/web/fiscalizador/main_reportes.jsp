<%@page import="java.time.LocalDate"%>
<%@page import="cl.femase.gestionweb.vo.FiltroBusquedaEmpleadosVO"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="cl.femase.gestionweb.vo.EmpleadoConsultaFiscalizadorVO"%>
<%@page import="cl.femase.gestionweb.vo.CargoVO"%>
<%@page import="cl.femase.gestionweb.vo.TurnoVO"%>
<%@page import="cl.femase.gestionweb.vo.UsuarioCentroCostoVO"%>
<%@page import="java.util.List"%>
<%
    List<UsuarioCentroCostoVO> cencos   = (List<UsuarioCentroCostoVO>)session.getAttribute("cencos_empleado");
    List<TurnoVO> turnos   = (List<TurnoVO>)session.getAttribute("turnos");
    List<CargoVO> cargos   = (List<CargoVO>)session.getAttribute("cargos");

    List<EmpleadoConsultaFiscalizadorVO> listaEmpleados = 
        (List<EmpleadoConsultaFiscalizadorVO>)request.getAttribute("fiscaliza_lista_empleados");
    if (listaEmpleados == null) listaEmpleados = new ArrayList<>();

    System.out.println("[fiscalizador.main_reportes.jsp]"
        + "empleados.size= " + listaEmpleados.size());
    
    FiltroBusquedaEmpleadosVO filtroEnSesion = (FiltroBusquedaEmpleadosVO)request.getAttribute("fiscaliza_filtro");
    LocalDate fechaActual = LocalDate.now();
    LocalDate fechaUnMesAtras = fechaActual.minusMonths(1);
    
    //Filtros de busqueda, valores por defecto
    String filtroTipoReporte = "asistencia";
    int filtroCencoId   = -1;
    String filtroRunEmpleado = "";
    String filtroNombre = "";
    int filtroTurnoId = -1;
    int filtroCargoId = -1;
    String filtroDesde = fechaUnMesAtras.toString();
    String filtroHasta = fechaActual.toString();
    String filtroFormatoSalida="PDF";
    String filtroPeriodoPredefinido = "mes";        
    if (filtroEnSesion != null){
        filtroTipoReporte=filtroEnSesion.getTipoReporte();
        filtroCencoId =filtroEnSesion.getCencoId();
        filtroRunEmpleado=filtroEnSesion.getRunEmpleado();
        filtroNombre=filtroEnSesion.getNombre();
        filtroTurnoId=filtroEnSesion.getTurnoId();
        filtroCargoId=filtroEnSesion.getCargoId();
        filtroDesde=filtroEnSesion.getDesde();
        filtroHasta=filtroEnSesion.getHasta();
        filtroFormatoSalida = filtroEnSesion.getFormatoSalida();
        filtroPeriodoPredefinido = filtroEnSesion.getPeriodoPredefinido();
        
        System.out.println("[fiscalizador.main_reportes.jsp]"
            + "filtro de busqueda Obj: " + filtroEnSesion.toString());
    }    

    

%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Reportes / Fiscalización / Selección empleados</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.10.0/css/bootstrap-datepicker.min.css" rel="stylesheet"/>
    <link href="https://cdn.datatables.net/1.13.6/css/dataTables.bootstrap5.min.css" rel="stylesheet">
    <link href="https://cdn.datatables.net/buttons/2.4.2/css/buttons.bootstrap5.min.css" rel="stylesheet">
    <style>
        body { padding: 10px; font-size: 0.93rem; }
        .row-filtros { margin-bottom: 5px; gap: 0.8rem;}
        .form-control-sm, .form-select-sm { font-size: 0.88rem; height:2.05rem; }
        .datepicker { background-color: #fff; }
        .input-radio-inline { display: inline-block; margin-right: 0.8rem; font-size:0.98em; vertical-align: middle;}
        .input-radio-inline input[type="radio"] { margin-right: 3px;}
        .card-body { padding: 10px; }
        
        .page-title {
            font-size: 1.18rem;
            font-weight: 600;
            margin-bottom: 1.1rem;
            background: linear-gradient(90deg, #e6f0fa 0%, #fafdff 100%);
            padding: 16px 24px;
            border-radius: 8px 8px 0 0;
            border-bottom: 2.5px solid #3394dc;
            color: #22334c;
            letter-spacing: 0.5px;
            box-shadow: 0 4px 8px 0 rgba(51, 148, 220, 0.03);
            display: inline-block;
        }
        
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
		  padding: 5px 19px;
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
	
                .form-select.form-select-sm {
		  background-color: #e6f0fa; /* azul muy suave */
		  border: 1.5px solid #3394dc; /* azul profesional */
		  color: #22334c; /* azul oscuro para el texto */
		  font-weight: 500;
		  box-shadow: none;
		  transition: border-color 0.2s, box-shadow 0.2s;
		}

		.form-select.form-select-sm:focus {
		  border-color: #1761a0; /* azul más fuerte al enfocar */
		  box-shadow: 0 0 0 2px rgba(51,148,220,0.15);
		  background-color: #f4faff;
		}
                
                .select2-container--bootstrap-5 .select2-selection,
		.select2-container--bootstrap-5 .select2-selection--single,
		.select2-container--bootstrap-5 .select2-selection--multiple {
		  background-color: #e6f0fa !important; /* Azul claro profesional */
		  border: 1.5px solid #3394dc;
		  color: #22334c;
		  font-size: 0.70em !important;
		}

		.select2-container--bootstrap-5 .select2-selection:focus,
		.select2-container--bootstrap-5 .select2-selection--single:focus,
		.select2-container--bootstrap-5 .select2-selection--multiple:focus {
		  border-color: #1761a0;
		  box-shadow: 0 0 0 2px rgba(51,148,220,0.15);
		  outline: none;
		  font-size: 0.70em !important;
		}
	
		.select2-container--bootstrap-5 .select2-results__option {
		  font-size: 0.70em !important;
		}
    
                /* Destaca el ítem seleccionado */
		.select2-container--bootstrap-5 .select2-results__option--selected {
		  background-color: #3394dc !important;  /* azul corporativo */
		  color: #fff !important;
		}
		/* Destaca el ítem con foco (hover/teclado) */
		.select2-container--bootstrap-5 .select2-results__option--highlighted {
		  background-color: #b1d6f4 !important;  /* azul claro */
		  color: #22334c !important;
		  cursor: pointer;
		}
                
                #centroCostoId.select2-hidden-accessible + .select2-container--bootstrap-5 {
		  width: 300px !important;
		}
		#turnoId.select2-hidden-accessible + .select2-container--bootstrap-5 {
		  width: 150px !important;
		}
		#cargoId.select2-hidden-accessible + .select2-container--bootstrap-5 {
		  width: 150px !important;
		}
                
                .td_datatable {
			width: 200px;
			white-space: normal !important;
			overflow-wrap: break-word !important;
			font-size: 11px;
			word-wrap: break-word;
			overflow: hidden;
			/* NO usar text-overflow: ellipsis */
		}

		.td_datatable_chica {
			width: 100px;
			white-space: normal !important;
			overflow-wrap: break-word !important;
			font-size: 11px;
			word-wrap: break-word;
			overflow: hidden;
			/* NO usar text-overflow: ellipsis */
		}
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
            <form id="frmBusqueda" name="frmBusqueda" method="POST" 
              action="<%=request.getContextPath()%>/fiscaliza/BuscarEmpleadosServlet?action=list" autocomplete="off">
                <!-- FILA 1 -->
                <div class="row row-cols-auto align-items-center row-filtros">
                    <div class="col">
                        <select name="tipo_reporte" id="tipo_reporte" class="form-select form-select-sm" style="min-width:160px">
                            <option value="-1">Tipo Reporte</option>
                            <option value="asistencia" selected>Asistencia</option>
                            <option value="jornada_diaria">Jornada diaria</option>
                            <option value="domingos_festivos">Días domingo y/o días festivos</option>
                            <option value="modificaciones_turnos">Modificaciones y/o alteraciones de turnos</option>
                            <option value="diario">Diario</option>
                            <option value="incidentes_tecnicos">Incidentes técnicos</option>
                        </select>
                    </div>
                    <div class="col">
                        <select id="modoBusqueda" 
                                name="modoBusqueda" 
                                onchange="actualizaCamposModoBusqueda()" class="form-select form-select-sm" style="min-width:160px">
                            <option value="individual" selected>Búsqueda Individual</option>
                            <option value="grupal">Búsqueda Grupal</option>
                        </select>
                    </div>
                    
					<div id="filtrosGrupales" class="oculto">
						  <select name="centroCostoId" id="centroCostoId">
						  <option value="-1" selected>Seleccione Centro Costo</option>
						  <%
                                        String valueCenco="";
                                        String labelCenco="";    
                                        Iterator<UsuarioCentroCostoVO> iteracencos = cencos.iterator();
                                        while(iteracencos.hasNext() ) {
                                            UsuarioCentroCostoVO auxCenco = iteracencos.next();
                                            valueCenco = auxCenco.getEmpresaId() + "|" + auxCenco.getDeptoId() + "|" + auxCenco.getCcostoId();
                                            labelCenco = "[" + auxCenco.getEmpresaNombre()+ "]," 
                                                + "[" + auxCenco.getDeptoNombre()+ "],"
                                                + "[" + auxCenco.getCcostoNombre()+ "]";
                                        %>
                                            <option value="<%=valueCenco%>"><%=labelCenco%></option>
                                            <%
                                        }
                                    %>
                                                  </select>
						  <select name="turnoId" id="turnoId">
							<option value="-1" selected>Seleccione Turno</option>
							<%
                                                        Iterator<TurnoVO> iteraturnos = turnos.iterator();
                                                        while(iteraturnos.hasNext() ) {
                                                            TurnoVO auxturno = iteraturnos.next();
                                                            String turnoLabel = "(" + auxturno.getId() + ") " + auxturno.getNombre();
                                                            %>
                                                            <option value="<%=auxturno.getId()%>"><%=turnoLabel%></option>
                                                            <%
                                                        }   
                                                    %>
						  </select>
						  <select name="cargoId" id="cargoId">
							<option value="-1" selected>Seleccione Cargo</option>
							<%
                                                            Iterator<CargoVO> iteracargos = cargos.iterator();
                                                            while(iteracargos.hasNext() ) {
                                                                CargoVO auxcargo = iteracargos.next();
                                                                %>
                                                                <option value="<%=auxcargo.getId()%>"><%=auxcargo.getNombre()%></option>
                                                                <%
                                                            }
                                                        %>
						  </select>
						</div>
						<div id="filtrosIndividuales" class="visible">
						  <input type="text" id="run" name="run" placeholder="RUN">
						  <input type="text" id="nombre" name="nombre" placeholder="Nombre">
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
                        <select id="periodoPredefinido" name="periodoPredefinido" class="form-select form-select-sm" style="min-width:120px">
                            <option value="semana">Semana</option>
                            <option value="quincena">Quincena</option>
                            <option value="mes">Mes Ant.</option>
                            <option value="anio">Año</option>
                        </select>
						
                    </div>
                    <div class="col campo-desde" style="display:none;">
                        <input type="text" class="form-control form-control-sm datepicker" name="fecha_inicio" id="fecha_inicio" placeholder="Desde" value="<%=filtroDesde%>">
                    </div>
                    <div class="col campo-hasta" style="display:none;">
                        <input type="text" class="form-control form-control-sm datepicker" name="fecha_fin" id="fecha_fin" placeholder="Hasta" value="<%=filtroHasta%>">
                    </div>
                    <div class="col campo-formato-salida">
                        <select id="formato_salida" name="formato_salida" class="form-select form-select-sm" style="min-width:120px">
                            <option value="PDF" selected>Reporte PDF</option>
                            <option value="XLSX">Reporte EXCEL</option>
                            <option value="DOCX">Reporte WORD</option>
                        </select>
                    </div>
                    <div class="col mx-2">
                        <button type="submit" class="btn btn-primary rounded-pill shadow-sm px-4">Buscar</button>
                        <button type="reset" id="btnLimpiar" class="btn btn-outline-secondary rounded-pill shadow-sm px-4">Limpiar</button>
		    </div>
                </div>
            </form>
        </div>
    </div>

    <!-- TABLA RESULTADOS CON EXPORTACIÓN -->
    <div class="card">
        <div class="card-body">
			<form id="formSeleccion"
                              name="formSeleccion"
                              action="<%=request.getContextPath()%>/fiscaliza/BuscarEmpleadosServlet?action=launchReport" autocomplete="off">
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
                                                <th>Asistencia</th>
					</tr>
					</thead>
					<tbody>
                                            <%
                                            Iterator<EmpleadoConsultaFiscalizadorVO> iter = listaEmpleados.iterator();
                                            EmpleadoConsultaFiscalizadorVO empleado = null;
                                            while (iter.hasNext()) {
                                                empleado = iter.next();

                                                String run      = empleado.getRun();
                                                String nombre   = empleado.getNombre();
                                                String empresaNombre   = empleado.getEmpresaNombre();
                                                String cencoNombre   = empleado.getCencoNombre();
                                                String estadoLabel   = empleado.getEstadoLabel();
                                                String turno   = empleado.getNombreTurno();
                                                String cargo   = empleado.getNombreCargo();
                                                String tieneAsistencia = empleado.getTieneAsistencia();
                                            %>
						<tr>
                                                    <td> <input type="checkbox" name="seleccion_registro[]" value="<%=run%>"></td>
							<td class="td_datatable_chica"><%=run%></td>
							<td class="td_datatable"><%=nombre%></td>
							<td class="td_datatable_chica"><%=empresaNombre%></td>
							<td class="td_datatable"><%=cencoNombre%></td>
							<td class="td_datatable_chica"><%=estadoLabel%></td>
							<td class="td_datatable"><%=turno%></td>
							<td class="td_datatable"><%=cargo%></td>
                                                        <td class="td_datatable"><%=tieneAsistencia%></td>
						</tr>
                                                <%}%>
					</tbody>
				</table>
                                        
                                <div id="spinnerCarga" style="display:none; text-align:center;">
				  <div class="spinner-border text-primary" role="status">
					<span class="visually-hidden">Cargando...</span>
				  </div>
				  <div>Cargando registros...</div>
				</div>        
                                        
                            <input type="hidden" id="tipoReporte" name="tipoReporte" value="<%=filtroTipoReporte%>">
                            <input type="hidden" id="periodoPredefinido" name="periodoPredefinido" value="<%=filtroPeriodoPredefinido%>">
				<input type="hidden" id="fecha_inicio" name="fecha_inicio" value="<%=filtroDesde%>">
				<input type="hidden" id="fecha_fin" name="fecha_fin" value="<%=filtroHasta%>">
				<input type="hidden" id="formato_salida" name="formato_salida" value="<%=filtroFormatoSalida%>">
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
$(document).ready(function() {

    // Inicializar select2 en los selects grupales
    $('#centroCostoId, #turnoId, #cargoId').select2(
        { 
            theme: "bootstrap-5", 
            width:'resolve'
        }
    );
    $('.datepicker').datepicker({
        format: "yyyy-mm-dd",
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
        dom: '<"row mb-2"<"col-sm-6"l><"col-sm-6"f>>' + // l = length (combo tamaño), f = search (buscador)
             'rt' +
             '<"row mt-2"<"col-sm-5"i><"col-sm-7"p>>',   // i = info, p = paginado
        buttons: [
            { extend: 'excelHtml5', text: 'Exportar Excel', className: 'btn btn-sm btn-success' },
            { extend: 'csvHtml5', text: 'Exportar CSV', className: 'btn btn-sm btn-info' }
        ],
        paging: true,
        pageLength: 10,
        lengthMenu: [ [5, 10, 25, 50, -1], [5, 10, 25, 50, "Todos"] ],
        language: {
            lengthMenu: "Mostrar _MENU_ registros por página",
            info: "Mostrando registros del _START_ al _END_ de un total de _TOTAL_ registros",
            infoEmpty: "Mostrando 0 registros",
            infoFiltered: "(filtrado de _MAX_ registros totales)",
            paginate: {
                previous: "Anterior",
                next: "Siguiente"
            }
            // Puedes agregar más traducciones según necesidad
        },
        ordering: true,
        order: [[1, 'asc']],
        responsive: true,
        columnDefs: [
            { targets: 0, orderable: false },
            { targets: 3, orderable: false } 
        ]
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
        e.preventDefault();
        const seleccionados = [];
        document.querySelectorAll('input[name="seleccion_registro[]"]:checked').forEach(function(checkbox) {
            seleccionados.push(checkbox.value);
        });
        //alert('Registros seleccionados:\n' + seleccionados.join('\n'));
        e.target.submit(); // Descomenta si quieres enviar el form después del alert
    });
    
    /*
    document.getElementById('formSeleccion').addEventListener('submit', function(e) {
        e.preventDefault();
        const tipoReporte = document.getElementById('tipoReporte')?.value || '';
        // Mostrar spinner
        document.getElementById('spinnerCarga').style.display = 'block';
        // Obtener los valores seleccionados 
        const seleccionados = [];
        document.querySelectorAll('input[name="seleccion_registro[]"]:checked').forEach(function(checkbox) {
            seleccionados.push(checkbox.value);
        });
        // Prepara el FormData
        const formData = new FormData();
        seleccionados.forEach(function(valor) {
            formData.append('seleccion_registro[]', valor);
        });
        Array.from(e.target.elements).forEach(el => {
            if (el.type !== "checkbox" && el.name && el.value !== undefined) 
                formData.append(el.name, el.value);
        });
        fetch(e.target.action, {
            method: 'POST',
            body: formData
        })
        .then(response => {
            if (!response.ok) throw new Error('No se pudo generar el archivo ZIP');
            return response.blob();
        })
        .then(blob => {
            // Se eliminó la parte de descarga Forzada ZIP
            document.getElementById('spinnerCarga').style.display = 'none';
            // Aquí puedes agregar alguna acción, si es necesario tratar el blob de otro modo
        })
        .catch(error => {
            document.getElementById('spinnerCarga').style.display = 'none';
            alert('Hubo un error al descargar el archivo: ' + error.message);
        });
    });
    */

   
   
    let spinnerMostradoEn = 0;
    const MINIMO_SPINNER = 700; // milisegundos (0.7 segundos)
    
    document.getElementById('frmBusqueda').addEventListener('submit', function(e) {
        e.preventDefault();
        // Obtención de valores
        const tipoReporte = document.getElementById('tipo_reporte')?.value || '';
        const modoBusqueda = document.getElementById('modoBusqueda')?.value || '';
        const run = document.getElementById('run')?.value || '';
        const nombre = document.getElementById('nombre')?.value || '';
        const centroCostoId = document.getElementById('centroCostoId')?.value || '';
        const turnoId = document.getElementById('turnoId')?.value || '';
        const cargoId = document.getElementById('cargoId')?.value || '';
        const periodoPredefinido = document.getElementById('periodoPredefinido')?.value || '';
        const tipoFecha = document.querySelector('input[name="tipoFecha"]:checked')?.value || '';
        const fechaInicio = document.getElementById('fecha_inicio')?.value || '';
        const fechaFin = document.getElementById('fecha_fin')?.value || '';
        // Validaciones
        if(!tipoReporte || tipoReporte === '-1') {
            return showWarningToast('Debe seleccionar el tipo de reporte.');
        }
        if(modoBusqueda === 'individual') {
			if(!run && !nombre) return showWarningToast('Debe ingresar campos RUN o Nombre.');
        } else {
			var hasCenco = true;
			var hasTurno = true;
			var hasCargo = true;
			if(!centroCostoId || centroCostoId === '-1') hasCenco = false;
            if(!turnoId || turnoId === '-1') hasTurno = false;
            if(!cargoId || cargoId === '-1') hasCargo = false;
			if (!hasCenco && !hasTurno && !hasCargo) return showWarningToast('Seleccione uno de los filtros (Centro de costo, Turno o Cargo)');
        }
        if(tipoFecha === 'periodo') {
            if(!periodoPredefinido || periodoPredefinido === '-1') return showWarningToast('Debe seleccionar un Período predefinido.');
        } else {
            if(!fechaInicio) return showWarningToast('Debe ingresar la fecha de inicio.');
            if(!fechaFin) return showWarningToast('Debe ingresar la fecha de fin.');
        }
       
        $('#spinnerCarga').show();
        spinnerMostradoEn = Date.now();
        // Aquí tu lógica para mostrar los datos, por ejemplo:
        setTimeout(function() {
          ocultaSpinnerConDelay();
        }, 10); // Simula el "fin de la carga"
        // 
        // Si todo bien, submit real
        this.submit();
    });

    // Función para ocultar el spinner tras un mínimo de tiempo:
    function ocultaSpinnerConDelay() {
      let tiempoTranscurrido = Date.now() - spinnerMostradoEn;
      if (tiempoTranscurrido < MINIMO_SPINNER) {
        setTimeout(function() {
          $('#spinnerCarga').hide();
        }, MINIMO_SPINNER - tiempoTranscurrido);
      } else {
        $('#spinnerCarga').hide();
      }
    }

    function showWarningToast(mensaje) {
	  document.getElementById('toastMessage').textContent = mensaje;
	  var toastElement = document.getElementById('liveToast');
	  var toast = bootstrap.Toast.getOrCreateInstance(toastElement); // Bootstrap 5 método recomendado
	  toast.show();
	}
});

</script>

<div class="position-fixed top-0 end-0 p-3" style="z-index: 1055">
  <div id="liveToast" class="toast align-items-center text-white bg-warning border-0" role="alert" aria-live="assertive" aria-atomic="true">
    <div class="d-flex">
      <div class="toast-body" id="toastMessage">
        <!-- El mensaje aparecerá aquí -->
      </div>
      <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
    </div>
  </div>
</div>

</body>
</html>
