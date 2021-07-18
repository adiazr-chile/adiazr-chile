<%@ page import="java.util.Iterator"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="cl.bolchile.portalinf.vo.UsuarioVO"%>
<%@ page import="cl.bolchile.portalinf.vo.ModuleActionVO"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="cl.bolchile.portalinf.report1064.ReportData"%>
<%@ page import="cl.bolchile.portalinf.report1064.dao.Broker"%>
<%@ page import="cl.bolchile.portalinf.report1064.dao.Intranet1064Log"%>
<%@ page import="cl.bolchile.portalinf.report1064.register.RegisterAsignacion"%>
<%@ page import="cl.bolchile.portalinf.report1064.register.RegisterData1064"%>
<%@ page import="cl.bolchile.portalinf.report1064.register.RegisterUnidades"%>
<%@ page import="cl.bolchile.portalinf.servicio.BrokerSrv"%>
<%@ page import="java.util.LinkedHashMap"%>
<%@ page import="java.util.Map"%>
<%@ page import="org.joda.time.format.DateTimeFormat"%>
<%@ page import="org.joda.time.format.DateTimeFormatter"%>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="java.text.DecimalFormatSymbols"%>



<%
    UsuarioVO theUser = (UsuarioVO) session.getAttribute("usuarioObj");
    ArrayList<String> foliosList = (ArrayList<String>) session.getAttribute("foliosList");
    ArrayList<RegisterAsignacion> asignaciones = (ArrayList<RegisterAsignacion>) session.getAttribute("listaAsignaciones");
    ArrayList<Broker> brokers = (ArrayList<Broker>) session.getAttribute("brokerList");
    ArrayList<RegisterUnidades> unidades = (ArrayList<RegisterUnidades>) session.getAttribute("listaAsignaciones");
    String desde = (String) session.getAttribute("foliosIni");
    String hasta = (String) session.getAttribute("foliosFin");
    String displayAsignaciones = (String) session.getAttribute("displayAsignaciones");
    String displayCargas = (String) session.getAttribute("displayCargas");
    String tipo = "";
    Integer perfil = (Integer) session.getAttribute("perfil");
    Integer modificada = (Integer) session.getAttribute("modificada");
    tipo = (String) session.getAttribute("tipo");
    String selectFolios = "all";
    if (session.getAttribute("select_folios") != null) {
        selectFolios = (String) session.getAttribute("select_folios");
    }
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    Broker brokerSelected = (Broker) session.getAttribute("brokerSelected");
    Integer folioID = (Integer) session.getAttribute("folioID");

    RegisterUnidades asgAct = (RegisterUnidades) session.getAttribute("asignacion");
    ArrayList<Intranet1064Log> logAsignaciones = (ArrayList<Intranet1064Log>) session.getAttribute("listaLog");
    DateTimeFormatter sdfd = DateTimeFormat.forPattern("yyyy-MM-dd");
    DateTimeFormatter sdfh = DateTimeFormat.forPattern("HH:mm:ss");
    DecimalFormatSymbols simbolo = new DecimalFormatSymbols();
    simbolo.setDecimalSeparator(',');
    simbolo.setGroupingSeparator('.');
    DecimalFormat formateador = new DecimalFormat("###,###.####", simbolo);
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <title>Reportes 1064</title>

        <!--  usados para el JQuery datepicker -->
        <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/style/style.css" media="screen" />
        <script src="../js/datepicker/jquery-1.9.1.js"></script>
        <script src="../js/datepicker/jquery-ui-1.10.3.custom.js"></script>
        <link rel="stylesheet" href="../style/datepicker/demostyle.css" />
        <!-- -->

        <script type="text/javascript">
            function showFilters(itemSelected) {
                document.filtrosAsignaciones.style.display = 'none';
                document.asignacion.style.display = 'none';
                document.carga.style.display = 'none';
                document.filtrosModificada.style.display = 'none';
                alert("Pase los style");
                if (itemSelected === 'asignacion') {
                    document.filtrosAsignaciones.style.display = 'block';
                    document.asignacion.style.display = 'block';
                    document.filtrosModificada.style.display = 'inline';
                } else if (itemSelected === 'carga') {
                    document.carga.style.display = 'block';
                }
            }

            function onSaveBtn() {
                parent.bodyFrm.onSaveBtn();
            }

            function cambiarBroker() {
                document.form1064Filtro.submit();
            }

            function ingresoArchivo() {
                document.ingreso_archivo.submit();
            }

            function aplicarCambios() {
                var nv = new Number(document.distribucion_unidades.asgActNV.value.replace(/\./g, "")) || 0;
                var vp = new Number(document.distribucion_unidades.asgActVP.value.replace(/\./g, "")) || 0;
                var vl = new Number(document.distribucion_unidades.asgActVL.value.replace(/\./g, "")) || 0;
                var ad = new Number(document.distribucion_unidades.asgActAD.value.replace(/\./g, "")) || 0;
                var ni = new Number(document.distribucion_unidades.asgActNI.value.replace(/\./g, "")) || 0;
                var ingresado = nv + vp + vl + ad + ni;
                var total = new Number(<%=asgAct.getNV() + asgAct.getVP() + asgAct.getVL() + asgAct.getAD() + asgAct.getNI()%>);
                var valor = total - ingresado;
                if (valor === 0) {
                    document.distribucion_unidades.submit();
                } else if (valor > 0) {
                    var mensaje = "Error en la asignaci\xf3n de las unidades: Faltan por asignar " + numberWithPoints(valor) + " unidades";
                    alert(mensaje);
                } else {
                    var mensaje = "Error en la asignaci\xf3n de las unidades: Sobran " + numberWithPoints(Math.abs(valor)) + " unidades";
                    alert(mensaje);
                }
            }

            function isNumber(n) {
                return !isNaN(parseInt(n)) && isFinite(n);
            }

            function numberWithCommas(x) {
                var parts = x.toString().split(",");
                parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ".");
                return parts.join(",");
            }

            function numberWithPoints(x) {
                var parts = x.toString();
                parts = parts.replace(/\B(?=(\d{3})+(?!\d))/g, ".");
                return parts;
            }

            function isNumberKey(evt) {
                var charCode = (evt.which) ? evt.which : event.keyCode
                if (charCode > 31 && (charCode < 48 || charCode > 57))
                    return false;

                return true;
            }

            function checkUnits() {
                var nv = new Number(document.distribucion_unidades.asgActNV.value.replace(/\./g, "")) || 0;
                var vp = new Number(document.distribucion_unidades.asgActVP.value.replace(/\./g, "")) || 0;
                var vl = new Number(document.distribucion_unidades.asgActVL.value.replace(/\./g, "")) || 0;
                var ad = new Number(document.distribucion_unidades.asgActAD.value.replace(/\./g, "")) || 0;
                var ni = new Number(document.distribucion_unidades.asgActNI.value.replace(/\./g, "")) || 0;
                var ingresado = nv + vp + vl + ad + ni;
                var total = new Number(<%=asgAct.getNV() + asgAct.getVP() + asgAct.getVL() + asgAct.getAD() + asgAct.getNI()%>);
                document.distribucion_unidades.asgActNV.value = numberWithPoints(nv);
                document.distribucion_unidades.asgActVP.value = numberWithPoints(vp);
                document.distribucion_unidades.asgActVL.value = numberWithPoints(vl);
                document.distribucion_unidades.asgActAD.value = numberWithPoints(ad);
                document.distribucion_unidades.asgActNI.value = numberWithPoints(ni);
                document.distribucion_unidades.asgDiff.value = numberWithPoints(total - ingresado);
            }
        </script>

        <link rel="stylesheet" type="text/css" href="../style/style.css" media="screen" />
        <style type="text/css">
            body {
                background-color: #FFF;
            }
        </style>
    </head>
    <body>
        <table width="90%" height="50" border="0">
            <tr>
                <td height="16" class="textOnly">SVS 1064</td>
                <td><span class="tableHeaderSpecial">Fecha: <%=ReportData.getConsultedDay().toString(sdfd)%> </span></td>
                <td>
                    <% if (perfil == 1) {%>
                    <form id="generar_archivo" name="generar_archivo" method="post" action="<%=request.getContextPath()%>/Reporte1064" targer="_top">
                        <input type="hidden" id="htmlFormName" name="htmlFormName" value="generar_archivo"/>
                        <input type="submit" name="generar_archivo" id="generar_archivo" value="Generar Archivo 1064" />    
                    </form>
                    <%
                        }
                    %>
                </td>
            </tr>
        </table>
        <form id="form1064Filtro" name="form1064Filtro" method="post" action="<%=request.getContextPath()%>/Reporte1064">
            <input type="hidden" id="htmlFormName" name="htmlFormName" value="form1064Filtro"/>
            <table>
                <tr>
                    <td class="tableHeaderSpecial">
                        Corredor
                    </td>
                    <td>
                        <select name="corredor_id" id="corredor_id" onchange="cambiarBroker()">
                            <%
                                if (brokers != null && brokers.size() > 0) {
                                    if (brokerSelected == null) {
                                        brokerSelected = brokers.get(0);
                                        session.setAttribute("brokerSelected", brokerSelected);
                                    }
                                    for (Iterator<Broker> it = brokers.iterator(); it.hasNext();) {
                                        Broker broker = it.next();
                                        if (broker.getCodBroker().equals(brokerSelected.getCodBroker())) {
                            %>
                            <option value="<%=broker.getCodBroker()%>" selected><%=broker.getBrokerName()%></option>
                            <%
                            } else {
                            %>
                            <option value="<%=broker.getCodBroker()%>"><%=broker.getBrokerName()%></option>
                            <%
                                        }
                                    }
                                }
                            %>
                        </select>
                    </td>
                    <td class="tableHeaderSpecial">&nbsp;Acci&oacute;n
                        <select name="tipo" id="tipo" onchange="cambiarBroker()">
                            <%
                                if (tipo.equals("asignacion")) {
                            %>
                            <option value="asignacion" selected>Revisar asignaciones</option>
                            <%                            } else {
                            %>
                            <option value="asignacion">Revisar asignaciones</option>
                            <%                        }
                                if (brokerSelected.getIs1064() == 2) {
                                    if (tipo.equals("carga")) {
                            %>
                            <option value="carga" selected>Cargar archivos</option>
                            <%                            } else {
                            %>
                            <option value="carga">Cargar archivos</option>
                            <%                                    }
                                }
                            %>
                        </select>
                    </td>
                    <%
                        if (brokerSelected.getIs1064() == 1) {
                    %>
                    <td class="tableHeaderSpecial">
                        <div class="filtrosModificada" id="filtrosModificada" style="display:<%=displayAsignaciones%>">
                            &nbsp;Estado de la asignaci&oacute;n
                            <select name="modificada" id="modificada" onchange="cambiarBroker()">
                                <% if (modificada.intValue() == 0) {
                                %>
                                <option value="0" selected>Todas</option>
                                <option value="1">Asignadas</option>
                                <option value="2">No asignadas</option>
                                <%                                } else if (modificada.intValue() == 1) {
                                %>
                                <option value="0">Todas</option>
                                <option value="1" selected>Asignadas</option>
                                <option value="2">No asignadas</option>
                                <%                                } else if (modificada.intValue() == 2) {
                                %>
                                <option value="0">Todas</option>
                                <option value="1">Asignadas</option>
                                <option value="2" selected>No asignadas</option>
                                <%                                    }
                                %>
                            </select>                        
                        </div>
                    </td>
                    <%
                        }
                    %>
                </tr>
            </table>
        </form>
        <% if (tipo.equals("asignacion")) {
                if (brokerSelected != null && unidades != null && !unidades.isEmpty()) {
        %>
        <hr/>
        <form id="form1064Filtro2" name="form1064Filtro2" method="post" action="<%=request.getContextPath()%>/Reporte1064">
            <div class="filtrosAsignaciones" id="filtrosAsignaciones" style="display:<%=displayAsignaciones%>">
                <table width="59%">
                    <tr>
                        <td width="167">&nbsp;</td>
                        <td width="256">&nbsp;</td>
                        <td width="66">&nbsp;</td>
                    </tr>
                    <tr>
                        <td width="167" class="tableHeaderSpecial">
                            <% if (selectFolios.equals("all")) {%>
                            <input type="radio" name="select_folios" value="all" id="select_folios_0" checked/>
                            <%} else {%>
                            <input type="radio" name="select_folios" value="all" id="select_folios_0"/>
                            <%}%>
                            Todos los folios
                        </td>
                        <td width="256">&nbsp;</td>
                        <td width="66">&nbsp;</td>
                    </tr>
                    <tr>
                        <td height="42" class="tableHeaderSpecial">
                            <% if (selectFolios.equals("list")) {%>
                            <input type="radio" name="select_folios" value="list" id="select_folios_1" checked/>
                            <%} else {%>
                            <input type="radio" name="select_folios" value="list" id="select_folios_1"/>
                            <%}%>
                            Folios
                        </td>
                        <td class="tableHeaderSpecial">Desde
                            <select name="folio_ini" id="folio_ini">
                                <%
                                    for (Iterator<String> it = foliosList.iterator(); it.hasNext();) {
                                        String folio = it.next();
                                        if (desde.compareTo(folio) == 0) {
                                %>
                                <option value="<%=folio%>" selected><%=folio%></option>
                                <%
                                } else {
                                %>
                                <option value="<%=folio%>"><%=folio%></option>
                                <%
                                        }
                                    }
                                %>
                            </select>
                            Hasta
                            <select name="folio_fin" id="folio_fin">
                                <%
                                    for (Iterator<String> it = foliosList.iterator(); it.hasNext();) {
                                        String folio = it.next();
                                        if (hasta.compareTo(folio) == 0) {
                                %>
                                <option value="<%=folio%>" selected><%=folio%></option>
                                <%
                                } else {
                                %>
                                <option value="<%=folio%>"><%=folio%></option>
                                <%
                                        }
                                    }
                                %>
                            </select>
                        </td>
                        <td>
                            <input type="submit" name="buscar_asign" id="buscar_asign" value="Consultar" />
                        </td>
                    </tr>
                </table>
            </div>
        </form>

        <br/>
        <hr/>
        <br/>
        <div class="asignacion" id="asignacion" style="display:<%=displayAsignaciones%>">
            <table>
                <tr>
                    <td valign="top">
                        <table width="90%" border="1" class="tbCss1">
                            <tr class="tbHeader1">
                                <th scope="col">Folio</th>
                                <th scope="col">Nemot&eacute;cnico</th>
                                <th scope="col">Operaci&oacute;n</th>
                                <th scope="col">Unidades</th>
                                <th scope="col">Monto</th>
                                <th scope="col">Asignadas</th>                    
                            </tr>
                            <%
                                for (Iterator<RegisterUnidades> it = unidades.iterator(); it.hasNext();) {
                                    RegisterUnidades asg = it.next();
                                    if (asg.equals(asgAct)) {
                            %>
                            <tr class="tableCellContent">    
                                <form id="<%=asg.getFolio()%>_<%=asg.getTipoOperacion()%>" name="<%=asg.getFolio()%>" method="post" action="<%=request.getContextPath()%>/Reporte1064" targer="_top">
                                    <input type="hidden" id="id_registro" name="id_registro" value="<%=asg.getFolio()%>_<%=asg.getTipoOperacion()%>"/>
                                    <input type="hidden" id="cod_broker" name="cod_broker" value="<%=brokerSelected.getCodBroker()%>"/>
                                    <input type="hidden" id="tipo" name="tipo" value="asignacion"/>
                                    <input type="hidden" id="htmlFormName" name="htmlFormName" value="asignacion"/>
                                    <input type="hidden" id="modificada" name="modificada" value="<%=modificada%>"/>
                                    <td>
                                        <a href="#" onclick="document.getElementById('<%=asg.getFolio()%>_<%=asg.getTipoOperacion()%>').submit();"><%=asg.getFolio()%></a>
                                    </td>
                                    <td>
                                        <%=asg.getNemotecnico()%>
                                    </td>
                                    <td>
                                        <%=asg.getTipoOperacion()%>
                                    </td>
                                    <td>
                                        <%=formateador.format(asg.getNV() + asg.getVP() + asg.getVL() + asg.getAD() + asg.getNI())%>
                                    </td>
                                    <td>
                                        <%=formateador.format(asg.getMonto())%>
                                    </td>
                                    <%
                                        if (asg.isModificado()) {
                                    %>
                                    <td align="center"><img src="<%=request.getContextPath()%>/images/checkMark.gif" /></td>
                                    <%
                                    } else {
                                    %>
                                    <td></td>                            
                                    <%                                        }
                                    %>
                                </form>
                            </tr>
                            <%
                            } else {
                            %>
                            <tr class="tbSubHeader1">    
                                <form id="<%=asg.getFolio()%>_<%=asg.getTipoOperacion()%>" name="<%=asg.getFolio()%>" method="post" action="<%=request.getContextPath()%>/Reporte1064" targer="_top">
                                    <input type="hidden" id="id_registro" name="id_registro" value="<%=asg.getFolio()%>_<%=asg.getTipoOperacion()%>"/>
                                    <input type="hidden" id="cod_broker" name="cod_broker" value="<%=brokerSelected.getCodBroker()%>"/>
                                    <input type="hidden" id="tipo" name="tipo" value="asignacion"/>
                                    <input type="hidden" id="htmlFormName" name="htmlFormName" value="asignacion"/>
                                    <input type="hidden" id="modificada" name="modificada" value="<%=modificada%>"/>
                                    <td><a href="#" onclick="document.getElementById('<%=asg.getFolio()%>_<%=asg.getTipoOperacion()%>').submit();"><%=asg.getFolio()%></a></td>
                                    <td><%=asg.getNemotecnico()%></td>
                                    <td><%=asg.getTipoOperacion()%></td>
                                    <td><%=formateador.format(asg.getNV() + asg.getVP() + asg.getVL() + asg.getAD() + asg.getNI())%></td>
                                    <td><%=formateador.format(asg.getMonto())%></td>
                                    <% if (asg.isModificado()) {%>
                                    <td align="center"><img src="<%=request.getContextPath()%>/images/checkMark.gif" /></td>
                                    <%} else {%>
                                    <td></td>                            
                                    <%}%>
                                </form>
                            </tr>
                            <%
                                    }
                                }
                            %>
                        </table>
                    </td>
                    <td valign="top">
                        <form id="distribucion_unidades" name="distribucion_unidades" method="post" action="<%=request.getContextPath()%>/Reporte1064" targer="_top">
                            <input type="hidden" id="id_registro" name="id_registro" value="<%=asgAct.getFolio()%>_<%=asgAct.getTipoOperacion()%>"/>
                            <input type="hidden" id="folioDist" name="folioDist" value="<%=asgAct.getFolio()%>"/>
                            <input type="hidden" id="fecha" name="fecha" value="<%=asgAct.getFecha().getMillis()%>"/>
                            <input type="hidden" id="cod_broker" name="cod_broker" value="<%=brokerSelected.getCodBroker()%>"/>
                            <input type="hidden" id="tipo_operacion" name="tipo_operacion" value="<%=asgAct.getTipoOperacion()%>"/>
                            <input type="hidden" id="htmlFormName" name="htmlFormName" value="distribucion_unidades"/>
                            <input type="hidden" id="modificada" name="modificada" value="<%=modificada%>"/>
                            <table width="90%" border="0" class="tbCss1">
                                <tr>
                                    <td class="tableHeaderSpecial" colspan="3" align="center">Instrumento <%=asgAct.getNemotecnico()%> - Folio <%=asgAct.getFolio()%> - Operaci&oacute;n <%=asgAct.getTipoOperacion()%></td>
                                </tr>
                                <tr class="tbHeader1">
                                    <th scope="col">Tipo de Cliente</th>
                                    <th scope="col">Tipo Operaci&oacute;n</th>
                                    <th scope="col">Unidades</th>
                                </tr>
                                <tr class="tbSubHeader1">
                                    <td>Tercero</td>
                                    <td>No vinculado</td>
                                    <td>
                                        <%
                                            if (brokerSelected.getIs1064() == 1) {
                                        %>
                                        <input name="asgActNV" type="text" id="asgActNV" value="<%=formateador.format(asgAct.getNV())%>" onkeypress="return isNumberKey(event)" onchange="checkUnits()" onclick="this.select()" autocomplete="off"/>
                                        <%
                                        } else {
                                        %>
                                        <%=formateador.format(asgAct.getNV())%>
                                        <%
                                            }
                                        %>
                                    </td>
                                </tr>
                                <tr class="tbSubHeader1">
                                    <td>Tercero</td>
                                    <td>Vinculado por propiedad</td>
                                    <td>
                                        <%
                                            if (brokerSelected.getIs1064() == 1) {
                                        %>
                                        <input name="asgActVP" type="text" id="asgActVP" value="<%=formateador.format(asgAct.getVP())%>" onkeypress="return isNumberKey(event)" onchange="checkUnits()" onclick="this.select()" autocomplete="off"/>
                                        <%
                                        } else {
                                        %>
                                        <%=formateador.format(asgAct.getVP())%>
                                        <%
                                            }
                                        %>                                        
                                    </td>
                                </tr>
                                <tr class="tbSubHeader1">
                                    <td>Tercero</td>
                                    <td>Vinculado laboralmente</td>
                                    <td>
                                        <%
                                            if (brokerSelected.getIs1064() == 1) {
                                        %>
                                        <input name="asgActVL" type="text" id="asgActVL" value="<%=formateador.format(asgAct.getVL())%>" onkeypress="return isNumberKey(event)" onchange="checkUnits()" onclick="this.select()" autocomplete="off"/>
                                        <%
                                        } else {
                                        %>
                                        <%=formateador.format(asgAct.getVL())%>
                                        <%
                                            }
                                        %>                                                                                
                                    </td>
                                </tr>
                                <tr class="tbSubHeader1">
                                    <td>Tercero</td>
                                    <td>Vinculado por administraci&oacute;n</td>
                                    <td>
                                        <%
                                            if (brokerSelected.getIs1064() == 1) {
                                        %>
                                        <input name="asgActAD" type="text" id="asgActAD" value="<%=formateador.format(asgAct.getAD())%>" onkeypress="return isNumberKey(event)" onchange="checkUnits()" onclick="this.select()" autocomplete="off"/>
                                        <%
                                        } else {
                                        %>
                                        <%=formateador.format(asgAct.getAD())%>
                                        <%
                                            }
                                        %>                                                                                
                                    </td>
                                </tr>
                                <tr class="tbSubHeader1">
                                    <td>Propio</td>
                                    <td>No corresponde informar</td>
                                    <td>
                                        <%
                                            if (brokerSelected.getIs1064() == 1) {
                                        %>
                                        <input name="asgActNI" type="text" id="asgActNI" value="<%=formateador.format(asgAct.getNI())%>" onkeypress="return isNumberKey(event)" onchange="checkUnits()" onclick="this.select()" autocomplete="off"/>
                                        <%
                                        } else {
                                        %>
                                        <%=formateador.format(asgAct.getNI())%>
                                        <%
                                            }
                                        %>                                                                                
                                    </td>
                                </tr>
                                <%if (brokerSelected.getIs1064() == 1) {%>
                                <tr class="tbSubHeader1">
                                    <td>Unidades no asignadas</td>
                                    <td></td>
                                    <td>
                                        <input name="asgDiff" type="text" id="asgDiff" value="0" readonly/>                            
                                    </td>
                                </tr>
                                <%                                    }
                                %>                                                                                
                            </table>
                            <%if (brokerSelected.getIs1064() == 1) {%>
                            <input type="button" name="cambios_unidades" id="cambios_unidades" value="Aplicar cambios" onclick="aplicarCambios()"/>
                            <%                                }
                            %>                                                                                
                        </form>
                    </td>
                </tr>         
            </table>
        </div>        
        <%
        } else {
        %>
        <br/>
        <br/>
        <h1>
            No hay datos
        </h1>
        <% }
        } else if (tipo.equals("carga")) {
        %>
        <div class="carga" id="carga" style="display:<%=displayCargas%>">
            <table width="90%" border="1" class="tbCss1">
                <tr>
                    <th colspan="4" scope="col">Historial de archivos</th>
                </tr>
                <tr class="tbHeader1">
                    <th scope="col">Hora</th>
                    <th scope="col">Usuario</th>
                    <th scope="col">Nombre del archivo</th>
                    <th scope="col">Detalles</th>
                </tr>
                <%
                    for (Iterator<Intranet1064Log> it = logAsignaciones.iterator(); it.hasNext();) {
                %>
                <tr class="tbSubHeader1">    
                    <%
                        Intranet1064Log log = it.next();
                        String[] split = log.getNoMarzoivo().split("[/\\\\]");
                    %>
                    <td><%=log.getFecha().toString(sdfh)%></td>
                    <td><%=log.getUsuario()%></td>
                    <td><%=split[split.length - 1]%></td>
                    <td><%=log.getDetalle()%></td>
                </tr>
                <%
                    }
                %>
            </table>
            <form id="ingreso_archivo" name="ingreso_archivo" method="post" action="<%=request.getContextPath()%>/Reporte1064" enctype="multipart/form-data" targer="_top">
                <input type="hidden" id="cod_broker" name="cod_broker" value="<%=brokerSelected.getCodBroker()%>"/>
                <input type="hidden" id="tipo" name="tipo" value="<%=tipo%>"/>
                <input type="hidden" id="modificada" name="modificada" value="<%=modificada%>"/>
                <input type="hidden" id="htmlFormName" name="htmlFormName" value="ingreso_archivo"/>
                <table width="90%" border="1">
                    <tr>
                        <th colspan="3" align="left" valign="top" class="tableHeaderSpecial" scope="col">
                            Subir archivo&nbsp;
                        </th>
                    </tr>
                    <tr>
                        <td width="20%" align="right" valign="top" scope="col" class="tableHeaderSpecial">Tipo de archivo</td>
                        <td width="20%" align="left" valign="top" scope="col">
                            <select name="tipo_archivo" id="tipo_archivo">
                                <option value="palos">Palos</option>
                                <option value="cieabi">CieAbi</option>
                            </select>
                        </td>
                        <td width="60%" align="left" valign="top" scope="col">
                            <input name="arch_carga" type="file" id="arch_carga" />
                        </td>
                        <td>
                            <input type="submit" name="subir_file" id="subir_file" value="Subir"/>
                        </td>
                    </tr>
                </table>
            </form>
        </div>
        <%
            }
        %>
    </body>
</html>
