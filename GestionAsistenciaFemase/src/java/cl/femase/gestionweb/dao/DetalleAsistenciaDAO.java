/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.CalculoHorasVO;
import cl.femase.gestionweb.vo.DetalleAsistenciaToInsertVO;
import cl.femase.gestionweb.vo.DetalleAsistenciaVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexander
 */
public class DetalleAsistenciaDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    //private Connection dbConn;
    //DbDirectConnection dbconnection = DbDirectConnection.getInstance();
    
    private String SQL_INSERT = "INSERT INTO detalle_asistencia "
        + "(empresa_id, "
        + "depto_id, "
        + "cenco_id, "
        + "rut_empleado, "
        + "fecha_hora_calculo, "
        + "fecha_marca_entrada, "
        + "hora_entrada, "
        + "hora_salida, "
        + "horas_teoricas, "
        + "horas_trabajadas, "
        + "minutos_extras_50,"
        + "horas_extras,"
        + "minutos_trabajados, "
        + "holgura_minutos, "
        + "es_feriado,"
        + "minutos_extras_100,"
        + "hora_entrada_teorica," 
        + "hora_salida_teorica,"
        + "art22,"
        + "minutos_atraso,"
        + "minutos_no_trabajados_entrada,"
        + " minutos_no_trabajados_salida,"
        + "hora_inicio_ausencia, "
        + "hora_fin_ausencia,"
        + "hrs_presenciales, "
            + "hrs_trabajadas, "
            + "observacion,"
            + "hrs_ausencia, "
            + "minutos_extras,"
            + "fecha_marca_salida,"
            + "hhmm_extras,"
            + "hhmm_atraso,"
            + "hhmm_justificadas,"
            + "marca_entrada_comentario,"
            + "marca_salida_comentario,"
            + "hhmm_salida_anticipada,"
            + "hrs_no_trabajadas) "
        + " VALUES ("
            + "?, ?, "
            + "?, ?, "
            + "?, ?, "
            + "?, ?, "
            + "?, ?, "
            + "?, ?, "
            + "?, ?,"
            + "?, ?,"
            + "?, ?,"
            + "?, ?,"
            + "?, ?,"
            + "?, ?,"
            + "?, ?,"
            + "?, ?,"
            + "?, ?,"
            + "?, ?, "
            + "?, ?,"
            + "?, ?, ?)";
    
    private final String SQL_DELETE ="delete "
        + "FROM detalle_asistencia "
        + "where rut_empleado = ? "
        + " and fecha_marca_entrada = ?";
    
    private final String SQL_UPDATE_MARCAS_PROCESADAS = "UPDATE marca "
        + "SET procesada = true "
        + "WHERE rut_empleado = ? "
        + "and fecha_hora = ? "
        + "and empresa_cod = ? "
        + "and cod_tipo_marca = ?";

    public DetalleAsistenciaDAO(PropertiesVO _propsValues) {
        //openDbConnection();
//        try {
//            dbLoc = DatabaseLocator.getInstance();
//            m_dbpoolName = _propsValues.getDbPoolName();
//            
//        } catch (DatabaseException ex) {
//            m_logger.error("DbError: "+ex.toString());
//        }
    }
    
    /**
     *
     */
    public void openDbConnection(){
        try{
            dbConn = dbLocator.getConnection(m_dbpoolName,"[DetalleAsistenciaDAO.openDbConnection]");
        }catch(DatabaseException dbe){
            System.err.println("[openDbConnection]"
                + "Error: " + dbe.toString());
            
        }
    }
    
    public void closeDbConnection(){
        try {
            dbLocator.freeConnection(dbConn);
        } catch (Exception ex) {
            System.err.println("[closeDbConnection]Error: "+ex.toString());
        }
    }
    
//    /**
//     * elimina
//     * @param _rutEmpleado
//     * @param _fechaMarca
//     * @return 
//     */
//    public ResultCRUDVO delete(String _rutEmpleado,
//            String _fechaMarca){
//        ResultCRUDVO objresultado = new ResultCRUDVO();
//        PreparedStatement psupdate = null;
//        int result=0;
//        String msgError = "Error al eliminar "
//            + "detalle_asistencia "
//            + ", "
//            + "rut: "+_rutEmpleado
//            + ", fecha: "+_fechaMarca;
//        
//        try{
//            String msgFinal = " eliminar detalle_asistencia:"
//                + "rut [" + _rutEmpleado + "]" 
//                + ", fecha [" + _fechaMarca + "]";
//            
//            System.out.println(msgFinal);
//            objresultado.setMsg(msgFinal);
//            
//            String sql ="delete "
//                + "FROM detalle_asistencia "
//                + "where rut_empleado = ? "
//                + "and fecha_marca='"+_fechaMarca+"'";
//            
//            //dbConn = dbLocator.getConnection(m_dbpoolName);
//            psupdate = dbConn.prepareStatement(sql);
//            psupdate.setString(1,  _rutEmpleado);
//                       
//            int rowAffected = psupdate.executeUpdate();
//            if (rowAffected == 1){
//                System.out.println(WEB_NAME+"[delete]detalle calculo"
//                    + ", rut:" +_rutEmpleado
//                    + ", fecha:" +_fechaMarca
//                    +" actualizado OK!");
//            }
//
//            psupdate.close();
//            ////dbLocator.freeConnection(dbConn);
//        }catch(SQLException|DatabaseException sqle){
//            System.err.println("[delete]detalle calculo. Error: "+sqle.toString());
//            objresultado.setThereError(true);
//            objresultado.setCodError(result);
//            objresultado.setMsgError(msgError+" :"+sqle.toString());
//        }finally{
//           // //dbLocator.freeConnection(dbConn);
//        }
//
//        return objresultado;
//    }
    
    /**
     * Actualiza un cargo
     * @param _rutEmpleado
     * @param _fechaMarca
     * @param _authAtraso
     * @param _authHextras
     * @param _hhmmExtrasAutorizadas
     * @return 
     */
    public ResultCRUDVO update(String _rutEmpleado,
            String _fechaMarca,
            String _authAtraso,
            String _authHextras,
            String _hhmmExtrasAutorizadas){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "detalle_asistencia "
            + ", rut: "+_rutEmpleado
            + ", fecha: "+_fechaMarca
            + ", authHextras: "+_authHextras
            + ", hhmmExtrasAutorizadas: "+_hhmmExtrasAutorizadas;
        
        try{
            String msgFinal = " Actualizar detalle_asistencia:"
                + "rut [" + _rutEmpleado + "]" 
                + ", fecha [" + _fechaMarca + "]"
                + ", authHextras [" + _authHextras + "]"
                + ", hhmmExtrasAutorizadas [" + _hhmmExtrasAutorizadas + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            String sql ="update "
                + "detalle_asistencia "
                + "set "
                + "autoriza_atraso = ?,"
                + "autoriza_hrsextras = ?,"
                + "hhmm_extras_autorizadas = ? "
                + "where rut_empleado = ? "
                + "and fecha_marca_entrada='"+_fechaMarca+"'";
            
            if (dbConn==null) dbConn = dbLocator.getConnection(m_dbpoolName,"DetalleAsistenciaDAO.update");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setString(1,  _authAtraso);
            psupdate.setString(2,  _authHextras);
            psupdate.setString(3,  _hhmmExtrasAutorizadas);
            psupdate.setString(4,  _rutEmpleado);
                       
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println(WEB_NAME+"[update]detalle_asistencia "
                    + ", rut:" +_rutEmpleado
                    + ", fecha:" +_fechaMarca
                    + ", authAtraso: "+_authAtraso
                    + ", authHextras: "+_authHextras
                    +" actualizado OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException sqle){
            System.err.println("[update]detalle_asistencia. Error: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }catch(DatabaseException dbex){
            System.err.println("[update]detalle_asistencia. "
                + "Error: " + dbex.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :" + dbex.toString());
        }
        finally{
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
    * Agrega un nuevo detalle_Asistencia
    * @param _detalle
    * @return 
    */
    public ResultCRUDVO insert(DetalleAsistenciaVO _detalle){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        int result=0;
        String msgError = "Error al insertar "
            + "Detalle Asistencia. "
            + ", empresa: "+_detalle.getEmpresaId()
            + ", rut: "+_detalle.getRutEmpleado()
            + ", fechaEntrada: "+_detalle.getFechaEntradaMarca()
            + ", horaEntrada: "+_detalle.getHoraEntrada()
            + ", fechaSalida: "+_detalle.getFechaSalidaMarca()    
            + ", horaSalida: "+_detalle.getHoraSalida();
        
       String msgFinal = " Inserta Detalle Asistencia:"
            + "empresa_id [" + _detalle.getEmpresaId() + "],"
            +  ", rut_empleado [" + _detalle.getRutEmpleado() + "]"
            +  ", fechaEntrada [" + _detalle.getFechaEntradaMarca() + "]"
            +  ", horaEntrada [" + _detalle.getHoraEntrada() + "]"
            +  ", fechaSalida [" + _detalle.getFechaSalidaMarca() + "]"   
            +  ", horaSalida [" + _detalle.getHoraSalida() + "]";            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO detalle_asistencia "
                    + "(empresa_id, "
                    + "depto_id, "
                    + "cenco_id, "
                    + "rut_empleado, "
                    + "fecha_hora_calculo, "
                    + "fecha_marca_entrada, "
                    + "hora_entrada, "
                    + "hora_salida, "
                    + "horas_teoricas, "
                    + "horas_trabajadas, "
                    + "minutos_extras_50,"
                    + "horas_extras,"
                    + "minutos_trabajados, "
                    + "holgura_minutos, "
                    + "es_feriado,"
                    + "minutos_extras_100,"
                    + "hora_entrada_teorica," 
                    + "hora_salida_teorica,"
                    + "art22,"
                    + "minutos_atraso,"
                    + "minutos_no_trabajados_entrada,"
                    + "minutos_no_trabajados_salida,"
                    + "fecha_marca_salida,"
                    + "hora_inicio_ausencia, "
                    + "hora_fin_ausencia) "
                    + " VALUES ("
                        + "?,?, "
                        + "?,?, "
                        + "?,?, "
                        + "?,?, "
                        + "?,?, "
                        + "?,?, "
                        + "?,?,"
                        + "?,?,"
                        + "?,?,"
                        + "?,?,"
                        + "?,?,"
                        + "?,?)";
            if (_detalle.getHoraInicioAusencia() == null){
                sql = "INSERT INTO detalle_asistencia "
                    + "(empresa_id, "
                    + "depto_id, "
                    + "cenco_id, "
                    + "rut_empleado, "
                    + "fecha_hora_calculo, "
                    + "fecha_marca_entrada, "
                    + "hora_entrada, "
                    + "hora_salida, "
                    + "horas_teoricas, "
                    + "horas_trabajadas, "
                    + "minutos_extras_50,"
                    + "horas_extras,"
                    + "minutos_trabajados,"
                    + "holgura_minutos, "
                    + "es_feriado,"
                    + "minutos_extras_100,"
                    + "hora_entrada_teorica,"
                    + "hora_salida_teorica,"
                        + "art22,"
                        + "minutos_atraso,"
                        + "minutos_no_trabajados_entrada, "
                        + "minutos_no_trabajados_salida,"
                        + "fecha_marca_salida) "
                    + " VALUES (?, ?, "
                        + "?, ?, "
                        + "?, ?, "
                        + "?, ?, "
                        + "?, ?, "
                        + "?, ?,"
                        + "?, ?,"
                        + "?, ?,"
                        + "?, ?,"
                        + "?, ?,"
                        + "?,?)";
            }
            
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _detalle.getEmpresaId());
            insert.setString(2,  _detalle.getDeptoId());
            insert.setInt(3,  _detalle.getCencoId());
            insert.setString(4,  _detalle.getRutEmpleado());
            insert.setTimestamp(5, Utilidades.getTimestamp(_detalle.getFechaHoraCalculo(),null));
            //detalle
            insert.setDate(6,  Utilidades.getJavaSqlDate(_detalle.getFechaEntradaMarca(),"yyyy-MM-dd"));
            insert.setTime(7,  Utilidades.getHora(_detalle.getHoraEntrada()));
            insert.setTime(8,  Utilidades.getHora(_detalle.getHoraSalida()));
            insert.setInt(9, _detalle.getHorasTeoricas());
            insert.setInt(10, _detalle.getHorasReales());
            insert.setInt(11, _detalle.getMinutosExtrasAl50());
            insert.setInt(12, _detalle.getHorasExtras());
            insert.setInt(13, _detalle.getMinutosReales());
            insert.setInt(14, _detalle.getHolguraMinutos());
            insert.setBoolean(15, _detalle.isEsFeriado());
            insert.setInt(16, _detalle.getMinutosExtrasAl100());
            insert.setTime(17,  Utilidades.getHora(_detalle.getHoraEntradaTeorica()));
            insert.setTime(18,  Utilidades.getHora(_detalle.getHoraSalidaTeorica()));
            insert.setBoolean(19, _detalle.isArt22());  
            insert.setInt(20, _detalle.getMinutosAtraso());
            insert.setInt(21, _detalle.getMinutosNoTrabajadosEntrada());
            insert.setInt(22, _detalle.getMinutosNoTrabajadosSalida());
            insert.setDate(23,  Utilidades.getJavaSqlDate(_detalle.getFechaSalidaMarca(),"yyyy-MM-dd"));
            if (_detalle.getHoraInicioAusencia() != null){
                insert.setTime(24,  Utilidades.getHora(_detalle.getHoraInicioAusencia()));
                insert.setTime(25,  Utilidades.getHora(_detalle.getHoraFinAusencia()));
            }
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println(WEB_NAME+"[insert detalle_asistencia]"
                   + " empresa: " + _detalle.getEmpresaId()
                    + ", rut: " + _detalle.getRutEmpleado()
                    + ", fechaEntrada: " + _detalle.getFechaEntradaMarca()
                    + ", horaEntrada: "+_detalle.getHoraEntrada()
                    + ", fechaSalida: " + _detalle.getFechaSalidaMarca()    
                    + ", horaSalida: "+_detalle.getHoraSalida()
                    +" insertado OK!");
            }
            
            insert.close();
            //dbLocator.freeConnection(dbConn);
        }catch(SQLException sqle){
            System.err.println("insert detalle_asistencia Error1: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (insert != null) insert.close();
                //dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
        
        return objresultado;
    }
    
    /**
    * 
    * @param entities
    * @throws java.sql.SQLException
    */
    public void saveList(ArrayList<DetalleAsistenciaToInsertVO> entities) throws SQLException{
        try (
            PreparedStatement statement = dbConn.prepareStatement(SQL_INSERT);
        ) {
            int i = 0;
            System.out.println(WEB_NAME+"[DetalleAsistenciaDAO.saveList]"
                + "items a insertar: "+entities.size());
            for (DetalleAsistenciaToInsertVO entity : entities) {
                System.out.println(WEB_NAME+"[DetalleAsistenciaDAO.saveList] "
                    + "Insert de calculos asistencia. "
                    + "Rut= " + entity.getDetalle().getRutEmpleado()
                    + ", fechaEntrada= " + entity.getDetalle().getFechaEntradaMarca()
                    + ", hora entrada= " + entity.getDetalle().getHoraEntrada()
                    + ", fechaSalida= " + entity.getDetalle().getFechaSalidaMarca()    
                    + ", hora salida= " + entity.getDetalle().getHoraSalida()  
                    );
                
                statement.setString(1,  entity.getDetalle().getEmpresaId());
                statement.setString(2,  entity.getDetalle().getDeptoId());
                statement.setInt(3,  entity.getDetalle().getCencoId());
                statement.setString(4,  entity.getDetalle().getRutEmpleado());
                statement.setTimestamp(5, Utilidades.getTimestamp(entity.getDetalle().getFechaHoraCalculo(),null));
                //detalle
                statement.setDate(6,  Utilidades.getJavaSqlDate(entity.getDetalle().getFechaEntradaMarca(),"yyyy-MM-dd"));
                statement.setTime(7,  Utilidades.getHora(entity.getDetalle().getHoraEntrada()));
                statement.setTime(8,  Utilidades.getHora(entity.getDetalle().getHoraSalida()));
                statement.setInt(9, entity.getDetalle().getHorasTeoricas());
                statement.setInt(10, entity.getDetalle().getHorasReales());
                statement.setInt(11, entity.getDetalle().getMinutosExtrasAl50());
                statement.setInt(12, entity.getDetalle().getHorasExtras());
                statement.setInt(13, entity.getDetalle().getMinutosReales());
                statement.setInt(14, entity.getDetalle().getHolguraMinutos());
                statement.setBoolean(15, entity.getDetalle().isEsFeriado());
                statement.setInt(16, entity.getDetalle().getMinutosExtrasAl100());
                statement.setTime(17,  Utilidades.getHora(entity.getDetalle().getHoraEntradaTeorica()));
                statement.setTime(18,  Utilidades.getHora(entity.getDetalle().getHoraSalidaTeorica()));
                statement.setBoolean(19, entity.getDetalle().isArt22());  
                statement.setInt(20, entity.getDetalle().getMinutosAtraso());
                statement.setInt(21, entity.getDetalle().getMinutosNoTrabajadosEntrada());
                statement.setInt(22, entity.getDetalle().getMinutosNoTrabajadosSalida());
                if (entity.getDetalle().getHoraInicioAusencia() != null){
                    statement.setTime(23,  Utilidades.getHora(entity.getDetalle().getHoraInicioAusencia()));
                    statement.setTime(24,  Utilidades.getHora(entity.getDetalle().getHoraFinAusencia()));
                }else{
                    statement.setTime(23,  null);
                    statement.setTime(24,  null);
                }
                
                /** added at 01-11-2017*/
                //hrs_presenciales, hrs_trabajadas, observacion
                statement.setString(25,  entity.getDetalle().getHrsPresenciales());
                statement.setString(26,  entity.getDetalle().getHrsTrabajadas());
                statement.setString(27,  entity.getDetalle().getObservacion());
                statement.setString(28,  entity.getDetalle().getHrsAusencia());
                statement.setInt(29,  entity.getDetalle().getMinutosExtras());
                statement.setDate(30,  Utilidades.getJavaSqlDate(entity.getDetalle().getFechaSalidaMarca(),"yyyy-MM-dd"));
                statement.setString(31,  entity.getDetalle().getHoraMinsExtras());
                statement.setString(32,  entity.getDetalle().getHhmmAtraso());
                statement.setString(33,  entity.getDetalle().getHhmmJustificadas());
                
                statement.setString(34,  entity.getDetalle().getComentarioMarcaEntrada());
                statement.setString(35,  entity.getDetalle().getComentarioMarcaSalida());
                
                statement.setString(36,  entity.getDetalle().getHoraMinsSalidaAnticipada());
                
                statement.setString(37,  entity.getDetalle().getHrsNoTrabajadas());
                               
                // ...
                statement.addBatch();
                i++;

                if (i % 50 == 0 || i == entities.size()) {
                    int[] rowsAffected = statement.executeBatch(); // Execute every 1000 items.
                    System.out.println(WEB_NAME+"[DetalleAsistenciaDAO."
                        + "saveList]filas afectadas= "+rowsAffected.length);
                }
            }
        }catch(Exception ex){
            System.err.println("[DetalleAsistenciaDAO."
                + "saveList]Error= " + ex.toString());
        }finally{
//            try {
//                //dbLocator.freeConnection(dbConn);
//            } catch (SQLException|DatabaseException ex) {
//                System.err.println("Error: "+ex.toString());
//            }
        }
    }
    
    public void deleteList(ArrayList<DetalleAsistenciaToInsertVO> entities) throws SQLException{
        try (
            PreparedStatement statement = dbConn.prepareStatement(SQL_DELETE);
        ) {
            int i = 0;
            System.out.println(WEB_NAME+"[DetalleAsistenciaDAO.delete]"
                + "items a eliminar: "+entities.size());
            for (DetalleAsistenciaToInsertVO entity : entities) {
                                
                System.out.println(WEB_NAME+"[DetalleAsistenciaDAO.delete]"
                    + "rut: "+entity.getDetalle().getRutEmpleado()
                    +", fechaMarca: "+entity.getDetalle().getFechaEntradaMarca());
                
                statement.setString(1,  entity.getDetalle().getRutEmpleado());
                Date dateFilter = Utilidades.getDateFromString(entity.getDetalle().getFechaEntradaMarca(),"yyyy-MM-dd");
                statement.setDate(2, new java.sql.Date(dateFilter.getTime()));
                
                // ...
                statement.addBatch();
                i++;

                if (i % 50 == 0 || i == entities.size()) {
                    int[] rowsAffected = statement.executeBatch(); // Execute every 1000 items.
                    System.out.println(WEB_NAME+"[DetalleAsistenciaDAO."
                        + "deleteList]filas afectadas= "+rowsAffected.length);
                }
            }
        }catch(Exception ex){
            System.err.println("[DetalleAsistenciaDAO."
                + "deleteList]Error= " + ex.toString());
        }finally{
//            try {
//                //dbLocator.freeConnection(dbConn);
//            } catch (SQLException|DatabaseException ex) {
//                System.err.println("Error: "+ex.toString());
//            }
        }
    }
    
//    /**
//     * Setea las marcas como procesadas
//     * 
//     * @param entities
//     * @throws java.sql.SQLException
//    */
//    public void setMarcasProcesadas(ArrayList<DetalleAsistenciaToInsertVO> entities) throws SQLException|DatabaseException{
//        try (
//            PreparedStatement statement = 
//                dbConn.prepareStatement(SQL_UPDATE_MARCAS_PROCESADAS);
//        ) {
//            int i = 0;
//            System.out.println(WEB_NAME+"[DetalleAsistenciaDAO.setMarcasProcesadas]"
//                + "items a insertar: "+entities.size());
//            for (DetalleAsistenciaToInsertVO entity : entities) {
//                
//                if (entity.getDetalle().getHoraEntrada().compareTo("00:00:00") != 0) {
//                    //set marca entrada como procesada
//                    statement.setString(1,  entity.getDetalle().getRutEmpleado());
//                    statement.setTimestamp(2, 
//                        Utilidades.getTimestamp(entity.getDetalle().getFechaEntradaMarca()+" "+entity.getDetalle().getHoraEntrada(),null));
//                    statement.setString(3,  entity.getDetalle().getEmpresaId());
//                    statement.setInt(4,  1);//marca entrada
//                    // ...
//                    statement.addBatch();
//                    i++;
//                    System.out.println(WEB_NAME+"[DetalleAsistenciaDAO.setMarcasProcesadas] "
//                        + "Update entrada procesada. "
//                        + "Rut= " + entity.getDetalle().getRutEmpleado()
//                        + ", fechaHora= " + 
//                            entity.getDetalle().getFechaEntradaMarca()
//                            + " "
//                            + entity.getDetalle().getHoraEntrada());
//                }
//                if (entity.getDetalle().getHoraSalida().compareTo("00:00:00") != 0) {
//                    //set marca salida como procesada
//                    statement.setString(1,  entity.getDetalle().getRutEmpleado());
//                    statement.setTimestamp(2, 
//                        Utilidades.getTimestamp(entity.getDetalle().getFechaSalidaMarca()+" "+entity.getDetalle().getHoraSalida(),null));
//                    statement.setString(3,  entity.getDetalle().getEmpresaId());
//                    statement.setInt(4,  2);//marca salida
//                    // ...
//                    statement.addBatch();
//                    i++;
//                    System.out.println(WEB_NAME+"[DetalleAsistenciaDAO.setMarcasProcesadas] "
//                        + "Update salida procesada. "
//                        + "Rut= " + entity.getDetalle().getRutEmpleado()
//                        + ", fechaHora= " + 
//                            entity.getDetalle().getFechaSalidaMarca()
//                            + " "
//                            + entity.getDetalle().getHoraSalida());
//                }
//                System.out.println(WEB_NAME+"[DetalleAsistenciaDAO.setMarcasProcesadas] "
//                    + "i = " + i
//                    + ", entities.size()= " +entities.size());
//                if (i % 50 == 0 || i%2 ==0 || i == entities.size()) {
//                    int[] rowsAffected = statement.executeBatch(); // Execute every 1000 items.
//                    System.out.println(WEB_NAME+"[DetalleAsistenciaDAO."
//                        + "setMarcasProcesadas]filas afectadas= "+rowsAffected.length);
//                }
//            }
//        }
//    }
    
    public boolean existeCalculo(String _rutEmpleado,
            String _fechaMarca){
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean existeRegistro=false;
        
         try{
            String sql ="SELECT fecha_hora_calculo "
                + "FROM detalle_asistencia "
                + "where rut_empleado = '"+_rutEmpleado+"' "
                + "and fecha_marca_entrada='"+_fechaMarca+"'";
             
            //dbConn = dbLocator.getConnection(m_dbpoolName);
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();
            existeRegistro = rs.next();
            
            ps.close();
            rs.close();
            //dbLocator.freeConnection(dbConn);
        }catch(SQLException sqle){
            m_logger.error("Error: "+sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                //dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
         
        return existeRegistro;
    }
    
    /**
    *
    * @param _rutEmpleado
    * @param _startDate
    * @param _endDate
    * @return
    */
    public List<DetalleAsistenciaVO> getDetalles(String _rutEmpleado,
            String _startDate, String _endDate){
        List<DetalleAsistenciaVO> lista = new ArrayList<>();
        DetalleAsistenciaVO data;
        
        System.out.println(WEB_NAME+"[DetalleAsistenciaDAO.getDetalles]inicio");
        
        String sql ="SELECT empresa_id, depto_id, "
            + "cenco_id, rut_empleado, "
            + "fecha_hora_calculo,"
            + "fecha_marca_entrada,"
            + "coalesce(fecha_marca_salida,fecha_marca_entrada) fecha_marca_salida, "
            + "to_char(fecha_marca_entrada, 'TMDy dd/MM/yyyy') fecha_entrada_marca_str,"
            + "to_char(fecha_marca_salida, 'TMDy dd/MM/yyyy') fecha_salida_marca_str,"
            + "to_char(hora_entrada, 'HH24:MI:SS') hora_entrada, "
            + "to_char(hora_salida, 'HH24:MI:SS') hora_salida, "
            + "horas_teoricas, "
            + "horas_trabajadas,"
            + "minutos_extras_50, "
            + "hora_inicio_ausencia, "
            + "hora_fin_ausencia, "
            + "minutos_trabajados,"
            + "horas_extras, "
            + "holgura_minutos, "
            + "es_feriado,"
            + "minutos_extras_100,"
            + "hora_entrada_teorica,"
            + "hora_salida_teorica,"
            + "art22,"
            + "minutos_atraso,minutos_extras,"
            + "autoriza_mins_no_trab_entrada,"
            + "autoriza_mins_no_trab_salida,"
            + "minutos_no_trabajados_entrada,"
            + "minutos_no_trabajados_salida,"
            + "hrs_presenciales," 
            + "hrs_trabajadas," 
            + "observacion,"
            + "hrs_ausencia,"
            + "coalesce(hhmm_extras,'00:00') hhmm_extras, "
            + "hhmm_atraso,"
            + "coalesce(autoriza_atraso,'N') autoriza_atraso,"
            + "coalesce(autoriza_hrsextras,'N') autoriza_hrsextras,"
            + "hhmm_justificadas,"
            + "coalesce(hhmm_extras_autorizadas,'00:00') hhmm_extras_autorizadas, "
            + "CASE WHEN marca_entrada_comentario is not null THEN '*' ELSE '' END entrada_comentario,"
            + "CASE "
            + "WHEN fecha_marca_salida > fecha_marca_entrada THEN '+1' "
            + "WHEN marca_salida_comentario is not null THEN '*' ELSE '' "
            + "END salida_comentario, hrs_no_trabajadas "    
            + "FROM detalle_asistencia "
            + "where rut_empleado = '"+_rutEmpleado+"' "
            + "and fecha_marca_entrada between '"+_startDate+"' "
                + "and '"+_endDate+"' "
            + "order by fecha_marca_entrada";
        
        try {
           dbConn = dbLocator.getConnection(m_dbpoolName,"[DetalleAsistenciaDAO.getDetalles]");
           PreparedStatement stmt = dbConn.prepareStatement(sql);
           try {
              ResultSet rs = stmt.executeQuery();
              try {
                // do whatever it is you want to do
                while (rs.next()){
                    data = new DetalleAsistenciaVO();
                    data.setRut(rs.getString("rut_empleado"));
                    data.setFechaEntradaMarca(rs.getString("fecha_marca_entrada"));
                    data.setFechaSalidaMarca(rs.getString("fecha_marca_salida"));
                    data.setLabelFechaEntradaMarca(rs.getString("fecha_entrada_marca_str"));
                    data.setLabelFechaSalidaMarca(rs.getString("fecha_salida_marca_str"));
                    data.setHoraEntrada(rs.getString("hora_entrada"));
                    data.setHoraSalida(rs.getString("hora_salida"));
                    data.setMinutosReales(rs.getInt("minutos_trabajados"));
                    data.setMinutosExtrasAl50(rs.getInt("minutos_extras_50"));
                    data.setMinutosExtrasAl100(rs.getInt("minutos_extras_100"));
                    data.setHolguraMinutos(rs.getInt("holgura_minutos"));
                    data.setEsFeriado(rs.getBoolean("es_feriado"));
                    data.setHoraInicioAusencia(rs.getString("hora_inicio_ausencia"));
                    data.setHoraFinAusencia(rs.getString("hora_fin_ausencia"));
                    data.setHoraEntradaTeorica(rs.getString("hora_entrada_teorica"));
                    data.setHoraSalidaTeorica(rs.getString("hora_salida_teorica"));
                    data.setArt22(rs.getBoolean("art22"));
                    data.setMinutosAtraso(rs.getInt("minutos_atraso"));
                    data.setMinutosExtras(rs.getInt("minutos_extras"));
                    data.setAutorizaMinsNoTrabajadosEntrada(rs.getString("autoriza_mins_no_trab_entrada"));
                    data.setAutorizaMinsNoTrabajadosSalida(rs.getString("autoriza_mins_no_trab_salida"));
                    data.setMinutosNoTrabajadosEntrada(rs.getInt("minutos_no_trabajados_entrada"));
                    data.setMinutosNoTrabajadosSalida(rs.getInt("minutos_no_trabajados_salida"));
                    data.setHrsPresenciales(rs.getString("hrs_presenciales"));
                    data.setHrsTrabajadas(rs.getString("hrs_trabajadas"));
                    data.setObservacion(rs.getString("observacion"), "DetalleAsistenciaDAO_1");
                    data.setHrsAusencia(rs.getString("hrs_ausencia"));
                    data.setHoraMinsExtras(rs.getString("hhmm_extras"));
                    data.setHhmmAtraso(rs.getString("hhmm_atraso"));
                    data.setAutorizaAtraso(rs.getString("autoriza_atraso"));
                    data.setAutorizaHrsExtras(rs.getString("autoriza_hrsextras"));
                    data.setHhmmJustificadas(rs.getString("hhmm_justificadas"));
                    data.setHorasMinsExtrasAutorizadas(rs.getString("hhmm_extras_autorizadas"));
                    data.setRowKey(data.getRut() + "|" 
                        + data.getFechaEntradaMarca());

                    data.setComentarioMarcaEntrada(rs.getString("entrada_comentario"));
                    data.setComentarioMarcaSalida(rs.getString("salida_comentario"));
                    data.setHrsNoTrabajadas(rs.getString("hrs_no_trabajadas"));
                    lista.add(data);
                } 
                 
                 
              } finally {
                 rs.close();
              }
           } finally {
              stmt.close();
           }
        }catch(SQLException|DatabaseException sqlex){
            
        } 
        finally {
            try {
                dbConn.close();//dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("DetalleAsistenciaDAO "
                    + "Error: " + ex.toString());
            }
        }
        
        return lista;
    }
    
    /**
    * Obtiene lista de detalles de asistencia para la lista de empleados
    * entregada como parametro
    *  
    * @param _listaEmpleados
    * @param _startDate
    * @param _endDate
    * @param _idTurno
    * 
    * @return
    */
    public LinkedHashMap<String,List<DetalleAsistenciaVO>> getDetallesInforme(List<EmpleadoVO> _listaEmpleados,
            String _startDate, String _endDate, int _idTurno){
        
        LinkedHashMap<String,List<DetalleAsistenciaVO>> fullList=new LinkedHashMap<>();
        List<DetalleAsistenciaVO> detallesAsistenciaEmpleado = new ArrayList<>();
        DetalleAsistenciaVO data;
        String cadenaRuts = "";
        
        System.out.println(WEB_NAME+"[DetalleAsistenciaDAO.getDetallesInforme]inicio");
        if (_listaEmpleados != null && !_listaEmpleados.isEmpty()){
            System.out.println(WEB_NAME+"[DetalleAsistenciaDAO.getDetallesInforme]"
                + "num empleados: "+ _listaEmpleados.size());
            for (EmpleadoVO empleado : _listaEmpleados) {
                cadenaRuts += "'" + empleado.getRut() + "',";
            }
            cadenaRuts = cadenaRuts.substring(0, cadenaRuts.length()-1);
        }
        
        String sql ="SELECT "
            + "detalle_asistencia.empresa_id, "
            + "detalle_asistencia.depto_id, "
            + "detalle_asistencia.cenco_id, "
            + "rut_empleado, "
            + "empleado.nombre nombreEmpleado,"
            + "empleado.empl_id_turno,"
            + "fecha_hora_calculo,"
            + "fecha_marca_entrada,"
            + "coalesce(fecha_marca_salida,fecha_marca_entrada) fecha_marca_salida, "
            + "to_char(fecha_marca_entrada, 'TMDy dd/MM/yyyy') fecha_entrada_marca_str,"
            + "to_char(fecha_marca_salida, 'TMDy dd/MM/yyyy') fecha_salida_marca_str,"
            + "to_char(hora_entrada, 'HH24:MI:SS') hora_entrada, "
            + "to_char(hora_salida, 'HH24:MI:SS') hora_salida, "
            + "horas_teoricas, "
            + "horas_trabajadas,"
            + "minutos_extras_50, "
            + "hora_inicio_ausencia, "
            + "hora_fin_ausencia, "
            + "minutos_trabajados,"
            + "horas_extras, "
            + "holgura_minutos, "
            + "es_feriado,"
            + "minutos_extras_100,"
            + "hora_entrada_teorica,"
            + "hora_salida_teorica,"
            + "art22,"
            + "minutos_atraso,minutos_extras,"
            + "autoriza_mins_no_trab_entrada,"
            + "autoriza_mins_no_trab_salida,"
            + "minutos_no_trabajados_entrada,"
            + "minutos_no_trabajados_salida,"
            + "coalesce(hrs_presenciales,'') hrs_presenciales," 
            + "coalesce(hrs_trabajadas,'') hrs_trabajadas," 
            + "coalesce(observacion,'') observacion,"
            + "hrs_ausencia,"
            + "coalesce(hhmm_extras,'00:00') hhmm_extras, "
            + "coalesce(hhmm_atraso,'') hhmm_atraso,"
            + "coalesce(autoriza_atraso,'N') autoriza_atraso,"
            + "coalesce(autoriza_hrsextras,'N') autoriza_hrsextras,"
            + "coalesce(hhmm_justificadas,'') hhmm_justificadas,"
            + "coalesce(hhmm_extras_autorizadas,'00:00') hhmm_extras_autorizadas,"
            + "coalesce(hhmm_salida_anticipada,'') hhmm_salida_anticipada,"
            + "CASE WHEN marca_entrada_comentario is not null THEN '*' ELSE '' END entrada_comentario,"
            + "CASE "
            + "WHEN fecha_marca_salida > fecha_marca_entrada THEN '+1' "
            + "WHEN marca_salida_comentario is not null THEN '*' ELSE '' "
            + "END salida_comentario,"
                + "hrs_no_trabajadas "
            + "FROM detalle_asistencia "
                + "inner join view_empleado empleado "
                + "on (detalle_asistencia.empresa_id = empleado.empresa_id "
                + "and detalle_asistencia.rut_empleado = empleado.rut) "
            + "where rut_empleado in ("+ cadenaRuts+") "
            + "and fecha_marca_entrada between '"+_startDate+"' "
                + "and '"+_endDate+"' ";
        
        if (_idTurno != -1){
            sql += " and empl_id_turno = "+_idTurno;
        }
        
        sql += " order by rut_empleado,fecha_marca_entrada";
            
        System.out.println(WEB_NAME+"[DetalleAsistenciaDAO."
            + "getDetallesInforme]Para lista empleados. "
            + "sql: "+ sql);
        try {
           dbConn = dbLocator.getConnection(m_dbpoolName,"[DetalleAsistenciaDAO.getDetallesInforme]");
           PreparedStatement stmt = dbConn.prepareStatement(sql);
           try {
              ResultSet rs = stmt.executeQuery();
              String rutIteracion = "";
              String rutAnterior= "";
              try {
                // do whatever it is you want to do
                while (rs.next()){
                    data = new DetalleAsistenciaVO();
                    rutIteracion = rs.getString("rut_empleado");
                    data.setRut(rutIteracion);
                    data.setNombreEmpleado(rs.getString("nombreEmpleado"));
                    data.setFechaEntradaMarca(rs.getString("fecha_marca_entrada"));
                    data.setFechaSalidaMarca(rs.getString("fecha_marca_salida"));
                    data.setLabelFechaEntradaMarca(rs.getString("fecha_entrada_marca_str"));
                    data.setLabelFechaSalidaMarca(rs.getString("fecha_salida_marca_str"));
                    data.setHoraEntrada(rs.getString("hora_entrada"));
                    data.setHoraSalida(rs.getString("hora_salida"));
                    data.setMinutosReales(rs.getInt("minutos_trabajados"));
                    data.setMinutosExtrasAl50(rs.getInt("minutos_extras_50"));
                    data.setMinutosExtrasAl100(rs.getInt("minutos_extras_100"));
                    data.setHolguraMinutos(rs.getInt("holgura_minutos"));
                    data.setEsFeriado(rs.getBoolean("es_feriado"));
                    data.setHoraInicioAusencia(rs.getString("hora_inicio_ausencia"));
                    data.setHoraFinAusencia(rs.getString("hora_fin_ausencia"));
                    data.setHoraEntradaTeorica(rs.getString("hora_entrada_teorica"));
                    data.setHoraSalidaTeorica(rs.getString("hora_salida_teorica"));
                    data.setArt22(rs.getBoolean("art22"));
                    data.setMinutosAtraso(rs.getInt("minutos_atraso"));
                    data.setMinutosExtras(rs.getInt("minutos_extras"));
                    data.setAutorizaMinsNoTrabajadosEntrada(rs.getString("autoriza_mins_no_trab_entrada"));
                    data.setAutorizaMinsNoTrabajadosSalida(rs.getString("autoriza_mins_no_trab_salida"));
                    data.setMinutosNoTrabajadosEntrada(rs.getInt("minutos_no_trabajados_entrada"));
                    data.setMinutosNoTrabajadosSalida(rs.getInt("minutos_no_trabajados_salida"));
                    data.setHrsPresenciales(rs.getString("hrs_presenciales"));
                    data.setHrsTrabajadas(rs.getString("hrs_trabajadas"));
                    data.setObservacion(rs.getString("observacion"), "DetalleAsistenciaDAO_2");
                    data.setHrsAusencia(rs.getString("hrs_ausencia"));
                    data.setHoraMinsExtras(rs.getString("hhmm_extras"));
                    data.setHhmmAtraso(rs.getString("hhmm_atraso"));
                    data.setAutorizaAtraso(rs.getString("autoriza_atraso"));
                    data.setAutorizaHrsExtras(rs.getString("autoriza_hrsextras"));
                    data.setHhmmJustificadas(rs.getString("hhmm_justificadas"));
                    data.setHorasMinsExtrasAutorizadas(rs.getString("hhmm_extras_autorizadas"));
                    data.setHoraMinsSalidaAnticipada(rs.getString("hhmm_salida_anticipada"));
                    
                    data.setComentarioMarcaEntrada(rs.getString("entrada_comentario"));
                    data.setComentarioMarcaSalida(rs.getString("salida_comentario"));
                    data.setHrsNoTrabajadas(rs.getString("hrs_no_trabajadas"));
                    
                    data.setRowKey(data.getRut() + "|" 
                        + data.getFechaEntradaMarca());
                    
                    System.out.println(WEB_NAME+"[DetalleAsistenciaDAO."
                        + "getDetallesInforme]"
                        + "rutIteracion= " + rutIteracion
                        + ", rutAnterior= " + rutAnterior);
                    
                    if (_listaEmpleados.size()==1){
                        System.out.println(WEB_NAME+"[DetalleAsistenciaDAO."
                            + "getDetallesInforme](A)Un solo rut. Agrega detalle "
                            + "asistencia para el rut: "+rutAnterior + " - (lista.put)");
                        detallesAsistenciaEmpleado.add(data);
                        fullList.put(rutAnterior, detallesAsistenciaEmpleado);
                    }else if (rutIteracion.compareTo(rutAnterior) == 0){//mas de un rut
                        //Agrega detalle asistencia para el rut
                        System.out.println(WEB_NAME+"[DetalleAsistenciaDAO."
                            + "getDetallesInforme](B)Agrega detalle "
                            + "asistencia para el rut: "+rutIteracion);
                        detallesAsistenciaEmpleado.add(data);
                    }else{
                        //if (rutAnterior.compareTo("")==0) rutAnterior=rutIteracion;
                        System.out.println(WEB_NAME+"[DetalleAsistenciaDAO."
                            + "getDetallesInforme](C)Agrega lista con detalle "
                            + "asistencia para el rut: " + rutAnterior  + " - (lista.put)");
                        fullList.put(rutAnterior, detallesAsistenciaEmpleado);
                        detallesAsistenciaEmpleado = new ArrayList<>();
                        //agrega detalle asistencia para el nuevo rut en la iteracion
                        
                        detallesAsistenciaEmpleado.add(data);
                    }
                    
                    rutAnterior = rutIteracion;
                    
                    //lista.add(data);
                } 
                 
                 
              } finally {
                 rs.close();
              }
           } finally {
              stmt.close();
           }
        }catch(SQLException|DatabaseException sqlex){
            System.err.println("[DetalleAsistenciaDAO]getDetallesInforme error: "+sqlex.toString());
        } 
        finally {
            try {
                dbConn.close();//dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("DetalleAsistenciaDAO.getDetallesInforme "
                    + "Error: " + ex.toString());
            }
        }
        
        return fullList;
    }
    
    
    
    /**
    * Obtiene lista de detalles de asistencia para la lista de empleados
    * entregada como parametro
    *  
    * @param _listaEmpleados
    * @param _startDate
    * @param _endDate
     * @param _idTurno
    * @return
    */
    public LinkedHashMap<String,List<DetalleAsistenciaVO>> getDetalles(
            List<EmpleadoVO> _listaEmpleados,
            String _startDate, 
            String _endDate, 
            int _idTurno){
        
        LinkedHashMap<String,List<DetalleAsistenciaVO>> fullList=new LinkedHashMap<>();
        List<DetalleAsistenciaVO> detallesAsistenciaEmpleado = new ArrayList<>();
        DetalleAsistenciaVO data;
        String cadenaRuts = "";
        
        System.out.println(WEB_NAME+"[DetalleAsistenciaDAO.getDetalles]inicio");
        if (_listaEmpleados != null && !_listaEmpleados.isEmpty()){
            System.out.println(WEB_NAME+"[DetalleAsistenciaDAO.getDetalles]"
                + "num empleados: "+ _listaEmpleados.size());
            for (EmpleadoVO empleado : _listaEmpleados) {
                cadenaRuts += "'" + empleado.getRut() + "',";
            }
            cadenaRuts = cadenaRuts.substring(0, cadenaRuts.length()-1);
        }
        
        String sql ="SELECT empresa_id, depto_id, "
            + "cenco_id, rut_empleado, "
            + "fecha_hora_calculo,"
            + "fecha_marca_entrada,"
            + "coalesce(fecha_marca_salida,fecha_marca_entrada) fecha_marca_salida, "
            + "to_char(fecha_marca_entrada, 'TMDy dd/MM/yyyy') fecha_entrada_marca_str,"
            + "to_char(fecha_marca_salida, 'TMDy dd/MM/yyyy') fecha_salida_marca_str,"
            + "to_char(hora_entrada, 'HH24:MI:SS') hora_entrada, "
            + "to_char(hora_salida, 'HH24:MI:SS') hora_salida, "
            + "horas_teoricas, "
            + "horas_trabajadas,"
            + "minutos_extras_50, "
            + "hora_inicio_ausencia, "
            + "hora_fin_ausencia, "
            + "minutos_trabajados,"
            + "horas_extras, "
            + "holgura_minutos, "
            + "es_feriado,"
            + "minutos_extras_100,"
            + "hora_entrada_teorica,"
            + "hora_salida_teorica,"
            + "art22,"
            + "minutos_atraso,minutos_extras,"
            + "autoriza_mins_no_trab_entrada,"
            + "autoriza_mins_no_trab_salida,"
            + "minutos_no_trabajados_entrada,"
            + "minutos_no_trabajados_salida,"
            + "hrs_presenciales," 
            + "hrs_trabajadas," 
            + "observacion,"
            + "hrs_ausencia,"
            + "coalesce(hhmm_extras,'00:00') hhmm_extras, "
            + "hhmm_atraso,"
            + "coalesce(autoriza_atraso,'N') autoriza_atraso,"
            + "coalesce(autoriza_hrsextras,'N') autoriza_hrsextras,"
            + "hhmm_justificadas,"
            + "coalesce(hhmm_extras_autorizadas,'00:00') hhmm_extras_autorizadas,"
            + "hrs_no_trabajadas "
            + "FROM detalle_asistencia "
            + "where rut_empleado in ("+ cadenaRuts+") "
            + "and fecha_marca_entrada between '"+_startDate+"' "
                + "and '"+_endDate+"' "
            + "order by rut_empleado,fecha_marca_entrada";
            
        System.out.println(WEB_NAME+"[DetalleAsistenciaDAO."
            + "getDetalles]Para lista empleados. "
            + "sql: "+ sql);
        try {
           dbConn = dbLocator.getConnection(m_dbpoolName,"[DetalleAsistenciaDAO.getDetalles]");
           PreparedStatement stmt = dbConn.prepareStatement(sql);
           try {
              ResultSet rs = stmt.executeQuery();
              String rutIteracion = "";
              String rutAnterior= "";
              try {
                // do whatever it is you want to do
                while (rs.next()){
                    data = new DetalleAsistenciaVO();
                    rutIteracion = rs.getString("rut_empleado");
                    data.setRut(rutIteracion);
                    data.setFechaEntradaMarca(rs.getString("fecha_marca_entrada"));
                    data.setFechaSalidaMarca(rs.getString("fecha_marca_salida"));
                    data.setLabelFechaEntradaMarca(rs.getString("fecha_entrada_marca_str"));
                    data.setLabelFechaSalidaMarca(rs.getString("fecha_salida_marca_str"));
                    data.setHoraEntrada(rs.getString("hora_entrada"));
                    data.setHoraSalida(rs.getString("hora_salida"));
                    data.setMinutosReales(rs.getInt("minutos_trabajados"));
                    data.setMinutosExtrasAl50(rs.getInt("minutos_extras_50"));
                    data.setMinutosExtrasAl100(rs.getInt("minutos_extras_100"));
                    data.setHolguraMinutos(rs.getInt("holgura_minutos"));
                    data.setEsFeriado(rs.getBoolean("es_feriado"));
                    data.setHoraInicioAusencia(rs.getString("hora_inicio_ausencia"));
                    data.setHoraFinAusencia(rs.getString("hora_fin_ausencia"));
                    data.setHoraEntradaTeorica(rs.getString("hora_entrada_teorica"));
                    data.setHoraSalidaTeorica(rs.getString("hora_salida_teorica"));
                    data.setArt22(rs.getBoolean("art22"));
                    data.setMinutosAtraso(rs.getInt("minutos_atraso"));
                    data.setMinutosExtras(rs.getInt("minutos_extras"));
                    data.setAutorizaMinsNoTrabajadosEntrada(rs.getString("autoriza_mins_no_trab_entrada"));
                    data.setAutorizaMinsNoTrabajadosSalida(rs.getString("autoriza_mins_no_trab_salida"));
                    data.setMinutosNoTrabajadosEntrada(rs.getInt("minutos_no_trabajados_entrada"));
                    data.setMinutosNoTrabajadosSalida(rs.getInt("minutos_no_trabajados_salida"));
                    data.setHrsPresenciales(rs.getString("hrs_presenciales"));
                    data.setHrsTrabajadas(rs.getString("hrs_trabajadas"));
                    data.setObservacion(rs.getString("observacion"), "DetalleAsistenciaDAO_3");
                    data.setHrsAusencia(rs.getString("hrs_ausencia"));
                    data.setHoraMinsExtras(rs.getString("hhmm_extras"));
                    data.setHhmmAtraso(rs.getString("hhmm_atraso"));
                    data.setAutorizaAtraso(rs.getString("autoriza_atraso"));
                    data.setAutorizaHrsExtras(rs.getString("autoriza_hrsextras"));
                    data.setHhmmJustificadas(rs.getString("hhmm_justificadas"));
                    data.setHorasMinsExtrasAutorizadas(rs.getString("hhmm_extras_autorizadas"));
                    data.setHrsNoTrabajadas(rs.getString("hrs_no_trabajadas"));
                    data.setRowKey(data.getRut() + "|" 
                        + data.getFechaEntradaMarca());
                    
                    if (rutIteracion.compareTo(rutAnterior) == 0){
                        //Agrega detalle asistencia para el rut
                        System.out.println(WEB_NAME+"[DetalleAsistenciaDAO."
                            + "getDetalles](1)Agrega detalle "
                            + "asistencia para el rut: "+rutIteracion);
                        detallesAsistenciaEmpleado.add(data);
                    }else{
                        //if (rutAnterior.compareTo("")==0) rutAnterior=rutIteracion;
                        System.out.println(WEB_NAME+"[DetalleAsistenciaDAO."
                            + "getDetalles](2)Agrega lista con detalle "
                            + "asistencia para el rut: " + rutAnterior);
                        fullList.put(rutAnterior, detallesAsistenciaEmpleado);
                        detallesAsistenciaEmpleado = new ArrayList<>();
                        //agrega detalle asistencia para el nuevo rut en la iteracion
                        detallesAsistenciaEmpleado.add(data);
                    }
                    
                    rutAnterior = rutIteracion;
                    //lista.add(data);
                } 
                 
                 
              } finally {
                 rs.close();
              }
           } finally {
              stmt.close();
           }
        }catch(SQLException|DatabaseException sqlex){
            System.err.println("[DetalleAsistenciaDAO]getDetallesList error: "+sqlex.toString());
        } 
        finally {
            try {
                dbConn.close();//dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("DetalleAsistenciaDAO "
                    + "Error: " + ex.toString());
            }
        }
        
        return fullList;
    }
    
    /**
     * 
     * @param _rutEmpleado
     * @param _startDate
     * @param _endDate
     * @return 
     */
    public List<DetalleAsistenciaVO> getDetallesHist(String _rutEmpleado,
            String _startDate, String _endDate){
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<DetalleAsistenciaVO> lista = new ArrayList<>();
        DetalleAsistenciaVO data;
        
        try{
            String sql ="SELECT empresa_id, depto_id, "
                + "cenco_id, rut_empleado, "
                + "fecha_hora_calculo,"
                + "fecha_marca_entrada,"
                + "coalesce(fecha_marca_salida,fecha_marca_entrada) fecha_marca_salida, "
                + "to_char(fecha_marca_entrada, 'TMDy dd/MM/yyyy') fecha_entrada_marca_str,"
                + "to_char(fecha_marca_salida, 'TMDy dd/MM/yyyy') fecha_salida_marca_str,"
                + "to_char(hora_entrada, 'HH24:MI:SS') hora_entrada, "
                + "to_char(hora_salida, 'HH24:MI:SS') hora_salida, "
                + "horas_teoricas, "
                + "hrs_trabajadas,"
                + "minutos_extras_50, "
                + "hora_inicio_ausencia, "
                + "hora_fin_ausencia, "
                + "minutos_trabajados,"
                + "horas_extras, "
                + "holgura_minutos, "
                + "es_feriado,"
                + "minutos_extras_100,"
                + "hora_entrada_teorica,"
                + "hora_salida_teorica,"
                + "art22,"
                + "minutos_atraso,minutos_extras,"
                + "autoriza_mins_no_trab_entrada,"
                + "autoriza_mins_no_trab_salida,"
                + "minutos_no_trabajados_entrada,"
                + "minutos_no_trabajados_salida,"
                + "hrs_presenciales," 
                + "hrs_trabajadas," 
                + "observacion,"
                + "hrs_ausencia,"
                + "coalesce(hhmm_extras,'00:00') hhmm_extras, "
                + "hhmm_atraso,"
                + "coalesce(autoriza_atraso,'N') autoriza_atraso,"
                + "coalesce(autoriza_hrsextras,'N') autoriza_hrsextras,"
                + "hhmm_justificadas,"
                + "coalesce(hhmm_extras_autorizadas,'00:00') hhmm_extras_autorizadas,"
                + "coalesce(hrs_no_trabajadas,'00:00') hrs_no_trabajadas "
                + "FROM detalle_asistencia_historica detalle_asistencia "
                + "where rut_empleado = '"+_rutEmpleado+"' "
                + "and fecha_marca_entrada between '"+_startDate+"' "
                    + "and '"+_endDate+"' "
                + "order by fecha_marca_entrada";
             
            System.out.println(WEB_NAME+"[DetalleAsistenciaDAO.getDetallesHist]Sql: "+sql); 
            dbConn = dbLocator.getConnection(m_dbpoolName,"[DetalleAsistenciaDAO.getDetalles]");
                        
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()){
                data = new DetalleAsistenciaVO();
                
                data.setRut(rs.getString("rut_empleado"));
                
                data.setFechaEntradaMarca(rs.getString("fecha_marca_entrada"));
                data.setFechaSalidaMarca(rs.getString("fecha_marca_salida"));
                data.setLabelFechaEntradaMarca(rs.getString("fecha_entrada_marca_str"));
                data.setLabelFechaSalidaMarca(rs.getString("fecha_salida_marca_str"));
                data.setHoraEntrada(rs.getString("hora_entrada"));
                data.setHoraSalida(rs.getString("hora_salida"));
                data.setMinutosReales(rs.getInt("minutos_trabajados"));
                
                data.setMinutosExtrasAl50(rs.getInt("minutos_extras_50"));
                data.setMinutosExtrasAl100(rs.getInt("minutos_extras_100"));

                data.setHolguraMinutos(rs.getInt("holgura_minutos"));
                data.setEsFeriado(rs.getBoolean("es_feriado"));
                data.setHoraInicioAusencia(rs.getString("hora_inicio_ausencia"));
                data.setHoraFinAusencia(rs.getString("hora_fin_ausencia"));
                data.setHoraEntradaTeorica(rs.getString("hora_entrada_teorica"));
                data.setHoraSalidaTeorica(rs.getString("hora_salida_teorica"));
                data.setArt22(rs.getBoolean("art22"));
                data.setMinutosAtraso(rs.getInt("minutos_atraso"));
                data.setMinutosExtras(rs.getInt("minutos_extras"));
                data.setAutorizaMinsNoTrabajadosEntrada(rs.getString("autoriza_mins_no_trab_entrada"));
                data.setAutorizaMinsNoTrabajadosSalida(rs.getString("autoriza_mins_no_trab_salida"));
                
                data.setMinutosNoTrabajadosEntrada(rs.getInt("minutos_no_trabajados_entrada"));
                data.setMinutosNoTrabajadosSalida(rs.getInt("minutos_no_trabajados_salida"));
                
                data.setHrsPresenciales(rs.getString("hrs_presenciales"));
                //data.setHrsTrabajadas(rs.getString("hrs_trabajadas"));
                data.setObservacion(rs.getString("observacion"), "DetalleAsistenciaDAO_4");
                data.setHrsAusencia(rs.getString("hrs_ausencia"));
                data.setHoraMinsExtras(rs.getString("hhmm_extras"));
                data.setHhmmAtraso(rs.getString("hhmm_atraso"));
                data.setAutorizaAtraso(rs.getString("autoriza_atraso"));
                data.setAutorizaHrsExtras(rs.getString("autoriza_hrsextras"));
                data.setHhmmJustificadas(rs.getString("hhmm_justificadas"));
                data.setHorasMinsExtrasAutorizadas(rs.getString("hhmm_extras_autorizadas"));
                data.setHrsNoTrabajadas(rs.getString("hrs_no_trabajadas"));
                
                data.setRowKey(data.getRut() + "|" 
                    + data.getFechaEntradaMarca());
                
                lista.add(data);
            }
            
            ps.close();
            rs.close();
            //dbLocator.freeConnection(dbConn);
        }catch(DatabaseException | SQLException dbex){
            m_logger.error("Error: " + dbex.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                //dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
         
        return lista;
    }
    
    /**
     * Retorna lista con los Cargos existentes en el sistema
     * 
     * @param _empresaId
     * @param _deptoId
     * @param _cencoId
     * @param _rutEmpleado
     * @param _jtStartIndex
     * @param _jtPageSize
     * @param _jtSorting
     * @return 
     */
    public List<CalculoHorasVO> getHeaders(String _empresaId,
            String _deptoId, int _cencoId,
            String _rutEmpleado,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<CalculoHorasVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        CalculoHorasVO data;
        
        try{
            String sql ="SELECT empresa_id, depto_id, cenco_id, "
                + "rut_empleado, "
                + "to_char(fecha_hora_calculo, 'yyyy-MM-dd HH24:MI:SS') fecha_hora_calculo "
//                    + "fecha_marca_entrada, hora_entrada, hora_salida, "
//                    + "horas_teoricas, horas_trabajadas,"
//                    + "horas_extras, hora_inicio_ausencia, "
//                    + "hora_fin_ausencia "
                + " FROM detalle_asistencia "
                + "inner join empleado on (detalle_asistencia.rut_empleado = empleado.empl_rut "
                + "and detalle_asistencia.empresa_id = empleado.empresa_id and "
                + "and detalle_asistencia.depto_id = empleado.depto_id and "
                + "and detalle_asistencia.cenco_id = empleado.cenco_id) "
                + "where 1 = 1 ";
           
            if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
                sql += " and detalle_asistencia.empresa_id ='" + _empresaId +"' ";
            }
            
            if (_deptoId != null && _deptoId.compareTo("-1") != 0){        
                sql += " and detalle_asistencia.depto_id ='" + _deptoId +"' ";
            }
            
            if (_cencoId != -1){        
                sql += " and detalle_asistencia.cenco_id =" + _cencoId +" ";
            }
           
            if (_rutEmpleado != null && _rutEmpleado.compareTo("") != 0){        
                sql += " and detalle_asistencia.rut_empleado ='" + _rutEmpleado +"' ";
            }
            
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            System.out.println(WEB_NAME+"[DetalleAsistenciaDAO."
                + "getHeaders]"
                + "empresa: "+_empresaId
                + ",depto: "+_deptoId
                    + ",cenco: "+_cencoId
                + ", _jtStartIndex: "+_jtStartIndex
                + ", _jtPageSize: "+_jtPageSize
                + ", _jtSorting: "+_jtSorting
                + ", Sql: "+sql);
            
            //dbConn = dbLocator.getConnection(m_dbpoolName);
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new CalculoHorasVO();
                data.setFechaHoraCalculo(rs.getString("fecha_hora_calculo"));
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setDeptoId(rs.getString("depto_id"));
                data.setCencoId(rs.getInt("cenco_id"));
                data.setRutEmpleado(rs.getString("rut_empleado"));
                lista.add(data);
            
            }

            ps.close();
            rs.close();
            //dbLocator.freeConnection(dbConn);
        }catch(SQLException sqle){
            m_logger.error("Error: "+sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                //dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
        return lista;
    }
    
    /**
     * 
     * @param _empresaId
     * @param _deptoId
     * @param _cencoId
     * @param _rutEmpleado
     * @return 
     */
    public int getHeadersCount(String _empresaId,
            String _deptoId, int _cencoId,
            String _rutEmpleado){
        int count=0;
        try {
            //dbConn = dbLocator.getConnection(m_dbpoolName);
            ResultSet rs;
            try (Statement statement = dbConn.createStatement()) {
                String strSql ="SELECT count(fecha_hora_calculo) "
                    + " FROM detalle_asistencia "
                    + "inner join empleado on (detalle_asistencia.rut_empleado = empleado.empl_rut "
                    + "and detalle_asistencia.empresa_id = empleado.empresa_id and "
                    + "and detalle_asistencia.depto_id = empleado.depto_id and "
                    + "and detalle_asistencia.cenco_id = empleado.cenco_id) "
                    + "where 1 = 1 ";
                if (_empresaId != null && _empresaId.compareTo("-1") != 0){
                    strSql += " and detalle_asistencia.empresa_id ='" + _empresaId +"' ";
                }   if (_deptoId != null && _deptoId.compareTo("-1") != 0){        
                    strSql += " and detalle_asistencia.depto_id ='" + _deptoId +"' ";
                }   if (_cencoId != -1){        
                    strSql += " and detalle_asistencia.cenco_id =" + _cencoId +" ";
                }   if (_rutEmpleado != null && _rutEmpleado.compareTo("") != 0){        
                    strSql += " and detalle_asistencia.rut_empleado ='" + _rutEmpleado +"' ";
                }   rs = statement.executeQuery(strSql);
                if (rs.next()) {
                    count=rs.getInt("count");
                }
            }
            rs.close();
            //dbLocator.freeConnection(dbConn);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
//            try {
//                //dbLocator.freeConnection(dbConn);
//            } catch (SQLException ex) {
//                System.err.println("Error: "+ex.toString());
//            }
        }
        return count;
    }
   
}
