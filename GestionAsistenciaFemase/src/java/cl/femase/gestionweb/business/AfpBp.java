/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.AfpVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.util.List;

/**
*
* @author Alexander
*/
public class AfpBp  extends BaseBp{

    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.AfpDAO afpService;
    
    public AfpBp(PropertiesVO props) {
        this.props = props;
        eventsService = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        afpService = new cl.femase.gestionweb.dao.AfpDAO(this.props);
    }

    public List<AfpVO> getAfps(String _nombre,int _estado,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<AfpVO> lista = 
            afpService.getAfps(_nombre, _estado, _jtStartIndex, 
                _jtPageSize, _jtSorting);

        return lista;
    }

    /**
    * 
    * @param _objectToUpdate
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO update(AfpVO _objectToUpdate, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO updValues = afpService.update(_objectToUpdate);
        
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
    * @param _objToInsert
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO insert(AfpVO _objToInsert, 
            MaintenanceEventVO _eventdata){
        
        ResultCRUDVO insValues = afpService.insert(_objToInsert);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
    
    public int getAfpsCount(String _nombre, int _estado){
        return afpService.getAfpsCount(_nombre, _estado);
    }

}
