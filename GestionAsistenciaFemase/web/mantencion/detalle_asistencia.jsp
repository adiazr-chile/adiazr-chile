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
    System.out.println("[GestionFemaseWeb]detalle_Asistencia.jsp]"
            + "startDate= " + startDate + 
            ", endDate= " + endDate);
    
%>
<!DOCTYPE html>

<html>
<head>
    <meta charset="utf-8" />

    <meta name="keywords" content="femase,gestion,web">
    <meta name="description" content="Sistema de Gestion FEMASE-RRHH-Control Asistencia">
    <meta name="author" content="Adiazr">

    <title>Detalle Asistencia</title>
 
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
        
        /** estilos para modal*/
        /**
        * Modals ($modals)
        */

       /* 1. Ensure this sits above everything when visible */
       .modal {
           position: absolute;
           z-index: 10000; /* 1 */
           top: 0;
           left: 0;
           visibility: hidden;
           width: 100%;
           height: 100%;
       }

       .modal.is-visible {
           visibility: visible;
       }

       .modal-overlay {
         position: fixed;
         z-index: 10;
         top: 0;
         left: 0;
         width: 100%;
         height: 100%;
         background: hsla(0, 0%, 0%, 0.5);
         visibility: hidden;
         opacity: 0;
         transition: visibility 0s linear 0.3s, opacity 0.3s;
       }

       .modal.is-visible .modal-overlay {
         opacity: 1;
         visibility: visible;
         transition-delay: 0s;
       }

       .modal-wrapper {
         position: absolute;
         z-index: 9999;
         top: 6em;
         left: 50%;
         width: 32em;
         margin-left: -16em;
         background-color: #fff;
         box-shadow: 0 0 1.5em hsla(0, 0%, 0%, 0.35);
       }

       .modal-transition {
         transition: all 0.3s 0.12s;
         transform: translateY(-10%);
         opacity: 0;
       }

       .modal.is-visible .modal-transition {
         transform: translateY(0);
         opacity: 1;
       }

       .modal-header,
       .modal-content {
         padding: 1em;
       }

       .modal-header {
         position: relative;
         background-color: #fff;
         box-shadow: 0 1px 2px hsla(0, 0%, 0%, 0.06);
         border-bottom: 1px solid #e8e8e8;
         background-color: steelblue;
       }

       .modal-close {
         position: absolute;
         top: 0;
         right: 0;
         padding: 1em;
         color: #aaa;
         background: none;
         border: 0;
       }

       .modal-close:hover {
         color: #777;
       }

       .modal-heading {
         font-size: 1.125em;
         margin: 0;
         -webkit-font-smoothing: antialiased;
         -moz-osx-font-smoothing: grayscale;
       }

       .modal-content > *:first-child {
         margin-top: 0;
       }

       .modal-content > *:last-child {
         margin-bottom: 0;
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
                 //alert('cencoID: '+$('#cencoId').val());
                 //var tokenCenco  = $('#cencoId').val().split("|");
                 var empresaSelected  = null;
                 var deptoSelected    = null;
                 var cencoSelected    = $('#cencoId').val();
                 
                 var sourceSelected = 'asistencia_modificar';
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
         });

        $('.modal-toggle').on('click', function(e) {
            e.preventDefault();
            $('.modal').toggleClass('is-visible');
        });
        
        function closeDialog(){
            $('.modal').toggleClass('is-visible');
        }
   
    </script>
    
</head>
<body>
    <div class="site-container">
        <div class="main-header" style="position: relative">
            <h1>Resultado c&aacute;lculos de asistencia</h1>
            <h2>Filtros de b&uacute;squeda</h2>
        </div>
<div class="content-container">
            
            <div class="padded-content-container">
                
<div class="filtering">
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
                <select name="rut" id="rut">
                    <option value="-1" selected>----------</option>
                </select>
            </label>    
            <label>Fecha desde:
                <input name="startDate" type="text" id="startDate">
                hasta <input name="endDate" type="text" id="endDate">
            </label>
        <button type="submit" id="LoadRecordsButton">Buscar</button>
        <a href="<%=request.getContextPath()%>/mantencion/detalle_asistencia_hist.jsp">&nbsp;Ver Hist&oacute;rico</a>
    </form>
</div>

<div id="DetalleAsistenciaTableContainer"></div>
<script type="text/javascript">

    $(document).ready(function () {

        $('#DetalleAsistenciaTableContainer').jtable({       
            title: 'Resultados',
            paging: true, //Enable paging
            pageSize: 10, //Set page size (default: 10)
            sorting: true, //Enable sorting
            defaultSorting: 'rutEmpleado ASC', //Set default sorting
            actions: {
                listAction: '<%=request.getContextPath()%>/DetalleAsistenciaController?action=listResults',
                //updateAction: '<%=request.getContextPath()%>/DetalleAsistenciaController?action=update'
                updateAction: function (postData) {
                    return $.Deferred(function ($dfd) {
                        $.ajax({
                            url: '<%=request.getContextPath()%>/DetalleAsistenciaController?action=update',
                            type: 'POST',
                            dataType: 'json',
                            data: postData,
                            success: function (data) {
                                $dfd.resolve(data);
                            },
                            error: function(xhr, ajaxOptions, thrownError) {
                                $('.modal').toggleClass('is-visible');
                            }     
                        });
                    });
                },
            },
            fields: {
                rowKey: {
                    title: 'key',
                    width: '1%',
                    key: true,
                    list: false
                },
                rut: {
                    title: 'Rut',
                    width: '6%',
                    list: true,
                    edit:true,
                    input: function (data) {
                        if (data.record) {
                            return '<input type="text" name="aRut" style="width:100px" value="' + data.record.rut + '" readonly />';
                        } else {
                            return '<input type="text" name="aRut" style="width:100px" value="nn" readonly />';
                        }
                    }
                },
                fechaMarca: {
                    title: 'Fecha',
                    width: '9%',
                    list: false,
                    edit:true,
                    type: 'date',
                    displayFormat: 'yy-mm-dd',
                    sorting: false,
                    input: function (data) {
                        if (data.record) {
                            return '<input type="text" name="aFecha" style="width:100px" value="' + data.record.fechaEntradaMarca + '" readonly />';
                        } else {
                            return '<input type="text" name="aFecha" style="width:100px" value="nn" readonly/>';
                        }
                    }
                },
                labelFechaEntradaMarca: {
                    title: 'F.Entrada',
                    width: '7%',
                    sorting: false,
                    list: true,
                    edit:false
                },
                horaEntrada: {
                    title: 'H.Entrada',
                    width: '5%',
                    sorting: false,
                    edit:false
                },
                labelFechaSalidaMarca: {
                    title: 'F.Salida',
                    width: '7%',
                    sorting: false,
                    list: true,
                    edit:false
                },
                horaSalida: {
                    title: 'H.Salida',
                    width: '4%',
                    sorting: false,
                    edit:false
                },
                horaEntradaTeorica: {
                    title: 'Ent.Turno',
                    width: '4%',
                    sorting: false,
                    edit:false
                },
                horaSalidaTeorica: {
                    title: 'Sal.Turno',
                    width: '4%',
                    sorting: false,
                    edit:false
                },
                hrsPresenciales: {
                    title: 'Hrs pres.',
                    width: '5%',
                    list: true,
                    sorting: false,
                    edit:false
                },
                esFeriado: {
                    title: 'Feriado',
                    width: '4%',
                    list: true,
                    edit:false,
                    sorting: false,
                    type: 'checkbox',
                    values: { 'false': 'No', 'true': 'Si' },
                    defaultValue: 'false'
                },
                hhmmAtraso: {
                    title: 'Atraso',
                    width: '4%',
                    list: true,
                    edit:false,
                    sorting: false
                },
                hhmmJustificadas: {
                    title: 'Hrs. Justif.',
                    width: '4%',
                    list: true,
                    edit:false,
                    sorting: false
                },
                horaMinsExtras: {
                    title: 'HE',
                    width: '4%',
                    list: true,
                    edit: true,
                    sorting: false,
                    input: function (data) {
                        if (data.record) {
                            return '<input type="text" name="heTope" style="width:200px" value="' + data.record.horaMinsExtras + '" readonly />';
                        }else{
                            return '<input type="text" name="heTope" style="width:200px" value="" />';
                        }
                    }
                },
                autorizaHrsExtras: {
                    title: '�Autoriza Hrs. Extras?',
                    width: '6%',
                    type: 'radiobutton',
                    options: { 'S': 'Si', 'N': 'No' },
                    inputClass: 'validate[required]',
                    defaultValue: 'N',
                    create:false,
                    edit:true
                },
                horasMinsExtrasAutorizadas: {
                    title: 'HE aut.',
                    width: '4%',
                    list: true,
                    edit:true,
                    sorting: false,
                    input: function (data) {
                        if (data.record) {
                            return '<input type="text" name="heAutorizadas" style="width:200px" value="' + data.record.horasMinsExtrasAutorizadas + '" readonly />';
                        }else{
                            return '<input type="text" name="heAutorizadas" style="width:200px" value="" />';
                        }
                    }
                },
                hextrasHH:{
                    title: 'Horas (autorizadas)',
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=horasTope&hhmm='+data.record.horaMinsExtras
                    },
                    list: false,        
                    width: '25%',
                    edit: true,
                    create: false
                },
                hextrasMins:{
                    title: 'Minutos (autorizados)',
                    list: false,
                    width: '25%',
                    edit: true,
                    create: false,
                    options: {  '0': '00', '1': '01', '2': '02', '3': '03', '4': '04', '5': '05', '6': '06', '7': '07', '8': '08', '9': '09', '10': '10', 
                                '11': '11', '12': '12', '13': '13', '14': '14', '15': '15', '16': '16', '17': '17', '18': '18', '19': '19', '20': '20',
                                '21': '21', '22': '22', '23': '23', '24': '24', '25': '25', '26': '26', '27': '27', '28': '28', '29': '29', '30': '30',
                                '31': '31', '32': '32', '33': '33', '34': '34', '35': '35', '36': '36', '37': '37', '38': '38', '39': '39', '40': '40',
                                '41': '41', '42': '42', '43': '43', '44': '44', '45': '45', '46': '46', '47': '47', '48': '48', '49': '49', '50': '50',
                                '51': '51', '52': '52', '53': '53', '54': '54', '55': '55', '56': '56', '57': '57', '58': '58', '59': '59'},
                },    
                observacion: {
                    title: 'Observacion',
                    width: '22%',
                    list: true,
                    edit:false,
                    sorting: false
                }
                
            }
        });

        //Re-load records when user click 'load records' button.
        $('#LoadRecordsButton').click(function (e) {
            e.preventDefault();
            $('#DetalleAsistenciaTableContainer').jtable('load', {
                rut: $('#rut').val(),
                startDate: $('#startDate').val(),
                endDate: $('#endDate').val(),
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
        </div>
        <div class="main-footer" style="position: relative"></div>
        
        <div class="modal">
            <div class="modal-overlay modal-toggle"></div>
            <div class="modal-wrapper modal-transition">
                <div class="modal-header">
                    <button class="modal-close modal-toggle"><svg class="icon-close icon" viewBox="0 0 32 32"><use xlink:href="#icon-close"></use></svg></button>
                    <h2 class="modal-heading">Error</h2>
                </div>

                <div class="modal-body">
                    <div class="modal-content">
                        <p>No se pudo ingresar el registro. Hrs extras autorizadas superan el tope.</p>
                        <button onclick="closeDialog()">Cerrar</button>
                    </div>
                </div>
            </div>
        </div>
        
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
