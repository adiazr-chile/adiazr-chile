/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.business.CalendarioFeriadoBp;
import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.vo.DetalleAusenciaVO;
import cl.femase.gestionweb.vo.InfoFeriadoVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.PermisoAdministrativoVO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.log4j.Logger;

/**
*
* Clase para acceder a la tabla 'permiso_administrativo'
* 
* @author Alexander
*/
public class PermisosAdministrativosDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    public boolean m_usedGlobalDbConnection = false;
    private PropertiesVO systemProperties;
    /**
    *   Fecha en formato yyyy-MM-dd 
    */
    private final SimpleDateFormat m_sdf = new SimpleDateFormat("yyyy-MM-dd");
    
//    private String SQL_INSERT_PERMISO_ADMINISTRATIVO = 
//        "INSERT INTO permiso_administrativo ("
//            + "empresa_id, "
//            + "run_empleado, "
//            + "anio, "
//            + "dias_disponibles, "
//            + "dias_utilizados, "
//            + "last_update) "
//            + "	VALUES (?, ?, ?, ?, ?, ?)";
//    
//    private String SQL_DELETE_PERMISO_ADMINISTRATIVO = "delete from permiso_administrativo " +
//        "WHERE empresa_id = ? " +
//            "and run_empleado = ? "
//            + "and anio = ?";
    
    /**
    *
    * @param _propsValues
    */
    public PermisosAdministrativosDAO(PropertiesVO _propsValues) {
        systemProperties = _propsValues;
    }

    /**
    * Actualiza dias disponibles y dias_utilizados
    * 
    * @param _data
    * @return 
    */
    public MaintenanceVO updateResumenPA(PermisoAdministrativoVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "resumen Permiso Administrativo, "
            + "EmpresaId: " + _data.getEmpresaId()
            + ", rutEmpleado: " + _data.getRunEmpleado()    
            + ", anio: " + _data.getAnio()
            + ", dias_disponibles: " + _data.getDiasDisponibles()
            + ", dias_utilizados: " + _data.getDiasUtilizados();
        
        try{
            String msgFinal = " Actualiza resumen Permiso Administrativo:"
                + "EmpresaId [" + _data.getEmpresaId() + "]" 
                + ", runEmpleado [" + _data.getRunEmpleado() + "]"    
                + ", anio [" + _data.getAnio() + "]"
                + ", dias_disponibles [" + _data.getDiasDisponibles() + "]"
                + ", dias_utilizados [" + _data.getDiasUtilizados() + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE permiso_administrativo "
                + "SET "
                    + "dias_disponibles = ?, "
                    + "dias_utilizados = ?, "
                    + "last_update = current_timestamp "
                + "WHERE empresa_id = ? "
                    + "and run_empleado = ? "
                    + "and anio = ?";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[PermisosAdministrativosDAO.updateResumenPA]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setDouble(1,  _data.getDiasDisponibles());
            psupdate.setDouble(2,  _data.getDiasUtilizados());
            
            //filtro update            
            psupdate.setString(3, _data.getEmpresaId());
            psupdate.setString(4, _data.getRunEmpleado());
            psupdate.setInt(5, _data.getAnio());
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println("[PermisosAdministrativosDAO.updateResumenPA]"
                   + ", empresaId:" + _data.getEmpresaId()
                    + ", runEmpleado:" + _data.getRunEmpleado()    
                    + ", dias_disponibles:" +_data.getDiasDisponibles()
                    + ", dias_utilizados:" + _data.getDiasUtilizados()
                    + " actualizado OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[PermisosAdministrativosDAO.updateResumenPA]"
                + "update Error: " + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (psupdate!=null) psupdate.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[PermisosAdministrativosDAO.updateResumenPA]Error: "+ex.toString());
            }
        }

        return objresultado;
    }

    /**
    * Reinicie el número de días de 'permisos administrativos' (disponibles y utilizados)
    * Esto es: 
    *   Insertar un nuevo registro en la tabla 'permiso_administrativo' 
    *   para el año sgte al anio especificado con dias_disponibles=NUM_MAXIMO y dias_utilizados = 0.
    * 
    * @param _empresaId
    * @param _maximoDiasPA
    * @param _currentYear
    * 
    * @return 
    */
    public MaintenanceVO resetearDiasAdministrativosAnio(String _empresaId, 
            int _maximoDiasPA,
            int _currentYear){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al retetear "
            + "Resumen Permiso Administrativo para el anio " + _currentYear
            + ", empresaId: " + _empresaId
            + ", maximo dias PA: " + _maximoDiasPA;
        
        String msgFinal = " Inserta Resumen Permiso Administrativo:"
            + " Anio [" + _currentYear 
            + ", empresaId [" + _empresaId 
            + ", runEmpleado [" + _empresaId + "]"    
            + ", maximo dias PA [" + _maximoDiasPA + "]";
       
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO permiso_administrativo("
                + "empresa_id, "
                + "run_empleado, "
                + "anio, "
                + "dias_disponibles, "
                + "dias_utilizados, "
                + "last_update) "
                + "select empresa_id, empl_rut, "
                    + _currentYear + "," + _maximoDiasPA + ",0,current_timestamp "
                    + "from empleado "
                    + "where empleado.empresa_id= '" + _empresaId + "' and empl_estado = 1 "
                    + "and empl_fec_fin_contrato >= current_date";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[PermisosAdministrativosDAO.resetearDiasAdministrativosAnio]");
            insert = dbConn.prepareStatement(sql);
                        
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas > 0){
                System.out.println("[PermisosAdministrativosDAO.resetearDiasAdministrativosAnio]"
                    + "Reseteo de Resumen PA OK, Anio: " + _currentYear+", filas afectadas= " + filasAfectadas);
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[PermisosAdministrativosDAO.resetearDiasAdministrativosAnio]"
                + "Error1: " + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (insert != null) insert.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[PermisosAdministrativosDAO.resetearDiasAdministrativosAnio]"
                    + "Error: " + ex.toString());
            }
        }

        return objresultado;
    }
    
    /**
    * Agrega un nuevo registro de Permiso Administrativo
    * 
    * @param _data
    * @return 
    */
    public MaintenanceVO insertResumenPA(PermisoAdministrativoVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al insertar "
            + "Resumen Permiso Administrativo, "
            + "EmpresaId: " + _data.getEmpresaId()
            + ", runEmpleado: " + _data.getRunEmpleado()    
            + ", anio: " + _data.getAnio()
            + ", dias_disponibles: " + _data.getDiasDisponibles()
            + ", dias_utilizados: " + _data.getDiasUtilizados();
        
        String msgFinal = " Inserta Resumen Permiso Administrativo:"
            + "EmpresaId [" + _data.getEmpresaId() + "]" 
            + ", runEmpleado [" + _data.getRunEmpleado() + "]"    
            + ", anio [" + _data.getAnio() + "]"
            + ", dias_disponibles [" + _data.getDiasDisponibles() + "]"
            + ", dias_utilizados [" + _data.getDiasUtilizados() + "]";
       
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO permiso_administrativo "
                + "(empresa_id, "
                + "run_empleado, "
                + "anio, "
                + "dias_disponibles, "
                + "dias_utilizados, "
                + "last_update) "
                + "VALUES (?, ?, ?, ?, ?, current_timestamp)";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[PermisosAdministrativosDAO.insertResumenPA]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getEmpresaId());
            insert.setString(2,  _data.getRunEmpleado());
            insert.setInt(3,  _data.getAnio());
            insert.setDouble(4,  _data.getDiasDisponibles());
            insert.setDouble(5,  _data.getDiasUtilizados());
                        
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println("[PermisosAdministrativosDAO.insertResumenPA]"
                    + "EmpresaId:" + _data.getEmpresaId()
                    + ", rutEmpleado:" + _data.getRunEmpleado()    
                    + ", anio:" +_data.getAnio()
                    + ", dias_utilizados:" + _data.getDiasUtilizados()
                    + ", dias_disponibles:" + _data.getDiasDisponibles()
                    + " insertado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[PermisosAdministrativosDAO.insertResumenPA]"
                + "Error1: " + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (insert != null) insert.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[PermisosAdministrativosDAO.insertResumenPA]"
                    + "Error: " + ex.toString());
            }
        }

        return objresultado;
    }
    
    /**
    * Elimina info de permiso administrativo
    * 
    * @param _data
    * @return 
    */
    public MaintenanceVO deleteResumenPA(PermisoAdministrativoVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al eliminar "
            + "resumen permiso administrativo, "
            + "empresaId: " + _data.getEmpresaId()
            + ", runEmpleado: " + _data.getRunEmpleado()
            + ", anio: " + _data.getAnio();
        
       String msgFinal = " Elimina resumen permiso administrativo:"
            + "empresaId [" + _data.getEmpresaId() + "]" 
            + ", rutEmpleado [" + _data.getRunEmpleado() + "]"
            + ", anio [" + _data.getAnio() + "]";
        objresultado.setMsg(msgFinal);
        PreparedStatement psdelete    = null;
        
        try{
            String sql = "delete from permiso_administrativo " +
                "WHERE empresa_id = ? " +
                    "and run_empleado = ? "
                    + "and anio = ?";

            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[PermisosAdministrativosDAO.deleteResumenPA]");
            psdelete = dbConn.prepareStatement(sql);
            psdelete.setString(1,  _data.getEmpresaId());
            psdelete.setString(2,  _data.getRunEmpleado());
            psdelete.setInt(3,  _data.getAnio());
                        
            int filasAfectadas = psdelete.executeUpdate();
            System.out.println("[PermisosAdministrativosDAO.deleteResumenPA]"
                + "filasAfectadas: " + filasAfectadas);
            if (filasAfectadas == 1){
                System.out.println("[PermisosAdministrativosDAO.deleteResumenPA]"
                    + "delete resumen permiso_administrativo]"
                    + ", empresaId:" +_data.getEmpresaId()
                    + ", runEmpleado:" + _data.getRunEmpleado()
                    + ", anio:" + _data.getAnio()    
                    + " eliminado OK!");
            }
            
            psdelete.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[PermisosAdministrativosDAO.deleteResumenPA]"
                + "Error: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (psdelete != null) psdelete.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[PermisosAdministrativosDAO.deleteResumenPA]"
                    + "Error: " + ex.toString());
            }
        }

        return objresultado;
    }
    
    /**
    * Elimina info de permiso administrativo
    * 
    * @param _empresaId
    * @param _anio
    * @return 
    */
    public boolean deleteResumenPAAnio(String _empresaId, int _anio){
        int result=0;
        PreparedStatement psdelete    = null;
        boolean isOk=true;
        try{
            String sql = "delete from permiso_administrativo " +
                "WHERE empresa_id = ? " +
                    "and anio = ?";

            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[PermisosAdministrativosDAO.deleteResumenPA]");
            psdelete = dbConn.prepareStatement(sql);
            psdelete.setString(1,  _empresaId);
            psdelete.setInt(2,  _anio);
                        
            int filasAfectadas = psdelete.executeUpdate();
            System.out.println("[PermisosAdministrativosDAO.deleteResumenPAAnio]"
                + "filasAfectadas: " + filasAfectadas);
            if (filasAfectadas == 1){
                System.out.println("[PermisosAdministrativosDAO.deleteResumenPAAnio]"
                    + "delete resumen permiso_administrativo]"
                    + ", empresaId:" + _empresaId
                    + ", anio:" + _anio   
                    + " registros eliminados sin error");
            }
            
            psdelete.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[PermisosAdministrativosDAO.deleteResumenPAAnio]"
                + "Error: "+sqle.toString());
            isOk = false;
       }finally{
            try {
                if (psdelete != null) psdelete.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[PermisosAdministrativosDAO.deleteResumenPAAnio]"
                    + "Error: " + ex.toString());
            }
        }

        return isOk;
    }
    
    /**
    * Retorna lista con Permisos administrativos
    * 
    * @param _empresaId
    * @param _runEmpleado
     * @param _anio
    * @param _cencoId
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * 
    * @return 
    */
    public List<PermisoAdministrativoVO> getResumenPermisosAdministrativos(String _empresaId, 
            String _runEmpleado,
            int _anio,
            int _cencoId,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<PermisoAdministrativoVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        PermisoAdministrativoVO data;
        
        try{
            String sql = "select "
                    + "pa.empresa_id,"
                    + "empleado.depto_id,"
                    + "empleado.depto_nombre,"
                    + "empleado.cenco_id,"
                    + "empleado.ccosto_nombre,"
                    + "pa.run_empleado,"
                    + "pa.anio,"
                    + "empleado.nombre || ' ' || empleado.materno nombre_empleado,"
                    + "pa.dias_disponibles,"
                    + "pa.dias_utilizados,"
                    + "to_char(pa.last_update,'yyyy-MM-dd HH24:MI:SS') last_update "
                + "from permiso_administrativo pa "
                    + "inner join view_empleado empleado "
                        + "on (empleado.empresa_id = pa.empresa_id and empleado.rut = pa.run_empleado) "
                + "where anio = " + _anio + " ";
           
            if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
                sql += " and pa.empresa_id = '" + _empresaId + "' ";
            }
            
            if (_runEmpleado != null && _runEmpleado.compareTo("-1") != 0){
                sql += " and pa.run_empleado = '" + _runEmpleado + "' ";
            }
            
            if (_cencoId != -1){
                sql += " and empleado.cenco_id = " + _cencoId;
            }
           
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            System.out.println("[PermisosAdministrativosDAO."
                + "getResumenPermisosAdministrativos]sql: " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[PermisosAdministrativosDAO.getResumenPermisosAdministrativos]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new PermisoAdministrativoVO();
                
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setDeptoId(rs.getString("depto_id"));
                data.setDeptoNombre(rs.getString("depto_nombre"));
                data.setCencoId(rs.getInt("cenco_id"));
                data.setCencoNombre(rs.getString("ccosto_nombre"));
                data.setRunEmpleado(rs.getString("run_empleado"));
                data.setNombreEmpleado(rs.getString("nombre_empleado"));
                data.setAnio(rs.getInt("anio"));
                
                data.setDiasDisponibles(rs.getDouble("dias_disponibles"));
                data.setDiasUtilizados(rs.getDouble("dias_utilizados"));
                data.setLastUpdate(rs.getString("last_update"));
                
                data.setRowKey(data.getEmpresaId() + "|" + data.getRunEmpleado());
                lista.add(data);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[PermisosAdministrativosDAO."
                + "getResumenPermisosAdministrativos]"
                + "Error_1: " + sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[PermisosAdministrativosDAO."
                    + "getResumenPermisosAdministrativos]"
                    + "Error_2: " + ex.toString());
            }
        }
        return lista;
    }
    
    
    /**
    * 
    * @param _empresaId
    * @param _runEmpleado
    * @param _anio
    * @param _cencoId
    * 
    * @return 
    */
    public int getResumenPermisosAdministrativosCount(String _empresaId, 
            String _runEmpleado,
            int _anio,
            int _cencoId){
        int count=0;
        Statement statement = null;
        ResultSet rs = null;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[PermisosAdministrativosDAO.getResumenPermisosAdministrativosCount]");
            statement = dbConn.createStatement();
             
            String strSql = "select "
                    + "count(run_empleado) numrows "
                + "from permiso_administrativo pa "
                    + "inner join view_empleado empleado "
                        + "on (empleado.empresa_id = pa.empresa_id and empleado.rut = pa.run_empleado) "
                + "where anio = " + _anio + " "; 
            
            if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
                strSql += " and pa.empresa_id = '" + _empresaId + "' ";
            }
            
            if (_runEmpleado != null && _runEmpleado.compareTo("-1") != 0){
                strSql += " and pa.run_empleado = '" + _runEmpleado + "' ";
            }
            
            if (_cencoId != -1){
                strSql += " and empleado.cenco_id = " + _cencoId;
            }
            
            rs = statement.executeQuery(strSql);		
            if (rs.next()) {
                count=rs.getInt("numrows");
            }
            
            statement.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        } catch (SQLException|DatabaseException e) {
            System.err.println("[PermisosAdministrativosDAO."
                + "getResumenPermisosAdministrativosCount]"
                + "Error_1: " + e.toString());    
            e.printStackTrace();
        }finally{
            try {
                if (statement != null) statement.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[PermisosAdministrativosDAO."
                    + "getResumenPermisosAdministrativosCount]"
                    + "Error_2: " + ex.toString());
            }
        }
        return count;
    }
    
    /**
    * Retorna lista con ausencias del tipo 'Permiso administrativo'
    * 
    * @param _empresaId
    * @param _runEmpleado
    * @param _cencoId
    * @param _anio
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * 
    * @return 
    */
    public List<DetalleAusenciaVO> getDetallePermisosAdministrativos(String _empresaId, 
            String _runEmpleado,
            int _cencoId,
            int _anio,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<DetalleAusenciaVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        DetalleAusenciaVO data;
        
        try{
            String sql = "select correlativo,"
                + "rut_empleado,"
                + "empleado.empresa_id,"
                + "coalesce(empleado.empl_nombres, '') || ' ' || coalesce(empleado.empl_ape_paterno, '') nombre_empleado,"
                + "fecha_ingreso fecha_ingreso_pa,"
                + "fecha_inicio,"
                + "fecha_fin,ausencia_id,"
                + "dias_solicitados,"
                + "saldo_post_pa,"
                + "rut_autoriza_ausencia "
                + "from detalle_ausencia "
                + "inner join empleado on detalle_ausencia.rut_empleado = empleado.empl_rut "
                + "where ausencia_id = " + Constantes.ID_AUSENCIA_PERMISO_ADMINISTRATIVO
                + "and to_char(fecha_inicio,'yyyy')::integer = " + _anio;
                    
            if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
                sql += " and empleado.empresa_id = '" + _empresaId + "' ";
            }
            
            if (_runEmpleado != null && _runEmpleado.compareTo("-1") != 0){
                sql += " and detalle_ausencia.rut_empleado = '" + _runEmpleado + "' ";
            }
            
            if (_cencoId != -1){
                sql += " and empleado.cenco_id = " + _cencoId;
            }
           
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            System.out.println("[PermisosAdministrativosDAO."
                + "getDetallePermisosAdministrativos]sql: " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[PermisosAdministrativosDAO.getDetallePermisosAdministrativos]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new DetalleAusenciaVO();
                
                data.setCorrelativo(rs.getInt("correlativo"));
                data.setRutEmpleado(rs.getString("rut_empleado"));
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setNombreEmpleado(rs.getString("nombre_empleado"));
                data.setFechaIngresoAsStr(rs.getString("fecha_ingreso_pa"));
                data.setFechaInicioAsStr(rs.getString("fecha_inicio"));
                data.setFechaFinAsStr(rs.getString("fecha_fin"));
                data.setIdAusencia(rs.getInt("ausencia_id"));
                data.setDiasSolicitados(rs.getDouble("dias_solicitados"));
                data.setSaldoPostPA(rs.getDouble("saldo_post_pa"));
                data.setRutAutorizador(rs.getString("rut_autoriza_ausencia"));
                
                lista.add(data);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[PermisosAdministrativosDAO."
                + "getDetallePermisosAdministrativos]"
                + "Error_1: " + sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[PermisosAdministrativosDAO."
                    + "getDetallePermisosAdministrativos]"
                    + "Error_2: " + ex.toString());
            }
        }
        return lista;
    }
    
    /**
    *  Obtiene numero de dias efectivos de permiso administrativo 
    *  para un rango de fechas determinado.Solo se cuentan los dias de lunes a viernes y dias NO FERIADOS 
    *
    * @param _inicioPermisoAdministrativo  
    * @param _finPermisoAdministrativo  
    * @param _empresaId
    * @param _runEmpleado
    * 
    * @return int 
    */
    public int getDiasEfectivos(String _inicioPermisoAdministrativo, 
            String _finPermisoAdministrativo, 
            String _empresaId,
            String _runEmpleado){
        String fechaInicioPA;
        String fechaFinPA;
        int diasEfectivos = 0;
        
        try{
            Date date1 = m_sdf.parse(_inicioPermisoAdministrativo);
            Date date2 = m_sdf.parse(_finPermisoAdministrativo);
            fechaInicioPA = _inicioPermisoAdministrativo;
            fechaFinPA = _finPermisoAdministrativo;
        }catch(ParseException pex){
            fechaInicioPA = Utilidades.getFechaYYYYmmdd(_inicioPermisoAdministrativo);
            fechaFinPA = Utilidades.getFechaYYYYmmdd(_finPermisoAdministrativo);
        }
        System.out.println("[PermisosAdministrativosDAO."
            + "getDiasEfectivos]"
            + "Iterar fechas en el rango. "
            + "Inicio: " + fechaInicioPA
            + ", Fin: " + fechaFinPA);
        //fechas existentes en el rango seleccionado
        String[] fechas;
        try{
            fechas = Utilidades.getFechas(fechaInicioPA, fechaFinPA);
        }catch(IllegalArgumentException iex){
            System.err.println("[PermisosAdministrativosDAO."
                + "getDiasEfectivos]Usar fechas sin formatear...");
            fechas = Utilidades.getFechas(_inicioPermisoAdministrativo, _finPermisoAdministrativo);
        }
        /**
        * Inicio 20210725-001
        * Carga en memoria la info de la tabla calendario_feriado segun rango de fechas
        * seleccionado. En cada fecha se tiene la info de si es feriado o no.
        * De ser feriado, se indica que feriado es y su tipo.
        * 
        */
        CalendarioFeriadoBp bpFeriados    = new CalendarioFeriadoBp(new PropertiesVO());
        LinkedHashMap<String, InfoFeriadoVO> fechasCalendarioFeriados = bpFeriados.getFechas(_empresaId, 
            _runEmpleado, 
            fechaInicioPA, fechaFinPA);
        //aca son dias efectivos totales
        for (String itfecha : fechas) {
            System.out.println("[PermisosAdministrativosDAO."
                + "getDiasEfectivos]"
                + "Itera fecha= " + itfecha);
            LocalDate localdate = Utilidades.getLocalDate(itfecha);
            int diaSemana = localdate.getDayOfWeek().getValue();
            if (diaSemana >= 1 && diaSemana <= 5){
                System.out.println("[PermisosAdministrativosDAO.getDiasEfectivos]Es dia de semana (Lu-Vi)");
                String strKey = _empresaId + "|" + _runEmpleado + "|" + itfecha;
                InfoFeriadoVO infoFeriado = fechasCalendarioFeriados.get(strKey);
                boolean esFeriado = infoFeriado.isFeriado();
                System.out.println("[PermisosAdministrativosDAO.getDiasEfectivos]"
                    + "Fecha " + itfecha + ", es feriado? " + esFeriado);
                if (!esFeriado) {
                    diasEfectivos++;
                }else{
                    System.out.println("[PermisosAdministrativosDAO.getDiasEfectivos]-1-"
                        + "Fecha " + itfecha + ", no contabilizar dias efectivos");
                }
            }else{
                System.out.println("[PermisosAdministrativosDAO.getDiasEfectivos]-2-"
                    + "Fecha " + itfecha + ", no contabilizar dias efectivos (no es dia de semana)");
            }
            
        }//fin iteracion de fechas

        return diasEfectivos;    
    }
    
    
    
    /**
    * Retorna total de dias solicitados por concepto de 'Permiso administrativo'
    * 
    * @param _empresaId
    * @param _runEmpleado
    * @param _anio
    * 
    * @return 
    */
    public double getDiasSolicitados(String _empresaId, 
            String _runEmpleado,
            int _anio){
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        double diasSolicitados = 0;
        
        try{
            String sql = "select sum(dias_solicitados) dias_solicitados "
                + " from detalle_ausencia "
                + " inner join empleado on detalle_ausencia.rut_empleado = empleado.empl_rut "
                + " where rut_empleado = '" + _runEmpleado + "' "
                + " and empleado.empresa_id = '" + _empresaId + "' "
                + " and ausencia_id = " + Constantes.ID_AUSENCIA_PERMISO_ADMINISTRATIVO
                + " and to_char(fecha_inicio,'yyyy')::integer = " + _anio
                + " group by empleado.empresa_id,detalle_ausencia.rut_empleado";
            
            System.out.println("[PermisosAdministrativosDAO."
                + "getDiasSolicitados]sql: " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[PermisosAdministrativosDAO.getDiasSolicitados]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                diasSolicitados = rs.getDouble("dias_solicitados");
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[PermisosAdministrativosDAO."
                + "getDiasSolicitados]"
                + "Error_1: " + sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[PermisosAdministrativosDAO."
                    + "getDiasSolicitados]"
                    + "Error_2: " + ex.toString());
            }
        }
        return diasSolicitados;
    }
    
    
    /**
    * Retorna total de dias disponibles por concepto de 'Permiso administrativo'
    *  para el anio especificado.
    * 
    * @param _empresaId
    * @param _runEmpleado
    * @param _anio
    * 
    * @return 
    */
    public double getDiasDisponibles(String _empresaId, 
            String _runEmpleado,
            int _anio){
        double diasDisponibles = 0;
        try{
            double diasSolicitados  = this.getDiasSolicitados(_empresaId, _runEmpleado, _anio);
            System.err.println("[PermisosAdministrativosDAO."
                + "getDiasDisponibles]"
                + "empresaId: " + _empresaId
                + ", runEmpleado: " + _runEmpleado    
                + ", anio: " + _anio        
                + ", dias solicitados: " + diasSolicitados);
            diasDisponibles = systemProperties.getParametrosSistema().get("maximo_anual_dias_pa") - diasSolicitados;
            
        }catch(Exception ex){
            System.err.println("[PermisosAdministrativosDAO."
                + "getDiasDisponibles]"
                + "Error_1: " + ex.toString());
        }
        return diasDisponibles;
    }
    
    /**
    * 
    * @param _empresaId
    * @param _runEmpleado
    * @param _cencoId
    * @param _anio
    * 
    * @return 
    */
    public int getDetallePermisosAdministrativosCount(String _empresaId, 
            String _runEmpleado,
            int _cencoId,
            int _anio){
        int count=0;
        Statement statement = null;
        ResultSet rs = null;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[PermisosAdministrativosDAO.getDetallePermisosAdministrativosCount]");
            statement = dbConn.createStatement();
             
            String strSql = "select count(correlativo) numrows "
                + "from detalle_ausencia "
                + "inner join empleado on detalle_ausencia.rut_empleado = empleado.empl_rut "
                + "where ausencia_id = " + Constantes.ID_AUSENCIA_PERMISO_ADMINISTRATIVO
                + "and to_char(fecha_inicio,'yyyy')::integer = " + _anio;
                        
            if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
                strSql += " and empleado.empresa_id = '" + _empresaId + "' ";
            }
            
            if (_runEmpleado != null && _runEmpleado.compareTo("-1") != 0){
                strSql += " and detalle_ausencia.rut_empleado = '" + _runEmpleado + "' ";
            }
            
            if (_cencoId != -1){
                strSql += " and empleado.cenco_id = " + _cencoId;
            }
            
            rs = statement.executeQuery(strSql);		
            if (rs.next()) {
                count=rs.getInt("numrows");
            }
            
            statement.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        } catch (SQLException|DatabaseException e) {
            System.err.println("[PermisosAdministrativosDAO."
                + "getDetallePermisosAdministrativosCount]"
                + "Error_1: " + e.toString());    
            e.printStackTrace();
        }finally{
            try {
                if (statement != null) statement.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[PermisosAdministrativosDAO."
                    + "getDetallePermisosAdministrativosCount]"
                    + "Error_2: " + ex.toString());
            }
        }
        return count;
    }
    
//    public void openDbConnection(){
//        try {
//            m_usedGlobalDbConnection = true;
//            dbConn = dbLocator.getConnection(m_dbpoolName,"[UsersDAO.openDbConnection]");
//        } catch (DatabaseException ex) {
//            System.err.println("[UsersDAO.openDbConnection]"
//                + "Error: " + ex.toString());
//        }
//    }
//    
//    public void closeDbConnection(){
//        try {
//            m_usedGlobalDbConnection = false;
//            dbLocator.freeConnection(dbConn);
//        } catch (Exception ex) {
//            System.err.println("[UsersDAO.closeDbConnection]"
//                + "Error: "+ex.toString());
//        }
//    }
}
