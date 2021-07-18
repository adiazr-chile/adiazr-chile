
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

    <title>jTable.org - A JQuery plugin to create AJAX based CRUD tables - Filtering Demo</title>

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

</head>
<body>
    <div class="site-container">
        <div class="main-header" style="position: relative">
            <h1>Lista de Conversion de Contratos<span class="light"></span></h1>
            <h2>Conversion de Contratos existentes en el sistema</h2>
        </div>
<div class="content-container">
            
            <div class="padded-content-container">
                
<div class="filtering">
    <form>
        <label>Nemotecnico: <input type="text" name="symbol" id="symbol" /></label>
        <button type="submit" id="LoadRecordsButton">Buscar</button>
    </form>
</div>

<div id="ContractRelationsTableContainer"></div>
<script type="text/javascript">

    $(document).ready(function () {

        $('#ContractRelationsTableContainer').jtable({       
            title: 'Conversion contratos',
            paging: true, //Enable paging
            pageSize: 10, //Set page size (default: 10)
            sorting: true, //Enable sorting
            defaultSorting: 'symbol ASC', //Set default sorting
            actions: {
                listAction: '<%=request.getContextPath()%>/ContractRelationController?action=list',
                createAction:'<%=request.getContextPath()%>/ContractRelationController?action=create',
                updateAction: '<%=request.getContextPath()%>/ContractRelationController?action=update',
                deleteAction: '<%=request.getContextPath()%>/ContractRelationController?action=delete'
            },
            fields: {
                symbol: {
                    title:'Nemotecnico',
                    width: '50%',
                    key: true,
                    list: true,
                    create:true
                },
                base:{
                    title: 'Base',
                    width: '50%',
                    edit:true
                }    
            }
        });

        //Re-load records when user click 'load records' button.
        $('#LoadRecordsButton').click(function (e) {
            e.preventDefault();
            $('#ContractRelationsTableContainer').jtable('load', {
                symbol: $('#symbol').val()
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
