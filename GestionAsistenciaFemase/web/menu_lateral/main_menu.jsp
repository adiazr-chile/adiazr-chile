<%@page import="cl.femase.gestionweb.vo.ModuloAccesoPerfilVO"%>
<%@page import="cl.femase.gestionweb.vo.ModuloSistemaVO"%>
<%@page import="cl.femase.gestionweb.vo.UsuarioVO"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.text.SimpleDateFormat"%>

<%
    UsuarioVO theUser = (UsuarioVO) session.getAttribute("usuarioObj");
    boolean tieneTurnoRotativo = (Boolean)session.getAttribute("tieneTurnoRotativo");
    LinkedHashMap<String, ModuloSistemaVO> modulosSistema = 
        (LinkedHashMap<String, ModuloSistemaVO>)session.getAttribute("modulosSistema");
    System.out.println("[GestionFemaseWeb]main_menu.jsp]tieneTurnoRotativo? " + tieneTurnoRotativo);    
%>
        
<!doctype html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Sistema Gestion Web-FEMASE</title>
    <link rel="stylesheet" href="css/style.css">
    <!--<link href="font_awesome/all.css" rel="stylesheet">-->
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.7.2/css/all.css" integrity="sha384-fnmOCqbTlWIlj8LyTjo7mOUStjsKC4pOpQbqyi7RrhN7udi9RwhKkMHpvLbHG9Sr" crossorigin="anonymous">
    <script src="js/jquery.min.js"></script>
    <script>
        $("a").mouseover(
            function()
            {
            window.status = "";
            }
        );
      
    </script>
    
    <style>
	a {
	background-color: #195598;
	display: block;
	font-size: 12px;
	color: #dee8ef;
	text-decoration: none;
	  }
	body,td,th {
	font-family: Montserrat, sans-serif;
}
body {
	background-color: #195598;
}
    </style>
	
<meta charset="utf-8">
</head>
<body>
    <ul class="mainmenu">
        
            <%
                LinkedHashMap<String, LinkedHashMap<String, ModuloAccesoPerfilVO>> accesosModulo = theUser.getAccesosModulo();
                String keyModuleId;
                String moduleName;
                String moduleFontIcon;
                Iterator<ModuloSistemaVO> itModulos = modulosSistema.values().iterator();
                while (itModulos.hasNext())
                {
                        ModuloSistemaVO currentModulo = itModulos.next();
                        LinkedHashMap<String, ModuloAccesoPerfilVO> listaAccesos 
                            = accesosModulo.get(""+currentModulo.getModulo_id());

                        moduleName      = currentModulo.getModulo_nombre();
                        moduleFontIcon  = currentModulo.getIconId();
                        
                        /**
                        * Si el usuario tiene turno rotativo, 
                        * no se debe mostrar el menu de Permisos Administrativos 
                        */
                        int idxPA = moduleName.indexOf("(PA)");
                        boolean mostrarModulo = true;
                        if (idxPA >= 0 && tieneTurnoRotativo){
                            mostrarModulo = false;
                        }
                    %>
                    <%if (mostrarModulo){%>
                            <!-- generar cabecera menu-->
                            <li>
                                <span><i class="<%=moduleFontIcon%>" aria-hidden="true"></i>&nbsp;<%=moduleName%></span>
                            </li>
                            <!-- generar menu items-->
                            <ul class="submenu">
                            <div class="expand-triangle"><img src="images/expand.png"></div>
                            <%//iterando acciones

                                String thetarget="mainFrame";
                                for ( Map.Entry<String, ModuloAccesoPerfilVO> entry : listaAccesos.entrySet() ){
                                    ModuloAccesoPerfilVO theaction = entry.getValue();
                                    thetarget="mainFrame";
                                    if (theaction.getAccesoUrl() != null) {
                                        if (theaction.getAccesoNombre().compareTo("endsession") == 0){
                                            thetarget="_parent";
                                        }%>
                                        <li>
                                            <a href="<%=request.getContextPath()%>/<%=theaction.getAccesoUrl()%>" 
                                               target="<%=thetarget%>" 
                                                title="<%=theaction.getAccesoLabel()%>">
                                                    <span><%=theaction.getAccesoLabel()%></span>
                                            </a>
                                        </li>
                                    <%}%>
                                <%}//fin iteracion de acciones x modulo del menu %>
                            </ul>
                       <%}%>     
                <%}//fin iteracion de modulos%>
        
	</ul><!-- fin div mainmenu-->
</body>
<script src="js/script.js"></script>
<script src="js/retina.min.js"></script>
</html>