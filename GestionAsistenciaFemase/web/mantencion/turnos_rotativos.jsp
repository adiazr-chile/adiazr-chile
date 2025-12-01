<%@ include file="/include/check_session.jsp" %>
<%@page import="cl.femase.gestionweb.vo.EmpresaVO"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.LinkedHashMap"%>

<%
    List<EmpresaVO> empresas = (List<EmpresaVO>)session.getAttribute("empresas");
%>
<!DOCTYPE html>

<html>
<head>
    <meta charset="utf-8" />
    <meta name="keywords" content="femase,gestion,web">
    <meta name="description" content="Sistema de Gestion FEMASE-RRHH-Control Asistencia">
    <meta name="author" content="Adiazr">

    <title>Admin Turnos Rotativos</title>

    <link href="../Jquery-JTable/Content/normalize.css" rel="stylesheet" type="text/css" />
    <link href='<%=request.getContextPath()%>/css-varios/googleapis.css' rel='stylesheet' type='text/css'>
    <link href="../Jquery-JTable/Content/Site.metro.css" rel="stylesheet" type="text/css" />
    <link href="../Jquery-JTable/Content/highlight.css" rel="stylesheet" type="text/css" />
    <link href="../Jquery-JTable/Content/themes/metroblue/jquery-ui.css" rel="stylesheet" type="text/css" />
    <link href="../Jquery-JTable/Scripts/jtable/themes/metro/blue/jtable.css" rel="stylesheet" type="text/css" />
    <link href="../Jquery-JTable/Scripts/syntaxhighligher/styles/shCore.css" rel="stylesheet" type="text/css" />
    <link href="../Jquery-JTable/Scripts/syntaxhighligher/styles/shThemeDefault.css" rel="stylesheet" type="text/css" />
    
    <!-- estilo para botones -->
    <!--<link rel="stylesheet" href="../jquery-plugins/chosen_v1.6.2/docsupport/style.css">-->
    
    <style>
        div.filtering
        {
            border: 1px solid #999;
            margin-bottom: 5px;
            padding: 10px;
            background-color: #EEE;
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

        function openEditForm(intid){
            //alert('idturno: '+strid);
            document.location.href='<%=request.getContextPath()%>/TurnosRotativosController?action=edit&idTurno='+intid; 
            //document.location.href='<%=request.getContextPath()%>/mantencion/new_editdetalle_turno.jsp?filtroId='+strid; 
        }
        
        function openAsignacionForm(idturno){
            document.location.href=
                '<%=request.getContextPath()%>/TurnosRotativosController?action=asignacion_start&idTurno='+idturno; 
	}
        
        /*
        function crearTurno(){
            document.location.href='<%=request.getContextPath()%>/TurnosRotativosController?action=create';
	}
        */
    </script>
</head>
<body>
    <div class="site-container">
        <div class="main-header" style="position: relative">
            <h1>Administraci&oacute;n de Turnos Rotativos</h1>
            <h2>Filtros de b&uacute;squeda</h2>
        </div>
    <div class="content-container">
    <div class="padded-content-container">
    <div class="filtering">
        <form>
            <label>Empresa
                <select name="filtroEmpresa" id="filtroEmpresa">
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
            <label>Nombre: <input type="text" name="filtroNombre" id="filtroNombre" /></label>
            <label for="estado">Estado</label>
                <select id="filtroEstado" name="filtroEstado" 
                           class="chosen-select" style="width:150px;" tabindex="2">
                        <option value="-1" selected>Cualquiera</option>
                        <option value="1" >Vigente</option>
                        <option value="2">No Vigente</option>
                    </select>
            
            <button type="submit" class="button button-blue" id="LoadRecordsButton">Buscar</button>
            <!-- <input name="botoncrear" type="button" value="Nuevo Turno" class="button button-blue" onclick="crearTurno();">-->
        </form>
    </div>

<div id="TurnosRotTableContainer"></div>
<script type="text/javascript">

    $(document).ready(function () {

        $('#TurnosRotTableContainer').jtable({       
            title: 'Turnos Rotativos',
            paging: true, //Enable paging
            pageSize: 10, //Set page size (default: 10)
            sorting: true, //Enable sorting
            defaultSorting: 'nombre_turno ASC', //Set default sorting
            actions: {
                listAction: '<%=request.getContextPath()%>/TurnosRotativosController?action=list',
                createAction:'<%=request.getContextPath()%>/TurnosRotativosController?action=create',
                updateAction: '<%=request.getContextPath()%>/TurnosRotativosController?action=update'
            },
            fields: {
                empresaId:{
                    title: 'Empresa',
                    list: true,
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=empresas'
                    },
                    width: '15%',
                    edit:true,
                    create:true,
                    sorting:false
                },
                id: {
                    title:'Id',
                    width: '8%',
                    key: true,
                    list: true,
                    create:false
                },
                nombre:{
                    title: 'Nombre',
                    width: '29%',
                    edit:true,
                    create:true
                },
                horaEntrada:{
                    title: 'Hora Entrada',
                    width: '10%',
                    edit:true,
                    create:true,
                    sorting: true
                },
                horaSalida:{
                    title: 'Hora Salida',
                    width: '10%',
                    edit:true,
                    create:true,
                    sorting: true
                },
                holgura:{
                    title: 'Holgura(mins)',
                    width: '10%',
                    edit:true,
                    create:true,
                    sorting: false
                },
                minutosColacion:{
                    title: 'Mins Colacion',
                    width: '10%',
                    edit:true,
                    create:true,
                    sorting: false
                },
                aplicarTodos: {
                    title: '�Aplicar a todos los turnos?',
                    width: '7%',
                    type: 'radiobutton',
                    options: { 'S': 'Si', 'N': 'No' },
                    inputClass: 'validate[required]',
                    defaultValue: 'N',
                    create:false,
                    edit:true,
                    list: false,
                    sorting: false
                },
                nocturno:{
                    title: '�Nocturno?',
                    width: '9%',
                    edit:true,
                    create:true,
                    type: 'radiobutton',
                    options: { 'S': 'Si', 'N': 'No' },
                    sorting: false
                },
                permiteFeriado:{
                    title: 'Permite feriado?',
                    width: '10%',
                    edit:true,
                    create:true,
                    type: 'radiobutton',
                    options: { 'S': 'Si', 'N': 'No' },
                    sorting: false
                },  
                estado:{
                    title: 'Estado',
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=estados';
                    },
                    width: '8%',
                    edit:true,
                    create:true,
                    sorting: false
                },
                fechaCreacionAsStr:{
                    title: 'Fecha Creacion',
                    width: '10%',
                    edit:false,
                    create:false
                },
                fechaModificacionAsStr:{
                    title: 'Fecha Modificacion',
                    width: '10%',
                    create:false,
                    edit:false
                }
                /*
                ,
                MyButton: {
                    title: 'Editar',
                    width: '11%',
                    sorting: false,
                    create:false,
                    edit:false,
                    display: function(data) {
                        return '<button type="button" onclick="openEditForm(' + data.record.id + ')" class="button button-blue">Detalle</button> ';
                    }
                } 
                */
            }
        });

        //Re-load records when user click 'load records' button.
        $('#LoadRecordsButton').click(function (e) {
            e.preventDefault();
            $('#TurnosRotTableContainer').jtable('load', {
                filtroEmpresa: $('#filtroEmpresa').val(),
                filtroNombre: $('#filtroNombre').val(),
                filtroEstado: $('#filtroEstado').val()
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

