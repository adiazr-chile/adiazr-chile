<%@page import="cl.femase.gestionweb.common.Constantes"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.Calendar"%>
<%@page import="cl.femase.gestionweb.vo.UsuarioVO"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%
    UsuarioVO userInSession = (UsuarioVO)session.getAttribute("usuarioObj");
    String mainPage = "quick_menu/quick_menu.jsp";//por defecto
    
    Calendar calHoy = Calendar.getInstance(new Locale("es","CL"));
    Date fechaActual = calHoy.getTime();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String strFechaActual = sdf.format(fechaActual);
    
    System.out.println("[homepage.jsp]"
        + "Fecha actual: " + strFechaActual
        + ", Usuario: " + userInSession.getUsername() 
        + ", marca virtual? " + userInSession.getMarcacionVirtual());
    LinkedHashMap<String, String> fechasMarcacionVirtual = userInSession.getFechasMarcacionVirtual();
    
    if (userInSession.getIdPerfil()==Constantes.ID_PERFIL_DIRECTOR 
            || userInSession.getIdPerfil()==Constantes.ID_PERFIL_DIRECTOR_TR
            || userInSession.getIdPerfil()==Constantes.ID_PERFIL_DIRECTOR_GENERAL){
                mainPage = "vistas/contratos_caducados.jsp";
    }else if (userInSession.getMarcacionVirtual().compareTo("S") == 0 
            && !fechasMarcacionVirtual.isEmpty()){
        System.out.println("[homepage.jsp]"
            + "Hay fechas definidas para marcacion virtual...");
        if (fechasMarcacionVirtual.containsKey(strFechaActual)){
            System.out.println("[homepage.jsp]"
                + "La fecha actual " + strFechaActual 
                +" es valida para realizar marcacion virtual.");
            mainPage = "marcacion_virtual/ingresa_marca_virtual.jsp";
        }else{
            System.out.println("[homepage.jsp]"
                + "La fecha actual " + strFechaActual 
                +" no esta dentro de las fechas asignadas para marcacion virtual.");
        }
    }
%>    
<head>
    <link rel="icon" href="images/femase_fav_icon_16x16.gif" type="image/gif" sizes="16x16"></link>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>FEMASE - Gestion de Asistencia</title>
</head>
<frameset rows="110,*" cols="*" frameborder="no" border="0" framespacing="0">
  <frame src="topFrame.jsp" name="topFrame" scrolling="No" noresize="noresize" id="topFrame" title="topFrame" />
  <frameset rows="*" cols="222,*" framespacing="0" frameborder="no" border="0">
      <frame src="menu_lateral/main_menu.jsp" name="leftFrame" scrolling="yes" noresize="noresize" id="leftFrame" title="leftFrame" />
        <!--<frame src="graficos/dashboard.jsp" name="mainFrame" id="mainFrame" title="mainFrame" />-->
        <frame src="<%=mainPage%>" name="mainFrame" id="mainFrame" title="mainFrame" />
	<!--<frame src="bootstrap-slider.html" name="mainFrame" id="mainFrame" title="mainFrame" />-->
  </frameset>
</frameset>
<noframes><body>
</body></noframes>
</html>
