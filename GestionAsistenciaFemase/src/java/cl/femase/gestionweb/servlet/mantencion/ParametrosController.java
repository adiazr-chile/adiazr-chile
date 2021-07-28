package cl.femase.gestionweb.servlet.mantencion;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.ParametroBp;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.ParametroVO;
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

public class ParametrosController extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 996L;
    
    public ParametrosController() {
        
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

    /**
    * 
     * @param request
     * @param response
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
    */
    @Override
    protected void doPost(HttpServletRequest request, 
            HttpServletResponse response) 
                throws ServletException, IOException {
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

        ParametroBp parametrosbp = new ParametroBp(appProperties);

        if(request.getParameter("action") != null){
            System.out.println("[ParametrosController]"
                + "action is: " + request.getParameter("action"));
            List<ParametroVO> listaObjetos = new ArrayList<ParametroVO>();
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            response.setContentType("application/json");

            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("PRM");
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
                   
            //Fetch Data from User Table
            int startPageIndex      = 0;
            int numRecordsPerPage   = 10;
            String jtSorting        = "param_code asc";
            /** filtros de busqueda */
            String empresaId = "";
            
            if (request.getParameter("jtStartIndex") != null) 
                startPageIndex = Integer.parseInt(request.getParameter("jtStartIndex"));
            if (request.getParameter("jtPageSize") != null) 
                numRecordsPerPage   = Integer.parseInt(request.getParameter("jtPageSize"));
            if (request.getParameter("jtSorting") != null) 
                jtSorting   = request.getParameter("jtSorting");
            
            //if (jtSorting.contains("code")) jtSorting = jtSorting.replaceFirst("code","param_code");
            System.out.println("[ParametrosController]"
                + "param busqueda "
                + "empresa_id: " + request.getParameter("paraEmpresaId")
                + ", empresa_id en objeto: " + request.getParameter("empresaId"));
            if (request.getParameter("empresaId") != null) 
                empresaId  = request.getParameter("empresaId");
            //objeto usado para update/insert
            ParametroVO parametro = new ParametroVO();
             
            if(request.getParameter("empresaId") != null){
                parametro.setEmpresaId(request.getParameter("empresaId"));
            }
            if(request.getParameter("paramCode")!=null){
                parametro.setParamCode(request.getParameter("paramCode"));
            }
            if(request.getParameter("paramLabel")!=null){
                parametro.setParamLabel(request.getParameter("paramLabel"));
            }
            if(request.getParameter("valor")!=null){
                parametro.setValor(Double.parseDouble(request.getParameter("valor")));
            }
            
            if (action.compareTo("list") == 0) {
                System.out.println("[ParametrosController]"
                    + "mostrando parametros para empresa_id= " + request.getParameter("empresaId"));
                try{
                    int objectsCount = 0;
                    if (request.getParameter("paraEmpresaId") != null 
                        && request.getParameter("paraEmpresaId").compareTo("-1") != 0){
                        listaObjetos = parametrosbp.getParametros(request.getParameter("paraEmpresaId"),
                            startPageIndex, 
                            numRecordsPerPage, 
                            jtSorting);
                        
                        //Get Total Record Count for Pagination
                        objectsCount = parametrosbp.getParametrosCount(empresaId);
                    }
                    //Convert Java Object to Json
                    JsonElement element = gson.toJsonTree(listaObjetos,
                        new TypeToken<List<ParametroVO>>() {}.getType());

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
                    System.out.println("[ParametrosController]"
                        + "Insertar Parametro. "
                        + "empresa_id: " + parametro.getEmpresaId()
                        + ", param_code: " + parametro.getParamCode()
                        + ", param_label: " + parametro.getParamLabel());
                    
                    MaintenanceVO doCreate = parametrosbp.insert(parametro, resultado);					
                    listaObjetos.add(parametro);

                    //Convert Java Object to Json
                    String json=gson.toJson(parametro);					
                    //Return Json in the format required by jTable plugin
                    String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";											
                    response.getWriter().print(listData);
            }else if (action.compareTo("update") == 0) {  
                    System.out.println("[ParametrosController]Modificar parametro...");
                    try{
                        MaintenanceVO doUpdate = parametrosbp.update(parametro, resultado);
                        listaObjetos.add(parametro);

                        //Convert Java Object to Json
                        String json=gson.toJson(parametro);					
                        //Return Json in the format required by jTable plugin
                        String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";											
                        response.getWriter().print(listData);
                        
                    }catch(Exception ex){
                        String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getStackTrace().toString()+"}";
                        response.getWriter().print(error);
                    }
            }
            
        }
    }
    
}
