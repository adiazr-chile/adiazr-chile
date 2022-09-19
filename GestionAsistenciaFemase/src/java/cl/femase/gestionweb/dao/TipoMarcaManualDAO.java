/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.TipoMarcaManualVO;
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
public class TipoMarcaManualDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    
    public TipoMarcaManualDAO(PropertiesVO _propsValues) {
    }
   
    
    /**
     * Retorna lista con los tipos de marcas manuales existentes en el sistema
     * 
     * @param _nombre
     * @param _vigente
     * @param _jtStartIndex
     * @param _jtPageSize
     * @param _jtSorting
     * @return 
     */
    public List<TipoMarcaManualVO> getTipos(String _nombre,
            String _vigente,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<TipoMarcaManualVO> lista = 
                new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        TipoMarcaManualVO data;
        
        try{
            String sql = "SELECT code, display_name, "
                + "display_order, "
                + "vigent, "
                + "to_char(create_datetime,'YYYY-MM-DD HH24:MI:SS') create_datetime, "
                + "to_char(update_datetime,'YYYY-MM-DD HH24:MI:SS') update_datetime "
                + " FROM tipo_marca_manual "
                + " where 1 = 1 ";
            
            if (_nombre != null && _nombre.compareTo("") != 0){        
                sql += " and upper(display_name) like '" + _nombre.toUpperCase() + "%'";
            }
            if (_vigente != null && _vigente.compareTo("-1") != 0){        
                sql +=  " and vigent='" + _vigente + "'";
            }
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TipoMarcaManualDAO.getTipos]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new TipoMarcaManualVO();
                
                data.setCode(rs.getInt("code"));
                data.setNombre(rs.getString("display_name"));
                data.setVigente(rs.getString("vigent"));
                data.setCreateDateTime(rs.getString("create_datetime"));
                data.setUpdateDateTime(rs.getString("update_datetime"));
                data.setOrden(rs.getInt("display_order"));
                                
                lista.add(data);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[TipoMarcaManualDAO.getTipos]Error: " + sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[TipoMarcaManualDAO.getTipos]Error2: " + ex.toString());
            }
        } 
        return lista;
    }
    
    /**
     * Retorna lista con los tipos de marcas manuales existentes en el sistema
     * 
     * @param _nombre
     * @param _vigente
     * 
     * @return 
     */
    public List<TipoMarcaManualVO> getTipos(String _nombre,
            String _vigente){
        
        List<TipoMarcaManualVO> lista = 
                new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        TipoMarcaManualVO data;
        
        try{
            String sql = "SELECT code, display_name, "
                + "display_order, "
                + "vigent, "
                + "to_char(create_datetime,'YYYY-MM-DD HH24:MI:SS') create_datetime, "
                + "to_char(update_datetime,'YYYY-MM-DD HH24:MI:SS') update_datetime "
                + "FROM tipo_marca_manual "
                + "where 1 = 1 ";
            
            if (_nombre != null && _nombre.compareTo("") != 0){        
                sql += " and upper(display_name) like '" + _nombre.toUpperCase() + "%'";
            }
            
            if (_vigente != null && _vigente.compareTo("-1") != 0){        
                sql +=  " and vigent='" + _vigente + "'";
            }
           
            sql += " order by display_order"; 
            System.out.println(WEB_NAME+"[TipoMarcaManualDAO.getTipos]Sql: " + sql);
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TipoMarcaManualDAO.getTipos]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new TipoMarcaManualVO();
                
                data.setCode(rs.getInt("code"));
                data.setNombre(rs.getString("display_name"));
                data.setVigente(rs.getString("vigent"));
                data.setCreateDateTime(rs.getString("create_datetime"));
                data.setUpdateDateTime(rs.getString("update_datetime"));
                
                lista.add(data);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[TipoMarcaManualDAO.getTipos]Error: " + sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[TipoMarcaManualDAO.getTipos]Error2: " + ex.toString());
            }
        } 
        return lista;
    }
    
    /**
     * Retorna lista con los tipos de marcas manuales existentes en el sistema
     * 
     * 
     * @return 
     */
    public HashMap<Integer, TipoMarcaManualVO> getHashTipos(){
        
        HashMap<Integer, TipoMarcaManualVO> hash = new HashMap<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        TipoMarcaManualVO data;
        
        try{
            String sql = "SELECT code, display_name, "
                + "display_order, "
                + "vigent, "
                + "to_char(create_datetime,'YYYY-MM-DD HH24:MI:SS') create_datetime, "
                + "to_char(update_datetime,'YYYY-MM-DD HH24:MI:SS') update_datetime "
                + "FROM tipo_marca_manual ";
            sql += " order by display_order"; 
            System.out.println(WEB_NAME+"[TipoMarcaManualDAO.getHashTipos]Sql: " + sql);
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TipoMarcaManualDAO.getHashTipos]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new TipoMarcaManualVO();
                
                data.setCode(rs.getInt("code"));
                data.setNombre(rs.getString("display_name"));
                data.setVigente(rs.getString("vigent"));
                data.setCreateDateTime(rs.getString("create_datetime"));
                data.setUpdateDateTime(rs.getString("update_datetime"));
                
                hash.put(data.getCode(), data);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[TipoMarcaManualDAO.getHashTipos]Error: " + sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[TipoMarcaManualDAO.getHashTipos]Error2: " + ex.toString());
            }
        } 
        return hash;
    }
    
    /**
     * 
     * @param _nombre
     * @param _vigente
     * @return 
     */
    public int getTiposCount(String _nombre, String _vigente){
        int count=0;
        Statement statement = null;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TipoMarcaManualDAO.getTiposCount]");
            statement = dbConn.createStatement();
            String sql ="SELECT count(code) "
                + "FROM tipo_marca_manual where 1=1 ";
               
            if (_nombre != null && _nombre.compareTo("") != 0){        
                sql += " and upper(display_name) like '" + _nombre.toUpperCase() + "%'";
            }
            
            if (_vigente != null && _vigente.compareTo("-1") != 0){        
                sql +=  " and vigent='" + _vigente + "'";
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
        }finally{
            try {
                if (statement != null) statement.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[TipoMarcaManualDAO.getTiposCount]Error: " + ex.toString());
            }
        } 
        
        return count;
    }
   
    /**
     * Actualiza tipo marca manual
     * 
     * @param _data
     * @return 
     */
    public MaintenanceVO update(TipoMarcaManualVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "tipo marca manual, "
            + "code: "+_data.getCode()
            + ", nombre: "+_data.getNombre()
            + ", vigente?: "+_data.getVigente();
        
        try{
            String msgFinal = " Actualiza tipo_marca_manual:"
                + "code [" + _data.getCode() + "]" 
                + ", nombre [" + _data.getNombre() + "]"
                + ", vigente? [" + _data.getVigente()+ "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE tipo_marca_manual "
                + "SET display_name = ?, "
                + "display_order = ?, "
                + "vigent = ?, "
                + "update_datetime = current_timestamp "
                + "WHERE code = ?";
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TipoMarcaManualDAO.update]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setString(1,  _data.getNombre());
            psupdate.setInt(2,  _data.getOrden());
            psupdate.setString(3,  _data.getVigente());
            psupdate.setInt(4, _data.getCode());
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println(WEB_NAME+"[update]tipo_marca_manual"
                    + ", code:" + _data.getCode()
                    + ", nombre:" + _data.getNombre()
                    + ", vigente?" + _data.getVigente()    
                    +" actualizado OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[TipoMarcaManualDAO.update]Error: " + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (psupdate != null) psupdate.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[TipoMarcaManualDAO.update]Error2: " + ex.toString());
            }
        } 

        return objresultado;
    }

    /**
     * Agrega un nuevo tipo marca manual
     * 
     * @param _data
     * @return 
     */
    public MaintenanceVO insert(TipoMarcaManualVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al insertar "
            + "tipo marca manual. "
            + ", nombre: " +_data.getNombre()
            + ", vigente? " + _data.getVigente()
            + ", orderDisplay: " + _data.getOrden();
        
       String msgFinal = " Inserta tipo_marca_manual:"
            + " nombre [" + _data.getNombre() + "]"
            + " vigente? [" + _data.getVigente() + "]"
            + " ordenDisplay? [" + _data.getOrden() + "]";
            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO tipo_marca_manual("
                + "code, display_name, display_order, "
                + "vigent, create_datetime, update_datetime) "
                + " VALUES (nextval('tipo_marca_manual_id_seq'), "
                    + "?, ?, ?, "
                    + "current_timestamp, current_timestamp)";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[TipoMarcaManualDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getNombre());
            insert.setInt(2,  _data.getOrden());
            insert.setString(3,  _data.getVigente());
            
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println(WEB_NAME+"[TipoMarcaManualDAO.insert]"
                    + ", nombre:" +_data.getNombre()
                    + ", vigente?:" +_data.getVigente()
                    + ", ordenDisplay:" +_data.getOrden()
                    +" insertado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[TipoMarcaManualDAO.insert]Error1: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (insert != null) insert.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[TipoMarcaManualDAO.insert]Error2: "+ex.toString());
            }
        }

        return objresultado;
    }
}
