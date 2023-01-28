/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.DetalleTurnoVO;
import cl.femase.gestionweb.vo.DiferenciaHorasVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexander
 */
public class DetalleTurnosDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    //private Connection dbConn;
    //DbDirectConnection dbconnection = DbDirectConnection.getInstance();
    SimpleDateFormat m_sdf = new SimpleDateFormat("yyyy-MM-dd"); 
            
    public DetalleTurnosDAO(PropertiesVO _propsValues) {
//        try {
//            dbLoc = DatabaseLocator.getInstance();
//            m_dbpoolName = _propsValues.getDbPoolName();
//        } catch (DatabaseException ex) {
//            m_logger.error("DbError: "+ex.toString());
//        }
    }

     /**
     * Agrega un nuevo detalle turno
     * @param _data
     * @return 
     */
    public ResultCRUDVO insert(DetalleTurnoVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        int result=0;
        String msgError = "Error al insertar "
            + "detalle turno. "
            + " idTurno: "+_data.getIdTurno();
        
       String msgFinal = " Inserta detalle turno:"
            + "idTurno [" + _data.getIdTurno() + "]"
            + ", codDia [" + _data.getCodDia() + "]"
            + ", hraEntrada [" + _data.getHoraEntrada() + "]"
            + ", hraSalida [" + _data.getHoraSalida() + "]"
            + ", minsColacion [" + _data.getMinutosColacion() + "]"
            + ", holgura [" + _data.getHolgura() + "]";
        
        LocalTime entrada = null;
        LocalTime salida = null;   
        
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO detalle_turno ("
                +"id_turno, "
                + "cod_dia, "
                + "hora_entrada, "
                + "hora_salida,"
                + "fecha_hora_modificacion,"
                    + "suma_horas, holgura, minutos_colacion) "
                + " VALUES (?, ?, ?, ?, current_timestamp, ?, ?, ?)";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[DetalleTurnosDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            insert.setInt(1,  _data.getIdTurno());
            insert.setInt(2,  _data.getCodDia());
            insert.setTime(3,  getHora(_data.getHoraEntrada()));
            insert.setTime(4,  getHora(_data.getHoraSalida()));
            
            DiferenciaHorasVO diferenciaET = 
                Utilidades.getTimeDifference(m_sdf.format(new Date()) + " " + _data.getHoraEntrada(), 
                                      m_sdf.format(new Date())+" " + _data.getHoraSalida());
            insert.setDouble(5,  diferenciaET.getIntDiferenciaHoras());
            insert.setInt(6,  _data.getHolgura());
            insert.setInt(7,  _data.getMinutosColacion());
            
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println(WEB_NAME+"[insert detalle turno]"
                    + ", idTurno:" +_data.getIdTurno()
                    + ", cod_dia:" +_data.getCodDia()
                    + ", hraEntrada:" + _data.getHoraEntrada() + " "
                    + ", hraSalida: " + _data.getHoraSalida() + " "
                    + ", holgura: " + _data.getHolgura() + " "
                    + ", minsColacion: " + _data.getMinutosColacion() + " "        
                    +" insertado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("insert detalle turno Error1: "+sqle.toString());
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
     * Actualiza un detalle turno
     * @param _data
     * @return 
     */
    public ResultCRUDVO update(DetalleTurnoVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        int result=0;
        String msgError = "Error al actualizar "
            + "detalle turno. "
            + " idTurno: " + _data.getIdTurno()
            +", codDia: " + _data.getCodDia()
            +", horaEntrada: " + _data.getHoraEntrada()
            +", horaSalida: " + _data.getHoraSalida()
            +", holgura: " + _data.getHolgura()
            +", minsColacion: " + _data.getMinutosColacion();
        
       String msgFinal = " Actualiza detalle turno:"
            + "idTurno [" + _data.getIdTurno() + "]"
            + ", codDia [" + _data.getCodDia() + "]"
            + ", hraEntrada [" + _data.getHoraEntrada() + "]"
            + ", hraSalida [" + _data.getHoraSalida() + "]"
            + ", holgura [" + _data.getHolgura() + "]"
            + ", minsColacion [" + _data.getMinutosColacion() + "]";
            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "UPDATE detalle_turno "
                + "SET hora_entrada=?, "
                    + "hora_salida=?, "
                    + "fecha_hora_modificacion = current_timestamp,"
                    + "suma_horas=?,"
                    + "holgura=?,"
                    + "minutos_colacion=? "
                + "WHERE id_turno = ? "
                + "and cod_dia = ? ";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[DetalleTurnosDAO.update]");
            insert = dbConn.prepareStatement(sql);
            insert.setTime(1,  getHora(_data.getHoraEntrada()));
            insert.setTime(2,  getHora(_data.getHoraSalida()));
            DiferenciaHorasVO diferenciaET = 
                Utilidades.getTimeDifference(m_sdf.format(new Date()) + " " + _data.getHoraEntrada(), 
                                      m_sdf.format(new Date())+" " + _data.getHoraSalida());
            insert.setDouble(3,  diferenciaET.getIntDiferenciaHoras());
            
            insert.setInt(4,  _data.getHolgura());
            insert.setInt(5,  _data.getMinutosColacion());                        
            
            //insert.setDouble(3,  Utilidades.getTotalHoras(_data.getHoraEntrada(), _data.getHoraSalida()));
            insert.setInt(6,  _data.getIdTurno());
            insert.setInt(7,  _data.getCodDia());                        
            
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println(WEB_NAME+"[update detalle turno]"
                    + ", idTurno:" +_data.getIdTurno()
                    + ", cod_dia:" +_data.getCodDia()
                    + ", hraEntrada:" + _data.getHoraEntrada() + " "
                    + ", hraSalida: " + _data.getHoraSalida() + " "    
                    + ", holgura: " + _data.getHolgura() + " "
                    + ", minsColacion: " + _data.getMinutosColacion() + " "        
                    +" actualizado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("update detalle turno Error1: "+sqle.toString());
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
     * Elimina un detalle turno
     * @param _data
     * @return 
     */
    public ResultCRUDVO delete(DetalleTurnoVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        int result=0;
        String msgError = "Error al eliminar "
            + "detalle turno. "
            + " idTurno: "+_data.getIdTurno()
            +", codDia: "+_data.getCodDia()
            +", horaEntrada: "+_data.getHoraEntrada()
            +", horaSalida: "+_data.getHoraSalida();
        
       String msgFinal = " Elimina detalle turno:"
            + "idTurno [" + _data.getIdTurno() + "]"
            + ", codDia [" + _data.getCodDia() + "]"
            + ", hraEntrada [" + _data.getHoraEntrada() + "]"
            + ", hraSalida [" + _data.getHoraSalida() + "]";
            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "delete from detalle_turno "
                + "WHERE id_turno = ? "
                + "and cod_dia = ? ";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[DetalleTurnosDAO.delete]");
            insert = dbConn.prepareStatement(sql);
            insert.setInt(1,  _data.getIdTurno());
            insert.setInt(2,  _data.getCodDia());                        
            
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println(WEB_NAME+"[delete detalle turno]"
                    + ", idTurno:" +_data.getIdTurno()
                    + ", cod_dia:" +_data.getCodDia()
                    + ", hraEntrada:" + _data.getHoraEntrada() + " "
                    + ", hraSalida: " + _data.getHoraSalida() + " "    
                    +" eliminado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("delete detalle turno Error1: "+sqle.toString());
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
     * Retorna un objeto Time.
     * 
     */
    private java.sql.Time getHora(String _hora){
        java.sql.Time theTime = null;
        
        SimpleDateFormat horaformat = new SimpleDateFormat("HH:mm:ss");
        Date asDate = null;
        try{
            asDate = horaformat.parse(_hora);
            theTime = new java.sql.Time(asDate.getTime());
        }catch(ParseException pex){
            System.err.println("[DetalleTurnosDAO.getHora]"
                + "Error al parsear hora entrada: "+pex.getMessage());
        }
        
        return theTime;
    }
    
    /**
     * Retorna el detalle de un turno especifico
     * 
     * @param _empresaId
     * @param _idTurno
     * @param _codDia
     * @return 
     */
    public DetalleTurnoVO getDetalleTurno(String _empresaId, 
            int _idTurno, 
            int _codDia){
        
        DetalleTurnoVO detalleTurno = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            String sql = "select "
                    + "turno.nombre_turno,"
                    + "dt.hora_entrada,"
                    + "dt.hora_salida,"
                    + "dt.holgura, "
                    + "dt.minutos_colacion "
                + "from detalle_turno dt "
                + " inner join turno turno on (turno.id_turno = dt.id_turno "
                    + "and turno.empresa_id='" + _empresaId + "') "
                + "where dt.id_turno=" + _idTurno
                    +" and dt.cod_dia= " + _codDia;
            
            System.out.println(WEB_NAME+"[DetalleTurnosDAO.getDetalleTurno]Sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[DetalleTurnosDAO.getDetalleTurno]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                detalleTurno = new DetalleTurnoVO();
                detalleTurno.setIdTurno(_idTurno);
                detalleTurno.setNombreTurno(rs.getString("nombre_turno"));
                detalleTurno.setCodDia(_codDia);
                detalleTurno.setIdTurno(_idTurno);
                detalleTurno.setHoraEntrada(rs.getString("hora_entrada"));
                detalleTurno.setHoraSalida(rs.getString("hora_salida"));
                detalleTurno.setMinutosColacion(rs.getInt("minutos_colacion"));
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
                System.err.println("[DetalleTurnosDAO.getDetalleTurno]Error: "+ex.toString());
            }
        }
        return detalleTurno;
    }
    
    /**
     * Retorna lista con el detalle de un turno especifico
     * 
     * @param _idTurno
     * @param _jtStartIndex
     * @param _jtPageSize
     * @param _jtSorting
     * @return 
     */
    public List<DetalleTurnoVO> getDetalleTurno(int _idTurno,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<DetalleTurnoVO> lista = 
                new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        DetalleTurnoVO data;
        
        try{
            String sql = "SELECT "
                + " detalle_turno.id_turno,"
                + "turno.nombre_turno,"
                + " detalle_turno.cod_dia,"
                + "dias_semana.label_dia,"
                + "dias_semana.label_corto,"
                + "detalle_turno.hora_entrada,"
                + "detalle_turno.hora_salida,"
                + "detalle_turno.hora_entrada hora_entrada_date,"
                + "detalle_turno.hora_salida hora_salida_date,"
                + "detalle_turno.fecha_hora_modificacion,"
                + "to_char(detalle_turno.fecha_hora_modificacion, 'yyyy-MM-dd HH24:MI:SS') fecha_modificacion_str, "
                + "detalle_turno.holgura,"
                    + "detalle_turno.minutos_colacion "
                + "FROM "
                + "detalle_turno,"
                + "turno,"
                + "dias_semana "
                + "WHERE "
                + "detalle_turno.id_turno = turno.id_turno AND "
                + "dias_semana.cod_dia = detalle_turno.cod_dia ";
                    
            if (_idTurno != -1){        
                sql += " and detalle_turno.id_turno = "+_idTurno;
            }
           
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[DetalleTurnosDAO.getDetalleTurno]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new DetalleTurnoVO();
                data.setIdTurno(rs.getInt("id_turno"));
                data.setNombreTurno(rs.getString("nombre_turno"));
                data.setCodDia(rs.getInt("cod_dia"));
                data.setLabelDia(rs.getString("label_dia"));
                data.setLabelCortoDia(rs.getString("label_corto"));
                
                data.setHoraEntrada(rs.getString("hora_entrada"));
                data.setHoraSalida(rs.getString("hora_salida"));
                
                data.setFechaModificacion(rs.getDate("fecha_hora_modificacion"));
                data.setFechaModificacionAsStr(rs.getString("fecha_modificacion_str"));
                data.setHolgura(rs.getInt("holgura"));
                data.setMinutosColacion(rs.getInt("minutos_colacion"));
                
                DiferenciaHorasVO diferenciaET = 
                    Utilidades.getTimeDifference(m_sdf.format(new Date()) + " " + data.getHoraEntrada(), 
                                          m_sdf.format(new Date())+" " + data.getHoraSalida());
                data.setTotalHoras(diferenciaET.getIntDiferenciaHoras());
                
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
    
    public LinkedHashMap<Integer,DetalleTurnoVO> getHashDetalleTurno(int _idTurno){
        
        LinkedHashMap<Integer,DetalleTurnoVO> lista = 
            new LinkedHashMap<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        DetalleTurnoVO data;
        
        try{
            String sql = "SELECT "
                + " detalle_turno.id_turno,"
                + "turno.nombre_turno,"
                + "detalle_turno.holgura, " 
                + "detalle_turno.minutos_colacion, " 
                + " detalle_turno.cod_dia,"
                + "dias_semana.label_dia,"
                + "dias_semana.label_corto,"
                + "detalle_turno.hora_entrada,"
                + "detalle_turno.hora_salida,"
                + "detalle_turno.hora_entrada hora_entrada_date,"
                + "detalle_turno.hora_salida hora_salida_date,"
                + "detalle_turno.fecha_hora_modificacion,"
                + "to_char(detalle_turno.fecha_hora_modificacion, 'yyyy-MM-dd HH24:MI:SS') fecha_modificacion_str "
                + "FROM "
                + "detalle_turno,"
                + "turno,"
                + "dias_semana "
                + "WHERE "
                + "detalle_turno.id_turno = turno.id_turno AND "
                + "dias_semana.cod_dia = detalle_turno.cod_dia ";
                    
            if (_idTurno != -1){        
                sql += " and detalle_turno.id_turno = "+_idTurno;
            }
           
            sql += " order by detalle_turno.cod_dia";
            
            System.out.println(WEB_NAME+"[DetalleTurnosDAO."
                    + "getHashDetalleTurno]"
                    + "Sql: " +sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[DetalleTurnosDAO.getHashDetalleTurno]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new DetalleTurnoVO();
                data.setIdTurno(rs.getInt("id_turno"));
                data.setNombreTurno(rs.getString("nombre_turno"));
                data.setHolgura(rs.getInt("holgura"));
                data.setCodDia(rs.getInt("cod_dia"));
                data.setLabelDia(rs.getString("label_dia"));
                data.setLabelCortoDia(rs.getString("label_corto"));
                
                data.setHoraEntrada(rs.getString("hora_entrada"));
                data.setHoraSalida(rs.getString("hora_salida"));
                
                data.setFechaModificacion(rs.getDate("fecha_hora_modificacion"));
                data.setFechaModificacionAsStr(rs.getString("fecha_modificacion_str"));
                
                DiferenciaHorasVO diferenciaET = 
                    Utilidades.getTimeDifference(m_sdf.format(new Date()) + " " + data.getHoraEntrada(), 
                        m_sdf.format(new Date())+" " + data.getHoraSalida());
                
                data.setTotalHoras(diferenciaET.getIntDiferenciaHoras());
                data.setMinutosColacion(rs.getInt("minutos_colacion"));
                lista.put(data.getCodDia(),data);
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
     * @param _idTurno
     * @return 
     */
    public int getDetalleTurnoCount(int _idTurno){
        int count=0;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[DetalleTurnosDAO.getDetalleTurnoCount]");
            ResultSet rs;
            try (Statement statement = dbConn.createStatement()) {
                String strSql ="SELECT "
                        + "count(detalle_turno.id_turno) "
                        + "FROM "
                        + "detalle_turno,"
                        + "turno,"
                        + "dias_semana "
                        + "WHERE "
                        + "detalle_turno.id_turno = turno.id_turno AND "
                        + "dias_semana.cod_dia = detalle_turno.cod_dia ";
                if (_idTurno != -1){
                    strSql += " and detalle_turno.id_turno = "+_idTurno;
                }   rs = statement.executeQuery(strSql);
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
   
}
