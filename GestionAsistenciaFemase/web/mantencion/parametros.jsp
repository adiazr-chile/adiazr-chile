<%@page import="cl.femase.gestionweb.vo.EmpresaVO"%>
<%@ include file="/include/check_session.jsp" %>

<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.LinkedHashMap"%>

<!DOCTYPE html>
<%
    List<EmpresaVO> empresas = (List<EmpresaVO>)session.getAttribute("empresas");
    
%>
<html>
<head>
    <meta charset="utf-8" />

    <meta name="keywords" content="femase,gestion,web">
    <meta name="description" content="Sistema de Gestion FEMASE-RRHH-Control Asistencia">
    <meta name="author" content="Adiazr">

    <title>Admin Prametros</title>

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
            <h1>Administraci&oacute;n de Par&aacute;metros</h1>
            <h2>Filtros de b&uacute;squeda</h2>
        </div>
    <div class="content-container">
            
        <div class="padded-content-container">
                
<div class="filtering">
    <form>
        <label for="paraEmpresaId">Empresa</label>
        <select id="paraEmpresaId" name="paraEmpresaId" style="width:150px;" required>
        <option value="-1"></option>
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
        <button type="submit" id="LoadRecordsButton">Buscar</button>
    </form>
</div>

<div id="ParametrosTableContainer"></div>
<script type="text/javascript">

    $(document).ready(function () {

        $('#ParametrosTableContainer').jtable({       
            title: 'Parametros',
            paging: true, //Enable paging
            pageSize: 10, //Set page size (default: 10)
            sorting: true, //Enable sorting
            defaultSorting: 'param_code ASC', //Set default sorting
            actions: {
                listAction: '<%=request.getContextPath()%>/ParametrosController?action=list',
                createAction:'<%=request.getContextPath()%>/ParametrosController?action=create',
                updateAction: '<%=request.getContextPath()%>/ParametrosController?action=update'
                //,deleteAction: '<%=request.getContextPath()%>/RegionesController?action=delete'
            },
            fields: {
                empresaId:{
                    title: 'Empresa',
                    width: '20%',
                    edit:true,
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=empresas';
                    },
                    sorting:false        
                },
                paramCode: {
                    title:'Code',
                    width: '10%',
                    key: true,
                    list: true,
                    create:true,
                    sorting:true
                },
                paramLabel:{
                    title: 'Label',
                    width: '30%',
                    edit:true,
                    sorting:false
                },
                valor:{
                    title: 'Valor',
                    width: '10%',
                    edit:true,
                    sorting:false
                },
                fechaCreacion:{
                    title: 'Fecha creacion',
                    width: '15%',
                    edit:false,
                    sorting:false,
                    create:false
                },
                fechaModificacion:{
                    title: 'Fecha modificacion',
                    width: '15%',
                    edit:false,
                    sorting:false,
                    create:false
                }
            }
        });

        //Re-load records when user click 'load records' button.
        $('#LoadRecordsButton').click(function (e) {
            e.preventDefault();
            $('#ParametrosTableContainer').jtable('load', {
                paraEmpresaId: $('#paraEmpresaId').val()
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
