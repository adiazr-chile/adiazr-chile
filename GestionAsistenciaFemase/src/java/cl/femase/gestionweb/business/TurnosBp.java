/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.TurnoVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.TurnoCentroCostoVO;
import cl.femase.gestionweb.vo.UsuarioCentroCostoVO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author Alexander
 */
public class TurnosBp  extends BaseBp{

    /**
     *
     */
    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.TurnosDAO turnosDao;
    
    public TurnosBp(PropertiesVO props) {
        this.props = props;
        eventsService = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        turnosDao = new cl.femase.gestionweb.dao.TurnosDAO(this.props);
    }

    public List<TurnoVO> getTurnos(String _empresaId, String _nombre,int _estado,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<TurnoVO> lista = 
            turnosDao.getTurnos(_empresaId, _nombre, _estado,_jtStartIndex, 
                _jtPageSize, _jtSorting);

        return lista;
    }
    
    
    public LinkedHashMap<Integer, TurnoVO> getTurnosAsignadosByCenco(String _empresaId,
            String _deptoId, 
            int _cencoId){
        
        return turnosDao.getTurnosAsignadosByCenco(_empresaId, _deptoId, _cencoId);
        
    }
        
    public LinkedHashMap<Integer, TurnoVO> getAllTurnosAsignadosByCenco(String _empresaId,
            String _deptoId, 
            int _cencoId){
        
        return turnosDao.getAllTurnosAsignadosByCenco(_empresaId, _deptoId, _cencoId);
        
    }
    
    /**
    * 
    * @param _empresaId
    * @param _deptoId
    * @param _cencoId
    * @return 
    */
    public LinkedHashMap<Integer, TurnoVO> getTurnosNoAsignadosByCenco(String _empresaId,
            String _deptoId, 
            int _cencoId){
        
        return turnosDao.getTurnosNoAsignadosByCenco(_empresaId, _deptoId, _cencoId);
        
    }
    
    public List<TurnoVO> getTurnosByEmpresa(String _empresaId){
        
        List<TurnoVO> lista = 
            turnosDao.getTurnosByEmpresa(_empresaId);

        return lista;
    }
    
    public List<TurnoVO> getTurnosByCencos(String _empresaId, 
            List<UsuarioCentroCostoVO> _cencosUsuario){
        List<TurnoVO> turnos = new ArrayList<>();
        if (_cencosUsuario!=null && !_cencosUsuario.isEmpty()){
            turnos = turnosDao.getTurnosByCencos(_empresaId, _cencosUsuario);
        }
        return turnos;
    }
    
    public int getTurnoRotativo(String _empresaId){
        return turnosDao.getTurnoRotativo(_empresaId);
    }        
            
    public TurnoVO getTurno(int _idTurno){
        
        TurnoVO data = 
            turnosDao.getTurno(_idTurno);

        return data;
    }
            
    public List<EmpleadoVO> getEmpleadosNoAsignados(int _idTurno,
            String _empresaId,
            String _deptoId,
            int _cencoId,
            int cargoId){
        List<EmpleadoVO> lista = 
            turnosDao.getEmpleadosNoAsignados(_idTurno, _empresaId, _deptoId, _cencoId, cargoId);

        return lista;
    }
            
    public List<EmpleadoVO> getEmpleados(int _idTurno,
            String _empresaId,
            String _deptoId,
            int _cencoId,
            int cargoId){
        List<EmpleadoVO> lista = 
            turnosDao.getEmpleados(_idTurno, _empresaId, _deptoId, _cencoId, cargoId);

        return lista;
    }
    
    /**
     *
     * @param _turnoToUpdate
     * @param _eventdata
     * @return
     */
    public MaintenanceVO update(TurnoVO _turnoToUpdate, 
            MaintenanceEventVO _eventdata){
        MaintenanceVO updValues = turnosDao.update(_turnoToUpdate);
        
        //if (!updValues.isThereError()){
            String msgFinal = updValues.getMsg();
            updValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return updValues;
    }
    
    public MaintenanceVO updateTurnoEmpleado(String _rutEmpleado,int _idTurno, 
            MaintenanceEventVO _eventdata){
        
        MaintenanceVO insValues = turnosDao.updateTurnoEmpleado(_rutEmpleado, _idTurno);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
            
    public MaintenanceVO insert(TurnoVO _objToInsert, 
            MaintenanceEventVO _eventdata){
        
        MaintenanceVO insValues = turnosDao.insert(_objToInsert);
        
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
    * @param _empresaId
    * @param _nombre
    * @param _estado
    * @return 
    */
    public int getTurnosCount(String _empresaId, String _nombre, int _estado){
        return turnosDao.getTurnosCount(_empresaId, _nombre, _estado);
    }

    /**
    * 
    * @param _asignaciones
    */
    public void insertarAsignacionesCencos(ArrayList<TurnoCentroCostoVO> _asignaciones){
       try{
            turnosDao.insertarAsignacionesCencos(_asignaciones);
        } catch (SQLException ex) {
            System.err.println("[TurnosBp."
                + "insertarAsignacionesCencos]Error: "+ex.toString());
        }
   }
   
   public void eliminarAsignacionesCencos(ArrayList<TurnoCentroCostoVO> _asignaciones){
       try{
            turnosDao.eliminarAsignacionesCencos(_asignaciones);
        } catch (SQLException ex) {
            System.err.println("[TurnosBp."
                + "eliminarAsignacionesCencos]Error: "+ex.toString());
        }
   }
    
}
