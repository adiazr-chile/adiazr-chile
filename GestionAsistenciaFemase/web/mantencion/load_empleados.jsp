<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" type="text/css" href="style/style.css" media="screen" />
<title>Untitled Document</title>

<script language="javascript">
    function volver(){
        document.getElementById("redirectForm").submit();
        
    }
</script>

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
          <td height="37" align="center"><h2>Load empleados</h2></td>
        </tr>
        <tr>
          <td width="381" height="37" align="center">
              <button type="button" class="btn btn-primary btn-lg" style="z-index: 1;" 
                    onClick="volver()">Volver</button>
          </td>
        </tr>
        <tr>
            <td align="center">
                <form name="redirectForm" id="redirectForm"  method="post" action="<%=request.getContextPath()%>/mantencion/lista_empleados.jsp">  
    <input type="hidden" name="cco" id="cco" value="emp01|05|38">  
</form>
                
            </td>
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
