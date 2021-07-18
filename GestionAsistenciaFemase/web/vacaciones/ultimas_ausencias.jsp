<%@page import="cl.femase.gestionweb.vo.UsuarioVO"%>
<%@page import="java.util.List"%>
<%@page import="cl.femase.gestionweb.business.DetalleAusenciaBp"%>
<%@page import="cl.femase.gestionweb.vo.DetalleAusenciaVO"%>
<%@page import="java.util.Iterator"%>
<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="" %>
<%
    UsuarioVO theUser	= (UsuarioVO)session.getAttribute("usuarioObj");
    DetalleAusenciaBp ausenciasBp=new DetalleAusenciaBp(null);
    List<DetalleAusenciaVO> ultimasAusencias = 
        ausenciasBp.getUltimasAusencias(theUser, 8);
  %>      
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Documento sin título</title>
<style type="text/css">
<!--
body {
	font: 100%/1.4 Verdana, Arial, Helvetica, sans-serif;
	background-color: #4E5869;
	margin: 0;
	padding: 0;
	color: #000;
}

/* ~~ Selectores de elemento/etiqueta ~~ */
ul, ol, dl { /* Debido a las diferencias existentes entre los navegadores, es recomendable no añadir relleno ni márgenes en las listas. Para lograr coherencia, puede especificar las cantidades deseadas aquí o en los elementos de lista (LI, DT, DD) que contienen. Recuerde que lo que haga aquí se aplicará en cascada en la lista .nav, a no ser que escriba un selector más específico. */
	padding: 0;
	margin: 0;
}
h1, h2, h3, h4, h5, h6, p {
	margin-top: 0;	 /* la eliminación del margen superior resuelve un problema que origina que los márgenes escapen de la etiqueta div contenedora. El margen inferior restante lo mantendrá separado de los elementos de que le sigan. */
	padding-right: 15px;
	padding-left: 15px; /* la adición de relleno a los lados del elemento dentro de las divs, en lugar de en las divs propiamente dichas, elimina todas las matemáticas de modelo de cuadro. Una div anidada con relleno lateral también puede usarse como método alternativo. */
}
a img { /* este selector elimina el borde azul predeterminado que se muestra en algunos navegadores alrededor de una imagen cuando está rodeada por un vínculo */
	border: none;
}

/* ~~ La aplicación de estilo a los vínculos del sitio debe permanecer en este orden (incluido el grupo de selectores que crea el efecto hover -paso por encima-). ~~ */
a:link {
	color:#414958;
	text-decoration: underline; /* a no ser que aplique estilos a los vínculos para que tengan un aspecto muy exclusivo, es recomendable proporcionar subrayados para facilitar una identificación visual rápida */
}
a:visited {
	color: #4E5869;
	text-decoration: underline;
}
a:hover, a:active, a:focus { /* este grupo de selectores proporcionará a un usuario que navegue mediante el teclado la misma experiencia de hover (paso por encima) que experimenta un usuario que emplea un ratón. */
	text-decoration: none;
}

/* ~~ este contenedor rodea a todas las demás divs, lo que les asigna su anchura basada en porcentaje ~~ */
.container {
	width: 80%;
	max-width: 1260px;/* puede que sea conveniente una anchura máxima (max-width) para evitar que este diseño sea demasiado ancho en un monitor grande. Esto mantiene una longitud de línea más legible. IE6 no respeta esta declaración. */
	min-width: 780px;/* puede que sea conveniente una anchura mínima (min-width) para evitar que este diseño sea demasiado estrecho. Esto permite que la longitud de línea sea más legible en las columnas laterales. IE6 no respeta esta declaración. */
	background-color: #FFF;
	margin: 0 auto; /* el valor automático de los lados, unido a la anchura, centra el diseño. No es necesario si establece la anchura de .container en el 100%. */
}

/* ~~ Esta es la información de diseño. ~~ 

1) El relleno sólo se sitúa en la parte superior y/o inferior de la div. Los elementos situados dentro de esta div tienen relleno a los lados. Esto le ahorra las "matemáticas de modelo de cuadro". Recuerde que si añade relleno o borde lateral a la div propiamente dicha, éste se añadirá a la anchura que defina para crear la anchura *total*. También puede optar por eliminar el relleno del elemento en la div y colocar una segunda div dentro de ésta sin anchura y el relleno necesario para el diseño deseado.

*/
.content {
	padding: 10px 0;
}

/* ~~ Este selector agrupado da espacio a las listas del área de .content ~~ */
.content ul, .content ol { 
	padding: 0 15px 15px 40px; /* este relleno reproduce en espejo el relleno derecho de la regla de encabezados y de párrafo incluida más arriba. El relleno se ha colocado en la parte inferior para que el espacio existente entre otros elementos de la lista y a la izquierda cree la sangría. Estos pueden ajustarse como se desee. */
}

/* ~~ clases float/clear varias ~~ */
.fltrt {  /* esta clase puede utilizarse para que un elemento flote en la parte derecha de la página. El elemento flotante debe preceder al elemento junto al que debe aparecer en la página. */
	float: right;
	margin-left: 8px;
}
.fltlft { /* esta clase puede utilizarse para que un elemento flote en la parte izquierda de la página. El elemento flotante debe preceder al elemento junto al que debe aparecer en la página. */
	float: left;
	margin-right: 8px;
}
.clearfloat { /* esta clase puede situarse en una <br /> o div vacía como elemento final tras la última div flotante (dentro de #container) si se elimina overflow:hidden en .container */
	clear:both;
	height:0;
	font-size: 1px;
	line-height: 0px;
}

.divTable {	display: table;
	width: 100%;
}
.divTableBody {	display: table-row-group;
}
.divTableCell {	border: 1px solid #999999;
	display: table-cell;
	font-family: monospace;
	font-size: 8px;
	background-color: azure;
}
.divTableHead {	border: 1px solid #999999;
	display: table-cell;
	padding: 3px 10px;
	font-family: monospace;
	font-size: smaller;
	background-color: azure;
}
.divTableHeading {	border: 1px solid #999999;
	display: table-cell;
	padding: 3px 10px;
	font-family: monospace;
	font-size: medium;
        background-color: khaki;
}
.divTableRow {	display: table-row;
}
div.abs {	width: 593px;
	height: 250px;
	position: absolute;
	left: 514px;
	top: 66px;
	cursor: pointer;
}

-->
</style>
</head>

<body>

<div class="container">
  <div class="content">
    
    <div class="divTable">
            <div class="divTableBody">
              <div class="divTableRow">
                <div class="divTableHeading">&nbsp;Centro de costo</div>
                <div class="divTableHeading">Rut&nbsp;</div>
                <div class="divTableHeading">&nbsp;Nombre</div>
                <div class="divTableHeading">&nbsp;Saldo vacaciones</div>
                <div class="divTableHeading">&nbsp;Tipo</div>
                <div class="divTableHeading">&nbsp;Fecha inicio</div>
                <div class="divTableHeading">Fecha fin&nbsp;</div>
                <div class="divTableHeading">Fecha ingreso</div>
                <div class="divTableHeading">Autorizador</div>
              </div>
                <%
                    Iterator<DetalleAusenciaVO> iteraUltimas = ultimasAusencias.iterator();
                    while(iteraUltimas.hasNext() ) {
                        DetalleAusenciaVO ultima = iteraUltimas.next();
                        //String cencoName = "[" + ultima.getEmpresaNombre() + "]" 
                        //    + ",[" + ultima.getDeptoNombre() + "]"
                        //    + ",[" + ultima.getCencoNombre() + "]";
						String cencoName = ultima.getCencoNombre();	
                        String labelAutorizador = ultima.getNombreAutorizador()
                            + "(" + ultima.getNomCencoAutorizador()+ ")";
                %>  
                        <div class="divTableRow">    
                          <div class="divTableCell"><%=cencoName%></div>
                          <div class="divTableCell"><%=ultima.getRutEmpleado()%></div>
                          <div class="divTableCell"><%=ultima.getNombreEmpleado()%></div>
                          <div class="divTableCell"><%=ultima.getSaldoDias()%></div>
                          <div class="divTableCell"><%=ultima.getNombreAusencia()%></div>
                          <div class="divTableCell"><%=ultima.getFechaInicioAsStr()%></div>
                          <div class="divTableCell"><%=ultima.getFechaFinAsStr()%></div>
                          <div class="divTableCell"><%=ultima.getFechaIngresoAsStr()%></div>
                          <div class="divTableCell"><%=labelAutorizador%></div>
                        </div>
                    <%}%>
            </div>
            <!-- fin divTableBody -->
          </div>
          <!-- fin divTable-->
        </div>
    
    <!-- end .content --></div>
  <!-- end .container --></div>
</body>
</html>