/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.TipoDispositivoVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.util.List;

/**
 *
 * @author Alexander
 */
public class TipoDispositivoBp {

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

    public MaintenanceVO update(TipoDispositivoVO _objToUpdate, 
            MaintenanceEventVO _eventdata){
        MaintenanceVO updValues = tiposDispositivosService.update(_objToUpdate);
        
        //if (!updValues.isThereError()){
            String msgFinal = updValues.getMsg();
            updValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return updValues;
    }
    
    public MaintenanceVO delete(TipoDispositivoVO _objToDelete, 
            MaintenanceEventVO _eventdata){
        MaintenanceVO updValues = tiposDispositivosService.delete(_objToDelete);
        
        //if (!updValues.isThereError()){
            String msgFinal = updValues.getMsg();
            updValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return updValues;
    }    
    
    public MaintenanceVO insert(TipoDispositivoVO _objToInsert, 
            MaintenanceEventVO _eventdata){
        
        MaintenanceVO insValues = tiposDispositivosService.insert(_objToInsert);
        
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
