package cl.femase.gestionweb.servlet.consultas;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.MaintenanceEventsBp;
import cl.femase.gestionweb.vo.EventoMantencionVO;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

public class EventosMantencionController extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 1L;
    
    public EventosMantencionController() {
        
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

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set standard HTTP/1.1 no-cache headers.
        response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
        
        HttpSession session = request.getSession(true);
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        if (session!=null) session.removeAttribute("mensaje");else session = request.getSession();
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        MaintenanceEventsBp auxnegocio=new MaintenanceEventsBp(appProperties);

        if(request.getParameter("action") != null){
            System.out.println(WEB_NAME+"\n[TiposDispositivosController]"
                + "action is: " + request.getParameter("action"));
            List<EventoMantencionVO> listaObjetos = new ArrayList<>();
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            response.setContentType("application/json");

//            MaintenanceEventVO resultado=new MaintenanceEventVO();
//            resultado.setUsername(userConnected.getUsername());
//            resultado.setDatetime(new Date());
//            resultado.setUserIP(request.getRemoteAddr());
//            resultado.setType("TDV");
                   
            //Fetch Data from User Table
            int startPageIndex      = 0;
            int numRecordsPerPage   = 10;
            String jtSorting        = "fecha_hora DESC";
            /** filtros de busqueda */
            String username = "";
            String tipoEvento = "";
            String startDate = "";
            String endDate = "";
            
            if (request.getParameter("jtStartIndex") != null) 
                startPageIndex = Integer.parseInt(request.getParameter("jtStartIndex"));
            if (request.getParameter("jtPageSize") != null) 
                numRecordsPerPage   = Integer.parseInt(request.getParameter("jtPageSize"));
            if (request.getParameter("jtSorting") != null) 
                jtSorting   = request.getParameter("jtSorting");
            
            System.out.println(WEB_NAME+"cl.femase.gestionweb.servlet."
                + "consultas.EventosMantencionController."
                + "processRequest(). jtSorting(1)= "+jtSorting);
            
            if (jtSorting.contains("descripcion")) jtSorting = jtSorting.replaceFirst("descripcion","descripcion_evento");
            else if (jtSorting.contains("ip")) jtSorting = jtSorting.replaceFirst("ip","ip_usuario");
            else if (jtSorting.contains("tipoEventoId")) jtSorting = jtSorting.replaceFirst("tipoEventoId","nombre_evento");
            else if (jtSorting.contains("fechaHoraAsStr")) jtSorting = jtSorting.replaceFirst("fechaHoraAsStr","fecha_hora");
            else if (jtSorting.contains("empresaNombre")) jtSorting = jtSorting.replaceFirst("empresaNombre","empresa.empresa_nombre");
            else if (jtSorting.contains("deptoNombre")) jtSorting = jtSorting.replaceFirst("deptoNombre","departamento.depto_nombre");
            else if (jtSorting.contains("cencoNombre")) jtSorting = jtSorting.replaceFirst("cencoNombre","centro_costo.ccosto_nombre");
            if (action.compareTo("list")==0) {
                
                try{
                    username    = request.getParameter("username");
                    tipoEvento  = request.getParameter("tipo_evento");
                    startDate   = request.getParameter("fecha_inicio");
                    endDate     = request.getParameter("fecha_fin");
                    
                    Calendar nowCal=Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    
                    System.out.println(WEB_NAME+"[EventosMantencionController]"
                        + "mostrando registros (1)."
                        + "username: "+username
                        + ", tipoEvento: "+tipoEvento
                        + ", startDate: "+startDate
                        + ", endDate: "+endDate);
                    
                    if (startDate == null || startDate.compareTo("") == 0 || startDate.compareTo("null") == 0) {
                        System.out.println(WEB_NAME+"seteo  fecha actual");
                        startDate = sdf.format(nowCal.getTime());
                        endDate = startDate;
                    }
                                        
                    System.out.println(WEB_NAME+"[EventosMantencionController]"
                        + "mostrando registros (2)."
                        + "username: "+username
                        + ", tipoEvento: "+tipoEvento
                        + ", startDate: "+startDate
                        + ", endDate: "+endDate);
                    
                    listaObjetos = auxnegocio.getEventosMantencion(username,
                        tipoEvento,
                        startDate,
                        endDate,
                        startPageIndex, 
                        numRecordsPerPage, 
                        jtSorting);
                        
                    //Get Total Record Count for Pagination
                    int objectsCount = auxnegocio.getEventosMantencionCount(username,
                        tipoEvento,
                        startDate,
                        endDate);

                    //Convert Java Object to Json
                    JsonElement element = gson.toJsonTree(listaObjetos,
                        new TypeToken<List<EventoMantencionVO>>() {}.getType());

                    JsonArray jsonArray = element.getAsJsonArray();
                    String listData=jsonArray.toString();
                        
                    //Return Json in the format required by jTable plugin
                    listData="{\"Result\":\"OK\",\"Records\":" + 
                        listData+",\"TotalRecordCount\": " + 
                        objectsCount + "}";
                    session.setAttribute("eventos|"+userConnected.getUsername(), listaObjetos);
                    request.setAttribute("startDate", startDate);
                    request.setAttribute("endDate", endDate);
                    
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
