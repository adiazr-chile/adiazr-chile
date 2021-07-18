/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.TipoAusenciaVO;
import cl.femase.gestionweb.vo.PropertiesVO;
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
public class TipoAusenciaDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    //private Connection dbConn;
    //private DatabaseLocator dbLoc;
    //DbDirectConnection dbconnection = DbDirectConnection.getInstance();
    
    public TipoAusenciaDAO(PropertiesVO _propsValues) {
//        try {
//            dbLoc = DatabaseLocator.getInstance();
//            m_dbpoolName = _propsValues.getDbPoolName();
//        } catch (DatabaseException ex) {
//            m_logger.error("DbError: "+ex.toString());
//        }
    }

    /**
     * Retorna lista con los tipos de ausencias
     * 
     * @param _nombre
     * @param _jtStartIndex
     * @param _jtPageSize
     * @param _jtSorting
     * @return 
     */
    public List<TipoAusenciaVO> getTipos(String _nombre,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<TipoAusenciaVO> lista = 
                new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        TipoAusenciaVO data;
        
        try{
            String sql = "SELECT tp_ausencia_id, tp_ausencia_nombre " +
                "FROM tipo_ausencia WHERE 1=1 ";

            if (_nombre!=null && _nombre.compareTo("")!=0){        
                sql += " and upper(tp_ausencia_nombre) like '"+_nombre.toUpperCase()+"%'";
            }
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TipoAusenciaDAO.getTipos]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new TipoAusenciaVO();
                data.setId(rs.getInt("tp_ausencia_id"));
                data.setNombre(rs.getString("tp_ausencia_nombre"));
                                
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
    public int getTiposCount(String _nombre){
        int count=0;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TipoAusenciaDAO.getTiposCount]");
            ResultSet rs;
            try (Statement statement = dbConn.createStatement()) {
                String strSql="SELECT count(*) as count "
                        + "FROM tipo_ausencia where 1=1 ";
                if (_nombre!=null && _nombre.compareTo("")!=0){
                    strSql += " and upper(tp_ausencia_nombre) like '"+_nombre.toUpperCase()+"%'";
                }   rs = statement.executeQuery(strSql);
                if (rs.next()) {
                    count=rs.getInt("count");
                }
            }
            rs.close();
            dbLocator.freeConnection(dbConn);
        } catch (SQLException|DatabaseException e) {
                e.printStackTrace();
        }
        return count;
    }
   
}
