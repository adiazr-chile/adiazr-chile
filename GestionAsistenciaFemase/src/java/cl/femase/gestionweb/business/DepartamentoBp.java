/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.DepartamentoVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.util.List;

/**
 *
 * @author Alexander
 */
public class DepartamentoBp {

    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.DepartamentoDAO deptosDao;
    
    public DepartamentoBp(PropertiesVO props) {
        this.props = props;
        eventsService = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        deptosDao = new cl.femase.gestionweb.dao.DepartamentoDAO(this.props);
    }

    public DepartamentoVO getDepartamentoByKey(String _deptoId){
        return deptosDao.getDepartamentoByKey(_deptoId);
    }
    
    /**
     *
     * @param _usuario
     * @param _empresaId
     * @return
     */
    public List<DepartamentoVO> getDepartamentosEmpresa(UsuarioVO _usuario, 
            String _empresaId){
        List<DepartamentoVO> lista = 
            deptosDao.getDepartamentosEmpresa(_usuario, _empresaId);
        return lista;
    }
    
    public List<DepartamentoVO> getDepartamentos(String _empresaId, 
            String _nombre,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<DepartamentoVO> lista = 
            deptosDao.getDepartamentos(_empresaId, 
                _nombre, _jtStartIndex, 
                _jtPageSize, _jtSorting);

        return lista;
    }

    public MaintenanceVO update(DepartamentoVO _accesoToUpdate, 
            MaintenanceEventVO _eventdata){
        MaintenanceVO updValues = deptosDao.update(_accesoToUpdate);
        
        //if (!updValues.isThereError()){
            String msgFinal = updValues.getMsg();
            updValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return updValues;
    }
    
    public MaintenanceVO delete(DepartamentoVO _objToDelete, 
            MaintenanceEventVO _eventdata){
        MaintenanceVO updValues = deptosDao.delete(_objToDelete);
        
        //if (!updValues.isThereError()){
            String msgFinal = updValues.getMsg();
            updValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return updValues;
    }    
    
    public MaintenanceVO insert(DepartamentoVO _objToInsert, 
            MaintenanceEventVO _eventdata){
        
        MaintenanceVO insValues = deptosDao.insert(_objToInsert);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
    
    public int getDepartamentosCount(String _empresaId, 
            String _nombre){
        return deptosDao.getDepartamentosCount(_empresaId, 
            _nombre);
    }

}
