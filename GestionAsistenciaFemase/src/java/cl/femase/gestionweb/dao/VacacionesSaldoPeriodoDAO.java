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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
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
    * @param _estado
    * 
    * @return 
    */
    public LinkedHashMap<String, VacacionesSaldoPeriodoVO> getPeriodos(String _empresaId, 
            String _runEmpleado, int _estado){
        
        LinkedHashMap<String, VacacionesSaldoPeriodoVO> hashmap = new LinkedHashMap<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        VacacionesSaldoPeriodoVO periodo;
        
        try{
            String sql = "select "
                    + "vsp.empresa_id,"
                    + "vsp.run_empleado,"
                    + "vsp.inicio_periodo,"
                    + "vsp.fin_periodo,"
                    + "vsp.saldo_vba,"
                    + "vsp.estado_id,"
                    + "estado.estado_nombre,"
                    + "to_char(vsp.update_datetime, 'YYYY-MM-DD HH24:MI:SS') update_datetime, "
                    + "CONCAT(e.empl_nombres, ' ', e.empl_ape_paterno, ' ', e.empl_ape_materno) nombre, "
                    + "d.depto_nombre, cc.ccosto_nombre cenco_nombre "
                + "from vacaciones_saldo_periodo vsp "
                    + "inner join estado on vsp.estado_id = estado.estado_id "
                    + "inner join empleado e on (vsp.empresa_id = e.empresa_id and vsp.run_empleado = e.empl_rut) "
                    + "inner join departamento d on (d.empresa_id=e.empresa_id and e.depto_id = d.depto_id) " 
                    + "inner join centro_costo cc on (e.cenco_id = cc.ccosto_id and d.depto_id = cc.depto_id) "
                + " where vsp.empresa_id = '" + _empresaId + "' "
                    + " and vsp.run_empleado='" + _runEmpleado + "' ";
            if (_estado != -1) sql += " and vsp.estado_id = " + _estado + " order by vsp.inicio_periodo asc";
            else sql += " order by vsp.inicio_periodo desc";
           
            System.out.println(WEB_NAME+"[VacacionesSaldoPeriodoDAO."
                + "getPeriodos]sql: " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[VacacionesSaldoPeriodoDAO.getPeriodos]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                periodo = new VacacionesSaldoPeriodoVO(-1,null,null);
                
                periodo.setEmpresaId(rs.getString("empresa_id"));
                periodo.setRunEmpleado(rs.getString("run_empleado"));
                periodo.setNombreEmpleado(rs.getString("nombre"));
                periodo.setFechaInicioPeriodo(rs.getString("inicio_periodo"));
                periodo.setFechaFinPeriodo(rs.getString("fin_periodo"));
                periodo.setSaldoVBA(rs.getDouble("saldo_vba"));
                periodo.setEstadoId(rs.getInt("estado_id"));
                periodo.setEstadoNombre(rs.getString("estado_nombre"));
                periodo.setUpdateDatetime(rs.getString("update_datetime"));
                String rowkey = periodo.getEmpresaId() 
                    + "|" + periodo.getRunEmpleado()
                    + "|" + periodo.getFechaInicioPeriodo();
                
                LocalDate inicioAsLocalDate = LocalDate.parse(periodo.getFechaInicioPeriodo());
                LocalDate finAsLocalDate = LocalDate.parse(periodo.getFechaFinPeriodo());
                
                periodo.setFechaInicio(inicioAsLocalDate);
                periodo.setFechaFin(finAsLocalDate);
                
                periodo.setDeptoNombre(rs.getString("depto_nombre"));
                periodo.setCencoNombre(rs.getString("cenco_nombre"));
                
                hashmap.put(rowkey, periodo);
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
    * Retorna lista con saldo de vacaciones vigentes por periodo para un empresa|empleado
    * ordenados por periodo (del mas antiguo al mas reciente)
    * 
    * @param _empresaId
    * @param _runEmpleado
    * @param _rowNum
    * 
    * @return 
    */
    public VacacionesSaldoPeriodoVO getPeriodoByRownum(String _empresaId, 
            String _runEmpleado, int _rowNum){
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        VacacionesSaldoPeriodoVO periodo = null;
        
        try{
            String sql = "SELECT * " +
                "FROM ("
                    + "select ROW_NUMBER() OVER (ORDER BY inicio_periodo) AS numero_fila,"
                    + "vsp.empresa_id,vsp.run_empleado,"
                    + "vsp.inicio_periodo,vsp.fin_periodo,"
                    + "vsp.saldo_vba,vsp.estado_id,vsp.update_datetime,"
                    + "vsp.dias_acumulados_vba,vsp.suma_dias_efectivos "
                    + "from vacaciones_saldo_periodo vsp "
                    + "where vsp.empresa_id ='" + _empresaId + "' "
                    + "and vsp.run_empleado ='" + _runEmpleado + "' "
                    + "order by vsp.inicio_periodo"
                    + ") AS subquery " +
                "WHERE numero_fila = " + _rowNum;
           
            System.out.println(WEB_NAME+"[VacacionesSaldoPeriodoDAO."
                + "getPeriodoByRownum]sql: " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[VacacionesSaldoPeriodoDAO.getPeriodoByRownum]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                periodo = new VacacionesSaldoPeriodoVO(-1,null,null);
                periodo.setEmpresaId(rs.getString("empresa_id"));
                periodo.setRunEmpleado(rs.getString("run_empleado"));
                periodo.setFechaInicioPeriodo(rs.getString("inicio_periodo"));
                periodo.setFechaFinPeriodo(rs.getString("fin_periodo"));
                periodo.setSaldoVBA(rs.getDouble("saldo_vba"));
                periodo.setEstadoId(rs.getInt("estado_id"));
                periodo.setUpdateDatetime(rs.getString("update_datetime"));
                LocalDate inicioAsLocalDate = LocalDate.parse(periodo.getFechaInicioPeriodo());
                LocalDate finAsLocalDate = LocalDate.parse(periodo.getFechaFinPeriodo());
                
                periodo.setFechaInicio(inicioAsLocalDate);
                periodo.setFechaFin(finAsLocalDate);
                
           }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println(WEB_NAME+"[VacacionesSaldoPeriodoDAO."
                + "getPeriodoByRownum]Error_1 Sql: " + sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println(WEB_NAME+"[VacacionesSaldoPeriodoDAO."
                    + "getPeriodoByRownum]Error_2: " + ex.toString());
            }
        }
        return periodo;
    }

    /**
    * Elimina los periodos existentes
    * 
    * @param _empresaId
    * @param _runEmpleado
     * @param _estado
    * @return 
    */
    public boolean deletePeriodos(String _empresaId, String _runEmpleado, int _estado){
        boolean isOk    = true;
        int result      = 0;
        PreparedStatement insert    = null;
        
        try{
            String sql = "delete "
                + "from vacaciones_saldo_periodo "
                + "where empresa_id =? "
                    + "and run_empleado = ? "
                    + "and estado_id = ?";
                    //+ "and dias_acumulados_vba is null";

            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[VacacionesSaldoPeriodoDAO.deletePeriodos]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _empresaId);
            insert.setString(2,  _runEmpleado);
            insert.setInt(3,  _estado);
            
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas > 0){
                System.out.println(WEB_NAME+"[delete vacaciones_saldo_periodo]"
                    +"Periodos eliminados OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("delete vacaciones_saldo_periodo Error1: "+sqle.toString());
            isOk = false;
        }finally{
            try {
                if (insert != null) insert.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }

        return isOk;
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
