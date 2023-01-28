package cl.femase.gestionweb.servlet.mantencion;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.dao.MaintenanceEventsDAO;
import cl.femase.gestionweb.dao.ProveedorCorreoDAO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.ProveedorCorreoVO;
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
import java.util.Date;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

public class ProveedorCorreoController extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 999L;
    
    public ProveedorCorreoController() {
        
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
    * @param request
    * @param response
    * @throws javax.servlet.ServletException
    * @throws java.io.IOException
    */
    @Override
    protected void processRequest(HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(true);
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        ProveedorCorreoDAO dao = new ProveedorCorreoDAO();
        MaintenanceEventsDAO daoEventsService = new MaintenanceEventsDAO(appProperties);
        
        if(request.getParameter("action") != null){
            System.out.println(WEB_NAME+"[ProveedorCorreoController.processRequest]"
                + "action is: " + request.getParameter("action"));
            List<ProveedorCorreoVO> listaObjetos = new ArrayList<ProveedorCorreoVO>();
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            response.setContentType("application/json");

            MaintenanceEventVO detailEvent = new MaintenanceEventVO();
            detailEvent.setUsername(userConnected.getUsername());
            detailEvent.setDatetime(new Date());
            detailEvent.setUserIP(request.getRemoteAddr());
            detailEvent.setType("MAI");
            detailEvent.setEmpresaIdSource(userConnected.getEmpresaId());
                   
            //Fetch Data from User Table
            int startPageIndex      = 0;
            int numRecordsPerPage   = 10;
            String jtSorting        = "provider_domain asc";
            
            if (request.getParameter("jtStartIndex") != null) 
                startPageIndex = Integer.parseInt(request.getParameter("jtStartIndex"));
            if (request.getParameter("jtPageSize") != null) 
                numRecordsPerPage   = Integer.parseInt(request.getParameter("jtPageSize"));
            if (request.getParameter("jtSorting") != null) 
                jtSorting   = request.getParameter("jtSorting");
            
            if (jtSorting.contains("code")) jtSorting = jtSorting.replaceFirst("code","provider_code");
            else if (jtSorting.contains("name")) jtSorting = jtSorting.replaceFirst("name","provider_name");
            else if (jtSorting.contains("domain")) jtSorting = jtSorting.replaceFirst("domain","provider_domain");
            
            //objeto usado para update/insert
            ProveedorCorreoVO proveedor = new ProveedorCorreoVO();
             
            if(request.getParameter("code") != null){
                proveedor.setCode(request.getParameter("code"));
            }
            if(request.getParameter("name") != null){
                proveedor.setName(request.getParameter("name"));
            }
            if(request.getParameter("domain")!=null){
                proveedor.setDomain(request.getParameter("domain"));
            }
            
            if (action.compareTo("list") == 0) {
                    System.out.println(WEB_NAME+"[ProveedorCorreoController."
                        + "processRequest]Listar proveedores "
                        + "de correo, nombre: " + request.getParameter("param_name"));
                    try{
//                        System.out.println(WEB_NAME+"Ordenar por columna "+jtSorting);
                        listaObjetos = dao.getProveedores(request.getParameter("param_name"), 
                                startPageIndex, 
                                numRecordsPerPage, 
                                jtSorting);
                        
                        //Get Total Record Count for Pagination
                        int rowCount = dao.getProveedoresCount(request.getParameter("param_name"));
                        
                        //Convert Java Object to Json
                        JsonElement element = gson.toJsonTree(listaObjetos,
                            new TypeToken<List<ProveedorCorreoVO>>() {}.getType());
                        
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
            }else if (action.compareTo("create") == 0) {
                        System.out.println(WEB_NAME+"[ProveedorCorreoController."
                            + "processRequest]Insertar proveedor de correo...");
                        ResultCRUDVO doCreate = dao.insert(proveedor);
                        
                        String msgFinal = doCreate.getMsg();
                        doCreate.setMsg(msgFinal);
                        detailEvent.setDescription(msgFinal);
                        //insertar evento 
                        daoEventsService.addEvent(detailEvent); 
                        listaObjetos.add(proveedor);

                        //Convert Java Object to Json
                        String json=gson.toJson(proveedor);					
                        //Return Json in the format required by jTable plugin
                        String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";											
                        response.getWriter().print(listData);
            }else if (action.compareTo("update") == 0) {  
                        String key = proveedor.getCode();
                        System.out.println(WEB_NAME+"[ProveedorCorreoController."
                            + "processRequest]"
                            + "Modificar proveedor de correo, code = " + key);
                        ResultCRUDVO doUpdate = dao.update(proveedor);
                        
                        proveedor = dao.getProveedorByCode(key);
                        
                        String msgFinal = doUpdate.getMsg();
                        doUpdate.setMsg(msgFinal);
                        detailEvent.setDescription(msgFinal);
                        //insertar evento 
                        daoEventsService.addEvent(detailEvent); 
                        listaObjetos.add(proveedor);

                        //Convert Java Object to Json
                        String json=gson.toJson(proveedor);					
                        //Return Json in the format required by jTable plugin
                        String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";											
                        response.getWriter().print(listData);
            }
            else if (action.compareTo("delete") == 0) {  
                    //Delete record
                    System.out.println(WEB_NAME+"[ProveedorCorreoController."
                        + "processRequest]Eliminar proveedor de correo, "
                        + "code: " + proveedor.getCode());
                    try{
                        ResultCRUDVO doDelete = dao.delete(proveedor.getCode());
                        
                        String msgFinal = doDelete.getMsg();
                        doDelete.setMsg(msgFinal);
                        detailEvent.setDescription(msgFinal);
                        //insertar evento 
                        daoEventsService.addEvent(detailEvent); 
                        
                        String listData="{\"Result\":\"OK\"}";								
                        response.getWriter().print(listData);
                        
                    }catch(Exception ex){
                        String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getStackTrace().toString()+"}";
                        response.getWriter().print(error);
                    }
            }
      }
    }
    
}
