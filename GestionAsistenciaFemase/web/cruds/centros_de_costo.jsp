
<%@page import="cl.femase.gestionweb.vo.DispositivoVO"%>
<%@page import="java.util.HashMap"%>
<%@page import="cl.femase.gestionweb.vo.UsuarioVO"%>
<%@page import="cl.femase.gestionweb.vo.ComunaVO"%>
<%@page import="cl.femase.gestionweb.vo.DepartamentoVO"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="cl.femase.gestionweb.common.Constantes"%>
<%@page import="cl.femase.gestionweb.vo.CentroCostoVO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>

<%
    String crudTitle = "Administracion Centros de Costo"; 
    String fileTitle = "centros_costo"; 
    String urlPattern   = "CentrosDeCostoCRUD";
    //num columnas
    String columnas = "0,1,2,3,4,5,6,7,8,9,10,11";
    
    UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
    
    String empresaId = userConnected.getEmpresaId();
    
    List<CentroCostoVO> list = (List<CentroCostoVO>)request.getAttribute("lista");
    if (list == null) list = new ArrayList<>();
    
    LinkedHashMap<String, List<DepartamentoVO>> departamentosHash = 
        (LinkedHashMap<String, List<DepartamentoVO>>)session.getAttribute("allDepartamentos");
    
    List<DepartamentoVO> departamentos = departamentosHash.get(empresaId);
    if (departamentos == null) System.out.println("[centro_costo.jsp]departamentos es NULL");
        
    List<ComunaVO> comunas = (List<ComunaVO>)session.getAttribute("comunas");
    
    String filtroDepartamentoId = (String)request.getAttribute("filtroDepartamentoId");
    String filtroNombreCenco = (String)request.getAttribute("filtroNombreCenco");
    int filtroEstadoId = 1;
    
    if (request.getAttribute("filtroEstadoId") != null) filtroEstadoId = (Integer)request.getAttribute("filtroEstadoId");
    
    if (filtroNombreCenco == null) filtroNombreCenco = "";
    if (filtroDepartamentoId == null) filtroDepartamentoId = "RM";
    if (request.getAttribute("filtroEstadoId") == null) filtroEstadoId = 1;
%>

<!doctype html>

<html lang="es">
    <head>
	<meta charset="UTF-8">
        <meta name="description" content="CRUD - <%=crudTitle%>" />
    <title><%=crudTitle%>-CRUD</title>
    
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
                //$('#newRowModalForm').modal('show');
                //document.getElementById("labelForm").value='Crear Registro';
                document.getElementById("action").value='create';
                document.getElementById('id').readOnly= true;
                
                document.getElementById("id").value = '';//textbox
                document.getElementById("deptoId").value = '-1';//combobox
                document.getElementById("nombre").value = '';//textbox
                document.getElementById("estado").value='1';//combobox
                document.getElementById("direccion").value='';//textbox
                document.getElementById("comunaId").value='-1';//combobox
                document.getElementById("telefonos").value='';//textbox
                document.getElementById("email").value='';//textbox
                document.getElementById("emailNotificacion").value='';//textbox
                document.getElementById("zonaExtrema").value='N';//combobox
        
                var botonEliminar = document.getElementById("deleteButton");
                botonEliminar.disabled = true;
                $("#editModalForm").modal("show");
            });
	});
        
        // code to read selected table row cell data (values).
        $("#myTable").on('click','.btnSelect',function(){
            // get the current row
            var currentRow=$(this).closest("tr"); 

            var idCenco = currentRow.find("td:eq(0)").text();//id cenco
            var nombreCenco = currentRow.find("td:eq(1)").text();//nombre cenco
            var idEstado = currentRow.find("td:eq(2)").text();//id estado
            var direccion = currentRow.find("td:eq(4)").text();//direccion
            var idComuna = currentRow.find("td:eq(5)").text();//id comuna
            var telefonos = currentRow.find("td:eq(7)").text();//telefonos
            var emailGral = currentRow.find("td:eq(8)").text();//email gral
            var emailNotificacion = currentRow.find("td:eq(9)").text();//email notificacion
            var dispositivos = currentRow.find("td:eq(10)").text();//dispositivos separados por '|'
            var zonaExtrema = currentRow.find("td:eq(11)").text();//es zona extrema {'S': Si, 'N': No}
           
            document.getElementById("action").value='update';
            setValuesToEdit(idCenco, nombreCenco, idEstado, direccion,idComuna,telefonos,emailGral,emailNotificacion,dispositivos,zonaExtrema);
        });
        
        $("#myTable").on('click','.btnSelectDelete',function(){
            // get the current row
            var currentRow=$(this).closest("tr"); 

            var idCenco = currentRow.find("td:eq(0)").text();//id cenco
            var nombreCenco = currentRow.find("td:eq(1)").text();//nombre cenco
            var idEstado = currentRow.find("td:eq(2)").text();//id estado
            var direccion = currentRow.find("td:eq(4)").text();//direccion
            var idComuna = currentRow.find("td:eq(5)").text();//id comuna
            var telefonos = currentRow.find("td:eq(7)").text();//telefonos
            var emailGral = currentRow.find("td:eq(8)").text();//email gral
            var emailNotificacion = currentRow.find("td:eq(9)").text();//email notificacion
            var dispositivos = currentRow.find("td:eq(10)").text();//dispositivos separados por '|'
            var zonaExtrema = currentRow.find("td:eq(11)").text();//es zona extrema {'S': Si, 'N': No}
            
            document.getElementById("action").value='delete';
            setValuesToEdit(idCenco, nombreCenco, idEstado, direccion,idComuna,telefonos,emailGral,emailNotificacion,dispositivos,zonaExtrema);
            
        });
        
        $("#datepicker").datepicker({ 
            autoclose: true, 
            todayHighlight: true,
            language: "es"
        }).datepicker('update', new Date());
                                    
        document.getElementById("filtroEmpresaId").value='<%=empresaId%>';
        document.getElementById("filtroNombreCenco").value='<%=filtroNombreCenco%>';
        document.getElementById("filtroDepartamentoId").value='<%=filtroDepartamentoId%>';
        document.getElementById("filtroEstadoId").value='<%=filtroEstadoId%>';
         
    } );

    /**
    * 
    * */
    function setValuesToEdit(idCenco, nombreCenco, idEstado, direccion,idComuna,telefonos,emailGral,emailNotificacion,dispositivos,zonaExtrema){
        var departamentoBuscar  = document.searchForm.filtroDepartamentoId;
        var nombreCencoBuscar   = document.searchForm.filtroNombreCenco;
        var estadoBuscar        = document.searchForm.filtroEstadoId;
        
        document.editNewForm.filtroDepartamentoId.value = departamentoBuscar.value;
        document.editNewForm.filtroNombreCenco.value = nombreCencoBuscar.value;
        document.editNewForm.filtroEstadoId.value = estadoBuscar.value;
        
        var idDelete = document.getElementById("idDelete");
        
        var inputDeptoId = document.getElementById("deptoId");
        var inputId = document.getElementById("id");
        var inputNombre = document.getElementById("nombre");
        var selectEstado = document.getElementById("estado");
        var inputDireccion = document.getElementById("direccion");
        var selectComuna = document.getElementById("comunaId");
        var inputTelefonos=document.getElementById("telefonos");
        var inputEmail=document.getElementById("email");
        var inputEmailNotificacion=document.getElementById("emailNotificacion");
        var selectZonaExtrema = document.getElementById("zonaExtrema");
        var inputDispositivos = document.getElementById("dispositivos");
        
        var botonGuardar = document.getElementById("saveButton");
        var botonEliminar = document.getElementById("deleteButton");
        
        idDelete.value = idCenco;
        inputId.value = idCenco;
        inputNombre.value = nombreCenco;
        selectEstado.value = idEstado;
        inputDireccion.value = direccion;
        inputDeptoId.value = departamentoBuscar.value;
        selectComuna.value = idComuna;
        inputTelefonos.value = telefonos;
        inputEmail.value=emailGral;
        inputEmailNotificacion.value=emailNotificacion;
        selectZonaExtrema.value = zonaExtrema;
        inputDispositivos.value = dispositivos;

        inputId.readOnly = true;
        inputDeptoId.readOnly = true;
        inputNombre.readOnly = false;
        selectEstado.readOnly = false;
        inputDireccion.readOnly = false;
        selectComuna.readOnly = false;
        inputTelefonos.readOnly = false;
        inputEmail.readOnly = false;
        inputEmailNotificacion.readOnly = false;
        selectZonaExtrema.readOnly = false;
        inputDispositivos.readOnly = false;
        
        botonGuardar.disabled = false;
        botonEliminar.disabled = true;
        if (document.getElementById("action").value === 'delete'){
            
            inputId.readOnly = true;
            inputNombre.readOnly = true;
            selectEstado.disabled = true;
            inputDireccion.readOnly = false;
            selectComuna.disabled = true;
            inputTelefonos.readOnly = false;
            inputEmail.readOnly = false;
            inputEmailNotificacion.readOnly = false;
            selectZonaExtrema.disabled = true;
            inputDispositivos.readOnly = false;
        
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
<form name="searchForm" id="searchForm" method="POST" action="<%=request.getContextPath()%>/<%=urlPattern%>?action=list" target="_self">
  <tr>
    <td width="10%">Departamento</td>
    <td width="17%">
        <select id="filtroDepartamentoId" name="filtroDepartamentoId" style="width:150px;" required>
        <%
                Iterator<DepartamentoVO> iteradeptos = departamentos.iterator();
                while(iteradeptos.hasNext() ) {
                    DepartamentoVO departamento = iteradeptos.next();
                    %>
                    <option value="<%=departamento.getId()%>"><%=departamento.getNombre()%></option>
        <%
                }
            %>
        </select>
      </td>
    <td width="15%">Nombre:</td>
    <td width="25%"><input type="text" name="filtroNombreCenco" id="filtroNombreCenco" /></td>
    
    <td width="15%">Estado:</td>
    <td width="25%">
        <select name="filtroEstadoId" id="filtroEstadoId">
            <option value="1">Vigente</option>
            <option value="2">No Vigente</option>
        </select>
    </td>
    
    <td width="16%">&nbsp;</td>
    <td width="17%"><input type="submit" name="buscar" id="buscar" value="Buscar" /></td>
  </tr>
  </form>
</table>
    
  <!-- Inicio tabla con datos -->  
   <div class="container">
  

  <div class="row">
    
    <div class="col-12" >
      <h3 class="titulo-tabla"><%=crudTitle%> </h3>
      
      
      <table id="myTable" class="table table-striped table-bordered" style="width:100%">
        <thead>
            <tr>
                <th>Id Cenco</th>
                <th>Nombre</th>
                <th>Estado Id</th>
                <th>Estado</th>
                <th>Direccion</th>
                <th>Id Comuna</th>
                <th>Comuna</th>
                <th>Telefonos</th>
                <th>Email Gral</th>
                <th>Email notificacion</th>
                <th>Dispositivos</th>
                <th>�Zona extrema?</th>
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
        Iterator<CentroCostoVO> iter = list.iterator();
        CentroCostoVO cenco = null;
        while (iter.hasNext()) {
            cenco = iter.next();
            HashMap<String, DispositivoVO> dispositivos = cenco.getDispositivos();
            String strDevices = "";
            for (String clave:dispositivos.keySet()) {
                DispositivoVO itDevice = dispositivos.get(clave);
                strDevices += itDevice.getId() + "<br>";
            }
            if (strDevices.indexOf("<br>") > 0){
                strDevices = strDevices.substring(0, strDevices.length() - 4);
            }
            
        %>
            
            <tr>
                <td><%= cenco.getId()%></td>
                <td><%= cenco.getNombre()%></td>
                <td><%= cenco.getEstado()%></td>
                <td><%= cenco.getEstadoNombre()%></td>
                <td><%= cenco.getDireccion()%></td>
                <td><%= cenco.getComunaId()%></td>
                <td><%= cenco.getComunaNombre()%></td>
                <td><%= cenco.getTelefonos()%></td>
                <td><%= cenco.getEmail()%></td>
                <td><%= cenco.getEmailNotificacion()%></td>
                <td><%= cenco.getDispositivosAsString()%></td>
                <td><%= cenco.getZonaExtrema()%></td>
                <td>
                    <!--<button type="button" class="btn btn-primary btn-xs dt-edit" style="margin-right:16px;">
                        <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
                    </button>-->
                    <button class="btnSelect btn btn-primary btn-xs" style="margin-right:16px;">
                        <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
                    </button>
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
                    <div class="col">ID<input type="text" class="form-control" name="id" id="id" size="10">
                    </div>
                    <div class="col">Depto ID<input type="text" class="form-control" name="deptoId" id="deptoId" size="10">
                    </div>
                </div>
            	<!-- fin fila -->
                
                <!-- Inicio nueva fila -->    
                <div class="row">
                    <div class="col">Nombre<input type="text" class="form-control" name="nombre" id="nombre" size="20">
                    </div>
                </div>
            	<!-- fin fila -->
                
                <!-- Inicio nueva fila -->    
                <div class="row">
                    <div class="col">
                        Estado
                        <select id="estado" name="estado" style="width:150px;" required>
                            <option value="1" selected>Vigente</option>
                            <option value="2">No Vigente</option>
                        </select>
                    </div>
                    <div class="col">
                        Zona extrema?
                        <select name="zonaExtrema" id="zonaExtrema">
                            <option value="S">Si</option>
                            <option value="N" selected>No</option>
                        </select>
                    </div>
                </div>
            	<!-- fin fila -->
                    
                <!-- Inicio nueva fila -->    
                <div class="row">    
                    <div class="col">Direccion<input type="text" class="form-control" name="direccion" id="direccion">
                    </div>
                </div>
            	<!-- fin fila -->
                <!-- Inicio nueva fila -->    
                <div class="row">
                    <div class="col">Comuna
                        <select name="comunaId" id="comunaId">
                            <%
                            for (int i = 0; i < comunas.size(); i++) {
                                ComunaVO comuna = comunas.get(i);
                                String label = comuna.getNombre() + "[" + comuna.getRegionShortName() + "]";
                            %>
                            <option value="<%=comuna.getId()%>"><%=label%></option>
                            <%}%>
                        </select>
                    </div>
                    <div class="col">Telefonos<input type="text" class="form-control" name="telefonos" id="telefonos">
                    </div>
                 </div>
                 <!-- fin fila -->
                 
                 <!-- Inicio nueva fila -->
                 <div class="row">       
                    <div class="col">E-mail<input type="text" class="form-control" name="email" id="email">
                    </div>
                 </div>
                 <!-- fin fila -->
                 
                 <!-- Inicio nueva fila -->
                 <div class="row">
                     <div class="col">E-mail notificacion<input type="text" class="form-control" name="emailNotificacion" id="emailNotificacion">
                    </div>
                 </div>
                 <!-- fin fila -->
                 
                 <!-- Inicio nueva fila -->
                 <div class="row">       
                    <div class="col">Dispositivos <a href='javascript:;' >[Modificar]</a><input type="text" class="form-control" name="dispositivos" id="dispositivos">
                    </div>    
                 </div>   
                 <!-- fin fila -->       
                
            </div>
            <div class="modal-footer border-top-0 d-flex justify-content-center">
                <input type="hidden" name="action" id="action" value="update">
                <input type="hidden" name="filtroEmpresaId" id="filtroEmpresaId">
                <input type="hidden" name="filtroDepartamentoId" id="filtroDepartamentoId">
                <input type="hidden" name="filtroNombreCenco" id="filtroNombreCenco">
                <input type="hidden" name="filtroEstadoId" id="filtroEstadoId">

                <input type="hidden" name="idDelete" id="idDelete">

                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                <button id="saveButton" type="submit" class="btn btn-primary">Guardar cambios</button>
                <button id="deleteButton" type="submit" class="btn btn-primary">Eliminar</button>
            
            </div>
            </div>
        </div>
    </div>
</form>

<!-- Fin modal para editar registro -->


</body>
</html>
