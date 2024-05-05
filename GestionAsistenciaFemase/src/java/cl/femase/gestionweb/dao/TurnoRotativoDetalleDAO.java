/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.vo.DetalleTurnoVO;
import cl.femase.gestionweb.vo.DiferenciaHorasVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.TurnoRotativoDetalleVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexander
 */
public class TurnoRotativoDetalleDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    //private Connection dbConn;
    //DbConnectionPool connectionPool = new DbConnectionPool();
    SimpleDateFormat m_sdf = new SimpleDateFormat("yyyy-MM-dd"); 
    
    private final String SQL_INSERT = "INSERT INTO "
            + "turno_rotativo_detalle("
                + "empresa_id, "
                + "rut_empleado, "
                + "id_turno, "
                + "anio, "
                + "mes, "
                + "dias_laborales,"
                + "dias_libres, "
                + "fecha_actualizacion) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, current_timestamp)";
    
    public TurnoRotativoDetalleDAO(PropertiesVO _propsValues) {

    }

    /**
     * Actualiza un detalle_turno_rotativo para un empleado
     * 
     * @param _detalle
     * @return 
     */
    public ResultCRUDVO updateDetalleTurnoEmpleado(TurnoRotativoDetalleVO _detalle){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "detalle Turno rotativo empleado. "
            + "Empresa: " + _detalle.getEmpresaId()
            + ", rut: " + _detalle.getRutEmpleado()
            + ", idTurno: " + _detalle.getId()
            + ", anio: " + _detalle.getAnio()
            + ", mes: " + _detalle.getMes();
        
        try{
            String msgFinal = " Actualiza "
                + "Detalle Turno rotativo empleado:"
                + "empresa [" + _detalle.getEmpresaId() + "]"
                + ", rut [" + _detalle.getRutEmpleado() + "]"
                + ", idTurno [" + _detalle.getId() + "]"
                + ", anio [" + _detalle.getAnio() + "]"
                + ", mes [" + _detalle.getMes() + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE turno_rotativo_detalle "
                    + "SET "
                    + "dias_laborales = ?, "
                    + "dias_libres = ?, "
                    + "fecha_actualizacion = current_tomestamp "
                    + "WHERE empresa_id = ? "
                    + "and rut_empleado = ? "
                    + "and id_turno = ? "
                    + "and anio = ? "
                    + "and mes = ?";
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnoRotativoDetalleDAO.updateDetalleTurnoEmpleado]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setString(1,  _detalle.getDiasLaborales());
            psupdate.setString(2,  _detalle.getDiasLibres());
            //filtro where
            psupdate.setString(3,  _detalle.getEmpresaId());
            psupdate.setString(4,  _detalle.getRutEmpleado());
            psupdate.setInt(5,  _detalle.getId());
            psupdate.setInt(6,  _detalle.getAnio());
            psupdate.setInt(7,  _detalle.getMes());
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println(WEB_NAME+"[TurnoRotativoDetalleDAO.updateDetalleTurnoEmpleado]"
                    + "rut:" + _detalle.getRutEmpleado()
                    + ", empresaId= "+_detalle.getEmpresaId()
                    + ", idTurno= "+_detalle.getId()
                    + ", anio= "+_detalle.getAnio()
                    + ", mes= "+_detalle.getMes()    
                    +" actualizado OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("TurnoRotativoDetalleDAO.updateDetalleTurnoEmpleado Error: "+sqle.toString());
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
     * Agrega un nuevo detalle_turno rotativo
     * @param _detalle
     * @return 
     */
    public ResultCRUDVO insertDetalleTurnoEmpleado(TurnoRotativoDetalleVO _detalle){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        int result=0;
        String msgError = "Error al insertar "
            + "detalle Turno rotativo empleado. "
            + "Empresa: " + _detalle.getEmpresaId()
            + ", rut: " + _detalle.getRutEmpleado()
            + ", idTurno: " + _detalle.getId()
            + ", anio: " + _detalle.getAnio()
            + ", mes: " + _detalle.getMes();
        
       String msgFinal = "Inserta "
                + "detalle Turno rotativo empleado:"
                + "empresa [" + _detalle.getEmpresaId() + "]"
                + ", rut [" + _detalle.getRutEmpleado() + "]"
                + ", idTurno [" + _detalle.getId() + "]"
                + ", anio [" + _detalle.getAnio() + "]"
                + ", mes [" + _detalle.getMes() + "]";
            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO turno_rotativo_detalle("
                + "empresa_id, rut_empleado, id_turno, "
                + "anio, mes, "
                + "dias_laborales, dias_libres, "
                + "fecha_actualizacion) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, current_timestamp)";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnoRotativoDetalleDAO.insertDetalleTurnoEmpleado]");
            insert = dbConn.prepareStatement(sql);
            //filtro where
            insert.setString(1,  _detalle.getEmpresaId());
            insert.setString(2,  _detalle.getRutEmpleado());
            insert.setInt(3,  _detalle.getId());
            insert.setInt(4,  _detalle.getAnio());
            insert.setInt(5,  _detalle.getMes());
            insert.setString(6,  _detalle.getDiasLaborales());
            insert.setString(7,  _detalle.getDiasLibres());
            
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println(WEB_NAME+"[TurnoRotativoDetalleDAO.insert turno_rotativo_detalle]"
                    + "Empresa: " + _detalle.getEmpresaId()
                    + ", rut: " + _detalle.getRutEmpleado()
                    + ", idTurno: " + _detalle.getId()
                    + ", anio: " + _detalle.getAnio()
                    + ", mes: " + _detalle.getMes()
                    +" insertado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("TurnoRotativoDetalleDAO.insert turno_rotativo_detalle Error1: "+sqle.toString());
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
     * Elimina registro en detalle_turno rotativo
     * @param _detalle
     * @return 
     */
    public ResultCRUDVO deleteDetalleTurnoEmpleado(TurnoRotativoDetalleVO _detalle){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        int result=0;
        String msgError = "Error al eliminar "
            + "detalle Turno rotativo empleado. "
            + "Empresa: " + _detalle.getEmpresaId()
            + ", rut: " + _detalle.getRutEmpleado()
            + ", idTurno: " + _detalle.getId()
            + ", anio: " + _detalle.getAnio()
            + ", mes: " + _detalle.getMes();
        
       String msgFinal = "Elimina "
                + "detalle Turno rotativo empleado:"
                + "empresa [" + _detalle.getEmpresaId() + "]"
                + ", rut [" + _detalle.getRutEmpleado() + "]"
                + ", idTurno [" + _detalle.getId() + "]"
                + ", anio [" + _detalle.getAnio() + "]"
                + ", mes [" + _detalle.getMes() + "]";
            
        objresultado.setMsg(msgFinal);
        PreparedStatement deletestmt = null;
        
        try{
            String sql = "delete "
                + "from turno_rotativo_detalle "
                + "where empresa_id='"+_detalle.getEmpresaId() +"' "
                + "and rut_empleado = '"+_detalle.getRutEmpleado()+"'"
                + "and id_turno = "+_detalle.getId()+" "
                + "and anio = '"+_detalle.getAnio()+"' "
                + "and mes = '"+_detalle.getMes()+"'";
               
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnoRotativoDetalleDAO.deleteDetalleTurnoEmpleado]");
            deletestmt = dbConn.prepareStatement(sql);
            //filtro where
            
            int filasAfectadas = deletestmt.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println(WEB_NAME+"[TurnoRotativoDetalleDAO.delete "
                    + "turno_rotativo_detalle]"
                    + "Empresa: " + _detalle.getEmpresaId()
                    + ", rut: " + _detalle.getRutEmpleado()
                    + ", idTurno: " + _detalle.getId()
                    + ", anio: " + _detalle.getAnio()
                    + ", mes: " + _detalle.getMes()
                    +" eliminado OK!");
            }
            
            deletestmt.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("TurnoRotativoDetalleDAO."
                + "delete turno_rotativo_detalle Error1: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (deletestmt != null) deletestmt.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
        
        return objresultado;
    }
    
    /**
     * Retorna lista con los detalles turnos rotativos existentes en el sistema.
     * se muestra el detalle para el empleado en el anio/mes especificado
     * 
     * @param _detalle
     * @return 
     */
    public TurnoRotativoDetalleVO getDetalleTurno(TurnoRotativoDetalleVO _detalle){
        
        TurnoRotativoDetalleVO detalle =null;
        
        PreparedStatement ps        = null;
        ResultSet rs                = null;
        TurnoRotativoDetalleVO data = null;
        
        try{
            String sql = "SELECT "
                + "empresa_id, "
                + "rut_empleado, "
                + "id_turno, "
                + "anio, "
                + "mes, "
                + "dias_laborales, "
                + "dias_libres, "
                + "fecha_actualizacion,"
                + "to_char(fecha_actualizacion, 'yyyy-MM-dd HH24:MI:SS') fecha_modificacion_str  "
                + "FROM turno_rotativo_detalle";

             sql += " where empresa_id='"+_detalle.getEmpresaId() +"' "
                + "and rut_empleado = '"+_detalle.getRutEmpleado()+"'"
                + "and id_turno = "+_detalle.getId()+" "
                + "and anio = "+_detalle.getAnio()+" "
                + "and mes = "+_detalle.getMes();
           
            sql += " order by anio desc,mes desc";
            System.out.println(WEB_NAME+"[TurnoRotativoDetalleDAO.getDetalleTurno]Sql: "+sql);
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnoRotativoDetalleDAO.getDetalleTurno]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                data = new TurnoRotativoDetalleVO();
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setRutEmpleado(rs.getString("rut_empleado"));
                data.setId(rs.getInt("id_turno"));
                data.setAnio(rs.getInt("anio"));
                data.setMes(rs.getInt("mes"));
                data.setDiasLaborales(rs.getString("dias_laborales"));
                data.setDiasLibres(rs.getString("dias_libres"));
                data.setFechaModificacion(rs.getDate("fecha_actualizacion"));
                data.setFechaModificacionAsStr(rs.getString("fecha_modificacion_str"));
               
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("[TurnoRotativoDetalleDAO."
                + "getDetalleTurno]Error: " + sqle.toString());
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
     * Retorna lista con los detalles turnos rotativos existentes en el sistema.
     * se muestra el detalle para el empleado en el anio/mes especificado
     * 
     * @param _empresaId
     * @param _rutEmpleado
     * @param _anio
     * @param _mes
     * @param _fecha
     * @return 
     * 
     * @Deprecated 
     */
    public DetalleTurnoVO getDetalleTurnoLaboralByFecha(String _empresaId, 
            String _rutEmpleado,
            int _anio, int _mes, 
            String _fecha){
        
        DetalleTurnoVO data =null;
        
        PreparedStatement ps        = null;
        ResultSet rs                = null;
        
        try{
            String sql = "select t.id_turno, t.nombre_turno,"
                + "t.hora_entrada,t.hora_salida,t.minutos_colacion "
                + "from turno_rotativo t "
                + "inner join turno_rotativo_detalle d "
                    + "on (t.id_turno=d.id_turno and t.empresa_id=d.empresa_id) "
                    + "where "
                    + "t.empresa_id='" + _empresaId + "' "
                    + "and d.rut_empleado='" + _rutEmpleado + "' "
                    + "and d.anio=" + _anio
                    + " and d.mes=" + _mes
                    + " and d.dias_laborales like '%" + _fecha + "%'";
            
            System.out.println(WEB_NAME+"[TurnoRotativoDetalleDAO."
                + "getDetalleTurnoLaboralByFecha]Sql: "+sql);
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnoRotativoDetalleDAO.getDetalleTurnoLaboralByFecha]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                data = new DetalleTurnoVO();
                data.setIdTurno(rs.getInt("id_turno"));
                data.setNombreTurno(rs.getString("nombre_turno"));
                data.setHoraEntrada(rs.getString("hora_entrada"));
                data.setHoraSalida(rs.getString("hora_salida"));
                //Si la diferencia entre la hora de entrada y las 00:00hrs es menor a 2hrs...consderar
                //    turno nocturno: fecha_salida es el dia sgte....
                Date dteSalida = new Date();
                DiferenciaHorasVO diferenciaInicial = 
                    Utilidades.getTimeDifference(m_sdf.format(new Date()) + " " + data.getHoraEntrada(), 
                                          m_sdf.format(new Date())+" 23:59:59");
                System.out.println(WEB_NAME+"[TRD]horaEntrada: " 
                    + data.getHoraEntrada()
                    +", diferencia en horas con las 23:59:59= " 
                    + diferenciaInicial.getIntDiferenciaHoras());
                if (diferenciaInicial.getIntDiferenciaHoras() <= 4){
                    //fecha salida = dia siguiente
                    dteSalida = Utilidades.sumaRestarFecha(new Date(), 1, "DAYS");
                }
                System.out.println(WEB_NAME+"[TRD]fechaSalida: " + dteSalida);
                System.out.println(WEB_NAME+"[TRD]Resta entrada-salida turno rotativo."
                    + "fecha hora entrada: "+m_sdf.format(new Date()) + " " + data.getHoraEntrada()
                    + ",fecha hora salida: "+m_sdf.format(dteSalida)+" " + data.getHoraSalida());
                
                DiferenciaHorasVO diferenciaET = 
                    Utilidades.getTimeDifference(m_sdf.format(new Date()) + " " + data.getHoraEntrada(), 
                                          m_sdf.format(dteSalida)+" " + data.getHoraSalida());
                
                data.setTotalHoras(diferenciaET.getIntDiferenciaHoras());
                data.setHolgura(0);
                data.setMinutosColacion(rs.getInt("minutos_colacion"));
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("[TurnoRotativoDetalleDAO."
                + "getDetalleTurnoLaboralByFecha]Error: " + sqle.toString());
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
     * Retorna lista con los detalles turnos rotativos (dias libres) existentes en el sistema.
     * se muestra el detalle para el empleado en el anio/mes especificado
     * 
     * @param _empresaId
     * @param _rutEmpleado
     * @param _anio
     * @param _mes
     * @param _fecha
     * @return 
     */
    public DetalleTurnoVO getDetalleTurnoLibreByFecha(String _empresaId, 
            String _rutEmpleado,
            int _anio, int _mes, 
            String _fecha){
        
        DetalleTurnoVO data =null;
        
        PreparedStatement ps        = null;
        ResultSet rs                = null;
        
        try{
            String sql = "select t.id_turno, t.nombre_turno,"
                + "t.hora_entrada,t.hora_salida,t.minutos_colacion "
                + "from turno_rotativo t "
                + "inner join turno_rotativo_detalle d "
                    + "on (t.id_turno=d.id_turno and t.empresa_id=d.empresa_id) "
                    + "where "
                    + "t.empresa_id='" + _empresaId + "' "
                    + "and d.rut_empleado='" + _rutEmpleado + "' "
                    + "and d.anio=" + _anio
                    + " and d.mes=" + _mes
                    + " and d.dias_libres like '%" + _fecha + "%'";
            
            System.out.println(WEB_NAME+"[TurnoRotativoDetalleDAO."
                + "getDetalleTurnoLibreByFecha]Sql: "+sql);
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnoRotativoDetalleDAO.getDetalleTurnoLibreByFecha]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                data = new DetalleTurnoVO();
                data.setIdTurno(rs.getInt("id_turno"));
                data.setNombreTurno(rs.getString("nombre_turno"));
                data.setHoraEntrada(rs.getString("hora_entrada"));
                data.setHoraSalida(rs.getString("hora_salida"));
                DiferenciaHorasVO diferenciaET = 
                    Utilidades.getTimeDifference(m_sdf.format(new Date()) + " " + data.getHoraEntrada(), 
                                          m_sdf.format(new Date())+" " + data.getHoraSalida());
                data.setTotalHoras(diferenciaET.getIntDiferenciaHoras());
                data.setHolgura(0);
                data.setMinutosColacion(rs.getInt("minutos_colacion"));
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("[TurnoRotativoDetalleDAO."
                + "getDetalleTurnoLibreByFecha]Error: " + sqle.toString());
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
    
    public void saveDetailList(ArrayList<TurnoRotativoDetalleVO> _detalles) throws SQLException{
        try (
            PreparedStatement statement = dbConn.prepareStatement(SQL_INSERT);
        ) {
            int i = 0;
            System.out.println(WEB_NAME+"[TurnoRotativoDetalleDAO.saveDetailList]"
                + "items a insertar: "+_detalles.size());
            for (TurnoRotativoDetalleVO entity : _detalles) {
                statement.setString(1,  entity.getEmpresaId());
                statement.setString(2,  entity.getRutEmpleado());
                statement.setInt(3,  entity.getId());
                statement.setInt(4,  entity.getAnio());
                statement.setInt(5,  entity.getMes());
                statement.setString(6,  entity.getDiasLaborales());
                statement.setString(7,  entity.getDiasLibres());

                // ...
                statement.addBatch();
                i++;

                if (i % 50 == 0 || i == _detalles.size()) {
                    int[] rowsAffected = statement.executeBatch(); // Execute every 1000 items.
                    System.out.println(WEB_NAME+"[TurnoRotativoDetalleDAO."
                        + "saveList]filas afectadas= "+rowsAffected.length);
                }
            }
        }
    }
    
    public void openDbConnection(){
        try{
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnoRotativoDetalleDAO.openDbConnection]");
        }catch(DatabaseException dbe){
            System.err.println("[openDbConnection]Error: "+dbe.toString());
            
        }
    }
    
    public void closeDbConnection(){
        try {
            dbLocator.freeConnection(dbConn);
        } catch (Exception ex) {
            System.err.println("[closeDbConnection]Error: "+ex.toString());
        }
    }
}
