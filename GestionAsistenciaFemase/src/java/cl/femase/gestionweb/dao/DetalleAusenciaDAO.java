/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.DetalleAusenciaVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioCentroCostoVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
    public MaintenanceVO update(DetalleAusenciaVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
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
                System.out.println("[update]detalle_ausencia"
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
    public MaintenanceVO updateDiasEfectivosVacaciones(DetalleAusenciaVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
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
                System.out.println("[DetalleAusenciaDAO."
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
    public MaintenanceVO updateDiasEfectivosVacaciones(int _correlativo, 
            int _diasEfectivos){
        MaintenanceVO objresultado = new MaintenanceVO();
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
                System.out.println("[update dias efectivos vacaciones]detalle_ausencia"
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
    public MaintenanceVO delete(DetalleAusenciaVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
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
                System.out.println("[delete]detalle_ausencia"
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
    * Hace update de las columnas:
    *   detalle_ausencia.saldo_dias_vacaciones_asignadas y
    *   detalle_ausencia.dias_acumulados_vacaciones_asignadas
    * 
    * @param _runEmpleado
    * @return 
    */
    public MaintenanceVO actualizaSaldosVacaciones(String _runEmpleado){
        MaintenanceVO objresultado = new MaintenanceVO();
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

            System.out.println("[DetalleAusenciaDAO."
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
    
    /**
    * Agrega detalle ausencia
    * @param _data
    * @return 
    */ 
    public MaintenanceVO insert(DetalleAusenciaVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
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

            System.out.println("[DetalleAusenciaDAO.insert]sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[DetalleAusenciaDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getRutEmpleado());
            insert.setInt(2,  _data.getIdAusencia());
            insert.setString(3,  _data.getRutAutorizador());
            insert.setString(4,  _data.getAusenciaAutorizada());
            insert.setString(5,  _data.getPermiteHora());
            
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println("[insert]detalle_ausencia"
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
    * Retorna lista con detalle ausencias
    * 
    * @param _source: indica si el listado de ausencias se muestra desde ausencias-detalle o desde admin vacaciones
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
    public List<DetalleAusenciaVO> getDetallesAusencias(String _source,
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
                + "vacaciones.dias_acumulados,"
                + "vacaciones.dias_progresivos,"
                + "vacaciones.saldo_dias, "
                + "coalesce(vacaciones.dias_especiales, 'N') dias_especiales,"
                + "detalle_ausencia.dias_efectivos_vacaciones,"
                + "coalesce(cenco.es_zona_extrema,'N') es_zona_extrema,"
                + "coalesce(detalle_ausencia.saldo_dias_vacaciones_asignadas,0) saldo_dias_vacaciones_asignadas,"
                + "coalesce(detalle_ausencia.dias_acumulados_vacaciones_asignadas,0) dias_acumulados_vacaciones_asignadas " 
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
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            System.out.println("[DetalleAusenciaDAO."
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
                if (strHoraInicio.compareTo("VACIO")!=0){
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
                System.out.println("[DetalleAusenciaDAO.getDetallesAusencias]"
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
            
            System.out.println("[DetalleAusenciaDAO."
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
            
            System.out.println("gestionweb."
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
                + "coalesce(to_char(detalle_ausencia.hora_inicio, 'HH24'),'VACIO') solohora_inicio_str,"
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
            
            System.out.println("cl.femase.gestionweb."
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
                if (strHoraInicio.compareTo("VACIO")!=0){
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
                System.out.println("[DetalleAusenciaDAO.getDetallesAusencias]"
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

            System.out.println("[DetalleAusenciaDAO."
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

            System.out.println("[DetalleAusenciaDAO."
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
                + "empleado.depto_id, depto.depto_nombre,"
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
            
            System.out.println("[DetalleAusenciaDAO."
                + "getAutorizadoresDisponibles]sql: " + sql);
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new DetalleAusenciaVO();
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
                
                System.out.println("[DetalleAusenciaDAO."
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
                
                System.out.println("cl.femase.gestionweb."
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
            
            System.out.println("[DetalleAusenciaDAO."
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
            
            System.out.println("[DetalleAusenciaDAO."
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
