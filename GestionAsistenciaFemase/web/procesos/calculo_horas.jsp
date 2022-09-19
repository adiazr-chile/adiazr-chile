<%@page import="cl.femase.gestionweb.common.Constantes"%>
<%@ include file="/include/check_session.jsp" %>

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
    List<EmpresaVO> empresas            = (List<EmpresaVO>)session.getAttribute("empresas");
    List<CargoVO> cargos                = (List<CargoVO>)session.getAttribute("cargos");    
    String startDate = (String)session.getAttribute("startDate");
    String endDate = (String)session.getAttribute("endDate");
    //ID_PERFIL_DIRECTOR
    System.out.println("[GestionFemaseWeb]calculoHoras.jsp]"
        + "startDate= "+startDate
        + ",endDate= "+endDate);
%>
<!DOCTYPE html>

<html>
<head>
    <meta charset="utf-8" />

    <meta name="keywords" content="femase,gestion,web">
    <meta name="description" content="Sistema de Gestion FEMASE-RRHH-Control Asistencia">
    <meta name="author" content="Adiazr">
            
    <title>Calculo Asistencia</title>
 
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
                 var empresaSelected = $("select#empresaId").val();
                 var deptoSelected = $("select#deptoId").val();
                 var cencoSelected = $("select#cencoId").val();
                 //alert('empresa: '+empresaSelected+',depto: '+deptoSelected+',cenco: '+cencoSelected);
                 var sourceSelected = 'calculo_asistencia';
                 $.get('<%=request.getContextPath()%>/JsonListServlet', {
                 	empresaID : empresaSelected,deptoID : deptoSelected,cencoID : cencoSelected,source: sourceSelected
                 }, function(response) {
                        var select = $('#rut');
                        select.find('option').remove();
                        var newoption = "";
                        newoption += "<option value='todos'>Todos</option>";
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
                //alert('aquii');
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
            <h1>Calcular Asistencia<span class="light"></span></h1>
            <h2>Empleados</h2>
        </div>
<div class="content-container">
            
            <div class="padded-content-container">
                
<div class="filtering">
    <form>
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
            <select id="deptoId" name="deptoId" style="width:200px;" required>
                <option value="-1">--------</option>
            </select>
                
            <label>Centro Costo
                <select name="cencoId" id="cencoId">
                    <option value="-1" selected>----------</option>
                </select>
            </label>
            <label>Empleado
                <select multiple name="rut" id="rut">
                    <option value="-1" selected>----------</option>
                </select>
            </label>    
            <label>Fecha desde:
                <input name="startDate" type="text" id="startDate">
                hasta <input name="endDate" type="text" id="endDate">
            </label>
        <button type="submit" id="LoadRecordsButton">Calcular</button>
    </form>
</div>

<div id="CalcHorasTableContainer"></div>
<script type="text/javascript">

    $(document).ready(function () {

        $('#CalcHorasTableContainer').jtable({       
            title: 'Empleados',
            paging: true, //Enable paging
            pageSize: 10, //Set page size (default: 10)
            sorting: true, //Enable sorting
            defaultSorting: 'rutEmpleado ASC', //Set default sorting
            actions: {
                //listAction: '<%=request.getContextPath()%>/DetalleAsistenciaController?action=calcular'
                listAction: function (postData) {
                    //console.log("creating from custom function...");
                    return $.Deferred(function ($dfd) {
                        $.ajax({
                            url: '<%=request.getContextPath()%>/DetalleAsistenciaController?action=calcular',
                            type: 'POST',
                            dataType: 'json',
                            data: postData,
                            success: function (data) {
                                $dfd.resolve(data);
                            },
                            error: function (data) {
                                if (data.Result !== 'OK') {
                                    $dfd.reject();
                                    $('.modal').toggleClass('is-visible');
                                    return;
                                }
                            }
                        });
                    });
                }
    
                /*
                listAction: function (postData, jtParams) {
                    console.log("Loading from custom function...");
                    return $.Deferred(function ($dfd) {
                        $.ajax({
                            url: '<%=request.getContextPath()%>/DetalleAsistenciaController?action=calcular',
                            type: 'POST',
                            dataType: 'json',
                            data: postData,
                            success: function (data) {
                                $dfd.resolve(data);
                            },
                            error: function (jqXHR, textStatus, errorThrown) {
                                // $('#result').html('<p>status code: '+jqXHR.status+'</p><p>errorThrown: ' + errorThrown + '</p><p>jqXHR.responseText:</p><div>'+jqXHR.responseText + '</div>');
                                alert('Error al calcular asistencia. jqXHR.responseText= ' + jqXHR.responseText);
                                //$dfd.reject();
                                 //_showError: function (message) {
                                  //  this._$errorDialogDiv.html(jqXHR.responseText).dialog('open');
                                //}
                            }
                        });
                    });
                }*/
            },
            fields: {
                rut: {
                    title:'Rut',
                    width: '14%',
                    key: true,
                    list: true,
                    create:false
                },
                //CHILD TABLE DEFINITION FOR "Tiempo Extra"
                ResultadosCalculo: {
                    title: '',
                    width: '5%',
                    sorting: false,
                    edit: false,
                    create: false,
                    display: function (empleadoData) {
                        //Create an image that will be used to open child table
                        var $img = $('<img src="<%=request.getContextPath()%>/images/reloj.png" title="Ver resultados calculo" />');
                        //Open child table when user clicks the image
                        $img.click(function () {
                            $('#CalcHorasTableContainer').jtable('openChildTable',
                                $img.closest('tr'),
                                {
                                    title: empleadoData.record.nombres + ' - Resultados',
                                    actions: {
                                        listAction: '<%=request.getContextPath()%>/DetalleAsistenciaController?'
                                            +'action=listResults'
                                            +'&rut=' + empleadoData.record.rut
                                            +'&startDate='+document.getElementById("startDate").value
                                            +'&endDate='+document.getElementById("endDate").value
                                    },
                                    fields: {
                                        /*rut: {
                                            type: 'hidden',
                                            defaultValue: empleadoData.record.rut
                                        },*/
                                        labelFechaEntradaMarca: {
                                            title: 'Fecha Entrada',
                                            width: '10%',
                                            key: true,
                                            list: true
                                            //,type: 'date',
                                            //displayFormat: 'yy-mm-dd'
                                        },
                                        horaEntrada: {
                                            title: 'Entrada',
                                            width: '10%'
                                        },
                                        labelFechaSalidaMarca: {
                                            title: 'Fecha Salida',
                                            width: '10%',
                                            key: true,
                                            list: true
                                            //,type: 'date',
                                            //displayFormat: 'yy-mm-dd'
                                        },
                                        horaSalida: {
                                            title: 'Salida',
                                            width: '10%'
                                        },
                                        minutosReales: {
                                            title: 'Mins reales',
                                            width: '10%',
                                            list: true
                                        },
                                        minutosExtrasAl50: {
                                            title: 'Mins extras 50',
                                            width: '10%',
                                            list: true
                                        },
                                        minutosExtrasAl100: {
                                            title: 'Mins extras 100',
                                            width: '10%',
                                            list: true
                                        },
                                        holguraMinutos: {
                                            title: 'Holgura (Mins)',
                                            width: '10%',
                                            list: true
                                        },
                                        esFeriado: {
                                            title: 'Feriado',
                                            width: '10%',
                                            list: true
                                        },
                                        horaInicioAusencia: {
                                            title: 'Ini Ausencia',
                                            width: '15%',
                                            list: true
                                        },
                                        horaFinAusencia: {
                                            title: 'Fin Ausencia',
                                            width: '15%',
                                            list: true
                                        }
                                    }
                                }, function (data) { //opened handler
                                    data.childTable.jtable('load');
                                    });
                        });
                        //Return image to show on the person row
                        return $img;
                    }
                },
                empresaNombre:{
                    title: 'Empresa',
                    width: '14%',
                    edit:true
                },
                deptoNombre:{
                    title: 'Departamento',
                    width: '14%',
                    edit:true
                },
                cencoNombre:{
                    title: 'Centro Costo',
                    width: '14%',
                    edit:true
                }
            }
        });

        //Re-load records when user click 'load records' button.
        $('#LoadRecordsButton').click(function (e) {
            e.preventDefault();
            if ($('#startDate').val()!==''){
                $("#LoadRecordsButton").prop('disabled', true);
            } 
            $('#CalcHorasTableContainer').jtable('load', {
                empresaId: $('#empresaId').val(),
                deptoId: $('#deptoId').val(),
                cencoId: $('#cencoId').val(),
                rut: $('#rut').val(),
                startDate: $('#startDate').val(),
                endDate: $('#endDate').val()
            });
            $("#LoadRecordsButton").prop('disabled', false);
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
    
    <div class="modal">
        <div class="modal-overlay modal-toggle"></div>
        <div class="modal-wrapper modal-transition">
          <div class="modal-header">
            <button class="modal-close modal-toggle"><svg class="icon-close icon" viewBox="0 0 32 32"><use xlink:href="#icon-close"></use></svg></button>
            <h2 class="modal-heading">Error</h2>
          </div>

          <div class="modal-body">
            <div class="modal-content">
              <p>Ya hay un calculo de asistencia en proceso...</p>
              <button onclick="closeDialog()">Cerrar</button>
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
    
        $(function() {
        <%if (theUser.getIdPerfil() == Constantes.ID_PERFIL_ADMIN || theUser.getIdPerfil() == Constantes.ID_PERFIL_DIRECTOR){%>
                $('#startDate').datepick({dateFormat: 'yyyy-mm-dd',minDate: -180});
                $('#endDate').datepick({dateFormat: 'yyyy-mm-dd',minDate: -180});
            <%}else{%>
                $('#startDate').datepick({dateFormat: 'yyyy-mm-dd',minDate: -90});
                $('#endDate').datepick({dateFormat: 'yyyy-mm-dd',minDate: -90});
            <%}%>

        });

    </script>                                         
                                            
</body>
</html>
