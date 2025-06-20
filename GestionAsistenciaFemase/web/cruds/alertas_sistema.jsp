
<%@page import="cl.femase.gestionweb.vo.UsuarioVO"%>
<%@page import="cl.femase.gestionweb.vo.AlertaSistemaVO"%>
<%@page import="cl.femase.gestionweb.common.Constantes"%>
<%@page import="cl.femase.gestionweb.vo.EmpresaVO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>

<%
   
   String mainTitle = "Alertas Sistema";
   String fileTitle = "alertas_sistema"; 
   String servletName = "AlertasSistemaCRUD";
   UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
   String empresaId = userConnected.getEmpresaId();
    
   List<AlertaSistemaVO> list = (List<AlertaSistemaVO>)request.getAttribute("lista");
   if (list == null) list = new ArrayList<>();
   //List<EmpresaVO> empresas = (List<EmpresaVO>)session.getAttribute("empresas");
   
   	//filtros de busqueda
    //String empresaId = (String)request.getAttribute("filtroEmpresaId");
    if (empresaId == null) empresaId = "-1";
    
%>

<!DOCTYPE html>
<html>
<head>
    <title>CRUD Alertas Sistema</title>
    
    <!-- Importar Google Icon Font -->
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <!-- Importar Materialize CSS -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/css/materialize.min.css">
  
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    
    <style>
        .modal { width: 80%; }
        .modal .modal-content { padding: 20px; }
        
        .table {
            width: 100%; /* Ocupa el 80% del contenedor */
            margin: auto; /* Centra la tabla */
        }
    </style>
</head>
<body>

    <div class="container">
        <h5>Alertas de Sistema</h5>

        <div class="row">
            <form id="searchForm" class="col s12" method="POST" action="<%=request.getContextPath()%>/<%=servletName%>?action=list" target="_self">
                <div class="row">
                    <div class="input-field col s6">
                        <input id="titulo" name="titulo" type="text" class="validate">
                        <label for="titulo">Título</label>
                    </div>
                    <div class="input-field col s3">
                        <input id="fecha" name="fecha" type="date" class="validate">
                        <label for="fecha">Fecha</label>
                    </div>
                    
                </div>
                <button class="btn waves-effect waves-light" type="submit">Buscar
                    <i class="material-icons right">search</i>
                </button>
                <a class="waves-effect waves-light btn modal-trigger" href="#createModal">Nuevo Registro</a>
            </form>
        </div>



        <!--<a class="waves-effect waves-light btn" href="#csv" id="exportarCSV">Exportar a CSV</a>-->

        <table class="striped responsive-table" id="miTabla">
            <thead>
                <tr>
                    <th data-field="id">ID <span id="sort-id"></span></th>
                    <th data-field="titulo">Titulo <span id="sort-titulo"></span></th>
                    <th data-field="mensaje">Mensaje <span id="sort-mensaje"></span></th>
                    <th data-field="tipo">Tipo<span id="sort-tipo"></span></th>
                    <th data-field="desde">Desde<span id="sort-desde"></span></th>
                    <th data-field="hasta">Hasta<span id="sort-hasta"></span></th>
                    <th data-field="creadoPor">Creado por<span id="sort-creadoPor"></span></th>
                    <th data-field="modificadoPor">Modificado por<span id="sort-modificadoPor"></span></th>
                    <th data-field="createdAt">Created At<span id="sort-createdAt"></span></th>
                    <th data-field="updatedAt">Updated At<span id="sort-updatedAt"></span></th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
		
                <%
                    Iterator<AlertaSistemaVO> iter = list.iterator();
                    AlertaSistemaVO alerta = null;
                    while (iter.hasNext()) {
                        alerta = iter.next();
                %>
                    <tr>
                        <td><%=alerta.getIdAlerta()%></td>
                        <td><%=alerta.getTitulo()%></td>
                        <td><%=alerta.getMensaje()%></td>
                        <td><%=alerta.getTipo()%></td>
                        <td><%=alerta.getFechaHoraDesde()%></td>
                        <td><%=alerta.getFechaHoraHasta()%></td>
                        <td><%=alerta.getCreadoPor()%></td>
                        <td><%=alerta.getModificadoPor()%></td>
                        <td><%=alerta.getCreatedAt()%></td>
                        <td><%=alerta.getUpdatedAt()%></td>
                        <td>
                            <a href="#editModal" class="modal-trigger"><i class="material-icons">edit</i></a> 
                            <a href="<%=request.getContextPath()%>/<%=servletName%>?action=delete?idDelete=<%=alerta.getIdAlerta()%>" class="modal-trigger"><i class="material-icons">delete</i></a>
                        </td>
                    </tr>
                <%}%>
                		
                </tbody>
        </table>

        <ul class="pagination">
            </ul>

        <div id="editModal" class="modal">
            <form id="createForm" class="col s12" method="POST" action="<%=request.getContextPath()%>/<%=servletName%>?action=update">
            <div class="modal-content">
                <h6>Modificar Registro</h6>
                
                <!-- Fila 1: 3 campos -->
                <div class="row">
                    <input type="hidden" id="editId" name="editId">
                    <div class="input-field col s4">
                        <textarea id="editTitulo" name="editTitulo" class="materialize-textarea"></textarea>
                        <label for="editTitulo">Título</label>
                    </div>
                    <div class="input-field col s4">
                        <textarea id="editMensaje" name="editMensaje" class="materialize-textarea"></textarea>
                        <label for="editMensaje">Mensaje</label>
                    </div>

                    <div class="input-field col s4">
                        <select id="editTipo" name="editTipo">
                            <option value="" selected>Seleccione una opción</option>
                            <option value="mantenimiento">Mantenimiento</option>
                            <option value="aviso">Aviso</option>
                            <option value="importante">Importante</option>
                        </select>
                        <label for="editTipo">Tipo</label>
                    </div>
                </div>
                <!-- Fila 2: 3 campos -->
                <div class="row">
                    <div class="input-field col s4">
                        <input id="editDesdeFecha" name="editDesdeFecha" type="date" class="datepicker">
                        <label for="editDesdeFecha">Desde Fecha</label>
                    </div>
                    <div class="input-field col s4">
                        <select id="editDesdeHora" name="editDesdeHora">
                            <option value="" disabled selected>Seleccione una opción</option>
                            <option value="07:00:00">07:00</option>
                            <option value="08:00:00">08:00</option>
                            <option value="09:00:00">09:00</option>
                            <option value="10:00:00">10:00</option>
                            <option value="11:00:00">11:00</option>
                            <option value="12:00:00">12:00</option>
                            <option value="13:00:00">13:00</option>
                            <option value="14:00:00">14:00</option>
                            <option value="15:00:00">15:00</option>
                            <option value="16:00:00">16:00</option>
                            <option value="17:00:00">17:00</option>
                            <option value="18:00:00">18:00</option>
                            <option value="19:00:00">19:00</option>
                            <option value="20:00:00">20:00</option>
                            <option value="21:00:00">21:00</option>
                            <option value="22:00:00">22:00</option>
                            <option value="23:00:00">23:00</option>
                        </select>
                        <label for="editDesdeHora">Desde Hora</label>
                    </div>
                    <div class="input-field col s4">
                        &nbsp;    
                    </div>
                </div>
                
                <div class="row">
                          <div class="input-field col s4">
                        <input id="editHastaFecha" name="editHastaFecha" type="date" class="datepicker">
                        <label for="editHastaFecha">Hasta Fecha</label>
                    </div>
                    <div class="input-field col s4">
                        <select id="editHastaHora" name="editHastaHora">
                            <option value="" disabled selected>Seleccione una opción</option>
                            <option value="07:00:00">07:00</option>
                            <option value="08:00:00">08:00</option>
                            <option value="09:00:00">09:00</option>
                            <option value="10:00:00">10:00</option>
                            <option value="11:00:00">11:00</option>
                            <option value="12:00:00">12:00</option>
                            <option value="13:00:00">13:00</option>
                            <option value="14:00:00">14:00</option>
                            <option value="15:00:00">15:00</option>
                            <option value="16:00:00">16:00</option>
                            <option value="17:00:00">17:00</option>
                            <option value="18:00:00">18:00</option>
                            <option value="19:00:00">19:00</option>
                            <option value="20:00:00">20:00</option>
                            <option value="21:00:00">21:00</option>
                            <option value="22:00:00">22:00</option>
                            <option value="23:00:00">23:00</option>
                        </select>
                        <label for="editHastaHora">Hasta Hora</label>
                    </div>
                    <div class="input-field col s4">
                        &nbsp;    
                    </div>
                        
                </div><!-- FILA -->
                
                
            </div><!-- modal content -->
            
            <div class="modal-footer">
                <a href="#!" class="modal-close waves-effect waves-green btn-flat">Cancelar</a>
                <button type="submit" class="waves-effect waves-light btn">Guardar</button>
            </div>
            </form>
        </div>

        <div id="createModal" class="modal">
            <form id="createForm" class="col s12" method="POST" action="<%=request.getContextPath()%>/<%=servletName%>?action=create">
            <div class="modal-content">
                <h6>Nuevo Registro</h6>
                
                <!-- Fila 1: 3 campos -->
                <div class="row">
                    <div class="input-field col s4">
                        <textarea id="createTitulo" name="createTitulo" class="materialize-textarea"></textarea>
                        <label for="createTitulo">Título</label>
                    </div>
                    <div class="input-field col s4">
                        <textarea id="createMensaje" name="createMensaje" class="materialize-textarea"></textarea>
                        <label for="createMensaje">Mensaje</label>
                    </div>

                    <div class="input-field col s4">
                        <select id="createTipo" name="createTipo">
                            <option value="" selected>Seleccione una opción</option>
                            <option value="mantenimiento">Mantenimiento</option>
                            <option value="aviso">Aviso</option>
                            <option value="importante">Importante</option>
                        </select>
                        <label for="createTipo">Tipo</label>
                    </div>
                </div>
                <!-- Fila 2: 3 campos -->
                <div class="row">
                    <div class="input-field col s4">
                        <input id="createDesdeFecha" name="createDesdeFecha" type="date" class="datepicker">
                        <label for="createDesdeFecha">Desde Fecha</label>
                    </div>
                    <div class="input-field col s4">
                        <select id="createDesdeHora" name="createDesdeHora">
                            <option value="" disabled selected>Seleccione una opción</option>
                            <option value="07:00:00">07:00</option>
                            <option value="08:00:00">08:00</option>
                            <option value="09:00:00">09:00</option>
                            <option value="10:00:00">10:00</option>
                            <option value="11:00:00">11:00</option>
                            <option value="12:00:00">12:00</option>
                            <option value="13:00:00">13:00</option>
                            <option value="14:00:00">14:00</option>
                            <option value="15:00:00">15:00</option>
                            <option value="16:00:00">16:00</option>
                            <option value="17:00:00">17:00</option>
                            <option value="18:00:00">18:00</option>
                            <option value="19:00:00">19:00</option>
                            <option value="20:00:00">20:00</option>
                            <option value="21:00:00">21:00</option>
                            <option value="22:00:00">22:00</option>
                            <option value="23:00:00">23:00</option>
                        </select>
                        <label for="createDesdeHora">Desde Hora</label>
                    </div>
                    <div class="input-field col s4">
                        &nbsp;    
                    </div>
                </div>
                
                <div class="row">
                          <div class="input-field col s4">
                        <input id="createHastaFecha" name="createHastaFecha" type="date" class="datepicker">
                        <label for="createHastaFecha">Hasta Fecha</label>
                    </div>
                    <div class="input-field col s4">
                        <select id="createHastaHora" name="createHastaHora">
                            <option value="" disabled selected>Seleccione una opción</option>
                            <option value="07:00:00">07:00</option>
                            <option value="08:00:00">08:00</option>
                            <option value="09:00:00">09:00</option>
                            <option value="10:00:00">10:00</option>
                            <option value="11:00:00">11:00</option>
                            <option value="12:00:00">12:00</option>
                            <option value="13:00:00">13:00</option>
                            <option value="14:00:00">14:00</option>
                            <option value="15:00:00">15:00</option>
                            <option value="16:00:00">16:00</option>
                            <option value="17:00:00">17:00</option>
                            <option value="18:00:00">18:00</option>
                            <option value="19:00:00">19:00</option>
                            <option value="20:00:00">20:00</option>
                            <option value="21:00:00">21:00</option>
                            <option value="22:00:00">22:00</option>
                            <option value="23:00:00">23:00</option>
                        </select>
                        <label for="createHastaHora">Hasta Hora</label>
                    </div>
                    <div class="input-field col s4">
                        &nbsp;    
                    </div>
                        
                </div><!-- FILA -->
                
                
            </div><!-- modal content -->
            <div class="modal-footer">
                <a href="#!" class="modal-close waves-effect waves-green btn-flat">Cancelar</a>
               <button type="submit" class="waves-effect waves-light btn">Guardar</button>
            </div>
            </form>
        </div>
    </div>

    <!-- Importar Materialize JS y jQuery -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/js/materialize.min.js"></script>
    
    <!-- Inicializar el select -->
  <script>
    $(document).ready(function() {
      $('select').formSelect();
    });
  </script>
  
    <script>
        
        document.getElementById('createModal').addEventListener('submit', function(event) {
            // Aquí puedes agregar cualquier validación o acción antes de enviar
            // Si no quieres que se envíe, puedes usar event.preventDefault();
            let titulo = document.getElementById('createTitulo').value;
            let mensaje = document.getElementById('createMensaje').value;
            let tipo = document.getElementById('createTipo').value;
            let desdeFecha = document.getElementById('createDesdeFecha').value;
            let desdeHora = document.getElementById('createDesdeHora').value;
            let hastaFecha = document.getElementById('createHastaFecha').value;
            let hastaHora = document.getElementById('createHastaHora').value;
            //let estado = document.getElementById('createEstado').value;
            
            if (titulo.trim() === '') {
                Swal.fire({
                  title: 'Error!',
                  text: 'El campo Título es obligatorio.',
                  icon: 'error',
                  confirmButtonText: 'Ok'
                });
                event.preventDefault(); // Evita que el formulario se envíe
                return;
            }

            if (mensaje.trim() === '') {
                Swal.fire({
                  title: 'Error!',
                  text: 'El campo mensaje es obligatorio.',
                  icon: 'error',
                  confirmButtonText: 'Ok'
                });
              event.preventDefault(); // Evita que el formulario se envíe
              return;
            }
            
            if (tipo.trim() === '-1') {
                Swal.fire({
                  title: 'Error!',
                  text: 'El campo tipo es obligatorio.',
                  icon: 'error',
                  confirmButtonText: 'Ok'
                });
              event.preventDefault(); // Evita que el formulario se envíe
              return;
            }

            if (desdeFecha === '') {
              Swal.fire({
                  title: 'Error!',
                  text: 'El campo fecha es obligatorio.',
                  icon: 'error',
                  confirmButtonText: 'Ok'
                });
              event.preventDefault(); // Evita que el formulario se envíe
              return;
            }
            if (desdeHora === '') {
              Swal.fire({
                  title: 'Error!',
                  text: 'El campo hora es obligatorio.',
                  icon: 'error',
                  confirmButtonText: 'Ok'
                });
              event.preventDefault(); // Evita que el formulario se envíe
              return;
            }
            if (hastaFecha === '') {
              Swal.fire({
                  title: 'Error!',
                  text: 'El campo fecha es obligatorio.',
                  icon: 'error',
                  confirmButtonText: 'Ok'
                });
              event.preventDefault(); // Evita que el formulario se envíe
              return;
            }
            if (hastaHora === '') {
              Swal.fire({
                  title: 'Error!',
                  text: 'El campo hora es obligatorio.',
                  icon: 'error',
                  confirmButtonText: 'Ok'
                });
              event.preventDefault(); // Evita que el formulario se envíe
              return;
            }
            /*
            if (estado.trim() === '-1') {
                Swal.fire({
                  title: 'Error!',
                  text: 'El campo estado es obligatorio.',
                  icon: 'error',
                  confirmButtonText: 'Ok'
                });
              event.preventDefault(); // Evita que el formulario se envíe
              return;
            }
            */
        });
        
        document.getElementById('editModal').addEventListener('submit', function(event) {
            // Aquí puedes agregar cualquier validación o acción antes de enviar
            // Si no quieres que se envíe, puedes usar event.preventDefault();
            let titulo = document.getElementById('editTitulo').value;
            let mensaje = document.getElementById('editMensaje').value;
            let tipo = document.getElementById('editTipo').value;
            let desdeFecha = document.getElementById('editDesdeFecha').value;
            let desdeHora = document.getElementById('editDesdeHora').value;
            let hastaFecha = document.getElementById('editHastaFecha').value;
            let hastaHora = document.getElementById('editHastaHora').value;
            //let estado = document.getElementById('createEstado').value;
            
            if (titulo.trim() === '') {
                Swal.fire({
                  title: 'Error!',
                  text: 'El campo Título es obligatorio.',
                  icon: 'error',
                  confirmButtonText: 'Ok'
                });
                event.preventDefault(); // Evita que el formulario se envíe
                return;
            }

            if (mensaje.trim() === '') {
                Swal.fire({
                  title: 'Error!',
                  text: 'El campo mensaje es obligatorio.',
                  icon: 'error',
                  confirmButtonText: 'Ok'
                });
              event.preventDefault(); // Evita que el formulario se envíe
              return;
            }
            
            if (tipo.trim() === '-1') {
                Swal.fire({
                  title: 'Error!',
                  text: 'El campo tipo es obligatorio.',
                  icon: 'error',
                  confirmButtonText: 'Ok'
                });
              event.preventDefault(); // Evita que el formulario se envíe
              return;
            }

            if (desdeFecha === '') {
              Swal.fire({
                  title: 'Error!',
                  text: 'El campo fecha es obligatorio.',
                  icon: 'error',
                  confirmButtonText: 'Ok'
                });
              event.preventDefault(); // Evita que el formulario se envíe
              return;
            }
            if (desdeHora === '') {
              Swal.fire({
                  title: 'Error!',
                  text: 'El campo hora es obligatorio.',
                  icon: 'error',
                  confirmButtonText: 'Ok'
                });
              event.preventDefault(); // Evita que el formulario se envíe
              return;
            }
            if (hastaFecha === '') {
              Swal.fire({
                  title: 'Error!',
                  text: 'El campo fecha es obligatorio.',
                  icon: 'error',
                  confirmButtonText: 'Ok'
                });
              event.preventDefault(); // Evita que el formulario se envíe
              return;
            }
            if (hastaHora === '') {
              Swal.fire({
                  title: 'Error!',
                  text: 'El campo hora es obligatorio.',
                  icon: 'error',
                  confirmButtonText: 'Ok'
                });
              event.preventDefault(); // Evita que el formulario se envíe
              return;
            }
            /*
            if (estado.trim() === '-1') {
                Swal.fire({
                  title: 'Error!',
                  text: 'El campo estado es obligatorio.',
                  icon: 'error',
                  confirmButtonText: 'Ok'
                });
              event.preventDefault(); // Evita que el formulario se envíe
              return;
            }
            */
        });
        

        document.addEventListener('DOMContentLoaded', function() {
            var elems = document.querySelectorAll('.modal');
            var instances = M.Modal.init(elems);

            // Datos de ejemplo (ahora en el HTML)
            const tableData = Array.from(document.querySelectorAll('table tbody tr')).map(row => {
                const cells = Array.from(row.querySelectorAll('td'));
                
                return {
                    id: cells[0].textContent,
                    titulo: cells[1].textContent,
                    mensaje: cells[2].textContent,
                    tipo: cells[3].textContent,
                    desde: cells[4].textContent,
                    hasta: cells[5].textContent,
                    creadoPor: cells[6].textContent,
                    modificadoPor: cells[7].textContent,
                    createdAt: cells[8].textContent,
                    updatedAt: cells[9].textContent
                };
            });

            // Paginación y ordenamiento
            var currentPage = 1;
            var rowsPerPage = 5;
            var currentSort = { field: 'id', order: 'asc' };

            function displayList(arr, rowsPerPage, page) {
                const tableBody = document.querySelector('table tbody');
                tableBody.innerHTML = '';
                page--;
                const start = rowsPerPage * page;
                const end = start + rowsPerPage;
                const paginatedData = arr.slice(start, end);

                for (let i = 0; i < paginatedData.length; i++) {
                    const row = document.createElement('tr');
                    const item = paginatedData[i];
                    for (const key in item) {
                        const cell = document.createElement('td');
                        cell.textContent = item[key];
                        row.appendChild(cell);
                    }
                    const actionsCell = document.createElement('td');
                    //actionsCell.innerHTML = '<a href="#editModal" class="modal-trigger" data-id="' + item.id + '"><i class="material-icons">edit</i></a> <a href="#deleteModal" class="modal-trigger" data-id="' + item.id + '"><i class="material-icons">delete</i></a>';
                    actionsCell.innerHTML = '<a href="#editModal" class="modal-trigger" data-id="' + item.id + '"><i class="material-icons">edit</i></a> <a href="<%=request.getContextPath()%>/<%=servletName%>?action=delete&idDelete=' + item.id + '" class="modal-trigger" data-id="' + item.id + '"><i class="material-icons">delete</i></a>';
                    row.appendChild(actionsCell);
                    tableBody.appendChild(row);
                }
            }

            function displayPagination(arr, rowsPerPage) {
                const pagination = document.querySelector('.pagination');
                pagination.innerHTML = '';
                const pagesCount = Math.ceil(arr.length / rowsPerPage);
                for (let i = 1; i <= pagesCount; i++) {
                    const li = document.createElement('li');
                    li.classList.add('waves-effect');
                    if (i === currentPage) {
                        li.classList.add('active');
                    }
                    li.innerHTML = `<a href="#">${i}</a>`;
                    li.addEventListener('click', () => {
                        currentPage = i;
                        displayList(tableData, rowsPerPage, currentPage);
                        displayPagination(tableData, rowsPerPage);
                    });
                    pagination.appendChild(li);
                }
            }

            function sortTable(field) {
                if (currentSort.field === field) {
                    currentSort.order = currentSort.order === 'asc' ? 'desc' : 'asc';
                } else {
                    currentSort.field = field;
                    currentSort.order = 'asc';
                }

                tableData.sort((a, b) => {
                    if (a[field] < b[field]) {
                        return currentSort.order === 'asc' ? -1 : 1;
                    }
                    if (a[field] > b[field]) {
                        return currentSort.order === 'asc' ? 1 : -1;
                    }
                    return 0;
                });

                displayList(tableData, rowsPerPage, currentPage);
                updateSortIcons();
            }

            function updateSortIcons() {
                document.querySelectorAll('th[data-field]').forEach(th => {
                    const field = th.dataset.field;
                    const sortIcon = th.querySelector('span');

                    if (currentSort.field === field) {
                        sortIcon.innerHTML = currentSort.order === 'asc' ? '&#9650;' : '&#9660;'; // Triángulos hacia arriba y abajo
                    } else {
                        sortIcon.innerHTML = '';
                    }
                });
            }

            // Inicializar paginación y ordenamiento
            displayList(tableData, rowsPerPage, currentPage);
            displayPagination(tableData, rowsPerPage);
            updateSortIcons();

            // Eventos de ordenamiento
            document.querySelectorAll('th[data-field]').forEach(th => {
                th.addEventListener('click', () => {
                    sortTable(th.dataset.field);
                });
            });

            // Evento para cargar datos en el modal de edición
            document.addEventListener('click', function(event) {
                if (event.target.closest('a[href="#editModal"]')) {
                    const editButton = event.target.closest('a[href="#editModal"]');
                    const recordId = editButton.dataset.id;
                    const record = tableData.find(item => item.id === recordId);
                    if (record) {
                        const strdesde = record.desde.split(' ');
                        const strhasta = record.hasta.split(' ');
                        //alert('desde.fecha: '+strdesde[0] + ', desde.hora: '+strdesde[1]);
                        document.getElementById('editId').value = record.id;
                        document.getElementById('editTitulo').value = record.titulo;
                        document.getElementById('editMensaje').value = record.mensaje;
                        document.getElementById('editTipo').value = record.tipo;
                        $('#editTipo').val(record.tipo);
                        $('#editTipo').formSelect(); 
                                                
                        document.getElementById('editDesdeFecha').value = strdesde[0];
                        document.getElementById('editDesdeHora').value = strdesde[1];
                        document.getElementById('editHastaFecha').value = strhasta[0];
                        document.getElementById('editHastaHora').value = strhasta[1];
                        $('#editDesdeFecha').val(strdesde[0]);
                        $('#editDesdeHora').val(strdesde[1]);
                        $('#editDesdeHora').formSelect();
                        
                        $('#editHastaFecha').val(strhasta[0]);
                        $('#editHastaHora').val(strhasta[1]);
                        $('#editHastaHora').formSelect();

                        M.updateTextFields(); // Actualizar los labels de los campos
                    }
                }else if (event.target.closest('a[href="#deleteModal"]')) {
						
                            const deleteButton = event.target.closest('a[href="#deleteModal"]');
                            const recordId = deleteButton.dataset.id;
                            const record = tableData.find(item => item.id === recordId);
                            //alert('elminar registro id: ' + recordId);
                            if (record) {
                                alert('Eliminar registro id: ' + recordId);
                            }
                }else if (event.target.closest('a[href="#csv"]')) {
			//alert('exportar a CSV siii!!');			
                        exportarTablaACSV('miTabla', 'tabla.csv');    
                }
            });

            // Evento para guardar los datos editados
            document.getElementById('saveEdit').addEventListener('click', function() {
                // Aquí puedes agregar la lógica para guardar los datos editados
                // Puedes obtener los datos de los campos del formulario modal
                // y luego actualizar el registro correspondiente en la tabla de datos.
                // Luego puedes cerrar el modal y actualizar la tabla.
                //alert('modificar datos');
                console.log('Datos guardados');
            });
			

            function exportarTablaACSV(tablaID, nombreArchivo) {
                const tabla = document.getElementById(tablaID);
                let filas = Array.from(tabla.rows);

                // Convierte las filas de la tabla en texto CSV
                let csvContenido = filas.map(fila => {
                    let columnas = Array.from(fila.cells);
                    return columnas.map(columna => `"${columna.textContent}"`).join(',');
                }).join('\n');

                // Crea un archivo Blob con el contenido CSV
                let blob = new Blob([csvContenido], { type: 'text/csv' });

                // Crea un enlace temporal para descargar el archivo
                let enlace = document.createElement('a');
                enlace.href = URL.createObjectURL(blob);
                enlace.download = nombreArchivo;

                // Simula un clic en el enlace para iniciar la descarga
                enlace.click();
            }

        });
    </script>

</body>
</html>