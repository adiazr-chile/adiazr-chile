<%@page import="cl.femase.gestionweb.vo.ModuloSistemaVO"%>
<%@page import="java.util.LinkedHashMap"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="cl.femase.gestionweb.vo.UsuarioVO"%>
<%@ page import="cl.femase.gestionweb.vo.ModuloAccesoPerfilVO"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>

<!doctype html>
<html lang="en">
    <head>
        <meta charset="utf-8" />
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <title>Menu</title>
        <!--<link rel="stylesheet" type="text/css" href="style/style.css" media="screen" />-->
        <link rel="stylesheet" href="<%=request.getContextPath()%>/style/jquery-redmond/jquery-ui-1.9.2.custom.css" />
        <script src="<%=request.getContextPath()%>/js/jquery-1.9.1.js"></script>
        <script src="<%=request.getContextPath()%>/js/jquery-ui.js"></script>

        <style type="text/css">
            <!--
            body {
                background-color: #FFF;
                font-family: "Trebuchet MS", "Helvetica", "Arial",  "Verdana", "sans-serif";
                font-size: 72.5%;
            }
            -->
        </style>

        <%
            UsuarioVO theUser = (UsuarioVO) session.getAttribute("usuarioObj");
            SimpleDateFormat sdfhora = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            java.util.Date ahora = new java.util.Date();
            LinkedHashMap<String, ModuloSistemaVO> modulosSistema = 
                    (LinkedHashMap<String, ModuloSistemaVO>)session.getAttribute("modulosSistema");
            
        %>
        <script language="javascript">
            $(function() {
                $("#accordion").accordion({
                    heightStyle: "content"
                });
            });

            
        </script>
    </head>
    <body>

        <div id="accordion">
            <%
                LinkedHashMap<String, LinkedHashMap<String, ModuloAccesoPerfilVO>> accesosModulo = theUser.getAccesosModulo();
                String keyModuleId;
                String moduleName;
                
                Iterator<ModuloSistemaVO> itModulos = modulosSistema.values().iterator();
                while (itModulos.hasNext())
                {
                    ModuloSistemaVO currentModulo = itModulos.next();
                    //System.out.println("[menuFrame.jsp]get acciones para modulo: "+currentModulo.getModulo_id());
                    LinkedHashMap<String, ModuloAccesoPerfilVO> listaAccesos 
                        = accesosModulo.get(""+currentModulo.getModulo_id());
                    /*
                    System.out.println("[menuFrame.jsp]"
                        + "total acciones para modulo: "+currentModulo.getModulo_id()
                        +", size= "+listaAccesos.size());*/
                    //actions = appModules.get(keyModuleId);//lista con acciones
                    moduleName = currentModulo.getModulo_nombre();%>
                    <%if (listaAccesos.size() > 0){%>
                        <h3><%=moduleName%></h3>
                    <%}%>

            <!-- Listar acciones(accesos) de cada modulo-->
            <div>
                <!--<ul>-->
                <%//iterando acciones (accesos)
                    String thetarget="mainFrame";
                    Iterator<ModuloAccesoPerfilVO> actionsIterator = listaAccesos.values().iterator();
                    while (actionsIterator.hasNext()) {
                        ModuloAccesoPerfilVO theaction = actionsIterator.next();
                        /* System.out.println("[menuFrame.jsp]"
                        + "mostrar accion: "+theaction.getAccesoLabel()
                        +",url: "+theaction.getAccesoUrl());
                        */
                        thetarget="mainFrame";
                        if (theaction.getAccesoUrl() != null) {
                            if (theaction.getAccesoNombre().compareTo("endsession") == 0){
                                thetarget="_parent";
                            }%>
                           <a href="<%=request.getContextPath()%><%=theaction.getAccesoUrl()%>" target="<%=thetarget%>" 
                            title="<%=theaction.getAccesoLabel()%>" title="<%=theaction.getAccesoLabel()%>"><%=theaction.getAccesoLabel()%>
                           </a>
                           <br>
                        <%}%>
                <%  }//fin while (actionsIterator.hasNext()) %>
                <!--</ul>-->
            </div>
            <%}//fin while (iterator.hasNext())%>

        </div>
    </body>
</html>