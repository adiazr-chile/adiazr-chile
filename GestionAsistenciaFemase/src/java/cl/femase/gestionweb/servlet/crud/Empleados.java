package cl.femase.gestionweb.servlet.crud;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.vo.FiltroBusquedaCRUDVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Date;
import java.util.StringTokenizer;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

public class Empleados extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final String CRUD_TABLE_NAME     = "Empleados";
    private static final String CRUD_SERVLET_NAME   = "[Empleados.servlet]";
    private static final String FORWARD_PAGE        = "cruds/empleados.jsp";
        
    public Empleados() {
        
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
     * @throws javax.servlet.ServletException
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

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(true);
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        EmpleadosBp claseBp = new EmpleadosBp(appProperties);

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
            String filtroCenco          = "";//Ej. 'emp01|02|80'--> empresa|deptoId|cencoId 
            String filtroRun            = "";
            String filtroNombreEmpleado = "";
            int filtroEstado           = -1;
            
            //if (jtSorting.contains("code")) jtSorting = jtSorting.replaceFirst("code","param_code");
            System.out.println(WEB_NAME + CRUD_SERVLET_NAME
                + "Param busqueda cenco: " + request.getParameter("filtroCenco")
                + ", param busqueda Run: " + request.getParameter("filtroRun")
                + ", param busqueda nombre: " + request.getParameter("filtroNombreEmpleado")
                + ", param busqueda estado: " + request.getParameter("filtroEstado")    
            );
            if (request.getParameter("filtroCenco") != null) 
                filtroCenco = request.getParameter("filtroCenco");
            if (request.getParameter("filtroRun") != null) 
                filtroRun = request.getParameter("filtroRun");
            if (request.getParameter("filtroNombre") != null) 
                filtroNombreEmpleado = request.getParameter("filtroNombre");
            if (request.getParameter("filtroEstado") != null) 
                filtroEstado = Integer.parseInt(request.getParameter("filtroEstado"));
            
            String empresaid="-1";
            String deptoid="-1";
            int cencoid=-1;
            System.out.println(WEB_NAME+ CRUD_SERVLET_NAME + "[processRequest]"
                + "token param 'cencoID'= " + filtroCenco);
            if (filtroCenco != null && filtroCenco.compareTo("-1") != 0){
                StringTokenizer tokenCenco  = new StringTokenizer(filtroCenco, "|");
                if (tokenCenco.countTokens() > 0){
                    while (tokenCenco.hasMoreTokens()){
                        empresaid   = tokenCenco.nextToken();
                        deptoid     = tokenCenco.nextToken();
                        cencoid     = Integer.parseInt(tokenCenco.nextToken());
                    }
                }
            }
            
            FiltroBusquedaCRUDVO filtroVO = new FiltroBusquedaCRUDVO();
            filtroVO.setEmpresaId(empresaid);
            filtroVO.setCencoId(cencoid);
            filtroVO.setRunEmpleado(filtroRun);
            filtroVO.setNombre(filtroNombreEmpleado);
            filtroVO.setEstado(filtroEstado);
            
            //Set filtros de busqueda seleccionados
            request.setAttribute("filtroCenco", filtroCenco);
            request.setAttribute("filtroRun", filtroRun);
            request.setAttribute("filtroNombre", filtroNombreEmpleado);
            request.setAttribute("filtroEstado", String.valueOf(filtroEstado));
           
            if (action.compareTo("list") == 0) {
                System.out.println(WEB_NAME + CRUD_SERVLET_NAME + "CRUD - " + CRUD_TABLE_NAME
                    + ". Mostrar registros");
                forwardToCRUDPage(request, response, claseBp, filtroVO);   
            }
        }
    }
    
    /**
    * 
    */
    private void forwardToCRUDPage(HttpServletRequest _request, 
        HttpServletResponse _response,
        EmpleadosBp _empleadoBp,
        FiltroBusquedaCRUDVO _filtroBusqueda){
    
        try {
            List<EmpleadoVO> listaObjetos = new ArrayList<>();
            System.out.println(WEB_NAME + CRUD_SERVLET_NAME + "CRUD - " + CRUD_TABLE_NAME
                + ". filtroNombre: " + _filtroBusqueda.getNombre()
                + ". filtroEstado: " + _filtroBusqueda.getEstado());
            
            listaObjetos = _empleadoBp.getEmpleados(_filtroBusqueda.getEmpresaId(), 
                null, 
                _filtroBusqueda.getCencoId(),
                -1,
                _filtroBusqueda.getTurnoId(),
                _filtroBusqueda.getRunEmpleado(), 
                _filtroBusqueda.getNombre(), 
                null, 
                null,
                _filtroBusqueda.getEstado(),
                0, 
                0, 
                "empl.empl_rut");
            
            _request.setAttribute("lista", listaObjetos);
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
    
}
