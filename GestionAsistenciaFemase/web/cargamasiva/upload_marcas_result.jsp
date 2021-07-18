<%@page import="java.util.Iterator"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="cl.femase.gestionweb.vo.MarcaVO"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%
    String tipoUpload = (String)request.getAttribute("tipo");
    //String deviceId = (String)request.getAttribute("deviceId");
    LinkedHashMap<String,MarcaVO> marcasOk = 
        (LinkedHashMap<String,MarcaVO>)request.getAttribute("marcasOk");
    LinkedHashMap<String,MarcaVO> marcasError = 
        (LinkedHashMap<String,MarcaVO>)request.getAttribute("marcasError");
    
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Upload cencos</title>

<style>
.divTable
{
width:100%;
display:block;
height:100%!important;
border:1px solid #01DF74;
padding:0px;
background-color:transparent;
margin:0px;
line-Height:26px;
}

.divCaption
{
width:100%;
background-color:transparent;
position:relative;
font-size:18px;
color:#2293FF;
text-align:center;
line-height:20px;
font-family:'arial','helvetica, sans-serif';
font-weight:bold;
border:0px solid #FFFFFF;
}

.divHeaderRow
{
width:100%;
position:relative;
}

.divHeaderCell
{
border:1px solid #81BEF7;
width:13.166666666666668%;
position:relative;
font-family:'arial','helvetica, sans-serif';font-size:13px;
font-style:bold;
background-color:#A9D0F5;
float:left;
}

.divRow
{
width:100%;
display:table;
position:relative;
color:#000000;
}

.divCell
{
    border:1px solid #81BEF7;
    width:13.2%;
    display:block;
    position:relative;
    background-color:#ffffff;
    color:#000000;
    font-size:12px;
    float:left;
    text-align:left;
    vertical-align:middle;
    letter-spacing:0px;
    word-spacing:0px;
}

.divFooterRow
{
    position:relative;
    width:100%;
}

.divFooterCell
{
background-color:#81BEF7;color:#585858;
width:100%;
font-family:'arial, helvetica, sans-serif';font-size:12px;
text-align:center;
position:relative;
line-height:20px;
font-weight:bold;
}
</style>

</head>

<body>
  
    <div class="center">
      <h2><a href="#">Sistema Gestion - FEMASE - Carga de marcas.</a></h2>
      <h1><a href="#"><%=(String)session.getAttribute("mensaje")%></a></h1>
    </div>
    <%if (marcasOk.size() > 0){%>  
        <!-- INICIO TABLA CON MARCAS CARGADAS CORRECTAMENTE-->
        <div id="divTable" class="divTable">
            <div id="divCaption" class="divCaption">MARCAS CARGADAS CORRECTAMENTE</div>
            <!-- Div Header  -->
            <div  class="divHeaderRow">
                <div id="divHeaderCell1" class="divHeaderCell">#</div>
                <div id="divHeaderCell2" class="divHeaderCell">Empresa-dispositivo</div>
                <div id="divHeaderCell2" class="divHeaderCell">Num Ficha</div>
                <div id="divHeaderCell2" class="divHeaderCell">Fecha-hora</div>
                <div id="divHeaderCell4" class="divHeaderCell">Tipo</div>
                <div id="divHeaderCell5" class="divHeaderCell">Id</div>
                <div id="divHeaderCell6" class="divHeaderCell">Hashcode</div>
            </div><!-- HeaderRow  Ends -->
                <% int correlativo = 1;
                Iterator<MarcaVO> it = marcasOk.values().iterator();
                while (it.hasNext())
                {
                    MarcaVO marca = it.next();
                    %>
                    <!-- Row  -->
                    <div id="divRow<%=correlativo%>" class="divRow">
                        <div class="divCell"><%=correlativo%></div>
                        <div class="divCell"><%=marca.getEmpresaCod()%>-<%=marca.getCodDispositivo()%></div>
                        <div class="divCell"><%=marca.getRutEmpleado()%></div>
                        <div class="divCell"><%=marca.getFechaHora()%></div>
                        <div class="divCell"><%=marca.getTipoMarca()%></div>
                        <div class="divCell"><%=marca.getId()%></div>
                        <div class="divCell"><%=marca.getHashcode()%></div>
                    </div>
                    <%correlativo++;  }%>
                    <!-- Row  Ends1 -->
                    <!--Start Footer  Row  -->
                    <div id="divFooterRow" class="divFooterRow">
                        <!-- Start Footer  cell  -->
                        <div id="divFooterCell" class="divFooterCell">FIN TABLA DE DATOS
                        <!-- End Footer  Cell  -->
                        </div>
                    <!-- End Footer  Row  -->
                    </div>
        </div><!-- Ends Main div  -->

        <!-- FIN TABLA CON MARCAS CARGADAS CORRECTAMENTE-->
    <%}%>
    <%if (marcasError.size() > 0){%> 
        <div id="divTableError" class="divTable">
	<div id="divCaptionError" class="divCaption">MARCAS NO CARGADAS</div>
        <!-- Div Header  -->
        <div  class="divHeaderRow">
                <div id="divHeaderCell1" class="divHeaderCell">#</div>
                <div id="divHeaderCell2" class="divHeaderCell">Empresa-dispositivo</div>
                <div id="divHeaderCell2" class="divHeaderCell">Num Ficha</div>
                <div id="divHeaderCell2" class="divHeaderCell">Fecha-hora</div>
                <div id="divHeaderCell4" class="divHeaderCell">Tipo</div>
                <div id="divHeaderCell5" class="divHeaderCell">Hashcode</div>
                <div id="divHeaderCell6" class="divHeaderCell">Error</div>
        </div><!-- HeaderRow  Ends -->
            <% int correlativo2 = 1;
               Iterator<MarcaVO> it2 = marcasError.values().iterator();
               while (it2.hasNext())
               {
                    MarcaVO marca2 = it2.next();
                    %>
                      <!-- Row  -->
                        <div id="divRow<%=correlativo2%>" class="divRow">
                            <div class="divCell"><%=correlativo2%></div>
                            <div class="divCell"><%=marca2.getEmpresaCod()%>-<%=marca2.getCodDispositivo()%></div>
                            <div class="divCell"><%=marca2.getRutEmpleado()%></div>
                            <div class="divCell"><%=marca2.getFechaHora()%></div>
                            <div class="divCell"><%=marca2.getTipoMarca()%></div>
                            <div class="divCell"><%=marca2.getHashcode()%></div>
                            <div class="divCell"><%=marca2.getUploadMessageError()%></div>
                        </div>
                    <!-- Row  Ends1 -->
                    <%correlativo2++; }%>
        
                <!--Start Footer  Row  -->
                <div id="divFooterRow" class="divFooterRow">
                <!-- Start Footer  cell  -->
                <div id="divFooterCell" class="divFooterCell">FIN TABLA DE DATOS
                <!-- End Footer  Cell  -->
                </div>
                <!-- End Footer  Row  -->
                </div>
        </div><!-- Ends Main div  error -->
        <!-- FIN TABLA CON MARCAS NO CARGADAS-->
        <%}%>
  
  
</body>
</html>
