<%@ include file="/include/check_session.jsp" %>

<%@page import="cl.femase.gestionweb.vo.UsuarioCentroCostoVO"%>
<%@page import="cl.femase.gestionweb.vo.CentroCostoVO"%>
<%@page import="cl.femase.gestionweb.vo.DepartamentoVO"%>
<%@page import="cl.femase.gestionweb.vo.EmpresaVO"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>

<%
    String cencoParam = (String)request.getParameter("cco");
    List<EmpresaVO> empresas = (List<EmpresaVO>)session.getAttribute("empresas");
    List<UsuarioCentroCostoVO> cencos   = (List<UsuarioCentroCostoVO>)session.getAttribute("cencos_empleado"); 
    String empresaSelected = (String)session.getAttribute("empresaSelected");
    String deptoSelected = (String)session.getAttribute("deptoSelected");
    String cencoSelected = (String)session.getAttribute("cencoSelected");
    
    System.out.println("[contratos_caducados.jsp]"
        + "empresaSelected= " + empresaSelected
        +", deptoSelected= " + deptoSelected
        +", cencoSelected= " + cencoSelected
        +", cencoParam= " + cencoParam);
%>

<!DOCTYPE html>

<html>
<head>
    <meta charset="Content-Type: text/html; charset=iso-8859-1" />

    <meta name="keywords" content="femase,gestion,web">
    <meta name="description" content="Sistema de Gestion FEMASE-RRHH-Control Asistencia">
    <meta name="author" content="Adiazr">

    <title>Contratos caducados o proximos a caducar</title>

    <link href="../Jquery-JTable/Content/normalize.css" rel="stylesheet" type="text/css" />
    <link href='<%=request.getContextPath()%>/css-varios/googleapis.css' rel='stylesheet' type='text/css'>
    <link href="../Jquery-JTable/Content/Site.metro.css" rel="stylesheet" type="text/css" />

    <link href="../Jquery-JTable/Content/highlight.css" rel="stylesheet" type="text/css" />

        <link href="../Jquery-JTable/Content/themes/metroblue/jquery-ui.css" rel="stylesheet" type="text/css" />
        <link href="../Jquery-JTable/Scripts/jtable/themes/metro/blue/jtable.css" rel="stylesheet" type="text/css" />

    <link href="../Jquery-JTable/Scripts/syntaxhighligher/styles/shCore.css" rel="stylesheet" type="text/css" />
    <link href="../Jquery-JTable/Scripts/syntaxhighligher/styles/shThemeDefault.css" rel="stylesheet" type="text/css" />

	<!-- estilo para botones -->
	<!--<link rel="stylesheet" href="../jquery-plugins/chosen_v1.6.2/docsupport/style.css">-->
    
    <style>
        div.filtering
        {
            border: 1px solid #999;
            margin-bottom: 5px;
            padding: 10px;
            background-color: #EEE;
        }
        
        body {
            background-color: #fafafa;
            font-size: 10pt;
            font-style: normal;
            color: #06C
        }
    </style>

    <style>
    select {
        color:black !important;
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
             
                <%
                    if (cencoParam != null){%> 
                      $("#cencoId").val('<%=cencoParam%>').change();  
                    <%}
                %>
                            
             
                //evento para combo centro costo
                $('#cencoId').change(function(event) {
                    var empresaSelected = null;//$("select#empresaId").val();
                    var deptoSelected = null;//$("select#deptoId").val();
                    var cencoSelected = $("select#cencoId").val();
                    var sourceSelected = 'cargar_turnos_by_cenco';
                    $.get('<%=request.getContextPath()%>/JsonListServlet', {
                       empresaID : empresaSelected,deptoID : deptoSelected,cencoID : cencoSelected,source: sourceSelected
                    }, function(response2) {
                            var select = $('#turno');
                            select.find('option').remove();
                            var newoption = "";
                            newoption += "<option value='-1'>Seleccione Turno</option>";
                            for (i2 = 0; i2 <response2.length; i2++) {
                                var idTurno = response2[i2].id;
                                var labelTurno = response2[i2].nombre;
                                newoption += "<option value='" + idTurno + "'>" + labelTurno + "</option>";
                            }
                            $('#turno').html(newoption);
                    });
                });
         });
         
         $("thead").height("50px");
         
        function openEditForm(strrut){
            document.location.href = '<%=request.getContextPath()%>/EmpleadosController?action=edit&rutEmpleado='+strrut+'&source=caducados';
        }
    </script>
</head>
<body>
    <div class="site-container">
        <div class="main-header" style="position: relative">
            <h1>Empleados con contratos caducados o pr&oacute;ximos a caducar</h1>
            <h2>Filtros de b&uacute;squeda</h2>
      </div>
       
	<div class="content-container">
            
            <div class="padded-content-container">
                
    <!-- Filtros de busqueda INICIO-->                
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
                <label>RUN: <input type="text" name="rutEmpleado" id="rutEmpleado" /></label>
            <label>Nombre: 
                <input type="text" name="nombres" id="nombres" /></label>
                <label>Turno
                    <select name="turno" id="turno">
                        <option value="-1" selected>Seleccione turno</option>
                    </select>
                </label>
            <input name="apePaterno" type="hidden" id="apePaterno">
            <input name="estado" type="hidden" id="estado" value="-1">
            
            <!-- Boton Buscar-->  
            <button type="submit" id="LoadRecordsButton" class="button button-blue">Buscar</button>
            
        </form>
    </div>
    <!-- Filtros de busqueda FIN-->

<div id="CaducadosTableContainer"></div>
<script type="text/javascript">
    $(document).ready(function () {
        $('#CaducadosTableContainer').jtable({       
            title: 'Empleados',
            paging: true, //Enable paging
            pageSize: 10, //Set page size (default: 10)
            sorting: true, //Enable sorting
            defaultSorting: 'empl_rut ASC', //Set default sorting
            actions: {
                listAction: '<%=request.getContextPath()%>/EmpleadosCaducadosController?action=list',
                updateAction: '<%=request.getContextPath()%>/EmpleadosCaducadosController?action=update'
            },
            fields: {
                empresaNombre:{
                    title: 'Empresa',
                    width: '4%',
                    edit:true,
                    input: function (data) {
                        if (data.record) {
                            return '<input type="text" name="aux1" style="width:100px" value="' + data.record.empresaNombre + '" readonly />';
                        }
                    }
                },
                deptoNombre:{
                    title: 'Depto',
                    width: '4%',
                    edit:true,
                    input: function (data) {
                        if (data.record) {
                            return '<input type="text" name="aux1" style="width:100px" value="' + data.record.deptoNombre + '" readonly />';
                        }
                    }
                },
                cencoNombre:{
                    title: 'Centro',
                    width: '4%',
                    edit:true,
                    input: function (data) {
                        if (data.record) {
                            return '<input type="text" name="aux1" style="width:100px" value="' + data.record.cencoNombre + '" readonly />';
                        }
                    }
                },
                codInterno: {
                    title:'RUN',
                    width: '4%',
                    key: true,
                    list: true,
                    create:false,
                    edit:true,
                    input: function (data) {
                        if (data.record) {
                            return '<input type="text" name="codInterno" style="width:100px" value="' + data.record.codInterno + '" readonly />';
                        }
                    }
                },
                rut:{
                    title: 'N° ficha',
                    width: '4%',
                    edit:true,
                    input: function (data) {
                        if (data.record) {
                            return '<input type="text" name="rut" style="width:100px" value="' + data.record.rut + '" readonly />';
                        }
                    }
                },
                nombres:{
                    title: 'Nombres',
                    width: '4%',
                    edit:true,
                    input: function (data) {
                        if (data.record) {
                            return '<input type="text" name="nombres" style="width:100px" value="' + data.record.nombres + '" readonly />';
                        }
                    }
                },
                apePaterno:{
                    title: 'Paterno',
                    width: '4%',
                    edit:true,
                    sorting: false,
                    input: function (data) {
                        if (data.record) {
                            return '<input type="text" name="apePaterno" style="width:100px" value="' + data.record.apePaterno + '" readonly />';
                        }
                    }
                },
                apeMaterno:{
                    title: 'Materno',
                    width: '4%',
                    edit:true,
                    sorting: false,
                    input: function (data) {
                        if (data.record) {
                            return '<input type="text" name="apeMaterno" style="width:100px" value="' + data.record.apeMaterno + '" readonly />';
                        }
                    }
                },
                nombreTurno:{
                    title: 'Turno',
                    width: '4%',
                    edit:false,
                    sorting: false
                },
                contratoIndefinido: {
                    title: 'Contrato Indef.',
                    width: '4%',
                    type: 'checkbox',
                    values: { 'false': 'No', 'true': 'Si' },
                    defaultValue: 'false',
                    sorting: false
                },
                estado:{
                    title: 'Estado',
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=estados';
                    },
                    width: '4%',
                    edit:true,
                    sorting: false
                },    
                articulo22: {
                    title: 'Art. 22',
                    width: '4%',
                    type: 'checkbox',
                    values: { 'false': 'No', 'true': 'Si' },
                    defaultValue: 'false',
                    sorting: false
                },
                fechaInicioContratoAsStr:{
                    title: 'Ini Contrato',
                    width: '4%',
                    type: 'date',
                    displayFormat: 'yy-mm-dd',
                    inputClass: 'validate[required,custom[date]]',
                    edit:false,
                    sorting: false
                },
                fechaTerminoContratoAsStr:{
                    title: 'Fin Contrato',
                    width: '4%',
                    type: 'date',
                    displayFormat: 'yy-mm-dd',
                    inputClass: 'validate[required,custom[date]]',
                    edit:true,
                    sorting: false
                }
            },
            toolbar: {
                    sorting: false,
                    items: [{
                            icon: '<%=request.getContextPath()%>/images/icon_csv.gif',
                            text: 'CSV',
                            click: function () {
                                document.location.href='<%=request.getContextPath()%>/DataExportServlet?type=empleadosCaducadosToCSV';
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
            $('#CaducadosTableContainer').jtable('load', {
                cencoId: $('#cencoId').val(),
                rutEmpleado: $('#rutEmpleado').val(),
                nombres: $('#nombres').val(),
                turno: $('#turno').val()
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

