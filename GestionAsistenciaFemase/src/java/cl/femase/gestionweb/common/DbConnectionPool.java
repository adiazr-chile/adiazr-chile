/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.common;

import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;

/**
 * @deprecated 
 * @author Alexander
 */
public class DbConnectionPool {
    public DataSource dataSource;
    private GetPropertyValues m_properties = new GetPropertyValues();

    //valores por defecto
    public String db = "femasegestion";
    public String url = "jdbc:postgresql://localhost:5432/"+db;
    public String user = "femase";
    public String pass = "gestion";

    public DbConnectionPool(){
        db = m_properties.getKeyValue("gestionfemase.databasename");
        url = "jdbc:postgresql://"
            + m_properties.getKeyValue("gestionfemase.hostname")
            + ":"
            + m_properties.getKeyValue("gestionfemase.port")
            +"/"
            +db;
        //System.out.println(WEB_NAME+"[DbConnectionPool]urldatabase: "+url);
        user = m_properties.getKeyValue("gestionfemase.user");
        pass = m_properties.getKeyValue("gestionfemase.password");
        inicializaDataSource();
    }
    
    private void inicializaDataSource(){
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName("org.postgresql.Driver");
        basicDataSource.setUsername(user);
        basicDataSource.setPassword(pass);
        basicDataSource.setUrl(url);
        basicDataSource.setMaxActive(50);
        basicDataSource.setPoolPreparedStatements(true);
                //prepareThreshold=0 into the JDBC connect URL.
        dataSource = basicDataSource;

    }
}
