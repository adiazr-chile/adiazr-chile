/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.vo.DetalleAusenciaVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.VacacionesVO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
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
 */
public class VacacionesDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    public boolean m_usedGlobalDbConnection = false;
    
    private String SQL_INSERT_VACACIONES = 
        "INSERT INTO vacaciones("
            + "empresa_id, "
            + "rut_empleado, "
            + "dias_progresivos,"
            + "afp_code, "
            + "fec_certif_vac_progresivas,"
            + "dias_especiales,"
            + "dias_adicionales) "
        + "VALUES (?, ?, ?, ?, ?, ?, ?)";
    
    private String SQL_DELETE_VACACIONES = "delete from vacaciones " +
        "WHERE empresa_id = ? " +
        "and rut_empleado = ?";
    
    /**
     *
     * @param _propsValues
     */
    public VacacionesDAO(PropertiesVO _propsValues) {
    }

    /**
    * Actualiza dias acumulados, dias_progresivos y saldo de dias de vacaciones
    * 
    * @param _data
    * @return 
    */
    public MaintenanceVO update(VacacionesVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "Info de Vacaciones, "
            + "EmpresaId: " + _data.getEmpresaId()
            + ", rutEmpleado: " + _data.getRutEmpleado()    
            + ", dias_acumulados: " + _data.getDiasAcumulados()
            + ", dias_progresivos: " + _data.getDiasProgresivos()
            + ", dias_especiales: " + _data.getDiasEspeciales()
            + ", saldo_dias: " + _data.getSaldoDias()
            + ", num cotizaciones segun certif: " + _data.getNumCotizaciones()
            + ", dias_adicionales: " + _data.getDiasAdicionales();
        
        try{
            String msgFinal = " Actualiza info de vacaciones:"
                + "EmpresaId [" + _data.getEmpresaId() + "]" 
                + ", rutEmpleado [" + _data.getRutEmpleado() + "]"    
                + ", dias_acumulados [" + _data.getDiasAcumulados() + "]"
                + ", dias_progresivos [" + _data.getDiasProgresivos() + "]"
                + ", dias_especiales [" + _data.getDiasEspeciales() + "]"
                + ", saldo_dias [" + _data.getSaldoDias() + "]"
                + ", num actual cotizaciones [" + _data.getNumActualCotizaciones() + "]"
                + ", dias_adicionales [" + _data.getDiasAdicionales() + "]"
                + ", afp_code [" + _data.getAfpCode() + "]"   
                + ", fecha certif afp [" + _data.getFechaCertifVacacionesProgresivas() + "]"
                + ", num cotizaciones segun certif [" + _data.getNumCotizaciones() + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE vacaciones "
                + "SET "
                    + "dias_acumulados = ?, "
                    + "dias_progresivos = ?, "
                    + "saldo_dias = ?,"
                    + "dias_especiales = ?,"
                    + "num_cotizaciones = ?,"
                    + "afp_code=?,"
                    + "fec_certif_vac_progresivas=?,"
                    + "dias_adicionales = ? "
                + "WHERE empresa_id=? "
                    + "and rut_empleado=?";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[VacacionesDAO.update]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setDouble(1,  _data.getDiasAcumulados());
            psupdate.setDouble(2,  _data.getDiasProgresivos());
            psupdate.setDouble(3,  _data.getSaldoDias());
            psupdate.setString(4,  _data.getDiasEspeciales());
            
            psupdate.setInt(5,  _data.getNumCotizaciones());
            psupdate.setString(6, _data.getAfpCode());
            if (_data.getFechaCertifVacacionesProgresivas() != null && 
                _data.getFechaCertifVacacionesProgresivas().compareTo("") != 0){
                    psupdate.setDate(7, Utilidades.getSqlDate(_data.getFechaCertifVacacionesProgresivas(), "yyyy-MM-dd"));
            }else {
                psupdate.setDate(7, null);
            }
            
            psupdate.setDouble(8,  _data.getDiasAdicionales());
            
            //filtro update            
            psupdate.setString(9, _data.getEmpresaId());
            psupdate.setString(10, _data.getRutEmpleado());
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println("[VacacionesDAO.update]vacaciones"
                   + ", empresaId:" + _data.getEmpresaId()
                    + ", rutEmpleado:" + _data.getRutEmpleado()    
                    + ", dias_acumulados:" +_data.getDiasAcumulados()
                    + ", dias_progresivos:" + _data.getDiasProgresivos()
                    + ", dias_especiales:" + _data.getDiasEspeciales()    
                    + ", saldo_dias:" + _data.getSaldoDias()    
                    +" actualizada OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("update vacaciones Error: "+sqle.toString());
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
    * Actualiza dias acumulados, dias_progresivos y saldo de dias de vacaciones
    * 
    * @param _data
    * @return 
    */
    public MaintenanceVO updateSaldoYUltimasVacaciones(VacacionesVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "saldo y ultimas Vacaciones, "
            + "EmpresaId: " + _data.getEmpresaId()
            + ", rutEmpleado: " + _data.getRutEmpleado()    
            + ", saldo_dias: " + _data.getSaldoDias()
            + ", inicio vacaciones: " + _data.getFechaInicioUltimasVacaciones()
            + ", fin vacaciones: " + _data.getFechaFinUltimasVacaciones()    ;
        
        try{
            String msgFinal = " Actualiza saldo y ultimas vacaciones:"
                + "EmpresaId [" + _data.getEmpresaId() + "]" 
                + ", rutEmpleado [" + _data.getRutEmpleado() + "]"    
                + ", saldo_dias [" + _data.getSaldoDias() + "]"
                + ", inicio vacaciones [" + _data.getFechaInicioUltimasVacaciones() + "]"
                + ", fin vacaciones [" + _data.getFechaFinUltimasVacaciones() + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE vacaciones "
                + "SET "
                    + "saldo_dias = ?,"
                    + "inicio_ult_vacacion = '" + _data.getFechaInicioUltimasVacaciones()+"',"
                    + "fin_ult_vacacion='" + _data.getFechaFinUltimasVacaciones()+"' "
                + " WHERE empresa_id = ? "
                    + " and rut_empleado = ?";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[VacacionesDAO.updateSaldoYUltimasVacaciones]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setDouble(1,  _data.getSaldoDias());
            psupdate.setString(2,  _data.getEmpresaId());
            psupdate.setString(3,  _data.getRutEmpleado());
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println("[VacacionesDAO.updateSaldoYUltimasVacaciones]vacaciones"
                   + ", empresaId:" + _data.getEmpresaId()
                    + ", rutEmpleado:" + _data.getRutEmpleado()    
                    + ", dias_acumulados:" +_data.getDiasAcumulados()
                    + ", dias_progresivos:" + _data.getDiasProgresivos()
                    + ", dias_especiales:" + _data.getDiasEspeciales()    
                    + ", saldo_dias:" + _data.getSaldoDias()    
                    +" actualizada OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[VacacionesDAO.updateSaldoYUltimasVacaciones]"
                + "Error: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (psupdate!=null) psupdate.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[VacacionesDAO."
                    + "updateSaldoYUltimasVacaciones]Error: "+ex.toString());
            }
        }

        return objresultado;
    }

    /**
    * Actualiza saldo vacaciones basicas anuales y
    * saldo vacaciones progresivas
    * 
    * @param _data
    * @return 
    */
    public MaintenanceVO updateSaldosVacacionesVBAyVP(VacacionesVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "saldos vacaciones (VBA y VP), "
            + "EmpresaId: " + _data.getEmpresaId()
            + ", rutEmpleado: " + _data.getRutEmpleado()    
            + ", saldo_dias_vba: " + _data.getSaldoDiasVBA()
            + ", saldo_dias_vp: " + _data.getSaldoDiasVP();
        
        try{
            String msgFinal = " Actualiza saldos vacaciones VBA y VP:"
                + "EmpresaId [" + _data.getEmpresaId() + "]" 
                + ", rutEmpleado [" + _data.getRutEmpleado() + "]"    
                + ", saldo_dias_vba [" + _data.getSaldoDiasVBA() + "]"
                + ", saldo_dias_vp [" + _data.getSaldoDiasVP() + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE vacaciones "
                + "SET "
                    + "saldo_dias_vba = " + _data.getSaldoDiasVBA()+","
                    + "saldo_dias_vp= " + _data.getSaldoDiasVP()+" "
                + " WHERE empresa_id = ? "
                    + " and rut_empleado = ?";
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[VacacionesDAO.updateSaldosVacacionesVBAyVP]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setString(1,  _data.getEmpresaId());
            psupdate.setString(2,  _data.getRutEmpleado());
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println("[VacacionesDAO.updateSaldosVacacionesVBAyVP]"
                   + ", empresaId:" + _data.getEmpresaId()
                    + ", rutEmpleado:" + _data.getRutEmpleado()    
                    +" saldos VBA y VP actualizados OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[VacacionesDAO.updateSaldosVacacionesVBAyVP]"
                + "Error: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (psupdate!=null) psupdate.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[VacacionesDAO."
                    + "updateSaldosVacacionesVBAyVP]Error: "+ex.toString());
            }
        }

        return objresultado;
    }
    
    /**
    * Actualiza ultimas vacaciones tomadas por el empleado
    * 
    * @param _data
    * @return 
    */
    public MaintenanceVO updateUltimasVacaciones(VacacionesVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "ultimas Vacaciones, "
            + "EmpresaId: " + _data.getEmpresaId()
            + ", rutEmpleado: " + _data.getRutEmpleado()    
            + ", inicio vacaciones: " + _data.getFechaInicioUltimasVacaciones()
            + ", fin vacaciones: " + _data.getFechaFinUltimasVacaciones()    ;
        
        try{
            String msgFinal = " Actualiza ultimas vacaciones:"
                + "EmpresaId [" + _data.getEmpresaId() + "]" 
                + ", rutEmpleado [" + _data.getRutEmpleado() + "]"    
                + ", inicio vacaciones [" + _data.getFechaInicioUltimasVacaciones() + "]"
                + ", fin vacaciones [" + _data.getFechaFinUltimasVacaciones() + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE vacaciones "
                + "SET "
                    + "inicio_ult_vacacion = '" + _data.getFechaInicioUltimasVacaciones()+"',"
                    + "fin_ult_vacacion='" + _data.getFechaFinUltimasVacaciones()+"' "
                + " WHERE empresa_id = ? "
                    + " and rut_empleado = ?";

            if (_data.getFechaInicioUltimasVacaciones() == null){
                sql = "UPDATE vacaciones "
                    + "SET "
                        + "inicio_ult_vacacion = null,"
                        + "fin_ult_vacacion = null "
                    + " WHERE empresa_id = ? "
                        + " and rut_empleado = ?";
            }
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[VacacionesDAO.updateUltimasVacaciones]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setString(1,  _data.getEmpresaId());
            psupdate.setString(2,  _data.getRutEmpleado());
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println("[VacacionesDAO.updateUltimasVacaciones]"
                   + ", empresaId:" + _data.getEmpresaId()
                    + ", rutEmpleado:" + _data.getRutEmpleado()    
                    +" actualizada OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[VacacionesDAO.updateUltimasVacaciones]"
                + "Error: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (psupdate!=null) psupdate.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[VacacionesDAO."
                    + "updateUltimasVacaciones]Error: "+ex.toString());
            }
        }

        return objresultado;
    }
    
    /**
    * Actualiza dias acumulados, 
    *   dias_progresivos, 
    *   dias zona extrema, 
    *   dias vacaciones tomadas 
    * Se invoca desde calculo
    * 
    * @param _data
    * @return 
    */
    public MaintenanceVO updateFromCalculo(VacacionesVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "saldo y dias progresivos, "
            + "EmpresaId: " + _data.getEmpresaId()
            + ", rutEmpleado: " + _data.getRutEmpleado()    
            + ", saldo_dias: " + _data.getSaldoDias()
            + ", dias_progresivos: " + _data.getDiasProgresivos()
            + ", dias_zona_extrema: " + _data.getDiasZonaExtrema()
            + ", fecha base VP: " + _data.getFechaBaseVp()
            + ", mensaje VP: " + _data.getMensajeVp();
        
        try{
            String msgFinal = " Actualiza saldo, "
                    + "dias progresivos y dias zona extrema:"
                + "EmpresaId [" + _data.getEmpresaId() + "]" 
                + ", rutEmpleado [" + _data.getRutEmpleado() + "]"    
                + ", saldo_dias [" + _data.getSaldoDias() + "]"
                + ", dias_progresivos [" + _data.getDiasProgresivos() + "]"
                + ", dias_zona_extrema [" + _data.getDiasZonaExtrema() + "]"
                + ", fecha base VP [" + _data.getFechaBaseVp() + "]"
                + ", mensaje VP [" + _data.getMensajeVp() + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE vacaciones "
                + "SET "
                    + "saldo_dias = ?, "
                    + "saldo_dias_vba = ?,"
                    + "dias_progresivos = " + _data.getDiasProgresivos() + "," 
                    + "saldo_dias_vp = ?, "
                    + "dias_zona_extrema= " + _data.getDiasZonaExtrema() + ","
                    + "fecha_calculo = current_timestamp, "
                    + "current_num_cotizaciones = ?, "
                    + "dias_acumulados = ?, "
                    + "dias_efectivos_tomados = ?, "
                    + "comentario = ?, "
                    + "fecha_base_vp = ?, "
                    + "mensaje_vp = ? "
                + " WHERE empresa_id = ? "
                    + " and rut_empleado = ?";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[VacacionesDAO.updateFromCalculo]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setDouble(1, _data.getSaldoDias());
            psupdate.setDouble(2, _data.getSaldoDiasVBA());
            psupdate.setDouble(3, _data.getSaldoDiasVP());
            psupdate.setInt(4, _data.getNumActualCotizaciones());
            psupdate.setDouble(5, _data.getDiasAcumulados());
            psupdate.setDouble(6, _data.getDiasEfectivos());
            psupdate.setString(7,  _data.getComentario());
            
            psupdate.setDate(8,  Utilidades.getJavaSqlDate(_data.getFechaBaseVp(), "yyyy-MM-dd"));
            psupdate.setString(9,  _data.getMensajeVp());
            
            //psupdate.setDouble(10, _data.getSaldoDiasVBA());
            //psupdate.setDouble(11, _data.getSaldoDiasVP());
            
            //filtro del update
            psupdate.setString(10,  _data.getEmpresaId());
            psupdate.setString(11,  _data.getRutEmpleado());
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println("[VacacionesDAO.updateFromCalculo]vacaciones"
                   + ", empresaId:" + _data.getEmpresaId()
                    + ", rutEmpleado:" + _data.getRutEmpleado()    
                    + ", dias_progresivos:" + _data.getDiasProgresivos()
                    + ", saldo_dias:" + _data.getSaldoDias()
                    + ", num_actual_cotizaciones:" + _data.getNumActualCotizaciones()
                    + ", fecha base VP:" + _data.getFechaBaseVp()
                    + ", mensaje VP:" + _data.getMensajeVp()    
                    +" actualizada OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[VacacionesDAO.updateFromCalculo]"
                + "Error: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (psupdate!=null) psupdate.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[VacacionesDAO."
                    + "updateFromCalculo]Error: "+ex.toString());
            }
        }

        return objresultado;
    }
    
    /**
    * Agrega un nuevo saldo de vacaciones de un empleado.
    * 
    * @param _data
    * @return 
    */
    public MaintenanceVO insert(VacacionesVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al insertar "
            + "Info de Vacaciones, "
            + "EmpresaId: " + _data.getEmpresaId()
            + ", rutEmpleado: " + _data.getRutEmpleado()    
            + ", dias_acumulados: " + _data.getDiasAcumulados()
            + ", dias_progresivos: " + _data.getDiasProgresivos()
            + ", dias_especiales: " + _data.getDiasEspeciales()
            + ", saldo_dias: " + _data.getSaldoDias()
            + ", dias_zona_extrema: " + _data.getDiasZonaExtrema()    
            + ", current_num_cotizaciones: " + _data.getNumActualCotizaciones()
            + ", comentario: " + _data.getComentario()
            + ", dias_adicionales: " + _data.getDiasAdicionales()
            + ", afp_code: " + _data.getAfpCode()   
            + ", fecha certif afp: " + _data.getFechaCertifVacacionesProgresivas()
            + ", fecha base VP: " + _data.getFechaBaseVp()
            + ", mensaje VP: " + _data.getMensajeVp();
        
        String msgFinal = " Inserta info de vacaciones:"
            + "EmpresaId [" + _data.getEmpresaId() + "]" 
            + ", rutEmpleado [" + _data.getRutEmpleado() + "]"    
            + ", dias_acumulados [" + _data.getDiasAcumulados() + "]"
            + ", dias_progresivos [" + _data.getDiasProgresivos() + "]"
            + ", dias_especiales [" + _data.getDiasEspeciales() + "]"
            + ", dias_adicionales [" + _data.getDiasAdicionales() + "]"
            + ", saldo_dias [" + _data.getSaldoDias() + "]"
            + ", current_num_cotizaciones [" + _data.getNumActualCotizaciones() + "]"
            + ", afp_code [" + _data.getAfpCode() + "]"   
            + ", fecha certif afp [" + _data.getFechaCertifVacacionesProgresivas() + "]"      
            + ", comentario [" + _data.getComentario() + "]"
            + ", fecha base VP [" + _data.getFechaBaseVp() + "]"
            + ", mensaje VP [" + _data.getMensajeVp() + "]";
       
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO vacaciones("
                + "empresa_id, "
                + "rut_empleado, "
                + "dias_acumulados, "
                + "dias_progresivos,"
                + "saldo_dias,"
                + "dias_especiales,"
                + "current_num_cotizaciones,"
                + "comentario,"
                + "dias_zona_extrema,"
                + "afp_code,"
                + "fec_certif_vac_progresivas,"
                + "dias_adicionales,"
                + "dias_efectivos_tomados, fecha_base_vp, mensaje_vp, "
                + "saldo_dias_vba, saldo_dias_vp) "
                + "VALUES (?, ?, ?, ?, "
                        + "?, ?, ?, ?, "
                        + "?, ?, ?, ?, "
                        + "?, ?, ?, ?, ?)";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[VacacionesDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _data.getEmpresaId());
            insert.setString(2,  _data.getRutEmpleado());
            insert.setDouble(3,  _data.getDiasAcumulados());
            insert.setDouble(4,  _data.getDiasProgresivos());
            insert.setDouble(5,  _data.getSaldoDias());
            insert.setString(6,  _data.getDiasEspeciales());
            
            insert.setInt(7,  _data.getNumActualCotizaciones());
            insert.setString(8,  _data.getComentario());
            insert.setDouble(9,  _data.getDiasZonaExtrema());
            insert.setString(10,  _data.getAfpCode());
            if (_data.getFechaCertifVacacionesProgresivas() != null && 
                _data.getFechaCertifVacacionesProgresivas().compareTo("") != 0){
                    insert.setDate(11, Utilidades.getSqlDate(_data.getFechaCertifVacacionesProgresivas(), "yyyy-MM-dd"));
            }else {
                insert.setDate(11, null);
            }
            insert.setDouble(12,  _data.getDiasAdicionales());
            insert.setDouble(13,  _data.getDiasEfectivos());
            
            insert.setDate(14,  Utilidades.getJavaSqlDate(_data.getFechaBaseVp(), "yyyy-MM-dd"));
            insert.setString(15,  _data.getMensajeVp());
            
            insert.setDouble(16, _data.getSaldoDiasVBA());
            insert.setDouble(17, _data.getSaldoDiasVP());
                        
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println("[VacacionesDAO.insert]vacaciones"
                    + ", empresaId:" + _data.getEmpresaId()
                    + ", rutEmpleado:" + _data.getRutEmpleado()    
                    + ", dias_acumulados:" +_data.getDiasAcumulados()
                    + ", dias_progresivos:" + _data.getDiasProgresivos()
                    + ", dias_especiales:" + _data.getDiasEspeciales()
                    + ", saldo_dias:" + _data.getSaldoDias()
                    + ", current_num_cotizaciones: " + _data.getNumActualCotizaciones()
                    + ", dias_efectivos_tomados: " + _data.getDiasEfectivos()
                    + ", fecha base VP: " + _data.getFechaBaseVp()
                    + ", mensaje VP: " + _data.getMensajeVp()    
                    +" insertada OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[VacacionesDAO.insert]Error1: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (insert != null) insert.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[VacacionesDAO.insert]"
                    + "Error: " + ex.toString());
            }
        }

        return objresultado;
    }
    
    /**
    * Inserta todos los registros faltantes en la tabla 'vacaciones'
    * 
    * @param _empresaId
    * @return 
    */
    public MaintenanceVO insertVacacionesFaltantes(String _empresaId){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al insertar "
            + "carga de registros faltantes en tabla vacaciones."
            + "EmpresaId: " + _empresaId;
        
        String msgFinal = " Inserta registros faltantes en tabla vacaciones:"
            + "EmpresaId [" + _empresaId + "]";
       
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO vacaciones"
                + "(empresa_id, rut_empleado, fecha_carga_inicial) "
                + "select e.empresa_id, e.empl_rut,current_timestamp "
                + "from empleado e "
                + "left outer join vacaciones v "
                    + "on (e.empresa_id = v.empresa_id and e.empl_rut = v.rut_empleado) "
                    + "inner join centro_costo cc on (e.cenco_id=cc.ccosto_id) "
                    + "where v.rut_empleado is null "
                    + "and e.empl_fec_fin_contrato >= current_date and cc.estado_id = 1 "
                    + "and e.empresa_id = ? ";

            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[VacacionesDAO.insertVacacionesFaltantes]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _empresaId);
            
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas > 0){
                System.out.println("[VacacionesDAO."
                    + "insertVacacionesFaltantes]"
                    + ", empresaId:" + _empresaId 
                    + "." + filasAfectadas +" insertadas OK!");
                objresultado.setMsg("Registros afectados: "+filasAfectadas);
            }else objresultado.setMsg("Registros afectados: 0");
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[VacacionesDAO.insertVacacionesFaltantes]"
                + "insert vacaciones Error1: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (insert != null) insert.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[VacacionesDAO.insertVacacionesFaltantes]"
                    + "Error: "+ex.toString());
            }
        }

        return objresultado;
    }
    
    /**
     * Elimina  un saldo de vacaciones para un empleado
     * 
     * @param _data
     * @return 
     */
    public MaintenanceVO delete(VacacionesVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al eliminar "
            + "info de vacaciones, empresaId: "+_data.getEmpresaId()
            + ", rutEmpleado: " + _data.getRutEmpleado();
        
       String msgFinal = " Elimina info de vacaciones:"
            + "empresaId [" + _data.getEmpresaId() + "]" 
            + ", rutEmpleado [" + _data.getRutEmpleado() + "]";
        objresultado.setMsg(msgFinal);
        PreparedStatement psdelete    = null;
        
        try{
            String sql = "delete from vacaciones " +
                "WHERE empresa_id = ? " +
                "and rut_empleado = ?";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[VacacionesDAO.delete]");
            psdelete = dbConn.prepareStatement(sql);
            psdelete.setString(1,  _data.getEmpresaId());
            psdelete.setString(2,  _data.getRutEmpleado());
                        
            int filasAfectadas = psdelete.executeUpdate();
            m_logger.debug("[delete vacaciones]filasAfectadas: "+filasAfectadas);
            if (filasAfectadas == 1){
                m_logger.debug("[delete vacaciones]"
                    + ", empresaId:" +_data.getEmpresaId()
                    + ", rutEmpleado:" +_data.getRutEmpleado()
                    +" eliminada OK!");
            }
            
            psdelete.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("delete vacaciones Error: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }finally{
            try {
                if (psdelete != null) psdelete.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }

        return objresultado;
    }
    
    /**
    * Retorna lista con saldo de vacaciones para un empleado o todos los
    * empleados de un centro de costo.
    * 
    * @param _empresaId
    * @param _rutEmpleado
    * @param _cencoId
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * 
    * @return 
    */
    public List<VacacionesVO> getInfoVacaciones(String _empresaId, 
            String _rutEmpleado,
            int _cencoId,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<VacacionesVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        VacacionesVO data;
        
        try{
            String sql = "select "
                    + "vac.empresa_id, "
                    + "empleado.depto_id,empleado.depto_nombre,"
                    + "empleado.cenco_id,empleado.ccosto_nombre,"
                    + "vac.rut_empleado,"
                    + "empleado.nombre || ' ' || empleado.materno nombre_empleado,"
                    + "to_char(vac.fecha_calculo, 'yyyy-MM-dd HH24:MI:SS') fecha_calculo,"
                    + "vac.dias_acumulados,"
                    + "vac.dias_progresivos,"
                    + "vac.saldo_dias,"
                    + "vac.inicio_ult_vacacion,"
                    + "vac.fin_ult_vacacion,"
                    + "coalesce(vac.dias_especiales, 'N') dias_especiales,"
                    + "vac.current_num_cotizaciones,"
                    + "vac.dias_zona_extrema,"
                    + "vac.comentario,empleado.fecha_inicio_contrato,"
                    + "coalesce(empleado.es_zona_extrema,'N') es_zona_extrema,"
                    + "coalesce(vac.afp_code,'NINGUNA') afp_code," 
                    + "coalesce(afp.afp_name,'NINGUNA') afp_name, " 
                    + "vac.fec_certif_vac_progresivas,"
                    + "vac.dias_adicionales,"
                    + "vac.dias_efectivos_tomados,"
                    + "to_char(vac.fecha_base_vp, 'yyyy-MM-dd') fecha_base_vp, "
                    + "coalesce(vac.num_cotizaciones, 0) num_cotizaciones, "
                    + "coalesce(vac.otra_institucion_emisora_certif,'') institucion_emisora_certif,"
                    + "coalesce(vac.mensaje_vp, '') mensaje_vp,"
                    + "saldo_dias_vba,"
                    + "saldo_dias_vp "
                + "from vacaciones vac "
                    + "inner join view_empleado empleado "
                    + "on (empleado.empresa_id = vac.empresa_id "
                    + "and empleado.rut=vac.rut_empleado) "
                    + "left outer join afp on (vac.afp_code = afp.afp_code) "
                    + "where 1 = 1 ";
           
            if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
                sql += " and vac.empresa_id = '" + _empresaId + "' ";
            }
            
            if (_rutEmpleado != null && _rutEmpleado.compareTo("-1") != 0){
                sql += " and vac.rut_empleado = '" + _rutEmpleado + "' ";
            }
            
            if (_cencoId != -1){
                sql += " and empleado.cenco_id = " + _cencoId;
            }
           
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            System.out.println("[VacacionesDAO."
                + "getInfoVacaciones]sql: " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[VacacionesDAO.getInfoVacaciones]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new VacacionesVO();
                
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setDeptoId(rs.getString("depto_id"));
                data.setDeptoNombre(rs.getString("depto_nombre"));
                data.setCencoId(rs.getInt("cenco_id"));
                data.setCencoNombre(rs.getString("ccosto_nombre"));
                data.setRutEmpleado(rs.getString("rut_empleado"));
                data.setNombreEmpleado(rs.getString("nombre_empleado"));
                data.setFechaCalculo(rs.getString("fecha_calculo"));
                data.setDiasAcumulados(rs.getDouble("dias_acumulados"));
                data.setDiasProgresivos(rs.getDouble("dias_progresivos"));
                data.setDiasEspeciales(rs.getString("dias_especiales"));
                data.setSaldoDias(rs.getDouble("saldo_dias"));
                data.setFechaInicioUltimasVacaciones(rs.getString("inicio_ult_vacacion"));
                data.setFechaFinUltimasVacaciones(rs.getString("fin_ult_vacacion"));
                
                data.setNumActualCotizaciones(rs.getInt("current_num_cotizaciones"));
                
                data.setDiasZonaExtrema(rs.getDouble("dias_zona_extrema"));
                data.setComentario(rs.getString("comentario"));
                data.setFechaInicioContrato(rs.getString("fecha_inicio_contrato"));
                data.setEsZonaExtrema(rs.getString("es_zona_extrema"));
                
                data.setAfpCode(rs.getString("afp_code"));
                data.setAfpName(rs.getString("afp_name"));
                data.setFechaCertifVacacionesProgresivas(rs.getString("fec_certif_vac_progresivas"));
                data.setDiasAdicionales(rs.getInt("dias_adicionales"));
                data.setDiasEfectivos(rs.getDouble("dias_efectivos_tomados"));
                
                data.setFechaBaseVp(rs.getString("fecha_base_vp"));
                data.setNumCotizaciones(rs.getInt("num_cotizaciones"));
                data.setOtraInstitucionEmisoraCertif(rs.getString("institucion_emisora_certif"));
                data.setMensajeVp(rs.getString("mensaje_vp"));
                
                data.setSaldoDiasVBA(rs.getDouble("saldo_dias_vba"));
                data.setSaldoDiasVP(rs.getDouble("saldo_dias_vp"));
                
                data.setRowKey(data.getEmpresaId()+"|"+data.getRutEmpleado());
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
    * Retorna lista con saldo de vacaciones para un empleado o todos los
    * empleados desvinculados de un centro de costo.
    * 
    * @param _empresaId
    * @param _rutEmpleado
    * @param _cencoId
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * 
    * @return 
    */
    public List<VacacionesVO> getInfoVacacionesDesvincula2(String _empresaId, 
            String _rutEmpleado,
            int _cencoId,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<VacacionesVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        VacacionesVO data;
        
        try{
            String sql = "select "
                + "vac.empresa_id, "
                + "empleado.depto_id,empleado.depto_nombre,"
                + "empleado.cenco_id,empleado.ccosto_nombre,"
                + "vac.rut_empleado,"
                + "empleado.nombre || ' ' || empleado.materno nombre_empleado,"
                + "to_char(vac.fecha_calculo, 'yyyy-MM-dd HH24:MI:SS') fecha_calculo,"
                + "vac.dias_acumulados,"
                + "vac.dias_progresivos,"
                + "vac.saldo_dias,"
                + "vac.inicio_ult_vacacion,"
                + "vac.fin_ult_vacacion,"
                + "coalesce(vac.dias_especiales, 'N') dias_especiales,"
                + "vac.current_num_cotizaciones,"
                + "vac.dias_zona_extrema,"
                + "vac.comentario,"
                + "empleado.fecha_inicio_contrato,fecha_desvinculacion, "
                + "coalesce(empleado.es_zona_extrema,'N') es_zona_extrema,"
                + "coalesce(vac.afp_code,'NINGUNA') afp_code," 
                + "coalesce(afp.afp_name,'NINGUNA') afp_name, " 
                + "vac.fec_certif_vac_progresivas,"
                + "vac.dias_adicionales,"
                + "vac.dias_efectivos_tomados,"
                + "to_char(vac.fecha_base_vp, 'yyyy-MM-dd') fecha_base_vp, "
                + "coalesce(vac.num_cotizaciones, 0) num_cotizaciones, "
                + "coalesce(vac.otra_institucion_emisora_certif,'') institucion_emisora_certif,"
                + "coalesce(vac.mensaje_vp, '') mensaje_vp,"
                + "saldo_dias_vba,"
                + "saldo_dias_vp "
                + "from vacaciones vac "
                    + "inner join view_empleado empleado "
                    + "on (empleado.empresa_id = vac.empresa_id "
                    + "and empleado.rut=vac.rut_empleado) "
                    + "left outer join afp on (vac.afp_code = afp.afp_code) "
                    + "where (empl_estado = " + Constantes.ESTADO_NO_VIGENTE + " and fecha_desvinculacion is not null) ";
           
            if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
                sql += " and vac.empresa_id = '" + _empresaId + "' ";
            }
            
            if (_rutEmpleado != null && _rutEmpleado.compareTo("-1") != 0){
                sql += " and vac.rut_empleado = '" + _rutEmpleado + "' ";
            }
            
            if (_cencoId != -1){
                sql += " and empleado.cenco_id = " + _cencoId;
            }
           
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            System.out.println("[VacacionesDAO."
                + "getInfoVacacionesDesvincula2]sql: " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[VacacionesDAO.getInfoVacacionesDesvincula2]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new VacacionesVO();
                
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setDeptoId(rs.getString("depto_id"));
                data.setDeptoNombre(rs.getString("depto_nombre"));
                data.setCencoId(rs.getInt("cenco_id"));
                data.setCencoNombre(rs.getString("ccosto_nombre"));
                data.setRutEmpleado(rs.getString("rut_empleado"));
                data.setNombreEmpleado(rs.getString("nombre_empleado"));
                data.setFechaCalculo(rs.getString("fecha_calculo"));
                data.setDiasAcumulados(rs.getDouble("dias_acumulados"));
                data.setDiasProgresivos(rs.getDouble("dias_progresivos"));
                data.setDiasEspeciales(rs.getString("dias_especiales"));
                data.setSaldoDias(rs.getDouble("saldo_dias"));
                data.setFechaInicioUltimasVacaciones(rs.getString("inicio_ult_vacacion"));
                data.setFechaFinUltimasVacaciones(rs.getString("fin_ult_vacacion"));
                
                data.setNumActualCotizaciones(rs.getInt("current_num_cotizaciones"));
                
                data.setDiasZonaExtrema(rs.getDouble("dias_zona_extrema"));
                data.setComentario(rs.getString("comentario"));
                data.setFechaInicioContrato(rs.getString("fecha_inicio_contrato"));
                data.setFechaDesvinculacion(rs.getString("fecha_desvinculacion"));
                data.setEsZonaExtrema(rs.getString("es_zona_extrema"));
                
                data.setAfpCode(rs.getString("afp_code"));
                data.setAfpName(rs.getString("afp_name"));
                data.setFechaCertifVacacionesProgresivas(rs.getString("fec_certif_vac_progresivas"));
                data.setDiasAdicionales(rs.getInt("dias_adicionales"));
                data.setDiasEfectivos(rs.getDouble("dias_efectivos_tomados"));
                
                data.setFechaBaseVp(rs.getString("fecha_base_vp"));
                data.setNumCotizaciones(rs.getInt("num_cotizaciones"));
                data.setOtraInstitucionEmisoraCertif(rs.getString("institucion_emisora_certif"));
                data.setMensajeVp(rs.getString("mensaje_vp"));
                
                data.setSaldoDiasVBA(rs.getDouble("saldo_dias_vba"));
                data.setSaldoDiasVP(rs.getDouble("saldo_dias_vp"));
                
                data.setRowKey(data.getEmpresaId()+"|"+data.getRutEmpleado());
                lista.add(data);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("[VacacionesDAO.getInfoVacacionesDesvincula2]"
                + "Error: " + sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[VacacionesDAO.getInfoVacacionesDesvincula2]"
                    + "Error: "+ex.toString());
            }
        }
        return lista;
    }
    
    /**
    * Retorna la suma total de dias de vacaciones que un empleado se ha tomado.
    * 
    * @param _rutEmpleado
    * 
    * @return 
    */
    public int getAllDiasEfectivosVacaciones(String _rutEmpleado){
        
        int diasEfectivos = 0;
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            String sql = "select "
                + "sum(dias_efectivos_vacaciones) suma_dias "
                + "from detalle_ausencia "
                + "where rut_empleado = '" + _rutEmpleado + "'";
            System.out.println("[VacacionesDAO."
                + "getAllDiasEfectivosVacaciones]sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[VacacionesDAO.getAllDiasEfectivosVacaciones]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                diasEfectivos = rs.getInt("suma_dias");
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
        return diasEfectivos;
    }
    
    /**
    * Retorna lista de vacaciones que un empleado se ha tomado a la fecha.
    * Esto es, todas las ausencias del tipo 'vacaciones' 
    * cuya fecha_inicio sea >= a la fecha de inicio del contrato del empleado.
    * 
    * @param _empresaId
    * @param _rutEmpleado
    * 
    * @return 
    */
    public ArrayList<DetalleAusenciaVO> getAllVacaciones(String _empresaId, String _rutEmpleado){
        PreparedStatement ps = null;
        ResultSet rs = null;
        String strJson = "";
        ArrayList<DetalleAusenciaVO> vacaciones 
            = new ArrayList<>(); 
        try{
            String sql = "SELECT "
                + "get_all_vacaciones_json" 
                    + "('" + _empresaId + "','" + _rutEmpleado + "') strjson";

            System.out.println("[VacacionesDAO."
                + "getAllVacaciones]Sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[VacacionesDAO.getAllVacaciones]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                strJson += rs.getString("strjson");
            }
            Type collectionType = new TypeToken<ArrayList<DetalleAusenciaVO>>(){}.getType();
            vacaciones = new Gson().fromJson(strJson, collectionType);
                    
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
        
        return vacaciones;
    }
    
    /**
    * Retorna lista de vacaciones que un empleado se ha tomado durante un rango de meses.
    * Esto es, todas las ausencias del tipo 'vacaciones' 
    * y cuya fecha_inicio y fecha_fin esten dentro del rango de meses seleccionado
    * 
    * @param _rutEmpleado
    * @param _mesInicio
    * @param _mesFin
    * 
    * @return 
    * 
    * @deprecated 
    */
    public ArrayList<DetalleAusenciaVO> getVacacionesRangoMeses(String _rutEmpleado, 
            String _mesInicio, 
            String _mesFin){
        PreparedStatement ps = null;
        ResultSet rs = null;
        String strJson = "";
        ArrayList<DetalleAusenciaVO> vacaciones 
            = new ArrayList<>(); 
        try{
            String sql = "SELECT "
                + "get_vacaciones_json_rango_meses"
                    + "('" + _rutEmpleado + "','" + _mesInicio + "','" + _mesFin + "') strjson";

            System.out.println("[VacacionesDAO."
                + "getVacacionesRangoMeses]Sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[VacacionesDAO.getVacacionesRangoMeses]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                strJson += rs.getString("strjson");
            }
            Type collectionType = new TypeToken<ArrayList<DetalleAusenciaVO>>(){}.getType();
            vacaciones = new Gson().fromJson(strJson, collectionType);
                    
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
        
        return vacaciones;
    }
    
    /**
    * 
    * @param _empresaId
    * @param _rutEmpleado
    * @param _cencoId
    * 
    * @return 
    */
    public int getInfoVacacionesCount(String _empresaId, 
            String _rutEmpleado,
            int _cencoId){
        int count=0;
        Statement statement = null;
        ResultSet rs = null;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[VacacionesDAO.getInfoVacacionesCount]");
            statement = dbConn.createStatement();
            String strSql ="SELECT count(rut_empleado) numrows "
                + "from vacaciones vac " +
                    "inner join view_empleado empleado on (empleado.empresa_id = vac.empresa_id and empleado.rut=vac.rut_empleado) "
                    + "where 1 = 1";
             
            if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
                strSql += " and vac.empresa_id = '" + _empresaId + "' ";
            }
            
            if (_rutEmpleado != null && _rutEmpleado.compareTo("") != 0){
                strSql += " and vac.rut_empleado = '" + _rutEmpleado + "' ";
            }
            
            if (_cencoId != -1){
                strSql += " and empleado.cenco_id = " + _cencoId;
            }
            
            rs = statement.executeQuery(strSql);		
            if (rs.next()) {
                count=rs.getInt("numrows");
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
    
    /**
    * 
    * @param _empresaId
    * @param _rutEmpleado
    * @param _cencoId
    * 
    * @return 
    */
    public int getInfoVacacionesDesvincula2Count(String _empresaId, 
            String _rutEmpleado,
            int _cencoId){
        int count=0;
        Statement statement = null;
        ResultSet rs = null;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[VacacionesDAO.getInfoVacacionesDesvincula2Count]");
            statement = dbConn.createStatement();
            String strSql ="SELECT count(rut_empleado) numrows "
                + "from vacaciones vac " +
                    "inner join view_empleado empleado "
                    + "on (empleado.empresa_id = vac.empresa_id and empleado.rut=vac.rut_empleado) "
                    + "where (empl_estado = " + Constantes.ESTADO_NO_VIGENTE + " and fecha_desvinculacion is not null) ";
             
            if (_empresaId != null && _empresaId.compareTo("-1") != 0){        
                strSql += " and vac.empresa_id = '" + _empresaId + "' ";
            }
            
            if (_rutEmpleado != null && _rutEmpleado.compareTo("") != 0){
                strSql += " and vac.rut_empleado = '" + _rutEmpleado + "' ";
            }
            
            if (_cencoId != -1){
                strSql += " and empleado.cenco_id = " + _cencoId;
            }
            
            rs = statement.executeQuery(strSql);		
            if (rs.next()) {
                count=rs.getInt("numrows");
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
                System.err.println("[VacacionesDAO.getInfoVacacionesDesvincula2Count]"
                    + "Error: " + ex.toString());
            }
        }
        return count;
    }
   
    /**
    * 
    * @param _vacaciones
    * @throws java.sql.SQLException
    */
    public void saveListVacaciones(ArrayList<VacacionesVO> _vacaciones) throws SQLException{
        try (
            PreparedStatement statement = dbConn.prepareStatement(SQL_INSERT_VACACIONES);
        ) {
            
            int i = 0;
            System.out.println("[VacacionesDAO.saveListVacaciones]"
                + "items a insertar: "+_vacaciones.size());
            for (VacacionesVO entity : _vacaciones) {
                System.out.println("[VacacionesDAO.saveListVacaciones] "
                    + "Insert info vacaciones. "
                    + "EmpresaId= " + entity.getEmpresaId()
                    + ", rut empleado= " + entity.getRutEmpleado()
                    + ", dias_adicionales= " + entity.getDiasAdicionales()
                    + ", dias_progresivos= " + entity.getDiasProgresivos()
                    + ", dias_especiales?: " + entity.getDiasEspeciales()
                    + ", afp_code: " + entity.getAfpCode());
            
                statement.setString(1, entity.getEmpresaId());
                statement.setString(2, entity.getRutEmpleado());
                statement.setDouble(3,  entity.getDiasProgresivos());
                statement.setString(4, entity.getAfpCode());
                statement.setDate(5,  
                    Utilidades.getJavaSqlDate(entity.getFechaCertifVacacionesProgresivas(), "yyyy-MM-dd"));
                statement.setString(6, entity.getDiasEspeciales());//S o N
                statement.setDouble(7, entity.getDiasAdicionales());
                
                // ...
                statement.addBatch();
                i++;

                if (i % 50 == 0 || i == _vacaciones.size()) {
                    int[] rowsAffected = statement.executeBatch(); // Execute every 1000 items.
                    System.out.println("[VacacionesDAO."
                        + "saveListVacaciones]filas afectadas= "+rowsAffected.length);
                }
            }
        }
    }
    
    /**
     * 
     * @param _vacaciones
     * @throws java.sql.SQLException
     */
    public void deleteListVacaciones(ArrayList<VacacionesVO> _vacaciones) throws SQLException{
        try (
            PreparedStatement statement = dbConn.prepareStatement(SQL_DELETE_VACACIONES);
        ) {
            
            int i = 0;
            System.out.println("[VacacionesDAO.deleteListVacaciones]"
                + "items a insertar: "+_vacaciones.size());
            for (VacacionesVO entity : _vacaciones) {
                System.out.println("[VacacionesDAO.deleteListVacaciones] "
                    + "Insert info vacaciones. "
                    + "EmpresaId= " + entity.getEmpresaId()
                    + ", rut empleado= " + entity.getRutEmpleado());
            
                statement.setString(1,  entity.getEmpresaId());
                statement.setString(2,  entity.getRutEmpleado());
                
                // ...
                statement.addBatch();
                i++;

                if (i % 50 == 0 || i == _vacaciones.size()) {
                    int[] rowsAffected = statement.executeBatch(); // Execute every 1000 items.
                    System.out.println("[VacacionesDAO."
                        + "deleteListVacaciones]filas afectadas= "+rowsAffected.length);
                }
            }
        }
    }
    
    public void openDbConnection(){
        try {
            m_usedGlobalDbConnection = true;
            dbConn = dbLocator.getConnection(m_dbpoolName,"[UsersDAO.openDbConnection]");
        } catch (DatabaseException ex) {
            System.err.println("[UsersDAO.openDbConnection]"
                + "Error: " + ex.toString());
        }
    }
    
    public void closeDbConnection(){
        try {
            m_usedGlobalDbConnection = false;
            dbLocator.freeConnection(dbConn);
        } catch (Exception ex) {
            System.err.println("[UsersDAO.closeDbConnection]"
                + "Error: "+ex.toString());
        }
    }
}
