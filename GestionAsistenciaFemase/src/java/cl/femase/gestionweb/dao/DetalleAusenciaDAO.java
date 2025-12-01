/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.DetalleAusenciaVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioCentroCostoVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexander
 */
public class DetalleAusenciaDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    //private Connection dbConn;
    //DbDirectConnection dbconnection = DbDirectConnection.getInstance();
    
    public DetalleAusenciaDAO(PropertiesVO _propsValues) {
//        try {
//            dbLoc = DatabaseLocator.getInstance();
//            m_dbpoolName = _propsValues.getDbPoolName();
//        } catch (DatabaseException ex) {
//            m_logger.error("DbError: "+ex.toString());
//        }
    }

    /**
    * 
    * @param _data
    * @return 
    */
    public ResultCRUDVO update(DetalleAusenciaVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "detalle_ausencia, "
            + "correlativo: "+_data.getCorrelativo()
            + ", rutEmpleado: "+_data.getRutEmpleado()
            + ", ausenciaId: "+_data.getIdAusencia()
            + ", ausenciaNombre: "+_data.getNombreAusencia()    
            + ", fechaIngreso: "+_data.getFechaIngresoAsStr()
            + ", fechaInicio: " + _data.getFechaInicioAsStr()
            + ", horaInicio: " + _data.getHoraInicioFullAsStr()    
            + ", fechaFin: " + _data.getFechaFinAsStr()
            + ", horaFin: " + _data.getHoraFinFullAsStr()
            + ", rutAutorizador: "+_data.getRutAutorizador()
            + ", ausenciaAutorizada: "+_data.getAusenciaAutorizada();
        
        try{
            String msgFinal = " Actualiza detalle_ausencia:"
                + " Correlativo [" + _data.getCorrelativo() + "]"
                + ", rutEmpleado [" + _data.getRutEmpleado() + "]" 
                + ", ausenciaId [" + _data.getIdAusencia() + "]"
                + ", ausenciaNombre [" + _data.getNombreAusencia() + "]"    
                + ", fechaIngreso [" + _data.getFechaIngresoAsStr() + "]"
                + ", fechaInicio [" + _data.getFechaInicioAsStr() + "]"
                + ", horaInicio [" + _data.getHoraInicioFullAsStr() + "]"    
                + ", fechaFin [" + _data.getFechaFinAsStr() + "]"
                + ", horaFin [" + _data.getHoraFinFullAsStr() + "]"    
                + ", rutAutorizador [" + _data.getRutAutorizador() + "]"
                + ", ausenciaAutorizada [" + _data.getAusenciaAutorizada() + "]"
                + ", permiteHora [" + _data.getPermiteHora() + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE detalle_ausencia "
                + "SET "
                + "fecha_inicio='"+_data.getFechaInicioAsStr()+"',"
                + "fecha_fin='"+_data.getFechaFinAsStr()+"',"
                + "rut_autoriza_ausencia=?, "
                + "ausencia_autorizada=?,"
                + "fecha_actualizacion=current_timestamp,"
                + "allow_hour = ?,"
                + "ausencia_id = ? ";
            
            if (_data.getPermiteHora().compareTo("S") == 0){
                sql+=",hora_inicio='" + _data.getHoraInicioFullAsStr() + "', "
                    + "hora_fin='" + _data.getHoraFinFullAsStr() + "' ";
            }else {
                sql+=",hora_inicio=null,hora_fin = null ";
            }
            
            if (_data.getIdAusencia() == 1){//Vacaciones
                sql += ",dias_efectivos_vacaciones = " + _data.getDiasEfectivosVacaciones();
            }
            
            sql += " WHERE correlativo = ? ";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[DetalleAusenciaDAO.update]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setString(1,  _data.getRutAutorizador());
            psupdate.setString(2,  _data.getAusenciaAutorizada());
            psupdate.setString(3,  _data.getPermiteHora());
            psupdate.setInt(4,  _data.getIdAusencia());
            psupdate.setInt(5,  _data.getCorrelativo());
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println(WEB_NAME+"[update]detalle_ausencia"
                    + ", rutEmpleado:" +_data.getRutEmpleado()
                    + ", fechaIngreso:" +_data.getFechaIngresoAsStr()
                    + ", ausenciaId:" +_data.getIdAusencia()    
                    +" actualizada OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("update detalle_ausencia Error: "+sqle.toString());
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
    * 
    * @param _data
    * @return 
    */
    public ResultCRUDVO updateDiasEfectivosVacaciones(DetalleAusenciaVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "detalle_ausencia.dias_efectivos_vacaciones "
            + ", empresaId: "+_data.getEmpresaId()    
            + ", rutEmpleado: "+_data.getRutEmpleado()
            + ", fechaIngreso: "+_data.getFechaIngresoAsStr();
        
        try{
            String msgFinal = " Actualiza detalle_ausencia.dias_efectivos_vacaciones:"
                + " EmpresaId [" + _data.getEmpresaId() + "]"
                + ", rutEmpleado [" + _data.getRutEmpleado() + "]" 
                + ", fechaIngreso [" + _data.getFechaIngresoAsStr() + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE detalle_ausencia "
                + "SET "
                    + "dias_efectivos_vacaciones = ?, "
                    + "fecha_actualizacion = current_timestamp "
                + " WHERE rut_empleado = ? "
                + " and fecha_inicio = '" + _data.getFechaInicioAsStr() + "' "
                + " and ausencia_id = " + Constantes.ID_AUSENCIA_VACACIONES;

            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[DetalleAusenciaDAO.updateDiasEfectivosVacaciones]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setDouble(1,  _data.getDiasEfectivosVacaciones());
            psupdate.setString(2,  _data.getRutEmpleado());
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println(WEB_NAME+"[DetalleAusenciaDAO."
                    + "updateDiasEfectivosVacaciones]"
                    + "detalle_ausencia"
                    + ", empresaId: " +_data.getEmpresaId()    
                    + ", rutEmpleado: " +_data.getRutEmpleado()
                    + ", fechaInicio: " +_data.getFechaInicioAsStr()
                    +" actualizada OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[DetalleAusenciaDAO."
                + "updateDiasEfectivosVacaciones]Error: " + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (psupdate != null) psupdate.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[DetalleAusenciaDAO."
                    + "updateDiasEfectivosVacaciones]Error: " + ex.toString());
            }
        }
        return objresultado;
    }
    
    /**
    * Actualiza dias efectivos de vacaciones
    * 
     * @param _correlativo
     * @param _diasEfectivos
    * @return 
    */
    public ResultCRUDVO updateDiasEfectivosVacaciones(int _correlativo, 
            int _diasEfectivos){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "detalle_ausencia, "
            + "correlativo: "+_correlativo
            + ", dias_efectivos_vacaciones: " + _diasEfectivos;
        
        try{
            String msgFinal = " Actualiza detalle_ausencia:"
                + " Correlativo [" + _correlativo + "]"
                + ", dias_efectivos_vacaciones [" + _diasEfectivos + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE detalle_ausencia "
                + "SET "
                + " dias_efectivos_vacaciones = ? "
                + " WHERE correlativo = ? ";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[DetalleAusenciaDAO.updateDiasEfectivosVacaciones]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setInt(1,  _diasEfectivos);
            psupdate.setInt(2,  _correlativo);
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println(WEB_NAME+"[update dias efectivos vacaciones]detalle_ausencia"
                    + ", correlativo:" + _correlativo
                    + ", dias_efectivos:" + _diasEfectivos    
                    +" actualizada OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("update detalle_ausencia, dias_efectivos Error: "+sqle.toString());
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
     * 
     * @param _data
     * @return 
     */
    public ResultCRUDVO delete(DetalleAusenciaVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al eliminar "
            + "detalle_ausencia, "
            + "correlativo: "+_data.getCorrelativo()
            + ", rutEmpleado: "+_data.getRutEmpleado()
            + ", ausenciaNombre: "+_data.getNombreAusencia()    
            + ", fechaIngreso: "+_data.getFechaIngresoAsStr()
            + ", fechaInicio: " + _data.getFechaInicioAsStr()
            + ", horaInicio: " + _data.getHoraInicioFullAsStr()    
            + ", fechaFin: " + _data.getFechaFinAsStr()
            + ", horaFin: " + _data.getHoraFinFullAsStr()
            + ", rutAutorizador: "+_data.getRutAutorizador()
            + ", nombreAutorizador: "+_data.getNombreAutorizador()    
            + ", ausenciaAutorizada: "+_data.getAusenciaAutorizada();
        
        try{
            String msgFinal = " Elimina detalle_ausencia:"
                + " Correlativo [" + _data.getCorrelativo() + "]"
                + ", rutEmpleado [" + _data.getRutEmpleado() + "]" 
                + ", ausenciaNombre [" + _data.getNombreAusencia() + "]"    
                + ", fechaIngreso [" + _data.getFechaIngresoAsStr() + "]"
                + ", fechaInicio [" + _data.getFechaInicioAsStr() + "]"
                + ", horaInicio [" + _data.getHoraInicioFullAsStr() + "]"    
                + ", fechaFin [" + _data.getFechaFinAsStr() + "]"
                + ", horaFin [" + _data.getHoraFinFullAsStr() + "]"    
                + ", rutAutorizador [" + _data.getRutAutorizador() + "]"
                + ", nombreAutorizador [" + _data.getNombreAutorizador() + "]"
                + ", ausenciaAutorizada [" + _data.getAusenciaAutorizada() + "]"
                + ", permiteHora [" + _data.getPermiteHora() + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "delete from detalle_ausencia "
                + "WHERE correlativo = ? ";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[DetalleAusenciaDAO.delete]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setInt(1,  _data.getCorrelativo());
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println(WEB_NAME+"[delete]detalle_ausencia"
                    + " Correlativo:" +_data.getCorrelativo()   
                    +" eliminada OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("update detalle_ausencia Error: "+sqle.toString());
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
    * Completa los campos:
    *  detalle_ausencia.saldo_dias_vacaciones_asignadas y 
    *  detalle_ausencia.dias_acumulados_vacaciones_asignadas 
    * a partir del valor existente en el campo dias_efectivos_vacaciones.
    * 
    * @param _runEmpleado
    * @return 
    */
    public ResultCRUDVO actualizaSaldosVacaciones(String _runEmpleado){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        PreparedStatement ps = null;
        ResultSet rs = null;
        int result=0;
        
        String msgError = "Error al actualizar saldos de "
            + "vacaciones en tabla detalle_ausencia"
            + ", run: " + _runEmpleado;
              
        String msgFinal = "Actualiza saldos de "
            + "vacaciones en tabla detalle_ausencia."
            + ", runEmpleado [" + _runEmpleado + "]";
       
        try{
            
            objresultado.setMsg(msgFinal);
            /**
             * Ejemplo:
             * SELECT setsaldodiasvacacionesasignadas('11111111-1');
             */
            String sql = "SELECT setsaldodiasvacacionesasignadas"
                    + "('" + _runEmpleado + "') strjson";

            System.out.println(WEB_NAME+"[DetalleAusenciaDAO."
                + "actualizaSaldosVacaciones]Sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[DetalleAusenciaDAO."
                    + "actualizaSaldosVacaciones]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            String salidaSp = "";
            if (rs.next()) {
                salidaSp = rs.getString("strjson");
            }
            objresultado.setMsgFromSp(salidaSp);
            
            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[DetalleAusenciaDAO."
                + "actualizaSaldosVacaciones]"
                + "Error al actualizar saldos de vacaciones:" + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[DetalleAusenciaDAO."
                    + "actualizaSaldosVacaciones]"
                    + "Error: " + ex.toString());
            }
        }
        
        return objresultado;
    }
    
    /*
    **
    * Completa los campos:
    *    detalle_ausencia.saldo_vba_pre_vacaciones y 
    *    detalle_ausencia.saldo_vba_post_vacaciones 
    *  a partir del valor existente en el campo dias_efectivos_vba.
    * 
    * @param _runEmpleado
    * @return 
    */
    public ResultCRUDVO actualizaSaldosVacacionesVBA(String _runEmpleado){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        PreparedStatement ps = null;
        ResultSet rs = null;
        int result=0;
        
        String msgError = "Error al actualizar saldos de "
            + "vacaciones en tabla detalle_ausencia"
            + ", run: " + _runEmpleado;
              
        String msgFinal = "Actualiza saldos de "
            + "vacaciones en tabla detalle_ausencia."
            + ", runEmpleado [" + _runEmpleado + "]";
       
        try{
            
            objresultado.setMsg(msgFinal);
            /**
             * Ejemplo:
             * SELECT setsaldodiasvacacionesasignadas_vba('11111111-1');
             */
            String sql = "SELECT setsaldodiasvacacionesasignadas_vba"
                    + "('" + _runEmpleado + "') strjson";

            System.out.println(WEB_NAME+"[DetalleAusenciaDAO."
                + "actualizaSaldosVacacionesVBA]Sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[DetalleAusenciaDAO."
                    + "actualizaSaldosVacacionesVBA]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            String salidaSp = "";
            if (rs.next()) {
                salidaSp = rs.getString("strjson");
            }
            objresultado.setMsgFromSp(salidaSp);
            
            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[DetalleAusenciaDAO."
                + "actualizaSaldosVacacionesVBA]"
                + "Error al actualizar saldos de vacaciones vba:" + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[DetalleAusenciaDAO."
                    + "actualizaSaldosVacacionesVBA]"
                    + "Error: " + ex.toString());
            }
        }
        
        return objresultado;
    }
    
    /**
    * Agrega detalle ausencia
    * @param _data
    * @return 
    */ 
    public ResultCRUDVO insert(DetalleAusenciaVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        int result=0;
        int newId = getNewIdDetalleAusencia();
        
        String msgError = "Error al insertar "
            + "detalle_ausencia, "
            + "rutEmpleado: " + _data.getRutEmpleado()    
            + ", newCorrelativo: " + newId
            + ", tipoAusenciaId: " + _data.getIdAusencia()
            + ", ausenciaNombre: " + _data.getNombreAusencia()    
            + ", fechaInicio: " + _data.getFechaInicioAsStr()
            + ", horaInicio: " + _data.getHoraInicioFullAsStr()    
            + ", fechaFin: " + _data.getFechaFinAsStr()
            + ", horaFin: " + _data.getHoraFinFullAsStr()   
            + ", rutAutorizador: " + _data.getRutAutorizador()
            + ", ausenciaAutorizada: " + _data.getAusenciaAutorizada();
        
        String msgFinal = " Inserta detalle_ausencia:"
            + "rutEmpleado [" + _data.getRutEmpleado() + "]" 
            + ", newCorrelativo [" + newId + "]"    
            + ", tipoAusenciaId [" + _data.getIdAusencia() + "]"
            + ", ausenciaNombre [" + _data.getNombreAusencia() + "]"    
            + ", fechaInicio [" + _data.getFechaInicioAsStr() + "]"
            + ", horaInicio [" + _data.getHoraInicioFullAsStr() + "]"    
            + ", fechaFin [" + _data.getFechaFinAsStr() + "]"
            + ", horaFin [" + _data.getHoraFinFullAsStr() + "]"    
            + ", rutAutorizador [" + _data.getRutAutorizador() + "]"
            + ", ausenciaAutorizada [" + _data.getAusenciaAutorizada() + "]";
            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO detalle_ausencia("
                + "correlativo, "
                + "rut_empleado, "
                + "fecha_ingreso, "
                + "ausencia_id, "
                + "fecha_inicio, "
                + "fecha_fin,"
                + "rut_autoriza_ausencia, "
                + "ausencia_autorizada, "
                + "fecha_actualizacion,"
                + "allow_hour";
            if (_data.getPermiteHora().compareTo("S") == 0){
                sql += ",hora_inicio,hora_fin";
            }
            if (_data.getIdAusencia() == 1){//Vacaciones
                sql += ",dias_efectivos_vacaciones";
            }
            
            sql += ")"
                + " VALUES ("+newId+", ?, current_date, ?,"
                + " '"+_data.getFechaInicioAsStr()+"', "
                + "'"+_data.getFechaFinAsStr()+"', ?, ?, "
                + "current_timestamp,? ";
            
            if (_data.getPermiteHora().compareTo("S") == 0){
                sql+=",'" + _data.getHoraInicioFullAsStr() + "', "
                    + "'" + _data.getHoraFinFullAsStr() + "' ";
            }
            if (_data.getIdAusencia() == 1){//Vacaciones
                sql += ","+_data.getDiasEfectivosVacaciones();
            }
            sql += ")";

            System.out.println(WEB_NAME+"[DetalleAusenciaDAO.insert]sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[DetalleAusenciaDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getRutEmpleado());
            insert.setInt(2,  _data.getIdAusencia());
            insert.setString(3,  _data.getRutAutorizador());
            insert.setString(4,  _data.getAusenciaAutorizada());
            insert.setString(5,  _data.getPermiteHora());
            
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println(WEB_NAME+"[insert]detalle_ausencia"
                    + ", rutEmpleado:" +_data.getRutEmpleado()
                    + ", fechaIngreso:" +_data.getFechaIngresoAsStr()
                    + ", ausenciaId:" +_data.getIdAusencia()    
                    +" insertada OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("insert detalle_ausencia Error1: "+sqle.toString());
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
    * Agrega una vacacion en la tabla 'detalle ausencia'
    * @param _data
    * @return 
    */ 
    public ResultCRUDVO insertaVacacion(DetalleAusenciaVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        int result=0;
        int newId = getNewIdDetalleAusencia();
        
        String msgError = "Error al insertar "
            + "Vacacion en detalle_ausencia, "
            + "rutEmpleado: " + _data.getRutEmpleado()    
            + ", newCorrelativo: " + newId
            + ", tipoAusenciaId: " + _data.getIdAusencia()
            + ", ausenciaNombre: " + _data.getNombreAusencia()    
            + ", fechaInicio: " + _data.getFechaInicioAsStr()
            + ", horaInicio: " + _data.getHoraInicioFullAsStr()    
            + ", fechaFin: " + _data.getFechaFinAsStr()
            + ", horaFin: " + _data.getHoraFinFullAsStr()   
            + ", rutAutorizador: " + _data.getRutAutorizador()
            + ", ausenciaAutorizada: " + _data.getAusenciaAutorizada();
        
        String msgFinal = " Inserta Vacacion en detalle_ausencia:"
            + "rutEmpleado [" + _data.getRutEmpleado() + "]" 
            + ", newCorrelativo [" + newId + "]"    
            + ", tipoAusenciaId [" + _data.getIdAusencia() + "]"
            + ", ausenciaNombre [" + _data.getNombreAusencia() + "]"    
            + ", fechaInicio [" + _data.getFechaInicioAsStr() + "]"
            + ", horaInicio [" + _data.getHoraInicioFullAsStr() + "]"    
            + ", fechaFin [" + _data.getFechaFinAsStr() + "]"
            + ", horaFin [" + _data.getHoraFinFullAsStr() + "]"    
            + ", rutAutorizador [" + _data.getRutAutorizador() + "]"
            + ", ausenciaAutorizada [" + _data.getAusenciaAutorizada() + "]";
            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO detalle_ausencia("
                + "correlativo, "
                + "rut_empleado, "
                + "fecha_ingreso, "
                + "ausencia_id, "
                + "fecha_inicio, "
                + "fecha_fin,"
                + "rut_autoriza_ausencia, "
                + "ausencia_autorizada, "
                + "fecha_actualizacion,"
                + "allow_hour,"
                + "dias_efectivos_vacaciones, "
                + "dias_efectivos_vba,"
                + "dias_efectivos_vp,"
                + "saldo_vba_pre_vacaciones,"
                + "saldo_vp_pre_vacaciones,"
                + "saldo_vba_post_vacaciones,"
                + "saldo_vp_post_vacaciones";
            sql += ")"
                + " VALUES ("+newId+", ?, current_date, ?,"
                + " '"+_data.getFechaInicioAsStr()+"', "
                + "'"+_data.getFechaFinAsStr()+"', ?, ?, "
                + "current_timestamp, ? ";
            sql += ","+_data.getDiasEfectivosVacaciones();
            sql += ", ?, ?, ?, ?, ?, ?)";

            System.out.println(WEB_NAME+"[DetalleAusenciaDAO.insertaVacacion]sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[DetalleAusenciaDAO.insertaVacacion]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getRutEmpleado());
            insert.setInt(2,  _data.getIdAusencia());
            insert.setString(3,  _data.getRutAutorizador());
            insert.setString(4,  _data.getAusenciaAutorizada());
            insert.setString(5,  _data.getPermiteHora());
            //desglose dias de vacaciones basicas anuales y progresivas
            insert.setInt(6,  _data.getDiasEfectivosVBA());
            insert.setInt(7,  _data.getDiasEfectivosVP());
            insert.setDouble(8,  _data.getSaldoVBAPreVacaciones());
            insert.setDouble(9,  _data.getSaldoVPPreVacaciones());
            insert.setDouble(10,  _data.getSaldoVBAPostVacaciones());
            insert.setDouble(11,  _data.getSaldoVPPostVacaciones());
            
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println(WEB_NAME+"[insertaVacacion]detalle_ausencia"
                    + ", rutEmpleado:" +_data.getRutEmpleado()
                    + ", fechaIngreso:" +_data.getFechaIngresoAsStr()
                    + ", ausenciaId:" +_data.getIdAusencia()    
                    + ", diasEfectivosTotales: " + _data.getDiasEfectivosVacaciones()
                    + ", diasEfectivosVBA: " + _data.getDiasEfectivosVBA()    
                    + ", diasEfectivosVP: " + _data.getDiasEfectivosVP()        
                    + ", saldoVBAPreVacacion: " + _data.getSaldoVBAPreVacaciones()        
                    + ", saldoVPPreVacacion: " + _data.getSaldoVPPreVacaciones()            
                    + ", saldoVBAPostVacacion: " + _data.getSaldoVBAPostVacaciones()        
                    + ", saldoVPPostVacacion: " + _data.getSaldoVPPostVacaciones()            
                    +". Vacacion insertada OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("insertaVacacion en detalle_ausencia Error1: "+sqle.toString());
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
    * Agrega un Permiso Administrativo en la tabla 'detalle ausencia'
    * @param _data
    * @return 
    */ 
    public ResultCRUDVO insertaPermisoAdministrativo(DetalleAusenciaVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        int result=0;
        int newId = getNewIdDetalleAusencia();
        String msgError = "Error al insertar "
            + "Permiso administrativo en detalle_ausencia, "
            + "runEmpleado: " + _data.getRutEmpleado()    
            + ", newCorrelativo: " + newId
            + ", tipoAusenciaId: " + _data.getIdAusencia()
            + ", ausenciaNombre: " + _data.getNombreAusencia()    
            + ", fechaInicio: " + _data.getFechaInicioAsStr()
            + ", horaInicio: " + _data.getHoraInicioFullAsStr()    
            + ", fechaFin: " + _data.getFechaFinAsStr()
            + ", horaFin: " + _data.getHoraFinFullAsStr()   
            + ", permiteHora: " + _data.getPermiteHora()       
            + ", rutAutorizador: " + _data.getRutAutorizador()
            + ", ausenciaAutorizada: " + _data.getAusenciaAutorizada()
            + ", dias solicitados: " + _data.getDiasSolicitados();
        
        String msgFinal = " Inserta Permiso administrativo en detalle_ausencia:"
            + "rutEmpleado [" + _data.getRutEmpleado() + "]" 
            + ", newCorrelativo [" + newId + "]"    
            + ", tipoAusenciaId [" + _data.getIdAusencia() + "]"
            + ", ausenciaNombre [" + _data.getNombreAusencia() + "]"    
            + ", fechaInicio [" + _data.getFechaInicioAsStr() + "]"
            + ", horaInicio [" + _data.getHoraInicioFullAsStr() + "]"    
            + ", fechaFin [" + _data.getFechaFinAsStr() + "]"
            + ", horaFin [" + _data.getHoraFinFullAsStr() + "]"    
            + ", permiteHora [" + _data.getPermiteHora() + "]"        
            + ", rutAutorizador [" + _data.getRutAutorizador() + "]"
            + ", ausenciaAutorizada [" + _data.getAusenciaAutorizada() + "]"
            + ", dias solicitados [" + _data.getDiasSolicitados() + "]";
        
        System.out.println(WEB_NAME+"[DetalleAusenciaDAO."
            + "insertaPermisoAdministrativo]" + msgFinal);
        
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO detalle_ausencia("
                + "correlativo, "
                + "rut_empleado, "
                + "fecha_ingreso, "
                + "ausencia_id, "
                + "fecha_inicio, "
                + "fecha_fin,"
                + "rut_autoriza_ausencia, "
                + "ausencia_autorizada, "
                + "fecha_actualizacion,"
                + "allow_hour,"
                + "dias_solicitados, "
                + "saldo_post_pa";
            if (_data.getPermiteHora().compareTo("S") == 0){
                sql += ",hora_inicio,hora_fin";
            }
            
            sql += ")"
                + " VALUES (" + newId + ", "    //correlativo
                    + "?, "                     //rut_empleado
                    + "current_date, "          //fecha_ingreso
                    + "?,"                      //ausencia_id
                    + " '" + _data.getFechaInicioAsStr() + "', "//fecha inicio
                    + " '" + _data.getFechaFinAsStr() + "', "    //fecha_fin
                    + "?, "                                 //rut_autoriza_ausencia
                    + "?, "                                 //ausencia_autorizada
                    + "current_timestamp, "                 //fecha_actualizacion
                    + "? ";                                 //allow_hour
            sql += ","+_data.getDiasSolicitados();          //dias_solicitados
            sql += ",?"; //saldo post PA
            
            if (_data.getPermiteHora().compareTo("S") == 0){
                sql+=",'" + _data.getHoraInicioFullAsStr() + "', "
                    + "'" + _data.getHoraFinFullAsStr() + "' ";
            }
            
            sql += ")";    

            System.out.println(WEB_NAME+"[DetalleAusenciaDAO.insertaPermisoAdministrativo]sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[DetalleAusenciaDAO.insertaPermisoAdministrativo]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getRutEmpleado());
            insert.setInt(2,  _data.getIdAusencia());
            insert.setString(3,  _data.getRutAutorizador());
            insert.setString(4,  _data.getAusenciaAutorizada());
            insert.setString(5,  _data.getPermiteHora());
            insert.setDouble(6,  _data.getSaldoPostPA());
            
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println(WEB_NAME+"[DetalleAusenciaDAO.insertaPermisoAdministrativo]detalle_ausencia"
                    + ", rutEmpleado:" +_data.getRutEmpleado()
                    + ", fechaIngreso:" +_data.getFechaIngresoAsStr()
                    + ", ausenciaId:" +_data.getIdAusencia()    
                    + ", dias solicitados: " + _data.getDiasSolicitados()
                    + ", saldo post PA: " + _data.getSaldoPostPA()   
                    +". Permiso administrativo insertado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[DetalleAusenciaDAO.insertaPermisoAdministrativo]Error1: "+sqle.toString());
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
    * Agrega un Permiso Administrativo en la tabla 'detalle ausencia'
    * @param _data
    * @return 
    */ 
    public ResultCRUDVO insertaPermisoExamenSaludPreventiva(DetalleAusenciaVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        int result=0;
        int newId = getNewIdDetalleAusencia();
        String msgError = "Error al insertar "
            + "Permiso examen salud preventiva en detalle_ausencia, "
            + "runEmpleado: " + _data.getRutEmpleado()    
            + ", newCorrelativo: " + newId
            + ", tipoAusenciaId: " + _data.getIdAusencia()
            + ", ausenciaNombre: " + _data.getNombreAusencia()    
            + ", fechaInicio: " + _data.getFechaInicioAsStr()
            + ", fechaFin: " + _data.getFechaFinAsStr()
            + ", rutAutorizador: " + _data.getRutAutorizador()
            + ", ausenciaAutorizada: " + _data.getAusenciaAutorizada()
            + ", dias solicitados: " + _data.getDiasSolicitados();
        
        String msgFinal = " Inserta Permiso examen salud preventiva en detalle_ausencia:"
            + "rutEmpleado [" + _data.getRutEmpleado() + "]" 
            + ", newCorrelativo [" + newId + "]"    
            + ", tipoAusenciaId [" + _data.getIdAusencia() + "]"
            + ", ausenciaNombre [" + _data.getNombreAusencia() + "]"    
            + ", fechaInicio [" + _data.getFechaInicioAsStr() + "]"
            + ", fechaFin [" + _data.getFechaFinAsStr() + "]"
            + ", permiteHora [" + _data.getPermiteHora() + "]"        
            + ", rutAutorizador [" + _data.getRutAutorizador() + "]"
            + ", ausenciaAutorizada [" + _data.getAusenciaAutorizada() + "]"
            + ", dias solicitados [" + _data.getDiasSolicitados() + "]";
        
        System.out.println(WEB_NAME+"[DetalleAusenciaDAO."
            + "insertaPermisoExamenSaludPreventiva]" + msgFinal);
        
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO detalle_ausencia("
                + "correlativo, "
                + "rut_empleado, "
                + "fecha_ingreso, "
                + "ausencia_id, "
                + "fecha_inicio, "
                + "fecha_fin,"
                + "rut_autoriza_ausencia, "
                + "ausencia_autorizada, "
                + "fecha_actualizacion,"
                + "allow_hour,"
                + "dias_solicitados, "
                + "saldo_post_pesp";
            sql += ")"
                + " VALUES (" + newId + ", "    //correlativo
                    + "?, "                     //rut_empleado
                    + "current_date, "          //fecha_ingreso
                    + "?,"                      //ausencia_id
                    + " '" + _data.getFechaInicioAsStr() + "', "//fecha inicio
                    + " '" + _data.getFechaFinAsStr() + "', "    //fecha_fin
                    + "?, "                                 //rut_autoriza_ausencia
                    + "?, "                                 //ausencia_autorizada
                    + "current_timestamp, "                 //fecha_actualizacion
                    + "? ";                                 //allow_hour
            sql += ","+_data.getDiasSolicitados();          //dias_solicitados
            sql += ",?)";    

            System.out.println(WEB_NAME+"[DetalleAusenciaDAO.insertaPermisoExamenSaludPreventiva]sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[DetalleAusenciaDAO.insertaPermisoExamenSaludPreventiva]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getRutEmpleado());
            insert.setInt(2,  _data.getIdAusencia());
            insert.setString(3,  _data.getRutAutorizador());
            insert.setString(4,  _data.getAusenciaAutorizada());
            insert.setString(5,  _data.getPermiteHora());
            insert.setInt(6,  _data.getSaldoPostPESP());
            
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println(WEB_NAME+"[DetalleAusenciaDAO.insertaPermisoExamenSaludPreventiva]detalle_ausencia"
                    + ", rutEmpleado:" +_data.getRutEmpleado()
                    + ", fechaIngreso:" +_data.getFechaIngresoAsStr()
                    + ", ausenciaId:" +_data.getIdAusencia()    
                    + ", dias solicitados: " + _data.getDiasSolicitados()
                    + ", saldo post PESP: " + _data.getSaldoPostPESP()   
                    +". Permiso examen salud preventiva insertado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[DetalleAusenciaDAO.insertaPermisoExamenSaludPreventiva]Error1: "+sqle.toString());
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
    * Agrega un Permiso Administrativo en la tabla 'detalle ausencia'
    * @param _data
    * @return 
    */ 
    public ResultCRUDVO insertaPermisoExamenSaludPreventivo(DetalleAusenciaVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        int result=0;
        int newId = getNewIdDetalleAusencia();
        String msgError = "Error al insertar "
            + "Permiso Examen Salud Preventivo en detalle_ausencia, "
            + "runEmpleado: " + _data.getRutEmpleado()    
            + ", newCorrelativo: " + newId
            + ", tipoAusenciaId: " + _data.getIdAusencia()
            + ", ausenciaNombre: " + _data.getNombreAusencia()    
            + ", fechaInicio: " + _data.getFechaInicioAsStr()
            + ", horaInicio: " + _data.getHoraInicioFullAsStr()    
            + ", fechaFin: " + _data.getFechaFinAsStr()
            + ", horaFin: " + _data.getHoraFinFullAsStr()   
            + ", permiteHora: " + _data.getPermiteHora()       
            + ", rutAutorizador: " + _data.getRutAutorizador()
            + ", ausenciaAutorizada: " + _data.getAusenciaAutorizada()
            + ", dias solicitados: " + _data.getDiasSolicitados();
        
        String msgFinal = " Inserta Permiso Examen Salud Preventivo en detalle_ausencia:"
            + "rutEmpleado [" + _data.getRutEmpleado() + "]" 
            + ", newCorrelativo [" + newId + "]"    
            + ", tipoAusenciaId [" + _data.getIdAusencia() + "]"
            + ", ausenciaNombre [" + _data.getNombreAusencia() + "]"    
            + ", fechaInicio [" + _data.getFechaInicioAsStr() + "]"
            + ", horaInicio [" + _data.getHoraInicioFullAsStr() + "]"    
            + ", fechaFin [" + _data.getFechaFinAsStr() + "]"
            + ", horaFin [" + _data.getHoraFinFullAsStr() + "]"    
            + ", permiteHora [" + _data.getPermiteHora() + "]"        
            + ", rutAutorizador [" + _data.getRutAutorizador() + "]"
            + ", ausenciaAutorizada [" + _data.getAusenciaAutorizada() + "]"
            + ", dias solicitados [" + _data.getDiasSolicitados() + "]";
        
        System.out.println(WEB_NAME+"[DetalleAusenciaDAO."
            + "insertaPermisoExamenSaludPreventivo]" + msgFinal);
        
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO detalle_ausencia("
                + "correlativo, "
                + "rut_empleado, "
                + "fecha_ingreso, "
                + "ausencia_id, "
                + "fecha_inicio, "
                + "fecha_fin,"
                + "rut_autoriza_ausencia, "
                + "ausencia_autorizada, "
                + "fecha_actualizacion,"
                + "allow_hour,"
                + "dias_solicitados, "
                + "saldo_post_pesp";
            if (_data.getPermiteHora().compareTo("S") == 0){
                sql += ",hora_inicio,hora_fin";
            }
            
            sql += ")"
                + " VALUES (" + newId + ", "    //correlativo
                    + "?, "                     //rut_empleado
                    + "current_date, "          //fecha_ingreso
                    + "?,"                      //ausencia_id
                    + " '" + _data.getFechaInicioAsStr() + "', "//fecha inicio
                    + " '" + _data.getFechaFinAsStr() + "', "    //fecha_fin
                    + "?, "                                 //rut_autoriza_ausencia
                    + "?, "                                 //ausencia_autorizada
                    + "current_timestamp, "                 //fecha_actualizacion
                    + "? ";                                 //allow_hour
            sql += ","+_data.getDiasSolicitados();          //dias_solicitados
            sql += ",?"; //saldo post PESP
            
            if (_data.getPermiteHora().compareTo("S") == 0){
                sql+=",'" + _data.getHoraInicioFullAsStr() + "', "
                    + "'" + _data.getHoraFinFullAsStr() + "' ";
            }
            
            sql += ")";    

            System.out.println(WEB_NAME+"[DetalleAusenciaDAO.insertaPermisoExamenSaludPreventivo]sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[DetalleAusenciaDAO.insertaPermisoExamenSaludPreventivo]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getRutEmpleado());
            insert.setInt(2,  _data.getIdAusencia());
            insert.setString(3,  _data.getRutAutorizador());
            insert.setString(4,  _data.getAusenciaAutorizada());
            insert.setString(5,  _data.getPermiteHora());
            insert.setDouble(6,  _data.getSaldoPostPESP());
            
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println(WEB_NAME+"[DetalleAusenciaDAO.insertaPermisoExamenSaludPreventivo]detalle_ausencia"
                    + ", rutEmpleado:" +_data.getRutEmpleado()
                    + ", fechaIngreso:" +_data.getFechaIngresoAsStr()
                    + ", ausenciaId:" +_data.getIdAusencia()    
                    + ", dias solicitados: " + _data.getDiasSolicitados()
                    + ", saldo post PESP: " + _data.getSaldoPostPA()   
                    +". Permiso Examen Salud Preventivo insertado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[DetalleAusenciaDAO.insertaPermisoExamenSaludPreventivo]Error1: " + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (insert != null) insert.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: " + ex.toString());
            }
        }

        return objresultado;
    }
    
    /**
    * 
    * @param _empresaId
    * @param _runEmpleado
    * @param _fechaInicio
    * @param _fechaFin
    * @param _tipoAusencia
    * @return 
    */
    public List<DetalleAusenciaVO> getAusenciasFiltro(String _empresaId,
            String _runEmpleado, 
            String _fechaInicio, 
            String _fechaFin, 
            int _tipoAusencia){
        
        List<DetalleAusenciaVO> lista = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        DetalleAusenciaVO data;

        try{
            String sql = "select da.rut_empleado, " +
                "empleado.empl_nombres || ' ' || empleado.empl_ape_paterno || ' ' || empleado.empl_ape_materno nombre_empleado, " +
                "cargo.cargo_nombre cargo_empleado, " +
                "da.correlativo correlativo_ausencia, " +
                "da.fecha_inicio, da.fecha_fin, " + 
                "da.ausencia_id, " +
                "a.ausencia_nombre, " +
                "da.allow_hour ausencia_por_horas, " +
                "coalesce(hora_inicio, '00:00:00') hora_inicio," + 
                "coalesce(hora_fin, '00:00:00') hora_fin, " + 
                "da.ausencia_autorizada, " +
                "da.rut_autoriza_ausencia, " +
                "autorizador.empl_nombres || ' ' || autorizador.empl_ape_paterno || ' ' || autorizador.empl_ape_materno nombre_autorizador, " +
                "cargo_autorizador.cargo_nombre cargo_autorizador,"
                + "cc.ccosto_id cencoIdEmpleado,"
                + "cc.ccosto_nombre cencoNombreEmpleado " +
            " from detalle_ausencia da " +
                " inner join ausencia a on (a.ausencia_id = da.ausencia_id) " +
                " inner join empleado on (da.rut_empleado = empleado.empl_rut and empleado.empresa_id = ?) " +
                " inner join empleado autorizador on (da.rut_autoriza_ausencia = autorizador.empl_rut and autorizador.empresa_id = ?) " +
                " inner join cargo on (empleado.empl_id_cargo = cargo.cargo_id) " +
                " inner join cargo cargo_autorizador on (autorizador.empl_id_cargo = cargo_autorizador.cargo_id) "
                + " inner join centro_costo cc on (empleado.cenco_id = cc.ccosto_id) "
            + " where a.ausencia_estado = 1 "
            + " and da.fecha_inicio <= '" + _fechaFin + "' and da.fecha_fin >= '" + _fechaInicio + "' " // <-- solape!
            + " and da.ausencia_id != " + Constantes.ID_AUSENCIA_VACACIONES
            + " and da.rut_empleado = ? ";
            if (_tipoAusencia != -1){
                sql+= " and da.ausencia_id= ? ";
            }
            sql += "order by da.fecha_inicio desc";

            dbConn = dbLocator.getConnection(m_dbpoolName, "[DetalleAusenciaDAO.getAusenciasFiltro]");
            ps = dbConn.prepareStatement(sql);

            int idx = 1;
            ps.setString(idx++, _empresaId);
            ps.setString(idx++, _empresaId);
            //ps.setString(idx++, _fechaFin);    // <= ? (fecha_inicio)
            //ps.setString(idx++, _fechaInicio); // >= ? (fecha_fin)
            ps.setString(idx++, _runEmpleado);
            if (_tipoAusencia != -1) {
                ps.setInt(idx++, _tipoAusencia);
            }

            rs = ps.executeQuery();

            while (rs.next()){
                data = new DetalleAusenciaVO();
                data.setRutEmpleado(rs.getString("rut_empleado"));
                data.setNombreEmpleado(rs.getString("nombre_empleado"));
                data.setNombreCargoEmpleado(rs.getString("cargo_empleado"));
                data.setCorrelativo(rs.getInt("correlativo_ausencia"));
                data.setFechaInicioAsStr(rs.getString("fecha_inicio"));
                data.setFechaFinAsStr(rs.getString("fecha_fin"));
                data.setIdAusencia(rs.getInt("ausencia_id"));
                data.setNombreAusencia(rs.getString("ausencia_nombre"));
                data.setPermiteHora(rs.getString("ausencia_por_horas"));
                data.setHoraInicioFullAsStr(rs.getString("hora_inicio"));
                data.setHoraFinFullAsStr(rs.getString("hora_fin"));
                data.setAusenciaAutorizada(rs.getString("ausencia_autorizada"));
                data.setRutAutorizador(rs.getString("rut_autoriza_ausencia"));
                data.setNombreAutorizador(rs.getString("nombre_autorizador"));
                data.setNombreCargoAutorizador(rs.getString("cargo_autorizador"));
                data.setCencoIdEmpleado(rs.getInt("cencoIdEmpleado"));
                data.setCencoNombreEmpleado(rs.getString("cencoNombreEmpleado"));
                lista.add(data);
            }
        }catch(Exception sqle){
            sqle.printStackTrace();
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return lista;
    }

    /**
    * Retorna lista con detalle ausencias
    * 
    * @param _source: indica si el listado de ausencias se muestra desde ausencias-detalle o desde admin vacaciones
    * @param _rutEmpleado
    * @param _rutAutorizador
     * @param _fechaInicio
     * @param _fechaFin
    * @param _ausenciaId
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * @return 
    */
    public List<DetalleAusenciaVO> getDetallesAusencias(String _source,
            String _rutEmpleado,
            String _rutAutorizador, 
            String _fechaInicio, 
            String _fechaFin,
            int _ausenciaId,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<DetalleAusenciaVO> lista = 
                new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        DetalleAusenciaVO data;
        
        try{
            String sql = "SELECT "
                + "detalle_ausencia.correlativo,"
                + "detalle_ausencia.rut_empleado,"
                + "empleado.empresa_id,"
                + "coalesce(empleado.empl_nombres, '') || ' ' || coalesce(empleado.empl_ape_paterno, '') nombre,"
                + "detalle_ausencia.fecha_ingreso,"
                + "to_char(detalle_ausencia.fecha_ingreso, 'yyyy-MM-dd') fecha_ingreso_str,"
                + "detalle_ausencia.ausencia_id,"
                + "ausencia.ausencia_nombre,"
                + "ausencia.ausencia_tipo,"
                + "detalle_ausencia.fecha_inicio,"
                + "detalle_ausencia.fecha_fin,"
                + "to_char(detalle_ausencia.fecha_inicio, 'yyyy-MM-dd') fecha_inicio_str,"
                + "to_char(detalle_ausencia.fecha_fin, 'yyyy-MM-dd') fecha_fin_str,"
                + "to_char(detalle_ausencia.hora_inicio, 'HH24:MI:SS') hora_inicio_str,"
                + "coalesce(to_char(detalle_ausencia.hora_inicio, 'HH24'),'') solohora_inicio_str,"
                + "to_char(detalle_ausencia.hora_inicio, 'MI') solomins_inicio_str,"
                + "to_char(detalle_ausencia.hora_fin, 'HH24:MI:SS') hora_fin_str,"
                + "to_char(detalle_ausencia.hora_fin, 'HH24') solohora_fin_str,"
                + "to_char(detalle_ausencia.hora_fin, 'MI') solomins_fin_str,"    
                + "detalle_ausencia.rut_autoriza_ausencia,"
                + "detalle_ausencia.ausencia_autorizada,"
                + "detalle_ausencia.fecha_actualizacion, "
                + "to_char(detalle_ausencia.fecha_actualizacion, 'dd-MM-yyyy HH24:MI:SS') fecha_actualizacion_str,"
                + "detalle_ausencia.allow_hour, "
                + "vacaciones.dias_acumulados,"
                + "vacaciones.dias_progresivos,"
                + "vacaciones.saldo_dias, "
                + "coalesce(vacaciones.dias_especiales, 'N') dias_especiales,"
                + "detalle_ausencia.dias_efectivos_vacaciones,"
                + "coalesce(cenco.es_zona_extrema,'N') es_zona_extrema,"
                + "coalesce(detalle_ausencia.saldo_dias_vacaciones_asignadas,0) saldo_dias_vacaciones_asignadas,"
                + "coalesce(detalle_ausencia.dias_acumulados_vacaciones_asignadas,0) dias_acumulados_vacaciones_asignadas, "
                    + "coalesce(dias_efectivos_vba,0) dias_efectivos_vba,"
                    + "coalesce(dias_efectivos_vp,0) dias_efectivos_vp,"
                    + "coalesce(saldo_vba_pre_vacaciones,0) saldo_vba_pre_vacaciones, "
                    + "coalesce(saldo_vp_pre_vacaciones,0) saldo_vp_pre_vacaciones, "
                    + "coalesce(saldo_vba_post_vacaciones,0) saldo_vba_post_vacaciones,"
                    + "coalesce(saldo_vp_post_vacaciones,0) saldo_vp_post_vacaciones " 
                + "FROM detalle_ausencia "
                    + "inner join empleado on detalle_ausencia.rut_empleado = empleado.empl_rut "
                    + "inner join ausencia on detalle_ausencia.ausencia_id = ausencia.ausencia_id "
                    + "left outer join vacaciones on "
                    + "(detalle_ausencia.rut_empleado = vacaciones.rut_empleado) "
                    + " inner join centro_costo cenco on (empleado.cenco_id = cenco.ccosto_id) "
                + "WHERE 1 = 1 "
                    + " and (detalle_ausencia.fecha_inicio >= empleado.empl_fec_ini_contrato) " ;//ausencia.ausencia_estado = 1";
            
            if (_rutEmpleado != null && _rutEmpleado.compareTo("-1")!=0){        
                sql += " and detalle_ausencia.rut_empleado = '" + _rutEmpleado + "'";
            }
            if (_rutAutorizador != null && _rutAutorizador.compareTo("-1")!=0){        
                sql += " and detalle_ausencia.rut_autoriza_ausencia = '" + _rutAutorizador + "'";
            }
            if (_fechaInicio != null && _fechaInicio.compareTo("") != 0){        
                if (_fechaFin==null || _fechaFin.compareTo("")==0)
                    _fechaFin = _fechaInicio; 
                sql += " and detalle_ausencia.fecha_inicio "
                    + "between '" + _fechaInicio +"' "
                    + "and '" + _fechaFin +"'";
            }
            if (_ausenciaId != -1){
                sql += " and detalle_ausencia.ausencia_id = " + _ausenciaId;
            }
            
            if (_source != null && _source.compareTo("detalle_ausencias") == 0)
            {   
                sql += " and detalle_ausencia.ausencia_id != " + Constantes.ID_AUSENCIA_VACACIONES;
            }
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            System.out.println(WEB_NAME+"[DetalleAusenciaDAO."
                + "getDetallesAusencias]SQL: " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[DetalleAusenciaDAO.getDetallesAusencias]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new DetalleAusenciaVO();
                data.setCorrelativo(rs.getInt("correlativo"));
                data.setRutEmpleado(rs.getString("rut_empleado"));
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setNombreEmpleado(rs.getString("nombre"));
                data.setFechaIngreso(rs.getDate("fecha_ingreso"));
                data.setFechaIngresoAsStr(rs.getString("fecha_ingreso_str"));
                data.setIdAusencia(rs.getInt("ausencia_id"));
                data.setNombreAusencia(rs.getString("ausencia_nombre"));
                data.setFechaInicio(rs.getDate("fecha_inicio"));
                data.setFechaInicioAsStr(rs.getString("fecha_inicio_str"));
                data.setFechaFin(rs.getDate("fecha_fin"));
                data.setFechaFinAsStr(rs.getString("fecha_fin_str")); 
                
                data.setHoraInicioFullAsStr(rs.getString("hora_inicio_str"));
                data.setHoraFinFullAsStr(rs.getString("hora_fin_str"));
                
                data.setSoloHoraInicio(rs.getString("solohora_inicio_str"));
                data.setSoloMinsInicio(rs.getString("solomins_inicio_str"));
                data.setSoloHoraFin(rs.getString("solohora_fin_str"));
                data.setSoloMinsFin(rs.getString("solomins_fin_str"));
                data.setRutAutorizador(rs.getString("rut_autoriza_ausencia"));
                data.setAusenciaAutorizada(rs.getString("ausencia_autorizada"));
                data.setFechaHoraActualizacion(rs.getString("fecha_actualizacion_str"));
                data.setPermiteHora(rs.getString("allow_hour"));                
                
                String strHoraInicio    = data.getSoloHoraInicio();
                String strMinsInicio    = data.getSoloMinsInicio();
                if (strHoraInicio.compareTo("VACIO")!=0 && strHoraInicio.compareTo("") != 0){
                    System.err.println("[DetalleAusenciaDAO."
                        + "getDetallesAusencias]parseo "
                        + "a entero Hra Inicio: "+strHoraInicio);
                    int intHoraInicio = -1;
                    try{
                        intHoraInicio = Integer.parseInt(strHoraInicio);
                        strHoraInicio = ""+intHoraInicio;
                    }catch(NumberFormatException nex){
                        System.err.println("[DetalleAusenciaDAO."
                            + "getDetallesAusencias]Hra Inicio < 10: "+nex.toString());
                        strHoraInicio = strHoraInicio.substring(strHoraInicio.length()-1);
                    }
                    int intMinsInicio = -1;
                    try{
                        intMinsInicio = Integer.parseInt(strMinsInicio);
                        strMinsInicio = ""+intMinsInicio;
                    }catch(NumberFormatException nex){
                        System.err.println("[DetalleAusenciaDAO."
                            + "getDetallesAusencias]Mins Inicio < 10: "+nex.toString());
                        strMinsInicio = strMinsInicio.substring(strMinsInicio.length()-1);
                    }
                }
                
                String strHoraFin  = data.getSoloHoraFin();
                String strMinsFin    = data.getSoloMinsFin();
                if (strHoraFin!=null){
                    int intHoraFin = -1;
                    try{
                        intHoraFin = Integer.parseInt(strHoraFin);
                        strHoraFin = ""+intHoraFin;
                    }catch(NumberFormatException nex){
                        System.err.println("[DetalleAusenciaDAO."
                            + "getDetallesAusencias]Hra fin < 10: "+nex.toString());
                        strHoraFin = strHoraFin.substring(strHoraFin.length()-1);
                    }
                    
                    int intMinsFin = -1;
                    try{
                        intMinsFin = Integer.parseInt(strMinsFin);
                        strMinsFin = ""+intMinsFin;
                    }catch(NumberFormatException nex){
                        System.err.println("[DetalleAusenciaDAO."
                            + "getDetallesAusencias]Mins Fin < 10: "+nex.toString());
                        strMinsFin = strMinsFin.substring(strMinsFin.length()-1);
                    }
                }
                System.out.println(WEB_NAME+"[DetalleAusenciaDAO.getDetallesAusencias]"
                    + "soloHoraInicio: " +strHoraInicio
                    + ",soloMinsInicio: " +strMinsInicio    
                    + ",soloHoraFin: " +strHoraFin
                    + ",soloMinsFin: " +strMinsFin);
                data.setSoloHoraInicio(strHoraInicio);
                data.setSoloHoraFin(strHoraFin);
                data.setSoloMinsInicio(strMinsInicio);
                data.setSoloMinsFin(strMinsFin);
                
                //tabla vacaciones
                data.setDiasAcumulados(rs.getDouble("dias_acumulados"));
                data.setDiasProgresivos(rs.getDouble("dias_progresivos"));
                data.setSaldoDias(rs.getDouble("saldo_dias"));
                data.setDiasEspeciales(rs.getString("dias_especiales"));
                
                data.setDiasEfectivosVacaciones(rs.getInt("dias_efectivos_vacaciones"));
                data.setEsZonaExtrema(rs.getString("es_zona_extrema"));
                
                //nuevas columnas
                data.setSaldoDiasVacacionesAsignadas(rs.getDouble("saldo_dias_vacaciones_asignadas"));
                data.setDiasAcumuladosVacacionesAsignadas(rs.getDouble("dias_acumulados_vacaciones_asignadas"));        
    
                //nuevas columnas por desglose de dias de vacaciones: VBA y VP
                /*
                    dias_efectivos_vba,
                    dias_efectivos_vp,
                    saldo_vba_pre_vacaciones, 
                    saldo_vp_pre_vacaciones, 
                    saldo_vba_post_vacaciones,
                    saldo_vp_post_vacaciones 
                */
                data.setDiasEfectivosVBA(rs.getInt("dias_efectivos_vba"));
                data.setDiasEfectivosVP(rs.getInt("dias_efectivos_vp"));
                data.setSaldoVBAPreVacaciones(rs.getDouble("saldo_vba_pre_vacaciones"));
                data.setSaldoVPPreVacaciones(rs.getDouble("saldo_vp_pre_vacaciones"));
                data.setSaldoVBAPostVacaciones(rs.getDouble("saldo_vba_post_vacaciones"));
                data.setSaldoVPPostVacaciones(rs.getDouble("saldo_vp_post_vacaciones"));
                
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
    * Retorna lista con detalle ausencias del tipo Permiso administrativo
    * 
    * @param _source: indica si el listado de ausencias se muestra desde ausencias-detalle o desde admin PA
    * @param _rutEmpleado
    * @param _rutAutorizador
    * @param _fechaIngresoInicio
    * @param _fechaIngresoFin
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * @return 
    */
    public List<DetalleAusenciaVO> getPermisosAdministrativos(String _source,
            String _rutEmpleado,
            String _rutAutorizador, 
            String _fechaIngresoInicio, 
            String _fechaIngresoFin,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<DetalleAusenciaVO> lista = 
                new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        DetalleAusenciaVO data;
        
        try{
            String sql = "SELECT "
                + "detalle_ausencia.correlativo,"
                + "detalle_ausencia.rut_empleado,"
                + "empleado.empresa_id,"
                + "coalesce(empleado.empl_nombres, '') || ' ' || coalesce(empleado.empl_ape_paterno, '') nombre,"
                + "detalle_ausencia.fecha_ingreso,"
                + "to_char(detalle_ausencia.fecha_ingreso, 'yyyy-MM-dd') fecha_ingreso_str,"
                + "detalle_ausencia.ausencia_id,"
                + "ausencia.ausencia_nombre,"
                + "ausencia.ausencia_tipo,"
                + "detalle_ausencia.fecha_inicio,"
                + "detalle_ausencia.fecha_fin,"
                + "to_char(detalle_ausencia.fecha_inicio, 'yyyy-MM-dd') fecha_inicio_str,"
                + "to_char(detalle_ausencia.fecha_fin, 'yyyy-MM-dd') fecha_fin_str,"
                + "to_char(detalle_ausencia.hora_inicio, 'HH24:MI:SS') hora_inicio_str,"
                + "coalesce(to_char(detalle_ausencia.hora_inicio, 'HH24'),'') solohora_inicio_str,"
                + "to_char(detalle_ausencia.hora_inicio, 'MI') solomins_inicio_str,"
                + "to_char(detalle_ausencia.hora_fin, 'HH24:MI:SS') hora_fin_str,"
                + "to_char(detalle_ausencia.hora_fin, 'HH24') solohora_fin_str,"
                + "to_char(detalle_ausencia.hora_fin, 'MI') solomins_fin_str,"    
                + "detalle_ausencia.rut_autoriza_ausencia,"
                + "detalle_ausencia.ausencia_autorizada,"
                + "detalle_ausencia.fecha_actualizacion, "
                + "to_char(detalle_ausencia.fecha_actualizacion, 'dd-MM-yyyy HH24:MI:SS') fecha_actualizacion_str,"
                + "detalle_ausencia.allow_hour, "
                + "coalesce(cenco.es_zona_extrema,'N') es_zona_extrema,"
                + "coalesce(detalle_ausencia.dias_solicitados,0) dias_solicitados, "
                + "coalesce(detalle_ausencia.saldo_post_pa,0) saldo_post_pa, "
                + "coalesce(detalle_ausencia.saldo_post_pesp,0) saldo_post_pesp "    
                + "FROM detalle_ausencia "
                    + "inner join empleado on detalle_ausencia.rut_empleado = empleado.empl_rut "
                    + "inner join ausencia on detalle_ausencia.ausencia_id = ausencia.ausencia_id "
                    + " inner join centro_costo cenco on (empleado.cenco_id = cenco.ccosto_id) "
                + "WHERE 1 = 1 "
                    + " and (detalle_ausencia.fecha_inicio >= empleado.empl_fec_ini_contrato) " ;//ausencia.ausencia_estado = 1";
            
            if (_rutEmpleado != null && _rutEmpleado.compareTo("-1")!=0){        
                sql += " and detalle_ausencia.rut_empleado = '" + _rutEmpleado + "'";
            }
            if (_rutAutorizador != null && _rutAutorizador.compareTo("-1")!=0){        
                sql += " and detalle_ausencia.rut_autoriza_ausencia = '" + _rutAutorizador + "'";
            }
            if (_fechaIngresoInicio != null && _fechaIngresoInicio.compareTo("") != 0){        
                if (_fechaIngresoFin==null || _fechaIngresoFin.compareTo("")==0)
                    _fechaIngresoFin = _fechaIngresoInicio; 
                sql += " and detalle_ausencia.fecha_inicio "
                    + "between '" + _fechaIngresoInicio +"' "
                    + "and '" + _fechaIngresoFin +"'";
            }
            sql += " and detalle_ausencia.ausencia_id = " + Constantes.ID_AUSENCIA_PERMISO_ADMINISTRATIVO;
            
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            System.out.println(WEB_NAME+"[DetalleAusenciaDAO."
                + "getPermisosAdministrativos]SQL: " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[DetalleAusenciaDAO.getPermisosAdministrativos]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new DetalleAusenciaVO();
                data.setCorrelativo(rs.getInt("correlativo"));
                data.setRutEmpleado(rs.getString("rut_empleado"));
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setNombreEmpleado(rs.getString("nombre"));
                data.setFechaIngreso(rs.getDate("fecha_ingreso"));
                data.setFechaIngresoAsStr(rs.getString("fecha_ingreso_str"));
                data.setIdAusencia(rs.getInt("ausencia_id"));
                data.setNombreAusencia(rs.getString("ausencia_nombre"));
                data.setFechaInicio(rs.getDate("fecha_inicio"));
                data.setFechaInicioAsStr(rs.getString("fecha_inicio_str"));
                data.setFechaFin(rs.getDate("fecha_fin"));
                data.setFechaFinAsStr(rs.getString("fecha_fin_str")); 
                
                data.setHoraInicioFullAsStr(rs.getString("hora_inicio_str"));
                data.setHoraFinFullAsStr(rs.getString("hora_fin_str"));
                
                data.setSoloHoraInicio(rs.getString("solohora_inicio_str"));
                data.setSoloMinsInicio(rs.getString("solomins_inicio_str"));
                data.setSoloHoraFin(rs.getString("solohora_fin_str"));
                data.setSoloMinsFin(rs.getString("solomins_fin_str"));
                data.setRutAutorizador(rs.getString("rut_autoriza_ausencia"));
                data.setAusenciaAutorizada(rs.getString("ausencia_autorizada"));
                data.setFechaHoraActualizacion(rs.getString("fecha_actualizacion_str"));
                data.setPermiteHora(rs.getString("allow_hour"));                
                
                System.out.println(WEB_NAME+"[DetalleAusenciaDAO."
                    + "getPermisosAdministrativos]"
                    + "Correlativo ausencia: " + data.getCorrelativo()
                    + ", rutAutorizador: " + data.getRutAutorizador());
                
                String strHoraInicio    = data.getSoloHoraInicio();
                String strMinsInicio    = data.getSoloMinsInicio();
                if (strHoraInicio.compareTo("VACIO")!=0 && strHoraInicio.compareTo("") != 0){
                    System.err.println("[DetalleAusenciaDAO."
                        + "getPermisosAdministrativos]parseo "
                        + "a entero Hra Inicio: "+strHoraInicio);
                    int intHoraInicio = -1;
                    try{
                        intHoraInicio = Integer.parseInt(strHoraInicio);
                        strHoraInicio = ""+intHoraInicio;
                    }catch(NumberFormatException nex){
                        System.err.println("[DetalleAusenciaDAO."
                            + "getPermisosAdministrativos]Hra Inicio < 10: "+nex.toString());
                        strHoraInicio = strHoraInicio.substring(strHoraInicio.length()-1);
                    }
                    int intMinsInicio = -1;
                    try{
                        intMinsInicio = Integer.parseInt(strMinsInicio);
                        strMinsInicio = ""+intMinsInicio;
                    }catch(NumberFormatException nex){
                        System.err.println("[DetalleAusenciaDAO."
                            + "getPermisosAdministrativos]Mins Inicio < 10: "+nex.toString());
                        strMinsInicio = strMinsInicio.substring(strMinsInicio.length()-1);
                    }
                }
                
                String strHoraFin  = data.getSoloHoraFin();
                String strMinsFin    = data.getSoloMinsFin();
                if (strHoraFin!=null){
                    int intHoraFin = -1;
                    try{
                        intHoraFin = Integer.parseInt(strHoraFin);
                        strHoraFin = ""+intHoraFin;
                    }catch(NumberFormatException nex){
                        System.err.println("[DetalleAusenciaDAO."
                            + "getPermisosAdministrativos]Hra fin < 10: "+nex.toString());
                        strHoraFin = strHoraFin.substring(strHoraFin.length()-1);
                    }
                    
                    int intMinsFin = -1;
                    try{
                        intMinsFin = Integer.parseInt(strMinsFin);
                        strMinsFin = ""+intMinsFin;
                    }catch(NumberFormatException nex){
                        System.err.println("[DetalleAusenciaDAO."
                            + "getPermisosAdministrativos]Mins Fin < 10: "+nex.toString());
                        strMinsFin = strMinsFin.substring(strMinsFin.length()-1);
                    }
                }
                System.out.println(WEB_NAME+"[DetalleAusenciaDAO.getPermisosAdministrativos]"
                    + "soloHoraInicio: " +strHoraInicio
                    + ",soloMinsInicio: " +strMinsInicio    
                    + ",soloHoraFin: " +strHoraFin
                    + ",soloMinsFin: " +strMinsFin);
                data.setSoloHoraInicio(strHoraInicio);
                data.setSoloHoraFin(strHoraFin);
                data.setSoloMinsInicio(strMinsInicio);
                data.setSoloMinsFin(strMinsFin);
                
                data.setDiasSolicitados(rs.getDouble("dias_solicitados"));
                data.setSaldoPostPA(rs.getDouble("saldo_post_pa"));
                data.setSaldoPostPESP(rs.getInt("saldo_post_pesp"));
                
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
    * Retorna lista con detalle ausencias del tipo Permiso Examen Salud Preventiva
    * 
    * @param _source: indica si el listado de ausencias se muestra desde ausencias-detalle o desde admin PESP
    * @param _rutEmpleado
    * @param _rutAutorizador
    * @param _fechaIngresoInicio
    * @param _fechaIngresoFin
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * @return 
    */
    public List<DetalleAusenciaVO> getPermisosExamenSaludPreventiva(String _source,
            String _rutEmpleado,
            String _rutAutorizador, 
            String _fechaIngresoInicio, 
            String _fechaIngresoFin,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<DetalleAusenciaVO> lista = 
                new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        DetalleAusenciaVO data;
        
        try{
            String sql = "SELECT "
                + "detalle_ausencia.correlativo,"
                + "detalle_ausencia.rut_empleado,"
                + "empleado.empresa_id,"
                + "coalesce(empleado.empl_nombres, '') || ' ' || coalesce(empleado.empl_ape_paterno, '') nombre,"
                + "detalle_ausencia.fecha_ingreso,"
                + "to_char(detalle_ausencia.fecha_ingreso, 'yyyy-MM-dd') fecha_ingreso_str,"
                + "detalle_ausencia.ausencia_id,"
                + "ausencia.ausencia_nombre,"
                + "ausencia.ausencia_tipo,"
                + "detalle_ausencia.fecha_inicio,"
                + "detalle_ausencia.fecha_fin,"
                + "to_char(detalle_ausencia.fecha_inicio, 'yyyy-MM-dd') fecha_inicio_str,"
                + "to_char(detalle_ausencia.fecha_fin, 'yyyy-MM-dd') fecha_fin_str,"
                + "to_char(detalle_ausencia.hora_inicio, 'HH24:MI:SS') hora_inicio_str,"
                + "coalesce(to_char(detalle_ausencia.hora_inicio, 'HH24'),'VACIO') solohora_inicio_str,"
                + "to_char(detalle_ausencia.hora_inicio, 'MI') solomins_inicio_str,"
                + "to_char(detalle_ausencia.hora_fin, 'HH24:MI:SS') hora_fin_str,"
                + "to_char(detalle_ausencia.hora_fin, 'HH24') solohora_fin_str,"
                + "to_char(detalle_ausencia.hora_fin, 'MI') solomins_fin_str,"    
                + "detalle_ausencia.rut_autoriza_ausencia,"
                + "detalle_ausencia.ausencia_autorizada,"
                + "detalle_ausencia.fecha_actualizacion, "
                + "to_char(detalle_ausencia.fecha_actualizacion, 'dd-MM-yyyy HH24:MI:SS') fecha_actualizacion_str,"
                + "detalle_ausencia.allow_hour, "
                + "coalesce(cenco.es_zona_extrema,'N') es_zona_extrema,"
                + "coalesce(detalle_ausencia.dias_solicitados,0) dias_solicitados, "
                + "coalesce(detalle_ausencia.saldo_post_pa,0) saldo_post_pa, "
                + "coalesce(detalle_ausencia.saldo_post_pesp,0) saldo_post_pesp "    
                + "FROM detalle_ausencia "
                    + "inner join empleado on detalle_ausencia.rut_empleado = empleado.empl_rut "
                    + "inner join ausencia on detalle_ausencia.ausencia_id = ausencia.ausencia_id "
                    + " inner join centro_costo cenco on (empleado.cenco_id = cenco.ccosto_id) "
                + "WHERE 1 = 1 "
                    + " and (detalle_ausencia.fecha_inicio >= empleado.empl_fec_ini_contrato) " ;//ausencia.ausencia_estado = 1";
            
            if (_rutEmpleado != null && _rutEmpleado.compareTo("-1")!=0){        
                sql += " and detalle_ausencia.rut_empleado = '" + _rutEmpleado + "'";
            }
            if (_rutAutorizador != null && _rutAutorizador.compareTo("-1")!=0){        
                sql += " and detalle_ausencia.rut_autoriza_ausencia = '" + _rutAutorizador + "'";
            }
            if (_fechaIngresoInicio != null && _fechaIngresoInicio.compareTo("") != 0){        
                if (_fechaIngresoFin==null || _fechaIngresoFin.compareTo("")==0)
                    _fechaIngresoFin = _fechaIngresoInicio; 
                sql += " and detalle_ausencia.fecha_inicio "
                    + "between '" + _fechaIngresoInicio +"' "
                    + "and '" + _fechaIngresoFin +"'";
            }
            sql += " and detalle_ausencia.ausencia_id = " + Constantes.ID_AUSENCIA_PERMISO_EXAMEN_SALUD_PREVENTIVA;
            
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            System.out.println(WEB_NAME+"[DetalleAusenciaDAO."
                + "getPermisosExamenSaludPreventiva]SQL: " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[DetalleAusenciaDAO.getPermisosExamenSaludPreventiva]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new DetalleAusenciaVO();
                data.setCorrelativo(rs.getInt("correlativo"));
                data.setRutEmpleado(rs.getString("rut_empleado"));
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setNombreEmpleado(rs.getString("nombre"));
                data.setFechaIngreso(rs.getDate("fecha_ingreso"));
                data.setFechaIngresoAsStr(rs.getString("fecha_ingreso_str"));
                data.setIdAusencia(rs.getInt("ausencia_id"));
                data.setNombreAusencia(rs.getString("ausencia_nombre"));
                data.setFechaInicio(rs.getDate("fecha_inicio"));
                data.setFechaInicioAsStr(rs.getString("fecha_inicio_str"));
                data.setFechaFin(rs.getDate("fecha_fin"));
                data.setFechaFinAsStr(rs.getString("fecha_fin_str")); 
                
                data.setHoraInicioFullAsStr(rs.getString("hora_inicio_str"));
                data.setHoraFinFullAsStr(rs.getString("hora_fin_str"));
                
                data.setSoloHoraInicio(rs.getString("solohora_inicio_str"));
                data.setSoloMinsInicio(rs.getString("solomins_inicio_str"));
                data.setSoloHoraFin(rs.getString("solohora_fin_str"));
                data.setSoloMinsFin(rs.getString("solomins_fin_str"));
                data.setRutAutorizador(rs.getString("rut_autoriza_ausencia"));
                data.setAusenciaAutorizada(rs.getString("ausencia_autorizada"));
                data.setFechaHoraActualizacion(rs.getString("fecha_actualizacion_str"));
                data.setPermiteHora(rs.getString("allow_hour"));                
                
                System.out.println(WEB_NAME+"[DetalleAusenciaDAO."
                    + "getPermisosExamenSaludPreventiva]"
                    + "Correlativo ausencia: " + data.getCorrelativo()
                    + ", rutAutorizador: " + data.getRutAutorizador());
                
                String strHoraInicio    = data.getSoloHoraInicio();
                String strMinsInicio    = data.getSoloMinsInicio();
                if (strHoraInicio.compareTo("VACIO")!=0 && strHoraInicio.compareTo("") != 0){
                    System.err.println("[DetalleAusenciaDAO."
                        + "getPermisosExamenSaludPreventiva]parseo "
                        + "a entero Hra Inicio: "+strHoraInicio);
                    int intHoraInicio = -1;
                    try{
                        intHoraInicio = Integer.parseInt(strHoraInicio);
                        strHoraInicio = ""+intHoraInicio;
                    }catch(NumberFormatException nex){
                        System.err.println("[DetalleAusenciaDAO."
                            + "getPermisosExamenSaludPreventiva]Hra Inicio < 10: "+nex.toString());
                        strHoraInicio = strHoraInicio.substring(strHoraInicio.length()-1);
                    }
                    int intMinsInicio = -1;
                    try{
                        intMinsInicio = Integer.parseInt(strMinsInicio);
                        strMinsInicio = ""+intMinsInicio;
                    }catch(NumberFormatException nex){
                        System.err.println("[DetalleAusenciaDAO."
                            + "getPermisosExamenSaludPreventiva]Mins Inicio < 10: "+nex.toString());
                        strMinsInicio = strMinsInicio.substring(strMinsInicio.length()-1);
                    }
                }
                
                String strHoraFin  = data.getSoloHoraFin();
                String strMinsFin    = data.getSoloMinsFin();
                if (strHoraFin!=null){
                    int intHoraFin = -1;
                    try{
                        intHoraFin = Integer.parseInt(strHoraFin);
                        strHoraFin = ""+intHoraFin;
                    }catch(NumberFormatException nex){
                        System.err.println("[DetalleAusenciaDAO."
                            + "getPermisosExamenSaludPreventiva]Hra fin < 10: "+nex.toString());
                        strHoraFin = strHoraFin.substring(strHoraFin.length()-1);
                    }
                    
                    int intMinsFin = -1;
                    try{
                        intMinsFin = Integer.parseInt(strMinsFin);
                        strMinsFin = ""+intMinsFin;
                    }catch(NumberFormatException nex){
                        System.err.println("[DetalleAusenciaDAO."
                            + "getPermisosExamenSaludPreventiva]Mins Fin < 10: "+nex.toString());
                        strMinsFin = strMinsFin.substring(strMinsFin.length()-1);
                    }
                }
                System.out.println(WEB_NAME+"[DetalleAusenciaDAO.getPermisosExamenSaludPreventiva]"
                    + "soloHoraInicio: " +strHoraInicio
                    + ",soloMinsInicio: " +strMinsInicio    
                    + ",soloHoraFin: " +strHoraFin
                    + ",soloMinsFin: " +strMinsFin);
                data.setSoloHoraInicio(strHoraInicio);
                data.setSoloHoraFin(strHoraFin);
                data.setSoloMinsInicio(strMinsInicio);
                data.setSoloMinsFin(strMinsFin);
                
                data.setDiasSolicitados(rs.getDouble("dias_solicitados"));
                data.setSaldoPostPA(rs.getDouble("saldo_post_pa"));
                data.setSaldoPostPESP(rs.getInt("saldo_post_pesp"));
                
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
    * Retorna lista con vacaciones
    * 
    * @param _rutEmpleado
    * @param _anioMesInicio
    * @return 
    */
    public List<DetalleAusenciaVO> getVacacionesByAnioMesInicio(String _rutEmpleado,
            String _anioMesInicio){
        
        List<DetalleAusenciaVO> lista = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        DetalleAusenciaVO data;
        try{
            String sql = "select "
                    + "da.correlativo,"
                    + "da.rut_empleado, "
                    + "da.ausencia_id,"
                    + "au.ausencia_nombre,"
                    + "to_char(da.fecha_inicio, 'yyyy-MM-dd') fecha_inicio_str,"
                    + "to_char(da.fecha_fin, 'yyyy-MM-dd') fecha_fin_str, "
                    + "dias_efectivos_vacaciones "
                + "from detalle_ausencia da "
                    + "inner join ausencia au on (da.ausencia_id = au.ausencia_id) "
                + "where da.rut_empleado = '" + _rutEmpleado + "' "
                + "and da.ausencia_id = " + Constantes.ID_AUSENCIA_VACACIONES
                + " and to_char(da.fecha_inicio,'yyyy-MM') = '" + _anioMesInicio + "' "
                + " order by da.fecha_inicio"; 
            
            System.out.println(WEB_NAME+"[DetalleAusenciaDAO."
                + "getDetallesAusencias]SQL: " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[DetalleAusenciaDAO.getVacacionesByAnioMesInicio]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new DetalleAusenciaVO();
                data.setCorrelativo(rs.getInt("correlativo"));
                data.setRutEmpleado(rs.getString("rut_empleado"));
                data.setIdAusencia(rs.getInt("ausencia_id"));
                data.setNombreAusencia(rs.getString("ausencia_nombre"));
                data.setFechaInicioAsStr(rs.getString("fecha_inicio_str"));
                data.setFechaFinAsStr(rs.getString("fecha_fin_str"));
                data.setDiasEfectivosVacaciones(rs.getInt("dias_efectivos_vacaciones"));
                
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
                System.err.println("[DetalleAusenciaDAO.getVacacionesByAnioMesInicio]"
                    + "Error: "+ex.toString());
            }
        }
        return lista;
    }
    
    /**
    * Retorna lista con Permisos Administrativos
    * 
    * @param _rutEmpleado
    * @param _anioMesInicio
    * @return 
    */
    public List<DetalleAusenciaVO> getPermisosAdministrativosByAnioMesInicio(String _rutEmpleado,
            String _anioMesInicio){
        
        List<DetalleAusenciaVO> lista = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        DetalleAusenciaVO data;
        try{
            String sql = "select "
                    + "da.correlativo,"
                    + "da.rut_empleado, "
                    + "da.ausencia_id,"
                    + "au.ausencia_nombre,"
                    + "to_char(da.fecha_inicio, 'yyyy-MM-dd') fecha_inicio_str,"
                    + "to_char(da.fecha_fin, 'yyyy-MM-dd') fecha_fin_str, "
                    + "dias_solicitados "
                + "from detalle_ausencia da "
                    + "inner join ausencia au on (da.ausencia_id = au.ausencia_id) "
                + "where da.rut_empleado = '" + _rutEmpleado + "' "
                + "and da.ausencia_id = " + Constantes.ID_AUSENCIA_PERMISO_ADMINISTRATIVO
                + " and to_char(da.fecha_inicio,'yyyy-MM') = '" + _anioMesInicio + "' "
                + " order by da.fecha_inicio"; 
            
            System.out.println(WEB_NAME+"[DetalleAusenciaDAO."
                + "getPermisosAdministrativosByAnioMesInicio]SQL: " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[DetalleAusenciaDAO.getPermisosAdministrativosByAnioMesInicio]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new DetalleAusenciaVO();
                data.setCorrelativo(rs.getInt("correlativo"));
                data.setRutEmpleado(rs.getString("rut_empleado"));
                data.setIdAusencia(rs.getInt("ausencia_id"));
                data.setNombreAusencia(rs.getString("ausencia_nombre"));
                data.setFechaInicioAsStr(rs.getString("fecha_inicio_str"));
                data.setFechaFinAsStr(rs.getString("fecha_fin_str"));
                data.setDiasSolicitados(rs.getDouble("dias_solicitados"));
                
                lista.add(data);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[DetalleAusenciaDAO."
                    + "getPermisosAdministrativosByAnioMesInicio]"
                    + "Error_2: " + sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[DetalleAusenciaDAO."
                    + "getPermisosAdministrativosByAnioMesInicio]"
                    + "Error_1: " + ex.toString());
            }
        }
        return lista;
    }
    
    /**
    * Retorna lista con Permisos Examen salud preventiva
    * 
    * @param _rutEmpleado
    * @param _anioMesInicio
    * @return 
    */
    public List<DetalleAusenciaVO> getPermisosExamenSaludPreventivaByAnioMesInicio(String _rutEmpleado,
            String _anioMesInicio){
        
        List<DetalleAusenciaVO> lista = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        DetalleAusenciaVO data;
        try{
            String sql = "select "
                    + "da.correlativo,"
                    + "da.rut_empleado, "
                    + "da.ausencia_id,"
                    + "au.ausencia_nombre,"
                    + "to_char(da.fecha_inicio, 'yyyy-MM-dd') fecha_inicio_str,"
                    + "to_char(da.fecha_fin, 'yyyy-MM-dd') fecha_fin_str, "
                    + "dias_solicitados "
                + "from detalle_ausencia da "
                    + "inner join ausencia au on (da.ausencia_id = au.ausencia_id) "
                + "where da.rut_empleado = '" + _rutEmpleado + "' "
                + "and da.ausencia_id = " + Constantes.ID_AUSENCIA_PERMISO_EXAMEN_SALUD_PREVENTIVA
                + " and to_char(da.fecha_inicio,'yyyy-MM') = '" + _anioMesInicio + "' "
                + " order by da.fecha_inicio"; 
            
            System.out.println(WEB_NAME+"[DetalleAusenciaDAO."
                + "getPermisosExamenSaludPreventivaByAnioMesInicio]SQL: " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[DetalleAusenciaDAO.getPermisosExamenSaludPreventivaByAnioMesInicio]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new DetalleAusenciaVO();
                data.setCorrelativo(rs.getInt("correlativo"));
                data.setRutEmpleado(rs.getString("rut_empleado"));
                data.setIdAusencia(rs.getInt("ausencia_id"));
                data.setNombreAusencia(rs.getString("ausencia_nombre"));
                data.setFechaInicioAsStr(rs.getString("fecha_inicio_str"));
                data.setFechaFinAsStr(rs.getString("fecha_fin_str"));
                data.setDiasSolicitados(rs.getDouble("dias_solicitados"));
                
                lista.add(data);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[DetalleAusenciaDAO."
                    + "getPermisosExamenSaludPreventivaByAnioMesInicio]"
                    + "Error_2: " + sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[DetalleAusenciaDAO."
                    + "getPermisosExamenSaludPreventivaByAnioMesInicio]"
                    + "Error_1: " + ex.toString());
            }
        }
        return lista;
    }
    
    /**
    * Retorna lista con detalle ausencias
    * 
    * @param _usuario
    * @param _rowLimit
    * @return 
    */
    public List<DetalleAusenciaVO> getUltimasAusencias(UsuarioVO _usuario, 
            int _rowLimit){
        
        List<DetalleAusenciaVO> lista = 
                new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        DetalleAusenciaVO data;
        String strcencos = "-1";
        try{
            List cencos = _usuario.getCencos();
            if (_usuario.getAdminEmpresa().compareTo("N") == 0 && cencos.size() > 0){
                strcencos = "";
                Iterator<UsuarioCentroCostoVO> cencosIt = cencos.iterator();
                while (cencosIt.hasNext()) {
                    strcencos += cencosIt.next().getCcostoId()+",";
                }
                strcencos = strcencos.substring(0, strcencos.length()-1);
            }
            
            String sql = "SELECT "
                + "da.correlativo,"
                + "da.rut_empleado,"
                + "empleado.nombre,"
                + "empleado.empresa_id,"
                + "empleado.empresa_nombre," 
                + "empleado.depto_nombre," 
                + "empleado.ccosto_nombre,"
                + "da.fecha_ingreso,"
                + "to_char(da.fecha_ingreso, 'yyyy-MM-dd') fecha_ingreso_str,"
                + "da.ausencia_id,"
                + "ausencia.ausencia_nombre,"
                + "to_char(da.fecha_inicio, 'yyyy-MM-dd') fecha_inicio_str,"
                + "to_char(da.fecha_fin, 'yyyy-MM-dd') fecha_fin_str,"
                + "da.rut_autoriza_ausencia autorizador_rut,"
                    + "autorizador.nombre autorizador_nombre,"
                    + "autorizador.ccosto_nombre autorizador_cenco,"
                    + "vacaciones.saldo_dias, "
                    + "coalesce(to_char(hora_inicio,'HH24:MI:SS'),'') hora_inicio,"
                    + "coalesce(to_char(hora_fin,'HH24:MI:SS'),'') hora_fin "
                    + "FROM detalle_ausencia da "
                    + "inner join view_empleado empleado on da.rut_empleado = empleado.rut "
                    + "inner join ausencia on da.ausencia_id = ausencia.ausencia_id "
                    + "inner join view_empleado autorizador on da.rut_autoriza_ausencia = autorizador.rut "
                    + "left outer join vacaciones on "
                    + " (da.rut_empleado = vacaciones.rut_empleado) "
                + "WHERE 1 = 1 ";
            if (_usuario.getIdPerfil() != Constantes.ID_PERFIL_SUPER_ADMIN 
                    && _usuario.getAdminEmpresa().compareTo("S") == 0){ 
                sql += " and (empleado.empresa_id = '" + _usuario.getEmpresaId() + "') ";
            }
            
            if (strcencos.compareTo("-1") != 0){
                sql += " and (empleado.cenco_id in (" + strcencos + ")) ";
            }
            
            sql += " order by da.fecha_inicio desc limit " + _rowLimit;
            
            System.out.println(WEB_NAME+"gestionweb."
                + "service.DetalleAusenciaDAO."
                + "getUltimasAusencias(). SQL: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[DetalleAusenciaDAO.getUltimasAusencias]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new DetalleAusenciaVO();
                
                data.setCorrelativo(rs.getInt("correlativo"));
                data.setRutEmpleado(rs.getString("rut_empleado"));
                data.setNombreEmpleado(rs.getString("nombre"));
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setEmpresaNombre(rs.getString("empresa_nombre"));
                data.setDeptoNombre(rs.getString("depto_nombre"));
                data.setCencoNombre(rs.getString("ccosto_nombre"));
                data.setFechaIngreso(rs.getDate("fecha_ingreso"));
                data.setFechaIngresoAsStr(rs.getString("fecha_ingreso_str"));
                data.setIdAusencia(rs.getInt("ausencia_id"));
                data.setNombreAusencia(rs.getString("ausencia_nombre"));
                data.setFechaInicioAsStr(rs.getString("fecha_inicio_str"));
                data.setFechaFinAsStr(rs.getString("fecha_fin_str")); 
                data.setRutAutorizador(rs.getString("autorizador_rut"));
                data.setNombreAutorizador(rs.getString("autorizador_nombre"));
                data.setNomCencoAutorizador(rs.getString("autorizador_cenco"));                
                data.setSaldoDias(rs.getDouble("saldo_dias"));
                data.setHoraInicioFullAsStr(rs.getString("hora_inicio"));
                data.setHoraFinFullAsStr(rs.getString("hora_fin"));
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
    * Retorna lista con detalle ausencias
    * 
    * @param _empresaId
     * @param _cencosUsuario
    * @return 
    */
    public LinkedHashMap<Integer, DetalleAusenciaVO> getAusenciasHoy(String _empresaId, 
        List<UsuarioCentroCostoVO> _cencosUsuario){
        LinkedHashMap<Integer, DetalleAusenciaVO> hashAusencias = 
            new LinkedHashMap<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        DetalleAusenciaVO inasistencia;
        try{
            String ccostoIds = _cencosUsuario.stream()
                .map(UsuarioCentroCostoVO::getCcostoId)
                .map(String::valueOf) // por si ccostoId no es String
                .collect(Collectors.joining(","));
            String sql = "SELECT "
                    + "da.correlativo,"
                    + "to_char(da.fecha_inicio, 'yyyy-MM-dd') fecha_inicio_str,"
                    + "to_char(da.fecha_fin, 'yyyy-MM-dd') fecha_fin_str,"
                    + "da.rut_empleado,"
                    + "upper(empleado.empl_nombres || ' ' "
                    + "|| empleado.empl_ape_paterno || ' ' "
                    + "|| empleado.empl_ape_materno) nombre,"
                    + "da.ausencia_id,"
                    + "ausencia.ausencia_nombre,"
                    + "empleado.cenco_id cenco_id, "
                    + "cc.ccosto_nombre cenco_nombre "
                + "FROM detalle_ausencia da "
                    + " left outer join ausencia on da.ausencia_id = ausencia.ausencia_id "
                    + " inner join empleado on da.rut_empleado = empleado.empl_rut "
                    + " inner join centro_costo cc on (empleado.cenco_id = cc.ccosto_id) ";
                   
                if (!_cencosUsuario.isEmpty()){
                    sql += " and empleado.cenco_id in (" + ccostoIds + ") ";
                }
                    
                if (_empresaId != null && _empresaId.compareTo("-1") != 0){    
                    sql += " and empleado.empresa_id = '" + _empresaId + "' ";
                }
                sql += " WHERE current_date BETWEEN da.fecha_inicio AND da.fecha_fin "
                + "order by da.fecha_inicio desc";
                    
            System.out.println("[DetalleAusenciaDAO.getAusenciasHoy]Sql: " + sql); 
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[DetalleAusenciaDAO.getAusenciasHoy]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                inasistencia = new DetalleAusenciaVO();
                
                inasistencia.setCorrelativo(rs.getInt("correlativo"));
                inasistencia.setFechaInicioAsStr(rs.getString("fecha_inicio_str"));
                inasistencia.setFechaFinAsStr(rs.getString("fecha_fin_str"));
                inasistencia.setRutEmpleado(rs.getString("rut_empleado"));
                inasistencia.setNombreEmpleado(rs.getString("nombre"));
                inasistencia.setIdAusencia(rs.getInt("ausencia_id"));
                inasistencia.setNombreAusencia(rs.getString("ausencia_nombre"));
                inasistencia.setCencoNombre(rs.getString("cenco_nombre"));
                hashAusencias.put(inasistencia.getCorrelativo(),inasistencia);
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
                System.err.println("[DetalleAusenciaDAO.getAusenciasHoy]"
                    + "Error: " + ex.toString());
            }
        }
        return hashAusencias;
    }
    
    /**
     * Retorna lista con detalle ausencias historicas
     * 
     * @param _rutEmpleado
     * @param _rutAutorizador
     * @param _fechaIngresoInicio
     * @param _fechaIngresoFin
     * @param _ausenciaId
     * @param _jtStartIndex
     * @param _jtPageSize
     * @param _jtSorting
     * @return 
     */
    public List<DetalleAusenciaVO> getDetallesAusenciasHist(
            String _rutEmpleado,
            String _rutAutorizador, 
            String _fechaIngresoInicio, 
            String _fechaIngresoFin,
            int _ausenciaId,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<DetalleAusenciaVO> lista = 
                new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        DetalleAusenciaVO data;
        
        try{
            String sql = "SELECT "
                + "detalle_ausencia.correlativo,"
                + "detalle_ausencia.rut_empleado,"
                + "empleado.empresa_id,"
                + "coalesce(empleado.empl_nombres, '') || ' ' || coalesce(empleado.empl_ape_paterno, '') nombre,"
                + "detalle_ausencia.fecha_ingreso,"
                + "to_char(detalle_ausencia.fecha_ingreso, 'yyyy-MM-dd') fecha_ingreso_str,"
                + "detalle_ausencia.ausencia_id,"
                + "ausencia.ausencia_nombre,"
                + "ausencia.ausencia_tipo,"
                + "detalle_ausencia.fecha_inicio,"
                + "detalle_ausencia.fecha_fin,"
                + "to_char(detalle_ausencia.fecha_inicio, 'yyyy-MM-dd') fecha_inicio_str,"
                + "to_char(detalle_ausencia.fecha_fin, 'yyyy-MM-dd') fecha_fin_str,"
                + "to_char(detalle_ausencia.hora_inicio, 'HH24:MI:SS') hora_inicio_str,"
                + "coalesce(to_char(detalle_ausencia.hora_inicio, 'HH24'),'') solohora_inicio_str,"
                + "to_char(detalle_ausencia.hora_inicio, 'MI') solomins_inicio_str,"
                + "to_char(detalle_ausencia.hora_fin, 'HH24:MI:SS') hora_fin_str,"
                + "to_char(detalle_ausencia.hora_fin, 'HH24') solohora_fin_str,"
                + "to_char(detalle_ausencia.hora_fin, 'MI') solomins_fin_str,"    
                + "detalle_ausencia.rut_autoriza_ausencia,"
                + "detalle_ausencia.ausencia_autorizada,"
                + "detalle_ausencia.fecha_actualizacion, "
                + "to_char(detalle_ausencia.fecha_actualizacion, 'dd-MM-yyyy HH24:MI:SS') fecha_actualizacion_str,"
                + "detalle_ausencia.allow_hour "
                + "FROM detalle_ausencia_historica detalle_ausencia "
                + "inner join empleado on detalle_ausencia.rut_empleado = empleado.empl_rut "
                + "inner join ausencia on detalle_ausencia.ausencia_id = ausencia.ausencia_id "
                + "WHERE 1 = 1 " ;//ausencia.ausencia_estado = 1";
            
            if (_rutEmpleado != null && _rutEmpleado.compareTo("-1")!=0){        
                sql += " and detalle_ausencia.rut_empleado = '" + _rutEmpleado + "'";
            }
            if (_rutAutorizador != null && _rutAutorizador.compareTo("-1")!=0){        
                sql += " and detalle_ausencia.rut_autoriza_ausencia = '" + _rutAutorizador + "'";
            }
            if (_fechaIngresoInicio != null && _fechaIngresoInicio.compareTo("") != 0){        
                if (_fechaIngresoFin==null || _fechaIngresoFin.compareTo("")==0)
                    _fechaIngresoFin = _fechaIngresoInicio;
                sql += " and detalle_ausencia.fecha_inicio "
                    + "between '" + _fechaIngresoInicio +"' "
                    + "and '" + _fechaIngresoFin +"'";
            }
            if (_ausenciaId != -1){
                sql += " and detalle_ausencia.ausencia_id = " + _ausenciaId;
            }
            
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            System.out.println(WEB_NAME+"cl.femase.gestionweb."
                + "service.DetalleAusenciaDAO."
                + "getDetallesAusencias(). SQL: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[DetalleAusenciaDAO.getDetallesAusenciasHist]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new DetalleAusenciaVO();
                data.setCorrelativo(rs.getInt("correlativo"));
                data.setRutEmpleado(rs.getString("rut_empleado"));
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setNombreEmpleado(rs.getString("nombre"));
                data.setFechaIngreso(rs.getDate("fecha_ingreso"));
                data.setFechaIngresoAsStr(rs.getString("fecha_ingreso_str"));
                data.setIdAusencia(rs.getInt("ausencia_id"));
                data.setNombreAusencia(rs.getString("ausencia_nombre"));
                data.setFechaInicio(rs.getDate("fecha_inicio"));
                data.setFechaInicioAsStr(rs.getString("fecha_inicio_str"));
                data.setFechaFin(rs.getDate("fecha_fin"));
                data.setFechaFinAsStr(rs.getString("fecha_fin_str")); 
                
                data.setHoraInicioFullAsStr(rs.getString("hora_inicio_str"));
                data.setHoraFinFullAsStr(rs.getString("hora_fin_str"));
                
                data.setSoloHoraInicio(rs.getString("solohora_inicio_str"));
                data.setSoloMinsInicio(rs.getString("solomins_inicio_str"));
                data.setSoloHoraFin(rs.getString("solohora_fin_str"));
                data.setSoloMinsFin(rs.getString("solomins_fin_str"));
                data.setRutAutorizador(rs.getString("rut_autoriza_ausencia"));
                data.setAusenciaAutorizada(rs.getString("ausencia_autorizada"));
                data.setFechaHoraActualizacion(rs.getString("fecha_actualizacion_str"));
                data.setPermiteHora(rs.getString("allow_hour"));                
                
                String strHoraInicio    = data.getSoloHoraInicio();
                String strMinsInicio    = data.getSoloMinsInicio();
                if (strHoraInicio.compareTo("VACIO") != 0 && strHoraInicio.compareTo("") != 0){
                    System.err.println("[DetalleAusenciaDAO."
                        + "getDetallesAusencias]parseo "
                        + "a entero Hra Inicio: "+strHoraInicio);
                    int intHoraInicio = -1;
                    try{
                        intHoraInicio = Integer.parseInt(strHoraInicio);
                        strHoraInicio = ""+intHoraInicio;
                    }catch(NumberFormatException nex){
                        System.err.println("[DetalleAusenciaDAO."
                            + "getDetallesAusencias]Hra Inicio < 10: "+nex.toString());
                        strHoraInicio = strHoraInicio.substring(strHoraInicio.length()-1);
                    }
                    int intMinsInicio = -1;
                    try{
                        intMinsInicio = Integer.parseInt(strMinsInicio);
                        strMinsInicio = ""+intMinsInicio;
                    }catch(NumberFormatException nex){
                        System.err.println("[DetalleAusenciaDAO."
                            + "getDetallesAusencias]Mins Inicio < 10: "+nex.toString());
                        strMinsInicio = strMinsInicio.substring(strMinsInicio.length()-1);
                    }
                }
                
                String strHoraFin  = data.getSoloHoraFin();
                String strMinsFin    = data.getSoloMinsFin();
                if (strHoraFin!=null){
                    int intHoraFin = -1;
                    try{
                        intHoraFin = Integer.parseInt(strHoraFin);
                        strHoraFin = ""+intHoraFin;
                    }catch(NumberFormatException nex){
                        System.err.println("[DetalleAusenciaDAO."
                            + "getDetallesAusencias]Hra fin < 10: "+nex.toString());
                        strHoraFin = strHoraFin.substring(strHoraFin.length()-1);
                    }
                    
                    int intMinsFin = -1;
                    try{
                        intMinsFin = Integer.parseInt(strMinsFin);
                        strMinsFin = ""+intMinsFin;
                    }catch(NumberFormatException nex){
                        System.err.println("[DetalleAusenciaDAO."
                            + "getDetallesAusencias]Mins Fin < 10: "+nex.toString());
                        strMinsFin = strMinsFin.substring(strMinsFin.length()-1);
                    }
                }
                System.out.println(WEB_NAME+"[DetalleAusenciaDAO.getDetallesAusencias]"
                    + "soloHoraInicio: " +strHoraInicio
                    + ",soloMinsInicio: " +strMinsInicio    
                    + ",soloHoraFin: " +strHoraFin
                    + ",soloMinsFin: " +strMinsFin);
                data.setSoloHoraInicio(strHoraInicio);
                data.setSoloHoraFin(strHoraFin);
                data.setSoloMinsInicio(strMinsInicio);
                data.setSoloMinsFin(strMinsFin);
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
     * Retorna lista de ausencias autorizadas en formato json
     * 
     * @param _rut
     * @param _fecha
     * 
     * @return 
     */
    public String getAusenciasJson(String _rut, 
            String _fecha){

        String strJson = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            String sql = "SELECT "
                + "get_ausencias_json"
                    + "('" + _rut + "','" + _fecha + "') strjson";

            System.out.println(WEB_NAME+"[DetalleAusenciaDAO."
                + "getAusenciasJson]Sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[DetalleAusenciaDAO.getAusenciasJson]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                strJson += rs.getString("strjson");
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
        
        return strJson;
    }
    
    /**
     * Retorna lista de ausencias historicas autorizadas en formato json
     * 
     * @param _rut
     * @param _fecha
     * 
     * @return 
     */
    public String getAusenciasHistJson(String _rut, 
            String _fecha){

        String strJson = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            String sql = "SELECT "
                + "get_ausencias_hist_json"
                    + "('" + _rut + "','" + _fecha + "') strjson";

            System.out.println(WEB_NAME+"[DetalleAusenciaDAO."
                + "getAusenciasHistJson]Sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[DetalleAusenciaDAO.getAusenciasHistJson]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                strJson += rs.getString("strjson");
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
        
        return strJson;
    }
    
    /**
     * Retorna detalle de ausencia autorizada
     * 
     * @param _rutEmpleado
     * @param _fecha
     * @return 
     */
    public ArrayList<DetalleAusenciaVO> getAusencias(
            String _rutEmpleado,
            String _fecha){
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<DetalleAusenciaVO> ausencias = new ArrayList<>();
        DetalleAusenciaVO data=null;
        
        try{
            String sql ="SELECT "
                + "correlativo,"
                + "rut_empleado, "
                + "to_char(hora_inicio, 'HH24:MI:SS') hora_inicio_str,"
                + "to_char(hora_fin, 'HH24:MI:SS') hora_fin_str,"
                + "allow_hour,"
                    + "ausencia.ausencia_nombre,"
                    + "ausencia.ausencia_tipo "
                + "FROM detalle_ausencia "
                    + "inner join ausencia "
                    + "on (detalle_ausencia.ausencia_id=ausencia.ausencia_id) "
                + "where "
                + "rut_empleado = '"+_rutEmpleado+"' "
                + "and '"+_fecha+"' between fecha_inicio "
                + "and fecha_fin and ausencia_autorizada='S'";
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[DetalleAusenciaDAO.getAusencias]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new DetalleAusenciaVO();
                data.setCorrelativo(rs.getInt("correlativo"));
                data.setRutEmpleado(rs.getString("rut_empleado"));
                data.setHoraInicioFullAsStr(rs.getString("hora_inicio_str"));
                data.setHoraFinFullAsStr(rs.getString("hora_fin_str"));
                data.setNombreAusencia(rs.getString("ausencia_nombre"));
                data.setPermiteHora(rs.getString("allow_hour"));
                data.setTipoAusencia(rs.getInt("ausencia_tipo"));
                ausencias.add(data);
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
        return ausencias;
    }
    
    /**
     *
     * @param _rutEmpleado
     * @param _fecha
     * @return
     */
    public DetalleAusenciaVO getAusencia(
            String _rutEmpleado,
            String _fecha){
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        DetalleAusenciaVO data=null;
        
        try{
            String sql ="SELECT "
                + "correlativo,"
                + "rut_empleado, "
                + "to_char(hora_inicio, 'HH24:MI:SS') hora_inicio_str,"
                + "to_char(hora_fin, 'HH24:MI:SS') hora_fin_str,"
                + "allow_hour,ausencia.ausencia_nombre "
                + "FROM detalle_ausencia "
                    + "inner join ausencia "
                    + "on (detalle_ausencia.ausencia_id=ausencia.ausencia_id) "
                + "where "
                + "rut_empleado = '"+_rutEmpleado+"' "
                + "and '"+_fecha+"' between fecha_inicio "
                + "and fecha_fin and ausencia_autorizada='S'";
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[DetalleAusenciaDAO.getAusencia]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                data = new DetalleAusenciaVO();
                data.setCorrelativo(rs.getInt("correlativo"));
                data.setRutEmpleado(rs.getString("rut_empleado"));
                data.setHoraInicioFullAsStr(rs.getString("hora_inicio_str"));
                data.setHoraFinFullAsStr(rs.getString("hora_fin_str"));
                data.setNombreAusencia(rs.getString("ausencia_nombre"));
                data.setPermiteHora(rs.getString("allow_hour"));
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
     * @param _correlativo
     * @return 
     * 
     */
    public DetalleAusenciaVO getDetalleAusenciaByCorrelativo(
            int _correlativo){
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        DetalleAusenciaVO data=null;
        
        try{
            String sql ="SELECT "
                    + "correlativo,"
                    + "rut_empleado, "
                    + "fecha_ingreso,"
                    + "ausencia.ausencia_nombre,"
                    + "fecha_inicio,"
                    + "fecha_fin,"
                    + "to_char(hora_inicio, 'HH24:MI:SS') hora_inicio_str,"
                    + "to_char(hora_fin, 'HH24:MI:SS') hora_fin_str,"
                    + "allow_hour,"
                    + "rut_autoriza_ausencia,"
                    + "empleado.empl_nombres || ' ' "
                    + "|| empleado.empl_ape_paterno|| ' ' "
                    + "|| empleado.empl_ape_materno nombre_autorizador,"
                    + "ausencia_autorizada, "
                    + "detalle_ausencia.ausencia_id,"
                    + "empleado.empresa_id, "
                    + "detalle_ausencia.dias_efectivos_vacaciones,"
                    + "saldo_dias_vacaciones_asignadas,"
                    + "dias_acumulados_vacaciones_asignadas "
                + "FROM detalle_ausencia "
                    + "inner join ausencia on (detalle_ausencia.ausencia_id=ausencia.ausencia_id) "
                    + "left outer join empleado on (detalle_ausencia.rut_empleado = empleado.empl_rut)"
                + "where "
                + "correlativo = " + _correlativo;
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[DetalleAusenciaDAO.getDetalleAusenciaByCorrelativo]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                data = new DetalleAusenciaVO();
                data.setCorrelativo(rs.getInt("correlativo"));
                data.setRutEmpleado(rs.getString("rut_empleado"));
                data.setFechaIngresoAsStr(rs.getString("fecha_ingreso"));
                data.setNombreAusencia(rs.getString("ausencia_nombre"));
                data.setFechaInicioAsStr(rs.getString("fecha_inicio"));
                data.setFechaFinAsStr(rs.getString("fecha_fin"));
                data.setHoraInicioFullAsStr(rs.getString("hora_inicio_str"));
                data.setHoraFinFullAsStr(rs.getString("hora_fin_str"));
                data.setPermiteHora(rs.getString("allow_hour"));
                data.setRutAutorizador(rs.getString("rut_autoriza_ausencia"));
                data.setNombreAutorizador(rs.getString("nombre_autorizador"));
                data.setAusenciaAutorizada(rs.getString("ausencia_autorizada"));
                data.setIdAusencia(rs.getInt("ausencia_id"));
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setDiasEfectivosVacaciones(rs.getInt("dias_efectivos_vacaciones"));
                //nuevas by Ramon
                data.setSaldoDiasVacacionesAsignadas(rs.getDouble("saldo_dias_vacaciones_asignadas"));
                data.setDiasAcumuladosVacacionesAsignadas(rs.getDouble("dias_acumulados_vacaciones_asignadas"));
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
    * 
    * @return 
    */
    public int getNewIdDetalleAusencia(){
        int newid = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            String sql ="select nextval('detalle_ausencia_corr_seq') newid";
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[DetalleAusenciaDAO.getNewIdDetalleAusencia]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                newid = rs.getInt("newid");
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
        return newid;
    }
    
     /**
     * Retorna lista con los empleados que autorizan ausencias y que pertenecen 
     * a alguno de los centros de costo asignados al usuario en sesion.
     * 
     * @param _usuario
     * @return 
     */
    public List<DetalleAusenciaVO> getAutorizadoresDisponibles(UsuarioVO _usuario){
        List<DetalleAusenciaVO> lista = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        DetalleAusenciaVO data;
        
        try{
            String strcencos = "-1";
            List cencos = _usuario.getCencos();
            if (_usuario.getAdminEmpresa().compareTo("N") == 0 && cencos.size() > 0){
                strcencos = "";
                Iterator<UsuarioCentroCostoVO> cencosIt = cencos.iterator();
                while (cencosIt.hasNext()) {
                    strcencos += cencosIt.next().getCcostoId()+",";
                }
                strcencos = strcencos.substring(0, strcencos.length()-1);
            }
            String sql = "SELECT "
                    + "empleado.empl_rut rut,"
                    + "coalesce(empleado.empl_nombres, '') || ' ' || coalesce(empleado.empl_ape_paterno, '') nombre,"
                    + "empleado.empresa_id,empleado.depto_id, depto.depto_nombre,"
                    + "empleado.cenco_id, cenco.ccosto_nombre "
                + "FROM empleado "
                + " inner join departamento depto "
                + "on (empleado.depto_id = depto.depto_id "
                + "and empleado.empresa_id = depto.empresa_id) "
                + "inner join centro_costo cenco "
                + "on (empleado.depto_id =cenco.depto_id "
                + "and empleado.cenco_id=cenco.ccosto_id) "
                + "WHERE empleado.autoriza_ausencia = true "
                    + " and empleado.empl_estado = 1 "
                + " and ("
                + " (empleado.contrato_indefinido is true) "
                    + "	or (empleado.contrato_indefinido is false "
                    + "	and current_date between empleado.empl_fec_ini_contrato and empleado.empl_fec_fin_contrato) "
                    + ") ";    
            
            if (_usuario.getIdPerfil() != Constantes.ID_PERFIL_SUPER_ADMIN 
                    && _usuario.getAdminEmpresa().compareTo("S") == 0){ 
                sql += " and (empleado.empresa_id = '" + _usuario.getEmpresaId() + "') ";
            }
            if (strcencos.compareTo("-1") != 0){
                sql += " and (empleado.cenco_id in (" + strcencos + ")) ";
            }
            sql += " order by empleado.empl_nombres";
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[DetalleAusenciaDAO.getAutorizadoresDisponibles]");
            
            System.out.println(WEB_NAME+"[DetalleAusenciaDAO."
                + "getAutorizadoresDisponibles]sql: " + sql);
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new DetalleAusenciaVO();
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setCencoIdEmpleado(rs.getInt("cenco_id"));
                data.setRutAutorizador(rs.getString("rut"));
                data.setNombreAutorizador(rs.getString("nombre"));
                data.setNomDeptoAutorizador(rs.getString("depto_nombre"));
                data.setNomCencoAutorizador(rs.getString("ccosto_nombre"));
                
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
    * @param _source
    * @param _rutEmpleado
    * @param _rutAutorizador
    * @param _fechaIngresoInicio
    * @param _fechaIngresoFin
    * @param _ausenciaId
    * @return 
    */
    public int getDetallesAusenciasCount(String _source, 
            String _rutEmpleado,
            String _rutAutorizador, 
            String _fechaIngresoInicio, 
            String _fechaIngresoFin,
            int _ausenciaId){
        int count=0;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[DetalleAusenciaDAO.getDetallesAusenciasCount]");
            ResultSet rs;
            try (Statement statement = dbConn.createStatement()) {
                String sql = "SELECT count(*) as count "
                    + "FROM detalle_ausencia " +
                     "inner join empleado on detalle_ausencia.rut_empleado = empleado.empl_rut " +
                     "inner join ausencia on detalle_ausencia.ausencia_id = ausencia.ausencia_id "
                    + " WHERE 1 = 1 ";
                if (_rutEmpleado != null && _rutEmpleado.compareTo("-1")!=0){        
                    sql += " and detalle_ausencia.rut_empleado = '" + _rutEmpleado + "'";
                }
                if (_rutAutorizador != null && _rutAutorizador.compareTo("-1")!=0){        
                    sql += " and detalle_ausencia.rut_autoriza_ausencia = '" + _rutAutorizador + "'";
                }
                if (_fechaIngresoInicio != null && _fechaIngresoInicio.compareTo("") != 0){        
                    if (_fechaIngresoFin==null || _fechaIngresoFin.compareTo("")==0)
                        _fechaIngresoFin = _fechaIngresoInicio;
                    sql += " and detalle_ausencia.fecha_inicio "
                        + "between '" + _fechaIngresoInicio +"' "
                        + "and '" + _fechaIngresoFin +"'";
                }
                if (_ausenciaId != -1){
                    sql += " and detalle_ausencia.ausencia_id = " + _ausenciaId;
                }   
                
                if (_source != null && _source.compareTo("detalle_ausencias") == 0)
                {   
                    sql += " and detalle_ausencia.ausencia_id != " + Constantes.ID_AUSENCIA_VACACIONES;
                }
                
                System.out.println(WEB_NAME+"[DetalleAusenciaDAO."
                    + "getDetallesAusenciasCount]SQL: " + sql);
                
                rs = statement.executeQuery(sql);
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
    
    /**
    * 
    * @param _source
    * @param _rutEmpleado
    * @param _rutAutorizador
    * @param _fechaIngresoInicio
    * @param _fechaIngresoFin
    * @return 
    */
    public int getPermisosAdministrativosCount(String _source, 
            String _rutEmpleado,
            String _rutAutorizador, 
            String _fechaIngresoInicio, 
            String _fechaIngresoFin){
        int count=0;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[DetalleAusenciaDAO.getPermisosAdministrativosCount]");
            ResultSet rs;
            try (Statement statement = dbConn.createStatement()) {
                String sql = "SELECT count(*) as count "
                    + "FROM detalle_ausencia " +
                     "inner join empleado on detalle_ausencia.rut_empleado = empleado.empl_rut " +
                     "inner join ausencia on detalle_ausencia.ausencia_id = ausencia.ausencia_id "
                    + " WHERE 1 = 1 ";
                if (_rutEmpleado != null && _rutEmpleado.compareTo("-1")!=0){        
                    sql += " and detalle_ausencia.rut_empleado = '" + _rutEmpleado + "'";
                }
                if (_rutAutorizador != null && _rutAutorizador.compareTo("-1")!=0){        
                    sql += " and detalle_ausencia.rut_autoriza_ausencia = '" + _rutAutorizador + "'";
                }
                if (_fechaIngresoInicio != null && _fechaIngresoInicio.compareTo("") != 0){        
                    if (_fechaIngresoFin==null || _fechaIngresoFin.compareTo("")==0)
                        _fechaIngresoFin = _fechaIngresoInicio;
                    sql += " and detalle_ausencia.fecha_inicio "
                        + "between '" + _fechaIngresoInicio +"' "
                        + "and '" + _fechaIngresoFin +"'";
                }
                
                sql += " and detalle_ausencia.ausencia_id = " + Constantes.ID_AUSENCIA_PERMISO_ADMINISTRATIVO;
                
                System.out.println(WEB_NAME+"[DetalleAusenciaDAO."
                    + "getPermisosAdministrativosCount]SQL: " + sql);
                
                rs = statement.executeQuery(sql);
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
    
    /**
    * 
    * @param _source
    * @param _rutEmpleado
    * @param _rutAutorizador
    * @param _fechaIngresoInicio
    * @param _fechaIngresoFin
    * @return 
    */
    public int getPermisosExamenSaludPreventivaCount(String _source, 
            String _rutEmpleado,
            String _rutAutorizador, 
            String _fechaIngresoInicio, 
            String _fechaIngresoFin){
        int count=0;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[DetalleAusenciaDAO.getPermisosExamenSaludPreventivaCount]");
            ResultSet rs;
            try (Statement statement = dbConn.createStatement()) {
                String sql = "SELECT count(*) as count "
                    + "FROM detalle_ausencia " +
                     "inner join empleado on detalle_ausencia.rut_empleado = empleado.empl_rut " +
                     "inner join ausencia on detalle_ausencia.ausencia_id = ausencia.ausencia_id "
                    + " WHERE 1 = 1 ";
                if (_rutEmpleado != null && _rutEmpleado.compareTo("-1")!=0){        
                    sql += " and detalle_ausencia.rut_empleado = '" + _rutEmpleado + "'";
                }
                if (_rutAutorizador != null && _rutAutorizador.compareTo("-1")!=0){        
                    sql += " and detalle_ausencia.rut_autoriza_ausencia = '" + _rutAutorizador + "'";
                }
                if (_fechaIngresoInicio != null && _fechaIngresoInicio.compareTo("") != 0){        
                    if (_fechaIngresoFin==null || _fechaIngresoFin.compareTo("")==0)
                        _fechaIngresoFin = _fechaIngresoInicio;
                    sql += " and detalle_ausencia.fecha_inicio "
                        + "between '" + _fechaIngresoInicio +"' "
                        + "and '" + _fechaIngresoFin +"'";
                }
                
                sql += " and detalle_ausencia.ausencia_id = " + Constantes.ID_AUSENCIA_PERMISO_EXAMEN_SALUD_PREVENTIVA;
                
                System.out.println(WEB_NAME+"[DetalleAusenciaDAO.getPermisosExamenSaludPreventivaCount]SQL: " + sql);
                
                rs = statement.executeQuery(sql);
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
                System.err.println("Error: " + ex.toString());
            }
        }
        return count;
    }
    
    /**
     * 
     * @param _rutEmpleado
     * @param _rutAutorizador
     * @param _fechaIngresoInicio
     * @param _fechaIngresoFin
     * @param _ausenciaId
     * @return 
     */
    public int getDetallesAusenciasHistCount(String _rutEmpleado,
            String _rutAutorizador, 
            String _fechaIngresoInicio, 
            String _fechaIngresoFin,
            int _ausenciaId){
        int count=0;
        ResultSet rs = null;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[DetalleAusenciaDAO.getDetallesAusenciasHistCount]");
            
            try (Statement statement = dbConn.createStatement()) {
                String sql = "SELECT count(*) as count "
                    + "FROM detalle_ausencia_historica detalle_ausencia " +
                     "inner join empleado on detalle_ausencia.rut_empleado = empleado.empl_rut " +
                     "inner join ausencia on detalle_ausencia.ausencia_id = ausencia.ausencia_id "
                    + " WHERE 1 = 1 ";
                if (_rutEmpleado != null && _rutEmpleado.compareTo("-1")!=0){        
                    sql += " and detalle_ausencia.rut_empleado = '" + _rutEmpleado + "'";
                }
                if (_rutAutorizador != null && _rutAutorizador.compareTo("-1")!=0){        
                    sql += " and detalle_ausencia.rut_autoriza_ausencia = '" + _rutAutorizador + "'";
                }
                if (_fechaIngresoInicio != null && _fechaIngresoInicio.compareTo("") != 0){        
                    if (_fechaIngresoFin==null || _fechaIngresoFin.compareTo("")==0)
                        _fechaIngresoFin = _fechaIngresoInicio;
                    sql += " and detalle_ausencia.fecha_inicio "
                        + "between '" + _fechaIngresoInicio +"' "
                        + "and '" + _fechaIngresoFin +"'";
                }
                if (_ausenciaId != -1){
                    sql += " and detalle_ausencia.ausencia_id = " + _ausenciaId;
                }   
                
                System.out.println(WEB_NAME+"cl.femase.gestionweb."
                    + "service.DetalleAusenciaDAO."
                    + "getDetallesAusenciasCount(). SQL: "+sql);
                
                rs = statement.executeQuery(sql);
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
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
        return count;
    }
   
    /**
    * Retorna ausencias por dia para un rango de fechas
    * 
    * @param _rutEmpleado
    * @param _fechaInicio
    * @param _fechaFin
    * @return 
    */
    public ArrayList<DetalleAusenciaVO> getAusenciasDiaConflicto(
            String _rutEmpleado,
            String _fechaInicio,
            String _fechaFin){
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<DetalleAusenciaVO> ausencias = new ArrayList<>();
        DetalleAusenciaVO data=null;
        
        try{
            String sql ="SELECT  correlativo,"
                + "rut_empleado, "
                + "coalesce(to_char(hora_inicio,'HH24:MI:SS'),'') hora_inicio_str,"
                + "coalesce(to_char(hora_fin,'HH24:MI:SS'),'') hora_fin_str, "
                + "allow_hour,ausencia.ausencia_nombre,"
                + "fecha_inicio,fecha_fin "
                + "FROM detalle_ausencia "
                + "inner join ausencia "
                + "on (detalle_ausencia.ausencia_id=ausencia.ausencia_id) "
                + "where "
                    + "rut_empleado = '" + _rutEmpleado + "' "
                    + "and allow_hour='N' "
                    + " and daterange(fecha_inicio, fecha_fin, '[]') && daterange(date '"+_fechaInicio+"', date '"+_fechaFin+"', '[]') ";     
            
            sql += " and ausencia_autorizada='S' ";
            
            System.out.println(WEB_NAME+"[DetalleAusenciaDAO."
                + "getAusenciasDiaConflicto]Sql: "+sql);
                                
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[DetalleAusenciaDAO.getAusenciasDiaConflicto]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new DetalleAusenciaVO();
                data.setCorrelativo(rs.getInt("correlativo"));
                data.setRutEmpleado(rs.getString("rut_empleado"));
                data.setHoraInicioFullAsStr(rs.getString("hora_inicio_str"));
                data.setHoraFinFullAsStr(rs.getString("hora_fin_str"));
                data.setNombreAusencia(rs.getString("ausencia_nombre"));
                data.setPermiteHora(rs.getString("allow_hour"));
                data.setNombreAusencia(rs.getString("ausencia_nombre"));
                data.setFechaInicioAsStr(rs.getString("fecha_inicio"));
                data.setFechaFinAsStr(rs.getString("fecha_fin"));
                
                ausencias.add(data);
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
        return ausencias;
    }
    
    /**
    * Retorna ausencias por hora para una fecha
    * 
    * @param _rutEmpleado
    * @param _fecha
    * @param _horaInicio
    * @param _horaFin
    * @return 
    */
    public ArrayList<DetalleAusenciaVO> getAusenciasHoraConflicto(
            String _rutEmpleado,
            String _fecha,
            String _horaInicio,
            String _horaFin){
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<DetalleAusenciaVO> ausencias = new ArrayList<>();
        DetalleAusenciaVO data=null;
        
        try{
            String sql ="SELECT  correlativo,"
                + "rut_empleado, "
                + "coalesce(to_char(hora_inicio,'HH24:MI:SS'),'') hora_inicio_str,"
                + "coalesce(to_char(hora_fin,'HH24:MI:SS'),'') hora_fin_str, "
                + "allow_hour,ausencia.ausencia_nombre,"
                + "fecha_inicio,fecha_fin "
                + "FROM detalle_ausencia "
                + "inner join ausencia "
                + "on (detalle_ausencia.ausencia_id=ausencia.ausencia_id) "
                + "where "
                    + "rut_empleado = '" + _rutEmpleado + "' "
                    + "and allow_hour = 'S' "    
                    + " and fecha_inicio= '" + _fecha + "' ";
             if (_horaInicio != null && _horaFin != null){
                sql += " and ( '" + _horaInicio + "'::time between hora_inicio and hora_fin "
                    + "or '" + _horaFin + "'::time between hora_inicio and hora_fin) ";
             }
             sql += " and ausencia_autorizada = 'S' ";
            
            System.out.println(WEB_NAME+"[DetalleAusenciaDAO."
                + "getAusenciasHoraConflicto]Sql: "+sql);
                                
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[DetalleAusenciaDAO.getAusenciasHoraConflicto]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new DetalleAusenciaVO();
                data.setCorrelativo(rs.getInt("correlativo"));
                data.setRutEmpleado(rs.getString("rut_empleado"));
                data.setHoraInicioFullAsStr(rs.getString("hora_inicio_str"));
                data.setHoraFinFullAsStr(rs.getString("hora_fin_str"));
                data.setNombreAusencia(rs.getString("ausencia_nombre"));
                data.setPermiteHora(rs.getString("allow_hour"));
                data.setNombreAusencia(rs.getString("ausencia_nombre"));
                data.setFechaInicioAsStr(rs.getString("fecha_inicio"));
                data.setFechaFinAsStr(rs.getString("fecha_fin"));
                
                ausencias.add(data);
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
        return ausencias;
    }
}
