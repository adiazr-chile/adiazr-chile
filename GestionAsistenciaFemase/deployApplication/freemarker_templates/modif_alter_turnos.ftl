<html>
<head>
  <title>${title}</title>
	<style type="text/css">
		.page_header {
			font-family: Arial, Helvetica, sans-serif;
			font-size: 16px;
			font-style: normal;
			font-weight: bold;
			color: #039;
		}
		.table_title{
			font-family: Arial, Helvetica, sans-serif;
			font-size: 16px;
			font-style: normal;
			font-weight: bold;
			color: #039;
		}
		.report_title{
			font-family: Arial, Helvetica, sans-serif;
			font-size: 14px;
			font-style: normal;
			font-weight: bold;
			color: #039;
		}
		.table_column_header{
			font-family: Arial, Helvetica, sans-serif;
			font-size: 11px;
			font-style: normal;
			font-weight: normal;
			color: #CCC;
			background-color: #195598;
			text-align: center;
			vertical-align: middle;
			height: 20px;
		}
		.table_column_data{
			font-family: Arial, Helvetica, sans-serif;
			font-size: 11px;
			font-style: normal;
			font-weight: normal;
			height: 20px;
			vertical-align: middle;
		}
		.table_column_data_bold{
			font-family: Arial, Helvetica, sans-serif;
			font-size: 11px;
			font-style: normal;
			font-weight: bold;
			height: 20px;
			vertical-align: middle;
		}
		.table_column_var_positiva{
			font-family: Arial, Helvetica, sans-serif;
			font-size: 11px;
			font-style: normal;
			font-weight: normal;
			color: #090;
		}
		.table_column_var_negativa{
			font-family: Arial, Helvetica, sans-serif;
			font-size: 11px;
			font-style: normal;
			font-weight: normal;
			color: #F00;
		}
		.table_row {
			background-color: #e6e6e6;
			color: #000;
			vertical-align: middle;
			height: 20px;
		}

		div {
			column-count:2;
			height: 400px;
		}
		
		.footer_text{
			 font-family: Arial, Helvetica, sans-serif;
			 font-size: 9px;
			 font-style: normal;
			 font-weight: normal;
			 height: 20px;
			 vertical-align: middle;
		}
		
	</style>
</head>
<body>

<table width="100%" cellpadding="1" cellspacing="1">
 <#setting number_format=",##0">
<#setting locale="es_CL">
         <tr>
            <td colspan="2" align="center" class="page_header">${title}</td>
            <td width="28%" rowspan="2" align="right"><img src="${logo_path}" alt="logo bec" width="164" height="85" /></td>
         </tr>
         <tr>
           <td colspan="2" align="left" class="report_title">${date}</td>
         </tr>
         <tr>
           <td colspan="3" align="left" class="page_header">&nbsp;</td>
         </tr>
      </table>

<table width="100%" border="0" align="center" cellpadding="1" cellspacing="2">
  <tr class="table_column_header">
    <td width="27%">Fecha reporte</td>
    <td width="19%">RUN trabajador</td>
    <td width="54%">Nombre trabajador</td>
  </tr>
  <tr class="table_row">
    <td class="table_column_data" >${header.fechaReporte}</td>
    <td align="left" class="table_column_data">${header.rutTrabajador}&nbsp;</td>
    <td align="left" class="table_column_data">${header.nombreTrabajador}&nbsp;</td>
  </tr>
  <tr class="table_column_header">
    <td class="table_column_data" >Empresa</td>
    <td align="center" class="table_column_data">Departamento</td>
    <td align="center" class="table_column_data">Centro de costo</td>
  </tr>
  <tr class="table_row">
    <td class="table_column_data" >${header.empresaLabel}&nbsp;</td>
    <td align="left" class="table_column_data">${header.deptoLabel}</td>
    <td align="left" class="table_column_data">${header.cencoLabel}&nbsp;</td>
  </tr>
  <tr class="table_column_header">
    <td class="table_column_data" >Fecha inicio</td>
    <td align="center" class="table_column_data">Fecha fin</td>
    <td align="right" class="table_column_data">&nbsp;</td>
  </tr>
  <tr class="table_row">
    <td align="center" class="table_column_data" >${header.fechaInicio}&nbsp;</td>
    <td align="center" class="table_column_data">${header.fechaFin}&nbsp;</td>
    <td align="right" class="table_column_data">&nbsp;</td>
  </tr>
  
</table>
<span class="page_header">Registros</span><br>
<table width="100%" border="0" align="center" cellpadding="1" cellspacing="2">
    <tr class="table_column_header">
      <td width="12%">Horario</td>
      <td width="10%">Fecha asignacion de turno</td>
      <td width="14%">Nuevo Horario</td>
	  <td width="9%">Descripcion (transitorio - permanente)</td>
	</tr>
    	
        <#list details as detail>
            <tr class="table_row">
					<td class="table_column_data" >${detail.horario}&nbsp;</td>
					<td align="center" class="table_column_data">${detail.fechaAsignacionTurno}&nbsp;</td>
					<td align="center" class="table_column_data">${detail.nuevoHorario}&nbsp;</td>
					<td align="left" class="table_column_data">${detail.descripcion}&nbsp;</td>
				</tr>
            
    	</#list> 
		
</table>
<p>&nbsp;</p>
</body>
</html>

