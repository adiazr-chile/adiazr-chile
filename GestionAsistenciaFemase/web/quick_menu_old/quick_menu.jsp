<%@page import="java.text.SimpleDateFormat"%>
<%@page import="cl.femase.gestionweb.vo.UsuarioVO"%>
<%@page import="cl.femase.gestionweb.vo.ModuloSistemaVO"%>
<%@page import="java.util.Iterator"%>
<%@page import="cl.femase.gestionweb.vo.ModuloAccesoPerfilVO"%>
<%@page import="java.util.LinkedHashMap"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%
    UsuarioVO theUser = (UsuarioVO) session.getAttribute("usuarioObj");
    SimpleDateFormat sdfhora = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    java.util.Date ahora = new java.util.Date();
    LinkedHashMap<String, ModuloSistemaVO> modulosSistema = 
        (LinkedHashMap<String, ModuloSistemaVO>)session.getAttribute("modulosSistema");
            
%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Menu rapido</title>

<style>

html {
  height: 100%;
}
body {
  background: 
    -webkit-linear-gradient(rgba(5, 610, 255, 0.4), rgba(135, 60, 255, 0.0) 80%),
    -webkit-linear-gradient(-45deg, rgba(120, 155, 255, 0.9) 25%, rgba(255, 160, 65, 0.9) 76%);
}
.cf:before, .cf:after {
  content:" ";
  display: table;
}
.cf:after {
  clear: both;
}
.cf {
  *zoom: 1;
}
.menu {
  list-style:none;
  margin: 10px auto;
  width: 800px;
  width: -moz-fit-content;
  width: -webkit-fit-content;
  width: fit-content;
}
.menu > li {
  background: #6699CC;
  float: left;
  position: relative;
  /* -webkit-transform: skewX(25deg);*/
}
.menu a {
  display: block;
  color: #000;
  /*text-transform: uppercase;*/
  text-decoration: none;
  font-family: Arial, Helvetica;
  font-size: 14px;
}
.menu li:hover {
  background: #CCCCCC;
}
.menu > li > a {
  /*-webkit-transform: skewX(-25deg);*/
  padding: 0.9em 1em;
}
/* Dropdown */
.submenu {
  position: absolute;
  width: 200px;
  left: 50%;
  margin-left: -100px;
  /*-webkit-transform: skewX(-25deg);*/
  -webkit-transform-origin: left top;
}
.submenu li {
  background-color: #3399CC;
  position: relative;
  overflow: hidden;
}
.submenu > li > a {
  padding: 1em 2em;
}
.submenu > li::after {
  content:'';
  position: absolute;
  top: -125%;
  height: 100%;
  width: 100%;
  box-shadow: 0 0 50px rgba(0, 0, 0, .9);
}
/* Odd stuff */
.submenu > li:nth-child(odd) {
  /*-webkit-transform: skewX(-25deg) translateX(0);*/
}
.submenu > li:nth-child(odd) > a {
 /* -webkit-transform: skewX(25deg);*/
}
.submenu > li:nth-child(odd)::after {
  right: -50%;
  /*-webkit-transform: skewX(-25deg) rotate(3deg);*/
}
/* Even stuff */
.submenu > li:nth-child(even) {
  /*-webkit-transform: skewX(25deg) translateX(0);*/
}
.submenu > li:nth-child(even) > a {
  /*-webkit-transform: skewX(-25deg);*/
}
.submenu > li:nth-child(even)::after {
  left: -50%;
  /*-webkit-transform: skewX(25deg) rotate(3deg);*/
}
/* Show dropdown */
.submenu, .submenu li {
  opacity: 0;
  visibility: hidden;
}
.submenu li {
  transition: .2s ease -webkit-transform;
}
.menu > li:hover .submenu, .menu > li:hover .submenu li {
  opacity: 1;
  visibility: visible;
}
.menu > li:hover .submenu li:nth-child(even) {
  /*-webkit-transform: skewX(25deg) translateX(15px);*/
}
.menu > li:hover .submenu li:nth-child(odd) {
  /*-webkit-transform: skewX(-25deg) translateX(-15px);*/
}

</style>

</head>

<body>
<ul class="menu cf">
    
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
        {%>  
            <li><!-- inicio li del modulo -->
                <a href="#"><%=moduleTitulo%></a>
                <ul class="submenu">
                <%
                    String thetarget="_self";
                    Iterator<ModuloAccesoPerfilVO> actionsIterator = listaAccesos.values().iterator();
                    while (actionsIterator.hasNext()) {
                        ModuloAccesoPerfilVO theaction = actionsIterator.next();
                        if (theaction.getAccesoUrl() != null) {
                            if (theaction.getAccesoNombre().compareTo("endsession") == 0){
                                thetarget="_parent";
                            }%>    
                                <li><a href="<%=request.getContextPath()%>/<%=theaction.getAccesoUrl()%>" 
                                        target="<%=thetarget%>" 
                                        title="<%=theaction.getAccesoLabel()%>">
                                          <%=theaction.getAccesoLabel()%></a>
                                </li><!-- fin itera accion -->
                        <%}
                    }//fin iteracion de accesos %>        
                </ul><!-- ul fin submenu modulo -->
                </li><!-- fin li modulo -->
            <%}//fin if que consulta si es un acceso rapido%>
            
    <%}//fin iteracion de modulos%>
    
</ul><!-- fin ul principal -->
</body>
</html>
