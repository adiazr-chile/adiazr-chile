/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.ParametroVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexander
 */
public class ParametroDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    
    public ParametroDAO(PropertiesVO _propsValues) {

    }
    
    /**
    * Retorna lista con los parametros existentes
    * 
    * @param _empresaId
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * @return 
    */
    public List<ParametroVO> getParametros(String _empresaId,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<ParametroVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        ParametroVO data;
        
        try{
            String sql = "SELECT "
                + "empresa_id, param_code, "
                + "param_label, param_value, "
                + "create_datetime,last_update "
                + "FROM parametro "
                + "where 1 = 1 ";

            if (_empresaId != null && _empresaId.compareTo("") != 0){        
                sql += " and empresa_id = '" +_empresaId+"' ";
            }
            
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[ParametroDAO.getParametros]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new ParametroVO();
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setParamCode(rs.getString("param_code"));
                data.setParamLabel(rs.getString("param_label"));
                data.setValor(rs.getDouble("param_value"));
                data.setFechaCreacion(rs.getString("create_datetime"));
                data.setFechaModificacion(rs.getString("last_update"));
                
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
    * Retorna lista con los parametros existentes para la empresa especificada
    * 
    * @param _empresaId
    * @return 
    */
    public HashMap<String, Double> getParametrosEmpresa(String _empresaId){
        
        HashMap<String, Double> parametros = new HashMap<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
                
        try{
            String sql = "SELECT "
                + "param_code, param_value "
                + "FROM parametro "
                + "where empresa_id = '" + _empresaId + "' "
                + "order by param_code"; 
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[ParametroDAO.getParametrosEmpresa]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                String paramCode = rs.getString("param_code");
                double paramValue = rs.getDouble("param_value");
                
                parametros.put(paramCode, paramValue);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("Error: "+sqle.toString());
        }
        
        return parametros;
    }
    
    /**
    * Retorna lista con los parametros existentes
    * 
    * @param _empresaId
    * @param _paramCode
    * 
    * @return 
    */
    public ParametroVO getParametroByKey(String _empresaId,String _paramCode){
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        ParametroVO data=null;
        
        try{
            String sql = "SELECT "
                + "empresa_id, param_code, "
                + "param_label, param_value, "
                + "create_datetime,last_update "
                + "FROM parametro "
                + "where "
                    + "empresa_id='" + _empresaId + "' "
                    + "and param_code = '" + _paramCode + "' ";
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[ParametroDAO.getParametroByKey]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                data = new ParametroVO();
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setParamCode(rs.getString("param_code"));
                data.setParamLabel(rs.getString("param_label"));
                data.setValor(rs.getDouble("param_value"));
                data.setFechaCreacion(rs.getString("create_datetime"));
                data.setFechaModificacion(rs.getString("last_update"));
                
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("Error: "+sqle.toString());
        }finally {
            try {
                ps.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[MaintenanceEventsDAO.addEvent]Error: "+ ex.toString());
            }
        }
        
        return data;
    }
    
    /**
    * 
    * @param _empresaId
    * @return 
    */
    public int getParametrosCount(String _empresaId){
        int count=0;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[ParametroDAO.getParametrosCount]");
            Statement statement = dbConn.createStatement();
            String strSql ="SELECT count(param_code) "
                + "FROM parametro "
                + "WHERE 1 = 1 ";

            if (_empresaId != null && _empresaId.compareTo("") != 0){        
                strSql += " and empresa_id = '" +_empresaId+"' ";
            }
            
            ResultSet rs = statement.executeQuery(strSql);		
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
    * Agrega un nuevo parametro
    * @param _data
    * @return 
    */
    public MaintenanceVO insert(ParametroVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al insertar "
            + "Parametro. "
            + " empresaId: " + _data.getEmpresaId()
            + ", code: " + _data.getParamCode()
            + ", label: " + _data.getParamLabel()
            + ", valor: " + _data.getValor();
        
       String msgFinal = " Inserta Parametro:"
            + "empresaId [" + _data.getEmpresaId() + "],"
            +  ", code [" + _data.getParamCode() + "]"
            +  ", label [" + _data.getParamLabel() + "]"
            +  ", valor [" + _data.getValor() + "]";            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO parametro("
                + "empresa_id, "
                + "param_code, "
                + "param_label, "
                + "param_value, "
                + "create_datetime) "
                + "VALUES (?, ?, ?, ?, current_timestamp)";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[ParametroDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getEmpresaId());
            insert.setString(2,  _data.getParamCode());
            insert.setString(3,  _data.getParamLabel());
            insert.setDouble(4,  _data.getValor());
                                               
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println("[ParametroDAO.insert parametro]"
                    + ", empresa:" + _data.getEmpresaId()
                    + ", code:" + _data.getParamCode()
                    + ", label:" + _data.getParamLabel()
                    + ", valor:" + _data.getValor()    
                    +" insertado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[ParametroDAO.insert parametro]Error_1: " + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError + " :" + sqle.toString());
        }finally{
            try {
                if (insert != null) insert.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[ParametroDAO.insert parametro]Error_2: " + ex.toString());
            }
        }
        return objresultado;
    }
    
    /**
    * Actualiza un parametro
    * @param _data
    * @return 
    */
    public MaintenanceVO update(ParametroVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "parametro, "
            + "empresa_id: " + _data.getEmpresaId()
            + ", code: " + _data.getParamCode()
            + ", label: " + _data.getParamLabel()
            + ", valor: " + _data.getValor();
        
        try{
            String msgFinal = " Actualiza parametro:"
                + "empresa_id [" + _data.getEmpresaId() + "]" 
                + ", code [" + _data.getParamCode()+ "]"
                + ", label [" + _data.getParamLabel() + "]"
                + ", valor [" + _data.getValor() + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE parametro "
                + "SET empresa_id = ?, "
                    + "param_label = ?, "
                    + "param_value = ?, "
                    + "last_update = current_timestamp "
                    + "WHERE param_code = ?";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[ParametroDAO.update]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setString(1,  _data.getEmpresaId());
            psupdate.setString(2,  _data.getParamLabel());
            psupdate.setDouble(3,  _data.getValor());
            psupdate.setString(4,  _data.getParamCode());
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println("[ParametroDAO.update]"
                    + ", empresa_id:" +_data.getEmpresaId()
                    + ", code:" +_data.getParamCode()
                    + ", label:" +_data.getParamLabel()
                    + ", valor:" +_data.getValor()    
                    +" actualizado OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[ParametroDAO.update]Error_1: " + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError + " :" + sqle.toString());
        }finally{
            try {
                if (psupdate != null) psupdate.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[ParametroDAO.update]Error_2: " + ex.toString());
            }
        }

        return objresultado;
    }
}
