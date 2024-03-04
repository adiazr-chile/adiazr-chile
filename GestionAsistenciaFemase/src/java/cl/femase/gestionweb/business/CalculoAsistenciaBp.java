/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.dao.LogErrorDAO;
import cl.femase.gestionweb.vo.CalculoAsistenciaEstadoEjecucionVO;
import cl.femase.gestionweb.vo.DetalleAsistenciaToInsertVO;
import cl.femase.gestionweb.vo.DetalleAsistenciaVO;
import cl.femase.gestionweb.vo.DetalleAusenciaVO;
import cl.femase.gestionweb.vo.DetalleMarcaVO;
import cl.femase.gestionweb.vo.DetalleTurnoVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.MarcaVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.TurnoVO;
import cl.femase.gestionweb.vo.DiferenciaHorasVO;
import cl.femase.gestionweb.vo.EstadoVO;
import cl.femase.gestionweb.vo.InfoFeriadoVO;
import cl.femase.gestionweb.vo.LogErrorVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import org.json.JSONObject;

/**
 * Clase encargada de realizar el calculo de asistencia de los 
 * empleados-
 * 
 * Realiza el calculo de asistencia para los empleados pertenecientes
 * al centro de costo especificado. 
 * Se requiere: empresa, depto, cenco_id, startDate, endDate.
 * 
 * El objetivo es calcular la asistencia de la ultima semana.
 * Por ejemplo, si fecha_actual es 19-04-2018
 *   startDate = fecha_actual - 7 dias = 12-04-2018
 *   endDate = fecha_actual
 * 
 * 
 * @author Alexander
 */
public class CalculoAsistenciaBp  extends BaseBp{

    public PropertiesVO props;
    private String IT_FECHA = "";
    private final HashMap<String, MarcaVO> m_marcasProcesadas = new HashMap<>();
    
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.DetalleAsistenciaDAO calculoAsistenciaService;
    
    private final cl.femase.gestionweb.dao.EstadoEjecucionCalculoAsistenciaDAO estadoEjecucionDao;
    
    public List<EmpleadoVO> m_listaEmpleados=new ArrayList<>();
    
    /**
     * nuevos: 30-08-2018
     */
    private final Calendar m_mycal = Calendar.getInstance();
    private final TiempoExtraBp m_tiempoExtraBp = new TiempoExtraBp(new PropertiesVO());
    private final TurnosBp m_turnosBp = new TurnosBp(new PropertiesVO());
    private final DetalleTurnosBp m_detalleTurnoBp = new DetalleTurnosBp(new PropertiesVO());
    private final CalendarioFeriadoBp m_feriadosBp = new CalendarioFeriadoBp(new PropertiesVO());
    private final DetalleAusenciaBp m_detalleAusenciaBp = new DetalleAusenciaBp(new PropertiesVO());
    //private final TurnoRotativoDetalleBp m_detalleTurnoRotaBp = new TurnoRotativoDetalleBp(new PropertiesVO());
    private final TurnoRotativoBp m_turnoRotativoBp=new TurnoRotativoBp(new PropertiesVO());
    private List<TurnoVO> m_listaTurnos;
    private String[] m_fechasCalculo;
    
    private final LinkedHashMap<Integer, LinkedHashMap<Integer,DetalleTurnoVO>> m_listaDetalleTurnos = 
        new LinkedHashMap<>();
    private HashMap<String,Integer> m_hashTiposHE;
//    private HashMap<String,String> m_hashFeriadosNew;
    private final HashMap<String,Integer> m_hashFechas = new HashMap<>();
    
    /**
     * Lista de ausencias para cada rut
     * La key de cada item en la lista es: rut+"|"+fecha
     */
    private final LinkedHashMap<String, ArrayList<DetalleAusenciaVO>> m_listaAusencias = 
        new LinkedHashMap<>();
    
    /**
     * Lista de marcas para cada rut
     * La key de cada item en la lista es: empresaId + "|" + rut + "|" + fecha
     */
    private final LinkedHashMap<String, LinkedHashMap<String, MarcaVO>> m_listaMarcas = 
        new LinkedHashMap<>();
    
    /**
     * Lista con detalle de dias laborales para turnos rotativos para cada rut
     * La key de cada item en la lista es: empresaId + "|" + rut + "|" + anio + "|"+ mes
     */
    private final LinkedHashMap<String, DetalleTurnoVO> m_listaAsignacionTurnosRotativos = 
        new LinkedHashMap<>();
    
    /**
    * 20210725-001:
    * 
    *   Lista con las fechas y los datos existentes en calendario_feriados.
    *   Los datos son los resultantes de invocar a la funcion validaFechaFeriado por cada fecha del rango
    * 
    */
    LinkedHashMap<String, InfoFeriadoVO> m_fechasCalendarioFeriados = 
        new LinkedHashMap<>(); 
    
    /**
     * Lista con detalle de dias libres para turnos rotativos para cada rut
     * La key de cada item en la lista es: empresaId + "|" + rut + "|" + anio + "|"+ mes
     */
//    private final LinkedHashMap<String, DetalleTurnoVO> m_listaDetallesTurnosRotativosLibres = 
//        new LinkedHashMap<>();
    
    public CalculoAsistenciaBp(PropertiesVO props) {
        this.props = props;
        
        calculoAsistenciaService = new cl.femase.gestionweb.dao.DetalleAsistenciaDAO(this.props);
        estadoEjecucionDao = new cl.femase.gestionweb.dao.EstadoEjecucionCalculoAsistenciaDAO(this.props);
    }

    public void setAusencias(List<String> _rutsEmpleados){
        System.out.println(WEB_NAME+"[GestionFemase."
            + "CalculoAsistenciaBp.setAusencias()]INICIO "
            + "set lista de ausencias para todos los ruts "
            + "y fechas en el rango...");
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
                    Type listType = new TypeToken<ArrayList<DetalleAusenciaVO>>() {}.getType();
                    ArrayList<DetalleAusenciaVO> ausencias = new Gson().fromJson(jsonOutput, listType);                
                    m_listaAusencias.put(rutEmpleado+"|"+itFecha, ausencias);
                }
                
            }
        }
        System.out.println(WEB_NAME+"[GestionFemase."
            + "CalculoAsistenciaBp.setAusencias()]FIN "
            + "set lista de ausencias para todos los ruts "
            + "y fechas en el rango...");
    }
    
    /**
     * 
     * @param _empresaId
     * @param _rutsEmpleados
     */
    public void setMarcas(String _empresaId, List<String> _rutsEmpleados){
        System.out.println(WEB_NAME+"[GestionFemase."
            + "CalculoAsistenciaBp.setMarcas()]INICIO "
            + "set lista de marcas para todos los ruts "
            + "y fechas en el rango...");
        MarcasBp auxMarcasBp = new MarcasBp(new PropertiesVO());
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
                        + "CalculoAsistenciaBp.setMarcas]"
                        + "getMarcasJson jsonOutput: " + jsonOutput);
                    
//                    Type listType = new TypeToken<LinkedHashMap<String, MarcaVO>>() {}.getType();
//                    LinkedHashMap<String, MarcaVO> marcas = new Gson().fromJson(jsonOutput, listType);                
                    
                    LinkedHashMap<String, MarcaVO> marcas = 
                        auxMarcasBp.getMarcasEmpleado(_empresaId,rutEmpleado, 
                         m_fechasCalculo[i],m_fechasCalculo[i]);
                    m_listaMarcas.put(_empresaId 
                            + "|" + rutEmpleado 
                            + "|" + itFecha, marcas);
                }
            }
        }
        System.out.println(WEB_NAME+"[GestionFemase."
            + "CalculoAsistenciaBp.setMarcas()]FIN "
            + "set lista de marcas para todos los ruts "
            + "y fechas en el rango...");
    }
    
    public void setAsignacionTurnoRotativo(String _empresaId, List<String> _rutsEmpleados){
        System.out.println(WEB_NAME+"[GestionFemase."
            + "CalculoAsistenciaBp.setAsignacionTurnoRotativo()]INICIO "
            + "set lista de asignacion turnos rotativos para todos los ruts "
            + "y fechas en el rango (nuevo modelo)");
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
                    
//                    DetalleTurnoVO asignacionTurnoRotativo = 
//                        m_turnoRotativoBp.getAsignacionTurnoByFecha(_empresaId, rutEmpleado, 
//                        itFecha);
                    if (asignacionTurnoRotativo!=null){
                        System.out.println(WEB_NAME+"[GestionFemase."
                            + "CalculoAsistenciaBp.setAsignacionTurnoRotativo()]rut: " + rutEmpleado
                            + ", itemKey: " + itemKey
                            + ", itFecha: " + itFecha
                            + ", idTurnoRot: " + asignacionTurnoRotativo.getIdTurno()
                            + ", entrada: " + asignacionTurnoRotativo.getHoraEntrada()
                            + ", salida: " + asignacionTurnoRotativo.getHoraSalida()    
                        );
                    }
                    
                    m_listaAsignacionTurnosRotativos.put(itFecha, asignacionTurnoRotativo);
                }
            }
        }
        System.out.println(WEB_NAME+"[GestionFemase."
            + "CalculoAsistenciaBp.setAsignacionTurnoRotativo()]FIN "
            + "set lista de asignacion turnos rotativos para todos los ruts "
            + "y fechas en el rango...");
    }
    
//    /**
//     * 
//     * @param _empresaId
//     * @param _rutsEmpleados
//     * 
//     * @deprecated 
//     */
//    public void setDetallesTurnoRotativoLaboral(String _empresaId, List<String> _rutsEmpleados){
//        System.out.println(WEB_NAME+"[GestionFemase."
//            + "CalculoAsistenciaBp.setDetallesTurnoRotativoLaboral()]INICIO "
//            + "set lista de detalles turnos rotativos (dias laborales) para todos los ruts "
//            + "y fechas en el rango...");
//        if (_rutsEmpleados.size() > 0){
//            for (String rutEmpleado : _rutsEmpleados) {
//                for( int i = 0; i <= m_fechasCalculo.length - 1; i++)
//                {
//                    String itFecha = m_fechasCalculo[i];
//                    StringTokenizer tokenfecha1=new StringTokenizer(itFecha, "-");
//                    String strAnio = tokenfecha1.nextToken();
//                    String strMes = tokenfecha1.nextToken();
//                    
//                    String itemKey=_empresaId + "|" 
//                        + rutEmpleado+ "|" 
//                        + Integer.parseInt(strAnio)+ "|" 
//                        + Integer.parseInt(strMes);
//                    DetalleTurnoVO detalleTurnoRotLaboral = 
//                        m_detalleTurnoRotaBp.getDetalleTurnoLaboralByFecha(_empresaId, 
//                        rutEmpleado, 
//                        Integer.parseInt(strAnio), 
//                        Integer.parseInt(strMes), itFecha);
//                    
//                    m_listaDetallesTurnosRotativosLaborales.put(itemKey, detalleTurnoRotLaboral);
//                }
//            }
//        }
//        System.out.println(WEB_NAME+"[GestionFemase."
//            + "CalculoAsistenciaBp.setDetallesTurnoRotativoLaboral()]FIN "
//            + "set lista de detalles turnos rotativos (dias laborales) para todos los ruts "
//            + "y fechas en el rango...");
//    }
    
//    /**
//     * 
//     * @param _empresaId
//     * @param _rutsEmpleados
//     * @deprecated
//    */
//    public void setDetallesTurnoRotativoLibre(String _empresaId, List<String> _rutsEmpleados){
//        System.out.println(WEB_NAME+"[GestionFemase."
//            + "CalculoAsistenciaBp.setDetallesTurnoRotativoLibre()]INICIO "
//            + "set lista de detalles turnos rotativos (dias libres) para todos los ruts "
//            + "y fechas en el rango...");
//        if (_rutsEmpleados.size() > 0){
//            for (String rutEmpleado : _rutsEmpleados) {
//                for( int i = 0; i <= m_fechasCalculo.length - 1; i++)
//                {
//                    String itFecha = m_fechasCalculo[i];
//                    StringTokenizer tokenfecha1=new StringTokenizer(itFecha, "-");
//                    String strAnio = tokenfecha1.nextToken();
//                    String strMes = tokenfecha1.nextToken();
//                    
//                    String itemKey=_empresaId + "|" 
//                        + rutEmpleado+ "|" 
//                        + Integer.parseInt(strAnio)+ "|" 
//                        + Integer.parseInt(strMes);
//                    DetalleTurnoVO detalleTurnoRotLibre = 
//                        m_detalleTurnoRotaBp.getDetalleTurnoLibreByFecha(_empresaId, 
//                        rutEmpleado, 
//                        Integer.parseInt(strAnio), 
//                        Integer.parseInt(strMes), itFecha);
//                    
//                    m_listaDetallesTurnosRotativosLibres.put(itemKey, detalleTurnoRotLibre);
//                }
//            }
//        }
//        System.out.println(WEB_NAME+"[GestionFemase."
//            + "CalculoAsistenciaBp.setDetallesTurnoRotativoLibre()]FIN "
//            + "set lista de detalles turnos rotativos (dias libres) para todos los ruts "
//            + "y fechas en el rango...");
//    }
    
    public void setParameters(String _empresaId, String _startDate, String _endDate){
        System.out.println(WEB_NAME+"[GestionFemase."
            + "CalculoAsistenciaBp.setParameters()]INICIO "
            + ". StartDate: " + _startDate+", endDate: "+_endDate);
        
        m_fechasCalculo = Utilidades.getFechas(_startDate, _endDate);
        m_hashTiposHE = m_tiempoExtraBp.getTiposHrasExtras();
////        System.out.println(WEB_NAME+"[GestionFemase."
////            + "CalculoAsistenciaBp.setParameters()]INICIO "
////            + ". getFeriados para el rango de fechas seleccionado");
////        m_hashFeriadosNew = m_feriadosBp.getHashFeriadosNew(_startDate, _endDate);
                
        m_listaTurnos = m_turnosBp.getTurnos(_empresaId, null, -1, 0, 0, "id_turno");
        List<TurnoVO> listaTurnos = 
            m_turnosBp.getTurnos(_empresaId, null, -1, 0, 0, "id_turno");
        for (TurnoVO turno : listaTurnos) {
            LinkedHashMap<Integer,DetalleTurnoVO> detalle = 
                m_detalleTurnoBp.getHashDetalleTurno(turno.getId());
            m_listaDetalleTurnos.put(turno.getId(), detalle);
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
            + "CalculoAsistenciaBp.setParameters()]FIN "
            + ". StartDate: " + _startDate+", endDate: "+_endDate);
    }
    
    /**
     * Obtiene lista de empleados, con toda la info de cada empleado.
     * Recibe como parametro una lista de ruts
     * @param _empresaId
     * @param _deptoId
     * @param _cencoId
     * @param _listaRuts
     * @return
     * 
     * @deprecated
     */
    public List<EmpleadoVO> getListaEmpleadosComplete(String _empresaId,
            String _deptoId, 
            int _cencoId,
            List<String> _listaRuts){
    
        System.out.println(WEB_NAME+"[CalculoAsistenciaBp."
            + "getListaEmpleadosComplete]"
            + "empresa: "+_empresaId
            +", depto: "+_deptoId
            +", cenco: "+_cencoId);
        
        EmpleadosBp empleadosBp = new EmpleadosBp();
        List<EmpleadoVO> listaFinalEmpleados = new ArrayList<>();
        
        Iterator<String> it = _listaRuts.iterator();
        // mientras al iterador queda proximo juego
        while(it.hasNext()){
            String strRut = it.next();
            System.out.println(WEB_NAME+"[CalculoAsistenciaBp."
                + "getListaEmpleadosComplete]"
                + "get info completa de "
                + "empleado rut: "+ strRut);
            
            String jsonOutput = 
                empleadosBp.getEmpleadoJson(_empresaId,_deptoId, 
                _cencoId, strRut);
            //Type listType = new TypeToken<List<EmpleadoVO>>() {}.getType();
            //EmpleadoVO infoEmpleado = new Gson().fromJson(jsonOutput, listType);    
            //EmpleadoVO infoEmpleado = new Gson().fromJson(jsonOutput, 
             //       EmpleadoVO.class);
            
            System.err.println("[parseo empleadoVO]jsonOutput: "+jsonOutput); 
             
            EmpleadoVO infoEmpleado;
            try {
                infoEmpleado = (EmpleadoVO)new Gson().fromJson(jsonOutput, EmpleadoVO.class);
            }
            catch(Exception ex){
                System.err.println("[parseo empleadoVO]error: "+ex.toString());
                Type clazzListType = new TypeToken<EmpleadoVO>() {}.getType();
                infoEmpleado = new Gson().fromJson(jsonOutput, clazzListType);
                System.err.println("[parseo empleadoVO]1-rut: "+infoEmpleado.getRut());
            }
            
            System.out.println(WEB_NAME+"[parseo empleadoVO]2-rut: "+infoEmpleado.getRut());
//            List<EmpleadoVO> lstempleados = empleadosBp.getEmpleadosShort(_empresaId, 
//                _deptoId, 
//                _cencoId, 
//                -1,  
//                strRut, 
//                null, 
//                null, 
//                null, 
//                0, 
//                0, 
//                "empleado.empl_rut");
            
            listaFinalEmpleados.add(infoEmpleado);
        }
        
        m_listaEmpleados = listaFinalEmpleados;
                
        return listaFinalEmpleados;
    }
    
    
    /**
     * Obtiene lista de empleados para el centro de costo especificado
     * 
     * 
     * @param _empresaId
     * @param _deptoId
     * @param _cencoId
     * @return 
     */
    public List<EmpleadoVO> getListaEmpleados(String _empresaId,
            String _deptoId, 
            int _cencoId){
    
        System.out.println(WEB_NAME+"[CalculoAsistenciaBp."
            + "getListaEmpleados]empresa: "+_empresaId
            +", depto: "+_deptoId
            +", cenco: "+_cencoId);
        
        EmpleadosBp empleadosBp = new EmpleadosBp();
        List<EmpleadoVO> listaEmpleados = new ArrayList<>();
        if (_empresaId.compareTo("-1") != 0 
                && _deptoId.compareTo("-1") != 0
                && _cencoId != -1){
                    //todos los empleados del cenco
                    listaEmpleados = empleadosBp.getEmpleadosShort(_empresaId, 
                            _deptoId, 
                            _cencoId, 
                            -1,  
                            null, 
                            null, 
                            null, 
                            null, 
                            0, 
                            0, 
                            "empleado.empl_rut");
        }
        m_listaEmpleados = listaEmpleados;
        return listaEmpleados;
    }
    
    /**
     *  Calcula asistencia para la lista de empleados especificada.
     *  Si no entrega lista de empleados, se deben rescatar todos los empleados
     *  de la empresa-depto-cenco especificados.
     * 
     * @param _empresaId
     * @param _deptoId
     * @param _cencoId
     * @param _listaRutsEmpleados
     * @param _startDate
     * @param _endDate
     * @return 
     * 
     **/
    public ResultCRUDVO calculaAsistencia(String _empresaId,
            String _deptoId, 
            int _cencoId,
            List<String> _listaRutsEmpleados,
            String _startDate, 
            String _endDate){
        ResultCRUDVO resultado=new ResultCRUDVO();
        Gson gson = new Gson();     
        System.out.println(WEB_NAME+"[GestionFemase."
            + "CalculoAsistenciaBp]calculaAsistencia."
            + "Empresa=" + _empresaId    
            + ",depto=" + _deptoId
            + ",cenco=" + _cencoId
            + ",startDate=" + _startDate
            + ",endDate=" + _endDate);
        ArrayList<DetalleAsistenciaToInsertVO> listaAsistencia = 
            new ArrayList<>();
        
        DetalleAsistenciaBp calculoBp       = new DetalleAsistenciaBp(new PropertiesVO()); 
        CalendarioFeriadoBp bpFeriados    = new CalendarioFeriadoBp(new PropertiesVO());
        List<EmpleadoVO> listaEmpleadosConMarcas = new ArrayList<>();
        List<EmpleadoVO> listaEmpleados          = new ArrayList<>();
        
//        if (_listaRutsEmpleados.isEmpty()){
//            listaEmpleados = getListaEmpleados(_empresaId, _deptoId, _cencoId);
//            
//        }else{
            listaEmpleados = 
                getListaEmpleadosComplete(_empresaId, _deptoId, _cencoId, _listaRutsEmpleados);
//        }
        System.out.println(WEB_NAME+"[GestionFemase."
            + "CalculoAsistenciaBp]calculaAsistencia."
            + "listaEmpleados.size()=" +listaEmpleados.size());
        
        /*        
        List<EmpleadoVO> listaEmpleados = new ArrayList<>();
        if (_empresaId.compareTo("-1") != 0 
                && _deptoId.compareTo("-1") != 0
                && _cencoId != -1){
                    //todos los empleados del cenco
                    listaEmpleados = empleadosBp.getEmpleadosShort(_empresaId, 
                            _deptoId, 
                            _cencoId, 
                            -1,  
                            null, 
                            null, 
                            null, 
                            null, 
                            0, 
                            0, 
                            "empleado.empl_rut");
        }
        */
        
        //para controlar error al calcular asistencia
        //boolean isError = false;
        //String errorMessage = "";
        if (listaEmpleados.size() > 0){
            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]calculaAsistencia "
                + ". Anio actual = " + m_mycal.get(Calendar.YEAR)
                + ". StartDate = " + _startDate
                + ". EndDate = " + _endDate);

            //rescatar detalle de los turnos existentes, la llave de esta lista es el idTurno del empleado
            
            //Lista de fechas en formato yyyy-MM-dd
            //m_fechasCalculo = Utilidades.getFechas(_startDate, _endDate);

            /**
             * Se obtiene lista de turnos y su respectivo detalle
             */
////            List<TurnoVO> listaTurnos = 
////                turnosBp.getTurnos(null, 0, 0, "id_turno");
////            HashMap<String,Integer> hashTiposHE = tiempoExtraBp.getTiposHrasExtras();
////            for (TurnoVO turno : listaTurnos) {
////                LinkedHashMap<Integer,DetalleTurnoVO> detalle = 
////                    detalleTurnoBp.getHashDetalleTurno(turno.getId());
////                listaDetalleTurnos.put(turno.getId(), detalle);
////            }
            
            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]calculaAsistencia"
                + ". Invocando procesa Asistencia para "
                + "los empleados size="+listaEmpleados.size());
            LinkedHashMap<String, ArrayList<DetalleAsistenciaVO>> listaCalculosEmpleado = 
                new LinkedHashMap<>();
            int iteracion = 1;
            for (EmpleadoVO empleado : listaEmpleados) {
                ArrayList<DetalleAsistenciaVO> dataFechasRut = new ArrayList<>();
                listaEmpleadosConMarcas.add(empleado);
                
                /**
                * Inicio 20210725-001
                * Carga en memoria la info de la tabla calendario_feriado segun rango de fechas
                * seleccionado. En cada fecha se tiene la info de si es feriado o no.
                * De ser feriado, se indica que feriado es y su tipo.
                * 
                */
                m_fechasCalendarioFeriados = bpFeriados.getFechas(empleado.getEmpresaId(), 
                    empleado.getRut(), 
                    _startDate, 
                    _endDate);
                
                /**
                * Fin 20210725-001
                */
                
                /**
                 * Ejecuta el calculo de asistencia segun
                 * marcas para las fechas solicitadas
                 */
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]calculaAsistencia"
                    + ". Inicio procesa Asistencia para "
                    + "rut= " + empleado.getRut()
                    + ", nombre= "+empleado.getNombreCompleto());
                try {
                    dataFechasRut = procesaAsistencia(empleado,
                            m_fechasCalculo,
                            m_listaDetalleTurnos,
                            m_hashTiposHE);
                    System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]calculaAsistencia"
                        + ".dataFechasRut.size=" + dataFechasRut.size());
                } catch (Exception ex) {
                    resultado.setMsgError("Error al procesar asistencia para "
                        + "rut= " + empleado.getRut());
                    System.err.println("[GestionFemase.CalculoAsistenciaBp]calculaAsistencia."
                        + resultado.getMsgError());
                    System.err.println("[GestionFemase.CalculoAsistenciaBp]calculaAsistencia. "
                        + "Error is: " + ex.toString());
                    ex.printStackTrace();
                    resultado.setThereError(true);
                    
                    //*****
                    System.err.println("[CalculoAsistenciaBp]"
                        + "Error: " + ex.toString());
                    String clase = "CalculoAsistenciaBp";
                    String exclabel = ex.toString();
                    JSONObject jsonObj = Utilidades.generateErrorMessage(clase, ex);
                    LogErrorDAO logDao  = new LogErrorDAO();
                    LogErrorVO log      = new LogErrorVO();
                    log.setModulo(Constantes.LOG_MODULO_ASISTENCIA);
                    log.setEvento(Constantes.LOG_EVENTO_CALCULO_ASISTENCIA);
                    log.setLabel(exclabel);
                    log.setDetalle(jsonObj.toString());
                    logDao.insert(log);
                }
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]calculaAsistencia. "
                    + "listaCalculosEmpleado.put "
                    + "KEY= " + empleado.getRut()+"|"+iteracion);

                listaCalculosEmpleado.put(empleado.getRut()+"|"+iteracion, dataFechasRut);

                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]calculaAsistencia. "
                    + "Fin procesa Asistencia para "
                    + "rut= " + empleado.getRut());
                iteracion++;
            }//fin for lista empleados     

            if (!resultado.isThereError()){
                /**
                 * Guardar en BD el resultado de los calculos
                 * de asistencia 
                 */
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]calculaAsistencia. "
                    + "Inicio guardar calculos en BD");
////                MaintenanceEventVO resultado2=new MaintenanceEventVO();
////                resultado2.setUsername(userConnected.getUsername());
////                resultado2.setDatetime(new Date());
////                resultado2.setUserIP(request.getRemoteAddr());
////                resultado2.setType("CHR");

                //***inicio llenar datos para los insert
                //Guardar en Base de datos los calculos realizados...
                //Iterar listaFinalCalculos
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]calculaAsistencia."
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
                        System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]calculaAsistencia."
                            + "Agregar calculos para el insert de calculos asistencia. "
                            + "Rut= " + calculosFecha.getRutEmpleado()
                            + ", fechaEntrada= " + calculosFecha.getFechaEntradaMarca()
                            + ", hora entrada= " + calculosFecha.getHoraEntrada()
                            + ", fechaSalida= " + calculosFecha.getFechaSalidaMarca()    
                            + ", hora salida= " + calculosFecha.getHoraSalida()
                            + ", minutos reales= " + calculosFecha.getMinutosReales()    
                            );

                        if (calculosFecha.getFechaEntradaMarca()!=null){
                            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]calculaAsistencia."
                                + " add To Insert...");

                            DetalleAsistenciaToInsertVO toInsert 
                                = new DetalleAsistenciaToInsertVO(calculosFecha);
                            listaAsistencia.add(toInsert);
                        }
                    }
                }//fin while 9

                if (listaAsistencia.size() > 0){
                    System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]calculaAsistencia. "
                        + "Num Empleados "
                        + "a actualizar detalle asistencia= "+listaAsistencia.size());
                    //Abrir conexion a BD
                    calculoBp.openDbConnection();
                    calculoBp.deleteList(listaAsistencia);
                    calculoBp.saveList(listaAsistencia);
                    //calculoBp.setMarcasProcesadas(listaAsistencia);
                    //cerrar conexion a BD
                    calculoBp.closeDbConnection();
                }
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]calculaAsistencia."
                    + "Fin guardar calculos en BD");
            }
        }else{
            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]calculaAsistencia."
                + " No hay empleados...");
        }//fin lista empleados > 0

        if (!resultado.isThereError()){
            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]calculaAsistencia."
                + " Calculo de asistencia Finalizado...");
        }else{
            System.err.println("[GestionFemase.CalculoAsistenciaBp]"
                + "Error en calculaAsistencia: " + resultado.getMsgError());
        }
        
        return resultado;
    }
    
    
    /**
     * Realiza los calculos de horas asistencia.
     * Iterar fechas (segun rango)
     * Por cada fecha (){
     *    fecha_iteracion = fechas.next()
     *    -rescatar marcas para empleado + fecha_iteracion
     *
     *      si hay marcas {
     *           -proceder al calculo de horas trabajadas para ese dia
     *           -rescatar horas extras:
     *               .- Si el dia corresponde a su turno, las hras extras se consideran al 50%
     *               .- Si el dia NO corresponde a su turno, las hras extras se consideran al 100%
     * 
     *      } de lo contrario { //si no hay marcas para la fecha
     *            - rescatar ausencias para empleado + fecha_iteracion (permisos, licencias, vacaciones, permisos autorizados, etc)
     *            - NO APLICAN HORAS EXTRAS, SIN MARCAS NO HAY HRS EXTRAS
     *      }
     *   }
     * 
     */
    //metodo debe retornar lista con las fechas..en cada fecha los datos calculados,,,
    private ArrayList<DetalleAsistenciaVO> procesaAsistencia(EmpleadoVO _empleado, 
            String[] _fechasCalculo,
            LinkedHashMap<Integer, 
            LinkedHashMap<Integer,DetalleTurnoVO>> _listaDetalleTurnos, 
            HashMap<String,Integer> _hashTiposHE) throws Exception{
        System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]procesaAsistencia.");
        //LinkedHashMap<String, DetalleAsistenciaVO> listaDetalleCalculoReturn=new LinkedHashMap<>();
        ArrayList<DetalleAsistenciaVO> listaAux=new ArrayList<>();        
        //try{
            Calendar mycal = Calendar.getInstance();
            
            //TurnoRotativoBp turnoRotativobp=new TurnoRotativoBp(new PropertiesVO());
            MarcasBp marcasBp = new MarcasBp(new PropertiesVO());
            //DetalleAusenciaBp detalleAusenciaBp = new DetalleAusenciaBp(new PropertiesVO());
            SimpleDateFormat fullFechaFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            SimpleDateFormat soloFechaFmt = new SimpleDateFormat("yyyy-MM-dd");
            LinkedHashMap<String, MarcaVO> marcas = 
                new LinkedHashMap<>();
            
            for( int i = 0; i <= _fechasCalculo.length - 1; i++)
            {
                boolean tieneTurnoRotativo=false;
                boolean continuar=true;
                IT_FECHA = _fechasCalculo[i];
                StringTokenizer tokenfecha1=new StringTokenizer(IT_FECHA, "-");
                String strAnio = tokenfecha1.nextToken();
                String strMes = tokenfecha1.nextToken();
                String strDia = tokenfecha1.nextToken();
                int codDia = m_hashFechas.get(IT_FECHA);
//                int codDia = 
//                    Utilidades.getDiaSemana(Integer.parseInt(strAnio), 
//                        Integer.parseInt(strMes), 
//                        Integer.parseInt(strDia));
                marcas = new LinkedHashMap<>();
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]procesaAsistencia."
                    + "Itera fecha " + IT_FECHA);
                
                System.out.println(WEB_NAME+"[InfoEmpleado]"
                    + "Rut: "+_empleado.getRut()
                    + ", nombre: "+_empleado.getNombres()
                    + ", empresaId: "+_empleado.getEmpresaid()
                    + ", deptoId: "+_empleado.getDeptoid()
                    + ", cencoId: "+_empleado.getCencoid()   
                    + ", idTurno: "+_empleado.getIdturno()       
                );
                
                String itemKey= _empleado.getEmpresaid() + "|" 
                    + _empleado.getRut() + "|" 
                    + Integer.parseInt(strAnio)+ "|" 
                    + Integer.parseInt(strMes);
                //Rescata detalle turno rotativo (dias laborales)
                System.out.println(WEB_NAME+"[GestionFemase."
                    + "CalculoAsistenciaBp]procesaAsistencia."
                    + "get detalle turno rotativo laboral. "
                    + "itemKey:" + itemKey);
                
                DetalleTurnoVO detalleTurnoRotLaboral   = m_listaAsignacionTurnosRotativos.get(IT_FECHA);
                //Rescata detalle turno rotativo (dias libres)
                //DetalleTurnoVO detalleTurnoRotLibre     = m_listaDetallesTurnosRotativosLibres.get(itemKey);
                
//                DetalleTurnoVO detalleTurnoRotLaboral = 
//                    detalleTurnoRotaBp.getDetalleTurnoLaboralByFecha(_empleado.getEmpresa().getId(), 
//                    _empleado.getRut(), 
//                    Integer.parseInt(strAnio), 
//                    Integer.parseInt(strMes), IT_FECHA);
                
                
//                DetalleTurnoVO detalleTurnoRotLibre = 
//                    detalleTurnoRotaBp.getDetalleTurnoLibreByFecha(_empleado.getEmpresa().getId(), 
//                    _empleado.getRut(), 
//                    Integer.parseInt(strAnio), 
//                    Integer.parseInt(strMes), IT_FECHA);
                
                //rescatar detalle turno del empleado
                LinkedHashMap<Integer,DetalleTurnoVO> auxListDetalleTurno
                    = new LinkedHashMap<>();
                DetalleTurnoVO detalleturno = new DetalleTurnoVO();//
                
                if (detalleTurnoRotLaboral != null){
                    tieneTurnoRotativo = true;
                    //auxListDetalleTurno.put(detalleTurnoRot.getIdTurno(), detalleTurnoRot);
                    detalleturno = detalleTurnoRotLaboral;
                    System.out.println(WEB_NAME+"[GestionFemase."
                        + "CalculoAsistenciaBp]"
                        + "tiene turno rotativo, "
                        + "id_turno: " + detalleturno.getIdTurno()
                        + ", codDia: " + codDia    
                        + ", nombre_turno: " + detalleturno.getNombreTurno()    
                        + ", codDia: " + detalleturno.getCodDia()    
                        + ", hora entrada: " + detalleturno.getHoraEntrada()
                        + ", hora salida: " + detalleturno.getHoraSalida()
                        + ", holgura: " + detalleturno.getHolgura()
                        + ", minsColacion: " + detalleturno.getMinutosColacion());
                    
                    //_listaDetalleTurnos.put(detalleTurnoRot.getIdTurno(), hashDetalleTurnoRotativo);
                }else{
                    auxListDetalleTurno = _listaDetalleTurnos.get(_empleado.getIdturno());
                    detalleturno = auxListDetalleTurno.get(codDia);
                    if (detalleturno != null){
                        System.out.println(WEB_NAME+"[GestionFemase."
                            + "CalculoAsistenciaBp]codDia: " + codDia);
                        System.out.println(WEB_NAME+"[GestionFemase."
                            + "CalculoAsistenciaBp]"
                            + "tiene turno normal, "
                            + "id_turno: " + detalleturno.getIdTurno()
                            + ", codDia: " + codDia    
                            + ", nombre_turno: " + detalleturno.getNombreTurno()    
                            + ", codDia: " + detalleturno.getCodDia()    
                            + ", hora entrada: " + detalleturno.getHoraEntrada()
                            + ", hora salida: " + detalleturno.getHoraSalida()
                            + ", holgura: " + detalleturno.getHolgura()
                            + ", minsColacion: " + detalleturno.getMinutosColacion());
                    }
                }
                if (!tieneTurnoRotativo){
                    System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]procesaAsistencia."
                        + "Itera fecha " + IT_FECHA + ", tiene Turno Normal. set ambas marcas...");
                    
                    marcas = m_listaMarcas.get(_empleado.getEmpresaid()
                            + "|" + _empleado.getRut() + "|" + _fechasCalculo[i]);
                    if (marcas == null) marcas = new LinkedHashMap<>();
////                    marcas = marcasBp.getMarcasEmpleado(_empleado.getEmpresa().getId(), 
////                        _empleado.getRut(), 
////                        _fechasCalculo[i], _fechasCalculo[i]);
                }else{
                    System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]procesaAsistencia."
                        + "Itera fecha " + IT_FECHA 
                        + ", tiene Turno Rotativo, buscar marca de entrada, "
                        + "sino tiene, buscar salida dia anterior. Add Marca");
                    System.out.println(WEB_NAME+"[PASO 1]");
                    String jsonOutput = marcasBp.getMarcaByTipoJson(_empleado.getEmpresaid(), 
                        _empleado.getRut(), 
                        _fechasCalculo[i], _fechasCalculo[i],1,null,false);
                                       
                    MarcaVO auxMarca = new Gson().fromJson(jsonOutput, MarcaVO.class);
                    
                    if (auxMarca != null){
                        System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]"
                            + "procesaAsistencia.AddMarca entrada...");
                        marcas.put("1", auxMarca);
                    }else{
                        System.out.println(WEB_NAME+"[GestionFemase."
                            + "CalculoAsistenciaBp]procesaAsistencia."
                            + "No hay marca de entrada "
                            + "para fecha " + IT_FECHA+", no procesar...");
                        continuar=false;
                        
                    }
                }
                
//////          comentado      InfoFeriadoVO infoFeriado = m_feriadosBp.validaFeriado(_fechasCalculo[i], _empleado.getEmpresaid(), _empleado.getRut());
                String strKey = _empleado.getEmpresaid() + "|" + _empleado.getRut() + "|" + _fechasCalculo[i];
                InfoFeriadoVO infoFeriado = m_fechasCalendarioFeriados.get(strKey);
                boolean esFeriado = infoFeriado.isFeriado();
                if (esFeriado){
                    System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]"
                        + "Informacion del feriado."
                        + " Fecha: " + _fechasCalculo[i]
                        + ", es feriado? " + esFeriado
                        + ", tipoFeriado: " + infoFeriado.getTipoFeriado()
                        + ", descripcion: " + infoFeriado.getDescripcion());
                }
                //boolean esFeriado = _hashFeriados.containsKey(_fechasCalculo[i]);
                
                if (continuar){
                    
                    System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]"
                        + "procesaAsistencia."
                        + "Fecha: " + _fechasCalculo[i]
                        +" es feriado? " + esFeriado); 
                        //validar que para la fecha hayan dos marcas : entrada y salida, en caso contrario...alertar

                         /**
                            * si hay marcas {
                            *   -proceder al calculo de horas trabajadas para ese dia
                            *   -rescatar horas extras:
                            *       .- Si el dia corresponde a su turno, las hras extras se consideran al 50%
                            *       .- Si el dia NO corresponde a su turno, las hras extras se consideran al 100%
                            * 
                            * } de lo contrario { //si no hay marcas para la fecha
                            *      - rescatar ausencias para empleado + fecha_iteracion (permisos, licencias, vacaciones, permisos autorizados, etc)
                            *      - NO APLICAN HORAS EXTRAS, SIN MARCAS NO HAY HRS EXTRAS
                            * }
                         */

                         //rescata detalle turno
                        //LinkedHashMap<Integer,DetalleTurnoVO> auxListDetalleTurno = _listaDetalleTurnos.get(_empleado.getIdTurno());
        //                DetalleTurnoVO detalleturno = auxListDetalleTurno.get(codDia);
                        //hrs contrato segun turno
                        int horasTeoricas   = 0;
                        int minutosTeoricos = 0;
                        String hhmmTurno    = "";
                        if (detalleturno != null){
                            horasTeoricas = detalleturno.getTotalHoras();
                            minutosTeoricos = horasTeoricas * 60;
                            DiferenciaHorasVO duracionTurno = 
                            Utilidades.getTimeDifference(_fechasCalculo[i]+" " +detalleturno.getHoraEntrada(), _fechasCalculo[i]+" " +detalleturno.getHoraSalida());
                                hhmmTurno = duracionTurno.getStrDiferenciaHorasMinutos();
                        }

                        System.out.println(WEB_NAME+"[GestionFemase."
                            + "CalculoAsistenciaBp]procesaAsistencia."
                            + "Fecha calculo: "+_fechasCalculo[i]
                            + ", codDia: "+codDia
                            + ", rut: " + _empleado.getRut()
                            + ", idTurno: " + _empleado.getIdturno()
                            + ", horasTeoricas: " + horasTeoricas
                            + ", tieneTurnoRotativo: " + tieneTurnoRotativo    
                        );

                        //rescata horas extras ingresadas y autorizadas previamente
            //            TiempoExtraVO hrsExtras = 
            //                tiempoExtraBp.getTiempoExtra(_empleado.getRut(), _fechasCalculo[i]);
                        //rescata tipos de horas extras ingresadas y autorizadas previamente
            //            HashMap<String,Integer> hashTiposHE = tiempoExtraBp.getTiposHrasExtras();
//                        System.out.println(WEB_NAME+"[GestionFemase."
//                            + "CalculoAsistenciaBp]procesaAsistencia. "
//                            + "Fecha calculo: "+_fechasCalculo[i]
//                            + ", codDia: "+codDia
//                            + ", rut: " + _empleado.getRut()
//                            + ", size marcas: " +marcas.size());
                        if (!marcas.isEmpty()){
                            if (marcas.size() == 1){
                                System.out.println(WEB_NAME+"[GestionFemase."
                                    + "CalculoAsistenciaBp]"
                                    + "procesaAsistencia.Tiene una sola marca...");
                                DetalleAsistenciaVO detalleCalculo = new DetalleAsistenciaVO();
                                detalleCalculo.setFechaHoraCalculo(fullFechaFmt.format(mycal.getTime()));
                                detalleCalculo.setEmpresaId(_empleado.getEmpresaid());
                                detalleCalculo.setDeptoId(_empleado.getDeptoid());
                                detalleCalculo.setCencoId(_empleado.getCencoid());
                                detalleCalculo.setRutEmpleado(_empleado.getRut());
                                String labelAlert="";
                                String auxAlert="";
                                Iterator<MarcaVO> collMarcas1 = marcas.values().iterator();
                                while(collMarcas1.hasNext()){//while 7
                                    MarcaVO currentMarca = collMarcas1.next();
                                    int tipoMarca1 = currentMarca.getTipoMarca();
                                    auxAlert = "Solo tiene marca ";
                                    labelAlert = currentMarca.getFechaHora()+", solo marca ";
                                    String fechaHoraMarcaFull = currentMarca.getFechaHora();//full =  incluye los segundos
                                    //2017-06-01 09:00:01
                                    StringTokenizer tokenFechaHora = new StringTokenizer(fechaHoraMarcaFull, " ");
                                    StringTokenizer tokenFecha = new StringTokenizer(tokenFechaHora.nextToken(), "-");
                                    StringTokenizer tokenHora = new StringTokenizer(tokenFechaHora.nextToken(), ":");
                                    String fechaMarca1 = tokenFecha.nextToken() + "-" + tokenFecha.nextToken() + "-" + tokenFecha.nextToken();
                                    String horaMarcaFull = tokenHora.nextToken() + ":" + tokenHora.nextToken() + ":" + tokenHora.nextToken();//considera los segundos de la hora
                                    //String horaMarcaShort = tokenHora.nextToken() + ":" + tokenHora.nextToken() + ":00";//seteo en cero mlos segundos
                                    String horaMarcaShort = horaMarcaFull.substring(0, horaMarcaFull.length()-3) + ":00";//no considerar los segundos
                                    if (tipoMarca1 == 1) {
                                        labelAlert += "Entrada";
                                        auxAlert += "Entrada";
                                        System.out.println(WEB_NAME+"[GestionFemase."
                                            + "CalculoAsistenciaBp]"
                                            + "procesaAsistencia."
                                            + "Solo hay marca de ENTRADA. auxAlert= "+auxAlert);
                                        System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]"
                                            + "procesaAsistencia. setFechaEntradaMarca: "+fechaMarca1);
                                        detalleCalculo.setFechaEntradaMarca(fechaMarca1);
                                        detalleCalculo.setHoraEntrada(horaMarcaFull);//fecha hora entrada full
                                        //comentado 30-09-2018. if (tieneTurnoRotativo){
                                            System.out.println(WEB_NAME+"[GestionFemase."
                                                + "CalculoAsistenciaBp]procesaAsistencia."
                                                + "Buscar marca de salida(turno normal o rotativo)");
                                            MarcaVO auxMarca = getMarcaTurnoRotativo(marcasBp, _empleado, _fechasCalculo[i], horaMarcaFull, tipoMarca1);
                                            if (auxMarca != null){
                                                marcas.put("2", auxMarca);
                                            }
                                        //}
                                    }else {
                                        labelAlert += "Salida";
                                        auxAlert += "Salida";
                                        System.out.println(WEB_NAME+"[GestionFemase."
                                            + "CalculoAsistenciaBp]procesaAsistencia."
                                            + "Solo hay marca de SALIDA. auxAlert= "+auxAlert);
                                        detalleCalculo.setFechaEntradaMarca(_fechasCalculo[i]);
                                        detalleCalculo.setFechaSalidaMarca(fechaMarca1);
                                        detalleCalculo.setHoraSalida(horaMarcaFull);//fecha hora salida full
                                        if (tieneTurnoRotativo){
                                            System.out.println(WEB_NAME+"[GestionFemase."
                                                + "CalculoAsistenciaBp]"
                                                + "procesaAsistencia."
                                                + "Buscar marca de entrada");
                                            MarcaVO auxMarca = getMarcaTurnoRotativo(marcasBp, _empleado, _fechasCalculo[i], horaMarcaFull, tipoMarca1);
                                            if (auxMarca != null){
                                                marcas.put("1", auxMarca);
                                            }
                                        }
                                    }

                                }//

                                if (marcas.size()==1){
                                    //auxAlert = labelAlert;
                                    labelAlert = "rut:" + _empleado.getRut()
                                        +", fecha: "+labelAlert;

                                    System.out.println(WEB_NAME+"[GestionFemase."
                                        + "CalculoAsistenciaBp]"
                                        + "procesaAsistencia.- "
                                       + "empresa:" + _empleado.getEmpresaid()
                                       + ", rut:" + _empleado.getRut()
                                       + ", fecha: " + _fechasCalculo[i] 
                                       + ". Alerta: " + labelAlert
                                       + ". observacion: " + auxAlert);
                                    
                                    ArrayList<DetalleAusenciaVO> detalleausencia = m_listaAusencias.get(_empleado.getRut()+"|"+_fechasCalculo[i]);
//                                    ArrayList<DetalleAusenciaVO> detalleausencia = 
//                                       detalleAusenciaBp.getAusencias(_empleado.getRut(), 
//                                           _fechasCalculo[i]);
                                    
                                    System.out.println(WEB_NAME+"[GestionFemase."
                                        + "CalculoAsistenciaBp]"
                                        + "procesaAsistencia.NO TIENE MARCAS, "
                                        + "Set observacion[4]: " + auxAlert);
                                    detalleCalculo.setObservacion(auxAlert, "CalculoAsistenciaBp_1");
                                    detalleCalculo.setFechaCalculo(_fechasCalculo[i]);
                                    detalleCalculo.setHorasTeoricas(horasTeoricas);
                                    detalleCalculo.setArt22(_empleado.isArticulo22());
                                    if (detalleturno != null){
                                        System.out.println(WEB_NAME+"[GestionFemase."
                                            + "CalculoAsistenciaBp]"
                                            + "procesaAsistencia."
                                            + "detalleturno.getHoraEntrada(): "+detalleturno.getHoraEntrada()
                                            + "detalleturno.getHoraSalida(): "+detalleturno.getHoraSalida());
                                        detalleCalculo.setHoraEntradaTeorica(detalleturno.getHoraEntrada());
                                        detalleCalculo.setHoraSalidaTeorica(detalleturno.getHoraSalida());
                                        detalleCalculo.setHolguraMinutos(detalleturno.getHolgura());
                                        detalleCalculo.setMinutosColacion(detalleturno.getMinutosColacion());
                                    }else{
                                        String aux11 = detalleCalculo.getObservacion();
                                        System.out.println(WEB_NAME+"[GestionFemase."
                                            + "CalculoAsistenciaBp]"
                                            + "procesaAsistencia.NO TIENE MARCAS, "
                                            + "Set observacion[3]: " + auxAlert);
                                        System.out.println(WEB_NAME+"[GestionFemase."
                                            + "CalculoAsistenciaBp]"
                                            + "procesaAsistencia] -1- Set Sin turno (Libre)");
                                        detalleCalculo.setObservacion("Libre","CalculoAsistenciaBp_2");
                                        detalleCalculo.setHoraSalida("00:00:00");
                                    }
                                    
                                    //detalleCalculo.setEsFeriado(_hashFeriados.containsKey(_fechasCalculo[i]));
                                    detalleCalculo.setEsFeriado(esFeriado);
                                    //iterando ausencias
                                    if (detalleausencia != null && detalleausencia.size() > 0){
                                        System.out.println(WEB_NAME+"[GestionFemase."
                                            + "CalculoAsistenciaBp]"
                                            + "procesaAsistencia."
                                            + "[A]Iterando ausencias...");
                                        String auxMsgAusencias = "";
                                        ArrayList<String> listaHrsAusencias = new ArrayList<>();
                                        Iterator<DetalleAusenciaVO> ausenciasIterator = detalleausencia.iterator();
                                        while (ausenciasIterator.hasNext()) {
                                            DetalleAusenciaVO ausencia = ausenciasIterator.next();
                                            auxMsgAusencias += ausencia.getNombreAusencia();
                                            if (ausencia.getPermiteHora().compareTo("S") == 0){
                                                auxMsgAusencias += ausencia.getNombreAusencia()
                                                    +":" + ausencia.getHoraInicioFullAsStr().substring(0, ausencia.getHoraInicioFullAsStr().length()-3) 
                                                    +"-" + ausencia.getHoraFinFullAsStr().substring(0, ausencia.getHoraFinFullAsStr().length()-3)+" hrs, ";
                                                //restar hrs con fecha
                                                DiferenciaHorasVO difAusencia = Utilidades.getTimeDifference(_fechasCalculo[i] + " " + ausencia.getHoraInicioFullAsStr() +":00", 
                                                        _fechasCalculo[i] + " " + ausencia.getHoraFinFullAsStr()+":00");
                                                listaHrsAusencias.add(difAusencia.getStrDiferenciaHorasMinutos());
                                            }else{
                                                auxMsgAusencias += ausencia.getNombreAusencia();
                                                if (hhmmTurno != null && hhmmTurno.compareTo("") != 0){
                                                    listaHrsAusencias.add(hhmmTurno);
                                                }
                                                //hrs justificadas = suma de hrs de ausencia...
                                            }
                                        }
                                        
                                        System.out.println(WEB_NAME+"[GestionFemase."
                                            + "CalculoAsistenciaBp]"
                                            + "procesaAsistencia."
                                            + "NO TIENE MARCAS, "
                                            + "Set observacion[2]: " + auxMsgAusencias);
                                        detalleCalculo.setObservacion(auxMsgAusencias,"CalculoAsistenciaBp_3");
                                        String sumaJustificadas = Utilidades.sumTimesList(listaHrsAusencias);
                                        detalleCalculo.setHhmmJustificadas(sumaJustificadas);
                                        //detalleCalculo.setHoraInicioAusencia(detalleausencia.getHoraInicioFullAsStr());
                                        //detalleCalculo.setHoraFinAusencia(detalleausencia.getHoraFinFullAsStr());
                                   }
                                   
                                    System.out.println(WEB_NAME+"[GestionFemase."
                                        + "CalculoAsistenciaBp]"
                                        + "procesaAsistencia."
                                        + "**1** Put detalleCalculo."
                                         + " Rut="+_empleado.getRut()
                                         + ",fecha entrada="+detalleCalculo.getFechaEntradaMarca()
                                         +", hora Entrada="+detalleCalculo.getHoraEntrada());
                                   listaAux.add(detalleCalculo);
//                                   listaDetalleCalculoReturn.put(detalleCalculo.getFechaEntradaMarca()
//                                        +"|"+detalleCalculo.getHoraEntrada()
//                                        +"|"+_empleado.getRut(), detalleCalculo);
                ////                   objCorreo.setSmtpHost(appProperties.getMailHost());
                ////                   objCorreo.setSmtpPort(appProperties.getMailPort());
                ////                   objCorreo.setFrom(appProperties.getMailFrom());
                ////                   objCorreo.setDestinatarios(appProperties.getMailTo());
                ////                   objCorreo.setSubject("Femase-Sistema Gestion");
                ////                   objCorreo.setBody(labelAlert);
                ////                    try {
                ////                        objCorreo.setEnviarMensaje();
                ////                    } catch (Exception ex) {
                ////                        System.err.println("Error al enviar "
                ////                            + "correo: "+ex.toString());
                ////                    }
                                }
                            }
                            if (marcas.size() > 1){
                                //ordenar par de marcas...
                                MarcaVO marcaEntrada = marcas.get("1");
                                MarcaVO marcaSalida = marcas.get("2");
                                System.out.println(WEB_NAME+"[GestionFemase."
                                    + "CalculoAsistenciaBp]"
                                    + "procesaAsistencia."
                                    + "FechaHora entrada: "+marcaEntrada.getFechaHora()
                                    + ", fechaHora salida: "+marcaSalida.getFechaHora()    
                                );
                                LinkedHashMap<String, MarcaVO> marcasOrdenadas = 
                                    new LinkedHashMap<>();
                                marcasOrdenadas.put("1", marcaEntrada);
                                marcasOrdenadas.put("2", marcaSalida);
                                m_marcasProcesadas.put(marcaEntrada.getFechaHora(), marcaEntrada);
                                m_marcasProcesadas.put(marcaSalida.getFechaHora(), marcaSalida);
                                //listaDetalleCalculoReturn = getInfoAsistencia(_empleado, marcas);
                                DetalleAsistenciaVO auxDetalleCalculo = 
                                    getInfoAsistencia(_empleado, 
                                        marcasOrdenadas, 
                                        detalleturno, 
                                        horasTeoricas, 
                                        minutosTeoricos, 
                                        detalleTurnoRotLaboral, 
                                        _hashTiposHE);
                                
                                listaAux.add(auxDetalleCalculo);
                                
                                System.out.println(WEB_NAME+"[GestionFemase."
                                    + "CalculoAsistenciaBp]"
                                    + "procesaAsistencia.**ZZ** "
                                    + "put Key= " + _empleado.getRut() 
                                    + "|" + auxDetalleCalculo.getFechaEntradaMarca()+ 
                                    "|"+ auxDetalleCalculo.getHoraEntrada()
                                    +", observacion: "+auxDetalleCalculo.getObservacion());
//                                listaDetalleCalculoReturn.put(_empleado.getRut()+"|"+marcaEntrada.getHora(),
//                                    auxDetalleCalculo);
                            }
                            Iterator<DetalleAsistenciaVO> itDetails11 = listaAux.iterator();
                            while (itDetails11.hasNext())//while 9
                            {   
                                DetalleAsistenciaVO calculosFecha22 = itDetails11.next();
                                System.out.println(WEB_NAME+"[GestionFemase."
                                    + "CalculoAsistenciaBp]"
                                    + "procesaAsistencia.**INTERMEDIO** detalleCalculo."
                                    + " Rut= " + _empleado.getRut()
                                    + ",fecha entrada= " + calculosFecha22.getFechaEntradaMarca()
                                    +", hora Entrada= " + calculosFecha22.getHoraEntrada());
                            }
                        }else{ // NO HAY MARCAS
                                /**
                                 * - rescatar ausencias para empleado + fecha_iteracion (permisos, licencias, vacaciones, permisos autorizados, etc)
                                 * - NO APLICAN HORAS EXTRAS, SIN MARCAS NO HAY HRS EXTRAS
                                 */

                                System.out.println(WEB_NAME+"[GestionFemase."
                                    + "CalculoAsistenciaBp]"
                                    + "procesaAsistencia. "
                                    + "empresa:"+ _empleado.getEmpresaid()
                                    + ", rut:"+ _empleado.getRut()
                                    + ", fecha: " + _fechasCalculo[i] 
                                    + "- NO TIENE MARCAS, rescatar ausencias...");
                                ArrayList<DetalleAusenciaVO> detalleausencia = m_listaAusencias.get(_empleado.getRut()+"|"+_fechasCalculo[i]);
                                
//                                ArrayList<DetalleAusenciaVO> detalleausencia = 
//                                   detalleAusenciaBp.getAusencias(_empleado.getRut(), 
//                                       _fechasCalculo[i]);
                                DetalleAsistenciaVO detalleCalculo = new DetalleAsistenciaVO();
                                detalleCalculo.setFechaHoraCalculo(fullFechaFmt.format(mycal.getTime()));
                                detalleCalculo.setEmpresaId(_empleado.getEmpresaid());
                                detalleCalculo.setDeptoId(_empleado.getDeptoid());
                                detalleCalculo.setCencoId(_empleado.getCencoid());
                                detalleCalculo.setRutEmpleado(_empleado.getRut());
                                detalleCalculo.setFechaCalculo(_fechasCalculo[i]);
                                detalleCalculo.setHorasTeoricas(horasTeoricas);
                                detalleCalculo.setArt22(_empleado.isArticulo22());
                                //String hhmmTurno = "";

                                if (detalleturno != null){
                                   System.out.println(WEB_NAME+"[GestionFemase."
                                        + "CalculoAsistenciaBp]"
                                        + "procesaAsistencia.-0- fechaHra1: "+_fechasCalculo[i]+" " +detalleturno.getHoraEntrada()+
                                        "fechaHra2: "+_fechasCalculo[i]+" " +detalleturno.getHoraSalida());
                                   DiferenciaHorasVO duracionTurno = 
                                        Utilidades.getTimeDifference(_fechasCalculo[i]+" " +detalleturno.getHoraEntrada(), _fechasCalculo[i]+" " +detalleturno.getHoraSalida());
                                   hhmmTurno = duracionTurno.getStrDiferenciaHorasMinutos();
                                   System.out.println(WEB_NAME+"[GestionFemase."
                                        + "CalculoAsistenciaBp]"
                                        + "procesaAsistencia.-2-"
                                        + "detalleturno.getHoraEntrada(): "+detalleturno.getHoraEntrada()
                                        + ", detalleturno.getHoraSalida(): "+detalleturno.getHoraSalida());
                                   detalleCalculo.setHoraEntradaTeorica(detalleturno.getHoraEntrada());
                                   detalleCalculo.setHoraSalidaTeorica(detalleturno.getHoraSalida());
                                   detalleCalculo.setHolguraMinutos(detalleturno.getHolgura());
                                   detalleCalculo.setMinutosColacion(detalleturno.getMinutosColacion());
                                   System.out.println(WEB_NAME+"[GestionFemase."
                                        + "CalculoAsistenciaBp]"
                                        + "procesaAsistencia.NO TIENE MARCAS "
                                        + "hhmmTurno:"+ hhmmTurno);
                                }
                                detalleCalculo.setEsFeriado(esFeriado);
                                //detalleCalculo.setEsFeriado(_hashFeriados.containsKey(_fechasCalculo[i]));
                                if (detalleausencia != null && detalleausencia.size() > 0){
                                    System.out.println(WEB_NAME+"[GestionFemase."
                                        + "CalculoAsistenciaBp]"
                                        + "procesaAsistencia.[B]Iterando ausencias...");
                                    String auxMsgAusencias = "";
                                    ArrayList<String> listaHrsAusencias     = new ArrayList<String>();
                                    ArrayList<String> listaHrsNoRemuneradas = new ArrayList<String>();
                                    //iterando ausencias
                                    Iterator<DetalleAusenciaVO> ausenciasIterator = detalleausencia.iterator();
                                    while (ausenciasIterator.hasNext()) {
                                        DetalleAusenciaVO ausencia = ausenciasIterator.next();
                                        //auxMsgAusencias += ausencia.getNombreAusencia();
                                        if (ausencia.getPermiteHora().compareTo("S")==0){
                                            auxMsgAusencias += ausencia.getNombreAusencia()
                                                +":" + ausencia.getHoraInicioFullAsStr().substring(0, ausencia.getHoraInicioFullAsStr().length()-3) 
                                                +"-" + ausencia.getHoraFinFullAsStr().substring(0, ausencia.getHoraFinFullAsStr().length()-3)+" hrs, ";
                                            System.out.println(WEB_NAME+"[GestionFemase."
                                                + "CalculoAsistenciaBp]"
                                                + "procesaAsistencia. "
                                                + "Ausencia x horas, msgAusencias: " + auxMsgAusencias);
                                            //restar hrs con fecha
                                            DiferenciaHorasVO difAusencia = Utilidades.getTimeDifference(_fechasCalculo[i] + " " + ausencia.getHoraInicioFullAsStr() +":00", 
                                                    _fechasCalculo[i] + " " + ausencia.getHoraFinFullAsStr()+":00");
                                            if (ausencia.getTipoAusencia()==1){
                                                listaHrsAusencias.add(difAusencia.getStrDiferenciaHorasMinutos());
                                            }else{
                                                listaHrsNoRemuneradas.add(difAusencia.getStrDiferenciaHorasMinutos());
                                            }
                                        }else{
                                            auxMsgAusencias += ausencia.getNombreAusencia();
                                            System.out.println(WEB_NAME+"[GestionFemase."
                                                + "CalculoAsistenciaBp]"
                                                + "procesaAsistencia. "
                                                + "Ausencia x dias, msgAusencias: " + auxMsgAusencias);
                                            if (ausencia.getTipoAusencia() == 1){ 
                                                if (hhmmTurno != null && hhmmTurno.compareTo("") != 0){
                                                    listaHrsAusencias.add(hhmmTurno);
                                                }
                                            }else{
                                                listaHrsNoRemuneradas.add(hhmmTurno);
                                            }
                                                
                                            //hrs justificadas = suma de hrs de ausencia...
                                        }
                                    }
                                    
                                    System.out.println(WEB_NAME+"[GestionFemase."
                                        + "CalculoAsistenciaBp]"
                                        + "procesaAsistencia.NO TIENE MARCAS, "
                                        + "Set observacion[12]: " + auxMsgAusencias
                                        + ", hrs justificadas: " + Utilidades.sumTimesList(listaHrsAusencias)
                                        + ", hrs no remuneradas: " + Utilidades.sumTimesList(listaHrsNoRemuneradas));
                                    
                                    detalleCalculo.setObservacion(auxMsgAusencias, "CalculoAsistenciaBp_4");
                                    detalleCalculo.setHhmmJustificadas(Utilidades.sumTimesList(listaHrsAusencias));
                                    detalleCalculo.setHrsAusencia(Utilidades.sumTimesList(listaHrsNoRemuneradas));                                    
//                                    if (hhmmTurno != null && hhmmTurno.compareTo("") != 0){
//                                        System.out.println(WEB_NAME+"[DetalleAsistenciaController.procesaAsistencia]NO TIENE MARCAS "
//                                            + "Set hrs ausencia con hrsTurno: "+ hhmmTurno);
//                                        detalleCalculo.setHhmmJustificadas(hhmmTurno);
//                                    }
                                    
                                }else{
                                    System.out.println(WEB_NAME+"[GestionFemase."
                                        + "CalculoAsistenciaBp]"
                                        + "procesaAsistencia."
                                        + "empresa:"+ _empleado.getEmpresaid()
                                        + ", rut:"+ _empleado.getRut()
                                        + ", fecha: " + _fechasCalculo[i] 
                                        + "- No tiene ausencias. "
                                        + "HH:MM turno: " + hhmmTurno);
                                    //No tiene marcas ni ausencia...
                                    if (hhmmTurno != null && hhmmTurno.compareTo("") != 0){
                                        System.out.println(WEB_NAME+"[GestionFemase."
                                            + "CalculoAsistenciaBp]"
                                            + "procesaAsistencia."
                                            + ", rut:"+ _empleado.getRut()
                                            + ", fecha: " + _fechasCalculo[i] 
                                            + "- Set hrs ausencia= " + hhmmTurno 
                                            + " y observacion = Sin marcas" );
                                        if (detalleturno != null){
                                            //detalleCalculo.setHoraEntrada(detalleturno.getHoraEntrada());
                                            //detalleCalculo.setHoraSalida(detalleturno.getHoraSalida());
                                            detalleCalculo.setFechaSalidaMarca(_fechasCalculo[i]);
                                        }
                                        if (!esFeriado){
                                            System.out.println(WEB_NAME+"[GestionFemase."
                                                + "CalculoAsistenciaBp]"
                                                + "procesaAsistencia.NO TIENE MARCAS "
                                                + "Set hrs ausencia con hrsTurno: "+ hhmmTurno);
                                            detalleCalculo.setHrsAusencia(hhmmTurno);
                                        }
                                        System.out.println(WEB_NAME+"[GestionFemase."
                                            + "CalculoAsistenciaBp]"
                                            + "procesaAsistencia.NO TIENE MARCAS, "
                                            + "Set observacion[11]. hh:mm turno: "+hhmmTurno);
//                                        if (esFeriado){
//                                            detalleCalculo.setObservacion("Feriado");
//                                        }else{
//                                            detalleCalculo.setObservacion("Sin marcas");
//                                        }

                                        if (esFeriado && (hhmmTurno.compareTo("") != 0)){
                                            detalleCalculo.setObservacion("Feriado", "CalculoAsistenciaBp_5");
                                        }else if (esFeriado && (hhmmTurno.compareTo("") == 0)){
                                            detalleCalculo.setObservacion("Libre", "CalculoAsistenciaBp_6");
                                        }else if (!esFeriado && hhmmTurno.compareTo("") != 0){
                                            detalleCalculo.setObservacion("Sin marcas", "CalculoAsistenciaBp_7");
                                        }
                                    
                                    }
                                }

                                if (detalleCalculo.getFechaEntradaMarca() != null){
                                    System.out.println(WEB_NAME+"[GestionFemase."
                                        + "CalculoAsistenciaBp]"
                                        + "procesaAsistencia.**2** put detalleCalculo."
                                        + " Rut="+_empleado.getRut()
                                        + ",fecha entrada="+detalleCalculo.getFechaEntradaMarca()
                                        +", hora Entrada="+detalleCalculo.getHoraEntrada());
                                }else{
                                    detalleCalculo.setFechaCalculo(_fechasCalculo[i]);
                                    detalleCalculo.setFechaEntradaMarca(_fechasCalculo[i]);
                                    System.out.println(WEB_NAME+"[GestionFemase."
                                        + "CalculoAsistenciaBp]"
                                        + "procesaAsistencia.NO TIENE MARCAS, "
                                        + "Set observacion[9], "
                                        + "hh:mm turno: "+hhmmTurno
                                        +", detalleausencia: "+detalleausencia);
                                    if (detalleausencia == null || detalleausencia.isEmpty()){
                                        System.err.println("Aqui 1");
                                        if (esFeriado && (hhmmTurno != null && hhmmTurno.compareTo("")!=0)){
                                            System.err.println("[GestionFemase."
                                                + "CalculoAsistenciaBp]"
                                                + "procesaAsistencia."
                                                + "Set Obs = feriado");
                                            detalleCalculo.setObservacion("Feriado", "CalculoAsistenciaBp_8");
                                        }else if (esFeriado && (hhmmTurno != null && hhmmTurno.compareTo("") == 0)){
                                            System.err.println("[GestionFemase."
                                                + "CalculoAsistenciaBp]"
                                                + "procesaAsistencia."
                                                + "Set Obs = Libre");
                                            detalleCalculo.setObservacion("Libre", "CalculoAsistenciaBp_9");
                                        }else if (!esFeriado && hhmmTurno.compareTo("") != 0){
                                            detalleCalculo.setObservacion("Sin marcas", "CalculoAsistenciaBp_10");
                                        }
                                    }
                                }
                                listaAux.add(detalleCalculo);
                        }
                }//fin if continuar

            }//fin iteracion de fechas
        
        Iterator<DetalleAsistenciaVO> itDetails11 = listaAux.iterator();
        while (itDetails11.hasNext())//while 9
        {   
            DetalleAsistenciaVO calculosFecha22 = itDetails11.next();
            System.out.println(WEB_NAME+"[GestionFemase."
                + "CalculoAsistenciaBp]"
                + "procesaAsistencia."
                + "**ANTES DE SALIR** "
                + "detalleCalculo."
                + " Rut= " + _empleado.getRut()
                + ",fecha entrada= " + calculosFecha22.getFechaEntradaMarca()
                +", hora Entrada= " + calculosFecha22.getHoraEntrada());
        }
           
        return listaAux;
    }
    
    /**
    * 
    */
    private DetalleAsistenciaVO getInfoAsistencia(EmpleadoVO _empleado, 
            LinkedHashMap<String, MarcaVO> _listaMarcas, 
            DetalleTurnoVO _detalleturno,
            int _horasTeoricas,
            int _minutosTeoricos,
            DetalleTurnoVO _detalleTurnoRotLibre,
            HashMap<String,Integer> _hashTiposHE){
    
        //Tiene las dos marcas en la misma fecha
        DetalleAsistenciaVO auxDetalleCalculoReturn=new DetalleAsistenciaVO();
        int tipoMarca;
        String fechaHoraMarca;
        String fechaMarca;
        String horaMarca;
        String comentarioMarca="";
        String horaMarcaFull;
        String fechaEntrada  = "";
        String fechaSalida  = "";
        int minsColacion = 0;
        String horaEntrada  = "";
        String horaSalida   = "";
        String comentarioEntrada  = "";
        String comentarioSalida   = "";
        String horaEntradaFull  = "";
        String horaSalidaFull   = "";
        System.out.println(WEB_NAME+"[GestionFemase."
            + "CalculoAsistenciaBp]"
            + "getInfoAsistencia(). "
            + "Ordenando marcas");
        
        if (_detalleturno != null){        
            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]"
                + "getInfoAsistencia. "
                + " turno.horaEntrada= " + _detalleturno.getHoraEntrada()
                + ",turno.horaSalida= " + _detalleturno.getHoraSalida()); 
        }else{
            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]"
                + "getInfoAsistencia. No tiene turno asignado para la fecha");
        
        }
        LinkedHashMap<String, DetalleMarcaVO> marcasOrdenadas 
            = new LinkedHashMap<>();
        SimpleDateFormat soloFechaFmt = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat fullFechaFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar mycal = Calendar.getInstance();
        
        Iterator<MarcaVO> collMarcas = _listaMarcas.values().iterator();
        while(collMarcas.hasNext()){//while 7
            MarcaVO currentMarca = collMarcas.next();
            
            tipoMarca = currentMarca.getTipoMarca();
            fechaHoraMarca = currentMarca.getFechaHora();
            comentarioMarca = currentMarca.getComentario();
            //2017-06-01 09:00:01
            StringTokenizer tokenFechaHora = new StringTokenizer(fechaHoraMarca, " ");
            StringTokenizer tokenFecha = new StringTokenizer(tokenFechaHora.nextToken(), "-");
            StringTokenizer tokenHora = new StringTokenizer(tokenFechaHora.nextToken(), ":");
            fechaMarca = tokenFecha.nextToken() + "-" + tokenFecha.nextToken() + "-" + tokenFecha.nextToken();
            horaMarcaFull = tokenHora.nextToken() + ":" + tokenHora.nextToken() + ":" + tokenHora.nextToken();//considera los segundos
            horaMarca = horaMarcaFull.substring(0, horaMarcaFull.length()-3) + ":00";//no considerar los segundos
            System.out.println(WEB_NAME+"[GestionFemase."
            + "CalculoAsistenciaBp]"
                + "getInfoAsistencia(). "
                + "fechaMarca: "+fechaMarca
                + ", tipoMarca: "+tipoMarca
                + ", horaMarca para calculos: "+horaMarca
                + ", horaMarca full: "+horaMarcaFull
                + ", comentarioMarca: "+comentarioMarca);
            
            //error al iterar marcas...debe tomar la marca de entrada primero...
            if (tipoMarca == 1){
                fechaEntrada = fechaMarca;
                horaEntrada = horaMarca;
                horaEntradaFull = horaMarcaFull;
                comentarioEntrada = comentarioMarca; 
            }else{
                fechaSalida = fechaMarca;
                horaSalida = horaMarca;
                horaSalidaFull = horaMarcaFull;
                comentarioSalida = comentarioMarca;
                System.out.println(WEB_NAME+"[GestionFemase."
                    + "CalculoAsistenciaBp]."
                    + "getInfoAsistencia(). "
                    + "empresaId: "+_empleado.getEmpresaid()
                    + ", rutEmpleado: "+_empleado.getRut()
                    + ", fechaEntrada: "+fechaEntrada
                    + ", horaEntrada: "+horaEntrada
                    + ", comentarioEntrada: "+comentarioEntrada
                    + ", fechaSalida: "+fechaSalida
                    + ", horaSalida: "+horaSalida
                    + ", comentarioSalida: "+comentarioSalida);
                DetalleMarcaVO detalleMarca = new DetalleMarcaVO(_empleado.getEmpresaid(), 
                    _empleado.getRut(),
                    horaEntrada,
                    horaSalida,
                    horaEntradaFull,
                    horaSalidaFull,    
                    fechaEntrada,
                    fechaSalida,
                    comentarioEntrada,
                    comentarioSalida);
                marcasOrdenadas.put(fechaMarca, detalleMarca);

            }

        }//fin while 7

        //iterar marcas ordenadas
        Iterator<DetalleMarcaVO> collMarcasOrdenadas = marcasOrdenadas.values().iterator();
        while(collMarcasOrdenadas.hasNext()){//while 8
            DetalleMarcaVO currentMarca = collMarcasOrdenadas.next();
            String hhmmTeoricas = "";
            String hhmmPresencial = "";
            
            DiferenciaHorasVO diferenciaReal = new DiferenciaHorasVO();
            //if (_detalleturno != null){
                // Restar hora_salida - hora_entrada = hrs_efectivas
                System.out.println(WEB_NAME+"[GestionFemase."
                    + "CalculoAsistenciaBp]"
                    + "getInfoAsistencia. "
                    + "Datos para Hrs presenciales: "+currentMarca.getFechaEntrada()+ " " + currentMarca.getHoraEntrada()
                    + ", fechaHora2: "+currentMarca.getFechaSalida() + " " + currentMarca.getHoraSalida());

                diferenciaReal = 
                    Utilidades.getTimeDifference(currentMarca.getFechaEntrada()+ " " + currentMarca.getHoraEntrada(), 
                        currentMarca.getFechaSalida()+" " +currentMarca.getHoraSalida());
                hhmmPresencial = diferenciaReal.getStrDiferenciaHorasMinutos();
                
            //}
            
            if (_detalleturno != null){
                DiferenciaHorasVO diferenciaTeorica = 
                    Utilidades.getTimeDifference(currentMarca.getFechaEntrada()
                        + " " + _detalleturno.getHoraEntrada(), 
                        currentMarca.getFechaSalida() 
                        + " " + _detalleturno.getHoraSalida());
                hhmmTeoricas = diferenciaTeorica.getStrDiferenciaHorasMinutos();
                minsColacion = _detalleturno.getMinutosColacion();
                System.out.println(WEB_NAME+"[GestionFemase."
                    + "CalculoAsistenciaBp]"
                    + "getInfoAsistencia. "
                    + "Minutos de colacion: " + minsColacion
                    + ",hhmmmPresenciales: " + hhmmPresencial);
                String aux = Utilidades.restarMinsHora(hhmmPresencial, minsColacion);
                hhmmPresencial = aux;
                System.out.println(WEB_NAME+"[GestionFemase."
                    + "CalculoAsistenciaBp]"
                    + "getInfoAsistencia. "
                    + "Hrs presenciales-mins colacion: " + hhmmPresencial);
                diferenciaReal.setStrDiferenciaHorasMinutos(hhmmPresencial);
            }else{
                System.out.println(WEB_NAME+"[GestionFemase."
                    + "CalculoAsistenciaBp]"
                    + "getInfoAsistencia. No tiene turno!!");
            }
            
            int horasReales = diferenciaReal.getIntDiferenciaHoras();
            int minutosReales = diferenciaReal.getIntDiferenciaMinutos();
            int minutosExtras = 0;
            int minutosAtraso = 0;
            int minutosNoTrabajadosEntrada = 0;
            int minutosNoTrabajadosSalida = 0;
            
            /**
             * Set cabecera calculo asistencia
             */
            String hhmmExtras = "00:00";
            DetalleAsistenciaVO detalleCalculo = new DetalleAsistenciaVO();
            detalleCalculo.setFechaHoraCalculo(fullFechaFmt.format(mycal.getTime()));
            detalleCalculo.setEmpresaId(_empleado.getEmpresaid());
            detalleCalculo.setDeptoId(_empleado.getDeptoid());
            detalleCalculo.setCencoId(_empleado.getCencoid());
            detalleCalculo.setRutEmpleado(_empleado.getRut());
            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia**2** "
                + "setFechaEntradaMarca: " + currentMarca.getFechaEntrada());
            detalleCalculo.setFechaEntradaMarca(currentMarca.getFechaEntrada());
            detalleCalculo.setHoraEntrada(currentMarca.getHoraEntradaFull());//fecha hora entrada full
            detalleCalculo.setFechaSalidaMarca(currentMarca.getFechaSalida());
            detalleCalculo.setHoraSalida(currentMarca.getHoraSalidaFull());//fecha hora salida full
            detalleCalculo.setHorasTeoricas(_horasTeoricas);
            detalleCalculo.setHorasReales(horasReales);
            detalleCalculo.setMinutosReales(minutosReales);
            detalleCalculo.setComentarioMarcaEntrada(comentarioEntrada);
            detalleCalculo.setComentarioMarcaSalida(comentarioSalida);
            //added 01-11-2017
            
            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia"
                + "--1-- setHrsPresenciales= " 
                + diferenciaReal.getStrDiferenciaHorasMinutos()+", minsColacion: "+minsColacion); 
            detalleCalculo.setHrsPresenciales(diferenciaReal.getStrDiferenciaHorasMinutos());

            detalleCalculo.setArt22(_empleado.isArticulo22());
            if (_detalleturno != null){
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia"
                    + "--3-- "
                    + "turno.horaEntrada= " + _detalleturno.getHoraEntrada()
                    + ",turno.horaSalida= " + _detalleturno.getHoraSalida()); 
                detalleCalculo.setHoraEntradaTeorica(_detalleturno.getHoraEntrada());
                detalleCalculo.setHoraSalidaTeorica(_detalleturno.getHoraSalida());
                detalleCalculo.setHolguraMinutos(_detalleturno.getHolgura());
                detalleCalculo.setMinutosColacion(_detalleturno.getMinutosColacion());
            }else{
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia."
                    + "NO TIENE MARCAS, "
                    + "Set observacion[8]");
                System.out.println(WEB_NAME+"[GestionFemase."
                    + "CalculoAsistenciaBp]"
                    + "procesaAsistencia] -2- Set Sin turno (Libre)");
                detalleCalculo.setObservacion("Sin turno", "CalculoAsistenciaBp_11");
            }
            
            
//////       comentado     InfoFeriadoVO infoFeriado = 
//////                m_feriadosBp.validaFeriado(currentMarca.getFechaEntrada(), 
//////                    _empleado.getEmpresaid(), 
//////                    _empleado.getRut());
            String strKey = _empleado.getEmpresaid() + "|" + _empleado.getRut() + "|" + currentMarca.getFechaEntrada();
            InfoFeriadoVO infoFeriado = m_fechasCalendarioFeriados.get(strKey);
            boolean esFeriado = infoFeriado.isFeriado();
            
            ////detalleCalculo.setEsFeriado(_hashFeriados.containsKey(currentMarca.getFechaEntrada()));//
            detalleCalculo.setEsFeriado(esFeriado);
            
            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia."
                + "Fecha marca entrada: "+currentMarca.getFechaEntrada()
                + ",Fecha marca salida: "+currentMarca.getFechaSalida()
                + ",Minutos Reales= "+minutosReales
                +", Minutos teoricos= "+_minutosTeoricos);
            int comparaHrsRealesTeoricas = Utilidades.comparaHoras(currentMarca.getFechaEntrada()
                + " " + hhmmTeoricas+":00", 
                    currentMarca.getFechaEntrada() + " " + hhmmPresencial+":00");
            DiferenciaHorasVO diferenciaNTSalida=new DiferenciaHorasVO();
            //if (minutosReales > _minutosTeoricos){
            if (comparaHrsRealesTeoricas == 1){    
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia."
                    + "Seteo de hrs extras. Restar horas-"
                    + " hhmmPresencial: " + hhmmPresencial
                    + ",hhmmTeoricas: "+hhmmTeoricas);
                
                DiferenciaHorasVO diferencia99 = 
                    Utilidades.getTimeDifference(currentMarca.getFechaEntrada()
                        + " " + hhmmPresencial+":00", 
                        currentMarca.getFechaEntrada() 
                        + " " + hhmmTeoricas+":00");
                hhmmExtras = diferencia99.getStrDiferenciaHorasMinutos();
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia."
                    + "Setear hh:mm extras= "+hhmmExtras);
                detalleCalculo.setHoraMinsExtras(hhmmExtras);
                detalleCalculo.setHorasMinsExtrasAutorizadas(hhmmExtras);
            }else
            {
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia."
                    + "Setear atraso..");
                //atraso
                if (minutosReales < _minutosTeoricos){
                    //le faltan minutos en la entrada o en la salida
                    if (_detalleturno != null){
                        String fechaHoraEntradaReal = currentMarca.getFechaEntrada()+" " +currentMarca.getHoraEntrada();
                        String fechaHoraSalidaReal = currentMarca.getFechaSalida()+" " +currentMarca.getHoraSalida();
                        String fechaHoraEntradaTeorica = currentMarca.getFechaEntrada()+" " +_detalleturno.getHoraEntrada();
                        String fechaHoraSalidaTeorica = currentMarca.getFechaSalida()+" " +_detalleturno.getHoraSalida();
                        
                        System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia."
                            + "fechaHoraEntradaReal: " + fechaHoraEntradaReal
                            + ",fechaHoraEntradaTeorica: " + fechaHoraEntradaTeorica
                            + ",minsColacion: " + minsColacion);
                        if (Utilidades.comparaHoras(fechaHoraEntradaTeorica,fechaHoraEntradaReal)==1){
                            //hora entrada real es posterior a la hora de entrada teorica
                            DiferenciaHorasVO diferenciaNTEntrada = 
                                Utilidades.getTimeDifference(currentMarca.getFechaEntrada() + " " + currentMarca.getHoraEntrada(), 
                                    currentMarca.getFechaEntrada()+" " +_detalleturno.getHoraEntrada());
                            
                            minutosNoTrabajadosEntrada = 
                                (diferenciaNTEntrada.getIntDiferenciaMinutos()) - _detalleturno.getMinutosColacion();
                            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia."
                                + "minutosNoTrabajadosEntrada= " + minutosNoTrabajadosEntrada
                                +", Setear atraso de hh:mm= " + diferenciaNTEntrada.getStrDiferenciaHorasMinutos());
                            detalleCalculo.setHhmmAtraso(diferenciaNTEntrada.getStrDiferenciaHorasMinutos());
                            if (minutosNoTrabajadosEntrada < 0) minutosNoTrabajadosEntrada = 0;
                        }
                        System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia."
                            + "fechaHoraSalidaReal: " + fechaHoraSalidaReal
                            + ",fechaHoraSalidaTeorica: " + fechaHoraSalidaTeorica);
                        if (Utilidades.comparaHoras(fechaHoraSalidaReal,fechaHoraSalidaTeorica)==1){
                            //hora salida teorica es posterior a la hora de salida real
                            diferenciaNTSalida = 
                                Utilidades.getTimeDifference(currentMarca.getFechaSalida() + " " + currentMarca.getHoraSalida(), 
                                                      currentMarca.getFechaSalida()+" " +_detalleturno.getHoraSalida());
                            minutosNoTrabajadosSalida = 
                                (diferenciaNTSalida.getIntDiferenciaMinutos()) - _detalleturno.getMinutosColacion();
                            
                            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia."
                                + "fechaHoraSalidaReal: " + fechaHoraSalidaReal
                                + ",fechaHoraSalidaTeorica: " + fechaHoraSalidaTeorica
                                + ",difHHMM: " + diferenciaNTSalida.getStrDiferenciaHorasMinutos());
                            
                            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia."
                                + "minutosNoTrabajadosSalida= "+minutosNoTrabajadosSalida);
                            if (minutosNoTrabajadosSalida < 0) minutosNoTrabajadosSalida = 0;
                        }
                        DiferenciaHorasVO diferenciaAtraso = 
                                Utilidades.getTimeDifference(currentMarca.getFechaEntrada() + " " + _detalleturno.getHoraEntrada(), 
                                                      currentMarca.getFechaEntrada()+" " +currentMarca.getHoraEntrada());                    
                        minutosAtraso = diferenciaAtraso.getIntDiferenciaMinutos();
                        String h1 = currentMarca.getFechaEntrada() + " " + currentMarca.getHoraEntrada();//marca 
                        String h2 = currentMarca.getFechaEntrada()+" " +_detalleturno.getHoraEntrada();//turno
                        String hhmmAtraso = getHorasMinutosAtraso(h1, h2);
                        System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia."
                            + "fechaHoraEntradaReal= " + h1
                            + ",fechaHoraEntradaTeorica= " + h2    
                            + ", hhmmAtraso= " + hhmmAtraso);
                        if (minutosAtraso < 0) minutosAtraso = 0;
                        detalleCalculo.setMinutosAtraso(minutosAtraso);
                        detalleCalculo.setHhmmAtraso(hhmmAtraso);
                        
                        detalleCalculo.setMinutosNoTrabajadosEntrada(minutosAtraso);
                        detalleCalculo.setMinutosNoTrabajadosSalida(minutosNoTrabajadosSalida);

                    }
                }
            }

            //added 01-11-2017
            /**
            * horaMinsTrabajados= (hh:mm turno) + (hh:mm marca_salida - hh:mm salida turno ) [B]
            * si (atraso > 0){
            *   horaMinsTrabajados =  horaMinsTrabajados - (hh:mm entrada turno - hh:mm marca_entrada) [C]
            * }
            */
            String hhmmTurno="";
            if (_detalleturno != null){
                System.err.println("[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia. "
                    + "Calcular diferencia entre hra entrada y hra entrada turno. "
                    + "hora marca entrada = "+ currentMarca.getHoraEntrada()
                    +", hora turno entrada = "+ _detalleturno.getHoraEntrada()
                    + ",hora marca salida = "+ currentMarca.getHoraSalida()
                    +", hora turno salida = "+ _detalleturno.getHoraSalida()
                );
                
                DiferenciaHorasVO diferenciaEntradaTurno = 
                    Utilidades.getTimeDifference(currentMarca.getFechaEntrada() + " " + currentMarca.getHoraEntrada(), 
                            currentMarca.getFechaEntrada()+" " +_detalleturno.getHoraEntrada());
                
                //int auxMinsAtraso = diferenciaEntradaTurno.getIntDiferenciaMinutos(); 
                
                String h1 = currentMarca.getFechaEntrada() + " " + currentMarca.getHoraEntrada();//marca 
                String h2 = currentMarca.getFechaEntrada()+" " +_detalleturno.getHoraEntrada();//turno
                String hhmmAtraso = getHorasMinutosAtraso(h1, h2);
                String parteBEntrada = diferenciaEntradaTurno.getStrDiferenciaHorasMinutos();
                                
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia.-1- "
                    + "fechaHra1: " + currentMarca.getFechaSalida() + " " + currentMarca.getHoraSalida()
                    + "fechaHra2: " + currentMarca.getFechaSalida()+" " +_detalleturno.getHoraSalida() 
                    + ", hhmmAtrasoX: " + hhmmAtraso);
                DiferenciaHorasVO diferenciaSalidaTurno = 
                    Utilidades.getTimeDifference(currentMarca.getFechaSalida() + " " + currentMarca.getHoraSalida(), 
                            currentMarca.getFechaSalida()+ " " + _detalleturno.getHoraSalida());
                int auxMinExtras = diferenciaSalidaTurno.getIntDiferenciaMinutos();  
                String parteBSalida = diferenciaSalidaTurno.getStrDiferenciaHorasMinutos();
                
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia.-2- "
                    + "fechaHra1: "+currentMarca.getFechaEntrada() + " " + _detalleturno.getHoraEntrada()+
                    ", fechaHra2: "+currentMarca.getFechaSalida()+" " +_detalleturno.getHoraSalida());
                DiferenciaHorasVO diferenciaTurno = 
                    Utilidades.getTimeDifference(currentMarca.getFechaEntrada() + " " + _detalleturno.getHoraEntrada(), 
                                          currentMarca.getFechaSalida() + " " + _detalleturno.getHoraSalida());
                hhmmTurno = diferenciaTurno.getStrDiferenciaHorasMinutos();
                System.err.println("[GestionFemase.CalculoAsistenciaBp]"
                        + "getInfoAsistencia.hhmmTurno = "+ hhmmTurno
                    +", parteBEntrada = "+parteBEntrada
                    +", parteBSalida = "+parteBSalida
                    +", hh:mm atraso = "+hhmmAtraso
                    +", auxMinExtras = "+auxMinExtras);
                 
                /**
                 * Si hora marca salida es menor (anterior) a la hora de salida del turno
                 * Si hora marca entrada es mayor (posterior) a la hora de entrada del turno
                 * Se deben restar minutos a las hrs trabajadas
                 * 
                 */
                String hraEntradaMarca = soloFechaFmt.format(mycal.getTime()) + " " + currentMarca.getHoraEntrada();
                String hraEntradaTurno = soloFechaFmt.format(mycal.getTime()) + " " + _detalleturno.getHoraEntrada();
                String hraSalidaMarca = soloFechaFmt.format(mycal.getTime()) + " " + currentMarca.getHoraSalida();
                String hraSalidaTurno = soloFechaFmt.format(mycal.getTime()) + " " + _detalleturno.getHoraSalida();
                int comparahraEntrada = Utilidades.comparaHoras(hraEntradaMarca, hraEntradaTurno);
                int comparahraSalida = Utilidades.comparaHoras(hraSalidaTurno,hraSalidaMarca);
                System.err.println("[GestionFemase.CalculoAsistenciaBp]"
                    + "getInfoAsistencia."
                    + "comparahraEntrada = "+ comparahraEntrada
                    + ", comparahraSalida = "+ comparahraSalida
                    + ", hhmmAtraso: "+hhmmAtraso);

                //if (auxMinsAtraso < 0) auxMinsAtraso = 0;
                //detalleCalculo.setMinutosAtraso(auxMinsAtraso);
                detalleCalculo.setHhmmAtraso(hhmmAtraso);    
                
                if (diferenciaNTSalida != null 
                        && diferenciaNTSalida.getStrDiferenciaHorasMinutos()!=null
                        && diferenciaNTSalida.getStrDiferenciaHorasMinutos().compareTo("")==0 
                        && auxMinExtras > 0) 
                {   System.err.println("[GestionFemase.CalculoAsistenciaBp]"
                        + "getInfoAsistencia."
                        + " **1** setHrsExtras = "+ parteBSalida);
                    detalleCalculo.setMinutosExtras(auxMinExtras);   
                }
                if ( diferenciaNTSalida != null 
                        && diferenciaNTSalida.getStrDiferenciaHorasMinutos()!=null
                        && diferenciaNTSalida.getStrDiferenciaHorasMinutos().compareTo("") == 0)
                {
                    System.err.println("[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia."
                        + " **2** setHrsExtras = "+ parteBSalida);
                    detalleCalculo.setHoraMinsExtras(parteBSalida);
                    detalleCalculo.setHorasMinsExtrasAutorizadas(parteBSalida);
                }
                
                System.err.println("[GestionFemase."
                    + "CalculoAsistenciaBp] -A- "
                    + "getInfoAsistencia. hh:mm atraso=" + hhmmAtraso);

////                detalleCalculo.setHrsTrabajadas(horasMinsTrabajados);
            }else{
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia."
                    + "NO TIENE MARCAS, "
                    + "Set observacion[7]");
                System.out.println(WEB_NAME+"[GestionFemase."
                    + "CalculoAsistenciaBp]"
                    + "procesaAsistencia] -3- Set Sin turno (Libre)");
                detalleCalculo.setObservacion("Sin turno", "CalculoAsistenciaBp_12");
                
////                DiferenciaHorasVO diferencia66 = 
////                    Utilidades.getTimeDifference(currentMarca.getFechaSalida() + " " + currentMarca.getHoraEntrada(), 
////                        currentMarca.getFechaSalida() + " " + currentMarca.getHoraSalida());
////                System.out.println(WEB_NAME+"[getInfoAsistencia]-6- set horasMinsTrabajados");
////                horasMinsTrabajados = diferencia66.getStrDiferenciaHorasMinutos();
////                detalleCalculo.setHrsTrabajadas(horasMinsTrabajados);
////                System.out.println(WEB_NAME+"[DetalleAsistenciaController."
////                    + "getInfoAsistencia]--2-- setHrsPresenciales= " 
////                    + horasMinsTrabajados); 
////                detalleCalculo.setHrsPresenciales(horasMinsTrabajados);
            }

            /**
             *  .- si hrs_efectivas > hrs_contrato --> verificar si el empleado tiene hrs extras aprobadas para la fecha_marca    
                .- si hrs_efectivas < hrs_contrato --> verificar si el empleado tiene hrs o dias de ausencia justificadas y aprobadas para la fecha_marca
             */
            //****buscando ausencias en fecha de entrada
            ArrayList<DetalleAusenciaVO> detalleausenciasFechaEntrada = m_listaAusencias.get(_empleado.getRut()+"|"+currentMarca.getFechaEntrada());
            DetalleAusenciaVO detalleausenciaEntrada = null;
            if (detalleausenciasFechaEntrada!=null 
                    && !detalleausenciasFechaEntrada.isEmpty()) {
                detalleausenciaEntrada = detalleausenciasFechaEntrada.get(0);
            }
//            DetalleAusenciaVO detalleausenciaEntrada = 
//                detalleAusenciaBp.getAusencia(_empleado.getRut(), currentMarca.getFechaEntrada());
            if (detalleausenciaEntrada != null){
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia.Tiene ausencias en fecha de entrada!");
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia."
                    + "ausenciaHraInicio:" + detalleausenciaEntrada.getHoraInicioFullAsStr()
                    + ",ausenciaHraFin:" + detalleausenciaEntrada.getHoraFinFullAsStr()
                    + ",ausenciaTipo:" + detalleausenciaEntrada.getNombreAusencia()
                    + ",ausenciaPorHora?:" + detalleausenciaEntrada.getPermiteHora());
                detalleCalculo.setHoraInicioAusencia(detalleausenciaEntrada.getHoraInicioFullAsStr());
                detalleCalculo.setHoraFinAusencia(detalleausenciaEntrada.getHoraFinFullAsStr());
                if (detalleausenciaEntrada.getPermiteHora().compareTo("S") == 0){
                    System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia.-6- "
                        + "fechaHra1: "+currentMarca.getFechaEntrada() + " " + detalleausenciaEntrada.getHoraInicioFullAsStr()+
                        "fechaHra2: "+currentMarca.getFechaEntrada()+" " +detalleausenciaEntrada.getHoraFinFullAsStr());
                    DiferenciaHorasVO diferencia7 = 
                        Utilidades.getTimeDifference(currentMarca.getFechaEntrada() + " " + detalleausenciaEntrada.getHoraInicioFullAsStr(), 
                                              currentMarca.getFechaEntrada() + " " + detalleausenciaEntrada.getHoraFinFullAsStr());
                    String hrsAusenciaTrabajadas = diferencia7.getStrDiferenciaHorasMinutos();
                    
                    ArrayList<String> listaHrsAusencias = new ArrayList<>();
                    ArrayList<String> listaHrsJustificadas = new ArrayList<>();
                    ArrayList<String> listaHrsNoRemuneradas = new ArrayList<>();
                    
                    System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia."
                        + "Hrs ausencia trabajadas"
                        + ",hrs autorizadas:" + hrsAusenciaTrabajadas
                       // + ",hrs mins trabajados:" + horasMinsTrabajados    
                        + ",hrs trabajadas reales:" + detalleCalculo.getHrsTrabajadas()
                       // + ",hh:mm totales:" + auxTotHras
                        + ",hh:mm atraso existentes:" + detalleCalculo.getHhmmAtraso());
                    if (detalleCalculo.getHhmmAtraso() != null 
                        && hrsAusenciaTrabajadas != null){
                            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia.-I-"
                                + "Iterar ausencias para seteo de hrs justificadas...");
                            String auxMsgAusencias = "";
                            String hhmmDifAusencia="";
                            boolean setNoAtraso = false;
                            Iterator<DetalleAusenciaVO> ausenciasIterator = detalleausenciasFechaEntrada.iterator();
                            while (ausenciasIterator.hasNext()) {
                                DetalleAusenciaVO ausencia = ausenciasIterator.next();
                                if (ausencia.getPermiteHora().compareTo("S") == 0){
                                    auxMsgAusencias += ausencia.getNombreAusencia()
                                        +":" + ausencia.getHoraInicioFullAsStr().substring(0, ausencia.getHoraInicioFullAsStr().length()-3) 
                                        +"-" + ausencia.getHoraFinFullAsStr().substring(0, ausencia.getHoraFinFullAsStr().length()-3)+" hrs, ";
                                    //restar hrs con fecha
                                    System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia.Resta hrs inicio-fin ausencia. "
                                        + "Inicio: "+currentMarca.getFechaEntrada() + " " + ausencia.getHoraInicioFullAsStr()
                                        + ", Fin: "+currentMarca.getFechaEntrada() + " " + ausencia.getHoraFinFullAsStr());
                                    DiferenciaHorasVO difAusencia = 
                                            Utilidades.getTimeDifference(currentMarca.getFechaEntrada() + " " + ausencia.getHoraInicioFullAsStr() +":00", 
                                        currentMarca.getFechaEntrada() + " " + ausencia.getHoraFinFullAsStr()+":00");
                                    hhmmDifAusencia = difAusencia.getStrDiferenciaHorasMinutos();
                                    System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia."
                                        + "Agregar dif ausencia= " + difAusencia.getStrDiferenciaHorasMinutos()
                                        + ", hhmmDifAusencia= " + hhmmDifAusencia
                                        + ", detalleCalculo.getHhmmAtraso()= " + detalleCalculo.getHhmmAtraso());
                                    if (ausencia.getTipoAusencia()==1){
                                        listaHrsJustificadas.add(difAusencia.getStrDiferenciaHorasMinutos());
                                    }else{
                                        listaHrsNoRemuneradas.add(difAusencia.getStrDiferenciaHorasMinutos());
                                    }
                                    if (ausencia.getHoraInicioFullAsStr().compareTo(detalleCalculo.getHoraEntradaTeorica()) != 0){
                                        listaHrsAusencias.add(difAusencia.getStrDiferenciaHorasMinutos());
                                    }
                                    if (!setNoAtraso && detalleCalculo.getHhmmAtraso().compareTo(hhmmDifAusencia) == 0){
                                        detalleCalculo.setHhmmAtraso("");
                                        setNoAtraso = true;
                                    }
                                }else{
                                    auxMsgAusencias += ausencia.getNombreAusencia()+",";
                                    if (hhmmTurno != null && hhmmTurno.compareTo("") != 0){
                                        listaHrsAusencias.add(hhmmTurno);
                                    }
                                    //hrs justificadas = suma de hrs de ausencia...
                                }
                            }
                            String sumaJustificadas = Utilidades.sumTimesList(listaHrsAusencias);
                            String sumaJustificadasTotales = Utilidades.sumTimesList(listaHrsJustificadas);
                            String sumaNoRemuneradasTotales = Utilidades.sumTimesList(listaHrsNoRemuneradas);
                            
                            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia."
                                + "sumaJustificadas= " + sumaJustificadas
                                + ",sumaJustificadasTotales= " + sumaJustificadasTotales    
                                + ", hhmmDifAusencia= " + hhmmDifAusencia
                                + ", hrsPresenciales= " + detalleCalculo.getHrsPresenciales());
                            detalleCalculo.setHhmmJustificadas(sumaJustificadasTotales);
                            detalleCalculo.setHrsAusencia(sumaNoRemuneradasTotales);
                            
                            if (detalleausenciasFechaEntrada.size() > 1){
                                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia."
                                    + "Recalculo horas presenciales...");
                                DiferenciaHorasVO newHP = Utilidades.getTimeDifference(currentMarca.getFechaEntrada()
                                    +" " + detalleCalculo.getHrsPresenciales(), 
                                    currentMarca.getFechaEntrada()
                                    +" " + sumaJustificadas+":00");
                                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia."
                                    + "new Horas presenciales: " + newHP.getStrDiferenciaHorasMinutos());
                                detalleCalculo.setHrsPresenciales(newHP.getStrDiferenciaHorasMinutos());
                            }
//                            if (sumaJustificadas.compareTo(hhmmDifAusencia) == 0){
//                                detalleCalculo.setHhmmAtraso("");
//                            }
                            //detalleCalculo.setHhmmJustificadas(hrsAusenciaTrabajadas);
                            //detalleCalculo.setHhmmAtraso("");
                    }
                    
                    //detalleCalculo.setHrsTrabajadas(auxTotHras);
                    detalleCalculo.setMinutosAtraso(0);
                    System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia."
                        + "NO TIENE MARCAS, "
                        + " Seteo de ausencias...");
                    
                    ArrayList<DetalleAusenciaVO> detalleausencia2 = m_listaAusencias.get(_empleado.getRut()+"|"+currentMarca.getFechaEntrada());
                    
//                    ArrayList<DetalleAusenciaVO> detalleausencia2 = 
//                        detalleAusenciaBp.getAusencias(_empleado.getRut(), 
//                            currentMarca.getFechaEntrada());
                    
                    String auxMsgAusencias = "";
                    //iterando ausencias
                    System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia.-II-"
                        + "Iterar ausencias para seteo de hrs justificadas...");
                    Iterator<DetalleAusenciaVO> ausenciasIterator = detalleausencia2.iterator();
                    while (ausenciasIterator.hasNext()) {
                        DetalleAusenciaVO ausencia = ausenciasIterator.next();
                        //***
                        if (ausencia.getPermiteHora().compareTo("S") == 0){
                            auxMsgAusencias += ausencia.getNombreAusencia()
                                +":" + ausencia.getHoraInicioFullAsStr().substring(0, ausencia.getHoraInicioFullAsStr().length()-3) 
                                +"-" + ausencia.getHoraFinFullAsStr().substring(0, ausencia.getHoraFinFullAsStr().length()-3)+" hrs, ";
                            //restar hrs con fecha
                            DiferenciaHorasVO difAusencia = Utilidades.getTimeDifference(currentMarca.getFechaEntrada() + " " + ausencia.getHoraInicioFullAsStr() +":00", 
                                currentMarca.getFechaEntrada() + " " + ausencia.getHoraFinFullAsStr()+":00");
                            listaHrsAusencias.add(difAusencia.getStrDiferenciaHorasMinutos());
                        }else{
                            auxMsgAusencias += ausencia.getNombreAusencia();
                            if (hhmmTurno != null && hhmmTurno.compareTo("") != 0){
                                listaHrsAusencias.add(hhmmTurno);
                            }
                        }
                    }
                    
                    auxMsgAusencias=auxMsgAusencias.substring(0, auxMsgAusencias.length()-2);
                    detalleCalculo.setObservacion(auxMsgAusencias, "CalculoAsistenciaBp_13");
                    System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]setobservacion [99]:"
                        + "auxMsgAusencias: "+auxMsgAusencias);
                    
                }
            }
            if (currentMarca.getFechaEntrada().compareTo(currentMarca.getFechaSalida()) !=0 ){
                ArrayList<DetalleAusenciaVO> detalleausenciasFechaSalida = m_listaAusencias.get(_empleado.getRut()+"|"+currentMarca.getFechaSalida());
                DetalleAusenciaVO detalleausenciaSalida = null;
                if (detalleausenciasFechaSalida!=null && !detalleausenciasFechaSalida.isEmpty()) {
                    detalleausenciaSalida = detalleausenciasFechaSalida.get(0);
                }
//                DetalleAusenciaVO detalleausenciaSalida = 
//                    detalleAusenciaBp.getAusencia(_empleado.getRut(), currentMarca.getFechaSalida());
            
                if (detalleausenciaSalida != null){
                    System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia."
                        + "Tiene ausencias en fecha de salida!");
                    System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia."
                        + "ausenciaHraInicio:" + detalleausenciaSalida.getHoraInicioFullAsStr()
                        + ",ausenciaHraFin:" + detalleausenciaSalida.getHoraFinFullAsStr()
                        + ",ausenciaTipo:" + detalleausenciaSalida.getNombreAusencia()
                        + ",ausenciaPorHora?:" + detalleausenciaSalida.getPermiteHora());
                    detalleCalculo.setHoraInicioAusencia(detalleausenciaSalida.getHoraInicioFullAsStr());
                    detalleCalculo.setHoraFinAusencia(detalleausenciaSalida.getHoraFinFullAsStr());
                    if (detalleausenciaSalida.getPermiteHora().compareTo("S") == 0){
                        System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia.-6- fechaHra1: "+currentMarca.getFechaSalida() + " " + detalleausenciaSalida.getHoraInicioFullAsStr()+
                                "fechaHra2: "+currentMarca.getFechaSalida()+" " +detalleausenciaSalida.getHoraFinFullAsStr());
                        DiferenciaHorasVO diferencia7 = 
                                Utilidades.getTimeDifference(currentMarca.getFechaSalida() + " " + detalleausenciaSalida.getHoraInicioFullAsStr(), 
                                                    currentMarca.getFechaSalida() + " " + detalleausenciaSalida.getHoraFinFullAsStr());
                        String hrsAusenciaTrabajadas = diferencia7.getStrDiferenciaHorasMinutos();
                        //String auxTotHras = Utilidades.sumaHoras(hrsAusenciaTrabajadas, horasMinsTrabajados);
                        ArrayList<String> listaHoras = new ArrayList<>();
                        listaHoras.add(hrsAusenciaTrabajadas);
                        //listaHoras.add(horasMinsTrabajados);
                        //String auxTotHras = Utilidades.sumaHoras(hrsAusenciaTrabajadas, horasMinsTrabajados);
                        String auxTotHras = Utilidades.sumTimesList(listaHoras);

                        System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia."
                            + "Hrs ausencia trabajadas"
                            + "hrs autorizadas:" + hrsAusenciaTrabajadas
                            + ",hrs trabajadas reales:" + detalleCalculo.getHrsTrabajadas()
                            + ",hh:mm totales:" + auxTotHras);
                        if (detalleCalculo.getHhmmAtraso() != null 
                            && hrsAusenciaTrabajadas != null){
                                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia."
                                    + "Seteo de hrs justificadas. "
                                    + "El atraso queda en cero por ausencia justificada");
                                ArrayList<DetalleAusenciaVO> detalleausencia = m_listaAusencias.get(_empleado.getRut()+"|"+currentMarca.getFechaEntrada());
                                
//                                ArrayList<DetalleAusenciaVO> detalleausencia = 
//                                       detalleAusenciaBp.getAusencias(_empleado.getRut(), 
//                                           currentMarca.getFechaEntrada());
                                ArrayList<String> listaHrsAusencias = new ArrayList<>();
                                String auxMsgAusencias="";
                                Iterator<DetalleAusenciaVO> ausenciasIterator = detalleausencia.iterator();
                                while (ausenciasIterator.hasNext()) {
                                    DetalleAusenciaVO ausencia = ausenciasIterator.next();
                                    if (ausencia.getPermiteHora().compareTo("S") == 0){
                                        auxMsgAusencias += ausencia.getNombreAusencia()
                                            +":" + ausencia.getHoraInicioFullAsStr().substring(0, ausencia.getHoraInicioFullAsStr().length()-3) 
                                            +"-" + ausencia.getHoraFinFullAsStr().substring(0, ausencia.getHoraFinFullAsStr().length()-3)+" hrs, ";
                                        //restar hrs con fecha
                                        DiferenciaHorasVO difAusencia = Utilidades.getTimeDifference(currentMarca.getFechaEntrada() + " " + ausencia.getHoraInicioFullAsStr() +":00", 
                                               currentMarca.getFechaEntrada() + " " + ausencia.getHoraFinFullAsStr()+":00");
                                        listaHrsAusencias.add(difAusencia.getStrDiferenciaHorasMinutos());
                                    }else{
                                        auxMsgAusencias += ausencia.getNombreAusencia()+",";
                                        if (hhmmTurno != null && hhmmTurno.compareTo("") != 0){
                                            listaHrsAusencias.add(hhmmTurno);
                                        }
                                        //hrs justificadas = suma de hrs de ausencia...
                                    }
                                }
                                String sumaJustificadas = Utilidades.sumTimesList(listaHrsAusencias);
                                detalleCalculo.setHhmmJustificadas(sumaJustificadas);
                                //detalleCalculo.setHhmmJustificadas(hrsAusenciaTrabajadas);
                                detalleCalculo.setHhmmAtraso("");
                        }
                        //detalleCalculo.setHrsTrabajadas(auxTotHras);
                        detalleCalculo.setMinutosAtraso(0);
                        System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia."
                            + "NO TIENE MARCAS, "
                            + "Set observacion[4]. Con detalle de ausencias");

                        ArrayList<DetalleAusenciaVO> detalleausencia = m_listaAusencias.get(_empleado.getRut()+"|"+currentMarca.getFechaSalida());
                        
//                        ArrayList<DetalleAusenciaVO> detalleausencia = 
//                            detalleAusenciaBp.getAusencias(_empleado.getRut(), 
//                                currentMarca.getFechaSalida());
                        
                        String auxMsgAusencias = "";
                        //iterando ausencias
                        Iterator<DetalleAusenciaVO> ausenciasIterator = detalleausencia.iterator();
                        while (ausenciasIterator.hasNext()) {
                            DetalleAusenciaVO ausencia = ausenciasIterator.next();
                            //**
                            auxMsgAusencias += ausencia.getNombreAusencia();
                            if (ausencia.getPermiteHora().compareTo("S") == 0){
                                auxMsgAusencias += ausencia.getNombreAusencia()
                                    +":" + ausencia.getHoraInicioFullAsStr().substring(0, ausencia.getHoraInicioFullAsStr().length()-3) 
                                    +"-" + ausencia.getHoraFinFullAsStr().substring(0, ausencia.getHoraFinFullAsStr().length()-3)+" hrs, ";
                                //restar hrs con fecha
                                DiferenciaHorasVO difAusencia = Utilidades.getTimeDifference(currentMarca.getFechaEntrada() + " " + ausencia.getHoraInicioFullAsStr() +":00", 
                                       currentMarca.getFechaEntrada() + " " + ausencia.getHoraFinFullAsStr()+":00");
                                //listaHrsAusencias.add(difAusencia.getStrDiferenciaHorasMinutos());
                            }else{
                                auxMsgAusencias += ausencia.getNombreAusencia();
    //                            if (hhmmTurno != null && hhmmTurno.compareTo("") != 0){
    //                                listaHrsAusencias.add(hhmmTurno);
    //                            }
                                //hrs justificadas = suma de hrs de ausencia...
                            }

    //                        auxMsgAusencias += ausencia.getNombreAusencia()
    //                            +":"+ausencia.getHoraInicioFullAsStr().substring(0, ausencia.getHoraInicioFullAsStr().length()-3) 
    //                            +"-"+ausencia.getHoraFinFullAsStr().substring(0, ausencia.getHoraFinFullAsStr().length()-3)+" hrs, ";
                            //***
                        }
                        auxMsgAusencias=auxMsgAusencias.substring(0, auxMsgAusencias.length()-2);
                        detalleCalculo.setObservacion(auxMsgAusencias, "CalculoAsistenciaBp_14");
    //                    detalleCalculo.setObservacion(detalleausenciaSalida.getNombreAusencia()
    //                        +" de "+detalleausenciaSalida.getHoraInicioFullAsStr().substring(0, detalleausenciaSalida.getHoraInicioFullAsStr().length()-3) 
    //                        +" a "+detalleausenciaSalida.getHoraFinFullAsStr().substring(0, detalleausenciaSalida.getHoraFinFullAsStr().length()-3)+" hrs");
                    }
                }
            }
            System.err.println("[GestionFemase."
                + "CalculoAsistenciaBp]"
                + "getInfoAsistencia."
                + "observacion a setear: "+detalleCalculo.getObservacion());
            
////            if (_detalleTurnoRotLibre!=null){
////                System.err.println("[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia.Set observacion= dia libre");
////                detalleCalculo.setObservacion("Dia libre");
////            }
            /**
             * Tipos de hrs extras:
                A.- Dentro del turno (dia habil)       	al 50 o al 100%?
                B.- Dentro del turno (dia feriado)     	al 50 o al 100%?
                C.- Fuera del turno (dia habil) 	al 50 o al 100%?
                D.- Fuera del turno (dia feriado)   al 50 o al 100%?
             */
            int factorHE = 50;
            if (!_empleado.isArticulo22()){
                if (minutosExtras > 0){
                    if (_horasTeoricas == 0){
                        /** 
                         *  FUERA DE TURNO
                         */
                        if (detalleCalculo.isEsFeriado()){
                            factorHE = _hashTiposHE.get("D");
                        }else factorHE = _hashTiposHE.get("C");

                        System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia."
                            + "codDia esta fuera de turno");
                        //minsExtrasAl100=minutosTrabajados;
                        if (factorHE == 50){
                            detalleCalculo.setMinutosExtrasAl50(minutosExtras);
                        }else if (factorHE == 100){
                            detalleCalculo.setMinutosExtrasAl100(minutosExtras);
                        }

                    }else if (_horasTeoricas > 0){
                            /** 
                            *  DENTRO DE TURNO
                            */
                           if (detalleCalculo.isEsFeriado()){
                               factorHE = _hashTiposHE.get("B");
                           }else factorHE = _hashTiposHE.get("A");

                            System.out.println(WEB_NAME+"[GestionFemase."
                                    + "CalculoAsistenciaBp]getInfoAsistencia."
                                + "codDia esta fuera de turno");
                            //minsExtrasAl100=minutosTrabajados;
                            if (factorHE == 50){
                                detalleCalculo.setMinutosExtrasAl50(minutosExtras);
                            }else if (factorHE == 100){
                                detalleCalculo.setMinutosExtrasAl100(minutosExtras);
                            }
                    }     
                }
            }

            detalleCalculo.setMinutosTeoricos(_horasTeoricas * 60);
//            String auxKey=detalleCalculo.getFechaEntradaMarca()
//                +"|"+currentMarca.getHoraEntrada()
//                +"|"+_empleado.getRut();
            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getInfoAsistencia."
                + "**3** Set detalleCalculo."
                + ",fecha entrada=" + detalleCalculo.getFechaEntradaMarca()
                +", hora Entrada=" + detalleCalculo.getHoraEntrada()
                +", observacion=" + detalleCalculo.getObservacion());
            
            auxDetalleCalculoReturn = detalleCalculo;
//            auxLstDetalleCalculoReturn.put(auxKey, detalleCalculo);
            
        }//fin while 8
        
        
        return auxDetalleCalculoReturn;
    }
    
    private String getHorasMinutosAtraso(String _horaMarcaEntrada, String _horaTurnoEntrada){
        String hhmmAtraso = "";//int auxMinsAtraso = diferenciaEntradaTurno.getIntDiferenciaMinutos(); 
        String h1 = _horaMarcaEntrada;//marca 
        String h2 = _horaTurnoEntrada;//turno
        int aux2 = Utilidades.comparaHoras(h2,h1);
        DiferenciaHorasVO diferenciaHoraEntrada = Utilidades.getTimeDifference(h1, h2);
        System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getHorasMinutosAtraso."
            + "hora1: "+h1+", hora2: "+h2+", diferencia: " + aux2);
        if (aux2 == 1){
            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getHorasMinutosAtraso."
                + "Hora marca entrada: " + h1 
                + ", hora turno entrada: " + h2 
                + ", Setear atraso de hh:mm= " + diferenciaHoraEntrada.getStrDiferenciaHorasMinutos());
            hhmmAtraso = diferenciaHoraEntrada.getStrDiferenciaHorasMinutos();
        }else{
            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getHorasMinutosAtraso."
                + "Hora marca entrada: " + h1 
                + ", hora turno entrada: " + h2 
                + ", No Hay atraso");
        }
        return hhmmAtraso;
    }
    
    /**
     * 
     * Procesa marcas de empleado con turno rotativo
     * 
     */
    private MarcaVO getMarcaTurnoRotativo(MarcasBp _marcasBp, 
            EmpleadoVO _empleado, 
            String _fechaMarca, 
            String _horaMarca, 
            int _tipoMarca){
    
        MarcaVO marca=null;
        SimpleDateFormat fechaFmt = new SimpleDateFormat("yyyy-MM-dd");
        String keyMarca = _fechaMarca + "|" + _horaMarca;
        Date dteFechamarca = Utilidades.getDateFromString(_fechaMarca, "yyyy-MM-dd");
        Date dteAnterior = Utilidades.sumaRestarFecha(dteFechamarca, -1, "DAYS");
        Date dteSiguiente = Utilidades.sumaRestarFecha(dteFechamarca, 1, "DAYS");
        String strDiaAnterior = Utilidades.getDatePartAsString(dteAnterior, "yyyy-MM-dd");
        String strDiaSiguiente = Utilidades.getDatePartAsString(dteSiguiente, "yyyy-MM-dd");
        System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getMarcaTurnoRotativo. "
            + "keyMarca: "+keyMarca
            + ",tipoMarca: "+_tipoMarca    
            + ",diaAnterior: "+strDiaAnterior
            + ",diaSiguiente: "+strDiaSiguiente);
        if (_tipoMarca == 1){//es una marca de entrada
            // -------- buscar marca de salida el dia siguiente ------------
            //no siempre se debe buscar la marca al dia sgte...
            DiferenciaHorasVO diferenciaET = 
                    Utilidades.getTimeDifference(fechaFmt.format(new Date()) + " " + _horaMarca, 
                                          fechaFmt.format(new Date())+" 23:59:59");
            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getMarcaTurnoRotativo."
                + "horaEntrada: " 
                + _horaMarca
                +", diferencia en horas con las 23:59:59= " 
                + diferenciaET.getIntDiferenciaHoras());
            if (diferenciaET.getIntDiferenciaHoras() <= 4){
                //fecha salida = dia siguiente (turno noche)
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getMarcaTurnoRotativo."
                    + " Fecha salida = dia siguiente (turno noche)");
//                marca = _marcasBp.getMarcaByTipo(_empleado.getEmpresa().getId(), 
//                    _empleado.getRut(), 
//                    strDiaSiguiente, strDiaSiguiente,2, _fechaMarca + " " + _horaMarca);
                System.out.println(WEB_NAME+"[PASO 2]");
                String jsonOutput = _marcasBp.getMarcaByTipoJson(_empleado.getEmpresaid(), 
                    _empleado.getRut(), 
                    strDiaSiguiente, strDiaSiguiente,2, _fechaMarca + " " + _horaMarca, false);
                marca = new Gson().fromJson(jsonOutput, MarcaVO.class);
                
            }else{
                //fecha salida = misma fecha de la entrada (turno normal)
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getMarcaTurnoRotativo."
                    + " Fecha salida = misma fecha de la entrada (turno normal)");
//                marca = _marcasBp.getMarcaByTipo(_empleado.getEmpresa().getId(), 
//                    _empleado.getRut(), 
//                    fechaFmt.format(dteFechamarca), fechaFmt.format(dteFechamarca),2,_fechaMarca + " " + _horaMarca);
                System.out.println(WEB_NAME+"[PASO 3]");
                String jsonOutput = _marcasBp.getMarcaByTipoJson(_empleado.getEmpresaid(), 
                    _empleado.getRut(), 
                    fechaFmt.format(dteFechamarca), fechaFmt.format(dteFechamarca),2,_fechaMarca + " " + _horaMarca, false);
                marca = new Gson().fromJson(jsonOutput, MarcaVO.class);
                
                if (marca == null){
                    System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getMarcaTurnoRotativo."
                        + " Fecha salida = dia siguiente (turno normal). "
                            + "Buscar marcas para el dia siguiente...");
                    //buscar marcas para el dia sgte
                    boolean ambasMarcas  = false;
                    
//                    LinkedHashMap<String, MarcaVO> marcasDiaSgte =
//                        _marcasBp.getMarcasEmpleado(_empleado.getEmpresa().getId(), _empleado.getRut()
//                        , strDiaSiguiente, strDiaSiguiente);
                    
                    String jsonOutput2 = 
                        _marcasBp.getMarcasJson(_empleado.getEmpresaid(), _empleado.getRut()
                        , strDiaSiguiente, strDiaSiguiente);
                    Type listType = new TypeToken<LinkedHashMap<String, MarcaVO>>() {}.getType();
                    LinkedHashMap<String, MarcaVO> marcasDiaSgte = new Gson().fromJson(jsonOutput2, listType);
                                        
                    if (!marcasDiaSgte.isEmpty()){
                        boolean entrada = false;
                        boolean salida  = false;
                        
                        Set entrySet    = marcasDiaSgte.entrySet();
                        Iterator it     = entrySet.iterator();
                        while(it.hasNext()){
                            Map.Entry item = (Map.Entry) it.next();
                            MarcaVO auxmarca = (MarcaVO)item.getValue();
                            System.out.println(WEB_NAME+"Key = " + item.getKey() + " Value = " + item.getValue());
                            if (auxmarca.getTipoMarca() == Constantes.MARCA_ENTRADA){
                                entrada = true;
                            }else if (auxmarca.getTipoMarca() == Constantes.MARCA_SALIDA){
                                    salida = true;
                            } 
                        }
                        if (entrada && salida) ambasMarcas = true;
                    }
                    if (!ambasMarcas){
//                        marca = _marcasBp.getMarcaByTipo(_empleado.getEmpresa().getId(), 
//                            _empleado.getRut(), 
//                            strDiaSiguiente, strDiaSiguiente,2, _fechaMarca + " " + _horaMarca);
                        System.out.println(WEB_NAME+"[PASO 4]");
                        String jsonOutput3 = _marcasBp.getMarcaByTipoJson(_empleado.getEmpresaid(), 
                            _empleado.getRut(), 
                            strDiaSiguiente, strDiaSiguiente,2, _fechaMarca + " " + _horaMarca, false);
                        marca = new Gson().fromJson(jsonOutput3, MarcaVO.class);
                    }
                }
            }
            
        }else if (m_marcasProcesadas.get(keyMarca)== null){//si la hora marca no esta procesada
                // ------- buscar marca de entrada el dia anterior -------------
                System.out.println(WEB_NAME+"[GestionFemase."
                    + "CalculoAsistenciaBp]getMarcaTurnoRotativo."
                    + "-->Buscar marca de entrada el dia anterior");
//                marca = _marcasBp.getMarcaByTipo(_empleado.getEmpresa().getId(), 
//                    _empleado.getRut(), 
//                    strDiaAnterior, strDiaAnterior,1,null);
                System.out.println(WEB_NAME+"[PASO 5]");
                String jsonOutput3 = _marcasBp.getMarcaByTipoJson(_empleado.getEmpresaId(), 
                    _empleado.getRut(), 
                    strDiaAnterior, strDiaAnterior,1,null, false);
                marca = new Gson().fromJson(jsonOutput3, MarcaVO.class);
                
        }
        if (marca!=null){
            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaBp]getMarcaTurnoRotativo."
                + "Agregar marca fecha-hora: "+marca.getFechaHora());
        }
        return marca;
    }
    
    public EstadoVO getCurrentStatus(String _empresaId, 
            String _deptoId, 
            int _cencoId){
    
        return this.estadoEjecucionDao.getCurrentStatus(_empresaId, _deptoId, _cencoId);
        
    }
    
    public int insertStatusCalculo(CalculoAsistenciaEstadoEjecucionVO _data){
        return this.estadoEjecucionDao.insert(_data);
    }
    
    public int deleteStatusCalculo(String _empresaId, 
            String _deptoId, 
            int _cencoId){
        return this.estadoEjecucionDao.delete(_empresaId, _deptoId, _cencoId);
    }
    
    public int updateStatusCalculo(CalculoAsistenciaEstadoEjecucionVO _data){
        return this.estadoEjecucionDao.updateStatus(_data);
    }
}
