/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.EstadoSolicitudVO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexander
 */
public class EstadoSolicitudDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    
    /**
    * Retorna lista con los estados de solicitud
    * 
    * @return 
    */
    public LinkedHashMap<String, EstadoSolicitudVO> getEstados(){
        LinkedHashMap<String, EstadoSolicitudVO> lista;
        lista = new LinkedHashMap<>();

        PreparedStatement ps = null;
        ResultSet rs = null;
        EstadoSolicitudVO data;
        
        try{
            String sql = "SELECT "
                + "status_id id, "
                + "status_label label, "
                + "status_abbreviation abr"
                + "FROM estado_solicitud order by status_label";
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[EstadoSolicitudDAO.getEstados]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new EstadoSolicitudVO(rs.getString("id"),rs.getString("label"),rs.getString("abr"));
                
                lista.put(data.getId(),data);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("[EstadoSolicitudDAO.getEstados]"
                + "Error: " + sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[EstadoSolicitudDAO.getEstados]"
                    + "Error: " + ex.toString());
            }
        }
        
        return lista;
    }
    
}
