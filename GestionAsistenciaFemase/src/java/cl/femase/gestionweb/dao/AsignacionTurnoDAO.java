/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.AsignacionTurnoVO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexander
 */
public class AsignacionTurnoDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
   
    String SQL_INSERT_ONE_ROW = "INSERT INTO turno_asignacion("
        + "empresa_id, "
        + "rut_empleado, "
        + "id_turno, "
        + "fecha_desde, "
        + "fecha_asignacion, "
        + "last_update,"
        + "username) "
        + "VALUES (?, ?, ?, current_timestamp, "
            + "current_timestamp, current_timestamp, ?)";
    
    /**
    * Retorna lista con las asignaciones de turno existentes
    * 
    * @param _empresaId
    * @param _rutEmpleado
    * @param _turnoNormalId
    * @param _startDate
    * @param _endDate
    * @param _cencoId
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * 
    * @return 
    */
    public List<AsignacionTurnoVO> getAsignaciones(String _empresaId, 
            String _rutEmpleado,
            int _turnoNormalId,
            String _startDate,
            String _endDate,
            int _cencoId,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<AsignacionTurnoVO> asignaciones = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        AsignacionTurnoVO data;
        
        try{
            String sql = "select "
                    + "ta.empresa_id,"
                    + "empresa.empresa_nombre,"
                    + "ta.rut_empleado,"
                    + "empleado.empl_nombres || ' ' || empleado.empl_ape_materno nombre_empleado,"
                    + "departamento.depto_id,"
                    + "departamento.depto_nombre,"
                    + "centro_costo.ccosto_id cenco_id,"
                    + "centro_costo.ccosto_nombre,"
                    + "ta.id_turno,"
                    + "turno.nombre_turno,"
                    + "to_char(ta.fecha_desde,'yyyy-MM-dd HH24:MI:SS') fecha_desde,"
                    + "to_char(ta.fecha_hasta,'yyyy-MM-dd HH24:MI:SS') fecha_hasta,"
                    + "to_char(ta.fecha_asignacion,'yyyy-MM-dd HH24:MI:SS') fecha_asignacion,"
                    + "to_char(ta.last_update,'yyyy-MM-dd HH24:MI:SS') last_update,"
                    + "ta.username "
                    + "from turno_asignacion ta "
                    + " join empleado on (ta.empresa_id = empleado.empresa_id and ta.rut_empleado=empleado.empl_rut) "
                    + " JOIN empresa ON empleado.empresa_id::text = empresa.empresa_id::text "
                    + " JOIN departamento ON empleado.depto_id::text = departamento.depto_id::text "
                    + " JOIN centro_costo ON empleado.cenco_id = centro_costo.ccosto_id "
                    + " JOIN comuna ON centro_costo.id_comuna = comuna.comuna_id "
                    + " JOIN region ON comuna.region_id = region.region_id "
                    + " JOIN cargo ON empleado.empl_id_cargo = cargo.cargo_id "
                    + " JOIN turno ON (ta.empresa_id = turno.empresa_id and ta.id_turno = turno.id_turno) "
                    + "where (1 = 1) ";

            if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
                sql += " and ta.empresa_id = '" + _empresaId + "' ";
            }
            if (_cencoId != -1){        
                sql += " and empleado.cenco_id = " + _cencoId;
            }
            if (_turnoNormalId != -1){        
                sql += " and ta.id_turno = " + _turnoNormalId;
            }
            if (_rutEmpleado != null && _rutEmpleado.compareTo("-1") != 0){        
                sql += " and ta.rut_empleado = '" + _rutEmpleado +"' ";
            }
            
            if ( (_startDate != null && _startDate.compareTo("") != 0) ){        
                if ( (_endDate == null || _endDate.compareTo("") == 0) ){ 
                    _endDate = _startDate;
                }
                sql += " and ta.fecha_desde::date "
                    + "between '" + _startDate + "' and '" + _endDate + "' ";
            }
            
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            System.out.println(WEB_NAME+"[AsignacionTurnoDAO.getAsignaciones]sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[AsignacionTurnoDAO.getAsignaciones]");
            ps = dbConn.prepareStatement(sql);
            
            rs = ps.executeQuery();

            while (rs.next()){
                data = new AsignacionTurnoVO();
                
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setEmpresaNombre(rs.getString("empresa_nombre"));
                data.setRutEmpleado(rs.getString("rut_empleado"));
                data.setNombreEmpleado(rs.getString("nombre_empleado"));
                data.setDeptoId(rs.getString("depto_id"));
                data.setDeptoNombre(rs.getString("depto_nombre"));
                data.setCencoId(rs.getInt("cenco_id"));
                data.setCencoNombre(rs.getString("ccosto_nombre"));
                data.setIdTurno(rs.getInt("id_turno"));
                data.setNombreTurno(rs.getString("nombre_turno"));
                data.setFechaDesde(rs.getString("fecha_desde"));
                data.setFechaHasta(rs.getString("fecha_hasta"));
                data.setFechaAsignacion(rs.getString("fecha_asignacion"));
                data.setLastUpdate(rs.getString("last_update"));
                data.setUsername(rs.getString("username"));
                
                asignaciones.add(data);
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
        
        return asignaciones;
    }
    
    /**
    * Retorna turno vigente
    * 
    * @param _empresaId
    * @param _rutEmpleado
    * 
    * @return 
    */
    public AsignacionTurnoVO getTurnoVigente(String _empresaId, 
            String _rutEmpleado){
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        AsignacionTurnoVO data = null;
        
        try{
            String sql = "select "
                + "ta.id_turno,"
                + "turno.nombre_turno,"
                + "ta.fecha_desde,"
                + "ta.fecha_hasta,"
                + "ta.fecha_asignacion "
                + "from turno_asignacion ta "
                + "inner JOIN turno "
                + " ON (ta.empresa_id = turno.empresa_id "
                + "and ta.id_turno = turno.id_turno) "
                + "where ta.empresa_id='"+_empresaId+"' "
                + "and ta.fecha_hasta is null "
                + "and ta.rut_empleado='"+_rutEmpleado+"' "
                + "order by ta.fecha_desde desc limit 1";
            
            System.out.println(WEB_NAME+"[AsignacionTurnoDAO.getTurnoVigente]sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[AsignacionTurnoDAO.getTurnoVigente]");
            ps = dbConn.prepareStatement(sql);
            
            rs = ps.executeQuery();

            if (rs.next()){
                data = new AsignacionTurnoVO();
               
                data.setEmpresaId(_empresaId);
                data.setRutEmpleado(_rutEmpleado);
                data.setIdTurno(rs.getInt("id_turno"));
                data.setNombreTurno(rs.getString("nombre_turno"));
                data.setFechaDesde(rs.getString("fecha_desde"));
                data.setFechaHasta(rs.getString("fecha_hasta"));
                data.setFechaAsignacion(rs.getString("fecha_asignacion"));
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
    * Retorna turno inmediatamente anterior al actual
    * 
    * @param _empresaId
    * @param _rutEmpleado
    * 
    * @return 
    */
    public AsignacionTurnoVO getTurnoAnterior(String _empresaId, 
            String _rutEmpleado){
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        AsignacionTurnoVO data = null;
        
        try{
            String sql = "select "
                + "ta.id_turno,"
                + "turno.nombre_turno,"
                + "ta.fecha_desde,"
                + "ta.fecha_hasta,"
                + "ta.fecha_asignacion "
                + "from turno_asignacion ta "
                + "inner JOIN turno "
                + " ON (ta.empresa_id = turno.empresa_id "
                + "and ta.id_turno = turno.id_turno) "
                + "where ta.empresa_id='"+_empresaId+"' "
                + "and ta.fecha_hasta is not null "
                + "and ta.rut_empleado='"+_rutEmpleado+"' "
                + "order by ta.fecha_desde desc limit 1";
            
            System.out.println(WEB_NAME+"[AsignacionTurnoDAO.getTurnoAnterior]sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[AsignacionTurnoDAO.getTurnoAnterior]");
            ps = dbConn.prepareStatement(sql);
            
            rs = ps.executeQuery();

            if (rs.next()){
                data = new AsignacionTurnoVO();
               
                data.setEmpresaId(_empresaId);
                data.setRutEmpleado(_rutEmpleado);
                data.setIdTurno(rs.getInt("id_turno"));
                data.setNombreTurno(rs.getString("nombre_turno"));
                data.setFechaDesde(rs.getString("fecha_desde"));
                data.setFechaHasta(rs.getString("fecha_hasta"));
                data.setFechaAsignacion(rs.getString("fecha_asignacion"));
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
     * Retorna id del turno actual del empleado.
     * Si el valor retornado es -99, indica que no hay turno vigente asignado.
     * 
     * @param _empresaId
     * @param _rutEmpleado
     * @return 
     */
    public int getTurnoActual(String _empresaId, 
            String _rutEmpleado){
        
        int idTurnoActual = -99;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            String sql = "select id_turno "
                + "from turno_asignacion "
                + "where "
                + "empresa_id='" + _empresaId + "' "
                + "and rut_empleado = '" + _rutEmpleado + "' "
                + "order by fecha_desde desc limit 1";
            
            System.out.println(WEB_NAME+"[AsignacionTurnoDAO.getTurnoActual]sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[AsignacionTurnoDAO.getTurnoActual]");
            ps = dbConn.prepareStatement(sql);
            
            rs = ps.executeQuery();

            if (rs.next()){
                idTurnoActual = rs.getInt("id_turno");
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
        
        return idTurnoActual;
    }
    
    /**
     * 
     * @param _asignacionTurno
     */
    public void venceTurno(AsignacionTurnoVO _asignacionTurno){
        try 
        {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[AsignacionTurnoDAO.venceTurnoAsignado]");
            String sqlUpdate = "update turno_asignacion "
                + "set fecha_hasta = current_timestamp, "
                    + "last_update = current_timestamp,"
                    + "username = ? "
                + "where empresa_id = ? "
                    + "and rut_empleado = ? "
                    + "and id_turno = ?";
            PreparedStatement statement = dbConn.prepareStatement(sqlUpdate);
           
            System.out.println(WEB_NAME+"[AsignacionTurnoDAO.venceTurnoAsignado] "
                + "Vence turno asignado. "
                + "empresaId= " + _asignacionTurno.getEmpresaId()
                + ", rut= " + _asignacionTurno.getRutEmpleado()
                + ", idTurno= " + _asignacionTurno.getIdTurno() 
                );
         
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            statement.setString(1,  _asignacionTurno.getUsername());
            statement.setString(2,  _asignacionTurno.getEmpresaId());
            statement.setString(3,  _asignacionTurno.getRutEmpleado());
            statement.setInt(4,  _asignacionTurno.getIdTurno());
            
            // ...
            statement.addBatch();

            int[] rowsAffected = statement.executeBatch(); // Execute every 1000 items.
            System.out.println(WEB_NAME+"[AsignacionTurnoDAO.venceTurnoAsignado]filas afectadas= "+rowsAffected.length);
           
        }catch(DatabaseException | SQLException ex){
            System.err.println("[AsignacionTurnoDAO.venceTurnoAsignado]Error= " + ex.toString());
        }finally{
            try {
                dbConn.close();
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
    }
    
    /**
     * 
     * @param _asignacionTurno
     */
    public void insertaTurno(AsignacionTurnoVO _asignacionTurno){
        try 
        {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[AsignacionTurnoDAO.insertaTurnoAsignado]");
            PreparedStatement statement = dbConn.prepareStatement(SQL_INSERT_ONE_ROW);
           
            System.out.println(WEB_NAME+"[AsignacionTurnoDAO.insertaTurnoAsignado] "
                + "Insert turno asignado. "
                + "empresaId= " + _asignacionTurno.getEmpresaId()
                + ", rut= " + _asignacionTurno.getRutEmpleado()
                + ", fechaDesde= " + _asignacionTurno.getFechaDesde()
                + ", idTurno= " + _asignacionTurno.getIdTurno() 
                + ", username= " + _asignacionTurno.getUsername());
         
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            statement.setString(1,  _asignacionTurno.getEmpresaId());
            statement.setString(2,  _asignacionTurno.getRutEmpleado());
            statement.setInt(3,  _asignacionTurno.getIdTurno());
//            statement.setTimestamp(4, new Timestamp(sdf.parse(_asignacionTurno.getFechaDesde()).getTime()));
//            statement.setTimestamp(5, new Timestamp(sdf.parse(_asignacionTurno.getFechaAsignacion()).getTime()));
//            statement.setTimestamp(6, new Timestamp(sdf.parse(_asignacionTurno.getFechaAsignacion()).getTime()));
            statement.setString(4,  _asignacionTurno.getUsername());
            // ...
            statement.addBatch();

            int[] rowsAffected = statement.executeBatch(); // Execute every 1000 items.
            System.out.println(WEB_NAME+"[AsignacionTurnoDAO.insertaTurnoAsignado]filas afectadas= "+rowsAffected.length);
           
        }catch(DatabaseException | SQLException ex){
            System.err.println("[AsignacionTurnoDAO.insertaTurnoAsignado]Error= " + ex.toString());
        }finally{
            try {
                dbConn.close();
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
    }
    
    /**
     * 
     * @param _empresaId
     * @param _rutEmpleado
     * @param _turnoId
     * @param _startDate
     * @param _endDate
     * @param _cencoId
     * @return 
     */
    public int getAsignacionesCount(String _empresaId, 
            String _rutEmpleado,
            int _turnoId,
            String _startDate,
            String _endDate,
            int _cencoId){
        int count=0;
        ResultSet rs;
        //Statement statement = null;
        //Statement statement = null;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[AsignacionTurnoDAO.getAsignacionesCount]");
            
            try (Statement statement = dbConn.createStatement()) {
                String strSql ="SELECT count(ta.rut_empleado) "
                         + "from "
                + "admingestionweb.turno_asignacion ta "
                + "inner join admingestionweb.view_empleado empleado "
                + "on (ta.empresa_id = empleado.empresa_id and ta.rut_empleado = empleado.rut) "
                    + "where (1 = 1) ";

                if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
                    strSql += " and ta.empresa_id = '" + _empresaId + "' ";
                }
                if (_cencoId != -1){        
                    strSql += " and empleado.cenco_id = " + _cencoId;
                }
                if (_rutEmpleado != null && _rutEmpleado.compareTo("-1") != 0){        
                    strSql += " and ta.rut_empleado = '" + _rutEmpleado +"' ";
                }
                System.out.println(WEB_NAME+"[AsignacionTurnoDAO.getAsignacionesCount]sql: "+strSql); 
                rs = statement.executeQuery(strSql);
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
   
}
