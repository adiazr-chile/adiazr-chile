<%@page import="cl.femase.gestionweb.vo.DetalleAusenciaVO"%>
<%@page import="cl.femase.gestionweb.vo.CentroCostoVO"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="cl.femase.gestionweb.vo.CentroCostoVO"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%
    LinkedHashMap<String,DetalleAusenciaVO> vacacionesOk = 
        (LinkedHashMap<String,DetalleAusenciaVO>)request.getAttribute("vacacionesOk");
	LinkedHashMap<String,DetalleAusenciaVO> vacacionesError = 
        (LinkedHashMap<String,DetalleAusenciaVO>)request.getAttribute("vacacionesError");	
    
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>Upload vacaciones</title>

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
            font-family:'arial,helvetica, sans-serif';
            font-size:13px;
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
            font-family:'arial, helvetica, sans-serif';
            font-size:12px;
            padding:0px;margin:0px;
            text-align:center;
            position:relative;
            line-height:20px;
            font-weight:bold;
        }
    </style>

</head>

<body>
  
    <div class="center">
      <h2><a href="#">Sistema Gestion - FEMASE - Carga de vacaciones</a></h2>
      <h1><a href="#"><%=(String)session.getAttribute("mensaje")%></a></h1>
    </div>
    <%if (vacacionesOk != null && !vacacionesOk.isEmpty()){%>  
        <!-- INICIO TABLA CON VACACIONES CARGADAS CORRECTAMENTE-->
        <div id="divTable" class="divTable">
            <div id="divCaption" class="divCaption">Vacaciones Cargadas Correctamente</div>
            <!-- Div Header  -->
            <div  class="divHeaderRow">
                <div id="divHeaderCell1" class="divHeaderCell">EmpresaId</div>
                <div id="divHeaderCell2" class="divHeaderCell">Rut empleado</div>
                <div id="divHeaderCell3" class="divHeaderCell">Desde</div>
                <div id="divHeaderCell4" class="divHeaderCell">Hasta</div>
            </div><!-- HeaderRow  Ends -->
                <% int correlativo = 1;
                Iterator<DetalleAusenciaVO> it = vacacionesOk.values().iterator();
                while (it.hasNext())
                {
                    DetalleAusenciaVO ausencia = it.next();
                    %>
                    <!-- Row  -->
                    <div id="divRow<%=correlativo%>" class="divRow">
                        <div class="divCell"><%=ausencia.getEmpresaId()%></div>
                        <div class="divCell"><%=ausencia.getRutEmpleado()%></div>
                        <div class="divCell"><%=ausencia.getFechaInicioAsStr()%></div>
                        <div class="divCell"><%=ausencia.getFechaFinAsStr()%></div>
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

        <!-- FIN TABLA CON VACACIONES CARGADAS CORRECTAMENTE-->
    <%}%>
    <%if (vacacionesError!=null && !vacacionesError.isEmpty()){%> 
        <div id="divTableError" class="divTable">
	<div id="divCaptionError" class="divCaption">VACACIONES NO CARGADAS</div>
        <!-- Div Header  -->
        <div  class="divHeaderRow">
                <div id="divHeaderCell1" class="divHeaderCell">EmpresaId</div>
                <div id="divHeaderCell2" class="divHeaderCell">Rut empleado</div>
                <div id="divHeaderCell5" class="divHeaderCell">Desde</div>
                <div id="divHeaderCell11" class="divHeaderCell">Hasta</div>
                <div id="divHeaderCell11" class="divHeaderCell">Error</div>
        </div><!-- HeaderRow  Ends -->
            <% int correlativo2 = 1;
               Iterator<DetalleAusenciaVO> it2 = vacacionesError.values().iterator();
               while (it2.hasNext())
               {
                    DetalleAusenciaVO ausencia = it2.next();
                    %>
                      <!-- Row  -->
                        <div id="divRow<%=correlativo2%>" class="divRow">
                            <div class="divCell"><%=ausencia.getEmpresaId()%></div>
                            <div class="divCell"><%=ausencia.getRutEmpleado()%></div>
                            <div class="divCell"><%=ausencia.getFechaInicioAsStr()%></div>
                            <div class="divCell"><%=ausencia.getFechaFinAsStr()%></div>
                            <div class="divCell"><%=ausencia.getMensajeError()%></div>
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
        <!-- FIN TABLA CON VACACIONES NO CARGADAS-->
        <%}%>
  
</body>
</html>
