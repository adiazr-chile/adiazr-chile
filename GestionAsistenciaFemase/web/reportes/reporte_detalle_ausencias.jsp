
<%@page import="cl.femase.gestionweb.vo.AusenciaVO"%>
<%@page import="cl.femase.gestionweb.vo.EmpleadoVO"%>
<%@page import="cl.femase.gestionweb.vo.DetalleAusenciaVO"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.LinkedHashMap"%>

<%
    //listas para realizar busquedas
    List<DetalleAusenciaVO> autorizadores = 
        (List<DetalleAusenciaVO>)session.getAttribute("autorizadores");
    List<EmpleadoVO> empleados = 
        (List<EmpleadoVO>)session.getAttribute("empleados");
    List<AusenciaVO> ausencias = 
        (List<AusenciaVO>)session.getAttribute("ausencias");
    
%>
<!DOCTYPE html>

<html>
<head>
    <meta charset="utf-8" />

    <meta name="keywords" content="femase,gestion,web">
    <meta name="description" content="Sistema de Gestion FEMASE-RRHH-Control Asistencia">
    <meta name="author" content="Adiazr">

    <title>Reporte Detalle Ausencias</title>

    <link href="../Jquery-JTable/Content/normalize.css" rel="stylesheet" type="text/css" />
    <link href='<%=request.getContextPath()%>/css-varios/googleapis.css' rel='stylesheet' type='text/css'>
    <link href="../Jquery-JTable/Content/Site.metro.css" rel="stylesheet" type="text/css" />

    <link href="../Jquery-JTable/Content/highlight.css" rel="stylesheet" type="text/css" />

        <link href="../Jquery-JTable/Content/themes/metroblue/jquery-ui.css" rel="stylesheet" type="text/css" />
        <link href="../Jquery-JTable/Scripts/jtable/themes/metro/blue/jtable.css" rel="stylesheet" type="text/css" />

    <link href="../Jquery-JTable/Scripts/syntaxhighligher/styles/shCore.css" rel="stylesheet" type="text/css" />
    <link href="../Jquery-JTable/Scripts/syntaxhighligher/styles/shThemeDefault.css" rel="stylesheet" type="text/css" />

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
    <link href="../jquery-plugins/datepicker/css/jquery.datepick.css"rel="stylesheet">
    
    <style>
        div.filtering
        {
            border: 1px solid #999;
            margin-bottom: 5px;
            padding: 10px;
            background-color: #EEE;
        }
    </style>

        
    <script type="text/javascript">
        function validaForm() {
            var formato = $('#formato').val();
            if (formato === 'csv'){
                document.getElementById('demo-form').method = 'get'; //Will set it
                return true;
            }
        }
    </script>
</head>
<body>
    <div class="site-container">
        <div class="main-header" style="position: relative">
            <h1>Lista de Detalles Ausencias<span class="light"></span></h1>
            <h2>Detalles Ausencias</h2>
        </div>
<div class="content-container">
                

    <form id="demo-form" 
          action="<%=request.getContextPath()%>/ReporteDetalleAusencias" 
          method="POST" 
          onsubmit="return validaForm();">
        <label>Empleado:
            <select name="paramRutEmpleado" id="paramRutEmpleado">
            <option value="-1" selected>----------</option>
            <%
                Iterator<EmpleadoVO> iteraEmpleados = empleados.iterator();
                while(iteraEmpleados.hasNext() ) {
                    EmpleadoVO auxempleado = iteraEmpleados.next();
                    %>
                    <option value="<%=auxempleado.getRut()%>"><%=auxempleado.getNombreCompleto()%></option>
                    <%
                }
            %>
            </select>
        </label>
        <label>Autorizador:
            <select name="paramRutAutorizador" id="paramRutAutorizador">
            <option value="-1" selected>----------</option>
            <%
                Iterator<DetalleAusenciaVO> itera2 = autorizadores.iterator();
                while(itera2.hasNext() ) {
                    DetalleAusenciaVO auxautoriza = itera2.next();
                    String label = auxautoriza.getNombreAutorizador()
                        + "[" + auxautoriza.getNomDeptoAutorizador()
                        + "-" + auxautoriza.getNomCencoAutorizador() + "]";
                    %>
                    <option value="<%=auxautoriza.getRutAutorizador()%>"><%=label%></option>
                    <%
                }
            %>
            </select>
        </label>
        <label>Fecha Ingreso ausencia, desde:
            <input name="paramFechaIngresoInicio" type="text" id="paramFechaIngresoInicio">
            hasta <input name="paramFechaIngresoFin" type="text" id="paramFechaIngresoFin">
        </label> 
                    
        </label>
        <label>Tipo Ausencia:
            <select name="paramAusenciaId" id="paramAusenciaId">
            <option value="-1" selected>----------</option>
            <%
                Iterator<AusenciaVO> itera3 = ausencias.iterator();
                while(itera3.hasNext() ) {
                    AusenciaVO auxausencia = itera3.next();
                    %>
                    <option value="<%=auxausencia.getId()%>"><%=auxausencia.getNombre()%></option>
                    <%
                }
            %>
            </select>
        </label>
        
        <label>Formato
            <select name="formato" id="formato" class="chosen-select">
                <option value="-1" selected>----------</option>
                <option value="pdf" selected>PDF</option>
                <option value="csv">CSV</option>
            </select>
        </label>    
            
        <button type="submit" 
            id="LoadRecordsButton"
            name="LoadRecordsButton"
            class="button button-blue">Generar Reporte
        </button>
                
    </form>



<br />
<hr />
        </div>


        <div class="main-footer" style="position: relative"></div>
    </div>
    
</body>
</html>
<script type="text/javascript">

    $(function() {
        $('#paramFechaIngresoInicio').datepick({dateFormat: 'yyyy-mm-dd'});
    });
    
   
</script>

<script type="text/javascript">

    $.datepicker.regional['es'] = {
        closeText: 'Cerrar',
        prevText: '< Ant',
        nextText: 'Sig >',
        currentText: 'Hoy',
        monthNames: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'],
        monthNamesShort: ['Ene','Feb','Mar','Abr', 'May','Jun','Jul','Ago','Sep', 'Oct','Nov','Dic'],
        dayNames: ['Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado'],
        dayNamesShort: ['Dom','Lun','Mar','Mié','Juv','Vie','Sáb'],
        dayNamesMin: ['Do','Lu','Ma','Mi','Ju','Vi','Sá'],
        weekHeader: 'Sm',
        dateFormat: 'dd/mm/yy',
        firstDay: 1,
        isRTL: false,
        showMonthAfterYear: false,
        yearSuffix: ''
    };
    $.datepicker.setDefaults($.datepicker.regional['es']);


</script>

<script type="text/javascript">

    $(function() {
        $('#paramFechaIngresoInicio').datepick({dateFormat: 'yyyy-mm-dd'});
        $('#paramFechaIngresoFin').datepick({dateFormat: 'yyyy-mm-dd'});
        
    });

</script>