/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.AfpVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.PropertiesVO;
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
public class AfpDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
        
    /**
    *
    * @param _propsValues
    */
    public AfpDAO(PropertiesVO _propsValues) {

    }

    /**
    * Retorna lista con las AFPs existentes
    * 
    * @return 
    */
    public HashMap<String, AfpVO> getAfps(){
        HashMap<String, AfpVO> lista = new HashMap<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            String sql = "SELECT "
                + "afp_code,"
                + "afp_name,"
                + "afp_status "
            + "FROM afp "
                + "where afp_status = 1"
                + "order by afp_code";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[AfpDAO.getAfps]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                AfpVO afp = new AfpVO();
                afp.setCode(rs.getString("afp_code"));
                afp.setNombre(rs.getString("afp_name"));
                lista.put(afp.getCode(), afp);
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
    * Retorna lista con las AFPs existentes en el sistema
    * 
    * @param _nombre
    * @param _estado
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * @return 
    */
    public List<AfpVO> getAfps(String _nombre,
            int _estado,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<AfpVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        AfpVO data;
        
        try{
            String sql = "SELECT afp_code, afp_name, afp_status FROM afp "
                + "where 1 = 1 and afp_code <> 'NINGUNA' ";
           
            if (_nombre != null && _nombre.compareTo("") != 0){        
                sql += " and upper(afp_name) like '%"+_nombre.toUpperCase()+"%'";
            }
            if (_estado != -1){        
                sql += " and afp_status = " + _estado;
            }
           
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            System.out.println(WEB_NAME+"[AfpDAO.getAfps]Sql: " + sql);
            dbConn = dbLocator.getConnection(m_dbpoolName,"[AfpDAO.getAfps]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new AfpVO();
                data.setCode(rs.getString("afp_code"));
                data.setNombre(rs.getString("afp_name"));
                data.setEstado(rs.getInt("afp_status"));
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
    * @param _nombre
    * @param _estado
    * @return 
    */
    public int getAfpsCount(String _nombre,int _estado){
        int count=0;
        Statement statement = null;
        ResultSet rs        = null;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[AfpDAO.getAfpsCount]");
            statement = dbConn.createStatement();
            String strSql ="SELECT count(afp_code) "
                + "FROM afp where 1 = 1 and afp_code <> 'NINGUNA' ";
               
            if (_nombre != null && _nombre.compareTo("") != 0){        
                strSql += " and upper(afp_name) like '" + _nombre.toUpperCase() + "%'";
            }
            if (_estado != -1){
                strSql += " and afp_status = "+_estado;
            }
            rs = statement.executeQuery(strSql);		
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
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[AfpDAO.getAfpsCount]Error: "+ex.toString());
            }
        }
        return count;
    }
    
    /**
    * Agrega una nueva AFP
    * @param _data
    * @return 
    */
    public ResultCRUDVO insert(AfpVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        int result=0;
        String msgError = "Error al insertar "
            + "Afp. "
            + "Code: " + _data.getCode()    
            + ", nombre: " + _data.getNombre()
            + ", estado: " + _data.getEstado();
        
       String msgFinal = " Inserta AFP:"
            + "Code [" + _data.getCode() + "],"   
            + ", nombre [" + _data.getNombre() + "],"
            +  ", estado [" + _data.getEstado() + "]";            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT "
                + "INTO afp(afp_code, afp_name, afp_status) "
                + "VALUES (?, ?, ?)";
            dbConn = dbLocator.getConnection(m_dbpoolName,"[AfpDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getCode());
            insert.setString(2,  _data.getNombre());
            insert.setInt(3,  _data.getEstado());
                                               
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println(WEB_NAME+"[AfpDAO.insert Afp]"
                    + ", code:" +_data.getCode()    
                    + ", nombre:" +_data.getNombre()
                    + ", estado:" +_data.getEstado()
                    +" insertada OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[AfpDAO.insert]insert Afp Error1: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (insert != null) insert.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[AfpDAO.insert]insert Afp Error2: "+ex.toString());
            }
        }
        return objresultado;
    }
    
    /**
    * Actualiza una AFP
    * @param _data
    * @return 
    */
    public ResultCRUDVO update(AfpVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "AFP, "
            + "Code: "+_data.getCode()
            + ", nombre: "+_data.getNombre()
            + ", estado: "+_data.getEstado();
        
        try{
            String msgFinal = " Actualiza AFP:"
                + "code [" + _data.getCode() + "]" 
                + ", nombre [" + _data.getNombre() + "]"
                + ", estado [" + _data.getEstado() + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE afp "
                    + " SET "
                    + "afp_name = ?, "
                    + "afp_status = ? "
                    + " WHERE afp_code = ?";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[AfpDAO.update]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setString(1,  _data.getNombre());
            psupdate.setInt(2,  _data.getEstado());
            psupdate.setString(3,  _data.getCode());
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println(WEB_NAME+"[AfpDAO.update]AFP"
                    + ", code:" +_data.getCode()
                    + ", nombre:" +_data.getNombre()
                    + ", estado:" +_data.getEstado()
                    +" actualizado OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("update Cargo Error: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (psupdate != null) psupdate.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }

        return objresultado;
    }
}