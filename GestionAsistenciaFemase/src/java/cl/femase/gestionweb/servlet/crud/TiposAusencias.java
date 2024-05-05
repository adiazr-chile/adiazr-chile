package cl.femase.gestionweb.servlet.crud;

import cl.femase.gestionweb.business.AusenciaBp;
import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.vo.AusenciaVO;
import cl.femase.gestionweb.vo.FiltroBusquedaCRUDVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.TipoAusenciaVO;
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

public class TiposAusencias extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final String CRUD_TABLE_NAME     = "TiposAusencias";
    private static final String CRUD_SERVLET_NAME   = "[TiposAusencias.servlet]";
    private static final String FORWARD_PAGE        = "cruds/tipos_ausencias.jsp";
        
    public TiposAusencias() {
        
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

        AusenciaBp claseBp = new AusenciaBp(appProperties);

        if(request.getParameter("action") != null){
            System.out.println(WEB_NAME + CRUD_SERVLET_NAME
                + "action is: " + request.getParameter("action"));
            
            String action=(String)request.getParameter("action");
            
            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("AUS");
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
                   
            /** filtros de busqueda */
            /** filtros de busqueda */
            String filtroNombre = "";
            String filtroTipo  = "-1";
            String filtroEstado= "-1";
            String filtroJustificaHoras     = "S";
            String filtroPagadaPorEmpleador = "S";
            
            //if (jtSorting.contains("code")) jtSorting = jtSorting.replaceFirst("code","param_code");
            System.out.println(WEB_NAME + CRUD_SERVLET_NAME
                + "param busqueda nombre: " + request.getParameter("filtroNombre"));
            if (request.getParameter("filtroNombre") != null) 
                filtroNombre  = request.getParameter("filtroNombre");
            if (request.getParameter("filtroTipo") != null) 
                filtroTipo  = request.getParameter("filtroTipo");
            if (request.getParameter("filtroEstado") != null) 
                filtroEstado  = request.getParameter("filtroEstado");
            if (request.getParameter("filtroJustificaHoras") != null) 
                filtroJustificaHoras  = request.getParameter("filtroJustificaHoras");
            if (request.getParameter("filtroPagadaPorEmpleador") != null) 
                filtroPagadaPorEmpleador  = request.getParameter("filtroPagadaPorEmpleador");
            
            FiltroBusquedaCRUDVO filtroVO = new FiltroBusquedaCRUDVO();
            filtroVO.setNombre(filtroNombre);
            filtroVO.setTipo(filtroTipo);
            filtroVO.setEstado(Integer.parseInt(filtroEstado));
            filtroVO.setJustificaHrs(filtroJustificaHoras);
            filtroVO.setPagadaPorEmpleador(filtroPagadaPorEmpleador);
            
            //Set filtros de busqueda seleccionados
            request.setAttribute("filtroNombre", filtroNombre);
            request.setAttribute("filtroTipo", filtroTipo);
            request.setAttribute("filtroEstado", filtroEstado);
            request.setAttribute("filtroJustificaHoras", filtroJustificaHoras);
            request.setAttribute("filtroPagadaPorEmpleador", filtroPagadaPorEmpleador);

            //objeto usado para update/insert
            AusenciaVO registro = new AusenciaVO();
            if (request.getParameter("id") != null && request.getParameter("id").compareTo("") != 0){
                registro.setId(Integer.parseInt(request.getParameter("id")));
            }
            if(request.getParameter("nombre")!=null){
                registro.setNombre(request.getParameter("nombre"));
            }
            if(request.getParameter("nombreCorto")!=null){
                registro.setNombreCorto(request.getParameter("nombreCorto"));
            }
            if(request.getParameter("tipoId")!=null){
                registro.setTipoId(Integer.parseInt(request.getParameter("tipoId")));
            }
            if (request.getParameter("estado")!=null){
                registro.setEstado(Integer.parseInt(request.getParameter("estado")));
            }
            if (request.getParameter("justificaHoras") != null){
                registro.setJustificaHoras(request.getParameter("justificaHoras"));
            }
            if (request.getParameter("pagadaPorEmpleador") != null){
                registro.setPagadaPorEmpleador(request.getParameter("pagadaPorEmpleador"));
            }
                       
            if (action.compareTo("list") == 0) {
                System.out.println(WEB_NAME + CRUD_SERVLET_NAME + "CRUD - " + CRUD_TABLE_NAME
                    + ". Mostrar registros");
                forwardToCRUDPage(request, response, claseBp, filtroVO);   
            }else if (action.compareTo("create") == 0) {
                    System.out.println(WEB_NAME + CRUD_SERVLET_NAME + "CRUD - " + CRUD_TABLE_NAME
                        + ". Insertar registro");
                    ResultCRUDVO doCreate = claseBp.insert(registro, resultado);					
                    forwardToCRUDPage(request, response, claseBp, filtroVO);
            }else if (action.compareTo("update") == 0) {  
                    System.out.println(WEB_NAME + CRUD_SERVLET_NAME + "CRUD - " + CRUD_TABLE_NAME
                        + ". Modificar registro");
                    ResultCRUDVO doUpdate = claseBp.update(registro, resultado);
                    forwardToCRUDPage(request, response, claseBp, filtroVO);
            }
        }
    }
    
    /**
    * 
    */
    private void forwardToCRUDPage(HttpServletRequest _request, 
        HttpServletResponse _response,
        AusenciaBp _auxnegocio,
        FiltroBusquedaCRUDVO _filtroBusqueda){
    
        try {
            List<AusenciaVO> listaObjetos = new ArrayList<>();
            System.out.println(WEB_NAME 
                + CRUD_SERVLET_NAME 
                + "CRUD - " 
                + CRUD_TABLE_NAME
                + ". filtroNombre: " + _filtroBusqueda.getNombre()
                + ". filtroTipo: " + _filtroBusqueda.getTipo()
                + ". filtroEstado: " + _filtroBusqueda.getEstado()
                + ". filtroJustificaHrs: " + _filtroBusqueda.getJustificaHrs()
                + ". filtroPagadaPorEmpleador: " + _filtroBusqueda.getPagadaPorEmpleador());
            
            listaObjetos = _auxnegocio.getAusencias(_filtroBusqueda.getNombre(),
                Integer.parseInt(_filtroBusqueda.getTipo()),
                _filtroBusqueda.getEstado(),
                _filtroBusqueda.getJustificaHrs(),
                _filtroBusqueda.getPagadaPorEmpleador(),
                0, 0, "ausencia_nombre");
            
            _request.setAttribute("lista", listaObjetos);
            RequestDispatcher vista = _request.getRequestDispatcher(FORWARD_PAGE);
            vista.forward(_request, _response);
        } catch (ServletException ex) {
            System.err.println(WEB_NAME + CRUD_SERVLET_NAME + "CRUD - " + CRUD_TABLE_NAME + " - "
                + "Error_1: " + ex.toString());
        } catch (IOException ex) {
            System.err.println(WEB_NAME + CRUD_SERVLET_NAME + "CRUD - " + CRUD_TABLE_NAME + " - "
                + "Error_2: " + ex.toString());
        }
        
    }
    
}
