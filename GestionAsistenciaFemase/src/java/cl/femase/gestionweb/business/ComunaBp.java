/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.ComunaVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.util.List;

/**
 *
 * @author Alexander
 */
public class ComunaBp  extends BaseBp{

    /**
     *
     */
    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.ComunaDAO comunasService;
    
    public ComunaBp(PropertiesVO props) {
        this.props = props;
        eventsService = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        comunasService = new cl.femase.gestionweb.dao.ComunaDAO();
    }

    public List<ComunaVO> getComunas(String _nombreComuna,
            int _regionId,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<ComunaVO> lista = 
            comunasService.getComunas(_nombreComuna, _regionId, _jtStartIndex, 
                        _jtPageSize, _jtSorting);

        return lista;
    }

    /**
    * 
    * @param _objectToUpdate
    * @param _eventdata
    * @return 
    */
    public MaintenanceVO update(ComunaVO _objectToUpdate, 
            MaintenanceEventVO _eventdata){
        MaintenanceVO updValues = comunasService.update(_objectToUpdate);
        
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
    public MaintenanceVO insert(ComunaVO _objToInsert, 
            MaintenanceEventVO _eventdata){
        
        MaintenanceVO insValues = comunasService.insert(_objToInsert);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
    
    public int getComunasCount(String _nombreComuna,
            int _regionId){
        return comunasService.getComunasCount(_nombreComuna,_regionId);
    }

}
