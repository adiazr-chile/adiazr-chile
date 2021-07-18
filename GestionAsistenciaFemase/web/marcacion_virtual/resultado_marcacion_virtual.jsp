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
    String nombreEmpresa = userInSession.getEmpresaNombre();
    boolean isOk = (Boolean)request.getAttribute("isOk");
    String message = (String)request.getAttribute("resultMessage");
%>    

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Marcacion Virtual Empleado</title>

<script type="text/javascript">
	
	function fnVolver(){
		document.location.href='<%=request.getContextPath()%>/quick_menu/quick_menu.jsp';
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
</style></head>

<body>
    <div class="divTable">
        <form name="theForm" action="<%=request.getContextPath()%>/MarcacionVirtualServlet" method="post">
            <div class="divTableBody">
            <div class="divTableRow">
            <div class="divTableHeading">&nbsp;Registro de   marcaci&oacute;n virtual en <%=nombreEmpresa%></div>
            </div>
            <div class="divTableRow">
            <div class="divTableCell">&nbsp;
            <table width="85%" height="119" border="1" align="center" cellpadding="0" cellspacing="0">
              <tbody>
            <tr>
              <td class="title_3"><%=message%></td>
            </tr>
            <tr>
              <td height="21" >&nbsp;</td>
            </tr>
            <tr>
            <td class="title_3"><input type="button" name="volver" id="volver" value="Volver" onclick="fnVolver();"/></td>
            </tr>
            </tbody>
            </table>
            </div>
            </div>
            </div>
        </form>    
    </div>
<!-- DivTable.com -->
<script src="js/clock.js"></script>
</body>
</html>
