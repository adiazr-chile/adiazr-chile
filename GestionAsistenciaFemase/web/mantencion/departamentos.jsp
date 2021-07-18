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

<html lang="es">
<head>
    <meta charset="utf-8" />

    <title>Admin Departamentos</title>

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
            <h1>Administraci&oacute;n de Departamentos</h1>
            <h2>Filtros de b&uacute;squeda</h2>
        </div>
<div class="content-container">
            
            <div class="padded-content-container">
                
<div class="filtering">
    <form>
        <label for="empresaId">Empresa</label>
        <select id="empresaId" name="empresaId" style="width:150px;" required>
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
        <label>Nombre Depto.: <input type="text" name="nombre" id="nombre" /></label>
        <button type="submit" id="LoadRecordsButton">Buscar</button>
    </form>
</div>

<div id="DeptosTableContainer"></div>
<script type="text/javascript">

    $(document).ready(function () {

        $('#DeptosTableContainer').jtable({       
            title: 'Departamentos',
            paging: true, //Enable paging
            pageSize: 10, //Set page size (default: 10)
            sorting: true, //Enable sorting
            defaultSorting: 'depto_nombre ASC', //Set default sorting
            actions: {
                listAction: '<%=request.getContextPath()%>/DepartamentosController?action=list',
                createAction:'<%=request.getContextPath()%>/DepartamentosController?action=create',
                updateAction: '<%=request.getContextPath()%>/DepartamentosController?action=update'
                ,deleteAction: '<%=request.getContextPath()%>/DepartamentosController?action=delete'
            },
            fields: {
                id: {
                    title:'Id Depto.',
                    width: '25%',
                    key: true,
                    list: true,
                    create:true,
                    input: function (data) {
                        if (data.record) {
                            return '<input type="text" name="id" style="width:50px" maxlength="3" value="' + data.record.Name + '" />';
                        } else {
                            return '<input type="text" name="id" style="width:50px" maxlength="3" value="" />';
                        }
                    }
                },
                empresaId: {
                    title:'Empresa',
                    width: '25%',
                    list: true,
                    create:true,
                    edit:true,
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=empresas'
                    }
                },
                nombre:{
                    title: 'Nombre',
                    width: '25%',
                    edit:true,
                    create:true
                },
                estado:{
                    title: 'Estado',
                    width: '25%',
                    edit:true,
                    create:true,
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=estados'
                    }
                }
            }
        });

        //Re-load records when user click 'load records' button.
        $('#LoadRecordsButton').click(function (e) {
            e.preventDefault();
            $('#DeptosTableContainer').jtable('load', {
                nombre: $('#nombre').val(),
                empresaId: $('#empresaId').val()
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
        
        
    });

</script>
