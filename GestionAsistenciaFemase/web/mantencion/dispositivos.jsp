<%@ include file="/include/check_session.jsp" %>
<%@page import="cl.femase.gestionweb.vo.EmpresaVO"%>
<%@page import="cl.femase.gestionweb.vo.TipoDispositivoVO"%>

<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.LinkedHashMap"%>

<%
    List<EmpresaVO> empresas = (List<EmpresaVO>)session.getAttribute("empresas");
    //List<DepartamentoVO> departamentos   = (List<DepartamentoVO>)session.getAttribute("departamentos");
    //List<CentroCostoVO> cencos   = (List<CentroCostoVO>)session.getAttribute("cencos");
    List<TipoDispositivoVO> tipos   = (List<TipoDispositivoVO>)session.getAttribute("tipos_dispositivos");
    
    String straux="values: {"; 
    Iterator<EmpresaVO> iteraempresas = empresas.iterator();
    while(iteraempresas.hasNext() ) {
        EmpresaVO auxempresa = iteraempresas.next();
        straux += "'"+auxempresa.getId()+"': '"+auxempresa.getNombre()+"',";                    
    }
    straux = straux.substring(0, straux.length()-1);
    straux += "},";
%>

<!DOCTYPE html>

<html>
<head>
    <meta charset="utf-8" />

    <meta name="keywords" content="femase,gestion,web">
    <meta name="description" content="Sistema de Gestion FEMASE-RRHH-Control Asistencia">
    <meta name="author" content="Adiazr">

    <title>Admin Dispositivos</title>

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
    <!-- javascript y estilo para calendario datepicker  -->
    <script src="../jquery-plugins/datepicker/js/jquery.datepick.js"></script>
    <script src="../jquery-plugins/datepicker/js/jquery.plugin.min.js"></script>
    <script src="../jquery-plugins/datepicker/js/jquery.datepick.js"></script>
    <link rel="stylesheet" href="../jquery-plugins/datepicker/css/jquery.datepick.css" >

    <script type="text/javascript">
        function openAsignacionForm(){
            document.location.href='<%=request.getContextPath()%>/DispositivosController?action=asignacion_start'; 
	}
        
        /*function openAsigacionForm(id){
            document.location.href='<%=request.getContextPath()%>/DispositivosController?action=asignacion'; 
        }*/
    </script>
</head>
<body>
    <div class="site-container">
        <div class="main-header" style="position: relative">
           <h1>Administraci&oacute;n de Dispositivos</h1>
            <h2>Filtros de b&uacute;squeda</h2>
        </div>
<div class="content-container">
            
            <div class="padded-content-container">
                
<div class="filtering">
    <form>
        <label>Tipo
            <select name="filtroTipo" id="filtroTipo">
            <option value="-1" selected>----------</option>
            <%
                Iterator<TipoDispositivoVO> iteraTipos = tipos.iterator();
                while(iteraTipos.hasNext() ) {
                    TipoDispositivoVO auxtipo = iteraTipos.next();
                    %>
                    <option value="<%=auxtipo.getId()%>"><%=auxtipo.getName()%></option>
                    <%
                }
            %>
            </select>
        </label>
                
        <label>Estado:
            <select id="filtroEstado" name="filtroEstado" style="width:150px;" tabindex="2" required>
                <option value="-1"></option>
                <option value="1">Vigente</option>
                <option value="2">No Vigente</option>
            </select>
        </label>
        <label>Fecha ingreso
            <input name="filtroFechaIngreso" type="text" id="filtroFechaIngreso">
        </label>
        <button type="submit" id="LoadRecordsButton" class="button button-blue">Buscar</button>
        <!--<input name="botonasignacion" 
               type="button" value="Asignacion Dispositivos" 
               class="button button-blue" 
               onclick="openAsignacionForm();">-->
    </form>
</div>

<div id="DispositivosTableContainer"></div>
<script type="text/javascript">

    $(document).ready(function () {

        $('#DispositivosTableContainer').jtable({       
            title: 'Dispositivos',
            paging: true, //Enable paging
            pageSize: 10, //Set page size (default: 10)
            sorting: true, //Enable sorting
            defaultSorting: 'type_id ASC', //Set default sorting
            actions: {
                listAction: '<%=request.getContextPath()%>/DispositivosController?action=list',
                createAction:'<%=request.getContextPath()%>/DispositivosController?action=create',
                updateAction: '<%=request.getContextPath()%>/DispositivosController?action=update'
            },
            fields: {
                id: {
                    title:'Id',
                    width: '8%',
                    key: true,
                    list: true,
                    create:true
                },
                idTipo:{
                    title: 'Tipo',
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=tipos_dispositivos'
                    },
                    width: '8%',
                    edit:true
                },
                fechaIngresoAsStr:{
                    title: 'Fecha Ingreso',
                    width: '8%',
                    type: 'text',
                    //displayFormat: 'yy-mm-dd',
                    //inputClass: 'validate[required,custom[date]]',
                    edit:false,
                    create:false
                },
                fechaHoraUltimaActualizacionAsStr:{
                    title: 'Fecha Ult. Actualizacion',
                    width: '8%',
                    type: 'text',
                    //displayFormat: 'yy-mm-dd',
                    //inputClass: 'validate[required,custom[date]]',
                    edit:false,
                    create:false
                },    
                ubicacion:{
                    title: 'Ubicacion',
                    width: '8%',
                    edit:true
                },
                idComuna:{
                    title: 'Comuna',
                    width: '8%',
                    edit:true,
                    sorting:false,
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=comunas'
                    }
                },
                estado:{
                    title: 'Estado',
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=estados'
                    },
                    width: '8%',
                    edit:true
                },
                modelo:{
                    title: 'Modelo',
                    width: '8%',
                    edit:true
                },
                fabricante:{
                    title: 'Fabricante',
                    width: '8%',
                    edit:true
                }, 
                firmwareVersion:{
                    title: 'Firmware ver.',
                    width: '8%',
                    edit:true
                },    
                ip:{
                    title: 'Direccion IP',
                    width: '8%',
                    edit:true
                },    
                gateway:{
                    title: 'Gateway',
                    width: '8%',
                    edit:true
                },
                dns:{
                    title: 'DNS',
                    width: '8%',
                    edit:true
                },
                direccion:{
                    title: 'Direccion',
                    width: '8%',
                    edit:true,
                    sorting:false
                },    
                asignacion: {
                    title: 'Asignacion',
                    width: '8%',
                    type: 'checkbox',
                    values: { '1': 'Uno', '2': 'Dos', '3': 'Tres' },
                    defaultValue: 'true'
                },    
            },
            toolbar: {
                    sorting: false,
                    items: [{
                            icon: '<%=request.getContextPath()%>/images/icon_csv.gif',
                            text: 'CSV',
                            click: function () {
                                document.location.href='<%=request.getContextPath()%>/DataExportServlet?type=dispositivosToCSV';
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
            $('#DispositivosTableContainer').jtable('load', {
                filtroTipo: $('#filtroTipo').val(),
                filtroEstado: $('#filtroEstado').val(),
                filtroFechaIngreso: $('#filtroFechaIngreso').val()
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

    $(function() {
        $('#filtroFechaIngreso').datepick({dateFormat: 'yyyy-mm-dd'});
    });

</script>
