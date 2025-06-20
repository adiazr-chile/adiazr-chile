package cl.femase.gestionweb.servlet.crud;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.DepartamentoBp;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.DepartamentoVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import java.util.Date;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import org.apache.log4j.Logger;

public class Departamento extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 994L;
    private static final String CRUD_TABLE_NAME = "Departamentos";
    public Departamento() {
        
    }

    /**
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
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
    */
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(true);
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
        DepartamentoBp auxnegocio=new DepartamentoBp(appProperties);

        if(request.getParameter("action") != null){
            System.out.println(WEB_NAME+"---->action is: " + request.getParameter("action"));
            
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            response.setContentType("application/json");

            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("ADE");
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
                   
            /** filtros de busqueda */
            String filtroNombre      = "";
            String filtroEmpresaId   = "";
            
            if (request.getParameter("filtroNombre") != null) 
                filtroNombre  = request.getParameter("filtroNombre");
            if (request.getParameter("filtroEmpresaId") != null) 
                filtroEmpresaId = request.getParameter("filtroEmpresaId");
            
            //Set filtros de busqueda seleccionados
            request.setAttribute("filtroEmpresaId", filtroEmpresaId);
            request.setAttribute("filtroNombre", filtroNombre);
            
            System.out.println(WEB_NAME+"CRUD - " + CRUD_TABLE_NAME + " - "
                + "filtroEmpresaId: " + filtroEmpresaId
                + ", filtroNombre: " + filtroNombre);
            
            //objeto usado para update/insert
            DepartamentoVO auxdata = new DepartamentoVO();
             
            if(request.getParameter("id")!=null){
                auxdata.setId(request.getParameter("id"));
            }
            if(request.getParameter("nombre")!=null){
                auxdata.setNombre(request.getParameter("nombre"));
            }
            if (request.getParameter("empresaId") != null && 
                    request.getParameter("empresaId").compareTo("-1") != 0){
                auxdata.setEmpresaId(request.getParameter("empresaId"));
                session.setAttribute("empresaId", request.getParameter("empresaId"));
            }
            if(request.getParameter("estado")!=null){
                auxdata.setEstado(Integer.parseInt(request.getParameter("estado")));
            }
                        
            if (action.compareTo("list")==0) {
                System.out.println(WEB_NAME+"CRUD - Departamentos - "
                    + "Listar " + CRUD_TABLE_NAME + "...");
                forwardToCRUDPage(request, response, auxnegocio, filtroEmpresaId, filtroNombre);
            }else if (action.compareTo("create") == 0) {
                    System.out.println(WEB_NAME+"CRUD - " + CRUD_TABLE_NAME + " - Insertar Depto...");
                    ResultCRUDVO doCreate = auxnegocio.insert(auxdata, resultado);	
                    
                    forwardToCRUDPage(request, response, auxnegocio, auxdata.getEmpresaId(), filtroNombre);
            }else if (action.compareTo("update") == 0) {  
                    System.out.println(WEB_NAME+"CRUD - " + CRUD_TABLE_NAME + " - Actualizar Depto...");
                    ResultCRUDVO doUpdate = auxnegocio.update(auxdata, resultado);
                    forwardToCRUDPage(request, response, auxnegocio, filtroEmpresaId, filtroNombre);
            }else if (action.compareTo("delete") == 0) {  
                    //Delete record primary key
                    auxdata.setEmpresaId(request.getParameter("empresaIdDelete"));
                    auxdata.setId(request.getParameter("idDelete"));
                    
                    System.out.println(WEB_NAME+"CRUD- Eliminando Depto- "
                        + "empresaId: " + auxdata.getEmpresaId()
                        + ", Id depto: " + auxdata.getId()
                        +", nombre: "+ auxdata.getNombre());
                    try{
                        auxnegocio.delete(auxdata, resultado);
                        forwardToCRUDPage(request, response, auxnegocio, auxdata.getEmpresaId(), filtroNombre);
                    }catch(Exception ex){
                        String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getStackTrace().toString()+"}";
                        response.getWriter().print(error);
                    }
            }
        }
    }

    /**
    * 
    */
    private void forwardToCRUDPage(HttpServletRequest _request, 
        HttpServletResponse _response,
        DepartamentoBp _auxnegocio,
        String _filtroEmpresaId,
        String _filtroNombre){
    
        try {
            List<DepartamentoVO> listaDeptos = new ArrayList<>();
            
            if (_filtroEmpresaId != null &&
                    _filtroEmpresaId.compareTo("-1") != 0){
                listaDeptos = _auxnegocio.getDepartamentos(_filtroEmpresaId,
                    _filtroNombre, 0, 0, "depto_nombre");
            }
            _request.setAttribute("lista", listaDeptos);
            RequestDispatcher vista = _request.getRequestDispatcher("cruds/departamentos.jsp");
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
