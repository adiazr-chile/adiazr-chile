    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.vo.DetalleAusenciaVO;
import cl.femase.gestionweb.vo.DiasEfectivosVacacionesVO;
import cl.femase.gestionweb.vo.DiferenciaEntreFechasVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.InfoFeriadoVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author Alexander
 */
public class VacacionesBp  extends BaseBp{

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

    /**
    * 
     * @param _empresaId
     * @param _rutEmpleado
     * @param _cencoId
     * @param _jtStartIndex
     * @param _jtPageSize
     * @param _jtSorting
     * @return 
    */
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
    
    /**
    * 
    * @param _empresaId
    * @param _rutsEmpleados
    * @return 
    */
    public List<VacacionesVO> getVacacionesCalculadasEmpleados(String _empresaId, 
            String[] _rutsEmpleados){
        
        List<VacacionesVO> lista = 
            vacacionesdao.getVacacionesCalculadasEmpleados(_empresaId, _rutsEmpleados);

        return lista;
    }
    /**
    * 
    * @param _empresaId
    * @param _rutEmpleado
    * @param _cencoId
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * @return 
    */
     public List<VacacionesVO> getInfoVacacionesDesvincula2(String _empresaId, 
            String _rutEmpleado,
            int _cencoId,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<VacacionesVO> lista = 
            vacacionesdao.getInfoVacacionesDesvincula2(_empresaId, 
                _rutEmpleado, 
                _cencoId, 
                _jtStartIndex, 
                _jtPageSize, _jtSorting);

        return lista;
    }

    /**
    * 
    * Generar desglose de dias de vacaciones.
    *    .- Rescatar vacaciones.saldo_dias           --> VB
    *    .- Rescatar vacaciones.dias_progresivos     --> VP
    *    .-  SI (VP >0 ) {
    *        obj.dias_efectivos_vba = dias_efectivos_vacaciones - VP
    *        obj.dias_efectivos_vp  = dias_efectivos_vacaciones - dias_efectivos_vba
    *    }else{
    *       obj.dias_efectivos_vba = dias_efectivos_vacaciones
    *    }       
    * 
    * @param _newAusencia
    * @return 
    */
    public DiasEfectivosVacacionesVO getDesgloseDiasVacaciones(DetalleAusenciaVO _newAusencia){
        DiasEfectivosVacacionesVO objDiasEfectivos = new DiasEfectivosVacacionesVO();
        String empresaID    = _newAusencia.getEmpresaId();
        String runEmpleado  = _newAusencia.getRutEmpleado();
        System.out.println(WEB_NAME+"[VacacionesBp."
            + "getDesgloseDiasVacaciones]Calcular desglose (VBA/VP) "
            + "de dias de vacaciones a insertar. "
            + "EmpresaId: " + empresaID
            + ", RunEmpleado: " + runEmpleado);
        List<VacacionesVO> infovacaciones = 
            this.getInfoVacaciones(empresaID, runEmpleado, -1, 0, -1, "rut_empleado");
        if (!infovacaciones.isEmpty()){
            VacacionesVO infoVacaciones = infovacaciones.get(0);
            double diasEfectivosVBA = 0;
            double diasEfectivosVP  = 0;
            double saldoVP = infoVacaciones.getSaldoDiasVP(); 
            double diasEfectivos = _newAusencia.getDiasEfectivosVacaciones();
            
             System.out.println(WEB_NAME+"[VacacionesBp."
                + "getDesgloseDiasVacaciones]"
                + "Dias efectivos solicitados= " + diasEfectivos     
                + ", vacaciones.saldoDias= " + infoVacaciones.getSaldoDias()
                + ", vacaciones.dias_progresivos= " + infoVacaciones.getDiasProgresivos()
                + ", vacaciones.saldo_dias_VBA (pre_vacaciones)= " + infoVacaciones.getSaldoDiasVBA()
                + ", vacaciones.saldo_dias_VP (pre_vacaciones)= " + infoVacaciones.getSaldoDiasVP());
            
             //saldos antes del ingreso de la nueva vacacion
            objDiasEfectivos.setSaldoVBAPreVacaciones(infoVacaciones.getSaldoDiasVBA());
            objDiasEfectivos.setSaldoVPPreVacaciones(infoVacaciones.getSaldoDiasVP());
        
            if (saldoVP > 0) {
                if (diasEfectivos > saldoVP){
                    diasEfectivosVBA = diasEfectivos - saldoVP;
                    diasEfectivosVP = diasEfectivos - diasEfectivosVBA;
                }else{
                    diasEfectivosVP = diasEfectivos;
                }

            }else{
                diasEfectivosVBA = diasEfectivos;
            }
            
            System.out.println(WEB_NAME+"[VacacionesBp."
                + "getDesgloseDiasVacaciones]"
                + "diasEfectivosVBA= " + diasEfectivosVBA
                + ", diasEfectivosVP= " + diasEfectivosVP);
            
            int intValue = (int) diasEfectivosVBA;
            objDiasEfectivos.setDiasEfectivosVBA(intValue);
            intValue = (int) diasEfectivosVP;
            objDiasEfectivos.setDiasEfectivosVP(intValue);
            
            //saldos despues del ingreso de la nueva vacacion
            double auxSaldoVPPost = objDiasEfectivos.getSaldoVPPreVacaciones() - diasEfectivosVP;
            if (auxSaldoVPPost <0 ) auxSaldoVPPost = 0;
            objDiasEfectivos.setSaldoVPPostVacaciones(auxSaldoVPPost);
            objDiasEfectivos.setSaldoVBAPostVacaciones(objDiasEfectivos.getSaldoVBAPreVacaciones() - diasEfectivosVBA);
            
            //objDiasEfectivos.setSaldoVPPostVacaciones(objDiasEfectivos.getSaldoVPPreVacaciones() - diasEfectivosVP);
            
            System.out.println(WEB_NAME+"[VacacionesBp."
                + "getDesgloseDiasVacaciones]Objeto a retornar. "
                + "objDiasEfectivos: " + objDiasEfectivos.toString());
        }
        
        return objDiasEfectivos;
    }
    
    /**
    * 
    * @param _data
    * @return 
    */
    public ResultCRUDVO updateSaldosVacacionesVBAyVP(VacacionesVO _data){
        ResultCRUDVO updValues = vacacionesdao.updateSaldosVacacionesVBAyVP(_data);
        
        return updValues;
    }
    
    /**
    * 
    * @param _objectToUpdate
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO update(VacacionesVO _objectToUpdate, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO updValues = vacacionesdao.update(_objectToUpdate);
        
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
    * @param _objectToUpdate
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO updateSaldoYUltimasVacaciones(VacacionesVO _objectToUpdate, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO updValues = vacacionesdao.updateSaldoYUltimasVacaciones(_objectToUpdate);
        
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
    * @param _objectToUpdate
    * @param _eventdata
    * @return 
    * 
    */
    public ResultCRUDVO delete(VacacionesVO _objectToUpdate, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO updValues = vacacionesdao.delete(_objectToUpdate);
        
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
    * @param _objectToUpdate
    * @param _eventdata
    * @return 
    * 
    */
    public ResultCRUDVO clearRegistry(VacacionesVO _objectToUpdate, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO updValues = vacacionesdao.clearRegistry(_objectToUpdate);
        
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
    public ResultCRUDVO insertVacacionesFaltantes(String _empresaId, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO insValues = vacacionesdao.insertVacacionesFaltantes(_empresaId);
        
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
    public ResultCRUDVO insert(VacacionesVO _objToInsert, 
            MaintenanceEventVO _eventdata){
        
        ResultCRUDVO insValues = vacacionesdao.insert(_objToInsert);
        
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
     * @param _desvinculados
    */
    public void calculaDiasVacaciones(String _username, 
            String _empresaId, 
            String _deptoId,
            int _cencoId, 
            HashMap<String, Double> _parametrosSistema, 
            boolean _desvinculados){
        
        System.out.println(WEB_NAME+"[VacacionesBp."
            + "calculaDiasVacaciones]Calcular dias vacaciones "
            + "para todos los empleados del cenco."
            + "empresa_id: " + _empresaId
            + ", depto_id: " + _deptoId    
            + ", cencoId: " + _cencoId
            + ", desvinculados?: " + _desvinculados);
        
        EmpleadosBp empleadosBp = new EmpleadosBp();
        List<EmpleadoVO> listaEmpleados = new ArrayList<>();
        if (_empresaId.compareTo("-1") != 0 
            && _deptoId.compareTo("-1") != 0
            && _cencoId != -1){
                //todos los empleados del cenco
                if (!_desvinculados){
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
                }else{
                    System.out.println(WEB_NAME+"[VacacionesBp."
                        + "calculaDiasVacaciones]Calcular dias vacaciones "
                        + "para todos los empleados DESVINCULADOS del cenco."
                        + "empresa_id: " + _empresaId
                        + ", depto_id: " + _deptoId    
                        + ", cencoId: " + _cencoId);
                    listaEmpleados = empleadosBp.getEmpleadosDesvinculados(_empresaId, 
                        _deptoId, 
                        _cencoId);
                }
                
                if (listaEmpleados.isEmpty()){
                    System.out.println(WEB_NAME+"[VacacionesBp."
                        + "calculaDiasVacaciones]No hay empleados DESVINCULADOS del cenco."
                        + "empresa_id: " + _empresaId
                        + ", depto_id: " + _deptoId    
                        + ", cencoId: " + _cencoId);
                }
                
                Iterator<EmpleadoVO> it = listaEmpleados.iterator();
                while(it.hasNext()){
                    EmpleadoVO empleado = it.next();
                    System.out.println(WEB_NAME+"[VacacionesBp."
                        + "calculaDiasVacaciones]Calcular dias vacaciones empleado. "
                        + "rut_empleado: " + empleado.getRut());
                    calculaDiasVacaciones(_username,
                        empleado.getEmpresaId(), 
                        empleado.getRut(), 
                        _parametrosSistema);
                    System.out.println(WEB_NAME+"[VacacionesBp."
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
        
        System.out.println(WEB_NAME+"[VacacionesBp."
            + "calculaDiasVacaciones]"
            + "empresa_id: " + _empresaId
            +", rut_empleado: " + _runEmpleado);
        
        EmpleadoVO infoEmpleado = 
            empleadoBp.getEmpleado(_empresaId, 
                _runEmpleado);
        //Calendar calHoy = Calendar.getInstance(new Locale("es","CL"));
        //Date fechaActual = calHoy.getTime();
        
        boolean insertar=false;
        /**
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
            System.out.println(WEB_NAME+"[VacacionesBp."
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
        Date fechaDesvinculacion = infoEmpleado.getFechaDesvinculacion();
        // 1.- Calcular d�as normales (1.25 por mes, a partir de la fecha de ingreso del empleado) 
        Date fechaMesVencido = getFechaMesVencido(fechaInicioContrato, fechaDesvinculacion);
        
        System.out.println(WEB_NAME+"[VacacionesBp."
            + "getFechaMesVencido]Calcular antiguedad entre:"
            + "Fecha inicio contrato: " + fechaInicioContrato
            +", fechaMesVencido: " + fechaMesVencido);
        DiferenciaEntreFechasVO difFechas = Utilidades.getDiferenciaEntreFechas(fechaInicioContrato, fechaMesVencido);
        BigDecimal bgMesesAntiguedad = new BigDecimal(difFechas.getMonths());
        System.out.println(WEB_NAME+"[VacacionesBp."
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
                        
        System.out.println(WEB_NAME+"[VacacionesBp."
            + "calculaDiasVacaciones]"
            + "paramFactorVacaciones: " + paramFactorVacaciones
            + ", paramMinMesesCotizando: " + paramMinMesesCotizando    
            + ", paramMesesAddVacProgresivas: " + paramMesesAddVacProgresivas
            + ", paramFactorVacacionesZonaExtrama: " + paramFactorVacacionesZonaExtrema
            + ", paramFactorVacacionesEspeciales: " + paramFactorVacacionesEspeciales);
        
        if (infoEmpleado.getCentroCosto().getZonaExtrema().compareTo("S") == 0){ 
            paramFactorVacaciones = paramFactorVacacionesZonaExtrema;
            System.out.println(WEB_NAME+"[VacacionesBp."
                + "calculaDiasVacaciones]"
                + "rut_empleado: " + _runEmpleado
                + ", cenco: " + infoEmpleado.getCentroCosto().getNombre()
                +", es zona extrema, usar factor dias= "+paramFactorVacaciones);
        }
        
        if (dataVacaciones.getDiasEspeciales().compareTo("S") == 0){
            System.out.println(WEB_NAME+"[VacacionesBp."
                + "calculaDiasVacaciones]"
                + "rut_empleado: " + _runEmpleado
                + ", cenco: " + infoEmpleado.getCentroCosto().getNombre()
                +". Usar factor dias vacaciones especiales");
            paramFactorVacaciones = paramFactorVacacionesEspeciales;
        }
        bgMesesAntiguedad = new BigDecimal(bgMesesAntiguedad.intValue());
        BigDecimal bgDiasDiasAFavor = bgMesesAntiguedad.multiply(new BigDecimal(paramFactorVacaciones));
        double diasNormales = bgDiasDiasAFavor.doubleValue();
        System.out.println(WEB_NAME+"[VacacionesBp."
            + "calculaDiasVacaciones]"
            + "rut_empleado: " + _runEmpleado
            + ", dias a favor(int): " + bgDiasDiasAFavor.intValue()
            + ", dias a favor(double): " + bgDiasDiasAFavor.doubleValue());
        
        //datos para despues guardar en la tabla 'vacaciones'
        double diasProgresivos = 0;
        boolean tieneCertifAFP = false;
        String mensajeVP    = "";
        String fechaBaseVP = null;
        
        System.out.println(WEB_NAME + "[VacacionesBp.calculaDiasVacaciones]"
            + "AFP Code: " + dataVacaciones.getAfpCode()
            + ", fecha CertifVacacionesProgresivas: " + dataVacaciones.getFechaCertifVacacionesProgresivas());
        
        if (dataVacaciones.getAfpCode()!=null && dataVacaciones.getAfpCode().compareTo("NINGUNA") != 0 &&
                dataVacaciones.getFechaCertifVacacionesProgresivas() != null){
            
            System.out.println(WEB_NAME + "[VacacionesBp.calculaDiasVacaciones]Inicia calculo de dias progresivos (VP)");
            
            /**
            * 2.- Calcular dias progresivos segun formula
            */
            CalculoDiasProgresivosVO auxProgresivos = 
                calculaDiasProgresivos(infoEmpleado, 
                    dataVacaciones, 
                    bgMesesAntiguedad, 
                    paramMinMesesCotizando, 
                    paramMesesAddVacProgresivas);

            diasProgresivos  = auxProgresivos.getDiasProgresivos();
            tieneCertifAFP  = auxProgresivos.isTieneCertifAFP();
            mensajeVP       = auxProgresivos.getMensajeVP();
            fechaBaseVP     = auxProgresivos.getFechaBaseVP();
        }else{
            System.out.println(WEB_NAME + "[VacacionesBp.calculaDiasVacaciones]No cumple para calculo de dias progresivos (VP)");
        }       
        System.out.println(WEB_NAME+"[VacacionesBp." +
            "calculaDiasVacaciones]"
            + "fechaBaseVP: " + fechaBaseVP
            + ", mensajeVP: " + mensajeVP);
        
        /**
        *   5.- dias a favor= dias_normales + dias_progresivos + dias_especiales
        *     nuevo_saldo_dias = saldo_dias - dias_vacaciones_tomadas en la empresa  
        */
        //dias de vacaciones que el empleado se ha tomado desde que llego a la empresa 
        //hay que recalcular los dias efectivos de vacaciones SOLO para 
        //aquellas vacaciones que no tengan dias efectivos seteados en la tabla detalle_ausencia
        String inicioVacacionReciente   = null;
        String finVacacionReciente      = null;
        int diasEfectivosVBAVacacionReciente = 0;
        int diasEfectivosVPVacacionReciente = 0;
        double saldoVBAPostVacaciones = 0;
        double saldoVPPostVacaciones = 0;
        int iteracion = 0;
        int diasVacacionesTomadas = 0; //vacacionesdao.getAllDiasEfectivosVacaciones(_rutEmpleado);
        
        //********************************************************************************************************************
        //********************************************************************************************************************
        System.out.println(WEB_NAME+"[VacacionesBp." +
            "calculaDiasVacaciones]"
            + "Buscar todas las vacaciones existentes entre todas las "
            + "ausencias del tipo 'vacaciones' "
            + "cuya fecha_inicio sea >= a la fecha de inicio del contrato del empleado."
            + " Empresa_id: " + _empresaId + ", rutEmpleado: " + _runEmpleado);
        
        ArrayList<DetalleAusenciaVO> vacaciones = vacacionesdao.getAllVacaciones(_empresaId, _runEmpleado);
        if (vacaciones!=null){
            Iterator<DetalleAusenciaVO> it = vacaciones.iterator();
            while(it.hasNext()){
                DetalleAusenciaVO auxVacacion = it.next();
                System.out.println(WEB_NAME+"[VacacionesBp." +
                    "calculaDiasVacaciones]Datos Vacacion mas reciente. "
                    + "Correlativo vacacion = " + auxVacacion.getCorrelativo() 
                    + ", dias_efectivos= " + auxVacacion.getDiasEfectivosVacaciones()
                    + ", dias_efectivos_vba= " + auxVacacion.getDiasEfectivosVBA()
                    + ", dias_efectivos_vp= " + auxVacacion.getDiasEfectivosVP()
                    + ", saldoVBA Post Vacacion= " + auxVacacion.getSaldoVBAPostVacaciones()
                    + ", saldoVP Post Vacacion= " + auxVacacion.getSaldoVPPostVacaciones()
                );
                //la suma de todos los dias efectivos es la que se usar� para descontar dias del saldo acumulado
                if (iteracion == 0){
                    inicioVacacionReciente          = auxVacacion.getFechainicio();
                    finVacacionReciente             = auxVacacion.getFechafin();
                    diasEfectivosVBAVacacionReciente = auxVacacion.getDiasEfectivosVBA();
                    diasEfectivosVPVacacionReciente = auxVacacion.getDiasEfectivosVP();
                    saldoVBAPostVacaciones          = auxVacacion.getSaldoVBAPostVacaciones();
                    saldoVPPostVacaciones          = auxVacacion.getSaldoVPPostVacaciones();
                }
                //if (auxVacacion.getDiasEfectivosVacaciones() == 0){
                diasVacacionesTomadas += auxVacacion.getDiasEfectivosVBA();
                
////////                    diasVacacionesTomadas += 
////////                        getDiasEfectivos(auxVacacion.getFechainicio(), 
////////                            auxVacacion.getFechafin(),
////////                            dataVacaciones.getDiasEspeciales(), 
////////                            _empresaId, 
////////                            _runEmpleado);
                //}
                iteracion++;
            }
        }
        System.out.println(WEB_NAME+"[VacacionesBp."
            + "calculaDiasVacaciones]"
            + "inicio ultima vacacion(reciente): " + inicioVacacionReciente
            + ", fin ultima vacacion(reciente): " + finVacacionReciente
            + ", dias efectivos VBA: " + diasEfectivosVBAVacacionReciente
            + ", dias efectivos VP: " + diasEfectivosVPVacacionReciente
            + ", saldoVBA Post Vacacion= " + saldoVBAPostVacaciones
            + ", saldoVP Post Vacacion= " + saldoVPPostVacaciones);
        
        BigDecimal diasNormalesBigDecimal = new BigDecimal(String.valueOf(diasNormales));
        diasNormalesBigDecimal = diasNormalesBigDecimal.setScale(2,BigDecimal.ROUND_HALF_DOWN);
        System.out.println(WEB_NAME+"[VacacionesBp."
            + "calculaDiasVacaciones]"
            + "rut_empleado: " + _runEmpleado
            + ", dias normales con decimales: " + diasNormalesBigDecimal.doubleValue());
        
//        double diasAFavor = diasNormalesBigDecimal.doubleValue() + diasProgresivos;
        double diasAFavor = diasNormalesBigDecimal.doubleValue();
        double nuevoSaldoDias = diasAFavor - diasVacacionesTomadas;
        System.out.println(WEB_NAME+"[VacacionesBp."
            + "calculaDiasVacaciones]"
            + "diasAFavor (A): " + diasAFavor
            + ", diasVacacionesTomadas (B): " + diasVacacionesTomadas
            + ", nuevoSaldoDias (A)-(B): " + nuevoSaldoDias);
        
        
        dataVacaciones.setSaldoDiasVBA(nuevoSaldoDias);
        ////dataVacaciones.setSaldoDiasVP(diasProgresivos);
        double saldoDiasVP = 0;
        if (inicioVacacionReciente != null && finVacacionReciente != null){
            saldoDiasVP = getDiasVP(inicioVacacionReciente, finVacacionReciente, diasProgresivos, saldoVPPostVacaciones);
        }
        System.out.println(WEB_NAME+"[VacacionesBp.calculaDiasVacaciones]saldoVP: " + saldoDiasVP);
        dataVacaciones.setSaldoDiasVP(saldoDiasVP);

        if (dataVacaciones.getSaldoDiasVBA() < 0) dataVacaciones.setSaldoDiasVBA(nuevoSaldoDias);
        if (dataVacaciones.getSaldoDiasVP() < 0) dataVacaciones.setSaldoDiasVP(diasProgresivos);
        
        System.out.println(WEB_NAME+"[VacacionesBp."
            + "calculaDiasVacaciones]"
            + "rut_empleado: " + _runEmpleado
            + ", dias a favor: " + diasAFavor
            + ", dias vacaciones tomadas: " + diasVacacionesTomadas
            + ", nuevo saldo dias: " + nuevoSaldoDias
            + ", saldo dias VBA: " + dataVacaciones.getSaldoDiasVBA()
            + ", saldo dias VP: " + dataVacaciones.getSaldoDiasVP());    
            
        dataVacaciones.setSaldoDias(nuevoSaldoDias);
        dataVacaciones.setDiasProgresivos(diasProgresivos);
        dataVacaciones.setDiasZonaExtrema(0);
        dataVacaciones.setDiasAcumulados(diasAFavor);
        dataVacaciones.setDiasEfectivos(diasVacacionesTomadas);
                       
        if (tieneCertifAFP) dataVacaciones.setComentario("");
        
        /**
        * 21-08-2021: campos para calculo de Vacaciones progresivas
        */
        dataVacaciones.setFechaBaseVp(fechaBaseVP);
        dataVacaciones.setMensajeVp(mensajeVP);
        
        /**
        * 6.- Si el empleado no tiene informacion de vacaciones, 
        *   se debe insertar un registro con los datos calculados.
        *   En caso contrario, los datos se actualizan (update)
        */
        
        if (!insertar){
            System.out.println(WEB_NAME+"[VacacionesBp."
                + "calculaDiasVacaciones]"
                + "empresa_id: "+_empresaId
                +", rut_empleado: " + _runEmpleado
                +". Actualizar informacion de vacaciones existente (update)...");
            vacacionesdao.updateFromCalculo(dataVacaciones);
        }else{
            System.out.println(WEB_NAME+"[VacacionesBp."
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
            System.out.println(WEB_NAME+"[VacacionesBp."
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
        
        System.out.println(WEB_NAME+"[VacacionesBp."
            + "getDiasEfectivos]"
            + "empresaId: " + _empresaId
            + ", Run empleado: " + _runEmpleado
            + ", vacaciones especiales?: " + _vacacionesEspeciales    
            + ", inicio vacaciones: " + _inicioVacaciones
            + ", fin vacaciones: " + _finVacaciones);
        
        try{
            Date date1 = m_sdf.parse(_inicioVacaciones);
            Date date2 = m_sdf.parse(_finVacaciones);
            fechaInicioVacaciones = _inicioVacaciones;
            fechaFinVacaciones = _finVacaciones;
        }catch(ParseException pex){
            fechaInicioVacaciones = Utilidades.getFechaYYYYmmdd(_inicioVacaciones);
            fechaFinVacaciones = Utilidades.getFechaYYYYmmdd(_finVacaciones);
        }
        System.out.println(WEB_NAME+"[VacacionesBp."
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
        /**
        * Inicio 20210725-001
        * Carga en memoria la info de la tabla calendario_feriado segun rango de fechas
        * seleccionado. En cada fecha se tiene la info de si es feriado o no.
        * De ser feriado, se indica que feriado es y su tipo.
        * 
        */
        CalendarioFeriadoBp bpFeriados    = new CalendarioFeriadoBp(new PropertiesVO());
        LinkedHashMap<String, InfoFeriadoVO> fechasCalendarioFeriados = bpFeriados.getFechas(_empresaId, 
            _runEmpleado, 
            fechaInicioVacaciones, fechaFinVacaciones);
        //aca son dias efectivos totales
        for (String itfecha : fechas) {
            System.out.println(WEB_NAME+"[VacacionesBp."
                + "getDiasEfectivos]"
                + "Itera fecha= " + itfecha);
            if (_vacacionesEspeciales != null && _vacacionesEspeciales.compareTo("N") == 0){    
                LocalDate localdate = Utilidades.getLocalDate(itfecha);
                int diaSemana = localdate.getDayOfWeek().getValue();
                if (diaSemana >= 1 && diaSemana <= 5){
                    System.out.println(WEB_NAME+"[VacacionesBp.getDiasEfectivos]Es dia de semana (Lu-Vi)");
                    String strKey = _empresaId + "|" + _runEmpleado + "|" + itfecha;
                    InfoFeriadoVO infoFeriado = fechasCalendarioFeriados.get(strKey);
                    
//////              comentado  InfoFeriadoVO infoFeriado = 
//////                        m_feriadosBp.validaFeriado(itfecha, 
//////                            _empresaId, 
//////                            _runEmpleado);
                    boolean esFeriado = infoFeriado.isFeriado();
                    System.out.println(WEB_NAME+"[VacacionesBp.getDiasEfectivos]"
                        + "Fecha " + itfecha + ", es feriado? " + esFeriado);
////                    boolean esFeriado = hashFeriados.containsKey(itfecha);
                    if (!esFeriado) {
                        diasEfectivos++;
                    }else{
                        System.out.println(WEB_NAME+"[VacacionesBp.getDiasEfectivos]-1-"
                            + "Fecha " + itfecha + ", no contabilizar dias efectivos");
                    }
                }else{
                    System.out.println(WEB_NAME+"[VacacionesBp.getDiasEfectivos]-2-"
                        + "Fecha " + itfecha + ", no contabilizar dias efectivos (no es dia de semana)");
                }
            }else {
                System.out.println(WEB_NAME+"[VacacionesBp."
                    + "getDiasEfectivos]"
                    + "Empleado con vacaciones especiales. "
                    + "Fecha: "+itfecha + " contar dia corrido");
                diasEfectivos++;
            }
        }//fin iteracion de fechas

        return diasEfectivos;    
    }
    
    
    /**
    * Obtiene el numero de dias para setear en la columna 'vacaciones.saldo_dias_vp'
    * @param _fechaInicioUltimaVacacion
    * @param _fechaFinUltimaVacacion
    * @param _saldoDiasVPCalculados
    * @param _saldoVpPostUltimaVacacion
    * @return 
    */
    public double getDiasVP(String _fechaInicioUltimaVacacion, 
            String _fechaFinUltimaVacacion, 
            double _saldoDiasVPCalculados, 
            double _saldoVpPostUltimaVacacion){
        double VACACIONES_SALDO_DIAS_VP = -1;
        SimpleDateFormat sdfFull    = new SimpleDateFormat("yyyy-MM-dd", new Locale("es","CL"));
        try {
            Calendar auxcalendarHoy = Calendar.getInstance(new Locale("es","CL"));
            //PARAMS DE ENTRADA
            String strFechaCalculo              = sdfFull.format(auxcalendarHoy.getTime());
            double saldo_resultante_calculo_VP     = _saldoDiasVPCalculados;
            double saldoVP_post_ultimas_vacaciones = _saldoVpPostUltimaVacacion;//valor int
            
            Date FECHA_CALCULO              = auxcalendarHoy.getTime();
            Date FECHA_INICIO_ULTIMA_VACACION = sdfFull.parse(_fechaInicioUltimaVacacion);
            Date FECHA_FIN_ULTIMA_VACACION = sdfFull.parse(_fechaFinUltimaVacacion);
               
            System.out.println(WEB_NAME+"[VacacionesBp.getDiasVP]---------------------------");
            System.out.println(WEB_NAME+"[VacacionesBp.getDiasVP]Inicio, parametros de entrada:");
            
            System.out.println( "[VacacionesBp.getDiasVP]FechaCalculo:\t\t\t\t"+ strFechaCalculo);
            System.out.println( "[VacacionesBp.getDiasVP]Fecha inicio ultima vacacion:\t\t" + _fechaInicioUltimaVacacion);
            System.out.println( "[VacacionesBp.getDiasVP]Fecha fin ultima vacacion:\t\t" + _fechaFinUltimaVacacion);
            System.out.println( "[VacacionesBp.getDiasVP]Saldo_resultante_calculo_VP:\t\t" + saldo_resultante_calculo_VP);
            System.out.println( "[VacacionesBp.getDiasVP]SaldoVP_post_ultimas_vacaciones:\t" + saldoVP_post_ultimas_vacaciones);
            
            /**
            *   SI ((Diferencia(FECHA_CALCULO, FECHA_FIN_ULTIMA_VACACION) > a 1 año).
                {
                    Quiere decir que es una vacación añeja y no vale la pena realizar los descuentos y solo se le debe cargar el nuevo saldo VP, por lo tanto seria:
                    vacaciones.saldo_dias_vp = vacaciones.dias_progresivos.
                }SINO {
                    Entonces lees los datos para realizar las asignaciones. ( vacaciones.saldo_dias_vp = detalle_ausencia.ultima_vacacion.saldo_vp_post_vacacion).
                    Porque quiere decir que esa última vacación fue durante el periodo actual de vacaciones.  
                }

            */
            System.out.println( "[VacacionesBp.getDiasVP]---------------------------");
            DiferenciaEntreFechasVO diferenciaFechas = Utilidades.getDiferenciaEntreFechas(FECHA_CALCULO, FECHA_FIN_ULTIMA_VACACION);
            System.out.println( "[VacacionesBp.getDiasVP]FechaCalculo:\t\t\t\t"+ strFechaCalculo);
            System.out.println( "[VacacionesBp.getDiasVP]Fecha fin ultima vacacion:\t\t" + _fechaFinUltimaVacacion);
            System.out.println( "[VacacionesBp.getDiasVP]Diferencia en anios:\t\t\t" + diferenciaFechas.getYears());
                        
            if (diferenciaFechas.getYears() > 1){
                System.out.println(WEB_NAME+"[VacacionesBp.getDiasVP]Vacacion antigua mas de un anio. Tomar saldo VP  calculado segun criterios definidos...");
                VACACIONES_SALDO_DIAS_VP = saldo_resultante_calculo_VP;
            }else{
                System.out.println(WEB_NAME+"[VacacionesBp.getDiasVP]Vacacion reciente. Tomar saldo_vp_post_vacacion de la ultima vacacion...");
                VACACIONES_SALDO_DIAS_VP = saldoVP_post_ultimas_vacaciones;
            }
            System.out.println(WEB_NAME+"[VacacionesBp.getDiasVP]FINAL -----> Saldo VP: " + VACACIONES_SALDO_DIAS_VP);
                        
        } catch (ParseException ex) {
            System.err.println("[VacacionesBp.getDiasVP]Error: " + ex.toString());
        }
    
        return VACACIONES_SALDO_DIAS_VP;
    }
    
    /**
    *   Calcula dias de vacaciones progresivas 
    * 
    * @param _infoEmpleado
    * @param _dataVacaciones
    * @param _bgMesesAntiguedad
    * @param _paramMinMesesCotizando
    * @param _paramMesesAddVacProgresivas
    * @return 
    */
    public CalculoDiasProgresivosVO calculaDiasProgresivos(EmpleadoVO _infoEmpleado, 
            VacacionesVO _dataVacaciones, 
            BigDecimal _bgMesesAntiguedad, 
            double _paramMinMesesCotizando,
            double _paramMesesAddVacProgresivas){
        
        double diasProgresivos = 0;
        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd",
            new Locale("es","CL"));
        Calendar auxcalendar = Calendar.getInstance(new Locale("es","CL"));
        Date fechaActual = auxcalendar.getTime();
        BigDecimal bdmmc = new BigDecimal(_paramMinMesesCotizando);//120
        bdmmc = bdmmc.setScale(0,BigDecimal.ROUND_HALF_DOWN);
        
        int numCotizaciones =  _dataVacaciones.getNumCotizaciones();         
        System.out.println(WEB_NAME+"[VacacionesBp."
            + "calculaDiasProgresivos]"
            + "run_empleado: " + _dataVacaciones.getRutEmpleado()
            + ", afp_code: " + _dataVacaciones.getAfpCode()
            + ", fecha certif afp(vac prog): " + _dataVacaciones.getFechaCertifVacacionesProgresivas()
            + ", numCotizaciones segun certif AFP: " + numCotizaciones);
        
        BigDecimal bdmavp = new BigDecimal(_paramMesesAddVacProgresivas);
        bdmavp = bdmavp.setScale(0,BigDecimal.ROUND_HALF_DOWN);
        
        System.out.println(WEB_NAME+"[VacacionesBp."
            + "calculaDiasProgresivos]"
            + "numCotizaciones(segun certificado afp): A= " + numCotizaciones
            + ", mesesAntiguedad en la empresa: B= " + _bgMesesAntiguedad.doubleValue()
            + ", ( A + B ): " + (numCotizaciones + _bgMesesAntiguedad.doubleValue())    
            + ", paramMinMesesCotizando: " + bdmmc.intValue());
        //tieneCertifAFP = true;
        /**
        * valores necesarios para calcular la fecha base de vacaciones progresivas
        */
        Date fechaCertificado = null;
        try{
            fechaCertificado = m_sdf.parse(_dataVacaciones.getFechaCertifVacacionesProgresivas());
        }catch(ParseException pex){
            System.err.println("Error al parsear fecha certif vac progresivas: " + pex.toString());
        }
        
        System.out.println(WEB_NAME+"[VacacionesBp."
            + "calculaDiasProgresivos]Calcular fecha base para Vacaciones progresivas (Fecha base VP)...");
        
        Date fechaInicioContrato = _infoEmpleado.getFechaInicioContrato();
        System.out.println(WEB_NAME+"[VacacionesBp."
            + "calculaDiasProgresivos]"
            + "Tiene marca de continuidad laboral? " + _infoEmpleado.getContinuidadLaboral()
            + ", nueva fecha inicio contrato: " + _infoEmpleado.getNuevaFechaIniContratoAsStr());
        

////        if (_infoEmpleado.getContinuidadLaboral().compareTo("S") == 0){
////            fechaInicioContrato = _infoEmpleado.getNuevaFechaIniContrato();
////            System.out.println(WEB_NAME+"[VacacionesBp."
////                + "calculaDiasProgresivos]Set Fecha inicio contrato = "
////                + "Nueva Fecha inicio contrato = " + fechaInicioContrato);
////        }
                
        Date fechaBaseVacProgresivas = 
            getFechaBaseVP(fechaInicioContrato, fechaCertificado, numCotizaciones, _paramMinMesesCotizando);
        
        DiferenciaEntreFechasVO difFechas2 = 
            Utilidades.getDiferenciaEntreFechas(fechaBaseVacProgresivas, fechaActual);
        BigDecimal bgMesesTranscurridos = new BigDecimal(difFechas2.getMonths());

        System.out.println(WEB_NAME+"[VacacionesBp.calculaDiasProgresivos]"
            + "numCotizaciones: " + numCotizaciones
            + ", meses antiguedad: " + _bgMesesAntiguedad.doubleValue()
            + ", minimo meses cotizando: " + bdmmc.intValue()
            + ", minimo num de meses para sumar dia progresivo= " + bdmavp.intValue()
            + ", meses entre fecha base de vac progresivas y la fecha actual: " + bgMesesTranscurridos.intValue()
            + ", Fecha base Vacaciones Progresivas (fecha base VP): " + fechaBaseVacProgresivas);
        
        Date fechaVacacionesBasicas = null;
        try{
            int currentYear = auxcalendar.get(Calendar.YEAR);
            auxcalendar.setTime(fechaInicioContrato);
            auxcalendar.set(Calendar.YEAR, currentYear);
            fechaVacacionesBasicas = auxcalendar.getTime();
        } catch (Exception ex) {
            System.err.println("[VacacionesBp.calculaDiasProgresivos]"
                + "Error al setear fecha vacaciones basicas: " + ex.toString());
        }
        
        System.out.println(WEB_NAME+"[VacacionesBp.calculaDiasProgresivos]"
            + "Validar si aplican DIAS PROGRESIVOS");
        System.out.println(WEB_NAME+"[VacacionesBp.calculaDiasProgresivos]"
            + "Fecha Actual= " + fechaActual
            + ", fechaBaseVacProgresivas: " + fechaBaseVacProgresivas
            + ", fechaCertificado: " + fechaCertificado
            + ", fechaVacacionesBasicas: " + fechaVacacionesBasicas    
        );
        
        String mensajeVP = "";
        /**
        *   2.2 (FECHA_CALCULO_ACTUAL > FECHA_BASE_VP) 
        *   2.3 (FECHA_CALCULO_ACTUAL > FECHA_CERTIFICADO)
        *   2.4 (FECHA_VACACIONES_BASICAS >= FECHA_CALCULO_ACTUAL) (esto es cuando se haya cumplido la fecha de vacaciones básicas anuales) 
        */
        boolean calcularVacacionesProgresivas = false;
        if (numCotizaciones > bdmmc.intValue()){
            if (bgMesesTranscurridos.intValue() > bdmavp.intValue()){    
                if (fechaActual.after(fechaBaseVacProgresivas)){
                    if (fechaActual.after(fechaCertificado)){
                        if (fechaActual.after(fechaVacacionesBasicas) || fechaActual.equals(fechaVacacionesBasicas)){
                            calcularVacacionesProgresivas = true;
                        }else{
                            mensajeVP = "La fecha de calculo de vacaciones: "
                                + sdf3.format(fechaActual) 
                                + ", es menor a la fecha de vacaciones basicas: " + sdf3.format(fechaVacacionesBasicas);
                            System.out.println(WEB_NAME+"[VacacionesBp.calculaDiasProgresivos]"
                                + mensajeVP);
                        }
                    }else{
                        mensajeVP = "La fecha de calculo de vacaciones: "
                            + sdf3.format(fechaActual) 
                            + ", es menor a la fecha de emision del certificado AFP u otro: " + sdf3.format(fechaCertificado);
                        System.out.println(WEB_NAME+"[VacacionesBp.calculaDiasProgresivos]"
                            + mensajeVP);
                    }
                }else{
                    mensajeVP = "La fecha de calculo de vacaciones "
                        + sdf3.format(fechaActual) 
                        + ", es menor a la fecha base de VP: " + sdf3.format(fechaBaseVacProgresivas);
                    System.out.println(WEB_NAME+"[VacacionesBp.calculaDiasProgresivos]"
                        + mensajeVP);
                }
            }else{
                mensajeVP = "Los meses transcurridos entre la fecha base de VP "
                    + "y la fecha de calculo no es mayor al minimo para VP: " + bdmavp.intValue();
                System.out.println(WEB_NAME+"[VacacionesBp.calculaDiasProgresivos]"
                    + mensajeVP);
            }
        }else{
            mensajeVP = "No cumple con el minimo de " + bdmmc.intValue() + " cotizaciones "
                + "para VP";
            System.out.println(WEB_NAME+"[VacacionesBp.calculaDiasProgresivos]"
                + mensajeVP);
        }
        
        if (calcularVacacionesProgresivas) {
            mensajeVP = "Cumple requisitos para dias de vacaciones progresivas";
            diasProgresivos = bgMesesTranscurridos.intValue() / bdmavp.intValue();
            System.out.println(WEB_NAME+"[VacacionesBp."
                + "calculaDiasProgresivos]"
                + "Dias de vacaciones progresivas calculados= " + diasProgresivos);
        }else{
            System.out.println(WEB_NAME+"[VacacionesBp."
                + "calculaDiasProgresivos]"
                + "Run empleado: " + _dataVacaciones.getRutEmpleado() 
                + " no cumple con requisito para dias progresivos");
        }
    
        CalculoDiasProgresivosVO valoresRetornar = 
            new CalculoDiasProgresivosVO(diasProgresivos, 
                true, sdf3.format(fechaBaseVacProgresivas), mensajeVP);
        
        return valoresRetornar;
    }
    
    /**
    * Obtiene fecha base para vacaciones progresivas
    */
    private static Date getFechaBaseVP(
            Date _fechaInicioContrato,
            Date _fechaCertificado,
            int _numCotizaciones,
            double _paramMinMesesCotizando){
        
        System.out.println(WEB_NAME+"[VacacionesBp."
            + "getFechaBaseVP]Inicio calculo. "
            + "Fecha inicio contrato: = " + _fechaInicioContrato
            + ", fecha certificaco= " + _fechaCertificado
            + ", _numCotizaciones: " + _numCotizaciones    
            + ", paramMinMesesCotizando: " + _paramMinMesesCotizando);
        
        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd", new Locale("es","CL"));
        BigDecimal aux1= new BigDecimal(_paramMinMesesCotizando);
        int minMesesCotizando   = aux1.intValue();
        int diferenciaEnMeses   = minMesesCotizando - _numCotizaciones;
        
        Date fechaBaseVP = Utilidades.sumaRestarFecha(_fechaCertificado, diferenciaEnMeses, "MONTHS");
        Calendar auxcalendarFechaBase = Calendar.getInstance(new Locale("es","CL"));
        auxcalendarFechaBase.setTime(fechaBaseVP);
        
        Calendar auxcalendarFC = Calendar.getInstance(new Locale("es","CL"));
        auxcalendarFC.setTime(_fechaInicioContrato);
            
        //fecha_base_vp = anioFechaBaseVP - mes(fecha ini contrato) - dia(fecha ini contrato);
        //Si FECHA_BASE_VP < FECHA_INICIO_CONTRATO ===> FECHA_BASE_VP = FECHA_INICIO_CONTRATO
        Calendar auxcalendarfinal = Calendar.getInstance(new Locale("es","CL"));
        auxcalendarfinal.set(Calendar.YEAR, auxcalendarFechaBase.get(Calendar.YEAR));
        auxcalendarfinal.set(Calendar.MONTH, auxcalendarFC.get(Calendar.MONTH));
        auxcalendarfinal.set(Calendar.DATE, auxcalendarFC.get(Calendar.DATE));
        fechaBaseVP = auxcalendarfinal.getTime();
        if (fechaBaseVP.before(_fechaInicioContrato)) fechaBaseVP = _fechaInicioContrato;
        
        return fechaBaseVP;
    }
    
    /**
    * 
    * @param _empresaId
    * @param _rutEmpleado
    * @param _cencoId
    * @return 
    */
    public int getInfoVacacionesCount(String _empresaId, 
            String _rutEmpleado,
            int _cencoId){
        return vacacionesdao.getInfoVacacionesCount(_empresaId, 
            _rutEmpleado, _cencoId);
    }
    
    /**
    * 
    * @param _empresaId
    * @param _rutEmpleado
    * @param _cencoId
    * @return 
    */
    public int getInfoVacacionesDesvincula2Count(String _empresaId, 
            String _rutEmpleado,
            int _cencoId){
        return vacacionesdao.getInfoVacacionesDesvincula2Count(_empresaId, 
            _rutEmpleado, _cencoId);
    }
    
    /**
    * 
     * @param _vacaciones
     * @throws java.sql.SQLException
    */
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
     * @param _fechaDesvinculacion
    * @return 
    */
    public Date getFechaMesVencido(Date _fechaInicioContrato, 
            Date _fechaDesvinculacion){
    
        Date FMV = null;
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calHoy = Calendar.getInstance(new Locale("es","CL"));
        Date dateActual = calHoy.getTime();
        System.out.println(WEB_NAME+"[VacacionesBp."
            + "getFechaMesVencido]"
            + "Fecha actual: " + dateFormat2.format(dateActual)
            + ", fecha inicio contrato: " + _fechaInicioContrato
            + ", fecha desvinculacion: " + _fechaDesvinculacion);
//        if (_fechaDesvinculacion == null){
        if (_fechaDesvinculacion != null){
            dateActual = _fechaDesvinculacion;
            System.out.println(WEB_NAME+"[VacacionesBp."
                + "getFechaMesVencido]"
                + "Fecha actual = fecha desvinculacion: " + dateFormat2.format(_fechaDesvinculacion));
        }
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
            System.out.println(WEB_NAME+"[VacacionesBp."
                + "getFechaMesVencido]"
                + "FMV final a la fecha actual: " + dateFormat2.format(FMV));
        }catch(Exception ex){
            System.err.println("[VacacionesBp."
                + "getFechaMesVencido]"
                + "Error al obtener fecha: " + ex.toString());
        }
//        }else{
//            FMV = _fechaDesvinculacion;
//            System.out.println(WEB_NAME+"[VacacionesBp."
//                + "getFechaMesVencido]"
//                + "FMV final = fecha desvinculacion: " + dateFormat2.format(FMV));
//        }
        return FMV;
    }

    private static class CalculoDiasProgresivosVO {
        
        private double diasProgresivos = 0;
        private boolean tieneCertifAFP = false;
        private final String fechaBaseVP;
        private final String mensajeVP;
        
        public CalculoDiasProgresivosVO(double _diasProgresivos,
                boolean _tieneCertifAFP, 
                String _fechaBaseVP,
                String _mensajeVP) {
            this.diasProgresivos = _diasProgresivos;
            this.tieneCertifAFP = _tieneCertifAFP;
            this.fechaBaseVP = _fechaBaseVP;
            this.mensajeVP = _mensajeVP;
        }

        public double getDiasProgresivos() {
            return diasProgresivos;
        }

        public boolean isTieneCertifAFP() {
            return tieneCertifAFP;
        }

        public String getFechaBaseVP() {
            return fechaBaseVP;
        }

        public String getMensajeVP() {
            return mensajeVP;
        }
        
    }
}
