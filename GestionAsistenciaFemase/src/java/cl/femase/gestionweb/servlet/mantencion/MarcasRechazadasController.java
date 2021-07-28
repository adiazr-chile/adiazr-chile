package cl.femase.gestionweb.servlet.mantencion;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.MarcasRechazosBp;
import cl.femase.gestionweb.vo.MarcaRechazoVO;
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
import java.util.StringTokenizer;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

public class MarcasRechazadasController extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 996L;
    
    public MarcasRechazadasController() {
        
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
        MarcasRechazosBp auxnegocio=new MarcasRechazosBp(appProperties);

        if(request.getParameter("action") != null){
            System.out.println("[MarcasRechazadasController]"
                + "action is: " + request.getParameter("action"));
            List<MarcaRechazoVO> listaObjetos = new ArrayList<>();
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            response.setContentType("application/json");

//            MaintenanceEventVO resultado=new MaintenanceEventVO();
//            resultado.setUsername(userConnected.getUsername());
//            resultado.setDatetime(new Date());
//            resultado.setUserIP(request.getRemoteAddr());
//            resultado.setType("MRE");
                   
            //Fetch Data from User Table
            int startPageIndex      = 0;
            int numRecordsPerPage   = 10;
            String jtSorting        = "fecha_hora_actualizacion desc";
            /** filtros de busqueda */
            
            if (request.getParameter("jtStartIndex") != null) 
                startPageIndex = Integer.parseInt(request.getParameter("jtStartIndex"));
            if (request.getParameter("jtPageSize") != null) 
                numRecordsPerPage   = Integer.parseInt(request.getParameter("jtPageSize"));
            if (request.getParameter("jtSorting") != null) 
                jtSorting   = request.getParameter("jtSorting").trim();
            
            System.out.println("[MarcasRechazadasController."
                + "processRequest()]."
                + " jtSorting antes_1: " + jtSorting);
            
            if (jtSorting.contains("codDispositivo")) jtSorting = jtSorting.replaceFirst("codDispositivo","cod_dispositivo");
            else if (jtSorting.contains("tipoMarca")) jtSorting = jtSorting.replaceFirst("tipoMarca","cod_tipo_marca");
            else if (jtSorting.contains("rutEmpleado")) jtSorting = jtSorting.replaceFirst("rutEmpleado","rut_empleado");
            //else if (jtSorting.contains("fechaHora")) jtSorting = jtSorting.replaceFirst("fechaHora","fecha_hora");
            else if (jtSorting.contains("motivoRechazo")) jtSorting = jtSorting.replaceFirst("motivoRechazo","motivo_rechazo");
            else if (jtSorting.contains("fechaHoraStr")) jtSorting = jtSorting.replaceAll("fechaHoraStr","fecha_hora");
            //else if (jtSorting.contains("fechaHoraActualizacion")) jtSorting = jtSorting.replaceFirst("fechaHoraActualizacion","fecha_hora_actualizacion");
            
//            if (jtSorting.compareTo("fechaHoraStr ASC") == 0){
//                System.out.println("--->set 1");
//                jtSorting = "fecha_hora ASC";
//            }else if (jtSorting.compareTo("fechaHoraStr DESC") == 0){
//                System.out.println("--->set 2");
//                jtSorting = "fecha_hora DESC";
//            } 
            
            System.out.println("[MarcasRechazadasController."
                + "processRequest()]."
                + " jtSorting despues_2: " + jtSorting);
       
            /**
            * En este parametro vendra 'deptoId|cencoId'
            */
            String empresaId= null;
            String deptoId  = "-1";
            String cencoId  = "-1";
            String paramCencoID         = request.getParameter("cencoId");
            System.out.println("[MarcasRechazadasController]"
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
            System.out.println("[MarcasRechazadasController]"
                + "empresaId: " + empresaId
                + ",deptoId: " + deptoId
                + ",cencoId: " + cencoId
                +", rut: "+request.getParameter("rutEmpleado")
                +", startDate: "+request.getParameter("startDate")
                +", endDate: "+request.getParameter("endDate"));
//            boolean listar=true;
//            if (empresaId==null || cencoId.compareTo("-1")==0 || 
//                    request.getParameter("rutEmpleado").compareTo("-1") == 0
//                    || request.getParameter("startDate").compareTo("") == 0 
//                    || request.getParameter("endDate").compareTo("") == 0){
//                listar=false;
//            }
//            if (request.getParameter("rutEmpleado").compareTo("-1") == 0 &&
//                    request.getParameter("startDate").compareTo("") == 0 &&
//                    request.getParameter("endDate").compareTo("") == 0 &&
//                    request.getParameter("dispositivo") != null && 
//                    request.getParameter("dispositivo").compareTo("-1") == 0){
//                listar=false;
//            }else if (empresaId != null && 
//                    request.getParameter("startDate").compareTo("") == 0 &&
//                    request.getParameter("endDate").compareTo("") == 0){
//                listar = false;
//            }
            
           // empresaId= null, deptoId= -1, cencoId= -1, rutEmpleado= -1, dispositivoId= null, startDate= , endDate= 

            if (action.compareTo("list") == 0){
                System.out.println("[MarcasRechazadasController]"
                    + "mostrando marcas rechazadas..."+
                        "empresaId= " + empresaId
                        +", deptoId= " + deptoId
                        +", cencoId= " +cencoId
                        +", rutEmpleado= " + request.getParameter("rutEmpleado")
                        +", dispositivoId= "  + request.getParameter("dispositivo")
                        +", startDate= " +request.getParameter("startDate")
                        +", endDate= " +request.getParameter("endDate"));
                try{
                    int objectsCount = 0;
                    if (cencoId != null && cencoId.compareTo("-1")!=0 
                            && request.getParameter("dispositivo").compareTo("-1") != 0 
                            && (request.getParameter("startDate").compareTo("") != 0 
                                || request.getParameter("endDate").compareTo("") != 0)
                            ){
                        request.setAttribute("empresa",empresaId);
                        request.setAttribute("rut_empleado", request.getParameter("rutEmpleado"));
                        
                        listaObjetos = auxnegocio.getMarcasRechazadas(empresaId,
                            request.getParameter("rutEmpleado"), 
                            request.getParameter("dispositivo"),
                            request.getParameter("startDate"), 
                            request.getParameter("endDate"),
                            startPageIndex, 
                            numRecordsPerPage, 
                            jtSorting);
                        session.setAttribute("marcasRechazadas|"+userConnected.getUsername(), listaObjetos);
                        
                        //Get Total Record Count for Pagination
                        objectsCount = auxnegocio.getMarcasRechazadasCount(empresaId,
                            request.getParameter("rutEmpleado"), 
                            request.getParameter("dispositivo"),
                            request.getParameter("startDate"), 
                            request.getParameter("endDate"));
                    }
                    //Convert Java Object to Json
                    JsonElement element = gson.toJsonTree(listaObjetos,
                        new TypeToken<List<MarcaRechazoVO>>() {}.getType());

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
