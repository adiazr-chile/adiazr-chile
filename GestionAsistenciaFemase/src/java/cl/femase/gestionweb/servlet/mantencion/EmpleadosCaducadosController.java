package cl.femase.gestionweb.servlet.mantencion;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.business.MaintenanceEventsBp;
import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioCentroCostoVO;
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
import java.util.StringTokenizer;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

public class EmpleadosCaducadosController extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 996L;
    
    public EmpleadosCaducadosController() {
        
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
            setResponseHeaders(response);
            processRequest(request, response);
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
        EmpleadosBp empleadosbp         = new EmpleadosBp(appProperties);
        MaintenanceEventsBp eventosBp   = new MaintenanceEventsBp(appProperties);
        
        if(request.getParameter("action") != null){
            System.out.println("[EmpleadosCaducadosController]action is: " + request.getParameter("action"));
            List<EmpleadoVO> listaEmpleados = new ArrayList<>();
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            response.setContentType("application/json");

            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("CAD");
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
                   
            //Fetch Data from User Table
            int startPageIndex      = 0;
            int numRecordsPerPage   = 10;
            String jtSorting        = "empl_rut asc";
            /** filtros de busqueda */
            String filtroRut     = "";
            String filtroNombre  = "";
            int filtroIdTurno    = -1;
                        
            if (request.getParameter("jtStartIndex") != null) 
                startPageIndex = Integer.parseInt(request.getParameter("jtStartIndex"));
            if (request.getParameter("jtPageSize") != null) 
                numRecordsPerPage   = Integer.parseInt(request.getParameter("jtPageSize"));
            if (request.getParameter("jtSorting") != null) 
                jtSorting   = request.getParameter("jtSorting");
            
            if (jtSorting.contains("codInterno")) jtSorting = jtSorting.replaceFirst("codInterno","cod_interno");
            else if (jtSorting.contains("rut")) jtSorting = jtSorting.replaceFirst("rut","empl_rut");
            else if (jtSorting.contains("nombres")) jtSorting = jtSorting.replaceFirst("nombres","empl_nombres");
            
            if (request.getParameter("rutEmpleado") != null) 
                filtroRut = request.getParameter("rutEmpleado");
            if (request.getParameter("nombres") != null) 
                filtroNombre = request.getParameter("nombres");
            if (request.getParameter("turno") != null) 
                filtroIdTurno  = Integer.parseInt(request.getParameter("turno"));
            //objeto usado para update/insert
            EmpleadoVO empleado = new EmpleadoVO();
           
            //esta es la PK
            if(request.getParameter("rut") != null){
                empleado.setRut(request.getParameter("rut"));
            }
            
            if(request.getParameter("codInterno") != null){
                empleado.setCodInterno(request.getParameter("codInterno"));
            }
            
            if(request.getParameter("fechaTerminoContratoAsStr") != null){
                empleado.setFechaTerminoContratoAsStr(request.getParameter("fechaTerminoContratoAsStr"));
            }
            if(request.getParameter("contratoIndefinido") != null){
                if (request.getParameter("contratoIndefinido").compareTo("true") == 0){
                    empleado.setContratoIndefinido(true);
                    empleado.setFechaTerminoContratoAsStr(Constantes.FECHA_FIN_CONTRATO_INDEFINIDO);
                }else empleado.setContratoIndefinido(false);
            }
                        
            if(request.getParameter("estado") != null){
                empleado.setEstado(Integer.parseInt(request.getParameter("estado")));
            }
            
            if (action.compareTo("list")==0) {
                System.out.println("[EmpleadosCaducadosController."
                    + "processRequest]"
                    + "Mostrando empleados...");
                try{
                    String empresaid="-1";
                    String deptoid="-1";
                    int cencoid=-1;
                    
                    String paramCencoID         = request.getParameter("cencoId");
                    System.out.println("[EmpleadosCaducadosController.processRequest]"
                        + "token param 'cencoID'= " + paramCencoID);
                    if (paramCencoID != null && paramCencoID.compareTo("-1") != 0){
                        StringTokenizer tokenCenco  = new StringTokenizer(paramCencoID, "|");
                        if (tokenCenco.countTokens() > 0){
                            while (tokenCenco.hasMoreTokens()){
                                empresaid   = tokenCenco.nextToken();
                                deptoid     = tokenCenco.nextToken();
                                cencoid     = Integer.parseInt(tokenCenco.nextToken());
                            }
                        }
                    }
                    
                    System.out.println("[EmpleadosCaducadosController."
                        + "processRequest]Mostrar empleados. "
                        + "Empresa: " + empresaid
                        + ", depto: " + deptoid
                        + ", cenco: " + cencoid
                        + ", idTurno: " + filtroIdTurno
                        + ", rut: " + filtroRut
                        + ", nombres: " + filtroNombre);
                    String strcencos = "";
                    if (cencoid == -1){
                        if (userConnected.getCencos()!=null && !userConnected.getCencos().isEmpty()){
                            Iterator<UsuarioCentroCostoVO> cencosIt = userConnected.getCencos().iterator();
                            while (cencosIt.hasNext()) {
                                strcencos += cencosIt.next().getCcostoId()+",";
                            }
                            strcencos = strcencos.substring(0, strcencos.length()-1);
                            System.out.println("[EmpleadosCaducadosController."
                                + "processRequest]"
                                + "Mostrar empleados "
                                + "para los cencos seleccionados: " + strcencos);
                        }else{
                            System.out.println("[EmpleadosCaducadosController."
                                + "processRequest]"
                                + "No hay cencos vigentes para el usuario conectado");
                        }
                        
                    }else strcencos = ""+ cencoid;
                    /**
                    * Busqueda por Rut: 
                    *   Al buscar por rut, se deben quitar los puntos y guion, 
                    *   luego buscar en la modalidad 'empiece con' (like 'sssss%')
                    * 
                    */
                    if (filtroRut != null){
                        String auxRun = filtroRut;
                        String newRun =  auxRun.replace(".", "");
                        filtroRut = newRun;
                    }
                    int empleadosCount=0;
                    if (userConnected.getCencos()!=null && !userConnected.getCencos().isEmpty()){
                        listaEmpleados = empleadosbp.getCaducados(empresaid, 
                            deptoid, 
                            cencoid,
                            -1,
                            filtroIdTurno,
                            filtroRut, 
                            filtroNombre, 
                            null, 
                            null,
                            -1,
                            strcencos,
                            startPageIndex, 
                            numRecordsPerPage, 
                            jtSorting);
                    
                        session.setAttribute("empresaId", empresaid);
                        session.setAttribute("deptoId", deptoid);
                        session.setAttribute("cencoId", "" + cencoid);
                        session.setAttribute("filtroIdTurno", filtroIdTurno);
                        session.setAttribute("filtroRut", filtroRut);
                        session.setAttribute("filtroNombre", filtroNombre);
                        
                        //Get Total Record Count for Pagination
                        empleadosCount = empleadosbp.getCaducadosCount(empresaid, 
                            deptoid, 
                                cencoid, 
                                -1,
                                filtroIdTurno,
                                filtroRut, 
                                filtroNombre, 
                                null, 
                                null,
                                -1,
                                strcencos);
                        System.out.println("[EmpleadosCaducadosController."
                            + "processRequest]num empleados: " + empleadosCount);
                        
                        //agregar evento al log.
                        resultado.setUsername(userConnected.getUsername());
                        resultado.setDatetime(new Date());
                        resultado.setUserIP(request.getRemoteAddr());
                        resultado.setType("CAD");
                        resultado.setEmpresaIdSource(userConnected.getEmpresaId());
                        resultado.setEmpresaId(empresaid);
                        resultado.setDeptoId(deptoid);
                        resultado.setCencoId(cencoid);
                        resultado.setDescription("Consulta empleados con contrato caducado.");
                        eventosBp.addEvent(resultado);
                    }
                    
                    //Convert Java Object to Json
                    JsonElement element = gson.toJsonTree(listaEmpleados,
                            new TypeToken<List<EmpleadoVO>>() {}.getType());

                    JsonArray jsonArray = element.getAsJsonArray();
                    String listData=jsonArray.toString();

                    //Return Json in the format required by jTable plugin
                    listData="{\"Result\":\"OK\",\"Records\":" + 
                            listData+",\"TotalRecordCount\": " + 
                            empleadosCount + "}";
                    
                    session.setAttribute("empleadoscaducados|"
                        + userConnected.getUsername(), listaEmpleados);
                    
                    System.out.println("[EmpleadosCaducadosController."
                        + "processRequest]"
                        + "FIN LISTAR EMPLEADOS");
                    response.getWriter().print(listData);
                    
                    //request.getRequestDispatcher("/mantenedores/mantenedoresFrmSet.jsp").forward(request, response);
                }catch(Exception ex){
                    String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getMessage()+"}";
                    response.getWriter().print(error);
                    ex.printStackTrace();
                }   
            }else if (action.compareTo("update") == 0) {  
                    try{
                        System.out.println("[EmpleadosCaducadosController."
                            + "processRequest]"
                            + "Modificar empleado. "
                            + "Rut: " + empleado.getRut()
                            + ", codInterno: " + empleado.getCodInterno()
                            + ", fechaTerminoContratoStr: " + empleado.getFechaTerminoContratoAsStr()    
                            + ", contratoIndefinido?: " + empleado.isContratoIndefinido()
                            + ", estado: " + empleado.getEstado());
                        MaintenanceVO doUpdate = 
                            empleadosbp.updateEmpleadoCaducado(empleado, resultado);
                        listaEmpleados.add(empleado);

                        //Convert Java Object to Json
                        String json=gson.toJson(empleado);					
                        //Return Json in the format required by jTable plugin
                        String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";											
                        response.getWriter().print(listData);
                        
                    }catch(Exception ex){
                        String error="{\"Result\":\"ERROR\",\"Message\":"
                            + ex.getStackTrace().toString()+"}";
                        response.getWriter().print(error);
                    }
            }
            
      }
    }
    
}
