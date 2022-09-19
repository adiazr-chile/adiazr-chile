/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.LogErrorVO;
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
public class LogErrorDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    
    /**
    * Retorna lista con registros de tabla log_error
    * 
    * @param _startDate
    * @param _endDate
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * @return 
    */
    public List<LogErrorVO> getRegistros(String _startDate, 
            String _endDate,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<LogErrorVO> lista = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        LogErrorVO data;
        
        try{
            String sql = "SELECT correlativo, "
                + "to_char(fecha_hora, 'yyyy-MM-dd HH:mm:ss') fecha_hora, "
                + "username, modulo, "
                + "evento, detalle, ip_usuario, label_error,"
                + "u.empresa_id "
                + "FROM log_error log " +
                "inner join usuario u on log.username = u.usr_username "
                + "where 1 = 1 ";

            if ( (_startDate != null && _startDate.compareTo("") != 0) ){        
                if ( (_endDate == null || _endDate.compareTo("") == 0) ){ 
                    _endDate = _startDate;
                }
                sql += " and fecha_hora::date "
                    + "between '" + _startDate + "' and '" + _endDate + "' ";
            }
            
////            if (_username != null && _username.compareTo("") != 0){        
////                sql += " and upper(username) like '" + _username.toUpperCase() + "%'";
////            }
////            
////            if (_modulo != null && _modulo.compareTo("") != 0){        
////                sql += " and upper(modulo) like '" + _modulo.toUpperCase() + "%'";
////            }
////            
////            if (_evento != null && _evento.compareTo("") != 0){        
////                sql += " and upper(evento) like '" + _evento.toUpperCase() + "%'";
////            }
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[LogErrorDAO.getRegistros]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new LogErrorVO();
                data.setCorrelativo(rs.getInt("correlativo"));
                data.setFechaHora(rs.getString("fecha_hora"));
                data.setUserName(rs.getString("username"));
                data.setModulo(rs.getString("modulo"));
                data.setEvento(rs.getString("evento"));
                data.setDetalle(rs.getString("detalle"));
                data.setIp(rs.getString("ip_usuario"));
                data.setLabel(rs.getString("label_error"));
                data.setUserEmpresaId(rs.getString("empresa_id"));
                
                lista.add(data);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("Error: "+sqle.toString());
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
    * @param _startDate
    * @param _endDate
    * @return 
    */
    public int getRegistrosCount(String _startDate, 
            String _endDate){
        int count=0;
        ResultSet rs;
        //Statement statement = null;
        //Statement statement = null;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[LogErrorDAO.getRegistrosCount]");
            
            try (Statement statement = dbConn.createStatement()) {
                String sql ="SELECT count(correlativo) "
                    + "FROM log_error "
                    + "WHERE 1 = 1 ";
                if ( (_startDate != null && _startDate.compareTo("") != 0) ){        
                    if ( (_endDate == null || _endDate.compareTo("") == 0) ){ 
                        _endDate = _startDate;
                    }
                    sql += " and fecha_hora::date "
                        + "between '" + _startDate + "' and '" + _endDate + "' ";
                }

                /*
                if (_username != null && _username.compareTo("") != 0){        
                    sql += " and upper(username) like '" + _username.toUpperCase() + "%'";
                }

                if (_modulo != null && _modulo.compareTo("") != 0){        
                    sql += " and upper(modulo) like '" + _modulo.toUpperCase() + "%'";
                }

                if (_evento != null && _evento.compareTo("") != 0){        
                    sql += " and upper(evento) like '" + _evento.toUpperCase() + "%'";
                }
                */
                rs = statement.executeQuery(sql);
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
    * Agrega un registro en Log error
    * @param _data
    * 
    */
    public void insert(LogErrorVO _data){
        int result=0;
        PreparedStatement insert    = null;
        try{
            String sql = "INSERT INTO log_error("
                + "fecha_hora, username, "
                + "modulo, evento, "
                + "detalle, ip_usuario, label_error) "
                + "VALUES (current_timestamp, "
                    + "?, "
                    + "?, "
                    + "?, "
                    + "?, "
                    + "?, "
                    + "?)";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[LogErrorDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getUserName());
            insert.setString(2,  _data.getModulo());
            insert.setString(3,  _data.getEvento());
            insert.setString(4,  _data.getDetalle());
            insert.setString(5,  _data.getIp());
            insert.setString(6,  _data.getLabel());
                                               
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println(WEB_NAME+"[LogErrorDao.insert log_error]"
                    + ", modulo: " +_data.getModulo()
                    + ", evento: " +_data.getEvento()
                    + ", usuario: " +_data.getUserName()
                    + ", detalle: " +_data.getDetalle()    
                    +" insert OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[LogErrorDAO.insert]insert log_error Error_1: " + sqle.toString());
        }finally{
            try {
                if (insert != null) insert.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[LogErrorDAO.insert]insert log_error Error_2: " + ex.toString());
            }
        }
        
    }
    
}
