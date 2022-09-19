/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.MarcacionVirtualVO;
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
public class MarcacionVirtualDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    public boolean m_usedGlobalDbConnection = false;
    
    private final String SQL_INSERT_ASIGNACION = 
        "INSERT INTO marcacion_virtual("
            + "empresa_id, "
            + "rut_empleado, "
            + "fechahora_asignacion, "
            + "username, "
            + "fecha1, "
            + "fecha2, "
            + "fecha3,"
            + "fecha4, "
            + "fecha5, "
            + "fecha6, "
            + "fecha7, "
            + "registrar_ubicacion, "
            + "marca_movil, "
            + "movil_id, "
            + "admite_marca_virtual) "
        + "VALUES (?, ?, ?, ?,"
                + "?, ?, ?, ?,"
                + "?, ?, ?, ?, "
                + "?, ?, ?)";
    
    /**
    *
    * @param _propsValues
    */
    public MarcacionVirtualDAO(PropertiesVO _propsValues) {
    }

//    /**
//    * Elimina registro de asignacion marcacion virtual para un empleado
//    * 
//    * @param _data
//    * @return 
//    */
//    public MaintenanceVO insert(MarcacionVirtualVO _data){
//        MaintenanceVO objresultado = new MaintenanceVO();
//        int result=0;
//        String msgError = "Error al insertar "
//            + "registro tabla marcacion_virtual, "
//            + "EmpresaId: " + _data.getEmpresaId()
//            + ", rutEmpleado: " + _data.getRutEmpleado()    
//            + ", username: " + _data.getUsername();
//        
//        String msgFinal = " Inserta registro tabla marcacion_virtual:"
//            + "EmpresaId [" + _data.getEmpresaId() + "]" 
//            + ", rutEmpleado [" + _data.getRutEmpleado() + "]"    
//            + ", username [" + _data.getUsername() + "]";
//       
//        objresultado.setMsg(msgFinal);
//        PreparedStatement insert    = null;
//        
//        try{
//            String sql = "INSERT INTO marcacion_virtual("
//                + "empresa_id, rut_empleado, "
//                + "fechahora_asignacion, username) "
//                + "VALUES (?, ?, current_timestamp, ?)";
//
//
//            dbConn = dbLocator.getConnection(m_dbpoolName,
//                "[MarcacionVirtualDAO.insert]");
//            insert = dbConn.prepareStatement(sql);
//            insert.setString(1,  _data.getEmpresaId());
//            insert.setString(2,  _data.getRutEmpleado());
//            insert.setString(3,  _data.getUsername());
//            
//            int filasAfectadas = insert.executeUpdate();
//            if (filasAfectadas == 1){
//                System.out.println(WEB_NAME+"[insert]def marcacion_virtual"
//                    + ", empresaId:" + _data.getEmpresaId()
//                    + ", rutEmpleado:" + _data.getRutEmpleado()    
//                    + ", username:" + _data.getUsername()    
//                    +" insertada OK!");
//            }
//            
//            insert.close();
//            dbLocator.freeConnection(dbConn);
//        }catch(SQLException|DatabaseException sqle){
//            System.err.println("[insert marcacion_virtual]"
//                + "Error1: "+sqle.toString());
//            objresultado.setThereError(true);
//            objresultado.setCodError(result);
//            objresultado.setMsgError(msgError+" :"+sqle.toString());
//        }finally{
//            try {
//                if (insert != null) insert.close();
//                dbLocator.freeConnection(dbConn);
//            } catch (SQLException ex) {
//                System.err.println("[insert marcacion_virtual]Error: "+ex.toString());
//            }
//        }
//
//        return objresultado;
//    }
    
//    /**
//    * 
//    * @param _asignaciones
//    * @throws java.sql.SQLException
//    */
//    public void insertList(ArrayList<MarcacionVirtualVO> _asignaciones) throws SQLException{
//        try (
//            PreparedStatement statement = dbConn.prepareStatement(SQL_INSERT_MARCACIONVIRTUAL);
//        ) {
//            
//            int i = 0;
//            System.out.println(WEB_NAME+"[MarcacionVirtualDAO.insertList]"
//                + "items a insertar: " + _asignaciones.size());
//            for (MarcacionVirtualVO entity : _asignaciones) {
//                System.out.println(WEB_NAME+"[MarcacionVirtualDAO.insertList] "
//                    + "Insert asignacion marcacion virtual. "
//                    + "EmpresaId= " + entity.getEmpresaId()
//                    + ", rut empleado= " + entity.getRutEmpleado()
//                    + ", username= " + entity.getUsername());
//            
//                statement.setString(1,  entity.getEmpresaId());
//                statement.setString(2,  entity.getRutEmpleado());
//                statement.setString(3,  entity.getUsername());
//                
//                // ...
//                statement.addBatch();
//                i++;
//
//                if (i % 50 == 0 || i == _asignaciones.size()) {
//                    int[] rowsAffected = statement.executeBatch(); // Execute every 1000 items.
//                    System.out.println(WEB_NAME+"[MarcacionVirtualDAO.insertList]"
//                        + "filas afectadas= " + rowsAffected.length);
//                }
//            }
//        }
//    }
    
//    /**
//    * 
//    * @param _asignaciones
//    * @throws java.sql.SQLException
//    */
//    public void deleteList(ArrayList<MarcacionVirtualVO> _asignaciones) throws SQLException{
//        try (
//            PreparedStatement statement = dbConn.prepareStatement(SQL_DELETE_MARCACIONVIRTUAL);
//        ) {
//            int i = 0;
//            System.out.println(WEB_NAME+"[MarcacionVirtualDAO.deleteList]"
//                + "items a insertar: " + _asignaciones.size());
//            for (MarcacionVirtualVO entity : _asignaciones) {
//                System.out.println(WEB_NAME+"[MarcacionVirtualDAO.deleteList] "
//                    + "Delete asignacion marcacion virtual. "
//                    + "EmpresaId= " + entity.getEmpresaId()
//                    + ", rut empleado= " + entity.getRutEmpleado());
//            
//                statement.setString(1,  entity.getEmpresaId());
//                statement.setString(2,  entity.getRutEmpleado());
//                
//                // ...
//                statement.addBatch();
//                i++;
//
//                if (i % 50 == 0 || i == _asignaciones.size()) {
//                    int[] rowsAffected = statement.executeBatch(); // Execute every 1000 items.
//                    System.out.println(WEB_NAME+"[MarcacionVirtualDAO.deleteList]"
//                        + "filas afectadas= " + rowsAffected.length);
//                }
//            }
//        }
//    }
    
    
    /**
    * Elimina registro de asignacion de marcacion virtual para un empleado
    * 
    * @param _asignacion
    * 
    * @return 
    */
    public MaintenanceVO delete(MarcacionVirtualVO _asignacion){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al eliminar "
            + "registro tabla marcacion_virtual, "
            + "empresaId: " + _asignacion.getEmpresaId()
            + ", rutEmpleado: " + _asignacion.getRutEmpleado();
        
       String msgFinal = " Elimina registro tabla marcacion_virtual:"
            + "empresaId [" + _asignacion.getEmpresaId() + "]" 
            + ", rutEmpleado [" + _asignacion.getRutEmpleado() + "]";
        objresultado.setMsg(msgFinal);
        PreparedStatement psdelete    = null;
        
        try{
            String sql = "delete from marcacion_virtual " +
                "WHERE empresa_id = ? " +
                "and rut_empleado = ?";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[MarcacionVirtualDAO.delete]");
            psdelete = dbConn.prepareStatement(sql);
            psdelete.setString(1, _asignacion.getEmpresaId());
            psdelete.setString(2, _asignacion.getRutEmpleado());
                        
            int filasAfectadas = psdelete.executeUpdate();
            m_logger.debug("[delete marcacion_virtual]filasAfectadas: "+filasAfectadas);
            if (filasAfectadas == 1){
                m_logger.debug("[delete marcacion_virtual]"
                    + ", empresaId:" + _asignacion.getEmpresaId()
                    + ", rutEmpleado:" + _asignacion.getRutEmpleado()
                    +" eliminado OK!");
            }
            
            psdelete.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("[delete marcacion_virtual]"
                + "Error: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (psdelete != null) psdelete.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[delete marcacion_virtual]"
                    + "Error: "+ex.toString());
            }
        }

        return objresultado;
    }
    
    /**
    * Inserta registro de asignacion de marcacion virtual para un empleado
    * 
    * @param _asignacion
    * 
    * @return 
    */
    public MaintenanceVO insert(MarcacionVirtualVO _asignacion){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al insertar "
            + "registro tabla marcacion_virtual, "
            + "empresaId: " + _asignacion.getEmpresaId()
            + ", rutEmpleado: " + _asignacion.getRutEmpleado()
            + ", fecha1: " + _asignacion.getFecha1()
            + ", fecha2: " + _asignacion.getFecha2()
            + ", fecha3: " + _asignacion.getFecha3()
            + ", fecha4: " + _asignacion.getFecha4()
            + ", fecha5: " + _asignacion.getFecha5()
            + ", fecha6: " + _asignacion.getFecha6()
            + ", fecha7: " + _asignacion.getFecha7()
            + ", registrar ubicacion? " + _asignacion.getRegistrarUbicacion()
            + ", marca_movil? " + _asignacion.getMarcaMovil()
            + ", movil_id " + _asignacion.getMovilId()
            + ", admite_marca_virtual " + _asignacion.getAdmiteMarcaVirtual();
        
       String msgFinal = "Inserta registro tabla marcacion_virtual:"
            + "empresaId [" + _asignacion.getEmpresaId() + "]" 
            + ", rutEmpleado [" + _asignacion.getRutEmpleado() + "]"
            + ", fecha1 [" + _asignacion.getFecha1() + "]"
            + ", fecha2 [" + _asignacion.getFecha2() + "]"
            + ", fecha3 [" + _asignacion.getFecha3() + "]"
            + ", fecha4 [" + _asignacion.getFecha4() + "]"
            + ", fecha5 [" + _asignacion.getFecha5() + "]"
            + ", fecha6 [" + _asignacion.getFecha6() + "]"
            + ", fecha7 [" + _asignacion.getFecha7() + "]"
            + ", registrar ubicacion [" + _asignacion.getRegistrarUbicacion() + "]"
            + ", marca movil [" + _asignacion.getMarcaMovil() + "]"
            + ", movil_id [" + _asignacion.getMovilId() + "]"
            + ", admite_marca_virtual [" + _asignacion.getAdmiteMarcaVirtual() + "]";
        objresultado.setMsg(msgFinal);
        PreparedStatement psinsert    = null;
        
        try{
            String sql = "INSERT INTO marcacion_virtual("
                + "empresa_id, "
                + "rut_empleado, "
                + "fechahora_asignacion, "
                + "username, "
                + "fecha1, "
                + "fecha2, "
                + "fecha3,"
                + "fecha4, "
                + "fecha5, "
                + "fecha6, "
                + "fecha7, "
                + "registrar_ubicacion, "
                + "marca_movil, "
                + "movil_id, "
                + "admite_marca_virtual) "
                + "VALUES (?, ?, ?, ?,"
                        + "?, ?, ?, ?, "
                        + "?, ?, ?, ?, "
                        + "?, ?, ?)";

            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[MarcacionVirtualDAO.insert]");
            psinsert = dbConn.prepareStatement(sql);
            psinsert.setString(1, _asignacion.getEmpresaId());
            psinsert.setString(2, _asignacion.getRutEmpleado());
            psinsert.setTimestamp(3, Utilidades.getTimestamp(_asignacion.getFechaAsignacion(), "yyyy-MM-dd HH:mm:ss"));
            psinsert.setString(4, _asignacion.getUsername());
            //fechas
            psinsert.setDate(5, Utilidades.getJavaSqlDate(_asignacion.getFecha1(), "yyyy-MM-dd"));
            psinsert.setDate(6, Utilidades.getJavaSqlDate(_asignacion.getFecha2(), "yyyy-MM-dd"));
            psinsert.setDate(7, Utilidades.getJavaSqlDate(_asignacion.getFecha3(), "yyyy-MM-dd"));
            psinsert.setDate(8, Utilidades.getJavaSqlDate(_asignacion.getFecha4(), "yyyy-MM-dd"));
            psinsert.setDate(9, Utilidades.getJavaSqlDate(_asignacion.getFecha5(), "yyyy-MM-dd"));
            psinsert.setDate(10, Utilidades.getJavaSqlDate(_asignacion.getFecha6(), "yyyy-MM-dd"));
            psinsert.setDate(11, Utilidades.getJavaSqlDate(_asignacion.getFecha7(), "yyyy-MM-dd"));
            psinsert.setString(12, _asignacion.getRegistrarUbicacion());
            psinsert.setString(13, _asignacion.getMarcaMovil());
            psinsert.setString(14, _asignacion.getMovilId());
            psinsert.setString(15, _asignacion.getAdmiteMarcaVirtual());
            
            int filasAfectadas = psinsert.executeUpdate();
            m_logger.debug("[insert marcacion_virtual]"
                + "filasAfectadas: "+filasAfectadas);
            if (filasAfectadas == 1){
                m_logger.debug("[insert marcacion_virtual]"
                    + ", empresaId:" + _asignacion.getEmpresaId()
                    + ", rutEmpleado:" + _asignacion.getRutEmpleado()
                    + ", fecha1: " + _asignacion.getFecha1()
                    + ", fecha2: " + _asignacion.getFecha2()
                    + ", fecha3: " + _asignacion.getFecha3()
                    + ", fecha4: " + _asignacion.getFecha4()
                    + ", fecha5: " + _asignacion.getFecha5()
                    + ", fecha6: " + _asignacion.getFecha6()
                    + ", fecha7: " + _asignacion.getFecha7()
                    + ", registrar ubicacion: " + _asignacion.getRegistrarUbicacion()
                    + ", marca movil: " + _asignacion.getMarcaMovil()
                    + ", movil_id: " + _asignacion.getMovilId()    
                    +" insert OK!");
            }
            
            psinsert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("[insert marcacion_virtual]"
                + "Error: " + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (psinsert != null) psinsert.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[insert marcacion_virtual]"
                    + "Error: "+ex.toString());
            }
        }

        return objresultado;
    }
    
    /**
    * Retorna lista con empleados
    * 
    * @param _rutEmpleado
    * @param _cencoId
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * 
    * @return 
    */
    public List<MarcacionVirtualVO> getRegistros(
            int _cencoId,
            String _rutEmpleado,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<MarcacionVirtualVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        MarcacionVirtualVO data;
        
        try{
            String sql = "select "
                + "ve.empresa_id, "
                + "ve.empresa_nombre,"
                + "ve.rut,"
                + "ve.nombre nombre_empleado,"
                + "ve.depto_id, "
                + "ve.depto_nombre,"
                + "ve.cenco_id, "
                + "ve.ccosto_nombre,"
                + "coalesce(to_char(mv.fechahora_asignacion, 'yyyy-MM-dd HH24:MI:SS'),'') fechahora_asignacion,"
                + "mv.username,"
                + "mv.comentario,"
                + "coalesce(mv.admite_marca_virtual,'N') as admite_marca_virtual,"
                    + "fecha1,"
                    + "fecha2,"
                    + "fecha3,"
                    + "fecha4,"
                    + "fecha5,"
                    + "fecha6,"
                    + "fecha7,"
                    + "coalesce(registrar_ubicacion,'N') registrar_ubicacion,"
                    + "coalesce(marca_movil,'N') marca_movil,"
                    + "mv.movil_id "
                + "from view_empleado ve left outer join marcacion_virtual mv " 
                    + "on (mv.empresa_id = ve.empresa_id and mv.rut_empleado = ve.rut) "
                + "where 1 =1 "
                    + "and empl_estado = 1 "
                    + "and fin_contrato >= current_date "
                    + "and art_22 = false "
                    + "and empresa_estado = 1 "
                    + "and depto_estado = 1 "
                    + "and cenco_estado = 1"; 
            
            if (_cencoId != -1){
                sql += " and ve.cenco_id = " + _cencoId;
            }
            
            if (_rutEmpleado != null && _rutEmpleado.compareTo("-1") != 0){        
                sql += " and ve.rut = '" + _rutEmpleado + "' ";
            }
            
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            System.out.println(WEB_NAME+"[MarcacionVirtualDAO."
                + "getRegistros]sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[MarcacionVirtualDAO.getRegistros]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new MarcacionVirtualVO();
                
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setDeptoId(rs.getString("depto_id"));
                data.setCencoId(rs.getInt("cenco_id"));
                
                data.setRutEmpleado(rs.getString("rut"));
                data.setNombreEmpleado(rs.getString("nombre_empleado"));
                data.setRowKey(data.getEmpresaId()+"|"+data.getRutEmpleado());
                data.setFechaAsignacion(rs.getString("fechahora_asignacion"));
                data.setUsername(rs.getString("username"));
                data.setComentario(rs.getString("comentario"));
                data.setAdmiteMarcaVirtual(rs.getString("admite_marca_virtual"));
                data.setComentario(rs.getString("comentario"));
                
                data.setFecha1(rs.getString("fecha1"));
                data.setFecha2(rs.getString("fecha2"));
                data.setFecha3(rs.getString("fecha3"));
                data.setFecha4(rs.getString("fecha4"));
                data.setFecha5(rs.getString("fecha5"));
                data.setFecha6(rs.getString("fecha6"));
                data.setFecha7(rs.getString("fecha7"));
                
                data.setEmpresaNombre(rs.getString("empresa_nombre"));
                data.setDeptoNombre(rs.getString("depto_nombre"));
                data.setCencoNombre(rs.getString("ccosto_nombre"));
                
                data.setRegistrarUbicacion(rs.getString("registrar_ubicacion"));
                data.setMarcaMovil(rs.getString("marca_movil"));
                data.setMovilId(rs.getString("movil_id"));
                
                lista.add(data);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("MarcacionVirtualDAO.getRegistros.Error: "+sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("MarcacionVirtualDAO.getRegistros.Error: "+ex.toString());
            }
        }
        return lista;
    }
    
    /**
    * 
    * @param _rutEmpleado
    * @param _cencoId
    * 
    * @return 
    */
    public int getRegistrosCount(int _cencoId, 
            String _rutEmpleado){
        int count=0;
        Statement statement = null;
        ResultSet rs = null;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[MarcacionVirtualDAO.getRegistrosCount]");
            statement = dbConn.createStatement();
            String strSql ="SELECT count(ve.rut) numrows "
                + "from view_empleado ve left outer join marcacion_virtual mv " 
                    + "on (mv.empresa_id = ve.empresa_id and mv.rut_empleado = ve.rut) "
                + "where 1 =1 "
                    + "and empl_estado = 1 "
                    + "and fin_contrato >= current_date "
                    + "and art_22 = false "
                    + "and empresa_estado = 1 "
                    + "and depto_estado = 1 "
                    + "and cenco_estado = 1"; 
            
            if (_cencoId != -1){
                strSql += " and ve.cenco_id = " + _cencoId;
            }
            
            if (_rutEmpleado != null && _rutEmpleado.compareTo("-1") != 0){        
                strSql += " and ve.rut = '" + _rutEmpleado + "' ";
            }
            rs = statement.executeQuery(strSql);		
            if (rs.next()) {
                count=rs.getInt("numrows");
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
                System.err.println("MarcacionVirtualDAO."
                    + "getRegistrosCount.Error: "+ex.toString());
            }
        }
        return count;
    }
   
    /**
    * 
    * @param _asignaciones
    */
    public void saveAsignacionList(ArrayList<MarcacionVirtualVO> _asignaciones){
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[MarcacionVirtualDAO.saveAsignacionList]");
            PreparedStatement statement = dbConn.prepareStatement(SQL_INSERT_ASIGNACION);
            int i = 0;
            System.out.println(WEB_NAME+"[MarcacionVirtualDAO.saveAsignacionList]"
                + "items a insertar: " + _asignaciones.size());
            for (MarcacionVirtualVO asignacion : _asignaciones) {
                if (asignacion.getRutEmpleado().compareTo("TODOS") != 0){    
                    System.out.println(WEB_NAME+"[MarcacionVirtualDAO.saveAsignacionList]"
                        + "Inserta registro tabla marcacion_virtual:"
                        + "empresaId [" + asignacion.getEmpresaId() + "]" 
                        + ", rutEmpleado [" + asignacion.getRutEmpleado() + "]"
                        + ", fecha1 [" + asignacion.getFecha1() + "]"
                        + ", fecha2 [" + asignacion.getFecha2() + "]"
                        + ", fecha3 [" + asignacion.getFecha3() + "]"
                        + ", fecha4 [" + asignacion.getFecha4() + "]"
                        + ", fecha5 [" + asignacion.getFecha5() + "]"
                        + ", fecha6 [" + asignacion.getFecha6() + "]"
                        + ", fecha7 [" + asignacion.getFecha7() + "]"
                        + ", registrar ubicacion [" + asignacion.getRegistrarUbicacion() + "]"
                        + ", marca movil [" + asignacion.getMarcaMovil() + "]"
                        + ", movil_id [" + asignacion.getMovilId() + "]"
                        + ", admite_marca_virtual [" + asignacion.getAdmiteMarcaVirtual() + "]");

                    statement.setString(1, asignacion.getEmpresaId());
                    statement.setString(2, asignacion.getRutEmpleado());
                    statement.setTimestamp(3, Utilidades.getTimestamp(asignacion.getFechaAsignacion(), "yyyy-MM-dd HH:mm:ss"));
                    statement.setString(4, asignacion.getUsername());
                    //fechas
                    statement.setDate(5, Utilidades.getJavaSqlDate(asignacion.getFecha1(), "yyyy-MM-dd"));
                    statement.setDate(6, Utilidades.getJavaSqlDate(asignacion.getFecha2(), "yyyy-MM-dd"));
                    statement.setDate(7, Utilidades.getJavaSqlDate(asignacion.getFecha3(), "yyyy-MM-dd"));
                    statement.setDate(8, Utilidades.getJavaSqlDate(asignacion.getFecha4(), "yyyy-MM-dd"));
                    statement.setDate(9, Utilidades.getJavaSqlDate(asignacion.getFecha5(), "yyyy-MM-dd"));
                    statement.setDate(10, Utilidades.getJavaSqlDate(asignacion.getFecha6(), "yyyy-MM-dd"));
                    statement.setDate(11, Utilidades.getJavaSqlDate(asignacion.getFecha7(), "yyyy-MM-dd"));
                    statement.setString(12, asignacion.getRegistrarUbicacion());
                    statement.setString(13, asignacion.getMarcaMovil());
                    statement.setString(14, asignacion.getMovilId());
                    statement.setString(15, asignacion.getAdmiteMarcaVirtual());
                    
                    // ...
                    statement.addBatch();
                    i++;

                    if (i % 50 == 0 || i == _asignaciones.size()) {
                        int[] rowsAffected = statement.executeBatch(); // Execute every 1000 items.
                        System.out.println(WEB_NAME+"[MarcacionVirtualDAO."
                            + "saveAsignacionList]"
                            + "filas afectadas= "+rowsAffected.length);
                    }
                }    
            }
        }catch(SQLException sqlex){
            System.err.println("[MarcacionVirtualDAO."
                + "saveAsignacionList]"
                + "Error_1 al asignar marcacion virtual "
                + "para lista de empleados: " + sqlex.toString());
        }catch(DatabaseException dbex){
            System.err.println("[MarcacionVirtualDAO."
                + "saveAsignacionList]"
                + "Error_2 al asignar marcacion virtual "
                + "para lista de empleados: " + dbex.toString());
        }
    }
    
}
