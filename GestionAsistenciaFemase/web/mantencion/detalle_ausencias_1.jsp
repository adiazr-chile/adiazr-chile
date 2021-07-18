
<%@page import="cl.femase.gestionweb.vo.UsuarioCentroCostoVO"%>
<%@page import="cl.femase.gestionweb.vo.EmpresaVO"%>
<%@page import="cl.femase.gestionweb.vo.AusenciaVO"%>
<%@page import="cl.femase.gestionweb.vo.EmpleadoVO"%>
<%@page import="cl.femase.gestionweb.vo.DetalleAusenciaVO"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.LinkedHashMap"%>

<%
    //listas para realizar busquedas
    List<EmpresaVO> empresas            = (List<EmpresaVO>)session.getAttribute("empresas");
    List<UsuarioCentroCostoVO> cencos   = (List<UsuarioCentroCostoVO>)session.getAttribute("cencos_empleado");
    
    List<DetalleAusenciaVO> autorizadores = 
        (List<DetalleAusenciaVO>)session.getAttribute("autorizadores");
    /*List<EmpleadoVO> empleados = 
        (List<EmpleadoVO>)session.getAttribute("empleados");
    */
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

    <title>Admin Ausencias</title>

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

    <script type="text/javascript">
        
        $(document).ready(function() {
             $('#empresaId').change(function(event) {
                 var empresaSelected = $("select#empresaId").val();
                 $.get('<%=request.getContextPath()%>/JsonListServlet', {
                         empresaID : empresaSelected
                 }, function(response) {
                        var select = $('#deptoId');
                        select.find('option').remove();
                        var newoption = "";
                        newoption += "<option value='-1'>Seleccione Departamento</option>";
                        for (i=0; i<response.length; i++) {
                            newoption += "<option value='"+response[i].id+"'>"+response[i].nombre+"</option>";
                        }
                        $('#deptoId').html(newoption);
                 });
             });
             //evento para combo depto
             $('#deptoId').change(function(event) {
                 var empresaSelected = $("select#empresaId").val();
                 var deptoSelected = $("select#deptoId").val();
                 $.get('<%=request.getContextPath()%>/JsonListServlet', {
                         empresaID : empresaSelected,deptoID : deptoSelected
                 }, function(response) {
                        var select = $('#cencoId');
                        select.find('option').remove();
                        var newoption = "";
                        newoption += "<option value='-1'>Seleccione Centro de costo</option>";
                        for (i=0; i<response.length; i++) {
                            newoption += "<option value='"+response[i].id+"'>"+response[i].nombre+"</option>";
                        }
                        $('#cencoId').html(newoption);
                 });
             });
             //evento para combo centro costo
             $('#cencoId').change(function(event) {
                 //var tokenCenco = $('select#cencoId').val().split("|");
                 var empresaSelected  = null;
                 var deptoSelected    = null;
                 var cencoSelected    = $('select#cencoId').val();
                 
                 var sourceSelected = 'reporte_asistencia';
                 $.get('<%=request.getContextPath()%>/JsonListServlet', {
                         empresaID : empresaSelected,deptoID : deptoSelected,cencoID : cencoSelected,source: sourceSelected
                 }, function(response) {
                        var select = $('#paramRutEmpleado');
                        select.find('option').remove();
                        var newoption = "";
                        newoption += "<option value='-1'>Seleccione Empleado</option>";
                        for (i=0; i<response.length; i++) {
                           var auxNombre = '['+response[i].rut+'] '+response[i].nombres + 
                                ' ' + response[i].apePaterno + ' '+response[i].apeMaterno;
                            newoption += "<option value='" + response[i].rut + "'>" + auxNombre + "</option>";
                        }
                        $('#paramRutEmpleado').html(newoption);
                 });
             });
         });

   
    </script>
    
    <style>
        div.filtering
        {
            border: 1px solid #999;
            margin-bottom: 5px;
            padding: 10px;
            background-color: #EEE;
        }
    </style>

</head>
<body>
    <div class="site-container">
        <div class="main-header" style="position: relative">
            <h1>Lista de Detalles Ausencias<span class="light"></span></h1>
            <h2>Detalles Ausencias</h2>
        </div>
<div class="content-container">
                
<!-- paramRutEmpleado-->
    <form>
        <!--
        <label>Empresa
                <select name="empresaId" id="empresaId">
                <option value="-1" selected>----------</option>
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
                
            <label for="deptoId">Departamento</label>
            <select id="deptoId" name="deptoId" style="width:350px;" required>
                <option value="-1">--------</option>
            </select>
                
            <label>Centro Costo
                <select name="cencoId" id="cencoId">
                    <option value="-1" selected>----------</option>
                </select>
            </label>
            -->
            <label>Centro Costo
                <select name="cencoId" id="cencoId">
                    <option value="-1" selected>----------</option>
                    <%
                    String valueCenco="";
                    String labelCenco="";    
                    Iterator<UsuarioCentroCostoVO> iteracencos = cencos.iterator();
                    while(iteracencos.hasNext() ) {
                        UsuarioCentroCostoVO auxCenco = iteracencos.next();
                        valueCenco = auxCenco.getEmpresaId() + "|" + auxCenco.getDeptoId() + "|" + auxCenco.getCcostoId();
                        labelCenco = "[" + auxCenco.getEmpresaNombre()+ "]," 
                            + "[" + auxCenco.getDeptoNombre()+ "],"
                            + "[" + auxCenco.getCcostoNombre()+ "]";
                    %>
                        <option value="<%=valueCenco%>"><%=labelCenco%></option>
                        <%
                    }
                %>
                </select>
            </label>
            <label>Empleado
                <select name="paramRutEmpleado" id="paramRutEmpleado">
                    <option value="-1" selected>----------</option>
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
        <button type="submit" id="LoadRecordsButton">Buscar</button>
    </form>


<div id="DetalleAusenciasTableContainer"></div>
<script type="text/javascript">

    $(document).ready(function () {
        $('#DetalleAusenciasTableContainer').jtable({       
            title: 'Detalle Ausencias',
            paging: true, //Enable paging
            pageSize: 10, //Set page size (default: 10)
            sorting: true, //Enable sorting
            defaultSorting: 'detalle_ausencia.rut_empleado ASC', //Set default sorting
            actions: {
                listAction: '<%=request.getContextPath()%>/DetalleAusenciaController?action=list',
                //createAction:'<%=request.getContextPath()%>/DetalleAusenciaController?action=create',
                createAction: function (postData) {
                    //console.log("creating from custom function...");
                    return $.Deferred(function ($dfd) {
                        $.ajax({
                            url: '<%=request.getContextPath()%>/DetalleAusenciaController?action=create',
                            type: 'POST',
                            dataType: 'json',
                            data: postData,
                            success: function (data) {
                                $dfd.resolve(data);
                            },
                            error: function (data) {
                                alert('Verifique ausencias existentes');
                                $dfd.reject();
                            }
                        });
                    });
                },
                updateAction: '<%=request.getContextPath()%>/DetalleAusenciaController?action=update',
                deleteAction: '<%=request.getContextPath()%>/DetalleAusenciaController?action=delete'
            },
            fields: {
                rutEmpleado: {
                    title:'Empleado',
                    options: function(data){
                        var tokenCenco = $('#cencoId').val().split("|");
                        var auxempresa  = tokenCenco[0];
                        var auxdepto    = tokenCenco[1];
                        var auxcenco    = tokenCenco[2];
                        
                        return '<%=request.getContextPath()%>/LoadItems?type=empleados'
                            + '&empresa='+auxempresa
                            + '&depto='+auxdepto
                            + '&cenco='+auxcenco;
                    },
                    width: '8%',
                    list: true,
                    create:true,
                    edit:false
                },
                empresaId:{
                    title: 'Id Empresa',
                    width: '2%',
                    edit:false,
                    create:false,
                    list:false,
                    type: 'hidden'
                },
                correlativo:{
                    title: 'ID',
                    width: '7%',
                    edit:false,
                    create:false,
                    key:true
                },
                fechaIngresoAsStr:{
                    title: 'Fecha Ingreso Ausencia',
                    width: '7%',
                    edit:false,
                    create:false
                },
                idAusencia:{
                    title: 'Tipo Ausencia',
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=ausencias'
                    },
                    width: '7%',
                    edit:true,
                    create:true
                },
                permiteHora: {
                    title: '¿Ausencia por hora?',
                    width: '7%',
                    type: 'radiobutton',
                    options: { 'S': 'Si', 'N': 'No' },
                    inputClass: 'validate[required]',
                    defaultValue: 'N',
                    create:true,
                    edit:true
                },       
                fechaInicioAsStr:{
                    title: 'Fecha Inicio',
                    width: '7%',
                    type: 'date',
                    displayFormat: 'yy-mm-dd',
                    inputClass: 'validate[required,custom[date]]',
                    edit:true,
                    create:true,
                    sorting:true
                },
                soloHoraInicio: {
                    title: 'Hora inicio (hora). Ej. 00, 01.. 23',
                    width: '7%',
                    //options: { '0': '00', '1': '01','2': '02', '3': '03','4': '04', '5': '05','6': '06', '7': '07','8': '08', '9': '09','10': '10', '11': '11','12': '12','13': '13','14': '14','15': '15','16': '16','17': '17','18': '18','19': '19','20': '20','21': '21','22': '22','23': '23'},
                    edit:true,
                    create:true,
                    sorting:true
                },
                soloMinsInicio: {
                    title: 'Hora inicio (minutos). Ej. 00, 01.. 59',
                    width: '7%',
                    //options: { '0': '00', '5': '05','10': '10','15': '15','20': '20','25': '25','30': '30','35': '35','40': '40','45': '45','50': '50','55': '55'},
                    edit:true,
                    create:true,
                    sorting:false 
                },    
                fechaFinAsStr:{
                    title: 'Fecha Fin',
                    width: '7%',
                    type: 'date',
                    displayFormat: 'yy-mm-dd',
                    inputClass: 'validate[required,custom[date]]',
                    edit:true,
                    create:true,
                    sorting:true
                },
                soloHoraFin: {
                    title: 'Hora termino (hora). Ej. 00, 01.. 23',
                    width: '7%',
                    //options: { '0': '00', '1': '01','2': '02', '3': '03','4': '04', '5': '05','6': '06', '7': '07','8': '08', '9': '09','10': '10', '11': '11','12': '12','13': '13','14': '14','15': '15','16': '16','17': '17','18': '18','19': '19','20': '20','21': '21','22': '22','23': '23'},
                    edit:true,
                    create:true,
                    sorting:true
                },
                soloMinsFin: {
                    title: 'Hora termino (minutos). Ej. 00, 01.. 59',
                    width: '7%',
                    //options: { '0': '00', '5': '05','10': '10','15': '15','20': '20','25': '25','30': '30','35': '35','40': '40','45': '45','50': '50','55': '55'},
                    edit:true,
                    create:true,
                    sorting:false
                },    
                rutAutorizador: {
                    title:'Rut Autorizador',
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=autorizadores'
                    },
                    width: '7%',
                    list: true,
                    edit:true,
                    create:true,
                    sorting:true
                },
                ausenciaAutorizada: {
                    title: 'Autorizada(Si/No)',
                    width: '7%',
                    type: 'radiobutton',
                    options: { 'S': 'Si', 'N': 'No' },
                    inputClass: 'validate[required]',
                    defaultValue: 'S',
                    create:true,
                    edit:true,
                    sorting:true
                },    
            }
        });

        //Re-load records when user click 'load records' button.
        $('#LoadRecordsButton').click(function (e) {
            e.preventDefault();
            $('#DetalleAusenciasTableContainer').jtable('load', {
                paramRutEmpleado: $('#paramRutEmpleado').val(),
                paramRutAutorizador: $('#paramRutAutorizador').val(),
                paramFechaIngresoInicio: $('#paramFechaIngresoInicio').val(),
                paramFechaIngresoFin: $('#paramFechaIngresoFin').val(),
                paramAusenciaId: $('#paramAusenciaId').val(),
                empresaId: $('#empresaId').val(),
                deptoId: $('#deptoId').val(),
                cencoId: $('#cencoId').val()
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


        <div class="main-footer" style="position: relative"></div>
    </div>
    
</body>
</html>
<script type="text/javascript">

    $(document).ready(function () {
        
        var $adsContainer = $('#AdsContainer');

        var showHideAds = function () {
            if ($(window).width() < 1070) {
                if ($adsContainer.is(':visible')) {
                    $adsContainer.hide();
                }
            } else {
                if (!$adsContainer.is(':visible')) {
                    $adsContainer.show();
                }
            }
        };
        showHideAds();

        $(window).resize(function () {
            showHideAds();
        });
    });

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