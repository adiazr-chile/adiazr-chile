<%@page import="cl.femase.gestionweb.vo.UsuarioVO"%>
<%@ include file="/include/check_session.jsp" %>
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
    UsuarioVO theUser	= (UsuarioVO)session.getAttribute("usuarioObj");
    
    //listas para realizar busquedas
    List<UsuarioCentroCostoVO> cencos   = (List<UsuarioCentroCostoVO>)session.getAttribute("cencos_empleado");
    List<DetalleAusenciaVO> autorizadores = 
        (List<DetalleAusenciaVO>)session.getAttribute("autorizadores");
    List<AusenciaVO> ausencias = 
        (List<AusenciaVO>)session.getAttribute("ausencias");
    boolean readOnly = false;
    //perfil empleado y fiscalizador solo pueden ver
    if (theUser.getIdPerfil() == 4 || theUser.getIdPerfil() == 7){
        readOnly = true;
    }
%>
<!DOCTYPE html>

<html>
<head>
    <meta charset="utf-8" />

    <meta name="keywords" content="femase,gestion,web">
    <meta name="description" content="Sistema de Gestion FEMASE-RRHH-Control Asistencia">
    <meta name="author" content="Adiazr">

    <title>Admin Justificaciones</title>

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
             //evento para combo centro costo
             $('#cencoId').change(function(event) {
                 var empresaSelected  = null;
                 var deptoSelected    = null;
                 var cencoSelected    = $('select#cencoId').val();
                 var sourceSelected = 'ausencias_detalle_lista';
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

            $('.modal-toggle').on('click', function(e) {
                //alert('aquii');
                e.preventDefault();
                $('.modal').toggleClass('is-visible');
            });
            function closeDialog(){
                $('.modal').toggleClass('is-visible');
            }
   
            function crearAusencia(){
                document.location.href='<%=request.getContextPath()%>/jqueryform-detalle-ausencias/form_crear_detalle_ausencia.jsp';
            }
    </script>
    
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
        /* Page wrapper */
        .wrapper {
          width: 90%;
          max-width: 800px;
          margin: 4em auto;
          text-align: center;

        }
        
        .jtable-input-readonly{
            background-color:lightgray;
        }
    </style>

</head>
<body>
    <div class="site-container">
        <div class="main-header" style="position: relative">
            <h1>Administraci&oacute;n de Justificaciones</h1>
            <h2>Filtros de b&uacute;squeda</h2>
        </div>
<div class="content-container">
                
<!-- paramRutEmpleado-->
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
                <select name="paramRutEmpleado" id="paramRutEmpleado">
                    <option value="-1" selected>----------</option>
                </select>
            </label>
            
        <label>
            <input type="hidden" id="paramRutAutorizador" name="paramRutAutorizador" value="-1">
        </label>
        <label>Fecha Inicio ausencia, desde:
            <input name="paramFechaIngresoInicio" type="text" id="paramFechaIngresoInicio">
            hasta <input name="paramFechaIngresoFin" type="text" id="paramFechaIngresoFin">
        </label> 
                    
        </label>
        <label>Tipo Ausencia:
            <!--<input type="hidden" id="paramAusenciaId" name="paramAusenciaId" value="-1">-->
            <select name="paramAusenciaId" id="paramAusenciaId">
                <option value="-1" selected>Todas</option>
                <%
                    Iterator<AusenciaVO> itera3 = ausencias.iterator();
                    while(itera3.hasNext() ) {
                        AusenciaVO auxausencia = itera3.next();
                        if (auxausencia.getId() != 1){
                        %>
                            <option value="<%=auxausencia.getId()%>"><%=auxausencia.getNombre()%></option>
                        <%}
                    }
                %>
            </select>
        </label>
        <button type="submit" id="LoadRecordsButton">Buscar</button>
        <!--<a href="<%=request.getContextPath()%>/jqueryform-detalle-ausencias/form_crear_detalle_ausencia.jsp">&nbsp;[Nuevo registro]</a>-->
        <%if (!readOnly){%>
            <input name="botoncrear" type="button" value="Crear Justificacion Inasistencia" class="button button-blue" onclick="crearAusencia();">
        <%}%>
        <a href="<%=request.getContextPath()%>/mantencion/detalle_ausencias_historicas.jsp">&nbsp;Ver Hist&oacute;rico</a>
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
                listAction: '<%=request.getContextPath()%>/DetalleAusenciaController?action=list&source=detalle_ausencias',
                //createAction:'<%=request.getContextPath()%>/DetalleAusenciaController?action=create',
                //createAction: function (postData) {
                    ////console.log("creating from custom function...");
                    //return $.Deferred(function ($dfd) {
                        //$.ajax({
                            //url: '<%=request.getContextPath()%>/DetalleAusenciaController?action=create',
                            //type: 'POST',
                            //dataType: 'json',
                            //data: postData,
                            //success: function (data) {
                            //    $dfd.resolve(data);
                            //},
                        //error: function(xhr, ajaxOptions, thrownError) {
                         //   $('.modal').toggleClass('is-visible');
                        //}     
                    //});
                //});
              //},
              <%if (!readOnly){%>
                updateAction: '<%=request.getContextPath()%>/DetalleAusenciaController?action=update',
                deleteAction: '<%=request.getContextPath()%>/DetalleAusenciaController?action=delete'
              <%}%>  
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
                    title: 'Motivo ausencia/justificacion',
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=ausenciassinvacas'
                    },
                    width: '7%',
                    edit:true,
                    create:true
                },
                permiteHora: {
                    title: 'Tipo de Ausencia',
                    width: '7%',
                    type: 'radiobutton',
                    options: { 'S': 'Ausencia por hora', 'N': 'Ausencia por todo el d�a'},
                    inputClass: 'validate[required]',
                    defaultValue: 'S',
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
                    create:false,
                    edit:false,
                    sorting:true
                }    
            },
            //Initialize validation logic when a form is created
            formCreated: function (event, data) {
                var rutSelected = $("select#paramRutEmpleado").val();
                //var permiteHora = $("Edit-permiteHora").val();
                //var aux1 = document.getElementById("permiteHora").value;
                //var aux2 = document.getElementById("permiteHora").checked;
                //alert('set permiteHora SI');
                //$('#Edit-permiteHora').val('S');
                
                if (rutSelected === '-1'){
                    alert('Seleccione empleado...');
                }
                
                $('#Edit-rutEmpleado').val(rutSelected);
                /*
                deshabilitarCampo('Edit-soloHoraInicio');
                deshabilitarCampo('Edit-soloMinsInicio');
                deshabilitarCampo('Edit-soloHoraFin');
                deshabilitarCampo('Edit-soloMinsFin');
                */
                $("input[type='radio']").change(function () {
                    var selection=$(this).val();
                    //alert("Radio button selection changed. Selected: "+selection);
                    if (selection === 'N') {
                        //alert('ausencia por dia');
                        deshabilitarCampo('Edit-soloHoraInicio');
                        deshabilitarCampo('Edit-soloMinsInicio');
                        deshabilitarCampo('Edit-soloHoraFin');
                        deshabilitarCampo('Edit-soloMinsFin');
                        
                    }else {
                        //alert('ausencia por hora');
                        habilitarCampo('Edit-soloHoraInicio');
                        habilitarCampo('Edit-soloMinsInicio');
                        habilitarCampo('Edit-soloHoraFin');
                        habilitarCampo('Edit-soloMinsFin');
                    } 
		});
                
                $('input[name=fechaInicioAsStr]').change(function() { 
                    //alert('(0)Fecha inicio es: ' + this.value);
                    var radioValue = $("input[name='permiteHora']:checked").val();
                    //alert('Permitehora:' + radioValue);
                    document.getElementById('Edit-fechaFinAsStr').value='';
                    if (radioValue === 'S') {
                        //alert('Setear Fecha fin a:' + this.value);                        
                        document.getElementById('Edit-fechaFinAsStr').value = this.value;
                    }
                });
                
                /*
                $( ".Edit-fechaInicioAsStr" ).change(function() {
                    alert('(0)Fecha inicio es: ' + this.value);
                });
                
                $('#fechaInicioAsStr').on('input',function(e){
                    alert('(1)Fecha inicio es: ' + this.value);
                });
                
                $('Edit-fechaInicioAsStr').change(function(){
                    alert('(2)Fecha inicio es: ' + this.value);
                    var radioValue = $("input[name='permiteHora']:checked").val();
                    if (radioValue === 'S') {
                        alert('Setear Fecha fin a:' + this.value);                        
                        document.getElementById('Edit-fechaFinAsStr').value = this.value;
                    }
                    //alert("The text has been changed.");
                    //Edit-fechaFinAsStr
                    
                });*/
            }
        });

        function deshabilitarCampo(nombreCampo){
            $('#'+nombreCampo).addClass('jtable-input-readonly');
            document.getElementById(nombreCampo).setAttribute("readonly", "true");
            //document.getElementById(nombreCampo).value='';
        }
        
        function habilitarCampo(nombreCampo){
            //alert('habilitarCampo...');
            $('#'+nombreCampo).removeClass('jtable-input-readonly');
            document.getElementById(nombreCampo).removeAttribute("readonly");
        }

        function showErrorMessage(xhr, status, error) {
            if (xhr.responseText !== "") {

                var jsonResponseText = $.parseJSON(xhr.responseText);
                var jsonResponseStatus = '';
                var message = '';
                $.each(jsonResponseText, function(name, val) {
                    if (name === "ResponseStatus") {
                        jsonResponseStatus = $.parseJSON(JSON.stringify(val));
                         $.each(jsonResponseStatus, function(name2, val2) {
                             if (name2 === "Message") {
                                 message = val2;
                             }
                         });
                    }
                });

                alert('MSG: ' + message);
            }
        }

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

<div class="modal">
    <div class="modal-overlay modal-toggle"></div>
    <div class="modal-wrapper modal-transition">
      <div class="modal-header">
        <button class="modal-close modal-toggle"><svg class="icon-close icon" viewBox="0 0 32 32"><use xlink:href="#icon-close"></use></svg></button>
        <h2 class="modal-heading">Error</h2>
      </div>
      
      <div class="modal-body">
        <div class="modal-content">
          <p>No se pudo ingresar el registro. Ya existe una ausencia para las fechas especificadas</p>
          <button onclick="closeDialog()">Cerrar</button>
        </div>
      </div>
    </div>
  </div>
        
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
   
   
</script>

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
        dayNamesMin: ['Do','Lu','Ma','Mi','Ju','Vi','Sa'],
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
        $('#paramFechaIngresoInicio').datepick(
            {
                dateFormat: 'yyyy-mm-dd',
                directionReverse: true
            }
        );
        $('#paramFechaIngresoFin').datepick(
                {
                    dateFormat: 'yyyy-mm-dd',
                    directionReverse: true
                }
        );
    });

</script>