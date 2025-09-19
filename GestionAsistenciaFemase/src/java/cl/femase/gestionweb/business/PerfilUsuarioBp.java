/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.PerfilUsuarioVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Alexander
 */
public class PerfilUsuarioBp  extends BaseBp{

    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.PerfilUsuarioDAO perfilesDao;
    
    public PerfilUsuarioBp(PropertiesVO props) {
        this.props = props;
        eventsService = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        perfilesDao = new cl.femase.gestionweb.dao.PerfilUsuarioDAO(this.props);
    }

    /**
    * 
    * @return 
    */
    public HashMap<Integer, String> getPerfilesVigentes(){
        return perfilesDao.getPerfilesVigentes();
    }
    
    public List<PerfilUsuarioVO> getPerfiles(String _nombre,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<PerfilUsuarioVO> lista = 
            perfilesDao.getPerfiles(_nombre, _jtStartIndex, 
                _jtPageSize, _jtSorting);

        return lista;
    }

    /**
    * 
    * @param _usuario
    * @return 
    */
    public List<PerfilUsuarioVO> getPerfilesByUsuario(UsuarioVO _usuario){
        
        List<PerfilUsuarioVO> lista = 
            perfilesDao.getPerfilesByUsuario(_usuario);

        return lista;
    }
    
    public ResultCRUDVO update(PerfilUsuarioVO _perfilToUpdate, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO updValues = perfilesDao.update(_perfilToUpdate);
        
        //if (!updValues.isThereError()){
            String msgFinal = updValues.getMsg();
            updValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return updValues;
    }
    
//    public ResultCRUDVO delete(PerfilUsuarioVO _accesoToDelete, 
//            MaintenanceEventVO _eventdata){
//        ResultCRUDVO updValues = perfilesDao.delete(_accesoToDelete);
//        
//        //if (!updValues.isThereError()){
//            String msgFinal = updValues.getMsg();
//            updValues.setMsg(msgFinal);
//            _eventdata.setDescription(msgFinal);
//            //insertar evento 
//            eventsService.addEvent(_eventdata); 
//        //}
//        
//        return updValues;
//    }    
    
    public ResultCRUDVO insert(PerfilUsuarioVO _perfilToInsert, 
            MaintenanceEventVO _eventdata){
        
        ResultCRUDVO insValues = perfilesDao.insert(_perfilToInsert);
        
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
    
    public int getPerfilesCount(String _nombre){
        return perfilesDao.getPerfilesCount(_nombre);
    }

}
