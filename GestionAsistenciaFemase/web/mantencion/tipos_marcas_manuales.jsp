<%@ include file="/include/check_session.jsp" %>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.LinkedHashMap"%>

<!DOCTYPE html>

<html>
<head>
    <meta charset="utf-8" />

    <title>Admin Tipos Marcas Manuales</title>

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
            <h1>Lista de Tipos de marcas manuales<span class="light"></span></h1>
            <h2>Tipos Existentes</h2>
        </div>
<div class="content-container">
            
            <div class="padded-content-container">
                
<div class="filtering">
    <form>
        <label>Nombre: <input type="text" name="nombre" id="nombre" /></label>
        <label>Vigente:
            <select id="vigente" name="vigente" style="width:150px;" tabindex="2" required>
                <option value="-1">Todos</option>
                <option value="S">Si</option>
                <option value="N">No</option>
            </select>
        </label>
        <button type="submit" id="LoadRecordsButton">Buscar</button>
    </form>
</div>

<div id="TMMTableContainer"></div>
<script type="text/javascript">

    $(document).ready(function () {

        $('#TMMTableContainer').jtable({       
            title: 'Tipos',
            paging: true, //Enable paging
            pageSize: 10, //Set page size (default: 10)
            sorting: true, //Enable sorting
            defaultSorting: 'display_order ASC', //Set default sorting
            actions: {
                listAction: '<%=request.getContextPath()%>/servlet/TiposMarcaManualController?action=list',
                createAction:'<%=request.getContextPath()%>/servlet/TiposMarcaManualController?action=create',
                updateAction: '<%=request.getContextPath()%>/servlet/TiposMarcaManualController?action=update'
                //,deleteAction: '<%=request.getContextPath()%>/ModulosSistemaController?action=delete'
            },
            fields: {
                code: {
                    title:'Id',
                    width: '10%',
                    key: true,
                    list: true,
                    create:false,
                    sorting:false
                },
                nombre:{
                    title: 'Nombre',
                    width: '25%',
                    create:true,
                    edit:true
                },
                orden:{
                    title: 'Orden despliegue',
                    width: '10%',
                    options: { '1': '1', '2': '2', '3': '3', '4': '4', '5': '5', '6': '6', '7': '7', '8': '8', '9': '9', '10': '10', '11': '11', '12': '12', '13': '13', '14': '14', '15': '15' },
                    create:true,
                    edit:true,
                    sorting:false
                },
                vigente:{
                    title: 'Vigente',
                    options: { 'S': 'Si', 'N': 'No' },
                    width: '15%',
                    edit:true,
                    sorting:false
                },
                createDateTime:{
                    title: 'Fecha creacion',
                    width: '20%',
                    create:false,
                    edit:false,
                    sorting:false
                },
                updateDateTime:{
                    title: 'Ultima actualizacion',
                    width: '20%',
                    create:false,
                    edit:false,
                    sorting:false
                }   
            }
        });

        //Re-load records when user click 'load records' button.
        $('#LoadRecordsButton').click(function (e) {
            e.preventDefault();
            $('#TMMTableContainer').jtable('load', {
                nombre: $('#nombre').val(),
                vigente: $('#vigente').val()
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
