package cl.femase.gestionweb.servlet.mantencion;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.DepartamentoBp;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.DepartamentoVO;
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

public class DepartamentosController extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 994L;
    
    public DepartamentosController() {
        
    }

    /**
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
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

    /**
     * 
     * @param request
     * @param response
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
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
        DepartamentoBp auxnegocio=new DepartamentoBp(appProperties);

        if(request.getParameter("action") != null){
            System.out.println("---->action is: " + request.getParameter("action"));
            List<DepartamentoVO> listaObjetos = new ArrayList<DepartamentoVO>();
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            response.setContentType("application/json");

            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("ADE");
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
                   
            //Fetch Data from User Table
            int startPageIndex      = 0;
            int numRecordsPerPage   = 10;
            String jtSorting        = "depto_nombre asc";
            /** filtros de busqueda */
            String nombre      = "";
            String empresaId   = "";
            
            if (request.getParameter("jtStartIndex") != null) 
                startPageIndex = Integer.parseInt(request.getParameter("jtStartIndex"));
            if (request.getParameter("jtPageSize") != null) 
                numRecordsPerPage   = Integer.parseInt(request.getParameter("jtPageSize"));
            if (request.getParameter("jtSorting") != null) 
                jtSorting   = request.getParameter("jtSorting");
            
            if (jtSorting.contains("id")) jtSorting = jtSorting.replaceFirst("id","depto_id");
            else if (jtSorting.contains("nombre")) jtSorting = jtSorting.replaceFirst("nombre","depto_nombre");
            else if (jtSorting.contains("empresaId")) jtSorting = jtSorting.replaceFirst("empresaId","empresa_id");
            else if (jtSorting.contains("estado")) jtSorting = jtSorting.replaceFirst("estado","estado_id");
            
            if (request.getParameter("nombre") != null) 
                nombre  = request.getParameter("nombre");
            if (request.getParameter("empresaId") != null) 
                empresaId = request.getParameter("empresaId");
            
            //objeto usado para update/insert
            DepartamentoVO auxdata = new DepartamentoVO();
             
            if(request.getParameter("id")!=null){
                auxdata.setId(request.getParameter("id"));
            }
            if(request.getParameter("nombre")!=null){
                auxdata.setNombre(request.getParameter("nombre"));
            }
            if (request.getParameter("empresaId") != null && 
                    request.getParameter("empresaId").compareTo("-1") != 0){
                auxdata.setEmpresaId(request.getParameter("empresaId"));
                session.setAttribute("empresaId", request.getParameter("empresaId"));
            }
            if(request.getParameter("estado")!=null){
                auxdata.setEstado(Integer.parseInt(request.getParameter("estado")));
            }
                        
            if (action.compareTo("list")==0) {
                System.out.println("Mantenedor - Departamentos - "
                    + "mostrando departamentos...");
                try{
                    int objectsCount = 0;
                    if (request.getParameter("empresaId") != null && 
                        request.getParameter("empresaId").compareTo("-1") != 0){
                            listaObjetos = auxnegocio.getDepartamentos(empresaId, 
                                nombre, 
                                startPageIndex, 
                                numRecordsPerPage, 
                                jtSorting);
                            //Get Total Record Count for Pagination
                            objectsCount = auxnegocio.getDepartamentosCount(empresaId,
                                nombre);
                    }    
                    
                    //Convert Java Object to Json
                    JsonElement element = gson.toJsonTree(listaObjetos,
                        new TypeToken<List<DepartamentoVO>>() {}.getType());

                    JsonArray jsonArray = element.getAsJsonArray();
                    String listData=jsonArray.toString();
                        
                    //Return Json in the format required by jTable plugin
                    listData="{\"Result\":\"OK\",\"Records\":" + 
                        listData+",\"TotalRecordCount\": " + 
                        objectsCount + "}";
                    response.getWriter().print(listData);
                    //request.getRequestDispatcher("/mantenedores/mantenedoresFrmSet.jsp").forward(request, response);
                }catch(IOException ex){
                    String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getMessage()+"}";
                    response.getWriter().print(error);
                    ex.printStackTrace();
                }   
            }else if (action.compareTo("create") == 0) {
                    System.out.println("Mantenedor - Departamentos - Insertar Depto...");
                    MaintenanceVO doCreate = auxnegocio.insert(auxdata, resultado);					
                    listaObjetos.add(auxdata);

                    //Convert Java Object to Json
                    String json=gson.toJson(auxdata);					
                    //Return Json in the format required by jTable plugin
                    String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";											
                    response.getWriter().print(listData);
            }else if (action.compareTo("update") == 0) {  
                    System.out.println("Mantenedor - Departamentos - Actualizar Depto...");
                    try{
                        MaintenanceVO doUpdate = auxnegocio.update(auxdata, resultado);
                        listaObjetos.add(auxdata);

                        //Convert Java Object to Json
                        String json=gson.toJson(auxdata);					
                        //Return Json in the format required by jTable plugin
                        String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";											
                        response.getWriter().print(listData);
                        
                    }catch(IOException ex){
                        String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getStackTrace().toString()+"}";
                        response.getWriter().print(error);
                    }
            }
            else if (action.compareTo("delete") == 0) {  
                    //Delete record
                    System.out.println("Eliminando Depto- "
                        + "Id: " + auxdata.getId()
                        +", nombre: "+ auxdata.getNombre());
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
