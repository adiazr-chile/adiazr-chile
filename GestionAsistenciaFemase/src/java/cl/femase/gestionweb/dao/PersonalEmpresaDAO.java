/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.CentroCostoVO;
import cl.femase.gestionweb.vo.DepartamentoVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.EmpresaVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.PersonalEmpresaVO;
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
 * @Deprecated 
 */
public class PersonalEmpresaDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    //private Connection dbConn;
    //DbDirectConnection dbconnection = DbDirectConnection.getInstance();
    
    public PersonalEmpresaDAO(PropertiesVO _propsValues) {
//        try {
//            dbLoc = DatabaseLocator.getInstance();
//            m_dbpoolName = _propsValues.getDbPoolName();
//        } catch (DatabaseException ex) {
//            m_logger.error("DbError: "+ex.toString());
//        }
    }

     /**
     * Agrega un nuevo personal empresa
     * @param _data
     * @return 
     */
    public ResultCRUDVO insert(PersonalEmpresaVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        int result=0;
        String msgError = "Error al insertar "
            + "empleado en personal_empresa. "
            + " rutEmpleado: "+_data.getRutEmpleado()
            + ", empresaId: "+_data.getEmpresaId()
            + ", deptoId: "+_data.getDeptoId()
            + ", CencoId: "+_data.getCencoId()
            + ", fechaIngreso: "+_data.getFechaIngreso()
            + ", estado: "+_data.getEstado();
        
       String msgFinal = " Inserta empleado en personal_empresa:"
            + "rutEmpleado [" + _data.getRutEmpleado() + "]"
            + ", empresaId [" + _data.getEmpresaId()+ "]"
            + ", deptoId [" + _data.getDeptoId() + "]"
            + ", cencoId [" + _data.getCencoId() + "]"
            + ", fechaIngreso [" + _data.getFechaIngreso() + "]"
            + ", estado [" + _data.getEstado() + "]";
            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO personal_empresa("
                    + "empl_rut, empresa_id, "
                    + "depto_id, ccosto_id, "
                    + "fecha_ingreso, estado_id) "
                    + " VALUES (?, ?, ?, ?, ?, ?)";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[PersonalEmpresaDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getRutEmpleado());
            insert.setString(2,  _data.getEmpresaId());
            insert.setString(3,  _data.getDeptoId());
            insert.setInt(4,  _data.getCencoId());
            insert.setDate(5,  new java.sql.Date(_data.getFechaIngreso().getTime()));
            insert.setInt(6,  _data.getEstado());
                        
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println(WEB_NAME+"[insert personal_empresa]"
                    + " rutEmpleado: "+_data.getRutEmpleado()
                    + ", empresaId: "+_data.getEmpresaId()
                    + ", deptoId: "+_data.getDeptoId()
                    + ", CencoId: "+_data.getCencoId()
                    +" insertado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("insert personal_empresa Error1: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.getMessage());
        }

        return objresultado;
    }
    
    /**
     * Elimina registro de personal (cambiar estado 
     * y fecha ingreso relacion)
     * @param _data
     * @return 
     */
    public ResultCRUDVO delete(PersonalEmpresaVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        PreparedStatement psupdate = null;
        int result=0;
         String msgError = "Error al eliminar "
            + "empleado en personal_empresa. "
            + " rutEmpleado(pk): "+ _data.getRutEmpleado()
            + ", empresaId(pk): " + _data.getEmpresaId()
            + ", deptoId(pk): "   + _data.getDeptoId()
            + ", CencoId(pk): " + _data.getCencoId()
            + ", estado: " + _data.getEstado();
        
        try{
            String msgFinal = " Elimina empleado en personal_empresa:"
                + "rutEmpleado [" + _data.getRutEmpleado() + "]"
                + ", empresaId [" + _data.getEmpresaId()+ "]"
                + ", deptoId [" + _data.getDeptoId() + "]"
                + ", cencoId [" + _data.getCencoId() + "]"
                + ", estado [" + _data.getEstado() + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "delete from personal_empresa "
                 + "WHERE empl_rut=? "
                + "and empresa_id=? "
                + "and depto_id=? "
                + "and ccosto_id=?";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[PersonalEmpresaDAO.delete]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setDate(1,  new java.sql.Date(_data.getFechaIngreso().getTime()));
            psupdate.setInt(2,  _data.getEstado());
            psupdate.setString(3,  _data.getRutEmpleado());
            psupdate.setString(4,  _data.getEmpresaId());
            psupdate.setString(5,  _data.getDeptoId());
            psupdate.setInt(6,  _data.getCencoId());
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println(WEB_NAME+"[delete]personal_empresa"
                    + " rutEmpleado(pk): "+ _data.getRutEmpleado()
                    + ", empresaId(pk): " + _data.getEmpresaId()
                    + ", deptoId(pk): "   + _data.getDeptoId()
                    + ", CencoId(pk): " + _data.getCencoId()
                    + ", fechaIngreso: " + _data.getFechaIngreso()
                    + ", estado: " + _data.getEstado()
                    + " eliminado OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("delete personal_empresa Error: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }

        return objresultado;
    }    
    
    
    /**
     * Retorna lista con personal empresa
     * 
     * @param _empresaId
     * @param _deptoId
     * @param _jtStartIndex
     * @param _rutEmpleado
     * @param _nombresEmpleado
     * @param _cencostoId
     * @param _jtPageSize
     * @param _jtSorting
     * @return 
     */
    public List<PersonalEmpresaVO> getPersonal(String _empresaId,
            String _deptoId,
            int _cencostoId,
            String _rutEmpleado,
            String _nombresEmpleado,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<PersonalEmpresaVO> lista = 
                new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        PersonalEmpresaVO data;
        
        try{
            String sql = "SELECT "
                + "personal_empresa.empl_rut,"
                + "empleado.empl_nombres,"
                + "empleado.empl_ape_paterno,"
                + "empleado.empl_ape_materno,"
                + "empleado.empl_email,"
                + "personal_empresa.empresa_id,"
                + "empresa.empresa_nombre,"
                + "empresa.empresa_rut,"
                + "personal_empresa.depto_id,"
                + "departamento.depto_nombre,"
                + "personal_empresa.ccosto_id,"
                + "centro_costo.ccosto_nombre,"
                + "personal_empresa.fecha_ingreso,"
                + "personal_empresa.estado_id "
                + "FROM "
                + "personal_empresa,empresa,"
                + "empleado,departamento,centro_costo "
                + "WHERE "
                + "personal_empresa.empresa_id = empresa.empresa_id "
                + "AND personal_empresa.empl_rut = empleado.empl_rut "
                + "AND personal_empresa.depto_id = departamento.depto_id "
                + "AND personal_empresa.ccosto_id = centro_costo.ccosto_id ";

            if (_empresaId != null && _empresaId.compareTo("") != 0){        
                sql += " and empresa_id = '" + _empresaId + "'";
            }
            if (_deptoId != null && _deptoId.compareTo("") != 0){        
                sql += " and depto_id = '" + _deptoId + "'";
            }
            if (_cencostoId != -1){        
                sql += " and ccosto_id = '" + _cencostoId + "'";
            }
            if (_rutEmpleado!=null && _rutEmpleado.compareTo("")!=0){        
                sql += " and upper(empl_rut) like '"+_rutEmpleado.toUpperCase()+"%'";
            }
            if (_nombresEmpleado!=null && _nombresEmpleado.compareTo("")!=0){        
                sql += " and upper(empl_nombres) like '"+_nombresEmpleado.toUpperCase()+"%'";
            }
            
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[PersonalEmpresaDAO.getPersonal]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new PersonalEmpresaVO();
                
                EmpleadoVO auxempleado  = new EmpleadoVO();
                EmpresaVO auxempresa    = new EmpresaVO();
                DepartamentoVO auxdepto = new DepartamentoVO();
                CentroCostoVO auxcenco  = new CentroCostoVO();
                
                auxempleado.setRut(rs.getString("empl_rut"));
                auxempleado.setNombres(rs.getString("empl_nombres"));
                auxempleado.setApePaterno(rs.getString("empl_ape_paterno"));
                auxempleado.setApeMaterno(rs.getString("empl_ape_materno"));
                auxempleado.setEmail(rs.getString("empl_email"));
                
                auxempresa.setId(rs.getString("empresa_id"));
                auxempresa.setRut(rs.getString("empresa_rut"));
                auxempresa.setNombre(rs.getString("empresa_nombre"));
                
                auxdepto.setId(rs.getString("depto_id"));
                auxdepto.setNombre(rs.getString("depto_nombre"));
                
                auxcenco.setId(rs.getInt("ccosto_id"));
                auxcenco.setNombre(rs.getString("ccosto_nombre"));
                
                //seteo objetos complejos
                data.setRutEmpleado(auxempleado.getRut());
                data.setEmpleado(auxempleado);
                
                data.setEmpresaId(auxempresa.getId());
                data.setEmpresa(auxempresa);
                
                data.setDeptoId(auxdepto.getId());
                data.setDepartamento(auxdepto);
                
                data.setCencoId(auxcenco.getId());
                data.setCencosto(auxcenco);
                
                data.setFechaIngreso(rs.getDate("fecha_ingreso"));
                data.setEstado(rs.getInt("estado_id"));
                
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
     * @param _empresaId
     * @param _deptoId
     * @param _cencostoId
     * @param _rutEmpleado
     * @return 
     */
    public int getPersonalCount(String _empresaId,
            String _deptoId,
            int _cencostoId,
            String _rutEmpleado,
            String _nombresEmpleado){
        int count=0;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[PersonalEmpresaDAO.getPersonalCount]");
            Statement statement = dbConn.createStatement();
            String strSql="SELECT count(*) as count "
                + "FROM personal_empresa where 1=1 ";
            
            if (_empresaId != null && _empresaId.compareTo("") != 0){        
                strSql += " and empresa_id = '" + _empresaId + "'";
            }
            if (_deptoId != null && _deptoId.compareTo("") != 0){        
                strSql += " and depto_id = '" + _deptoId + "'";
            }
            if (_cencostoId != -1){        
                strSql += " and ccosto_id = '" + _cencostoId + "'";
            }
            if (_rutEmpleado!=null && _rutEmpleado.compareTo("")!=0){        
                strSql += " and upper(empl_rut) like '"+_rutEmpleado.toUpperCase()+"%'";
            }
            if (_nombresEmpleado!=null && _nombresEmpleado.compareTo("")!=0){        
                strSql += " and upper(empl_nombres) like '"+_nombresEmpleado.toUpperCase()+"%'";
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
