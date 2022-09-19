<%@page import="java.util.Locale"%>
<%@page import="java.util.Calendar"%>
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
    
    Calendar mycal = Calendar.getInstance(new Locale("es","CL"));
    int anioActual = mycal.get(Calendar.YEAR);
    LinkedHashMap<Integer,String> listaAnios = new LinkedHashMap<Integer,String>();
    listaAnios.put(anioActual-5, String.valueOf(anioActual-5));
    listaAnios.put(anioActual-4, String.valueOf(anioActual-4));
    listaAnios.put(anioActual-3, String.valueOf(anioActual-3));
    listaAnios.put(anioActual-2, String.valueOf(anioActual-2));
    listaAnios.put(anioActual-1, String.valueOf(anioActual-1));
    listaAnios.put(anioActual, String.valueOf(anioActual));
    listaAnios.put(anioActual+1, String.valueOf(anioActual+1));

%>
<!DOCTYPE html>

<html>
<head>
    <meta charset="utf-8" />

    <title>Resumen - Permisos Administrativos</title>

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
             //showHideButtonNewRow();
        });
        $('#PATableContainer').find('.jtable-toolbar-item.jtable-toolbar-item-add-record').hide();
        /*
        function showHideButtonNewRow(){
            var rutSelected = $("select#rut").val();
            if (rutSelected !== '-1'){
                $('#PATableContainer').find('.jtable-toolbar-item.jtable-toolbar-item-add-record').show();
            }else{
                $('#PATableContainer').find('.jtable-toolbar-item.jtable-toolbar-item-add-record').hide();
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
            $('#PATableContainer').find('.jtable-toolbar-item.jtable-toolbar-item-add-record').hide();
            if (cencoId !== '-1'){
                var cencoSelected= $('#cencoId').find(":selected").text();
                var tokenLabelCenco = cencoSelected.split(",");
                empresaLabel  = tokenLabelCenco[0];
                deptoLabel    = tokenLabelCenco[1];
                cencoLabel    = tokenLabelCenco[2];
                //$('#PATableContainer').find('.jtable-toolbar-item.jtable-toolbar-item-add-record').show();
            }
            /*else if (rutEmpleado === '-1'){
                $('#PATableContainer').find('.jtable-toolbar-item.jtable-toolbar-item-add-record').hide();
            }*/
        }
        
        function setEmpleado(rut){
            //$('#PATableContainer').find('.jtable-toolbar-item.jtable-toolbar-item-add-record').hide();
            if (rut !== '-1'){
                $('#PATableContainer').find('.jtable-toolbar-item.jtable-toolbar-item-add-record').hide();
            }
        }
        //************

    </script>
    
</head>
<body>
    <div class="site-container">
        <div class="main-header" style="position: relative">
            <h1>Resumen de Permisos Administrativos</h1>
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
            
            <label>A&ntilde;o:
                <select id="paramAnio" name="paramAnio" style="width:150px;" tabindex="2" required>
                    <option value="-1">Seleccione a&ntilde;o</option>
                    <%
                        for(Integer anioKey : listaAnios.keySet()) {
                            String anioLabel = listaAnios.get(anioKey);
                           %>
                           <option value="<%=anioKey%>"><%=anioLabel%></option>
                        <%}%>
                </select>
            </label>
            
        <button type="submit" id="LoadRecordsButton">Consultar</button>
        
        <input type="hidden" name="action" id="action">
    </form>
</div>

<div id="PATableContainer"></div>
<script type="text/javascript">

    $(document).ready(function () {
        //showHideButtonNewRow();
        $('#PATableContainer').jtable({       
            title: 'Permisos Administrativos',
            paging: true, //Enable paging
            pageSize: 10, //Set page size (default: 10)
            sorting: true, //Enable sorting
            defaultSorting: 'run_empleado ASC', //Set default sorting
            actions: {
                listAction: '<%=request.getContextPath()%>/servlet/PermisosAdministrativosController?action=list'
                //createAction:'<%=request.getContextPath()%>/servlet/VacacionesController?action=create',
                //updateAction: '<%=request.getContextPath()%>/servlet/VacacionesController?action=update',
                //deleteAction: '<%=request.getContextPath()%>/servlet/VacacionesController?action=delete'
            },
            fields: {
               rowKey: {
                    title:'Row key',
                    width: '0%',
                    key: true,
                    list: false,
                    edit:false
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
                },
                cencoId: {
                    title:'Cenco ID',
                    width: '5%',
                    list: false,
                    edit: false,
                    sorting: false
                },
                runEmpleado:{
                    title: 'Run',
                    width: '6%',
                    key: false,
                    edit:true,
                    list: true,
                    input: function (data) {
                        var rutSelected = $("select#rut").val();//rut seleccionado en filtro de busqueda
                        if (data.record) {
                            return '<input type="text" name="aRut" style="width:100px" value="' + data.record.runEmpleado + '" readonly />';
                        } else {
                            return '<input type="text" name="aRut" style="width:100px" value="'+rutSelected+'" readonly/>';
                        }
                    }
                },
                nombreEmpleado:{
                    title: 'Nombre Empleado',
                    width: '8%',
                    list: true,
                    edit:false,
                    sorting: true
                },
                anio:{
                    title: 'Año',
                    width: '8%',
                    list: true,
                    edit:false,
                    sorting: false
                },
                diasDisponiblesSemestre1:{
                    title: 'Dias disponibles 1erSem',
                    width: '7%',
                    list: true,
                    edit:false,
                    sorting: false,
                    defaultValue: '0'
                },
                diasUtilizadosSemestre1:{
                    title: 'Dias utilizados 1erSem',
                    width: '6%',
                    list: true,
                    sorting: false
                },
                diasDisponiblesSemestre2:{
                    title: 'Dias disponibles 2oSem',
                    width: '7%',
                    list: true,
                    edit:false,
                    sorting: false,
                    defaultValue: '0'
                },
                diasUtilizadosSemestre2:{
                    title: 'Dias utilizados 2oSem',
                    width: '6%',
                    list: true,
                    sorting: false
                },
                lastUpdate:{
                    title: 'Ult. Actualizacion',
                    width: '6%',
                    list: true,
                    sorting: false
                }        
            },
            toolbar: {
                    sorting: false,
                    items: [{
                        icon: '<%=request.getContextPath()%>/images/icon_csv.gif',
                        text: 'CSV',
                            click: function () {
                                document.location.href='<%=request.getContextPath()%>/DataExportServlet?type=resumenPACSV';
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
            $('#PATableContainer').jtable('load', {
                cencoId: $('#cencoId').val(),
                rutEmpleado: $('#rut').val(),
                paramAnio: $('#paramAnio').val()
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
        
        document.getElementById("paramAnio").value = '<%=anioActual%>';
    });

</script>
