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

    <title>Administracion de Procesos</title>

    <link href="../Jquery-JTable/Content/normalize.css" rel="stylesheet" type="text/css" />
    <link href='<%=request.getContextPath()%>/css-varios/googleapis.css' rel='stylesheet' type='text/css'>
    <link href="../Jquery-JTable/Content/Site.metro.css" rel="stylesheet" type="text/css" />

    <link href="../Jquery-JTable/Content/highlight.css" rel="stylesheet" type="text/css" />

        <link href="../Jquery-JTable/Content/themes/metroblue/jquery-ui.css" rel="stylesheet" type="text/css" />
        <link href="../Jquery-JTable/Scripts/jtable/themes/metro/blue/jtable.css" rel="stylesheet" type="text/css" />

    <link href="../Jquery-JTable/Scripts/syntaxhighligher/styles/shCore.css" rel="stylesheet" type="text/css" />
    <link href="../Jquery-JTable/Scripts/syntaxhighligher/styles/shThemeDefault.css" rel="stylesheet" type="text/css" />
    
    <script type="text/javascript">
        function ejecutar(){
            document.location.href='<%=request.getContextPath()%>/mantencion/ejecucion_procesos.jsp';
	}
    </script>
    
    <style>
        div.filtering
        {
            border: 1px solid #999;
            margin-bottom: 5px;
            padding: 10px;
            background-color: #EEE;
        }
        .button-blue {
            background: #1385e5;
            background: -webkit-linear-gradient(top, #53b2fc, #1385e5);
            background: -moz-linear-gradient(top, #53b2fc, #1385e5);
            background: -o-linear-gradient(top, #53b2fc, #1385e5);
            background: linear-gradient(to bottom, #53b2fc, #1385e5);
            border-color: #075fa9;
            color: white;
            font-weight: bold;
            text-shadow: 0 -1px 0 rgba(0, 0, 0, 0.4);
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
            <h1>Administraci&oacute;n de Procesos</h1>
            <h2>Definici&oacute;n de procesos de ejecuci&oacute;n autom&aacute;tica</h2>
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
        <label>Nombre: <input type="text" name="nombre" id="nombre" /></label>
        <button type="submit" id="LoadRecordsButton">Buscar</button>
        <input name="botonpye" type="button" value="Ejecutar" class="button button-blue" onclick="ejecutar();">
         
    </form>
</div>

<div id="ProcesosTableContainer"></div>
<script type="text/javascript">

    $(document).ready(function () {

        $('#ProcesosTableContainer').jtable({       
            title: 'Procesos',
            paging: true, //Enable paging
            pageSize: 10, //Set page size (default: 10)
            sorting: true, //Enable sorting
            defaultSorting: 'proc_name ASC', //Set default sorting
            actions: {
                listAction: '<%=request.getContextPath()%>/ProcesosController?action=list',
                createAction:'<%=request.getContextPath()%>/ProcesosController?action=create',
                updateAction: '<%=request.getContextPath()%>/ProcesosController?action=update'
                //,deleteAction: '<%=request.getContextPath()%>/ModulosSistemaController?action=delete'
            },
            fields: {
                rowKey:{
                  key:true,
                  list:false,
                  create:false,
                  edit:false,
                  input: function (data) {
                        if (data.record) {
                            return '<input type="hidden" name="rowKey" style="width:100px" value="' + data.record.rowKey + '" />';
                        } 
                    },
                },
                empresaId: {
                    title:'Empresa',
                    width: '16%',
                    list: true,
                    create:true,
                    edit:false,
                    /*input: function (data) {
                        if (data.record) {
                            return '<input type="text" name="aEmpresaId" style="width:100px" value="' + data.record.empresaId + '" readonly />';
                        } 
                    },
                    */
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=empresas'
                    }
                },
                id: {
                    title:'Id',
                    width: '16%',
                    list: true,
                    create:false,
                    edit:true,
                    input: function (data) {
                        if (data.record) {
                            return '<input type="text" name="aId" style="width:100px" value="' + data.record.id + '" readonly />';
                        } 
                    }
                },
                estado:{
                    title: 'Estado',
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=estados'
                    },
                    width: '16%',
                    edit:true
                },
                nombre:{
                    title: 'Nombre (label)',
                    width: '18%',
                    create:true,
                    edit:true
                },
                jobName:{
                    title: 'Nombre Job',
                    width: '18%',
                    create:true,
                    edit:true
                },
                fechaHoraActualizacion:{
                    title: 'Fecha-Hora actualizacion',
                    width: '16%',
                    create:false,
                    edit:false
                }
            }
        });

        //Re-load records when user click 'load records' button.
        $('#LoadRecordsButton').click(function (e) {
            e.preventDefault();
            $('#ProcesosTableContainer').jtable('load', {
                empresaId: $('#empresaId').val(),
                nombre: $('#nombre').val()
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
