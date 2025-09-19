/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.PerfilUsuarioVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexander
 */
public class PerfilUsuarioDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    //private Connection dbConn;
    //DbDirectConnection dbconnection = DbDirectConnection.getInstance();
    
    public PerfilUsuarioDAO(PropertiesVO _propsValues) {
//        try {
//            dbLoc = DatabaseLocator.getInstance();
//            m_dbpoolName = _propsValues.getDbPoolName();
//        } catch (DatabaseException ex) {
//            m_logger.error("DbError: "+ex.toString());
//        }
    }

    /**
     * Actualiza un perfil de usuario
     * @param _data
     * @return 
     */
    public ResultCRUDVO update(PerfilUsuarioVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "perfil usuario, "
            + "id: " + _data.getId()
            + ", nombre: " + _data.getNombre()
            + ", estado_id: " + _data.getEstadoId()
            + ", admin_empresa?: " + _data.getAdminEmpresa();
        
        try{
            String msgFinal = " Actualiza perfil_usuario:"
                + "id [" + _data.getId() + "]" 
                + ", nombre [" + _data.getNombre() + "]"
                + ", estado_id [" + _data.getEstadoId() + "]"
                + ", admin_empresa [" + _data.getAdminEmpresa() + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE perfil_usuario "
                    + " SET "
                    + "perfil_nombre = ?, "
                    + "estado_id = ?, admin_empresa = ? "
                    + " WHERE perfil_id = ?";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[PerfilUsuarioDAO.update]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setString(1,  _data.getNombre());
            psupdate.setInt(2,  _data.getEstadoId());
            psupdate.setString(3,  _data.getAdminEmpresa());
            psupdate.setInt(4,  _data.getId());
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println(WEB_NAME+"[update]perfil_usuario "
                    + ", id:" + _data.getId()
                    + ", nombre:" + _data.getNombre()
                    + ", estado_id:" + _data.getEstadoId()
                    + ", admin_empresa:" + _data.getAdminEmpresa()    
                    +" actualizado OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("update perfil_usuario Error: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }

        return objresultado;
    }

     /**
     * Agrega un nuevo perfil de usuario
     * @param _data
     * @return 
     */
    public ResultCRUDVO insert(PerfilUsuarioVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        int result=0;
        String msgError = "Error al insertar "
            + "perfil usuario. "
            + " nombre: "+_data.getNombre()
            + ", estado_id: "+_data.getEstadoId()
            + ", admin_empresa?: "+_data.getAdminEmpresa();
        
       String msgFinal = " Inserta perfil usuario:"
            + "nombre [" + _data.getNombre() + "]"
            + ", estado_id [" + _data.getEstadoId()+ "]"
            + ", admin_empresa? [" + _data.getAdminEmpresa()+ "]";
            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO perfil_usuario "
                    + "(" +
                "perfil_id, perfil_nombre, estado_id, admin_empresa)"
                + " VALUES (nextval('perfil_usuario_perfil_id_seq'), ?, ?, ?)";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[PerfilUsuarioDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getNombre());
            insert.setInt(2,  _data.getEstadoId());
            insert.setString(3,  _data.getAdminEmpresa());
            
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println(WEB_NAME+"[PerfilUsuarioDAO.insert perfil_usuario]"
                    + ", nombre:" + _data.getNombre()
                    + ", estado:" + _data.getEstadoId()
                    + ", admin_empresa:" + _data.getAdminEmpresa()    
                    +" insertado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[PerfilUsuarioDAO]insert perfil "
                + "usuario Error1: " + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }

        return objresultado;
    }
    
    /**
    * Retorna lista con los perfiles de usuario existentes
    * 
    * @param _nombre
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * @return 
    */
    public List<PerfilUsuarioVO> getPerfiles(String _nombre,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<PerfilUsuarioVO> lista = 
                new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        PerfilUsuarioVO data;
        
        try{
            String sql = "SELECT "
                + "perfil.perfil_id,"
                + "perfil.perfil_nombre,"
                + "perfil.estado_id,"
                + "estado.estado_nombre,"
                + "coalesce(admin_empresa,'N') admin_empresa "
                + "FROM "
                + " perfil_usuario perfil,"
                + " estado "
                + "WHERE "
                + " perfil.estado_id = estado.estado_id ";
                    
            if (_nombre!=null && _nombre.compareTo("")!=0){        
                    sql += " and upper(perfil_nombre) like '"+_nombre.toUpperCase()+"%'";
            }
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
    
            dbConn = dbLocator.getConnection(m_dbpoolName,"[PerfilUsuarioDAO.getPerfiles]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new PerfilUsuarioVO();
                data.setId(rs.getInt("perfil_id"));
                data.setNombre(rs.getString("perfil_nombre"));
                data.setEstadoId(rs.getInt("estado_id"));
                data.setEstadoNombre(rs.getString("estado_nombre"));
                data.setAdminEmpresa(rs.getString("admin_empresa"));
                
                lista.add(data);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("Error: "+sqle.toString());
        }
        
        return lista;
    }
    
    /**
    * Retorna lista con los perfiles de usuario existentes
    * 
    * @return 
    */
    public HashMap<Integer, String> getPerfilesVigentes(){
        HashMap<Integer, String> hashPerfiles 
            = new HashMap<Integer, String>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            String sql = "SELECT "
                + "perfil.perfil_id,"
                + "perfil.perfil_nombre "
                + "FROM "
                + " perfil_usuario perfil "
                + "WHERE "
                + " perfil.estado_id = 1 "
                + "order by perfil.perfil_nombre";
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[PerfilUsuarioDAO.getPerfilesVigentes]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                hashPerfiles.put(rs.getInt("perfil_id"),
                    rs.getString("perfil_nombre"));
            }
            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("Error: "+sqle.toString());
        }
        
        return hashPerfiles;
    }
    
    
    /**
    * Retorna lista con los perfiles de usuario existentes, 
    * dependiendo del tipo de usuario
    * 
    * @param _usuario
    * @return 
    */
    public List<PerfilUsuarioVO> getPerfilesByUsuario(UsuarioVO _usuario){
        
        List<PerfilUsuarioVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        PerfilUsuarioVO data;
        
        try{
            String sql = "SELECT "
                + "perfil.perfil_id,"
                + "perfil.perfil_nombre,"
                + "perfil.estado_id,"
                + "estado.estado_nombre,"
                + "coalesce(admin_empresa,'N') admin_empresa "
                + "FROM "
                + " perfil_usuario perfil,"
                + " estado "
                + "WHERE "
                + " perfil.estado_id = estado.estado_id ";
            
            if (_usuario.getIdPerfil() != Constantes.ID_PERFIL_SUPER_ADMIN){
                if (_usuario.getAdminEmpresa().compareTo("S") == 0){
                    sql += " and (perfil_id != " + Constantes.ID_PERFIL_SUPER_ADMIN + ") "
                        + "and admin_empresa = 'N'";
                }else sql += " and (perfil_id = -1)";
            }
            
            sql += " order by perfil_nombre"; 
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[PerfilUsuarioDAO.getPerfiles]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new PerfilUsuarioVO();
                data.setId(rs.getInt("perfil_id"));
                data.setNombre(rs.getString("perfil_nombre"));
                data.setEstadoId(rs.getInt("estado_id"));
                data.setEstadoNombre(rs.getString("estado_nombre"));
                data.setAdminEmpresa(rs.getString("admin_empresa"));
                
                lista.add(data);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("Error: "+sqle.toString());
        }
        
        return lista;
    }
    
    /**
    * 
    * @param _nombre
    * @return 
    */
    public int getPerfilesCount(String _nombre){
        int count=0;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[PerfilUsuarioDAO.getPerfilesCount]");
            Statement statement = dbConn.createStatement();
            String strSql="SELECT count(*) as count "
                    + "FROM perfil_usuario where 1=1 ";
            if (_nombre!=null && _nombre.compareTo("")!=0){        
                strSql += " and upper(perfil_nombre) like '"+_nombre.toUpperCase()+"%'";
            }
            
            ResultSet rs = statement.executeQuery(strSql);		
            if (rs.next()) {
                count=rs.getInt("count");
            }
            
            statement.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        } catch (SQLException|DatabaseException e) {
            e.printStackTrace();
        }
        
        return count;
    }
   
}
