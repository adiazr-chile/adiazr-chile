package cl.femase.gestionweb.servlet.consultas;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.AsignacionTurnoBp;
import cl.femase.gestionweb.vo.AsignacionTurnoVO;
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
import java.util.StringTokenizer;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

public class AsignacionTurnosController extends BaseServlet {

    /**
     *
     */
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 989L;
    
    public AsignacionTurnosController() {
        
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        System.out.println("[AsignacionTurnosController.doGet]session: "+session);
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

    /**
    * 
    */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        System.out.println("[AsignacionTurnosController.doPost]session: "+session);
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

    /**
    * 
    * @param request
    * @param response
    * @throws ServletException
    * @throws IOException
    */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Set standard HTTP/1.1 no-cache headers.
        response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
        
        HttpSession session = request.getSession(false);
        System.out.println("[AsignacionTurnosController.processRequest]session: "+session);
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        if (session!=null) session.removeAttribute("mensaje");
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        AsignacionTurnoBp asignacionBp = new AsignacionTurnoBp(appProperties);
        
        if(request.getParameter("action") != null){
            System.out.println("[AsignacionTurnosController]action is: " + request.getParameter("action"));
            List<AsignacionTurnoVO> listaAsignaciones = new ArrayList<>();
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            response.setContentType("application/json");

            //Fetch Data from User Table
            int startPageIndex      = 0;
            int numRecordsPerPage   = 10;
            String jtSorting        = "fecha_desde desc";
            
            if (request.getParameter("jtStartIndex") != null) 
                startPageIndex = Integer.parseInt(request.getParameter("jtStartIndex"));
            if (request.getParameter("jtPageSize") != null) 
                numRecordsPerPage   = Integer.parseInt(request.getParameter("jtPageSize"));
            if (request.getParameter("jtSorting") != null) 
                jtSorting   = request.getParameter("jtSorting");
                       
            if (jtSorting.contains("empresaNombre")) jtSorting = jtSorting.replaceFirst("empresaNombre","ta.empresa_id");
            else if (jtSorting.contains("rutEmpleado")) jtSorting = jtSorting.replaceFirst("rutEmpleado","ta.rut_empleado");
            else if (jtSorting.contains("nombreEmpleado")) jtSorting = jtSorting.replaceFirst("nombreEmpleado","nombre");
            else if (jtSorting.contains("deptoId")) jtSorting = jtSorting.replaceFirst("deptoId","emp.depto_id");
            else if (jtSorting.contains("deptoNombre")) jtSorting = jtSorting.replaceFirst("deptoNombre","depto.depto_nombre");
            else if (jtSorting.contains("cencoNombre")) jtSorting = jtSorting.replaceFirst("cencoNombre","cenco.ccosto_nombre");
            else if (jtSorting.contains("nombreTurno")) jtSorting = jtSorting.replaceFirst("nombreTurno","cenco.ccosto_nombre");
            else if (jtSorting.contains("fechaDesde")) jtSorting = jtSorting.replaceFirst("fechaDesde","ta.fecha_desde");
            else if (jtSorting.contains("fechaHasta")) jtSorting = jtSorting.replaceFirst("fechaHasta","ta.fecha_hasta");
            else if (jtSorting.contains("fechaAsignacion")) jtSorting = jtSorting.replaceFirst("fechaAsignacion","ta.fecha_asignacion");
            else if (jtSorting.contains("username")) jtSorting = jtSorting.replaceFirst("username","ta.username");
            
            if (action.compareTo("list") == 0) {
                /**
                * En este parametro vendra 'deptoId|cencoId'
                */
                String empresaId= null;
                String deptoId  = null;
                String cencoId  = "-1";
                String paramCencoID         = request.getParameter("cencoId");
                System.out.println("[AsignacionTurnosController]"
                    + "token param 'cencoID'= " + paramCencoID);
                if (paramCencoID != null && paramCencoID.compareTo("-1") != 0){
                    StringTokenizer tokenCenco  = new StringTokenizer(paramCencoID, "|");
                    if (tokenCenco.countTokens() > 0){
                        while (tokenCenco.hasMoreTokens()){
                            empresaId   = tokenCenco.nextToken();
                            deptoId     = tokenCenco.nextToken();
                            cencoId     = tokenCenco.nextToken();
                        }
                    }
                }
                
                try{
                    int rowCount = 0;
                    int intTurnoId=-1;
                    // emp01|129
                    String paramTurno = request.getParameter("turno");
                    if (paramTurno != null && paramTurno.compareTo("-1") != 0){
                        StringTokenizer tokenTurno = new StringTokenizer(paramTurno, "|");
                        String kEmpresa = tokenTurno.nextToken();
                        String kTurnoId = tokenTurno.nextToken();
                        intTurnoId = Integer.parseInt(kTurnoId);
                    }
                    if (empresaId != null && 
                            empresaId.compareTo("-1") != 0){
                            
                        System.out.println("[AsignacionTurnosController]"
                            + "mostrando asignacion de turnos: "
                            + " empresaId: " + empresaId
                            + ", deptoId: " + deptoId    
                            + ", cencoId: " + cencoId        
                            + ", rut: " + request.getParameter("rutEmpleado")
                            + ", id turno normal: " + intTurnoId
                            + ", desde: " + request.getParameter("startDate")
                            + ", hasta: " + request.getParameter("endDate"));
                        
                        listaAsignaciones = asignacionBp.getAsignaciones(empresaId,
                            request.getParameter("rutEmpleado"),
                            intTurnoId,
                            request.getParameter("startDate"),
                            request.getParameter("endDate"),
                            Integer.parseInt(cencoId),
                            startPageIndex, 
                            numRecordsPerPage, 
                            jtSorting);
                        //Get Total Record Count for Pagination
                        rowCount = asignacionBp.getAsignacionesCount(empresaId,
                            request.getParameter("rutEmpleado"),
                            intTurnoId,
                            request.getParameter("startDate"),
                            request.getParameter("endDate"),
                            Integer.parseInt(cencoId));
                    }
                    
                    //Convert Java Object to Json
                    JsonElement element = gson.toJsonTree(listaAsignaciones,
                        new TypeToken<List<AsignacionTurnoVO>>() {}.getType());

                    JsonArray jsonArray = element.getAsJsonArray();
                    String listData=jsonArray.toString();
                    session.setAttribute("asignaciones|" + userConnected.getUsername(), listaAsignaciones);
                    
                    //Return Json in the format required by jTable plugin
                    listData="{\"Result\":\"OK\",\"Records\":" + 
                            listData+",\"TotalRecordCount\": " + 
                            rowCount + "}";
                    response.getWriter().print(listData);
                    
                }catch(IOException | NumberFormatException ex){
                    String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getMessage()+"}";
                    response.getWriter().print(error);
                    ex.printStackTrace();
                }   
            }
      }
    }
    
}
