<%@page import="cl.femase.gestionweb.vo.MensajeUsuarioVO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.OutputStream"%>
<%@page import="java.io.File"%>
<%@page import="java.net.URL"%>

<%
    String fechaDesde = (String)request.getAttribute("fechaDesde");
    String fechaHasta = (String)request.getAttribute("fechaHasta");
    ArrayList<MensajeUsuarioVO> mensajes = (ArrayList<MensajeUsuarioVO>)request.getAttribute("mensajes");
    
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>(FEMASE)Mensaje de administracion</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <script src="<%=request.getContextPath()%>/mantencion/turnos/jquery-1.9.1.min.js" type="text/javascript"></script>
   	
	<script language="javascript">
            function volver(){
				document.location.href='<%=request.getContextPath()%>/quick_menu/quick_menu.jsp';      
            }
        </script>
        
   <style>
   	/* spacing */

table {
  table-layout: fixed;
  width: 80%;
  border-collapse: collapse;
  border: 3px solid #195598;
}

thead th:nth-child(1) {
  width: 10%;
}

thead th:nth-child(2) {
  width: 20%;
}

thead th:nth-child(3) {
  width: 15%;
}

thead th:nth-child(4) {
  width: 35%;
}

th, td {
  padding: 3px;
}
	.titulo_tabla {
		font-family: Verdana, Geneva, sans-serif;
		font-size: 16px;
		color: #001e5b;
		background:#d6eef7;
	}
   	.label_tabla {
		font-family: Verdana, Geneva, sans-serif;
		font-size: 12px;
		color: #001e5b;
		background:#d6eef7;
	}
	.value_tabla {
		font-family: Verdana, Geneva, sans-serif;
		font-size: 12px;
		color: #001e5b;
		background:#fcfeff;	
	}
	/*
	.boton {
	font-family: Verdana, Geneva, sans-serif;
	font-size: 16px;
	color: #FFFFFF;
	background-color: #3399CC;
	}
	*/
	
	.boton4:hover {
	  color: rgba(255, 255, 255, 1) !important;
	  box-shadow: 0 4px 16px rgba(49, 138, 172, 1);
	  transition: all 0.2s ease;
	}
	.boton4 {
		color: rgba(255, 255, 255, 0.9) !important;
		font-size: 15px;
		font-weight: 500;
		padding: 0.5em 1.2em;
		background: #318aac;
		border: 2px solid;
		border-color: #318aac;
		position: relative;
	}


   </style>
   
</head>

<body>
 <form id="form1" name="form1" method="post" action="<%=request.getContextPath()%>/servlet/SolicitudVacacionesController" target="_self">

   <table align="center" summary="Vista Previa - Solicitud de Vacaciones">
  <caption><span class="titulo_tabla">Solicitud de Vacaciones Enviada</span></caption>
  <thead>
  <%
      for (int i = 0; i < mensajes.size(); i++) {
		MensajeUsuarioVO mensaje= mensajes.get(i);
		String label = mensaje.getLabel();
		String value = mensaje.getValue();
		System.out.println("[vista_previa.jsp]label=" + label + ",value= " + value);
            %>
            <tr>
              <th width="50%" align="right" class="label_tabla" scope="col"><%=label%></th>
              <th width="50%" align="left" class="value_tabla" scope="col">&nbsp;<%=value%></th>
             </tr>
    <%}%>
    <tr>
              <th align="right" scope="col" class="label_tabla"><input name="action" type="hidden" id="action" value="create" />
        <input name="fechaDesde" type="hidden" id="fechaDesde" value="<%=fechaDesde%>" />
        <input name="fechaHasta" type="hidden" id="fechaHasta" value="<%=fechaHasta%>" /></th>
              <th scope="col">&nbsp;
                 </th>
             </tr>
             <tr>
              <th width="50%" align="right" class="label_tabla" scope="col">&nbsp;</th>
              <th width="50%" align="left" class="value_tabla" scope="col">
              	<input type="button" value="Volver" class="boton4" onClick="volver()"></th>
             </tr>
  </thead>
  <tbody>
  <tfoot>
        
  </tfoot>
</table>
</form>
</body>
</html>
