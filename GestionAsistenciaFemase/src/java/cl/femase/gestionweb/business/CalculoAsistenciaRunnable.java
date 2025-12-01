/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.business;

import static cl.femase.gestionweb.business.BaseBp.WEB_NAME;
import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.dao.LogErrorDAO;
import cl.femase.gestionweb.vo.DetalleAsistenciaToInsertVO;
import cl.femase.gestionweb.vo.DetalleAsistenciaVO;
import cl.femase.gestionweb.vo.DetalleAusenciaJsonVO;
import cl.femase.gestionweb.vo.DetalleTurnoVO;
import cl.femase.gestionweb.vo.DiferenciaHorasVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.InfoFeriadoVO;
import cl.femase.gestionweb.vo.LogErrorVO;
import cl.femase.gestionweb.vo.MarcaJsonVO;
import cl.femase.gestionweb.vo.MarcaVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.TurnoVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;

/**
 *
 * @author Alexander
 */
public class CalculoAsistenciaRunnable extends BaseBp implements Runnable{
    
    private static UsuarioVO usuarioEnSesion;
    private EmpleadoVO empleado;
    private CalculoAsistenciaNew calculadorAsistencia;
    public static LinkedHashMap<String, ArrayList<DetalleAsistenciaVO>> listaCalculosEmpleado = 
        new LinkedHashMap<>();
    private static boolean isHistorico = false;    
    private static final TiempoExtraBp m_tiempoExtraBp = new TiempoExtraBp(new PropertiesVO());
    private static final CalendarioFeriadoBp m_feriadosBp = new CalendarioFeriadoBp(new PropertiesVO());
    private static final TurnosBp m_turnosBp = new TurnosBp(new PropertiesVO());
    private static final DetalleTurnosBp m_detalleTurnoBp = new DetalleTurnosBp(new PropertiesVO());
    private static final DetalleAusenciaBp m_detalleAusenciaBp = new DetalleAusenciaBp(new PropertiesVO());
    private static final TurnoRotativoBp m_turnoRotativoBp=new TurnoRotativoBp(new PropertiesVO()); 
    private static cl.femase.gestionweb.dao.DetalleAsistenciaDAO detalleAsistenciaDao;
    //hash a utilizar
    private static String[] m_fechasCalculo;
    private static HashMap<String,Integer> m_hashTiposHE;
    //private static HashMap<String,String> m_hashFeriados;
    private static List<TurnoVO> m_listaTurnos;
    private static final HashMap<String,Integer> m_hashFechas = new HashMap<>();
    
    static Calendar mycal = Calendar.getInstance(new Locale("es","CL"));
    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd H:m:s");
    static SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
    /**
     * Lista de ausencias para cada rut
     * La key de cada item en la lista es: rut+"|"+fecha
     */
    private static final LinkedHashMap<String, ArrayList<DetalleAusenciaJsonVO>> m_listaAusencias = 
        new LinkedHashMap<>();
    /**
     * Lista de marcas para cada rut
     * La key de cada item en la lista es: empresaId + "|" + rut + "|" + fecha
     */
    private static final LinkedHashMap<String, LinkedHashMap<String,MarcaVO>> m_listaMarcas = 
        new LinkedHashMap<>();
    
    private static final LinkedHashMap<Integer, LinkedHashMap<Integer,DetalleTurnoVO>> m_listaDetalleTurnos = 
        new LinkedHashMap<>();
    
    /**
     * Lista con detalle de dias laborales para turnos rotativos para cada rut
     * La key de cada item en la lista es: empresaId + "|" + rut + "|" + anio + "|"+ mes
     */
    private static final LinkedHashMap<String, DetalleTurnoVO> m_listaAsignacionTurnosRotativos = 
        new LinkedHashMap<>();
    
    /**
    * 20210725-001:
    * 
    *   Lista con las fechas y los datos existentes en calendario_feriados.
    *   Los datos son los resultantes de invocar a la funcion validaFechaFeriado por cada fecha del rango
    * 
    */
    private static LinkedHashMap<String, InfoFeriadoVO> m_fechasCalendarioFeriados = 
        new LinkedHashMap<>();
    private static String m_startDate;
    private static String m_endDate;
    private static String m_empresaId;
    
    public CalculoAsistenciaRunnable(EmpleadoVO empleado, 
            CalculoAsistenciaNew calculadorAsistencia,
            UsuarioVO _usuario) {
        
        this.empleado = empleado;
        this.calculadorAsistencia = calculadorAsistencia;
        this.detalleAsistenciaDao = new cl.femase.gestionweb.dao.DetalleAsistenciaDAO(null);
        this.usuarioEnSesion = _usuario;
    }
    
    /**
     * Obtiene todos los datos que seran utilizados durante el calculo de
     * asistencia
     * @param _startDate
     * @param _endDate
     * @param _empresaId
     * @param _listaStrEmpleados
     */
    public static void setData(String _startDate,
            String _endDate,
            String _empresaId,
            ArrayList<String> _listaStrEmpleados){
    
        setParameters(_empresaId, _startDate, _endDate);
        setAusencias(_listaStrEmpleados);
        setMarcas(_empresaId, _listaStrEmpleados);
        setAsignacionTurnoRotativo(_empresaId, _listaStrEmpleados);
        isHistorico = false;
    }
    
    /**
     * Obtiene todos los datos que seran utilizados durante el calculo de
     * asistencia.
     * Usa las tablas historicas de marcas y ausencias
     * 
     * @param _startDate
     * @param _endDate
     * @param _empresaId
     * @param _listaStrEmpleados
     */
    public static void setDataHist(String _startDate,
            String _endDate,
            String _empresaId,
            ArrayList<String> _listaStrEmpleados){
    
        setParameters(_empresaId, _startDate, _endDate);
        setAusenciasHist(_listaStrEmpleados);
        setMarcasHist(_empresaId, _listaStrEmpleados);
        setAsignacionTurnoRotativo(_empresaId, _listaStrEmpleados);
        System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaRunnable."
            + "setDataHist]seteo flag de calculo historico");
        
        isHistorico = true;
    }
    
    /**
    * 
    * @param _listaEmpleados
    */
    public static void setHebras(List<EmpleadoVO> _listaEmpleados){
         
        int i = 0;
        CalendarioFeriadoBp bpFeriados    = new CalendarioFeriadoBp(new PropertiesVO());
        //long init = System.currentTimeMillis();  // Instante inicial del procesamiento
        ExecutorService executor = Executors.newFixedThreadPool(_listaEmpleados.size());
        for (EmpleadoVO empleado: _listaEmpleados) {
            if (empleado != null){
                CalculoAsistenciaNew calculador1 = new CalculoAsistenciaNew("hebra_"+i, usuarioEnSesion);
                //set hash con todos los datos a usar
                calculador1.m_fechasCalculo = m_fechasCalculo;    
                calculador1.m_listaDetalleTurnos = m_listaDetalleTurnos;
                calculador1.m_hashTiposHE=m_hashTiposHE;
                //calculador1.m_hashFeriados=m_hashFeriados;
                calculador1.m_hashFechas=m_hashFechas;
                calculador1.m_listaAsignacionTurnosRotativos=m_listaAsignacionTurnosRotativos;
                calculador1.m_listaMarcas=m_listaMarcas;
                calculador1.m_listaAusencias=m_listaAusencias;
            
                /**
                * Inicio 20210725-001
                * Carga en memoria la info de la tabla calendario_feriado segun rango de fechas
                * seleccionado. En cada fecha se tiene la info de si es feriado o no.
                * De ser feriado, se indica que feriado es y su tipo.
                * 
                */
                m_fechasCalendarioFeriados = bpFeriados.getFechas(m_empresaId, 
                    empleado.getRut(), 
                    m_startDate, 
                    m_endDate);
                calculador1.m_fechasCalendarioFeriados = m_fechasCalendarioFeriados;
                
                Runnable proceso = new CalculoAsistenciaRunnable(empleado, calculador1, usuarioEnSesion);
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaRunnable."
                    + "setHebras]add hebra para rut--> "+empleado.getRut());
                executor.execute(proceso);
                i++;
            }
        }
        
        executor.shutdown();	// Cierro el Executor
        while (!executor.isTerminated()) {
             //Espero a que terminen de ejecutarse todos los procesos 
             //para pasar a las siguientes instrucciones
//            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaRunnable."
//                + "Aun no termina la ejecucion de las Hebras...");
        }
        
        System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaRunnable."
            + "Ha finalizado la ejecucion de las Hebras...");
        
        DetalleAsistenciaBp calculoBp = new DetalleAsistenciaBp(new PropertiesVO(), usuarioEnSesion); 
        ArrayList<DetalleAsistenciaToInsertVO> listaAsistencia = 
            new ArrayList<>();
        //if (!resultado.isThereError()){
                /**
                 * Guardar en BD el resultado de los calculos
                 * de asistencia 
                 */
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaRunnable.setHebras] "
                    + "Inicio guardar calculos en BD...");

                //***inicio llenar datos para los insert
                //Guardar en Base de datos los calculos realizados...
                //Iterar listaFinalCalculos
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaRunnable.setHebras]."
                    + "Lista Calculos empleado.size= " + listaCalculosEmpleado.size() );
                Iterator<ArrayList<DetalleAsistenciaVO>> itFechas 
                    = listaCalculosEmpleado.values().iterator();
                while (itFechas.hasNext())//while 9
                {   
                    ArrayList<DetalleAsistenciaVO> datosFecha = itFechas.next();
                    Iterator<DetalleAsistenciaVO> itDetails = datosFecha.iterator();
                    while (itDetails.hasNext())//while 9
                    {   
                        DetalleAsistenciaVO calculosFecha = itDetails.next();
                        if (calculosFecha.getFechaEntradaMarca()!=null){
                            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaRunnable.setHebras]."
                                + " add To Insert: "+calculosFecha.toString());
                            
                            /**
                            * 09-12-2023: Seteo de horas No trabajadas
                            * Si (total_del_dia > 0 Y total_del_dia < horas_teoricas){
                            *    horas_no_trabajadas = total_del_dia - horas_teoricas
                            *    detalle_asistencia.hrs_no_trabajadas = horas_no_trabajadas
                            * }
                            */
                            if (calculosFecha.getHorasTeoricas()>0 && calculosFecha.getHrsTrabajadas() != null){
                                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaRunnable.setHebras]."
                                    + "Calcular hrs no trabajadas");
                                String horasNoTrabajadas = calculaHorasNoTrabajadas(calculosFecha);
                                calculosFecha.setHrsNoTrabajadas(horasNoTrabajadas);
                            }
                            
                            if (calculosFecha.getHolguraMinutos() > 0){
                                
                                if (calculosFecha.getHhmmAtraso() != null 
                                    && calculosFecha.getHhmmAtraso().compareTo("") != 0){
                                        System.out.println(WEB_NAME+"[CalculoAsistenciaRunnable]"
                                            + "Verificar si atraso supera minutos de holgura. "
                                            + "hhmmAtraso= " + calculosFecha.getHhmmAtraso()
                                            + ", minsHolgura= " + calculosFecha.getHolguraMinutos());
                                        String atrasoReal = 
                                            getAtrasoReal(calculosFecha.getHhmmAtraso(),calculosFecha.getHolguraMinutos());    
                                        System.out.println(WEB_NAME+"[CalculoAsistenciaRunnable]"
                                            + "Atraso ajustado = " + atrasoReal);
                                        calculosFecha.setHhmmAtraso(atrasoReal);
                                }
                            }    
                            DetalleAsistenciaToInsertVO toInsert 
                                = new DetalleAsistenciaToInsertVO(calculosFecha);
                            listaAsistencia.add(toInsert);
                        }
                    }
                }//fin while 9

                if (listaAsistencia.size() > 0){
                    System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaRunnable.setHebras]. "
                        + "Num Empleados "
                        + "a actualizar detalle asistencia= "+listaAsistencia.size());
                    //Abrir conexion a BD
                    calculoBp.openDbConnection();
                    System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaRunnable]setHebras. "
                        + "Rescatar detalle de horas extras autorizadas previamente "
                        + "(antes de eliminar registros e insertar nuevos valores)...");
                    ArrayList<DetalleAsistenciaVO> dataHrsExtras = 
                        detalleAsistenciaDao.getDetallesHorasExtras(_listaEmpleados, m_startDate, m_endDate);
                    System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaRunnable]setHebras. "
                        + "Num registros con data de Horas Extras= " + dataHrsExtras.size());
                    
                    //if (!dataHrsExtras.isEmpty())    
                    //{
                        System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaRunnable]setHebras. "
                            + "Eliminar datos de calculos de asistencia existentes...");
                        calculoBp.deleteList(listaAsistencia);

                        System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaRunnable]setHebras. "
                            + "Guardar nuevos datos de calculos de asistencia ...");
                        calculoBp.saveList(listaAsistencia);
                        
                        if (!dataHrsExtras.isEmpty()){
                            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]calculaAsistencia. "
                                + "Modifica data de hrs extras autorizadas previamente");
                            for (int j = 0; j < dataHrsExtras.size(); j++) {
                                DetalleAsistenciaVO detalle = dataHrsExtras.get(j);
                                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaRunnable]setHebras. "
                                    + "Set data horas extras, "
                                    + "rut: " + detalle.getRut()
                                    + ", fecha entrada marca: " + detalle.getFechaEntradaMarca()
                                    + ", Hora Mins Extras: " + detalle.getHoraMinsExtras()
                                    + ", Autoriza Hrs Extras: " + detalle.getAutorizaHrsExtras()
                                    + ", Horas MinsExtras Autorizadas: " + detalle.getHorasMinsExtrasAutorizadas());

                                calculoBp.updateDataHorasExtras(detalle.getRut(),
                                    detalle.getFechaEntradaMarca(), 
                                    detalle.getHoraMinsExtras(), 
                                    detalle.getAutorizaHrsExtras(), 
                                    detalle.getHorasMinsExtrasAutorizadas());

                            }
                        }
                    //}
                    //calculoBp.setMarcasProcesadas(listaAsistencia);
                     //cerrar conexion a BD
                    calculoBp.closeDbConnection();
                }else{
                    System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaRunnable]setHebras. "
                        + "No hay data de Horas Extras...");
                }
                
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaRunnable.setHebras]."
                    + "Fin guardar calculos en BD");
            //}
    }
    
    @Override
    public void run() {
        System.out.println(WEB_NAME+"[GestionFemase."
            + "CalculoAsistenciaRunnable.run]invocar metodo calculaAsistencia"
            + " para empleado "
            + "rut: "+empleado.getRut()+", iteracion: "+empleado.getIteracion()
            +", calculo historico? " + isHistorico);
        ArrayList<DetalleAsistenciaVO> dataFechasRut 
            = this.calculadorAsistencia.calculaAsistencia(empleado, 
            empleado.getIteracion(), isHistorico);
        System.out.println(WEB_NAME+"[GestionFemase."
            + "CalculoAsistenciaRunnable.run]add calculos rut: "+empleado.getRut());
        listaCalculosEmpleado.put(empleado.getRut()+"|"+empleado.getIteracion(), dataFechasRut);
    }
    
    /**
    * 
    *    Si (total_del_dia > 0 Y total_del_dia < horas_teoricas){
    *            horas_no_trabajadas = horas_teoricas - total_del_dia 
    *            detalle_asistencia.hrs_no_trabajadas = horas_no_trabajadas
    *    }
    * 
    */
    private static String calculaHorasNoTrabajadas(DetalleAsistenciaVO _calculosFecha){
        String horasNoTrabajadas = "-";
        String fechaActuals1 = dbDateFormat.format(mycal.getTime());
        String horadesde = fechaActuals1 + " " + _calculosFecha.getHoraEntradaTeorica();
        String horahasta = fechaActuals1 + " " + _calculosFecha.getHoraSalidaTeorica();
        DiferenciaHorasVO difHrsTeoricas = Utilidades.getTimeDifference(horadesde,horahasta);
        String hhmmHrsTeoricasMenosColacion = Utilidades.restarMinsHora(difHrsTeoricas.getStrDiferenciaHorasMinutos(),_calculosFecha.getMinutosColacion());
        System.out.println("[calculaHorasNoTrabajadas]difHrsTeoricas= " + difHrsTeoricas.getStrDiferenciaHorasMinutos()
            + ", menos mins de colacion= " + hhmmHrsTeoricasMenosColacion);
        int dif33 = Utilidades.comparaHoras(fechaActuals1 + " " + _calculosFecha.getHrsTrabajadas() + ":00", 
            fechaActuals1 + " " +hhmmHrsTeoricasMenosColacion);
        if (dif33 == 1){
            DiferenciaHorasVO objHrsNoTrabajadas = 
                Utilidades.getTimeDifference(fechaActuals1 + " " +hhmmHrsTeoricasMenosColacion,
                        fechaActuals1 + " " + _calculosFecha.getHrsTrabajadas() + ":00");
            horasNoTrabajadas = objHrsNoTrabajadas.getStrDiferenciaHorasMinutos();
        }
        
        return horasNoTrabajadas;
    }
    
    
    /**
    * 
    */
    private static void setParameters(String _empresaId, 
            String _startDate, 
            String _endDate){
        System.out.println(WEB_NAME+"[GestionFemase."
            + "CalculoAsistenciaRunnable.setParameters()]INICIO "
            + ". StartDate: " + _startDate+", endDate: "+_endDate);
        
        m_fechasCalculo = Utilidades.getFechas(_startDate, _endDate);
        m_hashTiposHE = m_tiempoExtraBp.getTiposHrasExtras();
//        System.out.println(WEB_NAME+"[GestionFemase."
//            + "CalculoAsistenciaRunnable.setParameters()]INICIO "
//            + ". getFeriados para el rango de fechas seleccionado");
//        //m_hashFeriados = m_feriadosBp.getHashFeriados(_startDate, _endDate);
        
        m_startDate = _startDate;
        m_endDate   = _endDate;
        m_empresaId = _empresaId;
        
        m_listaTurnos = m_turnosBp.getTurnos(_empresaId, null,-1, 0, 0, "id_turno");
        List<TurnoVO> listaTurnos = 
            m_turnosBp.getTurnos(_empresaId, null, -1, 0, 0, "id_turno");
        for (TurnoVO turno : listaTurnos) {
//            System.out.println(WEB_NAME+"[GestionFemase."
//                + "CalculoAsistenciaRunnable."
//                + "setParameters()]"
//                + "turno.empresa_id: "+turno.getEmpresaId()
//                + "turno.turno_id: "+turno.getId());
        
            if (turno.getId() != -1){
                LinkedHashMap<Integer,DetalleTurnoVO> detalle = 
                    m_detalleTurnoBp.getHashDetalleTurno(turno.getId());
                m_listaDetalleTurnos.put(turno.getId(), detalle);
            }
        }
        
        //seteando dia de la semana para cada fecha
        for( int i = 0; i <= m_fechasCalculo.length - 1; i++)
        {
            String itFecha  = m_fechasCalculo[i];
            StringTokenizer tokenfecha1=new StringTokenizer(itFecha, "-");
            String strAnio = tokenfecha1.nextToken();
            String strMes = tokenfecha1.nextToken();
            String strDia = tokenfecha1.nextToken();
            int codDia = 
                Utilidades.getDiaSemana(Integer.parseInt(strAnio), 
                    Integer.parseInt(strMes), 
                    Integer.parseInt(strDia));
            m_hashFechas.put(itFecha, codDia);
        }
        
        System.out.println(WEB_NAME+"[GestionFemase."
            + "CalculoAsistenciaRunnable.setParameters()]FIN "
            + ". StartDate: " + _startDate+", endDate: "+_endDate);
    }
                
    private static void setAusencias(List<String> _rutsEmpleados){
        System.out.println(WEB_NAME+"[GestionFemase."
            + "CalculoAsistenciaRunnable.setAusencias()]INICIO "
            + "set lista de ausencias para todos los ruts "
            + "y fechas en el rango (usa get_ausencias_json)...");
        Gson gson = new GsonBuilder().create();
        if (_rutsEmpleados.size() > 0){
            for (String rutEmpleado : _rutsEmpleados) {
                for( int i = 0; i <= m_fechasCalculo.length - 1; i++)
                {
                    String itFecha = m_fechasCalculo[i];
                    //new usa funcion 'get_ausencias_json'
                    String jsonOutput = 
                        m_detalleAusenciaBp.getAusenciasJson(rutEmpleado, 
                         m_fechasCalculo[i]);
                    System.out.println(WEB_NAME+"[GestionFemase."
                        + "CalculoAsistenciaRunnable."
                        + "setAusencias]jsonOutput: "+jsonOutput);
                    Type listType = new TypeToken<ArrayList<DetalleAusenciaJsonVO>>() {}.getType();
                    ArrayList<DetalleAusenciaJsonVO> ausencias = new Gson().fromJson(jsonOutput, listType);                
                    System.out.println(WEB_NAME+"[GestionFemase."
                        + "CalculoAsistenciaRunnable."
                        + "setAusencias]add rut empleado "+rutEmpleado
                        +", fecha: "+itFecha);
                    m_listaAusencias.put(rutEmpleado+"|"+itFecha, ausencias);
                }
                
            }
        }
        System.out.println(WEB_NAME+"[GestionFemase."
            + "CalculoAsistenciaRunnable.setAusencias()]FIN "
            + "set lista de ausencias para todos los ruts "
            + "y fechas en el rango...");
    }
    
    private static void setAusenciasHist(List<String> _rutsEmpleados){
        System.out.println(WEB_NAME+"[GestionFemase."
            + "CalculoAsistenciaRunnable.setAusenciasHist()]INICIO "
            + "set lista de ausencias para todos los ruts "
            + "y fechas en el rango (usa get_ausencias_json)...");
        Gson gson = new GsonBuilder().create();
        if (_rutsEmpleados.size() > 0){
            for (String rutEmpleado : _rutsEmpleados) {
                for( int i = 0; i <= m_fechasCalculo.length - 1; i++)
                {
                    String itFecha = m_fechasCalculo[i];
                    //new usa funcion 'get_ausencias_json'
                    String jsonOutput = 
                        m_detalleAusenciaBp.getAusenciasHistJson(rutEmpleado, 
                         m_fechasCalculo[i]);
                    System.out.println(WEB_NAME+"[GestionFemase."
                        + "CalculoAsistenciaRunnable."
                        + "setAusenciasHist]jsonOutput: "+jsonOutput);
                    Type listType = new TypeToken<ArrayList<DetalleAusenciaJsonVO>>() {}.getType();
                    ArrayList<DetalleAusenciaJsonVO> ausencias = new Gson().fromJson(jsonOutput, listType);                
                    System.out.println(WEB_NAME+"[GestionFemase."
                        + "CalculoAsistenciaRunnable."
                        + "setAusenciasHist]add rut empleado "+rutEmpleado
                        +", fecha: "+itFecha);
                    m_listaAusencias.put(rutEmpleado+"|"+itFecha, ausencias);
                }
                
            }
        }
        System.out.println(WEB_NAME+"[GestionFemase."
            + "CalculoAsistenciaRunnable.setAusenciasHist()]FIN "
            + "set lista de ausencias para todos los ruts "
            + "y fechas en el rango...");
    }
    
    /**
    * 
    * @param _empresaId
    * @param _rutsEmpleados
    */
    private static void setMarcas(String _empresaId, List<String> _rutsEmpleados){
        System.out.println(WEB_NAME+"[GestionFemase."
            + "CalculoAsistenciaRunnable.setMarcas()]INICIO "
            + "set lista de marcas para todos los ruts "
            + "y fechas en el rango (usa get_marcas_json)...");
        MarcasBp auxMarcasBp = new MarcasBp(new PropertiesVO());
        MarcaVO auxmarca = new MarcaVO();
        if (_rutsEmpleados.size() > 0){
            for (String rutEmpleado : _rutsEmpleados) {
                for( int i = 0; i <= m_fechasCalculo.length - 1; i++)
                {
                    String itFecha = m_fechasCalculo[i];
                    
                    //new usa funcion 'get_marcas_json'
                    String jsonOutput = 
                        auxMarcasBp.getMarcasJson(_empresaId,rutEmpleado, 
                         m_fechasCalculo[i],m_fechasCalculo[i]);
                    System.out.println(WEB_NAME+"[GestionFemase."
                        + "CalculoAsistenciaRunnable.setMarcas]"
                        + "getMarcasJson jsonOutput: " + jsonOutput);
                                        
                    Type listType = new TypeToken<ArrayList<MarcaJsonVO>>() {}.getType();
                    ArrayList<MarcaJsonVO> marcas = new Gson().fromJson(jsonOutput, listType);                
                    
                    LinkedHashMap<String,MarcaVO> auxmarcas = new LinkedHashMap<>();
                    if (marcas != null){
                        for(int x=0; x < marcas.size(); x++) {
                            MarcaJsonVO itmarca = marcas.get(x); 
                            System.out.println(WEB_NAME+"[GestionFemase."
                            + "CalculoAsistenciaRunnable.setMarcas]"
                                + "iteracion: "
                                + "rut:" + itmarca.getRutempleado()
                                + ", fecha:" + itmarca.getFechahora()
                                + ", tipo:" + itmarca.getTipomarca()    
                            );
                            
                            auxmarca = new MarcaVO();
                            auxmarca.setCodDispositivo(itmarca.getCoddispositivo());
                            auxmarca.setEmpresaCod(itmarca.getEmpresacod());
                            auxmarca.setRutEmpleado(itmarca.getRutempleado());
                            auxmarca.setFechaHora(itmarca.getFechahora());
                            auxmarca.setFechaHoraCalculos(itmarca.getFechahoracalculos());
                            auxmarca.setTipoMarca(itmarca.getTipomarca());
                            auxmarca.setComentario(itmarca.getComentario());
                                
                            auxmarcas.put(""+auxmarca.getTipoMarca(), auxmarca);
                        }
                        
                    }
                    //usar json....
//                    LinkedHashMap<String, MarcaVO> marcas = 
//                        auxMarcasBp.getMarcasEmpleado(_empresaId,rutEmpleado, 
//                         m_fechasCalculo[i],m_fechasCalculo[i]);
                    m_listaMarcas.put(_empresaId 
                            + "|" + rutEmpleado 
                            + "|" + itFecha, auxmarcas);
                }
            }
        }
        System.out.println(WEB_NAME+"[GestionFemase."
            + "CalculoAsistenciaRunnable.setMarcas()]FIN "
            + "set lista de marcas para todos los ruts "
            + "y fechas en el rango...");
    }
    
    /**
     * Set marcas desde tabla historica
     * 
     * @param _empresaId
     * @param _rutsEmpleados
     */
    private static void setMarcasHist(String _empresaId, List<String> _rutsEmpleados){
        System.out.println(WEB_NAME+"[GestionFemase."
            + "CalculoAsistenciaRunnable.setMarcasHist()]INICIO "
            + "set lista de marcas para todos los ruts "
            + "y fechas en el rango (usa get_marcas_json)...");
        MarcasBp auxMarcasBp = new MarcasBp(new PropertiesVO());
        MarcaVO auxmarca = new MarcaVO();
        if (_rutsEmpleados.size() > 0){
            for (String rutEmpleado : _rutsEmpleados) {
                for( int i = 0; i <= m_fechasCalculo.length - 1; i++)
                {
                    String itFecha = m_fechasCalculo[i];
                    
                    //new usa funcion 'get_marcas_json'
                    String jsonOutput = 
                        auxMarcasBp.getMarcasHistJson(_empresaId,rutEmpleado, 
                         m_fechasCalculo[i],m_fechasCalculo[i]);
                    System.out.println(WEB_NAME+"[GestionFemase."
                        + "CalculoAsistenciaRunnable.setMarcasHist]"
                        + "getMarcasJson jsonOutput: " + jsonOutput);
                                        
                    Type listType = new TypeToken<ArrayList<MarcaJsonVO>>() {}.getType();
                    ArrayList<MarcaJsonVO> marcas = new Gson().fromJson(jsonOutput, listType);                
                    
                    LinkedHashMap<String,MarcaVO> auxmarcas = new LinkedHashMap<>();
                    if (marcas != null){
                        for(int x=0; x < marcas.size(); x++) {
                            MarcaJsonVO itmarca = marcas.get(x); 
                            System.out.println(WEB_NAME+"[GestionFemase."
                            + "CalculoAsistenciaRunnable.setMarcasHist]"
                                + "iteracion: "
                                + "rut:" + itmarca.getRutempleado()
                                + ", fecha:" + itmarca.getFechahora()
                                + ", tipo:" + itmarca.getTipomarca()    
                            );
                            
                            auxmarca = new MarcaVO();
                            auxmarca.setCodDispositivo(itmarca.getCoddispositivo());
                            auxmarca.setEmpresaCod(itmarca.getEmpresacod());
                            auxmarca.setRutEmpleado(itmarca.getRutempleado());
                            auxmarca.setFechaHora(itmarca.getFechahora());
                            auxmarca.setFechaHoraCalculos(itmarca.getFechahoracalculos());
                            auxmarca.setTipoMarca(itmarca.getTipomarca());
                            auxmarca.setComentario(itmarca.getComentario());
                                
                            auxmarcas.put(""+auxmarca.getTipoMarca(), auxmarca);
                        }
                    }
                    //usar json....
//                    LinkedHashMap<String, MarcaVO> marcas = 
//                        auxMarcasBp.getMarcasEmpleado(_empresaId,rutEmpleado, 
//                         m_fechasCalculo[i],m_fechasCalculo[i]);
                    m_listaMarcas.put(_empresaId 
                            + "|" + rutEmpleado 
                            + "|" + itFecha, auxmarcas);
                }
            }
        }
        System.out.println(WEB_NAME+"[GestionFemase."
            + "CalculoAsistenciaRunnable.setMarcasHist()]FIN "
            + "set lista de marcas para todos los ruts "
            + "y fechas en el rango...");
    }
    
    private static void setAsignacionTurnoRotativo(String _empresaId, List<String> _rutsEmpleados){
        System.out.println(WEB_NAME+"[GestionFemase."
            + "CalculoAsistenciaRunnable.setAsignacionTurnoRotativo()]INICIO "
            + "set lista de asignacion turnos rotativos para todos los ruts "
            + "y fechas en el rango (get_turno_rotativo_by_fecha_json)");
        if (_rutsEmpleados.size() > 0){
            for (String rutEmpleado : _rutsEmpleados) {
                for( int i = 0; i <= m_fechasCalculo.length - 1; i++)
                {
                    String itFecha = m_fechasCalculo[i];
                    StringTokenizer tokenfecha1=new StringTokenizer(itFecha, "-");
                    String strAnio = tokenfecha1.nextToken();
                    String strMes = tokenfecha1.nextToken();
                    
                    String itemKey=_empresaId + "|" 
                        + rutEmpleado+ "|" 
                        + Integer.parseInt(strAnio)+ "|" 
                        + Integer.parseInt(strMes);
//                    DetalleTurnoVO detalleTurnoRotLaboral = 
//                        m_detalleTurnoRotaBp.getDetalleTurnoLaboralByFecha(_empresaId, 
//                        rutEmpleado, 
//                        Integer.parseInt(strAnio), 
//                        Integer.parseInt(strMes), itFecha);
                    
                    //new usa funcion 'get_marcas_json'
                    String jsonOutput = 
                        m_turnoRotativoBp.getAsignacionTurnoByFechaJson(_empresaId, rutEmpleado, 
                        itFecha);
                    DetalleTurnoVO asignacionTurnoRotativo = new Gson().fromJson(jsonOutput, 
                    DetalleTurnoVO.class);
                    
                    if (asignacionTurnoRotativo!=null){
                        System.out.println(WEB_NAME+"[GestionFemase."
                            + "CalculoAsistenciaRunnable.setAsignacionTurnoRotativo()]rut: " + rutEmpleado
                            + ", itemKey: " + itemKey
                            + ", itFecha: " + itFecha
                            + ", idTurnoRot: " + asignacionTurnoRotativo.getIdTurno()
                            + ", entrada: " + asignacionTurnoRotativo.getHoraEntrada()
                            + ", salida: " + asignacionTurnoRotativo.getHoraSalida()
                            + ", nocturno(S/N)?: " + asignacionTurnoRotativo.getNocturno()    
                        );
                    }
                    
                    m_listaAsignacionTurnosRotativos.put(itFecha, asignacionTurnoRotativo);
                }
            }
        }
        System.out.println(WEB_NAME+"[GestionFemase."
            + "CalculoAsistenciaRunnable.setAsignacionTurnoRotativo()]FIN "
            + "set lista de asignacion turnos rotativos para todos los ruts "
            + "y fechas en el rango...");
    }
    
    public static void monitorThread(Thread monitorMe) {

        while(monitorMe.isAlive())
         {
         try{   
           StackTraceElement[] threadStacktrace=monitorMe.getStackTrace();
           
           System.out.println(WEB_NAME+"[GestionFemase."
                + "CalculoAsistenciaRunnable."
                + "monitorThread] "+monitorMe.getName() +" is Alive and it's state ="+monitorMe.getState()
                   +" ||  Execution is in method : ("+threadStacktrace[0].getClassName()
                   +"::"+threadStacktrace[0].getMethodName()+") @line"+threadStacktrace[0].getLineNumber());  

               TimeUnit.MILLISECONDS.sleep(700);
           }catch(InterruptedException ex){
                System.err.println("[CalculoAsistenciaRunnable]"
                    + "Error: " + ex.toString());
                String clase = "CalculoAsistenciaRunnable";
                String excdetail = Arrays.toString(ex.getStackTrace());
                String exclabel = ex.toString();
                JSONObject jsonObj = Utilidades.generateErrorMessage(clase, ex);
                LogErrorDAO logDao  = new LogErrorDAO();
                
                LogErrorVO log      = new LogErrorVO();
                log.setUserName(usuarioEnSesion.getUsername());
                log.setModulo(Constantes.LOG_MODULO_ASISTENCIA);
                log.setEvento(Constantes.LOG_EVENTO_CALCULO_ASISTENCIA);
                log.setLabel(exclabel);
                log.setDetalle(jsonObj.toString());
                logDao.insert(log);
                ex.printStackTrace();
           }
                /* since threadStacktrace may be empty upon reference since Thread A may be terminated after the monitorMe.getStackTrace(); call*/
         }
        System.out.println(WEB_NAME+"[GestionFemase."
                + "CalculoAsistenciaRunnable."
                + "monitorThread] " + monitorMe.getName()+" is dead and its state ="+monitorMe.getState());
    }
    
    /**
    * 
    */
    private static String getAtrasoReal(String _hhmmAtraso, int _minsHolgura){
        StringTokenizer tokenAtraso = new StringTokenizer(_hhmmAtraso,":");
        tokenAtraso.nextToken();
        String minsAtraso = tokenAtraso.nextToken();
        int intMinsAtraso = Integer.parseInt(minsAtraso);
        if (intMinsAtraso > _minsHolgura){
            String atrasoReal = Utilidades.restarMinsHora(_hhmmAtraso, 
                _minsHolgura); 
            atrasoReal = atrasoReal.substring(0, atrasoReal.length()-3);
            System.out.println(WEB_NAME+"atrasoReal: "+atrasoReal);
            _hhmmAtraso = atrasoReal;
        }else _hhmmAtraso = "00:00";
        
        return _hhmmAtraso;
    }
}
