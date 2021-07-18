
<%@page import="cl.femase.gestionweb.vo.CentroCostoVO"%>
<%@page import="cl.femase.gestionweb.vo.DepartamentoVO"%>
<%@page import="cl.femase.gestionweb.vo.EmpresaVO"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>

<%
    List<EmpresaVO> empresas = (List<EmpresaVO>)session.getAttribute("empresas");
    List<DepartamentoVO> departamentos   = (List<DepartamentoVO>)session.getAttribute("departamentos");
    List<CentroCostoVO> cencos   = (List<CentroCostoVO>)session.getAttribute("cencos");
    
%>

<!DOCTYPE html>

<html>
<head>
    <meta charset="utf-8" />

    <meta name="keywords" content="femase,gestion,web">
    <meta name="description" content="Sistema de Gestion FEMASE-RRHH-Control Asistencia">
    <meta name="author" content="Adiazr">

    <title>Admin Organizacion</title>

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

        function callDeleteAction(strempresaid,strdeptoid,strcencoid){
            //alert('empresaid: ' +strempresaid+', deptoid: ' +strdeptoid+', cencoid: ' +strcencoid);
            if (confirm('¿Esta seguro que desea eliminar este registro?')){
                document.location.href='<%=request.getContextPath()%>/OrganizacionEmpresaController?'
                    +'action=delete'
                    +'&empresaId='+strempresaid
                    +'&deptoId='+strdeptoid
                    +'&cencoId='+strcencoid; 
            }
        }
    </script>
    
</head>
<body>
    <div class="site-container">
        <div class="main-header" style="position: relative">
            <h1>Organizacion Empresa</h1>
            
        </div>
	<div class="content-container">
            
            <div class="padded-content-container">
                
    <!-- Filtros de busqueda INICIO-->                
    <div class="filtering">
        <form>
            <label>Empresa
                <select name="filtroEmpresa" id="filtroEmpresa">
                
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
                
            <label>Departamento
                <select name="filtroDepto" id="filtroDepto">
                <option value="-1" selected>----------</option>
                <%
                    Iterator<DepartamentoVO> iteradorDeptos = departamentos.iterator();
                    while(iteradorDeptos.hasNext() ) {
                        DepartamentoVO auxdepto = iteradorDeptos.next();
                        %>
                        <option value="<%=auxdepto.getId()%>"><%=auxdepto.getNombre()%></option>
                        <%
                    }
                %>
                </select>
            </label>
                
            <label>Centro Costo
                <select name="filtroCenco" id="filtroCenco">
                <option value="-1" selected>----------</option>
                <%
                    Iterator<CentroCostoVO> iteradorCencos = cencos.iterator();
                    while(iteradorCencos.hasNext() ) {
                        CentroCostoVO auxcenco = iteradorCencos.next();
                        %>
                        <option value="<%=auxcenco.getId()%>"><%=auxcenco.getNombre()%></option>
                        <%
                    }
                %>
                </select>
            </label>    

            <!-- Boton Buscar-->  
            <button type="submit" id="LoadRecordsButton" class="button button-blue">Buscar</button>
            
        </form>
    </div>
    <!-- Filtros de busqueda FIN-->

<div id="OrganizacionTableContainer"></div>
<script type="text/javascript">

    $(document).ready(function () {

        $('#OrganizacionTableContainer').jtable({       
            title: 'Organizacion',
            paging: true, //Enable paging
            pageSize: 10, //Set page size (default: 10)
            sorting: true, //Enable sorting
            defaultSorting: 'depto_nombre ASC', //Set default sorting
            gotoPageArea: 'textbox',
            actions: {
                listAction: '<%=request.getContextPath()%>/OrganizacionEmpresaController?action=list',
                createAction:'<%=request.getContextPath()%>/OrganizacionEmpresaController?action=create'
                //,deleteAction: '<%=request.getContextPath()%>/OrganizacionEmpresaController?action=delete'
            },
            fields: {
                empresaId:{
                    title: 'Empresa',
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=empresas'
                    },
                    width: '25%',
                    key : true,
                    list : true,
                    edit : false,
                    create : true
                },
                deptoId:{
                    title: 'Departamento',
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=departamentos'
                    },
                    width: '25%',
                    key : true,
                    list : true,
                    edit : false,
                    create : true
                },
                cencoId:{
                    title: 'Centro de Costo',
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=cencos'
                    },
                    width: '25%',
                    key : true,
                    list : true,
                    edit : false,
                    create : true
                },
                MyButton: {
                    title: 'Accion',
                    width: '25%',
                    sorting: false,
                    create : false,
                    display: function(data) {
                         return '<button type="button" onclick="callDeleteAction(' + data.record.empresaIdParam + ',' + data.record.deptoIdParam + ',' + data.record.cencoIdParam + ')">Delete</button> ';
                    }
                }                     
            },
            toolbar: {
                    sorting: false,
                    items: [{
                            icon: '<%=request.getContextPath()%>/images/icon_csv.gif',
                            text: 'CSV',
                            click: function () {
                                document.location.href='<%=request.getContextPath()%>/DataExportServlet?type=organizacionToCSV';
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
            $('#OrganizacionTableContainer').jtable('load', {
                filtroEmpresa: $('#filtroEmpresa').val(),
                filtroDepto: $('#filtroDepto').val(),
                filtroCenco: $('#filtroCenco').val()
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
        
        $("#filtroEmpresa").prop('selectedIndex',0);
    });

</script>
