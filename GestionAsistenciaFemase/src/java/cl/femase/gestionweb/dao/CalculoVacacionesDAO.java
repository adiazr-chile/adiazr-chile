
package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.FilasAfectadasJsonVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
* Clase encargada de implementar las llamadas a las funciones en base de datos Postgresql.
* Las funciones fueron creadas pot Femase, y tienen relacion con el calculo de vacaciones.
* Cada funcion setea columnas de la tabla 'vacaciones'.
* 
* 
* @author Alexander
*/
public class CalculoVacacionesDAO extends BaseDAO{

    /**
    * 
    * 
    * @param _empresaId 
    * @param _runEmpleado 
    * @param _fechaEmisionCertificadoAFP: Valor por defecto= null
    * @param _numCotizaciones: Valor por defecto = 0 
    * @param _diasEspeciales 
    * @param _diasAdicionales 
    * @param _afpCode: Valor por defecto = 'NINGUNA'
    * @return 
    */
    public ResultCRUDVO setFechaBaseVP(String _empresaId, 
            String _runEmpleado, 
            String _fechaEmisionCertificadoAFP, 
            int _numCotizaciones,
            String _diasEspeciales,
            double _diasAdicionales, 
            String _afpCode){
        ResultCRUDVO CRUDResult = new ResultCRUDVO();
        String strJson = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        int result = 0;
        String msgError = "Error calling the function '" + Constantes.fnSET_FECHA_BASE_VP + "'" 
            + ". Empresa_id: " + _empresaId
            + ", fechaEmisionCertificadoAFP: " + _fechaEmisionCertificadoAFP
            + ", numCotizaciones: " + _numCotizaciones
            + ", diasEspeciales: "  + _diasEspeciales
            + ", diasAdicionales: " + _diasAdicionales
             + ", AFP code: " + _afpCode;
        String msgFinal = "Calling the function '" + Constantes.fnSET_FECHA_BASE_VP + "'"
            + ". Empresa_id [" + _empresaId + "]"
            + ", fechaEmisionCertificadoAFP [" + _fechaEmisionCertificadoAFP + "]"
            + ", numCotizaciones [" + _numCotizaciones + "]"
            + ", diasEspeciales [" + _diasEspeciales + "]"
            + ", diasAdicionales [" + _diasAdicionales + "]"
            + ", AFP Code [" + _afpCode + "]";
        
        CRUDResult.setMsg(msgFinal);
        System.out.println("[CalculoVacacionesDAO.setFechaBaseVP]"
            + "Datos recibidos: " + msgFinal);
        
        /**
        * Ejemplo: 
        *   select set_fecha_base_vacacion_progresiva('9967722-8','emp01','2019-12-20',254,'N',0,'HABITAT');
        * 
        *   Si no se setean los parámetros de AFP, 
        *   y solo setea los parámetros de días especiales o días adicionales
        *   La función debe recibir: 
        *       select set_fecha_base_vacacion_progresiva('9967722-8','emp01',null,0 ,'S',2,'NINGUNA');
        * 
        */
        if (_fechaEmisionCertificadoAFP != null &&
            _fechaEmisionCertificadoAFP.compareTo("") == 0) {
                _fechaEmisionCertificadoAFP = "null";
        }else{
            _fechaEmisionCertificadoAFP = "'" + _fechaEmisionCertificadoAFP + "'";
        }
        String sqlFunctionInvoke = "select " + Constantes.fnSET_FECHA_BASE_VP + "('" + _runEmpleado + "',"
                + "'" + _empresaId + "',"
                + _fechaEmisionCertificadoAFP + ","
                + _numCotizaciones + ","
                + "'" + _diasEspeciales + "',"
                + _diasAdicionales + ","
                + "'" + _afpCode + "') "
            + "strjson";

        System.out.println("[CalculoVacacionesDAO.setFechaBaseVP]Sql: " + sqlFunctionInvoke);
        //CallableStatement callableStatement = null;
        try{
            dbConn = dbLocator.getConnection(m_dbpoolName, "[CalculoVacacionesDAO.setFechaBaseVP]");
            ps = dbConn.prepareStatement(sqlFunctionInvoke);
            rs = ps.executeQuery();

            while (rs.next()) {
                strJson += rs.getString("strjson");
            }

            FilasAfectadasJsonVO filasAfectadadaObj = (FilasAfectadasJsonVO)new Gson().fromJson(strJson, FilasAfectadasJsonVO.class);
            CRUDResult.setFilasAfectadasObj(filasAfectadadaObj);
            //System.out.println("Fila afectada Objeto: " + filasAfectadadaObj.toString());
            
//            callableStatement = dbConn.prepareCall(sqlFunctionInvoke);
//            callableStatement.execute();
//            // ... do something with the output ...
//            SQLWarning sqlWarning = callableStatement.getWarnings();
//            while (sqlWarning != null) {
//                fnWarningMessages.add(sqlWarning.getMessage());
//                sqlWarning = sqlWarning.getNextWarning();
//            }
//            CRUDResult.setWarningMessages(fnWarningMessages);
            
        }catch(SQLException sqle){
            System.err.println("[CalculoVacacionesDAO.setFechaBaseVP]"
                + "Error_1:" + sqle.toString());
            CRUDResult.setThereError(true);
            CRUDResult.setCodError(result);
            CRUDResult.setMsgError(msgError+" :"+sqle.toString());
        }catch(DatabaseException dbex){
            System.err.println("[CalculoVacacionesDAO.setFechaBaseVP]"
                + "Error_2:" + dbex.toString());
            CRUDResult.setThereError(true);
            CRUDResult.setCodError(result);
            CRUDResult.setMsgError(msgError+" :"+dbex.toString());
        }catch(Exception ex){
            System.err.println("[CalculoVacacionesDAO.setFechaBaseVP]"
                + "Error_3:" + ex.toString());
            ex.printStackTrace();
            CRUDResult.setThereError(true);
            CRUDResult.setCodError(result);
            CRUDResult.setMsgError(msgError+" :" + ex.toString());
        }
        finally{
            try {
                if (ps != null) {
                    ps.close();
                    rs.close();
                }
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[CalculoVacacionesDAO.setFechaBaseVP]"
                + "Error_3:" + ex.toString());
            }
        }

        return CRUDResult;
    }

    /**
    * 
    * Esta función realiza el cálculo de VBA para un empleado vigente, 
    * a partir de la fecha de inicio de contrato 
    * o de la nueva fecha de inicio de contrato 
    * si es que cuenta con continuidad laboral.
    * 
    * @param _empresaId 
    * @param _runEmpleado 
    * @return 
    */
    public ResultCRUDVO setVBA_Empleado(String _empresaId, 
            String _runEmpleado){
        
        ResultCRUDVO CRUDResult = new ResultCRUDVO();
        String strJson = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        int result = 0;
        String msgError = "Error calling the function '" + Constantes.fnSET_VBA_EMPLEADO + "'" 
            + ". Empresa_id: " + _empresaId
            + ", run empleado: " + _runEmpleado;
        String msgFinal = "Calling the function '" + Constantes.fnSET_VBA_EMPLEADO + "'"
            + ". Empresa_id [" + _empresaId + "]"
            + ", run empleado [" + _runEmpleado + "]";
                        
        CRUDResult.setMsg(msgFinal);
        
        /**
        *   Ejemplo: select set_saldo_vacaciones_basicas_anuales_empleado('9967722-8','emp01'); 
        */
        String sqlFunctionInvoke = "select " + Constantes.fnSET_VBA_EMPLEADO + "('" + _runEmpleado + "',"
                + "'" + _empresaId + "') "
            + "strjson";

        System.out.println("[CalculoVacacionesDAO.setVBA_Empleado]Sql: " + sqlFunctionInvoke);
        
        try{
            dbConn = dbLocator.getConnection(m_dbpoolName, "[CalculoVacacionesDAO.setVBA_Empleado]");
            ps = dbConn.prepareStatement(sqlFunctionInvoke);
            rs = ps.executeQuery();

            while (rs.next()) {
                strJson += rs.getString("strjson");
            }

            FilasAfectadasJsonVO filasAfectadadaObj = (FilasAfectadasJsonVO)new Gson().fromJson(strJson, FilasAfectadasJsonVO.class);
            CRUDResult.setFilasAfectadasObj(filasAfectadadaObj);
        }catch(SQLException sqle){
            System.err.println("[CalculoVacacionesDAO.setVBA_Empleado]"
                + "Error_1: " + sqle.toString());
            CRUDResult.setThereError(true);
            CRUDResult.setCodError(result);
            CRUDResult.setMsgError(msgError+" :"+sqle.toString());
        }catch(DatabaseException dbex){
            System.err.println("[CalculoVacacionesDAO.setVBA_Empleado]"
                + "Error_2: " + dbex.toString());
            CRUDResult.setThereError(true);
            CRUDResult.setCodError(result);
            CRUDResult.setMsgError(msgError+" :"+dbex.toString());
        }
        finally{
            try {
                if (ps != null) {
                    ps.close();
                    rs.close();
                }
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[CalculoVacacionesDAO.setVBA_Empleado]"
                + "Error_3:" + ex.toString());
            }
        }
        
        return CRUDResult;
    }
    
    /**
    * "Cálculo de vacaciones básicas anuales por CENCO."
    * 
    * Esta función realiza el cálculo de VBA para un CENTRO DE COSTO: 
    *   Obtiene a todos los empleados vigentes y 
    *   a partir de la fecha de inicio de contrato o 
    *   de la nueva fecha de inicio de contrato, 
    *   si es que cuenta con continuidad laboral.
    * 
    * @param _empresaId 
    * @param _cencoId 
    * @return 
    */
    public ResultCRUDVO setVBA_Cenco(String _empresaId, 
            int _cencoId){
        
        ResultCRUDVO CRUDResult = new ResultCRUDVO();
        String strJson = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        int result = 0;
        String msgError = "Error calling the function '" + Constantes.fnSET_VBA_CENCO + "'" 
            + ". Empresa_id: " + _empresaId
            + ", cenco_id: " + _cencoId;
        String msgFinal = "Calling the function '" + Constantes.fnSET_VBA_CENCO + "'"
            + ". Empresa_id [" + _empresaId + "]"
            + ", cenco_id [" + _cencoId + "]";
                        
        CRUDResult.setMsg(msgFinal);
        /**
        * Ejemplo: select set_saldo_vacaciones_basicas_anuales(70,'emp01'); 
        */
        String sqlFunctionInvoke = "select " + Constantes.fnSET_VBA_CENCO + "(" + _cencoId + ","
                + "'" + _empresaId + "') "
            + "strjson";

        System.out.println("[CalculoVacacionesDAO.setVBA_Cenco]Sql: " + sqlFunctionInvoke);
        try{
            dbConn = dbLocator.getConnection(m_dbpoolName, "[CalculoVacacionesDAO.setVBA_Cenco]");
            ps = dbConn.prepareStatement(sqlFunctionInvoke);
            rs = ps.executeQuery();

            while (rs.next()) {
                strJson += rs.getString("strjson");
            }

//            FilasAfectadasJsonVO filasAfectadadaObj = (FilasAfectadasJsonVO)new Gson().fromJson(strJson, FilasAfectadasJsonVO.class);
//            CRUDResult.setFilasAfectadasObj(filasAfectadadaObj);
            
            ArrayList<FilasAfectadasJsonVO> empleadosAfectados = new ArrayList<>();
            Type listType = new TypeToken<ArrayList<FilasAfectadasJsonVO>>() {}.getType();
            empleadosAfectados = new Gson().fromJson(strJson, listType);
            CRUDResult.setEmpleadosAfectados(empleadosAfectados);
            
        }catch(SQLException sqle){
            System.err.println("[CalculoVacacionesDAO.setVBA_Cenco]"
                + "Error_1: " + sqle.toString());
            CRUDResult.setThereError(true);
            CRUDResult.setCodError(result);
            CRUDResult.setMsgError(msgError+" :"+sqle.toString());
        }catch(DatabaseException dbex){
            System.err.println("[CalculoVacacionesDAO.setVBA_Cenco]"
                + "Error_2: " + dbex.toString());
            CRUDResult.setThereError(true);
            CRUDResult.setCodError(result);
            CRUDResult.setMsgError(msgError+" :"+dbex.toString());
        }finally{
            try {
                if (ps != null) {
                    ps.close();
                    rs.close();
                }
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[CalculoVacacionesDAO.setVBA_Cenco]"
                + "Error:" + ex.toString());
            }
        }
        
        return CRUDResult;
    }
    
    /**
    * Cálculo de vacaciones progresivas por empleado
    * 
    * Esta función realiza el cálculo de saldo de VP para un empleado, 
    *  basándose en la fecha base VP y con la ultima fecha de inicio de vacación.
    * 
    * @param _empresaId 
    * @param _runEmpleado 
    * @return 
    */
    public ResultCRUDVO setVP_Empleado(String _empresaId, 
            String _runEmpleado){
        
        ResultCRUDVO CRUDResult = new ResultCRUDVO();
        String strJson = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        int result = 0;
        String msgError = "Error calling the function '" + Constantes.fnSET_VP_EMPLEADO + "'" 
            + ". Empresa_id: " + _empresaId
            + ", RUN empleado: " + _runEmpleado;
        String msgFinal = "Calling the function '" + Constantes.fnSET_VP_EMPLEADO + "'"
            + ". Empresa_id [" + _empresaId + "]"
            + ", RUN empleado [" + _runEmpleado + "]";
        
        CRUDResult.setMsg(msgFinal);
        /**
        * Ejemplo: select set_saldo_vacaciones_progresivas_empleado('9967722-8','emp01');
        * 
        */
        String sqlFunctionInvoke = "select " + Constantes.fnSET_VP_EMPLEADO + "('" + _runEmpleado + "',"
                + "'" + _empresaId + "') "
            + "strjson";

        System.out.println("[CalculoVacacionesDAO.setVP_Empleado]Sql: " + sqlFunctionInvoke);
        
        try{
            dbConn = dbLocator.getConnection(m_dbpoolName, "[CalculoVacacionesDAO.setVP_Empleado]");
            ps = dbConn.prepareStatement(sqlFunctionInvoke);
            rs = ps.executeQuery();

            while (rs.next()) {
                strJson += rs.getString("strjson");
            }

            FilasAfectadasJsonVO filasAfectadadaObj = (FilasAfectadasJsonVO)new Gson().fromJson(strJson, FilasAfectadasJsonVO.class);
            CRUDResult.setFilasAfectadasObj(filasAfectadadaObj);
        }catch(SQLException sqle){
            System.err.println("[CalculoVacacionesDAO.setVP_Empleado]"
                + "Error_1: " + sqle.toString());
            CRUDResult.setThereError(true);
            CRUDResult.setCodError(result);
            CRUDResult.setMsgError(msgError+" :"+sqle.toString());
        }catch(DatabaseException dbex){
            System.err.println("[CalculoVacacionesDAO.setVP_Empleado]"
                + "Error_2: " + dbex.toString());
            CRUDResult.setThereError(true);
            CRUDResult.setCodError(result);
            CRUDResult.setMsgError(msgError+" :"+dbex.toString());
        }finally{
            try {
                if (ps != null) {
                    ps.close();
                    rs.close();
                }
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[CalculoVacacionesDAO.setVP_Empleado]"
                + "Error:" + ex.toString());
            }
        }
        return CRUDResult;
    }
    
    /**
    * "Cálculo de vacaciones progresivas por CENCO"
    * 
    * Esta función realiza el cálculo de saldo de VP para un CENTRO DE COSTO, 
    *   basándose en las fecha base VP y con la ultima fecha de inicio 
    *   de vacación de todos los empleados del CENCO.* 
    *
    * @param _empresaId 
    * @param _cencoId 
    * @return 
    */
    public ResultCRUDVO setVP_Cenco(String _empresaId, 
            int _cencoId){
        
        ResultCRUDVO CRUDResult = new ResultCRUDVO();
        String strJson = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        int result = 0;
        String msgError = "Error calling the function '" + Constantes.fnSET_VP_CENCO + "'" 
            + ". Empresa_id: " + _empresaId
            + ", cenco_id: " + _cencoId;
        String msgFinal = "Calling the function '" + Constantes.fnSET_VP_CENCO + "'"
            + ". Empresa_id [" + _empresaId + "]"
            + ", cenco_id [" + _cencoId + "]";
        
        CRUDResult.setMsg(msgFinal);
        
        /**
        * Ejemplo: select set_saldo_vacaciones_progresivas_empleado(70,'emp01'); 
        */
        String sqlFunctionInvoke = "select " + Constantes.fnSET_VP_CENCO + "(" + _cencoId + ","
                + "'" + _empresaId + "') "
            + "strjson";

        System.out.println("[SetVacacionesDAO.setVP_Cenco]Sql: " + sqlFunctionInvoke);
        
        try{
            dbConn = dbLocator.getConnection(m_dbpoolName, "[CalculoVacacionesDAO.setVP_Cenco]");
            ps = dbConn.prepareStatement(sqlFunctionInvoke);
            rs = ps.executeQuery();

            while (rs.next()) {
                strJson += rs.getString("strjson");
            }

//            FilasAfectadasJsonVO filasAfectadadaObj = (FilasAfectadasJsonVO)new Gson().fromJson(strJson, FilasAfectadasJsonVO.class);
//            CRUDResult.setFilasAfectadasObj(filasAfectadadaObj);
            ArrayList<FilasAfectadasJsonVO> empleadosAfectados = new ArrayList<>();
            Type listType = new TypeToken<ArrayList<FilasAfectadasJsonVO>>() {}.getType();
            empleadosAfectados = new Gson().fromJson(strJson, listType);
            CRUDResult.setEmpleadosAfectados(empleadosAfectados);
            
        }catch(SQLException sqle){
            System.err.println("[CalculoVacacionesDAO.setVP_Cenco]"
                + "Error_1: " + sqle.toString());
            CRUDResult.setThereError(true);
            CRUDResult.setCodError(result);
            CRUDResult.setMsgError(msgError+" :"+sqle.toString());
        }catch(DatabaseException dbex){
            System.err.println("[CalculoVacacionesDAO.setVP_Cenco]"
                + "Error_2: " + dbex.toString());
            CRUDResult.setThereError(true);
            CRUDResult.setCodError(result);
            CRUDResult.setMsgError(msgError+" :"+dbex.toString());
        }finally{
            try {
                if (ps != null) {
                    ps.close();
                    rs.close();
                }
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[SetVacacionesDAO.setVP_Cenco]"
                + "Error:" + ex.toString());
            }
        }
        return CRUDResult;
    }        
            
            
            
    
}
