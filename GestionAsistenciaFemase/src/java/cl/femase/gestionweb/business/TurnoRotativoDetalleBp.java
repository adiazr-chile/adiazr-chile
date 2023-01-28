/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.DetalleTurnoVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.TurnoRotativoDetalleVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Alexander
 */
public class TurnoRotativoDetalleBp  extends BaseBp{

    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.TurnoRotativoDetalleDAO turnoRotativoDetalleService;
    
    public TurnoRotativoDetalleBp(PropertiesVO props) {
        this.props = props;
        eventsService = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        turnoRotativoDetalleService = new cl.femase.gestionweb.dao.TurnoRotativoDetalleDAO(this.props);
    }

    /**
     *
     * @param _entities
     */
    public void saveList(ArrayList<TurnoRotativoDetalleVO> _entities){
        try {
            turnoRotativoDetalleService.saveDetailList(_entities);
        } catch (SQLException ex) {
            System.err.println("Error al insertar "
                + "asistencias: "+ex.toString());
        }
    }
    
    public void deleteDetalleTurnoEmpleado(TurnoRotativoDetalleVO _detalle){
        ResultCRUDVO updValues = turnoRotativoDetalleService.deleteDetalleTurnoEmpleado(_detalle);
        
        //if (!updValues.isThereError()){
//            String msgFinal = updValues.getMsg();
//            updValues.setMsg(msgFinal);
//            _eventdata.setDescription(msgFinal);
//            //insertar evento 
//            eventsService.addEvent(_eventdata); 
        //}
        
       // return updValues;
    }
    
    /**
     * 
     * @param _empresaId
     * @param _rutEmpleado
     * @param _anio
     * @param _mes
     * @param _fecha
     * 
     * @deprecated 
     * 
     * @return 
     */
    public DetalleTurnoVO getDetalleTurnoLaboralByFecha(String _empresaId, 
            String _rutEmpleado,
            int _anio, int _mes, 
            String _fecha){
        return turnoRotativoDetalleService.getDetalleTurnoLaboralByFecha(_empresaId, 
            _rutEmpleado, 
            _anio, 
            _mes, 
            _fecha);
    }
    
    /**
     * @return 
     * @deprecated 
     */
    public DetalleTurnoVO getDetalleTurnoLibreByFecha(String _empresaId, 
            String _rutEmpleado,
            int _anio, int _mes, 
            String _fecha){
        return turnoRotativoDetalleService.getDetalleTurnoLibreByFecha(_empresaId, 
            _rutEmpleado, 
            _anio, 
            _mes, 
            _fecha);
    }
    
    public TurnoRotativoDetalleVO getDetalleTurno(TurnoRotativoDetalleVO _detalle){
        return turnoRotativoDetalleService.getDetalleTurno(_detalle);
    }
    
    public void openDbConnection(){
        turnoRotativoDetalleService.openDbConnection();
    }
    
    public void closeDbConnection(){
        turnoRotativoDetalleService.closeDbConnection();
    }
}
