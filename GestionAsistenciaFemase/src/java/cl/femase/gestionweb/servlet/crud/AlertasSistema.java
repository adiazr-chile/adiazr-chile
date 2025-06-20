package cl.femase.gestionweb.servlet.crud;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.AlertaSistemaBp;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.AlertaSistemaVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import java.time.LocalDate;
import java.util.Date;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import org.apache.log4j.Logger;

public class AlertasSistema extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 994L;
    private static final String CRUD_TABLE_NAME = "Alertas Sistema";
    public AlertasSistema() {
        
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
        AlertaSistemaBp auxnegocio=new AlertaSistemaBp(appProperties);
        LocalDate fechaActual = LocalDate.now();
        
        if(request.getParameter("action") != null){
            System.out.println(WEB_NAME+"---->action is: " + request.getParameter("action"));
            
            String action=(String)request.getParameter("action");
            String inputPrefix = "create";
            
            if (action != null && action.compareTo("update")==0)
                inputPrefix = "edit";
            
            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("SYSALERT");
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
                   
            /** filtros de busqueda */
            String filtroTitulo   = "";
            String filtroFecha   = fechaActual.toString();
            
            if (request.getParameter("titulo") != null) 
                filtroTitulo = request.getParameter("titulo");
            if (request.getParameter("fecha") != null) 
                filtroFecha = request.getParameter("fecha");
            
            //Set filtros de busqueda seleccionados
            request.setAttribute("filtroTitulo", filtroTitulo);
            request.setAttribute("filtroFecha", filtroFecha);
            
            System.out.println(WEB_NAME+"CRUD - " + CRUD_TABLE_NAME + " - "
                + "filtros de busqueda-->titulo: " + filtroTitulo
                + ", fecha: " + filtroTitulo);
            
            //objeto usado para update/insert
            AlertaSistemaVO alertaSistema = new AlertaSistemaVO();
             
            alertaSistema.setEmpresaId(userConnected.getEmpresaId());
            session.setAttribute("empresaId", userConnected.getEmpresaId());
                
            if (request.getParameter(inputPrefix+"Id") != null){
                alertaSistema.setIdAlerta(Integer.parseInt(request.getParameter(inputPrefix+"Id")));
            }
            if (request.getParameter(inputPrefix+"Titulo") != null){
                alertaSistema.setTitulo(request.getParameter(inputPrefix+"Titulo"));
            }
            
            if (request.getParameter(inputPrefix+"Mensaje") != null){
                alertaSistema.setMensaje(request.getParameter(inputPrefix+"Mensaje"));
            }
            if (request.getParameter(inputPrefix+"Tipo") != null){
                alertaSistema.setTipo(request.getParameter(inputPrefix+"Tipo"));
            }
            String strFecha = "";
            String strHora = "";
            if (request.getParameter(inputPrefix+"DesdeFecha") != null){
                strFecha = request.getParameter(inputPrefix+"DesdeFecha");
                strHora = request.getParameter(inputPrefix+"DesdeHora");
                alertaSistema.setFechaHoraDesde(strFecha + " " + strHora);
            }
            if (request.getParameter(inputPrefix+"HastaFecha") != null){
                strFecha = request.getParameter(inputPrefix+"HastaFecha");
                strHora = request.getParameter(inputPrefix+"HastaHora");
                alertaSistema.setFechaHoraHasta(strFecha + " " + strHora);
            }
            if (request.getParameter(inputPrefix+"Estado") != null){
                alertaSistema.setEstado(request.getParameter(inputPrefix+"Estado"));
            }
            alertaSistema.setCreadoPor(userConnected.getUsername());
            alertaSistema.setModificadoPor(userConnected.getUsername());
            
            System.out.println(WEB_NAME+"CRUD - " + CRUD_TABLE_NAME + " - "
                + "Valores recibidos. "
                + "IdAlerta: " + alertaSistema.getIdAlerta()
                + ", titulo: " + alertaSistema.getTitulo()
                + ", mensaje: " + alertaSistema.getMensaje()
                + ", tipo: " + alertaSistema.getTipo()
                + ", fechaHoraDesde: " + alertaSistema.getFechaHoraDesde()
                + ", fechaHoraHasta: " + alertaSistema.getFechaHoraHasta()
                + ", estado: " + alertaSistema.getEstado()    
            );
            
            if (action.compareTo("list")==0) {
                System.out.println(WEB_NAME+"CRUD - Alertas Sistema - "
                    + "Listar " + CRUD_TABLE_NAME + "...");
                forwardToCRUDPage(request, 
                    response, 
                    auxnegocio, 
                    alertaSistema.getEmpresaId(),
                    filtroTitulo, 
                    filtroFecha);
            }else if (action.compareTo("create") == 0) {
                    System.out.println(WEB_NAME+"CRUD - " + CRUD_TABLE_NAME + " - Insertar Alerta Sistema...");
                    ResultCRUDVO doCreate = auxnegocio.insert(alertaSistema, resultado);	
                    
                    forwardToCRUDPage(request, 
                        response, 
                        auxnegocio, 
                        alertaSistema.getEmpresaId(),
                        filtroTitulo, 
                        filtroFecha);
            }else if (action.compareTo("update") == 0) {  
                    System.out.println(WEB_NAME+"CRUD - " + CRUD_TABLE_NAME + " - Modifica Alerta Sistema...");
                    ResultCRUDVO doUpdate = auxnegocio.update(alertaSistema, resultado);
                    forwardToCRUDPage(request, 
                        response, 
                        auxnegocio, 
                        alertaSistema.getEmpresaId(),
                        filtroTitulo, 
                        filtroFecha);
            }else if (action.compareTo("delete") == 0) {  
                    //Delete record primary key
                    alertaSistema.setEmpresaId(userConnected.getEmpresaId());
                    alertaSistema.setIdAlerta(Integer.parseInt(request.getParameter("idDelete")));
                    
                    System.out.println(WEB_NAME+"CRUD- Eliminando Alerta Sistema- "
                        + "empresaId: " + alertaSistema.getEmpresaId()
                        + ", Id alerta: " + alertaSistema.getIdAlerta()
                        +", titulo: "+ alertaSistema.getTitulo());
                    try{
                        auxnegocio.delete(alertaSistema, resultado);
                        forwardToCRUDPage(request, 
                            response, 
                            auxnegocio, 
                            alertaSistema.getEmpresaId(),
                            filtroTitulo, 
                            filtroFecha);
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
        AlertaSistemaBp _auxnegocio,
        String _filtroEmpresa,
        String _filtroTitulo, 
        String _filtroFecha){
    
        try {
            System.out.println(WEB_NAME+"CRUD- forwardToCRUDPage-listar "
                + "empresaId: " + _filtroEmpresa
                + ", titulo: " + _filtroTitulo
                +", fecha: "+ _filtroFecha);
            
            List<AlertaSistemaVO> listaAlertas = 
                _auxnegocio.getAlertas(_filtroEmpresa, 
                    _filtroTitulo, 
                    _filtroFecha);
       
            _request.setAttribute("lista", listaAlertas);
            RequestDispatcher vista = _request.getRequestDispatcher("cruds/alertas_sistema.jsp");
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
