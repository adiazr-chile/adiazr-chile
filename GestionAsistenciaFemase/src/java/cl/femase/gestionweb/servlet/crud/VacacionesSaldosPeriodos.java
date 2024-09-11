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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
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
            
            //if (jtSorting.contains("code")) jtSorting = jtSorting.replaceFirst("code","param_code");
            System.out.println(WEB_NAME + CRUD_SERVLET_NAME
                + "param busqueda Run: " + request.getParameter("filtroRun")
                + ", searchInput param: " + request.getParameter("searchInput")    
            );
            if (request.getParameter("filtroRun") != null){ 
                filtroRun = request.getParameter("filtroRun");
                
                EmpleadoVO empleado = empleadosBp.getEmpleado(null, filtroRun);
                filtroCenco     = empleado.getCencoid();
                filtroEmpresa   = empleado.getEmpresaId();
            }
            
            FiltroBusquedaCRUDVO filtroVO = new FiltroBusquedaCRUDVO();
            filtroVO.setEmpresaId(filtroEmpresa);
            filtroVO.setCencoId(filtroCenco);
            filtroVO.setRunEmpleado(filtroRun);
            
            //Set filtros de busqueda seleccionados
            session.removeAttribute("filtroRunVACP");
            
            //session.setAttribute("filtroCencoEMPL", filtroCenco);
            session.setAttribute("filtroRunVACP", filtroRun);
            
            if (action.compareTo("list") == 0) {
                System.out.println(WEB_NAME + CRUD_SERVLET_NAME + "CRUD - " + CRUD_TABLE_NAME
                    + ". Mostrar registros");
                session.removeAttribute("lista_CRUD_saldovacperiodos");
                forwardToCRUDPage(request, response, session, vacacionesPeriodosDao, filtroVO);   
            }
        }
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
                new LinkedHashMap<String, VacacionesSaldoPeriodoVO>();
            System.out.println(WEB_NAME + CRUD_SERVLET_NAME + "CRUD - " + CRUD_TABLE_NAME
                + ". filtroEmpresaId: " + _filtroBusqueda.getEmpresaId()
                + ". filtroRunEmpleado: " + _filtroBusqueda.getRunEmpleado());
            hashMapPeriodos = _vacacionesPeriodosDao.getPeriodos(_filtroBusqueda.getEmpresaId(),_filtroBusqueda.getRunEmpleado());
            
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
    
}
