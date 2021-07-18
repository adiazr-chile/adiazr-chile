    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.vo.DetalleAusenciaVO;
import cl.femase.gestionweb.vo.DiferenciaEntreFechasVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.InfoFeriadoVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.VacacionesVO;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author Alexander
 */
public class VacacionesBp {

    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.VacacionesDAO vacacionesdao;
    private final CalendarioFeriadoBp m_feriadosBp = new CalendarioFeriadoBp(new PropertiesVO());
    
    /**
    *   Fecha en formato yyyy-MM-dd 
    */
    private final SimpleDateFormat m_sdf = new SimpleDateFormat("yyyy-MM-dd");
    
    public VacacionesBp(PropertiesVO props) {
        this.props = props;
        eventsService = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        vacacionesdao = new cl.femase.gestionweb.dao.VacacionesDAO(this.props);
    }

    public List<VacacionesVO> getInfoVacaciones(String _empresaId, 
            String _rutEmpleado,
            int _cencoId,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<VacacionesVO> lista = 
            vacacionesdao.getInfoVacaciones(_empresaId, 
                _rutEmpleado, 
                _cencoId, 
                _jtStartIndex, 
                _jtPageSize, _jtSorting);

        return lista;
    }

    public MaintenanceVO update(VacacionesVO _objectToUpdate, 
            MaintenanceEventVO _eventdata){
        MaintenanceVO updValues = vacacionesdao.update(_objectToUpdate);
        
        //if (!updValues.isThereError()){
            String msgFinal = updValues.getMsg();
            updValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return updValues;
    }
    
    public MaintenanceVO updateSaldoYUltimasVacaciones(VacacionesVO _objectToUpdate, 
            MaintenanceEventVO _eventdata){
        MaintenanceVO updValues = vacacionesdao.updateSaldoYUltimasVacaciones(_objectToUpdate);
        
        //if (!updValues.isThereError()){
            String msgFinal = updValues.getMsg();
            updValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return updValues;
    }
    
    /**
    * 
    */
    public MaintenanceVO delete(VacacionesVO _objectToUpdate, 
            MaintenanceEventVO _eventdata){
        MaintenanceVO updValues = vacacionesdao.delete(_objectToUpdate);
        
        //if (!updValues.isThereError()){
            String msgFinal = updValues.getMsg();
            updValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return updValues;
    }
    
    /**
    * 
    * @param _empresaId
    * @param _eventdata
    * @return 
    */
    public MaintenanceVO insertVacacionesFaltantes(String _empresaId, 
            MaintenanceEventVO _eventdata){
        MaintenanceVO insValues = vacacionesdao.insertVacacionesFaltantes(_empresaId);
        
        String msgFinal = insValues.getMsg();
        insValues.setMsg(msgFinal);
        _eventdata.setDescription(msgFinal);
        //insertar evento 
        eventsService.addEvent(_eventdata); 
        
        return insValues;
    }
    
    
    /**
    * 
    * @param _objToInsert
    * @param _eventdata
    * @return 
    */
    public MaintenanceVO insert(VacacionesVO _objToInsert, 
            MaintenanceEventVO _eventdata){
        
        MaintenanceVO insValues = vacacionesdao.insert(_objToInsert);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
    
    /**
    * Calcula saldo vacaciones para todos los empleados de un centro de costo
    * 
    * @param _username
    * @param _empresaId
    * @param _deptoId
    * @param _cencoId
    * @param _parametrosSistema
    */
    public void calculaDiasVacaciones(String _username, 
            String _empresaId, 
            String _deptoId,
            int _cencoId, 
            HashMap<String, Double> _parametrosSistema){
        
        System.out.println("[VacacionesBp."
            + "calculaDiasVacaciones]Calcular dias vacaciones "
            + "para todos los empleados de un cenco."
            + "empresa_id: " + _empresaId
            + ", depto_id: " + _deptoId    
            +", cencoId: " + _cencoId);
        
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
                    
                Iterator<EmpleadoVO> it = listaEmpleados.iterator();
                while(it.hasNext()){
                    EmpleadoVO empleado = it.next();
                    System.out.println("[VacacionesBp."
                        + "calculaDiasVacaciones]Calcular dias vacaciones empleado. "
                        + "rut_empleado: " + empleado.getRut());
                    calculaDiasVacaciones(_username,
                        empleado.getEmpresaId(), 
                        empleado.getRut(), 
                        _parametrosSistema);
                    System.out.println("[VacacionesBp."
                        + "calculaDiasVacaciones]"
                        + "Actualizar saldos de vacaciones "
                        + "en tabla detalle_ausencia "
                        + "(usar nueva funcion setsaldodiasvacacionesasignadas). "
                        + "Run: "+ empleado.getRut());
                    DetalleAusenciaBp detAusenciaBp = new DetalleAusenciaBp(null);
                    detAusenciaBp.actualizaSaldosVacaciones(empleado.getRut());
                }
        }
    }
    
    /**
    * 
    * Metodo que actualiza los dias de vacaciones (en tabla 'vacaciones') 
    * segun los datos a la fecha: 
    *   - Fecha de ingreso del empleado, 
    *   - Vacaciones tomadas a la fecha. 
    *   - Dias normales (1.25 por mes)
    *   - Dias especiales
    *   - Dias progresivos
    *   - Dias por zonas extremas.
    *	
    *   Para el empleado seleccionado:
    * 
    *   1.- Calcular dias normales (1.25 por mes, a partir de la fecha de ingreso del empleado) --dias acumulados
    *   2.- Calcular dias progresivos
    *       formula:
    *            num_cotizaciones =  vacaciones.current_num_cotizaciones
    *            min_meses_cotizando =  parametro.min_meses_cotizando
    *            SI (num_cotizaciones MAYOR min_meses_cotizando) {
    *                meses_exceso = num_cotizaciones - min_meses_cotizando;
    *                SI (meses_exceso MAYOR o IGUAL parametro.n_meses_add_vac_prog){
    *                    //parte entera exacta
    *                    dias_progresivos= meses_exceso % n_meses_add_vac_prog;
    *                }	
    *            }          
    * 
    *   3.- rescatar dias especiales
    *   4.- rescatar dias por zona extrema (esto lo determina el centro de costo del empleado)
    *   5.- dias a favor= dias_normales + dias_progresivos + dias_especiales +  dias_zona_extrema
    *     nuevo_saldo_dias = dias a favor - dias_vacaciones_tomadas en la empresa 
    * 
    *   6.- Si el empleado no tiene informacion de vacaciones, se debe insertar un registro con los datos calculados.
    *       En caso contrario, los datos se actualizan (update)
    * 
    * @param _username
    * @param _empresaId
    * @param _runEmpleado
    * @param _parametrosSistema
    */
    public void calculaDiasVacaciones(String _username, 
            String _empresaId, 
            String _runEmpleado, 
            HashMap<String, Double> _parametrosSistema){
        EmpleadosBp empleadoBp          = new EmpleadosBp(null);
        VacacionesLogBp vacacionesLogBp = new VacacionesLogBp(null);
        VacacionesVO dataVacaciones     = new VacacionesVO();
        
        System.out.println("[VacacionesBp."
            + "calculaDiasVacaciones]"
            + "empresa_id: " + _empresaId
            +", rut_empleado: " + _runEmpleado);
        
        EmpleadoVO infoEmpleado = 
            empleadoBp.getEmpleado(_empresaId, 
                _runEmpleado);
        Calendar calHoy = Calendar.getInstance(new Locale("es","CL"));
        Date fechaActual = calHoy.getTime();
        
//        Calendar calHoy1erDia = Calendar.getInstance(new Locale("es","CL"));
//        calHoy1erDia.set(Calendar.DATE, 1);
//        Date fecha1erDiaMes = calHoy1erDia.getTime();
//        System.out.println("[VacacionesBp."
//            + "calculaDiasVacaciones]"
//            + "fecha1erDiaMes: " + fecha1erDiaMes);
        boolean insertar=false;
        /**
        * 
        * Rescatar valor de parametros.
        * Valores por defecto
        */
        double paramFactorVacaciones        = 1.25;
        double paramMinMesesCotizando       = 120;
        double paramMesesAddVacProgresivas  = 36;
        double paramFactorVacacionesZonaExtrema = 1.67;
        double paramFactorVacacionesEspeciales = 2.5;
        List<VacacionesVO> infoVacaciones = 
            this.getInfoVacaciones(_empresaId, 
                _runEmpleado, -1, -1, -1, "vac.rut_empleado");
        if (infoVacaciones.isEmpty()){
            System.out.println("[VacacionesBp."
                + "calculaDiasVacaciones]"
                + "empresa_id: "+_empresaId
                +", rut_empleado: " + _runEmpleado
                +". No tiene informacion de vacaciones. Continuar...");
            insertar = true;
            dataVacaciones.setEmpresaId(_empresaId);
            dataVacaciones.setRutEmpleado(_runEmpleado);
        }else{
            dataVacaciones = infoVacaciones.get(0);
        }        
        
        Date fechaInicioContrato = infoEmpleado.getFechaInicioContrato();
        // 1.- Calcular d�as normales (1.25 por mes, a partir de la fecha de ingreso del empleado) 
        Date fechaMesVencido = getFechaMesVencido(fechaInicioContrato);
        
        System.out.println("[VacacionesBp."
            + "getFechaMesVencido]Calcular antiguedad entre:"
            + "Fecha inicio contrato: " + fechaInicioContrato
            +", fechaMesVencido: " + fechaMesVencido);
        
//////        long diasAntiguedad = Utilidades.getDifferenceInDays(fechaInicioContrato, fechaMesVencido);
//////        //int mesesAntiguedad = Integer.parseInt(String.valueOf(diasAntiguedad)) / 30;
//////        System.out.println("[VacacionesBp."
//////            + "getFechaMesVencido]"
//////            + "Dias antiguedad (long)= " + diasAntiguedad);
//////        
//////        BigDecimal bgDiasAntiguedad, bgTreinta, bgMesesAntiguedad;
//////        bgDiasAntiguedad = new BigDecimal(diasAntiguedad);
//////        System.out.println("[VacacionesBp."
//////            + "getFechaMesVencido]"
//////            + "Dias antiguedad (bigDecimal.double)= " + bgDiasAntiguedad.doubleValue());
//////        bgTreinta = new BigDecimal("30");
        // divide bg1 with bg2 with 3 scale
//////        bgMesesAntiguedad = bgDiasAntiguedad.divide(bgTreinta, 2, RoundingMode.HALF_DOWN);
        
        //20201116-001
        DiferenciaEntreFechasVO difFechas = Utilidades.getDiferenciaEntreFechas(fechaInicioContrato, fechaMesVencido);
        BigDecimal bgMesesAntiguedad = new BigDecimal(difFechas.getMonths());
        System.out.println("[VacacionesBp."
            + "calculaDiasVacaciones]"
            + "rut_empleado: " + _runEmpleado
            + ", fecha inicio contrato: " + fechaInicioContrato    
            + ", meses antiguedad: " + bgMesesAntiguedad.intValue()
            + ", meses antiguedad(double: " + bgMesesAntiguedad.doubleValue());
        
        if (_parametrosSistema.get("factor_vacaciones") == 0) paramFactorVacaciones = 1.25;
        if (_parametrosSistema.get("min_meses_cotizando") == 0) paramMinMesesCotizando = 120;
        if (_parametrosSistema.get("n_meses_add_vac_prog") == 0) paramMesesAddVacProgresivas = 36;
        if (_parametrosSistema.get("factor_vac_zona_extrema") == 0) paramFactorVacacionesZonaExtrema = 1.67;
        if (_parametrosSistema.get("factor_vac_especiales") == 0) paramFactorVacacionesEspeciales = 2.5;
                        
        System.out.println("[VacacionesBp."
            + "calculaDiasVacaciones]"
            + "paramFactorVacaciones: " + paramFactorVacaciones
            + ", paramMinMesesCotizando: " + paramMinMesesCotizando    
            + ", paramMesesAddVacProgresivas: " + paramMesesAddVacProgresivas
            + ", paramFactorVacacionesZonaExtrama: " + paramFactorVacacionesZonaExtrema
            + ", paramFactorVacacionesEspeciales: " + paramFactorVacacionesEspeciales);
        
        if (infoEmpleado.getCentroCosto().getZonaExtrema().compareTo("S") == 0){ 
            paramFactorVacaciones = paramFactorVacacionesZonaExtrema;
            System.out.println("[VacacionesBp."
                + "calculaDiasVacaciones]"
                + "rut_empleado: " + _runEmpleado
                + ", cenco: " + infoEmpleado.getCentroCosto().getNombre()
                +", es zona extrema, usar factor dias= "+paramFactorVacaciones);
        }
        
        if (dataVacaciones.getDiasEspeciales().compareTo("S") == 0){
            System.out.println("[VacacionesBp."
                + "calculaDiasVacaciones]"
                + "rut_empleado: " + _runEmpleado
                + ", cenco: " + infoEmpleado.getCentroCosto().getNombre()
                +". Usar factor dias vacaciones especiales");
            paramFactorVacaciones = paramFactorVacacionesEspeciales;
        }
        bgMesesAntiguedad = new BigDecimal(bgMesesAntiguedad.intValue());
        BigDecimal bgDiasDiasAFavor = bgMesesAntiguedad.multiply(new BigDecimal(paramFactorVacaciones));
        double diasNormales = bgDiasDiasAFavor.intValue();
        System.out.println("[VacacionesBp."
            + "calculaDiasVacaciones]"
            + "rut_empleado: " + _runEmpleado
            + ", dias a favor(int): " + bgDiasDiasAFavor.intValue()
            + ", dias a favor(double): " + bgDiasDiasAFavor.doubleValue());
        /**
        * 2.- Calcular dias progresivos segun formula
        */
        BigDecimal bdmmc = new BigDecimal(paramMinMesesCotizando);
        bdmmc = bdmmc.setScale(0,BigDecimal.ROUND_HALF_DOWN);
        
        int diasProgresivos = 0;
//        int mesesExceso = 0;
        int numCotizaciones =  bdmmc.intValue(); //dataVacaciones.getNumActualCotizaciones();
        
        System.out.println("[VacacionesBp."
            + "calculaDiasVacaciones]"
            + "rut_empleado: " + _runEmpleado
            + ", afp_code: " + dataVacaciones.getAfpCode()
            + ", fecha certif afp(vac prog): " + dataVacaciones.getFechaCertifVacacionesProgresivas());
        
        if (dataVacaciones.getFechaCertifVacacionesProgresivas() == null 
            || dataVacaciones.getAfpCode() == null){
                numCotizaciones = 0;
        }else{
            System.out.println("[VacacionesBp."
                + "calculaDiasVacaciones]"
                + "rut_empleado: " + _runEmpleado
                + "Tiene certificado. "
                + "Setear num actual cotizaciones segun datos "
                + "en info vacacionesa. "
                + "Num cotizaciones=" + dataVacaciones.getNumActualCotizaciones());
            numCotizaciones =  dataVacaciones.getNumActualCotizaciones();
        }
        
        BigDecimal bdmavp = new BigDecimal(paramMesesAddVacProgresivas);
        bdmavp = bdmavp.setScale(0,BigDecimal.ROUND_HALF_DOWN);
        
        System.out.println("[VacacionesBp."
            + "calculaDiasVacaciones]"
            + "numCotizaciones(segun certificado afp): A= " + numCotizaciones
            + ", mesesAntiguedad en la empresa: B= " + bgMesesAntiguedad.doubleValue()
            + ", ( A + B ): " + (numCotizaciones + bgMesesAntiguedad.doubleValue())    
            + ", paramMinMesesCotizando: " + bdmmc.intValue());
        
        /**
        *   Actualizar numero de cotizaciones, 
        *   de acuerdo a la fecha de presentacion del certificado de vacaciones progresivas.
        *   Dicho certificado es emitido por la AFP del empleado.
        *
        */
        System.out.println("[VacacionesBp.calculaDiasVacaciones]CALCULAR NUM COTIZACIONES");
        boolean tieneCertifAFP          = false;
        BigDecimal bgMesesCertificado   = new BigDecimal(0);
        if (dataVacaciones.getAfpCode()!=null && dataVacaciones.getAfpCode().compareTo("NINGUNA") != 0 &&
                dataVacaciones.getFechaCertifVacacionesProgresivas() != null){
            try {
                numCotizaciones = bdmmc.intValue();
                System.out.println("[VacacionesBp."
                    + "calculaDiasVacaciones]"
                    + "Set num cotizaciones. "
                    + "Num cotizaciones inicial= " + numCotizaciones);
                
                tieneCertifAFP = true;
                
                Date fechaCertificado = m_sdf.parse(dataVacaciones.getFechaCertifVacacionesProgresivas());
                //20201116-001
                DiferenciaEntreFechasVO difFechas2 = Utilidades.getDiferenciaEntreFechas(fechaCertificado, fechaActual);
                bgMesesCertificado = new BigDecimal(difFechas2.getMonths());
                //long diasCertif = Utilidades.getDifferenceInDays(fechaCertificado, fechaActual);
                System.out.println("[VacacionesBp."
                    + "calculaDiasVacaciones]"
                    + "Diferencia en dias entre "
                    + "la Fecha certificado: " + fechaCertificado
                    + " y la fecha actual: " + fechaActual
                    + ", dif en meses = " + bgMesesCertificado.intValue());
                //BigDecimal bgDiasCertif, bgMesesCertificado;
                //bgDiasCertif = new BigDecimal(diasCertif);
                
                if (bgMesesCertificado.intValue() > 0){
                    numCotizaciones += bgMesesCertificado.intValue();
                }
                System.out.println("[[VacacionesBp."
                    + "calculaDiasVacaciones]"
                    + "Num cotizaciones actualizado= " + numCotizaciones);
            } catch (ParseException ex) {
                System.err.println("[[VacacionesBp."
                    + "calculaDiasVacaciones]"
                    + "Error al parsear fecha de emision del "
                    + "certificado AFP vacaciones progresivas: "+ex.toString());
            }
        }
        
        System.out.println("[VacacionesBp.calculaDiasVacaciones]Validar si aplican DIAS PROGRESIVOS");
        System.out.println("[VacacionesBp.calculaDiasVacaciones]"
            + "tieneCertifAFP: " + tieneCertifAFP
            + ", numCotizaciones: " + numCotizaciones
            + ", meses antiguedad: " + bgMesesAntiguedad.doubleValue()
            + ", minimo meses cotizando: " + bdmmc.intValue()
            + ", minimo num de meses para sumar dia progresivo= " + bdmavp.intValue()
            + ", meses entre fecha del certificado y fecha actual: " + bgMesesCertificado.intValue());
        //calcular dias progresivos
        //if (tieneCertifAFP && (numCotizaciones + bgMesesAntiguedad.doubleValue()) > bdmmc.intValue()) {
        if (tieneCertifAFP && (numCotizaciones > bdmmc.intValue()) && (bgMesesCertificado.intValue() > bdmavp.intValue())) {
//            //mesesExceso = (numCotizaciones + bgMesesAntiguedad.intValue()) - bdmmc.intValue();
            System.out.println("[VacacionesBp.calculaDiasVacaciones]"
                + "cada cuantos meses sumar dia progresivo= " + bdmavp.intValue());
//            if (mesesExceso >= bdmavp.intValue()){
                //parte entera exacta
//                diasProgresivos = mesesExceso / bdmavp.intValue();
                diasProgresivos = bgMesesCertificado.intValue() / bdmavp.intValue();
                System.out.println("[VacacionesBp."
                    + "calculaDiasVacaciones]APLICAN DIAS PROGRESIVOS. "
                    + "dias progresivos= " + diasProgresivos);
//            }else{
//                System.out.println("[VacacionesBp."
//                    + "calculaDiasVacaciones]"
//                    + "rut_empleado: " + _rutEmpleado 
//                    + " Los meses de exceso (" + mesesExceso + ") "
//                    + "no superan el minimo de meses para sumar dias progresivos (" + bdmavp.intValue() + ")");
//            }	
        }else{
            System.out.println("[VacacionesBp."
                + "calculaDiasVacaciones]"
                + "rut_empleado: " + _runEmpleado 
                + " no cumple con requisito para dias progresivos");
        }
        
//        /**
//        * 3.- rescatar dias especiales
//        */
//        int diasEspeciales = dataVacaciones.getDiasEspeciales();
//        System.out.println("[VacacionesBp."
//            + "calculaDiasVacaciones]"
//            + "rut_empleado: " + _rutEmpleado
//            + ", dias especiales: " + diasEspeciales);
        /**
        * 4.- Rescatar dias por zona extrema (esto lo determina el centro de costo del empleado)
        */
////        int diasZonaExtrema = 0;
////        System.out.println("[VacacionesBp."
////            + "calculaDiasVacaciones]"
////            + "rut_empleado: " + _rutEmpleado
////            + ", cenco: " + infoEmpleado.getCentroCosto().getNombre()
////            +", es zona extrema?: "+infoEmpleado.getCentroCosto().getZonaExtrema());
////        if (infoEmpleado.getCentroCosto().getZonaExtrema().compareTo("S") == 0){ 
////            String aux = String.valueOf(paramDiasZonaExtrema);
////            System.out.println("[VacacionesBp."
////                + "calculaDiasVacaciones]"
////                + "diasZonaExtrema valor param: "+paramDiasZonaExtrema);
////            int idx = aux.indexOf(".");
////            String strDZE = aux.substring(0, idx);
////            diasZonaExtrema = Integer.parseInt(strDZE);
////            System.out.println("[VacacionesBp."
////                + "calculaDiasVacaciones]"
////                + "rut_empleado: " + _rutEmpleado
////                + ", cenco: " + infoEmpleado.getCentroCosto().getNombre()
////                +", es zona extrema?: " + infoEmpleado.getCentroCosto().getZonaExtrema()
////                +", dias zona extrema?: " + diasZonaExtrema);
////        }
                
        /**
        *   5.- dias a favor= dias_normales + dias_progresivos + dias_especiales
        *     nuevo_saldo_dias = saldo_dias - dias_vacaciones_tomadas en la empresa  
        */
        //dias de vacaciones que el empleado se ha tomado desde que llego a la empresa 
        //hay que recalcular los dias efectivos de vacaciones SOLO para 
        //aquellas vacaciones que no tengan dias efectivos seteados en la tabla detalle_ausencia
        String inicioVacacionReciente   = null;
        String finVacacionReciente      = null;
        int iteracion = 0;
        int diasVacacionesTomadas = 0; //vacacionesdao.getAllDiasEfectivosVacaciones(_rutEmpleado);
        System.out.println("[VacacionesBp." +
            "calculaDiasVacaciones]"
            + "Buscar la ultima vacacion existente entre todas las "
            + "ausencias del tipo 'vacaciones' "
            + "cuya fecha_inicio sea >= a la fecha de inicio del contrato del empleado."
            + " Empresa_id: " + _empresaId + ", rutEmpleado: " + _runEmpleado);
        
        ArrayList<DetalleAusenciaVO> vacaciones = vacacionesdao.getAllVacaciones(_empresaId, _runEmpleado);
        if (vacaciones!=null){
            Iterator<DetalleAusenciaVO> it = vacaciones.iterator();
            while(it.hasNext()){
                DetalleAusenciaVO auxVacacion = it.next();
                //la suma de todos los dias efectivos es la que se usar� para descontar dias del saldo acumulado
                if (iteracion == 0){
                    inicioVacacionReciente = auxVacacion.getFechainicio();
                    finVacacionReciente = auxVacacion.getFechafin();
                }
                if (auxVacacion.getDiasEfectivosVacaciones() == 0){
                    diasVacacionesTomadas += 
                        getDiasEfectivos(auxVacacion.getFechainicio(), 
                            auxVacacion.getFechafin(),
                            dataVacaciones.getDiasEspeciales(), 
                            _empresaId, 
                            _runEmpleado);
                }
                iteracion++;
            }
        }
        System.out.println("[VacacionesBp."
            + "calculaDiasVacaciones]"
            + "inicio ultima vacacion(reciente): " + inicioVacacionReciente
            + ", fin ultima vacacion(reciente): " + finVacacionReciente);
        
        BigDecimal diasNormalesBigDecimal = new BigDecimal(String.valueOf(diasNormales));
        diasNormalesBigDecimal = diasNormalesBigDecimal.setScale(0,BigDecimal.ROUND_HALF_DOWN);
        System.out.println("[VacacionesBp."
            + "calculaDiasVacaciones]"
            + "rut_empleado: " + _runEmpleado
            + ", dias normales redondeados: " + diasNormalesBigDecimal.intValue());
                        
        int diasAFavor = diasNormalesBigDecimal.intValue() 
            + diasProgresivos;
        int nuevoSaldoDias = diasAFavor - diasVacacionesTomadas;
        System.out.println("[VacacionesBp."
            + "calculaDiasVacaciones]"
            + "rut_empleado: " + _runEmpleado
            + ", dias a favor: " + diasAFavor
            + ", dias vacaciones tomadas: " + diasVacacionesTomadas
            + ", nuevo saldo dias: " + nuevoSaldoDias
            + ", numCotizaciones: " + numCotizaciones);    
            
        dataVacaciones.setSaldoDias(nuevoSaldoDias);
        dataVacaciones.setDiasProgresivos(diasProgresivos);
        dataVacaciones.setDiasZonaExtrema(0);
        dataVacaciones.setNumActualCotizaciones(numCotizaciones);
        dataVacaciones.setDiasAcumulados(diasAFavor);
        dataVacaciones.setDiasEfectivos(diasVacacionesTomadas);
        
        if (tieneCertifAFP) dataVacaciones.setComentario("");
        
        /**
        * 6.- Si el empleado no tiene informacion de vacaciones, 
        *   se debe insertar un registro con los datos calculados.
        *   En caso contrario, los datos se actualizan (update)
        */
        
        if (!insertar){
            System.out.println("[VacacionesBp."
                + "calculaDiasVacaciones]"
                + "empresa_id: "+_empresaId
                +", rut_empleado: " + _runEmpleado
                +". Actualizar informacion de vacaciones...");
            vacacionesdao.updateFromCalculo(dataVacaciones);
        }else{
            System.out.println("[VacacionesBp."
                + "calculaDiasVacaciones]"
                + "empresa_id: "+_empresaId
                +", rut_empleado: " + _runEmpleado
                +". Insertar informacion de vacaciones...");
            dataVacaciones.setDiasProgresivos(0);
            dataVacaciones.setSaldoDias(nuevoSaldoDias);
            dataVacaciones.setComentario(Constantes.MENSAJE_1);
            vacacionesdao.insert(dataVacaciones);
        }
        //if (inicioVacacionReciente != null){
            System.out.println("[VacacionesBp."
                + "calculaDiasVacaciones]"
                + "Actualizar fechas de ultimas vacaciones");
            dataVacaciones.setFechaInicioUltimasVacaciones(inicioVacacionReciente);
            dataVacaciones.setFechaFinUltimasVacaciones(finVacacionReciente);
            vacacionesdao.updateUltimasVacaciones(dataVacaciones);
        //}
        
        //Insertar en vacaciones log (historia)
        dataVacaciones.setUsername(_username);
        vacacionesLogBp.insert(dataVacaciones);
    }
    
    /**
    *  Obtiene numero de dias efectivos de vacaciones 
    *  para un rango de fechas determinado.Para empleados con marca 'vacaciones_especiales'= 'S',
    * Los dias efectivos son dias corridos y los dias feriados tambien se cuentan.En caso contrario: 
    * Solo se cuentan los dias de lunes a viernes y dias NO FERIADOS
    * 
    * 
    * @param _inicioVacaciones
    * @param _finVacaciones
    * @param _vacacionesEspeciales
    * @param _empresaId
    * @param _runEmpleado
    * 
    * @return int 
    */
    public int getDiasEfectivos(String _inicioVacaciones, 
            String _finVacaciones, 
            String _vacacionesEspeciales,
            String _empresaId,
            String _runEmpleado){
        String fechaInicioVacaciones;
        String fechaFinVacaciones;
        int diasEfectivos = 0;
        
        try{
            Date date1 = m_sdf.parse(_inicioVacaciones);
            Date date2 = m_sdf.parse(_finVacaciones);
            fechaInicioVacaciones = _inicioVacaciones;
            fechaFinVacaciones = _finVacaciones;
        }catch(ParseException pex){
            fechaInicioVacaciones = Utilidades.getFechaYYYYmmdd(_inicioVacaciones);
            fechaFinVacaciones = Utilidades.getFechaYYYYmmdd(_finVacaciones);
        }
        System.out.println("[VacacionesBp."
            + "getDiasEfectivos]"
            + "Iterar fechas en el rango. "
            + "Inicio: " + fechaInicioVacaciones
            + ", Fin: " + fechaFinVacaciones);
        //fechas existentes en el rango seleccionado
        String[] fechas;
        try{
            fechas = Utilidades.getFechas(fechaInicioVacaciones, fechaFinVacaciones);
        }catch(IllegalArgumentException iex){
            System.err.println("[VacacionesBp."
                + "getDiasEfectivos]Usar fechas sin formatear...");
            fechas = Utilidades.getFechas(_inicioVacaciones, _finVacaciones);
        }
        for (String itfecha : fechas) {
            System.out.println("[VacacionesBp."
                + "getDiasEfectivos]"
                + "Itera fecha= " + itfecha);
            if (_vacacionesEspeciales != null && _vacacionesEspeciales.compareTo("N") == 0){    
                System.out.println("[VacacionesBp."
                    + "getDiasEfectivos]"
                    + "Verificar dia de la semana para la fecha: " + itfecha);
                
                LocalDate localdate = Utilidades.getLocalDate(itfecha);
                int diaSemana = localdate.getDayOfWeek().getValue();
                if (diaSemana >= 1 && diaSemana <= 5){
                    InfoFeriadoVO infoFeriado = 
                        m_feriadosBp.validaFeriado(itfecha, 
                            _empresaId, 
                            _runEmpleado);
                    boolean esFeriado = infoFeriado.isFeriado();
////                    boolean esFeriado = hashFeriados.containsKey(itfecha);
                    if (!esFeriado) diasEfectivos++;
                }
            }else {
                System.out.println("[VacacionesBp."
                    + "getDiasEfectivos]"
                    + "Empleado con vacaciones especiales. "
                    + "Fecha: "+itfecha + " contar dia corrido");
                diasEfectivos++;
            }
        }//fin iteracion de fechas

        return diasEfectivos;    
    }
    
    
    public int getInfoVacacionesCount(String _empresaId, 
            String _rutEmpleado,
            int _cencoId){
        return vacacionesdao.getInfoVacacionesCount(_empresaId, 
            _rutEmpleado, _cencoId);
    }
    
    public void saveListVacaciones(ArrayList<VacacionesVO> _vacaciones) throws SQLException {
        vacacionesdao.saveListVacaciones(_vacaciones);
    }
    
    public void deleteListVacaciones(ArrayList<VacacionesVO> _vacaciones) throws SQLException {
        vacacionesdao.deleteListVacaciones(_vacaciones);
    }
    
    public void openDbConnection(){
        vacacionesdao.openDbConnection();
    }
    public void closeDbConnection(){
        vacacionesdao.closeDbConnection();
    }

    /**
    * 
    * @param _fechaInicioContrato
    * @return 
    */
    public Date getFechaMesVencido(Date _fechaInicioContrato){
    
        Date FMV = null;
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calHoy = Calendar.getInstance(new Locale("es","CL"));
        Date dateActual = calHoy.getTime();
        System.out.println("[VacacionesBp."
            + "getFechaMesVencido]"
            + "Fecha actual: " + dateFormat2.format(dateActual)
            + ", fecha inicio contrato: " + _fechaInicioContrato);
        Date dateInicioContrato = null;
        try{
            dateInicioContrato = _fechaInicioContrato;
            int diaFIC = Utilidades.getDatePart(dateInicioContrato, "dd");
            int mesFIC = Utilidades.getDatePart(dateInicioContrato, "MM");
            int anioActual = Utilidades.getDatePart(dateActual, "yyyy");
            int mesActual = Utilidades.getDatePart(dateActual, "MM");
            int diaActual = Utilidades.getDatePart(dateActual, "dd");
            //crear fecha inicio contrato al anio actual
            calHoy.set(Calendar.DATE, diaFIC);
            calHoy.set(Calendar.MONTH, mesFIC - 1);
            calHoy.set(Calendar.YEAR, anioActual);
            FMV = calHoy.getTime();//fecha mes vencido
            if (diaFIC >= diaActual){
                calHoy.set(Calendar.DATE, diaFIC);
                calHoy.set(Calendar.MONTH, (mesActual - 2));
                calHoy.set(Calendar.YEAR, anioActual);
                FMV = calHoy.getTime();//fecha mes vencido
            }else { //if (dateActual.before(FMV)){
                calHoy.set(Calendar.DATE, diaFIC);
                calHoy.set(Calendar.MONTH, mesActual-1);
                FMV = calHoy.getTime();//fecha mes vencido a usar
            } 
            System.out.println("[VacacionesBp."
                + "getFechaMesVencido]"
                + "FMV final a la fecha actual: " + dateFormat2.format(FMV));
        }catch(Exception ex){
            System.err.println("[VacacionesBp."
                + "getFechaMesVencido]"
                + "Error al obtener fecha: " + ex.toString());
        }
        
        return FMV;
    }
}
