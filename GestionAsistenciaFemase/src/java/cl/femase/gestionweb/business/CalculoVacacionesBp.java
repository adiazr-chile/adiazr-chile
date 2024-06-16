package cl.femase.gestionweb.business;

import cl.femase.gestionweb.dao.EmpleadosDAO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.util.ArrayList;
import java.util.List;

/**
*
* @author Alexander
*/
public class CalculoVacacionesBp  extends BaseBp{

    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.CalculoVacacionesDAO calculoDao;
    
    public CalculoVacacionesBp(PropertiesVO props) {
        this.props = props;
        eventsService = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        calculoDao = new cl.femase.gestionweb.dao.CalculoVacacionesDAO();
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
    * "Cálculo de vacaciones básicas anuales por CENCO."
    * 
    * Esta función realiza el cálculo de VBA para un CENTRO DE COSTO: 
    *   Obtiene a todos los empleados vigentes y 
    *   a partir de la fecha de inicio de contrato o 
    *   de la nueva fecha de inicio de contrato, 
    *   si es que cuenta con continuidad laboral.
    * 
    * @param _empresaId 
     * @param _deptoId 
    * @param _cencoId 
    * @param _runEmpleado 
    * @param _eventdata 
    * @return  
    */ 
    ////descomentar
//////    public ResultCRUDVO setVBANew(String _empresaId, 
//////            String _deptoId,
//////            int _cencoId, 
//////            String _runEmpleado, 
//////            MaintenanceEventVO _eventdata){
//////       
//////        EmpleadosDAO daoEmpleados = new EmpleadosDAO(null);
//////        List<EmpleadoVO> empleadosList = new ArrayList<>();
//////        EmpleadoVO filtro = new EmpleadoVO();
//////        if (_runEmpleado == null){
//////            //Rescatar todos los empleados del centro de costo seleccionado
//////            filtro.setEmpresaId(_empresaId);
//////            filtro.setDeptoId(_deptoId);
//////            filtro.setCencoId(_cencoId);
//////            empleadosList = daoEmpleados.getEmpleadosByFiltro(filtro);
//////            
//////        }else{
//////            filtro.setEmpresaId(_empresaId);
//////            filtro.setDeptoId(_deptoId);
//////            filtro.setCencoId(_cencoId);
//////            filtro.setRut(_runEmpleado);
//////            empleadosList = daoEmpleados.getEmpleadosByFiltro(filtro);
//////        }
//////        
//////        A partir de la fecha inicio de contrato y la fecha actual, se define el inicio y fin del período en curso.
//////Ejemplo: 
//////Fecha actual= 19-05-2024, 
//////Fecha inicio contrato: 09-05-2020
//////--> INICIO_PERIODO	: 09-05-2024 (dia_inicio_contrato/mes_actual/ año_actual)
//////--> FIN_PERIODO		: 09-05-2025 (dia_inicio_contrato/mes_actual/ (año_actual + 1))
//////Luego, se debe invocar a la funcion 'set_vba_empleados' con la lista de empleados y ambas fechas seteadas para c/u.
//////        
//////        
//////        Antes de invocar a la función "set_vba_empleados", 
//////        se deben calcular las fechas de inicio y fin del actual período 
//////        para el empleado seleccionado o de todos los empleados del centro de costo.
//////        
//////                
//////                
//////       Generar un json con los datos del o los empleados seleccionados.
//////       Este json es param de entrada de la funcion 'set_vba_empleados'
//////       El metodo 'calculoDao.setVBANew' invoca a la función "set_vba_empleados"         
//////        
//////        ResultCRUDVO modifiedInfo = calculoDao.setVBANew(_empresaId, _cencoId);
//////        
//////        
//////        String msgFinal = modifiedInfo.getMsg();
//////        modifiedInfo.setMsg(msgFinal);
//////        _eventdata.setDescription(msgFinal);
//////        //insertar evento 
//////        eventsService.addEvent(_eventdata); 
//////        
//////        return modifiedInfo;
//////    }
//////    
    
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
