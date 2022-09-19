/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
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

    public MaintenanceVO delete(PersonalEmpresaVO _personalToUpdate, 
            MaintenanceEventVO _eventdata){
        MaintenanceVO updValues = personaEmpresaService.delete(_personalToUpdate);
        
        //if (!updValues.isThereError()){
            String msgFinal = updValues.getMsg();
            updValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return updValues;
    }
    
//    public MaintenanceVO delete(PersonalEmpresaVO _personalToDelete, 
//            MaintenanceEventVO _eventdata){
//        MaintenanceVO updValues = personaEmpresaService.delete(_personalToDelete);
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
    
    public MaintenanceVO insert(PersonalEmpresaVO _personalToInsert, 
            MaintenanceEventVO _eventdata){
        
        MaintenanceVO insValues = personaEmpresaService.insert(_personalToInsert);
        
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
