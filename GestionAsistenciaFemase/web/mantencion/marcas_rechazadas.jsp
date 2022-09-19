<%@ include file="/include/check_session.jsp" %>
<%@page import="cl.femase.gestionweb.vo.UsuarioCentroCostoVO"%>
<%@page import="cl.femase.gestionweb.vo.CargoVO"%>
<%@page import="cl.femase.gestionweb.vo.CentroCostoVO"%>
<%@page import="cl.femase.gestionweb.vo.DepartamentoVO"%>
<%@page import="cl.femase.gestionweb.vo.EmpresaVO"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.LinkedHashMap"%>

<%
    List<EmpresaVO> empresas            = (List<EmpresaVO>)session.getAttribute("empresas");
    List<CargoVO> cargos                = (List<CargoVO>)session.getAttribute("cargos");    
    List<UsuarioCentroCostoVO> cencos   = (List<UsuarioCentroCostoVO>)session.getAttribute("cencos_empleado"); 
    String startDate = (String)session.getAttribute("startDate");
    String endDate = (String)session.getAttribute("endDate");
    System.out.println("[GestionFemaseWeb]marcas_rechazadas.jsp]"
        + "startDate= "+startDate
        + ",endDate= "+endDate);
    String paramEmpresa = (String)request.getAttribute("empresa");
    String paramRut     = (String)request.getAttribute("rut_empleado");
    System.out.println("[GestionFemaseWeb]marcas_rechazadas.jsp]"
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

    <title>Marcas Rechazadas</title>
 
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
             
             //evento_1 para combo centro costo
             $('#cencoId').change(function(event) {
                 var empresaSelected = null;
                 var deptoSelected = null;
                 var cencoSelected = $("select#cencoId").val();
                 var sourceSelected = 'admin_marcas';
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
             
             //evento_2 para combo cenco
             $('#cencoId').change(function(event) {
                 var empresaSelected = $("select#empresaId").val();
                 var deptoSelected = $("select#deptoId").val();
                 var cencoSelected = $("select#cencoId").val();
                 //var rutSelected = $("select#rut").val();
                 $.get('<%=request.getContextPath()%>/JsonListServlet', {
                        empresaID : empresaSelected,deptoID : deptoSelected,cencoID : cencoSelected
                         //empresaID : empresaSelected,deptoID : deptoSelected,cencoID : cencoSelected,rutEmpleado : rutSelected
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

   
    </script>
    
</head>
<body>
    <div class="site-container">
        <div class="main-header" style="position: relative">
            <h1>Registros de asistencia rechazados</h1>
            <h2>Consulta</h2>
        </div>
<div class="content-container">
            
            <div class="padded-content-container">
                
<div class="filtering">
    <form>
            <label>Centro Costo</label>
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
            
                <label>Dispositivo
                    <select name="dispositivo" id="dispositivo">
                        <option value="-1" selected>----------</option>
                    </select>
                </label>
                
                <label>Fecha desde:
                    <input name="startDate" type="text" id="startDate">
                    hasta <input name="endDate" type="text" id="endDate">
                </label>
                
                <label>Empleado
                    <select name="rut" id="rut">
                        <option value="-1" selected>----------</option>
                    </select>
                </label>    
            
        <button type="submit" id="LoadRecordsButton">Buscar</button>
        <a href="<%=request.getContextPath()%>/mantencion/marcas_rechazadas_hist.jsp">&nbsp;Ver Hist&oacute;rico</a>
    </form>
</div>

<div id="RechazosTableContainer"></div>
<script type="text/javascript">

    $(document).ready(function () {

        $('#RechazosTableContainer').jtable({       
            title: 'Resultados',
            paging: true, //Enable paging
            pageSize: 10, //Set page size (default: 10)
            sorting: true, //Enable sorting
            defaultSorting: 'fecha_hora_actualizacion desc', //Set default sorting
            actions: {
                listAction: '<%=request.getContextPath()%>/MarcasRechazosController?action=list'
            },
            fields: {
                rutEmpleado: {
                    title: 'Rut',
                    width: '10%',
                    key: true,
                    list: true,
                    input: function (data) {
                        var rutSelected = $("select#rut").val();
                        if (data.record) {
                            return '<input type="text" name="aRut" style="width:100px" value="' + data.record.rutEmpleado + '" readonly />';
                        } else {
                            return '<input type="text" name="aRut" style="width:100px" value="'+rutSelected+'" readonly/>';
                        }
                    }
                },
                empresaCod: {
                    title: 'Empresa',
                    width: '10%',
                    key: true,
                    list: true,
                    sorting: false,
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=empresas'
                    }
                },
                rutKey: {
                    title: 'Rut',
                    width: '10%',
                    key: false,
                    list: false,
                    sorting: false,
                    type: 'hidden'
                },
                fechaHoraStr: {
                    title: 'Fecha hora marca',
                    list:true,
                    width: '10%',
                    key: true
                },
                updateDateTime: {
                    title: 'Fecha hora Actualizacion',
                    list:true,
                    width: '10%',
                    sorting: false,
                    key: true
                },
                tipoMarca: {
                    title: 'Tipo',
                    width: '10%',
                    sorting: false,
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=tiposMarcas';
                    }
                },
                codDispositivo: {
                    title: 'Cod dispositivo',
                    width: '10%',
                    list: true,
                    input: function (data) {
                        var deviceSelected = $("select#dispositivo").val();
                        if (data.record) {
                            return '<input type="text" name="aDevice" style="width:100px" value="' + data.record.codDispositivo + '" readonly />';
                        } else {
                            return '<input type="text" name="aDevice" style="width:100px" value="'+deviceSelected+'" readonly/>';
                        }
                    },
                    sorting: false
                },
                id: {
                    title: 'Id',
                    width: '10%',
                    list: true,
                    sorting: true
                },
                hashcode: {
                    title: 'Hashcode',
                    width: '10%',
                    list: true,
                    sorting: true
                },
                motivoRechazo: {
                    title: 'Motivo Rechazo',
                    width: '10%',
                    list: true,
                    sorting: true,
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=codErrorRechazo';
                    }
                }
            },
            toolbar: {
                    sorting: false,
                    items: [{
                            icon: '<%=request.getContextPath()%>/images/icon_csv.gif',
                            text: 'CSV',
                            click: function () {
                                document.location.href='<%=request.getContextPath()%>/DataExportServlet?type=marcasRechazadasToCSV';
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
            $('#RechazosTableContainer').jtable('load', {
                empresaId: $('#empresaId').val(),
                dispositivo: $('#dispositivo').val(),
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
    
        $(function() {
            $('#startDate').datepick({dateFormat: 'yyyy-mm-dd'});
            $('#endDate').datepick({dateFormat: 'yyyy-mm-dd'});

        });

    </script>                                         
                                            
</body>
</html>
