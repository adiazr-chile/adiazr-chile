<%@ include file="/include/check_session.jsp" %>

<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.LinkedHashMap"%>

<!DOCTYPE html>

<html>
<head>
    <meta charset="utf-8" />

    <meta name="keywords" content="femase,gestion,web">
    <meta name="description" content="Sistema de Gestion FEMASE-RRHH-Control Asistencia">
    <meta name="author" content="Adiazr">

    <title>CRUD- Ciclos turnos rotativos</title>

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
   
</head>
<body>
    <div class="site-container">
        <div class="main-header" style="position: relative">
            <h1>Ciclos - Turnos Rotativos<span class="light"></span></h1>
            <h2>Registros existentes</h2>
        </div>
<div class="content-container">
            
            <div class="padded-content-container">
                
<div class="filtering">
    <form>
        <label>Etiqueta <input type="text" name="etiqueta" id="etiqueta" /></label>
        <input name="numDias" type="hidden" id="numDias" value="0">
        
        <button type="submit" id="LoadRecordsButton">Buscar</button>
    </form>
</div>

<div id="CiclosTableContainer"></div>
<script type="text/javascript">

    $(document).ready(function () {

        $('#CiclosTableContainer').jtable({       
            title: 'Ciclos',
            paging: true, //Enable paging
            pageSize: 10, //Set page size (default: 10)
            sorting: true, //Enable sorting
            defaultSorting: 'ciclo_correlativo ASC', //Set default sorting
            actions: {
                listAction: '<%=request.getContextPath()%>/servlet/CiclosTurnosRotativosController?action=list',
                createAction:'<%=request.getContextPath()%>/servlet/CiclosTurnosRotativosController?action=create',
                updateAction: '<%=request.getContextPath()%>/servlet/CiclosTurnosRotativosController?action=update',
                deleteAction: '<%=request.getContextPath()%>servlet/CiclosTurnosRotativosController?action=delete'
            },
            fields: {
                correlativo: {
                    title:'Correlativo',
                    width: '10%',
                    key: true,
                    list: true,
                    create:false
                },
                empresaId:{
                    title: 'Empresa',
                    width: '20%',
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=empresas';
                    },
                    sorting: false
                },
                etiqueta:{
                    title: 'Etiqueta',
                    width: '30%',
                    edit:true
                },
                numDias:{
                    title: 'Num de dias',
                    width: '10%',
                    edit:true,
                    options: {'1': '1', '2': '2', '3': '3', '4': '4', '5': '5', '6': '6', '7': '7', '8': '8', '9': '9', '10': '10', 
                        '11': '11', '12': '12', '13': '13', '14': '14', '15': '15', '16': '16', '17': '17', '18': '18', '19': '19', '20': '20',
                        '21': '21', '22': '22', '23': '23', '24': '24', '25': '25', '26': '26', '27': '27', '28': '28', '29': '29', '30': '30'}
                },
                fechaCreacion:{
                    title: 'Fecha creacion',
                    width: '15%',
                    create:false,
                    edit:false,
                    sorting: false
                },
                fechaModificacion:{
                    title: 'Fecha modificacion',
                    width: '15%',
                    create:false,
                    edit:false,
                    sorting: false
                }    
            }
        });

        //Re-load records when user click 'load records' button.
        $('#LoadRecordsButton').click(function (e) {
            e.preventDefault();
            $('#CiclosTableContainer').jtable('load', {
                etiqueta: $('#etiqueta').val(),
                numDias: $('#numDias').val()
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
