/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
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

    public MaintenanceVO update(ModuloSistemaVO _moduleToUpdate, 
            MaintenanceEventVO _eventdata){
        MaintenanceVO updValues = modulosService.update(_moduleToUpdate);
        
        //if (!updValues.isThereError()){
            String msgFinal = updValues.getMsg();
            updValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return updValues;
    }
    
    public MaintenanceVO insert(ModuloSistemaVO _moduleToInsert, 
            MaintenanceEventVO _eventdata){
        
        MaintenanceVO insValues = modulosService.insert(_moduleToInsert);
        
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
    public MaintenanceVO delete(ContractRelationVO _relationToDelete, 
            MaintenanceEventVO _eventdata){
        MaintenanceVO insValues = contractRelService.delete(_relationToDelete);
        
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
