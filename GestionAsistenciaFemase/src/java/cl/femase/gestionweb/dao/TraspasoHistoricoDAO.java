/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.MaintenanceVO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Alexander
 */
public class TraspasoHistoricoDAO extends BaseDAO{

    //private Connection dbConn;
    //DbDirectConnection dbconnection = DbDirectConnection.getInstance();
    
    public TraspasoHistoricoDAO() {
        //dbConn = dbLocator.getConnection(m_dbpoolName);
    }
    
    /**
    * Metodo que traspasa a historico las marcas de asistencia
    * de los ruts especificados para la empresa indicada.Solo se consideran los registros cuya fecha sea menor o igual a la fecha especificada.
    * @param _empresaId
    * @param _fecha
    * @param _cadenaRuts
    * @return 
    */
    public MaintenanceVO insertMarcasHistoricas(String _empresaId, 
            String _fecha, 
            String _cadenaRuts){
        MaintenanceVO resultado = new MaintenanceVO();
        int affectedRows        = 0;
        PreparedStatement ps    = null;
        Connection dbConn       = null;
        try{
            String sql = "insert into marca_historica ("
                    + "cod_dispositivo,"
                    + "empresa_cod,"
                    + "rut_empleado,"
                    + "fecha_hora,"
                    + "cod_tipo_marca,"
                    + "fecha_hora_actualizacion,"
                    + "id,"
                    + "hashcode,"
                    + "modificada,"
                    + "comentario, "
                    + "cod_tpo_marca_manual,"
                    + "fecha_hora_traspaso, "
                    + "latitud, "
                    + "longitud,"
                    + "correlativo) "
                + "select "
                    + "cod_dispositivo,"
                    + "empresa_cod,"
                    + "rut_empleado,"
                    + "fecha_hora,"
                    + "cod_tipo_marca,"
                    + "fecha_hora_actualizacion,"
                    + "id,"
                    + "hashcode,"
                    + "modificada,"
                    + "comentario,"
                    + "cod_tpo_marca_manual,"
                    + "current_timestamp, "
                    + "latitud, "
                    + "longitud,"
                    + "correlativo "
                + "from marca "
                + "where fecha_hora::date <= '" + _fecha + "' "
                    + "and empresa_cod='" + _empresaId + "' "
                    + "and (rut_empleado in (" + _cadenaRuts + ") )"; 
            
            System.err.println("[GestionFemase."
                + "TraspasoHistoricosFemaseJob.insertMarcasHistoricas]Sql: " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[TraspasoHistoricoDAO.insertMarcasHistoricas]");
            ps = dbConn.prepareStatement(sql);
            
            affectedRows = ps.executeUpdate();
            resultado.setFilasAfectadas(affectedRows);
            ps.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            affectedRows = -1;
            resultado.setFilasAfectadas(affectedRows);
            resultado.setThereError(true);
            resultado.setMsg(sqle.getMessage());
            resultado.setMsgError(sqle.toString());
            
            System.err.println("[GestionFemase."
                + "TraspasoHistoricoDAO."
                + "insertMarcasHistoricas]"
                + "Error: " + sqle.toString());
        }
        
        return resultado;
    }
    
    /**
    * Metodo que traspasa a historico las marcas de asistencia rechazadas
    * de los ruts especificados para la empresa indicada.Solo se consideran los registros cuya fecha sea menor o igual a la fecha especificada.
    * @param _empresaId
    * @param _fecha
    * @param _cadenaRuts
    * @return 
    */
    public MaintenanceVO insertMarcasRechazosHistoricas(String _empresaId, 
            String _fecha, 
            String _cadenaRuts){
        MaintenanceVO resultado = new MaintenanceVO();
        int affectedRows =0;
        PreparedStatement ps = null;
        Connection dbConn = null;
        try{
            String sql = "insert into marca_rechazo_historica ("
                        + "cod_dispositivo,"
                        + "empresa_cod,"
                        + "rut_empleado,"
                        + "fecha_hora,"
                        + "cod_tipo_marca,"
                        + "fecha_hora_actualizacion,"
                        + "id,"
                        + "hashcode,"
                        + "motivo_rechazo,"
                        + "correlativo_rechazo,"
                        + "fecha_hora_traspaso, "
                        + "latitud, "
                        + "longitud) "
                + "select cod_dispositivo,"
                    + "empresa_cod,"
                    + "rut_empleado,"
                    + "fecha_hora,"
                    + "cod_tipo_marca,"
                    + "fecha_hora_actualizacion,"
                    + "id,"
                    + "hashcode,"
                    + "motivo_rechazo,"
                    + "correlativo_rechazo, "
                    + "current_timestamp, "
                    + "latitud, "
                    + "longitud "
                + "from marca_rechazo "
                + "where fecha_hora::date <= '" + _fecha + "' "
                + "and empresa_cod='" + _empresaId + "' "
                + "and (rut_empleado in (" + _cadenaRuts + ") )";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[TraspasoHistoricosFMCJob.insertMarcasRechazosHistoricas]");
            ps = dbConn.prepareStatement(sql);
            
            affectedRows = ps.executeUpdate();
            resultado.setFilasAfectadas(affectedRows);
            ps.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            affectedRows = -1;
            resultado.setFilasAfectadas(affectedRows);
            resultado.setThereError(true);
            resultado.setMsg(sqle.getMessage());
            resultado.setMsgError(sqle.toString());
            System.err.println("[GestionFemase."
                + "TraspasoHistoricosFMCJob.insertMarcasRechazosHistoricas]"
                + "Error:" + sqle.toString());
        }
        
        return resultado;
    }
    
    /**
    * Metodo que traspasa a historico las ausencia
    * de los ruts especificados para la empresa indicada.
    * Solo se consideran los registros cuya ausencia.fecha_inicio sea menor o igual a la fecha especificada.
    * 
    * @param _empresaId
    * @param _fecha
    * @param _cadenaRuts
    * @return 
    */
    public MaintenanceVO insertAusenciasHistoricas(String _empresaId, 
            String _fecha, 
            String _cadenaRuts){
        MaintenanceVO resultado = new MaintenanceVO();
        int affectedRows =0;
        PreparedStatement ps = null;
        Connection dbConn = null;
        try{
            String sql = "insert into detalle_ausencia_historica ("
                    + "rut_empleado,"
                    + "fecha_ingreso,"
                    + "ausencia_id,"
                    + "fecha_inicio,fecha_fin,rut_autoriza_ausencia,"
                    + "ausencia_autorizada,fecha_actualizacion,hora_inicio,"
                    + "hora_fin,allow_hour,correlativo,"
                    + "fecha_hora_traspaso, empresa_id, dias_efectivos_vacaciones) "
                + " select "
                    + " rut_empleado, "
                    + " fecha_ingreso,"
                    + "ausencia_id,"
                    + " fecha_inicio,"
                    + "fecha_fin,"
                    + "rut_autoriza_ausencia,"
                    + " ausencia_autorizada,"
                    + "fecha_actualizacion,"
                    + "hora_inicio,"
                    + "hora_fin,"
                    + "allow_hour,"
                    + "correlativo,"
                    + "current_timestamp, "
                    + "'" + _empresaId + "',"
                    + "dias_efectivos_vacaciones "
                + " from detalle_ausencia "
                    + "inner join empleado on (detalle_ausencia.rut_empleado = empleado.empl_rut) "
                + "where fecha_inicio <= '" + _fecha + "' "
                    + "and (empleado.empl_rut in (" + _cadenaRuts + ") )"; 
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TraspasoHistoricosFMCJob.insertAusenciasHistoricas]");
            ps = dbConn.prepareStatement(sql);
            
            affectedRows = ps.executeUpdate();
            resultado.setFilasAfectadas(affectedRows);
            ps.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            affectedRows =-1;
            resultado.setFilasAfectadas(affectedRows);
            resultado.setThereError(true);
            resultado.setMsg(sqle.getMessage());
            resultado.setMsgError(sqle.toString());
            System.err.println("[GestionFemase."
                + "TraspasoHistoricosFMCJob."
                + "insertAusenciasHistoricas]Error: " + sqle.toString());
        }
        
        return resultado;
    }
    
    /**
    * Metodo que traspasa a historico los calculos de asistencia
    * de los ruts especificados para la empresa indicada.Solo se consideran los registros cuya fecha sea menor o igual a la fecha especificada.
    * @param _empresaId
    * @param _fecha
    * @param _cadenaRuts
    * @return 
    */
    public MaintenanceVO insertDetalleAsistenciaHistoricos(String _empresaId, 
            String _fecha, 
            String _cadenaRuts){
        MaintenanceVO resultado = new MaintenanceVO();
        int affectedRows =0;
        PreparedStatement ps = null;
        Connection dbConn = null;
        try{
            String sql = "insert into detalle_asistencia_historica ("
                + "empresa_id ,  depto_id ,  cenco_id ,  rut_empleado,  fecha_hora_calculo,"
                + "  fecha_marca_entrada,  hora_entrada ,  hora_salida ,  horas_teoricas ,"
                + "  horas_trabajadas ,  minutos_extras_50 ,  hora_inicio_ausencia ,"
                + "  hora_fin_ausencia ,  minutos_trabajados ,  horas_extras ,  holgura_minutos ,"
                + "  es_feriado ,  minutos_extras_100 ,  hora_entrada_teorica ,  hora_salida_teorica ,"
                + "  minutos_extras ,  art22 ,  minutos_atraso ,  minutos_no_trabajados_entrada ,"
                + "  minutos_no_trabajados_salida ,  autoriza_mins_no_trab_entrada ,  autoriza_mins_no_trab_salida,"
                + "  hrs_presenciales,  hrs_trabajadas,  observacion,  hrs_ausencia,  fecha_marca_salida,"
                + "  hhmm_extras,  hhmm_atraso,  autoriza_atraso,  autoriza_hrsextras,  hhmm_justificadas,"
                + "  hhmm_extras_autorizadas,  marca_entrada_comentario,  marca_salida_comentario,  hhmm_salida_anticipada, fecha_hora_traspaso) "
                + "select "
                + "  empresa_id ,  depto_id ,  cenco_id ,  rut_empleado,  fecha_hora_calculo,  fecha_marca_entrada,  hora_entrada ,"
                + "  hora_salida ,  horas_teoricas ,  horas_trabajadas ,  minutos_extras_50 ,  hora_inicio_ausencia ,  hora_fin_ausencia ,"
                + "  minutos_trabajados ,  horas_extras ,  holgura_minutos ,  es_feriado ,  minutos_extras_100 ,  hora_entrada_teorica ,"
                + "  hora_salida_teorica ,  minutos_extras ,  art22 ,  minutos_atraso ,  minutos_no_trabajados_entrada ,  minutos_no_trabajados_salida ,"
                + "  autoriza_mins_no_trab_entrada ,  autoriza_mins_no_trab_salida,  hrs_presenciales,  hrs_trabajadas,  observacion,"
                + "  hrs_ausencia,  fecha_marca_salida,  hhmm_extras,  hhmm_atraso,  autoriza_atraso,  autoriza_hrsextras,  hhmm_justificadas,"
                + "  hhmm_extras_autorizadas,  marca_entrada_comentario,  marca_salida_comentario,  hhmm_salida_anticipada, current_timestamp "
                + "  from detalle_asistencia "
                + "where fecha_hora_calculo::date <= '" + _fecha + "' "
                    + "and empresa_id = '" + _empresaId + "' "
                    + "and (rut_empleado in (" + _cadenaRuts + ") )";
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[TraspasoHistoricosFMCJob.insertDetalleAsistenciaHistoricos]");
            ps = dbConn.prepareStatement(sql);
            
            affectedRows = ps.executeUpdate();
            resultado.setFilasAfectadas(affectedRows);
            ps.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            affectedRows =-1;
            resultado.setFilasAfectadas(affectedRows);
            resultado.setThereError(true);
            resultado.setMsg(sqle.getMessage());
            resultado.setMsgError(sqle.toString());
            System.err.println("[GestionFemase."
                + "TraspasoHistoricosFMCJob.insertDetalleAsistenciaHistoricos]"
                + "Error: " + sqle.toString());
        }
        
        return resultado;
    }
    
    /**
    * Metodo que traspasa a historico los logs de auditoria.Solo se consideran los registros cuya fecha sea menor o igual a la fecha especificada.
    * @param _empresaId
    * @param _fecha
    * @return 
    */
    public MaintenanceVO insertLogEventosHistoricos(String _empresaId, String _fecha){
        MaintenanceVO resultado = new MaintenanceVO();
        int affectedRows =0;
        PreparedStatement ps = null;
        Connection dbConn = null;
        try{
            String sql = "insert into mantencion_evento_historico "
                + "(username,  descripcion_evento,  ip_usuario,"
                + "tipo_evento,fecha_hora,empresa_id,depto_id,"
                + "cenco_id,empleado_rut,empresa_id_source,fecha_hora_traspaso) "
                + "select "
                + "username,descripcion_evento,ip_usuario,tipo_evento,"
                + "fecha_hora,empresa_id,depto_id,cenco_id,"
                + "empleado_rut,empresa_id_source,current_timestamp "
                + "from mantencion_evento "
                + "where fecha_hora::date <= '" + _fecha + "' "
                    + "and empresa_id_source='" + _empresaId + "'"; 
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TraspasoHistoricosFMCJob.insertLogEventosHistoricos]");
            ps = dbConn.prepareStatement(sql);
            
            affectedRows = ps.executeUpdate();
            resultado.setFilasAfectadas(affectedRows);
            ps.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            affectedRows =-1;
            resultado.setFilasAfectadas(affectedRows);
            resultado.setThereError(true);
            resultado.setMsg(sqle.getMessage());
            resultado.setMsgError(sqle.toString());
            System.err.println("[GestionFemase."
                + "TraspasoHistoricosFMCJob."
                + "insertLogEventosHistoricos]Error: " + sqle.toString());
        }
        
        return resultado;
    }
    
    /**
    * Metodo que elimina las marcas recien traspasadas a historico
    * de los ruts especificados y para la empresa indicada.
    * Solo se consideran los registros cuya fecha sea menor o igual a la fecha especificada.
    * 
    * @param _empresaId
    * @param _fecha
    * @param _cadenaRuts
    * 
    */
    public void deleteMarcas(String _empresaId, 
            String _fecha, 
            String _cadenaRuts){
        int affectedRows =0;
        PreparedStatement ps = null;
        Connection dbConn = null;
        try{
            String sql = "delete "
                + "from marca "
                + "where fecha_hora::date <= '" + _fecha + "' "
                    + "and empresa_cod='" + _empresaId + "' "
                    + "and (rut_empleado in (" + _cadenaRuts + ") )";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[TraspasoHistoricosFMCJob.met]");
            ps = dbConn.prepareStatement(sql);
            
            affectedRows = ps.executeUpdate();
            
            ps.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[GestionFemase."
                + "TraspasoHistoricosFemaseJob.deletMarcas]Error:"+sqle.toString());
        }
        
    }
    
    /**
    * Metodo que elimina las marcas rechazadas recien traspasadas a historico
    * de los ruts especificados y para la empresa indicada.
    * Solo se consideran los registros cuya fecha sea menor o igual a la fecha especificada.
    * 
    * @param _empresaId
    * @param _fecha
    * @param _cadenaRuts
    * 
    */
    public void deleteMarcasRechazadas(String _empresaId, 
            String _fecha, 
            String _cadenaRuts){
        int rowsAffected =0;
        PreparedStatement ps = null;
        Connection dbConn = null;
        try{
            String sql = "delete "
                + "from marca_rechazo "
                + "where fecha_hora::date <= '" + _fecha + "' "
                    + "and empresa_cod='" + _empresaId + "' "
                    + "and (rut_empleado in (" + _cadenaRuts + ") )";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[TraspasoHistoricosFMCJob.met]");
            ps = dbConn.prepareStatement(sql);
            
            rowsAffected = ps.executeUpdate();
            
            ps.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[GestionFemase."
                + "TraspasoHistoricosFemaseJob.deleteMarcasRechazadas]Error:"+sqle.toString());
        }
        
    }
    
    /**
    * Metodo que elimina los calculos de asistencia recien traspasadas a historico
    * de los ruts especificados y para la empresa indicada.
    * 
    * Solo se consideran los registros cuya fecha sea menor o igual a la fecha especificada.
    * 
    * @param _empresaId
    * @param _fecha
    * @param _cadenaRuts
    * 
    */
    public void deleteDetalleAsistencia(String _empresaId, 
            String _fecha, 
            String _cadenaRuts){
        int rowsAffected =0;
        PreparedStatement ps = null;
        Connection dbConn = null;
        try{
            String sql = "delete "
                + "from detalle_asistencia "
                + "where fecha_hora_calculo::date <= '" + _fecha + "' "
                    + "and empresa_id = '" + _empresaId + "' "
                    + "and (rut_empleado in (" + _cadenaRuts + ") )";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[TraspasoHistoricosFMCJob.met]");
            ps = dbConn.prepareStatement(sql);
            
            rowsAffected = ps.executeUpdate();
            
            ps.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[GestionFemase."
                + "TraspasoHistoricosFemaseJob.deleteAsistencias]Error:"+sqle.toString());
        }
        
    }
    
    /**
    * Metodo que elimina las ausencias recien traspasadas a historico
    * de los ruts especificados y para la empresa indicada.
    * 
    * Solo se consideran los registros cuya ausencia.fecha_inicio sea menor o igual a la fecha especificada.
    * 
    * @param _empresaId
    * @param _fecha
    * @param _cadenaRuts
    * 
    */
    public void deleteAusencias(String _empresaId, 
            String _fecha, 
            String _cadenaRuts){
        int rowsAffected =0;
        PreparedStatement ps = null;
        Connection dbConn = null;
        try{
            String sql = "delete "
                + "from detalle_ausencia "
                + "where fecha_inicio <= '" + _fecha + "' "
                    + "and (rut_empleado in (" + _cadenaRuts + ") )";

            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[TraspasoHistoricosFMCJob.deleteAusencias]");
            ps = dbConn.prepareStatement(sql);
            
            rowsAffected = ps.executeUpdate();
            
            ps.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[GestionFemase."
                + "TraspasoHistoricosFemaseJob.deletAusencias]Error:"+sqle.toString());
        }
        
    }
    
    /**
    * Metodo que elimina registros de log traspasadas a historico
    * de los ruts especificados y para la empresa indicada.
    * 
    * Solo se consideran los registros cuya fecha sea menor o igual a la fecha especificada.
    * 
    * @param _empresaId
    * @param _fecha
    * 
    */
    public void deleteLogEventos(String _empresaId, String _fecha){
        int rowsAffected =0;
        PreparedStatement ps = null;
        Connection dbConn = null;
        try{
            String sql = "delete "
                + "from mantencion_evento "
                + "where fecha_hora::date <= '" + _fecha + "' "
                    + "and empresa_id_source='" + _empresaId + "'";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[TraspasoHistoricosFMCJob.met]");
            ps = dbConn.prepareStatement(sql);
            
            rowsAffected = ps.executeUpdate();
            
            ps.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[GestionFemase."
                + "TraspasoHistoricosFemaseJob.deleteLogEventos]Error:"+sqle.toString());
        }
        
    }
    
    /**
    * Obtiene registros existentes y que seran traspasados a la respectiva tabla historica
    * 
    * @param _empresaId
    * @param _fecha
    * @param _cadenaRuts
    * @return 
    */
    public int getCountMarcas(String _empresaId, 
            String _fecha, 
            String _cadenaRuts){
        int affectedRows        = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection dbConn       = null;
        try{
            String sql = "select count(correlativo) filas "
                + "from marca "
                + "where fecha_hora::date <= '" + _fecha + "' "
                    + "and empresa_cod='" + _empresaId + "' "
                    + "and (rut_empleado in (" + _cadenaRuts + ") )"; 
            
            System.out.println("[GestionFemase."
                + "TraspasoHistoricosFemaseJob.getCountMarcas]Sql: " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[TraspasoHistoricoDAO.getCountMarcas]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                affectedRows = rs.getInt("filas");
            }
            
            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            affectedRows = -1;
            System.err.println("[GestionFemase."
                + "TraspasoHistoricoDAO."
                + "getCountMarcas]"
                + "Error: " + sqle.toString());
        }
        
        return affectedRows;
    }
    
    /**
    * Obtiene registros existentes y que seran traspasados a la respectiva tabla historica
    * 
    * @param _empresaId
    * @param _fecha
    * @param _cadenaRuts
    * 
    * @return 
    */
    public int getCountMarcasRechazadas(String _empresaId, 
            String _fecha, 
            String _cadenaRuts){
        int affectedRows        = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection dbConn       = null;
        try{
            String sql = "select count(correlativo_rechazo) filas "
                + "from marca_rechazo "
                + "where fecha_hora::date <= '" + _fecha + "' "
                    + "and empresa_cod='" + _empresaId + "' "
                    + "and (rut_empleado in (" + _cadenaRuts + ") )"; 
            
            System.out.println("[GestionFemase."
                + "TraspasoHistoricosFemaseJob.getCountMarcasRechazadas]Sql: " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[TraspasoHistoricoDAO.getCountMarcasRechazadas]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                affectedRows = rs.getInt("filas");
            }
            
            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            affectedRows = -1;
            System.err.println("[GestionFemase."
                + "TraspasoHistoricoDAO."
                + "getCountMarcasRechazadas]"
                + "Error: " + sqle.toString());
        }
        
        return affectedRows;
    }
    
    /**
    * Obtiene registros existentes y que seran traspasados a la respectiva tabla historica
    * 
    * @param _empresaId
    * @param _fecha
    * @param _cadenaRuts
    * 
    * @return 
    */
    public int getCountAusencias(String _empresaId, 
            String _fecha, 
            String _cadenaRuts){
        int affectedRows        = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection dbConn       = null;
        try{
            String sql = "select count(correlativo) filas "
                + " from detalle_ausencia "
                    + "inner join empleado on (detalle_ausencia.rut_empleado = empleado.empl_rut) "
                + "where empleado.empresa_id='" + _empresaId + "' "
                    + "and fecha_ingreso::date <= '" + _fecha + "' "
                    + "and (empleado.empl_rut in (" + _cadenaRuts + ") )"; 
            
            System.out.println("[GestionFemase."
                + "TraspasoHistoricosFemaseJob.getCountAusencias]Sql: " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[TraspasoHistoricoDAO.getCountAusencias]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                affectedRows = rs.getInt("filas");
            }
            
            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            affectedRows = -1;
            System.err.println("[GestionFemase."
                + "TraspasoHistoricoDAO."
                + "getCountAusencias]"
                + "Error: " + sqle.toString());
        }
        
        return affectedRows;
    }
    
    /**
    * Obtiene registros existentes y que seran traspasados a la respectiva tabla historica
    * 
    * @param _empresaId
    * @param _fecha
    * @param _cadenaRuts
    * 
    * @return 
    */
    public int getCountAsistencia(String _empresaId, 
            String _fecha, 
            String _cadenaRuts){
        int affectedRows        = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection dbConn       = null;
        try{
            String sql = "select count(fecha_marca_entrada) filas "
                + "from detalle_asistencia "
                + "where fecha_marca_entrada <= '" + _fecha + "' "
                    + "and empresa_id='" + _empresaId + "' "
                    + "and (rut_empleado in (" + _cadenaRuts + ") )";
            
            System.out.println("[GestionFemase."
                + "TraspasoHistoricosFemaseJob.getCountAsistencia]Sql: " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[TraspasoHistoricoDAO.getCountAsistencia]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                affectedRows = rs.getInt("filas");
            }
            
            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            affectedRows = -1;
            System.err.println("[GestionFemase."
                + "TraspasoHistoricoDAO."
                + "getCountAsistencia]"
                + "Error: " + sqle.toString());
        }
        
        return affectedRows;
    }
    
    /**
    * Obtiene registros existentes y que seran traspasados a la respectiva tabla historica
    * 
    * @param _empresaId
    * @param _fecha
    * 
    * @return 
    */
    public int getCountLogEventos(String _empresaId, String _fecha){
        int affectedRows        = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection dbConn       = null;
        try{
            String sql = "select count(fecha_hora) filas "
                + "from mantencion_evento "
                + "where fecha_hora::date <= '" + _fecha + "' "
                    + "and empresa_id_source='" + _empresaId + "'"; 
            
            System.out.println("[GestionFemase."
                + "TraspasoHistoricosFemaseJob.getCountLogEventos]Sql: " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[TraspasoHistoricoDAO.getCountLogEventos]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                affectedRows = rs.getInt("filas");
            }
            
            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            affectedRows = -1;
            System.err.println("[GestionFemase."
                + "TraspasoHistoricoDAO."
                + "getCountLogEventos]"
                + "Error: " + sqle.toString());
        }
        
        return affectedRows;
    }
    
    public void closeConnection(){
        try {
            dbLocator.freeConnection(dbConn);
        } catch (Exception ex) {
            System.out.println("[GestionFemase."
                + "TraspasoHistoricosFMCJob."
                + "closeConnection]Error al cerrar conexion: " + ex.toString());
        }
    }
}
