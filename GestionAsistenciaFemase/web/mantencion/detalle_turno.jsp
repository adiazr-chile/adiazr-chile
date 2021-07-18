<%@ include file="/include/check_session.jsp" %>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.LinkedHashMap"%>

<!DOCTYPE html>
<%
    String paramIdTurno = request.getParameter("filtroId");
%>
<html>
<head>
    <meta charset="utf-8" />
    <meta name="keywords" content="femase,gestion,web">
    <meta name="description" content="Sistema de Gestion FEMASE-RRHH-Control Asistencia">
    <meta name="author" content="Adiazr">

    <title>Admin Detalle Turno</title>

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
        /*div.filtering
        {
            border: 1px solid #999;
            margin-bottom: 5px;
            padding: 10px;
            background-color: #EEE;
        }*/
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
        function volver(){
            window.history.back();
	}
        

    </script>
</head>
<body>
    <div class="site-container">
        <div class="main-header" style="position: relative">
            <h1><span class="light"></span></h1>
            <input name="botonvolver" type="button" value="Volver" class="button button-blue" onclick="volver();">
        </div>
    <div class="content-container">
    <div class="padded-content-container">
    <div class="filtering">
        <form>
           <label><input type="text" name="filtroNombre" id="filtroNombre" /></label>
            <button type="submit" id="LoadRecordsButton">Buscar</button>
        </form>
    </div>

<div id="DetalleTurnoTableContainer"></div>
<script type="text/javascript">

    $(document).ready(function () {

        $('#DetalleTurnoTableContainer').jtable({       
            title: 'Detalle Turno',
            paging: true, //Enable paging
            pageSize: 10, //Set page size (default: 10)
            sorting: true, //Enable sorting
            defaultSorting: 'cod_dia ASC', //Set default sorting
            actions: {
                listAction: '<%=request.getContextPath()%>/DetalleTurnoController?action=list&filtroId=<%=paramIdTurno%>',
                createAction:'<%=request.getContextPath()%>/DetalleTurnoController?action=create',
                updateAction:'<%=request.getContextPath()%>/DetalleTurnoController?action=update',
                deleteAction:'<%=request.getContextPath()%>/DetalleTurnoController?action=delete&idTurno=<%=paramIdTurno%>'
            },
            fields: {
                idTurno:{
                    title: 'Id Turno',
                    list: true,
                    width: '10%',
                    edit:false,
                    create:true,
                    key: true,
                    type: 'hidden',
                    defaultValue: '<%=paramIdTurno%>'
                },
                nombreTurno:{
                    title: 'Turno',
                    list: true,
                    sorting: false,
                    width: '10%',
                    edit:false,
                    create:false
                },
                codDia: {
                    title:'Cod Dia',
                    list: true,
                    width: '15%',
                    key: true,
                    create:true,
                    type: 'radiobutton',
                    options: { '1': 'Lunes', '2': 'Martes', '3': 'Miercoles', '4': 'Jueves', '5': 'Viernes', '6': 'Sabado', '7': 'Domingo' },
                    inputClass: 'validate[required]',
                    defaultValue: '1',
                }
                /*,
                labelCortoDia:{
                    title: 'Dia',
                    width: '15%',
                    edit:false,
                    create:false
                }*/
                ,
                horaEntrada:{
                    title: 'Hra entrada',
                    width: '10%',
                    edit:true,
                    //list: true,
                    create:true,
                    /*options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=horas';
                    },*/
                    key: false
                },
                horaSalida:{
                    title: 'Hra salida',
                    width: '15%',
                    edit:true,
                    //list: true,
                    create:true,
                    //options: function(data){
                    //    return '<%=request.getContextPath()%>/LoadItems?type=horas';
                    //},
                    key: false
                },
                totalHoras:{
                    title: 'Total Horas',
                    list: true,
                    sorting: true,
                    width: '10%',
                    edit:false,
                    create:false
                },
                holgura:{
                    title: 'Holgura(mins)',
                    width: '10%',
                    edit:true,
                    create:true
                },
                minutosColacion:{
                    title: 'Mins Colacion',
                    width: '10%',
                    edit:true,
                    create:true
                }        
            },
            formCreated: function (event, data)
                {data.form.find('[name=horaEntrada]').mask('99:99:99'); }
        });
        
        //Re-load records when user click 'load records' button.
        $('#LoadRecordsButton').hide();
        $('#filtroNombre').hide();
        
        $('#LoadRecordsButton').click(function (e) {
            e.preventDefault();
            $('#DetalleTurnoTableContainer').jtable('load', {
                filtroNombre: $('#filtroNombre').val()
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

