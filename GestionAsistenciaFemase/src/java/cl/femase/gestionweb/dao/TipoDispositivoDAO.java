/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.TipoDispositivoVO;
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
public class TipoDispositivoDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    //private Connection dbConn;
    //DbDirectConnection dbconnection = DbDirectConnection.getInstance();
    
    /**
     *
     * @param _propsValues
     */
    public TipoDispositivoDAO(PropertiesVO _propsValues) {
//        try {
//            dbLoc = DatabaseLocator.getInstance();
//            m_dbpoolName = _propsValues.getDbPoolName();
//        } catch (DatabaseException ex) {
//            m_logger.error("DbError: "+ex.toString());
//        }
    }

    /**
     * Actualiza un tipo_dispositivo
     * @param _data
     * @return 
     */
    public ResultCRUDVO update(TipoDispositivoVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "tipo dispositivo, "
            + "id: "+_data.getId()
            + ", nombre: "+_data.getName()
            + ", descripcion: "+_data.getDesc();
        
        try{
            String msgFinal = " Actualiza tipo dispositivo:"
                + "id [" + _data.getId() + "]" 
                + ", nombre [" + _data.getName() + "]"
                + ", descripcion [" + _data.getDesc() + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE tipo_dispositivo "
                    + " SET dev_type_name = ?, "
                    + "dev_type_desc = ? "
                    + " WHERE dev_type_id = ?";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[TipoDispositivoDAO.update]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setString(1,  _data.getName());
            psupdate.setString(2,  _data.getDesc());
            psupdate.setInt(3,  _data.getId());
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println(WEB_NAME+"[update]tipo_dispositivo"
                    + ", id:" +_data.getId()
                    + ", nombre:" +_data.getName()
                    + ", descripcion:" +_data.getDesc()
                    +" actualizado OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("update tipo_dispositivo Error: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }

        return objresultado;
    }

     /**
     * Agrega un nuevo tipo_dispositivo
     * @param _data
     * @return 
     */
    public ResultCRUDVO insert(TipoDispositivoVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        int result=0;
        String msgError = "Error al insertar "
            + "tipo_dispositivo. "
            + " nombre: "+_data.getName()
            + ", descripcion: "+_data.getDesc();
        
       String msgFinal = " Inserta tipo_dispositivo:"
            + "nombre [" + _data.getName() + "]"
            + ", descripcion [" + _data.getDesc()+ "]";
            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO tipo_dispositivo("
                + " dev_type_id, dev_type_name, dev_type_desc) "
                + " VALUES (nextval('tipo_dispositivo_id_seq'), ?, ?) ";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[TipoDispositivoDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getName());
            insert.setString(2,  _data.getDesc());
                        
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println(WEB_NAME+"[insert tipo_dispositivo]"
                    + ", nombre:" +_data.getName()
                    + ", descripcion:" +_data.getDesc()
                    +" insertado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("insert tipo_dispositivo Error1: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }

        return objresultado;
    }
    
    /**
     * Elimina (desactiva) un tipo_dispositivo
     * @param _data
     * @return 
     */
    public ResultCRUDVO delete(TipoDispositivoVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        int result=0;
        String msgError = "Error al eliminar "
                + "un tipo_dispositivo, Id: "+_data.getId()
                + ", nombre: "+_data.getName()
                + ", descripcion: "+_data.getDesc();
        
       String msgFinal = " Elimina tipo_dispositivo:"
            + "Id [" + _data.getId() + "]" 
            + ", nombre [" + _data.getName() + "]"
            + ", descripcion [" + _data.getDesc() + "]";
       
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "delete from "
                + "tipo_dispositivo "
                + "where dev_type_id= ?";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[TipoDispositivoDAO.delete]");
            insert = dbConn.prepareStatement(sql);
            insert.setInt(1,  _data.getId());
                        
            int filasAfectadas = insert.executeUpdate();
            m_logger.debug("[delete tipo_dispositivo]filasAfectadas: "+filasAfectadas);
            if (filasAfectadas == 1){
                m_logger.debug("[delete tipo_dispositivo]"
                    + ", id:" +_data.getId()
                    + ", nombre:" +_data.getName()
                    + ", descripcion:" +_data.getDesc()
                    +" eliminado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("delete tipo_dispositivo Error: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }

        return objresultado;
    }
        
    /**
     * Retorna lista con los tipos de dispositivos existentes
     * 
     * @param _nombre
     * @param _jtStartIndex
     * @param _jtPageSize
     * @param _jtSorting
     * @return 
     */
    public List<TipoDispositivoVO> getTipos(String _nombre,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<TipoDispositivoVO> lista = 
                new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        TipoDispositivoVO data;
        
        try{
            String sql = "SELECT dev_type_id, dev_type_name, dev_type_desc "
                + " FROM tipo_dispositivo "
                + "WHERE 1=1 ";

            if (_nombre!=null && _nombre.compareTo("")!=0){        
                sql += " and upper(dev_type_name) like '"+_nombre.toUpperCase()+"%'";
            }
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TipoDispositivoDAO.getTipos]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new TipoDispositivoVO();
                data.setId(rs.getInt("dev_type_id"));
                data.setName(rs.getString("dev_type_name"));
                data.setDesc(rs.getString("dev_type_desc"));
                
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
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TipoDispositivoDAO.getTiposCount]");
            Statement statement = dbConn.createStatement();
            String strSql="SELECT count(*) as count "
                    + "FROM tipo_dispositivo where 1=1 ";
            if (_nombre!=null && _nombre.compareTo("")!=0){        
                strSql += " and upper(dev_type_name) like '"+_nombre.toUpperCase()+"%'";
            }
            ResultSet rs = statement.executeQuery(strSql);		
            if (rs.next()) {
                count=rs.getInt("count");
            }
            
            statement.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        } catch (SQLException|DatabaseException e) {
            e.printStackTrace();
        }
        
        return count;
    }
   
}
