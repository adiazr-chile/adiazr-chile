/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.vo.CentroCostoVO;
import cl.femase.gestionweb.vo.DepartamentoVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.EmpresaVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexander
 */
public class EmpleadosDAO extends BaseDAO{

    /**
     *
     */
    protected static Logger m_logger = Logger.getLogger("gestionweb");
    //private Connection dbConn;
    //DbDirectConnection dbconnection = DbDirectConnection.getInstance();
    
    private String SQL_INSERT_EMPLEADO = "INSERT INTO empleado("
        + " empl_rut, "
        + "empl_nombres, "
        + "empl_ape_paterno, "
        + "empl_ape_materno, "
        + "empl_fecha_nacimiento,"
        + "empl_direccion, "
        + "empl_email, "
        + "empl_fec_ini_contrato, "
        + "empl_estado,"
        + "empl_path_foto, "
        + "empl_sexo, "
        + "empl_fono_fijo, "
        + "empl_fono_movil, "
        + "id_comuna,"
        + "empl_fecha_creacion,"
        + "empl_id_turno,"
        + "autoriza_ausencia,"
        + "empl_id_cargo,"
        + "empl_fec_fin_contrato,"
        + "contrato_indefinido,"
        + "art_22,"
        + "empresa_id,"
        + "depto_id,"
        + "cenco_id,"
        + "cod_interno,"
        + "clave_marcacion) "
        + " VALUES (upper(?), ?, ?, ?, ?,"
            + "?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, current_date,?,?,?,?,?,?,?,?,?,?,?)";
 
    public EmpleadosDAO(PropertiesVO _propsValues) {
//        try {
//            dbLoc = DatabaseLocator.getInstance();
//            m_dbpoolName = _propsValues.getDbPoolName();
//        } catch (DatabaseException ex) {
//            m_logger.error("DbError: "+ex.toString());
//        }
    }

    /**
     * Actualiza un empleado
     * @param _data
     * @return 
     */
    public MaintenanceVO update(EmpleadoVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        PreparedStatement psupdate = null;
        int result=0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String msgError = "Error al actualizar "
            + "empleado, "
            + "rut: "+_data.getRut()
            + ", nombres: "+_data.getNombres()
            + ", apPaterno: "+_data.getApePaterno()
            + ", apMaterno: "+_data.getApeMaterno()    
            + ", email: "+_data.getEmail()
            + ", idTurno: "+_data.getIdTurno()
            + ", idCargo: "+_data.getIdCargo()
            + ", autorizaAusencia: "+_data.isAutorizaAusencia()
            + ", cambiarFoto: "+_data.isCambiarFoto()
            + ", empresaId: "+_data.getEmpresa().getId()    
            + ", deptoId: "+_data.getDepartamento().getId()
            + ", cencoId: "+_data.getCentroCosto().getId();
        
        try{
            String ffin = "";
            if (_data.getFechaTerminoContrato() != null){
                ffin = sdf.format(_data.getFechaTerminoContrato());
            }
            String msgFinal = " Actualiza empleado:"
                + "rut(PK)[" + _data.getRut() + "]" 
                + ", numFicha(cod_interno) [" + _data.getCodInterno() + "]"    
                + ", nombres [" + _data.getNombres() + "]"
                + ", apePaterno [" + _data.getApePaterno() + "]"
                + ", apeMaterno [" + _data.getApeMaterno() + "]"
                + ", email [" + _data.getEmail() + "]"
                + ", idTurno [" + _data.getIdTurno() + "]"
                + ", idCargo [" + _data.getIdCargo() + "]"    
                + ", foto [" + _data.getPathFoto() + "]"        
                + ", autorizaAusencia [" + _data.isAutorizaAusencia() + "]"
                + ", cambiarFoto [" + _data.isCambiarFoto() + "]"
                + ", contratoIndefinido [" + _data.isContratoIndefinido() + "]"
                + ", empresaId [" + _data.getEmpresa().getId() + "]"
                + ", deptoId [" + _data.getDepartamento().getId() + "]"
                + ", cencoId [" + _data.getCentroCosto().getId() + "]"
                + ", estado [" + _data.getEstado() + "]"
                + ", fechaDesvinculacion [" + _data.getFechaDesvinculacion() + "]"    
                + ", fechaInicioContrato [" + sdf.format(_data.getFechaInicioContrato()) + "]"
                + ", fechaFinContrato [" + ffin + "]"
                + ", idComuna [" + _data.getComunaId() + "]"
                + ", articulo22? [" + _data.isArticulo22() + "]";    
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            //fecha termino contrato por defecto: 31-12-3000
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, 3000);
            cal.set(Calendar.MONTH, Calendar.DECEMBER);
            cal.set(Calendar.DAY_OF_MONTH, 31);
            Date dateRepresentation = cal.getTime();
            
            String sql = "UPDATE empleado "
                + " SET empl_nombres = ?, "
                + "empl_ape_paterno=?, "
                + "empl_ape_materno = ?,"
                + "empl_fecha_nacimiento = ?, "
                + "empl_direccion = ?, "
                + "empl_email = ?, "
                + "empl_fec_ini_contrato = ?,"
                + "empl_estado = ?,";//8
            if (_data.isCambiarFoto()){
                sql += "empl_path_foto=?, ";
            }
            sql+= "empl_sexo = ?, "
                + "empl_fono_fijo = ?,"
                + "empl_fono_movil = ?, "
                + "id_comuna = ?,"
                + "empl_fecha_actualizacion = current_date,"
                + "empl_id_turno = ?,"
                + "autoriza_ausencia = ?, "
                + "empl_id_cargo = ?,"
                + "empl_fec_fin_contrato = ?,"
                + "contrato_indefinido = ?,"
                + "art_22 = ?,"
                + "cod_interno = ?,"
                + "clave_marcacion = ? ";
            
            //if (_data.getEstado() == Constantes.ESTADO_NO_VIGENTE){
                sql += ", fecha_desvinculacion = ? ";
            //}
            //if (_data.isModificarEmpresaDeptoCenco()){
                sql += ",empresa_id = ?, "
                     + "depto_id = ?, "
                     + "cenco_id = ? ";
            //}
            sql+= " WHERE empl_rut = ?";

            System.out.println("[updateEmpleado]Sql= " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[EmpleadosDAO.update]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setString(1,  _data.getNombres());
            psupdate.setString(2,  _data.getApePaterno());
            psupdate.setString(3,  _data.getApeMaterno());
            psupdate.setDate(4,  new java.sql.Date(_data.getFechaNacimiento().getTime()));
            psupdate.setString(5,  _data.getDireccion());
            psupdate.setString(6,  _data.getEmail());
            psupdate.setDate(7,  new java.sql.Date(_data.getFechaInicioContrato().getTime()));
            psupdate.setInt(8,  _data.getEstado());
            if (_data.isCambiarFoto()){
                System.out.println("[updateEmpleado]cambiar foto es true");
                psupdate.setString(9,  _data.getPathFoto());
                psupdate.setString(10,  _data.getSexo());
                psupdate.setString(11,  _data.getFonoFijo());
                psupdate.setString(12,  _data.getFonoMovil());//ok
                psupdate.setInt(13,  _data.getComunaId());
                psupdate.setInt(14,  _data.getIdTurno());
                psupdate.setBoolean(15,  _data.isAutorizaAusencia());
                psupdate.setInt(16,  _data.getIdCargo());//ok
                //agregados el 24-04-2017.
                //psupdate.setDate(17,  new java.sql.Date(_data.getFechaTerminoContrato().getTime()));
                if (_data.getFechaTerminoContrato() != null){
                    psupdate.setDate(17,  new java.sql.Date(_data.getFechaTerminoContrato().getTime()));
                }else{
                    psupdate.setDate(17,  new java.sql.Date(dateRepresentation.getTime()));
                }
                                
                psupdate.setBoolean(18,  _data.isContratoIndefinido());
                psupdate.setBoolean(19,  _data.isArticulo22());//ok
                psupdate.setString(20,  _data.getRut());//con puntos y guion
                psupdate.setString(21,  _data.getClaveMarcacion());
                
                if (_data.getEstado() == Constantes.ESTADO_NO_VIGENTE){
                    psupdate.setDate(22,  new java.sql.Date(_data.getFechaDesvinculacion().getTime()));
                }else{
                    psupdate.setDate(22,  null);
                }
                psupdate.setString(23,  _data.getEmpresa().getId());
                psupdate.setString(24,  _data.getDepartamento().getId());
                psupdate.setInt(25,  _data.getCentroCosto().getId());
                psupdate.setString(26,  _data.getCodInterno());
                
            }else{
                System.out.println("[update empleado]no actualiza foto...");
                psupdate.setString(9,  _data.getSexo());
                psupdate.setString(10,  _data.getFonoFijo());
                psupdate.setString(11,  _data.getFonoMovil());
                psupdate.setInt(12,  _data.getComunaId());
                psupdate.setInt(13,  _data.getIdTurno());
                psupdate.setBoolean(14,  _data.isAutorizaAusencia());
                psupdate.setInt(15,  _data.getIdCargo());
                //agregados el 24-04-2017.
                if (_data.getFechaTerminoContrato() != null){
                    System.out.println("[update empleado]"
                        + "Actualiza fecha fin contrato a: "
                        +_data.getFechaTerminoContrato());
                    psupdate.setDate(16,  new java.sql.Date(_data.getFechaTerminoContrato().getTime()));
                }else{
                    psupdate.setDate(16,  new java.sql.Date(dateRepresentation.getTime()));
                }
                System.out.println("[update empleado]"
                    + "Actualiza Tiene contrato indefinido "
                    + "a valor boolean="+_data.isContratoIndefinido());
                psupdate.setBoolean(17,  _data.isContratoIndefinido());
                psupdate.setBoolean(18,  _data.isArticulo22());
                //agregado el 19-08-2017, para guardar num ficha
                psupdate.setString(19,  _data.getRut());//con puntos y guion
                psupdate.setString(20,  _data.getClaveMarcacion());
                
                //if (_data.isModificarEmpresaDeptoCenco()){
                if (_data.getEstado() == Constantes.ESTADO_NO_VIGENTE){
                    psupdate.setDate(21,  new java.sql.Date(_data.getFechaDesvinculacion().getTime()));
                }else psupdate.setDate(21,  null);
                psupdate.setString(22,  _data.getEmpresa().getId());
                psupdate.setString(23,  _data.getDepartamento().getId());
                psupdate.setInt(24,  _data.getCentroCosto().getId());
                psupdate.setString(25,  _data.getCodInterno());
                    
                //}else{
                //    psupdate.setString(21,  _data.getCodInterno());
                //}
            }
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println("[update]empleado"
                    + ", rut (PK, puntos y guion):" +_data.getRut()
                    + ", codInterno:" +_data.getCodInterno()    
                    + ", nombres:" +_data.getNombres()
                    + ", email:" +_data.getEmail()
                    + ", empresaId: "+_data.getEmpresa().getId()
                    + ", deptoId: "+_data.getDepartamento().getId()
                    + ", cencoId: "+_data.getCentroCosto().getId()
                    + ", tieneContratoIndef? "+_data.isContratoIndefinido()
                    + ", fechaFinContrato: "+_data.getFechaTerminoContratoAsStr()    
                    +"  - actualizado OK!");
            }else{
                System.out.println("[update]empleado. No se actualizo empleado!!");
            }
            
            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("update empleado Error: "+sqle.toString());
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
    * Actualiza un empleado caducado o proximo a caducar
    * @param _data
    * @return 
    */
    public MaintenanceVO updateEmpleadoCaducado(EmpleadoVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "Empleado con contrato caducado, "
            + "rut: " + _data.getRut()
            + ", codInterno: " + _data.getCodInterno()
            + ", estado: " + _data.getEstado()
            + ", contrato indefinido?: " + _data.isContratoIndefinido()
            + ", fecha fin contrato: " + _data.getFechaTerminoContratoAsStr();
        
        try{
            String msgFinal = " Actualiza Empleado con contrato caducado:"
                + "rut [" + _data.getRut() + "]" 
                + ", codInterno [" + _data.getCodInterno() + "]"
                + ", estado [" + _data.getEstado() + "]"
                + ", contrato indefinido [" + _data.isContratoIndefinido() + "]"
                + ", fecha fin contrato [" + _data.getFechaTerminoContratoAsStr() + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE empleado "
                + " SET "
                + "empl_estado = ?, "
                + "empl_fec_fin_contrato = ?, "
                + "contrato_indefinido = ? "
                + " WHERE empl_rut = ?";

            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[CargoDAO.updateEmpleadoCaducado]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setInt(1,  _data.getEstado());
            psupdate.setDate(2,  Utilidades.getJavaSqlDate(_data.getFechaTerminoContratoAsStr(),"yyyy-MM-dd"));
            psupdate.setBoolean(3,  _data.isContratoIndefinido());
            psupdate.setString(4,  _data.getRut());
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println("[CargoDAO.updateEmpleadoCaducado]"
                    + "rut: " + _data.getRut()
                    + ", codInterno: " + _data.getCodInterno()
                    + ", estado: " + _data.getEstado()
                    + ", contrato indefinido?: " + _data.isContratoIndefinido()
                    + ", fecha fin contrato: " + _data.getFechaTerminoContratoAsStr()
                    + " actualizado OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[CargoDAO.updateEmpleadoCaducado]"
                + "Error: " + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (psupdate != null) psupdate.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[CargoDAO.updateEmpleadoCaducado]"
                    + "Error: " + ex.toString());
            }
        }

        return objresultado;
    }

    /**
    * Agrega un nuevo empleado
    * @param _data
    * @return 
    */
    public MaintenanceVO insert(EmpleadoVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String msgError = "Error al insertar "
            + "empleado, "
            + "rut: "+_data.getRut()
            + ", numFicha(cod_interno): "+_data.getCodInterno()    
            + ", nombres: "+_data.getNombres()
            + ", apPaterno: "+_data.getApePaterno()
            + ", apMaterno: "+_data.getApeMaterno()    
            + ", email: "+_data.getEmail()
            + ", autorizaAusencia: "+_data.isAutorizaAusencia()
            + ", idTurno: "+_data.getIdTurno()
            + ", idCargo: "+_data.getIdCargo()
            + ", empresaId: "+_data.getEmpresa().getId()    
            + ", deptoId: "+_data.getDepartamento().getId()
            + ", cencoId: "+_data.getCentroCosto().getId();
        
        String ffin = "";
        if (_data.getFechaTerminoContrato() != null){
            ffin = sdf.format(_data.getFechaTerminoContrato());
        }
        
        String msgFinal = " Inserta empleado:"
            + "rut [" + _data.getRut() + "]" 
            + ", numFicha(cod_interno) [" + _data.getCodInterno() + "]"    
            + ", nombres [" + _data.getNombres() + "]"
            + ", apePaterno [" + _data.getApePaterno() + "]"
            + ", apeMaterno [" + _data.getApeMaterno() + "]"
            + ", email [" + _data.getEmail() + "]"
            + ", idTurno [" + _data.getIdTurno() + "]"
            + ", autorizaAusencia [" + _data.isAutorizaAusencia() + "]"
            + ", idCargo [" + _data.getIdCargo() + "]"
            + ", empresaId [" + _data.getEmpresa().getId() + "]"    
            + ", deptoId [" + _data.getDepartamento().getId() + "]"
            + ", cencoId [" + _data.getCentroCosto().getId() + "]"
            + ", estado [" + _data.getEstado() + "]"
            + ", fechaInicioContrato [" + sdf.format(_data.getFechaInicioContrato()) + "]"
            + ", fechaFinContrato [" + ffin + "]"
            + ", idComuna [" + _data.getComunaId() + "]"
            + ", articulo22? [" + _data.isArticulo22() + "]";
        
        String codInterno = _data.getCodInterno();
        if (_data.getCodInternoCaracterAdicional() != null 
            && _data.getCodInternoCaracterAdicional().compareTo("") != 0){
                codInterno = codInterno + _data.getCodInternoCaracterAdicional();
        }
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO empleado("
                + " empl_rut, "
                + "empl_nombres, "
                + "empl_ape_paterno, "
                + "empl_ape_materno, "
                + "empl_fecha_nacimiento,"
                + "empl_direccion, "
                + "empl_email, "
                + "empl_fec_ini_contrato, "
                + "empl_estado,"
                + "empl_path_foto, "
                + "empl_sexo, "
                + "empl_fono_fijo, "
                + "empl_fono_movil, "
                + "id_comuna,"
                + "empl_fecha_creacion,"
                + "empl_id_turno,"
                + "autoriza_ausencia,"
                + "empl_id_cargo,"
                + "empl_fec_fin_contrato,"
                + "contrato_indefinido,"
                + "art_22,"
                + "empresa_id,"
                + "depto_id,"
                + "cenco_id,"
                + "cod_interno,"
                + "clave_marcacion) "
                + " VALUES (upper(?), ?, ?, ?, ?,"
                    + "?, ?, ?, ?, ?, "
                    + "?, ?, ?, ?, current_date,?,?,?,?,?,?,?,?,?,?,?)";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[EmpleadosDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  codInterno.toUpperCase());
            insert.setString(2,  _data.getNombres());
            insert.setString(3,  _data.getApePaterno());
            insert.setString(4,  _data.getApeMaterno());
            insert.setDate(5,  new java.sql.Date(_data.getFechaNacimiento().getTime()));
            insert.setString(6,  _data.getDireccion());
            insert.setString(7,  _data.getEmail());
            insert.setDate(8,  new java.sql.Date(_data.getFechaInicioContrato().getTime()));
            insert.setInt(9,  _data.getEstado());
            insert.setString(10,  _data.getPathFoto());
            insert.setString(11,  _data.getSexo());
            insert.setString(12,  _data.getFonoFijo());
            insert.setString(13,  _data.getFonoMovil());
            insert.setInt(14,  _data.getComunaId());
            insert.setInt(15,  _data.getIdTurno());
            insert.setBoolean(16,  _data.isAutorizaAusencia());
            insert.setInt(17,  _data.getIdCargo());
            //agregados el 24-04-2017.
            if (_data.getFechaTerminoContrato() != null){
                insert.setDate(18,  new java.sql.Date(_data.getFechaTerminoContrato().getTime()));
            }else{
                insert.setDate(18,  null);
            }
            insert.setBoolean(19,  _data.isContratoIndefinido());
            insert.setBoolean(20,  _data.isArticulo22());
            insert.setString(21,  _data.getEmpresa().getId());
            insert.setString(22,  _data.getDepartamento().getId());
            insert.setInt(23,  _data.getCentroCosto().getId());
            //agregado el 19-08-2017 para guardar num ficha
            insert.setString(24,  _data.getRut().toUpperCase());
            //agregado el 04-02-2018 para guardar clave marcacion
            insert.setString(25,  _data.getClaveMarcacion());
            
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println("[insert]empleado"
                    + ", rut:" +_data.getRut()
                    + ", codInterno:" +_data.getCodInterno()
                    + ", nombres:" +_data.getNombres()
                    + ", email:" +_data.getEmail()
                    + ", empresaId: "+_data.getEmpresa().getId()
                    + ", deptoId: "+_data.getDepartamento().getId()
                    + ", cencoId: "+_data.getCentroCosto().getId()    
                    +" - creado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("insert empleado Error1: "+sqle.toString());
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
    * Retorna lista con los empleados  existentes
    * 
    * @param _empresaId
    * @param _deptoId
    * @param _cencoId
    * @param _cargo
    * @param _rutEmpleado
    * @param _nombres
    * @param _apePaterno
    * @param _apeMaterno
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * @return 
    */
    public List<EmpleadoVO> getEmpleados(String _empresaId, 
            String _deptoId, 
            int _cencoId,
            int _cargo,
            String _rutEmpleado,
            String _nombres,
            String _apePaterno,
            String _apeMaterno,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<EmpleadoVO> lista = 
                new ArrayList<>();
        
        SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
        PreparedStatement ps = null;
        ResultSet rs = null;
        EmpleadoVO data;
        
        try{
            String sql = "SELECT "
                + "empl.empl_rut,"
                + "empl.empl_nombres,"
                + "empl.empl_ape_paterno,"
                + "empl.empl_ape_materno,"
                + "empl.empl_fecha_nacimiento,"
                + "empl.empl_direccion direccion,"
                + "empl.empl_email email,"
                + "empl.empl_fec_ini_contrato,"
                + "empl.fecha_desvinculacion,"
                + "coalesce(empl.empl_fec_fin_contrato,'3000-12-31') empl_fec_fin_contrato,"
                + "empl.empl_estado,"
                + "empl.empl_path_foto,"
                + "empl.empl_sexo,"
                + "coalesce(empl.empl_fono_fijo,'') empl_fono_fijo,"
                + "coalesce(empl.empl_fono_movil,'') empl_fono_movil,"
                + "empl.id_comuna,"
                + "comuna.comuna_nombre,"
                + "comuna.region_id,"
                + "region.region_nombre,"
                + "empl.empresa_id,"
                + "empresa.empresa_nombre,"
                + "empresa.empresa_rut,"
                + "empl.depto_id,"
                + "depto.depto_nombre,"
                + "empl.cenco_id,"
                + "cenco.ccosto_nombre,"
                + "empl_id_turno,"
                + "autoriza_ausencia,"
                + "coalesce(empl_id_cargo,-1) empl_id_cargo,"
                + "contrato_indefinido,"
                + "art_22,"
                + "cod_interno,clave_marcacion "
                + "FROM "
                + "empleado empl,"
                + "comuna,"
                + "region,"
                + "empresa,"
                + "departamento depto,"
                + "centro_costo cenco "
                + "WHERE "
                + "empl.id_comuna = comuna.comuna_id AND "
                + "empl.empresa_id = empresa.empresa_id AND "
                + "empl.depto_id = depto.depto_id AND "
                + "empl.cenco_id = cenco.ccosto_id AND "
                + "comuna.region_id = region.region_id ";
                    
            if (_empresaId!=null && _empresaId.compareTo("-1")!=0){        
                sql += " and empl.empresa_id= '"+_empresaId+"'";
            }
            if (_deptoId!=null && _deptoId.compareTo("-1")!=0){        
                sql += " and empl.depto_id = '"+_deptoId+"' ";
            }
            if (_cencoId != -1){        
                sql += " and empl.cenco_id = "+_cencoId+" ";
            }
            if (_cargo != -1){        
                sql += " and empl_id_cargo = "+_cargo+" ";
            }
            if (_rutEmpleado!=null && _rutEmpleado.compareTo("")!=0){        
                sql += " and upper(empl.empl_rut) like '"+_rutEmpleado.toUpperCase()+"%'";
            }
            if (_nombres!=null && _nombres.compareTo("")!=0){        
                sql += " and upper(empl_nombres) like '"+_nombres.toUpperCase()+"%'";
            }
            if (_apePaterno != null && _apePaterno.compareTo("") != 0){        
                sql += " and upper(empl_ape_paterno) like '"+_apePaterno.toUpperCase()+"%'";
            }
            if (_apeMaterno != null && _apeMaterno.compareTo("") != 0){        
                sql += " and upper(empl_ape_materno) like '"+_apeMaterno.toUpperCase()+"%'";
            }
            
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
                        
            System.out.println("EmpleadosDAO.getEmpleados(). SQL: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[EmpleadosDAO.getEmpleados]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new EmpleadoVO();
               
                data.setRut(rs.getString("empl_rut"));
                data.setNombres(rs.getString("empl_nombres"));
                data.setApePaterno(rs.getString("empl_ape_paterno"));
                data.setApeMaterno(rs.getString("empl_ape_materno"));
                
                data.setFechaNacimiento(rs.getDate("empl_fecha_nacimiento"));
                if (data.getFechaNacimiento() != null){
                    data.setFechaNacimientoAsStr(sdf.format(data.getFechaNacimiento()));
                }
                
                data.setDireccion(rs.getString("direccion"));
                data.setEmail(rs.getString("email"));
                
                data.setFechaInicioContrato(rs.getDate("empl_fec_ini_contrato"));
                if (data.getFechaInicioContrato() != null){
                    data.setFechaInicioContratoAsStr(sdf.format(data.getFechaInicioContrato()));
                }
                data.setFechaTerminoContrato(rs.getDate("empl_fec_fin_contrato"));
                if (data.getFechaTerminoContrato() != null){
                    data.setFechaTerminoContratoAsStr(sdf.format(data.getFechaTerminoContrato()));
                }
                
                data.setEstado(rs.getInt("empl_estado"));
                data.setPathFoto(rs.getString("empl_path_foto"));
                data.setSexo(rs.getString("empl_sexo"));
                data.setFonoFijo(rs.getString("empl_fono_fijo"));
                data.setFonoMovil(rs.getString("empl_fono_movil"));
                data.setComunaId(rs.getInt("id_comuna"));
                data.setComunaNombre(rs.getString("comuna_nombre"));
                
                EmpresaVO empresa=new EmpresaVO();
                empresa.setId(rs.getString("empresa_id"));
                empresa.setNombre(rs.getString("empresa_nombre"));
                empresa.setRut(rs.getString("empresa_rut"));
                
                DepartamentoVO departamento=new DepartamentoVO();
                departamento.setId(rs.getString("depto_id"));
                departamento.setNombre(rs.getString("depto_nombre"));
                
                CentroCostoVO cenco=new CentroCostoVO();
                cenco.setId(rs.getInt("cenco_id"));
                cenco.setNombre(rs.getString("ccosto_nombre"));
                
                data.setEmpresa(empresa);
                data.setDepartamento(departamento);
                data.setCentroCosto(cenco);
                data.setEmpresaNombre(empresa.getNombre());
                data.setDeptoNombre(departamento.getNombre());
                data.setCencoNombre(cenco.getNombre());
                data.setIdTurno(rs.getInt("empl_id_turno"));
                data.setAutorizaAusencia(rs.getBoolean("autoriza_ausencia"));
                data.setIdCargo(rs.getInt("empl_id_cargo"));
                
                data.setArticulo22(rs.getBoolean("art_22"));
                data.setContratoIndefinido(rs.getBoolean("contrato_indefinido"));
                
                data.setCodInterno(rs.getString("cod_interno"));
                data.setClaveMarcacion(rs.getString("clave_marcacion"));
                
                data.setFechaDesvinculacion(rs.getDate("fecha_desvinculacion"));
                if (data.getFechaDesvinculacion() != null){
                    data.setFechaDesvinculacionAsStr(sdf.format(data.getFechaDesvinculacion()));
                }else data.setFechaDesvinculacionAsStr("");
                
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
     * Retorna lista con los empleados  existentes
     * 
     * @param _empresaId
     * @param _deptoId
     * @param _cencoId
     * @param _cargo
     * @param _rutEmpleado
     * @param _nombres
     * @param _apePaterno
     * @param _apeMaterno
     * @param _estado
     * 
     * @return 
     */
    public List<EmpleadoVO> getEmpleados(String _empresaId, 
            String _deptoId, 
            int _cencoId,
            int _cargo,
            String _rutEmpleado,
            String _nombres,
            String _apePaterno,
            String _apeMaterno, 
            int _estado){
        
        List<EmpleadoVO> lista = new ArrayList<>();
        
        SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
        PreparedStatement ps = null;
        ResultSet rs = null;
        EmpleadoVO data;
        
        try{
            String sql = "SELECT "
                + "empl.empl_rut,"
                + "empl.empl_nombres,"
                + "empl.empl_ape_paterno,"
                + "empl.empl_ape_materno,"
                + "empl.empl_fecha_nacimiento,"
                + "empl.empl_direccion direccion,"
                + "empl.empl_email email,"
                + "empl.empl_fec_ini_contrato,"
                + "coalesce(empl.empl_fec_fin_contrato,'3000-12-31') empl_fec_fin_contrato,"
                + "empl.empl_estado,"
                + "empl.empl_path_foto,"
                + "empl.empl_sexo,"
                + "coalesce(empl.empl_fono_fijo,'') empl_fono_fijo,"
                + "coalesce(empl.empl_fono_movil,'') empl_fono_movil,"
                + "empl.id_comuna,"
                + "comuna.comuna_nombre,"
                + "comuna.region_id,"
                + "region.region_nombre,"
                + "empl.empresa_id,"
                + "empresa.empresa_nombre,"
                + "empresa.empresa_rut,"
                + "empl.depto_id,"
                + "depto.depto_nombre,"
                + "empl.cenco_id,"
                + "cenco.ccosto_nombre,"
                + "empl_id_turno,"
                + "autoriza_ausencia,"
                + "coalesce(empl_id_cargo,-1) empl_id_cargo,"
                + "contrato_indefinido,"
                + "art_22,"
                + "cod_interno,clave_marcacion,"
                + "turno.nombre_turno,"
                + "coalesce(vacaciones.saldo_dias,0) saldo_dias_vacaciones,"
                + "coalesce(vacaciones.dias_especiales,'N') dias_especiales "
                + " FROM "
                    + "empleado empl "
                    + "inner join comuna on (empl.id_comuna = comuna.comuna_id) "
                    + "inner join region on (comuna.region_id = region.region_id) "
                    + "inner join empresa on (empl.empresa_id = empresa.empresa_id) "
                    + "inner join departamento depto on (empl.depto_id = depto.depto_id) "
                    + "inner join centro_costo cenco on (empl.cenco_id = cenco.ccosto_id) "
                    + "inner join turno on (empl.empl_id_turno = turno.id_turno) "
                    + "left outer join vacaciones "
                    + "on (empl.empresa_id = vacaciones.empresa_id and empl.empl_rut = vacaciones.rut_empleado) "
                + "WHERE 1 = 1 ";
                    
            if (_empresaId!=null && _empresaId.compareTo("-1")!=0){        
                sql += " and empl.empresa_id= '"+_empresaId+"'";
            }
            if (_deptoId!=null && _deptoId.compareTo("-1")!=0){        
                sql += " and empl.depto_id = '"+_deptoId+"' ";
            }
            if (_cencoId != -1){        
                sql += " and empl.cenco_id = "+_cencoId+" ";
            }
            if (_cargo != -1){        
                sql += " and empl_id_cargo = "+_cargo+" ";
            }
            if (_estado != -1){        
                sql += " and empl_estado = " +_estado + " ";
            }
            if (_rutEmpleado!=null && _rutEmpleado.compareTo("")!=0){        
                sql += " and upper(empl.empl_rut) like '"+_rutEmpleado.toUpperCase()+"%'";
            }
            if (_nombres!=null && _nombres.compareTo("")!=0){        
                sql += " and upper(empl_nombres) like '"+_nombres.toUpperCase()+"%'";
            }
            if (_apePaterno != null && _apePaterno.compareTo("") != 0){        
                sql += " and upper(empl_ape_paterno) like '"+_apePaterno.toUpperCase()+"%'";
            }
            if (_apeMaterno != null && _apeMaterno.compareTo("") != 0){        
                sql += " and upper(empl_ape_materno) like '"+_apeMaterno.toUpperCase()+"%'";
            }
            
            sql += " order by empl.empl_rut";
            System.out.println("[EmpladosDAO."
                + "getEmpleados]sql: " + sql);
            dbConn = dbLocator.getConnection(m_dbpoolName,"[EmpleadosDAO.getEmpleados2]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new EmpleadoVO();
               
                data.setRut(rs.getString("empl_rut"));
                data.setNombres(rs.getString("empl_nombres"));
                data.setApePaterno(rs.getString("empl_ape_paterno"));
                data.setApeMaterno(rs.getString("empl_ape_materno"));
                data.setFechaNacimiento(rs.getDate("empl_fecha_nacimiento"));
                if (data.getFechaNacimiento() != null){
                    data.setFechaNacimientoAsStr(sdf.format(data.getFechaNacimiento()));
                }
                
                data.setDireccion(rs.getString("direccion"));
                data.setEmail(rs.getString("email"));
                
                data.setFechaInicioContrato(rs.getDate("empl_fec_ini_contrato"));
                if (data.getFechaInicioContrato() != null){
                    data.setFechaInicioContratoAsStr(sdf.format(data.getFechaInicioContrato()));
                }
                data.setFechaTerminoContrato(rs.getDate("empl_fec_fin_contrato"));
                if (data.getFechaTerminoContrato() != null){
                    data.setFechaTerminoContratoAsStr(sdf.format(data.getFechaTerminoContrato()));
                }
                
                data.setEstado(rs.getInt("empl_estado"));
                data.setPathFoto(rs.getString("empl_path_foto"));
                data.setSexo(rs.getString("empl_sexo"));
                data.setFonoFijo(rs.getString("empl_fono_fijo"));
                data.setFonoMovil(rs.getString("empl_fono_movil"));
                data.setComunaId(rs.getInt("id_comuna"));
                data.setComunaNombre(rs.getString("comuna_nombre"));
                
                EmpresaVO empresa=new EmpresaVO();
                empresa.setId(rs.getString("empresa_id"));
                empresa.setNombre(rs.getString("empresa_nombre"));
                empresa.setRut(rs.getString("empresa_rut"));
                
                DepartamentoVO departamento=new DepartamentoVO();
                departamento.setId(rs.getString("depto_id"));
                departamento.setNombre(rs.getString("depto_nombre"));
                
                CentroCostoVO cenco=new CentroCostoVO();
                cenco.setId(rs.getInt("cenco_id"));
                cenco.setNombre(rs.getString("ccosto_nombre"));
                
                data.setEmpresa(empresa);
                data.setDepartamento(departamento);
                data.setCentroCosto(cenco);
                data.setEmpresaNombre(empresa.getNombre());
                data.setDeptoNombre(departamento.getNombre());
                data.setCencoNombre(cenco.getNombre());
                data.setIdTurno(rs.getInt("empl_id_turno"));
                data.setAutorizaAusencia(rs.getBoolean("autoriza_ausencia"));
                data.setIdCargo(rs.getInt("empl_id_cargo"));
                
                data.setArticulo22(rs.getBoolean("art_22"));
                data.setContratoIndefinido(rs.getBoolean("contrato_indefinido"));
                
                data.setCodInterno(rs.getString("cod_interno"));
                data.setClaveMarcacion(rs.getString("clave_marcacion"));
                data.setNombreTurno(rs.getString("nombre_turno"));
                data.setSaldoDiasVacaciones(rs.getInt("saldo_dias_vacaciones"));
                data.setDiasEspeciales(rs.getString("dias_especiales"));
                
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
    * Lista de empleados, segun filtro.
    * @param _empleado
    * @return 
    */
    public List<EmpleadoVO> getEmpleadosByFiltro(EmpleadoVO _empleado){
        
        List<EmpleadoVO> lista = new ArrayList<>();
        
        SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
        PreparedStatement ps = null;
        ResultSet rs = null;
        EmpleadoVO data;
        
        try{
            String sql = "SELECT "
                + "empl.empl_rut,empl.empl_nombres,empl.empl_ape_paterno,"
                + "empl.empl_ape_materno,empl.empl_fecha_nacimiento,"
                + "empl.empl_direccion direccion,empl.empl_email email,"
                + "empl.empl_fec_ini_contrato,"
                + "coalesce(empl.empl_fec_fin_contrato,'3000-12-31') empl_fec_fin_contrato,"
                + "empl.empl_estado,empl.empl_path_foto,empl.empl_sexo,"
                + "coalesce(empl.empl_fono_fijo,'') empl_fono_fijo,"
                + "coalesce(empl.empl_fono_movil,'') empl_fono_movil,"
                + "empl.id_comuna,comuna.comuna_nombre,comuna.region_id,"
                + "region.region_nombre,empl.empresa_id,empresa.empresa_nombre,"
                + "empresa.empresa_rut,empl.depto_id,depto.depto_nombre,"
                + "empl.cenco_id,cenco.ccosto_nombre,empl_id_turno,"
                + "autoriza_ausencia,coalesce(empl_id_cargo,-1) empl_id_cargo,"
                + "contrato_indefinido,art_22,cod_interno,clave_marcacion,"
                + "turno.nombre_turno,cargo.cargo_nombre "
                + "FROM "
                    + "empleado empl,"
                    + "comuna,"
                    + "region,"
                    + "empresa,"
                    + "departamento depto,"
                    + "centro_costo cenco, "
                    + "turno, "
                    + "cargo "
                + " WHERE "
                    + "empl.id_comuna = comuna.comuna_id AND "
                    + "empl.empresa_id = empresa.empresa_id AND "
                    + "empl.depto_id = depto.depto_id AND "
                    + "empl.cenco_id = cenco.ccosto_id AND "
                    + "comuna.region_id = region.region_id "
                    + "and empl.empl_id_turno = turno.id_turno "
                    + "and empl.empl_id_cargo=cargo.cargo_id ";
                    
            if (_empleado.getEmpresaId() != null && _empleado.getEmpresaId().compareTo("-1")!=0){        
                sql += " and empl.empresa_id= '"+_empleado.getEmpresaId()+"'";
            }
            if (_empleado.getDeptoId() != null && _empleado.getDeptoId().compareTo("-1")!=0){        
                sql += " and empl.depto_id = '" + _empleado.getDeptoId() + "' ";
            }
            if (_empleado.getCencoId() != -1){        
                sql += " and empl.cenco_id = " + _empleado.getCencoId() + " ";
            }
            if (_empleado.getIdCargo() != -1){        
                sql += " and empl_id_cargo = " + _empleado.getIdCargo() + " ";
            }
            
            if (_empleado.isEmpleadoVigente()){
                sql += " and empl_estado = 1 "
                    + "and empl_fec_fin_contrato >= current_date ";
            }
            
            if (_empleado.getEstado() != -1){        
                sql += " and empl_estado = " + _empleado.getEstado() + " ";
            }
            if (_empleado.getRut() != null && _empleado.getRut().compareTo("")!=0){        
                sql += " and upper(empl.empl_rut) like '" + _empleado.getRut().toUpperCase() + "%'";
            }
            if (_empleado.isArticulo22()){        
                sql += " and art_22 = true ";
            }else sql += " and art_22 in (true,false) ";
                        
            sql += " order by empl.empl_rut";
            
            System.out.println("[EmpleadosDAO."
                + "getEmpleadosByFiltro]Sql: " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[EmpleadosDAO.getEmpleadosByFiltro]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new EmpleadoVO();
               
                data.setRut(rs.getString("empl_rut"));
                data.setNombres(rs.getString("empl_nombres"));
                data.setApePaterno(rs.getString("empl_ape_paterno"));
                data.setApeMaterno(rs.getString("empl_ape_materno"));
                
                data.setFechaNacimiento(rs.getDate("empl_fecha_nacimiento"));
                if (data.getFechaNacimiento() != null){
                    data.setFechaNacimientoAsStr(sdf.format(data.getFechaNacimiento()));
                }
                
                data.setDireccion(rs.getString("direccion"));
                data.setEmail(rs.getString("email"));
                
                data.setFechaInicioContrato(rs.getDate("empl_fec_ini_contrato"));
                if (data.getFechaInicioContrato() != null){
                    data.setFechaInicioContratoAsStr(sdf.format(data.getFechaInicioContrato()));
                }
                data.setFechaTerminoContrato(rs.getDate("empl_fec_fin_contrato"));
                if (data.getFechaTerminoContrato() != null){
                    data.setFechaTerminoContratoAsStr(sdf.format(data.getFechaTerminoContrato()));
                }
                
                data.setEstado(rs.getInt("empl_estado"));
                data.setPathFoto(rs.getString("empl_path_foto"));
                data.setSexo(rs.getString("empl_sexo"));
                data.setFonoFijo(rs.getString("empl_fono_fijo"));
                data.setFonoMovil(rs.getString("empl_fono_movil"));
                data.setComunaId(rs.getInt("id_comuna"));
                data.setComunaNombre(rs.getString("comuna_nombre"));
                
                EmpresaVO empresa=new EmpresaVO();
                empresa.setId(rs.getString("empresa_id"));
                empresa.setNombre(rs.getString("empresa_nombre"));
                empresa.setRut(rs.getString("empresa_rut"));
                
                DepartamentoVO departamento=new DepartamentoVO();
                departamento.setId(rs.getString("depto_id"));
                departamento.setNombre(rs.getString("depto_nombre"));
                
                CentroCostoVO cenco=new CentroCostoVO();
                cenco.setId(rs.getInt("cenco_id"));
                cenco.setNombre(rs.getString("ccosto_nombre"));
                
                data.setEmpresa(empresa);
                data.setDepartamento(departamento);
                data.setCentroCosto(cenco);
                data.setEmpresaNombre(empresa.getNombre());
                data.setDeptoNombre(departamento.getNombre());
                data.setCencoNombre(cenco.getNombre());
                data.setIdTurno(rs.getInt("empl_id_turno"));
                data.setAutorizaAusencia(rs.getBoolean("autoriza_ausencia"));
                data.setIdCargo(rs.getInt("empl_id_cargo"));
                data.setCodInterno(rs.getString("cod_interno"));
                data.setArticulo22(rs.getBoolean("art_22"));
                data.setContratoIndefinido(rs.getBoolean("contrato_indefinido"));
                
                data.setClaveMarcacion(rs.getString("clave_marcacion"));
                data.setNombreTurno(rs.getString("nombre_turno"));
                
                data.setNombreCargo(rs.getString("cargo_nombre"));
                
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
    * Retorna lista con los empleados  existentes
    * 
    * @param _empresaId
    * @param _deptoId
    * @param _cencoId
    * @return 
    */
    public List<EmpleadoVO> getEmpleadosDesvinculados(String _empresaId, 
            String _deptoId, 
            int _cencoId){
        
        List<EmpleadoVO> lista = 
                new ArrayList<>();
        
        SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
        PreparedStatement ps = null;
        ResultSet rs = null;
        EmpleadoVO data;
        
        try{
            String sql = "SELECT "
                + "empl.empl_rut,"
                + "empl.empl_nombres,"
                + "empl.empl_ape_paterno,"
                + "empl.empl_ape_materno,"
                + "empl.empl_fecha_nacimiento,"
                + "empl.empl_direccion direccion,"
                + "empl.empl_email email,"
                + "empl.empl_fec_ini_contrato,"
                + "coalesce(empl.empl_fec_fin_contrato,'3000-12-31') empl_fec_fin_contrato,"
                + "empl.empl_estado,"
                + "empl.fecha_desvinculacion,"
                + "empl.empl_path_foto,"
                + "empl.empl_sexo,"
                + "coalesce(empl.empl_fono_fijo,'') empl_fono_fijo,"
                + "coalesce(empl.empl_fono_movil,'') empl_fono_movil,"
                + "empl.id_comuna,"
                + "comuna.comuna_nombre,"
                + "comuna.region_id,"
                + "region.region_nombre,"
                + "empl.empresa_id,"
                + "empresa.empresa_nombre,"
                + "empresa.empresa_rut,"
                + "empl.depto_id,"
                + "depto.depto_nombre,"
                + "empl.cenco_id,"
                + "cenco.ccosto_nombre,"
                + "empl_id_turno,"
                + "autoriza_ausencia,"
                + "coalesce(empl_id_cargo,-1) empl_id_cargo,"
                + "cargo.cargo_nombre,"
                + "contrato_indefinido,"
                + "art_22,"
                + "cod_interno,"
                + "clave_marcacion,"
                + "turno.nombre_turno "    
                + "FROM empleado empl "
                    + " inner join comuna on (empl.id_comuna = comuna.comuna_id) "
                    + " inner join region on (comuna.region_id = region.region_id) "
                    + " inner join empresa on (empl.empresa_id = empresa.empresa_id) "
                    + " inner join departamento depto on (empl.depto_id = depto.depto_id) "
                    + " inner join centro_costo cenco on (empl.cenco_id = cenco.ccosto_id) "
                    + " inner join cargo on (empl.empl_id_cargo = cargo.cargo_id) "
                    + " inner join turno on (empl.empl_id_turno = turno.id_turno) "
                    + " where "
                    + " (empl.empl_estado = " + Constantes.ESTADO_NO_VIGENTE + " and fecha_desvinculacion is not null) ";
            
            if (_empresaId!=null && _empresaId.compareTo("-1")!=0){        
                sql += " and empl.empresa_id= '"+_empresaId+"'";
            }
            if (_deptoId!=null && _deptoId.compareTo("-1")!=0){        
                sql += " and empl.depto_id = '"+_deptoId+"' ";
            }
            if (_cencoId != -1){        
                sql += " and empl.cenco_id = '"+_cencoId+"' ";
            }
                        
            sql += " order by empl_nombres"; 
                        
            System.out.println("[EmpleadosDAO."
                + "getEmpleadosDesvinculados]SQL: " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[EmpleadosDAO.getEmpleadosDesvinculados]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new EmpleadoVO();
               
                data.setRut(rs.getString("empl_rut"));
                data.setNombres(rs.getString("empl_nombres"));
                data.setApePaterno(rs.getString("empl_ape_paterno"));
                data.setApeMaterno(rs.getString("empl_ape_materno"));
                
                data.setFechaNacimiento(rs.getDate("empl_fecha_nacimiento"));
                if (data.getFechaNacimiento() != null){
                    data.setFechaNacimientoAsStr(sdf.format(data.getFechaNacimiento()));
                }
                
                data.setDireccion(rs.getString("direccion"));
                data.setEmail(rs.getString("email"));
                
                data.setFechaInicioContrato(rs.getDate("empl_fec_ini_contrato"));
                if (data.getFechaInicioContrato() != null){
                    data.setFechaInicioContratoAsStr(sdf.format(data.getFechaInicioContrato()));
                }
                data.setFechaTerminoContrato(rs.getDate("empl_fec_fin_contrato"));
                if (data.getFechaTerminoContrato() != null){
                    data.setFechaTerminoContratoAsStr(sdf.format(data.getFechaTerminoContrato()));
                }
                
                data.setEstado(rs.getInt("empl_estado"));
                data.setPathFoto(rs.getString("empl_path_foto"));
                data.setSexo(rs.getString("empl_sexo"));
                data.setFonoFijo(rs.getString("empl_fono_fijo"));
                data.setFonoMovil(rs.getString("empl_fono_movil"));
                data.setComunaId(rs.getInt("id_comuna"));
                data.setComunaNombre(rs.getString("comuna_nombre"));
                
                EmpresaVO empresa=new EmpresaVO();
                empresa.setId(rs.getString("empresa_id"));
                empresa.setNombre(rs.getString("empresa_nombre"));
                empresa.setRut(rs.getString("empresa_rut"));
                
                DepartamentoVO departamento=new DepartamentoVO();
                departamento.setId(rs.getString("depto_id"));
                departamento.setNombre(rs.getString("depto_nombre"));
                
                CentroCostoVO cenco=new CentroCostoVO();
                cenco.setId(rs.getInt("cenco_id"));
                cenco.setNombre(rs.getString("ccosto_nombre"));
                
                data.setEmpresa(empresa);
                
                data.setEmpresaId(empresa.getId());
                
                data.setDepartamento(departamento);
                data.setCentroCosto(cenco);
                data.setEmpresaNombre(empresa.getNombre());
                data.setDeptoNombre(departamento.getNombre());
                data.setCencoNombre(cenco.getNombre());
                //data.setFechaIngresoPersonal(rs.getDate("fec_ingreso_depto_cenco"));
                //data.setEstadoPersonal(rs.getInt("estado_depto_cenco"));
                data.setIdTurno(rs.getInt("empl_id_turno"));
                data.setAutorizaAusencia(rs.getBoolean("autoriza_ausencia"));
                data.setIdCargo(rs.getInt("empl_id_cargo"));
                
                data.setArticulo22(rs.getBoolean("art_22"));
                data.setContratoIndefinido(rs.getBoolean("contrato_indefinido"));
                data.setNombreCargo(rs.getString("cargo_nombre"));
                data.setCodInterno(rs.getString("cod_interno"));
                data.setClaveMarcacion(rs.getString("clave_marcacion"));
                
                data.setFechaDesvinculacion(rs.getDate("fecha_desvinculacion"));
                if (data.getFechaDesvinculacion() != null){
                    data.setFechaDesvinculacionAsStr(sdf.format(data.getFechaDesvinculacion()));
                }else data.setFechaDesvinculacionAsStr("");
                
                data.setNombreTurno(rs.getString("nombre_turno"));
                
                lista.add(data);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("[EmpleadosDAO.getEmpleadosDesvinculados]"
                + "Error: " + sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[EmpleadosDAO.getEmpleadosDesvinculados]"
                    + "Error: " + ex.toString());
            }
        }
        
        return lista;
    }
    
    /**
    * Retorna lista con los empleados  existentes
    * 
    * @param _empresaId
    * @param _deptoId
    * @param _cencoId
    * @param _cargo
    * @param _idTurno
    * @param _rutEmpleado
    * @param _nombres
    * @param _apePaterno
    * @param _apeMaterno
    * @param _estado
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * @return 
    */
    public List<EmpleadoVO> getEmpleados(String _empresaId, 
            String _deptoId, 
            int _cencoId,
            int _cargo,
            int _idTurno,
            String _rutEmpleado,
            String _nombres,
            String _apePaterno,
            String _apeMaterno,
            int _estado,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<EmpleadoVO> lista = 
                new ArrayList<>();
        
        SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
        PreparedStatement ps = null;
        ResultSet rs = null;
        EmpleadoVO data;
        
        try{
            String sql = "SELECT "
                + "empl.empl_rut,"
                + "empl.empl_nombres,"
                + "empl.empl_ape_paterno,"
                + "empl.empl_ape_materno,"
                + "empl.empl_fecha_nacimiento,"
                + "empl.empl_direccion direccion,"
                + "empl.empl_email email,"
                + "empl.empl_fec_ini_contrato,"
                + "coalesce(empl.empl_fec_fin_contrato,'3000-12-31') empl_fec_fin_contrato,"
                + "empl.empl_estado,"
                + "empl.fecha_desvinculacion,"
                + "empl.empl_path_foto,"
                + "empl.empl_sexo,"
                + "coalesce(empl.empl_fono_fijo,'') empl_fono_fijo,"
                + "coalesce(empl.empl_fono_movil,'') empl_fono_movil,"
                + "empl.id_comuna,"
                + "comuna.comuna_nombre,"
                + "comuna.region_id,"
                + "region.region_nombre,"
                + "empl.empresa_id,"
                + "empresa.empresa_nombre,"
                + "empresa.empresa_rut,"
                + "empl.depto_id,"
                + "depto.depto_nombre,"
                + "empl.cenco_id,"
                + "cenco.ccosto_nombre,"
                + "empl_id_turno,"
                + "autoriza_ausencia,"
                + "coalesce(empl_id_cargo,-1) empl_id_cargo,"
                + "cargo.cargo_nombre,"
                + "contrato_indefinido,"
                + "art_22,"
                + "cod_interno,clave_marcacion "
                + "FROM empleado empl "
                    + "inner join comuna on (empl.id_comuna = comuna.comuna_id) "
                    + "inner join region on (comuna.region_id = region.region_id) "
                    + "inner join empresa on (empl.empresa_id = empresa.empresa_id) "
                    + "inner join departamento depto on (empl.depto_id = depto.depto_id) "
                    + "inner join centro_costo cenco on (empl.cenco_id = cenco.ccosto_id) "
                    + "inner join cargo on (empl.empl_id_cargo = cargo.cargo_id) "
                    + "where (1 = 1) ";
                        
            if (_empresaId!=null && _empresaId.compareTo("-1")!=0){        
                sql += " and empl.empresa_id= '"+_empresaId+"'";
            }
            if (_deptoId!=null && _deptoId.compareTo("-1")!=0){        
                sql += " and empl.depto_id = '"+_deptoId+"' ";
            }
            if (_cencoId != -1){        
                sql += " and empl.cenco_id = '"+_cencoId+"' ";
            }
            if (_cargo != -1){        
                sql += " and empl_id_cargo = "+_cargo+" ";
            }
            if (_idTurno != -1){        
                sql += " and empl_id_turno = " + _idTurno + " ";
            }
            if (_rutEmpleado!=null && _rutEmpleado.compareTo("")!=0){        
                sql += " and upper(empl.empl_rut) like '"+_rutEmpleado.toUpperCase()+"%'";
            }
            if (_nombres!=null && _nombres.compareTo("")!=0){        
                sql += " and upper(empl_nombres) like '"+_nombres.toUpperCase()+"%'";
            }
            if (_apePaterno != null && _apePaterno.compareTo("") != 0){        
                sql += " and upper(empl_ape_paterno) like '"+_apePaterno.toUpperCase()+"%'";
            }
            if (_apeMaterno != null && _apeMaterno.compareTo("") != 0){        
                sql += " and upper(empl_ape_materno) like '"+_apeMaterno.toUpperCase()+"%'";
            }
            
            if (_estado != -1){        
                sql += " and empl.empl_estado = " + _estado + " ";
            }
                        
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
                        
            System.out.println("[EmpleadosDAO.getEmpleados]SQL: " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[EmpleadosDAO.getEmpleados3]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new EmpleadoVO();
               
                data.setRut(rs.getString("empl_rut"));
                data.setNombres(rs.getString("empl_nombres"));
                data.setApePaterno(rs.getString("empl_ape_paterno"));
                data.setApeMaterno(rs.getString("empl_ape_materno"));
                
                data.setFechaNacimiento(rs.getDate("empl_fecha_nacimiento"));
                if (data.getFechaNacimiento() != null){
                    data.setFechaNacimientoAsStr(sdf.format(data.getFechaNacimiento()));
                }
                
                data.setDireccion(rs.getString("direccion"));
                data.setEmail(rs.getString("email"));
                
                data.setFechaInicioContrato(rs.getDate("empl_fec_ini_contrato"));
                if (data.getFechaInicioContrato() != null){
                    data.setFechaInicioContratoAsStr(sdf.format(data.getFechaInicioContrato()));
                }
                data.setFechaTerminoContrato(rs.getDate("empl_fec_fin_contrato"));
                if (data.getFechaTerminoContrato() != null){
                    data.setFechaTerminoContratoAsStr(sdf.format(data.getFechaTerminoContrato()));
                }
                
                data.setEstado(rs.getInt("empl_estado"));
                data.setPathFoto(rs.getString("empl_path_foto"));
                data.setSexo(rs.getString("empl_sexo"));
                data.setFonoFijo(rs.getString("empl_fono_fijo"));
                data.setFonoMovil(rs.getString("empl_fono_movil"));
                data.setComunaId(rs.getInt("id_comuna"));
                data.setComunaNombre(rs.getString("comuna_nombre"));
                
                EmpresaVO empresa=new EmpresaVO();
                empresa.setId(rs.getString("empresa_id"));
                empresa.setNombre(rs.getString("empresa_nombre"));
                empresa.setRut(rs.getString("empresa_rut"));
                
                DepartamentoVO departamento=new DepartamentoVO();
                departamento.setId(rs.getString("depto_id"));
                departamento.setNombre(rs.getString("depto_nombre"));
                
                CentroCostoVO cenco=new CentroCostoVO();
                cenco.setId(rs.getInt("cenco_id"));
                cenco.setNombre(rs.getString("ccosto_nombre"));
                
                data.setEmpresa(empresa);
                data.setDepartamento(departamento);
                data.setCentroCosto(cenco);
                data.setEmpresaNombre(empresa.getNombre());
                data.setDeptoNombre(departamento.getNombre());
                data.setCencoNombre(cenco.getNombre());
                //data.setFechaIngresoPersonal(rs.getDate("fec_ingreso_depto_cenco"));
                //data.setEstadoPersonal(rs.getInt("estado_depto_cenco"));
                data.setIdTurno(rs.getInt("empl_id_turno"));
                data.setAutorizaAusencia(rs.getBoolean("autoriza_ausencia"));
                data.setIdCargo(rs.getInt("empl_id_cargo"));
                
                data.setArticulo22(rs.getBoolean("art_22"));
                data.setContratoIndefinido(rs.getBoolean("contrato_indefinido"));
                data.setNombreCargo(rs.getString("cargo_nombre"));
                data.setCodInterno(rs.getString("cod_interno"));
                data.setClaveMarcacion(rs.getString("clave_marcacion"));
                
                data.setFechaDesvinculacion(rs.getDate("fecha_desvinculacion"));
                if (data.getFechaDesvinculacion() != null){
                    data.setFechaDesvinculacionAsStr(sdf.format(data.getFechaDesvinculacion()));
                }else data.setFechaDesvinculacionAsStr("");
                
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
    * Retorna lista con los empleados con contrato caducado o proximo a caducar
    *  Solo se deben mostrar los empleados que cumplan con lo sgte:
    *        .- CONTRATO INDEFINIDO = NO
    *        .- articulos 22 = NO
    *        .- Estado = VIGENTE
    * 
    * 
    * @param _empresaId
    * @param _deptoId
    * @param _cencoId
    * @param _cargo
    * @param _idTurno
    * @param _rutEmpleado
    * @param _nombres
    * @param _apePaterno
    * @param _apeMaterno
     * @param _cencosId
    * @param _estado
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * @return 
    */
    public List<EmpleadoVO> getCaducados(String _empresaId, 
            String _deptoId, 
            int _cencoId,
            int _cargo,
            int _idTurno,
            String _rutEmpleado,
            String _nombres,
            String _apePaterno,
            String _apeMaterno,
            int _estado,
            String _cencosId,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<EmpleadoVO> lista = 
                new ArrayList<>();
        
        //SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
        PreparedStatement ps = null;
        ResultSet rs = null;
        EmpleadoVO data;
        
        try{
            String sql = "SELECT "
                + "empl.empl_rut,"
                + "empl.empl_nombres,"
                + "empl.empl_ape_paterno,"
                + "empl.empl_ape_materno,"
                + "empl.empl_fecha_nacimiento,"
                + "empl.empl_direccion direccion,"
                + "empl.empl_email email,"
                + "empl.empl_fec_ini_contrato,"
                + "to_char(empl.empl_fec_ini_contrato,'yyyy-MM-dd') f_ini_contrato_str," 
                + "coalesce(to_char(empl.empl_fec_fin_contrato,'yyyy-MM-dd'),'3000-12-31') f_fin_contrato_str,"
                + "empl.empl_estado,"
                + "empl.empl_path_foto,"
                + "empl.empl_sexo,"
                + "coalesce(empl.empl_fono_fijo,'') empl_fono_fijo,"
                + "coalesce(empl.empl_fono_movil,'') empl_fono_movil,"
                + "empl.id_comuna,"
                + "comuna.comuna_nombre,"
                + "comuna.region_id,"
                + "region.region_nombre,"
                + "empl.empresa_id,"
                + "empresa.empresa_nombre,"
                + "empresa.empresa_rut,"
                + "empl.depto_id,"
                + "depto.depto_nombre,"
                + "empl.cenco_id,"
                + "cenco.ccosto_nombre,"
                + "empl_id_turno,"
                + "autoriza_ausencia,"
                + "coalesce(empl_id_cargo,-1) empl_id_cargo,"
                + "contrato_indefinido,"
                + "art_22,"
                + "cod_interno,clave_marcacion, turno.nombre_turno "
                + "FROM "
                    + "empleado empl "
                    + "inner join comuna  on (empl.id_comuna = comuna.comuna_id) "
                    + "inner join empresa on (empl.empresa_id = empresa.empresa_id) "
                    + "inner join region on (comuna.region_id = region.region_id) "
                    + "inner join departamento depto on (empl.depto_id = depto.depto_id) "
                    + "inner join centro_costo cenco on (empl.cenco_id = cenco.ccosto_id) "
                    + "inner join turno "
                    + "on (empl.empresa_id = turno.empresa_id and empl.empl_id_turno = turno.id_turno) "
                    + "WHERE "
                    + "contrato_indefinido = false "
                    + "and art_22 = false "
                    + "and empl_estado = 1 ";
            
            if (_empresaId!=null && _empresaId.compareTo("-1")!=0){        
                sql += " and empl.empresa_id= '"+_empresaId+"'";
            }
            if (_deptoId!=null && _deptoId.compareTo("-1")!=0){        
                sql += " and empl.depto_id = '"+_deptoId+"' ";
            }
            if (_cencosId != null){        
                sql += " and empl.cenco_id in (" + _cencosId + ") ";
            }
            if (_cargo != -1){        
                sql += " and empl_id_cargo = "+_cargo+" ";
            }
            if (_idTurno != -1){        
                sql += " and empl_id_turno = " + _idTurno + " ";
            }
                        
            if (_rutEmpleado!=null && _rutEmpleado.compareTo("")!=0){        
                sql += " and upper(empl.empl_rut) like '"+_rutEmpleado.toUpperCase()+"%'";
            }
            if (_nombres!=null && _nombres.compareTo("")!=0){        
                sql += " and upper(empl_nombres) like '"+_nombres.toUpperCase()+"%'";
            }
            if (_apePaterno != null && _apePaterno.compareTo("") != 0){        
                sql += " and upper(empl_ape_paterno) like '"+_apePaterno.toUpperCase()+"%'";
            }
            if (_apeMaterno != null && _apeMaterno.compareTo("") != 0){        
                sql += " and upper(empl_ape_materno) like '"+_apeMaterno.toUpperCase()+"%'";
            }
            if (_estado != -1){        
                sql += " and empl.empl_estado = " + _estado + " ";
            }
                        
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
                        
            System.out.println("[EmpleadosDAO.getCaducados]SQL: " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[EmpleadosDAO.getCaducados]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new EmpleadoVO();
               
                data.setRut(rs.getString("empl_rut"));
                data.setNombres(rs.getString("empl_nombres"));
                data.setApePaterno(rs.getString("empl_ape_paterno"));
                data.setApeMaterno(rs.getString("empl_ape_materno"));
                
                data.setFechaNacimiento(rs.getDate("empl_fecha_nacimiento"));
//                if (data.getFechaNacimiento() != null){
//                    data.setFechaNacimientoAsStr(sdf.format(data.getFechaNacimiento()));
//                }
                
                data.setDireccion(rs.getString("direccion"));
                data.setEmail(rs.getString("email"));
                
                data.setFechaInicioContratoAsStr(rs.getString("f_ini_contrato_str"));
                data.setFechaTerminoContratoAsStr(rs.getString("f_fin_contrato_str"));
                
                data.setEstado(rs.getInt("empl_estado"));
                data.setPathFoto(rs.getString("empl_path_foto"));
                data.setSexo(rs.getString("empl_sexo"));
                data.setFonoFijo(rs.getString("empl_fono_fijo"));
                data.setFonoMovil(rs.getString("empl_fono_movil"));
                data.setComunaId(rs.getInt("id_comuna"));
                data.setComunaNombre(rs.getString("comuna_nombre"));
                
                EmpresaVO empresa=new EmpresaVO();
                empresa.setId(rs.getString("empresa_id"));
                empresa.setNombre(rs.getString("empresa_nombre"));
                empresa.setRut(rs.getString("empresa_rut"));
                
                DepartamentoVO departamento=new DepartamentoVO();
                departamento.setId(rs.getString("depto_id"));
                departamento.setNombre(rs.getString("depto_nombre"));
                
                CentroCostoVO cenco=new CentroCostoVO();
                cenco.setId(rs.getInt("cenco_id"));
                cenco.setNombre(rs.getString("ccosto_nombre"));
                
                data.setEmpresa(empresa);
                data.setDepartamento(departamento);
                data.setCentroCosto(cenco);
                data.setEmpresaNombre(empresa.getNombre());
                data.setDeptoNombre(departamento.getNombre());
                data.setCencoNombre(cenco.getNombre());
                //data.setFechaIngresoPersonal(rs.getDate("fec_ingreso_depto_cenco"));
                //data.setEstadoPersonal(rs.getInt("estado_depto_cenco"));
                data.setIdTurno(rs.getInt("empl_id_turno"));
                data.setNombreTurno(rs.getString("nombre_turno"));
                data.setAutorizaAusencia(rs.getBoolean("autoriza_ausencia"));
                data.setIdCargo(rs.getInt("empl_id_cargo"));
                
                data.setArticulo22(rs.getBoolean("art_22"));
                data.setContratoIndefinido(rs.getBoolean("contrato_indefinido"));
                
                data.setCodInterno(rs.getString("cod_interno"));
                data.setClaveMarcacion(rs.getString("clave_marcacion"));
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
    
    public EmpleadoVO getEmpleado(String _empresaId, 
            String _codInterno){
        
        SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
        PreparedStatement ps = null;
        ResultSet rs = null;
        EmpleadoVO data=null;
        
        try{
            String sql = "SELECT "
                + "empleado.empl_rut AS rut,"
                + "coalesce(empleado.empl_nombres, '') || ' ' || "
                + "coalesce(empleado.empl_ape_paterno, '') nombre,"
                + "empleado.empl_id_cargo,"
                + "empleado.empl_email,"
                + "empleado.empl_id_turno, "
                + "empleado.empresa_id, "
                + "empresa.empresa_nombre,"
                + "empresa.empresa_direccion,"
                + "empresa.empresa_rut,"
                + "empleado.empl_ape_materno AS materno,"
                + "empleado.depto_id,"
                + "departamento.depto_nombre,"
                + "empleado.cenco_id,"
                + "centro_costo.ccosto_nombre,"
                + "centro_costo.id_comuna,"
                + "coalesce(centro_costo.es_zona_extrema,'N') es_zona_extrema,"
                + "centro_costo.email_notificacion,"
                + "comuna.comuna_nombre,"
                + "region.region_nombre,"
                + "region.region_id,"
                + "comuna.comuna_id,"
                + "cargo.cargo_nombre,"
                + "turno.nombre_turno,"
                + "empleado.empl_estado,"
                + "cargo.cargo_nombre,"
                + "turno.nombre_turno,"
                + "empleado.cod_interno,"
                + "empleado.empl_fec_ini_contrato fecha_inicio_contrato,"
                + "empleado.fecha_desvinculacion,"    
                + "to_char(empl_fec_ini_contrato,'dd/MM/yyyy') fechainicontrato,"
                + "clave_marcacion "
                + "FROM "
                + "empleado,"
                + "centro_costo,"
                + "empresa,"
                + "departamento,"
                + "comuna,"
                + "region,"
                + "cargo,"
                + "turno "
                + "WHERE "
                + "empleado.empl_id_cargo = cargo.cargo_id AND "
                + "empleado.empl_id_turno = turno.id_turno AND "
                + "empleado.empresa_id = empresa.empresa_id AND "
                + "empleado.depto_id = departamento.depto_id AND "
                + "empleado.cenco_id = centro_costo.ccosto_id AND "
                + "centro_costo.id_comuna = comuna.comuna_id AND "
                + "comuna.region_id = region.region_id ";
                                    
            sql += " and upper(empleado.empl_rut) = '" 
                + _codInterno.toUpperCase() + "' ";
            if (_empresaId!=null){
                sql += "and empleado.empresa_id ='"+_empresaId+"'";
            }
            System.out.println("[EmpleadosDAO.getEmpleado]SQL: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[EmpleadosDAO.getEmpleado]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                data = new EmpleadoVO();
               
                data.setRut(rs.getString("rut"));
                data.setCodInterno(rs.getString("cod_interno"));
                data.setNombres(rs.getString("nombre"));
                data.setApeMaterno(rs.getString("materno"));
                data.setEmail(rs.getString("empl_email"));
                data.setEmpresaNombre(rs.getString("empresa_nombre"));
                data.setEmpresaDireccion(rs.getString("empresa_direccion"));
                data.setEmpresaRut(rs.getString("empresa_rut"));
                data.setDeptoNombre(rs.getString("depto_nombre"));
                data.setCencoNombre(rs.getString("ccosto_nombre"));
                
                data.setComunaId(rs.getInt("comuna_id"));
                data.setComunaNombre(rs.getString("comuna_nombre"));
                
                data.setRegionId(rs.getInt("region_id"));
                data.setRegionNombre(rs.getString("region_nombre"));
                
                data.setIdCargo(rs.getInt("empl_id_cargo"));
                data.setNombreCargo(rs.getString("cargo_nombre"));
                data.setIdTurno(rs.getInt("empl_id_turno"));
                data.setNombreTurno(rs.getString("nombre_turno"));
                data.setFechaInicioContrato(rs.getDate("fecha_inicio_contrato"));
                data.setFechaInicioContratoAsStr(rs.getString("fechainicontrato"));
                data.setFechaDesvinculacion(rs.getDate("fecha_desvinculacion"));
                                
                EmpresaVO auxEmpresa = new EmpresaVO();
                auxEmpresa.setId(rs.getString("empresa_id"));
                auxEmpresa.setNombre(data.getEmpresaNombre());
                
                DepartamentoVO auxDepto = new DepartamentoVO();
                auxDepto.setId(rs.getString("depto_id"));
                auxDepto.setNombre(data.getDeptoNombre());
                
                CentroCostoVO auxCenco = new CentroCostoVO();
                auxCenco.setId(rs.getInt("cenco_id"));
                auxCenco.setNombre(data.getCencoNombre());
                auxCenco.setZonaExtrema(rs.getString("es_zona_extrema"));
                auxCenco.setEmailNotificacion(rs.getString("email_notificacion"));//emails separados por coma 
                        
                data.setEmpresaId(auxEmpresa.getId());
                data.setDeptoId(auxDepto.getId());
                data.setCencoId(auxCenco.getId());
                
                data.setEmpresa(auxEmpresa);
                data.setDepartamento(auxDepto);
                data.setCentroCosto(auxCenco);
                
                data.setClaveMarcacion(rs.getString("clave_marcacion"));
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
        
        return data;
    }
    
    /**
    * 
    * @param _rutEmpleado
    * @param _email
     * @param _empresaId
    * @return 
    */
    public boolean existeEmail(String _rutEmpleado, 
            String _email, 
            String _empresaId){
        boolean result=false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            String sql = "select * from empleado "
                + "where empl_email= '" + _email + "' "
                    + "and cod_interno != '" +_rutEmpleado + "' "
                    + "and empresa_id = '" + _empresaId + "'";
            System.out.println("[EmpleadosDAO.existeEmail]SQL: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[EmpleadosDAO.existeEmail]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            result = rs.next();
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
        
        return result;
    }
    
    public EmpleadoVO getEmpleado(String _empresaId, 
            String _codInterno, 
            int _turnoId){
        
        SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
        PreparedStatement ps = null;
        ResultSet rs = null;
        EmpleadoVO data=null;
        boolean openDb=false;
        try{
            String sql = "SELECT "
                + "empleado.empl_rut AS rut,"
                + "coalesce(empleado.empl_nombres, '') || ' ' || "
                + "coalesce(empleado.empl_ape_paterno, '') nombre,"
                + "empleado.empl_id_cargo,"
                + "empleado.empl_email,"
                + "empleado.empl_id_turno, "
                + "empleado.empresa_id, "
                + "empresa.empresa_nombre,"
                + "empresa.empresa_direccion,"
                + "empresa.empresa_rut,"
                + "empleado.empl_ape_materno AS materno,"
                + "empleado.depto_id,"
                + "departamento.depto_nombre,"
                + "empleado.cenco_id,"
                + "centro_costo.ccosto_nombre,"
                + "centro_costo.id_comuna,"
                + "comuna.comuna_nombre,"
                + "region.region_nombre,"
                + "region.region_id,"
                + "comuna.comuna_id,"
                + "cargo.cargo_nombre,"
                + "turno.nombre_turno,"
                + "empleado.empl_estado,"
                + "cargo.cargo_nombre,"
                + "turno.nombre_turno,"
                + "empleado.cod_interno,"
                + "empleado.empl_fec_ini_contrato fecha_inicio_contrato,"
                + "to_char(empl_fec_ini_contrato,'dd/MM/yyyy') fechainicontrato,"
                + "clave_marcacion "
                + "FROM "
                + "empleado,"
                + "centro_costo,"
                + "empresa,"
                + "departamento,"
                + "comuna,"
                + "region,"
                + "cargo,"
                + "turno "
                + "WHERE "
                + "empleado.empl_id_cargo = cargo.cargo_id AND "
                + "empleado.empl_id_turno = turno.id_turno AND "
                + "empleado.empresa_id = empresa.empresa_id AND "
                + "empleado.depto_id = departamento.depto_id AND "
                + "empleado.cenco_id = centro_costo.ccosto_id AND "
                + "centro_costo.id_comuna = comuna.comuna_id AND "
                + "comuna.region_id = region.region_id ";
                                    
            sql += " and upper(empleado.empl_rut) = '" 
                + _codInterno.toUpperCase() + "' ";
            if (_empresaId != null){
                sql += " and empleado.empresa_id ='" + _empresaId + "'";
            }
            if (_turnoId != -1){
                sql += " and empleado.empl_id_turno =" + _turnoId;
            }
            System.out.println("[EmpleadosDAO.getEmpleado]SQL: "+sql);
            //if (dbConn==null){ 
                openDb = true;
                dbConn = dbLocator.getConnection(m_dbpoolName,"[EmpleadosDAO.getEmpleado]");
            //}
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                data = new EmpleadoVO();
               
                data.setRut(rs.getString("rut"));
                data.setCodInterno(rs.getString("cod_interno"));
                data.setNombres(rs.getString("nombre"));
                data.setApeMaterno(rs.getString("materno"));
                data.setEmail(rs.getString("empl_email"));
                data.setEmpresaNombre(rs.getString("empresa_nombre"));
                data.setEmpresaDireccion(rs.getString("empresa_direccion"));
                data.setEmpresaRut(rs.getString("empresa_rut"));
                data.setDeptoNombre(rs.getString("depto_nombre"));
                data.setCencoNombre(rs.getString("ccosto_nombre"));
                data.setComunaNombre(rs.getString("comuna_nombre"));
                data.setRegionNombre(rs.getString("region_nombre"));
                data.setNombreCargo(rs.getString("cargo_nombre"));
                data.setIdTurno(rs.getInt("empl_id_turno"));
                data.setNombreTurno(rs.getString("nombre_turno"));
                data.setFechaInicioContratoAsStr(rs.getString("fechainicontrato"));
                
                EmpresaVO auxEmpresa = new EmpresaVO();
                auxEmpresa.setId(rs.getString("empresa_id"));
                auxEmpresa.setNombre(data.getEmpresaNombre());
                
                DepartamentoVO auxDepto = new DepartamentoVO();
                auxDepto.setId(rs.getString("depto_id"));
                auxDepto.setNombre(data.getDeptoNombre());
                
                CentroCostoVO auxCenco = new CentroCostoVO();
                auxCenco.setId(rs.getInt("cenco_id"));
                auxCenco.setNombre(data.getCencoNombre());
                
                data.setEmpresa(auxEmpresa);
                data.setDepartamento(auxDepto);
                data.setCentroCosto(auxCenco);
                
                data.setClaveMarcacion(rs.getString("clave_marcacion"));
            }

            ps.close();
            rs.close();
            dbConn.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("Error: "+sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbConn.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
        
        return data;
    }
    
    
    public boolean tieneContratoVigente(String _empresaId, 
            String _codInterno, int _cencoId){
        
        boolean estaVigente = true;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int countrows=0;
        try{
            String sql = "select count(empl_rut) contratoVigente "
                + "from empleado "
                + "where "
                + "(empresa_id = '" + _empresaId + "' "
                + "and upper(empl_rut) = upper('" + _codInterno + "')  and cenco_id = " + _cencoId + ") "
                + " and (contrato_indefinido is true or (contrato_indefinido is false and current_date between empl_fec_ini_contrato  and empl_fec_fin_contrato))";
            System.out.println("[EmpleadosDAO.tieneContratoVigente]SQL: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[EmpleadosDAO.tieneContratoVigente]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                countrows = rs.getInt("contratoVigente");
            }
            
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
        
        estaVigente = (countrows>0);
        return estaVigente;
    }
    
    
    
    /**
     * Indica si existe el empleado en el sistema
     * 
     * @param _empresaId
     * @param _codInterno
     * @param _rut
     * 
     * @return 
     */
    public boolean existeEmpleado(String _empresaId,
            String _codInterno,
            String _rut){
        ResultSet rs = null;
        PreparedStatement ps = null;
        boolean existe = false;
        try{
            String sql = "select empl_rut,cod_interno "
                + "from empleado "
                + "where empresa_id = '"+_empresaId+"' "
                + "and upper(empl_rut) = '"+_rut.toUpperCase()+"' "
                + "and cod_interno = '"+_codInterno+"'";
                        
            System.out.println("[EmpleadosDAO.existeEmpleado]"
                + "SQL: " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[EmpleadosDAO.existeEmpleado]");
            ps = dbConn.prepareStatement(sql);
            
            rs = ps.executeQuery();

            existe = rs.next();

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
        
        return existe;
    }
    
    /**
     * Retorna info del empleado en formato json
     * 
     * @param _empresaId
     * @param _deptoId
     * @param _cencoId
     * @param _rutEmpleado
     * @return 
     */
    public String getEmpleadoJson(String _empresaId, 
            String _deptoId, 
            int _cencoId,
            String _rutEmpleado){

        String strJson = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            String sql = "SELECT "
                + "get_empleado_json"
                    + "('" + _empresaId + "','" + _deptoId + "',"+_cencoId+",'" + _rutEmpleado + "') strjson";

            System.out.println("[EmpleadosDAO."
                + "getEmpleadoJson]Sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[EmpleadosDAO.getEmpleadoJson]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                strJson += rs.getString("strjson");
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
        
        return strJson;
    }
    
    /**
     * Retorna info de los empleados en formato json
     * 
     * @param _empresaId
     * @param _deptoId
     * @param _cencoId
     * 
     * @return 
     */
    public String getEmpleadosJson(String _empresaId, 
            String _deptoId, 
            int _cencoId){

        String strJson = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            String sql = "SELECT "
                + "get_empleados_json"
                    + "('" + _empresaId + "','" + _deptoId + "',"+_cencoId+") strjson";

            System.out.println("[EmpleadosDAO."
                + "getEmpleadosJson]Sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[EmpleadosDAO.getEmpleadosJson]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                strJson += rs.getString("strjson");
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
        
        return strJson;
    }
    
    /**
     * Obtiene lista de empleados filtrando por empresa, 
     * depto, cenco, turno
     * 
     * @param _empresaId
     * @param _deptoId
     * @param _cencoId
     * @param _turnoId
     * @return 
     */
    public List<EmpleadoVO> getEmpleadosNew(String _empresaId, 
            String _deptoId, 
            int _cencoId,
            int _turnoId){
        
        List<EmpleadoVO> lista = new ArrayList<>();
        boolean dbOpen=false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        EmpleadoVO data;
        SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
        
        try{
            String sql = "SELECT "
                + "empleado.empl_rut AS rut,"
                + "empl_fec_ini_contrato inicio_contrato,"
                + "coalesce(empleado.empl_nombres, '') || ' ' || "
                + "coalesce(empleado.empl_ape_paterno, '') nombre,"
                + "empleado.empl_id_cargo,"
                + "empleado.empl_id_turno, "
                + "empleado.empresa_id, "
                + "empresa.empresa_nombre, "
                + "empleado.empl_ape_materno AS materno,"
                + "empleado.depto_id,"
                + "departamento.depto_nombre,"
                + "empleado.cenco_id,"
                + "centro_costo.ccosto_nombre,"
                + "centro_costo.id_comuna,"
                + "comuna.comuna_nombre,"
                + "region.region_nombre,"
                + "region.region_id,"
                + "comuna.comuna_id,"
                + "cargo.cargo_nombre,"
                + "turno.nombre_turno,"
                + "empleado.empl_estado,"
                + "cargo.cargo_nombre,"
                + "turno.nombre_turno,"
                + "empresa.empresa_nombre,"
                    + "empleado.cod_interno,empleado.clave_marcacion "
                + "FROM "
                + "empleado,"
                + "centro_costo,"
                + "empresa,"
                + "departamento,"
                + "comuna,"
                + "region,"
                + "cargo,"
                + "turno "
                + "WHERE "
                + "empleado.empl_id_cargo = cargo.cargo_id AND "
                + "empleado.empl_id_turno = turno.id_turno AND "
                + "empleado.empresa_id = empresa.empresa_id AND "
                + "empleado.depto_id = departamento.depto_id AND "
                + "empleado.cenco_id = centro_costo.ccosto_id AND "
                + "centro_costo.id_comuna = comuna.comuna_id AND "
                + "comuna.region_id = region.region_id AND "
                + "empleado.empl_estado = 1 ";

            if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
                sql += " and empleado.empresa_id= '" + _empresaId + "'";
            }
            if (_deptoId != null && _deptoId.compareTo("-1") != 0){        
                sql += " and empleado.depto_id = '" + _deptoId + "' ";
            }
            if (_cencoId != -1){        
                sql += " and empleado.cenco_id = " + _cencoId + " ";
            }
            if (_turnoId != -1){        
                sql += " and empl_id_turno = "+_turnoId+" ";
            }
            
            sql += " order by empleado.empl_nombres";
            
            System.out.println("[EmpleadosDAO.getEmpleadosNew]"
                + "EmpresaId: " + _empresaId
                + ",deptoId: " + _deptoId
                + ",cencoId: " + _cencoId
                + ",turnoId: " + _turnoId
                + ", Sql: "+sql);
            
            //if (dbConn==null){
                dbOpen = true;
                dbConn = dbLocator.getConnection(m_dbpoolName,"[EmpleadosDAO.getEmpleadosNew]");
            //} 
            
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new EmpleadoVO();
               
                data.setRut(rs.getString("rut"));
                data.setCodInterno(rs.getString("cod_interno"));
                data.setNombres(rs.getString("nombre"));
                data.setApeMaterno(rs.getString("materno"));
                data.setEmpresaNombre(rs.getString("empresa_nombre"));
                data.setDeptoNombre(rs.getString("depto_nombre"));
                data.setCencoNombre(rs.getString("ccosto_nombre"));
                data.setComunaNombre(rs.getString("comuna_nombre"));
                data.setRegionNombre(rs.getString("region_nombre"));
                data.setNombreCargo(rs.getString("cargo_nombre"));
                data.setIdTurno(rs.getInt("empl_id_turno"));
                data.setNombreTurno(rs.getString("nombre_turno"));
                data.setFechaInicioContrato(rs.getDate("inicio_contrato"));
                if (data.getFechaInicioContrato() != null){
                    data.setFechaInicioContratoAsStr(sdf.format(data.getFechaInicioContrato()));
                }
                
                EmpresaVO auxEmpresa = new EmpresaVO();
                auxEmpresa.setId(rs.getString("empresa_id"));
                auxEmpresa.setNombre(data.getEmpresaNombre());
                
                data.setEmpresaId(auxEmpresa.getId());
                
                DepartamentoVO auxDepto = new DepartamentoVO();
                auxDepto.setId(rs.getString("depto_id"));
                auxDepto.setNombre(data.getDeptoNombre());
                data.setDeptoId(auxDepto.getId());
                
                CentroCostoVO auxCenco = new CentroCostoVO();
                auxCenco.setId(rs.getInt("cenco_id"));
                auxCenco.setNombre(data.getCencoNombre());
                data.setCencoId(auxCenco.getId());
                
                data.setEmpresa(auxEmpresa);
                data.setDepartamento(auxDepto);
                data.setCentroCosto(auxCenco);
                
                data.setClaveMarcacion(rs.getString("clave_marcacion"));
                lista.add(data);
                //
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
    * Obtiene lista de empleados con datos basicos
    * @param _empresaId
    * @param _deptoId
    * @param _cencoId
    * @param _cargo
    * @param _rutEmpleado
    * @param _nombres
    * @param _apePaterno
    * @param _apeMaterno
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * @return 
    */
    public List<EmpleadoVO> getEmpleadosShort(String _empresaId, 
            String _deptoId, 
            int _cencoId,
            int _cargo,
            String _rutEmpleado,
            String _nombres,
            String _apePaterno,
            String _apeMaterno,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<EmpleadoVO> lista = 
            new ArrayList<>();
    
        PreparedStatement ps = null;
        ResultSet rs = null;
        EmpleadoVO data;
        
        try{
            String sql = "SELECT "
                + "empleado.empl_rut AS rut,"
                + "coalesce(empleado.empl_nombres, '') || ' ' || "
                + "coalesce(empleado.empl_ape_paterno, '') nombre,"
                + "empleado.empl_id_cargo,"
                + "empleado.empl_id_turno, "
                + "empleado.empresa_id, "
                + "empresa.empresa_nombre, "
                + "empleado.empl_ape_materno AS materno,"
                + "empleado.depto_id,"
                + "departamento.depto_nombre,"
                + "empleado.cenco_id,"
                + "centro_costo.ccosto_nombre,"
                + "centro_costo.id_comuna,"
                + "comuna.comuna_nombre,"
                + "region.region_nombre,"
                + "region.region_id,"
                + "comuna.comuna_id,"
                + "cargo.cargo_nombre,"
                + "turno.nombre_turno,"
                + "empleado.empl_estado,"
                + "cargo.cargo_nombre,"
                + "turno.nombre_turno,"
                + "empresa.empresa_nombre,"
                    + "empleado.cod_interno,empleado.clave_marcacion "
                + "FROM "
                + "empleado,"
                + "centro_costo,"
                + "empresa,"
                + "departamento,"
                + "comuna,"
                + "region,"
                + "cargo,"
                + "turno "
                + "WHERE "
                + "empleado.empl_id_cargo = cargo.cargo_id AND "
                + "empleado.empl_id_turno = turno.id_turno AND "
                + "empleado.empresa_id = empresa.empresa_id AND "
                + "empleado.depto_id = departamento.depto_id AND "
                + "empleado.cenco_id = centro_costo.ccosto_id AND "
                + "centro_costo.id_comuna = comuna.comuna_id AND "
                + "comuna.region_id = region.region_id AND "
                + "empleado.empl_estado = 1 ";

            if (_empresaId!=null && _empresaId.compareTo("-1")!=0){        
                sql += " and empleado.empresa_id= '"+_empresaId+"'";
            }
            if (_deptoId!=null && _deptoId.compareTo("-1")!=0){        
                sql += " and empleado.depto_id = '"+_deptoId+"' ";
            }
            if (_cencoId != -1){        
                sql += " and empleado.cenco_id = "+_cencoId+" ";
            }
            if (_cargo != -1){        
                sql += " and empl_id_cargo = "+_cargo+" ";
            }
            if (_rutEmpleado != null && _rutEmpleado.compareTo("") != 0 && _rutEmpleado.compareTo("todos") != 0){        
                sql += " and upper(empleado.empl_rut) like '"+_rutEmpleado.toUpperCase()+"%'";
            }
            if (_nombres!=null && _nombres.compareTo("")!=0){        
                sql += " and upper(empl_nombres) like '"+_nombres.toUpperCase()+"%'";
            }
            if (_apePaterno != null && _apePaterno.compareTo("") != 0){        
                sql += " and upper(empl_ape_paterno) like '"+_apePaterno.toUpperCase()+"%'";
            }
            if (_apeMaterno != null && _apeMaterno.compareTo("") != 0){        
                sql += " and upper(empl_ape_materno) like '"+_apeMaterno.toUpperCase()+"%'";
            }
            
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            System.out.println("[EmpleadosDAO.getEmpleadosShort]"
                + "EmpresaId: "+_empresaId
                + ",deptoId: "+_deptoId
                + ",cencoId: "+_cencoId
                + ",rutEmpleado: "+_rutEmpleado
                + ",nombres: "+_nombres
                + ",apPaterno: "+_apePaterno
                + ",apMaterno: "+_apeMaterno
                + ", _jtStartIndex: "+_jtStartIndex
                + ", _jtPageSize: "+_jtPageSize
                + ", _jtSorting: "+_jtSorting
                + ", Sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[EmpleadosDAO.getEmpleadosShort]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new EmpleadoVO();
               
                data.setRut(rs.getString("rut"));
                data.setCodInterno(rs.getString("cod_interno"));
                data.setNombres(rs.getString("nombre"));
                data.setApeMaterno(rs.getString("materno"));
                data.setEmpresaNombre(rs.getString("empresa_nombre"));
                data.setDeptoNombre(rs.getString("depto_nombre"));
                data.setCencoNombre(rs.getString("ccosto_nombre"));
                data.setComunaNombre(rs.getString("comuna_nombre"));
                data.setRegionNombre(rs.getString("region_nombre"));
                data.setNombreCargo(rs.getString("cargo_nombre"));
                data.setIdTurno(rs.getInt("empl_id_turno"));
                data.setNombreTurno(rs.getString("nombre_turno"));
                
                EmpresaVO auxEmpresa = new EmpresaVO();
                auxEmpresa.setId(rs.getString("empresa_id"));
                auxEmpresa.setNombre(data.getEmpresaNombre());
                
                data.setEmpresaId(auxEmpresa.getId());
                
                DepartamentoVO auxDepto = new DepartamentoVO();
                auxDepto.setId(rs.getString("depto_id"));
                auxDepto.setNombre(data.getDeptoNombre());
                data.setDeptoId(auxDepto.getId());
                
                CentroCostoVO auxCenco = new CentroCostoVO();
                auxCenco.setId(rs.getInt("cenco_id"));
                auxCenco.setNombre(data.getCencoNombre());
                data.setCencoId(auxCenco.getId());
                
                data.setEmpresa(auxEmpresa);
                data.setDepartamento(auxDepto);
                data.setCentroCosto(auxCenco);
                
                data.setClaveMarcacion(rs.getString("clave_marcacion"));
                lista.add(data);
                //
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
    * Obtiene lista de empleados con datos basicos
    * @param _empresaId
    * @param _deptoId
    * @param _cencoId
    * @return 
    */
    public List<EmpleadoVO> getEmpleadosSimple(String _empresaId, 
            String _deptoId, 
            int _cencoId){
        
        List<EmpleadoVO> lista = new ArrayList<>();
    
        PreparedStatement ps = null;
        ResultSet rs = null;
        EmpleadoVO data;
        
        try{
            String sql = "SELECT "
                    + "rut,"
                    + "nombre nombreCompleto,"
                    + "empresa_id, "
                    + "cenco_id,"
                    + "ccosto_nombre,"
                    + "empl_id_turno,"
                    + "cod_interno,"
                    + "nombre,"
                    + "empleado.materno,"
                    + "cargo.cargo_nombre,"
                    + "empleado.depto_id,"
                    + "empresa_nombre,"
                    + "depto_nombre,"
                    + "ccosto_nombre,"
                    + "to_char(fecha_inicio_contrato,'dd/MM/yyyy') fechainicontrato "
                    + "FROM view_empleado empleado "
                    + "inner join cargo on (empleado.empl_id_cargo = cargo.cargo_id) "
                    + "WHERE "
                    + " empl_estado = 1 "
                    + " and empleado.empresa_id= '" + _empresaId + "' "
                    + " and empleado.depto_id = '" + _deptoId + "' "
                    + " and empleado.cenco_id = " + _cencoId + " "
                    + "order by rut";
            
            System.out.println("[EmpleadosDAO.getEmpleadosSimple]"
                + "EmpresaId: "+_empresaId
                + ",deptoId: "+_deptoId
                + ",cencoId: "+_cencoId);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[EmpleadosDAO.getEmpleadosSimple]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new EmpleadoVO();
               
                data.setRut(rs.getString("rut"));
                data.setNombres(rs.getString("nombreCompleto"));
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setCencoId(rs.getInt("cenco_id"));
                data.setCencoNombre(rs.getString("ccosto_nombre"));
                data.setIdTurno(rs.getInt("empl_id_turno"));
                data.setIdturno(data.getIdTurno());
                
                //nuevos
                data.setEmpresaNombre(rs.getString("empresa_nombre"));        
                data.setDeptoNombre(rs.getString("depto_nombre"));        
                data.setCencoNombre(rs.getString("ccosto_nombre"));        
                
                data.setCodInterno(rs.getString("cod_interno"));
                data.setNombres(rs.getString("nombre"));
                data.setApeMaterno(rs.getString("materno"));
                data.setNombreCargo(rs.getString("cargo_nombre"));
                
                EmpresaVO auxEmpresa = new EmpresaVO();
                auxEmpresa.setId(rs.getString("empresa_id"));
                auxEmpresa.setNombre(data.getEmpresaNombre());
                
                DepartamentoVO auxDepto = new DepartamentoVO();
                auxDepto.setId(rs.getString("depto_id"));
                auxDepto.setNombre(data.getDeptoNombre());
                
                CentroCostoVO auxCenco = new CentroCostoVO();
                auxCenco.setId(rs.getInt("cenco_id"));
                auxCenco.setNombre(data.getCencoNombre());
                
                data.setEmpresa(auxEmpresa);
                data.setDepartamento(auxDepto);
                data.setCentroCosto(auxCenco);
                
                data.setFechaInicioContratoAsStr(rs.getString("fechainicontrato"));
                                
                System.out.println("[EmpleadosDAO.getEmpleadosSimple]"
                    + "add. Rut: " + data.getRut()
                    + ",empresa.id: " + data.getEmpresa().getId() + " - " + data.getEmpresa().getNombre()
                    + ",deptoId.id: " + data.getDepartamento().getId() + " " + data.getDepartamento().getNombre()
                    + ",cencoI.id:" + data.getCentroCosto().getId() + " " + data.getCentroCosto().getNombre());
                
                lista.add(data);
                //
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
    * Obtiene lista de empleados con datos basicos
    * @param _empresaId
    * @param _deptoId
    * @param _cencoId
    * @param _estado
    * @param _articulo22
    * @return 
    */
    public List<EmpleadoVO> getEmpleadosSimpleByFiltro(String _empresaId, 
            String _deptoId, 
            int _cencoId, 
            int _estado, 
            boolean _articulo22){
        
        List<EmpleadoVO> lista = new ArrayList<>();
    
        PreparedStatement ps = null;
        ResultSet rs = null;
        EmpleadoVO data;
        
        try{
            String sql = "SELECT "
                    + "rut,"
                    + "nombre nombreCompleto,"
                    + "empresa_id, "
                    + "cenco_id,"
                    + "ccosto_nombre,"
                    + "empl_id_turno,"
                    + "cod_interno,"
                    + "nombre,"
                    + "empleado.materno,"
                    + "cargo.cargo_nombre,"
                    + "empleado.depto_id,"
                    + "empresa_nombre,"
                    + "depto_nombre,"
                    + "ccosto_nombre,"
                    + "to_char(fecha_inicio_contrato,'dd/MM/yyyy') fechainicontrato "
                    + "FROM view_empleado empleado "
                    + "inner join cargo on (empleado.empl_id_cargo = cargo.cargo_id) "
                    + "WHERE "
                    + " empl_estado = 1 "
                    + " and empleado.empresa_id= '" + _empresaId + "' "
                    + " and empleado.depto_id = '" + _deptoId + "' "
                    + " and empleado.cenco_id = " + _cencoId + " ";
            
            if (_estado != -1){        
                sql += " and empleado.empl_estado = " + _estado + " ";
            }
            sql += " and empleado.art_22 = " + _articulo22;
            
            sql += " order by rut";
            
            System.out.println("[EmpleadosDAO.getEmpleadosSimpleByFiltro]"
                + "EmpresaId: "+_empresaId
                + ",deptoId: "+_deptoId
                + ",cencoId: "+_cencoId+", sql: " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[EmpleadosDAO.getEmpleadosSimpleByFiltro]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new EmpleadoVO();
               
                data.setRut(rs.getString("rut"));
                data.setNombres(rs.getString("nombreCompleto"));
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setCencoId(rs.getInt("cenco_id"));
                data.setCencoNombre(rs.getString("ccosto_nombre"));
                data.setIdTurno(rs.getInt("empl_id_turno"));
                data.setIdturno(data.getIdTurno());
                
                //nuevos
                data.setEmpresaNombre(rs.getString("empresa_nombre"));        
                data.setDeptoNombre(rs.getString("depto_nombre"));        
                data.setCencoNombre(rs.getString("ccosto_nombre"));        
                
                data.setCodInterno(rs.getString("cod_interno"));
                data.setNombres(rs.getString("nombre"));
                data.setApeMaterno(rs.getString("materno"));
                data.setNombreCargo(rs.getString("cargo_nombre"));
                
                EmpresaVO auxEmpresa = new EmpresaVO();
                auxEmpresa.setId(rs.getString("empresa_id"));
                auxEmpresa.setNombre(data.getEmpresaNombre());
                
                DepartamentoVO auxDepto = new DepartamentoVO();
                auxDepto.setId(rs.getString("depto_id"));
                auxDepto.setNombre(data.getDeptoNombre());
                
                CentroCostoVO auxCenco = new CentroCostoVO();
                auxCenco.setId(rs.getInt("cenco_id"));
                auxCenco.setNombre(data.getCencoNombre());
                
                data.setEmpresa(auxEmpresa);
                data.setDepartamento(auxDepto);
                data.setCentroCosto(auxCenco);
                
                data.setFechaInicioContratoAsStr(rs.getString("fechainicontrato"));
                                
                System.out.println("[EmpleadosDAO.getEmpleadosSimpleByFiltro]"
                    + "add. Rut: " + data.getRut()
                    + ",empresa.id: " + data.getEmpresa().getId() + " - " + data.getEmpresa().getNombre()
                    + ",deptoId.id: " + data.getDepartamento().getId() + " " + data.getDepartamento().getNombre()
                    + ",cencoI.id:" + data.getCentroCosto().getId() + " " + data.getCentroCosto().getNombre());
                
                lista.add(data);
                //
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
     * Obtiene lista de empleados con datos basicos
     * @param _empresaId
     * @param _deptoId
     * @param _cencoId
     * @param _rutEmpleado
     * @param _turnoId
     * @param _jtStartIndex
     * @param _jtPageSize
     * @param _jtSorting
     * @return 
     */
    public List<EmpleadoVO> getEmpleadosByTurno(String _empresaId, 
            String _deptoId, 
            int _cencoId,
            int _turnoId,
            String _rutEmpleado,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<EmpleadoVO> lista = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        EmpleadoVO data;
        
        try{
            String sql = "SELECT "
                + "empleado.empl_rut AS rut,"
                + "coalesce(empleado.empl_nombres, '') || ' ' || "
                + "coalesce(empleado.empl_ape_paterno, '') nombre,"
                + "empleado.empl_id_cargo,"
                + "empleado.empl_id_turno, "
                + "empleado.empresa_id, "
                + "empresa.empresa_nombre, "
                + "empleado.empl_ape_materno AS materno,"
                + "empleado.depto_id,"
                + "departamento.depto_nombre,"
                + "empleado.cenco_id,"
                + "centro_costo.ccosto_nombre,"
                + "centro_costo.id_comuna,"
                + "comuna.comuna_nombre,"
                + "region.region_nombre,"
                + "region.region_id,"
                + "comuna.comuna_id,"
                + "cargo.cargo_nombre,"
                + "turno.nombre_turno,"
                + "empleado.empl_estado,"
                + "cargo.cargo_nombre,"
                + "turno.nombre_turno,"
                + "empresa.empresa_nombre,"
                    + "empleado.cod_interno,empleado.clave_marcacion "
                + "FROM "
                + "empleado,"
                + "centro_costo,"
                + "empresa,"
                + "departamento,"
                + "comuna,"
                + "region,"
                + "cargo,"
                + "turno "
                + "WHERE "
                + "empleado.empl_id_cargo = cargo.cargo_id AND "
                + "empleado.empl_id_turno = turno.id_turno AND "
                + "empleado.empresa_id = empresa.empresa_id AND "
                + "empleado.depto_id = departamento.depto_id AND "
                + "empleado.cenco_id = centro_costo.ccosto_id AND "
                + "centro_costo.id_comuna = comuna.comuna_id AND "
                + "comuna.region_id = region.region_id AND "
                + "empleado.empl_estado = 1 ";

            if (_empresaId!=null && _empresaId.compareTo("-1")!=0){        
                sql += " and empleado.empresa_id= '"+_empresaId+"'";
            }
            if (_deptoId!=null && _deptoId.compareTo("-1")!=0){        
                sql += " and empleado.depto_id = '"+_deptoId+"' ";
            }
            if (_cencoId != -1){        
                sql += " and empleado.cenco_id = "+_cencoId+" ";
            }
            if (_rutEmpleado != null && _rutEmpleado.compareTo("") != 0 && _rutEmpleado.compareTo("todos") != 0){        
                sql += " and upper(empleado.empl_rut) like '"+_rutEmpleado.toUpperCase()+"%'";
            }
            if (_turnoId != -1){        
                sql += " and empl_id_turno = " + _turnoId + " ";
            }
            
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            System.out.println("[EmpleadosDAO.getEmpleadosByTurno]"
                + ", Sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[EmpleadosDAO.getEmpleadosByTurno]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new EmpleadoVO();
               
                data.setRut(rs.getString("rut"));
                data.setCodInterno(rs.getString("cod_interno"));
                data.setNombres(rs.getString("nombre"));
                data.setApeMaterno(rs.getString("materno"));
                
                data.setEmpresaNombre(rs.getString("empresa_nombre"));
                data.setDeptoNombre(rs.getString("depto_nombre"));
                data.setCencoNombre(rs.getString("ccosto_nombre"));
                data.setComunaNombre(rs.getString("comuna_nombre"));
                data.setRegionNombre(rs.getString("region_nombre"));
                data.setNombreCargo(rs.getString("cargo_nombre"));
                data.setIdTurno(rs.getInt("empl_id_turno"));
                data.setNombreTurno(rs.getString("nombre_turno"));
                
                EmpresaVO auxEmpresa = new EmpresaVO();
                auxEmpresa.setId(rs.getString("empresa_id"));
                auxEmpresa.setNombre(data.getEmpresaNombre());
                
                data.setEmpresaId(auxEmpresa.getId());
                
                DepartamentoVO auxDepto = new DepartamentoVO();
                auxDepto.setId(rs.getString("depto_id"));
                auxDepto.setNombre(data.getDeptoNombre());
                data.setDeptoId(auxDepto.getId());
                
                CentroCostoVO auxCenco = new CentroCostoVO();
                auxCenco.setId(rs.getInt("cenco_id"));
                auxCenco.setNombre(data.getCencoNombre());
                data.setCencoId(auxCenco.getId());
                
                data.setEmpresa(auxEmpresa);
                data.setDepartamento(auxDepto);
                data.setCentroCosto(auxCenco);
                
                data.setClaveMarcacion(rs.getString("clave_marcacion"));
                lista.add(data);
                //
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
    
    public List<EmpleadoVO> getEmpleadosByListRuts(String _empresaId, 
            String _deptoId, 
            int _cencoId,
            int _cargo,
            ArrayList<String> _listaEmpleados,
            String _nombres,
            String _apePaterno,
            String _apeMaterno,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<EmpleadoVO> lista = 
            new ArrayList<>();
    
        PreparedStatement ps = null;
        ResultSet rs = null;
        EmpleadoVO data;
        
        try{
            String strEmpleados = "";
            // -------- Armar lista de empleados -----
            for (String rut : _listaEmpleados) {
                strEmpleados += "'" + rut + "',";
            }
            if (strEmpleados.compareTo("")!=0){
                strEmpleados=strEmpleados.substring(0, strEmpleados.length()-1);
            }
            String sql = "SELECT "
                + "empleado.empl_rut AS rut,"
                + "coalesce(empleado.empl_nombres, '') || ' ' || "
                + "coalesce(empleado.empl_ape_paterno, '') nombre,"
                + "empleado.empl_id_cargo,"
                + "empleado.empl_id_turno, "
                + "empleado.empresa_id, "
                + "empresa.empresa_nombre, "
                + "empleado.empl_ape_materno AS materno,"
                + "empleado.depto_id,"
                + "departamento.depto_nombre,"
                + "empleado.cenco_id,"
                + "centro_costo.ccosto_nombre,"
                + "centro_costo.id_comuna,"
                + "comuna.comuna_nombre,"
                + "region.region_nombre,"
                + "region.region_id,"
                + "comuna.comuna_id,"
                + "cargo.cargo_nombre,"
                + "turno.nombre_turno,"
                + "empleado.empl_estado,"
                + "cargo.cargo_nombre,"
                + "turno.nombre_turno,"
                + "empresa.empresa_nombre,"
                    + "empleado.cod_interno,empleado.clave_marcacion "
                + "FROM "
                + "empleado,"
                + "centro_costo,"
                + "empresa,"
                + "departamento,"
                + "comuna,"
                + "region,"
                + "cargo,"
                + "turno "
                + "WHERE "
                + "empleado.empl_id_cargo = cargo.cargo_id AND "
                + "empleado.empl_id_turno = turno.id_turno AND "
                + "empleado.empresa_id = empresa.empresa_id AND "
                + "empleado.depto_id = departamento.depto_id AND "
                + "empleado.cenco_id = centro_costo.ccosto_id AND "
                + "centro_costo.id_comuna = comuna.comuna_id AND "
                + "comuna.region_id = region.region_id AND "
                + "empleado.empl_estado = 1 ";

            if (_empresaId!=null && _empresaId.compareTo("-1")!=0){        
                sql += " and empleado.empresa_id= '"+_empresaId+"'";
            }
            if (_deptoId!=null && _deptoId.compareTo("-1")!=0){        
                sql += " and empleado.depto_id = '"+_deptoId+"' ";
            }
            if (_cencoId != -1){        
                sql += " and empleado.cenco_id = "+_cencoId+" ";
            }
            if (_cargo != -1){        
                sql += " and empl_id_cargo = "+_cargo+" ";
            }
            if (strEmpleados.compareTo("") != 0){
                sql += " and (empleado.empl_rut in (" + strEmpleados + ") )";
            }
//            if (_rutEmpleado != null && _rutEmpleado.compareTo("") != 0 && _rutEmpleado.compareTo("todos") != 0){        
//                sql += " and upper(empleado.empl_rut) like '"+_rutEmpleado.toUpperCase()+"%'";
//            }
            if (_nombres!=null && _nombres.compareTo("")!=0){        
                sql += " and upper(empl_nombres) like '"+_nombres.toUpperCase()+"%'";
            }
            if (_apePaterno != null && _apePaterno.compareTo("") != 0){        
                sql += " and upper(empl_ape_paterno) like '"+_apePaterno.toUpperCase()+"%'";
            }
            if (_apeMaterno != null && _apeMaterno.compareTo("") != 0){        
                sql += " and upper(empl_ape_materno) like '"+_apeMaterno.toUpperCase()+"%'";
            }
            
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            System.out.println("[EmpleadosDAO.getEmpleadosByListEmpleados]"
                + "EmpresaId: "+_empresaId
                + ",deptoId: "+_deptoId
                + ",cencoId: "+_cencoId
                + ",nombres: "+_nombres
                + ",apPaterno: "+_apePaterno
                + ",apMaterno: "+_apeMaterno
                + ", _jtStartIndex: "+_jtStartIndex
                + ", _jtPageSize: "+_jtPageSize
                + ", _jtSorting: "+_jtSorting
                + ", Sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[EmpleadosDAO.getEmpleadosByListRuts]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new EmpleadoVO();
               
                data.setRut(rs.getString("rut"));
                data.setCodInterno(rs.getString("cod_interno"));
                data.setNombres(rs.getString("nombre"));
                
                data.setEmpresaNombre(rs.getString("empresa_nombre"));
                data.setDeptoNombre(rs.getString("depto_nombre"));
                data.setCencoNombre(rs.getString("ccosto_nombre"));
                data.setComunaNombre(rs.getString("comuna_nombre"));
                data.setRegionNombre(rs.getString("region_nombre"));
                data.setNombreCargo(rs.getString("cargo_nombre"));
                data.setIdTurno(rs.getInt("empl_id_turno"));
                data.setNombreTurno(rs.getString("nombre_turno"));
                
                EmpresaVO auxEmpresa = new EmpresaVO();
                auxEmpresa.setId(rs.getString("empresa_id"));
                auxEmpresa.setNombre(data.getEmpresaNombre());
                
                DepartamentoVO auxDepto = new DepartamentoVO();
                auxDepto.setId(rs.getString("depto_id"));
                auxDepto.setNombre(data.getDeptoNombre());
                
                CentroCostoVO auxCenco = new CentroCostoVO();
                auxCenco.setId(rs.getInt("cenco_id"));
                auxCenco.setNombre(data.getCencoNombre());
                
                data.setEmpresa(auxEmpresa);
                data.setDepartamento(auxDepto);
                data.setCentroCosto(auxCenco);
                
                data.setClaveMarcacion(rs.getString("clave_marcacion"));
                lista.add(data);
                //
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
    * @param _empresaId
    * @param _deptoId
    * @param _cencoId
    * @param _cargo
    * @param _idTurno
    * @param _rutEmpleado
    * @param _nombres
    * @param _apePaterno
    * @param _apeMaterno
    * @param _estado
    * @return 
    */
    public int getEmpleadosCount(String _empresaId, 
            String _deptoId, 
            int _cencoId,
            int _cargo,
            int _idTurno,
            String _rutEmpleado,
            String _nombres,
            String _apePaterno,
            String _apeMaterno, 
            int _estado){
        int count=0;
        Statement statement = null;
        ResultSet rs        = null;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[EmpleadosDAO.getEmpleadosCount]");
            statement = dbConn.createStatement();
            String strSql="SELECT count(*) as count "
                + "FROM "
                + "empleado empl,"
                + "comuna,"
                + "region,"
                + "empresa,"
                + "departamento depto,"
                + "centro_costo cenco "
                + "WHERE "
                + "empl.id_comuna = comuna.comuna_id AND "
                + "empl.empresa_id = empresa.empresa_id AND "
                + "empl.depto_id = depto.depto_id AND "
                + "empl.cenco_id = cenco.ccosto_id AND "
                + "comuna.region_id = region.region_id ";
               
            if (_empresaId!=null && _empresaId.compareTo("-1")!=0){        
                strSql += " and empl.empresa_id= '"+_empresaId+"'";
            }
            if (_deptoId!=null && _deptoId.compareTo("-1")!=0){        
                strSql += " and empl.depto_id = '"+_deptoId+"' ";
            }
            if (_cencoId != -1){        
                strSql += " and empl.cenco_id = '"+_cencoId+"' ";
            }
            if (_cargo != -1){        
                strSql += " and empl_id_cargo = "+_cargo+" ";
            }
            if (_idTurno != -1){        
                strSql += " and empl_id_turno = " + _idTurno + " ";
            }
            if (_rutEmpleado!=null && _rutEmpleado.compareTo("")!=0){        
                strSql += " and upper(empl.empl_rut) like '"+_rutEmpleado.toUpperCase()+"%'";
            }
            if (_nombres!=null && _nombres.compareTo("")!=0){        
                strSql += " and upper(empl_nombres) like '"+_nombres.toUpperCase()+"%'";
            }
            if (_apePaterno != null && _apePaterno.compareTo("") != 0){        
                strSql += " and upper(empl_ape_paterno) like '"+_apePaterno.toUpperCase()+"%'";
            }
            if (_apeMaterno != null && _apeMaterno.compareTo("") != 0){        
                strSql += " and upper(empl_ape_materno) like '"+_apeMaterno.toUpperCase()+"%'";
            }
            
            if (_estado != -1){        
                strSql += " and empl.empl_estado = " + _estado + " ";
            }
            
            rs = statement.executeQuery(strSql);		
            if (rs.next()) {
                count=rs.getInt("count");
            }
            
            System.out.println("cl.femase.gestionweb."
                + "service.EmpleadosDAO."
                + "getEmpleadosCount(). Sql= "+strSql);
            
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
    
    /**
    * 
    * @param _empresaId
    * @param _deptoId
    * @param _cencoId
    * @param _cargo
    * @param _idTurno
    * @param _rutEmpleado
    * @param _nombres
    * @param _apePaterno
    * @param _apeMaterno
    * @param _estado
     * @param _cencosId
    * @return 
    */
    public int getCaducadosCount(String _empresaId, 
            String _deptoId, 
            int _cencoId,
            int _cargo,
            int _idTurno,
            String _rutEmpleado,
            String _nombres,
            String _apePaterno,
            String _apeMaterno, 
            int _estado,
            String _cencosId){
        int count=0;
        Statement statement = null;
        ResultSet rs        = null;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[EmpleadosDAO.getCaducadosCount]");
            statement = dbConn.createStatement();
            String strSql="SELECT count(*) as count "
                + "FROM "
                + "empleado empl,"
                + "comuna,"
                + "region,"
                + "empresa,"
                + "departamento depto,"
                + "centro_costo cenco "
                + "WHERE "
                + "empl.id_comuna = comuna.comuna_id AND "
                + "empl.empresa_id = empresa.empresa_id AND "
                + "empl.depto_id = depto.depto_id AND "
                + "empl.cenco_id = cenco.ccosto_id AND "
                + "comuna.region_id = region.region_id "
                + " and contrato_indefinido = false "
                + " and art_22 = false "
                + " and empl_estado = 1 ";
               
            if (_empresaId!=null && _empresaId.compareTo("-1")!=0){        
                strSql += " and empl.empresa_id= '"+_empresaId+"'";
            }
            if (_deptoId!=null && _deptoId.compareTo("-1")!=0){        
                strSql += " and empl.depto_id = '"+_deptoId+"' ";
            }
            if (_cencosId != null){        
                strSql += " and empl.cenco_id in (" + _cencosId + ") ";
            }
            if (_cargo != -1){        
                strSql += " and empl_id_cargo = "+_cargo+" ";
            }
            if (_idTurno != -1){        
                strSql += " and empl_id_turno = " + _idTurno + " ";
            }
            if (_rutEmpleado!=null && _rutEmpleado.compareTo("")!=0){        
                strSql += " and upper(empl.empl_rut) like '"+_rutEmpleado.toUpperCase()+"%'";
            }
            if (_nombres!=null && _nombres.compareTo("")!=0){        
                strSql += " and upper(empl_nombres) like '"+_nombres.toUpperCase()+"%'";
            }
            if (_apePaterno != null && _apePaterno.compareTo("") != 0){        
                strSql += " and upper(empl_ape_paterno) like '"+_apePaterno.toUpperCase()+"%'";
            }
            if (_apeMaterno != null && _apeMaterno.compareTo("") != 0){        
                strSql += " and upper(empl_ape_materno) like '"+_apeMaterno.toUpperCase()+"%'";
            }
            
            rs = statement.executeQuery(strSql);		
            if (rs.next()) {
                count=rs.getInt("count");
            }
            
            System.out.println("[EmpleadosDAO."
                + "getCaducadosCount]Sql= " + strSql);
            
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
                System.err.println("[EmpleadosDAO."
                    + "getCaducadosCount]Error: " + ex.toString());
            }
        }
        
        return count;
    }
   
    public int getEmpleadosByTurnoCount(String _empresaId, 
            String _deptoId, 
            int _cencoId,
            int _turnoId,
            String _rutEmpleado){
        int count=0;
        Statement statement = null;
        ResultSet rs        = null;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[EmpleadosDAO.getEmpleadosByTurnoCount]");
            statement = dbConn.createStatement();
            String sql = "SELECT count(*) as count "
                + "FROM "
                + "empleado,"
                + "centro_costo,"
                + "empresa,"
                + "departamento,"
                + "comuna,"
                + "region,"
                + "cargo,"
                + "turno "
                + "WHERE "
                + "empleado.empl_id_cargo = cargo.cargo_id AND "
                + "empleado.empl_id_turno = turno.id_turno AND "
                + "empleado.empresa_id = empresa.empresa_id AND "
                + "empleado.depto_id = departamento.depto_id AND "
                + "empleado.cenco_id = centro_costo.ccosto_id AND "
                + "centro_costo.id_comuna = comuna.comuna_id AND "
                + "comuna.region_id = region.region_id AND "
                + "empleado.empl_estado = 1 ";

            if (_empresaId!=null && _empresaId.compareTo("-1")!=0){        
                sql += " and empleado.empresa_id= '"+_empresaId+"'";
            }
            if (_deptoId!=null && _deptoId.compareTo("-1")!=0){        
                sql += " and empleado.depto_id = '"+_deptoId+"' ";
            }
            if (_cencoId != -1){        
                sql += " and empleado.cenco_id = "+_cencoId+" ";
            }
            if (_rutEmpleado != null && _rutEmpleado.compareTo("") != 0 && _rutEmpleado.compareTo("todos") != 0){        
                sql += " and upper(empleado.empl_rut) like '"+_rutEmpleado.toUpperCase()+"%'";
            }
            if (_turnoId != -1){        
                sql += " and empl_id_turno = " + _turnoId + " ";
            }
            
            rs = statement.executeQuery(sql);		
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
    
    //15-09-2018
    public void openDbConnection(){
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[EmpleadosDAO.openDbConnection]");
        } catch (DatabaseException ex) {
            System.err.println("Error: "+ ex.toString());
        }
    }
    
    public void closeDbConnection(){
        try {
            dbLocator.freeConnection(dbConn);
        } catch (Exception ex) {
            System.err.println("[closeDbConnection]Error: "+ex.toString());
        }
    }
    
    /**
    * 
    * @param _empleados
    * @throws java.sql.SQLException
    */
    public void insertListEmpleados(ArrayList<EmpleadoVO> _empleados) throws SQLException{
        try (
            PreparedStatement statement = dbConn.prepareStatement(SQL_INSERT_EMPLEADO);
        ) {
            
            int i = 0;
            System.out.println("[EmpleadosDAO.insertListEmpleados]"
                + "items a insertar: "+_empleados.size());
            for (EmpleadoVO entity : _empleados) {
                System.out.println("[EmpleadosDAO.insertListEmpleados] "
                    + "Insert empleado. "
                    + "Rut(sin puntos- Llave)= " + entity.getCodInterno()
                    + ", codInterno(con puntos)= " + entity.getRut()
                    + ", nombres= " + entity.getNombres()
                    + ", email= " + entity.getEmail()
                    + ", toString: "+ entity.toString());
                statement.setString(1,  entity.getCodInterno().toUpperCase());
                statement.setString(2,  entity.getNombres());
                statement.setString(3,  entity.getApePaterno());
                statement.setString(4,  entity.getApeMaterno());
                statement.setDate(5,  new java.sql.Date(entity.getFechaNacimiento().getTime()));
                statement.setString(6,  entity.getDireccion());
                statement.setString(7,  entity.getEmail());
                statement.setDate(8,  new java.sql.Date(entity.getFechaInicioContrato().getTime()));
                statement.setInt(9,  entity.getEstado());
                statement.setString(10,  entity.getPathFoto());
                statement.setString(11,  entity.getSexo());
                statement.setString(12,  entity.getFonoFijo());
                statement.setString(13,  entity.getFonoMovil());
                statement.setInt(14,  entity.getComunaId());
                statement.setInt(15,  entity.getIdTurno());
                statement.setBoolean(16,  entity.isAutorizaAusencia());
                statement.setInt(17,  entity.getIdCargo());
                //agregados el 24-04-2017.
                if (entity.getFechaTerminoContrato() != null){
                    statement.setDate(18,  new java.sql.Date(entity.getFechaTerminoContrato().getTime()));
                }else{
                    statement.setDate(18,  null);
                }
                statement.setBoolean(19,  entity.isContratoIndefinido());
                statement.setBoolean(20,  entity.isArticulo22());
                statement.setString(21,  entity.getEmpresa().getId());
                statement.setString(22,  entity.getDepartamento().getId());
                statement.setInt(23,  entity.getCentroCosto().getId());
                //agregado el 19-08-2017 para guardar num ficha
                statement.setString(24,  entity.getRut().toUpperCase());
                //agregado el 04-02-2018 para guardar clave marcacion
                statement.setString(25,  entity.getClaveMarcacion());
                
                // ...
                statement.addBatch();
                i++;
                System.out.println("[EmpleadosDAO.insertListEmpleados]"
                    + "fila i : " + i);
                
                if (i % 50 == 0 || i == _empleados.size()) {
                    try{
                        int[] rowsAffected = statement.executeBatch(); // Execute every 1000 items.
                        System.out.println("[EmpleadosDAO.insertListEmpleados]"
                            + "filas afectadas= "+rowsAffected.length);
                    }catch(Exception ex){
                        System.err.println("[EmpleadosDAO.insertListEmpleados]"
                            + "Error al insertar empleado= " + ex.toString());
                        ex.printStackTrace();
                    }
                    
                }
            }
        }
    }
    
////    /**
////     *
////     * @param _empleados
////     * @throws SQLException
////     */
////    public void insertListEmpleados(ArrayList<EmpleadoVO> _empleados)throws SQLException{
////        PreparedStatement statement    = null;
////        int i = 0;
////        System.out.println("[EmpleadosDAO.insertListEmpleados]"
////            + "items a insertar: "+_empleados.size());
////        for (EmpleadoVO entity : _empleados) {
////            System.out.println("[EmpleadosDAO.insertListEmpleados] "
////                + "Insert empleado. "
////                + "Rut(sin puntos- Llave)= " + entity.getCodInterno()
////                + ", codInterno(con puntos)= " + entity.getRut()
////                + ", nombres= " + entity.getNombres()
////                + ", email= " + entity.getEmail()
////                + ", toString: "+ entity.toString());
////            try{
////                statement.setString(1,  entity.getCodInterno().toUpperCase());
////                statement.setString(2,  entity.getNombres());
////                statement.setString(3,  entity.getApePaterno());
////                statement.setString(4,  entity.getApeMaterno());
////                statement.setDate(5,  new java.sql.Date(entity.getFechaNacimiento().getTime()));
////                statement.setString(6,  entity.getDireccion());
////                statement.setString(7,  entity.getEmail());
////                statement.setDate(8,  new java.sql.Date(entity.getFechaInicioContrato().getTime()));
////                statement.setInt(9,  entity.getEstado());
////                statement.setString(10,  entity.getPathFoto());
////                statement.setString(11,  entity.getSexo());
////                statement.setString(12,  entity.getFonoFijo());
////                statement.setString(13,  entity.getFonoMovil());
////                statement.setInt(14,  entity.getComunaId());
////                statement.setInt(15,  entity.getIdTurno());
////                statement.setBoolean(16,  entity.isAutorizaAusencia());
////                statement.setInt(17,  entity.getIdCargo());
////                //agregados el 24-04-2017.
////                if (entity.getFechaTerminoContrato() != null){
////                    statement.setDate(18,  new java.sql.Date(entity.getFechaTerminoContrato().getTime()));
////                }else{
////                    statement.setDate(18,  null);
////                }
////                statement.setBoolean(19,  entity.isContratoIndefinido());
////                statement.setBoolean(20,  entity.isArticulo22());
////                statement.setString(21,  entity.getEmpresa().getId());
////                statement.setString(22,  entity.getDepartamento().getId());
////                statement.setInt(23,  entity.getCentroCosto().getId());
////                //agregado el 19-08-2017 para guardar num ficha
////                statement.setString(24,  entity.getRut().toUpperCase());
////                //agregado el 04-02-2018 para guardar clave marcacion
////                statement.setString(25,  entity.getClaveMarcacion());
////            }catch(Exception ex){
////                System.err.println("[EmpleadosDAO.insertListEmpleados]"
////                    + "Error al parsear campos: " + ex.toString());
////                ex.printStackTrace();
////            }
////            System.out.println("[EmpleadosDAO.insertListEmpleados]"
////                + "(A)Add Insert, i= " + i);
////                
////            try{
////                // ...
////                statement.addBatch();
////                i++;
////                System.out.println("[EmpleadosDAO.insertListEmpleados]"
////                    + "Add Insert, i= " + i);
////                if (i % 50 == 0 || i == _empleados.size()) {
////                    int[] rowsAffected = statement.executeBatch(); // Execute every 1000 items.
////                    System.out.println("[EmpleadosDAO.insertListEmpleados]"
////                        + "filas afectadas= "+rowsAffected.length);
////                }
////            }catch(Exception ex){
////                System.err.println("[EmpleadosDAO."
////                    + "insertListEmpleados]"
////                    + "Error: " + ex.toString());
////                ex.printStackTrace();
////            }finally{
////                System.out.println("[EmpleadosDAO.insertListEmpleados]"
////                    + "Saliendo...");
////                try {
////                    if (statement != null) statement.close();
////                    dbLocator.freeConnection(dbConn);
////                } catch (SQLException ex) {
////                    System.err.println("Error: "+ex.toString());
////                }
////            }    
////
////        }
////            
////    }
    
}