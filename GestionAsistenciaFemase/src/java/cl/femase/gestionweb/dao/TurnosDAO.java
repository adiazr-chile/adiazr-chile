/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.TurnoVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.TurnoCentroCostoVO;
import cl.femase.gestionweb.vo.UsuarioCentroCostoVO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexander
 */
public class TurnosDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    
    public TurnosDAO(PropertiesVO _propsValues) {
    }

    /**
     * Actualiza un turno
     * @param _rutEmpleado
     * @param _idTurno
     * @return 
     */
    public ResultCRUDVO updateTurnoEmpleado(String _rutEmpleado,int _idTurno){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "Turno. "
            + "rut: "+_rutEmpleado
            + "idTurno: "+_idTurno;
        
        try{
            String msgFinal = " Actualiza turno empleado:"
                + "rut [" + _rutEmpleado + "]"
                + ", idTurno [" + _idTurno + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE empleado "
                + " SET empl_id_turno = ? "
                + " WHERE empl_rut = ?";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnosDAO.updateTurnoEmpleado]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setInt(1,  _idTurno);
            psupdate.setString(2,  _rutEmpleado);
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println(WEB_NAME+"[updateTurnoEmpleado]"
                    + "rut:" +_rutEmpleado+", idTurno= "+_idTurno
                    +" actualizado OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("updateTurnoEmpleado Error: "+sqle.toString());
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
     * Actualiza un turno
     * @param _data
     * @return 
     */
    public ResultCRUDVO update(TurnoVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "Turno, "
            + "id: "+_data.getId()
            + ", nombre: "+_data.getNombre()
            + ", idEmpresa: "+_data.getEmpresaId();
        
        try{
            String msgFinal = " Actualiza turno:"
                + "id [" + _data.getId() + "]" 
                + ", nombre [" + _data.getNombre() + "]"
                + ", id empresa [" + _data.getEmpresaId() + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE turno "
                + " SET "
                    + "nombre_turno = ?, "
                    + "estado_turno=?,"
                    + "fecha_modificacion = current_timestamp,"
                    + "empresa_id = ?, "
                    + "rotativo =? "
                + "WHERE id_turno = ?";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnosDAO.update]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setString(1,  _data.getNombre());
            psupdate.setInt(2,  _data.getEstado());
            psupdate.setString(3,  _data.getEmpresaId());
            psupdate.setBoolean(4,  _data.isRotativo());
            psupdate.setInt(5,  _data.getId());
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println(WEB_NAME+"[update]turno"
                    + ", id:" +_data.getId()
                    + ", nombre:" +_data.getNombre()
                    +" actualizado OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("update turno Error: "+sqle.toString());
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
     * Agrega un nuevo departamento
     * @param _data
     * @return 
     */
    public ResultCRUDVO insert(TurnoVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        int result=0;
        String msgError = "Error al insertar "
            + "turno. "
            + " empresaId: " + _data.getEmpresaId()
            + ",nombre: " + _data.getNombre()
            + ",rotativo?: " + _data.isRotativo();
        
       String msgFinal = " Inserta turno:"
            + "empresaId [" + _data.getEmpresaId() + "]"
            + ", nombre [" + _data.getNombre() + "]"
            + ", rotativo [" + _data.isRotativo() + "]";
            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO turno ("
                +"id_turno, "
                + "nombre_turno, "
                + "estado_turno, "
                + "fecha_creacion, "
                + "empresa_id, rotativo) "
                + " VALUES (nextval('turno_id_seq'), ?, ?, current_timestamp,?,?)";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnosDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getNombre());
            insert.setInt(2,  _data.getEstado());
            insert.setString(3,  _data.getEmpresaId());
            insert.setBoolean(4,  _data.isRotativo());
            
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println(WEB_NAME+"[insert turno]"
                    + ", nombre:" + _data.getNombre()
                    + ", id:" + _data.getId()
                    + ", rotativo:" + _data.isRotativo()    
                    +" insertado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("insert turno Error1: "+sqle.toString());
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
    * Retorna lista con los turnos existentes en el sistema
    * 
    * @param _empresaId
    * @param _nombre
    * @param _estado
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * 
    * @return 
    */
    public List<TurnoVO> getTurnos(String _empresaId, 
            String _nombre,
            int _estado,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<TurnoVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        TurnoVO data;
        
        try{
            String sql ="SELECT "
                + "id_turno, "
                + "nombre_turno,"
                + "empresa_id, "
                + "estado_turno, "
                + "fecha_creacion,"
                + "fecha_modificacion,"
                + "to_char(fecha_creacion, 'yyyy-MM-dd HH24:MI:SS') fecha_creacion_str, "
                + "to_char(fecha_modificacion, 'yyyy-MM-dd HH24:MI:SS') fecha_modificacion_str,"
                    + " CASE WHEN rotativo THEN 'S' ELSE 'N' END rotativo "
                + "FROM turno where 1=1 ";
           
            if (_nombre!=null && _nombre.compareTo("")!=0){        
                sql += " and upper(nombre_turno) like '%"+_nombre.toUpperCase()+"%'";
            }
            if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
                sql += " and empresa_id = '" + _empresaId + "'";
            }
            if (_estado != -1){        
                sql += " and estado_turno = " + _estado;
            }
           
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            System.out.println(WEB_NAME+"[TurnosDAO."
                + "getTurnos]Sql: " + sql);
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnosDAO.getTurnos]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new TurnoVO();
                data.setId(rs.getInt("id_turno"));
                data.setNombre(rs.getString("nombre_turno"));
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setEstado(rs.getInt("estado_turno"));
                
                data.setFechaCreacion(rs.getDate("fecha_creacion"));
                data.setFechaCreacionAsStr(rs.getString("fecha_creacion_str"));
                data.setFechaModificacion(rs.getDate("fecha_modificacion"));
                data.setFechaModificacionAsStr(rs.getString("fecha_modificacion_str"));
                data.setRotativo(rs.getString("rotativo"));
                        
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
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
        
        return lista;
    }
    
    /**
    * Retorna lista con los turnos existentes en el sistema
    * 
    * @param _empresaId
    * @param _deptoId
    * @param _cencoId
    * 
    * @return 
    */
    public LinkedHashMap<Integer, TurnoVO> getTurnosAsignadosByCenco(String _empresaId,
            String _deptoId, 
            int _cencoId){
               
        LinkedHashMap<Integer, TurnoVO> lista = new LinkedHashMap<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        TurnoVO turno;
        
        try{
            String sql = "SELECT "
                    + "turno.id_turno,"
                    + "turno.nombre_turno,"
                    + "turno.empresa_id,"
                    + "turno.holgura,"
                    + "turno.estado_turno,"
                    + "turno.fecha_creacion,"
                    + "turno.fecha_modificacion,"
                    + "turno.minutos_colacion,"
                    + "CASE WHEN turno.rotativo THEN 'S' ELSE 'N' END rotativo,"
                    + "to_char(tcc.fecha_asignacion,'YYYY-MM-DD HH24:MI:SS') fecha_asignacion,"
                    + "centro_costo.ccosto_nombre "
                + "FROM turno "
                + "inner join turno_centrocosto tcc "
                + "on (turno.empresa_id = tcc.empresa_id and turno.id_turno = tcc.id_turno) "
                    + "inner join centro_costo on (centro_costo.ccosto_id = tcc.cenco_id and centro_costo.depto_id = tcc.depto_id)"
                + "where (tcc.empresa_id = '" + _empresaId + "' "
                    + " and tcc.depto_id = '" + _deptoId + "' "
                    + " and cenco_id = " + _cencoId + " "
                    + ") "
                + "order by nombre_turno";
            System.out.println(WEB_NAME+"[TurnosDAO."
                + "getTurnosAsignadosByCenco]sql: " + sql);
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnosDAO.getTurnosAsignadosByCenco]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                turno = new TurnoVO();
                turno.setId(rs.getInt("id_turno"));
                turno.setNombre(rs.getString("nombre_turno"));
                turno.setEmpresaId(rs.getString("empresa_id"));
                turno.setFechaAsignacionStr(rs.getString("fecha_asignacion"));
                turno.setCencoNombre(rs.getString("ccosto_nombre"));
                turno.setRotativo(rs.getString("rotativo"));
                
                lista.put(turno.getId(),turno);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("Error: "+sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
        
        return lista;
    }
    
    
    /**
     * Retorna lista con los turnos normales asignados al cenco. 
     * y con todos los turnos rotativos
     * 
     * @param _empresaId
     * @param _deptoId
     * @param _cencoId
     * 
     * @return 
     */
    public LinkedHashMap<Integer, TurnoVO> getAllTurnosAsignadosByCenco(String _empresaId,
            String _deptoId, 
            int _cencoId){
               
        LinkedHashMap<Integer, TurnoVO> lista = new LinkedHashMap<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        TurnoVO turno;
        
        try{
            String sql = "SELECT "
                + "turno.id_turno,"
                + "turno.nombre_turno,"
                + "false rotativo "
                + "FROM turno "
                + "inner join turno_centrocosto tcc "
                + "on (turno.empresa_id = tcc.empresa_id and turno.id_turno = tcc.id_turno) "
                + "inner join centro_costo on (centro_costo.ccosto_id = tcc.cenco_id and centro_costo.depto_id = tcc.depto_id) "
                + "where (tcc.empresa_id = '" + _empresaId + "' "
                + " and tcc.depto_id = '" + _deptoId + "' "
                + " and cenco_id = " + _cencoId + ") "
                + " union "
                + "select "
                + "id_turno,"
                + "nombre_turno,"
                + "true rotativo "
                + "from turno_rotativo "
                + "where empresa_id = '" + _empresaId + "' "
                + "order by nombre_turno";
            
            System.out.println(WEB_NAME+"[TurnosDAO."
                + "getAllTurnosAsignadosByCenco]sql: " + sql);
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnosDAO.getAllTurnosAsignadosByCenco]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                turno = new TurnoVO();
                turno.setId(rs.getInt("id_turno"));
                turno.setNombre(rs.getString("nombre_turno"));
                turno.setRotativo(rs.getString("rotativo"));
                
                lista.put(turno.getId(),turno);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("Error: "+sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
        
        return lista;
    }
    
    /**
    * Retorna lista de turnos no asignados al centro de costo.
    * 
    * @param _empresaId
    * @param _deptoId
    * @param _cencoId
    * 
    * @return 
    */
    public LinkedHashMap<Integer, TurnoVO> getTurnosNoAsignadosByCenco(String _empresaId,
            String _deptoId, 
            int _cencoId){
               
        LinkedHashMap<Integer, TurnoVO> lista = new LinkedHashMap<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        TurnoVO turno;
        
        try{
            String sql = "SELECT "
                + "turno.id_turno,"
                + "turno.nombre_turno,"
                + "turno.empresa_id,"
                + "turno.holgura, "
                + "turno.estado_turno, "
                + "turno.fecha_creacion, "
                + "turno.fecha_modificacion, "
                + "turno.minutos_colacion, "
                + "CASE WHEN turno.rotativo THEN 'S' ELSE 'N' END rotativo "
                + "from turno "
                + "where turno.empresa_id = '" + _empresaId + "' and turno.id_turno<>-1 "
                + "and turno.estado_turno = 1 "
                + "and turno.id_turno not in ("
                    + "select id_turno "
                    + "from turno_centrocosto "
                    + "where turno_centrocosto.empresa_id = '" + _empresaId + "' "
                    + "and turno_centrocosto.depto_id = '" + _deptoId + "' "
                    + "and turno_centrocosto.cenco_id = " + _cencoId + " "
                + ") "
                + "order by turno.nombre_turno";

            System.out.println(WEB_NAME+"[TurnosDAO."
                + "getTurnosNoAsignadosByCenco]sql: "+ sql);
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnosDAO.getTurnosNoAsignadosByCenco]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                turno = new TurnoVO();
                turno.setId(rs.getInt("id_turno"));
                turno.setNombre(rs.getString("nombre_turno"));
                turno.setEmpresaId(rs.getString("empresa_id"));
                turno.setRotativo(rs.getString("rotativo"));
                
                lista.put(turno.getId(),turno);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("Error: "+sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
        
        return lista;
    }
    
    /**
    * 
    * @param _asignaciones
    * @throws java.sql.SQLException
    */
    public void eliminarAsignacionesCencos(ArrayList<TurnoCentroCostoVO> _asignaciones) throws SQLException{
        
        try{
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnosDAO.eliminarAsignacionesCencos]");
            int i = 0;
            System.out.println(WEB_NAME+"[TurnosDAO.eliminarAsignacionesCencos]"
                + "items a eliminar: " + _asignaciones.size());
            String sqlDelete = "delete "
                    + "from turno_centrocosto "
                + "where "
                    + "empresa_id = ? "
                    + "and depto_id = ? "
                    + "and cenco_id = ?";
            PreparedStatement statement = dbConn.prepareStatement(sqlDelete);
            
            for (TurnoCentroCostoVO asignacion : _asignaciones) {
                System.out.println(WEB_NAME+"[TurnosDAO.eliminarAsignacionesCencos] "
                    + "Eliminar asignacion turno. "
                    + "EmpresaId= " + asignacion.getEmpresaId()    
                    + ",deptoId= " + asignacion.getDeptoId()
                    + ", cencoId= " + asignacion.getCencoId());
                
                statement.setString(1,  asignacion.getEmpresaId());
                statement.setString(2,  asignacion.getDeptoId());
                statement.setInt(3,  asignacion.getCencoId());
                
                // ...
                statement.addBatch();
                i++;

                if (i % 50 == 0 || i == _asignaciones.size()) {
                    int[] rowsAffected = statement.executeBatch(); // Execute every 1000 items.
                    System.out.println(WEB_NAME+"[TurnosDAO."
                        + "eliminarAsignacionesCencos]"
                        + "filas afectadas= "+rowsAffected.length);
                }
            }
            dbLocator.freeConnection(dbConn);
        }catch(SQLException ex){
            System.err.println("[TurnosDAO."
                + "eliminarAsignacionesCencos]Error= " + ex.toString());
            ex.printStackTrace();
            dbLocator.freeConnection(dbConn);
        }catch(DatabaseException dbex){
            System.err.println("[TurnosDAO."
                + "eliminarAsignacionesCencos]Error= " + dbex.toString());
            dbex.printStackTrace();
            dbLocator.freeConnection(dbConn);
        }
        finally{
            dbLocator.freeConnection(dbConn);

        }
    }
    
    /**
    * 
    * @param _asignaciones
    * @throws java.sql.SQLException
    */
    public void insertarAsignacionesCencos(ArrayList<TurnoCentroCostoVO> _asignaciones) throws SQLException{
        
        try{
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnosDAO.insertarAsignacionesCencos]");
            int i = 0;
            System.out.println(WEB_NAME+"[TurnosDAO.insertarAsignacionesCencos]"
                + "items a insertar: "+_asignaciones.size());
            String sqlInsert = "INSERT INTO turno_centrocosto("
                    + " empresa_id, id_turno, depto_id, cenco_id, fecha_asignacion) "
                    + " VALUES (?, ?, ?, ?, current_timestamp)";
            PreparedStatement statement = dbConn.prepareStatement(sqlInsert);
            
            for (TurnoCentroCostoVO asignacion : _asignaciones) {
                System.out.println(WEB_NAME+"[TurnosDAO.insertarAsignacionesCencos] "
                    + "Insert turno_centrocosto. "
                    + "EmpresaId= " + asignacion.getEmpresaId()    
                    + ",idTurno= " + asignacion.getTurnoId()
                    + ", deptoId= " + asignacion.getDeptoId()
                    + ", cencoId= " + asignacion.getCencoId());
                
                statement.setString(1,  asignacion.getEmpresaId());
                statement.setInt(2,  asignacion.getTurnoId());
                statement.setString(3,  asignacion.getDeptoId());
                statement.setInt(4,  asignacion.getCencoId());
                
                // ...
                statement.addBatch();
                i++;

                if (i % 50 == 0 || i == _asignaciones.size()) {
                    int[] rowsAffected = statement.executeBatch(); // Execute every 1000 items.
                    System.out.println(WEB_NAME+"[TurnosDAO."
                        + "insertarAsignacionesCencos]"
                        + "filas afectadas= "+rowsAffected.length);
                }
            }
            dbLocator.freeConnection(dbConn);
        }catch(SQLException ex){
            System.err.println("[TurnosDAO."
                + "insertarAsignacionesCencos]Error= " + ex.toString());
            ex.printStackTrace();
            dbLocator.freeConnection(dbConn);
        }catch(DatabaseException dbex){
            System.err.println("[TurnosDAO."
                + "insertarAsignacionesCencos]Error= " + dbex.toString());
            dbex.printStackTrace();
            dbLocator.freeConnection(dbConn);
        }
        finally{
            dbLocator.freeConnection(dbConn);
//            try {
//                //dbLocator.freeConnection(dbConn);
//            } catch (SQLException|DatabaseException ex) {
//                System.err.println("Error: "+ex.toString());
//            }
        }
    }
    
    /**
     * Retorna lista con los turnos para la empresa indicada
     * 
     * @param _empresaId
     * @return 
     */
    public List<TurnoVO> getTurnosByEmpresa(String _empresaId){
        
        List<TurnoVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        TurnoVO data;
        
        try{
            String sql ="SELECT "
                + "id_turno, "
                + "nombre_turno,"
                + "empresa_id, "
                + "estado_turno, "
                + "fecha_creacion,"
                + "fecha_modificacion,"
                + "to_char(fecha_creacion, 'yyyy-MM-dd HH24:MI:SS') fecha_creacion_str, "
                + "to_char(fecha_modificacion, 'yyyy-MM-dd HH24:MI:SS') fecha_modificacion_str,"
                    + " CASE WHEN rotativo THEN 'S' ELSE 'N' END rotativo "
                + " FROM turno where empresa_id='"+_empresaId+"' ";
           
            sql += " order by nombre_turno";
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnosDAO.getTurnosByEmpresa]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new TurnoVO();
                data.setId(rs.getInt("id_turno"));
                data.setNombre(rs.getString("nombre_turno"));
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setEstado(rs.getInt("estado_turno"));
                
                data.setFechaCreacion(rs.getDate("fecha_creacion"));
                data.setFechaCreacionAsStr(rs.getString("fecha_creacion_str"));
                data.setFechaModificacion(rs.getDate("fecha_modificacion"));
                data.setFechaModificacionAsStr(rs.getString("fecha_modificacion_str"));
                data.setRotativo(rs.getString("rotativo"));
                
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
    
    /**
     * Retorna lista con los turnos asignados a empleados 
     * pertenecientes a alguno de los cencos especificados
     * 
     * @param _empresaId
     * @param _cencosUsuario
     * @return 
     */
    public List<TurnoVO> getTurnosByCencos(String _empresaId, 
            List<UsuarioCentroCostoVO> _cencosUsuario){
        
        List<TurnoVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        TurnoVO data;
        
        try{
            String strCencos = "";
            Iterator<UsuarioCentroCostoVO> it = _cencosUsuario.iterator();
            while(it.hasNext()){
                UsuarioCentroCostoVO cenco=it.next();
                strCencos += cenco.getCcostoId()+",";
            }
            if (!_cencosUsuario.isEmpty()){
                strCencos = strCencos.substring(0, strCencos.length() - 1);
            }
            String sql = "SELECT "
                    + "distinct(id_turno) id_turno, "
                    + "nombre_turno,"
                    + "turno.empresa_id,"
                    + "estado_turno,"
                    + "fecha_creacion,"
                    + "fecha_modificacion,"
                    + "to_char(fecha_creacion, 'yyyy-MM-dd HH24:MI:SS') fecha_creacion_str, "
                    + "to_char(fecha_modificacion, 'yyyy-MM-dd HH24:MI:SS') fecha_modificacion_str,"
                    + " CASE WHEN rotativo THEN 'S' ELSE 'N' END rotativo "
                + "FROM turno "
                    + "inner join empleado "
                    + "on (turno.id_turno = empleado.empl_id_turno and empleado.empresa_id='"+_empresaId+"') "
                + "where empleado.cenco_id in (" + strCencos + ") "
                + "order by nombre_turno"; 
                    
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnosDAO.getTurnosByCencos]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new TurnoVO();
                data.setId(rs.getInt("id_turno"));
                data.setNombre(rs.getString("nombre_turno"));
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setEstado(rs.getInt("estado_turno"));
                
                data.setFechaCreacion(rs.getDate("fecha_creacion"));
                data.setFechaCreacionAsStr(rs.getString("fecha_creacion_str"));
                data.setFechaModificacion(rs.getDate("fecha_modificacion"));
                data.setFechaModificacionAsStr(rs.getString("fecha_modificacion_str"));
                data.setRotativo(rs.getString("rotativo"));
                
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
    
    /**
    * 
     * @param _idTurno
     * @return 
    */
    public TurnoVO getTurno(int _idTurno){
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        TurnoVO data=null;
        
        try{
            String sql ="SELECT "
                    + "id_turno, "
                    + "nombre_turno,"
                    + "empresa_id, "
                    + "estado_turno, "
                    + "fecha_creacion,"
                    + "fecha_modificacion,"
                    + "to_char(fecha_creacion, 'yyyy-MM-dd HH24:MI:SS') fecha_creacion_str, "
                    + "to_char(fecha_modificacion, 'yyyy-MM-dd HH24:MI:SS') fecha_modificacion_str,"
                    + " CASE WHEN rotativo THEN 'S' ELSE 'N' END rotativo "
                + "FROM turno "
                + "where id_turno= "+_idTurno;
            
//            System.out.println(WEB_NAME+"[TurnosDAO.getTurno]sql: "+sql);
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnosDAO.getTurno]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                data = new TurnoVO();
                data.setId(rs.getInt("id_turno"));
                data.setNombre(rs.getString("nombre_turno"));
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setEstado(rs.getInt("estado_turno"));
                
                data.setFechaCreacion(rs.getDate("fecha_creacion"));
                data.setFechaCreacionAsStr(rs.getString("fecha_creacion_str"));
                data.setFechaModificacion(rs.getDate("fecha_modificacion"));
                data.setFechaModificacionAsStr(rs.getString("fecha_modificacion_str"));
                
                data.setRotativo(rs.getString("rotativo"));
                
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
        
        return data;
    }
    
    /**
     * 
     * @param _empresaId
     * @return 
     */
    public int getTurnoRotativo(String _empresaId){
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        int idTurno=0;
        try{
            String sql ="SELECT "
                + "id_turno "
                + "FROM turno "
                + "where rotativo= true and empresa_id='"+_empresaId+"'";
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnosDAO.getTurnoRotativo]empresa_id="+_empresaId);
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                idTurno = rs.getInt("id_turno");
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
        
        return idTurno;
    }
    
    /**
    * 
     * @param _idTurno
     * @param _empresaId
     * @param _deptoId
     * @param _cencoId
     * @param cargoId
     * @return 
    */
    public List<EmpleadoVO> getEmpleados(int _idTurno,
            String _empresaId,
            String _deptoId,
            int _cencoId,
            int cargoId){
        
        List<EmpleadoVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        EmpleadoVO data;
        
        try{
            String sql = "select empl.empl_rut rut,"
                + "coalesce(empl.empl_nombres, '') || ' ' || "
                + "coalesce(empl.empl_ape_paterno, '') nombre "
            + "FROM "
                + "empleado empl,"
                + "empresa,"
                + "departamento depto,"
                + "centro_costo cenco "
            + "where empl_id_turno = " + _idTurno
                + " AND empl.empresa_id = empresa.empresa_id "
                + " AND empl.depto_id = depto.depto_id "
                + " AND empl.cenco_id = cenco.ccosto_id ";

            if (_empresaId!=null && _empresaId.compareTo("-1")!=0){        
                sql += " and empl.empresa_id= '"+_empresaId+"'";
            }
            if (_deptoId!=null && _deptoId.compareTo("-1")!=0){        
                sql += " and empl.depto_id = '"+_deptoId+"' ";
            }
            if (_cencoId != -1){        
                sql += " and empl.cenco_id = "+_cencoId+" ";
            }
            if (cargoId != -1){        
                sql += " and empl_id_cargo = "+cargoId+" ";
            }
            
//            System.out.println(WEB_NAME+"cl.femase.gestionweb."
//                + "service.TurnosSrv.getEmpleados()."
//                + " SQL: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnosDAO.getEmpleados]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new EmpleadoVO();
                data.setRut(rs.getString("rut"));
                data.setNombres(rs.getString("nombre"));
                               
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
    
    /**
    * 
     * @param _idTurno
     * @param _empresaId
     * @param _deptoId
     * @param _cencoId
     * @param cargoId
     * @return 
    */
    public List<EmpleadoVO> getEmpleadosNoAsignados(int _idTurno,
            String _empresaId,
            String _deptoId,
            int _cencoId,
            int cargoId){
        
        List<EmpleadoVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        EmpleadoVO data;
        
        try{
            String sql ="select empl.empl_rut rut,"
                + "coalesce(empl.empl_nombres, '') || ' ' || "
                + "coalesce(empl.empl_ape_paterno, '') nombre "
                + "FROM "
                    + "empleado empl,"
                    + "empresa,"
                    + "departamento depto,"
                    + "centro_costo cenco "
                + "where empl_id_turno <> "+_idTurno
                + " AND empl.empresa_id = empresa.empresa_id AND "
                    + "empl.depto_id = depto.depto_id AND "
                    + "empl.cenco_id = cenco.ccosto_id";
            
            if (_empresaId!=null && _empresaId.compareTo("-1")!=0){        
                sql += " and empl.empresa_id= '"+_empresaId+"'";
            }
            if (_deptoId!=null && _deptoId.compareTo("-1")!=0){        
                sql += " and empl.depto_id = '"+_deptoId+"' ";
            }
            if (_cencoId != -1){        
                sql += " and empl.cenco_id = "+_cencoId+" ";
            }
            if (cargoId != -1){        
                sql += " and empl_id_cargo = "+cargoId+" ";
            }
           
            sql += " order by empl.empl_rut";
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnosDAO.getEmpleadosNoAsignados]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new EmpleadoVO();
                data.setRut(rs.getString("rut"));
                data.setNombres(rs.getString("nombre"));
                               
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
    
    /**
    * 
    * @param _empresaId
    * @param _nombre
    * @param _estado
    * @return 
    */
    public int getTurnosCount(String _empresaId, String _nombre, int _estado){
        int count=0;
        Statement statement = null;
        ResultSet rs = null;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnosDAO.getTurnosCount]");
            statement = dbConn.createStatement();
            String strSql ="SELECT count(id_turno) "
                + "FROM turno where 1=1 ";
               
            if (_nombre!=null && _nombre.compareTo("")!=0){        
                strSql += " and upper(nombre_turno) like '%"+_nombre.toUpperCase()+"%'";
            }
            if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
                strSql += " and empresa_id = '" + _empresaId + "'";
            }
            if (_estado != -1){        
                strSql += " and estado_turno = " + _estado;
            }
            rs = statement.executeQuery(strSql);		
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
                if (statement!= null) statement.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
        
        return count;
    }
   
}
