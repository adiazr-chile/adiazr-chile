/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.dao.CodigoErrorRechazoDAO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.CodigoErrorRechazoVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.util.List;

/**
 *
 * @author Alexander
 */
public class CodigoErrorRechazoBp  extends BaseBp{

    /**
     *
     */
    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final CodigoErrorRechazoDAO codErrorRechazoDao;
    
    public CodigoErrorRechazoBp(PropertiesVO props) {
        this.props = props;
        eventsService = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        codErrorRechazoDao = new CodigoErrorRechazoDAO();
    }

    /**
    * 
    * @param _descripcion
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * @return 
    */
    public List<CodigoErrorRechazoVO> getCodigos(String _descripcion,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<CodigoErrorRechazoVO> lista = 
            codErrorRechazoDao.getCodigos(_descripcion, _jtStartIndex, 
                _jtPageSize, _jtSorting);

        return lista;
    }

    /**
    * 
    * @param _codigoToUpdate
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO update(CodigoErrorRechazoVO _codigoToUpdate, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO updValues = codErrorRechazoDao.update(_codigoToUpdate);
        
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
    * @param _codigoToDelete
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO delete(CodigoErrorRechazoVO _codigoToDelete, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO updValues = codErrorRechazoDao.delete(_codigoToDelete);
        
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
    * @param _codigoToInsert
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO insert(CodigoErrorRechazoVO _codigoToInsert, 
            MaintenanceEventVO _eventdata){
        
        ResultCRUDVO insValues = codErrorRechazoDao.insert(_codigoToInsert);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
    
    public int getCodigosCount(String _descripcion){
        return codErrorRechazoDao.getCodigosCount(_descripcion);
    }

}
