
<%@page import="cl.femase.gestionweb.common.Constantes"%>
<%@page import="cl.femase.gestionweb.vo.AfpVO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>

<%
    String mainTitle    = "AFPs";
    String fileTitle    = "afps";
    String urlPattern   = "AfpsCRUD";
    List<AfpVO> list = (List<AfpVO>)request.getAttribute("lista");
    if (list == null) list = new ArrayList<>();
   
    //filtros de busqueda
    String filtroNombre = (String)request.getAttribute("filtroNombre");
    String filtroEstado = (String)request.getAttribute("filtroEstado");
    if (filtroNombre == null) filtroNombre = "";
    if (filtroEstado == null) filtroEstado = "1";
    
    //num columnas
    String columnas = "0,1,2,3";
%>

<!doctype html>
<html lang="es">
    <head>
	<meta charset="UTF-8">
        <meta name="description" content="CRUD - <%=mainTitle%>" />
    <title><%=mainTitle%>-CRUD</title>
    
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.css">
    <!--<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.1.1/css/bootstrap.css">-->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.5.2/css/bootstrap.css">
    <link rel="stylesheet" href="https://cdn.datatables.net/1.10.19/css/dataTables.bootstrap4.min.css">
    <link rel="stylesheet" href="https://cdn.datatables.net/buttons/1.5.2/css/buttons.bootstrap4.min.css">
   
   	<!-- css para modal -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.0.0-beta.2/css/bootstrap.css">
   
   <script type="text/javascript" language="javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
   <script type="text/javascript" language="javascript" src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js"></script>
   <script type="text/javascript" language="javascript" src="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.1.1/js/bootstrap.min.js"></script>
   <script type="text/javascript" language="javascript" src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>
   <script type="text/javascript" language="javascript" src="https://cdn.datatables.net/1.10.19/js/dataTables.bootstrap4.min.js"></script>
   <script type="text/javascript" language="javascript" src="https://cdn.datatables.net/buttons/1.5.2/js/dataTables.buttons.min.js"></script>
   <script type="text/javascript" language="javascript" src="https://cdn.datatables.net/buttons/1.5.2/js/buttons.bootstrap4.min.js"></script>
   <script type="text/javascript" language="javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.1.3/jszip.min.js"></script>
   <script type="text/javascript" language="javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.36/pdfmake.min.js"></script>
   <script type="text/javascript" language="javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.36/vfs_fonts.js"></script>
   <script type="text/javascript" language="javascript" src="https://cdn.datatables.net/buttons/1.5.2/js/buttons.html5.min.js"></script>
   <script type="text/javascript" language="javascript" src="https://cdn.datatables.net/buttons/1.5.2/js/buttons.print.min.js"></script>
   <script type="text/javascript" language="javascript" src="https://cdn.datatables.net/buttons/1.5.2/js/buttons.colVis.min.js"></script>
   
    <!-- include para modal -->
    <script type="text/javascript" language="javascript" src="https://unpkg.com/popper.js"></script>
    <script type="text/javascript" language="javascript" src="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.0.0-beta/js/bootstrap.min.js"></script>

    
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
            $("#loadercontainer").css("display","none");
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
            { "width": "20%", "targets": 0 }
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
	$('.dt-add').each(function () {
            $(this).on('click', function(evt){
                //Campos en el formulario de creacion de nuevo registro
                document.getElementById("action").value='create';
                document.getElementById("id").disabled = false;
                document.getElementById("id").value = '';
                document.getElementById("nombre").value = '';
                document.getElementById("estado").value = '1';
                
                var botonEliminar = document.getElementById("deleteButton");
                botonEliminar.disabled = true;
                var nombreBuscar = document.searchForm.filtroNombre;
                document.editNewForm.filtroNombre.value = nombreBuscar.value;
                var estadoBuscar = document.searchForm.filtroEstado;
                document.editNewForm.filtroEstado.value = estadoBuscar.value;

                //inicializar formulario para crear nuevo registro
                document.getElementById("id").readOnly = false;
                document.getElementById("nombre").readOnly = false;
                document.getElementById("estado").readOnly = false;
                
                document.getElementById("saveButton").disabled = false;
                document.getElementById("deleteButton").disabled = true;

                $("#editModalForm").modal("show");
            });
	});
        
        // code to read selected table row cell data (values).
        //evento click en boton Select. Toma los valores de la fila seleccionada (columna por columna)        
        $("#myTable").on('click','.btnSelect',function(){
             // get the current row
             var currentRow=$(this).closest("tr"); 
             var id     = currentRow.find("td:eq(0)").text(); // get current row 1st TD value
             var nombre = currentRow.find("td:eq(1)").text(); // get current row 2nd TD
             var estadoId  = currentRow.find("td:eq(2)").text(); // get current row 3rd TD
             document.getElementById("action").value='update';
             setValuesToEdit(id, nombre, estadoId);
             
             $("#editModalForm").modal("show");
        });
        
        //evento click en boton Delete. Toma los valores de la fila seleccionada (columna por columna)
        $("#myTable").on('click','.btnSelectDelete',function(){
             // get the current row
             var currentRow=$(this).closest("tr"); 

             var id     = currentRow.find("td:eq(0)").text(); // get current row 1st TD value
             var nombre = currentRow.find("td:eq(1)").text(); // get current row 2nd TD
             var estadoId  = currentRow.find("td:eq(2)").text(); // get current row 3rd TD
             
             document.getElementById("action").value='delete';
             ////document.getElementById("labelForm").value='Eliminar Registro';
             setValuesToEdit(id, nombre, estadoId);
        });
        
        //set params de busqueda previa
        document.getElementById("filtroNombre").value='<%=filtroNombre%>';
        document.getElementById("filtroEstado").value='<%=filtroEstado%>';
        
        /*
        $("#datepicker").datepicker({ 
            autoclose: true, 
            todayHighlight: true,
            language: "es"
        }).datepicker('update', new Date());
                                    
         $("#fechaInicioContrato").datepicker({ 
             autoclose: true, 
             todayHighlight: true,
             language: "es"
         }).datepicker('update', new Date());
         */
         
         
    } );

    /**
    * 
    * */
    function setValuesToEdit(id, nombre, estadoId){
        //campos de busqueda
        var nombreBuscar = document.searchForm.filtroNombre;
        var estadoBuscar = document.searchForm.filtroEstado;
	document.editNewForm.filtroNombre.value = nombreBuscar.value;
        document.editNewForm.filtroEstado.value = estadoBuscar.value;
        //Primary key del registro seleccionado
        var idDelete = document.getElementById("idDelete");
        //Atributos adicionales del registro seleccionado
        var inputID 	= document.getElementById("id");//hiddenfield
        var inputNombre = document.getElementById("nombre");//textbox
        var inputEstado = document.getElementById("estado");//listbox
        
        //botones
        var botonGuardar 	= document.getElementById("saveButton");
        var botonEliminar 	= document.getElementById("deleteButton");
        
        //Seteo para mostrar valores seleccionados en el formulario para edicion/modificacion
        idDelete.value = id;
        inputID.value = id;
        inputNombre.value = nombre;
        inputEstado.value = estadoId;
        
        inputID.readOnly = true;
        inputNombre.readOnly = false;
        inputEstado.readOnly = false;
        
        //botones
        botonGuardar.disabled = false;
        botonEliminar.disabled = true;
        
        if (document.getElementById("action").value === 'delete'){
            inputID.readOnly = true;
            //desactivar la edicion de los campos
            inputNombre.readOnly = true;
            inputEstado.readOnly = true;
            
            botonGuardar.disabled = true;
            botonEliminar.disabled = false;
        }    
        $("#editModalForm").modal("show");
        
    }

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
            
    </style>                    
</head>
<body>
    <table align="center" class="table table-striped table-bordered dt-responsive nowra" style="width:70%">
<form name="searchForm" id="searchForm" method="POST" action="<%=request.getContextPath()%>/<%=urlPattern%>?action=list" target="_self">
  <tr>
    <td width="10%">Nombre</td>
    <td width="17%">
        <input type="text" name="filtroNombre" id="filtroNombre" />
      </td>
    <td width="15%">Estado</td>
    <td width="25%">
        <select name="filtroEstado" id="filtroEstado">
            <option value="1">Vigente</option>
            <option value="2">No Vigente</option>
        </select>
    </td>
    <td width="16%"><input type="submit" name="buscar" id="buscar" value="Buscar" /></td>
    <td width="17%">&nbsp;</td>
  </tr>
  </form>
</table>
    
  <!-- Inicio tabla con datos -->  
   <div class="container">
  

  <div class="row">
    
    <div class="col-12" > 
      <h3 class="titulo-tabla"><%=mainTitle%> </h3>
      
      
      <table id="myTable" class="table table-striped table-bordered" style="width:100%">
        <thead>
            <tr>
                
            <th>Id </th>
            <th>Nombre</th>
            <th>EstadoId</th>
            <th>Estado nombre</th>
                <%if (!list.isEmpty()){%>
                    <th style="text-align:center;width:100px;">Nuevo registro 
                        <button type="button" class="btn btn-success btn-xs dt-add">
                            <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
                        </button>
                    </th>
                <%}%>
            </tr>
        </thead>
        <tbody>
            <%
        Iterator<AfpVO> iter = list.iterator();
        AfpVO registro = null;
        while (iter.hasNext()) {
            registro = iter.next();
        %>
            
            <tr>
                <td><%= registro.getCode()%></td>
                <td><%= registro.getNombre()%></td>
                <td><%= registro.getEstado()%></td>
                <td><%= registro.getEstadoNombre()%></td>
                <td>
                    <button class="btnSelect btn btn-primary btn-xs" style="margin-right:16px;">
                        <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
                    </button>
                    <!-- <button type="button" class="btnSelectDelete btn btn-danger btn-xs dt-delete">
                        <span class="glyphicon glyphicon-remove" aria-hidden="true"></span>
                    </button>-->
                
                </td>
            </tr>
        <%}%>
            
        </tbody>
    </table>
      
    </div>
  </div>
  
</div>
        
        
<!-- Fin tabla con datos -->

<!-- ************************************************************************************* -->
<!-- ************************************************************************************* -->
<form name="editNewForm" method="POST" action="<%=request.getContextPath()%>/<%=urlPattern%>">
<!-- Inicio modal para crear/editar registro -->                
<div class="modal fade" 
        id="editModalForm" name="editModalForm" 
        tabindex="-1" 
        role="dialog" 
        aria-labelledby="exampleModalLabel" 
        aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered modal-lg" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="exampleModalLabel">Crear/Modificar Registro</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="container-fluid">
        <!-- Inicio nueva fila -->    
        <div class="row">
            <div class="col">
                ID<input type="text"  name="id" id="id">
            </div>
            <div class="col">
                Nombre<input type="text"  name="nombre" id="nombre" placeholder="Ingrese nombre">
            </div>
            <div class="col">
                Estado
                <select name="estado" id="estado">
                    <option value="1">Vigente</option>
                    <option value="2">No Vigente</option>
                </select>
            </div>
            
        </div>
        <!-- fin fila -->
            
</div>
      <div class="modal-footer border-top-0 d-flex justify-content-center">
            <input type="hidden" name="action" id="action" value="update">
            <input type="hidden" name="filtroNombre" id="filtroNombre">
            <input type="hidden" name="filtroEstado" id="filtroEstado">
            <!-- Primary Key -->
            <input type="hidden" name="idDelete" id="idDelete">
            <!-- Fin Primary Key -->
            
            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            <button id="saveButton" type="submit" class="btn btn-primary">Guardar cambios</button>
            <button id="deleteButton" type="submit" class="btn btn-primary">Eliminar</button>
        </div>
    </div>
  </div>
</div>
</form>
<!-- Fin modal para crear/editar registro -->
     
</body>
</html>
