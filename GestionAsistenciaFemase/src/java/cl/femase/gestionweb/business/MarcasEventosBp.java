/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.MarcasEventosVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.util.List;

/**
 *
 * @author Alexander
 */
public class MarcasEventosBp  extends BaseBp{

    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.MarcasEventosDAO marcasEventosService;
    
    public MarcasEventosBp() {
        eventsService = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        marcasEventosService = new cl.femase.gestionweb.dao.MarcasEventosDAO(this.props);
    }
    
    public MarcasEventosBp(PropertiesVO props) {
        this.props          = props;
        eventsService       = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        marcasEventosService   = new cl.femase.gestionweb.dao.MarcasEventosDAO(this.props);
    }
    
    public List<MarcasEventosVO> getMarcasEventos(String _empresaId,
            String _deptoId,
            int _cencoId,
            String _codDispositivo,
            String _rutEmpleado, 
            String _startDate, 
            String _endDate,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<MarcasEventosVO> lista = 
            marcasEventosService.getMarcasEventos(_empresaId,
                _deptoId,
                _cencoId,
                _codDispositivo,
                _rutEmpleado,  
                _startDate, _endDate, _jtStartIndex, 
                _jtPageSize, _jtSorting);
        return lista;
    }
    
    /**
     *
     * @param _empresaId
     * @param _rutEmpleado
     * @param _fechaMarca
     * @param _tipoMarca
     * @param _jtStartIndex
     * @param _jtPageSize
     * @param _jtSorting
     * @return
     */
    public List<MarcasEventosVO> getEventos(String _empresaId,
            String _rutEmpleado,
            String _fechaMarca,int _tipoMarca,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<MarcasEventosVO> lista = 
            marcasEventosService.getEventos(_empresaId, _rutEmpleado, _fechaMarca, _tipoMarca,
                    _jtStartIndex, _jtPageSize, _jtSorting);
        return lista;
    }
    
    public int getMarcasModificadasCount(String _empresaId,
            String _deptoId,
            int _cencoId,
            String _codDispositivo,
            String _rutEmpleado, 
            String _startDate, 
            String _endDate){
        
        return marcasEventosService.getMarcasEventosCount(_empresaId,
            _deptoId,
            _cencoId,
            _codDispositivo,
            _rutEmpleado, 
            _startDate, 
            _endDate);
    }
    
    public int getEventosCount(String _empresaId,
            String _rutEmpleado,
            String _fechaMarca,
            int _tipoMarca){
        
        return marcasEventosService.getEventosCount(_empresaId,
            _rutEmpleado, 
            _fechaMarca,
            _tipoMarca);
    }
    
    /**
     * Inserta una marca modificada desde administrador de marcas
     * @param _objToInsert
     * @param _eventdata
     * @return 
     */
    public ResultCRUDVO insert(MarcasEventosVO _objToInsert, 
            MaintenanceEventVO _eventdata){
        
        ResultCRUDVO insValues = marcasEventosService.insert(_objToInsert);
        
        if (!insValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        }
        
        return insValues;
    }

}
