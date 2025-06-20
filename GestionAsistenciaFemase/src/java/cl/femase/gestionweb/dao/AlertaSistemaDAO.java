/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.AlertaSistemaVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexander
 */
public class AlertaSistemaDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    
    /**
    *
    * @param _propsValues
    */
    public AlertaSistemaDAO(PropertiesVO _propsValues) {
    }

    /**
    * Retorna info de alerta sistema
    * 
    * @param _id
    * @return 
    */
    public AlertaSistemaVO getAlertaSistemaByKey(int _id){
        AlertaSistemaVO alerta = null;       
        // Consulta SQL con parámetro
        String query = "SELECT id_alerta, empresa_id, titulo, mensaje, "
                + "tipo, "
                + "fecha_hora_desde, "
                + "fecha_hora_hasta, estado, "
                + "prioridad, "
                + "url_accion, "
                + "icono, "
                + "creado_por, "
                + "modificado_por, "
                + "to_char(created_at, 'YYYY-MM-DD HH24:MI:SS') created_at, "
                + "to_char(updated_at, 'YYYY-MM-DD HH24:MI:SS') updated_at " 
            + "FROM alertas_sistema "
            + "where id_alerta = ?";

        try (Connection dbConn = dbLocator.getConnection(m_dbpoolName,"[AlertaSistemaDAO.getAlertaSistemaByKey]");
            PreparedStatement pstmt = dbConn.prepareStatement(query)) {

            // Establecer el valor del parámetro ID
            pstmt.setInt(1, _id);

            // Ejecutar la consulta
            ResultSet rs = pstmt.executeQuery();

            // Procesar los resultados
            if (rs.next()) {
                alerta = new AlertaSistemaVO();
                alerta.setIdAlerta(rs.getInt("id_alerta"));
                alerta.setEmpresaId(rs.getString("empresa_id"));
                alerta.setTitulo(rs.getString("titulo"));
                alerta.setMensaje(rs.getString("mensaje"));
                alerta.setTipo(rs.getString("tipo"));
                alerta.setFechaHoraDesde(rs.getString("fecha_hora_desde"));
                alerta.setFechaHoraHasta(rs.getString("fecha_hora_hasta"));
                alerta.setEstado(rs.getString("estado"));
                alerta.setPrioridad(rs.getString("prioridad"));
                alerta.setUrlAccion(rs.getString("url_accion"));
                alerta.setIcono(rs.getString("icono"));
                alerta.setCreadoPor(rs.getString("creado_por"));
                alerta.setModificadoPor(rs.getString("modificado_por"));
                alerta.setCreatedAt(rs.getString("created_at"));
                alerta.setUpdatedAt(rs.getString("updated_at"));
                
            }

        } catch (Exception e) {
            e.printStackTrace();
        } 
        return alerta;
    }
    
    /**
     * Actualiza un alerta Sistema
     * @param _data
     * @return 
     */
    public ResultCRUDVO update(AlertaSistemaVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "Alerta Sistema, "
            + "id: " + _data.getIdAlerta()
            + ", empresaId: " + _data.getEmpresaId()    
            + ", titulo: " + _data.getTitulo()
            + ", estado: " + _data.getEstado();
        
        try{
            String msgFinal = " Actualiza alerta Sistema:"
                + "id [" + _data.getIdAlerta()+ "]" 
                + ", empresaid [" + _data.getEmpresaId() + "]"    
                + ", titulo [" + _data.getTitulo() + "]"
                + ", estado [" + _data.getEstado() + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE alertas_sistema "
                + "SET "
                    + "empresa_id = ?, "
                    + "titulo = ?, "
                    + "mensaje = ?, "
                    + "tipo = ?, "
                    + "fecha_hora_desde = '" + _data.getFechaHoraDesde() + "', "
                    + "fecha_hora_hasta = '" + _data.getFechaHoraHasta() + "', "
                    + "estado = ?, "
                    + "prioridad = ?, "
                    + "url_accion = ?, "
                    + "icono = ?, "
                    + "creado_por = ?, "
                    + "modificado_por = ?, "
                    + "updated_at = now() "
                    + "WHERE id_alerta = ?";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[AlertaSistemaDAO.update]");
            psupdate = dbConn.prepareStatement(sql);
            
            psupdate.setString(1, _data.getEmpresaId());
            psupdate.setString(2,  _data.getTitulo());
            psupdate.setString(3,  _data.getMensaje());
            psupdate.setString(4,  _data.getTipo());
            psupdate.setString(5,  _data.getEstado());
            psupdate.setString(6,  _data.getPrioridad());
            psupdate.setString(7,  _data.getUrlAccion());
            psupdate.setString(8,  _data.getIcono());
            psupdate.setString(9,  _data.getCreadoPor());
            psupdate.setString(10,  _data.getModificadoPor());
            psupdate.setInt(11,  _data.getIdAlerta());
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println(WEB_NAME+"[update]alerta Sistema"
                    + ", id: " +_data.getIdAlerta()
                    + ", empresaId: " +_data.getEmpresaId()
                    + ", estado: " +_data.getEstado()    
                    + ", titulo: " +_data.getTitulo()
                    +" actualizada OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("update alerta Sistema Error: "+sqle.toString());
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
    * Agrega una nueva alerta de sistema
    * 
    * @param _data
    * @return 
    */
    public ResultCRUDVO insert(AlertaSistemaVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        int result=0;
        String msgError = "Error al insertar "
            + "alerta de sistema. "
            + " id: "+_data.getIdAlerta()
            + ", titulo: "+_data.getTitulo()
            + ", empresa: "+_data.getEmpresaId()
            + ", estado: "+_data.getEstado();
        
       String msgFinal = " Inserta alerta de sistema:"
            + "id [" + _data.getIdAlerta()+ "]"
            + "titulo [" + _data.getTitulo() + "]"
            + "empresa [" + _data.getEmpresaId() + "]"
            + "estado [" + _data.getEstado() + "]";
            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO alertas_sistema "
                + "(id_alerta, "
                    + "empresa_id, "
                    + "titulo, "
                    + "mensaje, "
                    + "tipo, "
                    + "fecha_hora_desde, "
                    + "fecha_hora_hasta, "
                    + "estado, "
                    + "creado_por, "
                    + "created_at) "
                    + " VALUES(nextval('alertas_sistema_id_alerta_seq'::regclass), "
                    + "?, ?, ?, ?, "
                    + "'" + _data.getFechaHoraDesde() + "', "
                    + "'" + _data.getFechaHoraHasta() + "', "
                    + "?, ?, "
                    + "now())";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[AlertaSistemaDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            
            insert.setString(1, _data.getEmpresaId());
            insert.setString(2,  _data.getTitulo());
            insert.setString(3,  _data.getMensaje());
            insert.setString(4,  _data.getTipo());
            
            insert.setString(5,  _data.getEstado());
            insert.setString(6,  _data.getCreadoPor());
                                    
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println(WEB_NAME+"[insert alerta Sistema]"
                    + ", titulo:" +_data.getTitulo()
                    + ", id:" +_data.getIdAlerta()
                    +" insertada OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("insert alerta Sistema Error1: "+sqle.toString());
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
    * Elimina una alerta de Sistema
    * 
    * @param _data
    * @return 
    */
    public ResultCRUDVO delete(AlertaSistemaVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        int result=0;
        String msgError = "Error al eliminar "
            + "una alerta de sistema, Id: " +_data.getIdAlerta()
            + ", titulo: " +_data.getTitulo();
        
       String msgFinal = " Elimina alerta de sistema:"
            + "Id [" + _data.getIdAlerta() + "]" 
            + ", titulo [" + _data.getTitulo() + "]";
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "DELETE FROM alertas_sistema "
                + "WHERE id_alerta = ?";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[AlertaSistemaDAO.delete]");
            insert = dbConn.prepareStatement(sql);
            insert.setInt(1,  _data.getIdAlerta());
                        
            int filasAfectadas = insert.executeUpdate();
            m_logger.debug("[delete alerta Sistema]filasAfectadas: "+filasAfectadas);
            if (filasAfectadas == 1){
                m_logger.debug("[delete alerta Sistema]"
                    + ", id:" +_data.getIdAlerta()
                    + ", titulo:" +_data.getTitulo()
                    +" eliminada OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("delete alerta Sistema Error: "+sqle.toString());
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
    * Retorna lista con las alertas de sistema para la empresa especificada
    * 
    * @param _empresaId
    * @param _titulo
    * @param _fecha
    * @return 
    */
    public List<AlertaSistemaVO> getAlertas(String _empresaId, String _titulo, String _fecha){
        List<AlertaSistemaVO> alertas = new ArrayList<>();
        AlertaSistemaVO alerta = null;       
        String query = "SELECT "
                + "id_alerta, empresa_id, titulo, mensaje, "
                + "tipo, fecha_hora_desde, fecha_hora_hasta, estado, "
                + "prioridad, url_accion, icono, creado_por, "
                + "COALESCE(modificado_por, '') modificado_por, "
                + "to_char(created_at, 'YYYY-MM-DD HH24:MI:SS') created_at, "
                + "to_char(updated_at, 'YYYY-MM-DD HH24:MI:SS') updated_at " 
            + "FROM alertas_sistema "
            + "where empresa_id = ? ";
        
        if (_titulo != null && _titulo.compareTo("") != 0){
            query += " and titulo like '" + _titulo + "%'";
        }
        if (_fecha != null && _fecha.compareTo("") != 0){
            query += " and fecha_hora_desde::date = '" + _fecha + "'";
        }        

        try (Connection dbConn = dbLocator.getConnection(m_dbpoolName,"[AlertaSistemaDAO.getAlertas]");
            PreparedStatement pstmt = dbConn.prepareStatement(query)) {

            // Establecer el valor del parámetro ID
            pstmt.setString(1, _empresaId);

            // Ejecutar la consulta
            ResultSet rs = pstmt.executeQuery();

            // Procesar los resultados
            while (rs.next()) {
                alerta = new AlertaSistemaVO();
                alerta.setIdAlerta(rs.getInt("id_alerta"));
                alerta.setEmpresaId(rs.getString("empresa_id"));
                alerta.setTitulo(rs.getString("titulo"));
                alerta.setMensaje(rs.getString("mensaje"));
                alerta.setTipo(rs.getString("tipo"));
                alerta.setFechaHoraDesde(rs.getString("fecha_hora_desde"));
                alerta.setFechaHoraHasta(rs.getString("fecha_hora_hasta"));
                alerta.setEstado(rs.getString("estado"));
                alerta.setPrioridad(rs.getString("prioridad"));
                alerta.setUrlAccion(rs.getString("url_accion"));
                alerta.setIcono(rs.getString("icono"));
                alerta.setCreadoPor(rs.getString("creado_por"));
                alerta.setModificadoPor(rs.getString("modificado_por"));
                alerta.setCreatedAt(rs.getString("created_at"));
                alerta.setUpdatedAt(rs.getString("updated_at"));
                
                alertas.add(alerta);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } 
        return alertas;
    }
    
    /**
    * Retorna lista con las alertas de sistema activas a la fecha-hora actual
    * 
    * @param _empresaId
    * @return 
    */
    public List<AlertaSistemaVO> getAlertasActivas(String _empresaId){
        List<AlertaSistemaVO> alertas = new ArrayList<>();
        AlertaSistemaVO alerta = null;       
        String query = "SELECT as2.id_alerta, as2.titulo, as2.mensaje,"
                + "to_char(as2.fecha_hora_desde, 'YYYY-MM-DD HH24:MI:SS') desde,"
                + "to_char(as2.fecha_hora_hasta, 'YYYY-MM-DD HH24:MI:SS') hasta "
            + "FROM alertas_sistema as2 "
            + "WHERE empresa_id = ? "
            + "and as2.fecha_hora_desde <= CURRENT_TIMESTAMP "
                + "AND (as2.fecha_hora_hasta IS NULL OR as2.fecha_hora_hasta >= CURRENT_TIMESTAMP) "
                + "order by as2.fecha_hora_desde";
            
        try (Connection dbConn = dbLocator.getConnection(m_dbpoolName,"[AlertaSistemaDAO.getAlertasActivas]");
            PreparedStatement pstmt = dbConn.prepareStatement(query)) {

            // Establecer el valor del parámetro ID
            pstmt.setString(1, _empresaId);

            // Ejecutar la consulta
            ResultSet rs = pstmt.executeQuery();

            // Procesar los resultados
            while (rs.next()) {
                alerta = new AlertaSistemaVO();
                alerta.setIdAlerta(rs.getInt("id_alerta"));
                alerta.setTitulo(rs.getString("titulo"));
                alerta.setMensaje(rs.getString("mensaje"));
                alerta.setFechaHoraDesde(rs.getString("desde"));
                alerta.setFechaHoraHasta(rs.getString("hasta"));
                
                alertas.add(alerta);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } 
        return alertas;
    }
    
}
