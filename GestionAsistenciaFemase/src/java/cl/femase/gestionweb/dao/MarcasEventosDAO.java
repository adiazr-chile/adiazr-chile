/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.MarcasEventosVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.sql.Connection;
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
public class MarcasEventosDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    //private Connection dbConn;
    //DbDirectConnection dbconnection = DbDirectConnection.getInstance();
    
    public MarcasEventosDAO(PropertiesVO _propsValues) {
//        try {
//            dbLoc = DatabaseLocator.getInstance();
//            m_dbpoolName = _propsValues.getDbPoolName();
//        } catch (DatabaseException ex) {
//            m_logger.error("DbError: "+ex.toString());
//        }
    }
    
      
     /**
     * Retorna lista con las marcas modificadas
     * 
     * @param _empresaId
     * @param _deptoId
     * @param _cencoId
     * @param _codDispositivo
     * @param _jtStartIndex
     * @param _rutEmpleado
     * @param _startDate
     * @param _jtPageSize
     * @param _endDate
     * @param _jtSorting
     * @return 
     */
    public List<MarcasEventosVO> getMarcasEventos(String _empresaId,
            String _deptoId,
            int _cencoId,
            String _codDispositivo,
            String _rutEmpleado,
            String _startDate, 
            String _endDate,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<MarcasEventosVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        MarcasEventosVO data;
        
        try{
            String sql = "select "
                + "correlativo,"
                + "cod_disp,"
                + "emp_cod,"
                + "rut_empleado,"
                + "id,"
                + "hashcode,"
                + "to_char(fecha_hora_modificacion, 'TMDy dd/MM/yyyy HH24:mi:ss') fecha_hora_modificacion,"
                + "cod_usuario,"
                + "fecha_hora_orig,"
                + "tipo_marca_orig,"
                + "comentario_orig,"
                + "fecha_hora_new,"
                + "tipo_marca_new,"
                + "comentario_new, "
                + "tipo_evento "
                + "from marcas_eventos "
                + "where 1=1 ";
            
            if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
                sql += " and emp_cod = '" + _empresaId + "'";
            }
            
            if (_rutEmpleado != null && _rutEmpleado.compareTo("-1") != 0){        
                sql += " and rut_empleado = '" + _rutEmpleado + "'";
            }
            
            if (_startDate != null && _startDate.compareTo("") != 0){
                if (_endDate == null || _endDate.compareTo("") == 0){
                    _endDate = _startDate;
                }
                sql += " and fecha_hora_modificacion::date "
                    + "between '" + _startDate + "' and '" + _endDate + "' ";
            }
           
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            System.out.println(WEB_NAME+"[MarcaModificadaDAO.getMarcasEventos]sql: "+sql);
                        
            dbConn = dbLocator.getConnection(m_dbpoolName,"[MarcasEventosDAO.getMarcasEventos]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new MarcasEventosVO();
              
                data.setCorrelativo(rs.getInt("correlativo"));
                data.setCodDispositivo(rs.getString("cod_disp"));
                data.setEmpresaCod(rs.getString("emp_cod"));
                data.setRutEmpleado(rs.getString("rut_empleado"));
                data.setId(rs.getString("id"));
                data.setHashcode(rs.getString("hashcode"));
                data.setFechaHoraModificacion(rs.getString("fecha_hora_modificacion"));
                data.setCodUsuario(rs.getString("cod_usuario"));
  
                data.setFechaHoraOriginal(rs.getString("fecha_hora_orig"));
                data.setTipoMarcaOriginal(rs.getInt("tipo_marca_orig"));
                data.setComentarioOriginal(rs.getString("comentario_orig"));
                
                data.setFechaHoraNew(rs.getString("fecha_hora_new"));
                data.setTipoMarcaNew(rs.getInt("tipo_marca_new"));
                data.setComentarioNew(rs.getString("comentario_new"));
                data.setTipoEvento(rs.getString("tipo_evento"));
                
                lista.add(data);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[Error "
                + "getMarcasEventos]: "+sqle.toString());
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
     * Retorna lista con los eventos realizados sobre una marca 
     * 
     * @param _empresaId
     * @param _fechaMarca
     * @param _jtStartIndex
     * @param _tipoMarca
     * @param _rutEmpleado
     * @param _jtPageSize
     * @param _jtSorting
     * @return 
     */
    public List<MarcasEventosVO> getEventos(String _empresaId,
            String _rutEmpleado,
            String _fechaMarca,
            int _tipoMarca,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<MarcasEventosVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        MarcasEventosVO data;
        
        try{
            String sql = "select "
                    + "correlativo,"
                    + "cod_disp,emp_cod,"
                    + "rut_empleado,"
                    + "id,hashcode,"
                    + "to_char(fecha_hora_modificacion, 'TMDy dd/MM/yyyy HH24:mi:ss') fecha_hora_modificacion,"
                    + "cod_usuario,"
                    + "fecha_hora_orig,"
                    + "tipo_marca_orig,"
                    + "comentario_orig,"
                    + "fecha_hora_new,"
                    + "tipo_marca_new,"
                    + "comentario_new, "
                    + "tipo_evento "
                + "from marcas_eventos "
                + "where emp_cod = '" + _empresaId + "' "
                + "and rut_empleado = '" + _rutEmpleado + "' "
                + "and (fecha_hora_new::date='" + _fechaMarca + "' or fecha_hora_orig::date='" + _fechaMarca + "') "
                + "and (tipo_marca_new=" + _tipoMarca + " or tipo_marca_orig=" + _fechaMarca + ") "    
                + "and tipo_evento = 'M' ";
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            System.out.println(WEB_NAME+"[MarcaModificadaDAO.getEventos]sql: "+sql);
                        
            dbConn = dbLocator.getConnection(m_dbpoolName,"[MarcasEventosDAO.getMarcasEventos]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new MarcasEventosVO();
              
                data.setCorrelativo(rs.getInt("correlativo"));
                data.setCodDispositivo(rs.getString("cod_disp"));
                data.setEmpresaCod(rs.getString("emp_cod"));
                data.setRutEmpleado(rs.getString("rut_empleado"));
                data.setId(rs.getString("id"));
                data.setHashcode(rs.getString("hashcode"));
                data.setFechaHoraModificacion(rs.getString("fecha_hora_modificacion"));
                data.setCodUsuario(rs.getString("cod_usuario"));
  
                data.setFechaHoraOriginal(rs.getString("fecha_hora_orig"));
                data.setTipoMarcaOriginal(rs.getInt("tipo_marca_orig"));
                data.setComentarioOriginal(rs.getString("comentario_orig"));
                
                data.setFechaHoraNew(rs.getString("fecha_hora_new"));
                data.setTipoMarcaNew(rs.getInt("tipo_marca_new"));
                data.setComentarioNew(rs.getString("comentario_new"));
                data.setTipoEvento(rs.getString("tipo_evento"));
                
                lista.add(data);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[Error "
                + "getEventos]: "+sqle.toString());
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
     * @param _empresaId
     * @param _deptoId
     * @param _cencoId
     * @param _codDispositivo
     * @param _rutEmpleado
     * @param _startDate
     * @param _endDate
     * @return 
     */
    public int getMarcasEventosCount(String _empresaId,
            String _deptoId,
            int _cencoId,
            String _codDispositivo,
            String _rutEmpleado, 
            String _startDate, 
            String _endDate){
        int count=0;
        Statement statement = null;
        ResultSet rs = null;
        
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[MarcasEventosDAO.getMarcasEventosCount]");
            statement = dbConn.createStatement();
            String sql = "SELECT count(correlativo) "
                + "FROM marcas_eventos "
                + "WHERE 1 = 1 ";

            if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
                sql += " and emp_cod = '" + _empresaId + "'";
            }
            
            if (_rutEmpleado != null && _rutEmpleado.compareTo("-1") != 0){        
                sql += " and rut_empleado = '" + _rutEmpleado + "'";
            }
            
            if (_startDate != null && _startDate.compareTo("") != 0){
                if (_endDate == null || _endDate.compareTo("") == 0){
                    _endDate = _startDate;
                }
                sql += " and fecha_hora_modificacion::date "
                    + "between '" + _startDate + "' and '" + _endDate + "' ";
            }
            
            rs = statement.executeQuery(sql);		
            if (rs.next()) {
                count=rs.getInt("count");
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
    
    /**
     *
     * @param _empresaId
     * @param _rutEmpleado
     * @param _fechaMarca
     * @param _tipoMarca
     * @return
     */
    public int getEventosCount(String _empresaId,
            String _rutEmpleado, 
            String _fechaMarca,
            int _tipoMarca){
        int count=0;
        Statement statement = null;
        ResultSet rs = null;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[MarcasEventosDAO.getEventosCount]");
            statement = dbConn.createStatement();
            String sql = "SELECT count(correlativo) "
                + "FROM marcas_eventos "
                + "where emp_cod = '" + _empresaId + "' "
                + "and rut_empleado = '" + _rutEmpleado + "' "
                + "and (fecha_hora_new::date='" + _fechaMarca + "' or fecha_hora_orig::date='" + _fechaMarca + "') "
                + "and (tipo_marca_new=" + _tipoMarca + " or tipo_marca_orig=" + _fechaMarca + ") "    
                + "and tipo_evento = 'M' ";
            
            rs = statement.executeQuery(sql);		
            if (rs.next()) {
                count=rs.getInt("count");
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
    
    /**
    * Agrega una marca modificada
    * @param _data
    * @return 
    */
     public MaintenanceVO insert(MarcasEventosVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al insertar "
            + "marca evento. "
            + "CodDispositivo: " + _data.getCodDispositivo()
            + ",codEmpresa: " + _data.getEmpresaCod()
            + ",rutEmpleado: " + _data.getRutEmpleado()
            + ",tipoEvento: " + _data.getTipoEvento()
            + ",fechaHoraOriginal: " + _data.getFechaHoraOriginal()
            + ",fechaHoraNew: " + _data.getFechaHoraNew()    
            + ",tipoMarcaOriginal: " + _data.getTipoMarcaOriginal()
            + ",tipoMarcaNew: " + _data.getTipoMarcaNew();
        
       String msgFinal = " Inserta marcas_eventos:"
            + "dispositivoId [" + _data.getCodDispositivo() + "]"
            + ", empresaId [" + _data.getEmpresaCod() + "]"
            + ", rutEmpleado [" + _data.getRutEmpleado() + "]"
            + ", tipoEvento [" + _data.getTipoEvento() + "]"
            + ", fechaHoraOriginal [" + _data.getFechaHoraOriginal() + "]"
            + ", fechaHoraNew [" + _data.getFechaHoraNew() + "]"
            + ", tipoMarcaOriginal [" + _data.getTipoMarcaOriginal() + "]"
            + ", tipoMarcaNew [" + _data.getTipoMarcaNew() + "]";
        
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql="INSERT INTO marcas_eventos("
                + "correlativo, "
                + "cod_disp, "          // 1
                + "emp_cod, "           // 2
                + "rut_empleado, "      // 3
                + "id, "                // 4
                + "hashcode, "          // 5
                + "fecha_hora_modificacion,"
                + "cod_usuario, "       // 6
                + "fecha_hora_orig, "
                + "tipo_marca_orig, "   // 7
                + "comentario_orig,"    // 8
                + "fecha_hora_new, "
                + "tipo_marca_new, "    // 9
                + "comentario_new, "    // 10
                + "tipo_evento) "       // 11
                + " VALUES (nextval('corr_marcas_eventos_seq'), "
                    + "?, "
                    + "?, "
                    + "?, "
                    + "?, "
                    + "?, "
                    + "current_timestamp,"
                    + "?, "
                    + "'"+_data.getFechaHoraOriginal()+"', "
                    + "?, "
                    + "?, "
                    + "'"+_data.getFechaHoraNew()+"', "
                    + "?, "
                    + "?, "
                    + "?)";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[MarcasEventosDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            
            insert.setString(1,  _data.getCodDispositivo());
            insert.setString(2,  _data.getEmpresaCod());
            insert.setString(3,  _data.getRutEmpleado());
            insert.setString(4,  _data.getId());
            insert.setString(5,  _data.getHashcode());
            insert.setString(6,  _data.getCodUsuario());
            
            insert.setInt(7,  _data.getTipoMarcaOriginal());
            insert.setString(8,  _data.getComentarioOriginal());
            
            insert.setInt(9,  _data.getTipoMarcaNew());
            insert.setString(10,  _data.getComentarioNew());
            insert.setString(11,  _data.getTipoEvento());
            
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println(WEB_NAME+"[insert marcas_eventos]"
                    +" insertada OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("insert marcas_eventos Error1: "+sqle.toString());
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
    
}
