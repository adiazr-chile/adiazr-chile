package cl.femase.gestionweb.servlet.mantencion.turnos;

import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.business.EmpleadosBp;
import cl.femase.gestionweb.business.TurnoRotativoBp;
import cl.femase.gestionweb.business.TurnosBp;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.vo.AsignacionCiclicaTurnoVO;
import cl.femase.gestionweb.vo.AsignacionTurnoRotativoVO;
import cl.femase.gestionweb.vo.DuracionVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.TurnoRotativoVO;
import cl.femase.gestionweb.vo.UsuarioVO;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.StringTokenizer;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

public class AsignacionCiclicaServlet extends BaseServlet {
    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static final long serialVersionUID = 985L;
    
    static SimpleDateFormat m_dateformat = new SimpleDateFormat("yyyy-MM-dd");
    static Locale m_localeCl = new Locale("es", "CL");
    String m_strMaxFecha = "";
    
    public AsignacionCiclicaServlet() {
        
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
     * 
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(true);
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
 
        EmpleadosBp empleadosBp         = new EmpleadosBp(appProperties);
        TurnoRotativoBp turnoRotativoBp = new TurnoRotativoBp(appProperties);
        TurnosBp turnosBp                 = new TurnosBp(appProperties);
                
        if(request.getParameter("action") != null){
            System.out.println("[AsignacionCiclicaServlet]"
                + "action is: " + request.getParameter("action"));
            String action=(String)request.getParameter("action");
            Gson gson = new Gson();
            //response.setContentType("application/json");

            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(request.getRemoteAddr());
            resultado.setType("TUR");//turnos 
            resultado.setEmpresaIdSource(userConnected.getEmpresaId());
            
            /** filtros de busqueda */
            String empresaId= null;
            String deptoId  = null;
            String cencoId  = "-1";
            String paramCencoID = request.getParameter("cencoId");
            System.out.println("[AsignacionCiclicaServlet]"
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
            int idTurnoRotativo = turnosBp.getTurnoRotativo(empresaId);
                
            if (action.compareTo("list_empleados") == 0) {
                String labelCenco = request.getParameter("labelCenco");
                System.out.println("[AsignacionCiclicaServlet]"
                    + "mostrando empleados, "
                    + "num ciclos(semanas) y duracion (anios). "
                    + "EmpresaId: " + empresaId
                    + ", deptoId: " + deptoId
                    + ", cencoId: " + cencoId
                    + ", labelCenco: " + labelCenco );
                try{
                    List<EmpleadoVO> listaEmpleados 
                            = empleadosBp.getEmpleadosByTurno(empresaId, 
                                deptoId,
                                Integer.parseInt(cencoId), 
                                idTurnoRotativo, 
                                null, 
                                0, 
                                0, 
                                "empl_rut");
                    
                    session.removeAttribute("fecha_inicio");
                    session.removeAttribute("num_ciclos");
                    session.removeAttribute("duracion");
                    session.removeAttribute("detalle_ciclo");
                    session.removeAttribute("turnos_rotativos");
                    session.removeAttribute("empleados_seleccionados");
                                        
                    session.setAttribute("empresaId", empresaId);
                    session.setAttribute("deptoId", deptoId);
                    session.setAttribute("cencoId", "" + paramCencoID);
                    session.setAttribute("soloCencoId", cencoId);
                    session.setAttribute("labelCenco", labelCenco);
                    session.setAttribute("empleados_cenco", listaEmpleados);
                    
                    
                    //Return Json in the format required by jTable plugin
                    request.getRequestDispatcher("/mantencion/turnos/asignacion_ciclica.jsp").forward(request, response);
                }catch(IOException | NumberFormatException | ServletException ex){
                    String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getMessage()+"}";
                    response.getWriter().print(error);
                    ex.printStackTrace();
                }   
            }else if (action.compareTo("mostrar_ciclo") == 0) {  
                    System.out.println("[AsignacionCiclicaServlet]Mostrar ciclo "
                        + "para asignacion de turno para cada dia");
                    String[] empleadosSelected = request.getParameterValues("list2");
                    String paramFechaInicio = request.getParameter("fecha_inicio");
                    String paramNumCiclos   = request.getParameter("num_ciclos");
                    String paramDuracion    = request.getParameter("duracion");
                    int intDiasCiclo = Integer.parseInt(paramNumCiclos);
                    int intDuracion = Integer.parseInt(paramDuracion);
                    Date fechaInicioAsDate  = null;
                    try {
                        fechaInicioAsDate   = m_dateformat.parse(paramFechaInicio);
                    } catch (ParseException ex) {
                        System.err.println("[AsignacionCiclicaServlet]"
                            + "Error al parsear fecha inicio: " + ex.toString());
                    }

                    Calendar calendarInicio = Calendar.getInstance(m_localeCl);
                    calendarInicio.setTime(fechaInicioAsDate);
                    LocalDate start ;
                    start = LocalDate.of(calendarInicio.get(Calendar.YEAR), 
                        calendarInicio.get(Calendar.MONTH) + 1 ,
                        calendarInicio.get(Calendar.DATE) );
                    LocalDate ultimoDiaSemana = start.plusDays(intDiasCiclo-1);
                    System.out.println("[AsignacionCiclicaServlet.mostrar ciclos]"
                        + "1er dia semana: " + start.toString()
                        +", ultimo dia semana: " + ultimoDiaSemana.toString());

                    System.out.println("[AsignacionCiclicaServlet."
                        + "mostrar ciclos]Asignar turnos para el ciclo");

                    List<LocalDate> dates = 
                        Utilidades.getFechas(start, ultimoDiaSemana);
                    
                    session.setAttribute("fecha_inicio", paramFechaInicio);
                    session.setAttribute("num_ciclos", paramNumCiclos);
                    session.setAttribute("duracion", paramDuracion);
                    session.setAttribute("detalle_ciclo", dates);
                    
                    //setear empleados seleccionados
                    empresaId = (String)session.getAttribute("empresaId");
                    deptoId = (String)session.getAttribute("deptoId");
                    cencoId = (String)session.getAttribute("soloCencoId");
                    
                    if (empleadosSelected != null){
                        List<EmpleadoVO> listaEmpleados=new ArrayList<>();
                        for (int x = 0; x < empleadosSelected.length; x++){
                            String rutSelected = empleadosSelected[x];
                            
                            List<EmpleadoVO> auxEmpleados 
                                = empleadosBp.getEmpleadosByTurno(empresaId, 
                                    deptoId,
                                    Integer.parseInt(cencoId), 
                                    idTurnoRotativo, 
                                    rutSelected, 
                                    0, 
                                    0, 
                                    "empl_rut");
                            
                            listaEmpleados.add(auxEmpleados.get(0));
                        }
                        System.out.println("[AsignacionCiclicaServlet]-1-Set empleados asignados en sesion...");
                        session.setAttribute("empleados_seleccionados", listaEmpleados);
                    }
                    
                    //set lista de turnos en sesion
                    session.setAttribute("turnos_rotativos", 
                        turnoRotativoBp.getTurnosAsignadosByCencoAsArrayList(empresaId, 
                            deptoId,
                            Integer.parseInt(cencoId)));
                    
                    //session.setAttribute("asignados_turno", listaEmpleadosAsignados);
                    request.getRequestDispatcher("/mantencion/turnos/asignacion_ciclica.jsp").forward(request, response);
            }else if (action.compareTo("preview_asignacion_ciclo") == 0) {
                    System.out.println("[AsignacionCiclicaServlet]Generar vista previa de los turnos "
                        + "asignados.");
                    ArrayList<AsignacionCiclicaTurnoVO> turnosDelCiclo=new ArrayList<>();
                    String[] empleadosSelected = request.getParameterValues("list2");
                    System.out.println("[AsignacionCiclicaServlet]Empleados seleccionados: "+empleadosSelected);
                    //parametros para setear los turnos
                    String fechaInicio = (String)session.getAttribute("fecha_inicio");
                    String strDuracion = (String)session.getAttribute("duracion");
                    int duracion = 0;
                    if (strDuracion != null) duracion = Integer.parseInt(strDuracion);
                    
                    String strNumCiclos = (String)session.getAttribute("num_ciclos");
                    int intDiasCiclo = 0;
                    if (strNumCiclos != null) intDiasCiclo = Integer.parseInt(strNumCiclos);        
                    
                    String[] fechas = request.getParameterValues("date");
                    if (fechas != null){
                        for (int x = 0; x < fechas.length; x++){
                            LocalDate date = Utilidades.getLocalDate(fechas[x]);
                            
                            int diaSemana = date.getDayOfWeek().getValue();
                            int idTurno = Integer.parseInt(request.getParameter("turno_" + fechas[x]));
                            System.out.println("[AsignacionCiclicaServlet]"
                                + "fecha[" + x +"] = " + fechas[x]
                                + ", turno: " + request.getParameter("turno_" + fechas[x]));
                            
                            TurnoRotativoVO infoTurno = new TurnoRotativoVO(-1,"Sin Turno Asignado");
                            String turnoName = "Sin Turno Asignado";
                            if (idTurno != -1){        
                                infoTurno = turnoRotativoBp.getTurno(idTurno);
                                turnoName = infoTurno.getNombre() 
                                    + "[" + infoTurno.getHoraEntrada()
                                    + "-" + infoTurno.getHoraSalida() + "]";
                            }
                            AsignacionCiclicaTurnoVO asignacion = 
                                new AsignacionCiclicaTurnoVO(date, 
                                    idTurno, 
                                    diaSemana, 
                                    date.getDayOfWeek().getDisplayName(TextStyle.FULL, m_localeCl),
                                    turnoName);
                            turnosDelCiclo.add(asignacion);
                        }
                    }
                    LocalDate start = Utilidades.getLocalDate(fechaInicio);
                    ArrayList<AsignacionCiclicaTurnoVO> asignacionTurnos = 
                        setTurnosCiclicos(request,
                            turnoRotativoBp, 
                            turnosDelCiclo,
                            start,
                            duracion,
                            intDiasCiclo);
                    
                    Collections.sort(asignacionTurnos);
                    
                    System.out.println("[AsignacionCiclicaServlet]"
                        + "fecha maxima de asignacion= " + m_strMaxFecha);
                    
                    /**
                     * Setear lista de empleados seleccionados
                     */
                    if (empleadosSelected != null){
                        List<EmpleadoVO> listaEmpleados=new ArrayList<>();
                        for (int x = 0; x < empleadosSelected.length; x++){
                            String rutSelected = empleadosSelected[x];
                            
                            List<EmpleadoVO> auxEmpleados 
                                = empleadosBp.getEmpleadosByTurno(empresaId, 
                                    deptoId,
                                    Integer.parseInt(cencoId), 
                                    idTurnoRotativo, 
                                    rutSelected, 
                                    0, 
                                    0, 
                                    "empl_rut");
                            
                            listaEmpleados.add(auxEmpleados.get(0));
                        }
                        System.out.println("[AsignacionCiclicaServlet]-2-Set empleados asignados en sesion...");
                        session.setAttribute("empleados_asignados", listaEmpleados);
                    }
                    
                    session.setAttribute("fecha_maxima", m_strMaxFecha);
                    session.setAttribute("asignacion_turnos|" + userConnected.getUsername(), asignacionTurnos);
                    
                    request.getRequestDispatcher("/mantencion/turnos/asignacion_ciclica_preview.jsp").forward(request, response);
            }else if (action.compareTo("guardar_asignacion_ciclo") == 0){
                        System.out.println("[AsignacionCiclicaServlet]Guardar asignacion de turnos para el ciclo");
                        List<AsignacionCiclicaTurnoVO> listaAsignacion 
                            = (List<AsignacionCiclicaTurnoVO>)session.getAttribute("asignacion_turnos"+ "|" + userConnected.getUsername());
                        List<EmpleadoVO> listaEmpleados = (List<EmpleadoVO>)session.getAttribute("empleados_asignados");
                        ArrayList<DuracionVO> duraciones = turnoRotativoBp.getDuraciones();
                        int idDuracion = -1;
                        Iterator<DuracionVO> iteraDuraciones = duraciones.iterator();
                        while(iteraDuraciones.hasNext() ) {
                            DuracionVO duracion = iteraDuraciones.next();
                            if (duracion.getNumDias() == 1) {
                                idDuracion = duracion.getId();
                                break;
                            }
                            
                        }
                        /**
                         * Por cada empleado, 
                         *  insertar las asignaciones en la tabla turno_rotativo_asignacion
                         * 
                         */
                        ArrayList<AsignacionTurnoRotativoVO> nuevasAsignaciones = new ArrayList<>();
                        ArrayList<AsignacionTurnoRotativoVO> diasSinAsignacion = new ArrayList<>();
                        
                        Iterator<EmpleadoVO> iteraEmpleados = listaEmpleados.iterator();
                        while(iteraEmpleados.hasNext() ) {
                            EmpleadoVO empleado = iteraEmpleados.next();
                            System.out.println("[AsignacionCiclicaServlet]Inicio Guardar asignacion ciclica de turnos "
                                + "rut empleado= " + empleado.getRut());
                            //iterar asignacion de turnos....
                            Iterator<AsignacionCiclicaTurnoVO> iteraAsignacion = listaAsignacion.iterator();
                            while(iteraAsignacion.hasNext() ) 
                            {
                                AsignacionCiclicaTurnoVO asignacion = iteraAsignacion.next();
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                String formattedString = asignacion.getFecha().format(formatter);
                                String fechaKey = asignacion.getFecha().toString();
                                String labelDiaSemana=
                                    asignacion.getFecha().getDayOfWeek().getDisplayName(TextStyle.FULL, m_localeCl)
                                        + " " + formattedString;
                                System.out.println("[AsignacionCiclicaServlet]"
                                    + "Fecha1= " + formattedString
                                    + ", fechaKey= " + fechaKey
                                    + ", turnoId= " + asignacion.getIdTurno());
                                
                                AsignacionTurnoRotativoVO newAsignacion=new AsignacionTurnoRotativoVO();
                                newAsignacion.setEmpresaId(empleado.getEmpresaId());
                                newAsignacion.setRutEmpleado(empleado.getRut());
                                newAsignacion.setIdTurno(asignacion.getIdTurno());
                                newAsignacion.setFechaDesde(fechaKey);
                                newAsignacion.setFechaHasta(fechaKey);
                                newAsignacion.setIdDuracion(idDuracion);
                                
                                if (asignacion.getIdTurno() != -1){
                                    nuevasAsignaciones.add(newAsignacion);
                                }else{
                                    diasSinAsignacion.add(newAsignacion);
                                }
                            } 
                            System.out.println("[AsignacionCiclicaServlet]Fin Guardar asignacion ciclica de turnos "
                                + "rut empleado= " + empleado.getRut());
                        }
                        
                        /**
                         * Iterar las asignaciones. 
                         *      Por cada fecha {
                         *          .- Eliminar asignacion para la fecha.
                         *          
                         *      }
                         */
                        turnoRotativoBp.eliminarAsignaciones(diasSinAsignacion);//eliminar dias sin asignacion de turno
                        turnoRotativoBp.eliminarAsignaciones(nuevasAsignaciones);
                        turnoRotativoBp.insertarAsignaciones(nuevasAsignaciones);
                        String downloadFile = exportResultadosToCSV(request, response);
                        session.setAttribute("downloadFile|" + userConnected.getUsername(), downloadFile);
                        request.getRequestDispatcher("/mantencion/turnos/asignacion_ciclica_resultado.jsp").forward(request, response);
            }
        }
    }
    
    /**
     *  Iterar todos los d�as del ciclo, replicando los turnos.
     * 
     *       {
     *       - Al D�aX se le asigna el  turno_A
     *       - D�aX + num_dias_ciclo= NUEVA_FECHA.
     *       Si NUEVA_FECHA no es feriado {
     *          Si  (NUEVA_FECHA >= FECHA_INICIO  AND NUEVA_FECHA <= FECHA_FIN )
     *          { 
     *                    ? A  NUEVA_FECHA se le asigna el  mismo turno que el DiaX
     *              }
     *          }Else {
     *                Mensaje: finaliza asignaci�n
     *            }
     *       }else{
     *          Mensaje: la NUEVA_FECHA es feriado.
     *       }
     * 
     */
    private ArrayList<AsignacionCiclicaTurnoVO> setTurnosCiclicos(
            HttpServletRequest request,
            TurnoRotativoBp _turnoRotativoBp,
            ArrayList<AsignacionCiclicaTurnoVO> _asignacionCiclos, 
            LocalDate _startDate, 
            int _duracion, 
            int _numDiasCiclo){
        
        ArrayList<AsignacionCiclicaTurnoVO> asignacionFinal = 
            new ArrayList<>();               
        //obtener ultimo dia de la iteracion, segun la duracion
        LocalDate ultimoDiaAsignacion = _startDate.plusYears(_duracion);
        
        java.util.Iterator<AsignacionCiclicaTurnoVO> itAsignacion = _asignacionCiclos.iterator();
        while(itAsignacion.hasNext()){
            AsignacionCiclicaTurnoVO asignacion = itAsignacion.next();
            System.out.println("\n[AsignacionCiclicaServlet.setTurnosCiclicos]"
                + "Replicando turno del dia: " + asignacion.getFecha().toString() 
                +", turno: " + asignacion.getIdTurno());
            TurnoRotativoVO infoTurno = new TurnoRotativoVO(-1, "Sin Turno Asignado");
            String turnoName = "Sin Turno Asignado";
            if (asignacion.getIdTurno() != -1){
                infoTurno = _turnoRotativoBp.getTurno(asignacion.getIdTurno());
                turnoName = infoTurno.getNombre() 
                    + "[" + infoTurno.getHoraEntrada()
                    + "-" + infoTurno.getHoraSalida() + "]";
            }
            AsignacionCiclicaTurnoVO aux 
                = new AsignacionCiclicaTurnoVO(asignacion.getFecha(), 
                        asignacion.getIdTurno(), 
                        asignacion.getFecha().getDayOfWeek().getValue(), 
                        asignacion.getFecha().getDayOfWeek().getDisplayName(TextStyle.FULL, m_localeCl),
                        turnoName);
            asignacionFinal.add(aux);
                        
            LocalDate diaSgte = asignacion.getFecha().plusDays(_numDiasCiclo);
            
            while(diaSgte.isBefore(ultimoDiaAsignacion) ||  diaSgte.isEqual(ultimoDiaAsignacion)){
                System.out.println("[AsignacionCiclicaServlet.setTurnosCiclicos]"
                    + "FECHA FUTURA: " + diaSgte.toString()
                    + ", dia de la semana: " + diaSgte.getDayOfWeek().getDisplayName(TextStyle.FULL, m_localeCl)
                    + ", turno: " + asignacion.getIdTurno());
                infoTurno = new TurnoRotativoVO(-1, "Sin Turno Asignado");
                turnoName = "Sin Turno Asignado";
                if (asignacion.getIdTurno() != -1){
                    infoTurno = _turnoRotativoBp.getTurno(asignacion.getIdTurno());
                    turnoName = infoTurno.getNombre() 
                        + "[" + infoTurno.getHoraEntrada()
                        + "-" + infoTurno.getHoraSalida() + "]";
                }
                aux 
                    = new AsignacionCiclicaTurnoVO(diaSgte, 
                            asignacion.getIdTurno(), 
                            diaSgte.getDayOfWeek().getValue(), 
                            diaSgte.getDayOfWeek().getDisplayName(TextStyle.FULL, m_localeCl),
                            turnoName);
                asignacionFinal.add(aux);
                m_strMaxFecha = diaSgte.toString();
                diaSgte = diaSgte.plusDays(_numDiasCiclo);
            }
            System.out.println("[AsignacionCiclicaServlet.setTurnosCiclicos]"
                + "Fin ciclo...");
        }
        
        return asignacionFinal;
    }
    
    protected String exportResultadosToCSV(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        String filePath="";
        PrintWriter outfile=null;
        try {
            Calendar calNow=Calendar.getInstance();
            ServletContext application = this.getServletContext();
            PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
            HttpSession session = request.getSession(true);
            UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
            Locale localeCl_1 = new Locale("es", "CL");          
            String separatorFields = ";";
            ArrayList<AsignacionCiclicaTurnoVO> asignacionTurnos = (ArrayList<AsignacionCiclicaTurnoVO>)
                session.getAttribute("asignacion_turnos"+ "|" + userConnected.getUsername());
           
            if (asignacionTurnos != null && asignacionTurnos.size() > 0){
                filePath = appProperties.getPathExportedFiles()+
                    File.separator+
                    userConnected.getUsername()+"_asignacion_turnos.csv";
                System.out.println("[AsignacionCiclicaServlet."
                    + "exportResultadosToCSV]filePath:" + filePath);
                FileWriter filewriter = new FileWriter(filePath);
                outfile     = new PrintWriter(filewriter);
                Iterator<AsignacionCiclicaTurnoVO> iterador = asignacionTurnos.listIterator();
                NumberFormat nf = NumberFormat.getNumberInstance(new Locale("es", "ES"));
                Utilidades utils=new Utilidades();
                int filas=0;

                //cabecera
                outfile.println("Fecha;Dia;Turno");

                while( iterador.hasNext() ) {
                    filas++;
                    AsignacionCiclicaTurnoVO asignacion = iterador.next();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    String formattedString = asignacion.getFecha().format(formatter);
                    String fechaKey = asignacion.getFecha().toString();
                    String labelDiaSemana=
                        asignacion.getFecha().getDayOfWeek().getDisplayName(TextStyle.FULL, localeCl_1)
                            + " " + formattedString;
                        
                    //escribir lineas en el archivo
                    outfile.print(fechaKey);
                    outfile.print(separatorFields);
                    outfile.print(labelDiaSemana);
                    outfile.print(separatorFields);

                    if (filas < asignacionTurnos.size()){
                        outfile.println(asignacion.getNombreTurno());
                    }else{
                        outfile.print(asignacion.getNombreTurno());
                    }
                }

               //Flush the output to the file
               outfile.flush();

               //Close the Print Writer
               outfile.close();

               //Close the File Writer
               filewriter.close();
            }
        } finally {
            if (outfile!=null) outfile.close();
        }

        return filePath;
    }
}
