package cl.femase.gestionweb.servlet.mantencion;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.CodigoErrorRechazoBp;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.CodigoErrorRechazoVO;
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

public class CodigoErrorRechazoController extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 999L;
    
    public CodigoErrorRechazoController() {
        
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

        CodigoErrorRechazoBp auxnegocio=new CodigoErrorRechazoBp(appProperties);

        if(request.getParameter("action") != null){
            System.out.println(WEB_NAME+"[CodigoErrorRechazoController]"
                + "action is: " + request.getParameter("action"));
            List<CodigoErrorRechazoVO> listaObjetos = new ArrayList<>();
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            response.setContentType("application/json");

            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("CER");
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
                   
            //Fetch Data from User Table
            int startPageIndex      = 0;
            int numRecordsPerPage   = 10;
            String jtSorting        = "descripcion asc";
            /** filtros de busqueda */
            String paramDescripcion = "";
            
            if (request.getParameter("jtStartIndex") != null) 
                startPageIndex = Integer.parseInt(request.getParameter("jtStartIndex"));
            if (request.getParameter("jtPageSize") != null) 
                numRecordsPerPage   = Integer.parseInt(request.getParameter("jtPageSize"));
            if (request.getParameter("jtSorting") != null) 
                jtSorting   = request.getParameter("jtSorting");
            System.out.println(WEB_NAME+"[jtSorting]jtSorting: "+jtSorting); 
            if (jtSorting.contains("codigo")) jtSorting = jtSorting.replaceFirst("codigo","cod_error_rechazo");
            else if (jtSorting.contains("descripcion")) jtSorting = jtSorting.replaceFirst("descripcion","descripcion_codigo_rechazo");
            
            if (request.getParameter("descripcion") != null) 
                paramDescripcion = request.getParameter("descripcion");
            
            //objeto usado para update/insert
            CodigoErrorRechazoVO auxdata = new CodigoErrorRechazoVO();
             
            if(request.getParameter("codigo") != null){
                auxdata.setCodigo(request.getParameter("codigo"));
            }
            if(request.getParameter("descripcion") != null){
                auxdata.setDescripcion(request.getParameter("descripcion"));
            }
            if (action.compareTo("list") == 0) {
                System.out.println(WEB_NAME+"[CodigoErrorRechazoController]"
                    + "mostrando cod error rechazos...");
                try{
                    listaObjetos = auxnegocio.getCodigos(paramDescripcion, 
                        startPageIndex, 
                        numRecordsPerPage, 
                        jtSorting);

                    //Get Total Record Count for Pagination
                    int rowsCount = auxnegocio.getCodigosCount(paramDescripcion);
                        
                    //Convert Java Object to Json
                    JsonElement element = gson.toJsonTree(listaObjetos,
                        new TypeToken<List<CodigoErrorRechazoVO>>() {}.getType());

                    JsonArray jsonArray = element.getAsJsonArray();
                    String listData=jsonArray.toString();

                    //Return Json in the format required by jTable plugin
                    listData="{\"Result\":\"OK\",\"Records\":" + 
                        listData+",\"TotalRecordCount\": " + 
                        rowsCount + "}";
                    response.getWriter().print(listData);
                }catch(Exception ex){
                    String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getMessage()+"}";
                    response.getWriter().print(error);
                    ex.printStackTrace();
                }   
            }else if (action.compareTo("create") == 0) {
                    System.out.println(WEB_NAME+"[CodigoErrorRechazoController]"
                        + "Insertar cod_error_rechazo...");
                    ResultCRUDVO doCreate = auxnegocio.insert(auxdata, resultado);					
                    listaObjetos.add(auxdata);

                    //Convert Java Object to Json
                    String jsonOkMessage = gson.toJson(auxdata);	
                                        
                    System.out.println(WEB_NAME+"[CodigoErrorRechazoController]"
                        + "Hay error: "+doCreate.isThereError());
                    
                    //Return Json in the format required by jTable plugin
                    if (doCreate.isThereError()){
                        //String errorMsg = "{"
                        //        + "\"Result\":\"ERROR\"" 
                        //        + ",\"Message\" : \"El codigo ingresado ya existe.\""
                        //    + "}";
                        //System.out.println(WEB_NAME+"[CodigoErrorRechazoController]"
                        //    + "Error msg: "+auxError);
                        String errorMsg="{\"Result\":\"ERROR\",\"Message\":\"El codigo ingresado ya existe\"}";
                        response.getWriter().print(errorMsg);
                    }else{
                        String listData="{\"Result\":\"OK\",\"Record\":"+jsonOkMessage+"}";
                        response.getWriter().print(listData);
                    }
//                    String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";											
//                    response.getWriter().print(listData);
            }else if (action.compareTo("update") == 0) {  
                    System.out.println(WEB_NAME+"[CodigoErrorRechazoController]"
                        + "Actualizar cod_error_rechazo...");
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
                    System.out.println(WEB_NAME+"[CodigoErrorRechazoController]"
                        + "Eliminar cod_error_rechazo...");
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
