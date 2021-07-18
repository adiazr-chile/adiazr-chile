/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.EventoMantencionVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.SearchFilterVO;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author Alexander
 */
public class MaintenanceEventsBp {

    public PropertiesVO props;
    private cl.femase.gestionweb.dao.MaintenanceEventsDAO eventosDao;

    public MaintenanceEventsBp(PropertiesVO props) {
        this.props = props;
        eventosDao = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
    }

    public LinkedHashMap<String,String> getEventTypes(){
        
        LinkedHashMap<String,String> listaTipos = eventosDao.getEventTypes();

        return listaTipos;
    }

    public LinkedHashMap<String,MaintenanceEventVO> getEvents(SearchFilterVO _filter){
        
        LinkedHashMap<String,MaintenanceEventVO> eventslist = eventosDao.getEvents(_filter);

        return eventslist;
    }
        
    public MaintenanceVO addEvent(MaintenanceEventVO _eventdata){
        MaintenanceVO eventdata = eventosDao.addEvent(_eventdata); 
        
        return eventdata;
    }

    /**
     * 
     * @param _username
     * @param _tipoEvento
     * @param _startDate
     * @param _endDate
     * @param _jtStartIndex
     * @param _jtPageSize
     * @param _jtSorting
     * @return 
     */
    public List<EventoMantencionVO> getEventosMantencion(String _username,
            String _tipoEvento, 
            String _startDate,
            String _endDate,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<EventoMantencionVO> lista = 
            eventosDao.getEventosMantencion(_username,_tipoEvento, 
                _startDate,_endDate, 
                _jtStartIndex, 
                _jtPageSize, _jtSorting);

        return lista;
    }
    
    /**
     * 
     * @param _username
     * @param _tipoEvento
     * @param _startDate
     * @param _endDate
     * @param _jtStartIndex
     * @param _jtPageSize
     * @param _jtSorting
     * @return 
     */
    public List<EventoMantencionVO> getEventosMantencionHist(String _username,
            String _tipoEvento, 
            String _startDate,
            String _endDate,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<EventoMantencionVO> lista = 
            eventosDao.getEventosMantencionHist(_username,_tipoEvento, 
                _startDate,_endDate, 
                _jtStartIndex, 
                _jtPageSize, _jtSorting);

        return lista;
    }
    
    public int getEventosMantencionCount(String _username,
            String _tipoEvento, 
            String _startDate,
            String _endDate){
        
        int rows = 
            eventosDao.getEventosMantencionCount(_username,_tipoEvento, 
                _startDate,_endDate);

        return rows;
    }
    
    public int getEventosMantencionHistCount(String _username,
            String _tipoEvento, 
            String _startDate,
            String _endDate){
        
        int rows = 
            eventosDao.getEventosMantencionHistCount(_username,_tipoEvento, 
                _startDate,_endDate);

        return rows;
    }
    
    public void openDbConnection(){
        eventosDao.openDbConnection();
    }
    public void closeDbConnection(){
        eventosDao.closeDbConnection();
    }
}
