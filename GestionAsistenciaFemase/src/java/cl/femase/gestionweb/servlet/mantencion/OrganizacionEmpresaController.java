package cl.femase.gestionweb.servlet.mantencion;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.OrganizacionEmpresaBp;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.OrganizacionEmpresaVO;
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
import java.util.Enumeration;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

/**
 * @Deprecated 
 */
public class OrganizacionEmpresaController extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 988L;
    
    /**
     *
     */
    public OrganizacionEmpresaController() {
        
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

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(true);
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        if (session!=null) session.removeAttribute("mensaje");else session = request.getSession();
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        OrganizacionEmpresaBp auxnegocio=new OrganizacionEmpresaBp(appProperties);

        if(request.getParameter("action") != null){
            System.out.println(WEB_NAME+"\n[OrganizacionEmpresaController]action is: " + request.getParameter("action"));
            List<OrganizacionEmpresaVO> listaObjetos = new ArrayList<OrganizacionEmpresaVO>();
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            response.setContentType("application/json");

            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("OEM");
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
                   
            //Fetch Data from User Table
            int startPageIndex      = 0;
            int numRecordsPerPage   = 10;
            String jtSorting        = "depto_nombre asc";
            /** filtros de busqueda */
            String filtroEmpresa    = "";
            String filtroDepto      = "";
            int filtroCenco         = -1;
            
            if (request.getParameter("jtStartIndex") != null) 
                startPageIndex = Integer.parseInt(request.getParameter("jtStartIndex"));
            if (request.getParameter("jtPageSize") != null) 
                numRecordsPerPage   = Integer.parseInt(request.getParameter("jtPageSize"));
            if (request.getParameter("jtSorting") != null) 
                jtSorting   = request.getParameter("jtSorting");
            
            if (jtSorting.contains("empresaId")) jtSorting = jtSorting.replaceFirst("empresaId","empresa_id");
            if (jtSorting.contains("deptoId")) jtSorting = jtSorting.replaceFirst("deptoId","depto_id");
            if (jtSorting.contains("cencoId")) jtSorting = jtSorting.replaceFirst("cencoId","ccosto_id");
            
             System.out.println(WEB_NAME+"Mantenedor - Organizacion - "
                + "params para insert-update-delete. "
                + "empresaId: "+request.getParameter("empresaId")
                + ",deptoId: "+request.getParameter("deptoId")
                + ",cencoId: "+request.getParameter("cencoId"));
            
            //objeto usado para operaciones de insert/update/delete
            OrganizacionEmpresaVO auxdata = new OrganizacionEmpresaVO();
            if (request.getParameter("empresaId") != null){ 
                auxdata.setEmpresaId(request.getParameter("empresaId"));
            }
            if (request.getParameter("deptoId") != null){ 
                auxdata.setDeptoId(request.getParameter("deptoId"));
            }
            if (request.getParameter("cencoId") != null && request.getParameter("cencoId").compareTo("-1")!=0 ) {
                auxdata.setCencoId(Integer.parseInt(request.getParameter("cencoId")));
            }
                    
            System.out.println(WEB_NAME+"Mantenedor - Organizacion - "
                + "params para buscar. "
                + "filtroEmpresa: "+request.getParameter("filtroEmpresa")
                + ",filtroDepto: "+request.getParameter("filtroDepto")
                + ",filtroCenco: "+request.getParameter("filtroCenco"));
            System.out.println(WEB_NAME+"Mantenedor - Organizacion - "
                + "params para insert/update/delete. "
                + "idEmpresa: "+auxdata.getEmpresaId()
                + ",idDepto: "+auxdata.getDeptoId()
                + ",idCenco: "+auxdata.getCencoId());
            
            Enumeration<String> names = request.getParameterNames(); 
            while(names.hasMoreElements()) {
                String name = names.nextElement();
                String value = request.getParameter(name);
                System.out.println(WEB_NAME+"Param:"+name+" = "+value);
                
            } 
             
            if (action.compareTo("list")==0) {
                System.out.println(WEB_NAME+"Mantenedor - Organizacion - "
                    + "mostrando organizacion empresa...");
                try{
                    listaObjetos = auxnegocio.getOrganizacion(request.getParameter("filtroEmpresa"), 
                            request.getParameter("filtroDepto"), 
                            Integer.parseInt(request.getParameter("filtroCenco")), 
                        startPageIndex, 
                        numRecordsPerPage, 
                        jtSorting);
                        
                    //Get Total Record Count for Pagination
                    int objectsCount = auxnegocio.getOrganizacionCount(request.getParameter("filtroEmpresa"), 
                            request.getParameter("filtroDepto"), 
                            Integer.parseInt(request.getParameter("filtroCenco")));
                    System.out.println(WEB_NAME+"Mantenedor - Organizacion - "
                        + "items organizacion empresa= "+objectsCount);
                    //Convert Java Object to Json
                    JsonElement element = gson.toJsonTree(listaObjetos,
                        new TypeToken<List<OrganizacionEmpresaVO>>() {}.getType());

                    JsonArray jsonArray = element.getAsJsonArray();
                    String listData=jsonArray.toString();
                        
                    //Return Json in the format required by jTable plugin
                    listData="{\"Result\":\"OK\",\"Records\":" + 
                        listData+",\"TotalRecordCount\": " + 
                        objectsCount + "}";
                    session.setAttribute("organizacion|"+userConnected.getUsername(), listaObjetos);
                    response.getWriter().print(listData);
                    //request.getRequestDispatcher("/mantenedores/mantenedoresFrmSet.jsp").forward(request, response);
                }catch(Exception ex){
                    String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getMessage()+"}";
                    response.getWriter().print(error);
                    ex.printStackTrace();
                }   
            }else if (action.compareTo("create") == 0) {
                    System.out.println(WEB_NAME+"Mantenedor - Organizacion - Insertar...");
                    ResultCRUDVO doCreate = auxnegocio.insert(auxdata, resultado);					
                    listaObjetos.add(auxdata);

                    //Convert Java Object to Json
                    String json=gson.toJson(auxdata);					
                    //Return Json in the format required by jTable plugin
                    String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";											
                    response.getWriter().print(listData);
            }else if (action.compareTo("delete") == 0) {  
                    //Delete record
                    System.out.println(WEB_NAME+"Eliminando Organizacion- "
                        + "empresaID: " + auxdata.getEmpresaId()
                        +", deptoID: "+ auxdata.getDeptoId()
                        +", cencoID: "+ auxdata.getCencoId());
                    try{
                        auxnegocio.delete(auxdata, resultado);
//                        String listData="{\"Result\":\"OK\"}";								
//                        response.getWriter().print(listData);
                        System.out.println(WEB_NAME+"FIN Eliminar item Organizacion Empresa\n\n");
                        response.sendRedirect(request.getContextPath()+"/mantencion/lista_organizacion.jsp");
                    }catch(Exception ex){
                        String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getStackTrace().toString()+"}";
                        response.getWriter().print(error);
                    }
            }
      }
    }
    
}
