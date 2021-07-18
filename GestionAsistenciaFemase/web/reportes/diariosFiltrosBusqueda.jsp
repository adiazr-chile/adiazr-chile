<%@ page import="java.util.Iterator"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="cl.bolchile.portalinf.vo.UsuarioVO"%>
<%@ page import="cl.bolchile.portalinf.vo.ModuleActionVO"%>
<%@ page import="java.util.HashMap"%>

<%
    UsuarioVO theUser = (UsuarioVO) session.getAttribute("usuarioObj");
    ArrayList<String> nemosSimultaneas = (ArrayList<String>) session.getAttribute("nemosSimultaneas");
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    java.util.Date ahora = new java.util.Date();


%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <title>Reportes Diarios - busqueda</title>

        <!--  usados para el JQuery datepicker -->
        <link rel="stylesheet" href="../style/datepicker/jquery-ui-1.10.3.custom.css" />
        <script src="../js/datepicker/jquery-1.9.1.js"></script>
        <script src="../js/datepicker/jquery-ui-1.10.3.custom.js"></script>
        <link rel="stylesheet" href="../style/datepicker/demostyle.css" />
        <!-- -->

        <script type="text/javascript">

            $(function() {
                $("#the_fecha_inicial").datepicker();
                $("#the_fecha_final").datepicker();
                $("#fecha_actual").datepicker();
                $("#fecha_actual2").datepicker();
                $("#fecha_actual3").datepicker();
                $("#fecha_de_proceso").datepicker();
                $("#fecha_presencia").datepicker();
                $("#primer_dia_habil_anio").datepicker();

                $("#the_fecha_inicial").datepicker("option", "dateFormat", "dd/mm/yy");
                $("#the_fecha_final").datepicker("option", "dateFormat", "dd/mm/yy");
                $("#fecha_actual").datepicker("option", "dateFormat", "dd/mm/yy");
                $("#fecha_actual2").datepicker("option", "dateFormat", "dd/mm/yy");
                $("#fecha_actual3").datepicker("option", "dateFormat", "dd/mm/yy");
                $("#fecha_de_proceso").datepicker("option", "dateFormat", "dd/mm/yy");
                $("#fecha_presencia").datepicker("option", "dateFormat", "dd/mm/yy");
        $("#primer_dia_habil_anio").datepicker("option", "dateFormat", "dd/mm/yy");        

        document.form1.the_fecha_inicial.value = '<%=sdf.format(ahora)%>'        ;
        document.form1.the_fecha_final.value = '<%=sdf.format(ahora)%>'        ;
        document.form1.fecha_actual.value = '<%=sdf.format(ahora)%>'        ;
        document.form1.fecha_actual2.value = '<%=sdf.format(ahora)%>'        ;
        document.form1.fecha_actual3.value = '<%=sdf.format(ahora)%>'        ;
        document.form1.fecha_de_proceso.value = '<%=sdf.format(ahora)%>'        ;
        document.form1.fecha_presencia.value = '<%=sdf.format(ahora)%>'        ;
                document.form1.primer_dia_habil_anio.value = '<%=sdf.format(ahora)%>';

            });

            function inicio() {
                filtros1.style.visibility = 'hidden';
                capa1.style.visibility = 'hidden';
                capa2.style.visibility = 'hidden';
                capa3.style.visibility = 'hidden';
                capa4.style.visibility = 'hidden';
                capa5.style.visibility = 'hidden';
                capa6.style.visibility = 'hidden';
            }

            function enviarDatos() {
                //preLoaderDiv.style.visibility='visible';
                if (document.form1.tipo.value == 'precios_medios_simultaneas') {
                    var x = document.getElementById("nemos");
                    if (x.length == 0) {
                        alert('Debe ingresar al menos un nemotecnico');
                    } else {
                        var nemos = listaCreaCadena(x) + '|';
                        document.form1.listaNemos.value = nemos;
                        document.form1.submit();
                    }
        } else         {
                    document.form1.action = "<%=request.getContextPath()%>/RptCRDiario";
                    if (document.form1.tipo.value == 'C_folios_svs'
                            || document.form1.tipo.value == 'D_ftecik0'
                            || document.form1.tipo.value == 'E_ftecik9'
                    || document.form1.tipo.value == 'fondos_mutuos        ') {
                        document.form1.action = "<%=request.getContextPath()%>/ReportesTexto";
                    }

                    document.form1.submit();
                }
            }

            function showFilters(itemSelected) {
                /**	prensa-AM" selected="selected">Prensa AM (ESSENTIA)</option>
                 variacion-indices
                 */
                //**ocultar todos los filtros
                filtros1.style.visibility = 'hidden';
                capa1.style.visibility = 'hidden';
                capa2.style.visibility = 'hidden';
                capa3.style.visibility = 'hidden';
                capa4.style.visibility = 'hidden';
                capa5.style.visibility = 'hidden';
                capa6.style.visibility = 'hidden';
                if (itemSelected == 'prensa-AM' || itemSelected == 'variacion-indices' || itemSelected == 'variacion-indices-prensa') {
                    /*mostrar filtros:
                     ultimo_dia_habil_año_anterior
                     fecha_actual
                     dia_habil_anterior
                     ultimo_dia_habil_mes_anterior
                     */
                    filtros1.style.visibility = 'visible';
                } else if (itemSelected == 'C_folios_svs' || itemSelected == 'D_ftecik0' || itemSelected == 'E_ftecik9' || itemSelected == 'fondos_mutuos') {
                    //filtro fecha_de_proceso
                    capa1.style.visibility = 'visible';
                } else if (itemSelected == 'estadisticas') {
                    //filtro fecha_actual, fecha_presencia y Primer dia habil del año
                    capa3.style.visibility = 'visible';
                } else if (itemSelected == 'resumentrx'
                        || itemSelected == 'montos-semana'
                        || itemSelected == 'pizarra-banchile'
                        || itemSelected == 'pizarra-larrain'
                        || itemSelected == 'precios_adrian'
                        || itemSelected == 'precios_cierre_bec'
                        || itemSelected == 'pizarra_corredores') {
                    //filtro fecha inicial y fecha final
                    capa4.style.visibility = 'visible';
                } else if (itemSelected == 'op_plazo' || itemSelected == 'ventas_cortas') {
                    //filtro fecha_actual
                    capa5.style.visibility = 'visible';
                } else if (itemSelected == 'precios_medios_simultaneas') {
                    //filtro fecha inicial y fecha final
                    capa4.style.visibility = 'visible';
                    //filtro lista de nemotecnicos
                    capa6.style.visibility = 'visible';
                }
            }

            function addNemo()
            {
                var x = document.getElementById("nemos");

                var option = document.createElement("option");
                option.text = document.form1.nemotecnico.value;
                if (document.form1.nemotecnico.value != '') {
                    if (!listaExisteValor(x, document.form1.nemotecnico.value)) {
                        try {
                            // for IE earlier than version 8
                            x.radd(option, x.options[null]);
                        } catch (e) {
                            x.add(option, null);
                        }
                    }
                }
            }

            function removeNemo()
            {
                var x = document.getElementById("nemos");
                listaQuitarElemSel(x);
            }

            function listaQuitarElemSel(lista) {
                listaQuitarElemento(lista, lista.selectedIndex);
            }

            function listaQuitarElemento(lista, indice) {
                if (indice >= 0) {
                    for (x = indice; x < lista.options.length - 1; x++) {

                        lista.options[x] = new Option(lista.options[x + 1].text, lista.options[x + 1].value);
                        if (lista.options[x + 1].selected)
                            lista.options[x].selected = true;
                    }
                    lista.options.length = lista.options.length - 1;
                }
            }

            function listaCreaCadena(lista) {
                var cadena = "";
                for (x = 0; x < lista.options.length; x++) {
                    if (lista.options[x].value != "-1")
                        cadena += lista.options[x].value + "|";
                }
                cadena = cadena.substring(0, cadena.length - 1);
                return cadena;
            }

            function listaExisteValor(lista, valor) {
                var existe = false;
                for (x = 0; x < lista.options.length; x++) {
                    if (lista.options[x].value == valor) {
                        existe = true;
                        break;
                    }
                }
                return existe;
            }
        </script>
        <link rel="stylesheet" type="text/css" href="../style/style.css" media="screen" />
        <style type="text/css">
            <!--
            body {
                background-color: #FFF;
            }

            #capa1 {
                width: 400px;
                height: 50px;
                position: absolute;
                top: 60px;
                left: 100px;
                background: #FFF;
                border:groove;
            }

            #capa2 {
                width: 400px;
                height: 50px;
                position: absolute;
                top: 121px;
                left: 99px;
                background: #FFF;
                border:groove;
            }
            #capa3 {
                width: 935px;
                height: 50px;
                position: absolute;
                top: 60px;
                left: 100px;
                background: #FFF;
                border:groove;
            }
            #capa4 {
                width: 935px;
                height: 50px;
                position: absolute;
                top: 60px;
                left: 100px;
                background: #FFF;
                border:groove;
            }
            #capa5 {
                width: 935px;
                height: 50px;
                position: absolute;
                top: 60px;
                left: 100px;
                background: #FFF;
                border:groove;
            }
            #capa6 {
                width: 935px;
                height: 70px;
                position: absolute;
                top: 110px;
                left: 100px;
                background: #FFF;
                border:groove;
            }

        </style></head>

    <body onLoad="inicio();">
        <div id="menu">
            <div id="content">
                <div class="menu">
                    <form id="form1" name="form1" method="post" action="<%=request.getContextPath()%>/RptCRDiario" target="mainFrame2">
                        <table class="menu" width="100%" border="0" align="center" cellpadding="2" cellspacing="2">
                            <tr>
                                <td height="37" align="right" valign="top" class="tableHeaderSpecial">Reportes - Diarios</td>
                                <td valign="top">&nbsp;</td>
                                <td align="right" valign="top" class="tableHeaderSpecial">&nbsp;</td>
                                <td colspan="2" valign="top">&nbsp;</td>
                                <td valign="top">&nbsp;</td>
                                <td valign="top">&nbsp;</td>
                            </tr>
                            <tr>
                                <td width="16%" height="37" align="right" valign="top" class="tableHeaderSpecial">Tipo&nbsp;</td>
                                <td width="17%" valign="top">
                                    <select name="tipo" id="tipo" onchange="showFilters(this.value)">
                                        <option value="-1" selected="selected">Seleccione tipo</option>
                                        <%
                                            HashMap<String, ArrayList<ModuleActionVO>> appModules = theUser.getModuleActions();
                                            String keyModuleId = "7";
                                            ArrayList<ModuleActionVO> actions;
                                            //Iterator iterator = appModules.keySet().iterator();
                                            actions = appModules.get(keyModuleId);//lista con acciones
                                            Iterator<ModuleActionVO> actionsIterator = actions.iterator();
                                            while (actionsIterator.hasNext()) {
                                                ModuleActionVO theaction = actionsIterator.next();
                                                if (theaction.getUrl() == null
                                                        && theaction.getCategory() != null
                                  && theaction.getCategory().compareTo("diario") == 0) {%>
                                        <option value="<%=theaction.getCode()%>"><%=theaction.getName()%></option>
                                        <%}%>
                                        <%}//fin while (actionsIterator.hasNext()) %>

                                        <!-- 
                                        Mostrar opciones segun accesos del perfil de usuario
                                        
                                         <option value="prensa-AM">Prensa AM (ESSENTIA)</option>
                                         <option value="fondos_mutuos">Fondos Mutuos</option>
                                         <option value="variacion-indices">Variaciones Indices (Interno)</option>
                                         <option value="variacion-indices-prensa">Variaciones Indices (Prensa)</option>
                                         <option value="estadisticas">Estadisticas Diarias</option>
                                         <option value="resumentrx">Resumen Transacciones</option>
                                         <option value="montos-semana">Montos Semana (prensa, dia viernes)</option>
                                         <option value="pizarra-banchile">Pizarra Banchile</option>
                                         <option value="pizarra-larrain">Pizarra Larrain Vial</option>
                                         <option value="op_plazo">Operaciones a Plazo Vigentes</option>
                                         <option value="ventas_cortas">Posiciones Ventas Cortas</option>
                                         <option value="precios_adrian">Precios ADRIAN</option>
                                         <option value="precios_cierre_bec">Precios Cierre BEC</option>
                                         <option value="precios_medios_simultaneas">Precios Medios Simultaneas</option>
                                         <option value="pizarra_corredores">Pizarra Corredores</option>
                                         <option value="C_folios_svs">Folios SVS</option>
                                         <option value="D_ftecik0">Ftecik0</option>
                                         <option value="E_ftecik9">Ftecik9</option>
                                        -->
                                    </select></td>
                                <td width="21%" align="right" valign="top" class="tableHeaderSpecial">Formato Salida&nbsp;</td>
                                <td colspan="2" valign="top"><select name="formato" id="formato">
                                        <option value="pdf" selected="selected">PDF</option>
                                        <option value="excel">Excel</option>
                                        <!--<option value="txt">Texto</option>-->
                                    </select></td>
                                <td width="15%" valign="top"><input type="button" name="aceptar" id="aceptar" value="Aceptar" onclick="enviarDatos()"/></td>
                                <td width="15%" valign="top">&nbsp;</td>
                            </tr>
                            <tr>
                                <td height="37" colspan="7" align="right" valign="top"> 
                                    <!-- DIV CON FILTROS PARA PRENSA AM Y OTROS-->
                                    <div id="filtros1">

                                        <table width="100%" border="0" align="center" cellpadding="1" cellspacing="1">
                                            <tr>
                                                <td align="right" class="tableHeaderSpecial">Fecha actual (dd/MM/yyyy)</td>
                                                <td width="12%"><input name="fecha_actual" type="text" id="fecha_actual" size="12" maxlength="10" value="<%=sdf.format(ahora)%>"/></td>
                                                <td width="26%" align="right">&nbsp;&nbsp;</td>
                                                <td width="32%">&nbsp;</td>
                                            </tr>
                                        </table>
                                    </div>        

                                    <!-- FIN DIV CON FILTROS PARA PRENSA AM Y OTROS-->        
                                    <!-- DIV CON FILTRO FECHA DE PROCESO-->
                                    <div id="capa1">

                                        <table width="100%" border="0" align="center" cellpadding="2" cellspacing="5">
                                            <tr>
                                                <td width="58%" align="right" class="tableHeaderSpecial">Fecha de proceso (dd/MM/yyyy)</td>
                                                <td width="42%"><input name="fecha_de_proceso" type="text" id="fecha_de_proceso" size="12" maxlength="10" value="<%=sdf.format(ahora)%>"/></td>
                                            </tr>
                                        </table>
                                    </div>    

                                    <div id="capa2">
                                        <table width="100%" border="0" align="center" cellpadding="2" cellspacing="5">
                                            <tr>
                                                <td width="58%" align="right" class="tableHeaderSpecial">Fecha de proceso 2 (dd/MM/yyyy)</td>
                                                <td width="42%"><input name="fecha_2" type="text" id="fecha_2" size="12" maxlength="10" value="<%=sdf.format(ahora)%>"/></td>
                                            </tr>
                                        </table>
                                    </div>  

                                    <div id="capa3">
                                        <table width="100%" border="0" align="center" cellpadding="2" cellspacing="5">
                                            <tr>
                                                <td width="21%" align="right" class="tableHeaderSpecial">Fecha actual (dd/MM/yyyy)</td>
                                                <td width="12%"><input name="fecha_actual2" type="text" id="fecha_actual2" size="12" maxlength="10" value="<%=sdf.format(ahora)%>"/></td>
                                                <td width="18%" class="tableHeaderSpecial">Fecha Presencia (dd/MM/yyyy)</td>
                                                <td width="12%"><input name="fecha_presencia" type="text" id="fecha_presencia" size="12" maxlength="10" value="<%=sdf.format(ahora)%>"/></td>
                                                <td width="22%" class="tableHeaderSpecial">1er d&iacute;a h&aacute;bil del a&ntilde;o (dd/MM/yyyy)</td>
                                                <td width="15%"><input name="primer_dia_habil_anio" type="text" id="primer_dia_habil_anio" size="12" maxlength="10" value="<%=sdf.format(ahora)%>"/></td>
                                            </tr>
                                        </table>
                                    </div>

                                    <div id="capa4">
                                        <table width="100%" border="0" align="center" cellpadding="2" cellspacing="5">
                                            <tr>
                                                <td width="21%" align="right" class="tableHeaderSpecial">Fecha inicial(dd/MM/yyyy)</td>
                                                <td width="12%"><input name="the_fecha_inicial" type="text" id="the_fecha_inicial" size="12" maxlength="10" value="<%=sdf.format(ahora)%>"/></td>
                                                <td width="18%" class="tableHeaderSpecial">Fecha Final (dd/MM/yyyy)</td>
                                                <td width="12%"><input name="the_fecha_final" type="text" id="the_fecha_final" size="12" maxlength="10" value="<%=sdf.format(ahora)%>"/></td>
                                                <td width="22%">&nbsp;</td>
                                                <td width="15%">&nbsp;</td>
                                            </tr>
                                        </table>
                                    </div>        

                                    <div id="capa5">
                                        <table width="100%" border="0" align="center" cellpadding="2" cellspacing="5">
                                            <tr>
                                                <td width="21%" align="right" class="tableHeaderSpecial">Fecha actual (dd/MM/yyyy)</td>
                                                <td width="12%"><input name="fecha_actual3" type="text" id="fecha_actual3" size="12" maxlength="10" value="<%=sdf.format(ahora)%>"/></td>
                                                <td width="18%">&nbsp;</td>
                                                <td width="12%">&nbsp;</td>
                                                <td width="22%">&nbsp;</td>
                                                <td width="15%">&nbsp;</td>
                                            </tr>
                                        </table>
                                    </div>

                                    <div id="capa6">
                                        <table width="100%" border="0" align="center" cellpadding="2" cellspacing="5">
                                            <tr>
                                                <td width="24%" align="right" valign="top" class="tableHeaderSpecial">Nemot&eacute;cnico</td>
                                                <td width="22%" valign="top"><input name="nemotecnico" type="text" id="nemotecnico" size="25" maxlength="30"/></td>
                                                <td width="16%" valign="top"><h7>&nbsp;
                                                        <input type="button" name="aceptar2" id="aceptar2" value="Agregar" onclick="addNemo()"/>
                                                        <input type="button" name="aceptar3" id="aceptar3" value="Quitar" onclick="removeNemo()"/>
                                                    </h7></td>
                                                <td width="19%" valign="top" class="tableHeaderSpecial">Nemot&eacute;cnicos seleccionados:</td>
                                                <td width="19%" valign="top">
                                                    <select name="nemos" size="3" id="nemos" style="width:120px">
                                                        <%
                                                            String nemoListar = "";
                                                            Iterator<String> iterNemos = nemosSimultaneas.listIterator();
                                                            while (iterNemos.hasNext()) {
                nemoListar = iterNemos.next();%> 
                                                        <option value="<%=nemoListar%>"><%=nemoListar%></option>
                                                        <%}
                                                        %>
                                                    </select>
                                                    <input type="hidden" name="listaNemos" id="listaNemos" />
                                                    <h7></h7></td>
                                            </tr>
                                        </table>
                                    </div> </td>
                            </tr>
                        </table>
                    </form>

                </div>
                <div style="clear: both;"></div>
            </div>
        </div>
    </body>
</html>
