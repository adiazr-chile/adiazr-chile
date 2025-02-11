<%@page import="java.util.Date"%>
<%@page import="cl.femase.gestionweb.vo.MaintenanceEventVO"%>
<%@page import="cl.femase.gestionweb.vo.ResultCRUDVO"%>
<%@page import="cl.femase.gestionweb.business.CalculoVacacionesBp"%>
<%@page import="cl.femase.gestionweb.vo.PropertiesVO"%>
<%@page import="cl.femase.gestionweb.business.DetalleAusenciaBp"%>
<%@page import="java.util.HashMap"%>
<%@page import="cl.femase.gestionweb.common.Constantes"%>
<%@page import="cl.femase.gestionweb.vo.UsuarioVO"%>
<%@page import="cl.femase.gestionweb.business.VacacionesBp"%>
<%@page import="cl.femase.gestionweb.vo.VacacionesVO"%>
<%@page import="java.util.List"%>
<%
    UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
    PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties"); 
    double doubleSaldoVacaciones = 0;
    boolean haySaldo = true;
    String msgSaldo=""; 
    double progresivos = 0;
    CalculoVacacionesBp calculoVacacionesBp = new CalculoVacacionesBp(appProperties);
    DetalleAusenciaBp detAusenciaBp         = new DetalleAusenciaBp(appProperties);
    if (userConnected.getIdPerfil() == Constantes.ID_PERFIL_DIRECTOR 
            || userConnected.getIdPerfil() == Constantes.ID_PERFIL_DIRECTOR_TR){
                haySaldo = false;
                msgSaldo = "Para solicitar vacaciones, Ud. debe ingresar al Sistema con un usuario con perfil Empleado.";
    }else{
        VacacionesBp vacacionesBp = new VacacionesBp(null);
        String runEmpleado = userConnected.getUsername();
        if (userConnected.getIdPerfil() == Constantes.ID_PERFIL_DIRECTOR 
            || userConnected.getIdPerfil() == Constantes.ID_PERFIL_DIRECTOR_TR){
                runEmpleado = userConnected.getRunEmpleado();
        }    
            
        System.out.println("[GestionFemaseWeb.ingreso_solic_vacaciones.jsp]"
            + "Calcular saldo dias de vacaciones "
            + "para un solo empleado.");

        MaintenanceEventVO resultVO = new MaintenanceEventVO();
        resultVO.setUsername(userConnected.getUsername());
        resultVO.setDatetime(new Date());
        resultVO.setUserIP(request.getRemoteAddr());
        resultVO.setType("VAC");
        resultVO.setEmpresaIdSource(userConnected.getEmpresaId());

        //21-09-2024
        boolean vacacionesPeriodo = appProperties.isVacacionesPeriodos();
        ResultCRUDVO fnExec = null;
        System.out.println("[GestionFemaseWeb.ingreso_solic_vacaciones.jsp]"
            + "vacacionesPeriodo? " + vacacionesPeriodo);
        if (!vacacionesPeriodo){
            System.out.println("[GestionFemaseWeb.ingreso_solic_vacaciones.jsp]"
                + "Calculo VBA normal");
            fnExec = calculoVacacionesBp.setVBA_Empleado(userConnected.getEmpresaId(), runEmpleado, resultVO);
        }else{
            System.out.println("[GestionFemaseWeb.ingreso_solic_vacaciones.jsp]"
                + "Calculo VBA New. "
                + "user.empresa_id: " + userConnected.getEmpresaId()
                + ", runEmpleado: " + runEmpleado
                + ", user.depto_id: " + userConnected.getDeptoId()
                + ", user.cenco_id: " + userConnected.getCencoId());
            fnExec = calculoVacacionesBp.setVBANew(userConnected.getEmpresaId(), 
                userConnected.getDeptoId(), 
                Integer.parseInt(userConnected.getCencoId()), 
                runEmpleado, 
                resultVO);
        }
        
        if (fnExec != null && fnExec.getFilasAfectadasObj() != null){
            System.out.println("[GestionFemaseWeb.ingreso_solic_vacaciones.jsp]"
                + "Filas afectadas, post ejecucion de la funcion " + Constantes.fnSET_VBA_EMPLEADO
                + ": " + fnExec.getFilasAfectadasObj().toString());
        }
        fnExec = calculoVacacionesBp.setVP_Empleado(userConnected.getEmpresaId(), runEmpleado, resultVO);
        if (fnExec != null && fnExec.getFilasAfectadasObj() != null){
            System.out.println("[GestionFemaseWeb.ingreso_solic_vacaciones.jsp]"
                + "Filas afectadas, post ejecucion de la funcion " + Constantes.fnSET_VP_EMPLEADO
                + ": " + fnExec.getFilasAfectadasObj().toString());
        }
        System.out.println("[GestionFemaseWeb.ingreso_solic_vacaciones.jsp]"
            + "Actualizar saldos de vacaciones "
            + "en tabla detalle_ausencia "
            + "Run: "+ runEmpleado);

        detAusenciaBp.actualizaSaldosVacaciones(runEmpleado);    
            
            
        /* *********************
        System.out.println("[GestionFemaseWeb]ingresar_solicitud_vacaciones.jsp]"
            + "Actualizar saldos en tabla vacaciones. "
            + "EmpresaId: " + userConnected.getEmpresaId()
            + ", Run empleado: "+ runEmpleado);
        HashMap<String, Double> parametrosSistema = 
            (HashMap<String, Double>)session.getAttribute("parametros_sistema"); 
        vacacionesBp.calculaDiasVacaciones(userConnected.getUsername(),
            userConnected.getEmpresaId(), 
            runEmpleado, 
            parametrosSistema);
        System.out.println("[GestionFemaseWeb]ingresar_solicitud_vacaciones.jsp]"
            + "Actualizar saldos de vacaciones "
            + "en tabla detalle_ausencia "
            + "(usar nueva funcion setsaldodiasvacacionesasignadas). "
            + "Run empleado: "+ runEmpleado);
        DetalleAusenciaBp detAusenciaBp = new DetalleAusenciaBp(appProperties);
        detAusenciaBp.actualizaSaldosVacaciones(runEmpleado);
        * *********************/
        // -------------- ----------------- ---------------------------------------------------
        
        // -------------- ----------------- ---------------------------------------------------
        System.out.println("[GestionFemaseWeb]ingresar_solicitud_vacaciones.jsp]"
            + "Consultar saldos actualizados en tabla 'vacaciones'. "
            + "EmpresaId: " + userConnected.getEmpresaId()
            + ", Run empleado: "+ runEmpleado);
        
        List<VacacionesVO> infoVacaciones = 
            vacacionesBp.getInfoVacaciones(userConnected.getEmpresaId(), 
                runEmpleado, -1, -1, -1, "vac.rut_empleado");
        if (infoVacaciones.isEmpty()){
            System.out.println("[GestionFemaseWeb]ingresar_solicitud_vacaciones.jsp]"
                + "EmpresaId: " + userConnected.getEmpresaId()
                + ", rutEmpleado: " + userConnected.getUsername()
                + ", No tiene informacion de vacaciones (saldo)");
            haySaldo = false;
            msgSaldo = "Ud. no registra informacion de vacaciones<strong>. Comun&iacute;quese con su Jefe Directo.";
        }else{
            VacacionesVO saldoVacaciones = infoVacaciones.get(0);
            
            ////doubleSaldoVacaciones = saldoVacaciones.getSaldoDias();//vacaciones.saldo_dias
            ////progresivos = saldoVacaciones.getDiasProgresivos();//vacaciones.dias_progresivos
            
            doubleSaldoVacaciones = saldoVacaciones.getSaldoDiasVBA();//vacaciones.saldo_dias_vba
            progresivos = saldoVacaciones.getSaldoDiasVP();//vacaciones.saldo_dias_vp
            
            msgSaldo = "Saldo dias disponibles: " + doubleSaldoVacaciones;
            
            System.out.println("[GestionFemaseWeb]ingresar_solicitud_vacaciones.jsp]"
                + "EmpresaId: " + userConnected.getEmpresaId()
                + ", rutEmpleado: " + userConnected.getUsername()
                + ", saldoVacaciones= " + doubleSaldoVacaciones
                + ", dias_progresivos= " + progresivos);

        }
        if (doubleSaldoVacaciones <= 0){
            haySaldo=false;
            msgSaldo = "Ud. no tiene saldo de vacaciones disponible "
                + "o puede ser que se requiera efectuar un c&aacute;lculo de saldo de vacaciones." 
                + "Comun&iacute;quese con su Jefe Directo.";
        }
    }
        

%>
<html>
    <head>
        <title>Nueva Solicitud de vacaciones</title>
        <script src="../mantencion/js/jquery-3.4.1.min.js"></script>
        <!-- javascript y estilo para calendario datepicker  -->
		<script src="../jquery-plugins/datepicker/js/jquery.datepick.js"></script>
        <script src="../jquery-plugins/datepicker/js/jquery.plugin.min.js"></script>
        <script src="../jquery-plugins/datepicker/js/jquery.datepick.js"></script>
        
        <script type="text/javascript">
			
                <%if (!haySaldo){%>
                    //No hay saldo
                    $('#sinsaldo').removeClass('valid').addClass('invalid');
                    $('.change_password').attr('disabled', 'disabled');
                <%}%>

                var strDesde = '';
                var strHasta = '';
        	
            function validate(){
				  //alert('validar rango');	
				  //if (strDesde !== '' && strHasta !== ''){				 
					 // alert('strdesde: ' + strDesde + ', strhasta: ' + strHasta); 
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
								$('.change_password').removeAttr('disabled');
							 }else {
								//alert('rango no valido');
								$('#range').removeClass('valid').addClass('invalid');
								$('.change_password').attr('disabled', 'disabled');
							 }
						 //}
					 } 
				  //}//fin	
				  
				 
 			}//fin function
    	
		</script>	
        
        
        <link href="../jquery-plugins/datepicker/css/jquery.datepick.css"rel="stylesheet">
        
    </head>
    <body>
        <div class="wrapper">
            <h1>Nueva Solicitud de vacaciones</h1>
            <form id="form1" name="form1" method="post" action="<%=request.getContextPath()%>/servlet/SolicitudVacacionesController" target="_self">
                <input name="action" type="hidden" id="action" value="precreate" />
                <table width="100%" border="0" cellspacing="1" cellpadding="1">
      <tr>
        <td colspan="2"><%=msgSaldo%></td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td align="right">&nbsp;</td>
      </tr>
      <tr>
        <td align="right">Progresivos:</td>
        <td align="right"><%=progresivos%></td>
      </tr>
    </table>
                <%if (haySaldo){%>
                    <input type="text" id="fechaDesde" name="fechaDesde" value="" placeholder="Ingrese fecha de inicio" readonly/>
                    <input type="text" id="fechaHasta" name="fechaHasta" value="" placeholder="Ingrese fecha de termino" readonly/>
                    <input type="submit" class="change_password" value="Vista previa" disabled >
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
            $('#fechaDesde').datepick(
                {
                    dateFormat: 'yyyy-mm-dd',
                    //minDate: 0,
                    directionReverse: true,
                        onSelect: function(dateText) {
                          //alert("Selected date desde: " + dateText + ", Current Selected Value= " + this.value);
                          strDesde = this.value;
                          $(this).change();
                        }
            }).on("change", function() {
                //alert("Change event fec desde");
                validate();
            });
				
            $('#fechaHasta').datepick(
            {
                dateFormat: 'yyyy-mm-dd',
                //minDate: 0,
                directionReverse: true,
                onSelect: function(dateText) {
                  //alert("Selected date desde: " + dateText + ", Current Selected Value= " + this.value);
                  strHasta = this.value;
                  $(this).change();
                }
            }).on("change", function() {
                //alert("Change event fec hasta");
                validate();
            });
			
        });
		
		/*
		function DisplayDate(message) {
        	alert(message);
    	};
		*/
    
    </script>
    </body>
</html>