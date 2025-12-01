<%@page import="cl.femase.gestionweb.vo.UsuarioVO"%>
<%@page import="cl.femase.gestionweb.business.DetalleAusenciaBp"%>
<%@page import="cl.femase.gestionweb.vo.UsuarioCentroCostoVO"%>
<%@page import="cl.femase.gestionweb.vo.DetalleAusenciaVO"%>
<%@page import="cl.femase.gestionweb.vo.AusenciaVO"%>
<%@page import="cl.femase.gestionweb.vo.CargoVO"%>
<%@page import="cl.femase.gestionweb.vo.EmpresaVO"%>
<%@page import="cl.femase.gestionweb.vo.DepartamentoVO"%>
<%@page import="cl.femase.gestionweb.vo.CentroCostoVO"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@ page import="java.util.Collection"%>
<%@ page import="java.util.Iterator"%>

<%
    UsuarioVO theUser = (UsuarioVO) session.getAttribute("usuarioObj");
    List<UsuarioCentroCostoVO> cencos
            = (List<UsuarioCentroCostoVO>) session.getAttribute("cencos_empleado");
    List<AusenciaVO> ausencias
            = (List<AusenciaVO>) session.getAttribute("ausencias");
    List<DetalleAusenciaVO> autorizadores
            = (List<DetalleAusenciaVO>) session.getAttribute("autorizadores");
    String msgError = (String) request.getAttribute("msgerror");
    String cencoId = (String) request.getParameter("cencoId");
    String rutEmpleado = (String) request.getParameter("rut");
    System.out.println("[GestionFemaseWeb]crear_detalle_ausencia]cencoId param: " + cencoId + ", rut param: " + rutEmpleado);

%>
<!DOCTYPE html>
<html lang="en">

    <head>

        <meta charset="utf-8">
        <meta name="generator" content="jqueryform.com">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->

        <title>Crear Justificacion</title>

        <!-- Bootstrap -->
        <link href="css/bootstrap.min.css" rel="stylesheet">
        <link href="css/bootstrap-theme.min.css" rel="stylesheet">
        <link href="css/bootstrap-datepicker3.min.css" rel="stylesheet">


        <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
        <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
        <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
        <![endif]-->
        <script src="../Jquery-JTable/Scripts/jquery-1.9.1.min.js" type="text/javascript"></script>
        <script type="text/javascript" src="../js/validarut.js"></script>

        <script>
            $(document).ready(function () {
                $("#reload_iframe").click(function () {
                    $('iframe').attr('src', $('iframe').attr('src'));
                });
            });

            function replaceAll(originalString, find, replace) {
                return originalString.replace(new RegExp(find, 'g'), replace);
            }
            ;

            function recargarIframe() {
                document.getElementById('iframe_ultimas').src += '';
            }

            function volver() {
                var auxEmpresaId = $('#empresaId').val();
                var auxDeptoId = $('#deptoId').val();
                var auxCencoId = $('#cencoId').val();
                var cencoKey = auxEmpresaId + '|' + auxDeptoId + '|' + auxCencoId;

                $('#cco').val(cencoKey);
                document.getElementById("redirectForm").submit();
            }

            function deshabilitarCampo(nombreCampo) {
                $('#' + nombreCampo).addClass('jtable-input-readonly');
                document.getElementById(nombreCampo).setAttribute("readonly", "true");
                $('#' + nombreCampo).addClass('jf-disabled');
                document.getElementById(nombreCampo).value = '';
            }

            function habilitarCampo(nombreCampo) {
                $('#' + nombreCampo).removeClass('jtable-input-readonly');
                document.getElementById(nombreCampo).removeAttribute("readonly");
                $('#' + nombreCampo).removeClass();
            }

        </script>

        <link href="vendor.css" rel="stylesheet">

        <style type="text/css">
            body{
                background-color: transparent;
            }

            .jf-form{
                margin-top: 8px;
            }

            .jf-form > form{
                margin-bottom: 32px;
            }

            .jf-option-box{
                display: none;
                margin-left: 8px;
            }

            .jf-hide{
                display: none;
            }

            .jf-disabled {
                background-color: #eeeeee;
                opacity: 0.6;
                pointer-events: none;
            }

            /* 
            overwrite bootstrap default margin-left, because the <label> doesn't contain the <input> element.
            */
            .checkbox input[type=checkbox], .checkbox-inline input[type=checkbox], .radio input[type=radio], .radio-inline input[type=radio] {
                position: absolute;
                margin-top: 4px \9;
                margin-left: 0px;
            }

            div.form-group{
                /*padding: 8px 8px 4px 8px;*/
            }

            .mainDescription{
                margin-bottom: 10px;
            }

            p.description{
                margin:0px;
            }

            .responsive img{
                width: 100%;
            }

            p.error, p.validation-error{
                padding: 5px;
            }

            p.error{
                margin-top: 10px;
                color:#a94442;
            }

            p.server-error{
                font-weight: bold;
            }

            div.thumbnail{
                position: relative;
                text-align: center;
            }

            div.thumbnail.selected p{
                color: #ffffff;
            }

            div.thumbnail .glyphicon-ok-circle{
                position: absolute;
                top: 16px;
                left: 16px;
                color: #ffffff;
                font-size: 32px;
            }

            .jf-copyright{
                color: #f5f1f1;
                display: inline-block;
                margin: 16px;
                display:none;
            }

            .form-group.required .control-label:after {
                color: #dd0000;
                content: "*";
                margin-left: 6px;
            }

            .submit .btn.disabled, .submit .btn[disabled]{
                background: transparent;
                opacity: 0.75;
            }

            /* for image option with span text */
            .checkbox label > span, .radio label > span{
                display: block;
            }

            .form-group.inline .control-label,
            .form-group.col-1 .control-label,
            .form-group.col-2 .control-label,
            .form-group.col-3 .control-label
            {
                display: block;
            }

            .form-group.inline div.radio,
            .form-group.inline div.checkbox{
                display: inline-block;
            }

            .form-group.col-1 div.radio,
            .form-group.col-1 div.checkbox{
                display: block;
            }

            .form-group.col-2 div.radio,
            .form-group.col-2 div.checkbox{
                display: inline-flex;
                width: 48%;
            }

            .form-group.col-3 div.radio,
            .form-group.col-3 div.checkbox{
                display: inline-flex;
                width: 30%;
            }

            .clearfix:after {
                content: ".";
                visibility: hidden;
                display: block;
                height: 0;
                clear: both;
            }

            #errmsg1
            {
                color: red;
            }

            #errmsg2
            {
                color: red;
            }
            .divTable {
                display: table;
                width: 100%;
            }
            .divTableBody {
                display: table-row-group;
            }
            .divTableCell {
                border: 1px solid #999999;
                display: table-cell;
                font-family: monospace;
                font-size: 8px;
                background-color: azure;
            }
            .divTableHead {
                border: 1px solid #999999;
                display: table-cell;
                padding: 3px 10px;
                font-family: monospace;
                font-size: smaller;
                background-color: azure;
            }
            .divTableHeading {
                border: 1px solid #999999;
                display: table-cell;
                padding: 3px 10px;
                font-family: monospace;
                font-size: medium;
                background-color: khaki;
            }
            .divTableRow {
                display: table-row;
            }
            div.abs {
                width: 593px;
                height: 250px;
                position: absolute;
                left: 514px;
                top: 66px;
                cursor: pointer;
            }
        </style>

    </head>

    <body>

        <!-- ----------------------------------------------- -->
        <div class="container jf-form">
            <form data-licenseKey="JF-9X6926878C359674T" 
                  name="jqueryform-d81043" 
                  id="jqueryform-d81043" 
                  action='<%=request.getContextPath()%>/DetalleAusenciaController' 
                  method='post' 
                  novalidate autocomplete="on">
                <input type="hidden" name="method" value="validateForm">
                <input type="hidden" id="serverValidationFields" name="serverValidationFields" value="">
                <input type="hidden" name="action" id="action" value="create">
                <h1>Ingresar Justificaci&oacute;n de inasistencia</h1>
                <div class="abs">
                    Ultimas justificaciones ingresadas <!--<a href="javascript:location.reload();">Recargar</a>-->
                    <input type="button"
                           id="btn" 
                           value="Recargar" onClick="recargarIframe()"/>
                    <iframe src="<%=request.getContextPath()%>/jqueryform-detalle-ausencias/ultimas_ausencias.jsp" width="100%" height="100%" frameborder="0" scrolling="yes" id="iframe_ultimas"></iframe>
                </div>
                <div class="form-group fechaTerminoContratoAsStr " data-fid="fechaTerminoContratoAsStr">
                    <div class="form-group cencoId required" data-fid="cencoId">
                        <label class="control-label" for="cencoId">Centro de Costo</label>
                        <select 
                            class="form-control" 
                            id="cencoId" 
                            name="cencoId"   
                            data-rule-required="true" 
                            data-msg-required="Requerido" >
                            <option value="-1" selected>----------</option>
                            <%
                                String valueCenco = "";
                                String labelCenco = "";
                                Iterator<UsuarioCentroCostoVO> iteracencos = cencos.iterator();
                                while (iteracencos.hasNext()) {
                                    UsuarioCentroCostoVO auxCenco = iteracencos.next();
                                    valueCenco = auxCenco.getEmpresaId() + "|" + auxCenco.getDeptoId() + "|" + auxCenco.getCcostoId();
                                    labelCenco = "[" + auxCenco.getEmpresaNombre() + "],"
                                            + "[" + auxCenco.getDeptoNombre() + "],"
                                            + "[" + auxCenco.getCcostoNombre() + "]";
                            %>
                            <option value="<%=valueCenco%>"><%=labelCenco%></option>
                            <%
                                }
                            %>
                        </select>
                    </div>
                </div>
                <div class="form-group rutEmpleado required" data-fid="rutEmpleado">
                    <label class="control-label" for="rutEmpleado">Empleado</label>
                    <br>
                    <select id="rutEmpleado" name="rutEmpleado"   
                            data-rule-required="true" data-msg-required="Requerido" >
                        <option value='-1'>Seleccione Empleado</option>
                    </select>
                </div>
                <div class="form-group idAusencia required" data-fid="idAusencia">
                    <div class="form-group idCargo required" data-fid="motivoAusencia">
                        <label class="control-label" for="idAusencia">Motivo ausencia/justificaci&oacute;n</label>
                        <select class="form-control" id="idAusencia" name="idAusencia"   
                                data-rule-required="true" data-msg-required="Requerido" >
                            <option selected value="-1">- Seleccione motivo -</option>
                            <%
                                Iterator<AusenciaVO> iteraAusencias = ausencias.iterator();
                                while (iteraAusencias.hasNext()) {
                                    AusenciaVO ausencia = iteraAusencias.next();
                                    String label = ausencia.getNombre(); // + " (" + ausencia.getTipoNombre() + ")";
                                    if (!label.startsWith("Vacaciones")) {
                            %>
                            <option value="<%=ausencia.getId()%>"><%=label%></option>
                            <%}
                                }
                            %>
                        </select>
                    </div>
                    <div class="form-group permiteHora required" data-fid="permiteHora">
                        <label class="control-label" for="permiteHora">Tipo de ausencia</label>
                        <select class="form-control" id="permiteHora" name="permiteHora"   
                                data-rule-required="true" data-msg-required="Requerido" >
                            <option value="S">Ausencia por Hora</option>
                            <option value="N" selected>Ausencia por todo el dia</option>
                        </select>

                    </div>
                    <div class="form-group fechaInicioAsStr required" data-fid="fechaInicioAsStr">
                        <label class="control-label" for="fechaInicioAsStr">Fecha de inicio</label>
                        <div>
                            <input type="text" class="datepicker" id="fechaInicioAsStr" name="fechaInicioAsStr" value="" 
                                   placeholder="Ingrese fecha de inicio" 
                                   data-rule-required="true" data-msg-required="Requerido"  
                                   data-datepicker-format="dd-mm-yyyy"
                                   data-datepicker-startView="1" />
                            <label class="control-label" for="soloHoraInicio">Hora. Ej. 00, 01.. 23</label>
                            <input name="soloHoraInicio" type="text" class="text-success"  id="soloHoraInicio"  
                                   placeholder="Ingrese Hora. Por ej.: 10" value="" size="4" maxlength="2" 
                                   data-rule-required="false" data-msg-required="Requerido" 
                                   data-rule-maxlength="70" data-msg-maxlength="No debe exceder de {0} caracteres"   />

                            <span class="control-label">Mins</span>
                            <input name="soloMinsInicio" type="text" class="text-success"  id="soloMinsInicio"  
                                   placeholder="Ingrese Minutos. Por ej.: 05" value="" size="4" maxlength="2" 
                                   data-rule-required="false" data-msg-required="Requerido" 
                                   data-rule-maxlength="70" data-msg-maxlength="No debe exceder de {0} caracteres"   />
                            <span id="errmsg1"></span>
                            </span>  
                        </div>
                    </div>
                    <div class="form-group soloHoraInicio required" data-fid="soloHoraInicio"></div>
                    <div class="form-group fechaFinAsStr required" data-fid="fechaFinAsStr">
                        <label class="control-label" for="fechaFinAsStr">Fecha fin</label>
                        <div class="input-group date">
                            <input type="text" class="datepicker" id="fechaFinAsStr" name="fechaFinAsStr" value=""  
                                   placeholder="Ingrese fecha fin" 
                                   data-rule-required="true" data-msg-required="Requerido"  
                                   data-datepicker-format="dd-mm-yyyy"
                                   data-datepicker-startView="1" />
                            <span class="form-group soloHoraInicio required">
                                <label class="control-label" for="soloHoraFin">Hora. Ej. 00, 01.. 23</label>
                                <input name="soloHoraFin" type="text"  id="soloHoraFin"  
                                       placeholder="Ingrese Hora. Por ej.: 10" value="" size="4" maxlength="2" 
                                       data-rule-required="false" data-msg-required="Requerido" 
                                       data-rule-maxlength="70" data-msg-maxlength="No debe exceder de {0} caracteres"   />
                                <span class="control-label">Mins</span>
                                <input name="soloMinsFin" type="text"  id="soloMinsFin"  
                                       placeholder="Ingrese Minutos. Por ej.: 05" value="" size="4" maxlength="2" 
                                       data-rule-required="false" data-msg-required="Requerido" 
                                       data-rule-maxlength="70" data-msg-maxlength="No debe exceder de {0} caracteres"   />
                                <span id="errmsg2"></span>
                            </span>  </div>
                    </div>
                    <div id="error" style="color:#F00"></div>
                    <div class="form-group dias_solicitados" data-fid="dias_solicitados">
                        <label class="control-label" for="dias_solicitados">Dias solicitados (d&iacute;as corridos)</label>
                        <input name="dias_solicitados" type="text"  id="dias_solicitados" size="4" maxlength="3" readonly/>
                    </div>

                    <div class="form-group rutAutorizador required" data-fid="rutAutorizador">
                        <label class="control-label" for="rutAutorizador">Autorizador</label>
                        <select class="form-control" id="rutAutorizador" name="rutAutorizador"   
                                data-rule-required="true" data-msg-required="Requerido" >
                            <%
                                Iterator<DetalleAusenciaVO> iteraAutorizadores = autorizadores.iterator();
                                while (iteraAutorizadores.hasNext()) {
                                    DetalleAusenciaVO autorizador = iteraAutorizadores.next();
                                    String label = autorizador.getNombreAutorizador()
                                            + "[" + autorizador.getNomDeptoAutorizador()
                                            + "-" + autorizador.getNomCencoAutorizador() + "]";
                            %>
                            <option value="<%=autorizador.getRutAutorizador()%>"><%=label%></option>
                            <%
                                }
                            %>
                        </select>
                    </div>
                    <div class="form-group submit f0 " data-fid="f0" style="position: relative;">
                        <label class="control-label sr-only" for="f0" style="display: block;">Submit Button</label>

                        <div class="progress" style="display: none; z-index: -1; position: absolute;">
                            <div class="progress-bar progress-bar-striped active" role="progressbar" style="width:100%">
                            </div>
                        </div>

                        <button type="button" id='btnGuardar' class="btn btn-primary btn-lg" style="z-index: 1;">
                            Guardar
                        </button>
                        <button type="button" class="btn btn-primary btn-lg" style="z-index: 1;" 
                                onClick="volver()">Volver
                        </button>

                    </div>

                    <div class="submit" data-redirect="hola.jsp">
                        <p class="error bg-warning" style="display:none;">
                        </p>
                    </div>

            </form>

        </div>

        <div class="container jf-thankyou" style="display:none;" data-redirect="" data-seconds="10">
            <h3>Datos ingresados correctamente

            </h3>
        </div>

        <form name="redirectForm" id="redirectForm" method="post" 
              action="<%=request.getContextPath()%>/mantencion/ausencias_detalle_2.jsp">  
            <input type="hidden" name="cco" id="cco" value="<%=cencoId%>">  
        </form>
        <!-- ----------------------------------------------- -->


        <script src="js/jquery.min.js"></script>
        <script src="js/bootstrap.min.js"></script>
        <script src="js/bootstrap-datepicker.min.js"></script>
        <script src="js/jquery.validate.min.js"></script>
        <script src="js/additional-methods.min.js"></script>
        <script src="js/jquery.scrollTo.min.js"></script>
        <script src="vendor.js" ></script>

        <script src="js/jquery.rut.js" type="text/javascript"></script>

        <script src="jqueryform.com.min.js?ver=v2.0.3&id=jqueryform-d81043" ></script>

        <!-- [ Start: iCheck support ] ---------------------------------- -->
        <link href="css/_all.min.css" rel="stylesheet">
        <style type="text/css">
            /* overwrite bootstrap styles */
            .checkbox input[type=checkbox], .checkbox-inline input[type=checkbox], .radio input[type=radio], .radio-inline input[type=radio] {
                position: relative;
                margin-top: 0px;
                margin-left: 2px;
            }

            .checkbox label, .radio label {
                padding-left: 4px;
            }

        </style>

        <script src="js/icheck.min.js"></script>
        <script type="text/javascript">


                    ;
                    (function ($, undefined)
                    {

                        var checkers = '.icheckbox_flat-blue, .iradio_flat-blue';

                        function initICheck($input) {
                            $input.iCheck({
                                checkboxClass: 'icheckbox_flat-blue',
                                radioClass: 'iradio_flat-blue'
                            }).on('ifClicked', function (e) {
                                setTimeout(function () {
                                    $(e.target).valid();
                                    $(e.target).trigger('change').trigger('handleOptionBox');
                                }, 250);
                            });
                        }
                        ; // func

                        //$('.jf-form .checkbox, .jf-form .radio')
                        $('.jf-form input[type="checkbox"], .jf-form input[type="radio"]').each(function (i, e) {
                            var $input = $(e), $div = $input.closest('.checkbox,.radio'), hasImg = $div.find('label > img').length;
                            if (hasImg) {

                                $input.css({'opacity': '0', 'position': 'absolute', 'left': '-1000px', 'right': '-1000px'});

                            } else {

                                initICheck($input);

                                // IE11 and under, the table-cell makes the checkboxes/radio buttons not clickable
                                var isWin = navigator.platform.indexOf('Win') > -1,
                                        isEdge = navigator.userAgent.indexOf('Edge/') > -1,
                                        noTableCell = isWin && !isEdge;
                                if (!noTableCell) {
                                    $div.find('label').css({display: 'table-cell'});
                                    $(checkers).css({display: 'table-cell'});
                                }
                                ;

                            }
                            ;
                        });

                    })(jQuery);

        </script>
        <!-- [ End: iCheck support ] ---------------------------------- -->


        <!-- [ Start: Select2 support ] ---------------------------------- -->
        <link rel="stylesheet" type="text/css" href="css/select2.min.css">
        <script type="text/javascript" src="js/select2.full.min.js"></script>
        <style type="text/css">
            .select2-hidden-accessible{
                opacity: 0;
                width:1% !important;
            }
            .select2-container .select2-selection--single{
                /*height: 34px;
                padding-top: 2px;
                box-shadow: inset 0 1px 1px rgba(0,0,0,.075);
                border: 1px solid #ccc;*/
            }
            .select2-container--default .select2-selection--single .select2-selection__arrow{
                top: 4px;
            }
        </style>
        <script type="text/javascript">
                ;
                (function () {

                    function templateResult(obj) {
                        if (!obj.id) {
                            return obj.text; }

                        var img = $(obj.element).data('imgSrc');
                        if (img) {
                            return $('<span><img src="' + img + '" class="img-flag" /> ' + obj.text + '</span>');
                        };

                        return obj.text;
                    };

                    /*$(".jf-form select").css('width', '50%'); // make it responsive
                     $(".jf-form select").select2({
                     templateResult: templateResult
                     }).change( function(e){
                     $(e.target).valid();
                     });
                     */
                })();
        </script>
        <!-- [ End: Select2 support ] ---------------------------------- -->

        <script type="text/javascript">

            // start jqueryform initialization
            // --------------------------------
            JF.init('#jqueryform-d81043');

            $(document).ready(function () {
                //rutEmpleado

                //evento para combo centro costo
                $('#cencoId').change(function (event) {
                    //var tokenCenco = $('select#cencoId').val().split("|");
                    var empresaSelected = null;
                    var deptoSelected = null;
                    var cencoSelected = $('select#cencoId').val();

                    var sourceSelected = 'ausencias_detalle_crear';
                    $.get('<%=request.getContextPath()%>/JsonListServlet', {
                        empresaID: empresaSelected, deptoID: deptoSelected, cencoID: cencoSelected, source: sourceSelected
                    }, function (response) {
                        var select = $('#rutEmpleado');
                        select.find('option').remove();
                        //$("#rutEmpleado").empty();
                        var newoption = "";
                        newoption += "<option value='-1'>Seleccione Empleado</option>";
                        for (i = 0; i < response.length; i++) {
                            //var auxNombre = '['+response[i].rut+'] '+response[i].nombres + 
                            //    ' ' + response[i].apePaterno + ' '+response[i].apeMaterno;
                            var auxNombre = '[' + response[i].rut + '] ' + response[i].nombres +
                                    ' ' + response[i].apePaterno
                                    + ' (' + response[i].nombreTurno + ').';
                            newoption += "<option value='" + response[i].rut + "'>" + auxNombre + "</option>";
                        }
                        $('#rutEmpleado').html(newoption);
                    });
                });

                $("#jqueryform-d81043").validate();
            <%if (msgError != null) {%>
                $('.modal').toggleClass('is-visible');
            <%}%>
                $('.modal-toggle').on('click', function (e) {
                    //alert('aquii');
                    e.preventDefault();
                    $('.modal').toggleClass('is-visible');
                });
                function closeDialog() {
                    $('.modal').toggleClass('is-visible');
                }

                $("#fechaInicioAsStr").change(function () {
                    var fechaInicio = $(this).val();
                    var ausenciaPorHora = $('#permiteHora').find('option:selected').val();
                    if (ausenciaPorHora === 'S') {//ausencia por hora
                        $('#fechaFinAsStr').datepicker('setDate', fechaInicio);
                    }
                });

                $("#fechaFinAsStr").change(function () {
                    var fechaInicio = $(this).val();
                    var ausenciaPorHora = $('#permiteHora').find('option:selected').val();
                    if (ausenciaPorHora === 'N' && fechaInicio !== '') {//ausencia por dia
                        calculate();
                        // $('#diasSolicitados').datepicker('setDate', fechaInicio);
                    }
                });

                $("#fechaInicioAsStr").change(function () {
                    var fechaFin = $(this).val();
                    var ausenciaPorHora = $('#permiteHora').find('option:selected').val();
                    if (ausenciaPorHora === 'N' && fechaFin !== '') {//ausencia por dia
                        calculate();
                        // $('#diasSolicitados').datepicker('setDate', fechaInicio);
                    }
                });

                jQuery("#permiteHora").change(function () {
                    var value = jQuery(this).children(":selected").attr("value");
                    if (value === 'N') {
                        deshabilitarCampo('soloHoraInicio');
                        deshabilitarCampo('soloMinsInicio');
                        deshabilitarCampo('soloHoraFin');
                        deshabilitarCampo('soloMinsFin');
                    } else {
                        habilitarCampo('soloHoraInicio');
                        habilitarCampo('soloMinsInicio');
                        habilitarCampo('soloHoraFin');
                        habilitarCampo('soloMinsFin');

                    }
                });

                deshabilitarCampo('soloHoraInicio');
                deshabilitarCampo('soloMinsInicio');
                deshabilitarCampo('soloHoraFin');
                deshabilitarCampo('soloMinsFin');

                //validacion HORA INICIO
                $("#soloHoraInicio").change(function (e) {
                    var valor = parseInt($(this).val());
                    if (valor > 23) {
                        $("#errmsg1").html("Hora no valida").show().fadeOut(3000);
                        return false;
                    }
                });

                $("#soloHoraInicio").keypress(function (e) {
                    //if the letter is not digit then display error and don't type anything
                    var valor = $(this).val();
                    if (e.which !== 8 && e.which !== 0 && (e.which < 48 || e.which > 57)) {
                        //display error message
                        $("#errmsg1").html("Solo numeros").show().fadeOut(3000);
                        return false;
                    }
                });
                //MINUTOS INICIO
                $("#soloMinsInicio").change(function (e) {
                    var valor = parseInt($(this).val());
                    //alert('hora inicio change.value: '+valor);
                    if (valor > 59) {
                        $("#errmsg1").html("Minutos no validos").show().fadeOut(3000);
                        return false;
                    }
                });

                //called when key is pressed in textbox
                $("#soloMinsInicio").keypress(function (e) {
                    //if the letter is not digit then display error and don't type anything
                    var valor = $(this).val();
                    if (e.which !== 8 && e.which !== 0 && (e.which < 48 || e.which > 57)) {
                        //display error message
                        $("#errmsg1").html("Solo numeros").show().fadeOut(3000);
                        return false;
                    }
                });

                //validacion HORA FIN
                $("#soloHoraFin").change(function (e) {
                    var valor = parseInt($(this).val());
                    if (valor > 23) {
                        $("#errmsg2").html("Hora no valida").show().fadeOut(3000);
                        return false;
                    }
                });

                $("#soloHoraFin").keypress(function (e) {
                    //if the letter is not digit then display error and don't type anything
                    var valor = $(this).val();
                    if (e.which !== 8 && e.which !== 0 && (e.which < 48 || e.which > 57)) {
                        //display error message
                        $("#errmsg2").html("Solo numeros").show().fadeOut(3000);
                        return false;
                    }
                });
                //MINUTOS Fin
                $("#soloMinsFin").change(function (e) {
                    var valor = parseInt($(this).val());
                    if (valor > 59) {
                        $("#errmsg2").html("Minutos no validos").show().fadeOut(3000);
                        return false;
                    }
                });

                //called when key is pressed in textbox
                $("#soloMinsFin").keypress(function (e) {
                    //if the letter is not digit then display error and don't type anything
                    //var valor = $(this).val();
                    if (e.which !== 8 && e.which !== 0 && (e.which < 48 || e.which > 57)) {
                        //display error message
                        $("#errmsg2").html("Solo numeros").show().fadeOut(3000);
                        return false;
                    }
                });

                //btnGuardar	
                $("#btnGuardar").click(function (event) {
                    var horaInicio = $('#soloHoraInicio').val();
                    var minsInicio = $('#soloMinsInicio').val();
                    var horaFin = $('#soloHoraFin').val();
                    var minsFin = $('#soloMinsFin').val();
                    if (checkDate()) {
                        var ausenciaPorHora = $('#permiteHora').find('option:selected').val();
                        if (ausenciaPorHora === 'S') {//ausencia por hora				
                            if (horaInicio === '' || minsInicio === '' || horaFin === '' || minsFin === '') {
                                alert('Debe completar horas y minutos.');
                                event.preventDefault();
                            } else {
                                $("#jqueryform-d81043").submit();
                            }
                        } else
                            $("#jqueryform-d81043").submit();
                    }
                });


                var $from = $("#fechaInicioAsStr"), $to = $("#fechaFinAsStr");

                function calculate() {
                    //var dayFrom = $from.datepicker('getDate');
                    //var dayTo = $to.datepicker('getDate');

                    var dayFrom = document.getElementById('fechaInicioAsStr').value;
                    var dayTo = document.getElementById('fechaFinAsStr').value;
                    if (dayFrom !== '' && dayTo !== '') {
                        //alert('desde: ' + dayFrom + ', hasta: ' + dayTo);
                        var resDesde = dayFrom.split("-");
                        var resHasta = dayTo.split("-");

                        var startDate = Date.parse(resDesde[2] + "-" + resDesde[1] + "-" + resDesde[0]);
                        var endDate = Date.parse(resHasta[2] + "-" + resHasta[1] + "-" + resHasta[0]);
                        var timeDiff = endDate - startDate;
                        var daysDiff = Math.floor(timeDiff / (1000 * 60 * 60 * 24));
                        daysDiff++;
                        document.getElementById('dias_solicitados').value = daysDiff;
                    }
                    /*
                     if (dayFrom && dayTo) {
                     var days = calcDaysBetween(dayFrom, dayTo);
                     days++;
                     document.getElementById('dias_solicitados').value = days;
                     }*/
                }

                function calcDaysBetween(startDate, endDate) {
                    var intResult = (endDate - startDate) / (1000 * 60 * 60 * 24);
                    if (intResult < 0)
                        intResult = 0;
                    return intResult;
                }

                var sDate, eDate;
                $("#fechaInicioAsStr").datepicker().on('changeDate', function (e) {
                    sDate = new Date($(this).datepicker('getUTCDate'));
                    checkDate();
                });

                $("#fechaFinAsStr").datepicker().on('changeDate', function (date) {
                    eDate = new Date($(this).datepicker('getUTCDate'));
                    checkDate();
                });

                function checkDate()
                {
                    if (sDate && eDate && (eDate < sDate))
                    {
                        $('#error').text("La 'fecha fin' debe ser inferior a la fecha de inicio");
                        return false;
                    } else
                    {
                        $('#error').text("");
                        return true;
                    }
                }
            });
        </script>
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

    </body>
</html>