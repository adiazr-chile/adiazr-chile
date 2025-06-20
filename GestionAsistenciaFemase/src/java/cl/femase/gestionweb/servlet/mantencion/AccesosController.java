package cl.femase.gestionweb.servlet.mantencion;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.AccesoBp;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.AccesoVO;
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
import java.util.Date;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import org.apache.log4j.Logger;

public class AccesosController extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 999L;
    
    public AccesosController() {
        
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

        AccesoBp auxnegocio=new AccesoBp(appProperties);

        if(request.getParameter("action") != null){
            System.out.println(WEB_NAME+"---->action is: " + request.getParameter("action"));
            List<AccesoVO> listaObjetos = new ArrayList<AccesoVO>();
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            response.setContentType("application/json");

            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("ACC");
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
                   
            //Fetch Data from User Table
            int startPageIndex      = 0;
            int numRecordsPerPage   = 10;
            String jtSorting        = "acceso_label asc";
            /** filtros de busqueda */
            String label      = "";
            
            if (request.getParameter("jtStartIndex") != null) 
                startPageIndex = Integer.parseInt(request.getParameter("jtStartIndex"));
            if (request.getParameter("jtPageSize") != null) 
                numRecordsPerPage   = Integer.parseInt(request.getParameter("jtPageSize"));
            if (request.getParameter("jtSorting") != null) 
                jtSorting   = request.getParameter("jtSorting");
            
            if (jtSorting.contains("id")) jtSorting = jtSorting.replaceFirst("id","acceso_id");
            if (jtSorting.contains("nombre")) jtSorting = jtSorting.replaceFirst("nombre","acceso_nombre");
            if (jtSorting.contains("label")) jtSorting = jtSorting.replaceFirst("label","acceso_label");
            if (jtSorting.contains("url")) jtSorting = jtSorting.replaceFirst("url","acceso_url");    
            
            if (request.getParameter("label") != null) 
                label  = request.getParameter("label");
            
            //objeto usado para update/insert
            AccesoVO auxdata = new AccesoVO();
             
            if(request.getParameter("id")!=null){
                auxdata.setId(Integer.parseInt(request.getParameter("id")));
            }
            if(request.getParameter("nombre")!=null){
                auxdata.setNombre(request.getParameter("nombre"));
            }
            if(request.getParameter("label")!=null){
                auxdata.setLabel(request.getParameter("label"));
            }
            if(request.getParameter("url")!=null){
                auxdata.setUrl(request.getParameter("url"));
            }
            
            if (action.compareTo("list")==0) {
                    System.out.println(WEB_NAME+"Mantenedor - Accesos - mostrando accesos...");
                    try{
//                        System.out.println(WEB_NAME+"Ordenar por columna "+jtSorting);
                        listaObjetos = auxnegocio.getAccesos(label, 
                                startPageIndex, 
                                numRecordsPerPage, 
                                jtSorting);
                        
                        //Get Total Record Count for Pagination
                        int modulosCount = auxnegocio.getAccesosCount(label);
                        
                        //Convert Java Object to Json
                        JsonElement element = gson.toJsonTree(listaObjetos,
                                new TypeToken<List<AccesoVO>>() {}.getType());
                        
                        JsonArray jsonArray = element.getAsJsonArray();
                        String listData=jsonArray.toString();
                        
                        //Return Json in the format required by jTable plugin
                        listData="{\"Result\":\"OK\",\"Records\":" + 
                                listData+",\"TotalRecordCount\": " + 
                                modulosCount + "}";
                        response.getWriter().print(listData);
                        //request.getRequestDispatcher("/mantenedores/mantenedoresFrmSet.jsp").forward(request, response);
                    }catch(Exception ex){
                        String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getMessage()+"}";
                        response.getWriter().print(error);
                        ex.printStackTrace();
                    }   
            }else if (action.compareTo("create") == 0) {
                        System.out.println(WEB_NAME+"Mantenedor - Accesos - Insertar acceso...");
                        ResultCRUDVO doCreate = auxnegocio.insert(auxdata, resultado);					
                        listaObjetos.add(auxdata);

                        //Convert Java Object to Json
                        String json=gson.toJson(auxdata);					
                        //Return Json in the format required by jTable plugin
                        String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";											
                        response.getWriter().print(listData);
            }else if (action.compareTo("update") == 0) {  
                    System.out.println(WEB_NAME+"Mantenedor - Modulos - Actualizar modulo sistema...");
                    try{
                        ResultCRUDVO doUpdate = auxnegocio.update(auxdata, resultado);
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
            }
            else if (action.compareTo("delete") == 0) {  
                    //Delete record
                    System.out.println(WEB_NAME+"Eliminando accion- "
                            + "Id: " + auxdata.getId()
                            +", label: "+ auxdata.getLabel());
                    try{
                        auxnegocio.delete(auxdata, resultado);
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
