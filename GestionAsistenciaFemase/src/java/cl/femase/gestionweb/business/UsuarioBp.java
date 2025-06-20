/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.business;

import cl.femase.gestionweb.vo.UsuarioVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.ModuloSistemaVO;
import cl.femase.gestionweb.vo.UsuarioCentroCostoVO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author Alexander
 */
public class UsuarioBp  extends BaseBp{

    public PropertiesVO props;
    private cl.femase.gestionweb.dao.UsersDAO userDao;
    /** para guardar los eventos de mantencion de informacion*/
    private cl.femase.gestionweb.dao.MaintenanceEventsDAO eventsDao;
    private cl.femase.gestionweb.dao.ModuloAccesoPerfilDAO accesosPerfilDao;

    public UsuarioBp(PropertiesVO props) {
        this.props = props;
        userDao = new cl.femase.gestionweb.dao.UsersDAO(this.props);
        accesosPerfilDao = new cl.femase.gestionweb.dao.ModuloAccesoPerfilDAO(this.props);
        eventsDao = new cl.femase.gestionweb.dao.MaintenanceEventsDAO(this.props);
    }

    public UsuarioVO getLogin(UsuarioVO _user){
        UsuarioVO dataUser = userDao.getLogin(_user);
        return dataUser;
    }
    
    public List<UsuarioVO> getUsuarios(String _username,
            String _nombre,
            String _apePaterno,
            int _idPerfil,
            int _idEstado,
            String _empresaId,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<UsuarioVO> lista = 
            userDao.getUsuarios(_username, _nombre, 
                    _apePaterno, _idPerfil, 
                    _idEstado,_empresaId, 
                    _jtStartIndex, _jtPageSize, _jtSorting);

        return lista;
    }

    /**
    *
    * @param _usuario
    * @return
    */
    public List<UsuarioCentroCostoVO> getCencosUsuario(UsuarioVO _usuario){
        return userDao.getCencosUsuario(_usuario);
    }
    
    public boolean deleteCencos(String _username){
        return userDao.deleteCencos(_username);
    }
    
    /**
     *
     * @param _data
     * @return
     */
    public ResultCRUDVO insertCenco(UsuarioCentroCostoVO _data){
        return userDao.insertCenco(_data);
    }
    
    public UsuarioVO getUsuario(String _username){
        return userDao.getUsuario(_username);
    }
    
    /**
    * 
    * @param _userToUpdate
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO update(UsuarioVO _userToUpdate, 
            MaintenanceEventVO _eventdata){
        boolean existeEmail = false;
        ResultCRUDVO updValues = new ResultCRUDVO();
        if (existeEmail){
            updValues.setThereError(true);
            updValues.setCodError(99);
            updValues.setMsgError("El email " + _userToUpdate.getEmail()
                +" ya existe en el Sistema.");
            updValues.setMsg("Error al modificar usuario '" + _userToUpdate.getUsername()
                + "'. El email " + _userToUpdate.getEmail()
                +" ya existe en el Sistema.");
        }else{
            updValues = userDao.update(_userToUpdate);
        }
        
        String msgFinal = updValues.getMsg();
        updValues.setMsg(msgFinal);
        _eventdata.setDescription(msgFinal);
        _eventdata.setEmpresaId(_userToUpdate.getEmpresaId());

        eventsDao.addEvent(_eventdata); 
        
        return updValues;
    }
    
    /**
    * 
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO disableTemporaryUsers(MaintenanceEventVO _eventdata){
        
        ResultCRUDVO updValues = userDao.disableTemporaryUsers();
        String msgFinal = updValues.getMsg();
        updValues.setMsg(msgFinal);
        _eventdata.setDescription(msgFinal);
        _eventdata.setEmpresaId("emp01");
        
        eventsDao.addEvent(_eventdata); 
        
        return updValues;
    }
    
    
    //Disable temporary users
    
    
    /**
    * 
    * @param _userToInsert
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO insert(UsuarioVO _userToInsert, 
            MaintenanceEventVO _eventdata){

        boolean existeEmail = false;
        ResultCRUDVO insValues = new ResultCRUDVO();
        
        System.out.println(WEB_NAME+"[UsuarioBp.insert]Insertar usuario, inicio...");
        String msgFinal = insValues.getMsg();
        if (existeEmail){
            insValues.setThereError(true);
            insValues.setCodError(99);
            insValues.setMsgError("El email " + _userToInsert.getEmail()
                +" ya existe en el Sistema.");
            insValues.setMsg("Error al crear usuario '" + _userToInsert.getUsername()
                + "'. El email " + _userToInsert.getEmail()
                +" ya existe en el Sistema.");
            System.out.println(WEB_NAME+"[UsuarioBp.insert]Error: " + insValues.getMsg());
            msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsDao.addEvent(_eventdata); 
        }else{
            System.out.println(WEB_NAME+"[UsuarioBp.insert]Insertar usuario, realizar insert.");
            insValues = userDao.insert(_userToInsert);
            msgFinal = insValues.getMsg();
        }
        
        if (!insValues.isThereError()){
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            System.out.println(WEB_NAME+"[UsuarioBp.insert]"
                + "Insertar evento de insercion de usuario. "
                + "Descripcion: "+msgFinal);
            //insertar evento 
            eventsDao.addEvent(_eventdata); 
            
        }
        
        return insValues;
    }
    
    /**
    * 
    * @param _userToInsert
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO insertTemporaryUser(UsuarioVO _userToInsert, 
            MaintenanceEventVO _eventdata){

        boolean existeEmail = false;
        ResultCRUDVO insValues = new ResultCRUDVO();
        
        System.out.println(WEB_NAME+"[UsuarioBp.insertTemporaryUser]Insertar usuario temporal, inicio...");
        String msgFinal = insValues.getMsg();
        if (existeEmail){
            insValues.setThereError(true);
            insValues.setCodError(99);
            insValues.setMsgError("El email " + _userToInsert.getEmail()
                +" ya existe en el Sistema.");
            insValues.setMsg("Error al crear usuario temporal '" + _userToInsert.getUsername()
                + "'. El email " + _userToInsert.getEmail()
                +" ya existe en el Sistema.");
            System.out.println(WEB_NAME+"[UsuarioBp.insertTemporaryUser]"
                + "Error: " + insValues.getMsg());
            msgFinal = insValues.getMsg();
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            //insertar evento 
            eventsDao.addEvent(_eventdata); 
        }else{
            System.out.println(WEB_NAME+"[UsuarioBp.insertTemporaryUser]"
                + "Insertar usuario temporal, realizar insert.");
            insValues = userDao.insertTemporaryUser(_userToInsert);
            msgFinal = insValues.getMsg();
        }
        
        if (!insValues.isThereError()){
            insValues.setMsg(msgFinal);
            _eventdata.setDescription(msgFinal);
            System.out.println(WEB_NAME+"[UsuarioBp.insertTemporaryUser]"
                + "Insertar evento de insercion de usuario temporal. "
                + "Descripcion: "+msgFinal);
            //insertar evento 
            eventsDao.addEvent(_eventdata); 
            
        }
        
        return insValues;
    }
    
    
    public int getUsuariosCount(String _username,
            String _nombre,
            String _apePaterno,
            int _idPerfil,
            int _idEstado,
            String _empresaId){
        
        return userDao.getUsuariosCount(_username, 
            _nombre, _apePaterno, 
            _idPerfil, _idEstado, _empresaId);
    }
    
    public ResultCRUDVO setConnectionStatus(UsuarioVO _data){
        return userDao.setConnectionStatus(_data);
    }
    
    public ResultCRUDVO setPassword(String _username,String _password, MaintenanceEventVO _eventdata){
        ResultCRUDVO theResult = userDao.setPassword(_username, _password);
        String msgFinal = theResult.getMsg();
        theResult.setMsg(msgFinal);
        _eventdata.setDescription(msgFinal);
        //insertar evento 
        eventsDao.addEvent(_eventdata);
        return theResult;
    }
    
    public LinkedHashMap<String, ModuloSistemaVO> getModulosSistemaByEstado(int _estadoid){
        return userDao.getModulosSistemaByEstado(_estadoid);
    }
    
    public LinkedHashMap<String, ModuloSistemaVO> getModulosSistemaByPerfilUsuario(int _perfilUsuario){
        return userDao.getModulosSistemaByPerfilUsuario(_perfilUsuario);
    }
    
    /**
     *
     * @param _usuarios
     * @throws SQLException
     */
    public void saveListUsuarios(ArrayList<UsuarioVO> _usuarios) throws SQLException {
        userDao.saveListUsuarios(_usuarios);
    }
    
    public void saveListCencos(ArrayList<UsuarioCentroCostoVO> _centroscosto) throws SQLException {
        userDao.saveListCencos(_centroscosto);
    }
    
    /**
    * 
    * @param _email
    * @return 
    */
    public boolean existeEmail(String _email){
        return userDao.existeEmail(_email);
    }
    
    public void openDbConnection(){
        System.out.println(WEB_NAME+"[UsuarioBp.openDbConnection]"
            + "Abriendo conexion a la BD...");
        userDao.openDbConnection();
    }
    
    /**
    * 
    */
    public void closeDbConnection(){
        System.out.println(WEB_NAME+"[UsuarioBp.closeDbConnection]"
            + "Cerrando conexion a la BD...");
        userDao.closeDbConnection();
    }
}
