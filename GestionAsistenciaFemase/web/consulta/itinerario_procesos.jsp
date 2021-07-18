
<%@page import="cl.femase.gestionweb.vo.EmpresaVO"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.LinkedHashMap"%>

<%
    List<EmpresaVO> empresas = (List<EmpresaVO>)session.getAttribute("empresas");
    
%>

<!DOCTYPE html>

<html>
<head>
    <meta charset="utf-8" />

    <title>Itinerario de Procesos</title>

    <link href="../Jquery-JTable/Content/normalize.css" rel="stylesheet" type="text/css" />
    <link href='<%=request.getContextPath()%>/css-varios/googleapis.css' rel='stylesheet' type='text/css'>
    <link href="../Jquery-JTable/Content/Site.metro.css" rel="stylesheet" type="text/css" />

    <link href="../Jquery-JTable/Content/highlight.css" rel="stylesheet" type="text/css" />

        <link href="../Jquery-JTable/Content/themes/metroblue/jquery-ui.css" rel="stylesheet" type="text/css" />
        <link href="../Jquery-JTable/Scripts/jtable/themes/metro/blue/jtable.css" rel="stylesheet" type="text/css" />

    <link href="../Jquery-JTable/Scripts/syntaxhighligher/styles/shCore.css" rel="stylesheet" type="text/css" />
    <link href="../Jquery-JTable/Scripts/syntaxhighligher/styles/shThemeDefault.css" rel="stylesheet" type="text/css" />
    
    <script type="text/javascript">
        function ejecutar(){
            document.location.href='<%=request.getContextPath()%>/mantencion/ejecucion_procesos.jsp';
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
        .button-blue {
            background: #1385e5;
            background: -webkit-linear-gradient(top, #53b2fc, #1385e5);
            background: -moz-linear-gradient(top, #53b2fc, #1385e5);
            background: -o-linear-gradient(top, #53b2fc, #1385e5);
            background: linear-gradient(to bottom, #53b2fc, #1385e5);
            border-color: #075fa9;
            color: white;
            font-weight: bold;
            text-shadow: 0 -1px 0 rgba(0, 0, 0, 0.4);
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
   
</head>
<body>
    <div class="site-container">
        <div class="main-header" style="position: relative">
            <h1>Itinerario de Procesos<span class="light"></span></h1>
            <h2></h2>
        </div>
<div class="content-container">
            
            <div class="padded-content-container">
                
<div class="filtering">
    <form>
        <label for="empresaId">Empresa</label>
        <select id="empresaId" name="empresaId" style="width:150px;" required>
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
        <label>Proceso: 
            <select id="idProceso" name="idProceso" style="width:350px;" tabindex="2">
                <option value="-1" selected>----------</option>
              </select></label>
        <button type="submit" id="LoadRecordsButton">Buscar</button>
        <input name="botonpye" type="button" value="Ejecutar" class="button button-blue" onclick="ejecutar();">
    </form>
</div>

<div id="ItinerarioTableContainer"></div>
<script type="text/javascript">

    $(document).ready(function () {

            //Evento al seleccionar combo de empresa: cargar procesos definidos
             $('#empresaId').change(function(event) {
                 var auxempresaSelected = $("select#empresaId").val();
                 //alert('empresa selected: '+auxempresaSelected);
                 $.get('<%=request.getContextPath()%>/JsonListServlet', {
                        empresaProcesoID : auxempresaSelected,source: 'programacionProceso'
                 }, function(response) {
                        var select = $('#idProceso');
                        select.find('option').remove();
                        var newoption = "";
                        //var keyoption="";
                        var labeloption="";
                        newoption += "<option value='-1'>Todos</option>";
                        for (i=0; i<response.length; i++) {
                            //keyoption = response[i].empresaId+'|'+response[i].id;
                            labeloption = response[i].nombre+' ['+response[i].jobName+']';
                            newoption += "<option value='"+response[i].id+"' id='"+response[i].id+"'>"+labeloption+"</option>";
                        }
                        
                        $('#idProceso').html(newoption);
                        
                });
            });

        $('#ItinerarioTableContainer').jtable({       
            title: 'Itinerario de Procesos',
            paging: true, //Enable paging
            pageSize: 10, //Set page size (default: 10)
            sorting: true, //Enable sorting
            defaultSorting: 'proc_name ASC', //Set default sorting
            actions: {
                listAction: '<%=request.getContextPath()%>/ItinerarioProcesosController?action=list'
            },
            fields: {
                procesoId:{
                  title:'Id',  
                  key:true,
                  width: '11%'
                },
                procesoName: {
                    title:'Nombre',
                    width: '12%',
                    sorting: false
                },
                fechaHoraInicioEjecucion:{
                    title: 'Inicio ejecución',
                    width: '11%',
                    create:true,
                    edit:true
                },
                fechaHoraFinEjecucion:{
                    title: 'Fin ejecución',
                    width: '11%'
                },
                resultado:{
                    title: 'Resultado',
                    width: '11%',
                    sorting: false
                },
                usuario:{
                    title: 'Usuario',
                    width: '11%'
                },
                empresaId:{
                    title: 'Empresa',
                    width: '11%',
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=empresas'
                    },
                    sorting: false
                },
                deptoNombre:{
                    title: 'Departamento',
                    width: '11%'
                },
                cencoNombre:{
                    title: 'Centro de Costo',
                    width: '11%'
                }
            }
        });

        //Re-load records when user click 'load records' button.
        $('#LoadRecordsButton').click(function (e) {
            e.preventDefault();
            $('#ItinerarioTableContainer').jtable('load', {
                empresaId: $('#empresaId').val(),
                idProceso: $('#idProceso').val()
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
