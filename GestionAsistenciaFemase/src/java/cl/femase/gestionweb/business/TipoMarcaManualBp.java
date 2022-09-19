/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.TipoMarcaManualVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Alexander
 */
public class TipoMarcaManualBp  extends BaseBp{

    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.TipoMarcaManualDAO tpmanualDao;
    
    public TipoMarcaManualBp(PropertiesVO props) {
        this.props = props;
        eventsService = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        tpmanualDao = new cl.femase.gestionweb.dao.TipoMarcaManualDAO(this.props);
    }

    public List<TipoMarcaManualVO> getTipos(String _nombre,
            String _vigente,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        return tpmanualDao.getTipos(_nombre, _vigente, _jtStartIndex, _jtPageSize, _jtSorting);
    }
    
    public HashMap<Integer,TipoMarcaManualVO> getHashTipos(){
        
        return tpmanualDao.getHashTipos();
    }
    
    public List<TipoMarcaManualVO> getTipos(String _nombre,
            String _vigente){
        
        return tpmanualDao.getTipos(_nombre, _vigente);
    }

    public int getTiposCount(String _nombre, String _vigente){
        return tpmanualDao.getTiposCount(_nombre, _vigente);
    }
    
    public MaintenanceVO insert(TipoMarcaManualVO _objToInsert, 
            MaintenanceEventVO _eventdata){
        
        MaintenanceVO insValues = tpmanualDao.insert(_objToInsert);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
    
    public MaintenanceVO update(TipoMarcaManualVO _objectToUpdate, 
            MaintenanceEventVO _eventdata){
        MaintenanceVO updValues = tpmanualDao.update(_objectToUpdate);
        
        //if (!updValues.isThereError()){
            String msgFinal = updValues.getMsg();
            updValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return updValues;
    }

}
