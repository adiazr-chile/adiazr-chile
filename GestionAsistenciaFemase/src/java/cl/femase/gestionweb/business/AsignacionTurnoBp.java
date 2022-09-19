/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.AsignacionTurnoVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.util.List;

/**
 *
 * @author Alexander
 */
public class AsignacionTurnoBp  extends BaseBp{

    /**
     *
     */
    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.AsignacionTurnoDAO asignacionDao;
    
    public AsignacionTurnoBp(PropertiesVO props) {
        this.props = props;
        eventsService = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        asignacionDao = new cl.femase.gestionweb.dao.AsignacionTurnoDAO();
    }

    /**
     * 
     * @param _empresaId
     * @param _rutEmpleado
     * @param _turnoId
     * @param _startDate
     * @param _endDate
     * @param _cencoId
     * @param _jtStartIndex
     * @param _jtPageSize
     * @param _jtSorting
     * @return 
     */
    public List<AsignacionTurnoVO> getAsignaciones(String _empresaId, 
            String _rutEmpleado,
            int _turnoId,
            String _startDate,
            String _endDate,
            int _cencoId,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<AsignacionTurnoVO> lista = 
            asignacionDao.getAsignaciones(_empresaId, _rutEmpleado, 
                _turnoId, _startDate, 
                _endDate, _cencoId, 
                _jtStartIndex, _jtPageSize, _jtSorting);

        return lista;
    }

    /**
    * 
    * @param _empresaId
    * @param _rutEmpleado
    * @return 
    */
    public AsignacionTurnoVO getTurnoVigente(String _empresaId, 
            String _rutEmpleado){
        return asignacionDao.getTurnoVigente(_empresaId, _rutEmpleado);
    }
    
    /**
    * 
    * @param _empresaId
    * @param _rutEmpleado
    * @return 
    */
    public AsignacionTurnoVO getTurnoAnterior(String _empresaId, 
            String _rutEmpleado){
        return asignacionDao.getTurnoAnterior(_empresaId, _rutEmpleado);
    }
    
    /**
     * 
     * @param _empresaId
     * @param _rutEmpleado
     * @return 
     */
    public int getTurnoActual(String _empresaId, 
            String _rutEmpleado){
        return asignacionDao.getTurnoActual(_empresaId, _rutEmpleado);
    }
    
    /**
     * 
     * @param _asignacionTurno
     */
    public void venceTurno(AsignacionTurnoVO _asignacionTurno){
        asignacionDao.venceTurno(_asignacionTurno);
    }
    
    /**
     * 
     * @param _asignacionTurno
     */
    public void insertaTurno(AsignacionTurnoVO _asignacionTurno){
       asignacionDao.insertaTurno(_asignacionTurno);
    }
    
    /**
     * 
     * @param _empresaId
     * @param _rutEmpleado
     * @param _turnoId
     * @param _startDate
     * @param _endDate
     * @param _cencoId
     * @return 
     */
    public int getAsignacionesCount(String _empresaId, 
            String _rutEmpleado,
            int _turnoId,
            String _startDate,
            String _endDate,
            int _cencoId){
        return asignacionDao.getAsignacionesCount(_empresaId, _rutEmpleado, _turnoId, _startDate, _endDate, _cencoId);
    }

    /**
     * Si el empleado+empresa_id no tiene registros en la tabla 'turno_asignacion' 
     * Se debe hacer insert en 'turno_asignacion'. 
     * 
     * En caso contrario:
     *      .- Si el turno seleccionado desde el formulario de modificación de empleado 
     *      es distinto al turno actualmente asignado, 
     *      se debe:
     *          a- Setear 'turno_asignacion'.'fecha_hasta' = fecha de modificacion del empleado (vencer turno actual) 
     *          b- Se debe insertar el nuevo turno en turno_asignacion. 
     * 
     *      .- Si el turno seleccionado desde el formulario de modificación de empleado es igual al turno actual asignado, no se hace nada.
     * 
     * @param _empresaId
     * @param _rutEmpleado
     * @param _idTurnoSeleccionado
     * @param _userName
     */
    public void validaTurnoEmpleado(String _empresaId, 
            String _rutEmpleado,
            int _idTurnoSeleccionado, 
            String _userName){
        
        System.out.println(WEB_NAME+"[AsignacionTurnoBp."
            + "validaTurnoEmpleado]"
            + "empresaId: " + _empresaId
            + ", rutEmpleado: " + _rutEmpleado
            + ", turnoSeleccionado: " + _idTurnoSeleccionado
            + ", _userName: " + _userName);
        AsignacionTurnoVO asignacion=new AsignacionTurnoVO();
        asignacion.setUsername(_userName);
        
        int idTurnoActual = asignacionDao.getTurnoActual(_empresaId, _rutEmpleado);
        System.out.println(WEB_NAME+"[AsignacionTurnoBp."
            + "validaTurnoEmpleado]"
            + "empresaId: " + _empresaId
            + ", rutEmpleado: " + _rutEmpleado
            + ", turnoActual: " + idTurnoActual);
        
        
        if (idTurnoActual == -99){
            //No hay turno asignado. Insertar
            System.out.println(WEB_NAME+"[AsignacionTurnoBp."
                + "validaTurnoEmpleado]No hay turno asignado. Insertar nueva asignacion");
            asignacion.setEmpresaId(_empresaId);
            asignacion.setRutEmpleado(_rutEmpleado);
            asignacion.setIdTurno(_idTurnoSeleccionado);
            
            asignacionDao.insertaTurno(asignacion);
        }else{
            /**
            *   .- Si el turno seleccionado desde el formulario de modificación de empleado 
            *      es distinto al turno actualmente asignado, 
            *      se debe:
            *          a- Setear 'turno_asignacion'.'fecha_hasta' = fecha de modificacion del empleado y 
            *          b- Se debe insertar el nuevo turno en turno_asignacion. 
            */
            if (_idTurnoSeleccionado != idTurnoActual){
                //a- Setear 'turno_asignacion'.'fecha_hasta' = fecha de modificacion del empleado (vencer turno actual)
                System.out.println(WEB_NAME+"[AsignacionTurnoBp."
                    + "validaTurnoEmpleado]"
                    + "Setear 'turno_asignacion'.'fecha_hasta' = fecha_hora_actual (vencer turno actual)");
                asignacion.setEmpresaId(_empresaId);
                asignacion.setRutEmpleado(_rutEmpleado);
                asignacion.setIdTurno(idTurnoActual);
                asignacionDao.venceTurno(asignacion);
                
                System.out.println(WEB_NAME+"[AsignacionTurnoBp."
                    + "validaTurnoEmpleado]"
                    + "insertar el nuevo turno asignado en tabla 'turno_asignacion'.");
                //b- Se debe insertar el nuevo turno en turno_asignacion. 
                asignacion.setIdTurno(_idTurnoSeleccionado);
                asignacionDao.insertaTurno(asignacion);
                
            }
            
        }
    
    }
}
