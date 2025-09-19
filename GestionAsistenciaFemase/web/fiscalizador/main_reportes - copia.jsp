<%@page import="java.time.LocalDate"%>
<%@page import="cl.femase.gestionweb.vo.FiltroBusquedaEmpleadosVO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="cl.femase.gestionweb.vo.EmpleadoConsultaFiscalizadorVO"%>
<%@page import="cl.femase.gestionweb.vo.CargoVO"%>
<%@page import="cl.femase.gestionweb.vo.TurnoVO"%>
<%@page import="java.util.Iterator"%>
<%@page import="cl.femase.gestionweb.vo.UsuarioCentroCostoVO"%>
<%@page import="java.util.List"%>
<%
    List<UsuarioCentroCostoVO> cencos   = (List<UsuarioCentroCostoVO>)session.getAttribute("cencos_empleado");
    List<TurnoVO> turnos   = (List<TurnoVO>)session.getAttribute("turnos");
    List<CargoVO> cargos   = (List<CargoVO>)session.getAttribute("cargos");

    List<EmpleadoConsultaFiscalizadorVO> listaEmpleados = (List<EmpleadoConsultaFiscalizadorVO>)session.getAttribute("fiscaliza_lista_empleados");
    if (listaEmpleados == null) listaEmpleados = new ArrayList<>();

    System.out.println("empleados.size= " + listaEmpleados.size());
    
    FiltroBusquedaEmpleadosVO filtroEnSesion = (FiltroBusquedaEmpleadosVO)request.getAttribute("fiscaliza_filtro");

    LocalDate fechaActual = LocalDate.now();
    LocalDate fechaUnMesAtras = fechaActual.minusMonths(1);
    System.out.println("Fecha actual: " + fechaActual);
    System.out.println("Fecha un mes atrás: " + fechaUnMesAtras);
    
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
    }
    System.out.println("[main_reportes.jsp]filtroFormatoSalida: " + filtroFormatoSalida);
%>

<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <title>Seleccion de Empleados</title>
  <!-- Materialize CSS y Google Icons -->
  <!-- Materialize CSS y Google Icons -->
  <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet" />
  <link rel="stylesheet" href="https://cdn.datatables.net/v/bs4/dt-1.13.4/datatables.min.css" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  
  <!-- Materialize CSS -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/css/materialize.min.css">
    <!-- Select2 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <style>
    .table-container {
        width: 90%;
        max-width: 90%;
        margin-left: auto;
        margin-right: auto;
        /* Opcional: para evitar que la tabla se desborde */
        overflow-x: auto;
        display: block; /* para que margin auto funcione */
      }

    /* Sobrescribir el ancho máximo y margen de container */
    .container {
        max-width: 1200px !important; /* Cambia el ancho máximo */
        margin-left: 20px !important; /* Ajusta margen izquierdo */
        margin-right: 10px !important; /* Ajusta margen derecho */
        padding-left: 15px !important; /* Ajusta padding si quieres */
        padding-right: 15px !important;
    }
    
        body .container {
            width: 95%;
        }


    .input-field { 
        margin-bottom: 10px; 
    }
    
    
	
	h5.mi-titulo-personalizado {
            color: #3649d5;
            font-weight: 450;
            font-size: 1.8rem;
            text-transform: uppercase;
            letter-spacing: 1px;
        }
          
        /* Select visible */
        select {
          height: 32px !important;
          font-size: 0.85rem !important;
          padding: 4px 8px !important;
        }

        /* Opciones desplegadas */
        ul.dropdown-content.select-dropdown li > span {
          font-size: 0.85rem !important;
          padding: 1px 6px !important;
          min-height: auto !important;
          line-height: 1.2em !important;
        }

        /* Dropdown container */
        ul.dropdown-content.select-dropdown {
          max-height: 200px !important;
          font-size: 0.85rem !important;
        }
        
        .dropdown-content li {
            margin: 0 !important;
            line-height: 1.2 !important;
            height: auto !important;
            padding: 4px 10px !important;
          }
    
        tr {
            height: 50%; /* O un valor fijo en píxeles, por ejemplo 20px */
            line-height: 1em; /* Ajusta la altura de línea para que el contenido no expanda la fila */
            padding: 2px 5px; /* Reduce el padding para disminuir la altura total */
        }        
          
  </style>
</head>
<body>
  <div class="container">
    <h5 class="mi-titulo-personalizado">Reportes&nbsp;/&nbsp;Fiscalizaci&oacute;n/&nbsp;Selecci&oacute;n empleados</h5>
    <!-- Formulario de bÃºsqueda optimizado con grid -->
    <form id="searchForm"
          id="searchForm" 
              method="POST" 
              action="<%=request.getContextPath()%>/fiscaliza/BuscarEmpleadosServlet?action=list" autocomplete="off">
            <div class="row">
              <div class="input-field col s12 m6 l3">
                <input id="nombre" name="nombre" type="text" />
                <label for="nombre">Nombre empleado</label>
              </div>
              <div class="input-field col s12 m6 l3">
                <input id="run" name="run" type="text" />
                <label for="run">RUN empleado</label>
              </div>
                <!--
              <div class="input-field col s12 m6 l3">
                <input type="text" class="datepicker" id="desde" name="desde" placeholder="Fecha inicio" autocomplete="off" />
                <label for="fechaInicio" class="active">Fecha inicio</label>
              </div>
              <div class="input-field col s12 m6 l3">
                <input type="text" class="datepicker" id="hasta" name="hasta" placeholder="Fecha fin" autocomplete="off" />
                <label for="fechaFin" class="active">Fecha fin</label>
              </div>  
                -->

      </div>
      <div class="row">
        <div class="col s12 m6 l3">
          <select id="centroCostoId" name="centroCostoId" class="browser-default">
            <option value="-1" disabled selected>Seleccione centro de costo</option>
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
          <label>Centro de costo</label>
        </div>
        <div class="col s12 m6 l3">
          <select id="turnoId" name="turnoId" class="browser-default">
            <option value="-1" disabled selected>Seleccione turno</option>
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
          <label>Turno</label>
        </div>
        <div class="col s12 m6 l3">
          <select id="periodo" name="periodo" class="browser-default">
            <option value="-1" disabled selected>Seleccione per&iacute;­odo</option>
            <option value="semana">&Uacute;ltima semana</option>
            <option value="quincena">&Uacute;ltima quincena</option>
            <option value="mes">Mes anterior</option>
            <option value="anio">A&ntilde;o anterior</option>
          </select>
          <label>Per&iacute;­odo</label>
        </div>
        <div class="col s12 m6 l3">
          <select id="cargoId" name="cargoId" required class="browser-default">
            <option value="-1" disabled selected>Seleccione Cargo</option>
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
          <label>Cargo</label>
        </div>
      </div>

            <div class="row">
                <!--
                <div class="input-field col s12 m6 l3">
                    <select id="tipoReporte" name="tipoReporte">
                      <option value="asistencia" selected>Asistencia</option>
                      <option value="jornada_diaria">Jornada diaria</option>
                      <option value="domingos_festivos">Días domingo y/o días festivos</option>
                      <option value="modificaciones_turnos">Modificaciones y/o alteraciones de turnos</option>
                      <option value="diario">Diario</option>
                      <option value="incidentes_tecnicos">Incidentes técnicos</option>

                    </select>
                    <label>Tipo de reporte</label>
                </div>
                -->
                <!-- 
                <div class="input-field col s12 m6 l3">
                    <select id="formatoSalida" name="formatoSalida">
                      <option value="PDF" selected>PDF</option>
                      <option value="XLSX">EXCEL</option>
                      <option value="DOCX">WORD</option>
                    </select>
                    <label>Formato de salida</label>
                </div>
                -->
            </div>

      <div class="row">
        <div class="input-field col s12 right-align">
          <button class="btn waves-effect waves-light" type="submit">
            Buscar <i class="material-icons right">search</i>
          </button>
        </div>
      </div>
</form>

   <!-- Modal con campos a enviar cuando clic en boton 'Generar Reporte' -->
<div id="modalFiltros" class="modal">
  <div class="modal-content">
    <h4>Filtros de Reporte</h4>
    <div class="row">
      <form class="col s12" id="modalForm">
        <div class="row">
          <div class="input-field col s6">
            <input id="fecha_inicio_modal" name="fecha_inicio_modal" type="date" class="validate">
            <label for="fecha_inicio_modal" class="active">Fecha inicio</label>
          </div>
          <div class="input-field col s6">
            <input id="fecha_fin_modal" name="fecha_fin_modal" type="date" class="validate">
            <label for="fecha_fin_modal" class="active">Fecha fin</label>
          </div>
        </div>
        <div class="row">
          <div class="col s6">
            <select id="tipo_reporte_modal" name="tipo_reporte_modal" class="browser-default">
                <option value="asistencia" selected>Asistencia</option>
                <option value="jornada_diaria">Jornada diaria</option>
                <option value="domingos_festivos">Días domingo y/o días festivos</option>
                <option value="modificaciones_turnos">Modificaciones y/o alteraciones de turnos</option>
                <option value="diario">Diario</option>
                <option value="incidentes_tecnicos">Incidentes técnicos</option>
            </select>
            <label for="tipo_reporte_modal">Tipo de reporte</label>
          </div>
          <div class="col s6">
            <select id="formato_salida_modal" name="formato_salida_modal" class="browser-default">
                <option value="PDF" selected>PDF</option>
                <option value="XLSX">EXCEL</option>
                <option value="DOCX">WORD</option>
            </select>
            <label for="formato_salida_modal">Formato de salida</label>
          </div>
        </div>
        <div class="modal-footer">
          <a href="#!" class="modal-close waves-effect waves-green btn-flat">Cancelar</a>
          <button type="submit" class="waves-effect waves-light btn">Generar Reporte</button>
        </div>
      </form>
    </div>
  </div>
</div>             
                
                
    <!-- Tabla de resultados -->
    <div class="table-container">
        <form id="buildReportForm"
          id="buildReportForm" 
              method="POST" 
              action="<%=request.getContextPath()%>/fiscaliza/BuscarEmpleadosServlet?action=launchReport" autocomplete="off">
      <table class="striped highlight responsive-table" id="tablaEmpleados" style="width:100%">
          <input type="hidden" name="reportType" id="reportType">
          <input type="hidden" name="startDate" id="startDate">
          <input type="hidden" name="endDate" id="endDate">
          <input type="hidden" name="outputFormat" id="outputFormat">
          
        <thead>
          <tr>
            <th>RUT</th>
            <th>Nombre</th>
            <th>Empresa</th>
            <th>Centro de costo</th>
            <th>Estado</th>
            <th>Cargo</th>
            <th>Seleccionar</th>
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
                
            %>
            
                <tr>
                  <td><%=run%></td>
                  <td><%=nombre%></td>
                  <td><%=empresaNombre%></td>
                  <td><%=cencoNombre%></td>
                  <td><%=estadoLabel%></td>
                  <td><%=cargo%></td>
                  <td>
                    <label>
                      <input type="checkbox" 
                             class="filled-in empleadosSeleccionados"
                             id="empleadosSeleccionados" 
                             name="empleadosSeleccionados" value="<%=run%>" />
                      <span></span>
                    </label>
                  </td>
                </tr>
            <%}%>
          
        </tbody>
      </table>
      </form>      
    </div>

    <!-- Boton para mostrar modal con los filtros para generar el reporte -->
    <div class="right-align" style="margin-top: 20px;">
      <button class="btn waves-effect waves-light" id="botonGenerarReporte" type="button">
        Generar Reporte <i class="material-icons right">send</i>
      </button>
    </div>
  </div>

        <!-- Modal Structure -->
        <div id="modalObligatorio" class="modal">
          <div class="modal-content">
            <h4>Advertencia</h4>
            <p>Es obligatorio ingresar al menos un parámetro para la búsqueda de empleados.</p>
          </div>
          <div class="modal-footer">
            <a href="#!" class="modal-close waves-effect waves-green btn-flat">Aceptar</a>
          </div>
        </div>
            
  <!-- Scripts -->
  <!-- jQuery (requerido por Select2) -->
	<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
	 <!-- Materialize JS -->
	<script src="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/js/materialize.min.js"></script>
	<!-- Select2 JS -->
	<script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
	<script src="https://cdn.datatables.net/v/bs4/dt-1.13.4/datatables.min.js"></script>
  <script>
    $(document).ready(function () {
        
        document.addEventListener('DOMContentLoaded', function() {
            var elems = document.querySelectorAll('.modal');
            var instances = M.Modal.init(elems);
        });

      //$('select').formSelect();
      
      $('.datepicker').datepicker({
        format: 'yyyy-mm-dd',
        maxDate: new Date(),
        minDate: new Date(new Date().setFullYear(new Date().getFullYear() - 5)),
        yearRange: 5,
      });

        //seteo de los input de texto en el formulario de busqueda     
        $('input[name="nombre"]').val('<%=filtroNombre%>');
        $('input[name="run"]').val('<%=filtroRunEmpleado%>');
        $('input[name="desde"]').val('<%=filtroDesde%>');
        $('input[name="hasta"]').val('<%=filtroHasta%>');

        //seteo de los input del tipo Select en el formulario de busqueda
        $('select[name="centroCostoId"]').val('<%=filtroCencoId%>');
        $('select[name="turnoId"]').val('<%=filtroTurnoId%>');
        $('select[name="cargoId"]').val('<%=filtroCargoId%>');
        $('select[name="tipoReporte"]').val('<%=filtroTipoReporte%>');
        $('select[name="formatoSalida"]').val('<%=filtroFormatoSalida%>');

      $('#tablaEmpleados').DataTable({
        paging: false,
        pageLength: 5,
        lengthChange: false,
        ordering: true,
        order: [[1, 'asc']],
        language: {
			"decimal": "",
			"emptyTable": "No hay informaciÃ³n",
			"info": "Mostrando _START_ a _END_ de _TOTAL_ entradas",
			"infoEmpty": "Mostrando 0 a 0 de 0 entradas",
			"infoFiltered": "(filtrado de _MAX_ entradas totales)",
			"lengthMenu": "Mostrar _MENU_ entradas",
			"loadingRecords": "Cargando...",
			"processing": "Procesando...",
			"search": "Buscar:",
			"zeroRecords": "No se encontraron resultados",
			"paginate": {
			  "first": "Primero",
			  "last": "Ãšltimo",
			  "next": "Siguiente",
			  "previous": "Anterior"
			}
		  },
        columnDefs: [
          { orderable: false, targets: 6 },
        ],
        responsive: true,
      });

        // Inicializar el modal
        var modals = document.querySelectorAll('.modal');
        M.Modal.init(modals);

        // Inicializar los selects
        var selects = document.querySelectorAll('select');
        M.FormSelect.init(selects);
                  
	//submit del formulario en el modal antes de generar el reporte
        document.getElementById('modalForm').addEventListener('submit', function(e) {
		  e.preventDefault();

		  // Obtener empleados seleccionados
		  const empleados = [];
		  document.querySelectorAll('.empleadosSeleccionados:checked').forEach(function(checkbox) {
			empleados.push(checkbox.value);
		  });

		  // Obtener los campos del modal
		  const fecha_inicio = document.getElementById('fecha_inicio_modal').value;
		  const fecha_fin = document.getElementById('fecha_fin_modal').value;
		  const tipo_reporte = document.getElementById('tipo_reporte_modal').value;
		  const formato_salida = document.getElementById('formato_salida_modal').value;

                    // Construir objeto de datos
                    const data = {
                        empleados,
                        fecha_inicio,
                        fecha_fin,
                        tipo_reporte,
                        formato_salida
                    };

                    console.log('Datos a enviar:', data);

		  // Enviar al backend (AJAX ejemplo con fetch)
		  fetch('<%=request.getContextPath()%>/fiscaliza/BuscarEmpleadosServlet?action=launchReport', {
                        method: 'POST',
                        headers: {
                          'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(data)
                      })
                      .then(response => {
                        if (!response.ok) {
                          M.toast({html: 'Error al enviar los datos'});
                          throw new Error('Error en la respuesta del servidor');
                        }
                        // Esperar el blob (archivo binario)
                        return response.blob();
                      })
                      .then(blob => {
                        // Crear un link temporal para descargar el archivo
                        const url = window.URL.createObjectURL(blob);
                        const a = document.createElement('a');
                        a.href = url;

                        // Nombre sugerido para el archivo descargado (puedes cambiarlo)
                        a.download = 'reporte.zip';

                        // Agregar el link al DOM, hacer click y luego eliminarlo
                        document.body.appendChild(a);
                        a.click();
                        a.remove();

                        // Liberar el objeto URL
                        window.URL.revokeObjectURL(url);

                        // Cerrar modal y mostrar éxito
                        var modal = M.Modal.getInstance(document.getElementById('modalFiltros'));
                        modal.close();
                        M.toast({html: 'Archivo descargado correctamente'});
                      })
                      .catch(error => {
                        console.error('Error:', error);
                      });
                      
                      
                      
                      
		});
		
        document.getElementById('botonGenerarReporte').addEventListener('click', function(e) {
          e.preventDefault();
          var modal = M.Modal.getInstance(document.getElementById('modalFiltros'));
          modal.open();
        });

      $('#searchForm').submit(function (e) {
        //e.preventDefault();
        // Valores de los campos de busqueda
        var nombre = document.getElementById('nombre').value.trim();
        var run = document.getElementById('run').value.trim();
        var desde = document.getElementById('desde').value.trim();
        var hasta = document.getElementById('hasta').value.trim();

        var centroCostoId = document.getElementById('centroCostoId').value;
        var turnoId = document.getElementById('turnoId').value;
        var cargoId = document.getElementById('cargoId').value;
        //var tipoReporte = document.getElementById('tipoReporte').value;
        
        // Verifica si todos los campos están vacíos o sin seleccionar
        if (nombre === "" &&
            run === "" &&
            desde === "" &&
            hasta === "" &&
            centroCostoId === "-1" &&
            turnoId === "-1" &&
            cargoId === "-1" 
        ) {
            event.preventDefault(); // Detiene el envío del formulario
            mostrarModalObligatorio(); // Muestra el modal de advertencia
            return false;
        }
        // Si al menos uno tiene valor, permite el envío
        
      });
      
        // Función para abrir el modal
        function mostrarModalObligatorio() {
          var modalElem = document.getElementById('modalObligatorio');
          var modalInstance = M.Modal.getInstance(modalElem);
          if (modalInstance) {
            modalInstance.open();
          } else {
            // En caso de que no exista la instancia, inicialízala y abre
            modalInstance = M.Modal.init(modalElem);
            modalInstance.open();
          }
        }

        $('select.browser-default').select2({
            width: 'resolve',
            placeholder: "Seleccione una opción",
            allowClear: true
        });

    });
  </script>
</body>
</html>
