
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
   
   List<AlertaSistemaVO> list = (List<AlertaSistemaVO>)request.getAttribute("lista");
   if (list == null) list = new ArrayList<>();
   List<EmpresaVO> empresas = (List<EmpresaVO>)session.getAttribute("empresas");
   
   	//filtros de busqueda
    String empresaId = (String)request.getAttribute("filtroEmpresaId");
    if (empresaId == null) empresaId = "-1";
    
    //num columnas
    String columnas = "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14";
	
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
        const div = document.getElementById("compactForm");
        div.style.display = 'none';
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
                
                document.getElementById("idAlerta").value   = '';
                document.getElementById("empresaId").value  = '';
                document.getElementById("titulo").value     = '';
                document.getElementById("mensaje").value    = '';
                document.getElementById("tipo").value       = '';
                document.getElementById("fechaEnvio").value = '';
                document.getElementById("estado").value     = '';
                document.getElementById("prioridad").value  = '';
                document.getElementById("urlAccion").value  = '';
                document.getElementById("icono").value      = '';
                document.getElementById("fechaHoraDesde").value = '';
                document.getElementById("fechaHoraHasta").value = '';
                
                var botonEliminar = document.getElementById("deleteButton");
                botonEliminar.disabled = false;
                var empresaABuscar = document.searchForm.filtroEmpresaId;
                document.editNewForm.filtroEmpresaId.value = empresaABuscar.value;

                //inicializar formulario para crear nuevo registro
                document.getElementById("idAlerta").readOnly = true;
                document.getElementById("empresaId").readOnly = false;
                document.getElementById("titulo").readOnly = false;
                document.getElementById("mensaje").readOnly = false;
                document.getElementById("tipo").readOnly = false;
                document.getElementById("fechaEnvio").readOnly = false;
                document.getElementById("estado").readOnly = false;
                document.getElementById("prioridad").readOnly = false;
                document.getElementById("urlAccion").readOnly = false;
                document.getElementById("icono").readOnly = false;
                document.getElementById("fechaHoraDesde").readOnly = false;
                document.getElementById("fechaHoraHasta").readOnly = false;
                
                //botones
                document.getElementById("saveButton").disabled = false;
                document.getElementById("deleteButton").disabled = true;

                //$("#editModalForm").modal("show");
                //$("#compactForm").modal("show");
                const div = document.getElementById("compactForm");
                div.style.display = div.style.display === 'none' ? 'block' : 'none'; 
            });
	});
        
        // code to read selected table row cell data (values).
        $("#myTable").on('click','.btnSelect',function(){
             // get the current row. columnas de la fila seleccionada
             var currentRow=$(this).closest("tr"); 
             
                var idAlerta=currentRow.find("td:eq(0)").text();
                var empresaId=currentRow.find("td:eq(1)").text();
                var titulo=currentRow.find("td:eq(2)").text();
                var mensaje=currentRow.find("td:eq(3)").text();
                var tipo=currentRow.find("td:eq(4)").text();
                var fechaEnvio=currentRow.find("td:eq(5)").text();
                var estado=currentRow.find("td:eq(6)").text();
                var prioridad=currentRow.find("td:eq(7)").text();
                var urlAccion=currentRow.find("td:eq(8)").text();
                var icono=currentRow.find("td:eq(9)").text();
                var fechaHoraDesde=currentRow.find("td:eq(14)").text();
                var fechaHoraHasta=currentRow.find("td:eq(15)").text();
             
             document.getElementById("action").value='update';
             document.getElementById("labelForm").value='Modificar Registro';
             setValuesToEdit(idAlerta, empresaId, titulo, mensaje,tipo,fechaEnvio,estado, prioridad,urlAccion,icono,fechaHoraDesde,fechaHoraHasta);
             
        });
        
        $("#myTable").on('click','.btnSelectDelete',function(){
            // get the current row
            var currentRow=$(this).closest("tr"); 

            var idAlerta=currentRow.find("td:eq(0)").text();
            var empresaId=currentRow.find("td:eq(1)").text();
            var titulo=currentRow.find("td:eq(2)").text();
            var mensaje=currentRow.find("td:eq(3)").text();
            var tipo=currentRow.find("td:eq(4)").text();
            var fechaEnvio=currentRow.find("td:eq(5)").text();
            var estado=currentRow.find("td:eq(6)").text();
            var prioridad=currentRow.find("td:eq(7)").text();
            var urlAccion=currentRow.find("td:eq(8)").text();
            var icono=currentRow.find("td:eq(9)").text();
            var fechaHoraDesde=currentRow.find("td:eq(14)").text();
            var fechaHoraHasta=currentRow.find("td:eq(15)").text();
             
            document.getElementById("action").value='delete';
            document.getElementById("labelForm").value='Eliminar Registro';
            setValuesToEdit(idAlerta, empresaId, titulo, mensaje,tipo,fechaEnvio,estado, prioridad,urlAccion,icono,fechaHoraDesde,fechaHoraHasta);
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
    function setValuesToEdit(idAlerta, empresaId, titulo, mensaje,tipo,fechaEnvio,estado, prioridad,urlAccion,icono,fechaHoraDesde,fechaHoraHasta){
        var empresaABuscar = document.searchForm.filtroEmpresaId;
	document.editNewForm.filtroEmpresaId.value = empresaABuscar.value;
        
        var idDelete = document.getElementById("idDelete");
        var empresaIdDelete = document.getElementById("empresaIdDelete");
        //campos del formulario de modificacion/creacion
        var inputID 		= document.getElementById("idAlerta");
        var selectEmpresaId = document.getElementById("empresaId");
        var inputTitulo		= document.getElementById("titulo");
        var inputMensaje	= document.getElementById("mensaje");
        var inputTipo		= document.getElementById("tipo");
        var inputFechaEnvio	= document.getElementById("fechaEnvio");
        var inputEstado		= document.getElementById("estado");
        var inputPrioridad	= document.getElementById("prioridad");
        var inputUrlAccion	= document.getElementById("urlAccion");
        var inputIcono		= document.getElementById("icono");
        var inputFechaHoraDesde		= document.getElementById("fechaHoraDesde");
        var inputFechaHoraHasta		= document.getElementById("fechaHoraHasta");
        
        var botonGuardar 	= document.getElementById("saveButton");
        var botonEliminar 	= document.getElementById("deleteButton");
        
        idDelete.value = id;
        empresaIdDelete.value = empresaId;
        selectEmpresaId.value = empresaId;
        
        inputID.value=idAlerta; 			
        inputTitulo.value=titulo;			
        inputMensaje.value=mensaje;		
        inputTipo.value=tipo;			
        inputFechaEnvio.value=fechaEnvio;		
        inputEstado.value=estado;			
        inputPrioridad.value=prioridad;		
        inputUrlAccion.value=urlAccion;		
        inputIcono.value=icono;			
        inputFechaHoraDesde.value=fechaHoraDesde;
        inputFechaHoraHasta.value=fechaHoraHasta;
        
        //setear campos como de escritura
        selectEmpresaId.readOnly = false;
        inputID.readOnly = true; 			
        inputTitulo.readOnly = false;			
        inputMensaje.readOnly = false;		
        inputTipo.readOnly = false;			
        inputFechaEnvio.readOnly = false;		
        inputEstado.readOnly = false;			
        inputPrioridad.readOnly = false;		
        inputUrlAccion.readOnly = false;		
        inputIcono.readOnly = false;			
        inputFechaHoraDesde.readOnly = false;
        inputFechaHoraHasta.readOnly = false;
        
        botonGuardar.disabled = false;
        botonEliminar.disabled = true;
        //alert('accion: ' + document.getElementById("action").value);
        if (document.getElementById("action").value === 'delete'){
            inputID.readOnly = true;
            selectEmpresaId.disabled = true;
            
            inputID.readOnly = true; 			
            inputTitulo.readOnly = true;			
            inputMensaje.readOnly = true;		
            inputTipo.readOnly = true;			
            inputFechaEnvio.readOnly = true;		
            inputEstado.readOnly = true;			
            inputPrioridad.readOnly = true;		
            inputUrlAccion.readOnly = true;		
            inputIcono.readOnly = true;			
            inputFechaHoraDesde.readOnly = true;
            inputFechaHoraHasta.readOnly = true;
            
            botonGuardar.disabled = true;
            botonEliminar.disabled = false;
        }    
        //$("#editModalForm").modal("show");
        //$("#compactForm").modal("show");
        const div = document.getElementById("compactForm");
        div.style.display = div.style.display === 'none' ? 'block' : 'none';
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
            
        /*INICIO estilos para nuevo formulario compacto*/
        :root {
            --espacio: 8px;
            --padding: 12px;
            --border-radius: 4px;
        }

        .formulario-container {
            max-width: 800px;
            margin: 20px auto;
            padding: var(--padding);
            background: #fff;
            border-radius: var(--border-radius);
            box-shadow: 0 2px 5px rgba(0,0,0,0.05);
        }

        .formulario {
            display: flex;
            flex-direction: column;
            gap: var(--espacio);
        }

        .grupo-campos {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: var(--espacio);
        }

        .campo {
            margin: 0;
        }

        .campo input, .campo select, .campo textarea {
            width: 100%;
            padding: var(--padding);
            border: 1px solid #ddd;
            border-radius: var(--border-radius);
            font-size: 10px;
            margin-top: var(--espacio);
        }

        .campo label {
            display: block;
            color: #666;
            font-weight: 500;
            margin-bottom: var(--espacio);
        }

        .boton-enviar {
            background: #3498db;
            color: white;
            padding: var(--padding);
            border: none;
            border-radius: var(--border-radius);
            cursor: pointer;
            font-size: 14px;
            width: 100%;
            text-align: center;
            margin-top: var(--espacio);
        }

        /* Responsive */
        @media (max-width: 768px) {
            .grupo-campos {
                grid-template-columns: 1fr;
            }
        }

        /* Estilos generales */
        body {
            font-family: 'Segoe UI', system-ui, sans-serif;
            line-height: 1.4;
            background: #f5f5f5;
        }

        h2 {
            text-align: center;
            color: #2c3e50;
            margin-bottom: var(--espacio);
            font-size: 1.2em;
        }
        
        /*FIN estilos para nuevo formulario compacto*/
        
        /* estilos formulario modal*/
       
        /* Estilos para la ventana modal */
        .modal {
            display: none;
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0, 0, 0, 0.5);
            overflow: auto; /* Permite scroll si el contenido es largo */
        }

        .modal-content {
            background-color: #fff;
            margin: 5% auto; /* Reduce el margen superior */
            padding: 20px;
            border-radius: 8px;
            width: 80%; /* Aumenta el ancho */
            max-width: 600px; /* Limita el ancho máximo */
        }

        .cerrar {
            color: #aaa;
            float: right;
            font-size: 28px;
            font-weight: bold;
        }

        .cerrar:hover,
        .cerrar:focus {
            color: black;
            cursor: pointer;
        }
       /* fin estilos formulario modal*/
            
    </style>                    
</head>
<body>
    <table align="center" class="table table-striped table-bordered dt-responsive nowra" style="width:70%">
<form name="searchForm" id="searchForm" method="POST" action="<%=request.getContextPath()%>/<%=servletName%>?action=list" target="_self">
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
            <th>Titulo</th>
            <th>Mensaje</th>
            <th>Tipo</th>
            <th>Fecha Envio</th>
            <th>Estado</th>
            <th>Prioridad</th>
            <th>Url</th>
            <th>Icono</th>
            <th>Creado por</th>
            <th>Modificado por</th>
            <th>Created At</th>
            <th>Updated At</th>
            <th>Desde</th>
            <th>Hasta</th>
                <%//if (!list.isEmpty()){%>
                    <button id="abrirModal">Abrir Formulario</button>
                    <th style="text-align:center;width:100px;">Nuevo registro 
                        <button type="button" class="btn btn-success btn-xs dt-add">
                            <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
                        </button>
                    </th>
                <%//}%>
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
                <td><%= alerta.getIdAlerta()%></td>
                <td><%= alerta.getEmpresaId()%></td>
                <td><%= alerta.getTitulo()%></td>
                <td><%= alerta.getMensaje()%></td>
                <td><%= alerta.getTipo()%></td>
                <td><%= alerta.getFechaEnvio()%></td>
                <td><%= alerta.getEstado()%></td>
                <td><%= alerta.getPrioridad()%></td>
                <td><%= alerta.getUrlAccion()%></td>
                <td><%= alerta.getIcono()%></td>
                <td><%= alerta.getCreadoPor()%></td>
                <td><%= alerta.getModificadoPor()%></td>
                <td><%= alerta.getCreatedAt()%></td>
                <td><%= alerta.getUpdatedAt()%></td>
                <td><%= alerta.getFechaHoraDesde()%></td>
                <td><%= alerta.getFechaHoraHasta()%></td>
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

<!-- INICIO nuevo formulario mas compacto para mostrar todos los campos -->
<!-- Ventana modal -->
    <div id="miModal" class="modal">
        <div class="modal-content">
            <span class="cerrar">&times;</span>

            <!-- Contenido del formulario compacto -->
            <div id="compactForm" name="compactForm">
                <h2>Registro Express</h2>
                <form class="formulario">
                    <!-- Grupo 1 -->
                    <div class="grupo-campos">
                        <div class="campo">
                            <label>Nombre</label>
                            <input type="text" name="nombre" required>
                        </div>
                        <div class="campo">
                            <label>Apellido</label>
                            <input type="text" name="apellido" required>
                        </div>
                    </div>

                    <!-- Grupo 2 -->
                    <div class="grupo-campos">
                        <div class="campo">
                            <label>Email</label>
                            <input type="email" name="email" required>
                        </div>
                        <div class="campo">
                            <label>Teléfono</label>
                            <input type="tel" name="telefono" required>
                        </div>
                    </div>

                    <!-- Grupo 3 -->
                    <div class="grupo-campos">
                        <div class="campo">
                            <label>Género</label>
                            <select name="genero" required>
                                <option value="">Seleccione...</option>
                                <option value="masculino">Masculino</option>
                                <option value="femenino">Femenino</option>
                            </select>
                        </div>
                        <div class="campo">
                            <label>País</label>
                            <select name="pais" required>
                                <option value="">Seleccione...</option>
                                <option value="argentina">Argentina</option>
                                <option value="brasil">Brasil</option>
                            </select>
                        </div>
                    </div>

                    <!-- Grupo 4 -->
                    <div class="grupo-campos">
                        <div class="campo">
                            <label>Dirección</label>
                            <input type="text" name="direccion" required>
                        </div>
                        <div class="campo">
                            <label>Código postal</label>
                            <input type="text" name="cp" required>
                        </div>
                    </div>

                    <!-- Grupo 5 -->
                    <div class="grupo-campos">
                        <div class="campo">
                            <label>Fecha nacimiento</label>
                            <input type="date" name="fecha_nacimiento" required>
                        </div>
                        <div class="campo">
                            <label>Profesión</label>
                            <input type="text" name="profesion" required>
                        </div>
                    </div>

                    <!-- Grupo 6 -->
                    <div class="grupo-campos">
                        <div class="campo">
                            <label>Intereses</label>
                            <select name="intereses" multiple required>
                                <option value="deporte">Deporte</option>
                                <option value="musica">Música</option>
                                <option value="lectura">Lectura</option>
                            </select>
                        </div>
                        <div class="campo">
                            <label>Comentarios</label>
                            <textarea name="comentarios" rows="3"></textarea>
                        </div>
                    </div>

                    <button type="submit" class="boton-enviar">Enviar</button>
                </form>
            </div>
        </div>
    </div>

<!-- FIN nuevo formulario mas compacto para mostrar todos los campos -->

     
<script>
        // Obtener elementos del DOM
        const modal = document.getElementById("miModal");
        const btnAbrir = document.getElementById("abrirModal");
        const spanCerrar = document.querySelector(".cerrar");

        // Abrir la ventana modal al hacer clic en el botón
        btnAbrir.onclick = function () {
            //Campos en el formulario de edicion de registro
            /*document.getElementById("labelForm").value='Crear Registro';
            document.getElementById("action").value='create';

            document.getElementById("idAlerta").value   = '';
            document.getElementById("empresaId").value  = '';
            document.getElementById("titulo").value     = '';
            document.getElementById("mensaje").value    = '';
            document.getElementById("tipo").value       = '';
            document.getElementById("fechaEnvio").value = '';
            document.getElementById("estado").value     = '';
            document.getElementById("prioridad").value  = '';
            document.getElementById("urlAccion").value  = '';
            document.getElementById("icono").value      = '';
            document.getElementById("fechaHoraDesde").value = '';
            document.getElementById("fechaHoraHasta").value = '';

            var botonEliminar = document.getElementById("deleteButton");
            botonEliminar.disabled = false;
            var empresaABuscar = document.searchForm.filtroEmpresaId;
            document.editNewForm.filtroEmpresaId.value = empresaABuscar.value;

            //inicializar formulario para crear nuevo registro
            document.getElementById("idAlerta").readOnly = true;
            document.getElementById("empresaId").readOnly = false;
            document.getElementById("titulo").readOnly = false;
            document.getElementById("mensaje").readOnly = false;
            document.getElementById("tipo").readOnly = false;
            document.getElementById("fechaEnvio").readOnly = false;
            document.getElementById("estado").readOnly = false;
            document.getElementById("prioridad").readOnly = false;
            document.getElementById("urlAccion").readOnly = false;
            document.getElementById("icono").readOnly = false;
            document.getElementById("fechaHoraDesde").readOnly = false;
            document.getElementById("fechaHoraHasta").readOnly = false;

            //botones
            document.getElementById("saveButton").disabled = false;
            document.getElementById("deleteButton").disabled = true;
            */
            modal.style.display = "block";
        };

        // Cerrar la ventana modal al hacer clic en el botón "x"
        spanCerrar.onclick = function () {
            modal.style.display = "none";
        };

        // Cerrar la ventana modal al hacer clic fuera del contenido
        window.onclick = function (event) {
            if (event.target === modal) {
                modal.style.display = "none";
            }
        };
    </script>

</body>
</html>
