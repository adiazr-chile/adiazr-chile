package cl.femase.gestionweb.servlet.mantencion;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.DetalleAusenciaBp;
import cl.femase.gestionweb.business.MaintenanceEventsBp;
import cl.femase.gestionweb.vo.AusenciaVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.DetalleAusenciaVO;
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
import java.util.HashMap;
import java.util.StringTokenizer;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

public class DetalleAusenciaHistController extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 993L;
    
    public DetalleAusenciaHistController() {
        
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

    /**
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(true);
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
        DetalleAusenciaBp auxnegocio = new DetalleAusenciaBp(appProperties);
        MaintenanceEventsBp eventosBp = new MaintenanceEventsBp(appProperties);
        
        if(request.getParameter("action") != null){
            System.out.println(WEB_NAME+"[DetalleAusenciaHistController]"
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
            String jtSorting        = "detalle_ausencia.rut_empleado asc";
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
          
            System.out.println(WEB_NAME+"gestionwebfemase."
                + "DetalleAusenciaHistController. "
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
                System.out.println(WEB_NAME+"[DetalleAusenciaHistController]"
                    + "fullHoraInicio: " + fullHoraInicio
                    + ",fullHoraFin: " + fullHoraFin);
                auxdata.setHoraInicioFullAsStr(fullHoraInicio);
                auxdata.setHoraFinFullAsStr(fullHoraFin);
                
//////                /**
//////                * Para permisos por hora: 
//////                *  Buscar el turno correspondiente a la fecha de la ausencia
//////                *  y validar hora de entrada y salida de la ausencia vs el turno
//////                */
//////                
//////                StringTokenizer tokenfecha1 = new StringTokenizer(auxdata.getFechaInicioAsStr(), "-");
//////                String strAnio = tokenfecha1.nextToken();
//////                String strMes = tokenfecha1.nextToken();
//////                String strDia = tokenfecha1.nextToken();
//////                int codDiaInicio = 
//////                    Utilidades.getDiaSemana(Integer.parseInt(strAnio), 
//////                        Integer.parseInt(strMes), 
//////                        Integer.parseInt(strDia));
//////                EmpleadoVO infoEmpleado = 
//////                    empleadosBp.getEmpleado(auxdata.getEmpresaId(), 
//////                        auxdata.getRutEmpleado());//en duro
//////                //get turno empleado
//////                LinkedHashMap<Integer,DetalleTurnoVO> detallesTurno = 
//////                    detalleTurnoBp.getHashDetalleTurno(infoEmpleado.getIdTurno());
//////                DetalleTurnoVO auxDetalleTurno = detallesTurno.get(codDiaInicio);
//////                //detalleturno = auxListDetalleTurno.get(codDia);
//////                System.out.println(WEB_NAME+"[DetalleAusenciaHistController]"
//////                    + " Turno id= " + infoEmpleado.getIdTurno()
//////                    + ", codDia= " + codDiaInicio
//////                    + ", horaEntrada= " + auxDetalleTurno.getHoraEntrada()
//////                    + ", horaSalida= " + auxDetalleTurno.getHoraSalida());
//////                /**
//////                 * Si hra_inicio_ausencia < turno.hora_entrada o 
//////                 *    hra_fin_ausencia > turno.hora_entrada
//////                 * {
//////                 *   arrojar error...
//////                 * }
//////                 *
//////                */
//////                String h1 = auxdata.getFechaInicioAsStr()+" " +auxdata.getHoraInicioFullAsStr()+":00"; 
//////                String h2 = auxdata.getFechaInicioAsStr()+" " +auxDetalleTurno.getHoraEntrada();
//////                int aux2 = Utilidades.comparaHoras(h2,h1);
//////                if (aux2 == -1){
//////                    System.out.println(WEB_NAME+"Hora inicio ausencia es "
//////                        + "anterior a la hora entrada del turno");
//////                    horasOk = false;
//////                }
//////                
//////                h1 = auxdata.getFechaInicioAsStr()+" " +auxdata.getHoraFinFullAsStr()+":00"; 
//////                h2 = auxdata.getFechaInicioAsStr()+" " +auxDetalleTurno.getHoraSalida();
//////                aux2 = Utilidades.comparaHoras(h2,h1);
//////                if (aux2 == 1){
//////                    System.out.println(WEB_NAME+"Hora fin ausencia es "
//////                        + "posterior a la hora salida del turno");
//////                    horasOk = false;
//////                }
            }
            
            if (action.compareTo("list") == 0) {
                System.out.println(WEB_NAME+"[DetalleAusenciaHistController]"
                    + " mostrando detalles ausencias...");
                String paramEmpresa=request.getParameter("empresaId");
                String paramDepto=request.getParameter("deptoId");
                String cencoId=request.getParameter("cencoId");
                int intCenco = -1;
                
                String paramCencoID         = request.getParameter("cencoId");
                System.out.println(WEB_NAME+"[DetalleAusenciaHistController]"
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
                System.out.println(WEB_NAME+"[DetalleAusenciaHistController]"
                    + "empresa: " + paramEmpresa
                    +", depto: " + paramDepto
                    +", cenco: " + cencoId);
                
                if (cencoId != null){
                    intCenco = Integer.parseInt(cencoId);
                }
                
                int rowsCount =0;
                                
                try{
                    if ( (paramEmpresa != null && paramEmpresa.compareTo("-1") != 0)
                        && (paramDepto != null && paramDepto.compareTo("-1") != 0
                            && (request.getParameter("paramRutEmpleado") != null && request.getParameter("paramRutEmpleado").compareTo("-1") != 0))
                        && (intCenco != -1) ){
                            listaObjetos = auxnegocio.getDetallesAusenciasHist(rutEmpleado,
                                rutAutorizador, 
                                fechaIngresoInicio, 
                                fechaIngresoFin,
                                ausenciaId,
                                startPageIndex, 
                                numRecordsPerPage, 
                                jtSorting);

                            session.setAttribute("detalleAusencias|"+userConnected.getUsername(), listaObjetos);

                            //Get Total Record Count for Pagination
                            rowsCount = auxnegocio.getDetallesAusenciasHistCount(rutEmpleado,
                                rutAutorizador, 
                                fechaIngresoInicio, 
                                fechaIngresoFin,
                                ausenciaId);
                            
                                //agregar evento al log.
                                resultado.setRutEmpleado(rutEmpleado);
                                resultado.setEmpresaId(paramEmpresa);
                                resultado.setDeptoId(paramDepto);
                                resultado.setCencoId(intCenco);
                                resultado.setDescription("Consulta ausencias historicas."
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
            }
            /*else if (action.compareTo("create") == 0) {
                    String listData="";
                    if (!horasOk){
                        listData = "{\"Result\":\"ERROR\",\"Message\":"+"Horas de inicio/fin no validas"+"}";
                    }else{ 
                        ArrayList<DetalleAusenciaVO> ausenciasConflicto
                            =new ArrayList<>();
                        if (auxdata.getPermiteHora()!=null && 
                            auxdata.getPermiteHora().compareTo("N") == 0){
                            System.out.println(WEB_NAME+"Mantenedor - "
                                + "Detalle Ausencias - "
                                + "Crear detalle ausencia. "
                                + "Validar ausencias conflicto.");
                            //validar ausencias conflicto
                            ausenciasConflicto = 
                                auxnegocio.getAusenciasConflicto(auxdata.getRutEmpleado(),
                                    auxdata.getFechaInicioAsStr(),
                                    auxdata.getFechaFinAsStr());
                        }else if (auxdata.getPermiteHora().compareTo("S")==0){
                                    System.out.println(WEB_NAME+"Mantenedor - "
                                        + "Detalle Ausencias - "
                                        + "Crear detalle ausencia. "
                                        + "NO Validar ausencias conflicto.");
                        }
                        
                        if (ausenciasConflicto.isEmpty()){
                            System.out.println(WEB_NAME+"Mantenedor - "
                                + "Detalle Ausencias - "
                                + "Insertar detalle ausencia...");
                            resultado.setRutEmpleado(auxdata.getRutEmpleado());

                            MaintenanceVO doCreate = auxnegocio.insert(auxdata, resultado);					
                            listaObjetos.add(auxdata);

                            //Convert Java Object to Json
                            String json=gson.toJson(auxdata);					
                            listData="{\"Result\":\"OK\",\"Record\":"+json+"}";    
                            //Return Json in the format required by jTable plugin
                        }else {
                            System.err.println("Mantenedor - "
                                + "Detalle Ausencias - "
                                + "Hay conflicto con ausencias existentes...");
                            listData = "{\"Result\":\"ERROR\",\"Message\":"+"Ausencia conflicto"+"}";
                            //listData = return new { Result = "ERROR", Message = ex.Message };            
                        }
                    }
                    response.getWriter().print(listData);
            }else if (action.compareTo("update") == 0) {  
                        String listData="";
                        if (!horasOk){
                            listData = "{\"Result\":\"ERROR\",\"Message\":"+"Horas de inicio/fin no validas"+"}";
                        }else{ 
                            ArrayList<DetalleAusenciaVO> ausenciasConflicto
                                =new ArrayList<>();
                            if (auxdata.getPermiteHora()!=null && 
                                auxdata.getPermiteHora().compareTo("N") == 0){
                                System.out.println(WEB_NAME+"Mantenedor - "
                                    + "Detalle Ausencias - "
                                    + "Actualizar detalle ausencia. "
                                    + "Validar ausencias conflicto.");
                                //validar ausencias conflicto
                                ausenciasConflicto = 
                                    auxnegocio.getAusenciasConflicto(auxdata.getRutEmpleado(),
                                        auxdata.getFechaInicioAsStr(),
                                        auxdata.getFechaFinAsStr());
                            }else if (auxdata.getPermiteHora().compareTo("S")==0){
                                        System.out.println(WEB_NAME+"Mantenedor - "
                                            + "Detalle Ausencias - "
                                            + "Acualizar detalle ausencia. "
                                            + "NO Validar ausencias conflicto.");
                            }
                        
                            if (ausenciasConflicto.isEmpty()){
                                System.out.println(WEB_NAME+"Mantenedor - Detalle "
                                    + "Ausencias - Actualizar "
                                    + "detalle ausencia, "
                                    + "correlativo: " + auxdata.getCorrelativo()
                                    + ", tipoAusencia: " + auxdata.getNombreAusencia());
                            
                                DetalleAusenciaVO detalle = 
                                    auxnegocio.getDetalleAusenciaByCorrelativo(auxdata.getCorrelativo());
                                auxdata.setRutEmpleado(detalle.getRutEmpleado());
                                
                                MaintenanceVO doUpdate = auxnegocio.update(auxdata, resultado);
                                listaObjetos.add(auxdata);

                                //Convert Java Object to Json
                                String json=gson.toJson(auxdata);					
                                //Return Json in the format required by jTable plugin
                                listData="{\"Result\":\"OK\",\"Record\":"+json+"}";
                                if (!horasOk) listData = "{\"Result\":\"ERROR\",\"Message\":"+"Horas de inicio/fin no validas"+"}";
                                System.err.println("-->listData: "+listData);
                            }
                        }
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
                                    auxnegocio.getDetalleAusenciaByCorrelativo(auxdata.getCorrelativo());
                            
                                MaintenanceVO doUpdate = auxnegocio.delete(detalle, resultado);
                                listaObjetos.add(auxdata);

                                //Convert Java Object to Json
                                String json=gson.toJson(auxdata);					
                                //Return Json in the format required by jTable plugin
                                listData="{\"Result\":\"OK\",\"Record\":"+json+"}";
                                if (!horasOk) listData = "{\"Result\":\"ERROR\",\"Message\":"+"Horas de inicio/fin no validas"+"}";
                                System.err.println("-->listData: "+listData);
                        }
                        response.getWriter().print(listData);
            }*/
           
      }
    }
    
}
