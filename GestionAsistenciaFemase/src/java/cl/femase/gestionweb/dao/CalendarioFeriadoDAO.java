/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.CalendarioFeriadoVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexander
 */
public class CalendarioFeriadoDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    //private Connection dbConn;
    //DbDirectConnection dbconnection = DbDirectConnection.getInstance();
    
    public CalendarioFeriadoDAO(PropertiesVO _propsValues) {
//        try {
//            dbLoc = DatabaseLocator.getInstance();
//            m_dbpoolName = _propsValues.getDbPoolName();
//        } catch (DatabaseException ex) {
//            m_logger.error("DbError: "+ex.toString());
//        }
    }

    /**
     * Actualiza un calendario feriado
     * @param _data
     * @return 
     */
    public MaintenanceVO update(CalendarioFeriadoVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        PreparedStatement psupdate = null;
        int result=0;
        //llave para realizar el update
        StringTokenizer pkToken = new StringTokenizer(_data.getRowKey(), "|");
        String fechaPK        = pkToken.nextToken();
        int idTipoFeriadoPK   = Integer.parseInt(pkToken.nextToken());
        int regionIdPK        = Integer.parseInt(pkToken.nextToken());
        int comunaIdPK        = Integer.parseInt(pkToken.nextToken());
        
        String msgError = "Error al actualizar "
            + "CalendarioFeriado, "
            + "fecha: "+_data.getFecha()
            + ", dia: "+_data.getDia()
            + ", mes: "+_data.getMes()
            + ", anio: "+_data.getMes()
            + ", label: "+_data.getLabel()
            + ", observacion: "+_data.getObservacion();
        
        try{
            String msgFinal = " Actualiza CalendarioFeriado:"
                + "fecha [" + _data.getFecha() + "]" 
                + ", dia [" + _data.getDia() + "]"
                + ", mes [" + _data.getMes() + "]"
                + ", anio [" + _data.getAnio() + "]"
                + ", label [" + _data.getLabel() + "]"
                + ", observacion [" + _data.getObservacion() + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE calendario_feriados "
                + "SET "
                    + "dia = ?, "
                    + "mes = ?, "
                    + "anio = ?, "
                    + "label = ?, "
                    + "observacion = ?,"
                    + "fecha_hora_actualizacion = current_timestamp,"
                    + "irrenunciable = ?, tipo = ?, respaldo_legal = ?, cal_id_tipo_feriado = ?, cal_region_id = ?, cal_comuna_id = ? "
                + "where "
                    + "(fecha = '" + fechaPK + "') "
                    + " and (cal_id_tipo_feriado = " + idTipoFeriadoPK + ") "
                    + " and (cal_region_id = " + regionIdPK + ") "
                    + " and (cal_comuna_id = " + comunaIdPK + ") ";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[CalendarioFeriadoDAO.update]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setInt(1,  _data.getDia());
            psupdate.setInt(2,  _data.getMes());
            psupdate.setInt(3,  _data.getAnio());
            psupdate.setString(4,  _data.getLabel());
            psupdate.setString(5,  _data.getObservacion());
            
            //set de nuevas columnas segun requerimiento ID: 20210527-002:
            psupdate.setString(6,  _data.getIrrenunciable());
            psupdate.setString(7,  _data.getTipo());
            psupdate.setString(8,  _data.getRespaldoLegal());
            psupdate.setInt(9,  _data.getIdTipoFeriado());
            psupdate.setInt(10,  _data.getRegionId());
            psupdate.setInt(11,  _data.getComunaId());
                        
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println("[CalendarioFeriadoDAO.update]"
                    + ", fecha: " + _data.getFecha()
                    + ", label: " + _data.getLabel()
                    + ", observacion: " + _data.getObservacion()
                    + ", respaldo_legal: " + _data.getRespaldoLegal()
                    + ", tipo: " + _data.getTipo()    
                    +" actualizado OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[CalendarioFeriadoDAO.update]"
                + "Error: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (psupdate != null) psupdate.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[CalendarioFeriadoDAO.update]"
                    + "Error: " + ex.toString());
            }
        }

        return objresultado;
    }

    /**
     * Agrega un nuevo CalendarioFeriado
     * @param _data
     * @return 
     */
    public MaintenanceVO insert(CalendarioFeriadoVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al insertar "
            + "CalendarioFeriado, "
            + "fecha: "+_data.getFecha()
            + ", label: "+_data.getLabel()
            + ", observacion: "+_data.getObservacion();
        
        String msgFinal = " Inserta CalendarioFeriado:"
            + "fecha [" + _data.getFecha() + "]" 
            + ", label [" + _data.getLabel() + "]"
            + ", observacion [" + _data.getObservacion() + "]";
       
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO calendario_feriados("
                    + "fecha, "
                    + "dia, "
                    + "mes, "
                    + "anio, "
                    + "label, "
                    + "observacion, "
                    + "fecha_hora_ingreso,"
                    + "fecha_hora_actualizacion,"
                    + "irrenunciable, "
                    + "tipo, "
                    + "respaldo_legal, "
                    + "cal_id_tipo_feriado, "
                    + "cal_region_id, "
                    + "cal_comuna_id) "
                + "VALUES ('"+_data.getFecha()+"', ?, ?, ?, ?, ?, "
                    + "current_timestamp, "
                    + "current_timestamp, ?, ?, ?, ?, ?, ?)";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[CalendarioFeriadoDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            insert.setInt(1,  _data.getDia());
            insert.setInt(2,  _data.getMes());
            insert.setInt(3,  _data.getAnio());
            insert.setString(4,  _data.getLabel());
            insert.setString(5,  _data.getObservacion());
            
            //set de nuevas columnas segun requerimiento ID: 20210527-002:
            insert.setString(6,  _data.getIrrenunciable());
            insert.setString(7,  _data.getTipo());
            insert.setString(8,  _data.getRespaldoLegal());
            insert.setInt(9,  _data.getIdTipoFeriado());
            insert.setInt(10,  _data.getRegionId());
            insert.setInt(11,  _data.getComunaId());
            
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println("[CalendarioFeriadoDAO.insert]"
                    + ", fecha: " + _data.getFecha()
                    + ", label: " + _data.getLabel()
                    + ", observacion: " + _data.getObservacion()    
                    + ", ID_tipo_feriado: " + _data.getIdTipoFeriado()
                    + ", regionId: " + _data.getRegionId()
                    + ", comunaId: " + _data.getComunaId()    
                    + ", irrenunciable: " + _data.getIrrenunciable()
                    + ", respaldo_legal: " + _data.getRespaldoLegal()    
                    +" insertada OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[CalendarioFeriadoDAO.insert]"
                + "Error1: " + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (insert != null) insert.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[CalendarioFeriadoDAO.insert]"
                    + "Error: " + ex.toString());
            }
        }

        return objresultado;
    }
    
    /**
    * Elimina un Feriado
    * @param _rowKey: llave compuesta de la forma "fecha|idTipoFeriado|regionId|comunaId"
    * 
    * @return 
    */
    public MaintenanceVO delete(String _rowKey){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        
        //llave para realizar el delete
        StringTokenizer pkToken = new StringTokenizer(_rowKey, "|");
        String fechaPK        = pkToken.nextToken();
        int idTipoFeriadoPK   = Integer.parseInt(pkToken.nextToken());
        int regionIdPK        = Integer.parseInt(pkToken.nextToken());
        int comunaIdPK        = Integer.parseInt(pkToken.nextToken());
        
        String msgError = "Error al eliminar "
            + "CalendarioFeriado PK, "
            + "fecha: " + fechaPK
            + ", ID_tipo_feriado: " + idTipoFeriadoPK
            + ", region_id: " + regionIdPK
            + ", comuna_id: " + comunaIdPK;
        
        String msgFinal = " Elimina CalendarioFeriado: PK->"
            + "fecha [" + fechaPK + "]"
            + ", ID_tipo_feriado [" + idTipoFeriadoPK + "]"
            + ", region_id [" + regionIdPK + "]"
            + ", comuna_id [" + comunaIdPK + "]";
       
        objresultado.setMsg(msgFinal);
        PreparedStatement delete = null;
        
        try{
            String sql = "delete "
                + "from calendario_feriados "
                + "where "
                + "(fecha = '" + fechaPK + "') "
                + " and (cal_id_tipo_feriado = " + idTipoFeriadoPK + ") "
                + " and (cal_region_id = " + regionIdPK + ") "
                + " and (cal_comuna_id = " + comunaIdPK + ") ";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[CalendarioFeriadoDAO.delete]");
            delete = dbConn.prepareStatement(sql);
            
            int filasAfectadas = delete.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println("[CalendarioFeriadoDAO.delete]"
                    + ", fecha:" + fechaPK
                    + ", ID_tipo_feriado: " + idTipoFeriadoPK
                    + ", region_id: " + regionIdPK
                    + ", comuna_id: " + comunaIdPK  
                    +" delete OK!");
            }
            
            delete.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[CalendarioFeriadoDAO.delete]"
                + "Error1: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (delete != null) delete.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[CalendarioFeriadoDAO.delete]"
                    + "Error: " + ex.toString());
            }
        }

        return objresultado;
    }
    
    /**
     * Retorna lista con los Feriados existentes en el sistema
     * 
     * @param _anio
     * @param _mes
     * @param _tipoFeriado
     * @param _jtStartIndex
     * @param _jtPageSize
     * @param _jtSorting
     * @return 
     */
    public List<CalendarioFeriadoVO> getFeriados(int _anio,
            int _mes,
            int _tipoFeriado,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<CalendarioFeriadoVO> lista = 
            new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        CalendarioFeriadoVO data;
        Calendar calHoy     = Calendar.getInstance(new Locale("es","CL"));
        Date fechaActual    = calHoy.getTime();
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy");
        String defaultYear      = sdf.format(fechaActual);
        
        try{
            String sql = "SELECT fecha, "
                + "dia, mes, anio, "
                + "label, observacion, "
                + "fecha_hora_ingreso,"
                + "to_char(fecha_hora_ingreso, 'yyyy-MM-dd HH24:MI:SS') fecha_ingreso_str,"
                + "fecha_hora_actualizacion,"
                + "to_char(fecha_hora_actualizacion, 'yyyy-MM-dd HH24:MI:SS') fecha_actualiza_str,"
                + "irrenunciable, "
                + "tipo, "
                + "respaldo_legal, "
                + "cal_id_tipo_feriado, "
                + "cal_region_id, "
                + "cal_comuna_id "
                + "FROM calendario_feriados "
                + "where (1 = 1) ";
                               
            if (_anio != -1){        
                sql += " and (anio = " + _anio + ") ";
            }else{
                sql += " and (anio = " + defaultYear + ") ";
            }
            if (_mes != -1){        
                sql += " and mes = " + _mes;
            }
            if (_tipoFeriado != -1){        
                sql += " and cal_id_tipo_feriado = " + _tipoFeriado;
            }
            
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            System.out.println("[CalendarioFeriadoDAO."
                + "getCalendarioFeriados]"
                + "anio: "+_anio + ", mes: "+_mes
                + ", _jtStartIndex: "+_jtStartIndex
                + ", _jtPageSize: "+_jtPageSize
                + ", _jtSorting: "+_jtSorting
                + ", Sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[CalendarioFeriadoDAO.getFeriados]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new CalendarioFeriadoVO();
                data.setFecha(rs.getString("fecha"));
                data.setDia(rs.getInt("dia"));
                data.setMes(rs.getInt("mes"));
                data.setAnio(rs.getInt("anio"));
                data.setLabel(rs.getString("label"));
                data.setObservacion(rs.getString("observacion"));
                data.setFechaHoraIngreso(rs.getString("fecha_ingreso_str"));
                data.setFechaHoraActualizacion(rs.getString("fecha_actualiza_str"));
                
                data.setIrrenunciable(rs.getString("irrenunciable"));
                data.setTipo(rs.getString("tipo"));
                data.setRespaldoLegal(rs.getString("respaldo_legal"));
                data.setIdTipoFeriado(rs.getInt("cal_id_tipo_feriado"));
                data.setRegionId(rs.getInt("cal_region_id"));
                data.setComunaId(rs.getInt("cal_comuna_id"));
                
                String strKey = data.getFecha() + "|" 
                    + data.getIdTipoFeriado() + "|" 
                    + data.getRegionId() + "|" 
                    + data.getComunaId();
                
                data.setRowKey(strKey);
                
                lista.add(data);
            }

            ps.close();
            rs.close();
            
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
    * Retorna info de una fecha, respecto a si es feriado o no
    * 
    * @param _empresaId
    * @param _cencoId
    * @param _rutEmpleado
    * @param _fecha
    * @return 
    */
    public String esFeriadoJson(String _empresaId, 
            int _cencoId,
            String _rutEmpleado,
            String _fecha){

        String strJson = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            
            String sql = "SELECT json_agg(validaFechaFeriado) strjson "
                    + "FROM validaFechaFeriado('" + _empresaId + "'," + _cencoId + ",'" + _rutEmpleado + "','" + _fecha + "')";
            
            System.out.println("[CalendarioFeriadoDAO.esFeriadoJson]Sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[CalendarioFeriadoDAO.esFeriadoJson]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                strJson += rs.getString("strjson");
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[CalendarioFeriadoDAO.esFeriadoJson]"
                + "Error: " + sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[CalendarioFeriadoDAO.esFeriadoJson]"
                    + "Error: " + ex.toString());
            }
        }
        
        return strJson;
    }
    
    /**
    * Retorna info de una fecha, respecto a si es feriado o no
    * 
    * @param _empresaId
    * @param _cencoId
    * @param _rutEmpleado
    * @param _fechaInicio
    * @param _fechaFin
    * @return 
    */
    public String getValidaFechasJson(String _empresaId, 
            int _cencoId,
            String _rutEmpleado,
            String _fechaInicio, 
            String _fechaFin){

        String strJson = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            String sql = "select json_agg(f.*) json_it "
                + "from generate_series( '" + _fechaInicio + "', '" + _fechaFin + "', '1 day'::interval) as fecha_it "
                + "cross join validaFechaFeriado('" + _empresaId + "'," + _cencoId + ",'" + _rutEmpleado + "',fecha_it::date) f";
            
            System.out.println("[CalendarioFeriadoDAO.getValidaFechasJson]Sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[CalendarioFeriadoDAO.getValidaFechasJson]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                strJson += rs.getString("json_it");
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[CalendarioFeriadoDAO.getValidaFechasJson]"
                + "Error: " + sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[CalendarioFeriadoDAO.getValidaFechasJson]"
                    + "Error: " + ex.toString());
            }
        }
        
        return strJson;
    }
    
    /**
    * Entrega lista de feriados.
    * La llave de cada elemento es fecha|tipo_feriado_regionId|comunaId
    * 
    * @param _startDate
    * @param _endDate
    * @return 
    */
    public HashMap<String,String> getHashFeriadosNew(String _startDate, String _endDate){
        
        HashMap<String,String> lista = 
            new HashMap<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        CalendarioFeriadoVO data;
        
        try{
            String sql = "SELECT fecha, "
                    + "dia, mes, anio, "
                    + "label, observacion, "
                    + "fecha_hora_ingreso,"
                    + "to_char(fecha_hora_ingreso, 'yyyy-MM-dd HH24:MI:SS') fecha_ingreso_str,"
                    + "fecha_hora_actualizacion,"
                    + "to_char(fecha_hora_actualizacion, 'yyyy-MM-dd HH24:MI:SS') fecha_actualiza_str,"
                    + "cal_id_tipo_feriado, cal_region_id, cal_comuna_id "
                + "FROM calendario_feriados "
                + "where fecha between '" + _startDate + "' and '" + _endDate + "'";  
            
            sql += " order by fecha"; 
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[CalendarioFeriadoDAO.getHashFeriados]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new CalendarioFeriadoVO();
                data.setFecha(rs.getString("fecha"));
                data.setDia(rs.getInt("dia"));
                data.setMes(rs.getInt("mes"));
                data.setAnio(rs.getInt("anio"));
                data.setLabel(rs.getString("label"));
                data.setObservacion(rs.getString("observacion"));
                data.setFechaHoraIngreso(rs.getString("fecha_ingreso_str"));
                data.setFechaHoraActualizacion(rs.getString("fecha_actualiza_str"));
            
                data.setIdTipoFeriado(rs.getInt("cal_id_tipo_feriado"));
                data.setRegionId(rs.getInt("cal_region_id"));
                data.setComunaId(rs.getInt("cal_comuna_id"));
                
                String strKey = data.getFecha() + "|" 
                    + data.getIdTipoFeriado() + "|" 
                    + data.getRegionId() + "|" 
                    + data.getComunaId();
                data.setRowKey(strKey);
                
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
     * @param _anio
     * @param _mes
     * @param _tipoFeriado
     * 
     * @return 
     */
    public int getFeriadosCount(int _anio, int _mes, int _tipoFeriado){
        int count=0;
        ResultSet rs = null;
        Statement statement = null;
        Calendar calHoy     = Calendar.getInstance(new Locale("es","CL"));
        Date fechaActual    = calHoy.getTime();
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy");
        String defaultYear      = sdf.format(fechaActual);
        
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[CalendarioFeriadoDAO.getFeriadosCount]");
            statement = dbConn.createStatement();
            String sql = "SELECT count(*) as cuenta "
                + "FROM calendario_feriados "
                + "where (1 = 1) ";
                               
            if (_anio != -1){        
                sql += " and (anio = " + _anio + ") ";
            }else{
                sql += " and (anio = " + defaultYear + ") ";
            }
            if (_mes != -1){        
                sql += " and (mes = " + _mes + ") ";
            }
            if (_tipoFeriado != -1){        
                sql += " and cal_id_tipo_feriado = " + _tipoFeriado;
            }
            rs = statement.executeQuery(sql);		
            if (rs.next()) {
                count = rs.getInt("cuenta");
            }
            
            statement.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        } catch (SQLException|DatabaseException e) {
            e.printStackTrace();
        }finally{
            try {
                if (statement != null) statement.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
        return count;
    }
   
}
