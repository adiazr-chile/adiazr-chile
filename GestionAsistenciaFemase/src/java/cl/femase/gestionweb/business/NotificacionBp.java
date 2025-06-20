/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.NotificacionVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.util.List;

/**
 *
 * @author Alexander
 */
public class NotificacionBp  extends BaseBp{

    /**
     *
     */
    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.NotificacionDAO notificacionService;
    
    public NotificacionBp(PropertiesVO props) {
        this.props = props;
        eventsService = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        notificacionService = new cl.femase.gestionweb.dao.NotificacionDAO(this.props);
    }
            
    /**
    * 
    * @param _objToInsert
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO insert(NotificacionVO _objToInsert, 
            MaintenanceEventVO _eventdata){
        
        ResultCRUDVO insValues = notificacionService.insert(_objToInsert);
        
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
