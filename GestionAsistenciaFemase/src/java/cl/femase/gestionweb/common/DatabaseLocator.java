/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.common;

import java.sql.SQLException;
import java.sql.Connection;
import javax.sql.DataSource;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
*
* @author Alexander
*  
*/
public class DatabaseLocator {

    private static DatabaseLocator me;
    private InitialContext ctx;
    private Map dsNames;

    private DatabaseLocator() throws DatabaseException {
        try {
            ctx = new InitialContext();

            if (ctx == null) {
                System.out.println("Contexto nulo!");
                throw new DatabaseException(DatabaseException.ERROR_SIN_CONTEXTO, "Contexto nulo.");
            }
        } catch (NamingException e) {
            throw new DatabaseException(DatabaseException.ERROR_SIN_CONTEXTO, e.getMessage());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     * @throws cl.femase.gestionweb.common.DatabaseException
     */
    public static DatabaseLocator getInstance() throws DatabaseException {
        if (me == null) {
            me = new DatabaseLocator();
        }

        return me;
    }

    /**
    * DOCUMENT ME!
    *
    * @param dsName DOCUMENT ME!
    * @param _fromMethod
    *
    * @return DOCUMENT ME!
    * @throws cl.femase.gestionweb.common.DatabaseException
    */
    public Connection getConnection(String dsName, String _fromMethod) throws DatabaseException {
        Connection conn = null;
        System.out.println("[DatabaseLocator.getConnection]"
            + "Obteniendo conexion a la BD desde metodo: " + _fromMethod);
        try {
            DataSource ds = (DataSource) ctx.lookup("java:/" + dsName);
            
            if (ds != null) {
                conn = ds.getConnection();

                if (conn == null) {
                    throw new DatabaseException(DatabaseException.ERROR_CONEXION, "Conexi�n nula");
                }
            } else {
                throw new DatabaseException(DatabaseException.ERROR_DATASOURCE, "DataSource nulo.");
            }
        } catch (NamingException e) {
            System.err.println("Error1 al obtener conexion a la BD: " + e.toString());
            throw new DatabaseException(DatabaseException.ERROR_DATASOURCE, e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error2 al obtener conexion a la BD: " + e.toString());
            throw new DatabaseException(DatabaseException.ERROR_CONEXION, e.getMessage());
        } catch (Exception e) {
            System.err.println("Error3 al obtener conexion a la BD: " + e.toString());
            throw new DatabaseException(DatabaseException.ERROR_CONEXION, e.getMessage());
        }

        return conn;
    }

    
    /**
    * Closes the {@link Connection} to database.
    * @param _conn
    */
    public void freeConnection(Connection _conn){
        try{
            if(_conn != null && !_conn.isClosed()){
                _conn.close();
            }

        }catch(SQLException e){
            System.err.println("[DatabaseLocator.freeConnection]"
                + "Error al cerrar conexion a la BD: "+e.toString());
            e.printStackTrace();
        }
        finally{
            _conn = null;
        }
    }
    
//    
//    /**
//     * DOCUMENT ME!
//     *
//     * @param conn DOCUMENT ME!
//     */
//    public void freeConnection(Connection conn) {
//        try {
//            System.out.println("Liberando conexion a la BD...");
//            if (conn != null) {
//                conn.close();
//            }
//        } catch (SQLException e) {
//            //e.printStackTrace();
//        }
//    }
}
