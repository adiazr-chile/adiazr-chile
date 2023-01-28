package cl.femase.gestionweb.servlet.mantencion;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.AsignacionDispositivoBp;
import cl.femase.gestionweb.business.DispositivoBp;
import cl.femase.gestionweb.vo.DispositivoCentroCostoVO;
import cl.femase.gestionweb.vo.DispositivoDepartamentoVO;
import cl.femase.gestionweb.vo.DispositivoEmpresaVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.DispositivoVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import java.util.Date;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

public class DispositivosController extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 991L;
    
    public DispositivosController() {
        
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

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(true);
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        DispositivoBp auxnegocio=new DispositivoBp(appProperties);
        AsignacionDispositivoBp asignacionBp = new AsignacionDispositivoBp(appProperties);
        
        if(request.getParameter("action") != null){
            System.out.println(WEB_NAME+"\n[DispositivosController]"
                + "action is: " + request.getParameter("action"));
            List<DispositivoVO> listaObjetos = new ArrayList<>();
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            response.setContentType("application/json");

            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("DIS");
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
                   
            //Fetch Data from User Table
            int startPageIndex      = 0;
            int numRecordsPerPage   = 10;
            String jtSorting        = "type_id asc";
            /** filtros de busqueda */
            String buscarIdEmpresa = "";
            String buscarIdDepto = "";
            int buscarIdCenco = -1;
            int buscarIdEstado = -1;
            int buscarTipo = -1;
            String buscarFechaIngreso = "";
            
            if (request.getParameter("jtStartIndex") != null) 
                startPageIndex = Integer.parseInt(request.getParameter("jtStartIndex"));
            if (request.getParameter("jtPageSize") != null) 
                numRecordsPerPage   = Integer.parseInt(request.getParameter("jtPageSize"));
            if (request.getParameter("jtSorting") != null) 
                jtSorting   = request.getParameter("jtSorting");
  
            System.out.println(WEB_NAME+"cl.femase.gestionweb.servlet."
                + "mantencion.DispositivosController."
                + "processRequest(). jtSorting antes: "+jtSorting);
            
            if (jtSorting.compareTo("id ASC") == 0) jtSorting = "device_id ASC";
            else if (jtSorting.compareTo("id DESC")==0) jtSorting = "device_id DESC";
            else if (jtSorting.compareTo("idTipo ASC") == 0) jtSorting = "type_id ASC";
            else if (jtSorting.compareTo("idTipo DESC")==0) jtSorting = "type_id DESC";
            else if (jtSorting.compareTo("idEmpresa ASC") == 0) jtSorting = "id_empresa ASC";
            else if (jtSorting.compareTo("idEmpresa DESC")==0) jtSorting = "id_empresa DESC";
            else if (jtSorting.compareTo("idDepto ASC") == 0) jtSorting = "id_depto ASC";
            else if (jtSorting.compareTo("idDepto DESC")==0) jtSorting = "id_depto DESC";
            else if (jtSorting.compareTo("idCenco ASC") == 0) jtSorting = "id_cenco ASC";
            else if (jtSorting.compareTo("idCenco DESC")==0) jtSorting = "id_cenco DESC";
            else if (jtSorting.compareTo("fechaIngresoAsStr ASC") == 0) jtSorting = "fecha_ingreso ASC";
            else if (jtSorting.compareTo("fechaIngresoAsStr DESC")==0) jtSorting = "fecha_ingreso DESC";
            else if (jtSorting.contains("firmwareVersion")) jtSorting = jtSorting.replaceFirst("firmwareVersion","firmware_version");
            else if (jtSorting.contains("ip")) jtSorting = jtSorting.replaceFirst("ip","direccion_ip");
            
            
            //else if (jtSorting.contains("idTipo")) jtSorting = jtSorting.replaceFirst("idTipo","type_id");
            //else if (jtSorting.contains("idEmpresa")) jtSorting = jtSorting.replaceFirst("idEmpresa","id_empresa");
            //else if (jtSorting.contains("idDepto")) jtSorting = jtSorting.replaceFirst("idDepto","id_depto");
            //else if (jtSorting.contains("idCenco")) jtSorting = jtSorting.replaceFirst("idCenco","id_cenco");
            //else if (jtSorting.contains("fechaIngresoAsStr")) jtSorting = jtSorting.replaceFirst("fechaIngresoAsStr","fecha_ingreso");
            
            System.out.println(WEB_NAME+"cl.femase.gestionweb.servlet."
                + "mantencion.DispositivosController."
                + "processRequest(). jtSorting despues: "+jtSorting);
            
            //objeto usado para update/insert
            DispositivoVO auxdata = new DispositivoVO();
            
            if (request.getParameter("filtroEmpresa") != null) {
                buscarIdEmpresa  = request.getParameter("filtroEmpresa");
            }
            if (request.getParameter("filtroDepto") != null) {
                buscarIdDepto  = request.getParameter("filtroDepto");
            }
            if (request.getParameter("filtroTipo") != null) {
                buscarTipo  = Integer.parseInt(request.getParameter("filtroTipo"));
            }
            if (request.getParameter("filtroCenco") != null) {
                buscarIdCenco  = Integer.parseInt(request.getParameter("filtroCenco"));
            }
            if (request.getParameter("filtroEstado") != null) {
                buscarIdEstado  = Integer.parseInt(request.getParameter("filtroEstado"));
            }
            if (request.getParameter("filtroFechaIngreso") != null) {
                buscarFechaIngreso  = request.getParameter("filtroFechaIngreso");
            }
           
            if (request.getParameter("id") != null){ 
                auxdata.setId(request.getParameter("id"));
            }
            if (request.getParameter("idTipo") != null){ 
                auxdata.setIdTipo(Integer.parseInt(request.getParameter("idTipo")));
            }
            if (request.getParameter("estado") != null){ 
                auxdata.setEstado(Integer.parseInt(request.getParameter("estado")));
            }
            if (request.getParameter("fechaIngresoAsStr") != null){ 
                auxdata.setFechaIngresoAsStr(request.getParameter("fechaIngresoAsStr"));
            }
            if (request.getParameter("ubicacion") != null){ 
                auxdata.setUbicacion(request.getParameter("ubicacion"));
            }
            
            /*
            private String modelo="";
            private String fabricante="";
            private String firmwareVersion="";
            private String ip="";
            private String gateway="";
            private String dns="";
            */
            if (request.getParameter("modelo") != null){ 
                auxdata.setModelo(request.getParameter("modelo"));
            }
            
            if (request.getParameter("fabricante") != null){ 
                auxdata.setFabricante(request.getParameter("fabricante"));
            }
            
            if (request.getParameter("firmwareVersion") != null){ 
                auxdata.setFirmwareVersion(request.getParameter("firmwareVersion"));
            }
            
            if (request.getParameter("ip") != null){ 
                auxdata.setIp(request.getParameter("ip"));
            }
            
            if (request.getParameter("gateway") != null){ 
                auxdata.setGateway(request.getParameter("gateway"));
            }
            
            if (request.getParameter("dns") != null){ 
                auxdata.setDns(request.getParameter("dns"));
            }
            
            if (request.getParameter("direccion") != null){ 
                auxdata.setDireccion(request.getParameter("direccion"));
            }
            
            if (request.getParameter("idComuna") != null){ 
                auxdata.setIdComuna(Integer.parseInt(request.getParameter("idComuna")));
            }
            
            if (action.compareTo("list")==0) {
                System.out.println(WEB_NAME+"[DispositivosController]"
                    + "Mantenedor - Dispositivos - "
                    + "mostrando registros...");
                try{
                    
                    listaObjetos = 
                        auxnegocio.getDispositivos(null,buscarTipo,
                            buscarIdEstado,
                            buscarFechaIngreso,
                            startPageIndex, 
                            numRecordsPerPage, 
                            jtSorting);
                        
                    //Get Total Record Count for Pagination
                    int objectsCount = auxnegocio.getDispositivosCount(buscarTipo,
                        buscarIdEstado,
                        buscarFechaIngreso);

                    //Convert Java Object to Json
                    JsonElement element = gson.toJsonTree(listaObjetos,
                        new TypeToken<List<DispositivoVO>>() {}.getType());

                    JsonArray jsonArray = element.getAsJsonArray();
                    String listData=jsonArray.toString();
                        
                    session.setAttribute("dispositivos|"+userConnected.getUsername(), listaObjetos);
                    //Return Json in the format required by jTable plugin
                    listData="{\"Result\":\"OK\",\"Records\":" + 
                        listData+",\"TotalRecordCount\": " + 
                        objectsCount + "}";
                    response.getWriter().print(listData);
                    //request.getRequestDispatcher("/mantenedores/mantenedoresFrmSet.jsp").forward(request, response);
                }catch(IOException ex){
                    String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getMessage()+"}";
                    response.getWriter().print(error);
                    ex.printStackTrace();
                }   
            }else if (action.compareTo("create") == 0) {
                    System.out.println(WEB_NAME+"[DispositivosController]"
                        + "Mantenedor - Dispositivos - Insertar...");
                    ResultCRUDVO doCreate = auxnegocio.insert(auxdata, resultado);					
                    listaObjetos.add(auxdata);

                    //Convert Java Object to Json
                    String json=gson.toJson(auxdata);					
                    //Return Json in the format required by jTable plugin
                    String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";											
                    response.getWriter().print(listData);
            }else if (action.compareTo("update") == 0) {  
                    System.out.println(WEB_NAME+"[DispositivosController]"
                        + "Mantenedor - Dispositivos - Actualizar...");
                    try{
                        ResultCRUDVO doUpdate = auxnegocio.update(auxdata, resultado);
                        listaObjetos.add(auxdata);

                        //Convert Java Object to Json
                        String json=gson.toJson(auxdata);					
                        //Return Json in the format required by jTable plugin
                        String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";											
                        response.getWriter().print(listData);
                    }catch(IOException ex){
                        String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getStackTrace().toString()+"}";
                        response.getWriter().print(error);
                    }
            }else if (action.compareTo("asignacion_start") == 0) {  
                    System.out.println(WEB_NAME+"Mantenedor - Dispositivos"
                            + "- Mostrar Formulario Asignacion Dispositivos");
                    listaObjetos = 
                        auxnegocio.getDispositivos(null,
                                buscarTipo,
                            buscarIdEstado,
                            buscarFechaIngreso,
                            startPageIndex, 
                            0, 
                            "dispositivo.device_id");
                    session.removeAttribute("all_dispositivos");
                    session.setAttribute("all_dispositivos", listaObjetos);
                    request.getRequestDispatcher("/mantencion/asignacion_dispositivos.jsp").forward(request, response);        
            }else if (action.compareTo("load_asignacion") == 0) {  
                    System.out.println(WEB_NAME+"\nMantenedor - Dispositivos"
                        + "- Mostrar Asignacion "
                        + "para el Dispositivo: " + 
                            request.getParameter("device_id"));
                    listaObjetos = 
                        auxnegocio.getDispositivos(request.getParameter("device_id"),
                            buscarTipo,
                            buscarIdEstado,
                            buscarFechaIngreso,
                            startPageIndex, 
                            0, 
                            "dispositivo.device_id");
                    request.removeAttribute("dispositivo_selected");
                    request.setAttribute("dispositivo_selected", listaObjetos.get(0));
                    request.getRequestDispatcher("/mantencion/asignacion_dispositivos.jsp").forward(request, response);        
            }else if (action.compareTo("save_asignacion") == 0) {  
                    String deviceId = request.getParameter("device_id");
                    
//                    listaObjetos = 
//                        auxnegocio.getDispositivos(request.getParameter("device_id"),
//                            buscarTipo,
//                            buscarIdEstado,
//                            buscarFechaIngreso,
//                            startPageIndex, 
//                            0, 
//                            "dispositivo.device_id");
                    
                    System.out.println(WEB_NAME+"\n[Mantenedor - "
                        + "Dispositivos]- "
                        + "Guardar Asignacion "
                        + "de departamentos "
                        + "para el Dispositivo: " + deviceId);
                    /**
                     * Asignaciones dispositivo Departamentos
                     * 1.- elimina asignacion existente del dispositivo
                     * 2.- inserta las nueva asignaciones
                     */
                    asignacionBp.deleteAsignacionesDepartamento(deviceId,resultado);
                    //Guardar departamentos asignados
                    String[] deptos = request.getParameterValues("deptos_selected");
                    if (deptos!=null){
                        for (int x=0;x<deptos.length;x++){
                            System.out.println(WEB_NAME+"depto seleccionado["+x+"] = "+deptos[x]);
                            DispositivoDepartamentoVO newDepto = new DispositivoDepartamentoVO(deviceId,deptos[x]);
                            asignacionBp.insertAsignacionDepartamento(newDepto, resultado);
                        }     
                    }else System.out.println(WEB_NAME+"\n[Mantenedor - Dispositivos"
                        + "]- No hay departamentos asignados para el Dispositivo: " + 
                            deviceId);
                    
                    
                    System.out.println(WEB_NAME+"\n[Mantenedor - "
                        + "Dispositivos]- "
                        + "Guardar Asignacion "
                        + "de centros costo "
                        + "para el Dispositivo: " + deviceId);
                    /**
                     * Asignaciones dispositivo Centro Costo
                     * 1.- elimina asignacion existente del dispositivo
                     * 2.- inserta las nueva asignaciones
                     */
                    asignacionBp.deleteAsignacionesCentroCosto(deviceId, resultado);
                    //Guardar Centros de costo asignados
                    String[] cencos = request.getParameterValues("cencos_selected");
                    if (cencos!=null){
                        for (int x = 0; x < cencos.length; x++){
                            System.out.println(WEB_NAME+"Cenco seleccionado["+x+"] = "+cencos[x]);
                            DispositivoCentroCostoVO newCenco = new DispositivoCentroCostoVO(deviceId,Integer.parseInt(cencos[x]));
                            asignacionBp.insertAsignacionCentroCosto(newCenco, resultado);
                        }     
                    }else System.out.println(WEB_NAME+"\n[Mantenedor - Dispositivos"
                        + "]- No hay centros de costo asignados para el Dispositivo: " + 
                            deviceId);
                    
                    /**
                     * Asignaciones dispositivo Empresa
                     * 1.- elimina asignacion existente del dispositivo
                     * 2.- inserta las nueva asignaciones
                     */
                    asignacionBp.deleteAsignacionesEmpresa(deviceId, resultado);
                    //Guardar Centros de costo asignados
                    String[] empresas = request.getParameterValues("empresas_selected");
                    if (empresas != null){
                        for (int x = 0; x < empresas.length; x++){
                            System.out.println(WEB_NAME+"Empresa seleccionada["+x+"] = "+empresas[x]);
                            DispositivoEmpresaVO newEmpresa = new DispositivoEmpresaVO(deviceId,empresas[x]);
                            asignacionBp.insertAsignacionEmpresa(newEmpresa, resultado);
                        }     
                    }else System.out.println(WEB_NAME+"\n[Mantenedor - Dispositivos"
                        + "]- No hay Empresas asignadas para el Dispositivo: " + 
                            deviceId);
                    
                    /**
                     * Cargar asignaciones existentes
                     */
                    listaObjetos = 
                        auxnegocio.getDispositivos(request.getParameter("device_id"),
                            buscarTipo,
                            buscarIdEstado,
                            buscarFechaIngreso,
                            startPageIndex, 
                            0, 
                            "dispositivo.device_id");
                    request.removeAttribute("dispositivo_selected");
                    request.setAttribute("dispositivo_selected", listaObjetos.get(0));
                    request.getRequestDispatcher("/mantencion/asignacion_dispositivos.jsp").forward(request, response);        
            }
            
      }
    }
    
}
