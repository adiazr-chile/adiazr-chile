/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.EmpresaVO;
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
public class EmpresaDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    //private Connection dbConn;
    //private DatabaseLocator dbLoc;
    //DbDirectConnection dbconnection = DbDirectConnection.getInstance();
    
    public EmpresaDAO(PropertiesVO _propsValues) {
//        try {
//            dbLoc = DatabaseLocator.getInstance();
//            m_dbpoolName = _propsValues.getDbPoolName();
//        } catch (DatabaseException ex) {
//            m_logger.error("DbError: "+ex.toString());
//        }
    }
   
    
    /**
    * Retorna lista con las empresas existentes en el sistema
    * 
    * @param _nombre
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * @return 
    */
    public List<EmpresaVO> getEmpresas(String _nombre,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<EmpresaVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        EmpresaVO data;
        
        try{
            String sql = "SELECT "
                + "empresa_id,"
                + "empresa_nombre,"
                + "empresa_rut,"
                + "empresa_direccion,"
                + "empresa_estado,"
                + "estado.estado_nombre,"
                + "empresa.comuna_id,"
                + "comuna.comuna_nombre,"
                + "comuna.region_id,"
                + "region.region_nombre,"
                + "region.short_name region_shortname "
                + " from empresa "
                    + " left outer join comuna on (empresa.comuna_id=comuna.comuna_id) "
                    + " inner join region on (comuna.region_id = region.region_id) "
                    + " inner join estado on (empresa.empresa_estado = estado.estado_id) ";
            sql += " where 1 = 1 ";
           
            if (_nombre!=null && _nombre.compareTo("")!=0){        
                sql += " and upper(empresa_nombre) like '" + _nombre.toUpperCase() + "%'";
            }
           
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            System.out.println(WEB_NAME+"[EmpresasDAO.getEmpresas]Sql: " + sql);
            dbConn = dbLocator.getConnection(m_dbpoolName,"[EmpresaDAO.getEmpresas]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new EmpresaVO();
                data.setId(rs.getString("empresa_id"));
                data.setNombre(rs.getString("empresa_nombre"));
                data.setRut(rs.getString("empresa_rut"));
                data.setDireccion(rs.getString("empresa_direccion"));
                data.setEstadoId(rs.getInt("empresa_estado"));
                data.setEstadoNombre(rs.getString("estado_nombre"));
                data.setRegionId(rs.getInt("region_id"));
                data.setRegionNombre(rs.getString("region_nombre"));
                
                data.setComunaId(rs.getInt("comuna_id"));
                data.setComunaNombre(rs.getString("comuna_nombre"));
                
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
    * Retorna info de empresa
    * 
    * @param _empresaId
    * @return 
    */
    public EmpresaVO getEmpresaByKey(String _empresaId){
        
        EmpresaVO empresa = null;       
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            String sql = "SELECT "
                + "empresa_id,"
                + "empresa_nombre,"
                + "empresa_rut,"
                + "empresa_direccion,"
                + "empresa_estado,"
                + "empresa.comuna_id,"
                + "comuna.comuna_nombre,"
                + "comuna.region_id,"
                + "region.region_nombre,"
                + "region.short_name region_shortname "
                + "from empresa "
                + " left outer join comuna on (empresa.comuna_id=comuna.comuna_id) "
                + " inner join region on (comuna.region_id = region.region_id)";
            sql+= " where empresa_id='" + _empresaId + "' ";
                       
            dbConn = dbLocator.getConnection(m_dbpoolName,"[EmpresaDAO.getEmpresaByKey]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                empresa = new EmpresaVO();
                empresa.setId(rs.getString("empresa_id"));
                empresa.setNombre(rs.getString("empresa_nombre"));
                empresa.setRut(rs.getString("empresa_rut"));
                empresa.setDireccion(rs.getString("empresa_direccion"));
                empresa.setEstadoId(rs.getInt("empresa_estado"));
                
                empresa.setRegionId(rs.getInt("region_id"));
                empresa.setRegionNombre(rs.getString("region_nombre"));
                
                empresa.setComunaId(rs.getInt("comuna_id"));
                empresa.setComunaNombre(rs.getString("comuna_nombre"));
                
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
        return empresa;
    }
    
    /**
    * Retorna lista con las empresas existentes en el sistema.
    * Solo retorna las empresas a las que el usuario tiene acceso
    * Si el usuario tiene perfil de Super Admin, tendra acceso a todas las empresas.
    * Si el usuario tiene perfil 'Admin' de empresa, puede ver solo la respectiva empresa asignada a dicho usuario.
    * 
    * @param _usuario
    * @param _nombre
    * @return 
    */
    public List<EmpresaVO> getEmpresas(UsuarioVO _usuario, 
            String _nombre){
        List<EmpresaVO> lista = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        EmpresaVO data;
        
        try{
            String strcencos = "-1";
            List cencos = _usuario.getCencos();
            System.out.println(WEB_NAME+"[EmpresasDAO.getEmpresas]"
                + "usuario:" + _usuario.getUsername() 
                + ", adminEmpresa? " + _usuario.getAdminEmpresa()
                + ", cencos.size= " + cencos.size());
            if (_usuario.getAdminEmpresa().compareTo("N") == 0 && cencos.size() > 0){
                strcencos = "";
                Iterator<UsuarioCentroCostoVO> cencosIt = cencos.iterator();
                while (cencosIt.hasNext()) {
                    strcencos += cencosIt.next().getCcostoId()+",";
                }
                strcencos = strcencos.substring(0, strcencos.length()-1);
            }
                
            String sql ="SELECT "
                + "distinct(depto.empresa_id) empresa_id,"
                + "empresa.empresa_nombre, "
                + "empresa.empresa_rut,"
                + "empresa.empresa_direccion,"
                + "empresa.comuna_id, "
                + "empresa.empresa_estado "
                + "FROM "
                + " centro_costo cenco,"
                + "departamento depto,"
                + "empresa "
                + "WHERE "
                + "cenco.depto_id = depto.depto_id AND "
                + "depto.empresa_id = empresa.empresa_id ";
                
//            if (_usuario.getIdPerfil() != Constantes.ID_PERFIL_SUPER_ADMIN 
//                    && _usuario.getAdminEmpresa().compareTo("S") == 0){
//                sql += " AND depto.empresa_id  = '" + _usuario.getEmpresaId() + "'";
//            }
             
            if (_usuario.getIdPerfil() != Constantes.ID_PERFIL_SUPER_ADMIN){
                sql += " AND depto.empresa_id  = '" + _usuario.getEmpresaId() + "'";
            }
            
            if (_nombre != null && _nombre.compareTo("") != 0){        
                sql += " and upper(empresa_nombre) like '"+_nombre.toUpperCase()+"%'";
            }
            if (strcencos.compareTo("-1") != 0){
                sql += "and (cenco.ccosto_id in (" + strcencos + ")) ";
            }
            sql += " order by empresa_nombre";

            System.out.println(WEB_NAME+"[EmpresasDAO.getEmpresas]"
                + ", Sql: "+sql);

            dbConn = dbLocator.getConnection(m_dbpoolName,"[EmpresaDAO.getEmpresas]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new EmpresaVO();
                data.setId(rs.getString("empresa_id"));
                data.setNombre(rs.getString("empresa_nombre"));
                data.setRut(rs.getString("empresa_rut"));
                data.setDireccion(rs.getString("empresa_direccion"));
                data.setComunaId(rs.getInt("comuna_id"));
                data.setEstadoId(rs.getInt("empresa_estado"));
                
                System.out.println(WEB_NAME+"[EmpresasDAO.getEmpresas]"
                    + "add empresa_id: " + data.getId()
                    + ", nombre: " + data.getNombre());
                
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
     * 
     * @param _nombre
     * @return 
     */
    public int getEmpresasCount(String _nombre){
        int count=0;
        Statement statement = null;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[EmpresaDAO.getEmpresasCount]");
            statement = dbConn.createStatement();
            String strSql ="SELECT count(empresa_id) "
                + "FROM empresa where 1=1 ";
               
            if (_nombre!=null && _nombre.compareTo("")!=0){        
                strSql += " and upper(v) like '"+_nombre.toUpperCase()+"%'";
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
        }finally{
            try {
                if (statement != null) statement.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        } 
        
        return count;
    }
   
    /**
     * Actualiza una empresa
     * @param _data
     * @return 
     */
    public ResultCRUDVO update(EmpresaVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "empresa, "
            + "id: "+_data.getId()
            + ", nombre: "+_data.getNombre()
            + ", rut: "+_data.getRut()
            + ", direccion: "+_data.getDireccion()
            + ", comunaId: "+_data.getRegionId();
        
        try{
            String msgFinal = " Actualiza empresa:"
                + "id [" + _data.getId() + "]" 
                + ", nombre [" + _data.getNombre() + "]"
                + ", rut [" + _data.getRut()+ "]"    
                + ", direccion [" + _data.getDireccion() + "]"
                + ", comunaId [" + _data.getComunaId() + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE empresa "
                + "SET empresa_nombre = ?, "
                + "empresa_rut = ?, "
                + "empresa_direccion = ?,"
                + "comuna_id = ?, "
                + "empresa_estado = ? "
                + " WHERE empresa_id = ?";
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[EmpresaDAO.update]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setString(1,  _data.getNombre());
            psupdate.setString(2,  _data.getRut());
            psupdate.setString(3,  _data.getDireccion());
            psupdate.setInt(4,  _data.getComunaId());
            psupdate.setInt(5,  _data.getEstadoId());
            psupdate.setString(6,  _data.getId());
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println(WEB_NAME+"[update]empresa"
                    + ", id:" +_data.getId()
                    + ", rut:" +_data.getRut()    
                    + ", nombre:" +_data.getNombre()
                    + ", direccion:" +_data.getDireccion()
                    + ", comunaId:" +_data.getComunaId()    
                    +" actualizada OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("update empresa Error: "+sqle.toString());
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
     * Agrega una nueva empresa
     * @param _data
     * @return 
     */
    public ResultCRUDVO insert(EmpresaVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        int result=0;
        String msgError = "Error al insertar "
            + "empresa. "
            + " id: "+_data.getId()
            + ", rut: "+_data.getRut()    
            + ", nombre: "+_data.getNombre()
            + ", direccion: "+_data.getDireccion()
            + ", comunaId: "+_data.getComunaId();
        
       String msgFinal = " Inserta empresa:"
            + "id [" + _data.getId() + "]"
            + " rut [" + _data.getRut() + "]"   
            + " nombre [" + _data.getNombre() + "]"
            + " direccion [" + _data.getDireccion() + "]"
            + " comunaId [" + _data.getComunaId() + "]";
            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO empresa("
                + " empresa_id, empresa_nombre, "
                + "empresa_rut, empresa_direccion, "
                + "comuna_id, empresa_estado) "
                + " VALUES (?, ?, ?, ?, ?, ?)";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[EmpresaDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getId());
            insert.setString(2,  _data.getNombre());
            insert.setString(3,  _data.getRut());
            insert.setString(4,  _data.getDireccion());
            insert.setInt(5,  _data.getComunaId());
            insert.setInt(6,  _data.getEstadoId());
            
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println(WEB_NAME+"[insert empresa]"
                    + ", nombre:" +_data.getNombre()
                    + ", id:" +_data.getId()
                    + ", rut:" +_data.getRut()
                    + ", direccion:" +_data.getDireccion()
                    + ", comunaId:" +_data.getRegionId()
                    +" insertada OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("insert empresa Error1: "+sqle.toString());
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
}
