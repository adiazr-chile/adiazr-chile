/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.EventoMantencionVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.SearchFilterVO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexander
 */
public class MaintenanceEventsDAO extends BaseDAO{

    //public boolean m_usedGlobalDbConnection = false;
    /**
     *
     */
    protected static Logger m_logger = Logger.getLogger("gestionweb");
    //private Connection dbConn;
    //private DatabaseLocator dbLoc;
    //DbDirectConnection dbconnection = DbDirectConnection.getInstance();

    public MaintenanceEventsDAO(PropertiesVO _propsValues) {
//        try{
//            dbLoc = DatabaseLocator.getInstance();
//            m_dbpoolName = _propsValues.getDbPoolName();
//        } catch (DatabaseException ex) {
//            m_logger.error("Error: "+ex.toString());
//        }
    }

    /**
     * Retorna lista de eventos de mantencion de tablas
     * @param _filter
     * @return 
     */
    public LinkedHashMap<String,MaintenanceEventVO> getEvents(SearchFilterVO _filter){
        LinkedHashMap<String,MaintenanceEventVO> lista = new LinkedHashMap<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            String strdate="";
            String f1 = "";
            String f2 = "";
            String sql = "SELECT username,"
                + "fecha_hora,"
                + "descripcion_evento,"
                + "to_char(fecha_hora, 'yyyy-MM-dd HH24:MI:SS') fecha_hora_str,"
                + "ip_usuario,"
                + "tipo_evento,"
                + "empresa_id,"
                + "depto_id,"
                + "cenco_id,"
                + "empleado_rut "    
                + "FROM mantencion_evento "
                + "where 1=1 ";
                    
            if (_filter.getUsername() != null && _filter.getUsername().compareTo("")!=0){
                sql+=" and username like '"+_filter.getUsername()+"%' ";
            }
            
            if (_filter.getEventType() != null && _filter.getEventType().compareTo("-1")!=0){
                if (_filter.getEventType().compareTo("USR")==0){
                    sql+=" and tipo_evento in ('USR','MOD','UP','ACC')";
                }else {
                    sql+=" and tipo_evento = '"+_filter.getEventType()+"' ";
                }
            }
                        
            f1 = _filter.getFechaInicial();
            f2 = _filter.getFechaFinal();
                
            if (_filter.getFechaFinal().compareTo("") == 0){
                //Solo fecha inicial
                f2 = f1;
            }else{
                sql +=" fecha_hora::date between '"+f1+"' AND '"+f2+"' ";
            }
            sql+= " order by fecha_hora,username";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[MaintenanceEventsDAO.getEvents]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                MaintenanceEventVO evento=new MaintenanceEventVO();
                strdate = rs.getString("fecha_hora_str");
                evento.setUsername(rs.getString("username"));
                evento.setDatetime(rs.getTimestamp("fecha_hora"));
                evento.setStrdate(strdate);
                evento.setDescription(rs.getString("descripcion_evento"));
                evento.setUserIP(rs.getString("ip_usuario"));
                evento.setType(rs.getString("tipo_evento"));
                
                evento.setEmpresaId(rs.getString("empresa_id"));
                evento.setDeptoId(rs.getString("depto_id"));
                evento.setCencoId(rs.getInt("cenco_id"));
                evento.setRutEmpleado(rs.getString("empleado_rut"));
                
                lista.put(strdate,evento);
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
     * Retorna lista de tipos de eventos disponibles
     * @return 
     */
    public LinkedHashMap<String,String> getEventTypes(){
        LinkedHashMap<String,String> listaTypes = new LinkedHashMap<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            String sql = "SELECT tipo_evento,nombre "
                + "FROM mantencion_tipo_evento "
                + "order by nombre";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[MaintenanceEventsDAO.getEventTypes]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                listaTypes.put(rs.getString("tipo_evento"),
                 rs.getString("nombre"));
            }

           ps.close();
           rs.close();
           dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("Error: "+sqle.toString());
        }
        
        return listaTypes;
    }
    
    /**
    * Inserta un registro de evento
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO addEvent(MaintenanceEventVO _eventdata){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        PreparedStatement ps = null;
        int result=0;
        String headerError = "Error al insertar evento ";
        String datosEvento = "[usuario,tipo,descripcion]="
            + "["+_eventdata.getUsername()
            + "," + _eventdata.getType()+","
            + _eventdata.getDescription()+"]";     
        
        System.out.println(WEB_NAME+"[MaintenanceEventsDAO.addEvent]"
            + "tipo_evento = " + _eventdata.getType()
            + ", descripcion: " + _eventdata.getDescription()
            + ", deptoId: " + _eventdata.getDeptoId());
        try{
            SimpleDateFormat sdf = 
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            String sql = "insert into mantencion_evento "
                + "(username, "
                    + "fecha_hora, "
                + "descripcion_evento, "
                + "ip_usuario, "
                + "tipo_evento, "
                + "empresa_id, "
                + "depto_id,"
                + "cenco_id,"
                + "empleado_rut,"
                + "empresa_id_source,"
                    + "operating_system,"
                    + "browser_name) " 
                + " values (?, current_timestamp,"
                    + "?,?,"
                    + "?,?,"
                    + "?,?,"
                    + "?,?,"
                    + "?,?)";
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[MaintenanceEventsDAO.addEvent]");
            ps = dbConn.prepareStatement(sql);
            ps.setString(1, _eventdata.getUsername());
            //ps.setTimestamp(2, new Timestamp(_eventdata.getDatetime().getTime()));
            ps.setString(2, _eventdata.getDescription());
            ps.setString(3, _eventdata.getUserIP());
            ps.setString(4, _eventdata.getType().trim());
            ps.setString(5, _eventdata.getEmpresaIdSource());
            ps.setString(6, _eventdata.getDeptoId());
            ps.setInt(7, _eventdata.getCencoId());
            ps.setString(8, _eventdata.getRutEmpleado());
            ps.setString(9, _eventdata.getEmpresaIdSource());
            
            ps.setString(10, _eventdata.getOperatingSystem());
            ps.setString(11, _eventdata.getBrowserName());
            
            ps.executeUpdate(); 
            
            ps.close();
            dbConn.close();
            dbLocator.freeConnection(dbConn);
            
        }catch(SQLException|DatabaseException sqle){
            System.out.println(WEB_NAME+"[MaintenanceEventsDAO.addEvent]Error: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(headerError + datosEvento+" :" + sqle.toString());
        }finally {
            try {
                ps.close();
                dbConn.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[MaintenanceEventsDAO.addEvent]Error: "+ ex.toString());
            }
        }
        return objresultado;
    }
    
    /**
    * Inserta un registro de evento
    * @param _eventdata
    * @return 
    */
    public ResultCRUDVO addLogAuditoria(MaintenanceEventVO _eventdata){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        PreparedStatement ps = null;
        int result=0;
        String headerError = "Error al insertar auditoria_log ";
        String datosEvento = "[usuario,tipo,descripcion]="
            + "["+_eventdata.getUsername()
            + "," + _eventdata.getType()+","
            + _eventdata.getDescription()+"]";     
        
        System.out.println(WEB_NAME+"[MaintenanceEventsDAO.addLogAuditoria]"
            + "tipo_evento = " + _eventdata.getType()
            + ", descripcion: " + _eventdata.getDescription()
            + ", deptoId: " + _eventdata.getDeptoId());
        try{
            SimpleDateFormat sdf = 
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            String sql = "insert into auditoria_logs "
                + "(username, "
                    + "fecha_hora, "
                + "descripcion_evento, "
                + "ip_usuario, "
                + "tipo_evento, "
                + "empresa_id, "
                + "depto_id,"
                + "cenco_id,"
                + "empleado_rut,"
                + "empresa_id_source,"
                    + "operating_system,"
                    + "browser_name) " 
                + " values (?, current_timestamp,"
                    + "?,?,"
                    + "?,?,"
                    + "?,?,"
                    + "?,?,"
                    + "?,?)";
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[MaintenanceEventsDAO.addLogAuditoria]");
            ps = dbConn.prepareStatement(sql);
            ps.setString(1, _eventdata.getUsername());
            ps.setString(2, _eventdata.getDescription());
            ps.setString(3, _eventdata.getUserIP());
            ps.setString(4, _eventdata.getType().trim());
            ps.setString(5, _eventdata.getEmpresaIdSource());
            ps.setString(6, _eventdata.getDeptoId());
            ps.setInt(7, _eventdata.getCencoId());
            ps.setString(8, _eventdata.getRutEmpleado());
            ps.setString(9, _eventdata.getEmpresaIdSource());
            ps.setString(10, _eventdata.getOperatingSystem());
            ps.setString(11, _eventdata.getBrowserName());
            ps.executeUpdate(); 
            
            ps.close();
            dbConn.close();
            dbLocator.freeConnection(dbConn);
            
        }catch(SQLException|DatabaseException sqle){
            System.out.println(WEB_NAME+"[MaintenanceEventsDAO.addLogAuditoria]"
                + "Error: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(headerError + datosEvento+" :" 
                + sqle.toString());
        }finally {
            try {
                ps.close();
                dbConn.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[MaintenanceEventsDAO.addLogAuditoria]"
                    + "Error: "+ ex.toString());
            }
        }
        return objresultado;
    }
    
    /**
     * Retorna lista con los eventos de mantencion
     * 
     * @param _username
     * @param _tipoEvento
     * @param _startDate
     * @param _endDate
     * @param _jtStartIndex
     * @param _jtPageSize
     * @param _jtSorting
     * @return 
     */
    public List<EventoMantencionVO> getEventosMantencion(String _username,
            String _tipoEvento, 
            String _startDate,
            String _endDate,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<EventoMantencionVO> lista = 
                new ArrayList<>();
        SimpleDateFormat fechahoraformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        PreparedStatement ps = null;
        ResultSet rs = null;
        EventoMantencionVO data;
        
        try{
            String sql = "SELECT "
                    + "eventos.username,"
                    + "eventos.fecha_hora,"
                    + "eventos.descripcion_evento,"
                    + "eventos.fecha_hora,"
                    + "to_char(eventos.fecha_hora, 'yyyy-MM-dd HH24:MI:SS') fecha_hora_str,"
                    + "eventos.ip_usuario,"
                    + "eventos.tipo_evento,"
                    + "eventos.empresa_id,"
                    + "eventos.depto_id,"
                    + "eventos.cenco_id,"
                    + "eventos.empleado_rut,"
                    + "eventos.operating_system,"
                    + "eventos.browser_name,"
                    + "tipo.nombre nombre_evento,"
                    + "empresa.empresa_nombre," 
                    + "departamento.depto_nombre," 
                    + "centro_costo.ccosto_nombre "
                + "FROM mantencion_evento eventos "
                + " inner join mantencion_tipo_evento tipo on eventos.tipo_evento = tipo.tipo_evento "
                + " left outer join empresa on (eventos.empresa_id = empresa.empresa_id) "
                + " left outer join departamento on (departamento.empresa_id = eventos.empresa_id and eventos.depto_id=departamento.depto_id) "
                + " left outer join centro_costo "
                + "on (centro_costo.depto_id = eventos.depto_id "
                + "and eventos.cenco_id=centro_costo.ccosto_id) "
                + "where 1 = 1 ";

            if (_username != null && _username.compareTo("") != 0){        
                sql += " and upper(username) like '"+_username.toUpperCase()+"%'";
            }
            if (_tipoEvento != null && _tipoEvento.compareTo("-1") != 0){        
                sql += " and eventos.tipo_evento = '" + _tipoEvento + "'";
            }
            if ( (_startDate != null && _startDate.compareTo("") != 0) ){        
                sql += " and eventos.fecha_hora::date "
                    + "between '"+_startDate+"' and '"+_endDate+"'";
            }
            
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
           
            dbConn = dbLocator.getConnection(m_dbpoolName,"[MaintenanceEventsDAO.getEventosMantencion]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new EventoMantencionVO();
                data.setUsername(rs.getString("username"));
                data.setFechaHora(rs.getDate("fecha_hora"));
                if (data.getFechaHora() != null){
                    data.setFechaHoraAsStr(rs.getString("fecha_hora_str"));
                }
                data.setDescripcion(rs.getString("descripcion_evento"));
                data.setIp(rs.getString("ip_usuario"));
                data.setTipoEventoId(rs.getString("tipo_evento"));
                data.setTipoEventoNombre(rs.getString("nombre_evento"));
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setDeptoId(rs.getString("depto_id"));
                data.setCencoId(rs.getInt("cenco_id"));
                data.setRutEmpleado(rs.getString("empleado_rut"));
                
                data.setEmpresaNombre(rs.getString("empresa_nombre"));
                data.setDeptoNombre(rs.getString("depto_nombre"));
                data.setCencoNombre(rs.getString("ccosto_nombre"));
                
                data.setOperatingSystem(rs.getString("operating_system"));
                data.setBrowserName(rs.getString("browser_name"));
                
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
     * Retorna lista con los eventos de mantencion
     * 
     * @param _username
     * @param _tipoEvento
     * @param _startDate
     * @param _endDate
     * @param _jtStartIndex
     * @param _jtPageSize
     * @param _jtSorting
     * @return 
     */
    public List<EventoMantencionVO> getEventosMantencionHist(String _username,
            String _tipoEvento, 
            String _startDate,
            String _endDate,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<EventoMantencionVO> lista = 
                new ArrayList<>();
        SimpleDateFormat fechahoraformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        PreparedStatement ps = null;
        ResultSet rs = null;
        EventoMantencionVO data;
        
        try{
            String sql = "SELECT "
                + "eventos.username,"
                + "eventos.fecha_hora,"
                + "eventos.descripcion_evento,"
                + "eventos.fecha_hora,"
                + "to_char(eventos.fecha_hora, 'yyyy-MM-dd HH24:MI:SS') fecha_hora_str,"
                + "eventos.ip_usuario,"
                + "eventos.tipo_evento,"
                + "eventos.empresa_id,"
                + "eventos.depto_id,"
                + "eventos.cenco_id,"
                + "eventos.empleado_rut,"
                + "tipo.nombre nombre_evento,"
                + "empresa.empresa_nombre," 
                + "departamento.depto_nombre," 
                + "centro_costo.ccosto_nombre "
                + "FROM mantencion_evento_historico eventos "
                + " inner join mantencion_tipo_evento tipo on eventos.tipo_evento = tipo.tipo_evento "
                + " left outer join empresa on (eventos.empresa_id = empresa.empresa_id) "
                + " left outer join departamento on (departamento.empresa_id = eventos.empresa_id and eventos.depto_id=departamento.depto_id) "
                + " left outer join centro_costo "
                + "on (centro_costo.depto_id = eventos.depto_id "
                + "and eventos.cenco_id=centro_costo.ccosto_id) "
                + "where 1 = 1 ";

            if (_username != null && _username.compareTo("") != 0){        
                sql += " and upper(username) like '"+_username.toUpperCase()+"%'";
            }
            if (_tipoEvento != null && _tipoEvento.compareTo("-1") != 0){        
                sql += " and eventos.tipo_evento = '" + _tipoEvento + "'";
            }
            if ( (_startDate != null && _startDate.compareTo("") != 0) ){        
                sql += " and eventos.fecha_hora::date "
                    + "between '"+_startDate+"' and '"+_endDate+"'";
            }
            
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
           
            dbConn = dbLocator.getConnection(m_dbpoolName,"[MaintenanceEventsDAO.getEventosMantencionHist]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new EventoMantencionVO();
                data.setUsername(rs.getString("username"));
                data.setFechaHora(rs.getDate("fecha_hora"));
                if (data.getFechaHora() != null){
                    data.setFechaHoraAsStr(rs.getString("fecha_hora_str"));
                }
                data.setDescripcion(rs.getString("descripcion_evento"));
                data.setIp(rs.getString("ip_usuario"));
                data.setTipoEventoId(rs.getString("tipo_evento"));
                data.setTipoEventoNombre(rs.getString("nombre_evento"));
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setDeptoId(rs.getString("depto_id"));
                data.setCencoId(rs.getInt("cenco_id"));
                data.setRutEmpleado(rs.getString("empleado_rut"));
                
                data.setEmpresaNombre(rs.getString("empresa_nombre"));
                data.setDeptoNombre(rs.getString("depto_nombre"));
                data.setCencoNombre(rs.getString("ccosto_nombre"));
                
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
     * Num registros eventos de mantencion
     * 
     * @param _username
     * @param _tipoEvento
     * @param _startDate
     * @param _endDate
     * @return 
     */
    public int getEventosMantencionCount(String _username,
            String _tipoEvento, 
            String _startDate,
            String _endDate){
        int count=0;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[MaintenanceEventsDAO.getEventosMantencionCount]");
            Statement statement = dbConn.createStatement();
            String sql = "SELECT count(*) as count "
                    + "FROM mantencion_evento where 1=1 ";
            
            if (_username != null && _username.compareTo("") != 0){        
                sql += " and upper(username) like '"+_username.toUpperCase()+"%'";
            }
            if (_tipoEvento != null && _tipoEvento.compareTo("-1") != 0){        
                sql += " and tipo_evento = '" + _tipoEvento + "'";
            }
            if ( (_startDate != null && _startDate.compareTo("") != 0) ){        
                sql += " and fecha_hora::date "
                    + "between '"+_startDate+"' and '"+_endDate+"'";
            }
           
            ResultSet rs = statement.executeQuery(sql);		
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
    
    /**
     * Num registros eventos de mantencion historicos
     * 
     * @param _username
     * @param _tipoEvento
     * @param _startDate
     * @param _endDate
     * @return 
     */
    public int getEventosMantencionHistCount(String _username,
            String _tipoEvento, 
            String _startDate,
            String _endDate){
        int count=0;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[MaintenanceEventsDAO.getEventosMantencionHistCount]");
            Statement statement = dbConn.createStatement();
            String sql = "SELECT count(*) as count "
                    + "FROM mantencion_evento_historico where 1=1 ";
            
            if (_username != null && _username.compareTo("") != 0){        
                sql += " and upper(username) like '"+_username.toUpperCase()+"%'";
            }
            if (_tipoEvento != null && _tipoEvento.compareTo("-1") != 0){        
                sql += " and tipo_evento = '" + _tipoEvento + "'";
            }
            if ( (_startDate != null && _startDate.compareTo("") != 0) ){        
                sql += " and fecha_hora::date "
                    + "between '"+_startDate+"' and '"+_endDate+"'";
            }
           
            ResultSet rs = statement.executeQuery(sql);		
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
    
////    public void openDbConnection(){
////        try {
////            m_usedGlobalDbConnection = true;
////            dbConn = dbLocator.getConnection(m_dbpoolName,"[MaintenanceEventsDAO.openDbConnection]");
////        } catch (DatabaseException ex) {
////            System.err.println("[UsersDAO.openDbConnection]"
////                + "Error: " + ex.toString());
////        }
////    }
////    
////    public void closeDbConnection(){
////        try {
////            m_usedGlobalDbConnection = false;
////            dbLocator.freeConnection(dbConn);
////        } catch (Exception ex) {
////            System.err.println("[MaintenanceEventsDAO.closeDbConnection]"
////                + "Error: "+ex.toString());
////        }
////    }
}
