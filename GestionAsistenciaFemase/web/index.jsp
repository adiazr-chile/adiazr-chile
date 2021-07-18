<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <%
        String mensaje = (String) session.getAttribute("mensaje");

    %>
    <head>

        <title>Femase - Gestion WEB</title>
        <meta http-equiv="Content-Language" content="English" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <link rel="stylesheet" type="text/css" href="style/style.css" media="screen" />
        <script>
            location.replace("<%=request.getContextPath()%>/newLogin/index.jsp");
            //window.location.replace('<%=request.getContextPath()%>/Login_v18/index.jsp');
        </script>
    </head>
    <body>

        <div id="wrapLogin">
            <div id="headerLogin">
                <table width="100%" border="0">
                    <tr>
                        <td width="46%">&nbsp;</td>
                        <td width="54%">
                            <form id="form1" method="post" action="<%=request.getContextPath()%>/UserAuth">
                                <table width="281" border="0" align="center">
                                    <tr>
                                        <td width="83" rowspan="3" class="tableHeader">&nbsp;</td>
                                        <td width="83" class="tableHeader">Usuario</td>
                                        <td width="101"><label>
                                                <input name="username" type="text" id="username" size="20" maxlength="15" />
                                            </label></td>
                                    </tr>
                                    <tr>
                                        <td class="tableHeader">Clave</td>
                                        <td><label>
                                                <input name="password" type="password" id="password" size="12" maxlength="12" />
                                            </label></td>
                                    </tr>
                                    <tr>
                                        <td>&nbsp;</td>
                                        <td>
                                            <input type="submit" name="boton" id="boton" value="Aceptar" />     </td>
                                    </tr>
                                </table>
                            </form>
                        </td>
                    </tr>
                </table>
                <p>&nbsp;</p>
            </div>
            <div id="content">
                <div class="center"> 

                    <h2><img src="images/header_gestion_femase.png" alt="" width="629" height="86" /></h2>
                </div>
                <div style="clear: both;"> </div>

            </div>

            <div id="footer">
                  Versi&oacute;n 1.6.1. Designed by <a href="http://www.femase.cl">FEMASE</a>&copy; 2017-2018</div>
        </div>

    </body>

</html>

<script type="text/javascript">
    <%if (mensaje != null) {%>
    alert('<%=mensaje%>');
    <%}%>
</script>