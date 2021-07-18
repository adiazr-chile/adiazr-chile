<%@ page import="java.util.Iterator"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Calendar"%>
<%@ page import="java.util.Locale"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="cl.bolchile.portalinf.vo.UsuarioVO"%>
<%@ page import="cl.bolchile.portalinf.vo.ModuleActionVO"%>
<%@ page import="cl.bolchile.portalinf.vo.SignerCertificateVO"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.LinkedHashMap"%>
<%@ page import="java.util.Collection"%>
	             
<% 
   LinkedHashMap<String,SignerCertificateVO> signersCertificate	= (LinkedHashMap<String,SignerCertificateVO>)session.getAttribute("signers");
   UsuarioVO theUser	= (UsuarioVO)session.getAttribute("usuarioObj");
   Calendar mycal1 = Calendar.getInstance(new Locale("es","CL"));
   SimpleDateFormat sdf	= new SimpleDateFormat("dd/MM/yyyy");
   java.util.Date ahora	= mycal1.getTime();
   
   int theyear     = mycal1.get(Calendar.YEAR);
  %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Untitled Document</title>

<!--  usados para el JQuery datepicker -->
<link rel="stylesheet" href="../style/datepicker/jquery-ui-1.10.3.custom.css" />
<script src="../js/datepicker/jquery-1.9.1.js"></script>
<script src="../js/datepicker/jquery-ui-1.10.3.custom.js"></script>
<link rel="stylesheet" href="../style/datepicker/demostyle.css" />
<!-- -->

<script type="text/javascript">
        
        
	$(function() {
		$( "#fecha_inicio" ).datepicker();
		$( "#fecha_fin" ).datepicker();
				
		$( "#fecha_inicio" ).datepicker( "option", "dateFormat", "dd/mm/yy" );
		$( "#fecha_fin" ).datepicker( "option", "dateFormat", "dd/mm/yy" );
				
		document.form1.fecha_inicio.value='<%=sdf.format(ahora)%>';
		document.form1.fecha_fin.value='<%=sdf.format(ahora)%>';
		
	});

        function winload(){
            document.form1.aceptar.disabled = true;
        }
        
	function enviarDatos(){
		//preLoaderDiv.style.visibility='visible';
		var x=document.getElementById("nemos");
		//if (x.length == 0){
			//alert('Debe ingresar al menos un nemotecnico');
		//}else{
			//var nemos=listaCreaCadena(x)+'|';
			//document.form1.listaNemos.value=nemos;
			document.form1.submit();
		//}
	}//fin ok
	
	function enableDisableFields(itemSelected){
		document.form1.year.disabled = true;
		document.form1.fecha_inicio.disabled = false;
		document.form1.fecha_fin.disabled = false;
                document.form1.aceptar.disabled = false;
		if (itemSelected=='-1'){
                    document.form1.aceptar.disabled = true;
                }else if (itemSelected=='trimestral' || itemSelected=='mensual'){
			document.form1.year.disabled = false;
			//document.form1.fecha_inicio.disabled = true;
			//document.form1.fecha_fin.disabled = true;
		}
	}
	
	function addNemo(){
		var x=document.getElementById("nemos");
		var option=document.createElement("option");
		option.text=document.form1.nemotecnico.value;
		if (document.form1.nemotecnico.value != ''){
			if (!listaExisteValor(x, document.form1.nemotecnico.value)){
				try{
					x.add(option,null);
				}catch (e){
					// for IE earlier than version 8
					x.radd(option,x.options[null]);
				}		
			}//fin if 2
		}//fin if 1
	}//fin function
	
	function listaCreaCadena(lista) {
		var cadena = "";
		for(x=0; x< lista.options.length; x++) {
			if (lista.options[x].value!="-1") 
			  cadena += lista.options[x].value + "|";
		}
		cadena = cadena.substring(0, cadena.length-1);
		return cadena;
	}//fin function
	
	function listaExisteValor(lista, valor) {
		var existe = false;
		for(x=0; x< lista.options.length; x++) {
			if (lista.options[x].value == valor) {
				existe = true;
				break;
			}//fin if
		}//fin for
		return existe;
	}//fin function
	
	function removeNemo(){
		//var x=document.getElementById("nemos");
		listaQuitarElemSel(document.form1.nemos);
	}//fin function
	
	function listaQuitarElemSel(lista) {
		listaQuitarElemento(lista, lista.selectedIndex);
	}//fin function

	function listaQuitarElemento(lista, indice){
		if (indice >=0)  {
			for(x=indice; x< lista.options.length-1; x++) {
				lista.options[x] = new Option(lista.options[x+1].text, lista.options[x+1].value);
				if(lista.options[x+1].selected)
				lista.options[x].selected=true;
			}//fin for
			lista.options.length = lista.options.length-1;
		}//fin if
	}//fin function

</script>
<link rel="stylesheet" type="text/css" href="../style/style.css" media="screen" />
<style type="text/css">
<!--
body {
	background-color: #FFF;
}


-->
</style></head>


  
<body onload="winload()">
<div id="menu">
  <div id="content">
    <div class="menu">
      <form id="form1" name="form1" method="post" action="<%=request.getContextPath()%>/RptCRCertificados" target="mainFrame2">
      <table class="menu" width="100%" border="0" align="center" cellpadding="2" cellspacing="2">
        <tr>
          <td height="37" align="right" valign="top" class="textOnly">Reportes - Certificados</td>
          <td align="right" valign="middle" class="tableHeaderSpecial">Fecha inicio (dd/MM/yyyy)&nbsp;</td>
          <td colspan="2" align="left" valign="middle"><input name="fecha_inicio" type="text" id="fecha_inicio" size="12" maxlength="10" value="<%=sdf.format(ahora)%>"/></td>
<td colspan="2" align="right" valign="middle" class="tableHeaderSpecial">Fecha final (dd/MM/yyyy)&nbsp;</td>
          <td valign="middle"><input name="fecha_fin" type="text" id="fecha_fin" size="12" maxlength="10" value="<%=sdf.format(ahora)%>"/></td>
          <td width="12%" align="right" valign="middle">&nbsp;</td>
          <td width="13%" valign="middle">&nbsp;</td>
        </tr>
        <tr>
<td width="15%" height="37" align="right" valign="top" class="tableHeaderSpecial">Tipo&nbsp;</td>
              <td width="16%" valign="top"><select name="tipo" id="tipo" onchange="enableDisableFields(this.value)">
                <option value="-1">Seleccione</option>
                <%	
                HashMap<String, ArrayList<ModuleActionVO>> 
                  appModules = theUser.getModuleActions();
                String keyModuleId="7";
                ArrayList<ModuleActionVO> actions;
                //Iterator iterator = appModules.keySet().iterator();
                actions = appModules.get(keyModuleId);//lista con acciones
                Iterator<ModuleActionVO> actionsIterator = actions.iterator();
                while (actionsIterator.hasNext()) {
                    ModuleActionVO theaction = actionsIterator.next();
                    if (theaction.getUrl()==null 
                            && theaction.getCategory()!=null 
                            && theaction.getCategory().compareTo("certificados")==0){%>
                        <option value="<%=theaction.getCode()%>"><%=theaction.getName()%></option>
                   <%}%>
                <%}//fin while (actionsIterator.hasNext()) %>
                <!--
                <option value="diario" selected="selected">Diario por nemotecnico</option>
                <option value="mensual">Mensual</option>
                <option value="trimestral">Trimestral</option>
                -->
              </select></td>
<td width="9%" colspan="2" align="right" valign="top" class="tableHeaderSpecial">Nemot&eacute;cnico&nbsp;</td>
<td colspan="2" valign="top">
              <input name="nemotecnico" type="text" id="nemotecnico" size="25" maxlength="30"/></td>
              <td valign="top"><!--<input type="button" name="aceptar2" id="aceptar2" value="Agregar" onclick="addNemo()"/>
              <input type="button" name="aceptar3" id="aceptar3" value="Quitar" onclick="removeNemo()"/>--></td>
              <td colspan="2" valign="top"><!--<select name="nemos" size="3" id="nemos" style="width:120px">
                
              </select>-->
            <input type="hidden" name="listaNemos" id="listaNemos" /></td>
</tr>
        <tr>
          <td height="37" align="right" valign="top"><h7><span class="tableHeaderSpecial">Firma</span>&nbsp;</h7></td>
          <td valign="top"><select name="signerid" id="signerid">
            <%	
                Iterator<SignerCertificateVO> it = signersCertificate.values().iterator();
                String labelname="";
                while (it.hasNext())
                {   SignerCertificateVO signer = it.next();
                    labelname = signer.getNombres()+" " +signer.getApePaterno();
                    %>
                    <option value="<%=signer.getShortName()%>"><%=labelname%></option>
             <% }%>
                
          </select></td>
          <td align="right" valign="top"><h7><span class="tableHeaderSpecial">A&ntilde;o&nbsp;</span></h7></td>
          <td align="left" valign="top"><input name="year" type="text" id="year" value="<%=theyear%>" size="5" maxlength="5" /></td>
          <td width="10%" valign="top">&deg;</td>
          <td width="10%" align="right" valign="top" class="tableHeaderSpecial">Formato Salida&nbsp;</td>
          <td width="15%" valign="top"><select name="formato" id="formato">
            <option value="pdf" selected="selected">PDF</option>
            <option value="excel">Excel</option>
                              </select></td>
          <td colspan="2" valign="top"><input type="button" name="aceptar" id="aceptar" value="Aceptar" onclick="enviarDatos()"/></td>
</tr>
        <tr>
          <td height="37" align="right" valign="top"><span class="tableHeaderSpecial">Raz&oacute;n Social</span></td>
          <td colspan="3" valign="top"><input name="razon_social" type="text" id="razon_social" size="50" maxlength="50"/></td>
<td valign="top">&nbsp;</td>
          <td align="right" valign="top" class="tableHeaderSpecial">&nbsp;</td>
          <td valign="top">&nbsp;</td>
          <td colspan="2" valign="top">&nbsp;</td>
        </tr>
</table>
      </form>
      
    </div>
    <div style="clear: both;"></div>
  </div>
</div>
</body>
</html>
