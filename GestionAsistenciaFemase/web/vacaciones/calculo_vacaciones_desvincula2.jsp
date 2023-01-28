<%@page import="cl.femase.gestionweb.common.Constantes"%>
<%@page import="cl.femase.gestionweb.vo.UsuarioVO"%>
<%@page import="cl.femase.gestionweb.vo.UsuarioCentroCostoVO"%>
<%@ include file="/include/check_session.jsp" %>
<%@page import="cl.femase.gestionweb.vo.EmpresaVO"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.LinkedHashMap"%>

<%
    List<UsuarioCentroCostoVO> cencos   = (List<UsuarioCentroCostoVO>)session.getAttribute("cencos_empleado");
    UsuarioVO theUser	= (UsuarioVO)session.getAttribute("usuarioObj");
    boolean editarCampos = false;
    if (theUser.getIdPerfil() == Constantes.ID_PERFIL_ADMIN 
            || theUser.getIdPerfil() == Constantes.ID_PERFIL_JEFE_TECNICO_NACIONAL
            || theUser.getIdPerfil() == Constantes.ID_PERFIL_SUPER_ADMIN){//admin o super admin
        editarCampos = true;
    }
%>
<!DOCTYPE html>

<html>
<head>
    <meta charset="utf-8" />

    <title>Calculo de Vacaciones - Desvinculados</title>

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
        //showHideButtonNewRow();
        $(document).ready(function() {
            //evento para combo centro costo
            $('#cencoId').change(function(event) {
                 var empresaSelected = null;//$("select#empresaId").val();
                 var deptoSelected = null;//$("select#deptoId").val();
                 var cencoSelected = $("select#cencoId").val();
                 var sourceSelected = 'vacaciones_desvincula2';
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
             //showHideButtonNewRow();
        });
        $('#VacacionesTableContainer').find('.jtable-toolbar-item.jtable-toolbar-item-add-record').hide();
        /*
        function showHideButtonNewRow(){
            var rutSelected = $("select#rut").val();
            if (rutSelected !== '-1'){
                $('#VacacionesTableContainer').find('.jtable-toolbar-item.jtable-toolbar-item-add-record').show();
            }else{
                $('#VacacionesTableContainer').find('.jtable-toolbar-item.jtable-toolbar-item-add-record').hide();
            }
        }
        showHideButtonNewRow();
	*/
        
        //************
        var empresaLabel='';
        var deptoLabel='';
        var cencoLabel='';
        function setCenco(cencoId){
            //var rutEmpleado=$("select#rut").val();
            $('#VacacionesTableContainer').find('.jtable-toolbar-item.jtable-toolbar-item-add-record').hide();
            if (cencoId !== '-1'){
                var cencoSelected= $('#cencoId').find(":selected").text();
                var tokenLabelCenco = cencoSelected.split(",");
                empresaLabel  = tokenLabelCenco[0];
                deptoLabel    = tokenLabelCenco[1];
                cencoLabel    = tokenLabelCenco[2];
                //$('#VacacionesTableContainer').find('.jtable-toolbar-item.jtable-toolbar-item-add-record').show();
            }
            /*else if (rutEmpleado === '-1'){
                $('#VacacionesTableContainer').find('.jtable-toolbar-item.jtable-toolbar-item-add-record').hide();
            }*/
        }
        
        function setEmpleado(rut){
            //$('#VacacionesTableContainer').find('.jtable-toolbar-item.jtable-toolbar-item-add-record').hide();
            if (rut !== '-1'){
                $('#VacacionesTableContainer').find('.jtable-toolbar-item.jtable-toolbar-item-add-record').hide();
            }
        }
        //************

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
                '<%=request.getContextPath()%>/servlet/CalculoVacacionesServlet?action=calcula_vacaciones_desvincula2&empresa_id='+empresaId
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
            <h1>Informaci&oacute;n y cálculo de Vacaciones Desvinculados</h1>
            <h2>Filtros de b&uacute;squeda</h2>
      </div>
<div class="content-container">
            
            <div class="padded-content-container">
                
<div class="filtering">
    <form>
        <label>Centro Costo
            <select name="cencoId" id="cencoId" onChange="setCenco(this.value)">
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
            <select name="rut" id="rut" onChange="setEmpleado(this.value)">
                    <option value="-1" selected>----------</option>
              </select>
            </label>
        <button type="submit" id="LoadRecordsButton">Consultar datos</button>
        <input name="botoncrear" 
            type="button" 
            value="Calcular saldo vacaciones" 
            class="button button-blue" 
            onclick="calcularSaldo();">
        <!--<input name="boton_ingresar" 
            type="button" 
            value="Ingresar vacaciones" 
            class="button button-blue" 
            onclick="ingresarVacaciones();">-->
        <input type="hidden" name="action" id="action">
    </form>
</div>

<div id="VacacionesTableContainer"></div>
<script type="text/javascript">

    $(document).ready(function () {
        //showHideButtonNewRow();
        $('#VacacionesTableContainer').jtable({       
            title: 'Informacion y cálculo de Vacaciones (desvinculados)',
            paging: true, //Enable paging
            pageSize: 10, //Set page size (default: 10)
            sorting: true, //Enable sorting
            defaultSorting: 'rut_empleado ASC', //Set default sorting
            actions: {
                listAction: '<%=request.getContextPath()%>/servlet/VacacionesController?action=list&tipo=desvinculados',
                //createAction:'<%=request.getContextPath()%>/servlet/VacacionesController?action=create',
                updateAction: '<%=request.getContextPath()%>/servlet/VacacionesController?action=update',
                deleteAction: '<%=request.getContextPath()%>/servlet/VacacionesController?action=delete'
            },
            fields: {
                rowKey: {
                    title:'Row key',
                    width: '0%',
                    key: true,
                    list: false,
                    edit:false
                },
                empresaKey:{
                    title: 'EmpresaKey',
                    list: false,
                    type: 'hidden'
                },
                empresaId: {
                    title:'Empresa ID',
                    width: '5%',
                    key: false,
                    list: false,
                    edit:false,
                    sorting: false,
                    input: function (data) {
                        if (data.record) {
                            return '<input type="hidden" id="empresaId" name="empresaId" value="' + data.record.empresaId + '">';
                        }else{
                            return '<input type="text" id="empresaId" name="empresaId" readonly>';
                        }
                    }
                    /*options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=empresas';
                    }*/
                },
                cencoId: {
                    title:'Cenco ID',
                    width: '5%',
                    list: false,
                    edit: false,
                    sorting: false
                },
                rutEmpleado:{
                    title: 'Empleado',
                    width: '6%',
                    key: false,
                    edit:true,
                    list: true,
                    input: function (data) {
                        var rutSelected = $("select#rut").val();//rut seleccionado en filtro de busqueda
                        if (data.record) {
                            return '<input type="text" name="aRut" style="width:100px" value="' + data.record.rutEmpleado + '" readonly />';
                        } else {
                            return '<input type="text" name="aRut" style="width:100px" value="'+rutSelected+'" readonly/>';
                        }
                    }
                    /*,options: function(data){
                        var tokenCenco = $('#cencoId').val().split("|");
                        var auxempresa  = tokenCenco[0];
                        var auxdepto    = tokenCenco[1];
                        var auxcenco    = tokenCenco[2];
                        
                        return '<%=request.getContextPath()%>/LoadItems?type=empleados'
                            + '&empresa='+auxempresa
                            + '&depto='+auxdepto
                            + '&cenco='+auxcenco;
                    }*/
                },
                fechaCalculo:{
                    title: 'Fecha de calculo',
                    width: '8%',
                    list: true,
                    edit:false,
                    orting: false
                },
                fechaInicioContrato:{
                    title: 'F.Ini.Contrato',
                    width: '7%',
                    list: true,
                    edit:false,
                    sorting: false,
                    defaultValue: '0'
                },
                fechaDesvinculacion:{
                    title: 'F.Desvinculacion',
                    width: '7%',
                    list: true,
                    edit:false,
                    sorting: false,
                    defaultValue: '0'
                },        
                afpCode:{
                    title: 'AFP Certif',
                    width: '6%',
                    list: true,
                    <%if (editarCampos){%>
                        edit: true,
                    <%}else{%>
                        edit: false,    
                    <%}%>
                    sorting: false,
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=afps';
                    }
                },
                fechaCertifVacacionesProgresivas:{
                    title: 'Fec emision certif. AFP',
                    width: '7%',
                    list: true,
                    type: 'date',
                    displayFormat: 'yy-mm-dd',
                    <%if (editarCampos){%>
                        edit: true,
                    <%}else{%>
                        edit: false,    
                    <%}%>
                    sorting: false
                },
                numCotizaciones:{
                    title: 'Num cotiz. certif.',
                    width: '6%',
                    list: true,
                    <%if (editarCampos){%>
                        edit: true,
                    <%}else{%>
                        edit: false,    
                    <%}%>        
                    sorting: false,
                    defaultValue: '0'
                },
                fechaBaseVp:{
                    title: 'Fecha base VP',
                    width: '5%',
                    list: true,
                    edit: false,
                    sorting: false,
                    defaultValue: '0'
                },
                mensajeVp:{
                    title: 'Manseje VP',
                    width: '5%',
                    list: true,
                    edit: false,
                    sorting: false,
                    defaultValue: '0'
                },        
                diasProgresivos:{
                    title: 'D. Progres.',
                    width: '5%',
                    list: true,
                    edit: false,
                    sorting: false,
                    defaultValue: '0'
                },
                diasEspeciales:{
                    title: 'D. especiales',
                    width: '5%',
                    list: true,
                    edit:true,
                    sorting: false,
                    type: 'radiobutton',
                    options: { 'S': 'Si', 'N': 'No' },
                    defaultValue: 'N'
                },
                diasAdicionales:{
                    title: 'D. adicionales',
                    width: '5%',
                    list: true,
                    edit:true,
                    sorting: false
                },    
                diasAcumulados:{
                    title: 'Acum.(+)',
                    width: '5%',
                    list: true,
                    edit:false,
                    sorting: false
                },
                diasEfectivos:{
                    title: 'Asig.(-)',
                    width: '5%',
                    list: true,
                    edit:false,
                    sorting: false
                },    
                saldoDias:{
                    title: 'Saldo',
                    width: '5%',
                    list: true,
                    edit:false,
                    sorting: false
                },
                comentario:{
                    title: 'Coment.',
                    width: '8%',
                    list: true,
                    edit:false,
                    sorting: false
                },
                fechaInicioUltimasVacaciones:{
                    title: 'Ini. ult. vac.',
                    width: '6%',
                    list: true,
                    edit:false,
                    orting: false
                }, 
                fechaFinUltimasVacaciones:{
                    title: 'Fin ult. vac.',
                    width: '6%',
                    list: true,
                    edit:false,
                    sorting: false
                },
                saldoDiasVBA:{
                    title: 'Saldo VBA',
                    width: '5%',
                    list: true,
                    edit:false,
                    sorting: false
                },
                saldoDiasVP:{
                    title: 'Saldo VP',
                    width: '5%',
                    list: true,
                    edit:false,
                    sorting: false
                }        
            },
            toolbar: {
                    sorting: false,
                    items: [{
                        icon: '<%=request.getContextPath()%>/images/icon_csv.gif',
                        text: 'CSV',
                            click: function () {
                                document.location.href='<%=request.getContextPath()%>/DataExportServlet?type=infovacacionesCSV';
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
                //showHideButtonNewRow();
                $('#Edit-empresaId').val(auxempresa);
                
                $('#empresaId').val(empresaLabel);
                $('#Edit-empresaKey').val(auxempresa);
                
            }
        });

        //Re-load records when user click 'load records' button.
        $('#LoadRecordsButton').click(function (e) {
            e.preventDefault();
            $('#VacacionesTableContainer').jtable('load', {
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
        //showHideButtonNewRow();
        
        
    });

</script>
