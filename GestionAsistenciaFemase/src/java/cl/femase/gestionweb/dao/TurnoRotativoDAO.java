/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.vo.AsignacionTurnoRotativoVO;
import cl.femase.gestionweb.vo.DetalleTurnoVO;
import cl.femase.gestionweb.vo.DiferenciaHorasVO;
import cl.femase.gestionweb.vo.DuracionVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.TurnoRotativoVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexander
 */
public class TurnoRotativoDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    SimpleDateFormat m_sdf = new SimpleDateFormat("yyyy-MM-dd"); 
    
    public TurnoRotativoDAO(PropertiesVO _propsValues) {

    }

    /**
     * Actualiza un turno para un empleado
     * @param _rutEmpleado
     * @param _idTurno
     * @return 
     */
    public MaintenanceVO updateTurnoEmpleado(String _rutEmpleado,int _idTurno){
        MaintenanceVO objresultado = new MaintenanceVO();
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

            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnoRotativoDAO.updateTurnoEmpleado]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setInt(1,  _idTurno);
            psupdate.setString(2,  _rutEmpleado);
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println("[TurnoRotativoDAO.updateTurnoEmpleado]"
                    + "rut:" +_rutEmpleado+", idTurno= "+_idTurno
                    +" actualizado OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("TurnoRotativoDAO.updateTurnoEmpleado Error: "+sqle.toString());
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
     * Actualiza minutos de colacion y holgura
     * para todos los turnos rotativos
     * @param _turno
     * @return 
     */
    public boolean updateTodos(TurnoRotativoVO _turno){
        boolean isOk=true;
        PreparedStatement psupdate = null;
        
        try{
            String sql = "UPDATE turno_rotativo "
                + " SET "
                    + "fecha_modificacion = current_timestamp,"
                    + "minutos_colacion = ?, "
                    + "holgura=?  ";
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnoRotativoDAO.updateTodos]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setInt(1,  _turno.getMinutosColacion());
            psupdate.setInt(2,  _turno.getHolgura());
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println("[TurnoRotativoDAO.updateTodos]turno_rotativo"
                    + ", id:" +_turno.getId()
                    +" actualizado OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[TurnoRotativoDAO.updateTodos]Error: " + sqle.toString());
            isOk=false;
        }finally{
            try {
                if (psupdate != null) psupdate.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
        
        return isOk;
    }
    
    /**
     * Actualiza un turno rotativo
     * @param _data
     * @return 
     */
    public MaintenanceVO update(TurnoRotativoVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "Turno, "
            + "id: " + _data.getId()
            + ", nombre: " + _data.getNombre()
            + ", hora_entrada: " + _data.getHoraEntrada()
            + ", hora_salida: " + _data.getHoraSalida()
            + ", idEmpresa: " + _data.getEmpresaId()
                + ", minsColacion: " + _data.getMinutosColacion()
                + ", minsHolgura: " + _data.getHolgura()
                + ", nocturno: " + _data.getNocturno();
        
        try{
            String msgFinal = " Actualiza turno_rotativo:"
                + "id [" + _data.getId() + "]" 
                + ", nombre [" + _data.getNombre() + "]"
                + ", id empresa [" + _data.getEmpresaId() + "]"
                + ", hora_entrada [" + _data.getHoraEntrada() + "]"
                + ", hora_salida [" + _data.getHoraSalida() + "]"
                + ", minsColacion [" + _data.getMinutosColacion() + "]"
                + ", minsHolgura [" + _data.getHolgura() + "]"
                + ", nocturno [" + _data.getNocturno()+ "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE turno_rotativo "
                + " SET "
                    + "nombre_turno = ?, "
                    + "estado_turno=?,"
                    + "fecha_modificacion = current_timestamp,"
                    + "empresa_id = ?,"
                    + "minutos_colacion = ?, "
                    + "hora_entrada = ?, "
                    + "hora_salida = ?,"
                    + "holgura=?, "
                    + "turno_nocturno=? ";
            //if (_data.getAplicarTodos().compareTo("N") == 0){
                sql += "WHERE id_turno = ?";
            //}
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnoRotativoDAO.update]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setString(1,  _data.getNombre());
            psupdate.setInt(2,  _data.getEstado());
            psupdate.setString(3,  _data.getEmpresaId());
            psupdate.setInt(4,  _data.getMinutosColacion());
            psupdate.setTime(5,  getHora(_data.getHoraEntrada()));
            psupdate.setTime(6,  getHora(_data.getHoraSalida()));
            psupdate.setInt(7,  _data.getHolgura());
            psupdate.setString(8,  _data.getNocturno());
            psupdate.setInt(9,  _data.getId());
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println("[TurnoRotativoDAO.update]turno_rotativo"
                    + ", id:" +_data.getId()
                    + ", nombre:" +_data.getNombre()
                    +" actualizado OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("update turno_rotativo Error: "+sqle.toString());
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
     * Agrega un nuevo turno rotativo
     * @param _data
     * @return 
     */
    public MaintenanceVO insert(TurnoRotativoVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al insertar "
            + "turno rotativo. "
            + " empresaId:" + _data.getEmpresaId()
            + ",nombre:" + _data.getNombre()
            + ",hra entrada:" + _data.getHoraEntrada()
            + ",hra salida:" + _data.getHoraSalida()
            + ",mins colacion:" + _data.getMinutosColacion()
            + ",mins holgura:" + _data.getHolgura()
            + ",nocturno?:" + _data.getNocturno();
        
        String msgFinal = " Inserta turno rotativo:"
            + "empresaId [" + _data.getEmpresaId() + "]"
            + ", nombre [" + _data.getNombre() + "]"
            + ", hraEntrada [" + _data.getHoraEntrada() + "]"
            + ", hraSalida [" + _data.getHoraSalida() + "]"
            + ", minsColacion [" + _data.getMinutosColacion() + "]"
            + ", minsHolgura [" + _data.getHolgura() + "]"
            + ", nocturno [" + _data.getNocturno() + "]";
        
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO turno_rotativo ("
                +"id_turno, "
                + "nombre_turno, "
                + "estado_turno, "
                + "fecha_creacion, "
                + "fecha_modificacion, "
                + "empresa_id,"
                + "minutos_colacion,"
                + "hora_entrada,"
                + "hora_salida,"
                + "holgura,"
                + "turno_nocturno) "
                + " VALUES (nextval('turno_id_seq'), ?, ?, "
                    + "current_timestamp, "
                    + "current_timestamp, ?, ?, "
                    + "?, ?, ?, ?)";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnoRotativoDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getNombre());
            insert.setInt(2,  _data.getEstado());
            insert.setString(3,  _data.getEmpresaId());
            insert.setInt(4,  _data.getMinutosColacion());
            
            insert.setTime(5,  getHora(_data.getHoraEntrada()));
            insert.setTime(6,  getHora(_data.getHoraSalida()));
            insert.setInt(7,  _data.getHolgura());
            insert.setString(8,  _data.getNocturno());
            
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println("[TurnoRotativoDAO.insert turno_rotativo]"
                    + ", nombre:" +_data.getNombre()
                    + ", id:" +_data.getId()
                    +" insertado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("TurnoRotativoDAO.insert turno_rotativo Error1: "+sqle.toString());
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
     * Retorna lista de turnos sin detalle de turno definido
     * 
     * @param _empresaId
     * @return 
     */
    public List<TurnoRotativoVO> getTurnosSinDetalle(String _empresaId){
        List<TurnoRotativoVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        TurnoRotativoVO data;
        
        try{
            String sql = "select "
                + "id_turno, "
                + "empresa_id,"
                + "nombre_turno,"
                + "hora_entrada,"
                + "hora_salida "
                + "from turno_rotativo turno "
                + "where turno.empresa_id = '" + _empresaId + "' "
                + "and turno.estado_turno = 1 "
                + "and id_turno not in (select id_turno "
                    + "from turno_rotativo_detalle "
                    + "where id_turno = turno.id_turno) "
                + "order by nombre_turno";
            System.out.println("[TurnoRotativoDAO.getTurnosSinDetalle]"
                + "Mostrar turnos sin detalle empleados para empresa: "+_empresaId);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnoRotativoDAO.getTurnosSinDetalle]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new TurnoRotativoVO();
                data.setId(rs.getInt("id_turno"));
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setNombre(rs.getString("nombre_turno"));
                data.setHoraEntrada(rs.getString("hora_entrada"));
                data.setHoraSalida(rs.getString("hora_salida"));
                
                lista.add(data);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("TurnoRotativoDAO.getTurnosSinDetalle.Error: "+sqle.toString());
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
     * Retorna lista de turnos con detalle de turno definido
     * 
     * @param _empresaId
     * @return 
     */
    public List<TurnoRotativoVO> getTurnosConDetalle(String _empresaId){
        List<TurnoRotativoVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        TurnoRotativoVO data;
        
        try{
            String sql = "select "
                + "id_turno, "
                + "empresa_id,"
                + "nombre_turno,"
                + "hora_entrada,"
                + "hora_salida "
                + "from turno_rotativo turno "
                + "where turno.empresa_id = '" + _empresaId + "' "
                + "and turno.estado_turno = 1 "
                + "and id_turno in (select id_turno "
                    + "from turno_rotativo_detalle "
                    + "where id_turno = turno.id_turno) "
                + "order by nombre_turno";
            System.out.println("[TurnoRotativoDAO.getTurnosConDetalle]"
                + "Mostrar turnos con detalle empleados para empresa: "+_empresaId);
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnoRotativoDAO.getTurnosConDetalle]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new TurnoRotativoVO();
                data.setId(rs.getInt("id_turno"));
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setNombre(rs.getString("nombre_turno"));
                data.setHoraEntrada(rs.getString("hora_entrada"));
                data.setHoraSalida(rs.getString("hora_salida"));
                
                lista.add(data);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("TurnoRotativoDAO.getTurnosConDetalle.Error: "+sqle.toString());
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
    * Retorna lista con los turnos rotativos existentes en el sistema
    * 
    * @param _empresaId
    * @param _nombreTurno
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * @return 
    */
    public List<TurnoRotativoVO> getTurnos(String _empresaId, 
            String _nombreTurno,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<TurnoRotativoVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        TurnoRotativoVO data;
        
        try{
            String sql ="SELECT "
                + "empresa_id, "
                + "id_turno, "
                + "nombre_turno,"
                + "estado_turno, "
                + "hora_entrada,"
                + "hora_salida,"
                + "fecha_creacion,"
                + "fecha_modificacion,"
                + "to_char(fecha_creacion, 'yyyy-MM-dd HH24:MI:SS') fecha_creacion_str, "
                + "to_char(fecha_modificacion, 'yyyy-MM-dd HH24:MI:SS') fecha_modificacion_str,"
                + "minutos_colacion,"
                + "holgura,"
                + "turno_nocturno "
                + "FROM turno_rotativo where 1=1 ";
           
            if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
                sql += " and empresa_id = '" + _empresaId + "' ";
            }
            
            if (_nombreTurno != null && _nombreTurno.compareTo("") != 0){        
                sql += " and upper(nombre_turno) like '%" + _nombreTurno.toUpperCase() + "%'";
            }
           
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            System.out.println("[Gestion.TurnoRotativoDAO.getTurnos]sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnoRotativoDAO.getTurnos]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();
            String nombreTurno = ""; 
            while (rs.next()){
                data = new TurnoRotativoVO();
                data.setId(rs.getInt("id_turno"));
                nombreTurno = rs.getString("nombre_turno");
                
                data.setNombre(nombreTurno);
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setEstado(rs.getInt("estado_turno"));
                data.setHoraEntrada(rs.getString("hora_entrada"));
                data.setHoraSalida(rs.getString("hora_salida"));
                data.setFechaCreacion(rs.getDate("fecha_creacion"));
                data.setFechaCreacionAsStr(rs.getString("fecha_creacion_str"));
                data.setFechaModificacion(rs.getDate("fecha_modificacion"));
                data.setFechaModificacionAsStr(rs.getString("fecha_modificacion_str"));
                data.setMinutosColacion(rs.getInt("minutos_colacion"));
                data.setHolgura(rs.getInt("holgura"));
                data.setNocturno(rs.getString("turno_nocturno"));
                
                lista.add(data);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("TurnoRotativoDAO.getTurnos.Error: "+sqle.toString());
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
    
    public TurnoRotativoVO getTurno(int _idTurno){
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        TurnoRotativoVO data=null;
        
        try{
           String sql ="SELECT "
                + "empresa_id, "
                + "id_turno, "
                + "nombre_turno,"
                + "estado_turno, "
                + "hora_entrada,"
                + "hora_salida,"
                + "fecha_creacion,"
                + "fecha_modificacion,"
                + "to_char(fecha_creacion, 'yyyy-MM-dd HH24:MI:SS') fecha_creacion_str, "
                + "to_char(fecha_modificacion, 'yyyy-MM-dd HH24:MI:SS') fecha_modificacion_str,"
                + "minutos_colacion,holgura "
                + "FROM turno_rotativo "
                + "where id_turno= "+_idTurno;

            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnoRotativoDAO.getTurno]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                data = new TurnoRotativoVO();
                data.setId(rs.getInt("id_turno"));
                data.setNombre(rs.getString("nombre_turno"));
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setEstado(rs.getInt("estado_turno"));
                data.setHoraEntrada(rs.getString("hora_entrada"));
                data.setHoraSalida(rs.getString("hora_salida"));
                
                data.setFechaCreacion(rs.getDate("fecha_creacion"));
                data.setFechaCreacionAsStr(rs.getString("fecha_creacion_str"));
                data.setFechaModificacion(rs.getDate("fecha_modificacion"));
                data.setFechaModificacionAsStr(rs.getString("fecha_modificacion_str"));
                data.setMinutosColacion(rs.getInt("minutos_colacion"));
                data.setHolgura(rs.getInt("holgura"));
                
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("TurnoRotativoDAO.getTurno.Error: "+sqle.toString());
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
     * Retorna lista de empleados para el turno especificado
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
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnoRotativoDAO.getEmpleados]");
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
            m_logger.error("TurnoRotativoDAO.getEmpleados.Error: "+sqle.toString());
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
     * Retorna lista de empleados no asignados para el turno especificado
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
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnoRotativoDAO.getEmpleadosNoAsignados]");
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
            m_logger.error("TurnoRotativoDAO.getEmpleadosNoAsignados.Error: "+sqle.toString());
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
     * @param _nombreTurno
     * @return 
     */
    public int getTurnosCount(String _empresaId,String _nombreTurno){
        int count=0;
        Statement statement = null;
        ResultSet rs        = null;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnoRotativoDAO.getTurnosCount]");
            statement = dbConn.createStatement();
            String strSql ="SELECT count(id_turno) "
                + "FROM turno_rotativo where 1=1 ";
               
            if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
                strSql += " and empresa_id = '" + _empresaId + "' ";
            }
            
            if (_nombreTurno != null && _nombreTurno.compareTo("") != 0){        
                strSql += " and upper(nombre_turno) like '%" + _nombreTurno.toUpperCase() + "%'";
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
     * @param _deptoId
     * @param _cencoId
     * 
     * @return 
     */
    public int getEmpleadosConTurnoRotativoCount(String _empresaId, 
            String _deptoId, 
            int _cencoId){
        int count=0;
        Statement statement = null;
        ResultSet rs = null;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnoRotativoDAO.getEmpleadosConTurnoRotativoCount]");
            statement = dbConn.createStatement();
            String sql = "select count(empl_rut) count "
                 + "from empleado "
                + "inner join turno_rotativo_asignacion asignacion "
                + " on (empleado.empresa_id=asignacion.empresa_id and empleado.empl_rut = asignacion.rut_empleado) "
                    + "inner join turno_rotativo turno on (asignacion.id_turno = turno.id_turno) "
                    + "where "
                    + "empleado.empresa_id = '" +_empresaId + "'"
                    + " and empleado.depto_id = '" + _deptoId + "'"
                    + " and empleado.cenco_id = " + _cencoId  
                    + " and empleado.empl_id_turno = 21 "
                    + " and current_date between asignacion.fecha_desde and asignacion.fecha_hasta ";
            
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
    
    
    //nuevos metodos
    
//    /** 12-05-2018
//     *  Retorna lista con las asignaciones de turnos rotativos existentes
//     * @param _empresaId
//     * @param _rutEmpleado
//     * @param _idTurno
//     * @param _fechaDesde
//     * @param _fechaHasta
//     * @return 
//     */
//    public ArrayList<AsignacionTurnoRotativoVO> getAsignacion(String _empresaId, 
//            String _rutEmpleado, 
//            int  _idTurno, 
//            String _fechaDesde, 
//            String _fechaHasta){
//    
//        ArrayList<AsignacionTurnoRotativoVO> asignaciones= 
//            new ArrayList<>();
//
//        AsignacionTurnoRotativoVO asignacion;
//        try {
//            dbConn = dbLocator.getConnection(m_dbpoolName);
//            Statement statement = dbConn.createStatement();
//            String strSql = "select "
//                    + "turno.empresa_id,"
//                    + "asignacion.rut_empleado,"
//                    + "asignacion.id_turno, "
//                    + "asignacion.id_duracion,"
//                    + "turno.nombre_turno, "
//                    + "fecha_desde,"
//                    + "fecha_hasta,"
//                    + "turno.hora_entrada,"
//                    + "turno.hora_salida "
//                + "from turno_rotativo_asignacion asignacion "
//                + "inner join turno_rotativo turno "
//                    + "on (asignacion.id_turno = turno.id_turno) "
//                + "where asignacion.empresa_id='" + _empresaId + "' "
//                    + "and asignacion.rut_empleado='" + _rutEmpleado + "' ";
//                
//            if (_idTurno != 0 && _fechaDesde != null && _fechaHasta != null ){
//                    strSql += " and asignacion.id_turno="+_idTurno
//                        + " and asignacion.fecha_desde::date='" +_fechaDesde +"' "
//                        + " and asignacion.fecha_hasta::date='" +_fechaHasta +"' ";
//            }
//            strSql += "order by fecha_desde desc";
//            
//            ResultSet rs = statement.executeQuery(strSql);		
//            while (rs.next()) {
//                asignacion = new AsignacionTurnoRotativoVO();
//                
//                asignacion.setEmpresaId(rs.getString("empresa_id"));
//                asignacion.setRutEmpleado(rs.getString("rut_empleado"));
//                asignacion.setIdTurno(rs.getInt("id_turno"));
//                asignacion.setIdPeriodo(rs.getInt("id_duracion"));
//                asignacion.setHoraEntrada(rs.getString("hora_entrada"));
//                asignacion.setHoraSalida(rs.getString("hora_salida"));
//                
//                asignaciones.add(asignacion);
//            }
//            
//            statement.close();
//            rs.close();
//            dbLocator.freeConnection(dbConn);
//        } catch (SQLException|DatabaseException e) {
//            e.printStackTrace();
//        }
//        
//        return asignaciones;
//    }
    
    /** 13-05-2018
     *  
     *  Retorna lista con las duraciones predeterminadas de turnos
     * 
     * @return 
     */
    public ArrayList<DuracionVO> getDuraciones(){
        ArrayList<DuracionVO> duraciones = new ArrayList<>();
        DuracionVO duracion;
        Statement statement = null;
        ResultSet rs = null;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnoRotativoDAO.getDuraciones]");
            statement = dbConn.createStatement();
            String strSql = "SELECT "
                + "id_duracion, "
                + "nombre_duracion, "
                + "num_dias, "
                + "order_display "
                + "FROM duracion "
                + "order by order_display";
            
            rs = statement.executeQuery(strSql);		
            while (rs.next()) {
                duracion = new DuracionVO();
                
                duracion.setId(rs.getInt("id_duracion"));
                duracion.setNombre(rs.getString("nombre_duracion"));
                duracion.setNumDias(rs.getInt("num_dias"));
                
                duraciones.add(duracion);
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
        
        return duraciones;
    }
    
    /**
     * Obtiene lista de turnos rotativos asignados al rut/empresa especificados
     * 
     * @param _empresaId
     * @param _rutEmpleado
     * @param _idTurno
     * @param _fechaDesde
     * @param _fechaHasta
     * @param _limit
     * @return 
     */
    public List<AsignacionTurnoRotativoVO> getAsignacionTurnosRotativosByRut(String _empresaId,
            String _rutEmpleado, 
            int  _idTurno, 
            String _fechaDesde, 
            String _fechaHasta, 
            int _limit){
        
        List<AsignacionTurnoRotativoVO> lista = new ArrayList<>();
        
        SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
        PreparedStatement ps = null;
        ResultSet rs = null;
        AsignacionTurnoRotativoVO asignacion;
        
        try{
            String sql = "select "
                + "empresa.empresa_id,"
                + "empl_rut,"
                + "cod_interno,"
                + "empl_nombres || ' ' || empl_ape_paterno || ' ' || "
                + "empl_ape_materno nombre_empleado,"
                + "asignacion.id_turno,"
                + "asignacion.id_duracion,"
                + "duracion.num_dias,"
                + "trim(turno.nombre_turno) nombre_turno,"
                + "to_char(asignacion.fecha_desde,'YYYY-MM-DD') fecha_desde,"
                + "to_char(asignacion.fecha_hasta,'YYYY-MM-DD') fecha_hasta,"
                + "turno.hora_entrada,turno.hora_salida "
                + "from empleado "
                + "inner join turno_rotativo_asignacion asignacion  on (empleado.empresa_id=asignacion.empresa_id and empleado.empl_rut = asignacion.rut_empleado) "
                + "inner join turno_rotativo turno on (asignacion.id_turno = turno.id_turno) "
                + "inner join empresa on (empleado.empresa_id = empresa.empresa_id) "
                + "inner join duracion on (asignacion.id_duracion = duracion.id_duracion) "
                + "where empleado.empresa_id = '" + _empresaId + "' "
                + "and empl_rut='" + _rutEmpleado + "' ";
            
            if (_idTurno != 0 && _fechaDesde != null && _fechaHasta != null ){
                    sql += " and asignacion.id_turno="+_idTurno
                        + " and asignacion.fecha_desde::date='" +_fechaDesde +"' "
                        + " and asignacion.fecha_hasta::date='" +_fechaHasta +"' ";
            }
            
            sql += "order by fecha_hasta desc ";
            if (_limit > 0) sql += " limit " + _limit;
                        
            System.out.println("[TurnoRotativoDAO."
                + "getAsignacionTurnosRotativosByRut]SQL: " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnoRotativoDAO.getAsignacionTurnosRotativosByRut]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();
            String nombreTurno="";
            while (rs.next()){
                asignacion = new AsignacionTurnoRotativoVO();
                nombreTurno = rs.getString("nombre_turno");
                
                //if (nombreTurno != null 
                  //      && (nombreTurno.length() <= 8 || nombreTurno.length() <= 9)){
                    nombreTurno += " - " 
                        + rs.getString("hora_entrada")+" a " 
                        + rs.getString("hora_salida");
                //}
                asignacion.setNombreTurno(nombreTurno);
                
                asignacion.setEmpresaId(rs.getString("empresa_id"));
                asignacion.setRutEmpleado(rs.getString("empl_rut"));
                asignacion.setNumFicha(rs.getString("cod_interno"));
                asignacion.setNombreEmpleado(rs.getString("nombre_empleado"));
                asignacion.setIdTurno(rs.getInt("id_turno"));
                asignacion.setIdDuracion(rs.getInt("id_duracion"));
                asignacion.setDiasDuracion(rs.getInt("num_dias"));
                
                asignacion.setFechaDesde(rs.getString("fecha_desde"));
                asignacion.setFechaHasta(rs.getString("fecha_hasta"));
                
                lista.add(asignacion);
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
     * Este metodo se usa para verificar si las fechas ingresadas al crear/modificar
     * se intersectan con alguno de los turnos asignados existentes. 
     * 
     * @param _empresaId
     * @param _rutEmpleado
     * @param _idTurno
     * @param _fechaDesde
     * @param _fechaHasta
     * @return 
     */
    public List<AsignacionTurnoRotativoVO> getAsignacionesConflicto(String _empresaId,
            String _rutEmpleado,
            int _idTurno,
            String _fechaDesde, 
            String _fechaHasta){
        
        List<AsignacionTurnoRotativoVO> lista = new ArrayList<>();
        
        SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
        PreparedStatement ps = null;
        ResultSet rs = null;
        AsignacionTurnoRotativoVO asignacion;
        
        try{
            String sql = "select "
                + "empresa.empresa_id,"
                + "empl_rut,"
                + "cod_interno,"
                + "empl_nombres || ' ' || empl_ape_paterno || ' ' || "
                + "empl_ape_materno nombre_empleado,"
                + "asignacion.id_turno,"
                + "asignacion.id_duracion,"
                + "turno.nombre_turno,"
                + "to_char(asignacion.fecha_desde,'YYYY-MM-DD') fecha_desde,"
                + "to_char(asignacion.fecha_hasta,'YYYY-MM-DD') fecha_hasta "
                + "from empleado "
                + "inner join turno_rotativo_asignacion asignacion  "
                + "on (empleado.empresa_id=asignacion.empresa_id "
                + "and empleado.empl_rut = asignacion.rut_empleado) "
                + "inner join turno_rotativo turno "
                + "on (asignacion.id_turno = turno.id_turno) "
                + "inner join empresa on (empleado.empresa_id = empresa.empresa_id) "
                + "where empleado.empresa_id = '" + _empresaId + "' "
                + "and empl_rut='" + _rutEmpleado + "' "
                + " and '["+_fechaDesde+", "+_fechaHasta+"]'::daterange @> fecha_desde ";
//                    + "and ('"+_fechaDesde+"' between fecha_desde::date and fecha_hasta::date or " +
//                "'"+_fechaHasta+"' between fecha_desde::date and fecha_hasta::date) ";
            
            if (_idTurno > 0){
                sql += " and asignacion.id_turno <> " + _idTurno;
            }
            
            sql += "order by fecha_hasta desc";
                        
            System.out.println("[TurnoRotativoDAO."
                + "getAsignacionesConflicto]SQL: " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnoRotativoDAO.getAsignacionesConflicto]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                asignacion = new AsignacionTurnoRotativoVO();
                
                asignacion.setEmpresaId(rs.getString("empresa_id"));
                asignacion.setRutEmpleado(rs.getString("empl_rut"));
                asignacion.setNumFicha(rs.getString("cod_interno"));
                asignacion.setNombreEmpleado(rs.getString("nombre_empleado"));
                asignacion.setIdTurno(rs.getInt("id_turno"));
                asignacion.setIdDuracion(rs.getInt("id_duracion"));
                asignacion.setNombreTurno(rs.getString("nombre_turno"));
                asignacion.setFechaDesde(rs.getString("fecha_desde"));
                asignacion.setFechaHasta(rs.getString("fecha_hasta"));
                
                lista.add(asignacion);
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
    
    public void insertarAsignaciones(ArrayList<AsignacionTurnoRotativoVO> _asignaciones) throws SQLException{
        
        try{
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnoRotativoDAO.insertarAsignaciones]");
            int i = 0;
            System.out.println("[TurnoRotativoDAO.insertarAsignaciones]"
                + "items a insertar: "+_asignaciones.size());
            String sqlInsert = "INSERT INTO turno_rotativo_asignacion("
                + "empresa_id, "
                + "rut_empleado, "
                + "id_turno, "
                + "id_duracion, "
                + "fecha_desde," // duracion de 1 dia
                + "fecha_hasta, "
                + "fecha_asignacion, "
                + "fecha_ultima_actualizacion) "
            + " VALUES ("
                + "?, "
                + "?, "
                + "?, "
                + "?, "//duracion de 1 dia
                + "?,"
                + "?,"
                + "current_timestamp, "
                + "current_timestamp)";
            PreparedStatement statement = dbConn.prepareStatement(sqlInsert);
            
            for (AsignacionTurnoRotativoVO asignacion : _asignaciones) {
                System.out.println("[TurnoRotativoDAO.insertarAsignaciones] "
                    + "Insert asignacion turno. "
                    + "EmpresaId= " + asignacion.getEmpresaId()    
                    + ",Rut= " + asignacion.getRutEmpleado()
                    + ", idTurno= " + asignacion.getIdTurno()
                    + ", idDuracion= " + asignacion.getIdDuracion()    
                    + ", fechaDesde= " + asignacion.getFechaDesde()
                    + ", fechaHasta= " + asignacion.getFechaHasta());
                
                statement.setString(1,  asignacion.getEmpresaId());
                statement.setString(2,  asignacion.getRutEmpleado());
                statement.setInt(3,  asignacion.getIdTurno());
                statement.setInt(4,  asignacion.getIdDuracion());
                
                statement.setDate(5,  Utilidades.getJavaSqlDate(asignacion.getFechaDesde(),"yyyy-MM-dd"));
                statement.setDate(6,  Utilidades.getJavaSqlDate(asignacion.getFechaHasta(),"yyyy-MM-dd"));
                
                // ...
                statement.addBatch();
                i++;

                if (i % 50 == 0 || i == _asignaciones.size()) {
                    int[] rowsAffected = statement.executeBatch(); // Execute every 1000 items.
                    System.out.println("[TurnoRotativoDAO."
                        + "insertarAsignaciones]"
                        + "filas afectadas= "+rowsAffected.length);
                }
            }
            dbLocator.freeConnection(dbConn);
        }catch(SQLException ex){
            System.err.println("[TurnoRotativoDAO."
                + "insertarAsignaciones]Error= " + ex.toString());
            ex.printStackTrace();
            dbLocator.freeConnection(dbConn);
        }catch(DatabaseException dbex){
            System.err.println("[TurnoRotativoDAO."
                + "insertarAsignaciones]Error= " + dbex.toString());
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
    
    public void eliminarAsignaciones(ArrayList<AsignacionTurnoRotativoVO> _asignaciones) throws SQLException{
        
        try{
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnoRotativoDAO.eliminarAsignaciones]");
            int i = 0;
            System.out.println("[TurnoRotativoDAO.eliminarAsignaciones]"
                + "items a eliminar: " + _asignaciones.size());
            String sqlDelete = "delete from turno_rotativo_asignacion "
                + "where "
                + "empresa_id = ? "
                + "and rut_empleado = ? "
                + "and fecha_desde::date = ?";
            PreparedStatement statement = dbConn.prepareStatement(sqlDelete);
            
            for (AsignacionTurnoRotativoVO asignacion : _asignaciones) {
                System.out.println("[TurnoRotativoDAO.eliminarAsignaciones] "
                    + "Eliminar asignacion turno. "
                    + "EmpresaId= " + asignacion.getEmpresaId()    
                    + ",Rut= " + asignacion.getRutEmpleado()
                    + ", fechaDesde= " + asignacion.getFechaDesde());
                
                statement.setString(1,  asignacion.getEmpresaId());
                statement.setString(2,  asignacion.getRutEmpleado());
                
                statement.setDate(3,  Utilidades.getJavaSqlDate(asignacion.getFechaDesde(),"yyyy-MM-dd"));
                
                // ...
                statement.addBatch();
                i++;

                if (i % 50 == 0 || i == _asignaciones.size()) {
                    int[] rowsAffected = statement.executeBatch(); // Execute every 1000 items.
                    System.out.println("[TurnoRotativoDAO."
                        + "eliminarAsignaciones]"
                        + "filas afectadas= "+rowsAffected.length);
                }
            }
            dbLocator.freeConnection(dbConn);
        }catch(SQLException ex){
            System.err.println("[TurnoRotativoDAO."
                + "eliminarAsignaciones]Error= " + ex.toString());
            ex.printStackTrace();
            dbLocator.freeConnection(dbConn);
        }catch(DatabaseException dbex){
            System.err.println("[TurnoRotativoDAO."
                + "eliminarAsignaciones]Error= " + dbex.toString());
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
     * Agrega una nueva asignacion de turno rotativo
     * @param _data
     * @return 
     */
    public MaintenanceVO insertAsignacion(AsignacionTurnoRotativoVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al insertar "
            + "asignacion de turno rotativo. "
            + " empresaId:" + _data.getEmpresaId()
            + ",rutEmpleado:" + _data.getRutEmpleado()
            + ",idTurno:" + _data.getIdTurno()
            + ",idDuracion:" + _data.getIdDuracion()
            + ",fechaDesde:" + _data.getFechaDesde()
            + ",fechaHasta:" + _data.getFechaHasta();
        
        String msgFinal = " Inserta asignacion turno rotativo:"
            + "empresaId [" + _data.getEmpresaId() + "]"
            + ", rutEmpleado [" + _data.getRutEmpleado() + "]"
            + ", idTurno [" + _data.getIdTurno() + "]"
            + ", idDuracion [" + _data.getIdDuracion() + "]"
            + ", fechaDesde [" + _data.getFechaDesde() + "]"
            + ", fechaHasta [" + _data.getFechaHasta() + "]";
            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO turno_rotativo_asignacion("
                + "empresa_id, rut_empleado, id_turno, id_duracion, fecha_desde,fecha_hasta, fecha_asignacion, fecha_ultima_actualizacion) "
                + " VALUES (?, ?, ?, ?, '" + _data.getFechaDesde() 
                + "', '" + _data.getFechaHasta() + "', current_timestamp, current_timestamp)";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnoRotativoDAO.insertAsignacion]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getEmpresaId());
            insert.setString(2,  _data.getRutEmpleado());
            insert.setInt(3,  _data.getIdTurno());
            insert.setInt(4,  _data.getIdDuracion());
            
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println("[TurnoRotativoDAO.insert turno_rotativo_asignacion]"
                    + ", empresaId:" + _data.getEmpresaId()
                    + ", rutEmpleado:" + _data.getRutEmpleado()
                    + ", idTurno:" + _data.getIdTurno()
                    + ", idDuracion:" + _data.getIdDuracion()
                    + ", fechaDesde:" + _data.getFechaDesde()
                    + ", fechaHasta:" + _data.getFechaHasta()    
                    +" insertado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("TurnoRotativoDAO.insert turno_rotativo_asignacion Error1: "+sqle.toString());
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
     * Elimina una asignacion de turno rotativo
     * @param _data
     * @return 
     */
    public MaintenanceVO deleteAsignacion(AsignacionTurnoRotativoVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al eliminar "
            + "asignacion de turno rotativo. "
            + " empresaId:" + _data.getEmpresaId()
            + ",rutEmpleado:" + _data.getRutEmpleado()
            + ",idTurno:" + _data.getIdTurno()
            + ",fechaDesde:" + _data.getFechaDesde()
            + ",fechaHasta:" + _data.getFechaHasta();
        
        String msgFinal = " Elimina asignacion turno rotativo:"
            + "empresaId [" + _data.getEmpresaId() + "]"
            + ", rutEmpleado [" + _data.getRutEmpleado() + "]"
            + ", idTurno [" + _data.getIdTurno() + "]"
            + ", fechaDesde [" + _data.getFechaDesde() + "]"
            + ", fechaHasta [" + _data.getFechaHasta() + "]";
            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "DELETE "
                    + "FROM turno_rotativo_asignacion "
                    + "WHERE empresa_id = ? "
                    + "and rut_empleado=? "
                    + "and id_turno=? "
                    + "and fecha_desde='" + _data.getFechaDesde() + "' "
                    + "and fecha_hasta='" + _data.getFechaHasta() + "' ";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnoRotativoDAO.deleteAsignacion]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getEmpresaId());
            insert.setString(2,  _data.getRutEmpleado());
            insert.setInt(3,  _data.getIdTurno());
            
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println("[TurnoRotativoDAO.delete turno_rotativo_asignacion]"
                    + ", empresaId:" + _data.getEmpresaId()
                    + ", rutEmpleado:" + _data.getRutEmpleado()
                    + ", idTurno:" + _data.getIdTurno()
                    + ", fechaDesde:" + _data.getFechaDesde()
                    + ", fechaHasta:" + _data.getFechaHasta()    
                    +" eliminado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("TurnoRotativoDAO.delete turno_rotativo_asignacion Error1: "+sqle.toString());
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
     * Modifica una asignacion de turno rotativo
     * 
     * @param _currentAsignacion
     * @param _updatedAsignacion
     * @return 
     */
    public MaintenanceVO modifyAsignacion(AsignacionTurnoRotativoVO _currentAsignacion, 
            AsignacionTurnoRotativoVO _updatedAsignacion){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al modificar "
            + "asignacion de turno rotativo. "
            + " empresaId:" + _currentAsignacion.getEmpresaId()
            + ",rutEmpleado:" + _currentAsignacion.getRutEmpleado()
            + ",idTurno:" + _updatedAsignacion.getIdTurno()
            + ",idDuracion:" + _updatedAsignacion.getIdDuracion()
            + ",fechaDesde:" + _updatedAsignacion.getFechaDesde()
            + ",fechaHasta:" + _updatedAsignacion.getFechaHasta();
        
        String msgFinal = " Modifica asignacion turno rotativo:"
            + "empresaId [" + _currentAsignacion.getEmpresaId() + "]"
            + ", rutEmpleado [" + _currentAsignacion.getRutEmpleado() + "]"
            + ", idTurno [" + _updatedAsignacion.getIdTurno() + "]"
            + ", idDuracion [" + _updatedAsignacion.getIdDuracion() + "]"
            + ", fechaDesde [" + _updatedAsignacion.getFechaDesde() + "]"
            + ", fechaHasta [" + _updatedAsignacion.getFechaHasta() + "]";
        System.out.println("[TurnoRotativoDAO.modifyAsignacion] turno_rotativo_asignacion. " + msgFinal);
        
        objresultado.setMsg(msgFinal);
        PreparedStatement updatestmt    = null;
        
        try{
            String sql = "update "
                    + "turno_rotativo_asignacion "
                    + "set id_turno = ?, "
                    + "id_duracion = ?, "
                    + "fecha_desde = '" + _updatedAsignacion.getFechaDesde() + "',"
                    + "fecha_hasta = '" + _updatedAsignacion.getFechaHasta() + "',"
                    + "fecha_ultima_actualizacion = current_timestamp "
                    + " where empresa_id = ? "
                    + "and rut_empleado = ? "
                    + "and id_turno = ? "
                    + "and id_duracion = ? "
                    + "and fecha_desde = '"+_currentAsignacion.getFechaDesde()+"' "
                    + "and fecha_hasta = '"+_currentAsignacion.getFechaHasta()+"' ";

            System.out.println("[TurnoRotativoDAO.modifyAsignacion]"
                + "Sql= " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnoRotativoDAO.modifyAsignacion]");
            updatestmt = dbConn.prepareStatement(sql);
            
            updatestmt.setInt(1,  _updatedAsignacion.getIdTurno());
            updatestmt.setInt(2,  _updatedAsignacion.getIdDuracion());
                        
            updatestmt.setString(3,  _currentAsignacion.getEmpresaId());
            updatestmt.setString(4,  _currentAsignacion.getRutEmpleado());
            updatestmt.setInt(5,  _currentAsignacion.getIdTurno());
            updatestmt.setInt(6,  _currentAsignacion.getIdDuracion());
            
            int filasAfectadas = updatestmt.executeUpdate();
            System.out.println("[TurnoRotativoDAO.modifyAsignacion]"
                + "filas afectadas= "+filasAfectadas);
            if (filasAfectadas == 1){
                System.out.println("[TurnoRotativoDAO.modifyAsignacion]"
                    + "Turno_rotativo_asignacion. "
                    + ", empresaId:" + _currentAsignacion.getEmpresaId()
                    + ", rutEmpleado:" + _currentAsignacion.getRutEmpleado()
                    + ", idTurno:" + _updatedAsignacion.getIdTurno()
                    + ", idDuracion:" + _updatedAsignacion.getIdDuracion()
                    + ", fechaDesde:" + _updatedAsignacion.getFechaDesde()
                    + ", fechaHasta:" + _updatedAsignacion.getFechaHasta()    
                    +" actualizada OK!");
            }else{
                System.out.println("[TurnoRotativoDAO.modifyAsignacion]"
                    + "Insertar nueva asignacion");
                objresultado = this.insertAsignacion(_updatedAsignacion);
            }
            
            updatestmt.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("TurnoRotativoDAO.modifyAsignacion turno_rotativo_asignacion Error1: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (updatestmt != null) updatestmt.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
        
        return objresultado;
    }
    
    /**
     * Retorna asignacion turno rotativo
     * 
     * 
     * @param _empresaId
     * @param _rutEmpleado
     * @param _fecha
     * @return 
     */
    public String getAsignacionTurnoByFechaJson(String _empresaId, 
            String _rutEmpleado,
            String _fecha){

        String strJson = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            String sql = "SELECT "
                + "get_turno_rotativo_by_fecha_json"
                    + "('" + _empresaId + "','" + _rutEmpleado + "','" + _fecha + "') strjson";

            System.out.println("[TurnoRotativoDAO."
                + "getAsignacionTurnoByFechaJson]Sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnoRotativoDAO.getAsignacionTurnoByFechaJson]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                strJson += rs.getString("strjson");
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
        
        return strJson;
    }
    
    /**
     * Retorna lista con la asignacion de turno rotativo vigente 
     * del empleado para la para la fecha especificada.
     * 
     * @param _empresaId
     * @param _rutEmpleado
     * @param _fecha
     * @return 
     */
    public DetalleTurnoVO getAsignacionTurnoByFecha(String _empresaId, 
            String _rutEmpleado,
            String _fecha){
        
        DetalleTurnoVO data =null;
        
        PreparedStatement ps        = null;
        ResultSet rs                = null;
        
        try{
            String sql = "select "
                + "asignacion.id_turno,"
                + "turno.nombre_turno,"
                + "turno.hora_entrada,"
                + "turno.hora_salida,"
                + "coalesce(turno.holgura,0) holgura," 
                + "turno.minutos_colacion "
                + "from turno_rotativo_asignacion asignacion "
                + "inner join turno_rotativo turno "
                + "on (turno.empresa_id = asignacion.empresa_id "
                + "and turno.id_turno = asignacion.id_turno) "
                + "where asignacion.empresa_id='" + _empresaId + "' "
                + "and asignacion.rut_empleado='" + _rutEmpleado + "' "
                + "and '" + _fecha + "' between asignacion.fecha_desde "
                + "and asignacion.fecha_hasta";
                    
            System.out.println("[TurnoRotativoDAO."
                + "getAsignacionTurnoByFecha]Sql: "+sql);
            dbConn = dbLocator.getConnection(m_dbpoolName,"[TurnoRotativoDAO.getAsignacionTurnoByFecha]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                data = new DetalleTurnoVO();
                data.setIdTurno(rs.getInt("id_turno"));
                data.setNombreTurno(rs.getString("nombre_turno"));
                data.setHoraEntrada(rs.getString("hora_entrada"));
                data.setHoraSalida(rs.getString("hora_salida"));
                //Si la diferencia entre la hora de entrada y las 00:00hrs es menor a 2hrs...consderar
                //    turno nocturno: fecha_salida es el dia sgte....
                Date dteSalida = new Date();
                DiferenciaHorasVO diferenciaInicial = 
                    Utilidades.getTimeDifference(m_sdf.format(new Date()) + " " + data.getHoraEntrada(), 
                                          m_sdf.format(new Date())+" 23:59:59");
                System.out.println("[TurnoRotativoDAO."
                + "getAsignacionTurnoByFecha]horaEntrada: " 
                    + data.getHoraEntrada()
                    +", diferencia en horas con las 23:59:59= " 
                    + diferenciaInicial.getIntDiferenciaHoras());
                if (diferenciaInicial.getIntDiferenciaHoras() <= 4){
                    //fecha salida = dia siguiente
                    dteSalida = Utilidades.sumaRestarFecha(new Date(), 1, "DAYS");
                }
                System.out.println("[TurnoRotativoDAO."
                + "getAsignacionTurnoByFecha]fechaSalida: " + dteSalida);
                System.out.println("[TurnoRotativoDAO."
                + "getAsignacionTurnoByFecha]Resta entrada-salida turno rotativo."
                    + "fecha hora entrada: "+m_sdf.format(new Date()) + " " + data.getHoraEntrada()
                    + ",fecha hora salida: "+m_sdf.format(dteSalida)+" " + data.getHoraSalida());
                
                DiferenciaHorasVO diferenciaET = 
                    Utilidades.getTimeDifference(m_sdf.format(new Date()) + " " + data.getHoraEntrada(), 
                                          m_sdf.format(dteSalida)+" " + data.getHoraSalida());
                
                data.setTotalHoras(diferenciaET.getIntDiferenciaHoras());
                data.setHolgura(rs.getInt("holgura"));
                data.setMinutosColacion(rs.getInt("minutos_colacion"));
                //data.setMinutosColacion(rs.getInt("minutos_colacion"));
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("[TurnoRotativoDAO."
                + "getAsignacionTurnoByFecha]Error: " + sqle.toString());
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
}
