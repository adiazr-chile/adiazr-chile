
<%@page import="cl.femase.gestionweb.common.Constantes"%>
<%@page import="cl.femase.gestionweb.vo.ParametroVO"%>
<%@page import="cl.femase.gestionweb.vo.EmpresaVO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>

<%
   
   String mainTitle = "Parametros";
   String fileTitle = "parametros"; 
   List<ParametroVO> list = (List<ParametroVO>)request.getAttribute("lista");
   if (list == null) list = new ArrayList<>();
   List<EmpresaVO> empresas = (List<EmpresaVO>)session.getAttribute("empresas");
   
   	//filtros de busqueda
    String empresaId = (String)request.getAttribute("filtroEmpresaId");
    if (empresaId == null) empresaId = "-1";
    
	//num columnas
	String columnas = "0,1,2,3,4";
	
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
                //Campos en el formulario de edicion de registro
                document.getElementById("labelForm").value='Crear Registro';
                document.getElementById("action").value='create';
                document.getElementById('id').readOnly= false;
                
                document.getElementById("id").value='';
                document.getElementById("empresaId").value='';
                document.getElementById("label").value='';
                document.getElementById("valor").value='1';
                var botonEliminar = document.getElementById("deleteButton");
                botonEliminar.disabled = true;
				var empresaABuscar = document.searchForm.filtroEmpresaId;
				document.editNewForm.filtroEmpresaId.value = empresaABuscar.value;
        
		
				//inicializar formulario para crear nuevo registro
				document.getElementById("empresaId").readOnly = false;
				document.getElementById("label").readOnly = false;
				document.getElementById("valor").readOnly = false;
				document.getElementById("saveButton").disabled = false;
				document.getElementById("deleteButton").disabled = true;
		
				$("#editModalForm").modal("show");
            });
	});
        
        // code to read selected table row cell data (values).
        $("#myTable").on('click','.btnSelect',function(){
             // get the current row
             var currentRow=$(this).closest("tr"); 
             var id=currentRow.find("td:eq(0)").text(); // get current row 1st TD value
             var empresaId=currentRow.find("td:eq(1)").text(); // get current row 2nd TD
             var label=currentRow.find("td:eq(2)").text(); // get current row 3rd TD
             var valor=currentRow.find("td:eq(3)").text(); // get current row 3rd TD
             document.getElementById("action").value='update';
             document.getElementById("labelForm").value='Modificar Registro';
             setValuesToEdit(id, empresaId, label, valor);
             
        });
        
        $("#myTable").on('click','.btnSelectDelete',function(){
             // get the current row
             var currentRow=$(this).closest("tr"); 

             var id=currentRow.find("td:eq(0)").text(); // get current row 1st TD value
             var empresaId=currentRow.find("td:eq(1)").text(); // get current row 2nd TD
             var label=currentRow.find("td:eq(2)").text(); // get current row 3rd TD
             var valor=currentRow.find("td:eq(3)").text(); // get current row 3rd TD
             document.getElementById("action").value='delete';
             document.getElementById("labelForm").value='Eliminar Registro';
             setValuesToEdit(id, empresaId, label, valor);
        });
        
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
         
         document.getElementById("filtroEmpresaId").value='<%=empresaId%>';
         
    } );

    /**
    * 
    * */
    function setValuesToEdit(id, empresaId,label,valor){
        var empresaABuscar = document.searchForm.filtroEmpresaId;
		document.editNewForm.filtroEmpresaId.value = empresaABuscar.value;
        
        var idDelete = document.getElementById("idDelete");
        var empresaIdDelete = document.getElementById("empresaIdDelete");
        
        var inputID 		= document.getElementById("id");
        var selectEmpresaId = document.getElementById("empresaId");
		var inputLabel 		= document.getElementById("label");
        var inputValor 		= document.getElementById("valor");
        var botonGuardar 	= document.getElementById("saveButton");
        var botonEliminar 	= document.getElementById("deleteButton");
        
        idDelete.value = id;
        empresaIdDelete.value = empresaId;
        inputID.value = id;
        selectEmpresaId.value = empresaId;
        inputLabel.value = label;
        inputValor.value = valor;
        
        selectEmpresaId.readOnly = false;
        inputLabel.readOnly = false;
        inputValor.readOnly = false;
        
        botonGuardar.disabled = false;
        botonEliminar.disabled = true;
        //alert('accion: ' + document.getElementById("action").value);
        if (document.getElementById("action").value === 'delete'){
            inputID.readOnly = true;
            selectEmpresaId.disabled = true;
            inputLabel.readOnly = true;
            inputValor.readOnly = true;
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
    </style>                    
</head>
<body>
    <table align="center" class="table table-striped table-bordered dt-responsive nowra" style="width:70%">
<form name="searchForm" id="searchForm" method="POST" action="<%=request.getContextPath()%>/ParametrosCRUD?action=list" target="_self">
  <tr>
    <td width="10%">Empresa</td>
    <td width="17%">
      <select id="filtroEmpresaId" name="filtroEmpresaId" style="width:150px;" required>
          <option value="-1" selected="">Seleccione</option>
        <%
                Iterator<EmpresaVO> iteraempresas = empresas.iterator();
                while(iteraempresas.hasNext() ) {
                    EmpresaVO auxempresa = iteraempresas.next();
                    %>
        <option value="<%=auxempresa.getId()%>"><%=auxempresa.getNombre()%></option>
        <%
                }
            %>
        </select>
      </td>
    <td width="15%">&nbsp;</td>
    <td width="25%">&nbsp;</td>
    <td width="16%">&nbsp;</td>
    <td width="17%"><input type="submit" name="buscar" id="buscar" value="Buscar" /></td>
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
            <th>Empresa</th>
            <th>Label</th>
            <th>Valor</th>
            <th>Create Date</th>
            <th>Amend Date</th>
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
        Iterator<ParametroVO> iter = list.iterator();
        ParametroVO param = null;
        while (iter.hasNext()) {
            param = iter.next();
        %>
            
            <tr>
                <td><%= param.getParamCode()%></td>
                <td><%= param.getEmpresaId()%></td>
                <td><%= param.getParamLabel()%></td>
                <td><%= param.getValor()%></td>
                <td><%= param.getFechaCreacion()%></td>
                <td><%= param.getFechaModificacion()%></td>
                <td>
                    <button class="btnSelect btn btn-primary btn-xs" style="margin-right:16px;">
                        <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
                    </button>
                    <button type="button" class="btnSelectDelete btn btn-danger btn-xs dt-delete">
                        <span class="glyphicon glyphicon-remove" aria-hidden="true"></span>
                    </button>
                
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

<!-- Inicio modal para editar registro -->                
<div id="editModalForm" name="editModalForm" id="editModalForm" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered" role="document">
    <div class="modal-content">
      <div class="modal-header border-bottom-0">
        <input type="text" class="form-control" name="labelForm" id="labelForm" readonly>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
        <form name="editNewForm" method="POST" action="<%=request.getContextPath()%>/ParametrosCRUD">
        <div class="modal-body">
          
            <div class="form-group">
                <label for="empresaId">Empresa</label>
                <select id="empresaId" name="empresaId" style="width:150px;" required>
                    <option value="-1">Ninguna</option>
                    <%
                        iteraempresas = empresas.iterator();
                        while(iteraempresas.hasNext() ) {
                            EmpresaVO auxempresa = iteraempresas.next();
                            %>
                            <option value="<%=auxempresa.getId()%>"><%=auxempresa.getNombre()%></option>
                            <%
                        }
                    %>
                </select>
            </div>
            
            <div class="form-group">
            <label for="id">ID</label>
            <input type="text" class="form-control" name="id" id="id">
          </div>
          <div class="form-group">
            <label for="label">Label</label>
            <input type="text" class="form-control" name="label" id="label" placeholder="Ingrese texto">
          </div>
          
          <div class="form-group">
            <label for="valor">Valor</label>
            <input type="text" class="form-control" name="valor" id="valor" placeholder="Ingrese">
          </div>
        </div>
        <div class="modal-footer border-top-0 d-flex justify-content-center">
            <input type="hidden" name="action" id="action" value="update">
            <input type="hidden" name="filtroEmpresaId" id="filtroEmpresaId">
            
            <!-- Primary Key -->
            <input type="hidden" name="idDelete" id="idDelete">
            <input type="hidden" name="empresaIdDelete" id="empresaIdDelete">
            <!-- Fin Primary Key -->
            
            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            <button id="saveButton" type="submit" class="btn btn-primary">Guardar cambios</button>
            <button id="deleteButton" type="submit" class="btn btn-primary">Eliminar</button>
        </div>
      </form>
    </div>
  </div>
</div>
<!-- Fin modal para editar registro -->
     
</body>
</html>
