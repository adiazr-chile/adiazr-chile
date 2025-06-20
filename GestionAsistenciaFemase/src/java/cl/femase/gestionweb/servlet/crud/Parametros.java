package cl.femase.gestionweb.servlet.crud;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.ParametroBp;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.ParametroVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Date;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import org.apache.log4j.Logger;

public class Parametros extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final String CRUD_TABLE_NAME = "Parametros";
    
    public Parametros() {
        
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

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(true);
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        ParametroBp parametrosbp = new ParametroBp(appProperties);

        if(request.getParameter("action") != null){
            System.out.println(WEB_NAME+"[ParametrosController]"
                + "action is: " + request.getParameter("action"));
            
            String action=(String)request.getParameter("action");
            
            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("PRM");
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
                   
            /** filtros de busqueda */
            String filtroEmpresaId = "";
            
            //if (jtSorting.contains("code")) jtSorting = jtSorting.replaceFirst("code","param_code");
            System.out.println(WEB_NAME + "[ParametrosController]"
                + "param busqueda "
                + "empresa_id: " + request.getParameter("filtroEmpresaId")
                + ", formulario.empresa_id: " + request.getParameter("empresaId"));
            if (request.getParameter("filtroEmpresaId") != null) 
                filtroEmpresaId  = request.getParameter("filtroEmpresaId");
            
            //Set filtros de busqueda seleccionados
            request.setAttribute("filtroEmpresaId", filtroEmpresaId);

            //objeto usado para update/insert
            ParametroVO parametro = new ParametroVO();
             
            if(request.getParameter("empresaId") != null){
                parametro.setEmpresaId(request.getParameter("empresaId"));
            }
            if(request.getParameter("id")!=null){
                parametro.setParamCode(request.getParameter("id"));
            }
            if(request.getParameter("label")!=null){
                parametro.setParamLabel(request.getParameter("label"));
            }
            if(request.getParameter("valor")!=null){
                parametro.setValor(Double.parseDouble(request.getParameter("valor")));
            }
            
            if (action.compareTo("list") == 0) {
                System.out.println(WEB_NAME + "[ParametrosController]" + "CRUD - " + CRUD_TABLE_NAME
                    + ". Mostrar registros");
                forwardToCRUDPage(request, response, parametrosbp, filtroEmpresaId);   
            }else if (action.compareTo("create") == 0) {
                    System.out.println(WEB_NAME + "[ParametrosController]CRUD - " + CRUD_TABLE_NAME
                        + ". Insertar registro");
                    
                    ResultCRUDVO doCreate = parametrosbp.insert(parametro, resultado);					
                    forwardToCRUDPage(request, response, parametrosbp, filtroEmpresaId);
            }else if (action.compareTo("update") == 0) {  
                    System.out.println(WEB_NAME + "[ParametrosController]CRUD - " + CRUD_TABLE_NAME
                        + ". Modificar registro");
                    ResultCRUDVO doUpdate = parametrosbp.update(parametro, resultado);
                    forwardToCRUDPage(request, response, parametrosbp, filtroEmpresaId);
            }else if (action.compareTo("delete") == 0) {  
                    System.out.println(WEB_NAME + "[ParametrosController]CRUD - " + CRUD_TABLE_NAME
                        + ". Eliminar registro");
                    parametro.setEmpresaId(request.getParameter("empresaIdDelete"));        
                    ResultCRUDVO doUpdate = parametrosbp.delete(parametro, resultado);
                    forwardToCRUDPage(request, response, parametrosbp, filtroEmpresaId);
            }
            
        }
    }
    
    /**
    * 
    */
    private void forwardToCRUDPage(HttpServletRequest _request, 
        HttpServletResponse _response,
        ParametroBp _auxnegocio,
        String _filtroEmpresaId){
    
        try {
            List<ParametroVO> listaObjetos = new ArrayList<>();
            System.out.println(WEB_NAME + "[ParametrosController.forwardToCRUDPage]CRUD - " + CRUD_TABLE_NAME
                + ". filtroEmpresaId: " + _filtroEmpresaId);
            if (_filtroEmpresaId != null &&
                    _filtroEmpresaId.compareTo("-1") != 0){
                listaObjetos = _auxnegocio.getParametros(_filtroEmpresaId, 0, 0, "param_label");
            }
            _request.setAttribute("lista", listaObjetos);
            RequestDispatcher vista = _request.getRequestDispatcher("cruds/parametros.jsp");
            vista.forward(_request, _response);
        } catch (ServletException ex) {
            System.err.println(WEB_NAME+"CRUD - " + CRUD_TABLE_NAME + " - "
                + "Error_1: " + ex.toString());
        } catch (IOException ex) {
            System.err.println(WEB_NAME+"CRUD - " + CRUD_TABLE_NAME + " - "
                + "Error_2: " + ex.toString());
        }
        
    }
    
}
