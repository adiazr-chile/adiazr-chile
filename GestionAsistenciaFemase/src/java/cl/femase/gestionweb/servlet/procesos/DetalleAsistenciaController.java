package cl.femase.gestionweb.servlet.procesos;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.CalculoAsistenciaBp;
import cl.femase.gestionweb.business.CalculoAsistenciaRunnable;
import cl.femase.gestionweb.business.DetalleAsistenciaBp;
import cl.femase.gestionweb.business.CalendarioFeriadoBp;
import cl.femase.gestionweb.business.DetalleTurnosBp;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.business.MaintenanceEventsBp;
import cl.femase.gestionweb.business.TiempoExtraBp;
import cl.femase.gestionweb.business.TurnosBp;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.dao.ProcesosDAO;
import cl.femase.gestionweb.vo.DetalleAsistenciaVO;
import cl.femase.gestionweb.vo.EmpleadoConDetallesAsistenciaVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.MarcaVO;
import cl.femase.gestionweb.vo.ProcesoEjecucionVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;

public class DetalleAsistenciaController extends BaseServlet {

    /**
     *
     */
    protected static Logger m_logger = Logger.getLogger("gestionweb");
    private String IT_FECHA = "";
    private static final long serialVersionUID = 985L;
    private HashMap<String, MarcaVO> m_marcasProcesadas = new HashMap<>();
    
    public DetalleAsistenciaController() {
        
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

        System.out.println(WEB_NAME+"[DetalleAsistenciaController.doPost]"
            + "Entrando...");
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
        
        TurnosBp turnosBp = new TurnosBp(appProperties);
        DetalleTurnosBp detalleTurnoBp = new DetalleTurnosBp(appProperties);
        DetalleAsistenciaBp detalleAsistenciaBp = new DetalleAsistenciaBp(appProperties,userConnected);
        CalendarioFeriadoBp feriadosBp = new CalendarioFeriadoBp(appProperties);
        TiempoExtraBp tiempoExtraBp = new TiempoExtraBp(appProperties);
        EmpleadosBp empleadosBp = new EmpleadosBp(appProperties);
        MaintenanceEventsBp eventosBp   = new MaintenanceEventsBp(appProperties);
        
        List<EmpleadoVO> empleados = new ArrayList<>();
        
        Calendar mycal = Calendar.getInstance();
        MaintenanceEventVO resultado=new MaintenanceEventVO();
        resultado.setUsername(userConnected.getUsername());
        resultado.setDatetime(new Date());
        resultado.setUserIP(request.getRemoteAddr());
        resultado.setType("DTA");
        resultado.setEmpresaIdSource(userConnected.getEmpresaId());
        
        System.out.println(WEB_NAME+"[DetalleAsistenciaController]"
            + "action is: " + request.getParameter("action"));
        
        if (request.getParameter("action") != null){
            
            
            Gson gson = new Gson();
            response.setContentType("application/json");

            //Fetch Data from User Table
            int startPageIndex      = 0;
            int numRecordsPerPage   = 10;
            String jtSorting        = "rutEmpleado asc";
            /** filtros de busqueda */
            String filtroEmpresa     = "";
            String filtroDepto     = "";
            int filtroCenco      = -1;
            String filtroRutEmpleado = "";
            String startDate = "";
            String endDate = "";
            if (request.getParameter("jtStartIndex") != null) 
                startPageIndex = Integer.parseInt(request.getParameter("jtStartIndex"));
            if (request.getParameter("jtPageSize") != null) 
                numRecordsPerPage   = Integer.parseInt(request.getParameter("jtPageSize"));
            if (request.getParameter("jtSorting") != null) 
                jtSorting   = request.getParameter("jtSorting");
            
            if (jtSorting.contains("rutEmpleado")) jtSorting = jtSorting.replaceFirst("rutEmpleado","empleado.empl_rut");
            else if (jtSorting.contains("empresaId")) jtSorting = jtSorting.replaceFirst("empresaId","empresa_nombre");
            else if (jtSorting.contains("deptoId")) jtSorting = jtSorting.replaceFirst("deptoId","depto_nombre");
            else if (jtSorting.contains("cencoId")) jtSorting = jtSorting.replaceFirst("cencoId","ccosto_nombre");
                        
            String paramCencoID         = request.getParameter("cencoId");
            System.out.println(WEB_NAME+"[DetalleAsistenciaController]"
                + "token param 'cencoID'= " + paramCencoID);
            
            try{
                if (paramCencoID != null && paramCencoID.compareTo("-1") != 0){
                    StringTokenizer tokenCenco  = new StringTokenizer(paramCencoID, "|");
                    if (tokenCenco.countTokens() > 0){
                        while (tokenCenco.hasMoreTokens()){
                            filtroEmpresa   = tokenCenco.nextToken();
                            filtroDepto     = tokenCenco.nextToken();
                            filtroCenco     = Integer.parseInt(tokenCenco.nextToken());
                        }
                    }
                }
            }catch(NoSuchElementException nsuch){
                if (request.getParameter("empresaId") != null) 
                    filtroEmpresa  = request.getParameter("empresaId");
                if (request.getParameter("deptoId") != null) 
                    filtroDepto = request.getParameter("deptoId");
                if (request.getParameter("cencoId") != null) 
                    filtroCenco = Integer.parseInt(request.getParameter("cencoId"));
            }

            if (request.getParameter("startDate") != null) 
                startDate = request.getParameter("startDate");
            if (request.getParameter("endDate") != null) 
                endDate = request.getParameter("endDate");
            
            session.setAttribute("startDate", startDate);
            session.setAttribute("endDate", endDate);
            
            System.out.println(WEB_NAME+"cl.femase.gestionweb.servlet."
                + "procesos.DetalleAsistenciaController."
                + "processRequest()."
                + " Parametros. "
                + "empresa=" + filtroEmpresa    
                + ",depto=" + filtroDepto
                + ",cenco=" + filtroCenco
                + ",rutEmpleado=" + filtroRutEmpleado
                + ",startDate=" + startDate
                + ",endDate=" + endDate);

          if (request.getParameter("action").compareTo("calcular") == 0){
              ResultCRUDVO resultadoCalculo = new ResultCRUDVO();
              CalculoAsistenciaBp calculoBp=new CalculoAsistenciaBp(appProperties, userConnected);
              if (filtroEmpresa.compareTo("-1") != 0 
                        && filtroDepto.compareTo("-1") != 0
                        && filtroCenco != -1){
                    System.out.println(WEB_NAME+"[GestionFemase."
                        + "DetalleAsistenciaController]"
                        + "Calcular asistencia");
                    
                    System.out.println(WEB_NAME+"[GestionFemase."
                        + "DetalleAsistenciaController]"
                        + "Ejecutar Calculo de asistencia previo para "
                        + "empresa: " + filtroEmpresa
                        + ", depto: " + filtroDepto
                        + ", cenco: " + filtroCenco);
  
                    Date start = new Date();
                    System.out.println(WEB_NAME+"[GestionFemase."
                        + "DetalleAsistenciaController]"
                        + "Ejecutar Calculo de asistencia...");
                        
                    //set lista de empleados seleccionados como lista de string
                    ArrayList<String> listaStrEmpleados=new ArrayList<>();
                    try {
                        
                        String[] rutsSeleccionados = request.getParameterValues("rut");
                        if (rutsSeleccionados != null) {
                            for (String rut : rutsSeleccionados) {
                                System.out.println("Rut seleccionado: " + rut);
                                System.out.println(WEB_NAME+"[GestionFemase."
                                    + "DetalleAsistenciaController]"
                                    + "Rut seleccionado: " + rut);
                                listaStrEmpleados.add(rut);
                            }
                        } else {
                            System.out.println(WEB_NAME+"[GestionFemase."
                                + "DetalleAsistenciaController]"
                                + "No se seleccionó ningún rut....");
                        }
                        
//                        Enumeration<String> parameterNames = request.getParameterNames();
//                        while (parameterNames.hasMoreElements()) {
//                            String paramName = parameterNames.nextElement();
//                            if (paramName.compareTo("rut[]") == 0){
//                                String[] paramValues = request.getParameterValues(paramName);
//                                listaStrEmpleados.addAll(Arrays.asList(paramValues));
//                                System.out.println(WEB_NAME+"[GestionFemase."
//                                    + "DetalleAsistenciaController]"
//                                    + "Calcular asistencia para los empleados seleccionados");
//                                for (String aux: listaStrEmpleados) {
//                                    System.out.println(WEB_NAME+"[GestionFemase."
//                                        + "DetalleAsistenciaController]"
//                                        + "rut seleccionado: "+aux);
//                                }
//
//                            }
//                        }
                        
                        if (listaStrEmpleados.contains("todos")){
                            listaStrEmpleados = new ArrayList<>();
                            System.out.println(WEB_NAME+"[GestionFemase."
                                + "DetalleAsistenciaController]"
                                + "Calcular asistencia. Todos (json)...");
                            List<EmpleadoVO> listaEmpleados = 
                                empleadosBp.getListaEmpleadosJson(filtroEmpresa, 
                                    filtroDepto, 
                                    filtroCenco);
                            for (EmpleadoVO temp : listaEmpleados) {
                                listaStrEmpleados.add(temp.getRut());
                            }
                        }

                    } catch (Exception e) { 
                        e.printStackTrace(); 
                    }
                    
                    empleados = empleadosBp.getListaEmpleadosComplete(filtroEmpresa, 
                        filtroDepto, 
                        filtroCenco, 
                        listaStrEmpleados);
                        
                    /**
                    * Set datos a utilizar en los calculos
                    */
                    CalculoAsistenciaRunnable.setData(startDate, endDate, filtroEmpresa, listaStrEmpleados);
                    /**
                    * Inicializa las hebras.
                    * Crea una hebra para cada empleado.
                    * El calculo de asistencia es iniciado para cada uno de los empleados.
                    */
                    CalculoAsistenciaRunnable.setHebras(empleados);
                    System.out.println(WEB_NAME+"[GestionFemase."
                        + "DetalleAsistenciaController]"
                        + "FIN CALCULO ASISTENCIA...");

                    CalculoAsistenciaRunnable.listaCalculosEmpleado.clear();
                    System.out.println(WEB_NAME+"[GestionFemase."
                        + "DetalleAsistenciaController]"
                        + "Elimina estado ejecucion. "
                        + "Empresa: " + filtroEmpresa
                        + ", Depto: " + filtroDepto
                        + ", Cenco: " + filtroCenco);
                    calculoBp.deleteStatusCalculo(filtroEmpresa, 
                        filtroDepto, filtroCenco);
                        
                    /**
                    * 
                    */
                    System.out.println(WEB_NAME+"[GestionFemase."
                        + "DetalleAsistenciaController]"
                        + "Inserta registro en tabla itinerario "
                        + "ejecucion proceso. ");
                    String execUser = userConnected.getUsername();

                    ProcesoEjecucionVO ejecucion=new ProcesoEjecucionVO();
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    ejecucion.setEmpresaId(filtroEmpresa);
                    ejecucion.setDeptoId(filtroDepto);
                    ejecucion.setCencoId(filtroCenco);
                    ejecucion.setProcesoId(37);
                    ejecucion.setFechaHoraInicioEjecucion(sdf.format(start));
                    ProcesosDAO daoProcesos=new ProcesosDAO(null);
                    String result="Calculo de asistencia OK";
                    ejecucion.setFechaHoraFinEjecucion(sdf.format(new Date()));
                    ejecucion.setResultado(result);
                    ejecucion.setUsuario(execUser);
                    daoProcesos.insertItinerario(ejecucion);
                     
                    if (!resultadoCalculo.isThereError()){
                        
                        ArrayList<DetalleAsistenciaVO> detalle = 
                            detalleAsistenciaBp.getDetalleAsistencia(empleados, startDate, endDate);
                        
                        request.setAttribute("detalle_asistencia_resumen", detalle);
                        
                        Map<String, List<DetalleAsistenciaVO>> agrupado = detalle.stream()
                            .collect(Collectors.groupingBy(DetalleAsistenciaVO::getRut));
                        List<EmpleadoConDetallesAsistenciaVO> empleadosDetalles = new ArrayList<>();

                        for (Map.Entry<String, List<DetalleAsistenciaVO>> entry : agrupado.entrySet()) {
                            String rut = entry.getKey();
                            List<DetalleAsistenciaVO> detalles = entry.getValue();

                            // Obtenemos los datos generales desde el primer detalle
                            String nombre = detalles.get(0).getNombreEmpleado();
                            String centro = detalles.get(0).getCencoNombre();

                            EmpleadoConDetallesAsistenciaVO emp = new EmpleadoConDetallesAsistenciaVO();
                            emp.setRut(rut);
                            emp.setNombre(nombre);
                            emp.setCencoNombre(centro);
                            emp.setDetalles(detalles);

                            empleadosDetalles.add(emp);
                        }
                        
                        
                        request.setAttribute("empleados_detalle_asistencia", empleadosDetalles);
                        
                        
                        request.getRequestDispatcher("/procesos/calculo_asistencia_new.jsp").forward(request, response);
                        
//                        //mostrar header de calculos: ruts empleados
//                        try{
//                            //Get Total Record Count for Pagination
//                            int objectsCount = empleados.size();
//                            System.out.println(WEB_NAME+"[GestionFemase."
//                                + "DetalleAsistenciaController]"
//                                + "Calcular asistencia. Size empleados: "+objectsCount);
//                            //Convert Java Object to Json
//                            JsonElement element = gson.toJsonTree(empleados,
//                                new TypeToken<List<EmpleadoVO>>() {}.getType());
//
//                            JsonArray jsonArray = element.getAsJsonArray();
//                            String listData=jsonArray.toString();
//                            //Return Json in the format required by jTable plugin
//                            listData="{\"Result\":\"OK\",\"Records\":" + 
//                                listData+",\"TotalRecordCount\": " + 
//                                objectsCount + "}";
//                            //System.out.println(WEB_NAME+"[CalHrasTrabajadasController]json data: "+listData);
//                            response.getWriter().print(listData);
//                                //request.getRequestDispatcher("/mantenedores/mantenedoresFrmSet.jsp").forward(request, response);
//                        }catch(IOException ex){
//                            String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getMessage()+"}";
//                            response.getWriter().print(error);
//                            ex.printStackTrace();
//                        }   
                    }else{
                        //mostrar error en vista jsp
                        System.err.println("[DetalleAsistenciaController"
                            + ".processRequest()]Mostrar Error de calculo asistencia en JSP: " + resultadoCalculo.getMsgError());
                        String error="{\"Result\":\"ERROR\",\"Message\":" + resultadoCalculo.getMsgError() + "}";
                        response.setStatus(404);
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        response.getWriter().write(error);
                    }
              }else {
                  if (!resultadoCalculo.isThereError()){
                    //mostrar header de calculos: ruts empleados
                    try{
                        //Get Total Record Count for Pagination
                        int objectsCount = empleados.size();
                        System.out.println(WEB_NAME+"[GestionFemase."
                            + "DetalleAsistenciaController]"
                            + "Calcular asistencia. Size empleados: "+objectsCount);
                        //Convert Java Object to Json
                        JsonElement element = gson.toJsonTree(empleados,
                            new TypeToken<List<EmpleadoVO>>() {}.getType());

                        JsonArray jsonArray = element.getAsJsonArray();
                        String listData=jsonArray.toString();
                        //Return Json in the format required by jTable plugin
                        listData="{\"Result\":\"OK\",\"Records\":" + 
                            listData+",\"TotalRecordCount\": " + 
                            objectsCount + "}";
                        //System.out.println(WEB_NAME+"[CalHrasTrabajadasController]json data: "+listData);
                        response.getWriter().print(listData);
                        //request.getRequestDispatcher("/mantenedores/mantenedoresFrmSet.jsp").forward(request, response);
                    }catch(IOException ex){
                        String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getMessage()+"}";
                        response.getWriter().print(error);
                        ex.printStackTrace();
                    }   
                }else{
                    //mostrar error en vista jsp
                    System.err.println("[DetalleAsistenciaController"
                        + ".processRequest()]Mostrar Error de calculo asistencia en JSP: " + resultadoCalculo.getMsgError());
                    String error="{\"Result\":\"ERROR\",\"Message\":" + resultadoCalculo.getMsgError() + "}";
                    response.setStatus(404);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(error);
                }
            }
          }else if (request.getParameter("action").compareTo("listResults") == 0){
                String rutParam = request.getParameter("rut");
                String startDateParam = request.getParameter("startDate");
                String endDateParam = request.getParameter("endDate");
                session.setAttribute("startDate", startDateParam);
                session.setAttribute("endDate", endDateParam);
            
                System.out.println(WEB_NAME+"cl.femase.gestionweb.servlet."
                    + "procesos.DetalleAsistenciaController."
                    + "processRequest(). Listar detalle_asistencia."
                    + " startDateParam= " + startDateParam
                    + ", endDateParam= " + endDateParam
                    + ", rutParam= " + rutParam);
                try{
                    List<DetalleAsistenciaVO> listaDetalles = new ArrayList<>();
                    if (startDateParam!=null && startDateParam.compareTo("")!=0
                            && rutParam.compareTo("") != 0){
                        if (endDateParam==null 
                            || endDateParam.compareTo("") == 0){
                            endDateParam = startDateParam;
                        }
                        
                        listaDetalles = detalleAsistenciaBp.getDetalles(rutParam, startDateParam, endDateParam);
                    }
                    //Get Total Record Count for Pagination
                    int objectsCount = listaDetalles.size();

                    //Convert Java Object to Json
                    JsonElement element = gson.toJsonTree(listaDetalles,
                        new TypeToken<List<DetalleAsistenciaVO>>() {}.getType());

                    JsonArray jsonArray = element.getAsJsonArray();
                    String listData=jsonArray.toString();
                    //Return Json in the format required by jTable plugin
                    listData="{\"Result\":\"OK\",\"Records\":" + 
                        listData+",\"TotalRecordCount\": " + 
                        objectsCount + "}";
                    //System.out.println(WEB_NAME+"[CalHrasTrabajadasController]json data: "+listData);
                    response.getWriter().print(listData);
                    
                    if (startDate != null && startDate.compareTo("") != 0){
                        //agregar evento al log.
                        resultado.setEmpresaId(filtroEmpresa);
                        resultado.setDeptoId(filtroDepto);
                        resultado.setCencoId(filtroCenco);
                        resultado.setRutEmpleado(rutParam);
                        resultado.setDescription("Consulta asistencia-resultados."
                            + " Rut empleado: " + rutParam    
                            + ", desde: " + startDate
                            + ", hasta: " + endDate);
                        eventosBp.addEvent(resultado);
                    }
                    //request.getRequestDispatcher("/mantenedores/mantenedoresFrmSet.jsp").forward(request, response);
                }catch(IOException ex){
                    String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getMessage()+"}";
                    response.getWriter().print(error);
                    ex.printStackTrace();
                }   
            }else if (request.getParameter("action").compareTo("update") == 0){
                    String keyParam = request.getParameter("rowKey");
                    StringTokenizer keyToken = 
                        new StringTokenizer(keyParam, "|");
                    String rut      = keyToken.nextToken();
                    String fecha    = keyToken.nextToken();
                    String authAtraso = "N";
                    String authHextras = request.getParameter("autorizaHrsExtras");
                    String fechaI = (String)session.getAttribute("startDate");
                    String fechaF = (String)session.getAttribute("endDate");
                    String horasExtras = request.getParameter("hextrasHH");
                    String minutosExtras    = request.getParameter("hextrasMins");
                    String horaMinsExtrasTope   = request.getParameter("heTope");
                    //kkk
                    int intMins = 0;
                    if (minutosExtras != null) intMins = Integer.parseInt(minutosExtras);
                    if (intMins < 10) minutosExtras = "0" + intMins;
                    String hhmmExtrasAutorizadas = 
                        horasExtras + ":" + minutosExtras;
                    System.out.println(WEB_NAME+"[cl.femase.gestionweb."
                        + "servlet.procesos."
                        + "DetalleAsistenciaController.processRequest]"
                        + "Actualizar detalle_asistencia. "
                        + "Rut= " + rut 
                        + ", Fecha= " + fecha
                        + ", authExtras= " + authHextras
                        + ", hhmmExtrasTope= " + horaMinsExtrasTope    
                        + ", hhmmExtrasAutorizadas= " + hhmmExtrasAutorizadas    
                        + ", startDate= " + fechaI
                        + ", endDate= " + fechaF);
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                    //Calendar mycal=Calendar.getInstance();
                    String auxHETope        = sdf.format(mycal.getTime())+ " " +horaMinsExtrasTope+":00";
                    String auxHEAutorizadas = sdf.format(mycal.getTime())+ " " +hhmmExtrasAutorizadas+":00";
                    int comparaHras = Utilidades.comparaHoras(auxHETope, auxHEAutorizadas);
                    if (comparaHras==1){
                        System.out.println(WEB_NAME+"---->Hrs extras autorizadas "
                            + "superan el tope de hrs extras<----");
                        String error="{\"Result\":\"ERROR\",\"Message\":Hrs extras autorizadas superan el tope de hrs extras."+"}";
                        //listData = "{\"Result\":\"ERROR\",\"Message\":"+"Horas de inicio/fin no validas"+"}";
                        response.getWriter().print(error);
                    }else{
                        /**
                         * Permite actualizar las sgtes columnas de la tabla detalle_Asistencia: 
                         * 
                         * autoriza_atraso
                         * autoriza_hrsextras
                         * 
                         */
                        ResultCRUDVO doUpdate = detalleAsistenciaBp.update(rut,
                            fecha,
                            authAtraso,
                            authHextras,
                            hhmmExtrasAutorizadas,
                            resultado);

                        List<DetalleAsistenciaVO> listaDetalles 
                            = detalleAsistenciaBp.getDetalles(rut, fecha, fecha);
                        //listaObjetos.add(auxdata);

                        //Convert Java Object to Json
                        String json=gson.toJson(listaDetalles.get(0));					
                        //Return Json in the format required by jTable plugin
                        String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";											
                        response.getWriter().print(listData);
                    }
            }
        }//fin if action!=null
    }
}
