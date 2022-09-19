/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.TiempoExtraVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexander
 */
public class TiempoExtraDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    //private Connection dbConn;
    //DbDirectConnection dbconnection = DbDirectConnection.getInstance();
    
    public TiempoExtraDAO(PropertiesVO _propsValues) {
//        try {
//            dbLoc = DatabaseLocator.getInstance();
//            m_dbpoolName = _propsValues.getDbPoolName();
//        } catch (DatabaseException ex) {
//            m_logger.error("DbError: "+ex.toString());
//        }
    }

    /**
     * Actualiza un tiempo extra
     * @param _data
     * @return 
     */
    public MaintenanceVO update(TiempoExtraVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "tiempo_extra, "
            + "rut: "+_data.getRut()
            + ", fecha: "+_data.getFechaAsStr()
            + ", tiempoExtra: "+_data.getTiempoExtra();
        
        try{
            String msgFinal = " Actualiza Tiempo Extra:"
                + "rut [" + _data.getRut() + "]" 
                + ", fecha [" + _data.getFechaAsStr() + "]"
                + ", tiempoExtra [" + _data.getTiempoExtra() + "]"
                + ", tipo [" + _data.getTipo() + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE tiempo_extra "
                + "SET  "
                + "tiempo_extra = '"+_data.getTiempoExtra()+"', "
                + "fechahora_actualizacion = current_timestamp, "
                    + "usuario_responsable = ?, "
                    + "tipo = ? "
                + "WHERE rut_empleado = ? and "
                + "fecha = '"+_data.getFechaAsStr()+"'";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[TiempoExtraDAO.update]");
            psupdate = dbConn.prepareStatement(sql);
            //psupdate.setString(1,  _data.getTiempoExtra());
            psupdate.setString(1,  _data.getUsuarioResponsable());
            psupdate.setString(2,  _data.getTipo());
            psupdate.setString(3,  _data.getRut());
            //psupdate.setString(4,  _data.getFechaAsStr());
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println(WEB_NAME+"[update]tiempo_extra"
                    + ", fecha:" +_data.getFechaAsStr()
                    + ", tiempoExtra:" +_data.getTiempoExtra()
                    +" actualizado OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("update tiempo_extra Error: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }

        return objresultado;
    }
    
    /**
     * Elimina un tiempo extra
     * @param _data
     * @return 
     */
    public MaintenanceVO delete(TiempoExtraVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al eliminar "
            + "tiempo_extra, "
            + "rut: "+_data.getRut()
            + ", fecha: "+_data.getFechaAsStr()
            + ", tiempoExtra: "+_data.getTiempoExtra();
        
        try{
            String msgFinal = " Eliminar Tiempo Extra:"
                + "rut [" + _data.getRut() + "]" 
                + ", fecha [" + _data.getFechaAsStr() + "]"
                + ", tiempoExtra [" + _data.getTiempoExtra() + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "delete from tiempo_extra "
                + "WHERE rut_empleado = ? and "
                + "fecha = '"+_data.getFechaAsStr()+"'";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[TiempoExtraDAO.delete]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setString(1, _data.getRut());
            //psupdate.setString(2, _data.getFechaAsStr());
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println(WEB_NAME+"[delete]tiempo_extra."
                    + ", rut:" +_data.getRut()
                    + ", fecha:" +_data.getFechaAsStr()
                    + ", tiempoExtra:" +_data.getTiempoExtra()
                    +" eliminado OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("delete tiempo_extra Error: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }
        
        return objresultado;
    }

    /**
     * Agrega un nuevo tiempo extra
     * @param _data
     * @return 
     */
    public MaintenanceVO insert(TiempoExtraVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        PreparedStatement psinsert = null;
        int result=0;
        String msgError = "Error al ainsertar "
            + "tiempo_extra, "
            + "rut: "+_data.getRut()
            + ", fecha: "+_data.getFechaAsStr()
            + ", tiempoExtra: "+_data.getTiempoExtra();
        
        try{
            String msgFinal = " Inserta Tiempo Extra:"
                + "rut [" + _data.getRut() + "]" 
                + ", fecha [" + _data.getFechaAsStr() + "]"
                + ", tiempoExtra [" + _data.getTiempoExtra() + "]";
            objresultado.setMsg(msgFinal);
            String sql = "INSERT INTO tiempo_extra("
                + "rut_empleado, "
                + "fecha, "
                + "tiempo_extra, "
                + "fechahora_ingreso, "
                + "fechahora_actualizacion,"
                + "usuario_responsable,tipo) "
                + " VALUES (?, '"+_data.getFechaAsStr()
                +"', '"+_data.getTiempoExtra()
                +"', current_timestamp, current_timestamp, ?, ?)";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[TiempoExtraDAO.insert]");
            psinsert = dbConn.prepareStatement(sql);
            psinsert.setString(1,  _data.getRut());
            //psinsert.setString(2,  _data.getFechaAsStr());
            //psinsert.setString(3,  _data.getTiempoExtra());
            psinsert.setString(2,  _data.getUsuarioResponsable());
            psinsert.setString(3,  _data.getTipo());
                                               
            int filasAfectadas = psinsert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println(WEB_NAME+"[insert tiempo_extra]"
                    + ", rut:" +_data.getRut()
                    + ", fecha:" +_data.getFechaAsStr()
                    + ", tiempoExtra:" +_data.getTiempoExtra()    
                    +" insertado OK!");
            }
            
            psinsert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("insert tiempo_extra Error1: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }

        return objresultado;
    }
    
    /**
     * Retorna lista de tiempo extra de un empleado
     * 
     * @param _rutEmpleado
     * @param _todas
     * @return 
     */
    public List<TiempoExtraVO> getTiemposExtra(String _rutEmpleado,
            boolean _todas){
        
        List<TiempoExtraVO> lista = 
                new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        TiempoExtraVO data;
        
        try{
            String sql ="select "
                + "rut_empleado ,"
                + "to_char(fecha, 'yyyy-MM-dd') fecha_str,"
                + "tiempo_extra,"
                + "to_char(tiempo_extra, 'HH24') horas,"
                + "to_char(tiempo_extra, 'MI') minutos,"
                + "to_char(fechahora_ingreso, 'dd-MM-yyyy HH24:MI:SS') fecha_ingreso_str, "
                +" to_char(fechahora_actualizacion, 'dd-MM-yyyy HH24:MI:SS') fecha_actualizacion_str, "
                + "usuario_responsable,tipo "
                + "from tiempo_extra "
                + "where 1=1 ";
            
            if (!_todas){
                sql += "and fecha > current_date - 30";
            }
            if (_rutEmpleado != null && _rutEmpleado.compareTo("") != 0){        
                sql += " and rut_empleado = '" + _rutEmpleado + "'";
            }
            
            sql += " order by fecha desc"; 
            
            System.out.println(WEB_NAME+"[TiempoExtrarv."
                + "getTiemposExtra]"
                + "rut_empleado: "+_rutEmpleado);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TiempoExtraDAO.getTiemposExtra]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new TiempoExtraVO();
                data.setRut(rs.getString("rut_empleado"));
                data.setFechaAsStr(rs.getString("fecha_str"));
                data.setTiempoExtra(rs.getString("tiempo_extra"));
                data.setHoras(rs.getString("horas"));
                data.setMinutos(rs.getString("minutos"));
                data.setTipo(rs.getString("tipo"));
                String strHora    = data.getHoras();
                String strMins    = data.getMinutos();
                int intHora = -1;
                
                try{
                    intHora = Integer.parseInt(strHora);
                    strHora = "" + intHora;
                }catch(NumberFormatException nex){
                    System.err.println("[TiempoExtraDAO."
                        + "getTiemposExtra]Hra Inicio < 10: "+nex.toString());
                    strHora = strHora.substring(strHora.length()-1);
                }
                int intMins = -1;
                try{
                    intMins = Integer.parseInt(strMins);
                    strMins = "" + intMins;
                }catch(NumberFormatException nex){
                    System.err.println("[TiempoExtraDAO."
                        + "getTiemposExtra]Mins Inicio < 10: "+nex.toString());
                    strMins = strMins.substring(strMins.length()-1);
                }
                data.setHoras(strHora);
                data.setMinutos(strMins);                
                data.setFechaIngresoAsStr(rs.getString("tiempo_extra"));
                data.setFechaIngresoAsStr(rs.getString("fecha_ingreso_str"));
                data.setFechaActualizacionAsStr(rs.getString("fecha_actualizacion_str"));
                data.setUsuarioResponsable(rs.getString("usuario_responsable"));
                
                lista.add(data);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("Error: "+sqle.toString());
        }
        return lista;
    }
    
    /**
     * Retorna lista de tiempo extra de un empleado
     * 
     * @param _rutEmpleado
     * @param _fecha
     * @return 
     */
    public TiempoExtraVO getTiempoExtra(String _rutEmpleado,
            String _fecha){
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        TiempoExtraVO data = null;
        
        try{
            String sql ="select "
                + "rut_empleado ,"
                + "to_char(fecha, 'yyyy-MM-dd') fecha_str,"
                + "tiempo_extra,"
                + "to_char(tiempo_extra, 'HH24') horas,"
                + "to_char(tiempo_extra, 'MI') minutos,"
                + "to_char(fechahora_ingreso, 'dd-MM-yyyy HH24:MI:SS') fecha_ingreso_str, "
                +" to_char(fechahora_actualizacion, 'dd-MM-yyyy HH24:MI:SS') fecha_actualizacion_str, "
                + "usuario_responsable,tipo "
                + "from tiempo_extra "
                + "where 1=1 ";
            
            sql += "and fecha = '" + _fecha + "'";
            if (_rutEmpleado != null && _rutEmpleado.compareTo("") != 0){        
                sql += " and rut_empleado = '" + _rutEmpleado + "'";
            }
            
            System.out.println(WEB_NAME+"[TiempoExtrarv."
                + "getTiemposExtra]"
                + "rut_empleado: "+_rutEmpleado);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TiempoExtraDAO.getTiempoExtra]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                data = new TiempoExtraVO();
                data.setRut(rs.getString("rut_empleado"));
                data.setFechaAsStr(rs.getString("fecha_str"));
                data.setTiempoExtra(rs.getString("tiempo_extra"));
                data.setHoras(rs.getString("horas"));
                data.setMinutos(rs.getString("minutos"));
                data.setTipo(rs.getString("tipo"));
                String strHora    = data.getHoras();
                String strMins    = data.getMinutos();
                int intHora = -1;
                
                try{
                    intHora = Integer.parseInt(strHora);
                    strHora = "" + intHora;
                }catch(NumberFormatException nex){
                    System.err.println("[TiempoExtraDAO."
                        + "getTiemposExtra]Hra Inicio < 10: "+nex.toString());
                    strHora = strHora.substring(strHora.length()-1);
                }
                int intMins = -1;
                try{
                    intMins = Integer.parseInt(strMins);
                    strMins = "" + intMins;
                }catch(NumberFormatException nex){
                    System.err.println("[TiempoExtraDAO."
                        + "getTiemposExtra]Mins Inicio < 10: "+nex.toString());
                    strMins = strMins.substring(strMins.length()-1);
                }
                data.setHoras(strHora);
                data.setMinutos(strMins);                
                data.setFechaIngresoAsStr(rs.getString("tiempo_extra"));
                data.setFechaIngresoAsStr(rs.getString("fecha_ingreso_str"));
                data.setFechaActualizacionAsStr(rs.getString("fecha_actualizacion_str"));
                data.setUsuarioResponsable(rs.getString("usuario_responsable"));
                
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("Error: "+sqle.toString());
        }
        
        return data;
    }
    
    /**
     * Retorna lista de tipos de hras extras
     * 
     * @return 
     */
    public HashMap<String,Integer> getTiposHrasExtras(){
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        HashMap<String,Integer> tiposhash = 
            new HashMap<>();
        try{
            String sql = "SELECT "
                + "id, name, factor "
                + "FROM tiempo_extra_tipo "
                + "order by id";
                      
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TiempoExtraDAO.getTiposHrasExtras]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                tiposhash.put(rs.getString("id"), rs.getInt("factor"));
                
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("Error: "+sqle.toString());
        }
        
        return tiposhash;
    }
    
    /**
     * 
     * @param _rutEmpleado
     * @return 
     */
    public int getTiemposExtraCount(String _rutEmpleado){
        int count=0;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TiempoExtraDAO.getTiemposExtraCount]");
            Statement statement = dbConn.createStatement();
            String strSql ="SELECT count(rut_empleado) "
                + "FROM tiempo_extra where 1=1 ";
               
            if (_rutEmpleado != null && _rutEmpleado.compareTo("") != 0){        
                strSql += " and rut_empleado = '" + _rutEmpleado + "'";
            }
            ResultSet rs = statement.executeQuery(strSql);		
            if (rs.next()) {
                count=rs.getInt("count");
            }
            
            statement.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        } catch (SQLException|DatabaseException e) {
                e.printStackTrace();
        }
        
        return count;
    }
   
}
