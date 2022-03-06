/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.MarcaRechazoVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexander
 */
public class MarcasRechazosDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    //private Connection dbConn;
    //DbDirectConnection dbconnection = DbDirectConnection.getInstance();
    
    public MarcasRechazosDAO(PropertiesVO _propsValues) {
    }
    
    /**
     * Retorna lista con marcas rechazadas existentes
     * 
     * @param _empresaId
     * @param _rutEmpleado
     * @param _dispositivoId
     * @param _startDate
     * @param _endDate
     * @param _jtStartIndex
     * @param _jtPageSize
     * @param _jtSorting
     * @return 
     */
    public List<MarcaRechazoVO> getMarcasRechazadas(String _empresaId,
            String _rutEmpleado,
            String _dispositivoId,
            String _startDate, 
            String _endDate,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<MarcaRechazoVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        MarcaRechazoVO data;
        
        try{
            String sql = "SELECT correlativo_rechazo,"
                + "cod_dispositivo, "
                + "empresa_cod, "
                + "rut_empleado, "
                + "fecha_hora, "
                + "to_char(fecha_hora, 'TMDy dd/MM/yyyy HH24:mi:ss') fecha_hora_str,"
                + "cod_tipo_marca,"
                + "to_char(fecha_hora_actualizacion, 'yyyy-MM-dd HH24:MI:SS') fecha_actualiza_str, "
                + "id,"
                + "hashcode,"
                + "motivo_rechazo " 
            + "FROM marca_rechazo "
                    //+ "inner join empleado "
                    //+ "on (marca_rechazo.empresa_cod = empleado.empresa_id and marca_rechazo.rut_empleado = empleado.empl_rut) "
            + "where 1 = 1 ";
                //+ "and (marca_rechazo.fecha_hora::date >= empleado.empl_fec_ini_contrato) ";
            
            if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
                sql += " and empresa_cod = '" + _empresaId + "'";
            }
            
            if (_rutEmpleado != null && _rutEmpleado.compareTo("-1") != 0){        
                sql += " and rut_empleado = '" + _rutEmpleado + "'";
            }
            
            if (_dispositivoId != null && _dispositivoId.compareTo("-1") != 0){
                sql += " and cod_dispositivo = '" + _dispositivoId + "' ";
            }
            
            if (_startDate != null && _startDate.compareTo("") != 0){
                if (_endDate == null || _endDate.compareTo("") == 0){
                    _endDate = _startDate;
                }
                sql += " and fecha_hora::date "
                    + "between '" + _startDate + "' and '" + _endDate + "' ";
            }
            
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            System.out.println("[MarcasRechazosDAO.getMarcasRechazadas]Sql: "+sql);
            dbConn = dbLocator.getConnection(m_dbpoolName,"[MarcasRechazosDAO.getMarcasRechazadas]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new MarcaRechazoVO();
                data.setCodDispositivo(rs.getString("cod_dispositivo"));
                data.setEmpresaCod(rs.getString("empresa_cod"));
                data.setRutEmpleado(rs.getString("rut_empleado"));
                data.setFechaHora(rs.getString("fecha_hora"));
                data.setFechaHoraStr(rs.getString("fecha_hora_str"));
                data.setTipoMarca(rs.getInt("cod_tipo_marca"));
                data.setId(rs.getString("id"));
                data.setHashcode(rs.getString("hashcode"));
                data.setMotivoRechazo(rs.getString("motivo_rechazo"));
                data.setFechaHoraActualizacion(rs.getString("fecha_actualiza_str"));
              
                lista.add(data);
            }

            ps.close();
            rs.close();
            dbConn.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("Error: "+sqle.toString());
        }
        
        return lista;
    }
    
    /**
     * Retorna lista con marcas rechazadas existentes
     * 
     * @param _empresaId
     * @param _rutEmpleado
     * @param _dispositivoId
     * @param _startDate
     * @param _endDate
     * @param _jtStartIndex
     * @param _jtPageSize
     * @param _jtSorting
     * @return 
     */
    public List<MarcaRechazoVO> getMarcasRechazadasHist(String _empresaId,
            String _rutEmpleado,
            String _dispositivoId,
            String _startDate, 
            String _endDate,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<MarcaRechazoVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        MarcaRechazoVO data;
        
        try{
            String sql = "SELECT correlativo_rechazo,"
                + "cod_dispositivo, "
                + "empresa_cod, "
                + "rut_empleado, "
                + "fecha_hora, "
                + "to_char(fecha_hora, 'TMDy dd/MM/yyyy HH24:mi:ss') fecha_hora_str,"
                + "cod_tipo_marca,"
                + "to_char(fecha_hora_actualizacion, 'yyyy-MM-dd HH24:MI:SS') fecha_actualiza_str, "
                + "id,"
                + "hashcode,"
                + "motivo_rechazo " 
            + "FROM marca_rechazo_historica "
            + "where 1=1 ";
            
            if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
                sql += " and empresa_cod = '" + _empresaId + "'";
            }
            
            if (_rutEmpleado != null && _rutEmpleado.compareTo("-1") != 0){        
                sql += " and rut_empleado = '" + _rutEmpleado + "'";
            }
            
            if (_dispositivoId != null && _dispositivoId.compareTo("-1") != 0){
                sql += " and cod_dispositivo = '" + _dispositivoId + "' ";
            }
            
            if (_startDate != null && _startDate.compareTo("") != 0){
                if (_endDate == null || _endDate.compareTo("") == 0){
                    _endDate = _startDate;
                }
                sql += " and fecha_hora::date "
                    + "between '" + _startDate + "' and '" + _endDate + "' ";
            }
            
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            System.out.println("[MarcasRechazosDAO.getMarcasRechazadasHist]Sql: "+sql);
            dbConn = dbLocator.getConnection(m_dbpoolName,"[MarcasRechazosDAO.getMarcasRechazadasHist]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new MarcaRechazoVO();
                data.setCodDispositivo(rs.getString("cod_dispositivo"));
                data.setEmpresaCod(rs.getString("empresa_cod"));
                data.setRutEmpleado(rs.getString("rut_empleado"));
                data.setFechaHora(rs.getString("fecha_hora"));
                data.setFechaHoraStr(rs.getString("fecha_hora_str"));
                data.setTipoMarca(rs.getInt("cod_tipo_marca"));
                data.setId(rs.getString("id"));
                data.setHashcode(rs.getString("hashcode"));
                data.setMotivoRechazo(rs.getString("motivo_rechazo"));
                data.setFechaHoraActualizacion(rs.getString("fecha_actualiza_str"));
              
                lista.add(data);
            }

            ps.close();
            rs.close();
            dbConn.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("Error: "+sqle.toString());
        }
        
        return lista;
    }
    
    /**
     * 
     * @param _empresaId
     * @param _rutEmpleado
     * @param _dispositivoId
     * @param _startDate
     * @param _endDate
     * @return 
     */
    public int getMarcasRechazadasCount(String _empresaId,
            String _rutEmpleado, 
            String _dispositivoId,
            String _startDate, 
            String _endDate){
        int count=0;
        Statement statement = null;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[MarcasRechazosDAO.getMarcasRechazadasCount]");
            statement = dbConn.createStatement();
            String sql = "SELECT count(fecha_hora) "
                + "FROM marca_rechazo "
                + "WHERE (1 = 1) ";

            if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
                sql += " and empresa_cod = '" + _empresaId + "'";
            }
            
            if (_dispositivoId != null && _dispositivoId.compareTo("-1") != 0){
                sql += " and cod_dispositivo = '" + _dispositivoId + "' ";
            }
            
            if (_rutEmpleado != null && _rutEmpleado.compareTo("-1") != 0){        
                sql += " and marca_rechazo.rut_empleado = '" + _rutEmpleado + "'";
            }
            
            if (_startDate != null && _startDate.compareTo("") != 0){
                if (_endDate == null || _endDate.compareTo("") == 0){
                    _endDate = _startDate;
                }
                sql += " and marca_rechazo.fecha_hora::date "
                    + "between '" + _startDate + "' and '" + _endDate + "' ";
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
        }finally {
            try {
                statement.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[MaintenanceEventsDAO.addEvent]Error: "+ ex.toString());
            }
        }
        
        return count;
    }
    
    /**
     * 
     * @param _empresaId
     * @param _rutEmpleado
     * @param _dispositivoId
     * @param _startDate
     * @param _endDate
     * @return 
     */
    public int getMarcasRechazadasHistCount(String _empresaId,
            String _rutEmpleado,
            String _dispositivoId,
            String _startDate, 
            String _endDate){
        int count=0;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[MarcasRechazosDAO.getMarcasRechazadasHistCount]");
            Statement statement = dbConn.createStatement();
            String sql = "SELECT count(fecha_hora) "
                + "FROM marca_rechazo_historica marca_rechazo,empleado "
                + "WHERE (empleado.empl_rut = marca_rechazo.rut_empleado) ";

            if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
                sql += " and empleado.empresa_id = '" + _empresaId + "'";
            }
            
            if (_rutEmpleado != null && _rutEmpleado.compareTo("-1") != 0){        
                sql += " and marca_rechazo.rut_empleado = '" + _rutEmpleado + "'";
            }
            if (_dispositivoId != null && _dispositivoId.compareTo("-1") != 0){
                sql += " and cod_dispositivo = '" + _dispositivoId + "' ";
            }
            
            if (_startDate != null && _startDate.compareTo("") != 0){
                if (_endDate == null || _endDate.compareTo("") == 0){
                    _endDate = _startDate;
                }
                sql += " and marca_rechazo.fecha_hora::date "
                    + "between '" + _startDate + "' and '" + _endDate + "' ";
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
   
}
