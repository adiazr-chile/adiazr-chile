/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.ModuloAccesoPerfilVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexander
 */
public class ModuloAccesoPerfilDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    //private Connection dbConn;
    //private DatabaseLocator dbLoc;
    //DbDirectConnection dbconnection = DbDirectConnection.getInstance();
    
    public ModuloAccesoPerfilDAO(PropertiesVO _propsValues) {
//        try {
//            dbLoc = DatabaseLocator.getInstance();
//            m_dbpoolName = _propsValues.getDbPoolName();
//        } catch (DatabaseException ex) {
//            m_logger.error("DbError: "+ex.toString());
//        }
    }

//    /**
//     * Actualiza un acceso
//     * @param _data
//     * @return 
//     */
//    public MaintenanceVO update(ModuloAccesoPerfilVO _data){
//        MaintenanceVO objresultado = new MaintenanceVO();
//        PreparedStatement psupdate = null;
//        int result=0;
//        String msgError = "Error al actualizar "
//            + "acceso, "
//            + "id: "+_data.getId()
//            + ", nombre: "+_data.getNombre()
//            + ", label: "+_data.getLabel()
//            + ", url: "+_data.getUrl();
//        
//        try{
//            String msgFinal = " Actualiza acceso:"
//                + "id [" + _data.getId() + "]" 
//                + ", nombre [" + _data.getNombre() + "]"
//                + ", label [" + _data.getLabel() + "]"
//                + ", url [" + _data.getUrl() + "]";
//            
//            System.out.println(msgFinal);
//            objresultado.setMsg(msgFinal);
//            
//            String sql = "UPDATE acceso "
//                    + " SET "
//                    + "acceso_nombre=?, "
//                    + "acceso_label=?, "
//                    + "acceso_url=? "
//                    + " WHERE acceso_id=?";
//
//            dbConn = dbLocator.getConnection(m_dbpoolName);
//            psupdate = dbConn.prepareStatement(sql);
//            psupdate.setString(1,  _data.getNombre());
//            psupdate.setString(2,  _data.getLabel());
//            psupdate.setString(3,  _data.getUrl());
//            psupdate.setInt(4,  _data.getId());
//            int rowAffected = psupdate.executeUpdate();
//            if (rowAffected == 1){
//                System.out.println("[update]acceso"
//                        + ", id:" +_data.getId()
//                        + ", nombre:" +_data.getNombre()
//                        + ", label:" +_data.getLabel()
//                        + ", url:" +_data.getUrl()
//                        +" actualizado OK!");
//            }
//
//            psupdate.close();
//            dbLocator.freeConnection(dbConn);
//        }catch(SQLException|DatabaseException sqle){
//            System.err.println("update acceso Error: "+sqle.toString());
//            objresultado.setThereError(true);
//            objresultado.setCodError(result);
//            objresultado.setMsgError(msgError+" :"+sqle.toString());
//        }catch(DatabaseException dbe){
//            System.err.println("update acceso Error: "+dbe.toString());
//            objresultado.setThereError(true);
//            objresultado.setCodError(result);
//            objresultado.setMsgError(msgError+" :"+dbe.toString());
//        }finally{
//            dbLocator.freeConnection(dbConn);
//        }
//
//        return objresultado;
//    }

    /**
     * Agrega un nuevo acceso-modulo-perfil usuario
     * @param _data
     * @return 
     */
    public MaintenanceVO insert(ModuloAccesoPerfilVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al insertar "
            + "modulo acceso perfil-usuario. "
            + " modulo_id: "+_data.getModuloId()
            + ", acceso_id: "+_data.getAccesoId()
            + ", tipo_acceso: "+_data.getTipoAcceso()
            + ", orden_despliegue: "+_data.getOrdenDespliegue()
            + ", perfil_id: "+_data.getPerfilId();
        
       String msgFinal = " Inserta modulo_acceso_perfil:"
            + "modulo_id [" + _data.getModuloId() + "]"
            + ", acceso_id [" + _data.getAccesoId()+ "]"
            + ", tipo_acceso [" + _data.getTipoAcceso() + "]"
            + ", orden_despliegue [" + _data.getOrdenDespliegue() + "]"
            + ", perfil_id [" + _data.getPerfilId() + "]";
            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO modulo_acceso_perfil ("
                + " modulo_id, acceso_id, "
                + "tp_acceso_id, orden_despliegue, "
                + "perfil_id) "
                + " VALUES (?, ?, ?, ?, ?)";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[ModuloAccesoPerfilDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            insert.setInt(1,  _data.getModuloId());
            insert.setInt(2,  _data.getAccesoId());
            insert.setInt(3,  _data.getTipoAcceso());
            insert.setInt(4,  _data.getOrdenDespliegue());
            insert.setInt(5,  _data.getPerfilId());
            
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println("[insert "
                    + "modulo acceso perfil-usuario]"
                    + " modulo_id: "+_data.getModuloId()
                    + ", acceso_id: "+_data.getAccesoId()
                    + ", tipo_acceso: "+_data.getTipoAcceso()
                    + ", orden_despliegue: "+_data.getOrdenDespliegue()
                    + ", perfil_id: "+_data.getPerfilId()
                    +" insertado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("insert modulo acceso perfil-usuario Error1: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }

        return objresultado;
    }
    
    /**
     * Elimina los accesos existentes asociados
     * a un perfil-modulo
     * @param _data
     * @return 
     */
    public MaintenanceVO delete(ModuloAccesoPerfilVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al eliminar "
            + "accesos para perfilId: "+_data.getPerfilId()
            + ", moduloId: "+_data.getModuloId();
        
       String msgFinal = " Elimina accesos para:"
            + "perfilId [" + _data.getPerfilId() + "]" 
            + ", moduloId [" + _data.getModuloId() + "]";
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "delete FROM modulo_acceso_perfil "
                + "WHERE perfil_id=? and modulo_id=?";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[ModuloAccesoPerfilDAO.delete]");
            insert = dbConn.prepareStatement(sql);
            insert.setInt(1,  _data.getPerfilId());
            insert.setInt(2,  _data.getModuloId());
                        
            int filasAfectadas = insert.executeUpdate();
            m_logger.debug("[delete accesos perfil-modulo]filasAfectadas: "+filasAfectadas);
            if (filasAfectadas == 1){
                m_logger.debug("[delete accesos perfil-modulo]"
                        + ", perfilId:" +_data.getPerfilId()
                        + ", moduloId:" +_data.getModuloId()
                        +" eliminado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("delete Error: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }

        return objresultado;
    }
       
    /**
     * Retorna lista con los modulos de sistema existentes
     * 
     * @param _perfilUsuario
     * @param _moduloId
     * @return 
     */
    public LinkedHashMap<String,ModuloAccesoPerfilVO> getAccesosByModuloPerfil(int _perfilUsuario,int _moduloId){
        
        LinkedHashMap<String,ModuloAccesoPerfilVO> lista = 
            new LinkedHashMap<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        ModuloAccesoPerfilVO data;
        
        try{
            String sql = "SELECT ap.modulo_id, modulo.modulo_nombre,"
                + "ap.acceso_id, acceso.acceso_nombre,acceso.acceso_label,"
                + "tp_acceso_id, ap.orden_despliegue, "
                + "ap.perfil_id, perfil.perfil_nombre "
                + " FROM modulo_acceso_perfil ap "
                + "	inner join acceso on ap.acceso_id = acceso.acceso_id "
                + "	inner join modulo_sistema modulo on ap.modulo_id = modulo.modulo_id "
                + "	inner join perfil_usuario perfil on ap.perfil_id = perfil.perfil_id "
                + "  where 1 = 1 ";
            
            if (_perfilUsuario != -1){
                sql+=" and ap.perfil_id = "+_perfilUsuario;
            }
            if (_moduloId != -1){
                sql+=" and ap.modulo_id = "+_moduloId;
            }

            sql+=" order by ap.orden_despliegue";
            
            System.out.println("[ModuloAccesoPerfilDAO.getAccesosByModuloPerfil]"
                + ", Sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[ModuloAccesoPerfilDAO.getAccesosByModuloPerfil]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new ModuloAccesoPerfilVO();
                data.setModuloId(rs.getInt("modulo_id"));
                data.setModuloNombre(rs.getString("modulo_nombre"));
                data.setAccesoId(rs.getInt("acceso_id"));
                data.setAccesoNombre(rs.getString("acceso_nombre"));
                data.setAccesoLabel(rs.getString("acceso_label"));
                data.setTipoAcceso(rs.getInt("tp_acceso_id"));
                data.setOrdenDespliegue(rs.getInt("orden_despliegue"));
                data.setPerfilId(rs.getInt("perfil_id"));
                data.setPerfilNombre(rs.getString("perfil_nombre"));
                
                lista.put(""+data.getModuloId()+data.getAccesoId()+data.getPerfilId(), data);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("Error: "+sqle.toString());
        }
        
        return lista;
    }
    
    public LinkedHashMap<String,String> getSoloAccesosKeyByModuloPerfil(int _perfilUsuario,int _moduloId){
        
        LinkedHashMap<String,String> lista = 
            new LinkedHashMap<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            String sql = "SELECT ap.acceso_id "
                + " FROM modulo_acceso_perfil ap "
                + "	inner join acceso on ap.acceso_id = acceso.acceso_id "
                + "	inner join modulo_sistema modulo on ap.modulo_id = modulo.modulo_id "
                + "	inner join perfil_usuario perfil on ap.perfil_id = perfil.perfil_id "
                + "  where 1 = 1 ";
            
            if (_perfilUsuario != -1){
                sql+=" and ap.perfil_id = "+_perfilUsuario;
            }
            if (_moduloId != -1){
                sql+=" and ap.modulo_id = "+_moduloId;
            }
                
            sql+=" order by ap.orden_despliegue";

            System.out.println("[ModuloAccesoPerfilDAO.getSoloAccesosKeyByModuloPerfil]"
                + ", Sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[ModuloAccesoPerfilDAO.getSoloAccesosKeyByModuloPerfil]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();
            int intAccesoId=0;
            while (rs.next()){
                intAccesoId = rs.getInt("acceso_id");
                
                lista.put(""+intAccesoId, String.valueOf(intAccesoId));
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("Error: "+sqle.toString());
        }
        
        return lista;
    }
    
   
}
