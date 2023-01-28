/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.NotificacionVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexander
 */
public class NotificacionDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    
    public NotificacionDAO(PropertiesVO _propsValues) {
    }

    /**
    * Agrega una nueva notificacion
    * 
    * @param _data
    * @return 
    */
    public ResultCRUDVO insert(NotificacionVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        int result=0;
        String msgError = "Error al insertar "
            + "notificacion. "
            + " usuario: " + _data.getUsername()    
            + ", mailFrom: " + _data.getMailFrom()
            + ", mailSubject: " + _data.getMailSubject()
            + ", empresaId: " + _data.getEmpresaId()
            + ", cencoId: " + _data.getCencoId()
            + ", rutEmpleado: " + _data.getRutEmpleado()
            + ", comentario: " + _data.getComentario()
            + ", latitud: " + _data.getLatitud()
            + ", longitud: " + _data.getLongitud();
        
       String msgFinal = " Inserta notificacion:"
            + " usuario [" + _data.getUsername() + "]"   
            + ", mailFrom [" + _data.getMailFrom() + "]"
            + ", subject [" + _data.getMailSubject() + "]"
            + ", empresaId [" + _data.getEmpresaId() + "]"
            + ", cencoId [" + _data.getCencoId() + "]"
            + ", rutEmpleado [" + _data.getRutEmpleado() + "]"
               + ", comentario [" + _data.getComentario() + "]"
               + ", latitud [" + _data.getLatitud() + "]"
               + ", longitud [" + _data.getLongitud() + "]";
            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO notificacion("
                        + "correlativo, "
                        + "fecha_ingreso, "
                        + "mail_from,"
                        + "mail_to, "
                        + "mail_subject, "
                        + "mail_body, "
                        + "empresa_id, "
                        + "cenco_id, "
                        + "rut_empleado, "
                        + "username, "
                        + "marca_virtual, registrar_ubicacion, comentario, latitud, longitud) "
                + " VALUES ("
                    + "nextval('notificacion_corr_seq'), "
                    + "current_timestamp, "//fecha_ingreso
                    + "?,"//1- mail from
                    + "?,"//2- mail_to
                    + "?,"//3- mail subject
                    + "?,"//4- mail body
                    + "?,"//5- empresa_id
                    + "?,"//6- cenco_id
                    + "?,"//7- rut_empleado
                    + "?, "//8- username
                    + "?, "//9- marca_virtual
                    + "?, "//10-registrar_ubicacion
                    + "?, "//11-comentario
                    + "?, "//12-latitud
                    + "?)";//13-longitud
                    
            dbConn = dbLocator.getConnection(m_dbpoolName,"[NotificacionDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getMailFrom());
            insert.setString(2,  _data.getMailTo());
            insert.setString(3,  _data.getMailSubject());
            insert.setString(4,  _data.getMailBody());
            insert.setString(5,  _data.getEmpresaId());
            insert.setInt(6,  _data.getCencoId());
            insert.setString(7,  _data.getRutEmpleado());
            insert.setString(8,  _data.getUsername());
            
            //30-04-2020
            insert.setString(9,  _data.getMarcaVirtual());
            insert.setString(10, _data.getRegistraUbicacion());
            insert.setString(11, _data.getComentario());
            insert.setString(12, _data.getLatitud());
            insert.setString(13, _data.getLongitud());
            
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println(WEB_NAME+"[insert notificacion]"
                    + "Username:" +_data.getUsername()    
                    + ", empresaId:" +_data.getEmpresaId()
                    + ", cencoId:" +_data.getCencoId()
                    + ", rutEmpleado:" + _data.getRutEmpleado()
                    + ", asunto:" + _data.getMailSubject()
                    + ", mailTo:" + _data.getMailTo()    
                    +" insertada OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("insert notificacion Error1: "+sqle.toString());
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
