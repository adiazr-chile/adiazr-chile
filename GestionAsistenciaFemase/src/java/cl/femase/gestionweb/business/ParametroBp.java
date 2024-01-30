/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.ParametroVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Alexander
 */
public class ParametroBp  extends BaseBp{

    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.ParametroDAO parametrosService;
    
    public ParametroBp(PropertiesVO props) {
        this.props = props;
        eventsService = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        parametrosService = new cl.femase.gestionweb.dao.ParametroDAO(this.props);
    }

    /**
    * 
    * @param _empresaId
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * @return 
    */
    public List<ParametroVO> getParametros(String _empresaId,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<ParametroVO> lista = 
            parametrosService.getParametros(_empresaId, _jtStartIndex, 
                _jtPageSize, _jtSorting);

        return lista;
    }

    
    /**
    * Retorna todos los parametros definidos para la empresa especificada
    * @param _empresaId
    * @return 
    */
    public HashMap<String, Double> getParametrosEmpresa(String _empresaId){
        return parametrosService.getParametrosEmpresa(_empresaId);
    }
    
    
    /**
    * Obtiene parametro by code
    * 
    * @param _empresaId
    * @param _paramCode
    * 
    * @return 
    */
    public ParametroVO getParametroByKey(String _empresaId,String _paramCode){
        
        ParametroVO parametro = 
            parametrosService.getParametroByKey(_empresaId, _paramCode);

        return parametro;
    }
    
    /**
    * 
    * @param _objectToUpdate
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO update(ParametroVO _objectToUpdate, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO updValues = parametrosService.update(_objectToUpdate);
        
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
    * @param _objectToUpdate
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO delete(ParametroVO _objectToUpdate, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO updValues = parametrosService.delete(_objectToUpdate);
        
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
    public ResultCRUDVO insert(ParametroVO _objToInsert, 
            MaintenanceEventVO _eventdata){
        
        ResultCRUDVO insValues = parametrosService.insert(_objToInsert);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
    
    public int getParametrosCount(String _empresaId){
        return parametrosService.getParametrosCount(_empresaId);
    }

}
