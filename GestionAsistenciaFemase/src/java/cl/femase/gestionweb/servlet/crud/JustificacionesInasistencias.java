package cl.femase.gestionweb.servlet.crud;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.DetalleAusenciaBp;
import cl.femase.gestionweb.vo.DetalleAusenciaVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Date;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

public class JustificacionesInasistencias extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final String CRUD_TABLE_NAME     = "Justificaciones Inasistencia";
    private static final String CRUD_SERVLET_NAME   = "[JustificacionesInasistencias.servlet]";
    private static final String FORWARD_PAGE        = "cruds/justificacion_inasistencias.jsp";
    private static final String ATTRIBUTE_NAME_FOR_LIST= "lista_CRUD_detalle_ausencias";
    
    public JustificacionesInasistencias() {
        
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

        DetalleAusenciaBp claseBp = new DetalleAusenciaBp(appProperties);

        if(request.getParameter("action") != null){
            System.out.println(WEB_NAME + CRUD_SERVLET_NAME
                + "action is: " + request.getParameter("action"));
            
            String action=(String)request.getParameter("action");
            
            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("DAU");
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
                   
            /** filtros de busqueda */
            String runEmpleado          = null;
            String fechaInicioAusencia  = null; 
            String fechaFinAusencia    = null;
            int tipoAusencia           = -1;
                
            //if (jtSorting.contains("code")) jtSorting = jtSorting.replaceFirst("code","param_code");
            System.out.println(WEB_NAME + CRUD_SERVLET_NAME
                + "Params busqueda. runEmpleado: " + request.getParameter("runEmpleado")
                + ", inicio ausencia: " + request.getParameter("inicioAusencia")
                + ", fin ausencia: " + request.getParameter("finAusencia")
                + ", tipo ausencia: " + request.getParameter("tipoAusencia")    
            );
            if (request.getParameter("runEmpleado") != null) 
                runEmpleado = request.getParameter("runEmpleado");
            if (request.getParameter("inicioAusencia") != null) 
                fechaInicioAusencia = request.getParameter("inicioAusencia");
            if (request.getParameter("finAusencia") != null) 
                fechaFinAusencia = request.getParameter("finAusencia");
            if (request.getParameter("tipoAusencia") != null) 
                tipoAusencia = Integer.parseInt(request.getParameter("tipoAusencia"));
            
            //Set filtros de busqueda seleccionados
            request.setAttribute("filtroRunEmpleadoDetAusencia", runEmpleado);
            request.setAttribute("filtroInicioAusenciaDetAusencia", fechaInicioAusencia);
            request.setAttribute("filtroFinAusenciaDetAusencia", fechaFinAusencia);
            request.setAttribute("filtroTipoAusenciaDetAusencia", String.valueOf(tipoAusencia));
           
            if (action.compareTo("list") == 0) {
                System.out.println(WEB_NAME + CRUD_SERVLET_NAME + "CRUD - " + CRUD_TABLE_NAME
                    + ". Mostrar registros");
                request.removeAttribute(ATTRIBUTE_NAME_FOR_LIST);
                forwardToCRUDPage(request, response, session, claseBp, 
                    runEmpleado,fechaInicioAusencia,fechaFinAusencia,tipoAusencia);   
            }
        }
    }
    
    /**
    * 
    */
    private void forwardToCRUDPage(HttpServletRequest _request,
        HttpServletResponse _response,
        HttpSession _session,
        DetalleAusenciaBp _ausenciasBp,
        String _runEmpleado,
        String _fechaInicioAusencia, 
        String _fechaFinAusencia,
        int _tipoAusencia){
    
        try {
            List<DetalleAusenciaVO> lista = new ArrayList<>();
            System.out.println(WEB_NAME + CRUD_SERVLET_NAME + "CRUD - " + CRUD_TABLE_NAME
                + ". filtroRun: " + _runEmpleado
                + ", filtro fecha inicio ausencia: " + _fechaInicioAusencia
                + ", filtro fecha fin ausencia: " + _fechaFinAusencia
                + ", filtro tipo ausencia: " + _tipoAusencia);
            if (_runEmpleado.compareTo("-1") == 0 ){
                System.out.println("--------->Eliminar lista de Justificaciones ausencia de la sesion<---------");
                _request.removeAttribute(ATTRIBUTE_NAME_FOR_LIST);
                lista = null;
            }else{
                lista = _ausenciasBp.getDetallesAusencias("detalle_ausencias",
                    _runEmpleado,
                    null, 
                    _fechaInicioAusencia, 
                    _fechaFinAusencia,
                    _tipoAusencia,
                    0, 
                    0, 
                    "detalle_ausencia.correlativo");
            }
            
            _request.setAttribute(ATTRIBUTE_NAME_FOR_LIST, lista);
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
