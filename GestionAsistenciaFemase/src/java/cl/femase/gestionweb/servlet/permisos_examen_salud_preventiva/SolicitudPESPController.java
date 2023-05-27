package cl.femase.gestionweb.servlet.permisos_examen_salud_preventiva;

import cl.femase.gestionweb.business.CentroCostoBp;
import cl.femase.gestionweb.business.DetalleAusenciaBp;
import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.business.NotificacionBp;
import cl.femase.gestionweb.business.SolicitudPESPBp;
import cl.femase.gestionweb.common.ClientInfo;
import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.dao.PermisosExamenSaludPreventivaDAO;
import cl.femase.gestionweb.dao.SolicitudPermisoExamenSaludPreventivaDAO;
import cl.femase.gestionweb.vo.DestinatarioSolicitudVO;
import cl.femase.gestionweb.vo.DetalleAusenciaVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.MensajeUsuarioVO;
import cl.femase.gestionweb.vo.NotificacionSolicitudPESPVO;
import cl.femase.gestionweb.vo.NotificacionVO;
import cl.femase.gestionweb.vo.PermisoExamenSaludPreventivaVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import cl.femase.gestionweb.vo.SolicitudPermisoExamenSaludPreventivaVO;
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
import java.math.BigDecimal;
import java.text.ParseException;
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

@WebServlet(name = "SolicitudPESPController", urlPatterns = {"/servlet/SolicitudPESPController"})
public class SolicitudPESPController extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 995L;

    SimpleDateFormat anioFormat = 
        new SimpleDateFormat("yyyy", new Locale("es","CL"));    

    public SolicitudPESPController() {
        
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

        SolicitudPESPBp solicitudesBp = new SolicitudPESPBp(appProperties);
        SolicitudPermisoExamenSaludPreventivaDAO solicitudesDao = new SolicitudPermisoExamenSaludPreventivaDAO();
        PermisosExamenSaludPreventivaDAO pespDao = new PermisosExamenSaludPreventivaDAO(appProperties);
        EmpleadosBp empleadosbp             = new EmpleadosBp(appProperties);
        DetalleAusenciaBp detAusenciaBp     = new DetalleAusenciaBp(appProperties);
        HashMap<String, Double> parametrosSistema = appProperties.getParametrosSistema();
        
        if(request.getParameter("action") != null){
            System.out.println(WEB_NAME+"[SolicitudPESPController]"
                + "action is: " + request.getParameter("action"));
            List<SolicitudPermisoExamenSaludPreventivaVO> listaSolicitudes = new ArrayList<>();
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            response.setContentType("application/json");

            MaintenanceEventVO resultado = new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("SPESP");//
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
            SolicitudPermisoExamenSaludPreventivaVO solicitud = new SolicitudPermisoExamenSaludPreventivaVO();
            
            //Filtros de busqueda
            String filtroRutEmpleado    = request.getParameter("rutEmpleado");
            String filtroInicioPESP = request.getParameter("startPESP");
            String filtroFinPESP    = request.getParameter("endPESP");
            String paramCencoID         = request.getParameter("cencoId");
            String paramEmpresa = null;
            String paramDepto   = null;
            String cencoId      = "";
            
            System.out.println(WEB_NAME+"[SolicitudPESPController]"
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
            
            System.out.println(WEB_NAME+"[SolicitudPESPController]"
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
            
            if(request.getParameter("notaObservacion") != null){
                solicitud.setNotaObservacion(request.getParameter("notaObservacion"));
            }
            
            String runEmpleado = userConnected.getUsername();
            System.out.println(WEB_NAME+"[SolicitudPESPController]"
                + "userConnected.getUsername(): " + userConnected.getUsername());
            if (userConnected.getIdPerfil() == Constantes.ID_PERFIL_DIRECTOR 
                || userConnected.getIdPerfil() == Constantes.ID_PERFIL_DIRECTOR_TR
                || userConnected.getIdPerfil() == Constantes.ID_PERFIL_JEFE_TECNICO_NACIONAL){
                    runEmpleado = null;//userConnected.getRunEmpleado();
            }
            solicitud.setRunEmpleado(runEmpleado);
            System.out.println(WEB_NAME+"[SolicitudPESPController]"
                + "action: " + action 
                + ", set RunEmpleado: " + solicitud.getRunEmpleado());
                    
            if (action.compareTo("listPropias") == 0) {//**********************************************************************************
                try{
                    System.out.println(WEB_NAME+"[SolicitudPESPController]"
                        + "Mostrar lista de solicitudes propias del usuario. "
                        + "empresa: " + paramEmpresa
                        + ", usuario: " + userConnected.getUsername()
                        + ", inicio PESP: " + filtroInicioPESP
                        + ", fin PESP: " + filtroFinPESP);
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
                            filtroInicioPESP,
                            filtroFinPESP,
                            userConnected,
                            true,
                            estado,
                            startPageIndex, 
                            numRecordsPerPage, 
                            jtSorting);
                        
                        //Get Total Record Count for Pagination
                        objectsCount = solicitudesDao.getSolicitudesCount(paramEmpresa, 
                            solicitud.getRunEmpleado(),
                            filtroInicioPESP,
                            filtroFinPESP,
                            userConnected,
                            true,
                            estado);
                        session.setAttribute("solicitudesPESP|"+userConnected.getUsername(), listaSolicitudes);
                        
                    }
                    //Convert Java Object to Json
                    JsonElement element = gson.toJsonTree(listaSolicitudes,
                        new TypeToken<List<SolicitudPermisoExamenSaludPreventivaVO>>() {}.getType());

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
                    System.out.println(WEB_NAME+"[SolicitudPESPController]"
                        + "Mostrar " + action + " - lista de solicitudes. "
                        + "empresa: " + paramEmpresa
                        + ", cencoId: " + intCencoId
                        + ", usuario: " + userConnected.getUsername()
                        + ", inicio PESP: " + filtroInicioPESP
                        + ", fin PESP: " + filtroFinPESP);
                    int objectsCount = 0;
                    String estado="TODAS";
                    if (action.compareTo("listDirector") == 0) estado=Constantes.ESTADO_SOLICITUD_PENDIENTE; 
                    if (intCencoId != -1){
                        listaSolicitudes = solicitudesDao.getSolicitudes(paramEmpresa, 
                            intCencoId,
                            solicitud.getRunEmpleado(),
                            filtroInicioPESP,
                            filtroFinPESP,
                            userConnected,
                            false,
                            estado,
                            startPageIndex, 
                            numRecordsPerPage, 
                            jtSorting);
                        //Get Total Record Count for Pagination
                        objectsCount = solicitudesDao.getSolicitudesCount(paramEmpresa, 
                            solicitud.getRunEmpleado(),
                            filtroInicioPESP,
                            filtroFinPESP,
                            userConnected,
                            false,
                            estado);
                    }else {
                        listaSolicitudes = solicitudesDao.getSolicitudesAprobarRechazar(paramEmpresa, 
                            userConnected.getRunEmpleado(),
                            filtroInicioPESP,
                            filtroFinPESP,
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
                            filtroInicioPESP,
                            filtroFinPESP,
                            userConnected,
                            estado,
                            false,
                            userConnected.getCencos());
                    }
                    
                    session.setAttribute("solicitudesPESP|"+userConnected.getUsername(), listaSolicitudes);
                    
                    //Convert Java Object to Json
                    JsonElement element = gson.toJsonTree(listaSolicitudes,
                        new TypeToken<List<SolicitudPermisoExamenSaludPreventivaVO>>() {}.getType());

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
                    String reqDesde = request.getParameter("fecha");
                    String reqHasta = reqDesde;
                    
                    if (reqHasta == null || reqHasta.compareTo("") == 0) reqHasta = reqDesde;
                    
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy");
                    Date dteDesde = new Date();
                    Date dteHasta = new Date();
                    try{
                        dteDesde = sdf2.parse(reqDesde);
                        dteHasta = sdf2.parse(reqHasta);
                    }catch(ParseException pex){
                        System.err.println("[SolicitudPESPController]"
                            + "Error al parsear fechas: " + pex.toString());
                    }
                    
                    String strFechaDesde = sdf1.format(dteDesde);
                    String strFechaHasta = sdf1.format(dteHasta);
                    System.out.println(WEB_NAME+"[SolicitudPESPController]"
                        + "strFechaDesde: " + strFechaDesde
                        + ", strFechaHasta: " + strFechaHasta);
                    solicitud.setFechaInicioPESP(strFechaDesde);
                    solicitud.setFechaFinPESP(strFechaHasta);
                    
                    solicitud.setUsernameSolicita(userConnected.getUsername());
                    solicitud.setEmpresaId(userConnected.getEmpresaId());
                    solicitud.setFechaIngreso(strFechaHoraActual);
                    solicitud.setEstadoId(Constantes.ESTADO_SOLICITUD_PENDIENTE);
                    solicitud.setEstadoLabel(Constantes.ESTADO_SOLICITUD_PENDIENTE_LABEL);
                    solicitud.setFechaInicioPESP(reqDesde);
                    solicitud.setFechaFinPESP(reqHasta);
                    solicitud.setAnio(cal.get(Calendar.YEAR));
                    
                    ArrayList<MensajeUsuarioVO> mensajes = new ArrayList<>();
                    Utilidades.IntervaloVO intervalo = null;
                    
                    System.out.println(WEB_NAME+"[SolicitudPESPController]"
                        + "Vista previa antes de Insertar solicitud de Permiso Examen salud preventiva. "
                        + "Username: " + userConnected.getUsername()
                        + ", empresaId: " + solicitud.getEmpresaId()
                        + ", run_empleado: " + solicitud.getRunEmpleado()
                        + ", inicio_PA: " + solicitud.getFechaInicioPESP()
                        + ", fin_PA: " + solicitud.getFechaFinPESP()
                        + ", anio: " + solicitud.getAnio());
                    EmpleadoVO infoEmpleado = empleadosbp.getEmpleado(solicitud.getEmpresaId(), 
                        solicitud.getRunEmpleado());
                    
                    int diasEfectivosSolicitados = 1;
                    solicitud.setDiasSolicitados(diasEfectivosSolicitados);
                    
                    Calendar mycal = Calendar.getInstance(new Locale("es","CL"));
                    int anioActual = mycal.get(Calendar.YEAR);
    
                    double doubleSaldoPESPDisponible = 0;
                    List<PermisoExamenSaludPreventivaVO> infoPESP = 
                        pespDao.getResumenPermisosExamenSaludPreventiva(userConnected.getEmpresaId(), 
                            solicitud.getRunEmpleado(),anioActual, -1, -1, -1, "pesp.run_empleado");
                    if (!infoPESP.isEmpty()){
                        PermisoExamenSaludPreventivaVO saldoPESP = infoPESP.get(0);
                        doubleSaldoPESPDisponible = saldoPESP.getDiasDisponibles();
                    }
                    
                    double saldoPostPESP = doubleSaldoPESPDisponible - solicitud.getDiasSolicitados();
                    System.out.println(WEB_NAME+"[SolicitudPESPController]"
                        + "[precreate]empresaId: : " + solicitud.getEmpresaId()    
                        + ", run_empleado: : " + solicitud.getRunEmpleado()
                        + ", saldo_PESP_disponible: : " + doubleSaldoPESPDisponible
                        + ", diasSolicitados: : " + solicitud.getDiasSolicitados()
                        + ", saldoPostPESP: : " + saldoPostPESP);
                    
                    ArrayList<DestinatarioSolicitudVO> destinatarios = 
                        getDestinatariosSolicitud(solicitud);
                    String strDestinatarios = "";
                    for (int i=0;i<destinatarios.size();i++) {
                        DestinatarioSolicitudVO destinatario = destinatarios.get(i);
                        strDestinatarios += destinatario.getNombre() 
                            + "[" + destinatario.getEmail() + "],"
                            + "[" + destinatario.getCargo() + "],";
                        
                        System.out.println(WEB_NAME+"[SolicitudPESPController]"
                            + "strDestinatarios: " + strDestinatarios);
                    }
                    if (!destinatarios.isEmpty()) strDestinatarios = strDestinatarios.substring(0, strDestinatarios.length()-1);
                    
                    mensajes.add(new MensajeUsuarioVO("RUN trabajador", infoEmpleado.getCodInterno()));
                    mensajes.add(new MensajeUsuarioVO("Nombre trabajador", infoEmpleado.getNombreCompleto()));
                    mensajes.add(new MensajeUsuarioVO("Nombre de usuario solicitante", solicitud.getUsernameSolicita()));
                    mensajes.add(new MensajeUsuarioVO("Institucion", infoEmpleado.getEmpresaNombre()));
                    mensajes.add(new MensajeUsuarioVO("Centro de costo", infoEmpleado.getCencoNombre()));
                    mensajes.add(new MensajeUsuarioVO("Fecha/Hora solicitud", sdf.format(fechaActual)));
                    mensajes.add(new MensajeUsuarioVO("Inicio Permiso Examen Salud Preventiva", solicitud.getFechaInicioPESP()));
                    mensajes.add(new MensajeUsuarioVO("Termino Permiso Examen Salud Preventiva", solicitud.getFechaFinPESP()));
                    mensajes.add(new MensajeUsuarioVO("Año", "" + solicitud.getAnio()));
                    mensajes.add(new MensajeUsuarioVO("Dias solicitados", "" + solicitud.getDiasSolicitados()));
                    
                    if (intervalo != null){
                        mensajes.add(new MensajeUsuarioVO("Hora inicio PESP", intervalo.getHoraInicio()));
                        mensajes.add(new MensajeUsuarioVO("Hora fin PESP", intervalo.getHoraFin()));

                        request.setAttribute("hora_inicio", intervalo.getHoraInicio());
                        request.setAttribute("hora_fin", intervalo.getHoraFin());
                    }
                    
                    if (solicitud.getDiasSolicitados() > doubleSaldoPESPDisponible){
                        MensajeUsuarioVO msgError = new MensajeUsuarioVO("Observación", 
                            "Los dias solicitados superan los dias disponibles "
                            + "(" + doubleSaldoPESPDisponible + ")");
                        msgError.setType("ERROR");
                        mensajes.add(msgError);
                        request.setAttribute("ingresarPESP", false);
                    }else{
                        request.setAttribute("ingresarPESP", true);
                        mensajes.add(new MensajeUsuarioVO("Saldo Dias Post Permiso Examen Salud Preventiva", "" + saldoPostPESP));
                    }
                    
                    mensajes.add(new MensajeUsuarioVO("Destinatario(s)", strDestinatarios));
                    request.setAttribute("fechaDesde", reqDesde);
                    request.setAttribute("fechaHasta", reqHasta);
                    request.setAttribute("diasEfectivosSolicitados", "" + solicitud.getDiasSolicitados());
                    request.setAttribute("saldoPostPESP", "" + saldoPostPESP);
                    request.setAttribute("mensajes", mensajes);
                    request.getRequestDispatcher("/permisos_examen_salud_preventiva/vista_previa.jsp").forward(request, response);
            }
            else if (action.compareTo("create") == 0) {//**********************************************************************************  
                    Calendar cal                = Calendar.getInstance(new Locale("es","CL"));
                    Date fechaActual            = cal.getTime();  
                    SimpleDateFormat sdf        = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("es","CL"));
                    String strFechaHoraActual   = sdf.format(fechaActual);
                    String reqDesde             = request.getParameter("fechaDesde");
                    String reqHasta             = request.getParameter("fechaHasta");
                    String diasEfectivosSolicitados = request.getParameter("diasEfectivosSolicitados");
                    String saldoPostPESP        = request.getParameter("saldoPostPESP");
                    
                    solicitud.setUsernameSolicita(userConnected.getUsername());
                    solicitud.setEmpresaId(userConnected.getEmpresaId());
                    solicitud.setFechaIngreso(strFechaHoraActual);
                    solicitud.setEstadoId(Constantes.ESTADO_SOLICITUD_PENDIENTE);
                    solicitud.setEstadoLabel(Constantes.ESTADO_SOLICITUD_PENDIENTE_LABEL);
                    solicitud.setFechaInicioPESP(reqDesde);
                    solicitud.setFechaFinPESP(reqHasta);
                    solicitud.setAnio(cal.get(Calendar.YEAR));
                    solicitud.setDiasSolicitados(Integer.parseInt(diasEfectivosSolicitados));
                    
                    System.out.println(WEB_NAME+"[SolicitudPESPController]"
                        + "Insertar solicitud de Permiso Examen Salud Preventiva. "
                        + "Username: " + userConnected.getUsername()
                        + ", empresaId: " + solicitud.getEmpresaId()    
                        + ", rut_empleado: " + solicitud.getRunEmpleado()
                        + ", inicio_pesp: " + solicitud.getFechaInicioPESP()
                        + ", fin_pesp: " + solicitud.getFechaFinPESP()
                        + ", dias solicitados: " + solicitud.getDiasSolicitados()    
                        + ", anio: " + solicitud.getAnio());
                    EmpleadoVO infoEmpleado = empleadosbp.getEmpleado(solicitud.getEmpresaId(), 
                        solicitud.getRunEmpleado());
                    resultado.setEmpresaId(infoEmpleado.getEmpresa().getId());
                    resultado.setDeptoId(infoEmpleado.getDepartamento().getId());
                    resultado.setCencoId(infoEmpleado.getCentroCosto().getId());
                    
                    //***************************************************************
                    System.out.println(WEB_NAME+"[SolicitudPESPController]"
                        + "Insertar registro en tabla notificaciones, "
                        + "para enviar mail");
                    NotificacionSolicitudPESPVO evento = notificaEventoSolicitud("INGRESO_SOLICITUD",
                        "Ingreso de Solicitud de Examen Salud Preventiva", 
                        solicitud, userConnected, request,null,null,solicitud.getDiasSolicitados());
                    //solicitud.setDiasSolicitados(evento.getDiasSolicitados());
                    String mensajeFinal = evento.getMensajeFinal();
                    session.setAttribute("mensaje", "Solicitud de Permiso Examen Salud Preventiva ingresada exitosamente."
                        + "<p>" + mensajeFinal);
                    ResultCRUDVO doCreate = solicitudesBp.insert(solicitud, resultado);					
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
                        System.out.println(WEB_NAME+"[SolicitudPESPController]"
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
                    mensajes.add(new MensajeUsuarioVO("Inicio Permiso Examen Salud Preventiva", solicitud.getFechaInicioPESP()));
                    mensajes.add(new MensajeUsuarioVO("Termino Permiso Examen Salud Preventiva", solicitud.getFechaFinPESP()));
                    mensajes.add(new MensajeUsuarioVO("Dias solicitados", "" + solicitud.getDiasSolicitados()));
                    mensajes.add(new MensajeUsuarioVO("Año", "" + cal.get(Calendar.YEAR)));
                               
                    mensajes.add(new MensajeUsuarioVO("Destinatario(s)", strDestinatarios));
                    
                    request.setAttribute("mensajes", mensajes);
                    request.getRequestDispatcher("/permisos_examen_salud_preventiva/solicitud_confirmada.jsp").forward(request, response);
            }else if (action.compareTo("updatePropias") == 0) {//**********************************************************************************  
                    Calendar cal = Calendar.getInstance(new Locale("es","CL"));
                    Date fechaActual = cal.getTime();    
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("es","CL"));
                    String strFechaHoraActual = sdf.format(fechaActual);
                    String strAccion = request.getParameter("Accion");
                    System.out.println(WEB_NAME+"[SolicitudPESPController]"
                        + "Modificar estado de solicitud de Permiso Examen Salud Preventiva. "
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
                    ResultCRUDVO doCreate = new ResultCRUDVO();
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
                    Date fechaActual            = cal.getTime();    
                    SimpleDateFormat sdf        = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("es","CL"));
                    String strFechaHoraActual   = sdf.format(fechaActual);
                    String strAccion            = request.getParameter("Accion");
                    String permiteHora          = "N";
                    boolean ausenciaPorHora = false;
                    System.out.println(WEB_NAME+"[SolicitudPESPController]"
                        + "Aprobar/Rechazar solicitud de permiso examen salud preventiva. "
                        + "Id solicitud: : " + solicitud.getId());
                    
                    SolicitudPermisoExamenSaludPreventivaVO solicitudFromBd = 
                        solicitudesDao.getSolicitudByKey(solicitud.getId());
                    
                    System.out.println(WEB_NAME+"[SolicitudPESPController]"
                        + "Aprobar/Rechazar solicitud de permiso Examen salud preventiva. "
                        + "Id solicitud: : " + solicitud.getId()
                        + ", inicio permiso Examen salud preventiva: " + solicitudFromBd.getFechaInicioPESP()
                        + ", fin permiso Examen salud preventiva: " + solicitudFromBd.getFechaFinPESP()
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
                    ResultCRUDVO doCreate = new ResultCRUDVO();
                    String usernameApruebaRechaza = userConnected.getUsername();
                    if (strAccion.compareTo(Constantes.ESTADO_SOLICITUD_APROBADA) == 0){
                        /**
                        * Antes de aprobar una solicitud de Permiso examen salud preventiva, 
                        * se debe verificar que no haya conflicto con otra ausencia existente.
                        */
                        //validar ausencias conflicto
                        ArrayList<DetalleAusenciaVO> ausenciasConflicto = 
                            detAusenciaBp.getAusenciasConflicto(solicitudFromBd.getRunEmpleado(),
                            ausenciaPorHora,
                            solicitudFromBd.getFechaInicioPESP(),
                            solicitudFromBd.getFechaFinPESP(), 
                            null, 
                            null);
                        if (ausenciasConflicto.isEmpty()){
                            System.out.println(WEB_NAME+"[SolicitudPESPController]"
                                + "No hay conflicto. "
                                + "Se procede a Aprobar solicitud "
                                + "y a Insertar permiso Examen salud preventiva...");
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
                                "Solicitud de permiso examen salud preventiva Aprobada", 
                                solicitudFromBd, userConnected, 
                                request, 
                                solicitudFromBd.getFechaInicioPESP(), 
                                solicitudFromBd.getFechaFinPESP(),
                                solicitudFromBd.getDiasSolicitados());
                            
                            //*********************************************************
                            System.out.println(WEB_NAME+"[SolicitudPESPController]"
                                + "Aprobar solicitud. Insertar permiso examen salud preventiva."
                                + " Usuario.username: " + userConnected.getUsername()
                                + " Usuario.Run: " + userConnected.getRunEmpleado());
                            
                            SolicitudPermisoExamenSaludPreventivaVO auxSolicitud = 
                                solicitudesDao.getSolicitudByKey(solicitud.getId());
                            //Insertar Permiso Administrativo
                            DetalleAusenciaVO newAusencia;
                            newAusencia = new DetalleAusenciaVO();
                            newAusencia.setEmpresaId(solicitudFromBd.getEmpresaId());
                            newAusencia.setRutEmpleado(solicitudFromBd.getRunEmpleado());
                            newAusencia.setFechaInicioAsStr(auxSolicitud.getFechaInicioPESP());
                            newAusencia.setFechaFinAsStr(auxSolicitud.getFechaFinPESP());
                            //fijos
                            newAusencia.setIdAusencia(Constantes.ID_AUSENCIA_PERMISO_EXAMEN_SALUD_PREVENTIVA);
                            newAusencia.setRutAutorizador(userConnected.getRunEmpleado());
                            newAusencia.setAusenciaAutorizada("S");
                            newAusencia.setPermiteHora(permiteHora);
                            
                            newAusencia.setDiasSolicitados(solicitud.getDiasSolicitados());
                            ResultCRUDVO insertResult = new ResultCRUDVO();
                            insertResult = 
                                insertarPermisoExamenSaludPreventiva(request, 
                                    appProperties, 
                                    parametrosSistema, 
                                    userConnected, 
                                    newAusencia);
                            
                        }else{
                            /**
                            * Hay conflicto con ausencias existentes: Rechazar Solicitud de Permiso Examen Salud preventiva. 
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
                            System.out.println(WEB_NAME+"[SolicitudPESPController]"
                                + "Rechazar solicitud de Permiso Examen salud preventiva. "
                                + "Dias solicitados: " + solicitud.getDiasSolicitados());
                            notificaEventoSolicitud("SOLICITUD_RECHAZADA",
                                "Solicitud de Permiso Examen salud preventiva Rechazada", 
                                solicitudFromBd, userConnected, request,
                                solicitudFromBd.getFechaInicioPESP(), 
                                solicitudFromBd.getFechaFinPESP(),
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
                        System.out.println(WEB_NAME+"[SolicitudPESPController]"
                            + "Rechazar solicitud de Permiso Examen salud preventiva. "
                            + "Dias solicitados: " + solicitud.getDiasSolicitados());
                        notificaEventoSolicitud("SOLICITUD_RECHAZADA",
                            "Solicitud de Permiso Examen salud preventiva Rechazada", 
                            solicitudFromBd, userConnected, request,
                            solicitudFromBd.getFechaInicioPESP(), 
                            solicitudFromBd.getFechaFinPESP(),
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
    * 
    * Insertar ausencia del tipo 'Permiso examen salud preventiva'
    * 
    */
    private ResultCRUDVO insertarPermisoExamenSaludPreventiva(HttpServletRequest _request,
            PropertiesVO _appProperties,
            HashMap<String, Double> _parametrosSistema,
            UsuarioVO _userConnected, 
            DetalleAusenciaVO _ausencia){
       
        Calendar mycal = Calendar.getInstance(new Locale("es","CL"));
        int anioActual = mycal.get(Calendar.YEAR);
        Date currentDate = mycal.getTime();
        
        System.out.println(WEB_NAME+"[SolicitudPESPController."
            + "insertarPermisoExamenSaludPreventiva]"
            + "Insertar detalle ausencia (PERMISO EXAMEN SALUD PREVENTIVA). "
            + "EmpresaId: " + _ausencia.getEmpresaId()
            + ", Run empleado: " + _ausencia.getRutEmpleado()
            + ", Anio: " + anioActual);
        PermisosExamenSaludPreventivaDAO permisosDao = new PermisosExamenSaludPreventivaDAO(_appProperties);
        int intSaldoPESPDisponible = 0;
        int intSaldoPESPUtilizados = 0;
        List<PermisoExamenSaludPreventivaVO> infoPESP = 
            permisosDao.getResumenPermisosExamenSaludPreventiva(_ausencia.getEmpresaId(), 
                _ausencia.getRutEmpleado(),anioActual, -1, -1, -1, "pesp.run_empleado");
        if (!infoPESP.isEmpty()){
            PermisoExamenSaludPreventivaVO saldoPESP = infoPESP.get(0);
            intSaldoPESPDisponible = saldoPESP.getDiasDisponibles();
            intSaldoPESPUtilizados = saldoPESP.getDiasUtilizados();
        }
        
        BigDecimal bd = new BigDecimal(_ausencia.getDiasSolicitados());
        int intDiasSolicitados          = bd.intValue();
        
        System.out.println(WEB_NAME+"[SolicitudPESPController."
            + "insertarPermisoExamenSaludPreventiva]"
            + "saldoPESP disponible: " + intSaldoPESPDisponible
            + ", saldoPESP utilizados: " + intSaldoPESPUtilizados
            + ", ausencia.dias_solicitados: " + intDiasSolicitados    
        );
        
        int saldoDiasUtilizadosPostPESP = intSaldoPESPUtilizados + intDiasSolicitados;        
        int saldoDisponiblePostPESP     = intSaldoPESPDisponible - intDiasSolicitados;
        
        System.out.println(WEB_NAME+"[SolicitudPESPController."
            + "insertarPermisoExamenSaludPreventiva]"
            + "saldoDiasUtilizadosPostPESP: " + saldoDiasUtilizadosPostPESP
            + ", saldoDisponiblePostPESP: " + saldoDisponiblePostPESP);
        
        _ausencia.setSaldoPostPESP(saldoDisponiblePostPESP);
        
        String mensajeFinal = null;
        DetalleAusenciaBp ausenciasBp   = new DetalleAusenciaBp(_appProperties);
                
        MaintenanceEventVO resultado=new MaintenanceEventVO();
        resultado.setUsername(_userConnected.getUsername());
        resultado.setDatetime(new Date());
        resultado.setUserIP(_request.getRemoteAddr());
        resultado.setType("DAU");
        resultado.setEmpresaIdSource(_userConnected.getEmpresaId());
                                        
        resultado.setRutEmpleado(_ausencia.getRutEmpleado());
        System.out.println(WEB_NAME+"[SolicitudPESPController.insertarPermisoExamenSaludPreventiva]"
            + "Insertar detalle ausencia (PERMISO EXAMEN SALUD PREVENTIVA)");
        ResultCRUDVO doCreate = ausenciasBp.insertaPermisoExamenSaludPreventiva(_ausencia, resultado);
        
        if (doCreate.isThereError()){
            mensajeFinal= "Error al insertar Permiso examen salud preventiva: " + doCreate.getMsgError();
            doCreate.setMsg(mensajeFinal);
        }else{
            System.out.println(WEB_NAME+"[SolicitudPESPController."
                + "insertarPermisoExamenSaludPreventiva]"
                + "Actualizar resumen de Permisos Examen Salud Preventiva, "
                + "Anio= ["+ anioActual + "]");
            PermisoExamenSaludPreventivaVO objPESP = new PermisoExamenSaludPreventivaVO();
            objPESP.setEmpresaId(_ausencia.getEmpresaId());
            objPESP.setRunEmpleado(_ausencia.getRutEmpleado());
            objPESP.setAnio(anioActual);
            
            objPESP.setDiasDisponibles(saldoDisponiblePostPESP);
            objPESP.setDiasUtilizados(saldoDiasUtilizadosPostPESP);
            
            ResultCRUDVO resultadoUpdate = permisosDao.updateResumenPESP(objPESP);
             
        }
        
        System.out.println(WEB_NAME+"[SolicitudPESPController."
            + "insertarPermisoExamenSaludPreventiva]"
            + "Saliendo del metodo OK.");
        return doCreate;
    }
    
    /**
    * 
    * 
    */
    private NotificacionSolicitudPESPVO notificaEventoSolicitud(
            String _tipoEvento, 
            String _evento,
            SolicitudPermisoExamenSaludPreventivaVO _solicitud,
            UsuarioVO _userConnected, 
            HttpServletRequest _request,
            String _inicioPermisoAdministrativo,
            String _finPermisoAdministrativo,
            double _diasSolicitados){
    
        NotificacionSolicitudPESPVO evento = new NotificacionSolicitudPESPVO();
                
        EmpleadosBp empleadobp      = new EmpleadosBp();
        CentroCostoBp cencosbp      = new CentroCostoBp(null);
        PermisosExamenSaludPreventivaDAO pespDao   = new PermisosExamenSaludPreventivaDAO(null);
        
        System.out.println(WEB_NAME+"[SolicitudPESPController."
            + "notificaEventoSolicitud]Invocar getEmpleado. "
            + "EmpresaId: " + _solicitud.getEmpresaId()
            + ", rutEmpleado: " + _solicitud.getRunEmpleado()
            + ", dias_solicitados: " + _diasSolicitados);
                
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
        System.out.println(WEB_NAME+"[SolicitudPESPController."
            + "notificaEventoSolicitud]"
            + "Email por defecto: " + mailTo);
        String notaObservacion = _request.getParameter("notaObservacion");
        String cadenaNombres    = "";
        boolean hayJefeNacional = true;
        boolean hayJefeDirecto  = true;
        double diasSolicitados     = 0;
        
        if (_tipoEvento.compareTo("SOLICITUD_APROBADA") == 0 || _tipoEvento.compareTo("SOLICITUD_RECHAZADA") == 0){
            _solicitud.setFechaInicioPESP(_inicioPermisoAdministrativo);
            _solicitud.setFechaFinPESP(_finPermisoAdministrativo);
            diasSolicitados = _diasSolicitados;
        }
        
        if (_tipoEvento.compareTo("INGRESO_SOLICITUD") == 0){
            String cadenaEmails = "";
            System.out.println(WEB_NAME+"[SolicitudPESPController."
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
                    System.out.println(WEB_NAME+"[SolicitudPESPController."
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
                //Usuario con cargo Director, debe enviar solicitud a Gerencia en Admin Central (Cargo Jefe Técnico Nacional, ID 69 )
                System.out.println(WEB_NAME+"[SolicitudPESPController."
                    + "notificaEventoSolicitud]Empleado con cargo Director, "
                    + "debe enviar solicitud a Gerencia en Admin Central (Cargo Jefe Técnico Nacional, ID 69 )");
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
                    System.out.println(WEB_NAME+"[SolicitudPESPController."
                        + "notificaEventoSolicitud]Rescatar emails del Jefe Tecnico Nacional");
                    for(EmpleadoVO itJefazos : listaJefesTecnicosNacional){
                        cadenaEmails += itJefazos.getEmail() + ",";
                        cadenaNombres += itJefazos.getNombres()+ ",";
                        if (itJefazos.getApePaterno() != null) cadenaNombres += " " + itJefazos.getApePaterno();
                        if (itJefazos.getApeMaterno() != null) cadenaNombres += " " + itJefazos.getApeMaterno();
                    }
                    System.out.println(WEB_NAME+"[SolicitudPESPController."
                        + "notificaEventoSolicitud]Emails Jefe Tecnico Nacional: " + cadenaEmails);
                    System.out.println(WEB_NAME+"[SolicitudPESPController."
                        + "notificaEventoSolicitud]Nombres Jefe Tecnico Nacional: " + cadenaNombres);
                    
                    cadenaEmails = cadenaEmails.substring(0, cadenaEmails.length() - 1);
                    mailTo      = cadenaEmails;
                }else{
                    System.out.println(WEB_NAME+"[SolicitudPESPController."
                        + "notificaEventoSolicitud]No se encontraron "
                        + "empleados con Cargo 'JEFE TECNICO NACIONAL' ");
                    hayJefeNacional = false;
                }
            }
        }
        
        System.out.println(WEB_NAME+"[SolicitudPESPController."
            + "notificaEventoSolicitud]Dias solicitados: " + _solicitud.getDiasSolicitados());
        if (_solicitud.getDiasSolicitados() == 0){
            diasSolicitados =1;
        }
        
        String mensaje = "RUN trabajador: " + empleado.getCodInterno()
            + "<br>Nombre trabajador: " + empleado.getNombreCompleto()
            + "<br>Nombre de usuario solicitante: " + _solicitud.getUsernameSolicita()    
            + "<br>Institucion: " + empleado.getEmpresaNombre()
            + "<br>Centro de costo: " + empleado.getCencoNombre()
            + "<br>Fecha/Hora solicitud: " + sdf.format(fechaActual)
            + "<br>Inicio Permiso Examen Salud Preventiva: " + _solicitud.getFechaInicioPESP()
            + "<br>Termino Permiso Examen Salud Preventiva: " + _solicitud.getFechaFinPESP()
            + "<br>Dias solicitados: " + _solicitud.getDiasSolicitados();
        
        String mailBody = "Evento:" + _evento
            + "<br>RUN trabajador: " + empleado.getCodInterno()
            + "<br>Nombre trabajador: " + empleado.getNombreCompleto()
            + "<br>Nombre de usuario solicitante: " + _solicitud.getUsernameSolicita()    
            + "<br>Institucion: " + empleado.getEmpresaNombre()
            + "<br>Centro de costo: " + empleado.getCencoNombre()
            + "<br>Fecha/Hora solicitud: " + sdf.format(fechaActual)
            + "<br>Inicio Permiso Examen Salud Preventiva: " + _solicitud.getFechaInicioPESP()
            + "<br>Termino Permiso Examen Salud Preventiva: " + _solicitud.getFechaFinPESP()
            + "<br>Dias solicitados: " + _solicitud.getDiasSolicitados()
            + "<br>Nota/Observacion: " + notaObservacion;
        
        evento.setTipoEvento(_tipoEvento);
        evento.setMensajeFinal(mensaje);
        evento.setRunTrabajador(empleado.getCodInterno());
        evento.setNombreTrabajador(empleado.getNombreCompleto());
        evento.setInstitucion(empleado.getEmpresaNombre());
        evento.setCentroCosto(empleado.getCencoNombre());
        evento.setFechaHoraSolicitud(sdf.format(fechaActual));
        evento.setInicioPermiso(_solicitud.getFechaInicioPESP());
        evento.setTerminoPermiso(_solicitud.getFechaFinPESP());
        evento.setDiasSolicitados(_solicitud.getDiasSolicitados());
        
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
        
        System.out.println(WEB_NAME+"[SolicitudPESPController."
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
    * Obtiene destinatario(s) de la solicitud de Permiso examen salud preventiva
    */
    private ArrayList<DestinatarioSolicitudVO> getDestinatariosSolicitud(SolicitudPermisoExamenSaludPreventivaVO _solicitud){
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
        
        System.out.println(WEB_NAME+"[SolicitudPESPController."
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
                System.out.println(WEB_NAME+"[SolicitudPESPController."
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
                System.out.println(WEB_NAME+"[SolicitudPESPController."
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
            //Usuario con cargo Director, debe enviar solicitud a Gerencia en Admin Central (Cargo Jefe Técnico Nacional, ID 69 )
            System.out.println(WEB_NAME+"[SolicitudPESPController."
                + "getDestinatarioSolicitud]Usuario con cargo Director, "
                + "debe enviar solicitud a Gerencia en Admin Central "
                + "(Cargo Jefe Técnico Nacional, ID 69 )");
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
                System.out.println(WEB_NAME+"[SolicitudPESPController."
                    + "getDestinatarioSolicitud]No se encontraron "
                    + "empleados con Cargo 'JEFE TECNICO NACIONAL' ");
                hayJefeNacional = false;
            }
        }
        
        return destinatarios;
    }
    
    
}
