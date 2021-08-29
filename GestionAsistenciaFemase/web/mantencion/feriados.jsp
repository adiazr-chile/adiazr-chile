<%@page import="java.util.ArrayList"%>
<%@ include file="/include/check_session.jsp" %>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.LinkedHashMap"%>

<%
    Calendar mycal=Calendar.getInstance();
    int anioActual = mycal.get(Calendar.YEAR);
    LinkedHashMap<Integer,String> listaAnios = new LinkedHashMap<Integer,String>();
    listaAnios.put(anioActual-1, String.valueOf(anioActual-1));
    listaAnios.put(anioActual, String.valueOf(anioActual));
    listaAnios.put(anioActual+1, String.valueOf(anioActual+1));
    
    ArrayList<String> meses=new ArrayList<>();
    meses.add("Enero");
    meses.add("Febrero");
    meses.add("Marzo");
    meses.add("Abril");
    meses.add("Mayo");
    meses.add("Junio");
    meses.add("Julio");
    meses.add("Agosto");
    meses.add("Septiembre");
    meses.add("Octubre");
    meses.add("Noviembre");
    meses.add("Diciembre");
%>
<!DOCTYPE html>

<html>
<head>
    <meta charset="utf-8" />

    <meta name="keywords" content="femase,gestion,web">
    <meta name="description" content="Sistema de Gestion FEMASE-RRHH-Control Asistencia">
    <meta name="author" content="Adiazr">

    <title>Admin Feriados</title>

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
   
</head>
<body>
    <div class="site-container">
        <div class="main-header" style="position: relative">
            <h1>Administraci&oacute;n de Feriados</h1>
            <h2>Filtros de b&uacute;squeda</h2>
        </div>
<div class="content-container">
            
            <div class="padded-content-container">
                
<div class="filtering">
    <form>
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
        
        <label>Mes:
            <select id="paramMes" name="paramMes" style="width:150px;" tabindex="2" required>
                <option value="-1">Seleccione mes</option>
                <%
                    for(int x = 0; x < meses.size(); x++) {
                        System.out.println("--->Itera mes: " + meses.get(x));
                       %>
                       <option value="<%=String.valueOf(x+1)%>"><%=meses.get(x)%></option>
                    <%}%>
            </select>
        </label>
            
        <label>Tipo Feriado:
            <select id="paramTipoFeriado" name="paramTipoFeriado" style="width:150px;" tabindex="2" required>
                <option value="-1" selected>Todos</option>
                <option value="1">Regional</option>
                <option value="2">Comunal</option>
                <option value="3">Nacional</option>
            </select>
        </label>
            
        <button type="submit" id="LoadRecordsButton">Buscar</button>
    </form>
</div>

<div id="FeriadosTableContainer"></div>
<script type="text/javascript">

    $(document).ready(function () {

        $('#FeriadosTableContainer').jtable({       
            title: 'Feriados',
            paging: true, //Enable paging
            pageSize: 10, //Set page size (default: 10)
            sorting: true, //Enable sorting
            defaultSorting: 'fecha ASC', //Set default sorting
            actions: {
                listAction: '<%=request.getContextPath()%>/CalendarioFeriadosController?action=list',
                createAction:'<%=request.getContextPath()%>/CalendarioFeriadosController?action=create',
                updateAction: '<%=request.getContextPath()%>/CalendarioFeriadosController?action=update'
                ,deleteAction: '<%=request.getContextPath()%>/CalendarioFeriadosController?action=delete'
            },
            fields: {
                fecha: {
                    title: 'Fecha',
                    width: '6%',
                    list: true,
                    create: true,
                    type: 'date',
                    displayFormat: 'yy-mm-dd',
                    inputClass: 'validate[required,custom[date]]'
                },
                rowKey:{
                    title: 'RowKey',
                    list: false,
                    key: true,
                    edit:false,
                    create: false,
                    type: 'hidden'
                },
                idTipoFeriado:{
                    title: 'Tipo Feriado',
                    width: '6%',
                    sorting: false,
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=tipos_feriados';
                    }
                },
                label:{
                    title: 'Nombre',
                    width: '15%',
                    edit:true,
                    sorting: false
                },
                observacion:{
                    title: 'Observacion',
                    width: '9%',
                    edit:true,
                    sorting: false
                },
                irrenunciable:{
                    title: 'Irrenunciable',
                    width: '9%',
                    sorting: false,
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=irrenunciable';
                    }
                },
                tipo:{
                    title: 'Tipo',
                    width: '9%',
                    sorting: false,
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=tipo_feriado_1';
                    }
                },
                respaldoLegal:{
                    title: 'Respaldo Legal',
                    width: '9%',
                    sorting: false
                },
                regionId:{
                    title: 'Region',
                    width: '9%',
                    edit:true,
                    sorting:false,
                    options: function(data){
                        return '<%=request.getContextPath()%>/LoadItems?type=regiones';
                    }
                },
                comunaId: {
                    title: 'Comuna',
                    width: '9%',
                    sorting: false,
                    dependsOn: 'regionId', //Comunas dependen de regiones.
                    options: function (data) {
                        if (data.source === 'list') {
                            //Retorna todas las comunas. 
                            //This method is called for each row on the table and jTable caches options based on this url.
                            return '<%=request.getContextPath()%>/LoadItems?type=comunas&regionId=-1';
                        }

                        //This code runs when user opens edit/create form or changes continental combobox on an edit/create form.
                        //data.source == 'edit' || data.source == 'create'
                        return '<%=request.getContextPath()%>/LoadItems?type=comunas_feriados&regionId=' + data.dependedValues.regionId;
                    },
                    list: true
                }, 
                fechaHoraIngreso:{
                    title: 'Fecha Ingreso',
                    width: '9%',
                    edit:false,
                    create:false,
                    sorting: false
                },
                fechaHoraActualizacion:{
                    title: 'Fecha actualizacion',
                    width: '9%',
                    edit:false,
                    create:false,
                    sorting: false
                }
            }
        });

        //Re-load records when user click 'load records' button.
        $('#LoadRecordsButton').click(function (e) {
            e.preventDefault();
            $('#FeriadosTableContainer').jtable('load', {
                paramAnio: $('#paramAnio').val(),
                paramMes: $('#paramMes').val(),
                paramTipoFeriado: $('#paramTipoFeriado').val()
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
        dayNamesMin: ['Do','Lu','Ma','Mi','Ju','Vi','Sa'],
        weekHeader: 'Sm',
        dateFormat: 'dd/mm/yy',
        firstDay: 1,
        isRTL: false,
        showMonthAfterYear: false,
        yearSuffix: ''
    };
    $.datepicker.setDefaults($.datepicker.regional['es']);


</script>
</body>
</html>
