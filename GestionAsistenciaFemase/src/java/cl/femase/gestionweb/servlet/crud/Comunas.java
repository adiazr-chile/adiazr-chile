package cl.femase.gestionweb.servlet.crud;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.ComunaBp;
import cl.femase.gestionweb.vo.FiltroBusquedaCRUDVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.ComunaVO;
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

public class Comunas extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final String CRUD_TABLE_NAME     = "Comunas";
    private static final String CRUD_SERVLET_NAME   = "[Comunas.servlet]";
    private static final String FORWARD_PAGE        = "cruds/comunas.jsp";
        
    public Comunas() {
        
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

        ComunaBp claseBp = new ComunaBp(appProperties);

        if(request.getParameter("action") != null){
            System.out.println(WEB_NAME + CRUD_SERVLET_NAME
                + "action is: " + request.getParameter("action"));
            
            String action=(String)request.getParameter("action");
            
            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("COM");
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
                   
            /** filtros de busqueda */
            String filtroNombre = "";
            int filtroRegionId = -1;
            
            //if (jtSorting.contains("code")) jtSorting = jtSorting.replaceFirst("code","param_code");
            System.out.println(WEB_NAME + CRUD_SERVLET_NAME
                + "Param busqueda nombre: " + request.getParameter("filtroNombre")
                + ", param busqueda regionId: " + request.getParameter("filtroRegionId"));
            if (request.getParameter("filtroNombre") != null) 
                filtroNombre = request.getParameter("filtroNombre");
            if (request.getParameter("filtroRegionId") != null) 
                filtroRegionId = Integer.parseInt(request.getParameter("filtroRegionId"));
            
            FiltroBusquedaCRUDVO filtroVO = new FiltroBusquedaCRUDVO();
            filtroVO.setNombre(filtroNombre);
            filtroVO.setRegionId(filtroRegionId);
            //Set filtros de busqueda seleccionados
            request.setAttribute("filtroNombre", filtroNombre);
            request.setAttribute("filtroRegionId", "" + filtroRegionId);

            //objeto usado para update/insert
            ComunaVO registro = new ComunaVO();
            if(request.getParameter("id") != null){
                registro.setId(Integer.parseInt(request.getParameter("id")));
            }
            if(request.getParameter("nombre") != null){
                registro.setNombre(request.getParameter("nombre"));
            }
            if(request.getParameter("regionId") != null){
                registro.setRegionId(Integer.parseInt(request.getParameter("regionId")));
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
        ComunaBp _auxnegocio,
        FiltroBusquedaCRUDVO _filtroBusqueda){
    
        try {
            List<ComunaVO> listaObjetos = new ArrayList<>();
            System.out.println(WEB_NAME + CRUD_SERVLET_NAME + "CRUD - " + CRUD_TABLE_NAME
                + ". filtroNombre: " + _filtroBusqueda.getNombre()
                + ". filtroRegionId: " + _filtroBusqueda.getEstado());
            listaObjetos = _auxnegocio.getComunas(_filtroBusqueda.getNombre(),
                _filtroBusqueda.getRegionId(), 0, 0, "comuna_nombre");
            
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
