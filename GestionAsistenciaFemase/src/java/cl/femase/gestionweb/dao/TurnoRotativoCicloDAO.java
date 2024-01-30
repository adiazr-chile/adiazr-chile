/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.TurnoRotativoCicloVO;
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
public class TurnoRotativoCicloDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    
    /**
    * Modifica un ciclo de turno rotativo
    * 
    * @param _data
    * @return 
    */
    public ResultCRUDVO update(TurnoRotativoCicloVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al modificar "
            + "tabla turno_rotativo_ciclo. "
            + "Correlativo: " + _data.getCorrelativo()    
            + ", empresa_id: " + _data.getEmpresaId()
            + ", num_dias: " + _data.getNumDias()
            + ", etiqueta: " + _data.getEtiqueta();
        
        try{
            String msgFinal = " Modifica turno_rotativo_ciclo:"
                + "Correlativo [" + _data.getCorrelativo() + "]"
                + ", empresa_id [" + _data.getEmpresaId() + "]" 
                + ", num_dias [" + _data.getNumDias() + "]"
                + ", etiqueta [" + _data.getEtiqueta() + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE turno_rotativo_ciclo "
            + "SET ciclo_num_dias=?, "
            + "ciclo_etiqueta=?, "
            + "ciclo_fecha_modificacion=current_timestamp "
            + "WHERE ciclo_correlativo=?";
            
            dbConn = dbLocator.getConnection(m_dbpoolName, "[TurnoRotativoCicloDAO.update]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setInt(1,  _data.getNumDias());
            psupdate.setString(2, _data.getEtiqueta());
            psupdate.setInt(3,  _data.getCorrelativo());
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println(WEB_NAME+"[TurnoRotativoCicloDAO.update]"
                    + "turno_rotativo_ciclo"
                    + ", correlativo: " + _data.getCorrelativo()
                    + ", empresa_id: " + _data.getEmpresaId()
                    + ", num_dias:" + _data.getNumDias()
                    + ", etiqueta:" + _data.getEtiqueta()    
                    +" modificado OK!");
            }
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[TurnoRotativoCicloDAO.update]"
                + "turno_rotativo_ciclo Error: " + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }
        finally{
            try {
                if (psupdate != null) psupdate.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[TurnoRotativoCicloDAO.update]"
                    + "Error: " + ex.toString());
            }
        }

        return objresultado;
    }

     /**
     * Agrega un nuevo cod error rechazo
     * 
     * @param _data
     * @return 
     */
    public ResultCRUDVO insert(TurnoRotativoCicloVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        int result=0;
        String msgError = "Error al insertar "
            + "turno_rotativo_ciclo. "
            + " Empresa_id: " + _data.getEmpresaId()
            + ", num_dias: " + _data.getNumDias()
            + ", etiqueta: " + _data.getEtiqueta();
        
        String msgFinal = " Inserta turno_rotativo_ciclo:"
            + "Empresa_id [" + _data.getEmpresaId() + "]"
            + ", num_dias [" + _data.getNumDias() + "]"
            + ", etiqueta [" + _data.getEtiqueta() + "]";
            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO turno_rotativo_ciclo("
                + "ciclo_correlativo, "
                + "empresa_id, "
                + "ciclo_num_dias, "
                + "ciclo_etiqueta, "
                + "ciclo_fecha_creacion) "
                + "VALUES (nextval('turno_ciclo_sequence'), ?, ?, ?, current_timestamp)";

            dbConn = dbLocator.getConnection(m_dbpoolName, "[TurnoRotativoCicloDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getEmpresaId());
            insert.setInt(2,  _data.getNumDias());
            insert.setString(3,  _data.getEtiqueta());
            
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println(WEB_NAME+"[TurnoRotativoCicloDAO.insert]"
                    + "turno_rotativo_ciclo"
                    + ", empresa_id: " + _data.getEmpresaId()
                    + ", num_dias:" + _data.getNumDias()
                    + ", etiqueta:" + _data.getEtiqueta()    
                    +" creado OK!");
            }
            
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[TurnoRotativoCicloDAO.insert]"
                + "insert turno_rotativo_ciclo, Error1: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }
        finally{
            try {
                if (insert != null) insert.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[TurnoRotativoCicloDAO.insert]"
                    + "Error: "+ex.toString());
            }
        }
        return objresultado;
    }
    
    /**
    * Elimina un ciclo de turno rotativo
    * 
    * @param _data
    * @return 
    */
    public ResultCRUDVO delete(TurnoRotativoCicloVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        int result=0;
        String msgError = "Error al eliminar "
            + "un turno_rotativo_ciclo, correlativo: " + _data.getCorrelativo();
        
       String msgFinal = " Elimina turno_rotativo_ciclo:"
            + "Correlativo [" + _data.getCorrelativo() + "]" ;
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "delete from "
                + "turno_rotativo_ciclo "
                + "where ciclo_correlativo = ?";

            dbConn = dbLocator.getConnection(m_dbpoolName, "[TurnoRotativoCicloDAO.delete]");
            insert = dbConn.prepareStatement(sql);
            insert.setInt(1,  _data.getCorrelativo());
                        
            int filasAfectadas = insert.executeUpdate();
            m_logger.debug("[delete turno_rotativo_ciclo]filasAfectadas: " + filasAfectadas);
            if (filasAfectadas == 1){
                m_logger.debug("[delete turno_rotativo_ciclo]"
                    + ", correlativo:" + _data.getCorrelativo()
                    +" eliminado OK!");
            }
            
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("delete turno_rotativo_ciclo Error: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                insert.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[TurnoRotativoCicloDAO.delete]delete Error: "+ex.toString());
            }
        }

        return objresultado;
    }
        
    /**
    * Retorna lista con ciclos existentes
    * 
     * @param _empresaId
     * @param _numDias
     * @param _etiqueta
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * 
    * @return 
    */
    public List<TurnoRotativoCicloVO> getCiclos(String _empresaId, 
            int _numDias,
            String _etiqueta,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<TurnoRotativoCicloVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        TurnoRotativoCicloVO data;
        
        try{
            String sql = "SELECT ciclo_correlativo correlativo, "
                + "empresa_id, ciclo_num_dias, "
                + "ciclo_etiqueta, "
                + "to_char(ciclo_fecha_creacion,'yyyy-MM-dd HH24:MI:SS') fecha_creacion, "
                + "to_char(ciclo_fecha_modificacion,'yyyy-MM-dd HH24:MI:SS') fecha_modificacion "
                + "FROM turno_rotativo_ciclo "
                + "where empresa_id = '" + _empresaId + "' ";

            if (_numDias != 0){        
                sql += " and ciclo_num_dias =" + _numDias;
            }
            if (_etiqueta != null && _etiqueta.compareTo("") != 0){        
                sql += " and upper(ciclo_etiqueta) like '%" + _etiqueta.toUpperCase() + "%'";
            }
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            System.out.println(WEB_NAME+"[TurnoRotativoCicloDAO.getCiclos]"
                + "Sql: " + sql);
            
            dbConn = 
                dbLocator.getConnection(m_dbpoolName, "[TurnoRotativoCicloDAO.getCiclos]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                
                data = new TurnoRotativoCicloVO();
                data.setCorrelativo(rs.getInt("correlativo"));
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setNumDias(rs.getInt("ciclo_num_dias"));
                data.setEtiqueta(rs.getString("ciclo_etiqueta"));
                data.setFechaCreacion(rs.getString("fecha_creacion"));
                data.setFechaModificacion(rs.getString("fecha_modificacion"));
                
                lista.add(data);
            }
            
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[TurnoRotativoCicloDAO.getCiclos]"
                + "Error: " + sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[TurnoRotativoCicloDAO.getCiclos]"
                    + "Error: " + ex.toString());
            }
        }
        return lista;
    }
    
    /**
    * 
     * @param _empresaId
     * @param _numDias
     * @param _etiqueta
    * @return 
    */
    public int getCiclosCount(String _empresaId, 
            int _numDias,
            String _etiqueta){
        int count = 0;
        Statement statement = null;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName, "[TurnoRotativoCicloDAO.getCiclosCount]");
            statement = dbConn.createStatement();
            String strSql="SELECT count(*) as count "
                    + "FROM turno_rotativo_ciclo "
                    + "where empresa_id = '" + _empresaId + "' ";
            if (_numDias != 0){        
                strSql += " and ciclo_num_dias =" + _numDias;
            }
            if (_etiqueta != null && _etiqueta.compareTo("") != 0){        
                strSql += " and upper(ciclo_etiqueta) like '%" + _etiqueta.toUpperCase() + "%'";
            }
            ResultSet rs = statement.executeQuery(strSql);		
            if (rs.next()) {
                count=rs.getInt("count");
            }
            
            rs.close();
        } catch (SQLException|DatabaseException e) {
                e.printStackTrace();
        }
        finally{
            try {
                if (statement != null) statement.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[TurnoRotativoCicloDAO]"
                    + "getCiclosCount Error: "+ex.toString());
            }
        }
        return count;
    }
   
}
