<%@ include file="/include/check_session.jsp" %>

<%@page import="cl.femase.gestionweb.vo.TipoAusenciaVO"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.LinkedHashMap"%>

<%
    List<TipoAusenciaVO> tiposAusencia = 
        (List<TipoAusenciaVO>)session.getAttribute("tipos_ausencia");
       
%>
<!DOCTYPE html>

<html>
<head>
    <meta charset="utf-8" />

    <meta name="keywords" content="femase,gestion,web">
    <meta name="description" content="Sistema de Gestion FEMASE-RRHH-Control Asistencia">
    <meta name="author" content="Adiazr">

    <title>Admin Ausencias</title>

    <link href="../Jquery-JTable/Content/normalize.css" rel="stylesheet" type="text/css" />
    <link href='<%=request.getContextPath()%>/css-varios/googleapis.css' rel='stylesheet' type='text/css'>
    <link href="../Jquery-JTable/Content/Site.metro.css" rel="stylesheet" type="text/css" />

    <link href="../Jquery-JTable/Content/highlight.css" rel="stylesheet" type="text/css" />

        <link href="../Jquery-JTable/Content/themes/metroblue/jquery-ui.css" rel="stylesheet" type="text/css" />
        <link href="../Jquery-JTable/Scripts/jtable/themes/metro/blue/jtable.css" rel="stylesheet" type="text/css" />

    <link href="../Jquery-JTable/Scripts/syntaxhighligher/styles/shCore.css" rel="stylesheet" type="text/css" />
    <link href="../Jquery-JTable/Scripts/syntaxhighligher/styles/shThemeDefault.css" rel="stylesheet" type="text/css" />

    
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

    </script>
</head>
<body>
    <div class="site-container">
        <div class="main-header" style="position: relative">
            <h1>Administraci&oacute;n de Ausencias (tipos)</h1>
            <h2>Filtros de b&uacute;squeda</h2>
        </div>
<div class="content-container">
            
<div class="padded-content-container">
                
<div class="filtering">
    <form>
        <label>Nombre: <input type="text" name="paramNombre" id="paramNombre" /></label>
        <label>Tipo:
            <select name="paramTipo" id="paramTipo">
            <option value="-1" selected>----------</option>
            <%
                Iterator<TipoAusenciaVO> iteratipos = tiposAusencia.iterator();
                while(iteratipos.hasNext() ) {
                    TipoAusenciaVO auxtipo = iteratipos.next();
                    %>
                    <option value="<%=auxtipo.getId()%>"><%=auxtipo.getNombre()%></option>
                    <%
                }
            %>
            </select>
        </label>
        <label>Estado:
            <select name="paramEstado" id="paramEstado">
            <option value="-1" selected>----------</option>
                <option value="1">Vigente</option>
                <option value="2">No Vigente</option>
            </select>
        </label>
        <label>ï¿½Justifica horas?
            <select name="paramJustificaHrs" id="paramJustificaHrs">
            <option value="-1" selected>Todas</option>
                <option value="S">Si</option>
                <option value="N">No</option>
            </select>
        </label>
        <label>ï¿½Pagada por el empleador?
            <select name="paramPagadaPorEmpleador" id="paramPagadaPorEmpleador">
            <option value="-1" selected>Todas</option>
                <option value="S">Si</option>
                <option value="N">No</option>
            </select>
        </label>
        <button type="submit" id="LoadRecordsButton">Buscar</button>
    </form>
</div>

<div id="AusenciasTableContainer"></div>
<script type="text/javascript">

    $(document).ready(function () {

        $('#AusenciasTableContainer').jtable({       
            title: 'Tipos Ausencia',
            paging: true, //Enable paging
            pageSize: 10, //Set page size (default: 10)
            sorting: true, //Enable sorting
            defaultSorting: 'ausencia_nombre ASC', //Set default sorting
            actions: {
                listAction: '<%=request.getContextPath()%>/AusenciasController?action=list',
                createAction:'<%=request.getContextPath()%>/AusenciasController?action=create',
                updateAction: '<%=request.getContextPath()%>/AusenciasController?action=update'
            },
            fields: {
                id: {
                    title:'Id',
                    width: '4%',
                    key: true,
                    list: true,
                    create:false
                },
                nombre:{
                    title: 'Nombre',
                    width: '20%',
                    edit:true,
                    create:true
                },
                nombreCorto:{
                    title: 'Nombre corto',
                    width: '20%',
                    edit:true,
                    create:true,
                    sorting: false
                },
                tipoId:{
                    title: 'Tipo',
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=tipos_ausencia';
                    },
                    width: '10%',
                    edit:true,
                    create:true
                },
                estado:{
                    title: 'Estado',
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=estados';
                    },
                    width: '16%',
                    edit:true,
                    create:true
                },
                justificaHoras: {
                    title: 'ï¿½Justifica Hrs.?',
                    width: '15%',
                    type: 'radiobutton',
                    options: { 'S': 'Si', 'N': 'No' },
                    defaultValue: 'S',
                    create:true,
                    edit:true,
                    sorting: false
                },
                pagadaPorEmpleador: {
                    title: '¿Pagada por empleador?',
                    width: '15%',
                    type: 'radiobutton',
                    options: { 'S': 'Si', 'N': 'No' },
                    defaultValue: 'S',
                    create:true,
                    edit:true,
                    sorting: false
                }    
            }
        });

        //Re-load records when user click 'load records' button.
        $('#LoadRecordsButton').click(function (e) {
            e.preventDefault();
            $('#AusenciasTableContainer').jtable('load', {
                paramNombre: $('#paramNombre').val(),
                paramTipo: $('#paramTipo').val(),
                paramEstado: $('#paramEstado').val(),
                paramJustificaHrs: $('#paramJustificaHrs').val(),
                paramPagadaPorEmpleador: $('#paramPagadaPorEmpleador').val()
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
