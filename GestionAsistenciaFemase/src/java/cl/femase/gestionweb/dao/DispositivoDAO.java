/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.DispositivoCentroCostoVO;
import cl.femase.gestionweb.vo.DispositivoDepartamentoVO;
import cl.femase.gestionweb.vo.DispositivoEmpresaVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.DispositivoVO;
import cl.femase.gestionweb.vo.PropertiesVO;
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
public class DispositivoDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    
    public boolean m_usedGlobalDbConnection = false;
                
    /**
     *
     * @param _propsValues
     */
    public DispositivoDAO(PropertiesVO _propsValues) {
    
    }

    /**
    * Actualiza el estado de un dispositivo
    * @param _idDispositivo
    * @param _estado
    * @return 
    */
    public MaintenanceVO updateEstadoDispositivo(String _idDispositivo, 
            int _estado){
        MaintenanceVO objresultado = new MaintenanceVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "estado de Dispositivo, "
            + "movil_id: " + _idDispositivo
            + ", estado: " + _estado;
        
        try{
            String msgFinal = " Actualiza estado de Dispositivo:"
                + "movil_id [" + _idDispositivo + "]" 
                + ", estado [" + _estado + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE dispositivo "
                + " SET estado = ? "
                + " WHERE device_id = ?";

            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[DispositivoDAO.updateEstadoDispositivo]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setInt(1,  _estado);
            psupdate.setString(2,  _idDispositivo);
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println("[DispositivoDAO."
                    + "updateEstadoDispositivo]update Dispositivo."
                    + " Id:" + _idDispositivo
                    + ", estado:" + _estado   
                    +" actualizado OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[DispositivoDAO.updateEstadoDispositivo]"
                + "Error1: " + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError 
                + " :" + sqle.toString());
        }finally{
            try {
                if (psupdate != null) psupdate.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[DispositivoDAO.updateEstadoDispositivo]"
                    + "Error2: " + ex.toString());
            }
        }

        return objresultado;
    }
    
    /**
     * Actualiza un dispositivo
     * @param _data
     * @return 
     */
    public MaintenanceVO update(DispositivoVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "dispositivo. "
            + " id: "+_data.getId()
            + " ,tipo: "+_data.getIdTipo()
            + " ,modelo: "+_data.getModelo()
            + " ,fabricante: "+_data.getFabricante()
            + " ,idComuna: "+_data.getIdComuna()
            + " ,direccion: "+_data.getDireccion();
        
        String msgFinal = " Actualiza dispositivo:"
            + "id [" + _data.getId() + "]"
            + "tipo [" + _data.getIdTipo() + "]"
            + " modelo [" + _data.getModelo() + "]"
            + " fabricante [" + _data.getFabricante() + "]"
            + " idComuna [" + _data.getIdComuna() + "]"
            + " direccion [" + _data.getDireccion() + "]";
        
        try{
            objresultado.setMsg(msgFinal);
            String sql = "UPDATE dispositivo "
                    + " SET type_id=?, "
                        + "estado=?,"
                        + "ubicacion=?,"
                        + "fecha_ultima_actualizacion = current_timestamp,"
                        + "modelo = ?,"
                        + "fabricante = ?,"
                        + "firmware_version = ?,"
                        + "direccion_ip = ?,"
                        + "gateway = ?,"
                        + "dns = ?,"
                        + "comuna_id = ?, "
                        + "direccion = ? "
                    + " WHERE device_id=?";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[DispositivoDAO.update]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setInt(1,  _data.getIdTipo());
            psupdate.setInt(2,  _data.getEstado());
            psupdate.setString(3,  _data.getUbicacion());
            
            psupdate.setString(4,  _data.getModelo());
            psupdate.setString(5,  _data.getFabricante());
            psupdate.setString(6,  _data.getFirmwareVersion());
            psupdate.setString(7,  _data.getIp());
            psupdate.setString(8,  _data.getGateway());
            psupdate.setString(9,  _data.getDns());
            
            psupdate.setInt(10,  _data.getIdComuna());
            psupdate.setString(11,  _data.getDireccion());
            
            psupdate.setString(12,  _data.getId());
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println("[DispositivoDAO.update]"
                    + ", id:" +_data.getId()
                    + ", tipo:" +_data.getIdTipo()
                    +" actualizado OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[DispositivoDAO.update]"
                + "update dispositivo Error_1: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (psupdate != null) psupdate.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[DispositivoDAO.update]"
                    + "Error: "+ex.toString());
            }
        }

        return objresultado;
    }

     /**
     * Agrega un nuevo dispositivo
     * @param _data
     * @return 
     */
    public MaintenanceVO insert(DispositivoVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al insertar "
            + "dispositivo. "
            + " id: " + _data.getId()
            + " ,tipo: " + _data.getIdTipo()
            + " ,modelo: " + _data.getModelo()
            + " ,fabricante: " + _data.getFabricante()
            + " ,firmware: " + _data.getFirmwareVersion()
            + " ,idComuna: " + _data.getIdComuna()    
            + " ,direccion: " + _data.getDireccion();
        
       String msgFinal = " Inserta dispositivo:"
            + "id [" + _data.getId() + "]"
            + ",tipo [" + _data.getIdTipo() + "]"
            + ",modelo [" + _data.getModelo() + "]"
            + ",fabricante [" + _data.getFabricante() + "]"
            + ",idComuna [" + _data.getIdComuna() + "]"   
            + ",direccion [" + _data.getDireccion() + "]";
            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO dispositivo("
                    + "  device_id, "
                    + "type_id, "
                    + "estado, "
                    + "fecha_ingreso,"
                    + "ubicacion,"
                    + "fecha_ultima_actualizacion,"
                    + "modelo, "
                    + "fabricante, "
                    + "firmware_version, "
                    + "direccion_ip, "
                    + "gateway, "
                    + "dns,"
                    + "comuna_id,"
                    + "direccion) "
                + " VALUES (?, ?, ?, "
                    + "current_date, ?,"
                    + "current_timestamp,"
                    + "?, ?, ?, ?, ?, ?, ?, ?)";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[DispositivoDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getId());
            insert.setInt(2,  _data.getIdTipo());
            insert.setInt(3,  _data.getEstado());
            insert.setString(4,  _data.getUbicacion());
            insert.setString(5,  _data.getModelo());
            insert.setString(6,  _data.getFabricante());
            insert.setString(7,  _data.getFirmwareVersion());
            insert.setString(8,  _data.getIp());
            insert.setString(9,  _data.getGateway());
            insert.setString(10,  _data.getDns());
            insert.setInt(11,  _data.getIdComuna());
            insert.setString(12,  _data.getDireccion());
            
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println("[DispositivoDAO.insert]"
                    + ", Id:" +_data.getId()
                    + ", tipo:" +_data.getIdTipo()
                    + ", modelo:" +_data.getModelo()    
                    + ", fabricante:" +_data.getFabricante()        
                    +" insertado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[DispositivoDAO."
                + "insert]Error_1: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (insert != null) insert.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[DispositivoDAO."
                    + "insert]Error_2: "+ex.toString());
            }
        }
        return objresultado;
    }
    
    /**
     * Retorna lista con los dispositivos existentes y su 
     * correspondiente asignacion a empresas, deptos y centros de costo
     * 
     * @param _id
     * @param _tipo
     * @param _jtStartIndex
     * @param _fechaIngreso
     * @param _estado
     * @param _jtPageSize
     * @param _jtSorting
     * @return 
     */
    public List<DispositivoVO> getDispositivos(String _id,
            int _tipo,
            int _estado,
            String _fechaIngreso,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<DispositivoVO> lista = new ArrayList<>();
        SimpleDateFormat fechahoraformat = new SimpleDateFormat("dd-MM-yyyy");
        PreparedStatement ps = null;
        ResultSet rs = null;
        DispositivoVO data;
        
        try{
            String sql = "SELECT "
                    + " dispositivo.device_id,"
                    + " dispositivo.type_id,"
                    + " tipo_dispositivo.dev_type_name,"
                    + "dispositivo.estado,estado.estado_nombre,"
                    + "dispositivo.fecha_ingreso, "
                    + "to_char(dispositivo.fecha_ingreso, 'yyyy-MM-dd') fecha_ingreso_str,"
                    + "dispositivo.ubicacion,"
                    + "dispositivo.fecha_ultima_actualizacion,"
                    + "to_char(dispositivo.fecha_ultima_actualizacion, 'yyyy-MM-dd HH24:MI:SS') fecha_ultima_actualizacion_str,"
                    + "modelo,"
                    + "fabricante,"
                    + "firmware_version,"
                    + "direccion_ip,"
                    + "gateway,"
                    + "dns,"
                    + "coalesce(comuna_id,-1) comuna_id,"
                    + "direccion "
                + " FROM "
                    + " dispositivo "
                    + "inner join tipo_dispositivo on (dispositivo.type_id = tipo_dispositivo.dev_type_id) "
                    + "inner join estado on (dispositivo.estado = estado.estado_id)"
                + " where 1 = 1 ";
            
            if ( _id != null && _id.compareTo("") != 0){        
                sql += " and dispositivo.device_id = '" + _id + "'";
            }
            if (_tipo != -1){        
                sql += " and type_id = "+_tipo;
            }
            if (_estado != -1){        
                sql += " and dispositivo.estado = " + _estado;
            }
            
            if ( _fechaIngreso != null && _fechaIngreso.compareTo("") != 0){        
                sql += " and fecha_ingreso = '" + _fechaIngreso + "'";
            }
            
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            System.out.println("[DispositivoDAO.getDispositivos]sql: " + sql);
            if (!m_usedGlobalDbConnection) dbConn = dbLocator.getConnection(m_dbpoolName,"[DispositivoDAO.getDispositivos]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new DispositivoVO();
                data.setId(rs.getString("device_id"));
                data.setIdTipo(rs.getInt("type_id"));
                data.setFechaIngreso(rs.getDate("fecha_ingreso"));
                if (data.getFechaIngreso() != null){
                    data.setFechaIngresoAsStr(rs.getString("fecha_ingreso_str"));
                }
                data.setFechaHoraUltimaActualizacionAsStr(rs.getString("fecha_ultima_actualizacion_str"));
                data.setEstado(rs.getInt("estado"));
                data.setUbicacion(rs.getString("ubicacion"));
                data.setNombreEstado(rs.getString("estado_nombre"));        
                data.setNombreTipo(rs.getString("dev_type_name"));                
                
                data.setModelo(rs.getString("modelo"));
                data.setFabricante(rs.getString("fabricante"));
                data.setFirmwareVersion(rs.getString("firmware_version"));
                data.setIp(rs.getString("direccion_ip"));
                data.setGateway(rs.getString("gateway"));
                data.setDns(rs.getString("dns"));
                data.setIdComuna(rs.getInt("comuna_id"));
                data.setDireccion(rs.getString("direccion"));
                
                //setear listado de empresas, departamentos y cencos asignados
                data.setEmpresas(getEmpresasAsignadas(data.getId()));
                data.setDepartamentos(getDepartamentosAsignados(data.getId()));
                data.setCencos(getCentrosCostoAsignados(data.getId()));
                lista.add(data);
            }

            ps.close();
            rs.close();
            
            if (!m_usedGlobalDbConnection){
            dbConn.close();
            dbLocator.freeConnection(dbConn);
            }
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("Error: "+sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbConn.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
        
        return lista;
    }
    
    /**
     * 
     * @param _tipo
     * @param _estado
     * @param _fechaIngreso
     * @return 
     */
    public int getDispositivosCount(int _tipo,
            int _estado,
            String _fechaIngreso){
        int count=0;
        ResultSet rs        = null;
        Statement statement = null;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[DispositivoDAO.getDispositivosCount]");
            statement = dbConn.createStatement();
            String sql ="SELECT count(device_id) "
                + "FROM dispositivo where 1=1 ";
               
            if (_tipo != -1){        
                sql += " and type_id = "+_tipo;
            }
           
            if (_estado != -1){        
                sql += " and dispositivo.estado = " + _estado;
            }
            
            if ( _fechaIngreso != null && _fechaIngreso.compareTo("") != 0){        
                sql += " and fecha_ingreso = '" + _fechaIngreso + "'";
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
   
    private LinkedHashMap<String,DispositivoEmpresaVO> getEmpresasAsignadas(String _deviceId){
        LinkedHashMap<String,DispositivoEmpresaVO> listado = new LinkedHashMap<>();
        Statement statement = null;
        ResultSet rs        = null;
        DispositivoEmpresaVO data=new DispositivoEmpresaVO(null,null);
        try {
            if (!m_usedGlobalDbConnection) dbConn = dbLocator.getConnection(m_dbpoolName,"[DispositivoDAO.getEmpresasAsignadas]");
            statement = dbConn.createStatement();
            String sql = "SELECT asignacion.empresa_id, "
                + "asignacion.fecha_asignacion,"
                + "empresa.empresa_nombre "
                + "FROM dispositivo_empresa asignacion "
                + "inner join empresa "
                + "on (asignacion.empresa_id = empresa.empresa_id) "
                + "where asignacion.device_id='"+_deviceId+"' "
                + "order by asignacion.empresa_id";
                 
            rs = statement.executeQuery(sql);		
            while (rs.next()) {
                data = 
                    new DispositivoEmpresaVO(_deviceId,rs.getString("empresa_id"));
                data.setEmpresaNombre(rs.getString("empresa_nombre"));
                listado.put(data.getEmpresaId(), data);
            }
            
            statement.close();
            rs.close();
            
            if (!m_usedGlobalDbConnection) dbLocator.freeConnection(dbConn);
        } catch (SQLException|DatabaseException e) {
            e.printStackTrace();
        }finally{
            try {
                if (statement != null) statement.close();
                if (rs != null) rs.close();
                if (!m_usedGlobalDbConnection) dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
        
        return listado;
    }
    
    private LinkedHashMap<String,DispositivoDepartamentoVO> getDepartamentosAsignados(String _deviceId){
        LinkedHashMap<String,DispositivoDepartamentoVO> listado = new LinkedHashMap<>();
        Statement statement = null;
        ResultSet rs = null;
        DispositivoDepartamentoVO data=new DispositivoDepartamentoVO(null,null);
        try {
            if (!m_usedGlobalDbConnection) dbConn = dbLocator.getConnection(m_dbpoolName,"[DispositivoDAO.getDepartamentosAsignados]");
            statement = dbConn.createStatement();
            
            String sql ="SELECT asignacion.depto_id, "
                + "asignacion.fecha_asignacion,depto_nombre "
                + "FROM dispositivo_departamento asignacion "
                + "inner join departamento depto on "
                + "(asignacion.depto_id = depto.depto_id) "
                + "where (asignacion.device_id='"+_deviceId+"') "
                + "order by asignacion.depto_id";
            rs = statement.executeQuery(sql);		
            while (rs.next()) {
                data = 
                    new DispositivoDepartamentoVO(_deviceId,rs.getString("depto_id"));
                data.setDeptoNombre(rs.getString("depto_nombre"));
                listado.put(data.getDeptoId(), data);
            }
            
            statement.close();
            rs.close();
            if (!m_usedGlobalDbConnection) dbLocator.freeConnection(dbConn);
        } catch (SQLException|DatabaseException e) {
            e.printStackTrace();
        }finally{
            try {
                if (statement != null) statement.close();
                if (rs != null) rs.close();
                if (!m_usedGlobalDbConnection) dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
        
        return listado;
    }
    
    private LinkedHashMap<String,DispositivoCentroCostoVO> getCentrosCostoAsignados(String _deviceId){
        LinkedHashMap<String,DispositivoCentroCostoVO> listado = new LinkedHashMap<>();
        DispositivoCentroCostoVO data=new DispositivoCentroCostoVO(null,-1);
        Statement statement = null;
        ResultSet rs        = null;
        try {
            if (!m_usedGlobalDbConnection) dbConn = dbLocator.getConnection(m_dbpoolName,"[DispositivoDAO.getCentrosCostoAsignados]");
            statement = dbConn.createStatement();
            String sql = "SELECT asignacion.cenco_id, "
                    + "asignacion.fecha_asignacion, "
                    + "cenco.ccosto_nombre "
                    + "FROM dispositivo_centrocosto asignacion "
                    + "inner join centro_costo cenco "
                    + "on (asignacion.cenco_id = cenco.ccosto_id) "
                    + "where asignacion.device_id='"+_deviceId+"' "
                    + "order by asignacion.cenco_id";
            rs = statement.executeQuery(sql);		
            while (rs.next()) {
                data = 
                    new DispositivoCentroCostoVO(_deviceId,rs.getInt("cenco_id"));
                data.setCencoNombre(rs.getString("ccosto_nombre"));
                listado.put(""+data.getCencoId(), data);
            }
            
            statement.close();
            rs.close();
            if (!m_usedGlobalDbConnection) dbLocator.freeConnection(dbConn);
        } catch (SQLException|DatabaseException e) {
            e.printStackTrace();
        }finally{
            try {
                if (statement != null) statement.close();
                if (rs != null) rs.close();
                if (!m_usedGlobalDbConnection) dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
        
        return listado;
    }
    
    public void openDbConnection(){
        try {
            m_usedGlobalDbConnection = true;
            dbConn = dbLocator.getConnection(m_dbpoolName,"[UsersDAO.openDbConnection]");
        } catch (DatabaseException ex) {
            System.err.println("[UsersDAO.openDbConnection]"
                + "Error: " + ex.toString());
        }
    }
    
    public void closeDbConnection(){
        try {
            m_usedGlobalDbConnection = false;
            dbLocator.freeConnection(dbConn);
        } catch (Exception ex) {
            System.err.println("[UsersDAO.closeDbConnection]"
                + "Error: "+ex.toString());
        }
    }
}
