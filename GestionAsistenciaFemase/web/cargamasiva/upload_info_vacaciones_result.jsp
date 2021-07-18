<%@page import="cl.femase.gestionweb.vo.VacacionesVO"%>
<%@page import="cl.femase.gestionweb.vo.CentroCostoVO"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="cl.femase.gestionweb.vo.CentroCostoVO"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%
    LinkedHashMap<String,VacacionesVO> vacacionesOk = 
        (LinkedHashMap<String,VacacionesVO>)request.getAttribute("vacacionesOk");
    
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>Upload dias de vacaciones</title>

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
      <h2><a href="#">Sistema Gestion - FEMASE - Carga de dias de vacaciones</a></h2>
      <h1><a href="#"><%=(String)session.getAttribute("mensaje")%></a></h1>
    </div>
    <%if (vacacionesOk != null && vacacionesOk.size() > 0){%>  
        <!-- INICIO TABLA CON DIAS DE VACACIONES CARGADOS CORRECTAMENTE-->
        <div id="divTable" class="divTable">
            <div id="divCaption" class="divCaption">Informaci&oacute;n de vacaciones Cargada Correctamente</div>
            <!-- Div Header  -->
            <div  class="divHeaderRow">
                <div id="divHeaderCell1" class="divHeaderCell">EmpresaId</div>
                <div id="divHeaderCell2" class="divHeaderCell">Rut empleado</div>
                <div id="divHeaderCell3" class="divHeaderCell">AFP</div>
                <div id="divHeaderCell4" class="divHeaderCell">Fecha Certif AFP</div>
                <div id="divHeaderCell4" class="divHeaderCell">Dias especiales</div>
                <div id="divHeaderCell4" class="divHeaderCell">Dias adicionales</div>
            </div><!-- HeaderRow  Ends -->
                <% int correlativo = 1;
                Iterator<VacacionesVO> it = vacacionesOk.values().iterator();
                while (it.hasNext())
                {
                    VacacionesVO diasvacaciones = it.next();
                    %>
                    <!-- Row  -->
                    <div id="divRow<%=correlativo%>" class="divRow">
                        <div class="divCell"><%=diasvacaciones.getEmpresaId()%></div>
                        <div class="divCell"><%=diasvacaciones.getRutEmpleado()%></div>
                        <div class="divCell"><%=diasvacaciones.getAfpCode()%></div>
                        <div class="divCell"><%=diasvacaciones.getFechaCertifVacacionesProgresivas()%></div>
                        <div class="divCell"><%=diasvacaciones.getDiasEspeciales()%></div>
                        <div class="divCell"><%=diasvacaciones.getDiasAdicionales()%></div>
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

        <!-- FIN TABLA CON DIAS DE VACACIONES DE LOS EMPLEADOS CARGADOS CORRECTAMENTE-->
    <%}%>
    
  
</body>
</html>
