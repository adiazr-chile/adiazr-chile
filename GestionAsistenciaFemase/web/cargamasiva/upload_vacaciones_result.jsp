<%@page import="cl.femase.gestionweb.vo.DetalleAusenciaVO"%>
<%@page import="cl.femase.gestionweb.vo.ResultadoCargaCsvVO"%>
<%@page import="cl.femase.gestionweb.vo.ResultadoCargaDataCsvVO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="cl.femase.gestionweb.vo.EmpleadoVO"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%
    String tipoUpload = (String)request.getAttribute("tipo");
    ArrayList<ResultadoCargaDataCsvVO> vacacionesCargadas = (ArrayList<ResultadoCargaDataCsvVO>)request.getAttribute("vacacionesCargadas");
    
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Carga masiva de Vacaciones. Resultado</title>

<style>
	.columnTitle {
		font-family: "Lucida Sans Unicode", "Lucida Grande", sans-serif;
		font-size: 16px;
	}
	.cellValue {
		font-family: "Lucida Sans Unicode", "Lucida Grande", sans-serif;
		font-size: 12px;
	}
	.subCell {
		font-family: "Lucida Sans Unicode", "Lucida Grande", sans-serif;
		font-size: 12px;
	}
	.subCellRed {
		font-family: "Lucida Sans Unicode", "Lucida Grande", sans-serif;
		font-size: 12px;
		color:#F00;
	}
</style>

</head>

<body>
  
    <div class="center">
      <h2>Sistema Gestion - FEMASE - Carga Masiva de Vacaciones, resultado</h2>
    </div>
    <%if (vacacionesCargadas != null && !vacacionesCargadas.isEmpty()){%>  
        <table width="90%" border="1" align="center" cellpadding="2" cellspacing="2">
          <tr>
            <td bgcolor="#0066CC">
            <table width="100%" border="1">
              <tr class="columnTitle" >
                <td width="4%" bgcolor="#CCCCCC">#</td>
                <td width="10%" bgcolor="#CCCCCC">Empresa</td>
                <td width="9%" bgcolor="#CCCCCC">Depto</td>
                <td width="10%" bgcolor="#CCCCCC">Centro Costo</td>
                <td width="16%" bgcolor="#CCCCCC">RUN</td>
                <td width="33%" bgcolor="#CCCCCC">Nombre</td>
                <td width="33%" bgcolor="#CCCCCC">Inicio</td>
                <td width="33%" bgcolor="#CCCCCC">Fin</td>
                <td width="18%" bgcolor="#CCCCCC">RUN Autorizador</td>
              </tr>
              <% 
                int correlativo = 1;
                Iterator<ResultadoCargaDataCsvVO> it = vacacionesCargadas.iterator();
                while (it.hasNext())
                {
                    ResultadoCargaDataCsvVO empleadoCargado = it.next();
                    EmpleadoVO infoEmpleado         = empleadoCargado.getEmpleado();
                    DetalleAusenciaVO infoVacacion  = empleadoCargado.getVacacion();
                    String fullName = "";
                    if (infoEmpleado.getNombres() != null){
                        fullName = infoEmpleado.getNombres() + " " 
                            + infoEmpleado.getApePaterno()+ " " 
                            + infoEmpleado.getApeMaterno();
                    }
                    String inicio           = "";
                    String fin              = "";
                    String rutAutorizador   = "";
                    if (infoVacacion != null){
                        inicio          = infoVacacion.getFechaInicioAsStr();
                        fin             = infoVacacion.getFechaFinAsStr();
                        rutAutorizador  = infoVacacion.getRutAutorizador();
                    }
                    %>
                      <tr class="cellValue">
                        <td bgcolor="#E0E0E0"><%=correlativo%></td>
                        <td bgcolor="#E0E0E0"><%=infoEmpleado.getEmpresa().getId()%></td>
                        <td bgcolor="#E0E0E0"><%=infoEmpleado.getDepartamento().getId()%></td>
                        <td bgcolor="#E0E0E0"><%=infoEmpleado.getCentroCosto().getId()%></td>
                        <td bgcolor="#E0E0E0"><%=infoEmpleado.getRut()%></td>
                        <td bgcolor="#E0E0E0"><%=fullName%></td>
                        <td bgcolor="#E0E0E0"><%=inicio%></td>
                        <td bgcolor="#E0E0E0"><%=fin%></td>
                        <td bgcolor="#E0E0E0"><%=rutAutorizador%></td>
                      </tr>
                      <tr>
                        <td bgcolor="#66CCCC">&nbsp;</td>
                        <td align="right" bgcolor="#66CCCC"><span class="subCell">Resultado</span>&nbsp;</td>
                        <td colspan="4" bgcolor="#66CCCC"><table width="100%" border="0" cellspacing="1" cellpadding="1">
                        <%
                        Iterator<ResultadoCargaCsvVO> itMensajes = empleadoCargado.getMensajes().iterator();
                        while (itMensajes.hasNext())
                        {
                            ResultadoCargaCsvVO mensaje = itMensajes.next();
                            String strClass = "subCell";
                            if (mensaje.getTipo().compareTo("ERROR")==0) strClass = "subCellRed";
                        %>        
                          <tr>
                            <td class="<%=strClass%>"><%=mensaje.getMensaje()%></td>
                          </tr>
                        <%}%>  
                        </table></td>
                        <td bgcolor="#66CCCC">&nbsp;</td>
                      </tr>
                <%  
                    correlativo++;
                }
                %>
            </table>
            
            </td>
          </tr>
        </table>
	<%}%>
        
</body>
</html>
