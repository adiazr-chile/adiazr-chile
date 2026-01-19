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
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
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

    private static UsuarioVO usuarioEnSesion;
    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.DetalleAsistenciaDAO calculoAsistenciaDao;
    
    /**
    * 
     * @param props
    */
    public DetalleAsistenciaBp(PropertiesVO props, UsuarioVO _usuario) {
        this.props = props;
        eventsService = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        calculoAsistenciaDao = new cl.femase.gestionweb.dao.DetalleAsistenciaDAO(this.props);
        this.usuarioEnSesion = _usuario;
    }

    /**
    * 
    * @param _rutEmpleado
    * @param _startDate
    * @param _endDate
    * @return 
    */
    public List<DetalleAsistenciaVO> getDetalles(String _rutEmpleado,
            String _startDate, String _endDate){
    
        List<DetalleAsistenciaVO> lista = 
            calculoAsistenciaDao.getDetalles(_rutEmpleado, _startDate, _endDate);

        return lista;
    }
    
    /**
    * Lista resumen con detalles de asistencia
    * 
     * @param _listaEmpleados
     * @param _startDate
     * @param _endDate
     * @return 
    */
    public ArrayList<DetalleAsistenciaVO> getDetalleAsistencia(
            List<EmpleadoVO> _listaEmpleados,
            String _startDate, 
            String _endDate){
     
        return calculoAsistenciaDao.getDetalleAsistencia(_listaEmpleados, _startDate, _endDate);
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
            calculoAsistenciaDao.getDetallesInforme(_listaEmpleados, 
                _startDate, _endDate, _idTurno);

        return lista;
    }
    
    /**
    * 
    * @param _empresaId
    * @param _rutEmpleado
    * @param _startDate
    * @param _endDate
    * 
    * @return 
    */
    public List<DetalleAsistenciaVO> getDetallesInformeEmpleado(String _empresaId, 
            String _rutEmpleado,
            String _startDate,
            String _endDate){
    
        List<DetalleAsistenciaVO> lista = 
            calculoAsistenciaDao.getDetallesInformeEmpleado(_empresaId, 
                _rutEmpleado, 
                _startDate, 
                _endDate);

        return lista;
    }
    
    /**
    * 
    * @param _rutEmpleado
    * @param _startDate
    * @param _endDate
    * @return 
    */
    public List<DetalleAsistenciaVO> getDetallesHist(String _rutEmpleado,
            String _startDate, String _endDate){
    
        List<DetalleAsistenciaVO> lista = 
            calculoAsistenciaDao.getDetallesHist(_rutEmpleado, _startDate, _endDate);

        return lista;
    }
    
    /**
    * 
    * @param _empresaId
    * @param _deptoId
    * @param _cencoId
    * @param _rutEmpleado
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * @return 
    */
    public List<CalculoHorasVO> getHeaders(String _empresaId,
            String _deptoId, int _cencoId,
            String _rutEmpleado,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<CalculoHorasVO> lista = 
            calculoAsistenciaDao.getHeaders(_empresaId, _deptoId, _cencoId, _rutEmpleado, _jtStartIndex, _jtPageSize, _jtSorting);

        return lista;
    }

//    public ResultCRUDVO delete(String _rutEmpleado,
//            String _fechaMarca, 
//            MaintenanceEventVO _eventdata){
//        ResultCRUDVO deletedValues = calculoAsistenciaDao.delete(_rutEmpleado,_fechaMarca);
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
    
    /**
    * 
     * @param _rutEmpleado
     * @param _fechaMarca
     * @param _authAtraso
     * @param _authHextras
     * @param _hhmmExtrasAutorizadas
     * @param _eventdata
     * @return 
    */
    public ResultCRUDVO update(String _rutEmpleado,
            String _fechaMarca,
            String _authAtraso,
            String _authHextras,
            String _hhmmExtrasAutorizadas, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO updatedValues = 
            calculoAsistenciaDao.update(_rutEmpleado,
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
    
    /**
    * 
    * @param _entities
    */
    public void saveList(ArrayList<DetalleAsistenciaToInsertVO> _entities){
        try {
            calculoAsistenciaDao.saveList(_entities);
        } catch (SQLException ex) {
            System.err.println("[DetalleAsistenciaBp]"
                + "Error: " + ex.toString());
            String clase = "DetalleAsistenciaBp";
            String exclabel = ex.toString();
            JSONObject jsonObj = Utilidades.generateErrorMessage(clase, ex);
            LogErrorDAO logDao  = new LogErrorDAO();
            LogErrorVO log      = new LogErrorVO();
            log.setUserName(usuarioEnSesion.getUsername());
            log.setModulo(Constantes.LOG_MODULO_ASISTENCIA);
            log.setEvento(Constantes.LOG_EVENTO_CONSULTA_ASISTENCIA);
            log.setLabel(exclabel);
            log.setDetalle(jsonObj.toString());
            logDao.insert(log);
            System.err.println("Error al insertar "
                + "asistencias: "+ex.toString());
        }
    }
    
    /**
    * 
    * @param _rutEmpleado
    * @param _fechaMarca
    * @param _hhmmExtras
    * @param _autorizaHrsExtras
    * @param _hhmmExtrasAutorizadas
    * @return 
    */
    public ResultCRUDVO updateDataHorasExtras(String _rutEmpleado,
            String _fechaMarca,
            String _hhmmExtras,
            String _autorizaHrsExtras,
            String _hhmmExtrasAutorizadas){
    
        ResultCRUDVO updatedValues = calculoAsistenciaDao.updateDataHorasExtras(_rutEmpleado, 
            _fechaMarca, _hhmmExtras,
            _autorizaHrsExtras, _hhmmExtrasAutorizadas);

        return updatedValues;
    }
    
//    public void setMarcasProcesadas(ArrayList<DetalleAsistenciaToInsertVO> _entities){
//        try {
//            calculoAsistenciaDao.setMarcasProcesadas(_entities); 
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
            calculoAsistenciaDao.deleteList(_entities);
        } catch (SQLException ex) {
            System.err.println("Error al eliminar "
                + "detalles asistencia: "+ex.toString());
        }
    }
    
    /**
    * 
    * @param _detalle
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO insert(DetalleAsistenciaVO _detalle, 
            MaintenanceEventVO _eventdata){
        
        ResultCRUDVO insValues = calculoAsistenciaDao.insert(_detalle);
        
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
        return calculoAsistenciaDao.getHeadersCount(_empresaId, _deptoId, _cencoId, _empresaId);
    }
    
    /**
    * 
    * @param _rutEmpleado
    * @param _fechaMarca
    * @return 
    */
    public boolean existeCalculo(String _rutEmpleado,
        String _fechaMarca){
      return calculoAsistenciaDao.existeCalculo(_rutEmpleado, _fechaMarca);
    }

    /**
    * 
    */
    public void openDbConnection(){
        calculoAsistenciaDao.openDbConnection();
    }
    
    /**
    * 
    */
    public void closeDbConnection(){
        calculoAsistenciaDao.closeDbConnection();
    }
}
