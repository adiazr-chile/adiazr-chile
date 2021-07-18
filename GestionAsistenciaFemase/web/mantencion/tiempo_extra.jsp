<%@ include file="/include/check_session.jsp" %>
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
%>
<!DOCTYPE html>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

    <meta name="keywords" content="femase,gestion,web">
    <meta name="description" content="Sistema de Gestion FEMASE-RRHH-Control Asistencia">
    <meta name="author" content="Adiazr">

    <title>Admin Tiempo Extra</title>
 
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
             
             $('#cencoId').change(function(event) {
                 var empresaSelected = $("select#empresaId").val();
                 var deptoSelected = $("select#deptoId").val();
                 var cencoSelected = $("select#cencoId").val();
                 
                 $.get('<%=request.getContextPath()%>/JsonListServlet', {
                         empresaID : empresaSelected,deptoID : deptoSelected,cencoID : cencoSelected
                 }, function(response) {
                        var select = $('#paramRutEmpleado');
                        select.find('option').remove();
                        var newoption = "";
                        newoption += "<option value='-1'>Seleccione Empleado</option>";
                        for (i=0; i<response.length; i++) {
                            var auxNombre = response[i].nombres 
                                    + ' ' + response[i].apePaterno + ' ' + response[i].apeMaterno;
                            newoption += "<option value='" + response[i].rut + "'>" + auxNombre + "</option>";
                        }
                        $('#paramRutEmpleado').html(newoption);
                 });
             });
             
         });

   
    </script>
    
</head>
<body>
    <div class="site-container">
        <div class="main-header" style="position: relative">
            <h1>Lista de Empleados<span class="light"></span></h1>
            <h2>Tiempo Extra</h2>
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
            <select id="deptoId" name="deptoId" style="width:350px;" required>
                <option value="-1">--------</option>
            </select>
                
            <label>Centro Costo
                <select name="cencoId" id="cencoId">
                    <option value="-1" selected>----------</option>
                </select>
            </label>
             
            <label>Empleado
                <select name="paramRutEmpleado" id="paramRutEmpleado">
                    <option value="-1" selected>----------</option>
                </select>
            </label>
            
            <label>Cargo
                <select name="filtroCargo" id="filtroCargo">
                <option value="-1" selected>----------</option>
                <%
                    Iterator<CargoVO> iteradorCargos = cargos.iterator();
                    while(iteradorCargos.hasNext() ) {
                        CargoVO auxcargo = iteradorCargos.next();
                        %>
                        <option value="<%=auxcargo.getId()%>"><%=auxcargo.getNombre()%></option>
                        <%
                    }
                %>
                </select>
            </label>    
        
        <button type="submit" id="LoadRecordsButton">Buscar</button>
    </form>
</div>

<div id="HETableContainer"></div>
<script type="text/javascript">

    $(document).ready(function () {

        $('#HETableContainer').jtable({       
            title: 'Empleados',
            paging: true, //Enable paging
            pageSize: 10, //Set page size (default: 10)
            sorting: true, //Enable sorting
            defaultSorting: 'empleado.empl_rut ASC', //Set default sorting
            actions: {
                listAction: '<%=request.getContextPath()%>/TiempoExtraController?action=listEmpleados'
                //,createAction:'<%=request.getContextPath()%>/CargosController?action=create',
                //updateAction: '<%=request.getContextPath()%>/CargosController?action=update'
                //,deleteAction: '<%=request.getContextPath()%>/ModulosSistemaController?action=delete'
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
                Phones: {
                    title: '',
                    width: '5%',
                    sorting: false,
                    edit: false,
                    create: false,
                    display: function (empleadoData) {
                        //Create an image that will be used to open child table
                        var $img = $('<img src="<%=request.getContextPath()%>/images/reloj.png" title="Editar tiempo extra" />');
                        //Open child table when user clicks the image
                        $img.click(function () {
                            $('#HETableContainer').jtable('openChildTable',
                                $img.closest('tr'),
                                {
                                    title: empleadoData.record.nombres + ' - Tiempo extra',
                                    actions: {
                                            listAction: '<%=request.getContextPath()%>/TiempoExtraController?action=listTE&rut=' + empleadoData.record.rut,
                                            deleteAction: '<%=request.getContextPath()%>/TiempoExtraController?action=deleteTE&rut=' + empleadoData.record.rut,
                                            updateAction: '<%=request.getContextPath()%>/TiempoExtraController?action=updateTE',
                                            createAction: '<%=request.getContextPath()%>/TiempoExtraController?action=createTE'
                                    },
                                    fields: {
                                        rut: {
                                            type: 'hidden',
                                            defaultValue: empleadoData.record.rut
                                        },
                                        fechaAsStr: {
                                            title: 'Fecha',
                                            key: true,
                                            create: true,
                                            edit: true,
                                            list: true,
                                            type: 'date',
                                            displayFormat: 'yy-mm-dd'
                                        },
                                        tipo: {
                                            title:'Tipo',
                                            list: true,
                                            create:true,
                                            edit:true,
                                            options: { 'Entrada': 'Entrada', 'Salida': 'Salida' }
                                        },
                                        horas: {
                                            title: 'Horas',
                                            width: '7%',
                                            options: { '0': '00', '1': '01','2': '02', '3': '03','4': '04', '5': '05','6': '06', '7': '07','8': '08', '9': '09','10': '10', '11': '11','12': '12','13': '13','14': '14','15': '15','16': '16','17': '17','18': '18','19': '19','20': '20','21': '21','22': '22','23': '23'},
                                            edit:true,
                                            create:true,
                                            sorting:true
                                        },
                                        minutos: {
                                            title: 'Minutos',
                                            width: '7%',
                                            options: { '0': '00', '5': '05','10': '10','15': '15','20': '20','25': '25','30': '30','35': '35','40': '40','45': '45','50': '50','55': '55'},
                                            edit:true,
                                            create:true,
                                            sorting:false 
                                        },
                                        usuarioResponsable: {
                                            title: 'Usuario autoriza',
                                            width: '30%',
                                            options: function(data){
                                                return '<%=request.getContextPath()%>/LoadItems?type=usuarios'
                                            },
                                            list: true,
                                            edit: true,
                                            create:true
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
                nombres:{
                    title: 'Nombres',
                    width: '14%',
                    edit:true
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
                },
                nombreCargo:{
                    title: 'Cargo',
                    width: '14%',
                    edit:true
                },
                nombreTurno:{
                    title: 'Turno',
                    width: '14%',
                    edit:true
                }    
            }
        });

        //Re-load records when user click 'load records' button.
        $('#LoadRecordsButton').click(function (e) {
            e.preventDefault();
            $('#HETableContainer').jtable('load', {
                filtroEmpresa: $('#empresaId').val(),
                filtroDepto: $('#deptoId').val(),
                filtroCenco: $('#cencoId').val(),
                filtroCargo: $('#filtroCargo').val(),
                paramRutEmpleado: $('#paramRutEmpleado').val(),
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
    
</body>
</html>
