package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.PropertiesVO;

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
            int _diasAdicionales, 
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
