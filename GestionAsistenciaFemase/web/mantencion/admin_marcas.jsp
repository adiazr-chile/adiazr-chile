<%@page import="java.util.Calendar"%>
<%@page import="java.util.Locale"%>
<%@page import="java.text.SimpleDateFormat"%>
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
    
    SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd", new Locale("es","CL"));
    Calendar calendart1 = Calendar.getInstance(new Locale("ES","cl"));
    Date auxfechaactual = calendart1.getTime();
    // Set the day of the month to the first day of the month
    calendart1.set(Calendar.DAY_OF_MONTH,
    calendart1.getActualMinimum(Calendar.DAY_OF_MONTH));
    // Extract the Date from the Calendar instance
    Date firstDayOfTheMonth = calendart1.getTime();
    //System.out.println("1er dia del mes: " + sdf3.format(calendart1.getTime()) + ", fecha actual: " + sdf3.format(auxfechaactual));        
    
    String startDate = sdf3.format(calendart1.getTime());
    String endDate = sdf3.format(auxfechaactual);
    
    System.out.println("[adminMarcas.jsp]"
        + "startDate= "+startDate
        + ",endDate= "+endDate);
    String paramEmpresa = (String)request.getAttribute("empresa");
    String paramRut     = (String)request.getAttribute("rut_empleado");
    System.out.println("[adminMarcas.jsp]"
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

    <title>Admin Marcas</title>
 
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
        
        .disabled
        {
            background: none repeat scroll 0 0 burlyWood;
            cursor: default !important;
        }
        
        .jtable-input-readonly{
            background-color:lightgray;
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
            $('#MarcasTableContainer').find('.jtable-toolbar-item.jtable-toolbar-item-add-record').hide();
            if (cencoId !== '-1'){
                var cencoSelected= $('#cencoId').find(":selected").text();
                var tokenLabelCenco = cencoSelected.split(",");
                empresaLabel  = tokenLabelCenco[0];
                deptoLabel    = tokenLabelCenco[1];
                cencoLabel    = tokenLabelCenco[2];
            }else if (rutEmpleado === '-1' || iddevice === '-1'){
                $('#MarcasTableContainer').find('.jtable-toolbar-item.jtable-toolbar-item-add-record').hide();
            }
        }
        
        function setEmpleado(rut){
            var iddevice=$("select#dispositivo").val();
            $('#MarcasTableContainer').find('.jtable-toolbar-item.jtable-toolbar-item-add-record').hide();
            if (rut !== '-1' && iddevice!=='-1'){
                $('#MarcasTableContainer').find('.jtable-toolbar-item.jtable-toolbar-item-add-record').show();
            }
        }
        
        function setDispositivo(dispositivoId){
            var rutEmpleado=$("select#rut").val();
            $('#MarcasTableContainer').find('.jtable-toolbar-item.jtable-toolbar-item-add-record').hide();
            if (rutEmpleado !== '-1' && dispositivoId !== '-1'){
                $('#MarcasTableContainer').find('.jtable-toolbar-item.jtable-toolbar-item-add-record').show();
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
                                ' ' + response[i].apePaterno 
                                + ' '+response[i].apeMaterno
                                + ' (' + response[i].nombreTurno + ')';
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
                        $('#MarcasTableContainer').find('.jtable-toolbar-item.jtable-toolbar-item-add-record').hide();
                 });
                 
                
             });
         });

   
    </script>
    
</head>
<body>
    <div class="site-container">
        <div class="main-header" style="position: relative">
            <h1>Registros de asistencia</h1>
            <h2>Administracion</h2>
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
            </se0lect>
            -->    
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
            
            <label>Dispositivo
                <select name="dispositivo" id="dispositivo" onChange="setDispositivo(this.value)">
                    <option value="-1" selected>----------</option>
                </select>
            </label>    
            
            <label>Fecha desde:
                <input name="startDate" type="text" id="startDate" value="<%=startDate%>">
                hasta <input name="endDate" type="text" id="endDate" value="<%=endDate%>">
            </label>
        <button type="submit" id="LoadRecordsButton">Buscar</button>
        <a href="<%=request.getContextPath()%>/mantencion/marcas_historicas.jsp">&nbsp;Ver Hist&oacute;rico</a>
    </form>
</div>

<div id="MarcasTableContainer"></div>
<script type="text/javascript">
    <% 
        String readonly="readonly";
        if (theUser.getIdPerfil() == 1){
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
                listAction: '<%=request.getContextPath()%>/MarcasController?action=list',
                <% if (theUser.getIdPerfil() != 2 && theUser.getIdPerfil() != 4 && theUser.getIdPerfil() != 7){%>
                    updateAction: '<%=request.getContextPath()%>/MarcasController?action=update',
                    //createAction: '<%=request.getContextPath()%>/MarcasController?action=create'
                    createAction: function (postData, jtParams) {
                        return $.Deferred(function ($dfd) {
                            $.ajax({
                                url: '<%=request.getContextPath()%>/MarcasController?action=create',
                                type: 'POST',
                                dataType: 'json',
                                data: postData,
                                success: function (data) {
                                    $dfd.resolve(data);
                                },
                                error: function (request, status, error) {
                                    alert('status: '+status+', error: '+error);
                                    $dfd.reject();
                                }
                            });
                        });
                    }
                <%}%>
                <% if (theUser.getIdPerfil() == 1){%>
                    ,deleteAction: '<%=request.getContextPath()%>/MarcasController?action=delete'
                <%}%>
            },
            fields: {
                rutEmpleado: {
                    title: 'Rut',
                    width: '11%',
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
                    width: '11%',
                    key: false,
                    list: false,
                    create:true,
                    edit:false,
                    sorting: false,
                    input: function (data) {
                        if (data.record) {
                            return '<input type="hidden" id="empresaCod" name="empresaCod" value="' + data.record.empresaCod + '">';
                        }else{
                            return '<input type="text" id="empresaCod" name="empresaCod" readonly>';
                        }
                    }
                    //options: function(data){
                    //    return '<%=request.getContextPath()%>/LoadItems?type=empresas'
                    //}
                },
                //CHILD TABLE DEFINITION FOR "Eventos realizados a la marca"
                eventos: {
                    title: '',
                    width: '5%',
                    sorting: false,
                    edit: false,
                    create: false,
                    display: function (marcaData) {
                        //Create an image that will be used to open child table
                        var $img = $('<img src="<%=request.getContextPath()%>/images/stock_view-details.png" title="Ver eventos" />');
                        //Open child table when user clicks the image
                        $img.click(function () {
                            $('#MarcasTableContainer').jtable('openChildTable',
                                $img.closest('tr'),
                                {
                                    title: 'Eventos para la marca: ' + marcaData.record.fechaHoraStr,
                                    actions: {
                                        listAction: '<%=request.getContextPath()%>/MarcasEventosController?'
                                            + 'action=showEvents'
                                            + '&empresa=' + marcaData.record.empresaCod
                                            + '&rut=' + marcaData.record.rutEmpleado
                                            + '&fecha=' + marcaData.record.fecha
                                            + '&tipo=' + marcaData.record.tipoMarca
                                    },
                                    fields: {
                                        correlativo: {
                                            title: 'Corr',
                                            width: '7%',
                                            key: true,
                                            list: true,
                                            sorting: true
                                        },
                                        codDispositivo: {
                                            title: 'Dispositivo',
                                            width: '7%',
                                            key: false,
                                            list: false,
                                            sorting: false
                                        },
                                        empresaCod: {
                                            title: 'Empresa',
                                            width: '7%',
                                            key: false,
                                            list: false,
                                            sorting: false,
                                            options: function(data){
                                                return '<%=request.getContextPath()%>/LoadItems?type=empresas'
                                            }
                                        },    
                                        rutEmpleado: {
                                            title: 'Rut',
                                            width: '7%',
                                            key: false,
                                            list: false,
                                            sorting: false
                                        },
                                        id: {
                                            title: 'Id',
                                            width: '7%',
                                            list: true,
                                            sorting: false,
                                            edit: false
                                        },
                                        hashcode: {
                                            title: 'Hashcode',
                                            width: '7%',
                                            list: true,
                                            sorting: false,
                                            edit: false
                                        },    
                                        fechaHoraModificacion: {
                                            title: 'Hora evento',
                                            width: '7%',
                                            sorting: true
                                        },
                                        codUsuario: {
                                            title: 'Usuario',
                                            width: '7%',  
                                            sorting: false
                                        },
                                        tipoEvento: {
                                            title: 'Tipo Evento',
                                            width: '11%',
                                            sorting: false,
                                            options: { 'M': 'Modificacion', 'E': 'Eliminacion' }
                                        },    
                                        fechaHoraOriginal: {
                                            title: 'Hora orig.',
                                            width: '7%',
                                            sorting: false
                                        },
                                        fechaHoraNew: {
                                            title: 'Hora nueva',
                                            width: '7%',
                                            sorting: false
                                        },
                                        tipoMarcaOriginal: {
                                            title: 'Tipo orig.',
                                            width: '7%',
                                            sorting: false,
                                            options: function(data){
                                                return '<%=request.getContextPath()%>/LoadItems?type=tiposMarcas'
                                            }
                                        },
                                        tipoMarcaNew: {
                                            title: 'Tipo nuevo',
                                            width: '7%',
                                            sorting: false,
                                            options: function(data){
                                                return '<%=request.getContextPath()%>/LoadItems?type=tiposMarcas';
                                            }
                                        },    
                                        comentarioOriginal: {
                                            title: 'Coment. orig.',
                                            width: '11%',
                                            sorting: false
                                        },  
                                        comentarioNew: {
                                            title: 'Coment. nuevo',
                                            width: '11%',
                                            sorting: false
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
                empresaKey:{
                    title: 'EmpresaKey',
                    width: '10%',
                    create: true,
                    list: false,
                    type: 'hidden'
                },        
                cencoId: {
                    title: 'Cenco',
                    width: '10%',
                    key: false,
                    create: false,
                    edit:false,
                    list: false
                },    
                fechaHoraStr: {
                    title: 'Fec. Hra marca',
                    width: '10%',
                    key: false,
                    create: false,
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
                            return '<input type="text" name="fechaHora" style="width:200px" value="" readonly/>';
                        }
                    }                     
		},
                fechaHoraKey: {
                    title: 'Fec Hora marca',
                    width: '10%',
                    key: false,
                    list: false,
                    type: 'hidden',
                    edit:false
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
                    width: '11%',
                    create: true,
                    list: false,
                    edit: true,
                    create:true,
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
                    width: '11%',
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
                segundos: {
                    title: 'Segs marca',
                    width: '10%',
                    type: 'hidden',
                    list: false,
                    edit: true,
                    create:false
                },
                tipoMarca: {
                    title: 'Evento',
                    width: '11%',
                    sorting: false,
                    edit: true,
                    //options: { '1': 'Entrada', '2': 'Salida' }
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=tiposMarcas'
                    }
                },
                labelTurno: {
                    title: 'Turno',
                    width: '10%',
                    key: false,
                    create: false,
                    edit:false
                },
                masInfo: {
                    title: 'Mas info',
                    width: '10%',
                    list: true,
                    key: false,
                    create: false,
                    edit:false,
                    sorting: false
                },
                codDispositivo: {
                    title: 'Cod dispositivo',
                    width: '11%',
                    list: true,
                    edit: false,
                    create:true,
                    input: function (data) {
                        var deviceSelected = $("select#dispositivo").val();
                        if (data.record) {
                            return '<input type="text" name="aDevice" style="width:200px" value="' + data.record.codDispositivo + '" readonly />';
                        } else {
                            return '<input type="text" name="aDevice" style="width:200px" value="'+deviceSelected+'" readonly/>';
                        }
                    },
                    sorting: false
                },
                id: {
                    title: 'Id',
                    width: '11%',
                    list: true,
                    sorting: false,
                    edit: false,
                    create: false
                },
                hashcode: {
                    title: 'Hashcode',
                    width: '11%',
                    list: true,
                    sorting: false,
                    edit: false,
                    create: false
                },
                codTipoMarcaManual: {
                    title: 'Tipo Marca Manual',
                    width: '11%',
                    sorting: false,
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=tpmarcamanual'
                    },
                    sorting: false,
                    edit: false        
                },
                fechaHoraActualizacion: {
                    title: 'Hora actualizacion',
                    width: '11%',
                    list: true,
                    sorting: false,
                    edit: false,
                    create: false
                },
                comentario: {
                    title: 'Comentario',
                    width: '11%',
                    list: true,
                    sorting: false,
                    edit: true
                },
                
            },
            toolbar: {
                    sorting: false,
                    items: [{
                        icon: '<%=request.getContextPath()%>/images/icon_csv.gif',
                        text: 'CSV',
                            click: function () {
                                document.location.href='<%=request.getContextPath()%>/DataExportServlet?type=marcasCSV';
                            }
                        }
                    ]
            },
            rowInserted: function(event, data){
              //alert('tipoMarca: '+data.record.tipoMarca);
              var auxint=data.record.tipoMarca;
              if (auxint === 100 || auxint === 200 || auxint === 400){
                //alert('ocultar botones...');  
                data.row.find('.jtable-edit-command-button').hide();
                data.row.find('.jtable-delete-command-button').hide();
                data.row.css("background", "#c3595c");
                data.row.css("color", "#ffffff");
              }//else alert('nadaaaa');
            },
            recordsLoaded: function (event, data) {
                var cencoSelected = $("select#cencoId").val();
                                
                //alert('recordsLoaded. data.tipoMarca: ' + data.tipoMarca);
                //if (typeof $memberReviewExists != 'undefined' && $memberReviewExists == true){
                if (cencoSelected === '-1'){
                    //alert('ocultar boton new record');
                    $('#MarcasTableContainer').find('.jtable-toolbar-item.jtable-toolbar-item-add-record').hide();
                                                
                    //$(".jtable-add-record").hide();   
                    //$memberReviewExists = null;
                }else {
                    //No review currently exists for this user so show the Add review link                                      $(".jtable-add-record").show();
                }
            },
            //Initialize validation logic when a form is created
            formCreated: function (event, data) {
                var cencoSelected = $("select#cencoId").val();
                var tokenCenco = cencoSelected.split("|");
                var auxempresa  = tokenCenco[0];
                var auxdepto    = tokenCenco[1];
                var auxcenco    = tokenCenco[2];
                if ($('#dispositivo').val() === '-1'){
                    alert('Seleccione dispositivo...');
                }
                var tipoMarcaSelected=$("select#Edit-tipoMarca").val();                
                //alert('Tipo Marca selected_2: '+ $("select#Edit-tipoMarca").val());
                $("select#Edit-tipoMarca option[value='100']").remove(); //removes the option with value = 100
                $("select#Edit-tipoMarca option[value='200']").remove(); //removes the option with value = 200
                $("select#Edit-tipoMarca option[value='300']").remove(); //removes the option with value = 300
                $("select#Edit-tipoMarca option[value='400']").remove(); //removes the option with value = 300
                $("select#Edit-tipoMarca option[value='500']").remove(); //removes the option with value = 300
                $("select#Edit-tipoMarca option[value='600']").remove(); //removes the option with value = 300
                
                $('select#Edit-tipoMarca').val('').trigger('chosen:updated'); //refreshes the drop down list
                $('select#Edit-tipoMarca').val(tipoMarcaSelected);
                //inicial: comentario solo lectura y disabled
                document.getElementById("Edit-comentario").setAttribute("readonly", "true");
                $('#Edit-comentario').addClass('jtable-input-readonly');
                data.form.find('input[name="comentario"]').removeClass('validate[required]');
                
                $('select#Edit-codTipoMarcaManual').on('change', function() {
                    var valueSelected = this.value;
                    var textSelected = $("select#Edit-codTipoMarcaManual option:selected").text();
                    textSelected = textSelected.toUpperCase();
                    
                    $('#Edit-comentario').addClass('jtable-input-readonly');
                    if (textSelected === 'OTROS'){
                        data.form.find('input[name="comentario"]').addClass('validate[required]');
                        $('#Edit-comentario').removeClass('jtable-input-readonly');
                        document.getElementById("Edit-comentario").removeAttribute("readonly");
                    }else{
                        document.getElementById("Edit-comentario").setAttribute("readonly", "true");
                        $('#Edit-comentario').addClass('jtable-input-readonly');
                        document.getElementById("Edit-comentario").value='';
                        data.form.find('input[name="comentario"]').removeClass('validate[required]');
                    }
                });
                
                $('#empresaCod').val(empresaLabel);
                $('#Edit-empresaKey').val(auxempresa);
                                
                <% //if (theUser.getIdPerfil() == 1){%>
                    data.form.find('input[name="fechaHora"]').addClass('validate[required],custom[date]');
                    data.form.find('input[name="hora"]').addClass('validate[required],custom[integer]');
                    data.form.find('input[name="minutos"]').addClass('validate[required],custom[integer]');
                    data.form.find('input[name="codDispositivo"]').addClass('validate[required]');
                    $('select#Edit-codTipoMarcaManual').addClass('validate[required]');
                   // $("#fechaHora").datepicker({ minDate: -180})
                <%//}%>

                data.form.find('input[name="fechaHora"]').datepicker({dateFormat: 'yy-mm-dd',minDate: -180});
                <%if (theUser.getIdPerfil() == 1){%>
                    data.form.find('input[name="fechaHora"]').datepicker({dateFormat: 'yy-mm-dd',maxDate: 0});
                <%}else{%>
                    if (data.formType=='create'){
                        data.form.find('input[name="fechaHora"]').datepicker({dateFormat: 'yy-mm-dd',maxDate: 0});
                    }
                <%}%>
                
                //return false;
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
