/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.TurnoRotativoCicloVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.util.List;

/**
 *
 * @author Alexander
 */
public class TurnoRotativoCicloBp  extends BaseBp{

    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.TurnoRotativoCicloDAO ciclosDao;
    
    public TurnoRotativoCicloBp(PropertiesVO props) {
        this.props = props;
        eventsService = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        ciclosDao = new cl.femase.gestionweb.dao.TurnoRotativoCicloDAO();
    }

    
    public List<TurnoRotativoCicloVO> getCiclos(String _empresaId, 
            int _numDias,
            String _etiqueta,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<TurnoRotativoCicloVO> lista = 
            ciclosDao.getCiclos(_empresaId, _numDias, _etiqueta, _jtStartIndex, _jtPageSize, _jtSorting);

        return lista;
    }

    /**
    * 
    * @param _objectToUpdate
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO update(TurnoRotativoCicloVO _objectToUpdate, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO updValues = ciclosDao.update(_objectToUpdate);
        
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
    public ResultCRUDVO delete(TurnoRotativoCicloVO _objectToUpdate, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO updValues = ciclosDao.delete(_objectToUpdate);
        
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
    public ResultCRUDVO insert(TurnoRotativoCicloVO _objToInsert, 
            MaintenanceEventVO _eventdata){
        
        ResultCRUDVO insValues = ciclosDao.insert(_objToInsert);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
    
    public int getCiclosCount(String _empresaId, 
            int _numDias,
            String _etiqueta){
        return ciclosDao.getCiclosCount(_empresaId, _numDias, _etiqueta);
    }

}
