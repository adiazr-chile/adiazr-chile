/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.DispositivoCentroCostoVO;
import cl.femase.gestionweb.vo.DispositivoDepartamentoVO;
import cl.femase.gestionweb.vo.DispositivoEmpresaVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
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

    public MaintenanceVO insertAsignacionEmpresa(DispositivoEmpresaVO _data, 
            MaintenanceEventVO _eventdata){
        
        MaintenanceVO insValues = asignacionDispositivoService.insertAsignacionEmpresa(_data);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
    
    public MaintenanceVO insertAsignacionDepartamento(DispositivoDepartamentoVO _data, 
            MaintenanceEventVO _eventdata){
        
        MaintenanceVO insValues = asignacionDispositivoService.insertAsignacionDepartamento(_data);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
    
    public MaintenanceVO insertAsignacionCentroCosto(DispositivoCentroCostoVO _data, 
            MaintenanceEventVO _eventdata){
        
        MaintenanceVO insValues = asignacionDispositivoService.insertAsignacionCentroCosto(_data);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
    
    public MaintenanceVO deleteAsignacionesCentroCosto(String _deviceId, 
            MaintenanceEventVO _eventdata){
        
        MaintenanceVO insValues = asignacionDispositivoService.deleteAsignacionesCentroCosto(_deviceId);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
    
    public MaintenanceVO deleteAsignacionesDepartamento(String _deviceId, 
            MaintenanceEventVO _eventdata){
        
        MaintenanceVO insValues = asignacionDispositivoService.deleteAsignacionesDepartamento(_deviceId);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
    
    public MaintenanceVO deleteAsignacionesEmpresa(String _deviceId, 
            MaintenanceEventVO _eventdata){
        
        MaintenanceVO insValues = asignacionDispositivoService.deleteAsignacionesEmpresa(_deviceId);
        
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
