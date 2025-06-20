package cl.femase.gestionweb.servlet.permisos_examen_salud_preventiva;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.dao.PermisosExamenSaludPreventivaDAO;
import cl.femase.gestionweb.vo.PermisoExamenSaludPreventivaVO;
import cl.femase.gestionweb.vo.PropertiesVO;
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
import java.util.StringTokenizer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpSession;
import org.apache.log4j.Logger;

@WebServlet(name = "PermisoExamenSaludPreventivaController", urlPatterns = {"/servlet/PermisoExamenSaludPreventivaController"})
public class PermisoExamenSaludPreventivaController extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 995L;
    
    public PermisoExamenSaludPreventivaController() {
        
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

        PermisosExamenSaludPreventivaDAO permisosDao = new PermisosExamenSaludPreventivaDAO(appProperties);
        EmpleadosBp empleadosbp = new EmpleadosBp(appProperties);
        
        if(request.getParameter("action") != null){
            System.out.println(WEB_NAME+"[PESPController]"
                + "action is: " + request.getParameter("action")
                + ", tipo: " + request.getParameter("tipo"));
            List<PermisoExamenSaludPreventivaVO> listaObjetos = new ArrayList<>();
            String action                   = (String)request.getParameter("action");
            String tipo                   = (String)request.getParameter("tipo");
            Gson gson                       = new Gson();
            response.setContentType("application/json");

            /*
            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("PA");
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
            resultado.setDeptoId(action);
            */
            //Fetch Data from User Table
            int startPageIndex      = 0;
            int numRecordsPerPage   = 10;
            String jtSorting        = "run_empleado asc";
            
            if (request.getParameter("jtStartIndex") != null) 
                startPageIndex = Integer.parseInt(request.getParameter("jtStartIndex"));
            if (request.getParameter("jtPageSize") != null) 
                numRecordsPerPage   = Integer.parseInt(request.getParameter("jtPageSize"));
            if (request.getParameter("jtSorting") != null) 
                jtSorting   = request.getParameter("jtSorting");
            
            if (jtSorting.contains("runEmpleado")) jtSorting = jtSorting.replaceFirst("runEmpleado","run_empleado");
            else if (jtSorting.contains("nombreEmpleado")) jtSorting = jtSorting.replaceFirst("nombreEmpleado","nombre_empleado");
            
//            HashMap<String, Double> parametrosSistema = 
//                (HashMap<String, Double>)session.getAttribute("parametros_sistema");
//            
            System.out.println(WEB_NAME+"[PESPController]"
                + "empresaId: " + request.getParameter("empresaId")
                + ", empresaKey: " + request.getParameter("empresaKey")
                + ", rut empleado: " + request.getParameter("rutEmpleado"));
            
            if (action.compareTo("list") == 0) {
                try{
                    String paramCencoID = request.getParameter("cencoId");
                    String strAnio    = request.getParameter("paramAnio");
                    String paramEmpresa = null;
                    String paramDepto   = null;
                    String cencoId      = "";
                    
                    int paramAnio = -1;
                    if (strAnio.compareTo("") != 0) paramAnio = Integer.parseInt(strAnio);
                    
                    System.out.println(WEB_NAME+"[PESPController]"
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
                    System.out.println(WEB_NAME+"[PESPController]"
                        + "Listar resumen de Permisos Examen Salud Preventiva. "
                        + "empresa: " + paramEmpresa
                        + ", depto: " + paramDepto
                        + ", cenco: " + cencoId
                        + ", rut: " + request.getParameter("rutEmpleado")
                        + ", anio: " + paramAnio);
                    
                    int intCencoId=-1;
                    if (cencoId.compareTo("") != 0) intCencoId = Integer.parseInt(cencoId); 
                    
                    int objectsCount = 0;
                    if (intCencoId != -1){
                        if (tipo == null){
                            listaObjetos = permisosDao.getResumenPermisosExamenSaludPreventiva(paramEmpresa, 
                                request.getParameter("rutEmpleado"),
                                paramAnio,
                                intCencoId, 
                                startPageIndex, 
                                numRecordsPerPage, 
                                jtSorting);

                            //Get Total Record Count for Pagination
                            objectsCount = permisosDao.getResumenPermisosExamenSaludPreventivaCount(paramEmpresa, 
                                request.getParameter("rutEmpleado"),
                                paramAnio,
                                Integer.parseInt(cencoId));
                        }
                        
                        session.setAttribute("resumenPESP|"+userConnected.getUsername(), listaObjetos);
                    }
                    //Convert Java Object to Json
                    JsonElement element = gson.toJsonTree(listaObjetos,
                        new TypeToken<List<PermisoExamenSaludPreventivaVO>>() {}.getType());

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
            }
        }
    }
    
}
