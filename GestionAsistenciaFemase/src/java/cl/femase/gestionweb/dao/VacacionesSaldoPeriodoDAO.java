/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.VacacionesSaldoPeriodoVO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.apache.log4j.Logger;

/**
*
* Clase relacionada con la tabla 'vacaciones_saldo_periodo'
* 
* @author Alexander
*/
public class VacacionesSaldoPeriodoDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    public boolean m_usedGlobalDbConnection = false;
    
    private String SQL_UPDATE_ESTADO_PERIODO = 
        "update vacaciones_saldo_periodo set estado_id = ? "
            + "where empresa_id = ? "
            + "and run_empleado = ? "
            + "and inicio_periodo = ?";
    
    /**
    *
    * @param _propsValues
    */
    public VacacionesSaldoPeriodoDAO(PropertiesVO _propsValues) {
    }

    /**
    * Retorna lista con saldo de vacaciones por periodo para un empresa|empleado
    * ordenados por periodo (del mas reciente al mas antiguo)
    * 
    * @param _empresaId
    * @param _runEmpleado
    * 
    * @return 
    */
    public LinkedHashMap<String, VacacionesSaldoPeriodoVO> getPeriodos(String _empresaId, 
            String _runEmpleado){
        
        LinkedHashMap<String, VacacionesSaldoPeriodoVO> hashmap = new LinkedHashMap<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        VacacionesSaldoPeriodoVO data;
        
        try{
            String sql = "select "
                    + "vsp.empresa_id,"
                    + "vsp.run_empleado,"
                    + "vsp.inicio_periodo,"
                    + "vsp.fin_periodo,"
                    + "vsp.saldo_vba,"
                    + "vsp.estado_id,"
                    + "estado.estado_nombre,"
                    + "vsp.update_datetime "
                + "from vacaciones_saldo_periodo vsp "
                    + "inner join estado on vsp.estado_id = estado.estado_id "
                + "where vsp.empresa_id = '" + _empresaId + "' "
                    + "and vsp.run_empleado='" + _runEmpleado + "' "
                + "order by vsp.inicio_periodo desc";
           
            System.out.println(WEB_NAME+"[VacacionesSaldoPeriodoDAO."
                + "getPeriodos]sql: " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[VacacionesSaldoPeriodoDAO.getPeriodos]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new VacacionesSaldoPeriodoVO(-1,null,null);
                
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setRunEmpleado(rs.getString("run_empleado"));
                data.setFechaInicioPeriodo(rs.getString("inicio_periodo"));
                data.setFechaFinPeriodo(rs.getString("fin_periodo"));
                data.setSaldoVBA(rs.getDouble("saldo_vba"));
                data.setEstadoId(rs.getInt("estado_id"));
                data.setEstadoNombre(rs.getString("estado_nombre"));
                data.setUpdateDatetime(rs.getString("update_datetime"));
                String rowkey = data.getEmpresaId() + "|" + data.getRunEmpleado()+ "|" + data.getFechaInicioPeriodo();
                hashmap.put(rowkey, data);
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
        return hashmap;
    }

        
    /**
    * 
    * @param _periodos
    * @throws java.sql.SQLException
    */
    public void updateEstadoPeriodos(ArrayList<VacacionesSaldoPeriodoVO> _periodos) throws SQLException{
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[VacacionesSaldoPeriodoDAO.updateEstadoPeriodos]");
            PreparedStatement statement = dbConn.prepareStatement(SQL_UPDATE_ESTADO_PERIODO);
            int i = 0;
            System.out.println(WEB_NAME+"[VacacionesSaldoPeriodoDAO.updateEstadoPeriodos]"
                + "items a modificar: " + _periodos.size());
            for (VacacionesSaldoPeriodoVO entity : _periodos) {
                System.out.println(WEB_NAME+"[VacacionesSaldoPeriodoDAO.updateEstadoPeriodos] "
                    + "update estado periodo. "
                    + "EmpresaId= " + entity.getEmpresaId()
                    + ", run empleado= " + entity.getRunEmpleado()
                    + ", fecha inicio periodo= " + entity.getFechaInicioPeriodo()
                    + ", estado: " + entity.getEstadoId());
                            
                statement.setInt(1, entity.getEstadoId());
                statement.setString(2, entity.getEmpresaId());
                statement.setString(3,  entity.getRunEmpleado());
                statement.setDate(4,  
                    Utilidades.getJavaSqlDate(entity.getFechaInicioPeriodo(), "yyyy-MM-dd"));
                statement.addBatch();
                i++;

                if (i % 50 == 0 || i == _periodos.size()) {
                    int[] rowsAffected = statement.executeBatch(); // Execute every 1000 items.
                    System.out.println(WEB_NAME+"[VacacionesSaldoPeriodoDAO."
                        + "updateEstadoPeriodos]filas afectadas= "+rowsAffected.length);
                }
            }
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("Error: "+sqle.toString());
        }finally{
            dbLocator.freeConnection(dbConn);
            
        }
    }
    
    public void openDbConnection(){
        try {
            m_usedGlobalDbConnection = true;
            dbConn = dbLocator.getConnection(m_dbpoolName,"[UsersDAO.openDbConnection]");
        } catch (DatabaseException ex) {
            System.err.println("[UsersDAO.openDbConnection]"
                + "Error: " + ex.toString());
        }
    }
    
    public void closeDbConnection(){
        try {
            m_usedGlobalDbConnection = false;
            dbLocator.freeConnection(dbConn);
        } catch (Exception ex) {
            System.err.println("[UsersDAO.closeDbConnection]"
                + "Error: "+ex.toString());
        }
    }
}
