
<%@page import="com.businessobjects.report.web.json.JSONObject"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="cl.bolchile.portalinf.vo.TipoOperacionVO"%>
<%@page import="cl.bolchile.portalinf.vo.TipoInstrumentoVO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="cl.bolchile.portalinf.vo.SearchFilterVO"%>
<%@page import="cl.bolchile.portalinf.vo.UsuarioVO"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.LinkedHashMap"%>

<%
  
    UsuarioVO theUser                       = (UsuarioVO)session.getAttribute("usuarioObj");
    SearchFilterVO filter                   = (SearchFilterVO)session.getAttribute("detailFiltroVO");
    ArrayList<TipoInstrumentoVO> tiposIns   = (ArrayList<TipoInstrumentoVO>)session.getAttribute("tiposIns");
    ArrayList<TipoOperacionVO> tiposOp      = (ArrayList<TipoOperacionVO>)session.getAttribute("tiposOp");
    LinkedHashMap<String, String> brokers   = (LinkedHashMap<String, String>)session.getAttribute("brokers");
    
    String selected="";
    SimpleDateFormat fechaformat= new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat horaformat	= new SimpleDateFormat("HH:mm:ss");
    java.util.Date ahora        = new java.util.Date();
    String startDate = fechaformat.format(ahora);
    String endDate = fechaformat.format(ahora);

    SearchFilterVO auxfiltro = (SearchFilterVO)session.getAttribute("detailFiltroVO");
    System.out.println("----->auxfiltro: "+auxfiltro);    
    if (filter!=null && filter.getFechaInicial() != null && filter.getFechaInicial().compareTo("") != 0){
        startDate = filter.getFechaInicial().substring(0, 10);
        endDate = filter.getFechaFinal().substring(0, 10);
    }
   
%>

<!DOCTYPE html>

<html>
<head>
    <meta charset="utf-8" />

    <meta name="keywords" content="femase,gestion,web">
    <meta name="description" content="Sistema de Gestion FEMASE-RRHH-Control Asistencia">
    <meta name="author" content="Adiazr">

    <title>Detalle Transacciones</title>

    <link href="../Jquery-JTable/Content/normalize.css" rel="stylesheet" type="text/css" />
    <link href='<%=request.getContextPath()%>/css-varios/googleapis.css' rel='stylesheet' type='text/css'>
    <link href="../Jquery-JTable/Content/Site.metro.css" rel="stylesheet" type="text/css" />

    <link href="../Jquery-JTable/Content/highlight.css" rel="stylesheet" type="text/css" />

        <link href="../Jquery-JTable/Content/themes/metroblue/jquery-ui.css" rel="stylesheet" type="text/css" />
        <link href="../Jquery-JTable/Scripts/jtable/themes/metro/blue/jtable.css" rel="stylesheet" type="text/css" />

    <link href="../Jquery-JTable/Scripts/syntaxhighligher/styles/shCore.css" rel="stylesheet" type="text/css" />
    <link href="../Jquery-JTable/Scripts/syntaxhighligher/styles/shThemeDefault.css" rel="stylesheet" type="text/css" />

    
    <!-- Necesario para datepicker actualizado-->
    <!--Requirement jQuery-->
	<!--<script type="text/javascript" src="../js/datepicker_new/jquery_1_7_2.min.js"></script>-->
    <!--<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>-->
    <!--Load Script and Stylesheet -->
    
    <style>
        div.filtering
        {
            border: 1px solid #999;
            margin-bottom: 5px;
            padding: 10px;
            background-color: #EEE;
        }
		
		body { background-color: #fefefe; padding-left: 2%; padding-bottom: 100px; color: #101010; }
footer{ font-size:small;position:fixed;right:5px;bottom:5px; }
a:link, a:visited { color: #0000ee; }
pre{ background-color: #eeeeee; margin-left: 1%; margin-right: 2%; padding: 2% 2% 2% 5%; }
p { font-size: 0.9rem; }
ul { font-size: 0.9rem; }
hr { border: 2px solid #eeeeee; margin: 2% 0% 2% -3%; }
h3 { border-bottom: 2px solid #eeeeee; margin: 2rem 0 2rem -1%; padding-left: 1%; padding-bottom: 0.1em; }
h4 { border-bottom: 1px solid #eeeeee; margin-top: 2rem; margin-left: -1%; padding-left: 1%; padding-bottom: 0.1em; }
    </style>

    <script src="../Jquery-JTable/Scripts/modernizr-2.6.2.js" type="text/javascript"></script>
    <script src="../Jquery-JTable/Scripts/jquery-1.9.1.min.js" type="text/javascript"></script>
    <script src="../Jquery-JTable/Scripts/jquery-ui-1.10.0.min.js" type="text/javascript"></script>
    
    <!-- Load jQuery JS -->
    <script src="<%=request.getContextPath()%>/js-varios/jquery-1.9.1.js"></script>
    <!-- Load jQuery UI Main JS  -->
    <script src="<%=request.getContextPath()%>/js-varios/jquery-ui.js"></script>
    <!-- Load SCRIPT.JS which will create datepicker for input field  -->
    <!--<script src="../js/datepicker_new/script.js"></script>    -->
    
    <script src="../Jquery-JTable/Scripts/syntaxhighligher/shCore.js" type="text/javascript"></script>
    <script src="../Jquery-JTable/Scripts/syntaxhighligher/shBrushJScript.js" type="text/javascript"></script>
    <script src="../Jquery-JTable/Scripts/syntaxhighligher/shBrushXml.js" type="text/javascript"></script>
    <script src="../Jquery-JTable/Scripts/syntaxhighligher/shBrushCSharp.js" type="text/javascript"></script>
    <script src="../Jquery-JTable/Scripts/syntaxhighligher/shBrushSql.js" type="text/javascript"></script>
    <script src="../Jquery-JTable/Scripts/syntaxhighligher/shBrushPhp.js" type="text/javascript"></script>
    <script src="../Jquery-JTable/Scripts/jtablesite.js" type="text/javascript"></script>
    <script src="../Jquery-JTable/Scripts/jtable/jquery.jtable.js" type="text/javascript"></script>
    
	<!--Load Script and Stylesheet Datepicker julio 2015-->
	<link rel="stylesheet" href="<%=request.getContextPath()%>/css-varios/jquery-ui.css" />

    <script type="text/javascript">

        function test1(){
            alert('cambio...');
            
        }

    </script>
    
</head>
<body>
    <div class="site-container">
        <div class="main-header" style="position: relative">
            <h1><a href="/">Informe<span class="light"></span></a></h1>
            <h2>Transacciones</h2>
        </div>
<div class="content-container">
            
<div class="padded-content-container">
               
<div>
    <form>
        <label>Tipo Instrumento
        <select name="market" id="market">
	<%
            Iterator<TipoInstrumentoVO> iterador = tiposIns.listIterator();
            while( iterador.hasNext() ) {
                TipoInstrumentoVO tipo = iterador.next();
                %>
                <option value="<%=tipo.getCodigo()%>"><%=tipo.getNombre()%></option>
                <%
            }
        %>
        </select>
        </label>
        
        <label>Desde <input type="text" name="start_date" id="start_date" value="<%=startDate%>"/></label>
        <label>Hasta <input type="text" name="end_date" id="end_date" value="<%=endDate%>"/></label>
        <label>Instrumento <input type="text" name="symbol" id="symbol" /></label>
        <label>Estado
        <select name="estado" id="estado">
            <option value="-1">----------------</option>
                <%if (theUser.getIdPerfil() == 1 || theUser.getIdPerfil() == 2){%>
                    <option value="0" selected>Eliminadas</option>
                <%}%>
            <option value="1" selected>Vigentes</option>
        </select>
        </label>    
        <label>Tipo
        <select name="tipo" id="tipo">
            <option value="-1">----------------</option>
            <%
                Iterator<TipoOperacionVO> iteraTiposOp = tiposOp.listIterator();
                while( iteraTiposOp.hasNext() ) {
                    TipoOperacionVO tipo = iteraTiposOp.next();
                %>
                <option value="<%=tipo.getCodigo()%>"><%=tipo.getNombre()%></option>
            <% } %>
        </select>
        </label>
        <label>Corredor
        <select name="broker" id="broker">
            <option value="-1">Todos</option>
            <%  selected="";
                if (theUser.getIdPerfil() == 3 || theUser.getIdPerfil() == 4){
                    selected="selected";
                }
                Iterator it = brokers.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry e = (Map.Entry)it.next();%>
                    <option value="<%=e.getKey()%>" <%=selected%>><%="["+e.getKey()+"] "+e.getValue()%></option>
              <%}%>
        </select>
        </label>
        
        <button type="submit" id="LoadRecordsButton">Buscar</button>
    </form>
</div>

<div id="TradesTableContainer"></div>
<div id="TotalesTableContainer"></div>
<script type="text/javascript">
	

  $(document).ready(function () {
        $('#TradesTableContainer').jtable({  
            title: 'Transacciones',
            paging: true, //Enable paging
            pageSize: 100, //Set page size (default: 10)
            sorting: true, //Enable sorting
            defaultSorting: 'trades.trade_date ASC, trades.symbol ASC', //Set default sorting,
            selecting: true, //Enable selecting
            //multiselect: true, //Allow multiple selecting
            //selectingCheckboxes: true, //Show checkboxes on first column
            actions: {
                listAction: '<%=request.getContextPath()%>/DetalleTransaccionesController?action=list'
                //,updateAction: '<%=request.getContextPath()%>/DetalleTransaccionesController?action=update'
            },
            fields: {
                id:{
                    title:'Id',
                    width: '5%',
                    key: true,
                    edit:false,
                    create:false,
                    visibility:'hidden',
                    columnSelectable:'false'
                },
                fecha:{
                    title:'Fecha',
                    width: '5%',
                    key: true,
                    edit:false,
                    create:false,
		    visibility:'fixed'
                },
                hora:{
                    title:'Hora',
                    width: '5%',
                    key: true,
                    edit:false,
                    create:false,
		    visibility:'fixed'
                },
                mercadoNombre:{
                    title: 'Mercado',
                    width: '5%',
                    edit:false,
                    create:false,
                    displayFormat: 'yyyy-mm-dd',
                    visibility:'hidden'
                },
                boardID:{
                    title: 'Board',
                    width: '5%',
		    key: true,
                    edit:true,
                    create:false,
		    visibility:'fixed',
                    input: function (data) {
                        if (data.record) {
                            return '<input type="text" name="boardID" style="width:200px" value="' + data.record.boardID + '" readonly />';
                        } else {
                            return '&nbsp;';
                        }
                    }
                },
                instrumento:{
                    title: 'Instrumento',
                    width: '5%',
		    key: true,
                    edit:true,
                    create:false,
		    visibility:'fixed',
                    input: function (data) {
                        if (data.record) {
                            return '<input type="text" name="instrumento" style="width:200px" value="' + data.record.instrumento + '" readonly />'; 
                        } else {
                            return '&nbsp;';
                        }
                    }
                },
                folio:{
                    title: 'Folio',
                    width: '5%',
                    key: true,
                    edit:true,
                    create:false,
                    defaultValue:0,
		    visibility:'fixed',
                    display: function (data) {
                        return '<p align="right">'+data.record.folio+'</p>';
                    }
                    ,input: function (data) {
                        if (data.record) {
                            return '<input type="text" name="folio" style="width:200px" value="' + data.record.folio + '" readonly />';
                        } else {
                            return '&nbsp;';
                        }
                    }
                },
                cantidadFmt:{
                    title: 'Cantidad',
                    width: '5%',
                    key: false,
                    edit:true,
                    create:false,
                    defaultValue:0,
		    visibility:'fixed',
                    display: function (data) {
                        return '<p align="right">'+data.record.cantidadFmt+'</p>';
                    },
                    input: function (data) {
                        if (data.record) {
                            return '<input type="text" name="cantidadFmt" style="width:200px" value="' + data.record.cantidadFmt + '" readonly />';
                        } else {
                            return '&nbsp;';
                        }
                    }
                },
                precioFmt:{
                    title: 'Precio',
                    width: '5%',
                    key: false,
                    edit:true,
                    create:false,
                    defaultValue:0,
		    visibility:'fixed',
                    display: function (data) {
                        return '<p align="right">'+data.record.precioFmt+'</p>';
                    },
                    input: function (data) {
                        if (data.record) {
                            return '<input type="text" name="precioFmt" style="width:200px" value="' + data.record.precioFmt + '" readonly />';
                        } else {
                            return '&nbsp;';
                        }
                    }
                },
                variacionFmt:{
                    title: 'Tasa',
                    width: '5%',
                    edit:false,
                    create:false,
                    display: function (data) {
                        return '<p align="right">'+data.record.variacionFmt+'</p>';
                    }
                },
                settlementDate_yyyymmdd:{
                    title: 'Fecha Venc.',
                    width: '5%',
                    edit:false,
                    create:false
                },
                settleTypeStr:{
                    title: 'Forma Pago',
                    width: '5%',
                    edit:false,
                    create:false,
                    visibility:'hidden'
                },
                lugarPago:{
                    title: 'Lugar Pago',
                    width: '5%',
                    edit:false,
                    create:false
                },
                labelMonedaMonto:{
                    title: 'Total transado (moneda, monto)',
                    width: '5%',
                    edit:false,
                    create:false,
                    visibility:'fixed',
                    display: function (data) {
                        return '<p align="right">'+data.record.labelMonedaMonto+'</p>';
                    }
                },
                labelComprador:{
                    title: 'Comprador',
                    width: '5%',
                    edit:true,
                    create:false,
                    visibility:'fixed',
                    input: function (data) {
                        if (data.record) {
                            return '<input type="text" name="labelComprador" style="width:200px" value="' + data.record.labelComprador + '" readonly />';
                        } else {
                            return '&nbsp;';
                        }
                    }
                },
                labelVendedor:{
                    title: 'Vendedor',
                    width: '5%',
                    edit:true,
                    create:false,
                    visibility:'fixed',
                    input: function (data) {
                        if (data.record) {
                            return '<input type="text" name="labelVendedor" style="width:200px" value="' + data.record.labelVendedor + '" readonly />';
                        } else {
                            return '&nbsp;';
                        }
                    }
                },
                flagVentaCorta:{
                    title: 'Venta Corta',
                    width: '5%',
                    edit:false,
                    create:false
                },
                fondoComprador:{
                    title: 'Fondo Compra',
                    width: '5%',
                    edit:false,
                    create:false,
                    visibility:'hidden'
                },
                fondoVendedor:{
                    title: 'Fondo Venta',
                    width: '5%',
                    edit:false,
                    create:false,
                    visibility:'hidden'
                },
                fecha_evento:{
                    title: 'Fecha evento',
                    width: '5%',
                    edit:false,
                    create:false,
                    visibility:'hidden'
                }
            },
            toolbar: {
                items: [{
                        icon: '<%=request.getContextPath()%>/images/icon_csv.gif',
                        text: 'CSV',
                        click: function () {
                            document.location.href='<%=request.getContextPath()%>/TradesExport?tipo=detailToCSV';
                        }
                    }
                        ,
                        {
                            icon: '<%=request.getContextPath()%>/images/propias_por_mercado_export.png',
                            text: 'Propias por Mercado',
                            click: function () {
                                document.location.href='<%=request.getContextPath()%>/TradesExport?tipo=ownByMarket';
                            }
                        },
                        {
                            icon: '<%=request.getContextPath()%>/images/propias_export.png',
                            text: 'Propias',
                            click: function () {
                                document.location.href='<%=request.getContextPath()%>/TradesExport?tipo=own';
                            }
                        }
                ]
            },
            //Register to selectionChanged event to hanlde events
            selectionChanged: function () {
                //Get all selected rows
                var $selectedRows = $('#TradesTableContainer').jtable('selectedRows');
                   
                $('#SelectedRowList').empty();
                if ($selectedRows.length > 0) {
                    //Show selected rows
                    $selectedRows.each(function () {
                        var record = $(this).data('record');
                        //alert('Selected folio: '+record.folio); 
                        $('#SelectedRowList').append(
                            '<b>Id</b>: ' + record.folio +
                            '<br /><b>Fecha</b>:' + record.fecha + '<br /><br />'
                            );
                    });
                } else {
                    //No rows selected
                    $('#SelectedRowList').append('No row selected! Select rows to see here...');
                }
            }
        });

        //Re-load records when user click 'load records' button.
        $('#LoadRecordsButton').click(function (e) {
            e.preventDefault();
            $('#TradesTableContainer').jtable('load', {
                market: $('#market').val(),
                symbol: $('#symbol').val(),
                start_date: $('#start_date').val(),
                end_date: $('#end_date').val(),
                broker: $('#broker').val(),
                tipo: $('#tipo').val(),
                estado: $('#estado').val()
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


                            <div class="main-footer" style="position: relative"><input type="hidden" name="oculto"></div>
    </div>
    
</body>
</html>

<script type="text/javascript">

    $(document).ready(function () {
        var $adsContainer = $('#AdsContainer');
        var showHideAds = function () {
            if ($(window).width() < 1070) {
                if ($adsContainer.is(':visible')) {
                    $adsContainer.hide();
                }
            } else {
                if (!$adsContainer.is(':visible')) {
                    $adsContainer.show();
                }
            }
        };
    
        showHideAds();

        $(window).resize(function () {
            showHideAds();
        });
    });
    
    // When the document is ready
    $(document).ready(function () {
        $('#start_date').datepicker({
            dateFormat: "yy-mm-dd"
        });

        $('#end_date').datepicker({
            dateFormat: "yy-mm-dd"
        });  


    });
        
</script>

