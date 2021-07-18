    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.VacacionesVO;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 *
 * @author Alexander
 */
public class VacacionesLogBp {

    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.VacacionesLogDAO vacacioneslogdao;
    private final SimpleDateFormat m_sdf = new SimpleDateFormat("yyyy-MM-dd");
    
    public VacacionesLogBp(PropertiesVO props) {
        this.props = props;
        eventsService = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        vacacioneslogdao = new cl.femase.gestionweb.dao.VacacionesLogDAO(this.props);
    }

    /**
    * 
    * @param _empresaId
    * @param _rutEmpleado
    * @param _startDate
    * @param _endDate
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * @return 
    */
    public List<VacacionesVO> getInfoVacacionesLog(String _empresaId, 
            String _rutEmpleado,
            String _startDate,
            String _endDate,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<VacacionesVO> lista = 
            vacacioneslogdao.getInfoVacacionesLog(_empresaId, _rutEmpleado, 
                _startDate, _endDate, 
                _jtStartIndex, _jtPageSize, _jtSorting);

        return lista;
    }
    
    /**
    * 
    * @param _objToInsert
    * @return 
    */
    public MaintenanceVO insert(VacacionesVO _objToInsert){
        
        MaintenanceVO insValues = vacacioneslogdao.insert(_objToInsert);
        
//        //if (!updValues.isThereError()){
//            String msgFinal = insValues.getMsg();
//            insValues.setMsg(msgFinal);
//            _eventdata.setDescription(msgFinal);
//            //insertar evento 
//            eventsService.addEvent(_eventdata); 
//        //}
        
        return insValues;
    }
    
    /**
    * 
    * @param _empresaId
    * @param _rutEmpleado
    * @param _startDate
    * @param _endDate
    * @return 
    */
    public int getInfoVacacionesLogCount(String _empresaId, 
            String _rutEmpleado,
            String _startDate,
            String _endDate){
        return vacacioneslogdao.getInfoVacacionesLogCount(_empresaId, 
            _rutEmpleado, _startDate, _endDate);
    }
    
    
    
    public void openDbConnection(){
        vacacioneslogdao.openDbConnection();
    }
    public void closeDbConnection(){
        vacacioneslogdao.closeDbConnection();
    }

}
