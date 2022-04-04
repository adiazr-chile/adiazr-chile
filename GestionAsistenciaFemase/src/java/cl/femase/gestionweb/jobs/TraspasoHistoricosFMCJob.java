/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.jobs;
        
//import cl.femase.gestionweb.common.DbDirectConnection;
import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.common.DatabaseLocator;
import cl.femase.gestionweb.common.GetPropertyValues;
import cl.femase.gestionweb.common.MailSender;
import cl.femase.gestionweb.dao.ProcesosDAO;
import cl.femase.gestionweb.vo.ProcesoEjecucionVO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Job encargado de generar archivos CSV con marcas rechazadas
 *  
 */

public class TraspasoHistoricosFMCJob implements Job {

    GetPropertyValues m_properties = new GetPropertyValues();
    
    DatabaseLocator dbLocator;
    String m_dbpoolName;
    
    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        
        JobDataMap data = arg0.getJobDetail().getJobDataMap();
        String empresaId = data.getString("empresa_id");
        int procesoId = data.getInt("proceso_id");
        String execUser = data.getString("exec_user");
        
        try {
            dbLocator  = DatabaseLocator.getInstance();
            m_dbpoolName = m_properties.getKeyValue("dbpoolname");
        } catch (DatabaseException ex) {
            System.err.println("Error: "+ex.toString());
        }
        
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ProcesosDAO daoProcesos=new ProcesosDAO(null);
        ProcesoEjecucionVO ejecucion=new ProcesoEjecucionVO();
        Date start = new Date();
        System.out.println("\n[GestionFemase."
            + "TraspasoHistoricosFMCJob]"
            + "INICIO - Traspaso a tablas historicas los "
            + "registros con mas de 6 meses de antiguedad. "
            + "ProcesoId: " + procesoId
            + ", empresa_id: "+ empresaId
            + "Start at " + new Date());
        
        /**
         * Seteo de objeto para envio de correo
         * 
         */
        String fromLabel = "Gestion asistencia";
        String fromMail = m_properties.getKeyValue("mailFrom");
        String asuntoMail   = "Sistema de Gestion-Traspaso Historico Fundacion Mi Casa";
        String mailBody = "";
        String mailTo = m_properties.getKeyValue("mailAdmin");
//        System.out.println("[GestionFemase."
//            + "TraspasoHistoricosFMCJob]"
//            + "Enviando correo al admin ("+m_properties.getKeyValue("mailAdmin")+"), "
//            + "informando que se ha procedido a realizar traspaso a tablas historicas");
//        
        try {
            
            int numMarcas = insertMarcasHistoricas(empresaId);
            int numMarcasRechazo = insertMarcasRechazosHistoricas(empresaId);
            int numAusencias = insertAusenciasHistoricas(empresaId);
            int numDetallesAsistencia = insertDetalleAsistenciaHistoricos(empresaId);
            int numLogEventos = insertLogEventosHistoricos(empresaId);
            
            ejecucion.setEmpresaId(empresaId);
            ejecucion.setProcesoId(procesoId);
            ejecucion.setFechaHoraInicioEjecucion(sdf.format(start));
            if (numMarcas > 0) {
                System.out.println("[GestionFemase."
                    + "TraspasoHistoricosFMCJob]"
                    + "Eliminando marcas recien traspasadas a historico");
                deleteMarcas(empresaId);
            }
            if (numMarcasRechazo > 0) {
                System.out.println("[GestionFemase."
                    + "TraspasoHistoricosFMCJob]"
                    + "Eliminando marcas rechazadas recien traspasadas a historico");
                deleteMarcasRechazadas(empresaId);
            
            }
            
            if (numDetallesAsistencia > 0) {
                System.out.println("[GestionFemase."
                    + "TraspasoHistoricosFMCJob]"
                    + "Eliminando detalle_asistencia recien traspasadas a historico");
                deleteAsistencias(empresaId);
            }
            if (numAusencias > 0) {
                System.out.println("[GestionFemase."
                    + "TraspasoHistoricosFMCJob]"
                    + "Eliminando detalle_ausencia recien traspasadas a historico");
                deleteAusencias(empresaId);
            }
            if (numLogEventos > 0) {
                System.out.println("[GestionFemase."
                    + "TraspasoHistoricosFMCJob]"
                    + "Eliminando log de eventos recien traspasadas a historico");
                deleteLogEventos(empresaId);
                
            }
            
            String result="Registros historicos insertados:"
                + "Marcas = " + numMarcas + ","
                + "Marcas rechazadas= " + numMarcasRechazo + ","
                + "Ausencias = " + numAusencias + ","
                + "Log eventos = " + numLogEventos + ","
                + "Detalle asistencia = " + numDetallesAsistencia;
            ejecucion.setFechaHoraFinEjecucion(sdf.format(new Date()));
            ejecucion.setResultado(result);
            ejecucion.setUsuario(execUser);
            daoProcesos.insertItinerario(ejecucion);
            
            mailBody = "<p>Registros historicos insertados:"
                + "<p>Marcas = " + numMarcas + ",</p>"
                + "<p>Marcas rechazadas= " + numMarcasRechazo + ",</p>"
                + "<p>Ausencias = " + numAusencias + ",</p>"
                + "<p>Log eventos = " + numLogEventos + ",</p>"
                + "<p>Detalle asistencia = " + numDetallesAsistencia + ",</p>"
                + "<br><br><b> Atte. Equipo Soporte FEMASE</b>";
            System.out.println("[GestionFemase."
                + "TraspasoHistoricosFMCJob]"
                + "Registros historicos insertados:"
                + "Marcas = " + numMarcas + ","    
                + "Ausencias = " + numAusencias + ","
                + "Log eventos = " + numLogEventos + ","
                + "Detalle asistencia = " + numDetallesAsistencia);
            MailSender.sendWithAttachment(null, fromLabel, fromMail, mailTo, asuntoMail,mailBody);    
            System.out.println("\n[GestionFemase."
                + "TraspasoHistoricosFMCJob]"
                + "FIN - Traspaso historico. "
                + "End at " + new Date());
            
        } catch (Exception ex) {
            System.err.println("[GestionFemase."
                + "TraspasoHistoricosFMCJob]Error:"+ex.toString());
            String result="Registros historicos error";
            ejecucion.setFechaHoraFinEjecucion(sdf.format(new Date()));
            ejecucion.setResultado(result);
            ejecucion.setUsuario(execUser);
            daoProcesos.insertItinerario(ejecucion);
        }
        
    }
    
    /**
    * Las marcas de asistencia
    * con mas de 6 meses de antiguedad, son traspasadas a tabla historica.
    */
    private int insertMarcasHistoricas(String _empresaId){
        int rowsAffected =0;
        PreparedStatement ps = null;
        Connection dbConn = null;
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
                    + "modificada,comentario, "
                    + "fecha_hora_traspaso, "
                    + "correlativo, "
                    + "cod_tpo_marca_manual," 
                    + "latitud," 
                    + "longitud) "
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
                        + "comentario, "
                        + "current_timestamp, "
                        + "correlativo, "
                        + "cod_tpo_marca_manual, " 
                        + "latitud," 
                        + "longitud "
                    + "from marca "
                    + "where fecha_hora::date < (current_date - interval '6 month')::date "
                    + "and empresa_cod='" + _empresaId + "'";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[TraspasoHistoricosFMCJob.insertMarcasHistoricas]");
            ps = dbConn.prepareStatement(sql);
            
            rowsAffected = ps.executeUpdate();
            
            ps.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            rowsAffected = -1;
            System.err.println("[GestionFemase."
                + "TraspasoHistoricosFMCJob.insertMarcasHistoricas]Error:"+sqle.toString());
        }
        
        return rowsAffected;
    }
    
    
    
    /**
    * Las marcas de asistencia rechazadas y 
    * con mas de 6 meses de antiguedad, son traspasadas a tabla historica.
    */
    private int insertMarcasRechazosHistoricas(String _empresaId){
        int rowsAffected =0;
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
                            + "fecha_hora_traspaso) "
                + "select cod_dispositivo,"
                    + "empresa_cod,"
                    + "rut_empleado,"
                    + "	fecha_hora,"
                    + "cod_tipo_marca,"
                    + "fecha_hora_actualizacion,"
                    + "id,"
                    + "hashcode,"
                    + "motivo_rechazo,"
                    + "correlativo_rechazo, "
                    + "current_timestamp "
                    + "from marca_rechazo "
                    + "where fecha_hora::date < (current_date - interval '6 month')::date "
                    + "and empresa_cod='" + _empresaId + "'";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[TraspasoHistoricosFMCJob.insertMarcasRechazosHistoricas]");
            ps = dbConn.prepareStatement(sql);
            
            rowsAffected = ps.executeUpdate();
            
            ps.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            rowsAffected = -1;
            System.err.println("[GestionFemase."
                + "TraspasoHistoricosFMCJob.insertMarcasRechazosHistoricas]Error:"+sqle.toString());
        }
        
        return rowsAffected;
    }
    
    /**
     * Los log de eventos de sistema
     * con mas de 6 meses de antiguedad, son traspasadas a tabla historica.
     */
    private int insertLogEventosHistoricos(String _empresaId){
        int rowsAffected =0;
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
                + "where "
                + "fecha_hora::date < (current_date - interval '6 month')::date "
                    + "and empresa_id_source='" + _empresaId + "'";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[TraspasoHistoricosFMCJob.insertLogEventosHistoricos]");
            ps = dbConn.prepareStatement(sql);
            
            rowsAffected = ps.executeUpdate();
            
            ps.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            rowsAffected =-1;
            System.err.println("[GestionFemase."
                + "TraspasoHistoricosFMCJob."
                + "insertLogEventosHistoricos]Error:"+sqle.toString());
        }
        
        return rowsAffected;
    }
    
    /**
     * Las ausencias de empleados 
     * con mas de 6 meses de antiguedad, son traspasadas a tabla historica.
     */
    private int insertAusenciasHistoricas(String _empresaId){
        int insertedRows =0;
        PreparedStatement ps = null;
        Connection dbConn = null;
        try{
            String sql = "insert into detalle_ausencia_historica ("
                    + "rut_empleado,"
                    + "fecha_ingreso,"
                    + "ausencia_id,"
                    + "fecha_inicio,"
                    + "fecha_fin,"
                    + "rut_autoriza_ausencia,"
                    + "ausencia_autorizada,"
                    + "fecha_actualizacion,"
                    + "hora_inicio,"
                    + "hora_fin,"
                    + "allow_hour,"
                    + "correlativo,"
                    + "fecha_hora_traspaso, "
                    + "empresa_id,"
                    + "saldo_dias_vacaciones_asignadas, "
                    + "dias_acumulados_vacaciones_asignadas,"
                    + "dias_efectivos_vacaciones,"
                    + "dias_efectivos_vba, "
                    + "dias_efectivos_vp, "
                    + "saldo_vba_pre_vacaciones,"
                    + "saldo_vp_pre_vacaciones, "
                    + "saldo_vba_post_vacaciones,"
                    + "saldo_vp_post_vacaciones "
                    + ") "
                + " select "
                    + " rut_empleado, "
                    + "fecha_ingreso,"
                    + "ausencia_id,"
                    + "fecha_inicio,"
                    + "fecha_fin,"
                    + "rut_autoriza_ausencia,"
                    + "ausencia_autorizada,"
                    + "fecha_actualizacion,"
                    + "hora_inicio,"
                    + "hora_fin,"
                    + "allow_hour,"
                    + "correlativo,"
                    + "current_timestamp,'" + _empresaId + "',"
                    + "saldo_dias_vacaciones_asignadas, "
                    + "dias_acumulados_vacaciones_asignadas,"
                    + "dias_efectivos_vacaciones,"
                    + "dias_efectivos_vba, "
                    + "dias_efectivos_vp, "
                    + "saldo_vba_pre_vacaciones,"
                    + "saldo_vp_pre_vacaciones, "
                    + "saldo_vba_post_vacaciones,"
                    + "saldo_vp_post_vacaciones "
                + " from detalle_ausencia "
                    + "inner join empleado on (detalle_ausencia.rut_empleado = empleado.empl_rut) "
                + " where fecha_ingreso::date < (current_date - interval '6 month')::date "
                    + "and empleado.empresa_id='" + _empresaId + "'";

            //System.out.println("[insertAusenciasHistoricas]sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TraspasoHistoricosFMCJob.insertAusenciasHistoricas]");
            ps = dbConn.prepareStatement(sql);
            
            insertedRows = ps.executeUpdate();
            
            ps.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            insertedRows =-1;
            System.err.println("[GestionFemase."
                + "TraspasoHistoricosFMCJob.insertAusenciasHistoricas]Error:"+sqle.toString());
        }
        
        return insertedRows;
    }
    
    /**
     * Los detalles de asistencia (planillas de asistencia) de empleados 
     * con mas de 6 meses de antiguedad, son traspasadas a tabla historica.
     */
    private int insertDetalleAsistenciaHistoricos(String _empresaId){
        int insertedRows =0;
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
                + "  hhmm_extras_autorizadas,  marca_entrada_comentario,  marca_salida_comentario,  fecha_hora_traspaso) "
                + "select "
                + "  empresa_id ,  depto_id ,  cenco_id ,  rut_empleado,  fecha_hora_calculo,  fecha_marca_entrada,  hora_entrada ,"
                + "  hora_salida ,  horas_teoricas ,  horas_trabajadas ,  minutos_extras_50 ,  hora_inicio_ausencia ,  hora_fin_ausencia ,"
                + "  minutos_trabajados ,  horas_extras ,  holgura_minutos ,  es_feriado ,  minutos_extras_100 ,  hora_entrada_teorica ,"
                + "  hora_salida_teorica ,  minutos_extras ,  art22 ,  minutos_atraso ,  minutos_no_trabajados_entrada ,  minutos_no_trabajados_salida ,"
                + "  autoriza_mins_no_trab_entrada ,  autoriza_mins_no_trab_salida,  hrs_presenciales,  hrs_trabajadas,  observacion,"
                + "  hrs_ausencia,  fecha_marca_salida,  hhmm_extras,  hhmm_atraso,  autoriza_atraso,  autoriza_hrsextras,  hhmm_justificadas,"
                + "  hhmm_extras_autorizadas,  marca_entrada_comentario,  marca_salida_comentario,  current_timestamp "
                + "  from detalle_asistencia "
                + "  where fecha_marca_entrada < (current_date - interval '6 month')::date "
                    + " and empresa_id='" + _empresaId + "' ";

            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[TraspasoHistoricosFMCJob.insertDetalleAsistenciaHistoricos]");
            ps = dbConn.prepareStatement(sql);
            
            insertedRows = ps.executeUpdate();
            
            ps.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            insertedRows =-1;
            System.err.println("[GestionFemase."
                + "TraspasoHistoricosFMCJob.insertDetalleAsistenciaHistoricos]Error:"+sqle.toString());
        }
        
        return insertedRows;
    }
    
    /**
     * Elimina registros de marcas 
     * con mas de 6 meses de antiguedad (ahora en tabla historica).
     */
    private void deleteMarcas(String _empresaId){
        int rowsAffected =0;
        PreparedStatement ps = null;
        Connection dbConn = null;
        try{
            String sql = "delete "
                + "from marca "
                + "where fecha_hora::date < (current_date - interval '6 month')::date "
                    + "and marca.empresa_cod='" + _empresaId + "'";

            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[TraspasoHistoricosFMCJob.deleteMarcas]");
            ps = dbConn.prepareStatement(sql);
            
            rowsAffected = ps.executeUpdate();
            
            ps.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[GestionFemase."
                + "TraspasoHistoricosFMCJob.deletMarcas]Error:"+sqle.toString());
        }
        
    }
    
    /**
     * Elimina registros de marcas rechazadas
     * con mas de 6 meses de antiguedad (ahora en tabla historica).
     */
    private void deleteMarcasRechazadas(String _empresaId){
        int rowsAffected =0;
        PreparedStatement ps = null;
        Connection dbConn = null;
        try{
            String sql = "delete "
                + "from marca_rechazo "
                + "where fecha_hora::date < (current_date - interval '6 month')::date "
                    + "and empresa_cod='" + _empresaId + "'";

            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[TraspasoHistoricosFMCJob.deleteMarcasRechazadas]");
            ps = dbConn.prepareStatement(sql);
            
            rowsAffected = ps.executeUpdate();
            
            ps.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[GestionFemase."
                + "TraspasoHistoricosFMCJob.deleteMarcasRechazadas]Error:"+sqle.toString());
        }
        
    }
    
    /**
     * Elimina registros de ausencias 
     * con mas de 6 meses de antiguedad (ahora en tabla historica).
     */
    private void deleteAusencias(String _empresaId){
        int rowsAffected =0;
        PreparedStatement ps = null;
        Connection dbConn = null;
        try{
            String sql = "delete "
                + "from detalle_ausencia "
                + "where fecha_ingreso::date < (current_date - interval '6 month')::date "
                    + "and detalle_ausencia.rut_empleado in (select empl_rut from empleado where empresa_id='" + _empresaId + "')";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[TraspasoHistoricosFMCJob.deleteAusencias]");
            ps = dbConn.prepareStatement(sql);
            
            rowsAffected = ps.executeUpdate();
            
            ps.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[GestionFemase."
                + "TraspasoHistoricosFMCJob.deletAusencias]Error:"+sqle.toString());
        }
        
    }
    
    /**
     * Elimina registros de asistencia (planillas) 
     * con mas de 6 meses de antiguedad (ahora en tabla historica).
     */
    private void deleteAsistencias(String _empresaId){
        int rowsAffected =0;
        PreparedStatement ps = null;
        Connection dbConn = null;
        try{
            String sql = "delete "
                + "from detalle_asistencia "
                + "where fecha_marca_entrada < (current_date - interval '6 month')::date "
                    + "and empresa_id='" + _empresaId + "'";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[TraspasoHistoricosFMCJob.deleteAsistencias]");
            ps = dbConn.prepareStatement(sql);
            
            rowsAffected = ps.executeUpdate();
            
            ps.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[GestionFemase."
                + "TraspasoHistoricosFMCJob.deleteAsistencias]Error:"+sqle.toString());
        }
        
    }
    
    /**
     * Elimina registros de eventos (log) 
     * con mas de 6 meses de antiguedad (ahora en tabla historica).
     */
    private void deleteLogEventos(String _empresaId){
        int rowsAffected =0;
        PreparedStatement ps = null;
        Connection dbConn = null;
        try{
            String sql = "delete "
                + "from mantencion_evento "
                + "where fecha_hora::date < (current_date - interval '6 month')::date "
                    + "and empresa_id_source = '" + _empresaId + "'";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[TraspasoHistoricosFMCJob.deleteLogEventos]");
            ps = dbConn.prepareStatement(sql);
            
            rowsAffected = ps.executeUpdate();
            
            ps.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[GestionFemase."
                + "TraspasoHistoricosFMCJob.deleteLogEventos]Error:"+sqle.toString());
        }
        
    }
    
}
