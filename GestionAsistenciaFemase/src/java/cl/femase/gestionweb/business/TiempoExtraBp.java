/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.TiempoExtraVO;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Alexander
 */
public class TiempoExtraBp {

    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.TiempoExtraDAO texService;
    
    public TiempoExtraBp(PropertiesVO props) {
        this.props = props;
        eventsService = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        texService = new cl.femase.gestionweb.dao.TiempoExtraDAO(this.props);
    }

    public List<TiempoExtraVO> getTiemposExtra(String _rutEmpleado, boolean _todas){
        
        List<TiempoExtraVO> lista = 
            texService.getTiemposExtra(_rutEmpleado, _todas);

        return lista;
    }

    public TiempoExtraVO getTiempoExtra(String _rutEmpleado,
            String _fecha){
        TiempoExtraVO data = texService.getTiempoExtra(_rutEmpleado, _fecha);
        
        return data;
    }
    
    public MaintenanceVO update(TiempoExtraVO _objectToUpdate, 
            MaintenanceEventVO _eventdata){
        MaintenanceVO updValues = texService.update(_objectToUpdate);
        
        //if (!updValues.isThereError()){
            String msgFinal = updValues.getMsg();
            updValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return updValues;
    }
    
    public MaintenanceVO insert(TiempoExtraVO _objToInsert, 
            MaintenanceEventVO _eventdata){
        
        MaintenanceVO insValues = texService.insert(_objToInsert);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
    
    public MaintenanceVO delete(TiempoExtraVO _objToInsert, 
            MaintenanceEventVO _eventdata){
        
        MaintenanceVO insValues = texService.delete(_objToInsert);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
    
    public HashMap<String,Integer> getTiposHrasExtras(){
        
        HashMap<String,Integer> lista = 
            texService.getTiposHrasExtras();

        return lista;
    }
    
    
}
