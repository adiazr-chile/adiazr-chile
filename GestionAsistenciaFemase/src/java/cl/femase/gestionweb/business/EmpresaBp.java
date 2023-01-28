/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.EmpresaVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.util.List;

/**
 *
 * @author Alexander
 */
public class EmpresaBp  extends BaseBp{

    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.EmpresaDAO empresaDao;
    
    public EmpresaBp(PropertiesVO props) {
        this.props = props;
        eventsService = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        empresaDao = new cl.femase.gestionweb.dao.EmpresaDAO(this.props);
    }

    /**
    * 
     * @param _usuario
     * @param _nombre
     * @param _jtStartIndex
     * @param _jtPageSize
     * @param _jtSorting
     * @return 
    */
    public List<EmpresaVO> getEmpresas(UsuarioVO _usuario, String _nombre,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<EmpresaVO> lista;
        if (_usuario.getIdPerfil() != Constantes.ID_PERFIL_SUPER_ADMIN){
            System.out.println(WEB_NAME+"[EmpresaBp.getEmpresas]"
                + "Obtener lista de empresas para usuario normal...");
            lista = 
               empresaDao.getEmpresas(_usuario, _nombre);
        }else {
            //perfil super admin
            System.out.println(WEB_NAME+"[EmpresaBp.getEmpresas]"
                + "Obtener lista de empresas para usuario super admin...");
            lista = 
                empresaDao.getEmpresas(_nombre, _jtStartIndex, 
                _jtPageSize, _jtSorting);
        }
        
        return lista;
    }
    
    /**
    *  Obtiene info de una empresa especifica
     * @param _empresaId
     * @return 
    */
    public EmpresaVO getEmpresaByKey(String _empresaId){
        return empresaDao.getEmpresaByKey(_empresaId);
    }

//    public List<EmpresaVO> getEmpresas(UsuarioVO _usuario, String _nombre){
//        List<EmpresaVO> lista = 
//            empresaService.getEmpresas(_usuario, _nombre);
//
//        return lista;
//    }
    

    public int getEmpresasCount(String _nombre){
        return empresaDao.getEmpresasCount(_nombre);
    }
    
    public ResultCRUDVO insert(EmpresaVO _objToInsert, 
            MaintenanceEventVO _eventdata){
        
        ResultCRUDVO insValues = empresaDao.insert(_objToInsert);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
    
    public ResultCRUDVO update(EmpresaVO _objectToUpdate, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO updValues = empresaDao.update(_objectToUpdate);
        
        //if (!updValues.isThereError()){
            String msgFinal = updValues.getMsg();
            updValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return updValues;
    }

}
