/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.common.DatabaseLocator;
import cl.femase.gestionweb.common.GetPropertyValues;
import static cl.femase.gestionweb.dao.UsersDAO.m_logger;
import java.sql.Connection;

/**
 *
 * @author Alexander
 */
public class BaseDAO{
    Connection dbConn;
    DatabaseLocator dbLocator;
    String m_dbpoolName;
    
    public BaseDAO() {
        try {
            dbLocator  = DatabaseLocator.getInstance();
            GetPropertyValues m_properties = new GetPropertyValues();
            m_dbpoolName = m_properties.getKeyValue("dbpoolname");
            //System.out.println("[BaseDAO]datasource a usar: "+m_dbpoolName);
        } catch (DatabaseException ex) {
            m_logger.error("DbError: "+ex.toString());
        }
    }
    
    
}
