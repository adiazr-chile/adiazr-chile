/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.VacacionesVO;
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
public class VacacionesLogDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    public boolean m_usedGlobalDbConnection = false;
    
    private final String SQL_INSERT_VACACIONES = 
        "INSERT INTO vacaciones("
            + "empresa_id, "
            + "rut_empleado, "
            + "dias_acumulados, "
            + "dias_progresivos,"
            + "current_num_cotizaciones) "
        + "VALUES (?, ?, ?, ?, ?)";
    
    private String SQL_DELETE_VACACIONES = "delete from vacaciones " +
        "WHERE empresa_id = ? " +
        "and rut_empleado = ?";
    
    /**
     *
     * @param _propsValues
     */
    public VacacionesLogDAO(PropertiesVO _propsValues) {
    }

     /**
     * Agrega un nuevo saldo de vacaciones de un empleado.
     * 
     * @param _data
     * @return 
     */
    public MaintenanceVO insert(VacacionesVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al insertar "
            + "Info de Vacaciones. "
            + "Username: " + _data.getUsername()    
            + ", empresaId: " + _data.getEmpresaId()
            + ", rutEmpleado: " + _data.getRutEmpleado()    
            + ", dias_acumulados: " + _data.getDiasAcumulados()
            + ", dias_progresivos: " + _data.getDiasProgresivos()
            + ", dias_especiales: " + _data.getDiasEspeciales()
            + ", saldo_dias: " + _data.getSaldoDias()
            + ", dias_zona_extrema: " + _data.getDiasZonaExtrema()    
            + ", current_num_cotizaciones: " + _data.getNumActualCotizaciones()
            + ", comentario: " + _data.getComentario()
            + ", dias_adicionales: " + _data.getDiasAdicionales()
            + ", afp_code: " + _data.getAfpCode()   
            + ", fecha certif afp: " + _data.getFechaCertifVacacionesProgresivas();
        
        String msgFinal = " Inserta info de vacaciones:"
            + "EmpresaId [" + _data.getEmpresaId() + "]" 
            + ", rutEmpleado [" + _data.getRutEmpleado() + "]"    
            + ", dias_acumulados [" + _data.getDiasAcumulados() + "]"
            + ", dias_progresivos [" + _data.getDiasProgresivos() + "]"
            + ", dias_especiales [" + _data.getDiasEspeciales() + "]"
            + ", dias_adicionales [" + _data.getDiasAdicionales() + "]"
            + ", saldo_dias [" + _data.getSaldoDias() + "]"
            + ", current_num_cotizaciones [" + _data.getNumActualCotizaciones() + "]"
            + ", afp_code [" + _data.getAfpCode() + "]"   
            + ", fecha certif afp [" + _data.getFechaCertifVacacionesProgresivas() + "]"      
            + ", comentario [" + _data.getComentario() + "]";
       
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO vacaciones_log("
                + "fecha_evento,"
                + "empresa_id, "
                + "rut_empleado, "
                + "dias_acumulados, "
                + "dias_progresivos,"
                + "saldo_dias,"
                + "dias_especiales,"
                + "current_num_cotizaciones,"
                + "comentario,"
                + "dias_zona_extrema,"
                + "afp_code,"
                + "fec_certif_vac_progresivas,"
                + "dias_adicionales,"
                + "dias_efectivos_tomados,"
                + "username,"
                + "fecha_calculo,"
                    + "inicio_ult_vacacion,"
                    + "fin_ult_vacacion) "
                + "VALUES (current_timestamp,"
                    + "?, ?, ?, ?, "
                    + "?, ?, ?, ?, "
                    + "?, ?, ?, ?, "
                    + "?, ?,current_timestamp, ?, ?)";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[VacacionesLogDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getEmpresaId());
            insert.setString(2,  _data.getRutEmpleado());
            insert.setInt(3,  _data.getDiasAcumulados());
            insert.setInt(4,  _data.getDiasProgresivos());
            insert.setInt(5,  _data.getSaldoDias());
            insert.setString(6,  _data.getDiasEspeciales());
            
            insert.setInt(7,  _data.getNumActualCotizaciones());
            insert.setString(8,  _data.getComentario());
            insert.setInt(9,  _data.getDiasZonaExtrema());
            insert.setString(10,  _data.getAfpCode());
            if (_data.getFechaCertifVacacionesProgresivas() != null && 
                _data.getFechaCertifVacacionesProgresivas().compareTo("") != 0){
                    insert.setDate(11, Utilidades.getSqlDate(_data.getFechaCertifVacacionesProgresivas(), "yyyy-MM-dd"));
            }else {
                insert.setDate(11, null);
            }
            insert.setInt(12,  _data.getDiasAdicionales());
            insert.setInt(13,  _data.getDiasEfectivos());
            insert.setString(14,  _data.getUsername());
            
            //Fecha inicio ultimas vacaciones
            if (_data.getFechaInicioUltimasVacaciones() != null && 
                _data.getFechaInicioUltimasVacaciones().compareTo("") != 0){
                    insert.setDate(15, Utilidades.getSqlDate(_data.getFechaInicioUltimasVacaciones(), "yyyy-MM-dd"));
            }else {
                insert.setDate(15, null);
            }
            //Fecha fin ultimas vacaciones
            if (_data.getFechaFinUltimasVacaciones() != null && 
                _data.getFechaFinUltimasVacaciones().compareTo("") != 0){
                    insert.setDate(16, Utilidades.getSqlDate(_data.getFechaFinUltimasVacaciones(), "yyyy-MM-dd"));
            }else {
                insert.setDate(16, null);
            }
            
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println("[VacacionesLogDAO.insert]vacaciones"
                    + ". username:" + _data.getUsername()
                    + ", empresaId:" + _data.getEmpresaId()
                    + ", rutEmpleado:" + _data.getRutEmpleado()    
                    + ", dias_acumulados:" +_data.getDiasAcumulados()
                    + ", dias_progresivos:" + _data.getDiasProgresivos()
                    + ", dias_especiales:" + _data.getDiasEspeciales()
                    + ", saldo_dias:" + _data.getSaldoDias()
                    + ", current_num_cotizaciones: " + _data.getNumActualCotizaciones()
                    + ", dias_efectivos_tomados: " + _data.getDiasEfectivos()    
                    +" insertada OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("VacacionesLogDAO.insert. Error1: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (insert != null) insert.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("VacacionesLogDAO.insert Error: "+ex.toString());
            }
        }

        return objresultado;
    }
    
    /**
    * Retorna lista con saldo de vacaciones para un empleado o todos los
    * empleados de un centro de costo.
    * 
    * @param _empresaId
    * @param _rutEmpleado
    * @param _startDate
    * @param _endDate
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * 
    * @return 
    */
    public List<VacacionesVO> getInfoVacacionesLog(String _empresaId, 
            String _rutEmpleado,
            String _startDate,
            String _endDate,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<VacacionesVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        VacacionesVO data;
        
        try{
            String sql = "select "
                + "vac.fecha_evento,"
                + "vac.username,"
                + "vac.empresa_id, "
                + "empleado.depto_id,empleado.depto_nombre,"
                + "empleado.cenco_id,empleado.ccosto_nombre,"
                + "vac.rut_empleado,"
                + "empleado.nombre || ' ' || empleado.materno nombre_empleado,"
                + "to_char(vac.fecha_calculo, 'yyyy-MM-dd HH24:MI:SS') fecha_calculo,"
                + "vac.dias_acumulados,"
                + "vac.dias_progresivos,"
                + "vac.saldo_dias,"
                + "vac.inicio_ult_vacacion,"
                + "vac.fin_ult_vacacion,"
                + "coalesce(vac.dias_especiales,'N') dias_especiales,"
                + "vac.current_num_cotizaciones,"
                + "vac.dias_zona_extrema,"
                + "vac.comentario,empleado.fecha_inicio_contrato,"
                + "coalesce(empleado.es_zona_extrema,'N') es_zona_extrema,"
                + "coalesce(vac.afp_code,'NINGUNA') afp_code," 
                + "coalesce(afp.afp_name,'NINGUNA') afp_name, " 
                + "vac.fec_certif_vac_progresivas,"
                + "coalesce(vac.dias_adicionales,0) dias_adicionales,"
                + "vac.dias_efectivos_tomados "
                + "from vacaciones_log vac "
                    + "inner join view_empleado empleado "
                    + "on (empleado.empresa_id = vac.empresa_id "
                    + "and empleado.rut=vac.rut_empleado) "
                    + "left outer join afp on (vac.afp_code = afp.afp_code) "
                    + "where 1 = 1 ";
           
            if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
                sql += " and vac.empresa_id = '" + _empresaId + "' ";
            }
            
            if (_rutEmpleado != null && _rutEmpleado.compareTo("-1") != 0){
                sql += " and vac.rut_empleado = '" + _rutEmpleado + "' ";
            }
            
            if ( (_startDate != null && _startDate.compareTo("") != 0) ){        
                if ( (_endDate == null || _endDate.compareTo("") == 0) ){ 
                    _endDate = _startDate;
                }
                sql += " and fecha_evento::date "
                    + "between '" + _startDate + "' and '" + _endDate + "' ";
            }
           
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            System.out.println("[VacacionesLogDAO."
                + "getInfoVacacionesLog]sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[VacacionesLogDAO.getInfoVacacionesLog]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new VacacionesVO();
                
                data.setFechaEvento(rs.getString("fecha_evento"));
                data.setUsername(rs.getString("username"));
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setDeptoId(rs.getString("depto_id"));
                data.setDeptoNombre(rs.getString("depto_nombre"));
                data.setCencoId(rs.getInt("cenco_id"));
                data.setCencoNombre(rs.getString("ccosto_nombre"));
                data.setRutEmpleado(rs.getString("rut_empleado"));
                data.setNombreEmpleado(rs.getString("nombre_empleado"));
                data.setFechaCalculo(rs.getString("fecha_calculo"));
                data.setDiasAcumulados(rs.getInt("dias_acumulados"));
                data.setDiasProgresivos(rs.getInt("dias_progresivos"));
                data.setDiasEspeciales(rs.getString("dias_especiales"));
                data.setSaldoDias(rs.getInt("saldo_dias"));
                data.setFechaInicioUltimasVacaciones(rs.getString("inicio_ult_vacacion"));
                data.setFechaFinUltimasVacaciones(rs.getString("fin_ult_vacacion"));
                
                data.setNumActualCotizaciones(rs.getInt("current_num_cotizaciones"));
                
                data.setDiasZonaExtrema(rs.getInt("dias_zona_extrema"));
                data.setComentario(rs.getString("comentario"));
                data.setFechaInicioContrato(rs.getString("fecha_inicio_contrato"));
                data.setEsZonaExtrema(rs.getString("es_zona_extrema"));
                
                data.setAfpCode(rs.getString("afp_code"));
                data.setAfpName(rs.getString("afp_name"));
                data.setFechaCertifVacacionesProgresivas(rs.getString("fec_certif_vac_progresivas"));
                data.setDiasAdicionales(rs.getInt("dias_adicionales"));
                data.setDiasEfectivos(rs.getInt("dias_efectivos_tomados"));
                
                data.setRowKey(data.getEmpresaId()+"|"+data.getRutEmpleado());
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
    * @param _rutEmpleado
    * @param _startDate
    * @param _endDate
    * 
    * @return 
    */
    public int getInfoVacacionesLogCount(String _empresaId, 
            String _rutEmpleado,
            String _startDate,
            String _endDate){
        int count=0;
        Statement statement = null;
        ResultSet rs = null;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[VacacionesLogDAO.getInfoVacacionesLogCount]");
            statement = dbConn.createStatement();
            String strSql ="SELECT count(rut_empleado) numrows "
                + "from vacaciones_log vac " +
                    "inner join view_empleado "
                    + "empleado on (empleado.empresa_id = vac.empresa_id and empleado.rut=vac.rut_empleado) "
                    + "where 1 = 1";
             
            if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
                strSql += " and vac.empresa_id = '" + _empresaId + "' ";
            }
            
            if (_rutEmpleado != null && _rutEmpleado.compareTo("") != 0){
                strSql += " and vac.rut_empleado = '" + _rutEmpleado + "' ";
            }
            
            if ( (_startDate != null && _startDate.compareTo("") != 0) ){        
                if ( (_endDate == null || _endDate.compareTo("") == 0) ){ 
                    _endDate = _startDate;
                }
                strSql += " and fecha_evento::date "
                    + "between '" + _startDate + "' and '" + _endDate + "' ";
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
                System.err.println("Error: "+ex.toString());
            }
        }
        return count;
    }
   
    /**
    * 
    */
    public void openDbConnection(){
        try {
            m_usedGlobalDbConnection = true;
            dbConn = dbLocator.getConnection(m_dbpoolName,"[VacacionesLogDAO.openDbConnection]");
        } catch (DatabaseException ex) {
            System.err.println("[VacacionesLogDAO.openDbConnection]"
                + "Error: " + ex.toString());
        }
    }
    
    public void closeDbConnection(){
        try {
            m_usedGlobalDbConnection = false;
            dbLocator.freeConnection(dbConn);
        } catch (Exception ex) {
            System.err.println("[VacacionesLogDAO.closeDbConnection]"
                + "Error: "+ex.toString());
        }
    }
}
