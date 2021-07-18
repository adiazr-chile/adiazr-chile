/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.MarcaRechazoVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.util.List;

/**
 *
 * @author Alexander
 */
public class MarcasRechazosBp {

    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.MarcasRechazosDAO marcasService;
    
    public MarcasRechazosBp(PropertiesVO props) {
        this.props = props;
        eventsService = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        marcasService = new cl.femase.gestionweb.dao.MarcasRechazosDAO(this.props);
    }

    public List<MarcaRechazoVO> getMarcasRechazadas(String _empresaId,
            String _rutEmpleado, 
            String _dispositivoId,
            String _startDate, 
            String _endDate,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<MarcaRechazoVO> lista = 
            marcasService.getMarcasRechazadas(_empresaId,
                _rutEmpleado, 
                _dispositivoId, 
                _startDate, 
                _endDate,
                _jtStartIndex, 
                _jtPageSize, 
                _jtSorting);
        return lista;
    }
    
    public List<MarcaRechazoVO> getMarcasRechazadasHist(String _empresaId,
            String _rutEmpleado,
            String _dispositivoId,
            String _startDate, 
            String _endDate,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<MarcaRechazoVO> lista = 
            marcasService.getMarcasRechazadasHist(_empresaId,
                _rutEmpleado, _dispositivoId, _startDate, _endDate, _jtStartIndex, 
                _jtPageSize, _jtSorting);
        return lista;
    }
    
    public int getMarcasRechazadasCount(String _empresaId,
            String _rutEmpleado,
            String _dispositivoId,
            String _startDate, 
            String _endDate){
        
        return marcasService.getMarcasRechazadasCount(_empresaId, _rutEmpleado, 
            _dispositivoId, _startDate, _endDate);
    }
  
    public int getMarcasRechazadasHistCount(String _empresaId,
            String _rutEmpleado,
            String _dispositivoId,
            String _startDate, 
            String _endDate){
        
        return marcasService.getMarcasRechazadasHistCount(_empresaId, _rutEmpleado,_dispositivoId, 
            _startDate, _endDate);
    }
      
}
