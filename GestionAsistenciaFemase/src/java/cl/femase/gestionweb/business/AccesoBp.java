/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.AccesoVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.util.List;

/**
 *
 * @author Alexander
 */
public class AccesoBp  extends BaseBp{

    /**
     *
     */
    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.AccesoDAO accesosService;
    
    public AccesoBp(PropertiesVO props) {
        this.props = props;
        eventsService = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        accesosService = new cl.femase.gestionweb.dao.AccesoDAO();
    }

    public List<AccesoVO> getAccesos(String _label,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<AccesoVO> lista = 
                accesosService.getAccesos(_label, _jtStartIndex, 
                        _jtPageSize, _jtSorting);

        return lista;
    }

    public ResultCRUDVO update(AccesoVO _accesoToUpdate, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO updValues = accesosService.update(_accesoToUpdate);
        
        //if (!updValues.isThereError()){
            String msgFinal = updValues.getMsg();
            updValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return updValues;
    }
    
    public ResultCRUDVO delete(AccesoVO _accesoToDelete, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO updValues = accesosService.delete(_accesoToDelete);
        
        //if (!updValues.isThereError()){
            String msgFinal = updValues.getMsg();
            updValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return updValues;
    }    
    
    public ResultCRUDVO insert(AccesoVO _accesoToInsert, 
            MaintenanceEventVO _eventdata){
        
        ResultCRUDVO insValues = accesosService.insert(_accesoToInsert);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
    
    /*
    public ResultCRUDVO delete(ContractRelationVO _relationToDelete, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO insValues = contractRelService.delete(_relationToDelete);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
    */
    
    public int getAccesosCount(String _label){
        return accesosService.getAccesosCount(_label);
    }

}
