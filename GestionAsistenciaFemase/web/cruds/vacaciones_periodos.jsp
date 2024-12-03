
<%@page import="cl.femase.gestionweb.vo.UsuarioVO"%>
<%@page import="cl.femase.gestionweb.vo.VacacionesSaldoPeriodoVO"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="cl.femase.gestionweb.vo.UsuarioCentroCostoVO"%>
<%@page import="cl.femase.gestionweb.common.Constantes"%>
<%@page import="cl.femase.gestionweb.vo.EmpleadoVO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>

<%
    UsuarioVO userInSession = (UsuarioVO)session.getAttribute("usuarioObj");
    String mainTitle    = "Vacaciones por periodos";
    String fileTitle    = "vacaciones_saldo_periodo";
    String urlPattern   = "VacacionesSaldosPeriodosCRUD";
    LinkedHashMap<String, VacacionesSaldoPeriodoVO> hashPeriodos = 
        (LinkedHashMap<String, VacacionesSaldoPeriodoVO>)session.getAttribute("lista_CRUD_saldovacperiodos");
    if (hashPeriodos == null) hashPeriodos = new LinkedHashMap<>();
    //EmpleadosDAO daoEmpleados = n
    
    /*
        String sessionAttributeName="empleados_cenco_usuario";
        if (userInSession.getAdminEmpresa().compareTo("S") == 0)
            sessionAttributeName="empleados";

        List<EmpleadoVO> listaEmpleados = 
            (List<EmpleadoVO>)session.getAttribute(sessionAttributeName);
    */
    
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
    *           nombre              1
    *           Run                 2
                depto_nombre        3
                cenco_nombre        4
                Inicio periodo      5
                Fin periodo         6
                Saldo VBA           7
                Estado Id (oculto)
                Estado              8
                Ult. actualizacion  9
        
    */
    //num columnas
    String columnas = "0,1,2,3,4,5,6,7,8,9";
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
        const select = document.getElementById("empleados");
        select.style.display = "none"; //ocultar combo
    
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

        /**
        * 
        * */
        function buscarEmpleados() {
            eliminarListaEmpleados();
            var nombre = document.getElementById("busqueda").value;
            if (nombre !== '' && nombre !== ' '){
                var xhr = new XMLHttpRequest();
                xhr.open("GET", "<%=request.getContextPath()%>/<%=urlPattern%>?action=buscarEmpleados&nombre=" + nombre, true);
                xhr.onreadystatechange = function () {
                    if (xhr.readyState === 4 && xhr.status === 200) {
                        var jsonString = xhr.responseText;
                        //document.getElementById("resultado").innerHTML = jsonString; // Maneja el resultado aquí
                        try {
                            // Parsear el JSON
                            const empleados = JSON.parse(jsonString);

                            const select = document.getElementById("empleados");
                            select.style.display = "block"; // Muestra el select de empleados
    
                            // Obtener el elemento select
                            const selectElement = document.getElementById('empleados');

                            // Llenar el select con opciones
                            empleados.forEach(empleado => {
                                const option = document.createElement('option');
                                option.value = empleado.empresaId + '|' + empleado.cencoId + '|' + empleado.run;
                                option.textContent = '[' + empleado.deptoNombre + '][' + empleado.cencoNombre + '] ' + empleado.nombreCompleto; // Nombre a mostrar en la lista
                                selectElement.appendChild(option);
                            });
                            
                        } catch (error) {
                            console.error("Error al parsear el JSON:", error);
                        }
                    }
                };
                xhr.send();
            }else{
                //document.getElementById("resultado").innerHTML = '';
                eliminarListaEmpleados();
                const select = document.getElementById("empleados");
                select.style.display = "none"; //ocultar combo
            }
        }
    
        function eliminarListaEmpleados() {
            const select = document.getElementById("empleados");
            const cantidadOpciones = select.options.length;

            for (let i = cantidadOpciones - 1; i >= 0; i--) {
                select.remove(i); // Elimina cada opción desde el final hacia el principio
            }
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
        <input type="hidden" name="filtroRun" id="filtroRun" value="filtroRun">
        
        <input type="text" id="busqueda" onkeyup="buscarEmpleados()" placeholder="Ingresa el nombre del empleado.">
        
        <select id="empleados" name="empleados"></select>
        
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
                <th>Nombre</th>
                <th>Run</th>
                <th>Depto</th>
                <th>Cenco</th>
                <th>Inicio periodo</th>
                <th>Fin periodo</th>
                <th>Saldo VBA</th>
                <th style="display: none;">Estado Id</th>
                <th>Estado</th>
                <th>Ult. actualizacion</th>
            
                <%//if (!hashPeriodos.isEmpty()){%>
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
                <td><%= periodo.getNombreEmpleado()%></td>
                <td><%= periodo.getRunEmpleado()%></td>
                <td><%= periodo.getDeptoNombre()%></td>
                <td><%= periodo.getCencoNombre()%></td>
                <td><%= periodo.getFechaInicioPeriodo()%></td>
                <td><%= periodo.getFechaFinPeriodo()%></td>
                <td><%= periodo.getSaldoVBA()%></td>
                <td style="display: none;"><%= periodo.getEstadoId()%></td>
                <td><%= Constantes.ESTADO_LABEL.get(periodo.getEstadoId())%></td>
                <td><%= periodo.getUpdateDatetime()%></td>
                <!-- <td>
                    <button class="btnSelect btn btn-primary btn-xs" style="margin-right:16px;">
                        <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
                    </button>
                    
                    <button type="button" class="btnSelectDelete btn btn-danger btn-xs dt-delete">
                        <span class="glyphicon glyphicon-remove" aria-hidden="true"></span>
                    </button>
                    
                </td>
                -->
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
        

    </script>

</body>
</html>
