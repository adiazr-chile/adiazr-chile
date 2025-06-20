/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.AlertaSistemaVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.util.List;

/**
 *
 * @author Alexander
 */
public class AlertaSistemaBp  extends BaseBp{

    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.AlertaSistemaDAO alertasDao;
    
    /**
    * 
    * @param props
    */
    public AlertaSistemaBp(PropertiesVO props) {
        this.props = props;
        eventsService = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        alertasDao = new cl.femase.gestionweb.dao.AlertaSistemaDAO(this.props);
    }

    /**
    * 
    * @param _id
    * @return 
    */
    public AlertaSistemaVO getAlertaSistemaByKey(int _id){
        return alertasDao.getAlertaSistemaByKey(_id);
    }
    
    /**
    *
    * @param _empresaId
    * @param _titulo
    * @param _fecha
    * @return
    */
    public List<AlertaSistemaVO> getAlertas(String _empresaId, String _titulo, String _fecha){
        List<AlertaSistemaVO> lista = 
            alertasDao.getAlertas(_empresaId, _titulo, _fecha);
        return lista;
    }
    
    /**
    *
    * @param _empresaId
    * @return
    */
    public List<AlertaSistemaVO> getAlertasActivas(String _empresaId){
        List<AlertaSistemaVO> lista = 
            alertasDao.getAlertasActivas(_empresaId);
        return lista;
    }
    
//    public List<AlertaSistemaVO> getAlertas(String _empresaId, 
//            int _jtStartIndex, 
//            int _jtPageSize, 
//            String _jtSorting){
//        
//        List<AlertaSistemaVO> lista = 
//            alertasDao.getlertas(_empresaId, 
//                _jtStartIndex, 
//                _jtPageSize, _jtSorting);
//
//        return lista;
//    }

    /**
    * 
    * @param _alertaToUpdate
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO update(AlertaSistemaVO _alertaToUpdate, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO updValues = alertasDao.update(_alertaToUpdate);
        
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
    * @param _objToDelete
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO delete(AlertaSistemaVO _objToDelete, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO updValues = alertasDao.delete(_objToDelete);
        
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
    * @param _objToInsert
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO insert(AlertaSistemaVO _objToInsert, 
            MaintenanceEventVO _eventdata){
        
        ResultCRUDVO insValues = alertasDao.insert(_objToInsert);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
    
    
}
