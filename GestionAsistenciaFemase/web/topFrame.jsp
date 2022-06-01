<%@page import="cl.femase.gestionweb.vo.SolicitudPermisoAdministrativoVO"%>
<%@page import="cl.femase.gestionweb.dao.SolicitudPermisoAdministrativoDAO"%>
<%@page import="cl.femase.gestionweb.vo.PropertiesVO"%>
<%@page import="cl.femase.gestionweb.vo.SolicitudVacacionesVO"%>
<%@page import="cl.femase.gestionweb.vo.UsuarioCentroCostoVO"%>
<%@page import="java.util.List"%>
<%@page import="cl.femase.gestionweb.business.SolicitudVacacionesBp"%>
<%@page import="cl.femase.gestionweb.common.Constantes"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="cl.femase.gestionweb.vo.UsuarioVO"%>

<%
    PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
    String version      = appProperties.getVersion();
    String startYear    = appProperties.getStartYear();
    String currentYear  = appProperties.getCurrentYear();
    String labelAnios = startYear + "-" + currentYear;
    
    UsuarioVO userInSession = (UsuarioVO)session.getAttribute("usuarioObj");
    int numSolicitudesVACPendientes = 0;
    int numSolicitudesPAPendientes = 0;
        
    if (userInSession.getIdPerfil() == Constantes.ID_PERFIL_DIRECTOR 
        || userInSession.getIdPerfil() == Constantes.ID_PERFIL_DIRECTOR_TR
        || userInSession.getIdPerfil() == Constantes.ID_PERFIL_JEFE_TECNICO_NACIONAL){
    
        System.out.println("[topFrame.jsp]Usuario perfil Director/TR o Jefe Tecnico Nacional");
        System.out.println("[topFrame.jsp]Rescatar solicitudes "
            + "de vacaciones pendientes de algunos de los empleados "
            + "en alguno de los cencos del usuario");
        SolicitudVacacionesBp solicitudesVacBp = new SolicitudVacacionesBp(null);
        SolicitudPermisoAdministrativoDAO solicitudesPADao = new SolicitudPermisoAdministrativoDAO();
        /**
            - Si es perfil usuario director:
            - Rescatar todas las solicitudes de vacaciones y de permisos administrativos
             que esten pendientes. En alguno de los cencos donde el usuario es director
        */
        
        List<UsuarioCentroCostoVO> listaCencos = userInSession.getCencos();
        
        for (int i = 0; i < listaCencos.size(); i++) {
            UsuarioCentroCostoVO itcenco = listaCencos.get(i);
            
            List<SolicitudVacacionesVO> listaSolicitudesVAC = 
                solicitudesVacBp.getSolicitudes(userInSession.getEmpresaId(), 
                itcenco.getCcostoId(),
                userInSession.getRunEmpleado(),
                null,
                null,
                userInSession,
                false,
                Constantes.ESTADO_SOLICITUD_PENDIENTE,
                0, 
                0, 
                "solic_fec_ingreso");
            System.out.println("[topFrame.jsp]"
                + "cencoId= " + itcenco.getCcostoId()
                + ", numSolicitudesPendientes= " + numSolicitudesVACPendientes);
            numSolicitudesVACPendientes += listaSolicitudesVAC.size();
            
            //buscar solicitudes de permiso administrativo PENDIENTES
            List<SolicitudPermisoAdministrativoVO> listaSolicitudesPA = 
                solicitudesPADao.getSolicitudes(userInSession.getEmpresaId(), 
                itcenco.getCcostoId(),
                userInSession.getRunEmpleado(),
                null,
                null,
                userInSession,
                false,
                Constantes.ESTADO_SOLICITUD_PENDIENTE,
                0, 
                0, 
                "solic_fec_ingreso");
            System.out.println("[topFrame.jsp]"
                + "cencoId= " + itcenco.getCcostoId()
                + ", numSolicitudesPendientes= " + numSolicitudesPAPendientes);
            numSolicitudesPAPendientes += listaSolicitudesPA.size();
            
        }
        System.out.println("[topFrame.jsp]"
            + "(Final)Num solicitudes Vacaciones Pendientes= " + numSolicitudesVACPendientes);
        System.out.println("[topFrame.jsp]"
            + "(Final)Num solicitudes Permiso Administrativo Pendientes= " + numSolicitudesPAPendientes);
        
        
    }
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Top</title>
    <meta http-equiv="Content-Language" content="English" />
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <!--<link rel="stylesheet" type="text/css" href="style/style.css" media="screen" />-->
   
	<style type="text/css">
        <!--
        .notification {
	background-color: #001E5B;
	color: white;
	text-decoration: none;
	padding: 0px 26px;
	position: relative;
	display: inline-block;
	border-radius: 2px;
          }

          .notification:hover {
            background: #0080C0;
          }

          .notification .badge {
            position: absolute;
            top: -10px;
            right: -10px;
            padding: 5px 10px;
            border-radius: 50%;
            background-color: red;
            color: white;
          }
        body {
                margin-top: 0px;
                margin-bottom: 0px;
                margin-left: 0px;
                margin-right: 0px;
        }

        /* DivTable.com */
        .divTable{
                display: table;
                width: 100%;
        }
        .divTableRow {
                display: table-row;
        }
        .divTableRowBlack {
                display: table-row;
                background-color:#195598;
                color:#CCC;
        }

        .divTableHeading {
                background-color: #EEE;
                display: table-header-group;
        }
        .divTableCell, .divTableHead {
                border: 1px hidden #999999;
                display: table-cell;
                padding: 3px 10px;
                font-family:Tahoma, Geneva, sans-serif;
                font-size:12px;
        }
        .divTableCellBig {
                border: 1px hidden #999999;
                display: table-cell;
                padding: 3px 10px;
                font-family:"Trebuchet MS", Arial, Helvetica, sans-serif;
                color:#036;
                font-size:20px;
                vertical-align:middle;
        }

        .divTableHeading {
                background-color: #EEE;
                display: table-header-group;
                font-weight: bold;
        }
        .divTableFoot {
                background-color: #EEE;
                display: table-footer-group;
                font-weight: bold;
        }
        .divTableBody {
                display: table-row-group;
        }

        /* unvisited link */
        a:link {
          /*color: red;*/ 
        }

        /* visited link */
        a:visited {
          color: #4b83c1;
        }

        /* mouse over link */
        a:hover {
          color: #333;
        }

        /* selected link */
        a:active {
          color: blue;
        }
        -->
    </style>



</head>
<%
    UsuarioVO theUser	= (UsuarioVO)session.getAttribute("usuarioObj");
    String marketStatus	= (String)session.getAttribute("marketStatus");
    String logoEmpresa  = theUser.getEmpresaLogo();
    System.out.println("Logo empresa del usuario: "+logoEmpresa);
    SimpleDateFormat sdfhora	= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    java.util.Date ahora	= new java.util.Date();
%>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
    <div class="divTable">
        <div class="divTableBody">
            <div class="divTableRow">
           	  <div class="divTableCell"><img src="images/<%=logoEmpresa%>" alt="" /></div>
           	  <div class="divTableCellBig">&nbsp;</div>
           	  <div class="divTableCell"></div>
           	  <div class="divTableCell"><img src="images/logo_femase_2020.png">v<%=version%>. By FEMASE&copy; <%=labelAnios%></div>
            </div>
          <div class="divTableRowBlack">
            	<div class="divTableCell"><img src="images/icon_home.gif" alt="Home" width="13" height="12"  border="0"/>
                	<a href="<%=request.getContextPath()%>/quick_menu/quick_menu.jsp" target="mainFrame" title="Acceso rapido">Acceso r&aacute;pido</a></div>
            	<div class="divTableCell">Bienvenido&nbsp;<%=theUser.getNombreCompleto()%>,&nbsp;<%=theUser.getNomPerfil()%>
                	<%if (userInSession.getIdPerfil() == Constantes.ID_PERFIL_DIRECTOR 
                                || userInSession.getIdPerfil() == Constantes.ID_PERFIL_DIRECTOR_TR
                                || userInSession.getIdPerfil() == Constantes.ID_PERFIL_JEFE_TECNICO_NACIONAL){
                            
                            if (numSolicitudesVACPendientes > 0){
                                System.out.println("[topFrame.jsp]Mostrar notificaciones de solicitudes de vacaciones");%>
                                <a href="<%=request.getContextPath()%>/vacaciones/sol_vacaciones_aprobar_rechazar.jsp" 
                                   class="notification" target="mainFrame">
                                  <span>Solic. Vacaciones Pendientes</span>
                                  <span class="badge"><%=numSolicitudesVACPendientes%></span>
                                </a>
                            <%}%>
                            <%
                            if (numSolicitudesPAPendientes > 0){
                                System.out.println("[topFrame.jsp]Mostrar notificaciones de solicitudes de permisos administrativos");%>
                                <a href="<%=request.getContextPath()%>/permisos_administrativos/sol_pa_aprobar_rechazar.jsp" 
                                   class="notification" target="mainFrame">
                                  <span>Solic. PA Pendientes</span>
                                  <span class="badge"><%=numSolicitudesPAPendientes%></span>
                                </a>
                            <%}%>
                    <%}%>
            	</div>
            	<div class="divTableCell">Ingreso: <%=sdfhora.format(ahora)%></div>
                <div class="divTableCell"><img src="images/icon_logout.gif" width="16" height="14"  border="0"/><a href="<%=request.getContextPath()%>/UserLogout" target="_parent" title="Salir">Desconectarse [&nbsp;<%=theUser.getUsername()%>&nbsp;]</a></div>
            </div>
        </div>
</div>
</body>
</html>