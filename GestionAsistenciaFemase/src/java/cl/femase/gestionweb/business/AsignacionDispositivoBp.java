/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.DispositivoCentroCostoVO;
import cl.femase.gestionweb.vo.DispositivoDepartamentoVO;
import cl.femase.gestionweb.vo.DispositivoEmpresaVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.PropertiesVO;

/**
 *
 * @author Alexander
 */
public class AsignacionDispositivoBp  extends BaseBp{

    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.AsignacionDispositivoDAO asignacionDispositivoService;
    
    /**
     *
     * @param props
     */
    public AsignacionDispositivoBp(PropertiesVO props) {
        this.props = props;
        eventsService = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        asignacionDispositivoService = new cl.femase.gestionweb.dao.AsignacionDispositivoDAO(this.props);
    }

    public ResultCRUDVO insertAsignacionEmpresa(DispositivoEmpresaVO _data, 
            MaintenanceEventVO _eventdata){
        
        ResultCRUDVO insValues = asignacionDispositivoService.insertAsignacionEmpresa(_data);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
    
    public ResultCRUDVO insertAsignacionDepartamento(DispositivoDepartamentoVO _data, 
            MaintenanceEventVO _eventdata){
        
        ResultCRUDVO insValues = asignacionDispositivoService.insertAsignacionDepartamento(_data);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
    
    public ResultCRUDVO insertAsignacionCentroCosto(DispositivoCentroCostoVO _data, 
            MaintenanceEventVO _eventdata){
        
        ResultCRUDVO insValues = asignacionDispositivoService.insertAsignacionCentroCosto(_data);
        
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
    * 
    * @param _deviceId
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO deleteAsignacionesCentroCosto(String _deviceId, 
            MaintenanceEventVO _eventdata){
        
        ResultCRUDVO insValues = asignacionDispositivoService.deleteAsignacionesCentroCosto(_deviceId);
        
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
    * 
    * @param _cencoId
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO deleteAsignacionesCentroCosto(int _cencoId, 
            MaintenanceEventVO _eventdata){
        
        ResultCRUDVO insValues = asignacionDispositivoService.deleteAsignacionesCentroCosto(_cencoId);
        
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
    * 
    * @param _deviceId
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO deleteAsignacionesDepartamento(String _deviceId, 
            MaintenanceEventVO _eventdata){
        
        ResultCRUDVO insValues = asignacionDispositivoService.deleteAsignacionesDepartamento(_deviceId);
        
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
    * 
    * @param _deviceId
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO deleteAsignacionesEmpresa(String _deviceId, 
            MaintenanceEventVO _eventdata){
        
        ResultCRUDVO insValues = asignacionDispositivoService.deleteAsignacionesEmpresa(_deviceId);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
   
}
