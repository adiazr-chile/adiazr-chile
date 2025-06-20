package cl.femase.gestionweb.servlet.crud;

import cl.femase.gestionweb.business.AsignacionDispositivoBp;
import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.CentroCostoBp;
import cl.femase.gestionweb.vo.FiltroBusquedaCRUDVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.CentroCostoVO;
import cl.femase.gestionweb.vo.DispositivoCentroCostoVO;
import cl.femase.gestionweb.vo.DispositivoVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Date;
import java.util.HashMap;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import org.apache.log4j.Logger;

public class CentrosDeCosto extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final String CRUD_TABLE_NAME     = "CentrosDeCosto";
    private static final String CRUD_SERVLET_NAME   = "[" + CRUD_TABLE_NAME + ".servlet]";
    private static final String FORWARD_PAGE        = "cruds/centros_de_costo.jsp";
    private static final String DEFAULT_SORT_COLUMN = "ccosto_nombre";
    List<CentroCostoVO> listaCencos = new ArrayList<>();    
    
    public CentrosDeCosto() {
        
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

        CentroCostoBp claseBp = new CentroCostoBp(appProperties);

        if(request.getParameter("action") != null){
            System.out.println(WEB_NAME + CRUD_SERVLET_NAME
                + "action is: " + request.getParameter("action"));
            String empresaId= userConnected.getEmpresaId();
            String action=(String)request.getParameter("action");
            
            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("ACO");
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
                   
            /** filtros de busqueda */
            String filtroDepartamentoId = "";
            String filtroNombreCenco = "";
            int filtroEstadoId = 1;
            
            System.out.println(WEB_NAME + CRUD_SERVLET_NAME
                + "param busqueda "
                + "'filtroDepartamentoId': " + request.getParameter("filtroDepartamentoId")
                + ", 'filtroNombreCenco': " + request.getParameter("filtroNombreCenco")
                + ", 'filtroEstadoId': " + request.getParameter("filtroEstadoId"));
            if (request.getParameter("filtroDepartamentoId") != null) 
                filtroDepartamentoId = request.getParameter("filtroDepartamentoId");
            if (request.getParameter("filtroNombreCenco") != null) 
                filtroNombreCenco = request.getParameter("filtroNombreCenco");
            if (request.getParameter("filtroEstadoId") != null) 
                filtroEstadoId = Integer.parseInt(request.getParameter("filtroEstadoId"));
            
            //seteo filtros de busqueda de registros
            FiltroBusquedaCRUDVO filtroVO = new FiltroBusquedaCRUDVO();
            filtroVO.setEmpresaId(empresaId);
            filtroVO.setDepartamentoId(filtroDepartamentoId);
            filtroVO.setNombreCenco(filtroNombreCenco);
            filtroVO.setEstado(filtroEstadoId);
            
            //Set filtros de busqueda seleccionados
            request.setAttribute("filtroEmpresaId", empresaId);
            request.setAttribute("filtroDepartamentoId", filtroDepartamentoId);
            request.setAttribute("filtroNombreCenco", filtroNombreCenco);
            request.setAttribute("filtroEstadoId", filtroEstadoId);
            
            //objeto usado para update/insert
            CentroCostoVO registro = new CentroCostoVO();
            
            if(request.getParameter("id") != null){
                registro.setId(Integer.parseInt(request.getParameter("id")));
            }
            if(request.getParameter("deptoId") != null){
                registro.setDeptoId(request.getParameter("deptoId"));
            }
            if(request.getParameter("nombre") != null){
                registro.setNombre(request.getParameter("nombre"));
            }
            if(request.getParameter("estado") != null){
                registro.setEstado(Integer.parseInt(request.getParameter("estado")));
            }
            if(request.getParameter("direccion") != null){
                registro.setDireccion(request.getParameter("direccion"));
            }
            if(request.getParameter("comunaId") != null){
                registro.setComunaId(Integer.parseInt(request.getParameter("comunaId")));
            }
            if(request.getParameter("telefonos") != null){
                registro.setTelefonos(request.getParameter("telefonos"));
            }
            if(request.getParameter("email") != null){
                registro.setEmail(request.getParameter("email"));
            }
            if(request.getParameter("emailNotificacion") != null){
                registro.setEmailNotificacion(request.getParameter("emailNotificacion"));
            }
            if(request.getParameter("zonaExtrema") != null){
                registro.setZonaExtrema(request.getParameter("zonaExtrema"));
            }
            
            /* seteo desde obtejos checkbox
            String[] dispositivos = request.getParameterValues("dispositivo");
            if (dispositivos != null){
                for (int x = 0; x < dispositivos.length; x++){
                    System.out.println(WEB_NAME+"dispositivo seleccionado[" + x + "] = " + dispositivos[x]);
                    DispositivoCentroCostoVO newCenco = 
                        new DispositivoCentroCostoVO(dispositivos[x],registro.getId());
                    asignacionBp.insertAsignacionCentroCosto(newCenco, resultado);
                }
            }
            */
            //--------------------
            if (action.compareTo("list") == 0) {
                System.out.println(WEB_NAME + CRUD_SERVLET_NAME + "CRUD - " + CRUD_TABLE_NAME
                    + ". Mostrar registros");
                
                forwardToCRUDPage(request, response, claseBp, filtroVO);
                /*
                1.- Rescatar todos los cencos del depto seleccionado
                2.- Por cada cenco
                       - rescatar los dispositivos asignados
                       - llenar hashmap.key=cenco_id, hashmap.value=dispositivo_id        
                */
                HashMap<Integer,List<DispositivoVO>> hashDispositivos = new HashMap<>(); 
                for (int i = 0; i < listaCencos.size(); i++) {
                    CentroCostoVO cenco = listaCencos.get(i);
                    List<DispositivoVO> devicesCc = claseBp.getDispositivosAsignados(cenco.getId());
                    hashDispositivos.put(cenco.getId(), devicesCc);
                }
                request.setAttribute("dispositivosAsignados", hashDispositivos);
                
            }else if (action.compareTo("create") == 0) {
                    System.out.println(WEB_NAME + CRUD_SERVLET_NAME + "CRUD - " + CRUD_TABLE_NAME
                        + ". Insertar registro");
                    
                    ResultCRUDVO doCreate = claseBp.insert(registro, resultado);					
                    forwardToCRUDPage(request, response, claseBp, filtroVO);
            }else if (action.compareTo("update") == 0) {  
                    System.out.println(WEB_NAME + CRUD_SERVLET_NAME + "CRUD - " + CRUD_TABLE_NAME
                        + ". Modificar registro");
                    ResultCRUDVO doUpdate = claseBp.update(registro, resultado);
                    //insertar asignacion de dispotivos al cenco
                    AsignacionDispositivoBp asignacionBp = new AsignacionDispositivoBp(appProperties);
                    String[] dispositivos = request.getParameterValues("dispositivo");
                    
                    if (dispositivos != null && dispositivos.length > 0) {
                        asignacionBp.deleteAsignacionesCentroCosto(registro.getId(), resultado);
                        for (int x = 0; x < dispositivos.length; x++){
                            System.out.println(WEB_NAME + CRUD_SERVLET_NAME + "CRUD - " + CRUD_TABLE_NAME 
                                + "dispositivo seleccionado[" + x + "] = " + dispositivos[x]);
                            DispositivoCentroCostoVO newCenco = new DispositivoCentroCostoVO(dispositivos[x], registro.getId());
                            asignacionBp.insertAsignacionCentroCosto(newCenco, resultado);
                        }
                    } else {
                        System.err.println(WEB_NAME + CRUD_SERVLET_NAME + "CRUD - " + CRUD_TABLE_NAME 
                            + "No se han seleccionado dispositivos");
                    }
                    forwardToCRUDPage(request, response, claseBp, filtroVO);
            }
        }
    }
    
    /**
    * 
    */
     private void forwardToCRUDPage(HttpServletRequest _request, 
        HttpServletResponse _response,
        CentroCostoBp _auxnegocio,
        FiltroBusquedaCRUDVO _filtroBusqueda){
    
        try {
            
            System.out.println(WEB_NAME + CRUD_SERVLET_NAME + "CRUD - " + CRUD_TABLE_NAME + " - "
                + "Listar cencos");
            listaCencos = _auxnegocio.getCentrosCosto(_filtroBusqueda.getDepartamentoId(),
                _filtroBusqueda.getNombreCenco(), _filtroBusqueda.getEstado(),
                0, 0, DEFAULT_SORT_COLUMN);
            
            _request.setAttribute("lista", listaCencos);
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
