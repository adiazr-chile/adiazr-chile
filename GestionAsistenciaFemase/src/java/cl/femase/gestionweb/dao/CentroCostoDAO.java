/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.CentroCostoVO;
import cl.femase.gestionweb.vo.DispositivoVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioCentroCostoVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexander
 */
public class CentroCostoDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    
    //private Connection dbConn;
    //DbDirectConnection dbconnection = DbDirectConnection.getInstance();
    public boolean m_usedGlobalDbConnection = false;
    
    public CentroCostoDAO(PropertiesVO _propsValues) {
//        try {
//            dbLoc = DatabaseLocator.getInstance();
//            m_dbpoolName = _propsValues.getDbPoolName();
//        } catch (DatabaseException ex) {
//            m_logger.error("DbError: "+ex.toString());
//        }
    }

    /**
     * Actualiza un centro de costo
     * @param _data
     * @return 
     */
    public ResultCRUDVO update(CentroCostoVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "centro costo, "
            + "id: "+_data.getId()
            + ", nombre: "+_data.getNombre()
            + ", deptoId: "+_data.getDeptoId()
            + ", estado: "+_data.getEstado()
            + ", direccion: "+_data.getDireccion()
            + ", id_comuna: "+_data.getComunaId()
            + ", telefonos: "+_data.getTelefonos()
            + ", email: "+_data.getEmail()
            + ", zonaExtrema: "+_data.getZonaExtrema();
        
        try{
            String msgFinal = " Actualiza centro de costo:"
                + "id [" + _data.getId() + "]" 
                + ", nombre [" + _data.getNombre() + "]"
                + ", deptoId [" + _data.getDeptoId()+ "]"    
                + ", estado [" + _data.getEstado() + "]"
                + ", direccion [" + _data.getDireccion() + "]"
                + ", id_comuna [" + _data.getComunaId() + "]"
                + ", telefonos [" + _data.getTelefonos() + "]"
                + ", email [" + _data.getEmail() + "]"
                + ", emailNotificacion [" + _data.getEmailNotificacion()+ "]"
                + ", zonaExtrema [" + _data.getZonaExtrema()+ "]"    ;
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE centro_costo "
                    + " SET "
                    + "ccosto_nombre = ?, "
                    + "estado_id = ?, "
                    + "direccion = ?,"
                    + "id_comuna = ?,"
                    + "telefonos = ?,"
                    + " email = ?,"
                    + " depto_id = ?, "
                    + "email_notificacion = ?,"
                    + "es_zona_extrema = ? "
                    + " WHERE ccosto_id = ?";
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[CentroCostoDAO.update]");
            
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setString(1,  _data.getNombre());
            psupdate.setInt(2,  _data.getEstado());
            
            psupdate.setString(3,  _data.getDireccion());
            psupdate.setInt(4,  _data.getComunaId());
            psupdate.setString(5,  _data.getTelefonos());
            psupdate.setString(6,  _data.getEmail());
            psupdate.setString(7,  _data.getDeptoId());
            psupdate.setString(8,  _data.getEmailNotificacion());
            psupdate.setString(9,  _data.getZonaExtrema());
            psupdate.setInt(10,  _data.getId());
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println(WEB_NAME+"[update]centro costo"
                    + ", id:" +_data.getId()
                    + ", deptoId:" +_data.getDeptoId()    
                    + ", nombre:" +_data.getNombre()
                    + ", estado:" +_data.getEstado()
                    + ", direccion:" +_data.getDireccion()    
                    +" actualizado OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("update centro costo Error: "+sqle.toString());
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
     * Agrega un nuevo centro de costo
     * @param _data
     * @return 
     */
    public ResultCRUDVO insert(CentroCostoVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        int result=0;
        String msgError = "Error al insertar "
            + "centro de costo. "
            + " id: "+_data.getId()
            + ", deptoId: "+_data.getDeptoId()    
            + ", nombre: "+_data.getNombre()
            + ", direccion: "+_data.getDireccion()
            + ", email: "+_data.getEmail()
            + ", email_notificacion: "+_data.getEmailNotificacion()
            + ", zonaExtrema: "+_data.getZonaExtrema();
        
       String msgFinal = " Inserta centro de costo:"
            + "id [" + _data.getId() + "]"
            + " deptoId [" + _data.getDeptoId() + "]"   
            + " nombre [" + _data.getNombre() + "]"
            + " direccion [" + _data.getDireccion() + "]"
            + " email [" + _data.getEmail() + "]"
            + " emailNotificacion [" + _data.getEmailNotificacion()+ "]"
            + " zonaExtrema [" + _data.getZonaExtrema() + "]";
            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO centro_costo(" +
                    "ccosto_id, "
                    + "ccosto_nombre, "
                    + "estado_id,"
                    + "direccion,"
                    + "id_comuna,"
                    + "telefonos,"
                    + "email,"
                    + "depto_id,"
                    + "email_notificacion,"
                    + "es_zona_extrema) "
                + " VALUES (nextval('centro_costo_ccosto_id_seq'), "
                    + "?, "
                    + "?, ?, ?, ?, ?, ?, ?, ?)";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[CentroCostoDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getNombre());
            insert.setInt(2,  _data.getEstado());
            
            insert.setString(3,  _data.getDireccion());
            insert.setInt(4,  _data.getComunaId());
            insert.setString(5,  _data.getTelefonos());
            insert.setString(6,  _data.getEmail());
            insert.setString(7,  _data.getDeptoId());
            insert.setString(8,  _data.getEmailNotificacion());
            insert.setString(9,  _data.getZonaExtrema());
            
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println(WEB_NAME+"[insert centro de costo]"
                    + ", nombre:" +_data.getNombre()
                    + ", id:" +_data.getId()
                    + ", deptoId:" +_data.getDeptoId()
                    + ", direccion:" +_data.getDireccion()
                    + ", telefonos:" +_data.getTelefonos()
                    + ", email:" +_data.getEmail()
                    + ", emailNotificacion:" +_data.getEmailNotificacion()
                    + ", zonaExtrema:" +_data.getZonaExtrema()
                    +" insertado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("insert centro de costo Error1: "+sqle.toString());
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
    * Retorna lista con los centro de costos existentes en el sistema
    * 
    * @param _deptoId
    * @param _nombre
    * @param _estado
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * @return 
    */
    public List<CentroCostoVO> getCentrosCosto(String _deptoId, 
            String _nombre,
            int _estado,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<CentroCostoVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        CentroCostoVO data;
        
        try{
            String sql = "SELECT cc.ccosto_id,"
                    + "cc.ccosto_nombre,"
                    + "cc.estado_id,"
                    + "cc.direccion,"
                    + "cc.id_comuna,"
                    + "cc.telefonos,"
                    + "cc.email,"
                    + "cc.depto_id,"
                    + "depto.depto_nombre,"
                    + "comuna.region_id,"
                    + "depto.empresa_id,"
                    + "empresa.empresa_nombre,"
                    + "cc.email_notificacion,"
                    + "coalesce(cc.es_zona_extrema,'N') es_zona_extrema,"
                    + "comuna.comuna_nombre," 
                    + "estado.estado_nombre "
                + " FROM centro_costo cc "
                    + " inner join comuna on cc.id_comuna = comuna.comuna_id "
                    + " inner join region on comuna.region_id = region.region_id "
                    + " inner join departamento depto on cc.depto_id = depto.depto_id "
                    + " inner join empresa on depto.empresa_id = empresa.empresa_id "
                    + " inner join estado on cc.estado_id = estado.estado_id "
                + " where ccosto_id <> -1 ";
           
            if (_deptoId != null && _deptoId.compareTo("-1") != 0){        
                sql += " and cc.depto_id = '" + _deptoId + "'";
            }
            
            if (_nombre != null && _nombre.compareTo("") != 0){        
                sql += " and upper(cc.ccosto_nombre) like '%"+_nombre.toUpperCase()+"%'";
            }
            if (_estado != -1){        
                sql += " and cc.estado_id = "+_estado;
            }
           
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            System.err.println("[CentroCostoDAO.getCentrosCosto]Sql: " + sql);
            
            if (!m_usedGlobalDbConnection) dbConn = dbLocator.getConnection(m_dbpoolName,"[CentroCostoDAO.getCentrosCosto]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new CentroCostoVO();
                data.setId(rs.getInt("ccosto_id"));
                data.setNombre(rs.getString("ccosto_nombre"));
                data.setEstado(rs.getInt("estado_id"));
                
                data.setDireccion(rs.getString("direccion"));
                data.setComunaId(rs.getInt("id_comuna"));
                data.setRegionId(rs.getInt("region_id"));
                data.setTelefonos(rs.getString("telefonos"));
                data.setEmail(rs.getString("email"));
                
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setEmpresaNombre(rs.getString("empresa_nombre"));
                data.setDeptoId(rs.getString("depto_id"));
                data.setDeptoNombre(rs.getString("depto_nombre"));
                data.setEmailNotificacion(rs.getString("email_notificacion"));
                data.setZonaExtrema(rs.getString("es_zona_extrema"));
                //System.out.println(WEB_NAME+"rescatar dispositivos asignados a cencoId= "+data.getId());
                //data.setDispositivos(this.getDispositivosAsignados(data.getId()));
                
                data.setEstadoNombre(rs.getString("estado_nombre"));
                data.setComunaNombre(rs.getString("comuna_nombre"));
                
                lista.add(data);
            }

            ps.close();
            rs.close();
            if (!m_usedGlobalDbConnection) dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("Error: "+sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                if (!m_usedGlobalDbConnection) dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
        return lista;
    }
    
    /**
    * Retorna empleados con cargo Director en el cenco especificado
    * 
    * @param _empresaId
    * @param _deptoId
    * @param _cencoId
    * @return 
    */
    public List<EmpleadoVO> getDirectoresCenco(String _empresaId, 
            String _deptoId, 
            int _cencoId){
        
        List<EmpleadoVO> lista = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        EmpleadoVO data;
        try{
            String sql = "select distinct(upper(empl_rut)) rut,"
                + "empl_nombres || ' ' || empl_ape_paterno|| ' ' || empl_ape_materno nombre, "
                + "empl_email email,"
                + "autoriza_ausencia, empl_estado,cargo.cargo_nombre "
                + "from empleado inner join admingestionweb.cargo on (empleado.empl_id_cargo=cargo.cargo_id) "
                + "where cenco_id = " + _cencoId
                + " and empl_id_cargo = " + Constantes.ID_CARGO_DIRECTOR 
                + " and empl_estado = 1 and autoriza_ausencia=true ";//
                //+ " order by empl_rut";
       
            if (!m_usedGlobalDbConnection) dbConn = 
                dbLocator.getConnection(m_dbpoolName,"[CentroCostoDAO.getDirectoresCenco]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new EmpleadoVO();
                data.setRut(rs.getString("rut"));
                data.setNombres(rs.getString("nombre"));
                data.setEmail(rs.getString("email"));
                data.setAutorizaAusencia(rs.getBoolean("autoriza_ausencia"));
                data.setEstado(rs.getInt("empl_estado"));
                data.setNombreCargo(rs.getString("cargo_nombre"));
                
                lista.add(data);
            }

            ps.close();
            rs.close();
            if (!m_usedGlobalDbConnection) dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[CentroCostoDAO.getDirectoresCenco]"
                + "Error_1: " + sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                if (!m_usedGlobalDbConnection) dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[CentroCostoDAO.getDirectoresCenco]"
                    + "Error_2: " + ex.toString());
            }
        }
        return lista;
    }
    
    /**
    * 
    * @param _deptoId
    * @param _cencoId
    * @return 
    */
    public CentroCostoVO getCentroCostoByKey(String _deptoId,
            int _cencoId){
        
        CentroCostoVO cenco = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            String sql = "select ccosto_nombre,"
                    + "estado_id,"
                    + "email,"
                    + "es_zona_extrema,"
                    + "comuna.region_id,"
                    + "centro_costo.id_comuna "
                + "from centro_costo "
                    + "inner join comuna on (centro_costo.id_comuna = comuna.comuna_id ) "
                + "where depto_id = '" + _deptoId + "' and ccosto_id = " + _cencoId;
            
            System.out.println(WEB_NAME+"[CentroCostoDAO.getCentroCostoByKey]"
                + "Sql: " + sql);
             
            dbConn = dbLocator.getConnection(m_dbpoolName,"[CentroCostoDAO.getCentroCostoByKey]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                cenco = new CentroCostoVO();
                cenco.setId(_cencoId);
                cenco.setNombre(rs.getString("ccosto_nombre"));
                cenco.setEstado(rs.getInt("estado_id"));
                cenco.setEmail(rs.getString("email"));
                cenco.setDeptoId(_deptoId);
                cenco.setZonaExtrema(rs.getString("es_zona_extrema"));
                cenco.setRegionId(rs.getInt("region_id"));
                cenco.setComunaId(rs.getInt("id_comuna"));
            }

            ps.close();
            rs.close();
            if (!m_usedGlobalDbConnection) dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("Error: "+sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                if (!m_usedGlobalDbConnection) dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
        return cenco;
    }
    
    /**
    * 
     * @param _empresaId
     * @param _cencoId
    * @return 
    */
    public CentroCostoVO getDepartamentoByCentroCosto(String _empresaId,
            int _cencoId){
        
        CentroCostoVO cenco = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            String sql = "select centro_costo.depto_id, "
                    + "ccosto_nombre,"
                    + "email,"
                    + "es_zona_extrema "
                + "from centro_costo "
                    + "inner join departamento d "
                    + " on (centro_costo.depto_id = d.depto_id "
                        + "and d.empresa_id = '" + _empresaId + "')"
                + " where ccosto_id = " + _cencoId;
            
            System.out.println(WEB_NAME+"[CentroCostoDAO.getDepartamentoByCentroCosto]"
                + "Sql: " + sql);
             
            dbConn = dbLocator.getConnection(m_dbpoolName,"[CentroCostoDAO.getDepartamentoByCentroCosto]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                cenco = new CentroCostoVO();
                cenco.setEmpresaId(_empresaId);
                cenco.setDeptoId(rs.getString("depto_id"));
                cenco.setId(_cencoId);
                cenco.setNombre(rs.getString("ccosto_nombre"));
                cenco.setEmail(rs.getString("email"));
                cenco.setZonaExtrema(rs.getString("es_zona_extrema"));
            }

            ps.close();
            rs.close();
            if (!m_usedGlobalDbConnection) dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("Error: "+sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                if (!m_usedGlobalDbConnection) dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
        return cenco;
    }
    
    /**
     * Retorna lista con los centro de costos existentes en el sistema.
     * Si el usuario no es asmin, se muestran solo los cencos asignados
     * 
     * @param _usuario
     * @param _deptoId
     * @return 
     */
    public List<CentroCostoVO> getCentrosCostoDepto(UsuarioVO _usuario,String _deptoId){ 
            
        List<CentroCostoVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        CentroCostoVO data;
        
        try{
            /**
             * Para todos los perfiles de usuario, 
             * excepto el perfil de administrador, solo
             * se muestran los deptos/centros de costo
             * definidos para el respectivo usuario.
            */
            String strcencos = "-1";
            List cencos = _usuario.getCencos();
            if (_usuario.getAdminEmpresa().compareTo("N") == 0 && cencos.size() > 0){
                strcencos = "";
                Iterator<UsuarioCentroCostoVO> cencosIt = cencos.iterator();
                while (cencosIt.hasNext()) {
                    strcencos += cencosIt.next().getCcostoId()+",";
                }
                strcencos = strcencos.substring(0, strcencos.length()-1);
            }
            String sql = "SELECT cc.ccosto_id,"
                    + "cc.ccosto_nombre,"
                    + "cc.estado_id,"
                    + "cc.direccion,"
                    + "cc.id_comuna,"
                    + "cc.telefonos,"
                    + "cc.email,"
                    + "cc.depto_id,"
                    + "depto.depto_nombre,"
                    + "comuna.region_id,"
                    + "depto.empresa_id,"
                    + "empresa.empresa_nombre,"
                    + "coalesce(cc.es_zona_extrema,'N') es_zona_extrema "
                + " FROM centro_costo cc "
                    + " inner join comuna on cc.id_comuna = comuna.comuna_id "
                    + " inner join region on comuna.region_id = region.region_id "
                    + " inner join departamento depto on cc.depto_id = depto.depto_id "
                    + " inner join empresa on depto.empresa_id = empresa.empresa_id "
                + "where (cc.depto_id = '" + _deptoId + "' ";
            
            if (_usuario.getIdPerfil() != Constantes.ID_PERFIL_SUPER_ADMIN 
                    && _usuario.getAdminEmpresa().compareTo("S") == 0){ 
                sql += " and (depto.empresa_id = '" + _usuario.getEmpresaId() + "') ";
            }
            
            if (strcencos.compareTo("-1") != 0){
                sql += " and (cc.ccosto_id in (" + strcencos + ")) ";
            }
                
            sql+= ")";
            sql+= "order by cc.depto_id,cc.ccosto_nombre ";

            if (!m_usedGlobalDbConnection) dbConn = 
                dbLocator.getConnection(m_dbpoolName,"[CentroCostoDAO.getCentrosCostoDepto]");
            
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new CentroCostoVO();
                data.setId(rs.getInt("ccosto_id"));
                data.setNombre(rs.getString("ccosto_nombre"));
                data.setEstado(rs.getInt("estado_id"));
                
                data.setDireccion(rs.getString("direccion"));
                data.setComunaId(rs.getInt("id_comuna"));
                data.setRegionId(rs.getInt("region_id"));
                data.setTelefonos(rs.getString("telefonos"));
                data.setEmail(rs.getString("email"));
                
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setEmpresaNombre(rs.getString("empresa_nombre"));
                data.setDeptoId(rs.getString("depto_id"));
                data.setDeptoNombre(rs.getString("depto_nombre"));
                data.setZonaExtrema(rs.getString("es_zona_extrema"));
                //System.out.println(WEB_NAME+"rescatar dispositivos asignados a cencoId= "+data.getId());
                //data.setDispositivos(this.getDispositivosAsignados(data.getId()));
                
                lista.add(data);
            }

            ps.close();
            rs.close();
            if (!m_usedGlobalDbConnection) dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("[getCentrosCostoDepto]Error: "+sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                if (!m_usedGlobalDbConnection) dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
        return lista;
    }
    
    /**
     * Retorna lista con todos los centro de costos existentes en el sistema.
     * 
     * @param _username
     * @return 
     */
    public List<UsuarioCentroCostoVO> getAllCentrosCosto(String _username){ 
            
        List<UsuarioCentroCostoVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        UsuarioCentroCostoVO data;
        
        try{
            String sql = "SELECT "
                + "empresa.empresa_id,"
                + "empresa.empresa_nombre,"
                + "depto.depto_id,"
                + "depto.depto_nombre,"
                + "cc.ccosto_id,"
                + "cc.ccosto_nombre,"
                + "coalesce(cc.es_zona_extrema,'N') es_zona_extrema "
                + "FROM centro_costo cc "
                + "inner join comuna on cc.id_comuna = comuna.comuna_id "
                + "inner join region on comuna.region_id = region.region_id "
                + " inner join departamento depto on cc.depto_id = depto.depto_id "
                + " inner join empresa on depto.empresa_id = empresa.empresa_id "
                + " order by empresa.empresa_id,cc.depto_id,cc.ccosto_nombre";
       
            dbConn = dbLocator.getConnection(m_dbpoolName,"[CentroCostoDAO.getAllCentrosCosto]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new UsuarioCentroCostoVO(_username,
                    rs.getInt("ccosto_id"),0,rs.getString("ccosto_nombre"),
                    rs.getString("empresa_id"),rs.getString("depto_id"));
                
                String empresaNombre = rs.getString("empresa_nombre");
                if (empresaNombre.compareTo("Fundacion Mi Casa") == 0){
                    data.setEmpresaNombre("FMC");
                }else data.setEmpresaNombre(empresaNombre);
                
                data.setDeptoNombre(rs.getString("depto_nombre"));
                data.setZonaExtrema(rs.getString("es_zona_extrema"));
                lista.add(data);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[getAllCentrosCosto]Error: "+sqle.toString());
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
    * Retorna lista con los centro de costos existentes en el sistema
    * a los cuales el usuario tiene acceso
    * 
    * @param _usuario
    * @param _deptoId
    * @param _nombre
    * @param _estado
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * @return 
    */
    public List<CentroCostoVO> getCentrosCosto(UsuarioVO _usuario, String _deptoId, 
            String _nombre,
            int _estado,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<CentroCostoVO> lista = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        CentroCostoVO data;
        
        try{
            String strcencos = "-1";
            List cencos = _usuario.getCencos();
            
            System.out.println(WEB_NAME+"cl.femase.gestionweb."
                + "service.CentroCostoDAO."
                + "getCentrosCosto. "
                + "Cencos usuario.size= " + cencos.size());
            if (_usuario.getAdminEmpresa().compareTo("N") == 0 && cencos.size() > 0){
                strcencos = "";
                Iterator<UsuarioCentroCostoVO> cencosIt = cencos.iterator();
                while (cencosIt.hasNext()) {
                    strcencos += cencosIt.next().getCcostoId()+",";
                }
                strcencos = strcencos.substring(0, strcencos.length()-1);
            }
            
            String sql = "SELECT cc.ccosto_id,"
                + "cc.ccosto_nombre,"
                + "cc.estado_id,"
                + "cc.direccion,"
                + "cc.id_comuna,"
                + "cc.telefonos,"
                + "cc.email,"
                + "cc.depto_id,"
                + "depto.depto_nombre,"
                + "comuna.region_id,"
                + "depto.empresa_id,"
                + "empresa.empresa_nombre,"
                + "cc.es_zona_extrema "
                + " FROM centro_costo cc "
                + " inner join comuna on cc.id_comuna = comuna.comuna_id "
                + " inner join region on comuna.region_id = region.region_id "
                + " inner join departamento depto on cc.depto_id = depto.depto_id "
                + " inner join empresa on depto.empresa_id = empresa.empresa_id "
                + " where 1=1 ";
           
            if (_usuario.getIdPerfil() != Constantes.ID_PERFIL_SUPER_ADMIN 
                    && _usuario.getAdminEmpresa().compareTo("S") == 0){ 
                sql += " and (depto.empresa_id = '" + _usuario.getEmpresaId() + "') ";
            }
            if (_deptoId != null && _deptoId.compareTo("-1") != 0){        
                sql += " and cc.depto_id = '" + _deptoId + "'";
            }
            
            if (_nombre != null && _nombre.compareTo("") != 0){        
                sql += " and upper(cc.ccosto_nombre) like '%"+_nombre.toUpperCase()+"%'";
            }
            if (_estado != -1){        
                sql += " and cc.estado_id = "+_estado;
            }
            
            if (strcencos.compareTo("-1") != 0){
                sql += " and (cc.ccosto_id in (" + strcencos + ")) ";
            }
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
       
            System.out.println(WEB_NAME+"cl.femase.gestionweb."
                + "service.CentroCostoDAO."
                + "getCentrosCosto. SQL: " + sql);
            dbConn = dbLocator.getConnection(m_dbpoolName,"[CentroCostoDAO.getCentrosCosto]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new CentroCostoVO();
                data.setId(rs.getInt("ccosto_id"));
                data.setNombre(rs.getString("ccosto_nombre"));
                data.setEstado(rs.getInt("estado_id"));
                
                data.setDireccion(rs.getString("direccion"));
                data.setComunaId(rs.getInt("id_comuna"));
                data.setRegionId(rs.getInt("region_id"));
                data.setTelefonos(rs.getString("telefonos"));
                data.setEmail(rs.getString("email"));
                
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setEmpresaNombre(rs.getString("empresa_nombre"));
                data.setDeptoId(rs.getString("depto_id"));
                data.setDeptoNombre(rs.getString("depto_nombre"));
                data.setZonaExtrema(rs.getString("es_zona_extrema"));
//                System.out.println(WEB_NAME+"rescatar dispositivos asignados a cencoId= "+data.getId());
                //data.setDispositivos(this.getDispositivosAsignados(data.getId()));
                
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
    * Retorna lista con los centro de costos existentes en el sistema
    * a los cuales el usuario tiene acceso
    * 
    * @param _usuario
    * 
    * @return 
     * @throws java.sql.SQLException 
    */
    public List<CentroCostoVO> getCentrosCostoEmpresa(UsuarioVO _usuario) throws SQLException{
        List<CentroCostoVO> lista = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        CentroCostoVO cenco;
        
        try{
            String sql = "SELECT "
                + "empresa.empresa_id,"
                + "empresa.empresa_nombre empresanombre,"
                + "departamento.depto_id,"
                + "departamento.depto_nombre deptonombre,"
                + "centro_costo.ccosto_id,"
                + "centro_costo.ccosto_nombre cenconombre,"
                + "coalesce(centro_costo.es_zona_extrema,'N') es_zona_extrema,"
                + "centro_costo.estado_id ";
            
            sql += "FROM departamento "
                + "left outer join empresa "
                    + "on (departamento.empresa_id = empresa.empresa_id) "
                + "left outer join centro_costo "
                    + "on (departamento.depto_id = centro_costo.depto_id "
                    + "and centro_costo.estado_id = 1)";  
            
            if (_usuario.getIdPerfil() != Constantes.ID_PERFIL_SUPER_ADMIN){  
                sql += " WHERE (1 = 1) ";
                if (_usuario.getAdminEmpresa().compareTo("S") == 0){ 
                    sql += " and empresa.empresa_id = '" + _usuario.getEmpresaId() + "' ";
                    sql += " and (departamento.estado_id = 1) ";
                }
            }
            sql += " order by "
                + "empresa.empresa_id,"
                + "departamento.depto_id,"
                + "centro_costo.ccosto_nombre";
                    
            System.out.println(WEB_NAME+"CentroCostoDAO]"
                + "getCentrosCostoEmpresa. "
                + "SQL: " + sql);
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[CentroCostoDAO.getCentrosCostoEmpresa]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                cenco = new CentroCostoVO();
                
                cenco.setEmpresaId(rs.getString("empresa_id"));
                cenco.setEmpresaNombre(rs.getString("empresanombre"));
                cenco.setDeptoId(rs.getString("depto_id"));
                cenco.setDeptoNombre(rs.getString("deptonombre"));
                cenco.setId(rs.getInt("ccosto_id"));
                cenco.setNombre(rs.getString("cenconombre"));
                cenco.setEstado(rs.getInt("estado_id"));
                cenco.setZonaExtrema(rs.getString("es_zona_extrema"));
                
                lista.add(cenco);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[CentroCostoDAO."
                + "getCentrosCostoEmpresa]Error: " + sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[CentroCostoDAO."
                    + "getCentrosCostoEmpresa]Error: " + ex.toString());
            }
        }
        return lista;
    }
    
    /**
    * Retorna lista con los centro de costos
    * 
    * @param _usuario
    * @param _term
    * @return 
    * @throws java.sql.SQLException 
    */
    public List<CentroCostoVO> buscarCentrosCostoPorFiltro(UsuarioVO _usuario, 
            String _term) throws SQLException{
        List<CentroCostoVO> lista = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        CentroCostoVO cenco;
        
        try{
            String sql = "select d.depto_id, d.depto_nombre, "
                + "cc.ccosto_id cenco_id,"
                + "cc.ccosto_nombre cenco_nombre "
                + "from centro_costo cc "
                + "inner join departamento d "
                    + "on (d.empresa_id ='" + _usuario.getEmpresaId() + "' and d.depto_id = cc.depto_id) "
                + "where cc.estado_id = 1 "
                + "and (upper(cc.ccosto_nombre) like '%" + _term.toUpperCase() + "%') "
                + "order by cc.ccosto_nombre";
                    
            System.out.println(WEB_NAME+"CentroCostoDAO]"
                + "buscarCentrosCostoPorFiltro. "
                + "SQL: " + sql);
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[CentroCostoDAO.buscarCentrosCostoPorFiltro]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                cenco = new CentroCostoVO();
                
                cenco.setEmpresaId(_usuario.getEmpresaId());
                cenco.setDeptoId(rs.getString("depto_id"));
                cenco.setDeptoNombre(rs.getString("depto_nombre"));
                cenco.setId(rs.getInt("cenco_id"));
                cenco.setNombre(rs.getString("cenco_nombre"));
                
                lista.add(cenco);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[CentroCostoDAO."
                + "buscarCentrosCostoPorFiltro]Error: " + sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[CentroCostoDAO."
                    + "buscarCentrosCostoPorFiltro]Error: " + ex.toString());
            }
        }
        return lista;
    }
    
    /**
     * 
     * @param _deptoId
     * @param _nombre
     * @param _estado
     * @return 
     */
    public int getCentrosCostoCount(String _deptoId,
            String _nombre, int _estado){
        int count=0;
        Statement statement = null;
        ResultSet rs = null;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[CentroCostoDAO.getCentrosCostoCount]");
            statement = dbConn.createStatement();
            String strSql ="SELECT count(ccosto_id) count "
                + " FROM centro_costo cc "
                + "	inner join comuna on cc.id_comuna = comuna.comuna_id "
                + "	inner join region on comuna.region_id = region.region_id "
                + " inner join departamento depto on cc.depto_id = depto.depto_id ";
            
            if (_deptoId != null && _deptoId.compareTo("-1") != 0){        
                strSql += " and cc.depto_id = '" + _deptoId + "'";
            }
            
            if (_nombre!=null && _nombre.compareTo("")!=0){        
                strSql += " and upper(cc.ccosto_nombre) like '%"+_nombre.toUpperCase()+"%'";
            }
            if (_estado != -1){        
                strSql += " and cc.estado_id = "+_estado;
            }
            System.out.println(WEB_NAME+"cl.femase.gestionweb.service."
                + "CentroCostoDAO.getCentrosCostoCount(). sql: "+strSql);
                        
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
    * @param _cencoId
    * @return 
    */
    public HashMap<String,DispositivoVO> getDispositivosAsignados(int _cencoId){
        HashMap<String,DispositivoVO> listado = new HashMap<>();
        DispositivoVO data=new DispositivoVO();
        Statement statement = null;
        ResultSet rs = null;
        try {
            if (!m_usedGlobalDbConnection) dbConn = dbLocator.getConnection(m_dbpoolName,"[CentroCostoDAO.getDispositivosAsignados]");
            statement = dbConn.createStatement();
            String sql = "SELECT dispositivo.device_id, "
                + "tipo.dev_type_name tipo, "
                + "dispositivo.fabricante,"
                + "dispositivo.modelo,"
                + "dispositivo.direccion,"
                + "region.short_name || '-' || comuna.comuna_nombre label_comuna "
                + "FROM dispositivo_centrocosto asignacion "
                    + "inner join dispositivo dispositivo on (dispositivo.device_id = asignacion.device_id) "
                    + "inner join tipo_dispositivo tipo on dispositivo.type_id = tipo.dev_type_id "
                    + "left outer join comuna on (dispositivo.comuna_id = comuna.comuna_id) " 
                    + "left outer join region on (comuna.region_id = region.region_id) "
                + " where dispositivo.estado = 1 ";
            if (_cencoId != -1){
                sql += " and asignacion.cenco_id = " + _cencoId; 
            }
            sql += " order by asignacion.device_id";
              
            rs = statement.executeQuery(sql);		
            while (rs.next()) {
                data = new DispositivoVO();
                data.setId(rs.getString("device_id"));
                data.setNombreTipo(rs.getString("tipo"));
                data.setFabricante(rs.getString("fabricante"));
                data.setModelo(rs.getString("modelo"));
                data.setLabelComuna(rs.getString("label_comuna"));
                data.setDireccion(rs.getString("direccion"));
                
                listado.put(""+data.getId(), data);
            }
            
            statement.close();
            rs.close();
            if (!m_usedGlobalDbConnection) dbLocator.freeConnection(dbConn);
        } catch (SQLException|DatabaseException e) {
                e.printStackTrace();
        }finally{
            try {
                if (statement != null) statement.close();
                if (rs != null) rs.close();
                if (!m_usedGlobalDbConnection) dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
        
        return listado;
    }
    
    
    /**
    *   Retorna aquellos dispositivos que no estan asignados a ningun centro de costo 
    *   @return 
    */
    public HashMap<String,DispositivoVO> getDispositivosNoAsignados(){
        HashMap<String,DispositivoVO> listado = new HashMap<>();
        DispositivoVO data=new DispositivoVO();
        Statement statement = null;
        ResultSet rs = null;
        try {
            if (!m_usedGlobalDbConnection) dbConn = dbLocator.getConnection(m_dbpoolName,"[CentroCostoDAO.getDispositivosAsignados]");
            statement = dbConn.createStatement();
            String sql = "SELECT "
                    + "dispositivo.device_id,"
                    + "tipo.dev_type_name tipo,"
                    + "dispositivo.fabricante,"
                    + "dispositivo.modelo,"
                    + "dispositivo.direccion,"
                    + "region.short_name || '-' || comuna.comuna_nombre label_comuna "
                    + "FROM dispositivo "
                    + "inner join tipo_dispositivo tipo on dispositivo.type_id = tipo.dev_type_id "
                    + "left outer join comuna on (dispositivo.comuna_id = comuna.comuna_id) "
                    + "left outer join region on (comuna.region_id = region.region_id) "
                    + "where dispositivo.device_id not in (select device_id from dispositivo_centrocosto) "
                    + "and dispositivo.estado = 1 "
                    + "order by dispositivo.device_id";            
            rs = statement.executeQuery(sql);		
            while (rs.next()) {
                data = new DispositivoVO();
                data.setId(rs.getString("device_id"));
                data.setNombreTipo(rs.getString("tipo"));
                data.setFabricante(rs.getString("fabricante"));
                data.setModelo(rs.getString("modelo"));
                data.setLabelComuna(rs.getString("label_comuna"));
                data.setDireccion(rs.getString("direccion"));
                
                listado.put("" + data.getId(), data);
            }
            
            statement.close();
            rs.close();
            if (!m_usedGlobalDbConnection) dbLocator.freeConnection(dbConn);
        } catch (SQLException|DatabaseException e) {
                e.printStackTrace();
        }finally{
            try {
                if (statement != null) statement.close();
                if (rs != null) rs.close();
                if (!m_usedGlobalDbConnection) dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
        
        return listado;
    }
    
    
    
    public void openDbConnection(){
        try {
            m_usedGlobalDbConnection = true;
            dbConn = dbLocator.getConnection(m_dbpoolName,"[CentroCostoDAO.openDbConnection]");
        } catch (DatabaseException ex) {
            System.err.println("[CentroCostoDAO.openDbConnection]"
                + "Error: " + ex.toString());
        }
    }
    
    /**
     * Retorna nombre de empresa, depto y nombre de centro de costo en formato json
     * 
     * @param _empresaId
     * @param _deptoId
     * @param _cencoId
     * 
     * @return 
     */
    public String getEmpresaDeptoCencoJson(String _empresaId, 
            String _deptoId, 
            int _cencoId){

        String strJson = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            String sql = "SELECT "
                + "get_empresaDeptoCenco_json"
                + "('" + _empresaId + "','" + _deptoId + "'," + _cencoId + ") strjson";
            if (_deptoId.compareTo("-1")==0 && _cencoId == -1){
                sql = "SELECT "
                + "get_empresa_json"
                    + "('" + _empresaId + "') strjson";
            }
            System.out.println(WEB_NAME+"[CentroCostoDAO."
                + "getEmpresaDeptoCencoJson]Sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[CentroCostoDAO.getEmpresaDeptoCencoJson]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                strJson += rs.getString("strjson");
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[CentroCostoDAO."
                + "getEmpresaDeptoCencoJson]Error: "+sqle.toString());
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
    
    public void closeDbConnection(){
        try {
            m_usedGlobalDbConnection = false;
            dbLocator.freeConnection(dbConn);
        } catch (Exception ex) {
            System.err.println("[CentroCostoDAO.closeDbConnection]"
                + "Error: "+ex.toString());
        }
    }
}
