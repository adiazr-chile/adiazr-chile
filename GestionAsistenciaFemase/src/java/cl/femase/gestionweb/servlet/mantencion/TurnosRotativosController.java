package cl.femase.gestionweb.servlet.mantencion;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.business.TurnoRotativoBp;
import cl.femase.gestionweb.business.TurnoRotativoDetalleBp;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.vo.DetalleFechasTurnoRotativoVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.FechaVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.TurnoRotativoDetalleVO;
import cl.femase.gestionweb.vo.TurnoRotativoVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import org.apache.log4j.Logger;

public class TurnosRotativosController extends BaseServlet {

    /**
     *
     */
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 985L;
    
    public TurnosRotativosController() {
        
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

    /**
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
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

        TurnoRotativoBp turnoRotativoBp                 = new TurnoRotativoBp(appProperties);
                
        if(request.getParameter("action") != null){
            System.out.println(WEB_NAME+"[TurnosRotativosController]"
                + "action is: " + request.getParameter("action"));
            List<TurnoRotativoVO> listaObjetos = new ArrayList<TurnoRotativoVO>();
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            response.setContentType("application/json");

            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("TRO");//Turnos rotativos
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
            
            //Fetch Data from User Table
            int startPageIndex      = 0;
            int numRecordsPerPage   = 10;
            String jtSorting        = "nombre_turno asc";
            /** filtros de busqueda */
            String filtroNombre      = "";
            String filtroEmpresa     = "";
            int filtroEstado = -1;
            
            if (request.getParameter("jtStartIndex") != null) 
                startPageIndex = Integer.parseInt(request.getParameter("jtStartIndex"));
            if (request.getParameter("jtPageSize") != null) 
                numRecordsPerPage   = Integer.parseInt(request.getParameter("jtPageSize"));
            if (request.getParameter("jtSorting") != null) 
                jtSorting   = request.getParameter("jtSorting");
            
            if (jtSorting.contains("id")) jtSorting = jtSorting.replaceFirst("id","id_turno");
            else if (jtSorting.contains("nombre")) jtSorting = jtSorting.replaceFirst("nombre","nombre_turno");
            else if (jtSorting.contains("estado")) jtSorting = jtSorting.replaceFirst("estado","estado_turno");
            else if (jtSorting.contains("horaEntrada")) jtSorting = jtSorting.replaceFirst("horaEntrada","hora_entrada");
            else if (jtSorting.contains("horaSalida")) jtSorting = jtSorting.replaceFirst("horaSalida","hora_salida");
            else if (jtSorting.contains("fechaCreacionAsStr")) jtSorting = jtSorting.replaceFirst("fechaCreacionAsStr","fecha_creacion");
            else if (jtSorting.contains("fechaModificacionAsStr")) jtSorting = jtSorting.replaceFirst("fechaModificacionAsStr","fecha_modificacion");
            
            if (request.getParameter("filtroNombre") != null) 
                filtroNombre  = request.getParameter("filtroNombre");
            if (request.getParameter("filtroEmpresa") != null) 
                filtroEmpresa = request.getParameter("filtroEmpresa");
            if (request.getParameter("filtroEstado") != null) 
                filtroEstado = Integer.parseInt(request.getParameter("filtroEstado"));
            
            System.out.println(WEB_NAME+"Mantenedor - Turnos Rotativos - "
                + "Buscar empresa = " + filtroEmpresa
                + ", nombre turno: " + filtroNombre
                + ", estado: " + filtroEstado);
            
            //objeto usado para update/insert
            TurnoRotativoVO turnoRotativoVO = new TurnoRotativoVO();
         
            if (request.getParameter("empresaId") != null 
                && request.getParameter("empresaId").compareTo("") != 0){
                turnoRotativoVO.setEmpresaId(request.getParameter("empresaId"));
            }
            
            if (request.getParameter("id")!=null){
                turnoRotativoVO.setId(Integer.parseInt(request.getParameter("id")));
            }
            
            if (request.getParameter("nombre")!=null){
                turnoRotativoVO.setNombre(request.getParameter("nombre"));
            }
            
            if (request.getParameter("horaEntrada")!=null){
                turnoRotativoVO.setHoraEntrada(request.getParameter("horaEntrada"));
            }
            if (request.getParameter("horaSalida")!=null){
                turnoRotativoVO.setHoraSalida(request.getParameter("horaSalida"));
            }
            if (request.getParameter("estado")!=null){
                turnoRotativoVO.setEstado(Integer.parseInt(request.getParameter("estado")));
            }
            
            if(request.getParameter("aplicarTodos")!=null){
                turnoRotativoVO.setAplicarTodos(request.getParameter("aplicarTodos"));
            }
            
            if(request.getParameter("minutosColacion") != null){
                turnoRotativoVO.setMinutosColacion(Integer.parseInt(request.getParameter("minutosColacion")));
            }
            
            if(request.getParameter("holgura") != null){
                turnoRotativoVO.setHolgura(Integer.parseInt(request.getParameter("holgura")));
            }
            
            if(request.getParameter("nocturno") != null){
                turnoRotativoVO.setNocturno(request.getParameter("nocturno"));
            }
            
            session.removeAttribute("noasignados_turno_rotativo");
            session.removeAttribute("asignados_turno_rotativo");
                        
            if (action.compareTo("list") == 0) {
                System.out.println(WEB_NAME+"Mantenedor - Turnos Rotativos - "
                    + "mostrando turnos...");
                try{
                    int objectsCount = 0;
                    if (filtroEmpresa != null && filtroEmpresa.compareTo("-1") != 0){ 
                        listaObjetos = turnoRotativoBp.getTurnos(filtroEmpresa, 
                            filtroNombre, 
                            filtroEstado,
                            startPageIndex, 
                            numRecordsPerPage, 
                            jtSorting);
                        //Get Total Record Count for Pagination
                        objectsCount =
                            turnoRotativoBp.getTurnosCount(filtroEmpresa, 
                                filtroNombre, 
                                filtroEstado);
                    }
                    
                    //Convert Java Object to Json
                    JsonElement element = gson.toJsonTree(listaObjetos,
                        new TypeToken<List<TurnoRotativoVO>>() {}.getType());

                    JsonArray jsonArray = element.getAsJsonArray();
                    String listData=jsonArray.toString();
                        
                    session.setAttribute("turnos|"+userConnected.getUsername(), listaObjetos);
                    
                    //Return Json in the format required by jTable plugin
                    listData="{\"Result\":\"OK\",\"Records\":" + 
                        listData+",\"TotalRecordCount\": " + 
                        objectsCount + "}";
                    //System.out.println(WEB_NAME+"[TurnosRotativosController]json data: "+listData);
                    response.getWriter().print(listData);
                    //request.getRequestDispatcher("/mantenedores/mantenedoresFrmSet.jsp").forward(request, response);
                }catch(Exception ex){
                    String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getMessage()+"}";
                    response.getWriter().print(error);
                    ex.printStackTrace();
                }   
            }else if (action.compareTo("create") == 0) {
                    System.out.println(WEB_NAME+"Mantenedor - Turnos Rotativos- Insertar Turno...");
                    ResultCRUDVO doCreate = turnoRotativoBp.insert(turnoRotativoVO, resultado);					
                    
                    //enviar correo informando el evento de creacion de marca
                    
                    listaObjetos.add(turnoRotativoVO);

                    //Convert Java Object to Json
                    String json=gson.toJson(turnoRotativoVO);					
                    //Return Json in the format required by jTable plugin
                    String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";											
                    response.getWriter().print(listData);
            }else if (action.compareTo("update") == 0) {  
                    System.out.println(WEB_NAME+"Mantenedor - Turnos - "
                        + "Actualizar Turno Rotativo ID: "+ turnoRotativoVO.getId());
                    try{
                        
                        ResultCRUDVO doUpdate = turnoRotativoBp.update(turnoRotativoVO, resultado);
                        if (turnoRotativoVO.getAplicarTodos().compareTo("S") == 0){
                            turnoRotativoBp.updateTodos(turnoRotativoVO);
                        }
                        listaObjetos = turnoRotativoBp.getTurnos(turnoRotativoVO.getEmpresaId(), 
                            null, 
                            -1,
                            startPageIndex, 
                            numRecordsPerPage, 
                            "nombre_turno");
                        //Get Total Record Count for Pagination
                        int objectsCount = 
                            turnoRotativoBp.getTurnosCount(turnoRotativoVO.getEmpresaId(), filtroNombre, filtroEstado);
                                       
                        //Convert Java Object to Json
                        JsonElement element = gson.toJsonTree(listaObjetos,
                            new TypeToken<List<TurnoRotativoVO>>() {}.getType());

                        JsonArray jsonArray = element.getAsJsonArray();
                        String listData=jsonArray.toString();

                        session.setAttribute("turnos|"+userConnected.getUsername(), listaObjetos);

                        //Return Json in the format required by jTable plugin
                        listData="{\"Result\":\"OK\",\"Records\":" + 
                            listData+",\"TotalRecordCount\": " + 
                            objectsCount + "}";
                        //System.out.println(WEB_NAME+"[TurnosRotativosController]json data: "+listData);
                        response.getWriter().print(listData);
                        
//                        //Convert Java Object to Json
//                        String json=gson.toJson(turnoRotativoVO);					
//                        //Return Json in the format required by jTable plugin
//                        String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";											
//                        response.getWriter().print(listData);

                    }catch(Exception ex){
                        String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getStackTrace().toString()+"}";
                        response.getWriter().print(error);
                    }
            }else if (action.compareTo("crear_asignacion") == 0) {
                    String paramEmpresa = request.getParameter("empresaTurno");
                    String paramIdTurno = request.getParameter("idTurno");
                    String[] empleados = request.getParameterValues("rut");
                    /**
                     * Las fechas seleccionadas vienen en el sgte formato:
                     *  yyyy-MM|yyyy-MM-dd
                     * 
                     * Ejemplo: 2017-11|2017-11-09,2017-11|2017-11-10,2017-09|2017-09-09
                     */
                    String paramFechasLaborales = request.getParameter("input_dias_laborales");
                    String paramFechasLibres    = request.getParameter("input_dias_libres");
                    
                    System.out.println(WEB_NAME+"\n[Mantenedor - "
                        + "Turnos Rotativos]- "
                        + "Guardar Asignacion "
                        + "de empleados "
                        + "para: "
                        + "EmpresaId: " + paramEmpresa
                        + ", turnoID: " + paramIdTurno);
                             
                    guardaDetalleTurnoRotativo(paramEmpresa, paramIdTurno, 
                            empleados, paramFechasLaborales, 
                            paramFechasLibres);
                    
                    request.getRequestDispatcher("/mantencion/crear_asignacion_turno_rotativo.jsp").forward(request, response);        
            }else if (action.compareTo("load_asignacion") == 0) {
                    TurnoRotativoDetalleBp turnoRotativoDetalleBp   = new TurnoRotativoDetalleBp(appProperties);
                    EmpleadosBp empleadosbp=new EmpleadosBp(appProperties);
                    
                    String paramEmpresa = request.getParameter("empresaTurno");
                    String paramIdTurno = request.getParameter("idTurno");
                    String[] empleados = request.getParameterValues("rut");
                    String strAnioMes = request.getParameter("anioMes");// Ej.: 11-2017
                    StringTokenizer tokenaniomes = new StringTokenizer(strAnioMes, "-");
                    int intMes  = Integer.parseInt(tokenaniomes.nextToken());
                    int intAnio = Integer.parseInt(tokenaniomes.nextToken());
                    
                    EmpleadoVO infoEmpleado = empleadosbp.getEmpleado(paramEmpresa, empleados[0]);
                    TurnoRotativoDetalleVO abuscar = new TurnoRotativoDetalleVO();
                    abuscar.setEmpresaId(paramEmpresa);
                    abuscar.setRutEmpleado(infoEmpleado.getRut());
                    abuscar.setId(Integer.parseInt(paramIdTurno));
                    abuscar.setAnio(intAnio);
                    abuscar.setMes(intMes);
                    TurnoRotativoDetalleVO detalleTurno = turnoRotativoDetalleBp.getDetalleTurno(abuscar);
                    
                    System.out.println(WEB_NAME+"\n[Mantenedor - "
                        + "Turnos Rotativos]- "
                        + "Cargar Asignacion para el empleado "
                        + " empresaId: " + paramEmpresa
                        + ", turnoID: " + paramIdTurno
                        + ", rutEmpleado: " + infoEmpleado.getRut()
                        + ", nombre: " + infoEmpleado.getNombres()
                            +" " + infoEmpleado.getApePaterno()
                            +" " + infoEmpleado.getApeMaterno()
                        + ", departamento: " + infoEmpleado.getDepartamento().getNombre()
                        + ", centro costo: " + infoEmpleado.getCentroCosto().getNombre()
                        + ", anio: " + intAnio
                        + ", mes: " + intMes);
                    
                    ArrayList<String> diasLaborales = new ArrayList<>();
                    ArrayList<String> diasLibres = new ArrayList<>();
                    if (detalleTurno != null){
                        System.out.println(WEB_NAME+"\n[Mantenedor - "
                        + "Turnos Rotativos]- Detalle turno. "
                        + "Dias laborales: " + detalleTurno.getDiasLaborales()
                        + ", Dias libres: " + detalleTurno.getDiasLibres());
                        request.setAttribute("strDiasLaborales", detalleTurno.getDiasLaborales());
                        request.setAttribute("strDiasLibres", detalleTurno.getDiasLibres());
                        
                        if (detalleTurno.getDiasLaborales() != null 
                            && detalleTurno.getDiasLaborales().compareTo("") != 0){
                                StringTokenizer tokenfechasLab = new StringTokenizer(detalleTurno.getDiasLaborales(),",");
                                while (tokenfechasLab.hasMoreElements()){
                                    diasLaborales.add(tokenfechasLab.nextToken());
                                }
                        }
                        
                        
                        if (detalleTurno.getDiasLibres() != null 
                            && detalleTurno.getDiasLibres().compareTo("") != 0){
                                StringTokenizer tokenfechasLib = new StringTokenizer(detalleTurno.getDiasLibres(),",");
                                while (tokenfechasLib.hasMoreElements()){
                                    diasLibres.add(tokenfechasLib.nextToken());
                                }
                        }
                    }
                    
                    request.setAttribute("anioMesSelected", strAnioMes);
                    request.setAttribute("empleadoSelected", infoEmpleado);
                    //detalle turno rotativo
                    
                    request.setAttribute("diasLaborales", diasLaborales);
                    request.setAttribute("diasLibres", diasLibres);
                    
                    request.setAttribute("empresaTurnoSelected", paramEmpresa);
                    request.setAttribute("turnoSelected", paramIdTurno);
                    request.setAttribute("rutSelected", empleados[0]);
                    
                    request.getRequestDispatcher("/mantencion/modif_asig_turno_rotativo.jsp").forward(request, response);        
            }else if (action.compareTo("modif_asignacion")==0){
                        TurnoRotativoDetalleBp turnoRotativoDetalleBp = new TurnoRotativoDetalleBp(appProperties);
                        //modificar asignacion de dias para un empleado_x
                        String paramEmpresa = request.getParameter("empresaSelected");
                        String paramIdTurno = request.getParameter("turnoSelected");
                        String rutSelected = request.getParameter("rutSelected");
                        String[] empleados = new String[1];
                        empleados[0] = rutSelected;
                        String strAnioMes = request.getParameter("anioMes");// Ej.: 11-2017
                                                
                        StringTokenizer tokenaniomes = new StringTokenizer(strAnioMes, "-");
                        int intMes  = Integer.parseInt(tokenaniomes.nextToken());
                        int intAnio = Integer.parseInt(tokenaniomes.nextToken());
                        TurnoRotativoDetalleVO abuscar = new TurnoRotativoDetalleVO();
                        abuscar.setEmpresaId(paramEmpresa);
                        abuscar.setRutEmpleado(empleados[0]);
                        abuscar.setId(Integer.parseInt(paramIdTurno));
                        abuscar.setAnio(intAnio);
                        abuscar.setMes(intMes);
                       
                        //eliminar asignacion anterior existente
                        turnoRotativoDetalleBp.deleteDetalleTurnoEmpleado(abuscar);
                        
                        /**
                         * Las fechas seleccionadas vienen en el sgte formato:
                         *  yyyy-MM|yyyy-MM-dd
                         * 
                         * Ejemplo: 2017-11|2017-11-09,2017-11|2017-11-10,2017-09|2017-09-09
                         */
                        String paramFechasLaborales = request.getParameter("input_dias_laborales");
                        String paramFechasLibres    = request.getParameter("input_dias_libres");

                        System.out.println(WEB_NAME+"\n[Mantenedor - "
                            + "Turnos Rotativos]- "
                            + "Modificar Asignacion para el empleado. "
                            + " empresaId: " + paramEmpresa
                            + ", turnoID: " + paramIdTurno
                            + ", rutEmpleado: " + rutSelected
                            + ", anio: " + intAnio
                            + ", mes: " + intMes
                            + ", fechasLaborales: " + paramFechasLaborales
                            + ", fechasLibres: " + paramFechasLibres);
                        
                        guardaDetalleTurnoRotativo(paramEmpresa, paramIdTurno, 
                            empleados, paramFechasLaborales, 
                            paramFechasLibres);

                        request.getRequestDispatcher("/mantencion/modif_asig_turno_rotativo.jsp").forward(request, response);        
            }
//            else if (action.compareTo("edit") == 0) {  
//                    System.out.println(WEB_NAME+"Mantenedor - Turnos Rotativos- Mostrar datos para edicion");
//                    try{
//                       // get info del turno
//                        TurnoRotativoVO detalleTurno = turnoRotativoBp.getTurno(idTurno);
////                        ResultCRUDVO doUpdate = auxnegocio.update(auxdata, resultado);
////                        String listData="{\"Result\":\"OK\"}";
////                        response.getWriter().print(listData);
//                        request.setAttribute("turno_selected", detalleTurno);
//                        //request.setAttribute("asignados_turno_rotativo", listaEmpleadosAsignados);
//                        request.getRequestDispatcher("/mantencion/new_edit_turno_rotativo.jsp").forward(request, response);
//                    }catch(Exception ex){
//                        String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getStackTrace().toString()+"}";
//                        response.getWriter().print(error);
//                    }
//            }
//            else 
//                if (action.compareTo("insert") == 0) {  
//                    System.out.println(WEB_NAME+"Mantenedor - Turnos Rotativos- Insertar nuevo turno");
//                    try{
//                       // get info del turno
//                        TurnoRotativoVO detalleTurno = turnoRotativoBp.getTurno(idTurno);
////                        ResultCRUDVO doUpdate = auxnegocio.update(auxdata, resultado);
////                        String listData="{\"Result\":\"OK\"}";
////                        response.getWriter().print(listData);
//                        request.setAttribute("turno_selected", detalleTurno);
//                        //request.setAttribute("asignados_turno_rotativo", listaEmpleadosAsignados);
//                        request.getRequestDispatcher("/mantencion/new_edit_turno_rotativo.jsp").forward(request, response);
//                    }catch(Exception ex){
//                        String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getStackTrace().toString()+"}";
//                        response.getWriter().print(error);
//                    }
            
//            else if (action.compareTo("asignacion_start") == 0) {  
//                    System.out.println(WEB_NAME+"Mantenedor - Turnos Rotativos"
//                        + "- Mostrar Formulario Asignacion "
//                            + "masiva de Turnos");
//                    
//                    TurnoRotativoVO turnoselected = auxnegocio.getTurno(idTurno);
//                    session.removeAttribute("turnoSelected");
//                    session.setAttribute("turnoSelected", turnoselected);
//                    
//                    List<EmpleadoVO> listaEmpleadosAsignados = 
//                        auxnegocio.getEmpleados(idTurno, empresaId, deptoId, idCenco, idCargo);
//                   
//                    session.setAttribute("asignados_turno_rotativo", listaEmpleadosAsignados);
//                    request.getRequestDispatcher("/mantencion/asignacion_turnos.jsp").forward(request, response);        
//            }
//            else if (action.compareTo("load_asignacion") == 0) {
//                    System.out.println(WEB_NAME+"\nMantenedor - Turnos Rotativos"
//                        + "- Mostrar Asignacion "
//                        + "para el turno: " + 
//                            idTurno);
//                    //lista de empleados no asignados al turno
//                    List<EmpleadoVO> listaEmpleadosNoAsignados = 
//                        auxnegocio.getEmpleadosNoAsignados(idTurno, empresaId, deptoId, idCenco, idCargo);
//                    session.removeAttribute("noasignados_turno_rotativo");
//                    session.setAttribute("noasignados_turno_rotativo", listaEmpleadosNoAsignados);
//                    
//                    List<EmpleadoVO> listaEmpleadosAsignados = 
//                        auxnegocio.getEmpleados(idTurno, empresaId, deptoId, idCenco, idCargo);
//                    session.removeAttribute("asignados_turno_rotativo");
//                    session.setAttribute("asignados_turno_rotativo", listaEmpleadosAsignados);
//                    
//                    request.getRequestDispatcher("/mantencion/asignacion_turnos.jsp").forward(request, response);        
//            }
            
      }
    }
        
    /**
    * 
    */
    private void guardaDetalleTurnoRotativo(String _empresaId,
            String _idTurno, 
            String[] _empleados,
            String _fechasLaborales,
            String _fechasLibres){
        /**
        * Hash para guardar las fechas seleccionadas. 
        * En cada caso se debe obtener el mes-anio correspondiente
        */
        HashMap<String,FechaVO> hashFechasLaborales = new HashMap<>();
        HashMap<String,FechaVO> hashFechasLibres    = new HashMap<>();
        /** Lista de anios y meses ara guardar*/
        LinkedHashMap<String,String> hashAnioMes = new LinkedHashMap<>();
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        TurnoRotativoDetalleBp turnoRotativoDetalleBp   = new TurnoRotativoDetalleBp(appProperties);
       //auxnegocio.eliminaTurno(empresaId, resultado)deleteAsignacionesDepartamento(deviceId,resultado);

       //dias laborales seleccionados
       if (_fechasLaborales!=null && _fechasLaborales.compareTo("")!=0){
           StringTokenizer tokenFechasLaborales = new StringTokenizer(_fechasLaborales, ",");//n elementos como fechas se hayan seleccionado
           while (tokenFechasLaborales.hasMoreTokens()){
               String tuplaFecha = tokenFechasLaborales.nextToken();//2017-11|2017-11-09
               System.out.println(WEB_NAME+"[TurnosRotativosController]tuplaFecha: " + tuplaFecha);
               StringTokenizer tokenTuplaFechaLaboral = new StringTokenizer(tuplaFecha, "|");//2 elementos
               while (tokenTuplaFechaLaboral.hasMoreTokens()){
                   String anioMesKey = tokenTuplaFechaLaboral.nextToken().trim();//2017-11 (KEY)
                   System.out.println(WEB_NAME+"[TurnosRotativosController]anioMesKey: " + anioMesKey);
                   hashAnioMes.put(anioMesKey,anioMesKey);

                   StringTokenizer tokenAnioMes = new StringTokenizer(anioMesKey, "-");//2 elementos {anio, mes}
                   String auxanio=tokenAnioMes.nextToken().trim();
                   String auxames=tokenAnioMes.nextToken();
                   System.out.println(WEB_NAME+"[TurnosRotativosController]"
                       + "auxAnio: " + auxanio+", auxMes: "+auxames);
                   int intAnio = Integer.parseInt(auxanio);
                   int intMes = Integer.parseInt(auxames);
                   String fechaSelected = tokenTuplaFechaLaboral.nextToken().trim();//2017-11-09
                   FechaVO objFecha = new FechaVO(intAnio, intMes, fechaSelected, anioMesKey); 

                   hashFechasLaborales.put(fechaSelected, objFecha);

               }
           }
       }

       //dias libres seleccionados
       if (_fechasLibres!=null && _fechasLibres.compareTo("")!=0){
           StringTokenizer tokenFechasLibres = new StringTokenizer(_fechasLibres, ",");//n elementos como fechas se hayan seleccionado
           while (tokenFechasLibres.hasMoreTokens()){
               String tuplaFechaLibre = tokenFechasLibres.nextToken();//2017-11|2017-11-09
               StringTokenizer tokenTuplaFechaLibre = new StringTokenizer(tuplaFechaLibre, "|");//2 elementos
               while (tokenTuplaFechaLibre.hasMoreTokens()){
                   String anioMesKey = tokenTuplaFechaLibre.nextToken().trim();//2017-11 (KEY)
                   hashAnioMes.put(anioMesKey,anioMesKey);
                   StringTokenizer tokenAnioMes = new StringTokenizer(anioMesKey, "-");//2 elementos {anio, mes}
                   int intAnio = Integer.parseInt(tokenAnioMes.nextToken().trim());
                   int intMes = Integer.parseInt(tokenAnioMes.nextToken());
                   String fechaSelected = tokenTuplaFechaLibre.nextToken().trim();//2017-11-09
                   FechaVO objFecha = new FechaVO(intAnio, intMes, fechaSelected,anioMesKey); 

                   hashFechasLibres.put(fechaSelected, objFecha);

               }
           }
       }

       HashMap<String, ArrayList<DetalleFechasTurnoRotativoVO>> hashFechas = new HashMap<>();

       //itera anio-mes
       hashAnioMes.values().stream().forEach((aniomesKey) -> {
           System.out.println(WEB_NAME+"[TurnosRotativosController]AnioMesKey = " + aniomesKey);
           ArrayList<DetalleFechasTurnoRotativoVO> listaFechas = new ArrayList<>();

           //------------ itera fechas laborales --------------------
           hashFechasLaborales.values().stream().forEach((objFechaLaboral) -> {
               final DetalleFechasTurnoRotativoVO detalleFechaLaboral = new DetalleFechasTurnoRotativoVO();
               if (objFechaLaboral.getAnioMes().compareTo(aniomesKey) == 0){
                   System.out.println(WEB_NAME+"[TurnosRotativosController]Add fecha laboral = " + objFechaLaboral.toString());
                   detalleFechaLaboral.setFechaLaboral(objFechaLaboral);
                   listaFechas.add(detalleFechaLaboral);
               }
           });

           // -------------- itera fechas libres para el anio-mes ------------------
           hashFechasLibres.values().stream().forEach((objFechaLibre) -> {
               if (objFechaLibre.getAnioMes().compareTo(aniomesKey) == 0){
                   final DetalleFechasTurnoRotativoVO detalleFechaLibre = new DetalleFechasTurnoRotativoVO();
                   System.out.println(WEB_NAME+"[TurnosRotativosController]Add fecha libre = " + objFechaLibre.toString());
                   detalleFechaLibre.setFechaLibre(objFechaLibre);
                   listaFechas.add(detalleFechaLibre);
               }
           });
           hashFechas.put(aniomesKey, listaFechas);

       });

       //iterar empleados para generar los objetos a insertar en turno_rotativo_detalle
       if (_empleados!=null){
           ArrayList<TurnoRotativoDetalleVO> detallesTR=new ArrayList<>();
           for (String rutEmpleado : _empleados) {
               System.out.println(WEB_NAME+"[TurnosRotativosController]"
                   + "rutEmpleado = " + rutEmpleado);
               if (rutEmpleado.compareTo("-1") != 0){
                   String diasLaborales    = "";
                   String diasLibres       = "";
                   //itera anio-mes y llena lista de detalle turno rotativo con la info de fechas de los empleados

                   for ( Map.Entry<String, String> entry : hashAnioMes.entrySet() )
                   {
                       diasLaborales    = "";
                       diasLibres       = "";
                       String aniomesKey = entry.getValue();
                       StringTokenizer tokenAnioMes=new StringTokenizer(aniomesKey, "-");
                       int intAnio = Integer.parseInt(tokenAnioMes.nextToken());
                       int intMes = Integer.parseInt(tokenAnioMes.nextToken());

                       System.out.println(WEB_NAME+"[TurnosRotativosController]AnioMesKey = " + aniomesKey);
                       ArrayList<DetalleFechasTurnoRotativoVO> listaFechas = hashFechas.get(aniomesKey);
                       Iterator<DetalleFechasTurnoRotativoVO> fechasIterator = listaFechas.iterator();
                       while (fechasIterator.hasNext()) {
                           DetalleFechasTurnoRotativoVO infoFecha=fechasIterator.next();
                           if (infoFecha.getFechaLaboral() != null){
                               System.out.println(WEB_NAME+"[TurnosRotativosController]"
                                   + "detalleFechas.fechaLaboral = " + infoFecha.getFechaLaboral().toString());
                               diasLaborales += infoFecha.getFechaLaboral().getFecha()+",";
                           }else{
                               System.out.println(WEB_NAME+"[TurnosRotativosController]"
                                   + "detalleFechas.fechaLibre = " + infoFecha.getFechaLibre().toString());
                               diasLibres += infoFecha.getFechaLibre().getFecha()+",";
                           }
                       }//itera fechas para el anio-mes
                       if (diasLaborales.compareTo("") != 0){
                            diasLaborales   = diasLaborales.substring(0, diasLaborales.length()-1);
                            diasLaborales   = Utilidades.getValoresOrdenados(diasLaborales, ",");
                       }
                       
                       if (diasLibres.compareTo("") != 0){
                            diasLibres      = diasLibres.substring(0, diasLibres.length()-1);
                            diasLibres      = Utilidades.getValoresOrdenados(diasLibres, ",");
                       }
                      
                       System.out.println(WEB_NAME+"\n[Mantenedor - "
                           + "Turnos Rotativos]- "
                           + "Insertar Asignacion turno rotativo."
                           + "EmpresaId: " + _empresaId
                           + ", turnoID: " + _idTurno
                           + ", rut: " + rutEmpleado
                           + ", anio: " + intAnio
                           + ", mes: " + intMes
                           + ", diasLaborales: " + diasLaborales
                           + ", diasLibres: " + diasLibres);
                       TurnoRotativoDetalleVO detalle=new TurnoRotativoDetalleVO();
                       detalle.setEmpresaId(_empresaId);
                       detalle.setRutEmpleado(rutEmpleado);
                       detalle.setId(Integer.parseInt(_idTurno));//id turno
                       detalle.setAnio(intAnio);
                       detalle.setMes(intMes);
                       detalle.setDiasLaborales(diasLaborales);
                       detalle.setDiasLibres(diasLibres);

                       detallesTR.add(detalle);

                   }//fin iteracion anio.mes     
               }//fin rut!=-1

           }//fin iteracion de empleados   

           //guardar detalle asignacion de turno rotativo
           turnoRotativoDetalleBp.openDbConnection();
           turnoRotativoDetalleBp.saveList(detallesTR);
           turnoRotativoDetalleBp.openDbConnection();
       }else {
           System.out.println(WEB_NAME+"\n[Mantenedor - Asignacion Turnos Rotativos"
           + "]- No hay empleados asignados para el Turno: " + 
               _idTurno);
       }
    }
}
