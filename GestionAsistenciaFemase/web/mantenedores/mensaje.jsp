<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" type="text/css" href="style/style.css" media="screen" />
<title>Untitled Document</title>

<%
	String type=(String)session.getAttribute("type");//TRD (trades), SUM(summary) o FPA (financial parameters)
	String module_id = (String)session.getAttribute("module_id");
	
	String strServletName="MaintenanceTrades";
	if (type.compareTo("SUM")==0){
		strServletName="MaintenanceStatistics";	
	}else if (type.compareTo("FPA")==0){
		strServletName="MaintenanceFinancialParameters";	
	}else if (type.compareTo("MC")==0){
		strServletName="MaintenanceMarketCapital";	
	}else if (type.compareTo("UP")==0){
		strServletName="UserProfilesMgr";	
	}else if (type.compareTo("MOD")==0){
		strServletName="ModulesMgr";
	}else if (type.compareTo("USR")==0){
		strServletName="UsersMgr";
	}else if (type.compareTo("ACC")==0){
		strServletName="ActionsModuleMgr";
	}else if (type.compareTo("ROL")==0){
		strServletName="MailListMgr";
	}else if (type.compareTo("DES")==0){
		strServletName="ContactBrokerMgr";
	}else if (type.compareTo("IDX")==0){
		strServletName="MaintenanceIndexValuesServlet";
	}else if (type.compareTo("SOC")==0){
		strServletName="MaintenanceCorporationServlet";
	}else if (type.compareTo("CCL")==0){
		strServletName="TradesDetail";
	}
	
 	String msg = (String)session.getAttribute("mante_mensaje");
	
	String msgpart		= "";
	String msgMostrar	= "";
	String auxstr		= msg;
	int partsize		= 100;
	int inipos			= 0;
	int numiter = auxstr.length() / partsize;
	if (auxstr != null){
		for (int i=0; i <= numiter; i++){
			try{
				msgpart = auxstr.substring(inipos, inipos + partsize);
			}catch(StringIndexOutOfBoundsException sbex){
				msgpart = auxstr.substring(inipos,auxstr.length());
			}
			//System.out.println("Msg[" + i + "]: " + msgpart);
			msgMostrar += msgpart+"<br>";
			inipos += partsize;
		}
	}
	//System.out.println("--->msgMostrar:\n" + msgMostrar);
	
	/*int posx=msg.indexOf("Updated values");
    int size=posx;
	int sizePart;
	int startPos=0;
	int endPos=0;
	int intdiv=msg.length() / size;
	int resto = msg.length() % size;
	if (resto>0) intdiv++;
	for (int pos=1; pos <= intdiv; pos++){
		sizePart = size*pos;
		startPos = ((size * pos) - size);
		endPos = (size * pos);
		if (endPos>msg.length()) endPos = msg.length();
		String strPart=msg.substring(startPos,endPos)+"<br>";
		System.out.println("Parte["+pos+"]: "+ strPart);
		msgMostrar+=strPart;
	}
	if (msgMostrar.compareTo("")==0){
		msgMostrar=msg;	
	}*/
	
	System.out.println("---->msgMostrar: "+ msgMostrar);
	
%>
<style type="text/css">
<!--
body {
	background-color: #FFF;
}
-->
</style></head>

<body>
<div id="wrap">
  <div id="content">
    <div class="center">
      
      <table width="389" border="0" align="center" cellpadding="1" cellspacing="1" class="tbCss1">
        <tr>
          <td height="37" align="center"><h2><a href="#">Mantenci&oacute;n de tablas</a></h2></td>
        </tr>
        <tr>
          <td width="381" height="37" align="center">&nbsp;<%=msgMostrar%></td>
        </tr>
        <tr>
        <td align="center">
        <%if (type.compareTo("ACC")==0){%>
			<a href="<%=request.getContextPath()%>/<%=strServletName%>?accion=list&tipo=<%=(String)session.getAttribute("type")%>&module_id=<%=module_id%>" 
      title="Volver" 
      target="_self">
		<%}else{%>
         <a href="<%=request.getContextPath()%>/<%=strServletName%>?accion=list&tipo=<%=(String)session.getAttribute("type")%>" 
      title="Volver" 
      target="_self">
      <%}%>
      <br />
      <h2>Volver</h2></a>        </td>
        </tr>
      </table>
      
     
      <br/>
      <br/>
    </div>
    <div style="clear: both;"></div>
  </div>
  
</div>
</body>
</html>
