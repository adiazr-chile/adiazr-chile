
<%@page import="cl.femase.gestionweb.vo.TurnoVO"%>
<%@page import="cl.femase.gestionweb.vo.TurnoRotativoVO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="cl.femase.gestionweb.vo.UsuarioCentroCostoVO"%>
<%@page import="cl.femase.gestionweb.vo.UsuarioVO"%>
<%@page import="cl.femase.gestionweb.vo.CargoVO"%>
<%@page import="cl.femase.gestionweb.vo.CentroCostoVO"%>
<%@page import="cl.femase.gestionweb.vo.DepartamentoVO"%>
<%@page import="cl.femase.gestionweb.vo.EmpresaVO"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.LinkedHashMap"%>

<%
    //UsuarioVO theUser	= (UsuarioVO)session.getAttribute("usuarioObj");
    List<EmpresaVO> empresas            = (List<EmpresaVO>)session.getAttribute("empresas");
    List<CargoVO> cargos                = (List<CargoVO>)session.getAttribute("cargos");
    List<UsuarioCentroCostoVO> cencos   = (List<UsuarioCentroCostoVO>)session.getAttribute("cencos_empleado"); 
    if (cencos == null) cencos = new ArrayList<UsuarioCentroCostoVO>();
    
    //List<TurnoVO> turnos = (List<TurnoVO>)session.getAttribute("turnos");
    List<TurnoRotativoVO> turnosRotativos = (List<TurnoRotativoVO>)session.getAttribute("turnos_rotativos");
    
    String startDate = (String)session.getAttribute("startDate");
    String endDate = (String)session.getAttribute("endDate");
    System.out.println("[GestionFemaseWeb]consulta/asignacion_turnos.jsp]"
        + "startDate= "+startDate
        + ",endDate= "+endDate);
    String paramEmpresa = (String)request.getAttribute("empresa");
    String paramRut     = (String)request.getAttribute("rut_empleado");
    
    System.out.println("[GestionFemaseWeb]consulta/asignacion_turnos.jsp]"
        + "paramEmpresa= " + paramEmpresa
        + ",paramRut= " + paramRut);
%>
<!DOCTYPE html>

<html>
<head>
    <meta charset="utf-8" />

    <meta name="keywords" content="femase,gestion,web">
    <meta name="description" content="Sistema de Gestion FEMASE-RRHH-Control Asistencia">
    <meta name="author" content="Adiazr">

    <title>Asignacion de turnos</title>
 
    <link href="../Jquery-JTable/Content/normalize.css" rel="stylesheet" type="text/css" />
    <link href='<%=request.getContextPath()%>/css-varios/googleapis.css' rel='stylesheet' type='text/css'>
    <link href="../Jquery-JTable/Content/Site.metro.css" rel="stylesheet" type="text/css" />

    <link href="../Jquery-JTable/Content/highlight.css" rel="stylesheet" type="text/css" />

        <link href="../Jquery-JTable/Content/themes/metroblue/jquery-ui.css" rel="stylesheet" type="text/css" />
        <link href="../Jquery-JTable/Scripts/jtable/themes/metro/blue/jtable.css" rel="stylesheet" type="text/css" />

    <link href="../Jquery-JTable/Scripts/syntaxhighligher/styles/shCore.css" rel="stylesheet" type="text/css" />
    <link href="../Jquery-JTable/Scripts/syntaxhighligher/styles/shThemeDefault.css" rel="stylesheet" type="text/css" />

    
    <!-- Import CSS file for validation engine (in Head section of HTML) -->
    <link href="../Jquery-JTable/Scripts/validationEngine/validationEngine.jquery.css" rel="stylesheet" type="text/css" />
 
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
    <link href="../jquery-plugins/datepicker/css/jquery.datepick.css"rel="stylesheet">

        
    <!-- Import Javascript files for validation engine (in Head section of HTML) -->
    <script type="text/javascript" src="../Jquery-JTable/Scripts/validationEngine/jquery.validationEngine.js"></script>
    <script type="text/javascript" src="../Jquery-JTable/Scripts/validationEngine/jquery.validationEngine-en.js"></script>
    <script type="text/javascript">
        var empresaLabel='';
        var deptoLabel='';
        var cencoLabel='';
        function setCenco(cencoId){
            var rutEmpleado=$("select#rut").val();
            var iddevice=$("select#dispositivo").val();
            $('#AsignacionTableContainer').find('.jtable-toolbar-item.jtable-toolbar-item-add-record').hide();
            if (cencoId !== '-1'){
                var cencoSelected= $('#cencoId').find(":selected").text();
                var tokenLabelCenco = cencoSelected.split(",");
                empresaLabel  = tokenLabelCenco[0];
                deptoLabel    = tokenLabelCenco[1];
                cencoLabel    = tokenLabelCenco[2];
            }else if (rutEmpleado === '-1' || iddevice === '-1'){
                $('#AsignacionTableContainer').find('.jtable-toolbar-item.jtable-toolbar-item-add-record').hide();
            }
        }
        
        function setEmpleado(rut){
            var iddevice=$("select#dispositivo").val();
            $('#AsignacionTableContainer').find('.jtable-toolbar-item.jtable-toolbar-item-add-record').hide();
            if (rut !== '-1' && iddevice!=='-1'){
                $('#AsignacionTableContainer').find('.jtable-toolbar-item.jtable-toolbar-item-add-record').show();
            }
        }
        
        function setDispositivo(dispositivoId){
            var rutEmpleado=$("select#rut").val();
            $('#AsignacionTableContainer').find('.jtable-toolbar-item.jtable-toolbar-item-add-record').hide();
            if (rutEmpleado !== '-1' && dispositivoId !== '-1'){
                $('#AsignacionTableContainer').find('.jtable-toolbar-item.jtable-toolbar-item-add-record').show();
            }
        }
        
        
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
                 var empresaSelected = null;//$("select#empresaId").val();
                 var deptoSelected = null;//$("select#deptoId").val();
                 var cencoSelected = $("select#cencoId").val();
                 var sourceSelected = 'reporte_asignacion_turnos';
                 
                $.get('<%=request.getContextPath()%>/JsonListServlet', {
                        empresaID : empresaSelected,deptoID : deptoSelected,cencoID : cencoSelected,source: sourceSelected
                 }, function(response) {
                        var select = $('#rut');
                        select.find('option').remove();
                        var newoption = "";
                        newoption += "<option value='-1'>Seleccione Empleado</option>";
                        for (i=0; i<response.length; i++) {
                            var auxNombre = '['+response[i].rut+'] '+response[i].nombres + 
                                ' ' + response[i].apePaterno 
                                + ' '+response[i].apeMaterno
                                + ' (' + response[i].nombreTurno + ')';
                            newoption += "<option value='" + response[i].rut + "'>" + auxNombre + "</option>";
                        }
                        $('#rut').html(newoption);

                 });
                 
                 sourceSelected = 'cargar_turnos_by_cenco';
                 $.get('<%=request.getContextPath()%>/JsonListServlet', {
                        empresaID : empresaSelected,deptoID : deptoSelected,cencoID : cencoSelected,source: sourceSelected
                 }, function(response2) {
                        var select = $('#turno');
                        select.find('option').remove();
                        var newoption = "";
                        newoption += "<option value='-1'>Seleccione Turno</option>";
                        for (i2 = 0; i2 <response2.length; i2++) {
                            var idTurno = response2[i2].empresaId + "|" + response2[i2].id;
                            var labelTurno = response2[i2].nombre;
                            newoption += "<option value='" + idTurno + "'>" + labelTurno + "</option>";
                        }
                        $('#turno').html(newoption);
                 });
                
             });
           
         });

   
    </script>
    
</head>
<body>
    <div class="site-container">
        <div class="main-header" style="position: relative">
            <h1>Registro de asignacion de turnos<span class="light"></span></h1>
            <h2>Modificaciones o alteraciones de los turnos de los empleados.</h2>
        </div>
<div class="content-container">
            
            <div class="padded-content-container">
                
<div class="filtering">
    <form>
            <label>Centro Costo
                <select name="cencoId" id="cencoId" onChange="setCenco(this.value)">
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
            <select name="rut" id="rut" onChange="setEmpleado(this.value)">
                    <option value="-1" selected>----------</option>
                </select>
            </label>    
            <label>Turno
                <select name="turno" id="turno">
                    <option value="-1" selected>Seleccione turno</option>
                </select>
            </label>
                           
            <label>Fecha desde:
                <input name="startDate" type="text" id="startDate">
                hasta <input name="endDate" type="text" id="endDate">
            </label>
        <button type="submit" id="LoadRecordsButton">Buscar</button>
    </form>
</div>

<div id="AsignacionTableContainer"></div>
<script type="text/javascript">
    $(document).ready(function () {

        $('#AsignacionTableContainer').jtable({       
            title: 'Resultados',
            paging: true, //Enable paging
            pageSize: 10, //Set page size (default: 10)
            sorting: true, //Enable sorting
            defaultSorting: 'fecha_desde desc', //Set default sorting
            actions: {
                listAction: '<%=request.getContextPath()%>/servlet/AsignacionTurnosController?action=list'
            },
            fields: {
                empresaNombre: {
                    title: 'Empresa',
                    width: '10%'
                    //sorting: false
                },
                rutEmpleado: {
                    title: 'Rut',
                    width: '10%'
                },
                nombreEmpleado:{
                    title: 'Nombre',
                    width: '10%'
                },        
                deptoNombre: {
                    title: 'Depto',
                    width: '10%'
                },    
                cencoNombre: {
                    title: 'Cenco',
                    width: '10%'
                },
                idTurno: {
                    title: 'Id Turno',
                    width: '10%'
                },
                nombreTurno: {
                    title: 'Nombre turno',
                    width: '10%'
                },
                fechaDesde: {
                    title: 'Desde',
                    width: '10%'
                },
                fechaHasta: {
                    title: 'Hasta',
                    width: '10%'
                },
                fechaAsignacion: {
                    title: 'Asignacion',
                    width: '10%'
                },
                username: {
                    title: 'Usuario',
                    width: '10%'
                }
            },
            toolbar: {
                sorting: false,
                items: [{
                    icon: '<%=request.getContextPath()%>/images/icon_csv.gif',
                    text: 'CSV',
                    click: function () {
                        document.location.href='<%=request.getContextPath()%>/DataExportServlet?type=asignacionTurnosToCSV';
                    }
                }
                ]
            }
        });

        //Re-load records when user click 'load records' button.
        $('#LoadRecordsButton').click(function (e) {
            e.preventDefault();
            $('#AsignacionTableContainer').jtable('load', {
                empresaId: $('#empresaId').val(),
                cencoId: $('#cencoId').val(),
                rutEmpleado: $('#rut').val(),
                startDate: $('#startDate').val(),
                endDate: $('#endDate').val(),
                turno: $('#turno').val()
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
    
   <script type="text/javascript">
        $.datepicker.regional['es'] = {
            closeText: 'Cerrar',
            prevText: '< Ant',
            nextText: 'Sig >',
            currentText: 'Hoy',
            monthNames: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'],
            monthNamesShort: ['Ene','Feb','Mar','Abr', 'May','Jun','Jul','Ago','Sep', 'Oct','Nov','Dic'],
            dayNames: ['Domingo', 'Lunes', 'Martes', 'Miercoles', 'Jueves', 'Viernes', 'Sabado'],
            dayNamesShort: ['Dom','Lun','Mar','Mie','Juv','Vie','Sab'],
            dayNamesMin: ['Do','Lu','Ma','Mi','Ju','Vi','Sab'],
            weekHeader: 'Sm',
            dateFormat: 'dd/mm/yy',
            firstDay: 1,
            isRTL: false,
            showMonthAfterYear: false,
            yearSuffix: ''
        };
        $.datepicker.setDefaults($.datepicker.regional['es']);
        
        $(function() {
            
            
                $('#startDate').datepick({dateFormat: 'yyyy-mm-dd'});
                $('#endDate').datepick({dateFormat: 'yyyy-mm-dd'});
            
        });

    </script>                                         
                                            
</body>
</html>
