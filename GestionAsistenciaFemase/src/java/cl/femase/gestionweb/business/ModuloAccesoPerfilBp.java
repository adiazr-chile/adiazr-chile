/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.ModuloAccesoPerfilVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.util.LinkedHashMap;

/**
 *
 * @author Alexander
 */
public class ModuloAccesoPerfilBp  extends BaseBp{

    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    
    private final cl.femase.gestionweb.dao.ModuloAccesoPerfilDAO accesosPerfilService;
    
    public ModuloAccesoPerfilBp(PropertiesVO props) {
        this.props = props;
        eventsService = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        accesosPerfilService = new cl.femase.gestionweb.dao.ModuloAccesoPerfilDAO(this.props);
    }

    public LinkedHashMap<String,ModuloAccesoPerfilVO> getAccesosByModuloPerfil(int _moduloId,int _perfilUsuario){
        
        LinkedHashMap<String,ModuloAccesoPerfilVO> lista = 
                accesosPerfilService.getAccesosByModuloPerfil(_perfilUsuario, _moduloId);

        return lista;
    }

    public LinkedHashMap<String,String> getSoloAccesosKeyByModuloPerfil(int _moduloId,int _perfilUsuario){
        
        LinkedHashMap<String,String> lista = 
            accesosPerfilService.getSoloAccesosKeyByModuloPerfil(_perfilUsuario, _moduloId);

        return lista;
    }
    
    public ResultCRUDVO delete(ModuloAccesoPerfilVO _objToUpdate, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO updValues = accesosPerfilService.delete(_objToUpdate);
        
        //if (!updValues.isThereError()){
            String msgFinal = updValues.getMsg();
            updValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return updValues;
    }
    
    public ResultCRUDVO insert(ModuloAccesoPerfilVO _objToInsert, 
            MaintenanceEventVO _eventdata){
        
        ResultCRUDVO insValues = accesosPerfilService.insert(_objToInsert);
        
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
   
}
