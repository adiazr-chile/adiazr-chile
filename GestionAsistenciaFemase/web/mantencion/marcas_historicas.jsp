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
    List<EmpresaVO> empresas            = (List<EmpresaVO>)session.getAttribute("empresas");
    List<CargoVO> cargos                = (List<CargoVO>)session.getAttribute("cargos"); 
    List<UsuarioCentroCostoVO> cencos   = (List<UsuarioCentroCostoVO>)session.getAttribute("cencos_empleado"); 
    
    String startDate = (String)session.getAttribute("startDate");
    String endDate = (String)session.getAttribute("endDate");
  
    String paramEmpresa = (String)request.getAttribute("empresa");
    String paramRut     = (String)request.getAttribute("rut_empleado");
  
%>
<!DOCTYPE html>

<html>
<head>
    <meta charset="utf-8" />

    <meta name="keywords" content="femase,gestion,web">
    <meta name="description" content="Sistema de Gestion FEMASE-RRHH-Control Asistencia">
    <meta name="author" content="Adiazr">

    <title>Marcas Historicas</title>
 
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

   
    </script>
    
</head>
<body>
    <div class="site-container">
        <div class="main-header" style="position: relative">
            <h1>Registros de asistencia</h1>
            <h2>Hist&oacute;rico</h2>
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
            <label>Dispositivo
                <select name="dispositivo" id="dispositivo">
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

<div id="MarcasTableContainer"></div>
<script type="text/javascript">
    <% 
        String readonly="readonly";
        if (theUser.getIdPerfil() == Constantes.ID_PERFIL_ADMIN){
            readonly="";
        }
    %>
    $(document).ready(function () {

        $('#MarcasTableContainer').jtable({       
            title: 'Resultados',
            paging: true, //Enable paging
            pageSize: 10, //Set page size (default: 10)
            sorting: true, //Enable sorting
            defaultSorting: 'rut_empleado ASC', //Set default sorting
            actions: {
                listAction: '<%=request.getContextPath()%>/MarcasHistController?action=list'
            },
            fields: {
                rutEmpleado: {
                    title: 'Rut',
                    width: '10%',
                    key: false,
                    list: true,
                    create: true,
                    edit: true,
                    sorting: false,
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
                    key: false,
                    list: true,
                    create:true,
                    edit:false,
                    sorting: false,
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=empresas';
                    }
                },    
                rutKey: {
                    title: 'Rut',
                    width: '10%',
                    key: false,
                    list: false,
                    type: 'hidden',
                    sorting: false
                },
                rowKey: {
                    title: 'Rut',
                    width: '10%',
                    key: true,
                    list: false,
                    type: 'hidden',
                    sorting: false
                },    
                fechaHoraStr: {
                    title: 'Fecha Hora marca',
                    width: '10%',
                    key: false,
                    create: false,
                    edit:false
                },
                fechaHora: {
                    title: 'Fecha marca',
                    list:false,
                    width: '10%',
                    key: false,
                    create: true,
                    type: 'date',
                    displayFormat: 'yy-mm-dd',
                    inputClass: 'validate[required,custom[date]]',
                    input: function (data) {
                        if (data.record) {
                            return '<input type="text" name="fechaHora" style="width:200px" value="' + data.record.fecha + '" <%=readonly%> />';
                        }else{
                            return '<input type="text" name="fechaHora" style="width:200px" value=""/>';
                        }
                    }                     
		},
                fechaHoraKey: {
                    title: 'Fecha Hora marca',
                    width: '10%',
                    key: false,
                    list: false,
                    type: 'hidden',
                    edit:false
                },
                correlativo: {
                    title: 'Correlativo',
                    width: '10%',
                    key: false,
                    create: false,
                    edit:false,
                    sorting: false
                },
                fecha: {
                    title: 'Fecha solo',
                    width: '10%',
                    key: false,
                    list: false,
                    type: 'hidden',
                    edit:false
                },
                hora: {
                    title: 'Hora marca',
                    width: '10%',
                    create: true,
                    list: false,
                    edit: true,
                    input: function (data) {
                        if (data.record) {
                            return '<input type="text" name="hora" style="width:200px" value="' + data.record.hora + '" <%=readonly%> />';
                        }else{
                            return '<input type="text" name="hora" style="width:200px" value="" />';
                        }
                    }
                },
                minutos: {
                    title: 'Mins marca',
                    width: '10%',
                    list: false,
                    edit: true,
                    create:true,
                    input: function (data) {
                        if (data.record) {
                            return '<input type="text" name="minutos" style="width:200px" value="' + data.record.minutos + '" <%=readonly%> />';
                        }else{
                            return '<input type="text" name="minutos" style="width:200px" value="" />';
                        }
                    }
                },
                tipoMarca: {
                    title: 'Evento',
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
                    edit: false,
                    create:true,
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
                    sorting: false,
                    edit: false
                },
                hashcode: {
                    title: 'Hashcode',
                    width: '10%',
                    list: true,
                    sorting: false,
                    edit: false
                },
                comentario: {
                    title: 'Comentario',
                    width: '10%',
                    list: true,
                    sorting: false,
                    edit: true
                }
            },
            //Initialize validation logic when a form is created
            formCreated: function (event, data) {
                //alert('event type: '+event.type
                //        +',data.formType:'+data.formType);
                <% //if (theUser.getIdPerfil() == 1){%>
                    data.form.find('input[name="fechaHora"]').addClass('validate[required],custom[date]');
                    data.form.find('input[name="hora"]').addClass('validate[required],custom[integer]');
                    data.form.find('input[name="minutos"]').addClass('validate[required],custom[integer]');
                    data.form.find('input[name="codDispositivo"]').addClass('validate[required]');
                <%//}%>

                <%if (theUser.getIdPerfil() == Constantes.ID_PERFIL_ADMIN){%>
                    data.form.find('input[name="fechaHora"]').datepicker({dateFormat: 'yy-mm-dd'});
                <%}else{%>
                    if (data.formType=='create'){
                        data.form.find('input[name="fechaHora"]').datepicker({dateFormat: 'yy-mm-dd'});
                    }
                <%}%>
                data.form.find('input[name="comentario"]').addClass('validate[required]');
                data.form.validationEngine();
            },
            //Validate form when it is being submitted
            formSubmitting: function (event, data) {
                return data.form.validationEngine('validate');
            },
            //Dispose validation logic when form is closed
            formClosed: function (event, data) {
                data.form.validationEngine('hide');
                data.form.validationEngine('detach');
            },
            rowInserted: function(event, data){
              var auxint=data.record.tipoMarca;
              if (auxint === 100 || auxint === 200){
                data.row.find('.jtable-edit-command-button').hide();
                data.row.find('.jtable-delete-command-button').hide();
                data.row.css("background", "#da0021");
                data.row.css("color", "#ffffff");
              }
            }
        });

        //Re-load records when user click 'load records' button.
        $('#LoadRecordsButton').click(function (e) {
            e.preventDefault();
            $('#MarcasTableContainer').jtable('load', {
                empresaId: $('#empresaId').val(),
                deptoId: $('#deptoId').val(),
                cencoId: $('#cencoId').val(),
                rutEmpleado: $('#rut').val(),
                startDate: $('#startDate').val(),
                endDate: $('#endDate').val(),
                dispositivoId: $('#dispositivo').val()
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
            $('#startDate').datepick(
                {
                    dateFormat: 'yyyy-mm-dd'
                }
            );
            $('#endDate').datepick({dateFormat: 'yyyy-mm-dd'});
        });

    </script>                                         
                                            
</body>
</html>
