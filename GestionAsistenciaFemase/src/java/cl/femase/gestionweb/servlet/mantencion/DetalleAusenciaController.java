package cl.femase.gestionweb.servlet.mantencion;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.DetalleAusenciaBp;
import cl.femase.gestionweb.business.DetalleTurnosBp;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.business.MaintenanceEventsBp;
import cl.femase.gestionweb.business.TurnoRotativoBp;
import cl.femase.gestionweb.business.TurnosBp;
import cl.femase.gestionweb.business.VacacionesBp;
import cl.femase.gestionweb.common.ClientInfo;
import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.vo.AusenciaVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.DetalleAusenciaVO;
import cl.femase.gestionweb.vo.DiasEfectivosVacacionesVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import cl.femase.gestionweb.vo.VacacionesVO;
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
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.StringTokenizer;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

public class DetalleAusenciaController extends BaseServlet {

    /**
     *
     */
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 993L;
    
    public DetalleAusenciaController() {
        
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
     * @throws ServletException
     * @throws IOException
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
     * @param request
     * @param response
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
    */
    @Override
    protected void processRequest(HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(true);
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
        HashMap<String, Double> parametrosSistema = (HashMap<String, Double>)session.getAttribute("parametros_sistema");
        
        DetalleAusenciaBp detAusenciaBp = new DetalleAusenciaBp(appProperties);
        //EmpleadosBp empleadosBp       = new EmpleadosBp(appProperties);
        //DetalleTurnosBp detalleTurnoBp = new DetalleTurnosBp(appProperties);
        MaintenanceEventsBp eventosBp   = new MaintenanceEventsBp(appProperties);
        VacacionesBp vacacionesBp = new VacacionesBp(appProperties);
        String source = request.getParameter("source");
        if(request.getParameter("action") != null){
            System.out.println(WEB_NAME+"[DetalleAusenciaController]"
                + "action is: " + request.getParameter("action"));
            List<DetalleAusenciaVO> listaObjetos = new ArrayList<>();
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            response.setContentType("application/json");

            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("DAU");
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
                   
            //Fetch Data from User Table
            int startPageIndex      = 0;
            int numRecordsPerPage   = 10;
            String jtSorting        = "detalle_ausencia.fecha_inicio desc";
            /** filtros de busqueda */
            String rutEmpleado=null;
            String rutAutorizador=null; 
            String fechaIngresoInicio=null; 
            String fechaIngresoFin=null;
            int ausenciaId=-1;
            boolean horasOk=true;
            
            if (request.getParameter("jtStartIndex") != null) 
                startPageIndex = Integer.parseInt(request.getParameter("jtStartIndex"));
            if (request.getParameter("jtPageSize") != null) 
                numRecordsPerPage   = Integer.parseInt(request.getParameter("jtPageSize"));
            if (request.getParameter("jtSorting") != null) 
                jtSorting   = request.getParameter("jtSorting");
            
            /** ordenamiento por las columnas definidas*/
            if (jtSorting.contains("rutEmpleado")) jtSorting = jtSorting.replaceFirst("rutEmpleado","detalle_ausencia.rut_empleado");
            else if (jtSorting.contains("fechaIngresoAsStr")) jtSorting = jtSorting.replaceFirst("fechaIngresoAsStr","fecha_ingreso");    
            else if (jtSorting.contains("idAusencia")) jtSorting = jtSorting.replaceFirst("idAusencia","ausencia_id");    
            else if (jtSorting.contains("fechaInicioAsStr")) jtSorting = jtSorting.replaceFirst("fechaInicioAsStr","fecha_inicio");    
            else if (jtSorting.contains("fechaFinAsStr")) jtSorting = jtSorting.replaceFirst("fechaFinAsStr","fecha_fin");
            else if (jtSorting.contains("rutAutorizador")) jtSorting = jtSorting.replaceFirst("rutAutorizador","rut_autoriza_ausencia");
            else if (jtSorting.contains("ausenciaAutorizada")) jtSorting = jtSorting.replaceFirst("ausenciaAutorizada","ausencia_autorizada");
            else if (jtSorting.contains("permiteHora")) jtSorting = jtSorting.replaceFirst("permiteHora","allow_hour");
            else if (jtSorting.contains("soloHoraInicio")) jtSorting = jtSorting.replaceFirst("soloHoraInicio","hora_inicio");
            else if (jtSorting.contains("soloHoraFin")) jtSorting = jtSorting.replaceFirst("soloHoraFin","hora_fin");
            
            if (request.getParameter("paramRutEmpleado") != null) 
                rutEmpleado = request.getParameter("paramRutEmpleado");
            if (request.getParameter("paramRutAutorizador") != null) 
                rutAutorizador  = request.getParameter("paramRutAutorizador");
            if (request.getParameter("paramFechaIngresoInicio") != null) 
                fechaIngresoInicio  = request.getParameter("paramFechaIngresoInicio");
            if (request.getParameter("paramFechaIngresoFin") != null) 
                fechaIngresoFin  = request.getParameter("paramFechaIngresoFin");
            if (request.getParameter("paramAusenciaId") != null) 
                ausenciaId  = Integer.parseInt(request.getParameter("paramAusenciaId"));
            
            //objeto usado para update/insert. toma los datos del formulario
            DetalleAusenciaVO auxdata = new DetalleAusenciaVO();
             
            if(request.getParameter("paramRutEmpleado")!=null){
                auxdata.setRutEmpleado(request.getParameter("paramRutEmpleado"));
            }
            if(request.getParameter("fechaIngresoAsStr")!=null){
                auxdata.setFechaIngresoAsStr(request.getParameter("fechaIngresoAsStr"));
            }
            if(request.getParameter("rutEmpleado")!=null){
                auxdata.setRutEmpleado(request.getParameter("rutEmpleado"));
            }
            if(request.getParameter("empresaId")!=null){
                auxdata.setEmpresaId(request.getParameter("empresaId"));
            }
            
            if(request.getParameter("idAusencia")!=null){
                auxdata.setIdAusencia(Integer.parseInt(request.getParameter("idAusencia")));
                HashMap<Integer,String> hashAusencias = (HashMap<Integer,String>)session.getAttribute("hashAusencias");        
                auxdata.setNombreAusencia(hashAusencias.get(auxdata.getIdAusencia()));
            }
            if(request.getParameter("fechaInicioAsStr")!=null){
                auxdata.setFechaInicioAsStr(request.getParameter("fechaInicioAsStr"));
            }
            if(request.getParameter("fechaFinAsStr")!=null){
                auxdata.setFechaFinAsStr(request.getParameter("fechaFinAsStr"));
            }         
            if(request.getParameter("rutAutorizador")!=null){
                auxdata.setRutAutorizador(request.getParameter("rutAutorizador"));
            } 
            if(request.getParameter("ausenciaAutorizada")!=null){
                auxdata.setAusenciaAutorizada(request.getParameter("ausenciaAutorizada"));
            } 
            
            if(request.getParameter("permiteHora")!=null){
                auxdata.setPermiteHora(request.getParameter("permiteHora"));
            } 
            
            if(request.getParameter("soloHoraInicio")!=null){
                auxdata.setSoloHoraInicio(request.getParameter("soloHoraInicio"));
            } 
            
            if(request.getParameter("soloMinsInicio")!=null){
                auxdata.setSoloMinsInicio(request.getParameter("soloMinsInicio"));
            } 
              
            if(request.getParameter("soloHoraFin")!=null){
                auxdata.setSoloHoraFin(request.getParameter("soloHoraFin"));
            } 
                    
            if(request.getParameter("soloMinsFin")!=null){
                auxdata.setSoloMinsFin(request.getParameter("soloMinsFin"));
            }
            
            if(request.getParameter("correlativo")!=null){
                auxdata.setCorrelativo(Integer.parseInt(request.getParameter("correlativo")));
            }
            
            //nuevos
            if(request.getParameter("diasAcumuladosVacacionesAsignadas") != null){
                auxdata.setDiasAcumuladosVacacionesAsignadas(Integer.parseInt(request.getParameter("diasAcumuladosVacacionesAsignadas")));
            }
            if(request.getParameter("saldoDiasVacacionesAsignadas") != null){
                auxdata.setSaldoDiasVacacionesAsignadas(Integer.parseInt(request.getParameter("saldoDiasVacacionesAsignadas")));
            }
            
            System.out.println(WEB_NAME+"[DetalleAusenciaController]-ANTES-"
                + "diasAcumuladosVacacionesAsignadas: " + auxdata.getDiasAcumuladosVacacionesAsignadas()
                + ", saldoDiasVacacionesAsignadas: " + auxdata.getSaldoDiasVacacionesAsignadas());
            
            if (action.compareTo("create") == 0 || action.compareTo("update") == 0) {
                if (auxdata.getFechaFinAsStr() == null 
                    || auxdata.getFechaFinAsStr().compareTo("") == 0){

                    if (auxdata.getFechaInicioAsStr() != null 
                        || auxdata.getFechaInicioAsStr().compareTo("") != 0){
                            auxdata.setFechaFinAsStr(auxdata.getFechaInicioAsStr());
                    }
                }

                if (auxdata.getFechaInicioAsStr() == null 
                    || auxdata.getFechaInicioAsStr().compareTo("") == 0){

                    if (auxdata.getFechaFinAsStr() != null 
                        || auxdata.getFechaFinAsStr().compareTo("") != 0){
                            auxdata.setFechaInicioAsStr(auxdata.getFechaFinAsStr());
                    }
                }
            }
            
            System.out.println(WEB_NAME+"gestionwebfemase."
                + "DetalleAusenciaController. "
                + "Filtros de busqueda:"
                + "Empresa: " + request.getParameter("empresaId")
                + ", empresaId(hidden): " + auxdata.getEmpresaId()
                + ", paramRutEmpleado: " + request.getParameter("paramRutEmpleado")
                + ", deptoId: " + request.getParameter("deptoId")
                + ", cencoId: " + request.getParameter("cencoId")
                + ", correlativo: " + auxdata.getCorrelativo()
                + ", permiteHora: " + auxdata.getPermiteHora()
                + ", horaInicio: " + auxdata.getSoloHoraInicio()+":"+auxdata.getSoloMinsInicio()
                + ", horaFin: " + auxdata.getSoloHoraFin()+":"+auxdata.getSoloMinsFin());
            if (auxdata.getPermiteHora().compareTo("S") == 0){
                //formar la hora en base a hrs y minutos seleccionados
                String fullHoraInicio   = "";
                
                String strHour = auxdata.getSoloHoraInicio();
                String strMins = auxdata.getSoloMinsInicio();
                int intHour = Integer.parseInt(auxdata.getSoloHoraInicio());
                int intMins = Integer.parseInt(auxdata.getSoloMinsInicio());
                if (intHour < 10) strHour = "0" + intHour;
                if (intMins < 10) strMins = "0" + intMins;
                fullHoraInicio = strHour + ":" + strMins;
                
                String fullHoraFin   = "";
                strHour = auxdata.getSoloHoraFin();
                strMins = auxdata.getSoloMinsFin();
                intHour = Integer.parseInt(auxdata.getSoloHoraFin());
                intMins = Integer.parseInt(auxdata.getSoloMinsFin());
                if (intHour < 10) strHour = "0" + intHour;
                if (intMins < 10) strMins = "0" + intMins;
                fullHoraFin = strHour + ":" + strMins;
                System.out.println(WEB_NAME+"[DetalleAusenciaController]"
                    + "fullHoraInicio: " + fullHoraInicio
                    + ",fullHoraFin: " + fullHoraFin);
                auxdata.setHoraInicioFullAsStr(fullHoraInicio);
                auxdata.setHoraFinFullAsStr(fullHoraFin);
                
            }
            String cencoId=request.getParameter("cencoId");
            String paramCencoID         = request.getParameter("cencoId");
            String paramEmpresa=request.getParameter("empresaId");
            String paramDepto=request.getParameter("deptoId");
            System.out.println(WEB_NAME+"[DetalleAusenciaController]"
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
            auxdata.setEmpresaId(paramEmpresa);
                
            if (action.compareTo("list") == 0) {
                System.out.println(WEB_NAME+"[DetalleAusenciaController]"
                    + " mostrando detalles ausencias...");
                int intCenco = -1;
                
                System.out.println(WEB_NAME+"[DetalleAusenciaController]Mostrar ausencias."
                    + "empresa: " + paramEmpresa
                    + ", depto: " + paramDepto
                    + ", cenco: " + cencoId
                    + ", runEmpleado: " + rutEmpleado
                    + ", ausenciaId: " + ausenciaId);
                
                if (cencoId != null){
                    intCenco = Integer.parseInt(cencoId);
                }
                
                int rowsCount =0;
                                
                try{
                    if ( (paramEmpresa != null && paramEmpresa.compareTo("-1") != 0)
                        && (paramDepto != null && paramDepto.compareTo("-1") != 0
                            && (request.getParameter("paramRutEmpleado") != null && request.getParameter("paramRutEmpleado").compareTo("-1") != 0))
                        && (intCenco != -1) ){
                        
                            if (source != null && source.compareTo("adm_pa") == 0){
                                listaObjetos = detAusenciaBp.getPermisosAdministrativos(source,
                                    rutEmpleado,
                                    rutAutorizador, 
                                    fechaIngresoInicio, 
                                    fechaIngresoFin,
                                    startPageIndex, 
                                    numRecordsPerPage, 
                                    jtSorting);
                            }else{
                                listaObjetos = detAusenciaBp.getDetallesAusencias(source,
                                    rutEmpleado,
                                    rutAutorizador, 
                                    fechaIngresoInicio, 
                                    fechaIngresoFin,
                                    ausenciaId,
                                    startPageIndex, 
                                    numRecordsPerPage, 
                                    jtSorting);
                            }
                            session.setAttribute("detalleAusencias|"+userConnected.getUsername(), listaObjetos);

                            //Get Total Record Count for Pagination
                            if (source != null && source.compareTo("adm_pa") == 0){
                                rowsCount = detAusenciaBp.getPermisosAdministrativosCount(source, 
                                    rutEmpleado,
                                    rutAutorizador, 
                                    fechaIngresoInicio, 
                                    fechaIngresoFin);
                            }else{    
                                rowsCount = detAusenciaBp.getDetallesAusenciasCount(source, 
                                    rutEmpleado,
                                    rutAutorizador, 
                                    fechaIngresoInicio, 
                                    fechaIngresoFin,
                                    ausenciaId);
                            }
                            //agregar evento al log.
                            resultado.setEmpresaId(paramEmpresa);
                            resultado.setDeptoId(paramDepto);
                            resultado.setCencoId(intCenco);
                            resultado.setRutEmpleado(rutEmpleado);
                            resultado.setDescription("Consulta ausencias."
                                + " Rut empleado: " + rutEmpleado    
                                + ", desde: " + fechaIngresoInicio
                                + ", hasta: " + fechaIngresoFin);
                            eventosBp.addEvent(resultado);
                        
                    }
                    //Convert Java Object to Json
                    JsonElement element = gson.toJsonTree(listaObjetos,
                            new TypeToken<List<AusenciaVO>>() {}.getType());

                    JsonArray jsonArray = element.getAsJsonArray();
                    String listData=jsonArray.toString();

                    //Return Json in the format required by jTable plugin
                    listData="{\"Result\":\"OK\",\"Records\":" + 
                            listData+",\"TotalRecordCount\": " + 
                            rowsCount + "}";
                    response.getWriter().print(listData);
                    //request.getRequestDispatcher("/mantenedores/mantenedoresFrmSet.jsp").forward(request, response);
                }catch(Exception ex){
                    String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getMessage()+"}";
                    response.getWriter().print(error);
                    ex.printStackTrace();
                }   
            }else if (action.compareTo("create") == 0) {
                    String listData     = "";
                    String mensajeFinal = "";
                    if (!horasOk){
                        listData = "{\"Result\":\"ERROR\",\"Message\":"+"Horas de inicio/fin no validas"+"}";
                        mensajeFinal= "Horas de inicio/fin no validas";
                    }else{ 
                        ArrayList<DetalleAusenciaVO> ausenciasConflicto
                            =new ArrayList<>();
                        if (auxdata.getPermiteHora()!=null && 
                            auxdata.getPermiteHora().compareTo("N") == 0){
                            System.out.println(WEB_NAME+"[DetalleAusenciaController](insert) - "
                                + "Ausencia x dias."
                                + " Validar ausencias conflicto.");
                            //validar ausencias conflicto
                            ausenciasConflicto = 
                                detAusenciaBp.getAusenciasConflicto(auxdata.getRutEmpleado(),
                                    false,
                                    auxdata.getFechaInicioAsStr(),
                                    auxdata.getFechaFinAsStr(), 
                                    null, 
                                    null);
                        }else if (auxdata.getPermiteHora().compareTo("S")==0){
                                    System.out.println(WEB_NAME+"[DetalleAusenciaController] (insert) - "
                                        + "Ausencia x horas."
                                        + " Validar ausencias conflicto.");
                                    ausenciasConflicto = 
                                        detAusenciaBp.getAusenciasConflicto(auxdata.getRutEmpleado(),
                                            true,
                                            auxdata.getFechaInicioAsStr(),
                                            auxdata.getFechaFinAsStr(), 
                                            auxdata.getHoraInicioFullAsStr(), 
                                            auxdata.getHoraFinFullAsStr());
                        }
                        if (ausenciasConflicto.isEmpty()){
                            VacacionesVO saldoVacaciones = new VacacionesVO();
                            if (auxdata.getIdAusencia() == 1){//VACACIONES
                                //despues de insertar nuevo registro de vacaciones
                                saldoVacaciones = 
                                    validarVacaciones(auxdata,
                                        null,
                                        appProperties,
                                        userConnected,
                                        request);
                                System.out.println(WEB_NAME+"[DetalleAusenciaController]post create. dias efectivos tomados: "
                                    + saldoVacaciones.getDiasEfectivos());
                                auxdata.setDiasEfectivosVacaciones(saldoVacaciones.getDiasEfectivos());
                            }
                            if (saldoVacaciones.getMensajeValidacion() == null){
                                resultado.setRutEmpleado(auxdata.getRutEmpleado());
                                auxdata.setAusenciaAutorizada("S");
                                mensajeFinal = "Datos ingresados exitosamente.";
                                if (auxdata.getIdAusencia() != Constantes.ID_AUSENCIA_VACACIONES){
                                    System.out.println(WEB_NAME+"[DetalleAusenciaController] - "
                                        + "Insertar detalle ausencia (NO Vacacion)...");
                                    MaintenanceVO doCreate = detAusenciaBp.insert(auxdata, resultado);	
                                    if (doCreate.isThereError()){
                                        mensajeFinal= "Error al crear registro "
                                            + doCreate.getMsgError();
                                    }
                                }else {//VACACIONES
                                    System.out.println(WEB_NAME+"[DetalleAusenciaController]"
                                        + "Insertar detalle ausencia (Vacacion)...");
                                    //Insertar vacacion
                                    DetalleAusenciaVO newAusencia;
                                    newAusencia = new DetalleAusenciaVO();
                                    newAusencia.setEmpresaId(auxdata.getEmpresaId());
                                    newAusencia.setRutEmpleado(auxdata.getRutEmpleado());
                                    newAusencia.setFechaInicioAsStr(auxdata.getFechaInicioAsStr());
                                    newAusencia.setFechaFinAsStr(auxdata.getFechaFinAsStr());
                                    newAusencia.setRutAutorizador(auxdata.getRutAutorizador());
                                    System.out.println(WEB_NAME+"[DetalleAusenciaController]"
                                        + "Rut autorizador: " + newAusencia.getRutAutorizador());
                                    //fijos
                                    newAusencia.setIdAusencia(Constantes.ID_AUSENCIA_VACACIONES);
                                    newAusencia.setAusenciaAutorizada("S");
                                    newAusencia.setPermiteHora("N");
                                    newAusencia.setDiasEfectivosVacaciones(saldoVacaciones.getDiasEfectivos());
                                    MaintenanceVO insertResult = new MaintenanceVO();
                                    insertResult = insertarVacacion(request, appProperties, parametrosSistema, userConnected, newAusencia);

                                    List<VacacionesVO> infoVacaciones = 
                                        vacacionesBp.getInfoVacaciones(auxdata.getEmpresaId(), 
                                            auxdata.getRutEmpleado(), -1, -1, -1, "vac.rut_empleado");
                                    saldoVacaciones = infoVacaciones.get(0);
                                        
//                                        System.out.println(WEB_NAME+"[servlet."
//                                            + "DetalleAusenciasController."
//                                            + "processRequest]Insertar vacaciones."
//                                            + "Recalcular saldo dias de vacaciones "
//                                            + "para empleado. "
//                                            + "empresa_id: " + auxdata.getEmpresaId()
//                                            +", rutEmpleado: " + auxdata.getRutEmpleado());
//                                        vacacionesBp.calculaDiasVacaciones(userConnected.getUsername(), 
//                                            auxdata.getEmpresaId(), auxdata.getRutEmpleado(), 
//                                            parametrosSistema);
//                                        
//                                        System.out.println(WEB_NAME+"[servlet."
//                                            + "DetalleAusenciasController."
//                                            + "processRequest]"
//                                            + "Actualizar saldos de vacaciones "
//                                            + "en tabla detalle_ausencia "
//                                            + "(usar nueva funcion setsaldodiasvacacionesasignadas). "
//                                            + "Run: "+ auxdata.getRutEmpleado());
//                                        detAusenciaBp.actualizaSaldosVacaciones(auxdata.getRutEmpleado());
//                                        
//                                        List<VacacionesVO> infoVacaciones = 
//                                            vacacionesBp.getInfoVacaciones(auxdata.getEmpresaId(), 
//                                                auxdata.getRutEmpleado(), -1, -1, -1, "vac.rut_empleado");
//                                        saldoVacaciones = infoVacaciones.get(0);
                                    
                                    //fin insertar ausencia VACACIONES
                                    mensajeFinal = "Vacaciones ingresadas correctamente. "
                                        + "Saldo dias vacaciones: " + saldoVacaciones.getSaldoDias()
                                        + ", dias efectivos: " + auxdata.getDiasEfectivosVacaciones();
                                }
                                
                            }else{
                                mensajeFinal= saldoVacaciones.getMensajeValidacion();
                            }
                        }else {
                            System.err.println("[DetalleAusenciaController]"
                                + "Hay conflicto con ausencias existentes...");
                            request.setAttribute("msgerror", "Ausencia conflicto");
                            mensajeFinal= "Hay conflicto con ausencias existentes...";
                            mensajeFinal += Utilidades.getAusenciasConflictoStr(ausenciasConflicto);
                            //guardar en log de eventos
                            addLogEventos(request, auxdata.getRutEmpleado(), auxdata.getEmpresaId(), null, -1, 
                                "Ingresar Vacacion: Hay conflicto con ausencias existentes.");
                        }
                    }
                    System.err.println("[DetalleAusenciaController]"
                        + "Mostrar nuevo formulario...");
                    response.setContentType("text/html;charset=UTF-8");
                    try (PrintWriter out = response.getWriter()) {
                        /* TODO output your page here. You may use following sample code. */
                        out.println(mensajeFinal);
                    }
            }else if (action.compareTo("update") == 0) {
                        String listData         = "";
                        String mensajeFinalUpd  = "";
                        if (!horasOk){
                            listData = "{\"Result\":\"ERROR\",\"Message\":"+"Horas de inicio/fin no validas"+"}";
                        }else{ 
                            DetalleAusenciaVO detalleActual = 
                                detAusenciaBp.getDetalleAusenciaByCorrelativo(auxdata.getCorrelativo());
                            System.out.println(WEB_NAME+"[DetalleAusenciaController]update - "
                                + "Datos existentes:"
                                + "rutEmpleado: " + detalleActual.getRutEmpleado()
                                + ", correlativo: " + auxdata.getCorrelativo()
                                + ", fechaInicio: " + detalleActual.getFechaInicioAsStr()
                                + ", fechaFin: " + detalleActual.getFechaFinAsStr()
                                + ", tipoAusencia: " + detalleActual.getTipoAusencia()
                                + ", idAusencia: " + detalleActual.getIdAusencia());
                            boolean sePuedeModificar = true;
                            SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd", new Locale("es","CL"));
                            Calendar cal = Calendar.getInstance(new Locale("es","CL"));
                            Date fechaActual = cal.getTime();                     
                            try{
                                Date dteFecFinAusencia = sdf3.parse(detalleActual.getFechaFinAsStr());
                                if (dteFecFinAusencia.before(fechaActual)){
                                    System.out.println(WEB_NAME+"[DetalleAusenciaController]"
                                        + "Fecha fin ausencia pasada, no se puede modificar...");
                                    sePuedeModificar = false;
                                }
                            }catch(ParseException pex){
                                System.err.println("[DetalleAusenciaController]"
                                    + "Error al parsear "
                                    + "fecha fin ausencia: " + pex.toString());
                            }
                            if (sePuedeModificar){
                                    auxdata.setRutEmpleado(detalleActual.getRutEmpleado());
                                    ArrayList<DetalleAusenciaVO> ausenciasConflicto
                                        =new ArrayList<>();
                                    if (auxdata.getPermiteHora()!=null && 
                                        auxdata.getPermiteHora().compareTo("N") == 0){
                                        System.out.println(WEB_NAME+"[DetalleAusenciaController]"
                                            + "Mantenedor - "
                                            + "Detalle Ausencias (update) - "
                                            + "Ausencia x dias."
                                            + " Validar ausencias conflicto.");
                                        //validar ausencias conflicto 
                                        ausenciasConflicto = 
                                            detAusenciaBp.getAusenciasConflicto(auxdata.getRutEmpleado(),
                                                false,
                                                auxdata.getFechaInicioAsStr(),
                                                auxdata.getFechaFinAsStr(),
                                                null,
                                                null);
                                    }else if (auxdata.getPermiteHora().compareTo("S")==0){
                                                System.out.println(WEB_NAME+"[DetalleAusenciaController]"
                                                    + "Mantenedor - "
                                                    + "Detalle Ausencias (update) - Ausencia x horas."
                                                    + " Validar ausencias conflicto.");
                                                ausenciasConflicto = 
                                                detAusenciaBp.getAusenciasConflicto(auxdata.getRutEmpleado(),
                                                    true,
                                                    auxdata.getFechaInicioAsStr(),
                                                    auxdata.getFechaFinAsStr(), 
                                                    auxdata.getHoraInicioFullAsStr(), 
                                                    auxdata.getHoraFinFullAsStr());
                                    }
                                    boolean hayConflicto=false;
                                    Iterator<DetalleAusenciaVO> itAc = ausenciasConflicto.iterator();
                                    while(itAc.hasNext()){
                                        if (itAc.next().getCorrelativo() != auxdata.getCorrelativo()){
                                            hayConflicto = true;
                                            break;
                                        }
                                    }

                                    if (!hayConflicto){
                                        VacacionesVO saldoVacaciones = new VacacionesVO();
                                        if (auxdata.getIdAusencia() == 1){//VACACIONES
                                            //saldo_vacaciones = saldo_actual - ( dias_efectivos vacaciones que se estan modificando ) 
                                            System.out.println(WEB_NAME+"[DetalleAusenciaController]"
                                                + "Actualizar Vacacion existente. "
                                                + "Correlativo ausencia: " + detalleActual.getCorrelativo()
                                                + ", diasEfectivos actuales: " + detalleActual.getDiasEfectivosVacaciones());
                                            saldoVacaciones = 
                                                validarVacaciones(auxdata,
                                                    detalleActual,
                                                    appProperties,
                                                    userConnected,
                                                    request);
                                            auxdata.setDiasEfectivosVacaciones(saldoVacaciones.getDiasEfectivos());
                                        }

                                        System.out.println(WEB_NAME+"[DetalleAusenciaController]"
                                            + "Admin detalle Ausencias - Actualizar "
                                            + ". Msg Validacion vacaciones: "  + saldoVacaciones.getMensajeValidacion());
                                        if (saldoVacaciones.getMensajeValidacion() == null){
                                            System.out.println(WEB_NAME+"[DetalleAusenciaController]"
                                                + "Mantenedor - Detalle "
                                                + "Ausencias - Actualizar "
                                                + "detalle ausencia, "
                                                + "correlativo: " + auxdata.getCorrelativo()
                                                + ", tipoAusencia: " + auxdata.getNombreAusencia());

                                            auxdata.setAusenciaAutorizada("S");
                                            MaintenanceVO doUpdate = detAusenciaBp.update(auxdata, resultado);
                                            mensajeFinalUpd = "Datos actualizados exitosamente.";
                                            listaObjetos.add(auxdata);

                                            if (!doUpdate.isThereError() 
                                                && auxdata.getIdAusencia() == 1){//VACACIONES
                                                //no setea dias efectivos al update
                                                System.out.println(WEB_NAME+"[DetalleAusenciaController]"
                                                    + "Update vacaciones."
                                                    + "ReCalcular saldo dias de vacaciones "
                                                    + "para empleado. "
                                                    + "empresa_id: " + auxdata.getEmpresaId()
                                                    +", rutEmpleado: " + auxdata.getRutEmpleado());
                                                vacacionesBp.calculaDiasVacaciones(userConnected.getUsername(), 
                                                    auxdata.getEmpresaId(), auxdata.getRutEmpleado(), 
                                                    parametrosSistema);
                                                System.out.println(WEB_NAME+"[DetalleAusenciaController]"
                                                    + "Actualizar saldos de vacaciones "
                                                    + "en tabla detalle_ausencia "
                                                    + "(usar nueva funcion setsaldodiasvacacionesasignadas). "
                                                    + "Run: "+ auxdata.getRutEmpleado());
                                                detAusenciaBp.actualizaSaldosVacaciones(auxdata.getRutEmpleado());
                                                
                                                
                                            }
                                            
                                            DetalleAusenciaVO ausenciaPostUpdate = 
                                                detAusenciaBp.getDetalleAusenciaByCorrelativo(auxdata.getCorrelativo());
                                            
                                            System.out.println(WEB_NAME+"[DetalleAusenciaController]"
                                                + "Despues de Actualizar ausencia: "
                                                + "RUN: " + auxdata.getRutEmpleado()
                                                + ", correlativo: " + auxdata.getCorrelativo()
                                                + ", diasAcumuladosVacacionesAsignadas: " + ausenciaPostUpdate.getDiasAcumuladosVacacionesAsignadas()
                                                + ", saldoDiasVacacionesAsignadas: " + ausenciaPostUpdate.getSaldoDiasVacacionesAsignadas());
                                            //Convert Java Object to Json
                                            String json=gson.toJson(ausenciaPostUpdate);					
                                            //Return Json in the format required by jTable plugin
                                            listData="{\"Result\":\"OK\",\"Record\":"+json+"}";
                                            String aux2 = "\""+"Horas de inicio/fin no validas"+"\"";
                                            if (!horasOk) listData = "{\"Result\":\"ERROR\",\"Message\":"+aux2+"}";
                                            System.err.println("-->listData: "+listData);
                                        }else{
                                            System.out.println(WEB_NAME+"[DetalleAusenciaController]"
                                                + "Mantenedor.DetalleAusencias - Actualizar "
                                                + "Hay conflicto con el saldo de dias de vacaciones");
                                            //guardar en log de eventos
                                            addLogEventos(request, detalleActual.getRutEmpleado(), detalleActual.getEmpresaId(), null, -1, 
                                                "Modificar ausencia: Hay conflicto con el saldo de dias de vacaciones: " + saldoVacaciones.getMensajeValidacion());
                                            mensajeFinalUpd= "\"" + saldoVacaciones.getMensajeValidacion() + "\"";
                                            listData = "{\"Result\":\"ERROR\",\"Message\":"+mensajeFinalUpd+"}";
                                        }    
                                    }else{
                                        System.out.println(WEB_NAME+"[DetalleAusenciaController]"
                                            + "Mantenedor.DetalleAusencias - Actualizar "
                                            + "Hay conflicto con ausencia existentes");
                                        mensajeFinalUpd= "\""+"Hay conflicto con ausencias existentes..."+"\"";
                                        //guardar en log de eventos
                                        addLogEventos(request, detalleActual.getRutEmpleado(), detalleActual.getEmpresaId(), null, -1, 
                                            "Modificar ausencia: Hay conflicto con ausencias existentes.");
//                                        mensajeFinal = "Hay conflicto con ausencias existentes...<br>";
//                                        mensajeFinal += Utilidades.getAusenciasConflictoStr(ausenciasConflicto);
                                        listData = "{\"Result\":\"ERROR\",\"Message\":"+mensajeFinalUpd+"}";
                                    }
                            }else{
                                mensajeFinalUpd= "\""+"Esta ausencia no se puede modificar. Ausencia pasada."+"\"";
                                //guardar en log de eventos
                                addLogEventos(request, detalleActual.getRutEmpleado(), detalleActual.getEmpresaId(), null, -1, 
                                    "Modificar ausencia: Esta ausencia no se puede modificar (Fecha pasada).");
                                listData = "{\"Result\":\"ERROR\",\"Message\":"+mensajeFinalUpd+"}";
                            }
                        }      
                        System.out.println(WEB_NAME+"[DetalleAusenciaController]"
                            + "Update."
                            + " mensaje json a retornar: "+listData);
                        response.getWriter().print(listData);
            }else if (action.compareTo("delete") == 0) {  
                        String listData="";
                        if (!horasOk){
                            listData = "{\"Result\":\"ERROR\",\"Message\":"+"Horas de inicio/fin no validas"+"}";
                        }else{ 
                            System.out.println(WEB_NAME+"Mantenedor - Detalle "
                                + "Ausencias - Eliminar "
                                + "detalle ausencia, correlativo: " + auxdata.getCorrelativo());
                                
                            DetalleAusenciaVO detalle = 
                                detAusenciaBp.getDetalleAusenciaByCorrelativo(auxdata.getCorrelativo());
                            
                            MaintenanceVO doDelete = detAusenciaBp.delete(detalle, resultado);
                            listaObjetos.add(auxdata);
                            System.out.println(WEB_NAME+"[servlet."
                                + "DetalleAusenciasController."
                                + "processRequest]Delete vacacion. "
                                + "doDelete.isThereError()? "+doDelete.isThereError()
                                +", idAusencia: "+detalle.getIdAusencia());
                            if (!doDelete.isThereError() 
                                && detalle.getIdAusencia() == 1){//VACACIONES
                                    System.out.println(WEB_NAME+"[servlet."
                                        + "DetalleAusenciaController."
                                        + "processRequest]Delete vacacion."
                                        + "Recalcular saldo dias de vacaciones "
                                        + "para empleado. "
                                        + "empresa_id: " + detalle.getEmpresaId()
                                        +", rutEmpleado: " + detalle.getRutEmpleado());
                                vacacionesBp.calculaDiasVacaciones(userConnected.getUsername(), 
                                    detalle.getEmpresaId(), detalle.getRutEmpleado(), 
                                    parametrosSistema);
                                System.out.println(WEB_NAME+"[servlet."
                                    + "DetalleAusenciaController."
                                    + "processRequest]"
                                    + "Actualizar saldos de vacaciones "
                                    + "en tabla detalle_ausencia "
                                    + "(usar nueva funcion setsaldodiasvacacionesasignadas). "
                                    + "Run: "+ detalle.getRutEmpleado());
                                //DetalleAusenciaBp detAusenciaBp = new DetalleAusenciaBp(appProperties);
                                detAusenciaBp.actualizaSaldosVacaciones(detalle.getRutEmpleado());
                                
                               
                            }
                            //Convert Java Object to Json
                            String json=gson.toJson(auxdata);					
                            //Return Json in the format required by jTable plugin
                            listData="{\"Result\":\"OK\",\"Record\":"+json+"}";
                            //if (!horasOk) listData = "{\"Result\":\"ERROR\",\"Message\":"+"Horas de inicio/fin no validas"+"}";
                            String aux2 = "\""+"Horas de inicio/fin no validas"+"\"";
                            if (!horasOk) listData = "{\"Result\":\"ERROR\",\"Message\":"+aux2+"}";
                            System.err.println("-->listData: " + listData);
                        }
                        response.getWriter().print(listData);
            }
           
        }
    }
    
    /**
    * Add evento al log
    */
    private void addLogEventos(HttpServletRequest _request, 
            String _rutEmpleado,
            String _empresaId, 
            String _deptoId, 
            int _cencoId,
            String _detalleEvento){
        
        HttpSession session         = _request.getSession(true);
        ServletContext application  = this.getServletContext();
        PropertiesVO appProperties  = (PropertiesVO)application.getAttribute("appProperties");
        UsuarioVO userConnected     = (UsuarioVO)session.getAttribute("usuarioObj");
        MaintenanceEventsBp eventosBp   = new MaintenanceEventsBp(appProperties);
        
        MaintenanceEventVO resultado = new MaintenanceEventVO();
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.printClientInfo(_request);
        resultado.setOperatingSystem(clientInfo.getClientOS(_request));
        resultado.setBrowserName(clientInfo.getClientBrowser(_request));
        resultado.setUsername(userConnected.getUsername());
        resultado.setDatetime(new Date());
        resultado.setUserIP(_request.getRemoteAddr());
        resultado.setType("DAU");
        resultado.setEmpresaIdSource(userConnected.getEmpresaId());
        
        //agregar evento al log.
        resultado.setRutEmpleado(_rutEmpleado);
        resultado.setEmpresaId(_empresaId);
        resultado.setDeptoId(_deptoId);
        resultado.setCencoId(_cencoId);
        resultado.setDescription(_detalleEvento);
        
        eventosBp.addEvent(resultado);
    }
    
    /**
    * 
    * Valida si los dias de vacaciones seleccionados estan disponibles
    *   1.- Obtener valor de 'vacaciones'.'saldo_dias' --->dias disponibles de vacaciones
    * 
    *   2.- Se calculan los dias de vacaciones que se estan solicitando. 
    *       Esto se realiza en base al rango de fechas seleccionado (inicio y fin) --->dias_solicitados
    *       2.1 Se deben contabilizar solo los dias en que le corresponde turno. Sab y Domingo no se deben contabilizar
    * 
    *   3.- Si (dias_solicitados > saldo_dias) {
    *           --> retornar 'los dias solicitados ([dias_solicitados]) superan el saldo de dias disponibles: [saldo_dias]'
    *       }sino{
    *           --> vacaciones correctas. Actualizar saldo
    *       }
    *           
    */
    private VacacionesVO validarVacaciones(DetalleAusenciaVO _datosAusencia,
            DetalleAusenciaVO _datosAusenciaExistente,
            PropertiesVO _appProperties, 
            UsuarioVO _userConnected, 
            HttpServletRequest _request){
        VacacionesVO saldoVacaciones=new VacacionesVO();
                
        ////String mensaje = null;
        EmpleadosBp empleadoBp  = new EmpleadosBp(_appProperties);
        TurnosBp turnoBp        = new TurnosBp(_appProperties);
        DetalleTurnosBp detalleTurnoBp = new DetalleTurnosBp(_appProperties);
        TurnoRotativoBp turnoRotBp = new TurnoRotativoBp(_appProperties);
        VacacionesBp vacacionesBp = new VacacionesBp(_appProperties);
               
        MaintenanceEventVO resultado=new MaintenanceEventVO();
        resultado.setUsername(_userConnected.getUsername());
        resultado.setDatetime(new Date());
        resultado.setUserIP(_request.getRemoteAddr());
        resultado.setType("DAU");
        resultado.setEmpresaIdSource(_userConnected.getEmpresaId());
        
//        int idTurnoRotativo = turnoBp.getTurnoRotativo(_datosAusencia.getEmpresaId());
//        EmpleadoVO infoEmpleado = 
//            empleadoBp.getEmpleado(_datosAusencia.getEmpresaId(), 
//                _datosAusencia.getRutEmpleado());
        
        int diasSolicitados=0;
        /*
        *   1.- Obtener valor de 'vacaciones'.'saldo_dias' --->dias disponibles de vacaciones
        * 
        *   2.- Se calculan los dias de vacaciones que se estan solicitando. 
        *       Esto se realiza en base al rango de fechas seleccionado (inicio y fin) --->dias_solicitados
        *       2.1 Se deben contabilizar solo los dias en que le corresponde turno. Sab y Domingo no se deben contabilizar
        * 
        *   3.- Si (dias_solicitados > saldo_dias) {
        *           --> retornar 'los dias solicitados ([dias_solicitados]) superan el saldo de dias disponibles: [saldo_dias]'
        *       }sino{
        *           Vacaciones correctas.
        *           Actualizar saldo de dias de vacaciones.
        *       }
        */
        
        List<VacacionesVO> infoVacaciones = 
            vacacionesBp.getInfoVacaciones(_datosAusencia.getEmpresaId(), 
                _datosAusencia.getRutEmpleado(), -1, -1, -1, "vac.rut_empleado");
        
        System.out.println(WEB_NAME+"[servlet."
            + "DetalleAusenciaController.validarVacaciones]"
            + "EmpresaId: " + _datosAusencia.getEmpresaId()
            + ", rutEmpleado: " + _datosAusencia.getRutEmpleado()
            + ", fecha inicio: " + _datosAusencia.getFechaInicioAsStr()
            + ", fecha fin: " + _datosAusencia.getFechaFinAsStr());

        if (infoVacaciones.isEmpty()){
            System.out.println(WEB_NAME+"[servlet."
                + "DetalleAusenciaController.validarVacaciones]"
                + "EmpresaId: " + _datosAusencia.getEmpresaId()
                + ", rutEmpleado: " + _datosAusencia.getRutEmpleado()
                + ", No tiene informacion de vacaciones (saldo)");
            saldoVacaciones.setMensajeValidacion("No tiene informacion de vacaciones (saldo)");
        }else{
            //03-02-2020
            String fechaInicioVacaciones;
            String fechaFinVacaciones;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try{
                Date date1 = sdf.parse(_datosAusencia.getFechaInicioAsStr());
                Date date2 = sdf.parse(_datosAusencia.getFechaFinAsStr());
                fechaInicioVacaciones = _datosAusencia.getFechaInicioAsStr();
                fechaFinVacaciones = _datosAusencia.getFechaFinAsStr();
            }catch(ParseException pex){
                fechaInicioVacaciones = Utilidades.getFechaYYYYmmdd(_datosAusencia.getFechaInicioAsStr());
                fechaFinVacaciones = Utilidades.getFechaYYYYmmdd(_datosAusencia.getFechaFinAsStr());
            }
                        
            System.out.println(WEB_NAME+"[servlet."
                + "DetalleAusenciaController.validarVacaciones]"
                + "Iterar fechas en el rango. "
                + "Inicio: " + fechaInicioVacaciones
                + ", Fin: " + fechaFinVacaciones);
            saldoVacaciones = infoVacaciones.get(0);
            double saldoDias = saldoVacaciones.getSaldoDias();
            
            double diasEfectivosActuales = 0;
            if (_datosAusenciaExistente != null) diasEfectivosActuales = _datosAusenciaExistente.getDiasEfectivosVacaciones();
            
            diasSolicitados = 
                vacacionesBp.getDiasEfectivos(fechaInicioVacaciones, 
                    fechaFinVacaciones, 
                    saldoVacaciones.getDiasEspeciales(),
                    _datosAusencia.getEmpresaId(),
                    _datosAusencia.getRutEmpleado());
            double nuevoSaldoDias = saldoDias + diasEfectivosActuales;
            
            System.out.println(WEB_NAME+"[servlet."
                + "DetalleAusenciaController.validarVacaciones]"
                + "EmpresaId: " + _datosAusencia.getEmpresaId()
                + ", rutEmpleado: " + _datosAusencia.getRutEmpleado()
                + ", dias solicitados: " + diasSolicitados
                + ", dias efectivos vacacion existente (DEF): " + diasEfectivosActuales    
                + ", dias disponibles(a la fecha) (SALDO_DIAS): " + saldoDias
                + ", nuevo saldo dias disponibles(SALDO_DIAS + DEF): " + nuevoSaldoDias);
     
            //2020-03-15. Se deshabilita validacion de dias solicitados vs el saldo actual.
            if (diasSolicitados > nuevoSaldoDias) {
                System.out.println(WEB_NAME+"[servlet."
                    + "DetalleAusenciaController.validarVacaciones]"
                    + "EmpresaId: " + _datosAusencia.getEmpresaId()
                    + ", rutEmpleado: " + _datosAusencia.getRutEmpleado()
                    + ", los dias solicitados ("+diasSolicitados+") "
                    + "superan el saldo de dias disponibles: "+nuevoSaldoDias);
                
                saldoVacaciones.setMensajeValidacion("Los dias solicitados "
                    + "("+diasSolicitados+") superan el saldo "
                    + "de dias disponibles: " + nuevoSaldoDias);
                //enviar correo al director?
            }else{
                System.out.println(WEB_NAME+"[servlet."
                    + "DetalleAusenciaController.validarVacaciones]"
                    + "EmpresaId: " + _datosAusencia.getEmpresaId()
                    + ", rutEmpleado: " + _datosAusencia.getRutEmpleado()
                    + ", dias solicitados(A): " + diasSolicitados);
     
                double saldoFinal = nuevoSaldoDias - diasSolicitados;
                saldoVacaciones.setSaldoDias(saldoFinal);
                saldoVacaciones.setDiasEfectivos(diasSolicitados);
                //_datosAusencia.getFechaInicioAsStr(), _datosAusencia.getFechaFinAsStr()
                saldoVacaciones.setFechaInicioUltimasVacaciones(fechaInicioVacaciones);
                saldoVacaciones.setFechaFinUltimasVacaciones(fechaFinVacaciones);
                vacacionesBp.updateSaldoYUltimasVacaciones(saldoVacaciones, resultado);
                System.out.println(WEB_NAME+"[servlet."
                    + "DetalleAusenciaController.validarVacaciones]"
                    + "EmpresaId: " + _datosAusencia.getEmpresaId()
                    + ", rutEmpleado: " + _datosAusencia.getRutEmpleado()
                    + ", dias solicitados(B): " + saldoVacaciones.getDiasEfectivos());
            }
        }
       
        return saldoVacaciones;
    }
    
    /**
    * Insertar ausencia=Vacacion
    */
    private MaintenanceVO insertarVacacion(HttpServletRequest _request,
            PropertiesVO _appProperties,
            HashMap<String, Double> _parametrosSistema,
            UsuarioVO _userConnected, 
            DetalleAusenciaVO _ausencia){
       
        System.out.println(WEB_NAME+"[DetalleAusenciaController.insertarVacacion]"
            + "Insertar detalle ausencia (VACACION)");
        VacacionesBp vacaciones = new VacacionesBp(_appProperties);
        
        DiasEfectivosVacacionesVO objDE = vacaciones.getDesgloseDiasVacaciones(_ausencia);
        _ausencia.setDiasEfectivosVBA(objDE.getDiasEfectivosVBA());
        _ausencia.setDiasEfectivosVP(objDE.getDiasEfectivosVP());
        _ausencia.setSaldoVBAPreVacaciones(objDE.getSaldoVBAPreVacaciones());
        _ausencia.setSaldoVPPreVacaciones(objDE.getSaldoVPPreVacaciones());
        _ausencia.setSaldoVBAPostVacaciones(objDE.getSaldoVBAPostVacaciones());
        _ausencia.setSaldoVPPostVacaciones(objDE.getSaldoVPPostVacaciones());
                
        String mensajeFinal = null;
        DetalleAusenciaBp ausenciasBp   = new DetalleAusenciaBp(_appProperties);
//        VacacionesBp vacacionesBp       = new VacacionesBp(_appProperties);
//        VacacionesVO saldoVacaciones = new VacacionesVO();
                
        MaintenanceEventVO resultado=new MaintenanceEventVO();
        resultado.setUsername(_userConnected.getUsername());
        resultado.setDatetime(new Date());
        resultado.setUserIP(_request.getRemoteAddr());
        resultado.setType("DAU");
        resultado.setEmpresaIdSource(_userConnected.getEmpresaId());
                                        
        resultado.setRutEmpleado(_ausencia.getRutEmpleado());
        System.out.println(WEB_NAME+"[DetalleAusenciaController.insertarVacacion]"
            + "Insertar detalle ausencia (VACACION)");
        MaintenanceVO doCreate = ausenciasBp.insertaVacacion(_ausencia, resultado);
        
        if (doCreate.isThereError()){
            mensajeFinal= "Error al insertar vacacion " + doCreate.getMsgError();
            doCreate.setMsg(mensajeFinal);
        }else{
            System.out.println(WEB_NAME+"[DetalleAusenciaController."
                + "insertarVacacion]"
                + "Actualizar saldos de vacaciones "
                + "en tabla detalle_ausencia (usar nueva funcion setsaldodiasvacacionesasignadas). "
                + "Run: "+ _ausencia.getRutEmpleado());
            ausenciasBp.actualizaSaldosVacaciones(_ausencia.getRutEmpleado());
            
            System.out.println(WEB_NAME+"[DetalleAusenciaController."
                + "insertarVacacion]"
                + "Actualizar saldos de vacaciones "
                + "en tabla detalle_ausencia (usar nueva funcion setsaldodiasvacacionesasignadas_vba). "
                + "Run: "+ _ausencia.getRutEmpleado());
            ausenciasBp.actualizaSaldosVacacionesVBA(_ausencia.getRutEmpleado());
                        
            //Actualizar saldos en tabla vacaciones: columnas saldo_dias_vba y saldo_dias_vp
            System.out.println(WEB_NAME+"[DetalleAusenciaController."
                + "insertarVacacion]"
                + "Actualizar saldos en tabla vacaciones: columnas saldo_dias_vba y saldo_dias_vp");
            VacacionesVO objVacaciones = new VacacionesVO();
            objVacaciones.setEmpresaId(_ausencia.getEmpresaId());
            objVacaciones.setRutEmpleado(_ausencia.getRutEmpleado());
            objVacaciones.setSaldoDiasVBA(objDE.getSaldoVBAPostVacaciones());
            objVacaciones.setSaldoDiasVP(objDE.getSaldoVPPostVacaciones());
            vacaciones.updateSaldosVacacionesVBAyVP(objVacaciones);
            
        }
        System.out.println(WEB_NAME+"[DetalleAusenciaController.insertarVacacion]"
            + "Saliendo del metodo OK.");
        return doCreate;
    }
    
}
