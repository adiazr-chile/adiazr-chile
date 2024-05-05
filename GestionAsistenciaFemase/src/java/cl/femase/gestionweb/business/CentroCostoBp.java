/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.vo.CentroCostoVO;
import cl.femase.gestionweb.vo.DispositivoVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioCentroCostoVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Alexander
 */
public class CentroCostoBp  extends BaseBp{

    public PropertiesVO props;
    /** para guardar los eventos de mantencion de informacion*/
    private final cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsDao;
    private final cl.femase.gestionweb.dao.CentroCostoDAO centrocostoDao;
    
    /**
    * 
    * @param props
    */
    public CentroCostoBp(PropertiesVO props) {
        this.props = props;
        eventsDao = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
        centrocostoDao = new cl.femase.gestionweb.dao.CentroCostoDAO(this.props);
    }

    /**
    * 
    * @param _empresaId
    * @param _deptoId
    * @param _cencoId
    * @return 
    */
    public List<EmpleadoVO> getDirectoresCenco(String _empresaId, 
            String _deptoId, 
            int _cencoId){
        return centrocostoDao.getDirectoresCenco(_empresaId, _deptoId, _cencoId);
    }
    
    /**
    * 
    * @param _usuario
    * @param _deptoId
    * @return 
    */
    public List<CentroCostoVO> getCentrosCostoDepto(UsuarioVO _usuario, String _deptoId){
        List<CentroCostoVO> listaFinal = new ArrayList<>();
        List<CentroCostoVO> listaBase = 
            centrocostoDao.getCentrosCostoDepto(_usuario,_deptoId);
        
        listaBase.forEach((temp) -> {
            temp.setDispositivos(centrocostoDao.getDispositivosAsignados(temp.getId()));
            listaFinal.add(temp);
        });
        
        return listaFinal;
    }
    
    /**
    * 
    * @param _username
    * @return 
    */
    public List<UsuarioCentroCostoVO> getAllCentrosCosto(String _username){ 
        return centrocostoDao.getAllCentrosCosto(_username);
    }
    
    /**
    * 
    * @param _deptoId
    * @param _cencoId
    * @return 
    */
    public CentroCostoVO getCentroCostoByKey(String _deptoId,
            int _cencoId){
        
        CentroCostoVO cenco = centrocostoDao.getCentroCostoByKey(_deptoId, _cencoId);
        if (cenco != null){
            HashMap<String,DispositivoVO> dispositivos = 
                centrocostoDao.getDispositivosAsignados(_cencoId);
            cenco.setDispositivos(dispositivos);
        }
        return cenco;
    }
    
    public List<CentroCostoVO> getCentrosCosto(String _depto, 
            String _nombre,
            int _estado,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        List<CentroCostoVO> listaFinal = new ArrayList<>();
        
        List<CentroCostoVO> listaBase = 
            centrocostoDao.getCentrosCosto(_depto, 
                _nombre, 
                _estado, 
                _jtStartIndex, 
                _jtPageSize, 
                _jtSorting);

        listaBase.forEach((temp) -> {
            temp.setDispositivos(centrocostoDao.getDispositivosAsignados(temp.getId()));
            listaFinal.add(temp);
        });
        
        return listaFinal;
    }
    
    public List<CentroCostoVO> getCentrosCosto(UsuarioVO _usuario, 
            String _depto, 
            String _nombre,
            int _estado,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        List<CentroCostoVO> listaFinal = new ArrayList<>();
        List<CentroCostoVO> listaBase;
        if (_usuario.getIdPerfil() != Constantes.ID_PERFIL_SUPER_ADMIN){
            //filtrar cencos: mostrar solo los asignados al usuario
            listaBase = 
                centrocostoDao.getCentrosCosto(_usuario, 
                    _depto, 
                    _nombre, 
                    _estado, 
                    _jtStartIndex, 
                    _jtPageSize, 
                    _jtSorting);
        }else {
            //mostrar todos los centros de costo existentes
            listaBase = 
                centrocostoDao.getCentrosCosto(_depto, 
                    _nombre, 
                    _estado, 
                    _jtStartIndex, 
                    _jtPageSize, 
                    _jtSorting);
        }
        
        listaBase.forEach((temp) -> {
            temp.setDispositivos(centrocostoDao.getDispositivosAsignados(temp.getId()));
            listaFinal.add(temp);
        });
        
        return listaFinal;
    }

    public ResultCRUDVO update(CentroCostoVO _objectToUpdate, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO updValues = centrocostoDao.update(_objectToUpdate);
        
        //if (!updValues.isThereError()){
            String msgFinal = updValues.getMsg();
            updValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsDao.addEvent(_eventdata); 
        //}
        
        return updValues;
    }
    
    public ResultCRUDVO insert(CentroCostoVO _objToInsert, 
            MaintenanceEventVO _eventdata){
        
        ResultCRUDVO insValues = centrocostoDao.insert(_objToInsert);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsDao.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
    
    /*
    public ResultCRUDVO delete(ContractRelationVO _relationToDelete, 
            MaintenanceEventVO _eventdata){
        ResultCRUDVO insValues = contractRelService.delete(_relationToDelete);
        
        //if (!updValues.isThereError()){
            String msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsDao.addEvent(_eventdata); 
        //}
        
        return insValues;
    }
    */
    
    public int getCentrosCostoCount(String _deptoId, 
            String _nombre, int _estado){
        return centrocostoDao.getCentrosCostoCount(_deptoId, 
            _nombre, _estado);
    }
    
    public List<DispositivoVO> getDispositivosAsignados(int _cencoId){
        
        System.out.println(WEB_NAME+"cl.femase.gestionweb."
            + "business.CentroCostoBp."
            + "getDispositivosAsignados(). CencoID: "+_cencoId);
        List<DispositivoVO> dispositivos=null; 
        
        HashMap<String,DispositivoVO> lista = 
            centrocostoDao.getDispositivosAsignados(_cencoId);
        System.out.println(WEB_NAME+"cl.femase.gestionweb."
            + "business.CentroCostoBp."
            + "getDispositivosAsignados(). "
            + "CencoID: "+_cencoId
            +", lista.size(): "+lista.size());
        if (lista.size() > 0){
            dispositivos = new ArrayList<>();
            //iterando solo sobre valores
            for (DispositivoVO value : lista.values()) {
                dispositivos.add(value);
            }
        }
        return dispositivos;
    }
    
    /**
    * 
    * @return 
    */
    public HashMap<String,DispositivoVO> getDispositivosNoAsignados(){
        return centrocostoDao.getDispositivosNoAsignados();
    }
    
    /**
    * 
    * Retorna nombre de empresa, depto y centro de costo
    * @param _empresaId
    * @param _deptoId
    * @param _cencoId
    * @return 
    */
    public String getEmpresaDeptoCencoJson(String _empresaId,
            String _deptoId,
            int _cencoId){
        
        String jsonOutput = 
            centrocostoDao.getEmpresaDeptoCencoJson(
                _empresaId, _deptoId, _cencoId);

        return jsonOutput;
    }
    
    public void openDbConnection(){
        centrocostoDao.openDbConnection();
    }
    public void closeDbConnection(){
        centrocostoDao.closeDbConnection();
    }
}
