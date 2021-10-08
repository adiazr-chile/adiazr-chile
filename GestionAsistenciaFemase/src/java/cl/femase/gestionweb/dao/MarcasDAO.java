/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.CentroCostoVO;
import cl.femase.gestionweb.vo.InfoMarcaVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.MarcaVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexander
 */
public class MarcasDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    //private Connection dbConn;
    //DbDirectConnection dbconnection = DbDirectConnection.getInstance();
    SimpleDateFormat sdfSemana  = new SimpleDateFormat("E dd/MM/yyyy", new Locale("ES","cl"));
    SimpleDateFormat sdfFecha   = new SimpleDateFormat("yyyy-MM-dd", new Locale("ES","cl"));
        
    public MarcasDAO(PropertiesVO _propsValues) {
//        try {
//            dbLoc = DatabaseLocator.getInstance();
//            m_dbpoolName = _propsValues.getDbPoolName();
//        } catch (DatabaseException ex) {
//            m_logger.error("DbError: "+ex.toString());
//        }
    }
    
    /**
     * Retorna una lista de tipos de marcas
     * @return 
     */
    public HashMap<Integer, String> getTiposMarca(){
        HashMap<Integer, String> tipos = new HashMap<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            String sql = "SELECT cod_tipo, nombre_tipo "
                + "FROM tipo_marca order by cod_tipo";
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[MarcasDAO.getTiposMarca]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()){
                tipos.put(rs.getInt("cod_tipo"), rs.getString("nombre_tipo"));
            }
            
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
        
        return tipos;
    }
    
    /**
    * Retorna lista con marcas existentes
    * 
    * @param _empresaId
    * @param _rutEmpleado
    * @param _startDate
    * @param _endDate
    * @return 
    */
    public LinkedHashMap<String, MarcaVO> getMarcasEmpleado(String _empresaId,
            String _rutEmpleado, 
            String _startDate, 
            String _endDate){
        
        LinkedHashMap<String, MarcaVO> lista = 
            new LinkedHashMap<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        MarcaVO data;
        String strWhere ="";
        if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
            strWhere += " empresa_cod = '" + _empresaId + "'";
        }

        if (_rutEmpleado != null && _rutEmpleado.compareTo("-1") != 0){        
            strWhere += " and rut_empleado = '" + _rutEmpleado + "'";
        }

        if (_startDate != null && _startDate.compareTo("") != 0){
            if (_endDate == null || _endDate.compareTo("") == 0){
                _endDate = _startDate;
            }
            strWhere += " and fecha_hora::date "
                + "between '" + _startDate + "' and '" + _endDate + "' ";
        }
      
        try{
            String sql="SELECT cod_dispositivo,"
                + "empresa_cod, rut_empleado, fecha_hora, "
                + "to_char(fecha_hora, 'yyyy-MM-dd HH24:MI:00') fecha_hora_fmt,"
                + "cod_tipo_marca,fecha_hora_actualizacion,"
                + "comentario,correlativo "
                + "FROM marca "
                + "where "
                + strWhere +
                    " and ("
                        + "fecha_hora = (select min(fecha_hora) min_entrada "
                        + " from marca "
                        + " where "
                        + strWhere + " and cod_tipo_marca = 1) "
                        + " or ( "
                            + " fecha_hora = (select max(fecha_hora) max_salida "
                            + " from marca "
                            + " where "
                            + strWhere + " and cod_tipo_marca = 2) "
                        + ") "
                    + ") ";
    
            sql += " order by cod_tipo_marca ";// + _jtSorting; 

            System.out.println("[MarcasDAO."
                + "getMarcasEmpleado]Sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[MarcasDAO.getMarcasEmpleado]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new MarcaVO();
                data.setCodDispositivo(rs.getString("cod_dispositivo"));
                data.setEmpresaCod(rs.getString("empresa_cod"));
                data.setRutEmpleado(rs.getString("rut_empleado"));
                data.setFechaHora(rs.getString("fecha_hora"));
                data.setFechaHoraCalculos(rs.getString("fecha_hora_fmt"));
                data.setTipoMarca(rs.getInt("cod_tipo_marca"));
                data.setComentario(rs.getString("comentario"));
                data.setCorrelativo(rs.getInt("correlativo"));
                
                lista.put(""+data.getTipoMarca(), data);
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
     * Retorna lista de empleados sin marca de entrada
     * 
     * @param _empresaId
     * @param _codDia
     * @param _horaLimite
     * @param _cencoId
     * 
     * @return 
     */
    public String getEmpleadosSinMarcaEntradaDiaJson(String _empresaId, 
            int _codDia, 
            String _horaLimite, 
            int _cencoId){

        String jsonEmpleados = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            String sql = "SELECT "
                + "get_empleados_sin_marca_entrada_dia_json"
                    + "('" + _empresaId + "'," + _codDia + ",'" + _horaLimite + "'," + _cencoId + ") infoempleado";

            System.out.println("[MarcasDAO."
                + "getEmpleadosSinMarcaEntradaDiaJson]Sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[MarcasDAO.getEmpleadosSinMarcaEntradaDiaJson]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                jsonEmpleados += rs.getString("infoempleado");
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
        
        return jsonEmpleados;
    }
    
    /**
     * Retorna lista de marcas en formato json
     * 
     * @param _empresaId
     * @param _rut
     * @param _startDate
     * @param _endDate
     * 
     * @return 
     */
    public String getMarcasJson(String _empresaId, String _rut, 
            String _startDate,String _endDate){

        String strJson = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            String sql = "SELECT "
                + "get_marcas_json"
                    + "('" + _empresaId + "','" + _rut + "','" + _startDate + "','" + _endDate + "') strjson";

            System.out.println("[MarcasDAO."
                + "getMarcasJson]Sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[MarcasDAO.getMarcasJson]");
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
    * Retorna lista de marcas en formato json
    * 
    * @param _empresaId
    * @param _rut
    * @param _startDate
    * @param _endDate
    * 
    * @return 
    */
    public String getAllMarcasJson(String _empresaId, String _rut, 
            String _startDate,String _endDate){

        String strJson = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            String sql = "SELECT "
                + "get_all_marcas_json"
                    + "('" + _empresaId + "','" + _rut + "','" + _startDate + "','" + _endDate + "') strjson";

            System.out.println("[MarcasDAO."
                + "getAllMarcasJson]Sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[MarcasDAO.getAllMarcasJson]");
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
    * Retorna lista de marcas (tabla historica) en formato json
    * 
    * @param _empresaId
    * @param _rut
    * @param _startDate
    * @param _endDate
    * 
    * @return 
    */
    public String getAllMarcasHistJson(String _empresaId, String _rut, 
            String _startDate,String _endDate){

        String strJson = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            String sql = "SELECT "
                + "get_all_marcas_hist_json"
                    + "('" + _empresaId + "','" + _rut + "','" + _startDate + "','" + _endDate + "') strjson";

            System.out.println("[MarcasDAO."
                + "getAllMarcasHistJson]Sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[MarcasDAO."
                + "getAllMarcasHistJson]");
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
                System.err.println("[MarcasDAO.getAllMarcasHistJson]"
                    + "Error: " + ex.toString());
            }
        }
        return strJson;
    }
    
    /**
     * Retorna lista de marcas historicas en formato json
     * 
     * @param _empresaId
     * @param _rut
     * @param _startDate
     * @param _endDate
     * 
     * @return 
     */
    public String getMarcasHistJson(String _empresaId, String _rut, 
            String _startDate,String _endDate){

        String strJson = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            String sql = "SELECT "
                + "get_marcas_hist_json"
                    + "('" + _empresaId + "','" + _rut + "','" + _startDate + "','" + _endDate + "') strjson";

            System.out.println("[MarcasDAO."
                + "getMarcasHistJson]Sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[MarcasDAO.getMarcasHistJson]");
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
     * Retorna lista de marcas en formato json
     * 
     * @param _empresaId
     * @param _rutEmpleado
     * @param _startDate
     * @param _endDate
     * @param _tipoMarca
     * @param _fechaHoraEntrada
     * @param _isHistorico
     * 
     * @return 
     */
    public String getMarcaByTipoJson(String _empresaId,
            String _rutEmpleado, 
            String _startDate, 
            String _endDate,
            int _tipoMarca, 
            String _fechaHoraEntrada, 
            boolean _isHistorico){

        String strJson = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        String strFuncion1 = "get_marca_by_tipo_and_fecha_hora_json";
        String strFuncion2 = "get_marca_by_tipo_json";
        
        if (_isHistorico){
            strFuncion1 = "get_marca_hist_by_tipo_and_fecha_hora_json";
            strFuncion2 = "get_marca_hist_by_tipo_json";
        }
        
        try{
            
            String sql ="";
            if (_fechaHoraEntrada != null){
                sql = "SELECT "
                    + strFuncion1
                    + " ('" + _empresaId + "','" + _rutEmpleado + "','" + _startDate + "','" + _endDate + "',"+_tipoMarca+",'" + _fechaHoraEntrada + "') strjson";
            }else{
                sql = "SELECT "
                    + strFuncion2
                    + " ('" + _empresaId + "','" + _rutEmpleado + "','" + _startDate + "','" + _endDate + "',"+_tipoMarca+") strjson";
            }
            
            System.out.println("[MarcasDAO."
                + "getMarcaByTipoJson]Sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[MarcasDAO.getMarcaByTipoJson]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                strJson += rs.getString("strjson");
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[MarcasDAO."
                + "getMarcaByTipoJson]Error: "+sqle.toString());
            m_logger.error("Error: "+sqle.toString());
            sqle.printStackTrace();
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
        if (strJson.compareTo("")==0) strJson = null;
        return strJson;
    }
    
    /**
     * Retorna lista de marcas en formato json
     * 
     * @param _empresaId
     * @param _rutEmpleado
     * @param _startDate
     * @param _endDate
     * @param _tipoMarca
     * @param _isHistorico
     * 
     * @return 
     */
    public String getMarcaByTipoAndFechaJson(String _empresaId,
            String _rutEmpleado, 
            String _startDate, 
            String _endDate,
            int _tipoMarca,
            boolean _isHistorico){

        String strJson = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        String strFuncion1 = "get_marca_by_tipo_and_fecha_json";
        
        if (_isHistorico){
            strFuncion1 = "get_marca_hist_by_tipo_and_fecha_json";
        }
        
        try{
            
            String sql ="";
            sql = "SELECT "
                + strFuncion1
                + " ('" + _empresaId + "',"
                + "'" + _rutEmpleado + "',"
                + "'" + _startDate + "',"
                + "'" + _endDate + "',"
                + _tipoMarca + ") strjson";
            
            System.out.println("[MarcasDAO."
                + "getMarcaByTipoAndFechaJson]Sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[MarcasDAO.getMarcaByTipoAndFechaJson]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                strJson += rs.getString("strjson");
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[MarcasDAO."
                + "getMarcaByTipoAndFechaJson]Error: "+sqle.toString());
            m_logger.error("Error: "+sqle.toString());
            sqle.printStackTrace();
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
        if (strJson.compareTo("")==0) strJson = null;
        return strJson;
    }
    
    /**
    * Retorna lista de empleados con marcas rechazadas para una fecha X
    * 
    * @param _empresaId
    * @param _fecha: en formato 'yyyy-MM-dd'
    * 
    * @return 
    */
    public String getEmpleadosConMarcasRechadasFechaJson(String _empresaId, 
            String _fecha){
        
        String jsonEmpleados = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            String sql = "SELECT "
                + "get_empleados_con_marcas_rechazadas_fecha_json"
                    + "('" + _empresaId + "','" + _fecha + "') infoempleado";

            System.out.println("[MarcasDAO."
                + "getEmpleadosConMarcasRechadasFechaJson]Sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[MarcasDAO.getEmpleadosConMarcasRechadasFechaJson]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                jsonEmpleados += rs.getString("infoempleado");
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
        
        return jsonEmpleados;
    }
    
    /**
    * Retorna marca existente
    * 
    * @param _empresaId
    * @param _rutEmpleado
    * @param _startDate
    * @param _endDate
    * @param _tipoMarca
    * @param _fechaHoraEntrada
    * @return 
    */
    public MarcaVO getMarcaByTipo(String _empresaId,
            String _rutEmpleado, 
            String _startDate, 
            String _endDate,
            int _tipoMarca, 
            String _fechaHoraEntrada){
        
        MarcaVO data=null;
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        String strWhere ="";
        if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
            strWhere += " empresa_cod = '" + _empresaId + "'";
        }

        if (_rutEmpleado != null && _rutEmpleado.compareTo("-1") != 0){        
            strWhere += " and rut_empleado = '" + _rutEmpleado + "'";
        }

        if (_startDate != null && _startDate.compareTo("") != 0){
            if (_endDate == null || _endDate.compareTo("") == 0){
                _endDate = _startDate;
            }
            strWhere += " and fecha_hora::date "
                + "between '" + _startDate + "' and '" + _endDate + "' ";
        }
      
        try{
            String sql="SELECT cod_dispositivo,"
                    + "empresa_cod, rut_empleado, fecha_hora, "
                    + "to_char(fecha_hora, 'yyyy-MM-dd HH24:MI:00') fecha_hora_fmt,"
                    + "cod_tipo_marca,fecha_hora_actualizacion,"
                    + "comentario,"
                    + "correlativo "
                    + "FROM marca "
                    + "where "
                    + strWhere +
                    " and (";
            sql += " fecha_hora = (select min(fecha_hora) min_entrada ";
           
            sql += " from marca "
                + " where "
                + strWhere + " and cod_tipo_marca = "+_tipoMarca;
            if (_fechaHoraEntrada != null){
                sql+=" and (fecha_hora > '" + _fechaHoraEntrada + "')";
            }    
            sql += " ) "
                + " ) ";
    
            sql += " order by cod_tipo_marca ";// + _jtSorting; 

            System.out.println("[MarcasDAO."
                + "getMarcaByTipo]Sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[MarcasDAO.getMarcaByTipo]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                data = new MarcaVO();
                data.setCodDispositivo(rs.getString("cod_dispositivo"));
                data.setEmpresaCod(rs.getString("empresa_cod"));
                data.setRutEmpleado(rs.getString("rut_empleado"));
                data.setFechaHora(rs.getString("fecha_hora"));
                data.setFechaHoraCalculos(rs.getString("fecha_hora_fmt"));
                data.setTipoMarca(rs.getInt("cod_tipo_marca"));
                data.setComentario(rs.getString("comentario"));
                data.setCorrelativo(rs.getInt("correlativo"));
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
    * Retorna marca existente
    * 
    * @param _empresaId
    * @param _rutEmpleado
    * @param _fechaHora
    * @param _tipoMarca
    * @param _historico
    * @return 
    */
    public MarcaVO getMarcaByKey(String _empresaId,
            String _rutEmpleado, 
            String _fechaHora, 
            int _tipoMarca, boolean _historico){
        
        MarcaVO data=null;
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        String tabla =  "marca";
        if (_historico) tabla = "marca_historica";
        
        try{
            String sql = "SELECT "
                + "cod_dispositivo,"
                + "empresa_cod,"
                + "rut_empleado,"
                + "fecha_hora,"
                + "to_char(fecha_hora, 'TMday dd/MM/yyyy HH24:mi:ss') fecha_hora_str,"
                + "to_char(fecha_hora, 'HH24:mi:ss') solohora_str,"
                + "coalesce(to_char(fecha_hora, 'HH24'),'VACIO') solohora,"
                + "to_char(fecha_hora, 'yyyy-MM-dd HH24:MI:00') fecha_hora_fmt," 
                + "to_char(fecha_hora, 'MI') solomins,"
                + "to_char(fecha_hora, 'SS') solosecs,"
                + "to_char(fecha_hora, 'yyyy-MM-dd') solofecha," 
                + "cod_tipo_marca," 
                + "tipo_marca.nombre_tipo,"
                + "fecha_hora_actualizacion,"
                //+ "to_char(fecha_hora_actualizacion, 'TMday dd/MM/yyyy HH24:mi:ss') fecha_actualizacion,"
                + "CASE WHEN (id ='null' or id is null) THEN '' ELSE id END as id,"
                + "hashcode,"
                + "empleado.empresa_id,"
                + "empleado.depto_id,"
                + "empleado.cenco_id,"
                + "marca.comentario, marca.correlativo,"
                + "CASE "
                    + "WHEN (DATE_PART('second', fecha_hora_actualizacion - fecha_hora) > 10) "
                    + "	THEN to_char(fecha_hora_actualizacion, 'TMday dd/MM/yyyy HH24:mi:ss') "
                    + "	ELSE null END AS fecha_actualizacion,"
                    + "tipo_marca_manual.code,"
                    + "tipo_marca_manual.display_name "     
                + "FROM marca "
                    + "inner join empleado on (empleado.empl_rut = marca.rut_empleado) " +
                    " inner join tipo_marca on marca.cod_tipo_marca = tipo_marca.cod_tipo "
                    + "left outer join tipo_marca_manual on (marca.cod_tpo_marca_manual = tipo_marca_manual.code) "
                + " where 1=1 ";
            
            if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
                sql += " and empleado.empresa_id = '" + _empresaId + "'";
            }
            
            if (_rutEmpleado != null && _rutEmpleado.compareTo("-1") != 0){        
                sql += " and marca.rut_empleado = '" + _rutEmpleado + "'";
            }
            
            sql+= " and (marca.cod_tipo_marca=" + _tipoMarca + ") ";
            
            if (_fechaHora.length() > 10){
                sql+=  "and marca.fecha_hora = '"+_fechaHora+"'";
            }else{
                sql+=  "and marca.fecha_hora::date = '"+_fechaHora+"'";
            }
            if (dbConn==null || (dbConn!=null && dbConn.isClosed())) dbConn = dbLocator.getConnection(m_dbpoolName,"[MarcasDAO.getMarcaByKey]");
            System.out.println("[MarcasDAO.getMarcaByKey]sql: " + sql);
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                data = new MarcaVO();
                
                data.setCodDispositivo(rs.getString("cod_dispositivo"));
                data.setEmpresaCod(rs.getString("empresa_cod"));
                data.setRutEmpleado(rs.getString("rut_empleado"));
                data.setRutKey(data.getRutEmpleado());
                data.setCencoId(rs.getInt("cod_tipo_marca"));
                data.setFechaHora(rs.getString("fecha_hora"));
                data.setSoloHora(rs.getString("solohora_str"));
                
                data.setFechaHoraStr(rs.getString("fecha_hora_str"));
                data.setFechaHoraKey(data.getFechaHora());
                data.setFechaHoraCalculos(rs.getString("fecha_hora_fmt"));
                data.setHora(rs.getString("solohora"));
                data.setFecha(rs.getString("solofecha"));
                data.setMinutos(rs.getString("solomins"));
                data.setSegundos(rs.getString("solosecs"));
                data.setTipoMarca(rs.getInt("cod_tipo_marca"));
                data.setId(rs.getString("id"));
                data.setHashcode(rs.getString("hashcode"));
                data.setComentario(rs.getString("comentario"));
                data.setFechaHoraActualizacion(rs.getString("fecha_actualizacion"));
                data.setCodTipoMarcaManual(rs.getInt("code"));
                data.setNombreTipoMarcaManual(rs.getString("display_name"));
                data.setCorrelativo(rs.getInt("correlativo"));
                
                data.setRowKey(data.getEmpresaCod()
                    + "|" + data.getRutEmpleado()
                    + "|" + data.getFechaHora()
                    + "|" + data.getTipoMarca());
                
            }

            ps.close();
            rs.close();
            //dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[MarcasDAO.getMarcaByKey]"
                + "Error: " + sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                //dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
        
        return data;
    }
    
    /**
    * 
    * @param _empresaId
    * @param _rutEmpleado
    * @param _fechaHora
    * @param _tipoMarca
    * @param _historico
    * 
    * @return 
    */
    public ArrayList<MarcaVO> getMarcasByKey(String _empresaId,
            String _rutEmpleado, 
            String _fechaHora, 
            int _tipoMarca, 
            boolean _historico){
        
        ArrayList<MarcaVO> marcas=new ArrayList<>();
        MarcaVO data=null;
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        String tabla =  "marca";
        if (_historico) tabla = "marca_historica";
        
        try{
            String sql = "SELECT "
                + "cod_dispositivo,"
                + "empresa_cod,"
                + "rut_empleado,"
                + "fecha_hora,"
                + "to_char(fecha_hora, 'TMday dd/MM/yyyy HH24:mi:ss') fecha_hora_str,"
                + "to_char(fecha_hora, 'HH24:mi:ss') solohora_str,"
                + "coalesce(to_char(fecha_hora, 'HH24'),'VACIO') solohora,"
                + "to_char(fecha_hora, 'yyyy-MM-dd HH24:MI:00') fecha_hora_fmt," 
                + "to_char(fecha_hora, 'MI') solomins,"
                + "to_char(fecha_hora, 'SS') solosecs,"
                + "to_char(fecha_hora, 'yyyy-MM-dd') solofecha," 
                + "cod_tipo_marca," 
                + "tipo_marca.nombre_tipo,"
                + "fecha_hora_actualizacion,"
                //+ "to_char(fecha_hora_actualizacion, 'TMday dd/MM/yyyy HH24:mi:ss') fecha_actualizacion,"
                + "CASE WHEN (id ='null' or id is null) THEN '' ELSE id END as id,"
                + "hashcode,"
                + "empleado.empresa_id,"
                + "empleado.depto_id,"
                + "empleado.cenco_id,"
                + "marca.comentario,marca.correlativo "
                + "CASE "
                    + "WHEN (DATE_PART('second', fecha_hora_actualizacion - fecha_hora) > 10) "
                    + "	THEN to_char(fecha_hora_actualizacion, 'TMday dd/MM/yyyy HH24:mi:ss') "
                    + "	ELSE null END AS fecha_actualizacion,"
                    + "tipo_marca_manual.code,"
                    + "tipo_marca_manual.display_name "     
                + "FROM marca "
                    + "inner join empleado on (empleado.empl_rut = marca.rut_empleado) " +
                    " inner join tipo_marca on marca.cod_tipo_marca = tipo_marca.cod_tipo "
                    + "left outer join tipo_marca_manual on (marca.cod_tpo_marca_manual = tipo_marca_manual.code) "
                + " where 1=1 ";
            
            if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
                sql += " and empleado.empresa_id = '" + _empresaId + "'";
            }
            
            if (_rutEmpleado != null && _rutEmpleado.compareTo("-1") != 0){        
                sql += " and marca.rut_empleado = '" + _rutEmpleado + "'";
            }
            
            sql+= " and (marca.cod_tipo_marca=" + _tipoMarca + ") ";
            
            if (_fechaHora.length() > 10){
                sql+=  "and marca.fecha_hora = '"+_fechaHora+"'";
            }else{
                sql+=  "and marca.fecha_hora::date = '"+_fechaHora+"'";
            }
            if (dbConn==null || (dbConn!=null && dbConn.isClosed())) 
                dbConn = dbLocator.getConnection(m_dbpoolName,"[MarcasDAO.getMarcasByKey]");
            System.out.println("[MarcasDAO.getMarcasByKey]sql: " + sql);
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new MarcaVO();
                
                data.setCodDispositivo(rs.getString("cod_dispositivo"));
                data.setEmpresaCod(rs.getString("empresa_cod"));
                data.setRutEmpleado(rs.getString("rut_empleado"));
                data.setRutKey(data.getRutEmpleado());
                data.setCencoId(rs.getInt("cod_tipo_marca"));
                data.setFechaHora(rs.getString("fecha_hora"));
                data.setSoloHora(rs.getString("solohora_str"));
                
                data.setFechaHoraStr(rs.getString("fecha_hora_str"));
                data.setFechaHoraKey(data.getFechaHora());
                data.setFechaHoraCalculos(rs.getString("fecha_hora_fmt"));
                data.setHora(rs.getString("solohora"));
                data.setFecha(rs.getString("solofecha"));
                data.setMinutos(rs.getString("solomins"));
                data.setSegundos(rs.getString("solosecs"));
                data.setTipoMarca(rs.getInt("cod_tipo_marca"));
                data.setId(rs.getString("id"));
                data.setHashcode(rs.getString("hashcode"));
                data.setComentario(rs.getString("comentario"));
                data.setFechaHoraActualizacion(rs.getString("fecha_actualizacion"));
                data.setCodTipoMarcaManual(rs.getInt("code"));
                data.setNombreTipoMarcaManual(rs.getString("display_name"));
                data.setCorrelativo(rs.getInt("correlativo"));
                
                data.setRowKey(data.getEmpresaCod()
                    + "|" + data.getRutEmpleado()
                    + "|" + data.getFechaHora()
                    + "|" + data.getTipoMarca());
                
                marcas.add(data);
            }

            ps.close();
            rs.close();
            //dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[MarcasDAO.getMarcasByKey]"
                + "Error: " + sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                //dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
        
        return marcas;
    }
    
//    /**
//     * Retorna lista con marcas de entrada y salida existentes
//     * para una fecha especifica
//     * 
//     * @param _empresaId
//     * @param _rutEmpleado
//     * @param _startDate
//     * @param _endDate
//     * @return 
//     */
//    public LinkedHashMap<String, MarcaVO> getMarcasEmpleado(String _empresaId,
//            String _rutEmpleado, 
//            String _startDate, 
//            String _endDate){
//        
//        LinkedHashMap<String, MarcaVO> lista = 
//            new LinkedHashMap<>();
//        
//        PreparedStatement ps = null;
//        ResultSet rs = null;
//        MarcaVO data;
//        
//        try{
//            String sql = "SELECT cod_dispositivo, "
//                    + "empresa_cod, "
//                    + "rut_empleado, "
//                    + "fecha_hora, "
//                    + "cod_tipo_marca,"
//                    + "fecha_hora_actualizacion "
//                + "FROM marca "
//                + "where 1=1 ";
//            
//            if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
//                sql += " and empresa_cod = '" + _empresaId + "'";
//            }
//            
//            if (_rutEmpleado != null && _rutEmpleado.compareTo("-1") != 0){        
//                sql += " and rut_empleado = '" + _rutEmpleado + "'";
//            }
//            
//            if (_startDate != null && _startDate.compareTo("") != 0){
//                if (_endDate == null || _endDate.compareTo("") == 0){
//                    _endDate = _startDate;
//                }
//                sql += "and fecha_hora::date "
//                    + "between '" + _startDate + "' and '" + _endDate + "' ";
//            }
//            
//            sql += " order by fecha_hora,cod_tipo_marca ";// + _jtSorting; 
////            if (_jtPageSize > 0){
////                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
////            }
//            
//            System.out.println("cl.femase.gestionweb."
//                + "service.MarcasDAO.getMarcasEmpleado. SQL: "+sql);
//
//            dbConn = dbLocator.getConnection(m_dbpoolName);
//            ps = dbConn.prepareStatement(sql);
//            rs = ps.executeQuery();
//
//            while (rs.next()){
//                data = new MarcaVO();
//                data.setCodDispositivo(rs.getString("cod_dispositivo"));
//                data.setEmpresaCod(rs.getString("empresa_cod"));
//                data.setRutEmpleado(rs.getString("rut_empleado"));
//                data.setFechaHora(rs.getString("fecha_hora"));
//                data.setTipoMarca(rs.getInt("cod_tipo_marca"));
//              
//                lista.put(data.getFechaHora(), data);
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
//    
    /**
    * Retorna lista con las marcas
    * 
    * @param _empresaId
    * @param _deptoId
    * @param _cencoId
    * @param _jtStartIndex
    * @param _dispositivoId
    * @param _rutEmpleado
    * @param _startDate
    * @param _jtPageSize
    * @param _endDate
    * @param _jtSorting
    * @return 
    */
    public List<MarcaVO> getMarcas(String _empresaId,
            String _deptoId,
            int _cencoId,
            String _rutEmpleado, 
            String _dispositivoId,
            String _startDate, 
            String _endDate,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<MarcaVO> lista = new ArrayList<>();
                        
        PreparedStatement ps = null;
        ResultSet rs = null;
        MarcaVO data;
        
        try{
            String sql = "SELECT "
                + "cod_dispositivo,"
                + "empresa_cod,"
                + "rut_empleado,"
                + "fecha_hora,"
                + "to_char(fecha_hora, 'TMday dd/MM/yyyy HH24:mi:ss') fecha_hora_str,"
                + "coalesce(to_char(fecha_hora, 'HH24'),'VACIO') solohora,"
                + "to_char(fecha_hora, 'yyyy-MM-dd HH24:MI:00') fecha_hora_fmt," 
                + "to_char(fecha_hora, 'MI') solomins,"
                + "to_char(fecha_hora, 'SS') solosecs,"
                + "to_char(fecha_hora, 'yyyy-MM-dd') solofecha," 
                + "cod_tipo_marca," 
                + "tipo_marca.nombre_tipo,"
                + "fecha_hora_actualizacion,"
                //+ "to_char(fecha_hora_actualizacion, 'TMday dd/MM/yyyy HH24:mi:ss') fecha_actualizacion,"
                + "CASE WHEN (id ='null' or id is null) THEN '' ELSE id END as id,"
                + "hashcode,"
                + "empleado.empresa_id,"
                + "empleado.depto_id,"
                + "empleado.cenco_id,"
                + "marca.comentario,marca.correlativo "
                + "CASE "
                    + "WHEN (DATE_PART('second', fecha_hora_actualizacion - fecha_hora) > 10) "
                    + "	THEN to_char(fecha_hora_actualizacion, 'TMday dd/MM/yyyy HH24:mi:ss') "
                    + "	ELSE null END AS fecha_actualizacion,"
                    + "tipo_marca_manual.code,"
                    + "tipo_marca_manual.display_name "     
                + "FROM marca "
                    + "inner join empleado on (empleado.empl_rut = marca.rut_empleado) " +
                    " inner join tipo_marca on marca.cod_tipo_marca = tipo_marca.cod_tipo "
                    + "left outer join tipo_marca_manual on (marca.cod_tpo_marca_manual = tipo_marca_manual.code) "
                + " where 1=1 ";
            
            if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
                sql += " and empleado.empresa_id = '" + _empresaId + "'";
            }
            
            if (_deptoId != null && _deptoId.compareTo("-1") != 0){        
                sql += " and empleado.depto_id = '" + _deptoId + "'";
            }
            
            if (_cencoId != -1){        
                sql += " and empleado.cenco_id = '" + _cencoId + "'";
            }
            
            if (_rutEmpleado != null && _rutEmpleado.compareTo("-1") != 0){        
                sql += " and marca.rut_empleado = '" + _rutEmpleado + "'";
            }
            
            if (_dispositivoId != null && _dispositivoId.compareTo("-1") != 0){        
                sql += " and marca.cod_dispositivo = '" + _dispositivoId + "'";
            }
            
            if (_startDate != null && _startDate.compareTo("") != 0){
                if (_endDate == null || _endDate.compareTo("") == 0){
                    _endDate = _startDate;
                }
                sql += " and marca.fecha_hora::date "
                    + "between '" + _startDate + "' and '" + _endDate + "' ";
            }
           
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            System.out.println("[MarcasDAO]getMarcas()- sql: " + sql);
                        
            dbConn = dbLocator.getConnection(m_dbpoolName,"[MarcasDAO.getMarcas]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new MarcaVO();
                data.setCodDispositivo(rs.getString("cod_dispositivo"));
                data.setEmpresaCod(rs.getString("empresa_cod"));
                data.setRutEmpleado(rs.getString("rut_empleado"));
                data.setRutKey(data.getRutEmpleado());
                data.setCencoId(rs.getInt("cod_tipo_marca"));
                data.setFechaHora(rs.getString("fecha_hora"));
                data.setFechaHoraStr(rs.getString("fecha_hora_str"));
                data.setFechaHoraKey(data.getFechaHora());
                data.setFechaHoraCalculos(rs.getString("fecha_hora_fmt"));
                data.setHora(rs.getString("solohora"));
                data.setFecha(rs.getString("solofecha"));
                data.setMinutos(rs.getString("solomins"));
                data.setSegundos(rs.getString("solosecs"));
                data.setTipoMarca(rs.getInt("cod_tipo_marca"));
                data.setId(rs.getString("id"));
                data.setHashcode(rs.getString("hashcode"));
                data.setComentario(rs.getString("comentario"));
                data.setFechaHoraActualizacion(rs.getString("fecha_actualizacion"));
                data.setCodTipoMarcaManual(rs.getInt("code"));
                data.setNombreTipoMarcaManual(rs.getString("display_name"));
                data.setCorrelativo(rs.getInt("correlativo"));                
                
                data.setRowKey(data.getEmpresaCod()
                    + "|" + data.getRutEmpleado()
                    + "|" + data.getFechaHora()
                    + "|" + data.getTipoMarca());
                
//                System.out.println("[MarcasDAO]getMarcas(). "
//                    + "add marca. "
//                    + "rowKey= " + data.getRowKey()
//                    + ", code= " + data.getCodTipoMarcaManual()
//                    + ", nombre= " + data.getNombreTipoMarcaManual());
                
                lista.add(data);
                
////                MarcaVO marcaFaltante = new MarcaVO();
////                if (data.getTipoMarca() == 1){
////                    //busco marca de salida
////                    marcaFaltante = getMarcaByKey(data.getEmpresaCod(),
////                            data.getRutEmpleado(),
////                            data.getFecha(),2, false);
////                    if (marcaFaltante == null){
////                       // System.out.println("[MarcasDAO]getMarcas(). Falta marca de salida, fecha: "+data.getFecha());
////                        //marcaFaltante = data;
////                        marcaFaltante = new MarcaVO();
////                        marcaFaltante.setTipoMarca(200);//FALTA MARCA DE SALIDA
////                    }
////                }else{
////                    //busco marca de entrada
////                    marcaFaltante = getMarcaByKey(data.getEmpresaCod(),
////                            data.getRutEmpleado(),
////                            data.getFecha(),1,false);
////                    if (marcaFaltante == null){
////                        //System.out.println("[MarcasDAO]getMarcas(). Falta marca de entrada, fecha: "+data.getFecha());
////                        //marcaFaltante = data;
////                        marcaFaltante = new MarcaVO();
////                        marcaFaltante.setTipoMarca(100);//FALTA MARCA DE ENTRADA
////                    }
////                }
////                
////                if (marcaFaltante.getTipoMarca() == 100 || marcaFaltante.getTipoMarca() == 200){
////                    Date fechaAux=null;
////                    try {
////                        fechaAux = sdfFecha.parse(data.getFecha());
////                    } catch (ParseException ex) {
////                        System.err.println("Error al parsear fecha marca: "+ ex.toString());
////                    }
////                    
////                    marcaFaltante.setRutEmpleado(data.getRutEmpleado());
////                    marcaFaltante.setEmpresaCod(data.getEmpresaCod());
////                    marcaFaltante.setCodDispositivo(data.getCodDispositivo());
////                    marcaFaltante.setFechaHora(data.getFecha());
////                    marcaFaltante.setFechaHoraStr(sdfSemana.format(fechaAux));
////                    marcaFaltante.setRowKey(data.getEmpresaCod()
////                        + "|" + data.getRutEmpleado()
////                        + "|" + data.getFecha()
////                        + "|" + marcaFaltante.getTipoMarca());
//////                    System.out.println("[MarcasDAO]getMarcas(). "
//////                        + "add marca faltante. rowKey= "+marcaFaltante.getRowKey());
////
////                    lista.add(marcaFaltante);
////                }
            }
            
            ps.close();
            rs.close();
            if (dbConn != null){    
                dbConn.close();
                dbLocator.freeConnection(dbConn);
            }
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[MarcasDAO.getMarcas]"
                    + "Error1: " + sqle.toString());
            sqle.printStackTrace();
            m_logger.error("Error: "+sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                if (dbConn != null){    
                    dbConn.close();
                    dbLocator.freeConnection(dbConn);
                }
            } catch (SQLException ex) {
                System.err.println("[MarcasDAO.getMarcas]"
                    + "Error2: " + ex.toString());
            }
        }
        
        return lista;
    }
    
    /**
    * Retorna lista con las marcas virtuales
    * 
    * @param _empresaId
    * @param _jtStartIndex
    * @param _rutEmpleado
    * @param _startDate
     * @param _tipoMarcaManual
    * @param _jtPageSize
    * @param _endDate
    * @param _jtSorting
    * @return 
    */
    public List<MarcaVO> getMarcasVirtuales(String _empresaId,
            String _rutEmpleado, 
            String _startDate, 
            String _endDate,
            int _tipoMarcaManual,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<MarcaVO> lista = new ArrayList<>();
                        
        PreparedStatement ps = null;
        ResultSet rs = null;
        MarcaVO data;
        
        try{
            String sql = "select "
                + "correlativo,"
                + "cod_dispositivo,"
                + "empresa_cod,"
                + "rut_empleado,"
                + "empleado.empl_nombres || ' ' || empleado.empl_ape_paterno|| ' ' || empleado.empl_ape_materno nombre_empleado,"
                + "cod_tipo_marca,"
                + "tipo_marca.nombre_tipo,"
                + "to_char(fecha_hora, 'TMday dd/MM/yyyy HH24:mi:ss') fecha_hora_str,"
                + "comentario,"
                + "latitud,"
                + "longitud "
                + "from marca "
                + "inner join tipo_marca on (marca.cod_tipo_marca = tipo_marca.cod_tipo) "
                + "inner join empleado on (empleado.empl_rut = marca.rut_empleado and empleado.empresa_id = marca.empresa_cod) "
                + "where cod_tpo_marca_manual = " + _tipoMarcaManual
                + " and empresa_cod = '" + _empresaId + "' "
                + " and rut_empleado = '" + _rutEmpleado + "' "
                + " and ( LENGTH(latitud) > 0 and LENGTH(longitud) > 0 and (latitud!='null' and longitud!='null') ) ";
           
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
            
            System.out.println("[MarcasDAO.getMarcasVirtuales]sql: " + sql);
                        
            dbConn = dbLocator.getConnection(m_dbpoolName,"[MarcasDAO.getMarcasVirtuales]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new MarcaVO();
                
                data.setCorrelativo(rs.getInt("correlativo"));
                data.setCodDispositivo(rs.getString("cod_dispositivo"));
                data.setEmpresaCod(rs.getString("empresa_cod"));
                data.setRutEmpleado(rs.getString("rut_empleado"));
                data.setNombreEmpleado(rs.getString("nombre_empleado"));
                data.setTipoMarca(rs.getInt("cod_tipo_marca"));
                data.setNombreTipoMarca(rs.getString("nombre_tipo"));
                data.setFechaHoraStr(rs.getString("fecha_hora_str"));
                data.setComentario(rs.getString("comentario"));
                data.setLatitud(rs.getString("latitud"));
                data.setLongitud(rs.getString("longitud"));
                
                if ( (data.getLatitud() != null && data.getLatitud().compareTo("") != 0 ) && 
                        (data.getLongitud() != null && data.getLongitud().compareTo("") != 0 )){
                    
                    data.setGoogleMapUrl(Constantes.GOOGLE_MAPS_PREFIJO
                        + data.getLatitud() 
                        + "," 
                        + data.getLongitud());
                }else{
                    data.setGoogleMapUrl("");
                }
                
                lista.add(data);

            }
            
            ps.close();
            rs.close();
            if (dbConn != null){    
                dbConn.close();
                dbLocator.freeConnection(dbConn);
            }
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[MarcasDAO.getMarcasVirtuales]"
                    + "Error1: " + sqle.toString());
            sqle.printStackTrace();
            m_logger.error("Error: "+sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                if (dbConn != null){    
                    dbConn.close();
                    dbLocator.freeConnection(dbConn);
                }
            } catch (SQLException ex) {
                System.err.println("[MarcasDAO.getMarcasVirtuales]"
                    + "Error2: " + ex.toString());
            }
        }
        
        return lista;
    }
    
    /**
    * Retorna lista con las marcas
    * 
    * @param _empresaId
    * @param _deptoId
    * @param _cencoId
    * @param _jtStartIndex
    * @param _dispositivoId
    * @param _rutEmpleado
    * @param _startDate
    * @param _jtPageSize
    * @param _endDate
    * @param _jtSorting
    * @return 
    */
    public LinkedHashMap<String, MarcaVO> getHashMarcas(String _empresaId,
            String _deptoId,
            int _cencoId,
            String _rutEmpleado, 
            String _dispositivoId,
            String _startDate, 
            String _endDate,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        LinkedHashMap<String, MarcaVO> hashMarcas = new LinkedHashMap<>();
                        
        PreparedStatement ps = null;
        ResultSet rs = null;
        MarcaVO data;
        
        try{
            String sql = "SELECT "
                + "cod_dispositivo,"
                + "empresa_cod,"
                + "rut_empleado,"
                + "fecha_hora,"
                + "to_char(fecha_hora, 'TMday dd/MM/yyyy HH24:mi:ss') fecha_hora_str,"
                + "coalesce(to_char(fecha_hora, 'HH24'),'VACIO') solohora,"
                + "to_char(fecha_hora, 'yyyy-MM-dd HH24:MI:00') fecha_hora_fmt," 
                + "to_char(fecha_hora, 'MI') solomins,"
                + "to_char(fecha_hora, 'SS') solosecs,"
                + "to_char(fecha_hora, 'yyyy-MM-dd') solofecha," 
                + "cod_tipo_marca," 
                + "tipo_marca.nombre_tipo,"
                + "fecha_hora_actualizacion,"
                //+ "to_char(fecha_hora_actualizacion, 'TMday dd/MM/yyyy HH24:mi:ss') fecha_actualizacion,"
                + "CASE WHEN (id ='null' or id is null) THEN '' ELSE id END as id,"
                + "hashcode,"
                + "empleado.empresa_id,"
                + "empleado.depto_id,"
                + "empleado.cenco_id,"
                + "marca.comentario, "
                + "marca.correlativo, "
                + "CASE "
                    + "WHEN (DATE_PART('second', fecha_hora_actualizacion - fecha_hora) > 10) "
                    + "	THEN to_char(fecha_hora_actualizacion, 'TMday dd/MM/yyyy HH24:mi:ss') "
                    + "	ELSE null END AS fecha_actualizacion,"
                    + "tipo_marca_manual.code,"
                    + "tipo_marca_manual.display_name "     
                + "FROM marca "
                    + "inner join empleado on (empleado.empl_rut = marca.rut_empleado) " +
                    " inner join tipo_marca on marca.cod_tipo_marca = tipo_marca.cod_tipo "
                    + "left outer join tipo_marca_manual on (marca.cod_tpo_marca_manual = tipo_marca_manual.code) "
                + " where 1=1 ";
            
            if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
                sql += " and empleado.empresa_id = '" + _empresaId + "'";
            }
            
            if (_deptoId != null && _deptoId.compareTo("-1") != 0){        
                sql += " and empleado.depto_id = '" + _deptoId + "'";
            }
            
            if (_cencoId != -1){        
                sql += " and empleado.cenco_id = '" + _cencoId + "'";
            }
            
            if (_rutEmpleado != null && _rutEmpleado.compareTo("-1") != 0){        
                sql += " and marca.rut_empleado = '" + _rutEmpleado + "'";
            }
            
            if (_dispositivoId != null && _dispositivoId.compareTo("-1") != 0){        
                sql += " and marca.cod_dispositivo = '" + _dispositivoId + "'";
            }
            
            if (_startDate != null && _startDate.compareTo("") != 0){
                if (_endDate == null || _endDate.compareTo("") == 0){
                    _endDate = _startDate;
                }
                sql += " and marca.fecha_hora::date "
                    + "between '" + _startDate + "' and '" + _endDate + "' ";
            }
           
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            System.out.println("[MarcasDAO]getHashMarcas()- sql: " + sql);
                        
            dbConn = dbLocator.getConnection(m_dbpoolName,"[MarcasDAO.getHashMarcas]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new MarcaVO();
                data.setCodDispositivo(rs.getString("cod_dispositivo"));
                data.setEmpresaCod(rs.getString("empresa_cod"));
                data.setRutEmpleado(rs.getString("rut_empleado"));
                data.setRutKey(data.getRutEmpleado());
                data.setCencoId(rs.getInt("cod_tipo_marca"));
                data.setFechaHora(rs.getString("fecha_hora"));
                data.setFechaHoraStr(rs.getString("fecha_hora_str"));
                data.setFechaHoraKey(data.getFechaHora());
                data.setFechaHoraCalculos(rs.getString("fecha_hora_fmt"));
                data.setHora(rs.getString("solohora"));
                data.setFecha(rs.getString("solofecha"));
                data.setMinutos(rs.getString("solomins"));
                data.setSegundos(rs.getString("solosecs"));
                data.setTipoMarca(rs.getInt("cod_tipo_marca"));
                data.setId(rs.getString("id"));
                data.setHashcode(rs.getString("hashcode"));
                data.setComentario(rs.getString("comentario"));
                data.setFechaHoraActualizacion(rs.getString("fecha_actualizacion"));
                data.setCodTipoMarcaManual(rs.getInt("code"));
                data.setNombreTipoMarcaManual(rs.getString("display_name"));
                data.setCorrelativo(rs.getInt("correlativo"));                
                
                data.setRowKey(data.getEmpresaCod()
                    + "|" + data.getRutEmpleado()
                    + "|" + data.getFechaHora()
                    + "|" + data.getTipoMarca());
                
//                System.out.println("[MarcasDAO]getMarcas(). "
//                    + "add marca. "
//                    + "rowKey= " + data.getRowKey()
//                    + ", code= " + data.getCodTipoMarcaManual()
//                    + ", nombre= " + data.getNombreTipoMarcaManual());
                
                hashMarcas.put(data.getRowKey(),data);
                
////                MarcaVO marcaFaltante = new MarcaVO();
////                if (data.getTipoMarca() == 1){
////                    //busco marca de salida
////                    marcaFaltante = getMarcaByKey(data.getEmpresaCod(),
////                            data.getRutEmpleado(),
////                            data.getFecha(),2, false);
////                    if (marcaFaltante == null){
////                       // System.out.println("[MarcasDAO]getMarcas(). Falta marca de salida, fecha: "+data.getFecha());
////                        //marcaFaltante = data;
////                        marcaFaltante = new MarcaVO();
////                        marcaFaltante.setTipoMarca(200);//FALTA MARCA DE SALIDA
////                    }
////                }else{
////                    //busco marca de entrada
////                    marcaFaltante = getMarcaByKey(data.getEmpresaCod(),
////                            data.getRutEmpleado(),
////                            data.getFecha(),1,false);
////                    if (marcaFaltante == null){
////                        //System.out.println("[MarcasDAO]getMarcas(). Falta marca de entrada, fecha: "+data.getFecha());
////                        //marcaFaltante = data;
////                        marcaFaltante = new MarcaVO();
////                        marcaFaltante.setTipoMarca(100);//FALTA MARCA DE ENTRADA
////                    }
////                }
////                
////                if (marcaFaltante.getTipoMarca() == 100 || marcaFaltante.getTipoMarca() == 200){
////                    Date fechaAux=null;
////                    try {
////                        fechaAux = sdfFecha.parse(data.getFecha());
////                    } catch (ParseException ex) {
////                        System.err.println("Error al parsear fecha marca: "+ ex.toString());
////                    }
////                    
////                    marcaFaltante.setRutEmpleado(data.getRutEmpleado());
////                    marcaFaltante.setEmpresaCod(data.getEmpresaCod());
////                    marcaFaltante.setCodDispositivo(data.getCodDispositivo());
////                    marcaFaltante.setFechaHora(data.getFecha());
////                    marcaFaltante.setFechaHoraStr(sdfSemana.format(fechaAux));
////                    marcaFaltante.setRowKey(data.getEmpresaCod()
////                        + "|" + data.getRutEmpleado()
////                        + "|" + data.getFecha()
////                        + "|" + marcaFaltante.getTipoMarca());
//////                    System.out.println("[MarcasDAO]getMarcas(). "
//////                        + "add marca faltante. rowKey= "+marcaFaltante.getRowKey());
////
////                    lista.add(marcaFaltante);
////                }
            }
            
            ps.close();
            rs.close();
            if (dbConn != null){    
                dbConn.close();
                dbLocator.freeConnection(dbConn);
            }
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[MarcasDAO.getHashMarcas]"
                    + "Error1: " + sqle.toString());
            sqle.printStackTrace();
            m_logger.error("Error: "+sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                if (dbConn != null){    
                    dbConn.close();
                    dbLocator.freeConnection(dbConn);
                }
            } catch (SQLException ex) {
                System.err.println("[MarcasDAO.getHashMarcas]"
                    + "Error2: " + ex.toString());
            }
        }
        
        return hashMarcas;
    }
   
    /**
    * Retorna lista con las marcas historicas
    * 
    * @param _empresaId
    * @param _deptoId
    * @param _cencoId
    * @param _jtStartIndex
    * @param _dispositivoId
    * @param _rutEmpleado
    * @param _startDate
    * @param _jtPageSize
    * @param _endDate
    * @param _jtSorting
    * @return 
    */
    public List<MarcaVO> getMarcasHist(String _empresaId,
            String _deptoId,
            int _cencoId,
            String _rutEmpleado, 
            String _dispositivoId,
            String _startDate, 
            String _endDate,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<MarcaVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        MarcaVO data;
        
        try{
            String sql = "SELECT "
                + "cod_dispositivo,"
                + "empresa_cod,"
                + "rut_empleado,"
                + "fecha_hora,"
                + "to_char(fecha_hora, 'TMday dd/MM/yyyy HH24:mi:ss') fecha_hora_str,"
                + "coalesce(to_char(fecha_hora, 'HH24'),'VACIO') solohora,"
                + "to_char(fecha_hora, 'yyyy-MM-dd HH24:MI:00') fecha_hora_fmt," 
                + "to_char(fecha_hora, 'MI') solomins,"
                + "to_char(fecha_hora, 'yyyy-MM-dd') solofecha," 
                + "cod_tipo_marca," 
                + "tipo_marca.nombre_tipo,"
                + "fecha_hora_actualizacion,"
                + "id,"
                + "hashcode,"
                + "empleado.empresa_id,"
                + "empleado.depto_id,"
                + "empleado.cenco_id,"
                + "marca.comentario,"
                + "marca.correlativo "
                + "FROM marca_historica marca "
                    + "inner join empleado on (empleado.empl_rut = marca.rut_empleado) " +
                    " inner join tipo_marca on marca.cod_tipo_marca = tipo_marca.cod_tipo "
                + "where 1=1 ";
            
            if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
                sql += " and empleado.empresa_id = '" + _empresaId + "'";
            }
            
            if (_deptoId != null && _deptoId.compareTo("-1") != 0){        
                sql += " and empleado.depto_id = '" + _deptoId + "'";
            }
            
            if (_cencoId != -1){        
                sql += " and empleado.cenco_id = '" + _cencoId + "'";
            }
            
            if (_rutEmpleado != null && _rutEmpleado.compareTo("-1") != 0){        
                sql += " and marca.rut_empleado = '" + _rutEmpleado + "'";
            }
            
            if (_dispositivoId != null && _dispositivoId.compareTo("-1") != 0){        
                sql += " and marca.cod_dispositivo = '" + _dispositivoId + "'";
            }
            
            if (_startDate != null && _startDate.compareTo("") != 0){
                if (_endDate == null || _endDate.compareTo("") == 0){
                    _endDate = _startDate;
                }
                sql += " and marca.fecha_hora::date "
                    + "between '" + _startDate + "' and '" + _endDate + "' ";
            }
           
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            System.out.println("cl.femase.gestionweb."
                + "service.MarcasDAO.getMarcasHist()- sql: "+sql);
                        
            dbConn = dbLocator.getConnection(m_dbpoolName,"[MarcasDAO.getMarcasHist]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new MarcaVO();
                data.setCodDispositivo(rs.getString("cod_dispositivo"));
                data.setEmpresaCod(rs.getString("empresa_cod"));
                data.setRutEmpleado(rs.getString("rut_empleado"));
                data.setRutKey(data.getRutEmpleado());
                data.setFechaHora(rs.getString("fecha_hora"));
                data.setFechaHoraStr(rs.getString("fecha_hora_str"));
                data.setFechaHoraKey(data.getFechaHora());
                data.setFechaHoraCalculos(rs.getString("fecha_hora_fmt"));
                data.setHora(rs.getString("solohora"));
                data.setFecha(rs.getString("solofecha"));
                data.setMinutos(rs.getString("solomins"));
                data.setTipoMarca(rs.getInt("cod_tipo_marca"));
                data.setId(rs.getString("id"));
                data.setHashcode(rs.getString("hashcode"));
                data.setComentario(rs.getString("comentario"));
                data.setCorrelativo(rs.getInt("correlativo"));
                
                data.setRowKey(data.getEmpresaCod()
                    + "|" + data.getRutEmpleado()
                    + "|" + data.getFechaHora()
                    + "|" + data.getTipoMarca());
                lista.add(data);
                
                MarcaVO marcaFaltante = new MarcaVO();
                if (data.getTipoMarca() == Constantes.MARCA_ENTRADA){
                    //busco marca de salida
                    marcaFaltante = getMarcaByKey(data.getEmpresaCod(),
                            data.getRutEmpleado(),
                            data.getFecha(),2,true);
                    if (marcaFaltante == null){
                       // System.out.println("[MarcasDAO]getMarcas(). Falta marca de salida, fecha: "+data.getFecha());
                        //marcaFaltante = data;
                        marcaFaltante = new MarcaVO();
                        marcaFaltante.setTipoMarca(200);//FALTA MARCA DE SALIDA
                    }
                }else{
                    //busco marca de entrada
                    marcaFaltante = getMarcaByKey(data.getEmpresaCod(),
                            data.getRutEmpleado(),
                            data.getFecha(),1,true);
                    if (marcaFaltante == null){
                        //System.out.println("[MarcasDAO]getMarcas(). Falta marca de entrada, fecha: "+data.getFecha());
                        //marcaFaltante = data;
                        marcaFaltante = new MarcaVO();
                        marcaFaltante.setTipoMarca(100);//FALTA MARCA DE ENTRADA
                    }
                }
                
                if (marcaFaltante.getTipoMarca() == 100 || marcaFaltante.getTipoMarca() == 200){
                    Date fechaAux=null;
                    try {
                        fechaAux = sdfFecha.parse(data.getFecha());
                    } catch (ParseException ex) {
                        System.err.println("Error al parsear fecha marca: "+ ex.toString());
                    }
                    
                    marcaFaltante.setRutEmpleado(data.getRutEmpleado());
                    marcaFaltante.setEmpresaCod(data.getEmpresaCod());
                    marcaFaltante.setCodDispositivo(data.getCodDispositivo());
                    marcaFaltante.setFechaHora(data.getFecha());
                    marcaFaltante.setFechaHoraStr(sdfSemana.format(fechaAux));
                    marcaFaltante.setRowKey(data.getEmpresaCod()
                        + "|" + data.getRutEmpleado()
                        + "|" + data.getFecha()
                        + "|" + marcaFaltante.getTipoMarca());
                    System.out.println("[MarcasDAO]getMarcasHist(). "
                        + "add marca faltante. rowKey= "+marcaFaltante.getRowKey());

                    lista.add(marcaFaltante);
                }
                
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("Error getMarcasHist: "+sqle.toString());
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
    * Agrega una marca
    * @param _data
    * @return 
    * 
    */
    public MaintenanceVO insert(MarcaVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al insertar "
            + "marca. "
            + "CodDispositivo: "+_data.getCodDispositivo()
            + ",codEmpresa: "+_data.getEmpresaCod()
            + ",rutEmpleado: "+_data.getRutEmpleado()
            + ",fechaHora: "+_data.getFechaHora()
            + ",tipoMarca: "+_data.getTipoMarca()
            + ",tipoMarcaManual: "+_data.getCodTipoMarcaManual();
        
       String msgFinal = " Inserta marca:"
            + "dispositivoId [" + _data.getCodDispositivo() + "]"
            + ", empresaId [" + _data.getEmpresaCod() + "]"
            + ", rutEmpleado [" + _data.getRutEmpleado() + "]"
            + ", fechaHora [" + _data.getFechaHora() + "]"
            + ", tipoMarca [" + _data.getTipoMarca() + "]"
            + ", comentario [" + _data.getComentario() + "]"
            + ", tipoMarcaManual [" + _data.getCodTipoMarcaManual() + "]";
        
        LocalTime entrada = null;
        LocalTime salida = null;   
        
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql="INSERT INTO marca("
                + "cod_dispositivo, "
                + "empresa_cod, "
                + "rut_empleado, "
                + "fecha_hora, "
                + "cod_tipo_marca,"
                + "fecha_hora_actualizacion,"
                + "modificada,"
                + "id,"
                + "hashcode,"
                + "comentario,"
                + "cod_tpo_marca_manual, correlativo) "
                + " VALUES (?, ?, ?, '"+_data.getFechaHora()+"', "
                    + "?, current_timestamp,true, ?, ?, ?, ?, ?)";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[MarcasDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getCodDispositivo());
            insert.setString(2,  _data.getEmpresaCod());
            insert.setString(3,  _data.getRutEmpleado());
            insert.setInt(4,  _data.getTipoMarca());
            insert.setString(5,  _data.getId());
            insert.setString(6,  _data.getHashcode());
            insert.setString(7,  _data.getComentario());
            insert.setInt(8,  _data.getCodTipoMarcaManual());
            insert.setInt(9,  getNewCorrelativoMarca());
            
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println("[insertDirecto marca]"
                    +" insertada OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("insert marca Error1: "+sqle.toString());
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
    * Inserta una marca manual: realiza todas las validaciones
    * para la insercion de una nueva marca de asistencia
    * 
    * @param _marca
    * @return 
    */
    public MaintenanceVO insertaMarcaManual(MarcaVO _marca){
        MaintenanceVO objresultado = new MaintenanceVO();
        PreparedStatement ps = null;
        ResultSet rs = null;
        int result=0;
        String salidaSp = "";
        boolean marcaVirtual = false;
        String label = "Manual";
        if (_marca.getLatitud() != null){
            marcaVirtual = true;
            label = "Virtual";
        }
        
        String msgError = "Error al insertar "
            + "marca " + label + ". "
            + "CodDispositivo: "+_marca.getCodDispositivo()
            + ",codEmpresa: "+_marca.getEmpresaCod()
            + ",rutEmpleado: "+_marca.getRutEmpleado()
            + ",fechaHora: "+_marca.getFechaHora()
            + ",tipoMarca: "+_marca.getTipoMarca()
            + ",tipoMarcaManual: "+_marca.getCodTipoMarcaManual();
       if (marcaVirtual) msgError += ",latitud: " + _marca.getLatitud()
            + ",longitud: " + _marca.getLongitud();
              
        String msgFinal = " Inserta marca " + label + ":"
            + "dispositivoId [" + _marca.getCodDispositivo() + "]"
            + ", empresaId [" + _marca.getEmpresaCod() + "]"
            + ", rutEmpleado [" + _marca.getRutEmpleado() + "]"
            + ", fechaHora [" + _marca.getFechaHora() + "]"
            + ", tipoMarca [" + _marca.getTipoMarca() + "]"
            + ", comentario [" + _marca.getComentario() + "]"
            + ", tipoMarcaManual [" + _marca.getCodTipoMarcaManual() + "]";
        if (marcaVirtual) msgFinal += ",latitud [" + _marca.getLatitud()+"]"
            + ",longitud [" + _marca.getLongitud() + "]";    
       
        try{
            
            objresultado.setMsg(msgFinal);
            /**
             * Ejemplo:
             * SELECT new_inserta_marca_manual(
                    'emp01',
                    '12.439.729-4',
                    '2019-06-03',
                    '08:58:00',
                    1,
                    'CENIM_POZO_ALMONTE',
                    'id_y',
                    'hash_y',
                    null,
                    1);
             */
            String sql = "SELECT "
                + "new_inserta_marca_manual"
                    + "('" + _marca.getEmpresaCod() + "',"
                    + "'" + _marca.getCodInternoEmpleado() + "',"
                    + "'" + _marca.getFecha() + "',"
                    + "'" + _marca.getHora() + "',"
                    + _marca.getTipoMarca()+","
                    + "'" + _marca.getCodDispositivo() + "',"
                    + "'" + _marca.getId() + "',"
                    + "'" + _marca.getHashcode() + "',"
                    + "'" + _marca.getComentario()+ "',"
                    + _marca.getCodTipoMarcaManual()+ ") strjson";

            if (marcaVirtual){
                System.out.println("[MarcasDAO."
                    + "insertaMarcaManual]Insertar marca virtual con geolocalizacion.");
                sql = "SELECT "
                    + "inserta_marca_virtual"
                    + "('" + _marca.getEmpresaCod() + "',"
                    + "'" + _marca.getCodInternoEmpleado() + "',"
                    + "'" + _marca.getFecha() + "',"
                    + "'" + _marca.getHora() + "',"
                    + _marca.getTipoMarca()+","
                    + "'" + _marca.getCodDispositivo() + "',"
                    + "'" + _marca.getId() + "',"
                    + "'" + _marca.getHashcode() + "',"
                    + "'" + _marca.getComentario()+ "',"
                    + _marca.getCodTipoMarcaManual()+ ","
                    + "'" + _marca.getLatitud()+ "',"
                    + "'" + _marca.getLongitud()+ "') strjson";
            }
            
            System.out.println("[MarcasDAO."
                + "insertaMarcaManual]Sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[MarcasDAO.insertaMarcaManual]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()) {
                salidaSp = rs.getString("strjson");
            }
            objresultado.setMsgFromSp(salidaSp);
            
            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("Error al insertar marca manual:" + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
        
        return objresultado;
    }
    
    /**
    * Agrega una marca
    * @param _marcaModificada
    * @param _marcaOriginal
    * @return 
    */
    public MaintenanceVO update(MarcaVO _marcaModificada,
            MarcaVO _marcaOriginal){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        StringTokenizer tkfechaHora = new StringTokenizer(_marcaModificada.getFechaHora()," ");
        String auxfecha = tkfechaHora.nextToken();
        String auxhora = tkfechaHora.nextToken();
        String newfechaHora = auxfecha+" "+_marcaModificada.getHora()+":"+_marcaModificada.getMinutos()+":"+_marcaModificada.getSegundos();
        String msgError = "Error al actualizar "
            + "marca. "
            + ",rutEmpleado: "+_marcaModificada.getRutEmpleado()
            + ",fechaHora_original: "+_marcaOriginal.getFechaHora()
            + ",hora_modificada: "+_marcaModificada.getHora()+":"+_marcaModificada.getMinutos()+":"+_marcaModificada.getSegundos()
            + ",tipoMarca: "+_marcaModificada.getTipoMarca();
        
       String msgFinal = " Actualiza marca:"
            + ", rutEmpleado [" + _marcaModificada.getRutEmpleado() + "]"
            + ", fechaHoraOriginal [" + _marcaOriginal.getFechaHora() + "]"
            + ", nuevaFechaHora [" + newfechaHora + "]"   
            + ", tipoMarca [" + _marcaModificada.getTipoMarca() + "]"
            + ", comentario [" + _marcaModificada.getComentario() + "]";
        
        System.out.println("cl.femase.gestionweb."
            + "service.MarcasDAO."
            + "update(). "+msgFinal);
       
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            
            String sql= "UPDATE marca "
                + "SET "
                    + "cod_tipo_marca = ?, "
                    + "fecha_hora_actualizacion = current_timestamp,"
                    + " modificada = true,"
                    + "comentario = ?,"
                    + "fecha_hora = '" + newfechaHora + "',"
                    + "hashcode = ? "
                + "where "
                    + "rut_empleado = ? "
                    + "and fecha_hora= '" + _marcaOriginal.getFechaHora() + "'";
                    
            dbConn = dbLocator.getConnection(m_dbpoolName,"[MarcasDAO.update]");
            insert = dbConn.prepareStatement(sql);
            insert.setInt(1,  _marcaModificada.getTipoMarca());
            insert.setString(2,  _marcaModificada.getComentario());
            insert.setString(3,  _marcaModificada.getHashcode());
            insert.setString(4,  _marcaModificada.getRutEmpleado());
                                                
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println("[update marca]"
                    + "RutEmpleado: " + _marcaModificada.getRutEmpleado()
                    + ",fechaHora: " + _marcaModificada.getFechaHora()
                    +" actualizada OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("update marca Error1: "+sqle.toString());
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
    * Elimina una marca
    * @param _data
    * @return 
    */
    public MaintenanceVO delete(MarcaVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al eliminar "
            + "marca. "
            + ",rutEmpleado: "+_data.getRutEmpleado()
            + ",fechaHora_original: "+_data.getFechaHora()
            + ",hora_modificada: "+_data.getHora()+":"+_data.getMinutos()
            + ",tipoMarca: "+_data.getTipoMarca();
        
       String msgFinal = " Elimina marca:"
            + ", rutEmpleado [" + _data.getRutEmpleado() + "]"
            + ", fechaHoraOriginal [" + _data.getFechaHora() + "]"
            + ", tipoMarca [" + _data.getTipoMarca() + "]"
            + ", comentario [" + _data.getComentario() + "]";
        
        System.out.println("cl.femase.gestionweb."
            + "service.MarcasDAO."
            + "delete(). "+msgFinal);
       
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql= "delete from marca "
                + "where rut_empleado = ? "
                + "and fecha_hora= '"+_data.getFechaHora()+"'";
                    
            dbConn = dbLocator.getConnection(m_dbpoolName,"[MarcasDAO.delete]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1, _data.getRutEmpleado());
                                                
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println("[delete marca]"
                    + "RutEmpleado: "+_data.getRutEmpleado()
                    + ",fechaHora: "+_data.getFechaHora()
                    +" marca eliminada OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("delete marca Error1: "+sqle.toString());
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
    * 
    * @param _empresaId
    * @param _deptoId
    * @param _cencoId
    * @param _rutEmpleado
    * @param _dispositivoId
    * @param _startDate
    * @param _endDate
    * @return 
    */
   public int getMarcasCount(String _empresaId,
            String _deptoId,
            int _cencoId,
            String _rutEmpleado, 
            String _dispositivoId,
            String _startDate, 
            String _endDate){
        int count=0;
        Statement statement = null;
        ResultSet rs = null;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[MarcasDAO.getMarcasCount]");
            statement = dbConn.createStatement();
            String sql = "SELECT count(fecha_hora) "
                + "FROM marca,empleado "
                + "WHERE (empleado.empl_rut = marca.rut_empleado) ";

            if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
                sql += " and empleado.empresa_id = '" + _empresaId + "'";
            }
            
            if (_deptoId != null && _deptoId.compareTo("-1") != 0){        
                sql += " and empleado.depto_id = '" + _deptoId + "'";
            }
            
            if (_cencoId != -1){        
                sql += " and empleado.cenco_id = '" + _cencoId + "'";
            }
            
            if (_rutEmpleado != null && _rutEmpleado.compareTo("-1") != 0){        
                sql += " and marca.rut_empleado = '" + _rutEmpleado + "'";
            }
            
            if (_dispositivoId != null && _dispositivoId.compareTo("-1") != 0){        
                sql += " and marca.cod_dispositivo = '" + _dispositivoId + "'";
            }
            
            if (_startDate != null && _startDate.compareTo("") != 0){
                if (_endDate == null || _endDate.compareTo("") == 0){
                    _endDate = _startDate;
                }
                sql += " and marca.fecha_hora::date "
                    + "between '" + _startDate + "' and '" + _endDate + "' ";
            }
            
            rs = statement.executeQuery(sql);		
            if (rs.next()) {
                count=rs.getInt("count");
            }
            
            statement.close();
            rs.close();
            dbConn.close();
            dbLocator.freeConnection(dbConn);
        } catch (SQLException|DatabaseException e) {
            e.printStackTrace();
        }finally{
            try {
                if (statement != null) statement.close();
                if (rs != null) rs.close();
                dbConn.close();
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
    * @param _startDate
    * @param _endDate
    * @param _tipoMarcaManual
    * @return 
    */
   public int getMarcasVirtualesCount(String _empresaId,
            String _rutEmpleado, 
            String _startDate, 
            String _endDate,
            int _tipoMarcaManual){
        int count=0;
        Statement statement = null;
        ResultSet rs = null;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[MarcasDAO.getMarcasVirtualesCount]");
            statement = dbConn.createStatement();
            String sql = "SELECT count(correlativo) "
                + "from marca "
                + " inner join tipo_marca "
                    + "on (marca.cod_tipo_marca = tipo_marca.cod_tipo) "
                + " inner join empleado "
                    + "on (empleado.empl_rut = marca.rut_empleado and empleado.empresa_id = marca.empresa_cod) "
                + "where "
                    + "cod_tpo_marca_manual = " + _tipoMarcaManual
                    + " and empresa_cod = '" + _empresaId + "' "
                    + " and rut_empleado = '" + _rutEmpleado + "' ";
            
            if (_startDate != null && _startDate.compareTo("") != 0){
                if (_endDate == null || _endDate.compareTo("") == 0){
                    _endDate = _startDate;
                }
                sql += " and fecha_hora::date "
                    + "between '" + _startDate + "' and '" + _endDate + "' ";
            }
            
            rs = statement.executeQuery(sql);		
            if (rs.next()) {
                count=rs.getInt("count");
            }
            
            statement.close();
            rs.close();
            dbConn.close();
            dbLocator.freeConnection(dbConn);
        } catch (SQLException|DatabaseException e) {
            e.printStackTrace();
        }finally{
            try {
                if (statement != null) statement.close();
                if (rs != null) rs.close();
                dbConn.close();
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
    * @param _rutEmpleado
    * @param _dispositivoId
    * @param _startDate
    * @param _endDate
    * @return 
    */
    public int getMarcasHistCount(String _empresaId,
            String _deptoId,
            int _cencoId,
            String _rutEmpleado, 
            String _dispositivoId,
            String _startDate, 
            String _endDate){
        int count=0;
        Statement statement = null;
        ResultSet rs = null;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[MarcasDAO.getMarcasHistCount]");
            statement = dbConn.createStatement();
            String sql = "SELECT count(fecha_hora) "
                + "FROM marca_historica marca,empleado "
                + "WHERE (empleado.empl_rut = marca.rut_empleado) ";

            if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
                sql += " and empleado.empresa_id = '" + _empresaId + "'";
            }
            
            if (_deptoId != null && _deptoId.compareTo("-1") != 0){        
                sql += " and empleado.depto_id = '" + _deptoId + "'";
            }
            
            if (_cencoId != -1){        
                sql += " and empleado.cenco_id = '" + _cencoId + "'";
            }
            
            if (_rutEmpleado != null && _rutEmpleado.compareTo("-1") != 0){        
                sql += " and marca.rut_empleado = '" + _rutEmpleado + "'";
            }
            
            if (_dispositivoId != null && _dispositivoId.compareTo("-1") != 0){        
                sql += " and marca.cod_dispositivo = '" + _dispositivoId + "'";
            }
            
            if (_startDate != null && _startDate.compareTo("") != 0){
                if (_endDate == null || _endDate.compareTo("") == 0){
                    _endDate = _startDate;
                }
                sql += " and marca.fecha_hora::date "
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
    *  21-09-2019
    *  
    * @param _empresaId
    * @param _rutEmpleado
    * @param _startDate
    * @param _endDate
    * @param _regionIdEmpleado
    * @param _comunaIdEmpleado
    * @param _infoCenco
    * 
    * @return 
    */
    public LinkedHashMap<String, InfoMarcaVO> getHashMarcasTurnoRotativo(String _empresaId,
            String _rutEmpleado,
            String _startDate, 
            String _endDate,
            int _regionIdEmpleado, 
            int _comunaIdEmpleado,
            CentroCostoVO _infoCenco){
        
        LinkedHashMap<String, InfoMarcaVO> hashMarcas = new LinkedHashMap<>();
                        
        PreparedStatement ps = null;
        ResultSet rs = null;
        InfoMarcaVO data;
        
        try{
            String sql = "select "
                        + "fecha_it::date fecha_it,"
                        + "to_char(fecha_it::date, 'dd/MM/yyyy') fecha_label,"
                        + "extract(isodow  from fecha_it::date) cod_dia,"
                        + "'" + _empresaId + "' empresa_cod,"
                        + "'" + _rutEmpleado + "' rut_empleado,"
                        + "to_char(marca.fecha_hora, 'yyyy-MM-dd HH24:MI:SS') fecha_hora_marca,"
                        + "to_char(marca.fecha_hora, 'TMday dd/MM/yyyy HH24:MI:SS') fecha_hora_marca_label,"
                        + "to_char(marca.fecha_hora::time, 'HH24:MI:SS') solo_hora_marca,"
                        + "coalesce(marca.cod_tipo_marca,-1) tipo_marca,"
                        + "marca.cod_dispositivo,"
                        + "empleado.empl_id_turno,"
                        + "coalesce(turno_rotativo_asignacion.id_turno,-1) id_turno_asignado,"
                        + "turno_rotativo.hora_entrada hora_entrada_teorica,"
                        + "turno_rotativo.hora_salida hora_salida_teorica,"
                        + "turno_rotativo.minutos_colacion,"
                        + "coalesce(turno_rotativo.turno_nocturno,'N') turno_nocturno,"
                        + "calendario_feriados.label label_calendario, "
                        + "to_char(marca.fecha_hora, 'HH24') solohora,"
                        + "to_char(marca.fecha_hora, 'MI') solomins,"
                        + "to_char(marca.fecha_hora, 'SS') solosecs,"
                        + "to_char(marca.fecha_hora, 'yyyy-MM-dd') solofecha,"
                        + "marca.fecha_hora_actualizacion,"
                        + "CASE WHEN (marca.id ='null' or marca.id is null) THEN '' ELSE id END as id,"
                        + "marca.hashcode,"
                        + "marca.cod_dispositivo,"
                        + "marca.comentario,"
                        + "marca.cod_tpo_marca_manual, "
                        + "marca.correlativo,"
                        + "CASE "
                        + "WHEN (DATE_PART('second', marca.fecha_hora_actualizacion - marca.fecha_hora) > 10) "
                        + "	THEN to_char(marca.fecha_hora_actualizacion, 'TMday dd/MM/yyyy HH24:mi:ss') "
                        + "	ELSE null END AS fecha_actualizacion,"
                        + "tipo_marca_manual.code,"
                        + "tipo_marca_manual.display_name,"
                        + "detalle_ausencia.fecha_inicio inicio_ausencia," +
                        "detalle_ausencia.fecha_fin fin_ausencia," +
                        "detalle_ausencia.allow_hour ausencia_por_hora," +
                        "ausencia.ausencia_nombre,"
                        + "detalle_ausencia.hora_inicio,"
                        + "detalle_ausencia.hora_fin, "
                        + "calendario_feriados.cal_region_id feriado_region_id, calendario_feriados.cal_comuna_id feriado_comuna_id,"
                        + "empleado.region_id empleado_region_id, empleado.comuna_id empleado_comuna_id "
                    + " from generate_series( '" + _startDate + "'::timestamp, '" + _endDate + "'::timestamp, '1 day'::interval) as fecha_it "
                        + " left outer join marca on (fecha_it::date = marca.fecha_hora::date "
                            + "and marca.empresa_cod = '" + _empresaId + "' and marca.rut_empleado='" + _rutEmpleado + "') "
                    + "	left outer join view_empleado empleado on (empleado.rut = '" + _rutEmpleado + "' "
                            + "and empleado.empresa_id = '" + _empresaId + "') " 
                        + "left outer join turno_rotativo_asignacion "
                            + "on (turno_rotativo_asignacion.empresa_id='" + _empresaId + "' "
                            + "and turno_rotativo_asignacion.rut_empleado='" + _rutEmpleado + "' "
                            + "and fecha_it::date between fecha_desde::date and fecha_hasta::date) "
                        + "left outer join turno_rotativo "
                            + "on (turno_rotativo.empresa_id='" + _empresaId + "' "
                            + "and turno_rotativo.id_turno=turno_rotativo_asignacion.id_turno) "
                        + " left outer join calendario_feriados "
                            + "	on ( fecha_it::date = calendario_feriados.fecha ) "
                            + "	and ( calendario_feriados.cal_region_id = " + _infoCenco.getRegionId() + " or calendario_feriados.cal_comuna_id = " + _infoCenco.getComunaId() + " or cal_id_tipo_feriado is not null) "
                            //+ " and ( empleado.comuna_id = calendario_feriados.cal_comuna_id or empleado.region_id = calendario_feriados.cal_region_id) "
                        + "left outer join tipo_marca_manual on (marca.cod_tpo_marca_manual = tipo_marca_manual.code) "
                        + "left outer join detalle_ausencia on (detalle_ausencia.rut_empleado='" + _rutEmpleado + "' " +
                        " and fecha_it::date between fecha_inicio and fecha_fin and ausencia_autorizada='S') " +
                        " left outer join ausencia on (detalle_ausencia.ausencia_id = ausencia.ausencia_id) "
                    + " where (fecha_it::date >= empleado.fecha_inicio_contrato) "
                    + "order by fecha_it::date,marca.cod_tipo_marca,fecha_hora"; 
            
            System.out.println("[MarcasDAO.getHashMarcasTurnoRotativo]sql: " + sql);
                        
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[MarcasDAO.getHashMarcasTurnoRotativo]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();
            int correlativo = 1;
            while (rs.next()){
                data = new InfoMarcaVO();
            
                String fecha = rs.getString("fecha_it");
                int regionIdFeriado = rs.getInt("feriado_region_id");
                int comunaIdFeriado = rs.getInt("feriado_comuna_id");
                int regionIdEmpleado = rs.getInt("empleado_region_id");
                int comunaIdEmpleado = rs.getInt("empleado_comuna_id");
                String labelCalendario = rs.getString("label_calendario");
                System.out.println("[MarcasDAO.getHashMarcasTurnoNormal]"
                    + "fecha= " + fecha
                    + ", regionIdFeriado= " + regionIdFeriado
                    + ", comunaIdFeriado= " + comunaIdFeriado
                    + ", cenco.region= " + _infoCenco.getRegionId()
                    + ", cenco.comuna= " + _infoCenco.getComunaId()
                    + ", regionIdEmpleado= " + regionIdEmpleado
                    + ", comunaIdEmpleado= " + comunaIdEmpleado
                    + ", labelCalendario= " + labelCalendario);
                //if (regionIdFeriado == 0 && comunaIdFeriado == 0) feriadoNacional = true;
                if (regionIdFeriado > 0 && regionIdFeriado != _infoCenco.getRegionId()){
                    labelCalendario = null;
                }
                if (comunaIdFeriado > 0 && comunaIdFeriado != _infoCenco.getComunaId()){
                    labelCalendario = null;
                }
                data.setFecha(fecha);
                data.setId(rs.getString("id"));
                data.setHashcode(rs.getString("hashcode"));
                data.setFechaLabel(rs.getString("fecha_label"));
                data.setCodDia(rs.getInt("cod_dia"));
                data.setEmpresaId(rs.getString("empresa_cod"));
                data.setRutEmpleado(rs.getString("rut_empleado"));
                data.setFechaHoraMarca(rs.getString("fecha_hora_marca"));
                data.setFechaHoraMarcaLabel(rs.getString("fecha_hora_marca_label"));
                data.setHoraMarca(rs.getString("solo_hora_marca"));
                data.setTipoMarca(rs.getInt("tipo_marca"));
                data.setCodDispositivo(rs.getString("cod_dispositivo"));
                data.setIdTurnoEmpleado(rs.getInt("empl_id_turno"));
                data.setIdTurnoAsignado(rs.getInt("id_turno_asignado"));
                data.setHoraEntradaTurno(rs.getString("hora_entrada_teorica"));
                data.setHoraSalidaTurno(rs.getString("hora_salida_teorica"));  
                data.setTurnoNocturno(rs.getString("turno_nocturno"));
                data.setLabelCalendario(labelCalendario);
                
                data.setAusenciaNombre(rs.getString("ausencia_nombre"));
                data.setAusenciaPorHora(rs.getString("ausencia_por_hora"));
                data.setFechaInicioAusencia(rs.getString("inicio_ausencia"));
                data.setFechaFinAusencia(rs.getString("fin_ausencia"));
                data.setHoraInicioAusencia(rs.getString("hora_inicio"));
                data.setHoraFinAusencia(rs.getString("hora_fin"));
                
                data.setHora(rs.getString("solohora"));
                data.setMinutos(rs.getString("solomins"));
                data.setSegundos(rs.getString("solosecs"));
                
                data.setFechaHoraActualizacion(rs.getString("fecha_actualizacion"));
                data.setCodTipoMarcaManual(rs.getInt("cod_tpo_marca_manual"));//
                data.setNombreTipoMarcaManual(rs.getString("display_name"));
                data.setComentario(rs.getString("comentario"));
                data.setCodTipoMarcaManual(rs.getInt("cod_tpo_marca_manual"));//
                data.setCorrelativo(rs.getInt("correlativo"));
                
                data.setRowKey(_empresaId
                    + "|" + _rutEmpleado
                    + "|" + data.getFecha()
                    + "|" + data.getCodDia()
                    + "|" + data.getTipoMarca()
                    +"|"+correlativo);
                
                hashMarcas.put(data.getRowKey(),data);
                correlativo++;
            }
            
            ps.close();
            rs.close();
            if (dbConn != null){    
                dbConn.close();
            }
        }catch(SQLException sqle){
            System.err.println("[MarcasDAO.getHashMarcasTurnoRotativo]"
                + "Error_1: " + sqle.toString());
            sqle.printStackTrace();
        }catch(DatabaseException dbex){
            System.err.println("[MarcasDAO.getHashMarcasTurnoRotativo]"
                + "Error_2: " + dbex.toString());
            dbex.printStackTrace();
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                if (dbConn != null){    
                    dbConn.close();
                    dbConn.close();
                }
            } catch (SQLException ex) {
                System.err.println("[MarcasDAO."
                    + "getHashMarcasTurnoRotativo]"
                    + "Error2: " + ex.toString());
            }
        }
        
        return hashMarcas;
    }
    
    /**
    *  21-09-2019
    *  
    * @param _empresaId
    * @param _rutEmpleado
    * @param _startDate
    * @param _endDate
     * @param _regionIdEmpleado
     * @param _comunaIdEmpleado
     * @param _infoCenco
    * @return 
    */
    public LinkedHashMap<String, InfoMarcaVO> getHashMarcasTurnoNormal(String _empresaId,
            String _rutEmpleado,
            String _startDate, 
            String _endDate,
            int _regionIdEmpleado, 
            int _comunaIdEmpleado,
            CentroCostoVO _infoCenco){
        
        LinkedHashMap<String, InfoMarcaVO> hashMarcas = new LinkedHashMap<>();
                        
        PreparedStatement ps = null;
        ResultSet rs = null;
        InfoMarcaVO data;
        
        try{
            String sql = "select "
                + "fecha_it::date fecha_it,"
                + "to_char(fecha_it::date, 'dd/MM/yyyy') fecha_label,"
                + "extract(isodow  from fecha_it::date) cod_dia,"
                + "'" + _empresaId + "' empresa_cod,"
                + "'" + _rutEmpleado + "' rut_empleado,"
                + "to_char(fecha_hora, 'yyyy-MM-dd HH24:MI:SS') fecha_hora_marca,"
                + "to_char(fecha_hora, 'TMday dd/MM/yyyy HH24:MI:SS') fecha_hora_marca_label,"
                + "coalesce(to_char(fecha_hora::time, 'HH24:MI:SS'),'') solo_hora_marca,"
                + "coalesce(marca.cod_tipo_marca,-1) tipo_marca,"
                + "marca.cod_dispositivo,"
                + "empleado.empl_id_turno,"
                + "detalle_turno.hora_entrada hora_entrada_teorica,"
                + "detalle_turno.hora_salida hora_salida_teorica,"
                + "detalle_turno.minutos_colacion,"
                + "coalesce(detalle_turno.id_turno,-1) id_turno_asignado,"
                + "calendario_feriados.label label_calendario, "
                + "coalesce(to_char(marca.fecha_hora, 'HH24'),'VACIO') solohora,"
                + "to_char(marca.fecha_hora, 'yyyy-MM-dd HH24:MI:00') fecha_hora_fmt,"
                    + "to_char(marca.fecha_hora, 'MI') solomins,"
                    + "to_char(marca.fecha_hora, 'SS') solosecs,"
                    + "to_char(marca.fecha_hora, 'yyyy-MM-dd') solofecha,"
                    + "marca.fecha_hora_actualizacion,"
                    + "CASE WHEN (marca.id ='null' or marca.id is null) THEN '' ELSE id END as id,"
                    + "marca.hashcode,"
                    + "marca.cod_dispositivo,"
                    + "marca.comentario,"
                    + "marca.cod_tpo_marca_manual,"
                    + "marca.correlativo,"
                    + "CASE "
                    + "WHEN (DATE_PART('second', marca.fecha_hora_actualizacion - marca.fecha_hora) > 10) "
                    + "	THEN to_char(marca.fecha_hora_actualizacion, 'TMday dd/MM/yyyy HH24:mi:ss') "
                    + "	ELSE null END AS fecha_actualizacion,"
                    + "tipo_marca_manual.code,"
                    + "tipo_marca_manual.display_name,"
                    + "detalle_ausencia.fecha_inicio inicio_ausencia," +
                    "detalle_ausencia.fecha_fin fin_ausencia," +
                    "detalle_ausencia.allow_hour ausencia_por_hora," +
                    "ausencia.ausencia_nombre, "
                    + "detalle_ausencia.hora_inicio,"
                    + "detalle_ausencia.hora_fin, "
                    + "calendario_feriados.cal_region_id feriado_region_id, calendario_feriados.cal_comuna_id feriado_comuna_id,"
                    + "empleado.region_id empleado_region_id, empleado.comuna_id empleado_comuna_id "
                + "from generate_series( '" + _startDate + "'::timestamp, '" + _endDate + "'::timestamp, '1 day'::interval) as fecha_it "
                    + "	left outer join marca on (fecha_it::date = marca.fecha_hora::date "
                            + "and marca.empresa_cod = '" + _empresaId + "' and marca.rut_empleado='" + _rutEmpleado + "') "
                    + "	left outer join view_empleado empleado on (empleado.rut = '" + _rutEmpleado + "' "
                            + "and empleado.empresa_id = '" + _empresaId + "') "
                    + "left outer join detalle_turno "
                            + "on (detalle_turno.id_turno = empleado.empl_id_turno "
                            + "and detalle_turno.cod_dia = extract(isodow  from fecha_it::date)) "
                    + " left outer join calendario_feriados "
                        + "	on (fecha_it::date = calendario_feriados.fecha) "
                        + "	and (calendario_feriados.cal_region_id = " + _infoCenco.getRegionId() + " or calendario_feriados.cal_comuna_id = " + _infoCenco.getComunaId() + " or cal_id_tipo_feriado is not null) "
                        //+ " and ( empleado.comuna_id = calendario_feriados.cal_comuna_id or empleado.region_id = calendario_feriados.cal_region_id) "
                    + "left outer join tipo_marca_manual on (marca.cod_tpo_marca_manual = tipo_marca_manual.code) "
                    + "left outer join detalle_ausencia on (detalle_ausencia.rut_empleado='" + _rutEmpleado + "' " +
                        " and fecha_it::date between fecha_inicio and fecha_fin and ausencia_autorizada='S') " +
                    " left outer join ausencia on (detalle_ausencia.ausencia_id = ausencia.ausencia_id) "
                    + " where (fecha_it::date >= empleado.fecha_inicio_contrato) "
                    + " order by fecha_it::date,marca.cod_tipo_marca,fecha_hora";
            
            System.out.println("[MarcasDAO."
                + "getHashMarcasTurnoNormal]sql: " + sql);
                        
            dbConn = dbLocator.getConnection(m_dbpoolName,"[MarcasDAO.getHashMarcasTurnoNormal]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();
            int correlativo = 1;
            while (rs.next()){
                data = new InfoMarcaVO();
                String fecha = rs.getString("fecha_it");
                int regionIdFeriado = rs.getInt("feriado_region_id");
                int comunaIdFeriado = rs.getInt("feriado_comuna_id");
                int regionIdEmpleado = rs.getInt("empleado_region_id");
                int comunaIdEmpleado = rs.getInt("empleado_comuna_id");
                String labelCalendario = rs.getString("label_calendario");
                System.out.println("[MarcasDAO.getHashMarcasTurnoNormal]"
                    + "fecha= " + fecha
                    + ", regionIdFeriado= " + regionIdFeriado
                    + ", comunaIdFeriado= " + comunaIdFeriado
                    + ", cenco.region= " + _infoCenco.getRegionId()
                    + ", cenco.comuna= " + _infoCenco.getComunaId()    
                    + ", regionIdEmpleado= " + regionIdEmpleado
                    + ", comunaIdEmpleado= " + comunaIdEmpleado
                    + ", labelCalendario= " + labelCalendario);
                //if (regionIdFeriado == 0 && comunaIdFeriado == 0) feriadoNacional = true;
                
                /**
                 * fecha= 2021-08-20, 
                 * regionIdFeriado= -1, 
                 * regionIdEmpleado= 1,
                 * 
                 * comunaIdFeriado= 180, 
                 * comunaIdEmpleado= 1, 
                 * labelCalendario= Nacimiento del Procer de la Independencia
                */

                if (regionIdFeriado > 0 && regionIdFeriado != _infoCenco.getRegionId()){
                    System.out.println("[MarcasDAO.getHashMarcasTurnoNormal]"
                        + "set label calendario NULL (1)");
                    labelCalendario = null;
                }
                if (comunaIdFeriado > 0 && comunaIdFeriado != _infoCenco.getComunaId()){
                    System.out.println("[MarcasDAO.getHashMarcasTurnoNormal]"
                        + "set label calendario NULL (2)");
                    labelCalendario = null;
                }
                
                //if (feriadoNacional || feriadoRegional || feriadoComunal){
                    data.setFecha(fecha);
                    data.setFechaLabel(rs.getString("fecha_label"));
                    data.setId(rs.getString("id"));
                    data.setHashcode(rs.getString("hashcode"));
                    data.setCodDia(rs.getInt("cod_dia"));
                    data.setEmpresaId(rs.getString("empresa_cod"));
                    data.setRutEmpleado(rs.getString("rut_empleado"));
                    data.setFechaHoraMarca(rs.getString("fecha_hora_marca"));
                    data.setFechaHoraMarcaLabel(rs.getString("fecha_hora_marca_label"));
                    data.setHoraMarca(rs.getString("solo_hora_marca"));
                    data.setTipoMarca(rs.getInt("tipo_marca"));
                    data.setCodDispositivo(rs.getString("cod_dispositivo"));
                    data.setIdTurnoEmpleado(rs.getInt("empl_id_turno"));
                    data.setIdTurnoAsignado(rs.getInt("id_turno_asignado"));
                    data.setHoraEntradaTurno(rs.getString("hora_entrada_teorica"));
                    data.setHoraSalidaTurno(rs.getString("hora_salida_teorica"));  
                    data.setLabelCalendario(labelCalendario);

                    data.setAusenciaNombre(rs.getString("ausencia_nombre"));
                    data.setAusenciaPorHora(rs.getString("ausencia_por_hora"));
                    data.setFechaInicioAusencia(rs.getString("inicio_ausencia"));
                    data.setFechaFinAusencia(rs.getString("fin_ausencia"));
                    data.setHoraInicioAusencia(rs.getString("hora_inicio"));
                    data.setHoraFinAusencia(rs.getString("hora_fin"));

                    data.setHora(rs.getString("solohora"));
                    data.setMinutos(rs.getString("solomins"));
                    data.setSegundos(rs.getString("solosecs"));
                    data.setFechaHoraActualizacion(rs.getString("fecha_actualizacion"));
                    data.setCodTipoMarcaManual(rs.getInt("cod_tpo_marca_manual"));//
                    data.setNombreTipoMarcaManual(rs.getString("display_name"));
                    data.setComentario(rs.getString("comentario"));
                    data.setCorrelativo(rs.getInt("correlativo"));//

                    data.setRowKey(_empresaId
                        + "|" + _rutEmpleado
                        + "|" + data.getFecha()
                        + "|" + data.getCodDia()
                        + "|" + data.getTipoMarca()
                        + "|" + correlativo);

                    hashMarcas.put(data.getRowKey(),data);
                    correlativo++;
                //}
            }
            
            ps.close();
            rs.close();
            if (dbConn != null){    
                dbConn.close();
            }
        }catch(SQLException sqle){
            System.err.println("[MarcasDAO.getHashMarcasTurnoNormal]"
                + "Error1: " + sqle.toString());
            sqle.printStackTrace();
        }catch(DatabaseException dbex){
            System.err.println("[MarcasDAO.getHashMarcasTurnoNormal]"
                + "Error2: " + dbex.toString());
            dbex.printStackTrace();
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                if (dbConn != null){    
                    dbConn.close();
                    dbConn.close();
                }
            } catch (SQLException ex) {
                System.err.println("[MarcasDAO."
                    + "getHashMarcasTurnoNormal]"
                    + "Error2: " + ex.toString());
            }
        }
        
        return hashMarcas;
    }
    
    /**
    * 
    * @return 
    */
    public int getNewCorrelativoMarca(){
        int newid = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            String sql ="select nextval('correlativo_marca_seq') newid";
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[MarcasDAO.getNewCorrelativoMarca]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                newid = rs.getInt("newid");
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
        return newid;
    }
}
