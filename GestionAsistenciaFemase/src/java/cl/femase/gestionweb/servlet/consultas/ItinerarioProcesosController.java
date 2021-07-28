package cl.femase.gestionweb.servlet.consultas;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.ProcesosBp;
import cl.femase.gestionweb.vo.ModuloSistemaVO;
import cl.femase.gestionweb.vo.ProcesoEjecucionVO;
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
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

public class ItinerarioProcesosController extends BaseServlet {

    /**
     *
     */
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 989L;
    
    public ItinerarioProcesosController() {
        
    }

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

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(true);
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        if (session!=null) session.removeAttribute("mensaje");else session = request.getSession();
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        ProcesosBp procesosBp=new ProcesosBp(appProperties);

        if(request.getParameter("action") != null){
            System.out.println("[ItinerarioProcesosController]action is: " + request.getParameter("action"));
            List<ProcesoEjecucionVO> lstItinerario = new ArrayList<>();
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            response.setContentType("application/json");

            //Fetch Data from User Table
            int startPageIndex      = 0;
            int numRecordsPerPage   = 10;
            String jtSorting        = "fecha_inicio_ejecucion desc";
            
            if (request.getParameter("jtStartIndex") != null) 
                startPageIndex = Integer.parseInt(request.getParameter("jtStartIndex"));
            if (request.getParameter("jtPageSize") != null) 
                numRecordsPerPage   = Integer.parseInt(request.getParameter("jtPageSize"));
            if (request.getParameter("jtSorting") != null) 
                jtSorting   = request.getParameter("jtSorting");
            
            /**
             * fechaHoraInicioEjecucion
             * fechaHoraFinEjecucion
             * usuario
             * deptoNombre
             * cencoNombre
             */
            if (jtSorting.contains("procesoId")) jtSorting = jtSorting.replaceFirst("procesoId","iti.proc_id");
            else if (jtSorting.contains("fechaHoraInicioEjecucion")) jtSorting = jtSorting.replaceFirst("fechaHoraInicioEjecucion","iti.fecha_inicio_ejecucion");
            else if (jtSorting.contains("fechaHoraFinEjecucion")) jtSorting = jtSorting.replaceFirst("fechaHoraFinEjecucion","iti.fecha_fin_ejecucion");
            else if (jtSorting.contains("usuario")) jtSorting = jtSorting.replaceFirst("usuario","iti.exec_user");
            else if (jtSorting.contains("deptoNombre")) jtSorting = jtSorting.replaceFirst("deptoNombre","depto.depto_nombre");
            else if (jtSorting.contains("cencoNombre")) jtSorting = jtSorting.replaceFirst("cencoNombre","cenco.ccosto_nombre");
            
            if (action.compareTo("list") == 0) {
                System.out.println("[ItinerarioProcesosController]"
                    + "mostrando itinerario: empresaId: "+request.getParameter("empresaId")
                    +", idProceso: " + request.getParameter("idProceso"));
                try{
                    int intProcesoId = -1;
                    int rowCount = 0;
                    if (request.getParameter("idProceso") != null){
                        intProcesoId = Integer.parseInt(request.getParameter("idProceso"));
                    }
                    if (request.getParameter("empresaId")!=null && 
                            request.getParameter("empresaId").compareTo("-1")!=0){
                        lstItinerario = procesosBp.getItinerario(request.getParameter("empresaId"),
                            intProcesoId,
                            startPageIndex, 
                            numRecordsPerPage, 
                            jtSorting);
                        //Get Total Record Count for Pagination
                        rowCount = procesosBp.getItinerarioCount(request.getParameter("empresaId"),
                            intProcesoId);
                    }
                    
                    //Convert Java Object to Json
                    JsonElement element = gson.toJsonTree(lstItinerario,
                        new TypeToken<List<ModuloSistemaVO>>() {}.getType());

                    JsonArray jsonArray = element.getAsJsonArray();
                    String listData=jsonArray.toString();
                        
                    //Return Json in the format required by jTable plugin
                    listData="{\"Result\":\"OK\",\"Records\":" + 
                            listData+",\"TotalRecordCount\": " + 
                            rowCount + "}";
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
