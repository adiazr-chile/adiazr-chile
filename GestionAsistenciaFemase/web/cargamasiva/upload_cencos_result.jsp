<%@page import="cl.femase.gestionweb.vo.CentroCostoVO"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="cl.femase.gestionweb.vo.CentroCostoVO"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%
    LinkedHashMap<String,CentroCostoVO> cencosOk = 
        (LinkedHashMap<String,CentroCostoVO>)request.getAttribute("cencosOk");
    LinkedHashMap<String,CentroCostoVO> cencosError = 
        (LinkedHashMap<String,CentroCostoVO>)request.getAttribute("cencosError");
    
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
        line-height:26px;
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
        font-family:'arial',helvetica, sans-serif;
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
        width:16.166666666666668%;
        position:relative;
        font-family:'arial,helvetica, sans-serif';font-size:13px;
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
width:16.166666666666668%;
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
font-family:'Arial, helvetica, sans-serif';font-size:12px;
padding:0px;margin:0px;
spacing:0px;
text-align:center;
position:relative;
line-height:20px;
font-weight:bold;
}
</style>

</head>

<body>
  
    <div class="center">
      <h2><a href="#">Sistema Gestion - FEMASE - Carga Centros de Costo</a></h2>
      <h1><a href="#"><%=(String)session.getAttribute("mensaje")%></a></h1>
    </div>
    <%if (cencosOk.size() > 0){%>  
        <!-- INICIO TABLA CON CENTROS COSTO CARGADOS CORRECTAMENTE-->
        <div id="divTable" class="divTable">
            <div id="divCaption" class="divCaption">CENTROS DE COSTO CARGADOS CORRECTAMENTE</div>
            <!-- Div Header  -->
            <div  class="divHeaderRow">
                <div id="divHeaderCell1" class="divHeaderCell">#</div>
                <div id="divHeaderCell2" class="divHeaderCell">Nombre</div>
                <div id="divHeaderCell3" class="divHeaderCell">Estado</div>
                <div id="divHeaderCell4" class="divHeaderCell">Direccion</div>
                <div id="divHeaderCell5" class="divHeaderCell">ID Comuna</div>
                <div id="divHeaderCell6" class="divHeaderCell">Email</div>
            </div><!-- HeaderRow  Ends -->
                <% int correlativo = 1;
                Iterator<CentroCostoVO> it = cencosOk.values().iterator();
                while (it.hasNext())
                {
                    CentroCostoVO cenco = it.next();
                    %>
                    <!-- Row  -->
                    <div id="divRow<%=correlativo%>" class="divRow">
                        <div class="divCell"><%=correlativo%></div>
                        <div class="divCell"><%=cenco.getNombre()%></div>
                        <div class="divCell"><%=cenco.getEstado()%></div>
                        <div class="divCell"><%=cenco.getDireccion()%></div>
                        <div class="divCell"><%=cenco.getComunaId()%></div>
                        <div class="divCell"><%=cenco.getEmail()%></div>
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

        <!-- FIN TABLA CON EMPLEADOS CARGADOS CORRECTAMENTE-->
    <%}%>
    <%if (cencosError.size() > 0){%> 
        <div id="divTableError" class="divTable">
	<div id="divCaptionError" class="divCaption">CENTROS DE COSTO NO CARGADOS</div>
        <!-- Div Header  -->
        <div  class="divHeaderRow">
                <div id="divHeaderCell1" class="divHeaderCell">#</div>
                <div id="divHeaderCell2" class="divHeaderCell">Nombre</div>
                <div id="divHeaderCell5" class="divHeaderCell">ID Comuna</div>
                <div id="divHeaderCell6" class="divHeaderCell">Email</div>
                <div id="divHeaderCell11" class="divHeaderCell">Error</div>
        </div><!-- HeaderRow  Ends -->
            <% int correlativo2 = 1;
               Iterator<CentroCostoVO> it2 = cencosError.values().iterator();
               while (it2.hasNext())
               {
                    CentroCostoVO cenco2 = it2.next();
                    %>
                      <!-- Row  -->
                        <div id="divRow<%=correlativo2%>" class="divRow">
                            <div class="divCell"><%=correlativo2%></div>
                            <div class="divCell"><%=cenco2.getNombre()%></div>
                            <div class="divCell"><%=cenco2.getComunaId()%></div>
                            <div class="divCell"><%=cenco2.getEmail()%></div>
                            <div class="divCell"><%=cenco2.getUploadMessageError()%></div>
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
        <!-- FIN TABLA CON EMPLEADOS NO CARGADOS-->
        <%}%>
  
  
</body>
</html>
