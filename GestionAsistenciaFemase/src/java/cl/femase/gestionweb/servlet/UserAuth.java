/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.servlet;

import cl.femase.gestionweb.business.AusenciaBp;
import cl.femase.gestionweb.business.CargoBp;
import cl.femase.gestionweb.business.CentroCostoBp;
import cl.femase.gestionweb.business.CodigoErrorRechazoBp;
import cl.femase.gestionweb.business.DetalleAusenciaBp;
import cl.femase.gestionweb.business.ComunaBp;
import cl.femase.gestionweb.business.DepartamentoBp;
import cl.femase.gestionweb.business.DispositivoBp;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.business.EmpresaBp;
import cl.femase.gestionweb.business.MaintenanceEventsBp;
import cl.femase.gestionweb.business.MarcasBp;
import cl.femase.gestionweb.business.ParametroBp;
import cl.femase.gestionweb.business.PerfilUsuarioBp;
import cl.femase.gestionweb.business.RegionBp;
import cl.femase.gestionweb.business.TipoAusenciaBp;
import cl.femase.gestionweb.business.TipoDispositivoBp;
import cl.femase.gestionweb.business.TipoMarcaManualBp;
import cl.femase.gestionweb.business.TurnoRotativoBp;
import cl.femase.gestionweb.business.TurnoRotativoCicloBp;
import cl.femase.gestionweb.business.TurnosBp;
import cl.femase.gestionweb.business.UsuarioBp;
import cl.femase.gestionweb.common.ClientInfo;
import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.dao.AfpDAO;
import cl.femase.gestionweb.dao.LogErrorDAO;
import cl.femase.gestionweb.dao.ProveedorCorreoDAO;
import cl.femase.gestionweb.vo.CentroCostoVO;
import cl.femase.gestionweb.vo.DepartamentoVO;
import cl.femase.gestionweb.vo.DispositivoVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.EmpresaVO;
import cl.femase.gestionweb.vo.LogErrorVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.MarcaVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.ProveedorCorreoVO;
import cl.femase.gestionweb.vo.TurnoRotativoCicloVO;
import cl.femase.gestionweb.vo.TurnoVO;
import cl.femase.gestionweb.vo.UsuarioCentroCostoVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.json.JSONObject;

/**
 *
 * @author Alexander
 */
public class UserAuth extends BaseServlet {

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    
    /** 
    * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
    * @param request servlet request
    * @param response servlet response
    * @throws ServletException if a servlet-specific error occurs
    * @throws IOException if an I/O error occurs
    */
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        PrintWriter out     = response.getWriter();
        LogErrorDAO logDao  = new LogErrorDAO();
        LogErrorVO log      = new LogErrorVO();
        
        try {
            System.out.println(WEB_NAME+"UserAuth]Intentando login para "
                + "username: "+ request.getParameter("username"));
                
            SimpleDateFormat fechaFmt = new SimpleDateFormat("yyyy-MM-dd");
            Locale localeCl = new Locale("es", "CL");
            Calendar calendarioHoy=Calendar.getInstance(localeCl);
            String fechaActual = fechaFmt.format(calendarioHoy.getTime());
            
            String empresaId = request.getParameter("empresaId");
            UsuarioVO user = new UsuarioVO(request.getParameter("username"),
                request.getParameter("password"),
                empresaId);
            
            //sesion del usuario
            HttpSession session = request.getSession();
            boolean loginOk = true;
            
            Calendar thecal=Calendar.getInstance();
            SimpleDateFormat sdf=new SimpleDateFormat("HHmmss");
            String strTimeConnection=sdf.format(thecal.getTime());
            ServletContext application = this.getServletContext();
            session.removeAttribute("modulosSistema");
            PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
            UsuarioBp userBp    = new UsuarioBp(appProperties);
                        
            userBp.openDbConnection();
            UsuarioVO userOk = userBp.getLogin(user);
            
            System.out.println(WEB_NAME+"UserAuth]Intentando Autenticar "
                + "usuario: " + user.getUsername());
            
            if (userOk != null){
                log.setUserName(userOk.getUsername());
                log.setIp(request.getRemoteAddr());
                
                System.out.println(WEB_NAME+"UserAuth]Autenticando "
                    + "usuario: " + userOk.getUsername()
                    + ", perfil_usuario: " + userOk.getNomPerfil());

                //test
                //session = null;
                //Setea maximo tiempo de inactividad (en segundos)
                session.setMaxInactiveInterval(30 * 60);//30 minutos
                Date expiry = new Date(session.getLastAccessedTime() + session.getMaxInactiveInterval()*1000);
                System.out.println(WEB_NAME+"UserAuth]La sesion expira el: " + expiry);
                
                AusenciaBp ausenciasBp = new AusenciaBp(appProperties);    
                CargoBp cargosBp = new CargoBp(appProperties);
                CodigoErrorRechazoBp codErrorRechazoBp = new CodigoErrorRechazoBp(appProperties);
                CentroCostoBp cencosBp = new CentroCostoBp(appProperties);
                ComunaBp comunaBp = new ComunaBp(appProperties);     
                DepartamentoBp deptosBp = new DepartamentoBp(appProperties);
                DetalleAusenciaBp autorizaAusenciaBp = new DetalleAusenciaBp(appProperties);
                DispositivoBp devicesBp = new DispositivoBp(appProperties);
                EmpleadosBp empleadosBp = new EmpleadosBp(appProperties);    
                EmpresaBp empresasBp = new EmpresaBp(appProperties);
                MarcasBp marcasBp = new MarcasBp(appProperties);
                MaintenanceEventsBp eventosBp = new MaintenanceEventsBp(appProperties);
                PerfilUsuarioBp perfilusuarioBp = new PerfilUsuarioBp(appProperties);
                RegionBp regionesBp = new RegionBp(appProperties);
                TipoDispositivoBp tipos2bp = new TipoDispositivoBp(appProperties);    
                TurnosBp turnosBp = new TurnosBp(appProperties);
                TurnoRotativoBp turnoRotativoBp = new TurnoRotativoBp(appProperties);
                TipoAusenciaBp tiposAusencias = new TipoAusenciaBp(appProperties);    
                TipoMarcaManualBp tpMarcaManualBp = new TipoMarcaManualBp(appProperties);
                ProveedorCorreoDAO proveedorMailDao = new ProveedorCorreoDAO();
                
                MaintenanceEventVO resultado=new MaintenanceEventVO();
                /**
                * 20200229-001: Agregar info a tabla eventos: Sistema operativo y navegador
                */
                ClientInfo clientInfo = new ClientInfo();
                clientInfo.printClientInfo(request);
                resultado.setOperatingSystem(clientInfo.getClientOS(request));
                resultado.setBrowserName(clientInfo.getClientBrowser(request));
                
                resultado.setUsername(userOk.getUsername());
                resultado.setDatetime(new Date());
                resultado.setUserIP(request.getRemoteAddr());
                resultado.setType("AUT");
                resultado.setEmpresaIdSource(userOk.getEmpresaId());
                resultado.setEmpresaId(userOk.getEmpresaId());
                resultado.setDeptoId(userOk.getDeptoId());
                resultado.setCencoId(userOk.getIdCencoUsuario());
            
                resultado.setDescription("Login exitoso");
                eventosBp.addEvent(resultado);
                                
                session.setAttribute("modulosSistema", userBp.getModulosSistemaByPerfilUsuario(userOk.getIdPerfil()));
                session.setAttribute("usuarioObj", userOk);
                session.setAttribute("user"+request.getParameter("username"), userOk.getUsername());
                System.out.println(WEB_NAME+"UserAuth]Usuario conectado, "
                    + "username: " + userOk.getUsername()
                    + ", hora conexion: " + userOk.getHoraConexion()
                    + ", marcacion_virtual? " + userOk.getMarcacionVirtual());
                m_logger.debug("[UserAuth]usuario conectado - username: " + userOk.getUsername()
                    +"- hora conexion: " + userOk.getHoraConexion());

                // user.setUserConnected("Y");
                userBp.setConnectionStatus(user);

                session.setAttribute("path_images", appProperties.getImagesPath());
                
                if (userOk.getCencos().isEmpty()){
                    System.out.println(WEB_NAME+"UserAuth]Setear lista de cencos del usuario "
                        + "en sesion...");
                    userOk.setCencos(userBp.getCencosUsuario(userOk));
                }
                System.out.println(WEB_NAME+"UserAuth]Obtener lista "
                    + "de empresas en sesion...");
                List<EmpresaVO> listaEmpresas = empresasBp.getEmpresas(userOk, null, 0, 0, "empresa_nombre");
                LinkedHashMap<String,List<DepartamentoVO>> allDeptos = new LinkedHashMap<>();
                LinkedHashMap<String,List<CentroCostoVO>> allCencos = new LinkedHashMap<>();

                session.setAttribute("empresas", listaEmpresas);
                
                AfpDAO afpdao = new AfpDAO(appProperties);
                session.setAttribute("afps", afpdao.getAfps());

                System.out.println(WEB_NAME+"UserAuth]Obtener una lista "
                    + "con los departamentos para cada empresa existente");
                //iterar empresas y obtener un Linkedhash con los departamentos para cada empresa existente
                for (EmpresaVO itEmpresa : listaEmpresas) {
                    System.out.println(WEB_NAME+"UserAuth]Carga departamentos para empresaID: "+itEmpresa.getId());
                    allDeptos.put(itEmpresa.getId(), deptosBp.getDepartamentosEmpresa(userOk, itEmpresa.getId()));
                }
                    
                System.out.println(WEB_NAME+"UserAuth]Obtener una lista "
                    + "con los centros de costo para cada departatamento existente");
                cencosBp.openDbConnection();
                //iterar departamentos y obtener una lista con los centro de costo existente para cada departamento
                for(String key : allDeptos.keySet()) {
                    //System.out.println(WEB_NAME+"[UserAuth]Carga cencos empresa: "+key);
                    List<DepartamentoVO> listaDeptos = allDeptos.get(key);
                    for(int i=0; i < listaDeptos.size(); i++){
                        DepartamentoVO itDepto = listaDeptos.get(i);
                        //System.out.println(WEB_NAME+"[UserAuth]Carga cencos para deptoID: "+itDepto.getId());
                        allCencos.put(itDepto.getId(), cencosBp.getCentrosCostoDepto(userOk, itDepto.getId()));
                    }
                }
                cencosBp.closeDbConnection();
                //lista de departamentos. Key=empresa_id
                session.setAttribute("allDepartamentos", allDeptos);
                //lista de centros de costo. Key=depto_id
                session.setAttribute("allCencos", allCencos);
                    
                /** cargar lista de comunas en sesion */
                session.setAttribute("comunas", comunaBp.getComunas(null, -1, 0, 0, "comuna_nombre"));
                    
                /** cargar lista de tipos eventos en sesion */
                session.setAttribute("tipos_eventos", eventosBp.getEventTypes());

                session.setAttribute("tipos_dispositivos", tipos2bp.getTipos(null, 0, 0, "dev_type_name"));

                /** cargar lista de dispositivos en sesion */
                devicesBp.openDbConnection();
                session.setAttribute("dispositivos", devicesBp.getDispositivos(null, -1, 1,null,0,0,"device_id"));
                devicesBp.closeDbConnection();

                /** cargar lista de turnos en sesion */
                session.setAttribute("turnos", 
                    turnosBp.getTurnos(userOk.getEmpresaId(),null, -1, 0, 0, "id_turno"));
                session.setAttribute("turnos_rotativos", 
                    turnoRotativoBp.getTurnos(userOk.getEmpresaId(), null,-1, 0, 0, "nombre_turno"));
                                
                /** cargar lista de tipos de marcas manuales */
                System.out.println(WEB_NAME+"UserAuth]Cargar en sesion "
                    + "lista de tipos de marcas manuales...");

                session.setAttribute("tiposMarcasManuales", 
                    tpMarcaManualBp.getTipos(null, "S"));

                List<TurnoVO> turnosEmpresa = turnosBp.getTurnosByEmpresa(userOk.getEmpresaId()); 
                session.setAttribute("turnos_empresa", turnosEmpresa);
                    
                if (userOk.getIdPerfil() != Constantes.ID_PERFIL_SUPER_ADMIN){
                    session.setAttribute("turnos_cencos", 
                        turnosBp.getTurnosByCencos(userOk.getEmpresaId(), userOk.getCencos()));
                }else {
                    session.setAttribute("turnos_cencos", 
                        turnosEmpresa);
                }
                    
                /** cargar lista de regiones en sesion */
                session.setAttribute("regiones", 
                    regionesBp.getRegiones(null, 0, 0, "short_name"));

                session.setAttribute("tipos_ausencia", 
                    tiposAusencias.getTipos(null, 0, 0, "tp_ausencia_nombre"));
                    
                /** a ser usados en jsp detalle_ausencias*/
                System.out.println(WEB_NAME+"UserAuth]Cargar en sesion "
                    + "lista de autorizadores de ausencias...");
                session.setAttribute("autorizadores", 
                    autorizaAusenciaBp.getAutorizadoresDisponibles(userOk));

                session.setAttribute("empleados", 
                    empleadosBp.getEmpleados(null,null,-1,-1,null,null,null,null,0,0,"empl_rut"));

                session.setAttribute("ausencias", 
                    ausenciasBp.getAusencias(null,
                        -1,
                        1,
                        "-1",
                        "-1",
                        0, 
                        0, 
                        "ausencia_nombre"));

                session.setAttribute("hashAusencias", 
                    ausenciasBp.getAusencias());
                /** cargar lista de cencos en sesion */
                session.setAttribute("cargos", cargosBp.getCargos(null, 1, 0, 0, "cargo_nombre"));
                /** cargar lista de perfiles de usuario en sesion */
                session.setAttribute("perfiles", 
                    perfilusuarioBp.getPerfilesByUsuario(userOk));
                System.out.println(WEB_NAME+"UserAuth]Carga lista de usuarios en sesion");
                /** cargar lista de de usuario en sesion */
                session.setAttribute("usuarios", 
                    userBp.getUsuarios(null,null,null,
                    -1,1,
                    empresaId, 0,0,
                    "usr_nombres"));
                //Cargar cencos a los cuales tiene acceso el usuario
                List<UsuarioCentroCostoVO> cencosEmpleado = new ArrayList<>();
                if (userOk.getIdPerfil() != Constantes.ID_PERFIL_SUPER_ADMIN){//solo el perfil usuario empleado
                    System.out.println(WEB_NAME+"UserAuth]Usuario Normal. "
                        + "Cargar todos los centros de costo de "
                        + "la empresa del usuario conectado: "
                        + "[usuario, empresa] = [" + userOk.getUsername() 
                        + "," + userOk.getEmpresaId() + "]");
                    cencosEmpleado = userBp.getCencosUsuario(userOk);
                }else{
                    System.out.println(WEB_NAME+"UserAuth]Usuario Super Admin. "
                        + "Cargar todos los centros de costo del Sistema");
                    cencosEmpleado = cencosBp.getAllCentrosCosto(userOk.getUsername());
                }
                    
                userBp.closeDbConnection();
                session.setAttribute("cencos_empleado", cencosEmpleado);
                    
                session.setAttribute("errores_rechazos", 
                    codErrorRechazoBp.getCodigos(null, 0, 0, "cod_error_rechazo"));
                
                //****************************************************************
                //****************************************************************
                System.out.println(WEB_NAME+"UserAuth]Seteo de parametros "
                    + "de Sistema para la empresa: " + userOk.getEmpresaId());
                
                ParametroBp parametroBp  = new ParametroBp(null);
                HashMap<String, Double> parametrosSistema = parametroBp.getParametrosEmpresa(userOk.getEmpresaId());
                
//                HashMap<String, Double> parametrosSistema = new HashMap<>();
//                
//                ParametroVO parametro = parametroBp.getParametroByKey(userOk.getEmpresaId(), "factor_vacaciones");
//                if (parametro != null) parametrosSistema.put("factor_vacaciones", parametro.getValor());
//
//                parametro = parametroBp.getParametroByKey(userOk.getEmpresaId(), "min_meses_cotizando");
//                if (parametro != null) parametrosSistema.put("min_meses_cotizando", parametro.getValor());
//                
//                parametro = parametroBp.getParametroByKey(userOk.getEmpresaId(), "n_meses_add_vac_prog");
//                if (parametro != null) parametrosSistema.put("n_meses_add_vac_prog", parametro.getValor());
//
//                parametro = parametroBp.getParametroByKey(userOk.getEmpresaId(), "factor_vac_zona_extrema");
//                if (parametro != null) parametrosSistema.put("factor_vac_zona_extrema", parametro.getValor());
//                
//                parametro = parametroBp.getParametroByKey(userOk.getEmpresaId(), "factor_vac_especiales");
//                if (parametro != null) parametrosSistema.put("factor_vac_especiales", parametro.getValor());
                                
                session.setAttribute("parametros_sistema", parametrosSistema);
                appProperties.setParametrosSistema(parametrosSistema);
                application.setAttribute("appProperties", appProperties);
                
                List<ProveedorCorreoVO> proveedoresCorreo = proveedorMailDao.getProveedores(null, 0, 0, "provider_domain");
                session.setAttribute("proveedores_correo", proveedoresCorreo);
                
                session.removeAttribute("tieneTurnoRotativo");
                session.setAttribute("tieneTurnoRotativo", false);
                
                //set ciclos para turnos rotativos
                System.out.println(WEB_NAME+"[UserAuth]Set ciclos para turnos rotativos");
                TurnoRotativoCicloBp cicloBp = new TurnoRotativoCicloBp(appProperties);
                List<TurnoRotativoCicloVO> ciclos = 
                    cicloBp.getCiclos(userOk.getEmpresaId(), 0, null, 0, -1, "ciclo_num_dias");
                session.setAttribute("ciclos", ciclos);
                
                //Buscar los centros de costo asignados al usuario tipo empleado
                if (userOk.getIdPerfil() == Constantes.ID_PERFIL_EMPLEADO 
                        || userOk.getIdPerfil() == Constantes.ID_PERFIL_DIRECTOR){
                    if (userOk.getIdPerfil() == Constantes.ID_PERFIL_EMPLEADO){
                        System.out.println(WEB_NAME+"[UserAuth]Usuario tiene perfil empleado");
                        EmpleadoVO infoEmpleado = 
                            empleadosBp.getEmpleado(userOk.getEmpresaId(), 
                                userOk.getUsername());
                        String deptoId  = infoEmpleado.getDeptoId();
                        int cencoId     = infoEmpleado.getCencoId();
                        System.out.println(WEB_NAME+"UserAuth]Usuario empleado autenticado. "
                            + "deptoId: " + deptoId 
                            + ", cencoId: " + cencoId);
                        CentroCostoVO cenco = cencosBp.getCentroCostoByKey(deptoId, cencoId);
                        HashMap<String, DispositivoVO> dispositivos = cenco.getDispositivos();
                        session.setAttribute("dispositivosUsuario", dispositivos);
                    
                        //setear si el usuario empleado tiene turno rotativo
                        int idTurnoRotativo = turnosBp.getTurnoRotativo(userOk.getEmpresaId());
                        boolean tieneTurnoRotativo=false;
                        if (idTurnoRotativo == infoEmpleado.getIdTurno()){
                            System.out.println(WEB_NAME+"[MarcasController.mostrarMarcas]"
                                + "Empleado.rut: " + infoEmpleado.getRut()
                                + ", nombres: " + infoEmpleado.getNombres()
                                + ", Tiene turno rotativo");
                            tieneTurnoRotativo = true;
                            session.setAttribute("tieneTurnoRotativo", tieneTurnoRotativo);
                        }
                    }
                    //else{
////                        System.out.println(WEB_NAME+"[UserAuth]Usuario perfil Director");
////                        System.out.println(WEB_NAME+"[UserAuth]Rescatar solicitudes "
////                            + "de vacaciones pendientes de algunos de los empleados "
////                            + "en alguno de los cencos del usuario Director");
////                        SolicitudVacacionesBp solicitudesBp = new SolicitudVacacionesBp(appProperties);
////                        
////                        /**
////                            - Si es perfil usuario director:
////                            - Rescatar todas las solicitudes de vacaciones que estén pendientes. En alguno de los cencos donde el usuario es director
////                        */
////                        List<UsuarioCentroCostoVO> listaCencos = userOk.getCencos();
////                        int numSolicitudes = 0;
////                        for (int i = 0; i < listaCencos.size(); i++) {
////                            UsuarioCentroCostoVO itcenco = listaCencos.get(i);
////                            List<SolicitudVacacionesVO> listaSolicitudes = 
////                                solicitudesBp.getSolicitudes(userOk.getEmpresaId(), 
////                                itcenco.getCcostoId(),
////                                null,
////                                null,
////                                null,
////                                userOk.getUsername(),
////                                false,
////                                Constantes.ESTADO_SOLICITUD_PENDIENTE,
////                                0, 
////                                0, 
////                                "solic_fec_ingreso");
////                            numSolicitudes += listaSolicitudes.size();
////                        }
////                        session.setAttribute("solicitudes_pendientes", numSolicitudes);
                    //}
                    
                    //buscar ultimas marcas realizadas en la fecha actual
                    LinkedHashMap<String,MarcaVO> allMarcas = marcasBp.getAllMarcas(userOk.getEmpresaId(), 
                        userOk.getUsername(), fechaActual, fechaActual);
                    session.setAttribute("marcasUsuario", allMarcas);
                }
                
                ////userBp.closeDbConnection();
                request.getRequestDispatcher("/homepage.jsp").forward(request, response);//frameset

            }else{
                MaintenanceEventVO resultado=new MaintenanceEventVO();
                ClientInfo clientInfo = new ClientInfo();
                clientInfo.printClientInfo(request);
                resultado.setOperatingSystem(clientInfo.getClientOS(request));
                resultado.setBrowserName(clientInfo.getClientBrowser(request));
                resultado.setUsername(user.getUsername());
                resultado.setDatetime(new Date());
                resultado.setUserIP(request.getRemoteAddr());
                resultado.setType("AUT");
                resultado.setDescription("Intento fallido "
                    + "de ingreso al Sistema. "
                    + "Nombre de usuario:" 
                    + user.getUsername());
                MaintenanceEventsBp eventosBp   = new MaintenanceEventsBp(appProperties);
                eventosBp.addEvent(resultado);
                
                userBp.closeDbConnection();
                session.setAttribute("mensaje", "Usuario "+request.getParameter("username")
                    +" o clave no validos");
                m_logger.debug("Usuario "+request.getParameter("username")
                        +" o clave no validos");
                request.getRequestDispatcher("/index.jsp").forward(request, response);
            }
            
            userBp.closeDbConnection();
        } catch(Exception ex){ 
            System.err.println("[UserAuth]Error: "+ex.toString());
            String excdetail = Arrays.toString(ex.getStackTrace());
            String exclabel = ex.toString();
            JSONObject jsonObj = 
                Utilidades.generateErrorMessage(this.getClass().getName(), ex);
            System.out.println(WEB_NAME+"-->JsonStr: " + jsonObj.toString());
            log.setModulo(Constantes.LOG_MODULO_AUTENTICACION);
            log.setEvento(Constantes.LOG_EVENTO_AUTENTICACION);
            log.setLabel(exclabel);
            log.setDetalle(jsonObj.toString());
            logDao.insert(log);
            ex.printStackTrace();
            
        } finally { 
            out.close();
        }
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        if (request.getParameter("username") != null && request.getParameter("username").compareTo("null") != 0){
            setResponseHeaders(response);
            processRequest(request, response);
        }else{
            //newLogin/index.jsp
            request.getRequestDispatcher("/").forward(request, response);
        }
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
         if (request.getParameter("username") != null && request.getParameter("username").compareTo("null") != 0){
            setResponseHeaders(response);
            processRequest(request, response);
        }else{
            //newLogin/index.jsp
            request.getRequestDispatcher("/").forward(request, response);
        }
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

   
}
