/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.CargoVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.util.List;

/**
 *
 * @author Alexander
 */
public class CargoBp  extends BaseBp{

    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.CargoDAO cargosDao;
    
    public CargoBp(PropertiesVO props) {
        this.props = props;
        eventsService = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        cargosDao = new cl.femase.gestionweb.dao.CargoDAO(this.props);
    }

    public CargoVO getCargoByKey(int _cargoId){
        return cargosDao.getCargoByKey(_cargoId);
    }
    
    public List<CargoVO> getCargos(String _nombre,int _estado,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<CargoVO> lista = 
            cargosDao.getCargos(_nombre, _estado, _jtStartIndex, 
                _jtPageSize, _jtSorting);

        return lista;
    }

    /**
    * 
    * @param _objectToUpdate
    * @param _eventdata
    * @return 
    */
    public MaintenanceVO update(CargoVO _objectToUpdate, 
            MaintenanceEventVO _eventdata){
        MaintenanceVO updValues = cargosDao.update(_objectToUpdate);
        
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
    public MaintenanceVO insert(CargoVO _objToInsert, 
            MaintenanceEventVO _eventdata){
        
        MaintenanceVO insValues = cargosDao.insert(_objToInsert);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
    
    public int getCargosCount(String _nombre, int _estado){
        return cargosDao.getCargosCount(_nombre, _estado);
    }

}
