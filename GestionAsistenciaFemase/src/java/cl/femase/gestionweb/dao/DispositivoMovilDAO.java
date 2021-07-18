/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.DispositivoMovilVO;
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
public class DispositivoMovilDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    
    public DispositivoMovilDAO(PropertiesVO _propsValues) {

    }

    /**
    * Actualiza un dispositivo movil
    * @param _data
    * @return 
    */
    public MaintenanceVO update(DispositivoMovilVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "Dispositivo movil, "
            + "movil_id: " + _data.getId()
            + ", correlativo: " + _data.getCorrelativo()
            + ", rut_director: " + _data.getDirectorRut()
            + ", cenco_director: " + _data.getDirectorCencoId()
            + ", estado: " + _data.getEstado();
        
        try{
            String msgFinal = " Actualiza Dispositivo movil:"
                + "movil_id [" + _data.getId() + "]" 
                + ", correlativo [" + _data.getCorrelativo() + "]"
                + ", rut_director [" + _data.getDirectorRut() + "]"
                + ", cenco_director [" + _data.getDirectorCencoId() + "]"
                + ", estado [" + _data.getEstado() + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE tb_dispositivo_movil "
                    + " SET "
                    + "estado_disp_movil = ? "
                    + " WHERE movil_id = ?";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[DispositivoMovilDAO.update]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setInt(1,  _data.getEstado());
            psupdate.setString(2,  _data.getId());
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println("[DispositivoMovilDAO.update]Dispositivo movil."
                    + " Id:" + _data.getId()
                    + ", correlativo:" + _data.getCorrelativo()
                    + ", rut_director:" + _data.getDirectorRut()
                    + ", estado:" + _data.getEstado()    
                    +" actualizado OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[DispositivoMovilDAO.update]"
                + "Error1: " + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError 
                + " :" + sqle.toString());
        }finally{
            try {
                if (psupdate != null) psupdate.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[DispositivoMovilDAO.update]"
                    + "Error2: " + ex.toString());
            }
        }

        return objresultado;
    }

    
    
    /**
    * Retorna lista con dispositivos moviles
    * 
    * @param _cencoId
    * @param _estado
    * @param _movilId
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * @return 
    */
    public List<DispositivoMovilVO> getDispositivosMoviles(int _cencoId,
            int _estado,
            String _movilId,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<DispositivoMovilVO> lista = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        DispositivoMovilVO data;
        
        try{
            String sql ="SELECT "
                    + "correlativo, movil_id, "
                    + "android_id, "
                    + "to_char(fecha_creacion, 'yyyy-MM-dd HH:mm:ss') fecha_creacion, "
                    + "rut_director, cenco_director, "
                    + "estado_disp_movil, "
                    + "empleado.empresa_id,"
                    + "cenco.ccosto_nombre cenco_nombre,"
                    + "empleado.empl_nombres || ' ' || empleado.empl_ape_paterno || ' ' || empleado.empl_ape_materno nombre_director " +
                " FROM admingestionweb.tb_dispositivo_movil movil " +
                    " inner join admingestionweb.empleado on (movil.rut_director = empleado.empl_rut) " +
                    " inner join admingestionweb.centro_costo cenco on (movil.cenco_director = cenco.ccosto_id) "
                + "where 1 = 1 ";
           
            if (_movilId != null && _movilId.compareTo("") != 0){        
                sql += " and (upper(movil_id) like '" + _movilId.toUpperCase()+"%') ";
            }
            if (_cencoId != -1){        
                sql += " and (cenco_director = " + _cencoId + ") ";
            }
            if (_estado != -1){        
                sql += " and (estado_disp_movil = " + _estado + ") ";
            }
           
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            System.out.println("[DispositivoMovilDAO."
                + "getDispositivosMoviles]Sql: "+ sql);    
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[DispositivoMovilDAO.getDispositivosMoviles]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new DispositivoMovilVO();
                
                data.setCorrelativo(rs.getInt("correlativo"));
                data.setId(rs.getString("movil_id"));
                data.setAndroidId(rs.getString("android_id"));
                data.setFechaHoraCreacion(rs.getString("fecha_creacion"));
                data.setDirectorRut(rs.getString("rut_director"));
                data.setDirectorCencoId(rs.getInt("cenco_director"));
                data.setDirectorRut(rs.getString("rut_director"));
                data.setDirectorNombre(rs.getString("nombre_director"));
                
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setCencoNombre(rs.getString("cenco_nombre"));
                data.setEstado(rs.getInt("estado_disp_movil"));
                
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
    * Retorna dispositivo movil segun id
    * 
    * @param _id
    * @return 
    */
    public DispositivoMovilVO getDispositivoById(String _id){
        PreparedStatement ps    = null;
        ResultSet rs            = null;
        DispositivoMovilVO data = null;
        try{
            String sql ="SELECT "
                    + "correlativo, movil_id, "
                    + "android_id, "
                    + "to_char(fecha_creacion, 'yyyy-MM-dd HH:mm:ss') fecha_creacion, "
                    + "rut_director, cenco_director, "
                    + "estado_disp_movil, "
                    + "empleado.empresa_id,"
                    + "cenco.ccosto_nombre cenco_nombre,"
                    + "empleado.empl_nombres || ' ' || empleado.empl_ape_paterno || ' ' || empleado.empl_ape_materno nombre_director " +
                " FROM admingestionweb.tb_dispositivo_movil movil " +
                    " inner join admingestionweb.empleado on (movil.rut_director = empleado.empl_rut) " +
                    " inner join admingestionweb.centro_costo cenco on (movil.cenco_director = cenco.ccosto_id) "
                + "where (upper(movil_id) = '" + _id.toUpperCase() + "') ";
            System.out.println("[DispositivoMovilDAO."
                + "getDispositivoById]Sql: " + sql);    
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[DispositivoMovilDAO.getDispositivoById]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()){
                data = new DispositivoMovilVO();
                data.setCorrelativo(rs.getInt("correlativo"));
                data.setId(rs.getString("movil_id"));
                data.setAndroidId(rs.getString("android_id"));
                data.setFechaHoraCreacion(rs.getString("fecha_creacion"));
                data.setDirectorRut(rs.getString("rut_director"));
                data.setDirectorCencoId(rs.getInt("cenco_director"));
                data.setDirectorRut(rs.getString("rut_director"));
                data.setDirectorNombre(rs.getString("nombre_director"));
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setCencoNombre(rs.getString("cenco_nombre"));
                data.setEstado(rs.getInt("estado_disp_movil"));
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
        return data;
    }
    
    /**
    * Retorna lista con dispositivos moviles asignados a un centro de costo
    * 
    * @param _cencoId
    * @return 
    */
    public List<DispositivoMovilVO> getDispositivosByCencoId(int _cencoId){
        List<DispositivoMovilVO> lista = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        DispositivoMovilVO data;
        try{
            String sql = "select "
                + "dc.cenco_id,"
                + "dm.correlativo,"
                + "dm.movil_id,"
                + "dm.android_id,"
                + "dm.rut_director "
                + "from admingestionweb.dispositivo_centrocosto dc "
                + "inner join admingestionweb.tb_dispositivo_movil dm "
                + "on (dc.device_id = dm.movil_id) "
                + "where dc.cenco_id = " + _cencoId 
                    + " and estado_disp_movil = 1";
                       
            System.out.println("[DispositivoMovilDAO."
                + "getDispositivosByCencoId]Sql: "+ sql);    
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[DispositivoMovilDAO.getDispositivosByCencoId]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new DispositivoMovilVO();
                data.setDirectorCencoId(rs.getInt("cenco_id"));
                data.setCorrelativo(rs.getInt("correlativo"));
                data.setId(rs.getString("movil_id"));
                data.setAndroidId(rs.getString("android_id"));
                data.setDirectorRut(rs.getString("rut_director"));
                lista.add(data);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("[DispositivoMovilDAO.getDispositivosByCencoId]"
                + "Error1: " + sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[DispositivoMovilDAO."
                    + "getDispositivosByCencoId]"
                    + "Error2: " + ex.toString());
            }
        }
        return lista;
    }
    
    /**
    * 
    * @param _cencoId
    * @param _estado
    * @param _movilId
    * @return 
    */
    public int getDispositivosMovilesCount(int _cencoId,
            int _estado,
            String _movilId){
        int count=0;
        Statement statement = null;
        ResultSet rs        = null;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[DispositivoMovilDAO.getDispositivosMovilesCount]");
            statement = dbConn.createStatement();
            String sql = "SELECT count(movil_id) "
                + "FROM tb_dispositivo_movil "
                + "where 1 = 1 ";
               
            if (_movilId != null && _movilId.compareTo("") != 0){        
                sql += " and (upper(movil_id) like '" + _movilId.toUpperCase()+"%') ";
            }
            if (_cencoId != -1){        
                sql += " and (cenco_director = " + _cencoId + ") ";
            }
            if (_estado != -1){        
                sql += " and (estado_disp_movil = " + _estado + ") ";
            }
            
            rs = statement.executeQuery(sql);		
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
