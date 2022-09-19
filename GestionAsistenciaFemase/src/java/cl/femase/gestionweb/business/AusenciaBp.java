/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.AusenciaVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Alexander
 */
public class AusenciaBp  extends BaseBp{

    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.AusenciaDAO ausenciasService;
    
    /**
     *
     * @param props
     */
    public AusenciaBp(PropertiesVO props) {
        this.props = props;
        eventsService = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        ausenciasService = new cl.femase.gestionweb.dao.AusenciaDAO(this.props);
    }

    /**
     * 
     * @param _nombre
     * @param _tipo
     * @param _estado
     * @param _justificaHoras
     * @param _pagadaPorEmpleador
     * @param _jtStartIndex
     * @param _jtPageSize
     * @param _jtSorting
     * @return 
     */
    public List<AusenciaVO> getAusencias(String _nombre,
            int _tipo,
            int _estado,
            String _justificaHoras,
            String _pagadaPorEmpleador,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<AusenciaVO> lista = 
            ausenciasService.getAusencias(_nombre, 
                _tipo, 
                _estado,
                _justificaHoras,
                _pagadaPorEmpleador,
                _jtStartIndex, 
                _jtPageSize, 
                _jtSorting);

        return lista;
    }
    
    public HashMap<Integer, String> getAusencias(){
        return ausenciasService.getAusencias();
    }

    public MaintenanceVO update(AusenciaVO _ausenciaToUpdate, 
            MaintenanceEventVO _eventdata){
        MaintenanceVO updValues = ausenciasService.update(_ausenciaToUpdate);
        
        //if (!updValues.isThereError()){
            String msgFinal = updValues.getMsg();
            updValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return updValues;
    }
    
    public MaintenanceVO insert(AusenciaVO _ausenciaToInsert, 
            MaintenanceEventVO _eventdata){
        
        MaintenanceVO insValues = ausenciasService.insert(_ausenciaToInsert);
        
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
    
    /**
    * 
     * @param _nombre
     * @param _tipo
     * @param _estado
     * @param _justificaHoras
     * @param _pagadaPorEmpleador
     * @return 
    */
    public int getAusenciasCount(String _nombre,
            int _tipo,
            int _estado,
            String _justificaHoras,
            String _pagadaPorEmpleador){
        return ausenciasService.getAusenciasCount(_nombre,
            _tipo,
            _estado,
            _justificaHoras,
            _pagadaPorEmpleador);
    }

}
