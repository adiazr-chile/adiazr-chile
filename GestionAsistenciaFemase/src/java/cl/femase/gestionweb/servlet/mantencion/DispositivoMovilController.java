package cl.femase.gestionweb.servlet.mantencion;

import cl.femase.gestionweb.business.DispositivoBp;
import cl.femase.gestionweb.dao.DispositivoDAO;
import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.dao.DispositivoMovilDAO;
import cl.femase.gestionweb.dao.MaintenanceEventsDAO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.DispositivoMovilVO;
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
import java.util.StringTokenizer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import org.apache.log4j.Logger;

public class DispositivoMovilController extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 996L;
    
    public DispositivoMovilController() {
        
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
        
        DispositivoBp devicesBp = new DispositivoBp(appProperties);
        DispositivoMovilDAO dao         = new DispositivoMovilDAO(appProperties);
        DispositivoDAO daoDispositivo   = new DispositivoDAO(appProperties);
        MaintenanceEventsDAO daoEventos = new MaintenanceEventsDAO(appProperties);
        
        if(request.getParameter("action") != null){
            System.out.println(WEB_NAME+"[DispositivoMovilController]"
                + "action is: " + request.getParameter("action"));
            List<DispositivoMovilVO> devicesList = new ArrayList<DispositivoMovilVO>();
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            response.setContentType("application/json");

            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("DMO");
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
                   
            //Fetch Data from User Table
            int startPageIndex      = 0;
            int numRecordsPerPage   = 10;
            String jtSorting        = "movil_id asc";
            /** filtros de busqueda */
            int filtroEstado = -1;
            String filtroMovilId = "";
            
            if (request.getParameter("jtStartIndex") != null) 
                startPageIndex = Integer.parseInt(request.getParameter("jtStartIndex"));
            if (request.getParameter("jtPageSize") != null) 
                numRecordsPerPage   = Integer.parseInt(request.getParameter("jtPageSize"));
            if (request.getParameter("jtSorting") != null) 
                jtSorting   = request.getParameter("jtSorting");
            
            if (jtSorting.contains("id")) jtSorting = jtSorting.replaceFirst("id","movil_id");
            else if (jtSorting.contains("estado")) jtSorting = jtSorting.replaceFirst("estado","estado_disp_movil");
            
            if (request.getParameter("filtroEstado") != null) 
                filtroEstado  = Integer.parseInt(request.getParameter("filtroEstado"));
            if (request.getParameter("filtroMovilId") != null) 
                filtroMovilId = request.getParameter("filtroMovilId");
            
            //objeto usado para update
            DispositivoMovilVO device = new DispositivoMovilVO();
            
            if(request.getParameter("id") != null){
                device.setId(request.getParameter("id"));
            }
            if(request.getParameter("estado") != null){
                device.setEstado(Integer.parseInt(request.getParameter("estado")));
            }
            if(request.getParameter("correlativo") != null){
                device.setCorrelativo(Integer.parseInt(request.getParameter("correlativo")));
            }
            if(request.getParameter("androidId") != null){
                device.setAndroidId(request.getParameter("androidId"));
            }
            if(request.getParameter("fechaHoraCreacion") != null){
                device.setFechaHoraCreacion(request.getParameter("fechaHoraCreacion"));
            }
            if(request.getParameter("directorRut") != null){
                device.setDirectorRut(request.getParameter("directorRut"));
            }
            if(request.getParameter("directorCencoId") != null){
                device.setDirectorCencoId(Integer.parseInt(request.getParameter("directorCencoId")));
            }
             
            if(request.getParameter("empresaId") != null){
                device.setEmpresaId(request.getParameter("empresaId"));
            }
            if(request.getParameter("cencoNombre") != null){
                device.setCencoNombre(request.getParameter("cencoNombre"));
            }
            
            if (action.compareTo("list") == 0) {
                System.out.println(WEB_NAME+"[DispositivoMovilController]"
                    + "mostrando dispositivos moviles...");
                String paramCencoID  = request.getParameter("cencoId");
                String paramEmpresa = null;
                String paramDepto   = null;
                String cencoId      = "";
                int intCencoId = -1;
                System.out.println(WEB_NAME+"[DispositivoMovilController]"
                    + "List-token param 'cencoID'= " + paramCencoID);
                if (paramCencoID != null && paramCencoID.compareTo("-1") != 0){
                    StringTokenizer tokenCenco  = new StringTokenizer(paramCencoID, "|");
                    if (tokenCenco.countTokens() > 0){
                        while (tokenCenco.hasMoreTokens()){
                            paramEmpresa   = tokenCenco.nextToken();
                            paramDepto     = tokenCenco.nextToken();
                            cencoId     = tokenCenco.nextToken();
                            intCencoId = Integer.parseInt(cencoId);
                            session.setAttribute("dmoParamEmpresa", paramEmpresa);
                            session.setAttribute("dmoParamDepto", paramDepto);
                            session.setAttribute("dmoParamCencoId", intCencoId);
                        }
                    }
                }
                System.out.println(WEB_NAME+"[DispositivoMovilController]"
                    + "Listar dispositivos moviles. "
                    + "empresa: " + paramEmpresa
                    +", depto: " + paramDepto
                    +", intCencoId: " + intCencoId);
                try{
                    int rowCount = 0;
                    if (intCencoId != -1){
                        devicesList = 
                            dao.getDispositivosMoviles(intCencoId,
                                filtroEstado, 
                                filtroMovilId,
                                startPageIndex, 
                                numRecordsPerPage, 
                                jtSorting);
                        //Get Total Record Count for Pagination
                        rowCount = dao.getDispositivosMovilesCount(intCencoId,
                            filtroEstado, 
                            filtroMovilId);
                        
                        session.setAttribute("cencoIdMoviles", intCencoId);
                        session.setAttribute("filtroEstado", filtroEstado);
                        session.setAttribute("filtroMovilId", filtroMovilId);
                    }
                    
                    //Convert Java Object to Json
                    JsonElement element = gson.toJsonTree(devicesList,
                        new TypeToken<List<DispositivoMovilVO>>() {}.getType());

                    JsonArray jsonArray = element.getAsJsonArray();
                    String listData=jsonArray.toString();
                        
                    //Return Json in the format required by jTable plugin
                    listData="{\"Result\":\"OK\",\"Records\":" + 
                        listData+",\"TotalRecordCount\": " + 
                        rowCount + "}";
                    response.getWriter().print(listData);
                    //request.getRequestDispatcher("/mantenedores/mantenedoresFrmSet.jsp").forward(request, response);
                }catch(IOException ex){
                    String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getMessage()+"}";
                    response.getWriter().print(error);
                    ex.printStackTrace();
                }   
            }else if (action.compareTo("update") == 0) {  
                    System.out.println(WEB_NAME+"[DispositivoMovilController]"
                        + "Modificar dispositivo movil. "
                        + "DeviceId: " + device.getId()
                        + ", correlativo: " + device.getCorrelativo()
                        + ", estado: " + device.getEstado());
                    try{
                        String pEmpresaId   = (String)session.getAttribute("dmoParamEmpresa");
                        String pDeptoId     = (String)session.getAttribute("dmoParamDepto");
                        int pCencoId        = (Integer)session.getAttribute("dmoParamCencoId");
                        //agregar evento al log.
                        resultado.setEmpresaId(pEmpresaId);
                        resultado.setDeptoId(pDeptoId);
                        resultado.setCencoId(pCencoId);
                            
                        ResultCRUDVO updValues = dao.update(device);
                        daoDispositivo.updateEstadoDispositivo(device.getId(), device.getEstado());
                        
                        /** Refrescar lista de dispositivos en sesion */
                        devicesBp.openDbConnection();
                        session.setAttribute("dispositivos", devicesBp.getDispositivos(null, -1, 1,null,0,0,"device_id"));
                        devicesBp.closeDbConnection();
                        
                        //if (!updValues.isThereError()){
                        String msgFinal = updValues.getMsg();
                        updValues.setMsg(msgFinal);
                        resultado.setDescription(msgFinal);
                        //insertar evento al log 
                        daoEventos.addEvent(resultado);
                        
                        DispositivoMovilVO aux = 
                            dao.getDispositivoById(device.getId());
                        device.setCorrelativo(aux.getCorrelativo());
                        devicesList.add(device);

                        //Convert Java Object to Json
                        String json=gson.toJson(device);					
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
