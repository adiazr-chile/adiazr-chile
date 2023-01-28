/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.PersonalEmpresaVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.util.List;

/**
 *
 * @author Alexander
 * @deprecated 
 */
public class PersonalEmpresaBp  extends BaseBp{

    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.PersonalEmpresaDAO personaEmpresaService;
    
    public PersonalEmpresaBp(PropertiesVO props) {
        this.props = props;
        eventsService = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        personaEmpresaService = new cl.femase.gestionweb.dao.PersonalEmpresaDAO(this.props);
    }

    public List<PersonalEmpresaVO> getPersonal(String _empresaId,
            String _deptoId,
            int _cencostoId,
            String _rutEmpleado,
            String _nombresEmpleado,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<PersonalEmpresaVO> lista = 
            personaEmpresaService.getPersonal(_empresaId, 
                _deptoId, 
                _cencostoId,
                _rutEmpleado,
                _nombresEmpleado,
                _jtStartIndex, 
                _jtPageSize, _jtSorting);

        return lista;
    }

    public ResultCRUDVO delete(PersonalEmpresaVO _personalToUpdate, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO updValues = personaEmpresaService.delete(_personalToUpdate);
        
        //if (!updValues.isThereError()){
            String msgFinal = updValues.getMsg();
            updValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return updValues;
    }
    
//    public ResultCRUDVO delete(PersonalEmpresaVO _personalToDelete, 
//            MaintenanceEventVO _eventdata){
//        ResultCRUDVO updValues = personaEmpresaService.delete(_personalToDelete);
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
    
    public ResultCRUDVO insert(PersonalEmpresaVO _personalToInsert, 
            MaintenanceEventVO _eventdata){
        
        ResultCRUDVO insValues = personaEmpresaService.insert(_personalToInsert);
        
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
    
    public int getPersonalCount(String _empresaId,
            String _deptoId,
            int _cencostoId,
            String _rutEmpleado,
            String _nombresEmpleado){
        return personaEmpresaService.getPersonalCount(_empresaId, _deptoId, 
                _cencostoId, _rutEmpleado,
                _nombresEmpleado);
    }

}
