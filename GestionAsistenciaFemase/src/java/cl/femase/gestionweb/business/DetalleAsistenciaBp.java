/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.dao.LogErrorDAO;
import cl.femase.gestionweb.vo.CalculoHorasVO;
import cl.femase.gestionweb.vo.DetalleAsistenciaToInsertVO;
import cl.femase.gestionweb.vo.DetalleAsistenciaVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.LogErrorVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.json.JSONObject;

/**
*
* @author Alexander
*/
public class DetalleAsistenciaBp  extends BaseBp{

    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.DetalleAsistenciaDAO calculoHorasService;
    
    public DetalleAsistenciaBp(PropertiesVO props) {
        this.props = props;
        eventsService = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        calculoHorasService = new cl.femase.gestionweb.dao.DetalleAsistenciaDAO(this.props);
    }

    public List<DetalleAsistenciaVO> getDetalles(String _rutEmpleado,
            String _startDate, String _endDate){
    
        List<DetalleAsistenciaVO> lista = 
            calculoHorasService.getDetalles(_rutEmpleado, _startDate, _endDate);

        return lista;
    }
    
    /**
    * 
    * @param _listaEmpleados
    * @param _startDate
    * @param _endDate
    * @param _idTurno
    * 
    * @return 
    */
    public LinkedHashMap<String,List<DetalleAsistenciaVO>> getDetallesInforme(List<EmpleadoVO> _listaEmpleados,
            String _startDate, String _endDate, int _idTurno){
    
        LinkedHashMap<String,List<DetalleAsistenciaVO>> lista = 
            calculoHorasService.getDetallesInforme(_listaEmpleados, 
                _startDate, _endDate, _idTurno);

        return lista;
    }
    
    public List<DetalleAsistenciaVO> getDetallesHist(String _rutEmpleado,
            String _startDate, String _endDate){
    
        List<DetalleAsistenciaVO> lista = 
            calculoHorasService.getDetallesHist(_rutEmpleado, _startDate, _endDate);

        return lista;
    }
    
    public List<CalculoHorasVO> getHeaders(String _empresaId,
            String _deptoId, int _cencoId,
            String _rutEmpleado,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<CalculoHorasVO> lista = 
            calculoHorasService.getHeaders(_empresaId, _deptoId, _cencoId, _rutEmpleado, _jtStartIndex, _jtPageSize, _jtSorting);

        return lista;
    }

//    public MaintenanceVO delete(String _rutEmpleado,
//            String _fechaMarca, 
//            MaintenanceEventVO _eventdata){
//        MaintenanceVO deletedValues = calculoHorasService.delete(_rutEmpleado,_fechaMarca);
//        
//        //if (!updValues.isThereError()){
//            String msgFinal = deletedValues.getMsg();
//            deletedValues.setMsg(msgFinal);
//            _eventdata.setDescription(msgFinal);
//            //insertar evento 
//            eventsService.addEvent(_eventdata); 
//        //}
//        
//        return deletedValues;
//    }
    
    public MaintenanceVO update(String _rutEmpleado,
            String _fechaMarca,
            String _authAtraso,
            String _authHextras,
            String _hhmmExtrasAutorizadas, 
            MaintenanceEventVO _eventdata){
        MaintenanceVO updatedValues = 
            calculoHorasService.update(_rutEmpleado,
                _fechaMarca,
                _authAtraso,
                _authHextras,
                _hhmmExtrasAutorizadas);
        
        //if (!updValues.isThereError()){
            String msgFinal = updatedValues.getMsg();
            updatedValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return updatedValues;
    }
    
    public void saveList(ArrayList<DetalleAsistenciaToInsertVO> _entities){
        try {
            calculoHorasService.saveList(_entities);
        } catch (SQLException ex) {
            System.err.println("[DetalleAsistenciaBp]"
                + "Error: " + ex.toString());
            String clase = "DetalleAsistenciaBp";
            String exclabel = ex.toString();
            JSONObject jsonObj = Utilidades.generateErrorMessage(clase, ex);
            LogErrorDAO logDao  = new LogErrorDAO();
            LogErrorVO log      = new LogErrorVO();
            log.setModulo(Constantes.LOG_MODULO_ASISTENCIA);
            log.setEvento(Constantes.LOG_EVENTO_CONSULTA_ASISTENCIA);
            log.setLabel(exclabel);
            log.setDetalle(jsonObj.toString());
            logDao.insert(log);
            System.err.println("Error al insertar "
                + "asistencias: "+ex.toString());
        }
    }
    
//    public void setMarcasProcesadas(ArrayList<DetalleAsistenciaToInsertVO> _entities){
//        try {
//            calculoHorasService.setMarcasProcesadas(_entities); 
//        } catch (SQLException ex) {
//            System.err.println("[setMarcasProcesadas]Error al "
//                + "setear marcas como procesadas: " 
//                + ex.toString());
//        }
//    }

    /**
     *
     * @param _entities
     */
    
    public void deleteList(ArrayList<DetalleAsistenciaToInsertVO> _entities){
        try {
            calculoHorasService.deleteList(_entities);
        } catch (SQLException ex) {
            System.err.println("Error al eliminar "
                + "detalles asistencia: "+ex.toString());
        }
    }
    
    public MaintenanceVO insert(DetalleAsistenciaVO _detalle, 
            MaintenanceEventVO _eventdata){
        
        MaintenanceVO insValues = calculoHorasService.insert(_detalle);
        
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
     * @param _deptoId
     * @param _cencoId
     * @return
     */
    public int getHeadersCount(String _empresaId,
            String _deptoId, int _cencoId){
        return calculoHorasService.getHeadersCount(_empresaId, _deptoId, _cencoId, _empresaId);
    }
    
    public boolean existeCalculo(String _rutEmpleado,
            String _fechaMarca){
    
        return calculoHorasService.existeCalculo(_rutEmpleado, _fechaMarca);
    }

    public void openDbConnection(){
        calculoHorasService.openDbConnection();
    }
    
    public void closeDbConnection(){
        calculoHorasService.closeDbConnection();
    }
}
