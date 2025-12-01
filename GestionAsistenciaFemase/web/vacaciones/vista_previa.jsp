<%@page import="cl.femase.gestionweb.vo.MensajeUsuarioVO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.OutputStream"%>
<%@page import="java.io.File"%>
<%@page import="java.net.URL"%>

<%
    String fechaDesde = (String)request.getAttribute("fechaDesde");
    String fechaHasta = (String)request.getAttribute("fechaHasta");
    String diasEfectivosSolicitados = (String)request.getAttribute("diasEfectivosSolicitados");
    String saldoPostVacaciones = (String)request.getAttribute("saldoPostVacaciones");

    ArrayList<MensajeUsuarioVO> mensajes = (ArrayList<MensajeUsuarioVO>)request.getAttribute("mensajes");
        
    Boolean seguir      = (Boolean)request.getAttribute("seguir");
    String errorMessage = (String)request.getAttribute("errorMessage");
    System.out.println("[solicitar_vacaciones.vista_previa_jsp]seguir? " + seguir+", mensajeError:" + errorMessage);
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>(FEMASE)Vista previa Solicitud de vacaciones</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <script src="<%=request.getContextPath()%>/mantencion/turnos/jquery-1.9.1.min.js" type="text/javascript"></script>
   


	<script language="javascript">
            function volver(){
                window.history.back();          
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
        
        .texto_td_tabla_destacado {
		font-family: Verdana, Geneva, sans-serif;
		font-size: 16px;
		color: #0aea35;
		background:#fcfeff;	
	}
        
        .texto_td_tabla_warning {
		font-family: Verdana, Geneva, sans-serif;
		font-size: 16px;
                color: #ff3333;
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

        .boton4 {
            color: rgba(255, 255, 255, 0.9) !important;
            font-size: 15px;
            font-weight: 500;
            padding: 0.5em 1.2em;
            background: #318aac;
            border: 2px solid #318aac;
            border-radius: 8px;
            position: relative;
            text-decoration: none;
            display: inline-block;
            cursor: pointer;
            transition: all 0.2s ease;
          }

          .boton4:hover {
            color: #fff !important;
            box-shadow: 0 4px 16px rgba(49, 138, 172, 1);
            background: #256c88;
          }

        
   </style>
   
</head>

<body>
 <form id="form1" name="form1" method="post" action="<%=request.getContextPath()%>/servlet/SolicitudVacacionesController" target="_self">

   <table align="center" summary="Vista Previa - Solicitud de Vacaciones">
  <caption><span class="titulo_tabla">Vista Previa - Solicitud de Vacaciones</span></caption>
  <thead>
  <%
      for (int i = 0; i < mensajes.size(); i++) {
		MensajeUsuarioVO mensaje= mensajes.get(i);
		String label = mensaje.getLabel();
		String value = mensaje.getValue();
                String labelClass="label_tabla";
                String valueClass="value_tabla";	
                if (label.compareTo("Dias solicitados") == 0 
                        || label.compareTo("Saldo VBA Pre Vacaciones") == 0
                        || label.compareTo("Saldo VBA Post Vacaciones") == 0){
                    labelClass = "texto_td_tabla_destacado";
                    valueClass = "texto_td_tabla_destacado";
                    if (label.compareTo("Saldo VBA Post Vacaciones") == 0){
                        double dblSaldo = Double.parseDouble(value);
                        if (dblSaldo < 0){
                            labelClass = "texto_td_tabla_warning";
                            valueClass = "texto_td_tabla_warning";
                        }
                    }
                }
               
            %>
            <tr>
                <th width="50%" align="right" class="<%=labelClass%>" scope="col"><%=label%></th>
              <th width="50%" align="left" class="<%=valueClass%>" scope="col">&nbsp;<%=value%></th>
             </tr>
    <%}%>
    <tr>
              <th align="right" scope="col" class="label_tabla">
                  <input name="action" type="hidden" id="action" value="create" />
        <input name="fechaDesde" type="hidden" id="fechaDesde" value="<%=fechaDesde%>" />
        <input name="fechaHasta" type="hidden" id="fechaHasta" value="<%=fechaHasta%>" />
        
        <input name="diasEfectivosSolicitados" type="hidden" id="diasEfectivosSolicitados" value="<%=diasEfectivosSolicitados%>" />
        <input name="saldoPostVacaciones" type="hidden" id="saldoPostVacaciones" value="<%=saldoPostVacaciones%>" />
        
        </th>
              <th scope="col">&nbsp;
                 </th>
             </tr>
             <tr>
              <th width="50%" align="right" class="label_tabla" scope="col">&nbsp;</th>
              <th width="50%" align="left" class="value_tabla" scope="col">
                  <%if (seguir){%>
                    <input type="submit" value="Ingresar Solicitud de Vacaciones" class="boton4">
                <%}else{%>
                        <a href="<%=request.getContextPath()%>/vacaciones/ingresar_solicitud_vacaciones.jsp" class="boton4">
                        Volver
                      </a>

                <%}%>
              </th>
             </tr>
  </thead>
  <tbody>
  <tfoot>
        
  </tfoot>
</table>
</form>
        <!-- Toast básico -->
        <div id="toast" style="
            display:none;
            position:fixed;
            bottom:30px;
            left:50%;
            transform:translateX(-50%);
            min-width:210px;
            max-width:80vw;
            background-color:#ffc107; /* amarillo warning Bootstrap */
            color:#333;
            padding:16px 32px;
            border-radius:8px;
            z-index:9999;
            box-shadow: 0 2px 10px rgba(60,60,60,0.20);
            font-weight:500;
        ">
          <%=errorMessage%>
        </div>
       
        <script>
            var flag = <%= (seguir != null && seguir) ? "true" : "false" %>;
            function showConditionalToast() {
              if (!flag) {
                var toast = document.getElementById('toast');
                toast.style.display = 'block';
                setTimeout(() => toast.style.display = 'none', 6000);
              }
            }
            document.addEventListener('DOMContentLoaded', showConditionalToast); 
        </script>
</body>
</html>
