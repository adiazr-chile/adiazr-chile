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

    <title>Vacaciones - Informacion Historica</title>

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
                 var sourceSelected = 'vacaciones';
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
                $('#VacacionesLogTableContainer').find('.jtable-toolbar-item.jtable-toolbar-item-add-record').show();
            }else{
                $('#VacacionesLogTableContainer').find('.jtable-toolbar-item.jtable-toolbar-item-add-record').hide();
            }
        }
        showHideButtonNewRow();
	
    </script>
    
</head>
<body>
    <div class="site-container">
        <div class="main-header" style="position: relative">
            <h1>Informacion Vacaciones<span class="light"></span> Log</h1>
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
        <button type="submit" id="LoadRecordsButton">Consultar</button>
        <input type="hidden" name="action" id="action">
    </form>
</div>

<div id="VacacionesLogTableContainer"></div>
<script type="text/javascript">

    $(document).ready(function () {
        showHideButtonNewRow();
        $('#VacacionesLogTableContainer').jtable({       
            title: 'Informacion Historica Vacaciones',
            paging: true, //Enable paging
            pageSize: 10, //Set page size (default: 10)
            sorting: true, //Enable sorting
            defaultSorting: 'fecha_evento desc', //Set default sorting
            actions: {
                listAction: '<%=request.getContextPath()%>/servlet/VacacionesLogController?action=list'
            },
            fields: {
                rutEmpleado:{
                    title: 'Empleado',
                    width: '10%',
                    key: false,
                    edit:true,
                    create:true,
                    list: true
                    ,options: function(data){
                        var tokenCenco = $('#cencoId').val().split("|");
                        var auxempresa  = tokenCenco[0];
                        var auxdepto    = tokenCenco[1];
                        var auxcenco    = tokenCenco[2];
                        
                        return '<%=request.getContextPath()%>/LoadItems?type=empleados'
                            + '&empresa='+auxempresa
                            + '&depto='+auxdepto
                            + '&cenco='+auxcenco;
                    }
                },
                fechaCalculo:{
                    title: 'Fecha de calculo',
                    width: '6%',
                    list: true,
                    edit:false,
                    create:false,
                    sorting: false
                },
                fechaEvento:{
                    title: 'Fecha evento',
                    width: '6%',
                    list: true,
                    edit:false,
                    create:false,
                    sorting: false
                }, 
                username:{
                    title: 'Usuario',
                    width: '6%',
                    list: true,
                    edit:false,
                    create:false,
                    sorting: false
                },    
                fechaInicioContrato:{
                    title: 'F.Ini.Contrato',
                    width: '6%',
                    list: true,
                    edit:false,
                    create:false,
                    sorting: false,
                    defaultValue: '0'
                },
                afpCode:{
                    title: 'AFP Certif',
                    width: '6%',
                    list: true,
                    edit:true,
                    create:true,
                    sorting: false,
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=afps';
                    }
                },
                fechaCertifVacacionesProgresivas:{
                    title: 'Fecha certificado AFP',
                    width: '6%',
                    list: true,
                    type: 'date',
                    displayFormat: 'yy-mm-dd',
                    edit:true,
                    create:true,
                    sorting: false
                },
                numCotizaciones:{
                    title: 'Num cotiz. certif.',
                    width: '6%',
                    list: true,
                    sorting: false,
                    defaultValue: '0'
                },
                fechaBaseVp:{
                    title: 'FVP',
                    width: '5%',
                    list: true,
                    edit: false,
                    sorting: false,
                    defaultValue: '0'
                },    
                diasProgresivos:{
                    title: 'Dias Progresivos',
                    width: '6%',
                    list: true,
                    edit:true,
                    create:true,
                    sorting: false,
                    defaultValue: '0'
                },
                diasEspeciales:{
                    title: 'Dias especiales',
                    width: '6%',
                    list: true,
                    edit:true,
                    create:true,
                    sorting: false,
                    defaultValue: '0'
                },
                diasAdicionales:{
                    title: 'Dias adicionales',
                    width: '6%',
                    list: true,
                    edit:true,
                    create:true,
                    sorting: false,
                    defaultValue: '0'
                },    
                diasAcumulados:{
                    title: 'Acumulados(+)',
                    width: '6%',
                    list: true,
                    edit:false,
                    create:false,
                    sorting: false
                },
                diasEfectivos:{
                    title: 'Asignados(-)',
                    width: '6%',
                    list: true,
                    edit:false,
                    create:false,
                    sorting: false
                },    
                saldoDias:{
                    title: 'Saldo',
                    width: '6%',
                    list: true,
                    edit:false,
                    create:false,
                    sorting: false
                },
                comentario:{
                    title: 'Comentario',
                    width: '6%',
                    list: false,
                    edit:false,
                    create:false,
                    sorting: false
                },
                fechaInicioUltimasVacaciones:{
                    title: 'Ini. ult. vac.',
                    width: '6%',
                    list: true,
                    edit:false,
                    create:false,
                    sorting: false
                }, 
                fechaFinUltimasVacaciones:{
                    title: 'Fin ult. vac.',
                    width: '6%',
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
                                document.location.href = '<%=request.getContextPath()%>/DataExportServlet?type=vacacioneslogCSV';
                            }
                        }
                    ]
            },
            recordsLoaded: function (event, data) {
                //alert('records: '+data.TotalRecordCount);
            },
            //Initialize validation logic when a form is created
            formCreated: function (event, data) {
                var cencoSelected = $("select#cencoId").val();
                var tokenCenco = cencoSelected.split("|");
                var auxempresa  = tokenCenco[0];
                showHideButtonNewRow();
                $('#Edit-empresaId').val(auxempresa);
            }
        });

        //Re-load records when user click 'load records' button.
        $('#LoadRecordsButton').click(function (e) {
            e.preventDefault();
            $('#VacacionesLogTableContainer').jtable('load', {
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
