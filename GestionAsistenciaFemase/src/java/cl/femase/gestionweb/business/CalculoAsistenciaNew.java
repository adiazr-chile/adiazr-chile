/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.business;

import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.dao.LogErrorDAO;
import cl.femase.gestionweb.vo.DetalleAsistenciaVO;
import cl.femase.gestionweb.vo.DetalleAusenciaJsonVO;
import cl.femase.gestionweb.vo.DetalleMarcaVO;
import cl.femase.gestionweb.vo.DetalleTurnoVO;
import cl.femase.gestionweb.vo.DiferenciaHorasVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.InfoFeriadoVO;
import cl.femase.gestionweb.vo.LogErrorVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.MarcaJsonVO;
import cl.femase.gestionweb.vo.MarcaVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import org.json.JSONObject;

/**
 *
 * @author Alexander
 */
public class CalculoAsistenciaNew extends BaseBp{
    
    private final String nombreHebra;
    private String IT_FECHA = "";
    private final HashMap<String, MarcaVO> m_marcasProcesadas = new HashMap<>();
    public List<EmpleadoVO> m_listaEmpleados=new ArrayList<>();
    public String[] m_fechasCalculo;    
    public LinkedHashMap<Integer, LinkedHashMap<Integer,DetalleTurnoVO>> m_listaDetalleTurnos = 
        new LinkedHashMap<>();
    public HashMap<String,Integer> m_hashTiposHE;
    public HashMap<String,Integer> m_hashFechas = new HashMap<>();
    private static final CalendarioFeriadoBp m_feriadosBp = new CalendarioFeriadoBp(new PropertiesVO());
    
    /**
     * Lista con detalle de dias laborales para turnos rotativos para cada rut
     * La key de cada item en la lista es: empresaId + "|" + rut + "|" + anio + "|"+ mes
     */
    public LinkedHashMap<String, DetalleTurnoVO> m_listaAsignacionTurnosRotativos = 
        new LinkedHashMap<>();
    /**
     * Lista de marcas para cada rut
     * La key de cada item en la lista es: empresaId + "|" + rut + "|" + fecha
     */
    public LinkedHashMap<String, LinkedHashMap<String, MarcaVO>> m_listaMarcas = 
        new LinkedHashMap<>();
    
    /**
     * Lista de ausencias para cada rut
     * La key de cada item en la lista es: rut+"|"+fecha
     */
    public LinkedHashMap<String, ArrayList<DetalleAusenciaJsonVO>> m_listaAusencias = 
        new LinkedHashMap<>();
    
    boolean isHistorico=false;
    
    /**
    * 20210725-001:
    * 
    *   Lista con las fechas y los datos existentes en calendario_feriados.
    *   Los datos son los resultantes de invocar a la funcion validaFechaFeriado por cada fecha del rango
    * 
    */
    public LinkedHashMap<String, InfoFeriadoVO> m_fechasCalendarioFeriados = 
        new LinkedHashMap<>();
    
    public CalculoAsistenciaNew(String _nombreHebra) {
        this.nombreHebra = _nombreHebra;
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
    
        System.out.println(WEB_NAME+"[CalculoAsistenciaNew."
            + "getListaEmpleados]empresa: "+_empresaId
            +", depto: "+_deptoId
            +", cenco: "+_cencoId);
        
        EmpleadosBp empleadosBp = new EmpleadosBp();
        List<EmpleadoVO> listaEmpleados = new ArrayList<>();
        if (_empresaId.compareTo("-1") != 0 
                && _deptoId.compareTo("-1") != 0
                && _cencoId != -1){
                    //todos los empleados del cenco
                    //usar funcion en BD
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
     * Obtiene lista de empleados, con toda la info de cada empleado.
     * Recibe como parametro una lista de ruts
     * @param _empresaId
     * @param _deptoId
     * @param _cencoId
     * @param _listaRuts
     * @return 
     */
    public List<EmpleadoVO> getListaEmpleadosComplete(String _empresaId,
            String _deptoId, 
            int _cencoId,
            List<String> _listaRuts){
    
        System.out.println(WEB_NAME+"[CalculoAsistenciaNew."
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
            System.out.println(WEB_NAME+"[CalculoAsistenciaNew."
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
            
            //System.err.println("[parseo empleadoVO]jsonOutput: "+jsonOutput); 
             
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
            
            //System.out.println(WEB_NAME+"[parseo empleadoVO]2-rut: "+infoEmpleado.getRut());
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
    
    //**********************************************
    /**
     * Metodo principal de calculo de asistencia
     * 
     * @param _empleado
     * @param _iteracion
     * @param _isHistorico
     * 
     * @return 
     */
    public ArrayList<DetalleAsistenciaVO> calculaAsistencia(EmpleadoVO _empleado, 
            int _iteracion, 
            boolean _isHistorico) {
        
        isHistorico = _isHistorico;
        
        System.out.println(WEB_NAME+"Proceso Calculo asistencia ("+this.nombreHebra+"), " + 
            " COMIENZA A PROCESAR "
            + "empleado (" + _empleado.getRut() +")"
            + ", isHistorico? " + isHistorico );
        MaintenanceVO resultado=new MaintenanceVO();
        ArrayList<DetalleAsistenciaVO> dataFechasRut = new ArrayList<>();
        //listaEmpleadosConMarcas.add(empleado);

        /**
         * Ejecuta el calculo de asistencia segun
         * marcas para las fechas solicitadas
         */
        System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]calculaAsistencia"
            + ". Inicio procesa Asistencia para "
            + "rut= " + _empleado.getRut()
            + ", nombre= "+_empleado.getNombreCompleto());
        try {
            
            dataFechasRut = procesaAsistencia(_empleado,
                m_fechasCalculo,
                m_listaDetalleTurnos,
                m_hashTiposHE);
            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]calculaAsistencia"
                + ".dataFechasRut.size=" + dataFechasRut.size());
        } catch (Exception ex) {
            resultado.setMsgError("Error al procesar asistencia para "
                + "rut= " + _empleado.getRut());
            resultado.setThereError(true);
            System.err.println("[GestionFemase.CalculoAsistenciaNew]calculaAsistencia."
                + resultado.getMsgError());
            ex.printStackTrace();
            //*****
            System.err.println("[CalculoAsistenciaRunnable]"
                + "Error: " + ex.toString());
            String clase = "CalculoAsistenciaNew";
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
//        System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]calculaAsistencia. "
//            + "listaCalculosEmpleado.put "
//            + "KEY= " + _empleado.getRut()+"|"+_iteracion);
//        
        //listaCalculosEmpleado.put(_empleado.getRut()+"|"+_iteracion, dataFechasRut);

        System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]calculaAsistencia. "
            + "Fin procesa Asistencia para "
            + "rut= " + _empleado.getRut());
        
        System.out.println(WEB_NAME+"Proceso Calculo asistencia (" + this.nombreHebra + ")"
                + " HA TERMINADO DE PROCESAR rut " + _empleado.getRut());
        
        return dataFechasRut;
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
        ArrayList<DetalleAsistenciaVO> listaAux=new ArrayList<>();        
        Calendar mycal = Calendar.getInstance();
        MarcasBp marcasBp = new MarcasBp(new PropertiesVO());
        SimpleDateFormat fullFechaFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        LinkedHashMap<String, MarcaVO> marcas = new LinkedHashMap<>();
            
        for( int i = 0; i <= _fechasCalculo.length - 1; i++)
        {
            boolean tieneTurnoRotativo  = false;
            boolean turnoNocturno       = false;
            boolean continuar=true;
            IT_FECHA = _fechasCalculo[i];
                       
            String strKey = _empleado.getEmpresaid() + "|" + _empleado.getRut() + "|" + IT_FECHA;
            InfoFeriadoVO infoFeriado = m_fechasCalendarioFeriados.get(strKey);
            
//////      comentado  InfoFeriadoVO infoFeriado = 
//////                m_feriadosBp.validaFeriado(_fechasCalculo[i], 
//////                    _empleado.getEmpresaid(), 
//////                    _empleado.getRut());
            boolean esFeriado = infoFeriado.isFeriado();
            
            ////boolean esFeriado = _hashFeriados.containsKey(_fechasCalculo[i]);
            
            StringTokenizer tokenfecha1=new StringTokenizer(IT_FECHA, "-");
            String strAnio  = tokenfecha1.nextToken();
            String strMes   = tokenfecha1.nextToken();
            String strDia   = tokenfecha1.nextToken();
            int codDia      = m_hashFechas.get(IT_FECHA);

            marcas = new LinkedHashMap<>();
            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]procesaAsistencia."
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
                + "CalculoAsistenciaNew]procesaAsistencia."
                + "get detalle turno rotativo laboral. "
                + "itemKey:" + itemKey
                + ", fecha:" + IT_FECHA);
                
            DetalleTurnoVO detalleTurnoRotLaboral   = m_listaAsignacionTurnosRotativos.get(IT_FECHA);
                
            //rescatar detalle turno del empleado
            LinkedHashMap<Integer,DetalleTurnoVO> auxListDetalleTurno
                = new LinkedHashMap<>();
            DetalleTurnoVO detalleturno = new DetalleTurnoVO();//
                
            if (detalleTurnoRotLaboral != null){
                tieneTurnoRotativo = true;
                detalleturno = detalleTurnoRotLaboral;
                System.out.println(WEB_NAME+"[GestionFemase."
                    + "CalculoAsistenciaNew]"
                    + "tiene turno rotativo, "
                    + "id_turno: " + detalleturno.getIdTurno()
                    + ", codDia: " + codDia    
                    + ", nombre_turno: " + detalleturno.getNombreTurno()    
                    + ", hora entrada: " + detalleturno.getHoraEntrada()
                    + ", hora salida: " + detalleturno.getHoraSalida()
                    + ", holgura: " + detalleturno.getHolgura()
                    + ", minsColacion: " + detalleturno.getMinutosColacion()
                    + ", turnoNocturno(S/N)?: " + detalleturno.getNocturno());
                if (detalleturno.getNocturno().compareTo("S") == 0) turnoNocturno = true;
            }else{
                auxListDetalleTurno = _listaDetalleTurnos.get(_empleado.getIdturno());
                if (detalleTurnoRotLaboral == null && auxListDetalleTurno == null){
                    System.out.println(WEB_NAME+"[GestionFemase."
                        + "CalculoAsistenciaNew]"
                        + "NO TIENE TURNO para la fecha: " + IT_FECHA);
                    continuar=false;
////                    if (!esFeriado){ 
////                        detalleCalculo.setObservacion("Libre");
////                    }else if (esFeriado) detalleCalculo.setObservacion("Feriado");
                }else if (auxListDetalleTurno!=null){
                        detalleturno = auxListDetalleTurno.get(codDia);
                        if (detalleturno != null){
                            System.out.println(WEB_NAME+"[GestionFemase."
                                + "CalculoAsistenciaNew]codDia: " + codDia);
                            System.out.println(WEB_NAME+"[GestionFemase."
                                + "CalculoAsistenciaNew]"
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
            }
            
            if (continuar && !tieneTurnoRotativo){
                //TIENE TURNO NORMAL
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]procesaAsistencia."
                    + "Itera fecha " + IT_FECHA 
                    + ", tiene Turno Normal. "
                    + "Set ambas marcas. "
                    + "Obtener lista de marcas desde buffer...");
                    
                marcas = m_listaMarcas.get(_empleado.getEmpresaid()
                    + "|" + _empleado.getRut() + "|" + _fechasCalculo[i]);
                    
                if (marcas == null) marcas = new LinkedHashMap<>();
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]procesaAsistencia."
                        + "Marcas size()= " + marcas.size());
            }else if (continuar){
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]procesaAsistencia."
                    + "Itera fecha " + IT_FECHA 
                    + ", tiene Turno Rotativo, buscar marca de entrada, "
                    + "sino tiene, buscar salida dia anterior. Add Marca");
                System.out.println(WEB_NAME+"[PASO 6]");
                    
                String jsonOutput = marcasBp.getMarcaByTipoJson(_empleado.getEmpresaid(), 
                    _empleado.getRut(), 
                    _fechasCalculo[i], _fechasCalculo[i],1,null,isHistorico);
                                       
                MarcaVO auxMarca = new Gson().fromJson(jsonOutput, MarcaVO.class);
                    
                if (auxMarca != null){
                    System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]"
                        + "procesaAsistencia.AddMarca entrada...");
                    marcas.put("1", auxMarca);
                }else{
                        System.out.println(WEB_NAME+"[GestionFemase."
                            + "CalculoAsistenciaNew]procesaAsistencia."
                            + "No hay marca de entrada "
                            + "para fecha " + IT_FECHA+", continuar...");
                        //////continuar=false;
                        marcas = new LinkedHashMap<>();
//                        System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]procesaAsistencia."
//                            + "Marcas size()= " + marcas.size());
                }
            }
                
            if (continuar){
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]"
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
                        int minutosColacion= 0;
                        if (detalleturno != null){
                            horasTeoricas = detalleturno.getTotalHoras();
                            minutosTeoricos = horasTeoricas * 60;
                            minutosColacion = detalleturno.getMinutosColacion();
                            System.out.println(WEB_NAME+"[GestionFemase."
                                + "CalculoAsistenciaNew]procesaAsistencia."
                                + "Fecha calculo: "+_fechasCalculo[i]
                                + ", turno.hora_entrada: " + detalleturno.getHoraEntrada()
                                + ", turno.hora_salida: " + detalleturno.getHoraSalida()
                                + ", turno.mins_colacion: " + minutosColacion    
                            );
                            
                            DiferenciaHorasVO duracionTurno = 
                                Utilidades.getTimeDifference(_fechasCalculo[i]+" " +detalleturno.getHoraEntrada(), _fechasCalculo[i]+" " +detalleturno.getHoraSalida());
                            hhmmTurno = duracionTurno.getStrDiferenciaHorasMinutos();
                        }

                        System.out.println(WEB_NAME+"[GestionFemase."
                            + "CalculoAsistenciaNew]procesaAsistencia."
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
//                            + "CalculoAsistenciaNew]procesaAsistencia. "
//                            + "Fecha calculo: "+_fechasCalculo[i]
//                            + ", codDia: "+codDia
//                            + ", rut: " + _empleado.getRut()
//                            + ", size marcas: " +marcas.size());
                        if (marcas!=null && !marcas.isEmpty()){
                            if (marcas.size() == 1){
                                System.out.println(WEB_NAME+"[GestionFemase."
                                    + "CalculoAsistenciaNew]"
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
                                            + "CalculoAsistenciaNew]"
                                            + "procesaAsistencia."
                                            + "Solo hay marca de ENTRADA(aquiii. auxAlert= "+auxAlert);
                                        System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]"
                                            + "procesaAsistencia. setFechaEntradaMarca: "+fechaMarca1);
                                        detalleCalculo.setFechaEntradaMarca(fechaMarca1);
                                        detalleCalculo.setHoraEntrada(horaMarcaFull);//fecha hora entrada full
                                        System.out.println(WEB_NAME+"[GestionFemase."
                                            + "CalculoAsistenciaNew]"
                                            + "procesaAsistencia."
                                            + "Set observacion[44]: " + auxAlert
                                            + ", tieneTurnoRotativo: " + tieneTurnoRotativo);
                                        detalleCalculo.setObservacion(auxAlert);
                                        if (tieneTurnoRotativo){
                                            System.out.println(WEB_NAME+"[GestionFemase."
                                                + "CalculoAsistenciaNew]procesaAsistencia."
                                                + "Buscar marca de salida(turno normal o rotativo)");
                                            MarcaVO auxMarca = getMarcaTurnoRotativo(marcasBp, _empleado, _fechasCalculo[i], horaMarcaFull, tipoMarca1);
                                            if (auxMarca != null){
                                                marcas.put("2", auxMarca);
                                            }
                                        }
                                    }else {
                                        labelAlert += "Salida";
                                        auxAlert += "Salida";
                                        System.out.println(WEB_NAME+"[GestionFemase."
                                            + "CalculoAsistenciaNew]procesaAsistencia."
                                            + "Solo hay marca de SALIDA. auxAlert= "+auxAlert);
                                        detalleCalculo.setFechaEntradaMarca(_fechasCalculo[i]);
                                        detalleCalculo.setFechaSalidaMarca(fechaMarca1);
                                        detalleCalculo.setHoraSalida(horaMarcaFull);//fecha hora salida full
                                        if (tieneTurnoRotativo){
                                            System.out.println(WEB_NAME+"[GestionFemase."
                                                + "CalculoAsistenciaNew]"
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
                                        + "CalculoAsistenciaNew]"
                                        + "procesaAsistencia.- "
                                       + "empresa:" + _empleado.getEmpresaid()
                                       + ", rut:" + _empleado.getRut()
                                       + ", fecha: " + _fechasCalculo[i] 
                                       + ". Alerta: " + labelAlert
                                       + ". observacion: " + auxAlert);
                                    
                                    ArrayList<DetalleAusenciaJsonVO> detalleausencia = m_listaAusencias.get(_empleado.getRut()+"|"+_fechasCalculo[i]);
                                    System.out.println(WEB_NAME+"[GestionFemase."
                                        + "CalculoAsistenciaNew]"
                                        + "procesaAsistencia.NO TIENE MARCAS, "
                                        + "Set observacion[4]: " + auxAlert);
                                    detalleCalculo.setObservacion(auxAlert);
                                    detalleCalculo.setFechaCalculo(_fechasCalculo[i]);
                                    detalleCalculo.setHorasTeoricas(horasTeoricas);
                                    detalleCalculo.setArt22(_empleado.isArticulo22());
                                    if (detalleturno != null){
                                        System.out.println(WEB_NAME+"[GestionFemase."
                                            + "CalculoAsistenciaNew]"
                                            + "procesaAsistencia."
                                            + "detalleturno.getHoraEntrada(): "+detalleturno.getHoraEntrada()
                                            + "detalleturno.getHoraSalida(): "+detalleturno.getHoraSalida());
                                        detalleCalculo.setHoraEntradaTeorica(detalleturno.getHoraEntrada());
                                        detalleCalculo.setHoraSalidaTeorica(detalleturno.getHoraSalida());
                                        detalleCalculo.setHolguraMinutos(detalleturno.getHolgura());
                                    }else{
                                        String aux11 = detalleCalculo.getObservacion();
                                        System.out.println(WEB_NAME+"[GestionFemase."
                                            + "CalculoAsistenciaNew]"
                                            + "procesaAsistencia.NO TIENE MARCAS, "
                                            + "Set observacion[3]: " + auxAlert);
                                        //detalleCalculo.setObservacion("Sin turno - " + auxAlert);
                                        System.out.println(WEB_NAME+"[GestionFemase."
                                            + "CalculoAsistenciaNew]"
                                            + "procesaAsistencia] -1- Set Sin turno (Libre)");
                                        detalleCalculo.setObservacion("Libre");
                                        detalleCalculo.setHoraSalida("00:00:00");
                                    }
                                    detalleCalculo.setEsFeriado(esFeriado);
                                    //iterando ausencias
                                    if (detalleausencia != null 
                                            && detalleausencia.size() > 0)
                                    {
                                        System.out.println(WEB_NAME+"[GestionFemase."
                                            + "CalculoAsistenciaNew]"
                                            + "procesaAsistencia."
                                            + "[A]Iterando ausencias...");
                                        String auxMsgAusencias = "";
                                        ArrayList<String> listaHrsAusencias = new ArrayList<>();
                                        Iterator<DetalleAusenciaJsonVO> ausenciasIterator = detalleausencia.iterator();
                                        while (ausenciasIterator.hasNext()) {
                                            DetalleAusenciaJsonVO ausencia = ausenciasIterator.next();
                                            boolean isFound = auxMsgAusencias.contains(ausencia.getNombreausencia());
                                            if (!isFound) auxMsgAusencias += ausencia.getNombreausencia();
                                            System.out.println(WEB_NAME+"[ausenciaMsg]"
                                                + "isFound?" + isFound
                                                + ", 1- " + auxMsgAusencias);
                                            if (ausencia.getPermitehora().compareTo("S") == 0){
                                                auxMsgAusencias += ausencia.getNombreausencia()
                                                    +":" + ausencia.getHorainiciofullasstr().substring(0, ausencia.getHorainiciofullasstr().length()-3) 
                                                    +"-" + ausencia.getHorafinfullasstr().substring(0, ausencia.getHorafinfullasstr().length()-3)+" hrs, ";
                                                //restar hrs con fecha
                                                DiferenciaHorasVO difAusencia = Utilidades.getTimeDifference(_fechasCalculo[i] + " " + ausencia.getHorainiciofullasstr() +":00", 
                                                    _fechasCalculo[i] + " " + ausencia.getHorafinfullasstr() + ":00");
                                                if (ausencia.getJustificahoras().compareTo("S") == 0){
                                                    listaHrsAusencias.add(difAusencia.getStrDiferenciaHorasMinutos());
                                                }
                                            }else{
                                                isFound = auxMsgAusencias.contains(ausencia.getNombreausencia());
                                                if (!isFound) auxMsgAusencias += ausencia.getNombreausencia();
                                                if (hhmmTurno != null && hhmmTurno.compareTo("") != 0){
                                                    if (ausencia.getJustificahoras().compareTo("S") == 0){
                                                        listaHrsAusencias.add(hhmmTurno);
                                                    }
                                                }
                                                //hrs justificadas = suma de hrs de ausencia...
                                            }
                                            System.out.println(WEB_NAME+"[ausenciaMsg]2- "+auxMsgAusencias);
                                        }
                                        System.out.println(WEB_NAME+"[ausenciaMsg]3- "+auxMsgAusencias);
                                        System.out.println(WEB_NAME+"[GestionFemase."
                                            + "CalculoAsistenciaNew]"
                                            + "procesaAsistencia."
                                            + "NO TIENE MARCAS, "
                                            + "Set observacion[2]: " + auxMsgAusencias);
                                        detalleCalculo.setObservacion(auxMsgAusencias);
                                        String sumaJustificadas = Utilidades.sumTimesList(listaHrsAusencias);
                                        detalleCalculo.setHhmmJustificadas(sumaJustificadas);

                                    }
                                   
                                    System.out.println(WEB_NAME+"[GestionFemase."
                                        + "CalculoAsistenciaNew]"
                                        + "procesaAsistencia."
                                        + "**1** Put detalleCalculo."
                                         + " Rut="+_empleado.getRut()
                                         + ",fecha entrada="+detalleCalculo.getFechaEntradaMarca()
                                         +", hora Entrada="+detalleCalculo.getHoraEntrada());
                                   listaAux.add(detalleCalculo);

                                }
                            }
                            System.out.println(WEB_NAME+"[GestionFemase."
                                + "CalculoAsistenciaNew]"
                                + "procesaAsistencia. "
                                + "marcas.size= " + marcas.size());
                            if (marcas.size() > 1){
                                //ordenar par de marcas...
                                MarcaVO marcaEntrada = marcas.get("1");
                                MarcaVO marcaSalida = marcas.get("2");
                                System.out.println(WEB_NAME+"[GestionFemase."
                                    + "CalculoAsistenciaNew]"
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
                                        _hashTiposHE, 
                                        tieneTurnoRotativo, 
                                        turnoNocturno);
                                
                                listaAux.add(auxDetalleCalculo);
                                
                                System.out.println(WEB_NAME+"[GestionFemase."
                                    + "CalculoAsistenciaNew]"
                                    + "procesaAsistencia.**ZZ** "
                                    + "put Key= " + _empleado.getRut() 
                                    + "|" + auxDetalleCalculo.getFechaEntradaMarca()+ 
                                    "|"+ auxDetalleCalculo.getHoraEntrada()
                                    +", observacion: "+auxDetalleCalculo.getObservacion());
                            }
                            
                            Iterator<DetalleAsistenciaVO> itDetails11 = listaAux.iterator();
                            while (itDetails11.hasNext())//while 9
                            {   
                                DetalleAsistenciaVO calculosFecha22 = itDetails11.next();
                                System.out.println(WEB_NAME+"[GestionFemase."
                                    + "CalculoAsistenciaNew]"
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
                                    + "CalculoAsistenciaNew]"
                                    + "procesaAsistencia. "
                                    + "empresa:"+ _empleado.getEmpresaid()
                                    + ", rut:"+ _empleado.getRut()
                                    + ", fecha: " + _fechasCalculo[i] 
                                    + " NO TIENE MARCAS, rescatar ausencias...");
                                ArrayList<DetalleAusenciaJsonVO> detalleausencia = m_listaAusencias.get(_empleado.getRut()+"|"+_fechasCalculo[i]);
                                DetalleAsistenciaVO detalleCalculo = new DetalleAsistenciaVO();
                                detalleCalculo.setFechaHoraCalculo(fullFechaFmt.format(mycal.getTime()));
                                detalleCalculo.setEmpresaId(_empleado.getEmpresaid());
                                detalleCalculo.setDeptoId(_empleado.getDeptoid());
                                detalleCalculo.setCencoId(_empleado.getCencoid());
                                detalleCalculo.setRutEmpleado(_empleado.getRut());
                                detalleCalculo.setFechaCalculo(_fechasCalculo[i]);
                                detalleCalculo.setHorasTeoricas(horasTeoricas);
                                detalleCalculo.setArt22(_empleado.isArticulo22());
                               
                                if (detalleturno != null){
                                   System.out.println(WEB_NAME+"[GestionFemase."
                                        + "CalculoAsistenciaNew]"
                                        + "procesaAsistencia. Tiene Turno."
                                        + "-0- fechaHra1: "+_fechasCalculo[i]+" " +detalleturno.getHoraEntrada()
                                        + ", fechaHra2: "+_fechasCalculo[i]+" " +detalleturno.getHoraSalida());
                                   DiferenciaHorasVO duracionTurno = 
                                        Utilidades.getTimeDifference(_fechasCalculo[i]+" " +detalleturno.getHoraEntrada(), _fechasCalculo[i]+" " +detalleturno.getHoraSalida());
                                   hhmmTurno = duracionTurno.getStrDiferenciaHorasMinutos();
                                   System.out.println(WEB_NAME+"[GestionFemase."
                                        + "CalculoAsistenciaNew]"
                                        + "procesaAsistencia.-2-"
                                        + " detalleturno.getHoraEntrada(): " + detalleturno.getHoraEntrada()
                                        + ", detalleturno.getHoraSalida(): " + detalleturno.getHoraSalida()
                                        + ", detalleturno.getMinutosColacion(): " + detalleturno.getMinutosColacion());
                                   detalleCalculo.setHoraEntradaTeorica(detalleturno.getHoraEntrada());
                                   detalleCalculo.setHoraSalidaTeorica(detalleturno.getHoraSalida());
                                   detalleCalculo.setHolguraMinutos(detalleturno.getHolgura());
                                   System.out.println(WEB_NAME+"[GestionFemase."
                                        + "CalculoAsistenciaNew]"
                                        + "procesaAsistencia.NO TIENE MARCAS "
                                        + "hhmmTurno:"+ hhmmTurno);
                                }
                                detalleCalculo.setEsFeriado(esFeriado);
                                if (detalleausencia != null && detalleausencia.size() > 0){
                                    System.out.println(WEB_NAME+"[GestionFemase."
                                        + "CalculoAsistenciaNew]"
                                        + "procesaAsistencia.[B]Iterando ausencias...");
                                    String auxMsgAusencias = "";
                                    ArrayList<String> listaHrsAusencias     = new ArrayList<>();
                                    ArrayList<String> listaHrsAusenciasPagadasPorEmpleador     = new ArrayList<>();
                                    ArrayList<String> listaHrsNoRemuneradas = new ArrayList<>();
                                    //iterando ausencias
                                    Iterator<DetalleAusenciaJsonVO> ausenciasIterator = detalleausencia.iterator();
                                    while (ausenciasIterator.hasNext()) {
                                        DetalleAusenciaJsonVO ausencia = ausenciasIterator.next();
                                        //auxMsgAusencias += ausencia.getNombreAusencia();
                                        if (ausencia.getPermitehora().compareTo("S")==0){
                                            auxMsgAusencias += ausencia.getNombreausencia()
                                                +":" + ausencia.getHorainiciofullasstr().substring(0, ausencia.getHorainiciofullasstr().length()-3) 
                                                +"-" + ausencia.getHorafinfullasstr().substring(0, ausencia.getHorafinfullasstr().length()-3)+" hrs, ";
                                            System.out.println(WEB_NAME+"[GestionFemase."
                                                + "CalculoAsistenciaNew]"
                                                + "procesaAsistencia. "
                                                + "Ausencia x horas, msgAusencias: " + auxMsgAusencias);
                                            //restar hrs con fecha
                                            DiferenciaHorasVO difAusencia = Utilidades.getTimeDifference(_fechasCalculo[i] + " " + ausencia.getHorainiciofullasstr() +":00", 
                                                _fechasCalculo[i] + " " + ausencia.getHorafinfullasstr()+":00");
                                            if (ausencia.getTipoausencia() == 1){
                                                if (ausencia.getJustificahoras().compareTo("S") == 0){
                                                    listaHrsAusencias.add(difAusencia.getStrDiferenciaHorasMinutos());
                                                }
                                                if (ausencia.getPagadaporempleador().compareTo("S") == 0){
                                                    listaHrsAusenciasPagadasPorEmpleador.add(difAusencia.getStrDiferenciaHorasMinutos());
                                                }
                                            }else{
                                                listaHrsNoRemuneradas.add(difAusencia.getStrDiferenciaHorasMinutos());
                                            }
                                        }else{
                                            auxMsgAusencias += ausencia.getNombreausencia();
                                            System.out.println(WEB_NAME+"[GestionFemase."
                                                + "CalculoAsistenciaNew]"
                                                + "procesaAsistencia. "
                                                + "Ausencia x dias, "
                                                + "msgAusencias: " + auxMsgAusencias
                                                + ", tipoAusencia: " + ausencia.getTipoausencia()
                                                + ", justificaHoras: " + ausencia.getJustificahoras()
                                                + ", pagadaPorEmpleador: " + ausencia.getPagadaporempleador());
                                            if (hhmmTurno != null && hhmmTurno.compareTo("") != 0){
                                                String aux2 = hhmmTurno;
                                                if (ausencia.getTipoausencia() == 1){
                                                    if (ausencia.getJustificahoras().compareTo("S") == 0){
                                                        listaHrsAusencias.add(aux2);
                                                    }
                                                    if (ausencia.getPagadaporempleador().compareTo("S") == 0){
                                                        listaHrsAusenciasPagadasPorEmpleador.add(aux2);
                                                    }
                                                }else{
                                                    if (minutosColacion > 0) aux2 = Utilidades.restarMinsHora(hhmmTurno, minutosColacion);
                                                    listaHrsNoRemuneradas.add(aux2);
                                                }
                                            }else{
                                                System.out.println(WEB_NAME+"[GestionFemase."
                                                    + "CalculoAsistenciaNew]procesaAsistencia. No tiene turno");
                                            }
                                            //hrs justificadas = suma de hrs de ausencia...
                                        }
                                    }
                                    System.out.println(WEB_NAME+"[GestionFemase."
                                        + "CalculoAsistenciaNew]"
                                        + "Es Feriado? " + detalleCalculo.isEsFeriado()
                                        + ", hhmmTurno: " + hhmmTurno);
                                    if (hhmmTurno != null && hhmmTurno.compareTo("") != 0){
                                        System.out.println(WEB_NAME+"[GestionFemase."
                                            + "CalculoAsistenciaNew]Tiene "
                                            + "turno para la fecha: "+IT_FECHA);
                                        System.out.println(WEB_NAME+"[GestionFemase."
                                            + "CalculoAsistenciaNew]"
                                            + "procesaAsistencia.NO TIENE MARCAS, pero tiene ausencia, "
                                            + "Set observacion[12]: " + auxMsgAusencias
                                            + ", hrs justificadas: " + Utilidades.sumTimesList(listaHrsAusencias)
                                            + ", hrs no remuneradas: " + Utilidades.sumTimesList(listaHrsNoRemuneradas));
                                        detalleCalculo.setObservacion(auxMsgAusencias);
                                        //if (esFeriado){
                                        //    detalleCalculo.setHhmmJustificadas("");
                                        //}else{
                                            System.out.println(WEB_NAME+"[GestionFemase."
                                                + "CalculoAsistenciaNew]PASO 10...");
                                            int minsColacion = detalleturno.getMinutosColacion();
                                            detalleCalculo.setHhmmJustificadas(Utilidades.sumTimesList(listaHrsAusencias));
                                            String hrsTotalesMenosColacion = Utilidades.restarMinsHora(hhmmTurno, minsColacion);
                                            
                                            System.out.println(WEB_NAME+"[GestionFemase."
                                                + "CalculoAsistenciaNew]"
                                                + "Set hrs ausencia final. "
                                                + "hrs justificadas= " + detalleCalculo.getHhmmJustificadas()
                                                + ", minsColacion= " + minsColacion
                                                + ", hrsJustificadasMenosColacion= " + hrsTotalesMenosColacion);
                                            detalleCalculo.setHrsAusencia(Utilidades.sumTimesList(listaHrsNoRemuneradas)); 
                                            //detalleCalculo.setHhmmJustificadas(detalleCalculo.getHhmmJustificadas());
                                            //aqui
                                            if (!listaHrsAusenciasPagadasPorEmpleador.isEmpty()){
                                                System.out.println(WEB_NAME+"[GestionFemase. "
                                                    + "CalculoAsistenciaNew]"
                                                    + "ausencia tipo "+auxMsgAusencias+" es pagada por el empleador."
                                                    + "Set hrs trabajadas(1) y verificar feriado");
                                                if (esFeriado) detalleCalculo.setObservacion("Feriado");
                                                detalleCalculo.setHrsTrabajadas(hrsTotalesMenosColacion);
                                            }
                                        //}
                                    }else {
                                        /**
                                        *   No tiene turno. 
                                        *   Validar el tipo de ausencia
                                        *   Si es Licencia medica, debe mostrar Observacion='Licencia Medica'
                                        *   y hrs justificadas=0
                                        */
                                        
                                        if (auxMsgAusencias.toUpperCase().startsWith("LICENCIA")){
                                            detalleCalculo.setObservacion(auxMsgAusencias);
                                            //detalleCalculo.setHhmmJustificadas(Utilidades.sumTimesList(listaHrsAusencias));
                                            //detalleCalculo.setHrsAusencia(Utilidades.sumTimesList(listaHrsNoRemuneradas)); 
                                        }else{
                                            String observacion1 = "";
                                            if (detalleturno != null){
                                                observacion1 = getObservacion(IT_FECHA,
                                                    esFeriado, hhmmTurno, false, detalleturno.getCodDia());
                                            }else if (!esFeriado){ 
                                                observacion1 = "Libre";
                                            }else if (esFeriado) observacion1 = "Feriado";
                                            System.out.println(WEB_NAME+"[GestionFemase."
                                                + "CalculoAsistenciaNew]"
                                                + "Fecha: " + IT_FECHA
                                                + ", esFeriado?: " + esFeriado
                                                + ", hhmmTurno: " + hhmmTurno    
                                                +", observacion: " + observacion1);
                                        
                                            detalleCalculo.setObservacion(observacion1);
                                            detalleCalculo.setHhmmJustificadas("");
                                            detalleCalculo.setHrsAusencia("");
                                        }
                                         
                                    }
                                    
//                                    detalleCalculo.setObservacion(auxMsgAusencias);
//                                    detalleCalculo.setHhmmJustificadas(Utilidades.sumTimesList(listaHrsAusencias));
//                                    detalleCalculo.setHrsAusencia(Utilidades.sumTimesList(listaHrsNoRemuneradas));                                    
//                                    if (hhmmTurno != null && hhmmTurno.compareTo("") != 0){
//                                        System.out.println(WEB_NAME+"[DetalleAsistenciaController.procesaAsistencia]NO TIENE MARCAS "
//                                            + "Set hrs ausencia con hrsTurno: "+ hhmmTurno);
//                                        detalleCalculo.setHhmmJustificadas(hhmmTurno);
//                                    }
                                    
                                }else{
                                    System.out.println(WEB_NAME+"[GestionFemase."
                                        + "CalculoAsistenciaNew]"
                                        + "procesaAsistencia."
                                        + "empresa:"+ _empleado.getEmpresaid()
                                        + ", rut:"+ _empleado.getRut()
                                        + ", fecha: " + _fechasCalculo[i] 
                                        + "- No tiene ausencias. "
                                        + "HH:MM turno: " + hhmmTurno);
                                    //No tiene marcas ni ausencia...
                                    if (hhmmTurno != null && hhmmTurno.compareTo("") != 0){
                                        System.out.println(WEB_NAME+"[GestionFemase."
                                            + "CalculoAsistenciaNew]"
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
                                            
                                            int minsColacion = detalleturno.getMinutosColacion();
                                            System.out.println(WEB_NAME+"[GestionFemase."
                                                + "CalculoAsistenciaNew]"
                                                + "getInfoAsistencia. "
                                                + "Minutos de colacion: " + minsColacion
                                                + ",hhmmTurno: " + hhmmTurno);
                                            String aux = hhmmTurno;
                                            if (minsColacion > 0) aux = Utilidades.restarMinsHora(hhmmTurno, minsColacion);
                                            //hhmmPresencial = aux;
                                            System.out.println(WEB_NAME+"[GestionFemase."
                                                + "CalculoAsistenciaNew]"
                                                + "getInfoAsistencia. "
                                                + "Set hrs ausencia con (Hrs turno - mins colacion): " + aux);
                                            
                                            detalleCalculo.setHrsAusencia(aux);
                                        }
                                        System.out.println(WEB_NAME+"[GestionFemase."
                                            + "CalculoAsistenciaNew]"
                                            + "procesaAsistencia.NO TIENE MARCAS, "
                                            + "Set observacion[11]. hh:mm turno: "+hhmmTurno);
                                        detalleCalculo.setObservacion(getObservacion(_fechasCalculo[i],esFeriado, hhmmTurno, false, detalleturno.getCodDia()));
                                        System.out.println(WEB_NAME+"[GestionFemase."
                                            + "CalculoAsistenciaNew]"
                                            + "procesaAsistencia. "
                                            + " -1- getObservacion: "+detalleCalculo.getObservacion());
                                    }
                                }

                                if (detalleCalculo.getFechaEntradaMarca() != null){
                                    System.out.println(WEB_NAME+"[GestionFemase."
                                        + "CalculoAsistenciaNew]"
                                        + "procesaAsistencia.**2** put detalleCalculo."
                                        + " Rut="+_empleado.getRut()
                                        + ",fecha entrada="+detalleCalculo.getFechaEntradaMarca()
                                        +", hora Entrada="+detalleCalculo.getHoraEntrada());
                                }else{
                                    detalleCalculo.setFechaCalculo(_fechasCalculo[i]);
                                    detalleCalculo.setFechaEntradaMarca(_fechasCalculo[i]);
                                    System.out.println(WEB_NAME+"[GestionFemase."
                                        + "CalculoAsistenciaNew]"
                                        + "procesaAsistencia.NO TIENE MARCAS, "
                                        + "Set observacion[9], "
                                        + "hh:mm turno: "+hhmmTurno
                                        +", detalleausencia: "+detalleausencia
                                        + ", esFeriado?: " + esFeriado);
                                    if (detalleausencia == null || detalleausencia.isEmpty()){
                                        System.out.println(WEB_NAME+"[GestionFemase."
                                            + "CalculoAsistenciaNew."
                                            + "procesaAsistencia]Aqui 1, "
                                            + "esFeriado?: " + esFeriado);
                                        
                                        System.out.println(WEB_NAME+"[GestionFemase."
                                            + "CalculoAsistenciaNew]"
                                            + "procesaAsistencia. No tiene marcas. "
                                            + " -2- getObservacion: "+detalleCalculo.getObservacion()
                                            + ", _fecha: " + _fechasCalculo[i]
                                            + ", esFeriado?" + esFeriado);
                                        if (detalleturno != null){
                                            detalleCalculo.setObservacion(getObservacion(_fechasCalculo[i], esFeriado, hhmmTurno, false, detalleturno.getCodDia()));
                                        }else if (!esFeriado){ 
                                            detalleCalculo.setObservacion("Libre");
                                        }else if (esFeriado) detalleCalculo.setObservacion("Feriado");
                                        
                                        
//                                        if (esFeriado && (hhmmTurno != null && hhmmTurno.compareTo("")!=0)){
//                                            System.err.println("[GestionFemase."
//                                                + "CalculoAsistenciaNew]"
//                                                + "procesaAsistencia."
//                                                + "Set Obs = feriado");
//                                            detalleCalculo.setObservacion("Feriado");
//                                        }else if (esFeriado && (hhmmTurno != null && hhmmTurno.compareTo("") == 0)){
//                                            System.err.println("[GestionFemase."
//                                                + "CalculoAsistenciaNew]"
//                                                + "procesaAsistencia."
//                                                + "Set Obs = Libre");
//                                            detalleCalculo.setObservacion("Libre");
//                                        }else if (!esFeriado && hhmmTurno.compareTo("") != 0){
//                                                    System.err.println("[GestionFemase."
//                                                        + "CalculoAsistenciaNew]"
//                                                        + "procesaAsistencia."
//                                                        + "Set Obs = Sin marcas");
//                                                    detalleCalculo.setObservacion("Sin marcas");
//                                        }
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
                + "CalculoAsistenciaNew]"
                + "procesaAsistencia."
                + "**ANTES DE SALIR** "
                + "detalleCalculo."
                + " Rut= " + _empleado.getRut()
                + ",fecha entrada= " + calculosFecha22.getFechaEntradaMarca()
                + ", hora Entrada= " + calculosFecha22.getHoraEntrada()
                + ", hrsJustificadas= " + calculosFecha22.getHhmmJustificadas()
                + ", hrsPresenciales= " + calculosFecha22.getHrsPresenciales()
                + ", fechaEntradaMarca= " + calculosFecha22.getFechaEntradaMarca()
                + ", getHrsAusencia= " + calculosFecha22.getHrsAusencia());
            
        }
           
        return listaAux;
    }
    
    /**
    * Retorna un valor para setear el campo Observacion de la
    * tabla 'detalle_asistencia'
    */
    private String getObservacion(String _fecha, 
            boolean _esFeriado, 
            String _hhmmTurno, 
            boolean _tieneMarcas, int _codDia){
        String observacion="Libre";
//        observacion="Feriado";
//        
//        if (_hhmmTurno.compareTo("") != 0){// no tiene marcas y tiene turno
//            if (!_esFeriado) observacion="Sin marcas";
//        }else if (_hhmmTurno.compareTo("") == 0){//no tiene turno
//            observacion="Libre";
//        }
        //if (_esFeriado && _hhmmTurno.compareTo("") != 0) observacion = "Feriado";
        //else 
        if (_hhmmTurno.compareTo("") != 0){// no tiene marcas y tiene turno
            observacion="Sin marcas";
        }
        
        if (observacion.compareTo("Sin marcas") == 0 && _esFeriado && _codDia != 6) observacion = "Feriado";
        
        /*else if (_hhmmTurno.compareTo("") == 0){//no tiene turno
            observacion="Libre";
        }*/

                
        System.out.println(WEB_NAME+"[CalculoAsistenciaNew.getObservacion]"
            + " Fecha: " + _fecha
                + ", codDia? " + _codDia
            + ", esFeriado? " + _esFeriado
            + ", tieneMarcas? " + _tieneMarcas
            + ", hhmmTurno: " + _hhmmTurno
            + ", observacion: " + observacion);
        
        return observacion;
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
        System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getMarcaTurnoRotativo. "
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
            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getMarcaTurnoRotativo."
                + "horaEntrada: " 
                + _horaMarca
                +", diferencia en horas con las 23:59:59= " 
                + diferenciaET.getIntDiferenciaHoras());
            if (diferenciaET.getIntDiferenciaHoras() <= 4){
                //fecha salida = dia siguiente (turno noche)
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getMarcaTurnoRotativo."
                    + " Fecha salida = dia siguiente (turno noche)");
//                marca = _marcasBp.getMarcaByTipo(_empleado.getEmpresa().getId(), 
//                    _empleado.getRut(), 
//                    strDiaSiguiente, strDiaSiguiente,2, _fechaMarca + " " + _horaMarca);
                System.out.println(WEB_NAME+"[PASO 7]");
                String jsonOutput = _marcasBp.getMarcaByTipoJson(_empleado.getEmpresaid(), 
                    _empleado.getRut(), 
                    strDiaSiguiente, strDiaSiguiente,2, _fechaMarca + " " + _horaMarca, isHistorico);
                marca = new Gson().fromJson(jsonOutput, MarcaVO.class);
                
            }else{
                //fecha salida = misma fecha de la entrada (turno normal)
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getMarcaTurnoRotativo."
                    + " Fecha salida = misma fecha de la entrada (turno normal)");
//                marca = _marcasBp.getMarcaByTipo(_empleado.getEmpresa().getId(), 
//                    _empleado.getRut(), 
//                    fechaFmt.format(dteFechamarca), fechaFmt.format(dteFechamarca),2,_fechaMarca + " " + _horaMarca);
                System.out.println(WEB_NAME+"[PASO 8]");
                String jsonOutput = _marcasBp.getMarcaByTipoJson(_empleado.getEmpresaid(), 
                    _empleado.getRut(), 
                    fechaFmt.format(dteFechamarca), fechaFmt.format(dteFechamarca),2,_fechaMarca + " " + _horaMarca, isHistorico);
                marca = new Gson().fromJson(jsonOutput, MarcaVO.class);
                
                if (marca == null){
                    System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getMarcaTurnoRotativo."
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
                    
                    System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]"
                        + "getMarcasJson result. jsonOutput2: " + jsonOutput2);
                    
                    //////old Type listType = new TypeToken<LinkedHashMap<String, MarcaVO>>() {}.getType();
                    //////old LinkedHashMap<String, MarcaVO> marcasDiaSgte = new Gson().fromJson(jsonOutput2, listType);
                    
                    Type collectionType = new TypeToken<ArrayList<MarcaJsonVO>>(){}.getType();
                    ArrayList<MarcaJsonVO> marcasDiaSgte = new Gson().fromJson(jsonOutput2, collectionType);
                    
                    ///Type collectionType = new TypeToken<Collection<ChannelSearchEnum>>(){}.getType();
                    ///Collection<ChannelSearchEnum> enums = gson.fromJson(yourJson, collectionType);
                    
                    
                    //*************
////                    EmpleadoVO infoEmpleado;
////                    try {
////                        infoEmpleado = (EmpleadoVO)new Gson().fromJson(jsonOutput, EmpleadoVO.class);
////                    }
////                    catch(Exception ex){
////                        System.err.println("[parseo empleadoVO]error: "+ex.toString());
////                        Type clazzListType = new TypeToken<EmpleadoVO>() {}.getType();
////                        infoEmpleado = new Gson().fromJson(jsonOutput, clazzListType);
////                        System.err.println("[parseo empleadoVO]1-rut: "+infoEmpleado.getRut());
////                    }
                    
                    //**********
                    
                    if (marcasDiaSgte!=null && !marcasDiaSgte.isEmpty()){
                        boolean entrada = false;
                        boolean salida  = false;
                        
                        //Set entrySet    = marcasDiaSgte.entrySet();
                        Iterator<MarcaJsonVO> it = marcasDiaSgte.iterator();
                        while(it.hasNext()){
                            //Map.Entry item = (Map.Entry) it.next();
                            MarcaJsonVO auxmarca = it.next();
                            //MarcaJsonVO auxmarca = item.getValue();
                            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]"
                                + "fechaHora = " + auxmarca.getFechahora()
                                + "tipoMarca = " + auxmarca.getTipomarca());
                            if (auxmarca.getTipomarca() == Constantes.MARCA_ENTRADA){
                                entrada = true;
                            }else if (auxmarca.getTipomarca() == Constantes.MARCA_SALIDA){
                                    salida = true;
                            } 
                        }
                        if (entrada && salida) ambasMarcas = true;
                    }
                    if (!ambasMarcas){
//                        marca = _marcasBp.getMarcaByTipo(_empleado.getEmpresa().getId(), 
//                            _empleado.getRut(), 
//                            strDiaSiguiente, strDiaSiguiente,2, _fechaMarca + " " + _horaMarca);
                        System.out.println(WEB_NAME+"[PASO 9]solo tiene una marca");
                        String jsonOutput3 = _marcasBp.getMarcaByTipoJson(_empleado.getEmpresaid(), 
                            _empleado.getRut(), 
                            strDiaSiguiente, strDiaSiguiente,2, _fechaMarca + " " + _horaMarca, isHistorico);
                        System.out.println(WEB_NAME+"[CalculoAsistenciaNew]"
                            + "jsonOutput3: "+jsonOutput3);
                        marca = new Gson().fromJson(jsonOutput3, MarcaVO.class);
                        if (marca!=null){ 
                            System.out.println(WEB_NAME+"[CalculoAsistenciaNew]PASO 99."
                                + "marca,toString: " + marca.toString());
                        }
                    }
                }
            }
            
        }else if (m_marcasProcesadas.get(keyMarca)== null){//si la hora marca no esta procesada
                // ------- buscar marca de entrada el dia anterior -------------
                System.out.println(WEB_NAME+"[GestionFemase."
                    + "CalculoAsistenciaNew]getMarcaTurnoRotativo."
                    + "-->Buscar marca de entrada el dia anterior");
//                marca = _marcasBp.getMarcaByTipo(_empleado.getEmpresa().getId(), 
//                    _empleado.getRut(), 
//                    strDiaAnterior, strDiaAnterior,1,null);
                System.out.println(WEB_NAME+"[PASO 10]");
                String jsonOutput3 = _marcasBp.getMarcaByTipoJson(_empleado.getEmpresaId(), 
                    _empleado.getRut(), 
                    strDiaAnterior, strDiaAnterior,1,null, isHistorico);
                marca = new Gson().fromJson(jsonOutput3, MarcaVO.class);
                
        }
        if (marca!=null){
            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getMarcaTurnoRotativo."
                + "Agregar marca fecha-hora: "+marca.getFechaHora());
        }
        return marca;
    }
    
    /**
    * 
    * 
    */
    private DetalleAsistenciaVO getInfoAsistencia(EmpleadoVO _empleado, 
            LinkedHashMap<String, MarcaVO> _listaMarcas, 
            DetalleTurnoVO _detalleturno,
            int _horasTeoricas,
            int _minutosTeoricos,
            DetalleTurnoVO _detalleTurnoRotLibre,
            HashMap<String,Integer> _hashTiposHE, 
            boolean _tieneTurnoRotativo,
            boolean _turnoNocturno){
            
        //Tiene las dos marcas en la misma fecha
        DetalleAsistenciaVO auxDetalleCalculoReturn=new DetalleAsistenciaVO();
        int tipoMarca;
        String fechaHoraMarca;
        String fechaMarca;
        String horaMarca;
        String comentarioMarca      = "";
        String horaMarcaFull;
        String fechaEntrada         = "";
        String fechaSalida          = "";
        int minsColacion            = 0;
        String horaEntrada          = "";
        String horaSalida           = "";
        String comentarioEntrada    = "";
        String comentarioSalida     = "";
        String horaEntradaFull      = "";
        String horaSalidaFull       = "";
        System.out.println(WEB_NAME+"[GestionFemase."
            + "CalculoAsistenciaNew]"
            + "getInfoAsistencia(). "
            + "Ordenando marcas. "
            + "Tiene turno Rotativo?" + _tieneTurnoRotativo
            + "Tiene turno Nocturno?" + _turnoNocturno);
        
        if (_detalleturno != null){        
            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]"
                + "getInfoAsistencia. "
                + " turno.horaEntrada= " + _detalleturno.getHoraEntrada()
                + ",turno.horaSalida= " + _detalleturno.getHoraSalida()); 
        }else{
            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]"
                + "getInfoAsistencia. No tiene turno asignado para la fecha");
        
        }
        LinkedHashMap<String, DetalleMarcaVO> marcasOrdenadas 
            = new LinkedHashMap<>();
        SimpleDateFormat soloFechaFmt = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat fullFechaFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar mycal = Calendar.getInstance();
        
        //iterando marcas
        Iterator<MarcaVO> collMarcas = _listaMarcas.values().iterator();
        while(collMarcas.hasNext()){//while 7
            MarcaVO currentMarca = collMarcas.next();
            
            tipoMarca = currentMarca.getTipoMarca();
            fechaHoraMarca = currentMarca.getFechaHora();
            comentarioMarca = currentMarca.getComentario();
            //2017-06-01 09:00:01
            System.out.println(WEB_NAME+"[CalculoAsistenciaNew.getInfoAsistencia]"
                + "itera marca. "
                + " tipoMarca: " + tipoMarca
                + ", fechaHoraMarca: " + fechaHoraMarca);
            StringTokenizer tokenFechaHora = new StringTokenizer(fechaHoraMarca, " ");
            StringTokenizer tokenFecha = new StringTokenizer(tokenFechaHora.nextToken(), "-");
            StringTokenizer tokenHora = new StringTokenizer(tokenFechaHora.nextToken(), ":");
            fechaMarca = tokenFecha.nextToken() + "-" + tokenFecha.nextToken() + "-" + tokenFecha.nextToken();
            horaMarcaFull = tokenHora.nextToken() + ":" + tokenHora.nextToken() + ":" + tokenHora.nextToken();//considera los segundos
            horaMarca = horaMarcaFull.substring(0, horaMarcaFull.length()-3) + ":00";//no considerar los segundos
            System.out.println(WEB_NAME+"[GestionFemase."
            + "CalculoAsistenciaNew]"
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
                    + "CalculoAsistenciaNew]."
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
                    + "CalculoAsistenciaNew]"
                    + "getInfoAsistencia. "
                    + "Datos para Hrs presenciales. "
                    + "Entrada1: " + currentMarca.getFechaEntrada()+ " " + currentMarca.getHoraEntrada()
                    + ", Entrada2: " + currentMarca.getFechaEntrada()+ " " + currentMarca.getHoraEntradaFull()
                    + ", Salida1: " + currentMarca.getFechaSalida() + " " + currentMarca.getHoraSalida()
                    + ", Salida2: " + currentMarca.getFechaSalida() + " " + currentMarca.getHoraSalidaFull());

                //rescatar todas las marcas, hacer el pareo y calcular hrs presenciales
                String hp = getHorasPresenciales(_empleado.getEmpresaid(), 
                    _empleado.getRut(),
                    currentMarca.getFechaEntrada());
                
                diferenciaReal = 
                    Utilidades.getTimeDifference(currentMarca.getFechaEntrada()+ " " + currentMarca.getHoraEntradaFull(), 
                        currentMarca.getFechaSalida() +" " +currentMarca.getHoraSalidaFull());
                hhmmPresencial = diferenciaReal.getStrDiferenciaHorasMinutosSegundos();
                System.out.println(WEB_NAME+"[GestionFemase."
                    + "CalculoAsistenciaNew]"
                    + "getInfoAsistencia. "
                    + "Hrs presenciales calculadas: " + hhmmPresencial
                    + ", Hrs presenciales (new): " + hp);
                 if (!_turnoNocturno) hhmmPresencial = hp;
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
                    + "CalculoAsistenciaNew]"
                    + "getInfoAsistencia. "
                    + "Minutos de colacion: " + minsColacion
                    + ",hhmmmPresenciales: " + hhmmPresencial);
            }else{
                System.out.println(WEB_NAME+"[GestionFemase."
                    + "CalculoAsistenciaNew]"
                    + "getInfoAsistencia. No tiene turno!!");
            }
            
            int horasReales     = diferenciaReal.getIntDiferenciaHoras();
            int minutosReales   = diferenciaReal.getIntDiferenciaMinutos();
            int minutosExtras   = 0;
            int minutosAtraso   = 0;
            int minutosNoTrabajadosEntrada  = 0;
            int minutosNoTrabajadosSalida   = 0;
            
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
            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia**2** "
                + "FechaEntradaMarca: " + currentMarca.getFechaEntrada()
                + ", horaEntradaMarca: " + currentMarca.getHoraEntradaFull()
                + ", fechaSalidaMarca: " + currentMarca.getFechaSalida()
                + ", horaSalidaMarca: " + currentMarca.getHoraSalidaFull());
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
            
            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia"
                + "--1-- setHrsPresenciales= " 
                + diferenciaReal.getStrDiferenciaHorasMinutos()+", minsColacion: "+minsColacion); 
            detalleCalculo.setHrsPresenciales(hhmmPresencial);
            //detalleCalculo.setHrsPresenciales(diferenciaReal.getStrDiferenciaHorasMinutos());

            detalleCalculo.setArt22(_empleado.isArticulo22());
            if (_detalleturno != null){
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia"
                    + "--3-- "
                    + "turno.horaEntrada= " + _detalleturno.getHoraEntrada()
                    + ",turno.horaSalida= " + _detalleturno.getHoraSalida()); 
                detalleCalculo.setHoraEntradaTeorica(_detalleturno.getHoraEntrada());
                detalleCalculo.setHoraSalidaTeorica(_detalleturno.getHoraSalida());
                detalleCalculo.setHolguraMinutos(_detalleturno.getHolgura());
            }else{
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia."
                    + "NO TIENE MARCAS, "
                    + "Set observacion[8]");
                System.out.println(WEB_NAME+"[GestionFemase."
                    + "CalculoAsistenciaNew]"
                    + "procesaAsistencia] -2- Set Sin turno (Libre)");
                detalleCalculo.setObservacion("Sin turno");
            }
            
            String strKey = _empleado.getEmpresaid() + "|" + _empleado.getRut() + "|" + currentMarca.getFechaEntrada();
            InfoFeriadoVO infoFeriado = m_fechasCalendarioFeriados.get(strKey);
            
////////    comentado        InfoFeriadoVO infoFeriado = 
////////                m_feriadosBp.validaFeriado(currentMarca.getFechaEntrada(), 
////////                    _empleado.getEmpresaid(), 
////////                    _empleado.getRut());
            
            boolean esFeriado = infoFeriado.isFeriado();
            detalleCalculo.setEsFeriado(esFeriado);//
            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia."
                + "Fecha marca entrada: " + currentMarca.getFechaEntrada()
                + ",Fecha marca salida: " + currentMarca.getFechaSalida()
                + ",Minutos Reales= " + minutosReales
                + ", Minutos teoricos= " + _minutosTeoricos
                + ", hhmmTeoricas= " + hhmmTeoricas
                + ", hhmmPresencial= " + hhmmPresencial);
            int comparaHrsRealesTeoricas = Utilidades.comparaHoras(currentMarca.getFechaEntrada()
                + " " + hhmmTeoricas+":00", 
                currentMarca.getFechaEntrada() + " " + hhmmPresencial+":00");
            DiferenciaHorasVO diferenciaNTSalida=new DiferenciaHorasVO();
            System.out.println(WEB_NAME+"[GestionFemase."
                + "CalculoAsistenciaNew]NEW. comparaHrsRealesTeoricas: "
                + comparaHrsRealesTeoricas);
            DiferenciaHorasVO auxDifEntrada = null;
            if (_detalleturno != null){
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]NEW."
                        + "Calculo diferencia entre hora entrada real vs teorica");
                auxDifEntrada = 
                    Utilidades.getTimeDifference(currentMarca.getFechaEntrada()
                        + " " + currentMarca.getHoraEntrada()+":00", 
                        currentMarca.getFechaEntrada() 
                        + " " + _detalleturno.getHoraEntrada());
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]NEW."
                    + "Diferencia hras entrada= " + auxDifEntrada.getStrDiferenciaHorasMinutos());
                if (auxDifEntrada.isHoraInicialMenor()) {
                    //comparaHrsRealesTeoricas = 0;
                    System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]NEW."
                        + "No hay atraso");
                }
            }
            //if (minutosReales > _minutosTeoricos){
            if ((auxDifEntrada != null && auxDifEntrada.getStrDiferenciaHorasMinutos().compareTo("00:00") == 0) 
                    && comparaHrsRealesTeoricas == 1){    
                
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia."
                    + "Seteo de hrs extras. Restar horas-"
                    + " hhmmPresencial: " + hhmmPresencial
                    + ",hhmmTeoricas: "+hhmmTeoricas);
                
                DiferenciaHorasVO diferencia99 = 
                    Utilidades.getTimeDifference(currentMarca.getFechaEntrada()
                        + " " + hhmmPresencial+":00", 
                        currentMarca.getFechaEntrada() 
                        + " " + hhmmTeoricas+":00");
                hhmmExtras = diferencia99.getStrDiferenciaHorasMinutos();
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia."
                    + "Setear hh:mm extras= "+hhmmExtras);
                detalleCalculo.setHoraMinsExtras(hhmmExtras);
                detalleCalculo.setHorasMinsExtrasAutorizadas(hhmmExtras);
            }else
            {
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia."
                    + "Setear atraso..");
                //atraso
                if (minutosReales < _minutosTeoricos){
                    //le faltan minutos en la entrada o en la salida
                    if (_detalleturno != null){
                        String fechaHoraEntradaReal = currentMarca.getFechaEntrada()+" " +currentMarca.getHoraEntrada();
                        String fechaHoraSalidaReal = currentMarca.getFechaSalida()+" " +currentMarca.getHoraSalida();
                        String fechaHoraEntradaTeorica = currentMarca.getFechaEntrada()+" " +_detalleturno.getHoraEntrada();
                        String fechaHoraSalidaTeorica = currentMarca.getFechaSalida()+" " +_detalleturno.getHoraSalida();
                        
                        System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia."
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
                            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia."
                                + "minutosNoTrabajadosEntrada= " + minutosNoTrabajadosEntrada
                                +", Setear atraso de hh:mm= " + diferenciaNTEntrada.getStrDiferenciaHorasMinutos());
                            detalleCalculo.setHhmmAtraso(diferenciaNTEntrada.getStrDiferenciaHorasMinutos());
                            
                            if (minutosNoTrabajadosEntrada < 0) minutosNoTrabajadosEntrada = 0;
                        }
                        System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia."
                            + "fechaHoraSalidaReal: " + fechaHoraSalidaReal
                            + ",fechaHoraSalidaTeorica: " + fechaHoraSalidaTeorica);
                        if (Utilidades.comparaHoras(fechaHoraSalidaReal,fechaHoraSalidaTeorica)==1){
                            //hora salida teorica es posterior a la hora de salida real
                            diferenciaNTSalida = 
                                Utilidades.getTimeDifference(currentMarca.getFechaSalida() + " " + currentMarca.getHoraSalida(), 
                                                      currentMarca.getFechaSalida()+" " +_detalleturno.getHoraSalida());
                            minutosNoTrabajadosSalida = 
                                (diferenciaNTSalida.getIntDiferenciaMinutos()) - _detalleturno.getMinutosColacion();
                            
                            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia."
                                + "fechaHoraSalidaReal: " + fechaHoraSalidaReal
                                + ",fechaHoraSalidaTeorica: " + fechaHoraSalidaTeorica
                                + ",difHHMM: " + diferenciaNTSalida.getStrDiferenciaHorasMinutos());
                            
                            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia."
                                + "minutosNoTrabajadosSalida= "+minutosNoTrabajadosSalida);
                            if (minutosNoTrabajadosSalida < 0) minutosNoTrabajadosSalida = 0;
                        }
                        DiferenciaHorasVO diferenciaAtraso = 
                                Utilidades.getTimeDifference(currentMarca.getFechaEntrada() + " " + _detalleturno.getHoraEntrada(), 
                                                      currentMarca.getFechaEntrada()+" " +currentMarca.getHoraEntrada());                    
                        minutosAtraso = diferenciaAtraso.getIntDiferenciaMinutos();
                        
                        if (diferenciaAtraso.isHoraInicialMenor()){
                            minutosAtraso = 0;
                            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia."
                                + " Hora entrada real es menor a la hora entrada del turno");
                        } 
                        
                        String h1 = currentMarca.getFechaEntrada() + " " + currentMarca.getHoraEntrada();//marca 
                        String h2 = currentMarca.getFechaEntrada()+" " +_detalleturno.getHoraEntrada();//turno
                        String hhmmAtraso = getHorasMinutosAtraso(h1, h2);
                        System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia."
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
                System.err.println("[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia."
                    + "hora marca entrada = "+ currentMarca.getHoraEntrada()
                    +", hora turno entrada = "+ _detalleturno.getHoraEntrada()
                    + ",hora marca salida = "+ currentMarca.getHoraSalida()
                    +", hora turno salida = "+ _detalleturno.getHoraSalida()
                );
                
                DiferenciaHorasVO diferenciaEntradaTurno = 
                    Utilidades.getTimeDifference(
                        currentMarca.getFechaEntrada() + " " + currentMarca.getHoraEntrada(), 
                        currentMarca.getFechaEntrada() + " " +_detalleturno.getHoraEntrada());
                
                //int auxMinsAtraso = diferenciaEntradaTurno.getIntDiferenciaMinutos(); 
                String hhmmAtraso       = "";
                String parteBEntrada    = "";
                String hhmmReal1 = currentMarca.getFechaEntrada() + " " + currentMarca.getHoraEntrada();//marca 
                String hhmmTeorica1 = currentMarca.getFechaEntrada()+" " +_detalleturno.getHoraEntrada();//turno
                
                int resultcompare = Utilidades.comparaHoras(hhmmTeorica1, hhmmReal1);
                if (resultcompare == 1){
                    //System.out.println(WEB_NAME+"Hora real: "+horareal+", hora teorica: "+horateorica+", Set atraso");
                    hhmmAtraso = getHorasMinutosAtraso(hhmmReal1, hhmmTeorica1);
                }else if (resultcompare == -1){
                    //System.out.println(WEB_NAME+"Hora real: "+horareal+", hora teorica: "+horateorica+", Set hrs extras");
                    parteBEntrada = diferenciaEntradaTurno.getStrDiferenciaHorasMinutos();
                }
                            
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia.-1- "
                    + "fechaHra1: " + currentMarca.getFechaSalida() + " " + currentMarca.getHoraSalida()
                    + "fechaHra2: " + currentMarca.getFechaSalida()+" " +_detalleturno.getHoraSalida() 
                    + ", hhmmAtrasoX: " + hhmmAtraso);
                DiferenciaHorasVO diferenciaSalidaTurno = 
                    Utilidades.getTimeDifference(currentMarca.getFechaSalida() + " " + currentMarca.getHoraSalida(), 
                            currentMarca.getFechaSalida()+ " " + _detalleturno.getHoraSalida());
                int auxMinExtras = diferenciaSalidaTurno.getIntDiferenciaMinutos();  
                String parteBSalida = diferenciaSalidaTurno.getStrDiferenciaHorasMinutos();
                
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia.-2- "
                    + "fechaHra1: "+currentMarca.getFechaEntrada() + " " + _detalleturno.getHoraEntrada()+
                    ", fechaHra2: "+currentMarca.getFechaSalida()+" " +_detalleturno.getHoraSalida());
                DiferenciaHorasVO diferenciaTurno = 
                    Utilidades.getTimeDifference(currentMarca.getFechaEntrada() + " " + _detalleturno.getHoraEntrada(), 
                                          currentMarca.getFechaSalida() + " " + _detalleturno.getHoraSalida());
                hhmmTurno = diferenciaTurno.getStrDiferenciaHorasMinutos();
                System.err.println("[GestionFemase.CalculoAsistenciaNew]"
                        + "getInfoAsistencia.hhmmTurno = "+ hhmmTurno
                    + ", parteBEntrada = "+parteBEntrada
                    + ", parteBSalida = "+parteBSalida
                    + ", hh:mm Atraso = "+hhmmAtraso
                    + ", auxMinExtras = "+auxMinExtras);
                 
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
                System.err.println("[GestionFemase.CalculoAsistenciaNew]"
                    + "getInfoAsistencia."
                    + "comparahraEntrada = "+ comparahraEntrada
                    + ", comparahraSalida = "+ comparahraSalida+", hhmmAtraso: "+hhmmAtraso);

//                if (auxMinsAtraso < 0) auxMinsAtraso = 0;
//                detalleCalculo.setMinutosAtraso(auxMinsAtraso);
                System.err.println("[GestionFemase.CalculoAsistenciaNew]"
                    + "-1- Seteando atraso"
                    + ", auxMinExtras: " + auxMinExtras
                    + ", parteBSalida: " + parteBSalida);
                detalleCalculo.setHhmmAtraso(hhmmAtraso);    
                if (diferenciaNTSalida != null){
                    System.err.println("[GestionFemase.CalculoAsistenciaNew]"
                        + "-1- Seteando atraso"
                        + ", difHrasMinutos: " 
                        + diferenciaNTSalida.getStrDiferenciaHorasMinutos());
                }
                
//                if (diferenciaNTSalida != null 
//                        && diferenciaNTSalida.getStrDiferenciaHorasMinutos()!=null
//                        && diferenciaNTSalida.getStrDiferenciaHorasMinutos().compareTo("")==0 
//                        && auxMinExtras > 0) 
//                {   System.err.println("[GestionFemase.CalculoAsistenciaNew]"
//                        + "getInfoAsistencia."
//                        + " **1** setHrsExtras = "+ parteBSalida);
//                    detalleCalculo.setMinutosExtras(auxMinExtras);   
//                }
                //if ( diferenciaNTSalida != null 
                  //      && diferenciaNTSalida.getStrDiferenciaHorasMinutos()!=null
                    //    && diferenciaNTSalida.getStrDiferenciaHorasMinutos().compareTo("") == 0)
                //{
                if (comparahraSalida < 0 ){
                    System.err.println("[GestionFemase.CalculoAsistenciaNew]"
                        + "getInfoAsistencia."
                        + " **2.0** set hhmm salida anticipada "
                        + "con: " + parteBSalida);
                    detalleCalculo.setHoraMinsSalidaAnticipada(parteBSalida);
                }
                if (comparahraEntrada > 0 && (parteBEntrada != null && parteBEntrada.compareTo("") != 0) 
                        && comparahraSalida > 0 
                        && (parteBSalida != null && parteBSalida.compareTo("") != 0)){
                    System.err.println("[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia."
                        + " **2.1** sumar Hrs Extras "
                        + "en la entrada: " + parteBEntrada
                        + " mas en la salida: " + parteBSalida);
                    ArrayList<String> heList = new ArrayList<>();
                    heList.add(parteBEntrada);
                    heList.add(parteBSalida);
                    String heTotales = Utilidades.sumTimesList(heList);
                    detalleCalculo.setHoraMinsExtras(heTotales);
                }else {
                    if (comparahraEntrada > 0 && parteBEntrada != null && parteBEntrada.compareTo("") != 0){
                        System.err.println("[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia."
                            + " **2.1** setHrsExtras en la entrada= " + parteBEntrada);
                        detalleCalculo.setHoraMinsExtras(parteBEntrada);
                    }
                    if (comparahraSalida > 0 && parteBSalida!=null && parteBSalida.compareTo("") != 0){
                        System.err.println("[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia."
                            + " **2.2** setHrsExtras en la salida= " + parteBSalida);
                        detalleCalculo.setHoraMinsExtras(parteBSalida);
                    }
                }
                
                System.err.println("[GestionFemase."
                    + "CalculoAsistenciaNew]"
                    + "getInfoAsistencia. hh:mm Atraso=" + hhmmAtraso);

////                detalleCalculo.setHrsTrabajadas(horasMinsTrabajados);
            }else{
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia."
                    + "NO TIENE MARCAS, "
                    + "Set observacion[7]");
                System.out.println(WEB_NAME+"[GestionFemase."
                    + "CalculoAsistenciaNew]"
                    + "procesaAsistencia] -3- Set Sin turno (Libre)");
                detalleCalculo.setObservacion("Sin turno");
                
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
            System.out.println(WEB_NAME+"[GestionFemase."
                + "CalculoAsistenciaNew]"
                + "getInfoAsistencia."
                + "listaAusencias.size()= " + m_listaAusencias.size()
                +", rut: " + _empleado.getRut()
                +", fechaEntrada= " + currentMarca.getFechaEntrada());
            
            ArrayList<DetalleAusenciaJsonVO> detalleausenciasFechaEntrada 
                = m_listaAusencias.get(_empleado.getRut()+"|"+currentMarca.getFechaEntrada());
            DetalleAusenciaJsonVO detalleausenciaEntrada = null;
            if (detalleausenciasFechaEntrada!=null 
                    && !detalleausenciasFechaEntrada.isEmpty()) {
                detalleausenciaEntrada = detalleausenciasFechaEntrada.get(0);
                
            }
//            DetalleAusenciaVO detalleausenciaEntrada = 
//                detalleAusenciaBp.getAusencia(_empleado.getRut(), currentMarca.getFechaEntrada());
            if (detalleausenciaEntrada != null){
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia."
                    + "Tiene ausencias en fecha de entrada!");
                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia."
                    + "ausenciaHraInicio:" + detalleausenciaEntrada.getHorainiciofullasstr()
                    + ",ausenciaHraFin:" + detalleausenciaEntrada.getHorafinfullasstr()
                    + ",ausenciaTipo:" + detalleausenciaEntrada.getNombreausencia()
                    + ",ausenciaPorHora?:" + detalleausenciaEntrada.getPermitehora());
                detalleCalculo.setHoraInicioAusencia(detalleausenciaEntrada.getHorainiciofullasstr());
                detalleCalculo.setHoraFinAusencia(detalleausenciaEntrada.getHorafinfullasstr());
                if (detalleausenciaEntrada.getPermitehora().compareTo("S") == 0){
                    System.out.println(WEB_NAME+"[GestionFemase."
                        + "CalculoAsistenciaNew]getInfoAsistencia.-6- "
                        + "fechaHra1: "+currentMarca.getFechaEntrada() + " " + detalleausenciaEntrada.getHorainiciofullasstr()+
                        "fechaHra2: "+currentMarca.getFechaEntrada()+" " +detalleausenciaEntrada.getHorafinfullasstr());
                    DiferenciaHorasVO diferencia7 = 
                        Utilidades.getTimeDifference(currentMarca.getFechaEntrada() + " " + detalleausenciaEntrada.getHorainiciofullasstr(), 
                                              currentMarca.getFechaEntrada() + " " + detalleausenciaEntrada.getHorafinfullasstr());
                    String hrsAusenciaTrabajadas = diferencia7.getStrDiferenciaHorasMinutos();
                    
                    ArrayList<String> listaHrsAusencias = new ArrayList<>();
                    ArrayList<String> listaHrsJustificadas = new ArrayList<>();
                    ArrayList<String> listaHrsNoRemuneradas = new ArrayList<>();
                    
                    System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia."
                        + "Hrs ausencia trabajadas"
                        + ",hrs autorizadas:" + hrsAusenciaTrabajadas
                        + ",hrs trabajadas reales:" + detalleCalculo.getHrsTrabajadas()
                        + ",hh:mm atraso existentes:" + detalleCalculo.getHhmmAtraso());
                    if (detalleCalculo.getHhmmAtraso() != null 
                        && hrsAusenciaTrabajadas != null){
                            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia.-I-"
                                + "Iterar ausencias para seteo de hrs justificadas...");
                            String auxMsgAusencias = "";
                            String hhmmDifAusencia="";
                            boolean setNoAtraso = false;
                            Iterator<DetalleAusenciaJsonVO> ausenciasIterator = detalleausenciasFechaEntrada.iterator();
                            while (ausenciasIterator.hasNext()) {
                                DetalleAusenciaJsonVO ausencia = ausenciasIterator.next();
                                if (ausencia.getPermitehora().compareTo("S") == 0){
                                    auxMsgAusencias += ausencia.getNombreausencia()
                                        +":" + ausencia.getHorainiciofullasstr().substring(0, ausencia.getHorainiciofullasstr().length()-3) 
                                        +"-" + ausencia.getHorafinfullasstr().substring(0, ausencia.getHorafinfullasstr().length()-3)+" hrs, ";
                                    //restar hrs con fecha
                                    System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia."
                                        + "Resta hrs inicio-fin ausencia. "
                                        + "Inicio: "+currentMarca.getFechaEntrada() + " " + ausencia.getHorainiciofullasstr()
                                        + ", Fin: "+currentMarca.getFechaEntrada() + " " + ausencia.getHorafinfullasstr());
                                    DiferenciaHorasVO difAusencia = 
                                            Utilidades.getTimeDifference(currentMarca.getFechaEntrada() + " " + ausencia.getHorainiciofullasstr() +":00", 
                                        currentMarca.getFechaEntrada() + " " + ausencia.getHorafinfullasstr()+":00");
                                    hhmmDifAusencia = difAusencia.getStrDiferenciaHorasMinutos();
                                    System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia."
                                        + "Agregar dif ausencia= " + difAusencia.getStrDiferenciaHorasMinutos()
                                        + ", hhmmDifAusencia= " + hhmmDifAusencia
                                        + ", detalleCalculo.getHhmmAtraso()= " + detalleCalculo.getHhmmAtraso());
                                    if (ausencia.getTipoausencia()==1){
                                        if (ausencia.getJustificahoras().compareTo("S") == 0){
                                            listaHrsJustificadas.add(difAusencia.getStrDiferenciaHorasMinutos());
                                        }
                                    }else{
                                        if (ausencia.getJustificahoras().compareTo("S") == 0){
                                            listaHrsNoRemuneradas.add(difAusencia.getStrDiferenciaHorasMinutos());
                                        }
                                    }
                                    System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]-BB- "
                                        + "ausencia.getHorainiciofullasstr()= " + ausencia.getHorainiciofullasstr()
                                        + ", horaEntradaTeorica= " + detalleCalculo.getHoraEntradaTeorica());
                                    if (ausencia.getJustificahoras().compareTo("S") == 0){
                                        if (ausencia.getHorainiciofullasstr().compareTo(detalleCalculo.getHoraEntradaTeorica()) != 0){
                                            listaHrsAusencias.add(difAusencia.getStrDiferenciaHorasMinutos());
                                        }
                                    }
//////                                    if (!setNoAtraso && detalleCalculo.getHhmmAtraso().compareTo(hhmmDifAusencia) == 0){
//////                                        System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]-CC- "
//////                                            + "hhmm atraso: " + detalleCalculo.getHhmmAtraso() + 
//////                                            ", hhmmDifAusencia:" + hhmmDifAusencia
//////                                            +", Set hrs atraso en CERO");
//////                                        
//////                                        detalleCalculo.setHhmmAtraso("");
//////                                        setNoAtraso = true;
//////                                    }
                                }else{
                                    auxMsgAusencias += ausencia.getNombreausencia()+",";
                                    if (hhmmTurno != null && hhmmTurno.compareTo("") != 0){
                                        if (ausencia.getJustificahoras().compareTo("S") == 0){
                                            listaHrsAusencias.add(hhmmTurno);
                                        }
                                    }
                                    //hrs justificadas = suma de hrs de ausencia...
                                }
                            }
                            String sumaJustificadas = Utilidades.sumTimesList(listaHrsAusencias);
                            String sumaJustificadasTotales = Utilidades.sumTimesList(listaHrsJustificadas);
                            String sumaNoRemuneradasTotales = Utilidades.sumTimesList(listaHrsNoRemuneradas);
                            
                            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia."
                                + "sumaJustificadas= " + sumaJustificadas
                                + ",sumaJustificadasTotales= " + sumaJustificadasTotales    
                                + ", hhmmDifAusencia= " + hhmmDifAusencia
                                + ", hrsPresenciales= " + detalleCalculo.getHrsPresenciales());
                            detalleCalculo.setHhmmJustificadas(sumaJustificadasTotales);
                            detalleCalculo.setHrsAusencia(sumaNoRemuneradasTotales);
                            //Recalculo horas presenciales...
////////////                            if (detalleausenciasFechaEntrada.size() > 1){
////////////                                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia."
////////////                                    + "Recalculo horas presenciales...");
////////////                                
////////////                                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia."
////////////                                    + "Fecha hora entrada: " + currentMarca.getFechaEntrada()
////////////                                        + " " + detalleCalculo.getHrsPresenciales()
////////////                                        + ", fecha hora justificadas: " + currentMarca.getFechaEntrada()
////////////                                        + " " + sumaJustificadas + ":00");
////////////                                
////////////                                DiferenciaHorasVO newHP = Utilidades.getTimeDifference(currentMarca.getFechaEntrada()
////////////                                    +" " + detalleCalculo.getHrsPresenciales(), 
////////////                                    currentMarca.getFechaEntrada()
////////////                                    +" " + sumaJustificadas+":00");
////////////                                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia."
////////////                                    + "new Horas presenciales: " + newHP.getStrDiferenciaHorasMinutosSegundos());
////////////                                detalleCalculo.setHrsPresenciales(newHP.getStrDiferenciaHorasMinutosSegundos());
////////////                            }
//                            if (sumaJustificadas.compareTo(hhmmDifAusencia) == 0){
//                                detalleCalculo.setHhmmAtraso("");
//                            }
                            //detalleCalculo.setHhmmJustificadas(hrsAusenciaTrabajadas);
                            //detalleCalculo.setHhmmAtraso("");
                    }
                    
                    //detalleCalculo.setHrsTrabajadas(auxTotHras);
                    detalleCalculo.setMinutosAtraso(0);
                    System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia."
                        + "NO TIENE MARCAS, "
                        + " Seteo de ausencias...");
                    
                    ArrayList<DetalleAusenciaJsonVO> detalleausencia2 = m_listaAusencias.get(_empleado.getRut()+"|"+currentMarca.getFechaEntrada());
                    
//                    ArrayList<DetalleAusenciaVO> detalleausencia2 = 
//                        detalleAusenciaBp.getAusencias(_empleado.getRut(), 
//                            currentMarca.getFechaEntrada());
                    
                    String auxMsgAusencias = "";
                    //iterando ausencias
                    System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia.-II-"
                        + "Iterar ausencias para seteo de hrs justificadas...");
                    Iterator<DetalleAusenciaJsonVO> ausenciasIterator = detalleausencia2.iterator();
                    while (ausenciasIterator.hasNext()) {
                        DetalleAusenciaJsonVO ausencia = ausenciasIterator.next();
                        System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia.-II.1-"
                            + "ausencia.nombre: " + ausencia.getNombreausencia()
                            + ",permiteHora: " + ausencia.getPermitehora()
                            + ",justificaHoras: " + ausencia.getJustificahoras()
                            + ",hhmmTurno: " + hhmmTurno);
                        if (ausencia.getPermitehora().compareTo("S") == 0){
                            auxMsgAusencias += ausencia.getNombreausencia()
                                +":" + ausencia.getHorainiciofullasstr().substring(0, ausencia.getHorainiciofullasstr().length()-3) 
                                +"-" + ausencia.getHorafinfullasstr().substring(0, ausencia.getHorafinfullasstr().length()-3)+" hrs, ";
                            //restar hrs con fecha
                            DiferenciaHorasVO difAusencia = Utilidades.getTimeDifference(currentMarca.getFechaEntrada() + " " + ausencia.getHorainiciofullasstr() +":00", 
                                currentMarca.getFechaEntrada() + " " + ausencia.getHorafinfullasstr()+":00");
                            if (ausencia.getJustificahoras().compareTo("S") == 0){
                                listaHrsAusencias.add(difAusencia.getStrDiferenciaHorasMinutos());
                            }
                        }else{
                            auxMsgAusencias += ausencia.getNombreausencia();
                            if (hhmmTurno != null && hhmmTurno.compareTo("") != 0){
                                if (ausencia.getJustificahoras().compareTo("S") == 0){
                                    listaHrsAusencias.add(hhmmTurno);
                                }
                            }
                        }
                    }
                    
                    auxMsgAusencias=auxMsgAusencias.substring(0, auxMsgAusencias.length()-2);
                    detalleCalculo.setObservacion(auxMsgAusencias);
                    System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]setobservacion [99]:"
                        + "auxMsgAusencias: "+auxMsgAusencias);
                    
                }
            }
            if (currentMarca.getFechaEntrada().compareTo(currentMarca.getFechaSalida()) !=0 ){
                ArrayList<DetalleAusenciaJsonVO> detalleausenciasFechaSalida = m_listaAusencias.get(_empleado.getRut()+"|"+currentMarca.getFechaSalida());
                DetalleAusenciaJsonVO detalleausenciaSalida = null;
                if (detalleausenciasFechaSalida!=null && !detalleausenciasFechaSalida.isEmpty()) {
                    detalleausenciaSalida = detalleausenciasFechaSalida.get(0);
                    
                }
//                DetalleAusenciaVO detalleausenciaSalida = 
//                    detalleAusenciaBp.getAusencia(_empleado.getRut(), currentMarca.getFechaSalida());
            
                if (detalleausenciaSalida != null){
                    System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia."
                        + "Tiene ausencias en fecha de salida!");
                    System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia."
                        + "ausenciaHraInicio:" + detalleausenciaSalida.getHorainiciofullasstr()
                        + ",ausenciaHraFin:" + detalleausenciaSalida.getHorafinfullasstr()
                        + ",ausenciaTipo:" + detalleausenciaSalida.getNombreausencia()
                        + ",ausenciaPorHora?:" + detalleausenciaSalida.getPermitehora());
                    detalleCalculo.setHoraInicioAusencia(detalleausenciaSalida.getHorainiciofullasstr());
                    detalleCalculo.setHoraFinAusencia(detalleausenciaSalida.getHorafinfullasstr());
                    if (detalleausenciaSalida.getPermitehora().compareTo("S") == 0){
                        System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]"
                            + "getInfoAsistencia."
                            + "-7- fechaHra1: "+currentMarca.getFechaSalida() 
                            + " " + detalleausenciaSalida.getHorainiciofullasstr()+
                            ",fechaHra2: "+currentMarca.getFechaSalida()
                            +" " +detalleausenciaSalida.getHorafinfullasstr());
                        DiferenciaHorasVO diferencia7 = 
                            Utilidades.getTimeDifference(currentMarca.getFechaSalida() + " " + detalleausenciaSalida.getHorainiciofullasstr(), 
                                currentMarca.getFechaSalida() + " " + detalleausenciaSalida.getHorafinfullasstr());
                        String hrsAusenciaTrabajadas = diferencia7.getStrDiferenciaHorasMinutos();
                        ArrayList<String> listaHoras = new ArrayList<>();
                        listaHoras.add(hrsAusenciaTrabajadas);
                        String auxTotHras = Utilidades.sumTimesList(listaHoras);

                        System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia."
                            + " -A- Hrs ausencia trabajadas"
                            + "hrs autorizadas:" + hrsAusenciaTrabajadas
                            + ",hrs trabajadas reales:" + detalleCalculo.getHrsTrabajadas()
                            + ",hh:mm totales:" + auxTotHras);
                        if (detalleCalculo.getHhmmAtraso() != null 
                            && hrsAusenciaTrabajadas != null){
                                System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia."
                                    + "Seteo de hrs justificadas. "
                                    + "El atraso queda en cero por ausencia justificada");
                                ArrayList<DetalleAusenciaJsonVO> detalleausencia = m_listaAusencias.get(_empleado.getRut()+"|"+currentMarca.getFechaEntrada());
                                
//                                ArrayList<DetalleAusenciaVO> detalleausencia = 
//                                       detalleAusenciaBp.getAusencias(_empleado.getRut(), 
//                                           currentMarca.getFechaEntrada());
                                ArrayList<String> listaHrsAusencias = new ArrayList<>();
                                String auxMsgAusencias="";
                                Iterator<DetalleAusenciaJsonVO> ausenciasIterator = detalleausencia.iterator();
                                while (ausenciasIterator.hasNext()) {
                                    DetalleAusenciaJsonVO ausencia = ausenciasIterator.next();
                                    if (ausencia.getPermitehora().compareTo("S") == 0){
                                        auxMsgAusencias += ausencia.getNombreausencia()
                                            +":" + ausencia.getHorainiciofullasstr().substring(0, ausencia.getHorainiciofullasstr().length()-3) 
                                            +"-" + ausencia.getHorafinfullasstr().substring(0, ausencia.getHorafinfullasstr().length()-3)+" hrs, ";
                                        //restar hrs con fecha
                                        DiferenciaHorasVO difAusencia = Utilidades.getTimeDifference(currentMarca.getFechaEntrada() + " " + ausencia.getHorainiciofullasstr() +":00", 
                                               currentMarca.getFechaEntrada() + " " + ausencia.getHorafinfullasstr()+":00");
                                        if (ausencia.getJustificahoras().compareTo("S") == 0){
                                            listaHrsAusencias.add(difAusencia.getStrDiferenciaHorasMinutos());
                                        }
                                    }else{
                                        auxMsgAusencias += ausencia.getNombreausencia()+",";
                                        if (hhmmTurno != null && hhmmTurno.compareTo("") != 0){
                                            if (ausencia.getJustificahoras().compareTo("S")==0){
                                                listaHrsAusencias.add(hhmmTurno);
                                            }
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
                        System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia."
                            + "NO TIENE MARCAS, "
                            + "Set observacion[4]. Con detalle de ausencias");

                        ArrayList<DetalleAusenciaJsonVO> detalleausencia = m_listaAusencias.get(_empleado.getRut()+"|"+currentMarca.getFechaSalida());
                        
//                        ArrayList<DetalleAusenciaVO> detalleausencia = 
//                            detalleAusenciaBp.getAusencias(_empleado.getRut(), 
//                                currentMarca.getFechaSalida());
                        
                        String auxMsgAusencias = "";
                        //iterando ausencias
                        Iterator<DetalleAusenciaJsonVO> ausenciasIterator = detalleausencia.iterator();
                        while (ausenciasIterator.hasNext()) {
                            DetalleAusenciaJsonVO ausencia = ausenciasIterator.next();
                            //**
                            auxMsgAusencias += ausencia.getNombreausencia();
                            if (ausencia.getPermitehora().compareTo("S") == 0){
                                auxMsgAusencias += ausencia.getNombreausencia()
                                    +":" + ausencia.getHorainiciofullasstr().substring(0, ausencia.getHorainiciofullasstr().length()-3) 
                                    +"-" + ausencia.getHorafinfullasstr().substring(0, ausencia.getHorafinfullasstr().length()-3)+" hrs, ";
                                //restar hrs con fecha
                                DiferenciaHorasVO difAusencia = Utilidades.getTimeDifference(currentMarca.getFechaEntrada() + " " + ausencia.getHorainiciofullasstr() +":00", 
                                       currentMarca.getFechaEntrada() + " " + ausencia.getHorafinfullasstr()+":00");
                                
                                //listaHrsAusencias.add(difAusencia.getStrDiferenciaHorasMinutos());
                            }else{
                                auxMsgAusencias += ausencia.getNombreausencia();
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
                        detalleCalculo.setObservacion(auxMsgAusencias);
    //                    detalleCalculo.setObservacion(detalleausenciaSalida.getNombreAusencia()
    //                        +" de "+detalleausenciaSalida.getHoraInicioFullAsStr().substring(0, detalleausenciaSalida.getHoraInicioFullAsStr().length()-3) 
    //                        +" a "+detalleausenciaSalida.getHoraFinFullAsStr().substring(0, detalleausenciaSalida.getHoraFinFullAsStr().length()-3)+" hrs");
                    }
                }
            }
            System.err.println("[GestionFemase."
                + "CalculoAsistenciaNew]"
                + "getInfoAsistencia."
                + "observacion a setear: "+detalleCalculo.getObservacion());
            
////            if (_detalleTurnoRotLibre!=null){
////                System.err.println("[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia.Set observacion= dia libre");
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

                        System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia."
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
                                    + "CalculoAsistenciaNew]getInfoAsistencia."
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
            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]getInfoAsistencia."
                + "**3** Set detalleCalculo."
                + ",fecha entrada=" + detalleCalculo.getFechaEntradaMarca()
                +", hora Entrada=" + detalleCalculo.getHoraEntrada()
                +", observacion=" + detalleCalculo.getObservacion());
            
            auxDetalleCalculoReturn = detalleCalculo;
        }//fin while de marcas ordenadas
        
        /**
        * 25-08-2019. 
        *  Restar minutos de colacion... 
        *   al tiempo total de Hrs presenciales + hrs justificadas...
        * 
        */
        String auxHrsJustificadas=auxDetalleCalculoReturn.getHhmmJustificadas();
        if (auxDetalleCalculoReturn.getHhmmJustificadas()==null){
            auxHrsJustificadas = "00:00";
        }
        
        ArrayList<String> auxListaHoras = new ArrayList<>();
        auxListaHoras.add(auxDetalleCalculoReturn.getHrsPresenciales());
        auxListaHoras.add(auxHrsJustificadas);
        String sumaHoras = Utilidades.sumTimesList(auxListaHoras);
        int hrsTeoricas = -1;
        if (_detalleturno != null){
            hrsTeoricas = _detalleturno.getTotalHoras();
            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]"
                + "getInfoAsistencia. Saliendo. "
                + "turno.horasTotales= " + _detalleturno.getTotalHoras()); 
        }else{
            System.out.println(WEB_NAME+"[GestionFemase.CalculoAsistenciaNew]"
                + "getInfoAsistencia. Saliendo. No tiene turno asignado para la fecha");
        
        }
        
        System.out.println(WEB_NAME+"[GestionFemase."
            + "CalculoAsistenciaNew]getInfoAsistencia."
            + "Saliendo."
            + " fecha entrada=" + auxDetalleCalculoReturn.getFechaEntradaMarca()    
            + ",hrs presenciales=" + auxDetalleCalculoReturn.getHrsPresenciales()
            +", hrs justificadas=" + auxHrsJustificadas
            +", hrs ausencia=" + auxDetalleCalculoReturn.getHrsAusencia()
            +", mins colacion=" + minsColacion
            +", horasTotales=" + sumaHoras);
        
        if (hrsTeoricas!=-1){    
            String hrsTotalesMenosColacion = sumaHoras;
            if (minsColacion > 0) hrsTotalesMenosColacion = Utilidades.restarMinsHora(sumaHoras, minsColacion);
            System.out.println(WEB_NAME+"[GestionFemase."
                + "CalculoAsistenciaNew]getInfoAsistencia."
                + "Hrs totales - mins colacion = "+ hrsTotalesMenosColacion);
            auxDetalleCalculoReturn.setHrsTrabajadas(hrsTotalesMenosColacion);
            
        }
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
    * Rescatar todas las marcas del empleado para una fecha dada, 
    * hacer el pareo y calcular hrs presenciales
    * 
    */
    private String getHorasPresenciales(String _empresaId, 
            String _rutEmpleado,
            String _fecha){
        MarcasBp marcasBp = new MarcasBp(new PropertiesVO());
        String sumaHorasPresenciales = "00:00";
        System.out.println(WEB_NAME+"[CalculoAsistenciaNew.getHorasPresenciales]"
            + "empresa: " + _empresaId
            + ", rutEmpleado: " + _rutEmpleado
            + ", fecha: " + _fecha);
        LinkedHashMap<String, MarcaVO> allMarcas = 
            marcasBp.getAllMarcas(_empresaId, 
                _rutEmpleado,
                _fecha,_fecha);
        
        // Obtain an Iterator for the entries Set
        //Iterator<MarcaVO> itMarcas = entrySet.iterator();
        MarcaVO marcaEntrada = null;
        MarcaVO marcaSalida = null;
        DiferenciaHorasVO difHoras = new DiferenciaHorasVO();
        ArrayList<String> listaHorasPresenciales = new ArrayList<>();
        String hhmmPresencial = "";
        
        Set entrySet    = allMarcas.entrySet();
        Iterator itMarcas     = entrySet.iterator();        
        while(itMarcas.hasNext()){
            Map.Entry item = (Map.Entry) itMarcas.next();
            MarcaVO marca = (MarcaVO)item.getValue();
            if (marca.getTipoMarca() == Constantes.MARCA_ENTRADA){
                marcaEntrada = marca;
            }else if (marca.getTipoMarca() == Constantes.MARCA_SALIDA){
                    marcaSalida = marca;
            }
            
            if (marcaEntrada != null && marcaSalida != null){
                difHoras = 
                    Utilidades.getTimeDifference(marcaEntrada.getFechaHora(), 
                        marcaSalida.getFechaHora());
                hhmmPresencial = difHoras.getStrDiferenciaHorasMinutos();
                System.out.println(WEB_NAME+"[CalculoAsistenciaNew.getHorasPresenciales]"
                    + "calculando hrs presenciales. "
                    + "fechaHora entrada: " + marcaEntrada.getFechaHora()
                    + ", fechaHora salida: " + marcaSalida.getFechaHora()
                    + ", hrsPresenciales: " + hhmmPresencial);
                
                listaHorasPresenciales.add(hhmmPresencial);
                marcaEntrada = null;
                marcaSalida = null;
            }
        }
        
        sumaHorasPresenciales = Utilidades.sumTimesList(listaHorasPresenciales);
        System.out.println(WEB_NAME+"[CalculoAsistenciaNew.getHorasPresenciales]"
            + "suma hrs presenciales= " + sumaHorasPresenciales);
        
        return sumaHorasPresenciales;
    }
}
