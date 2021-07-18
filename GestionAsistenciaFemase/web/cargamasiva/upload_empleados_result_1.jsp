<%@page import="cl.femase.gestionweb.vo.ResultadoCargaEmpleadoCsvVO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="cl.femase.gestionweb.vo.EmpleadoVO"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%
    String tipoUpload = (String)request.getAttribute("tipo");
    LinkedHashMap<String,EmpleadoVO> empleadosInsertadosOk = 
        (LinkedHashMap<String,EmpleadoVO>)request.getAttribute("empleadosInsertadosOk");
    LinkedHashMap<String,EmpleadoVO> empleadosActualizadosOk = 
        (LinkedHashMap<String,EmpleadoVO>)request.getAttribute("empleadosActualizadosOk");
    LinkedHashMap<String,EmpleadoVO> empleadosError = 
        (LinkedHashMap<String,EmpleadoVO>)request.getAttribute("empleadosError");
    
    ArrayList<ResultadoCargaEmpleadoCsvVO> empleadosCargados = (ArrayList<ResultadoCargaEmpleadoCsvVO>)request.getAttribute("empleadosCargados");
            
    if (empleadosInsertadosOk != null){
        System.out.println("[upload_empleados_result.jsp]num empleados insertados ok= "+ empleadosInsertadosOk.size());
    }
    if (empleadosActualizadosOk != null){
        System.out.println("[upload_empleados_result.jsp]num empleados actualizados ok= "+ empleadosActualizadosOk.size());
    }
    if (empleadosError != null){
        System.out.println("[upload_empleados_result.jsp]num empleados error= "+ empleadosError.size());
    }
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Untitled Document</title>

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
width:8%;
position:relative;
font-family:'arial',helvetica, sans-serif;font-size:13px;
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
width:8%;
display:block;
position:relative;
background-color:#ffffff;
color:#000000;
font-size:10px;
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
      <h2><a href="#">Sistema Gestion - FEMASE - Carga empleados</a></h2>
    </div>
    <%if (empleadosInsertadosOk !=null && empleadosInsertadosOk.size() > 0){%>  
        <!-- INICIO TABLA CON EMPLEADOS INSERTADOS CORRECTAMENTE-->
        <div id="divTable" class="divTable">
            <div id="divCaption" class="divCaption">EMPLEADOS (NUEVOS) CARGADOS CORRECTAMENTE</div>
            <!-- Div Header  -->
            <div  class="divHeaderRow">
                <div id="divHeaderCell1" class="divHeaderCell">#</div>
                <div id="divHeaderCell2" class="divHeaderCell">Rut</div>
                <div id="divHeaderCell3" class="divHeaderCell">Nombres</div>
                <div id="divHeaderCell4" class="divHeaderCell">APaterno</div>
                <div id="divHeaderCell5" class="divHeaderCell">AMaterno</div>
                <div id="divHeaderCell6" class="divHeaderCell">Email</div>
                <div id="divHeaderCell7" class="divHeaderCell">Empresa</div>
                <div id="divHeaderCell8" class="divHeaderCell">Depto</div>
                <div id="divHeaderCell9" class="divHeaderCell">CentroCosto</div>
                <div id="divHeaderCell10" class="divHeaderCell">IdTurno</div>
                <div id="divHeaderCell11" class="divHeaderCell">IdCargo</div>
            </div><!-- HeaderRow  Ends -->
                <% int correlativo = 1;
                Iterator<EmpleadoVO> it = empleadosInsertadosOk.values().iterator();
                while (it.hasNext())
                {
                    EmpleadoVO empleado = it.next();
                    %>
                    <!-- Row  -->
                    <div id="divRow<%=correlativo%>" class="divRow">
                        <div class="divCell"><%=correlativo%></div>
                        <div class="divCell"><%=empleado.getRut()%></div>
                        <div class="divCell"><%=empleado.getNombres()%></div>
                        <div class="divCell"><%=empleado.getApePaterno()%></div>
                        <div class="divCell"><%=empleado.getApeMaterno()%></div>
                        <div class="divCell"><%=empleado.getEmail()%></div>
                        <div class="divCell"><%=empleado.getEmpresa().getId()%></div>
                        <div class="divCell"><%=empleado.getDepartamento().getId()%></div>
                        <div class="divCell"><%=empleado.getCentroCosto().getId()%></div>
                        <div class="divCell"><%=empleado.getIdTurno()%></div>
                        <div class="divCell"><%=empleado.getIdCargo()%></div>
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

        <!-- FIN TABLA CON EMPLEADOS NUEVOS CARGADOS CORRECTAMENTE-->
    <%}%>
    
    <%if (empleadosActualizadosOk != null && empleadosActualizadosOk.size() > 0){%>  
        <!-- INICIO TABLA CON EMPLEADOS ACTUALIZADOS CORRECTAMENTE-->
        <div id="divTable" class="divTable">
            <div id="divCaption" class="divCaption">EMPLEADOS ACTUALIZADOS CORRECTAMENTE</div>
            <!-- Div Header  -->
            <div  class="divHeaderRow">
                <div id="divHeaderCell1" class="divHeaderCell">#</div>
                <div id="divHeaderCell2" class="divHeaderCell">Rut</div>
                <div id="divHeaderCell3" class="divHeaderCell">Nombres</div>
                <div id="divHeaderCell4" class="divHeaderCell">APaterno</div>
                <div id="divHeaderCell5" class="divHeaderCell">AMaterno</div>
                <div id="divHeaderCell6" class="divHeaderCell">Email</div>
                <div id="divHeaderCell7" class="divHeaderCell">Empresa</div>
                <div id="divHeaderCell8" class="divHeaderCell">Depto</div>
                <div id="divHeaderCell9" class="divHeaderCell">CentroCosto</div>
                <div id="divHeaderCell10" class="divHeaderCell">IdTurno</div>
                <div id="divHeaderCell11" class="divHeaderCell">IdCargo</div>
            </div><!-- HeaderRow  Ends -->
                <% int correlativo = 1;
                Iterator<EmpleadoVO> it2 = empleadosActualizadosOk.values().iterator();
                while (it2.hasNext())
                {
                    EmpleadoVO empleado2 = it2.next();
                    %>
                    <!-- Row  -->
                    <div id="divRow<%=correlativo%>" class="divRow">
                        <div class="divCell"><%=correlativo%></div>
                        <div class="divCell"><%=empleado2.getRut()%></div>
                        <div class="divCell"><%=empleado2.getNombres()%></div>
                        <div class="divCell"><%=empleado2.getApePaterno()%></div>
                        <div class="divCell"><%=empleado2.getApeMaterno()%></div>
                        <div class="divCell"><%=empleado2.getEmail()%></div>
                        <div class="divCell"><%=empleado2.getEmpresa().getId()%></div>
                        <div class="divCell"><%=empleado2.getDepartamento().getId()%></div>
                        <div class="divCell"><%=empleado2.getCentroCosto().getId()%></div>
                        <div class="divCell"><%=empleado2.getIdTurno()%></div>
                        <div class="divCell"><%=empleado2.getIdCargo()%></div>
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

        <!-- FIN TABLA CON EMPLEADOS NUEVOS CARGADOS CORRECTAMENTE-->
    <%}%>
    
    <%if (empleadosError !=null  && empleadosError.size() > 0){%> 
        <div id="divTableError" class="divTable">
	<div id="divCaptionError" class="divCaption">EMPLEADOS NO CARGADOS</div>
        <!-- Div Header  -->
        <div  class="divHeaderRow">
           <div id="divHeaderCell1" class="divHeaderCell">#</div>
            <div id="divHeaderCell2" class="divHeaderCell">Rut</div>
            <div id="divHeaderCell3" class="divHeaderCell">Nombres</div>
            <div id="divHeaderCell4" class="divHeaderCell">APaterno</div>
            <div id="divHeaderCell6" class="divHeaderCell">Email</div>
            <div id="divHeaderCell7" class="divHeaderCell">Error</div>
        </div><!-- HeaderRow  Ends -->
            <% int correlativo2 = 1;
               Iterator<EmpleadoVO> it2 = empleadosError.values().iterator();
               while (it2.hasNext())
               {
                    EmpleadoVO empleado = it2.next();
                    %>
                      <!-- Row  -->
                        <div id="divRow<%=correlativo2%>" class="divRow">
                            <div class="divCell"><%=correlativo2%></div>
                            <div class="divCell"><%=empleado.getRut()%></div>
                            <div class="divCell"><%=empleado.getNombres()%></div>
                            <div class="divCell"><%=empleado.getApePaterno()%></div>
                            <div class="divCell"><%=empleado.getEmail()%></div>
                            <div class="divCell"><%=empleado.getUploadMessageError()%></div>
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
