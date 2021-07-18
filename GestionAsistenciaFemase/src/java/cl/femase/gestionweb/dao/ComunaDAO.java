/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.ComunaVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
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
public class ComunaDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    //private Connection dbConn;
    //DbDirectConnection dbconnection = DbDirectConnection.getInstance();
    
    /**
     * Retorna lista con los modulos de sistema existentes
     * 
     * @param _nombreComuna
     * @param _regionId
     * @param _jtStartIndex
     * @param _jtPageSize
     * @param _jtSorting
     * @return 
     */
    public List<ComunaVO> getComunas(String _nombreComuna,
            int _regionId,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<ComunaVO> lista = 
                new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        ComunaVO data;
        
        try{
            String sql ="SELECT "
                + "comuna.comuna_id,"
                + "comuna.comuna_nombre,"
                + "comuna.region_id,"
                + "region.region_nombre,"
                + "region.short_name regionShortName "
                + "FROM "
                + " comuna,region "
                + "WHERE comuna.region_id = region.region_id ";

            if (_nombreComuna!=null && _nombreComuna.compareTo("")!=0){        
                sql += " and upper(comuna_nombre) like '"+_nombreComuna.toUpperCase()+"%'";
            }
            if (_regionId != -1){        
                sql += " and comuna.region_id="+_regionId;
            }
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[ComunaDAO.getComunas]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new ComunaVO();
                data.setId(rs.getInt("comuna_id"));
                data.setNombre(rs.getString("comuna_nombre"));
                data.setRegionId(rs.getInt("region_id"));
                data.setRegionNombre(rs.getString("region_nombre"));
                data.setRegionShortName(rs.getString("regionShortName"));
                
                lista.add(data);
            }

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
                System.err.println("Error: "+ex.toString());
            }
        }
        
        return lista;
    }
    
    /**
     * 
     * @param _nombreComuna
     * @param _regionId
     * @return 
     */
    public int getComunasCount(String _nombreComuna,
            int _regionId){
        int count=0;
        ResultSet rs;
        //Statement statement = null;
        //Statement statement = null;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[ComunaDAO.getComunasCount]");
            
            try (Statement statement = dbConn.createStatement()) {
                String strSql ="SELECT count(comuna.comuna_id) "
                        + "FROM "
                        + " comuna,region "
                        + "WHERE comuna.region_id = region.region_id ";
                if (_nombreComuna!=null && _nombreComuna.compareTo("")!=0){
                    strSql += " and upper(comuna_nombre) like '"+_nombreComuna.toUpperCase()+"%'";
                }   if (_regionId != -1){        
                    strSql += " and region_id="+_regionId;
                }   rs = statement.executeQuery(strSql);
                if (rs.next()) {
                    count=rs.getInt("count");
                }
            }
            rs.close();
            dbLocator.freeConnection(dbConn);
        } catch (SQLException|DatabaseException e) {
            e.printStackTrace();
        }finally{
            try {
                dbLocator.freeConnection(dbConn);
            } catch (Exception ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
        return count;
    }
   
    /**
    * Agrega una nueva comuna
    * @param _data
    * @return 
    */
    public MaintenanceVO insert(ComunaVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al insertar "
            + "Comuna. "
            + " nombre: " + _data.getNombre()
            + " regionId: " + _data.getRegionId();
        
       String msgFinal = " Inserta Comuna:"
            + "nombre [" + _data.getNombre() + "],"
            +  ", regionId [" + _data.getRegionId() + "]";            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO comuna(" +
                "comuna_id, comuna_nombre, region_id) "
                + " VALUES (nextval('comuna_id_sequence'), ?, ?)";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[ComunaDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getNombre());
            insert.setInt(2,  _data.getRegionId());
                                               
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println("[insert comuna]"
                    + ", nombre: " +_data.getNombre()
                    + ", region_id: " +_data.getRegionId()
                    +" insert OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("insert comuna Error_1: " + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError + " :" + sqle.toString());
        }finally{
            try {
                if (insert != null) insert.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("insert comuna Error_2: " + ex.toString());
            }
        }
        return objresultado;
    }
    
    /**
    * Actualiza una comuna
    * @param _data
    * @return 
    */
    public MaintenanceVO update(ComunaVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "Comuna, "
            + "id: " + _data.getId()
            + ", nombre: " + _data.getNombre()
            + ", region_id: " + _data.getRegionId();
        
        try{
            String msgFinal = " Actualiza Comuna:"
                + "id [" + _data.getId() + "]" 
                + ", nombre [" + _data.getNombre() + "]"
                + ", region_id [" + _data.getRegionId() + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE comuna "
                + " SET "
                + "comuna_nombre = ?, "
                + "region_id = ? "
                + " WHERE comuna_id = ?";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[ComunaDAO.update]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setString(1,  _data.getNombre());
            psupdate.setInt(2,  _data.getRegionId());
            psupdate.setInt(3,  _data.getId());
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println("[update]Comuna"
                    + ", id:" +_data.getRegionId()
                    + ", nombre:" +_data.getNombre()
                    + ", region_id:" +_data.getRegionId()
                    +" update OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("update Comuna Error_1: " + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError + " :" + sqle.toString());
        }finally{
            try {
                if (psupdate != null) psupdate.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("update comuna Error_2: " + ex.toString());
            }
        }

        return objresultado;
    }
}
