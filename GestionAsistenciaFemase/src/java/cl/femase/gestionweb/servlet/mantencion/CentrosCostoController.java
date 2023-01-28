package cl.femase.gestionweb.servlet.mantencion;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.AsignacionDispositivoBp;
import cl.femase.gestionweb.business.CentroCostoBp;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.CentroCostoVO;
import cl.femase.gestionweb.vo.DispositivoCentroCostoVO;
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

/**
 *
 * @author Alexander
 */
public class CentrosCostoController extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 995L;
    
    public CentrosCostoController() {
        
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
        CentroCostoBp auxnegocio = 
            new CentroCostoBp(appProperties);
        AsignacionDispositivoBp asignacionBp = 
            new AsignacionDispositivoBp(appProperties);
        
        if(request.getParameter("action") != null){
            System.out.println(WEB_NAME+"---->action is: " + request.getParameter("action"));
            List<CentroCostoVO> listaObjetos = new ArrayList<CentroCostoVO>();
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            response.setContentType("application/json");

            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("ACO");
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
                   
            //Fetch Data from User Table
            int startPageIndex      = 0;
            int numRecordsPerPage   = 10;
            String jtSorting        = "ccosto_nombre asc";
            /** filtros de busqueda */
            String deptoId     = "";
            String nombre      = "";
            String empresaId   = "";
            int estado=-1;
            
            if (request.getParameter("jtStartIndex") != null) 
                startPageIndex = Integer.parseInt(request.getParameter("jtStartIndex"));
            if (request.getParameter("jtPageSize") != null) 
                numRecordsPerPage   = Integer.parseInt(request.getParameter("jtPageSize"));
            if (request.getParameter("jtSorting") != null) 
                jtSorting   = request.getParameter("jtSorting");
            
            if (jtSorting.contains("id")) jtSorting = jtSorting.replaceFirst("id","ccosto_id");
            else if (jtSorting.contains("nombre")) jtSorting = jtSorting.replaceFirst("nombre","ccosto_nombre");
            else if (jtSorting.contains("estado")) jtSorting = jtSorting.replaceFirst("estado","estado_id");
            else if (jtSorting.contains("comunaId")) jtSorting = jtSorting.replaceFirst("comunaId","id_comuna");
            else if (jtSorting.contains("empresaId")) jtSorting = jtSorting.replaceFirst("empresaId","depto.empresa_id");
            else if (jtSorting.contains("deptoId")) jtSorting = jtSorting.replaceFirst("deptoId","cc.depto_id");
            else if (jtSorting.contains("regionId")) jtSorting = jtSorting.replaceFirst("regionId","comuna.region_id");
                        
            System.out.println(WEB_NAME+"[cl.femase.gestionweb.servlet."
                    + "mantencion.CentrosCostoController.processRequest]"
                    + "empresaId = " +request.getParameter("empresaId"));
            if (request.getParameter("empresaId") != null && 
                    request.getParameter("empresaId").compareTo("-1") != 0){ 
                session.setAttribute("empresaId", request.getParameter("empresaId"));
            }
            if (request.getParameter("deptoId") != null) 
                deptoId  = request.getParameter("deptoId");
            if (request.getParameter("nombre") != null) 
                nombre  = request.getParameter("nombre");
            if (request.getParameter("estado") != null) 
                estado  = Integer.parseInt(request.getParameter("estado"));
            //objeto usado para update/insert
            CentroCostoVO auxdata = new CentroCostoVO();
             
            if(request.getParameter("id") != null){
                auxdata.setId(Integer.parseInt(request.getParameter("id")));
            }
            if(request.getParameter("deptoId") != null){
                auxdata.setDeptoId(request.getParameter("deptoId"));
            }
            if(request.getParameter("nombre") != null){
                auxdata.setNombre(request.getParameter("nombre"));
            }
            if(request.getParameter("estado") != null){
                auxdata.setEstado(Integer.parseInt(request.getParameter("estado")));
            }
            if(request.getParameter("direccion") != null){
                auxdata.setDireccion(request.getParameter("direccion"));
            }
            if(request.getParameter("comunaId") != null){
                auxdata.setComunaId(Integer.parseInt(request.getParameter("comunaId")));
            }
            if(request.getParameter("telefonos") != null){
                auxdata.setTelefonos(request.getParameter("telefonos"));
            }
            if(request.getParameter("email") != null){
                auxdata.setEmail(request.getParameter("email"));
            }
            if(request.getParameter("emailNotificacion") != null){
                auxdata.setEmailNotificacion(request.getParameter("emailNotificacion"));
            }
            if(request.getParameter("deptoId") != null){
                auxdata.setDeptoId(request.getParameter("deptoId"));
            }
            if(request.getParameter("zonaExtrema") != null){
                auxdata.setZonaExtrema(request.getParameter("zonaExtrema"));
            }
            
            String[] dispositivos = request.getParameterValues("dispositivo");
            if (dispositivos != null){
                for (int x = 0; x < dispositivos.length; x++){
                    System.out.println(WEB_NAME+"dispositivo seleccionado[" + x + "] = " + dispositivos[x]);
                    DispositivoCentroCostoVO newCenco = 
                        new DispositivoCentroCostoVO(dispositivos[x],auxdata.getId());
                    asignacionBp.insertAsignacionCentroCosto(newCenco, resultado);
                }
            }
            
            if (action.compareTo("list") == 0) {
                System.out.println(WEB_NAME+"[servlet.CentrosCostoController]"
                    + "Mantenedor - Centro de Costo - "
                    + "Listar centros de costo...");
                try{
                    int objectsCount = 0;
                    if (request.getParameter("empresaId") != null && 
                            request.getParameter("empresaId").compareTo("-1") != 0){
                        listaObjetos = auxnegocio.getCentrosCosto(deptoId, nombre, estado,
                            startPageIndex, 
                            numRecordsPerPage, 
                            jtSorting);
                        //Get Total Record Count for Pagination
                        objectsCount = auxnegocio.getCentrosCostoCount(deptoId, nombre,estado);
                    }    
                    
                    //Convert Java Object to Json
                    JsonElement element = gson.toJsonTree(listaObjetos,
                        new TypeToken<List<CentroCostoVO>>() {}.getType());

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
                    System.out.println(WEB_NAME+"[servlet.CentrosCostoController]Insertar...");
                    ResultCRUDVO doCreate = auxnegocio.insert(auxdata, resultado);					
                    listaObjetos.add(auxdata);

                    //Convert Java Object to Json
                    String json=gson.toJson(auxdata);					
                    //Return Json in the format required by jTable plugin
                    String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";											
                    response.getWriter().print(listData);
            }else if (action.compareTo("update") == 0) {  
                    System.out.println(WEB_NAME+"[servlet.CentrosCostoController]Actualizar...");
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
            
      }
    }
    
}
