package cl.femase.gestionweb.servlet.permisos_administrativos;

import cl.femase.gestionweb.business.CentroCostoBp;
import cl.femase.gestionweb.business.DetalleAusenciaBp;
import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.business.NotificacionBp;
import cl.femase.gestionweb.business.SolicitudPermisoAdministrativoBp;
import cl.femase.gestionweb.common.ClientInfo;
import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.dao.PermisosAdministrativosDAO;
import cl.femase.gestionweb.dao.SolicitudPermisoAdministrativoDAO;
import cl.femase.gestionweb.vo.DestinatarioSolicitudVO;
import cl.femase.gestionweb.vo.DetalleAusenciaVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.MensajeUsuarioVO;
import cl.femase.gestionweb.vo.NotificacionSolicitudPermisoAdministrativoVO;
import cl.femase.gestionweb.vo.NotificacionVO;
import cl.femase.gestionweb.vo.PermisoAdministrativoVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import cl.femase.gestionweb.vo.SolicitudPermisoAdministrativoVO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.StringTokenizer;
import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

@WebServlet(name = "SolicitudPermisoAdministrativoController", urlPatterns = {"/servlet/SolicitudPermisoAdministrativoController"})
public class SolicitudPermisoAdministrativoController extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 995L;

    SimpleDateFormat anioFormat = 
        new SimpleDateFormat("yyyy", new Locale("es","CL"));    

    public SolicitudPermisoAdministrativoController() {
        
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        if (session!=null) session.removeAttribute("mensaje");else session = request.getSession();
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        if (userConnected != null){
            setResponseHeaders(response);processRequest(request, response);
        }else{
            session.setAttribute("mensaje", "Sesion de usuario "+request.getParameter("username")
                    +" no valida");
            m_logger.debug("Sesion de usuario "+request.getParameter("username")
                    +" no valida");
            request.getRequestDispatcher("/mensaje.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        if (session!=null) session.removeAttribute("mensaje");else session = request.getSession();
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        if (userConnected != null){
            setResponseHeaders(response);processRequest(request, response);
        }else{
            session.setAttribute("mensaje", "Sesion de usuario "+request.getParameter("username")
                    +" no valida");
            m_logger.debug("Sesion de usuario "+request.getParameter("username")
                    +" no valida");
            request.getRequestDispatcher("/mensaje.jsp").forward(request, response);
        }
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        HttpSession session = request.getSession(true);
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        SolicitudPermisoAdministrativoBp solicitudesBp = new SolicitudPermisoAdministrativoBp(appProperties);
        SolicitudPermisoAdministrativoDAO solicitudesDao = new SolicitudPermisoAdministrativoDAO();
        PermisosAdministrativosDAO permisoAdminDao = new PermisosAdministrativosDAO(appProperties);
        EmpleadosBp empleadosbp             = new EmpleadosBp(appProperties);
        DetalleAusenciaBp detAusenciaBp     = new DetalleAusenciaBp(appProperties);
        HashMap<String, Double> parametrosSistema = appProperties.getParametrosSistema();//HashMap<String, Double>)session.getAttribute("parametros_sistema");
        
        if(request.getParameter("action") != null){
            System.out.println("[SolicitudPermisoAdministrativoController]"
                + "action is: " + request.getParameter("action"));
            List<SolicitudPermisoAdministrativoVO> listaSolicitudes = new ArrayList<>();
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            response.setContentType("application/json");

            MaintenanceEventVO resultado = new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("SPA");//
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
            //resultado.setDeptoId(action);
            //new
            ClientInfo clientInfo = new ClientInfo();
            clientInfo.printClientInfo(request);

            /**
            * 20200229-001: Agregar info a tabla eventos: Sistema operativo y navegador
            */
            resultado.setOperatingSystem(clientInfo.getClientOS(request));
            resultado.setBrowserName(clientInfo.getClientBrowser(request));

            //Fetch Data from User Table
            int startPageIndex      = 0;
            int numRecordsPerPage   = 10;
            String jtSorting        = "solicitud.solic_fec_ingreso desc";
            
            if (request.getParameter("jtStartIndex") != null) 
                startPageIndex = Integer.parseInt(request.getParameter("jtStartIndex"));
            if (request.getParameter("jtPageSize") != null) 
                numRecordsPerPage   = Integer.parseInt(request.getParameter("jtPageSize"));
            if (request.getParameter("jtSorting") != null) 
                jtSorting   = request.getParameter("jtSorting");
            
            if (jtSorting.contains("id")) jtSorting = jtSorting.replaceFirst("id","sv.solic_id");
            else if (jtSorting.contains("estadoLabel")) jtSorting = jtSorting.replaceFirst("estadoLabel","solicitud.status_id");
            else if (jtSorting.contains("fechaInicioPA")) jtSorting = jtSorting.replaceFirst("fechaInicioPA","solicitud.solic_inicio");
                  
            //objeto usado para update/insert
            SolicitudPermisoAdministrativoVO solicitud = new SolicitudPermisoAdministrativoVO();
            
            //Filtros de busqueda
            String filtroRutEmpleado    = request.getParameter("rutEmpleado");
            String filtroInicioPA = request.getParameter("startPA");
            String filtroFinPA    = request.getParameter("endPA");
            String paramCencoID         = request.getParameter("cencoId");
            String paramEmpresa = null;
            String paramDepto   = null;
            String cencoId      = "";
            
            System.out.println("[SolicitudPermisoAdministrativoController]"
                + "token param 'cencoID'= " + paramCencoID);
            if (paramCencoID != null && paramCencoID.compareTo("-1") != 0){
                StringTokenizer tokenCenco  = new StringTokenizer(paramCencoID, "|");
                if (tokenCenco.countTokens() > 0){
                    while (tokenCenco.hasMoreTokens()){
                        paramEmpresa   = tokenCenco.nextToken();
                        paramDepto     = tokenCenco.nextToken();
                        cencoId     = tokenCenco.nextToken();
                    }
                }
            }
            
            System.out.println("[SolicitudPermisoAdministrativoController]"
                + "empresaId: " + paramEmpresa
                + ", run empleado: " + request.getParameter("rutEmpleado"));
            if(request.getParameter("id") != null){
                solicitud.setId(Integer.parseInt(request.getParameter("id")));
            }
            if(request.getParameter("empresaId") != null){
                solicitud.setEmpresaId(paramEmpresa);
            }
            if(request.getParameter("rutEmpleado") != null){
                solicitud.setRunEmpleado(request.getParameter("rutEmpleado"));
            }
            
            solicitud.setUsernameSolicita(userConnected.getUsername());
            if(request.getParameter("fechaInicioPA") != null){
                solicitud.setFechaInicioPA(request.getParameter("fechaInicioPA"));
            }
            if(request.getParameter("fechaFinPA") != null){
                solicitud.setFechaFinPA(request.getParameter("fechaFinPA"));
            }
            if(request.getParameter("notaObservacion") != null){
                solicitud.setNotaObservacion(request.getParameter("notaObservacion"));
            }
            
            String runEmpleado = userConnected.getUsername();
            System.out.println("[SolicitudPermisoAdministrativoController]"
                + "userConnected.getUsername(): " + userConnected.getUsername());
            if (userConnected.getIdPerfil() == Constantes.ID_PERFIL_DIRECTOR 
                || userConnected.getIdPerfil() == Constantes.ID_PERFIL_DIRECTOR_TR
                || userConnected.getIdPerfil() == Constantes.ID_PERFIL_JEFE_TECNICO_NACIONAL){
                    runEmpleado = null;//userConnected.getRunEmpleado();
            }
            solicitud.setRunEmpleado(runEmpleado);
            System.out.println("[SolicitudPermisoAdministrativoController]"
                + "action: " + action 
                + ", set RunEmpleado: " + solicitud.getRunEmpleado());
                    
            if (action.compareTo("listPropias") == 0) {//**********************************************************************************
                try{
                    System.out.println("[SolicitudPermisoAdministrativoController]"
                        + "Mostrar lista de solicitudes propias del usuario. "
                        + "empresa: " + paramEmpresa
                        + ", usuario: " + userConnected.getUsername()
                        + ", inicio PA: " + filtroInicioPA
                        + ", fin PA: " + filtroFinPA);
                    session.setAttribute("pEmpresaId", paramEmpresa);
                    session.setAttribute("pDeptoId", paramDepto);
                    session.setAttribute("pCencoId", cencoId);
                    int intCencoId  = -1;
                    String estado   = "TODAS";
                    if (cencoId.compareTo("") != 0) intCencoId = Integer.parseInt(cencoId); 
                    int objectsCount = 0;
                    if (intCencoId != -1){
                        listaSolicitudes = solicitudesDao.getSolicitudes(paramEmpresa, 
                            intCencoId,
                            solicitud.getRunEmpleado(),
                            filtroInicioPA,
                            filtroFinPA,
                            userConnected,
                            true,
                            estado,
                            startPageIndex, 
                            numRecordsPerPage, 
                            jtSorting);
                        
                        //Get Total Record Count for Pagination
                        objectsCount = solicitudesDao.getSolicitudesCount(paramEmpresa, 
                            solicitud.getRunEmpleado(),
                            filtroInicioPA,
                            filtroFinPA,
                            userConnected,
                            true,
                            estado);
                        session.setAttribute("solicitudesPA|"+userConnected.getUsername(), listaSolicitudes);
                        
                    }
                    //Convert Java Object to Json
                    JsonElement element = gson.toJsonTree(listaSolicitudes,
                        new TypeToken<List<SolicitudPermisoAdministrativoVO>>() {}.getType());

                    JsonArray jsonArray = element.getAsJsonArray();
                    String listData=jsonArray.toString();
                        
                    //Return Json in the format required by jTable plugin
                    listData="{\"Result\":\"OK\",\"Records\":" + 
                        listData+",\"TotalRecordCount\": " + 
                        objectsCount + "}";
                    response.getWriter().print(listData);
                    //request.getRequestDispatcher("/mantenedores/mantenedoresFrmSet.jsp").forward(request, response);
                }catch(IOException | NumberFormatException ex){
                    String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getMessage()+"}";
                    response.getWriter().print(error);
                    ex.printStackTrace();
                }   
            }else if (action.compareTo("listAdmin") == 0 || action.compareTo("listDirector") == 0) {//**********************************************************************************
                try{
                    session.setAttribute("pEmpresaId", paramEmpresa);
                    session.setAttribute("pDeptoId", paramDepto);
                    session.setAttribute("pCencoId", cencoId);
                    
                    int intCencoId=-1;
                    if (cencoId.compareTo("") != 0) intCencoId = Integer.parseInt(cencoId); 
                    System.out.println("[SolicitudPermisoAdministrativoController]"
                        + "Mostrar " + action + " - lista de solicitudes. "
                        + "empresa: " + paramEmpresa
                        + ", cencoId: " + intCencoId
                        + ", usuario: " + userConnected.getUsername()
                        + ", inicio PA: " + filtroInicioPA
                        + ", fin PA: " + filtroFinPA);
                    int objectsCount = 0;
                    String estado="TODAS";
                    if (action.compareTo("listDirector") == 0) estado=Constantes.ESTADO_SOLICITUD_PENDIENTE; 
                    if (intCencoId != -1){
                        listaSolicitudes = solicitudesDao.getSolicitudes(paramEmpresa, 
                            intCencoId,
                            solicitud.getRunEmpleado(),
                            filtroInicioPA,
                            filtroFinPA,
                            userConnected,
                            false,
                            estado,
                            startPageIndex, 
                            numRecordsPerPage, 
                            jtSorting);
                        //Get Total Record Count for Pagination
                        objectsCount = solicitudesDao.getSolicitudesCount(paramEmpresa, 
                            solicitud.getRunEmpleado(),
                            filtroInicioPA,
                            filtroFinPA,
                            userConnected,
                            false,
                            estado);
                    }else {
                        listaSolicitudes = solicitudesDao.getSolicitudesAprobarRechazar(paramEmpresa, 
                            userConnected.getRunEmpleado(),
                            filtroInicioPA,
                            filtroFinPA,
                            userConnected,
                            estado,
                            false,
                            userConnected.getCencos(),
                            startPageIndex, 
                            numRecordsPerPage, 
                            jtSorting);
                        //Get Total Record Count for Pagination
                        objectsCount = solicitudesDao.getSolicitudesAprobarRechazarCount(paramEmpresa, 
                            userConnected.getRunEmpleado(),
                            filtroInicioPA,
                            filtroFinPA,
                            userConnected,
                            estado,
                            false,
                            userConnected.getCencos());
                    }
                    
                    session.setAttribute("solicitudesPA|"+userConnected.getUsername(), listaSolicitudes);
                    
                    //Convert Java Object to Json
                    JsonElement element = gson.toJsonTree(listaSolicitudes,
                        new TypeToken<List<SolicitudPermisoAdministrativoVO>>() {}.getType());

                    JsonArray jsonArray = element.getAsJsonArray();
                    String listData=jsonArray.toString();
                        
                    //Return Json in the format required by jTable plugin
                    listData="{\"Result\":\"OK\",\"Records\":" + 
                        listData+",\"TotalRecordCount\": " + 
                        objectsCount + "}";
                    response.getWriter().print(listData);
                    //request.getRequestDispatcher("/mantenedores/mantenedoresFrmSet.jsp").forward(request, response);
                }catch(IOException | NumberFormatException ex){
                    String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getMessage()+"}";
                    response.getWriter().print(error);
                    ex.printStackTrace();
                }   
            }else if (action.compareTo("precreate") == 0) {//**********************************************************************************  
                    Calendar cal = Calendar.getInstance(new Locale("es","CL"));
                    Date fechaActual = cal.getTime();    
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("es","CL"));
                    
                    String strFechaHoraActual = sdf.format(fechaActual);
                    String reqDesde = request.getParameter("fechaDesde");
                    String reqHasta = request.getParameter("fechaHasta");
                    String jornada  = request.getParameter("jornada");
             
                    solicitud.setUsernameSolicita(userConnected.getUsername());
                    solicitud.setEmpresaId(userConnected.getEmpresaId());
                    solicitud.setFechaIngreso(strFechaHoraActual);
                    solicitud.setEstadoId(Constantes.ESTADO_SOLICITUD_PENDIENTE);
                    solicitud.setEstadoLabel(Constantes.ESTADO_SOLICITUD_PENDIENTE_LABEL);
                    solicitud.setFechaInicioPA(reqDesde);
                    solicitud.setFechaFinPA(reqHasta);
                    solicitud.setJornada(jornada);
                    solicitud.setAnio(cal.get(Calendar.YEAR));
                    
                    System.out.println("[SolicitudPermisoAdministrativoController]"
                        + "Vista previa antes de Insertar solicitud de Permiso Administrativo. "
                        + "Username: " + userConnected.getUsername()
                        + ", empresaId: " + solicitud.getEmpresaId()
                        + ", run_empleado: " + solicitud.getRunEmpleado()
                        + ", inicio_PA: " + solicitud.getFechaInicioPA()
                        + ", fin_PA: " + solicitud.getFechaFinPA()
                        + ", jornada: " + solicitud.getJornada()
                        + ", anio: " + solicitud.getAnio());
                    EmpleadoVO infoEmpleado = empleadosbp.getEmpleado(solicitud.getEmpresaId(), 
                        solicitud.getRunEmpleado());
                    
                    //VacacionesBp vacacionesBp = new VacacionesBp(null);
                    if (solicitud.getJornada().compareTo("TODO_EL_DIA") == 0){
                        int diasEfectivosSolicitados = 
                            permisoAdminDao.getDiasEfectivos(solicitud.getFechaInicioPA(), 
                                solicitud.getFechaFinPA(), 
                                solicitud.getEmpresaId(), 
                                solicitud.getRunEmpleado());
                        solicitud.setDiasSolicitados(diasEfectivosSolicitados);
                    }else{
                        solicitud.setDiasSolicitados(0.5);
                    }
                                        
                    Calendar mycal = Calendar.getInstance(new Locale("es","CL"));
                    int anioActual = mycal.get(Calendar.YEAR);
    
                    double doubleSaldoPADisponible = 0;
                    List<PermisoAdministrativoVO> infoPA = 
                        permisoAdminDao.getResumenPermisosAdministrativos(userConnected.getEmpresaId(), 
                            solicitud.getRunEmpleado(),anioActual, -1, -1, -1, "pa.run_empleado");
                    if (!infoPA.isEmpty()){
                        PermisoAdministrativoVO saldoPA = infoPA.get(0);
                        doubleSaldoPADisponible = saldoPA.getDiasDisponibles();
                    }
                    
                    double saldoPostPA = doubleSaldoPADisponible - solicitud.getDiasSolicitados();
                    System.out.println("[SolicitudPermisoAdministrativoController]"
                        + "[precreate]empresaId: : " + solicitud.getEmpresaId()    
                        + ", run_empleado: : " + solicitud.getRunEmpleado()
                        + ", saldo_PA_disponible: : " + doubleSaldoPADisponible
                        + ", diasSolicitados: : " + solicitud.getDiasSolicitados()
                        + ", saldoPostPA: : " + saldoPostPA);
                    
                    ArrayList<DestinatarioSolicitudVO> destinatarios = 
                        getDestinatariosSolicitud(solicitud);
                    String strDestinatarios = "";
                    for (int i=0;i<destinatarios.size();i++) {
                        DestinatarioSolicitudVO destinatario = destinatarios.get(i);
                        strDestinatarios += destinatario.getNombre() 
                            + "[" + destinatario.getEmail() + "],"
                            + "[" + destinatario.getCargo() + "],";
                        
                        System.out.println("[SolicitudPermisoAdministrativoController]"
                            + "strDestinatarios: " + strDestinatarios);
                    }
                    if (!destinatarios.isEmpty()) strDestinatarios = strDestinatarios.substring(0, strDestinatarios.length()-1);
                    
                    ArrayList<MensajeUsuarioVO> mensajes = new ArrayList<>();
                    
                    mensajes.add(new MensajeUsuarioVO("RUN trabajador", infoEmpleado.getCodInterno()));
                    mensajes.add(new MensajeUsuarioVO("Nombre trabajador", infoEmpleado.getNombreCompleto()));
                    mensajes.add(new MensajeUsuarioVO("Nombre de usuario solicitante", solicitud.getUsernameSolicita()));
                    mensajes.add(new MensajeUsuarioVO("Institucion", infoEmpleado.getEmpresaNombre()));
                    mensajes.add(new MensajeUsuarioVO("Centro de costo", infoEmpleado.getCencoNombre()));
                    mensajes.add(new MensajeUsuarioVO("Fecha/Hora solicitud", sdf.format(fechaActual)));
                    mensajes.add(new MensajeUsuarioVO("Inicio Permiso Administrativo", solicitud.getFechaInicioPA()));
                    mensajes.add(new MensajeUsuarioVO("Termino Permiso Administrativo", solicitud.getFechaFinPA()));
                    mensajes.add(new MensajeUsuarioVO("Jornada", jornada));
                    mensajes.add(new MensajeUsuarioVO("A??o", "" + solicitud.getAnio()));
                    mensajes.add(new MensajeUsuarioVO("Dias solicitados", "" + solicitud.getDiasSolicitados()));
                    
                    if (solicitud.getDiasSolicitados() > doubleSaldoPADisponible){
                        MensajeUsuarioVO msgError = new MensajeUsuarioVO("Observaci??n", 
                            "Los dias solicitados superan los dias disponibles "
                            + "(" + doubleSaldoPADisponible + ")");
                        msgError.setType("ERROR");
                        mensajes.add(msgError);
                        request.setAttribute("ingresarPA", false);
                    }else{
                        request.setAttribute("ingresarPA", true);
                        mensajes.add(new MensajeUsuarioVO("Saldo Dias Post Permiso Administrativo", "" + saldoPostPA));
                    }
                    
//                    DetalleAusenciaVO newAusencia = new DetalleAusenciaVO();
//                    newAusencia.setEmpresaId(solicitud.getEmpresaId());
//                    newAusencia.setRutEmpleado(solicitud.getRunEmpleado());
//                    newAusencia.setDiasSolicitados(Double.parseDouble(String.valueOf(diasEfectivosSolicitados)));
//                    newAusencia.setSaldoPostPA(saldoPostPA);

                    mensajes.add(new MensajeUsuarioVO("Destinatario(s)", strDestinatarios));
                    request.setAttribute("fechaDesde", reqDesde);
                    request.setAttribute("fechaHasta", reqHasta);
                    request.setAttribute("jornada", jornada);
                    request.setAttribute("diasEfectivosSolicitados", "" + solicitud.getDiasSolicitados());
                    request.setAttribute("saldoPostPA", "" + saldoPostPA);
                    
                    request.setAttribute("mensajes", mensajes);
                    
                    request.getRequestDispatcher("/permisos_administrativos/vista_previa.jsp").forward(request, response);//like mensaje.jsp
            }
            else if (action.compareTo("create") == 0) {//**********************************************************************************  
                    Calendar cal = Calendar.getInstance(new Locale("es","CL"));
                    Date fechaActual = cal.getTime();    
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("es","CL"));
                    String strFechaHoraActual = sdf.format(fechaActual);
                    String reqDesde = request.getParameter("fechaDesde");
                    String reqHasta = request.getParameter("fechaHasta");
                    String jornada  = request.getParameter("jornada");
                    
                    String diasEfectivosSolicitados = request.getParameter("diasEfectivosSolicitados");
                    String saldoPostPA = request.getParameter("saldoPostPA");
                    
                    solicitud.setUsernameSolicita(userConnected.getUsername());
                    solicitud.setEmpresaId(userConnected.getEmpresaId());
                    solicitud.setFechaIngreso(strFechaHoraActual);
                    solicitud.setEstadoId(Constantes.ESTADO_SOLICITUD_PENDIENTE);
                    solicitud.setEstadoLabel(Constantes.ESTADO_SOLICITUD_PENDIENTE_LABEL);
                    solicitud.setFechaInicioPA(reqDesde);
                    solicitud.setFechaFinPA(reqHasta);
                    solicitud.setJornada(jornada);
                    solicitud.setAnio(cal.get(Calendar.YEAR));
                    solicitud.setDiasSolicitados(Double.parseDouble(diasEfectivosSolicitados));
                    
                    System.out.println("[SolicitudPermisoAdministrativoController]"
                        + "Insertar solicitud de Permiso Administrativo. "
                        + "Username: " + userConnected.getUsername()
                        + ", empresaId: " + solicitud.getEmpresaId()    
                        + ", rut_empleado: " + solicitud.getRunEmpleado()
                        + ", inicio_permiso administrativo: " + solicitud.getFechaInicioPA()
                        + ", fin_permiso administrativo: " + solicitud.getFechaFinPA()
                        + ", dias solicitados: " + solicitud.getDiasSolicitados()    
                        + ", jornada: " + solicitud.getJornada()
                        + ", anio: " + solicitud.getAnio());
                    EmpleadoVO infoEmpleado = empleadosbp.getEmpleado(solicitud.getEmpresaId(), 
                        solicitud.getRunEmpleado());
                    resultado.setEmpresaId(infoEmpleado.getEmpresa().getId());
                    resultado.setDeptoId(infoEmpleado.getDepartamento().getId());
                    resultado.setCencoId(infoEmpleado.getCentroCosto().getId());
                    
                    //***************************************************************
                    System.out.println("[SolicitudPermisoAdministrativoController]"
                        + "Insertar registro en tabla notificaciones, "
                        + "para enviar mail");
                    NotificacionSolicitudPermisoAdministrativoVO evento = notificaEventoSolicitud("INGRESO_SOLICITUD",
                        "Ingreso de Solicitud de Permiso Administrativo", 
                        solicitud, userConnected, request,null,null,solicitud.getDiasSolicitados());
                    //solicitud.setDiasSolicitados(evento.getDiasSolicitados());
                    String mensajeFinal = evento.getMensajeFinal();
                    session.setAttribute("mensaje", "Solicitud de Permiso Administrativo ingresada exitosamente."
                        + "<p>" + mensajeFinal);
                    MaintenanceVO doCreate = solicitudesBp.insert(solicitud, resultado);					
                    listaSolicitudes.add(solicitud);
                    //************************************************************
                    //************************************************************
                    ArrayList<DestinatarioSolicitudVO> destinatarios = 
                        getDestinatariosSolicitud(solicitud);
                    String strDestinatarios = "";
                    for (int i=0;i<destinatarios.size();i++) {
                        DestinatarioSolicitudVO destinatario = destinatarios.get(i);
                        strDestinatarios += destinatario.getNombre() 
                            + "[" + destinatario.getEmail() + "],"
                            + "[" + destinatario.getCargo() + "],";
                        System.out.println("[SolicitudPermisoAdministrativoController]"
                            + "strDestinatarios: " + strDestinatarios);
                    }
                    if (!destinatarios.isEmpty()) strDestinatarios = strDestinatarios.substring(0, strDestinatarios.length()-1);
                    
                    ArrayList<MensajeUsuarioVO> mensajes = new ArrayList<>();
                    
                    mensajes.add(new MensajeUsuarioVO("RUN trabajador", infoEmpleado.getCodInterno()));
                    mensajes.add(new MensajeUsuarioVO("Nombre trabajador", infoEmpleado.getNombreCompleto()));
                    mensajes.add(new MensajeUsuarioVO("Nombre de usuario solicitante", solicitud.getUsernameSolicita()));
                    mensajes.add(new MensajeUsuarioVO("Institucion", infoEmpleado.getEmpresaNombre()));
                    mensajes.add(new MensajeUsuarioVO("Centro de costo", infoEmpleado.getCencoNombre()));
                    mensajes.add(new MensajeUsuarioVO("Fecha/Hora solicitud", sdf.format(fechaActual)));
                    mensajes.add(new MensajeUsuarioVO("Inicio Permiso Administrativo", solicitud.getFechaInicioPA()));
                    mensajes.add(new MensajeUsuarioVO("Termino Permiso Administrativo", solicitud.getFechaFinPA()));
                    mensajes.add(new MensajeUsuarioVO("Jornada", solicitud.getJornada()));
                    mensajes.add(new MensajeUsuarioVO("A??o", "" + cal.get(Calendar.YEAR)));
                                                
                    //mensajes.add(new MensajeUsuarioVO("Dias solicitados", "" + diasEfectivosSolicitados));
                    
                    DetalleAusenciaVO newAusencia = new DetalleAusenciaVO();
                    newAusencia.setEmpresaId(solicitud.getEmpresaId());
                    newAusencia.setRutEmpleado(solicitud.getRunEmpleado());
                    newAusencia.setDiasSolicitados(Double.parseDouble(diasEfectivosSolicitados));
                    
//                    VacacionesBp vacaciones = new VacacionesBp(appProperties);
//                    DiasEfectivosVacacionesVO objDE = vacaciones.getDesgloseDiasVacaciones(newAusencia);
//                    mensajes.add(new MensajeUsuarioVO("Dias efectivos VBA", "" + objDE.getDiasEfectivosVBA()));
//                    mensajes.add(new MensajeUsuarioVO("Dias efectivos VP", "" + objDE.getDiasEfectivosVP()));
//                    mensajes.add(new MensajeUsuarioVO("Saldo VBA Pre Vacaciones", "" + objDE.getSaldoVBAPreVacaciones()));
//                    mensajes.add(new MensajeUsuarioVO("Saldo VP Pre Vacaciones", "" + objDE.getSaldoVPPreVacaciones()));
//                    mensajes.add(new MensajeUsuarioVO("Saldo VBA Post Vacaciones", "" + objDE.getSaldoVBAPostVacaciones()));
//                    mensajes.add(new MensajeUsuarioVO("Saldo VP Post Vacaciones", "" + objDE.getSaldoVPPostVacaciones()));
                    
                    //mensajes.add(new MensajeUsuarioVO("Saldo post vacaciones", "" + saldoPostVacaciones));
                    
                    mensajes.add(new MensajeUsuarioVO("Destinatario(s)", strDestinatarios));
                    request.setAttribute("mensajes", mensajes);
                    request.getRequestDispatcher("/permisos_administrativos/solicitud_confirmada.jsp").forward(request, response);
            }else if (action.compareTo("updatePropias") == 0) {//**********************************************************************************  
                    Calendar cal = Calendar.getInstance(new Locale("es","CL"));
                    Date fechaActual = cal.getTime();    
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("es","CL"));
                    String strFechaHoraActual = sdf.format(fechaActual);
                    String strAccion = request.getParameter("Accion");
                    System.out.println("[SolicitudPermisoAdministrativoController]"
                        + "Modificar estado de solicitud de Permiso Administrativo. "
                        + "Id solicitud: : " + solicitud.getId()    
                        + ", username: : " + userConnected.getUsername()
                        + ", empresaId: : " + solicitud.getEmpresaId()    
                        + ", rut_empleado: : " + solicitud.getRunEmpleado()
                        + ", accion a realizar: " + strAccion);

                    EmpleadoVO infoEmpleado = empleadosbp.getEmpleado(solicitud.getEmpresaId(), 
                        solicitud.getRunEmpleado());
                    resultado.setEmpresaId(infoEmpleado.getEmpresa().getId());
                    resultado.setDeptoId(infoEmpleado.getDepartamento().getId());
                    resultado.setCencoId(infoEmpleado.getCentroCosto().getId());
                    MaintenanceVO doCreate = new MaintenanceVO();
                    if (strAccion.compareTo(Constantes.ESTADO_SOLICITUD_CANCELADA) == 0){
                        solicitud.setFechaHoraCancela(strFechaHoraActual);
                        doCreate = 
                            solicitudesBp.cancelarSolicitud(solicitud.getId(), 
                                userConnected.getUsername(), 
                                strFechaHoraActual, 
                                resultado);
                        solicitud.setEstadoId("C");
                        solicitud.setEstadoLabel(Constantes.ESTADO_SOLICITUD_CANCELADA_LABEL);
                        
                    }					
                    listaSolicitudes.add(solicitud);

                    //Convert Java Object to Json
                    String json=gson.toJson(solicitud);					
                    //Return Json in the format required by jTable plugin
                    String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";
                    if (doCreate.isThereError()) listData = "{\"Result\":\"ERROR\",\"Message\":"+doCreate.getMsgError()+"}";
                    response.getWriter().print(listData);
                }else if (action.compareTo("apruebaRechaza") == 0) {//**********************************************************************************  
                    Calendar cal = Calendar.getInstance(new Locale("es","CL"));
                    Date fechaActual = cal.getTime();    
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("es","CL"));
                    String strFechaHoraActual = sdf.format(fechaActual);
                    String strAccion = request.getParameter("Accion");
                    
                    System.out.println("[SolicitudPermisoAdministrativoController]"
                        + "Aprobar/Rechazar solicitud de permiso administrativo. "
                        + "Id solicitud: : " + solicitud.getId());
                    
                    SolicitudPermisoAdministrativoVO solicitudFromBd = 
                        solicitudesDao.getSolicitudByKey(solicitud.getId());
                    
                    System.out.println("[SolicitudPermisoAdministrativoController]"
                        + "Aprobar/Rechazar solicitud de permiso administrativo. "
                        + "Id solicitud: : " + solicitud.getId()
                        + ", inicio permiso administrativo: " + solicitudFromBd.getFechaInicioPA()
                        + ", fin permiso administrativo: " + solicitudFromBd.getFechaFinPA()
                        + ", dias Solicitados: " + solicitudFromBd.getDiasSolicitados()    
                        + ", username: : " + userConnected.getUsername()
                        + ", empresaId: : " + solicitudFromBd.getEmpresaId()    
                        + ", run_empleado: : " + solicitudFromBd.getRunEmpleado()
                        + ", accion a realizar: " + strAccion
                        + ", nota_observacion: " + solicitud.getNotaObservacion());

                    solicitud.setDiasSolicitados(solicitudFromBd.getDiasSolicitados());
                    
                    EmpleadoVO infoEmpleado = empleadosbp.getEmpleado(solicitudFromBd.getEmpresaId(), 
                        solicitudFromBd.getRunEmpleado());
                    resultado.setEmpresaId(infoEmpleado.getEmpresa().getId());
                    resultado.setDeptoId(infoEmpleado.getDepartamento().getId());
                    resultado.setCencoId(infoEmpleado.getCentroCosto().getId());
                    MaintenanceVO doCreate = new MaintenanceVO();
                    String usernameApruebaRechaza = userConnected.getUsername();
                    if (strAccion.compareTo(Constantes.ESTADO_SOLICITUD_APROBADA) == 0){
                        /**
                        * Antes de aprobar una solicitud de Permiso administrativo, 
                        * se debe verificar que no haya conflicto con otra ausencia existente.
                        */
                        //validar ausencias conflicto
                        ArrayList<DetalleAusenciaVO> ausenciasConflicto = 
                            detAusenciaBp.getAusenciasConflicto(solicitudFromBd.getRunEmpleado(),
                            false,
                            solicitudFromBd.getFechaInicioPA(),
                            solicitudFromBd.getFechaFinPA(), 
                            null, 
                            null);
                        if (ausenciasConflicto.isEmpty()){
                            System.out.println("[SolicitudPermisoAdministrativoController]"
                                + "No hay conflicto. "
                                + "Se procede a Aprobar solicitud "
                                + "y a Insertar permiso administrativo...");
                            //No hay conflicto con ausencias existentes
                            solicitud.setFechaHoraApruebaRechaza(strFechaHoraActual);
                            doCreate = 
                                solicitudesBp.aprobarSolicitud(solicitud.getId(), 
                                    usernameApruebaRechaza, 
                                    strFechaHoraActual,
                                    solicitud.getNotaObservacion(),
                                    resultado);
                            solicitud.setEstadoId(Constantes.ESTADO_SOLICITUD_APROBADA);
                            solicitud.setEstadoLabel(Constantes.ESTADO_SOLICITUD_APROBADA_LABEL);
                            notificaEventoSolicitud("SOLICITUD_APROBADA",
                                "Solicitud de permiso administrativo Aprobada", 
                                solicitudFromBd, userConnected, 
                                request, 
                                solicitudFromBd.getFechaInicioPA(), 
                                solicitudFromBd.getFechaFinPA(),
                                solicitudFromBd.getDiasSolicitados());
                            
                            //*********************************************************
                            System.out.println("[SolicitudPermisoAdministrativoController]"
                                + "Aprobar solicitud. Insertar permiso administrativo."
                                + " Usuario.username: " + userConnected.getUsername()
                                + " Usuario.Run: " + userConnected.getRunEmpleado());
                            
                            SolicitudPermisoAdministrativoVO auxSolicitud = 
                                solicitudesDao.getSolicitudByKey(solicitud.getId());
                            //Insertar Permiso Administrativo
                            DetalleAusenciaVO newAusencia;
                            newAusencia = new DetalleAusenciaVO();
                            newAusencia.setEmpresaId(solicitudFromBd.getEmpresaId());
                            newAusencia.setRutEmpleado(solicitudFromBd.getRunEmpleado());
                            newAusencia.setFechaInicioAsStr(auxSolicitud.getFechaInicioPA());
                            newAusencia.setFechaFinAsStr(auxSolicitud.getFechaFinPA());
                            //fijos
                            newAusencia.setIdAusencia(Constantes.ID_AUSENCIA_PERMISO_ADMINISTRATIVO);
                            newAusencia.setRutAutorizador(userConnected.getRunEmpleado());
                            newAusencia.setAusenciaAutorizada("S");
                            newAusencia.setPermiteHora("N");
                            newAusencia.setDiasSolicitados(solicitud.getDiasSolicitados());
                            MaintenanceVO insertResult = new MaintenanceVO();
                            insertResult = 
                                insertarPermisoAdministrativo(request, 
                                    appProperties, 
                                    parametrosSistema, 
                                    userConnected, 
                                    newAusencia);
                            
                        }else{
                            /**
                            * Hay conflicto con ausencias existentes: Rechazar Solicitud de Permiso Administrativo. 
                            */
                            System.err.println("[DetalleAusenciaController]"
                                + "Hay conflicto con ausencias existentes...");
                            request.setAttribute("msgerror", "Ausencia conflicto");
                            String mensajeFinal= "Hay conflicto con ausencias existentes...";
                            mensajeFinal += Utilidades.getAusenciasConflictoStr(ausenciasConflicto);
                            solicitud.setNotaObservacion(mensajeFinal);
                            solicitud.setFechaHoraApruebaRechaza(strFechaHoraActual);
                            
                            doCreate = 
                                solicitudesBp.rechazarSolicitud(solicitud.getId(), 
                                    usernameApruebaRechaza, 
                                    strFechaHoraActual,solicitud.getNotaObservacion(),
                                    resultado);
                            solicitud.setEstadoId(Constantes.ESTADO_SOLICITUD_RECHAZADA);
                            solicitud.setEstadoLabel(Constantes.ESTADO_SOLICITUD_RECHAZADA_LABEL);
                            System.out.println("[SolicitudPermisoAdministrativoController]"
                                + "Rechazar solicitud de Permiso Administrativo. "
                                + "Dias solicitados: " + solicitud.getDiasSolicitados());
                            notificaEventoSolicitud("SOLICITUD_RECHAZADA",
                                "Solicitud de Permiso Administrativo Rechazada", 
                                solicitudFromBd, userConnected, request,
                                solicitudFromBd.getFechaInicioPA(), 
                                solicitudFromBd.getFechaFinPA(),
                                solicitudFromBd.getDiasSolicitados());
                        }
                    }else if (strAccion.compareTo(Constantes.ESTADO_SOLICITUD_RECHAZADA) == 0){
                        solicitud.setFechaHoraApruebaRechaza(strFechaHoraActual);
                        doCreate = 
                            solicitudesBp.rechazarSolicitud(solicitud.getId(), 
                                usernameApruebaRechaza, 
                                strFechaHoraActual,solicitud.getNotaObservacion(),
                                resultado);
                        solicitud.setEstadoId(Constantes.ESTADO_SOLICITUD_RECHAZADA);
                        solicitud.setEstadoLabel(Constantes.ESTADO_SOLICITUD_RECHAZADA_LABEL);
                        System.out.println("[SolicitudPermisoAdministrativoController]"
                            + "Rechazar solicitud de Permiso Administrativo. "
                            + "Dias solicitados: " + solicitud.getDiasSolicitados());
                        notificaEventoSolicitud("SOLICITUD_RECHAZADA",
                            "Solicitud de Permiso Administrativo Rechazada", 
                            solicitudFromBd, userConnected, request,
                            solicitudFromBd.getFechaInicioPA(), 
                            solicitudFromBd.getFechaFinPA(),
                            solicitudFromBd.getDiasSolicitados());
                    }					
                    listaSolicitudes.add(solicitud);

                    //Convert Java Object to Json
                    String json=gson.toJson(solicitud);					
                    //Return Json in the format required by jTable plugin
                    String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";
                    if (doCreate.isThereError()) listData = "{\"Result\":\"ERROR\",\"Message\":"+doCreate.getMsgError()+"}";
                    response.getWriter().print(listData);

                }
            
        }
    }
    
    /**
    * Insertar ausencia del tipo 'Permiso administrativo'
    */
    private MaintenanceVO insertarPermisoAdministrativo(HttpServletRequest _request,
            PropertiesVO _appProperties,
            HashMap<String, Double> _parametrosSistema,
            UsuarioVO _userConnected, 
            DetalleAusenciaVO _ausencia){
       
        Calendar mycal = Calendar.getInstance(new Locale("es","CL"));
        int anioActual = mycal.get(Calendar.YEAR);
        
        System.out.println("[SolicitudPermisoAdministrativoController."
            + "insertarPermisoAdministrativo]"
            + "Insertar detalle ausencia (PERMISO ADMINISTRATIVO). "
            + "EmpresaId: " + _ausencia.getEmpresaId()
            + ", Run empleado: " + _ausencia.getRutEmpleado()
            + ", Anio: " + anioActual);
        PermisosAdministrativosDAO permisosDao = new PermisosAdministrativosDAO(_appProperties);
        double doubleSaldoPADisponible = 0;
        double doubleSaldoPAUtilizados = 0;
        List<PermisoAdministrativoVO> infoPA = 
            permisosDao.getResumenPermisosAdministrativos(_ausencia.getEmpresaId(), 
                _ausencia.getRutEmpleado(),anioActual, -1, -1, -1, "pa.run_empleado");
        if (!infoPA.isEmpty()){
            PermisoAdministrativoVO saldoPA = infoPA.get(0);
            doubleSaldoPADisponible = saldoPA.getDiasDisponibles();
            doubleSaldoPAUtilizados = saldoPA.getDiasUtilizados();
        }
        double saldoDiasUtilizadosPostPA = doubleSaldoPAUtilizados + _ausencia.getDiasSolicitados();            
        double saldoDisponiblePostPA = doubleSaldoPADisponible - _ausencia.getDiasSolicitados();
        _ausencia.setSaldoPostPA(saldoDisponiblePostPA);
        
        String mensajeFinal = null;
        DetalleAusenciaBp ausenciasBp   = new DetalleAusenciaBp(_appProperties);
                
        MaintenanceEventVO resultado=new MaintenanceEventVO();
        resultado.setUsername(_userConnected.getUsername());
        resultado.setDatetime(new Date());
        resultado.setUserIP(_request.getRemoteAddr());
        resultado.setType("DAU");
        resultado.setEmpresaIdSource(_userConnected.getEmpresaId());
                                        
        resultado.setRutEmpleado(_ausencia.getRutEmpleado());
        System.out.println("[SolicitudPermisoAdministrativoController.insertarPermisoAdministrativo]"
            + "Insertar detalle ausencia (PERMISO ADMINISTRATIVO)");
        MaintenanceVO doCreate = ausenciasBp.insertaPermisoAdministrativo(_ausencia, resultado);
        
        if (doCreate.isThereError()){
            mensajeFinal= "Error al insertar Permiso administrativo: " + doCreate.getMsgError();
            doCreate.setMsg(mensajeFinal);
        }else{
             System.out.println("[SolicitudPermisoAdministrativoController."
                + "insertarPermisoAdministrativo]"
                + "Actualizar resumen de Permisos Administrativos");
             /*
             permisosDao.getResumenPermisosAdministrativos(_ausencia.getEmpresaId(), 
                _ausencia.getRutEmpleado(),anioActual
             */
             PermisoAdministrativoVO objPA = new PermisoAdministrativoVO();
             objPA.setEmpresaId(_ausencia.getEmpresaId());
             objPA.setRunEmpleado(_ausencia.getRutEmpleado());
             objPA.setAnio(anioActual);
             objPA.setDiasDisponibles(saldoDisponiblePostPA);
             objPA.setDiasUtilizados(saldoDiasUtilizadosPostPA);
             MaintenanceVO resultadoUpdate = permisosDao.updateResumenPA(objPA);
             
             
        }
        
        System.out.println("[SolicitudPermisoAdministrativoController."
            + "insertarPermisoAdministrativo]"
            + "Saliendo del metodo OK.");
        return doCreate;
    }
    
    /**
    * 
    * 
    */
    private NotificacionSolicitudPermisoAdministrativoVO notificaEventoSolicitud(
            String _tipoEvento, 
            String _evento,
            SolicitudPermisoAdministrativoVO _solicitud,
            UsuarioVO _userConnected, 
            HttpServletRequest _request,
            String _inicioPermisoAdministrativo,
            String _finPermisoAdministrativo,
            double _diasSolicitados){
    
        NotificacionSolicitudPermisoAdministrativoVO evento = 
            new NotificacionSolicitudPermisoAdministrativoVO();
                
        EmpleadosBp empleadobp      = new EmpleadosBp();
        CentroCostoBp cencosbp      = new CentroCostoBp(null);
        PermisosAdministrativosDAO permisoAdminDao   = new PermisosAdministrativosDAO(null);
        
        System.out.println("[SolicitudPermisoAdministrativoController."
            + "notificaEventoSolicitud]Invocar getEmpleado. "
            + "EmpresaId: " + _solicitud.getEmpresaId()
            + ", rutEmpleado: " + _solicitud.getRunEmpleado());
                
        EmpleadoVO empleado = 
            empleadobp.getEmpleado(_solicitud.getEmpresaId(), 
            _solicitud.getRunEmpleado());
        Calendar cal            = Calendar.getInstance(new Locale("es","CL"));
        Date fechaActual        = cal.getTime();    
        SimpleDateFormat sdf    = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("es","CL"));
        String fromLabel        = "Gestion asistencia";
        String fromMail         = m_properties.getKeyValue("mailFrom");
        String asuntoMail       = "Sistema de Gestion-" + _evento;
        String mailTo           = empleado.getEmail();
        System.out.println("[SolicitudPermisoAdministrativoController."
            + "notificaEventoSolicitud]"
            + "Email por defecto: " + mailTo);
        String notaObservacion = _request.getParameter("notaObservacion");
        String cadenaNombres    = "";
        boolean hayJefeNacional = true;
        boolean hayJefeDirecto  = true;
        double diasSolicitados     = 0;
        
        if (_tipoEvento.compareTo("SOLICITUD_APROBADA") == 0 || _tipoEvento.compareTo("SOLICITUD_RECHAZADA") == 0){
            _solicitud.setFechaInicioPA(_inicioPermisoAdministrativo);
            _solicitud.setFechaFinPA(_finPermisoAdministrativo);
            diasSolicitados = _diasSolicitados;
        }
        
        if (_tipoEvento.compareTo("INGRESO_SOLICITUD") == 0){
            String cadenaEmails = "";
            System.out.println("[SolicitudPermisoAdministrativoController."
                + "notificaEventoSolicitud]"
                + "Cargo empleado: " + empleado.getIdCargo()
                + ", empresaId: " + empleado.getEmpresaId()
                + ", deptoId: " + empleado.getDeptoId()
                + ", cencoId: " + empleado.getCencoId());
            if (empleado.getIdCargo() != Constantes.ID_CARGO_DIRECTOR){
                //obtener correo de notficacion: email del o los directores del cenco
                //enviar solicitud a su jefe directo (Cargo Director, ID 22).
                List<EmpleadoVO> directoresCenco = 
                    cencosbp.getDirectoresCenco(empleado.getEmpresaId(), empleado.getDeptoId(), empleado.getCencoId());
                for(EmpleadoVO itDirectores : directoresCenco){
                    cadenaEmails += itDirectores.getEmail() + ",";
                    cadenaNombres += itDirectores.getNombres()+ ",";
                    if (itDirectores.getApePaterno() != null) cadenaNombres += " " + itDirectores.getApePaterno();
                    if (itDirectores.getApeMaterno() != null) cadenaNombres += " " + itDirectores.getApeMaterno();
                }
                if (!directoresCenco.isEmpty()){
                    //quitar ultima Coma
                    cadenaEmails = cadenaEmails.substring(0, cadenaEmails.length() - 1);
                    mailTo      = cadenaEmails;
                    cadenaNombres = cadenaNombres.substring(0, cadenaNombres.length() - 1);
                }else{
                    System.out.println("[SolicitudPermisoAdministrativoController."
                        + "notificaEventoSolicitud]No se encontraron "
                        + "empleados con Cargo 'DIRECTOR' para "
                        + "[empresa, depto, cencoId] = "
                            + "[" 
                                + empleado.getEmpresaId() 
                                + "," + empleado.getDeptoId()  
                                + "," + empleado.getCencoId() + 
                            "]");
                    hayJefeDirecto = false;
                }
            }else{
                //Usuario con cargo Director, debe enviar solicitud a Gerencia en Admin Central (Cargo Jefe T??cnico Nacional, ID 69 )
                System.out.println("[SolicitudPermisoAdministrativoController."
                    + "notificaEventoSolicitud]Empleado con cargo Director, "
                    + "debe enviar solicitud a Gerencia en Admin Central (Cargo Jefe T??cnico Nacional, ID 69 )");
                EmpleadoVO filtroEmpleado = new EmpleadoVO();
                filtroEmpleado.setEmpresaId(empleado.getEmpresaId());
                filtroEmpleado.setEstado(1);
                filtroEmpleado.setIdCargo(Constantes.ID_CARGO_JEFE_TECNICO_NACIONAL);
                filtroEmpleado.setEmpleadoVigente(true);
                filtroEmpleado.setArticulo22(true);
                filtroEmpleado.setCencoId(-1);
                List<EmpleadoVO> listaJefesTecnicosNacional = 
                    empleadobp.getEmpleadosByFiltro(filtroEmpleado);
                if (!listaJefesTecnicosNacional.isEmpty()){
                    System.out.println("[SolicitudPermisoAdministrativoController."
                        + "notificaEventoSolicitud]Rescatar emails del Jefe Tecnico Nacional");
                    for(EmpleadoVO itJefazos : listaJefesTecnicosNacional){
                        cadenaEmails += itJefazos.getEmail() + ",";
                        cadenaNombres += itJefazos.getNombres()+ ",";
                        if (itJefazos.getApePaterno() != null) cadenaNombres += " " + itJefazos.getApePaterno();
                        if (itJefazos.getApeMaterno() != null) cadenaNombres += " " + itJefazos.getApeMaterno();
                    }
                    System.out.println("[SolicitudPermisoAdministrativoController."
                        + "notificaEventoSolicitud]Emails Jefe Tecnico Nacional: " + cadenaEmails);
                    System.out.println("[SolicitudPermisoAdministrativoController."
                        + "notificaEventoSolicitud]Nombres Jefe Tecnico Nacional: " + cadenaNombres);
                    
                    cadenaEmails = cadenaEmails.substring(0, cadenaEmails.length() - 1);
                    mailTo      = cadenaEmails;
                }else{
                    System.out.println("[SolicitudPermisoAdministrativoController."
                        + "notificaEventoSolicitud]No se encontraron "
                        + "empleados con Cargo 'JEFE TECNICO NACIONAL' ");
                    hayJefeNacional = false;
                }
            }
        }
        
        //VacacionesBp vacacionesBp = new VacacionesBp(null);
        //int diasSolicitados = _solicitud.getDiasEfectivosVacacionesSolicitadas();
        
//        int diasSolicitados = vacacionesBp.getDiasEfectivos(_solicitud.getInicioVacaciones(), 
//            _solicitud.getFinVacaciones(), "N");
        
        if (_solicitud.getDiasSolicitados() == 0){
            diasSolicitados = 
                permisoAdminDao.getDiasEfectivos(_solicitud.getFechaInicioPA(), 
                    _solicitud.getFechaFinPA(),  
                    _solicitud.getEmpresaId(), 
                    _solicitud.getRunEmpleado());
        }
        
        String mensaje = "RUN trabajador: " + empleado.getCodInterno()
            + "<br>Nombre trabajador: " + empleado.getNombreCompleto()
            + "<br>Nombre de usuario solicitante: " + _solicitud.getUsernameSolicita()    
            + "<br>Institucion: " + empleado.getEmpresaNombre()
            + "<br>Centro de costo: " + empleado.getCencoNombre()
            + "<br>Fecha/Hora solicitud: " + sdf.format(fechaActual)
            + "<br>Inicio Permiso Administrativo: " + _solicitud.getFechaInicioPA()
            + "<br>Termino Permiso Administrativo: " + _solicitud.getFechaFinPA()
            + "<br>Dias solicitados: " + diasSolicitados;
        
        String mailBody = "Evento:" + _evento
            + "<br>RUN trabajador: " + empleado.getCodInterno()
            + "<br>Nombre trabajador: " + empleado.getNombreCompleto()
            + "<br>Nombre de usuario solicitante: " + _solicitud.getUsernameSolicita()    
            + "<br>Institucion: " + empleado.getEmpresaNombre()
            + "<br>Centro de costo: " + empleado.getCencoNombre()
            + "<br>Fecha/Hora solicitud: " + sdf.format(fechaActual)
            + "<br>Inicio Permiso Administrativo: " + _solicitud.getFechaInicioPA()
            + "<br>Termino Permiso Administrativo: " + _solicitud.getFechaFinPA()
            + "<br>Dias solicitados: " + diasSolicitados
            + "<br>Nota/Observacion: " + notaObservacion;
        
        evento.setTipoEvento(_tipoEvento);
        evento.setMensajeFinal(mensaje);
        evento.setRunTrabajador(empleado.getCodInterno());
        evento.setNombreTrabajador(empleado.getNombreCompleto());
        evento.setInstitucion(empleado.getEmpresaNombre());
        evento.setCentroCosto(empleado.getCencoNombre());
        evento.setFechaHoraSolicitud(sdf.format(fechaActual));
        evento.setInicioPermisoAdministrativo(_solicitud.getFechaInicioPA());
        evento.setTerminoPermisoAdministrativo(_solicitud.getFechaFinPA());
        evento.setDiasSolicitados(diasSolicitados);
        
        MaintenanceEventVO resultado=new MaintenanceEventVO();
        resultado.setUsername(_userConnected.getUsername());
        resultado.setDatetime(new Date());
        resultado.setUserIP(_request.getRemoteAddr());
        resultado.setType("NOT");
        resultado.setEmpresaIdSource(_userConnected.getEmpresaId());
        resultado.setEmpresaId(empleado.getEmpresaId());
        resultado.setDeptoId(empleado.getDeptoId());
        resultado.setCencoId(empleado.getCencoId());
        resultado.setRutEmpleado(empleado.getRut());
        
        System.out.println("[SolicitudPermisoAdministrativoController."
            + "notificaEventoSolicitud]"
            + "EmpresaId: " + resultado.getEmpresaId()
            + ", deptoId: " + resultado.getDeptoId()
            + ", cencoId: " + resultado.getCencoId()
            + ", mailTo: " + mailTo);
        
        NotificacionBp notificacionBp=new NotificacionBp(null);
        NotificacionVO notificacion = new NotificacionVO();
        notificacion.setEmpresaId(empleado.getEmpresaId());
        notificacion.setCencoId(empleado.getCencoId());
        notificacion.setRutEmpleado(empleado.getRut());
        notificacion.setMailFrom(fromMail);
        notificacion.setMailTo(mailTo);
        notificacion.setMailSubject(asuntoMail);
        notificacion.setMailBody(mailBody);
        notificacion.setUsername(_userConnected.getUsername());
        notificacion.setComentario(_evento);
        notificacionBp.insert(notificacion, resultado);
        
        if (empleado.getIdCargo() != Constantes.ID_CARGO_DIRECTOR){
            if (hayJefeDirecto){
                mensaje += "<br>Nombre Jefe Directo: " + cadenaNombres;
                mensaje += "<br>Email Jefe Directo: " + mailTo;
            }else{
                mensaje += "<br>No hay Jefe Directo definido";
            }
        }else{
            if (hayJefeNacional){
                mensaje += "<br>Nombre Jefe Directo: " + cadenaNombres;
                mensaje += "<br>Email Jefe Directo: " + mailTo;
            }else{
                mensaje += "<br>No hay Jefe Nacional definido";
            }
        }
        return evento;
    }
    
    /**
    * Obtiene destinatario(s) de la solicitud de Permiso administrativo
    */
    private ArrayList<DestinatarioSolicitudVO> getDestinatariosSolicitud(SolicitudPermisoAdministrativoVO _solicitud){
        CentroCostoBp cencosbp      = new CentroCostoBp(null);
        EmpleadosBp empleadobp      = new EmpleadosBp();
        EmpleadoVO empleado         = 
            empleadobp.getEmpleado(_solicitud.getEmpresaId(), 
            _solicitud.getRunEmpleado());    
        ArrayList<DestinatarioSolicitudVO> destinatarios = new ArrayList<>();
        String strNombre= "";
        String strEmail = "";
        String strCargo = "";
        String mailTo               = empleado.getEmail();
        boolean hayJefeNacional     = true;
        boolean hayJefeDirecto      = true;
        
        System.out.println("[SolicitudPermisoAdministrativoController."
            + "getDestinatarioSolicitud]"
            + "Cargo empleado: " + empleado.getIdCargo()
            + ", empresaId: " + empleado.getEmpresaId()
            + ", deptoId: " + empleado.getDeptoId()
            + ", cencoId: " + empleado.getCencoId());
        if (empleado.getIdCargo() != Constantes.ID_CARGO_DIRECTOR){
            //obtener correo de notficacion: email del o los directores del cenco
            //enviar solicitud a su jefe directo (Cargo Director, ID 22).
            List<EmpleadoVO> directoresCenco = 
                cencosbp.getDirectoresCenco(empleado.getEmpresaId(), empleado.getDeptoId(), empleado.getCencoId());
            for(EmpleadoVO itDirectores : directoresCenco){
                strEmail    = itDirectores.getEmail();
                strNombre   = itDirectores.getNombres();
                strCargo    = itDirectores.getNombreCargo();
                if (itDirectores.getApePaterno() != null) strNombre += " " + itDirectores.getApePaterno();
                if (itDirectores.getApeMaterno() != null) strNombre += " " + itDirectores.getApeMaterno();
                System.out.println("[SolicitudPermisoAdministrativoController."
                    + "getDestinatarioSolicitud]add destinatario. "
                    + "strNombre: " + strNombre
                    + ", strEmail: " + strEmail
                    + ", strCargo: " + strCargo);
                DestinatarioSolicitudVO destinatario = 
                    new DestinatarioSolicitudVO(strNombre, strEmail, strCargo);
                //add destinatarios
                destinatarios.add(destinatario);
            }
            if (directoresCenco.isEmpty()){
                System.out.println("[SolicitudPermisoAdministrativoController."
                    + "getDestinatarioSolicitud]No se encontraron "
                    + "empleados con Cargo 'DIRECTOR' para "
                    + "[empresa, depto, cencoId] = "
                        + "[" 
                            + empleado.getEmpresaId() 
                            + "," + empleado.getDeptoId()  
                            + "," + empleado.getCencoId() + 
                        "]");
                hayJefeDirecto = false;
            }
        }else{
            //Usuario con cargo Director, debe enviar solicitud a Gerencia en Admin Central (Cargo Jefe T??cnico Nacional, ID 69 )
            System.out.println("[SolicitudPermisoAdministrativoController."
                + "getDestinatarioSolicitud]Usuario con cargo Director, "
                + "debe enviar solicitud a Gerencia en Admin Central "
                + "(Cargo Jefe T??cnico Nacional, ID 69 )");
            EmpleadoVO filtroEmpleado = new EmpleadoVO();
            filtroEmpleado.setEmpresaId(empleado.getEmpresaId());
            filtroEmpleado.setEstado(1);
            filtroEmpleado.setIdCargo(Constantes.ID_CARGO_JEFE_TECNICO_NACIONAL);
            filtroEmpleado.setEmpleadoVigente(true);
            filtroEmpleado.setArticulo22(true);
            filtroEmpleado.setCencoId(-1);
            List<EmpleadoVO> listaJefesTecnicosNacional = 
                empleadobp.getEmpleadosByFiltro(filtroEmpleado);
            if (!listaJefesTecnicosNacional.isEmpty()){
                for(EmpleadoVO itJefazos : listaJefesTecnicosNacional){
                    strEmail    = itJefazos.getEmail();
                    strNombre   = itJefazos.getNombres();
                    strCargo    = itJefazos.getNombreCargo();
                    if (itJefazos.getApePaterno() != null) strNombre += " " + itJefazos.getApePaterno();
                    if (itJefazos.getApeMaterno() != null) strNombre += " " + itJefazos.getApeMaterno();
                    DestinatarioSolicitudVO destinatario = new DestinatarioSolicitudVO(strNombre, strEmail, strCargo);
                    destinatarios.add(destinatario);
                }
            }else{
                System.out.println("[SolicitudPermisoAdministrativoController."
                    + "getDestinatarioSolicitud]No se encontraron "
                    + "empleados con Cargo 'JEFE TECNICO NACIONAL' ");
                hayJefeNacional = false;
            }
        }
        
        /*
        if (empleado.getIdCargo() != Constantes.ID_CARGO_DIRECTOR){
            if (hayJefeDirecto){
                destinatario.setNombres(cadenaNombres);//mensaje += "<br>Nombre Jefe Directo: " + cadenaNombres;
                destinatario.setEmails(mailTo);//mensaje += "<br>Email Jefe Directo: " + mailTo;
            }
        }else{
            if (hayJefeNacional){
                destinatario.setNombres(cadenaNombres);
                destinatario.setEmails(mailTo);
            }
        }
        */
        return destinatarios;
    }
    
    
}
