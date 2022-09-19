package cl.femase.gestionweb.servlet.mantencion;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.business.TiempoExtraBp;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.TiempoExtraVO;
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

public class TiempoExtraController extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 985L;
    
    public TiempoExtraController() {
        
    }

    /**
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
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

    /**
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(true);
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        EmpleadosBp auxnegocio=new EmpleadosBp(appProperties);
        TiempoExtraBp tenegocio=new TiempoExtraBp(appProperties);
        List<EmpleadoVO> listaEmpleados = new ArrayList<EmpleadoVO>();
        List<TiempoExtraVO> listaTE = new ArrayList<TiempoExtraVO>();
        if(request.getParameter("action") != null){
            System.out.println(WEB_NAME+"[TiempoExtraController]"
                + "action is: " + request.getParameter("action"));
            
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            response.setContentType("application/json");

            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("TEX");
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());       
            //Fetch Data from User Table
            int startPageIndex      = 0;
            int numRecordsPerPage   = 10;
            String jtSorting        = "empleado.empl_rut asc";
            /** filtros de busqueda */
            String filtroEmpresa     = "";
            int filtroCenco      = -1;
            int filtroCargo      = -1;
            String filtroDepto     = "";
            
            if (request.getParameter("jtStartIndex") != null) 
                startPageIndex = Integer.parseInt(request.getParameter("jtStartIndex"));
            if (request.getParameter("jtPageSize") != null) 
                numRecordsPerPage   = Integer.parseInt(request.getParameter("jtPageSize"));
            if (request.getParameter("jtSorting") != null) 
                jtSorting   = request.getParameter("jtSorting");
            
            if (jtSorting.contains("rut")) jtSorting = jtSorting.replaceFirst("rut","empleado.empl_rut");
            else if (jtSorting.contains("nombres")) jtSorting = jtSorting.replaceFirst("nombres","empl_nombres");
            else if (jtSorting.contains("empresaNombre")) jtSorting = jtSorting.replaceFirst("empresaNombre","empresa_nombre");
            else if (jtSorting.contains("deptoNombre")) jtSorting = jtSorting.replaceFirst("deptoNombre","depto_nombre");
            else if (jtSorting.contains("cencoNombre")) jtSorting = jtSorting.replaceFirst("cencoNombre","ccosto_nombre");
            else if (jtSorting.contains("nombreCargo")) jtSorting = jtSorting.replaceFirst("nombreCargo","cargo_nombre");
            else if (jtSorting.contains("nombreTurno")) jtSorting = jtSorting.replaceFirst("nombreTurno","nombre_turno");
            
            if (request.getParameter("filtroEmpresa") != null) 
                filtroEmpresa  = request.getParameter("filtroEmpresa");
            if (request.getParameter("filtroDepto") != null) 
                filtroDepto = request.getParameter("filtroDepto");
            if (request.getParameter("filtroCenco") != null) 
                filtroCenco = Integer.parseInt(request.getParameter("filtroCenco"));
            if (request.getParameter("filtroCargo") != null) 
                filtroCargo = Integer.parseInt(request.getParameter("filtroCargo"));
            
            //objeto usado para update/insert
            TiempoExtraVO auxdata = new TiempoExtraVO();
            System.out.println(WEB_NAME+"Mantenedor - Tiempos extra - "
                + "Parametros. "
                + "action="+request.getParameter("action")    
                + ",rut="+request.getParameter("rut")
                + ", fecha="+request.getParameter("fechaAsStr"));
            if (request.getParameter("rut")!=null){
                auxdata.setRut(request.getParameter("rut"));
            }
            if (request.getParameter("fechaAsStr")!=null){
                auxdata.setFechaAsStr(request.getParameter("fechaAsStr"));
            }
            if (request.getParameter("horas")!=null){
                auxdata.setHoras(request.getParameter("horas"));
            }
            if (request.getParameter("minutos")!=null){
                auxdata.setMinutos(request.getParameter("minutos"));
            }
            if (request.getParameter("usuarioResponsable") != null){
                auxdata.setUsuarioResponsable(request.getParameter("usuarioResponsable"));
            }
            if (request.getParameter("tipo") != null){
                auxdata.setTipo(request.getParameter("tipo"));
            }
            //formar la hora en base a hrs y minutos seleccionados
            String fullTiempoExtra   = "";

            String strHour = auxdata.getHoras();
            String strMins = auxdata.getMinutos();
            int intHour = Integer.parseInt(auxdata.getHoras());
            int intMins = Integer.parseInt(auxdata.getMinutos());
            if (intHour < 10) strHour = "0" + intHour;
            if (intMins < 10) strMins = "0" + intMins;
            fullTiempoExtra = strHour + ":" + strMins;
            auxdata.setTiempoExtra(fullTiempoExtra);
            
            if (action.compareTo("listEmpleados")==0) {
                System.out.println(WEB_NAME+"Mantenedor - Tiempos extra - "
                    + "mostrando listado de empleados...");
                try{
                    int objectsCount = 0;
                    
                    if ( (filtroEmpresa != null && filtroEmpresa.compareTo("-1") != 0 )
                        && (filtroDepto != null && filtroDepto.compareTo("-1") != 0)
                            && (request.getParameter("paramRutEmpleado") != null && request.getParameter("paramRutEmpleado").compareTo("-1") != 0)
                        && (filtroCenco != -1) ){
                        listaEmpleados = auxnegocio.getEmpleadosShort(filtroEmpresa, 
                            filtroDepto, 
                            filtroCenco, 
                            filtroCargo, 
                            request.getParameter("paramRutEmpleado"), 
                            null, null, null, 
                            startPageIndex, startPageIndex, jtSorting);

                        //Get Total Record Count for Pagination
                        objectsCount = auxnegocio.getEmpleadosCount(filtroEmpresa, 
                            filtroDepto, 
                            filtroCenco, 
                            filtroCargo,
                            -1,
                            request.getParameter("paramRutEmpleado"), 
                            null, 
                            null,
                            null,
                            -1);
                    }
                    //Convert Java Object to Json
                    JsonElement element = gson.toJsonTree(listaEmpleados,
                        new TypeToken<List<EmpleadoVO>>() {}.getType());

                    JsonArray jsonArray = element.getAsJsonArray();
                    String listData=jsonArray.toString();

                    session.setAttribute("empleadoshe|"+userConnected.getUsername(), listaEmpleados);

                    //Return Json in the format required by jTable plugin
                    listData="{\"Result\":\"OK\",\"Records\":" + 
                        listData+",\"TotalRecordCount\": " + 
                        objectsCount + "}";
                    System.out.println(WEB_NAME+"[TiempoExtraController]json data: "+listData);
                    response.getWriter().print(listData);
                        //request.getRequestDispatcher("/mantenedores/mantenedoresFrmSet.jsp").forward(request, response);
                }catch(IOException ex){
                    String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getMessage()+"}";
                    response.getWriter().print(error);
                    ex.printStackTrace();
                }   
            }
            else if (action.compareTo("listTE") == 0) {
                    try{
                        System.out.println(WEB_NAME+"Mantenedor - Tiempos extra - "
                            + "Mostrar tiempos extra "
                            + "para el rut: " 
                            + request.getParameter("rut"));
                        listaTE = 
                            tenegocio.getTiemposExtra(request.getParameter("rut"),true);

                        //Convert Java Object to Json
                        JsonElement element = gson.toJsonTree(listaTE,
                            new TypeToken<List<EmpleadoVO>>() {}.getType());

                        JsonArray jsonArray = element.getAsJsonArray();
                        String listData=jsonArray.toString();

                        //session.setAttribute("empleadoshe|"+userConnected.getUsername(), listaObjetos);

                        //Return Json in the format required by jTable plugin
                        listData="{\"Result\":\"OK\",\"Records\":" + 
                            listData+",\"TotalRecordCount\": " + 
                            listaTE.size() + "}";
                        System.out.println(WEB_NAME+"[TiempoExtraController]json data: "+listData);
                        response.getWriter().print(listData);
                        //request.getRequestDispatcher("/mantenedores/mantenedoresFrmSet.jsp").forward(request, response);
                    }catch(IOException ex){
                        String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getMessage()+"}";
                        response.getWriter().print(error);
                        ex.printStackTrace();
                    }   
            }else if (action.compareTo("createTE") == 0) {  
                    System.out.println(WEB_NAME+"Mantenedor Tiempos extra - "
                        + "Crear nuevo tiempo extra...");
                    try{
                        resultado.setRutEmpleado(auxdata.getRut());
                        MaintenanceVO doInsert = tenegocio.insert(auxdata, resultado);
                        listaTE.add(auxdata);
                        //Convert Java Object to Json
                        String json=gson.toJson(auxdata);					
                        //Return Json in the format required by jTable plugin
                        String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";											
                        response.getWriter().print(listData);

                    }catch(Exception ex){
                        String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getStackTrace().toString()+"}";
                        response.getWriter().print(error);
                    }
            }else if (action.compareTo("updateTE") == 0) {  
                    System.out.println(WEB_NAME+"Mantenedor Tiempos extra - "
                        + "Actualizar tiempo extra...");
                    try{
                        MaintenanceVO doUpdate = tenegocio.update(auxdata, resultado);
                        //listaTE.add(auxdata);
                        String listData="{\"Result\":\"OK\"}";
                        response.getWriter().print(listData);

                    }catch(Exception ex){
                        String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getStackTrace().toString()+"}";
                        response.getWriter().print(error);
                    }
            }else if (action.compareTo("deleteTE") == 0) {  
                    System.out.println(WEB_NAME+"Mantenedor Tiempos extra - "
                        + "Eliminar tiempo extra...");
                    try{
                        MaintenanceVO doDelete = tenegocio.delete(auxdata, resultado);
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
