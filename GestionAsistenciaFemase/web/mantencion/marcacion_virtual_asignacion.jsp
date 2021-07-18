<%@page import="cl.femase.gestionweb.vo.UsuarioCentroCostoVO"%>
<%@ include file="/include/check_session.jsp" %>
<%@page import="cl.femase.gestionweb.vo.EmpresaVO"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.LinkedHashMap"%>

<%
    List<UsuarioCentroCostoVO> cencos   = (List<UsuarioCentroCostoVO>)session.getAttribute("cencos_empleado");
        
%>
<!DOCTYPE html>

<html>
<head>
    <meta charset="utf-8" />

    <title>Asignacion - Marcacion Virtual</title>

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
        showHideButtonNewRow();
        $(document).ready(function() {
            //evento para combo centro costo
            $('#cencoId').change(function(event) {
                 var empresaSelected = null;//$("select#empresaId").val();
                 var deptoSelected = null;//$("select#deptoId").val();
                 var cencoSelected = $("select#cencoId").val();
                 var sourceSelected = 'marcacion_virtual';
                 $.get('<%=request.getContextPath()%>/JsonListServlet', {
                 	empresaID : empresaSelected,deptoID : deptoSelected,cencoID : cencoSelected,source: sourceSelected
                 }, function(response) {
                        var select = $('#rut');
                        select.find('option').remove();
                        var newoption = "";
                        newoption += "<option value='-1'>Todos</option>";
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
             showHideButtonNewRow();
        });
        
        function showHideButtonNewRow(){
            var rutSelected = $("select#rut").val();
            if (rutSelected !== '-1'){
                $('#MVirtualTableContainer').find('.jtable-toolbar-item.jtable-toolbar-item-add-record').show();
            }else{
                $('#MVirtualTableContainer').find('.jtable-toolbar-item.jtable-toolbar-item-add-record').hide();
            }
        }
        showHideButtonNewRow();
		
        function calcularSaldo(){
            var cencoSelected = $("select#cencoId").val();
            var rutSelected = $("select#rut").val();
            var tokenLabelCenco = cencoSelected.split("|");
            var empresaId  = tokenLabelCenco[0];
            var deptoId    = tokenLabelCenco[1];
            var cencoId    = tokenLabelCenco[2];
            if (cencoSelected !== '-1'){
                //alert('calcular. cencoId:' + cencoSelected + ', rutSelected: '+rutSelected);
                document.location.href=
                '<%=request.getContextPath()%>/servlet/VacacionesController?action=calcula_saldo&empresa_id='+empresaId
                +'&depto_id=' + deptoId + '&cenco_id=' + cencoId + '&rutEmpleado=' + rutSelected;
            }else{
                alert('La seleccion de centro de costo es obligatoria');
            }

        }
	
        function ingresarVacaciones(){
            //alert('calcular. cencoId:' + cencoSelected + ', rutSelected: '+rutSelected);
            document.location.href = '<%=request.getContextPath()%>/vacaciones/ingresar_vacaciones.jsp';
        }
        
    </script>
    
</head>
<body>
    <div class="site-container">
        <div class="main-header" style="position: relative">
            <h1>Asignaci&oacute;n Marcaci&oacute;n Virtual</h1>
            <h2>Filtros de b&uacute;squeda</h2>
        </div>
<div class="content-container">
            
            <div class="padded-content-container">
                
<div class="filtering">
    <form>
        <label>Centro Costo
            <select name="cencoId" id="cencoId" onchange="showHideButtonNewRow()">
                    <option value="-1" selected>----------</option>
                    <%
                    String valueCenco="";
                    String labelCenco="";    
                    String labelZonaExtrema = "";
                    Iterator<UsuarioCentroCostoVO> iteracencos = cencos.iterator();
                    while(iteracencos.hasNext() ) {
                        UsuarioCentroCostoVO auxCenco = iteracencos.next();
                        valueCenco = auxCenco.getEmpresaId() + "|" + auxCenco.getDeptoId() + "|" + auxCenco.getCcostoId();
                        if (auxCenco.getZonaExtrema().compareTo("N") == 0) labelZonaExtrema = "";
                        else labelZonaExtrema = "(Zona Extrema)";
                        labelCenco = "[" + auxCenco.getEmpresaNombre()+ "]," 
                            + "[" + auxCenco.getDeptoNombre()+ "],"
                            + "[" + auxCenco.getCcostoNombre()+ "]"
                            + " " + labelZonaExtrema;
                    %>
                        <option value="<%=valueCenco%>"><%=labelCenco%></option>
                        <%
                    }
                %>
                </select>
            </label>
            <label>Empleado
            <select name="rut" id="rut" onchange="showHideButtonNewRow()">
                    <option value="-1" selected>----------</option>
                </select>
            </label>
        <button type="submit" id="LoadRecordsButton">Consultar datos</button>
        
        <input type="hidden" name="action" id="action">
    </form>
</div>

<div id="MVirtualTableContainer"></div>
<script type="text/javascript">

    $(document).ready(function () {
        showHideButtonNewRow();
        $('#MVirtualTableContainer').jtable({       
            title: 'Asignacion Marcacion Virtual',
            paging: true, //Enable paging
            pageSize: 10, //Set page size (default: 10)
            sorting: true, //Enable sorting
            defaultSorting: 've.nombre ASC', //Set default sorting
            actions: {
                listAction: '<%=request.getContextPath()%>/servlet/MarcacionVirtualController?action=list',
                updateAction: '<%=request.getContextPath()%>/servlet/MarcacionVirtualController?action=update'
            },
            fields: {
                rowKey: {
                    title:'Row key',
                    key: true,
                    list: false,
                    create:false,
                    edit:false
                },
                empresaId: {
                    title:'Empresa',
                    key: false,
                    list: false,
                    create:true,
                    edit:true,
                    sorting: false,
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=empresas';
                    }
                },
                rutEmpleado:{
                    title: 'RUN',
                    width: '7%',
                    key: false,
                    edit:true,
                    create:true,
                    list: true,
                    input: function (data) {
                        //var rutSelected = $("select#rut").val();
                        if (data.record) {
                            return '<input type="text" name="aRut" style="width:100px" value="' + data.record.rutEmpleado + '" readonly />';
                        } 
                        //else {
                        //    return '<input type="text" name="aRut" style="width:100px" value="'+rutSelected+'" readonly/>';
                        //}
                    }
                },
                nombreEmpleado:{
                    title: 'Nombre',
                    width: '16%',
                    key: false,
                    edit:false,
                    create:false,
                    list: true,
                    sorting:true
                },
                admiteMarcaVirtual:{
                    title: 'Marca virtual',
                    width: '7%',
                    list: true,
                    edit:true,
                    create:true,
                    sorting: false,
                    type: 'radiobutton',
                    options: { 'S': 'Si', 'N': 'No' },
                    defaultValue: 'N'
                },
                registrarUbicacion:{
                    title: 'Registrar Ubicacion',
                    width: '7%',
                    list: true,
                    edit:true,
                    create:true,
                    sorting: false,
                    type: 'radiobutton',
                    options: { 'S': 'Si', 'N': 'No' },
                    defaultValue: 'N'
                },
                marcaMovil:{
                    title: 'Marca Movil',
                    width: '7%',
                    list: true,
                    edit:true,
                    create:true,
                    sorting: false,
                    type: 'radiobutton',
                    options: { 'S': 'Si', 'N': 'No' },
                    defaultValue: 'N'
                },    
                movilId:{
                    title: 'Movil Id',
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=moviles&cencoId='+data.record.cencoId;
                    },
                    width: '30%',
                    edit:true,
                    sorting: true
                },
                fecha1:{
                    title: 'Fecha 1',
                    width: '7%',
                    type: 'date',
                    displayFormat: 'yy-mm-dd',
                    startDate: new Date(),
                    edit:true,
                    create:true,
                    sorting:false
                },
                fecha2:{
                    title: 'Fecha 2',
                    width: '7%',
                    type: 'date',
                    displayFormat: 'yy-mm-dd',
                    edit:true,
                    create:true,
                    sorting:false
                },
                fecha3:{
                    title: 'Fecha 3',
                    width: '7%',
                    type: 'date',
                    displayFormat: 'yy-mm-dd',
                    edit:true,
                    create:true,
                    sorting:false
                },
                fecha4:{
                    title: 'Fecha 4',
                    width: '7%',
                    type: 'date',
                    displayFormat: 'yy-mm-dd',
                    edit:true,
                    create:true,
                    sorting:false
                },
                fecha5:{
                    title: 'Fecha 5',
                    width: '7%',
                    type: 'date',
                    displayFormat: 'yy-mm-dd',
                    edit:true,
                    create:true,
                    sorting:false
                },
                fecha6:{
                    title: 'Fecha 6',
                    width: '7%',
                    type: 'date',
                    displayFormat: 'yy-mm-dd',
                    edit:true,
                    create:true,
                    sorting:false
                },
                fecha7:{
                    title: 'Fecha 7',
                    width: '7%',
                    type: 'date',
                    displayFormat: 'yy-mm-dd',
                    edit:true,
                    create:true,
                    sorting:false
                },    
                fechaAsignacion:{
                    title: 'Fecha de asignacion',
                    width: '7%',
                    list: true,
                    edit:false,
                    create:false,
                    sorting: false
                },
                username:{
                    title: 'Nombre de usuario',
                    width: '7%',
                    list: true,
                    edit:false,
                    create:false,
                    sorting: false
                }    
            },
            toolbar: {
                    sorting: false,
                    items: [{
                        icon: '<%=request.getContextPath()%>/images/icon_csv.gif',
                        text: 'CSV',
                            click: function () {
                                document.location.href='<%=request.getContextPath()%>/DataExportServlet?type=asignacionMarcaVirtualCSV';
                            }
                        }
                    ]
            },
            recordsLoaded: function (event, data) {
                //alert('records: '+data.TotalRecordCount);
            },
            //Initialize validation logic when a form is created
            formCreated: function (event, data) {
                var rutSelected = $("select#rut").val();
                var cencoSelected = $("select#cencoId").val();
                var tokenCenco = cencoSelected.split("|");
                var auxempresa  = tokenCenco[0];
                var auxdepto    = tokenCenco[1];
                var auxcenco    = tokenCenco[2];
                showHideButtonNewRow();
                $('#Edit-empresaId').val(auxempresa);
            }
        });

        //Re-load records when user click 'load records' button.
        $('#LoadRecordsButton').click(function (e) {
            e.preventDefault();
            $('#MVirtualTableContainer').jtable('load', {
                cencoId: $('#cencoId').val(),
                rutEmpleado: $('#rut').val()
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
<script type="text/javascript">

    $(document).ready(function () {
        showHideButtonNewRow();
    });

</script>
