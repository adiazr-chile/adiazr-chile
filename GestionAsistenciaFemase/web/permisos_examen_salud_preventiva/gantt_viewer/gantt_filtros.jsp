<%@page import="cl.femase.gestionweb.common.Constantes"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.Iterator"%>
<%@page import="cl.femase.gestionweb.vo.UsuarioCentroCostoVO"%>
<%@page import="java.util.List"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.Calendar"%>
<%@page import="cl.femase.gestionweb.vo.UsuarioVO"%>
<%
    UsuarioVO theUser	= (UsuarioVO)session.getAttribute("usuarioObj");
    Calendar mycal=Calendar.getInstance();
    int anioActual = mycal.get(Calendar.YEAR);
    LinkedHashMap<Integer,String> listaAnios = new LinkedHashMap<Integer,String>();
    listaAnios.put(anioActual-1, String.valueOf(anioActual-1));
    listaAnios.put(anioActual, String.valueOf(anioActual));
    listaAnios.put(anioActual+1, String.valueOf(anioActual+1));
    
    ArrayList<String> meses=new ArrayList<>();
    meses.add("Enero");
    meses.add("Febrero");
    meses.add("Marzo");
    meses.add("Abril");
    meses.add("Mayo");
    meses.add("Junio");
    meses.add("Julio");
    meses.add("Agosto");
    meses.add("Septiembre");
    meses.add("Octubre");
    meses.add("Noviembre");
    meses.add("Diciembre");
	
    //listas para realizar busquedas
    List<UsuarioCentroCostoVO> cencos   = (List<UsuarioCentroCostoVO>)session.getAttribute("cencos_empleado");
    boolean readOnly = false;
    //perfil empleado y fiscalizador solo pueden ver
    if (theUser.getIdPerfil() == Constantes.ID_PERFIL_FISCALIZADOR || theUser.getIdPerfil() == Constantes.ID_PERFIL_EMPLEADO){
        readOnly = true;
    }
%>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN"
   "http://www.w3.org/TR/html4/strict.dtd">

<html lang="en">
<head>
	<!-- estilos tablas -->
    <link href="../../Jquery-JTable/Content/normalize.css" rel="stylesheet" type="text/css" />
    <link href="../../Jquery-JTable/Content/Site.metro.css" rel="stylesheet" type="text/css" />
    <link href="../../Jquery-JTable/Content/highlight.css" rel="stylesheet" type="text/css" />
    <link href="../../Jquery-JTable/Content/themes/metroblue/jquery-ui.css" rel="stylesheet" type="text/css" />
    <link href="../../Jquery-JTable/Scripts/jtable/themes/metro/blue/jtable.css" rel="stylesheet" type="text/css" />
    <link href="../../Jquery-JTable/Scripts/syntaxhighligher/styles/shCore.css" rel="stylesheet" type="text/css" />
    <link href="../../Jquery-JTable/Scripts/syntaxhighligher/styles/shThemeDefault.css" rel="stylesheet" type="text/css" />
    
    <script src="../../Jquery-JTable/Scripts/modernizr-2.6.2.js" type="text/javascript"></script>
    <script src="../../Jquery-JTable/Scripts/jquery-1.9.1.min.js" type="text/javascript"></script>
    <script src="../../Jquery-JTable/Scripts/jquery-ui-1.10.0.min.js" type="text/javascript"></script>
    
	<style type="text/css">
		body {
			font-family: tahoma, verdana, helvetica;
			font-size: 0.8em;
			padding: 10px;
		}
	</style>
	
    <title>Gantt Vacaciones</title>
	<script type="text/javascript">
		function loadGantt(){
                    //var cencoId = document.getElementById("cencoId").value;
                    document.forms["filtrosForm"].submit();
		}
    </script>
</head>
<body>
	<form name="filtrosForm" id="filtrosForm" 
    action="<%=request.getContextPath()%>/servlet/GanttPermisoExamenSaludPreventiva?action=loadResult" 
    method="post"
    target="bottomFrame1">
        <div class="site-container">
        	<div class="main-header" style="position: relative">
                <h1>Planificaci&oacute;n de Permisos Examen Salud Preventiva</h1>
                <h2>Filtros de b&uacute;squeda</h2>
        	</div>
        	<div class="content-container">
            	<div class="padded-content-container">
                	<div class="filtering">
                    	<form>
                        <label>Centro Costo
                            <select name="cencoId" id="cencoId">
                                <option value="-1" selected>----------</option>
                                <%
                                String valueCenco="";
                                String labelCenco="";    
                                Iterator<UsuarioCentroCostoVO> iteracencos = cencos.iterator();
                                while(iteracencos.hasNext() ) {
                                    UsuarioCentroCostoVO auxCenco = iteracencos.next();
                                    valueCenco = auxCenco.getEmpresaId() + "|" + auxCenco.getDeptoId() + "|" + auxCenco.getCcostoId();
                                    labelCenco = "[" + auxCenco.getEmpresaNombre()+ "]," 
                                        + "[" + auxCenco.getDeptoNombre()+ "],"
                                        + "[" + auxCenco.getCcostoNombre()+ "]";
                                %>
                                    <option value="<%=valueCenco%>"><%=labelCenco%></option>
                                    <%
                                }
                            %>
                            </select>
                        </label>                        
                        
                        <label>A&ntilde;o:
                            <select id="paramAnio" name="paramAnio" style="width:150px;" tabindex="2" required>
                                <option value="-1">Seleccione a&ntilde;o</option>
                                <%
                                    for(Integer anioKey : listaAnios.keySet()) {
                                        String anioLabel = listaAnios.get(anioKey);
                                       %>
                                       <option value="<%=anioKey%>"><%=anioLabel%></option>
                                    <%}%>
                            </select>
                        </label>
                        
                        <label>Mes:
                            <select id="paramMes" name="paramMes" style="width:150px;" tabindex="2" required>
                                <option value="-1">Seleccione mes</option>
                                <%
                                    for(int x = 0; x < meses.size(); x++) {
                                        String numMes = String.valueOf(x+1);
                                        if ( x + 1 < 10) numMes = "0" + numMes;
                                        System.out.println("--->Itera mes: " + meses.get(x));
                                       %>
                                       <option value="<%=numMes%>"><%=meses.get(x)%></option>
                                    <%}%>
                            </select>
                        </label>
                        
                        <button type="button" id="LoadRecordsButton" onClick="loadGantt()">Buscar</button>
                        <input type="hidden" name="action" id="action">
                    	</form>
                	</div><!-- fin <div class="filtering"> -->
            	</div><!-- fin <div class="padded-content-container"> -->	
        	</div><!-- fin <div class="content-container"> -->
    	</div><!-- fin <div class="site-container"> -->
    </form>
	
</body>
</html>
