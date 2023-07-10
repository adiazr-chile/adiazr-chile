/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.RegistroHistoricoVO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 *
 * @author Alexander
 */
public class TraspasoHistoricoDAO extends BaseDAO{

    //private Connection dbConn;
    //DbDirectConnection dbconnection = DbDirectConnection.getInstance();
    
    private Hashtable<String,String> NOMBRE_TABLAS_HISTORICAS = new Hashtable<>();
    
    public TraspasoHistoricoDAO() {
        NOMBRE_TABLAS_HISTORICAS.put("marca", "marca_historica");
        NOMBRE_TABLAS_HISTORICAS.put("marca_rechazo", "marca_rechazo_historica");
        NOMBRE_TABLAS_HISTORICAS.put("detalle_ausencia", "detalle_ausencia_historica");
        NOMBRE_TABLAS_HISTORICAS.put("detalle_asistencia", "detalle_asistencia_historica");
        NOMBRE_TABLAS_HISTORICAS.put("mantencion_evento", "mantencion_evento_historico");
        
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
    public ResultCRUDVO insertMarcasHistoricas(String _empresaId, 
            String _fecha, 
            String _cadenaRuts){
        ResultCRUDVO resultado = new ResultCRUDVO();
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
    public ResultCRUDVO insertMarcasRechazosHistoricas(String _empresaId, 
            String _fecha, 
            String _cadenaRuts){
        ResultCRUDVO resultado = new ResultCRUDVO();
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

            dbConn = dbLocator.getConnection(m_dbpoolName,"[TraspasoHistoricoDAO.insertMarcasRechazosHistoricas]");
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
                + "TraspasoHistoricoDAO.insertMarcasRechazosHistoricas]"
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
    public ResultCRUDVO insertAusenciasHistoricas(String _empresaId, 
            String _fecha, 
            String _cadenaRuts){
        ResultCRUDVO resultado = new ResultCRUDVO();
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
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TraspasoHistoricoDAO.insertAusenciasHistoricas]");
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
                + "TraspasoHistoricoDAO."
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
    public ResultCRUDVO insertDetalleAsistenciaHistoricos(String _empresaId, 
            String _fecha, 
            String _cadenaRuts){
        ResultCRUDVO resultado = new ResultCRUDVO();
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
                "[TraspasoHistoricoDAO.insertDetalleAsistenciaHistoricos]");
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
                + "TraspasoHistoricoDAO.insertDetalleAsistenciaHistoricos]"
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
    public ResultCRUDVO insertLogEventosHistoricos(String _empresaId, String _fecha){
        ResultCRUDVO resultado = new ResultCRUDVO();
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
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TraspasoHistoricoDAO.insertLogEventosHistoricos]");
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
                + "TraspasoHistoricoDAO."
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

            dbConn = dbLocator.getConnection(m_dbpoolName,"[TraspasoHistoricoDAO.met]");
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

            dbConn = dbLocator.getConnection(m_dbpoolName,"[TraspasoHistoricoDAO.met]");
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

            dbConn = dbLocator.getConnection(m_dbpoolName,"[TraspasoHistoricoDAO.met]");
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
                "[TraspasoHistoricoDAO.deleteAusencias]");
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

            dbConn = dbLocator.getConnection(m_dbpoolName,"[TraspasoHistoricoDAO.met]");
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
            
            System.out.println(WEB_NAME+"[GestionFemase."
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
            
            System.out.println(WEB_NAME+"[GestionFemase."
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
            
            System.out.println(WEB_NAME+"[GestionFemase."
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
            
            System.out.println(WEB_NAME+"[GestionFemase."
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
            
            System.out.println(WEB_NAME+"[GestionFemase."
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
            System.out.println(WEB_NAME+"[GestionFemase."
                + "TraspasoHistoricoDAO."
                + "closeConnection]Error al cerrar conexion: " + ex.toString());
        }
    }
    
    /**
    * Obtiene ultimo registro historico traspasado
    * 
    * @param _empresaId
    * @param _tabla
    * @return 
    */
    public RegistroHistoricoVO getUltimoRegistroHistorico(String _empresaId, 
            String _tabla){
        RegistroHistoricoVO registro = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection dbConn       = null;
        try{
            /**
             *  marca_historica			fecha_hora          empresa_cod		
                marca_rechazo_historica		fecha_hora          empresa_cod
                detalle_ausencia_historica	fecha_inicio        empresa_id
                detalle_asistencia_historica	fecha_hora_calculo  empresa_id
                mantencion_evento_historico	fecha_hora          empresa_id
             */
            
            String columnaFechaHora = "fecha_hora";
            if (_tabla.compareTo("detalle_ausencia") == 0) columnaFechaHora = "fecha_inicio";
            else if (_tabla.compareTo("detalle_asistencia") == 0) columnaFechaHora = "fecha_hora_calculo";
            
            String columnaEmpresa = "empresa_id";
            if (_tabla.compareTo("marca") == 0 || _tabla.compareTo("marca_rechazo") == 0) columnaEmpresa = "empresa_cod";
            
            String nombreTablaHist = NOMBRE_TABLAS_HISTORICAS.get(_tabla);
            
            String sql = "select to_char(" + columnaFechaHora + ",'yyyy-MM-dd') fecha_registro,"
                + "to_char(fecha_hora_traspaso,'yyyy-MM-dd HH24:MI:SS') fecha_hora_traspaso "
                + "from " + nombreTablaHist
                + " where " + columnaEmpresa + " = '" + _empresaId + "' "
                + "order by " + columnaFechaHora + " desc limit 1";
            
            System.out.println(WEB_NAME+"[GestionFemase."
                + "TraspasoHistoricosFemaseJob.getUltimoRegistroHistorico]Sql: " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[TraspasoHistoricoDAO.getUltimoRegistroHistorico]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                registro = new RegistroHistoricoVO();
                registro.setEmpresaId(_empresaId);
                registro.setTabla(_tabla);
                registro.setFechaRegistro(rs.getString("fecha_registro"));
                registro.setFechaHoraTraspaso(rs.getString("fecha_hora_traspaso"));
            }
            
            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[GestionFemase."
                + "TraspasoHistoricoDAO."
                + "getUltimoRegistroHistorico]"
                + "Error: " + sqle.toString());
        }
        
        return registro;
    }
    
    /**
    * Obtiene numero de registros para traspaso a historico
    * 
    * @param _empresaId
    * @param _tabla
     * @param _startDate
     * @param _endDate
    * 
    * @return 
    */
    public RegistroHistoricoVO getVistaPreviaTraspasoHistorico(String _empresaId, 
            String _tabla, 
            String _startDate, 
            String _endDate){
        RegistroHistoricoVO registro = new RegistroHistoricoVO();
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection dbConn       = null;
        try{
            String strSql = "";
            switch (_tabla){
                case "marca":
                    strSql = "select count(fecha_hora) registros "
                        + "from marca "
                        + "where empresa_cod = '" + _empresaId + "' "
                        + "and fecha_hora::date between '" + _startDate + "' and '" + _endDate + "'";
                    break;
                case "marca_rechazo":
                    strSql = "select count(fecha_hora) registros "
                        + "from marca_rechazo "
                        + "where empresa_cod = '" + _empresaId + "' "
                        + "and fecha_hora::date between '" + _startDate + "' and '" + _endDate + "'";
                    break;    
                case "detalle_ausencia":
                    strSql = "select count(fecha_inicio) registros "
                        + "from detalle_ausencia "
                        + "where rut_empleado in "
                            + "( "
                            + "select empl_rut from empleado where empresa_id = '" + _empresaId + "' "
                            + ") "
                            + "and fecha_inicio::date between '" + _startDate + "' and '" + _endDate + "'";
                    break;
                case "detalle_asistencia":
                    strSql = "select count(fecha_hora_calculo) registros "
                        + "from detalle_asistencia "
                        + "where empresa_id = '" + _empresaId + "' "
                        + "and fecha_hora_calculo::date between '" + _startDate + "' and '" + _endDate + "'";
                    break;    
                case "mantencion_evento":
                    strSql = "select count(fecha_hora) registros "
                        + "from mantencion_evento "
                        + "where empresa_id = '" + _empresaId + "' "
                        + "and fecha_hora::date between '" + _startDate + "' and '" + _endDate + "'";
                    break;    
            }

            System.out.println(WEB_NAME+"[GestionFemase."
                + "TraspasoHistoricosFemaseJob.getVistaPreviaTraspasoHistorico]Sql: " + strSql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[TraspasoHistoricoDAO.getVistaPreviaTraspasoHistorico]");
            ps = dbConn.prepareStatement(strSql);
            rs = ps.executeQuery();

            if (rs.next()){
                registro = new RegistroHistoricoVO();
                registro.setEmpresaId(_empresaId);
                registro.setTabla(_tabla);
                registro.setNumRegistros(rs.getDouble("registros"));
            }
            
            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[GestionFemase."
                + "TraspasoHistoricoDAO."
                + "getVistaPreviaTraspasoHistorico]"
                + "Error: " + sqle.toString());
        }
        
        return registro;
    }
    
    /**
    * Metodo que traspasa a historico las marcas de asistencia
    * 
    * @param _empresaId
    * @param _startDate
    * @param _endDate
    * @return 
    */
    public ResultCRUDVO traspasaMarcasHistoricas(String _empresaId, 
            String _startDate, 
            String _endDate){
        ResultCRUDVO resultado = new ResultCRUDVO();
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
                + "where empresa_cod = '" + _empresaId + "' "
                    + "and fecha_hora::date between '" + _startDate + "' and '" + _endDate + "'";
            
            System.err.println("[GestionFemase."
                + "TraspasoHistoricosFemaseJob.traspasaMarcasHistoricas]Sql: " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[TraspasoHistoricoDAO.traspasaMarcasHistoricas]");
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
                + "traspasaMarcasHistoricas]"
                + "Error: " + sqle.toString());
        }
        
        return resultado;
    }
    
    
    /**
    * Metodo que traspasa a historico las marcas de asistencia rechazadas
    * 
    * @param _empresaId
    * @param _startDate
    * @param _endDate
    * 
    * @return 
    */
    public ResultCRUDVO traspasaMarcasRechazosHistoricas(String _empresaId, 
            String _startDate, 
            String _endDate){
        ResultCRUDVO resultado = new ResultCRUDVO();
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
                + "where empresa_cod = '" + _empresaId + "' "
                + "and fecha_hora::date between '" + _startDate + "' and '" + _endDate + "'";

            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[TraspasoHistoricoDAO.traspasaMarcasRechazosHistoricas]");
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
                + "TraspasoHistoricoDAO.traspasaMarcasRechazosHistoricas]"
                + "Error:" + sqle.toString());
        }
        
        return resultado;
    }
 
    
    /**
    * Metodo que traspasa a historico los detalles ausencias.
    * 
    * @param _empresaId
    * @param _startDate
    * @param _endDate
    * @return 
    */
    public ResultCRUDVO traspasaDetallesAusenciasHistoricas(String _empresaId, 
            String _startDate, 
            String _endDate){
        ResultCRUDVO resultado = new ResultCRUDVO();
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
                    + "fecha_hora_traspaso, "
                    + "empresa_id,"
                    + "saldo_dias_vacaciones_asignadas,"
                    + "dias_acumulados_vacaciones_asignadas,"
                    + "dias_efectivos_vacaciones,"
                    + "dias_efectivos_vba,"
                    + "dias_efectivos_vp,"
                    + "saldo_vba_pre_vacaciones,"
                    + "saldo_vp_pre_vacaciones,"
                    + "saldo_vba_post_vacaciones,"
                    + "saldo_vp_post_vacaciones,"
                    + "dias_solicitados,"
                    + "saldo_post_pa "
                    + " ) "
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
                    + "saldo_dias_vacaciones_asignadas,"
                    + "dias_acumulados_vacaciones_asignadas,"
                    + "dias_efectivos_vacaciones,"
                    + "dias_efectivos_vba,"
                    + "dias_efectivos_vp,"
                    + "saldo_vba_pre_vacaciones,"
                    + "saldo_vp_pre_vacaciones,"
                    + "saldo_vba_post_vacaciones,"
                    + "saldo_vp_post_vacaciones,"
                    + "dias_solicitados,"
                    + "saldo_post_pa "
                + " from detalle_ausencia "
                + "where rut_empleado in "
                    + "( "
                    + "select empl_rut from empleado where empresa_id = '" + _empresaId + "' "
                    + ") "
                    + "and fecha_inicio::date between '" + _startDate + "' and '" + _endDate + "'";
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[TraspasoHistoricoDAO.traspasaDetallesAusenciasHistoricas]");
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
                + "TraspasoHistoricoDAO."
                + "traspasaDetallesAusenciasHistoricas]Error: " + sqle.toString());
        }
        
        return resultado;
    }
    
    /**
    * Metodo que traspasa a historico los calculos de asistencia
    * 
    * @param _empresaId
    * @param _startDate
    * @param _endDate
    * @return 
    */
    public ResultCRUDVO traspasaDetalleAsistenciaHistoricos(String _empresaId, 
            String _startDate, 
            String _endDate){
        ResultCRUDVO resultado = new ResultCRUDVO();
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
                + "where empresa_id = '" + _empresaId + "' "
                + "and fecha_hora_calculo::date between '" + _startDate + "' and '" + _endDate + "'";
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[TraspasoHistoricoDAO.traspasaDetalleAsistenciaHistoricos]");
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
                + "TraspasoHistoricoDAO.traspasaDetalleAsistenciaHistoricos]"
                + "Error: " + sqle.toString());
        }
        
        return resultado;
    }
    
    /**
    * Metodo que traspasa a historico los logs de auditoria.
    * 
    * @param _empresaId
    * @param _startDate
    * @param _endDate
    * @return 
    */
    public ResultCRUDVO traspasaLogEventosHistoricos(String _empresaId, 
            String _startDate, 
            String _endDate){
        ResultCRUDVO resultado = new ResultCRUDVO();
        int affectedRows =0;
        PreparedStatement ps = null;
        Connection dbConn = null;
        try{
            String sql = "insert into mantencion_evento_historico "
                + "(username,  descripcion_evento,  ip_usuario,"
                    + "tipo_evento,fecha_hora,empresa_id,depto_id,"
                    + "cenco_id,empleado_rut,empresa_id_source,"
                    + "fecha_hora_traspaso,"
                    + "operating_system,"
                    + "browser_name) "
                + "select "
                + "username,descripcion_evento,ip_usuario,tipo_evento,"
                + "fecha_hora,empresa_id,depto_id,cenco_id,"
                + "empleado_rut,empresa_id_source,current_timestamp "
                + "from mantencion_evento "
                + "where empresa_id = '" + _empresaId + "' "
                + "and fecha_hora::date between '" + _startDate + "' and '" + _endDate + "'";
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TraspasoHistoricoDAO.insertLogEventosHistoricos]");
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
                + "TraspasoHistoricoDAO."
                + "insertLogEventosHistoricos]Error: " + sqle.toString());
        }
        
        return resultado;
    }
    
    /**
    * Metodo que elimina las marcas recien traspasadas a historico
    * de los ruts especificados y para la empresa indicada.Solo se consideran los registros cuya fecha sea menor o igual a la fecha especificada.
    * 
    * @param _empresaId
    * @param _tabla
    * @param _startDate
    * @param _endDate
    * @return 
    * 
    */
    public int eliminaRegistrosTraspasados(String _empresaId, 
            String _tabla, 
            String _startDate, 
            String _endDate){
        int affectedRows =0;
        PreparedStatement ps = null;
        Connection dbConn = null;
        try{
            String strSql = "";
            
            switch (_tabla){
                case "marca":
                    strSql = "delete "
                        + "from marca "
                        + "where empresa_cod = '" + _empresaId + "' "
                        + "and fecha_hora::date between '" + _startDate + "' and '" + _endDate + "'";
                    break;
                case "marca_rechazo":
                    strSql = "delete "
                        + "from marca_rechazo "
                        + "where empresa_cod = '" + _empresaId + "' "
                        + "and fecha_hora::date between '" + _startDate + "' and '" + _endDate + "'";
                    break;    
                case "detalle_ausencia":
                    strSql = "delete "
                        + "from detalle_ausencia "
                        + "where rut_empleado in "
                            + "( "
                            + "select empl_rut from empleado where empresa_id = '" + _empresaId + "' "
                            + ") "
                            + "and fecha_inicio::date between '" + _startDate + "' and '" + _endDate + "'";
                    break;
                case "detalle_asistencia":
                    strSql = "delete "
                        + "from detalle_asistencia "
                        + "where empresa_id = '" + _empresaId + "' "
                        + "and fecha_hora_calculo::date between '" + _startDate + "' and '" + _endDate + "'";
                    break;    
                case "mantencion_evento":
                    strSql = "delete "
                        + "from mantencion_evento "
                        + "where empresa_id = '" + _empresaId + "' "
                        + "and fecha_hora::date between '" + _startDate + "' and '" + _endDate + "'";
                    break;    
            }
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[TraspasoHistoricoDAO.eliminaRegistrosTraspasados]");
            ps = dbConn.prepareStatement(strSql);
            
            affectedRows = ps.executeUpdate();
            
            ps.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[GestionFemase."
                + "TraspasoHistoricoDAO.eliminaRegistrosTraspasados]Error:"+sqle.toString());
        }
        
        return affectedRows;
    }
    
}
