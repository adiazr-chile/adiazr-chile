
<%@page import="cl.femase.gestionweb.vo.CentroCostoVO"%>
<%@page import="cl.femase.gestionweb.vo.DepartamentoVO"%>
<%@page import="cl.femase.gestionweb.vo.EmpresaVO"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>

<%
    List<EmpresaVO> empresas = (List<EmpresaVO>)session.getAttribute("empresas");
    //List<DepartamentoVO> departamentos   = (List<DepartamentoVO>)session.getAttribute("departamentos");
    //List<CentroCostoVO> cencos   = (List<CentroCostoVO>)session.getAttribute("cencos");
    String empresaSelected = (String)session.getAttribute("empresaSelected");
    String deptoSelected = (String)session.getAttribute("deptoSelected");
    String cencoSelected = (String)session.getAttribute("cencoSelected");
    
    System.out.println("[lista_empleados.jsp]"
        + "empresaSelected= "+empresaSelected
        +", deptoSelected= "+deptoSelected
        +", cencoSelected= "+cencoSelected);
%>

<!DOCTYPE html>

<html>
<head>
    <meta charset="Content-Type: text/html; charset=iso-8859-1" />

    <meta name="keywords" content="femase,gestion,web">
    <meta name="description" content="Sistema de Gestion FEMASE-RRHH-Control Asistencia">
    <meta name="author" content="Adiazr">

    <title>jTable.org - A JQuery plugin to create AJAX based CRUD tables - Filtering Demo</title>

    <link href="../Jquery-JTable/Content/normalize.css" rel="stylesheet" type="text/css" />
    <link href='<%=request.getContextPath()%>/css-varios/googleapis.css' rel='stylesheet' type='text/css'>
    <link href="../Jquery-JTable/Content/Site.metro.css" rel="stylesheet" type="text/css" />

    <link href="../Jquery-JTable/Content/highlight.css" rel="stylesheet" type="text/css" />

        <link href="../Jquery-JTable/Content/themes/metroblue/jquery-ui.css" rel="stylesheet" type="text/css" />
        <link href="../Jquery-JTable/Scripts/jtable/themes/metro/blue/jtable.css" rel="stylesheet" type="text/css" />

    <link href="../Jquery-JTable/Scripts/syntaxhighligher/styles/shCore.css" rel="stylesheet" type="text/css" />
    <link href="../Jquery-JTable/Scripts/syntaxhighligher/styles/shThemeDefault.css" rel="stylesheet" type="text/css" />

	<!-- estilo para botones -->
	<link rel="stylesheet" href="../jquery-plugins/chosen_v1.6.2/docsupport/style.css">
    
    <style>
        div.filtering
        {
            border: 1px solid #999;
            margin-bottom: 5px;
            padding: 10px;
            background-color: #EEE;
        }
        
        body {
            background-color: #fafafa;
            font-size: 10pt;
            font-style: normal;
            color: #06C
        }
    </style>

    <style>
    select {
        color:black !important;
    }
</style>

    <script src="../Jquery-JTable/Scripts/modernizr-2.6.2.js" type="text/javascript"></script>
    <script src="../Jquery-JTable/Scripts/jquery-1.9.1.min.js" type="text/javascript"></script>
    <script src="../Jquery-JTable/Scripts/jquery-ui-1.10.0.min.js" type="text/javascript"></script>
    <script src="../Jquery-JTable/Scripts/syntaxhighligher/shCore.js" type="text/javascript"></script>
    <script src="../Jquery-JTable/Scripts/syntaxhighligher/shBrushJScript.js" type="text/javascript"></script>
    <script src="../Jquery-JTable/Scripts/syntaxhighligher/shBrushXml.js" type="text/javascript"></script>
    <script src="../Jquery-JTable/Scripts/syntaxhighligher/shBrushCSharp.js" type="text/javascript"></script>
    <script src="../Jquery-JTable/Scripts/syntaxhighligher/shBrushSql.js" type="text/javascript"></script>
    <script src="../Jquery-JTable/Scripts/syntaxhighligher/shBrushPhp.js" type="text/javascript"></script>
    <script src="../Jquery-JTable/Scripts/jtablesite.js" type="text/javascript"></script>
    <script src="../Jquery-JTable/Scripts/jtable/jquery.jtable.js" type="text/javascript"></script>
    <script src="../Jquery-JTable/Scripts/jtable/jquery.jtable.es.js" type="text/javascript"></script>
    
    <script type="text/javascript">
       
        $(document).ready(function() {
             $('#empresaId').change(function(event) {
                 var empresaSelected = $("select#empresaId").val();
                 $.get('<%=request.getContextPath()%>/JsonListServlet', {
                         empresaID : empresaSelected
                 }, function(response) {
                        var select = $('#deptoId');
                        select.find('option').remove();
                        var newoption = "";
                        newoption += "<option value='-1'>Seleccione Departamento</option>";
                        for (i=0; i<response.length; i++) {
                            newoption += "<option value='"+response[i].id+"'>"+response[i].nombre+"</option>";
                        }
                        $('#deptoId').html(newoption);
                 });
             });
             //evento para combo depto
             $('#deptoId').change(function(event) {
                 var empresaSelected = $("select#empresaId").val();
                 var deptoSelected = $("select#deptoId").val();
                 $.get('<%=request.getContextPath()%>/JsonListServlet', {
                         empresaID : empresaSelected,deptoID : deptoSelected
                 }, function(response) {
                        var select = $('#cencoId');
                        select.find('option').remove();
                        var newoption = "";
                        newoption += "<option value='-1'>Seleccione Centro de costo</option>";
                        for (i=0; i<response.length; i++) {
                            newoption += "<option value='"+response[i].id+"'>"+response[i].nombre+"</option>";
                        }
                        $('#cencoId').html(newoption);
                 });
             });
         });
         
         $("thead").height("50px");
         
        function crearEmpleado(){
            //document.location.href='<%=request.getContextPath()%>/mantencion/crear_empleado.jsp';
            document.location.href='<%=request.getContextPath()%>/jqueryform-empleado/form_crear_empleado.jsp';
	}
        
        function openEditForm(strrut){
            document.location.href='<%=request.getContextPath()%>/EmpleadosController?action=edit&rutEmpleado='+strrut; 
        }
    </script>
</head>
<body>
    <div class="site-container">
        <div class="main-header" style="position: relative">
            <h1>Lista de Empleados</h1>
            
        </div>
	<div class="content-container">
            
            <div class="padded-content-container">
                
    <!-- Filtros de busqueda INICIO-->                
    <div class="filtering">
        <form>
            <label>Empresa
                <select name="empresaId" id="empresaId">
                <option value="-1" selected>----------</option>
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
            </label>
                
            <label for="deptoId">Departamento</label>
            <select id="deptoId" name="deptoId" style="width:350px;" required>
                <option value="-1">--------</option>
            </select>
                
            <label>Centro Costo
                <select name="cencoId" id="cencoId">
                    <option value="-1" selected>----------</option>
                </select>
            </label>    

                <label>Rut: <input type="text" name="rutEmpleado" id="rutEmpleado" /></label>
            <label>Nombres: 
              <input type="text" name="nombres" id="nombres" /></label>
            <label>Paterno: 
              <input type="text" name="apePaterno" id="apePaterno" /></label>
            <label>Estado: 
              <select name="estado" id="estado">
                    <option value="-1" selected>Todos</option>
                    <option value="1">Vigentes</option>
                    <option value="2">No Vigentes</option>
                </select>
            </label>
            
            <!-- Boton Buscar-->  
            <button type="submit" id="LoadRecordsButton" class="button button-blue">Buscar</button>
            <!--<a class="button button-blue" href="javascript:;" onclick="crearEmpleado();">Crear Empleado</a>-->
            <input name="botoncrear" type="button" value="Crear Empleado" class="button button-blue" onclick="crearEmpleado();">
        </form>
    </div>
    <!-- Filtros de busqueda FIN-->

<div id="EmpleadosTableContainer"></div>
<script type="text/javascript">

    $(document).ready(function () {

        $('#EmpleadosTableContainer').jtable({       
            title: 'Empleados',
            paging: true, //Enable paging
            pageSize: 10, //Set page size (default: 10)
            sorting: true, //Enable sorting
            defaultSorting: 'empl_rut ASC', //Set default sorting
            actions: {
                listAction: '<%=request.getContextPath()%>/EmpleadosController?action=list'
                //,createAction:'<%=request.getContextPath()%>/EmpleadosController?action=create',
                //createAction:'<%=request.getContextPath()%>/mantencion/admin_empleados.jsp',
                //updateAction: '<%=request.getContextPath()%>/EmpleadosController?action=edit'
                //,deleteAction: '<%=request.getContextPath()%>/AccesosController?action=delete'
            },
            fields: {
                codInterno: {
                    title:'N° ficha',
                    width: '5%',
                    key: true,
                    list: true,
                    create:true
                },
                rut:{
                    title: 'Rut',
                    width: '7%',
                    edit:true
                },
                nombres:{
                    title: 'Nombres',
                    width: '7%',
                    edit:true
                },
                apePaterno:{
                    title: 'Paterno',
                    width: '6%',
                    edit:true
                },
                apeMaterno:{
                    title: 'Materno',
                    width: '5%',
                    edit:true
                },
                fechaNacimientoAsStr:{
                    title: 'Fec Nac',
                    width: '5%',
                    type: 'text',
                    displayFormat: 'dd-mm-yy',
                    inputClass: 'validate[required,custom[date]]',
                    edit:true
                },
                direccion:{
                    title: 'Dir.',
                    width: '5%',
                    edit:true
                },
                empresaNombre:{
                    title: 'Empresa',
                    width: '7%',
                    edit:true
                },
                deptoNombre:{
                    title: 'Depto',
                    width: '7%',
                    edit:true
                },
                cencoNombre:{
                    title: 'Cenco',
                    width: '7%',
                    edit:true
                },
                idTurno:{
                    title: 'Turno',
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=turnos'
                    },
                    width: '5%',
                    edit:true
                },
                idCargo:{
                    title: 'Cargo',
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=cargos'
                    },
                    width: '5%',
                    edit:true
                },
                autorizaAusencia: {
                    title: 'Autoriza Ausencia',
                    width: '12%',
                    type: 'checkbox',
                    values: { 'false': 'No', 'true': 'Si' },
                    defaultValue: 'false'
                }, 
                articulo22: {
                    title: 'Art. 22',
                    width: '12%',
                    type: 'checkbox',
                    values: { 'false': 'No', 'true': 'Si' },
                    defaultValue: 'false'
                },
                contratoIndefinido: {
                    title: 'Indef.',
                    width: '12%',
                    type: 'checkbox',
                    values: { 'false': 'No', 'true': 'Si' },
                    defaultValue: 'false'
                },
                email:{
                    title: 'Email',
                    width: '9%',
                    edit:true
                },
                fechaInicioContratoAsStr:{
                    title: 'Ini Contrato',
                    width: '5%',
                    type: 'text',
                    displayFormat: 'dd-mm-yy',
                    inputClass: 'validate[required,custom[date]]',
                    edit:true
                },
                fechaTerminoContratoAsStr:{
                    title: 'Fin Contrato',
                    width: '5%',
                    type: 'text',
                    displayFormat: 'dd-mm-yy',
                    inputClass: 'validate[required,custom[date]]',
                    edit:true
                },
                estado:{
                    title: 'Estado',
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=estados'
                    },
                    width: '5%',
                    edit:true
                },
                /*pathFoto:{
                    title: 'Foto',
                    width: '5%',
                    edit:true
                },*/
                sexo:{
                    title: 'Sexo',
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=sexos'
                    },
                    width: '5%',
                    edit:true
                },
                fonoFijo:{
                    title: 'F.Fijo',
                    width: '5%',
                    edit:true
                },
                fonoMovil:{
                    title: 'F.Movil',
                    width: '5%',
                    edit:true
                },
                comunaNombre:{
                    title: 'Comuna',
                    width: '5%',
                    edit:true
                },    
                /*
                    comunaId:{
                    title: 'Comuna',
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=comunas'
                    },
                    width: '5%',
                    edit:true
                },*/
                MyButton: {
                    title: 'Accion',
                    width: '40%',
                    sorting: false,
                    display: function(data) {
                         //return '<button type="button" onclick="openEditForm(' + data.record.rut + ')">update record</button> ';
                         //var mystring = new String(data.record.rutParam);
                         return '<button type="button" onclick="openEditForm(' + data.record.rutParam + ')">update</button> ';
                         //return '<button type="button" onclick="alert(' + data.record.rutParam + ')">update</button> ';
                    }
                }
            },
            toolbar: {
                    sorting: false,
                    items: [{
                            icon: '<%=request.getContextPath()%>/images/icon_csv.gif',
                            text: 'CSV',
                            click: function () {
                                document.location.href='<%=request.getContextPath()%>/DataExportServlet?type=empleadosToCSV';
                            }
                        }
                       /* ,
                        {
                            icon: '<%=request.getContextPath()%>/images/propias_por_mercado_export.png',
                            text: 'Propias por Mercado',
                            click: function () {
                                document.location.href='<%=request.getContextPath()%>/TradesExport?tipo=ownByMarket';
                            }
                        }
                        */
                        
                    ]
            }
        });

        //Re-load records when user click 'load records' button.
        $('#LoadRecordsButton').click(function (e) {
            e.preventDefault();
            $('#EmpleadosTableContainer').jtable('load', {
                empresaId: $('#empresaId').val(),
                deptoId: $('#deptoId').val(),
                cencoId: $('#cencoId').val(),
                rutEmpleado: $('#rutEmpleado').val(),
                nombres: $('#nombres').val(),
                apePaterno: $('#apePaterno').val(),
                estado: $('#estado').val()
            });
        });

        //Load all records when page is first shown
        $('#LoadRecordsButton').click();
    });

</script>
<br />
<hr />
<h3>&nbsp;</h3><div class="tabsContainer"><div id="tabs-webforms"></div>
</div>

            </div>
            
        </div>


        <div class="main-footer" style="position: relative"></div>
    </div>
    
</body>
</html>
<script type="text/javascript">

    $(document).ready(function () {
        
        var $adsContainer = $('#AdsContainer');

        var showHideAds = function () {
            if ($(window).width() < 1070) {
                if ($adsContainer.is(':visible')) {
                    $adsContainer.hide();
                }
            } else {
                if (!$adsContainer.is(':visible')) {
                    $adsContainer.show();
                }
            }
        };
        showHideAds();

        $(window).resize(function () {
            showHideAds();
        });
    });

</script>
