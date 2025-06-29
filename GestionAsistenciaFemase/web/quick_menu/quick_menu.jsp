<%@page import="java.text.SimpleDateFormat"%>
<%@page import="cl.femase.gestionweb.vo.UsuarioVO"%>
<%@page import="cl.femase.gestionweb.vo.ModuloSistemaVO"%>
<%@page import="java.util.Iterator"%>
<%@page import="cl.femase.gestionweb.vo.ModuloAccesoPerfilVO"%>
<%@page import="java.util.LinkedHashMap"%>

<%
    UsuarioVO theUser = (UsuarioVO) session.getAttribute("usuarioObj");
    boolean tieneTurnoRotativo = (Boolean)session.getAttribute("tieneTurnoRotativo");
    SimpleDateFormat sdfhora = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    java.util.Date ahora = new java.util.Date();
    LinkedHashMap<String, ModuloSistemaVO> modulosSistema = 
        (LinkedHashMap<String, ModuloSistemaVO>)session.getAttribute("modulosSistema");
    System.out.println("[GestionFemaseWeb]quick_menu.jsp]tieneTurnoRotativo? " + tieneTurnoRotativo);        
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

<title>Quick menu</title>



    

<style>
	/* A simple, css only, (some-what) responsive menu */

    body { 
      background: #ccc;
      font-family: helvetica, arial, serif;
      font-size: smaller;
      text-transform: uppercase;
      text-align: center;
    }

    .wrap {
      display: inline-block;
      -webkit-box-shadow: 0 0 70px #fff;
      -moz-box-shadow: 0 0 70px #fff;
      box-shadow: 0 0 70px #fff;
      margin-top: 40px;
    }

    /* a little "umph" */
    .decor {
      background: #6EAF8D;
      background: -webkit-linear-gradient(left, #3470be 50%, #3470be 50%);
      background: -moz-linear-gradient(left, #CDEBDB 50%, #6EAF8D 50%);
      background: -o-linear-gradient(left, #CDEBDB 50%, #6EAF8D 50%);
      background: linear-gradient(left, white 50%, #6EAF8D 50%);
      background-size: 50px 25%;;
      padding: 2px;
      display: block;
    }

    a {
      text-decoration: none;
      color: #fff;
      display: block;
    }

    ul {
      list-style: none;
      position: relative;
      text-align: left;
    }

    li {
      float: left;
    }

    /* clear'n floats */
    ul:after {
      clear: both;
    }

    ul:before,
    ul:after {
        content: " ";
        display: table;
    }

    nav {
      position: relative;
      background: #2B2B2B;
      background-image: -webkit-linear-gradient(bottom, #2B2B2B 7%, #333333 100%);
      background-image: -moz-linear-gradient(bottom, #2B2B2B 7%, #333333 100%);
      background-image: -o-linear-gradient(bottom, #2B2B2B 7%, #333333 100%);
      background-image: linear-gradient(bottom, #2B2B2B 7%, #333333 100%);
      text-align: center;
      letter-spacing: 1px;
      text-shadow: 1px 1px 1px #0E0E0E;
      -webkit-box-shadow: 2px 2px 3px #888;
      -moz-box-shadow: 2px 2px 3px #888;
      box-shadow: 2px 2px 3px #888;
      border-bottom-right-radius: 8px;
      border-bottom-left-radius: 8px;
    }

    /* prime */
    ul.primary li a {
      display: block;
      padding: 20px 30px;
      border-right: 1px solid #3D3D3D;
    }

    ul.primary li:last-child a {
      border-right: none;
    }

    ul.primary li a:hover {

      color: #000;
    }

    /* subs */
    ul.sub {
      position: absolute;
      z-index: 200;
      box-shadow: 2px 2px 0 #BEBEBE;
      width: 35%;
      display:none;
    }

    ul.sub li {
      float: none;
      margin: 0;
    }

    ul.sub li a {
      border-bottom: 1px dotted #ccc;
      border-right: none;
      color: #000;
      padding: 15px 30px;
    }

    ul.sub li:last-child a {
      border-bottom: none;
    }

    ul.sub li a:hover {
      color: #000;
      background: #eeeeee;
    }

    /* sub display*/
    ul.primary li:hover ul {
      display: block;
      background: #fff;
    }

    /* keeps the tab background white */
    ul.primary li:hover a {
      background: #fff;
      color: #666;
      text-shadow: none;
    }

    ul.primary li:hover > a{
      color: #000;
    } 

    @media only screen and (max-width: 600px) {
      .decor {
        padding: 3px;
      }

      .wrap {
        width: 100%;
        margin-top: 0px;
      }

       li {
        float: none;
      }

      ul.primary li:hover a {
        background: none;
        color: #8B8B8B;
        text-shadow: 1px 1px #000;
      }

      ul.primary li:hover ul {
        display: block;
        background: #272727;
        color: #fff;
        font-size: smaller; 
      }

      ul.sub {
        display: block;  
        position: static;
        box-shadow: none;
        width: 100%;
      }

      ul.sub li a {
        background: #272727;
            border: none;
        color: #8B8B8B;
      }

      ul.sub li a:hover {
        color: #ccc;
        background: none;
      }
    }


</style>

</head>

<body>
<div class="wrap">
<span class="decor"></span>
<nav>
  <ul class="primary">
    
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
		/**
                * Si el usuario tiene turno rotativo, 
                * no se debe mostrar el menu de Permisos Administrativos 
                */
                int idxPA = moduleTitulo.trim().toUpperCase().indexOf("PERMISOS ADMINISTRATIVOS");
                System.out.println("[GestionFemaseWeb]quick_menu.jsp]"
                    + "moduleTitulo: " + moduleTitulo
                    + ", idxPA: " + idxPA);
                boolean mostrarModulo = true;
                if (idxPA >= 0 && tieneTurnoRotativo){
                    mostrarModulo = false;
                }
                        
                if (currentModulo.getAccesoRapido().compareTo("S") == 0 && mostrarModulo)
		{
		%>
			<li>
			  <a href="javascript:;"><%=moduleTitulo%></a>
			  <ul class="sub">
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
                            </ul>
			</li>
                <%}%>
        <%}%>        
    </ul>
</nav>
</div>

    
    
</body>
</html>
