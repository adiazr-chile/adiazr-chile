/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.AusenciaVO;
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
public class AusenciaDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    //private Connection dbConn;
    
    /**
     *
     * @param _propsValues
     */
    public AusenciaDAO(PropertiesVO _propsValues) {
//        try {
//            dbLoc = DatabaseLocator.getInstance();
//            m_dbpoolName = _propsValues.getDbPoolName();
//        } catch (DatabaseException ex) {
//            m_logger.error("DbError: "+ex.toString());
//        }
    }

    /**
     * Actualiza una ausencia
     * @param _data
     * @return 
     */
    public MaintenanceVO update(AusenciaVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "ausencia, "
            + "id: " + _data.getId()
            + ", nombre: " + _data.getNombre()
            + ", nombreCorto: " + _data.getNombreCorto()
            + ", tipo_ausencia: " + _data.getTipoId()
            + ", estado: " + _data.getEstado()
            + ", justificaHoras: " + _data.getJustificaHoras()
            + ", pagadaPorEmpleador: " + _data.getPagadaPorEmpleador();
        
        try{
            String msgFinal = " Actualiza ausencia:"
                + "id [" + _data.getId() + "]" 
                + ", nombre [" + _data.getNombre() + "]"
                + ", nombreCorto [" + _data.getNombreCorto()+ "]"    
                + ", tipo_ausencia [" + _data.getTipoId() + "]"
                + ", estado [" + _data.getEstado() + "]"
                + ", justificaHoras [" + _data.getJustificaHoras() + "]"
                + ", pagadaPorEmpleador [" + _data.getPagadaPorEmpleador() + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE ausencia "
                    + "SET "
                    + "ausencia_nombre = ?, "
                    + "ausencia_tipo = ?, "
                    + "ausencia_estado = ?, "
                    + "justifica_horas = ?,"
                    + "pagada_por_empleador = ?, "
                    + "nombre_corto=? "
                    + "WHERE ausencia_id = ?";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[AusenciaDAO.update]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setString(1,  _data.getNombre());
            psupdate.setInt(2,  _data.getTipoId());
            psupdate.setInt(3,  _data.getEstado());
            psupdate.setString(4,  _data.getJustificaHoras());
            psupdate.setString(5,  _data.getPagadaPorEmpleador());
            psupdate.setString(6,  _data.getNombreCorto());
            psupdate.setInt(7,  _data.getId());
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println("[update]ausencia"
                    + ", id:" +_data.getId()
                    + ", nombre:" +_data.getNombre()
                    + ", nombreCorto:" +_data.getNombreCorto()
                    + ", tipo:" +_data.getTipoId()
                    + ", estado:" +_data.getEstado()
                    + ", justifica_horas:" +_data.getJustificaHoras()
                    + ", pagada_por_empleador:" +_data.getPagadaPorEmpleador()
                    +" actualizada OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("update ausencia Error: "+sqle.toString());
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
     * Agrega un nuevo acceso
     * @param _data
     * @return 
     */
    public MaintenanceVO insert(AusenciaVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al insertar "
            + "ausencia. "
            + " nombre: " + _data.getNombre()
            + ", nombreCorto: " + _data.getNombreCorto()
            + ", tipo: " + _data.getTipoId()
            + ", estado: " + _data.getEstado()
            + ", justifica_horas: " + _data.getJustificaHoras()
            + ", pagada_por_empleador: " + _data.getPagadaPorEmpleador();
        
       String msgFinal = " Inserta ausencia:"
            + "nombre [" + _data.getNombre() + "]"
            + ", nombreCorto [" + _data.getNombreCorto() + "]"   
            + ", tipo [" + _data.getTipoId()+ "]"
            + ", estado [" + _data.getEstado() + "]"
            + ", justificaHoras [" + _data.getJustificaHoras() + "]"
            + ", pagada_por_empleador [" + _data.getPagadaPorEmpleador() + "]";
            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO ausencia("
                + "ausencia_id, ausencia_nombre, "
                + "ausencia_tipo, ausencia_estado, "
                + "justifica_horas,pagada_por_empleador,nombre_corto) "
                + "VALUES (nextval('ausencia_id_seq'), ?, ?, ?, ?, ?, ?)";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[AusenciaDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getNombre());
            insert.setInt(2,  _data.getTipoId());
            insert.setInt(3,  _data.getEstado());
            insert.setString(4,  _data.getJustificaHoras());
            insert.setString(5,  _data.getPagadaPorEmpleador());
            insert.setString(6,  _data.getNombreCorto());
            
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println("[insert ausencia]"
                    + ", nombre:" +_data.getNombre()
                    + ", nombreCorto:" +_data.getNombreCorto()
                    + ", tipo:" +_data.getTipoId()
                    + ", estado:" +_data.getEstado()
                    +" insertado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("insert ausencia Error1: "+sqle.toString());
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
     * Retorna lista con las asusencias: vacaciones, licencias, permisos, etc
     * 
     * @param _nombre
     * @param _tipo
     * @param _estado
     * @param _justificaHoras
     * @param _pagadaPorEmpleador
     * @param _jtStartIndex
     * @param _jtPageSize
     * @param _jtSorting
     * @return 
     */
    public List<AusenciaVO> getAusencias(String _nombre,
            int _tipo,
            int _estado,
            String _justificaHoras,
            String _pagadaPorEmpleador,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<AusenciaVO> lista = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        AusenciaVO data;
        
        try{
            String sql = "SELECT "
                    + "ausencia.ausencia_id,"
                    + "ausencia.ausencia_nombre,"
                    + " ausencia.ausencia_tipo,"
                    + "tipo_ausencia.tp_ausencia_nombre,"
                    + "ausencia.ausencia_estado,"
                    + "estado.estado_nombre,"
                    + "coalesce(justifica_horas,'S') justifica_horas,"
                    + "pagada_por_empleador,"
                    + "coalesce(nombre_corto,'') nombre_corto "
                + "FROM "
                    + "ausencia,"
                    + "tipo_ausencia,"
                    + "estado "
                + "WHERE "
                + "ausencia.ausencia_estado = estado.estado_id AND "
                + "ausencia.ausencia_tipo = tipo_ausencia.tp_ausencia_id ";

            if (_nombre != null && _nombre.compareTo("")!=0){        
                sql += " and upper(ausencia_nombre) like '%"+_nombre.toUpperCase()+"%'";
            }
            if (_tipo != -1){        
                sql += " and ausencia_tipo = "+_tipo;
            }
            if (_estado != -1){        
                sql += " and ausencia_estado = "+_estado;
            }
            if (_justificaHoras.compareTo("-1") != 0){        
                sql += " and justifica_horas = '" + _justificaHoras + "' ";
            }
            if (_pagadaPorEmpleador.compareTo("-1") != 0){        
                sql += " and pagada_por_empleador = '" + _pagadaPorEmpleador + "' ";
            }
            
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[AusenciaDAO.getAusencias]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new AusenciaVO();
                data.setId(rs.getInt("ausencia_id"));
                data.setNombre(rs.getString("ausencia_nombre"));
                data.setNombreCorto(rs.getString("nombre_corto"));
                data.setTipoId(rs.getInt("ausencia_tipo"));
                data.setTipoNombre(rs.getString("tp_ausencia_nombre"));
                data.setEstado(rs.getInt("ausencia_estado"));
                data.setEstadoNombre(rs.getString("estado_nombre"));
                data.setJustificaHoras(rs.getString("justifica_horas"));
                data.setPagadaPorEmpleador(rs.getString("pagada_por_empleador"));
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
     * Retorna lista con las asusencias: vacaciones, licencias, permisos, etc
     * 
     * @return 
     */
    public HashMap<Integer, String> getAusencias(){
        
        HashMap<Integer, String> lista = 
            new HashMap<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            String sql = "SELECT "
                + "ausencia_id id,"
                + "ausencia_nombre nombre "
                + "FROM "
                + "ausencia order by ausencia_id";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[AusenciaDAO.getAusencias]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                lista.put(rs.getInt("id"), rs.getString("nombre"));
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
     * @param _nombre
     * @param _tipo
     * @param _estado
     * @param _justificaHoras
     * @param _pagadaPorEmpleador
     * @return 
     */
    public int getAusenciasCount(String _nombre,
            int _tipo,
            int _estado,
            String _justificaHoras,
            String _pagadaPorEmpleador){
        int count=0;
        Statement statement=null;
        ResultSet rs = null;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[AusenciaDAO.getAusenciasCount]");
            statement = dbConn.createStatement();
            String strSql="SELECT count(*) as count "
                + "FROM "
                + "ausencia,"
                + "tipo_ausencia,"
                + "estado "
                + "WHERE "
                + "ausencia.ausencia_estado = estado.estado_id "
                + "AND ausencia.ausencia_tipo = tipo_ausencia.tp_ausencia_id ";
            
            if (_nombre != null && _nombre.compareTo("")!=0){        
                strSql += " and upper(ausencia_nombre) like '%"+_nombre.toUpperCase()+"%'";
            }
            if (_tipo != -1){        
                strSql += " and ausencia_tipo = "+_tipo;
            }
            if (_estado != -1){        
                strSql += " and ausencia_estado = "+_estado;
            }
            if (_justificaHoras.compareTo("-1") != 0){        
                strSql += " and justifica_horas = '" + _justificaHoras + "' ";
            }
            if (_pagadaPorEmpleador.compareTo("-1") != 0){        
                strSql += " and pagada_por_empleador = '" + _pagadaPorEmpleador + "' ";
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
