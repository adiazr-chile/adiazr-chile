
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.LinkedHashMap"%>

<%
    LinkedHashMap<String,String> tipos = 
        (LinkedHashMap<String,String>)session.getAttribute("tipos_eventos");
    String startDate = (String)request.getAttribute("startDate");
    String endDate = (String)request.getAttribute("endDate");
   System.out.println("JSP. startDate= "+startDate+", endDate= "+endDate);
   if (startDate==null) startDate="";
   if (endDate==null) endDate="";
%>

<!DOCTYPE html>

<html>
<head>
    <meta charset="utf-8" />

    <meta name="keywords" content="femase,gestion,web">
    <meta name="description" content="Sistema de Gestion FEMASE-RRHH-Control Asistencia">
    <meta name="author" content="Adiazr">

    <title>Eventos de Mantencion</title>

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
</head>
<body>
    <div class="site-container">
        <div class="main-header" style="position: relative">
            <h1>Registro de Eventos</h1>
            <h2>Hist&oacute;rico</h2>
        </div>
<div class="content-container">
            
            <div class="padded-content-container">
                
<div class="filtering">
    <form>
        <label>Nombre: 
            <input type="text" name="username" id="username" />
        </label>
            
        <label>Tipo Evento
            <select name="tipo_evento" id="tipo_evento">
            <option value="-1" selected>----------</option>
            <%
                for(String key : tipos.keySet()) {
                    String name = tipos.get(key);
                %>
                    <option value="<%=key%>"><%=name%></option>
                    <%
                }
            %>
            </select>
        </label>
        <label>Fecha inicio
            <input name="fecha_inicio" type="text" id="fecha_inicio" value="<%=startDate%>">
        </label>
        <label>Fecha fin
            <input name="fecha_fin" type="text" id="fecha_fin" value="<%=endDate%>">
        </label> 
        <button type="submit" id="LoadRecordsButton">Buscar</button>
    </form>
</div>

<div id="EventosTableContainer"></div>
<script type="text/javascript">

    $(document).ready(function () {

        $('#EventosTableContainer').jtable({       
            title: 'Eventos Mantencion',
            paging: true, //Enable paging
            pageSize: 10, //Set page size (default: 10)
            sorting: true, //Enable sorting
            defaultSorting: 'fecha_hora DESC', //Set default sorting
            actions: {
                listAction: '<%=request.getContextPath()%>/EventosMantencionHistController?action=list'
            },
            fields: {
                username: {
                    title:'Usuario',
                    width: '9%',
                    key: true,
                    list: true,
                    create:true
                },
                descripcion:{
                    title: 'Evento',
                    width: '9%',
                    edit:true
                },
                ip:{
                    title: 'IP',
                    width: '9%',
                    edit:true
                },
                tipoEventoId:{
                    title: 'Tipo Evento',
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=tipos_eventos'
                    },
                    width: '9%',
                    edit:true
                },
                fechaHoraAsStr:{
                    title: 'Fecha-hora',
                    width: '9%'
                },
                empresaNombre:{
                    title: 'Empresa',
                    width: '9%'
                },
                deptoNombre:{
                    title: 'Depto',
                    width: '9%'
                },
                cencoNombre:{
                    title: 'Cenco',
                    width: '9%'
                },
                rutEmpleado:{
                    title: 'Rut',
                    width: '9%'
                }    
            },
            toolbar: {
                sorting: false,
                items: [{
                    icon: '<%=request.getContextPath()%>/images/icon_csv.gif',
                    text: 'CSV',
                    click: function () {
                        document.location.href='<%=request.getContextPath()%>/DataExportServlet?type=eventosToCSV';
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
            $('#EventosTableContainer').jtable('load', {
                username: $('#username').val(),
                tipo_evento: $('#tipo_evento').val(),
                fecha_inicio: $('#fecha_inicio').val(),
                fecha_fin: $('#fecha_fin').val(),
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
             $('#fecha_inicio').datepick({dateFormat: 'yyyy-mm-dd'});
             $('#fecha_fin').datepick({dateFormat: 'yyyy-mm-dd'});
         });

</script>
