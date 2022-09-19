package cl.femase.gestionweb.servlet.mantencion;

import cl.femase.gestionweb.business.DetalleAusenciaBp;
import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.business.VacacionesBp;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
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
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

public class VacacionesController extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 995L;
    
    public VacacionesController() {
        
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

    
    /**
    * 
    */
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(true);
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        VacacionesBp vacacionesBp = new VacacionesBp(appProperties);
        EmpleadosBp empleadosbp=new EmpleadosBp(appProperties);
        
        if(request.getParameter("action") != null){
            System.out.println(WEB_NAME+"[VacacionesController]"
                + "action is: " + request.getParameter("action")
                + ", tipo: " + request.getParameter("tipo"));
            List<VacacionesVO> listaObjetos = new ArrayList<VacacionesVO>();
            String action                   = (String)request.getParameter("action");
            String tipo                   = (String)request.getParameter("tipo");
            Gson gson                       = new Gson();
            response.setContentType("application/json");

            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("VAC");
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
            resultado.setDeptoId(action);
            //Fetch Data from User Table
            int startPageIndex      = 0;
            int numRecordsPerPage   = 10;
            String jtSorting        = "rut_empleado asc";
            
            if (request.getParameter("jtStartIndex") != null) 
                startPageIndex = Integer.parseInt(request.getParameter("jtStartIndex"));
            if (request.getParameter("jtPageSize") != null) 
                numRecordsPerPage   = Integer.parseInt(request.getParameter("jtPageSize"));
            if (request.getParameter("jtSorting") != null) 
                jtSorting   = request.getParameter("jtSorting");
            
            if (jtSorting.contains("rutEmpleado")) jtSorting = jtSorting.replaceFirst("rutEmpleado","rut_empleado");
            else if (jtSorting.contains("nombreEmpleado")) jtSorting = jtSorting.replaceFirst("nombreEmpleado","nombre");
            
            HashMap<String, Double> parametrosSistema = 
                (HashMap<String, Double>)session.getAttribute("parametros_sistema");
                    
            //objeto usado para update/insert
            VacacionesVO infoVacacion = new VacacionesVO();
            
            System.out.println(WEB_NAME+"[VacacionesController]"
                + "empresaId: " + request.getParameter("empresaId")
                + ", empresaKey: " + request.getParameter("empresaKey")
                + ", rut empleado: " + request.getParameter("rutEmpleado"));
            
            if(request.getParameter("rowKey") != null){
                infoVacacion.setRowKey(request.getParameter("rowKey"));
            }
            
            if(request.getParameter("empresaId") != null){
                infoVacacion.setEmpresaId(request.getParameter("empresaId"));
            }
           
            infoVacacion.setEmpresaId(request.getParameter("empresaKey"));//request.getParameter("empresaCod"));
            
            if(request.getParameter("rutEmpleado") != null){
                infoVacacion.setRutEmpleado(request.getParameter("rutEmpleado"));
            }
            if(request.getParameter("diasAcumulados") != null){
                infoVacacion.setDiasAcumulados(Integer.parseInt(request.getParameter("diasAcumulados")));
            }
            if(request.getParameter("diasProgresivos") != null){
                infoVacacion.setDiasProgresivos(Integer.parseInt(request.getParameter("diasProgresivos")));
            }
            if(request.getParameter("diasEspeciales") != null){
                infoVacacion.setDiasEspeciales(request.getParameter("diasEspeciales"));
            }
            if(request.getParameter("saldoDias") != null){
                infoVacacion.setSaldoDias(Integer.parseInt(request.getParameter("saldoDias")));
            }
            
            //2020-03-17
            if(request.getParameter("afpCode") != null){
                infoVacacion.setAfpCode(request.getParameter("afpCode"));
            }
            if(request.getParameter("fechaCertifVacacionesProgresivas") != null){
                infoVacacion.setFechaCertifVacacionesProgresivas(request.getParameter("fechaCertifVacacionesProgresivas"));
            }
            if(request.getParameter("diasAdicionales") != null){
                infoVacacion.setDiasAdicionales(Integer.parseInt(request.getParameter("diasAdicionales")));
            }
            
            /**
            * 19-08-2021
            */
            if(request.getParameter("numCotizaciones") != null && request.getParameter("numCotizaciones").compareTo("") != 0){
                infoVacacion.setNumCotizaciones(Integer.parseInt(request.getParameter("numCotizaciones")));
            }
            if(request.getParameter("otraInstitucionEmisoraCertif") != null){
                infoVacacion.setOtraInstitucionEmisoraCertif(request.getParameter("otraInstitucionEmisoraCertif"));
            }
            
            resultado.setRutEmpleado(infoVacacion.getRutEmpleado());
            
            /**
            * Si no tiene certificado AFP seteado 
            *   y se ingresa uno en el formulario de nuevo/modificar registro 
            *   --> num cotizaciones =120
            * 
            */
            boolean tieneCertificadoAFPInBd = false;
            boolean recentCertificadoAFP    = false;
            if (infoVacacion.getFechaCertifVacacionesProgresivas() != null && 
                infoVacacion.getFechaCertifVacacionesProgresivas().compareTo("") != 0){
                    recentCertificadoAFP = true;
            }
            
            VacacionesVO dataVacacionesInBd = new VacacionesVO();
            List<VacacionesVO> infoVacaciones = 
                vacacionesBp.getInfoVacaciones(infoVacacion.getEmpresaId(), infoVacacion.getRutEmpleado(), 
                    -1, -1, -1, "vac.rut_empleado");
            if (!infoVacaciones.isEmpty()){
                dataVacacionesInBd = infoVacaciones.get(0);
                if (dataVacacionesInBd.getFechaCertifVacacionesProgresivas() != null && 
                    dataVacacionesInBd.getFechaCertifVacacionesProgresivas().compareTo("") != 0){
                        tieneCertificadoAFPInBd = true;
                }
            }      
            
            if (recentCertificadoAFP && !tieneCertificadoAFPInBd){
                BigDecimal bdmmc = new BigDecimal(parametrosSistema.get("min_meses_cotizando"));
                infoVacacion.setNumActualCotizaciones(bdmmc.intValue());
            }
            
            if (action.compareTo("list") == 0) {
                try{
                    String paramCencoID  = request.getParameter("cencoId");
                    
                    String paramEmpresa = null;
                    String paramDepto   = null;
                    String cencoId      = "";
                    System.out.println(WEB_NAME+"[VacacionesController]"
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
                    System.out.println(WEB_NAME+"[VacacionesController]"
                        + "Listar info vacaciones. "
                        + "empresa: " + paramEmpresa
                        +", depto: " + paramDepto
                        +", cenco: " + cencoId
                        +", rut: " + request.getParameter("rutEmpleado"));
                    
                    int intCencoId=-1;
                    if (cencoId.compareTo("") != 0) intCencoId = Integer.parseInt(cencoId); 
                    
                    int objectsCount = 0;
                    if (intCencoId != -1){
                        if (tipo == null){
                            listaObjetos = vacacionesBp.getInfoVacaciones(paramEmpresa, 
                                request.getParameter("rutEmpleado"),
                                intCencoId, 
                                startPageIndex, 
                                numRecordsPerPage, 
                                jtSorting);

                            //Get Total Record Count for Pagination
                            objectsCount = vacacionesBp.getInfoVacacionesCount(paramEmpresa, 
                                request.getParameter("rutEmpleado"),
                                Integer.parseInt(cencoId));
                        }else{
                            System.out.println(WEB_NAME+"[VacacionesController]"
                                + "Listar info vacaciones para empleados desvinculados. "
                                + "empresa: " + paramEmpresa
                                +", depto: " + paramDepto
                                +", cenco: " + cencoId
                                +", rut: " + request.getParameter("rutEmpleado"));
                            listaObjetos = vacacionesBp.getInfoVacacionesDesvincula2(paramEmpresa, 
                                request.getParameter("rutEmpleado"),
                                intCencoId, 
                                startPageIndex, 
                                numRecordsPerPage, 
                                jtSorting);

                            //Get Total Record Count for Pagination
                            objectsCount = vacacionesBp.getInfoVacacionesDesvincula2Count(paramEmpresa, 
                                request.getParameter("rutEmpleado"),
                                Integer.parseInt(cencoId));
                        }
                        session.setAttribute("infovacaciones|"+userConnected.getUsername(), listaObjetos);
                    }
                    //Convert Java Object to Json
                    JsonElement element = gson.toJsonTree(listaObjetos,
                        new TypeToken<List<VacacionesVO>>() {}.getType());

                    JsonArray jsonArray = element.getAsJsonArray();
                    String listData=jsonArray.toString();
                        
                    //Return Json in the format required by jTable plugin
                    listData="{\"Result\":\"OK\",\"Records\":" + 
                        listData+",\"TotalRecordCount\": " + 
                        objectsCount + "}";
                    response.getWriter().print(listData);
                    //request.getRequestDispatcher("/mantenedores/mantenedoresFrmSet.jsp").forward(request, response);
                }catch(Exception ex){
                    String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getMessage()+"}";
                    response.getWriter().print(error);
                    ex.printStackTrace();
                }   
            }else if (action.compareTo("create") == 0) {
                    System.out.println(WEB_NAME+"[VacacionesController]"
                        + "Insertar nuevo registro info vacaciones...");
                    
                    EmpleadoVO infoEmpleado = empleadosbp.getEmpleado(infoVacacion.getEmpresaId(), 
                            infoVacacion.getRutEmpleado());
                    resultado.setEmpresaId(infoEmpleado.getEmpresa().getId());
                    resultado.setDeptoId(infoEmpleado.getDepartamento().getId());
                    resultado.setCencoId(infoEmpleado.getCentroCosto().getId());
                    
                    MaintenanceVO doCreate = vacacionesBp.insert(infoVacacion, resultado);					
                    listaObjetos.add(infoVacacion);

                    //Convert Java Object to Json
                    String json=gson.toJson(infoVacacion);					
                    //Return Json in the format required by jTable plugin
                    String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";
                    if (doCreate.isThereError()) listData = "{\"Result\":\"ERROR\",\"Message\":"+doCreate.getMsgError()+"}";
                    response.getWriter().print(listData);
            }else if (action.compareTo("update") == 0) {  
                    String rowKey = infoVacacion.getRowKey();
                    StringTokenizer keytoken = new StringTokenizer(rowKey, "|");
                    infoVacacion.setEmpresaId(keytoken.nextToken());
                    infoVacacion.setRutEmpleado(keytoken.nextToken());
                    
                    EmpleadoVO infoEmpleado = empleadosbp.getEmpleado(infoVacacion.getEmpresaId(), 
                            infoVacacion.getRutEmpleado());
                    resultado.setEmpresaId(infoEmpleado.getEmpresa().getId());
                    resultado.setDeptoId(infoEmpleado.getDepartamento().getId());
                    resultado.setCencoId(infoEmpleado.getCentroCosto().getId());
                    
                    System.out.println(WEB_NAME+"[VacacionesController]"
                        + "Actualizar info vacaciones. "
                        + "empresa_id: " + infoVacacion.getEmpresaId()
                        + ", rutEmpleado: " + infoVacacion.getRutEmpleado());
                    try{
                        MaintenanceVO doUpdate = vacacionesBp.update(infoVacacion, resultado);
                        listaObjetos.add(infoVacacion);

                        //Convert Java Object to Json
                        String json=gson.toJson(infoVacacion);					
                        //Return Json in the format required by jTable plugin
                        String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";											
                        response.getWriter().print(listData);

                    }catch(Exception ex){
                        String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getStackTrace().toString()+"}";
                        response.getWriter().print(error);
                    }
            }else if (action.compareTo("delete") == 0) {  
                    String rowKey = infoVacacion.getRowKey();
                    StringTokenizer keytoken = new StringTokenizer(rowKey, "|");
                    infoVacacion.setEmpresaId(keytoken.nextToken());
                    infoVacacion.setRutEmpleado(keytoken.nextToken());
                    EmpleadoVO infoEmpleado = empleadosbp.getEmpleado(infoVacacion.getEmpresaId(), 
                            infoVacacion.getRutEmpleado());
                    resultado.setEmpresaId(infoEmpleado.getEmpresa().getId());
                    resultado.setDeptoId(infoEmpleado.getDepartamento().getId());
                    resultado.setCencoId(infoEmpleado.getCentroCosto().getId());
                    
                    System.out.println(WEB_NAME+"[VacacionesController]"
                        + "Eliminar info vacaciones. "
                        + "empresa_id: " + infoVacacion.getEmpresaId()
                        + ", rutEmpleado: " + infoVacacion.getRutEmpleado());
                    try{
                        MaintenanceVO doDelete = vacacionesBp.delete(infoVacacion, resultado);
                        
                        //Convert Java Object to Json
                        String json=gson.toJson(infoVacacion);					
                        //Return Json in the format required by jTable plugin
                        String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";											
                        response.getWriter().print(listData);

                    }catch(Exception ex){
                        String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getStackTrace().toString()+"}";
                        response.getWriter().print(error);
                    }
            }else if (action.compareTo("calcula_saldo") == 0) {
                    String paramEmpresa = request.getParameter("empresa_id");
                    String paramDeptoId = request.getParameter("depto_id");
                    String paramCencoId = request.getParameter("cenco_id");
                    String rutEmpleado = request.getParameter("rutEmpleado");
                    System.out.println(WEB_NAME+"[DetalleAusenciaController]"
                        + "Calcular saldo dias de vacaciones. "
                        + "empresa_id= " + paramEmpresa
                        + ", depto_id= " + paramDeptoId
                        + ", cenco_id= " + paramCencoId
                        + ", rut_empleado= " + rutEmpleado);
                    int intCencoId=-1;
                    if (paramCencoId.compareTo("") != 0)
                        intCencoId = Integer.parseInt(paramCencoId); 
                    
                    System.out.println(WEB_NAME+"[VacacionesController]"
                        + "Calcular saldo dias de vacaciones. "
                        + "empresa_id: " + paramEmpresa
                        + ", cencoId: " + paramCencoId     
                        + ", rutEmpleado: " + rutEmpleado);
                    if (rutEmpleado != null && rutEmpleado.compareTo("-1") != 0){
                        System.out.println(WEB_NAME+"[VacacionesController]"
                            + "Calcular saldo dias de vacaciones "
                            + "para un solo empleado.");
                        vacacionesBp.calculaDiasVacaciones(userConnected.getUsername(),
                            paramEmpresa, 
                            rutEmpleado, 
                            parametrosSistema);
                        
                        DetalleAusenciaBp detAusenciaBp = new DetalleAusenciaBp(appProperties);
                        
                        System.out.println(WEB_NAME+"[VacacionesController]"
                            + "Actualizar saldos de vacaciones "
                            + "en tabla detalle_ausencia "
                            + "(usar nueva funcion setsaldodiasvacacionesasignadas). "
                            + "Run: "+ rutEmpleado);
                        detAusenciaBp.actualizaSaldosVacaciones(rutEmpleado);
                        
                    }else{
                        System.out.println(WEB_NAME+"[VacacionesController]"
                            + "Calcular saldo dias de vacaciones "
                            + "para todos los empleados del centro de costo. "
                            + "empresa_id: " + paramEmpresa
                            + ", deptoId: " + paramDeptoId    
                            + ", cencoId: " + paramCencoId);
                        vacacionesBp.calculaDiasVacaciones(userConnected.getUsername(),
                            paramEmpresa, 
                            paramDeptoId, 
                            intCencoId, 
                            parametrosSistema, 
                            false);
                    }
                    response.sendRedirect(request.getContextPath()
                        +"/vacaciones/info_vacaciones.jsp");
            }else if (action.compareTo("calcula_vacaciones_desvincula2") == 0) {
                    String paramEmpresa = request.getParameter("empresa_id");
                    String paramDeptoId = request.getParameter("depto_id");
                    String paramCencoId = request.getParameter("cenco_id");
                    String rutEmpleado = request.getParameter("rutEmpleado");
                    System.out.println(WEB_NAME+"[DetalleAusenciaController]"
                        + "Calcular vacaciones para empleados desvinculados. "
                        + "empresa_id= " + paramEmpresa
                        + ", depto_id= " + paramDeptoId
                        + ", cenco_id= " + paramCencoId
                        + ", rut_empleado= " + rutEmpleado);
                    int intCencoId=-1;
                    if (paramCencoId.compareTo("") != 0)
                        intCencoId = Integer.parseInt(paramCencoId); 
                    
                    System.out.println(WEB_NAME+"[VacacionesController]"
                        + "Calcular vacaciones (desvinculado). "
                        + "empresa_id: " + paramEmpresa
                        + ", cencoId: " + paramCencoId     
                        + ", rutEmpleado: " + rutEmpleado);
                    if (rutEmpleado != null && rutEmpleado.compareTo("-1") != 0){
                        System.out.println(WEB_NAME+"[VacacionesController]"
                            + "Calcular vacaciones "
                            + "para un solo empleado desvinculado.");
                        vacacionesBp.calculaDiasVacaciones(userConnected.getUsername(),
                            paramEmpresa, 
                            rutEmpleado, 
                            parametrosSistema);
                        DetalleAusenciaBp detAusenciaBp = new DetalleAusenciaBp(appProperties);
                        
                        System.out.println(WEB_NAME+"[VacacionesController]"
                            + "Actualizar saldos de vacaciones "
                            + "en tabla detalle_ausencia "
                            + "(usar nueva funcion setsaldodiasvacacionesasignadas). "
                            + "Run: "+ rutEmpleado);
                        detAusenciaBp.actualizaSaldosVacaciones(rutEmpleado);
                        
                    }else{
                        System.out.println(WEB_NAME+"[VacacionesController]"
                            + "Calcular saldo dias de vacaciones "
                            + "para todos los empleados DESVINCULADOS del centro de costo. "
                            + "empresa_id: " + paramEmpresa
                            + ", deptoId: " + paramDeptoId    
                            + ", cencoId: " + paramCencoId);
                        vacacionesBp.calculaDiasVacaciones(userConnected.getUsername(),
                            paramEmpresa,   
                            paramDeptoId, 
                            intCencoId, 
                            parametrosSistema, 
                            true);
                    }
                    response.sendRedirect(request.getContextPath()
                        + "/vacaciones/calculo_vacaciones_desvincula2.jsp");
            }
            
      }
    }
    
}
