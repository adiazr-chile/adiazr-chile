package cl.femase.gestionweb.servlet.mantencion.turnos;

import cl.femase.gestionweb.business.TurnoRotativoCicloBp;
import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.TurnoRotativoCicloVO;
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
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpSession;
import org.apache.log4j.Logger;

@WebServlet(name = "CiclosTurnosRotativosController", urlPatterns = {"/servlet/CiclosTurnosRotativosController"})
public class CiclosTurnosRotativosController extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 996L;
    
    public CiclosTurnosRotativosController() {
        
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

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(true);
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
        TurnoRotativoCicloBp auxnegocio=new TurnoRotativoCicloBp(appProperties);

        if(request.getParameter("action") != null){
            System.out.println(WEB_NAME+".CiclosTurnosRotativosController---->action is: " + request.getParameter("action"));
            List<TurnoRotativoCicloVO> listaObjetos = new ArrayList<TurnoRotativoCicloVO>();
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            response.setContentType("application/json");

            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("TRC");
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
                   
            //Fetch Data from User Table
            int startPageIndex      = 0;
            int numRecordsPerPage   = 10;
            String jtSorting        = "ciclo_num_dias asc";
            /** filtros de busqueda */
            String empresaId= userConnected.getEmpresaId();
            String etiqueta = "";
            int numDias     = -1;
            
            if (request.getParameter("jtStartIndex") != null) 
                startPageIndex = Integer.parseInt(request.getParameter("jtStartIndex"));
            if (request.getParameter("jtPageSize") != null) 
                numRecordsPerPage   = Integer.parseInt(request.getParameter("jtPageSize"));
            if (request.getParameter("jtSorting") != null) 
                jtSorting   = request.getParameter("jtSorting");
            
            if (jtSorting.contains("correlativo")) jtSorting = jtSorting.replaceFirst("correlativo","ciclo_correlativo");
            else if (jtSorting.contains("numDias")) jtSorting = jtSorting.replaceFirst("numDias","ciclo_num_dias");
            else if (jtSorting.contains("etiqueta")) jtSorting = jtSorting.replaceFirst("etiqueta","ciclo_etiqueta");
            
            if (request.getParameter("etiqueta") != null) 
                etiqueta  = request.getParameter("etiqueta");
            if (request.getParameter("numDias") != null) 
                numDias  = Integer.parseInt(request.getParameter("numDias"));
            //objeto usado para update/insert
            TurnoRotativoCicloVO auxdata = new TurnoRotativoCicloVO();
             
            if(request.getParameter("correlativo") != null){
                auxdata.setCorrelativo(Integer.parseInt(request.getParameter("correlativo")));
            }
            //if(request.getParameter("empresaId") != null){
                auxdata.setEmpresaId(empresaId);
            //}
            if(request.getParameter("numDias") != null){
                auxdata.setNumDias(Integer.parseInt(request.getParameter("numDias")));
            }
            if(request.getParameter("etiqueta") != null){
                auxdata.setEtiqueta(request.getParameter("etiqueta"));
            }
            
            if (action.compareTo("list") == 0) {
                System.out.println(WEB_NAME+"CRUD - Ciclos turnos rotativos - "
                    + "Listar registros...");
                try{
                    listaObjetos = auxnegocio.getCiclos(empresaId, numDias, etiqueta, 
                        startPageIndex, 
                        numRecordsPerPage, 
                        jtSorting);
                        
                    //Get Total Record Count for Pagination
                    int objectsCount = auxnegocio.getCiclosCount(empresaId, numDias, etiqueta);

                    //Convert Java Object to Json
                    JsonElement element = gson.toJsonTree(listaObjetos,
                        new TypeToken<List<TurnoRotativoCicloVO>>() {}.getType());

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
                    System.out.println(WEB_NAME+"CRUD - Ciclos turnos rotativos - Insertar...");
                    ResultCRUDVO doCreate = auxnegocio.insert(auxdata, resultado);					
                    listaObjetos.add(auxdata);

                    //Convert Java Object to Json
                    String json=gson.toJson(auxdata);					
                    //Return Json in the format required by jTable plugin
                    String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";											
                    response.getWriter().print(listData);
            }else if (action.compareTo("update") == 0) {  
                    System.out.println(WEB_NAME+"CRUD - Ciclos turnos rotativos - Modificar...");
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
            }else if (action.compareTo("delete") == 0) {  
                    System.out.println(WEB_NAME+"CRUD - Ciclos turnos rotativos - Eliminar...");
                    try{
                        ResultCRUDVO doUpdate = auxnegocio.delete(auxdata, resultado);
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
            
      }
    }
    
}
