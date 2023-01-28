/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.DepartamentoVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioCentroCostoVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexander
 */
public class DepartamentoDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    //private Connection dbConn;
    //DbDirectConnection dbconnection = DbDirectConnection.getInstance();
    
    /**
     *
     * @param _propsValues
     */
    public DepartamentoDAO(PropertiesVO _propsValues) {
//        try {
//            dbLoc = DatabaseLocator.getInstance();
//            m_dbpoolName = _propsValues.getDbPoolName();
//        } catch (DatabaseException ex) {
//            m_logger.error("DbError: "+ex.toString());
//        }
    }

    /**
    * Retorna info de departamento
    * 
    * @param _deptoId
    * @return 
    */
    public DepartamentoVO getDepartamentoByKey(String _deptoId){
        DepartamentoVO departamento = null;       
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            String sql = "select depto_id,"
                + "depto_nombre,"
                + "empresa_id,"
                + "estado_id "
                + "from departamento "
                + "where depto_id = '" + _deptoId + "'";
            System.out.println(WEB_NAME+"[DepartamentoDAO.getDepartamentoByKey]"
                + "Sql: " + sql);
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[DepartamentoDAO.getDepartamentoByKey]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                departamento = 
                    new DepartamentoVO(rs.getString("depto_id"), rs.getString("depto_nombre"), rs.getString("empresa_id"));
                departamento.setEstado(rs.getInt("estado_id"));
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
                System.err.println("[DepartamentoDAO."
                    + "getDepartamentoByKey]Error: "+ex.toString());
            }
        } 
        return departamento;
    }
    
    /**
     * Actualiza un departamento
     * @param _data
     * @return 
     */
    public ResultCRUDVO update(DepartamentoVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "Departamento, "
            + "id: "+_data.getId()
            + ", empresaId: "+_data.getEmpresaId()    
            + ", nombre: "+_data.getNombre()
            + ", estado: "+_data.getEstado();
        
        try{
            String msgFinal = " Actualiza departamento:"
                + "id [" + _data.getId() + "]" 
                + ", empresaid [" + _data.getEmpresaId() + "]"    
                + ", nombre [" + _data.getNombre() + "]"
                + ", estado [" + _data.getEstado() + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE departamento "
                + " SET "
                + "depto_nombre = ?,"
                + "empresa_id = ?,"
                + "estado_id = ? "
                + "WHERE depto_id=?";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[DepartamentoDAO.update]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setString(1,  _data.getNombre());
            psupdate.setString(2,  _data.getEmpresaId());
            psupdate.setInt(3,  _data.getEstado());
            psupdate.setString(4,  _data.getId());
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println(WEB_NAME+"[update]departamento"
                    + ", id:" +_data.getId()
                    + ", empresaId:" +_data.getEmpresaId()
                    + ", estado:" +_data.getEstado()    
                    + ", nombre:" +_data.getNombre()
                    +" actualizado OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("update departamento Error: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (psupdate!=null) psupdate.close();
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
    public ResultCRUDVO insert(DepartamentoVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        int result=0;
        String msgError = "Error al insertar "
            + "departamento. "
            + " id: "+_data.getId()
            + ", nombre: "+_data.getNombre()
            + ", empresa: "+_data.getEmpresaId()
            + ", estado: "+_data.getEstado();
        
       String msgFinal = " Inserta departamento:"
            + "id [" + _data.getId() + "]"
            + "nombre [" + _data.getNombre() + "]"
            + "empresa [" + _data.getEmpresaId() + "]"
            + "estado [" + _data.getEstado() + "]";
            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO departamento "
                + "(depto_id, depto_nombre, empresa_id, estado_id) "
                + " VALUES (?, ?, ?, ?)";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[DepartamentoDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getId());
            insert.setString(2,  _data.getNombre());
            insert.setString(3,  _data.getEmpresaId());
            insert.setInt(4,  _data.getEstado());
                                    
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println(WEB_NAME+"[insert departamento]"
                    + ", nombre:" +_data.getNombre()
                    + ", id:" +_data.getId()
                    +" insertado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("insert departamento Error1: "+sqle.toString());
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
     * Elimina  un departamento
     * @param _data
     * @return 
     */
    public ResultCRUDVO delete(DepartamentoVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        int result=0;
        String msgError = "Error al eliminar "
            + "un departamento, Id: "+_data.getId()
            + ", nombre: "+_data.getNombre();
        
       String msgFinal = " Elimina departamento:"
            + "Id [" + _data.getId() + "]" 
            + ", nombre [" + _data.getNombre() + "]";
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "delete from "
                + "departamento where depto_id= ?";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[DepartamentoDAO.delete]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getId());
                        
            int filasAfectadas = insert.executeUpdate();
            m_logger.debug("[delete departamento]filasAfectadas: "+filasAfectadas);
            if (filasAfectadas == 1){
                m_logger.debug("[delete departamento]"
                    + ", id:" +_data.getId()
                    + ", nombre:" +_data.getNombre()
                    +" eliminado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("delete departamento Error: "+sqle.toString());
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
     * Retorna lista con los departamentos existentes en el sistema
     * 
     * @param _empresaId
     * @param _nombre
     * @param _jtStartIndex
     * @param _jtPageSize
     * @param _jtSorting
     * @return 
     */
    public List<DepartamentoVO> getDepartamentos(String _empresaId, 
            String _nombre,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<DepartamentoVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        DepartamentoVO data;
        
        try{
            String sql ="SELECT "
                + "depto_id, "
                + "depto_nombre,"
                + "empresa_id, "
                + "estado_id "
                + "FROM "
                + "departamento "
                + "where 1=1 ";
           
            if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
                sql += " and empresa_id = '" + _empresaId + "'";
            }
            
            if (_nombre!=null && _nombre.compareTo("")!=0){        
                sql += " and upper(depto_nombre) like '%"+_nombre.toUpperCase()+"%'";
            }
           
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            System.out.println(WEB_NAME+"[cl.femase.gestionweb."
                + "service.DepartamentoDAO."
                + "getDepartamentos]sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[DepartamentoDAO.getDepartamentos]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new DepartamentoVO();
                data.setId(rs.getString("depto_id"));
                data.setNombre(rs.getString("depto_nombre"));
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setEstado(rs.getInt("estado_id"));
               
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
    * Retorna lista de departamentos existentes.
    * 
    * @param _usuario
    * @param _empresaId
    * @return 
    */
    public List<DepartamentoVO> getDepartamentosEmpresa(UsuarioVO _usuario, 
            String _empresaId){
        
        List<DepartamentoVO>  lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        DepartamentoVO data;
        
        try{
            /**
             * Para todos los perfiles de usuario, 
             * excepto el perfil de administrador, solo
             * se muestran los deptos/centros de costo
             * definidos para el respectivo usuario.
            */
            System.out.println(WEB_NAME+"[DepartamentoDAO]"
                + "getDepartamentosEmpresa. "
                + "Usuario: " + _usuario.getUsername());
            String strdeptos = "-1";
            if (_usuario.getIdPerfil() != Constantes.ID_PERFIL_SUPER_ADMIN){
                List cencos = _usuario.getCencos();
                System.out.println(WEB_NAME+"[DepartamentoDAO]"
                    + "getDepartamentosEmpresa. "
                    + "Cencos usuario.size= " + cencos.size());
                if (cencos.size() > 0){
                    strdeptos = "";
                    Iterator<UsuarioCentroCostoVO> cencosIt = cencos.iterator();
                    while (cencosIt.hasNext()) {
                        strdeptos += "'" + cencosIt.next().getDeptoId()+"',";
                    }
                    strdeptos = strdeptos.substring(0, strdeptos.length()-1);
                }
            }
            
            String sql ="SELECT "
                + "depto_id, "
                + "depto_nombre,"
                + "empresa_id, "
                + "estado_id "
                + "FROM departamento "
                + "where (empresa_id = '" + _empresaId + "') ";
            
            if (strdeptos.compareTo("-1") != 0){
                sql += " and (depto_id in (" + strdeptos + ") ) ";
            }
           
            if (_usuario.getIdPerfil() != Constantes.ID_PERFIL_SUPER_ADMIN){
                sql += " and (estado_id = 1) ";
            }
            
            sql += "order by empresa_id,depto_id";
            System.out.println(WEB_NAME+"[DepartamentoDAO]sql: "+sql);
            dbConn = dbLocator.getConnection(m_dbpoolName,"[DepartamentoDAO.getDepartamentosEmpresa]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new DepartamentoVO();
                data.setId(rs.getString("depto_id"));
                data.setNombre(rs.getString("depto_nombre"));
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setEstado(rs.getInt("estado_id"));
               
                lista.add(data);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("[getDepartamentosEmpresa]Error: "+sqle.toString());
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
     * @return 
     */
    public int getDepartamentosCount(String _empresaId,
            String _nombre){
        int count=0;
        Statement statement = null;
        ResultSet rs = null;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[DepartamentoDAO.getDepartamentosCount]");
            statement = dbConn.createStatement();
            String strSql ="SELECT count(depto_id) "
                + "FROM departamento where 1=1 ";
             
            if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
                strSql += " and empresa_id = '" + _empresaId + "'";
            }
            
            if (_nombre!=null && _nombre.compareTo("")!=0){        
                strSql += " and upper(depto_nombre) like '%"+_nombre.toUpperCase()+"%'";
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
   
}
