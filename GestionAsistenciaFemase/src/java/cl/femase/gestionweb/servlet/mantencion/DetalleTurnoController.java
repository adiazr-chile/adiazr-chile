package cl.femase.gestionweb.servlet.mantencion;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.DetalleTurnosBp;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.DetalleTurnoVO;
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
import java.util.Iterator;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

public class DetalleTurnoController extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 992L;
    
    public DetalleTurnoController() {
        
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
        DetalleTurnosBp auxnegocio=new DetalleTurnosBp(appProperties);

        if(request.getParameter("action") != null){
            System.out.println(WEB_NAME+"[DetalleTurnoController]"
                + "action is: " + request.getParameter("action"));
            List<DetalleTurnoVO> listaObjetos = new ArrayList<DetalleTurnoVO>();
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            response.setContentType("application/json");

            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("DTU");
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
            
            /**Mostrar parametros recibidos*/
            List<String> parameterNames = new ArrayList<>(request.getParameterMap().keySet());
            Iterator<String> paramsIterator = parameterNames.iterator();
            while (paramsIterator.hasNext()) {
                System.out.println(WEB_NAME+"----->Param Name: " + paramsIterator.next());
            }
            
            //Fetch Data from User Table
            int startPageIndex      = 0;
            int numRecordsPerPage   = 10;
            String jtSorting        = "cod_dia asc";
            /** filtros de busqueda */
            int filtroIdTurno     = -1;
                        
            if (request.getParameter("jtStartIndex") != null) 
                startPageIndex = Integer.parseInt(request.getParameter("jtStartIndex"));
            if (request.getParameter("jtPageSize") != null) 
                numRecordsPerPage   = Integer.parseInt(request.getParameter("jtPageSize"));
            if (request.getParameter("jtSorting") != null) 
                jtSorting   = request.getParameter("jtSorting");
            
            if (jtSorting.contains("idTurno")) jtSorting = jtSorting.replaceFirst("idTurno","id_turno");
            else if (jtSorting.contains("codDia")) jtSorting = jtSorting.replaceFirst("codDia","cod_dia");
            else if (jtSorting.contains("horaEntrada")) jtSorting = jtSorting.replaceFirst("horaEntrada","hora_entrada");
            else if (jtSorting.contains("horaSalida")) jtSorting = jtSorting.replaceFirst("horaSalida","hora_salida");
                        
            if (request.getParameter("filtroId") != null) 
                filtroIdTurno  = Integer.parseInt(request.getParameter("filtroId"));
                       
            //objeto usado para update/insert
            DetalleTurnoVO auxdata = new DetalleTurnoVO();
            System.out.println(WEB_NAME+"cl.femase.gestionweb.servlet."
                + "mantencion.DetalleTurnoController."
                + "processRequest()."
                + "param idTurno= "+request.getParameter("idTurno")
                + ",param codDia= "+request.getParameter("codDia")
                + ",param horaEntrada= "+request.getParameter("horaEntrada")
                + ",param horaSalida= "+request.getParameter("horaSalida")
            );
            if (request.getParameter("idTurno")!=null){
                auxdata.setIdTurno(Integer.parseInt(request.getParameter("idTurno")));
            }
            if (request.getParameter("codDia")!=null){
                auxdata.setCodDia(Integer.parseInt(request.getParameter("codDia")));
            }
            if (request.getParameter("horaEntrada")!=null){
                auxdata.setHoraEntrada(request.getParameter("horaEntrada"));
            }
            if (request.getParameter("horaSalida")!=null){
                auxdata.setHoraSalida(request.getParameter("horaSalida"));
            }
            
            if (request.getParameter("holgura")!=null){
                auxdata.setHolgura(Integer.parseInt(request.getParameter("holgura")));
            }
            if (request.getParameter("minutosColacion")!=null){
                auxdata.setMinutosColacion(Integer.parseInt(request.getParameter("minutosColacion")));
            }
            
            if (action.compareTo("list")==0) {
                System.out.println(WEB_NAME+"Mantenedor - Detalle Turno - "
                    + "mostrando detalle turno...");
                try{
                    listaObjetos = auxnegocio.getDetalleTurno(filtroIdTurno, 
                        startPageIndex, 
                        numRecordsPerPage, 
                        jtSorting);
                        
                    //Get Total Record Count for Pagination
                    int objectsCount = auxnegocio.getDetalleTurnoCount(filtroIdTurno);

                    //Convert Java Object to Json
                    JsonElement element = gson.toJsonTree(listaObjetos,
                        new TypeToken<List<DetalleTurnoVO>>() {}.getType());

                    JsonArray jsonArray = element.getAsJsonArray();
                    String listData=jsonArray.toString();
                        
                    session.setAttribute("detalleturnos|"+userConnected.getUsername(), listaObjetos);
                    
                    //Return Json in the format required by jTable plugin
                    listData="{\"Result\":\"OK\",\"Records\":" + 
                        listData+",\"TotalRecordCount\": " + 
                        objectsCount + "}";
                    System.out.println(WEB_NAME+"[DetalleTurnoController]json data: "+listData);
                    response.getWriter().print(listData);
                    //request.getRequestDispatcher("/mantenedores/mantenedoresFrmSet.jsp").forward(request, response);
                }catch(Exception ex){
                    String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getMessage()+"}";
                    response.getWriter().print(error);
                    ex.printStackTrace();
                }   
            }else if (action.compareTo("create") == 0) {
                    System.out.println(WEB_NAME+"Mantenedor - DetalleTurnos - Insertar Detalle Turno...");
                    MaintenanceVO doCreate = auxnegocio.insert(auxdata, resultado);					
                    listaObjetos.add(auxdata);

                    //Convert Java Object to Json
                    String json=gson.toJson(auxdata);					
                    //Return Json in the format required by jTable plugin
                    String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";											
                    response.getWriter().print(listData);
            }else if (action.compareTo("update") == 0) {  
                    System.out.println(WEB_NAME+"Mantenedor - Turnos - Actualizar Detalle Turno...");
                    try{
                        MaintenanceVO doUpdate = auxnegocio.update(auxdata, resultado);
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
                    System.out.println(WEB_NAME+"Mantenedor - Turnos - Elimina Detalle Turno. "
                        + "IdTurno: "+auxdata.getIdTurno()
                        + ". codDia: "+auxdata.getCodDia());
                    try{
                        MaintenanceVO doDelete = auxnegocio.delete(auxdata, resultado);
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
