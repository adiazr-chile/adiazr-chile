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
import cl.femase.gestionweb.vo.FilasAfectadasJsonVO;
import cl.femase.gestionweb.vo.InfoFeriadoVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.PermisoAdministrativoVO;
import com.google.gson.Gson;
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
    public ResultCRUDVO updateResumenPA(PermisoAdministrativoVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        PreparedStatement psupdate = null;
        int result=0;
        Date currentDate = new Date();
        int semestreActual = Utilidades.getSemestre(currentDate);
        double diasDisponiblesSemestre = _data.getDiasDisponiblesSemestre1();
        double diasUtilizadosSemestre = _data.getDiasUtilizadosSemestre1();
        if (semestreActual == 2){
            diasDisponiblesSemestre = _data.getDiasDisponiblesSemestre2();
            diasUtilizadosSemestre = _data.getDiasUtilizadosSemestre2();
        }
        String msgError = "Error al actualizar "
            + "resumen Permiso Administrativo (nueva funcion), "
            + "EmpresaId: " + _data.getEmpresaId()
            + ", rutEmpleado: " + _data.getRunEmpleado()    
            + ", anio: " + _data.getAnio()
            + ", dias_disponibles semestre (" + semestreActual + " ): " + diasDisponiblesSemestre
            + ", dias_utilizados (" + semestreActual + " ): " + diasUtilizadosSemestre;
        
        try{
            String msgFinal = " Actualiza resumen Permiso Administrativo:"
                + "EmpresaId [" + _data.getEmpresaId() + "]" 
                + ", runEmpleado [" + _data.getRunEmpleado() + "]"    
                + ", anio [" + _data.getAnio() + "]"
                + ", dias_disponibles (" + semestreActual + " )[" + diasDisponiblesSemestre + "]"
                + ", dias_utilizados (" + semestreActual + " )[" + diasUtilizadosSemestre + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            String sql = "UPDATE permiso_administrativo "
                + "SET "
                    + "dias_disponibles_semestre" + semestreActual + "= ?, "
                    + "dias_utilizados_semestre" + semestreActual + "= ?, "
                    + "last_update = current_timestamp "
                + "WHERE empresa_id = ? "
                    + "and run_empleado = ? "
                    + "and anio = ?";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[PermisosAdministrativosDAO.updateResumenPA]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setDouble(1,  diasDisponiblesSemestre);
            psupdate.setDouble(2,  diasUtilizadosSemestre);
            
            //filtro update            
            psupdate.setString(3, _data.getEmpresaId());
            psupdate.setString(4, _data.getRunEmpleado());
            psupdate.setInt(5, _data.getAnio());
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println(WEB_NAME+"[PermisosAdministrativosDAO.updateResumenPA]"
                   + ", empresaId:" + _data.getEmpresaId()
                    + ", runEmpleado:" + _data.getRunEmpleado()    
                    + ", dias_disponibles:" + diasDisponiblesSemestre
                    + ", dias_utilizados:" + diasUtilizadosSemestre
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
    * Actualiza dias disponibles y dias_utilizados del semestre especificado
    * 
    * @param _empresaId
    * @param _anio
    * @param _maximoDiasSemestre
    * @param _semestre
    * @return 
    */
    public ResultCRUDVO updateDiasAdministrativosSemestre(String _empresaId, 
            int _anio, 
            int _semestre,
            int _maximoDiasSemestre){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "resumen Permiso Administrativo, "
            + "EmpresaId: " + _empresaId
            + ", anio: " + _anio
            + ", semestre: " + _semestre;
        int semestrePrevio = 1;
        if (_semestre == 1){
            semestrePrevio = 2;
        }
        
        try{
            String msgFinal = " Actualiza resumen Permiso Administrativo:"
                + "EmpresaId [" + _empresaId + "]" 
                + ", anio [" + _anio + "]"
                + ", semestre [" + _semestre + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE permiso_administrativo "
                + "SET "
                    + "dias_disponibles_semestre" + semestrePrevio + " = 0, "
                    + "dias_disponibles_semestre" + _semestre + " = ?, "
                    + "dias_utilizados_semestre" + _semestre + " = 0, "
                    + "last_update = current_timestamp "
                + "WHERE empresa_id = ? "
                    + "and anio = ?";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[PermisosAdministrativosDAO.updateDiasAdministrativosSemestre]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setDouble(1,  _maximoDiasSemestre);
            
            //filtro update            
            psupdate.setString(2, _empresaId);
            psupdate.setInt(3, _anio);
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println(WEB_NAME+"[PermisosAdministrativosDAO.updateDiasAdministrativosSemestre]"
                   + ", empresaId:" + _empresaId
                    + ", anio:" + _anio    
                    + ", semestre:" + _semestre
                    + " actualizado OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[PermisosAdministrativosDAO.updateDiasAdministrativosSemestre]"
                + "update Error: " + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (psupdate!=null) psupdate.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[PermisosAdministrativosDAO.updateDiasAdministrativosSemestre]Error: "+ex.toString());
            }
        }

        return objresultado;
    }

    /**
    * Reinicia el número de días de 'permisos administrativos' de un semestre (disponibles y utilizados)
    * Para todos los empleados de la empresa indicada
    * Esto es: 
    *   Insertar un nuevo registro en la tabla 'permiso_administrativo' 
    *   
    * @param _empresaId
    * @param _maximoDiasSemestre
    * @param _currentYear
    * @param _semestre
    * 
    * @return 
    */
    public ResultCRUDVO resetearDiasAdministrativosSemestre(String _empresaId, 
            int _maximoDiasSemestre,
            int _currentYear, 
            int _semestre){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        int result=0;
        String msgError = "Error al retetear "
            + "Resumen Permiso Administrativo para el "
            + "anio-semestre: " + _currentYear + "-" + _semestre
            + ", empresaId: " + _empresaId
            + ", maximo dias PA Semestre: " + _maximoDiasSemestre;
        
        String msgFinal = " Inserta Resumen Permiso Administrativo:"
            + " Anio [" + _currentYear 
            + ", semestre [" + _semestre     
            + ", empresaId [" + _empresaId 
            + ", maximo dias Semestre [" + _maximoDiasSemestre + "]";
       
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
       
        try{
            String sql = "INSERT INTO permiso_administrativo("
                + "empresa_id, "
                + "run_empleado, "
                + "anio, "
                + "dias_disponibles_semestre" + _semestre + ", "
                + "dias_utilizados_semestre" + _semestre + ", "
                + "last_update) "
                + "select empresa_id, empl_rut, "
                    + _currentYear + "," + _maximoDiasSemestre + ",0,current_timestamp "
                    + "from empleado "
                    + "where empleado.empresa_id= '" + _empresaId + "' and empl_estado = 1 "
                    + "and empl_fec_fin_contrato >= current_date";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[PermisosAdministrativosDAO.resetearDiasAdministrativosSemestre]");
            insert = dbConn.prepareStatement(sql);
                        
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas > 0){
                System.out.println(WEB_NAME+"[PermisosAdministrativosDAO.resetearDiasAdministrativosSemestre]"
                    + "Reseteo de Resumen PA OK, Anio: " + _currentYear+", filas afectadas= " + filasAfectadas);
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[PermisosAdministrativosDAO.resetearDiasAdministrativosSemestre]"
                + "Error1: " + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (insert != null) insert.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[PermisosAdministrativosDAO.resetearDiasAdministrativosSemestre]"
                    + "Error: " + ex.toString());
            }
        }

        return objresultado;
    }
    
    /**
    * Reinicia el número de días de 'permisos administrativos' de un semestre (disponibles y utilizados)
    * Para todos los empleados de la empresa indicada
    * Esto es: 
    *   Insertar un nuevo registro en la tabla 'permiso_administrativo' 
    *   
    * @param _empresaId
     * @param _runEmpleado
     * @param _maximoDiasSemestre
     * @param _currentYear
     * @param _semestre
    * 
    * @return 
    */
    public ResultCRUDVO insertaRegistroPermisoAdministrativo(String _empresaId, 
            String _runEmpleado,
            int _maximoDiasSemestre,
            int _currentYear, 
            int _semestre){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        int result=0;
        String msgError = "Error al insertar "
            + "Resumen Permiso Administrativo para: "
            + "EmpresaId: " + _empresaId
            + ", runEmpleado: " + _runEmpleado
            + ", anio-semestre: " + _currentYear + "-" + _semestre
            + ", maximo dias PA Semestre: " + _maximoDiasSemestre;
        
        String msgFinal = " Inserta Resumen Permiso Administrativo:"
            + " EmpresaId [" + _empresaId + "]"
            + ", run empleado [" + _runEmpleado + "]"    
            + ", anio-semestre [" + _currentYear + "-" + _semestre + "]"
            + ", maximo dias Semestre [" + _maximoDiasSemestre + "]";
       
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
       
        try{
            String sql = "INSERT INTO permiso_administrativo("
                + "empresa_id, "
                + "run_empleado, "
                + "anio, "
                + "dias_disponibles_semestre" + _semestre + ", "
                + "dias_utilizados_semestre" + _semestre + ", "
                + "last_update) values ('" + _empresaId + "','" + _runEmpleado + "'," + _currentYear + "," + _maximoDiasSemestre + ",0,current_timestamp)";

            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[PermisosAdministrativosDAO.insertaRegistroPermisoAdministrativo]");
            insert = dbConn.prepareStatement(sql);
                        
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas > 0){
                System.out.println(WEB_NAME+"[PermisosAdministrativosDAO."
                    + "insertaRegistroPermisoAdministrativo]"
                    + "Reseteo de Resumen PA OK, Anio: " + _currentYear+", filas afectadas= " + filasAfectadas);
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[PermisosAdministrativosDAO."
                + "insertaRegistroPermisoAdministrativo]"
                + "Error1: " + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (insert != null) insert.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[PermisosAdministrativosDAO."
                    + "insertaRegistroPermisoAdministrativo]"
                    + "Error: " + ex.toString());
            }
        }

        return objresultado;
    }
    
////    /**
////    * Agrega un nuevo registro de Permiso Administrativo
////    * 
////    * @param _data
////    * @return 
////    */
////    public ResultCRUDVO insertResumenPASemestre1(PermisoAdministrativoVO _data){
////        ResultCRUDVO objresultado = new ResultCRUDVO();
////        int result=0;
////        String msgError = "Error al insertar "
////            + "Resumen Permiso Administrativo (1er semestre), "
////            + "EmpresaId: " + _data.getEmpresaId()
////            + ", runEmpleado: " + _data.getRunEmpleado()    
////            + ", anio: " + _data.getAnio()
////            + ", dias_disponibles_1erSemestre: " + _data.getDiasDisponiblesSemestre1()
////            + ", dias_utilizados_1erSemestre: " + _data.getDiasUtilizadosSemestre1();
////        
////        String msgFinal = " Inserta Resumen Permiso Administrativo (1er semestre):"
////            + "EmpresaId [" + _data.getEmpresaId() + "]" 
////            + ", runEmpleado [" + _data.getRunEmpleado() + "]"    
////            + ", anio [" + _data.getAnio() + "]"
////            + ", dias_disponibles [" + _data.getDiasDisponiblesSemestre1() + "]"
////            + ", dias_utilizados [" + _data.getDiasUtilizadosSemestre1() + "]";
////       
////        objresultado.setMsg(msgFinal);
////        PreparedStatement insert    = null;
////        
////        try{
////            String sql = "INSERT INTO permiso_administrativo("
////                + "empresa_id, run_empleado, anio, "
////                + "dias_disponibles_semestre1, "
////                + "dias_utilizados_semestre1, last_update) "
////                + "VALUES (?, ?, ?, ?, ?, current_timestamp)";
////
////            dbConn = dbLocator.getConnection(m_dbpoolName,"[PermisosAdministrativosDAO.insertResumenPASemestre1]");
////            insert = dbConn.prepareStatement(sql);
////            insert.setString(1,  _data.getEmpresaId());
////            insert.setString(2,  _data.getRunEmpleado());
////            insert.setInt(3,  _data.getAnio());
////            insert.setDouble(4,  _data.getDiasDisponiblesSemestre1());
////            insert.setDouble(5,  _data.getDiasUtilizadosSemestre1());
////                        
////            int filasAfectadas = insert.executeUpdate();
////            if (filasAfectadas == 1){
////                System.out.println(WEB_NAME+"[PermisosAdministrativosDAO.insertResumenPASemestre1]"
////                    + "EmpresaId:" + _data.getEmpresaId()
////                    + ", rutEmpleado:" + _data.getRunEmpleado()    
////                    + ", anio:" +_data.getAnio()
////                    + ", dias_utilizados:" + _data.getDiasUtilizadosSemestre1()
////                    + ", dias_disponibles:" + _data.getDiasDisponiblesSemestre1()
////                    + " insertado OK!");
////            }
////            
////            insert.close();
////            dbLocator.freeConnection(dbConn);
////        }catch(SQLException|DatabaseException sqle){
////            System.err.println("[PermisosAdministrativosDAO.insertResumenPASemestre1]"
////                + "Error1: " + sqle.toString());
////            objresultado.setThereError(true);
////            objresultado.setCodError(result);
////            objresultado.setMsgError(msgError+" :"+sqle.toString());
////        }finally{
////            try {
////                if (insert != null) insert.close();
////                dbLocator.freeConnection(dbConn);
////            } catch (SQLException ex) {
////                System.err.println("[PermisosAdministrativosDAO.insertResumenPASemestre1]"
////                    + "Error: " + ex.toString());
////            }
////        }
////
////        return objresultado;
////    }
    
    /**
    * Elimina info de permiso administrativo
    * 
    * @param _data
    * @return 
    */
    public ResultCRUDVO deleteResumenPA(PermisoAdministrativoVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
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
            System.out.println(WEB_NAME+"[PermisosAdministrativosDAO.deleteResumenPA]"
                + "filasAfectadas: " + filasAfectadas);
            if (filasAfectadas == 1){
                System.out.println(WEB_NAME+"[PermisosAdministrativosDAO.deleteResumenPA]"
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
    * Elimina info de permiso administrativo para el semestre-anio indicado
    * 
    * @param _empresaId
    * @param _anio
    * @return 
    */
    public boolean deleteResumenPAAnio(String _empresaId, 
            int _anio){
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
            System.out.println(WEB_NAME+"[PermisosAdministrativosDAO.deleteResumenPAAnio]"
                + "filasAfectadas: " + filasAfectadas);
            if (filasAfectadas == 1){
                System.out.println(WEB_NAME+"[PermisosAdministrativosDAO.deleteResumenPAAnio]"
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
                    + "pa.dias_disponibles_semestre1,"
                    + "pa.dias_utilizados_semestre1,"
                    + "pa.dias_disponibles_semestre2,"
                    + "pa.dias_utilizados_semestre2,"
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
            
            System.out.println(WEB_NAME+"[PermisosAdministrativosDAO."
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
                
                data.setDiasDisponiblesSemestre1(rs.getDouble("dias_disponibles_semestre1"));
                data.setDiasUtilizadosSemestre1(rs.getDouble("dias_utilizados_semestre1"));
                data.setDiasDisponiblesSemestre2(rs.getDouble("dias_disponibles_semestre2"));
                data.setDiasUtilizadosSemestre2(rs.getDouble("dias_utilizados_semestre2"));
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
            
            System.out.println(WEB_NAME+"[PermisosAdministrativosDAO."
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
        System.out.println(WEB_NAME+"[PermisosAdministrativosDAO."
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
            System.out.println(WEB_NAME+"[PermisosAdministrativosDAO."
                + "getDiasEfectivos]"
                + "Itera fecha= " + itfecha);
            LocalDate localdate = Utilidades.getLocalDate(itfecha);
            int diaSemana = localdate.getDayOfWeek().getValue();
            if (diaSemana >= 1 && diaSemana <= 5){
                System.out.println(WEB_NAME+"[PermisosAdministrativosDAO.getDiasEfectivos]Es dia de semana (Lu-Vi)");
                String strKey = _empresaId + "|" + _runEmpleado + "|" + itfecha;
                InfoFeriadoVO infoFeriado = fechasCalendarioFeriados.get(strKey);
                boolean esFeriado = infoFeriado.isFeriado();
                System.out.println(WEB_NAME+"[PermisosAdministrativosDAO.getDiasEfectivos]"
                    + "Fecha " + itfecha + ", es feriado? " + esFeriado);
                if (!esFeriado) {
                    diasEfectivos++;
                }else{
                    System.out.println(WEB_NAME+"[PermisosAdministrativosDAO.getDiasEfectivos]-1-"
                        + "Fecha " + itfecha + ", no contabilizar dias efectivos");
                }
            }else{
                System.out.println(WEB_NAME+"[PermisosAdministrativosDAO.getDiasEfectivos]-2-"
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
            
            System.out.println(WEB_NAME+"[PermisosAdministrativosDAO."
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
    
    /**
    * 
    * Para llamar a la función:
    *   codempresa
    *   rutempleado
    *   anio_calc: Extraerlo de la fecha de inicio del PA
    *   semestre: usar el mes de la misma fecha de inicio, que se encuentre entre el 1 al 6, y del 7 al 12 para 1er o 2do semestre respectivamente.
    * 
    * @param _empresaId 
    * @param _runEmpleado 
    * @param _anioCalculo 
    * @param _semestre 
    * @return 
    */
    public ResultCRUDVO setSaldoPermisoAdministrativo(String _empresaId, 
            String _runEmpleado, 
            int _anioCalculo, 
            String _semestre){
        ResultCRUDVO CRUDResult = new ResultCRUDVO();
        String strJson = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        int result = 0;
        String msgError = "Error calling the function '" + Constantes.fnSET_SALDO_PERMISO_ADMINISTRATIVO + "'" 
            + ". Empresa_id: " + _empresaId
            + ", run empleado: " + _runEmpleado
            + ", anio calculo: " + _anioCalculo
            + ", semestre: " + _semestre;
        String msgFinal = "Calling the function '" + Constantes.fnSET_SALDO_PERMISO_ADMINISTRATIVO + "'"
            + ". Empresa_id [" + _empresaId + "]"
            + ", run empleado [" + _runEmpleado + "]"
            + ", anio calculo [" + _anioCalculo + "]"
            + ", semestre [" + _semestre + "]";
                        
        CRUDResult.setMsg(msgFinal);
        String sqlFunctionInvoke = "select " + Constantes.fnSET_SALDO_PERMISO_ADMINISTRATIVO + "('" + _empresaId + "',"
            + "'" + _runEmpleado + "',"
            + _anioCalculo + ","
            + "'" + _semestre + "') "
            + "strjson";

        System.out.println("[PermisosAdministrativosDAO.setSaldoPermisoAdministrativo]Sql: " + sqlFunctionInvoke);
        
        try{
            dbConn = dbLocator.getConnection(m_dbpoolName, "[PermisosAdministrativosDAO.setSaldoPermisoAdministrativo]");
            ps = dbConn.prepareStatement(sqlFunctionInvoke);
            rs = ps.executeQuery();

            while (rs.next()) {
                strJson += rs.getString("strjson");
            }
            System.out.println("[PermisosAdministrativosDAO.setSaldoPermisoAdministrativo]"
                + "salida funcion json: " + strJson);
            
            FilasAfectadasJsonVO filasAfectadadaObj = (FilasAfectadasJsonVO)new Gson().fromJson(strJson, FilasAfectadasJsonVO.class);
            CRUDResult.setFilasAfectadasObj(filasAfectadadaObj);
        }catch(SQLException sqle){
            System.err.println("[PermisosAdministrativosDAO.setSaldoPermisoAdministrativo]"
                + "Error_1: " + sqle.toString());
            CRUDResult.setThereError(true);
            CRUDResult.setCodError(result);
            CRUDResult.setMsgError(msgError+" :"+sqle.toString());
        }catch(DatabaseException dbex){
            System.err.println("[PermisosAdministrativosDAO.setSaldoPermisoAdministrativo]"
                + "Error_2: " + dbex.toString());
            CRUDResult.setThereError(true);
            CRUDResult.setCodError(result);
            CRUDResult.setMsgError(msgError+" :"+dbex.toString());
        }
        finally{
            try {
                if (ps != null) {
                    ps.close();
                    rs.close();
                }
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[PermisosAdministrativosDAO.setSaldoPermisoAdministrativo]"
                + "Error_3:" + ex.toString());
            }
        }
        
        return CRUDResult;
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
