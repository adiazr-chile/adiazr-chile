/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.CentroCostoVO;
import cl.femase.gestionweb.vo.DepartamentoVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.OrganizacionEmpresaVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.util.List;

/**
 *
 * @author Alexander
 * @deprecated 
 */
public class OrganizacionEmpresaBp  extends BaseBp{

    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsService;
    private final cl.femase.gestionweb.dao.OrganizacionEmpresaDAO organizacionService;
    
    public OrganizacionEmpresaBp(PropertiesVO props) {
        this.props = props;
        eventsService = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        organizacionService = new cl.femase.gestionweb.dao.OrganizacionEmpresaDAO(this.props);
    }

    public List<DepartamentoVO> getDepartamentos(String _empresaId){
        
        List<DepartamentoVO> lista = 
            organizacionService.getDepartamentos(_empresaId);

        return lista;
    }
    
     public List<CentroCostoVO> getCentrosCosto(String _empresaId, String _deptoId){
        
        List<CentroCostoVO> lista = 
            organizacionService.getCentrosCosto(_empresaId, _deptoId);

        return lista;
    }

    /**
     *
     * @param _empresaId
     * @param _deptoId
     * @param _cencoId
     * @param _jtStartIndex
     * @param _jtPageSize
     * @param _jtSorting
     * @return
     */
    public List<OrganizacionEmpresaVO> getOrganizacion(String _empresaId, 
            String _deptoId,int _cencoId,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<OrganizacionEmpresaVO> lista = 
            organizacionService.getOrganizacion(_empresaId, _deptoId, _cencoId, _jtStartIndex, 
                _jtPageSize, _jtSorting);

        return lista;
    }
    
    public int getOrganizacionCount(String _empresaId, 
            String _deptoId,int _cencoId){
        return organizacionService.getOrganizacionCount(_empresaId, _deptoId, _cencoId);
    }
    
     public MaintenanceVO insert(OrganizacionEmpresaVO _objToInsert, 
            MaintenanceEventVO _eventdata){
        
        MaintenanceVO insValues = organizacionService.insert(_objToInsert);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
    
     public MaintenanceVO delete(OrganizacionEmpresaVO _objToDelete, 
            MaintenanceEventVO _eventdata){
        
        MaintenanceVO deleteValues = organizacionService.delete(_objToDelete);
        
        //if (!updValues.isThereError()){
            String msgFinal = deleteValues.getMsg();
            deleteValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsService.addEvent(_eventdata); 
        //}
        
        return deleteValues;
    }
}
