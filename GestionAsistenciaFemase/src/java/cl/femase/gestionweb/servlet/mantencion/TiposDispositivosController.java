package cl.femase.gestionweb.servlet.mantencion;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.TipoDispositivoBp;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.TipoDispositivoVO;
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

public class TiposDispositivosController extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 986L;
    
    public TiposDispositivosController() {
        
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

        TipoDispositivoBp auxnegocio=new TipoDispositivoBp(appProperties);

        if(request.getParameter("action") != null){
            System.out.println(WEB_NAME+"\n[TiposDispositivosController]"
                + "action is: " + request.getParameter("action"));
            List<TipoDispositivoVO> listaObjetos = new ArrayList<TipoDispositivoVO>();
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            response.setContentType("application/json");

            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("TDV");
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
            
            //Fetch Data from User Table
            int startPageIndex      = 0;
            int numRecordsPerPage   = 10;
            String jtSorting        = "dev_type_name asc";
            /** filtros de busqueda */
            int id = -1;
            String nombre = "";
            String descripcion = "";
            
            if (request.getParameter("jtStartIndex") != null) 
                startPageIndex = Integer.parseInt(request.getParameter("jtStartIndex"));
            if (request.getParameter("jtPageSize") != null) 
                numRecordsPerPage   = Integer.parseInt(request.getParameter("jtPageSize"));
            if (request.getParameter("jtSorting") != null) 
                jtSorting   = request.getParameter("jtSorting");
            
            if (jtSorting.contains("id")) jtSorting = jtSorting.replaceFirst("id","dev_type_id");
            if (jtSorting.contains("name")) jtSorting = jtSorting.replaceFirst("name","dev_type_name");
            if (jtSorting.contains("desc")) jtSorting = jtSorting.replaceFirst("desc","dev_type_desc");
            
            //objeto usado para update/insert
            TipoDispositivoVO auxdata = new TipoDispositivoVO();
            if (request.getParameter("id") != null) {
                id  = Integer.parseInt(request.getParameter("id"));
                auxdata.setId(id);
            }
            if (request.getParameter("name") != null){ 
                nombre = request.getParameter("name");
                auxdata.setName(nombre);
            }    
            if (request.getParameter("desc") != null){ 
                descripcion = request.getParameter("desc");
                auxdata.setDesc(descripcion);
            }
            
            if (action.compareTo("list")==0) {
                System.out.println(WEB_NAME+"[TiposDispositivosController]"
                    + "Mantenedor - Tipos Dispositivos - "
                    + "mostrando registros...");
                try{
                    listaObjetos = auxnegocio.getTipos(request.getParameter("filterName"),
                        startPageIndex, 
                        numRecordsPerPage, 
                        jtSorting);
                        
                    //Get Total Record Count for Pagination
                    int objectsCount = auxnegocio.getTiposCount(nombre);

                    //Convert Java Object to Json
                    JsonElement element = gson.toJsonTree(listaObjetos,
                        new TypeToken<List<TipoDispositivoVO>>() {}.getType());

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
            }else if (action.compareTo("create") == 0) {
                    System.out.println(WEB_NAME+"[TiposDispositivosController]"
                        + "Mantenedor - Tipos Dispositivos - Insertar...");
                    MaintenanceVO doCreate = auxnegocio.insert(auxdata, resultado);					
                    listaObjetos.add(auxdata);

                    //Convert Java Object to Json
                    String json=gson.toJson(auxdata);					
                    //Return Json in the format required by jTable plugin
                    String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";											
                    response.getWriter().print(listData);
            }else if (action.compareTo("update") == 0) {  
                    System.out.println(WEB_NAME+"[TiposDispositivosController]"
                        + "Mantenedor - Tipos Dispositivos - Actualizar...");
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
            }else if (action.compareTo("delete") == 0) {  
                    System.out.println(WEB_NAME+"[TiposDispositivosController]"
                        + "Mantenedor - Tipos Dispositivos - "
                        + "Eliminar tipo ID= "+auxdata.getId());
                    
                    try{
                        MaintenanceVO doUpdate = auxnegocio.delete(auxdata, resultado);
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
