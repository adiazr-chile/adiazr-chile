/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.CentroCostoVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.ModuloAccesoPerfilVO;
import cl.femase.gestionweb.vo.ModuloSistemaVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioCentroCostoVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexander
 */
public class UsersDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    public boolean m_usedGlobalDbConnection = false;
//    //private Connection dbConn;
//    DatabaseLocator dbLocator;
//    String m_dbpoolName;
//    //DbConnectionPool connectionPool = new DbConnectionPool();
//    //DbDirectConnection dbconnection = DbDirectConnection.getInstance();
    
    private String SQL_INSERT_USUARIO = "INSERT INTO usuario("
        + "usr_username, usr_password, "
        + "estado_id, usr_nombres, usr_ape_paterno,"
        + "usr_ape_materno, usr_perfil_id, "
        + "usr_email, "
        + "usr_fecha_ultima_conexion,"
        + "empresa_id,create_datetime) "
        + "VALUES (?, ?, 1, ?, ?,"
        + "?, ?, ?, null,?,current_timestamp)";
    
    private String SQL_INSERT_USUARIO_CENTROCOSTO = 
        "INSERT INTO "
        + "usuario_centrocosto (username, "
            + "ccosto_id, "
            + "por_defecto, "
            + "empresa_id, "
            + "depto_id)"
        + " VALUES (?, ?, 0, ?, ?)";
    
        public UsersDAO(PropertiesVO _propsValues) {

        }

    /**
    * Actualiza un usuario
    * @param _data
    * @return 
    */
    public MaintenanceVO update(UsuarioVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "usuario, "
            + "username: " + _data.getUsername()
            + ",empresaId: " + _data.getEmpresaId()    
            + ", run_empleado: " + _data.getRunEmpleado()
            + ", apellidos: " + _data.getApPaterno()+"-"+_data.getApMaterno()
            + ", nombres encoded: " + _data.getNombresEncode()
            + ", paterno encoded: " + _data.getApPaternoEncode()
            + ", materno encoded: " + _data.getApMaternoEncode()
            + ", email: " + _data.getEmail()
            + ", empresaId: " + _data.getEmpresaId();
        
        try{
            String msgFinal = " Actualiza usuario:"
                + "username [" + _data.getUsername() + "]" 
                + ", empresaId [" + _data.getEmpresaId() 
                + ", run_empleado [" + _data.getRunEmpleado()
                + ", nombres [" + _data.getNombres() 
                + ", apellidos: " + _data.getApPaterno()+"-"+_data.getApMaterno()
                + ", email: " + _data.getEmail()
                + ", empresaId: " + _data.getEmpresaId()    
                + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE usuario "
                + " SET "
                    + "estado_id = ?, "
                    + "usr_nombres = ?, "
                    + "usr_ape_paterno = ?, "
                    + " usr_ape_materno = ?, "
                    + "usr_perfil_id = ?, "
                    + "usr_email = ?,"
                    + "empresa_id = ?, "
                    + "usr_password = ?, "
                    + "last_update_datetime = current_timestamp,"
                    + "run_empleado = ? "
                + " WHERE usr_username = ?";
            dbConn = dbLocator.getConnection(m_dbpoolName,"[UsersDAO.update]");
            
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setInt(1,  _data.getEstado());
            psupdate.setString(2,  _data.getNombresEncode());
            psupdate.setString(3,  _data.getApPaternoEncode());
            psupdate.setString(4,  _data.getApMaternoEncode());
            psupdate.setInt(5,  _data.getIdPerfil());
            psupdate.setString(6,  _data.getEmail());
            psupdate.setString(7,  _data.getEmpresaId());
            psupdate.setString(8,  _data.getPassword());
            psupdate.setString(9,  _data.getRunEmpleado());
            psupdate.setString(10,  _data.getUsername());//filtro del update
                        
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                m_logger.debug("[update]usuario"
                + ", username:" + _data.getUsername()
                + ", empresaId:" + _data.getEmpresaId()
                + ", email:" + _data.getEmail()
                + ", estado:" + _data.getEstado()
                +" actualizado OK!");
            }else{
                System.err.println("[update]usuario. "
                    + "Error al actualizar usuario: username:" + _data.getUsername());
            }

            psupdate.close();
             dbLocator.freeConnection(dbConn);
            //dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("update usuario Error: "+sqle.toString());
            System.err.println("[update]usuario. "
                + "Error1 al actualizar usuario: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }
        finally{
            try {
                if (psupdate!= null) psupdate.close();
                 dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[UsersDAO.update]"
                    + "Error: "+ex.toString());
            }
        }
//        catch(DatabaseException dbe){
//            m_logger.error("update usuario Error: "+dbe.toString());
//            System.err.println("[update]usuario. "
//                + "Error2 al actualizar usuario: "+dbe.toString());
//            objresultado.setThereError(true);
//            objresultado.setCodError(result);
//            objresultado.setMsgError(msgError+" :"+dbe.toString());
//        }
//        finally{
//            
////             dbLocator.freeConnection(dbConn);
//        }

        return objresultado;
    }
    
    /**
    * Actualiza un usuario
    * @param _username
    * @param _estado
    * @return 
    */
    public MaintenanceVO setEstadoUsuario(String _username, int _estado){
        MaintenanceVO objresultado = new MaintenanceVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "estado del usuario, "
            + "username: " + _username
            + ",estado_id: " + _estado;
                
        try{
            String msgFinal = " Actualiza estado del usuario:"
                + "username [" + _username + "]" 
                + ", estado_id [" + _estado + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE usuario "
                + " SET estado_id = ? "
                + " WHERE usr_username = ?";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[UsersDAO.setEstadoUsuario]");
            
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setInt(1,  _estado);
            psupdate.setString(2,  _username);//filtro del update
                        
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                m_logger.debug("[setEstadoUsuario]usuario"
                + ", username:" + _username
                + ", estado_id:" + _estado
                +" actualizado OK!");
            }else{
                System.err.println("[setEstadoUsuario]usuario. "
                    + "Error al actualizar estado usuario: "
                    + "username:" + _username);
            }

            psupdate.close();
             dbLocator.freeConnection(dbConn);
            //dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("update estado usuario Error: "+sqle.toString());
            System.err.println("[setEstadoUsuario]usuario. "
                + "Error1 al actualizar estado del usuario: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }
        finally{
            try {
                if (psupdate!= null) psupdate.close();
                 dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[UsersDAO.setEstadoUsuario]"
                    + "Error: "+ex.toString());
            }
        }

        return objresultado;
    }
    
    /**
     * Elimina los centros de costo asociados al usuario
     * @param _username
     * @return 
     */
    public boolean deleteCencos(String _username){
       
        PreparedStatement psupdate = null;
        boolean result=true;
                       
        try{
            String sql = "delete from usuario_centrocosto "
                + " WHERE username = ?";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[UsersDAO.deleteCencos]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setString(1,  _username);
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                m_logger.debug("[deleteCencos]usuario"
                + ", username:" +_username
                +" actualizado OK!");
            }

            psupdate.close();
             dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("deleteCencos Error: "+sqle.toString());
            result=false;
        }
        finally{
            try {
                if (psupdate!= null) psupdate.close();
                 dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
        
        return result;
    }
    
    /**
     * Actualiza ultima conexion del usuario
     * @param _data
     * @return 
     */
    public MaintenanceVO setConnectionStatus(UsuarioVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "ultima conexion usuario, "
            + "username: "+_data.getUsername();
                
        try{
            String msgFinal = " Actualiza ultima conexion usuario:"
                + "username [" + _data.getUsername() + "]";
            
            System.out.println(WEB_NAME + msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE usuario "
                + " SET "
                + "usr_fecha_ultima_conexion=current_timestamp "
                + " WHERE usr_username = ?";

            //dbConn = dbLocator.getConnection(m_dbpoolName);
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setString(1, _data.getUsername());
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                m_logger.debug("[setConnectionStatus]usuario"
                + ", username:" +_data.getUsername()
                + ", email:" +_data.getEmail()
                + ", estado:" +_data.getEstado()
                +" actualizado OK!");
            }

            psupdate.close();
            //dbLocator.freeConnection(dbConn);
        }catch(SQLException sqle){
            m_logger.error("setConnectionStatus Error: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }
        finally{
            try {
                if (psupdate!= null) psupdate.close();
                 //dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println(WEB_NAME + "[UsersDAO."
                    + "setConnectionStatus]Error: "+ex.toString());
            }
        }
//        catch(DatabaseException dbe){
//            m_logger.error("setConnectionStatus Error: "+dbe.toString());
//            objresultado.setThereError(true);
//            objresultado.setCodError(result);
//            objresultado.setMsgError(msgError+" :"+dbe.toString());
//        }finally{
//             dbLocator.freeConnection(dbConn);
//        }

        return objresultado;
    }

    
    /**
     * Agrega un centro costo al usuario
     * @param _data
     * @return 
     */
    public MaintenanceVO insertCenco(UsuarioCentroCostoVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al insertar "
            + "usuario-ccosto, "
            + "username: " + _data.getUsername()
            + ", empresaId: " + _data.getEmpresaId()
            + ", deptoId: " + _data.getDeptoId()    
            + ", ccostoId: " + _data.getCcostoId()
            + ", porDefecto: " + _data.getPorDefecto();
        
       String msgFinal = " Inserta usuario-ccosto:"
            + "username [" + _data.getUsername() + "]" 
            + ", empresaId [" + _data.getEmpresaId() + "]"   
            + ", deptoId [" + _data.getDeptoId() + "]"      
            + ", ccostoId [" + _data.getCcostoId() + "]"
            + ", porDefecto [" + _data.getPorDefecto() + "]";
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO usuario_centrocosto "
                    + "(username, "
                    + "ccosto_id, "
                    + "por_defecto,"
                    + "empresa_id,"
                    + "depto_id)"
                + " VALUES (?, ?, ?, ?, ?)";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[UsersDAO.insertCenco]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getUsername());
            insert.setInt(2,  _data.getCcostoId());
            insert.setInt(3,  _data.getPorDefecto());
            insert.setString(4,  _data.getEmpresaId());
            insert.setString(5,  _data.getDeptoId());
            
            int filasAfectadas = insert.executeUpdate();
            m_logger.debug("[insert usuario_centrocosto]filasAfectadas: "+filasAfectadas);
            if (filasAfectadas == 1){
                m_logger.debug("[insert usuario_centrocosto]"
                    + ", username:" +_data.getUsername()
                    + ", empresaId: " + _data.getEmpresaId()
                    + ", deptoId: " + _data.getDeptoId()    
                    + ", ccostoId: " + _data.getCcostoId()
                    + ", porDefecto: " + _data.getPorDefecto()
                    +" insertado OK!");
            }
            
            insert.close();
             dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("insert usuario_centrocosto Error: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (insert!= null) insert.close();
                 dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println(WEB_NAME + "Error: " + ex.toString());
            }
        }

        return objresultado;
    }
    
     /**
     * Agrega un nuevo usuario
     * @param _data
     * @return 
     */
    public MaintenanceVO insert(UsuarioVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al insertar "
            + "usuario, "
            + "username: "+_data.getUsername()
            + ", email: "+_data.getEmail()
            + ", empresaId: "+_data.getEmpresaId()
            + ", run: "+_data.getRunEmpleado();
        
       String msgFinal = " Inserta usuario:"
            + "username [" + _data.getUsername() + "]" 
            + ", email [" + _data.getEmail() + "]"
            + ", empresaId [" + _data.getEmpresaId() + "]"
            + ", run [" + _data.getRunEmpleado() + "]";
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO usuario( "
                + " usr_username, "
                + "usr_password, estado_id, "
                + "usr_nombres, "
                + "usr_ape_paterno,"
                + " usr_ape_materno, "
                + "usr_perfil_id, "
                + "usr_email, "
                + "empresa_id,"
                + "create_datetime,"
                + "run_empleado) "
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp, ?)";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[UsersDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getUsername());
            insert.setString(2,  _data.getPassword());
            insert.setInt(3,  _data.getEstado());
            insert.setString(4,  _data.getNombresEncode());
            insert.setString(5,  _data.getApPaternoEncode());
            insert.setString(6,  _data.getApMaternoEncode());
            insert.setInt(7,  _data.getIdPerfil());
            insert.setString(8,  _data.getEmail());
            insert.setString(9,  _data.getEmpresaId());
            insert.setString(10,  _data.getRunEmpleado());
            
            int filasAfectadas = insert.executeUpdate();
            m_logger.debug("[insert usuario]"
                + "filasAfectadas: "+filasAfectadas);
            if (filasAfectadas == 1){
                m_logger.debug("[insert usuario]"
                    + ", username:" +_data.getUsername()
                    + ", empresaId:" +_data.getEmpresaId()    
                    + ", nombre:" +_data.getNombres()
                    + ", email:" +_data.getEmail()
                    + ", perfil:" +_data.getIdPerfil()
                    +" insertado OK!");
            }
            
            insert.close();
             dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("insert usuario Error: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (insert!= null) insert.close();
                 dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println(WEB_NAME + "Error: "+ex.toString());
            }
        }
        
        return objresultado;
    }
    
////    /**
////     * Elimina un usuario (deja el usuario en estado No Vigente)
////     * @param _data
////     * @return 
////     */
////    public MaintenanceVO delete(UsuarioVO _data){
////        MaintenanceVO objresultado = new MaintenanceVO();
////        int result=0;
////        String msgError = "Error al dejar usuario como No Vigente "
////            + ", username: "+_data.getUsername();
////        
////        String msgFinal = " Deja usuario como no vigente:"
////            + "username [" + _data.getUsername() + "]";
////        objresultado.setMsg(msgFinal);
////        PreparedStatement insert    = null;
////        
////        try{
////            String sql = "update usuario set  "
////                + "estado_id = ? "
////                + " WHERE usr_username = ?";
////
////            dbConn = dbLocator.getConnection(m_dbpoolName);dbConn = dbLocator.getConnection(m_dbpoolName);
////            insert = dbConn.prepareStatement(sql);
////            insert.setInt(1,  2);
////                        
////            int filasAfectadas = insert.executeUpdate();
////            m_logger.debug("[delete usuario]filasAfectadas: "+filasAfectadas);
////            if (filasAfectadas == 1){
////                m_logger.debug("[delete usuario]"
////                    + ", username:" +_data.getUsername()
////                    +" eliminado(no vigente) OK!");
////            }
////            
////            insert.close();
////             dbLocator.freeConnection(dbConn);
////        }catch(SQLException|DatabaseException sqle){
////            m_logger.error("delete usuario Error: "+sqle.toString());
////            objresultado.setThereError(true);
////            objresultado.setCodError(result);
////            objresultado.setMsgError(msgError+" :"+sqle.toString());
////        }catch(DatabaseException dbe){
////            m_logger.error("delete usuario Error: "+dbe.toString());
////            objresultado.setThereError(true);
////            objresultado.setCodError(result);
////            objresultado.setMsgError(msgError+" :"+dbe.toString());
////        }finally{
////             dbLocator.freeConnection(dbConn);
////        }
////
////        return objresultado;
////    }
        
    /**
    * Retorna lista de usuarios existentes
    * 
    * @param _username
    * @param _nombre
    * @param _apePaterno
    * @param _idPerfil
    * @param _idEstado
    * @param _empresaId
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * @return 
    */
    public List<UsuarioVO> getUsuarios(String _username,
            String _nombre,
            String _apePaterno,
            int _idPerfil,
            int _idEstado,
            String _empresaId,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<UsuarioVO> lista = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        UsuarioVO data;
        
        try{
            String sql = "SELECT "
                    + "usr_username, usr_password, "
                    + "usuario.estado_id, "
                    + "usuario.run_empleado,"
                    + "usr_nombres, "
                    + "usr_ape_paterno, "
                    + "usr_ape_materno, "
                    + "usr_nombres || ' ' || usr_ape_paterno || ' ' || usr_ape_materno fullname, "    
                    + "usr_perfil_id, "
                    + "usr_email, "
                    + "usr_fecha_ultima_conexion,"
                    + "perfil_usuario.perfil_nombre,"
                    + "coalesce(usuario.empresa_id,'-1') empresa_id,"
                    + "to_char(create_datetime,'YYYY-MM-DD HH24:MI:SS') create_datetime,"
                    + "to_char(last_update_datetime,'YYYY-MM-DD HH24:MI:SS') last_update_datetime, "
                    + "coalesce(perfil_usuario.admin_empresa,'N') admin_empresa "
                + " FROM usuario inner join perfil_usuario "
                        + "on usuario.usr_perfil_id = perfil_usuario.perfil_id "
                + " where (1 = 1) ";
                        
            if (_username != null && _username.compareTo("") != 0){        
                sql += " and usr_username like '" + _username + "%'";
            }
            
            if (_nombre != null && _nombre.compareTo("") != 0){        
                sql += " and upper(usr_nombres) like '" + _nombre.toUpperCase() + "%'";
            }
            
            if (_apePaterno != null && _apePaterno.compareTo("") != 0){        
                sql += " and upper(usr_ape_paterno) like '" + _apePaterno.toUpperCase() + "%'";
            }
            
            if (_idPerfil != -1){        
                sql += " and usr_perfil_id = " + _idPerfil+" ";
            }
            
            if (_idEstado != -1){        
                sql += " and usuario.estado_id = " + _idEstado+" ";
            }
            
            if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
                sql += " and empresa_id = '" + _empresaId+"' ";
            }
            
            sql += " order by " + _jtSorting;
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            System.out.println(WEB_NAME+"[UsersDAO.getUsuarios]sql: "+sql);
            if (!m_usedGlobalDbConnection) dbConn = dbLocator.getConnection(m_dbpoolName,"[UsersDAO.getUsuarios]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new UsuarioVO();
              
                data.setUsername(rs.getString("usr_username"));
                data.setPassword(rs.getString("usr_password"));
                data.setEstado(rs.getInt("estado_id"));
                data.setNombres(rs.getString("usr_nombres"));
                data.setRunEmpleado(rs.getString("run_empleado"));
                data.setApPaterno(rs.getString("usr_ape_paterno"));
                data.setApMaterno(rs.getString("usr_ape_materno"));
                data.setNombreCompleto(rs.getString("fullname"));
                data.setIdPerfil(rs.getInt("usr_perfil_id"));
                data.setNomPerfil(rs.getString("perfil_nombre"));
                data.setEmail(rs.getString("usr_email"));
                data.setFechaHoraUltimaConexion(rs.getString("usr_fecha_ultima_conexion"));
                data.setLabelUsuario(data.getNombreCompleto()+" ("+data.getNomPerfil()+")");
                data.setEmpresaId(rs.getString("empresa_id"));
                //last_update_datetime
                data.setFechaCreacion(rs.getString("create_datetime"));
                data.setFechaActualizacion(rs.getString("last_update_datetime"));
                data.setAdminEmpresa(rs.getString("admin_empresa"));
//                data.setCencos(this.getCencosUsuario(data.getUsername()));
//                if (data.getCencos().size() > 0){
//                    data.setCencoUsuario(data.getCencos().get(0).getCcostoNombre());//cenco por defecto
//                }
                lista.add(data);
            }

            ps.close();
            rs.close();
            if (!m_usedGlobalDbConnection){
                dbConn.close();
                dbLocator.freeConnection(dbConn);
            }
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("getUsuarios Error: "+sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (!m_usedGlobalDbConnection){
                    dbConn.close();
                    dbLocator.freeConnection(dbConn);
                }
                 
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
        return lista;
    }
    
    /**
     * Retorna usuario existente
     * 
     * @param _username
     * @return 
     */
    public UsuarioVO getUsuario(String _username){
        PreparedStatement ps = null;
        ResultSet rs = null;
        UsuarioVO data=null;
        
        try{
            String sql = "SELECT "
                + "usr_username, "
                + "usr_password, "
                + "usuario.estado_id, "
                + "coalesce(run_empleado,'') run_empleado, "
                + "usr_nombres, "
                + "usr_ape_paterno, "
                + "usr_ape_materno, "
                + "usr_nombres || ' ' || usr_ape_paterno || ' ' || usr_ape_materno fullname,  "
                + "usr_perfil_id, "
                + "usr_email, "
                + "usr_fecha_ultima_conexion,"
                + "perfil_usuario.perfil_nombre,"
                + "coalesce(perfil_usuario.admin_empresa,'N') admin_empresa,"   
                + "coalesce(usuario.empresa_id,'-1') empresa_id "
                + " FROM usuario inner join perfil_usuario "
                + "on usuario.usr_perfil_id = perfil_usuario.perfil_id "
                + " where (usr_username = '" + _username + "') ";
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[UsersDAO.getUsuario]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                data = new UsuarioVO();
              
                data.setUsername(rs.getString("usr_username"));
                data.setPassword(rs.getString("usr_password"));
                data.setEstado(rs.getInt("estado_id"));
                data.setRunEmpleado(rs.getString("run_empleado"));
                data.setNombres(rs.getString("usr_nombres"));
                data.setApPaterno(rs.getString("usr_ape_paterno"));
                data.setApMaterno(rs.getString("usr_ape_materno"));
                data.setNombreCompleto(rs.getString("fullname"));
                data.setIdPerfil(rs.getInt("usr_perfil_id"));
                data.setNomPerfil(rs.getString("perfil_nombre"));
                data.setEmail(rs.getString("usr_email"));
                data.setFechaHoraUltimaConexion(rs.getString("usr_fecha_ultima_conexion"));
                data.setLabelUsuario(data.getNombreCompleto()+" ("+data.getNomPerfil()+")");
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setAdminEmpresa(rs.getString("admin_empresa"));
                
                data.setCencos(this.getCencosUsuario(data));
            }

            ps.close();
            rs.close();
            dbConn.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("getUsuario Error: "+sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                dbConn.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
        return data;
    }
    
    /**
     * 
     * @param _username
     * @param _nombre
     * @param _apePaterno
     * @param _idPerfil
     * @param _idEstado
     * @param _empresaId
     * @return 
     */    
    public int getUsuariosCount(String _username,
            String _nombre,
            String _apePaterno,
            int _idPerfil,
            int _idEstado,
            String _empresaId){
        int count=0;
        Statement statement = null;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[UsersDAO.getUsuariosCount]");
            statement = dbConn.createStatement();
            String sql="SELECT count(*) as count "
                + "FROM usuario where 1 = 1 ";
            
            if (_username != null && _username.compareTo("") != 0){        
                sql += " and usr_username like '" + _username + "%'";
            }
            
            if (_nombre != null && _nombre.compareTo("") != 0){        
                sql += " and upper(usr_nombres) like '" + _nombre.toUpperCase() + "%'";
            }
            
            if (_apePaterno != null && _apePaterno.compareTo("") != 0){        
                sql += " and upper(usr_ape_paterno) like '" + _apePaterno.toUpperCase() + "%'";
            }
            
            if (_idPerfil != -1){        
                sql += " and usr_perfil_id = " + _idPerfil+" ";
            }
            
            if (_idEstado != -1){        
                sql += " and usuario.estado_id = " + _idEstado+" ";
            }
            
            if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
                sql += " and empresa_id = '" + _empresaId+"' ";
            }
            
            ResultSet rs = statement.executeQuery(sql);		
            if (rs.next()) {
                count=rs.getInt("count");
            }
            
            statement.close();
            rs.close();
             dbLocator.freeConnection(dbConn);
        } catch (SQLException|DatabaseException e) {
                e.printStackTrace();
        }
        finally{
            try {
                if (statement != null) statement.close();
                 dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
        
        return count;
    }
   
    /**
    * Retorna lista de centros de costos que el usuario puede ver.
    * 
    * @param _usuario
    * @return 
    */
    public List<UsuarioCentroCostoVO> getCencosUsuario(UsuarioVO _usuario){
        
        List<UsuarioCentroCostoVO> listaCencosUsuario = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        UsuarioCentroCostoVO data;
        CentroCostoDAO cencosDAO = new CentroCostoDAO(null);
        try{
            String sqlCencosUsuario = "select "
                + "uc.username,"
                + "uc.empresa_id,"
                + "uc.depto_id,"
                + "uc.ccosto_id,"
                + "uc.por_defecto,"
                + "cc.ccosto_nombre,"
                + "depto.empresa_id,"
                + "empresa.empresa_nombre,"
                + "depto.depto_nombre,"
                + "coalesce(cc.es_zona_extrema,'N') es_zona_extrema " 
                + "FROM usuario_centrocosto uc "
                + "inner join centro_costo cc on (uc.ccosto_id = cc.ccosto_id) "
                + " inner join departamento depto on (cc.depto_id = depto.depto_id) "
                + " inner join empresa on (depto.empresa_id = empresa.empresa_id) "
                + " where (username = '" + _usuario.getUsername() + "') ";
                    //+ 
            if (_usuario.getIdPerfil() != Constantes.ID_PERFIL_SUPER_ADMIN){
                //deptos y cencos vigentes
                sqlCencosUsuario += " and (depto.estado_id = 1 and cc.estado_id = 1) "; 
            }
            
            sqlCencosUsuario += "order by cc.depto_id,"
                + "cc.ccosto_nombre";
            
            System.out.println(WEB_NAME+"[UsersDAO.getCencosUsuario]"
                + "El usuario: " + _usuario.getUsername()
                + " es admin de empresa?(S/N): " + _usuario.getAdminEmpresa());
            if (_usuario.getAdminEmpresa().compareTo("S") == 0){
                System.out.println(WEB_NAME+"[UsersDAO.getCencosUsuario]"
                    + "El usuario: " + _usuario.getUsername()
                    + " administra todos los centros de "
                    + "costo de la empresa_id: " + _usuario.getEmpresaId());
                //obtener todos los cencos de la empresa del usuario
                List<CentroCostoVO> cencosMostrar = 
                    cencosDAO.getCentrosCostoEmpresa(_usuario);
                Iterator<CentroCostoVO> it = cencosMostrar.iterator();
                while(it.hasNext()){
                    CentroCostoVO cenco = it.next();
                    UsuarioCentroCostoVO uc = 
                        new UsuarioCentroCostoVO(_usuario.getUsername(),
                            cenco.getId(),
                            -1,
                            cenco.getNombre(),
                            cenco.getEmpresaId(),
                            cenco.getDeptoId());
                    uc.setEmpresaId(cenco.getEmpresaId());
                    uc.setEmpresaNombre(cenco.getEmpresaNombre());
                    uc.setDeptoId(cenco.getDeptoId());
                    uc.setDeptoNombre(cenco.getDeptoNombre());
                    uc.setZonaExtrema(cenco.getZonaExtrema());
                    listaCencosUsuario.add(uc);
                }
            }else{
                System.out.println(WEB_NAME+"[UsersDAO.getCencosUsuario]"
                    + "El usuario: " + _usuario.getUsername()
                    + " NO ES admin de empresa. "
                    + "Mostrar solo los cencos asignados");
                
                if (!m_usedGlobalDbConnection) dbConn = 
                    dbLocator.getConnection(m_dbpoolName,
                        "[UsersDAO.getCencosUsuario]");
           
                ps = dbConn.prepareStatement(sqlCencosUsuario);
                rs = ps.executeQuery();
                while (rs.next()){
                    data = new UsuarioCentroCostoVO(rs.getString("username"),
                        rs.getInt("ccosto_id"),
                        rs.getInt("por_defecto"),
                        rs.getString("ccosto_nombre"),
                        rs.getString("empresa_id"),
                        rs.getString("depto_id"));

                    data.setEmpresaId(rs.getString("empresa_id"));
                    data.setEmpresaNombre(rs.getString("empresa_nombre"));
                    data.setDeptoId(rs.getString("depto_id"));
                    data.setDeptoNombre(rs.getString("depto_nombre"));
                    data.setZonaExtrema(rs.getString("es_zona_extrema"));

//                    System.err.println("[UsersDAO.getCencosUsuario]"
//                        + "Usuario " + _usuario.getUsername() 
//                        + ", add cenco= " + data.getCcostoId());
                    listaCencosUsuario.add(data);
                }

                ps.close();
                rs.close();
                if (!m_usedGlobalDbConnection){
                    dbConn.close();
                    dbLocator.freeConnection(dbConn);
                }
            }
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[UsersDAO."
                + "getCencosUsuario]Error: "+sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (!m_usedGlobalDbConnection){
                    dbConn.close();
                    dbLocator.freeConnection(dbConn);
                }
            } catch (SQLException ex) {
                System.err.println("[UsersDAO."
                    + "getCencosUsuario]Error: "+ex.toString());
            }
        }
        return listaCencosUsuario;
    }
    
    /**
    * Actualiza un usuario
    * @param _username
    * @param _password
    * @return 
    */
    public MaintenanceVO setPassword(String _username,String _password){
        MaintenanceVO objresultado = new MaintenanceVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "clave de usuario, "
            + "username: " + _username;
                
        try{
            String msgFinal = " Actualiza clave de usuario:"
                + "username [" + _username + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE usuario "
                + " SET "
                + "usr_password = ? "
                + " WHERE usr_username = ?";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[UsersDAO.setPassword]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setString(1,  _password);
            psupdate.setString(2,  _username);//filtro del update
                        
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println(WEB_NAME+"[UsersDAO.setPassword]usuario"
                    + ", username:" +_username
                    + " actualizado OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[UsersDAO.setPassword]"
                + "update clave de usuario "
                + "Error: " + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }
        return objresultado;
    }
    
    /**
     * retorna data del usuario 
     * @param _user
     * @return 
     */
    public UsuarioVO getLogin(UsuarioVO _user){
        UsuarioVO user = new UsuarioVO();
        int idPerfil=0;
        System.err.println(WEB_NAME + "[UsersDAO.getLogin]validando usuario...");
        try {
            String sql = "SELECT "
                + "usr_username,"
                + "usr_password,"
                + "usuario.estado_id,"
                + "usr_nombres,"
                + "usr_ape_paterno,"
                + "usr_ape_materno,"
                + "usr_nombres || ' ' || usr_ape_paterno || ' ' || usr_ape_materno fullname,  "
                + "usr_perfil_id,"
                + "usr_email,"
                + "usr_fecha_ultima_conexion,"
                + "perfil_usuario.perfil_nombre,"
                + "coalesce(usuario.empresa_id,'-1') empresa_id,"
                + "empresa.empresa_nombre,"
                + "empresa.empresa_logo,"
                + "empleado.depto_id,"
                + "departamento.depto_nombre,"
                + "empleado.cenco_id,"
                + "centro_costo.ccosto_nombre,"
                + " CASE WHEN mv.admite_marca_virtual is null THEN 'N' ELSE 'S' END as marcacion_virtual,"
                + "mv.fecha1,mv.fecha2,mv.fecha3,mv.fecha4,"
                + "mv.fecha5,mv.fecha6,mv.fecha7,"
                + "coalesce(registrar_ubicacion,'N') registrar_ubicacion, "
                + "coalesce(perfil_usuario.admin_empresa,'N') admin_empresa, "
                + "coalesce(run_empleado,'') run_empleado "
                + "  FROM usuario "
                + "	inner join perfil_usuario on usuario.usr_perfil_id = perfil_usuario.perfil_id "
                + "	left outer join empresa on usuario.empresa_id = empresa.empresa_id "
                + "	left outer join empleado on (usuario.usr_username = empleado.empl_rut) "
                + "	left outer join departamento on (empleado.depto_id = departamento.depto_id) "
                + "	 left outer join centro_costo on (empleado.cenco_id = centro_costo.ccosto_id) "
                + "  left outer join marcacion_virtual mv "
                    + "on (usuario.empresa_id = mv.empresa_id and usuario.usr_username = mv.rut_empleado)"
                + " where usr_username = '" + _user.getUsername() + "' "
                + " and usr_password = '" + _user.getPassword() + "' "
                + "	and usuario.estado_id = 1";
            System.out.println(WEB_NAME+"[UsersDAO.getLogin]sql: " + sql);    
            PreparedStatement stmt = dbConn.prepareStatement(sql);
            
            try {
               ResultSet rs = stmt.executeQuery();
               try {
                    // do whatever it is you want to do
                    if (rs.next()){
                        System.out.println(WEB_NAME+"[getLogin]Usuario logeado OK...");
                        user.setUsername(_user.getUsername());
                        user.setPassword(_user.getPassword());
                        user.setEstado(rs.getInt("estado_id"));
                        user.setNombres(rs.getString("usr_nombres"));
                        user.setRunEmpleado(rs.getString("run_empleado"));
                        user.setApPaterno(rs.getString("usr_ape_paterno"));
                        user.setApMaterno(rs.getString("usr_ape_materno"));
                        user.setNombreCompleto(rs.getString("fullname"));
                        idPerfil = rs.getInt("usr_perfil_id");
                        user.setEmail(rs.getString("usr_email"));
                        user.setNomPerfil(rs.getString("perfil_nombre"));
                        user.setIdPerfil(idPerfil);
                        user.setAdminEmpresa(rs.getString("admin_empresa"));
                        //14-04-2018
                        user.setEmpresaId(rs.getString("empresa_id"));
                        user.setEmpresaNombre(rs.getString("empresa_nombre"));
                        user.setEmpresaLogo(rs.getString("empresa_logo"));

                        //15-05-2018
                        user.setDeptoId(rs.getString("depto_id"));
                        user.setDeptoNombre(rs.getString("depto_nombre"));
                        user.setCencoId(rs.getString("cenco_id"));
                        user.setCencoNombre(rs.getString("ccosto_nombre"));
                        //obtener listado de modulos vigentes
                        LinkedHashMap<String, ModuloSistemaVO> modulos=getModulosSistemaByPerfilUsuario(idPerfil);

                        LinkedHashMap<String, LinkedHashMap<String, ModuloAccesoPerfilVO>> auxaccesosModulo
                                    = new LinkedHashMap<>();
                        //iterar modulos del sistema
                        Iterator<ModuloSistemaVO> it = modulos.values().iterator();
                        while (it.hasNext())
                        {
                            ModuloSistemaVO currentModulo = it.next();
                            auxaccesosModulo.put(""+currentModulo.getModulo_id(), 
                                getModuloAccesosByPerfil(currentModulo.getModulo_id(),idPerfil));
                        }
                        user.setAccesosModulo(auxaccesosModulo);
                        user.setCencos(this.getCencosUsuario(user));
                        user.setMarcacionVirtual(rs.getString("marcacion_virtual"));
                        user.setRegistrarUbicacion(rs.getString("registrar_ubicacion"));
                        LinkedHashMap<String, String> fechasMarcacionVirtual
                            = new LinkedHashMap<>();
                        String fecha1 = rs.getString("fecha1");
                        String fecha2 = rs.getString("fecha2");
                        String fecha3 = rs.getString("fecha3");
                        String fecha4 = rs.getString("fecha4");
                        String fecha5 = rs.getString("fecha5");
                        String fecha6 = rs.getString("fecha6");
                        String fecha7 = rs.getString("fecha7");
                        
                        if (fecha1 != null && fecha1.compareTo("") != 0) fechasMarcacionVirtual.put(fecha1, fecha1);
                        if (fecha2 != null && fecha2.compareTo("") != 0) fechasMarcacionVirtual.put(fecha2, fecha2);
                        if (fecha3 != null && fecha3.compareTo("") != 0) fechasMarcacionVirtual.put(fecha3, fecha3);
                        if (fecha4 != null && fecha4.compareTo("") != 0) fechasMarcacionVirtual.put(fecha4, fecha4);
                        if (fecha5 != null && fecha5.compareTo("") != 0) fechasMarcacionVirtual.put(fecha5, fecha5);
                        if (fecha6 != null && fecha6.compareTo("") != 0) fechasMarcacionVirtual.put(fecha6, fecha6);
                        if (fecha7 != null && fecha7.compareTo("") != 0) fechasMarcacionVirtual.put(fecha7, fecha7);
                        
                        user.setFechasMarcacionVirtual(fechasMarcacionVirtual);
                    }else{
                        user = null;
                        System.err.println(WEB_NAME +"[UsersDAO.getLogin]"
                            + "Usuario No existe!");
                    }
               } finally {
                  rs.close();
               }
            } finally {
               stmt.close();
            }
        }catch(SQLException error){
            System.err.println(WEB_NAME +"[UsersDAO.getLogin]Error: "+error.toString());
        }finally {
////            try {
////                System.err.println("[UsersDAO.getLogin]Cerrar conexion a la BD...");
////                dbConn.close();
////                dbLocator.freeConnection(dbConn);
////            } catch (SQLException ex) {
////                System.err.println("[UsersDAO.getLogin]Error: "+ ex.toString());
////            }
        }
        
        return user;
    }
    
    /**
     * Retorna lista de accesos por modulo, para el usuario
     * 
     * @param _moduloId
     * @param _idPerfil
     * @return 
     */
    public LinkedHashMap<String, ModuloAccesoPerfilVO> getModuloAccesosByPerfil(int _moduloId, 
            int _idPerfil){
        
        LinkedHashMap<String, ModuloAccesoPerfilVO> lista =
            new LinkedHashMap<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        ModuloAccesoPerfilVO data;
        
        try{
            String sql = "SELECT " +
                    " usuario.usr_username," +
                    "perfil_usuario.perfil_id," +
                    "modulo_acceso_perfil.modulo_id," +
                    "modulo_sistema.modulo_nombre," +
                    "modulo_sistema.orden_despliegue," +
                    "modulo_acceso_perfil.acceso_id," +
                    "acceso.acceso_nombre," +
                    "acceso.acceso_label," +
                    "acceso.acceso_url," +
                    "modulo_acceso_perfil.tp_acceso_id," +
                    "modulo_acceso_perfil.orden_despliegue,"
                    + "coalesce(modulo_sistema.title,'') title,"
                    + "coalesce(modulo_sistema.sub_title,'') sub_title,"
                    + "coalesce(modulo_sistema.image,'') image,"
                    + "modulo_sistema.quick_access " +
                "FROM usuario," +
                " modulo_sistema, " +
                " acceso, " +
                " modulo_acceso_perfil, " +
                "perfil_usuario " +
                " WHERE " +
                " modulo_acceso_perfil.modulo_id = modulo_sistema.modulo_id AND " +
                " modulo_acceso_perfil.acceso_id = acceso.acceso_id AND " +
                " modulo_acceso_perfil.perfil_id = perfil_usuario.perfil_id " +
                " AND modulo_acceso_perfil.perfil_id = " + _idPerfil +
                " AND modulo_acceso_perfil.modulo_id = " + _moduloId +
                " order by modulo_sistema.orden_despliegue," +
                "modulo_acceso_perfil.orden_despliegue";
           
            //dbConn = dbLocator.getConnection(m_dbpoolName);
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = 
                    new ModuloAccesoPerfilVO(null, rs.getInt("perfil_id"), rs.getInt("modulo_id"), 
                        rs.getString("modulo_nombre"), rs.getInt("acceso_id"), 
                        rs.getString("acceso_nombre"), rs.getString("acceso_label"), 
                        rs.getString("acceso_url"), rs.getInt("tp_acceso_id"));
                
                data.setAccesoRapido(rs.getString("quick_access"));
                data.setModuloTitle(rs.getString("title"));
                data.setModuloSubTitle(rs.getString("sub_title"));
                data.setModuloImagen(rs.getString("image"));
                
                lista.put(data.getModuloId()+"|"+data.getAccesoId(), data);
            }

            ps.close();
            rs.close();
            //dbLocator.freeConnection(dbConn);
        }catch(SQLException sqle){
            m_logger.error("getModuloAccesosByPerfil Error: "+sqle.toString());
        }
        return lista;
    }
    
    /**
     * Retorna lista de modulos del sistema
     * 
     * @param _estadoid
     * @return 
     */
    public LinkedHashMap<String, ModuloSistemaVO> getModulosSistemaByEstado(int _estadoid){
        System.out.println(WEB_NAME+"[UsersDAO.getModulosSistema]inicio");
        LinkedHashMap<String, ModuloSistemaVO> lista =
            new LinkedHashMap<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        ModuloSistemaVO modulo=null;        
        try{
            String sql = "SELECT modulo_id, modulo_nombre, "
                + "estado_id,"
                    + "coalesce(title,'') title,"
                    + "coalesce(sub_title,'') sub_title,"
                    + "coalesce(image,'') image, quick_access "
                + "FROM modulo_sistema where 1=1 ";
            
            if (_estadoid != -1){
                sql+= "and estado_id = " + _estadoid;
            }
            
            sql+= " order by orden_despliegue";
                        
            dbConn = dbLocator.getConnection(m_dbpoolName,"[UsersDAO.getModulosSistemaByEstado]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                modulo = new ModuloSistemaVO();
                modulo.setModulo_id(rs.getInt("modulo_id"));
                modulo.setModulo_nombre(rs.getString("modulo_nombre"));
                
                modulo.setAccesoRapido(rs.getString("quick_access"));
                modulo.setTitulo(rs.getString("title"));
                modulo.setSubTitulo(rs.getString("sub_title"));
                modulo.setImagen(rs.getString("image"));
                                                              
                lista.put(""+modulo.getModulo_id(), modulo);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("getModulosSistema Error: "+sqle.toString());
        }
        
        return lista;
    }
    
    
    /**
     * Retorna lista de modulos del sistema
     * 
     * @param _idPerfil
     * @return 
     */
    public LinkedHashMap<String, ModuloSistemaVO> getModulosSistemaByPerfilUsuario(int _idPerfil){
//        System.out.println(WEB_NAME+"[UsersDAO.getModulosSistemaByPerfilUsuario]inicio");
        LinkedHashMap<String, ModuloSistemaVO> lista =
            new LinkedHashMap<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        ModuloSistemaVO modulo=null;        
        try{
            String sql = "select "
                    + "distinct(accesoperfil.modulo_id) modulo_id, "
                    + "modulo.modulo_nombre,"
                    + "modulo.orden_despliegue,"
                    + "modulo.icon_id,modulo."
                    + "quick_access,"
                    + "coalesce(modulo.title,'') title,"
                    + "coalesce(modulo.sub_title,'') sub_title,"
                    + "coalesce(modulo.image,'') image " +
                "from modulo_acceso_perfil accesoperfil "
                    + "inner join modulo_sistema modulo on "
                        + "modulo.modulo_id = accesoperfil.modulo_id " +
                "where perfil_id = "+_idPerfil 
                +" order by modulo.orden_despliegue";
            
            //dbConn = dbLocator.getConnection(m_dbpoolName);
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                modulo = new ModuloSistemaVO();
                modulo.setModulo_id(rs.getInt("modulo_id"));
                modulo.setModulo_nombre(rs.getString("modulo_nombre"));
                modulo.setIconId(rs.getString("icon_id"));
                
                modulo.setAccesoRapido(rs.getString("quick_access"));
                modulo.setTitulo(rs.getString("title"));
                modulo.setSubTitulo(rs.getString("sub_title"));
                modulo.setImagen(rs.getString("image"));
                
                lista.put(""+modulo.getModulo_id(), modulo);
            }

            ps.close();
            rs.close();
            //dbLocator.freeConnection(dbConn);
        }catch(SQLException sqle){
            m_logger.error("getModulosSistemaByPerfilUsuario Error: "+sqle.toString());
        }
        
        return lista;
    }
    
    /**
    * 
    * @param _usuarios
    * @throws java.sql.SQLException
    */
    public void saveListUsuarios(ArrayList<UsuarioVO> _usuarios) throws SQLException{
        try (
            PreparedStatement statement = dbConn.prepareStatement(SQL_INSERT_USUARIO);
        ) {
            
            int i = 0;
            System.out.println(WEB_NAME+"[UsersDAO.saveListUsuarios]"
                + "items a insertar: "+_usuarios.size());
            for (UsuarioVO entity : _usuarios) {
                System.out.println(WEB_NAME+"[UsersDAO.saveListUsuarios] "
                    + "Insert usuario. "
                    + "username= " + entity.getUsername()
                    + ", nombres= " + entity.getNombres()
                    + ", paterno= " + entity.getApPaterno()
                    + ", email= " + entity.getEmail());
            
                statement.setString(1,  entity.getUsername());
                statement.setString(2,  entity.getPassword());
                statement.setString(3,  entity.getNombres());
                statement.setString(4,  entity.getApPaterno());
                statement.setString(5,  entity.getApMaterno());
                statement.setInt(6,  entity.getIdPerfil());
                statement.setString(7,  entity.getEmail());
                statement.setString(8,  entity.getEmpresaId());
                
                // ...
                statement.addBatch();
                i++;

                if (i % 50 == 0 || i == _usuarios.size()) {
                    int[] rowsAffected = statement.executeBatch(); // Execute every 1000 items.
                    System.out.println(WEB_NAME+"[UsersDAO."
                        + "saveListUsuarios]filas afectadas= "+rowsAffected.length);
                }
            }
        }
    }
    
    /**
     * Insert into usuario centro costo
     * @param _centroscosto
     * @throws java.sql.SQLException
     */
    public void saveListCencos(ArrayList<UsuarioCentroCostoVO> _centroscosto) throws SQLException{
        try (
            PreparedStatement statement = dbConn.prepareStatement(SQL_INSERT_USUARIO_CENTROCOSTO);
        ) {
           
            int i = 0;
            System.out.println(WEB_NAME+"[UsersDAO.saveListCencos]"
                + "items a insertar: "+_centroscosto.size());
            for (UsuarioCentroCostoVO entity : _centroscosto) {
                System.out.println(WEB_NAME+"[UsersDAO.saveListCencos] "
                    + "Insert usuario. "
                    + "username= " + entity.getUsername()
                    + ", centrocosto= " + entity.getCcostoId());
            
                statement.setString(1,  entity.getUsername());
                statement.setInt(2,  entity.getCcostoId());
                statement.setString(3,  entity.getEmpresaId());
                statement.setString(4,  entity.getDeptoId());
                
                // ...
                statement.addBatch();
                i++;

                if (i % 50 == 0 || i == _centroscosto.size()) {
                    int[] rowsAffected = statement.executeBatch(); // Execute every 1000 items.
                    System.out.println(WEB_NAME+"[UsersDAO."
                        + "saveListCencos]filas afectadas= "+rowsAffected.length);
                }
            }
        }
    }
    
    /**
    * 
    * @param _username
    * @param _email
     * @param _empresaId
    * @return 
    */
    public boolean existeEmail(String _username, 
            String _email, 
            String _empresaId){
        boolean result=false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            String sql = "select * from usuario "
                + "where usr_email='" + _email + "' "
                    + "and usr_username != '" + _username + "' "
                    + "and empresa_id = '" + _empresaId + "' ";
            System.out.println(WEB_NAME+"[UsersDAO.existeEmail]SQL: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[UsersDAO.existeEmail]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            result = rs.next();
            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("Error: "+sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[UsersDAO.existeEmail]"
                    + "Error: " + ex.toString());
            }
        }
        
        return result;
    }
    
    public void openDbConnection(){
        try {
            m_usedGlobalDbConnection = true;
            dbConn = dbLocator.getConnection(m_dbpoolName,"[UsersDAO.openDbConnection]");
            System.out.println(WEB_NAME+"[UsersDAO.openDbConnection]Conexion a bd: "+dbConn);
        } catch (DatabaseException ex) {
            System.err.println("[UsersDAO.openDbConnection]"
                + "Error: " + ex.toString());
        }
    }
   
    /**
    * 
    */
    public void closeDbConnection(){
        try {
            System.out.println(WEB_NAME+"[UsersDAO.closeDbConnection]Cerrando conexion a la BD...");
            m_usedGlobalDbConnection = false;
            dbLocator.freeConnection(dbConn);
        } catch (Exception ex) {
            System.err.println("[UsersDAO.closeDbConnection]"
                + "Error: "+ex.toString());
        }
    }
}
