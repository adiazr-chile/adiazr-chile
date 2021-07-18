/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.ProveedorCorreoVO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
*
* @author Alexander
*/
public class ProveedorCorreoDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    
    /**
    * Actualiza un proveedor de correo
    * @param _data
    * @return 
    */
    public MaintenanceVO update(ProveedorCorreoVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "proveedor de correo, "
            + "code: " + _data.getCode()
            + ", name: "+_data.getName()
            + ", domain: "+_data.getDomain();
        
        try{
            String msgFinal = " Actualiza proveedor de correo:"
                + "code [" + _data.getCode() + "]" 
                + ", name [" + _data.getName() + "]"
                + ", domain [" + _data.getDomain() + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE proveedor_correo "
                + "SET "
                    + "provider_name = ?, "
                    + "provider_domain = ?, "
                    + "update_datetime = current_timestamp "
                    + " WHERE provider_code = ?";

            dbConn = dbLocator.getConnection(m_dbpoolName, 
                "[ProveedorCorreoDAO.update]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setString(1,  _data.getName());
            psupdate.setString(2,  _data.getDomain());
            psupdate.setString(3,  _data.getCode());
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println("[ProveedorCorreoDAO.update]"
                    + "proveedor correo"
                    + ". Code:" + _data.getCode()
                    + ", name:" + _data.getName()
                    + ", domain:" + _data.getDomain()
                    +" actualizado OK!");
            }
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[ProveedorCorreoDAO.update]"
                + "Error: " + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }
        finally{
            try {
                if (psupdate != null) psupdate.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[ProveedorCorreoDAO.update]"
                    + "Error: " + ex.toString());
            }
        }

        return objresultado;
    }

    /**
    * Agrega un nuevo proveedor de correo
    * @param _data
    * @return 
    */
    public MaintenanceVO insert(ProveedorCorreoVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al insertar "
            + "proveedor de correo. "
            + " code: " + _data.getCode()
            + ", name: " + _data.getName()
            + ", domain: " + _data.getDomain();
        
       String msgFinal = " Inserta proveedor de correo:"
            + "Code [" + _data.getCode() + "]"
            + ", name [" + _data.getName()+ "]"
            + ", domain [" + _data.getDomain() + "]";
            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO proveedor_correo "
                + "(provider_code, provider_name, "
                    + "provider_domain, create_datetime) "
                    + "VALUES (?, ?, ?, current_timestamp)";
            dbConn = dbLocator.getConnection(m_dbpoolName, 
                "[ProveedorCorreoDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getCode());
            insert.setString(2,  _data.getName());
            insert.setString(3,  _data.getDomain());
                        
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println("[ProveedorCorreoDAO.insert]"
                    + "Code:" +_data.getCode()
                    + ", name:" +_data.getName()
                    + ", domain:" +_data.getDomain()
                    +" insertado OK!");
            }
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[ProveedorCorreoDAO.insert]"
                + "Error_1: " + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }
        finally{
            try {
                if (insert != null) insert.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[ProveedorCorreoDAO.insert]"
                    + "Error_2: " + ex.toString());
            }
        }
        return objresultado;
    }
    
    /**
    * Elimina un proveedor de correo
     * @param _code
    * @return 
    */
    public MaintenanceVO delete(String _code){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al eliminar "
            + "un proveedor de correo, "
            + "Code: " + _code;
        
       String msgFinal = " Elimina proveedor de correo:"
            + "Code [" + _code + "]";
       
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "DELETE FROM proveedor_correo "
                + "WHERE provider_code = ?";

            dbConn = dbLocator.getConnection(m_dbpoolName, "[ProveedorCorreoDAO.delete]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _code);
                        
            int filasAfectadas = insert.executeUpdate();
            System.out.println("[ProveedorCorreoDAO.delete]"
                + "filasAfectadas: " + filasAfectadas);
            if (filasAfectadas == 1){
                System.out.println("[delete proveedor de correo]"
                    + "Code:" + _code
                    +" eliminado OK!");
            }
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[ProveedorCorreoDAO.delete]Error: " + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError + " :" + sqle.toString());
        }finally{
            try {
                insert.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[ProveedorCorreoDAO.delete]"
                    + "Error: " + ex.toString());
            }
        }

        return objresultado;
    }
    
    /**
    * Retorna lista con los proveedores de correo existentes
    * 
    * @param _name
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * @return 
    */
    public List<ProveedorCorreoVO> getProveedores(String _name,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<ProveedorCorreoVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        ProveedorCorreoVO data;
        
        try{
            String sql = "SELECT "
                + "provider_code, provider_name, "
                + "provider_domain, "
                + "to_char(create_datetime, 'yyyy-MM-dd HH:mm:ss') create_datetime,"
                + "to_char(update_datetime, 'yyyy-MM-dd HH:mm:ss') update_datetime "
                + "FROM proveedor_correo where 1 = 1 ";

            if (_name != null && _name.compareTo("") != 0){        
                sql += " and upper(provider_name) like '%" + _name.toUpperCase()+"%'";
            }
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit " + _jtPageSize + " offset " + _jtStartIndex;
            }
            
            System.out.println("[ProveedorCorreoDAO.getProveedores]"
                + "Name: " + _name
                + ", _jtStartIndex: " + _jtStartIndex
                + ", _jtPageSize: " + _jtPageSize
                + ", _jtSorting: " + _jtSorting
                + ", Sql: " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName, 
                "[ProveedorCorreoDAO.getProveedores]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new ProveedorCorreoVO();
                data.setCode(rs.getString("provider_code"));
                data.setName(rs.getString("provider_name"));
                data.setDomain(rs.getString("provider_domain"));
                data.setCreateDate(rs.getString("create_datetime"));
                data.setUpdateDate(rs.getString("update_datetime"));
                lista.add(data);
            }
            
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[ProveedorCorreoDAO."
                + "getProveedores]" + sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[ProveedorCorreoDAO."
                    + "getProveedores]Error: " + ex.toString());
            }
        }
        return lista;
    }
    
    /**
    * 
    * @param _code
    * @return 
    */
    public ProveedorCorreoVO getProveedorByCode(String _code){
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        ProveedorCorreoVO data=null;
        
        try{
            String sql = "SELECT "
                + "provider_code, provider_name, "
                + "provider_domain, create_datetime,"
                + "update_datetime "
                + "FROM proveedor_correo "
                + "where provider_code = '" + _code + "' ";

            dbConn = dbLocator.getConnection(m_dbpoolName, 
                "[ProveedorCorreoDAO.getProveedorByCode]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                data = new ProveedorCorreoVO();
                data.setCode(rs.getString("provider_code"));
                data.setName(rs.getString("provider_name"));
                data.setDomain(rs.getString("provider_domain"));
                data.setCreateDate(rs.getString("create_datetime"));
                data.setUpdateDate(rs.getString("update_datetime"));
            }
            
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[ProveedorCorreoDAO."
                + "getProveedorByCode]" + sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[ProveedorCorreoDAO."
                    + "getProveedorByCode]Error: " + ex.toString());
            }
        }
        return data;
    }
    
    /**
    * 
    * @param _name
    * @return 
    */
    public int getProveedoresCount(String _name){
        int count = 0;
        Statement statement = null;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName, 
                "[ProveedorCorreoDAO.getProveedoresCount]");
            statement = dbConn.createStatement();
            String strSql="SELECT count(*) as count "
                    + "FROM proveedor_correo where 1=1 ";
            if (_name != null && _name.compareTo("") != 0){        
                strSql += " and upper(provider_name) like '%" + _name.toUpperCase()+"%'";
            }
            ResultSet rs = statement.executeQuery(strSql);		
            if (rs.next()) {
                count=rs.getInt("count");
            }
            
            //statement.close();
            rs.close();
            //dbLocator.freeConnection(dbConn);
        } catch (SQLException|DatabaseException e) {
                e.printStackTrace();
        }
        finally{
            try {
                if (statement != null) statement.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[ProveedorCorreoDAO."
                    + "getProveedoresCount]Error: " + ex.toString());
            }
        }
        return count;
    }
}
