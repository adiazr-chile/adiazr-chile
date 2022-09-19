<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.StringTokenizer"%>
<%@page import="cl.femase.gestionweb.common.Constantes"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Set"%>
<%@page import="cl.femase.gestionweb.vo.MarcaVO"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="cl.femase.gestionweb.vo.DispositivoVO"%>
<%@page import="java.util.HashMap"%>
<%@page import="cl.femase.gestionweb.vo.UsuarioVO"%>
<%
    UsuarioVO userInSession = (UsuarioVO)session.getAttribute("usuarioObj");
    boolean solicitarUbicacion = false;
    
    if (userInSession.getRegistrarUbicacion().compareTo("S") == 0){
        solicitarUbicacion = true;
    }
    
    String nombreEmpresa = userInSession.getEmpresaNombre();
    HashMap<String, DispositivoVO> dispositivos = 
        (HashMap<String, DispositivoVO>)session.getAttribute("dispositivosUsuario");
    LinkedHashMap<String,MarcaVO> allMarcas = 
        (LinkedHashMap<String,MarcaVO>)session.getAttribute("marcasUsuario");
    Calendar calHoy = Calendar.getInstance(new Locale("es","CL"));
    Date fechaActual = calHoy.getTime();
    System.out.println("[GestionFemaseWeb]ShowClock]"
        + "fechaHoraActual: " + fechaActual);
    SimpleDateFormat weekdayFormat = new SimpleDateFormat("EEEEE");
    SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
    SimpleDateFormat monthFormat = new SimpleDateFormat("MMMMM");
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
    String labelFecha = weekdayFormat.format(fechaActual) + ", " + dayFormat.format(fechaActual) 
        + " de " + monthFormat.format(fechaActual)
        + " de " + yearFormat.format(fechaActual);
    System.out.println("fecha: " + labelFecha);
        
    long timeInServer = fechaActual.getTime();
%>    

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Marcacion Virtual Empleado</title>

<script type="text/javascript">
	
    function geoFindMe() {
        <%if (solicitarUbicacion){%>    
            var output = document.getElementById("out");

            if (!navigator.geolocation){
                output.innerHTML = "<p>Geolocation is not supported by your browser</p>";
                return;
            }

            function success(position) {
                var latitude  = position.coords.latitude;
                var longitude = position.coords.longitude;
                document.getElementById('latitud').value=latitude;
                document.getElementById('longitud').value=longitude;
            };

            function error() {
                //output.innerHTML = "Unable to retrieve your location";
            };

            navigator.geolocation.getCurrentPosition(success, error);
        <%}//fin if solicitar ubicacion%>    
    }	

    function enviarMarca(tipoMarca){
        document.getElementById("tipoMarca").value=tipoMarca;
        document.theForm.submit();
    }
    
    var serverTime  = <%=timeInServer%>;
        var expected = serverTime;
        var date;
        var hours;
        var minutes;
        var seconds;
        var now = performance.now();
        var then = now;
        var dt = 0;
        var nextInterval = interval = 1000; // ms

        setTimeout(step, interval);
        function step() {
            then = now;
            now = performance.now();
            dt = now - then - nextInterval; // the drift

            nextInterval = interval - dt;
            serverTime += interval;
            date     = new Date(serverTime);
            hours    = date.getHours();
            minutes  = date.getMinutes();
            seconds  = date.getSeconds();
            document.getElementById('hora_server').innerHTML = hours + ':' + minutes + ':' + seconds;
			document.getElementById('form_hora_server').innerHTML = hours + ':' + minutes + ':' + seconds;
			
			document.getElementById('fecha_server').innerHTML = '<%=labelFecha%>';
						
            //document.getElementById('txt').innerHTML =
            console.log(nextInterval, dt); //Click away to another tab and check the logs after a while
            now = performance.now();

            setTimeout(step, Math.max(0, nextInterval)); // take into account drift
        }
	
</script>
<style type="text/css">
	<!--
	/* DivTable.com */
	.divTable{
	display: table;
	width: 75%;
	right: 50px;
	left: 100px;
	top: 50px;
	bottom: 50px;
	clip: rect(50px,50px,50px,50px);
	text-align: center;
	border-top-color: #CCC;
	border-right-color: #CCC;
	border-bottom-color: #CCC;
	border-left-color: #CCC;
	}
	.divTableRow {
		display: table-row;
	}
	
	.divTableCell, .divTableHead {
		display: table-cell;
		padding: 3px 10px;
		border-top-width: 1px;
		border-right-width: 1px;
		border-bottom-width: 1px;
		border-left-width: 100px;
		/*border-top-style: dashed;
		border-right-style: dashed;
		border-bottom-style: dashed;
		border-left-style: dashed;*/
		background-color: #fbfbfb;
	}
	.divTableHeading {
		font-family: Arial, Helvetica, sans-serif;
		display: table-header-group;
		font-size: 24px;
		font-weight: bold;
		
	}
	.divTableFoot {
		background-color: #FFF;
		display: table-footer-group;
		font-weight: bold;
	}
	.divTableBody {
		display: table-row-group;
	}
	.title_2 {
		font-family: Arial, Helvetica, sans-serif;
		font-size: 12px;
		color: #494c43;
		font-weight: bold;
		alignment-adjust: central;
		background-color: #494c43;
	}
	.title_3 {
		font-family: Arial, Helvetica, sans-serif;
		font-size: 18px;
		color: #494c43;
		font-weight: normal;
		text-align: center;
		background-color: #c9efd0;
	}
	
	.fecha {
		font-family: Arial, Helvetica, sans-serif;
		font-size: 16px;
		color: #494c43;
		font-weight: bolder;
		text-align: center;	
	}
	
	.fecha_ch {
		font-family: Arial, Helvetica, sans-serif;
		font-size: 14px;
		color: #494c43;
		font-weight: bolder;
		text-align: center;	
	}
	
	.fecha_ch_rojo {
		font-family: Arial, Helvetica, sans-serif;
		font-size: 14px;
		color: #ba4d4e;
		font-weight: bolder;
		text-align: center;	
	}
	
	.fecha_left {
		font-family: Arial, Helvetica, sans-serif;
		font-size: 12px;
		color: #494c43;
		font-weight: bolder;
		text-align: left;	
	}
	
	.fecha_right {
		font-family: Arial, Helvetica, sans-serif;
		font-size: 12px;
		color: #494c43;
		font-weight: bolder;
		text-align: right;	
	}
	
	.texto_simple {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 11px;
	color: #494c43;
	font-weight: bolder;
	text-align: left;
	}
	
	
	.hora {
		font-family: Arial, Helvetica, sans-serif;
		font-size: 36px;
		color: #0033CC;
		font-weight: bold;
		text-align: center;
	}
	
	.button-green {
	  /*background: #1385e5;
	  background: -webkit-linear-gradient(top, #53b2fc, #1385e5);
	  background: -moz-linear-gradient(top, #53b2fc, #1385e5);
	  background: -o-linear-gradient(top, #53b2fc, #1385e5);*/
	  padding: 8px 60px;
	  background: linear-gradient(to bottom, #75b651, #75b651);
	  border-color: #75b651;
	  color: white;
	  font-weight: bold;
	  text-shadow: 0 -1px 0 rgba(0, 0, 0, 0.4);
	}
	.button-red {
	/*background: #1385e5;
	  background: -webkit-linear-gradient(top, #53b2fc, #1385e5);
	  background: -moz-linear-gradient(top, #53b2fc, #1385e5);
	  background: -o-linear-gradient(top, #53b2fc, #1385e5);*/
	padding: 8px 60px;
	background: linear-gradient(to bottom, #cf6868, #cf6868);
	border-color: #cf6868;
	color: white;
	font-weight: bold;
	text-shadow: 0 -1px 0 rgba(0, 0, 0, 0.4);
	}
	.date {
		font-size: 16px;
	}
	
	.clock {
		font-size: 26px;
		color: #0b65c5;
	}
	
	body {
		background-color: #dee8ef;
	}
        -->
        
    </style>
</head>

    <body onload="geoFindMe()">
    <div class="divTable">
        <form name="theForm" action="<%=request.getContextPath()%>/MarcacionVirtualServlet" method="post">
            <div class="divTableBody">
            <div class="divTableRow">
            <div class="divTableHeading">&nbsp;Registrar  marcaci&oacute;n virtual en <%=nombreEmpresa%></div>
            </div>
            <div class="divTableRow">
            <div class="divTableCell">&nbsp;
            <table width="85%" height="141" border="1" align="center" cellpadding="0" cellspacing="0">
              <tbody>
            <tr>
              <td class="title_3">Reloj donde realiza el registro</td>
            </tr>
            <tr>
              <td >&nbsp;
                <table width="100%" border="0" cellpadding="0" cellspacing="0">
                    <tr>
                        <td width="26%" class="fecha_right">
                                <label for="device_id">Seleccione Reloj</label>&nbsp;
                        </td>
                        <td class="fecha_left">&nbsp;
                            <select name="device_id" id="empresaId">
                            
                                <%
                                for (String key : dispositivos.keySet()) {
                                    DispositivoVO device = dispositivos.get(key);
                                    String direccion = device.getDireccion();
                                    String comuna = device.getLabelComuna();
                                    String label = direccion + ", " + comuna;
                                %>   
                                    <option value="<%=key%>"><%=key%> - <%=label%></option>
                                <%}%>
                            </select>
                        </td>
                    </tr>
                        <tr>
                            <td width="26%" class="fecha_left">&nbsp;</td>
                            <td width="74%" class="fecha_left">&nbsp;</td>
                        </tr>
</table></td>
            </tr>
            <tr>
            <td class="title_3">Hora actual</td>
            </tr>
            <tr>
                    <td height="81"><span class="fecha">
						<div class="date"><span id="fecha_server" class="weekDay"></span></div>
                    <div class="clock">
                        <span id="hora_server" class="hours"></span>
                        <input name="form_hora_server" type="hidden" id="form_hora_server" />
                        <input name="tipoMarca" type="hidden" id="tipoMarca" />
                        <input name="latitud" type="hidden" id="latitud" />
                        <input name="longitud" type="hidden" id="longitud" />
                    </div>
                    </td>
            </tr>
            <tr>
              <td cl0ass="title_3">&nbsp;</td>
            </tr>
            <tr>
              <td class="title_3">&Uacute;ltima marcaci&oacute;n realizada</td>
            </tr>
            <tr>
              <td ><table width="65%" border="0" align="center" cellpadding="0" cellspacing="0">
                <tr>
                  <td width="25%" class="fecha"><label for="device_id2">Fecha</label>&nbsp; </td>
                  <td class="fecha">&nbsp;Hora</td>
                  <td class="fecha">Tipo</td>
                  </tr>
                
                <%
                    Set entrySet    = allMarcas.entrySet();
                    Iterator itMarcas     = entrySet.iterator();        
                    while(itMarcas.hasNext()){
                        Map.Entry item = (Map.Entry) itMarcas.next();
                        MarcaVO marca = (MarcaVO)item.getValue();
                        String fechaHora = marca.getFechaHora();
                        StringTokenizer tokenfechahora= new StringTokenizer(fechaHora," ");
                        String soloFecha= tokenfechahora.nextToken();
                        String soloHora = tokenfechahora.nextToken();
                        String tipoMarca = "Entrada";
                        if (marca.getTipoMarca() == Constantes.MARCA_SALIDA){
                            tipoMarca = "Salida";
                        }
                        %>      
                <tr>
                  <td width="25%" class="fecha_ch"><%=soloFecha%></td>
                  <td width="32%" class="fecha_ch_rojo"><%=soloHora%></td>
                  <td width="43%" class="fecha_ch"><%=tipoMarca%></td>
                  </tr>
                <%}%>  
                <tr>
                  <td width="25%" class="fecha_ch">&nbsp;</td>
                  <td width="32%" class="fecha_ch">&nbsp;</td>
                  <td width="43%" class="fecha_ch">&nbsp;</td>
                  </tr>    
                </table></td>
            </tr>
            <tr>
              <td class="texto_simple">Para registrar asistencia, presione el bot&oacute;n correspondiente al tipo de marcaci&oacute;n que desea generar. Este registro quedar&aacute; asociado al reloj seleccionado y por temas de seguridad, se guardar&aacute; la direcci&oacute;n del equipo donde realiza la marcaci&oacute;n.<br />
                <br /><br /></td>
            </tr>
            <tr>
              <td>
                <table width="100%" >
                  <tbody>
                    <tr>
                      <td align="center" style="width: 15px;"><input name="btn_entrada" type="button" class="button-green" id="btn_entrada" value="Registrar Entrada" onclick="enviarMarca(1);" /></td>
                      <td align="center" style="width: 15px;"><input name="btn_salida" type="button" class="button-red" id="btn_salida" value="Registrar Salida" onclick="enviarMarca(2);"/></td>
                      </tr>
                    </tbody>
                  </table>
                </td>
            </tr>
            </tbody>
            </table>
            </div>
            </div>
            </div>
        </form>    
    </div>
<!-- DivTable.com -->
<!--<script src="js/clock.js"></script>-->
</body>

