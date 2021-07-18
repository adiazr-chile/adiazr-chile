<%@page import="cl.femase.gestionweb.vo.UsuarioCentroCostoVO"%>
<%@ include file="/include/check_session.jsp" %>
<%@page import="cl.femase.gestionweb.vo.EmpresaVO"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.LinkedHashMap"%>

<%
    List<UsuarioCentroCostoVO> cencos   = (List<UsuarioCentroCostoVO>)session.getAttribute("cencos_empleado");
        
%>
<!DOCTYPE html>

<html>
<head>
    <meta charset="utf-8" />

    <title>Admin Dispositivos Moviles</title>

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
        $(document).ready(function() {
            
        });
    </script>
    
</head>
<body>
    <div class="site-container">
        <div class="main-header" style="position: relative">
            <h1>Administraci&oacute;n de Dipositivos M&oacute;viles</h1>
            <h2>Filtros de b&uacute;squeda</h2>
        </div>
<div class="content-container">
            
            <div class="padded-content-container">
                
<div class="filtering">
    <form>
        <label>Centro Costo
            <select name="cencoId" id="cencoId">
                    <option value="-1" selected>----------</option>
                    <%
                    String valueCenco="";
                    String labelCenco="";    
                    String labelZonaExtrema = "";
                    Iterator<UsuarioCentroCostoVO> iteracencos = cencos.iterator();
                    while(iteracencos.hasNext() ) {
                        UsuarioCentroCostoVO auxCenco = iteracencos.next();
                        valueCenco = auxCenco.getEmpresaId() + "|" + auxCenco.getDeptoId() + "|" + auxCenco.getCcostoId();
                        if (auxCenco.getZonaExtrema().compareTo("N") == 0) labelZonaExtrema = "";
                        else labelZonaExtrema = "(Zona Extrema)";
                        labelCenco = "[" + auxCenco.getEmpresaNombre()+ "]," 
                            + "[" + auxCenco.getDeptoNombre()+ "],"
                            + "[" + auxCenco.getCcostoNombre()+ "]"
                            + " " + labelZonaExtrema;
                    %>
                        <option value="<%=valueCenco%>"><%=labelCenco%></option>
                        <%
                    }
                %>
                </select>
            </label>
            <label>Nombre: <input type="text" name="filtroMovilId" id="filtroMovilId" /></label>    
            <label for="estado">Estado</label>
                <select id="filtroEstado" name="filtroEstado" 
                           class="chosen-select" style="width:350px;" tabindex="2">
                        <option value="-1">Todos</option>
                        <option value="1" selected>Vigente</option>
                        <option value="2">No Vigente</option>
                    </select>
                
        <button type="submit" id="LoadRecordsButton">Buscar</button>
        
        <input type="hidden" name="action" id="action">
    </form>
</div>

<div id="MovilesTableContainer"></div>
<script type="text/javascript">

    $(document).ready(function () {
        $('#MovilesTableContainer').jtable({       
            title: 'Dispositivos moviles',
            paging: true, //Enable paging
            pageSize: 10, //Set page size (default: 10)
            sorting: true, //Enable sorting
            defaultSorting: 'movil_id asc', //Set default sorting
            actions: {
                listAction: '<%=request.getContextPath()%>/servlet/DispositivoMovilController?action=list',
                updateAction: '<%=request.getContextPath()%>/servlet/DispositivoMovilController?action=update'
            },
            fields: {
               id: {
                    title:'Id',
                    key: true,
                    list: true,
                    create:false,
                    edit:false,
                    sorting:true,
                    input: function (data) {
                        if (data.record) {
                            return '<input type="text" name="id" style="width:100px" value="' + data.record.id + '" readonly />';
                        } 
                    }
                },
                correlativo: {
                    title:'Correlativo',
                    key: false,
                    list: true,
                    edit:false,
                    sorting: false
                },
                directorCencoId: {
                    title:'CencoId Director',
                    key: false,
                    list: false,
                    edit:false,
                    sorting: false
                },
                cencoNombre: {
                    title:'Centro de costo',
                    key: false,
                    list: true,
                    edit:false,
                    sorting: false
                },
                androidId: {
                    title:'Android Id',
                    key: false,
                    list: true,
                    edit:false,
                    sorting: false
                },
                fechaHoraCreacion: {
                    title:'Hora creacion',
                    key: false,
                    list: true,
                    edit:false,
                    sorting: false
                },
                directorRut:{
                    title: 'RUN Director',
                    width: '7%',
                    key: false,
                    edit:false,
                    list: true,
                    sorting: false
                },
                directorNombre:{
                    title: 'Nombre Director',
                    width: '16%',
                    key: false,
                    edit:false,
                    create:false,
                    list: true,
                    sorting:false
                },
                estado:{
                    title: 'Estado',
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=estados';
                    },
                    width: '30%',
                    edit:true,
                    sorting: true
                }    
            },
            toolbar: {
                    sorting: false,
                    items: [{
                        icon: '<%=request.getContextPath()%>/images/icon_csv.gif',
                        text: 'CSV',
                            click: function () {
                                document.location.href='<%=request.getContextPath()%>/DataExportServlet?type=movilesToCSV';
                            } 
                        }
                    ]
            },
            recordsLoaded: function (event, data) {
                //alert('records: '+data.TotalRecordCount);
            },
            
        });

        //Re-load records when user click 'load records' button.
        $('#LoadRecordsButton').click(function (e) {
            e.preventDefault();
            $('#MovilesTableContainer').jtable('load', {
                cencoId: $('#cencoId').val(),
                filtroMovilId: $('#filtroMovilId').val(),
                filtroEstado: $('#filtroEstado').val()
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
        showHideButtonNewRow();
    });

</script>
