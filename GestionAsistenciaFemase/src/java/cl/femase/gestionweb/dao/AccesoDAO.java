/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

//import cl.femase.gestionweb.common.DbConnectionPool;
//import cl.femase.gestionweb.common.DbDirectConnection;
import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.AccesoVO;
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
public class AccesoDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    
    
    /**
     * Actualiza un acceso
     * @param _data
     * @return 
     */
    public MaintenanceVO update(AccesoVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "acceso, "
            + "id: "+_data.getId()
            + ", nombre: "+_data.getNombre()
            + ", label: "+_data.getLabel()
            + ", url: "+_data.getUrl();
        
        try{
            String msgFinal = " Actualiza acceso:"
                + "id [" + _data.getId() + "]" 
                + ", nombre [" + _data.getNombre() + "]"
                + ", label [" + _data.getLabel() + "]"
                + ", url [" + _data.getUrl() + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE acceso "
                    + " SET "
                    + "acceso_nombre=?, "
                    + "acceso_label=?, "
                    + "acceso_url=? "
                    + " WHERE acceso_id=?";
            
            dbConn = dbLocator.getConnection(m_dbpoolName, "[AccesosDAO.update]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setString(1,  _data.getNombre());
            psupdate.setString(2,  _data.getLabel());
            psupdate.setString(3,  _data.getUrl());
            psupdate.setInt(4,  _data.getId());
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println("[update]acceso"
                        + ", id:" +_data.getId()
                        + ", nombre:" +_data.getNombre()
                        + ", label:" +_data.getLabel()
                        + ", url:" +_data.getUrl()
                        +" actualizado OK!");
            }
        }catch(SQLException|DatabaseException sqle){
            System.err.println("update acceso Error: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }
        finally{
            try {
                if (psupdate != null) psupdate.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }

        return objresultado;
    }

     /**
     * Agrega un nuevo acceso
     * @param _data
     * @return 
     */
    public MaintenanceVO insert(AccesoVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al insertar "
            + "acceso. "
            + " nombre: "+_data.getNombre()
            + ", label: "+_data.getLabel()
            + ", Url: "+_data.getUrl();
        
       String msgFinal = " Inserta acceso:"
                    + "nombre [" + _data.getNombre() + "]"
                    + ", label [" + _data.getLabel()+ "]"
                    + ", url [" + _data.getUrl() + "]";
            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO acceso(" +
                "acceso_id, acceso_nombre, acceso_label, acceso_url) "
                    + " VALUES (nextval('acceso_acceso_id_seq'), ?, ?, ?)";

            dbConn = dbLocator.getConnection(m_dbpoolName, "[AccesosDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getNombre());
            insert.setString(2,  _data.getLabel());
            insert.setString(3,  _data.getUrl());
                        
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println("[insert acceso]"
                    + ", nombre:" +_data.getNombre()
                    + ", label:" +_data.getLabel()
                    + ", url:" +_data.getUrl()
                    +" insertado OK!");
            }
            
           
        }catch(SQLException|DatabaseException sqle){
            System.err.println("insert acceso Error1: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }
        finally{
            try {
                if (insert != null) insert.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
        return objresultado;
    }
    
    /**
     * Elimina (desactiva) un acceso
     * @param _data
     * @return 
     */
    public MaintenanceVO delete(AccesoVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al eliminar "
                + "un acceso, Id: "+_data.getId()
                + ", nombre: "+_data.getNombre()
                + ", label: "+_data.getLabel()
                + ", url: "+_data.getUrl();
        
       String msgFinal = " Elimina acceso:"
            + "Id [" + _data.getId() + "]" 
            + ", nombre [" + _data.getNombre() + "]"
            + ", label [" + _data.getLabel() + "]"
            + ", url [" + _data.getUrl() + "]";
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "delete from "
                + "acceso where acceso_id= ?";

            dbConn = dbLocator.getConnection(m_dbpoolName, "[AccesosDAO.delete]");
            insert = dbConn.prepareStatement(sql);
            insert.setInt(1,  _data.getId());
                        
            int filasAfectadas = insert.executeUpdate();
            m_logger.debug("[delete acceso]filasAfectadas: "+filasAfectadas);
            if (filasAfectadas == 1){
                m_logger.debug("[delete acceso]"
                        + ", id:" +_data.getId()
                        + ", nombre:" +_data.getNombre()
                        + ", label:" +_data.getLabel()
                        +" eliminado OK!");
            }
            
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("delete Error: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                insert.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[AccesosDAO]delete Error: "+ex.toString());
            }
        }

        return objresultado;
    }
        
    
    
    /**
     * Retorna lista con los modulos de sistema existentes
     * 
     * @param _label
     * @param _jtStartIndex
     * @param _jtPageSize
     * @param _jtSorting
     * @return 
     */
    public List<AccesoVO> getAccesos(String _label,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<AccesoVO> lista = 
                new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        AccesoVO data;
        
        try{
            String sql = "SELECT "
                    + "acceso_id,"
                    + "acceso_nombre,"
                    + "acceso_label,"
                    + "acceso_url "
                    + "FROM acceso "
                    + "WHERE "
                    + " 1=1 ";

            if (_label!=null && _label.compareTo("")!=0){        
                    sql += " and upper(acceso_label) like '%"+_label.toUpperCase()+"%'";
            }
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            System.out.println("[AccesoDAO.getAccesos]"
                    + "label: "+_label
                    + ", _jtStartIndex: "+_jtStartIndex
                    + ", _jtPageSize: "+_jtPageSize
                    + ", _jtSorting: "+_jtSorting
                    + ", Sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName, "[AccesosDAO.getAccesos]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new AccesoVO();
                data.setId(rs.getInt("acceso_id"));
                data.setNombre(rs.getString("acceso_nombre"));
                data.setLabel(rs.getString("acceso_label"));
                data.setUrl(rs.getString("acceso_url"));
                
                lista.add(data);
            }
            
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("Error: "+sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[AccesosDAO]delete Error: "+ex.toString());
            }
        }
        return lista;
    }
    
    /**
     * 
     * @param _label
     * @return 
     */
    public int getAccesosCount(String _label){
        int count = 0;
        Statement statement = null;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName, "[AccesosDAO.getAccesosCount]");
            statement = dbConn.createStatement();
            String strSql="SELECT count(*) as count "
                    + "FROM acceso where 1=1 ";
            if (_label!=null && _label.compareTo("")!=0){        
                    strSql += " and upper(acceso_label) like '"+_label.toUpperCase()+"%'";
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
                System.err.println("[AccesosDAO]delete Error: "+ex.toString());
            }
        }
        return count;
    }
   
}
