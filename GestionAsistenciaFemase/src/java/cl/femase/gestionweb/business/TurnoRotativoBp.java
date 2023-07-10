/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.AsignacionTurnoRotativoVO;
import cl.femase.gestionweb.vo.DetalleTurnoVO;
import cl.femase.gestionweb.vo.DuracionVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.TurnoRotativoVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.TurnoCentroCostoVO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author Alexander
 */
public class TurnoRotativoBp  extends BaseBp{

    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.TurnoRotativoDAO turnosRotativosDao;
    
    public TurnoRotativoBp(PropertiesVO props) {
        this.props = props;
        eventsService = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        turnosRotativosDao = new cl.femase.gestionweb.dao.TurnoRotativoDAO(this.props);
    }

    public List<TurnoRotativoVO> getTurnos(String _empresaId, 
            String _nombreTurno,
            int _estado,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<TurnoRotativoVO> lista = 
            turnosRotativosDao.getTurnos(_empresaId, _nombreTurno, 
                _estado, _jtStartIndex, 
                _jtPageSize, _jtSorting);

        return lista;
    }
    
    public TurnoRotativoVO getTurno(int _idTurno){
        
        TurnoRotativoVO data = 
            turnosRotativosDao.getTurno(_idTurno);

        return data;
    }

    public List<EmpleadoVO> getEmpleadosNoAsignados(int _idTurno,
            String _empresaId,
            String _deptoId,
            int _cencoId,
            int cargoId){
        List<EmpleadoVO> lista = 
            turnosRotativosDao.getEmpleadosNoAsignados(_idTurno, _empresaId, _deptoId, _cencoId, cargoId);

        return lista;
    }
            
    public List<EmpleadoVO> getEmpleados(int _idTurno,
            String _empresaId,
            String _deptoId,
            int _cencoId,
            int cargoId){
        List<EmpleadoVO> lista = 
            turnosRotativosDao.getEmpleados(_idTurno, _empresaId, _deptoId, _cencoId, cargoId);

        return lista;
    }
    
    public ResultCRUDVO update(TurnoRotativoVO _turnoToUpdate, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO updValues = turnosRotativosDao.update(_turnoToUpdate);
        
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
     * @param _rutEmpleado
     * @param _idTurno
     * @param _eventdata
     * @return
     */
    public ResultCRUDVO updateTurnoEmpleado(String _rutEmpleado,int _idTurno, 
            MaintenanceEventVO _eventdata){
        
        ResultCRUDVO insValues = turnosRotativosDao.updateTurnoEmpleado(_rutEmpleado, _idTurno);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
           
    /**
    * 
     * @param _asignaciones
    */
    public void insertarAsignaciones(ArrayList<AsignacionTurnoRotativoVO> _asignaciones){
    
        try {
            turnosRotativosDao.insertarAsignaciones(_asignaciones);
          
        } catch (SQLException ex) {
            System.err.println("[TurnoRotativoBp."
                + "insertarAsignaciones]Error: "+ex.toString());
        }
    }
    
    /**
    * 
     * @param _asignaciones
    */
    public void eliminarAsignaciones(ArrayList<AsignacionTurnoRotativoVO> _asignaciones){
    
        try {
            turnosRotativosDao.eliminarAsignaciones(_asignaciones);
           
        } catch (SQLException ex) {
            System.err.println("[TurnoRotativoBp."
                + "insertarAsignaciones]Error: "+ex.toString());
        }
    }
    
    /**
    * 
     * @param _objToInsert
     * @param _eventdata
     * @return 
    */
    public ResultCRUDVO insert(TurnoRotativoVO _objToInsert, 
            MaintenanceEventVO _eventdata){
        
        ResultCRUDVO insValues = turnosRotativosDao.insert(_objToInsert);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
    
    /**
    * 
     * @param _currentAsignacion
     * @param _updatedAsignacion
     * @param _eventdata
     * @return 
    */
    public ResultCRUDVO modifyAsignacion(AsignacionTurnoRotativoVO _currentAsignacion, 
            AsignacionTurnoRotativoVO _updatedAsignacion, 
            MaintenanceEventVO _eventdata){
        
        ResultCRUDVO updatedValues = turnosRotativosDao.modifyAsignacion(_currentAsignacion, _updatedAsignacion);
        
        //if (!updValues.isThereError()){
            String msgFinal = updatedValues.getMsg();
            updatedValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return updatedValues;
    }
        
    /**
    * 
    * @param _empresaId
    * @param _nombreTurno
    * @param _estado
    * @return 
    */
    public int getTurnosCount(String _empresaId,
            String _nombreTurno, int _estado){
        return turnosRotativosDao.getTurnosCount(_empresaId, 
            _nombreTurno, 
            _estado);
    }

    public List<TurnoRotativoVO> getTurnosConDetalle(String _empresaId){
        
        List<TurnoRotativoVO> lista = 
            turnosRotativosDao.getTurnosConDetalle(_empresaId);

        return lista;
    }
    
    public List<TurnoRotativoVO> getTurnosSinDetalle(String _empresaId){
        
        List<TurnoRotativoVO> lista = 
            turnosRotativosDao.getTurnosSinDetalle(_empresaId);

        return lista;
    }
    
    // nuevos. 21-05-2018
    public List<AsignacionTurnoRotativoVO> getAsignacionTurnosRotativosByRut(String _empresaId,
            String _rutEmpleado,
            int  _idTurno, 
            String _fechaDesde, 
            String _fechaHasta, 
            int _limit){
        
        return turnosRotativosDao.getAsignacionTurnosRotativosByRut(_empresaId,
                _rutEmpleado,
                _idTurno,
                _fechaDesde,
                _fechaHasta, 
                _limit);
        
    }
            
//    public ArrayList<AsignacionTurnoRotativoVO> getAsignacion(String _empresaId, 
//            String _rutEmpleado, 
//            int  _idTurno, 
//            String _fechaDesde, 
//            String _fechaHasta){
//        return turnosRotativosDao.getAsignacion(_empresaId, 
//                _rutEmpleado,
//                _idTurno,
//                _fechaDesde,
//                _fechaHasta);
//    }    
    
    public ArrayList<DuracionVO> getDuraciones(){
        return turnosRotativosDao.getDuraciones();
    }
 
    public ResultCRUDVO insertAsignacion(AsignacionTurnoRotativoVO _objToInsert, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO insValues = turnosRotativosDao.insertAsignacion(_objToInsert);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
    
    public ResultCRUDVO deleteAsignacion(AsignacionTurnoRotativoVO _objToDelete, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO deletedValues = turnosRotativosDao.deleteAsignacion(_objToDelete);
        
        //if (!updValues.isThereError()){
            String msgFinal = deletedValues.getMsg();
            deletedValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return deletedValues;
    }
    
    public List<AsignacionTurnoRotativoVO> getAsignacionesConflicto(String _empresaId,
            String _rutEmpleado, 
            int _idTurno,
            String _fechaDesde, 
            String _fechaHasta){
    
        return turnosRotativosDao.getAsignacionesConflicto(_empresaId, 
                _rutEmpleado, 
                _idTurno,
                _fechaDesde, 
                _fechaHasta);
    }
    
    /**
     *
     * @param _empresaId
     * @param _rutEmpleado
     * @param _fecha
     * @return
     */
    public DetalleTurnoVO getAsignacionTurnoByFecha(String _empresaId, 
            String _rutEmpleado,
            String _fecha){
        return turnosRotativosDao.getAsignacionTurnoByFecha(_empresaId, _rutEmpleado, _fecha);
    }
    
    /**
    * 
    * @param _empresaId
    * @param _rutEmpleado
    * @param _fecha
    * @return 
    */
    public String getAsignacionTurnoByFechaJson(String _empresaId, 
            String _rutEmpleado,
            String _fecha){
        
        String jsonOutput = 
            turnosRotativosDao.getAsignacionTurnoByFechaJson(_empresaId, _rutEmpleado, _fecha);

        return jsonOutput;
    }
    
    public boolean updateTodos(TurnoRotativoVO _turno){
        return turnosRotativosDao.updateTodos(_turno);
    }
    
    //************************************************************************
    //*** Nuevos metodos para la administracion de turnos rotativos asignados a centros de costo
    //************************************************************************
    
    
    /**
    * 
    * @param _empresaId
    * @param _deptoId
    * @param _cencoId
    * @return 
    */
    public LinkedHashMap<Integer, TurnoRotativoVO> getTurnosAsignadosByCenco(String _empresaId,
            String _deptoId, 
            int _cencoId){
        
        return turnosRotativosDao.getTurnosAsignadosByCenco(_empresaId, _deptoId, _cencoId);
        
    }
    
    /**
    * 
    * @param _empresaId
    * @param _deptoId
    * @param _cencoId
    * @return 
    */
    public ArrayList<TurnoRotativoVO> getTurnosAsignadosByCencoAsArrayList(String _empresaId,
            String _deptoId, 
            int _cencoId){
        
        LinkedHashMap<Integer, TurnoRotativoVO> lhashmapTurnosAsignados = 
            turnosRotativosDao.getTurnosAsignadosByCenco(_empresaId, _deptoId, _cencoId);
        
        ArrayList<TurnoRotativoVO> listValues
            = new ArrayList<>(lhashmapTurnosAsignados.values());
        
        return listValues;
    }
     
    /**
    * 
    * @param _empresaId
    * @param _deptoId
    * @param _cencoId
    * 
    * @return 
    */
    public LinkedHashMap<Integer, TurnoRotativoVO> getTurnosNoAsignadosByCenco(String _empresaId,
            String _deptoId, 
            int _cencoId){
        
        return turnosRotativosDao.getTurnosNoAsignadosByCenco(_empresaId, _deptoId, _cencoId);
        
    }
    
    /**
    * 
    * @param _asignaciones
    */
    public void eliminarAsignacionesCencos(ArrayList<TurnoCentroCostoVO> _asignaciones){
       try{
            turnosRotativosDao.eliminarAsignacionesCencos(_asignaciones);
        } catch (SQLException ex) {
            System.err.println("[TurnosBp."
                + "eliminarAsignacionesCencos]Error: "+ex.toString());
        }
    }
    
    /**
    * 
    * @param _asignaciones
    */
    public void insertarAsignacionesCencos(ArrayList<TurnoCentroCostoVO> _asignaciones){
       try{
            turnosRotativosDao.insertarAsignacionesCencos(_asignaciones);
        } catch (SQLException ex) {
            System.err.println("[TurnosBp."
                + "insertarAsignacionesCencos]Error: "+ex.toString());
        }
   }
    
}
