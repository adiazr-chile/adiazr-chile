<%@page import="java.text.SimpleDateFormat"%>
<%@page import="cl.femase.gestionweb.vo.PermisoAdministrativoVO"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.Calendar"%>
<%@page import="cl.femase.gestionweb.dao.PermisosAdministrativosDAO"%>
<%@page import="cl.femase.gestionweb.vo.PropertiesVO"%>
<%@page import="cl.femase.gestionweb.business.DetalleAusenciaBp"%>
<%@page import="java.util.HashMap"%>
<%@page import="cl.femase.gestionweb.common.Constantes"%>
<%@page import="cl.femase.gestionweb.vo.UsuarioVO"%>
<%@page import="java.util.List"%>
<%
    UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
    PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties"); 
    double diasDisponibles  = 0;
    double diasUtilizados   = 0;
    boolean haySaldo = true;
    String msgSaldo=""; 
    Calendar mycal = Calendar.getInstance(new Locale("es","CL"));
    int anioActual = mycal.get(Calendar.YEAR);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String fechaActual = sdf.format(mycal.getTime());
    if (userConnected.getIdPerfil() == Constantes.ID_PERFIL_DIRECTOR 
            || userConnected.getIdPerfil() == Constantes.ID_PERFIL_DIRECTOR_TR){
                haySaldo = false;
                msgSaldo = "Para solicitar permiso administrativo, Ud. debe ingresar al Sistema con un usuario con perfil Empleado.";
    }else{
        PermisosAdministrativosDAO daoPA = new PermisosAdministrativosDAO(null);
        String runEmpleado = userConnected.getUsername();
        if (userConnected.getIdPerfil() == Constantes.ID_PERFIL_DIRECTOR 
            || userConnected.getIdPerfil() == Constantes.ID_PERFIL_DIRECTOR_TR){
                runEmpleado = userConnected.getRunEmpleado();
        }    
        
        // -------------- ----------------- ---------------------------------------------------
        System.out.println("[ingresar_solicitud_pa.jsp]"
            + "Consultar saldos en tabla permiso_administrativo. "
            + "EmpresaId: " + userConnected.getEmpresaId()
            + ", Run empleado: "+ runEmpleado);
        
        List<PermisoAdministrativoVO> resumenPA = 
            daoPA.getResumenPermisosAdministrativos(userConnected.getEmpresaId(), 
                runEmpleado, anioActual, -1, -1, -1, "pa.run_empleado");
        if (resumenPA.isEmpty()){
            System.out.println("[ingresar_solicitud_pa.jsp]"
                + "EmpresaId: " + userConnected.getEmpresaId()
                + ", rutEmpleado: " + userConnected.getUsername()
                + ", No tiene info de Resumen de Permisos Administrativos (saldos)");
            haySaldo = false;
            msgSaldo = "Ud. no registra informacion de Permisos Administrativos<strong>. Comun&iacute;quese con su Jefe Directo.";
        }else{
            PermisoAdministrativoVO saldoPA = resumenPA.get(0);
            
            ////doubleSaldoVacaciones = saldoVacaciones.getSaldoDias();//vacaciones.saldo_dias
            ////progresivos = saldoVacaciones.getDiasProgresivos();//vacaciones.dias_progresivos
            
            diasDisponibles = saldoPA.getDiasDisponibles();
            diasUtilizados  = saldoPA.getDiasUtilizados();
            
            msgSaldo = "Dias disponibles: " + diasDisponibles;
            
            System.out.println("[ingresar_solicitud_pa.jsp]"
                + "EmpresaId: " + userConnected.getEmpresaId()
                + ", rutEmpleado: " + userConnected.getUsername()
                + ", dias disponibles= " + diasDisponibles
                + ", dias utilizados= " + diasUtilizados);

        }
        if (diasDisponibles <= 0){
            haySaldo=false;
            msgSaldo = "Ud. no tiene dias administrativos disponibles. Comun&iacute;quese con su Jefe Directo.";
        }
    }
        

%>
<html>
    <head>
        <title>Nueva Solicitud de permiso administrativo</title>
        <script src="../mantencion/js/jquery-3.4.1.min.js"></script>
        
        <script type="text/javascript">
			
                <%if (!haySaldo){%>
                    //No hay saldo
                    $('#sinsaldo').removeClass('valid').addClass('invalid');
                    $('.change_password').attr('disabled', 'disabled');
                <%}%>

                var strDesde = '';
                var strHasta = '';
        	
            function validate(){
				var jornada = document.getElementById("jornada").value;
				strDesde = document.getElementById("fechaDesde").value;
				strHasta = document.getElementById("fechaHasta").value;
				if (jornada==='TODO_EL_DIA'){
					if (strDesde !== '' && strHasta !== ''){	
						document.getElementById("form1").submit();
					}else{
						alert('Seleccione fechas');	
					}
				}else if (jornada==='AM' || jornada==='PM'){
					if (strDesde !== ''){	
						document.getElementById("form1").submit();
					}else{
						alert('Seleccione fecha');	
					}
				}
					
				
				  //alert('validar rango');	
				  //if (strDesde !== '' && strHasta !== ''){				 
					 // alert('strdesde: ' + strDesde + ', strhasta: ' + strHasta); 
				 /*
				  
				  var auxDesde = strDesde.split("-");
				  var year  = parseInt(auxDesde[0]);
				  var month = parseInt(auxDesde[1]);
				  var day   = parseInt(auxDesde[2]);
				  month++; 
				  var desde = new Date(year,month, day);
				  
				  var auxHasta = strHasta.split("-");
				  year  = parseInt(auxHasta[0]);
				  month = parseInt(auxHasta[1]);
				  day   = parseInt(auxHasta[2]);
				  month++; 
				  var hasta = new Date(year,month, day);
				  
				 if (isNaN(desde.getTime()) && isNaN(hasta.getTime())) {
				  // Date is unreal.
				 } else {
					 //alert('desde: ' + desde + ', hasta: ' + hasta);
					 //if (strDesde !== '' && strHasta !== ''){
						 if (desde <= hasta){
							//alert('rango valido'); 
							$('#range').removeClass('invalid').addClass('valid');
							//$('.change_password').removeAttr('disabled');
							document.getElementById("form1").submit();
						 }else {
							//alert('rango no valido');
							$('#range').removeClass('valid').addClass('invalid');
							//$('.change_password').attr('disabled', 'disabled');
						 }
					 //}
				 } */
				  //}//fin	
				  
				 
 			}//fin function
    	
			function setFechaHasta(jornada){
				if (jornada === 'AM' || jornada === 'PM'){
					//habilitar fecha desde
					//alert('Habilitar solo fecha desde');
					$("#fechaDesde").attr("disabled", false);
					$("#fechaHasta").attr("disabled", true);
					//$('#fechaDesde').prop('readonly', false);
					
					//var fechaDesde = document.getElementById("fechaDesde").value;
					//if (fechaDesde !== ''){
					//	document.getElementById("fechaHasta").value = document.getElementById("fechaDesde").value;
					//}
				}else{
					//habilitar ambas fechas
					//alert('Habilitar ambas fechas');
					$("#fechaDesde").attr("disabled", false);
					$("#fechaHasta").attr("disabled", false);
					
				}
			}
        
		</script>	
        
        
        <link href="../jquery-plugins/datepicker/css/jquery.datepick.css"rel="stylesheet">
        
    </head>
    <body>
        <div class="wrapper">
            <h1>Nueva Solicitud de Permiso Administrativo</h1>
            <form id="form1" name="form1" method="post" 
                  action="<%=request.getContextPath()%>/servlet/SolicitudPermisoAdministrativoController" target="_self">
                <input name="action" type="hidden" id="action" value="precreate" />
                <table width="100%" border="0" cellspacing="1" cellpadding="1">
      <tr>
        <td colspan="2"><%=msgSaldo%></td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td align="right">&nbsp;</td>
      </tr>
      
    </table>
                <%if (haySaldo){%>
                    <select name="jornada" id="jornada" onChange="setFechaHasta(this.value)">
                      <option value="TODO_EL_DIA">TODO EL DIA</option>
                      <option value="AM">AM-MA&Ntilde;ANA</option>
                      <option value="PM">PM-TARDE</option>
                    </select>
                    
                    <input type="date" id="fechaDesde" name="fechaDesde" min="<%=fechaActual%>" value="" placeholder="Ingrese fecha de inicio" />
                    <input type="date" id="fechaHasta" name="fechaHasta" min="<%=fechaActual%>" value="" placeholder="Ingrese fecha de termino" />
                    <label for="jornada">&nbsp;</label>
                    <br>
                    <input type="button" class="change_password" value="Vista previa" onClick="validate()" >
                <%}%>
            </form>

<div id="password_details">
                <h1>La contrase&ntilde;a debe cumplir los siguientes requisitos:</h1>
                <ul>
			        <li id="range" class="invalid">Rango de fechas <strong> inv&aacute;lido</strong></li>
                    <li id="sinsaldo" class="invalid">Sin saldo</li>
                </ul>
                
            </div>

        </div>
        <style>
            body{margin: 0;background-color: #dee8ef;font-family: arial, sans-serif;}
            .wrapper{position:relative;max-width: 260px;width: 100%; margin: 40px auto; padding: 0;background-color: #fff;border-radius: 5px;}
            .wrapper > h1{text-align: center;background-color: #195598;margin: 0;color: #fff; border-top-right-radius: 5px; border-top-left-radius: 5px;font-size: 18px;padding: 15px 0;}
            .wrapper input{padding: 10px;width: 100%; margin-bottom: 15px;font-size: 12px;border: 1px solid #dedede;}
            .change_password{background-color: #66bb6a;color: #fff;border: none !important; cursor: pointer;}
            .change_password:disabled{background-color: #dedede; color:#555;}
            .wrapper form{padding: 15px;}
            #password_details{display:none; position: absolute; left:calc(100% + 20px); border-radius: 5px; top:50px; min-width: 250px; background-color:#fff;padding: 10px;font-size: 12px;}
            #password_details > h1{margin: 0;padding: 5px;font-size: 14px;color: #555;}
            #password_details > ul{margin: 0;padding: 0 30px;}
            #password_details > ul > li{padding:5px 0;}
            #password_details:before{content: "\25C0";position: absolute;top: 20px;left: -10px;font-size: 14px; line-height: 14px;color: #fff;text-shadow: none;display: block;}
            .invalid{color:#fc2e2e}
            .valid{color:#12d600}
        </style>
        
        <script type="text/javascript">
    
        $(function() {
           
        });
		
    
    </script>
    </body>
</html>