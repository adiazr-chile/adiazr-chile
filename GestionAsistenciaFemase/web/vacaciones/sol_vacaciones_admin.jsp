<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.Calendar"%>
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
    Calendar mycal          = Calendar.getInstance(new Locale("es","CL"));
    Date currentDate        = mycal.getTime();
    SimpleDateFormat sdf    = new SimpleDateFormat("yyyy-MM-dd");
    String fechaActual      = sdf.format(currentDate);
    
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

    <title>Solicitud Vacaciones Admin</title>

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
            $('#cencoId').change(function(event) 
            {
                var empresaSelected  = null;
                var deptoSelected    = null;
                var cencoSelected    = $('select#cencoId').val();
                var sourceSelected = 'solicitudes_admin';
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
   
    </script>
    
    <style>
        div.filtering
        {
            border: 1px solid #999;
            margin-bottom: 5px;
            padding: 10px;
            background-color: #EEE;
        }
        
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
            <h1>Solicitudes de Vacaciones</h1>
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
        
        <label>Fecha Inicio vacaci&oacute;n, desde:
            <input name="startVacacion" type="text" id="startVacacion">
            hasta <input name="endVacacion" type="text" id="endVacacion">
        </label> 
                    
        </label>
        
        <button type="submit" id="LoadRecordsButton">Buscar</button>
        
    </form>


<div id="SolAdminTableContainer"></div>
<script type="text/javascript">

    $(document).ready(function () {
        
        $('#SolAdminTableContainer').jtable({       
            title: 'Solicitudes existentes',
            paging: true, //Enable paging
            pageSize: 10, //Set page size (default: 10)
            sorting: true, //Enable sorting
            defaultSorting: 'sv.solic_fec_ingreso desc', //Set default sorting
            actions: {
                listAction: '<%=request.getContextPath()%>/servlet/SolicitudVacacionesController?action=listAdmin'
            },
            fields: {
		id:{
                    title: 'ID',
                    width: '3%',
                    edit:true,
                    create:false,
                    key:true,
                    list: true,
                    sorting: true,
                    input: function (data) {
                        if (data.record) {
                            return '<input type="text" id="id" name="id" style="width:50px" value="' + data.record.id + '" readonly />';
                        } 
                    }
                },
		rutEmpleado: {
                    title:'RUN Empleado',
                    width: '6%',
                    list: true,
                    create:false,
                    sorting: false,
                    edit:true,
                    input: function (data) {
                        if (data.record) {
                            return '<input type="text" id="rutEmpleado" name="rutEmpleado" style="width:100px" value="' + data.record.rutEmpleado + '" readonly />';
                        } 
                    }
                },
                nombreEmpleado: {
                    title:'Nombre Empleado',
                    width: '14%',
                    list: true,
                    create:false,
                    sorting: false,
                    edit:true,
                    input: function (data) {
                        if (data.record) {
                            return '<input type="text" id="nombreEmpleado" name="nombreEmpleado" style="width:200px" value="' + data.record.nombreEmpleado + '" readonly />';
                        } 
                    }
                },
                fechaIngreso: {
                    title:'Fec. Generacion',
                    width: '10%',
                    list: true,
                    create:false,
                    edit:false,
                    sorting: true
                },
                estadoLabel: {
                    title:'Estado',
                    width: '6%',
                    list: true,
                    create:false,
                    edit:false,
                    sorting: true
                },
                inicioVacaciones:{
                    title: 'Inicio vacaciones',
                    width: '8%',
                    type: 'date',
                    displayFormat: 'yy-mm-dd',
                    edit:false,
                    create:true,
                    list: true,
                    sorting: true
                },
                finVacaciones:{
                    title: 'Termino vacaciones',
                    width: '8%',
                    type: 'date',
                    displayFormat: 'yy-mm-dd',
                    edit:false,
                    create:true,
                    list: true,
                    sorting: false
                },
                fechaHoraCancela: {
                    title:'Fecha hora cancelacion',
                    width: '10%',
                    list: true,
                    create:false,
                    edit:false,
                    sorting: false
                },
		fechaHoraApruebaRechaza: {
                    title:'Fec. hora aprueba/rechaza',
                    width: '12%',
                    list: true,
                    create:false,
                    edit:false,
                    sorting: false
                },    
                usernameApruebaRechaza: {
                    title:'Aprueba/Rechaza',
                    width: '9%',
                    list: true,
                    create:false,
                    edit:false,
                    sorting: false
                },
                notaObservacion: {
                    title:'Nota/Observacion',
                    width: '14%',
                    list: true,
                    create:false,
                    edit:false,
                    sorting: false
                },
                Accion:{
                    title: 'Accion',
                    width: '10%',
                    options: function(data){
                        var auxEstadoId = data.record.estadoId;
                        return '<%=request.getContextPath()%>/LoadItems?type=acciones_solicitud&estadoSolicitud='+auxEstadoId;
                    },
                    create:false,
                    list: false,
                    edit:true,
                    sorting: false
                }    
            },
            //Initialize validation logic when a form is created
            formCreated: function (event, data) {
                var element = document.getElementById("Edit-Accion");
                //If it isn't "undefined" and it isn't "null", then it exists.
                if(typeof(element) !== 'undefined' && element !== null){
                    var accionSelected = $("select#Edit-Accion").val();
                    //alert('Accion seleccionada: ' + accionSelected);
                    //$("#EditDialogSaveButton").attr("disabled", true);
                    if (accionSelected === '-1'){
                        document.getElementById("EditDialogSaveButton").setAttribute("disabled", "true");
                    }
                } else{
                    //alert('Element does not exist!');
                }
                        
                //return false;
                //data.form.validationEngine();
            }
        });

        //Re-load records when user click 'load records' button.
        $('#LoadRecordsButton').click(function (e) {
            e.preventDefault();
            $('#SolAdminTableContainer').jtable('load', {
                cencoId: $('#cencoId').val(),
				rutEmpleado: $('#paramRutEmpleado').val(),
				startVacacion: $('#startVacacion').val(),
                endVacacion: $('#endVacacion').val()
            });
        });

        //Load all records when page is first shown
        $('#LoadRecordsButton').click();
    });

</script>
<br />
<hr />
<div class="tabsContainer"><div id="tabs-webforms"></div>
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

    $.datepicker.regional['es'] = {
        closeText: 'Cerrar',
        prevText: '< Ant',
        nextText: 'Sig >',
        currentText: 'Hoy',
        monthNames: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 	'Diciembre'],
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
        $('#startVacacion').datepick(
			{
				dateFormat: 'yyyy-mm-dd',
				//minDate: -180,
				directionReverse: true
			}
        );
        $('#endVacacion').datepick(
			{
				dateFormat: 'yyyy-mm-dd',
				//minDate: -180,
				directionReverse: true
			}
        );
    });

</script>
  </body>
</html>
