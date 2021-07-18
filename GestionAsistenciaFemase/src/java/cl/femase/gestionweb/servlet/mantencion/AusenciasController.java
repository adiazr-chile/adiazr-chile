package cl.femase.gestionweb.servlet.mantencion;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.AusenciaBp;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.AusenciaVO;
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

public class AusenciasController extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 998L;
    
    public AusenciasController() {
        
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
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

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(true);
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        if (session!=null) session.removeAttribute("mensaje");
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        AusenciaBp auxnegocio=new AusenciaBp(appProperties);

        if(request.getParameter("action") != null){
            System.out.println("---->action is: " + request.getParameter("action"));
            List<AusenciaVO> listaObjetos = new ArrayList<>();
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            response.setContentType("application/json");

            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("AUS");
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
                   
            //Fetch Data from User Table
            int startPageIndex      = 0;
            int numRecordsPerPage   = 10;
            String jtSorting        = "ausencia_nombre asc";
            /** filtros de busqueda */
            String nombre = "";
            int tipo    = -1;
            int estado  = -1;
            String justificaHoras = "S";
            String pagadaPorEmpleador = "S";
            
            if (request.getParameter("jtStartIndex") != null) 
                startPageIndex = Integer.parseInt(request.getParameter("jtStartIndex"));
            if (request.getParameter("jtPageSize") != null) 
                numRecordsPerPage   = Integer.parseInt(request.getParameter("jtPageSize"));
            if (request.getParameter("jtSorting") != null) 
                jtSorting   = request.getParameter("jtSorting");
         
            if (jtSorting.contains("id")) jtSorting = jtSorting.replaceFirst("id","ausencia_id");
            else if (jtSorting.contains("nombre")) jtSorting = jtSorting.replaceFirst("nombre","ausencia_nombre");
            else if (jtSorting.contains("tipoId")) jtSorting = jtSorting.replaceFirst("tipoId","ausencia_tipo");
            else if (jtSorting.contains("estado")) jtSorting = jtSorting.replaceFirst("estado","ausencia_estado");    
            
            if (request.getParameter("paramNombre") != null) 
                nombre  = request.getParameter("paramNombre");
            if (request.getParameter("paramTipo") != null) 
                tipo  = Integer.parseInt(request.getParameter("paramTipo"));
            if (request.getParameter("paramEstado") != null) 
                estado  = Integer.parseInt(request.getParameter("paramEstado"));
            if (request.getParameter("paramJustificaHrs") != null) 
                justificaHoras  = request.getParameter("paramJustificaHrs");
            if (request.getParameter("paramPagadaPorEmpleador") != null) 
                pagadaPorEmpleador  = request.getParameter("paramPagadaPorEmpleador");
            
            //objeto usado para update/insert
            AusenciaVO auxdata = new AusenciaVO();
             
            if(request.getParameter("id")!=null){
                auxdata.setId(Integer.parseInt(request.getParameter("id")));
            }
            if(request.getParameter("nombre")!=null){
                auxdata.setNombre(request.getParameter("nombre"));
            }
            if(request.getParameter("nombreCorto")!=null){
                auxdata.setNombreCorto(request.getParameter("nombreCorto"));
            }
            if(request.getParameter("tipoId")!=null){
                auxdata.setTipoId(Integer.parseInt(request.getParameter("tipoId")));
            }
            if (request.getParameter("estado")!=null){
                auxdata.setEstado(Integer.parseInt(request.getParameter("estado")));
            }
            if (request.getParameter("justificaHoras") != null){
                auxdata.setJustificaHoras(request.getParameter("justificaHoras"));
            }
            if (request.getParameter("pagadaPorEmpleador") != null){
                auxdata.setPagadaPorEmpleador(request.getParameter("pagadaPorEmpleador"));
            }
            
            if (action.compareTo("list") == 0) {
                    System.out.println("Mantenedor - Ausencias - mostrando ausencias...");
                    try{
                        listaObjetos = auxnegocio.getAusencias(nombre,
                            tipo,
                            estado,
                            justificaHoras,
                            pagadaPorEmpleador,
                            startPageIndex, 
                            numRecordsPerPage, 
                            jtSorting);
                        
                        //Get Total Record Count for Pagination
                        int rowCount = auxnegocio.getAusenciasCount(
                                nombre,
                                tipo,
                                estado,
                                justificaHoras,
                                pagadaPorEmpleador);
                        
                        //Convert Java Object to Json
                        JsonElement element = gson.toJsonTree(listaObjetos,
                                new TypeToken<List<AusenciaVO>>() {}.getType());
                        
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
                        System.out.println("Mantenedor - Ausencias - Insertar ausencia...");
                        MaintenanceVO doCreate = auxnegocio.insert(auxdata, resultado);					
                        listaObjetos.add(auxdata);

                        //Convert Java Object to Json
                        String json=gson.toJson(auxdata);					
                        //Return Json in the format required by jTable plugin
                        String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";											
                        response.getWriter().print(listData);
            }else if (action.compareTo("update") == 0) {  
                    System.out.println("Mantenedor - Ausencias - Actualizar ausencia...");
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
           
      }
    }
    
}
