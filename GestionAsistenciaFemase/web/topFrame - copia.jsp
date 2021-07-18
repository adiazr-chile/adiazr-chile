<%@page import="cl.femase.gestionweb.common.Constantes"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="cl.femase.gestionweb.vo.UsuarioVO"%>

<%
    String version="3.8.7";
    String appAnios = "2017-2020";
    
    UsuarioVO userInSession = (UsuarioVO)session.getAttribute("usuarioObj");
    int numSolicitudesPendientes = 0;
    if (userInSession.getIdPerfil() == Constantes.ID_PERFIL_DIRECTOR){
        numSolicitudesPendientes = (Integer)session.getAttribute("solicitudes_pendientes");
    }
    
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Top</title>
    <meta http-equiv="Content-Language" content="English" />
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <!--<link rel="stylesheet" type="text/css" href="style/style.css" media="screen" />-->
   
	<style type="text/css">
        <!--
        .notification {
            background-color: #0079dc;
            color: white;
            text-decoration: none;
            padding: 0px 26px;
            position: relative;
            display: inline-block;
            border-radius: 2px;
          }

          .notification:hover {
            background: #8080FF;
          }

          .notification .badge {
            position: absolute;
            top: -10px;
            right: -10px;
            padding: 5px 10px;
            border-radius: 50%;
            background-color: red;
            color: white;
          }
body {
	margin-top: 1px;
	margin-bottom: 1px;
}
        -->
    </style>

</head>
<%
    UsuarioVO theUser	= (UsuarioVO)session.getAttribute("usuarioObj");
    String marketStatus	= (String)session.getAttribute("marketStatus");
    String logoEmpresa  = theUser.getEmpresaLogo();
    System.out.println("Logo empresa del usuario: "+logoEmpresa);
    SimpleDateFormat sdfhora	= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    java.util.Date ahora	= new java.util.Date();
%>
<body topmargin="1" marginheight="1">
<table width="100%" border="0" cellpadding="0" cellspacing="2">
  <tr>
    <td width="18%" align="center"><h1>
            <img src="images/<%=logoEmpresa%>" alt="" /></h1></td>
    <td width="43%" align="center">&nbsp;</td>
    <td width="39%" ><img src="images/logo_femase_2020.png">v<%=version%>. By FEMASE&copy; <%=appAnios%>
      </td>
</tr>
</table>


<table width="100%" border="0" cellspacing="0" cellpadding="0" bgcolor="#483933">
  <tr>
    <td width="152" align="center"><img src="images/icon_home.gif" alt="Home" width="13" height="12"  border="0"/>
    <a href="<%=request.getContextPath()%>/quick_menu/quick_menu.jsp" target="mainFrame" title="Acceso rapido">Acceso r&aacute;pido</a></td>
    <td width="33" align="center">&nbsp;</td>
    <td width="304" align="left" >Bienvenido&nbsp;<%=theUser.getNombreCompleto()%>,&nbsp;<%=theUser.getNomPerfil()%></td>
    <td width="320" align="left" >&nbsp;
        <%if (userInSession.getIdPerfil() == Constantes.ID_PERFIL_DIRECTOR){%>
            <%if (numSolicitudesPendientes > 0){%>
                <a href="<%=request.getContextPath()%>/vacaciones/sol_vacaciones_aprobar_rechazar.jsp" 
                   class="notification" target="mainFrame">
                  <span>Solicitudes Vacaciones Pendientes</span>
                  <span class="badge"><%=numSolicitudesPendientes%></span>
                </a>
            <%}%>
        <%}%>
    </td>
    <td width="311" align="left">Ingreso: <%=sdfhora.format(ahora)%></td>
    <td width="122" align="left">&nbsp;</td>
<td width="477" align="left"><img src="images/icon_logout.gif" width="16" height="14"  border="0"/><a href="<%=request.getContextPath()%>/UserLogout" target="_parent" title="Salir">Desconectarse [&nbsp;<%=theUser.getUsername()%>&nbsp;]</a></td>
  </tr>
</table>


</body>
</html>