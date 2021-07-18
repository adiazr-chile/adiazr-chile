<%@ include file="/include/check_session.jsp" %>

<%@page import="cl.femase.gestionweb.vo.DispositivoVO"%>
<%@page import="cl.femase.gestionweb.vo.DepartamentoVO"%>
<%@page import="cl.femase.gestionweb.vo.EmpresaVO"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.LinkedHashMap"%>

<%
    List<EmpresaVO> empresas = (List<EmpresaVO>)session.getAttribute("empresas");
    List<DispositivoVO> dispositivos = (List<DispositivoVO>)session.getAttribute("dispositivos");
%>

<!DOCTYPE html>

<html>
<head>
    <meta charset="utf-8" />

    <title>Admin Centros de costo</title>

    <link href="../Jquery-JTable/Content/themes/metroblue/jquery-ui.css" rel="stylesheet" type="text/css" />
    <link href="../Jquery-JTable/Scripts/jtable/themes/metro/blue/jtable.css" rel="stylesheet" type="text/css" />
    
    <link href="../Jquery-JTable/Content/normalize.css" rel="stylesheet" type="text/css" />
    <link href='<%=request.getContextPath()%>/css-varios/googleapis.css' rel='stylesheet' type='text/css'>
    <link href="../Jquery-JTable/Content/Site.metro.css" rel="stylesheet" type="text/css" />

    <link href="../Jquery-JTable/Content/highlight.css" rel="stylesheet" type="text/css" />
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
             
         });
         
        function asignarTurnos(empresaId, deptoId, idCenco){
            var paramCencoId = empresaId+'--'+deptoId+'--'+idCenco;
            document.location.href='<%=request.getContextPath()%>/servlet/AsignacionTurnosCencosServlet?action=list_turnos&cencoId='+paramCencoId; 
        }
        
    </script>
    
</head>
<body>
    <div class="site-container">
        <div class="main-header" style="position: relative">
            <h1>Administraci&oacute;n de Centros de Costo</h1>
            <h2>Filtros de b&uacute;squeda</h2>
        </div>
<div class="content-container">
            
            <div class="padded-content-container">
                
<div class="filtering">
    <form>
        
        <label for="empresaId">Empresa</label>
        <select id="empresaId" name="empresaId" style="width:350px;" required>
        <option value="-1"></option>
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
        
        <label for="deptoId">Departamento</label>
        <select id="deptoId" name="deptoId" style="width:350px;" required>
            <option value="-1">--------</option>
        </select>

        <label>Nombre Cenco: <input type="text" name="nombre" id="nombre" /></label>
        <label>Estado:
            <select id="estado" name="estado" style="width:150px;" tabindex="2" required>
                <option value="-1"></option>
                <option value="1">Vigente</option>
                <option value="2">No Vigente</option>
            </select>
        </label>
        <button type="submit" id="LoadRecordsButton">Buscar</button>
    </form>
</div>

<div id="CCostoTableContainer"></div>
<script type="text/javascript">

    $(document).ready(function () {

        $('#CCostoTableContainer').jtable({       
            title: 'Centros de Costo',
            paging: true, //Enable paging
            pageSize: 10, //Set page size (default: 10)
            sorting: true, //Enable sorting
            defaultSorting: 'ccosto_nombre ASC', //Set default sorting
            actions: {
                listAction: '<%=request.getContextPath()%>/CentrosCostoController?action=list',
                createAction:'<%=request.getContextPath()%>/CentrosCostoController?action=create',
                updateAction: '<%=request.getContextPath()%>/CentrosCostoController?action=update'
                //,deleteAction: '<%=request.getContextPath()%>/ModulosSistemaController?action=delete'
            },
            fields: {
                MyButton: {
                    title: 'Accion',
                    width: '4%',
                    sorting: false,
                    edit: false,
                    create: false,
                    display: function(data) {
                         return '<button type="button" onclick="asignarTurnos(\'' + data.record.empresaId + '\',\'' + data.record.deptoId + '\',' + data.record.id + ')">Turnos</button> ';
                    }
                },
                id: {
                    title:'Id',
                    width: '10%',
                    key: true,
                    list: true,
                    create:false
                },
                nombre:{
                    title: 'Nombre',
                    width: '15%',
                    edit:true
                },
                empresaId: {
                    title:'Empresa',
                    width: '15%',
                    list: false,
                    create:true,
                    edit:true,
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=empresas';
                    }
                },
                /*empresaNombre:{
                    title: 'Empresa',
                    width: '15%',
                    list: true,
                    edit:false,
                    create:false        
                },*/
                /*deptoNombre:{
                    title: 'Departamento',
                    width: '15%',
                    list: true,
                    edit:false,
                    create:false        
                },*/
                deptoId: {
                    title:'Depto',
                    width: '15%',
                    list: false,
                    create:true,
                    edit:true,
                    dependsOn: 'empresaId', //deptos dependen de empresa.
                    options: function (data) {
                        if (data.source === 'list') {
                            //Retorna todas las comunas. 
                            //This method is called for each row on the table and jTable caches options based on this url.
                            return '<%=request.getContextPath()%>/LoadItems?type=departamentos&empresaId=-1';
                        }

                        //This code runs when user opens edit/create form or changes continental combobox on an edit/create form.
                        //data.source == 'edit' || data.source == 'create'
                        //alert('get deptos, empresa: '+ data.dependedValues.empresaId);
                        return '<%=request.getContextPath()%>/LoadItems?type=departamentos&empresaId=' + data.dependedValues.empresaId;
                    }
                },
                estado:{
                    title: 'Estado',
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=estados';
                    },
                    width: '10%',
                    edit:true
                },
                direccion:{
                    title: 'Direccion',
                    width: '20%',
                    edit:true
                },
                regionId: {
                    title: 'Region',
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=regiones';
                    },
                    width: '8%',
                    list: true
                },
                comunaId: {
                    title: 'Comuna',
                    dependsOn: 'regionId', //Comunas dependen de regiones.
                    options: function (data) {
                        if (data.source === 'list') {
                            //Retorna todas las comunas. 
                            //This method is called for each row on the table and jTable caches options based on this url.
                            return '<%=request.getContextPath()%>/LoadItems?type=comunas&regionId=-1';
                        }

                        //This code runs when user opens edit/create form or changes continental combobox on an edit/create form.
                        //data.source == 'edit' || data.source == 'create'
                        return '<%=request.getContextPath()%>/LoadItems?type=comunas&regionId=' + data.dependedValues.regionId;
                    },
                    list: true
                },    
                telefonos:{
                    title: 'Telefonos',
                    width: '10%',
                    edit:true
                },    
                email:{
                    title: 'Email Gral.',
                    width: '15%',
                    edit:true
                },
                emailNotificacion:{
                    title: 'Email notificacion',
                    width: '15%',
                    edit:true
                },
                dispositivosAsString:{
                    title: 'Dispositivos',
                    width: '15%',
                    edit:true
                },
                zonaExtrema:{
                    title: '�Zona Extrema?',
                    width: '5%',
                    edit:true,
                    type: 'radiobutton',
                    options: { 'S': 'Si', 'N': 'No' },
                    defaultValue: 'N'
                },    
                dispositivos: {
                    title: 'Dispositivos',
                    width: '20%',
                    list : false,
                    edit: true,
                    create: true,
                    input: function (data) {
                                var strdev="";
                                if (data.record){
                                    <%
                                    DispositivoVO currentDevice;
                                    Iterator<DispositivoVO> itdevices = dispositivos.iterator();
                                    while (itdevices.hasNext())
                                    {   currentDevice = itdevices.next();
                                        %>
                                        if (data.record.dispositivosAsString.search("<%=currentDevice.getId()%>") === -1){
                                            strdev = strdev + '<input type="checkbox" name="dispositivo" value="<%=currentDevice.getId()%>"><%=currentDevice.getId()%><br>';
                                        }
                                    <%}%>
                                }else{
                                    <%
                                    DispositivoVO currentDevice2;
                                    Iterator<DispositivoVO> itdevices2 = dispositivos.iterator();
                                    while (itdevices2.hasNext())
                                    {   currentDevice2 = itdevices2.next();
                                    %>
                                        strdev = strdev + '<input type="checkbox" name="dispositivo" value="<%=currentDevice2.getId()%>"><%=currentDevice2.getId()%><br>';
                                        
                                    <%}%>
                                    
                                }
                            return strdev;    
                            }
                }//fin field dispositivos
            }//fin fields
        });//fin jtable container   
    

    //Re-load records when user click 'load records' button.
    $('#LoadRecordsButton').click(function (e) {
        e.preventDefault();
        $('#CCostoTableContainer').jtable('load', {
            nombre: $('#nombre').val(),
            estado: $('#estado').val(),
            empresaId: $('#empresaId').val(),
            deptoId: $('#deptoId').val()
        });
    });

    //Load all records when page is first shown
    $('#LoadRecordsButton').click();
});//fin document.ready

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
