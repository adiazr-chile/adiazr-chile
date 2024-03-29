<%@page import="cl.femase.gestionweb.common.Utilidades"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="cl.femase.gestionweb.dao.PermisosExamenSaludPreventivaDAO"%>
<%@page import="cl.femase.gestionweb.vo.PermisoExamenSaludPreventivaVO"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.Calendar"%>
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
    String minDate = "";
    String maxDate = "";
    String msgSaldo = ""; 
    Calendar mycal = Calendar.getInstance(new Locale("es","CL"));
    int anioActual = mycal.get(Calendar.YEAR);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String fechaActual = sdf.format(mycal.getTime());
    if (userConnected.getIdPerfil() == Constantes.ID_PERFIL_DIRECTOR 
            || userConnected.getIdPerfil() == Constantes.ID_PERFIL_DIRECTOR_TR){
                haySaldo = false;
                msgSaldo = "Para solicitar permiso Examen salud preventiva, Ud. debe ingresar al Sistema con un usuario con perfil Empleado.";
    }else{
        PermisosExamenSaludPreventivaDAO daoPESP = new PermisosExamenSaludPreventivaDAO(null);
        String runEmpleado = userConnected.getUsername();
        if (userConnected.getIdPerfil() == Constantes.ID_PERFIL_DIRECTOR 
            || userConnected.getIdPerfil() == Constantes.ID_PERFIL_DIRECTOR_TR){
                runEmpleado = userConnected.getRunEmpleado();
        }    
        
        // -------------- ----------------- ---------------------------------------------------
        System.out.println("[GestionFemaseWeb]ingresar_solicitud_pesp.jsp]"
            + "Consultar saldos en tabla permiso_examen_salud_preventiva. "
            + "EmpresaId: " + userConnected.getEmpresaId()
            + ", Run empleado: "+ runEmpleado);
        
        List<PermisoExamenSaludPreventivaVO> resumenPESP = 
            daoPESP.getResumenPermisosExamenSaludPreventiva(userConnected.getEmpresaId(), 
                runEmpleado, anioActual, -1, -1, -1, "pesp.run_empleado");
        if (resumenPESP.isEmpty()){
            System.out.println("[GestionFemaseWeb]ingresar_solicitud_pesp.jsp]"
                + "EmpresaId: " + userConnected.getEmpresaId()
                + ", rutEmpleado: " + userConnected.getUsername()
                + ", No tiene info de Resumen de Permisos Examen Salud Preventiva (saldos)");
            haySaldo = false;
            msgSaldo = "Ud. no registra informacion de Permisos Examen Salud Preventiva<strong>. Comun&iacute;quese con su Jefe Directo.";
        }else{
            PermisoExamenSaludPreventivaVO saldoPESP = resumenPESP.get(0);
            Date currentDate = new Date();
            diasDisponibles = saldoPESP.getDiasDisponibles();
            diasUtilizados  = saldoPESP.getDiasUtilizados();
            minDate = fechaActual;
            maxDate = anioActual + "-06-30";
           
            msgSaldo = "Dias disponibles: " + diasDisponibles;
            
            System.out.println("[GestionFemaseWeb]ingresar_solicitud_pesp.jsp]"
                + "EmpresaId: " + userConnected.getEmpresaId()
                + ", rutEmpleado: " + userConnected.getUsername()
                + ", dias disponibles= " + diasDisponibles
                + ", dias utilizados= " + diasUtilizados);
            
        }
        if (diasDisponibles <= 0){
            haySaldo=false;
            msgSaldo = "Ud. no tiene dias de Permisos Examen Salud Preventiva disponibles. Comun&iacute;quese con su Jefe Directo.";
        }
    }
        

%>
<html>
    <head>
        <title>Nueva Solicitud de permiso Examen Salud Preventiva</title>
        <script src="../mantencion/js/jquery-3.4.1.min.js"></script>
        
        <script type="text/javascript">
			
            <%if (!haySaldo){%>
                //No hay saldo
                $('#sinsaldo').removeClass('valid').addClass('invalid');
                $('.change_password').attr('disabled', 'disabled');
            <%}%>

            var strDesde = '';
                   
            /**
            * 
            * */
            function validate(){
                strDesde = document.getElementById("fecha").value;
                if (strDesde !== ''){	
                    document.getElementById("form1").submit();
                }else{
                    alert('Ingrese fecha');	
                }
            }//fin function
    	
            
        </script>	
                
        <link href="../jquery-plugins/datepicker/css/jquery.datepick.css"rel="stylesheet">
        
    </head>
    <body>
        <div class="wrapper">
            <h1>Nueva Solicitud de Permiso Examen Salud Preventiva</h1>
            <form id="form1" name="form1" method="post" 
                  action="<%=request.getContextPath()%>/servlet/SolicitudPESPController" target="_self">
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
                    <input type="date" id="fecha" name="fecha" 
                           min="<%=minDate%>" max="<%=maxDate%>"
                           value="" placeholder="Ingrese fecha" />
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