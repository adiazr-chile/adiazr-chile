/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.ModuloSistemaVO;
import cl.femase.gestionweb.vo.PropertiesVO;
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
public class ModulosSistemaDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    //private Connection dbConn;
    //DbDirectConnection dbconnection = DbDirectConnection.getInstance();
    
    public ModulosSistemaDAO(PropertiesVO _propsValues) {
//        try {
//            dbLoc = DatabaseLocator.getInstance();
//            m_dbpoolName = _propsValues.getDbPoolName();
//        } catch (DatabaseException ex) {
//            m_logger.error("DbError: "+ex.toString());
//        }
    }

    /**
     * Actualiza un modulo de sistema
     * @param _data
     * @return 
     */
    public MaintenanceVO update(ModuloSistemaVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "modulo sistema, "
            + "id: "+_data.getEstado_id()
            + ", nombre: "+_data.getModulo_nombre()
            + ", estado: "+_data.getEstado_id()
            + ", orden despliegue: "+_data.getOrden_despliegue()
            + ", icon_id: "+_data.getIconId()
            + ", acceso_rapido: " + _data.getAccesoRapido()
            + ", titulo: " + _data.getTitulo()
            + ", sub_titulo: " + _data.getSubTitulo()
            + ", imagen: " + _data.getImagen();
        
        try{
            String msgFinal = " Actualiza modulo sistema:"
                + "id [" + _data.getEstado_id() + "]" 
                + ", nombre [" + _data.getModulo_nombre() + "]"
                + ", estado [" + _data.getEstado_id() + "]"
                + ", orden despliegue [" + _data.getOrden_despliegue() + "]"
                + ", icon_id [" + _data.getIconId() + "]"
                + ", acceso_rapido [" + _data.getAccesoRapido() + "]"
                + ", titulo [" + _data.getTitulo() + "]"
                + ", sub_titulo [" + _data.getSubTitulo() + "]"
                + ", imagen [" + _data.getImagen() + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE modulo_sistema "
                + "SET "
                    + "modulo_nombre = ?, "
                    + "estado_id = ?, "
                    + "orden_despliegue = ?, "
                    + "icon_id = ?,"
                    + "title = ?, sub_title = ?, "
                    + "image = ?, quick_access = ? "
                + " WHERE modulo_id = ?";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[ModuloSistemaDAO.update]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setString(1,  _data.getModulo_nombre());
            psupdate.setInt(2,  _data.getEstado_id());
            psupdate.setInt(3,  _data.getOrden_despliegue());
            psupdate.setString(4,  _data.getIconId());
            
            psupdate.setString(5,  _data.getTitulo());
            psupdate.setString(6,  _data.getSubTitulo());
            psupdate.setString(7,  _data.getImagen());
            psupdate.setString(8,  _data.getAccesoRapido());
            
            psupdate.setInt(9,  _data.getModulo_id());
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println(WEB_NAME+"[ModuloSistemaDAO.update]modulo sistema"
                    + ", id:" +_data.getEstado_id()
                    + ", nombre:" +_data.getModulo_nombre()
                    +" actualizado OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[ModuloSistemaDAO.update]Error: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }

        return objresultado;
    }

     /**
     * Agrega un nuevo modulo de sistema
     * @param _data
     * @return 
     */
    public MaintenanceVO insert(ModuloSistemaVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al insertar "
            + "modulo sistema. "
            + " nombre: " + _data.getModulo_nombre()
            + ", estado: " + _data.getEstado_id()
            + ", orden despliegue: " + _data.getOrden_despliegue()
            + ", acceso_rapido: " + _data.getAccesoRapido()
            + ", titulo: " + _data.getTitulo()
            + ", sub_titulo: " + _data.getSubTitulo()
            + ", imagen: " + _data.getImagen();    
        
       String msgFinal = " Inserta modulo sistema:"
            + "nombre [" + _data.getModulo_nombre() + "]"
            + ", estado [" + _data.getEstado_id()+ "]"
            + ", orden despliegue [" + _data.getOrden_despliegue() + "]"
            + ", icon_id [" + _data.getIconId() + "]"
            + ", acceso_rapido [" + _data.getAccesoRapido() + "]"
            + ", titulo [" + _data.getTitulo() + "]"
               + ", sub_titulo [" + _data.getSubTitulo() + "]"
               + ", imagen [" + _data.getImagen() + "]";
            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO modulo_sistema("
                    + " modulo_id, "
                    + "modulo_nombre, "
                    + "estado_id, "
                    + "orden_despliegue,"
                    + "icon_id, "
                    + "title, sub_title, image, quick_access) "
                + " VALUES (nextval('modulo_sistema_modulo_id_seq'), "
                    + "?, ?, ?, ?, ?, ?, ?, ?)";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[ModuloSistemaDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getModulo_nombre());
            insert.setInt(2,  _data.getEstado_id());
            insert.setInt(3,  _data.getOrden_despliegue());
            insert.setString(4,  _data.getIconId());
            
            insert.setString(5,  _data.getTitulo());
            insert.setString(6,  _data.getSubTitulo());
            insert.setString(7,  _data.getImagen());
            insert.setString(8,  _data.getAccesoRapido());
                        
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println(WEB_NAME+"[ModuloSistemaDAO.insert]"
                    + ", nombre:" +_data.getModulo_nombre()
                    + ", estado:" +_data.getEstado_id()
                    + ", orden despliegue:" +_data.getOrden_despliegue()
                    +" insertado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[ModuloSistemaDAO.insert]Error1: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }

        return objresultado;
    }
    
    /**
     * Elimina (desactiva) un modulo de sistema
     * @param _data
     * @return 
     */
//    public MaintenanceVO delete(ModuloSistemaVO _data){
//        MaintenanceVO objresultado = new MaintenanceVO();
//        int result=0;
//        String msgError = "Error al eliminar "
//                + "conversion de contratos, symbol: "+_data.getSymbol()
//                + ", base: "+_data.getBase();
//        
//       String msgFinal = " Elimina conversion de contratos:"
//                    + "symbol [" + _data.getSymbol() + "]" 
//                    + ", base [" + _data.getBase() + "]";
//        objresultado.setMsg(msgFinal);
//        PreparedStatement insert    = null;
//        
//        try{
//            String sql = "delete from "
//                + "contract_relation where symbol= ?";
//
//            dbConn = dbLocator.getConnection(m_dbpoolName);
//            insert = dbConn.prepareStatement(sql);
//            insert.setString(1,  _data.getSymbol());
//                        
//            int filasAfectadas = insert.executeUpdate();
//            m_logger.debug("[delete contract_relation]filasAfectadas: "+filasAfectadas);
//            if (filasAfectadas == 1){
//                m_logger.debug("[delete contract_relation]"
//                        + ", symbol:" +_data.getSymbol()
//                        + ", base:" +_data.getBase()
//                        +" eliminado OK!");
//            }
//            
//            insert.close();
//            dbLocator.freeConnection(dbConn);
//        }catch(SQLException|DatabaseException sqle){
//            m_logger.error("delete Error: "+sqle.toString());
//            objresultado.setThereError(true);
//            objresultado.setCodError(result);
//            objresultado.setMsgError(msgError+" :"+sqle.toString());
//        }catch(DatabaseException dbe){
//            m_logger.error("delete Error: "+dbe.toString());
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
     * Retorna lista con los modulos de sistema existentes
     * 
     * @param _nombre
     * @param _jtStartIndex
     * @param _jtPageSize
     * @param _jtSorting
     * @return 
     */
    public List<ModuloSistemaVO> getModulosSistema(String _nombre,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<ModuloSistemaVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        ModuloSistemaVO data;
        
        try{
            String sql = "SELECT "
                    + "modulo_sistema.modulo_id,modulo_sistema.modulo_nombre,"
                    + "modulo_sistema.estado_id,estado.estado_nombre,"
                    + "modulo_sistema.orden_despliegue,"
                    + "icon_id, "
                    + "title, sub_title,image,quick_access "
                + "FROM "
                + " modulo_sistema,"
                + "estado "
                + "WHERE "
                + " modulo_sistema.estado_id = estado.estado_id ";

            if (_nombre!=null && _nombre.compareTo("")!=0){        
                    sql += " and upper(modulo_nombre) like '"+_nombre.toUpperCase()+"%'";
            }
            
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            System.out.println(WEB_NAME+"[ModulosSistemaDAO.getModulosSistema]"
                + "nombre: "+_nombre
                + ", _jtStartIndex: "+_jtStartIndex
                + ", _jtPageSize: "+_jtPageSize
                + ", _jtSorting: "+_jtSorting
                + ", Sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[ModuloSistemaDAO.getModulosSistema]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new ModuloSistemaVO();
                data.setModulo_id(rs.getInt("modulo_id"));
                data.setModulo_nombre(rs.getString("modulo_nombre"));
                data.setEstado_id(rs.getInt("estado_id"));
                data.setOrden_despliegue(rs.getInt("orden_despliegue"));
                data.setIconId(rs.getString("icon_id"));
                
                //20200323
                data.setTitulo(rs.getString("title"));
                data.setSubTitulo(rs.getString("sub_title"));
                data.setImagen(rs.getString("image"));
                data.setAccesoRapido(rs.getString("quick_access"));
                
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
     * 
     * @param _nombre
     * @return 
     */
    public int getModulosSistemaCount(String _nombre){
        int count=0;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[ModuloSistemaDAO.getModulosSistemaCount]");
            Statement statement = dbConn.createStatement();
            String strSql="SELECT count(*) as count "
                    + "FROM modulo_sistema where 1=1 ";
            if (_nombre!=null && _nombre.compareTo("")!=0){        
                    strSql += " and upper(modulo_nombre) like '"+_nombre.toUpperCase()+"%'";
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
   
}
