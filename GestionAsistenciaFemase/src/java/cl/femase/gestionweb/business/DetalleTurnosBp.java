/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.DetalleTurnoVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author Alexander
 */
public class DetalleTurnosBp  extends BaseBp{

    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.DetalleTurnosDAO detalleturnosService;
    
    public DetalleTurnosBp(PropertiesVO props) {
        this.props = props;
        eventsService = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        detalleturnosService = new cl.femase.gestionweb.dao.DetalleTurnosDAO(this.props);
    }

    public List<DetalleTurnoVO> getDetalleTurno(int _idTurno,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<DetalleTurnoVO> lista = 
            detalleturnosService.getDetalleTurno(_idTurno, _jtStartIndex, 
                _jtPageSize, _jtSorting);

        return lista;
    }

    /**
     * 
     * @param _empresaId
     * @param _idTurno
     * @param _codDia
     * @return 
     */
    public DetalleTurnoVO getDetalleTurno(String _empresaId, 
            int _idTurno, 
            int _codDia){
        
        DetalleTurnoVO detalle 
            = detalleturnosService.getDetalleTurno(_empresaId, _idTurno, _codDia);

        return detalle;
    }
    
    /**
    * 
    * @param _idTurno
    * @return 
    */
    public LinkedHashMap<Integer,DetalleTurnoVO> getHashDetalleTurno(int _idTurno){
        
        LinkedHashMap<Integer,DetalleTurnoVO> lista = 
            detalleturnosService.getHashDetalleTurno(_idTurno);

        return lista;
    }
    
    /**
    * 
    * @param _turnoToUpdate
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO update(DetalleTurnoVO _turnoToUpdate, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO updValues = detalleturnosService.update(_turnoToUpdate);
        
        //if (!updValues.isThereError()){
            String msgFinal = updValues.getMsg();
            updValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return updValues;
    }
    
    public ResultCRUDVO delete(DetalleTurnoVO _turnoToDelete, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO deleteValues = detalleturnosService.delete(_turnoToDelete);
        
        //if (!updValues.isThereError()){
            String msgFinal = deleteValues.getMsg();
            deleteValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return deleteValues;
    }
    
    public ResultCRUDVO insert(DetalleTurnoVO _objToInsert, 
            MaintenanceEventVO _eventdata){
        
        ResultCRUDVO insValues = detalleturnosService.insert(_objToInsert);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
    
    public int getDetalleTurnoCount(int _idTurno){
        return detalleturnosService.getDetalleTurnoCount(_idTurno);
    }

}
