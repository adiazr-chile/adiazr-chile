/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.TipoAusenciaVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.util.List;

/**
 *
 * @author Alexander
 */
public class TipoAusenciaBp  extends BaseBp{

    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.TipoAusenciaDAO tiposAusenciasService;
    
    public TipoAusenciaBp(PropertiesVO props) {
        this.props = props;
        eventsService = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        tiposAusenciasService = new cl.femase.gestionweb.dao.TipoAusenciaDAO(this.props);
    }

    public List<TipoAusenciaVO> getTipos(String _nombre,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<TipoAusenciaVO> lista = 
            tiposAusenciasService.getTipos(_nombre, _jtStartIndex, 
                _jtPageSize, _jtSorting);

        return lista;
    }

    public int getTiposCount(String _nombre){
        return tiposAusenciasService.getTiposCount(_nombre);
    }

}
