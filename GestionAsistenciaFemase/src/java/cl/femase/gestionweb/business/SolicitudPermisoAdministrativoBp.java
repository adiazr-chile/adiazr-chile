/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.SolicitudPermisoAdministrativoVO;

/**
 *
 * @author Alexander
 */
public class SolicitudPermisoAdministrativoBp {

    /**
     *
     */
    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.SolicitudPermisoAdministrativoDAO solicitudDao;
    
    public SolicitudPermisoAdministrativoBp(PropertiesVO props) {
        this.props      = props;
        eventsService   = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        solicitudDao    = new cl.femase.gestionweb.dao.SolicitudPermisoAdministrativoDAO();
    }

    /**
    * 
    * @param _objToInsert
    * @param _eventdata
    * @return 
    */
    public MaintenanceVO insert(SolicitudPermisoAdministrativoVO _objToInsert, 
            MaintenanceEventVO _eventdata){
        
        MaintenanceVO insValues = solicitudDao.insert(_objToInsert);
        
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
    * @param _idSolicitud
    * @param _username
     * @param _fechaHoraCancelacion
    * @param _eventdata
    * @return 
    */
    public MaintenanceVO cancelarSolicitud(int _idSolicitud, 
            String _username, String _fechaHoraCancelacion, 
            MaintenanceEventVO _eventdata){
        MaintenanceVO updValues = 
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
    public MaintenanceVO aprobarSolicitud(int _idSolicitud, 
            String _usernameAprueba, 
            String _fechaHoraAprobacion,
            String _notaObservacion,
             MaintenanceEventVO _eventdata){
        MaintenanceVO updValues = 
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
    public MaintenanceVO rechazarSolicitud(int _idSolicitud, 
            String _usernameRechaza,
            String _fechaHoraRechazo,
            String _motivoRechazo,
             MaintenanceEventVO _eventdata){
        MaintenanceVO updValues = 
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
}
