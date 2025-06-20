package cl.femase.gestionweb.servlet.mantencion;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.ModulosSistemaBp;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.ModuloSistemaVO;
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

public class ModulosSistemaController extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 989L;
    
    public ModulosSistemaController() {
        
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

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(true);
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        ModulosSistemaBp modulosBp=new ModulosSistemaBp(appProperties);

        if(request.getParameter("action") != null){
            System.out.println(WEB_NAME+"---->action is: " + request.getParameter("action"));
            List<ModuloSistemaVO> lstModulos = new ArrayList<ModuloSistemaVO>();
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            response.setContentType("application/json");

            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("MOD");
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());       
            
            //Fetch Data from User Table
            int startPageIndex      = 0;
            int numRecordsPerPage   = 10;
            String jtSorting        = "orden_despliegue asc";
            /** filtros de busqueda */
            String nombre      = "";
            
            if (request.getParameter("jtStartIndex") != null) 
                startPageIndex = Integer.parseInt(request.getParameter("jtStartIndex"));
            if (request.getParameter("jtPageSize") != null) 
                numRecordsPerPage   = Integer.parseInt(request.getParameter("jtPageSize"));
            if (request.getParameter("jtSorting") != null) 
                jtSorting   = request.getParameter("jtSorting");
            
            if (request.getParameter("nombre") != null) 
                nombre  = request.getParameter("nombre");
            
            //objeto usado para update/insert
            ModuloSistemaVO moduloobj = new ModuloSistemaVO();
             
            if(request.getParameter("modulo_id")!=null){
                moduloobj.setModulo_id(Integer.parseInt(request.getParameter("modulo_id")));
            }
            if(request.getParameter("modulo_nombre")!=null){
                moduloobj.setModulo_nombre(request.getParameter("modulo_nombre"));
            }
            if(request.getParameter("iconId")!=null){
                moduloobj.setIconId(request.getParameter("iconId"));
            }
            if(request.getParameter("estado_id")!=null){
                moduloobj.setEstado_id(Integer.parseInt(request.getParameter("estado_id")));
            }
            if(request.getParameter("orden_despliegue")!=null){
                moduloobj.setOrden_despliegue(Integer.parseInt(request.getParameter("orden_despliegue")));
            }
            //20200323
            if(request.getParameter("accesoRapido")!=null){
                moduloobj.setAccesoRapido(request.getParameter("accesoRapido"));
            }
            if(request.getParameter("imagen")!=null){
                moduloobj.setImagen(request.getParameter("imagen"));
            }
            if(request.getParameter("titulo")!=null){
                moduloobj.setTitulo(request.getParameter("titulo"));
            }
            if(request.getParameter("subTitulo")!=null){
                moduloobj.setSubTitulo(request.getParameter("subTitulo"));
            }
            
            if (action.compareTo("list")==0) {
                    System.out.println(WEB_NAME+"Mantenedor - Modulos - mostrando modulos sistema...");
                    try{
//                        System.out.println(WEB_NAME+"Ordenar por columna "+jtSorting);
                        lstModulos = modulosBp.getModulosSistema(nombre, 
                                startPageIndex, 
                                numRecordsPerPage, 
                                jtSorting);
                        
                        //Get Total Record Count for Pagination
                        int modulosCount = modulosBp.getModulosSistemaCount(nombre);
                        
                        //Convert Java Object to Json
                        JsonElement element = gson.toJsonTree(lstModulos,
                                new TypeToken<List<ModuloSistemaVO>>() {}.getType());
                        
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
                        System.out.println(WEB_NAME+"Mantenedor - Modulos - Insertar modulo...");
                        ResultCRUDVO doCreate = modulosBp.insert(moduloobj, resultado);					
                        lstModulos.add(moduloobj);

                        //Convert Java Object to Json
                        String json=gson.toJson(moduloobj);					
                        //Return Json in the format required by jTable plugin
                        String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";											
                        response.getWriter().print(listData);
            }else if (action.compareTo("update") == 0) {  
                    System.out.println(WEB_NAME+"Mantenedor - Modulos - Actualizar modulo sistema...");
                    try{
                        ResultCRUDVO doUpdate = modulosBp.update(moduloobj, resultado);
                        lstModulos.add(moduloobj);

                        //Convert Java Object to Json
                        String json=gson.toJson(moduloobj);					
                        //Return Json in the format required by jTable plugin
                        String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";											
                        response.getWriter().print(listData);
                        
                    }catch(IOException ex){
                        String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getStackTrace().toString()+"}";
                        response.getWriter().print(error);
                    }
            }
            /*else if (action.compareTo("delete") == 0) {  
                    //Delete record
                    System.out.println(WEB_NAME+"Eliminando conversion de contrato- "
                            + "symbol: " + contractRelation.getSymbol()
                            +", base: "+ contractRelation.getBase());
                    try{
                        crelationBp.delete(contractRelation, resultado);
                        String listData="{\"Result\":\"OK\"}";								
                        response.getWriter().print(listData);
                        
                    }catch(Exception ex){
                        String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getStackTrace().toString()+"}";
                        response.getWriter().print(error);
                    }
            }*/
      }
    }
    
}
