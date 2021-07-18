/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package cl.femase.gestionweb.common;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

/**
*
* @author Alexander
*/
public class GetPropertyValues {
    String result = "";
	InputStream inputStream;
    Properties m_prop = new Properties();    
    
    public GetPropertyValues() {
        try {
            //Properties prop = new Properties();
            String propFileName = "gestionfemase.properties";
            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
            if (inputStream != null) {
                m_prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }
        } catch (Exception e) {
            System.err.println("[GetPropertyValues]Exception: " + e);
        } finally {
            try{
                inputStream.close();
            }catch(IOException ioex){
                System.err.println("[GetPropertyValues.Error al cargar archivo "
                    + "properties]Error: " + ioex.toString()); 
            }
        }
    }
 
    public String getKeyValue(String _key){
//        System.out.println("[GetPropertyValues.getKeyValue]"
//            + "key a buscar: " + _key);
        return m_prop.getProperty(_key);
    }
    
    public String getPropValues() throws IOException {
        try {
            Properties prop = new Properties();
            String propFileName = "gestionfemase.properties";
            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }
            Date time = new Date(System.currentTimeMillis());

            // get the property value and print it out
            String mailHost = prop.getProperty("mailHost");
            String mailPort = prop.getProperty("mailPort");
            String mailFrom = prop.getProperty("mailFrom");
            String dbpoolname = prop.getProperty("dbpoolname");

            System.out.println("mailHost: " + mailHost
                + ", mailPort: " + mailPort
                + ", mailFrom: " + mailFrom
                + ", dbpoolname: " + dbpoolname);
//            result = "Company List = " + company1 + ", " + company2 + ", " + company3;
//            System.out.println(result + "\nProgram Ran on " + time + " by user=" + user);
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            inputStream.close();
        }
        return result;
    }
}
