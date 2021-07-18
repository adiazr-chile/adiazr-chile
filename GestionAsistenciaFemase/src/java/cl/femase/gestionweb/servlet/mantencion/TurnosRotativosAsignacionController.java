package cl.femase.gestionweb.servlet.mantencion;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.business.TurnoRotativoBp;
import cl.femase.gestionweb.business.TurnosBp;
import cl.femase.gestionweb.vo.AsignacionTurnoRotativoVO;
import cl.femase.gestionweb.vo.DuracionVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.TurnoRotativoVO;
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
import java.util.Date;
import java.util.StringTokenizer;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

public class TurnosRotativosAsignacionController extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 996L;
    
    public TurnosRotativosAsignacionController() {
        
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        if (session!=null) session.removeAttribute("mensaje");
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
        if (session!=null) session.removeAttribute("mensaje");
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

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(true);
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        if (session!=null) session.removeAttribute("mensaje");
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        TurnoRotativoBp auxnegocio  = new TurnoRotativoBp(appProperties);
        EmpleadosBp auxempleados    = new EmpleadosBp(appProperties);
        if(request.getParameter("action") != null){
            System.out.println("[TurnosRotativosAsignacionController]"
                + "action is: " + request.getParameter("action"));
            List<EmpleadoVO> listaEmpleados = new ArrayList<>();
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            response.setContentType("application/json");

            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("ATR");
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
            
            //Fetch Data from User Table
            int startPageIndex      = 0;
            int numRecordsPerPage   = 10;
            String jtSorting        = "fecha_desde";
            /** filtros de busqueda */
            
            if (request.getParameter("jtStartIndex") != null) 
                startPageIndex = Integer.parseInt(request.getParameter("jtStartIndex"));
            if (request.getParameter("jtPageSize") != null) 
                numRecordsPerPage   = Integer.parseInt(request.getParameter("jtPageSize"));
            if (request.getParameter("jtSorting") != null) 
                jtSorting   = request.getParameter("jtSorting");
            System.out.println("TurnosRotativosAsignacionController." +
                " jtSorting: " + request.getParameter("jtSorting"));
            
            if (jtSorting.contains("nombres")) jtSorting = jtSorting.replaceFirst("nombres","empl_nombres");
            else if (jtSorting.contains("rut")) jtSorting = jtSorting.replaceFirst("rut","empl_rut");
            else if (jtSorting.contains("codInterno")) jtSorting = jtSorting.replaceFirst("codInterno","cod_interno");
                    
            System.out.println("[TurnosRotativosAsignacionController."
                + "processRequest]"
                + ", empresaCod: " + request.getParameter("empresaId")
                + ", deptoId: " + request.getParameter("deptoId")
                + ", cencoId: " + request.getParameter("cencoId"));
            
            boolean listar=true;
            
            if (action.compareTo("list") == 0){
                System.out.println("[TurnosRotativosAsignacionController]"
                    + "mostrando empleados con turno rotativo."+
                        "empresaId= " + request.getParameter("empresaId")
                        +", deptoId= " + request.getParameter("deptoId")
                        +", cencoId= " +request.getParameter("cencoId"));
                try{
                    int objectsCount = 0;
                    if (listar){
                        String pEmpresa = request.getParameter("empresaId");
                        String pDepto   = request.getParameter("deptoId");
                        String pCenco   = request.getParameter("cencoId");
                        int intCenco = Integer.parseInt(pCenco);
                        
                        if (pEmpresa != null && pEmpresa.compareTo("-1")!=0 
                                && (pDepto != null && pDepto.compareTo("-1") != 0)        
                                && (intCenco != -1)){        
                            
                            //obtener turno rotativo general desde tabla turno
                            TurnosBp auxturno   = new TurnosBp(appProperties);
                            
                            int idTurnoRotativo = auxturno.getTurnoRotativo(pEmpresa);
                            
                            listaEmpleados = auxempleados.getEmpleadosByTurno(pEmpresa, 
                                pDepto, 
                                intCenco, 
                                idTurnoRotativo, 
                                null, 
                                startPageIndex, 
                                startPageIndex, 
                                jtSorting);

                            //Get Total Record Count for Pagination
                            objectsCount = auxempleados.getEmpleadosByTurnoCount(pEmpresa, 
                                pDepto, 
                                intCenco, 
                                idTurnoRotativo, 
                                null);
                        }
                    }
                    
                    //Convert Java Object to Json
                    JsonElement element = gson.toJsonTree(listaEmpleados,
                        new TypeToken<List<EmpleadoVO>>() {}.getType());

                    JsonArray jsonArray = element.getAsJsonArray();
                    String listData=jsonArray.toString();
                        
                    //Return Json in the format required by jTable plugin
                    listData="{\"Result\":\"OK\",\"Records\":" + 
                        listData+",\"TotalRecordCount\": " + 
                        objectsCount + "}";
                    response.getWriter().print(listData);
                    //request.getRequestDispatcher("/mantenedores/mantenedoresFrmSet.jsp").forward(request, response);
                }catch(IOException ex){
                    String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getMessage()+"}";
                    response.getWriter().print(error);
                    ex.printStackTrace();
                }   
            }else if (action.compareTo("masivaListEmpleados") == 0){
                System.out.println("[TurnosRotativosAsignacionController]"
                    + "mostrando empleados con turno rotativo mas reciente."+
                        "empresaId= " + request.getParameter("empresaId")
                        +", deptoId= " + request.getParameter("deptoId")
                        +", cencoId= " +request.getParameter("cencoId"));
                try{
                    int objectsCount = 0;
                    List<EmpleadoVO> listaEmpleadosConTurno = new ArrayList<>();
                    if (listar){
                        String pEmpresa = request.getParameter("empresaId");
                        String pDepto   = request.getParameter("deptoId");
                        String pCenco   = request.getParameter("cencoId");
                        int intCenco = Integer.parseInt(pCenco);
                        
                        if (pEmpresa != null && pEmpresa.compareTo("-1")!=0 
                                && (pDepto != null && pDepto.compareTo("-1") != 0)        
                                && (intCenco != -1)){        
                            
                            //obtener turno rotativo general desde tabla turno
                            TurnosBp auxturno   = new TurnosBp(appProperties);
                            int idTurnoRotativo = auxturno.getTurnoRotativo(pEmpresa);
                            listaEmpleados = auxempleados.getEmpleadosByTurno(pEmpresa, 
                                pDepto, 
                                intCenco, 
                                idTurnoRotativo, 
                                null, 
                                startPageIndex, 
                                startPageIndex, 
                                jtSorting);

                            //iterar empleados y para c/u rescatar el mas reciente turno rotativo
                            for (int i = 0; i < listaEmpleados.size(); i++) {
                                EmpleadoVO empleado = listaEmpleados.get(i);
                                List<AsignacionTurnoRotativoVO> asignaciones = auxnegocio.getAsignacionTurnosRotativosByRut(pEmpresa,
                                    empleado.getRut(), -1, null, null, 1); 
                                if (asignaciones.size() > 0) {
                                    empleado.setNombreTurno(asignaciones.get(0).getNombreTurno());
                                    empleado.setFechaDesdeTurno(asignaciones.get(0).getFechaDesde());
                                    empleado.setFechaHastaTurno(asignaciones.get(0).getFechaHasta());
                                }
                                System.out.println("[TurnosRotativosAsignacionController]"
                                    + "add empleado final rut: "+empleado.getRut());
                                listaEmpleadosConTurno.add(empleado);
                            }
                                                                    
                            //Get Total Record Count for Pagination
                            objectsCount = auxempleados.getEmpleadosByTurnoCount(pEmpresa, 
                                pDepto, 
                                intCenco, 
                                idTurnoRotativo, 
                                null);
                            System.out.println("[TurnosRotativosAsignacionController]"
                                    + "rows= "+objectsCount);
                        }
                    }
                    
                    //Convert Java Object to Json
                    JsonElement element = gson.toJsonTree(listaEmpleadosConTurno,
                        new TypeToken<List<EmpleadoVO>>() {}.getType());

                    JsonArray jsonArray = element.getAsJsonArray();
                    String listData=jsonArray.toString();
                        
                    //Return Json in the format required by jTable plugin
                    listData="{\"Result\":\"OK\",\"Records\":" + 
                        listData+",\"TotalRecordCount\": " + 
                        objectsCount + "}";
                    response.getWriter().print(listData);
                    //request.getRequestDispatcher("/mantenedores/mantenedoresFrmSet.jsp").forward(request, response);
                }catch(IOException ex){
                    String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getMessage()+"}";
                    response.getWriter().print(error);
                    ex.printStackTrace();
                }   
            }else if (action.compareTo("mostrarAsignacionEmpleado") == 0){
                    String pEmpresa     = request.getParameter("empresa");
                    String pRutEmpleado = request.getParameter("rut");
                    EmpleadoVO infoempleado = auxempleados.getEmpleado(pEmpresa, pRutEmpleado);
                    
                    System.out.println("[TurnosRotativosAsignacionController]"
                        + "mostrarAsignacionEmpleado. "
                        + "Empresa: " + pEmpresa
                        +", rutEmpleado: "+pRutEmpleado);
                    
                    //obtener asignacion de turnos
                    List<AsignacionTurnoRotativoVO> listaTurnos = 
                        auxnegocio.getAsignacionTurnosRotativosByRut(pEmpresa, pRutEmpleado, 0, null, null,0);
                    
                    request.setAttribute("infoempleado", infoempleado);
                    request.setAttribute("listaturnos", listaTurnos);
                    request.getRequestDispatcher("/mantencion/turno_rotativo_asignacion_2.jsp").forward(request, response);
            }else if (action.compareTo("crearModificarAsignacion") == 0){
                    //mostrar vista con los periodos y fechas del turno seleccionado
                    String pEmpresa     = request.getParameter("empresaId");
                    String pRutEmpleado = request.getParameter("rutEmpleado");
                    String asignacionKey = request.getParameter("asignacionKey");//
                    int idTurno         = 0;
                    String fechaDesde   = "";
                    String fechaHasta   = "";
                    AsignacionTurnoRotativoVO turnoAsignado = new AsignacionTurnoRotativoVO();
                    if (asignacionKey!=null && asignacionKey.compareTo("") != 0){
                        System.out.println("[TurnosRotativosAsignacionController]Ver/Modificar asignacion de turno...");
                        StringTokenizer tokenTurno = new StringTokenizer(asignacionKey, "|");
                        while (tokenTurno.hasMoreTokens()){
                            idTurno = Integer.parseInt(tokenTurno.nextToken());
                            fechaDesde = tokenTurno.nextToken();
                            fechaHasta = tokenTurno.nextToken();
                        }
                        //obtener asignacion de turnos
                        List<AsignacionTurnoRotativoVO> listaTurnos = 
                            auxnegocio.getAsignacionTurnosRotativosByRut(pEmpresa, 
                                pRutEmpleado, 
                                idTurno, 
                                fechaDesde, 
                                fechaHasta,0);
                        turnoAsignado = listaTurnos.get(0);
                        
                    }else{
                        System.out.println("[TurnosRotativosAsignacion"
                            + "Controller]Crear nueva "
                            + "asignacion de turno...");
                    }
                    EmpleadoVO infoempleado = auxempleados.getEmpleado(pEmpresa, pRutEmpleado);
                    List<TurnoRotativoVO> turnos = auxnegocio.getTurnos(pEmpresa, null, 0, 0, "id_turno");
                    ArrayList<DuracionVO> duraciones = auxnegocio.getDuraciones();
                    if (turnoAsignado != null){
                        System.out.println("[TurnosRotativosAsignacion"
                            + "Controller]"
                            + "Id turno: " + turnoAsignado.getIdTurno()
                            + ", nombre turno: " + turnoAsignado.getNombreTurno()
                            + ", idDuracion: " + turnoAsignado.getIdDuracion()
                            + ", diasDuracion: " + turnoAsignado.getDiasDuracion());
                    }
                    //seteo de objetos a utilizar
                    request.setAttribute("infoempleado", infoempleado);
                    request.setAttribute("turnoAsignado", turnoAsignado);
                    request.setAttribute("duraciones", duraciones);
                    request.setAttribute("turnos", turnos);
                    request.getRequestDispatcher("/mantencion/turno_rotativo_asignacion_3.jsp").forward(request, response);
            }else if (action.compareTo("crear") == 0){ //------------------------------------------------------------------------------ Crear nueva asignacion ------------------------
                    System.out.println("[TurnosRotativosAsignacion"
                        + "Controller]Insert "
                        + "asignacion de turno...");
                    String pEmpresa     = request.getParameter("empresaId");
                    String pRutEmpleado = request.getParameter("rutEmpleado");
                    String keyDuracion   = request.getParameter("duracion");   //key=idDuracion|numDias
                    String idTurno      = request.getParameter("turno");
                    String fecInicio    = request.getParameter("startDate");
                    String fecTermino   = request.getParameter("endDate");
                    
                    String asignacionConflictoKey = request.getParameter("asignacionConflictoKey");//
                    if (asignacionConflictoKey!=null && asignacionConflictoKey.compareTo("") != 0){
                        int idTurnoConflicto        = 0;
                        String fecInicioConflicto    = "";
                        String fecTerminoConflicto   = "";
                        StringTokenizer tokenTurno = new StringTokenizer(asignacionConflictoKey, "|");
                        while (tokenTurno.hasMoreTokens()){
                            idTurnoConflicto = Integer.parseInt(tokenTurno.nextToken());
                            fecInicioConflicto = tokenTurno.nextToken();
                            fecTerminoConflicto = tokenTurno.nextToken();
                        }
                        System.out.println("[TurnosRotativosAsignacionController](Crear)Asignacion conflicto"
                            + "Eliminar asignacion conflicto. "
                            + "idTurno: " + idTurnoConflicto
                            + ", fechaInicio: " + fecInicioConflicto
                            + ", fechaTermino: " + fecTerminoConflicto);
                        AsignacionTurnoRotativoVO deleteAsignacion=new AsignacionTurnoRotativoVO();
                        deleteAsignacion.setEmpresaId(pEmpresa);
                        deleteAsignacion.setRutEmpleado(pRutEmpleado);
                        deleteAsignacion.setIdTurno(idTurnoConflicto);
                        deleteAsignacion.setFechaDesde(fecInicioConflicto);
                        deleteAsignacion.setFechaHasta(fecTerminoConflicto);
                        auxnegocio.deleteAsignacion(deleteAsignacion, resultado);
                    }
                                        
                    EmpleadoVO infoempleado = auxempleados.getEmpleado(pEmpresa, pRutEmpleado);
                    
                    resultado.setEmpresaId(pEmpresa);
                    resultado.setDeptoId(infoempleado.getDepartamento().getId());
                    resultado.setCencoId(infoempleado.getCentroCosto().getId());
                    resultado.setRutEmpleado(pRutEmpleado);
                    
                    StringTokenizer tokenDuracion=new StringTokenizer(keyDuracion, "|");
                    int idDuracion = Integer.parseInt(tokenDuracion.nextToken());
                    int numDiasDuracion = Integer.parseInt(tokenDuracion.nextToken());
                    System.out.println("[TurnosRotativosAsignacion"
                        + "Controller]Insert "
                        + "asignacion de turno."
                        + "empresa: " + pEmpresa
                        + ",rutEmpleado: " + pRutEmpleado
                        + ",idTurno: " + idTurno
                        + ",idDuracion: " + idDuracion
                        + ",numDiasDuracion: " + numDiasDuracion
                        + ",fecInicio: " + fecInicio
                        + ",fecTermino: " + fecTermino    
                    );
                    
                    AsignacionTurnoRotativoVO newAsignacion=new AsignacionTurnoRotativoVO();
                    newAsignacion.setEmpresaId(pEmpresa);
                    newAsignacion.setRutEmpleado(pRutEmpleado);
                    newAsignacion.setIdTurno(Integer.parseInt(idTurno));
                    newAsignacion.setIdDuracion(idDuracion);
                    newAsignacion.setDiasDuracion(numDiasDuracion);
                    newAsignacion.setFechaDesde(fecInicio);
                    newAsignacion.setFechaHasta(fecTermino);
                    
                    /**
                     * Validar interseccion de turnos
                     */
                    List<AsignacionTurnoRotativoVO> asignacionesConflicto = 
                        auxnegocio.getAsignacionesConflicto(pEmpresa, 
                            pRutEmpleado, 
                            0,
                            fecInicio, 
                            fecTermino);
                    
                    if (asignacionesConflicto.isEmpty()){
                        System.out.println("[TurnosRotativosAsignacion"
                            + "Controller]Asignacion de turno correcta. Insertar: "
                            + "empresa: " + pEmpresa
                            + ",rutEmpleado: " + pRutEmpleado
                            + ",idTurno: " + idTurno
                            + ",idDuracion: " + idDuracion
                            + ",fecInicio: " + fecInicio
                            + ",fecTermino: " + fecTermino    
                        );
                        auxnegocio.insertAsignacion(newAsignacion, resultado);
                        request.getRequestDispatcher("/TurnosRotativosAsignacionController?"
                            + "action=mostrarAsignacionEmpleado"
                            + "&empresa=" + pEmpresa 
                            + "&rut=" + pRutEmpleado).forward(request, response);
                    }else{
                        /**
                         * La asignacion a ingresar intersecta con una o mas
                         * asignaciones existentes.
                         */
                        List<TurnoRotativoVO> turnos 
                            = auxnegocio.getTurnos(pEmpresa, null, 
                                    0, 0, "id_turno");
                        ArrayList<DuracionVO> duraciones
                            = auxnegocio.getDuraciones();
                        //seteo de asignaciones conflicto
                        request.setAttribute("asignacionesConflicto", asignacionesConflicto);
                        //seteo de objetos a utilizar
                        request.setAttribute("turnoAsignado", new AsignacionTurnoRotativoVO());
                        request.setAttribute("newTurnoAsignado", newAsignacion);
                        request.setAttribute("infoempleado", infoempleado);
                        request.setAttribute("duraciones", duraciones);
                        request.setAttribute("turnos", turnos);
                        
                        request.setAttribute("newIdDuracion", ""+idDuracion);
                        request.setAttribute("newIdTurno", idTurno);
                        request.setAttribute("newFechaDesde", fecInicio);
                        request.setAttribute("newFechaHasta", fecTermino);
                        request.getRequestDispatcher("/mantencion/turno_rotativo_asignacion_3.jsp").forward(request, response);
                        
                    }
            }else if (action.compareTo("modificar") == 0){//------------------------------------------------------------------------------ Modificar asignacion ------------------------
                    System.out.println("[TurnosRotativosAsignacion"
                        + "Controller]Modificar asignacion de turno existente...");
                    String pEmpresa     = request.getParameter("empresaId");
                    String pRutEmpleado = request.getParameter("rutEmpleado");
                    
                    String newKeyDuracion   = request.getParameter("duracion");
                    String newIdTurno      = request.getParameter("turno");
                    String newFecInicio    = request.getParameter("startDate");
                    String newFecTermino   = request.getParameter("endDate");
                    
                    System.out.println("[TurnosRotativosAsignacion"
                        + "Controller]"
                        + "newKeyDuracion: " + newKeyDuracion
                        + ",newIdTurno: " + newIdTurno
                        + ",newFecInicio: " + newFecInicio
                        + ",newFecTermino: " + newFecTermino);
                    
                    int currentIdTurno                  = Integer.parseInt(request.getParameter("currentIdTurno"));
                    int currentIdDuracion                 = Integer.parseInt(request.getParameter("currentIdDuracion"));
                    String currentFechaInicio    = request.getParameter("currentFechaDesde");
                    String currentFecTermino        = request.getParameter("currentFechaHasta");
                    
                    //obtener asignacion de turno actual
                    List<AsignacionTurnoRotativoVO> listaTurnos = 
                        auxnegocio.getAsignacionTurnosRotativosByRut(pEmpresa, 
                            pRutEmpleado, 
                            currentIdTurno, 
                            currentFechaInicio, 
                            currentFecTermino,0);
                    AsignacionTurnoRotativoVO currentAsignacion = listaTurnos.get(0);
                    System.out.println("---->Current key duracion: " + 
                        currentAsignacion.getIdDuracion() + "|" 
                        + currentAsignacion.getDiasDuracion());
                    String asignacionConflictoKey = request.getParameter("asignacionConflictoKey");//
                    if (asignacionConflictoKey!=null && asignacionConflictoKey.compareTo("") != 0){
                        int idTurnoConflicto        = 0;
                        String fecInicioConflicto    = "";
                        String fecTerminoConflicto   = "";
                        StringTokenizer tokenTurno = new StringTokenizer(asignacionConflictoKey, "|");
                        while (tokenTurno.hasMoreTokens()){
                            idTurnoConflicto = Integer.parseInt(tokenTurno.nextToken());
                            fecInicioConflicto = tokenTurno.nextToken();
                            fecTerminoConflicto = tokenTurno.nextToken();
                        }
                        System.out.println("[TurnosRotativosAsignacionController](Modificar)Asignacion conflicto"
                            + "Eliminar asignacion conflicto. "
                            + "idTurno: " + idTurnoConflicto
                            + ", fechaInicio: " + fecInicioConflicto
                            + ", fechaTermino: " + fecTerminoConflicto);
                        AsignacionTurnoRotativoVO deleteAsignacion=new AsignacionTurnoRotativoVO();
                        deleteAsignacion.setEmpresaId(pEmpresa);
                        deleteAsignacion.setRutEmpleado(pRutEmpleado);
                        deleteAsignacion.setIdTurno(idTurnoConflicto);
                        deleteAsignacion.setFechaDesde(fecInicioConflicto);
                        deleteAsignacion.setFechaHasta(fecTerminoConflicto);
                        auxnegocio.deleteAsignacion(deleteAsignacion, resultado);
                    }
                                        
                    EmpleadoVO infoempleado = auxempleados.getEmpleado(pEmpresa, pRutEmpleado);
                    
                    resultado.setEmpresaId(pEmpresa);
                    resultado.setDeptoId(infoempleado.getDepartamento().getId());
                    resultado.setCencoId(infoempleado.getCentroCosto().getId());
                    resultado.setRutEmpleado(pRutEmpleado);
                    
                    StringTokenizer tokenDuracion=new StringTokenizer(newKeyDuracion, "|");
                    int newIdDuracion = Integer.parseInt(tokenDuracion.nextToken());
                    int newDiasDuracion = Integer.parseInt(tokenDuracion.nextToken());
                    System.out.println("[TurnosRotativosAsignacion"
                        + "Controller]Modificar asignacion de turno. Nuevos valores:"
                        + "empresa: " + pEmpresa
                        + ",rutEmpleado: " + pRutEmpleado
                        + ",idTurno: " + newIdTurno
                        + ",idDuracion: " + newIdDuracion
                        + ",diasDuracion: " + newDiasDuracion    
                        + ",fecInicio: " + newFecInicio
                        + ",fecTermino: " + newFecTermino    
                    );
                    
                    //asignacion con los nuevos datos seleccionados
                    AsignacionTurnoRotativoVO newAsignacion=new AsignacionTurnoRotativoVO();
                    newAsignacion.setEmpresaId(pEmpresa);
                    newAsignacion.setRutEmpleado(pRutEmpleado);
                    newAsignacion.setIdTurno(Integer.parseInt(newIdTurno));
                    newAsignacion.setIdDuracion(newIdDuracion);
                    newAsignacion.setDiasDuracion(newDiasDuracion);
                    newAsignacion.setFechaDesde(newFecInicio);
                    newAsignacion.setFechaHasta(newFecTermino);
                    
                    /**
                     * Validar interseccion de turnos antes de modificar asignacion de turno
                     */
                    List<AsignacionTurnoRotativoVO> asignacionesConflicto = 
                        auxnegocio.getAsignacionesConflicto(pEmpresa, 
                            pRutEmpleado, 
                            newAsignacion.getIdTurno(),
                            newFecInicio, 
                            newFecTermino);
                    
                    if (asignacionesConflicto.isEmpty()){
                       
                        auxnegocio.modifyAsignacion(currentAsignacion, 
                                newAsignacion, resultado);
                        request.getRequestDispatcher("/TurnosRotativosAsignacionController?"
                            + "action=mostrarAsignacionEmpleado"
                            + "&empresa=" + pEmpresa 
                            + "&rut=" + pRutEmpleado).forward(request, response);
                    }else{
                        /**
                         * La asignacion a actualizar intersecta con una o mas
                         * asignaciones existentes.
                         */
                        List<TurnoRotativoVO> turnos 
                            = auxnegocio.getTurnos(pEmpresa, null, 
                                    0, 0, "id_turno");
                        ArrayList<DuracionVO> duraciones = auxnegocio.getDuraciones();
                        //seteo de asignaciones conflicto
                        request.setAttribute("asignacionesConflicto", asignacionesConflicto);
                        //seteo de objetos a utilizar
                        request.setAttribute("turnoAsignado", currentAsignacion);
                        request.setAttribute("newTurnoAsignado", newAsignacion);
                        request.setAttribute("infoempleado", infoempleado);
                        request.setAttribute("duraciones", duraciones);
                        request.setAttribute("turnos", turnos);
                        
                        request.setAttribute("newIdDuracion", ""+newIdDuracion);
                        request.setAttribute("newIdTurno", newIdTurno);
                        request.setAttribute("newFechaDesde", newFecInicio);
                        request.setAttribute("newFechaHasta", newFecTermino);
                        request.getRequestDispatcher("/mantencion/turno_rotativo_asignacion_3.jsp").forward(request, response);
                        
                    }
            }else if (action.compareTo("deleteAsignacion") == 0){
                    //Eliminar una o mas asignaciones de turno
                    String pEmpresa     = request.getParameter("empresaId");
                    String pRutEmpleado = request.getParameter("rutEmpleado");
                    EmpleadoVO infoempleado = auxempleados.getEmpleado(pEmpresa, pRutEmpleado);
                    resultado.setEmpresaId(pEmpresa);
                    resultado.setDeptoId(infoempleado.getDepartamento().getId());
                    resultado.setCencoId(infoempleado.getCentroCosto().getId());
                    resultado.setRutEmpleado(pRutEmpleado);
                    
                    String[] checksValues = request.getParameterValues("asignacionKey_del");
                    System.out.println("[TurnosRotativos"
                        + "AsignacionController]"
                        + "Eliminar asignacion de turno. "
                        + "empresaId: " + pEmpresa
                        + ", rutEmpleado: " + pRutEmpleado);
                    int idTurno=0;
                    String fechaDesde = "";
                    String fechaHasta = "";
                    for (int i = 0; i < checksValues.length; i++) {
                        String asignacionKey = checksValues[i];//
                        
                        StringTokenizer tokenTurno = new StringTokenizer(asignacionKey, "|");
                        while (tokenTurno.hasMoreTokens()){
                            idTurno = Integer.parseInt(tokenTurno.nextToken());
                            fechaDesde = tokenTurno.nextToken();
                            fechaHasta = tokenTurno.nextToken();
                            
                            System.out.println("[TurnosRotativos"
                                + "AsignacionController]"
                                + "Eliminar asignacion de turno. "
                                + "turnoId: " + pEmpresa
                                + ", desde: " + fechaDesde
                                + ", hasta: " + fechaHasta);
                            
                            AsignacionTurnoRotativoVO deleteAsignacion=new AsignacionTurnoRotativoVO();
                            deleteAsignacion.setEmpresaId(pEmpresa);
                            deleteAsignacion.setRutEmpleado(pRutEmpleado);
                            deleteAsignacion.setIdTurno(idTurno);
                            deleteAsignacion.setFechaDesde(fechaDesde);
                            deleteAsignacion.setFechaHasta(fechaHasta);
                            auxnegocio.deleteAsignacion(deleteAsignacion, resultado);
                        }
                    
                    }
            }else if (action.compareTo("seleccionDuracionTurno") == 0){
                    System.out.println("[TurnosRotativos"
                        + "AsignacionController]Mostrar vista de seleccion de turno y duracion...");
                    String pEmpresa = request.getParameter("empresaId");
                    ArrayList<EmpleadoVO> empleados=new ArrayList<>();
                    String[] checksValues = request.getParameterValues("rutSelected");
                    for (int i = 0; i < checksValues.length; i++) {
                        String rutSelected = checksValues[i];//
                        EmpleadoVO infoempleado = auxempleados.getEmpleado(pEmpresa, rutSelected);
                        empleados.add(infoempleado);
                        System.out.println("[TurnosRotativos"
                            + "AsignacionController]rutSelected: "+rutSelected);
                    }
                    
                    List<TurnoRotativoVO> turnos 
                        = auxnegocio.getTurnos(pEmpresa, null, 
                                    0, 0, "id_turno");
                    ArrayList<DuracionVO> duraciones = auxnegocio.getDuraciones();
                    request.setAttribute("duraciones", duraciones);
                    request.setAttribute("turnos", turnos);    
                    request.setAttribute("empleados", empleados);
                    request.getRequestDispatcher("/mantencion/turno_rotativo_asignacion_masiva_2.jsp").forward(request, response);        
            }else if (action.compareTo("guardarAsignacionMasiva") == 0){
                        System.out.println("\n[TurnosRotativos"
                            + "AsignacionController]"
                            + "Guardar asignacion masiva empleados...");
                        
                        String[] empleados = request.getParameterValues("rutSelected");
                        String keyDuracion   = request.getParameter("duracion");   //key=idDuracion|numDias
                        String idTurno      = request.getParameter("turno");
                        String fecInicio    = request.getParameter("startDate");
                        String fecTermino   = request.getParameter("endDate");
                        StringTokenizer tokenDuracion=new StringTokenizer(keyDuracion, "|");
                        int idDuracion = Integer.parseInt(tokenDuracion.nextToken());
                        int numDiasDuracion = Integer.parseInt(tokenDuracion.nextToken());
                        
                        /**
                        * Para cada empleado, validar interseccion de turnos
                        * para el rango de fechas seleccionado
                        */
                        String empresaId    = "";
                        String rutEmpleado  = "";
                        for (int i = 0; i < empleados.length; i++) {
                            String rutSelected = empleados[i];//
                            StringTokenizer tokenEmpleado = new StringTokenizer(rutSelected, "|");
                            empresaId   = tokenEmpleado.nextToken();
                            rutEmpleado = tokenEmpleado.nextToken();
//                            EmpleadoVO infoempleado = auxempleados.getEmpleado(empresaId, rutEmpleado);
                            
                            System.out.println("[TurnosRotativos"
                                + "AsignacionController]Asignacion masiva "
                                + ". empresa: " + empresaId
                                + ", rutEmpleado: " + rutEmpleado);
                            
                            /**
                            * Validar interseccion de turnos antes de modificar asignacion de turno
                            */
                            /*
                            Si hay uno o mas turnos asignados que intersectan con las fechas 
                            que se estan asignando, estos seran eliminados y luego se
                            inserta la nueva asignacion...
                            */
                            List<AsignacionTurnoRotativoVO> asignacionesConflicto = 
                                auxnegocio.getAsignacionesConflicto(empresaId, 
                                        rutEmpleado, 
                                        0,
                                        fecInicio, 
                                        fecTermino);
                                
                            if (!asignacionesConflicto.isEmpty()){
                                for (int j = 0; j < asignacionesConflicto.size(); j++) {
                                        AsignacionTurnoRotativoVO asignacion = asignacionesConflicto.get(j);
                                        System.out.println("[TurnosRotativos"
                                            + "AsignacionController]Eliminar asignacion conflicto."
                                            + ". turnoId: " + asignacion.getIdTurno()
                                            + ", fechaInicio: " + asignacion.getFechaDesde()
                                            + ", fechaHasta: " + asignacion.getFechaHasta());
                                        
                                        AsignacionTurnoRotativoVO deleteAsignacion=new AsignacionTurnoRotativoVO();
                                        deleteAsignacion.setEmpresaId(empresaId);
                                        deleteAsignacion.setRutEmpleado(rutEmpleado);
                                        deleteAsignacion.setIdTurno(Integer.parseInt(idTurno));
                                        deleteAsignacion.setFechaDesde(fecInicio);
                                        deleteAsignacion.setFechaHasta(fecTermino);
                                        auxnegocio.deleteAsignacion(deleteAsignacion, resultado);
                                }
                            }
                            
                            System.out.println("[TurnosRotativosAsignacion"
                                + "Controller]Guardar asignacion masiva."
                                + "Insert "
                                + "asignacion de turno."
                                + "empresa: " + empresaId
                                + ",rutEmpleado: " + rutEmpleado
                                + ",idTurno: " + idTurno
                                + ",idDuracion: " + idDuracion
                                + ",numDiasDuracion: " + numDiasDuracion
                                + ",fecInicio: " + fecInicio
                                + ",fecTermino: " + fecTermino    
                            );
                    
                            //insertar nueva asignacion
                            AsignacionTurnoRotativoVO newAsignacion=new AsignacionTurnoRotativoVO();
                            newAsignacion.setEmpresaId(empresaId);
                            newAsignacion.setRutEmpleado(rutEmpleado);
                            newAsignacion.setIdTurno(Integer.parseInt(idTurno));
                            newAsignacion.setIdDuracion(idDuracion);
                            newAsignacion.setDiasDuracion(numDiasDuracion);
                            newAsignacion.setFechaDesde(fecInicio);
                            newAsignacion.setFechaHasta(fecTermino);
                            
                            auxnegocio.insertAsignacion(newAsignacion, resultado);
//                            request.getRequestDispatcher("/TurnosRotativosAsignacionController?"
//                            + "action=mostrarAsignacionEmpleado"
//                            + "&empresa=" + pEmpresa 
//                            + "&rut=" + pRutEmpleado).forward(request, response);
                        }//fin ciclo iteracion de empleados
                       request.getRequestDispatcher("/mantencion/trotativo_asigmasiva_frameset.jsp").forward(request, response);        
            }//fin action = guardar asignacion masiva 
            
        }
    }
    
}
