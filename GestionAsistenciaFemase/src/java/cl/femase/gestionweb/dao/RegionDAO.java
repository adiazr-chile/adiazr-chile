/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.RegionVO;
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
public class RegionDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    //private Connection dbConn;
    //DbDirectConnection dbconnection = DbDirectConnection.getInstance();
    
    public RegionDAO(PropertiesVO _propsValues) {
//        try {
//            dbLoc = DatabaseLocator.getInstance();
//            m_dbpoolName = _propsValues.getDbPoolName();
//        } catch (DatabaseException ex) {
//            m_logger.error("DbError: "+ex.toString());
//        }
    }
    
    /**
     * Retorna lista con las regiones existentes
     * 
     * @param _nombre
     * @param _jtStartIndex
     * @param _jtPageSize
     * @param _jtSorting
     * @return 
     */
    public List<RegionVO> getRegiones(String _nombre,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<RegionVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        RegionVO data;
        
        try{
            String sql ="SELECT region_id id, "
                + "region_nombre nombre, "
                + "short_name "
                + "FROM region "
                + "where 1 = 1 ";

            if (_nombre != null && _nombre.compareTo("") != 0){        
                sql += " and upper(region_nombre) like '%"+_nombre.toUpperCase()+"%'";
            }
            
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[RegionDAO.getRegiones]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new RegionVO();
                data.setRegionId(rs.getInt("id"));
                data.setNombre(rs.getString("nombre"));
                data.setNombreCorto(rs.getString("short_name"));
                
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
    public int getRegionesCount(String _nombre){
        int count=0;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[RegionDAO.getRegionesCount]");
            Statement statement = dbConn.createStatement();
            String strSql ="SELECT count(region_id) "
                + "FROM region "
                + "WHERE 1 = 1 ";

            if (_nombre != null && _nombre.compareTo("") != 0){        
                strSql += " and upper(region_nombre) like '%"+_nombre.toUpperCase()+"%'";
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
   
    /**
    * Agrega una nueva region
    * @param _data
    * @return 
    */
    public ResultCRUDVO insert(RegionVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        int result=0;
        String msgError = "Error al insertar "
            + "Region. "
            + " nombre: " + _data.getNombre()
            + " shortName: " + _data.getNombreCorto();
        
       String msgFinal = " Inserta Region:"
            + "nombre [" + _data.getNombre() + "],"
            +  ", nombreCorto [" + _data.getNombreCorto() + "]";            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO region(" +
                "region_id, region_nombre, short_name) "
                + " VALUES (nextval('region_id_sequence'), ?, ?)";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[RegionDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getNombre());
            insert.setString(2,  _data.getNombreCorto());
                                               
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println(WEB_NAME+"[insert region]"
                    + ", nombre:" +_data.getNombre()
                    + ", nombre_corto:" +_data.getNombreCorto()
                    +" insertado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("insert region Error_1: " + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError + " :" + sqle.toString());
        }finally{
            try {
                if (insert != null) insert.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("insert region Error_2: " + ex.toString());
            }
        }
        return objresultado;
    }
    
    /**
    * Actualiza una region
    * @param _data
    * @return 
    */
    public ResultCRUDVO update(RegionVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "Region, "
            + "id: "+_data.getRegionId()
            + ", nombre: "+_data.getNombre()
            + ", nombreCorto: "+_data.getNombreCorto();
        
        try{
            String msgFinal = " Actualiza Region:"
                + "id [" + _data.getRegionId() + "]" 
                + ", nombre [" + _data.getNombre() + "]"
                + ", nombreCorto [" + _data.getNombreCorto() + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE region "
                    + " SET "
                    + "region_nombre = ?, "
                    + "short_name = ? "
                    + " WHERE region_id = ?";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[RegionDAO.update]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setString(1,  _data.getNombre());
            psupdate.setString(2,  _data.getNombreCorto());
            psupdate.setInt(3,  _data.getRegionId());
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println(WEB_NAME+"[update]Region"
                    + ", id:" +_data.getRegionId()
                    + ", nombre:" +_data.getNombre()
                    + ", nombreCorto:" +_data.getNombreCorto()
                    +" actualizado OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("update Region Error_1: " + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError + " :" + sqle.toString());
        }finally{
            try {
                if (psupdate != null) psupdate.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("update region Error_2: " + ex.toString());
            }
        }

        return objresultado;
    }
}
