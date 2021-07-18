<%@ page import="java.util.Iterator"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="cl.bolchile.portalinf.vo.UsuarioVO"%>
<%@ page import="cl.bolchile.portalinf.vo.ModuleActionVO"%>
<%@ page import="java.util.HashMap"%>

<% //ArrayList<TipoInstrumentoVO> tiposIns	= (ArrayList<TipoInstrumentoVO>)session.getAttribute("tiposIns");
    UsuarioVO theUser = (UsuarioVO) session.getAttribute("usuarioObj");
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    java.util.Date ahora = new java.util.Date();

%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <title>Untitled Document</title>

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

                $("#the_fecha_inicial").datepicker("option", "dateFormat", "dd/mm/yy");
                $("#the_fecha_final").datepicker("option", "dateFormat", "dd/mm/yy");

                document.form1.the_fecha_inicial.value = '<%=sdf.format(ahora)%>';
                document.form1.the_fecha_final.value = '<%=sdf.format(ahora)%>';

            });

            function inicio() {
                filtros1.style.visibility = 'hidden';

            }

            function enviarDatos() {
                document.form1.submit();
            }

            function showFilters(itemSelected) {

                filtros1.style.visibility = 'visible';

            }

        </script>
        <link rel="stylesheet" type="text/css" href="../style/style.css" media="screen" />
        <style type="text/css">
            <!--
            body {
                background-color: #FFF;
            }

        </style></head>



    <body onLoad="inicio();">
        <div id="menu">
            <div id="content">
                <div class="menu">

                    <form id="form1" name="form1" method="post" action="<%=request.getContextPath()%>/RptCRMensual" target="mainFrame2">
                        <table class="menu" width="100%" border="0" align="center" cellpadding="2" cellspacing="2">
                            <tr>
                                <td height="37" align="right" valign="top" class="textOnly">Reportes - Mensuales</td>
                                <td align="right" valign="middle" class="tableHeaderSpecial">Fecha inicial (dd/MM/yyyy)
                                    &nbsp;&nbsp;</td>
                                <td align="left" valign="middle"><input name="the_fecha_inicial" type="text" id="the_fecha_inicial" size="12" maxlength="10" value="<%=sdf.format(ahora)%>"/></td>
                                <td colspan="2" align="right" valign="middle" class="tableHeaderSpecial">Fecha Final (dd/MM/yyyy)&nbsp;</td>
                                <td valign="middle"><input name="the_fecha_final" type="text" id="the_fecha_final" size="12" maxlength="10" value="<%=sdf.format(ahora)%>"/></td>
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
                                                /*
                                                 System.out.println("\nIterando acciones, para el modulo: " +keyModuleId);
                                                 System.out.println("Action.code     : " + theaction.getCode());
                                                 System.out.println("Action.name     : " + theaction.getName());
                                                 System.out.println("Action.Category : " + theaction.getCategory());
                                                 System.out.println("Action.url      : " + theaction.getUrl());
                                                 * */
                                                if (theaction.getUrl() == null
                                                        && theaction.getCategory() != null
                                && theaction.getCategory().compareTo("mensual") == 0) {%>
                                        <option value="<%=theaction.getCode()%>"><%=theaction.getName()%></option>
                                        <%}%>
                                        <%}//fin while (actionsIterator.hasNext()) %>



                                        <!--<option value="gestion-acc">Gestion Acciones</option>
                                        <option value="gestion-irf">Gestion Renta Fija</option>
                                        <option value="gestion-iif">Gestion Intermediacion</option>
                                        <option value="participacion_sitrel">Participacion corredores SITREL</option>
                                        <option value="participacion_xstream">Participacion corredores XSTREAM</option>
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
                                                <td width="24%" align="right">&nbsp;</td>
                                                <td width="15%" align="left">&nbsp;</td>
                                                <td width="20%" align="right">&nbsp;</td>
                                                <td width="41%">&nbsp;</td>
                                            </tr>
                                        </table>
                                    </div>        

                                    <!-- FIN DIV CON FILTROS PARA PRENSA AM Y OTROS-->        
                                    <!-- DIV CON FILTRO FECHA DE PROCESO--></td>
                            </tr>
                        </table>
                    </form>

                </div>
                <div style="clear: both;"></div>
            </div>
        </div>
    </body>
</html>
