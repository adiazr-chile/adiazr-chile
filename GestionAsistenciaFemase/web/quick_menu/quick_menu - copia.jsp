<%@page import="java.text.SimpleDateFormat"%>
<%@page import="cl.femase.gestionweb.vo.UsuarioVO"%>
<%@page import="cl.femase.gestionweb.vo.ModuloSistemaVO"%>
<%@page import="java.util.Iterator"%>
<%@page import="cl.femase.gestionweb.vo.ModuloAccesoPerfilVO"%>
<%@page import="java.util.LinkedHashMap"%>

<%
    UsuarioVO theUser = (UsuarioVO) session.getAttribute("usuarioObj");
    SimpleDateFormat sdfhora = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    java.util.Date ahora = new java.util.Date();
    LinkedHashMap<String, ModuloSistemaVO> modulosSistema = 
        (LinkedHashMap<String, ModuloSistemaVO>)session.getAttribute("modulosSistema");
            
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html class="csstransforms no-csstransforms3d csstransitions"><head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
	<title>Menu de accesos rapidos</title>
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/quick_menu/css/font-awesome.css">
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/quick_menu/css/menu.css">
    
	<script type="text/javascript" src="<%=request.getContextPath()%>/quick_menu/js/jquery.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/quick_menu/js/function.js"></script>

</head>
<body>
<div style="background:#0099cc; font-size:22px; text-align:center; color:#FFF; font-weight:bold; height:50px; padding-top:50px;"></div>
<div id="wrap">
    <header>
        <div class="inner relative">
            <a id="menu-toggle" class="button dark" href="#"><i class="icon-reorder"></i></a>
            <nav id="navigation">
                <ul id="main-menu"><!-- main menu -->
                    <!-- itera modulos -->
                    <%
                    LinkedHashMap<String, LinkedHashMap<String, ModuloAccesoPerfilVO>> accesosModulo = theUser.getAccesosModulo();
                    String keyModuleId;
                    String moduleTitulo;
                    String moduleSubTitulo;
                    String moduleImagen;

                    Iterator<ModuloSistemaVO> itModulos = modulosSistema.values().iterator();
                    while (itModulos.hasNext())
                    {
                        ModuloSistemaVO currentModulo = itModulos.next();
                        LinkedHashMap<String, ModuloAccesoPerfilVO> listaAccesos 
                                = accesosModulo.get(""+currentModulo.getModulo_id());
                        moduleTitulo = currentModulo.getTitulo();
                        moduleSubTitulo = currentModulo.getSubTitulo();
                        moduleImagen = currentModulo.getImagen();
                        if (currentModulo.getAccesoRapido().compareTo("S")==0)
                        {
                        %>
                            <li class="parent">
                                <a href="javascript:;"><%=moduleTitulo%></a>
                                <!-- itera accesos urls-->
                                <ul class="sub-menu">
                                <%
                                String thetarget="_self";
                                Iterator<ModuloAccesoPerfilVO> actionsIterator = listaAccesos.values().iterator();
                                while (actionsIterator.hasNext()) {
                                    ModuloAccesoPerfilVO theaction = actionsIterator.next();
                                    if (theaction.getAccesoUrl() != null) {
                                        if (theaction.getAccesoNombre().compareTo("endsession") == 0){
                                            thetarget="_parent";
                                        }%>
                                            <li><a href="<%=request.getContextPath()%>/<%=theaction.getAccesoUrl()%>" target="<%=thetarget%>">
                                            <%=theaction.getAccesoLabel()%></a>
                                            </li>
                                    <%}%>
                                <%}//fin while accesos%>	
                                </ul><!-- fin iteracion de accesos -->
                            </li><!-- fin modulo -->
                        <%}
                    }//fin while modulos%>
                </ul>
            </nav>
        <div class="clear"></div>
        </div>
    </header>	
</div>    
</body></html>