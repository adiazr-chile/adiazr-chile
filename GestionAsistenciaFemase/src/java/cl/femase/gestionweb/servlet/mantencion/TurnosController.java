package cl.femase.gestionweb.servlet.mantencion;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.TurnosBp;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.TurnoVO;
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
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

public class TurnosController extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 985L;
    
    public TurnosController() {
        
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

        TurnosBp auxnegocio=new TurnosBp(appProperties);

        if(request.getParameter("action") != null){
            System.out.println("[TurnosController]"
                + "action is: " + request.getParameter("action"));
            List<TurnoVO> listaObjetos = new ArrayList<TurnoVO>();
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            response.setContentType("application/json");

            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("TUR");
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
            
            //Fetch Data from User Table
            int startPageIndex      = 0;
            int numRecordsPerPage   = 10;
            String jtSorting        = "nombre_turno asc";
            /** filtros de busqueda */
            String filtroNombre      = "";
            String filtroEmpresa     = "";
            
            if (request.getParameter("jtStartIndex") != null) 
                startPageIndex = Integer.parseInt(request.getParameter("jtStartIndex"));
            if (request.getParameter("jtPageSize") != null) 
                numRecordsPerPage   = Integer.parseInt(request.getParameter("jtPageSize"));
            if (request.getParameter("jtSorting") != null) 
                jtSorting   = request.getParameter("jtSorting");
            
            if (jtSorting.contains("id")) jtSorting = jtSorting.replaceFirst("id","id_turno");
            else if (jtSorting.contains("nombre")) jtSorting = jtSorting.replaceFirst("nombre","nombre_turno");
            else if (jtSorting.contains("estado")) jtSorting = jtSorting.replaceFirst("estado","estado_turno");
            else if (jtSorting.contains("fechaCreacionAsStr")) jtSorting = jtSorting.replaceFirst("fechaCreacionAsStr","fecha_creacion");
            else if (jtSorting.contains("fechaModificacionAsStr")) jtSorting = jtSorting.replaceFirst("fechaModificacionAsStr","fecha_modificacion");
            else if (jtSorting.contains("minutosColacion")) jtSorting = jtSorting.replaceFirst("minutosColacion","minutos_colacion");
            
            if (request.getParameter("filtroNombre") != null) 
                filtroNombre  = request.getParameter("filtroNombre");
            if (request.getParameter("filtroEmpresa") != null) 
                filtroEmpresa = request.getParameter("filtroEmpresa");
            
            //objeto usado para update/insert
            TurnoVO auxdata = new TurnoVO();
         
            if (request.getParameter("id")!=null){
                auxdata.setId(Integer.parseInt(request.getParameter("id")));
            }
            if (request.getParameter("nombre")!=null){
                auxdata.setNombre(request.getParameter("nombre"));
            }

            if (request.getParameter("estado")!=null){
                auxdata.setEstado(Integer.parseInt(request.getParameter("estado")));
            }
            if (request.getParameter("empresaId") != null 
                && request.getParameter("empresaId").compareTo("") != 0){
                auxdata.setEmpresaId(request.getParameter("empresaId"));
            }
            if (request.getParameter("estado")!=null){
                auxdata.setEstado(Integer.parseInt(request.getParameter("estado")));
            }
            if (request.getParameter("rotativo") != null){
                auxdata.setRotativo(request.getParameter("rotativo"));
            }
            
            int idTurno = -1;
            int idCenco = -1;
            int idCargo = -1;
            String empresaId = "";
            String deptoId = "";
            
            session.removeAttribute("noasignados_turno");
            session.removeAttribute("asignados_turno");
            
            if (request.getParameter("idTurno")!=null){
                idTurno = Integer.parseInt(request.getParameter("idTurno"));
                if (request.getParameter("cencoId")!=null){
                    idCenco = Integer.parseInt(request.getParameter("cencoId"));
                }
                if (request.getParameter("cargoId")!=null){
                    idCargo = Integer.parseInt(request.getParameter("cargoId"));
                }
                empresaId = request.getParameter("empresaId");
                deptoId = request.getParameter("deptoId");
                System.out.println("Mantenedor de Turnos."
                    + "asignacion empleados. "
                    + "idTurno: "+idTurno
                    + ",idEmpresa: "+empresaId
                    + ",idDepto: "+deptoId
                    + ",idCenco: "+idCenco
                    + ",idCargo: "+idCargo);
            }
            if (action.compareTo("list")==0) {
                System.out.println("Mantenedor - Turnos - "
                    + "mostrando turnos. "
                    + "Empresa: " + filtroEmpresa
                    + ", nombre: " + filtroNombre);
                            
                try{
                    int objectsCount = 0;
                    if (filtroEmpresa.compareTo("-1") != 0){
                        listaObjetos = auxnegocio.getTurnos(filtroEmpresa, 
                            filtroNombre, 
                            startPageIndex, 
                            numRecordsPerPage, 
                            jtSorting);

                        //Get Total Record Count for Pagination
                        objectsCount = auxnegocio.getTurnosCount(filtroEmpresa, 
                            filtroNombre);
                    }
                    //Convert Java Object to Json
                    JsonElement element = gson.toJsonTree(listaObjetos,
                        new TypeToken<List<TurnoVO>>() {}.getType());

                    JsonArray jsonArray = element.getAsJsonArray();
                    String listData=jsonArray.toString();
                        
                    session.setAttribute("turnos|"+userConnected.getUsername(), listaObjetos);
                    
                    //Return Json in the format required by jTable plugin
                    listData="{\"Result\":\"OK\",\"Records\":" + 
                        listData+",\"TotalRecordCount\": " + 
                        objectsCount + "}";
                    System.out.println("[TurnosController]json data: "+listData);
                    response.getWriter().print(listData);
                    //request.getRequestDispatcher("/mantenedores/mantenedoresFrmSet.jsp").forward(request, response);
                }catch(Exception ex){
                    String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getMessage()+"}";
                    response.getWriter().print(error);
                    ex.printStackTrace();
                }   
            }else if (action.compareTo("create") == 0) {
                    System.out.println("Mantenedor - Turnos - Insertar Turno...");
                    MaintenanceVO doCreate = auxnegocio.insert(auxdata, resultado);					
                    listaObjetos.add(auxdata);

                    //Convert Java Object to Json
                    String json=gson.toJson(auxdata);					
                    //Return Json in the format required by jTable plugin
                    String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";											
                    response.getWriter().print(listData);
            }else if (action.compareTo("update") == 0) {  
                    System.out.println("Mantenedor - Turnos - Actualizar Turno...");
                    try{
                        MaintenanceVO doUpdate = auxnegocio.update(auxdata, resultado);
                        listaObjetos.add(auxdata);

                        //Convert Java Object to Json
                        String json=gson.toJson(auxdata);					
                        //Return Json in the format required by jTable plugin
                        String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";											
                        response.getWriter().print(listData);

                    }catch(Exception ex){
                        String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getStackTrace().toString()+"}";
                        response.getWriter().print(error);
                    }
            }else if (action.compareTo("asignacion_start") == 0) {  
                    System.out.println("Mantenedor - Turnos"
                        + "- Mostrar Formulario Asignacion "
                            + "masiva de Turnos");
                    
                    TurnoVO turnoselected = auxnegocio.getTurno(idTurno);
                    session.removeAttribute("turnoSelected");
                    session.setAttribute("turnoSelected", turnoselected);
                    
                    List<EmpleadoVO> listaEmpleadosAsignados = 
                        auxnegocio.getEmpleados(idTurno, empresaId, deptoId, idCenco, idCargo);
                   
                    session.setAttribute("asignados_turno", listaEmpleadosAsignados);
                    request.getRequestDispatcher("/mantencion/asignacion_turnos.jsp").forward(request, response);        
            }else if (action.compareTo("load_asignacion") == 0) {
                    System.out.println("\n\nMantenedor - Turnos"
                        + "- Mostrar Asignacion "
                        + "para el turno: " + 
                            idTurno);
                    //lista de empleados no asignados al turno
                    List<EmpleadoVO> listaEmpleadosNoAsignados = 
                        auxnegocio.getEmpleadosNoAsignados(idTurno, empresaId, deptoId, idCenco, idCargo);
                    session.removeAttribute("noasignados_turno");
                    session.setAttribute("noasignados_turno", listaEmpleadosNoAsignados);
                    
                    List<EmpleadoVO> listaEmpleadosAsignados = 
                        auxnegocio.getEmpleados(idTurno, empresaId, deptoId, idCenco, idCargo);
                    session.removeAttribute("asignados_turno");
                    session.setAttribute("asignados_turno", listaEmpleadosAsignados);
                    
                    request.getRequestDispatcher("/mantencion/asignacion_turnos.jsp").forward(request, response);        
            }else if (action.compareTo("save_asignacion") == 0) {  
                     System.out.println("\n\n[Mantenedor - "
                        + "Turnos]- "
                        + "Guardar Asignacion "
                        + "de empleados "
                        + "para el TurnoID: " + idTurno);
                   
                    //auxnegocio.eliminaTurno(empresaId, resultado)deleteAsignacionesDepartamento(deviceId,resultado);
                    //Guardar empleados asignados
                    String[] empleados = request.getParameterValues("empleados_selected");
                    if (empleados!=null){
                        for (int x=0;x < empleados.length;x++){
                            System.out.println("empleado seleccionado["+x+"] = "+empleados[x]);
                            auxnegocio.updateTurnoEmpleado(empleados[x], idTurno, resultado);
                        }     
                    }else System.out.println("\n\n[Mantenedor - Asignacion Turnos"
                        + "]- No hay empleados asignados para el Turno: " + 
                            idTurno);
                    
                    /**
                     * Cargar asignaciones de turno existentes
                     */
                    //lista de empleados no asignados al turno
                    List<EmpleadoVO> listaEmpleadosNoAsignados = 
                        auxnegocio.getEmpleadosNoAsignados(idTurno, empresaId, deptoId, idCenco, idCargo);
                    session.removeAttribute("noasignados_turno");
                    session.setAttribute("noasignados_turno", listaEmpleadosNoAsignados);
                    
                    List<EmpleadoVO> listaEmpleadosAsignados = 
                        auxnegocio.getEmpleados(idTurno, empresaId, deptoId, idCenco, idCargo);
                    session.removeAttribute("asignados_turno");
                    session.setAttribute("asignados_turno", listaEmpleadosAsignados);
                    
                    request.getRequestDispatcher("/mantencion/asignacion_turnos.jsp").forward(request, response);        
            }
      }
    }
    
}
