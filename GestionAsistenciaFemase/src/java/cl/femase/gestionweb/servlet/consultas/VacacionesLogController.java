package cl.femase.gestionweb.servlet.consultas;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.VacacionesLogBp;
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
import java.util.StringTokenizer;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

public class VacacionesLogController extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 995L;
    
    public VacacionesLogController() {
        
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

        VacacionesLogBp vacacionesLogBp = new VacacionesLogBp(appProperties);
        
        if(request.getParameter("action") != null){
            System.out.println("[VacacionesLogController]"
                + "action is: " + request.getParameter("action"));
            List<VacacionesVO> listaObjetos = new ArrayList<>();
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            response.setContentType("application/json");

            //Fetch Data from User Table
            int startPageIndex      = 0;
            int numRecordsPerPage   = 10;
            String jtSorting        = "fecha_evento desc";
            
            if (request.getParameter("jtStartIndex") != null) 
                startPageIndex = Integer.parseInt(request.getParameter("jtStartIndex"));
            if (request.getParameter("jtPageSize") != null) 
                numRecordsPerPage   = Integer.parseInt(request.getParameter("jtPageSize"));
            if (request.getParameter("jtSorting") != null) 
                jtSorting   = request.getParameter("jtSorting");
            
            if (jtSorting.contains("rutEmpleado")) jtSorting = jtSorting.replaceFirst("rutEmpleado","rut_empleado");
            else if (jtSorting.contains("nombreEmpleado")) jtSorting = jtSorting.replaceFirst("nombreEmpleado","nombre");
            
            if (action.compareTo("list") == 0) {
                try{
                    String paramCencoID = request.getParameter("cencoId");
                    String paramStartDate   = request.getParameter("startDate");
                    String paramEndDate   = request.getParameter("endDate");
                    
                    String paramEmpresa = null;
                    String paramDepto   = null;
                    String cencoId      = "";
                    System.out.println("[VacacionesLogController]"
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
                    System.out.println("[VacacionesLogController]"
                        + "Listar info vacaciones. "
                        + "empresa: " + paramEmpresa
                        + ", depto: " + paramDepto
                        + ", cenco: " + cencoId
                        + ", rut: " + request.getParameter("rutEmpleado")
                        + ", startDate: " + paramStartDate
                        + ", endDate: " + paramEndDate);
                    
                    int intCencoId=-1;
                    if (cencoId.compareTo("") != 0) intCencoId = Integer.parseInt(cencoId); 
                    
                    int objectsCount = 0;
                    if (intCencoId != -1 && 
                            (request.getParameter("rutEmpleado") != null 
                            && request.getParameter("rutEmpleado").compareTo("-1") != 0)){
                        listaObjetos = vacacionesLogBp.getInfoVacacionesLog(paramEmpresa, 
                            request.getParameter("rutEmpleado"),
                            paramStartDate,
                            paramEndDate,
                            startPageIndex, 
                            numRecordsPerPage, 
                            jtSorting);

                        //Get Total Record Count for Pagination
                        objectsCount = vacacionesLogBp.getInfoVacacionesLogCount(paramEmpresa, 
                            request.getParameter("rutEmpleado"),
                            paramStartDate,
                            paramEndDate);
                        session.setAttribute("vacacioneslog|"+userConnected.getUsername(), listaObjetos);
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
                }catch(IOException | NumberFormatException ex){
                    String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getMessage()+"}";
                    response.getWriter().print(error);
                    ex.printStackTrace();
                }   
            }
        }
    }
}
