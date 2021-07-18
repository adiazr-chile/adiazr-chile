    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.MarcacionVirtualVO;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alexander
 */
public class MarcacionVirtualBp {

    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.MarcacionVirtualDAO marcacionvirtualDao;
    private final SimpleDateFormat m_sdf = new SimpleDateFormat("yyyy-MM-dd");
    
    public MarcacionVirtualBp(PropertiesVO props) {
        this.props = props;
        eventsService = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        marcacionvirtualDao = new cl.femase.gestionweb.dao.MarcacionVirtualDAO(this.props);
    }

    /**
    * 
     * @param _cencoId
     * @param _rutEmpleado
     * @param _jtStartIndex
     * @param _jtPageSize
     * @param _jtSorting
     * @return 
    */
    public List<MarcacionVirtualVO> getRegistros(int _cencoId,
            String _rutEmpleado,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<MarcacionVirtualVO> lista = 
            marcacionvirtualDao.getRegistros(_cencoId, 
                _rutEmpleado,
                _jtStartIndex, 
                _jtPageSize, _jtSorting);

        return lista;
    }
    
    /**
    * 
    * @param _asignacion
    * @param _eventdata
    * @return 
    *  
    */
    public MaintenanceVO delete(MarcacionVirtualVO _asignacion, 
            MaintenanceEventVO _eventdata){
            
        MaintenanceVO delValues = 
            marcacionvirtualDao.delete(_asignacion);
        if (_asignacion.isInsertaEvento()){
            String msgFinal = delValues.getMsg();
            delValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        }
        return delValues;
    }
    
    /**
    * 
    * @param _asignacion
    * @param _eventdata
    * 
    * @return 
    *  
    */
    public MaintenanceVO insert(MarcacionVirtualVO _asignacion, 
            MaintenanceEventVO _eventdata){
            
        MaintenanceVO delValues = 
            marcacionvirtualDao.insert(_asignacion);

        String msgFinal = delValues.getMsg();
        delValues.setMsg(msgFinal);
        _eventdata.setDescription(msgFinal);
        //insertar evento 
        eventsService.addEvent(_eventdata); 

        return delValues;
    }
    
    /**
    * @param _asignaciones
    * @param _eventdata
    * 
    * @throws java.sql.SQLException
    */
    public void saveAsignacionList(ArrayList<MarcacionVirtualVO> _asignaciones, 
            MaintenanceEventVO _eventdata) throws SQLException{
            
        marcacionvirtualDao.saveAsignacionList(_asignaciones);

        for (MarcacionVirtualVO asignacion : _asignaciones) {
            if (asignacion.getRutEmpleado().compareTo("TODOS") != 0){
                System.out.println("[MarcacionVirtualDAO.saveAsignacionList]"
                    + "Inserta registro tabla marcacion_virtual:"
                    + "empresaId [" + asignacion.getEmpresaId() + "]" 
                    + ", rutEmpleado [" + asignacion.getRutEmpleado() + "]");
                String msgFinal = "Inserta registro tabla marcacion_virtual:"
                    + "empresaId [" + asignacion.getEmpresaId() + "]" 
                    + ", rutEmpleado [" + asignacion.getRutEmpleado() + "]"
                    + ", fecha1 [" + asignacion.getFecha1() + "]"
                    + ", fecha2 [" + asignacion.getFecha2() + "]"
                    + ", fecha3 [" + asignacion.getFecha3() + "]"
                    + ", fecha4 [" + asignacion.getFecha4() + "]"
                    + ", fecha5 [" + asignacion.getFecha5() + "]"
                    + ", fecha6 [" + asignacion.getFecha6() + "]"
                    + ", fecha7 [" + asignacion.getFecha7() + "]"
                    + ", registrar ubicacion [" + asignacion.getRegistrarUbicacion() + "]"
                    + ", tipo_evento [" + _eventdata.getType() + "]";

                _eventdata.setRutEmpleado(asignacion.getRutEmpleado());
                _eventdata.setDescription(msgFinal);
                //insertar evento 
                eventsService.addEvent(_eventdata);
            }
        }
       
    }
    
    public int getRegistrosCount(int _cencoId, 
            String _rutEmpleado){
        return marcacionvirtualDao.getRegistrosCount(_cencoId, 
            _rutEmpleado);
    }
    
    
}
