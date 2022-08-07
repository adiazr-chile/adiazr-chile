package cl.femase.gestionweb.servlet.mantencion;

import cl.femase.gestionweb.business.CentroCostoBp;
import cl.femase.gestionweb.business.DetalleAusenciaBp;
import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.business.NotificacionBp;
import cl.femase.gestionweb.business.SolicitudVacacionesBp;
import cl.femase.gestionweb.business.VacacionesBp;
import cl.femase.gestionweb.common.ClientInfo;
import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.vo.DestinatarioSolicitudVO;
import cl.femase.gestionweb.vo.DetalleAusenciaVO;
import cl.femase.gestionweb.vo.DiasEfectivosVacacionesVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.MensajeUsuarioVO;
import cl.femase.gestionweb.vo.NotificacionSolicitudVacacionesVO;
import cl.femase.gestionweb.vo.NotificacionVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import cl.femase.gestionweb.vo.SolicitudVacacionesVO;
import cl.femase.gestionweb.vo.VacacionesVO;
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
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

public class SolicitudVacacionesController extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 995L;
    
    public SolicitudVacacionesController() {
        
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

        SolicitudVacacionesBp solicitudesBp = new SolicitudVacacionesBp(appProperties);
        EmpleadosBp empleadosbp             = new EmpleadosBp(appProperties);
        DetalleAusenciaBp detAusenciaBp     = new DetalleAusenciaBp(appProperties);
        HashMap<String, Double> parametrosSistema = (HashMap<String, Double>)session.getAttribute("parametros_sistema");
        
        if(request.getParameter("action") != null){
            System.out.println("[SolicitudVacacionesController]"
                + "action is: " + request.getParameter("action"));
            List<SolicitudVacacionesVO> listaSolicitudes = new ArrayList<SolicitudVacacionesVO>();
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            response.setContentType("application/json");

            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("SVA");//
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
            String jtSorting        = "sv.solic_fec_ingreso desc";
            
            if (request.getParameter("jtStartIndex") != null) 
                startPageIndex = Integer.parseInt(request.getParameter("jtStartIndex"));
            if (request.getParameter("jtPageSize") != null) 
                numRecordsPerPage   = Integer.parseInt(request.getParameter("jtPageSize"));
            if (request.getParameter("jtSorting") != null) 
                jtSorting   = request.getParameter("jtSorting");
            
            if (jtSorting.contains("id")) jtSorting = jtSorting.replaceFirst("id","sv.solic_id");
            else if (jtSorting.contains("estadoLabel")) jtSorting = jtSorting.replaceFirst("estadoLabel","sv.status_id");
            else if (jtSorting.contains("inicioVacaciones")) jtSorting = jtSorting.replaceFirst("inicioVacaciones","sv.solic_inicio");
                  
            //objeto usado para update/insert
            SolicitudVacacionesVO solicitud = new SolicitudVacacionesVO();
            
            //Filtros de busqueda
            String filtroRutEmpleado    = request.getParameter("rutEmpleado");
            String filtroInicioVacacion = request.getParameter("startVacacion");
            String filtroFinVacacion    = request.getParameter("endVacacion");
            String paramCencoID         = request.getParameter("cencoId");
            String paramEmpresa = null;
            String paramDepto   = null;
            String cencoId      = "";
            
            System.out.println("[SolicitudVacacionesController]"
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
            
            System.out.println("[SolicitudVacacionesController]"
                + "empresaId: " + paramEmpresa
                + ", rut empleado: " + request.getParameter("rutEmpleado"));
            if(request.getParameter("id") != null){
                solicitud.setId(Integer.parseInt(request.getParameter("id")));
            }
            if(request.getParameter("empresaId") != null){
                solicitud.setEmpresaId(paramEmpresa);
            }
            if(request.getParameter("rutEmpleado") != null){
                solicitud.setRutEmpleado(request.getParameter("rutEmpleado"));
            }
            solicitud.setUsernameSolicita(userConnected.getUsername());
            if(request.getParameter("inicioVacaciones") != null){
                solicitud.setInicioVacaciones(request.getParameter("inicioVacaciones"));
            }
            if(request.getParameter("finVacaciones") != null){
                solicitud.setFinVacaciones(request.getParameter("finVacaciones"));
            }
            if(request.getParameter("notaObservacion") != null){
                solicitud.setNotaObservacion(request.getParameter("notaObservacion"));
            }
                    
            String runEmpleado = userConnected.getUsername();
            if (userConnected.getIdPerfil() == Constantes.ID_PERFIL_DIRECTOR 
                || userConnected.getIdPerfil() == Constantes.ID_PERFIL_DIRECTOR_TR
                || userConnected.getIdPerfil() == Constantes.ID_PERFIL_JEFE_TECNICO_NACIONAL){
                    runEmpleado = null;//userConnected.getRunEmpleado();
            }
            solicitud.setRutEmpleado(runEmpleado);
            System.out.println("[SolicitudVacacionesController]"
                + "action: " + action 
                + ", set RunEmpleado: " + solicitud.getRutEmpleado());
                    
            if (action.compareTo("listPropias") == 0) {//**********************************************************************************
                try{
                    System.out.println("[SolicitudVacacionesController]"
                        + "Mostrar lista de solicitudes propias del usuario. "
                        + "empresa: " + paramEmpresa
                        + ", usuario: " + userConnected.getUsername()
                        + ", inicioVacacion: " + filtroInicioVacacion
                        + ", finVacacion: " + filtroFinVacacion);
                    session.setAttribute("pEmpresaId", paramEmpresa);
                    session.setAttribute("pDeptoId", paramDepto);
                    session.setAttribute("pCencoId", cencoId);
                    int intCencoId  = -1;
                    String estado   = "TODAS";
                    if (cencoId.compareTo("") != 0) intCencoId = Integer.parseInt(cencoId); 
                    int objectsCount = 0;
                    if (intCencoId != -1){
                        listaSolicitudes = solicitudesBp.getSolicitudes(paramEmpresa, 
                            intCencoId,
                            solicitud.getRutEmpleado(),
                            filtroInicioVacacion,
                            filtroFinVacacion,
                            userConnected,
                            true,
                            estado,
                            startPageIndex, 
                            numRecordsPerPage, 
                            jtSorting);
                        
                        //Get Total Record Count for Pagination
                        objectsCount = solicitudesBp.getSolicitudesCount(paramEmpresa, 
                            solicitud.getRutEmpleado(),
                            filtroInicioVacacion,
                            filtroFinVacacion,
                            userConnected,
                            true,
                            estado);
                        session.setAttribute("solcitudesVacaciones|"+userConnected.getUsername(), listaSolicitudes);
                        
                    }
                    //Convert Java Object to Json
                    JsonElement element = gson.toJsonTree(listaSolicitudes,
                        new TypeToken<List<SolicitudVacacionesVO>>() {}.getType());

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
                    System.out.println("[SolicitudVacacionesController]"
                        + "Mostrar " + action + "lista de solicitudes. "
                        + "empresa: " + paramEmpresa
                        + ", cencoId: " + intCencoId
                        + ", usuario: " + userConnected.getUsername()
                        + ", inicioVacacion: " + filtroInicioVacacion
                        + ", finVacacion: " + filtroFinVacacion);
                    int objectsCount = 0;
                    String estado="TODAS";
                    if (action.compareTo("listDirector") == 0) estado=Constantes.ESTADO_SOLICITUD_PENDIENTE; 
                    if (intCencoId != -1){
                        listaSolicitudes = solicitudesBp.getSolicitudes(paramEmpresa, 
                            intCencoId,
                            solicitud.getRutEmpleado(),
                            filtroInicioVacacion,
                            filtroFinVacacion,
                            userConnected,
                            false,
                            estado,
                            startPageIndex, 
                            numRecordsPerPage, 
                            jtSorting);
                        //Get Total Record Count for Pagination
                        objectsCount = solicitudesBp.getSolicitudesCount(paramEmpresa, 
                            solicitud.getRutEmpleado(),
                            filtroInicioVacacion,
                            filtroFinVacacion,
                            userConnected,
                            false,
                            estado);
                    }else {
                        listaSolicitudes = solicitudesBp.getSolicitudesAprobarRechazar(paramEmpresa, 
                            userConnected.getRunEmpleado(),
                            filtroInicioVacacion,
                            filtroFinVacacion,
                            userConnected,
                            estado,
                            false,
                            userConnected.getCencos(),
                            startPageIndex, 
                            numRecordsPerPage, 
                            jtSorting);
                        //Get Total Record Count for Pagination
                        objectsCount = solicitudesBp.getSolicitudesAprobarRechazarCount(paramEmpresa, 
                            userConnected.getRunEmpleado(),
                            filtroInicioVacacion,
                            filtroFinVacacion,
                            userConnected,
                            estado,
                            false,
                            userConnected.getCencos());
                    }
                    
                    session.setAttribute("solcitudesVacaciones|"+userConnected.getUsername(), listaSolicitudes);
                    
                    //Convert Java Object to Json
                    JsonElement element = gson.toJsonTree(listaSolicitudes,
                        new TypeToken<List<SolicitudVacacionesVO>>() {}.getType());

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
                    VacacionesVO saldoVacaciones=new VacacionesVO();
                    solicitud.setUsernameSolicita(userConnected.getUsername());
                    solicitud.setEmpresaId(userConnected.getEmpresaId());
                    solicitud.setFechaIngreso(strFechaHoraActual);
                    solicitud.setEstadoId(Constantes.ESTADO_SOLICITUD_PENDIENTE);
                    solicitud.setEstadoLabel(Constantes.ESTADO_SOLICITUD_PENDIENTE_LABEL);
                    solicitud.setInicioVacaciones(reqDesde);
                    solicitud.setFinVacaciones(reqHasta);
                    System.out.println("[SolicitudVacacionesController]"
                        + "Vista previa antes de Insertar solicitud de vacaciones. "
                        + "Username: : " + userConnected.getUsername()
                        + ", empresaId: : " + solicitud.getEmpresaId()    
                        + ", rut_empleado: : " + solicitud.getRutEmpleado()
                        + ", inicio_vacaciones: : " + solicitud.getInicioVacaciones()
                        + ", fin_vacaciones: : " + solicitud.getFinVacaciones());
                    EmpleadoVO infoEmpleado = empleadosbp.getEmpleado(solicitud.getEmpresaId(), 
                        solicitud.getRutEmpleado());
                    
                    VacacionesBp vacacionesBp = new VacacionesBp(null);
                    
                    List<VacacionesVO> infoVacaciones = 
                        vacacionesBp.getInfoVacaciones(userConnected.getEmpresaId(), 
                            solicitud.getRutEmpleado(), -1, -1, -1, "vac.rut_empleado");
                    if (infoVacaciones.isEmpty()){
                         System.out.println("[SolicitudVacacionesController]"
                        + "Vista previa antes de Insertar solicitud de vacaciones. "
                        + "el trabajador no tiene registro en la tabla 'vacaciones', Salir");
                         return;
                    }else{
                        saldoVacaciones = infoVacaciones.get(0);
                    }
                    
                    System.out.println("[SolicitudVacacionesController]"
                        + "Vista previa antes de Insertar solicitud de vacaciones. "
                        + "Username: : " + userConnected.getUsername()
                        + ", empresaId: : " + solicitud.getEmpresaId()    
                        + ", rut_empleado: : " + solicitud.getRutEmpleado()
                        + ", inicio_vacaciones: : " + solicitud.getInicioVacaciones()
                        + ", fin_vacaciones: : " + solicitud.getFinVacaciones()
                        + ", dias_especiales: : " + saldoVacaciones.getDiasEspeciales());
                    
                    int diasEfectivosSolicitados = 
                        vacacionesBp.getDiasEfectivos(solicitud.getInicioVacaciones(), 
                            solicitud.getFinVacaciones(), 
                            saldoVacaciones.getDiasEspeciales(), 
                            solicitud.getEmpresaId(), 
                            solicitud.getRutEmpleado());
                    solicitud.setDiasEfectivosVacacionesSolicitadas(diasEfectivosSolicitados);
                    
                    double doubleSaldoVacaciones = 0;
                    
                    if (!infoVacaciones.isEmpty()){
                        //VacacionesVO saldoVacaciones = infoVacaciones.get(0);
                        doubleSaldoVacaciones = saldoVacaciones.getSaldoDias();
                    }
                    
                    double saldoPostVacaciones = doubleSaldoVacaciones - diasEfectivosSolicitados;
                    System.out.println("[SolicitudVacacionesController]"
                        + ", empresaId: : " + solicitud.getEmpresaId()    
                        + ", rut_empleado: : " + solicitud.getRutEmpleado()
                        + ", saldo_vacaciones: : " + doubleSaldoVacaciones
                        + ", diasEfectivosSolicitados: : " + diasEfectivosSolicitados
                        + ", saldoPostVacaciones: : " + saldoPostVacaciones);
                    
                    ArrayList<DestinatarioSolicitudVO> destinatarios = 
                        getDestinatariosSolicitud(solicitud);
                    String strDestinatarios = "";
                    for (int i=0;i<destinatarios.size();i++) {
                        DestinatarioSolicitudVO destinatario = destinatarios.get(i);
                        strDestinatarios += destinatario.getNombre() 
                            + "[" + destinatario.getEmail() + "],"
                            + "[" + destinatario.getCargo() + "],";
                        
                        System.out.println("[SolicitudVacacionesController]"
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
                    mensajes.add(new MensajeUsuarioVO("Inicio vacaciones", solicitud.getInicioVacaciones()));
                    mensajes.add(new MensajeUsuarioVO("Termino vacaciones", solicitud.getFinVacaciones()));
                    mensajes.add(new MensajeUsuarioVO("Dias solicitados", "" + diasEfectivosSolicitados));
                    
                    DetalleAusenciaVO newAusencia = new DetalleAusenciaVO();
                    newAusencia.setEmpresaId(solicitud.getEmpresaId());
                    newAusencia.setRutEmpleado(solicitud.getRutEmpleado());
                    newAusencia.setDiasEfectivosVacaciones(Double.parseDouble(String.valueOf(diasEfectivosSolicitados)));
                    
                    VacacionesBp vacaciones = new VacacionesBp(appProperties);
                    DiasEfectivosVacacionesVO objDE = vacaciones.getDesgloseDiasVacaciones(newAusencia);
                    mensajes.add(new MensajeUsuarioVO("Dias efectivos VBA", "" + objDE.getDiasEfectivosVBA()));
                    mensajes.add(new MensajeUsuarioVO("Dias efectivos VP", "" + objDE.getDiasEfectivosVP()));
                    mensajes.add(new MensajeUsuarioVO("Saldo VBA Pre Vacaciones", "" + objDE.getSaldoVBAPreVacaciones()));
                    mensajes.add(new MensajeUsuarioVO("Saldo VP Pre Vacaciones", "" + objDE.getSaldoVPPreVacaciones()));
                    mensajes.add(new MensajeUsuarioVO("Saldo VBA Post Vacaciones", "" + objDE.getSaldoVBAPostVacaciones()));
                    mensajes.add(new MensajeUsuarioVO("Saldo VP Post Vacaciones", "" + objDE.getSaldoVPPostVacaciones()));
                    
                    //mensajes.add(new MensajeUsuarioVO("Saldo post vacaciones", "" + saldoPostVacaciones));
                    mensajes.add(new MensajeUsuarioVO("Destinatario(s)", strDestinatarios));
                    request.setAttribute("fechaDesde", reqDesde);
                    request.setAttribute("fechaHasta", reqHasta);
                    request.setAttribute("diasEfectivosSolicitados", "" + diasEfectivosSolicitados);
                    request.setAttribute("saldoPostVacaciones", "" + saldoPostVacaciones);
                    request.setAttribute("mensajes", mensajes);
                    
                    request.getRequestDispatcher("/vacaciones/vista_previa.jsp").forward(request, response);//like mensaje.jsp
            }
            else if (action.compareTo("create") == 0) {//**********************************************************************************  
                    Calendar cal = Calendar.getInstance(new Locale("es","CL"));
                    Date fechaActual = cal.getTime();    
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("es","CL"));
                    String strFechaHoraActual = sdf.format(fechaActual);
                    String reqDesde = request.getParameter("fechaDesde");
                    String reqHasta = request.getParameter("fechaHasta");
                    
                    String diasEfectivosSolicitados = request.getParameter("diasEfectivosSolicitados");
                    String saldoPostVacaciones = request.getParameter("saldoPostVacaciones");
                    
                    solicitud.setUsernameSolicita(userConnected.getUsername());
                    solicitud.setEmpresaId(userConnected.getEmpresaId());
                    solicitud.setFechaIngreso(strFechaHoraActual);
                    solicitud.setEstadoId(Constantes.ESTADO_SOLICITUD_PENDIENTE);
                    solicitud.setEstadoLabel(Constantes.ESTADO_SOLICITUD_PENDIENTE_LABEL);
                    solicitud.setInicioVacaciones(reqDesde);
                    solicitud.setFinVacaciones(reqHasta);
                    System.out.println("[SolicitudVacacionesController]"
                        + "Insertar solicitud de vacaciones. "
                        + "Username: : " + userConnected.getUsername()
                        + ", empresaId: : " + solicitud.getEmpresaId()    
                        + ", rut_empleado: : " + solicitud.getRutEmpleado()
                        + ", inicio_vacaciones: : " + solicitud.getInicioVacaciones()
                        + ", fin_vacaciones: : " + solicitud.getFinVacaciones());
                    EmpleadoVO infoEmpleado = empleadosbp.getEmpleado(solicitud.getEmpresaId(), 
                        solicitud.getRutEmpleado());
                    resultado.setEmpresaId(infoEmpleado.getEmpresa().getId());
                    resultado.setDeptoId(infoEmpleado.getDepartamento().getId());
                    resultado.setCencoId(infoEmpleado.getCentroCosto().getId());
                    
                    //***************************************************************
                    System.out.println("[SolicitudVacacionesController]"
                        + "Insertar registro en tabla notificaciones, "
                        + "para enviar mail");
                    NotificacionSolicitudVacacionesVO evento = notificaEventoSolicitud("INGRESO_SOLICITUD",
                        "Ingreso de Solicitud de Vacaciones", 
                        solicitud, userConnected, request,null,null,-1);
                    solicitud.setDiasEfectivosVacacionesSolicitadas(evento.getDiasSolicitados());
                    String mensajeFinal = evento.getMensajeFinal();
                    session.setAttribute("mensaje", "Solicitud de vacaciones ingresada exitosamente."
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
                        System.out.println("[SolicitudVacacionesController]"
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
                    mensajes.add(new MensajeUsuarioVO("Inicio vacaciones", solicitud.getInicioVacaciones()));
                    mensajes.add(new MensajeUsuarioVO("Termino vacaciones", solicitud.getFinVacaciones()));
                    //mensajes.add(new MensajeUsuarioVO("Dias solicitados", "" + diasEfectivosSolicitados));
                    
                    DetalleAusenciaVO newAusencia = new DetalleAusenciaVO();
                    newAusencia.setEmpresaId(solicitud.getEmpresaId());
                    newAusencia.setRutEmpleado(solicitud.getRutEmpleado());
                    newAusencia.setDiasEfectivosVacaciones(Double.parseDouble(diasEfectivosSolicitados));
                    
                    VacacionesBp vacaciones = new VacacionesBp(appProperties);
                    DiasEfectivosVacacionesVO objDE = vacaciones.getDesgloseDiasVacaciones(newAusencia);
                    mensajes.add(new MensajeUsuarioVO("Dias efectivos VBA", "" + objDE.getDiasEfectivosVBA()));
                    mensajes.add(new MensajeUsuarioVO("Dias efectivos VP", "" + objDE.getDiasEfectivosVP()));
                    mensajes.add(new MensajeUsuarioVO("Saldo VBA Pre Vacaciones", "" + objDE.getSaldoVBAPreVacaciones()));
                    mensajes.add(new MensajeUsuarioVO("Saldo VP Pre Vacaciones", "" + objDE.getSaldoVPPreVacaciones()));
                    mensajes.add(new MensajeUsuarioVO("Saldo VBA Post Vacaciones", "" + objDE.getSaldoVBAPostVacaciones()));
                    mensajes.add(new MensajeUsuarioVO("Saldo VP Post Vacaciones", "" + objDE.getSaldoVPPostVacaciones()));
                    
                    //mensajes.add(new MensajeUsuarioVO("Saldo post vacaciones", "" + saldoPostVacaciones));
                    
                    mensajes.add(new MensajeUsuarioVO("Destinatario(s)", strDestinatarios));
                    request.setAttribute("mensajes", mensajes);
                    request.getRequestDispatcher("/vacaciones/solicitud_confirmada.jsp").forward(request, response);
            }else if (action.compareTo("updatePropias") == 0) {//**********************************************************************************  
                    Calendar cal = Calendar.getInstance(new Locale("es","CL"));
                    Date fechaActual = cal.getTime();    
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("es","CL"));
                    String strFechaHoraActual = sdf.format(fechaActual);
                    String strAccion = request.getParameter("Accion");
                    System.out.println("[SolicitudVacacionesController]"
                        + "Modificar estado de solicitud de vacaciones. "
                        + "Id solicitud: : " + solicitud.getId()    
                        + ", username: : " + userConnected.getUsername()
                        + ", empresaId: : " + solicitud.getEmpresaId()    
                        + ", rut_empleado: : " + solicitud.getRutEmpleado()
                        + ", accion a realizar: " + strAccion);

                    EmpleadoVO infoEmpleado = empleadosbp.getEmpleado(solicitud.getEmpresaId(), 
                        solicitud.getRutEmpleado());
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
                    
                    SolicitudVacacionesVO solicitudFromBd = 
                        solicitudesBp.getSolicitudByKey(solicitud.getId());
                    
                    System.out.println("[SolicitudVacacionesController]"
                        + "Aprobar/Rechazar solicitud de vacaciones. "
                        + "Id solicitud: : " + solicitud.getId()
                        + ", inicioVacaciones: " + solicitudFromBd.getInicioVacaciones()
                        + ", finVacaciones: " + solicitudFromBd.getFinVacaciones()
                        + ", diasEfectivosSolicitados: " + solicitudFromBd.getDiasEfectivosVacacionesSolicitadas()    
                        + ", username: : " + userConnected.getUsername()
                        + ", empresaId: : " + solicitudFromBd.getEmpresaId()    
                        + ", rut_empleado: : " + solicitudFromBd.getRutEmpleado()
                        + ", accion a realizar: " + strAccion
                        + ", nota_observacion: " + solicitud.getNotaObservacion());

                    solicitud.setDiasEfectivosVacacionesSolicitadas(solicitudFromBd.getDiasEfectivosVacacionesSolicitadas());
                    
                    EmpleadoVO infoEmpleado = empleadosbp.getEmpleado(solicitudFromBd.getEmpresaId(), 
                        solicitudFromBd.getRutEmpleado());
                    resultado.setEmpresaId(infoEmpleado.getEmpresa().getId());
                    resultado.setDeptoId(infoEmpleado.getDepartamento().getId());
                    resultado.setCencoId(infoEmpleado.getCentroCosto().getId());
                    MaintenanceVO doCreate = new MaintenanceVO();
                    String usernameApruebaRechaza = userConnected.getUsername();
                    if (strAccion.compareTo(Constantes.ESTADO_SOLICITUD_APROBADA) == 0){
                        /**
                        * Antes de aprobar una solicitud de vacaciones, 
                        * se debe verificar que no haya conflicto con otra ausencia existente.
                        */
                        //validar ausencias conflicto
                        ArrayList<DetalleAusenciaVO> ausenciasConflicto = detAusenciaBp.getAusenciasConflicto(solicitudFromBd.getRutEmpleado(),
                            false,
                            solicitudFromBd.getInicioVacaciones(),
                            solicitudFromBd.getFinVacaciones(), 
                            null, 
                            null);
                        if (ausenciasConflicto.isEmpty()){
                            System.out.println("[SolicitudVacacionesController]"
                                + "No hay conflicto. "
                                + "Aprobar solicitud de vacaciones "
                                + "e Insertar vacacion...");
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
                                "Solicitud de Vacaciones Aprobada", 
                                solicitudFromBd, userConnected, 
                                request, 
                                solicitudFromBd.getInicioVacaciones(), 
                                solicitudFromBd.getFinVacaciones(),
                                solicitudFromBd.getDiasEfectivosVacacionesSolicitadas());
                            
                            //*********************************************************
                            System.out.println("[SolicitudVacacionesController]"
                                + "Aprobar solicitud de vacaciones. Insertar vacacion...");
                            
                            SolicitudVacacionesVO auxSolicitud = 
                                solicitudesBp.getSolicitudByKey(solicitud.getId());
                            //Insertar vacacion
                            DetalleAusenciaVO newAusencia;
                            newAusencia = new DetalleAusenciaVO();
                            newAusencia.setEmpresaId(solicitudFromBd.getEmpresaId());
                            newAusencia.setRutEmpleado(solicitudFromBd.getRutEmpleado());
                            newAusencia.setFechaInicioAsStr(auxSolicitud.getInicioVacaciones());
                            newAusencia.setFechaFinAsStr(auxSolicitud.getFinVacaciones());
                            //fijos
                            newAusencia.setIdAusencia(Constantes.ID_AUSENCIA_VACACIONES);
                            newAusencia.setRutAutorizador(userConnected.getRunEmpleado());
                            newAusencia.setAusenciaAutorizada("S");
                            newAusencia.setPermiteHora("N");
                            newAusencia.setDiasEfectivosVacaciones(solicitud.getDiasEfectivosVacacionesSolicitadas());
                            MaintenanceVO insertResult = new MaintenanceVO();
                            insertResult = insertarVacacion(request, appProperties, parametrosSistema, userConnected, newAusencia);
                            
                        }else{
                            /**
                            * Hay conflicto con ausencias existentes: Rechazar Solicitud de vacaciones. 
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
                            System.out.println("[SolicitudVacacionesController]"
                                + "Rechazar solicitud de vacaciones. Dias solicitados: " + solicitud.getDiasEfectivosVacacionesSolicitadas());
                            notificaEventoSolicitud("SOLICITUD_RECHAZADA",
                                "Solicitud de Vacaciones Rechazada", 
                                solicitudFromBd, userConnected, request,
                                solicitudFromBd.getInicioVacaciones(), 
                                solicitudFromBd.getFinVacaciones(),
                                solicitudFromBd.getDiasEfectivosVacacionesSolicitadas());
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
                        System.out.println("[SolicitudVacacionesController]"
                            + "Rechazar solicitud de vacaciones. Dias solicitados: " + solicitud.getDiasEfectivosVacacionesSolicitadas());
                        notificaEventoSolicitud("SOLICITUD_RECHAZADA",
                            "Solicitud de Vacaciones Rechazada", 
                            solicitudFromBd, userConnected, request,
                            solicitudFromBd.getInicioVacaciones(), 
                            solicitudFromBd.getFinVacaciones(),
                            solicitudFromBd.getDiasEfectivosVacacionesSolicitadas());
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
    * Insertar ausencia=Vacacion
    */
    private MaintenanceVO insertarVacacion(HttpServletRequest _request,
            PropertiesVO _appProperties,
            HashMap<String, Double> _parametrosSistema,
            UsuarioVO _userConnected, 
            DetalleAusenciaVO _ausencia){
       
        System.out.println("[SolicitudVacacionesController.insertarVacacion]"
            + "Insertar detalle ausencia (VACACION)");
        VacacionesBp vacaciones = new VacacionesBp(_appProperties);
        
        DiasEfectivosVacacionesVO objDE = vacaciones.getDesgloseDiasVacaciones(_ausencia);
        _ausencia.setDiasEfectivosVBA(objDE.getDiasEfectivosVBA());
        _ausencia.setDiasEfectivosVP(objDE.getDiasEfectivosVP());
        _ausencia.setSaldoVBAPreVacaciones(objDE.getSaldoVBAPreVacaciones());
        _ausencia.setSaldoVPPreVacaciones(objDE.getSaldoVPPreVacaciones());
        _ausencia.setSaldoVBAPostVacaciones(objDE.getSaldoVBAPostVacaciones());
        _ausencia.setSaldoVPPostVacaciones(objDE.getSaldoVPPostVacaciones());
                
        String mensajeFinal = null;
        DetalleAusenciaBp ausenciasBp   = new DetalleAusenciaBp(_appProperties);
//        VacacionesBp vacacionesBp       = new VacacionesBp(_appProperties);
//        VacacionesVO saldoVacaciones = new VacacionesVO();
                
        MaintenanceEventVO resultado=new MaintenanceEventVO();
        resultado.setUsername(_userConnected.getUsername());
        resultado.setDatetime(new Date());
        resultado.setUserIP(_request.getRemoteAddr());
        resultado.setType("DAU");
        resultado.setEmpresaIdSource(_userConnected.getEmpresaId());
                                        
        resultado.setRutEmpleado(_ausencia.getRutEmpleado());
        System.out.println("[SolicitudVacacionesController.insertarVacacion]"
            + "Insertar detalle ausencia (VACACION)");
        MaintenanceVO doCreate = ausenciasBp.insertaVacacion(_ausencia, resultado);
        
        if (doCreate.isThereError()){
            mensajeFinal= "Error al insertar vacacion " + doCreate.getMsgError();
            doCreate.setMsg(mensajeFinal);
        }else{
            System.out.println("[SolicitudVacacionesController."
                + "insertarVacacion]"
                + "Actualizar saldos de vacaciones "
                + "en tabla detalle_ausencia (usar nueva funcion setsaldodiasvacacionesasignadas). "
                + "Run: "+ _ausencia.getRutEmpleado());
            ausenciasBp.actualizaSaldosVacaciones(_ausencia.getRutEmpleado());
            
            //Actualizar saldos en tabla vacaciones: columnas saldo_dias_vba y saldo_dias_vp
            System.out.println("[SolicitudVacacionesController."
                + "insertarVacacion]"
                + "Actualizar saldos en tabla vacaciones: columnas saldo_dias_vba y saldo_dias_vp");
            VacacionesVO objVacaciones = new VacacionesVO();
            objVacaciones.setEmpresaId(_ausencia.getEmpresaId());
            objVacaciones.setRutEmpleado(_ausencia.getRutEmpleado());
            objVacaciones.setSaldoDiasVBA(objDE.getSaldoVBAPostVacaciones());
            objVacaciones.setSaldoDiasVP(objDE.getSaldoVPPostVacaciones());
            vacaciones.updateSaldosVacacionesVBAyVP(objVacaciones);
            
        }
        System.out.println("[SolicitudVacacionesController.insertarVacacion]"
            + "Saliendo del metodo OK.");
        return doCreate;
    }
    
    /**
    * 
    * 
    */
    private NotificacionSolicitudVacacionesVO notificaEventoSolicitud(
            String _tipoEvento, 
            String _evento,
            SolicitudVacacionesVO _solicitud,
            UsuarioVO _userConnected, 
            HttpServletRequest _request,
            String _inicioVacaciones,
            String _finVacaciones,
            int _diasSolicitados){
    
        NotificacionSolicitudVacacionesVO evento = new NotificacionSolicitudVacacionesVO();
                
        EmpleadosBp empleadobp      = new EmpleadosBp();
        CentroCostoBp cencosbp      = new CentroCostoBp(null);
        VacacionesBp vacacionesbp   = new VacacionesBp(null);
        
        System.out.println("[SolicitudVacacionesController."
            + "notificaEventoSolicitud]Invocar getEmpleado. "
            + "EmpresaId: " + _solicitud.getEmpresaId()
            + ", rutEmpleado: " + _solicitud.getRutEmpleado());
                
        List<VacacionesVO> infoVacaciones = 
            vacacionesbp.getInfoVacaciones(_solicitud.getEmpresaId(), 
                _solicitud.getRutEmpleado(), -1, -1, -1, "vac.rut_empleado");
        VacacionesVO saldoVacaciones=new VacacionesVO();
        saldoVacaciones = infoVacaciones.get(0);
        EmpleadoVO empleado = 
            empleadobp.getEmpleado(_solicitud.getEmpresaId(), 
            _solicitud.getRutEmpleado());
        Calendar cal            = Calendar.getInstance(new Locale("es","CL"));
        Date fechaActual        = cal.getTime();    
        SimpleDateFormat sdf    = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("es","CL"));
        String fromLabel        = "Gestion asistencia";
        String fromMail         = m_properties.getKeyValue("mailFrom");
        String asuntoMail       = "Sistema de Gestion-" + _evento;
        String mailTo           = empleado.getEmail();
        System.out.println("[SolicitudVacacionesController."
            + "notificaEventoSolicitud]"
            + "Email por defecto: " + mailTo);
        String notaObservacion = _request.getParameter("notaObservacion");
        String cadenaNombres    = "";
        boolean hayJefeNacional = true;
        boolean hayJefeDirecto  = true;
        int diasSolicitados     = 0;
        
        if (_tipoEvento.compareTo("SOLICITUD_APROBADA") == 0 || _tipoEvento.compareTo("SOLICITUD_RECHAZADA") == 0){
            _solicitud.setInicioVacaciones(_inicioVacaciones);
            _solicitud.setFinVacaciones(_finVacaciones);
            diasSolicitados = _diasSolicitados;
        }
        
        if (_tipoEvento.compareTo("INGRESO_SOLICITUD") == 0){
            String cadenaEmails = "";
            System.out.println("[SolicitudVacacionesController."
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
                    System.out.println("[SolicitudVacacionesController."
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
                System.out.println("[SolicitudVacacionesController."
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
                    System.out.println("[SolicitudVacacionesController."
                        + "notificaEventoSolicitud]Rescatar emails del Jefe Tecnico Nacional");
                    for(EmpleadoVO itJefazos : listaJefesTecnicosNacional){
                        cadenaEmails += itJefazos.getEmail() + ",";
                        cadenaNombres += itJefazos.getNombres()+ ",";
                        if (itJefazos.getApePaterno() != null) cadenaNombres += " " + itJefazos.getApePaterno();
                        if (itJefazos.getApeMaterno() != null) cadenaNombres += " " + itJefazos.getApeMaterno();
                    }
                    System.out.println("[SolicitudVacacionesController."
                        + "notificaEventoSolicitud]Emails Jefe Tecnico Nacional: " + cadenaEmails);
                    System.out.println("[SolicitudVacacionesController."
                        + "notificaEventoSolicitud]Nombres Jefe Tecnico Nacional: " + cadenaNombres);
                    
                    cadenaEmails = cadenaEmails.substring(0, cadenaEmails.length() - 1);
                    mailTo      = cadenaEmails;
                }else{
                    System.out.println("[SolicitudVacacionesController."
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
        
        if (_solicitud.getDiasEfectivosVacacionesSolicitadas() == 0){
            diasSolicitados = 
                vacacionesbp.getDiasEfectivos(_solicitud.getInicioVacaciones(), 
                    _solicitud.getFinVacaciones(), 
                    saldoVacaciones.getDiasEspeciales(), 
                    _solicitud.getEmpresaId(), 
                    _solicitud.getRutEmpleado());
        }
        
        String mensaje = "RUN trabajador: " + empleado.getCodInterno()
            + "<br>Nombre trabajador: " + empleado.getNombreCompleto()
            + "<br>Nombre de usuario solicitante: " + _solicitud.getUsernameSolicita()    
            + "<br>Institucion: " + empleado.getEmpresaNombre()
            + "<br>Centro de costo: " + empleado.getCencoNombre()
            + "<br>Fecha/Hora solicitud: " + sdf.format(fechaActual)
            + "<br>Inicio vacaciones: " + _solicitud.getInicioVacaciones()
            + "<br>Termino vacaciones: " + _solicitud.getFinVacaciones()
            + "<br>Dias solicitados: " + diasSolicitados;
        
        String mailBody = "Evento:" + _evento
            + "<br>RUN trabajador: " + empleado.getCodInterno()
            + "<br>Nombre trabajador: " + empleado.getNombreCompleto()
            + "<br>Nombre de usuario solicitante: " + _solicitud.getUsernameSolicita()    
            + "<br>Institucion: " + empleado.getEmpresaNombre()
            + "<br>Centro de costo: " + empleado.getCencoNombre()
            + "<br>Fecha/Hora solicitud: " + sdf.format(fechaActual)
            + "<br>Inicio vacaciones: " + _solicitud.getInicioVacaciones()
            + "<br>Termino vacaciones: " + _solicitud.getFinVacaciones()
            + "<br>Dias solicitados: " + diasSolicitados
            + "<br>Nota/Observacion: " + notaObservacion;
        
        evento.setTipoEvento(_tipoEvento);
        evento.setMensajeFinal(mensaje);
        evento.setRunTrabajador(empleado.getCodInterno());
        evento.setNombreTrabajador(empleado.getNombreCompleto());
        evento.setInstitucion(empleado.getEmpresaNombre());
        evento.setCentroCosto(empleado.getCencoNombre());
        evento.setFechaHoraSolicitud(sdf.format(fechaActual));
        evento.setInicioVacaciones(_solicitud.getInicioVacaciones());
        evento.setTerminoVacaciones(_solicitud.getFinVacaciones());
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
        
        System.out.println("[SolicitudVacacionesController."
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
    * Obtiene destinatario(s) de la solicitud de vacaciones
    */
    private ArrayList<DestinatarioSolicitudVO> getDestinatariosSolicitud(SolicitudVacacionesVO _solicitud){
        CentroCostoBp cencosbp      = new CentroCostoBp(null);
        EmpleadosBp empleadobp      = new EmpleadosBp();
        EmpleadoVO empleado         = 
            empleadobp.getEmpleado(_solicitud.getEmpresaId(), 
            _solicitud.getRutEmpleado());    
        ArrayList<DestinatarioSolicitudVO> destinatarios = new ArrayList<>();
        String strNombre= "";
        String strEmail = "";
        String strCargo = "";
        String mailTo               = empleado.getEmail();
        boolean hayJefeNacional     = true;
        boolean hayJefeDirecto      = true;
        
        System.out.println("[SolicitudVacacionesController."
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
                System.out.println("[SolicitudVacacionesController."
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
                System.out.println("[SolicitudVacacionesController."
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
            System.out.println("[SolicitudVacacionesController."
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
                System.out.println("[SolicitudVacacionesController."
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
