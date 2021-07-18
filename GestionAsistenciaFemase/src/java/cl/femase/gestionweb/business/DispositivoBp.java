/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.DispositivoEmpresaVO;
import cl.femase.gestionweb.vo.DispositivoVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.util.List;

/**
 *
 * @author Alexander
 */
public class DispositivoBp {

    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsDao;
    private final cl.femase.gestionweb.dao.DispositivoDAO dispositivosDao;
    private final cl.femase.gestionweb.dao.AsignacionDispositivoDAO asignacionDispositivosDao;
    
    public DispositivoBp(PropertiesVO props) {
        this.props = props;
        eventsDao                   = 
            new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        dispositivosDao             = 
            new cl.femase.gestionweb.dao.DispositivoDAO(this.props);
        asignacionDispositivosDao   = 
            new cl.femase.gestionweb.dao.AsignacionDispositivoDAO(this.props);
    }

    /**
    * 
     * @param _id
     * @param _tipo
     * @param _estado
     * @param _fechaIngreso
     * @param _jtStartIndex
     * @param _jtPageSize
     * @param _jtSorting
     * @return 
    */
    public List<DispositivoVO> getDispositivos(String _id,int _tipo,
            int _estado,
            String _fechaIngreso,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<DispositivoVO> lista = 
            dispositivosDao.getDispositivos(_id,_tipo, _estado, 
                _fechaIngreso, _jtStartIndex,
                _jtPageSize, _jtSorting);

        return lista;
    }

    public MaintenanceVO update(DispositivoVO _dispositivoToUpdate, 
            MaintenanceEventVO _eventdata){
        MaintenanceVO updValues = dispositivosDao.update(_dispositivoToUpdate);
        
        //if (!updValues.isThereError()){
            String msgFinal = updValues.getMsg();
            updValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsDao.addEvent(_eventdata); 
        //}
        
        return updValues;
    }
    
    public MaintenanceVO insert(DispositivoVO _objToInsert, 
            MaintenanceEventVO _eventdata){
        
        MaintenanceVO insValues = dispositivosDao.insert(_objToInsert);
        
        //insertar asignacion empresa
        DispositivoEmpresaVO asignacionEmpresa = 
            new DispositivoEmpresaVO(_objToInsert.getId(),"emp01");
        asignacionDispositivosDao.insertAsignacionEmpresa(asignacionEmpresa);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsDao.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
    
    /**
     *
     * @param _tipo
     * @param _estado
     * @param _fechaIngreso
     * @return
     */
    public int getDispositivosCount(int _tipo,
            int _estado,
            String _fechaIngreso){
        return dispositivosDao.getDispositivosCount(_tipo, 
            _estado, _fechaIngreso);
    }

    public void openDbConnection(){
        dispositivosDao.openDbConnection();
    }
    public void closeDbConnection(){
        dispositivosDao.closeDbConnection();
    }
}
