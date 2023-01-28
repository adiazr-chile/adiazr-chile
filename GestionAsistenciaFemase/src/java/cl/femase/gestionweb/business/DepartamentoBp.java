/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.DepartamentoVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.util.List;

/**
 *
 * @author Alexander
 */
public class DepartamentoBp  extends BaseBp{

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

    public ResultCRUDVO update(DepartamentoVO _accesoToUpdate, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO updValues = deptosDao.update(_accesoToUpdate);
        
        //if (!updValues.isThereError()){
            String msgFinal = updValues.getMsg();
            updValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return updValues;
    }
    
    public ResultCRUDVO delete(DepartamentoVO _objToDelete, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO updValues = deptosDao.delete(_objToDelete);
        
        //if (!updValues.isThereError()){
            String msgFinal = updValues.getMsg();
            updValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return updValues;
    }    
    
    public ResultCRUDVO insert(DepartamentoVO _objToInsert, 
            MaintenanceEventVO _eventdata){
        
        ResultCRUDVO insValues = deptosDao.insert(_objToInsert);
        
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
