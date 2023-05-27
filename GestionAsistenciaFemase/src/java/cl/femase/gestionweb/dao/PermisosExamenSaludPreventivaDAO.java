/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.DetalleAusenciaVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.PermisoExamenSaludPreventivaVO;
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
* Clase para acceder a la tabla 'permiso_examen_salud_preventiva'
* 
* @author Alexander
*/
public class PermisosExamenSaludPreventivaDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    public boolean m_usedGlobalDbConnection = false;
    private PropertiesVO systemProperties;
    /**
    *   Fecha en formato yyyy-MM-dd 
    */
    private final SimpleDateFormat m_sdf = new SimpleDateFormat("yyyy-MM-dd");

    /**
    *
    * @param _propsValues
    */
    public PermisosExamenSaludPreventivaDAO(PropertiesVO _propsValues) {
        systemProperties = _propsValues;
    }

    /**
    * Actualiza dias disponibles y dias_utilizados
    * 
    * @param _data
    * @return 
    */
    public ResultCRUDVO updateResumenPESP(PermisoExamenSaludPreventivaVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        PreparedStatement psupdate = null;
        int result=0;
        int diasDisponibles = _data.getDiasDisponibles();
        int diasUtilizados = _data.getDiasUtilizados();
        
        String msgError = "Error al actualizar "
            + "resumen Permiso Examen Salud Preventiva, "
            + "EmpresaId: " + _data.getEmpresaId()
            + ", rutEmpleado: " + _data.getRunEmpleado()    
            + ", anio: " + _data.getAnio()
            + ", dias_disponibles: " + diasDisponibles
            + ", dias_utilizados: " + diasUtilizados;
        
        try{
            String msgFinal = " Actualiza resumen Permiso Examen Salud Preventiva:"
                + "EmpresaId [" + _data.getEmpresaId() + "]" 
                + ", runEmpleado [" + _data.getRunEmpleado() + "]"    
                + ", anio [" + _data.getAnio() + "]"
                + ", dias_disponibles [" + diasDisponibles + "]"
                + ", dias_utilizados [" + diasUtilizados + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            String sql = "UPDATE permiso_examen_salud_preventiva "
                + "SET "
                    + "dias_disponibles = ?, "
                    + "dias_utilizados = ?, "
                    + "last_update = current_timestamp "
                + "WHERE empresa_id = ? "
                    + "and run_empleado = ? "
                    + "and anio = ?";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[PermisosExamenSaludPreventivaDAO.updateResumenPESP]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setDouble(1,  diasDisponibles);
            psupdate.setDouble(2,  diasUtilizados);
            
            //filtro update            
            psupdate.setString(3, _data.getEmpresaId());
            psupdate.setString(4, _data.getRunEmpleado());
            psupdate.setInt(5, _data.getAnio());
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println(WEB_NAME+"[PermisosExamenSaludPreventivaDAO.updateResumenPESP]"
                   + ", empresaId:" + _data.getEmpresaId()
                    + ", runEmpleado:" + _data.getRunEmpleado()    
                    + ", dias_disponibles:" + diasDisponibles
                    + ", dias_utilizados:" + diasUtilizados
                    + " actualizado OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[PermisosExamenSaludPreventivaDAO.updateResumenPESP]"
                + "update Error: " + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (psupdate!=null) psupdate.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[PermisosExamenSaludPreventivaDAO.updateResumenPESP]Error: "+ex.toString());
            }
        }

        return objresultado;
    }
    
    /**
    * Actualiza dias disponibles y dias_utilizados del semestre especificado
    * 
    * @param _empresaId
    * @param _anio
     * @param _maximoDiasAnio
    * @return 
    */
    public ResultCRUDVO updateDiasExamenSaludPreventivaAnio(String _empresaId, 
            int _anio, int _maximoDiasAnio){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "resumen Permiso Examen Salud Preventiva, "
            + "EmpresaId: " + _empresaId
            + ", anio: " + _anio;
        
        try{
            String msgFinal = " Actualiza resumen Permiso Examen Salud Preventiva:"
                + "EmpresaId [" + _empresaId + "]" 
                + ", anio [" + _anio + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE permiso_examen_salud_preventiva "
                + "SET "
                    + "dias_disponibles = 0, "
                    + "dias_disponibles = ?, "
                    + "dias_utilizados = 0, "
                    + "last_update = current_timestamp "
                + "WHERE empresa_id = ? "
                    + "and anio = ?";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[PermisosExamenSaludPreventivaDAO.updateDiasExamenSaludPreventivaAnio]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setInt(1,  _maximoDiasAnio);
            
            //filtro update            
            psupdate.setString(2, _empresaId);
            psupdate.setInt(3, _anio);
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println(WEB_NAME+"[PermisosExamenSaludPreventivaDAO.updateDiasExamenSaludPreventivaAnio]"
                   + ", empresaId:" + _empresaId
                    + ", anio:" + _anio 
                    + " actualizado OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[PermisosExamenSaludPreventivaDAO.updateDiasExamenSaludPreventivaAnio]"
                + "update Error: " + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (psupdate!=null) psupdate.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[PermisosExamenSaludPreventivaDAO.updateDiasExamenSaludPreventivaAnio]Error: "+ex.toString());
            }
        }

        return objresultado;
    }

    /**
    * Reinicia el número de días de 'permisos examen salud preventiva' de un anio (disponibles y utilizados)
    * Para todos los empleados de la empresa indicada
    * Esto es: 
    *   Insertar un nuevo registro en la tabla 'permiso_examen_salud_preventiva' 
    *   
    * @param _empresaId
     * @param _maximoDiasAnio
    * @param _currentYear
    * 
    * @return 
    */
    public ResultCRUDVO resetearDiasExamenSaludPreventivaAnio(String _empresaId, 
            int _maximoDiasAnio,
            int _currentYear){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        int result=0;
        String msgError = "Error al retetear "
            + "Resumen Permiso Examen Salud Preventiva para el "
            + "anio: " + _currentYear
            + ", empresaId: " + _empresaId
            + ", maximo dias PESP Anio: " + _maximoDiasAnio;
        
        String msgFinal = " Inserta Resumen Permiso Examen Salud Preventiva:"
            + " Anio [" + _currentYear 
            + ", empresaId [" + _empresaId 
            + ", maximo dias Anio [" + _maximoDiasAnio + "]";
       
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
       
        try{
            String sql = "INSERT INTO permiso_examen_salud_preventiva("
                + "empresa_id, "
                + "run_empleado, "
                + "anio, "
                + "dias_disponibles, "
                + "dias_utilizados, "
                + "last_update) "
                + "select empresa_id, empl_rut, "
                    + _currentYear + "," + _maximoDiasAnio + ",0,current_timestamp "
                    + "from empleado "
                    + "where empleado.empresa_id= '" + _empresaId + "' "
                        + "and empl_estado = 1 "
                        + "and empl_fec_fin_contrato >= current_date";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[PermisosExamenSaludPreventivaDAO.resetearDiasExamenSaludPreventivaAnio]");
            insert = dbConn.prepareStatement(sql);
                        
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas > 0){
                System.out.println(WEB_NAME+"[PermisosExamenSaludPreventivaDAO.resetearDiasExamenSaludPreventivaAnio]"
                    + "Reseteo de Resumen PESP OK, Anio: " + _currentYear+", filas afectadas= " + filasAfectadas);
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[PermisosExamenSaludPreventivaDAO.resetearDiasExamenSaludPreventivaAnio]"
                + "Error1: " + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (insert != null) insert.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[PermisosExamenSaludPreventivaDAO.resetearDiasExamenSaludPreventivaAnio]"
                    + "Error: " + ex.toString());
            }
        }

        return objresultado;
    }
    
    /**
    * Reinicia el número de días de 'permisos examen salud preventiva' de un semestre (disponibles y utilizados)
    * 
    * Para todos los empleados de la empresa indicada
    * Esto es: 
    *   Insertar un nuevo registro en la tabla 'permiso_examen_salud_preventiva' 
    *   
    * @param _empresaId
    * @param _runEmpleado
     * @param _maximoDiasAnio
    * @param _currentYear
    * 
    * @return 
    */
    public ResultCRUDVO insertaRegistroPermisoExamenSaludPreventiva(String _empresaId, 
            String _runEmpleado,
            int _maximoDiasAnio,
            int _currentYear){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        int result=0;
        String msgError = "Error al insertar "
            + "Resumen Permiso Examen Salud Preventiva para: "
            + "EmpresaId: " + _empresaId
            + ", runEmpleado: " + _runEmpleado
            + ", anio: " + _currentYear 
            + ", maximo dias PESP: " + _maximoDiasAnio;
        
        String msgFinal = " Inserta Resumen Permiso Examen Salud Preventiva:"
            + " EmpresaId [" + _empresaId + "]"
            + ", run empleado [" + _runEmpleado + "]"    
            + ", anio [" + _currentYear + "]"
            + ", maximo dias anio [" + _maximoDiasAnio + "]";
       
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
       
        try{
            String sql = "INSERT INTO permiso_examen_salud_preventiva("
                + "empresa_id, "
                + "run_empleado, "
                + "anio, "
                + "dias_disponibles, "
                + "dias_utilizados, "
                + "last_update) values ('" + _empresaId 
                    + "','" + _runEmpleado 
                    + "'," + _currentYear 
                    + "," + _maximoDiasAnio 
                    + ",0,current_timestamp)";

            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[PermisosExamenSaludPreventivaDAO.insertaRegistroPermisoExamenSaludPreventiva]");
            insert = dbConn.prepareStatement(sql);
                        
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas > 0){
                System.out.println(WEB_NAME+"[PermisosExamenSaludPreventivaDAO."
                    + "insertaRegistroPermisoExamenSaludPreventiva]"
                    + "Reseteo de Resumen PESP OK, Anio: " + _currentYear+", filas afectadas= " + filasAfectadas);
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[PermisosExamenSaludPreventivaDAO."
                + "insertaRegistroPermisoExamenSaludPreventiva]"
                + "Error1: " + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (insert != null) insert.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[PermisosExamenSaludPreventivaDAO."
                    + "insertaRegistroPermisoExamenSaludPreventiva]"
                    + "Error: " + ex.toString());
            }
        }

        return objresultado;
    }

    /**
    * Elimina info de permiso examen salud preventiva
    * 
    * @param _data
    * @return 
    */
    public ResultCRUDVO deleteResumenPESP(PermisoExamenSaludPreventivaVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        int result=0;
        String msgError = "Error al eliminar "
            + "resumen permiso Examen Salud Preventiva, "
            + "empresaId: " + _data.getEmpresaId()
            + ", runEmpleado: " + _data.getRunEmpleado()
            + ", anio: " + _data.getAnio();
        
       String msgFinal = " Elimina resumen permiso Examen Salud Preventiva:"
            + "empresaId [" + _data.getEmpresaId() + "]" 
            + ", rutEmpleado [" + _data.getRunEmpleado() + "]"
            + ", anio [" + _data.getAnio() + "]";
        objresultado.setMsg(msgFinal);
        PreparedStatement psdelete    = null;
        
        try{
            String sql = "delete from permiso_examen_salud_preventiva " +
                "WHERE empresa_id = ? " +
                    "and run_empleado = ? "
                    + "and anio = ?";

            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[PermisosExamenSaludPreventivaDAO.deleteResumenPESP]");
            psdelete = dbConn.prepareStatement(sql);
            psdelete.setString(1,  _data.getEmpresaId());
            psdelete.setString(2,  _data.getRunEmpleado());
            psdelete.setInt(3,  _data.getAnio());
                        
            int filasAfectadas = psdelete.executeUpdate();
            System.out.println(WEB_NAME+"[PermisosExamenSaludPreventivaDAO.deleteResumenPESP]"
                + "filasAfectadas: " + filasAfectadas);
            if (filasAfectadas == 1){
                System.out.println(WEB_NAME+"[PermisosExamenSaludPreventivaDAO.deleteResumenPESP]"
                    + "delete resumen permiso_examen_salud_preventiva]"
                    + ", empresaId:" +_data.getEmpresaId()
                    + ", runEmpleado:" + _data.getRunEmpleado()
                    + ", anio:" + _data.getAnio()    
                    + " eliminado OK!");
            }
            
            psdelete.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[PermisosExamenSaludPreventivaDAO.deleteResumenPESP]"
                + "Error: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (psdelete != null) psdelete.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[PermisosExamenSaludPreventivaDAO.deleteResumenPESP]"
                    + "Error: " + ex.toString());
            }
        }

        return objresultado;
    }
    
    /**
    * Elimina info de permiso examen salud preventiva para el anio indicado
    * 
    * @param _empresaId
    * @param _anio
    * @return 
    */
    public boolean deleteResumenPESPAnio(String _empresaId, 
            int _anio){
        int result=0;
        PreparedStatement psdelete    = null;
        boolean isOk=true;
        try{
            String sql = "delete from permiso_examen_salud_preventiva " +
                "WHERE empresa_id = ? " +
                    "and anio = ?";

            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[PermisosExamenSaludPreventivaDAO.deleteResumenPESP]");
            psdelete = dbConn.prepareStatement(sql);
            psdelete.setString(1,  _empresaId);
            psdelete.setInt(2,  _anio);
                        
            int filasAfectadas = psdelete.executeUpdate();
            System.out.println(WEB_NAME+"[PermisosExamenSaludPreventivaDAO.deleteResumenPESPAnio]"
                + "filasAfectadas: " + filasAfectadas);
            if (filasAfectadas == 1){
                System.out.println(WEB_NAME+"[PermisosExamenSaludPreventivaDAO.deleteResumenPESPAnio]"
                    + "delete resumen permiso_examen_salud_preventiva]"
                    + ", empresaId:" + _empresaId
                    + ", anio:" + _anio   
                    + " registros eliminados sin error");
            }
            
            psdelete.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[PermisosExamenSaludPreventivaDAO.deleteResumenPESPAnio]"
                + "Error: "+sqle.toString());
            isOk = false;
       }finally{
            try {
                if (psdelete != null) psdelete.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[PermisosExamenSaludPreventivaDAO.deleteResumenPESPAnio]"
                    + "Error: " + ex.toString());
            }
        }

        return isOk;
    }
    
    /**
    * Retorna lista con Permisos examen salud preventiva
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
    public List<PermisoExamenSaludPreventivaVO> getResumenPermisosExamenSaludPreventiva(String _empresaId, 
            String _runEmpleado,
            int _anio,
            int _cencoId,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<PermisoExamenSaludPreventivaVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        PermisoExamenSaludPreventivaVO data;
        
        try{
            String sql = "select "
                    + "pesp.empresa_id,"
                    + "empleado.depto_id,"
                    + "empleado.depto_nombre,"
                    + "empleado.cenco_id,"
                    + "empleado.ccosto_nombre,"
                    + "pesp.run_empleado,"
                    + "pesp.anio,"
                    + "empleado.nombre || ' ' || empleado.materno nombre_empleado,"
                    + "pesp.dias_disponibles,"
                    + "pesp.dias_utilizados,"
                    + "to_char(pesp.last_update,'yyyy-MM-dd HH24:MI:SS') last_update "
                + "from permiso_examen_salud_preventiva pesp "
                    + "inner join view_empleado empleado "
                        + "on (empleado.empresa_id = pesp.empresa_id and empleado.rut = pesp.run_empleado) "
                + "where anio = " + _anio + " ";
           
            if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
                sql += " and pesp.empresa_id = '" + _empresaId + "' ";
            }
            
            if (_runEmpleado != null && _runEmpleado.compareTo("-1") != 0){
                sql += " and pesp.run_empleado = '" + _runEmpleado + "' ";
            }
            
            if (_cencoId != -1){
                sql += " and empleado.cenco_id = " + _cencoId;
            }
           
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            System.out.println(WEB_NAME+"[PermisosExamenSaludPreventivaDAO."
                + "getResumenPermisosExamenSaludPreventiva]sql: " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[PermisosExamenSaludPreventivaDAO.getResumenPermisosExamenSaludPreventiva]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new PermisoExamenSaludPreventivaVO();
                
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setDeptoId(rs.getString("depto_id"));
                data.setDeptoNombre(rs.getString("depto_nombre"));
                data.setCencoId(rs.getInt("cenco_id"));
                data.setCencoNombre(rs.getString("ccosto_nombre"));
                data.setRunEmpleado(rs.getString("run_empleado"));
                data.setNombreEmpleado(rs.getString("nombre_empleado"));
                data.setAnio(rs.getInt("anio"));
                
                data.setDiasDisponibles(rs.getInt("dias_disponibles"));
                data.setDiasUtilizados(rs.getInt("dias_utilizados"));
                data.setLastUpdate(rs.getString("last_update"));
                
                data.setRowKey(data.getEmpresaId() + "|" + data.getRunEmpleado());
                lista.add(data);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[PermisosExamenSaludPreventivaDAO."
                + "getResumenPermisosExamenSaludPreventiva]"
                + "Error_1: " + sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[PermisosExamenSaludPreventivaDAO."
                    + "getResumenPermisosExamenSaludPreventiva]"
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
    public int getResumenPermisosExamenSaludPreventivaCount(String _empresaId, 
            String _runEmpleado,
            int _anio,
            int _cencoId){
        int count=0;
        Statement statement = null;
        ResultSet rs = null;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[PermisosExamenSaludPreventivaDAO.getResumenPermisosExamenSaludPreventivaCount]");
            statement = dbConn.createStatement();
             
            String strSql = "select "
                    + "count(run_empleado) numrows "
                + "from permiso_examen_salud_preventiva pesp "
                    + "inner join view_empleado empleado "
                        + "on (empleado.empresa_id = pesp.empresa_id and empleado.rut = pesp.run_empleado) "
                + "where anio = " + _anio + " "; 
            
            if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
                strSql += " and pesp.empresa_id = '" + _empresaId + "' ";
            }
            
            if (_runEmpleado != null && _runEmpleado.compareTo("-1") != 0){
                strSql += " and pesp.run_empleado = '" + _runEmpleado + "' ";
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
            System.err.println("[PermisosExamenSaludPreventivaDAO."
                + "getResumenPermisosExamenSaludPreventivaCount]"
                + "Error_1: " + e.toString());    
            e.printStackTrace();
        }finally{
            try {
                if (statement != null) statement.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[PermisosExamenSaludPreventivaDAO."
                    + "getResumenPermisosExamenSaludPreventivaCount]"
                    + "Error_2: " + ex.toString());
            }
        }
        return count;
    }
    
    /**
    * Retorna lista con ausencias del tipo 'Permiso examen salud preventiva'
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
    public List<DetalleAusenciaVO> getDetallePermisosExamenSaludPreventiva(String _empresaId, 
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
                + "where ausencia_id = " + Constantes.ID_AUSENCIA_PERMISO_EXAMEN_SALUD_PREVENTIVA
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
            
            System.out.println(WEB_NAME+"[PermisosExamenSaludPreventivaDAO."
                + "getDetallePermisosExamenSaludPreventiva]sql: " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[PermisosExamenSaludPreventivaDAO.getDetallePermisosExamenSaludPreventiva]");
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
                data.setSaldoPostPESP(rs.getInt("saldo_post_pesp"));
                data.setRutAutorizador(rs.getString("rut_autoriza_ausencia"));
                
                lista.add(data);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[PermisosExamenSaludPreventivaDAO."
                + "getDetallePermisosExamenSaludPreventiva]"
                + "Error_1: " + sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[PermisosExamenSaludPreventivaDAO."
                    + "getDetallePermisosExamenSaludPreventiva]"
                    + "Error_2: " + ex.toString());
            }
        }
        return lista;
    }
    
    /**
    * Retorna total de dias solicitados por concepto de 'Permiso examen salud preventiva'
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
                + " and ausencia_id = " + Constantes.ID_AUSENCIA_PERMISO_EXAMEN_SALUD_PREVENTIVA
                + " and to_char(fecha_inicio,'yyyy')::integer = " + _anio
                + " group by empleado.empresa_id,detalle_ausencia.rut_empleado";
            
            System.out.println(WEB_NAME+"[PermisosExamenSaludPreventivaDAO."
                + "getDiasSolicitados]sql: " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[PermisosExamenSaludPreventivaDAO.getDiasSolicitados]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                diasSolicitados = rs.getDouble("dias_solicitados");
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[PermisosExamenSaludPreventivaDAO."
                + "getDiasSolicitados]"
                + "Error_1: " + sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[PermisosExamenSaludPreventivaDAO."
                    + "getDiasSolicitados]"
                    + "Error_2: " + ex.toString());
            }
        }
        return diasSolicitados;
    }
    
    
    /**
    * Retorna total de dias disponibles por concepto de 'Permiso examen salud preventiva'
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
            System.err.println("[PermisosExamenSaludPreventivaDAO."
                + "getDiasDisponibles]"
                + "empresaId: " + _empresaId
                + ", runEmpleado: " + _runEmpleado    
                + ", anio: " + _anio        
                + ", dias solicitados: " + diasSolicitados);
            diasDisponibles = systemProperties.getParametrosSistema().get("maximo_anual_dias_pa") - diasSolicitados;
            
        }catch(Exception ex){
            System.err.println("[PermisosExamenSaludPreventivaDAO."
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
    public int getDetallePermisosExamenSaludPreventivaCount(String _empresaId, 
            String _runEmpleado,
            int _cencoId,
            int _anio){
        int count=0;
        Statement statement = null;
        ResultSet rs = null;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[PermisosExamenSaludPreventivaDAO.getDetallePermisosExamenSaludPreventivaCount]");
            statement = dbConn.createStatement();
             
            String strSql = "select count(correlativo) numrows "
                + "from detalle_ausencia "
                + "inner join empleado on detalle_ausencia.rut_empleado = empleado.empl_rut "
                + "where ausencia_id = " + Constantes.ID_AUSENCIA_PERMISO_EXAMEN_SALUD_PREVENTIVA
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
            System.err.println("[PermisosExamenSaludPreventivaDAO."
                + "getDetallePermisosExamenSaludPreventivaCount]"
                + "Error_1: " + e.toString());    
            e.printStackTrace();
        }finally{
            try {
                if (statement != null) statement.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[PermisosExamenSaludPreventivaDAO."
                    + "getDetallePermisosExamenSaludPreventivaCount]"
                    + "Error_2: " + ex.toString());
            }
        }
        return count;
    }

}
