package cl.femase.gestionweb.servlet.mantencion;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.business.MarcacionVirtualBp;
import cl.femase.gestionweb.common.ClientInfo;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import cl.femase.gestionweb.vo.MarcacionVirtualVO;
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
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

public class MarcacionVirtualController extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 995L;
    
    public MarcacionVirtualController() {
        
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

        MarcacionVirtualBp negocio = new MarcacionVirtualBp(appProperties);
        EmpleadosBp empleadosbp=new EmpleadosBp(appProperties);
        
        if(request.getParameter("action") != null){
            System.out.println(WEB_NAME+"[MarcacionVirtualController]"
                + "action is: " + request.getParameter("action"));
            List<MarcacionVirtualVO> listaRegistros = new ArrayList<MarcacionVirtualVO>();
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            response.setContentType("application/json");

            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("AMV");//admin marcacion virtual
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
            //resultado.setDeptoId(action);
            //new
            ClientInfo clientInfo = new ClientInfo();
            clientInfo.printClientInfo(request);

            /**
            * 20200229-001: Agregar info a tabla eventos: Sistema operativo y navegador
            */
            resultado.setOperatingSystem(clientInfo.getClientOS(request));
            resultado.setBrowserName(clientInfo.getClientBrowser(request));

            //Fetch Data from User Table
            int startPageIndex      = 0;
            int numRecordsPerPage   = 10;
            String jtSorting        = "ve.rut asc";
            
            if (request.getParameter("jtStartIndex") != null) 
                startPageIndex = Integer.parseInt(request.getParameter("jtStartIndex"));
            if (request.getParameter("jtPageSize") != null) 
                numRecordsPerPage   = Integer.parseInt(request.getParameter("jtPageSize"));
            if (request.getParameter("jtSorting") != null) 
                jtSorting   = request.getParameter("jtSorting");
            
            if (jtSorting.contains("nombreEmpleado")) jtSorting = jtSorting.replaceFirst("nombreEmpleado","ve.nombre");
            else if (jtSorting.contains("rutEmpleado")) jtSorting = jtSorting.replaceFirst("rutEmpleado","ve.rut");
                        
            //objeto usado para update/insert
            MarcacionVirtualVO asignacion = new MarcacionVirtualVO();
            
            System.out.println(WEB_NAME+"[MarcacionVirtualController]"
                + "empresaId: " + request.getParameter("empresaId")
                + ", rut empleado: " + request.getParameter("rutEmpleado"));
            
            if(request.getParameter("rowKey") != null){
                asignacion.setRowKey(request.getParameter("rowKey"));
            }
            
            if(request.getParameter("empresaId") != null){
                asignacion.setEmpresaId(request.getParameter("empresaId"));
            }
            
            if(request.getParameter("rutEmpleado") != null){
                asignacion.setRutEmpleado(request.getParameter("rutEmpleado"));
            }
            
            //if(request.getParameter("username") != null){
                asignacion.setUsername(userConnected.getUsername());
            //}
            //tieneMarcaVirtual;//'S' o 'N'
            if(request.getParameter("admiteMarcaVirtual") != null){
                asignacion.setAdmiteMarcaVirtual(request.getParameter("admiteMarcaVirtual"));
            }
            if(request.getParameter("fecha1") != null){
                asignacion.setFecha1(request.getParameter("fecha1"));
            }
            if(request.getParameter("fecha2") != null){
                asignacion.setFecha2(request.getParameter("fecha2"));
            }
            if(request.getParameter("fecha3") != null){
                asignacion.setFecha3(request.getParameter("fecha3"));
            }
            if(request.getParameter("fecha4") != null){
                asignacion.setFecha4(request.getParameter("fecha4"));
            }
            if(request.getParameter("fecha5") != null){
                asignacion.setFecha5(request.getParameter("fecha5"));
            }
            if(request.getParameter("fecha6") != null){
                asignacion.setFecha6(request.getParameter("fecha6"));
            }
            if(request.getParameter("fecha7") != null){
                asignacion.setFecha7(request.getParameter("fecha7"));
            }
            if(request.getParameter("registrarUbicacion") != null){
                asignacion.setRegistrarUbicacion(request.getParameter("registrarUbicacion"));
            }
            if(request.getParameter("marcaMovil") != null){
                asignacion.setMarcaMovil(request.getParameter("marcaMovil"));
            }
            if(request.getParameter("movilId") != null){
                asignacion.setMovilId(request.getParameter("movilId"));
            }
            if(request.getParameter("registrarUbicacion") != null){
                asignacion.setRegistrarUbicacion(request.getParameter("registrarUbicacion"));
            }
            
            Calendar calHoy = Calendar.getInstance(new Locale("es","CL"));
            Date fechaActual = calHoy.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String fechaHoraAsignacion = sdf.format(fechaActual);
            System.out.println(WEB_NAME+"[MarcacionVirtualController]List- "
                + "fecha_hora asignacion= " + fechaHoraAsignacion);
            if (action.compareTo("list") == 0) {
                try{
                    String paramCencoID         = request.getParameter("cencoId");
                    String paramEmpresa = null;
                    String paramDepto   = null;
                    String cencoId      = "";
                    System.out.println(WEB_NAME+"[MarcacionVirtualController]List- "
                        + "token param 'cencoID'= " + paramCencoID);
                    if (paramCencoID != null && paramCencoID.compareTo("-1") != 0){
                        StringTokenizer tokenCenco  = new StringTokenizer(paramCencoID, "|");
                        if (tokenCenco.countTokens() > 0){
                            while (tokenCenco.hasMoreTokens()){
                                paramEmpresa   = tokenCenco.nextToken();
                                paramDepto     = tokenCenco.nextToken();
                                cencoId     = tokenCenco.nextToken();
                            }
                        }
                    }
                    System.out.println(WEB_NAME+"[MarcacionVirtualController]"
                        + "Listar asignacion marcacion virtual. "
                        + "empresa: " + paramEmpresa
                        +", depto: " + paramDepto
                        +", cenco: " + cencoId
                        +", rut: " + request.getParameter("rutEmpleado"));
                    session.setAttribute("pEmpresaId", paramEmpresa);
                    session.setAttribute("pDeptoId", paramDepto);
                    session.setAttribute("pCencoId", cencoId);
                    int intCencoId=-1;
                    if (cencoId.compareTo("") != 0) intCencoId = Integer.parseInt(cencoId); 
                    
                    int objectsCount = 0;
                    if (intCencoId != -1){
                        listaRegistros = negocio.getRegistros(intCencoId, 
                            request.getParameter("rutEmpleado"),
                            startPageIndex, 
                            numRecordsPerPage, 
                            jtSorting);
                        if (request.getParameter("rutEmpleado") != null && request.getParameter("rutEmpleado").compareTo("-1")==0){
                            //selecciona empleados = Todos
                            asignacion = new MarcacionVirtualVO();
                
                            asignacion.setEmpresaId(paramEmpresa);
                            asignacion.setDeptoId(paramDepto);
                            asignacion.setCencoId(intCencoId);

                            asignacion.setRutEmpleado("TODOS");
                            asignacion.setNombreEmpleado("EMPLEADOS VIGENTES");
                            asignacion.setRowKey(paramEmpresa + "|TODOS");
                            asignacion.setAdmiteMarcaVirtual("N");
                            asignacion.setRegistrarUbicacion("N");
                            asignacion.setMarcaMovil("N");
                            listaRegistros.add(asignacion);
                        }
                        //Get Total Record Count for Pagination
                        objectsCount = negocio.getRegistrosCount(intCencoId, 
                            request.getParameter("rutEmpleado"));
                        objectsCount++;
                        session.setAttribute("asignacionMV|"+userConnected.getUsername(), listaRegistros);
                        
                        session.setAttribute("cencoIdMV", intCencoId);
                        session.setAttribute("rutEmpleadoMV", request.getParameter("rutEmpleado"));
                        
                    }
                    //Convert Java Object to Json
                    JsonElement element = gson.toJsonTree(listaRegistros,
                        new TypeToken<List<MarcacionVirtualVO>>() {}.getType());

                    JsonArray jsonArray = element.getAsJsonArray();
                    String listData=jsonArray.toString();
                        
                    //Return Json in the format required by jTable plugin
                    listData="{\"Result\":\"OK\",\"Records\":" + 
                        listData+",\"TotalRecordCount\": " + 
                        objectsCount + "}";
                    response.getWriter().print(listData);
                    //request.getRequestDispatcher("/mantenedores/mantenedoresFrmSet.jsp").forward(request, response);
                }catch(Exception ex){
                    String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getMessage()+"}";
                    response.getWriter().print(error);
                    ex.printStackTrace();
                }   
            }else if (action.compareTo("update") == 0) {  
                    String rowKey = asignacion.getRowKey();
                    StringTokenizer keytoken = new StringTokenizer(rowKey, "|");
                    asignacion.setEmpresaId(keytoken.nextToken());
                    asignacion.setRutEmpleado(keytoken.nextToken());
                    System.out.println(WEB_NAME+"[MarcacionVirtualController]"
                        + "Modificar datos de marcacion virtual, "
                        + "rut: : " + asignacion.getRutEmpleado());
                    if (asignacion.getRutEmpleado().compareTo("TODOS") != 0){
                        EmpleadoVO infoEmpleado = empleadosbp.getEmpleado(asignacion.getEmpresaId(), 
                            asignacion.getRutEmpleado());
                        resultado.setEmpresaId(infoEmpleado.getEmpresa().getId());
                        resultado.setDeptoId(infoEmpleado.getDepartamento().getId());
                        resultado.setCencoId(infoEmpleado.getCentroCosto().getId());
                        System.out.println(WEB_NAME+"[MarcacionVirtualController]"
                            + "Modificar datos para el rut:" + asignacion.getRutEmpleado());
                        //boolean insertar = true;
                        System.out.println(WEB_NAME+"[MarcacionVirtualController]"
                            + "Admite marca virtual: " + asignacion.getAdmiteMarcaVirtual());
                        //if (asignacion.getAdmiteMarcaVirtual().compareTo("N") == 0) insertar = false;
                        //System.out.println(WEB_NAME+"[MarcacionVirtualController]"
                        //    + "Insertar:" + insertar);    
                        try{
                            ResultCRUDVO evento;
                            resultado.setRutEmpleado(asignacion.getRutEmpleado());
                            //if (insertar){
                                System.out.println(WEB_NAME+"[MarcacionVirtualController]"
                                    + "Delete e Insert asignacion "
                                    + "marcacion virtual, "
                                    + "empresa_id: " + asignacion.getEmpresaId()
                                    + ", rut_empleado: " + asignacion.getRutEmpleado());
                                asignacion.setInsertaEvento(false);
                                evento = negocio.delete(asignacion, resultado);
                                
                                asignacion.setFechaAsignacion(fechaHoraAsignacion);
                                
                                System.out.println(WEB_NAME+"[MarcacionVirtualController]"
                                    + "Datos a insertar: " + asignacion.toString());
                                
                                evento = negocio.insert(asignacion, resultado);
                            //}
                            /*
                            else{
                                System.out.println(WEB_NAME+"[MarcacionVirtualController]"
                                    + "Eliminar asignacion "
                                    + "marcacion virtual, "
                                    + "empresa_id: " + asignacion.getEmpresaId()
                                    + ", rut_empleado: " + asignacion.getRutEmpleado());
                                evento = negocio.delete(asignacion, resultado);
                                asignacion.setUsername("");
                                asignacion.setFechaAsignacion("");
                                asignacion.setFecha1("");
                                asignacion.setFecha2("");
                                asignacion.setFecha3("");
                                asignacion.setFecha4("");
                                asignacion.setFecha5("");
                                asignacion.setFecha6("");
                                asignacion.setFecha7("");
                                asignacion.setRegistrarUbicacion("");
                                asignacion.setMarcaMovil("");
                                
                            }*/
                            System.out.println(WEB_NAME+"[MarcacionVirtualController]"
                                + "Datos actualizados, "
                                + "empresa_id: " + asignacion.getEmpresaId()
                                + ", rut_empleado: " + asignacion.getRutEmpleado()
                                + ", marca_virtual?: " + asignacion.getAdmiteMarcaVirtual()
                                + ", fecha_asignacion: " + asignacion.getFechaAsignacion()
                                + ", marca_movil?: " + asignacion.getMarcaMovil()   
                                + ", username: " + asignacion.getUsername());

                            listaRegistros.add(asignacion);
                            //Convert Java Object to Json
                            String json=gson.toJson(asignacion);
                            System.out.println(WEB_NAME+"[MarcacionVirtualController]"
                                + "Json string: "+json);
                            //Return Json in the format required by jTable plugin
                            String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";											
                            response.getWriter().print(listData);

                        }catch(IOException ex){
                            String error = "{\"Result\":\"ERROR\",\"Message\":"
                                +ex.getStackTrace().toString()+"}";
                            response.getWriter().print(error);
                        }
                    }else{
                        System.out.println(WEB_NAME+"[MarcacionVirtualController]"
                            + "Asignar marcacion virtual "
                            + "para todos los empleados del "
                            + "cenco_id: " + asignacion.getCencoId() 
                            + ", empresa_id: " + asignacion.getEmpresaId());
                        String paramCencoId = (String)session.getAttribute("pCencoId");
                        resultado.setEmpresaId((String)session.getAttribute("pEmpresaId"));
                        resultado.setDeptoId((String)session.getAttribute("pDeptoId"));
                        resultado.setCencoId(Integer.parseInt(paramCencoId));
                        
                        ArrayList<MarcacionVirtualVO> listaSaveTodos = new ArrayList<>();
                        
                        List<MarcacionVirtualVO> listaBaseTodos = 
                            negocio.getRegistros(resultado.getCencoId(), 
                            request.getParameter("rutEmpleado"),
                            0, 
                            0, 
                            jtSorting);
                                
                        //listaRegistros 
                        //    = (List<MarcacionVirtualVO>)session.getAttribute("asignacionMV|" + userConnected.getUsername());
                        for (MarcacionVirtualVO it_asignacion : listaBaseTodos) {
                            it_asignacion.setUsername(asignacion.getUsername());
                            it_asignacion.setAdmiteMarcaVirtual(asignacion.getAdmiteMarcaVirtual());
                            it_asignacion.setRegistrarUbicacion(asignacion.getRegistrarUbicacion());
                            it_asignacion.setMarcaMovil(asignacion.getMarcaMovil());
                            it_asignacion.setFechaAsignacion(fechaHoraAsignacion);
                            
                            String f1 = asignacion.getFecha1();
                            String f2 = asignacion.getFecha2();
                            String f3 = asignacion.getFecha3();
                            String f4 = asignacion.getFecha4();
                            String f5 = asignacion.getFecha5();
                            String f6 = asignacion.getFecha6();
                            String f7 = asignacion.getFecha7();
                            
                            it_asignacion.setFecha1(f1);
                            it_asignacion.setFecha2(f2);
                            it_asignacion.setFecha3(f3);
                            it_asignacion.setFecha4(f4);
                            it_asignacion.setFecha5(f5);
                            it_asignacion.setFecha6(f6);
                            it_asignacion.setFecha7(f7);
                            System.out.println(WEB_NAME+"[MarcacionVirtualController]-TODOS-"
                                + "Asignar marcacion virtual rut: " 
                                + it_asignacion.getRutEmpleado());
                            negocio.delete(it_asignacion, resultado);    
                            listaSaveTodos.add(it_asignacion);
                        }
                        try {
                            
                            negocio.saveAsignacionList(listaSaveTodos, resultado);
                        } catch (SQLException ex) {
                            System.err.println("[MarcacionVirtualController]"
                                + "Error al asignar marcacion "
                                + "virtual para todos los empleados: " + ex.toString());
                        }
                        listaRegistros = negocio.getRegistros(resultado.getCencoId(), 
                            request.getParameter("rutEmpleado"),
                            startPageIndex, 
                            numRecordsPerPage, 
                            jtSorting);
                        //selecciona empleados = Todos
                        asignacion = new MarcacionVirtualVO();

                        asignacion.setEmpresaId(resultado.getEmpresaId());
                        asignacion.setDeptoId(resultado.getDeptoId());
                        asignacion.setCencoId(resultado.getCencoId());

                        asignacion.setRutEmpleado("TODOS");
                        asignacion.setNombreEmpleado("EMPLEADOS VIGENTES");
                        asignacion.setRowKey(resultado.getEmpresaId() + "|TODOS");
                        asignacion.setAdmiteMarcaVirtual("N");
                        asignacion.setRegistrarUbicacion("N");
                        asignacion.setMarcaMovil("N");
                        
                        listaRegistros.add(asignacion);
                        //Convert Java Object to Json
                        JsonElement element = gson.toJsonTree(listaRegistros,
                            new TypeToken<List<MarcacionVirtualVO>>() {}.getType());

                        JsonArray jsonArray = element.getAsJsonArray();
                        String listData=jsonArray.toString();
                        //Get Total Record Count for Pagination
                        int objectsCount = negocio.getRegistrosCount(resultado.getCencoId(), 
                            request.getParameter("rutEmpleado"));
                        objectsCount++;
                        session.setAttribute("asignacionMV|"+userConnected.getUsername(), listaRegistros);
                        
                        session.setAttribute("cencoIdMV", resultado.getCencoId());
                        session.setAttribute("rutEmpleadoMV", request.getParameter("rutEmpleado"));
                        //Return Json in the format required by jTable plugin
                        listData="{\"Result\":\"OK\",\"Records\":" + 
                            listData+",\"TotalRecordCount\": " + 
                            objectsCount + "}";
                        response.getWriter().print(listData);

                    }    
            }
        }
    }
    
}
