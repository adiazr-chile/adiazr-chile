package cl.femase.gestionweb.business;

import cl.femase.gestionweb.common.CalculadoraPeriodo;
import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.dao.EmpleadosDAO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.SetVBAEmpleadoVO;
import cl.femase.gestionweb.vo.VacacionesSaldoPeriodoVO;
import cl.femase.gestionweb.vo.VacacionesVO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
*
* @author Alexander
*/
public class CalculoVacacionesBp  extends BaseBp{

    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.CalculoVacacionesDAO calculoDao;
    private final cl.femase.gestionweb.dao.VacacionesSaldoPeriodoDAO periodosDao;
    private final cl.femase.gestionweb.dao.VacacionesDAO vacacionesDao;
    
    public CalculoVacacionesBp(PropertiesVO props) {
        this.props = props;
        eventsService = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        calculoDao = new cl.femase.gestionweb.dao.CalculoVacacionesDAO();
        periodosDao = new cl.femase.gestionweb.dao.VacacionesSaldoPeriodoDAO(this.props);
        vacacionesDao = new cl.femase.gestionweb.dao.VacacionesDAO(this.props);
    }

    /**
    * 
    * 
    * @param _empresaId 
    * @param _runEmpleado 
    * @param _fechaEmisionCertificadoAFP: Valor por defecto= null
    * @param _numCotizaciones: Valor por defecto = 0 
    * @param _diasEspeciales 
    * @param _diasAdicionales 
    * @param _afpCode: Valor por defecto = 'NINGUNA'
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO setFechaBaseVP(String _empresaId, 
            String _runEmpleado, 
            String _fechaEmisionCertificadoAFP, 
            int _numCotizaciones,
            String _diasEspeciales,
            double _diasAdicionales, 
            String _afpCode, 
            MaintenanceEventVO _eventdata){
        
            ResultCRUDVO modifiedInfo = calculoDao.setFechaBaseVP(_empresaId, 
                _runEmpleado, 
                _fechaEmisionCertificadoAFP, 
                _numCotizaciones, 
                _diasEspeciales, 
                _diasAdicionales, 
                _afpCode);
        
        //if (!modifiedInfo.isThereError()){
            String msgFinal = modifiedInfo.getMsg();
            modifiedInfo.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return modifiedInfo;
    }

    /**
    * Cálculo de vacaciones progresivas por empleado
    * 
    * Esta función realiza el cálculo de saldo de VP para un empleado, 
    *  basándose en la fecha base VP y con la ultima fecha de inicio de vacación.
    * 
    * @param _empresaId 
    * @param _runEmpleado 
    * @param _eventdata 
    * @return  
    */
    public ResultCRUDVO setVP_Empleado(String _empresaId, 
            String _runEmpleado,
            MaintenanceEventVO _eventdata){
        
        ResultCRUDVO modifiedInfo = calculoDao.setVP_Empleado(_empresaId, _runEmpleado);
        String msgFinal = modifiedInfo.getMsg();
        modifiedInfo.setMsg(msgFinal);
        _eventdata.setDescription(msgFinal);
        //insertar evento 
        eventsService.addEvent(_eventdata); 
        
        return modifiedInfo;
    }
    
    /**
    * 
    * Esta función realiza el cálculo de VBA para un empleado vigente, 
    * a partir de la fecha de inicio de contrato 
    * o de la nueva fecha de inicio de contrato 
    * si es que cuenta con continuidad laboral.
    * 
    * @param _empresaId 
    * @param _runEmpleado 
    * @param _eventdata 
    * @return  
    */
    public ResultCRUDVO setVBA_Empleado(String _empresaId, 
            String _runEmpleado, 
            MaintenanceEventVO _eventdata){
        
        ResultCRUDVO modifiedInfo = calculoDao.setVBA_Empleado(_empresaId, _runEmpleado);
        String msgFinal = modifiedInfo.getMsg();
        modifiedInfo.setMsg(msgFinal);
        _eventdata.setDescription(msgFinal);
        //insertar evento 
        eventsService.addEvent(_eventdata); 
        
        return modifiedInfo;
    }
    
    /**
    * "Cálculo de vacaciones básicas anuales por CENCO."
    * 
    * Esta función realiza el cálculo de VBA para un CENTRO DE COSTO: 
    *   Obtiene a todos los empleados vigentes y 
    *   a partir de la fecha de inicio de contrato o 
    *   de la nueva fecha de inicio de contrato, 
    *   si es que cuenta con continuidad laboral.
    * 
    * @param _empresaId 
    * @param _cencoId 
    * @param _eventdata 
    * @return  
    */
    public ResultCRUDVO setVBA_Cenco(String _empresaId, int _cencoId, MaintenanceEventVO _eventdata){
        ResultCRUDVO modifiedInfo = calculoDao.setVBA_Cenco(_empresaId, _cencoId);
        String msgFinal = modifiedInfo.getMsg();
        modifiedInfo.setMsg(msgFinal);
        _eventdata.setDescription(msgFinal);
        //insertar evento 
        eventsService.addEvent(_eventdata); 
        
        return modifiedInfo;
    }
    
    /**
    * Obtiene y modifica el estado de saldos de vacaciones por periodos
    * 
    * @param _empresaId 
    * @param _runEmpleado 
    * @return  
    */ 
    public ResultCRUDVO setEstadoSaldosVacacionesPeriodos(String _empresaId, String _runEmpleado){
        ResultCRUDVO modifiedInfo=new ResultCRUDVO();
        
        System.err.println(WEB_NAME+"[CalculoVacacionesBp."
            + "setEstadoSaldosVacacionesPeriodos]"
            + "modifica el estado de saldos de vacaciones por periodos "
            + "[runEmpleado, empresaId] = [" + _empresaId + "," + _runEmpleado + "] ");

        ArrayList<VacacionesSaldoPeriodoVO> vigentes    = new ArrayList<>();
        ArrayList<VacacionesSaldoPeriodoVO> noVigentes  = new ArrayList<>();
        
        LinkedHashMap<String, VacacionesSaldoPeriodoVO> periodos = periodosDao.getPeriodos(_empresaId, 
            _runEmpleado, -1);
        //int itemPosition = 1;
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();
        
        /**
        *   LOS PERIODOS VIGENTES SON: 
        *   El período correspondiente al año en curso y los dos períodos inmediatamente anteriores.
        * 
        */
        
        
        for (Map.Entry<String, VacacionesSaldoPeriodoVO> entry : periodos.entrySet()) {
            String key = entry.getKey();
            VacacionesSaldoPeriodoVO periodo = entry.getValue();
            System.err.println(WEB_NAME+"[CalculoVacacionesBp."
                + "setEstadoSaldosVacacionesPeriodos]"
                + "itera periodo para determinar si esta vigente/no vigente. "
                + "currentYear: " + currentYear
                + ", fecha inicio periodo: " + periodo.getFechaInicioPeriodo() 
                + ", fecha fin periodo: " + periodo.getFechaFinPeriodo());
            if (periodo.esVigente(currentYear)) {
                periodo.setEstadoId(Constantes.ESTADO_VIGENTE);
                System.err.println(WEB_NAME+"[CalculoVacacionesBp."
                    + "setEstadoSaldosVacacionesPeriodos]"
                    + "Set periodo VIGENTE");
                vigentes.add(periodo);
            } else {
                periodo.setEstadoId(Constantes.ESTADO_NO_VIGENTE);
                System.err.println(WEB_NAME+"[CalculoVacacionesBp."
                    + "setEstadoSaldosVacacionesPeriodos]"
                    + "Set periodo NO VIGENTE");
                noVigentes.add(periodo);
            }
                        
////            if (itemPosition > 2) {
////                periodo.setEstadoId(Constantes.ESTADO_NO_VIGENTE);
////                System.err.println(WEB_NAME+"[CalculoVacacionesBp."
////                    + "setEstadoSaldosVacacionesPeriodos]"
////                    + "Set periodo No Vigente");
////                noVigentes.add(periodo);
////            }else{
////                periodo.setEstadoId(Constantes.ESTADO_VIGENTE);
////                System.err.println(WEB_NAME+"[CalculoVacacionesBp."
////                    + "setEstadoSaldosVacacionesPeriodos]"
////                    + "Set periodo Vigente");
////                vigentes.add(periodo);
////            }
////            itemPosition++;
        }
        
        if (!vigentes.isEmpty()){
            try {
                periodosDao.updateEstadoPeriodos(vigentes);
                VacacionesVO dataVacaciones = new VacacionesVO();
                dataVacaciones.setEmpresaId(_empresaId);
                dataVacaciones.setRutEmpleado(_runEmpleado);
                double sumSaldoVBA = 0;
                for (VacacionesSaldoPeriodoVO itperiodo: vigentes) {
                    sumSaldoVBA += itperiodo.getSaldoVBA();
                }
                dataVacaciones.setDiasAcumulados(sumSaldoVBA);
                dataVacaciones.setSaldoDias(sumSaldoVBA);
                dataVacaciones.setSaldoDiasVBA(sumSaldoVBA);
                
                vacacionesDao.updateVBA(dataVacaciones);
                
            } catch (SQLException ex) {
                System.err.println(WEB_NAME+"[CalculoVacacionesBp."
                    + "setEstadoSaldosVacacionesPeriodos]"
                    + "Error al modificar estado de los periodos VIGENTES: " + ex.toString());
            }
        }
        
        if (!noVigentes.isEmpty()){
            try {
                periodosDao.updateEstadoPeriodos(noVigentes);
            } catch (SQLException ex) {
                System.err.println(WEB_NAME+"[CalculoVacacionesBp."
                    + "setEstadoSaldosVacacionesPeriodos]"
                    + "Error al modificar estado de los periodos NO VIGENTES: " + ex.toString());
            }
        }
    
                
        
        return modifiedInfo;
    }
    
    /**
    * "Cálculo de vacaciones básicas anuales por periodo"
    * 
    * @param _empresaId 
    * @param _deptoId 
    * @param _cencoId 
    * @param _runEmpleado 
    * @param _eventdata 
    * @return  
    */ 
    public ResultCRUDVO setVBANew(String _empresaId, 
            String _deptoId,
            int _cencoId, 
            String _runEmpleado, 
            MaintenanceEventVO _eventdata){
       
        EmpleadosDAO daoEmpleados = new EmpleadosDAO(null);
        List<EmpleadoVO> empleadosList = new ArrayList<>();
        SimpleDateFormat dbformat = new SimpleDateFormat("yyyy-MM-dd");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        Calendar currentCalendar = Calendar.getInstance(new Locale("es","CL"));
        String strCurrentDate = dbformat.format(currentCalendar.getTime());
        
        EmpleadoVO filtro = new EmpleadoVO();
        filtro.setEmpleadoVigente(true);
        filtro.setEmpresaId(_empresaId);
        filtro.setDeptoId(_deptoId);
        filtro.setCencoId(_cencoId);
        filtro.setEstado(1);
        if (_runEmpleado == null){
            //Rescatar todos los empleados del centro de costo seleccionado
            empleadosList = daoEmpleados.getEmpleadosByFiltro(filtro);
        }else{
            System.out.println("[CalculoVacacionesBp.setVBANew]"
                + "Un solo empleado seleccionadoSet. Rut: " + _runEmpleado);
            filtro.setRut(_runEmpleado);
            empleadosList = daoEmpleados.getEmpleadosByFiltro(filtro);
        }
        List<SetVBAEmpleadoVO> empleadosSetVBA = new ArrayList<>();
        for (EmpleadoVO empleado : empleadosList) {
            
            String fecIniContrato = dbformat.format(empleado.getFechaInicioContrato());
            VacacionesSaldoPeriodoVO periodo = CalculadoraPeriodo.getPeriodoVacaciones(strCurrentDate, fecIniContrato);
            String strFecInicioPeriodo = periodo.getFechaInicio().format(formatter);
            String strFecFinPeriodo = periodo.getFechaFin().format(formatter);
            SetVBAEmpleadoVO empleadoInput = new SetVBAEmpleadoVO();
            
            empleadoInput.setEmpresa_id(empleado.getEmpresa().getId());
            empleadoInput.setRun_empleado(empleado.getRut());
            empleadoInput.setFecha_inicio_contrato(fecIniContrato);
            empleadoInput.setFecha_inicio_periodo(strFecInicioPeriodo);
            empleadoInput.setFecha_fin_periodo(strFecFinPeriodo);
            empleadosSetVBA.add(empleadoInput);
            
            System.out.println("[CalculoVacacionesBp.setVBANew]"
                + "Seteando objeto final: " + empleadosSetVBA.toString());
        }
        
        ResultCRUDVO modifiedInfo=new ResultCRUDVO();
        if (!empleadosSetVBA.isEmpty()){
            //transformar ArrayList a json
            // Convertir la lista de Contratos a JSON usando Gson
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonEmpleadosToSet = gson.toJson(empleadosSetVBA);

            /**        
            * Generar un json con los datos del o los empleados seleccionados.(uno o mas)
            * Este json es param de entrada de la funcion 'set_vba_empleados'
            * El metodo 'calculoDao.setVBANew' invoca a la nueva función "set_vba_empleados"         
            **/
            modifiedInfo = calculoDao.setVBANew(jsonEmpleadosToSet);
        }else{
            System.out.println(WEB_NAME+"[CalculoVacacionesBp.setVBANew]No se encontraron empleados...");
        }
        
        String msgFinal = modifiedInfo.getMsg();
        modifiedInfo.setMsg(msgFinal);
        _eventdata.setDescription(msgFinal);
        //insertar evento 
        eventsService.addEvent(_eventdata); 
        
        return modifiedInfo;
    }
    
    
    /**
    * "Cálculo de vacaciones progresivas por CENCO"
    * 
    * Esta función realiza el cálculo de saldo de VP para un CENTRO DE COSTO, 
    *   basándose en las fecha base VP y con la ultima fecha de inicio 
    *   de vacación de todos los empleados del CENCO.* 
    *
    * @param _empresaId 
    * @param _cencoId 
    * @param _eventdata 
    * @return  
    */
    public ResultCRUDVO setVP_Cenco(String _empresaId, int _cencoId, MaintenanceEventVO _eventdata){
        ResultCRUDVO modifiedInfo = calculoDao.setVP_Cenco(_empresaId, _cencoId);
        String msgFinal = modifiedInfo.getMsg();
        modifiedInfo.setMsg(msgFinal);
        _eventdata.setDescription(msgFinal);
        //insertar evento 
        eventsService.addEvent(_eventdata); 
        
        return modifiedInfo;
    }
}
