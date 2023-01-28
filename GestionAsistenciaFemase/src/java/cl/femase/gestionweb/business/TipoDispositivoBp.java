/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.TipoDispositivoVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.util.List;

/**
 *
 * @author Alexander
 */
public class TipoDispositivoBp  extends BaseBp{

    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.TipoDispositivoDAO tiposDispositivosService;
    
    public TipoDispositivoBp(PropertiesVO props) {
        this.props = props;
        eventsService = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        tiposDispositivosService = new cl.femase.gestionweb.dao.TipoDispositivoDAO(this.props);
    }

    public List<TipoDispositivoVO> getTipos(String _nombre,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<TipoDispositivoVO> lista = 
            tiposDispositivosService.getTipos(_nombre, _jtStartIndex, 
                _jtPageSize, _jtSorting);

        return lista;
    }

    public ResultCRUDVO update(TipoDispositivoVO _objToUpdate, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO updValues = tiposDispositivosService.update(_objToUpdate);
        
        //if (!updValues.isThereError()){
            String msgFinal = updValues.getMsg();
            updValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return updValues;
    }
    
    public ResultCRUDVO delete(TipoDispositivoVO _objToDelete, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO updValues = tiposDispositivosService.delete(_objToDelete);
        
        //if (!updValues.isThereError()){
            String msgFinal = updValues.getMsg();
            updValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return updValues;
    }    
    
    public ResultCRUDVO insert(TipoDispositivoVO _objToInsert, 
            MaintenanceEventVO _eventdata){
        
        ResultCRUDVO insValues = tiposDispositivosService.insert(_objToInsert);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
    
    public int getTiposCount(String _nombre){
        return tiposDispositivosService.getTiposCount(_nombre);
    }

}
