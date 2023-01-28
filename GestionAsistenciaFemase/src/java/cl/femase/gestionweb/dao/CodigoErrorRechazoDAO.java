/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.CodigoErrorRechazoVO;
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
public class CodigoErrorRechazoDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    
    /**
     * Actualiza un codigo error rechazo
     * @param _data
     * @return 
     */
    public ResultCRUDVO update(CodigoErrorRechazoVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "codigo_error_rechazo. "
            + "codigo: " + _data.getCodigo()
            + ", descripcion: " + _data.getDescripcion();
        
        try{
            String msgFinal = " Actualiza codigo_error_rechazo:"
                + "codigo [" + _data.getCodigo() + "]" 
                + ", descripcion [" + _data.getDescripcion() + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE codigo_error_rechazo "
                + " SET descripcion_codigo_rechazo = ? "
                + " WHERE cod_error_rechazo = ?";
            
            dbConn = dbLocator.getConnection(m_dbpoolName, "[CodigoErrorRechazoDAO.update]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setString(1,  _data.getDescripcion());
            psupdate.setString(2,  _data.getCodigo());
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println(WEB_NAME+"[CodigoErrorRechazoDAO.update]"
                    + "cod_error_rechazo"
                    + ", codigo:" +_data.getCodigo()
                    + ", descripcion:" +_data.getDescripcion()
                    +" actualizado OK!");
            }
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[CodigoErrorRechazoDAO.update]"
                + "cod_error_rechazo Error: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }
        finally{
            try {
                if (psupdate != null) psupdate.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[CodigoErrorRechazoDAO.update]"
                    + "Error: " + ex.toString());
            }
        }

        return objresultado;
    }

     /**
     * Agrega un nuevo cod error rechazo
     * 
     * @param _data
     * @return 
     */
    public ResultCRUDVO insert(CodigoErrorRechazoVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        int result=0;
        String msgError = "Error al insertar "
            + "cod_error_rechazo. "
            + " codigo: "+_data.getCodigo()
            + ", desccripcion: "+_data.getDescripcion();
        
        String msgFinal = " Inserta codigo_error_rechazo:"
            + "codigo [" + _data.getCodigo() + "]"
            + ", descripcion [" + _data.getDescripcion()+ "]";
            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO codigo_error_rechazo(" +
                "cod_error_rechazo, descripcion_codigo_rechazo) "
                + " VALUES (?, ?)";

            dbConn = dbLocator.getConnection(m_dbpoolName, "[CodigoErrorRechazoDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getCodigo());
            insert.setString(2,  _data.getDescripcion());
                        
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println(WEB_NAME+"[CodigoErrorRechazoDAO.insert]"
                    + "codigo:" +_data.getCodigo()
                    + ", descripcion:" +_data.getDescripcion()
                    +" insertado OK!");
            }
            
           
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[CodigoErrorRechazoDAO.insert]"
                + "insert cod_error_rechazo, Error1: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }
        finally{
            try {
                if (insert != null) insert.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[CodigoErrorRechazoDAO.insert]"
                    + "Error: "+ex.toString());
            }
        }
        return objresultado;
    }
    
    /**
    * Elimina un cod_error_rechazo
    * 
    * @param _data
    * @return 
    */
    public ResultCRUDVO delete(CodigoErrorRechazoVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        int result=0;
        String msgError = "Error al eliminar "
            + "un codigo_error_rechazo, codigo: "+_data.getCodigo();
        
       String msgFinal = " Elimina codigo_error_rechazo:"
            + "Codigo [" + _data.getCodigo() + "]" ;
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "delete from "
                + "codigo_error_rechazo "
                + "where cod_error_rechazo = ?";

            dbConn = dbLocator.getConnection(m_dbpoolName, "[CodigoErrorRechazoDAO.delete]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getCodigo());
                        
            int filasAfectadas = insert.executeUpdate();
            m_logger.debug("[delete cod_error_rechazo]filasAfectadas: "+filasAfectadas);
            if (filasAfectadas == 1){
                m_logger.debug("[delete cod_error_rechazo]"
                    + ", codigo:" +_data.getCodigo()
                    +" eliminado OK!");
            }
            
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("delete cod_error_rechazo Error: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                insert.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[CodigoErrorRechazoDAO.delete]delete Error: "+ex.toString());
            }
        }

        return objresultado;
    }
        
    /**
    * Retorna lista con los modulos de sistema existentes
    * 
     * @param _descripcion
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * 
    * @return 
    */
    public List<CodigoErrorRechazoVO> getCodigos(String _descripcion,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<CodigoErrorRechazoVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        CodigoErrorRechazoVO data;
        
        try{
            String sql = "SELECT "
                + "cod_error_rechazo codigo,"
                + "descripcion_codigo_rechazo descripcion "
                + "FROM codigo_error_rechazo "
                + "WHERE "
                + " 1=1 ";

            if (_descripcion != null && _descripcion.compareTo("") != 0){        
                sql += " and upper(descripcion_codigo_rechazo) like '%"+_descripcion.toUpperCase()+"%'";
            }
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            System.out.println(WEB_NAME+"[CodigoErrorRechazoDAO.getCodigos]"
                + "descripcion: " + _descripcion
                + ", _jtStartIndex: "+_jtStartIndex
                + ", _jtPageSize: "+_jtPageSize
                + ", _jtSorting: "+_jtSorting
                + ", Sql: "+sql);
            
            dbConn = 
                dbLocator.getConnection(m_dbpoolName, "[CodigoErrorRechazoDAO.getCodigos]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new CodigoErrorRechazoVO();
                data.setCodigo(rs.getString("codigo"));
                data.setDescripcion(rs.getString("descripcion"));
                
                lista.add(data);
            }
            
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("getCodigos Error: "+sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[CodigoErrorRechazoDAO]"
                    + "getCodigos Error: "+ex.toString());
            }
        }
        return lista;
    }
    
    /**
    * 
    * @param _descripcion
    * @return 
    */
    public int getCodigosCount(String _descripcion){
        int count = 0;
        Statement statement = null;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName, "[CodigoErrorRechazoDAO.getCodigosCount]");
            statement = dbConn.createStatement();
            String strSql="SELECT count(*) as count "
                    + "FROM codigo_error_rechazo where 1=1 ";
            if (_descripcion != null && _descripcion.compareTo("") != 0){        
                strSql += " and upper(descripcion_codigo_rechazo) like '%"+_descripcion.toUpperCase()+"%'";
            }
            ResultSet rs = statement.executeQuery(strSql);		
            if (rs.next()) {
                count=rs.getInt("count");
            }
            
            rs.close();
        } catch (SQLException|DatabaseException e) {
                e.printStackTrace();
        }
        finally{
            try {
                if (statement != null) statement.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[CodigoErrorRechazoDAO]"
                    + "getCodigosCount Error: "+ex.toString());
            }
        }
        return count;
    }
   
}
