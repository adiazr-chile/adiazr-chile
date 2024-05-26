package cl.femase.gestionweb.servlet.mantencion;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.DetalleAusenciaBp;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.business.MaintenanceEventsBp;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.dao.TraspasoHistoricoDAO;
import cl.femase.gestionweb.vo.CentroCostoVO;
import cl.femase.gestionweb.vo.DepartamentoVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.EmpresaVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
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
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.StringTokenizer;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

public class EmpleadosController extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 990L;
    
    public EmpleadosController() {
        
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        if (session!=null) session.removeAttribute("mensaje");else session = request.getSession();
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
        System.out.println(WEB_NAME+"[EmpleadosController.doGet]"
            + "param action: "+request.getParameter("action"));
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
        System.out.println(WEB_NAME+"[EmpleadosController.doPost]"
            + "userConnected.nombres: " + userConnected.getUsername());
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
        request.setCharacterEncoding("UTF-8");
        EmpleadosBp empleadoBp = new EmpleadosBp(appProperties);
        MaintenanceEventsBp eventosBp   = new MaintenanceEventsBp(appProperties);
        //objeto usado para update/insert
        EmpleadoVO auxdata = new EmpleadoVO();
        MaintenanceEventVO resultado=new MaintenanceEventVO();
        
        //lo primero...guardar foto
        /**Obtengo los valores desde el formulario*/
        System.out.println(WEB_NAME+"[EmpleadosController.processRequest] "
            + "Empleados - Obteniendo datos desde "
            + "formulario y guardando foto...");
        auxdata = guardarFotoEmpleado(request, auxdata.getRut(),appProperties);
        System.out.println(WEB_NAME+"[EmpleadosController.processRequest]"
            + "full object: "+auxdata.toString());
        
        EmpleadoVO auxdataRequest = getFormValues(request);
        
        System.out.println(WEB_NAME+"[EmpleadosController.processRequest]"
            + "Full datos desde formulario(guarda foto): " + auxdata.toString());
        System.out.println(WEB_NAME+"[EmpleadosController.processRequest]"
            + "Full datos desde formulario(parseo request): " + auxdataRequest.toString());
        String action = auxdata.getAction();
        
        if (action == null) action = auxdataRequest.getAction();
        if (auxdata.getAction2() != null) {
            System.out.println(WEB_NAME+"[EmpleadosController.processRequest]"
                + "auxdata.getAction2() = "+auxdata.getAction2());
            action = "update";
        }
        
        System.out.println(WEB_NAME+"[EmpleadosController.processRequest]"
            + "param action is: "+action);
        if(action != null){
            List<EmpleadoVO> listaObjetos = new ArrayList<>();
            
            Gson gson = new Gson();
            response.setContentType("application/json");
            //Fetch Data from User Table
            int startPageIndex      = 0;
            int numRecordsPerPage   = 10;
            String jtSorting        = "empl_rut asc";
            /** filtros de busqueda */
           
            if (auxdataRequest.getJtStartIndex() != -1) 
                startPageIndex = auxdataRequest.getJtStartIndex();
            if (auxdataRequest.getJtPageSize() != -1) 
                numRecordsPerPage   = auxdataRequest.getJtPageSize();
            if (auxdataRequest.getJtSorting() != null) 
                jtSorting   = auxdataRequest.getJtSorting();
            
            if (jtSorting.compareTo("rut ASC")==0) jtSorting = "empl_rut ASC";
            else if (jtSorting.compareTo("rut DESC")==0) jtSorting = "empl_rut DESC";
            else if (jtSorting.contains("nombres")) jtSorting = jtSorting.replaceFirst("nombres","empl_nombres");
            else if (jtSorting.contains("apePaterno")) jtSorting = jtSorting.replaceFirst("apePaterno","empl_ape_paterno");
            else if (jtSorting.contains("apeMaterno")) jtSorting = jtSorting.replaceFirst("apeMaterno","empl_ape_materno");    
            else if (jtSorting.contains("fechaNacimientoAsStr")) jtSorting = jtSorting.replaceFirst("fechaNacimientoAsStr","empl_fecha_nacimiento");
            else if (jtSorting.contains("direccion")) jtSorting = jtSorting.replaceFirst("direccion","empl_direccion");
            else if (jtSorting.contains("email")) jtSorting = jtSorting.replaceFirst("email","empl_email");
            else if (jtSorting.contains("fechaInicioContratoAsStr")) jtSorting = jtSorting.replaceFirst("fechaInicioContratoAsStr","empl_fec_ini_contrato");
            else if (jtSorting.contains("estado")) jtSorting = jtSorting.replaceFirst("estado","empl_estado");
            else if (jtSorting.contains("pathFoto")) jtSorting = jtSorting.replaceFirst("pathFoto","empl_path_foto");
            else if (jtSorting.contains("sexo")) jtSorting = jtSorting.replaceFirst("sexo","empl_sexo");
            else if (jtSorting.contains("fonoFijo")) jtSorting = jtSorting.replaceFirst("fonoFijo","empl_fono_fijo");
            else if (jtSorting.contains("fonoMovil")) jtSorting = jtSorting.replaceFirst("fonoMovil","empl_fono_movil");
            else if (jtSorting.contains("comunaId")) jtSorting = jtSorting.replaceFirst("comunaId","empl.id_comuna");
            else if (jtSorting.contains("empresaNombre")) jtSorting = jtSorting.replaceFirst("empresaNombre","empresa_nombre");
            else if (jtSorting.contains("deptoNombre")) jtSorting = jtSorting.replaceFirst("deptoNombre","depto_nombre");
            else if (jtSorting.contains("cencoNombre")) jtSorting = jtSorting.replaceFirst("cencoNombre","ccosto_nombre");
            else if (jtSorting.contains("idTurno")) jtSorting = jtSorting.replaceFirst("idTurno","empl_id_turno");
            else if (jtSorting.contains("idCargo")) jtSorting = jtSorting.replaceFirst("idCargo","empl_id_cargo");
            else if (jtSorting.contains("autorizaAusencia")) jtSorting = jtSorting.replaceFirst("autorizaAusencia","autoriza_ausencia");
            else if (jtSorting.contains("fechaTerminoContratoAsStr")) jtSorting = jtSorting.replaceFirst("fechaTerminoContratoAsStr","empl_fec_fin_contrato");
            else if (jtSorting.contains("articulo22")) jtSorting = jtSorting.replaceFirst("articulo22","art_22");
            else if (jtSorting.contains("contratoIndefinido")) jtSorting = jtSorting.replaceFirst("contratoIndefinido","contrato_indefinido");
            else if (jtSorting.contains("nombreCargo")) jtSorting = jtSorting.replaceFirst("nombreCargo","cargo.cargo_nombre");
                        
            if (request.getParameter("empresaId") != null && 
                    request.getParameter("empresaId").compareTo("-1") != 0){ 
                session.setAttribute("empresaId", request.getParameter("empresaId"));
            }
            
            System.out.println(WEB_NAME+"[EmpleadosController.processRequest]"
                + "jtSorting= " + jtSorting
                +", startPageIndex: " + startPageIndex
                +", numRecordsPerPage: " + numRecordsPerPage);
            
            if (action.compareTo("list")==0) {
                System.out.println(WEB_NAME+"[EmpleadosController.processRequest]"
                    + "Mostrando empleados...");
                
                try{
                    String empresaid="-1";
                    String deptoid="-1";
                    int cencoid=-1;
                    
                    String paramCencoID         = request.getParameter("cencoId");
                    System.out.println(WEB_NAME+"[EmpleadosController.processRequest]"
                        + "token param 'cencoID'= " + paramCencoID);
                    if (paramCencoID != null && paramCencoID.compareTo("-1") != 0){
                        StringTokenizer tokenCenco  = new StringTokenizer(paramCencoID, "|");
                        if (tokenCenco.countTokens() > 0){
                            while (tokenCenco.hasMoreTokens()){
                                empresaid   = tokenCenco.nextToken();
                                deptoid     = tokenCenco.nextToken();
                                cencoid     = Integer.parseInt(tokenCenco.nextToken());
                            }
                        }
                    }
                    
                    System.out.println(WEB_NAME+"[EmpleadosController.processRequest]Mostrar empleados. "
                        + "Empresa: " + empresaid
                        + ", depto: " + deptoid
                        + ", cenco: " + cencoid
                        + ", idTurno: " + auxdataRequest.getIdTurno()
                        + ", rut: " + auxdataRequest.getRut()
                        + ", nombres: " + auxdataRequest.getNombres()
                        + ", ap.paterno: " + auxdataRequest.getApePaterno()
                        + ", ap.materno: " + auxdataRequest.getApeMaterno()
                        + ", estado: " + auxdataRequest.getEstado());
                    
                    /**
                    * Busqueda por Rut: 
                    *   Al buscar por rut, se deben quitar los puntos y guion, 
                    *   luego buscar en la modalidad 'empiece con' (like 'sssss%')
                    * 
                    */
                    if (auxdataRequest.getRut() != null){
                        String auxRun = auxdataRequest.getRut();
                        String newRun =  auxRun.replace(".", "");
                        auxdataRequest.setRut(newRun);
                    }
                    if (auxdataRequest.getEmpresa()!=null) empresaid=auxdataRequest.getEmpresa().getId();
                    if (auxdataRequest.getDepartamento()!=null) deptoid=auxdataRequest.getDepartamento().getId();
                    if (auxdataRequest.getCentroCosto()!=null) cencoid=auxdataRequest.getCentroCosto().getId();
                    
                    int empleadosCount=0;
                    if (cencoid != -1){
                            listaObjetos = empleadoBp.getEmpleados(empresaid, 
                                deptoid, 
                                cencoid,
                                -1,
                                auxdataRequest.getIdTurno(),
                                auxdataRequest.getRut(), 
                                auxdataRequest.getNombres(), 
                                auxdataRequest.getApePaterno(), 
                                auxdataRequest.getApeMaterno(),
                                auxdataRequest.getEstado(),
                                startPageIndex, 
                                numRecordsPerPage, 
                                jtSorting);
                    
                        //Get Total Record Count for Pagination
                        empleadosCount = empleadoBp.getEmpleadosCount(empresaid, 
                            deptoid, 
                                cencoid, 
                                -1,
                                auxdataRequest.getIdTurno(),
                                auxdataRequest.getRut(), 
                                auxdataRequest.getNombres(), 
                                auxdataRequest.getApePaterno(), 
                                auxdataRequest.getApeMaterno(),
                                auxdataRequest.getEstado());
                        System.out.println(WEB_NAME+"[EmpleadosController.processRequest] num empleados: "+empleadosCount);
                        
                        //agregar evento al log.
                        
                        resultado.setUsername(userConnected.getUsername());
                        resultado.setDatetime(new Date());
                        resultado.setUserIP(request.getRemoteAddr());
                        resultado.setType("AEM");
                        resultado.setEmpresaIdSource(userConnected.getEmpresaId());
                        resultado.setEmpresaId(empresaid);
                        resultado.setDeptoId(deptoid);
                        resultado.setCencoId(cencoid);
                        resultado.setDescription("Consulta empleados.");
                        eventosBp.addEvent(resultado);
                    }
                    
                    //Convert Java Object to Json
                    JsonElement element = gson.toJsonTree(listaObjetos,
                            new TypeToken<List<EmpleadoVO>>() {}.getType());

                    JsonArray jsonArray = element.getAsJsonArray();
                    String listData=jsonArray.toString();

                    //Return Json in the format required by jTable plugin
                    listData="{\"Result\":\"OK\",\"Records\":" + 
                            listData+",\"TotalRecordCount\": " + 
                            empleadosCount + "}";
                    
                    session.setAttribute("empleados|"+userConnected.getUsername(), listaObjetos);
                    
                    System.out.println(WEB_NAME+"[EmpleadosController.processRequest]FIN LISTAR EMPLEADOS - Mantenedor - Empleados\n\n");
                    response.getWriter().print(listData);
                    
                    //request.getRequestDispatcher("/mantenedores/mantenedoresFrmSet.jsp").forward(request, response);
                }catch(Exception ex){
                    String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getMessage()+"}";
                    response.getWriter().print(error);
                    ex.printStackTrace();
                }   
            }else if (action.compareTo("create") == 0) {
                    boolean rutValido = Utilidades.validarRut(auxdata.getRut());
                    System.out.println(WEB_NAME+"[EmpleadosController.processRequest]"
                        + "Insertar empleado, "
                        + "Rut= " + auxdata.getRut()
                        +", valido? "+rutValido);
                    String strMsg = "Empleado creado correctamente...";
                    ResultCRUDVO doCreate=null;
                    if (rutValido){
                        auxdata.setPathFoto(auxdata.getPathFoto());
                        //insertar empleado
                        System.out.println(WEB_NAME+"[EmpleadosController.processRequest]"
                            + "Insertar empleado. "
                            + "Rut valido, Insertar en base de datos. "
                            + "Empresa: " + auxdata.getEmpresa().getId()
                            + ", deptoId: " + auxdata.getDepartamento().getId()
                            + ", cencoId: " + auxdata.getCentroCosto().getId());
                        resultado.setUsername(userConnected.getUsername());
                        resultado.setDatetime(new Date());
                        resultado.setUserIP(request.getRemoteAddr());
                        resultado.setType("EMP");
                        resultado.setEmpresaId(auxdata.getEmpresa().getId());
                        resultado.setEmpresaIdSource(userConnected.getEmpresaId());
                        resultado.setRutEmpleado(auxdata.getCodInterno());
                        resultado.setDeptoId(auxdata.getDepartamento().getId());
                        resultado.setCencoId(auxdata.getCentroCosto().getId());
                        
                        doCreate = empleadoBp.insert(auxdata, resultado);					
                        if (doCreate.isThereError()){
                            strMsg = doCreate.getMsgError();
                        }
                        listaObjetos.add(auxdata);
                    }else{
                        System.out.println(WEB_NAME+"[EmpleadosController.processRequest]"
                            + "Insertar empleado, "
                            + "Rut no valido. NO INSERTAR");
                        strMsg = "Rut No valido...";
                    }
                    //actualizar data en sesion
                     /** a ser usados en jsp detalle_ausencias*/
                    DetalleAusenciaBp autorizaAusenciaBp = 
                        new DetalleAusenciaBp(appProperties);    
                    session.setAttribute("autorizadores", 
                        autorizaAusenciaBp.getAutorizadoresDisponibles(userConnected));
                    
                    EmpleadosBp empleadosBp = 
                        new EmpleadosBp(appProperties);    
                    session.setAttribute("empleados", 
                        empleadosBp.getEmpleados(null,null,-1,-1,null,null,null,null,0,0,"empl_rut"));
                    
                    System.out.println(WEB_NAME+"[EmpleadosController.processRequest]FIN CREAR EMPLEADO - Mensaje a mostrar: " + strMsg);
                    
                    if (strMsg.indexOf("duplicate key") > 0 || strMsg.indexOf("llave duplicada") > 0){
                        /**
                        * Mostrar mensaje: Trabajador rut XXXX 
                        * ya existe en el centro de costo ZZZ
                        */
                        EmpleadoVO prevEmpleado = 
                            empleadosBp.getEmpleado(null, 
                                auxdata.getCodInterno());
                        if (prevEmpleado != null){
                            strMsg = "Trabajador rut "+auxdata.getRut()
                                + ", numFicha: " + auxdata.getCodInterno()
                                +" ya existe en el centro de costo "
                                + ": " + prevEmpleado.getEmpresaNombre() + " - "
                                + prevEmpleado.getCencoNombre();
                        }
                    }
                    
                    RequestDispatcher view = 
                        request.getRequestDispatcher("/DestineServlet?mensaje="+strMsg);
                    view.forward(request, response);
            }else if (action.compareTo("update") == 0) {
                    EmpleadoVO currentEmpleado = 
                        empleadoBp.getEmpleado(auxdata.getEmpresa().getId(), auxdata.getCodInterno());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String newFechaInicioContrato = sdf.format(auxdata.getFechaInicioContrato());
                    String oldFechaInicioContrato = sdf.format(currentEmpleado.getFechaInicioContrato());
                    System.out.println(WEB_NAME+"[GestionFemase.EmpleadosController]"
                        + "Modificar datos del Empleado. "
                        + "RUN (PK): " + auxdata.getRut()
                        + ", empresaId: " + currentEmpleado.getEmpresaId()   
                        + ", fechaInicioContratoNEW: " + newFechaInicioContrato
                        + ", fechaInicioContratoOLD: " + oldFechaInicioContrato);
                                        
                    System.out.println(WEB_NAME+"[GestionFemase.EmpleadosController]"
                        + "Actualizar Empleado. "
                        + "Num ficha: " + auxdata.getRut()
                        + ", rut: " + auxdata.getCodInterno()
                        + ", empresaId: " + auxdata.getEmpresa().getId()
                        + ", deptoId: " + auxdata.getDepartamento().getId()
                        + ", cencoId: " + auxdata.getCentroCosto().getId()    
                        + ", contratoIndefinido?: " + auxdata.isContratoIndefinido()
                        + ", fechaFinContratoStr: " + auxdata.getFechaTerminoContratoAsStr()
                        + ", fechaFinContratoDate: " + auxdata.getFechaTerminoContrato()
                        + ", fechaInicioContratoStr (new): " + auxdata.getFechaInicioContratoAsStr()
                        + ", fechaInicioContratoDate (new): " + auxdata.getFechaInicioContrato()
                        + ", continuidadLaboral: " + auxdata.getContinuidadLaboral()
                        + ", nueva fecha inicio contrato: " + auxdata.getNuevaFechaIniContratoAsStr()    
                    );
                    String strMsg = "Empleado modificado correctamente.";
                    try{
                        resultado.setUsername(userConnected.getUsername());
                        resultado.setDatetime(new Date());
                        resultado.setUserIP(request.getRemoteAddr());
                        resultado.setType("EMP");
                        resultado.setEmpresaId(auxdata.getEmpresa().getId());
                        resultado.setEmpresaIdSource(userConnected.getEmpresaId());
                        resultado.setRutEmpleado(auxdata.getCodInterno());
                        resultado.setDeptoId(auxdata.getDepartamento().getId());
                        resultado.setCencoId(auxdata.getCentroCosto().getId());
                        
                        ResultCRUDVO doUpdate = empleadoBp.update(auxdata, resultado);
                        request.setAttribute("action","ok");
                        if (doUpdate.isThereError()){
                            strMsg = doUpdate.getMsgError();
                        }else{
                            Date dteNewFechaInicioContrato = auxdata.getFechaInicioContrato();
                            Date dteOldFechaInicioContrato = currentEmpleado.getFechaInicioContrato();
                            
                            System.out.println(WEB_NAME+"[GestionFemase.EmpleadosController]"
                                + "Se ha modificado la fecha de inicio de contrato. "
                                + "Fecha inicio contrato actual: " + dteOldFechaInicioContrato
                                + ", fecha inicio contrato nueva: " + dteNewFechaInicioContrato);
                            
                            if (dteNewFechaInicioContrato.after(dteOldFechaInicioContrato)){
                                System.out.println(WEB_NAME+"[GestionFemase.EmpleadosController]"
                                    + "Toda la historia del empleado "
                                    + "(desde la la nueva fecha de contrato hacia atrás)"
                                    + " sera movida a tablas historicas "
                                    + "(marcas, ausencias, marcas rechazadas "
                                    + "y registros de asistencia).");
                                HashMap<String, ResultCRUDVO> hashHist = 
                                    traspasoHistorico(currentEmpleado.getEmpresaId(), 
                                        newFechaInicioContrato, auxdata.getCodInterno());
                                strMsg += " Registros movidos a tablas historicas.";
                            }else{
                                System.out.println(WEB_NAME+"[GestionFemase.EmpleadosController]"
                                    + "No se ha modificado la fecha de inicio de contrato. "
                                    + "No hay traspaso a tablas historicas.");
                            }
                        }
                        
                        session.setAttribute("empresaSelected", auxdata.getEmpresa().getId());
                        session.setAttribute("deptoSelected", auxdata.getDepartamento().getId());
                        session.setAttribute("cencoSelected", ""+auxdata.getCentroCosto().getId());
                        System.out.println(WEB_NAME+"[EmpleadosController.processRequest]FIN Modificar EMPLEADO - Mensaje a mostrar: " + strMsg);
                    
                        RequestDispatcher view = 
                            request.getRequestDispatcher("/DestineServlet?mensaje="+strMsg);
                        view.forward(request, response);
                        
                    }catch(Exception ex){
                        String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getStackTrace().toString()+"}";
                        System.err.println("[EmpleadosController.processRequest]Error al actualizar empleado: "+ex.getMessage());
                        ex.printStackTrace();
                        response.getWriter().print(error);
                    }
            }else if (action.compareTo("edit") == 0) {  
                    System.out.println(WEB_NAME+"[EmpleadosController.processRequest]"
                        + "Editar Data Empleado rut: "+auxdataRequest.getRut());
                    listaObjetos = 
                        empleadoBp.getEmpleados("-1", 
                            "-1", 
                            -1,
                            -1, 
                            auxdataRequest.getRut(), 
                            null, 
                            null, 
                            null, 
                            0, 
                            10, 
                            "empl_rut ASC");
                    System.out.println(WEB_NAME+"[EmpleadosController.processRequest]"
                        + "Editar Data Empleado. Path images: "+appProperties.getImagesPath());
                    EmpleadoVO toedit = listaObjetos.get(0);
                    request.removeAttribute("empleado");
                    session.setAttribute("empleado", toedit);
                    request.setAttribute("images_path", appProperties.getImagesPath());
                    request.setAttribute("action", "update");
                    //request.getRequestDispatcher("/mantencion/modificar_empleado.jsp").forward(request, response);        
                    request.getRequestDispatcher("/jqueryform-empleado/form_modificar_empleado.jsp").forward(request, response);        
            }
//            else if (action.compareTo("delete") == 0) {  
//                    //Delete record
//                    System.out.println(WEB_NAME+"Eliminando accion- "
//                            + "Id: " + auxdata.getId()
//                            +", label: "+ auxdata.getLabel());
//                    try{
//                        auxnegocio.delete(auxdata, resultado);
//                        String listData="{\"Result\":\"OK\"}";								
//                        response.getWriter().print(listData);
//                        
//                    }catch(Exception ex){
//                        String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getStackTrace().toString()+"}";
//                        response.getWriter().print(error);
//                    }
//            }
      }
    }
    
    /**
    * 
    */
    private HashMap<String, ResultCRUDVO> traspasoHistorico(String _empresaId, 
            String _fecha, 
            String _runEmpleado){
        TraspasoHistoricoDAO historicosDao = new TraspasoHistoricoDAO();
        
        System.out.println(WEB_NAME+"[EmpleadosController.traspasoHistorico]"
            + "EmpresaId: " + _empresaId 
            + ", fecha tope para realizar traspaso a historico: " + _fecha
            + ", run empleado: " + _runEmpleado);
        
        _runEmpleado = "'" + _runEmpleado + "'";
        
        HashMap<String, ResultCRUDVO> hashFilasAfectadas = new HashMap<>();
        
        ResultCRUDVO resultado = historicosDao.insertMarcasHistoricas(_empresaId, _fecha, _runEmpleado);
        int affectedRows = resultado.getFilasAfectadas();
        System.out.println(WEB_NAME+"[EmpleadosController.traspasoHistorico]"
            + "marcas traspasadas a historico: " + affectedRows);
        if (affectedRows > 0) {
            System.out.println(WEB_NAME+"[EmpleadosController.traspasoHistorico]"
                + "Eliminando marcas recien traspasadas a historico");
            historicosDao.deleteMarcas(_empresaId, _fecha, _runEmpleado);
            hashFilasAfectadas.put("Marcas", resultado);
        }
        
        resultado = historicosDao.insertMarcasRechazosHistoricas(_empresaId, _fecha, _runEmpleado);
        affectedRows = resultado.getFilasAfectadas();
        System.out.println(WEB_NAME+"[EmpleadosController.traspasoHistorico]"
            + "marcas rechazadas traspasadas a historico: " + affectedRows);
        if (affectedRows > 0) {
            System.out.println(WEB_NAME+"[EmpleadosController.traspasoHistorico]"
                + "Eliminando marcas rechazadas recien traspasadas a historico");
            historicosDao.deleteMarcasRechazadas(_empresaId, _fecha, _runEmpleado);
            hashFilasAfectadas.put("Marcas Rechazadas", resultado);
        }
        
        resultado = historicosDao.insertAusenciasHistoricas(_empresaId, _fecha, _runEmpleado);
        affectedRows = resultado.getFilasAfectadas();
        System.out.println(WEB_NAME+"[EmpleadosController.traspasoHistorico]"
            + "ausencias traspasadas a historico: " + affectedRows);
        if (affectedRows > 0) {
            System.out.println(WEB_NAME+"[EmpleadosController.traspasoHistorico]"
                + "Eliminando ausencias (detalle_ausencia) recien traspasadas a historico");
            historicosDao.deleteAusencias(_empresaId, _fecha, _runEmpleado);
            hashFilasAfectadas.put("Ausencias", resultado);
        }
        
        resultado = historicosDao.insertDetalleAsistenciaHistoricos(_empresaId, _fecha, _runEmpleado);
        affectedRows = resultado.getFilasAfectadas();
        System.out.println(WEB_NAME+"[EmpleadosController.traspasoHistorico]"
            + "calculos de asistencia (detalle_asistencia) traspasados a historico: " + affectedRows);
        if (affectedRows > 0) {
            System.out.println(WEB_NAME+"[EmpleadosController.traspasoHistorico]"
                + "Eliminando calculos de asistencia recien traspasados a historico");
            historicosDao.deleteDetalleAsistencia(_empresaId, _fecha, _runEmpleado);
            hashFilasAfectadas.put("Calculos de Asistencia", resultado);
        }
        
        return hashFilasAfectadas;
    }
    
    /**
    * 
    */
    private EmpleadoVO getFormValues(HttpServletRequest _request){
        EmpleadoVO auxdata=new EmpleadoVO();
        
        System.out.println(WEB_NAME+"[EmpleadosController.getFormValues]"
            + "Obteniendo datos desde formulario "
            + "(getFormValues)...");
        /*-------------*/
        try{
            
            boolean isMultipart = ServletFileUpload.isMultipartContent(_request);
//            ServletFileUpload upload = new ServletFileUpload();
//            String paramValue="";
//            byte[] str;
            SimpleDateFormat fechaFormat = new SimpleDateFormat("dd-MM-yyyy");
            System.out.println(WEB_NAME+"[EmpleadosController.getFormValues]isMultipart?" + isMultipart);
            System.out.println(WEB_NAME+"Mantenedor - Empleados - "
                + "getFormValues. "
                + "Param action= "+_request.getParameter("action"));
            //if(!isMultipart){
            System.out.println(WEB_NAME+"[EmpleadosController.getFormValues]"
                + "rescatar parametros desde formulario comun (usar getParameter)...");
            System.out.println(WEB_NAME+"[EmpleadosController.getFormValues]"
                + "param action= " + _request.getParameter("action")
                + ", param rutEmpleado= " + _request.getParameter("rutEmpleado")
                + ", param nombres= " + _request.getParameter("nombres")
                + ", param apePaterno= " + _request.getParameter("apePaterno")
                + ", param estado= " + _request.getParameter("estado")
                + ", param empresaId= " + _request.getParameter("empresaId")
                + ", contratoIndefinido= " + _request.getParameter("empresaId")
                + ", idTurno= " + _request.getParameter("turno"));
            //rescatar parametros desde formulario comun
            if (_request.getParameter("action") != null && _request.getParameter("action").compareTo("") != 0){
                auxdata.setAction(_request.getParameter("action"));
            }
            if (_request.getParameter("rutEmpleado") != null && _request.getParameter("rutEmpleado").compareTo("") != 0){
                auxdata.setRut(_request.getParameter("rutEmpleado"));
            }
            if (_request.getParameter("nombres") != null && _request.getParameter("nombres").compareTo("") != 0){
                auxdata.setNombres(_request.getParameter("nombres"));
            }
            if (_request.getParameter("turno") != null && _request.getParameter("turno").compareTo("-1") != 0){
                System.out.println(WEB_NAME+"[EmpleadosController."
                    + "getFormValues] -1- seteo de turno: "+ _request.getParameter("turno"));
                auxdata.setIdTurno(Integer.parseInt(_request.getParameter("turno")));
            }
            if (_request.getParameter("apePaterno") != null && _request.getParameter("apePaterno").compareTo("") != 0){
                auxdata.setApePaterno(_request.getParameter("apePaterno"));
            } 
            if (_request.getParameter("apeMaterno") != null && _request.getParameter("apeMaterno").compareTo("") != 0){
                auxdata.setApeMaterno(_request.getParameter("apeMaterno"));
            } 
            if (_request.getParameter("estado") != null && _request.getParameter("estado").compareTo("") != 0){
                auxdata.setEstado(Integer.parseInt(_request.getParameter("estado")));
            }
                
            //auxdata.setPathFoto(getValueFromMultipart(stream));
            if (_request.getParameter("empresaId") != null && _request.getParameter("empresaId").compareTo("-1") != 0){
                EmpresaVO auxempresa=new EmpresaVO();
                auxempresa.setId(_request.getParameter("empresaId"));
                auxdata.setEmpresa(auxempresa);
            } 

            if (_request.getParameter("deptoId") != null && _request.getParameter("deptoId").compareTo("-1") != 0){
                DepartamentoVO auxdepto=new DepartamentoVO();
                auxdepto.setId(_request.getParameter("deptoId"));
                auxdata.setDepartamento(auxdepto);
            } 

            if (_request.getParameter("cencoId") != null && _request.getParameter("cencoId").compareTo("-1") != 0){
                CentroCostoVO auxcenco=new CentroCostoVO();
                try{
                    auxcenco.setId(Integer.parseInt(_request.getParameter("cencoId")));
                }catch(NumberFormatException nfex){
                    System.out.println(WEB_NAME+"[EmpleadosController."
                        + "getFormValues]set empresa,depto y cenco, "
                        + "usando combo unico CENCO");
                    StringTokenizer tokencenco=new StringTokenizer(_request.getParameter("cencoId"), "|");
                    EmpresaVO auxempresa = new EmpresaVO();
                    auxempresa.setId(tokencenco.nextToken());
                    auxdata.setEmpresa(auxempresa);
                    
                    DepartamentoVO auxdepto=new DepartamentoVO();
                    auxdepto.setId(tokencenco.nextToken());
                    auxdata.setDepartamento(auxdepto);
                    
                    auxcenco.setId(Integer.parseInt(tokencenco.nextToken()));
                    auxdata.setCentroCosto(auxcenco);
                }
            } 
                
            if (_request.getParameter("idTurno") != null 
                    && _request.getParameter("idTurno").compareTo("-1") != 0){
                System.out.println(WEB_NAME+"[EmpleadosController."
                    + "getFormValues] -2- seteo de turno: "+ _request.getParameter("idTurno"));
                auxdata.setIdTurno(Integer.parseInt(_request.getParameter("idTurno")));
            }
            
            if (_request.getParameter("idCargo") != null 
                    && _request.getParameter("idCargo").compareTo("-1") != 0){
                auxdata.setIdCargo(Integer.parseInt(_request.getParameter("idCargo")));
            }
                
            if (_request.getParameter("autorizaAusencia") != null ){
                boolean autoriza=false;
                if (_request.getParameter("autorizaAusencia").compareTo("S")==0){
                    autoriza = true;
                }
                auxdata.setAutorizaAusencia(autoriza);
            }
                
            if (_request.getParameter("cambiarFoto") != null ){
                boolean cambiarFoto=false;
                if (_request.getParameter("cambiarFoto").compareTo("S")==0){
                    cambiarFoto = true;
                }
                auxdata.setCambiarFoto(cambiarFoto);
            }

            //24-04-2017
            if (_request.getParameter("fechaTerminoContratoAsStr") != null && _request.getParameter("fechaTerminoContratoAsStr").compareTo("") != 0){
                auxdata.setFechaTerminoContratoAsStr(_request.getParameter("fechaTerminoContratoAsStr"));
            }
            
            if (_request.getParameter("fechaDesvinculacionAsStr") != null && _request.getParameter("fechaDesvinculacionAsStr").compareTo("") != 0){
                auxdata.setFechaDesvinculacionAsStr(_request.getParameter("fechaDesvinculacionAsStr"));
            }

            if (_request.getParameter("articulo22") != null ){
                boolean articulo22=false;
                if (_request.getParameter("articulo22").compareTo("S")==0){
                    articulo22 = true;
                }
                auxdata.setArticulo22(articulo22);
            }
                
            if (_request.getParameter("cod_interno") != null 
                    && _request.getParameter("cod_interno").compareTo("") != 0){
                auxdata.setCodInterno(_request.getParameter("cod_interno"));
            }

            if (_request.getParameter("cod_interno_adicional") != null 
                    && _request.getParameter("cod_interno_adicional").compareTo("") != 0){
                auxdata.setCodInternoCaracterAdicional(_request.getParameter("cod_interno_adicional"));
            }
            
            if (_request.getParameter("claveMarcacion") != null 
                    && _request.getParameter("claveMarcacion").compareTo("") != 0){
                 auxdata.setClaveMarcacion(_request.getParameter("claveMarcacion"));
            }

            if (_request.getParameter("contratoIndefinido") != null ){
                boolean contratoIndefinido=true;
                if (_request.getParameter("contratoIndefinido").compareTo("N")==0){
                    contratoIndefinido = false;
                }
                auxdata.setContratoIndefinido(contratoIndefinido);
            }
            
            
            if (_request.getParameter("continuidadLaboral") != null){
                auxdata.setContinuidadLaboral(_request.getParameter("continuidadLaboral"));
                if (_request.getParameter("continuidadLaboral").compareTo("N") == 0){
                    auxdata.setNuevaFechaIniContrato(null);
                    auxdata.setNuevaFechaIniContratoAsStr("");
                }else{
                    if (_request.getParameter("nuevaFechaIniContratoAsStr") != null && _request.getParameter("nuevaFechaIniContratoAsStr").compareTo("") != 0){
                        auxdata.setNuevaFechaIniContratoAsStr(_request.getParameter("nuevaFechaIniContratoAsStr"));
                    }
                }
            }
            
            System.out.println(WEB_NAME+"[EmpleadosController.getFormValues]"
                + "continuidad laboral (S/N)?: " + auxdata.getContinuidadLaboral()
                + ",nueva fecha inicio contrato: " + auxdata.getNuevaFechaIniContratoAsStr()    
            );

            if (_request.getParameter("modificar") != null ){
                boolean modificarEmpresaDeptoCenco=false;
                if (_request.getParameter("modificar").compareTo("Si")==0){
                    modificarEmpresaDeptoCenco = true;
                }
                auxdata.setModificarEmpresaDeptoCenco(modificarEmpresaDeptoCenco);
            }
                
            System.out.println(WEB_NAME+"[EmpleadosController.getFormValues]"
                + "parameter jtStartIndex= " + _request.getParameter("jtStartIndex")
                + ",parameter jtPageSize= " + _request.getParameter("jtPageSize")
                + ",parameter jtSorting= " + _request.getParameter("jtSorting"));

            if (_request.getParameter("jtStartIndex") != null){ 
                auxdata.setJtStartIndex(Integer.parseInt(_request.getParameter("jtStartIndex")));
            }
            if (_request.getParameter("jtPageSize") != null) 
                 auxdata.setJtPageSize(Integer.parseInt(_request.getParameter("jtPageSize")));
            if (_request.getParameter("jtSorting") != null) 
                auxdata.setJtSorting(_request.getParameter("jtSorting"));
                
        }catch(NumberFormatException fue){
            System.err.println("[EmpleadosController.getFormValues]Error 1: "+fue.toString());
        }
        
        return auxdata;
    }
    
////    private String getValueFromMultipart(InputStream _stream){
////        String paramValue="";
////        try{
////            byte[] str = new byte[_stream.available()];
////            _stream.read(str);
////            paramValue = new String(str,"UTF8");
////            System.out.println(WEB_NAME+"[EmpleadosController."
////                + "getValueFromMultipart]paramName= " + str 
////                + ", paramValue= "+paramValue);
////        }catch(IOException ioex){
////            System.err.println("[EmpleadosController."
////                + "getValueFromMultipart]Error al leer parametro: "+ioex.toString());
////        }
////        return paramValue;
////    }
    
    
    /**
    * 
    * Permite subir un archivo al servidor
    */
    private EmpleadoVO guardarFotoEmpleado(HttpServletRequest _request, 
            String _rutEmpleado, PropertiesVO _appProperties){
        boolean isMultipart = ServletFileUpload.isMultipartContent(_request);
//        String name = "";
        EmpleadoVO auxdata=new EmpleadoVO();
        SimpleDateFormat fechaFormat = new SimpleDateFormat("dd-MM-yyyy");
        
        if(isMultipart){
            try{
                String pathUploadedFiles = _appProperties.getImagesPath();
                List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(_request);
                for (FileItem item : items) {
                    String fieldName = item.getFieldName();
                    if (item.isFormField()) {
                        String fieldValue = item.getString("UTF-8"); // <-- HERE
                        if (fieldValue != null) fieldValue = fieldValue.trim();
                        System.out.println(WEB_NAME+"[EmpleadosController.guardarFotoEmpleado]"
                            + "fieldName: " + fieldName
                            +", fieldValue: " + fieldValue);
                        switch (item.getFieldName()) {
                            case "action":
                                auxdata.setAction(fieldValue);
                                break;
                            case "action2":
                                auxdata.setAction2(fieldValue);
                                break;
                            case "rut":
                                auxdata.setRut(fieldValue);
                                break;
                            case "nombres":
                                auxdata.setNombres(fieldValue);
                                break;
                            case "apePaterno":
                                auxdata.setApePaterno(fieldValue);
                                break;
                            case "apeMaterno":
                                auxdata.setApeMaterno(fieldValue);
                                break;
                            case "fechaNacimientoAsStr":
                                {
                                    Date auxDate = null;
                                    try{
                                        auxDate = fechaFormat.parse(fieldValue);
                                    }catch(ParseException pex){
                                        System.err.println("[EmpleadosController.guardarFotoEmpleado]"
                                                + "error al parsear fecha nacimiento: "+pex.toString());
                                    }   auxdata.setFechaNacimiento(auxDate);
                                    break;
                                }
                            case "direccion":
                                auxdata.setDireccion(fieldValue);
                                break;
                            case "email":
                                auxdata.setEmail(fieldValue);
                                break;
                            case "email_personal":
                                auxdata.setEmailPersonal(fieldValue);
                                break;    
                            case "fechaInicioContratoAsStr":
                                {
                                    Date auxDate = null;
                                    try{
                                        auxDate = fechaFormat.parse(fieldValue);
                                    }catch(ParseException pex){
                                        System.err.println("[EmpleadosController.guardarFotoEmpleado]"
                                                + "error al parsear fecha inicio contrato: "+pex.toString());
                                    }   auxdata.setFechaInicioContrato(auxDate);
                                    break;
                                }
                            case "estado":
                                try{
                                    auxdata.setEstado(Integer.parseInt(fieldValue));
                                }catch(NumberFormatException nfe){}
                                break;
                            case "foto":
                                auxdata.setPathFoto(fieldValue);
                                break;
                            case "sexo":
                                auxdata.setSexo(fieldValue);
                                break;
                            case "fono_fijo":
                                auxdata.setFonoFijo(fieldValue);
                                break;
                            case "fono_movil":
                                auxdata.setFonoMovil(fieldValue);
                                break;
                            case "comunaId":
                                try{
                                    auxdata.setComunaId(Integer.parseInt(fieldValue));
                                }catch(NumberFormatException nfe){}
                                break;
                            case "empresaId":
                                EmpresaVO auxempresa=new EmpresaVO();
                                auxempresa.setId(fieldValue);
                                auxdata.setEmpresa(auxempresa);
                                break;   
                            case "deptoId":
                                DepartamentoVO auxdepto=new DepartamentoVO();
                                auxdepto.setId(fieldValue);
                                auxdata.setDepartamento(auxdepto);
                                break;    
                            case "cencoId":
                                CentroCostoVO auxcenco=new CentroCostoVO();
                                try{
                                    auxcenco.setId(Integer.parseInt(fieldValue));
                                    auxdata.setCentroCosto(auxcenco);
                                }catch(NumberFormatException nfe){}
                                break;
                            case "idTurno":
                                auxdata.setIdTurno(Integer.parseInt(fieldValue));
                                break;
                            case "idCargo":
                                auxdata.setIdCargo(Integer.parseInt(fieldValue));
                                break;    
                            case "autorizaAusencia":
                                boolean autoriza=false;
                                if (fieldValue.compareTo("S")==0)
                                    autoriza=true;
                                auxdata.setAutorizaAusencia(autoriza);
                                break;    
                            case "cambiarFoto":
                                boolean cambiaFoto=false;
                                if (fieldValue.compareTo("S")==0)
                                    cambiaFoto=true;
                                auxdata.setCambiarFoto(cambiaFoto);
                                break;
                            case "fechaTerminoContratoAsStr":
                                {
                                    Date auxDate;
                                    Calendar c1 = GregorianCalendar.getInstance();
                                    c1.set(3000, 11, 31);
                                    auxDate = c1.getTime();//fecha por defecto 31-12-3000
                                    try{
                                        auxDate = fechaFormat.parse(fieldValue);
                                        auxdata.setFechaTerminoContrato(auxDate);
                                    }catch(ParseException pex){
                                        System.err.println("[EmpleadosController."
                                            + "guardarFotoEmpleado]"
                                            + "error al parsear fecha "
                                            + "termino contrato: " + pex.toString()+", setear fecha 31-12-3000");
                                        auxdata.setFechaTerminoContrato(auxDate);
                                    }   
                                    break;
                                }
                            case "articulo22":
                                boolean articulo22=false;
                                if (fieldValue.compareTo("S")==0)
                                    articulo22=true;
                                auxdata.setArticulo22(articulo22);
                                break;
                            case "cod_interno":
                                auxdata.setCodInterno(fieldValue);
                                break;
                            case "cod_interno_adicional":
                                auxdata.setCodInternoCaracterAdicional(fieldValue);
                                break;
                            case "claveMarcacion":
                                auxdata.setClaveMarcacion(fieldValue);
                                break;    
                            case "contratoIndefinido":
                                boolean contratoIndefinido=true;
                                if (fieldValue.compareTo("N")==0)
                                    contratoIndefinido=false;
                                auxdata.setContratoIndefinido(contratoIndefinido);
                                break;
                            case "modificar":
                                boolean modificarEDC=false;
                                if (fieldValue.compareTo("Si")==0)
                                    modificarEDC = true;
                                auxdata.setModificarEmpresaDeptoCenco(modificarEDC);
                                break;
                            case "fechaDesvinculacionAsStr":
                            {
                                Date auxDate = null;
                                try{
                                    auxDate = fechaFormat.parse(fieldValue);
                                    auxdata.setFechaDesvinculacion(auxDate);
                                }catch(ParseException pex){
                                    System.err.println("[EmpleadosController.guardarFotoEmpleado]"
                                            + "error al parsear fecha desvinculacion: " + pex.toString());
                                }   
                                break;
                            }
                            case "continuidadLaboral":
                                auxdata.setContinuidadLaboral(fieldValue);
                                break;
                            case "nuevaFechaIniContratoAsStr":
                            {
                                Date auxDate = null;
                                try{
                                    auxDate = fechaFormat.parse(fieldValue);
                                    auxdata.setNuevaFechaIniContrato(auxDate);
                                }catch(ParseException pex){
                                    System.err.println("[EmpleadosController.guardarFotoEmpleado]"
                                        + "error al parsear Nueva fecha inicio contrato: " + pex.toString());
                                }   
                                break;
                            }    
                        }        
                        //fin procesar campos normales    
                    }else{
                        System.out.println(WEB_NAME+"[EmpleadosController.guardarFotoEmpleado]procesar campo file."
                            + "Guardar foto como archivo (upload)");
                        String fileName = item.getName();
                        long sizeInBytes = item.getSize();
                        File uploadedFile = new File(pathUploadedFiles + File.separator + fileName);
                        item.write(uploadedFile);
                        System.out.println(WEB_NAME+"[EmpleadosController.guardarFotoEmpleado]File Size: " + sizeInBytes + " bytes");
                        System.out.println(WEB_NAME+"[EmpleadosController.guardarFotoEmpleado]File Path: " + uploadedFile.getPath());
                        System.out.println(WEB_NAME+"[EmpleadosController.guardarFotoEmpleado]fileName: " + fileName);
                        auxdata.setPathFoto(fileName);
                    }
                }
            }catch(FileUploadException fuex){
                System.err.println("[EmpleadosController.guardarFotoEmpleado]Error al leer parametros multipart: " + fuex.toString());
            }catch(UnsupportedEncodingException uex){
                System.err.println("[EmpleadosController.guardarFotoEmpleado]Error en encode: " + uex.toString());
            }catch(Exception ex){
                System.err.println("[EmpleadosController.guardarFotoEmpleado]Error al guardar upload archivo: " + ex.toString());
            }    
            
        }
    
        return auxdata;
    }
    
}
