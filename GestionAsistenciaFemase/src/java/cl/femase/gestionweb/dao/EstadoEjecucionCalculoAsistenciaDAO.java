/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.CalculoAsistenciaEstadoEjecucionVO;
import cl.femase.gestionweb.vo.EstadoVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexander
 */
public class EstadoEjecucionCalculoAsistenciaDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    //private Connection dbConn;
    //DbDirectConnection dbconnection = DbDirectConnection.getInstance();
    
    public EstadoEjecucionCalculoAsistenciaDAO(PropertiesVO _propsValues) {
//        try {
//            dbLoc = DatabaseLocator.getInstance();
//            m_dbpoolName = _propsValues.getDbPoolName();
//        } catch (DatabaseException ex) {
//            m_logger.error("DbError: "+ex.toString());
//        }
    }

    /**
     * Actualiza una estado ejecucion calculo asistencia
     * @param _data
     * @return 
     */
    public int updateStatus(CalculoAsistenciaEstadoEjecucionVO _data){
        PreparedStatement psupdate = null;
        int rowsAffected = 0;
       
        try{
            String sql = "UPDATE calculo_asistencia_estado_ejecucion "
                + " SET "
                + "status_id=?, "
                + "status_timestamp=current_timestamp, "
                + "exec_user=? "
                + "WHERE empresa_id=?, "
                    + "depto_id=?, "
                    + "cenco_id=?";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[EstadoEjecucionCalculoAsistenciaDAO.updateStatus]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setString(1,  _data.getStatusId());
            psupdate.setString(2,  _data.getUsuario());
            psupdate.setString(3,  _data.getEmpresaId());
            psupdate.setString(4,  _data.getDeptoId());
            psupdate.setInt(5,  _data.getCencoId());
                        
            rowsAffected = psupdate.executeUpdate();
           
            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("update calculo_asistencia_estado_ejecucion Error: "+sqle.toString());
        }finally{
            try {
                if (psupdate != null) psupdate.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }

        return rowsAffected;
    }

     /**
     * insert calculo_asistencia_estado_ejecucion
     * @param _data
     * @return 
     */
    public int insert(CalculoAsistenciaEstadoEjecucionVO _data){
        int filasAfectadas=0;
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO calculo_asistencia_estado_ejecucion("
                + " empresa_id, "
                + "depto_id, "
                + "cenco_id, "
                + "status_id, "
                + "status_timestamp,"
                + "exec_user) "
                + "VALUES (?, ?, ?, ?, current_timestamp, ?)";
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[EstadoEjecucionCalculoAsistenciaDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getEmpresaId());
            insert.setString(2,  _data.getDeptoId());
            insert.setInt(3,  _data.getCencoId());
            insert.setString(4,  _data.getStatusId());
            insert.setString(5,  _data.getUsuario());
                                    
            filasAfectadas = insert.executeUpdate();
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("insert calculo_asistencia_estado_ejecucion Error1: "+sqle.toString());
        }finally{
            try {
                if (insert != null) insert.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }

        return filasAfectadas;
    }
    
    /**
     * Elimina registro de calculo_asistencia_ejecucion.
     * Se invoca cuando el calculo de asistencia ha finalizado
     * 
     * @param _empresaId
     * @param _deptoId
     * @param _cencoId
     * @return 
     */
    public int delete(String _empresaId, String _deptoId, int _cencoId){
        int filasAfectadas=0;
        PreparedStatement stmt = null;
        
        try{
            String sql = "delete from calculo_asistencia_estado_ejecucion "
                + "WHERE empresa_id = ? "
                    + "and depto_id = ? "
                    + "and cenco_id= ?";
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[EstadoEjecucionCalculoAsistenciaDAO.delete]");
            stmt = dbConn.prepareStatement(sql);
            stmt.setString(1,  _empresaId);
            stmt.setString(2,  _deptoId);
            stmt.setInt(3,  _cencoId);
                                    
            filasAfectadas = stmt.executeUpdate();
            
            stmt.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("delete calculo_asistencia_estado_ejecucion Error1: "+sqle.toString());
        }finally{
            try {
                if (stmt != null) stmt.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }

        return filasAfectadas;
    }
    
    /**
     * Retorna lista con los modulos de sistema existentes
     * 
     * @param _empresaId
     * @param _deptoId
     * @param _cencoId
     * @return 
     */
    public EstadoVO getCurrentStatus(String _empresaId, 
            String _deptoId, int _cencoId){
        
        EstadoVO currentStatus = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            String sql ="SELECT "
                + "status_id id,"
                + "estado.estado_label "
                + "FROM calculo_asistencia_estado_ejecucion "
                + "inner join estado_ejecucion estado "
                    + "on (status_id = estado.estado_id) "
                + "WHERE empresa_id='" + _empresaId + "' "
                    + "and depto_id='" + _deptoId + "' "
                    + "and cenco_id= " + _cencoId;
 
            System.out.println("[Dao Estado Ejecucion]sql: " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[EstadoEjecucionCalculoAsistenciaDAO.getCurrentStatus]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                currentStatus = new EstadoVO(rs.getString("id"),
                    rs.getString("estado_label"));
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
        
        return currentStatus;
    }
   
}
