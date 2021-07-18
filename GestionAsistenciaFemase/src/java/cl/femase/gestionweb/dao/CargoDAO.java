/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.CargoVO;
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
public class CargoDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    //private Connection dbConn;
    //DbDirectConnection dbconnection = DbDirectConnection.getInstance();
//    //DbDirectConnection dbconnection = DbDirectConnection.getInstance();
    
    public CargoDAO(PropertiesVO _propsValues) {
//        try {
//            dbLoc = DatabaseLocator.getInstance();
//            m_dbpoolName = _propsValues.getDbPoolName();
//        } catch (DatabaseException ex) {
//            m_logger.error("DbError: "+ex.toString());
//        }
    }

    /**
    * Retorna info de departamento
    * 
    * @param _cargoId
    * @return 
    */
    public CargoVO getCargoByKey(int _cargoId){
        
        CargoVO cargo = null;       
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            String sql = "select cargo_id,"
                + "cargo_nombre,"
                + "cargo_estado "
                + " from cargo "
                + " where cargo_id = " + _cargoId;
            System.out.println("[CargoDAO.getCargoByKey]"
                + "Sql: " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[CargoDAO.getCargoByKey]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                cargo = 
                    new CargoVO(rs.getInt("cargo_id"), rs.getString("cargo_nombre"), rs.getInt("cargo_estado"));
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("Error: "+sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[CargoDAO."
                    + "getCargoByKey]Error: "+ex.toString());
            }
        } 
        return cargo;
    }
    
    /**
    * Actualiza un cargo
    * @param _data
    * @return 
    */
    public MaintenanceVO update(CargoVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "Cargo, "
            + "id: "+_data.getId()
            + ", nombre: "+_data.getNombre()
            + ", estado: "+_data.getEstado();
        
        try{
            String msgFinal = " Actualiza Cargo:"
                + "id [" + _data.getId() + "]" 
                + ", nombre [" + _data.getNombre() + "]"
                + ", estado [" + _data.getEstado() + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE cargo "
                    + " SET "
                    + "cargo_nombre = ?, "
                    + "cargo_estado = ? "
                    + " WHERE cargo_id = ?";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[CargoDAO.update]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setString(1,  _data.getNombre());
            psupdate.setInt(2,  _data.getEstado());
            psupdate.setInt(3,  _data.getId());
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println("[update]Cargo"
                    + ", id:" +_data.getId()
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

 /**
     * Agrega un nuevo Cargo
     * @param _data
     * @return 
     */
    public MaintenanceVO insert(CargoVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al insertar "
            + "Cargo. "
            + " nombre: "+_data.getNombre()
            + " estado: "+_data.getEstado();
        
       String msgFinal = " Inserta Cargo:"
            + "nombre [" + _data.getNombre() + "],"
            +  ", estado [" + _data.getId() + "]";            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO cargo(" +
                "cargo_id, cargo_nombre, cargo_estado) "
                + " VALUES (nextval('cargo_id_sequence'), ?, ?)";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[CargoDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getNombre());
            insert.setInt(2,  _data.getEstado());
                                               
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println("[insert Cargo]"
                    + ", nombre:" +_data.getNombre()
                    + ", estado:" +_data.getEstado()
                    +" insertado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("insert Cargo Error1: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
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
     * Retorna lista con los Cargos existentes en el sistema
     * 
     * @param _nombre
     * @param _estado
     * @param _jtStartIndex
     * @param _jtPageSize
     * @param _jtSorting
     * @return 
     */
    public List<CargoVO> getCargos(String _nombre,
            int _estado,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<CargoVO> lista = 
                new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        CargoVO data;
        
        try{
            String sql ="SELECT cargo_id, "
                + "cargo_nombre, "
                + "cargo_estado " +
                " FROM cargo "
                + "where 1=1 ";
           
            if (_nombre != null && _nombre.compareTo("") != 0){        
                sql += " and upper(cargo_nombre) like '"+_nombre.toUpperCase()+"%'";
            }
            if (_estado != -1){        
                sql += " and cargo_estado = "+_estado;
            }
           
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            System.out.println("[CargoDAO.getCargos]Sql: "+ sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[CargoDAO.getCargos]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new CargoVO();
                data.setId(rs.getInt("cargo_id"));
                data.setNombre(rs.getString("cargo_nombre"));
                data.setEstado(rs.getInt("cargo_estado"));
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
    public int getCargosCount(String _nombre,int _estado){
        int count=0;
        Statement statement = null;
        ResultSet rs        = null;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[CargoDAO.getCargosCount]");
            statement = dbConn.createStatement();
            String strSql ="SELECT count(cargo_id) "
                + "FROM cargo where 1=1 ";
               
            if (_nombre!=null && _nombre.compareTo("")!=0){        
                strSql += " and upper(cargo_nombre) like '"+_nombre.toUpperCase()+"%'";
            }
            if (_estado != -1){        
                strSql += " and cargo_estado = "+_estado;
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
                System.err.println("Error: "+ex.toString());
            }
        }
        return count;
    }
   
}
