package cl.femase.gestionweb.servlet.crud;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.dao.VacacionesSaldoPeriodoDAO;
import cl.femase.gestionweb.vo.FiltroBusquedaCRUDVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import cl.femase.gestionweb.vo.VacacionesSaldoPeriodoVO;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import org.apache.log4j.Logger;
             
public class VacacionesSaldosPeriodos extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final String CRUD_TABLE_NAME     = "VacacionesSaldosPeriodos";
    private static final String CRUD_SERVLET_NAME   = "[VacacionesSaldosPeriodos.servlet]";
    private static final String FORWARD_PAGE        = "cruds/vacaciones_periodos.jsp";
        
    public VacacionesSaldosPeriodos() {
        
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

    /**
    * 
     * @param request
     * @param response
     * @throws jakarta.servlet.ServletException
     * @throws java.io.IOException
    */
    @Override
    protected void doPost(HttpServletRequest request, 
            HttpServletResponse response) 
                throws ServletException, IOException {
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
    */
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(true);
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        VacacionesSaldoPeriodoDAO vacacionesPeriodosDao = new VacacionesSaldoPeriodoDAO(appProperties);
        EmpleadosBp empleadosBp = new EmpleadosBp(appProperties);
        
        if(request.getParameter("action") != null){
            System.out.println(WEB_NAME + CRUD_SERVLET_NAME
                + "action is: " + request.getParameter("action"));
            
            String action=(String)request.getParameter("action");
            
            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("AEM");
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
                   
            /** filtros de busqueda */
            String filtroRun    = "";
            String filtroEmpresa= "";
            int filtroCenco     = -1;
            
//            //if (jtSorting.contains("code")) jtSorting = jtSorting.replaceFirst("code","param_code");
//            System.out.println(WEB_NAME + CRUD_SERVLET_NAME
//                + "param busqueda Run: " + request.getParameter("filtroRun")
//                + ", searchInput param: " + request.getParameter("searchInput")    
//            );
//            if (request.getParameter("filtroRun") != null){ 
//                filtroRun = request.getParameter("filtroRun");
//                
//                EmpleadoVO empleado = empleadosBp.getEmpleado(null, filtroRun);
//                filtroCenco     = empleado.getCencoid();
//                filtroEmpresa   = empleado.getEmpresaId();
//            }
//            
//            FiltroBusquedaCRUDVO filtroVO = new FiltroBusquedaCRUDVO();
//            filtroVO.setEmpresaId(filtroEmpresa);
//            filtroVO.setCencoId(filtroCenco);
//            filtroVO.setRunEmpleado(filtroRun);
//            
//            //Set filtros de busqueda seleccionados
//            session.removeAttribute("filtroRunVACP");
//            
//            //session.setAttribute("filtroCencoEMPL", filtroCenco);
//            session.setAttribute("filtroRunVACP", filtroRun);
            
            if (action.compareTo("list") == 0) {
                System.out.println(WEB_NAME + CRUD_SERVLET_NAME + "CRUD - " + CRUD_TABLE_NAME
                    + ". Mostrar registros para el empleado seleccionado");
                if (request.getParameter("empleados") != null){ 
                    String empleadoSeleccionado = request.getParameter("empleados");

                    StringTokenizer tokenEmpleado = new StringTokenizer(empleadoSeleccionado, "|");
                    filtroEmpresa   = tokenEmpleado.nextToken();
                    filtroCenco     = Integer.parseInt(tokenEmpleado.nextToken());
                    filtroRun       = tokenEmpleado.nextToken();
                }

                
                
                FiltroBusquedaCRUDVO filtroVO = new FiltroBusquedaCRUDVO();
                filtroVO.setEmpresaId(filtroEmpresa);
                filtroVO.setCencoId(filtroCenco);
                filtroVO.setRunEmpleado(filtroRun);

                //Set filtros de busqueda seleccionados
                session.removeAttribute("filtroRunVACP");

                //session.setAttribute("filtroCencoEMPL", filtroCenco);
                session.setAttribute("filtroRunVACP", filtroRun);
                session.removeAttribute("lista_CRUD_saldovacperiodos");
                forwardToCRUDPage(request, response, session, vacacionesPeriodosDao, filtroVO);   
            }else if (action.compareTo("buscarEmpleados") == 0){
                String nombreBuscar = request.getParameter("nombre");
                nombreBuscar=nombreBuscar.toUpperCase();
                System.out.println(WEB_NAME + CRUD_SERVLET_NAME + "CRUD - " + CRUD_TABLE_NAME
                    + ". Usuario admin_empresa? " + userConnected.getAdminEmpresa() 
                    + ". Buscar empleados cuyo nombre comience con: " + nombreBuscar);
                String sessionAttributeName="empleados_cenco_usuario";
                if (userConnected.getAdminEmpresa().compareTo("S") == 0)
                    sessionAttributeName="empleados";
                
                ArrayList<EmpleadoJsonVO> empleadosEncontrados = new ArrayList<>();
                List<EmpleadoVO> listaEmpleados = (List<EmpleadoVO>)session.getAttribute(sessionAttributeName);
                for (EmpleadoVO empleado : listaEmpleados) {
                    //System.out.println(WEB_NAME + CRUD_SERVLET_NAME + "CRUD - " + CRUD_TABLE_NAME
                    //    +". empleado.getNombreCompleto(): " + empleado.getNombreCompleto().trim().toUpperCase());
                    if (empleado.getNombreCompleto().trim().toUpperCase().startsWith(nombreBuscar)){
                        //System.out.println(WEB_NAME + CRUD_SERVLET_NAME + "CRUD - " + CRUD_TABLE_NAME
                         //+ "add empleado coincide");
                         
                        String empresaId    = empleado.getEmpresaId();
                        String deptoId      = empleado.getDeptoId();
                        String deptoNombre  = empleado.getDeptoNombre();
                        int cencoId         = empleado.getCencoId();
                        String cencoNombre  = empleado.getCencoNombre();
                        
                        if (sessionAttributeName.compareTo("empleados") == 0){
                            empresaId   = empleado.getEmpresa().getId();
                            deptoId     = empleado.getDepartamento().getId();
                            deptoNombre = empleado.getDepartamento().getNombre();
                            cencoId     = empleado.getCentroCosto().getId();
                            cencoNombre = empleado.getCentroCosto().getNombre();
                        }
                        
                        EmpleadoJsonVO empleadox = 
                            new EmpleadoJsonVO(empresaId,
                            null,
                            empleado.getRut(),
                            empleado.getNombreCompleto().trim().toUpperCase(),
                            deptoId, deptoNombre,
                            cencoId, cencoNombre);
                        empleadosEncontrados.add(empleadox);
                    }
                }
                // Configura la respuesta para ser JSON
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();

                // Convierte la lista de empleados a JSON
                // Crear una instancia de Gson
                Gson gson = new Gson();

                // Convertir el ArrayList a JSON
                String jsonArray = gson.toJson(empleadosEncontrados);

                out.print(jsonArray);
                out.flush();
            }
        }
    }
    
    /**
    * 
    */
    private String convertirAJson(ArrayList<EmpleadoVO> empleados) {
        // Implementa la conversión a JSON aquí (puedes usar una librería como Gson)
        // Ejemplo simple:
        StringBuilder jsonBuilder = new StringBuilder("[");
        for (int i = 0; i < empleados.size(); i++) {
            EmpleadoVO emp = empleados.get(i);
            jsonBuilder.append("{\"run\":")
                .append(emp.getRut())
                .append(",\"nombre\":\"")
                .append(emp.getNombreCompleto())
                .append(",\"label\":\"")
                .append("[").append(emp.getDeptoNombre()) 
                .append("][")
                .append(emp.getCencoNombre())
                .append("]")    
                .append("\"}");
            if (i < empleados.size() - 1) jsonBuilder.append(",");
        }
        jsonBuilder.append("]");
        return jsonBuilder.toString();
    }
    
    /**
    * 
    */
    private void forwardToCRUDPage(HttpServletRequest _request,
        HttpServletResponse _response,
        HttpSession _session,
        VacacionesSaldoPeriodoDAO _vacacionesPeriodosDao,
        FiltroBusquedaCRUDVO _filtroBusqueda){
    
        try {
            LinkedHashMap<String, VacacionesSaldoPeriodoVO> hashMapPeriodos = 
                new LinkedHashMap<>();
            System.out.println(WEB_NAME + CRUD_SERVLET_NAME + "CRUD - " + CRUD_TABLE_NAME
                + ". filtroEmpresaId: " + _filtroBusqueda.getEmpresaId()
                + ". filtroRunEmpleado: " + _filtroBusqueda.getRunEmpleado());
            hashMapPeriodos = _vacacionesPeriodosDao.getPeriodos(_filtroBusqueda.getEmpresaId(),
                    _filtroBusqueda.getRunEmpleado(), -1);
            
            _session.setAttribute("lista_CRUD_saldovacperiodos", hashMapPeriodos);
            
            RequestDispatcher vista = _request.getRequestDispatcher(FORWARD_PAGE);
            vista.forward(_request, _response);
        } catch (ServletException ex) {
            System.err.println(WEB_NAME + CRUD_SERVLET_NAME + "CRUD - " + CRUD_TABLE_NAME + " - "
                + "Error_1: " + ex.toString());
            ex.printStackTrace();
        } catch (IOException ex) {
            System.err.println(WEB_NAME + CRUD_SERVLET_NAME + "CRUD - " + CRUD_TABLE_NAME + " - "
                + "Error_2: " + ex.toString());
        }
        
    }

    private static class EmpleadoJsonVO {

        String empresaId;
        String empresaNombre;
        String run;
        String nombreCompleto;
        String deptoId;
        String deptoNombre;
        int cencoId;
        String cencoNombre;

        public EmpleadoJsonVO(String _empresaId, 
                String _empresaNombre, 
                String _run, 
                String _nombreCompleto, 
                String _deptoId, 
                String _deptoNombre,
                int _cencoId, 
                String _cencoNombre) {
            this.empresaId = _empresaId;
            this.empresaNombre = _empresaNombre;
            this.run = _run;
            this.nombreCompleto = _nombreCompleto;
            this.deptoId = _deptoId;
            this.deptoNombre = _deptoNombre;
            this.cencoId = _cencoId;
            this.cencoNombre = _cencoNombre;
        }

        public String getEmpresaId() {
            return empresaId;
        }

        public String getEmpresaNombre() {
            return empresaNombre;
        }

        public String getRun() {
            return run;
        }

        public String getNombreCompleto() {
            return nombreCompleto;
        }

        public String getDeptoId() {
            return deptoId;
        }

        public String getDeptoNombre() {
            return deptoNombre;
        }

        public int getCencoId() {
            return cencoId;
        }

        public String getCencoNombre() {
            return cencoNombre;
        }
            
        
        
    }
    
}
