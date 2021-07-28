package cl.femase.gestionweb.servlet.mantencion;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.MaintenanceEventsBp;
import cl.femase.gestionweb.business.MarcasBp;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.MarcaVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
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

public class MarcasHistController extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 996L;
    
    public MarcasHistController() {
        
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
        MarcasBp auxnegocio=new MarcasBp(appProperties);
        MaintenanceEventsBp eventosBp   = new MaintenanceEventsBp(appProperties);
        
        if(request.getParameter("action") != null){
            System.out.println("[MarcasHistController]"
                + "action is: " + request.getParameter("action"));
            List<MarcaVO> listaObjetos = new ArrayList<>();
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            response.setContentType("application/json");

            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("MAR");
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
                   
            /**
            * En este parametro vendra 'deptoId|cencoId'
            */
            String empresaId= null;
            String deptoId  = null;
            String cencoId  = "-1";
            String paramCencoID         = request.getParameter("cencoId");
            System.out.println("[MarcasHistController]"
                + "token param 'cencoID'= " + paramCencoID);
            if (paramCencoID != null && paramCencoID.compareTo("-1") != 0){
                StringTokenizer tokenCenco  = new StringTokenizer(paramCencoID, "|");
                if (tokenCenco.countTokens() > 0){
                    while (tokenCenco.hasMoreTokens()){
                        empresaId   = tokenCenco.nextToken();
                        deptoId     = tokenCenco.nextToken();
                        cencoId     = tokenCenco.nextToken();
                    }
                }
            }
            
            //Fetch Data from User Table
            int startPageIndex      = 0;
            int numRecordsPerPage   = 10;
            String jtSorting        = "rut_empleado,fecha_hora asc";
            /** filtros de busqueda */
            
            if (request.getParameter("jtStartIndex") != null) 
                startPageIndex = Integer.parseInt(request.getParameter("jtStartIndex"));
            if (request.getParameter("jtPageSize") != null) 
                numRecordsPerPage   = Integer.parseInt(request.getParameter("jtPageSize"));
            if (request.getParameter("jtSorting") != null) 
                jtSorting   = request.getParameter("jtSorting");
            System.out.println("MarcasHistController." +
                " jtSorting: " + request.getParameter("jtSorting"));
            
            if (jtSorting.contains("codDispositivo")) jtSorting = jtSorting.replaceFirst("codDispositivo","cod_dispositivo");
            else if (jtSorting.contains("tipoMarca")) jtSorting = jtSorting.replaceFirst("tipoMarca","cod_tipo_marca");
            else if (jtSorting.contains("rutEmpleado")) jtSorting = jtSorting.replaceFirst("rutEmpleado","rut_empleado");
            else if (jtSorting.contains("fechaHoraStr")) jtSorting = jtSorting.replaceFirst("fechaHoraStr","fecha_hora");
                                         
            System.out.println("cl.femase.gestionweb."
                + "servlet.mantencion."
                + "MarcasHistController."
                + "processRequest()."
                + " FechaHoraKey: " + request.getParameter("fechaHoraKey")
                + ", rutEmpleado: " + request.getParameter("rutEmpleado")
                + ", empresaCod: " + request.getParameter("empresaCod")
                + ", rutKey: " + request.getParameter("rutKey")
                + ", aEmpresa: " + request.getParameter("aEmpresa")
                + ", aRut: " + request.getParameter("aRut")      
                 + ", aDevice: " + request.getParameter("aDevice")        
                + ", fechaHora: " + request.getParameter("fechaHora")
                + ", fechaHoraKey: " + request.getParameter("fechaHoraKey")
                + ", fecha: " + request.getParameter("fecha")
                + ", hora: " + request.getParameter("hora")
                + ", minutos: " + request.getParameter("minutos")
                + ", tipoMarca: " + request.getParameter("tipoMarca")
                + ", codDispositivo: " + request.getParameter("codDispositivo")
                + ", id: " + request.getParameter("id")
                + ", hashcode: " + request.getParameter("hashcode")
                + ", comentario: " + request.getParameter("comentario")
                + ", jtSorting: " + request.getParameter("jtSorting"));

            //objeto usado para update/insert
            MarcaVO auxdata = new MarcaVO();
            
            if (request.getParameter("fecha") != null){
                auxdata.setFecha(request.getParameter("fecha"));
            }
            if (request.getParameter("hora") != null){
                auxdata.setHora(request.getParameter("hora"));
            }
            if (request.getParameter("minutos") != null){
                auxdata.setMinutos(request.getParameter("minutos"));
            }
            if (request.getParameter("rutKey") != null){
                auxdata.setRutEmpleado(request.getParameter("rutKey"));
            }
            if (request.getParameter("empresaCod") != null){
                auxdata.setEmpresaCod(request.getParameter("empresaCod"));
            }
            if (action.compareTo("create") != 0){
                if (request.getParameter("fechaHoraKey") != null){
                    auxdata.setFechaHora(request.getParameter("fechaHoraKey"));
                }
            }
            if (request.getParameter("tipoMarca") != null){
                auxdata.setTipoMarca(Integer.parseInt(request.getParameter("tipoMarca")));
            }
            if (request.getParameter("id") != null){
                auxdata.setId(request.getParameter("id"));
            }
            if (request.getParameter("hashcode") != null){
                auxdata.setHashcode(request.getParameter("hashcode"));
            }
            if (request.getParameter("comentario") != null){
                auxdata.setComentario(request.getParameter("comentario"));
            }
            
            boolean listar=true;
            
            if ((cencoId==null || cencoId.compareTo("-1") == 0) &&
                    (request.getParameter("rutEmpleado")!=null && request.getParameter("rutEmpleado").compareTo("-1") == 0) &&
                    request.getParameter("startDate").compareTo("") == 0 &&
                    request.getParameter("endDate").compareTo("") == 0 &&
                    request.getParameter("dispositivo") != null && 
                    request.getParameter("dispositivo").compareTo("-1") == 0){
                listar=false;
            }else if ((request.getParameter("startDate")!=null && request.getParameter("startDate").compareTo("") == 0) &&
                    request.getParameter("endDate").compareTo("") == 0){
                listar = false;
            }
            
            if (action.compareTo("list") == 0){
                System.out.println("[MarcasHistController]"
                    + "mostrando marcas."+
                        "CencoId= " +cencoId
                        +", rutEmpleado= " +request.getParameter("rutEmpleado")
                        +", dispositivoId= " +request.getParameter("dispositivoId")
                        +", startDate= " +request.getParameter("startDate")
                        +", endDate= " +request.getParameter("endDate"));
                try{
                    int objectsCount = 0;
                    if (listar){
                        request.setAttribute("empresa", empresaId);
                        request.setAttribute("rut_empleado", request.getParameter("rutEmpleado"));
                        
                        listaObjetos = auxnegocio.getMarcasHist(empresaId,
                            deptoId,
                            Integer.parseInt(cencoId),
                            request.getParameter("rutEmpleado"), 
                            request.getParameter("dispositivoId"), 
                            request.getParameter("startDate"), 
                            request.getParameter("endDate"),
                            startPageIndex, 
                            numRecordsPerPage, 
                            jtSorting);

                        //Get Total Record Count for Pagination
                        objectsCount = auxnegocio.getMarcasHistCount(empresaId,
                            deptoId,
                            Integer.parseInt(cencoId),
                            request.getParameter("rutEmpleado"), 
                            request.getParameter("dispositivoId"), 
                            request.getParameter("startDate"), 
                            request.getParameter("endDate"));
                        
                            //agregar evento al log.
                            resultado.setRutEmpleado(request.getParameter("rutEmpleado"));
                            resultado.setEmpresaId(empresaId);
                            resultado.setDeptoId(deptoId);
                            resultado.setCencoId(Integer.parseInt(cencoId));
                            resultado.setDescription("Consulta marcas historicas. "
                                + " Rut empleado: " + request.getParameter("rutEmpleado")    
                                + ", desde: " + request.getParameter("startDate")
                                + ", hasta: " + request.getParameter("endDate")
                                + ", dispositivo: " + request.getParameter("dispositivoId"));
                            eventosBp.addEvent(resultado);
                    }
                    //Convert Java Object to Json
                    JsonElement element = gson.toJsonTree(listaObjetos,
                        new TypeToken<List<MarcaVO>>() {}.getType());

                    JsonArray jsonArray = element.getAsJsonArray();
                    String listData=jsonArray.toString();
                        
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
            }
//            else if (action.compareTo("create") == 0) {
//                String fechaHora = request.getParameter("fechaHora")
//                    +" "+request.getParameter("hora")
//                    +":"+request.getParameter("minutos")
//                    +":00";
//                auxdata.setEmpresaCod(request.getParameter("empresaCod"));
//                auxdata.setRutEmpleado(request.getParameter("aRut"));
//                auxdata.setFechaHora(fechaHora);
//                auxdata.setCodDispositivo(request.getParameter("aDevice"));
//                auxdata.setId(request.getParameter("id"));
//                auxdata.setHashcode(request.getParameter("hashcode"));
//                auxdata.setComentario(request.getParameter("comentario"));
//                auxdata.setTipoMarca(Integer.parseInt(request.getParameter("tipoMarca")));
//                      
//                if (auxdata.getRutEmpleado()!=null && auxdata.getRutEmpleado().compareTo("")!=0){
//                    EmpleadosBp empleadosbp=new EmpleadosBp(appProperties);
//                    EmpleadoVO infoEmpleado = empleadosbp.getEmpleado(auxdata.getEmpresaCod(), auxdata.getRutEmpleado());
//                    resultado.setEmpresaId(infoEmpleado.getEmpresa().getId());
//                    resultado.setDeptoId(infoEmpleado.getDepartamento().getId());
//                    resultado.setCencoId(infoEmpleado.getCentroCosto().getId());
//                }
//                
//                System.out.println("[MarcasHistController]Insertar marca. "
//                    + "Empresa: " + auxdata.getEmpresaCod()
//                    + ", rut: " + auxdata.getRutEmpleado()
//                    + ", fechaHora: " + auxdata.getFechaHora()
//                    + ", CodDispositivo: " + auxdata.getCodDispositivo()
//                    + ", TipoMarca: " + auxdata.getTipoMarca());
//                    
//                MaintenanceVO doCreate = auxnegocio.insertWithLog(auxdata, resultado);					
//                listaObjetos.add(auxdata);
//
//                //Convert Java Object to Json
//                String json=gson.toJson(auxdata);					
//                //Return Json in the format required by jTable plugin
//                String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";											
//                response.getWriter().print(listData);
//            }else if (action.compareTo("update") == 0) {  
//                    System.out.println("[MarcasHistController]}"
//                        + "Actualizar marca, rowKey: "+request.getParameter("rowKey"));
//                    if (request.getParameter("rowKey")!=null){
//                        StringTokenizer tokenKey = new StringTokenizer(request.getParameter("rowKey"), "|");
//                        //emp01|15695517B|2018-01-15 08:34:00|1
//                        auxdata.setEmpresaCod(tokenKey.nextToken());
//                        auxdata.setRutEmpleado(tokenKey.nextToken());
//                        auxdata.setFechaHora(tokenKey.nextToken());
//                        System.out.println("[MarcasHistController]}"
//                            + "Actualizar marca, "
//                            + "rut: " + auxdata.getRutEmpleado()
//                            + ", fechaHora: " + auxdata.getFechaHora());
//                    }
//                    
//                    try{
//                        resultado.setEmpresaId(auxdata.getEmpresaCod());
//                        resultado.setRutEmpleado(auxdata.getRutEmpleado());
//                        if (auxdata.getRutEmpleado()!=null && auxdata.getRutEmpleado().compareTo("")!=0){
//                            EmpleadosBp empleadosbp=new EmpleadosBp(appProperties);
//                            EmpleadoVO infoEmpleado = empleadosbp.getEmpleado(auxdata.getEmpresaCod(), auxdata.getRutEmpleado());
//                            resultado.setEmpresaId(infoEmpleado.getEmpresa().getId());
//                            resultado.setDeptoId(infoEmpleado.getDepartamento().getId());
//                            resultado.setCencoId(infoEmpleado.getCentroCosto().getId());
//                        }
//                        MaintenanceVO doUpdate = auxnegocio.update(auxdata, resultado);
//                        StringTokenizer tkfechaHora = new StringTokenizer(auxdata.getFechaHora()," ");
//                        String auxfecha = tkfechaHora.nextToken();
//                        String auxhora = tkfechaHora.nextToken();
//                        String newfechaHora = auxfecha + " " 
//                            + auxdata.getHora()
//                            + ":" + auxdata.getMinutos() + ":00";
//                        System.out.println("[MarcasHistController]new fecha hora: "+newfechaHora);
//                        auxdata.setFechaHora(newfechaHora);
//                        auxdata.setFechaHoraKey(newfechaHora);
//                        auxdata.setFechaHoraStr(newfechaHora);
//                        listaObjetos.add(auxdata);
//
//                        //Convert Java Object to Json
//                        String json=gson.toJson(auxdata);					
//                        //Return Json in the format required by jTable plugin
//                        String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";											
//                        response.getWriter().print(listData);
//                        
//                    }catch(Exception ex){
//                        String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getStackTrace().toString()+"}";
//                        response.getWriter().print(error);
//                    }
//            }else if (action.compareTo("delete") == 0) {
//                    System.out.println("[MarcasHistController]}"
//                        + "Eliminar marca, rowKey: "+request.getParameter("rowKey"));
//                    StringTokenizer tokenKey = new StringTokenizer(request.getParameter("rowKey"), "|");
//                    //emp01|15695517B|2018-01-15 08:34:00|1
//                    auxdata.setEmpresaCod(tokenKey.nextToken());
//                    auxdata.setRutEmpleado(tokenKey.nextToken());
//                    auxdata.setFechaHora(tokenKey.nextToken());
//                    System.out.println("[MarcasHistController]}"
//                        + "Eliminar marca, rut: " + auxdata.getRutEmpleado());
//                    try{
//                        resultado.setEmpresaId(auxdata.getEmpresaCod());
//                        resultado.setRutEmpleado(auxdata.getRutEmpleado());
//                        if (auxdata.getRutEmpleado()!=null && auxdata.getRutEmpleado().compareTo("")!=0){
//                            EmpleadosBp empleadosbp=new EmpleadosBp(appProperties);
//                            EmpleadoVO infoEmpleado = empleadosbp.getEmpleado(auxdata.getEmpresaCod(), auxdata.getRutEmpleado());
//                            resultado.setEmpresaId(infoEmpleado.getEmpresa().getId());
//                            resultado.setDeptoId(infoEmpleado.getDepartamento().getId());
//                            resultado.setCencoId(infoEmpleado.getCentroCosto().getId());
//                        }
//                        MaintenanceVO doDelete = auxnegocio.delete(auxdata, resultado);
//                        listaObjetos.add(auxdata);
//
//                        //Convert Java Object to Json
//                        String json=gson.toJson(auxdata);					
//                        //Return Json in the format required by jTable plugin
//                        String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";											
//                        response.getWriter().print(listData);
//                        
//                    }catch(Exception ex){
//                        String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getStackTrace().toString()+"}";
//                        response.getWriter().print(error);
//                    }
//            }
            
        }
    }
    
}
