/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.SolicitudVacacionesVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioCentroCostoVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.util.List;

/**
 *
 * @author Alexander
 */
public class SolicitudVacacionesBp  extends BaseBp{

    /**
     *
     */
    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.SolicitudVacacionesDAO solicitudDao;
    
    public SolicitudVacacionesBp(PropertiesVO props) {
        this.props      = props;
        eventsService   = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        solicitudDao    = new cl.femase.gestionweb.dao.SolicitudVacacionesDAO();
    }

    /**
    * 
    * @param _empresaId
    * @param _cencoId
    * @param _rutEmpleado
    * @param _startDate
    * @param _endDate
     * @param _usuario
    * @param _propias
    * @param _estado
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * @return 
    */
    public List<SolicitudVacacionesVO> getSolicitudes(String _empresaId,
            int _cencoId,
            String _rutEmpleado,
            String _startDate,
            String _endDate,
            UsuarioVO _usuario,
            boolean _propias,
            String _estado,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<SolicitudVacacionesVO> lista = 
            solicitudDao.getSolicitudes(_empresaId, 
                _cencoId, 
                _rutEmpleado, 
                _startDate, 
                _endDate, 
                _usuario, 
                _propias, 
                _estado,
                _jtStartIndex, _jtPageSize, _jtSorting);

        return lista;
    }

    /**
    * 
    * @param _empresaId
    * @param _rutEmpleado
    * @param _startDate
    * @param _endDate
    * @param _usuario
    * @param _estado
    * @param _propias
    * @param _cencosUsuario
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * @return 
    */
    public List<SolicitudVacacionesVO> getSolicitudesAprobarRechazar(String _empresaId,
            String _rutEmpleado,
            String _startDate,
            String _endDate,
            UsuarioVO _usuario,
            String _estado,
            boolean _propias,
            List<UsuarioCentroCostoVO> _cencosUsuario,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<SolicitudVacacionesVO> lista = 
            solicitudDao.getSolicitudesAprobarRechazar(_empresaId, 
                _rutEmpleado, 
                _startDate, 
                _endDate, 
                _usuario, 
                _estado,
                _propias,
                _cencosUsuario,
                _jtStartIndex, _jtPageSize, _jtSorting);

        return lista;
    }
    
    /**
    * 
    * @param _id
    * @return 
    */
    public SolicitudVacacionesVO getSolicitudByKey(int _id){
        return solicitudDao.getSolicitudByKey(_id);
    }
    
    /**
    * 
    * @param _idSolicitud
    * @param _username
     * @param _fechaHoraCancelacion
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO cancelarSolicitud(int _idSolicitud, 
            String _username, String _fechaHoraCancelacion, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO updValues = 
            solicitudDao.cancelarSolicitud(_idSolicitud, 
                _username, 
                _fechaHoraCancelacion);
        
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
    * @param _idSolicitud
    * @param _fechaHoraAprobacion
    * @param _usernameAprueba
     * @param _notaObservacion
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO aprobarSolicitud(int _idSolicitud, 
            String _usernameAprueba, 
            String _fechaHoraAprobacion,
            String _notaObservacion,
             MaintenanceEventVO _eventdata){
        ResultCRUDVO updValues = 
            solicitudDao.aprobarSolicitud(_idSolicitud, 
                _usernameAprueba, 
                _fechaHoraAprobacion, _notaObservacion);
        
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
    * @param _idSolicitud
    * @param _fechaHoraRechazo
    * @param _usernameRechaza
     * @param _motivoRechazo
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO rechazarSolicitud(int _idSolicitud, 
            String _usernameRechaza,
            String _fechaHoraRechazo,
            String _motivoRechazo,
             MaintenanceEventVO _eventdata){
        ResultCRUDVO updValues = 
            solicitudDao.rechazarSolicitud(_idSolicitud, 
                _usernameRechaza, 
                _fechaHoraRechazo, 
                _motivoRechazo);
        
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
    * @param _objToInsert
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO insert(SolicitudVacacionesVO _objToInsert, 
            MaintenanceEventVO _eventdata){
        
        ResultCRUDVO insValues = solicitudDao.insert(_objToInsert);
        
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
    * @param _rutEmpleado
    * @param _startDate
    * @param _endDate
    * @param _usuario
    * @param _propias
    * @param _estado
    * @return 
    */
    public int getSolicitudesCount(String _empresaId,
            String _rutEmpleado,
            String _startDate,
            String _endDate,
            UsuarioVO _usuario, 
            boolean _propias,
            String _estado){
        return solicitudDao.getSolicitudesCount(_empresaId,
            _rutEmpleado, 
            _startDate, 
            _endDate, 
            _usuario, 
            _propias, 
            _estado);
    }
    
    /**
    * 
    * @param _empresaId
    * @param _rutEmpleado
    * @param _startDate
    * @param _endDate
    * @param _usuario
    * @param _estado
    * @param _propias
    * @param _cencosUsuario
    * @return 
    */
    public int getSolicitudesAprobarRechazarCount(String _empresaId,
            String _rutEmpleado,
            String _startDate,
            String _endDate,
            UsuarioVO _usuario, 
            String _estado,
            boolean _propias,
            List<UsuarioCentroCostoVO> _cencosUsuario){
        return solicitudDao.getSolicitudesAprobarRechazarCount(_empresaId,
            _rutEmpleado, 
            _startDate, 
            _endDate, 
            _usuario, 
            _estado,
            _propias,
            _cencosUsuario);
    }

}
