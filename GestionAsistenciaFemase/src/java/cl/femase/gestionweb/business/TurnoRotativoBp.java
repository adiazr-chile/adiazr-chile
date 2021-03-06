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
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alexander
 */
public class TurnoRotativoBp {

    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.TurnoRotativoDAO turnoRotativoService;
    
    public TurnoRotativoBp(PropertiesVO props) {
        this.props = props;
        eventsService = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        turnoRotativoService = new cl.femase.gestionweb.dao.TurnoRotativoDAO(this.props);
    }

    public List<TurnoRotativoVO> getTurnos(String _empresaId, String _nombreTurno,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<TurnoRotativoVO> lista = 
            turnoRotativoService.getTurnos(_empresaId, _nombreTurno, _jtStartIndex, 
                _jtPageSize, _jtSorting);

        return lista;
    }
    
    public TurnoRotativoVO getTurno(int _idTurno){
        
        TurnoRotativoVO data = 
            turnoRotativoService.getTurno(_idTurno);

        return data;
    }

    public List<EmpleadoVO> getEmpleadosNoAsignados(int _idTurno,
            String _empresaId,
            String _deptoId,
            int _cencoId,
            int cargoId){
        List<EmpleadoVO> lista = 
            turnoRotativoService.getEmpleadosNoAsignados(_idTurno, _empresaId, _deptoId, _cencoId, cargoId);

        return lista;
    }
            
    public List<EmpleadoVO> getEmpleados(int _idTurno,
            String _empresaId,
            String _deptoId,
            int _cencoId,
            int cargoId){
        List<EmpleadoVO> lista = 
            turnoRotativoService.getEmpleados(_idTurno, _empresaId, _deptoId, _cencoId, cargoId);

        return lista;
    }
    
    public MaintenanceVO update(TurnoRotativoVO _turnoToUpdate, 
            MaintenanceEventVO _eventdata){
        MaintenanceVO updValues = turnoRotativoService.update(_turnoToUpdate);
        
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
    public MaintenanceVO updateTurnoEmpleado(String _rutEmpleado,int _idTurno, 
            MaintenanceEventVO _eventdata){
        
        MaintenanceVO insValues = turnoRotativoService.updateTurnoEmpleado(_rutEmpleado, _idTurno);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
            
    public void insertarAsignaciones(ArrayList<AsignacionTurnoRotativoVO> _asignaciones){
    
        try {
            turnoRotativoService.insertarAsignaciones(_asignaciones);
            
//        //if (!updValues.isThereError()){
//            String msgFinal = insValues.getMsg();
//            insValues.setMsg(msgFinal);
//            _eventdata.setDescription(msgFinal);
//            //insertar evento
//            eventsService.addEvent(_eventdata); 
//        //}
//
//return insValues;
        } catch (SQLException ex) {
            System.err.println("[TurnoRotativoBp."
                + "insertarAsignaciones]Error: "+ex.toString());
        }
    }
    
    public void eliminarAsignaciones(ArrayList<AsignacionTurnoRotativoVO> _asignaciones){
    
        try {
            turnoRotativoService.eliminarAsignaciones(_asignaciones);
            
//        //if (!updValues.isThereError()){
//            String msgFinal = insValues.getMsg();
//            insValues.setMsg(msgFinal);
//            _eventdata.setDescription(msgFinal);
//            //insertar evento
//            eventsService.addEvent(_eventdata); 
//        //}
//
//return insValues;
        } catch (SQLException ex) {
            System.err.println("[TurnoRotativoBp."
                + "insertarAsignaciones]Error: "+ex.toString());
        }
    }
    
    public MaintenanceVO insert(TurnoRotativoVO _objToInsert, 
            MaintenanceEventVO _eventdata){
        
        MaintenanceVO insValues = turnoRotativoService.insert(_objToInsert);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
    
    public MaintenanceVO modifyAsignacion(AsignacionTurnoRotativoVO _currentAsignacion, 
            AsignacionTurnoRotativoVO _updatedAsignacion, 
            MaintenanceEventVO _eventdata){
        
        MaintenanceVO updatedValues = turnoRotativoService.modifyAsignacion(_currentAsignacion, _updatedAsignacion);
        
        //if (!updValues.isThereError()){
            String msgFinal = updatedValues.getMsg();
            updatedValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return updatedValues;
    }
        
    public int getTurnosCount(String _empresaId,String _nombreTurno){
        return turnoRotativoService.getTurnosCount(_empresaId, _nombreTurno);
    }

    public List<TurnoRotativoVO> getTurnosConDetalle(String _empresaId){
        
        List<TurnoRotativoVO> lista = 
            turnoRotativoService.getTurnosConDetalle(_empresaId);

        return lista;
    }
    
    public List<TurnoRotativoVO> getTurnosSinDetalle(String _empresaId){
        
        List<TurnoRotativoVO> lista = 
            turnoRotativoService.getTurnosSinDetalle(_empresaId);

        return lista;
    }
    
    // nuevos. 21-05-2018
    public List<AsignacionTurnoRotativoVO> getAsignacionTurnosRotativosByRut(String _empresaId,
            String _rutEmpleado,
            int  _idTurno, 
            String _fechaDesde, 
            String _fechaHasta, 
            int _limit){
        
        return turnoRotativoService.getAsignacionTurnosRotativosByRut(_empresaId,
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
//        return turnoRotativoService.getAsignacion(_empresaId, 
//                _rutEmpleado,
//                _idTurno,
//                _fechaDesde,
//                _fechaHasta);
//    }    
    
    public ArrayList<DuracionVO> getDuraciones(){
        return turnoRotativoService.getDuraciones();
    }
 
    public MaintenanceVO insertAsignacion(AsignacionTurnoRotativoVO _objToInsert, 
            MaintenanceEventVO _eventdata){
        MaintenanceVO insValues = turnoRotativoService.insertAsignacion(_objToInsert);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
    
    public MaintenanceVO deleteAsignacion(AsignacionTurnoRotativoVO _objToDelete, 
            MaintenanceEventVO _eventdata){
        MaintenanceVO deletedValues = turnoRotativoService.deleteAsignacion(_objToDelete);
        
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
    
        return turnoRotativoService.getAsignacionesConflicto(_empresaId, 
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
        return turnoRotativoService.getAsignacionTurnoByFecha(_empresaId, _rutEmpleado, _fecha);
    }
    
    public String getAsignacionTurnoByFechaJson(String _empresaId, 
            String _rutEmpleado,
            String _fecha){
        
        String jsonOutput = 
            turnoRotativoService.getAsignacionTurnoByFechaJson(_empresaId, _rutEmpleado, _fecha);

        return jsonOutput;
    }
    
    public boolean updateTodos(TurnoRotativoVO _turno){
        return turnoRotativoService.updateTodos(_turno);
    }
    
}
