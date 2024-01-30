/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.ModuloSistemaVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.util.List;

/**
 *
 * @author Alexander
 */
public class ModulosSistemaBp  extends BaseBp{

    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    
    private final cl.femase.gestionweb.dao.ModulosSistemaDAO modulosService;
    
    public ModulosSistemaBp(PropertiesVO props) {
        this.props = props;
        eventsService = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        modulosService = new cl.femase.gestionweb.dao.ModulosSistemaDAO(this.props);
    }

    public List<ModuloSistemaVO> getModulosSistema(String _nombre,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<ModuloSistemaVO> lista = 
                modulosService.getModulosSistema(_nombre, _jtStartIndex, 
                        _jtPageSize, _jtSorting);

        return lista;
    }

    public ResultCRUDVO update(ModuloSistemaVO _moduleToUpdate, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO updValues = modulosService.update(_moduleToUpdate);
        
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
    * @param _moduleToInsert
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO insert(ModuloSistemaVO _moduleToInsert, 
            MaintenanceEventVO _eventdata){
        
        ResultCRUDVO insValues = modulosService.insert(_moduleToInsert);
        
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
    
    public int getModulosSistemaCount(String _nombre){
        return modulosService.getModulosSistemaCount(_nombre);
    }

}
