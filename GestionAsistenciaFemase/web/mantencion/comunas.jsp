<%@page import="cl.femase.gestionweb.vo.RegionVO"%>
<%@ include file="/include/check_session.jsp" %>

<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.LinkedHashMap"%>

<%
    List<RegionVO> regiones = (List<RegionVO>)session.getAttribute("regiones"); 
%>

<!DOCTYPE html>

<html>
<head>
    <meta charset="utf-8" />

    <meta name="keywords" content="femase,gestion,web">
    <meta name="description" content="Sistema de Gestion FEMASE-RRHH-Control Asistencia">
    <meta name="author" content="Adiazr">

    <title>Admin Comunas</title>

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
            <h1>Administraci&oacute;n de comunas</h1>
            <h2>Filtros de b&uacute;squeda</h2>
        </div>
<div class="content-container">
            
            <div class="padded-content-container">
                
<div class="filtering">
    <form>
        <label>Nombre: <input type="text" name="nombre" id="nombre" /></label>
        <label>Estado:
            <select id="regionId" name="regionId" style="width:150px;" tabindex="2" required>
                <option value="-1">Todas</option>
                <%
                    Iterator<RegionVO> itRegiones = regiones.iterator();
                    while (itRegiones.hasNext()) {
                        RegionVO regionVo = itRegiones.next();
                        String label = "[" + regionVo.getNombreCorto()+"] " 
                            + regionVo.getNombre(); 
                    %>
                        <option value="<%=regionVo.getRegionId()%>"><%=label%></option>  
                    <%}
                %>
            </select>
        </label>
        
        <button type="submit" id="LoadRecordsButton">Buscar</button>
    </form>
</div>

<div id="ComunasTableContainer"></div>
<script type="text/javascript">

    $(document).ready(function () {

        $('#ComunasTableContainer').jtable({       
            title: 'Regiones',
            paging: true, //Enable paging
            pageSize: 10, //Set page size (default: 10)
            sorting: true, //Enable sorting
            defaultSorting: 'region_id ASC', //Set default sorting
            actions: {
                listAction: '<%=request.getContextPath()%>/ComunasController?action=list',
                createAction:'<%=request.getContextPath()%>/ComunasController?action=create',
                updateAction: '<%=request.getContextPath()%>/ComunasController?action=update'
                //,deleteAction: '<%=request.getContextPath()%>/RegionesController?action=delete'
            },
            fields: {
                regionId:{
                    title: 'Region',
                    width: '30%',
                    edit:true,
                    sorting:true,
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=regiones';
                    }
                },
                id: {
                    title:'Id',
                    width: '20%',
                    key: true,
                    list: true,
                    create:false,
                    sorting:true
                },
                nombre:{
                    title: 'Nombre',
                    width: '50%',
                    edit:true,
                    sorting:true
                }
            }
        });

        //Re-load records when user click 'load records' button.
        $('#LoadRecordsButton').click(function (e) {
            e.preventDefault();
            $('#ComunasTableContainer').jtable('load', {
                nombre: $('#nombre').val(),
                regionId: $('#regionId').val()
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
