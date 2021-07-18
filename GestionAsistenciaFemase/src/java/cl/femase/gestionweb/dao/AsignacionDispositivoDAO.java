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
import cl.femase.gestionweb.vo.PropertiesVO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexander
 */
public class AsignacionDispositivoDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    //private Connection dbConn;
    
    public AsignacionDispositivoDAO(PropertiesVO _propsValues) {
//        try {
//            dbLoc = DatabaseLocator.getInstance();
//            m_dbpoolName = _propsValues.getDbPoolName();
//        } catch (DatabaseException ex) {
//            m_logger.error("DbError: "+ex.toString());
//        }
    }

//    /**
//     * Actualiza un dispositivo
//     * @param _data
//     * @return 
//     */
//    public MaintenanceVO update(DispositivoVO _data){
//        MaintenanceVO objresultado = new MaintenanceVO();
//        PreparedStatement psupdate = null;
//        int result=0;
//        String msgError = "Error al actualizar "
//            + "dispositivo. "
//            + " id: "+_data.getId()
//            + " tipo: "+_data.getIdTipo();
//        
//        String msgFinal = " Actualiza dispositivo:"
//            + "id [" + _data.getId() + "]"
//            + "tipo [" + _data.getIdTipo() + "]";
//        
//        try{
//            objresultado.setMsg(msgFinal);
//            String sql = "UPDATE dispositivo "
//                    + " SET type_id=?, "
//                        + "estado=?,"
//                        + "ubicacion=?,"
//                    + "fecha_ultima_actualizacion=current_timestamp "
//                    + " WHERE device_id=?";
//
//            dbConn = dbLocator.getConnection(m_dbpoolName);
//            psupdate = dbConn.prepareStatement(sql);
//            psupdate.setInt(1,  _data.getIdTipo());
//            psupdate.setInt(2,  _data.getEstado());
//            psupdate.setString(3,  _data.getUbicacion());
//            psupdate.setString(4,  _data.getId());
//            
//            int rowAffected = psupdate.executeUpdate();
//            if (rowAffected == 1){
//                System.out.println("[update]dispositivo"
//                    + ", id:" +_data.getId()
//                    + ", tipo:" +_data.getIdTipo()
//                    +" actualizado OK!");
//            }
//
//            psupdate.close();
//            dbLocator.freeConnection(dbConn);
//        }catch(SQLException|DatabaseException sqle){
//            System.err.println("update dispositivo Error: "+sqle.toString());
//            objresultado.setThereError(true);
//            objresultado.setCodError(result);
//            objresultado.setMsgError(msgError+" :"+sqle.toString());
//        }catch(DatabaseException dbe){
//            System.err.println("update dispositivo Error: "+dbe.toString());
//            objresultado.setThereError(true);
//            objresultado.setCodError(result);
//            objresultado.setMsgError(msgError+" :"+dbe.toString());
//        }finally{
//            dbLocator.freeConnection(dbConn);
//        }
//
//        return objresultado;
//    }

     /**
     * Agrega un nuevo dispositivo a la empresa
     * @param _data
     * @return 
     */
    public MaintenanceVO insertAsignacionEmpresa(DispositivoEmpresaVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al insertar "
            + "dispositivo-empresa. "
            + " deviceId: "+_data.getDeviceId()
            + " empresaId: "+_data.getEmpresaId();
        
       String msgFinal = " Inserta dispositivo-empresa:"
            + "deviceId [" + _data.getDeviceId() + "]"
            + "empresaId [" + _data.getEmpresaId() + "]";
            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO dispositivo_empresa("
                    + "  device_id, "
                    + "empresa_id, "
                    + "fecha_asignacion) "
                    + " VALUES (?, ?, current_date)";

            dbConn = dbLocator.getConnection(m_dbpoolName, "[AsignacionDispositivoDAO.insertAsignacionEmpresa]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getDeviceId());
            insert.setString(2,  _data.getEmpresaId());
            
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println("[insert dispositivo_empresa]"
                    + ", deviceId:" +_data.getDeviceId()
                    + ", empresaId:" +_data.getEmpresaId()
                    +" insertado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("insert dispositivo_empresa Error1: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (insert != null) insert.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("delete Error: "+ex.toString());
            }
        }

        return objresultado;
    }
    
    /**
     * Agrega un nuevo dispositivo a un departamento
     * @param _data
     * @return 
     */
    public MaintenanceVO insertAsignacionDepartamento(DispositivoDepartamentoVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al insertar "
            + "dispositivo-departamento. "
            + " deviceId: "+_data.getDeviceId()
            + " departamentoId: "+_data.getDeptoId();
        
       String msgFinal = " Inserta dispositivo-departamento:"
            + "deviceId [" + _data.getDeviceId() + "]"
            + "departamentoId [" + _data.getDeptoId() + "]";
            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO dispositivo_departamento("
                    + "  device_id, "
                    + "depto_id, "
                    + "fecha_asignacion) "
                    + " VALUES (?, ?, current_date)";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[AsignacionDispositivoDAO.insertAsignacionDepartamento]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getDeviceId());
            insert.setString(2,  _data.getDeptoId());
            
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println("[insert dispositivo_departamento]"
                    + ", deviceId:" +_data.getDeviceId()
                    + ", departamentoId:" +_data.getDeptoId()
                    +" insertado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("insert dispositivo_departamento "
                + "Error1: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }
        finally{
            try {
                if (insert != null) insert.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("delete Error: "+ex.toString());
            }
        }

        return objresultado;
    }
    
    public MaintenanceVO deleteAsignacionesDepartamento(String _deviceId){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al eliminar asignacion "
            + "dispositivo-departamento. "
            + " deviceId: "+_deviceId;
        
       String msgFinal = " Elimina asignacion dispositivo-departamento:"
            + "deviceId [" + _deviceId + "]";
            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "delete "
                + "from dispositivo_departamento "
                + "where device_id = ? ";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[AsignacionDispositivoDAO.deleteAsignacionesDepartamento]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _deviceId);
           
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println("[delete dispositivo_departamento]"
                    + ", deviceId:" +_deviceId
                    +" insertado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("delete dispositivo_departamento Error1: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }
        finally{
            try {
                if (insert != null) insert.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("delete Error: "+ex.toString());
            }
        }

        return objresultado;
    }
    
    public MaintenanceVO deleteAsignacionesCentroCosto(String _deviceId){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al eliminar asignacion "
            + "dispositivo-centroCosto. "
            + " deviceId: "+_deviceId;
        
       String msgFinal = " Elimina asignacion dispositivo-centroCosto:"
            + "deviceId [" + _deviceId + "]";
            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "delete "
                + "from dispositivo_centrocosto "
                + "where device_id = ? ";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[AsignacionDispositivoDAO.deleteAsignacionesCentroCosto]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _deviceId);
           
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println("[delete dispositivo_centrocosto]"
                    + ", deviceId:" +_deviceId
                    +" insertado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("delete dispositivo_centrocosto Error1: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }
        finally{
            try {
                if (insert != null) insert.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("delete Error: "+ex.toString());
            }
        }

        return objresultado;
    }
    
    public MaintenanceVO deleteAsignacionesEmpresa(String _deviceId){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al eliminar asignacion "
            + "dispositivo-empresa. "
            + " deviceId: "+_deviceId;
        
       String msgFinal = " Elimina asignacion dispositivo-empresa:"
            + "deviceId [" + _deviceId + "]";
            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "delete "
                + "from dispositivo_empresa "
                + "where device_id = ? ";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[AsignacionDispositivoDAO.deleteAsignacionesEmpresa]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _deviceId);
           
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println("[delete dispositivo_empresa]"
                    + ", deviceId:" +_deviceId
                    +" insertado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("delete dispositivo_empresa Error1: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (insert != null) insert.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("delete Error: "+ex.toString());
            }
        }

        return objresultado;
    }
    
    /**
     * Agrega un nuevo dispositivo a un centro costo
     * @param _data
     * @return 
    */
    public MaintenanceVO insertAsignacionCentroCosto(DispositivoCentroCostoVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al insertar "
            + "dispositivo-centro costo. "
            + " deviceId: "+_data.getDeviceId()
            + " centro costoId: "+_data.getCencoId();
        
       String msgFinal = " Inserta dispositivo-centro costo:"
            + "deviceId [" + _data.getDeviceId() + "]"
            + "centro costoId [" + _data.getCencoId() + "]";
            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO dispositivo_centrocosto("
                    + "  device_id, "
                    + "cenco_id, "
                    + "fecha_asignacion) "
                    + " VALUES (?, ?, current_date)";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[AsignacionDispositivoDAO.insertAsignacionCentroCosto]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getDeviceId());
            insert.setInt(2,  _data.getCencoId());
            
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println("[insert dispositivo_centro costo]"
                    + ", deviceId:" +_data.getDeviceId()
                    + ", centro costoId:" +_data.getCencoId()
                    +" insertado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("insert dispositivo_centro costo Error1: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (insert != null) insert.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("delete Error: "+ex.toString());
            }
        }
        return objresultado;
    }
        
    /**
     * Retorna lista con los dispositivos
     * 
     * @param _tipo
     * @param _jtStartIndex
     * @param _fechaIngreso
     * @param _estado
     * @param _jtPageSize
     * @param _jtSorting
     * @return 
     */
//    public List<DispositivoVO> getDispositivos(int _tipo,
//            int _estado,
//            String _fechaIngreso,
//            int _jtStartIndex, 
//            int _jtPageSize, 
//            String _jtSorting){
//        
//        List<DispositivoVO> lista = 
//                new ArrayList<>();
//        SimpleDateFormat fechahoraformat = new SimpleDateFormat("dd-MM-yyyy");
//        PreparedStatement ps = null;
//        ResultSet rs = null;
//        DispositivoVO data;
//        
//        try{
//            String sql = "SELECT "
//                    + " dispositivo.device_id,"
//                    + " dispositivo.type_id,"
//                    + " tipo_dispositivo.dev_type_name,"
//                    + "dispositivo.estado,estado.estado_nombre,"
//                    + "dispositivo.fecha_ingreso, "
//                    + "to_char(dispositivo.fecha_ingreso, 'yyyy-MM-dd') fecha_ingreso_str,"
//                    + "dispositivo.ubicacion,"
//                    + "dispositivo.fecha_ultima_actualizacion,"
//                    + "to_char(dispositivo.fecha_ultima_actualizacion, 'yyyy-MM-dd HH:mm:ss') fecha_ultima_actualizacion_str "
//                    + "FROM "
//                    + " dispositivo "
//                    + "inner join tipo_dispositivo on (dispositivo.type_id = tipo_dispositivo.dev_type_id) "
//                    + "inner join estado on (dispositivo.estado = estado.estado_id)"
//                    + "where 1 = 1 ";
//
//            if (_tipo != -1){        
//                sql += " and type_id = "+_tipo;
//            }
//            
//            if (_estado != -1){        
//                sql += " and dispositivo.estado = " + _estado;
//            }
//            
//            if ( _fechaIngreso != null && _fechaIngreso.compareTo("") != 0){        
//                sql += " and fecha_ingreso = '" + _fechaIngreso + "'";
//            }
//            
//            sql += " order by " + _jtSorting; 
//            if (_jtPageSize > 0){
//                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
//            }
//            
//            System.out.println("cl.femase.gestionweb.service."
//                + "DispositivoDAO.getDispositivos() "
//                    + "SQL: "+sql);
//            dbConn = dbLocator.getConnection(m_dbpoolName);
//            ps = dbConn.prepareStatement(sql);
//            rs = ps.executeQuery();
//
//            while (rs.next()){
//                data = new DispositivoVO();
//                data.setId(rs.getString("device_id"));
//                data.setIdTipo(rs.getInt("type_id"));
//                data.setFechaIngreso(rs.getDate("fecha_ingreso"));
//                if (data.getFechaIngreso() != null){
//                    data.setFechaIngresoAsStr(rs.getString("fecha_ingreso_str"));
//                }
//                data.setFechaHoraUltimaActualizacionAsStr(rs.getString("fecha_ultima_actualizacion_str"));
//                data.setEstado(rs.getInt("estado"));
//                data.setUbicacion(rs.getString("ubicacion"));
//                data.setNombreEstado(rs.getString("estado_nombre"));        
//                data.setNombreTipo(rs.getString("dev_type_name"));                
//                
//                //setear listado de empresas, departamentos y cencos asignados
//                data.setEmpresas(getEmpresasAsignadas(data.getId()));
//                data.setDepartamentos(getDepartamentosAsignados(data.getId()));
//                data.setCencos(getCentrosCostoAsignados(data.getId()));
//                
//                lista.add(data);
//            }
//
//            ps.close();
//            rs.close();
//            
//        }catch(SQLException|DatabaseException sqle){
//            m_logger.error("Error: "+sqle.toString());
//        }finally{
//            dbLocator.freeConnection(dbConn);
//        }
//        return lista;
//    }
    
////    /**
////     * 
////     * @param _tipo
////     * @param _estado
////     * @param _fechaIngreso
////     * @return 
////     */
////    public int getDispositivosCount(int _tipo,
////            int _estado,
////            String _fechaIngreso){
////        int count=0;
////        try {
////            dbConn = dbLocator.getConnection(m_dbpoolName);
////            Statement statement = dbConn.createStatement();
////            String sql ="SELECT count(device_id) "
////                + "FROM dispositivo where 1=1 ";
////               
////            if (_tipo != -1){        
////                sql += " and type_id = "+_tipo;
////            }
////           
////            if (_estado != -1){        
////                sql += " and dispositivo.estado = " + _estado;
////            }
////            
////            if ( _fechaIngreso != null && _fechaIngreso.compareTo("") != 0){        
////                sql += " and fecha_ingreso = '" + _fechaIngreso + "'";
////            }
////            
////            ResultSet rs = statement.executeQuery(sql);		
////            if (rs.next()) {
////                count=rs.getInt("count");
////            }
////            
////            statement.close();
////            rs.close();
////            dbLocator.freeConnection(dbConn);
////        } catch (SQLException|DatabaseException e) {
////                e.printStackTrace();
////        }catch(DatabaseException dbe){
////            m_logger.error("Error: "+dbe.toString());
////        }finally{
////            dbLocator.freeConnection(dbConn);
////        }
////        return count;
////    }
   
////    private LinkedHashMap<String,DispositivoDepartamentoVO> getDepartamentosAsignados(String _deviceId){
////        LinkedHashMap<String,DispositivoDepartamentoVO> listado = new LinkedHashMap<>();
////    
////        DispositivoDepartamentoVO data=new DispositivoDepartamentoVO(null,null);
////        try {
////            dbConn = dbLocator.getConnection(m_dbpoolName);
////            Statement statement = dbConn.createStatement();
////            String sql ="SELECT depto_id, fecha_asignacion " +
////                " FROM dispositivo_departamento "
////                + "where device_id='"+_deviceId+"' "
////                    + "order by depto_id";
////             
////            ResultSet rs = statement.executeQuery(sql);		
////            while (rs.next()) {
////                data = 
////                    new DispositivoDepartamentoVO(_deviceId,rs.getString("depto_id"));
////                
////                listado.put(data.getDeptoId(), data);
////            }
////            
////            statement.close();
////            rs.close();
////            dbLocator.freeConnection(dbConn);
////        } catch (SQLException|DatabaseException e) {
////                e.printStackTrace();
////        }catch(DatabaseException dbe){
////            m_logger.error("Error: "+dbe.toString());
////        }finally{
////            dbLocator.freeConnection(dbConn);
////        }
////        
////        return listado;
////    }
////    
////    private LinkedHashMap<String,DispositivoCentroCostoVO> getCentrosCostoAsignados(String _deviceId){
////        LinkedHashMap<String,DispositivoCentroCostoVO> listado = new LinkedHashMap<>();
////    
////        DispositivoCentroCostoVO data=new DispositivoCentroCostoVO(null,-1);
////        try {
////            dbConn = dbLocator.getConnection(m_dbpoolName);
////            Statement statement = dbConn.createStatement();
////            String sql ="SELECT cenco_id, fecha_asignacion " +
////                " FROM dispositivo_centrocosto "
////                + "where device_id='"+_deviceId+"' "
////                    + "order by cenco_id";
////             
////            ResultSet rs = statement.executeQuery(sql);		
////            while (rs.next()) {
////                data = 
////                    new DispositivoCentroCostoVO(_deviceId,rs.getInt("cenco_id"));
////                
////                listado.put(""+data.getCencoId(), data);
////            }
////            
////            statement.close();
////            rs.close();
////            dbLocator.freeConnection(dbConn);
////        } catch (SQLException|DatabaseException e) {
////                e.printStackTrace();
////        }catch(DatabaseException dbe){
////            m_logger.error("Error: "+dbe.toString());
////        }finally{
////            dbLocator.freeConnection(dbConn);
////        }
////        
////        return listado;
////    }
////    
////    private LinkedHashMap<String,DispositivoEmpresaVO> getEmpresasAsignadas(String _deviceId){
////        LinkedHashMap<String,DispositivoEmpresaVO> listado = new LinkedHashMap<>();
////    
////        DispositivoEmpresaVO data=new DispositivoEmpresaVO(null,null);
////        try {
////            dbConn = dbLocator.getConnection(m_dbpoolName);
////            Statement statement = dbConn.createStatement();
////            String sql ="SELECT empresa_id, fecha_asignacion " +
////                " FROM dispositivo_empresa "
////                + "where device_id='"+_deviceId+"' "
////                    + "order by empresa_id";
////             
////            ResultSet rs = statement.executeQuery(sql);		
////            while (rs.next()) {
////                data = 
////                    new DispositivoEmpresaVO(_deviceId,rs.getString("empresa_id"));
////                
////                listado.put(data.getEmpresaId(), data);
////            }
////            
////            statement.close();
////            rs.close();
////            dbLocator.freeConnection(dbConn);
////        } catch (SQLException|DatabaseException e) {
////                e.printStackTrace();
////        }catch(DatabaseException dbe){
////            m_logger.error("Error: "+dbe.toString());
////        }finally{
////            dbLocator.freeConnection(dbConn);
////        }
////        
////        return listado;
////    }
}
