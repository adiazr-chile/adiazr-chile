<%@page import="cl.femase.gestionweb.common.Constantes"%>
<%@ include file="/include/check_session.jsp" %>

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
    UsuarioVO theUser	= (UsuarioVO)session.getAttribute("usuarioObj");
    List<UsuarioCentroCostoVO> cencos   = (List<UsuarioCentroCostoVO>)session.getAttribute("cencos_empleado"); 
    
    String startDate = (String)session.getAttribute("startDate");
    String endDate = (String)session.getAttribute("endDate");
    System.out.println("[GestionFemaseWeb]localizacion_marcas.jsp]"
        + "startDate= "+startDate
        + ",endDate= "+endDate);
    String paramEmpresa = (String)request.getAttribute("empresa");
    String paramRut     = (String)request.getAttribute("rut_empleado");
    System.out.println("[GestionFemaseWeb]localizacion_marcas.jsp]"
        + "paramEmpresa= " + paramEmpresa
        + ",paramRut= " + paramRut
        + ",usuario= " + theUser.getUsername()
        + ",perfil= " + theUser.getIdPerfil());
%>
<!DOCTYPE html>

<html>
<head>
    <meta charset="utf-8" />

    <meta name="keywords" content="femase,gestion,web">
    <meta name="description" content="Sistema de Gestion FEMASE-RRHH-Control Asistencia">
    <meta name="author" content="Adiazr">

    <title>Marcas Virtuales</title>
 
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
        
        $(document).ready(function() {
             
             //evento para combo centro costo
             $('#cencoId').change(function(event) {
                 var empresaSelected = null;//$("select#empresaId").val();
                 var deptoSelected = null;//$("select#deptoId").val();
                 var cencoSelected = $("select#cencoId").val();
                 var sourceSelected = 'marcas_virtuales';
                 $.get('<%=request.getContextPath()%>/JsonListServlet', {
                 	empresaID : empresaSelected,deptoID : deptoSelected,cencoID : cencoSelected,source: sourceSelected
                 }, function(response) {
                        var select = $('#rut');
                        select.find('option').remove();
                        var newoption = "";
                        newoption += "<option value='-1'>Seleccione Empleado</option>";
                        for (i=0; i<response.length; i++) {
                            var auxNombre = '['+response[i].rut+'] '+response[i].nombres + 
                                ' ' + response[i].apePaterno + ' '+response[i].apeMaterno;
                            newoption += "<option value='" + response[i].rut + "'>" + auxNombre + "</option>";
                        }
                        $('#rut').html(newoption);
                 });
             });
             
             //evento para combo rut empleado
             $('#rut').change(function(event) {
                 var empresaSelected = $("select#empresaId").val();
                 var deptoSelected = $("select#deptoId").val();
                 var cencoSelected = $("select#cencoId").val();
                 var rutSelected = $("select#rut").val();
                 $.get('<%=request.getContextPath()%>/JsonListServlet', {
                         empresaID : empresaSelected,deptoID : deptoSelected,cencoID : cencoSelected,rutEmpleado : rutSelected
                 }, function(response) {
                        var select = $('#dispositivo');
                        select.find('option').remove();
                        var newoption = "";
                        newoption += "<option value='-1'>Seleccione Dispositivo</option>";
                        for (i=0; i<response.length; i++) {
                            var auxNombre = response[i].id + ' ' + 
                                '[' + response[i].nombreTipo + ']';
                            newoption += "<option value='" + response[i].id + "'>" + auxNombre + "</option>";
                        }
                        $('#dispositivo').html(newoption);
                 });
             });
         });

        function openUrl(url) {
            if (url !== ''){
                window.open(url);
            }
        }

    </script>
    
</head>
<body>
    <div class="site-container">
        <div class="main-header" style="position: relative">
            <h1>Registros de asistencia virtuales</h1>
            <h2>Consulta Localizaciones</h2>
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
                <select name="rut" id="rut">
                    <option value="-1" selected>----------</option>
                </select>
            </label>    
            
            <label>Fecha desde:
                <input name="startDate" type="text" id="startDate">
                hasta <input name="endDate" type="text" id="endDate">
            </label>
        <button type="submit" id="LoadRecordsButton">Buscar</button>
    </form>
</div>

<div id="MarcasVirtualesTableContainer"></div>
<script type="text/javascript">
    <% 
        String readonly="readonly";
        if (theUser.getIdPerfil() == Constantes.ID_PERFIL_ADMIN){
            readonly="";
        }
    %>
    $(document).ready(function () {

        $('#MarcasVirtualesTableContainer').jtable({       
            title: 'Resultados',
            paging: true, //Enable paging
            pageSize: 10, //Set page size (default: 10)
            sorting: true, //Enable sorting
            defaultSorting: 'fecha_hora desc', //Set default sorting
            actions: {
                listAction: '<%=request.getContextPath()%>/servlet/MarcasVirtualesView?action=list'
            },
            fields: {
                correlativo: {
                    title: 'Correlativo',
                    width: '9%',
                    key: true,
                    list: true,
                    sorting: true
                },
                codDispositivo: {
                    title: 'Dispositivo',
                    width: '12%',
                    key: false,
                    list: true,
                    sorting: false
                },
                rutEmpleado: {
                    title: 'Rut',
                    width: '12%',
                    key: false,
                    list: false,
                    sorting: false
                },
                nombreEmpleado: {
                    title: 'Nombre',
                    width: '12%',
                    key: false,
                    list: false,
                    sorting: false
                },
                nombreTipoMarca: {
                    title: 'Tipo',
                    width: '8%',
                    list: true,
                    sorting: false,
                    edit: false
                },
                fechaHoraStr: {
                    title: 'Fecha Hora',
                    width: '19%',
                    sorting: false
                },
                comentario: {
                    title: 'Comentario',
                    width: '16%',
                    sorting: false
                },
                googleMapUrl: {
                    title: 'Ubicacion',
                    width: '12%',
                    sorting: false,
                    display: function(data) {
                        if (data.record.googleMapUrl !== ''){
                            return '<a href="javascript:;" onclick="openUrl(\'' + data.record.googleMapUrl + '\');return false;">Ver Mapa</a>';
                        }
                    }
                }
                
            }
            /*            
            ,
            toolbar: {
                    sorting: false,
                    items: [{
                        icon: '<%=request.getContextPath()%>/images/icon_csv.gif',
                        text: 'CSV',
                            click: function () {
                                document.location.href='<%=request.getContextPath()%>/DataExportServlet?type=marcasVirtualesCSV';
                            }
                        }
                    ]
            }
            */
        });

        //Re-load records when user click 'load records' button.
        $('#LoadRecordsButton').click(function (e) {
            e.preventDefault();
            $('#MarcasVirtualesTableContainer').jtable('load', {
                cencoId: $('#cencoId').val(),
                rutEmpleado: $('#rut').val(),
                startDate: $('#startDate').val(),
                endDate: $('#endDate').val()
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
            dayNames: ['Domingo', 'Lunes', 'Martes', 'Mi�rcoles', 'Jueves', 'Viernes', 'S�bado'],
            dayNamesShort: ['Dom','Lun','Mar','Mi�','Juv','Vie','S�b'],
            dayNamesMin: ['Do','Lu','Ma','Mi','Ju','Vi','S�'],
            weekHeader: 'Sm',
            dateFormat: 'dd/mm/yy',
            firstDay: 1,
            isRTL: false,
            showMonthAfterYear: false,
            yearSuffix: ''
        };
        $.datepicker.setDefaults($.datepicker.regional['es']);
        
        <% if (theUser.getIdPerfil() != Constantes.ID_PERFIL_ADMIN){%>
            $('.datepicker').datepicker('disable');
        <%}%>
        $(function() {
            $('#startDate').datepick({dateFormat: 'yyyy-mm-dd',minDate: -180});
            $('#endDate').datepick({dateFormat: 'yyyy-mm-dd',minDate: -180});
        });

    </script>                                         
                                            
</body>
</html>
