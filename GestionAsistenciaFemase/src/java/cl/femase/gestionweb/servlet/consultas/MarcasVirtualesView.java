package cl.femase.gestionweb.servlet.consultas;

import cl.femase.gestionweb.business.MarcasBp;
import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.common.GetPropertyValues;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.MarcaVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
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

public class MarcasVirtualesView extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 996L;
    GetPropertyValues m_properties = new GetPropertyValues();
    
    public MarcasVirtualesView() {
        
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

        MarcasBp auxnegocio=new MarcasBp(appProperties);

        if(request.getParameter("action") != null){
            System.out.println(WEB_NAME+"[MarcasEventosController]"
                + "action is: " + request.getParameter("action"));
            List<MarcaVO> listaMarcas = new ArrayList<>();
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            response.setContentType("application/json");

            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("MAR");
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
                   
            /**
            * En este parametro vendra 'deptoId|cencoId'
            */
            String empresaId= null;
            String deptoId  = null;
            String cencoId  = "-1";
            String paramCencoID         = request.getParameter("cencoId");
            System.out.println(WEB_NAME+"[MarcasVirtualesView]"
                + "token param 'cencoID'= " + paramCencoID);
            if (paramCencoID != null && paramCencoID.compareTo("-1") != 0){
                StringTokenizer tokenCenco  = new StringTokenizer(paramCencoID, "|");
                if (tokenCenco.countTokens() > 0){
                    while (tokenCenco.hasMoreTokens()){
                        empresaId   = tokenCenco.nextToken();
                        deptoId     = tokenCenco.nextToken();
                        cencoId     = tokenCenco.nextToken();
                    }
                }
            }
            
            //Fetch Data from User Table
            int startPageIndex      = 0;
            int numRecordsPerPage   = 10;
            String jtSorting        = "fecha_hora desc";
            /** filtros de busqueda */
            
            if (request.getParameter("jtStartIndex") != null) 
                startPageIndex = Integer.parseInt(request.getParameter("jtStartIndex"));
            if (request.getParameter("jtPageSize") != null) 
                numRecordsPerPage   = Integer.parseInt(request.getParameter("jtPageSize"));
            if (request.getParameter("jtSorting") != null) 
                jtSorting   = request.getParameter("jtSorting");
            System.out.println(WEB_NAME+"MarcasVirtualesView." +
                " jtSorting: " + request.getParameter("jtSorting"));
            
            if (jtSorting.contains("fechaHoraModificacion")) 
                jtSorting = jtSorting.replaceFirst("fechaHoraModificacion","fecha_hora_modificacion");
            /*else if (jtSorting.contains("rutEmpleado")) jtSorting = jtSorting.replaceFirst("rutEmpleado","rut_empleado");
            else if (jtSorting.contains("fechaHoraStr")) jtSorting = jtSorting.replaceFirst("fechaHoraStr","fecha_hora");
            */
            
            boolean listar=true;
            
            if ((cencoId==null || cencoId.compareTo("-1") == 0) &&
                    (request.getParameter("rutEmpleado")!=null && request.getParameter("rutEmpleado").compareTo("-1") == 0) &&
                    request.getParameter("startDate").compareTo("") == 0 &&
                    request.getParameter("endDate").compareTo("") == 0){
                listar=false;
            }else if ((request.getParameter("startDate")!=null && request.getParameter("startDate").compareTo("") == 0) &&
                    request.getParameter("endDate").compareTo("") == 0){
                listar = false;
            }
            
            if (action.compareTo("list") == 0){
                System.out.println(WEB_NAME+"[MarcasVirtualesView]"
                    + "mostrando marcas virtuales."+
                    "CencoId= " +cencoId
                    +", rutEmpleado= " +request.getParameter("rutEmpleado")
                    +", startDate= " +request.getParameter("startDate")
                    +", endDate= " +request.getParameter("endDate"));
                try{
                    int objectsCount = 0;
                    if (listar){
                        request.setAttribute("empresa", empresaId);
                        request.setAttribute("rut_empleado", request.getParameter("rutEmpleado"));
                        
                        listaMarcas = auxnegocio.getMarcasVirtuales(empresaId,
                            request.getParameter("rutEmpleado"), 
                            request.getParameter("startDate"), 
                            request.getParameter("endDate"),
                            Constantes.COD_MARCA_VIRTUAL,
                            startPageIndex, 
                            numRecordsPerPage, 
                            jtSorting);
                        session.setAttribute("marcasVirtuales|"+userConnected.getUsername(),listaMarcas);
        
                        //Get Total Record Count for Pagination
                        objectsCount = auxnegocio.getMarcasVirtualesCount(empresaId,
                            request.getParameter("rutEmpleado"), 
                            request.getParameter("startDate"), 
                            request.getParameter("endDate"),
                            Constantes.COD_MARCA_VIRTUAL);
                    }
                    //Convert Java Object to Json
                    JsonElement element = gson.toJsonTree(listaMarcas,
                        new TypeToken<List<MarcaVO>>() {}.getType());

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
            }
        }
    }
    
}
