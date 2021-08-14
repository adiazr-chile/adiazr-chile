<%@page import="cl.femase.gestionweb.common.Constantes"%>
<%@page import="cl.femase.gestionweb.vo.UsuarioVO"%>
<%@page import="cl.femase.gestionweb.business.VacacionesBp"%>
<%@page import="cl.femase.gestionweb.vo.VacacionesVO"%>
<%@page import="java.util.List"%>
<%
    UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
    VacacionesBp vacacionesBp = new VacacionesBp(null);
    int intSaldoVacaciones = 0;
    boolean haySaldo = true;
    String msgSaldo="";
    String runEmpleado = userConnected.getUsername();
    if (userConnected.getIdPerfil() == Constantes.ID_PERFIL_DIRECTOR 
        || userConnected.getIdPerfil() == Constantes.ID_PERFIL_DIRECTOR_TR){
            runEmpleado = userConnected.getRunEmpleado();
    }    
    
    List<VacacionesVO> infoVacaciones = 
        vacacionesBp.getInfoVacaciones(userConnected.getEmpresaId(), 
            runEmpleado, -1, -1, -1, "vac.rut_empleado");
    if (infoVacaciones.isEmpty()){
        System.out.println("[ingresar_solicitud_vacaciones]"
            + "EmpresaId: " + userConnected.getEmpresaId()
            + ", rutEmpleado: " + userConnected.getUsername()
            + ", No tiene informacion de vacaciones (saldo)");
        haySaldo = false;
        msgSaldo = "Ud. no registra informacion de vacaciones<strong>. Comun&iacute;quese con su Jefe Directo.";
    }else{
        VacacionesVO saldoVacaciones = infoVacaciones.get(0);
        intSaldoVacaciones = saldoVacaciones.getSaldoDias();
        msgSaldo = "Saldo dias disponibles: " + intSaldoVacaciones;
        System.out.println("[ingresar_solicitud_vacaciones]"
            + "EmpresaId: " + userConnected.getEmpresaId()
            + ", rutEmpleado: " + userConnected.getUsername()
            + ", saldoVacaciones= " + intSaldoVacaciones);
        
    }
    if (intSaldoVacaciones <= 0){
        haySaldo=false;
        msgSaldo = "Ud. no tiene saldo de vacaciones disponible. Comun&iacute;quese con su Jefe Directo.";
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
				<label><%=msgSaldo%></label>
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
                    minDate: 0,
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
                minDate: 0,
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