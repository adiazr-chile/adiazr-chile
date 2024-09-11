
<%@page import="cl.femase.gestionweb.vo.VacacionesSaldoPeriodoVO"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="cl.femase.gestionweb.vo.UsuarioCentroCostoVO"%>
<%@page import="cl.femase.gestionweb.common.Constantes"%>
<%@page import="cl.femase.gestionweb.vo.EmpleadoVO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>

<%
    String mainTitle    = "Vacaciones por periodos";
    String fileTitle    = "vacaciones_saldo_periodo";
    String urlPattern   = "VacacionesSaldosPeriodosCRUD";
    LinkedHashMap<String, VacacionesSaldoPeriodoVO> hashPeriodos = 
        (LinkedHashMap<String, VacacionesSaldoPeriodoVO>)session.getAttribute("lista_CRUD_saldovacperiodos");
    if (hashPeriodos == null) hashPeriodos = new LinkedHashMap<>();
    //EmpleadosDAO daoEmpleados = n
    List<EmpleadoVO> listaEmpleados = (List<EmpleadoVO>)session.getAttribute("empleados");
            
    //filtros de busqueda
    //List<UsuarioCentroCostoVO> cencos   = (List<UsuarioCentroCostoVO>)session.getAttribute("cencos_empleado");
    
    //String filtroCenco = (String)session.getAttribute("filtroCencoVACP");
    String filtroRun = (String)session.getAttribute("filtroRunVACP");
    
    System.out.println("[crud.vacaciones_periodos]filtros en sesion: "
        + "filtroRun " + filtroRun);
    
    //if (filtroCenco == null) filtroCenco = "-1";
    if (filtroRun == null) filtroRun = "";
    
    /**
    *   Columnas para mostrar:
    *                               columna num
    *           Run                 1
                Inicio periodo      2
                Fin periodo         3
                Saldo VBA           4
                Estado Id (oculto)
                Estado              5
                Ult. actualizacion  6
        
    */
    //num columnas
    String columnas = "0,1,2,3,4,5,6";
%>

<!doctype html>
<html lang="es">
    <head>
	<meta charset="UTF-8">
         <meta name="viewport" content="width=device-width, initial-scale=1.0">
        
        <meta name="description" content="CRUD - <%=mainTitle%>" />
    <title><%=mainTitle%>-CRUD</title>
    
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.css">
    <!--<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.1.1/css/bootstrap.css">-->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.5.2/css/bootstrap.css">
    <link rel="stylesheet" href="https://cdn.datatables.net/1.10.19/css/dataTables.bootstrap4.min.css">
    <link rel="stylesheet" href="https://cdn.datatables.net/buttons/1.5.2/css/buttons.bootstrap4.min.css">
   
   	<!-- css para modal -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.0.0-beta.2/css/bootstrap.css">
   
   <script type="text/javascript"  src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
   <script type="text/javascript"  src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js"></script>
   <script type="text/javascript"  src="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.1.1/js/bootstrap.min.js"></script>
   <script type="text/javascript"  src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>
   <script type="text/javascript"  src="https://cdn.datatables.net/1.10.19/js/dataTables.bootstrap4.min.js"></script>
   <script type="text/javascript"  src="https://cdn.datatables.net/buttons/1.5.2/js/dataTables.buttons.min.js"></script>
   <script type="text/javascript"  src="https://cdn.datatables.net/buttons/1.5.2/js/buttons.bootstrap4.min.js"></script>
   <script type="text/javascript"  src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.1.3/jszip.min.js"></script>
   <script type="text/javascript"  src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.36/pdfmake.min.js"></script>
   <script type="text/javascript"  src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.36/vfs_fonts.js"></script>
   <script type="text/javascript"  src="https://cdn.datatables.net/buttons/1.5.2/js/buttons.html5.min.js"></script>
   <script type="text/javascript"  src="https://cdn.datatables.net/buttons/1.5.2/js/buttons.print.min.js"></script>
   <script type="text/javascript"  src="https://cdn.datatables.net/buttons/1.5.2/js/buttons.colVis.min.js"></script>
   
    <!-- include para modal -->
    <script type="text/javascript"  src="https://unpkg.com/popper.js"></script>
    <script type="text/javascript"  src="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.0.0-beta/js/bootstrap.min.js"></script>

    
    <!-- para datetime picker -->
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.9.0/css/bootstrap-datepicker.min.css">
    
    <!-- para datepicker -->    
    
    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.9.0/css/bootstrap-datepicker.min.css" rel="stylesheet"/>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.9.0/js/bootstrap-datepicker.min.js"></script>
    
    <script src="<%=request.getContextPath()%>/cruds/js/datatable_idioma.js"></script>
    <script src="<%=request.getContextPath()%>/cruds/js/datepicker_init.js"></script>
    <link href="<%=request.getContextPath()%>/cruds/css/datatable_view.css" rel="stylesheet"/>
        
    <script type="text/javascript">
        
    $(document).ready(function() {
        var table = $('#myTable').on( 'draw.dt', function () {
            $("#container").attr("id", "container");
            if ($("#loadercontainer").length) {
                $("#loadercontainer").css("display","none");
            }
            //$("#loadercontainer").css("display","none");
          } ).DataTable( {
                  "paging": true,
                    "lengthChange": true,
                    "searching": true,
                    "ordering": true,
                    "info": true,
                    "autoWidth": true,
                    "language": idioma,
                    "lengthMenu": [[5,10,20, -1],[5,10,50,"Mostrar Todo"]],
                    dom: 'Bfrt<"col-md-6 inline"i> <"col-md-6 inline"p>',
          "columnDefs": [
                { "width": "10%", "targets": 0 },
                { "width": "10%", "targets": 1 }, 
                { "width": "10%", "targets": 2 },
                { "width": "10%", "targets": 3 },
                { "width": "10%", "targets": 4 },
                { "width": "10%", "targets": 5 },
                { "width": "10%", "targets": 6 }
          ],
          buttons: {
              dom: {
                container:{
                  tag:'div',
                  className:'flexcontent'
                },
                buttonLiner: {
                  tag: null
                }
              },
          buttons: [
                {
                    extend:    'colvis',
                    text:      'Columnas visibles'
                    //className: 'btn btn-app export barras'
                },
                    {
                    extend:    'copyHtml5',
                    text:      '<i class="fa fa-clipboard" style="font-size:15px;"></i>Copiar',
                    title:'<%=fileTitle%>',
                    titleAttr: 'Copiar',
                    className: 'btn btn-app export barras',
                    exportOptions: {
                        columns: [ <%=columnas%>]
                    }
                },

                    {
                        extend:    'excelHtml5',
                        text:      '<i class="fa fa-file-excel-o" style="font-size:15px;"></i>Excel',
                        title:'<%=fileTitle%>',
                        titleAttr: 'Excel',
                        className: 'btn btn-app export excel',
                        exportOptions: {
                            columns: [<%=columnas%>]
                        }
                    },
                    {
                        extend:    'csvHtml5',
                        text:      '<i class="fa fa-file-text-o" style="font-size:15px;"></i>CSV',
                        title:'<%=fileTitle%>',
                        titleAttr: 'CSV',
                        className: 'btn btn-app export csv',
                        exportOptions: {
                            columns: [<%=columnas%>]
                        }
                    },
                    {
                        extend:    'print',
                        text:      '<i class="fa fa-print" style="font-size:15px;"></i>Imprimir',
                        title:'<%=fileTitle%>',
                        titleAttr: 'Imprimir',
                        className: 'btn btn-app export imprimir',
                        exportOptions: {
                            columns: [ <%=columnas%>]
                        }
                    },
                    {
                        extend:    'pageLength',
                        titleAttr: 'Registros a mostrar',
                        className: 'selectTable'
                    }
                ]
          
            }
        } );
        
        
        
        //Add row button
	/*$('.dt-add').each(function () {
            $(this).on('click', function(evt){
                document.location.href='<%=request.getContextPath()%>/jqueryform-empleado/form_crear_empleado.jsp';
            });
	});
        */
        
        // code to read selected table row cell data (values).
        //evento click en boton Select. Toma los valores de la fila seleccionada (columna por columna)        
        /*$("#myTable").on('click','.btnSelect',function(){
            // get the current row
            var currentRow=$(this).closest("tr");
            var numFicha= currentRow.find("td:eq(1)").text(); // get current row 2nd TD
            document.location.href='<%=request.getContextPath()%>/EmpleadosController?action=edit&rutEmpleado='+numFicha;
        });*/
        
         
    } );

    
    </script>
                        
    <style> 
        
        /* Modal styles */
        .modal.fade .modal-dialog {
            transition: transform .3s ease-out;
            transform: translate(0,50px);
        }
        .modal-dialog {
          position: fixed !important;
          bottom: 0 !important;
          left: 0% !important;
          right: 0% !important;
          margin-bottom: 0 !important;

        } 

        .modal.show .modal-dialog {
            transform: none;
        }

        .modal-content {
          border-bottom-left-radius: 0;
          border-bottom-right-radius: 0;

        }
            .modal .modal-dialog {
            max-width: 400px;
            }
            .modal .modal-header, .modal .modal-body, .modal .modal-footer {
            padding: 20px 30px;
            }
            .modal .modal-content {
            border-radius: 3px;
            }
            .modal .modal-footer {
            background: #ecf0f1;
            border-radius: 0 0 3px 3px;
            }
            .modal .modal-title {
            display: inline-block;
            }
            .modal .form-control {
            border-radius: 2px;
            box-shadow: none;
            border-color: #dddddd;
            }
            .modal textarea.form-control {
            resize: vertical;
            }
            .modal .btn {
            border-radius: 2px;
            min-width: 100px;
            }
            .modal form label {
            font-weight: normal;
            }
            
            body {
                margin: 4rem 0;
              }

              h4 {
                margin-bottom: 2rem;
                margin-top: 3rem;
              }

              .panel {
                border-radius: 0.3rem;
                padding: 1rem;
                margin-bottom: 1rem;
              }
              .panel.panel-blue {
                border: 1px solid #0087ff;
                background-color: #ddedff;
                color: #0087ff;
              }
              .panel.panel-yellow {
                border: 1px solid #ffbd00;
                background-color: #fef0b2;
                color: #ffbd00;
              }
              .panel.panel-pink {
                border: 1px solid #f84f7f;
                background-color: #fad2e1;
                color: #f84f7f;
              }
              .panel.panel-purple {
                border: 1px solid #7f51f4;
                background-color: #dfccff;
                color: #7f51f4;
              }
   
        /* Estilos para busqueda de empleado... */
        #searchInput {
            width: 100%;
            padding: 10px;
            margin-bottom: 10px;
        }
        #autocompleteList {
            list-style-type: none;
            margin: 0;
            padding: 0;
            border: 1px solid #ccc;
            max-height: 150px;
            overflow-y: auto;
            background-color: #fff;
            position: absolute;
            width: 100%;
            z-index: 1;
            display: none;
        }
        #autocompleteList li {
            padding: 10px;
            cursor: pointer;
        }
        #autocompleteList li:hover {
            background-color: #f0f0f0;
        } 
        /* Fin Estilos para busqueda de empleado... */      
    </style>                    
</head>
<body>
    <h3 class="titulo-main">Buscar empleado</h3>
    <form id="searchForm" name="searchForm" method="POST" action="<%=request.getContextPath()%>/<%=urlPattern%>?action=list" target="_self">
       <input type="text" id="searchInput" id="searchInput" placeholder="Ingresa el nombre del empleado...">
        <input type="hidden" name="filtroRun" id="filtroRun" value="filtroRun">
        
        <ul id="autocompleteList"></ul>
        <button type="submit">Buscar</button>
    </form>
    
  <!-- Inicio tabla con datos -->  
   <div class="container">
  

  <div class="row">
    
    <div class="col-12" > 
      <h3 class="titulo-tabla"><%=mainTitle%> </h3>
      
      
      <table id="myTable" class="table table-striped table-bordered" style="width:100%">
        <thead>
            <tr>
                <th>Run</th>
                <th>Inicio periodo</th>
                <th>Fin periodo</th>
                <th>Saldo VBA</th>
                <th style="display: none;">Estado Id</th>
                <th>Estado</th>
                <th>Ult. actualizacion</th>
            
                <%//if (!listaEmpleados.isEmpty()){%>
                    <!--<th style="text-align:center;width:100px;">Nuevo registro 
                        <button type="button" class="btn btn-success btn-xs dt-add">
                            <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
                        </button>
                    </th>-->
                <%//}%>
            </tr>
        </thead>
        <tbody>
            <%
        for (VacacionesSaldoPeriodoVO periodo : hashPeriodos.values()) {
            //String fullName = registro.getApePaterno()+ " " +registro.getApeMaterno()+ " " + registro.getNombres();
        %>
            
            <tr>
                <td><%= periodo.getRunEmpleado()%></td>
                <td><%= periodo.getFechaInicioPeriodo()%></td>
                <td><%= periodo.getFechaFinPeriodo()%></td>
                <td><%= periodo.getSaldoVBA()%></td>
                <td style="display: none;"><%= periodo.getEstadoId()%></td>
                <td><%= Constantes.ESTADO_LABEL.get(periodo.getEstadoId())%></td>
                <td><%= periodo.getUpdateDatetime()%></td>
                <td>
                    <!--<button class="btnSelect btn btn-primary btn-xs" style="margin-right:16px;">
                        <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
                    </button>-->
                    <!--
                    <button type="button" class="btnSelectDelete btn btn-danger btn-xs dt-delete">
                        <span class="glyphicon glyphicon-remove" aria-hidden="true"></span>
                    </button>
                    -->
                </td>
            </tr>
        <%}%>
            
        </tbody>
    </table>
      
    </div>
  </div>
  
</div>
        
        
<!-- Fin tabla con datos -->

<script>
    //set params de busqueda previa
    //document.getElementById("filtroRun").value='<%=filtroRun%>';
        
</script>

<script>
        // Lista de empleados pre-cargada
        const empleados = [
        <%
            Iterator<EmpleadoVO> iterator = listaEmpleados.iterator();
            while (iterator.hasNext()) {
                EmpleadoVO empleado = iterator.next();
                String deptoNombre = empleado.getDeptoNombre();
                String cencoNombre = empleado.getCencoNombre();
                deptoNombre = deptoNombre.replaceAll("'", "");
                cencoNombre = cencoNombre.replaceAll("'", "");
                String label=empleado.getNombreCompleto()
                    + "[" + deptoNombre + "]"
                    + "[" + cencoNombre + "]";
                %>
                { id: '<%= empleado.getRut() %>', nombre: '<%= label %>' },        
            <%}%>
        ];
        
        const searchInput = document.getElementById('searchInput');
        const autocompleteList = document.getElementById('autocompleteList');

        // Filtrar empleados en tiempo real
        searchInput.addEventListener('input', function() {
            const filter = searchInput.value.toLowerCase();
            autocompleteList.innerHTML = '';

            if (filter.length === 0) {
                autocompleteList.style.display = 'none';
                return;
            }

            const filteredEmpleados = empleados.filter(empleado => 
                empleado.nombre.toLowerCase().includes(filter)
            );

            if (filteredEmpleados.length > 0) {
                autocompleteList.style.display = 'block';
                filteredEmpleados.forEach(empleado => {
                    const li = document.createElement('li');
                    //li.textContent = empleado.nombre;
                    li.textContent = '[' + empleado.id + '] ' + empleado.nombre;
                    li.dataset.id = empleado.id;
                    document.getElementById('filtroRun').value=empleado.id;
                    autocompleteList.appendChild(li);
                });
            } else {
                autocompleteList.style.display = 'none';
            }
        });

        // Seleccionar un empleado
        autocompleteList.addEventListener('click', function(event) {
            if (event.target.tagName === 'LI') {
                searchInput.value = event.target.textContent;
                autocompleteList.style.display = 'none';
            }
        });

        // Manejar el envío del formulario
        document.getElementById('searchForm').addEventListener('submit', function(event) {
            //event.preventDefault();
            if (searchInput.value === '') event.preventDefault();
            //alert('Empleado seleccionado: ' + searchInput.value);
        });

        // Ocultar la lista de autocompletar si se hace clic fuera de ella
        document.addEventListener('click', function(event) {
            if (!autocompleteList.contains(event.target) && event.target !== searchInput) {
                autocompleteList.style.display = 'none';
            }
        });
    </script>

</body>
</html>
