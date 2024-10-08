/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.DetalleAusenciaVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alexander
 */
public class DetalleAusenciaBp  extends BaseBp{

    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsDao;
    private final cl.femase.gestionweb.dao.DetalleAusenciaDAO detAusenciaDao;
    private final cl.femase.gestionweb.dao.EmpleadosDAO empleadosDao;
    
    public DetalleAusenciaBp(PropertiesVO props) {
        this.props = props;
        eventsDao = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        detAusenciaDao = new cl.femase.gestionweb.dao.DetalleAusenciaDAO(this.props);
        empleadosDao = new cl.femase.gestionweb.dao.EmpleadosDAO(this.props);
    }

    /**
    * 
    * @param _source
    * @param _rutEmpleado
    * @param _rutAutorizador
    * @param _fechaInicio
    * @param _fechaFin
    * @param _ausenciaId
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * @return 
    */
    public List<DetalleAusenciaVO> getDetallesAusencias(
            String _source,
            String _rutEmpleado,
            String _rutAutorizador, 
            String _fechaInicio, 
            String _fechaFin,
            int _ausenciaId,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<DetalleAusenciaVO> lista = 
            detAusenciaDao.getDetallesAusencias(_source, 
                _rutEmpleado, 
                _rutAutorizador, _fechaInicio,
                _fechaFin, _ausenciaId, _jtStartIndex, 
                _jtPageSize, _jtSorting);

        return lista;
    }
    
    /**
    * 
    * @param _source
    * @param _rutEmpleado
    * @param _rutAutorizador
    * @param _fechaIngresoInicio
    * @param _fechaIngresoFin
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * @return 
    */
    public List<DetalleAusenciaVO> getPermisosAdministrativos(
            String _source,
            String _rutEmpleado,
            String _rutAutorizador, 
            String _fechaIngresoInicio, 
            String _fechaIngresoFin,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<DetalleAusenciaVO> lista = 
            detAusenciaDao.getPermisosAdministrativos(_source, 
                _rutEmpleado, 
                _rutAutorizador, _fechaIngresoInicio,
                _fechaIngresoFin, _jtStartIndex, 
                _jtPageSize, _jtSorting);

        return lista;
    }
    
    /**
    * 
    * @param _source
    * @param _rutEmpleado
    * @param _rutAutorizador
    * @param _fechaIngresoInicio
    * @param _fechaIngresoFin
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * @return 
    */
    public List<DetalleAusenciaVO> getPermisosExamenSaludPreventiva(
            String _source,
            String _rutEmpleado,
            String _rutAutorizador, 
            String _fechaIngresoInicio, 
            String _fechaIngresoFin,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<DetalleAusenciaVO> lista = 
            detAusenciaDao.getPermisosExamenSaludPreventiva(_source, 
                _rutEmpleado, 
                _rutAutorizador, _fechaIngresoInicio,
                _fechaIngresoFin, _jtStartIndex, 
                _jtPageSize, _jtSorting);

        return lista;
    }
  
    /**
    * 
    * @param _rutEmpleado
    * @param _anioMesInicio
     * @return 
    */
    public List<DetalleAusenciaVO> getVacacionesByAnioMesInicio(String _rutEmpleado,
            String _anioMesInicio){
    
        List<DetalleAusenciaVO> lista = 
            detAusenciaDao.getVacacionesByAnioMesInicio(_rutEmpleado, _anioMesInicio);

        return lista;
    }
    
    /**
    * 
    * @param _rutEmpleado
    * @param _anioMesInicio
     * @return 
    */
    public List<DetalleAusenciaVO> getPermisosAdministrativosByAnioMesInicio(String _rutEmpleado,
            String _anioMesInicio){
    
        List<DetalleAusenciaVO> lista = 
            detAusenciaDao.getPermisosAdministrativosByAnioMesInicio(_rutEmpleado, _anioMesInicio);

        return lista;
    }
    
    /**
    * 
    * @param _rutEmpleado
    * @param _anioMesInicio
     * @return 
    */
    public List<DetalleAusenciaVO> getPermisosExamenSaludPreventivaByAnioMesInicio(String _rutEmpleado,
            String _anioMesInicio){
    
        List<DetalleAusenciaVO> lista = 
            detAusenciaDao.getPermisosExamenSaludPreventivaByAnioMesInicio(_rutEmpleado, _anioMesInicio);

        return lista;
    }
    
    public List<DetalleAusenciaVO> getDetallesAusenciasHist(String _rutEmpleado,
            String _rutAutorizador, 
            String _fechaIngresoInicio, 
            String _fechaIngresoFin,
            int _ausenciaId,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<DetalleAusenciaVO> lista = 
            detAusenciaDao.getDetallesAusenciasHist(_rutEmpleado, 
                _rutAutorizador, _fechaIngresoInicio,
                _fechaIngresoFin, _ausenciaId, _jtStartIndex, 
                _jtPageSize, _jtSorting);

        return lista;
    }
    
    public ArrayList<DetalleAusenciaVO> getAusencias(
            String _rutEmpleado,
            String _fecha){
        
        ArrayList<DetalleAusenciaVO> listaAusencias = 
            detAusenciaDao.getAusencias(_rutEmpleado, 
                _fecha);

        return listaAusencias;
    }
    
    public String getAusenciasJson(
            String _rutEmpleado,
            String _fecha){
        
        String jsonAusencias = 
            detAusenciaDao.getAusenciasJson(_rutEmpleado, 
                _fecha);

        return jsonAusencias;
    }
    
    public String getAusenciasHistJson(
            String _rutEmpleado,
            String _fecha){
        
        String jsonAusencias = 
            detAusenciaDao.getAusenciasHistJson(_rutEmpleado, 
                _fecha);

        return jsonAusencias;
    }
    
    public List<DetalleAusenciaVO> getUltimasAusencias(UsuarioVO _usuario, 
            int _rowLimit){
    
        return detAusenciaDao.getUltimasAusencias(_usuario, _rowLimit); 
    }
    
    public DetalleAusenciaVO getAusencia(
            String _rutEmpleado,
            String _fecha){
        
        DetalleAusenciaVO ausencia = 
            detAusenciaDao.getAusencia(_rutEmpleado, 
                _fecha);

        return ausencia;
    }
    
    public List<DetalleAusenciaVO> getAutorizadoresDisponibles(UsuarioVO _usuario){
        
        List<DetalleAusenciaVO> lista = 
            detAusenciaDao.getAutorizadoresDisponibles(_usuario);

        return lista;
    }
    
    
    public ResultCRUDVO updateDiasEfectivosVacaciones(DetalleAusenciaVO _objToUpdate, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO updValues = detAusenciaDao.updateDiasEfectivosVacaciones(_objToUpdate);
        
        return updValues;
    }
    
    public ResultCRUDVO update(DetalleAusenciaVO _objToUpdate, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO updValues = detAusenciaDao.update(_objToUpdate);
        System.out.println(WEB_NAME+"[DetalleAusenciaBp.update]"
            + "rutEmpleado: " + _objToUpdate.getRutEmpleado());
        EmpleadoVO empleado = empleadosDao.getEmpleado(null, _objToUpdate.getRutEmpleado());
        _eventdata.setEmpresaId(empleado.getEmpresa().getId());
        _eventdata.setDeptoId(empleado.getDepartamento().getId());
        _eventdata.setCencoId(empleado.getCentroCosto().getId());
        _eventdata.setRutEmpleado(empleado.getRut());
        
        //if (!updValues.isThereError()){
            String msgFinal = updValues.getMsg();
            updValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsDao.addEvent(_eventdata); 
        //}
        
        return updValues;
    }
    
    public ResultCRUDVO delete(DetalleAusenciaVO _objToDelete, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO deleteValues = detAusenciaDao.delete(_objToDelete);
        
        EmpleadoVO empleado = empleadosDao.getEmpleado(null, _objToDelete.getRutEmpleado());
        _eventdata.setEmpresaId(empleado.getEmpresa().getId());
        _eventdata.setDeptoId(empleado.getDepartamento().getId());
        _eventdata.setCencoId(empleado.getCentroCosto().getId());
        _eventdata.setRutEmpleado(empleado.getRut());
        //if (!updValues.isThereErreor()){
            String msgFinal = deleteValues.getMsg();
            deleteValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsDao.addEvent(_eventdata); 
        //}
        
        return deleteValues;
    }
    
    /**
    * 
    * @param _objToInsert
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO insert(DetalleAusenciaVO _objToInsert, 
            MaintenanceEventVO _eventdata){
        
        ResultCRUDVO insValues = detAusenciaDao.insert(_objToInsert);
        
        EmpleadoVO empleado = empleadosDao.getEmpleado(null, _objToInsert.getRutEmpleado());
        _eventdata.setEmpresaId(empleado.getEmpresa().getId());
        _eventdata.setDeptoId(empleado.getDepartamento().getId());
        _eventdata.setCencoId(empleado.getCentroCosto().getId());
        _eventdata.setRutEmpleado(empleado.getRut());
        
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
    * @param _newAusencia
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO insertaVacacion(DetalleAusenciaVO _newAusencia, 
            MaintenanceEventVO _eventdata){
        
        ResultCRUDVO insValues = detAusenciaDao.insertaVacacion(_newAusencia);
        
        EmpleadoVO empleado = empleadosDao.getEmpleado(null, _newAusencia.getRutEmpleado());
        _eventdata.setEmpresaId(empleado.getEmpresa().getId());
        _eventdata.setDeptoId(empleado.getDepartamento().getId());
        _eventdata.setCencoId(empleado.getCentroCosto().getId());
        _eventdata.setRutEmpleado(empleado.getRut());
        
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
    * @param _newAusencia
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO insertaPermisoAdministrativo(DetalleAusenciaVO _newAusencia, 
            MaintenanceEventVO _eventdata){
        
        ResultCRUDVO insValues = detAusenciaDao.insertaPermisoAdministrativo(_newAusencia);
        
        EmpleadoVO empleado = empleadosDao.getEmpleado(null, _newAusencia.getRutEmpleado());
        _eventdata.setEmpresaId(empleado.getEmpresa().getId());
        _eventdata.setDeptoId(empleado.getDepartamento().getId());
        _eventdata.setCencoId(empleado.getCentroCosto().getId());
        _eventdata.setRutEmpleado(empleado.getRut());
        
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
    * @param _newAusencia
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO insertaPermisoExamenSaludPreventiva(DetalleAusenciaVO _newAusencia, 
            MaintenanceEventVO _eventdata){
        
        ResultCRUDVO insValues = detAusenciaDao.insertaPermisoExamenSaludPreventiva(_newAusencia);
        
        EmpleadoVO empleado = empleadosDao.getEmpleado(null, _newAusencia.getRutEmpleado());
        _eventdata.setEmpresaId(empleado.getEmpresa().getId());
        _eventdata.setDeptoId(empleado.getDepartamento().getId());
        _eventdata.setCencoId(empleado.getCentroCosto().getId());
        _eventdata.setRutEmpleado(empleado.getRut());
        
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
    * Invoca funcion setsaldodiasvacacionesasignadas
    * @param _runEmpleado
    * @return 
    */
    public ResultCRUDVO actualizaSaldosVacaciones(String _runEmpleado){
        ResultCRUDVO updValues = detAusenciaDao.actualizaSaldosVacaciones(_runEmpleado);
        return updValues;
    }
    
    /**
    * Invoca funcion setsaldodiasvacacionesasignadas_vba
    * @param _runEmpleado
    * @return 
    */
    public ResultCRUDVO actualizaSaldosVacacionesVBA(String _runEmpleado){
        ResultCRUDVO updValues = detAusenciaDao.actualizaSaldosVacacionesVBA(_runEmpleado);
        return updValues;
    }
    
    /**
    * 
    * @param _correlativo
    * @return 
    */
    public DetalleAusenciaVO getDetalleAusenciaByCorrelativo(
            int _correlativo){
        return detAusenciaDao.getDetalleAusenciaByCorrelativo(_correlativo);
    }
    
    /*
    public ResultCRUDVO delete(ContractRelationVO _relationToDelete, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO insValues = contractRelService.delete(_relationToDelete);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsDao.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
    */
    
    /**
    * 
    * @param _source
    * @param _rutEmpleado
    * @param _rutAutorizador
    * @param _fechaIngresoInicio
    * @param _fechaIngresoFin
    * @param _ausenciaId
    * @return 
    */
    public int getDetallesAusenciasCount(String _source,
            String _rutEmpleado,
            String _rutAutorizador, 
            String _fechaIngresoInicio, 
            String _fechaIngresoFin,
            int _ausenciaId){
        return detAusenciaDao.getDetallesAusenciasCount(_source, 
            _rutEmpleado, 
            _rutAutorizador, _fechaIngresoInicio, 
            _fechaIngresoFin, _ausenciaId);
    }
    
    /**
    * 
    * @param _source
    * @param _rutEmpleado
    * @param _rutAutorizador
    * @param _fechaIngresoInicio
    * @param _fechaIngresoFin
    * @return 
    */
    public int getPermisosAdministrativosCount(String _source,
            String _rutEmpleado,
            String _rutAutorizador, 
            String _fechaIngresoInicio, 
            String _fechaIngresoFin){
        return detAusenciaDao.getPermisosAdministrativosCount(_source, 
            _rutEmpleado, 
            _rutAutorizador, _fechaIngresoInicio, 
            _fechaIngresoFin);
    }
    
    /**
    * 
    * @param _source
    * @param _rutEmpleado
    * @param _rutAutorizador
    * @param _fechaIngresoInicio
    * @param _fechaIngresoFin
    * @return 
    */
    public int getPermisosExamenSaludPreventivaCount(String _source,
            String _rutEmpleado,
            String _rutAutorizador, 
            String _fechaIngresoInicio, 
            String _fechaIngresoFin){
        return detAusenciaDao.getPermisosExamenSaludPreventivaCount(_source, 
            _rutEmpleado, 
            _rutAutorizador, _fechaIngresoInicio, 
            _fechaIngresoFin);
    }
    
    public int getDetallesAusenciasHistCount(String _rutEmpleado,
            String _rutAutorizador, 
            String _fechaIngresoInicio, 
            String _fechaIngresoFin,
            int _ausenciaId){
        return detAusenciaDao.getDetallesAusenciasHistCount(_rutEmpleado, 
                _rutAutorizador, _fechaIngresoInicio, 
                _fechaIngresoFin, _ausenciaId);
    }
    
    /**
    * Retorna ausencias existentes.Al rescatar las ausencias, se hace la diferencia si se buscan ausencias 
    * por dia o por horas.
    * 
    * @param _rutEmpleado
    * @param _ausenciaPorHora
    * @param _fechaInicio
    * @param _fechaFin
    * @param _horaInicio
    * @param _horaFin
    * @return 
    */
    public ArrayList<DetalleAusenciaVO> getAusenciasConflicto(
            String _rutEmpleado,
            boolean _ausenciaPorHora,
            String _fechaInicio,
            String _fechaFin,
            String _horaInicio,
            String _horaFin){
        
        ArrayList<DetalleAusenciaVO> ausencias = new ArrayList<>();
        if (!_ausenciaPorHora){
            System.out.println(WEB_NAME+"[DetalleAusenciaBp.getAusenciasConflicto]"
                + "La ausencia a insertar es por DIA");
            System.out.println(WEB_NAME+"[DetalleAusenciaBp.getAusenciasConflicto]"
                + "-1- Validar si hay ausencias por Hora");
            ausencias = detAusenciaDao.getAusenciasHoraConflicto(_rutEmpleado, 
                    _fechaInicio, _horaInicio, _horaFin);
            if (ausencias.isEmpty()){
                System.out.println(WEB_NAME+"[DetalleAusenciaBp.getAusenciasConflicto]"
                    + "-1.1- No hay ausencias por Hora");
                System.out.println(WEB_NAME+"[DetalleAusenciaBp.getAusenciasConflicto]"
                    + "-2- Ahora Validar si hay ausencias por Dia");
                ausencias = detAusenciaDao.getAusenciasDiaConflicto(_rutEmpleado, 
                    _fechaInicio, _fechaFin);
                if (ausencias.isEmpty()){
                    System.out.println(WEB_NAME+"[DetalleAusenciaBp.getAusenciasConflicto]"
                        + "-2.1- No hay ausencias por Dia. Ok");
                }else{
                    System.out.println(WEB_NAME+"[DetalleAusenciaBp.getAusenciasConflicto]"
                        + "-2.2- Ya existen ausencias por Dia. Error");
                }
            }
        }else{
            System.out.println(WEB_NAME+"[DetalleAusenciaBp.getAusenciasConflicto]"
                + "La ausencia a insertar es por HORA");
            //System.out.println(WEB_NAME+"[DetalleAusenciaBp.getAusenciasConflicto]"
            //    + "-1- Validar si hay ausencias por Dia");
            //ausencias = detAusenciaDao.getAusenciasDiaConflicto(_rutEmpleado, 
            //    _fechaInicio, _fechaFin);
            //if (ausencias.isEmpty()){
                System.out.println(WEB_NAME+"[DetalleAusenciaBp.getAusenciasConflicto]"
                    + "-2- Validar si hay ausencias por Hora");
                ausencias = detAusenciaDao.getAusenciasHoraConflicto(_rutEmpleado, 
                    _fechaInicio, _horaInicio, _horaFin);
            //}
        }
        
        return ausencias;
    }
    
}
