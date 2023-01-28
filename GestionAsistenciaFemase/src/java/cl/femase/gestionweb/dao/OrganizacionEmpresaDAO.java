/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.CentroCostoVO;
import cl.femase.gestionweb.vo.DepartamentoVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.OrganizacionEmpresaVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.sql.Connection;
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
 * @deprecated 
 */
public class OrganizacionEmpresaDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    //private Connection dbConn;
    //private DatabaseLocator dbLoc;
    //DbDirectConnection dbconnection = DbDirectConnection.getInstance();
    
    public OrganizacionEmpresaDAO(PropertiesVO _propsValues) {
//        
    }
   
    /**
     * Obtiene lista de departamemtos para la empresa seleccionada
     * 
     * @param _empresaId
     * @return 
     */
    public List<DepartamentoVO> getDepartamentos(String _empresaId){
        List<DepartamentoVO> lista = 
                new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        DepartamentoVO data;
        
        try{
            String sql ="SELECT "
                + "distinct(organizacion_empresa.depto_id) depto_id, "
                + "departamento.depto_nombre "
                + "FROM "
                    + "organizacion_empresa, "
                    + "empresa, "
                    + "departamento, "
                    + "centro_costo "
                + "WHERE "
                    + "organizacion_empresa.empresa_id = empresa.empresa_id AND "
                    + "organizacion_empresa.depto_id = departamento.depto_id AND "
                    + "organizacion_empresa.ccosto_id = centro_costo.ccosto_id "
                    + "and centro_costo.estado_id=1 "
                    + "and organizacion_empresa.empresa_id='"+_empresaId+"'";
            
            System.out.println(WEB_NAME+"[OrganizacionempresaDAO.getDepartamentos]"
                    + "empresa: "+_empresaId);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[OrganizacionEmpresaDAO.getDepartamentos]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new DepartamentoVO();
                data.setId(rs.getString("depto_id"));
                data.setNombre(rs.getString("depto_nombre"));
                
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
     * Obtiene lista de departamemtos para la empresa seleccionada
     * 
     * @param _empresaId
     * @param _deptoId
     * @return 
     */
    public List<CentroCostoVO> getCentrosCosto(String _empresaId, String _deptoId){
        List<CentroCostoVO> lista = 
                new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        CentroCostoVO data;
        
        try{
            String sql ="SELECT "
                    + "organizacion_empresa.ccosto_id id,"
                    + "centro_costo.ccosto_nombre nombre "
                + "FROM "
                    + "organizacion_empresa, "
                    + "empresa, "
                    + "departamento, "
                    + "centro_costo "
                + "WHERE "
                    + "organizacion_empresa.empresa_id = empresa.empresa_id AND "
                    + "organizacion_empresa.depto_id = departamento.depto_id AND "
                    + "organizacion_empresa.ccosto_id = centro_costo.ccosto_id "
                    + "and centro_costo.estado_id=1 "
                    + "and organizacion_empresa.empresa_id='"+_empresaId+"' "
                    + "AND organizacion_empresa.depto_id='"+_deptoId+"'";
            
            System.out.println(WEB_NAME+"[OrganizacionempresaDAO.getCentrosCosto]"
                + "empresa: "+_empresaId+", depto: "+_deptoId);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[OrganizacionEmpresaDAO.getCentrosCosto]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new CentroCostoVO();
                data.setId(rs.getInt("id"));
                data.setNombre(rs.getString("nombre"));
                
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
    
    //********organizacion empresa
    /**
     * Obtiene lista de departamemtos para la empresa seleccionada
     * 
     * @param _empresaId
     * @param _deptoId
     * @param _cencoId
     * @param _jtStartIndex
     * @param _jtPageSize
     * @param _jtSorting
     * @return 
     */
    public List<OrganizacionEmpresaVO> getOrganizacion(String _empresaId, 
            String _deptoId,int _cencoId,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        List<OrganizacionEmpresaVO> lista = 
                new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        OrganizacionEmpresaVO data;
        
        try{
            String sql ="SELECT "
                + "org.empresa_id,"
                + "emp.empresa_nombre,"
                + "org.depto_id depto_id, "
                + "depto.depto_nombre,"
                + "cc.ccosto_id,"
                + "cc.ccosto_nombre "
                + "FROM "
                + "organizacion_empresa org "
                + "inner join empresa emp on org.empresa_id = emp.empresa_id "
                + "inner join departamento depto on org.depto_id = depto.depto_id "
                + "inner join centro_costo cc on org.ccosto_id = cc.ccosto_id "
                + "WHERE org.empresa_id='"+_empresaId+"' ";
            
            if (_deptoId != null && _deptoId.compareTo("-1")!=0){        
                sql += " and org.depto_id= '" + _deptoId + "' ";
            }
            
            if (_cencoId != -1){        
                sql += " and org.ccosto_id= " + _cencoId;
            }
            
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            System.out.println(WEB_NAME+"[OrganizacionempresaDAO.getOrganizacion]"
                    + "empresa: "+_empresaId+", sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[OrganizacionEmpresaDAO.getOrganizacion]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new OrganizacionEmpresaVO();
               
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setEmpresaNombre(rs.getString("empresa_nombre"));
                data.setDeptoId(rs.getString("depto_id"));
                data.setDeptoNombre(rs.getString("depto_nombre"));
                data.setCencoId(rs.getInt("ccosto_id"));
                data.setCencoNombre(rs.getString("ccosto_nombre"));
               
                lista.add(data);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("OrganizacionempresaDAO.getOrganizacion Error: "+sqle.toString());
        }
        return lista;
    }
    
    /**
     * 
     * @param _empresaId
     * @param _deptoId
     * @param _cencoId
     * @return 
     */
    public int getOrganizacionCount(String _empresaId, String _deptoId,int _cencoId){
        int count=0;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[OrganizacionEmpresaDAO.getOrganizacionCount]");
            Statement statement = dbConn.createStatement();
            String strSql="SELECT "
                + "count(cc.ccosto_id) count "
                + "FROM "
                + "organizacion_empresa org "
                + "inner join empresa emp on org.empresa_id = emp.empresa_id "
                + "inner join departamento depto on org.depto_id = depto.depto_id "
                + "inner join centro_costo cc on org.ccosto_id = cc.ccosto_id "
                + "WHERE org.empresa_id='"+_empresaId+"' ";
            if (_deptoId != null && _deptoId.compareTo("-1")!=0){        
                strSql += " and org.depto_id= '" + _deptoId + "' ";
            }
             if (_cencoId != -1){        
                strSql += " and org.ccosto_id= '" + _deptoId + "' ";
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
     * Agrega un nuevo registro en organizacion empresa
     * @param _data
     * @return 
     */
    public ResultCRUDVO insert(OrganizacionEmpresaVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        int result=0;
        String msgError = "Error al insertar "
            + "organizacion_empresa. "
            + " id_empresa: "+_data.getEmpresaId()
            + ", id_depto: "+_data.getDeptoId()
            + ", id_cenco: "+_data.getCencoId();
        
       String msgFinal = " Inserta organizacion_empresa:"
            + "empresaID [" + _data.getEmpresaId() + "]"
            + ", deptoID [" + _data.getDeptoId()+ "]"
            + ", cencoID [" + _data.getCencoId() + "]";
            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO organizacion_empresa("
                + "empresa_id, depto_id, ccosto_id) "
                + "VALUES (?, ?, ?)";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[OrganizacionEmpresaDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getEmpresaId());
            insert.setString(2,  _data.getDeptoId());
            insert.setInt(3,  _data.getCencoId());
                        
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println(WEB_NAME+"[insert organizacion_empresa]"
                    + " id_empresa: "+_data.getEmpresaId()
                    + ", id_depto: "+_data.getDeptoId()
                    + ", id_cenco: "+_data.getCencoId()
                    +" insertado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("insert organizacion_empresa Error1: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }

        return objresultado;
    }
    
    /**
     * Elimina un registro en organizacion empresa
     * @param _data
     * @return 
     */
    public ResultCRUDVO delete(OrganizacionEmpresaVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        int result=0;
        String msgError = "Error al eliminar "
            + "organizacion_empresa. "
            + " id_empresa: "+_data.getEmpresaId()
            + ", id_depto: "+_data.getDeptoId()
            + ", id_cenco: "+_data.getCencoId();
        
       String msgFinal = " Elimina organizacion_empresa:"
            + "empresaID [" + _data.getEmpresaId() + "]"
            + ", deptoID [" + _data.getDeptoId()+ "]"
            + ", cencoID [" + _data.getCencoId() + "]";
            
        objresultado.setMsg(msgFinal);
        PreparedStatement delete    = null;
        
        try{
            String sql = "delete "
                + "from organizacion_empresa "
                + "where "
                + "empresa_id=? "
                + "and depto_id=? "
                + "and ccosto_id=?";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[OrganizacionEmpresaDAO.delete]");
            delete = dbConn.prepareStatement(sql);
            delete.setString(1,  _data.getEmpresaId());
            delete.setString(2,  _data.getDeptoId());
            delete.setInt(3,  _data.getCencoId());
                        
            int filasAfectadas = delete.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println(WEB_NAME+"[delete organizacion_empresa]"
                    + " id_empresa: "+_data.getEmpresaId()
                    + ", id_depto: "+_data.getDeptoId()
                    + ", id_cenco: "+_data.getCencoId()
                    +" eliminado OK!");
            }
            
            delete.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("delete organizacion_empresa Error1: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }
        
        return objresultado;
    }
}